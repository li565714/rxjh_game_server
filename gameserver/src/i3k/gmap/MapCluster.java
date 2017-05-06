package i3k.gmap;

import i3k.SBean;
import i3k.SBean.Location;
import i3k.gmap.BaseMap.*;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapCluster
{

	private static final int GRID_UNIT_SIZE_SMALL = 500;
	private static final int GRID_UNIT_SIZE_BIG = 10000;
	SBean.MapClusterCFGS mapClusterCfg;

	MapCluster(int id, MapServer ms)
	{
		this.id = id;
		this.ms = ms;
	}

	MapCluster createNew()
	{
		SBean.MapClusterCFGS cfg = GameData.getInstance().getMapClusterCFGS(this.id);
		if (cfg == null)
			return null;

		this.mapClusterCfg = cfg;
		return this;
	}

	PrivateMap createPrivateMap(int mapInstanceID)
	{
		PrivateMap map = new PrivateMap(this.mapClusterCfg, mapInstanceID, ms).start();
		if (map != null)
			maps.put(map.getInstanceID(), map);
		return map;
	}

	TeamMapCopy createTeamMap(int mapInstanceID)
	{
		SBean.MapCopyCFGS mapcopyCfg = GameData.getInstance().getMapCopyCFGS(this.mapClusterCfg.id);
		if(mapcopyCfg == null)
			return null;
		
		TeamMapCopy map = new TeamMapCopy(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL, mapcopyCfg.winCondition, mapcopyCfg.bosses).start();
		if (map != null)
			maps.put(map.getInstanceID(), map);
		return map;
	}

	ActivityMapCopy createActivityMap(int mapInstanceID)
	{
		SBean.ActivityMapCFGS acCfg = GameData.getInstance().getActivityMapCFGS(this.mapClusterCfg.id);
		if(acCfg == null)
			return null;
		
		ActivityMapCopy map = new ActivityMapCopy(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL, acCfg.winCondition, acCfg.bosses).start(acCfg.maxTime);
		maps.put(map.getInstanceID(), map);
		return map;
	}

	WeaponMapCopy createWeaponMap(int mapInstanceID)
	{
		SBean.WeaponMapCFGS wmCfg = GameData.getInstance().getWeaponMapCFGS(this.mapClusterCfg.id);
		if(wmCfg == null)
			return null;
		
		WeaponMapCopy map = new WeaponMapCopy(mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL, wmCfg.winCondition, wmCfg.bosses).start(wmCfg.maxTime);
		maps.put(map.getInstanceID(), map);
		return map;
	}

	JusticeMapCopy createJusticeMap(int mapInstanceID)
	{
		SBean.JusticeMapCopyCFGS wmCfg = GameData.getInstance().getJusticeMapCopyCFGS(this.mapClusterCfg.id);
		if(wmCfg == null)
			return null;
		
		JusticeMapCopy map = new JusticeMapCopy(mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL, wmCfg.winCondition, new HashMap<>()).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createWorldMap(int mapInstance)
	{
		PublicMap map = new WorldMap(this.mapClusterCfg, mapInstance, ms, GRID_UNIT_SIZE_SMALL).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}

	PublicMap createSectMap(int mapInstanceID)
	{
		PublicMap map = new SectMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL).start();
		if (map != null)
			maps.put(map.getInstanceID(), map);
		return map;
	}

	PublicMap createArenaMap(int mapInstanceID)
	{
		PublicMap map = new ArenaMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_BIG).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}

	PublicMap createSuperArenaMap(int mapID, int mapInstanceID)
	{
		SBean.SuperArenaMapCFGS mapCfg = GameData.getInstance().getSuperArenaMapCFGS(mapID);
		if(mapCfg == null)
			return null;
		
		SBean.SuperArenaTypeCFGS typeCfg = GameData.getInstance().getSuperArenaTypeCFG(mapCfg.type);
		if(typeCfg == null)
			return null;
		
		PublicMap map = null;
		switch (typeCfg.type)
		{
		case GameData.MAPCOPY_SUPERARENA_NORMAL:
			map = new SuperArenaNormalMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_BIG, typeCfg).start();
			break;
		case GameData.MAPCOPY_SUPERARENA_THREEBEST:
			map = new SuperArenaThreeBestMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_BIG, typeCfg).start();
			break;
		default:
			break;
		}
		if(map == null)
			return null;
		
		maps.put(map.getInstanceID(), map);
		return map;
	}

	PublicMap createBWArenaMap(int mapInstanceID)
	{
		PublicMap map = new BWArenaMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_BIG).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createForceWarMap(int mapInstanceID)
	{
		PublicMap map = new ForceWarMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}

	PublicMap createSectGroupMap(int mapInstanceID)
	{
		PublicMap map = new SectGroupMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL).start();
		if (map != null)
			maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createPetLifeMap(int mapInstanceID)
	{
		PublicMap map = new PetLifeMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createClimbTowerMap(int mapInstanceID)
	{
		SBean.ClimbTowerMapCFGS ctCfg = GameData.getInstance().getClimbTowerMapCFGS(this.mapClusterCfg.id);
		if(ctCfg == null)
			return null;
		
		PublicMap map = new ClimbTowerMap(this.mapClusterCfg, mapInstanceID, ms, GRID_UNIT_SIZE_SMALL, ctCfg.winCondition, ctCfg.winCondParam, ctCfg.bosses).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createDemonHoleMap(int mapInstance)
	{
		SBean.DemonHoleMapCFGS dhCfg = GameData.getInstance().getDemonHoleMapCFGS(this.mapClusterCfg.id);
		if(dhCfg == null)
			return null;
		
		PublicMap map = new DemonHoleMap(this.mapClusterCfg, mapInstance, ms, GRID_UNIT_SIZE_SMALL).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createEmergencyMap(int mapInstance)
	{
		PublicMap map = new EmergencyMap(this.mapClusterCfg, mapInstance, ms, GRID_UNIT_SIZE_SMALL).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createFightNpcMap(int mapInstance)
	{
		SBean.FightNpcMapCFGS mapCfg = GameData.getInstance().getFightNpcMapCFGS(this.mapClusterCfg.id);
		if(mapCfg == null)
			return null;
		
		PublicMap map = new FightNpcMapCopy(mapClusterCfg, mapInstance, ms, GRID_UNIT_SIZE_SMALL, mapCfg.winCondition, mapCfg.bosses).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	PublicMap createTowerDefenceMap(int mapInstance)
	{
		SBean.TowerDefenceMapCFGS mapCfg = GameData.getInstance().getTowerDefenceMapCFGS(this.mapClusterCfg.id);
		if(mapCfg == null)
			return null;
		
		PublicMap map = new TowerDefenceMap(mapClusterCfg, mapInstance, ms, GRID_UNIT_SIZE_SMALL).start();
		maps.put(map.getInstanceID(), map);
		return map;
	}
	
	void destoryMap(int mapInstanceID, boolean delay)
	{
//		if(delay)
//		{
//			BaseMap map = maps.get(mapInstanceID);
//			map.setDelayDestroy();
//		}
//		else
//		{
//			BaseMap map = maps.remove(mapInstanceID);
//			if (map != null)
//				this.destoryMapImp(map);
//		}
		
		BaseMap map = maps.remove(mapInstanceID);
		if (map != null)
			this.destoryMapImp(map);
	}
	
	void destoryMapImp(BaseMap map)
	{
		if(map.mapRoles.size() > 0)
		{
			int count = 0;
			for (MapRole r : map.mapRoles.values())
			{
				if(r.robot)
					continue;
				
				if (r.id > 0)
					count++;
				
				ms.getMapManager().delMapRole(r.id);
			}
			if (count > 0)
				ms.getLogger().warn("destory map " + map.mapID + " , " + map.mapInstanceID + " map role count = " + map.mapRoles.size());
		}
	}
	
	BaseMap getMap(int mapInstanceID)
	{
		return maps.get(mapInstanceID);
	}

	boolean isEmpty()
	{
		return maps.isEmpty();
	}

	int getID()
	{
		return id;
	}

	void onTimer(long timeMillis, int timeTick, long logicTime)
	{
		Iterator<BaseMap> it = maps.values().iterator();
		while (it.hasNext())
		{
			BaseMap m = it.next();
			m.onTimer(timeMillis, timeTick, logicTime);
//			if(m.checkDestroy(timeMillis, timeTick, logicTime))
//			{
//				this.destoryMapImp(m);
//				it.remove();
//			}
		}
	}

	void clearAllMaps()
	{
		maps.values().forEach(BaseMap::clearAllRoles);
		maps.clear();
		mapSteles.clear();
	}

	void syncWorldNum(int worldNum, int extralWorldNum)
	{
		for(int i = maps.size(); i < (worldNum + extralWorldNum); i++)
			this.createWorldMap(i + 1);
		
		//×¨Êô·ÖÏß
		if(!maps.containsKey(0))
		{
			SBean.WorldMapCFGS wmCfg = GameData.getInstance().getWorldMapCFGS(id);
			if(wmCfg != null && wmCfg.pkType == GameData.MAP_PKTYPE_NORMAL)
			{
				this.createWorldMap(0);
				ms.getLogger().info("create wolrd map " + id + " special map");
			}
		}
	}
	
	void createWorldSuperMonster(SBean.WorldMonsterCFGS cfg, int standTime)
	{
		for(BaseMap m: this.maps.values())
		{
			int seq = GameRandom.getRandInt(1, cfg.refreshPos.size() + 1);
			m.createWorldSuperMonster(cfg.id, seq, standTime);
			
			ms.getRPCManager().syncSuperMonster(new SBean.ActivityEntity(cfg.id, GameData.ACTIVITY_ENTITY_TYPE_LITTLEBOSS, m.getMapID(), m.getInstanceID(), seq), true);
		}
	}
	
	void createWorldMineral(SBean.WorldMineralCFGS cfg, int standTime)
	{
		for(BaseMap m: this.maps.values())
		{
			int seq = GameRandom.getRandInt(1, cfg.refreshPos.size() + 1);
			m.createWorldMineral(cfg, seq, standTime);
			
			ms.getRPCManager().syncWorldMineral(new SBean.ActivityEntity(cfg.id, GameData.ACTIVITY_ENTITY_TYPE_BOX, m.getMapID(), m.getInstanceID(), seq), true);
		}
	}
	
	void createStele(int steleType, int index, SBean.SteleMineralCFGS mCfg, int remainTimes)
	{
		int steleKey = getSteleKey(steleType, index);
		MapStele mStele = mapSteles.get(steleKey);
		if(mStele != null)
			return;
		
		mStele = new MapStele(steleType, index, remainTimes);
		mapSteles.put(steleKey, mStele);
		for(BaseMap m: maps.values())
		{
			if(m.getInstanceID() <= 0)
				continue;
			
			if(m instanceof WorldMap)
			{
				WorldMap wm = WorldMap.class.cast(m);
				SteleMineral steleMine = wm.createSteleMineral(steleKey, mCfg, mStele);
				mStele.addMineral(steleMine.getID());
			}
		}
	}
	
	void destroyStele(int steleType, int index)
	{
		int steleKey = getSteleKey(steleType, index);
		MapStele mStele = mapSteles.remove(steleKey);
		if(mStele == null)
			return;
		
		mStele.destroyStele();
	}
	
	class MapStele
	{
		private final int steleType;
		private final int index;
		private Set<Integer> mineInstanceIDs;
		private int remainTimes;
		
		MapStele(int steleType, int index, int remainTimes)
		{
			this.steleType = steleType;
			this.index = index;
			this.remainTimes = remainTimes;
			this.mineInstanceIDs = new HashSet<>();
		}
		
		public int getSteleType()
		{
			return steleType;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		void addMineral(int mineInstanceID)
		{
			this.mineInstanceIDs.add(mineInstanceID);
		}
		
		boolean canMineral()
		{
			return remainTimes > 0;
		}
		
		int getRemainTimes()
		{
			return remainTimes;
		}
		
		void reduceRemainTimes()
		{
			if(remainTimes <= 0)
				return;
			
			remainTimes--;
			if(remainTimes <= 0)
				onSteleNone();
			
			ms.getRPCManager().notifyGSSyncSteleRemainTimes(steleType, index, remainTimes);
		}
		
		private void onSteleNone()
		{
			for(int mineInstanceID: mineInstanceIDs)
			{
				for(BaseMap map: MapCluster.this.maps.values())
				{
					Mineral m = map.getMineral(mineInstanceID);
					if(m != null)
						m.onStateNone();
				}
			}
		}
		
		void destroyStele()
		{
			for(int mineInstanceID: mineInstanceIDs)
			{
				for(BaseMap map: MapCluster.this.maps.values())
					map.onMineralTimeOut(mineInstanceID);
			}
			
			mineInstanceIDs.clear();
		}
	}
	
	public static int getSteleKey(int steleType, int index)
	{
		return steleType * 100_000 + index;
	}

	public void addJusticeNpc(Location location)
	{
		for (BaseMap map : maps.values())
		{
			Npc npc = new Npc(GameData.getInstance().getJusticeMapCFGS().npcId, this.ms).createNew(new GVector3(location.position.x, location.position.y, location.position.z));
			npc.curRotation = new GVector3(location.rotation);
			map.addNpc(npc);
		}
	}

	public void delJusticeNpc()
	{
		for (BaseMap map : maps.values())
		{
			if (map instanceof WorldMap)
			{
				WorldMap wm = WorldMap.class.cast(map);
				wm.delAllNpc();
			}
		}
	}
	
	private MapServer ms;
	private int id;
	private ConcurrentMap<Integer, BaseMap> maps = new ConcurrentHashMap<>(); //<mapID,BaseMap>
	private Map<Integer, MapStele> mapSteles = new HashMap<>();

}
