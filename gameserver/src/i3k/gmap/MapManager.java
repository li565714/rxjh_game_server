package i3k.gmap;

import i3k.SBean;
import i3k.SBean.MapFlagSectOverView;
import i3k.gmap.BaseMap.*;
import i3k.gmap.BaseRole.EnterInfo;
import i3k.gmap.DropGoods.DropItem;
import i3k.gs.BossManager;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.management.ObjectName;



public class MapManager 
{
	// mbean
	public interface MapStatMBean
	{
		int getRoleCount();
//		int getPetCount();
		
		int getSamplinginterval();
		int getCompletedTaskCount();
		int getUncompletedTaskCount();
		int getReceiveTimerTaskCount();
		int getReceiveClientTaskCount();
		int getReceiveServerTaskCount();
	}	
	//
	
//	public static final int MAPGRIDUNIT = 500;		//grid 大小
//	public static final int GRIDMAXX = 4000;			
//	public static final int GRIDMAXZ = 4000;
	public static final int MAXROW = 16;			
	public static final int MAXCOL = 16;
	
	//Trap state  陷阱基础态
	public static final int ESTRAP_BASE		= 10000;
	public static final int ESTRAP_LOCKED	= 10001;	//未激活状态
	public static final int ESTRAP_ACTIVE	= 10002;	//激活未触发状态
	public static final int ESTRAP_TRIG		= 10003;	//激活触发状态
	public static final int ESTRAP_OPEN		= 10004;	//开启/损坏状态
	
	//状态转换条件
	public static final int TRAP_CHANGE_STATE1 = 1;		//清怪
	public static final int TRAP_CHANGE_STATE2 = 2;		//关联激活
	
	//陷阱类型
	public static final int TRAP_TRIGTYPE_CLICKED	= 1;	//开关触发器类型(点击)
	public static final int TRAP_TRIGTYPE_ATTACK	= 2;	//无触发效果类型
	
	
	//攻击命中类型
	public static final int ATTACK_DODGE	= 0;	//闪避
	public static final int ATTACK_ATR		= 1;	//命中
	public static final int ATTACK_DEFLECT	= 2;	//偏斜
	
	public static final int TARGET_POSITION_OFFSET 		= 200;		//目标位置偏差
	public static final int COLLISION_POSITION_OFFSET	= 100;		//碰撞位置偏差
	
	public static final int PET_CREATE_RADIUS 		= 300;			//宠物的出生半径
	public static final int PET_RESETPOS_RADIUS 	= 3000;			//超过一定距离将佣兵拉倒玩家身边
	public static final int FEAR_MAX_RADIUS 		= 1500;
	
	public static final int PROTOCOL_AUTOSEND_INTERVAL	= 250;
	
	MapManager(MapServer ms)
	{
		this.ms = ms;
	}

	void start()
	{
		stat.start();
		new Behavior().initMatchTbl();		
		for (int mapID : ms.getDeployConf().getMapDelpoy().getDeployMaps())
			this.mapClusters.put(mapID, new MapCluster(mapID, ms).createNew());
		
		taskExecutor = Executors.newSingleThreadExecutor();
		this.lastCreateSuperMonsterTime = GameTime.getTime();
		this.startTime = GameTime.getTimeMillis();
		this.timeTick = this.setTimeTick();
	}

	void destroy()
	{
		try
		{
			taskExecutor.shutdown();
			while (!taskExecutor.awaitTermination(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
			ms.getLogger().warn("shutdown executor cause exception", e);
		}
	}

	void clearAllMapRoles()
	{
		mapClusters.values().forEach(MapCluster::clearAllMaps);
		
		allRoles.clear();
//		allPetClusters.clear();
		allEscortCars.clear();
	}

	void onTimer(long timeMillis, int timeTick)
	{
		this.updateTimeTick(timeMillis);
		this.onSecondTask(timeTick);
		Iterator<MapCluster> it = mapClusters.values().iterator();
		while (it.hasNext())
		{
			MapCluster cluster = it.next();
			cluster.onTimer(timeMillis, timeTick, this.logicTime);
		}
	}

	void onSecondTask(int timeTick)
	{
		if (timeTick > second)
		{
			updateOpenDropCfg(timeTick);
			second = timeTick;
		}
	}

	BaseMap getMap(int mapID, int mapInstanceID)
	{
		MapCluster cluster = mapClusters.get(mapID);
		if (cluster != null)
			return cluster.getMap(mapInstanceID);

		return null;
	}

	//副本
	boolean createMapCopy(int mapID, int mapInstanceID)
	{
		if (!ms.getDeployConf().getMapDelpoy().getDeployMaps().contains(mapID))
			return false;

		MapCluster cluster = mapClusters.get(mapID);
		if (cluster == null)
		{
			ms.getLogger().warn("mapcluster " + mapID + " can not find !");
			return false;
		}
		
		BaseMap map = null;
		switch (cluster.mapClusterCfg.type)
		{
		case GameData.MAP_TYPE_MAPCOPY_NORMAL:
			int openType = GameData.getInstance().getMapCopyOpenType(mapID);
			if (openType == GameData.MAPCOPY_OPEN_TYPE_PRIVATE)
				map = cluster.createPrivateMap(mapInstanceID);
			else if (openType == GameData.MAPCOPY_OPEN_TYPE_PUBLIC)
				map = cluster.createTeamMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_SECT:
			map = cluster.createSectMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_ARENA:
			map = cluster.createArenaMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_ACTIVITY:
			map = cluster.createActivityMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_SUPERARENA:
			map = cluster.createSuperArenaMap(mapID, mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_BWARENA:
			map = cluster.createBWArenaMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_PETLIFE:
			map = cluster.createPetLifeMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_CLIMBTOWER:
			map = cluster.createClimbTowerMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
			map = cluster.createForceWarMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_SECT_GROUP:
			map = cluster.createSectGroupMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_WEAPON:
			map = cluster.createWeaponMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_JUSTICE:
			map = cluster.createJusticeMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE:
			map = cluster.createDemonHoleMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_EMERGENCY:
			map = cluster.createEmergencyMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_FIGHTNPC:
			map = cluster.createFightNpcMap(mapInstanceID);
			break;
		case GameData.MAP_TYPE_MAPCOPY_TOWER_DEFENCE:
			map = cluster.createTowerDefenceMap(mapInstanceID);
			break;
		default:
			break;
		}
		
		return map != null;
	}
	
	boolean createForceWarMap(int mapID, int mapInsrance)
	{
		return createMapCopy(mapID, mapInsrance);
	}
	
	void mapcopyReady(int mapID, int mapInstanceID)
	{
		BaseMap map = getMap(mapID, mapInstanceID);
		if (map == null)
			return;

		map.syncGSMapStart();
	}

	void resetSectMap(int mapID, int mapInstanceID, Map<Integer, Integer> progress)
	{
		BaseMap map = getMap(mapID, mapInstanceID);
		if (map == null)
			return;

		if (map instanceof SectMap)
		{
			SectMap sm = SectMap.class.cast(map);
			sm.resetSectMap(progress);
		}
	}

	void resetSectGroupMap(int mapID, int mapInstanceID, Map<Integer, Integer> process, Map<Integer, Integer> killNum, Map<Integer, SBean.RoleDamageDetail> damageRank)
	{
		BaseMap map = getMap(mapID, mapInstanceID);
		if (map == null)
			return;

		if (map instanceof SectGroupMap)
		{
			SectGroupMap sm = SectGroupMap.class.cast(map);
			sm.resetSectGroupMap(process, killNum, damageRank);
		}
	}

	void resetArenaMap(int mapID, int mapInstanceID, SBean.BattleArray enemy)
	{
		BaseMap map = getMap(mapID, mapInstanceID);
		if (map == null)
			return;

		if (map instanceof ArenaMap)
		{
			ArenaMap am = ArenaMap.class.cast(map);
			am.resetFightMap(enemy, GameData.getInstance().getArenaCFGS().prepareTime);
		}
	}

	void resetBWArenaMap(int mapID, int mapInstanceID, SBean.BattleArray enemy, boolean petLack)
	{
		BaseMap map = getMap(mapID, mapInstanceID);
		if (map == null)
			return;

		if (map instanceof BWArenaMap)
		{
			BWArenaMap bam = BWArenaMap.class.cast(map);
			bam.resetBWArenaMap(enemy, petLack);
		}
	}
	
//	void resetClanMap(int mapID, int mapInstanceID, SBean.BattleArray enemy)
//	{
//		BaseMap map = getMap(mapID, mapInstanceID);
//		if (map == null)
//			return;

//		if (map instanceof ClanMap)
//		{
//			ClanMap cm = ClanMap.class.cast(map);
//			enemy.fightRole.base.roleID = -enemy.fightRole.base.roleID;
//			cm.resetFightMap(enemy, 0);
//		}
//	}

	void destoryMap(int mapID, int mapInstanceID, boolean delay)
	{
		MapCluster cluster = mapClusters.get(mapID);
		if (cluster != null && cluster.mapClusterCfg.type != GameData.MAP_TYPE_MAP_WORLD)
		{
			cluster.destoryMap(mapInstanceID, delay);
		}
	}
	
//--------------------------------------------------------------------------------------------------
	void escortCarEnterMap(SBean.DBEscortCar carInfo, int ownerID, String ownerName, int teamCarCnt, SBean.Team team, int sectID)
	{
		BaseMap m = getMap(carInfo.mapID, carInfo.mapInstance);
		if(m == null)
		{
			ms.getLogger().warn("escort car EnterMap map [" + carInfo.mapID + " , " + carInfo.mapInstance + "] not exist");
			return;
		}
		
		EscortCar car = getMapCar(ownerID);		//镖车的ID 和 ownerID 一样
		if(car != null)
		{
			ms.getLogger().warn("map [" + carInfo.mapID + " , " + carInfo.mapInstance + "] already exist escort car " + ownerID);
			return;
		}
		
		car = new EscortCar(ms, false).fromDBWitoutLock(carInfo, ownerID, ownerName, team, sectID);
		allEscortCars.put(car.getID(), car);
		m.addEscortCar(car);
		car.onUpdateTeamCarCnt(teamCarCnt);
		
		MapRole owner = m.getRole(ownerID);
		if(owner != null)
		{
			car.updateOwner(owner);
			if(owner.active)
			{
				ms.getRPCManager().sendStrPacket(ownerID, new SBean.role_escortcar(car.getEnterEscortCar()));
				car.active();
			}
		}
	}
	
	//地图传送、或者运镖任务时间到离开地图
	void escortCarLeaveMap(int carID)
	{
		EscortCar car = this.allEscortCars.remove(carID);
		if(car == null)
		{
			ms.getLogger().warn("escort car " + carID + " leave map car not exit");
			return;
		}
		
		int mapID = car.getMapID();
		int mapInstance = car.getMapInstanceID();
		
		BaseMap m = this.getMap(mapID, mapInstance);
		if(m == null)
		{
			ms.getLogger().warn("escort car " + carID + " leave map [" + mapID + ", " + mapInstance + "] map not exit");
			return;
		}
		
		if(car.active)
		{
			Set<Integer> rids = car.getRoleIDsNearBy(null);
			car.onSelfLeaveNearBy(rids, 1);
		}
		
		m.delEscortCar(carID);
		ms.getLogger().warn("escort car " + carID + " leave map [" + mapID + ", " + mapInstance + "] success");
	}

	void escortCarUpdateTeamCarCnt(int carID, int teamCarCnt)
	{
		EscortCar car = getMapCar(carID);
		if(car == null)
		{
			ms.getLogger().warn("escort car " + carID + " car update teamcar cnt car not exit");
			return;
		}
		
		car.onUpdateTeamCarCnt(teamCarCnt);
	}
	
	void updateRoleCarBehavior(int roleID, byte carOwner, byte carRobber)
	{
		MapRole role = getMapRole(roleID);
		if(role == null)
			return;
		
		role.updateCarBehavior(carOwner, carRobber);
	}
	
//--------------------------------------------------------------------------------------------------
	private void checkRobotRushSkill(SBean.FightRole fightRole)
	{
		Iterator<Integer> it = fightRole.curSkills.iterator();
		while(it.hasNext())
		{
			int skillID = it.next();
			if(skillID > 0 && GameData.getInstance().checkSkillRush(skillID))
				it.remove();
		}
		
		if(fightRole.curUniqueSkill > 0 && GameData.getInstance().checkSkillRush(fightRole.curUniqueSkill))
			fightRole.curUniqueSkill = 0;
	}
	
	void roleEnterMap(SBean.FightRole fightRole, int mapId, int mapInstance, SBean.Location location, int hp, int sp, int armorVal, Map<Integer, SBean.DBBuff> buffs, Map<Integer, SBean.FightPet> pets,
			List<Integer> petSeqs, SBean.PetHost host, SBean.PKInfo pkInfo, SBean.Team team, int curRideHorse, SBean.DBAlterState alterState, boolean isMainSpawnPos, SBean.MulRoleInfo mulRoleInfo, int dayFailedStreak, int vipLevel, int curWizardPet, byte canTakeDrop)
	{
		BaseMap map = getMap(mapId, mapInstance);
		if (map == null)
		{
			ms.getLogger().warn("roleEnterMap map [" + mapId + " , " + mapInstance + "] not exist");
			return;
		}
		
		MapRole role = getMapRole(fightRole.base.roleID);
		boolean call = false;
		long now = getMapLogicTime();
		if (role == null)
		{
			if(fightRole.base.roleID > 0)
				role = new MapRole(ms, false).fromDBWitoutLock(mapId, location, fightRole, pets, petSeqs, host, hp, sp, armorVal, buffs, pkInfo, curRideHorse, alterState, mulRoleInfo, dayFailedStreak, vipLevel, curWizardPet, canTakeDrop);
			else
			{
				checkRobotRushSkill(fightRole);
				role = new MapRoleCopy(ms, getMapLogicTime()).fromDBWitoutLock(mapId, location, fightRole, pets, petSeqs, host, hp, sp, armorVal, buffs, pkInfo, curRideHorse, alterState, mulRoleInfo, dayFailedStreak, vipLevel, curWizardPet, canTakeDrop);
			}
			role.setIsMainSpawnPos(isMainSpawnPos);		//需要在加入地图前设置（会武竞技场）
			map.addRole(role);
			role.update(hp, sp);
			role.setTeam(team);
			allRoles.put(fightRole.base.roleID, role);
			call = true;
		}
		else
			ms.getLogger().warn("roleID " + fightRole.base.roleID + " enter map exist already");

		if (map.getMapType() != GameData.MAP_TYPE_MAP_WORLD)
		{
			role.curHP = role.getMaxHP();
			role.armorMgr.setArmorValue(role.getArmorValMax(), role.getArmorValMax(), role);
			this.enterMapSetPrepareState(map, role, now);
		}
		ms.getRPCManager().syncRoleHP(role.getID(), mapId, mapInstance, role.curHP, role.getMaxHP());
		ms.getRPCManager().syncRoleArmorVal(role.getID(), mapId, mapInstance, role.getArmorValue(), role.getArmorValMax());

		if (call)
			role.summonCurPets();
		
		if (map.getMapType() == GameData.MAP_TYPE_MAPCOPY_SUPERARENA)
			role.active = true;
		else
			role.active = false;
		
		EscortCar car = map.getEscortCar(role.id);
		if(car != null)
			car.updateOwner(role);
	}
	
	void enterMapSetPrepareState(BaseMap map, MapRole role, long now)
	{
		if(map.isInPrepared(now))
		{
			role.addState(Behavior.EBPREPAREFIGHT);
			role.specialState.put(Behavior.EBPREPAREFIGHT, map.getMapPrepareTime());
		}
	}
	
	void clientRoleEnterMap(int roleID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
		{
			ms.getRPCManager().notifyGSKickRoleFromMap(roleID);
			return;
		}

		BaseMap map = role.getCurMap();
		if (map == null)
		{
			ms.getRPCManager().notifyGSKickRoleFromMap(roleID);
			return;
		}
		
		boolean active = role.active;
		role.active = true;
		
		ms.getRPCManager().mapRoleReady(roleID);
		ms.getRPCManager().sendStrPacket(roleID, new SBean.role_map_welcome_start());
		ms.getRPCManager().sendStrPacket(role.getID(), new SBean.role_update_timetick(getTimeTickDeep()));
		ms.getLogger().info("welcome role " + roleID + " to map[" + map.getMapID() + " " + map.getInstanceID() + "]  timeTick[" + timeTickToString() + "] at " + GameTime.getTimeMillis());
		if(role.getForceType() > 0)
			ms.getRPCManager().sendStrPacket(role.getID(), new SBean.sync_role_forcetype(role.getForceType()));
		
		//身世副本变身佣兵
		if(role.getPetAlterID() > 0)
			ms.getRPCManager().sendStrPacket(roleID, new SBean.role_petalter(role.getPetAlterID()));
		
		//坐骑
		if(role.getCurRideHorseID() > 0)
			ms.getRPCManager().sendStrPacket(roleID, new SBean.role_curridehorse(role.getCurRideHorseID()));
		
		if(role.mulRoleInfo.leader > 0)
		{
			MapRole leader = role.curMap.getRole(role.mulRoleInfo.leader);
			if(leader != null)
			{
				role.setNewPosition(new GVector3().reset(leader.getCurPosition()));
				switch (role.mulRoleInfo.type)
				{
				case GameData.MULROLE_TYPE_HORSE:
					ms.getRPCManager().sendStrPacket(role.id, new SBean.role_mulhorse(leader.getRoleDetail(), leader.getMulRoleMembers(), leader.getLogicPosition(), leader.getCurRotation().toVector3F()));
					break;
				case GameData.MULROLE_TYPE_STAYWITH:
					ms.getRPCManager().sendStrPacket(role.id, new SBean.role_staywith(leader.getRoleDetail(), role.getStayWithMember(), leader.getLogicPosition(), leader.getCurRotation().toVector3F()));
					break;
				default:
					break;
				}
			}
		}
		
		//神兵变身
		if(role.getWeaponLeftTime() > 0)
			ms.getRPCManager().sendStrPacket(roleID, new SBean.role_weaponlefttime(role.getWeaponLeftTime()));
		
		//任务变身
		{
			SBean.DBAlterState taskAlter = role.getAlterState();
			if(taskAlter != null && taskAlter.alterID > 0)
				ms.getRPCManager().sendStrPacket(roleID, new SBean.role_taskalter(taskAlter));
		}
		
		//技能cd
		{
			Map<Integer, Integer> cds = role.getAllSkillCD();
			if(!cds.isEmpty())
			{
				ms.getRPCManager().sendStrPacket(roleID, new SBean.role_skills_cooldown(cds));
//						ms.getLogger().debug("@@@@@@@@@ role " + roleID + " role_skills_cooldown " + cds);
			}
		}
		
		//同步内甲状态
		ms.getRPCManager().sendStrPacket(roleID, new SBean.role_armor(role.getArmorValue(), role.getArmorFreezeState(), role.getArmorWeakState()));
		
		
		ms.getRPCManager().sendStrPacket(roleID, new SBean.role_map_welcome(role.curHP, role.getCurSP(), role.isDead() ? 1 : 0, role.getFightSP(), role.getAllBuffs(), role.getPKMode(), 
												role.getNameGrade(), role.getPKState(), role.getBorrowFightPets(), role.otherPethost, this.timeTick.kdClone()));
		
		this.syncAllDrops(map, role);
		if (map instanceof PrivateMap)
		{
			role.syncClientCurPets();
			PrivateMap pm = PrivateMap.class.cast(map);
			this.syncPrivateMapEnterInfo(pm, roleID);
		}
		else
		{
			this.syncPublicMapEnterInfo(map, role, active);
			EscortCar car = map.getEscortCar(role.id);
			if(car != null)
			{
				ms.getRPCManager().sendStrPacket(role.id, new SBean.role_escortcar(car.getEnterEscortCar()));
				if(!car.active)
					car.active();
			}
			
			if (map instanceof WorldMap)
			{
				WorldMap wm = WorldMap.class.cast(map);
				ms.getRPCManager().sendStrPacket(roleID, new SBean.map_flag_info(wm.sectOverview));
				
				WeddingCar wcar = wm.getWeddingCar(role.weddingCar);
				if(wcar != null)
					ms.getRPCManager().sendStrPacket(roleID, new SBean.role_weddingcar(wcar.getEnterWeddingCar()));
			}
			
			role.syncClientCurPets();
		}
		role.syncClientRoleEnterInfo();
	}
	
	void syncPrivateMapEnterInfo(PrivateMap pm, int roleID)
	{
		SBean.MapProgress progress = new SBean.MapProgress(new ArrayList<>(), new ArrayList<>(), pm.getAllMapBuffsInfo(), pm.getAllMineralsInfo());
		for (SBean.SpawnPointProgress p : pm.privateMapProgress.values())
			progress.spawnPoint.add(p.kdClone());

		for (SBean.TrapState t : pm.getTriggedTraps().values())
			progress.trap.add(t.kdClone());

		ms.getRPCManager().sendStrPacket(roleID, new SBean.privatemap_sync_progress(progress));
	}
	
	void syncPublicMapEnterInfo(BaseMap map, MapRole role, boolean active)
	{
		int gridX = role.getCurMapGrid().getGridX();
		int gridZ = role.getCurMapGrid().getGridZ();
		EnterInfo enterInfo = map.getEntitiesNearBy(gridX, gridZ, role);
		
		if(!active && !enterInfo.roles.isEmpty())
		{
			Set<Integer> rids = new HashSet<>();
			for(int rid: enterInfo.roles.keySet())
			{
				if(rid != role.id)
				{
					MapRole r = role.curMap.getRole(rid);
					if(r != null && !r.robot)
						rids.add(rid);
				}
			}
			
			if(!rids.isEmpty())
			{
				role.filterAndAddNearByRoleIDs(rids, false);
				role.onSelfEnterNearBy(rids);
			}
		}
		
		role.syncNearByRoleOnEnter();
		role.notifyEnterInfo(enterInfo);
	}
	
	void syncAllDrops(BaseMap map, MapRole role)
	{
		List<DropItem> dropLst = new ArrayList<>();
		DropGoods dropGoods = map.getDropGoods(role.id);
		if (dropGoods != null)
		{
			dropLst = dropGoods.getDropItemList();
//			role.setStartDropID(dropGoods.getMaxDropID() + 1);
		}
		List<SBean.DropDetail> drops = new ArrayList<>();
		dropLst.forEach(d -> drops.add(new SBean.DropDetail(new SBean.DropInfo(d.dropID, d.item.id, d.item.count), d.position)));
		if(drops.size() > 0)
			ms.getRPCManager().sendStrPacket(role.id, new SBean.role_sync_alldrops(drops));
	}
	
	void roleLeaveMap(int roleID)
	{
		MapRole role = allRoles.remove(roleID);
		if (role == null)
		{
			ms.getLogger().warn("role " + roleID + " leave map not exit");
			return;
		}

		roleLeaveMapImp(role);
	}
	
	private void roleLeaveMapImp(MapRole role)
	{
		int mapID = role.getMapID();
		int mapInstanceID = role.getMapInstanceID();
		BaseMap m = this.getMap(mapID, mapInstanceID);
		if (m == null)
			return;

		role.onLeavaMap();
		EscortCar car = m.getEscortCar(role.id);
		if(car != null)
			car.updateOwner(null);
		
		m.delRole(role.id);
		ms.getLogger().info("receive role " + role.getID() + " leave map[" + mapID + " " + mapInstanceID + "] success");
	}
	
	void roleInActive(int roleID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.inActive();
	}

	void roleUpdateActive(int roleID, boolean active)
	{
		if (active)
			clientRoleEnterMap(roleID);
		else
			roleInActive(roleID);
	}

	void roleResetLocation(int roleID, SBean.Location location)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		BaseMap m = role.curMap;
		if (m == null)
			return;

		role.resetLocation(location);
	}
	
	void syncRoleTaskDrop(int roleID, Map<Integer, Integer> taskDrop)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.syncTaskDrop(taskDrop);
	}
	
	void roleSyncPetLack(int roleID, boolean petLack)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.setPetLack(petLack);
	}
	
//	void roleSyncClanBattleHurt(int roleID, boolean hurt)
//	{
//		MapRole role = getMapRole(roleID);
//		if (role == null)
//			return;
//		
//		role.clanBattleHurt = hurt && role.curMap.getMapType() == GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLE;
//		if(role.clanBattleHurt)
//			role.setClanBattleHurtHP();
//	}
	
	void roleSetPetAlter(int roleID, SBean.FightPet fightPet, SBean.PetHost petHost)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.setPetAlter(fightPet, petHost);
	}
	
	Pet createPet(SBean.FightPet fPet, MapRole owner, SBean.Location location, int seq)
	{
		Pet pet = new Pet(ms, owner.serverControl).createNew(fPet.kdClone(), owner, location, owner.getPetHost(fPet.id), seq);
		return pet;
	}

	void roleGainNewSuite(int roleID, int suiteID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onGainNewSuite(suiteID);
	}

	void roleUpdateEquip(int roleID, int wid, SBean.DBEquip equip)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateEquip(wid, equip);
	}

	void roleUpdateEquipPart(int roleID, SBean.DBEquipPart equipPart)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateEquipPart(equipPart);
	}

	void roleUpdateSealGrade(int roleID, int sealGrade)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateSealGrade(sealGrade);
	}
	
	void roleUpdateSealSkills(int roleID, Map<Integer, Integer> skills)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateSealSkills(skills);
	}
	
	void roleUpdateSkill(int roleID, SBean.DBSkill skill)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateSkill(skill);
	}

	void roleUpdateCurSkills(int roleID, List<Integer> curSkills)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateCurSkill(curSkills);
	}

	void roleUpdateCurUniqueSkill(int roleID, int curUniqueSkill)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateCurUniqueSkill(curUniqueSkill);
	}
	
	void roleUpdateSpirit(int roleID, SBean.DBSpirit spirit)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateSpirit(spirit);
	}

	void roleUpdateCurSpirits(int roleID, Set<Integer> curSpirits)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateCurSpirit(curSpirits);
	}

	void roleUpdateBuff(int roleID, SBean.DBBuff buff)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateBuff(buff);
	}

	void roleAddBuff(int roleID, int buffID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onAddBuff(buffID);
	}

	void roleUpdateLevel(int roleID, int level)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateLevel(level);
	}

	void roleAddHp(int roleID, int hp)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		if (!role.canAddHp())
			return;

		role.onAddHp(hp);
	}

	void petAddHp(int roleID, int pid, int hp)
	{
//		PetCluster cluster = this.getPetCluster(roleID);
		MapRole role = this.getMapRole(roleID);
		if(role == null)
			return;
		
		Pet pet = role.curMap.getPet(roleID, pid);
		if(pet == null || pet.isDead())
			return;
		
		pet.onAddHp(hp);
	}
	
	void roleUpdateWeapon(int roleID, SBean.DBWeapon weapon)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateWeapon(weapon);
	}

	void roleChangeCurWeapon(int roleID, int curWeapon)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onChangeWeapon(curWeapon);
	}

	void roleSetWeaponForm(int roleID, int weaponID, byte form)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.setWeaponForm(weaponID, form);
	}
	
	void roleStartMine(int roleID, int mineID, int mineInstance)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onStartMineral(mineID, mineInstance);
	}

	void roleRevive(int roleID, boolean fullHp)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onRoleRevive(fullHp);
	}

	void roleUpdatePet(int roleID, SBean.FightPet fightPet)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdatePet(fightPet);
	}

	void roleChangeCurPets(int roleID, Map<Integer, SBean.FightPet> pets)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.changeCurFightPets(pets);
	}

	void roleUnSummonFightPets(int roleID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.unSummonFightPets(false);
	}

	void roleUpdateCurPetSpirit(int roleID, int petID, int index, SBean.PetSpirit spirit)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateCurPetSpirit(petID, index, spirit);
	}
	
	void roleUpdateTeam(int roleID, SBean.Team team)
	{
		MapRole role = getMapRole(roleID);
		if (role != null)
		{
			role.setTeam(team);

			if (team != null && team.id != 0) 
				role.dissolveCurPets();							//加入队伍
			else
				role.changeCurFightPetsImpl();		//离开队伍
		}
		
		EscortCar car = getMapCar(roleID);
		if(car != null)
			car.setTeam(team);
	}

	void roleUpdateSectBrief(int roleID, SBean.SectBrief sectBrief)
	{
		MapRole role = getMapRole(roleID);
		if (role != null)
			role.onUpdateSectBrief(sectBrief);
	}

	void roleUpdateSectAura(int roleID, int auraID, int auraLvl)
	{
		MapRole role = getMapRole(roleID);
		if (role != null)
			role.onUpdateSectAura(auraID, auraLvl);
	}

	void roleResetSectAuras(int roleID, Map<Integer, Integer> sectAuras)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onResetSectAuras(sectAuras);
	}

//	void roleUpdateClanDiziTangAttr(int roleID, SBean.ClanOwnerAttriAddition attr)
//	{
//		MapRole role = getMapRole(roleID);
//		if (role == null)
//			return;
//		
//		role.onUpdateClanDiziTangAttr(attr);
//	}
	
	void roleUpdatePKInfo(int roleID, int pkMode, int pkValue)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.updateAttackMode(pkMode);
		role.setPKValue(pkValue);
	}

	void roleUpdateCurDIYSkill(int roleID, SBean.DBDIYSkillData curDIYSkill)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onUpdateCurDIYSkill(curDIYSkill);
	}

	void roleUpdateTransformInfo(int roleID, byte transformLevel, byte BWType)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateTransformInfo(transformLevel, BWType);
	}

	void roleUpdateHorseInfo(int roleID, SBean.HorseInfo info)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onUpdateHorse(info);
	}

	void roleUpdateCurUseHorse(int roleID, int hid)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onUpdateCurUseHorse(hid);
	}
	
	void roleUpdateHorseSkill(int roleID, int skillID, int skillLvl)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateHorseSkill(skillID, skillLvl);
	}
	
	public void roleUpdateWeaponSkill(int roleID, int weaponID, List<Integer> skills)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateWeaponSkill(weaponID, skills);
	}
	
	public void roleUpdatePetSkill(int roleID, int petID, List<Integer> skills)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdatePetSkill(petID, skills);
	}
	
	public void roleUpdatePerfectDegree(int roleID, int perfectDegree)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdatePerfectDegree(perfectDegree);
	}
	
	public void roleUpdateHeirloomDisplay(int roleID, boolean display)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateHeirloomDisplay(display);
	}
	
	public void roleUpdateMarriageSkillInfo(int roleID, Map<Integer, Integer> skills, int partnerId)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateMarriageSkillInfo(skills, partnerId);
	}
	
	public void roleUpdateMarriageSkillLevel(int roleID, int skillId, int skillLevel)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateMarriageSkillLevel(skillId, skillLevel);
	}
	
	public void roleUpdateVipLevel(int roleID, int vipLevel)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateVipLevel(vipLevel);
	}
	
	public void roleUpdateWizardPet(int roleID, int petId)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateCurWizardPet(petId);
	}
	
	public void roleUpdateSpecialCardAttr(int roleID, Map<Integer, Integer> attrs)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateSpecialCardAttr(attrs);
	}
	
	public void roleShowProps(int roleID, int propID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.showProps(propID);
	}
	
	public void roleGMCommend(int roleID, String iType, int iArg1, int iArg2, int iArg3, String sArg)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.gmCommond(iType, iArg1, iArg2, iArg3, sArg);
	}
	
	public void roleMarriageLevelChange(int roleID, int newLevel)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onMarriageLevelChange(newLevel);
	}
	
	public void roleDMGTransferPointLvlsUpdate(int roleID, Map<Integer, Integer> pointLvls)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onDMGTransferPointLvlsUpdate(pointLvls);
	}
	
	public void roleUpdateWeaponTalent(int roleID, int weaponID, List<Integer> talents)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateWeaponTalent(weaponID, talents);
	}

	public void roleWeaponOpen(int roleID, int weaponID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onWeaponOpen(weaponID);
	}
	
	public void roleUpdateItemProps(int roleID, Map<Integer, Integer> props)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		role.onUpdateItemProps(props);
	}

	void roleChangeHorseShow(int roleID, int hid, int showID)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onChangeHorseShow(hid, showID);
	}

	void roleUpdateMedal(int roleID, int medal, byte state)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onUpdateMedal(medal, state);
	}

	void roleUpWearFashion(int roleID, int type, int fashionID, int isShow)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onUpWearFashion(type, fashionID, isShow);
	}

	void roleUpdateAlterState(int roleID, SBean.DBAlterState alterState)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;

		role.onUpdateAlterState(alterState);
	}

	void roleUpdateGrasp(int roleID, int graspID, int level)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateGrasp(graspID, level);
	}
	
	void roleUpdateRareBook(int roleID, int bookID, int level)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateRareBook(bookID, level);
	}
	
	void roleUpdateTitle(int roleID, int titleID, boolean add)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateTitle(titleID, add);
	}
	
	void roleUpdateCurTitle(int roleID, int titleID, int titleType)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdateCurTitle(titleID, titleType);
	}
	
	void roleUpdatePetAchieves(int roleID, Set<Integer> achieves)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onUpdatePetAchieves(achieves);
	}
	
	boolean roleUseItemSkill(int roleID, int itemID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return false;
		
		if(role.isInPrivateMap())
			return false;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT))
			return false;
		
		return role.useItemSkill(itemID, pos, rotation, targetID, targetType, ownerID, timeTick);
	}
	
	void roleRename(int roleID, String newName)
	{
		MapRole role = getMapRole(roleID);
		if (role == null)
			return;
		
		role.onRoleRename(newName);
	}
	
	void syncRoleCurRideHorse(int roleID, int horseID)
	{
		MapRole role = getMapRole(roleID);
		if(role == null)
			return;
		
		role.syncRoleCurRideHorse(horseID);
	}
	
	void roleUpdateMulHorse(int leaderID, int pos, int memberID)
	{
		MapRole leader =  getMapRole(leaderID);
		if(leader == null)
			return;
		
		leader.onUpdateMulHorse(pos, memberID);
	}
	
	void roleUpdateStayWith(int leaderID, SBean.MulRoleInfo mulRoleInfo)
	{
		MapRole leader =  getMapRole(leaderID);
		if(leader == null)
			return;
		
		leader.onUpdateStayWith(mulRoleInfo);
	}
	
	void roleChangeCurArmor(int roleID, SBean.ArmorFightData curArmor)
	{
		MapRole role = getMapRole(roleID);
		if(role == null)
			return;
		
		role.onChangeCurArmor(curArmor);
	}
	
	void roleUpdateArmorLevel(int roleID, int armorLevel)
	{
		MapRole role = getMapRole(roleID);
		if(role == null)
			return;
		
		role.onUpdateArmorLevel(armorLevel);
	}
	
	void roleUpdateArmorRank(int roleID, int armorRank)
	{
		MapRole role = getMapRole(roleID);
		if(role == null)
			return;
		
		role.onUpdateArmorRank(armorRank);
	}
	
	void roleUpdateArmorRunes(int roleID, int index, List<Integer> runes)
	{
		MapRole role = getMapRole(roleID);
		if(role == null)
			return;
		
		role.onUpdateArmorRune(index, runes);
	}
	
	void roleUpdateArmorTalent(int roleID, Map<Integer, Integer> talentPoint)
	{
		MapRole role = getMapRole(roleID);
		if(role == null)
			return;
		
		role.onUpdateArmorTalent(talentPoint);
	}
	
	void roleStartMarriageParade(int mapID, int mapInstance, int carID, SBean.RoleOverview man, SBean.RoleOverview woman)
	{
		BaseMap map = this.getMap(mapID, mapInstance);
		if(map == null)
			return;
		
		if(map instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(map);
			WeddingCar car =  wm.createWeddingCar(carID, man, woman);
			MapRole roleMan = wm.getRole(man.id);
			if(roleMan != null)
			{
				roleMan.onCreateWeddingCar(car);
			}
			
			MapRole roleWoman = wm.getRole(woman.id);
			if(roleWoman != null)
			{
				roleWoman.onCreateWeddingCar(car);
			}
		}
	}
	
	void roleStartMarriageBanquet(int roleID, int mapID, int mapInstance, int banquet)
	{
		BaseMap map = this.getMap(mapID, mapInstance);
		if(map == null)
			return;
		
		if(map instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(map);
			wm.createMrgBanquet(roleID, banquet);
		}
	}
	
	void roleSpawnSceneMonster(int mapID, int mapInstance, int roleID, int pointID)
	{
		BaseMap map = getMap(mapID, mapInstance);
		if(map == null)
			return;
		
		if(map instanceof WorldMap)
		{
			MapRole role = map.getRole(roleID);
			if(role == null)
				return;
			
			SBean.SceneSpawnPointCFGS pointCfg = GameData.getInstance().getSceneSpawnPointCFGS(pointID);
			if(pointCfg == null || pointCfg.mapID != mapID)
				return;
			
			WorldMap wm = WorldMap.class.cast(map);
			for (int i = 0; i < pointCfg.count; i++)
			{
				if(GameData.checkRandom(pointCfg.odds))
				{
					Monster monster = wm.spawnSceneMonster(pointCfg, GameRandom.getRandFloat(0, (float) (Math.PI * 2.f)), roleID);
					role.addSceneTrigMonster(monster.getID());
				}
			}
		}
	}
	
	void roleClearSceneMonster(int roleID, int monsterID)
	{
		MapRole role = this.getMapRole(roleID);
		if(role == null)
			return;
		
		role.clearSceneTrigMonsters(monsterID);
	}
	
	void roleRedNamePunish(int roleID)
	{
		MapRole role = this.getMapRole(roleID);
		if(role == null)
			return;
		
		role.redNamePunish();
	}
	
	void createWorldMapBoss(int mapID, int mapInstanceID, int bossID, int seq, int curHP)
	{
		BaseMap map = this.getMap(mapID, mapInstanceID);
		if (map == null)
			return;

		map.createWorldBoss(bossID, seq, curHP);
	}

	void destroyWorldMapBoss(int mapID, int mapInstanceID, int bossID)
	{
		BaseMap map = this.getMap(mapID, mapInstanceID);
		if (map == null)
			return;
		
		map.destroyWorldBoss(bossID);
	}
	
	void worldBossPop(int mapID, int mapInstance, int bossID, int index)
	{
		BaseMap map = this.getMap(mapID, mapInstance);
		if (map == null)
			return;
		
		if(map instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(map);
			wm.worldBossPop(bossID, index);
		}
	}
	
	void rolePickUpResult(int roleID, Set<Integer> dropIDs, boolean success)
	{
		MapRole role = this.getMapRole(roleID);
		if(role == null)
			return;
		
		role.pickUpResult(dropIDs, success);
	}
	
	void createWorldMapSuperMonster(int mapID, int superMonsterID, int standTime)
	{
		SBean.WorldMonsterCFGS cfg = GameData.getInstance().getWorldSuperMonster(superMonsterID);
		if(cfg == null)
			return;
		
		MapCluster cluster = mapClusters.get(cfg.mapID);
		if(cluster == null)
			return;
		
		cluster.createWorldSuperMonster(cfg, standTime);
	}
	
	void createWorldMapFlag(int mapID, SBean.Vector3 flagPoint, int flagId, List<Integer> monsterPointId, SBean.MapFlagSectOverView sect)
	{
		BaseMap map = getMap(mapID, GameData.getInstance().getFlagBattleCFGS().activityLine);
		if (map == null)
			return;
		if (map instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(map);
			wm.createWorldMapFlag(flagPoint, flagId, monsterPointId, sect);
		}
	}

	void updateMapFlagInfo(int mapID, MapFlagSectOverView sect)
	{
		BaseMap map = getMap(mapID, GameData.getInstance().getFlagBattleCFGS().activityLine);
		if (map == null)
			return;
		if (map instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(map);
			wm.updateMapFlagInfo(sect);
		}
	}
	
	void createWorldMineral(int mapID, int worldMineralID, int standTime)
	{
		SBean.WorldMineralCFGS cfg = GameData.getInstance().getWorldMineral(worldMineralID);
		if(cfg == null)
			return;
		
		MapCluster cluster = mapClusters.get(cfg.mapID);
		if(cluster == null)
			return;
		
		cluster.createWorldMineral(cfg, standTime);
	}
	
	void initWorldBoss(SBean.DBBoss dbBoss)
	{
		for (Map.Entry<Integer, SBean.DBBossState> e : dbBoss.bosses.entrySet())
		{
			int bossID = e.getKey();
			SBean.WorldBossCFGS cfg = GameData.getInstance().getWorldBossCFGS(bossID);
			if (cfg == null)
				continue;

			SBean.DBBossState state = e.getValue();
			if(state.curHp > 0)
			{
				ms.getLogger().debug("initWorldBoss world map [" + cfg.base.mapID + " , " + BossManager.BOSS_CREATE_WORLD_LINE + "] create boss " + bossID + " seq " + state.seq + " curHp " + state.curHp);
				createWorldMapBoss(cfg.base.mapID, BossManager.BOSS_CREATE_WORLD_LINE, bossID, state.seq, state.curHp);
			}
		}
	}
	
	void initWorldMapFlag(Map<Integer, SBean.MapFlagInfo> mapFlags)
	{
		SBean.FlagBattleCFGS flagCFGS = GameData.getInstance().getFlagBattleCFGS();
		for (SBean.FlagBattleMapCFGS flag : flagCFGS.flags.values())
		{
			SBean.MapFlagSectOverView cursect = new SBean.MapFlagSectOverView(0, "", 0);
			if (mapFlags.containsKey(flag.mapId))
			{
				cursect = mapFlags.get(flag.mapId).curSect;
			}
			ms.getLogger().debug("initWorldMapFlag world map [" + flag.mapId + " , " + 1 + "] create flag curSectId " + cursect.sectId + " , cursectName " + cursect.sectName);
			createWorldMapFlag(flag.mapId, flag.flagPoint, flag.flagId, flag.monsterPointId, cursect);
		}
	}

	void createRobotHero(int mapID, int mapInstance, SBean.FightRole robot, Map<Integer, SBean.FightPet> curFightPets, int spawnPoint)
	{
		BaseMap map = this.getMap(mapID, mapInstance);
		if(map == null)
			return;
		
		if(map instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(map);
			wm.createRobotHero(robot, curFightPets, spawnPoint);
		}
	}
	
	void destroyRobotHero(int mapID, int mapInstance, int roleID)
	{
		BaseMap map = this.getMap(mapID, mapInstance);
		if(map == null)
			return;
		
		if(map instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(map);
			wm.destroyRobotHero(roleID);
		}
	}
	
	void createStele(int steleType, int index, int remainTimes)
	{
		SBean.SteleMineralTypeCFGS typeCfg = GameData.getInstance().getSteleMineralTypeCFGS(steleType);
		if(typeCfg == null)
			return;
		
		SBean.SteleMineralCFGS mCfg = GameData.getSteleMineralCFGS(typeCfg, index);
		if(mCfg == null)
			return;
		
		MapCluster mc = this.mapClusters.get(mCfg.mapLocation.mapID);
		if(mc == null)
			return;
		
		mc.createStele(steleType, index, mCfg, remainTimes);
	}
	
	void destroyStele(int steleType, int index)
	{
		SBean.SteleMineralTypeCFGS typeCfg = GameData.getInstance().getSteleMineralTypeCFGS(steleType);
		if(typeCfg == null)
			return;
		
		SBean.SteleMineralCFGS mCfg = GameData.getSteleMineralCFGS(typeCfg, index);
		if(mCfg == null)
			return;
		
		MapCluster mc = this.mapClusters.get(mCfg.mapLocation.mapID);
		if(mc == null)
			return;
		
		mc.destroyStele(steleType, index);
	}
	
	void addJusticeNpc(int posIndex)
	{
		SBean.MapLocation location = GameData.getInstance().getJusticeNpcPoint(posIndex);
		if (location == null)
			return;
		MapCluster cluster = mapClusters.get(location.mapID);
		if (cluster != null)
			cluster.addJusticeNpc(location.location);
	}
	
	void delJusticeNpc(int posIndex)
	{
		SBean.MapLocation location = GameData.getInstance().getJusticeNpcPoint(posIndex);
		if (location == null)
			return;
		MapCluster cluster = mapClusters.get(location.mapID);
		if (cluster != null)
			cluster.delJusticeNpc();
	}
	
	void syncEmergencyLastTime(int mapId, int mapInstanceId, int lastTime)
	{
		BaseMap map = getMap(mapId, mapInstanceId);
		if (map == null)
			return;
		if (map instanceof EmergencyMap)
		{
			EmergencyMap em = EmergencyMap.class.cast(map);
			em.setTimeout(lastTime);
		}
	}
	
	void addTimerEvent(Runnable runnable)
	{
		if (this.taskExecutor.isShutdown())
			return;
		
		stat.timerEventCounter.incrementAndGet();
		this.taskExecutor.execute(() -> 
				{
					long startTime = GameTime.getTimeMillis();
					runnable.run();
					stat.completedEventCounter.incrementAndGet();
					long endTime = GameTime.getTimeMillis();
					int cost = (int)(endTime - startTime);
					if (cost > 100)
						ms.getLogger().warn("timer task cost " + cost + " too long!");
				});
	}
	
	void addServerEvent(Runnable runnable)
	{
		if (this.taskExecutor.isShutdown())
			return;
		
		stat.serverEventCounter.incrementAndGet();
		this.taskExecutor.execute(() -> 
			{
				long startTime = GameTime.getTimeMillis();
				runnable.run();
				stat.completedEventCounter.incrementAndGet();
				long endTime = GameTime.getTimeMillis();
				int cost = (int)(endTime - startTime);
				if (cost > 100)
					ms.getLogger().warn("server task cost " + cost + " too long!");
			});
	}
	
	void addClientEvent(Runnable runnable)
	{
		if (this.taskExecutor.isShutdown())
			return;
		
		stat.clientEventCounter.incrementAndGet();
		this.taskExecutor.execute(() -> 
			{
				long startTime = GameTime.getTimeMillis();
				runnable.run();
				stat.completedEventCounter.incrementAndGet();
				long endTime = GameTime.getTimeMillis();
				int cost = (int)(endTime - startTime);
				if (cost > 100)
					ms.getLogger().warn("client task cost " + cost + " too long!");
			});
	}

	void resetMap()
	{
		ms.getLogger().info("reset map  ...");
		try
		{
			clearAllMapRoles();
		}
		catch (Exception ex)
		{
			ms.getLogger().warn(ex.getMessage(), ex);
		}
	}
	
	void clearGlobalMapZoneRolesOnSessionClose(int session, Set<Integer> zones)
	{
		ms.getLogger().info("clear zones " + zones + " roles on session " + session + " close ...");
		try
		{
			Iterator<MapRole> it = this.allRoles.values().iterator();
			while(it.hasNext())
			{
				MapRole role = it.next();
				if(zones.contains(GameData.getZoneIdFromRoleId(role.id)))
				{
					roleLeaveMapImp(role);
					it.remove();
				}
			}
		}
		catch (Exception ex)
		{
			ms.getLogger().warn(ex.getMessage(), ex);
		}
	}
	
	void syncDoubleDropCfgs(List<SBean.DoubleDropCfg> dounleCfgs)
	{
		this.allDounleCfgs = dounleCfgs;
	}

	void syncExtraDropCfgs(List<SBean.ExtraDropCfg> extraCfgs)
	{
		this.allExtraCfgs = extraCfgs;
	}

	void syncWorldNum(int worldNum, Map<Integer, Integer> extralWorldNum)
	{
		for (int mapID: ms.getDeployConf().getMapDelpoy().getDeployMaps())
		{
			MapCluster cluster = this.mapClusters.get(mapID);
			if(cluster == null)
			{
				ms.getLogger().warn("mapcluster " + mapID + " can not find!");
				continue;
			}
			
			SBean.WorldMapCFGS worldMapCfg = GameData.getInstance().getWorldMapCFGS(mapID);
			if (worldMapCfg != null)
			{
				if(worldMapCfg.worldNum > 0)
					cluster.syncWorldNum(worldMapCfg.worldNum, 0);
				else
					cluster.syncWorldNum(worldNum, extralWorldNum.getOrDefault(mapID, 0));
			}
		}
	}
	
	private void updateOpenDropCfg(int timeTick)
	{
		this.openDounleCfg = null;
		for (SBean.DoubleDropCfg cfg : this.allDounleCfgs)
		{
			if (cfg.time.startTime <= timeTick && cfg.time.endTime >= timeTick)
			{
				this.openDounleCfg = cfg;
				break;
			}
		}

		this.openExtraCfg = null;
		for (SBean.ExtraDropCfg cfg : this.allExtraCfgs)
		{
			if (cfg.time.startTime <= timeTick && cfg.time.endTime >= timeTick)
			{
				this.openExtraCfg = cfg;
				break;
			}
		}
	}

	SBean.DoubleDropCfg getCurrentDoubleDropConfig()
	{
		return openDounleCfg;
	}

	SBean.ExtraDropCfg getCurrentExtralDropConfig()
	{
		return this.openExtraCfg;
	}

	///////////////////////////////////////////////////////world monster //////////////////////////////////////////////////////////////
//	private void tryCreateSuperMonster(int timeTick)
//	{
//		Map<Integer, SBean.WorldMonsterCFGS> allSuperMonsters = GameData.getInstance().getAllWorldSuperMonsters();
//		int weekDay = GameTime.getWeekday();
//		boolean update = false;
//		for (SBean.WorldMonsterCFGS cfg : allSuperMonsters.values())
//		{
//			if (!ms.getDeployConf().getMapDelpoy().getDeployMaps().contains(cfg.mapID))
//				continue;
//
//			if (!cfg.openDay.contains(weekDay))
//				continue;
//
//			List<Integer> refreshTime = cfg.refreshTime;
//			int lastRefreshTime = GameData.getLastRefreshTime(timeTick, refreshTime);
//			if (timeTick > lastRefreshTime && lastRefreshTime > this.lastCreateSuperMonsterTime)
//			{
//				MapCluster cluster = mapClusters.get(cfg.mapID);
//				if(cluster == null)
//					continue;
//				
//				cluster.createWorldSuperMonster(cfg);
//				update = true;
//			}
//		}
//
//		if (update)
//			this.lastCreateSuperMonsterTime = timeTick;
//	}

	/////////////////////////////////////////////////////world monster end////////////////////////////////////////////////////////////
	SBean.TimeTick getTimeTick()
	{
		return this.timeTick;
	}

	String timeTickToString(SBean.TimeTick timeTick)
	{
		return timeTick.tickLine + " , " + timeTick.outTick;
	}
	
	String timeTickToString()
	{
		return timeTick.tickLine + " , " + timeTick.outTick;
	}
	
	SBean.TimeTick getTimeTickDeep()
	{
		return this.timeTick.kdClone();
	}
	
	long getCurMapLogicTime()
	{
		return GameTime.getTimeMillis() - this.startTime;
	}
	
	long getMapLogicTime()
	{
		return this.logicTime;
	}

	SBean.TimeTick setTimeTick()
	{
		long now = GameTime.getTimeMillis();
		if (startTime > now)
			startTime = now;

		this.logicTime = now - startTime;
		int interval = GameData.getInstance().getCommonCFG().engine.interval;
		return new SBean.TimeTick((int) ((now - startTime) / interval), (int) ((now - startTime) % interval));
	}

	void updateTimeTick(long timeMillis)
	{
		if (startTime > timeMillis)
			startTime = timeMillis;

		int interval = GameData.getInstance().getCommonCFG().engine.interval;
		this.logicTime = timeMillis - startTime;
		this.timeTick = new SBean.TimeTick((int) (this.logicTime / interval), (int) (this.logicTime % interval));
	}

	MapRole getMapRole(int roleID)
	{
		return allRoles.get(roleID);
	}
	
	void delMapRole(int roleID)
	{
		this.allRoles.remove(roleID);
	}
	
//	PetCluster getPetCluster(int roleID)
//	{
//		return allPetClusters.get(roleID);
//	}

	EscortCar getMapCar(int carID)
	{
		return allEscortCars.get(carID);
	}
	
	AtomicInteger getNextMonsterID()
	{
		return nextMonsterID;
	}

	AtomicInteger getNextNpcID()
	{
		return this.nextNpcID;
	}

	AtomicInteger getNextMineralID()
	{
		return this.nextMineralID;
	}

	AtomicInteger getNextTrapID()
	{
		return this.nextTrapID;
	}

	AtomicInteger getNextBlurID()
	{
		return this.nextBlurID;
	}

	AtomicInteger getNextMapBuffID()
	{
		return this.nextMapBuffID;
	}

	AtomicInteger getNextSkillEntityID()
	{
		return this.nextSkillEntityID;
	}
	
	int getNextWeddingCarID()
	{
		this.nextWeddingCarID++;
		return this.nextWeddingCarID;
	}
	
	MapStat getStats()
	{
		return this.stat;
	}
	
	class MrgBanquet
	{
		private int second;
		private int lastRefreshTime;
		
		int roleID;
		private final SBean.MarriageBanquetCFGS banquetCfg;
		private final int endTime;
		
		MrgBanquet(int roleID, SBean.MarriageBanquetCFGS banquetCfg, int endTime)
		{
			this.roleID = roleID;
			this.banquetCfg = banquetCfg;
			this.endTime = endTime;
		}
		
		boolean onTimer(int timeTick)
		{
			if(timeTick > second)
			{
				tryRefresh(timeTick);
				second = timeTick;
			}
			
			return timeTick > endTime;
		}
		
		private void tryRefresh(int timeTick)
		{
			if(banquetCfg.refreshInterval <= 0)
				return;
			
			if(timeTick < lastRefreshTime + banquetCfg.refreshInterval)
				return;
				
			lastRefreshTime = timeTick;
		}
	}
	
	public class MapStat implements MapStatMBean
	{
		AtomicLong completedEventCounter = new AtomicLong(0);
		AtomicLong timerEventCounter = new AtomicLong(0);
		AtomicLong clientEventCounter = new AtomicLong(0);
		AtomicLong serverEventCounter = new AtomicLong(0);
		
		long profileTime;
		long profileInterval;
		long lastProfileCompletedEventCount;
		long lastProfileTimerEventCount;
		long lastProfileClientEventCount;
		long lastProfileServerEventCount;
		int curProfileCompletedTaskCount;
		int curProfileUncompletedTaskCount;
		int curProfileProduceTimerTaskCount;
		int curProfileProduceClientTaskCount;
		int curProfileProduceServerTaskCount;
		public MapStat()
		{
			profileTime = GameTime.getTimeMillis();
		}
		
		public void start()
		{
			try
			{
				ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName("i3k.gmap:type=MapStat"));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		@Override
		public int getRoleCount()
		{
			return allRoles.size();
		}
		
//		@Override
//		public int getPetCount()
//		{
//			return allPetClusters.size();
//		}				
		
		@Override
		public int getSamplinginterval()
		{
			return (int)profileInterval;
		}
		
		@Override
		public int getCompletedTaskCount()
		{
			return curProfileCompletedTaskCount;
		}
		
		@Override
		public int getUncompletedTaskCount()
		{
			return curProfileUncompletedTaskCount;
		}
		
		@Override
		public int getReceiveTimerTaskCount()
		{
			return curProfileProduceTimerTaskCount;
		}
		
		@Override
		public int getReceiveClientTaskCount()
		{
			return curProfileProduceClientTaskCount;
		}
		
		@Override
		public int getReceiveServerTaskCount()
		{
			return curProfileProduceServerTaskCount;
		}
		
		public void profile()
		{
			long now = GameTime.getTimeMillis();
			profileInterval = now - profileTime;
			profileTime = now;
			if (profileInterval < 100)
				ms.getLogger().warn("profile timer tick is less than 100ms");
			if (profileInterval == 0)
				profileInterval = 1;
			
			long curProfileCompletedEventCount = completedEventCounter.get();
			long curProfileTimerEventCount = timerEventCounter.get();
			long curProfileClientEventCount = clientEventCounter.get();
			long curProfileServerEventCount = serverEventCounter.get();
			
			curProfileCompletedTaskCount = (int)(curProfileCompletedEventCount - lastProfileCompletedEventCount);
			curProfileUncompletedTaskCount = (int)(-curProfileCompletedEventCount + curProfileTimerEventCount + curProfileClientEventCount + curProfileServerEventCount);
			curProfileProduceTimerTaskCount = (int)(curProfileTimerEventCount - lastProfileTimerEventCount);
			curProfileProduceClientTaskCount = (int)(curProfileClientEventCount - lastProfileClientEventCount);
			curProfileProduceServerTaskCount = (int)(curProfileServerEventCount - lastProfileServerEventCount);
		
			lastProfileCompletedEventCount = curProfileCompletedEventCount;
			lastProfileTimerEventCount = curProfileTimerEventCount;
			lastProfileClientEventCount = curProfileClientEventCount;
			lastProfileServerEventCount = curProfileServerEventCount;
			
			//ms.getLogger().info(String.format("SI=%d, ctask=%d, ucttask=%d, ttask=%d, ctask=%d, stask=%d", 
			//		profileInterval, curProfileCompletedTaskCount, curProfileUncompletedTaskCount, 
			//		curProfileProduceTimerTaskCount, curProfileProduceClientTaskCount, curProfileProduceServerTaskCount));
		}
	}
	
	public MapStat stat = new MapStat();
	private ExecutorService taskExecutor;
	private AtomicInteger nextMonsterID = new AtomicInteger();
	private AtomicInteger nextNpcID = new AtomicInteger();
	private AtomicInteger nextMineralID = new AtomicInteger();
	private AtomicInteger nextTrapID = new AtomicInteger();
	private AtomicInteger nextBlurID = new AtomicInteger();
	private AtomicInteger nextMapBuffID = new AtomicInteger();
	private AtomicInteger nextSkillEntityID = new AtomicInteger();
	private int nextWeddingCarID;
	final MapServer ms;
	

	private ConcurrentMap<Integer, MapRole> allRoles = new ConcurrentHashMap<>(); //<rid, MapRole>
//	private ConcurrentMap<Integer, PetCluster> allPetClusters = new ConcurrentHashMap<>(); //<roleID, PetCluster>
	private ConcurrentMap<Integer, MapCluster> mapClusters = new ConcurrentHashMap<>(); //<mapID,MapCluster>
	private ConcurrentMap<Integer, EscortCar> allEscortCars = new ConcurrentHashMap<>();	//<carID, EscortCar>  , carID == roleID
	private int lastCreateSuperMonsterTime;

	private List<SBean.DoubleDropCfg> allDounleCfgs = new ArrayList<>();
	private SBean.DoubleDropCfg openDounleCfg;
	private List<SBean.ExtraDropCfg> allExtraCfgs = new ArrayList<>();
	private SBean.ExtraDropCfg openExtraCfg;

	private int second;
	private long startTime;
	private SBean.TimeTick timeTick;
	private long logicTime;

}
