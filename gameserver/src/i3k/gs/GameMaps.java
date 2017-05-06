package i3k.gs;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.TreeSet;

public class GameMaps
{

	GameMaps(int session, int id, GameServer ms)
	{
		this.session = session;
		this.msid = id;
		this.gs = ms;
	}

	int getID()
	{
		return this.msid;
	}

	void init(Set<Integer> maps)
	{
		for (int mapId : maps)
		{
			try
			{
				this.allMaps.put(mapId, createMapCluster(mapId));
			}
			catch (GameData.MapException e)
			{
				gs.getLogger().warn("!!!!! map server init map " + mapId + " cause exception : " + e.getMessage());
			}
		}
	}

	void fini()
	{
		this.allMaps.values().forEach(MapCluster::close);
		this.allMaps.clear();
	}

	void reset()
	{
		this.allMaps.values().forEach(MapCluster::reset);
	}
	
	public MapCluster createMapCluster(int mapId) throws GameData.MapException
	{
		int mapType = GameData.getInstance().checkMapValid(mapId);
		switch (mapType)
		{
		case GameData.MAP_TYPE_MAP_WORLD:
			return new WorldMapCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_NORMAL:
			return new NormalMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_SECT:
			return new SectMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_ARENA:
			return new ArenaMapCopyCluster(mapId);
//		case GameData.MAP_TYPE_MAPCOPY_CLAN_ORE:
//			return new ClanOreMapCopyCluster(mapId);
//		case GameData.MAP_TYPE_MAPCOPY_CLAN_TASK:
//			return new ClanTaskMapCopyCluster(mapId);
//		case GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLE:
//			return new ClanBattleMapCopyCluster(mapId);
//		case GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLEHELP:
//			return new ClanBattleHelpMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_ACTIVITY:
			return new ActivityMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_SUPERARENA:
			return new SuperArenaMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_BWARENA:
			return new BWArenaMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_PETLIFE:
			return new PetLifeMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_CLIMBTOWER:
			return new ClimbTowerMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
			return new ForceWarMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_SECT_GROUP:
			return new SectGroupMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_WEAPON:
			return new WeaponMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE:
			return new DemonHoleMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_JUSTICE:
			return new JusticeMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_EMERGENCY:
			return new EmergencyMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_FIGHTNPC:
			return new FightNpcMapCopyCluster(mapId);
		case GameData.MAP_TYPE_MAPCOPY_TOWER_DEFENCE:
			return new TowerDefenceMapCopyCluster(mapId);
		default:
			throw new GameData.MapException("not support map type " + mapType + " cluster create !!!");
		}
	}

	Collection<Integer> getAllMaps()
	{
		return this.allMaps.keySet();
	}

	Collection<Integer> getWorldMapRoles(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc != null)
		{
			if (mcc instanceof WorldMapCluster)
			{
				WorldMapCluster wmc = WorldMapCluster.class.cast(mcc);
				return wmc.getAllMapRoles(mapInstance);
			}
		}
		return GameData.emptyList();
	}

	Collection<Integer> getSectGroupMapRoles(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc != null)
		{
			if (mcc instanceof SectGroupMapCopyCluster)
			{
				SectGroupMapCopyCluster sgmcc = SectGroupMapCopyCluster.class.cast(mcc);
				return sgmcc.getAllMapRoles(mapInstance);
			}
		}
		return GameData.emptyList();
	}

	void onTimer(int timeTick)
	{
		Iterator<Map.Entry<Integer, MapCluster>> it = allMaps.entrySet().iterator();
		while (it.hasNext())
		{
			MapCluster mcc = it.next().getValue();
			mcc.onTimer(timeTick);
		}
	}

	int getMinWorld(int mapID, int roleID, int line, int lastLine)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc instanceof WorldMapCluster)
		{
			WorldMapCluster wmcc = WorldMapCluster.class.cast(mcc);
			return wmcc.getMinWorld(roleID, line, lastLine);
		}
		
		return -1;
	}
	
	int getWorldLineNum(int mapID)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc instanceof WorldMapCluster)
		{
			WorldMapCluster wmcc = WorldMapCluster.class.cast(mcc);
			return wmcc.getLineNum();
		}
		
		return -1;
	}
	
	boolean isWorldMapFull(int mapID, int line)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc instanceof WorldMapCluster)
		{
			WorldMapCluster wmcc = WorldMapCluster.class.cast(mcc);
			return wmcc.isWorldMapFull(line);
		}
		
		return false;
	}
	
	void onWorldLineNumChange(int num, Map<Integer, Integer> extralWorldNum)
	{
		for(MapCluster mcc: this.allMaps.values())
		{
			if(mcc instanceof WorldMapCluster)
			{
				WorldMapCluster wmcc = WorldMapCluster.class.cast(mcc);
				wmcc.onWorldCntChange(num, extralWorldNum.getOrDefault(wmcc.mapId, 0));
			}
		}
		
		gs.getMapService().syncMapWorldNum(num, extralWorldNum);
	}
	
	boolean roleEnterMap(int rid, int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		return mcc != null && mcc.roleEnterMap(rid, mapInstance);
	}

	void roleLeaveMap(int rid, int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		mcc.roleLeaveMap(rid, mapInstance);
	}

	void syncMapCopyReady(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof MapCopyCluser)
		{
			MapCopyCluser bmcc = MapCopyCluser.class.cast(mcc);
			bmcc.syncMapCopyReady(mapInstance);
		}
	}
	
	int createPetLifeMapCopy(int mapID)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return 0;
		
		if(mcc instanceof PetLifeMapCopyCluster)
		{
			PetLifeMapCopyCluster plmcc = PetLifeMapCopyCluster.class.cast(mcc);
			return plmcc.createMap();
		}
		
		return 0;
	}
	
	int createNormalMapCopy(int mapId)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return 0;
		if (mcc instanceof NormalMapCopyCluster)
		{
			NormalMapCopyCluster nmcc = NormalMapCopyCluster.class.cast(mcc);
			return nmcc.createMap();
		}
		return 0;
	}

	int createActivityMapCopy(int mapId)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return 0;
		if (mcc instanceof ActivityMapCopyCluster)
		{
			ActivityMapCopyCluster amcc = ActivityMapCopyCluster.class.cast(mcc);
			return amcc.createMap();
		}
		return 0;
	}
	
	int createWeaponMapCopy(int mapID)
	{
		MapCluster mc = this.allMaps.get(mapID);
		if (mc == null)
			return 0;
		
		if(mc instanceof WeaponMapCopyCluster)
		{
			WeaponMapCopyCluster wmcc = WeaponMapCopyCluster.class.cast(mc);
			return wmcc.createMap(); 
		}
		return 0;
	}
	
	int createJusticeMapCopy(int mapID)
	{
		MapCluster mc = this.allMaps.get(mapID);
		if (mc == null)
			return 0;
		
		if(mc instanceof JusticeMapCopyCluster)
		{
			JusticeMapCopyCluster jmcc = JusticeMapCopyCluster.class.cast(mc);
			return jmcc.createMap(); 
		}
		return 0;
	}
	
	int createClimbTowerMapCopy(int mapId)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return 0;
		if (mcc instanceof ClimbTowerMapCopyCluster)
		{
			ClimbTowerMapCopyCluster amcc = ClimbTowerMapCopyCluster.class.cast(mcc);
			return amcc.createMap();
		}
		return 0;
	}

	int createSectMapCopy(int mapId, int sectId, Map<Integer, Integer> progress)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return 0;
		if (mcc instanceof SectMapCopyCluster)
		{
			SectMapCopyCluster smcc = SectMapCopyCluster.class.cast(mcc);
			return smcc.createMap(sectId, progress);
		}
		return 0;
	}

	int createSectGroupMapCopy(int mapId, int sectId, int startTime, Map<Integer, Integer> process, Map<Integer, Integer> killNum, Map<Integer, SBean.RoleDamageDetail> damageRank)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return 0;
		if (mcc instanceof SectGroupMapCopyCluster)
		{
			SectGroupMapCopyCluster smcc = SectGroupMapCopyCluster.class.cast(mcc);
			return smcc.createMap(sectId, startTime, process, killNum, damageRank);
		}
		return 0;
	}

	int createEmergencyMapCopy(int mapId, int endTime)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return 0;
		if (mcc instanceof EmergencyMapCopyCluster)
		{
			EmergencyMapCopyCluster smcc = EmergencyMapCopyCluster.class.cast(mcc);
			return smcc.createMap(endTime);
		}
		return 0;
	}

	int createArenaMapCopy(int mapId, SBean.BattleArray enemy)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return 0;
		if (mcc instanceof ArenaMapCopyCluster)
		{
			ArenaMapCopyCluster amcc = ArenaMapCopyCluster.class.cast(mcc);
			return amcc.createMap(enemy);
		}
		return 0;
	}

	int createSuperArenaMapCopy(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return 0;

		if (mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			return samcc.createMap(mapInstance);
		}

		return 0;
	}
	
	int createBWArenaMapCopy(int mapID, SBean.BattleArray enemy, boolean petLack, boolean sameBWArenaLvl)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return 0;

		if (mcc instanceof BWArenaMapCopyCluster)
		{
			BWArenaMapCopyCluster bamcc = BWArenaMapCopyCluster.class.cast(mcc);
			return bamcc.createMap(enemy, petLack, sameBWArenaLvl);
		}

		return 0;
	}
	
	int createFightNpcMapCopy(int mapID)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return 0;
		
		if(mcc instanceof FightNpcMapCopyCluster)
		{
			FightNpcMapCopyCluster fnmcc = FightNpcMapCopyCluster.class.cast(mcc);
			return fnmcc.createMap();
		}
		return 0;
	}
	
	int createTowerDefence(int mapID)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return 0;
		
		if(mcc instanceof TowerDefenceMapCopyCluster)
		{
			TowerDefenceMapCopyCluster tdmcc = TowerDefenceMapCopyCluster.class.cast(mcc);
			return tdmcc.createMap();
		}
		return 0;
	}
	
	int createForceWarMapCopy(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return 0;
		
		if(mcc instanceof ForceWarMapCopyCluster)
		{
			ForceWarMapCopyCluster fwmcc = ForceWarMapCopyCluster.class.cast(mcc);
			return fwmcc.createMap(mapInstance);
		}
		
		return 0;
	}
	
	int createDemonHoleMapCopy(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return 0;
		
		if(mcc instanceof DemonHoleMapCopyCluster)
		{
			DemonHoleMapCopyCluster dhmcc = DemonHoleMapCopyCluster.class.cast(mcc);
			return dhmcc.createMap(mapInstance);
		}
		
		return 0;
	}
	
	int createFightMapCopy(int mapID, int mapInstance)
	{
		SBean.MapClusterCFGS mcc = GameData.getInstance().getMapClusterCFGS(mapID);
		if(mcc == null)
			return 0;
		
		switch (mcc.type)
		{
		case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
			return createForceWarMapCopy(mapID, mapInstance);
		case GameData.MAP_TYPE_MAPCOPY_SUPERARENA:
			return createSuperArenaMapCopy(mapID, mapInstance);
		case GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE:
			return createDemonHoleMapCopy(mapID, mapInstance);
		default:
			break;
		}
		
		return 0;
	}
	
//	//夺矿战
//	int createClanOreMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return 0;
//		if (mcc instanceof ClanOreMapCopyCluster)
//		{
//			ClanOreMapCopyCluster comcc = ClanOreMapCopyCluster.class.cast(mcc);
//			return comcc.createMap(enemy);
//		}
//		return 0;
//	}
//
//	//宗门战支援战
//	int createClanBattleHelpMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return 0;
//		if (mcc instanceof ClanBattleHelpMapCopyCluster)
//		{
//			ClanBattleHelpMapCopyCluster comcc = ClanBattleHelpMapCopyCluster.class.cast(mcc);
//			return comcc.createMap(enemy);
//		}
//		return 0;
//	}
//
//	//宗门战
//	int createClanBattleMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return 0;
//		if (mcc instanceof ClanBattleMapCopyCluster)
//		{
//			ClanBattleMapCopyCluster comcc = ClanBattleMapCopyCluster.class.cast(mcc);
//			return comcc.createMap(enemy);
//		}
//		return 0;
//
//	}
//
//	//遭遇战
//	int createClanTaskMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return 0;
//		if (mcc instanceof ClanTaskMapCopyCluster)
//		{
//			ClanTaskMapCopyCluster comcc = ClanTaskMapCopyCluster.class.cast(mcc);
//			return comcc.createMap(enemy);
//		}
//		return 0;
//	}

	void syncCommonMapCopyEnd(int mapId, int mapInstance, int score)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof CommonMapCopyCluster)
		{
			CommonMapCopyCluster nmcc = CommonMapCopyCluster.class.cast(mcc);
			nmcc.onEnd(mapInstance, score);
		}
	}

	void syncCommonMapCopyStart(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof CommonMapCopyCluster)
		{
			CommonMapCopyCluster nmcc = CommonMapCopyCluster.class.cast(mcc);
			nmcc.onStart(mapInstance);
		}
	}

	void syncSectMapCopyStart(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof SectMapCopyCluster)
		{
			SectMapCopyCluster smcc = SectMapCopyCluster.class.cast(mcc);
			smcc.onStart(mapInstance);
		}
	}

	void syncSectGroupMapCopyStart(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof SectGroupMapCopyCluster)
		{
			SectGroupMapCopyCluster smcc = SectGroupMapCopyCluster.class.cast(mcc);
			smcc.onStart(mapInstance);
		}
	}
	
	void syncEmergencyMapCopyStart(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof EmergencyMapCopyCluster)
		{
			EmergencyMapCopyCluster smcc = EmergencyMapCopyCluster.class.cast(mcc);
			smcc.onStart(mapInstance);
		}
	}
	
	void syncEmergencyMapCopyEnd(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof EmergencyMapCopyCluster)
		{
			EmergencyMapCopyCluster smcc = EmergencyMapCopyCluster.class.cast(mcc);
			smcc.onEnd(mapInstance);
		}
	}

	void syncSectMapCopyProgress(int mapId, int mapInstance, int spawnPoint, int hpLostBP, int damage)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof SectMapCopyCluster)
		{
			SectMapCopyCluster smcc = SectMapCopyCluster.class.cast(mcc);
			smcc.onProgressChanged(mapInstance, spawnPoint, hpLostBP, damage);
		}
	}

	void syncArenaMapCopyStart(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof ArenaMapCopyCluster)
		{
			ArenaMapCopyCluster amcc = ArenaMapCopyCluster.class.cast(mcc);
			amcc.onStart(mapInstance);
		}
	}

	void syncArenaMapCopyEnd(int mapId, int mapInstance, boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof ArenaMapCopyCluster)
		{
			ArenaMapCopyCluster amcc = ArenaMapCopyCluster.class.cast(mcc);
			amcc.onEnd(mapInstance, win, attackingSideHp, defendingSideHp);
		}
	}

	void syncSuperArenaMapCopyStart(int mapID, int mapInstance, Map<Integer, Integer> eloDiffs)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;

		if (mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			samcc.onStart(mapInstance, eloDiffs);
		}
	}

	void syncSuperArenaMapCopyEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result, int lastRewardDayOrWeek)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;

		if (mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			samcc.onEnd(mapInstance, result, lastRewardDayOrWeek);
		}
	}

	void syncSuperEnterArenaRace(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;

		if (mcc instanceof SuperArenaMapCopyCluster)
		{
			SuperArenaMapCopyCluster samcc = SuperArenaMapCopyCluster.class.cast(mcc);
			samcc.onRaceEnd(mapInstance);
		}
	}
	
	void syncBWArenaMapCopyStart(int mapId, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof BWArenaMapCopyCluster)
		{
			BWArenaMapCopyCluster bamcc = BWArenaMapCopyCluster.class.cast(mcc);
			bamcc.onStart(mapInstance);
		}
	}

	void syncBWArenaMapCopyEnd(int mapId, int mapInstance, boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof BWArenaMapCopyCluster)
		{
			BWArenaMapCopyCluster bamcc = BWArenaMapCopyCluster.class.cast(mcc);
			bamcc.onEnd(mapInstance, win, attackingSideHp, defendingSideHp);
		}
	}
	
	void syncPetLifeMapCopyStart(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;
		if (mcc instanceof PetLifeMapCopyCluster)
		{
			PetLifeMapCopyCluster plmcc = PetLifeMapCopyCluster.class.cast(mcc);
			plmcc.onStart(mapInstance);
		}
	}
	
	void syncForceWarMapCopyStart(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;
		
		if(mcc instanceof ForceWarMapCopyCluster)
		{
			ForceWarMapCopyCluster fwmcc = ForceWarMapCopyCluster.class.cast(mcc);
			fwmcc.onStart(mapInstance);
		}
	}
	
	void syncForceWarMapCopyEnd(int mapID, int mapInstance, SBean.RankClearTime rankClearTime, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;
		if (mcc instanceof ForceWarMapCopyCluster)
		{
			ForceWarMapCopyCluster fwmcc = ForceWarMapCopyCluster.class.cast(mcc);
			fwmcc.onEnd(mapInstance, rankClearTime, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
		}
	}
	
	void syncDemonHoleMapCopyEnd(int mapID, int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof DemonHoleMapCopyCluster)
		{
			DemonHoleMapCopyCluster dhmcc = DemonHoleMapCopyCluster.class.cast(mcc);
			dhmcc.onEnd(mapInstance, curFloor, total);
		}
	}
	
	void syncFightNpcMapCopyStart(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof FightNpcMapCopyCluster)
		{
			FightNpcMapCopyCluster fnmcc = FightNpcMapCopyCluster.class.cast(mcc);
			fnmcc.onStart(mapInstance);
		}
	}
	
	void syncFightNpcMapCopyEnd(int mapID, int mapInstance, boolean win)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof FightNpcMapCopyCluster)
		{
			FightNpcMapCopyCluster fnmcc = FightNpcMapCopyCluster.class.cast(mcc);
			fnmcc.onEnd(mapInstance, win);
		}
	}
	
	void syncTowerDefenceMapCopyStart(int mapID, int mapInstance)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof TowerDefenceMapCopyCluster)
		{
			TowerDefenceMapCopyCluster tdmcc = TowerDefenceMapCopyCluster.class.cast(mcc);
			tdmcc.onStart(mapInstance);
		}
	}
	
	void syncTowerDefenceMapCopyEnd(int mapID, int mapInstance, int count)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof TowerDefenceMapCopyCluster)
		{
			TowerDefenceMapCopyCluster tdmcc = TowerDefenceMapCopyCluster.class.cast(mcc);
			tdmcc.onEnd(mapInstance, count);
		}
	}
	
	void syncTowerDefenceSpawnCount(int mapID, int mapInstance, int count)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if(mcc == null)
			return;
		
		if(mcc instanceof TowerDefenceMapCopyCluster)
		{
			TowerDefenceMapCopyCluster tdmcc = TowerDefenceMapCopyCluster.class.cast(mcc);
			tdmcc.syncSpawnCount(mapInstance, count);
		}
	}
	
	void syncMapCopyTimeOut(int mapID, int mapInstance)
	{
		MapCluster mc = this.allMaps.get(mapID);
		if (mc == null)
			return;
		
		if(mc instanceof MapCopyCluser)
		{
			MapCopyCluser mcc = MapCopyCluser.class.cast(mc);
			mcc.onMapCopyTimeOut(mapInstance);
		}
	}
	
//	void syncClanOreMapCopyStart(int mapId, int mapInstance)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanOreMapCopyCluster)
//		{
//			ClanOreMapCopyCluster tmcc = ClanOreMapCopyCluster.class.cast(mcc);
//			tmcc.onStart(mapInstance);
//		}
//	}
//
//	void syncClanOreMapCopyEnd(int mapId, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanOreMapCopyCluster)
//		{
//			ClanOreMapCopyCluster tmcc = ClanOreMapCopyCluster.class.cast(mcc);
//			tmcc.onEnd(mapInstance, win == 1, attackingSideHp, defendingSideHp);
//		}
//	}
//
//	void syncClanBattleHelpMapCopyStart(int mapId, int mapInstance)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanBattleHelpMapCopyCluster)
//		{
//			ClanBattleHelpMapCopyCluster tmcc = ClanBattleHelpMapCopyCluster.class.cast(mcc);
//			tmcc.onStart(mapInstance);
//		}
//	}
//
//	void syncClanBattleHelpMapCopyEnd(int mapId, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanBattleHelpMapCopyCluster)
//		{
//			ClanBattleHelpMapCopyCluster tmcc = ClanBattleHelpMapCopyCluster.class.cast(mcc);
//			tmcc.onEnd(mapInstance, win == 1, attackingSideHp, defendingSideHp);
//		}
//	}
//
//	void syncClanBattleMapCopyStart(int mapId, int mapInstance)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanBattleMapCopyCluster)
//		{
//			ClanBattleMapCopyCluster tmcc = ClanBattleMapCopyCluster.class.cast(mcc);
//			tmcc.onStart(mapInstance);
//		}
//	}
//
//	void syncClanBattleMapCopyEnd(int mapId, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanBattleMapCopyCluster)
//		{
//			ClanBattleMapCopyCluster tmcc = ClanBattleMapCopyCluster.class.cast(mcc);
//			tmcc.onEnd(mapInstance, win == 1, attackingSideHp, defendingSideHp);
//		}
//	}
//
//	void syncClanTaskMapCopyStart(int mapId, int mapInstance)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanTaskMapCopyCluster)
//		{
//			ClanTaskMapCopyCluster tmcc = ClanTaskMapCopyCluster.class.cast(mcc);
//			tmcc.onStart(mapInstance);
//		}
//	}
//
//	void syncClanTaskMapCopyEnd(int mapId, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
//	{
//		MapCluster mcc = this.allMaps.get(mapId);
//		if (mcc == null)
//			return;
//		if (mcc instanceof ClanTaskMapCopyCluster)
//		{
//			ClanTaskMapCopyCluster tmcc = ClanTaskMapCopyCluster.class.cast(mcc);
//			tmcc.onEnd(mapInstance, win == 1, attackingSideHp, defendingSideHp);
//		}
//	}
	//

	private abstract class MapCluster
	{

		public final int mapId;

		public MapCluster(int mapId)
		{
			this.mapId = mapId;
		}

		abstract void close();
		
		abstract void reset();

		abstract boolean roleEnterMap(int rid, int mapInstance);

		abstract boolean roleLeaveMap(int rid, int mapInstance);

		abstract void onTimer(int timeTick);
	}

	private class WorldMapCluster extends MapCluster
	{
		private int lastPrintTime;
		private static final int PRINT_INFO_INTERVAL = 10;
		
//		Set<Integer> roles = new HashSet<>();
		List<Set<Integer>> maps = new ArrayList<>();
		Set<Integer> specialMap;
		
		public WorldMapCluster(int mapId)
		{
			super(mapId);
			SBean.WorldMapCFGS wmCfg = GameData.getInstance().getWorldMapCFGS(mapId);
			int worldNum = (wmCfg != null && wmCfg.worldNum > 0) ? wmCfg.worldNum : gs.getConfig().getWorldLineNum(mapId);
			for(int i = 0;i < worldNum; i++)
				maps.add(new HashSet<>());
			
			if(wmCfg != null && wmCfg.pkType == GameData.MAP_PKTYPE_NORMAL)
				specialMap = new HashSet<>();
				
		}

		//close，enter，leave, getAllMapRoles 4个函数加锁主要因为roles是非并发集合
		synchronized void close()
		{
			for(Set<Integer> roles: this.maps)
			{
				for (int rid : roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.kickFromMap();
				}
				roles.clear();
			}
			
			if(specialMap != null)
			{
				for (int rid : specialMap)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.kickFromMap();
				}
			}
			
			maps.clear();
			specialMap = null;
		}
		
		synchronized void reset()
		{
			this.close();
		}
		
		//mapInstance 0:表示专属线
		private Set<Integer> getWorldMapRoles(int mapInstance)
		{
			if(mapInstance == 0)
			{
				return specialMap;
			}
			else
			{
				if(mapInstance <= 0 || mapInstance > this.maps.size())
					return null;
				
				return this.maps.get(mapInstance - 1);
			}
		}
		
		synchronized int getLineNum()
		{
			return this.maps.size();
		}
		
		synchronized boolean roleEnterMap(int rid, int mapInstance)
		{
			Set<Integer> mapRoles = getWorldMapRoles(mapInstance);
			if(mapRoles == null)
				return false;
			
			mapRoles.add(rid);
			return true;
		}

		synchronized boolean roleLeaveMap(int rid, int mapInstance)
		{
			Set<Integer> mapRoles = getWorldMapRoles(mapInstance);
			if(mapRoles == null)
				return false;
			
			mapRoles.remove(rid);
			return true;
		}

		synchronized Collection<Integer> getAllMapRoles(int mapInstance)
		{
			Set<Integer> mapRoles = getWorldMapRoles(mapInstance);
			if(mapRoles == null)
				return GameData.emptySet();
			
			return new TreeSet<>(mapRoles);
		}

		void onTimer(int timeTick)
		{
			tryPrintWorldRoleCnt(timeTick);
		}
		
		synchronized void tryPrintWorldRoleCnt(int timeTick)
		{
			if(timeTick < lastPrintTime + PRINT_INFO_INTERVAL || gs.getConfig().pMapRoles == 0)
				return;
			
			int total = 0;
			int min = 999999;
			int minLine = 1;
			int max = 0;
			int maxLine = 1;
			for(int i = 1; i <= this.maps.size(); i++)
			{
				int cnt = this.maps.get(i - 1).size();
				total += cnt;
				if(cnt < min)
				{
					min = cnt;
					minLine = i;
				}
				
				if(cnt > max)
				{
					max = cnt;
					maxLine = i;
				}
			}
			
			if(this.specialMap != null)
			{
				int cnt = this.specialMap.size();
				total += cnt;
				
				if(cnt < min)
				{
					min = cnt;
					minLine = 0;
				}
				
				if(cnt > max)
				{
					max = cnt;
					maxLine = 0;
				}
			}
			
			if(max < gs.getConfig().pMapRoles)
				return;
			
			int lines = this.specialMap == null ? this.maps.size() : (this.maps.size() + 1);
			int averager = total / lines;
			gs.getLogger().info("@@ world " + this.mapId + ", lines: " + lines + " | total roles : " + total + " | average roles " + averager + " | min line " + minLine + " roles " + min + " | max line " + maxLine + " roles " + max);
			lastPrintTime = timeTick;
		}
		
		synchronized boolean isWorldMapFull(int line)
		{
			Set<Integer> mapRoles = getWorldMapRoles(line);
			if(mapRoles == null)
				return false;
			
			return mapRoles.size() >= gs.getConfig().mapMaxRoles;
		}
		
		synchronized int getMinWorld(int roleID, int line, int lastLine)
		{
			//专属分线
			if(line == 0 && specialMap != null)
				return line;
			
			if(line > 0)
			{
				line = line % this.maps.size();
				return line == 0 ? this.maps.size() : line;
			}
			
			int minGrade = Integer.MAX_VALUE;
			int lastLineGrade = Integer.MAX_VALUE;
			int autoLine = 1;
			
			for(int i = 0; i < this.maps.size(); i++)
			{
				if(this.maps.get(i).contains(roleID))
					return i + 1;
				
				int grade = this.maps.get(i).size() / GameData.WORLD_GRADE_ROLES;
				if(grade < minGrade)
				{
					minGrade = grade;
					autoLine = i + 1;
				}
				
				if(i + 1 == lastLine)
					lastLineGrade = grade;
			}
			
			return lastLineGrade == minGrade ? lastLine : autoLine;
		}
		
		synchronized void onWorldCntChange(int worldNum, int extralWorldNum)
		{
			for(int i = this.maps.size(); i < (worldNum + extralWorldNum); i++)
			{
				maps.add(new HashSet<>());
				gs.getLogger().debug("gamemaps world " + this.mapId + " add line " +(i + 1));
			}
		}
	}

	private abstract class MapCopyCluser<T extends MapCopy> extends MapCluster
	{
		boolean closed;
		AtomicInteger nextInstanceId = new AtomicInteger();
		ConcurrentMap<Integer, T> maps;

		public MapCopyCluser(int mapId, ConcurrentMap<Integer, T> maps)
		{
			super(mapId);
			this.maps = maps;
		}

		int getNextMapInstanceId()
		{
			return nextInstanceId.incrementAndGet();
		}

		synchronized boolean addMap(T map)
		{
			if (closed)
				return false;
			maps.put(map.mapInstance, map);
			gs.getMapService().syncStartMapCopy(GameMaps.this.session, mapId, map.mapInstance);
			return true;
		}

		//close，enter，leave3个函数加锁主要为closed变量同步
		synchronized void close()
		{
			List<T> mapsCopy = new ArrayList<>();
			mapsCopy.addAll(maps.values());
			maps.clear();
			gs.getLoginManager().addNormalTaskEvent(() ->
			{
				for (MapCopy map : mapsCopy)
				{
					map.close(mapId);
				}
			});
			closed = true;
		}

		synchronized void reset()
		{
			List<T> mapsCopy = new ArrayList<>();
			mapsCopy.addAll(maps.values());
			maps.clear();
			gs.getLoginManager().addNormalTaskEvent(() ->
			{
				for (MapCopy map : mapsCopy)
				{
					map.reset(mapId);
				}
			});
		}
		
		synchronized boolean roleEnterMap(int rid, int mapInstance)
		{
			if (closed)
				return false;
			MapCopy map = this.maps.get(mapInstance);
			if (map == null)
				return false;
			map.enterMap(rid);
			return true;
		}

		synchronized boolean roleLeaveMap(int rid, int mapInstance)
		{
			if (closed)
				return false;
			MapCopy map = this.maps.get(mapInstance);
			if (map == null)
				return false;
			if (map.leaveMap(rid) && map.canClose())
			{
				this.maps.remove(mapInstance);
				gs.getLogger().debug("all role leave, destroy empty map copy " + mapId + " " + mapInstance);
				gs.getMapService().syncEndMapCopy(GameMaps.this.session, mapId, mapInstance);
			}
			return true;
		}

		synchronized void syncMapCopyReady(int mapInstance)
		{
			if (!closed && this.maps.containsKey(mapInstance))
				gs.getMapService().syncMapCopyReady(GameMaps.this.session, mapId, mapInstance);
		}

		//timer不需要加锁(并发集合遍历，不需要锁)，也不能加锁(大集合定时变量不能加锁，并且其内可能调用Role的方法，会获取Role的锁，和前全面enter，leave函数被Role方法调用加锁顺序相反，会导致死锁)
		void onTimer(int timeTick)
		{
			Iterator<Map.Entry<Integer, T>> it = maps.entrySet().iterator();
			while (it.hasNext())
			{
				MapCopy map = it.next().getValue();
				if (map.onTimer(timeTick))
				{
					it.remove();
					map.forceAllRoleLeaveMap(mapId);
					gs.getLogger().debug("destroy timeout mapcopy " + mapId + " " + map.mapInstance);
					gs.getMapService().syncEndMapCopy(GameMaps.this.session, MapCopyCluser.this.mapId, map.mapInstance);
				}
			}
		}
		
		void onMapCopyTimeOut(int mapInstance)
		{
			MapCopy map = this.maps.remove(mapInstance);
			if(map != null)
			{
				gs.getLogger().debug("destroy timeout mapcopy " + mapId + " " + map.mapInstance + " from fs");
				map.forceAllRoleLeaveMap(mapId);
			}
		}
	}

	private abstract class MapCopy
	{
		boolean closed;
		final int mapInstance;
		Set<Integer> roles = new CopyOnWriteArraySet<>();

		MapCopy(int mapInstance)
		{
			this.mapInstance = mapInstance;
		}

		//close,enter,leave本不需要加锁，操作的集合roles是线程安全的，但是closed变量需要锁来保证
		void close(int mapId)
		{
			gs.getLogger().debug("close map " + mapId + " " + mapInstance + " kick all roles " + roles + " ...");
			this.onClose();
			Iterator<Integer> it = null;
			synchronized (roles)
			{
				it = roles.iterator();
				roles.clear();
				closed = true;
			}
			while (it.hasNext())
			{
				int rid = it.next();
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
					role.kickFromMap();
			}
		}
		
		void onClose()
		{
		}
		
		boolean canClose()
		{
			return true;
		}
		
		void reset(int mapId)
		{
			gs.getLogger().debug("reset map " + mapId + " " + mapInstance + " kick all roles " + roles + " ...");
			Iterator<Integer> it = roles.iterator();
			roles.clear();
			
			while (it.hasNext())
			{
				int rid = it.next();
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
					role.kickFromMap();
			}
		}
		
		boolean enterMap(int rid)
		{
			synchronized (roles)
			{
				if (closed)
					return false;
				roles.add(rid);
				return true;
			}
		}

		boolean leaveMap(int rid)
		{
			synchronized (roles)
			{
				if (closed)
					return false;
				roles.remove(rid);
				return roles.isEmpty();
			}
		}

		//不需要加锁，除roles集合是并发集合外，此方法调用时
		void forceAllRoleLeaveMap(int mapId)
		{
			gs.getLogger().debug("force map " + mapId + " " + mapInstance + " all roles " + roles + " leave ...");
			Iterator<Integer> it = null;
			synchronized (roles)
			{
				it = roles.iterator();
				roles.clear();
				closed = true;
			}
			while (it.hasNext())
			{
				int rid = it.next();
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
					role.syncMapCopyTimeoutLeave(mapId, MapCopy.this.mapInstance);
			}
		}

		abstract boolean onTimer(int timeTick);

	}

	private class PetLifeMapCopyCluster extends MapCopyCluser<PetLifeMapCopyCluster.PetLifeMap>
	{
		public PetLifeMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
		}
		
		int createMap()
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create pet life map " + mapId + " " + mapInstance + " success");
			if (addMap(new PetLifeMap(mapInstance)))
				return mapInstance;
			
			return 0;
		}
		
		public void onStart(int mapInstance)
		{
			PetLifeMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}
		
		class PetLifeMap extends MapCopy
		{
			int startTime = 0;
			
			PetLifeMap(int mapInstance)
			{
				super(mapInstance);
			}

			@Override
			boolean onTimer(int timeTick)
			{
				return false;
			}
			
			synchronized void onStart()
			{
				gs.getLogger().debug("on pet life map copy " + mapId + " " + mapInstance + " start");
				if (this.startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapCopyStart(now);
					this.startTime = now;
				}
			}
			
			void onRolesMapCopyStart(int time)
			{
				gs.getLogger().debug("on pet life map copy " + mapId + " " + mapInstance + " start");
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncPetLifeMapCopyStart(PetLifeMapCopyCluster.this.mapId, PetLifeMap.this.mapInstance, time);
				}
			}
		}
	}
	
	private class CommonMapCopyCluster extends MapCopyCluser<CommonMapCopyCluster.CommonMap>
	{
		int cfgMaxTime;//副本最大时长
		int cfgSlowMotionTime;//副本结束慢动作播放时长
		int cfgAutoPopUpTime;//副本不播慢动作时副本结束至弹出结算框之间时长
		int cfgAutoFlipCardTime;//结算框弹出至自动翻牌时长
		int cfgAutoCloseTime;//自动翻牌至自动退出副本时长

		public CommonMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
			this.cfgAutoPopUpTime = GameData.getInstance().getCommonCFG().map.mapCopyNoAnimationWaitPopupTime;
			this.cfgAutoFlipCardTime = GameData.getInstance().getCommonCFG().map.mapCopyAutoFlipCardTime;
			this.cfgAutoCloseTime = GameData.getInstance().getCommonCFG().map.mapCopyAutoCloseTime;
		}

		void setMapCfg(int cfgSlowMotionTime, int maxTime)
		{
			this.cfgSlowMotionTime = cfgSlowMotionTime;
			this.cfgMaxTime = maxTime;
		}

		int createMap()
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create common map " + mapId + " " + mapInstance + " success, maxTime=" + this.cfgMaxTime);
			if (addMap(new CommonMap(mapInstance)))
				return mapInstance;
			return 0;
		}

		public void onStart(int mapInstance)
		{
			CommonMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}

		public void onEnd(int mapInstance, int score)
		{
			CommonMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onEnd(score);
		}

		class CommonMap extends MapCopy
		{
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;
			int endSelectCardTime;

			CommonMap(int mapInstance)
			{
				super(mapInstance);
				this.closeTime = GameTime.getTime() + (cfgMaxTime + cfgAutoCloseTime);
			}

			synchronized boolean onTimer(int timeTick)
			{
				if (this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				if (this.endSelectCardTime > 0 && this.endSelectCardTime <= timeTick)
				{
					this.onRolesEndSelectCard(timeTick);
					this.endSelectCardTime = 0;
				}
				return this.closeTime <= timeTick;
			}

			synchronized void onStart()
			{
				gs.getLogger().debug("on common map copy " + mapId + " " + mapInstance + " start");
				if (this.startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapCopyStart(now);
					this.startTime = now;
				}
			}

			synchronized void onEnd(int score)
			{
				gs.getLogger().debug("on common map copy " + mapId + " " + mapInstance + " end");
				if (this.endTime == 0)
				{
					int now = GameTime.getTime();
					//正常击杀完boss完成副本需要加上播放慢动作时间
					if (score != 0)
					{
//						this.popupResultTime = (finishType == GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_BOSSES) ? now + cfgAutoPopUpTime + cfgSlowMotionTime : now + cfgAutoPopUpTime;
						this.popupResultTime = now + cfgSlowMotionTime + cfgAutoPopUpTime;
						this.endSelectCardTime = popupResultTime + cfgAutoFlipCardTime;
						this.closeTime = endSelectCardTime + cfgAutoCloseTime;
					}
					else
					{
						this.popupResultTime = now + cfgAutoPopUpTime;
						this.endSelectCardTime = 0;
						this.closeTime = this.popupResultTime + cfgAutoCloseTime;
					}
					this.onRolesMapCopyEnd(now, score);
					this.endTime = now;
				}
			}

			void onRolesMapCopyStart(int time)
			{
				gs.getLogger().debug("on common map copy " + mapId + " " + mapInstance + " start, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncCommonMapCopyStart(CommonMapCopyCluster.this.mapId, CommonMap.this.mapInstance, time);
				}
			}

			void onRolesMapCopyEnd(int time, int score)
			{
				gs.getLogger().debug("on common map copy " + mapId + " " + mapInstance + " end, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncCommonMapCopyEnd(CommonMapCopyCluster.this.mapId, CommonMap.this.mapInstance, time, score, roles);
				}
			}

			void onRolesPopupResult(int time)
			{
				gs.getLogger().debug("on common map copy " + mapId + " " + mapInstance + " popup result, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncCommonMapCopyPopupResult(CommonMapCopyCluster.this.mapId, CommonMap.this.mapInstance, time);
				}
			}

			void onRolesEndSelectCard(int time)
			{
				gs.getLogger().debug("on common map copy " + mapId + " " + mapInstance + " end reward, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncCommonMapCopyEndSelectCard(CommonMapCopyCluster.this.mapId, CommonMap.this.mapInstance, time);
				}
			}
		}
	}

	private class NormalMapCopyCluster extends CommonMapCopyCluster
	{
		NormalMapCopyCluster(int mapId)
		{
			super(mapId);
			SBean.MapCopyCFGS mapCopyCfg = GameData.getInstance().getMapCopyCFGS(mapId);
			super.setMapCfg(mapCopyCfg.slowMotionTime, mapCopyCfg.maxTime);
		}
	}

	private class ActivityMapCopyCluster extends CommonMapCopyCluster
	{
		ActivityMapCopyCluster(int mapId)
		{
			super(mapId);
			SBean.ActivityMapCFGS mapCopyCfg = GameData.getInstance().getActivityMapCFGS(mapId);
			super.setMapCfg(mapCopyCfg.slowMotionTime, mapCopyCfg.maxTime);
		}
	}
	
	private class ClimbTowerMapCopyCluster extends CommonMapCopyCluster
	{
		ClimbTowerMapCopyCluster(int mapId)
		{
			super(mapId);
			SBean.ClimbTowerMapCFGS mapCopyCfg = GameData.getInstance().getClimbTowerMapCFGS(mapId);
			super.setMapCfg(0, mapCopyCfg.maxTime);
		}
	}
	
	private class WeaponMapCopyCluster extends CommonMapCopyCluster
	{
		WeaponMapCopyCluster(int mapID)
		{
			super(mapID);
			SBean.WeaponMapCFGS mapCopyCfg = GameData.getInstance().getWeaponMapCFGS(mapID);
			super.setMapCfg(mapCopyCfg.slowMotionTime, mapCopyCfg.maxTime);
		}
	}

	private class JusticeMapCopyCluster extends CommonMapCopyCluster
	{
		JusticeMapCopyCluster(int mapId)
		{
			super(mapId);
			SBean.JusticeMapCopyCFGS mapCopyCfg = GameData.getInstance().getJusticeMapCopyCFGS(mapId);
			super.setMapCfg(mapCopyCfg.slowMotionTime, mapCopyCfg.maxTime);
		}
	}
	
	private class SectMapCopyCluster extends MapCopyCluser<SectMapCopyCluster.SectMap>
	{
		int maxTime;
		int slowMotionTime;
		int autoPopUpTime;//副本不播慢动作时副本结束至弹出结算框之间时长
		int autoCloseTime;

		public SectMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
			SBean.SectMapCFGS cfg = GameData.getInstance().getSectMapCFGS(mapId);
			this.maxTime = cfg.maxTime;
			this.slowMotionTime = cfg.slowMotionTime;
			this.autoPopUpTime = GameData.getInstance().getCommonCFG().map.mapCopyNoAnimationWaitPopupTime;
			this.autoCloseTime = GameData.getInstance().getCommonCFG().sect.mapAutoCloseTime;
		}

		int createMap(int sectId, Map<Integer, Integer> progress)
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create sect " + sectId + " map " + mapId + " " + mapInstance + " success, maxTime=" + this.maxTime);
			if (addMap(new SectMap(mapInstance, sectId)))
			{
				gs.getMapService().syncResetSectMap(GameMaps.this.session, mapId, mapInstance, progress);
				return mapInstance;
			}
			return 0;
		}

		public void onStart(int mapInstance)
		{
			SectMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}

		public void onProgressChanged(int mapInstance, int spawnPointId, int progress, int damage)
		{
			SectMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onProgressChanged(spawnPointId, progress, damage);
		}

		class SectMap extends MapCopy
		{
			int sectId;
			int closeTime;

			int startTime;
			int endTime;
			int popupResultTime;

			SectMap(int mapInstance, int sectId)
			{
				super(mapInstance);
				this.sectId = sectId;
				this.closeTime = GameTime.getTime() + maxTime + autoCloseTime;
			}

			synchronized boolean onTimer(int timeTick)
			{
				if (popupResultTime > 0 && popupResultTime <= timeTick)
				{
					this.onRolesPopupAttackResult(timeTick);
					this.popupResultTime = 0;
				}
				return this.closeTime <= timeTick;
			}

			synchronized void onStart()
			{
				gs.getLogger().debug("on sect map copy " + mapId + " " + mapInstance + " start");
				if (this.startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapcopyStart(now);
					this.startTime = now;
				}
			}

			synchronized void onProgressChanged(int spawnPointId, int progress, int damage)
			{
				gs.getLogger().debug("on sect map copy " + mapId + " " + mapInstance + " progress changed, spawnPointId " + spawnPointId + " hp lost " + progress + " damage " + damage);
				if (this.endTime == 0)
				{
					//spawnPointId --> 0 : 玩家挂了， -1：超时结束， > 0 挂掉血或挂了
					SBean.SectMapAttacker attacker = gs.getSectManager().syncSectMapProgress(sectId, mapId, spawnPointId, progress, damage);
					if (attacker != null)
					{
						int now = GameTime.getTime();
						this.popupResultTime = spawnPointId > 0 ? now + slowMotionTime + autoPopUpTime: now + autoPopUpTime;
						this.closeTime = popupResultTime + autoCloseTime;
						this.onRolesMapcopyEnd(now, attacker.endProgress, attacker.accDamage, attacker.accDamageRank, attacker.maxDamageRank, attacker.exReward);
						this.endTime = now;
					}
				}
			}

			void onRolesMapcopyStart(int time)
			{
				gs.getLogger().debug("on sect map copy " + mapId + " " + mapInstance + " start, left Time=" + (this.closeTime - time));
				for (int rid : roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncSectMapCopyStart(SectMapCopyCluster.this.mapId, SectMap.this.mapInstance, time);
				}
			}

			void onRolesMapcopyEnd(int time, int progress, int damage, int accDamageRank, int maxDamageRank, int extraReward)
			{
				gs.getLogger().debug("on sect map copy " + mapId + " " + mapInstance + " end, left Time=" + (this.closeTime - time));
				for (int rid : roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncSectMapCopyEnd(SectMapCopyCluster.this.mapId, SectMap.this.mapInstance, time, progress, damage, accDamageRank, maxDamageRank, extraReward);
				}
			}

			void onRolesPopupAttackResult(int time)
			{
				gs.getLogger().debug("on sect map copy " + mapId + " " + mapInstance + " popup result, left Time=" + (this.closeTime - time));
				for (int rid : roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncSectMapCopyPopupResult(SectMapCopyCluster.this.mapId, SectMap.this.mapInstance, time);
				}
			}
		}
	}

	private class SectGroupMapCopyCluster extends MapCopyCluser<SectGroupMapCopyCluster.SectGroupMap>
	{
		int maxTime;
		int autoCloseTime;

		public SectGroupMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
			SBean.SectGroupMapCFGS cfg = GameData.getInstance().getSectGroupMapCFGS(mapId);
			this.maxTime = cfg.maxTime;
			this.autoCloseTime = GameData.getInstance().getCommonCFG().sect.groupMapFinishEndTime;
		}

		int createMap(int sectId, int startTime, Map<Integer, Integer> process, Map<Integer, Integer> killNum, Map<Integer, SBean.RoleDamageDetail> damageRank)
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create sect " + sectId + " group map " + mapId + " " + mapInstance + " success, maxTime=" + this.maxTime);
			if (addMap(new SectGroupMap(mapInstance, sectId, startTime)))
			{
				gs.getMapService().syncResetSectGroupMap(GameMaps.this.session, mapId, mapInstance, process, killNum, damageRank);
				return mapInstance;
			}
			return 0;
		}

		public void onStart(int mapInstance)
		{
			SectGroupMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}

		synchronized Collection<Integer> getAllMapRoles(int mapInstance)
		{
			if(mapInstance < 0)
				return GameData.emptySet();
			
			return new TreeSet<>(this.maps.get(mapInstance).roles);
		}
		
		public void onSyncStatus(int mapId, int mapInstance, int progress)
		{
			SectGroupMap map = maps.get(mapInstance);
			if (map == null)
				return;
			gs.getSectManager().setGroupMapProcess(map.sectId, mapId, mapInstance, progress);
		}

		public void onSyncResult(int mapId, int mapInstance, int progress)
		{
			SectGroupMap map = maps.get(mapInstance);
			if (map == null)
				return;
			gs.getSectManager().receiveGroupMapReward(map.sectId, mapId, mapInstance, progress);
			map.onReceiveResult();
		}

		public void onProgressChanged(int mapInstance, int spawnPointId, int roleId, int monsterId, int progress, int damage)
		{
			SectGroupMap map = maps.get(mapInstance);
			if (map == null)
				return;
			gs.getSectManager().groupMapProcessChanged(map.sectId, mapId, mapInstance, spawnPointId, roleId, monsterId, progress, damage);
		}

		public void onMonsterAddKill(int mapInstance, int monsterId, int spawnPointId)
		{
			SectGroupMap map = maps.get(mapInstance);
			if (map == null)
				return;
			gs.getSectManager().groupMapMonsterAddKill(map.sectId, mapId, mapInstance, monsterId, spawnPointId);
		}

		class SectGroupMap extends MapCopy
		{
			int sectId;
			int closeTime;

			int startTime;
			int endTime;
			int popupResultTime;
			boolean isFinish = false;

			SectGroupMap(int mapInstance, int sectId, int startTime)
			{
				super(mapInstance);
				this.sectId = sectId;
				this.closeTime = startTime + maxTime + autoCloseTime;
				this.startTime = startTime;
			}
			
			public void onReceiveResult()
			{
				int now = GameTime.getTime();
				this.popupResultTime = now;
				this.closeTime = popupResultTime + autoCloseTime;
				this.endTime = now;
				isFinish = true;
			}

			boolean enterMap(int rid)
			{
				if (super.enterMap(rid))
				{
					onRoleEnter(rid);
					return true;
				}
				return false;
			}
			
			synchronized boolean onTimer(int timeTick)
			{
				return this.closeTime <= timeTick;
			}

			synchronized void onStart()
			{
				gs.getLogger().debug("on sect group map copy " + mapId + " " + mapInstance + " start");
				if (this.startTime == 0)
				{
					int now = GameTime.getTime();
					this.startTime = now;
				}
			}

			synchronized void onRoleEnter(int roleId)
			{
				gs.getLogger().debug("on sect group map copy " + mapId + " " + mapInstance + " start, left Time=" + (this.closeTime - GameTime.getTime()));
				if (roles.contains(roleId))
				{
					Role role = gs.getLoginManager().getOnGameRole(roleId);
					if (role != null)
						role.syncSectGroupMapCopyStart(SectGroupMapCopyCluster.this.mapId, SectGroupMap.this.mapInstance, SectGroupMap.this.startTime);
				}
			}
			
			void onClose()
			{
				gs.getSectManager().groupMapOnClose(sectId, mapId);
			}
			
			boolean canClose()
			{
				return isFinish;
			}
		}
	}

	private class EmergencyMapCopyCluster extends MapCopyCluser<EmergencyMapCopyCluster.EmergencyMap>
	{
		public EmergencyMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
		}

		int createMap(int endTime)
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create emergency map " + mapId + " " + mapInstance + " success, endTime=" + endTime);
			if (addMap(new EmergencyMap(mapInstance, endTime)))
			{
				gs.getMapService().syncEmergencyLastTime(this.mapId, mapInstance, endTime - GameTime.getTime());
				return mapInstance;
			}
			return 0;
		}

		public void onStart(int mapInstance)
		{
			EmergencyMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}

		public void onEnd(int mapInstance)
		{
			EmergencyMap map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onEnd();
		}

		class EmergencyMap extends MapCopy
		{
			int closeTime;
			int startTime;
			int autoEndTime = 5;
			boolean isFinish = false;

			EmergencyMap(int mapInstance, int endTime)
			{
				super(mapInstance);
				this.closeTime = endTime + this.autoEndTime;
			}
			
			synchronized void onEnd()
			{
				int now = GameTime.getTime();
				this.closeTime = now + this.autoEndTime;
				gs.getLogger().debug("on emergency map copy " + mapId + " " + mapInstance + " end");
				for (int rid : roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncEmergencyMapCopyEnd(EmergencyMapCopyCluster.this.mapId, this.mapInstance, now);
				}
				gs.getEmergencyManager().onEmergencyMapFinish(EmergencyMapCopyCluster.this.mapId, this.mapInstance);
			}
			
			synchronized boolean onTimer(int timeTick)
			{
				boolean close =  this.closeTime <= timeTick;
				if (close)
					gs.getEmergencyManager().onEmergencyMapFinish(EmergencyMapCopyCluster.this.mapId, this.mapInstance);
				return close;
			}

			synchronized void onStart()
			{
				gs.getLogger().debug("on emergency map copy " + mapId + " " + mapInstance + " start " + this.startTime);
				if (this.startTime == 0)
				{
					this.startTime = GameTime.getTime();
				}
			}

			void onClose()
			{
			}
			
			boolean canClose()
			{
				return isFinish;
			}
		}
	}
	
	private class ArenaMapCopyCluster extends MapCopyCluser<ArenaMapCopyCluster.ArenaMapCopy>
	{
		int maxTime;
		int autoCloseTime;

		public ArenaMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
			this.maxTime = GameData.getInstance().getArenaCFGS().arenaMaxTime;
			this.autoCloseTime = GameData.getInstance().getArenaCFGS().arenaAutoCloseTime;
		}

		int createMap(SBean.BattleArray enemy)
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create arena map " + mapId + " " + mapInstance + " success, maxTime=" + this.maxTime);
			if (addMap(new ArenaMapCopy(mapInstance)))
			{
				gs.getMapService().syncResetArenaMap(GameMaps.this.session, ArenaMapCopyCluster.this.mapId, mapInstance, enemy);
				return mapInstance;
			}
			return 0;
		}

		public void onStart(int mapInstance)
		{
			ArenaMapCopy map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}

		public void onEnd(int mapInstance, boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
		{
			ArenaMapCopy map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onEnd(win, attackingSideHp, defendingSideHp);
		}

		class ArenaMapCopy extends MapCopy
		{
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;

			public ArenaMapCopy(int mapInstance)
			{
				super(mapInstance);
				this.closeTime = GameTime.getTime() + maxTime + autoCloseTime;
			}

			synchronized boolean onTimer(int timeTick)
			{
				if (this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				return this.closeTime <= timeTick;
			}

			synchronized void onStart()
			{
				gs.getLogger().debug("on arena map " + mapId + " " + mapInstance + " start");
				if (startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapcopyStart(now);
					this.startTime = now;
				}
			}

			synchronized void onEnd(boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
			{
				gs.getLogger().debug("on arena map " + mapId + " " + mapInstance + " end");
				if (endTime == 0)
				{
					SBean.ArenaBattleResult result = gs.getArenaManager().onArenaBattleEnd(win, attackingSideHp, defendingSideHp);
					if (result != null)
					{
						int now = GameTime.getTime();
						this.popupResultTime = now;
						this.closeTime = popupResultTime + autoCloseTime;
						this.onRolesMapcopyEnd(now, win, result);
						this.endTime = now;
					}
				}
			}

			void onRolesMapcopyStart(int time)
			{
				gs.getLogger().debug("on arena map copy " + mapId + " " + mapInstance + " start, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncArenaMapCopyStart(ArenaMapCopyCluster.this.mapId, ArenaMapCopy.this.mapInstance, time);
				}
			}

			void onRolesMapcopyEnd(int time, boolean win, SBean.ArenaBattleResult result)
			{
				gs.getLogger().debug("on arena map copy " + mapId + " " + mapInstance + " end, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncArenaMapCopyEnd(ArenaMapCopyCluster.this.mapId, ArenaMapCopy.this.mapInstance, time, win, result);
				}
			}

			void onRolesPopupResult(int time)
			{
				gs.getLogger().debug("on arena map copy " + mapId + " " + mapInstance + " popup result, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncArenaMapCopyPopupResult(ArenaMapCopyCluster.this.mapId, ArenaMapCopy.this.mapInstance, time);
				}
			}
		}
	}

	private class SuperArenaMapCopyCluster extends MapCopyCluser<SuperArenaMapCopyCluster.SuperArenaMapCopy>
	{
		int maxTime;
		int autoCloseTime;

		public SuperArenaMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
			SBean.SuperArenaMapCFGS mapCfg = GameData.getInstance().getSuperArenaMapCFGS(mapId);
			if(mapCfg != null)
			{
				SBean.SuperArenaTypeCFGS typeCfg = GameData.getInstance().getSuperArenaTypeCFG(mapCfg.type);
				if(typeCfg != null)
					this.maxTime = typeCfg.maxTime * typeCfg.races;
			}
			
			this.autoCloseTime = GameData.getInstance().getSuperArenaCFGS().normal.autoCloseTime;
		}

		int createMap()
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().info("create super arena map " + mapId + " " + mapInstance + " success, maxTime=" + this.maxTime);
			if (addMap(new SuperArenaMapCopy(mapInstance)))
				return mapInstance;
			return 0;
		}

		int createMap(int mapInstance)
		{
			gs.getLogger().info("create super arena map " + mapId + " " + mapInstance + " success, maxTime=" + this.maxTime);
			if (addMap(new SuperArenaMapCopy(mapInstance)))
				return mapInstance;
			return 0;
		}
		
		synchronized boolean addMap(SuperArenaMapCopy map)
		{
			if (closed)
				return false;
			
			//FS 已经通知GlobalMap 创建地图， 这里不需要
			maps.put(map.mapInstance, map);
			return true;
		}
		
		void onTimer(int timeTick)
		{
			this.maps.values().forEach(map -> map.onTimer(timeTick));
		}
		
		public void onStart(int mapInstance, Map<Integer, Integer> eloDiffs)
		{
			SuperArenaMapCopy map = this.maps.get(mapInstance);
			if (map == null)
				return;

			map.onStart(eloDiffs);
		}

		public void onEnd(int mapInstance, SBean.SuperArenaBattleResult result, int rankClearTime)
		{
			SuperArenaMapCopy map = this.maps.get(mapInstance);
			if (map == null)
				return;

			map.onEnd(result, rankClearTime);
		}

		public void onRaceEnd(int mapInstance)
		{
			SuperArenaMapCopy map = this.maps.get(mapInstance);
			if (map == null)
				return;

			map.onRaceEnd();
		}
		
		class SuperArenaMapCopy extends MapCopy
		{
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;

			SuperArenaMapCopy(int mapInstance)
			{
				super(mapInstance);
				this.closeTime = GameTime.getTime() + maxTime + autoCloseTime;
			}

			@Override
			boolean onTimer(int timeTick)
			{
				if (this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				return this.closeTime <= timeTick;
			}

			synchronized void onStart(Map<Integer, Integer> eloDiffs)
			{
				gs.getLogger().info("on super arena map " + mapId + " " + mapInstance + " start");
				if (startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapCopyStart(now, eloDiffs);
					this.startTime = now;
				}
			}

			void onRolesMapCopyStart(int time, Map<Integer, Integer> eloDiffs)
			{
				gs.getLogger().info("on super arena map copy " + mapId + " " + mapInstance + " start, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncSuperArenaMapCopyStart(SuperArenaMapCopyCluster.this.mapId, this.mapInstance, time, eloDiffs.getOrDefault(rid, 0));
				}
			}

			synchronized void onEnd(SBean.SuperArenaBattleResult result, int rankClearTime)
			{
				gs.getLogger().info("on super arena map " + mapId + " " + mapInstance + " end lose group : " + result.loseTeam);
				if (endTime == 0)
				{
					int now = GameTime.getTime();
					this.popupResultTime = now;
					this.closeTime = popupResultTime + autoCloseTime;
					this.onRolesMapcopyEnd(now, result, rankClearTime);
					this.endTime = now;
				}
			}

			void onRolesMapcopyEnd(int time, SBean.SuperArenaBattleResult result, int rankClearTime)
			{
				gs.getLogger().info("on super arena map copy " + mapId + " " + mapInstance + " end, left Time=" + (this.closeTime - time) + " popupResultTime " + this.popupResultTime);
				SBean.SuperArenaMapCFGS mapCfg = GameData.getInstance().getSuperArenaMapCFGS(SuperArenaMapCopyCluster.this.mapId);
				if(mapCfg != null)
				{
					SBean.SuperArenaTypeCFGS typeCfg = GameData.getInstance().getSuperArenaTypeCFG(mapCfg.type);
					if(typeCfg != null)
					{
						for(SBean.SABattleTeamInfo teamInfo: result.teams.values())
						{
							for(SBean.SABattleInfo info: teamInfo.members.values())
							{
								Role role = gs.getLoginManager().getOnGameRole(info.rid);
								if(role != null)
								{
//									gs.getSuperArenaManager().onSuperArenaEnd(role, result, mapCfg.type);
									int dayEnterTimes = role.getSuperArenaDayEnterTimes(mapCfg.type);
									int percent = GameData.getSuperArenaAddHonorPencent(typeCfg.honorPercent, dayEnterTimes);
									info.addHonor = (int) (info.addHonor * (percent / 100.f));
								}
									
							}
						}
					}
				}
				
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncSuperArenaMapCopyEnd(SuperArenaMapCopyCluster.this.mapId, this.mapInstance, time, result, rankClearTime);
				}
			}

			void onRolesPopupResult(int time)
			{
				gs.getLogger().info("on super arena map copy " + mapId + " " + mapInstance + " popup result, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncSuperArenaMapCopyPopupResult(SuperArenaMapCopyCluster.this.mapId, this.mapInstance, time);
				}
			}
			
			synchronized void onRaceEnd()
			{
				gs.getLogger().info("sync enter super arena race [" + mapId + " , " + mapInstance + "]");
				int now = GameTime.getTime();
				for(int rid: this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncEnterSuperArenaRace(SuperArenaMapCopyCluster.this.mapId, this.mapInstance, now);
				}
			}
		}
	}
	
	//正邪道场
	private class BWArenaMapCopyCluster extends MapCopyCluser<BWArenaMapCopyCluster.BWArenaMapCopy>
	{
		int maxTime;
		int autoCloseTime;

		public BWArenaMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
			this.maxTime = GameData.getInstance().getBWArenaCFGS().fight.maxTime;
			this.autoCloseTime = GameData.getInstance().getBWArenaCFGS().fight.autoCloseTime;
		}

		int createMap(SBean.BattleArray enemy, boolean petLack, boolean sameBWArenaLvl)
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create bw arena map " + mapId + " " + mapInstance + " success, maxTime = " + this.maxTime);
			if (addMap(new BWArenaMapCopy(mapInstance, sameBWArenaLvl)))
			{
				gs.getMapService().syncResetBWArenaMap(GameMaps.this.session, BWArenaMapCopyCluster.this.mapId, mapInstance, enemy, petLack);
				return mapInstance;
			}
			return 0;
		}

		public void onStart(int mapInstance)
		{
			BWArenaMapCopy map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}

		public void onEnd(int mapInstance, boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
		{
			BWArenaMapCopy map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onEnd(win, attackingSideHp, defendingSideHp);
		}

		class BWArenaMapCopy extends MapCopy
		{
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;
			boolean sameBWArenaLvl;
			
			public BWArenaMapCopy(int mapInstance, boolean sameBWArenaLvl)
			{
				super(mapInstance);
				this.closeTime = GameTime.getTime() + maxTime + autoCloseTime;
				this.sameBWArenaLvl = sameBWArenaLvl;
			}

			synchronized boolean onTimer(int timeTick)
			{
				if (this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				return this.closeTime <= timeTick;
			}

			synchronized void onStart()
			{
				gs.getLogger().debug("on bw arena map " + mapId + " " + mapInstance + " start");
				if (startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapcopyStart(now);
					this.startTime = now;
				}
			}

			synchronized void onEnd(boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
			{
				gs.getLogger().debug("on bw arena map " + mapId + " " + mapInstance + " end");
				if (endTime == 0)
				{
					int now = GameTime.getTime();
					this.popupResultTime = now;
					this.closeTime = popupResultTime + autoCloseTime;
					this.onRolesMapcopyEnd(now, win, attackingSideHp, defendingSideHp);
					this.endTime = now;
				}
			}

			void onRolesMapcopyStart(int time)
			{
				gs.getLogger().debug("on bw arena map copy " + mapId + " " + mapInstance + " start, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncBWArenaMapCopyStart(BWArenaMapCopyCluster.this.mapId, BWArenaMapCopy.this.mapInstance, time);
				}
			}

			void onRolesMapcopyEnd(int time, boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
			{
				gs.getLogger().debug("on bw arena map copy " + mapId + " " + mapInstance + " end, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
					{
						int addScore = gs.getBWArenaManager().onBWArenaBattleEnd(role, win, sameBWArenaLvl, attackingSideHp, defendingSideHp);
						role.syncBWArenaMapCopyEnd(BWArenaMapCopyCluster.this.mapId, BWArenaMapCopy.this.mapInstance, time, addScore, attackingSideHp, defendingSideHp);
					}
				}
			}

			void onRolesPopupResult(int time)
			{
				gs.getLogger().debug("on bw arena map copy " + mapId + " " + mapInstance + " popup result, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncBWArenaMapCopyPopupResult(BWArenaMapCopyCluster.this.mapId, BWArenaMapCopy.this.mapInstance, time);
				}
			}
		}
	}
	
	//势力战
	private class ForceWarMapCopyCluster extends MapCopyCluser<ForceWarMapCopyCluster.ForceWarMapCopy>
	{
		public ForceWarMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
		}

		synchronized boolean addMap(ForceWarMapCopy map)
		{
			if (closed)
				return false;
			
			//FS 已经通知GlobalMap 创建地图， 这里不需要
			maps.put(map.mapInstance, map);
			return true;
		}
		
		int createMap(int mapInstance)
		{
			gs.getLogger().debug("create force war map " + mapId + " " + mapInstance + " success");
			if (addMap(new ForceWarMapCopy(mapInstance)))
				return mapInstance;
			return 0;
		}

		void onTimer(int timeTick)
		{
			this.maps.values().forEach(map -> map.onTimer(timeTick));
		}
		
		public void onStart(int mapInstance)
		{
			ForceWarMapCopy map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onStart();
		}

		public void onEnd(int mapInstance, SBean.RankClearTime rankClearTime, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
		{
			ForceWarMapCopy map = maps.get(mapInstance);
			if (map == null)
				return;
			map.onEnd(rankClearTime, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
		}

		class ForceWarMapCopy extends MapCopy
		{
			int popupResultTime;
			public ForceWarMapCopy(int mapInstance)
			{
				super(mapInstance);
			}

			synchronized boolean onTimer(int timeTick)
			{
				if (this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				return false;
			}

			synchronized void onStart()
			{
				gs.getLogger().debug("on force war map " + mapId + " " + mapInstance + " start");
				this.onRolesMapcopyStart(GameTime.getTime());
			}

			void onRolesMapcopyStart(int time)
			{
				gs.getLogger().debug("on force war map copy " + mapId + " " + mapInstance + " start");
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncForceWarMapCopyStart(ForceWarMapCopyCluster.this.mapId, ForceWarMapCopy.this.mapInstance, time);
				}
			}

			synchronized void onEnd(SBean.RankClearTime rankClearTime, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
			{
				gs.getLogger().debug("on force war map " + mapId + " " + mapInstance + " end");
				int now = GameTime.getTime();
				this.popupResultTime = now;
				this.onRolesMapcopyEnd(now, rankClearTime, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
			}
			
			void onRolesMapcopyEnd(int time, SBean.RankClearTime rankClearTime, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
			{
				gs.getLogger().debug("on force war map copy " + mapId + " " + mapInstance + " end");
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
					{
						role.syncForceWarMapCopyEnd(ForceWarMapCopyCluster.this.mapId, ForceWarMapCopy.this.mapInstance, time, role.BWType == 1 ? rankClearTime.whiteClearTime : rankClearTime.blackClearTime, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
					}
				}
			}

			void onRolesPopupResult(int time)
			{
				gs.getLogger().debug("on force war map copy " + mapId + " " + mapInstance + " popup result");
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncForceWarMapCopyPopupResult(ForceWarMapCopyCluster.this.mapId, ForceWarMapCopy.this.mapInstance, time);
				}
			}
		}
	}
	
	//伏魔洞
	private class DemonHoleMapCopyCluster extends MapCopyCluser<DemonHoleMapCopyCluster.DemonHoleMapCopy>
	{
		public DemonHoleMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
		}

		synchronized boolean addMap(DemonHoleMapCopy map)
		{
			if(closed)
				return false;
			
			maps.put(map.mapInstance, map);
			return true;
		}
		
		int createMap(int mapInstance)
		{
			if(maps.containsKey(mapInstance))
				return 0;
			
			gs.getLogger().debug("create demon hole map " + mapId + " " + mapInstance + " success");
			if(addMap(new DemonHoleMapCopy(mapInstance)))
				return mapInstance;
			
			return 0;
		}
		
		void onTimer(int timeTick)
		{
			this.maps.values().forEach(map -> map.onTimer(timeTick));
		}
		
		//伏魔洞没有start
		public void onStart(int mapInstance)
		{
			//TODO
		}
		
		public void onEnd(int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
		{
			DemonHoleMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onEnd(curFloor, total);
		}
		
		class DemonHoleMapCopy extends MapCopy
		{
			int popupResultTime;
			public DemonHoleMapCopy(int mapInstance)
			{
				super(mapInstance);
			}

			@Override
			synchronized boolean onTimer(int timeTick)
			{
				if(this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				return false;
			}
			
			synchronized void onEnd(List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
			{
				gs.getLogger().info("on demon hole map copy " + mapId + " " + mapInstance + " end");
				int now = GameTime.getTime();
				this.popupResultTime = now;
				this.onRolesMapCopyEnd(now, curFloor, total);
			}
			
			private void onRolesMapCopyEnd(int time, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
			{
				gs.getLogger().info("on demon hole map copy " + mapId + " "  + mapInstance + " end");
				for(int rid: this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if(role != null)
					{
						role.syncDemonHoleMapCopyEnd(DemonHoleMapCopyCluster.this.mapId, DemonHoleMapCopy.this.mapInstance, time, curFloor, total);
					}
				}
			}
			
			private void onRolesPopupResult(int time)
			{
				gs.getLogger().info("on demon hole map copy " + mapId + " "  + mapInstance + " popup result");
				for(int rid: this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if(role != null)
					{
						role.syncDemonHoleMapCopyPopupResult(DemonHoleMapCopyCluster.this.mapId, DemonHoleMapCopy.this.mapInstance, time);
					}
				}
			}
			
			boolean canClose()
			{
				return true;
			}
		}
	}
	
	public class FightNpcMapCopyCluster extends MapCopyCluser<FightNpcMapCopyCluster.FightNpcMapCopy>
	{
		final int maxTime;
		final int autoCloseTime = 1;
		public FightNpcMapCopyCluster(int mapId)
		{
			super(mapId, new ConcurrentHashMap<>());
			SBean.FightNpcMapCFGS mapCfg = GameData.getInstance().getFightNpcMapCFGS(mapId);
			maxTime = mapCfg == null ? 0 : mapCfg.maxTime;
		}
		
		int createMap()
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create fight npc map copy[" + mapId + " " + mapInstance + "] success, maxTime=" + this.maxTime);
			if(addMap(new FightNpcMapCopy(mapInstance)))
			{
				return mapInstance;
			}
			return 0;
		}
		
		public void onStart(int mapInstance)
		{
			FightNpcMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onStart();
		}
		
		public void onEnd(int mapInstance, boolean win)
		{
			FightNpcMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onEnd(win);
		}
		
		class FightNpcMapCopy extends MapCopy
		{
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;
			
			FightNpcMapCopy(int mapInstance)
			{
				super(mapInstance);
				this.closeTime = GameTime.getTime() + maxTime + autoCloseTime;
			}

			@Override
			synchronized boolean onTimer(int timeTick)
			{
				if (this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				
				return this.closeTime <= timeTick;
			}
			
			private void onRolesPopupResult(int time)
			{
				gs.getLogger().debug("on fight npc map copy " + mapId + " " + mapInstance + " popup result, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncFightNpcMapCopyPopupResult(FightNpcMapCopyCluster.this.mapId, this.mapInstance, time);
				}
			}
			
			synchronized void onStart()
			{
				gs.getLogger().debug("on fight npc map copy [" + mapId + " " + mapInstance + "] start");
				if(startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapcopyStart(now);
					this.startTime = now;
				}
			}
			
			private void onRolesMapcopyStart(int time)
			{
				gs.getLogger().debug("on fight npc map copy [" + mapId + " " + mapInstance + "] start, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncFightNpcMapCopyStart(FightNpcMapCopyCluster.this.mapId, this.mapInstance, time);
				}
			}
			
			synchronized void onEnd(boolean win)
			{
				gs.getLogger().debug("on fight npc map copy[" + mapId + " " + mapInstance + "] end");
				if (endTime == 0)
				{
					int now = GameTime.getTime();
					this.popupResultTime = now;
					this.closeTime = popupResultTime + autoCloseTime;
					this.onRolesMapcopyEnd(now, win);
					this.endTime = now;
				}
			}
			
			private void onRolesMapcopyEnd(int time, boolean win)
			{
				gs.getLogger().debug("on fight npc map copy[" + mapId + " " + mapInstance + "]end, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncFightNpcMapCopyEnd(FightNpcMapCopyCluster.this.mapId, this.mapInstance, time, win);
				}
			}
		}
	}
	
	public class TowerDefenceMapCopyCluster extends MapCopyCluser<TowerDefenceMapCopyCluster.TowerDefenceMapCopy>
	{
		int cfgMaxTime;//副本最大时长
		int cfgSlowMotionTime;//副本结束慢动作播放时长
		int cfgAutoPopUpTime;//副本不播慢动作时副本结束至弹出结算框之间时长
		int cfgAutoFlipCardTime;//结算框弹出至自动翻牌时长
		int cfgAutoCloseTime;//自动翻牌至自动退出副本时长
		
		public TowerDefenceMapCopyCluster(int mapId) 
		{
			super(mapId, new ConcurrentHashMap<>());
			SBean.TowerDefenceMapCFGS mapCfg = GameData.getInstance().getTowerDefenceMapCFGS(mapId);
			this.cfgMaxTime = mapCfg == null ? 0 : mapCfg.maxTime;
			this.cfgAutoPopUpTime = GameData.getInstance().getCommonCFG().map.mapCopyNoAnimationWaitPopupTime;
			this.cfgAutoFlipCardTime = GameData.getInstance().getCommonCFG().map.mapCopyAutoFlipCardTime;
			this.cfgAutoCloseTime = GameData.getInstance().getCommonCFG().map.mapCopyAutoCloseTime;
		}

		int createMap()
		{
			int mapInstance = getNextMapInstanceId();
			gs.getLogger().debug("create tower defence map copy[" + mapId + " " + mapInstance + "] success, maxTime=" + this.cfgMaxTime);
			if(addMap(new TowerDefenceMapCopy(mapInstance)))
			{
				return mapInstance;
			}
			return 0;
		}
		
		public void onStart(int mapInstance)
		{
			TowerDefenceMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onStart();
		}
		
		public void onEnd(int mapInstance, int count)
		{
			TowerDefenceMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.onEnd(count);
		}
		
		public void syncSpawnCount(int mapInstance, int count)
		{
			TowerDefenceMapCopy map = maps.get(mapInstance);
			if(map == null)
				return;
			
			map.syncSpawnCount(count);
		}
		
		class TowerDefenceMapCopy extends MapCopy
		{
			int closeTime;
			int startTime;
			int endTime;
			int popupResultTime;
			int endSelectCardTime;
			
			TowerDefenceMapCopy(int mapInstance)
			{
				super(mapInstance);
				this.closeTime = GameTime.getTime() + (cfgMaxTime + cfgAutoCloseTime);
			}

			@Override
			synchronized boolean onTimer(int timeTick)
			{
				if (this.popupResultTime > 0 && this.popupResultTime <= timeTick)
				{
					this.onRolesPopupResult(timeTick);
					this.popupResultTime = 0;
				}
				if (this.endSelectCardTime > 0 && this.endSelectCardTime <= timeTick)
				{
					this.onRolesEndSelectCard(timeTick);
					this.endSelectCardTime = 0;
				}
				return this.closeTime <= timeTick;
			}
			
			private void onRolesPopupResult(int time)
			{
				gs.getLogger().debug("on tower defence map copy " + mapId + " " + mapInstance + " popup result, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncTowerDefenceMapCopyPopupResult(TowerDefenceMapCopyCluster.this.mapId, this.mapInstance, time);
				}
			}
			
			void onRolesEndSelectCard(int time)
			{
				gs.getLogger().info("on tower defence map copy " + mapId + " " + mapInstance + " end card reward, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncTowerDefenceEndSelectCard(TowerDefenceMapCopyCluster.this.mapId, TowerDefenceMapCopy.this.mapInstance, time);
				}
			}
			
			synchronized void onStart()
			{
				gs.getLogger().info("on tower defence map copy [" + mapId + " " + mapInstance + "] start");
				if(startTime == 0)
				{
					int now = GameTime.getTime();
					this.onRolesMapcopyStart(now);
					this.startTime = now;
				}
			}
			
			private void onRolesMapcopyStart(int time)
			{
				gs.getLogger().info("on tower defence map copy [" + mapId + " " + mapInstance + "] start, left Time=" + (this.closeTime - time));
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncTowerDefenceMapCopyStart(TowerDefenceMapCopyCluster.this.mapId, this.mapInstance, time);
				}
			}
			
			synchronized void onEnd(int count)
			{
				gs.getLogger().info("on tower defence map copy[" + mapId + " " + mapInstance + "] end");
				if (endTime == 0)
				{
					int now = GameTime.getTime();
					this.popupResultTime = now + cfgSlowMotionTime + cfgAutoPopUpTime;
					this.endSelectCardTime = popupResultTime + cfgAutoFlipCardTime;
					this.closeTime = endSelectCardTime + cfgAutoCloseTime;
					this.onRolesMapcopyEnd(now, count);
					this.endTime = now;
				}
			}
			
			private void onRolesMapcopyEnd(int time, int count)
			{
				int useTime = time - this.startTime;
				gs.getLogger().info("on tower defence map copy[" + mapId + " " + mapInstance + "]end, left Time=" + (this.closeTime - time) + " useTime " + useTime);
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncTowerDefenceMapCopyEnd(TowerDefenceMapCopyCluster.this.mapId, this.mapInstance, time, count, useTime);
				}
			}
			
			synchronized void syncSpawnCount(int count)
			{
				for (int rid : this.roles)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if (role != null)
						role.syncTowerDefenceSpawnCount(TowerDefenceMapCopyCluster.this.mapId, this.mapInstance, count);
				}
			}
		}
	}
	
	public void syncSectGroupMapCopyStatus(int mapID, int mapInstance, int progress)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;
		if (mcc instanceof SectGroupMapCopyCluster)
		{
			SectGroupMapCopyCluster smcc = SectGroupMapCopyCluster.class.cast(mcc);
			smcc.onSyncStatus(mapID, mapInstance, progress);
		}
	}

	public void syncSectGroupMapCopyResult(int mapID, int mapInstance, int progress)
	{
		MapCluster mcc = this.allMaps.get(mapID);
		if (mcc == null)
			return;
		if (mcc instanceof SectGroupMapCopyCluster)
		{
			SectGroupMapCopyCluster smcc = SectGroupMapCopyCluster.class.cast(mcc);
			smcc.onSyncResult(mapID, mapInstance, progress);
		}
	}

	void syncSectGroupMapCopyProgress(int mapId, int mapInstance, int spawnPoint, int roleId, int monsterId, int hpLostBP, int damage)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof SectGroupMapCopyCluster)
		{
			SectGroupMapCopyCluster smcc = SectGroupMapCopyCluster.class.cast(mcc);
			smcc.onProgressChanged(mapInstance, spawnPoint, roleId, monsterId, hpLostBP, damage);
		}
	}

	void syncSectGroupMapCopyAddKill(int mapId, int mapInstance, int monsterId, int spawnPointId)
	{
		MapCluster mcc = this.allMaps.get(mapId);
		if (mcc == null)
			return;
		if (mcc instanceof SectGroupMapCopyCluster)
		{
			SectGroupMapCopyCluster smcc = SectGroupMapCopyCluster.class.cast(mcc);
			smcc.onMonsterAddKill(mapInstance, monsterId, spawnPointId);
		}
	}
	
	final int msid;
	int session;
	GameServer gs;

	ConcurrentMap<Integer, MapCluster> allMaps = new ConcurrentHashMap<>();

}
