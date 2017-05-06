package i3k.gmap;

import i3k.SBean;
import i3k.gmap.BaseRole.EnterInfo;
import i3k.gmap.BaseRole.LeaveInfo;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapGrid
{

	MapGrid(int gridX, int gridZ)
	{
		this.gridX = gridX;
		this.gridZ = gridZ;
	}

	public int getGridX()
	{
		return gridX;
	}

	public int getGridZ()
	{
		return gridZ;
	}

	void addSkillEntity(SkillEntity skillEntity)
	{
		skillEntitys.put(skillEntity.id, skillEntity);
		skillEntity.setCurMapGrid(this);
	}

	void delSkillEntity(int entityID)
	{
		SkillEntity skillEntity = this.skillEntitys.remove(entityID);
		if (skillEntity != null)
			skillEntity.setCurMapGrid(null);
	}

	void addRole(MapRole role)
	{
		roles.put(role.getID(), role);
		role.setCurMapGrid(this);
	}

	MapRole delRole(MapRole role)
	{
		role.setCurMapGrid(null);
		return roles.remove(role.getID());
	}

	void addMonster(Monster monster)
	{
		monsters.put(monster.getID(), monster);
		monster.setCurMapGrid(this);
	}

	Monster delMonster(Monster monster)
	{
		monster.setCurMapGrid(null);
		return monsters.remove(monster.getID());
	}

	void addNpc(Npc npc)
	{
		npcs.put(npc.getID(), npc);
		npc.setCurMapGrid(this);
	}

	Npc delNpc(Npc npc)
	{
		npc.setCurMapGrid(null);
		return npcs.remove(npc.getID());
	}

	void addWayPoint(WayPoint wayPoint)
	{
		wayPoints.put(wayPoint.getID(), wayPoint);
		wayPoint.setCurMapGrid(this);
	}

	WayPoint delWayPoint(WayPoint wayPoint)
	{
		wayPoint.setCurMapGrid(null);
		return wayPoints.remove(wayPoint.getID());
	}

	void addMineral(Mineral mineral)
	{
		minerals.put(mineral.getID(), mineral);
		mineral.setCurMapGrid(this);
	}

	Mineral delMineral(Mineral mineral)
	{
		mineral.setCurMapGrid(null);
		return minerals.remove(mineral.getID());
	}

	void addPet(Pet pet)
	{
		PetCluster petCluster = petClusters.get(pet.owner.getID());
		if (petCluster == null)
		{
			petCluster = new PetCluster(pet.owner.getID());
			petClusters.put(petCluster.id, petCluster);
		}

		petCluster.addPet(pet);
		pet.setCurMapGrid(this);
	}

	void delPet(Pet pet)
	{
		int roleID = pet.owner.getID();
		pet.setCurMapGrid(null);
		PetCluster petCluster = petClusters.get(roleID);
		if (petCluster != null)
		{
			petCluster.delPet(pet.getID());
			if (petCluster.pets.isEmpty())
				petClusters.remove(roleID);
		}
	}

	void addTrap(Trap trap)
	{
		this.traps.put(trap.getID(), trap);
		trap.setCurMapGrid(this);
	}

	void delTrap(Trap trap)
	{
		trap.setCurMapGrid(null);
		this.traps.remove(trap.getID());
	}

	void addBlur(Blur blur)
	{
		this.blurs.put(blur.getID(), blur);
		blur.setCurMapGrid(this);
	}

	void delBlur(Blur blur)
	{
		blur.setCurMapGrid(null);
		this.blurs.remove(blur.getID());
	}

	void addMapBuff(MapBuff mapBuff)
	{
		this.mapBuffs.put(mapBuff.id, mapBuff);
		mapBuff.setCurMapGrid(this);
	}

	void delMapBuff(MapBuff mapBuff)
	{
		mapBuff.setCurMapGrid(null);
		this.mapBuffs.remove(mapBuff.id);
	}
	
	void addEscortCar(EscortCar car)
	{
		this.escortCars.put(car.getID(), car);
		car.setCurMapGrid(this);
	}
	
	void delEscortCar(EscortCar car)
	{
		car.setCurMapGrid(null);
		this.escortCars.remove(car.getID());
	}
	
	void addWeddingCar(WeddingCar car)
	{
		this.weddingCars.put(car.getID(), car);
		car.setCurMapGrid(this);
	}
	
	void delWeddingCar(WeddingCar car)
	{
		car.setCurMapGrid(null);
		this.weddingCars.remove(car.getID());
	}
	
	Map<Integer, MapRole> getRoles()
	{
		return roles;
	}

	Map<Integer, Monster> getMonsters()
	{
		return monsters;
	}

	Map<Integer, Npc> getNpcs()
	{
		return npcs;
	}

	Map<Integer, Mineral> getMinerals()
	{
		return this.minerals;
	}

	Map<Integer, PetCluster> getPetClusters()
	{
		return petClusters;
	}

	Map<Integer, Trap> getTraps()
	{
		return this.traps;
	}

	Map<Integer, WayPoint> getWayPoints()
	{
		return this.wayPoints;
	}

	Map<Integer, Blur> getBlurs()
	{
		return this.blurs;
	}

	Map<Integer, SkillEntity> getSkillEntitys()
	{
		return this.skillEntitys;
	}
	
	Collection<EscortCar> getEscortCars()
	{
		return this.escortCars.values();
	}
	
	void clearAllRoles()
	{
		this.roles.clear();
		this.petClusters.clear();
		this.blurs.clear();
		this.skillEntitys.clear();
		this.escortCars.clear();
	}
	
	void setEnterInfo(EnterInfo enterInfo, MapRole self, boolean big)
	{
		if (big)
		{
			this.roles.values().stream().filter(r -> self != r && r.active).forEach(r -> enterInfo.roles.put(r.getID(), r.getEnterDetail()));
			this.petClusters.values().stream().filter(c -> c.id != self.id).forEach(c -> {
				c.pets.values().stream().filter(p -> p.active).forEach(p -> enterInfo.pets.add(p.getEnterPet()));
			});
			this.skillEntitys.values().stream().filter(s -> !s.isDead()).forEach(s -> enterInfo.skillEntitys.add(s.getEnterSkillEntity()));
			this.blurs.values().stream().filter(b -> !b.isDead()).forEach(b -> enterInfo.blurs.add(b.getEnterDetail()));
			this.escortCars.values().stream().filter(c -> c.active && self.id != c.id).forEach(c -> enterInfo.cars.add(c.getEnterEscortCar()));
			this.weddingCars.values().stream().filter(wc -> wc.man.id != self.id && wc.woman.id != self.id).forEach(wc -> enterInfo.wcars.add(wc.getEnterWeddingCar()));
		}
		else
		{
			this.monsters.values().stream().filter(m -> !m.isDead() && m.canBeSeen(self)).forEach(m -> enterInfo.monsters.add(m.getEnterMonster()));
			this.npcs.values().forEach(n -> enterInfo.npcs.add(n.getEnterBase()));
			this.minerals.values().stream().filter(m -> m.mineralCount != 0).forEach(m -> enterInfo.minerals.add(m.getEnterMineral()));
			this.traps.values().forEach(t -> enterInfo.traps.add(t.getEnterBase()));
//			this.wayPoints.values().forEach(w -> enterInfo.wayPoints.add(w.getEnterBase()));
			this.mapBuffs.values().stream().filter(m -> !m.isDead()).forEach(m -> enterInfo.mapBuffs.add(m.getEnterBase()));
		}
	}
	
	void setLeaveInfo(LeaveInfo leaveInfo, MapRole self, boolean big)
	{
		if (big)
		{
			this.roles.values().stream().filter(r -> self != r && r.active).forEach(r -> leaveInfo.roles.add(r.id));
			this.petClusters.values().stream().filter(c -> c.id != self.id).forEach(c -> {
				c.pets.values().stream().filter(p -> p.active && p.owner.isNearByRoles(self.id)).forEach(p -> leaveInfo.pets.add(new SBean.PetBase(p.owner.id, p.id)));
			});
			this.skillEntitys.values().stream().filter(s -> !s.isDead() && s.owner.isNearByRoles(self.id)).forEach(s -> leaveInfo.skillEntitys.add(s.id));
			this.blurs.values().stream().filter(b -> !b.isDead() && b.owner.isNearByRoles(self.id)).forEach(b -> leaveInfo.blurs.add(b.id));
			this.escortCars.values().stream().filter(c -> c.active && self.id != c.id).forEach(c -> leaveInfo.cars.add(c.id));
			this.weddingCars.values().stream().filter(wc -> wc.man.id != self.id && wc.woman.id != self.id).forEach(wc -> leaveInfo.wcars.add(wc.id));
		}
		else
		{
			this.monsters.values().stream().filter(m -> !m.isDead() && m.canBeSeen(self)).forEach(m -> leaveInfo.monsters.add(m.id));
			this.npcs.values().forEach(n -> leaveInfo.npcs.add(n.id));
			this.minerals.values().stream().filter(m -> m.mineralCount != 0).forEach(m -> leaveInfo.minerals.add(m.id));
			this.traps.values().forEach(t -> leaveInfo.traps.add(t.id));
//			this.wayPoints.values().forEach(w -> leaveInfo.wayPoints.add(w.id));
			this.mapBuffs.values().stream().filter(m -> !m.isDead()).forEach(m -> leaveInfo.mapBuffs.add(m.id));
		}
	}
	
	///////
	private final int gridX;
	private final int gridZ;

	private Map<Integer, MapRole> roles = new TreeMap<>();
	private Map<Integer, Monster> monsters = new TreeMap<>();
	private Map<Integer, Npc> npcs = new TreeMap<>();
	private Map<Integer, WayPoint> wayPoints = new TreeMap<>();
	private Map<Integer, Mineral> minerals = new TreeMap<>();
	private Map<Integer, PetCluster> petClusters = new TreeMap<>(); //<roleID, PetCluster>
	private Map<Integer, Trap> traps = new TreeMap<>();
	private Map<Integer, Blur> blurs = new TreeMap<>();
	private Map<Integer, MapBuff> mapBuffs = new TreeMap<>();
	private Map<Integer, SkillEntity> skillEntitys = new TreeMap<>();
	private Map<Integer, EscortCar> escortCars = new TreeMap<>();	//<carID, EscortCar>  carID == roleID
	private Map<Integer, WeddingCar> weddingCars = new TreeMap<>();
}
