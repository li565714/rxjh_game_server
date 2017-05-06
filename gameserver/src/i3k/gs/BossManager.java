package i3k.gs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;






import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;
import i3k.SBean;
import i3k.TLog;
import i3k.util.GameRandom;
import i3k.util.GameTime;
public class BossManager
{
	private static final int BOSS_SAVE_INTERVAL = 900;
	public static final int BOSS_CREATE_WORLD_LINE = 1;
	
	private static final int BOSS_STATE_GOTOREFRESH 	= 1;		//即将刷新
	private static final int BOSS_STATE_ALREADYREFRESH 	= 2;		//已经刷新
	private static final int BOSS_STATE_FIGHTING 		= 3;		//正在战斗
	private static final int BOSS_STATE_DEAD 			= 4;		//死亡
	private static final int BOSS_STATE_NOREFRESH 		= 5;		//未刷新·
	
	public static final int BOSS_REFRESH_FORWARDTIME	= 10 * 60;	//load时减10分钟
	public static final int BOSS_DANGEROUS_LEVEL		= 10;
	
	public class SaveTrans implements Transaction
	{
		public SaveTrans(SBean.DBBoss dbboss)
		{
			this.dbboss = dbboss;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("boss");
			byte[] data = Stream.encodeLE(dbboss);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if( errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("world map boss save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		public final SBean.DBBoss dbboss;
	}
	
	public static class Boss
	{
		public Boss(int id, SBean.DBBossState bossState, int standbyTime, int maxHp, boolean canRefresh, int lastPopTime, int mapID)
		{
			this.id = id;
			this.bossState = bossState;
			this.standbyTime = standbyTime;
			this.maxHp = maxHp;
			this.canRefresh = canRefresh;
			this.lastPopTime = lastPopTime;
			this.mapID = mapID;
		}
		int id;
		SBean.DBBossState bossState;
		int standbyTime;
		int maxHp;
		boolean canRefresh;
		int lastPopTime;
		int mapID;
		
		int tryPop(SBean.WorldBossCFGS cfg, int timeTick)
		{
			for(int i = 0; i < cfg.popTimes.size(); i++)
			{
				if(timeTick >= (this.bossState.refreshTime + cfg.popTimes.get(i)) && (this.bossState.refreshTime + cfg.popTimes.get(i)) > lastPopTime)
				{
					lastPopTime = timeTick;
					return i + 1;
				}
			}
			return -1;
		}
	}
	
	public BossManager(GameServer gs)
	{
		this.gs = gs;
	}

	public void init(SBean.DBBoss dbBoss)
	{
		this.initWorldMap();
		int now = GameTime.getTime();
		if(dbBoss != null)
		{
			for(Map.Entry<Integer, SBean.DBBossState> e: dbBoss.bosses.entrySet())
			{
				int bossID = e.getKey();
				SBean.WorldBossCFGS cfg = GameData.getInstance().getWorldBossCFGS(bossID);
				if(cfg == null)
					continue;
				
				SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(cfg.base.monsterID);
				if(monsterCfg == null)
					continue;
				
				SBean.DBBossState state = e.getValue();
				this.bosses.put(bossID, new Boss(bossID, state, cfg.base.standbyTime, monsterCfg.maxHP, state.refreshTime + cfg.base.standbyTime > now, now, cfg.base.mapID));
			}
			this.lastDayRefresh = dbBoss.lastDayRefresh;
		}
		this.lastCreateSuperMonsterTime = GameTime.getTime();
		this.lastCreateMineralTime = GameTime.getTime();
	}
	
	void initWorldMap()
	{
		for(SBean.WorldMapCFGS w: GameData.getInstance().getAllWorldMapCFGS())
			this.worldMapClusters.put(w.id, new WorldMapCluster().init(w, w.worldNum > 0 ? w.worldNum : gs.getConfig().getWorldLineNum(w.id)));
	}
	
	synchronized void onWorldLineNumChange(int num, Map<Integer, Integer> extralWorldNum)
	{
		for(WorldMapCluster wc: this.worldMapClusters.values())
		{
			wc.onWorldLineNumChange(num, extralWorldNum.getOrDefault(wc.mapID, 0));
		}
	}
	
	public void save()
	{
		gs.getDB().execute(new SaveTrans(getDBBoss()));
		this.lastSaveTime = GameTime.getTime();
	}
	
	private synchronized SBean.DBBoss getDBBoss()
	{
		SBean.DBBoss dbBoss = new SBean.DBBoss(this.lastDayRefresh, new HashMap<>());
		this.bosses.values().forEach(b -> dbBoss.bosses.put(b.id, b.bossState.kdClone()));
		
		return dbBoss;
	}
	
	private synchronized SBean.DBBoss getLiveDBBoss(Set<Integer> maps)
	{
		int now = GameTime.getTime();
		SBean.DBBoss dbBoss = new SBean.DBBoss(this.lastDayRefresh, new HashMap<>());
		this.bosses.values().stream().filter(b -> maps.contains(b.mapID) && b.bossState.curHp > 0 && (now > b.bossState.refreshTime) && (b.bossState.refreshTime + b.standbyTime) > now).forEach(b -> {
			dbBoss.bosses.put(b.id, b.bossState.kdClone());
			b.canRefresh = false;
		});
		
		return dbBoss;
	}
	
	public void onTimer(int timeTick)
	{
		this.tryUpdateBossState(timeTick);
		this.tryRefreshBoss(timeTick);
		this.tryCreateSuperMonster(timeTick);
		this.tryCreateMineral(timeTick);
		this.dayRefresh(timeTick);
		if(this.lastSaveTime + BOSS_SAVE_INTERVAL <= timeTick)
			this.save();
	}
	
	private synchronized void tryUpdateBossState(int timeTick)
	{
		Map<Integer, SBean.WorldBossCFGS> allBoss = GameData.getInstance().getAllWorldBoss();
		int weekDay = GameTime.getWeekdayByOffset(timeTick, GameData.GAME_DAY_REFRESH_TIME * 3600);
		for(SBean.WorldBossCFGS cfg: allBoss.values())
		{
			List<Integer> refreshTime = cfg.base.refreshTime;
			int lastRefreshTime = GameData.getLastRefreshTime(timeTick, refreshTime);
			int bossID = cfg.base.id;
			SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(cfg.base.monsterID);
			if(monsterCfg == null)
				continue;
			
			Boss boss = this.bosses.get(bossID);
			if(boss == null)
			{	
				boolean canRefresh = lastRefreshTime + BOSS_REFRESH_FORWARDTIME + cfg.base.standbyTime > timeTick;
				boss = new Boss(bossID, new SBean.DBBossState(canRefresh ? monsterCfg.maxHP : 0, GameRandom.getRandInt(1, cfg.base.refreshPos.size() + 1), lastRefreshTime + BOSS_REFRESH_FORWARDTIME, "", new HashMap<>()), 
						cfg.base.standbyTime, monsterCfg.maxHP, canRefresh, timeTick, cfg.base.mapID);
				this.bosses.put(bossID, boss);
			}
			
			if(lastRefreshTime > boss.bossState.refreshTime && cfg.base.openDay.contains(weekDay) && !boss.canRefresh)
			{
				 if(lastRefreshTime >= GameTime.getDayTime(refreshTime.get(0)))
				 {
					 boss.maxHp = monsterCfg.maxHP;
					 boss.bossState.refreshTime = lastRefreshTime + BOSS_REFRESH_FORWARDTIME;
					 boss.bossState.curHp = boss.maxHp;
					 boss.bossState.seq = GameRandom.getRandInt(1, cfg.base.refreshPos.size() + 1);
					 boss.canRefresh = true;
				 }
			}
		}
	}
	
	private synchronized void tryRefreshBoss(int timeTick)
	{
		int weekDay = GameTime.getWeekdayByOffset(timeTick, GameData.GAME_DAY_REFRESH_TIME * 3600);
		StringBuilder refreshed = null;
		for(Boss b: this.bosses.values())
		{
			int bossID = b.id;
			SBean.WorldBossCFGS cfg = GameData.getInstance().getWorldBossCFGS(bossID);
			if(cfg == null)
				continue;
			
			//refreshTime 错误校正
			if(b.bossState.refreshTime - timeTick >= 3600)
			{
				b.bossState.refreshTime = 0;
				b.canRefresh = false;
				gs.getLogger().warn("world boss " + bossID + " refreshTime " + GameTime.getDateTimeStampStr(b.bossState.refreshTime) + " error");
				continue;
			}
			
			if(timeTick >= b.bossState.refreshTime && b.canRefresh)
			{
				if(!cfg.base.openDay.contains(weekDay))
					continue;
				
				b.standbyTime = cfg.base.standbyTime;
				b.canRefresh = false;
				int standbyTime =  b.bossState.refreshTime + cfg.base.standbyTime - timeTick;
				if(standbyTime > 0)
				{
					if(gs.getMapService().syncCreateWorldMapBoss(cfg.base.mapID, BOSS_CREATE_WORLD_LINE, b.id, b.bossState.seq, b.bossState.curHp))
					{
						this.addBoss(new SBean.ActivityEntity(bossID, GameData.ACTIVITY_ENTITY_TYPE_WORLDBOSS, cfg.base.mapID, BOSS_CREATE_WORLD_LINE, b.bossState.seq));
						if(refreshed == null)
							refreshed = new StringBuilder();

						refreshed.append(b.id);
						refreshed.append("|");
					}
				}
			}
			
			int popIndex = b.tryPop(cfg, timeTick);
			if(popIndex > 0)
				gs.getMapService().syncWorldBossPop(cfg.base.mapID, BOSS_CREATE_WORLD_LINE, bossID, popIndex);
			
			if(timeTick > (b.bossState.refreshTime + b.standbyTime) && b.bossState.curHp > 0)
			{
				b.bossState.curHp = 0;
				gs.getMapService().syncDestroyWorldMapBoss(cfg.base.mapID, BOSS_CREATE_WORLD_LINE, bossID);
				this.delBoss(bossID, cfg.base.mapID, BOSS_CREATE_WORLD_LINE);
			}
		}
		
		if(refreshed != null)
		{
			gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_BOSS_REFRESH, refreshed.toString());
			gs.getLogger().debug("roll notice boss refresh " + refreshed.toString());
		}
	}
	
	private void tryCreateSuperMonster(int timeTick)
	{
		final Map<Integer, SBean.WorldMonsterCFGS> allSuperMonsters = GameData.getInstance().getAllWorldSuperMonsters();
		int weekDay = GameTime.getWeekdayByOffset(timeTick, GameData.GAME_DAY_REFRESH_TIME * 3600);
		boolean update = false;
		for (SBean.WorldMonsterCFGS cfg : allSuperMonsters.values())
		{
			if (!cfg.openDay.contains(weekDay))
				continue;

			List<Integer> refreshTime = cfg.refreshTime;
			int lastRefreshTime = GameData.getLastRefreshTime(timeTick, refreshTime);
			if (timeTick > lastRefreshTime && lastRefreshTime > this.lastCreateSuperMonsterTime)
			{
				int standTime = lastRefreshTime + cfg.standbyTime - timeTick;
				if(standTime > 0)
				{
					gs.getMapService().syncCreateSuperMonster(cfg.mapID, cfg.id, standTime);
					if(cfg.rollNoticeType > 0)
					{
						gs.getLoginManager().roleAddRollNotice(cfg.rollNoticeType, "");
						gs.getLogger().debug("roll notice create super monster " + cfg.id);
					}
				}
				update = true;
			}
		}

		if (update)
			this.lastCreateSuperMonsterTime = timeTick;
	}
	
	private void tryCreateMineral(int timeTick)
	{
		int weekDay = GameTime.getWeekdayByOffset(timeTick, GameData.GAME_DAY_REFRESH_TIME * 3600);
		boolean update = false;
		
		for (SBean.WorldMineralCFGS cfg : GameData.getInstance().getAllWorldMinerals())
		{
			if (!cfg.openDay.contains(weekDay))
				continue;
			
			List<Integer> refreshTime = cfg.refreshTime;
			int lastRefreshTime = GameData.getLastRefreshTime(timeTick, refreshTime);
			if (timeTick > lastRefreshTime && lastRefreshTime > this.lastCreateMineralTime)
			{
				int standTime = lastRefreshTime + cfg.standbyTime - timeTick;
				if(standTime > 0)
					gs.getMapService().syncCreateWorldMineral(cfg.mapID, cfg.id, standTime);
				
				update = true;
			}
		}
		
		if (update)
			this.lastCreateMineralTime = timeTick;
	}
	
	private synchronized void dayRefresh(int timeTick)
	{
		int nowday = GameData.getDayByRefreshTimeOffset(timeTick);
		if(nowday != this.lastDayRefresh)
		{
			for(Boss boss: this.bosses.values())
				boss.bossState.records.clear();
			
			this.lastDayRefresh = nowday;
		}
	}
	
	public synchronized Map<Integer, SBean.BossState> syncAllBossesInfo()
	{
		Map<Integer, SBean.BossState> all = new TreeMap<>();
		for(Boss b: this.bosses.values())
		{
			int curState = this.getBossState(b.id);
			String killerName = b.bossState.killerName;
			all.put(b.id, new SBean.BossState(curState, killerName));
		}
		return all;
	}
	
	public synchronized SBean.BossRecord getBossRewardInfo(int bossID, boolean last)
	{
		Boss boss = this.bosses.get(bossID);
		if(boss == null)
			return null;
		
		SBean.WorldBossCFGS bossCfg = GameData.getInstance().getWorldBossCFGS(bossID);
		if(bossCfg == null)
			return null;
		
		int curSeq = getBossRefreshSeq(bossCfg.base.refreshTime, GameTime.getTime());
		return boss.bossState.records.get((last ? curSeq - 1 : curSeq));
	}
	
	private int getBossState(int bossID)
	{
		int now = GameTime.getTime();
		Boss boss = this.bosses.get(bossID);
		if(boss == null || now >= boss.bossState.refreshTime + boss.standbyTime)
			return BOSS_STATE_NOREFRESH;
		
		SBean.DBBossState state = boss.bossState;
		if(state.refreshTime > now)
			return BOSS_STATE_GOTOREFRESH;
		
		if(state.curHp == boss.maxHp)
			return BOSS_STATE_ALREADYREFRESH;
		
		if(state.curHp <= 0)
			return BOSS_STATE_DEAD;
			
		return BOSS_STATE_FIGHTING;
	}
	
	private boolean checkBossDead(int bossID)
	{
		int state = this.getBossState(bossID);
		return state == BOSS_STATE_DEAD;
	}
	
	public synchronized int walkToBoss(Role role, int bossID)
	{
//		if(this.checkBossDead(bossID))
//			return BOSS_STATE_DEAD;
		
		if(!role.checkCanWalkToBoss(bossID))
			return GameData.PROTOCOL_OP_FAILED;
		
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized int transToBoss(Role role, int bossID, int seq)
	{
//		if(this.checkBossDead(bossID))
//			return BOSS_STATE_DEAD;
		
		if(!role.checkCanTransToBoss(bossID, seq))
			return GameData.PROTOCOL_OP_FAILED;
		
		gs.getTLogger().logBossTaskFlow(role, bossID, TLog.BOSSEVENT_TRANSTO, 0, 0);
		
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized void syncBossProgress(int bossID, int hp, int killerID, String killerName)
	{
		Boss boss = this.bosses.get(bossID);
		if(boss == null)
		{
			gs.getLogger().warn("@@@@@@@@@@ world boss " + bossID + " be killed by role " + killerID + " , " + killerName + " not found");
			return;
		}
		if(killerID > 0)
		{
			Role role = gs.getLoginManager().getOnGameRole(killerID);
			if (role != null)
				role.tryLogBossScheduleData();
		}
		boss.bossState.curHp = hp;
		if(hp <= 0)
		{
			gs.getLogger().info("@@@@@@@@@@ world boss " + bossID + " be killed by role " + killerID + " , " + killerName);
			boss.bossState.killerName = killerName;
		}
	}
	
	//from map
	public synchronized void syncBossRecord(int bossID, SBean.BossRecord record)
	{
		Boss boss = this.bosses.get(bossID);
		if(boss == null)
			return;
		
		SBean.WorldBossCFGS bossCfg = GameData.getInstance().getWorldBossCFGS(bossID);
		if(bossCfg == null)
			return;
		
		int seq = getBossRefreshSeq(bossCfg.base.refreshTime, GameTime.getTime());
		boss.bossState.records.put(seq, record);
		this.delBoss(bossID, bossCfg.base.mapID, BOSS_CREATE_WORLD_LINE);
		
		if(record.killer.damage.roleID > 0)
		{
			gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_BOSS_KILLED, bossID + "|" + record.killer.damage.roleName);
			gs.getLogger().debug("roll notice boss killed " + bossID + "|" + record.killer.damage.roleName);
		}
	}
	
    public void syncBossDamage(final int bossID,final int killer, Map<Integer, Integer> damageRoles)
    {
		Boss boss = this.bosses.get(bossID);
		if(boss == null)
			return;
		
		SBean.WorldBossCFGS bossCfg = GameData.getInstance().getWorldBossCFGS(bossID);
		if(bossCfg == null)
			return;
		
		for (Map.Entry<Integer, Integer> entry : damageRoles.entrySet())
		{
		    final int roleId = entry.getKey()==null?0:entry.getKey();
		    final int damage = entry.getValue()==null?0:entry.getValue();
		    
		    Role role = gs.getLoginManager().getOnGameRole(roleId);
		    if (role!=null)
		        role.onBossDamageSettlement(bossID, damage, killer==roleId);
		}
    }
	
	private int getBossRefreshSeq(List<Integer> refreshTime, int now)
	{
		int s = GameTime.getSecondOfDay(now);
		for(int i=0; i<refreshTime.size(); i++)
		{
			if(s < refreshTime.get(i) + BOSS_REFRESH_FORWARDTIME)
				return i;
		}
		
		return refreshTime.size();
	}
	
	public void mapStartInitBoss(int sid, Set<Integer> maps)
	{
		SBean.DBBoss dbBoss = getLiveDBBoss(maps);
		if(!dbBoss.bosses.isEmpty())
		{
			StringBuilder refreshed = new StringBuilder();
			for(int bossID: dbBoss.bosses.keySet())
			{
				refreshed.append(bossID);
				refreshed.append("|");
			}
			
			gs.getRPCManager().notifyMapInitWorldBoss(sid, dbBoss);
			gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_BOSS_REFRESH, refreshed.toString());
			gs.getLogger().debug("roll notice add boss refresh " + refreshed.toString());
		}
	}
	
	public synchronized void mapStopWork(int mapID)
	{
		WorldMapCluster wmc = this.worldMapClusters.get(mapID);
		if(wmc == null)
			return;
		
		wmc.clearEntities();
	}
	
	//------------------------------------------------------------------------------
	public synchronized void syncSuperMonster(SBean.ActivityEntity monster, boolean add)
	{
		if(add)
			this.addSuperMonster(monster);
		else
			this.delSuperMonster(monster.id, monster.mapID, monster.mapLine);
	}
	
	public synchronized void syncWorldMineral(SBean.ActivityEntity mineral, boolean add)
	{
		if(add)
			this.addMineral(mineral);
		else
			this.delMineral(mineral.id, mineral.mapID, mineral.mapLine);
	}
	
	public class WorldMapCluster
	{
		Map<Integer, WorldMap> maps;	// <mapInstance, WorldMap>
		
		int mapID;
		WorldMapCluster()
		{
			maps = new HashMap<>();
		}
		
		WorldMapCluster init(final SBean.WorldMapCFGS wmCfg, int count)
		{
			this.mapID = wmCfg.id;
			for(int i = 1; i <= count; i++)
				this.maps.put(i, new WorldMap());
			
			if(wmCfg.pkType == GameData.MAP_PKTYPE_NORMAL)
				this.maps.put(0, new WorldMap());
			
			return this;
		}
		
		WorldMap getMap(int mapInstance)
		{
			return maps.get(mapInstance);
		}
		
		void clearEntities()
		{
			for(WorldMap map: this.maps.values())
				map.clearEntities();
		}
		
		void onWorldLineNumChange(int num, int extralWorldNum)
		{
			for(int i = 1; i <= (num + extralWorldNum); i++)
			{
				if(!this.maps.containsKey(i))
				{
					gs.getLogger().info("boss manager world " + mapID + " add line " + i);
					this.maps.put(i, new WorldMap());
				}
			}
		}
	}
	
	public class WorldMap
	{
		Map<Integer, SBean.ActivityEntity> bosses;
		Map<Integer, SBean.ActivityEntity> superMonters;
		Map<Integer, SBean.ActivityEntity> minerals;
		
		WorldMap()
		{
			bosses = new HashMap<>();
			superMonters = new HashMap<>();
			minerals = new HashMap<>();
		}
		
		void clearEntities()
		{
			this.superMonters.clear();
			this.minerals.clear();
		}
		
		void addBoss(SBean.ActivityEntity boss)
		{
			this.bosses.put(boss.id, boss);
		}
		
		void delBoss(int bossID)
		{
			this.bosses.remove(bossID);
		}
		
		void addSuperMonster(SBean.ActivityEntity monster)
		{
			this.superMonters.put(monster.id, monster);
		}
		
		void delSuperMonster(int monsterID)
		{
			this.superMonters.remove(monsterID);
		}
		
		void addMineral(SBean.ActivityEntity mineral)
		{
			this.minerals.put(mineral.id, mineral);
		}
		
		void delMineral(int mineralID)
		{
			this.minerals.remove(mineralID);
		}
	}
	
	private WorldMap getMap(int mapID, int mapInstance)
	{
		WorldMapCluster wmc = this.worldMapClusters.get(mapID);
		if(wmc == null)
			return null;
		
		return wmc.getMap(mapInstance);
	}
	
	private void addBoss(SBean.ActivityEntity boss)
	{
		WorldMap wm = this.getMap(boss.mapID, boss.mapLine);
		if(wm == null)
			return;
		
		wm.addBoss(boss);
	}
	
	private void delBoss(int bossID, int mapID, int mapInstance)
	{
		WorldMap wm = this.getMap(mapID, mapInstance);
		if(wm == null)
			return;
		
		wm.delBoss(bossID);
	}
	
	private void addSuperMonster(SBean.ActivityEntity monster)
	{
		WorldMap wm = this.getMap(monster.mapID, monster.mapLine);
		if(wm == null)
			return;
		
		wm.addSuperMonster(monster);
	}
	
	private void delSuperMonster(int monsterID, int mapID, int mapInstance)
	{
		WorldMap wm = this.getMap(mapID, mapInstance);
		if(wm == null)
			return;
		
		wm.delSuperMonster(monsterID);
	}
	
	private void addMineral(SBean.ActivityEntity mineral)
	{
		WorldMap wm = this.getMap(mineral.mapID, mineral.mapLine);
		if(wm == null)
			return;
		
		wm.addMineral(mineral);
	}
	
	private void delMineral(int mineralID, int mapID, int mapInstance)
	{
		WorldMap wm = this.getMap(mapID, mapInstance);
		if(wm == null)
			return;
		
		wm.delMineral(mineralID);
	}
	
	public synchronized int getInsightList(Role role ,int mapId)
	{
		int result = role.checkWeaponInsight();
		if(result<=0)
			return result;
		List<SBean.ActivityEntity> list = new ArrayList<SBean.ActivityEntity>();
		WorldMapCluster thismap = this.worldMapClusters.get(mapId);
		for (WorldMap mapline : thismap.maps.values())
		{
			list.addAll(mapline.bosses.values());
		}
		for (WorldMap mapline : thismap.maps.values())
		{
			list.addAll(mapline.superMonters.values());
		}
		for (WorldMap mapline : thismap.maps.values())
		{
			list.addAll(mapline.minerals.values());
		}
		return role.setInsightList(Stream.clone(list));
	}
	//------------------------------------------------------------------------------
	
	int lastSaveTime;
	GameServer gs;
	Map<Integer, Boss> bosses = new TreeMap<>();
	int lastDayRefresh;
	private int lastCreateSuperMonsterTime;
	private int lastCreateMineralTime;
	private Map<Integer, WorldMapCluster> worldMapClusters = new HashMap<>();	//<mapID, WorldMapCluster>
}
