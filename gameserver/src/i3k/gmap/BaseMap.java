package i3k.gmap;

import i3k.SBean;
import i3k.SBean.MapClusterCFGS;
import i3k.SBean.RoleDamageDetail;
import i3k.SBean.SuperArenaTypeCFGS;
import i3k.gmap.BaseRole.Buff;
import i3k.gmap.BaseRole.EnterInfo;
import i3k.gmap.BaseRole.LeaveInfo;
import i3k.gmap.DropGoods.DropItem;
import i3k.gmap.MapCluster.MapStele;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 副本的销毁gs通知
 * 
 */

abstract public class BaseMap
{
	private static final int MAX_TIME_TICK = 1;
	private static final int MAPCOPY_TIMEOUT_DELAY_DESTROY_TIME = 120;	//second
	private static final int DAMAGE_RANK_SYNC_INTERVAL = 2;	//second
	
	final MapServer ms;
	final int mapID;
	final int mapInstanceID;
	final SBean.MapClusterCFGS mapClusterCfg;
	int mapMinX;
	int mapMinZ;
	int mapMaxX;
	int mapMaxZ;
	long randomTick;
	long timeout;
	
	private int nextDropID;
	
	
	ConcurrentMap<Integer, MapRole> mapRoles = new ConcurrentHashMap<>();
	List<Integer> mapRolesRemoveCache = new ArrayList<>();
	
	ConcurrentMap<Integer, PetCluster> mapPetCluster = new ConcurrentHashMap<>(); //<roleID, PetCluster>
	ConcurrentMap<Integer, MapBuff> mapMapBuffs = new ConcurrentHashMap<>();
	ConcurrentMap<Integer, Mineral> mapMinerals = new ConcurrentHashMap<>();
	List<Integer> mapMineralsRemoveCache = new ArrayList<>();

	boolean isMapAlreadyFinish = false;
	int killMonsters;
	Map<Integer, Integer> mapKilledBosses = new HashMap<>();
	Map<Integer, Integer> roleDeads = new HashMap<>();
	Map<Integer, Integer> roleKills = new HashMap<>();
	Map<Integer, DropGoods> mapDropGoods = new HashMap<>(); //<roleID, DropGoods>
	int mostKillRole;
	long prepareTime;
	int second;
	
	abstract boolean isMapFinish();

	abstract void processMapFinish();

	abstract void checkTimeOut();

	abstract boolean isAllMonsterKilled();

	abstract void syncGSMapStart();
	abstract int getMapType();
	abstract boolean filterRole();
	
	BaseMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms)
	{
		this.mapID = cfg.id;
		this.mapInstanceID = mapInstanceID;
		this.ms = ms;

		this.mapClusterCfg = cfg;
		this.randomTick = GameRandom.getRandInt(0, MAX_TIME_TICK);
	}

	BaseMap start()
	{
		this.loadMapBuff();
		this.loadMinerals();

		return this;
	}
	
	int getNextDropID()
	{
		this.nextDropID++;
		if(this.nextDropID < 0)
			this.nextDropID = 1;
		
		return this.nextDropID;
	}
	
	void clearAllRoles()
	{
		mapRoles.clear();
		mapPetCluster.clear();
	}
	
	MapGrid getGrid(int gridX, int gridZ)
	{
		return null;
	}

	public int calcGridCoordinateX(int x)
	{
		return 0;
	}

	public int calcGridCoordinateZ(int z)
	{
		return 0;
	}
	
	public GVector3 fixPos(GVector3 pos, BaseRole entity)
	{
		if (pos.x + entity.getRadius() > this.mapMaxX)
		{
			pos.x = this.mapMaxX - entity.getRadius();
		}
		if (pos.x - entity.getRadius() < this.mapMinX)
		{
			pos.x = this.mapMinX + entity.getRadius();
		}
		if (pos.z + entity.getRadius() > this.mapMaxZ)
		{
			pos.z = this.mapMaxZ - entity.getRadius();
		}
		if (pos.z - entity.getRadius() < this.mapMinZ)
		{
			pos.z = this.mapMinZ + entity.getRadius();
		}
		
		return pos;
	}
	
	int getCurOrNextSpawnArea()
	{
		return -1;
	}

	void addSpawnPoint(SpawnPoint point)
	{

	}

	SpawnPoint getSpawnPoint(int id)
	{
		return null;
	}

	boolean canPetRevive()
	{
		return false;
	}

	boolean isPrivateMap()
	{
		return false;
	}

	//mapInstanceID == 0 是专属分线
	boolean isPKMap()
	{
		return this.mapInstanceID == 0;
	}
	
	boolean isFightMap()
	{
		return false;
	}

	boolean roleDeadClearChild()
	{
		return true;
	}
	
	long getMapPrepareTime()
	{
		return this.prepareTime;
	}
	
	boolean isInPrepared(long logicTime)
	{
		return this.prepareTime >= logicTime;
	}
	
//	boolean checkDestroy(long timeMillis, int timeTick, long logicTime)
//	{
//		if(this.timeout < 0)
//			return false;
//		
//		return (this.timeout + MAPCOPY_TIMEOUT_DELAY_DESTROY_TIME * 1000L) < logicTime;
//	}
	
	void setRoleAttack(int roleID)
	{
		
	}

	void syncDamageRank(MapRole mapRole)
	{
	}
	
	void syncEnterInfo(MapRole role)
	{
		
	}
	
	boolean ignoreTeamMemberDistance()
	{
		return false;
	}
	
//	void setRoleAutoRevive(MapRole role)
//	{
//		
//	}
	
	boolean updateCurSpawn()
	{
		return false;
	}

	SBean.SpawnPointProgress getPrivateMapSpawn(int spawnID)
	{
		return null;
	}

	SBean.SpawnPointProgress addPrivateMapSpawn(int spawnID)
	{
		return null;
	}

	Monster createMonster(int monsterID, GVector3 pos, GVector3 rotation, boolean canRotation, int standByTime, int curHP)
	{
		return null;
	}

	Monster getMonster(int monsterID)
	{
		return null;
	}
	
	Monster createWorldBoss(int bossID, int seq, int curHP)
	{
		return null;
	}
	
	void destroyWorldBoss(int bossID)
	{
		
	}
	
	Monster createWorldSuperMonster(int superMonsterID, int seq, int standTime)
	{
		return null;
	}

	Mineral createWorldMineral(SBean.WorldMineralCFGS cfg, int seq, int standTime)
	{
		return null;
	}
	
	void addMonster(Monster monster)
	{

	}

	void delMonsterToCache(Monster monster)
	{

	}
	
	void addKillMonster(MapRole killer, Monster monster)
	{
		this.killMonsters++;
	}
	
	void delMonster(int mid)
	{

	}

	void addBlur(Blur blur)
	{

	}

	void delBlurToRemoveCache(int blurID)
	{

	}

	void delBlur(int blurID)
	{

	}

	Blur getBlur(int blurID)
	{
		return null;
	}
	
	EscortCar getEscortCar(int carID)
	{
		return null;
	}
	
	void addEscortCar(EscortCar car)
	{
		
	}
	
	void delEscortCar(int carID)
	{
		
	}
	
	SkillEntity createSkillEntity(MapRole owner, GVector3 position, int modelID, SBean.SkillBaseCommonCFGS baseCommon, SBean.SkillBaseFixCFGS baseFix, SBean.SkillLevelFixCFGS lvlFixCfg, int skillLvl, int skillRealmLvl, int speed, long createTime)
	{
		return null;
	}

	void delSkillEntityToRemoveCache(int skillEntityID)
	{

	}

	void delSkillEntity(int skillEntityID)
	{

	}

	SkillEntity getSkillEntity(int entityID)
	{
		return null;
	}

	void addTriggedTrip(SBean.TrapState trap)
	{

	}

	SBean.TrapState getTriggedTrip(int tid)
	{
		return null;
	}

	Map<Integer, SBean.TrapState> getTriggedTraps()
	{
		return null;
	}

	void addClosedTraps(int trapID)
	{

	}

	boolean isTrapAlreadyClosed(int trapID)
	{
		return false;
	}

	void addNpc(Npc npc)
	{

	}

	void delNpc(int npcID)
	{

	}

	void addTrap(Trap trap)
	{

	}

	void delTrap(int trapID)
	{

	}

	Trap getTrap(int trapID)
	{
		return null;
	}

	Trap getConfigTrap(int configID)
	{
		return null;
	}

	void addWayPoint(WayPoint wayPoint)
	{

	}

	void delWayPoint(int wayPointID)
	{

	}

	boolean checkHasRoleNearBy(int gridX, int gridZ, BaseRole entity, int size, int distance)
	{
		return false;
	}
	
	void setAutoRevive(int roleID)
	{
		
	}
	
	
	boolean checkBaseRoleCanAttack(BaseRole attacker, BaseRole target)
	{	
		return target.checkCanBeAttack(attacker);
	}
	
	public GVector3 createPetPos(MapRole owner, int radius, float angle, int seq)
	{
		GVector3 pos = new GVector3((float) (radius * Math.cos(angle)), 0.0f, (float) (radius * Math.sin(angle)));
		pos = owner.getCurPosition().sum(pos);

		return this.fixPos(pos, owner);
	}
	
	//包括机器人
	List<MapRole> getRoleNearBy(int gridX, int gridZ, MapRole self, boolean big)
	{
		return GameData.emptyList();
	}
	
	List<Pet> getPetNearBy(int gridX, int gridZ)
	{
		return GameData.emptyList();
	}

	List<Blur> getBlurNearBy(int gridX, int gridZ)
	{
		return GameData.emptyList();
	}
	
	//不包括机器人
	Set<Integer> getRoleIDsNearBy(int gridX, int gridZ, MapRole self, boolean big)
	{
		return GameData.emptySet();
	}
	
	Map<Integer, SBean.EnterDetail> refreshNearByRoleIDs(int gridX, int gridZ, MapRole self, Set<Integer> curNearBy)
	{
		return GameData.emptyMap();
	}
	
	//包括机器人
	EnterInfo getEntitiesNearBy(int gridX, int gridZ, MapRole self)
	{
		return new EnterInfo();
	}
	
	//包括机器人(活的)
	List<BaseRole> getPlayerEnemiesNearBy(int gridX, int gridZ, MapRole owner)
	{
		return GameData.emptyList();
	}
	
	List<BaseRole> getRoleFriendEntityNearBy(int gridX, int gridZ, MapRole self)
	{
		return GameData.emptyList();
	}

	List<BaseRole> getRoleFriendEntityNearBy(int gridX, int gridZ, MapRole self, int distance)
	{
		return GameData.emptyList();
	}

	List<MapRole> getRoleByIndex(int minGridX, int maxGridX, int minGridZ, int maxGridZ)
	{
		return GameData.emptyList();
	}

	List<Monster> getMonsterNearBy(int gridX, int gridZ)
	{
		return GameData.emptyList();
	}

	List<Monster> getMonsterAround(int gridX, int gridZ, Monster monster)
	{
		return GameData.emptyList();
	}

	List<Trap> getTrapNearBy(int gridX, int gridZ)
	{
		return GameData.emptyList();
	}
	
	//不包括机器人
	Set<Integer> getSelfEnterRoleIDsNearby(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole role, boolean big)
	{
		return GameData.emptySet();
	}
	
	//包括机器人
	EnterInfo getSelfEnterEntities(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole self)
	{
		return new EnterInfo();
	}

	//不包括机器人
	Set<Integer> getSelfLeaveRoleIDsNearby(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole role, boolean big)
	{
		return GameData.emptySet();
	}
	
	//包括机器人
	LeaveInfo getSelfLeaveEntities(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole self)
	{
		return new LeaveInfo();
	}
	
	List<MapGrid> getSelfEnterGridNearBy(int oldGridX, int oldGridZ, int newGridX, int newGridZ, boolean big)
	{
		return GameData.emptyList();
	}

	List<MapGrid> getSelfLeaveGridNearBy(int oldGridX, int oldGridZ, int newGridX, int newGridZ, boolean big)
	{
		return GameData.emptyList();
	}

	boolean isTimeOut()
	{
		return ms.getMapManager().getMapLogicTime() > this.timeout && this.timeout > 0;
	}

	long getTimeLeft()
	{
		return this.timeout - ms.getMapManager().getMapLogicTime();
	}

	int getMapID()
	{
		return mapID;
	}

	int getInstanceID()
	{
		return mapInstanceID;
	}
	
	int getMapVipDropRatio(int vipLvl)
	{
		return 0;
	}
	
	List<DropItem> getDropItemList(int roleID, int fixedDropId, int randomDropId, int randomDropCount, int fixedDropRatio, int randomDropRatio, SBean.ExtraDropTbl extraDropTbl, SBean.Vector3 position, int configID, int entityType, boolean canTakeDrop)
	{
		if (!canTakeDrop)
			return GameData.emptyList();
		List<SBean.DummyGoods> drops = GameData.getDrops(fixedDropId, randomDropId, randomDropCount, fixedDropRatio, randomDropRatio, extraDropTbl);
		return getDropItemList(roleID, drops, position, configID, entityType);
	}
	
	List<DropItem> getDropItemList(int roleID, final List<SBean.DummyGoods> drops, SBean.Vector3 position, int configID, int entityType)
	{
		List<DropItem> lst = new ArrayList<>();
		if (drops != null)
		{
			DropGoods dropGoods = this.getDropGoods(roleID);
			if (dropGoods == null)
				dropGoods = this.addDropGoods(roleID);

			List<Integer> clearTime = GameData.getInstance().getCommonCFG().map.dropClearTime;
			if (clearTime != null)
			{
				int rank;
				for (SBean.DummyGoods d : drops)
				{
					rank = GameData.getInstance().getItemRank(d.id);
					if (rank <= 0 || rank > clearTime.size())
					{
						ms.getLogger().warn("item " + d.id + " rank = " + rank);
						continue;
					}

					int nextID = this.getNextDropID();
					ms.getLogger().trace("create dropID " + nextID + " rank " + rank);
					DropItem dropItem = new DropItem(nextID, new SBean.DummyGoods(d.id, d.count), ms.getMapManager().getMapLogicTime() + (long) (clearTime.get(rank - 1) * 60 * 1000), position, entityType == GameData.ENTITY_TYPE_MONSTER ? configID : 0);
					lst.add(dropItem);
					dropGoods.addDropItem(dropItem);
				}
			}
		}
		return lst;
	}
	
	boolean onTimer(long timeMillis, int timeTick, long logicTime)
	{
		if ((timeMillis / MapServer.TIME_TICK_INTERVAL) % MAX_TIME_TICK == randomTick)
		{
			this.mapRolesRemoveCache.forEach(this::onMapRoleTimeOut);
			this.mapRolesRemoveCache.clear();
			this.mapRoles.values().forEach(role -> {
				if(role.onTimer(timeTick, logicTime))
					this.mapRolesRemoveCache.add(role.id);
			});
			
			this.mapPetCluster.values().forEach(cluster -> {
				cluster.pets.values().forEach(p -> p.onTimer(timeTick, logicTime));
			});
			this.mapMapBuffs.values().forEach(mapBuff -> mapBuff.onTimer(timeTick));
			
			this.mapMineralsRemoveCache.forEach(this::onMineralTimeOut);
			this.mapMineralsRemoveCache.clear();
			this.mapMinerals.values().forEach(mineral -> {
				if(mineral.onTimer(timeTick, logicTime))
					this.mapMineralsRemoveCache.add(mineral.id);
			});

			if(timeTick > this.second)
			{
				this.checkFinish();
				this.checkTimeOut();
				this.second = timeTick;
			}
			return true;
		}
		return false;
	}

	void checkFinish()
	{
		if (!this.isMapAlreadyFinish && this.isMapFinish())
			this.processMapFinish();
	}

	boolean checkMapCopyFinish(int winCondition, Map<Integer, Integer> bosses)
	{
		if (winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_BOSSES)
			return this.checkIsAllBossKilled(bosses);
		else if (winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT)
		{
			if (this.isTimeOut())
				return true;

			if (this.checkIsAllMonsterAndMineralKilled())
				return true;
		}
		return false;
	}

	boolean checkIsAllBossKilled(Map<Integer, Integer> bosses)
	{
		Iterator<Map.Entry<Integer, Integer>> it = bosses.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Integer> entry = it.next();
			int monsterID = entry.getKey();
			int count = entry.getValue();
			if (this.getKilledBossCount(monsterID) < count)
				return false;
		}
		return true;
	}

	void addKilledBoss(int monsterID)
	{
		this.mapKilledBosses.compute(monsterID, (k, v) -> v == null ? 1 : v + 1);
	}

	int getKilledBossCount(int monsterID)
	{
		return this.mapKilledBosses.getOrDefault(monsterID, 0);
	}

	DropGoods getDropGoods(int roleID)
	{
		return this.mapDropGoods.get(roleID);
	}

	DropGoods addDropGoods(int roleID)
	{
		DropGoods dropGoods = new DropGoods(roleID);
		this.mapDropGoods.put(roleID, dropGoods);
		return dropGoods;
	}

	DropItem pickDropItem(int roleID, int dropID)
	{
		DropGoods dropGoods = this.mapDropGoods.get(roleID);
		if (dropGoods != null)
			return dropGoods.pickDropItem(dropID);
		return null;
	}
	
	DropItem getDropItem(int roleID, int dropID)
	{
		DropGoods dropGoods = this.mapDropGoods.get(roleID);
		if (dropGoods != null)
			return dropGoods.getDropItem(dropID);
		return null;
	}
	
	DropItem delDropItem(int roleID, int dropID)
	{
		DropGoods dropGoods = this.mapDropGoods.get(roleID);
		if (dropGoods != null)
			return dropGoods.delDropItem(dropID);
		return null;
	}
	
	void resetDropItem(int roleID, int dropID)
	{
		DropGoods dropGoods = this.mapDropGoods.get(roleID);
		if (dropGoods != null)
			dropGoods.resetDropItem(dropID);
	}
	
	//killer 可能是null
	void onMapRoleDead(MapRole deader, MapRole killer)
	{
		this.roleDeads.compute(deader.getID(), (k, v) -> v == null ? 1 : v + 1);
	}

	int getRoleMapDeadTimes(int roleID)
	{
		return this.roleDeads.getOrDefault(roleID, 0);
	}

	int getTotalDeadTimes()
	{
		int deadtimes = 0;
		for (Integer d : this.roleDeads.values())
			deadtimes += d;

		return deadtimes;
	}
	
	int addRoleMapKills(MapRole killer, MapRole deader)
	{
//		int count = this.roleKills.getOrDefault(killer.id, 0);
//		count++;
//		this.roleKills.put(killer.id, count);
//		this.setMostKillRole(count);
//		return count;
		return 0;
	}
	
	int getRoleMapKills(int roleID)
	{
		return this.roleKills.getOrDefault(roleID, 0);
	}
	
	void setMostKillRole(int count)
	{
		if (count > this.mostKillRole)
			this.mostKillRole = count;
	}

	boolean checkIsAllMonsterAndMineralKilled()
	{
		return this.isAllMonsterKilled() && this.mapMinerals.isEmpty();
	}

	void setMapFinish()
	{
		this.isMapAlreadyFinish = true;
	}

	void addRole(MapRole role)
	{
		mapRoles.put(role.getID(), role);
		role.setCurMap(this);

		if(this.isPrivateMap())
			return;
		
		SBean.Vector3 pos = role.getLogicPosition();
		int gridX = this.calcGridCoordinateX(pos.x);
		int gridZ = this.calcGridCoordinateZ(pos.z);
		MapGrid grid = this.getGrid(gridX, gridZ);
		if (grid != null)
			grid.addRole(role);
		else if(!this.isPrivateMap())
		{
			SBean.MapClusterCFGS mcc = GameData.getInstance().getMapClusterCFGS(this.mapID);
			if(mcc != null)
			{
				role.curPosition = new GVector3(mcc.spawnPos);
				int sGridX = this.calcGridCoordinateX(mcc.spawnPos.x);
				int sGridZ = this.calcGridCoordinateX(mcc.spawnPos.z);
				MapGrid sGrid = this.getGrid(sGridX, sGridZ);
				if(sGrid != null)
					sGrid.addRole(role);
				else
					ms.getLogger().warn("############ map " + this.mapID + " , " + this.mapInstanceID + " addRole " + role.id + " to spawn grid[" + gridX + "], spawnPos " + GameData.toString(mcc.spawnPos) + "grid not find");
					
			}
			ms.getLogger().warn("@@@@@@@@@@@@@@ map " + this.mapID + " , " + this.mapInstanceID + " addRole " + role.id + " to grid[" + gridX + " , " + gridZ + "], pos " + GameData.toString(pos) + "grid not find");
		}
	}
	
	void delRole(int rid)
	{
		MapRole role = mapRoles.remove(rid);
		if (role == null)
			return;

		role.setCurMap(null);
		MapGrid grid = role.getCurMapGrid();
		if (grid != null)
			grid.delRole(role);
	}

	void onMapRoleTimeOut(int roleID)
	{
		MapRole role = mapRoles.get(roleID);
		if(role != null && role.robot)
		{
			role.onLeavaMap();
			this.delRole(roleID);
		}
	}
	
	MapRole getRole(int roleID)
	{
		return this.mapRoles.get(roleID);
	}

	void addPet(Pet pet)
	{
		PetCluster petCluster = mapPetCluster.get(pet.owner.getID());
		if (petCluster == null)
		{
			petCluster = new PetCluster(pet.owner.getID());
			mapPetCluster.put(pet.owner.getID(), petCluster);
		}
		petCluster.addPet(pet);
		pet.setCurMap(this);

		if(this.isPrivateMap())
			return;
		
		SBean.Vector3 pos = pet.getLogicPosition();
		int gridX = this.calcGridCoordinateX(pos.x);
		int gridZ = this.calcGridCoordinateZ(pos.z);
		MapGrid grid = this.getGrid(gridX, gridZ);
		if (grid != null)
			grid.addPet(pet);
		else
			ms.getLogger().warn("@@@@@@@@@@@@@@ map " + this.mapID + " , " + this.mapInstanceID + " addPet " + pet.configID + " to grid[" + gridX + " , " + gridZ + "], grid not find");
	}

	void delPet(int rid, int pid)
	{
		PetCluster petCluster = this.mapPetCluster.get(rid);
		if (petCluster == null)
			return;

		Pet pet = petCluster.pets.get(pid);
		if (petCluster.pets.isEmpty())
			this.mapPetCluster.remove(rid);

		if (pet == null)
			return;

		MapGrid grid = pet.getCurMapGrid();
		if (grid != null)
			grid.delPet(pet);

		pet.setCurMap(null);
	}

	PetCluster delPetCluster(int rid)
	{
		return this.mapPetCluster.remove(rid);
	}
	
	PetCluster getPetCluster(int rid)
	{
		return this.mapPetCluster.get(rid);
	}

	Pet getPet(int rid, int pid)
	{
		PetCluster cluster = this.mapPetCluster.get(rid);
		if (cluster == null)
			return null;

		return cluster.pets.get(pid);
	}
	
	void loadMapBuff()
	{
		for (Integer pid : this.mapClusterCfg.mapBuffs)
		{
			SBean.MapBuffPointCFGS buffPointCfg = GameData.getInstance().getBuffPointCFGS(pid);
			if (buffPointCfg == null)
				continue;

			MapBuff mapBuff = new MapBuff(buffPointCfg.mapBuffID, this.ms).createNew(new GVector3(buffPointCfg.position));
			this.addMapBuff(mapBuff);
		}
	}

	void addMapBuff(MapBuff mapBuff)
	{
		this.mapMapBuffs.put(mapBuff.id, mapBuff);
		mapBuff.setCurMap(this);
		
		if(this.isPrivateMap())
			return;
		
		SBean.Vector3 pos = mapBuff.getLogicPosition();
		int gridX = this.calcGridCoordinateX(pos.x);
		int gridZ = this.calcGridCoordinateZ(pos.z);
		MapGrid grid = this.getGrid(gridX, gridZ);
		if (grid != null)
			grid.addMapBuff(mapBuff);

		Set<Integer> rids = this.getRoleIDsNearBy(gridX, gridZ, null, false);
		mapBuff.onSelfEnterNearBy(rids);
	}

	void delMapBuff(int mapBuffID)
	{
		MapBuff mapBuff = this.mapMapBuffs.remove(mapBuffID);
		if (mapBuff == null)
			return;

		if(this.isPrivateMap())
			return;
		
		MapGrid grid = mapBuff.getCurMapGrid();
		if (grid != null)
			grid.delMapBuff(mapBuff);

		mapBuff.setCurMap(null);
	}

	MapBuff getMapBuff(int mapBuffID)
	{
		return this.mapMapBuffs.get(mapBuffID);
	}

	List<SBean.BriefInfo> getAllMapBuffsInfo()
	{
		List<SBean.BriefInfo> lst = new ArrayList<>();
		for (MapBuff b : mapMapBuffs.values())
			lst.add(new SBean.BriefInfo(b.getID(), b.getConfigID(), b.getLogicPosition()));

		return lst;
	}

	void loadMinerals()
	{
		for (int mineralPointID : this.mapClusterCfg.minerals)
		{
			SBean.MineralPointCFGS pointCfg = GameData.getInstance().getMineralPointCFGS(mineralPointID);
			if (pointCfg == null)
				continue;

			Mineral mineral = new Mineral(pointCfg.relatedID, this.ms).createNew(new GVector3(pointCfg.position), -1);
			mineral.curRotation = new GVector3(pointCfg.rotation);
			this.addMineral(mineral);
		}
	}

	void addMineral(Mineral mineral)
	{
		mapMinerals.put(mineral.id, mineral);
		mineral.setCurMap(this);

		if(this.isPrivateMap())
			return;
		
		SBean.Vector3 pos = mineral.getLogicPosition();
		int gridX = this.calcGridCoordinateX(pos.x);
		int gridZ = this.calcGridCoordinateZ(pos.z);
		MapGrid grid = this.getGrid(gridX, gridZ);
		if (grid != null)
			grid.addMineral(mineral);
	}
	
	void onMineralTimeOut(int mineralID)
	{
		Mineral mineral = mapMinerals.get(mineralID);
		if(mineral != null)
		{
			mineral.onMineralBreakByNone();
			Set<Integer> rids = mineral.getRoleIDsNearBy(null);
			mineral.onSelfLeaveNearBy(rids, 1);
			
			this.delMineral(mineralID);
		}
	}
	
	Mineral delMineral(int mineralID)
	{
		Mineral mineral = mapMinerals.remove(mineralID);
		if (mineral == null)
			return null;

		MapGrid grid = mineral.getCurMapGrid();
		if (grid != null)
			grid.delMineral(mineral);
		mineral.setCurMap(null);
		return mineral;
	}

	Mineral getMineral(int mineralID)
	{
		return this.mapMinerals.get(mineralID);
	}

	List<SBean.BriefInfo> getAllMineralsInfo()
	{
		List<SBean.BriefInfo> lst = new ArrayList<>();
		for (Mineral m : mapMinerals.values())
		{
			if (m.mineralCount == -1 || m.mineralCount > 0)
				lst.add(new SBean.BriefInfo(m.getID(), m.getConfigID(), m.getLogicPosition()));
		}

		return lst;
	}

	public static class PrivateMap extends BaseMap
	{
		SBean.MapCopyCFGS mapCopyCfg;
		Map<Integer, Integer> pointID2Count;
		Map<Integer, SBean.SpawnPointProgress> privateMapProgress = new HashMap<>(); //<spawnId, SpawnPointProgress>
		Map<Integer, SBean.TrapState> triggedTraps = new HashMap<>();
		Map<Integer, Integer> closedTraps = new HashMap<>();
		Map<Integer, SBean.AttackDamageDetail> damageRank = new HashMap<>();

		PrivateMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms)
		{
			super(cfg, mapInstanceID, ms);
		}
		
		void addRole(MapRole role)
		{
			super.addRole(role);
			damageRank.put(role.getID(), new SBean.AttackDamageDetail(role.getRoleName(), 0));
		}

		void addPet(Pet pet)
		{
			super.addPet(pet);
			damageRank.put(pet.getID(), new SBean.AttackDamageDetail("", 0));
		}

		PrivateMap start()
		{
			super.start();
			SBean.MapCopyCFGS mCfg = GameData.getInstance().getMapCopyCFGS(this.mapID);
			if (mCfg == null)
				return null;

			this.mapCopyCfg = mCfg;
			this.timeout = ms.getMapManager().getMapLogicTime() + mCfg.maxTime * 1000L;
			pointID2Count = new HashMap<>();
			for (int areaID : this.mapClusterCfg.spawnAreas)
			{
				SBean.SpawnAreaCFGS areacfg = GameData.getInstance().getSpawnArea(areaID);
				if (areacfg == null)
					continue;

				for (int pid : areacfg.spawnPoint)
				{
					SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(pid);
					if (pointCfg == null)
						continue;

					this.pointID2Count.put(pid, GameData.getPointMonsterCount(pointCfg));
				}
			}
			return this;
		}
		
		boolean filterRole()
		{
			return false;
		}
		
		public GVector3 fixPos(GVector3 pos, BaseRole entity)
		{
			return pos;
		}
		
		Mineral delMineral(int mineralID)
		{
			Mineral mineral = super.delMineral(mineralID);
			
			if(!this.isMapAlreadyFinish && this.checkMapCopyFinish(this.mapCopyCfg.winCondition, this.mapCopyCfg.bosses))
				this.processMapFinish();
			
			return mineral;
		}
		
		void delMonster(int mid)
		{
			if(!this.isMapAlreadyFinish && this.checkMapCopyFinish(this.mapCopyCfg.winCondition, this.mapCopyCfg.bosses))
				this.processMapFinish();
		}
		
		boolean isAllMonsterKilled()
		{
			for (Map.Entry<Integer, Integer> e : this.pointID2Count.entrySet())
			{
				SBean.SpawnPointProgress progress = this.privateMapProgress.get(e.getKey());
				if (progress == null || progress.killedCount < e.getValue())
					return false;
			}

			return true;
		}

		boolean canPetRevive()
		{
			return true;
		}

		boolean isPrivateMap()
		{
			return true;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_NORMAL;
		}
		
		SBean.SpawnPointProgress getPrivateMapSpawn(int spawnID)
		{
			return this.privateMapProgress.get(spawnID);
		}

		SBean.SpawnPointProgress addPrivateMapSpawn(int spawnID)
		{
			SBean.SpawnPointProgress progress = new SBean.SpawnPointProgress(spawnID, 0, new HashMap<>());
			this.privateMapProgress.put(spawnID, progress);
			return progress;
		}

		void addTriggedTrip(SBean.TrapState trap)
		{
			this.triggedTraps.put(trap.id, trap);
		}

		SBean.TrapState getTriggedTrip(int tid)
		{
			return this.triggedTraps.get(tid);
		}

		Map<Integer, SBean.TrapState> getTriggedTraps()
		{
			return this.triggedTraps;
		}

		void addClosedTraps(int trapID)
		{
			this.closedTraps.put(trapID, trapID);
		}

		boolean isTrapAlreadyClosed(int trapID)
		{
			return this.closedTraps.get(trapID) != null;
		}

		Set<Integer> getRoleIDsNearBy(int gridX, int gridZ, MapRole self, boolean big)
		{
			return new HashSet<>(this.mapRoles.keySet());
		}
		
		@Override
		boolean isMapFinish()
		{
			return this.mapCopyCfg.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT ? this.isTimeOut() : false;
//			return this.checkMapCopyFinish(this.mapCopyCfg.winCondition, this.mapCopyCfg.bosses);
		}

		@Override
		void processMapFinish()
		{
			int deadTimes = this.getTotalDeadTimes();
			int leftTime = (int) ((this.timeout - ms.getMapManager().getMapLogicTime()) / 1000);
			int finishTime = this.mapCopyCfg.maxTime - leftTime;

			int score = GameData.getInstance().calcNormalMapCopyScore(true, finishTime, this.mapCopyCfg.maxTime, deadTimes);
			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, score);
			this.setMapFinish();
		}
		
		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut() || mapCopyCfg.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT)
				return;

			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_SCORE_FAILED);
			this.setMapFinish();
		}

		@Override
		void syncGSMapStart()
		{
			ms.getRPCManager().syncCommonMapCopyStart(mapID, mapInstanceID);
		}
		
		void syncDamageRank(MapRole self)
		{
			ms.getRPCManager().sendStrPacket(self.id, new SBean.map_copy_damage_rank(damageRank));
		}
	}

	abstract public static class PublicMap extends BaseMap
	{
		public static final int GRID_VIEW_RADUIS_UNIT_BIG 	= 4;
		public static final int GRID_VIEW_RADUIS_UNIT_SMALL = 3;

		final int gridSize;
		int mapGridXCount;
		int mapGridZCount;
		MapGrid[][] mapGrids;
		boolean clearAllMonster;

		ConcurrentMap<Integer, Monster> mapMonsters = new ConcurrentHashMap<>();
		List<Integer> mapMonstersRemoveCache = new ArrayList<>();
		ConcurrentMap<Integer, Npc> mapNpcs = new ConcurrentHashMap<>();
//		ConcurrentMap<Integer, WayPoint> mapWayPoints = new ConcurrentHashMap<>();
		ConcurrentMap<Integer, Trap> mapTraps = new ConcurrentHashMap<>(); //<id, trap>
		ConcurrentMap<Integer, Trap> mapConfigTraps = new ConcurrentHashMap<>(); //<configID, trap>
		ConcurrentMap<Integer, Blur> mapBlurs = new ConcurrentHashMap<>();
		List<Integer> mapBlursRemoveCache = new ArrayList<>();
		ConcurrentMap<Integer, SkillEntity> skillEntitys = new ConcurrentHashMap<>(); //<roleID, SkillEntity>
		List<Integer> skillEntityRemoveCache = new ArrayList<>();
		ConcurrentMap<Integer, EscortCar> mapEscortCars = new ConcurrentHashMap<>();	//<carID, EscortCar>  carID == roleID
		
		List<SpawnArea> mapSpawnAreas = new ArrayList<>();
		List<SpawnArea> curAreas = new ArrayList<>();
		Map<Integer, SpawnPoint> mapSpawnPoints = new HashMap<>();
		Map<Integer, Integer> worldBosses = new HashMap<>();
		Map<Integer, Integer> worldSuperMonsters = new HashMap<>();
		Map<Integer, Integer> worldMinerals = new HashMap<>();
		
		Map<Integer, Long> roleAutoRevives = new HashMap<>();					//<roleID, revivetime>
		
		PublicMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms);
			this.gridSize = gridSize;
			this.initMapGrid(cfg.minX, cfg.minZ, cfg.maxX, cfg.maxZ);
		}

		void initMapGrid(int minX, int minZ, int maxX, int maxZ)
		{
			this.mapMinX = minX;
			this.mapMinZ = minZ;
			this.mapMaxX = maxX;
			this.mapMaxZ = maxZ;
			this.mapGridXCount = (maxX - minX) / gridSize + 1;
			this.mapGridZCount = (maxZ - minZ) / gridSize + 1;
			this.mapGrids = new MapGrid[mapGridXCount][mapGridZCount];

			for (int gridZ = 0; gridZ < mapGridZCount; ++gridZ)
			{
				for (int gridX = 0; gridX < mapGridXCount; ++gridX)
				{
					mapGrids[gridX][gridZ] = new MapGrid(gridX, gridZ);
				}
			}
		}
		
		boolean filterRole()
		{
			return false;
		}
		
		public static boolean isMapGridNearBy(MapGrid grid1, MapGrid grid2)
		{
			return (Math.abs(grid1.getGridX() - grid2.getGridX()) <= GRID_VIEW_RADUIS_UNIT_BIG) && (Math.abs(grid1.getGridZ() - grid2.getGridZ()) <= GRID_VIEW_RADUIS_UNIT_BIG);
		}
		
		MapGrid getGrid(int gridX, int gridZ)
		{
			return isGridCoordinateXValid(gridX) && isGridCoordinateZValid(gridZ) ? mapGrids[gridX][gridZ] : null;
		}

		public int calcGridCoordinateX(int x)
		{
			return calcGridCoordinate(x, this.mapMinX);
		}

		public int calcGridCoordinateZ(int z)
		{
			return calcGridCoordinate(z, this.mapMinZ);
		}

		private boolean isGridCoordinateXValid(int x)
		{
			return x >= 0 && x < mapGridXCount;
		}

		private boolean isGridCoordinateZValid(int z)
		{
			return z >= 0 && z < mapGridZCount;
		}

		private int calcGridCoordinate(int p, int minp)
		{
			return (p - minp) / gridSize;
		}

		private boolean checkInSmallRange(int x, int z, int gridX, int gridZ)
		{
			return Math.abs(x - gridX) <= GRID_VIEW_RADUIS_UNIT_SMALL && Math.abs(z - gridZ) <= GRID_VIEW_RADUIS_UNIT_SMALL;
		}
		
		void setMapFinish()
		{
			super.setMapFinish();
			SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
			this.mapMonsters.values().stream().filter(m -> m.isMoving()).forEach(m -> m.onStopMove(m.curPosition, timeTick, true));
			this.mapRoles.values().stream().filter(r -> !r.robot).forEach(r -> {
				r.breakSkill();
				r.addState(Behavior.EBPREPAREFIGHT);
				ms.getRPCManager().sendStrPacket(r.id, new SBean.role_addstate(Behavior.EBPREPAREFIGHT, timeTick));
			});
		}
		
		void onMapFinishHandler()
		{
			
		}
		
		PublicMap start()
		{
			super.start();
			this.loadMonsters();
			this.loadTraps();
//			this.loadNpcs();
//			this.loadWayPoints();

			return this;
		}

		void clearAllRoles()
		{
			super.clearAllRoles();
			this.mapBlurs.clear();
			this.skillEntitys.clear();
			this.mapEscortCars.clear();
			
			for(int i=0; i<mapGrids.length; i++)
			{
				for(int j=0; j<mapGrids[i].length; j++)
				{
					mapGrids[i][j].clearAllRoles();
				}
			}
		}

		int getCurOrNextSpawnArea()
		{
			int size = this.curAreas.size();
			if (size > 0)
				return this.mapSpawnAreas.isEmpty() ? -this.curAreas.get(size - 1).getID() : this.curAreas.get(size - 1).getID();

			if (!this.mapClusterCfg.spawnAreas.isEmpty())
				return this.mapSpawnAreas.isEmpty() ? this.mapClusterCfg.spawnAreas.get(0) : -this.mapClusterCfg.spawnAreas.get(this.mapClusterCfg.spawnAreas.size() - 1);

			return 0;
		}

		boolean isAllMonsterKilled()
		{
			for (SpawnArea area : this.curAreas)
			{
				if (!area.isEmpty())
					return false;
			}

			for (SpawnArea area : this.mapSpawnAreas)
			{
				if (!area.isEmpty())
					return false;
			}

			return true;
		}

		boolean checkHasRoleNearBy(int gridX, int gridZ, BaseRole entity, int size, int distance)
		{
			for (int z = gridZ - size; z <= gridZ + size; ++z)
			{
				for (int x = gridX - size; x <= gridX + size; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid == null)
						continue;

					for (MapRole r : grid.getRoles().values())
					{
						if (r.active && (distance < 0 || r.curPosition.distance(entity.curPosition) <= distance))
							return true;
					}
				}
			}

			return false;
		}

		//包括机器人
		List<MapRole> getRoleNearBy(int gridX, int gridZ, MapRole self, boolean big)
		{
			int unit = big ? GRID_VIEW_RADUIS_UNIT_BIG : GRID_VIEW_RADUIS_UNIT_SMALL;
			List<MapRole> lst = new ArrayList<>();
			for (int z = gridZ - unit; z <= gridZ + unit; ++z)
			{
				for (int x = gridX - unit; x <= gridX + unit; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
					{
						for (MapRole r : grid.getRoles().values())
						{
							if (r != self && r.active)
								lst.add(r);
						}
					}
				}
			}
			return lst;
		}
		
		List<Pet> getPetNearBy(int gridX, int gridZ)
		{
			List<Pet> lst = new ArrayList<>();
			for (int z = gridZ - GRID_VIEW_RADUIS_UNIT_BIG; z <= gridZ + GRID_VIEW_RADUIS_UNIT_BIG; ++z)
			{
				for (int x = gridX - GRID_VIEW_RADUIS_UNIT_BIG; x <= gridX + GRID_VIEW_RADUIS_UNIT_BIG; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
						grid.getPetClusters().values().forEach(c -> c.pets.values().stream().filter(p -> p.active).forEach(p -> lst.add(p)));
				}
			}
			return lst;
		}
		
		List<Blur> getBlurNearBy(int gridX, int gridZ)
		{
			List<Blur> lst = new ArrayList<>();
			for (int z = gridZ - GRID_VIEW_RADUIS_UNIT_BIG; z <= gridZ + GRID_VIEW_RADUIS_UNIT_BIG; ++z)
			{
				for (int x = gridX - GRID_VIEW_RADUIS_UNIT_BIG; x <= gridX + GRID_VIEW_RADUIS_UNIT_BIG; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
						grid.getBlurs().values().stream().filter(b -> !b.isDead()).forEach(b -> lst.add(b));
				}
			}

			return lst;
		}
		
		//不包括机器人
		Set<Integer> getRoleIDsNearBy(int gridX, int gridZ, MapRole self, boolean big)
		{
			Set<Integer> rids = new HashSet<>();
			int unit = big ? GRID_VIEW_RADUIS_UNIT_BIG : GRID_VIEW_RADUIS_UNIT_SMALL;
			for (int z = gridZ - unit; z <= gridZ + unit; ++z)
			{
				for (int x = gridX - unit; x <= gridX + unit; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
					{
						for (MapRole r : grid.getRoles().values())
						{
							if (r != self && r.active && !r.robot)
								rids.add(r.id);
						}
					}
				}
			}
			return rids;
		}
		
		Map<Integer, SBean.EnterDetail> refreshNearByRoleIDs(int gridX, int gridZ, MapRole self, Set<Integer> curNearBy)
		{
			Map<Integer, SBean.EnterDetail> roles = new HashMap<>();
			for(int offset = 0; offset <= GRID_VIEW_RADUIS_UNIT_BIG; ++offset)
			{
				for (int z = gridZ - offset; z <= gridZ + offset; ++z)
				{
					for (int x = gridX - offset; x <= gridX + offset; ++x)
					{
						if(Math.abs(z - gridZ) < offset && Math.abs(x - gridX) < offset)
							continue;
						
						MapGrid grid = this.getGrid(x, z);
						if (grid != null)
						{
							for (MapRole r : grid.getRoles().values())
							{
								if(curNearBy.contains(r.getID()))
									continue;
								
								if (r != self && r.active && !r.robot && !r.isVirtual())
									roles.put(r.id, r.getEnterDetail());
								
								if(curNearBy.size() + roles.size() == ms.getConfig().getWorldMaxRole(this.mapInstanceID == 0))
									break;
							}
						}
					}
				}
			}
			return roles;
		}
		
		//包括机器人
		EnterInfo getEntitiesNearBy(int gridX, int gridZ, MapRole self)
		{
			EnterInfo entityInfo = new EnterInfo().createNew();
			for (int z = gridZ - GRID_VIEW_RADUIS_UNIT_BIG; z <= gridZ + GRID_VIEW_RADUIS_UNIT_BIG; ++z)
			{
				for (int x = gridX - GRID_VIEW_RADUIS_UNIT_BIG; x <= gridX + GRID_VIEW_RADUIS_UNIT_BIG; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
					{
						grid.setEnterInfo(entityInfo, self, true);

						if (checkInSmallRange(x, z, gridX, gridZ))
							grid.setEnterInfo(entityInfo, self, false);
					}
				}
			}

			return entityInfo;
		}
		
		//包括机器人(活的)
		List<BaseRole> getPlayerEnemiesNearBy(int gridX, int gridZ, MapRole owner)
		{
			List<BaseRole> lst = new ArrayList<>();
			int ownerID = owner != null ? owner.id : 0;
			this.mapRoles.values().stream().filter(r -> r.id != ownerID && r.active && !r.isDead()).forEach(r -> lst.add(r));
			this.mapPetCluster.values().stream().filter(c -> c.id != ownerID).forEach(c -> {
				c.pets.values().stream().filter(p -> p.active && !p.isDead()).forEach(p -> lst.add(p));
			});
			this.mapBlurs.values().stream().filter(b -> b.owner.id != ownerID && !b.isDead()).forEach(b -> lst.add(b));
			return lst;
		}
		
		List<BaseRole> getRoleFriendEntityNearBy(int gridX, int gridZ, MapRole self)
		{
			List<BaseRole> lst = new ArrayList<>();
			PetCluster cluster = this.mapPetCluster.get(self.id);
			if (cluster != null)
				cluster.pets.values().stream().filter(p -> p.active && !p.isDead()).forEach(p -> lst.add(p));
			self.blurs.stream().filter(b -> !b.isDead()).forEach(b -> lst.add(b));
			
			int teamID = self.getTeamID();
			if (teamID == 0)
				return lst;

			this.mapRoles.values().stream().filter(r -> r.id != self.id && r.isTeamMember(self) && r.active && !r.isDead()).forEach(r -> lst.add(r));
			this.mapPetCluster.values().stream().filter(c -> c.id != self.id).forEach(c -> {
				c.pets.values().stream().filter(p -> p.owner.isTeamMember(self) && p.active && !p.isDead()).forEach(p -> lst.add(p));
			});
			this.mapBlurs.values().stream().filter(b -> b.owner.id != self.id && b.owner.isTeamMember(self) && !b.isDead()).forEach(b -> lst.add(b));

			return lst;
		}

		List<BaseRole> getRoleFriendEntityNearBy(int gridX, int gridZ, MapRole self, int distance)
		{
			List<BaseRole> lst = new ArrayList<>();
			PetCluster cluster = this.mapPetCluster.get(self.id);
			if (cluster != null)
				cluster.pets.values().stream().filter(pet -> pet.active && !pet.isDead() && self.curPosition.distance(pet.curPosition) <= distance).forEach(pet -> lst.add(pet));
			self.blurs.stream().filter(b -> !b.isDead() && self.curPosition.distance(b.curPosition) <= distance).forEach(b -> lst.add(b));
			if (self.getTeamID() == 0)
				return lst;
			
			this.mapRoles.values().stream().filter(r -> r.id != self.id && r.isTeamMember(self) && r.active && !r.isDead() && self.curPosition.distance(r.curPosition) <= distance).forEach(r -> lst.add(r));
			this.mapPetCluster.values().stream().filter(c -> c.id != self.id).forEach(c -> {
				c.pets.values().stream().filter(p -> p.owner.isTeamMember(self) && p.active && !p.isDead() && self.curPosition.distance(p.curPosition) <= distance).forEach(p -> lst.add(p));
			});
			this.mapBlurs.values().stream().filter(b -> b.owner.id != self.id && b.owner.isTeamMember(self) && !b.isDead() && self.curPosition.distance(b.curPosition) <= distance).forEach(b -> lst.add(b));
			return lst;
		}

		List<MapRole> getRoleByIndex(int minGridX, int maxGridX, int minGridZ, int maxGridZ)
		{
			List<MapRole> lst = new ArrayList<>();
			for (int z = minGridZ; z <= maxGridZ; z++)
			{
				for (int x = minGridX; x <= maxGridX; x++)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
						grid.getRoles().values().stream().filter(r -> r.active).forEach(r -> lst.add(r));
				}
			}
			return lst;
		}
		
		List<Monster> getMonsterNearBy(int gridX, int gridZ)
		{
			List<Monster> lst = new ArrayList<>();
			for (int z = gridZ - GRID_VIEW_RADUIS_UNIT_SMALL; z <= gridZ + GRID_VIEW_RADUIS_UNIT_SMALL; ++z)
			{
				for (int x = gridX - GRID_VIEW_RADUIS_UNIT_SMALL; x <= gridX + GRID_VIEW_RADUIS_UNIT_SMALL; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
						grid.getMonsters().values().stream().filter(m -> !m.isDead()).forEach(m -> lst.add(m));
				}
			}
			return lst;
		}

		List<Monster> getMonsterAround(int gridX, int gridZ, Monster monster)
		{
			List<Monster> lst = new ArrayList<>();
			for (int z = gridZ - 1; z <= gridZ + 1; ++z)
			{
				for (int x = gridX - 1; x <= gridX + 1; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
						grid.getMonsters().values().stream().filter(m -> !m.isDead() && m != monster).forEach(m -> lst.add(m));
				}
			}
			return lst;
		}

		List<Trap> getTrapNearBy(int gridX, int gridZ)
		{
			List<Trap> lst = new ArrayList<>();
			for (int z = gridZ - GRID_VIEW_RADUIS_UNIT_SMALL; z <= gridZ + GRID_VIEW_RADUIS_UNIT_SMALL; ++z)
			{
				for (int x = gridX - GRID_VIEW_RADUIS_UNIT_SMALL; x <= gridX + GRID_VIEW_RADUIS_UNIT_SMALL; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
						grid.getTraps().values().forEach(t -> lst.add(t));
				}
			}

			return lst;
		}
		
		//不包括机器人
		Set<Integer> getSelfEnterRoleIDsNearby(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole role, boolean big)
		{
			List<MapGrid> grids = this.getSelfEnterGridNearBy(oldGridX, oldGridZ, newGridX, newGridZ, big);
			Set<Integer> rids = new HashSet<>();
			grids.forEach(g -> g.getRoles().values().stream().filter(r -> r != role && r.active && !r.robot && (role == null || r.isNearByRoles(role.id))).forEach(r -> rids.add(r.id)));

			return rids;
		}
		
		//包括机器人
		EnterInfo getSelfEnterEntities(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole self)
		{
			EnterInfo entityInfo = new EnterInfo().createNew();
			List<MapGrid> smallGrids = this.getSelfEnterGridNearBy(oldGridX, oldGridZ, newGridX, newGridZ, false);
			List<MapGrid> bigGrids = this.getSelfEnterGridNearBy(oldGridX, oldGridZ, newGridX, newGridZ, true);
			smallGrids.forEach(g -> g.setEnterInfo(entityInfo, self, false));
			bigGrids.forEach(g -> g.setEnterInfo(entityInfo, self, true));

			return entityInfo;
		}

		//不包括机器人
		Set<Integer> getSelfLeaveRoleIDsNearby(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole role, boolean big)
		{
			List<MapGrid> grids = this.getSelfLeaveGridNearBy(oldGridX, oldGridZ, newGridX, newGridZ, big);
			Set<Integer> rids = new HashSet<>();
			grids.forEach(g -> g.getRoles().values().stream().filter(r -> r != role && r.active && !r.robot && (role == null || r.isNearByRoles(role.id))).forEach(r -> rids.add(r.id)));

			return rids;
		}
		
		//包括机器人
		LeaveInfo getSelfLeaveEntities(int oldGridX, int oldGridZ, int newGridX, int newGridZ, MapRole self)
		{
			LeaveInfo info = new LeaveInfo();
			List<MapGrid> smallGrids = this.getSelfLeaveGridNearBy(oldGridX, oldGridZ, newGridX, newGridZ, false);
			List<MapGrid> bigGrids = this.getSelfLeaveGridNearBy(oldGridX, oldGridZ, newGridX, newGridZ, true);
			smallGrids.forEach(g -> g.setLeaveInfo(info, self, false));
			bigGrids.forEach(g -> g.setLeaveInfo(info, self, true));
			
			return info;
		}

		List<MapGrid> getSelfEnterGridNearBy(int oldGridX, int oldGridZ, int newGridX, int newGridZ, boolean big)
		{
			int unit = big ? GRID_VIEW_RADUIS_UNIT_BIG : GRID_VIEW_RADUIS_UNIT_SMALL;

			List<MapGrid> grids = new ArrayList<>();
			if (oldGridZ < newGridZ) //向上
			{
				//新的最上面一行
				for (int z = newGridZ + unit; z > oldGridZ + unit; --z)
				{
					for (int x = newGridX - unit; x <= newGridX + unit; ++x)
					{
						MapGrid grid = this.getGrid(x, z);
						if (grid != null)
							grids.add(grid);
					}
				}

				if (oldGridX < newGridX) //向右
				{
					for (int z = oldGridZ + unit; z >= newGridZ - unit; --z)
					{
						//最右边一列（少最上）
						for (int x = newGridX + unit; x > oldGridX + unit; --x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
				else
				{
					for (int z = oldGridZ + unit; z >= newGridZ - unit; --z)
					{
						//最左边一列（少最上）
						for (int x = newGridX - unit; x < oldGridX - unit; ++x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
			}
			else
			{
				//新的最下面一行
				for (int z = newGridZ - unit; z < oldGridZ - unit; ++z)
				{
					for (int x = newGridX - unit; x <= newGridX + unit; ++x)
					{
						MapGrid grid = this.getGrid(x, z);
						if (grid != null)
							grids.add(grid);
					}
				}

				if (oldGridX < newGridX)
				{
					for (int z = oldGridZ - unit; z <= newGridZ + unit; ++z)
					{
						//最右边一列（少最上）
						for (int x = newGridX + unit; x > oldGridX + unit; --x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
				else
				{
					for (int z = oldGridZ - unit; z <= newGridZ + unit; ++z)
					{
						//最左边一列（少最上）
						for (int x = newGridX - unit; x < oldGridX - unit; ++x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
			}

			return grids;
		}

		List<MapGrid> getSelfLeaveGridNearBy(int oldGridX, int oldGridZ, int newGridX, int newGridZ, boolean big)
		{
			int unit = big ? GRID_VIEW_RADUIS_UNIT_BIG : GRID_VIEW_RADUIS_UNIT_SMALL;
			List<MapGrid> grids = new ArrayList<>();
			if (oldGridZ < newGridZ) //向上
			{
				//最下面一行
				for (int z = oldGridZ - unit; z < newGridZ - unit; ++z)
				{
					for (int x = oldGridX - unit; x <= oldGridX + unit; ++x)
					{
						MapGrid grid = this.getGrid(x, z);
						if (grid != null)
							grids.add(grid);
					}
				}

				if (oldGridX < newGridX) //向右
				{
					for (int z = newGridZ - unit; z <= oldGridZ + unit; ++z)
					{
						//最左边一列（少最下）
						for (int x = oldGridX - unit; x < newGridX - unit; ++x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
				else
				{
					for (int z = newGridZ - unit; z <= oldGridZ + unit; ++z)
					{
						//最右边一列（少最下）
						for (int x = oldGridX + unit; x > newGridX + unit; --x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
			}
			else
			{
				//最上边一行
				for (int z = oldGridZ + unit; z > newGridZ + unit; --z)
				{
					for (int x = oldGridX - unit; x <= oldGridX + unit; ++x)
					{
						MapGrid grid = this.getGrid(x, z);
						if (grid != null)
							grids.add(grid);
					}
				}

				if (oldGridX < newGridX)
				{
					for (int z = newGridZ + unit; z >= oldGridZ - unit; --z)
					{
						//最左边一列（少最上）
						for (int x = oldGridX - unit; x < newGridX - unit; ++x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
				else
				{
					for (int z = newGridZ + unit; z >= oldGridZ - unit; --z)
					{
						//最右边一列（少最上）
						for (int x = oldGridX + unit; x > newGridX + unit; --x)
						{
							MapGrid grid = this.getGrid(x, z);
							if (grid != null)
								grids.add(grid);
						}
					}
				}
			}

			return grids;
		}

		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if (super.onTimer(timeMillis, timeTick, logicTime))
			{
				this.mapEscortCars.values().stream().filter(c -> c.active).forEach(c -> c.onTimer(timeTick, logicTime));
				
				this.mapMonstersRemoveCache.forEach(this::delMonster);
				this.mapMonstersRemoveCache.clear();
				this.mapMonsters.values().stream().forEach(m -> m.onTimer(timeTick, logicTime));
				if(this.isMapAlreadyFinish)
					this.onMapFinishHandler();

				this.skillEntityRemoveCache.forEach(this::delSkillEntity);
				this.skillEntityRemoveCache.clear();
				this.skillEntitys.values().stream().filter(skillEntity -> skillEntity.onTimer(timeTick, logicTime)).forEach(skillEntity -> skillEntity.onDeadHandle());

				this.mapBlursRemoveCache.forEach(this::delBlur);
				this.mapBlursRemoveCache.clear();
				this.mapBlurs.values().forEach(blur -> blur.onTimer(timeTick, logicTime));

//				this.mapNpcs.values().forEach(npc -> npc.onTimer(timeTick));
				this.mapTraps.values().forEach(trap -> trap.onTimer(timeTick, logicTime));
				
				this.checkAutoRevive(logicTime);
				return true;
			}
			return false;
		}
		
		void checkAutoRevive(long logicTime)
		{
			if(this.roleAutoRevives.isEmpty())
				return;
			
			Iterator<Map.Entry<Integer, Long>> it = this.roleAutoRevives.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<Integer, Long> e = it.next();
				if(logicTime < e.getValue())
					continue;
				
				it.remove();
				MapRole role = this.mapRoles.get(e.getKey());
				if(role == null || !role.isDead())
					continue;
				
				role.autoRevive();
			}
		}
		
		void updateSpawnArea(long timeMillis, int timeTick, long logicTime)
		{	
			Iterator<SpawnArea> it = this.curAreas.iterator();
			while (it.hasNext())
			{
				SpawnArea area = it.next();
				if (area.isEmpty())
				{
					SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(area.getID());
					if (areaCfg.hasTripDoor == 1)
					{
						for (Integer tid : areaCfg.trapDoorIDs)
						{
							Trap t = this.getConfigTrap(tid);
							if (t != null)
								t.onTrigTrap(MapManager.TRAP_CHANGE_STATE1, null);
						}
					}

					for (Integer tid : areaCfg.clearClose)
					{
						Trap t = this.getConfigTrap(tid);
						if (t != null)
							t.setTrapState(MapManager.ESTRAP_ACTIVE);
					}

					for (Integer tid : areaCfg.clearOpen)
					{
						Trap t = this.getConfigTrap(tid);
						if (t != null)
							t.setTrapState(MapManager.ESTRAP_TRIG);
					}
					it.remove();
				}
				else
				{
					area.onTimer(timeMillis, timeTick);
				}
			}

			if (this.curAreas.isEmpty() && !this.mapSpawnAreas.isEmpty())
			{
				SpawnArea area = this.mapSpawnAreas.remove(0);
				this.curAreas.add(area);
				if (this.updateCurSpawn())
				{
					int areaID = this.mapSpawnAreas.isEmpty() ? -area.getID() : area.getID();
					Set<Integer> rids = new HashSet<>();
					this.mapRoles.values().stream().filter(r -> !r.robot && r.active).forEach(r -> rids.add(r.id));
					if(!rids.isEmpty())
						ms.getRPCManager().broadcastStrPacket(rids, new SBean.update_curspawnarea(areaID));
				}
			}
		}

		void clearMonster()
		{
			Iterator<Monster> it = this.mapMonsters.values().iterator();
			while (it.hasNext())
			{
				Monster monster = it.next();
				if (monster.isDead())
					continue;

				SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(monster.getConfigID());
				if (monsterCfg == null || monster.monsterMapType == GameData.SECTMAP_BOSS_PROGRESS)
					continue;

				if (monster.monsterMapType == GameData.SECTMAP_MONSTER_PROGRESS)
					ms.getRPCManager().syncSectMapProgress(this.mapID, this.mapInstanceID, monster.spawnPointID, monster.curHP, 10000);

				SBean.DropRatio dropRatio = GameData.getMonsterDoubleDropRatio(this.mapID, monsterCfg.id, ms.getMapManager().getCurrentDoubleDropConfig());
				SBean.ExtraDropTbl extraDropTbl = GameData.getMonsterExtraDropTable(this.mapID, monsterCfg.id, ms.getMapManager().getCurrentExtralDropConfig());

				for (MapRole r : mapRoles.values())
				{
					ms.getRPCManager().sendStrPacket(r.getID(), new SBean.nearby_monster_dead(monster.getID(), r.getID()));
					List<DropItem> dropItems = this.getDropItemList(r.id, monsterCfg.fixedDropID, monsterCfg.randomDropIDs.get(r.configID - 1), monsterCfg.randomDropCnt, dropRatio.fixedDrop, dropRatio.randomDrop, extraDropTbl, monster.getLogicPosition(), monster.configID, monster.entityType, r.canTakeDrop);
					if (!dropItems.isEmpty())
					{
						List<SBean.DropInfo> dropInfo = new ArrayList<>();
						for (DropItem d : dropItems)
							dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));
						ms.getRPCManager().sendStrPacket(r.getID(), new SBean.role_sync_drops(monster.getLogicPosition(), dropInfo));
					}

					ms.getRPCManager().addRoleKill(r.getID(), r.getMapID(), r.getMapInstanceID(), monster.getEntityType(), monster.getConfigID(), 1, r.getID()); //杀怪数
				}
				
				monster.setCurHP(0);
				this.delMonsterToCache(monster);
			}
		}

		void loadNpcs()
		{
			for (int pointID : this.mapClusterCfg.npcs)
			{
				SBean.NpcPointCFGS pointCfg = GameData.getInstance().getNpcPointCfg(pointID);
				if (pointCfg == null)
					continue;

				Npc npc = new Npc(pointCfg.relatedID, this.ms).createNew(new GVector3(pointCfg.position.x, pointCfg.position.y, pointCfg.position.z));
				npc.curRotation = new GVector3(pointCfg.rotation);
				this.addNpc(npc);
			}
		}

		void addNpc(Npc npc)
		{
			mapNpcs.put(npc.getID(), npc);
			npc.setCurMap(this);

			SBean.Vector3 pos = npc.getLogicPosition();
			int gridX = this.calcGridCoordinateX(pos.x);
			int gridZ = this.calcGridCoordinateZ(pos.z);
			MapGrid grid = this.getGrid(gridX, gridZ);
			if (grid != null)
				grid.addNpc(npc);
		}

		void delNpc(int npcID)
		{
			Npc npc = mapNpcs.remove(npcID);
			if (npc == null)
				return;
			npc.setCurMap(null);
			MapGrid grid = npc.getCurMapGrid();
			if (grid != null)
				grid.delNpc(npc);
		}

		void delAllNpc()
		{
			for (Npc npc : mapNpcs.values())
			{
				npc.onSelfLeaveNearBy(npc.getRoleIDsNearBy(null), 1);
				delNpc(npc.getID());
			}
		}

		void loadTraps()
		{
			for (Integer tid : this.mapClusterCfg.traps)
			{
				SBean.TrapExpandedCFGS tcfg = GameData.getInstance().getTrapCFG(tid);
				if (tcfg == null)
					continue;

				Trap trap = new Trap(tcfg.id, this.ms).createNew(new GVector3(tcfg.position));
				trap.curRotation = new GVector3(tcfg.rotation);
				this.addTrap(trap);
			}
		}

		void addTrap(Trap trap)
		{
			this.mapTraps.put(trap.getID(), trap);
			this.mapConfigTraps.put(trap.getConfigID(), trap);
			trap.setCurMap(this);

			SBean.Vector3 pos = trap.getLogicPosition();
			int gridX = this.calcGridCoordinateX(pos.x);
			int gridZ = this.calcGridCoordinateZ(pos.z);
			MapGrid grid = this.getGrid(gridX, gridZ);
			if (grid != null)
				grid.addTrap(trap);
		}

		void delTrap(int trapID)
		{
			Trap trap = this.mapTraps.remove(trapID);
			if (trap == null)
				return;

			MapGrid grid = trap.getCurMapGrid();
			if (grid != null)
				grid.delTrap(trap);

			trap.setCurMap(null);
			this.mapConfigTraps.remove(trap.getConfigID());
		}

		Trap getTrap(int trapID)
		{
			return this.mapTraps.get(trapID);
		}

		Trap getConfigTrap(int configID)
		{
			return this.mapConfigTraps.get(configID);
		}

		void loadWayPoints()
		{
			for (Integer wid : this.mapClusterCfg.wayPoints)
			{
				SBean.WayPointCFGS wCfg = GameData.getInstance().getWayPointCFGS(wid);
				if (wCfg != null)
				{
					WayPoint wayPoint = new WayPoint(wCfg.id, ms).createNew(new GVector3(wCfg.position));
					this.addWayPoint(wayPoint);
				}
			}
		}

//		void addWayPoint(WayPoint wayPoint)
//		{
//			mapWayPoints.put(wayPoint.getID(), wayPoint);
//			wayPoint.setCurMap(this);
//
//			SBean.Vector3 pos = wayPoint.getLogicPosition();
//			int gridX = this.calcGridCoordinateX(pos.x);
//			int gridZ = this.calcGridCoordinateZ(pos.z);
//			MapGrid grid = this.getGrid(gridX, gridZ);
//			if (grid != null)
//				grid.addWayPoint(wayPoint);
//
//		}
//
//		void delWayPoint(int wayPointID)
//		{
//			WayPoint wayPoint = mapWayPoints.remove(wayPointID);
//			if (wayPoint == null)
//				return;
//
//			MapGrid grid = wayPoint.getCurMapGrid();
//			if (grid != null)
//				grid.delWayPoint(wayPoint);
//
//			wayPoint.setCurMap(null);
//		}

		void addSpawnPoint(SpawnPoint point)
		{
			this.mapSpawnPoints.put(point.spawnPointID, point);
		}

		SpawnPoint getSpawnPoint(int id)
		{
			return this.mapSpawnPoints.get(id);
		}

		void loadMonsters()
		{
			for (int areaID : this.mapClusterCfg.spawnAreas)
			{
				SpawnArea area = new SpawnArea(areaID, this).createFromCfg();
				mapSpawnAreas.add(area);
			}
		}

		Monster createMonster(int monsterID, GVector3 pos, GVector3 rotation, boolean canRotation, int standByTime, int curHP)
		{
			SBean.MonsterCFGS cfg = GameData.getInstance().getMonsterCFGS(monsterID);
			if(cfg == null)
				return null;
			Monster monster = new Monster(monsterID, this.ms).createNew(pos, rotation, canRotation, standByTime);
			if (curHP > 0)
				monster.curHP = curHP;
			this.addMonster(monster);
			return monster;
		}

		void addMonster(Monster monster)
		{
			mapMonsters.put(monster.getID(), monster);
			monster.setCurMap(this);

			SBean.Vector3 pos = monster.getLogicPosition();
			int gridX = this.calcGridCoordinateX(pos.x);
			int gridZ = this.calcGridCoordinateZ(pos.z);
			MapGrid grid = this.getGrid(gridX, gridZ);
			if (grid != null)
				grid.addMonster(monster);
			else
				ms.getLogger().warn("@@@@@@@@@@@@@@ map " + this.mapID + " , " + this.mapInstanceID + " addMonster " + monster.configID + " to grid[" + gridX + " , " + gridZ + "], grid not find");

//			Set<Integer> rids = this.getRoleIDsNearBy(gridX, gridZ, null, false);
			Set<Integer> rids = monster.getRoleIDsNearBy(null);
			monster.onSelfSpawnNearBy(rids);
		}

		void delMonsterToCache(Monster monster)
		{
			this.mapMonstersRemoveCache.add(monster.getID());
		}

		void delMonster(int mid)
		{
			Monster monster = this.mapMonsters.remove(mid);
			if (monster == null)
				return;

			MapGrid grid = monster.getCurMapGrid();
			if (grid != null)
				grid.delMonster(monster);

			SpawnPoint point = monster.getCurSpawnPoint();
			if (point != null)
			{
				point.delMonster(monster);
				SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(monster.getConfigID());
				if (monsterCfg != null && monsterCfg.bossType == GameData.MONSTER_BOSSTYPE_FINALBOSS)
					this.addKilledBoss(monster.getConfigID());
			}
			monster.setCurMap(null);
			this.worldBosses.remove(monster.mapBossID);
			if(this.worldSuperMonsters.remove(monster.mapBossID) != null)
				ms.getRPCManager().syncSuperMonster(new SBean.ActivityEntity(monster.mapBossID, GameData.ACTIVITY_ENTITY_TYPE_WORLDBOSS, this.getMapID(), this.getInstanceID(), 0), false);
		}

		Monster getMonster(int monsterID)
		{
			return this.mapMonsters.get(monsterID);
		}
		
		void addBlur(Blur blur)
		{
			this.mapBlurs.put(blur.getID(), blur);
			blur.setCurMap(this);

			SBean.Vector3 pos = blur.getLogicPosition();
			int gridX = this.calcGridCoordinateX(pos.x);
			int gridZ = this.calcGridCoordinateZ(pos.z);
			MapGrid grid = this.getGrid(gridX, gridZ);
			if (grid != null)
				grid.addBlur(blur);
			else 
				ms.getLogger().warn("@@@@@@@@@@@@@@ map " + this.mapID + " , " + this.mapInstanceID + " addBlur " + blur.configID + " to grid[" + gridX + " , " + gridZ + "], grid not find");
		}

		void delBlurToRemoveCache(int blurID)
		{
			this.mapBlursRemoveCache.add(blurID);
		}

		void delBlur(int blurID)
		{
			Blur blur = this.mapBlurs.remove(blurID);
			if (blur == null)
				return;

			MapGrid grid = blur.getCurMapGrid();
			if (grid != null)
				grid.delBlur(blur);

			blur.setCurMap(null);
		}
		
		Blur getBlur(int blurID)
		{
			return this.mapBlurs.get(blurID);
		}
		
		EscortCar getEscortCar(int carID)
		{
			return this.mapEscortCars.get(carID);
		}
		
		void addEscortCar(EscortCar car)
		{
			this.mapEscortCars.put(car.getID(), car);
			car.setCurMap(this);

			SBean.Vector3 pos = car.getLogicPosition();
			int gridX = this.calcGridCoordinateX(pos.x);
			int gridZ = this.calcGridCoordinateZ(pos.z);
			MapGrid grid = this.getGrid(gridX, gridZ);
			if (grid != null)
				grid.addEscortCar(car);
		}
		
		void delEscortCar(int carID)
		{
			EscortCar car = this.mapEscortCars.remove(carID);
			if (car == null)
				return;

			MapGrid grid = car.getCurMapGrid();
			if (grid != null)
				grid.delEscortCar(car);

			car.setCurMap(null);
		}
		
		SkillEntity createSkillEntity(MapRole owner, GVector3 position, int modelID, SBean.SkillBaseCommonCFGS baseCommon, SBean.SkillBaseFixCFGS baseFix, SBean.SkillLevelFixCFGS lvlFixCfg, int skillLvl, int skillRealmLvl, int speed, long createTime)
		{
			SkillEntity skillEntity = new SkillEntity(this.ms, owner, position, baseCommon.id, modelID);
			this.skillEntitys.put(skillEntity.id, skillEntity);
			skillEntity.setCurMap(this);

			SBean.Vector3 pos = skillEntity.getLogicPosition();
			int gridX = this.calcGridCoordinateX(pos.x);
			int gridZ = this.calcGridCoordinateZ(pos.z);
			MapGrid grid = this.getGrid(gridX, gridZ);
			if (grid != null)
				grid.addSkillEntity(skillEntity);
			
			skillEntity.onCreateHandle();
			skillEntity.createNew(baseCommon, baseFix, lvlFixCfg, skillLvl, skillRealmLvl, speed, createTime);
			return skillEntity;
		}

		void delSkillEntityToRemoveCache(int skillEntityID)
		{
			this.skillEntityRemoveCache.add(skillEntityID);
		}

		void delSkillEntity(int skillEntityID)
		{
			SkillEntity skillEntity = this.skillEntitys.remove(skillEntityID);
			if (skillEntity == null)
				return;

			MapGrid grid = skillEntity.getCurMapGrid();
			if (grid != null)
				grid.delSkillEntity(skillEntity.id);

			skillEntity.setCurMap(null);
		}

		SkillEntity getSkillEntity(int entityID)
		{
			return this.skillEntitys.get(entityID);
		}
		
		int addRoleMapKills(MapRole killer, MapRole deader)
		{
			int count = this.roleKills.getOrDefault(killer.id, 0);
			count++;
			this.roleKills.put(killer.id, count);
			this.setMostKillRole(count);
			return count;
		}
	}

	public static class WorldMap extends PublicMap
	{
		boolean isPKMap = false;
		Map<Integer, Monster> flagMonster = new HashMap<>();
		int flag = 0;
		SBean.MapFlagSectOverView sectOverview = new SBean.MapFlagSectOverView(0, "", 0);
		Map<Integer, WeddingCar> worldWeddingCars = new HashMap<>();
		List<Integer> weddingCarRemoveCache = new ArrayList<>();
		Map<Integer, MrgBanquet> mrgBanquets = new HashMap<>();
		
		WorldMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
			SBean.WorldMapCFGS wCfg = GameData.getInstance().getWorldMapCFGS(cfg.id);
			if(wCfg != null)
				this.isPKMap = (wCfg.pkType == GameData.MAP_PKTYPE_SECT || wCfg.pkType == GameData.MAP_PKTYPE_KILL);
			
			this.timeout = -1;
		}

		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if (super.onTimer(timeMillis, timeTick, logicTime))
			{
				this.weddingCarRemoveCache.forEach(this::delWeddingCar);
				this.weddingCarRemoveCache.clear();
				this.worldWeddingCars.values().forEach(c -> c.onTimer(timeTick, logicTime));
				this.updateSpawnArea(timeMillis, timeTick, logicTime);
				this.checkDropItemForClear(logicTime);
				this.updateMrgBanqute(timeTick);
				return true;
			}

			return false;
		}

		private void updateMrgBanqute(int timeTick)
		{
			Iterator<MrgBanquet> it = this.mrgBanquets.values().iterator();
			while(it.hasNext())
			{
				MrgBanquet banquet = it.next();
				if(banquet.onTimer(timeTick))
				{
					it.remove();
					ms.getLogger().info("marriage banquet " + banquet.getOpenRole() + " time out del");
				}
			}
		}
		
		void createMrgBanquet(int roleID, int banquet)
		{
			if(GameData.getInstance().getMarriageBanquetMap() != this.mapID)
				return;
			
			if(this.mrgBanquets.containsKey(roleID))
				return;
			
			SBean.MarriageBanquetCFGS banquetCfg = GameData.getInstance().getMarriageBanquetCFGS(banquet);
			if(banquetCfg == null)
				return;
			
			int now = GameTime.getTime();
			MrgBanquet mb = new MrgBanquet(roleID, banquetCfg, now + GameData.getInstance().getMarriageBaseCFGS().banquetLast, this);
			mb.onCreate(now);
			this.mrgBanquets.put(roleID, mb);
		}
		
		boolean isTimeOut()
		{
			return false;
		}

		boolean isPKMap()
		{
			return this.isPKMap || super.isPKMap();
		}
		
		boolean filterRole()
		{
			return true;
		}
		
		Monster createWorldBoss(int bossID, int seq, int curHP)
		{
			if (this.worldBosses.containsKey(bossID))
				return null;
			
			SBean.WorldBossCFGS cfg = GameData.getInstance().getWorldBossCFGS(bossID);
			if (cfg == null || seq <= 0 || seq > cfg.base.refreshPos.size())
				return null;

			Monster monster = this.createMonster(cfg.base.monsterID, new GVector3(cfg.base.refreshPos.get(seq - 1)), GVector3.randomRotation(), true, -1, curHP);
			if(monster != null)
			{
				monster.monsterMapType = GameData.WORLDMAP_BOSS;
				monster.mapBossID = bossID;
				this.worldBosses.put(bossID, monster.getID());
			}
			return monster;
		}
		
		Monster gmCreateWorldBoss(MapRole role, int bossID)
		{
			if (this.worldBosses.containsKey(bossID))
				return null;
			
			SBean.WorldBossCFGS cfg = GameData.getInstance().getWorldBossCFGS(bossID);
			Monster monster = this.createMonster(cfg.base.monsterID, new GVector3().reset(role.getCurPosition()), GVector3.randomRotation(), true, cfg.base.standbyTime * 1000, -1);
			if(monster == null)
				return null;
			monster.monsterMapType = GameData.WORLDMAP_BOSS;
			monster.mapBossID = bossID;
			this.worldBosses.put(bossID, monster.getID());
			return monster;
		}
		
		void destroyWorldBoss(int bossID)
		{
			Integer mid = this.worldBosses.get(bossID);
			if(mid == null)
				return;
			
			Monster monster = this.mapMonsters.get(mid);
			if(monster != null)
			{
				monster.curHP = 0;
				monster.onDeadHandle(0, 0);
			}
		}
		
		void worldBossPop(int bossID, int popIndex)
		{
			Integer mid = this.worldBosses.get(bossID);
			if(mid == null)
				return;
			
			Monster monster = this.mapMonsters.get(mid);
			if(monster != null)
				monster.worldBossPop(popIndex);
		}
		
		Monster createWorldSuperMonster(int superMonsterID, int seq, int standTime)
		{
			ms.getLogger().debug("createWorldMapSuperMonster " + mapID + " , " + mapInstanceID + " , " + superMonsterID + " , " + seq + " standTime " + standTime);
			if (this.worldSuperMonsters.containsKey(superMonsterID))
				return null;

			SBean.WorldMonsterCFGS cfg = GameData.getInstance().getWorldSuperMonster(superMonsterID);
			if (cfg == null || seq <= 0 || seq > cfg.refreshPos.size())
				return null;

			Monster monster = this.createMonster(cfg.monsterID, new GVector3(cfg.refreshPos.get(seq - 1)), GVector3.randomRotation(), true, standTime * 1000, -1);
			if(monster != null)
			{
				monster.monsterMapType = GameData.WORLDMAP_SUPERMONSTER;
				monster.mapBossID = superMonsterID;
				this.worldSuperMonsters.put(superMonsterID, monster.getID());
			}
			return monster;
		}
		
		Mineral createWorldMineral(SBean.WorldMineralCFGS cfg, int seq, int standTime)
		{
			ms.getLogger().debug("createWorldMapMineral " + mapID + " , " + mapInstanceID + " , " + cfg.id + " , " + seq);
			if(this.worldMinerals.containsKey(cfg.id))
				return null;
			
			if (seq <= 0 || seq > cfg.refreshPos.size())
				return null;
			
			SBean.Vector3 pos = cfg.refreshPos.get(seq - 1);
			Mineral mineral = new Mineral(cfg.mineralID, this.ms).createNew(new GVector3(pos), standTime * 1000L);
			mineral.worldMineralID = cfg.id;
			mineral.curRotation.reset(GVector3.UNIT_X);
			this.worldMinerals.put(cfg.id, mineral.getID());
			this.addMineral(mineral);
			mineral.onCreate();
			return mineral;
		}
		
		Mineral createMineral(int mineralID, SBean.Vector3 pos, int standTime)
		{
			Mineral mineral = new Mineral(mineralID, this.ms).createNew(new GVector3(pos), standTime < 0 ? -1 : standTime * 1000L);
			mineral.curRotation.reset(GVector3.UNIT_X);
			this.addMineral(mineral);
			
			mineral.onCreate();
			return mineral;
		}
		
		Mineral delMineral(int mineralID)
		{
			Mineral mineral = super.delMineral(mineralID);
			if(mineral != null && mineral.worldMineralID > 0)
			{
				this.worldMinerals.remove(mineral.worldMineralID);
				ms.getRPCManager().syncWorldMineral(new SBean.ActivityEntity(mineral.worldMineralID, GameData.ACTIVITY_ENTITY_TYPE_BOX, this.getMapID(), this.getInstanceID(), 0), false);
			}
			return mineral;
		}
		
		void addKillMonster(MapRole killer, Monster monster)
		{
			if (flagMonster.containsKey(monster.id))
			{
				flagMonster.remove(monster.id);
				if (flagMonster.isEmpty())
					ms.getRPCManager().syncFlagCanTake(this.mapID);
			}
		}
		
		Monster spawnSceneMonster(final SBean.SceneSpawnPointCFGS pointCfg, float angle, int spawnRole)
		{
			GVector3 spawnPosition = new GVector3(pointCfg.position);
			if (pointCfg.posRand == 1)
			{
				float radius = GameRandom.getRandFloat(0, pointCfg.randRadius / 2.f) + pointCfg.randRadius / 2.f;
				spawnPosition.selfSum(new GVector3((float) (radius * Math.cos(angle)), 0.0f, (float) (radius * Math.sin(angle))));
			}
			
			Monster monster = new Monster(pointCfg.monsterID, this.ms).createNew(spawnPosition, GVector3.randomRotation(), true, -1);
			if(pointCfg.seeType == 1)			//仅自己可见
				monster.spawnRole = spawnRole;
			else
				monster.spawnRole = -spawnRole;
			
			this.addMonster(monster);
			return monster;
		}
		
		//包括机器人(活的)
		List<BaseRole> getPlayerEnemiesNearBy(int gridX, int gridZ, MapRole owner)
		{
			int unit = owner != null ? GRID_VIEW_RADUIS_UNIT_BIG : GRID_VIEW_RADUIS_UNIT_SMALL;
			List<BaseRole> lst = new ArrayList<>();
			for (int z = gridZ - unit; z <= gridZ + unit; ++z)
			{
				for (int x = gridX - unit; x <= gridX + unit; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
					{
						grid.getRoles().values().stream().filter(r -> r.active && !r.isDead()).forEach(r -> lst.add(r));
						grid.getPetClusters().values().forEach(cluster -> cluster.pets.values().stream().filter(p -> p.active && !p.isDead()).forEach(p -> lst.add(p)));
						grid.getBlurs().values().stream().filter(b -> !b.isDead()).forEach(b -> lst.add(b));
						if(owner != null && owner.carRobber != 0)
							grid.getEscortCars().stream().filter(c -> !c.isDead() && c.id != owner.id && c.checkCanBeAttack(owner)).forEach(c -> lst.add(c));
					}
				}
			}
			return lst;
		}
		
		List<BaseRole> getRoleFriendEntityNearBy(int gridX, int gridZ, MapRole self)
		{
			List<BaseRole> lst = new ArrayList<>();
			PetCluster cluster = this.mapPetCluster.get(self.id);
			if (cluster != null)
				cluster.pets.values().stream().filter(p -> p.active && !p.isDead()).forEach(p -> lst.add(p));
			self.blurs.stream().filter(b -> !b.isDead()).forEach(b -> lst.add(b));
			
			int teamID = self.getTeamID();
			if (teamID == 0)
				return lst;

			for (int z = gridZ - GRID_VIEW_RADUIS_UNIT_BIG; z <= gridZ + GRID_VIEW_RADUIS_UNIT_BIG; ++z)
			{
				for (int x = gridX - GRID_VIEW_RADUIS_UNIT_BIG; x <= gridX + GRID_VIEW_RADUIS_UNIT_BIG; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
					{
						grid.getRoles().values().stream().filter(r -> r != self && !r.isDead() && r.active && r.isTeamMember(self)).forEach(r ->lst.add(r));
						grid.getPetClusters().values().stream().filter(c -> c.id != self.id).forEach(c -> {
							c.pets.values().stream().filter(p -> p.active && !p.isDead() && p.owner.isTeamMember(self)).forEach(p -> lst.add(p));
						});
						grid.getPetClusters().values().forEach(c -> c.pets.values().stream().filter(p -> p.active && !p.isDead() && p.owner != self && p.owner.isTeamMember(self)).forEach(p -> lst.add(p)));
						grid.getBlurs().values().stream().filter(b -> !b.isDead() && b.owner != self && b.owner.isTeamMember(self)).forEach(b -> lst.add(b));
//						if (checkInSmallRange(x, z, gridX, gridZ))
//							grid.getMonsters().values().stream().filter(m -> role.checkEntityFriend(role, m) && !m.isDead()).forEach(m -> lst.add(m));
					}
				}
			}

			return lst;
		}
		
		List<BaseRole> getRoleFriendEntityNearBy(int gridX, int gridZ, MapRole self, int distance)
		{
			List<BaseRole> lst = new ArrayList<>();
			PetCluster cluster = this.mapPetCluster.get(self.id);
			if (cluster != null)
				cluster.pets.values().stream().filter(pet -> pet.active && !pet.isDead() && self.curPosition.distance(pet.curPosition) <= distance).forEach(pet -> lst.add(pet));
			self.blurs.stream().filter(b -> !b.isDead() && self.curPosition.distance(b.curPosition) <= distance).forEach(b -> lst.add(b));
			
			if (self.getTeamID() == 0)
				return lst;

			for (int z = gridZ - GRID_VIEW_RADUIS_UNIT_BIG; z <= gridZ + GRID_VIEW_RADUIS_UNIT_BIG; ++z)
			{
				for (int x = gridX - GRID_VIEW_RADUIS_UNIT_BIG; x <= gridX + GRID_VIEW_RADUIS_UNIT_BIG; ++x)
				{
					MapGrid grid = this.getGrid(x, z);
					if (grid != null)
					{
						grid.getRoles().values().stream().filter(r -> r != self && r.active && self.isTeamMember(r) && self.curPosition.distance(r.curPosition) <= distance).forEach(r -> lst.add(r));
						grid.getPetClusters().values().stream().filter(c -> c.id != self.id).forEach(c -> {
							c.pets.values().stream().filter(p -> p.active && !p.isDead() && p.owner.isTeamMember(self) && self.curPosition.distance(p.curPosition) <= distance).forEach(p -> lst.add(p));
						});
						grid.getBlurs().values().stream().filter(b -> !b.isDead() && b.owner != self && b.owner.isTeamMember(self) && self.curPosition.distance(b.curPosition) <= distance).forEach(b -> lst.add(b));
					}
				}
			}

			return lst;
		}
		
		void loadMonsters()
		{
			super.loadMonsters();
			for (SpawnArea area : this.mapSpawnAreas)
			{
				this.curAreas.add(area);
				area.addMonsters(new HashMap<>());
			}
			this.mapSpawnAreas.clear();
		}

		void checkDropItemForClear(long logicTime)
		{
			Iterator<Map.Entry<Integer, DropGoods>> it1 = this.mapDropGoods.entrySet().iterator();
			int roleID;
			while (it1.hasNext())
			{
				Map.Entry<Integer, DropGoods> entry = it1.next();
				DropGoods dropGoods = entry.getValue();
				roleID = entry.getKey();
				if (dropGoods.allDrops.isEmpty())
					it1.remove();
				else
				{
					Iterator<DropItem> it2 = dropGoods.allDrops.values().iterator();
					while (it2.hasNext())
					{
						DropItem dropItem = it2.next();
						if (logicTime > dropItem.clearTime)
						{
							ms.getLogger().trace("checkDropItemForClear " + dropItem.dropID);
							ms.getRPCManager().sendStrPacket(roleID, new SBean.drop_delete(dropItem.dropID));
							it2.remove();
						}
					}
				}
			}
		}

		boolean canPetRevive()
		{
			return true;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAP_WORLD;
		}
		
		int getCurOrNextSpawnArea()
		{
			return -1;
		}

		@Override
		boolean isMapFinish()
		{
			return false;
		}

		@Override
		void processMapFinish()
		{

		}

		@Override
		void checkTimeOut()
		{

		}

		@Override
		void syncGSMapStart()
		{

		}
		
		int addRoleMapKills(MapRole killer, MapRole deader)
		{
			if(killer.getID() != deader.getID())
				ms.getRPCManager().addRoleKill(killer.getID(), this.mapID, this.mapInstanceID, deader.getEntityType(), deader.getID(), 1, killer.getID()); 
			return 0;
		}

		public void createWorldMapFlag(SBean.Vector3 flagPoint, int flagId, List<Integer> monsterAreaId, SBean.MapFlagSectOverView sectOverview)
		{
			int oldsect = this.sectOverview.sectId;
			this.sectOverview = sectOverview;
			this.createFlag(flagPoint, flagId, -1);
			for (int pid : monsterAreaId)
			{
				boolean isAlive = false;
				for (Monster flagmonster : this.flagMonster.values())
				{
					if (flagmonster.getSpawnPointID() == pid)
					{
						flagmonster.healHp();
						isAlive = true;
					}
				}
				if (isAlive)
					continue;
				SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getFlagBattleCFGS().flagMonsters.get(pid);
				if (pointCfg == null)
					continue;
				Monster monster = this.createFlagMonster(pointCfg.monsterID, new GVector3(pointCfg.position), GVector3.randomRotation(), true, -1, -1);
				monster.setCurSpawnPoint(pid);
				this.flagMonster.put(monster.id, monster);
			}
			if (oldsect != this.sectOverview.sectId)
			{
				Set<Integer> rids = new HashSet<>();
				rids.addAll(this.mapRoles.keySet());
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.map_flag_sect_change(this.sectOverview));
			}
		}

		private Monster createFlagMonster(int monsterID, GVector3 pos, GVector3 rotation, boolean canRotation, int standByTime, int curHP)
		{
			Monster monster = new Monster(monsterID, this.ms).createNew(pos, rotation, canRotation, standByTime);
			monster.setSectId(sectOverview.sectId == 0 ? -1 : sectOverview.sectId);
			if (curHP > 0)
				monster.curHP = curHP;
			this.addMonster(monster);
			return monster;
		}

		public void updateMapFlagInfo(SBean.MapFlagSectOverView sectOverview)
		{
			this.sectOverview = sectOverview;
			Set<Integer> rids = new HashSet<>();
			rids.addAll(this.mapRoles.keySet());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.map_flag_info(sectOverview));
		}
		
		Mineral createFlag(SBean.Vector3 flagPoint, int flagId, int standTime)
		{
			ms.getLogger().debug("createWorldMapMineral " + mapID + " , " + mapInstanceID + " , " + flagId);
			if(this.flag != 0)
				return null;
			
			Mineral mineral = new Mineral(flagId, this.ms).createNew(new GVector3(flagPoint), standTime * 1000L);
			mineral.curRotation.reset(GVector3.UNIT_X);
			this.addMineral(mineral);
			this.flag = mineral.id;
			return mineral;
		}
		
		WeddingCar createWeddingCar(int carID, SBean.RoleOverview man, SBean.RoleOverview woman)
		{
			SBean.MarriageCarCFGS carCfg = GameData.getInstance().getMarriageCarCFGS(carID);
			if(carCfg == null)
				return null;
			
			SBean.Vector3 firstPos = GameData.getInstance().getMarriageLinePos(carCfg.lineID, 0);
			if(firstPos == null)
				return null;
			WeddingCar car = new WeddingCar(this.ms, carCfg.lineID, carID, man, woman).createNew(firstPos);
			this.addWeddingCar(car);
			car.onCreateHandle();
			return car;
		}
		
		WeddingCar getWeddingCar(int carID)
		{
			return this.worldWeddingCars.get(carID);
		}
		
		void addWeddingCar(WeddingCar car)
		{
			this.worldWeddingCars.put(car.getID(), car);
			car.setCurMap(this);
			
			int gridX = this.calcGridCoordinateX((int)car.getCurPosition().x);
			int gridZ = this.calcGridCoordinateZ((int)car.getCurPosition().z);
			MapGrid grid = this.getGrid(gridX, gridZ);
			if(grid != null)
				grid.addWeddingCar(car);
				
		}
		
		void delWeddingCar(int carID)
		{
			WeddingCar car = worldWeddingCars.remove(carID);
			if(car == null)
				return;
			
			car.setCurMap(null);
			MapGrid grid = car.getCurMapGrid();
			if(grid != null)
				grid.delWeddingCar(car);
		}
		
		void delWeddingCarToRemoveCache(int carID)
		{
			this.weddingCarRemoveCache.add(carID);
		}
		
		void createRobotHero(SBean.FightRole robot, Map<Integer, SBean.FightPet> curFightPets, int spawnPoint)
		{
			SBean.MapClusterCFGS mccfg = GameData.getInstance().getMapClusterCFGS(mapID);
			if (mccfg == null || !mccfg.spawnPoints.contains(spawnPoint))
				return;
			
			SBean.SpawnPointCFGS spCfg = GameData.getInstance().getSpawnPoint(spawnPoint);
			if(spCfg == null)
				return;
			
			if(this.mapRoles.containsKey(robot.base.roleID))
				return;
			
			RobotHero hero = new RobotHero(this.ms).createRobotRole(robot, curFightPets, this.mapID, spCfg);
			this.addRole(hero);
			hero.update(-1, 0);
			hero.onCreate();
			hero.changeCurFightPetsImpl();
		}
		
		void destroyRobotHero(int roleID)
		{
			MapRole hero = this.getRole(roleID);
			if(hero == null || !hero.robot)
				return;
			
			hero.onLeavaMap();
			this.delRole(roleID);
		}
		
		SteleMineral createSteleMineral(int steleKey, SBean.SteleMineralCFGS mCfg, MapStele mStele)
		{
			SteleMineral m = new SteleMineral(mCfg.mineralID, ms, mStele).createNew(mCfg.mapLocation.location);
			this.addMineral(m);
			m.onCreate();
			return m;
		}
	}

	abstract public static class MapCopy extends PublicMap
	{
		final int winCondition;
		final Map<Integer, Integer> bosses;
		MapCopy(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, int winCondition, Map<Integer, Integer> bosses)
		{
			super(cfg, mapInstanceID, ms, gridSize);
			this.winCondition = winCondition;
			this.bosses = bosses;
		}
		
		void loadMonsters()
		{
			super.loadMonsters();
			this.curAreas.add(this.mapSpawnAreas.remove(0));
		}
		
		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if (super.onTimer(timeMillis, timeTick, logicTime))
			{
				this.updateSpawnArea(timeMillis, timeTick, logicTime);
				return true;
			}

			return false;
		}

		boolean canPetRevive()
		{
			return true;
		}

		boolean updateCurSpawn()
		{
			return true;
		}	
		
		@Override
		void syncGSMapStart()
		{
			ms.getRPCManager().syncCommonMapCopyStart(mapID, mapInstanceID);
		}
		
		@Override
		boolean isMapFinish()
		{
			return this.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT ? this.isTimeOut() : false;
//			return this.checkMapCopyFinish(this.winCondition, this.bosses);
		}
		
		Mineral delMineral(int mineralID)
		{
			Mineral mineral = super.delMineral(mineralID);
			
			if(!this.isMapAlreadyFinish && this.checkMapCopyFinish(this.winCondition, this.bosses))
				this.processMapFinish();
			
			return mineral;
		}
		
		void delMonster(int mid)
		{
			super.delMonster(mid);
			
			if(!this.isMapAlreadyFinish && this.checkMapCopyFinish(this.winCondition, this.bosses))
				this.processMapFinish();
		}
		
		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut() || this.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT)
				return;

			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_SCORE_FAILED);
			this.setMapFinish();
		}
		
		@Override
		void processMapFinish()
		{
			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_NO_SCORE);
			this.setMapFinish();
		}
	}
	
	public static class PetLifeMap extends PublicMap
	{
		PetLifeMap(MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
		}

		@Override
		boolean isMapFinish()
		{
			return false;
		}

		@Override
		void processMapFinish()
		{
			
		}

		@Override
		void checkTimeOut()
		{	
			
		}

		@Override
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_PETLIFE;
		}
		
		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if (super.onTimer(timeMillis, timeTick, logicTime))
			{
				this.updateSpawnArea(timeMillis, timeTick, logicTime);
				return true;
			}

			return false;
		}
		
		int getCurOrNextSpawnArea()
		{
			return -1;
		}
		
		boolean isTimeOut()
		{
			return false;
		}
		
		void syncGSMapStart()
		{
			ms.getRPCManager().syncPetLifeMapStart(mapID, mapInstanceID);
		}
		
		boolean canPetRevive()
		{
			return true;
		}
		
		void loadMonsters()
		{
			super.loadMonsters();
			for (SpawnArea area : this.mapSpawnAreas)
			{
				this.curAreas.add(area);
				area.addMonsters(new HashMap<>());
			}
			this.mapSpawnAreas.clear();
		}
	}
	
	public static class TeamMapCopy extends MapCopy
	{
		private int mapCopyMaxTime;
		public Map<Integer, SBean.AttackDamageDetail> damageRank = new HashMap<>();
		TeamMapCopy(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, int winCondition, Map<Integer, Integer> bosses)
		{
			super(cfg, mapInstanceID, ms, gridSize, winCondition, bosses);
		}
		
		void addRole(MapRole role)
		{
			super.addRole(role);
			damageRank.put(role.getID(), new SBean.AttackDamageDetail(role.getRoleName(), 0));
		}

		void addPet(Pet pet)
		{
			super.addPet(pet);
			damageRank.put(pet.getID(), new SBean.AttackDamageDetail("", 0));
		}

		TeamMapCopy start()
		{
			super.start();
			SBean.MapCopyCFGS mCfg = GameData.getInstance().getMapCopyCFGS(this.mapID);
			if (mCfg == null)
				return null;
			
			this.mapCopyMaxTime = mCfg.maxTime;
			this.timeout = ms.getMapManager().getMapLogicTime() + mCfg.maxTime * 1000L;
			return this;
		}
		
		int getMapVipDropRatio(int vipLvl)
		{
			if(GameData.checkRandom(GameData.getInstance().getVipCFGS(vipLvl).teamMapDropTimeRate))
				return 1;
			
			return 0;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_NORMAL;
		}
		
		boolean ignoreTeamMemberDistance()
		{
			return true;
		}
		
		@Override
		void processMapFinish()
		{
			int deadTimes = this.getTotalDeadTimes();
			int leftTime = (int) ((this.timeout - ms.getMapManager().getMapLogicTime()) / 1000);
			int finishTime = this.mapCopyMaxTime - leftTime;

			int score = GameData.getInstance().calcNormalMapCopyScore(false, finishTime, this.mapCopyMaxTime, deadTimes);
			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, score);
			this.setMapFinish();
			this.clearAllMonster = true;
		}
		
		void onMapFinishHandler()
		{
			if(!this.mapMonsters.isEmpty() && this.clearAllMonster)
				this.clearMonster();
		}
		
		public void onMonsterGetDamage(int attackerID, int damage)
		{
			if(!this.mapRoles.containsKey(attackerID))
				return;
			damageRank.merge(attackerID, new SBean.AttackDamageDetail(this.mapRoles.get(attackerID).getName(), damage), (ov, nv) -> {ov.damage += nv.damage; return ov;});
		}
		
		void syncDamageRank(MapRole self)
		{
			ms.getRPCManager().sendStrPacket(self.id, new SBean.map_copy_damage_rank(damageRank));
		}
		
		void delRole(int rid)
		{
			super.delRole(rid);
			
			if(this.isMapAlreadyFinish)
				return;
			
			for(int r: this.mapRoles.keySet())
			{
				if(r > 0)
					return;
			}
			
			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, 0);
			this.setMapFinish();
		}
	}
	
	public static class JusticeMapCopy extends MapCopy
	{
		Map<Integer, SBean.MapSkillData> roleMapSkillInfos = new HashMap<>();
		JusticeMapCopy(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, int winCondition, Map<Integer, Integer> bosses)
		{
			super(cfg, mapInstanceID, ms, gridSize, winCondition, bosses);
		}

		JusticeMapCopy start()
		{
			super.start();
			SBean.JusticeMapCopyCFGS mCfg = GameData.getInstance().getJusticeMapCopyCFGS(this.mapID);
			if (mCfg == null)
				return null;
			this.timeout = ms.getMapManager().getMapLogicTime() + mCfg.maxTime * 1000L;
			return this;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_JUSTICE;
		}
		
		boolean ignoreTeamMemberDistance()
		{
			return true;
		}
		
		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut() || this.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT)
				return;

			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_NO_SCORE);
			this.setMapFinish();
		}
		
		@Override
		void processMapFinish()
		{
			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_NO_SCORE);
			this.setMapFinish();
			this.clearAllMonster = true;
		}
		
		void onMapFinishHandler()
		{
			if(!this.mapMonsters.isEmpty() && this.clearAllMonster)
				this.clearMonster();
		}

		public int checkCanUseMapSkill(int id, int skillID)
		{
			SBean.MapSkillCFGS skill = GameData.getInstance().getMapSkillCFGS(this.mapID);
			if (skill == null || !skill.skills.containsKey(skillID))
				return 0;
			SBean.MapSkillInstance skillInstance = skill.skills.get(skillID);
			SBean.MapSkillData info = roleMapSkillInfos.get(id);
			if (info != null && skillInstance.skillTime > 0 && info.skillUseTime.getOrDefault(skillID, 0) >= skillInstance.skillTime)
				return 0;
			if (skill.isCommonCD == 1 && info != null && info.skillCommonUseTime + skill.commonCD > GameTime.getTime())
				return 0;
			return skillInstance.skillLvl;
		}
		
		public void onUseMapSkill(int id, int skillID)
		{
			if (!roleMapSkillInfos.containsKey(id))
				roleMapSkillInfos.put(id, new SBean.MapSkillData(new HashMap<>(), 0));
			SBean.MapSkillData info = roleMapSkillInfos.get(id);
			info.skillUseTime.merge(skillID, 1, (ov, nv) -> ov + nv);
			info.skillCommonUseTime = GameTime.getTime();
		}
		
		void syncEnterInfo(MapRole role)
		{
			ms.getRPCManager().sendStrPacket(role.id, new SBean.sync_role_mapskill(roleMapSkillInfos.get(role.id)));
		}
	}

	public static class ClimbTowerMap extends MapCopy
	{
		final int winCondParam;
		ClimbTowerMap(MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, int winCondition, int winCondParam, Map<Integer, Integer> bosses)
		{
			super(cfg, mapInstanceID, ms, gridSize, winCondition, bosses);
			this.winCondParam = winCondParam;
		}

		ClimbTowerMap start()
		{
			super.start();
			SBean.ClimbTowerMapCFGS ctmCfg = GameData.getInstance().getClimbTowerMapCFGS(this.mapID);
			if (ctmCfg == null)
				return null;

			this.timeout = ms.getMapManager().getMapLogicTime() + ctmCfg.maxTime * 1000L;
			return this;
		}

		@Override
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_CLIMBTOWER;
		}
		
		void onMapRoleDead(MapRole deader, MapRole killer)
		{
			super.onMapRoleDead(deader, killer);
			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_SCORE_FAILED);
			this.setMapFinish();
		}
		
		void addKillMonster(MapRole killer, Monster monster)
		{
			super.addKillMonster(killer, monster);
			if(winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_MONSTER_COUNT && this.killMonsters >= this.winCondParam)
			{
				this.setMapFinish();
				ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_NO_SCORE);
			}
		}
	}
	
	public static class ActivityMapCopy extends MapCopy
	{
		public Map<Integer, SBean.AttackDamageDetail> damageRank = new HashMap<>();
		int damageLastSyncTime = 0;
		ActivityMapCopy(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, int winCondition, Map<Integer, Integer> bosses)
		{
			super(cfg, mapInstanceID, ms, gridSize, winCondition, bosses);
		}
		
		void addRole(MapRole role)
		{
			super.addRole(role);
			damageRank.put(role.getID(), new SBean.AttackDamageDetail(role.getRoleName(), 0));
		}

		void addPet(Pet pet)
		{
			super.addPet(pet);
			damageRank.put(pet.getID(), new SBean.AttackDamageDetail("", 0));
		}
		
		ActivityMapCopy start(int maxTime)
		{
			super.start();

			this.timeout = ms.getMapManager().getMapLogicTime() + maxTime * 1000L;
			return this;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_ACTIVITY;
		}
		
		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut() || this.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT)
				return;

			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_NO_SCORE);
			this.setMapFinish();
		}
		
		public void onMonsterGetDamage(int attackerType, int attackerID, int damage)
		{
			if (attackerType == GameData.ENTITY_TYPE_PLAYER && !this.mapRoles.containsKey(attackerID))
				return;
			damageRank.merge(attackerID, new SBean.AttackDamageDetail(attackerType == GameData.ENTITY_TYPE_PLAYER ? this.mapRoles.get(attackerID).getName() : "", damage), (ov, nv) -> {ov.damage += nv.damage; return ov;});
		}
		
		void syncDamageRank(MapRole self)
		{
			ms.getRPCManager().sendStrPacket(self.id, new SBean.map_copy_damage_rank(damageRank));
		}
	}
	
	public static class WeaponMapCopy extends MapCopy
	{
		WeaponMapCopy(MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, int winCondition, Map<Integer, Integer> bosses)
		{
			super(cfg, mapInstanceID, ms, gridSize, winCondition, bosses);
		}
		
		WeaponMapCopy start(int maxTime)
		{
			super.start();

			this.timeout = ms.getMapManager().getMapLogicTime() + maxTime * 1000L;
			return this;
		}
		
		@Override
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_WEAPON;
		}
		
		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut() || this.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT)
				return;

			ms.getRPCManager().syncCommonMapCopyEnd(this.mapID, this.mapInstanceID, GameData.MAPCOPY_FINISH_NO_SCORE);
			this.setMapFinish();
		}
	}
	
	public static class SectMap extends PublicMap
	{
		Map<Integer, SBean.AttackDamageDetail> damageRank = new HashMap<>();
		int damageLastSyncTime = 0;

		SectMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
		}
		
		void addRole(MapRole role)
		{
			super.addRole(role);
			damageRank.put(role.getID(), new SBean.AttackDamageDetail(role.getRoleName(), 0));
		}

		void addPet(Pet pet)
		{
			super.addPet(pet);
			damageRank.put(pet.getID(), new SBean.AttackDamageDetail("", 0));
		}

		SectMap start()
		{
			SBean.SectMapCFGS cfg = GameData.getInstance().getSectMapCFGS(this.mapID);
			if (cfg == null)
				return null;

			this.timeout = ms.getMapManager().getMapLogicTime() + cfg.maxTime * 1000L;
			return this;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_SECT;
		}
		
		@Override
		boolean isMapFinish()
		{
			return false;
		}

		boolean updateCurSpawn()
		{
			return true;
		}

		@Override
		void processMapFinish()
		{
			this.setMapFinish();
			this.clearAllMonster = true;
		}

		void onMapFinishHandler()
		{
			if(!this.mapMonsters.isEmpty() && this.clearAllMonster)
				this.clearMonster();
		}
		
		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut())
				return;
			
			this.setMapFinish();
			this.clearAllMonster = false;
			ms.getRPCManager().syncSectMapProgress(this.mapID, this.mapInstanceID, -1, 0, 0);
		}

		@Override
		void syncGSMapStart()
		{
			ms.getRPCManager().syncSectMapCopyStart(mapID, mapInstanceID);
		}

		void resetSectMap(Map<Integer, Integer> progress)
		{
			Integer hp = 0;
			SBean.SectMapCFGS sectMapCfg = GameData.getInstance().getSectMapCFGS(this.mapID);
			if (sectMapCfg == null)
				return;

			for (Integer areaID : this.mapClusterCfg.spawnAreas)
			{
				SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(areaID);
				if (areaCfg == null)
					continue;

				for (Integer pointID : areaCfg.spawnPoint)
				{
					SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(pointID);
					if (pointCfg == null)
						continue;

					hp = progress.remove(pointID);

					if (hp == null || hp != 10000)
					{
						Monster monster = this.createMonster(pointCfg.monsterID, new GVector3(pointCfg.position), GVector3.randomRotation(), true, -1, -1);
						if(monster != null)
						{
							monster.setCurSpawnPoint(pointID);

							monster.monsterMapType = GameData.SECTMAP_MONSTER_NORMAL;
							if (sectMapCfg.monsters.contains(pointID))
								monster.monsterMapType = GameData.SECTMAP_MONSTER_PROGRESS;

							if (pointID == sectMapCfg.boss)
								monster.monsterMapType = GameData.SECTMAP_BOSS_PROGRESS;

							if (hp != null)
								monster.setCurHP((int) (monster.getMaxHP() * (10000.0f - hp) / 10000.0f));
						}
					}
				}
			}
		}
		
		void onMapRoleDead(MapRole deader, MapRole killer)
		{
			super.onMapRoleDead(deader, killer);
			ms.getRPCManager().syncSectMapProgress(this.mapID, this.mapInstanceID, 0, 0, 0);
		}
		
		public void onMonsterGetDamage(int attackerType, int attackerID, int damage)
		{
			if (attackerType == GameData.ENTITY_TYPE_PLAYER && !this.mapRoles.containsKey(attackerID))
				return;
			damageRank.merge(attackerID, new SBean.AttackDamageDetail(attackerType == GameData.ENTITY_TYPE_PLAYER ? this.mapRoles.get(attackerID).getName() : "", damage), (ov, nv) ->
			{
				ov.damage += nv.damage;
				return ov;
			});
		}

		void syncDamageRank(MapRole self)
		{
			ms.getRPCManager().sendStrPacket(self.id, new SBean.map_copy_damage_rank(damageRank));
		}
	}
	
	public static class SectGroupMap extends PublicMap
	{
		int monsterTimes;
		int curTimes;
		int startTime;
		int lastSyncDamageTime;
		public Map<Integer, Integer> killNum;
		public Map<Integer, RoleDamageDetail> damageRank;
		SectGroupMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
		}

		SectGroupMap start()
		{
			super.start();
			SBean.SectGroupMapCFGS cfg = GameData.getInstance().getSectGroupMapCFGS(this.mapID);
			if (cfg == null)
				return null;

			this.timeout = ms.getMapManager().getMapLogicTime() + cfg.maxTime * 1000L;
			this.curTimes = monsterTimes = this.mapSpawnAreas.size();
			this.startTime = GameTime.getTime();
			return this;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_SECT_GROUP;
		}
		
		boolean filterRole()
		{
			return true;
		}
		
		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if (super.onTimer(timeMillis, timeTick, logicTime))
			{
				this.updateSpawnArea(timeMillis, timeTick, logicTime);
				if (curTimes != this.mapSpawnAreas.size() + 1)
				{
					curTimes = this.mapSpawnAreas.size() + 1;
					ms.getRPCManager().syncSectGroupMapStatus(this.mapID, this.mapInstanceID, (monsterTimes - curTimes) * 10000 / monsterTimes);
				}
				if (timeTick - lastSyncDamageTime >= 5)
				{
					Set<Integer> rids = new HashSet<>();
					rids.addAll(this.mapRoles.keySet());
					lastSyncDamageTime = timeTick;
				}
				return true;
			}
			return false;
		}

		void resetSectGroupMap(Map<Integer, Integer> progress, Map<Integer, Integer> killNum, Map<Integer, SBean.RoleDamageDetail> damageRank)
		{
			this.killNum = killNum;
			this.damageRank = damageRank;
			if (progress.isEmpty())
				return;
			Integer num = 0;
			Iterator<SpawnArea> areas = this.mapSpawnAreas.iterator();
			while (areas.hasNext())
			{
				SpawnArea area = areas.next();
				SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(area.getID());
				if (areaCfg == null)
					continue;
				boolean isfinish = true;
				for (Integer pointID : areaCfg.spawnPoint)
				{
					SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(pointID);
					if (pointCfg == null)
						continue;

					num = progress.get(pointID);

					if (num == null || num < pointCfg.spawnNum.get(0))
					{
						isfinish = false;
						break;
//						Monster monster = this.createMonster(pointCfg.monsterID, new GVector3(pointCfg.position), GVector3.randomRotation(), true, -1, -1);
//						monster.setCurSpawnPoint(pointID);
//						if (hp != null)
//							monster.setCurHP((int) (monster.getMaxHP() * (10000.0f - hp) / 10000.0f));
					}
				}
				if (isfinish == true)
					areas.remove();
				else
					break;
			}
			SpawnArea area = this.mapSpawnAreas.remove(0);
			this.curAreas.clear();
			this.curAreas.add(area);
			area.addMonsters(progress);
		}
		
		@Override
		boolean isMapFinish()
		{
			return isAllMonsterKilled();
		}

		boolean updateCurSpawn()
		{
			return true;
		}

		@Override
		void processMapFinish()
		{
			this.setMapFinish();
			ms.getRPCManager().syncSectGroupMapResult(this.mapID, this.mapInstanceID, 10000);
		}

		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut())
				return;
			this.setMapFinish();
			ms.getRPCManager().syncSectGroupMapResult(this.mapID, this.mapInstanceID, (monsterTimes - curTimes) * 10000 / monsterTimes);
		}

		@Override
		void syncGSMapStart()
		{
			ms.getRPCManager().syncSectGroupMapStart(mapID, mapInstanceID);
		}

		boolean canPetRevive()
		{
			return true;
		}
		
		void syncEnterInfo(MapRole role)
		{
			ms.getRPCManager().sendStrPacket(role.id, new SBean.enter_sect_group_map(mapID, killNum, damageRank));
		}
		
		void syncSectGroupCurInfo(MapRole role)
		{
			ms.getRPCManager().sendStrPacket(role.id, new SBean.sect_group_map_sync_info(killNum, damageRank));
		}

		void addKillMonster(MapRole killer, Monster monster)
		{
			super.addKillMonster(killer, monster);
			Set<Integer> rids = new HashSet<>();
			rids.addAll(this.mapRoles.keySet());
			killNum.merge(monster.configID, 1, (ov, nv) -> ov + nv);
			ms.getRPCManager().syncSectGroupMapCopyAddKill(this.mapID, this.mapInstanceID, monster.configID, monster.spawnPointID);
		}

		public void onMonsterGetDamage(int attackerID, int damage)
		{
			if (!this.mapRoles.containsKey(attackerID))
				return;
			damageRank.merge(attackerID, new RoleDamageDetail(this.mapRoles.get(attackerID).getRoleOverview(), damage), (ov, nv) -> new RoleDamageDetail(nv.role, ov.damage + nv.damage));
		}
	}
	
	public static class EmergencyMap extends MapCopy
	{
		EmergencyMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize, GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT, new HashMap<>());
			this.timeout = -1;
		}

		void setTimeout(int lastTime)
		{
			this.timeout = ms.getMapManager().getMapLogicTime() + lastTime * 1000L;
			ms.getLogger().info("emergency map[" + this.mapID + " , " + this.mapInstanceID + " set time out " + this.timeout + " lastTime " + lastTime);
		}

		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_EMERGENCY;
		}
		
		boolean filterRole()
		{
			return true;
		}
		
		@Override
		boolean isMapFinish()
		{
			return isAllMonsterKilled();
		}

		boolean updateCurSpawn()
		{
			return true;
		}

		@Override
		void processMapFinish()
		{
			ms.getRPCManager().syncEmergencyMapEnd(this.mapID, this.mapInstanceID);
			this.setMapFinish();
		}

		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut())
				return;
			this.setMapFinish();
			
			ms.getLogger().info("emergency map[" + this.mapID + " , " + this.mapInstanceID + " time out finish ");
		}

		boolean canPetRevive()
		{
			return true;
		}
		
		void addKillMonster(MapRole killer, Monster monster)
		{
			super.addKillMonster(killer, monster);
			Set<Integer> shareRole = new HashSet<Integer>();
			shareRole.add(killer.id);
			List<MapRole> members = monster.getTeamMemberNearBy(killer, GameData.getInstance().getEmergencyCFGS().prestigeDistance);
			for (MapRole member : members)
				shareRole.add(member.id);
			ms.getRPCManager().syncEmergencyMapKillMonster(shareRole, monster.getConfigID());
		}

		@Override
		void syncGSMapStart()
		{
			ms.getRPCManager().syncEmergencyMapStart(mapID, mapInstanceID);
		}
		
		void loadMonsters()
		{
			super.loadMonsters();
			for (SpawnArea area : this.mapSpawnAreas)
			{
				this.curAreas.add(area);
				area.addMonsters(new HashMap<>());
			}
			this.mapSpawnAreas.clear();
		}
		
	}

	abstract public static class FightMap extends PublicMap
	{
		SBean.BattleArrayHp attArray = null;
		SBean.BattleArrayHp defArray = null;

		FightMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
		}

		@Override
		boolean isMapFinish()
		{
			return true;
		}

		int getCurOrNextSpawnArea()
		{
			return -1;
		}

		boolean isFightMap()
		{
			return true;
		}
		
		boolean roleDeadClearChild()
		{
			return false;
		}
		
		boolean checkBaseRoleCanAttack(BaseRole attacker, BaseRole target)
		{
			if (attacker.owner == null || target.owner == null)
				return true;

			if (attacker.owner == target.owner)
				return false;
			
			if (!target.active || attacker.isInProtectTime() || target.isInProtectTime())
				return false;

			return true;
		}
		
		//不包括机器人
		Set<Integer> getRoleIDsNearBy(int gridX, int gridZ, MapRole self, boolean big)
		{
			Set<Integer> rids = new HashSet<>();
			this.mapRoles.values().stream().filter(r -> r != self && r.active && !r.robot).forEach(r -> rids.add(r.id));
			return rids;
		}
		
		//包括机器人
		List<MapRole> getRoleNearBy(int gridX, int gridZ, MapRole self, boolean big)
		{
			List<MapRole> lst = new ArrayList<>();
			this.mapRoles.values().stream().filter(r -> r != self && r.active).forEach(r -> lst.add(r));
			return lst;
		}
		
		List<Pet> getPetNearBy(int gridX, int gridZ)
		{
			List<Pet> lst = this.mapPetCluster.isEmpty() ? GameData.emptyList() : new ArrayList<>();
			this.mapPetCluster.values().forEach(c -> {
				c.pets.values().stream().filter(p -> p.active).forEach(p -> lst.add(p));
			});
			return lst;
		}

		List<Blur> getBlurNearBy(int gridX, int gridZ)
		{
			List<Blur> lst = this.mapBlurs.isEmpty() ? GameData.emptyList() : new ArrayList<>();
			this.mapBlurs.values().stream().filter(b -> !b.isDead()).forEach(b -> lst.add(b));
			return lst;
		}
		
		//包括机器人
		EnterInfo getEntitiesNearBy(int gridX, int gridZ, MapRole self)
		{
			EnterInfo enterInfo = new EnterInfo().createNew();
			this.mapRoles.values().stream().filter(r -> r != self).forEach(r -> enterInfo.roles.put(r.getID(), r.getEnterDetail()));
			this.mapPetCluster.values().stream().filter(c -> c.id != self.id).forEach(c -> {
				c.pets.values().forEach(p -> enterInfo.pets.add(p.getEnterPet()));
			});
			this.skillEntitys.values().stream().filter(s -> !s.isDead()).forEach(s -> enterInfo.skillEntitys.add(s.getEnterSkillEntity()));
			this.mapBlurs.values().stream().filter(b -> !b.isDead()).forEach(b -> enterInfo.blurs.add(b.getEnterDetail()));
			this.mapMonsters.values().stream().filter(m -> !m.isDead()).forEach(m -> enterInfo.monsters.add(m.getEnterMonster()));
//			this.npcs.values().forEach(n -> enterInfo.npcs.add(n.getEnterBase()));
			this.mapMinerals.values().stream().filter(m -> m.mineralCount != 0).forEach(m -> enterInfo.minerals.add(m.getEnterMineral()));
			this.mapTraps.values().forEach(t -> enterInfo.traps.add(t.getEnterBase()));
//			this.wayPoints.values().forEach(w -> enterInfo.wayPoints.add(w.getEnterBase()));
			this.mapMapBuffs.values().forEach(m -> enterInfo.mapBuffs.add(m.getEnterBase()));
			return enterInfo;
		}
		
		List<Monster> getMonsterNearBy(int gridX, int gridZ)
		{
			return GameData.emptyList();
		}
		
		List<Trap> getTrapNearBy(int gridX, int gridZ)
		{
			return GameData.emptyList();
		}
		
		void processMapFinish()
		{
			boolean selfDead = false;
			boolean robotDead = false;
			for (MapRole r : this.mapRoles.values())
			{
				if (r.isDead())
				{
					PetCluster cluster = this.mapPetCluster.get(r.getID());
					if (cluster != null)
					{
						for (Pet p : cluster.pets.values())
						{
							if (!p.isDead())
							{
								if (r.robot)
								{
									robotDead = false;
									break;
								}
								else
								{
									selfDead = false;
									break;
								}
							}
							else
							{
								if (r.robot)
									robotDead = true;
								else
									selfDead = true;
							}
						}
					}
					else
					{
						if (r.robot)
							robotDead = true;
						else
							selfDead = true;
					}
				}
			}

			if (selfDead || robotDead)
			{
				this.notifyFightMapEnd(selfDead ? 0 : 1);
				this.setMapFinish();
			}
		}

		boolean notifyFightMapEnd(int win)
		{
			for (MapRole r : mapRoles.values())
			{
				if (r.robot)
				{
					defArray = this.getBattleArrayHp(r);
					if(r.isMoving())
					{
						SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
						r.onStopMove(r.curPosition, timeTick, true);
					}
				}
				else
					attArray = this.getBattleArrayHp(r);
			}

			return attArray != null && defArray != null;
		}

		private SBean.BattleArrayHp getBattleArrayHp(MapRole role)
		{
			SBean.BattleArrayHp array = new SBean.BattleArrayHp(role.id, new SBean.Hp(role.curHP, role.getMaxHP()), new HashMap<>());
			PetCluster cluster = this.mapPetCluster.get(role.getID());
			if (cluster != null)
			{
				for (Pet pet : cluster.pets.values())
					array.petsHp.put(pet.id, new SBean.Hp(pet.curHP, pet.getMaxHP()));
			}

			return array;
		}

		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut())
				return;

			this.notifyFightMapEnd(0);
			this.setMapFinish();
		}

		MapRole resetFightMap(SBean.BattleArray array, int prepareTime)
		{
			MapRole role = new MapRole(this.ms, true).createRobotRole(array.fightRole, array.fightPets, array.petSeq, this.mapClusterCfg);
			this.addRole(role);
			role.update(-1, 0);
			
			if (this.isInPrepared(ms.getMapManager().getMapLogicTime()))
			{
				role.addState(Behavior.EBPREPAREFIGHT);
				role.specialState.put(Behavior.EBPREPAREFIGHT, this.prepareTime);
			}
			Set<Integer> rids = role.getRoleIDsNearBy(role);
			role.onSelfEnterNearBy(rids);
			role.robotRoleSummonPets(prepareTime);
			return role;
		}
	}

	public static class ArenaMap extends FightMap
	{
		ArenaMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
			SBean.ArenaCFGS arenaCfg = GameData.getInstance().getArenaCFGS();
			this.timeout = ms.getMapManager().getMapLogicTime() + arenaCfg.arenaMaxTime * 1000L;
			this.prepareTime = ms.getMapManager().getMapLogicTime() + arenaCfg.prepareTime * 1000L;
		}

		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_ARENA;
		}
		
		public GVector3 createPetPos(MapRole owner, int radius, float angle, int seq)
		{
			SBean.Vector3 pos = GameData.getInstance().getArenaPetPosBySeq(this.mapID, seq, !owner.robot);
			if(pos != null)
				return new GVector3(pos);
			
			return super.createPetPos(owner, radius, angle, seq);
		}
		
		@Override
		void syncGSMapStart()
		{
			ms.getRPCManager().syncArenaMapStart(mapID, mapInstanceID);
		}

		boolean notifyFightMapEnd(int win)
		{
			if (!super.notifyFightMapEnd(win))
				return false;

			ms.getRPCManager().syncArenaMapEnd(this.mapID, this.mapInstanceID, win, attArray, defArray);
			return true;
		}
	}

	abstract public static class SuperArenaMap extends FightMap
	{
		abstract void onGroupAllDead(int loseGroup);
		
		final SBean.SuperArenaTypeCFGS typeCfg;
		SBean.SuperArenaBattleResult result;
		Map<Integer, Integer> reviveTimes;		//<roleID,count>
		long earliestCloseTime;
		long trapsTrigTime;
		Map<Integer, Integer> roleHonors = new HashMap<>();
		Map<Integer, Byte> roleAttack = new HashMap<>();
		Map<Integer, Integer> roleAssist = new HashMap<>();
		Set<Integer> oneoffTraps = new HashSet<>();
		
		private final long autoReviveTime;
		
		SuperArenaMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, SBean.SuperArenaTypeCFGS typeCfg)
		{
			super(cfg, mapInstanceID, ms, gridSize);
			this.typeCfg = typeCfg;
			this.timeout = ms.getMapManager().getMapLogicTime() + typeCfg.maxTime * typeCfg.races * 1000L;
			this.prepareTime = ms.getMapManager().getMapLogicTime() + GameData.getInstance().getSuperArenaCFGS().normal.prepareTime * 1000L;
			this.earliestCloseTime = this.prepareTime;
			this.trapsTrigTime = this.prepareTime;
			this.autoReviveTime = GameData.getInstance().getSuperArenaCFGS().normal.autoRevive * 1000L;
//			this.earliestCloseTime = ms.getMapManager().getMapLogicTime() + this.prepareTime * 1000L;
			this.reviveTimes = new HashMap<>();
			result = new SBean.SuperArenaBattleResult(0, new HashMap<Integer, SBean.SABattleTeamInfo>());
			SBean.SuperArenaMapCFGS mapCfg = GameData.getInstance().getSuperArenaMapCFGS(this.mapID);
			if(mapCfg != null)
				oneoffTraps = new HashSet<>(mapCfg.oneoffTraps);
		}
		
		boolean checkBaseRoleCanAttack(BaseRole attacker, BaseRole target)
		{
			if (attacker.owner == null || target.owner == null)
				return true;

			if (attacker.owner == target.owner)
				return false;
			
			if (attacker.isInProtectTime() || target.isInProtectTime() || attacker.owner.isTeamMember(target.owner))
				return false;

			return true;
		}
		
		boolean roleDeadClearChild()
		{
			return true;
		}
		
		boolean canPetRevive()
		{
			return true;
		}
		
		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if (super.onTimer(timeMillis, timeTick, logicTime))
			{
				checkOneOffTraps(logicTime);
				return true;
			}
			return false;
		}
		
		private void checkOneOffTraps(long logicTime)
		{
			if(this.oneoffTraps.isEmpty())
				return;
			
			if(logicTime > this.trapsTrigTime)
			{
				for (Integer tid : this.oneoffTraps)
				{
					Trap t = this.getConfigTrap(tid);
					if (t != null)
						t.setTrapState(MapManager.ESTRAP_TRIG);
				}
				
				this.oneoffTraps.clear();
			}
		}
		
		void checkAutoRevive(long logicTime)
		{
			if(this.roleAutoRevives.isEmpty())
				return;
			
			Iterator<Map.Entry<Integer, Long>> it = this.roleAutoRevives.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<Integer, Long> e = it.next();
				if(logicTime < e.getValue())
					continue;
				
				it.remove();
				int count = this.reviveTimes.getOrDefault(e.getKey(), 0);
				if(count == 0)
					continue;
				
				MapRole role = this.mapRoles.get(e.getKey());
				if(role == null || !role.isDead())
					continue;
				
				role.autoRevive();
				count--;
				this.reviveTimes.put(e.getKey(), count);
			}
		}
		
		void setAutoRevive(int roleID)
		{
			MapRole role = this.getRole(roleID);
			if(role == null)
				return;
			
			int count = this.reviveTimes.getOrDefault(roleID, 0);
			if(count <= 0)
			{
				role.setGhost();
				return;
			}
			
			this.roleAutoRevives.put(roleID, ms.getMapManager().getMapLogicTime() + this.autoReviveTime);
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_SUPERARENA;
		}
		
		void syncEnterInfo(MapRole role)
		{
			ms.getRPCManager().sendStrPacket(role.getID(), new SBean.role_spawn_point(role.isMainSpawnPos ? 1 : 2));
			if(role.ghost)
				ms.getRPCManager().sendStrPacket(role.id, new SBean.role_ghost());
			ms.getRPCManager().sendStrPacket(role.id, new SBean.superarena_info(getSuperArenaMemberLives(role), getSuperArenaEnemies(role)));
		}
		
		private Map<Integer, SBean.SuperArenaEnemy> getSuperArenaEnemies(MapRole self)
		{
			Map<Integer, SBean.SuperArenaEnemy> enemies = new HashMap<>();
			for(MapRole r: this.mapRoles.values())
			{
				if(self.isMainSpawnPos != r.isMainSpawnPos)
				{
					int lives = this.reviveTimes.getOrDefault(r.id, 0) + (r.isDead() ? 0 : 1);
					enemies.put(r.id, new SBean.SuperArenaEnemy(r.id, r.roleName, r.headIcon, r.getBWType(), r.curHP, r.getCurHP(), lives));
				}
			}
			
			return enemies;
		}
		
		private Map<Integer, Integer> getSuperArenaMemberLives(MapRole self)
		{
			Map<Integer, Integer> lives = new HashMap<>();
			for(MapRole r: this.mapRoles.values())
			{
				if(self.isMainSpawnPos == r.isMainSpawnPos)
				{
					int l = this.reviveTimes.getOrDefault(r.id, 0) + (r.isDead() ? 0 : 1);
					lives.put(r.id, l);
				}
			}
			
			return lives;
		}
		
		//包括机器人
//		EnterInfo getEntitiesNearBy(int gridX, int gridZ, MapRole self)
//		{
//			EnterInfo enterInfo = new EnterInfo();
//			enterInfo.roles = new ArrayList<>();
//			this.mapRoles.values().stream().filter(r -> r != self).forEach(r -> enterInfo.roles.add(r.getEnterDetail()));
//			return enterInfo;
//		}
		
		void addRole(MapRole role)
		{
			super.addRole(role);
			this.superArenaAddRole(role);
			addBaseBuff(role);
		}
		
		void superArenaAddRole(MapRole role)
		{
			int groupID = role.isMainSpawnPos ? 1 : 2;
			SBean.SABattleTeamInfo teamInfo = result.teams.get(groupID);
			if(teamInfo == null)
			{
				teamInfo = new SBean.SABattleTeamInfo(new HashMap<>(), new ArrayList<>());
				result.teams.put(groupID, teamInfo);
			}
			
			teamInfo.members.put(role.id, new SBean.SABattleInfo(role.id, role.roleName, role.level, role.gender, role.headIcon, role.getBWType(), role.configID, role.getFightPower(), 0, 0, 0, 0, 0));
		}
		
		void addBaseBuff(MapRole role)
		{			
			//基础buff
			SBean.BuffCFGS baseBuffCfg = GameData.getInstance().getBuffCFG(GameData.getInstance().getSuperArenaCFGS().buff.baseBuff);
			if(baseBuffCfg != null)
			{
				Buff baseBuff = role.createNewBuff(baseBuffCfg, 0, 0, role, null);
				baseBuff.endTime = -1;
				baseBuff.spiritEffectID = -1;
				role.addBuff(baseBuff, role, null);
			}
			
			//连败buff
			if(role.dayFailedStreak > 0)
			{
				int failedBuffID = GameData.getInstance().getSuperArenaFailedBuff(role.dayFailedStreak);
				SBean.BuffCFGS failedBuffCfg = GameData.getInstance().getBuffCFG(failedBuffID);
				if(failedBuffCfg != null)
				{
					Buff failedBuff = role.createNewBuff(failedBuffCfg, 0, 0, role, null);
					failedBuff.endTime = -1;
					failedBuff.spiritEffectID = -1;
					role.addBuff(failedBuff, role, null);
				}
			}
		}
		
		void addRoleMapHonor(int roleID, int value)
		{
			this.roleHonors.compute(roleID, (k, v) -> v == null ? value : v + value);
		}
		
		int getRoleMapHonor(int roleID)
		{
			return this.roleHonors.getOrDefault(roleID, 0);
		}
		
		int getRoleAssist(int roleID)
		{
			return this.roleAssist.getOrDefault(roleID, 0);
		}
		
		void setRoleAttack(int roleID)
		{	
			this.roleAttack.put(roleID, (byte)1);
		}
		
		@Override
		void syncGSMapStart()
		{
//			ms.getRPCManager().syncSuperArenaMapStart(this.mapID, this.mapInstanceID);
		}

		void processMapFinish()
		{
			if(ms.getMapManager().getMapLogicTime() <= this.earliestCloseTime)
				return;
			
			Map<Integer, Integer> groupRoles = new HashMap<>(); //<groupID, roles>
			Map<Integer, Integer> groupDeadRoles = new HashMap<>(); //<groupID, daedRoles>
			for (MapRole r : this.mapRoles.values())
			{
				int groupID = r.isMainSpawnPos ? 1 : 2;
				groupRoles.compute(groupID, (k, v) -> v == null ? 1 : ++v);
				int count = this.reviveTimes.getOrDefault(r.id, 0);
				if (r.isDead() && count <= 0)
					groupDeadRoles.compute(groupID, (k, v) -> v == null ? 1 : ++v);
			}
			
			//对方人死光了
			for (Map.Entry<Integer, Integer> e : groupRoles.entrySet())
			{
				int groupID = e.getKey();
				if (groupDeadRoles.get(groupID) == e.getValue())
				{
					this.onGroupAllDead(groupID);
					return;
				}
			}
			
			//对方人全部离开了
			for(Map.Entry<Integer, SBean.SABattleTeamInfo> e: result.teams.entrySet())
			{
				int groupID = e.getKey();
				SBean.SABattleTeamInfo teamInfo = e.getValue();
				int count = 0;
				for(SBean.SABattleInfo info: teamInfo.members.values())
				{
					MapRole role = this.mapRoles.get(info.rid);
					if(role != null)
						break;
					
					count++;
				}
				
				if(count == teamInfo.members.size())
				{
					this.notifyFightMapEnd(groupID);
					this.setMapFinish();
					return;
				}
			}
		}

		boolean notifyFightMapEnd(int loseGroup)
		{
			ms.getRPCManager().notifyFightSuperArenaMapEnd(this.mapID, this.mapInstanceID, getBattleResult(loseGroup));
			return true;
		}
		
		SBean.SuperArenaBattleResult getBattleResult(int lostTeam)
		{
			return null;
		}
	}
	
	public static class SuperArenaNormalMap extends SuperArenaMap
	{
		SuperArenaNormalMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, SBean.SuperArenaTypeCFGS typeCfg)
		{
			super(cfg, mapInstanceID, ms, gridSize, typeCfg);
		}
		
		@Override
		void onGroupAllDead(int loseGroup)
		{
			this.notifyFightMapEnd(loseGroup);
			this.setMapFinish();
		}
		
		void superArenaAddRole(MapRole role)
		{
			super.superArenaAddRole(role);
			this.reviveTimes.put(role.id, GameData.getInstance().getSuperArenaCFGS().normal.reviveTimes);
		}
		
		int addRoleMapKills(MapRole killer, MapRole deader)
		{
			int count = super.addRoleMapKills(killer, deader);
			this.setRoleHonor(killer, deader, killer.getKillRoleCount(deader.id));
			return count;
		}
		
		void setRoleHonor(MapRole killer, MapRole deader, int count)
		{	
			SBean.SuperArenaCFGS cfg = GameData.getInstance().getSuperArenaCFGS();
			int killAdd = GameData.getSuperArenaKillHonor(cfg.normal.killHonors, count);
			this.addRoleMapHonor(killer.id, killAdd);
			
			List<SBean.RoleKill> assists = GameData.emptyList();
			int groupID = killer.isMainSpawnPos ? 1 : 2;
			SBean.SABattleTeamInfo teamInfo = result.teams.get(groupID);
			if(teamInfo != null)
			{
				int otherHonor = GameData.getSuperArenaKillHonor(cfg.normal.otherKillHonors, count);
				assists = new ArrayList<>();
				for(SBean.SABattleInfo e: teamInfo.members.values())
				{
					MapRole assister = getRole(e.rid);
					if(assister == null || killer.getID() == e.rid || assister.curPosition.distance(killer.curPosition) > cfg.normal.addHonorRange)
						continue;
					
					addRoleMapHonor(e.rid, otherHonor);
					assists.add(new SBean.RoleKill(assister.id, assister.roleName, otherHonor));
					this.roleAssist.compute(e.rid, (k,v) -> v == null ? 1 : v + 1);
				}
			}
			
			Set<Integer> rids = new HashSet<>();
			this.mapRoles.values().forEach(r -> rids.add(r.id));
			if (this.getTotalDeadTimes() == 1)				//首杀有额外奖励
			{
				this.addRoleMapHonor(killer.id, cfg.normal.firstBloodHonor);
				killAdd += cfg.normal.firstBloodHonor;
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_first_blood(new SBean.RoleKill(killer.id, killer.roleName, killAdd), 
																						 new SBean.RoleKill(deader.id, deader.roleName, 0), 
																						 assists));
			}
			else
			{
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_kill(new SBean.RoleKill(killer.id, killer.roleName, killAdd), 
																					   new SBean.RoleKill(deader.id, deader.roleName, 0), 
																					   assists));
			}
		}
		
		SBean.SuperArenaBattleResult getBattleResult(int lostTeam)
		{
			SBean.SuperArenaCFGS sacfg = GameData.getInstance().getSuperArenaCFGS();
			Set<Integer> mostKills = new HashSet<>();
			if(mostKillRole > 0)
			{
				for(SBean.SABattleTeamInfo teamInfo: result.teams.values())
				{
					for(SBean.SABattleInfo info: teamInfo.members.values())
					{
						if (this.getRoleMapKills(info.rid) == mostKillRole)
							mostKills.add(info.rid);
					}
				}
			}
			
			int mostKillHonor = mostKills.isEmpty() ? 0 : sacfg.normal.mostKillHonor / mostKills.size();
			for(Map.Entry<Integer, SBean.SABattleTeamInfo> e: result.teams.entrySet())
			{
				int teamID = e.getKey();
				SBean.SABattleTeamInfo teamInfo = e.getValue();
				for(SBean.SABattleInfo info: teamInfo.members.values())
				{
					int addHonor = this.getRoleMapHonor(info.rid);
					MapRole role = this.mapRoles.get(info.rid);
					if(role != null || this.reviveTimes.getOrDefault(info.rid, 0) == 0)
					{
						if (lostTeam == 0 || teamID == lostTeam)
							addHonor +=  this.roleAttack.containsKey(info.rid) ? this.typeCfg.loseHonor : this.typeCfg.hangLoseHonor;
						else
							addHonor += this.roleAttack.containsKey(info.rid)  ? this.typeCfg.winHonor : this.typeCfg.hangWinHonor;
		
						if (mostKills.contains(info.rid))
							addHonor += mostKillHonor;
					}
	
					info.kills = this.getRoleMapKills(info.rid);
					info.dead = this.getRoleMapDeadTimes(info.rid);
					info.assist = this.getRoleAssist(info.rid);
					info.addHonor = addHonor;
				}
			}
			result.loseTeam = lostTeam;
			return result;
		}
	}
	
	//2v2 竞技场 3局两胜
	public static class SuperArenaThreeBestMap extends SuperArenaMap
	{
		private static final int TOTAL_PLAY_COUNT = 3;
		List<Integer> loseGroups = new ArrayList<>();
		long raceEndTime;
		long delayEnterRaceTime;
		
		SuperArenaThreeBestMap(MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, SuperArenaTypeCFGS typeCfg)
		{
			super(cfg, mapInstanceID, ms, gridSize, typeCfg);
			this.raceEndTime = ms.getMapManager().getMapLogicTime() + typeCfg.maxTime * 1000L;
		}
		
		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if(super.onTimer(timeMillis, timeTick, logicTime))
			{
				this.checkEnterRace(logicTime);
				this.checkRaceTimeOut(logicTime);
				return true;
			}
			return false;
		}
		
		void syncEnterInfo(MapRole self)
		{
			super.syncEnterInfo(self);
			syncSuperArenaInfo(self);
		}
		
		void syncSuperArenaInfo(MapRole self)
		{
			int groupID = self.isMainSpawnPos ? 1 : 2;
			List<Integer> results = new ArrayList<>();
			for(int loseGroup: this.loseGroups)
			{
				if(loseGroup == 0)
					results.add(GameData.SUPER_ARENA_RESULT_NONE);
				else
					results.add(loseGroup == groupID ? GameData.SUPER_ARENA_RESULT_LOSE : GameData.SUPER_ARENA_RESULT_WIN);
			}
			ms.getRPCManager().sendStrPacket(self.id, new SBean.superarena_race_results(results));
		}
		
		void processMapFinish()
		{
			if(this.delayEnterRaceTime > 0)
				return;
			
			super.processMapFinish();
		}
		
		private void checkEnterRace(long logicTime)
		{
			if(this.delayEnterRaceTime == 0)
				return;
			
			if(this.delayEnterRaceTime > logicTime)
				return;
			
			this.reEnterRace();
			this.delayEnterRaceTime = 0;
		}
		
		void checkTimeOut()
		{
			
		}
		
		private void checkRaceTimeOut(long logicTime)
		{
			if(this.raceEndTime == 0 || this.isMapAlreadyFinish)
				return;
			
			if(this.raceEndTime > logicTime)
				return;
			
			if(this.delayEnterRaceTime == 0)
			{
				for(MapRole role: this.mapRoles.values())
					ms.getRPCManager().sendStrPacket(role.id, new SBean.superarena_race_result(GameData.SUPER_ARENA_RESULT_NONE));
				
				if(this.loseGroups.size() == TOTAL_PLAY_COUNT - 1)
				{
					this.onGroupAllDead(0);
					this.raceEndTime = 0;
					return;
				}
				this.loseGroups.add(0);
			}
			
			this.reEnterRace();
		}
		
		@Override
		void onGroupAllDead(int loseGroup)
		{
			this.loseGroups.add(loseGroup);
			int group1LoseTimes = 0;
			int group2LoseTimes = 0;
			for(int group: this.loseGroups)
			{
				if(group == 1)
					group1LoseTimes++;
				else if(group == 2)
					group2LoseTimes++;
			}
			
			if(group1LoseTimes >= 2 || group2LoseTimes >= 2 || this.loseGroups.size() == TOTAL_PLAY_COUNT)
			{
				this.notifyFightMapEnd(calcLoseTeam(group1LoseTimes, group2LoseTimes));
				this.setMapFinish();
				return;
			}
			
			for(MapRole role: this.mapRoles.values())
			{
				if(loseGroup == 0)
					ms.getRPCManager().sendStrPacket(role.id, new SBean.superarena_race_result(GameData.SUPER_ARENA_RESULT_NONE));
				else
				{
					int groupID = role.isMainSpawnPos ? 1 : 2;
					ms.getRPCManager().sendStrPacket(role.id, new SBean.superarena_race_result(groupID == loseGroup ? GameData.SUPER_ARENA_RESULT_LOSE : GameData.SUPER_ARENA_RESULT_WIN));
				}
			}
			this.delayEnterRaceTime = ms.getMapManager().getMapLogicTime() + 1000L;
		}
		
		private void reEnterRace()
		{
			SBean.SuperArenaMapCFGS mapCfg = GameData.getInstance().getSuperArenaMapCFGS(this.mapID);
			if(mapCfg != null)
				oneoffTraps = new HashSet<>(mapCfg.oneoffTraps);
			
			for (Integer tid : this.oneoffTraps)
			{
				Trap t = this.getConfigTrap(tid);
				if (t != null)
					t.setTrapState(MapManager.ESTRAP_ACTIVE);
			}
			this.trapsTrigTime = ms.getMapManager().getMapLogicTime() + GameData.getInstance().getSuperArenaCFGS().normal.prepareTime * 1000L;
			
			for(MapRole role: this.mapRoles.values())
				role.superAreanRaceEnd();
			
			ms.getRPCManager().notifyFightSuperArenaRaceEnd(this.mapID, this.mapInstanceID);
			raceEndTime = ms.getMapManager().getMapLogicTime() + typeCfg.maxTime * 1000L;
		}
		
		void setAutoRevive(int roleID)
		{
			MapRole role = this.getRole(roleID);
			if(role == null)
				return;
			
			role.setGhost();
		}
		
		private int calcLoseTeam(int group1LoseTimes, int group2LoseTimes)
		{
			if(group1LoseTimes != group2LoseTimes)
				return group1LoseTimes > group2LoseTimes ? 1 : 2;
				
			int group1DeadTimes = 0;
			int group2DeadTimes = 0;
			for(Map.Entry<Integer, SBean.SABattleTeamInfo> e: result.teams.entrySet())
			{
				int groupID = e.getKey();
				for(SBean.SABattleInfo info: e.getValue().members.values())
				{
					if(groupID == 1)
						group1DeadTimes += this.roleDeads.getOrDefault(info.rid, 0);
					else if(groupID == 2)
						group2DeadTimes += this.roleDeads.getOrDefault(info.rid, 0);
				}
			}
			
			if(group1DeadTimes == group2DeadTimes)
				return 0;	//平
			
			return group1DeadTimes > group2DeadTimes ? 1 : 2;
		}
		
		SBean.SuperArenaBattleResult getBattleResult(int lostTeam)
		{
			for(Map.Entry<Integer, SBean.SABattleTeamInfo> e: result.teams.entrySet())
			{
				int teamID = e.getKey();
				SBean.SABattleTeamInfo teamInfo = e.getValue();
				teamInfo.results.clear();
				for(int loseGroup: this.loseGroups)
				{
					if(loseGroup == 0)
						teamInfo.results.add(GameData.SUPER_ARENA_RESULT_NONE);
					else
						teamInfo.results.add(loseGroup == teamID ? GameData.SUPER_ARENA_RESULT_LOSE : GameData.SUPER_ARENA_RESULT_WIN);
				}
				
				for(SBean.SABattleInfo info: teamInfo.members.values())
				{
					int addHonor = this.getRoleMapHonor(info.rid);
					MapRole role = this.mapRoles.get(info.rid);
					if(role != null || this.reviveTimes.getOrDefault(info.rid, 0) == 0)
					{
						if (lostTeam == 0 || teamID == lostTeam)
							addHonor +=  this.roleAttack.containsKey(info.rid) ? this.typeCfg.loseHonor : this.typeCfg.hangLoseHonor;
						else
							addHonor += this.roleAttack.containsKey(info.rid)  ? this.typeCfg.winHonor : this.typeCfg.hangWinHonor;
					}
	
					info.kills = this.getRoleMapKills(info.rid);
					info.dead = this.getRoleMapDeadTimes(info.rid);
					info.assist = this.getRoleAssist(info.rid);
					info.addHonor = addHonor;
				}
			}
			result.loseTeam = lostTeam;
			return result;
		}
	}
	
	public static class BWArenaMap extends FightMap
	{

		BWArenaMap(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
			this.timeout = ms.getMapManager().getMapLogicTime() + GameData.getInstance().getBWArenaCFGS().fight.maxTime * 1000L;
			this.prepareTime = ms.getMapManager().getMapLogicTime() + GameData.getInstance().getBWArenaCFGS().fight.prepareTime * 1000L;
		}

		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_BWARENA;
		}
		
		void addRole(MapRole role)
		{
			super.addRole(role);
			role.setCurSP((int)(role.getMaxSP() / 2.f));
		}
		
		public GVector3 createPetPos(MapRole owner, int radius, float angle, int seq)
		{
			SBean.Vector3 pos = GameData.getInstance().getBWArenaPetPosBySeq(this.mapID, seq, !owner.robot);
			if(pos != null)
				return new GVector3(pos);
			
			return super.createPetPos(owner, radius, angle, seq);
		}
		
		@Override
		void syncGSMapStart()
		{
			ms.getRPCManager().syncBWArenaMapStart(mapID, mapInstanceID);
		}

		boolean notifyFightMapEnd(int win)
		{
			if (!super.notifyFightMapEnd(win))
				return false;

			ms.getRPCManager().syncBWArenaMapEnd(this.mapID, this.mapInstanceID, win, attArray, defArray);
			return true;
		}
		
		void resetBWArenaMap(SBean.BattleArray array, boolean petLack)
		{
			MapRole robot = this.resetFightMap(array, GameData.getInstance().getBWArenaCFGS().fight.prepareTime);
			if(petLack)
				robot.setPetLack(petLack);
		}
	}
	
	public static class ForceWarMap extends PublicMap
	{
		final private static int DOUCLE_SCORE_VALUE = 2;
		
		private int whiteMostKill;
		private int blackMostKill;
		
		private int whiteCampScore;
		private int blackCampScore;
		
		private Map<Integer, SBean.ForceWarOverview> whiteSide = new HashMap<>();		//<roleID, ForceWarOverview>
		private Map<Integer, SBean.ForceWarOverview> blackSide = new HashMap<>();		//<roleID, ForceWarOverview>
		private Map<Integer, Integer> roleKillNpcs = new HashMap<>();					// <roleID, count>
		private Map<Integer, Integer> roleKillStreaks = new HashMap<>();
		
		private Set<Integer> statues = new HashSet<>();									//所有的雕像包括水晶
		private int totalNormalStatue;
		private int totalBigStatue;
		private int whiteDeadStatues;
		private int blackDeadStatues;
		private int whiteBossStatue;
		private int blackBossStatue;
		
		private final long doubleScoreStartTime;
		private final int assistDistance;
		private final int killStreaks;
		private final int endKills;
		private final long autoReviveTime;
		
		ForceWarMap(MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
			this.prepareTime = ms.getMapManager().getMapLogicTime() + GameData.getInstance().getForceWarBaseCFGS().other.prepareTime * 1000L;
			this.doubleScoreStartTime = ms.getMapManager().getMapLogicTime() + GameData.getInstance().getForceWarBaseCFGS().other.doubleScoreStartTime * 1000L;
			this.assistDistance = GameData.getInstance().getForceWarBaseCFGS().other.assistDistance;
			this.killStreaks = GameData.getInstance().getForceWarBaseCFGS().other.killStreaks;
			this.endKills = GameData.getInstance().getForceWarBaseCFGS().other.endKills;
			this.autoReviveTime = GameData.getInstance().getForceWarBaseCFGS().other.autoRevive * 1000L;
			
			SBean.ForceWarMapCFGS mCfg = GameData.getInstance().getForceWarMapCFGS(this.mapID);
			if(mCfg != null)
			{
				totalNormalStatue = mCfg.normalStatues;
				totalBigStatue = mCfg.bigStatues;
				
				SBean.ForceWarCFGS fwcfg = GameData.getInstance().getForceWarCFGS(mCfg.type);
				if(fwcfg != null)
					this.timeout = ms.getMapManager().getMapLogicTime() + fwcfg.maxTime * 1000L;
			}
		}
		
		boolean isPKMap()
		{
			return true;
		}
		
		boolean isFightMap()
		{
			return true;
		}
		
		void addRole(MapRole role)
		{
			super.addRole(role);
			if(role.isMainSpawnPos())
			{
				role.setForceWarType(GameData.FORCEWAR_TYPE_ONE);
				this.whiteSide.put(role.getID(), new SBean.ForceWarOverview(role.getID(), 0, role.getName(), role.level, 0, 0, 0, 0, 0, 0, (byte)0));
			}
			else
			{
				role.setForceWarType(GameData.FORCEWAR_TYPE_TWO);
				this.blackSide.put(role.getID(), new SBean.ForceWarOverview(role.getID(), 0, role.getName(), role.level, 0, 0, 0, 0, 0, 0, (byte)0));
			}
			
			addBaseBuff(role);
		}
		
		void addBaseBuff(MapRole role)
		{
			SBean.BuffCFGS bCfg = GameData.getInstance().getBuffCFG(GameData.getInstance().getForceWarBaseCFGS().baseBuff);
			if(bCfg == null)
				return;
			
			Buff buff = role.createNewBuff(bCfg, 0, 0, role, null);
			buff.endTime = -1;
			buff.spiritEffectID = -1;
			role.addBuff(buff, role, null);
		}
		
		void loadMonsters()
		{
			super.loadMonsters();
			for (SpawnArea area : this.mapSpawnAreas)
			{
				this.curAreas.add(area);
				area.addMonsters(new HashMap<>());
			}
			this.mapSpawnAreas.clear();
		}
		
		void addMonster(Monster monster)
		{
			super.addMonster(monster);
			switch (monster.getBossType())
			{
				case GameData.MONSTER_BOSSTYPE_NORMALSTATUE:
					this.statues.add(monster.getID());
					break;
				case GameData.MONSTER_BOSSTYPE_BIGSTATUE:
					this.statues.add(monster.getID());
					break;
				case GameData.MONSTER_BOSSTYPE_BOSSSTATUE:
					this.statues.add(monster.getID());
					monster.addState(Behavior.EBINVINCIBLE);
					if(monster.getBWType() == GameData.BWTYPE_WHITE)
						this.whiteBossStatue = monster.getID();
					else if(monster.getBWType() == GameData.BWTYPE_BLACK)
						this.blackBossStatue = 	monster.getID();
					break;
				default :
					break;
			}
		}
		
		void syncEnterInfo(MapRole role)
		{
			ms.getRPCManager().sendStrPacket(role.id, new SBean.role_spawn_point(role.isMainSpawnPos ? 1 : 2));
			syncForwarStatues(role.getID());
		}
		
		void syncForwarStatues(int roleID)
		{
			List<SBean.EnterDetail> lst = new ArrayList<>();
			for(int s: this.statues)
			{
				Monster m = getMonster(s);
				if(m == null)
					continue;
				
				lst.add(m.getEnterDetail());
			}
			
			ms.getRPCManager().sendStrPacket(roleID, new SBean.role_forcewar_statues(lst, totalNormalStatue, totalBigStatue));
			ms.getRPCManager().sendStrPacket(roleID, new SBean.nearby_forcewar_campscore(whiteCampScore, blackCampScore));
		}
		
		void onQueryMemberPos(MapRole role)
		{
			Map<Integer, SBean.Vector3> positions = new HashMap<>();
			boolean white = role.getBWType() == 1;
			for(SBean.ForceWarOverview e: white ? this.whiteSide.values() : this.blackSide.values())
			{
				if(e.rid == role.getID())
					continue;
				
				MapRole m = this.mapRoles.get(e.rid);
				if(m == null)
					continue;
				
				positions.put(e.rid, m.getLogicPosition());
			}
			
			ms.getRPCManager().sendStrPacket(role.getID(), new SBean.forcewar_members_position(positions));
		}
		
		void onQueryForceWarResult(int roleID)
		{
			List<SBean.ForceWarOverview> white = this.whiteSide.values().stream().map(e -> e.kdClone()).collect(Collectors.toList());
			List<SBean.ForceWarOverview> black = this.blackSide.values().stream().map(e -> e.kdClone()).collect(Collectors.toList());
			ms.getRPCManager().sendStrPacket(roleID, new SBean.roles_forcewaroverview(white, black));
		}
		
		boolean checkBaseRoleCanAttack(BaseRole attacker, BaseRole target)
		{
			if(attacker.getForceType() == 0)
				return true;
			
			if (attacker == target || attacker.isInProtectTime() || target.isInProtectTime())
				return false;
			
			return attacker.getForceType() != target.getForceType();
		}
		
		@Override
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut())
				return;
			
			int winSide = this.whiteCampScore == this.blackCampScore ? 0 : (this.whiteCampScore > this.blackCampScore ? GameData.BWTYPE_WHITE : GameData.BWTYPE_BLACK);
			this.notifyForceWarMapEnd(winSide, 0);
		}
		
		@Override
		void syncGSMapStart()
		{
			// TODO Auto-generated method stub
		}

		@Override
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_FORCEWAR;
		}
		
		void delRole(int rid)
		{
			super.delRole(rid);
			if(this.isMapAlreadyFinish)
				return;
			
			int white = 0;
			int black = 0;
			for(MapRole role: this.mapRoles.values())
			{
				if(role.getBWType() == GameData.BWTYPE_WHITE)
					white++;
				else if(role.getBWType() == GameData.BWTYPE_BLACK)
					black++;
				
				if(white > 0 && black > 0)
					return;
			}
			
			if(white == 0)
			{
				notifyForceWarMapEnd(GameData.BWTYPE_BLACK, 0);
				return;
			}
			
			if(black == 0)
			{
				notifyForceWarMapEnd(GameData.BWTYPE_WHITE, 0);
				return;
			}
		}
		
		//杀怪积分
		void addKillMonster(MapRole killer, Monster monster)
		{
			super.addKillMonster(killer, monster);
			final SBean.ForceWarScore score = GameData.getInstance().getForceWarBaseCFGS().score.killMonsters.get(monster.getBossType());
			if(score == null)
				return;
			
			Set<Integer> rids = new HashSet<>();
			rids.addAll(this.mapRoles.keySet());
			this.setKillScore(killer, score, rids, monster.damageRoles.keySet(), true);
			switch (monster.getBossType())
			{
				case GameData.MONSTER_BOSSTYPE_NORMALSTATUE:
					this.delStatues(monster);
					break;
				case GameData.MONSTER_BOSSTYPE_BIGSTATUE:
					this.delStatues(monster);
					break;
				case GameData.MONSTER_BOSSTYPE_BOSSSTATUE:
					this.notifyForceWarMapEnd(killer.getBWType(), monster.getBWType());
					break;
				case GameData.MONSTER_BOSSTYPE_NPC:
					this.roleKillNpcs.compute(killer.getID(), (k,v) -> v == null ? 1 : v + 1);
					SBean.ForceWarOverview fwo = killer.getBWType() == GameData.BWTYPE_WHITE ? this.whiteSide.get(killer.getID()) : this.blackSide.get(killer.getID());
					if(fwo != null)
						fwo.killNpcs = this.roleKillNpcs.getOrDefault(killer.getID(), 0);
					return;
				default :
					return;
			}
			
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_forcewar_statues(monster.getID(), 0));
		}
		
		void delStatues(Monster monster)
		{
			this.statues.remove(monster.getID());
			if(monster.getBWType() == GameData.BWTYPE_WHITE)
			{
				this.whiteDeadStatues++;
				if(this.whiteDeadStatues == totalNormalStatue + totalBigStatue)
					setBossStatueCanAttack(this.whiteBossStatue);
			}
			else if(monster.getBWType() == GameData.BWTYPE_BLACK)
			{
				this.blackDeadStatues++;
				if(this.blackDeadStatues == totalNormalStatue + totalBigStatue)
					setBossStatueCanAttack(this.blackBossStatue);
			}
		}
		
		private void setBossStatueCanAttack(int bossStatue)
		{
			Monster boss = this.mapMonsters.get(bossStatue);
			if(boss == null)
				return;
			
			boss.removeState(Behavior.EBINVINCIBLE);
		}
		
		void updateStatues(Monster monster)
		{
			switch (monster.getBossType())
			{
				case GameData.MONSTER_BOSSTYPE_NORMALSTATUE:
				case GameData.MONSTER_BOSSTYPE_BIGSTATUE:
				case GameData.MONSTER_BOSSTYPE_BOSSSTATUE:
					break;
				default :
					return;
			}
			
			Set<Integer> rids = new HashSet<>();
			rids.addAll(this.mapRoles.keySet());
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_forcewar_statues(monster.getID(), monster.getCurHP()));
		}
		
		//杀人积分
		int addRoleMapKills(MapRole killer, MapRole deader)
		{
			int count = super.addRoleMapKills(killer, deader);
			this.setKillRoleScore(killer, deader);
			this.updateForceWarMostKill(count, killer.getBWType() == GameData.BWTYPE_WHITE);
			
			SBean.ForceWarOverview fwo = killer.getBWType() == GameData.BWTYPE_WHITE ? this.whiteSide.get(killer.getID()) : this.blackSide.get(killer.getID());
			if(fwo != null)
				fwo.kills = count;
			
			return count;
		}
		
		void updateForceWarMostKill(int count, boolean white)
		{
			if(white)
			{
				if(count > this.whiteMostKill)
					this.whiteMostKill = count;
			}
			else
			{
				if(count > this.blackMostKill)
					this.blackMostKill = count;
			}
		}
		
		void onMapRoleDead(MapRole deader, MapRole killer)
		{
			super.onMapRoleDead(deader, killer);
			
			SBean.ForceWarOverview fwo = deader.getBWType() == GameData.BWTYPE_WHITE ? this.whiteSide.get(deader.getID()) : this.blackSide.get(deader.getID());
			if(fwo != null)
				fwo.bekills = this.getRoleMapDeadTimes(deader.getID());
		}
		
		void setKillRoleScore(MapRole killer, MapRole deader)
		{
			Set<Integer> rids = new HashSet<>();
			rids.addAll(this.mapRoles.keySet());
			
			this.roleKillStreaks.compute(killer.getID(), (k, v) -> v == null ? 1 : v + 1);
			if (this.getTotalDeadTimes() == 1)				//首杀有额外奖励
			{
				final SBean.ForceWarScore score = GameData.getInstance().getForceWarBaseCFGS().score.firstKill;
				if(!rids.isEmpty())
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.forcewar_first_blood(killer.getName(), deader.getName()));
				
				this.setKillScore(killer, score, rids, deader.damageRoles, false);
			}
			
			{
				final SBean.ForceWarScore score = GameData.getInstance().getForceWarBaseCFGS().score.killRole;
				int killerKills = this.roleKillStreaks.getOrDefault(killer.getID(), 0);
				int deaderKills = this.roleKillStreaks.getOrDefault(deader.getID(), 0);
				if(!rids.isEmpty() && (killerKills >= killStreaks || deaderKills >= endKills))
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_forcewar_kill(killer.getName(), killerKills, deader.getName(), deaderKills));
					
				this.setKillScore(killer, score, rids, deader.damageRoles, true);
			}
		}
		
		void clearForceWarKillStreaks(int deadRoleID)
		{
			this.roleKillStreaks.remove(deadRoleID);
		}
		
		void setAutoRevive(int roleID)
		{
			this.roleAutoRevives.put(roleID, ms.getMapManager().getMapLogicTime() + this.autoReviveTime);
		}
		
		private void setKillScore(MapRole killer, final SBean.ForceWarScore score, Set<Integer> rids, Set<Integer> damageRoles, boolean countAssist)
		{
			int doubleScore = ms.getMapManager().getMapLogicTime() >= this.doubleScoreStartTime ? DOUCLE_SCORE_VALUE : 1;
			boolean white = killer.getBWType() == 1;
			SBean.ForceWarOverview k = white ? this.whiteSide.get(killer.id) : this.blackSide.get(killer.id);
			if(k != null)
			{
				k.score += score.killer * doubleScore;
				ms.getRPCManager().sendStrPacket(k.rid, new SBean.role_forcewar_kill(score.killer * doubleScore));
//				ms.getLogger().debug("@@@@@@@@@role " + killer.getID() + " , " + killer.getName() + "force war add kill score " + score.killer + " doubleScore " + doubleScore);
				
				for(SBean.ForceWarOverview e: white ? this.whiteSide.values() : this.blackSide.values())
				{
					if(e.rid == killer.getID() || !damageRoles.contains(e.rid))
						continue;
					
					MapRole role = getRole(e.rid);
					if(role == null || role.curPosition.distance(killer.curPosition) <= assistDistance)
					{
						e.score += score.assist * doubleScore;
						if(countAssist)
							e.assist++;
						ms.getRPCManager().sendStrPacket(e.rid, new SBean.role_forcewar_assist(score.assist * doubleScore));
//						ms.getLogger().debug("##########role " + role.getID() + " , " + role.getName() + "force war add assister score " + score.assist + " doubleScore " + doubleScore);
					}
				}
			}
			
			if(white)
				this.whiteCampScore += score.camp * doubleScore;
			else
				this.blackCampScore += score.camp * doubleScore;
			
//			ms.getLogger().debug(white ? " white " : " black " + " camp add score " + score.camp + " doubleScore " + doubleScore);
			
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_forcewar_campscore(whiteCampScore, blackCampScore));
		}
		
		//winSide 0:都输 1：正派胜利 2:邪派胜利 ，killedBoos 1：正派的水晶被击杀， 2：邪派的水晶被击杀
		void notifyForceWarMapEnd(int winSide, int killedBoss)
		{
			setForceWarOverviews(this.whiteSide, this.whiteMostKill);
			setForceWarOverviews(this.blackSide, this.blackMostKill);
			
			ms.getRPCManager().notifyFightForceWarMapEnd(this.mapID, this.mapInstanceID, winSide, killedBoss, whiteCampScore, blackCampScore, whiteSide, blackSide);
			this.setMapFinish();
		}
		
		private void setForceWarOverviews(Map<Integer, SBean.ForceWarOverview> scores, int mostKill)
		{
			Set<SBean.ForceWarOverview> mostKillRoles = new HashSet<>();
			for(SBean.ForceWarOverview e: scores.values())
			{
				e.kills = this.roleKills.getOrDefault(e.rid, 0);
				e.bekills = this.roleDeads.getOrDefault(e.rid, 0);
				e.killNpcs = this.roleKillNpcs.getOrDefault(e.rid, 0);
				
				if(mostKill >= GameData.getInstance().getForceWarBaseCFGS().other.mostKillCnt && e.kills == mostKill)
					mostKillRoles.add(e);
				
				e.quit = (byte)(this.getRole(e.rid) == null ? 1 : 0);
				
			}
			
			//杀人数本阵营第一 积分
			if(mostKill >= GameData.getInstance().getForceWarBaseCFGS().other.mostKillCnt && !mostKillRoles.isEmpty())
			{
				int unit = GameData.getInstance().getForceWarBaseCFGS().score.mostKill.killer / mostKillRoles.size();
				for(SBean.ForceWarOverview e: mostKillRoles)
					e.score += unit;
			}
		}

		@Override
		boolean isMapFinish()
		{
			return false;
		}

		@Override
		void processMapFinish()
		{
			
		}
	}
	
	public static class DemonHoleMap extends PublicMap
	{
		int refreshBossTime = -1;
		private Map<Integer, Integer> roleKillStreaks = new HashMap<>();
		private Map<Integer, Integer> roleDeadStreaks = new HashMap<>();
		
		DemonHoleMap(MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize);
			SBean.DemonHoleMapCFGS mapCfg = GameData.getInstance().getDemonHoleMapCFGS(this.mapID);
			if(mapCfg != null)
			{
				if(mapCfg.bossRefreshTime > 0)
				{
					this.refreshBossTime = GameTime.getSecondOfDay(GameData.getInstance().getDemonHoleBaseCFGS().startTime) + mapCfg.bossRefreshTime;
				}
			}
		}
		
		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if(super.onTimer(timeMillis, timeTick, logicTime))
			{
				tryRefresgBoss(timeTick);
				return true;
			}
			return false;
		}
		
		private void tryRefresgBoss(int timeTick)
		{
			if(this.refreshBossTime <= 0)
				return;
			
			if(this.refreshBossTime > timeTick)
				return;
			
			SBean.DemonHoleMapCFGS mapCfg = GameData.getInstance().getDemonHoleMapCFGS(this.mapID);
			if(mapCfg != null && mapCfg.bossID > 0)
			{
				SBean.DemonHoleBossCFGS bossCfg = GameData.getInstance().getDemonHoleBossCFGS(mapCfg.bossID);
				if(bossCfg != null)
				{
					Monster monster = this.createMonster(bossCfg.monsterID, new GVector3(mapCfg.bossPos), GVector3.randomRotation(), true, -1, -1);
					if(monster != null)
					{
						monster.monsterMapType = GameData.DEMONHOLEMAP_BOSS;
						monster.mapBossID = mapCfg.bossID;
						monster.setDamageRankCount(bossCfg.rankDrops.size());
					}
				}
			}
			this.refreshBossTime = -1;
		}
		
		void loadMonsters()
		{
			super.loadMonsters();
			for (SpawnArea area : this.mapSpawnAreas)
			{
				this.curAreas.add(area);
				area.addMonsters(new HashMap<>());
			}
			this.mapSpawnAreas.clear();
		}
		
		boolean isTimeOut()
		{
			return false;
		}
		
		@Override
		boolean isMapFinish()
		{
			return false;
		}

		@Override
		void processMapFinish()
		{
			// TODO Auto-generated method stub
		}

		@Override
		void checkTimeOut()
		{
			// TODO Auto-generated method stub
		}

		@Override
		void syncGSMapStart()
		{
			// TODO Auto-generated method stub
		}

		@Override
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE;
		}
		
		boolean filterRole()
		{
			return true;
		}
		
		void onMapRoleDead(MapRole deader, MapRole killer)
		{
			super.onMapRoleDead(deader, killer);
			this.roleDeadStreaks.compute(deader.getID(), (k, v) -> v == null ? 1 : v + 1);
			this.roleKillStreaks.remove(deader.getID());		//死亡本身会清除buff
			
//			if(killer != null)
//				killer.demonHoleRoleDeadDrop(deader.getLogicPosition());
//			else
//				deader.demonHoleRoleDeadDrop(deader.getLogicPosition());
		}
		
		int addRoleMapKills(MapRole killer, MapRole deader)
		{
			int count = super.addRoleMapKills(killer, deader);
			int killStreak = this.roleKillStreaks.getOrDefault(killer.getID(), 0);
			killer.setDemonHoleKillStreakBuff(killStreak, this.roleDeadStreaks.getOrDefault(killer.getID(), 0));
			this.roleKillStreaks.put(killer.getID(), killStreak + 1);
			this.roleDeadStreaks.remove(killer.getID());
			ms.getRPCManager().notifyFightSyncDemonHoleKill(this.mapID, this.mapInstanceID, killer.getID(), deader.getID());
			return count;
		}
		
		void addDeadStreakOnRevive(MapRole role)
		{
			role.addDemonHoleDeadStreakBuffOnRevive(this.roleDeadStreaks.getOrDefault(role.getID(), 0));
		}
		
		void delRole(int rid)
		{
			super.delRole(rid);
			this.roleKillStreaks.remove(rid);
			this.roleDeadStreaks.remove(rid);
		}
	}
	
	public static class FightNpcMapCopy extends MapCopy
	{
		FightNpcMapCopy(SBean.MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize, int winCondition, Map<Integer, Integer> bosses)
		{
			super(cfg, mapInstanceID, ms, gridSize, winCondition, bosses);
		}
		
		FightNpcMapCopy start()
		{
			super.start();
			SBean.FightNpcMapCFGS mapCfg = GameData.getInstance().getFightNpcMapCFGS(this.mapID);
			if(mapCfg != null)
				this.timeout = ms.getMapManager().getMapLogicTime() + mapCfg.maxTime * 1000L;
			
			return this;
		}
		
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_FIGHTNPC;
		}
		
		void syncGSMapStart()
		{
			ms.getRPCManager().syncFightNpcMapCopyStart(this.mapID, this.mapInstanceID);
		}
		
		void processMapFinish()
		{
			ms.getRPCManager().syncFightNpcMapCopyEnd(this.mapID, this.mapInstanceID, true);
			this.setMapFinish();
		}
		
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut())
				return;

			ms.getRPCManager().syncFightNpcMapCopyEnd(this.mapID, this.mapInstanceID, false);
			this.setMapFinish();
		}
		
		void onMapRoleDead(MapRole deader, MapRole killer)
		{
			super.onMapRoleDead(deader, killer);
			ms.getRPCManager().syncFightNpcMapCopyEnd(this.mapID, this.mapInstanceID, false);
			this.setMapFinish();
		}
	}
	
	public static class TowerDefenceMap extends MapCopy
	{
		final int totalSpawnCount;
		int curSpawnCount;
		int finishCount;
		long spawnTime;
		Set<Integer> spawnMonsters = new HashSet<>();
		private int defenceNpcID;
		
		TowerDefenceMap(MapClusterCFGS cfg, int mapInstanceID, MapServer ms, int gridSize)
		{
			super(cfg, mapInstanceID, ms, gridSize, 0, new HashMap<>());
			this.totalSpawnCount = cfg.spawnAreas.size();
			this.spawnTime = -1;
		}

		TowerDefenceMap start()
		{
			super.start();
			SBean.TowerDefenceMapCFGS cfg = GameData.getInstance().getTowerDefenceMapCFGS(this.mapID);
			this.timeout = ms.getMapManager().getMapLogicTime() + (cfg == null ? 0 : cfg.maxTime * 1000L);
			return this;
		}
		
		boolean ignoreTeamMemberDistance()
		{
			return true;
		}
		
		@Override
		int getMapType()
		{
			return GameData.MAP_TYPE_MAPCOPY_TOWER_DEFENCE;
		}
		
		void syncGSMapStart()
		{
			ms.getRPCManager().syncTowerDefenceMapCopyStart(this.mapID, this.mapInstanceID);
		}
		
		boolean onTimer(long timeMillis, int timeTick, long logicTime)
		{
			if (super.onTimer(timeMillis, timeTick, logicTime))
			{
				trySpawnMonster(logicTime);
				return true;
			}

			return false;
		}
		
		void checkTimeOut()
		{
			if (this.isMapAlreadyFinish || !this.isTimeOut())
				return;

			processMapFinish();
		}
		
		void syncEnterInfo(MapRole role)
		{
			Monster defenceNpc = this.getMonster(defenceNpcID);
			if(defenceNpc != null)
			{
				ms.getRPCManager().sendStrPacket(role.getID(), new SBean.towerdefence_npc_info(defenceNpc.getCurHP(), defenceNpc.getMaxHP()));
			}
		}
		
		void delMonster(int mid)
		{
			super.delMonster(mid);
			this.spawnMonsters.remove(mid);
			
			if (this.isMapAlreadyFinish)
				return;
			
			if(mid == defenceNpcID)
			{
				processMapFinish();
				return;
			}
			
			if(this.spawnMonsters.isEmpty())
			{
				finishCount++;
				if(this.curSpawnCount >= this.totalSpawnCount)
				{
					processMapFinish();
				}
				else
				{
					this.curSpawnCount++;
					tryUpdateSpawnTime();
				}
			}
		}
		
		boolean isMapFinish()
		{
			//TODO
			return false;
		}
		
		boolean checkMapCopyFinish(int winCondition, Map<Integer, Integer> bosses)
		{
			//TODO
			return false;
		}
		
		void updateSpawnArea(long timeMillis, int timeTick, long logicTime)
		{
			//TODO
		}
		
		void processMapFinish()
		{
			ms.getRPCManager().syncTowerDefenceMapCopyEnd(this.mapID, this.mapInstanceID, this.finishCount);
			this.setMapFinish();
		}
		
		void loadMonsters()
		{
			createDefenceNpc();	//先创建守护NPC
			this.curSpawnCount = 1;
			this.finishCount = 0;
			tryUpdateSpawnTime();
		}
		
		private void tryUpdateSpawnTime()
		{
			ms.getRPCManager().syncTowerDefenceMapCopySpawnCount(this.mapID, this.mapInstanceID, this.finishCount);
			SBean.MapClusterCFGS clusterCfg = GameData.getInstance().getMapClusterCFGS(this.mapID);
			if(clusterCfg == null || this.curSpawnCount <= 0 || this.curSpawnCount > clusterCfg.spawnAreas.size())
			{
				processMapFinish();
				ms.getLogger().warn("tower defence map copy[" + this.mapID + " " + this.mapInstanceID + "] curSpawnCount " + this.curSpawnCount + " error");
				return;
			}
			
			SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(clusterCfg.spawnAreas.get(this.curSpawnCount - 1));
			if(areaCfg != null)
			{
				this.spawnTime = ms.getMapManager().getMapLogicTime() + areaCfg.delaySpawnTime;
				ms.getLogger().info("tower defence map copy[" + this.mapID + " " + this.mapInstanceID + "] update spawn time " + this.spawnTime + " curSpawnCount " + this.curSpawnCount);
			}
			else
			{
				processMapFinish();
				ms.getLogger().warn("tower defence map copy[" + this.mapID + " " + this.mapInstanceID + "] spawn area " + this.curSpawnCount + " not found");
			}
		}
		
		private void trySpawnMonster(long logicTime)
		{
			if(this.spawnTime < 0 || logicTime < this.spawnTime)
				return;
			
			SBean.MapClusterCFGS clusterCfg = GameData.getInstance().getMapClusterCFGS(this.mapID);
			if(clusterCfg == null || this.curSpawnCount <= 0 || this.curSpawnCount > clusterCfg.spawnAreas.size())
			{
				processMapFinish();
				ms.getLogger().warn("tower defence map copy[" + this.mapID + " " + this.mapInstanceID + " curSpawnCount " + this.curSpawnCount + " error");
			}
			else
			{
				spawnMonster(logicTime, GameData.getInstance().getSpawnArea(clusterCfg.spawnAreas.get(this.curSpawnCount - 1)));
			}
			
			this.spawnTime = -1;
		}
		
		private void spawnMonster(long logicTime, SBean.SpawnAreaCFGS areaCfg)
		{
			for(int pointID: areaCfg.spawnPoint)
			{
				SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(pointID);
				if(pointCfg != null)
				{
					Monster monster = this.createMonster(pointCfg.monsterID, new GVector3(pointCfg.position), new GVector3(pointCfg.rotation), true, -1, -1);
					if(monster != null)
					{
						this.spawnMonsters.add(monster.getID());
						Monster defenceNpc = this.getMonster(defenceNpcID);
						if(defenceNpc != null)
						{
							monster.addEnemy(defenceNpc);
							monster.setAttacked(true);
							monster.moveTargetRealPos.reset(defenceNpc.getCurPosition());
							monster.moveTargetPos.reset(defenceNpc.getCurPosition());
							monster.startMove(logicTime);
						}
					}
				}
			}
			Set<Integer> rids = new HashSet<>(this.mapRoles.keySet());
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.towerdefence_spawn_monsters(this.curSpawnCount));
		}
		
		private void createDefenceNpc()
		{
			SBean.TowerDefenceCFGS tdCfg = GameData.getInstance().getTowerDefenceCFGS(this.mapID);
			if(tdCfg != null)
			{
				Monster defenceNpc =  this.createMonster(tdCfg.base.protectID, new GVector3(tdCfg.base.protectLocation.position),  new GVector3(tdCfg.base.protectLocation.rotation), false, -1, -1);
				if(defenceNpc != null)
				{
					defenceNpcID = defenceNpc.getID();
					if(!tdCfg.base.pops.isEmpty())
						defenceNpc.towerDefencePops = new ArrayList<>(tdCfg.base.pops);
				}
			}
		}
		
		boolean checkBaseRoleCanAttack(BaseRole attacker, BaseRole target)
		{
			if (attacker == target || attacker.isInProtectTime() || target.isInProtectTime())
				return false;
			
			
			if(attacker.getEntityType() == GameData.ENTITY_TYPE_MONSTER)
			{
				return target.checkCanBeAttack(attacker);
			}
			else
			{
				if(target.getEntityType() == GameData.ENTITY_TYPE_MONSTER && target.getBWType() == GameData.BWTYPE_SAFE)
					return false;
				
				return target.checkCanBeAttack(attacker);
			}
		}
		
		void sendAlarm(MapRole sender, int type)
		{
			Set<Integer> rids = new HashSet<>(this.mapRoles.keySet());
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.receive_towerdefence_alarm(sender.getID(), sender.getName(), type));
		}
		
		Set<Integer> getRoleIDsNearBy(int gridX, int gridZ, MapRole self, boolean big)
		{
			Set<Integer> rids = new HashSet<>();
			this.mapRoles.values().stream().filter(r -> r != self && r.active && !r.robot).forEach(r -> rids.add(r.id));
			return rids;
		}
		
		void addKillMonster(MapRole killer, Monster monster)
		{
			super.addKillMonster(killer, monster);
			if(killer != null)
				ms.getRPCManager().syncTowerDefenceMapCopyScore(this.mapID, this.mapInstanceID, killer.getID(), monster.getConfigID());
		}
	}
	
	class MrgBanquet
	{
		private int second;
		private int lastRefreshTime;
		private int curRefreshCnt;
		
		int roleID;
		private final SBean.MarriageBanquetCFGS banquetCfg;
		private final int endTime;
		WorldMap map;
		
		MrgBanquet(int roleID, SBean.MarriageBanquetCFGS banquetCfg, int endTime, WorldMap map)
		{
			this.roleID = roleID;
			this.banquetCfg = banquetCfg;
			this.endTime = endTime;
			this.map = map;
		}
		
		boolean onTimer(int timeTick)
		{
			if(timeTick > second)
			{
				tryRefreshMonsters(timeTick);
				second = timeTick;
			}
			
			return timeTick > endTime;
		}
		
		int getOpenRole()
		{
			return roleID;
		}
		
		private void tryRefreshMonsters(int timeTick)
		{
			if(banquetCfg.refreshInterval <= 0)
				return;
			
			if(timeTick < lastRefreshTime + banquetCfg.refreshInterval)
				return;
			
			tryCreateMonsters();
			lastRefreshTime = timeTick;
		}
		
		void onCreate(int now)
		{
			tryCreateMinerals();
			tryCreateMonsters();
			lastRefreshTime = now;
		}
		
		private void tryCreateMinerals()
		{
			for(int m: this.banquetCfg.minerals)
			{
				SBean.PosEntity pe = GameData.getInstance().getMarriageMineral(m);
				if(pe == null)
					continue;
				
				this.map.createMineral(pe.id, pe.pos, GameData.getInstance().getMarriageBaseCFGS().banquetLast);
			}
		}
		
		private void tryCreateMonsters()
		{
			if(banquetCfg.refreshInterval <= 0 || curRefreshCnt >= banquetCfg.refreshCnt)
				return;
			
			for(int m: this.banquetCfg.monsters)
			{
				SBean.PosEntity pe = GameData.getInstance().getMarriageMonster(m);
				if(pe == null)
					continue;
				
				this.map.createMonster(pe.id, new GVector3(pe.pos), GVector3.randomRotation(), true, banquetCfg.refreshInterval * 1000, -1);
			}
			
			curRefreshCnt++;
		}
	}

}
