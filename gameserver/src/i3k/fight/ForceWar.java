package i3k.fight;

import i3k.SBean;
import i3k.gs.FightService;
import i3k.gs.GameData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ForceWar
{
	int startMatchTime;
	AtomicInteger nextMapInstance = new AtomicInteger();
	FightServer fs;
	List<Integer> lvlMapIDs;
	
	abstract int getForceType();
	abstract void matchWar(int timeTick);
	abstract void clearRolesByServerID(int serverID);
	
	ForceWar(FightServer fs, List<Integer> lvlMapIDs)
	{
		this.fs = fs;
		this.lvlMapIDs = lvlMapIDs;
	}
	
	static int getMidLevel(List<Integer> levels)
	{
		if(levels.isEmpty())
			return 0;
		
		return levels.get(levels.size() / 2);
	}
	
	public class ForceMemberDetail
	{
		SBean.ForceWarJoin joinInfo;
		boolean mainSpawn;
		
		ForceMemberDetail(SBean.ForceWarJoin joinInfo, boolean mainSpawn)
		{
			this.joinInfo = joinInfo;
			this.mainSpawn = mainSpawn;
		}
	}
	
	public class FWJoinTeam
	{
		final int id;
		List<SBean.ForceWarJoin> members = new ArrayList<>();
		int joinTime;
		boolean free;
		
		FWJoinTeam(int id, SBean.ForceWarJoin member)
		{
			this.id = id;
			this.members.add(member);
			
			this.free = true;
		}
		
		FWJoinTeam(int id, List<SBean.ForceWarJoin> members)
		{
			this.id = id;
			this.members = members;
			this.free = true;
		}
		
		void setFree(boolean free)
		{
			this.free = free;
		}
		
		boolean isFree()
		{
			return this.free;
		}
		
		int getMemberCount()
		{
			return this.members.size();
		}
		
		int getID()
		{
			return this.id;
		}
		
		int getServerID()
		{
			for(SBean.ForceWarJoin member: members)
			{
				Integer gsid = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(member.overview.id)); 
				return gsid == null ? 0 : gsid;
			}
			
			return 0;
		}
	}
	
	public class FWTmpTeam
	{
		List<FWJoinTeam> teams = new ArrayList<>();
		int memberCount;
		
		FWTmpTeam()
		{
			this.memberCount = 0;
		}
			
		int getMemberCount()
		{
			return this.memberCount;
		}
		
		FWJoinTeam popTeam()
		{
			if(this.teams.isEmpty())
				return null;
			
			FWJoinTeam t = this.teams.remove(this.teams.size() - 1);
			this.memberCount -= t.getMemberCount();
			t.setFree(true);
			return t;
		}
		
		int pushTeam(FWJoinTeam t)
		{
			this.teams.add(t);
			this.memberCount += t.getMemberCount();
			t.setFree(false);
			return this.memberCount;
		}
	}
	
	public class FWTeamQueue
	{
		final int cap; 
		Map<Integer, FWJoinTeam> teams = new HashMap<>();
		
		FWTeamQueue(int cap)
		{
			this.cap = cap;
		}
		
		boolean isEmpty()
		{
			return this.teams.isEmpty();
		}
		
		public String toString()
		{
			return "cap " + cap + " team queue : " + teams.size();
		}
		
		FWJoinTeam tryGetFreeTeam()
		{
			Iterator<FWJoinTeam> it =  this.teams.values().iterator();
			while(it.hasNext())
			{
				FWJoinTeam t = it.next();
				if(!t.isFree())
					continue;
				
				return t;
			}
			
			return null;
		}
		
		void clearRolesByServerID(int serverID, List<Integer> roles)
		{
			Iterator<FWJoinTeam> it = this.teams.values().iterator();
			while(it.hasNext())
			{
				FWJoinTeam t = it.next();
				if(t.getServerID() == serverID)
				{
					for(SBean.ForceWarJoin m: t.members)
					{
						roles.add(m.overview.id);
					}
					it.remove();
				}
			}
		}
		
		void addTeam(FWJoinTeam t)
		{
			this.teams.put(t.getID(), t);
		}
		
		FWJoinTeam delTeam(int teamID)
		{
			return this.teams.remove(teamID);
		}
	}
	
	public class FWQueueCluster
	{
		List<FWTeamQueue> teamQueues = new ArrayList<>();
		private AtomicInteger nextTeamID = new AtomicInteger();
		Map<Integer, Integer> role2team = new HashMap<>();
		
		FWQueueCluster(int bwType)
		{
			
		}
		
		FWQueueCluster init(int maxCount)
		{
			for(int i = 1; i <= maxCount; ++i)
				this.teamQueues.add(new FWTeamQueue(i));
			
			return this;
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for(FWTeamQueue tq: teamQueues)
			{
				if(sb.length() != 0)
					sb.append(" | ");
				
				sb.append(tq.toString());
			}
			return sb.toString();
		}
		
		int getNextTeamID()
		{
			return this.nextTeamID.incrementAndGet();
		}
		
		FWTeamQueue getTeamQueue(int cnt)
		{
			if(cnt <= 0 || cnt > this.teamQueues.size())
				return null;
			
			return this.teamQueues.get(cnt - 1);
		}
		
		boolean isEmpty()
		{
			for(FWTeamQueue tq: this.teamQueues)
			{
				if(!tq.isEmpty())
					return false;
			}
			
			return true;
		}
		
		FWTmpTeam tryGetTmpTeam(int cap)
		{
			while(true)
			{
				FWTmpTeam tt = new FWTmpTeam();
				if(!tryMatch(tt, cap, cap))
					break;
				
				return tt;
			}
			 return null;
		}
		
		private boolean tryMatch(FWTmpTeam tt, int cap, int level)
		{
			if(tt.getMemberCount() >= cap)
				return true;
			
			int left = Math.min(cap - tt.getMemberCount(), level);
			while(left > 0)
			{
				FWJoinTeam t = tryGetFreeTeam(left);
				if(t == null)
					break;
				
				tt.pushTeam(t);
				if(tryMatch(tt, cap, left))
					return true;
				
				tt.popTeam();
				left = Math.min(t.getMemberCount() - 1, left - 1);
			}
			
			return false;
		}
		
		private FWJoinTeam tryGetFreeTeam(int level)
		{
			for(int i = Math.min(this.teamQueues.size(), level); i > 0 ; --i)
			{
				FWJoinTeam t = this.teamQueues.get(i - 1).tryGetFreeTeam();
				if(t != null)
					return t;
			}
			
			return null;
		}
		
		void clearRolesByServerID(int serverID)
		{
			List<Integer> roles = new ArrayList<>();
			for(FWTeamQueue tq: this.teamQueues)
				tq.clearRolesByServerID(serverID, roles);
			
			for(int rid: roles)
			{
				this.role2team.remove(rid);
			}
		}
		
		boolean containRole(int roleID)
		{
			return this.role2team.containsKey(roleID);
		}
		
		boolean addTeam(FWJoinTeam t)
		{
			FWTeamQueue tq = getTeamQueue(t.getMemberCount());
			if(tq == null)
				return false;
			
			tq.addTeam(t);
			for(SBean.ForceWarJoin e: t.members)
			{
				this.role2team.put(e.overview.id, t.getID());
			}
			
			return true;
		}
		
		boolean delTeam(int teamID, int cnt)
		{
			FWTeamQueue tq = getTeamQueue(cnt);
			if(tq == null)
				return false;
			
			FWJoinTeam t = tq.delTeam(teamID);
			if(t == null)
				return false;
			
			for(SBean.ForceWarJoin e: t.members)
			{
				this.role2team.remove(e.overview.id);
			}
			
			return true;
		}
		
		boolean roleQuit(int roleID, int cnt)
		{
			Integer teamID = this.role2team.remove(roleID);
			if(teamID == null)
				return false;
			
			return delTeam(teamID, cnt);
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	public void onTimer(int timeTick)
	{
		this.matchWar(timeTick);
	}
	
	FWQueueCluster getJoinCluster(int bwType)
	{
		return null;
	}

	synchronized void roleJoin(SBean.ForceWarJoin member, FightService.RoleJoinForceWarReqCallBack callback)
	{
		FWQueueCluster cluster = getJoinCluster(member.overview.bwType);
		if(cluster == null || cluster.containRole(member.overview.id))
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		callback.onCallback(cluster.addTeam(new FWJoinTeam(cluster.getNextTeamID(), member)) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
	}
	
	synchronized void roleJoin(List<SBean.ForceWarJoin> members, int bwType, FightService.TeamJoinForceWarCallBack callback)
	{
		FWQueueCluster cluster = getJoinCluster(bwType);
		if(cluster == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		callback.onCallback(cluster.addTeam(new FWJoinTeam(cluster.getNextTeamID(), members)) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
	}
	
	synchronized void roleQuit(int roleID, byte bwType, FightService.RoleQuitForceWarCallBack callback)
	{
		FWQueueCluster cluster = getJoinCluster(bwType);
		if(cluster == null || !cluster.containRole(roleID))
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		callback.onCallback(cluster.roleQuit(roleID, 1) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
	}
	
	synchronized void roleQuit(int roleID, byte bwType, int cnt, FightService.TeamQuitForceWarCallBack callback)
	{
		FWQueueCluster cluster = getJoinCluster(bwType);
		if(cluster == null || !cluster.containRole(roleID))
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		callback.onCallback(cluster.roleQuit(roleID, cnt) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
	}
	
	void processMatchInfo(FWTmpTeam tt, FWQueueCluster cluster, Set<Integer> gsids, List<Integer> levels, List<FWJoinTeam> joinTeams, Map<Integer, ForceMemberDetail> roles, boolean mainSpawn)
	{
		for(FWJoinTeam t: tt.teams)
		{
			cluster.delTeam(t.getID(), t.getMemberCount());
			for(SBean.ForceWarJoin e: t.members)
			{
				roles.put(e.overview.id, new ForceMemberDetail(e, mainSpawn));
				Integer gsid = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(e.overview.id));
				if(gsid != null)
					gsids.add(gsid);
				
				fs.getRPCManager().notifyGSForceWarMatchResult(e.overview.id, 1);
				levels.add(e.overview.level);
			}
			joinTeams.add(t);
		}
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------
class ForceWarBWType extends ForceWar
{
	private FWQueueCluster whiteCluster;
	private FWQueueCluster blackCluster;
	
	ForceWarBWType(FightServer fs, List<Integer> lvlMapIDs) 
	{
		super(fs, lvlMapIDs);
		
		whiteCluster = new FWQueueCluster(GameData.BWTYPE_WHITE).init(GameData.FORCEWAR_ROOM_MAX_COUNT);
		blackCluster = new FWQueueCluster(GameData.BWTYPE_BLACK).init(GameData.FORCEWAR_ROOM_MAX_COUNT);
	}
	
	int getForceType()
	{
		return GameData.FORCEWAR_TYPE_BWTYPE;
	}
	
	synchronized void matchWar(int timeTick)
	{
		if(this.whiteCluster.isEmpty() && this.blackCluster.isEmpty())
		{
			this.startMatchTime = 0;
			return;
		}
		
		if(this.startMatchTime <= 0)
			this.startMatchTime = timeTick;
		
		tryPrintJoinInfo(timeTick);
		final SBean.ForceWarMatchCFGS matchCfg = GameData.getInstance().getForceWarBaseCFGS().match;
		int cap = (timeTick - this.startMatchTime) > matchCfg.lowerCntTime ? matchCfg.lowerCnt : matchCfg.maxCnt;
		
		FWTmpTeam wtt = this.whiteCluster.tryGetTmpTeam(cap);
		if(wtt == null)
			return;
		
		FWTmpTeam btt = this.blackCluster.tryGetTmpTeam(cap);
		if(btt == null)
		{
			for(FWJoinTeam t: wtt.teams)
				t.setFree(true);
			return;
		}
		
		Set<Integer> gsids = new HashSet<>();
		List<Integer> levels = new ArrayList<>();
		List<FWJoinTeam> joinTeams = new ArrayList<>();
		Map<Integer, ForceMemberDetail> roles = new HashMap<>();
		processMatchInfo(wtt, this.whiteCluster, gsids, levels, joinTeams, roles, true);
		processMatchInfo(btt, this.blackCluster, gsids, levels, joinTeams, roles, false);
		
		Collections.sort(levels);
		fs.getGlobalMapService().createForceWarMap(roles, gsids, GameData.getInstance().getForceWarMap(lvlMapIDs, getMidLevel(levels)), this.nextMapInstance.incrementAndGet(), joinTeams);
		this.startMatchTime = 0;
	}
	
	private void tryPrintJoinInfo(int timeTick)
	{
		if(fs.getConfig().showForcewar == 0)
			return;
		
		if(timeTick % 10 == 0)
		{
			fs.getLogger().info("white join info " + this.whiteCluster.toString());
			fs.getLogger().info("black join info " + this.blackCluster.toString());
		}
	}
	
	synchronized void clearRolesByServerID(int serverID)
	{
		this.whiteCluster.clearRolesByServerID(serverID);
		this.blackCluster.clearRolesByServerID(serverID);
	}
	
	synchronized FWQueueCluster getJoinCluster(int bwType)
	{
		if(bwType == GameData.BWTYPE_WHITE)
			return this.whiteCluster;
		else if(bwType == GameData.BWTYPE_BLACK)
			return this.blackCluster;
		
		return null;
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------
class ForceWarMess extends ForceWar
{
	private FWQueueCluster commonCluster;
	
	ForceWarMess(FightServer fs, List<Integer> lvlMapIDs)
	{
		super(fs, lvlMapIDs);
		
		commonCluster = new FWQueueCluster(GameData.BWTYPE_NONE).init(GameData.FORCEWAR_ROOM_MAX_COUNT);
	}

	@Override
	int getForceType()
	{
		return GameData.FORCEWAR_TYPE_MESS;
	}

	@Override
	synchronized void matchWar(int timeTick)
	{
		if(this.commonCluster.isEmpty())
		{
			this.startMatchTime = 0;
			return;
		}
		
		if(this.startMatchTime <= 0)
			this.startMatchTime = timeTick;
		
		tryPrintJoinInfo(timeTick);
		final SBean.ForceWarMatchCFGS matchCfg = GameData.getInstance().getForceWarBaseCFGS().match;
		int cap = (timeTick - this.startMatchTime) > matchCfg.lowerCntTime ? matchCfg.lowerCnt : matchCfg.maxCnt;
		
		List<FWTmpTeam> tts = new ArrayList<>();
		while(true)
		{
			FWTmpTeam tt = this.commonCluster.tryGetTmpTeam(cap);
			if(tt == null)
				break;
			
			tts.add(tt);
		}
		
		matchHandler(tts);
	}
	
	private void matchHandler(List<FWTmpTeam> tts)
	{
		Iterator<FWTmpTeam> it = tts.iterator();
		while(tts.size() >= 2)
		{
			FWTmpTeam tt1 = it.next();
			it.remove();
			
			FWTmpTeam tt2 = it.next();
			it.remove();
			
			Set<Integer> gsids = new HashSet<>();
			List<Integer> levels = new ArrayList<>();
			List<FWJoinTeam> joinTeams = new ArrayList<>();
			Map<Integer, ForceMemberDetail> roles = new HashMap<>();
			processMatchInfo(tt1, this.commonCluster, gsids, levels, joinTeams, roles, true);
			processMatchInfo(tt2, this.commonCluster, gsids, levels, joinTeams, roles, false);
			
			Collections.sort(levels);
			fs.getGlobalMapService().createForceWarMap(roles, gsids, GameData.getInstance().getForceWarMap(lvlMapIDs, getMidLevel(levels)), this.nextMapInstance.incrementAndGet(), joinTeams);
			this.startMatchTime = 0;
		}
		
		//left FWTmpTeam
		for(FWTmpTeam tt: tts)
		{
			for(FWJoinTeam t: tt.teams)
				t.setFree(true);
		}
	}
	
	private void tryPrintJoinInfo(int timeTick)
	{
		if(fs.getConfig().showForcewar == 0)
			return;
		
		if(timeTick % 10 == 0)
		{
			fs.getLogger().info("common join info " + this.commonCluster.toString());
		}
	}
	
	@Override
	synchronized void clearRolesByServerID(int serverID)
	{
		this.commonCluster.clearRolesByServerID(serverID);
	}
	
	synchronized FWQueueCluster getJoinCluster(int bwType)
	{
		return this.commonCluster;
	}
}



