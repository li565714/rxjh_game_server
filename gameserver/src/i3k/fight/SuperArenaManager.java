package i3k.fight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import i3k.SBean;
import i3k.gs.FightService;
import i3k.gs.GameData;
import i3k.util.GameRandom;
import i3k.util.GameTime;

public class SuperArenaManager
{
	public static final int TEAM_RAND_INTERVAL_MAX = 15;
	
	FightServer fs;
	Map<Integer, SuperArena> allArenas = new HashMap<>();
	private AtomicInteger nextMapInstance = new AtomicInteger();
	
	SuperArenaManager(FightServer fs)
	{
		this.fs = fs;
	}
	
	public void init()
	{
		for(int type: GameData.getInstance().getAllSuperArenaTypes())
		{
			if(this.allArenas.containsKey(type))
				continue;
			
			SuperArena sa = new SuperArena(type).init();
			this.allArenas.put(type, sa);
		}
	}
	
	int getNextMapInstance()
	{
		return nextMapInstance.incrementAndGet();
	}
	
	void onTimer(int timeTick)
	{
		this.allArenas.values().forEach(arena -> arena.onTimer(timeTick));
	}
	
	public void clearServerRoles(int serverID)
	{
		this.allArenas.values().forEach(arena -> arena.clearServerRoles(serverID));
	}
	
	public void singleJoin(int gsid, SBean.SuperArenaJoin joinInfo, int grade, int arenaType, FightService.SingleJoinSuperArenaCallBack callback)
	{
		SuperArena sa = this.allArenas.get(arenaType);
		if(sa == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		sa.singleJoin(gsid, joinInfo, grade, callback);
	}
	
	public void singleQuit(int roleID, int grade, int arenaType, FightService.SingleQuitSuperArenaCallBack callback)
	{
		SuperArena sa = this.allArenas.get(arenaType);
		if(sa == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		sa.singleQuit(roleID, grade, callback);
	}
	
	public void teamQuit(int roleID, int teamCnt, int arenaType, int grade, FightService.TeamQuitSuperArenaCallBack callback)
	{
		SuperArena sa = this.allArenas.get(arenaType);
		if(sa == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		sa.teamQuit(roleID, teamCnt, grade, callback);
	}
	
	public void teamJoin(int gsid, List<SBean.SuperArenaJoin> members, int grade, int arenaType, FightService.TeamJoinSuperArenaCallBack callback)
	{
		SuperArena sa = this.allArenas.get(arenaType);
		if(sa == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		sa.teamJoin(gsid, members, grade, callback);
	}
	
	//-------------------------------------------------------------------------------------------------
	public class TmpTeamPair
	{
		List<TmpTeam> lst = new ArrayList<>();
		TmpTeamPair(TmpTeam... teams)
		{
			Collections.addAll(lst, teams);
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public class TmpTeam
	{
		List<JoinTeam> teams = new ArrayList<>();
		int power;
		int memberCount;
		boolean mainSpawnPos = false;
		int selfElo = 0;
		int enemyELO = 0;
		
		TmpTeam()
		{
			
		}
		
		void onMatchSuccess(boolean mainSpawnPos)
		{
			setMainSpawnPos(mainSpawnPos);
			calcELO();
		}
		
		private void setMainSpawnPos(boolean mainSpawnPos)
		{
			this.mainSpawnPos = mainSpawnPos;
		}
		
		private void calcELO()
		{
			selfElo = 0;
			for(JoinTeam t: teams)
			{
				for(SBean.SuperArenaJoin m: t.members)
				{
					selfElo += m.elo;
				}
			}
			selfElo /= this.memberCount;
		}
		
		int getSelfELO()
		{
			return this.selfElo;
		}
		
		int getEnemyELO()
		{
			return this.enemyELO;
		}
		
		void setEnemyELO(int enemyELO)
		{
			this.enemyELO = enemyELO;
		}
		
		TmpTeam init(JoinTeam team)
		{
			this.teams.add(team);
			this.memberCount = team.members.size();
			this.power = team.power;
			return this;
		}
		
		int pushTeam(JoinTeam t)
		{
			this.teams.add(t);
			this.memberCount += t.getMemberCount();
			t.setFree(false);
			return this.memberCount;
		}
		
		JoinTeam popTeam()
		{
			if(this.teams.isEmpty())
				return null;
			
			JoinTeam t = this.teams.remove(this.teams.size() - 1);
			this.memberCount -= t.getMemberCount();
			t.setFree(true);
			
			return t;
		}
		
		int getMemberCount()
		{
			return memberCount;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public class JoinTeam
	{
		final int id;
		final int randTick;
		List<SBean.SuperArenaJoin> members = new ArrayList<>();
		int joinTime;
		int lastDegradeTime;
		boolean free;
		int power;
		
		public JoinTeam(int id, List<SBean.SuperArenaJoin> members, int joinTime)
		{
			this.id = id;
			this.randTick = GameRandom.getRandInt(0, TEAM_RAND_INTERVAL_MAX);
			this.members = members;
			this.joinTime = joinTime;
			this.lastDegradeTime = joinTime;
			this.free = true;
			
			int total = 0;
			for(SBean.SuperArenaJoin member: members)
				total += member.overview.fightPower;
			
			this.power = total / members.size();
		}
		
		public void setJoinTime(int time)
		{
			this.joinTime = time;
		}
		
		public boolean isFree()
		{
			return this.free;
		}
		
		public void setFree(boolean free)
		{
			this.free = free;
		}
		
		public int getServerID()
		{
			for(SBean.SuperArenaJoin member: members)
			{
				Integer gsid = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(member.overview.id)); 
				return gsid == null ? 0 : gsid;
			}
			
			return 0;
		}
		
		int getMemberCount()
		{
			return this.members.size();
		}
		
		boolean isMatchTimeOut(int timeTick)
		{
			return timeTick - this.joinTime >= GameData.getInstance().getSuperArenaCFGS().maxMatchTime;
		}
		
		boolean isDegrade(int timeTick, int degradeTime)
		{
			return timeTick - this.lastDegradeTime >= degradeTime && degradeTime >= 0;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public class LevelGradeCluster
	{
		final int lvlGrade;
		List<ELOGradeCluster> eloClusters = new ArrayList<>();
		Map<Integer, Integer> role2elo = new HashMap<>();
		
		LevelGradeCluster(int lvlGrade)
		{
			this.lvlGrade = lvlGrade;
		}
		
		LevelGradeCluster init(int cap, int arenaType)
		{
			List<SBean.ELOGradeCFG> elos = GameData.getInstance().getAllELOGradeByType(arenaType);
			for(int eloGrade = 1; eloGrade <= elos.size(); eloGrade ++)
			{
				this.eloClusters.add(new ELOGradeCluster(eloGrade, elos.get(eloGrade - 1).degradeTime).init(cap));
			}
			return this;
		}
		
		ELOGradeCluster getELOGradeCluster(int grade)
		{
			if(grade <= 0 || grade > eloClusters.size())
				return null;
			
			return this.eloClusters.get(grade - 1);
		}
		
		synchronized boolean roleJoin(List<SBean.SuperArenaJoin> members, int arenaType)
		{
			for(SBean.SuperArenaJoin m: members)
			{
				if(role2elo.containsKey(m.overview.id))
					return false;
			}
			
			int elo = 0;
			for(SBean.SuperArenaJoin m: members)
			{
				elo += m.elo;
			}
			elo /= members.size();
			int eloGrade = GameData.getInstance().getELOGrade(elo, arenaType);
			ELOGradeCluster cluster = getELOGradeCluster(eloGrade);
			if(cluster == null)
				return false;
			
			JoinTeam team = cluster.roleJoin(members);
			if(team != null)
			{
				for(SBean.SuperArenaJoin m: members)
				{
					role2elo.put(m.overview.id, eloGrade);
					fs.getLogger().debug("role " + m.overview.id + " join super arena elo grade " + eloGrade + " success ");
				}
			}
			return team != null;
		}
		
		synchronized boolean roleQuit(int roleID, int teamCnt)
		{
			Integer eloGrade = role2elo.get(roleID);
			if(eloGrade == null)
				return false;
			
			ELOGradeCluster cluster = getELOGradeCluster(eloGrade);
			if(cluster == null)
				return false;
			
			JoinTeam team = cluster.roleQuit(roleID, teamCnt);
			if(team == null)
				return false;
			
			for(SBean.SuperArenaJoin m: team.members)
			{
				this.role2elo.remove(m.overview.id);
			}
			
			return true;

		}
		
		synchronized void onTimer(int timeTick, int cap, int arenaMapID, int arenaType)
		{
			matchBattle(timeTick, cap, arenaMapID, arenaType);
			checkDegradeAndTimeOut(timeTick, arenaType);
		}
		
		private void matchBattle(int timeTick, int cap, int arenaMapID, int arenaType)
		{
			for(ELOGradeCluster cluster: this.eloClusters)
			{
				 cluster.matchBattle(timeTick, cap, arenaMapID, arenaType);
			}
		}
		
		private void checkDegradeAndTimeOut(int timeTick, int arenaType)
		{
			this.eloClusters.forEach(e -> e.checkDegradeAndTimeOut(timeTick, arenaType));
		}
		
		synchronized void clearServerRoles(int serverID)
		{
			List<Integer> roles = new ArrayList<>();
			this.eloClusters.forEach(e -> e.clearServerRoles(serverID, roles));
			for(int rid: roles)
			{
				this.role2elo.remove(rid);
			}
		}
		
		
		
		
		
		
		//-------------------------------------------------------------------------------------------------
		public class ELOGradeCluster
		{
			final int eloGrade;
			final int degradeTime;
			private AtomicInteger nextTeamID = new AtomicInteger();
			List<TeamQueue> teamQueues = new ArrayList<>();
			
			ELOGradeCluster(int eloGrade, int degradeTime)
			{
				this.eloGrade = eloGrade;
				this.degradeTime = degradeTime;
			}
			
			ELOGradeCluster init(int cap)
			{
				for(int i = 1; i <= cap; i++)
					this.teamQueues.add(new TeamQueue(i));
				return this;
			}
			
			private TeamQueue getTeamQueue(int cap)
			{
				if(cap <= 0 || cap > teamQueues.size())
					return null;
				
				return this.teamQueues.get(cap - 1);
			}
			
			public JoinTeam roleJoin(List<SBean.SuperArenaJoin> members)
			{
				TeamQueue tq = getTeamQueue(members.size());
				if(tq == null)
					return null;
				
				JoinTeam team = new JoinTeam(nextTeamID.incrementAndGet(), members, GameTime.getTime());
				tq.roleJoin(team);
				return team;
			}
			
			public JoinTeam roleQuit(int roleID, int teamCnt)
			{
				TeamQueue tq = getTeamQueue(teamCnt);
				if(tq == null)
					return null;
				
				return tq.roleQuit(roleID);
			}
			
			public void matchBattle(int timeTick, int cap, int arenaMapID, int arenaType)
			{
				List<TmpTeam> tts = new ArrayList<>();
				while(true)
				{
					TmpTeam tt = new TmpTeam();
					if(!tryMatch(tt, cap, cap))
						break;
					
					tts.add(tt);
				}
				
				matchHandler(tts, arenaMapID, arenaType);
			}
			
			private void matchHandler(List<TmpTeam> tts, int arenaMapID, int arenaType)
			{
				Iterator<TmpTeam> it = tts.iterator();
				while(tts.size() >= 2)
				{
					TmpTeam tt1 = it.next();
					it.remove();
					tt1.onMatchSuccess(true);
					TmpTeam tt2 = it.next();
					it.remove();
					tt2.onMatchSuccess(false);
					
					tt1.setEnemyELO(tt2.getSelfELO());
					tt2.setEnemyELO(tt1.getSelfELO());
					onTeamMatchSuccess(new TmpTeamPair(tt1, tt2), arenaType, arenaMapID);
				}
				
				//left teams
				for(TmpTeam tt: tts)
				{
					for(JoinTeam t: tt.teams)
					{
						t.setFree(true);
					}
				}
			}
			
			private void onTeamMatchSuccess(TmpTeamPair pair, int arenaType, int arenaMapID)
			{
				Set<Integer> gsids = new HashSet<>();
				for(TmpTeam tt: pair.lst)
				{
					for(JoinTeam t: tt.teams)
					{
						this.delJoinTeam(t);
						for(SBean.SuperArenaJoin m: t.members)
						{
							LevelGradeCluster.this.role2elo.remove(m.overview.id);
							fs.getRPCManager().notifyGSSuperArenaMatchResult(m.overview.id, arenaType, LevelGradeCluster.this.lvlGrade, GameData.PROTOCOL_OP_SUCCESS);
						}
						gsids.add(t.getServerID());
					}
				}
				
				fs.getGlobalMapService().createSuperArenaMap(pair, gsids, arenaMapID, getNextMapInstance());
			}
			
			void checkDegradeAndTimeOut(int timeTick, int arenaType)
			{
				ELOGradeCluster preELO = LevelGradeCluster.this.getELOGradeCluster(this.eloGrade - 1);
				
				for(TeamQueue tq: this.teamQueues)
				{
					Iterator<JoinTeam> it = tq.teams.values().iterator();
					while(it.hasNext())
					{
						JoinTeam t = it.next();
						
						if(timeTick % TEAM_RAND_INTERVAL_MAX != t.randTick)
							continue;
						
						if(t.isMatchTimeOut(timeTick))
						{
							for(SBean.SuperArenaJoin member: t.members)
							{
								tq.role2team.remove(member.overview.id);
								fs.getRPCManager().notifyGSSuperArenaMatchResult(member.overview.id, arenaType, LevelGradeCluster.this.lvlGrade, GameData.PROTOCOL_OP_SUPERARENA_MATCH_TIMEOUT);
								LevelGradeCluster.this.role2elo.remove(member.overview.id);
							}
							it.remove();
							continue;
						}
						
						if(preELO == null)
							continue;
						
						if(t.isDegrade(timeTick, degradeTime))
						{
							for(SBean.SuperArenaJoin member: t.members)
							{
								tq.role2team.remove(member.overview.id);
								LevelGradeCluster.this.role2elo.remove(member.overview.id);
								LevelGradeCluster.this.role2elo.put(member.overview.id, this.eloGrade - 1);
								fs.getLogger().debug("role " + member.overview.id + " super arena degrade from " + this.eloGrade + " to " + (this.eloGrade - 1));
							}
							
							JoinTeam newTeam = preELO.roleJoin(t.members);
							if(newTeam != null)
								newTeam.setJoinTime(t.joinTime);
							
							it.remove();
						}
					}
				}
			}
			
			public void delJoinTeam(JoinTeam team)
			{
				TeamQueue tq = getTeamQueue(team.getMemberCount());
				if(tq == null)
					return;
				
				tq.delTeam(team.id);
			}
			
			private boolean tryMatch(TmpTeam tt, int cap, int level)
			{
				if(tt.getMemberCount() >= cap)
					return true;
				
				int left = Math.min(cap - tt.getMemberCount(), level);
				while(left > 0)
				{
					JoinTeam t = tryGetFreeTeam(left);					//match left
					if(t == null)
						break;
					
					tt.pushTeam(t);
					if(tryMatch(tt, cap, left))
						return true;
					
					tt.popTeam();
					left = Math.min(t.getMemberCount() - 1, left - 1);	//match (left - 1)
				}
				
				return false;
			}
			
			private JoinTeam tryGetFreeTeam(int level)
			{
				for(int i = Math.min(this.teamQueues.size(), level); i > 0; --i)
				{
					JoinTeam t = this.teamQueues.get(i - 1).tryGetFreeTeam();
					if(t != null)
						return t;
				}
				
				return null;
			}
			
			void clearServerRoles(int serverID, List<Integer> roles)
			{
				this.teamQueues.forEach(teamQueue -> teamQueue.clearServerRoles(serverID, roles));
			}
		}
	}
	//-------------------------------------------------------------------------------------------------
	public class TeamQueue	// cap [1,4]
	{
		final int cap;
		TreeMap<Integer, JoinTeam> teams = new TreeMap<>();		//<teamID, JoinTeam>
		Map<Integer, Integer> role2team = new HashMap<>();		//<roleID, teamID>
		
		TeamQueue(int cap)
		{
			this.cap = cap;
		}
		
		void addTeam(JoinTeam team)
		{
			this.teams.put(team.id, team);
			for(SBean.SuperArenaJoin member: team.members)
				this.role2team.put(member.overview.id, team.id);
		}
		
		JoinTeam delTeam(int teamID)
		{
			JoinTeam team = this.teams.remove(teamID);
			if(team == null)
				return null;

			for(SBean.SuperArenaJoin member: team.members)
				this.role2team.remove(member.overview.id);
				
			return team;
		}
		
		void roleJoin(JoinTeam t)
		{
			this.addTeam(t);
		}
		
		JoinTeam roleQuit(int roleID)
		{
			Integer teamID = this.role2team.get(roleID);
			if(teamID == null)
				return null;
			
			return this.delTeam(teamID);
		}
		
		List<Integer> clearServerRoles(int serverID, List<Integer> roles)
		{
			Iterator<JoinTeam> it = this.teams.values().iterator();
			while(it.hasNext())
			{
				JoinTeam team = it.next();
				if(team.getServerID() != serverID)
					continue;
				
				for(SBean.SuperArenaJoin member: team.members)
				{
					this.role2team.remove(member.overview.id);
					roles.add(member.overview.id);
				}
					
				it.remove();
			}
			return roles;
		}
		
		JoinTeam tryGetFreeTeam()
		{
			Iterator<JoinTeam> it = this.teams.descendingMap().values().iterator();
			while(it.hasNext())
			{
				JoinTeam t = it.next();
				if(!t.isFree())
					continue;
				
				return t;
			}
			
			return null;
		}
		
		public String toString()
		{
			int free = 0;
			for(JoinTeam t: this.teams.values())
			{
				if(t.isFree())
					free++;
			}
			return cap + " roles TeamQueue " + free;
		}
	}
	
	public class SuperArena			//synchronized  type 2v2 4v4
	{
		final int arenaType;
		final SBean.SuperArenaTypeCFGS typeCfg;
		final int cap;
		List<LevelGradeCluster> lvlGradeCluster = new ArrayList<>();
		
		SuperArena(int arenaType)
		{
			this.arenaType = arenaType;
			this.typeCfg = GameData.getInstance().getSuperArenaTypeCFG(arenaType);
			this.cap = typeCfg.members;
		}
		
		SuperArena init()
		{
			for(int i = 0; i < GameData.getInstance().getSuperArenaGradeSize(this.arenaType); i++)
			{
				lvlGradeCluster.add(new LevelGradeCluster(i + 1).init(this.cap, this.arenaType));
			}
			
			return this;
		}
		
		public void onTimer(int timeTick)
		{
			this.lvlGradeCluster.forEach(l -> l.onTimer(timeTick, this.cap, this.typeCfg.mapID, this.arenaType));
		}
		
		LevelGradeCluster getLvlGradeCluster(int grade)
		{
			if(grade <= 0 || grade > this.lvlGradeCluster.size())
				return null;
			
			return this.lvlGradeCluster.get(grade - 1);
		}
		
		public void singleJoin(int gsid, SBean.SuperArenaJoin joinInfo, int grade, FightService.SingleJoinSuperArenaCallBack callback)
		{
			LevelGradeCluster cluster = getLvlGradeCluster(grade);
			if(cluster == null)
			{
				callback.onCallback(GameData.PROTOCOL_OP_FAILED);
				return;
			}
			
			List<SBean.SuperArenaJoin> members = new ArrayList<>();
			members.add(joinInfo);
			callback.onCallback(cluster.roleJoin(members, this.arenaType) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
		}
		
		public void reJoinMatch(JoinTeam team, int grade)
		{
			if(team.id < 0 || team.isFree())
				return;
			
			team.setFree(true);
		}
		
		public void teamJoin(int gsid, List<SBean.SuperArenaJoin> members, int grade, FightService.TeamJoinSuperArenaCallBack callback)
		{
			LevelGradeCluster cluster = getLvlGradeCluster(grade);
			if(cluster == null)
			{
				callback.onCallback(GameData.PROTOCOL_OP_FAILED);
				return;
			}
			
			callback.onCallback(cluster.roleJoin(members, this.arenaType) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
		}
		
		public void singleQuit(int roleID, int grade, FightService.SingleQuitSuperArenaCallBack callback)
		{
			LevelGradeCluster cluster = getLvlGradeCluster(grade);
			if(cluster == null)
			{
				callback.onCallback(GameData.PROTOCOL_OP_FAILED);
				return;
			}
			
			callback.onCallback(cluster.roleQuit(roleID, 1) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
		}
		
		public void teamQuit(int roleID, int teamCnt, int grade, FightService.TeamQuitSuperArenaCallBack callback)
		{
			LevelGradeCluster cluster = getLvlGradeCluster(grade);
			if(cluster == null)
			{
				callback.onCallback(GameData.PROTOCOL_OP_FAILED);
				return;
			}
			
			callback.onCallback(cluster.roleQuit(roleID, teamCnt) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
		}
		
		public void clearServerRoles(int serverID)
		{
			this.lvlGradeCluster.forEach(cluster -> cluster.clearServerRoles(serverID));
		}
	}
	//--------------------------------------------------------------------------------------------------------------------
}
