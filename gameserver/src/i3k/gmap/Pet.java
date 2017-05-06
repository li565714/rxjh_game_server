package i3k.gmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import i3k.SBean;
import i3k.gmap.Skill;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;

public class Pet extends BaseRole
{

	Pet(MapServer ms, boolean serverControl)
	{
		super(ms, serverControl);
	}

	Pet createNew(SBean.FightPet fPet, MapRole owner, SBean.Location location, SBean.PetHost petHost, int seq)
	{
		this.owner = owner;
		this.curMapID = owner.getMapID();
		this.curPosition = new GVector3(location.position);
		this.curRotation = new GVector3(location.rotation);
		this.active = this.owner.active;
		this.fightpower = fPet.fightPower;
		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_PET;

		this.id = fPet.id;
		this.configID = Math.abs(fPet.id);
		this.level = fPet.level;
		this.propPet = new PropPet(false).createNew(fPet, owner.propRole, petHost);
		this.setPropBase(this.propPet);
		this.seq = seq;
		
		SBean.PetCFGS petCfg = GameData.getInstance().getPetCFG(this.getConfigID());
		if (petCfg != null)
		{
			for (int sid : petCfg.skills)
				this.curSkills.add(sid);

			this.radius = petCfg.radius;
			this.attackList = petCfg.attackList;
			this.hurtAddSP = petCfg.hurtAddSP;
			this.utlSkill = petCfg.skills.get(petCfg.skills.size() - 1);
			this.stupidInfo = new StupidTime(petCfg);
			this.maxBehaviorRadius = petCfg.maxBehaviorRadius;
		}

		this.autoReviveTime = GameData.getInstance().getCommonCFG().pet.autoRevive;
		this.resetFightSkills(fPet.skill);
		this.updateBuffProps();
		this.updateMaxHp();
		this.curHP = this.getMaxHP();
		this.setCurSkillID();
		if(this.owner.robot)
		{
			this.maxTraceRange = this.owner.maxTraceRange;
			this.birthPosition = this.owner.birthPosition;
		}
		
		this.updatePetStupidTime();
		this.updateSpiritAis();
		return this;
	}
	
	int getFightProp(int propID)
	{
		switch (propID)
		{
		case BaseRole.EPROPID_LVL:
			return this.level;
		case BaseRole.EPROPID_HP:
			return this.curHP;
		case BaseRole.EPROPID_SP:
			return this.curSP;
		case BaseRole.EPROPID_SPEED:
			int speed = this.getPropBase().getFinalProps(propID, this.owner.getPropRedNameDamageDecreaseValue(propID));
			return speed < 0 ? 0 : speed;
		default:
			return this.getPropBase().getFinalProps(propID, this.owner.getPropRedNameDamageDecreaseValue(propID));
		}
	}
	
	public boolean updateMaxHp()
	{
		boolean update = super.updateMaxHp();
		if (update && this.curMap != null)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
			this.notifyUpdateMaxHp(rids);
		}
		return update;
	}

	private void onSecondTask(int timeTick)
	{
		if (timeTick > this.second)
		{
			this.second = timeTick;
			this.onCheckAutoRevive(timeTick);
			if(this.isInPrivateMap())
				return;
			
			this.checkMovePosition(timeTick);
			this.onCheckPosToFar();
		}
	}

	private void onCheckPosToFar()
	{
		if(this.owner.robot)
			return;
		
		float distance = this.curPosition.distance(this.owner.curPosition);
		if (distance > MapManager.PET_RESETPOS_RADIUS && !this.isDead())
			this.resetLocation();
	}

	private void checkMovePosition(int timeTick)
	{
		if (this.owner.robot || this.moveSpeed == 0)
			return;

		float total = 0;
		for (int speed : this.checkSpeeds)
			total += speed;

		if (total / 10.f > this.getFightProp(EPROPID_SPEED) * 2)
			this.adjustPos();
	}

	void onMilliSecondTask(long logicTime)
	{
		if(this.isInPrivateMap())
		{
			this.checkSpecialState(logicTime);
			return;
		}
		
		super.onMilliSecondTask(logicTime);
		this.onCheckStopMove(logicTime);
		this.lastLogicTime = logicTime;
	}

	void onTimer(int timeTick, long logicTime)
	{
		if (this.curMap == null || (this.curMapGrid == null && !this.isInPrivateMap()) || !this.active)
		{
			return;
		}
		this.onSecondTask(timeTick);
		this.onMilliSecondTask(logicTime);
	}
	
	void onCheckSkillDuration(long logicTime)
	{
		if(this.owner.robot)
		{
			super.onCheckSkillDuration(logicTime);
		}
		else
		{
			if (this.attack == null)
				return;

			if (this.attack.skillEndTime - timerInterval > logicTime)
				return;
			
			if(this.checkState(Behavior.EBATTACK))
			{
				this.curStates.remove(Behavior.EBATTACK);
				this.notifyFinishAttack(this.attack);
			}
			
			if (!this.checkState(Behavior.EBATTACK) && !this.checkState(Behavior.EBDISATTACK))
			{
				if (this.attack.rushInfo != null && !this.attack.rushInfo.rushStart && this.attack.valid)
					this.adjustPos();

				this.nextSkill();
			}
					
			if (this.attack.skillEndTime > logicTime)
				return;
//			ms.getLogger().debug("skill " + this.attack.id + " remove state attack " + this.curUseSkills.size() + " at " + logicTime + " , " + GameTime.getTimeMillis() + 
//					" at tick[" + ms.getMapManager().getTimeTick().tickLine + " , " + ms.getMapManager().getTimeTick().outTick + "]");
			this.attack = null;
		}
	}
	
	void checkSkill(long logicTime)
	{
		if (this.owner.robot)
		{
			super.checkSkill(logicTime);
		}
		else
		{
			this.refreshCurUseSkills();
			Set<Skill> inUseSkills = new HashSet<>(this.curUseSkills);
			if (this.attack != null)
				inUseSkills.add(this.attack);

			for (Skill skill : inUseSkills)
			{
				long skillStartTime = skill.skillEndTime - skill.skillDuration;
				int time = skill.baseCommonCfg.spell.time + skill.baseCommonCfg.charge.time;
				if (logicTime > time + skillStartTime && skill.valid)
				{
					if (skill.flyInfo != null)
						this.checkFlySkill(logicTime, skill);

					if (skill.rushInfo != null)
						this.updateRushPos(logicTime, skill);

					if(skill.summonInfo != null)
						this.checkSummonSkill(logicTime, skill);
					
					this.checkChiledSkill(logicTime, skill);
				}
			}

			if (this.curUseSkills.size() > 0)
			{
				this.onAutoProcessDamage(logicTime);
			}
		}
	}

	void onAutoFight(long logicTime)
	{
		if (!this.owner.robot)
		{
			this.updateMovePosition(logicTime);
			return;
		}

		if (this.isDead() || this.isInProtectTime())
			return;

		if (this.checkState(Behavior.EBPREPAREFIGHT))
			return;

		super.onAutoFight(logicTime);
	}

	void attackNearest(BaseRole role, long logicTime)
	{
		if (this.stupidTime > logicTime)
			return;
		
		super.attackNearest(role, logicTime);
	}

	void startMove(long logicTime)
	{
		if(this.owner.robot)
			this.onMoveMent((int) this.getFightProp(EPROPID_SPEED), this.curRotation.toVector3F(), this.getCurPosition(), this.moveTargetPos, ms.getMapManager().getTimeTick());
	}

	BaseRole getRoleInCheckRange()
	{
		BaseRole entity = null;
		if (this.curMap.isFightMap())
			entity = this.getRandomPlayerEntity(this.getRoleNearBy(this.owner), this.getPetNearBy());
		else
		{
			entity = this.getPlayerEntity();
//			if (entity == null)
//				entity = this.getNearestTrap(this.getTrapNearBy());
		}

		return entity;
	}

	private BaseRole getRandomPlayerEntity(List<MapRole> roles, List<Pet> pets)
	{
		List<BaseRole> allEntity = new ArrayList<>();
		
		for (Pet p : pets)
		{
			if (!p.isDead() && !p.isInProtectTime())
			{
				if (this.curMap.checkBaseRoleCanAttack(this, p))
				{
					if(this.seq > 0 && this.seq == p.seq)
						return p;
					
					allEntity.add(p);
				}
			}
		}
		
		for (MapRole r : roles)
		{
			if (!r.isDead() && !r.isInProtectTime())
			{
				if (this.curMap.checkBaseRoleCanAttack(this, r))
					allEntity.add(r);
			}
		}

		int count = allEntity.size();
		int rnd = count > 0 ? GameRandom.getRandom().nextInt(count) : 0;
		return count > 0 ? allEntity.get(rnd) : null;
	}

	private void onCheckAutoRevive(int timeTick)
	{
		if (this.curHP > 0)
			return;

		if (this.owner.isDead())
			return;

		if (!this.curMap.canPetRevive())
			return;

		if (timeTick - this.deadTime > this.autoReviveTime)
			this.onPetRevive(true);
	}
	
	void onUpdatePetSkill(List<Integer> skillLvls)
	{
		this.resetSkill(skillLvls);
	}
	
	private void resetAttack()
	{
		SBean.PetCFGS petcfg = GameData.getInstance().getPetCFG(this.getConfigID());
		if (petcfg != null)
		{
			//普通攻击
			for (Integer sid : petcfg.attacks)
			{
				if (this.fightSkills.containsKey(sid))
					continue;

				SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(sid);
				if (sCfg != null)
				{
					FightSkill skill = new FightSkill(sid, sCfg.baseData.common.maxLvl, 0, 0, Skill.eSG_Attack, 1, GameData.SKILL_USE_TYPE_ATTACK).copySkillData();
					if (skill != null)
						this.fightSkills.put(sid, skill);
				}
			}
		}
	}
	
	private void resetSkill(List<Integer> skillLvls)
	{
		SBean.PetCFGS petcfg = GameData.getInstance().getPetCFG(this.getConfigID());
		if (petcfg != null)
		{
			int count = skillLvls.size() < petcfg.skills.size() ? skillLvls.size() : petcfg.skills.size();
			for (int i = 0; i < count; i++)
			{
				int sid = petcfg.skills.get(i);
				int lvl = skillLvls.get(i);
				FightSkill skill = this.fightSkills.get(sid);
				if(skill != null && skill.level == lvl)
					continue;
				
				SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(sid);
				if (sCfg != null && lvl > 0)
				{
					skill = new FightSkill(sid, lvl, skill == null ? 0 : skill.coolDownTime, 0, sid == this.utlSkill ? Skill.eSG_Attack : Skill.eSG_Skill, 1, GameData.SKILL_USE_TYPE_CUR).copySkillData();
					if (skill != null)
						this.fightSkills.put(sid, skill);
				}
			}
		}
	}
	
	private void resetFightSkills(List<Integer> skillLvls)
	{
		this.fightSkills.clear();
		this.resetAttack();
		this.resetSkill(skillLvls);
	}

	void onUpdateHost(SBean.PetHost petHost)
	{
		this.propPet.onUpdateHost(petHost);
		this.updateMaxHp();
	}
	
	void onUpdateInfo(SBean.FightPet fightPet)
	{
		if(this.onUpdateLvl(fightPet.level))
			return;
		
		if(this.onUpdateCoPracticeLvl(fightPet.coPracticeLvl))
			return;
		
		if(this.onUpdateStarLvl(fightPet.star))
			return;
		
		this.onUpdateBreakSkills(fightPet.breakSkills);
	}
	
	boolean onUpdateLvl(int lvl)
	{
		if(this.propPet.onUpdateLvl(lvl))
		{
			this.updateMaxHp();
			this.setCurHP(this.getMaxHP());
			ms.getRPCManager().sendStrPacket(this.owner.id, new SBean.nearby_pet_updatehp(this.owner.getID(), this.getID(), this.getCurHP()));
			Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
			this.notifyUpdateHp(rids);
			return true;
		}
		
		return false;
	}
	
	boolean onUpdateCoPracticeLvl(int coPracticeLvl)
	{
		if(this.propPet.onUpdateCoPracticeLvl(coPracticeLvl))
		{
			this.updateMaxHp();
//			this.resetFightSkills();
			return true;
		}
		
		return false;
	}
	
	boolean onUpdateStarLvl(int starLvl)
	{
		if(this.propPet.onUpdateStar(starLvl))
		{
			this.updateMaxHp();
			return true;
		}
		
		return false;
	}
	
	boolean onUpdateBreakSkills(Map<Integer, Integer> breakSkills)
	{
		this.propPet.onUpdateBreakSkills(breakSkills);
		this.updateMaxHp();
		return true;
	}

	void updateBuffProps()
	{
		super.updateBuffProps();
		this.updateMaxHp();
	}
	
	void resetLocation()
	{
		int curGridX = this.getCurMapGrid().getGridX();
		int curGridZ = this.getCurMapGrid().getGridZ();
		this.setCurPosition(this.owner.createPosition(MapManager.PET_CREATE_RADIUS));
		int newGridX = this.curMap.calcGridCoordinateX((int) this.getLogicPosition().x);
		int newGridZ = this.curMap.calcGridCoordinateZ((int) this.getLogicPosition().z);
		MapGrid newGrid = this.curMap.getGrid(newGridX, newGridZ);
		if (newGrid != null)
		{
			this.adjustPos();
			boolean change = this.changeMapGrid(newGrid);
			if (change)
				this.onSelfLeaveNearBy(this.curMap.getSelfLeaveRoleIDsNearby(curGridX, curGridZ, newGridX, newGridZ, this.owner, this.owner != null), 1);
			
			Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_resetposition(this.owner.id, this.id, this.getLogicPosition()));
		}
		this.breakSkill();
//		this.clearAllSkillsCD();
		if(this.isMoving())
			this.onStopMoveImpl(this.curPosition, ms.getMapManager().getTimeTickDeep(), false);
	}
	
	void onPetRevive(boolean roleFullHp)
	{
		this.curHP = this.getMaxHP();
		if (this.isInPrivateMap())
		{
			ms.getRPCManager().sendStrPacket(this.owner.getID(), new SBean.role_revive_pet(this.getID(), new SBean.Location(this.getLogicPosition(), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
		}
		else
		{
			if (this.owner.curPosition.distance(this.getCurPosition()) > this.maxBehaviorRadius)
				this.setNewPosition(this.owner.createPosition(MapManager.PET_CREATE_RADIUS));
			
			if(roleFullHp)
			{
				ms.getRPCManager().sendStrPacket(this.owner.getID(), new SBean.role_revive_pet(this.getID(), new SBean.Location(this.getLogicPosition(), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
				Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
				if(!rids.isEmpty())
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_revive(this.owner.getID(), this.getID(), this.getLogicPosition()));
			}

			this.clearAllSkillsCD();
		}
		
		if(roleFullHp)
		{
			SBean.CommonCFGS commonCfg = GameData.getInstance().getCommonCFG();
			this.addState(Behavior.EBREVIVE);
			this.specialState.put(Behavior.EBREVIVE, ms.getMapManager().getMapLogicTime() + (long) (commonCfg.revives.protectTime + 500));
			ms.getRPCManager().sendStrPacket(this.owner.getID(), new SBean.pet_addstate(this.getID(), Behavior.EBREVIVE, ms.getMapManager().getTimeTickDeep()));
		}
	}

	boolean changeMapGrid(MapGrid grid)
	{
		if (grid != this.curMapGrid)
		{
			this.curMapGrid.delPet(this);
			grid.addPet(this);
			return true;
		}
		return false;
	}

	private void addMoveCheckSpeed(int checkSpeed)
	{
		this.checkSpeeds.add(checkSpeed);
		if (this.checkSpeeds.size() > 10)
			this.checkSpeeds.remove(0);
	}

	boolean onMoveMent(int speed, SBean.Vector3F rotation, GVector3 pos, GVector3 targetPos, SBean.TimeTick timeTick)
	{
		if(speed > this.getFightProp(EPROPID_SPEED) * 2)
			return false;
		
		if(!super.onMoveMent(speed, rotation, pos, targetPos, timeTick))
			return false;

		if(!this.owner.robot)
		{
			if (this.preMovePosition != null && this.preMoveTimeTick != null)
				this.addMoveCheckSpeed((int) (this.preMovePosition.distance(pos) / (getTimeInterval(this.preMoveTimeTick, timeTick) / 1000.f)));

			this.preMovePosition = pos;
			this.preMoveTimeTick = timeTick;
			
			GVector3 realPos = pos.sum(this.curRotation.scale(this.owner.getPing() * speed / 1000.0f));
			this.fixNewPosition(this.curRotation, realPos, this.moveTargetPos);
			
			this.setNewPosition(realPos);
		}
		
		this.serverStopTime = 0;
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_pet(this.owner.getID(), this.getID(), this.getLogicPosition(), this.moveSpeed, rotation, timeTick));
		
		return true;
	}

	void onSelfUpdatePosition(Set<Integer> rids, SBean.Vector3 position, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_updateposition(this.owner.id, this.id, position, timeTick));
	}

	void clientAdjustPetPos(SBean.Vector3 position)
	{
		if (this.moveSpeed == 0 || !this.checkState(Behavior.EBMOVE))
			return;
		
		this.setCurPosition(new GVector3(position));
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_updateposition(this.owner.id, this.id, position, ms.getMapManager().getTimeTickDeep()));
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
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_pet(this.owner.getID(), this.getID(), this.getLogicPosition(), this.getFightProp(EPROPID_SPEED), ms.getMapManager().getTimeTickDeep()));
		}
	}
	
	void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.setNewPosition(position);
		this.onStopMoveImpl(position, timeTick, broadcast);
	}
	
	void onStopMoveImpl(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		int speed = this.getFightProp(EPROPID_SPEED);
		if(broadcast || this.owner.robot)
		{
			this.serverStopTime = 0;
			Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_pet(this.owner.getID(), this.getID(), this.getLogicPosition(), speed, timeTick));
		}
		
		this.moveSpeed = 0;
		this.moveTarget = null;
		this.preMovePosition = null;
		this.removeState(Behavior.EBMOVE);
		this.removeState(Behavior.EBRETREAT);
	}
	
	SBean.PetDetail getPetDetail()
	{
		Map<Integer, Integer> curBuffs = this.buffs.isEmpty() ? GameData.emptyMap() : new HashMap<>();
		this.buffs.values().forEach(b -> curBuffs.put(b.id, b.id));
		
		return new SBean.PetDetail(this.owner.id, this.getPetOverview(), new SBean.FightState(this.curHP, this.getMaxHP(), curBuffs, 0, GameData.emptySet()));
	}
	
	SBean.PetProfile getPetProfile()
	{
		return new SBean.PetProfile(this.getPetOverview(), this.curHP, this.getMaxHP()); 
	}
	
	SBean.PetOverview getPetOverview()
	{
		return new SBean.PetOverview(this.id, this.level, this.propPet.getStarLvl(), this.fightpower);
	}
	
	boolean checkCanBeAttack(BaseRole attacker)
	{
		return this.owner.checkCanBeAttack(attacker);
	}
	
	//获取目标
	List<BaseRole> getTargets(Skill skill)
	{
		SBean.Scope scope = skill.baseFixCfg.scope;
		int skillType = skill.baseFixCfg.type;
		int maxTargets = skill.baseFixCfg.maxTargets;
		List<BaseRole> targets = new ArrayList<>();
		//技能范围类型（自身、单点技能）
		if (scope.type == Skill.eSScopT_Owner)
		{
			targets.add(this);
			return targets;
		}
		else if (scope.type == Skill.eSScopT_Single)
		{
			float skillRange = scope.args.get(0) + this.getRadius();
			float distance = 0.0f;
			BaseRole entity = null;
			if (skill.triSkillTarget > 0 && this.lastAttacker != null)
			{
				if (this.checkTargetValid(this.lastAttacker))
					entity = this.lastAttacker;
				else
					this.lastAttacker = null;
			}

			if (entity == null)
			{
				BaseRole force = this.getForceTarget();
				if (force != null && this.checkTargetValid(force))
					entity = force;
			}

			if (entity != null)
			{
				if (!this.curMap.checkBaseRoleCanAttack(this, entity))
					entity = null;
			}

			if (entity == null)
				entity = this.getRoleInCheckRange();

			if (entity != null)
			{
				distance = this.curPosition.distance(entity.curPosition);
				if (distance <= skillRange)
					targets.add(entity);
			}
			return targets;
		}

		//技能效果类型
		switch (skillType)
		{
		case GameData.eSE_Buff:
			//己方单位
			targets.addAll(this.getRoleFriendEntityNearBy(this.owner));
			targets.add(this.owner);
			break;
		case GameData.eSE_Damage:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());
			//中立单位
			for (Trap t : this.getTrapNearBy())
			{
				if(!t.isDead())
					targets.add(t);
			}
			break;
		case GameData.eSE_DBuff:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());
			break;
		default:
			break;
		}
		this.filterTarget(targets, this.owner.getNearByRoles());
		targets = skill.checkTarget(this, targets, scope, maxTargets);

		return targets;
	}

	void addSPOnUseSkill(int sp)
	{
		this.setCurSP(this.curSP + sp);
	}

	void setCurSP(int sp)
	{
		int temp = this.curSP;
		if (sp < 0)
		{
			this.curSP = 0;
		}
		else if (sp > this.getMaxSP())
		{
			this.curSP = this.getMaxSP();
		}
		else
		{
			this.curSP = sp;
		}
		if (temp != this.curSP && !this.isInPrivateMap())
		{
			if (!this.owner.robot)
				ms.getRPCManager().sendStrPacket(this.owner.getID(), new SBean.pet_sync_sp(this.getID(), this.curSP));
		}
	}

	public int setCurHP(int curHP)
	{
		if (this.isDead())
			return 0;

		if (curHP <= 0)
		{
			this.deadTrig();
			curHP = 0;
		}
		else if (curHP > this.getMaxHP())
			curHP = this.getMaxHP();

		if (this.curHP != curHP)
		{
			this.curHP = curHP;
			ms.getRPCManager().syncPetHp(this.owner.id, this.id, this.getMapID(), this.getMapInstanceID(), this.curHP, this.maxHp);
		}

		if (this.isDead())
			this.deadTime = GameTime.getTime();

		return this.curHP;
	}
	
	public void onAddHp(int hp)
	{
		if(hp == 0 || this.getCurHP() == 0)
			return;
		
		this.setCurHP(this.getCurHP() + hp);
		if(!this.isInPrivateMap())
		{
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			this.notifyUpdateHp(rids);
		}
		else
		{
			ms.getRPCManager().sendStrPacket(this.owner.id, new SBean.nearby_pet_updatehp(this.owner.getID(), this.getID(), this.getCurHP()));
		}
	}
	
	void superAreanRaceEnd()
	{
		this.clearAllBuff();
		this.curHP = this.getMaxHP();
		ms.getRPCManager().syncPetHp(this.owner.id, this.id, this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
		if(this.curSP > 0)
			this.setCurSP(0);
		
		this.addState(Behavior.EBPREPAREFIGHT);
		this.specialState.put(Behavior.EBPREPAREFIGHT, ms.getMapManager().getMapLogicTime() + GameData.getInstance().getSuperArenaCFGS().normal.prepareTime * 1000L);
	}
	
	void onTrigChildSkill(int mainSkill, int skillID)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_usechildskill(this.owner.id, this.id, mainSkill, skillID));
	}
	
	void onSelfTrigSkill(Set<Integer> rids, int skillID)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_usetrigskill(this.owner.id, this.id, skillID));
	}

	boolean onUseSkill(int skillID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		if (this.isInProtectTime())
			return false;

		GVector3 newPos = new GVector3(pos);
		if (!this.serverControl && this.isPositionError(this.curPosition, newPos))
		{
			this.adjustPos();
			return false;
		}
		else
			this.setCurPosition(newPos);

		FightSkill fSkill = this.fightSkills.get(skillID);
		if (fSkill == null)
			return false;

		if (skillID == this.utlSkill) //大招
		{
			if (this.curSP < this.getMaxSP())
			{
				this.curStates.remove(Behavior.EBATTACK);
				return false;
			}

			this.breakSkill();
			this.setCurSP(0);
		}

		if (!this.canUseSkill(fSkill))
			return false;

		this.forceTarget = new MapEntity(targetType, targetID, ownerID);
		BaseRole entity = this.getForceTarget();
		if (entity != null)
		{
			if(entity != this)
				this.setCurRotation(entity.curPosition.diffence2D(this.getCurPosition()).normalize());
		}
		else
			this.setCurRotation(new GVector3(rotation));
		
		long now = this.getTimeByTimeTick(timeTick);
		Skill skill = this.createNewSkill(fSkill.baseDataCfg.common, fSkill.baseDataCfg.fix, fSkill.lvlFixCfg, fSkill.level, fSkill.realmLvl, fSkill.skillGroup, now);
		if (skill == null)
			return false;
		
		this.onUseSkillTrig(skill.baseFixCfg.type, fSkill.useType);
		skill.target = entity;
		this.attack = skill;
		this.curUseSkillAddCache.add(skill);
//		fSkill.coolDownTime = now + (long) (skill.lvlFixCfg.cool * fSkill.coolDownPercent / 100.0f);
		if (skill.baseCommonCfg.canAttack == 0)
			this.addState(Behavior.EBDISATTACK);

		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_useskill(this.owner.getID(), this.getID(), skillID, pos, rotation, targetID, targetType, ownerID, timeTick));
		
//		ms.getLogger().debug("@@@@@@@@@@@@@@@@@pet " + this.owner.roleName + " use skill " + skillID + " curAttackSeq " + this.curAttackSeq);
		return true;
	}

	void onEndSkillHandle(Skill skill)
	{
		if (skill.isAttackEffect)
		{
			if (skill.skillGroup == Skill.eSG_Skill)
			{
				if (skill.lvlFixCfg.addSP > 0 && skill.id != this.utlSkill && !this.isDead())
				{
					this.addSPOnUseSkill(skill.lvlFixCfg.addSP);
				}
			}
			skill.isAttackEffect = false;
		}

		if (skill.baseCommonCfg.canAttack == 0)
			this.curStates.remove(Behavior.EBDISATTACK);

		if (this.owner.robot)
		{
			this.stupidTime = ms.getMapManager().getMapLogicTime() + GameRandom.getRandInt(this.stupidInfo.curMinTime, this.stupidInfo.curMaxTime);
//			ms.getLogger().debug("pet end skill " + skill.id + " " + this.owner.roleName + " stupidTime " + (stupidTime - ms.getMapManager().getMapLogicTime()));
		}
		if (skill.rushInfo != null && !skill.rushInfo.rushStart && skill.valid)
		{
			if (!this.checkState(Behavior.EBATTACK) && !this.checkState(Behavior.EBDISATTACK))
				this.adjustPos();
		}
		
		this.notifyEndSkill(skill);
	}

	void notifyEndSkill(Skill skill)
	{
		super.notifyEndSkill(skill);
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(skill.slashInfo != null && skill.damageTick < skill.damageCount)
			rids.add(this.owner.id);
		
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_endskill(this.id, this.owner.id, skill.id, ms.getMapManager().getTimeTickDeep()));
	}
	
	void notifyFinishAttack(Skill skill)
	{
		if (this.owner.robot)
			this.stupidTime = ms.getMapManager().getMapLogicTime() + GameRandom.getRandInt(this.stupidInfo.curMinTime, this.stupidInfo.curMaxTime);
		
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(skill.slashInfo != null && skill.damageTick < skill.damageCount)
			rids.add(this.owner.id);
		
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_finishattack(this.id, this.owner.id, skill.id, ms.getMapManager().getTimeTickDeep()));
	}
	
	void notifyBreakSkill()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_breakskill(this.owner.id, this.id));
	}
	
	void adjustPos()
	{
		ms.getRPCManager().sendStrPacket(this.owner.id, new SBean.pet_adjust_pos(this.id, this.curPosition.toVector3()));
	}

	public void onRushStart(int skillID, SBean.Vector3 endPos, SBean.TimeTick timeTick)
	{
		Skill skill = this.getCurInUseSkill(skillID, -1);
		if (skill == null || skill.rushInfo == null)
			return;

		int newGridX = this.curMap.calcGridCoordinateX((int) endPos.x);
		int newGridZ = this.curMap.calcGridCoordinateZ((int) endPos.z);
		MapGrid newGrid = this.curMap.getGrid(newGridX, newGridZ);
		if (newGrid == null)
			return;

		GVector3 endPosition = new GVector3(endPos);
		float distance = this.curPosition.distance(endPosition);
		if (distance > Math.abs(skill.rushInfo.distance) + 100)
			return;

		skill.rushInfo.rushEndPos = endPosition;
		skill.rushInfo.rushRotation = skill.rushInfo.rushEndPos.diffence2D(this.curPosition).normalize();
		skill.rushInfo.rushStart = true;
		skill.rushInfo.preUpdateTime = ms.getMapManager().getMapLogicTime();
		skill.attackPosition = new GVector3().reset(this.curPosition);
		Set<Integer> rids = this.getRoleIDsNearBy(this.owner);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_rushstart(this.owner.id, this.getID(), skillID, endPos, timeTick));
	}

	int getFightSkillLevel(int sid)
	{
		FightSkill skill = this.fightSkills.get(sid);
		if (skill == null)
			return 0;
		return skill.level;
	}

	void nextSkill()
	{
		if(!this.owner.robot)
			return;
		
		if (!this.setUtlSkill())
		{
			this.curAttackSeq++;
			if (this.curAttackSeq >= this.attackList.size())
				this.curAttackSeq = 0;

			this.curSkillID = null;
			this.setCurSkillID();
		}
	}

	private boolean setUtlSkill()
	{
		if (this.curSP >= this.getMaxSP())
		{
			this.curSkillID = this.utlSkill;
			this.setAttackRange();
//			this.setCurSkillID();
			return true;
		}
		return false;
	}

	private void setCurSkillID()
	{
		if (this.curSkillID != null || this.attackList.isEmpty())
			return;

		SBean.PetCFGS petCfg = GameData.getInstance().getPetCFG(this.getConfigID());
		if (petCfg != null)
		{
			int attackNum = this.attackList.get(this.curAttackSeq);
			int rand = 0;
			if (attackNum == 0)
			{
				rand = GameRandom.getRandom().nextInt(petCfg.attacks.size());
				this.curSkillID = petCfg.attacks.get(rand);
			}
			else
			{
				if (attackNum <= this.curSkills.size())
					this.curSkillID = this.curSkills.get(attackNum - 1);
			}

			if (this.curSkillID == null || this.curSkillID == 0)
			{
				this.nextSkill();
				return;
			}

			this.setAttackRange();
		}
	}

//	public void onProcessDamage(int skillID, int damageTick, GVector3 rotation, SBean.TimeTick timeTick)
//	{
//		int curEventID = damageTick;
//		Skill skill = this.getCurInUseSkill(skillID, damageTick);
//		if (skill == null)
//			return;
//
//		if (skill.damageTick >= skill.damageCount)
//			return;
//
//		if (!(skill.flyInfo == null || skill.touchTarget))
//			return;
//
//		if (skill.guideInfo != null)
//			curEventID = skill.guideInfo.damageEventID;
//
//		skill.onProcessDamageHandler(this, curEventID, rotation, timeTick);
//	}

	void onGetDamageHandler(BaseRole attacker, int damage)
	{
		SBean.PetCFGS petcfg = GameData.getInstance().getPetCFG(this.getID());
		this.setCurSP(this.curSP + (petcfg == null ? 0 : petcfg.hurtAddSP));
		if (attacker.owner != null && this.owner.effectPKState(attacker.owner))
			attacker.owner.resetPKState(this.owner.getNameColor() == GameData.NAME_COLOR_RED);
	}

	SBean.EnterPet getEnterPet()
	{
		return new SBean.EnterPet(getEnterBase(), this.curHP, this.maxHp, seq, this.isDead() ? 1 : 0);
	}
	
	void onSelfEnterNearBy(Set<Integer> rids)
	{
		rids.remove(this.owner.id);
		if(!rids.isEmpty())
		{
			List<SBean.EnterPet> pets = new ArrayList<>();
			pets.add(this.getEnterPet());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_pets(pets));
		}
	}

	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		rids.remove(this.owner.id);
		if(!rids.isEmpty())
		{
			List<SBean.PetBase> pets = new ArrayList<>();
			pets.add(new SBean.PetBase(this.owner.getID(), this.getID()));
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_pets(pets, destory));
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_pet(this.owner.getID(), this.getID(), destory));
		}
	}
	
	void onSelfDead(Set<Integer> rids, int killerID)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_dead(this.owner.getID(), this.getID(), killerID));
	}

	void onDeadHandle(int attackerType, int attackerID)
	{
		super.onDeadHandle(attackerType, attackerID);
		this.specialState.remove(Behavior.EBREVIVE);
		if(this.checkState(Behavior.EBREVIVE))
			this.removeState(Behavior.EBREVIVE);
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfDead(rids, attackerID);
		
		this.setCurSP(0);
		this.clearAllBuff();
		if (!this.curMap.isFightMap())
			this.clearSummonMonster(attackerID);
	}

	void notifyAddBuff(Set<Integer> rids, int buffID, int realmLvl, int remainTime, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_addbuff(this.owner.getID(), this.getID(), buffID, realmLvl, remainTime, timeTick));
	}

	void notifyRemoveBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_removebuff(this.owner.getID(), this.getID(), buffID, timeTick));
	}

	void notifyDispelBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_dispelbuff(this.owner.getID(), this.getID(), buffID, timeTick));
	}

	void notifyUpdateHp(Set<Integer> rids)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_updatehp(this.owner.getID(), this.getID(), this.getCurHP()));
	}

	void notifyBuffDamage(Set<Integer> rids, int attackerType, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_buffdamage(this.owner.getID(), this.getConfigID(), this.getCurHP(), attackerType, timeTick));
	}

	void notifySelfGetDamage(Set<Integer> rids, BaseRole attacker, int ownerID, SBean.DamageResult res, int skillID, int curDamageEventID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_ondamage(this.owner.id, this.id, attacker.id, attacker.getEntityType(), ownerID, skillID, curDamageEventID, this.getCurHP(), res.dodge, res.deflect, res.crit, res.suckBlood, res.behead, res.remit, timeTick));
		
//		ms.getLogger().debug("pet " + this.owner.roleName + " on damage skill " + skillID + " dmg " + res.damage + " curHp " + this.curHP);
	}

	void notifySelfReduce(Set<Integer> rids, int reduce)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_reduce(this.owner.id, this.id, reduce));
	}

//	void notifySelfBeHead(Set<Integer> rids)
//	{
//		if (!rids.isEmpty())
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_behead(this.owner.id, this.id));
//	}

	void addEbataunt(Buff buff, SBean.TimeTick timeTick)
	{
		this.forceTarget = new MapEntity(buff.attackerType, buff.attackerID, buff.attackOwnerID);
		BaseRole entity = this.getForceTarget();
		if (entity != null)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_addataunt_pet(this.getID(), this.owner.getID(), buff.attackerID, buff.attackerType, buff.attackOwnerID, timeTick));
		}
	}

	void notifyUpdateMaxHp(Set<Integer> rids)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_updatemaxhp(this.owner.getID(), this.getID(), this.getMaxHP()));
	}

	void notifySelfShiftEnd(Set<Integer> rids, int skillID, GVector3 endpos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_shiftend_pet(skillID, this.owner.getID(), this.getID(), endpos.toVector3(), timeTick));
	}

	void notifySelfRemoveState(int sid, SBean.TimeTick timeTick)
	{
		if (!this.owner.robot && Behavior.notifyStates.contains(sid))
		{
			ms.getRPCManager().sendStrPacket(this.owner.id, new SBean.pet_removestate(this.id, sid, timeTick));
		}
	}

	void notifySelfRushStart(Set<Integer> rids, int skillID, SBean.Vector3 endPos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_pet_rushstart(this.owner.id, this.id, skillID, endPos, timeTick));
	}

	void notifyResetSkill(int skillID)
	{
		if (skillID > 0)
			ms.getRPCManager().sendStrPacket(this.owner.id, new SBean.pet_reset_skill(this.id, skillID));
	}

	void notifyQuickCoolSkill(int skillID, int time)
	{
		if (skillID > 0)
			ms.getRPCManager().sendStrPacket(this.owner.id, new SBean.pet_quickcool_skill(this.id, skillID, time));
	}

	String getName()
	{
		return this.owner.roleName;
	}

	public void onPrivateMapUpdateHP(int hp)
	{
		this.setCurHP(hp);
	}

	public void onSyncPrivateMapSP(int skillID)
	{
		if (skillID == -1)
		{
			this.addSPOnUseSkill(this.hurtAddSP);
		}
		else if (skillID == 0)
		{
			this.curSP = 0;
		}
		else
		{
			FightSkill fighSkill = this.fightSkills.get(skillID);
			if (fighSkill == null || fighSkill.skillGroup != Skill.eSG_Skill)
				return;

			SBean.SkillCFGS skillCfg = GameData.getInstance().getSkillCFG(skillID);
			if (skillCfg == null)
				return;

			SBean.SkillLevelCFGS skillData = GameData.getSkillLevelCFG(skillCfg, fighSkill.level);
			if(skillData != null)
				this.addSPOnUseSkill(skillData.fix.addSP);
		}
	}

	int getCurSP()
	{
		return this.curSP;
	}

	int getSpiritTotalLays()
	{
		return this.propPet.getSpiritTotalLays();
	}

	int getWeaponTotalLvls()
	{
		return this.propPet.getWeaponTotalLvls();
	}

	Set<Integer> getRoleIDsNearBy(MapRole role)
	{
		if(!this.filterRole())
			return super.getRoleIDsNearBy(role);
		
		Set<Integer> rids = new HashSet<>(this.owner.beSeenRoles);
		if(role == null)
			rids.add(this.owner.id);
		
		return rids;
	}
	
	public class StupidTime
	{
		final int minTime;
		final int maxTime;
		int curMinTime;
		int curMaxTime;
		
		StupidTime(SBean.PetCFGS petCfg)
		{
			this.minTime = petCfg.minStupidTime;
			this.maxTime = petCfg.maxStupidTime;
			reset();
		}
		
		void reset()
		{
			this.curMinTime = minTime;
			this.curMaxTime = maxTime;
		}
		
		void check()
		{
			if(this.curMinTime < 0)
				this.curMinTime = 0;
			
			if(this.curMaxTime < 0)
				this.curMaxTime = 0;
		}
	}
	
	void onUpdatePetSpirit(int index, SBean.PetSpirit spirit)
	{
		SBean.PetSpirit old = this.propPet.getSpirit(index);
		if(old != null)
			this.updateSpiritAi(old, false);
		
		this.propPet.onUpdatePetSpirit(index, spirit);
		this.updatePetStupidTime();
		this.updateSpiritAi(spirit, true);
		this.updateMaxHp();
	}
	
	private void updatePetStupidTime()
	{
		this.stupidInfo.reset();
		for(SBean.PetSpirit s: this.propPet.getCurSpirits())
		{
			if(s.id == 0)
				continue;
			
			SBean.PetSpiritCFGS cfg = GameData.getInstance().getPetSpiritCFGS(s.id, s.level);
			if(cfg == null || cfg.effectType != GameData.PET_SPIRIT_EFFECT_TYPE_REDUCE_STUPID)
				continue;
			
			this.stupidInfo.curMinTime -= cfg.params.get(0);
			this.stupidInfo.curMaxTime -= cfg.params.get(1);
		}
		
		this.stupidInfo.check();
	}
	
	private void updateSpiritAis()
	{
		for(SBean.PetSpirit s: this.propPet.getCurSpirits())
			updateSpiritAi(s, true);
	}
	
	private void updateSpiritAi(SBean.PetSpirit s, boolean add)
	{
		if(s.id == 0)
			return;
		
		SBean.PetSpiritCFGS cfg = GameData.getInstance().getPetSpiritCFGS(s.id, s.level);
		if(cfg == null || cfg.effectType != GameData.PET_SPIRIT_EFFECT_TYPE_PET_AI)
			return;
		
		int aid = cfg.params.get(0);
		if(aid <= 0)
			return;

		if(add)
		{
			TrigerAi trigAi = this.addTrigAi(aid, 0);
			if(trigAi != null)
				this.spiritAis.put(aid, trigAi.id);
		}
		else
		{
			Integer instanceID = this.spiritAis.remove(aid);
			if(instanceID != null)
				this.trigerAiMgr.removeTrigerAi(instanceID);
		}
	}
	
	///// move
	private int second;
	private List<Integer> checkSpeeds = new ArrayList<>();

	private int fightpower; //战力
	int curSP;

	private PropPet propPet;
	private int curAttackSeq; //自动战斗
	private int deadTime;
	private int hurtAddSP;
	private int utlSkill;
	private int autoReviveTime;
	private long moveTickLine;

	private StupidTime stupidInfo;
	private long stupidTime;
	private int maxBehaviorRadius;
	
	private GVector3 preMovePosition;
	private SBean.TimeTick preMoveTimeTick;
	int seq;
	private Map<Integer, Integer> spiritAis = new HashMap<>();
}
