package i3k.gmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import i3k.SBean;
import i3k.gmap.BaseRole.SpiritCluster;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

public class SkillEntity extends BaseRole
{
	private static final int MAX_DISTANCE_TO_HOSTER = 300;

	Skill skill;
	long lifeMax;
	int modelID;
	boolean valid;

	SkillEntity(MapServer ms, MapRole owner, GVector3 position, int skillID, int modelID)
	{
		super(ms, true);
		this.id = ms.getMapManager().getNextSkillEntityID().incrementAndGet();
		this.entityType = GameData.ENTITY_TYPE_SKILL;
		this.ctrlType = ECTRL_TYPE_AI;
		this.owner = owner;
		this.active = owner.active;
		this.level = owner.level;
		this.curMapID = owner.getMapID();
		this.curPosition.reset(position);
		this.configID = skillID;
		this.modelID = modelID;
	}
	
	SkillEntity createNew(SBean.SkillBaseCommonCFGS baseCommon, SBean.SkillBaseFixCFGS baseFix, SBean.SkillLevelFixCFGS lvlFixCfg, int skillLvl, int skillRealmLvl, int speed, long createTime)
	{
		this.speed = speed;
		this.skill = this.createNewSkill(baseCommon, baseFix, lvlFixCfg, skillLvl, skillRealmLvl, Skill.eSG_TriSkill, createTime);
		this.skill.attackPosition = this.curPosition;
		FightSkill fSkill = new FightSkill(this.skill.id, this.skill.level, this.skill.lvlFixCfg.cool, this.skill.realmLvl, Skill.eSG_TriSkill, 1, GameData.SKILL_USE_TYPE_ATTACK).copySkillData();
		if (fSkill != null)
			this.fightSkills.put(fSkill.id, fSkill);
		
		this.lifeMax = (long) skill.skillDuration + ms.getMapManager().getMapLogicTime();
		this.valid = true;
		return this;
	}
	
	SpiritCluster getNormalSpirit(int type)
	{
		return this.owner.allSpirits.get(type);
	}
	
	SpiritCluster getHorseSpirit(int type)
	{
		return this.owner.horseSkillSpirits.get(type);
	}
	
	void onTrigChildSkill(int mainSkill, int skillID)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_skillentity_usechildskill(this.id, mainSkill, skillID));
		
//		ms.getLogger().debug("############skill entity rids " + rids + this.id + " nearby_skillentity_usechildskill mainSkill　" + mainSkill + " skillID " + skillID);
	}
	
	void notifyEndSkill(Skill skill)
	{
		super.notifyEndSkill(skill);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_skillentity_endskill(this.id, skill.id, ms.getMapManager().getTimeTickDeep()));
	}
	
	SBean.EnterSkillEntity getEnterSkillEntity()
	{
		return new SBean.EnterSkillEntity(this.getEnterBase(), this.modelID);
	}
	
	String getName()
	{
		return this.owner.roleName;
	}
	
	boolean onTimer(int timeTick, long logicTime)
	{
		if (this.curMap == null || (this.curMapGrid == null && !this.isInPrivateMap()) || !this.active)
			return false;

		this.onMilliSecondTask(logicTime);
		return !this.curMap.isTimeOut() && this.isDead();
	}

	boolean isDead()
	{
		return (ms.getMapManager().getMapLogicTime() > this.lifeMax && !this.valid) || (this.owner.isDead());
	}

	void onMilliSecondTask(long logicTime)
	{
		if ((logicTime > this.lifeMax && !this.valid) || (this.owner.isDead()) || this.skill == null)
			return;

		this.checkSkill(logicTime);
		this.moveToHoster(logicTime);

		this.lastLogicTime = logicTime;
		if (!this.valid)
			return;

		if (!this.skill.autoProcessDamage(this, logicTime, ms.getMapManager().getTimeTickDeep()))
			this.valid = false;
	}

	void moveToHoster(long logicTime)
	{
		if (this.speed == 0)
			return;

		if (this.moveSpeed > 0)
			this.onMoving(logicTime);
		else
			this.tryToMove();
	}

	int getFightProp(int propID)
	{
		return this.owner.getFightProp(propID);
	}

	void onMoving(long logicTime)
	{
		this.updateMoveTarget();
		SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
		float distance = this.curPosition.distance(this.owner.curPosition);
		if (this.curPosition.distance(this.moveTargetPos) <= 5 || distance <= MAX_DISTANCE_TO_HOSTER)
			this.onStopMove(this.curPosition, timeTick, true);
		else
		{
			float length = (this.moveSpeed * (logicTime - this.lastLogicTime) / 1000.0f);
			GVector3 moveDir = this.moveTargetPos.diffence2D(this.curPosition).normalize();
			GVector3 newPosition = this.curPosition.sum(moveDir.scale(length));
			this.fixNewPosition(moveDir, newPosition, this.moveTargetPos);

			this.setCurPosition(newPosition);
			this.skill.attackPosition = newPosition;
			this.setCurRotation(moveDir);
			if (logicTime - this.preMoveTime >= MapManager.PROTOCOL_AUTOSEND_INTERVAL && this.moveSpeed > 0)
			{
				this.onMoveMent(newPosition, this.moveTargetPos, this.moveSpeed, timeTick);
				this.preMoveTime = logicTime;
			}
		}
	}

	void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.onStopMoveImpl(position, timeTick, broadcast);
	}

	void onStopMoveImpl(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.moveSpeed = 0;
		this.moveTarget = null;
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_skillentity(this.getID(), this.getLogicPosition(), timeTick));
	}
	
	void tryToMove()
	{
		if (this.curPosition.distance(this.owner.curPosition) <= MAX_DISTANCE_TO_HOSTER)
			return;

		this.moveSpeed = this.speed;
		int radius = GameRandom.getRandInt(150, 300);
		GVector3 dirCToM = this.getCurPosition().diffence2D(this.getBirthPosition()).normalize();
		this.moveTargetPos = this.getRandomTargetPos(this.owner.curPosition, dirCToM, radius, (float) Math.PI / 3);
		this.moveTarget = this.owner;
		this.onMoveMent(this.curPosition, this.moveTargetPos, this.moveSpeed, ms.getMapManager().getTimeTickDeep());
	}

	void onMoveMent(GVector3 position, GVector3 target, int speed, SBean.TimeTick timeTick)
	{
		int newGridX = this.curMap.calcGridCoordinateX((int) position.x);
		int newGridZ = this.curMap.calcGridCoordinateZ((int) position.z);
		MapGrid newGrid = this.curMap.getGrid(newGridX, newGridZ);

		if (newGrid != null && this.getCurMapGrid() != null)
		{
			this.moveTargetPos = target;
			this.moveSpeed = speed;

			this.setCurPosition(position);
			this.setCurRotation(this.moveTargetPos.diffence2D(this.getCurPosition()).normalize());
			
			int curGridX = this.getCurMapGrid().getGridX();
			int curGridZ = this.getCurMapGrid().getGridZ();
			boolean change = this.changeMapGrid(newGrid);
			if (change)
				this.changePosition(curGridX, curGridZ, newGridX, newGridZ, 0);

			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_skillentity(this.id, this.getLogicPosition(), this.moveSpeed, this.getCurRotation().toVector3F(), timeTick));
		}
	}

	boolean changeMapGrid(MapGrid grid)
	{
		if (grid != this.curMapGrid)
		{
			this.curMapGrid.delSkillEntity(this.id);
			grid.addSkillEntity(this);
			return true;
		}
		return false;
	}

	void checkSkill(long logicTime)
	{
		long skillStartTime = skill.skillEndTime - skill.skillDuration;
		int time = this.skill.baseCommonCfg.spell.time + this.skill.baseCommonCfg.charge.time;
		if (logicTime > skillStartTime + time)
		{
			if (this.skill.flyInfo != null)
				this.checkFlySkill(logicTime, this.skill);

			if (this.skill.rushInfo != null)
				this.checkRushSkill(logicTime, this.skill);

			if (this.skill.shiftInfo != null)
				this.useSkillShift(this.skill);

			if (this.skill.auraInfo != null)
				this.checkAuraSkill(logicTime, this.skill);
			
			if(skill.summonInfo != null)
				this.checkSummonSkill(logicTime, skill);
		}

		this.checkChiledSkill(logicTime, this.skill);
	}

	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterSkillEntity> lst = new ArrayList<>();
			lst.add(this.getEnterSkillEntity());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_skillentitys(lst));
		}
	}
	
	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> lst = new ArrayList<>();
			lst.add(this.id);
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_skillentitys(lst));
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_skillentity(this.id));
		}
	}
	
	void onCreateHandle()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfEnterNearBy(rids);
		
//		ms.getLogger().debug("notify rids " + rids + " create skillEntity " + this.id);
	}

	void onDeadHandle()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfLeaveNearBy(rids, 1);

		if (this.skill.auraInfo != null)
		{
			for (BaseRole r : this.skill.auraInfo.effectEntitys)
				r.removeBuff(skill.auraInfo.buff);

			this.skill.auraInfo.effectEntitys.clear();
		}

		this.owner.skillEntitys.remove(this);
		this.curMap.delSkillEntityToRemoveCache(this.id);
	}

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
			BaseRole entity = this.getForceTarget();
			if (entity != null && !entity.isDead() && !entity.isInProtectTime())
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
//			List<Trap> trapsNearBy = this.getTrapNearBy();
//			for (Trap t : trapsNearBy)
//			{
//				targets.add(t);
//			}
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

	BaseRole getRoleInCheckRange()
	{
		BaseRole entity = null;
		entity = this.getPlayerEntity();
//		if (entity == null)
//			entity = this.getNearestTrap(this.getTrapNearBy());
		return entity;
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
}
