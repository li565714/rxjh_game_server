package i3k.fight;

import i3k.SBean;
import i3k.fight.DemonHoleManager.ServerRole;
import i3k.fight.ForceWar.FWJoinTeam;
import i3k.fight.ForceWar.ForceMemberDetail;
import i3k.fight.SuperArenaManager.JoinTeam;
import i3k.fight.SuperArenaManager.TmpTeam;
import i3k.fight.SuperArenaManager.TmpTeamPair;
import i3k.gs.GameData;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameMaps
{
	final FightServer fs;
	ConcurrentMap<Integer, MapCopyCluster> allMaps = new ConcurrentHashMap<>();
	
	class FightTmpTeam
	{
		int leader;
		Set<Integer> members = new HashSet<>();	//不包括队长
		
		FightTmpTeam(List<Integer> members)
		{
			if(!members.isEmpty())
			{
				leader = members.get(0);
				for(int rid: members)
				{
					if(rid != leader)
						this.members.add(rid);
				}
			}
		}
		
		void delMember(int rid)
		{
			if(leader == rid)
				leader = 0;
			
			members.remove(rid);
		}
		
		List<Integer> getAllMembers()
		{
			List<Integer> all = new ArrayList<>();
			if(leader != 0)
				all.add(leader);
			
			all.addAll(members);
			return all;
		}
		
		int getServerID()
		{
			if(leader > 0)
				return fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(leader));
			
			for(int rid: members)
				return fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(rid));
			
			return 0;
		}
	}
	
	GameMaps(FightServer fs)
	{
		this.fs = fs;
	}
	
	void init(Set<Integer> maps)
	{
		for (int mapID : maps)
		{
			try
			{
				this.allMaps.put(mapID, createMapCopyCluster(mapID));
			}
			catch (GameData.MapException e)
			{
				fs.getLogger().warn("!!!!! map server init map " + mapID + " cause exception : " + e.getMessage());
			}
		}
	}
	
	void fini()
	{
		this.allMaps.values().forEach(MapCopyCluster::close);
		this.allMaps.clear();
	}
	
	void onTimer(int timeTick)
	{
		this.allMaps.values().forEach(mcc -> mcc.onTimer(timeTick));
	}
	
	void roleLeaveMap(int roleID, int mapID, int mapInstance)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		mcc.roleLeaveMap(roleID, mapInstance);
	}
	
	int createForceWarMapCopy(Map<Integer, ForceMemberDetail> roles, Set<Integer> gsids, int mapID, int mapInstance, List<FWJoinTeam> joinTeams)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return 0;
		
		if(mcc instanceof ForceWarMapCopyCluster)
		{
			ForceWarMapCopyCluster fwmcc = ForceWarMapCopyCluster.class.cast(mcc);
			return fwmcc.createMap(mapInstance, gsids, roles, joinTeams);
		}
		
		return 0;
	}
	
	void syncForceWarMapCopyStart(int mapID, int mapInstance)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof ForceWarMapCopyCluster)
		{
			ForceWarMapCopyCluster fwmcc = ForceWarMapCopyCluster.class.cast(mcc);
			fwmcc.onStart(mapInstance);
		}
	}
	
	void syncForceWarMapCopyEnd(int mapID, int mapInstance, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof ForceWarMapCopyCluster)
		{
			ForceWarMapCopyCluster fwmcc = ForceWarMapCopyCluster.class.cast(mcc);
			fwmcc.onEnd(mapInstance, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
		}
	}
	
	void forceWarSendMsg(int mapID, int mapInstance, int rid, SBean.MessageInfo msgContent)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof ForceWarMapCopyCluster)
		{
			ForceWarMapCopyCluster fmcc = ForceWarMapCopyCluster.class.cast(mcc);
			fmcc.roleSendMsg(mapInstance, rid, msgContent);
		}
	}
	
	int createSuperArenaMapCopy(TmpTeamPair pair, Set<Integer> gsids, int mapID, int mapInstance)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return 0;
		
		if(mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			return samcc.createMap(mapInstance, gsids, pair);
		}
		
		return 0;
	}
	
	void syncSuperArenaMapCopyStart(int mapID, int mapInstance)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			samcc.onStart(mapInstance);
		}
	}
	
	void syncSuperArenaMapCopyEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			samcc.onEnd(mapInstance, result);
		}
	}
	
	void syncSuperArenaRaceEnd(int mapID, int mapInstance)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			samcc.onRaceEnd(mapInstance);
		}
	}
	
	int createDemonHoleMapCopy(int mapID, int mapInstance, int floor, Map<Integer, ServerRole> serverRoles)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return 0;
		
		if(mcc instanceof DemonHoleMapCopyCluster)
		{
			DemonHoleMapCopyCluster dhmcc = DemonHoleMapCopyCluster.class.cast(mcc);
			return dhmcc.createMap(mapInstance, floor, serverRoles);
		}
		
		return 0;
	}
	
	void roleEnterDemonHoleMap(int roleID, int mapID, int mapInstance)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof DemonHoleMapCopyCluster)
		{
			DemonHoleMapCopyCluster dhmcc = DemonHoleMapCopyCluster.class.cast(mcc);
			dhmcc.roleEnter(roleID, mapInstance);
		}
	}
	
	void syncDemonHoleMapCopyEnd(int mapID, int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		MapCopyCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof DemonHoleMapCopyCluster)
		{
			DemonHoleMapCopyCluster dhmcc = DemonHoleMapCopyCluster.class.cast(mcc);
			dhmcc.onEnd(mapInstance, curFloor, total);
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------	
	public MapCopyCluster createMapCopyCluster(int mapID) throws GameData.MapException
	{
		int mapType = GameData.getInstance().checkMapValid(mapID);
		switch (mapType)
		{
		case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
			SBean.ForceWarMapCFGS mapCfg = GameData.getInstance().getForceWarMapCFGS(mapID);
			return new ForceWarMapCopyCluster(mapCfg.type, mapID);
		case GameData.MAP_TYPE_MAPCOPY_SUPERARENA:
			return new SuperArenaMapCopyCluster(mapID);
		case GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE:
			return new DemonHoleMapCopyCluster(mapID);
		default:
			throw new GameData.MapException("global map not support map type " + mapType + " cluster create !!!");
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	private abstract class MapCopyCluster<T extends MapCopy>
	{
		public final int mapID;
		ConcurrentMap<Integer, T> maps;

		public MapCopyCluster(int mapID, ConcurrentMap<Integer, T> maps)
		{
			this.mapID = mapID;
			this.maps = maps;
		}
		
		synchronized boolean addMap(T map)
		{
			maps.put(map.mapInstance, map);
			return true;
		}
		
		synchronized boolean roleLeaveMap(int roleID, int mapInstance)
		{
			MapCopy map = this.maps.get(mapInstance);
			if(map == null)
				return false;
			
			if(map.leaveMap(roleID))
			{
				this.maps.remove(mapInstance);
				fs.getLogger().info("all role leave, destroy empty map copy " + mapID + " , " + mapInstance);
				fs.getRPCManager().notifyGlobalMapEndMapCopy(mapID, mapInstance);
			}
			return true;
		}
		
		void onTimer(int timeTick)
		{
			Iterator<Map.Entry<Integer, T>> it = maps.entrySet().iterator();
			while (it.hasNext())
			{
				MapCopy map = it.next().getValue();
				if (map.onTimer(timeTick))
				{
					it.remove();
					map.forceAllRoleLeaveMap(mapID);
					fs.getLogger().info("destroy timeout mapcopy " + mapID + " " + map.mapInstance);
					
					for(int serverID: map.gsids)
						fs.getRPCManager().notifyGSSyncMapCopyTimeOut(serverID, mapID, map.mapInstance);
					
					fs.getRPCManager().notifyGlobalMapEndMapCopy(mapID, map.mapInstance);
				}
			}
		}
		
		void close()
		{
			// TODO
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	private abstract class MapCopy
	{
		final int mapInstance;
		final Set<Integer> gsids;
		MapCopy(int mapInstance, Set<Integer> gsids)
		{
			this.mapInstance = mapInstance;
			this.gsids = gsids;
		}

		//不需要加锁，除roles集合是并发集合外，此方法调用时
		void forceAllRoleLeaveMap(int mapID)
		{
			
		}
		
		void recoverTeam(List<FightTmpTeam> tmpTeams)
		{
			for(FightTmpTeam tt: tmpTeams)
			{
				int serverID = tt.getServerID();
				if(serverID <= 0)
					continue;
				
				List<Integer> members = tt.getAllMembers();
				if(members.size() <= 1)
					continue;
				
				fs.getRPCManager().notifyGSSyncCreateNewTeam(serverID, members);
			}
		}
		
		abstract boolean onTimer(int timeTick);
		abstract boolean leaveMap(int roleID);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//势力战
	private class ForceWarMapCopyCluster extends MapCopyCluster<ForceWarMapCopyCluster.ForceWarMapCopy>
	{
		final int maxTime;
		final int autoCloseTime;
		final int forcewarType;
		public ForceWarMapCopyCluster(int forcewarType, int mapID)
		{
			super(mapID, new ConcurrentHashMap<>());
			SBean.ForceWarCFGS cfg = GameData.getInstance().getForceWarCFGS(forcewarType);
			this.maxTime = cfg == null ? 0 : cfg.maxTime;
			this.forcewarType = forcewarType;
			this.autoCloseTime = GameData.getInstance().getForceWarBaseCFGS().other.autoCloseTime;
		}

		int createMap(int mapInstance, Set<Integer> gsids, Map<Integer, ForceMemberDetail> roles, List<FWJoinTeam> joinTeams)
		{
			fs.getLogger().info("create force war map " + mapID + " " + mapInstance + " success, maxTime = " + this.maxTime);
			if (addMap(new ForceWarMapCopy(mapInstance, gsids, roles, joinTeams)))
			{
				for(int serverID: gsids)
					fs.getRPCManager().notifyGSCreateMapCopy(serverID, mapID, mapInstance);
				
				return mapInstance;
			}
			
			return 0;
		}
		
		public void onStart(int mapInstance)
		{
			ForceWarMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onStart();
		}
		
		public void onEnd(int mapInstance, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
		{
			ForceWarMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onEnd(winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
		}

		public void roleSendMsg(int mapInstance, int rid, SBean.MessageInfo msgContent)
		{
			ForceWarMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.roleSendMsg(rid, msgContent);
		}
		
		class ForceWarMapCopy extends MapCopy
		{
			final Map<Integer, ForceMemberDetail> roles;
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;
			
			List<FightTmpTeam> tmpTeams = new ArrayList<>();
			ForceWarMapCopy(int mapInstance, Set<Integer> gsids, Map<Integer, ForceMemberDetail> roles, List<FWJoinTeam> joinTeams)
			{
				super(mapInstance, gsids);
				this.roles = roles;
				this.closeTime = GameTime.getTime() + maxTime + autoCloseTime;
				
				for(FWJoinTeam ft: joinTeams)
				{
					if(ft.members.size() <= 1)
						continue;
					
					List<Integer> all = new ArrayList<>();
					for(SBean.ForceWarJoin e: ft.members)
						all.add(e.overview.id);
					
					tmpTeams.add(new FightTmpTeam(all));
				}
			}

			@Override
			boolean onTimer(int timeTick)
			{
				return this.closeTime <= timeTick;
			}
			
			synchronized boolean leaveMap(int roleID)
			{
				this.roles.remove(roleID);
				for(FightTmpTeam tt: this.tmpTeams)
					tt.delMember(roleID);
				
				return this.roles.isEmpty();
			}
			
			synchronized void onStart()
			{
				fs.getLogger().info("on force war map " + mapID + " " + mapInstance + " start");
				if (startTime == 0)
				{
					int now = GameTime.getTime();
					this.onGSMapcopyStart(now);
					this.startTime = now;
				}
			}
			
			void onGSMapcopyStart(int time)
			{
				fs.getLogger().info("on force war map copy " + mapID + " " + mapInstance + " start, left Time=" + (this.closeTime - time));
				for(int serverID: gsids)
					fs.getRPCManager().notifyGSSyncForceWarMapStart(serverID, mapID, mapInstance);
			}
			
			synchronized void onEnd(int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
			{
				this.setForceWarResult(winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
				fs.getLogger().info("on force war map " + mapID + " " + mapInstance + " end");
				if (endTime == 0)
				{
					recoverTeam(this.tmpTeams);
					int now = GameTime.getTime();
					this.popupResultTime = now;
					this.closeTime = popupResultTime + autoCloseTime;
					this.onGSMapcopyEnd(now, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
					this.endTime = now;
				}
			}
			
			private void setForceWarResult(int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
			{
				Collections.sort(whiteSide, (e1, e2) ->{
					return sortFunction(e1, e2);
				});
				setForceWarFeat(ForceWarMapCopyCluster.this.forcewarType, whiteSide, winSide == GameData.BWTYPE_WHITE, this.roles);
				
				Collections.sort(blackSide, (e1, e2) ->{
					return sortFunction(e1, e2);
				});
				setForceWarFeat(ForceWarMapCopyCluster.this.forcewarType, blackSide, winSide == GameData.BWTYPE_BLACK, this.roles);
			}
			
			void onGSMapcopyEnd(int time, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
			{
				tryUpdateForceWarRank(whiteSide, GameData.RANK_TYPE_LOCAL_FORCEWAR_WHITE);
				tryUpdateForceWarRank(blackSide, GameData.RANK_TYPE_LOCAL_FORCEWAR_BLACK);
				fs.getLogger().info("on force war map copy " + mapID + " " + mapInstance + " end, left Time=" + (this.closeTime - time));
				for(int serverID: gsids)
				{
					SBean.RankClearTime rankClearTime = fs.getFightRankManager().getForceWarRankCurClearTime(serverID);
					fs.getRPCManager().notifyGSSyncForceWarMapEnd(serverID, mapID, mapInstance, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide, rankClearTime);
				}
			}
			
			void tryUpdateForceWarRank(List<SBean.ForceWarOverview> whiteSide, int rankID)
			{
				fs.getLogger().info("force war " + mapID + " , " + mapInstance + " end update rank " + rankID + "---------------------");
				for(SBean.ForceWarOverview e: whiteSide)
				{
					fs.getLogger().info(getForceWarOverview(e));
					ForceMemberDetail r = this.roles.get(e.rid);
					if(r == null || e.quit == 1)
						continue;
					
					Integer gsid = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(r.joinInfo.overview.id));
					if(gsid == null)
						continue;
					
					fs.getFightRankManager().tryUpdateRank( gsid,
														    rankID, 
														    r.joinInfo.rankClearTime, 
														    new SBean.RankRole(r.joinInfo.overview, e.gainFeat + r.joinInfo.curWeekFeat), 
														    new SBean.RankRole(r.joinInfo.overview, e.gainFeat));
				}
			}
			
			synchronized void roleSendMsg(int rid, final SBean.MessageInfo msgContent)
			{
				ForceMemberDetail sender = this.roles.get(rid);
				if(sender == null)
					return;
				
				for(ForceMemberDetail e: this.roles.values())
				{
					if(e.joinInfo.overview.bwType == sender.joinInfo.overview.bwType)
					{
						fs.getRPCManager().notifyGSSendFightMsg(e.joinInfo.overview.id, msgContent);
					}
				}
			}
		}
	}
	
	static int sortFunction(SBean.ForceWarOverview e1, SBean.ForceWarOverview e2)
	{
		if(e1.score != e2.score)
			return e1.score > e2.score ? -1 : 1;
		
		if(e1.kills != e2.kills)
			return e1.kills > e2.kills ? -1 : 1;
		
		if(e1.bekills != e2.bekills)
			return e1.bekills < e2.bekills ? -1 : 1;
			
		if(e1.rid != e2.rid)
			return e1.rid > e2.rid ? -1 : 1;

		return 0;
	}
	
	static void setForceWarFeat(int type, List<SBean.ForceWarOverview> side, boolean win, final Map<Integer, ForceMemberDetail> roles)
	{
		SBean.ForceWarCFGS fwc = GameData.getInstance().getForceWarCFGS(type);
		if(fwc == null)
			return;
		
		for(int i=0; i<side.size(); i++)
		{
			SBean.ForceWarOverview e = side.get(i);
			if(e.quit == 1)
				continue;
			
			int dayEnterTimes = roles.get(e.rid) == null ? 1 : roles.get(e.rid).joinInfo.dayEnterTimes + 1;
			e.gainFeat = (int) (GameData.getInstance().getForceWarFeatByRank(i + 1, win) * GameData.getForceWarDayFeatPercent(dayEnterTimes, fwc.gainFeats));
			e.rank = i + 1;
		}
	}
	
	static String getForceWarOverview(SBean.ForceWarOverview fo)
	{
		return "force war rank " + fo.rank + " role [" + fo.name + ", " + fo.rid + "] kills " + fo.kills + " bekills " + fo.bekills + " killNpcs " + fo.killNpcs + " assist " + fo.assist + " score " + fo.score + " quit " + fo.quit;
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	private class SuperArenaMapCopyCluster extends MapCopyCluster<SuperArenaMapCopyCluster.SuperArenaMapCopy>
	{
		int maxTime;
		int autoCloseTime;
		public SuperArenaMapCopyCluster(int mapID)
		{
			super(mapID, new ConcurrentHashMap<>());
			SBean.SuperArenaMapCFGS mapCfg = GameData.getInstance().getSuperArenaMapCFGS(mapID);
			if(mapCfg != null)
			{
				SBean.SuperArenaTypeCFGS typeCfg = GameData.getInstance().getSuperArenaTypeCFG(mapCfg.type);
				if(typeCfg != null)
					this.maxTime = typeCfg.maxTime * typeCfg.races;
			}
			
			this.autoCloseTime = GameData.getInstance().getSuperArenaCFGS().normal.autoCloseTime;
		}
		
		int createMap(int mapInstance, Set<Integer> gsids, TmpTeamPair pair)
		{
			fs.getLogger().info("create super arena map " + mapID + " , " + mapInstance + " success , maxTime = " + this.maxTime);
			if(addMap(new SuperArenaMapCopy(mapInstance, gsids, pair)))
			{
				for(int serverID: gsids)
					fs.getRPCManager().notifyGSCreateMapCopy(serverID, mapID, mapInstance);
				return mapInstance;
			}
			return 0;
		}
		
		public void onStart(int mapInstance)
		{
			SuperArenaMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onStart();
		}
		
		public void onEnd(int mapInstance, SBean.SuperArenaBattleResult result)
		{
			SuperArenaMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onEnd(result);
		}
		
		public void onRaceEnd(int mapInstance)
		{
			SuperArenaMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onRaceEnd();
		}
		
		class SuperArenaMapCopy extends MapCopy
		{
			Map<Integer, SBean.SuperArenaJoin> roles = new HashMap<>();
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;
			Map<Integer, Integer> role2elodiff = new HashMap<>();
			
			List<FightTmpTeam> tmpTeams = new ArrayList<>();
			SuperArenaMapCopy(int mapInstance, Set<Integer> gsids, TmpTeamPair pair) 
			{
				super(mapInstance, gsids);
				for(TmpTeam tt: pair.lst)
				{
					for(JoinTeam t: tt.teams)
					{
						List<Integer> members = new ArrayList<>();
						for(SBean.SuperArenaJoin e: t.members)
						{
							roles.put(e.overview.id, e);
							members.add(e.overview.id);
							role2elodiff.put(e.overview.id, e.elo - tt.getEnemyELO());
						}
						
						if(members.size() > 1)
							tmpTeams.add(new FightTmpTeam(members));
					}
				}
				this.closeTime = GameTime.getTime() + maxTime + autoCloseTime;
			}

			@Override
			boolean onTimer(int timeTick)
			{
				return this.closeTime <= timeTick;
			}
			
			synchronized boolean leaveMap(int roleID)
			{
				this.roles.remove(roleID);
				for(FightTmpTeam tt: tmpTeams)
					tt.delMember(roleID);
				
				return this.roles.isEmpty();
			}
			
			synchronized void onStart()
			{
				fs.getLogger().info("on super arena map " + mapID + " , " + mapInstance + " start");
				if(startTime == 0)
				{
					int now = GameTime.getTime();
					this.onGSMapCopyStart(now);
					this.startTime = now;
				}
			}
			
			void onGSMapCopyStart(int time)
			{
				fs.getLogger().info("sync gs " + gsids + " on super arena map copy " + mapID + " " + mapInstance + " start, left Time=" + (this.closeTime - time));
				for(int serverID: gsids)
					fs.getRPCManager().notifyGSSyncSuperArenaMapStart(serverID, mapID, mapInstance, new HashMap<>(this.role2elodiff));
			}
			
			synchronized void onEnd(SBean.SuperArenaBattleResult result)
			{
				fs.getLogger().info("on super arena map " + mapID + " " + mapInstance + " end");
				if (endTime == 0)
				{
					leaveGlobalTeam();
					recoverTeam(this.tmpTeams);
					int now = GameTime.getTime();
					this.popupResultTime = now;
					this.closeTime = popupResultTime + autoCloseTime;
					this.onGSMapcopyEnd(now, result);
					this.endTime = now;
				}
			}
			
			private void leaveGlobalTeam()
			{
				for(int rid: this.roles.keySet())
					fs.getFightTeamManager().roleLeaveTeam(rid);
			}
			
			void onGSMapcopyEnd(int time, SBean.SuperArenaBattleResult result)
			{
				calcResult(result);
				fs.getLogger().info("sync gs " + gsids + " on super arena map copy " + mapID + " " + mapInstance + " end, left Time=" + (this.closeTime - time));
				
				for(int serverID: gsids)
				{
					int rankClearTime = fs.getFightRankManager().getSuperArenaCurClearTime(serverID);
					fs.getRPCManager().notifyGSSyncSuperArenaMapEnd(serverID, mapID, mapInstance, result, rankClearTime);
				}
			}
			
			void calcResult(SBean.SuperArenaBattleResult result)
			{
				fs.getLogger().info("-----super arena " + mapID + " , " + mapInstance + " end calc result-----");
				SBean.SuperArenaCFGS cfg = GameData.getInstance().getSuperArenaCFGS();
				for(Map.Entry<Integer, SBean.SABattleTeamInfo> e: result.teams.entrySet())
				{
					int group = e.getKey();
					SBean.SABattleTeamInfo teamInfo = e.getValue();
					
					for(SBean.SABattleInfo info: teamInfo.members.values())
					{
						SBean.SuperArenaJoin role = roles.get(info.rid);
						if(role == null)
							continue;
						
						tryUpdateSuperArenaRank(info, role);
						Integer diff = role2elodiff.get(info.rid);
						if(diff == null)
							continue;
						
						double w = result.loseTeam == 0 ? cfg.eloParamNone : ((group == result.loseTeam) ? cfg.eloParamLose : cfg.eloParamWin);
						info.addELO = GameData.getInstance().getSuperArenaAddELO(role.elo, diff, w);
						fs.getLogger().info("-----super arena " + mapID + " , " + mapInstance + " end role old elo " + role.elo + " diff elo " + diff + " add elo " + info.addELO);
					}
				}
			}
			
			private void tryUpdateSuperArenaRank(SBean.SABattleInfo info, SBean.SuperArenaJoin role)
			{
				Integer gsid = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(role.overview.id));
				if(gsid == null)
					return;
				
				fs.getFightRankManager().tryUpdateRank(gsid, 
													   GameData.RANK_TYPE_SUPER_ARENA_WEEK, 
													   role.rankClearTime, 
													   new SBean.RankRole(role.overview, role.historyHonor + info.addHonor), 
													   new SBean.RankRole(role.overview, info.addHonor));
				
				fs.getFightRankManager().tryUpdateRank(gsid, 
													   GameData.RANK_TYPE_SUPER_ARENA_HISTORY, 
													   0, 
													   new SBean.RankRole(role.overview, role.historyHonor + info.addHonor));
			}
			
			synchronized void onRaceEnd()
			{
				fs.getLogger().info("sync gs " + gsids + " on super arena race [" + mapID + " , " + mapInstance + "] end");
				for(int serverID: gsids)
					fs.getRPCManager().nottifyGSEnterSuperArenaRace(serverID, mapID, mapInstance);
			}
		}
	}
	
	public class DemonHoleMapCopyCluster extends MapCopyCluster<DemonHoleMapCopyCluster.DemonHoleMapCopy>
	{
		int autoCloseTime;
		
		public DemonHoleMapCopyCluster(int mapID)
		{
			super(mapID, new ConcurrentHashMap<>());
			autoCloseTime = GameData.getInstance().getDemonHoleBaseCFGS().autoCloseTime;
		}

		int createMap(int mapInstance, int floor, Map<Integer, ServerRole> serverRoles)
		{
			fs.getLogger().info("create demon hole map [" + mapID + " , " + mapInstance +"] success");
			if(addMap(new DemonHoleMapCopy(mapInstance, floor, serverRoles)))
			{
				return mapInstance;
			}
			return 0;
		}
		
		void roleEnter(int roleID, int mapInstance)
		{
			DemonHoleMapCopy dhmc = this.maps.get(mapInstance);
			if(dhmc == null)
				return;
			
			dhmc.roleEnter(roleID);
		}
		
		void onEnd(int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
		{
			DemonHoleMapCopy dhmc = this.maps.get(mapInstance);
			if(dhmc == null)
				return;
			
			dhmc.onEnd(curFloor, total);
		}
		
		class DemonHoleMapCopy extends MapCopy
		{
			final int floor;
			int closeTime;
//			int startTime;
			int endTime;
			int popupResultTime;
			
			
			Map<Integer, ServerRole> serverRoles = new HashMap<>();
			DemonHoleMapCopy(int mapInstance, int floor, Map<Integer, ServerRole> serverRoles)
			{
				super(mapInstance, new HashSet<>());
				this.floor = floor;
				for(Map.Entry<Integer, ServerRole> e: serverRoles.entrySet())
					this.serverRoles.put(e.getKey(), e.getValue());
			}
			
			@Override
			synchronized boolean onTimer(int timeTick)
			{
				return this.closeTime > 0 && this.closeTime <= timeTick;
			}

			@Override
			synchronized boolean leaveMap(int roleID)
			{
				Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(serverID == null)
					return false;
				
				ServerRole sr = serverRoles.get(serverID);
				if(sr == null)
					return false;
				
				sr.delRole(roleID);
				if(sr.isEmpty())
					serverRoles.remove(serverID);
				
				return false;
			}
			
			synchronized void roleEnter(int roleID)
			{
				Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
				if(serverID == null)
					return;
				
				ServerRole sr = serverRoles.get(serverID);
				if(sr == null)
				{
					sr = new ServerRole();
					serverRoles.put(serverID, sr);
					fs.getRPCManager().notifyGSCreateMapCopy(serverID, mapID, mapInstance);
				}
				sr.addRole(roleID);
			}
			
			synchronized void onEnd(List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
			{
				if(this.endTime == 0)
				{
					int now = GameTime.getTime();
					this.popupResultTime = now;
					this.closeTime = this.popupResultTime + autoCloseTime;
					onGSMapcopyEnd(now, curFloor, total);
					this.endTime = now;
					this.gsids.addAll(serverRoles.keySet());
				}
			}
			
			private void onGSMapcopyEnd(int now, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
			{
				fs.getLogger().info("on demon hole map copy[" + mapID + " " + mapInstance + "] end left Time= " + (this.closeTime - now));
				for(int serverID: this.serverRoles.keySet())
					fs.getRPCManager().notifyGSSyncDemonHoleMapEnd(serverID, mapID, mapInstance, curFloor, total);
			}
		}
	}
}
