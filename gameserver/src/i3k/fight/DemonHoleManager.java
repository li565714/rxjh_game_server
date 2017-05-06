package i3k.fight;


import i3k.DBDemonHoleGroup;
import i3k.SBean;
import i3k.SBean.RankCFGS;
import i3k.SBean.RankDemon;
import i3k.gs.FightService;
import i3k.gs.GameData;
import i3k.gs.RankData;
import i3k.gs.RankData.RankItemReader;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ket.kdb.Table;
import ket.kdb.TableEntry;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;

public class DemonHoleManager
{
	private static final int DEMON_HOLE_CURFLOOR_BATTLE_RANK_COUNT = 20;
	private static final int DEMON_HOLE_TOTAL_BATTLE_RANK_COUNT = 20;
	final static RankDemonReader RankDemonHoleReader =  new RankDemonReader();
	
	private static final int DEMONHOLE_SAVE_INTERVAL 	= 900;
	int saveTime;
	
	final FightServer fs;
	List<DemonHoleGrade> allGrades = new ArrayList<>();
	ConcurrentMap<Integer, Integer> role2grade = new ConcurrentHashMap<>();
	AtomicInteger nextInstanceID = new AtomicInteger();
	volatile int lastOpenTime;
	volatile int lastEndTime;
	
	public class SaveTrans implements Transaction
	{
		SaveTrans(Map<Integer, DBDemonHoleGroup> groups)
		{
			this.groups = groups;
		}
		
		@Override
		public boolean doTransaction()
		{
			groups.entrySet().forEach(e -> demonholegroups.put(e.getKey(), e.getValue()));
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				fs.getLogger().warn("demon hole groups save failed");
			}
		}
		
		private final Map<Integer, DBDemonHoleGroup> groups;
		
		@AutoInit
		public Table<Integer, DBDemonHoleGroup> demonholegroups;
	}
	
	DemonHoleManager(FightServer fs)
	{
		this.fs = fs;
	}
	
	void init(TableReadonly<Integer, DBDemonHoleGroup> dbGroups)
	{
		for(int i = 0; i < GameData.getInstance().getDemonHoleGrades(); i++)
			allGrades.add(new DemonHoleGrade(i + 1));
		
		if(dbGroups == null)
			return;
		
		boolean inOpenTime = GameData.getInstance().checkDemonHoleInOpenTime(GameTime.getTime());
		int nowDay = GameData.getDayByRefreshTimeOffset(GameTime.getTime());
		for(TableEntry<Integer, DBDemonHoleGroup> e: dbGroups)
		{
			if(e.getValue().lastOpenTime > this.lastOpenTime)
				this.lastOpenTime = e.getValue().lastOpenTime;
			
			if(e.getValue().lastEndTime > this.lastEndTime)
				this.lastEndTime = e.getValue().lastEndTime;
			
			if(!inOpenTime)
				continue;
			
			int lastOpenDay = GameData.getDayByRefreshTimeOffset(e.getValue().lastOpenTime);
			if(nowDay != lastOpenDay)
				continue;
			
			int grade = GameData.getDemonHoleGradeFromKey(e.getKey());
			int groupID = GameData.getDemonHoleGroupIDFromKey(e.getKey());
			if(grade <= 0 || grade > allGrades.size())
				continue;
		
			allGrades.get(grade - 1).addGroup(groupID, e.getValue());
		}
	}
	
	Map<Integer, DBDemonHoleGroup> toDB()
	{
		Map<Integer, DBDemonHoleGroup> dbGroups = new HashMap<>();
		for(int grade = 1; grade <= allGrades.size(); grade++)
			dbGroups.putAll(allGrades.get(grade - 1).toDB(grade));
		
		return dbGroups;
	}
	
	void save()
	{
		fs.getDB().execute(new SaveTrans(toDB()));
		this.saveTime = GameTime.getTime();
	}
	
	void onTimer(int timeTick)
	{
		int newOpenTamp = GameTime.getDayTime(GameData.getInstance().getDemonHoleBaseCFGS().startTime);
		int newEndTamp = newOpenTamp + GameData.getInstance().getDemonHoleBaseCFGS().lastTime;
		if(newOpenTamp > this.lastOpenTime && timeTick >  newOpenTamp)
			this.lastOpenTime = timeTick;
		
		if(newEndTamp > this.lastEndTime && timeTick > newEndTamp)
		{
			this.lastEndTime = timeTick;
			this.allGrades.forEach(g -> g.onEnd());
			this.role2grade.clear();
		}
		
		if(timeTick - this.saveTime > DEMONHOLE_SAVE_INTERVAL)
			this.save();
	}
	
	public static class RankDemonReader implements RankItemReader<SBean.RankDemon>
	{
		@Override
		public int getRankItemId(RankDemon rankItem)
		{
			return rankItem.roleID;
		}

		@Override
		public int getRankItemKey(RankDemon rankItem) 
		{
			return rankItem.rankKey;
		}

		@Override
		public boolean canUpdateRankItem(RankDemon rankItem, RankCFGS cfg, boolean blackListOn, Set<Integer> idBlackList)
		{
			return true;
		}
	}
	
	public static class DemonHoleRank extends RankData.Ranks<SBean.RankDemon>
	{
		final int rankShowCnt;
		public DemonHoleRank(RankItemReader<SBean.RankDemon> reader, int rankShowCnt)
		{
			super(reader);
			this.rankShowCnt = rankShowCnt;
		}
		
		List<SBean.RoleDemonHole> getShowRanks(Map<Integer, SBean.RoleDemonHole> roles)
		{
			List<SBean.RoleDemonHole> lst = new ArrayList<>();
			for(long rv: rankValues.descendingSet())
			{
				SBean.RoleDemonHole info = roles.get(getItemId(rv));
				if(info == null)
					continue;
				
				lst.add(info.kdClone());
				if(lst.size() == rankShowCnt)
					return lst;
			}
			return lst;
		}
	}
	
	class DemonHoleGroup
	{
		int cfgMaxRoles;
		
		private List<Integer> floors = new ArrayList<>();						//<instanceID>
		private Map<Integer, Integer> mapInstance2floor = new HashMap<>();		//<instanceID, floor>
		final int grade;
		final int groupID;
		
		List<DemonHoleFloor> floorRoles = new ArrayList<>();
		Map<Integer, SBean.RoleDemonHole> allRoles = new HashMap<>();		//<roleID,>
		DemonHoleRank rank = new DemonHoleRank(RankDemonHoleReader, DEMON_HOLE_TOTAL_BATTLE_RANK_COUNT);
		
		DemonHoleGroup(int grade, int groupID)
		{
			this.grade = grade;
			this.groupID = groupID;
			SBean.DemonHoleGradeCFGS cfg = GameData.getInstance().getDemonHoleGradeCFGS(grade);
			if(cfg != null)
			{
				this.cfgMaxRoles = cfg.maxRoles;
				for(int f = 1; f <= cfg.maps.size(); f++)
				{
					int instanceID = nextInstanceID.incrementAndGet();
					floors.add(instanceID);
					mapInstance2floor.put(instanceID, f);
				}

			}
		}
		
		DemonHoleGroup(int grade, int groupID, Map<Integer, Integer> role2group, DBDemonHoleGroup dbGroup)
		{
			this.grade = grade;
			this.groupID = groupID;
			SBean.DemonHoleGradeCFGS cfg = GameData.getInstance().getDemonHoleGradeCFGS(grade);
			if(cfg != null)
			{
				this.cfgMaxRoles = cfg.maxRoles;
				for(int f = 1; f <= cfg.maps.size(); f++)
				{
					int instanceID = nextInstanceID.incrementAndGet();
					int mapID = cfg.maps.get(f - 1);
					floors.add(instanceID);
					mapInstance2floor.put(instanceID, f);
					
					if(f > dbGroup.floors.size())
						continue;
					
					SBean.DBDemonHoleFloor dbFloor = dbGroup.floors.get(f - 1);
					floorRoles.add(new DemonHoleFloor(mapID, f, dbFloor));
					for(SBean.RoleDemonHole r: dbFloor.roles)
					{
						allRoles.put(r.role.id, r.kdClone());
						if(r.kills > 0)
							rank.tryUpdateRankNoLength(new SBean.RankDemon(r.role.id, GameData.createDemonHoleRankKey(r.kills, r.bekills)));
						
						role2group.put(r.role.id, groupID);
						DemonHoleManager.this.role2grade.put(r.role.id, grade);
					}
				}
			}
		}
		
		DemonHoleFloor createFloor(int floor)
		{
			DemonHoleFloor df = new DemonHoleFloor(GameData.getInstance().getDemonHoleFloorMapID(this.grade, floor), floor);
			floorRoles.add(df);
			return df;
		}
		
		boolean isFull()
		{
			return allRoles.size() >= cfgMaxRoles;
		}
		
		public DBDemonHoleGroup toDB()
		{
			DBDemonHoleGroup dbGroup = new DBDemonHoleGroup();
			dbGroup.floors = getDBFloors();
			dbGroup.lastOpenTime = DemonHoleManager.this.lastOpenTime;
			dbGroup.lastEndTime = DemonHoleManager.this.lastEndTime;
			return dbGroup;
		}
		
		private List<SBean.DBDemonHoleFloor> getDBFloors()
		{
			List<SBean.DBDemonHoleFloor> dbFloors = new ArrayList<>();
			for(int floor = 0; floor < floors.size(); floor++)
			{
				if(floor >= floorRoles.size())
					continue;
				
				dbFloors.add(floorRoles.get(floor).toDB());
			}
			return dbFloors;
		}
		
		int syncRoleDemonHole(int roleID)
		{
			SBean.RoleDemonHole info = allRoles.get(roleID);
			return info == null ? 0 : info.curFloor;
		}
		
		int roleEnter(SBean.RoleOverview role)
		{
			SBean.RoleDemonHole info = allRoles.get(role.id);
			if(info == null)
				return GameData.PROTOCOL_OP_FAILED;
			
			if(info.curFloor <= 0 || info.curFloor > floorRoles.size())
				return GameData.PROTOCOL_OP_FAILED;
			
			int ok = floorRoles.get(info.curFloor - 1).enter(role, this.grade, this.groupID, floors.get(info.curFloor - 1));
			if(ok <= 0)
				return ok;
			
			return this.grade;
		}
		
		int roleFirstEnter(SBean.RoleOverview role)
		{
			DemonHoleFloor df = floorRoles.isEmpty() ? createFloor(1) : floorRoles.get(0);
			int ok =  df.enter(role, this.grade, this.groupID, floors.get(0));
			if(ok <= 0)
				return ok;
			
			SBean.RoleDemonHole info = new SBean.RoleDemonHole(role, 0, 0, 1);
			allRoles.put(role.id, info);
			info.curFloor = 1;
			return this.grade;
		}

		int roleChangeFloor(SBean.RoleOverview role, int toFloor)
		{
			SBean.RoleDemonHole info = allRoles.get(role.id);
			if(info == null)
				return GameData.PROTOCOL_OP_DEMON_HOLE_ROLE_NOT_JOIN;
			
			if(toFloor <= 0 || toFloor > floors.size())
				return GameData.PROTOCOL_OP_DEMON_HOLE_OUT_FLOOR;
			
			
			return GameData.PROTOCOL_OP_SUCCESS;
		}
		
		void roleEnterDemonHoleFloor(SBean.RoleOverview role, int toFloor)
		{
			SBean.RoleDemonHole info = allRoles.get(role.id);
			if(info == null)
				return;
			
			if(toFloor <= 0 || toFloor > floors.size())
				return;
			
			DemonHoleFloor df = toFloor > floorRoles.size() ? createFloor(toFloor) : floorRoles.get(toFloor - 1);
			df.enter(role, this.grade, this.groupID, floors.get(toFloor - 1));
			info.curFloor = toFloor;
		}
		
		void queryBattleInfo(int roleID, FightService.RoleDemonHoleBattleReqCallBack callback)
		{
			SBean.RoleDemonHole info = allRoles.get(roleID);
			if(info == null)
			{
				callback.onCallBack(GameData.emptyList(), GameData.emptyList());
				return;
			}
			
			if(info.curFloor == 0 || info.curFloor > floorRoles.size())
			{
				callback.onCallBack(GameData.emptyList(), GameData.emptyList());
				return;
			}
			
			DemonHoleFloor df = floorRoles.get(info.curFloor - 1);
			callback.onCallBack(df.queryBattleInfo(), getTotalBattleRanks());
		}
		
		List<SBean.RoleDemonHole> getTotalBattleRanks()
		{
			return this.rank.getShowRanks(allRoles);
		}
		
		void onCreateDemonHoleMapSuccess(int mapInstance)
		{
			Integer floor = mapInstance2floor.get(mapInstance);
			if(floor == null || floor <= 0 || floor > floorRoles.size())
				return;
			
			floorRoles.get(floor - 1).onCreateDemonHoleMapSuccess(mapInstance, this.grade);
		}
		
		void onEnd()
		{
			List<SBean.RoleDemonHole> total = this.getTotalBattleRanks();
			this.floorRoles.forEach(f -> f.onEnd(total));
		}
		
		void syncRoleKill(int killerID, int deaderID)
		{
			SBean.RoleDemonHole killer = allRoles.get(killerID);
			SBean.RoleDemonHole deader = allRoles.get(deaderID);
			if(killer == null || deader == null)
				return;
			
			if(killer.curFloor == 0 || killer.curFloor > floorRoles.size())
				return;
			
			DemonHoleFloor df = floorRoles.get(killer.curFloor - 1);
			df.syncKill(killerID, deaderID);
			killer.kills++;
			deader.bekills++;
			rank.tryUpdateRankNoLength(new SBean.RankDemon(killerID, GameData.createDemonHoleRankKey(killer.kills, killer.bekills)));
			if(deader.kills > 0)
				rank.tryUpdateRankNoLength(new SBean.RankDemon(deaderID, GameData.createDemonHoleRankKey(deader.kills, deader.bekills)));
		}
		
		void mapStopWork()
		{
			this.floorRoles.forEach(f -> f.mapStopWork());
		}
	}
	
	public static class ServerRole
	{
		Set<Integer> roles = new HashSet<>();
		void addRole(int roleID)
		{
			this.roles.add(roleID);
		}
		
		boolean delRole(int roleID)
		{
			return this.roles.remove(roleID);
		}
		
		boolean isEmpty()
		{
			return this.roles.isEmpty();
		}
	}
	
	class DemonHoleFloor
	{
		final int mapID;
		int instanceID;		//-1:正在创建，0：未创建，> 0:已创建
		final int floor;
		
		Map<Integer, ServerRole> cache = new HashMap<>();
		
		Map<Integer, SBean.RoleDemonHole> roles = new HashMap<>();
		DemonHoleRank rank = new DemonHoleRank(RankDemonHoleReader, DEMON_HOLE_CURFLOOR_BATTLE_RANK_COUNT);
		
		DemonHoleFloor(int mapID, int floor)
		{
			this.mapID = mapID;
			this.floor = floor;
		}
		
		DemonHoleFloor(int mapID, int floor, SBean.DBDemonHoleFloor dbFloor)
		{
			this.mapID = mapID;
			this.floor = floor;
			for(SBean.RoleDemonHole r: dbFloor.roles)
			{
				roles.put(r.role.id, r);
				rank.tryUpdateRankNoLength(new SBean.RankDemon(r.role.id, GameData.createDemonHoleRankKey(r.kills, r.bekills)));
			}
		}
		
		public SBean.DBDemonHoleFloor toDB()
		{
			return new SBean.DBDemonHoleFloor(roles.values().stream().collect(Collectors.toList()));
		}
		
		int enter(SBean.RoleOverview role, int grade, int groupID, int mapInstance)
		{
			Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(role.id));
			if(serverID == null)
				return GameData.PROTOCOL_OP_FAILED;
			
			if(!roles.containsKey(role.id))
				roles.put(role.id, new SBean.RoleDemonHole(role.kdClone(), 0, 0, 1));
			
			switch (this.instanceID)
			{
			case -1:
				break;
			case 0:
				fs.getGlobalMapService().createDemonHoleMap(grade, groupID, this.mapID, mapInstance);
				this.instanceID = -1;
				break;
			default:
				fs.getGlobalMapService().roleEnterDemonHoleFloor(role.kdClone(), this.mapID, mapInstance);
				fs.getRPCManager().notifyGSRoleEnterDemonHole(role.id, this.mapID, mapInstance, this.floor, grade);
				return GameData.PROTOCOL_OP_SUCCESS;
			}
			
			ServerRole sr = cache.get(serverID);
			if(sr == null)
			{
				sr = new ServerRole();
				cache.put(serverID, sr);
			}
			sr.addRole(role.id);
			
			return GameData.PROTOCOL_OP_SUCCESS;
		}
		
		List<SBean.RoleDemonHole> queryBattleInfo()
		{
			return this.rank.getShowRanks(roles);
		}
		
		public void onCreateDemonHoleMapSuccess(int mapInstance, int grade)
		{
			if(this.instanceID > 0)
				return;
			
			int instanceID = fs.getGlobalMapService().createDemonHoleMapImpl(this.mapID, mapInstance, this.floor, this.cache);
			if(instanceID > 0)
			{
				for(Map.Entry<Integer, ServerRole> e: this.cache.entrySet())
				{
					fs.getRPCManager().notifyGSCreateMapCopy(e.getKey(), this.mapID, mapInstance);
					for(int roleID: e.getValue().roles)
					{
						SBean.RoleDemonHole info = roles.get(roleID);
						if(info != null)
						{
							fs.getRPCManager().notifyGSRoleEnterDemonHole(roleID, mapID, mapInstance, floor, grade);
							fs.getRoleManager().roleEnterMap(info.role.kdClone(), mapID, mapInstance);
						}
					}
				}
				
				this.cache.clear();
			}
			
			this.instanceID = instanceID;
		}
		
		void onEnd(final List<SBean.RoleDemonHole> total)
		{
			if(this.instanceID > 0)
				fs.getGlobalMapService().handleDemonHoleEnd(this.mapID, this.instanceID, queryBattleInfo(), total);
		}
		
		void syncKill(int killerID, int deaderID)
		{
			SBean.RoleDemonHole killer = roles.get(killerID);
			if(killer != null)
			{
				killer.kills++;
				rank.tryUpdateRankNoLength(new SBean.RankDemon(killerID, GameData.createDemonHoleRankKey(killer.kills, killer.bekills)));
			}
			
			SBean.RoleDemonHole deader = roles.get(deaderID);
			if(deader != null)
			{
				deader.bekills++;
				if(deader.kills > 0)
					rank.tryUpdateRankNoLength(new SBean.RankDemon(deaderID, GameData.createDemonHoleRankKey(deader.kills, deader.bekills)));
			}
		}
		
		void mapStopWork()
		{
			this.instanceID = 0;
		}
	}
	
	class DemonHoleGrade
	{
		final int grade;
		TreeMap<Integer, DemonHoleGroup> groups = new TreeMap<>();
		Map<Integer, Integer> role2group = new HashMap<>();
		
		private int nextGroupID;
		
		DemonHoleGrade(int grade)
		{
			this.grade = grade;
		}
		
		private void reset()
		{
			this.groups.clear();
			this.role2group.clear();
			nextGroupID = 0;
		}
		
		private int getNextGroupID()
		{
			return ++nextGroupID;
		}
		
		public synchronized DemonHoleGroup createNewGroup()
		{
			int groupID = getNextGroupID();
			DemonHoleGroup group = new DemonHoleGroup(grade, groupID);
			groups.put(groupID, group);
			return group;
		}
		
		public synchronized Map<Integer, DBDemonHoleGroup> toDB(int grade)
		{
			return groups.entrySet().stream().collect(Collectors.toMap(e -> GameData.createDemonHoleGroupKey(grade, e.getKey()), e -> e.getValue().toDB()));
		}
		
		public synchronized void addGroup(int groupID, DBDemonHoleGroup dbGroup)
		{
			groups.put(groupID, new DemonHoleGroup(grade, groupID, role2group, dbGroup));
		}
		
		private DemonHoleGroup getGroupByRoleID(int roleID)
		{
			Integer groupID = role2group.get(roleID);
			if(groupID == null)
				return null;
			
			return groups.get(groupID);
		}
		
		public synchronized int syncRoleDemonHole(int roleID)
		{
			DemonHoleGroup group = getGroupByRoleID(roleID);
			if(group == null)
				return 0;
			
			return group.syncRoleDemonHole(roleID);
		}
		
		public synchronized int roleJoin(SBean.RoleOverview role)
		{
			DemonHoleGroup group = getGroupByRoleID(role.id);
			if(group != null)		//已经参加过
			{
				return group.roleEnter(role);
			}
			else
			{
				group = groups.lastEntry() == null ? null : groups.lastEntry().getValue();
				if(group == null || group.isFull())
					group = this.createNewGroup();
				
				int ok = group.roleFirstEnter(role);
				if(ok > 0)
				{
					role2group.put(role.id, group.groupID);
					DemonHoleManager.this.role2grade.put(role.id, this.grade);
				}
				
				return ok;
			}
		}
		
		public synchronized int roleChangeFloor(SBean.RoleOverview role, int toFloor)
		{
			DemonHoleGroup group = getGroupByRoleID(role.id);
			if(group == null)
				return GameData.PROTOCOL_OP_DEMON_HOLE_ROLE_NOT_JOIN;
			
			return group.roleChangeFloor(role, toFloor);
		}
		
		public synchronized void roleEnterDemonHoleFloor(SBean.RoleOverview role, int toFloor)
		{
			DemonHoleGroup group = getGroupByRoleID(role.id);
			if(group == null)
				return;
			
			group.roleEnterDemonHoleFloor(role, toFloor);
		}
		
		public synchronized void queryBattleInfo(int roleID, FightService.RoleDemonHoleBattleReqCallBack callback)
		{
			DemonHoleGroup group = getGroupByRoleID(roleID);
			if(group == null)
			{
				callback.onCallBack(GameData.emptyList(), GameData.emptyList());
				return;
			}
			
			group.queryBattleInfo(roleID, callback);
		}
		
		public synchronized void onCreateDemonHoleMapSuccess(int groupID, int mapInstance)
		{
			DemonHoleGroup group = groups.get(groupID);
			if(group == null)
				return;
			
			group.onCreateDemonHoleMapSuccess(mapInstance);
		}
		
		//活动结束
		public synchronized void onEnd()
		{
			this.groups.values().forEach(group -> group.onEnd());
			this.reset();
		}
		
		public synchronized void syncRoleKill(int killerID, int deaderID)
		{
			DemonHoleGroup group = getGroupByRoleID(killerID);
			if(group == null)
				return;
			
			group.syncRoleKill(killerID, deaderID);
		}
		
		public synchronized void mapStopWork()
		{
			this.groups.values().forEach(group -> group.mapStopWork());
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------
	private DemonHoleGrade getDemonHoleGrade(int grade)
	{
		if(grade <= 0 || grade > allGrades.size())
			return null;
		
		return allGrades.get(grade - 1);
	}
	
	private DemonHoleGrade getDemonHoleGradeByRoleID(int roleID)
	{
		Integer grade = this.role2grade.get(roleID);
		if(grade == null || grade <= 0 || grade > allGrades.size())
			return null;
		
		return allGrades.get(grade - 1);
	}
	
	public void syncRoleDemonHole(int roleID, FightService.SyncRoleDemonHoleReqCallBack callback)
	{
		DemonHoleGrade dhGrade = getDemonHoleGradeByRoleID(roleID);
		if(dhGrade == null)
		{
			callback.onCallback(0, -1);
			return;
		}
		
		callback.onCallback(dhGrade.syncRoleDemonHole(roleID), dhGrade.grade);
	}
	
	public void roleJoin(SBean.RoleOverview role, FightService.RoleJoinDemonHoleReqCallBack callback)
	{
		if(!GameData.getInstance().checkDemonHoleInOpenTime(GameTime.getTime()))
		{
			callback.onCallBack(GameData.PROTOCOL_OP_DEMON_HOLE_NOT_IN_OPENTIME);
			return;
		}
		
		DemonHoleGrade grade = getDemonHoleGradeByRoleID(role.id);
		if(grade == null)
			grade = getDemonHoleGrade(GameData.getInstance().getDemonHoleGradeByLevel(role.level));

		if(grade == null)
		{
			callback.onCallBack(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		callback.onCallBack(grade.roleJoin(role));
	}
	
	public void roleChangeFloor(SBean.RoleOverview role, int toFloor, FightService.RoleChangeDemonFloorReqCallBack callback)
	{
		DemonHoleGrade grade = getDemonHoleGradeByRoleID(role.id);
		if(grade == null)
		{
			callback.onCallBack(GameData.PROTOCOL_OP_DEMON_HOLE_ROLE_NOT_JOIN);
			return;
		}
		
		callback.onCallBack(grade.roleChangeFloor(role, toFloor));
	}
	
	public void roleEnterDemonHoleFloor(SBean.RoleOverview role, int toFloor)
	{
		DemonHoleGrade grade = getDemonHoleGradeByRoleID(role.id);
		if(grade == null)
			return;
		
		grade.roleEnterDemonHoleFloor(role, toFloor);
	}
	
	public void queryBattleInfo(int roleID, FightService.RoleDemonHoleBattleReqCallBack callback)
	{
		DemonHoleGrade grade = getDemonHoleGradeByRoleID(roleID);
		if(grade == null)
		{
			callback.onCallBack(GameData.emptyList(), GameData.emptyList());
			return;
		}
		
		grade.queryBattleInfo(roleID, callback);
	}
	
	public void onCreateDemonHoleMapSuccess(int grade, int groupID, int mapInstance)
	{
		DemonHoleGrade dGrade = getDemonHoleGrade(grade);
		if(dGrade == null)
			return;
		
		dGrade.onCreateDemonHoleMapSuccess(groupID, mapInstance);
	}
	
	public void syncRoleKill(int killerID, int deaderID)
	{
		DemonHoleGrade dGrade = getDemonHoleGradeByRoleID(killerID);
		if(dGrade == null)
			return;
		
		dGrade.syncRoleKill(killerID, deaderID);
	}
	
	public void mapStopWork()
	{
		this.allGrades.forEach(g -> g.mapStopWork());
	}
}
