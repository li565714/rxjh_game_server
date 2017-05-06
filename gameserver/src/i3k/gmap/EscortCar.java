package i3k.gmap;

import i3k.SBean;
import i3k.gmap.BaseMap.PublicMap;
import i3k.gs.GameData;
import i3k.util.GVector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class EscortCar extends BaseRole
{
	EscortCar(MapServer ms, boolean serverControl)
	{
		super(ms, serverControl);
	}
	
	EscortCar fromDBWitoutLock(SBean.DBEscortCar carInfo, int ownerID, String ownerName, SBean.Team team, int sectID)
	{
		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_ESCORTCAR;
		
		this.id = ownerID;
		this.ownerName = ownerName;
		this.team = team;
		this.sectID = sectID;
		
		this.damageRoles = carInfo.damageRoles;
		this.configID = carInfo.configID;
		this.brokenTimes = carInfo.brokenTimes;
		
		this.active = false;
		this.fromDB(carInfo.location);
		this.setPropBase(new PropFightRole());
		this.getPropBase().addFightPropFixValue(EPROPID_MAXHP, carInfo.maxHP);
		this.getPropBase().addFightPropFixValue(EPROPID_SPEED, GameData.getInstance().getSectDeliverCfgs().moveSpeed);
		this.speed = GameData.getInstance().getSectDeliverCfgs().moveSpeed;
		this.curHP = carInfo.curHP;
		
		SBean.SectDeliverVehicle sdv = GameData.getInstance().getSectDeliverVehicleCfgs(this.configID);
		if(sdv != null)
		{
			this.radius = sdv.radius;
			this.getPropBase().addFightPropFixValue(EPROPID_DEFN, sdv.defN);
			this.getPropBase().addFightPropFixValue(EPROPID_CTR, sdv.ctr);
			this.getPropBase().addFightPropFixValue(EPROPID_TOU, sdv.tou);
			this.getPropBase().addFightPropFixValue(EPROPID_DEFC, sdv.defC);
			this.getPropBase().addFightPropFixValue(EPROPID_DEFW, sdv.defW);
			this.updateMaxHp();
			
			this.damageLimit = (int) (sdv.damageLimit / 10_000.f * this.maxHp);
		}	
		return this;
	}
	
	void active()
	{
		this.active = true;
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		this.onSelfEnterNearBy(rids);
	}
	
	void onUpdateTeamCarCnt(int teamCarCnt)
	{
		if(this.teamCarCnt == teamCarCnt || this.isDead())
			return;
		
		this.teamCarCnt = teamCarCnt;
		int dmgByBuffID = GameData.getInstance().getSectDeliverCfgs().teamBuffs.get(0);
		int maxHPBuffID = GameData.getInstance().getSectDeliverCfgs().teamBuffs.get(1);
		Buff dmgByBuff = this.buffs.get(dmgByBuffID);
		Buff maxHPBuff = this.buffs.get(maxHPBuffID);
		if(this.teamCarCnt <= 1)
		{
			if(dmgByBuff != null)
				this.removeBuff(dmgByBuff);

			if(maxHPBuff != null)
				this.removeBuff(maxHPBuff);
		}
		else
		{
			this.updateTeamBuff(dmgByBuff, dmgByBuffID, GameData.getInstance().getSectDeliverCfgs().dmgBy);
			this.updateTeamBuff(maxHPBuff, maxHPBuffID, GameData.getInstance().getSectDeliverCfgs().maxHp);
		}
	}
	
	void updateTeamBuff(Buff buff, int buffID, int factor)
	{
		if(buff == null)
		{
			SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(buffID);
			if(buffCfg != null)
			{
				buff = this.createNewBuff(buffCfg, 0, 0, this, null);
				buff.endTime = -1;
				this.addBuff(buff, this, null);
			}
		}
		
		if(buff == null)
			return;
		
		buff.value = (this.teamCarCnt - 1) * factor;
	}
	
	void setTeam(SBean.Team team)
	{
		boolean change = this.team == null ? true : this.team.id != team.id;
		this.team = team;
		if(change)
			this.notifyUpdateSocial();
	}
	
//	void updateSect(int sectID)
//	{
//		boolean change = this.sectID != sectID;
//		this.sectID = sectID;
//		if(change)
//			this.notifyUpdateSocial();
//	}
	
	private void notifyUpdateSocial()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_updatesocial(this.id, this.team == null ? 0 : this.team.id, this.sectID));
	}
//------------------------------------------------------------------------------------------------------------
	
	void updateOwner(MapRole role)
	{
		this.owner = role;
//		if(this.owner == null)
//			this.beSeenRoles.remove(this.id);
//		else
//			this.beSeenRoles.add(this.id);
	}
	
	SBean.EnterEscortCar getEnterEscortCar()
	{
		List<Integer> curBuffs = this.buffs.isEmpty() ? GameData.emptyList() : new ArrayList<Integer>();
		this.buffs.values().stream().filter(b -> b.hasShowID && b.spiritEffectID <= 0).forEach(b -> curBuffs.add(b.id));
		return new SBean.EnterEscortCar(this.getEnterDetail(), curBuffs, this.ownerName, this.getBreakState(), this.team == null ? 0 : this.team.id, this.sectID);
	}
	
	int getBreakState()
	{
		return this.brokenTimes >= GameData.getInstance().getSectDeliverCfgs().beRobbedTimes ? 1 : 0;
	}
	
	void onTimer(int timeTick, long logicTime)
	{
		if (this.curMap == null || (this.curMapGrid == null && !this.isInPrivateMap()))
			return;

		this.onSecondTask(timeTick);
		this.onMilliSecondTask(logicTime);
	}
	
	private void onSecondTask(int timeTick)
	{
		if (timeTick > this.second)
		{
			this.syncPosition();
			this.second = timeTick;
			
//			if(timeTick % 5 == 0)
//				System.err.println("role " + this.ownerName + "'s car " + " curMapGrid " + this.curMapGrid.getGridX() + " , " + this.curMapGrid.getGridZ() + " curPosition " + this.curPosition);
		}
	}
	
	void onMilliSecondTask(long logicTime)
	{
//		this.checkSpecialState(logicTime);
		this.onUpdateCurBuff(logicTime);
		this.onCheckStopMove(logicTime);
		this.lastLogicTime = logicTime;
	}
///////////////////////////////////////////////////////////////
	void updateBeSeenRoles(Set<Integer> rids, boolean enter)
	{
		if(enter)
			this.beSeenRoles.addAll(rids);
		else
			this.beSeenRoles.removeAll(rids);
	}
	
	void updateBeSeenRoles(int rid, boolean enter)
	{
		if(enter)
			this.beSeenRoles.add(rid);
		else
			this.beSeenRoles.remove(rid);
	}
	
	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterEscortCar> cars = new ArrayList<>();
			cars.add(this.getEnterEscortCar());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_escortcars(cars));
			this.updateBeSeenRoles(rids, true);
		}
	}
	
	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> cars = new ArrayList<>();
			cars.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_escortcars(cars));
//			ms.getLogger().debug("escort car leave notify rids " + rids);
			this.updateBeSeenRoles(rids, false);
		}
	}
	
	void notifySelfGetDamage(Set<Integer> rids, BaseRole attacker, int ownerID, SBean.DamageResult res, int skillID, int curDamageEventID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_ondamage(this.getID(), attacker.getID(), attacker.getEntityType(), ownerID, skillID, curDamageEventID, this.getCurHP(), res.dodge, res.deflect, res.crit, res.suckBlood, res.behead, res.remit, timeTick));
		
//		ms.getLogger().debug("car on get damage curHP " + this.curHP + " rids " + rids);
	}
	
	void notifyBuffDamage(Set<Integer> rids, int attackerType, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_buffdamage(this.getID(), this.getCurHP(), attackerType, timeTick));
	}
	
	void notifyAddBuff(Set<Integer> rids, int buffID, int realmLvl, int remainTime, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_addbuff(this.getID(), buffID, timeTick));
	}
	
	void notifyRemoveBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_removebuff(this.getID(), buffID, timeTick));
	}
	
	void notifyUpdateHp(Set<Integer> rids)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_updatehp(this.getID(), this.getCurHP()));
	}
	
	void notifyUpdateMaxHp(Set<Integer> rids)
	{	
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_updatemaxhp(this.getID(), this.getMaxHP()));
	}
///////////////////////////////////////////////////////////////
	private void addMoveCheckSpeed(int checkSpeed)
	{
		this.checkSpeeds.add(checkSpeed);
		if (this.checkSpeeds.size() > 10)
			this.checkSpeeds.remove(0);
	}
	
	private void onCheckStopMove(long logicTime)
	{
		if(this.serverStopTime == 0)
			return;
			
		if(logicTime - this.serverStopTime >= 1000)
		{
			this.serverStopTime = 0;
			Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_escortcar(this.getID(), this.getLogicPosition(), ms.getMapManager().getTimeTickDeep()));
		}
	}
	
	boolean onMoveMent(int speed, SBean.Vector3F rotation, GVector3 pos, GVector3 targetPos, SBean.TimeTick timeTick)
	{
		if(this.owner == null || this.owner.curPosition.distance(this.curPosition) > (GameData.getInstance().getSectDeliverCfgs().stopDistance + 1000))	//1000 distance error
			return false;
		
		{
			if (this.preMovePosition != null && this.preMoveTimeTick != null)
				this.addMoveCheckSpeed((int) (this.preMovePosition.distance(pos) / (getTimeInterval(this.preMoveTimeTick, timeTick) / 1000.f)));

			this.preMovePosition = pos;
			this.preMoveTimeTick = timeTick;
		}

		if (speed > this.getFightProp(EPROPID_SPEED) * 2)
			return false;

		if (!super.onMoveMent(speed, rotation, pos, targetPos, timeTick))
			return false;

		GVector3 realPos = pos.sum(this.curRotation.scale(this.owner.getPing() * speed / 1000.0f));
		this.fixNewPosition(this.curRotation, realPos, this.moveTargetPos);
		this.setNewPosition(realPos);
		
		this.serverStopTime = 0;
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_escortcar(this.getID(), this.getLogicPosition(), speed, rotation, targetPos.toVector3(), timeTick));
		
		return true;
	}
	
	void changePosition(int curGridX, int curGridZ, int newGridX, int newGridZ, int destory)
	{
		Set<Integer> enterRids = this.curMap.getSelfEnterRoleIDsNearby(curGridX, curGridZ, newGridX, newGridZ, null, true);
		enterRids.remove(this.id);
		this.onSelfEnterNearBy(enterRids);
		Set<Integer> leaveRids = this.curMap.getSelfLeaveRoleIDsNearby(curGridX, curGridZ, newGridX, newGridZ, null, true);
		leaveRids.remove(this.id);
		this.onSelfLeaveNearBy(leaveRids, destory);
	}
	
	boolean changeMapGrid(MapGrid grid)
	{
		if (grid != this.curMapGrid)
		{
			this.curMapGrid.delEscortCar(this);
			grid.addEscortCar(this);
			return true;
		}
		return false;
	}
	
	void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.setNewPosition(position);
		this.onStopMoveImpl(position, timeTick, broadcast);
	}
	
	void onStopMoveImpl(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		if(broadcast)
		{
			this.serverStopTime = 0;
			Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_escortcar(this.getID(), this.getLogicPosition(), timeTick));
		}
		this.removeState(Behavior.EBMOVE);
		this.moveSpeed = 0;
		this.moveTarget = null;
		this.preMovePosition = null;
	}
	
///////////////////////////////////////////////////////////////
	void setDamageLimit(SBean.DamageResult res)
	{
		if(res.damage > this.damageLimit)
			res.damage = this.damageLimit;
	}
	
	boolean checkCanBeAttack(BaseRole attacker)
	{
		if(attacker.owner == null)
			return false;
		
		if(attacker.owner.carRobber == 0 || attacker.owner.id == this.id)
			return false;
		
		if(this.team == null)
			return true;
		
		if(this.team.id != 0 && this.team.id == attacker.owner.getTeamID())
			return false;
		
		if(this.sectID != 0 && this.sectID == attacker.owner.getSectID())
			return false;
		
		return true;
	}
	
	boolean isDead()
	{
		return this.curHP <= 0 || this.brokenTimes >= GameData.getInstance().getSectDeliverCfgs().beRobbedTimes;
	}
	
	void onBroken()
	{
		this.robSuccess();
		if(this.brokenTimes + 1 == GameData.getInstance().getSectDeliverCfgs().beRobbedTimes)
		{
			int dmgByBuffID = GameData.getInstance().getSectDeliverCfgs().teamBuffs.get(0);
			int maxHPBuffID = GameData.getInstance().getSectDeliverCfgs().teamBuffs.get(1);
			Buff dmgByBuff = this.buffs.get(dmgByBuffID);
			Buff maxHPBuff = this.buffs.get(maxHPBuffID);
			if(dmgByBuff != null)
				this.removeBuff(dmgByBuff);
			
			if(maxHPBuff != null)
				this.removeBuff(maxHPBuff);
			
			this.setCurHP(this.maxHp);
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_updatestate(this.id, 1));
			this.notifyUpdateHp(rids);
		}
		else
			this.resetCar();
		
		this.brokenTimes++;
	}
	
	void onGetDamageHandler(BaseRole attacker, int damage)
	{
		if(this.getBreakState() == 1)
			return;
		
		if(attacker.owner != null)
		{
			updateDamageRole(attacker.owner.id, damage);
			if(this.curHP <= this.getMaxHP() * (GameData.getInstance().getSectDeliverCfgs().robSuccessHP / 10_000.f))
				this.onBroken();
		}
	}
	
	void onGetBuffDamageHandler(Buff buff, int value)
	{
		if(value >= 0 || this.getBreakState() == 1)
			return;
		
		if(buff.attackOwnerID > 0)
			updateDamageRole(buff.attackOwnerID, Math.abs(value));
		
		if(this.curHP <= this.getMaxHP() * (GameData.getInstance().getSectDeliverCfgs().robSuccessHP / 10_000.f))
			this.onBroken();
	}
	
	private void updateDamageRole(int roleID, int damage)
	{
		MapRole attacker = this.curMap.getRole(roleID);
		if(attacker == null)
			return;
		
		attacker.exposeCarRobber();
		this.damageRoles.compute(roleID, (k, v) -> v == null ? damage : v + damage);
		ms.getRPCManager().updateCarDamage(this.id, this.getMapID(), this.getMapInstanceID(), roleID, damage);
		
		if(this.owner != null && this.owner.active && PublicMap.isMapGridNearBy(this.owner.curMapGrid, attacker.owner.curMapGrid))
		{
			this.owner.onDamageEnterRole(attacker);
			attacker.onDamageEnterRole(this.owner);
		}
	}
	
	public int setCurHP(int curHP)
	{
		if (this.isDead())
			return 0;

		if (curHP <= 0)
			curHP = 0;
		else if (curHP > this.getMaxHP())
			curHP = this.getMaxHP();

		if (this.curHP != curHP)
		{
			this.curHP = curHP;
			ms.getRPCManager().syncCarCurHP(this.id, this.getMapID(), this.getMapInstanceID(), this.curHP);
		}
		
		return this.curHP;
	}
	
	//同步位置信息
	private void syncPosition()
	{
		if (!this.getCurPosition().equals(this.syncPostion))
		{
			syncPostion.reset(this.getCurPosition());
			ms.getRPCManager().syncCarLocation(this.id, this.getMapID(), this.getMapInstanceID(), new SBean.Location(this.getLogicPosition(), this.getCurRotation().toVector3F()));		//carID == roleID
		}
	}
	
	//劫镖成功
	private void robSuccess()
	{
		if(this.getBreakState() == 1)
			return;
		
		Set<MapRole> roles = getNearByDamageRoles();
		List<SBean.RoleInfo> robs = new ArrayList<>();
		final int num = GameData.getInstance().getSectDeliverCfgs().robValidNum;
		if(roles.size() <= num)
		{
			for(MapRole r: roles)
			{
				ms.getRPCManager().syncRoleRobSuccess(r.id, this.configID);
				robs.add(new SBean.RoleInfo(r.id, r.roleName));
				r.updateCarBehavior((byte)0, (byte)0);
			}
			this.notifyRobbed(robs);
			return;
		}	
		
		TreeMap<Long, MapRole> dmgs = new TreeMap<>();
		for(MapRole r: roles)
		{
			Integer damage = this.damageRoles.get(r.id);
			if(damage == null)
				continue;
			
			dmgs.put(GameData.getLongTypeValue(damage, r.id), r);
		}
		
		int index = 0;
		for(MapRole r: dmgs.values())
		{
			ms.getRPCManager().syncRoleRobSuccess(r.id, this.configID);
			robs.add(new SBean.RoleInfo(r.id, r.roleName));
			r.updateCarBehavior((byte)0, (byte)0);
			index++;
			if(index == num)
				break;
		}
		this.notifyRobbed(robs);
	}
	
	private void notifyRobbed(List<SBean.RoleInfo> robs)
	{
		if(robs.isEmpty())
			return;
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_escortcar_robbed(this.configID, new SBean.RoleInfo(this.id, this.ownerName), robs));
	}
	
	private Set<MapRole> getNearByDamageRoles()
	{
		Set<MapRole> roles = new HashSet<>();
		for(int rid: this.damageRoles.keySet())
		{
			MapRole role = this.curMap.getRole(rid);
			if(role == null || !role.active)
				continue;

			if(this.curPosition.distance(role.curPosition) <= GameData.getInstance().getSectDeliverCfgs().robValidDistance)
				roles.add(role);
		}
		
		return roles;
	}
	
	private void resetCar()
	{
		this.setCurHP(this.maxHp);
		this.addBuffByID(GameData.getInstance().getSectDeliverCfgs().invincibleBuff, this, null);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.notifyUpdateHp(rids);
	}
	
	Set<Integer> getRoleIDsNearBy(MapRole role)
	{
		return super.getRoleIDsNearBy(role);
//		Set<Integer> rids = new HashSet<>(this.beSeenRoles);
//		if(role == null)
//			rids.add(this.id);
//		return rids;
	}
	
	private Map<Integer, Integer> damageRoles = new HashMap<>();
	private int brokenTimes;
	private int teamCarCnt;
	private String ownerName = "";
	
	private int second;
	private GVector3 syncPostion = new GVector3();
	private GVector3 preMovePosition;
	private SBean.TimeTick preMoveTimeTick;
	private List<Integer> checkSpeeds = new ArrayList<>();
	
	private Set<Integer> beSeenRoles = new HashSet<>();
	private SBean.Team team;
	private int damageLimit;
	private int sectID;
}