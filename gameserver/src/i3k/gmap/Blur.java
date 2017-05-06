package i3k.gmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

public class Blur extends BaseRole
{

	Blur(int configID, MapServer ms)
	{
		super(ms, true);
		this.configID = configID;
	}

	public Blur createNew(GVector3 position, MapRole owner)
	{
		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_BLUR;
		this.id = ms.getMapManager().getNextBlurID().incrementAndGet();
		this.curPosition.reset(position);
		this.birthPosition.reset(position);
		this.curRotation.reset(GVector3.randomRotation());

		this.owner = owner;
		this.active = this.owner.active;
		this.level = 1;
		SBean.FightSPCFGS fightCfg = GameData.getInstance().getfFightSPCFGS(GameData.CLASS_TYPE_ARROW);
		if (fightCfg != null)
			this.level = this.owner.getSkillLvl(fightCfg.relatedSkill);

		this.curMapID = owner.getMapID();
		SBean.BlurCFGS cfg = GameData.getInstance().getBlurCFGS(this.configID);
		if (cfg != null)
		{
			this.standTime = ms.getMapManager().getMapLogicTime() + (long) cfg.survivalTime;
			this.radius = cfg.radius;
			this.speed = cfg.speed;
			//this.maxCheckRange = cfg.checkRadius;
			this.maxTraceRange = 1600;

			this.attackList = cfg.attackList;
			if (this.attackList.size() > 0)
				this.setCurSkillID();
		}

		this.setPropBase(new PropFightRole());
		this.updateBaseProps();
		this.resetFightSkills();
		this.updateMaxHp();
		this.curHP = this.getMaxHP();
		return this;
	}

	boolean onTimer(int timeTick, long logicTime)
	{
		if (this.curMap == null || (this.curMapGrid == null && !this.isInPrivateMap()) || !this.active)
			return false;

		if (this.isDead() || this.isInProtectTime())
			return false;

		if (this.curMap.isTimeOut() || this.curMap.isMapAlreadyFinish || this.curMap.isInPrepared(logicTime))
			return false;

		this.onMilliSecondTask(logicTime);
		return this.isDead() || (this.standTime >= 0 && logicTime > this.standTime);
	}

	void onMilliSecondTask(long logicTime)
	{
		super.onMilliSecondTask(logicTime);
		this.checkStandByTime(logicTime);
		this.lastLogicTime = logicTime;
	}

	void checkStandByTime(long logicTime)
	{
		if (logicTime > this.standTime)
		{
			this.onDeadHandle(0, 0);
		}
	}

	String getName()
	{
		return this.owner.roleName;
	}
	
	void onMoving(long logicTime)
	{
		if (!this.forceFollow)
		{
			BaseRole entity = this.getRoleInCheckRange();
			if (entity != null && !entity.isDead())
			{
				float range = this.attackRange + this.getRadius() + entity.getRadius();
				float distance = entity.getCurPosition().distance(this.getCurPosition());
				if (distance < range) //攻击范围内
				{
					this.onStopMove(this.curPosition, ms.getMapManager().getTimeTickDeep(), true);
					this.attackNearest(entity, logicTime);
					return;
				}
			}
		}

		this.processMove(logicTime);
	}

	void onOwnerUpdateAttackMode()
	{
		BaseRole entity = this.getForceTarget();
		if (entity != null)
		{
			if (!this.curMap.checkBaseRoleCanAttack(this, entity))
				this.forceTarget = null;
		}

		Iterator<BaseRole> it = this.enmityList.iterator();
		while (it.hasNext())
		{
			BaseRole r = it.next();
			if (!this.checkTargetValid(r) || !this.curMap.checkBaseRoleCanAttack(this, r))
				it.remove();
		}
	}

	void moveToNearest(long logicTime)
	{
		if (!this.isInCheckRange(this.curPosition.distance(this.getBirthPosition()))) //超出移动距离
		{
			if (!this.checkState(Behavior.EBRETREAT))
				this.moveToBirth(logicTime);
			return;
		}

		BaseRole entity = this.getForceTarget();
		if (entity == null || !this.checkTargetValid(entity) || !this.curMap.checkBaseRoleCanAttack(this, entity))
			entity = this.getRoleInCheckRange();

		if (entity != null)
		{

			float range = this.attackRange + this.getRadius() + entity.getRadius();
			float distance = entity.getCurPosition().distance(this.getCurPosition());
			if (distance < range)
			{
				this.attackNearest(entity, logicTime);
				return;
			}
		}
		else if (this.curPosition.distance(this.owner.curPosition) > 500)
			entity = this.owner;

		if (entity == null)
			return;

		if (this.addState(Behavior.EBMOVE))
		{
			this.removeState(Behavior.EBMOVE);
			int radius = GameRandom.getRandInt(150, 300);
			GVector3 dirCToM = this.getCurPosition().diffence2D(entity.getCurPosition()).normalize();
			this.moveTargetPos = this.getRandomTargetPos(entity.getCurPosition(), dirCToM, radius, (float) Math.PI / 3);
			
			this.moveTarget = entity;
			this.setCurRotation(this.moveTargetPos.diffence2D(this.getCurPosition()).normalize());
			this.startMove(ms.getMapManager().getMapLogicTime());
		}
	}

	GVector3 getBirthPosition()
	{
		return this.owner == null ? this.birthPosition : this.owner.curPosition;
	}

	void moveToBirth(long logicTime)
	{
		this.forceTarget = null;
		this.forceFollow = true;
		if (this.addState(Behavior.EBRETREAT))
			this.moveSpeed = this.getFightProp(EPROPID_SPEED);
		GVector3 dirCToM = this.getCurPosition().diffence2D(this.getBirthPosition()).normalize();
//		int range = this.attackRange + this.getRadius();
		int radius = GameRandom.getRandInt(150, 300);
		this.moveTargetPos = this.getRandomTargetPos(this.getBirthPosition(), dirCToM, radius, (float) Math.PI / 3);
		this.moveTarget = this.owner;
		this.setAttacked(false);
		this.enmityList.clear();

		this.setCurRotation(this.moveTargetPos.diffence2D(this.getCurPosition()).normalize());
		this.startMove(logicTime);
	}

	void startMove(long logicTime)
	{
		this.onMoveMent((int) this.getFightProp(EPROPID_SPEED), this.curRotation.toVector3F(), this.getCurPosition(), this.moveTargetPos, ms.getMapManager().getTimeTick());
	}
	
	void processMove(long logicTime)
	{
		this.updateMoveTarget();
		SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
		if (this.curPosition.distance(this.moveTargetPos) <= 5)
			this.onStopMove(this.curPosition, timeTick, true);
		else
		{
			float distance  = this.curPosition.distance(this.getBirthPosition());
			if (!this.isInCheckRange(distance) && !this.checkState(Behavior.EBRETREAT)) //超出移动距离
			{
				this.moveToBirth(logicTime);
				return;
			}
			else if (this.forceFollow && distance < 800)
				this.forceFollow = false;
			
			GVector3 moveDir = this.moveTargetPos.diffence2D(this.curPosition).normalize();
			if (!moveDir.equals(this.curRotation))
			{
				this.onMoveMent((int) this.getFightProp(EPROPID_SPEED), moveDir.toVector3F(), this.curPosition, moveTargetPos, timeTick);
				this.preMoveTime = logicTime;
			}
			else
				this.updateMovePosition(logicTime);
		}
	}
	
	boolean onMoveMent(int speed, SBean.Vector3F rotation, GVector3 pos, GVector3 targetPos, SBean.TimeTick timeTick)
	{
		if(!super.onMoveMent(speed, rotation, pos, targetPos, timeTick))
			return false;

		this.serverStopTime = 0;
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_blur(this.id, this.getLogicPosition(), this.moveSpeed, this.curRotation.toVector3F(), this.moveTargetPos.toVector3(), timeTick));
		
		return true;
	}
	
	void notifySelfMove()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_blur(this.id, this.getLogicPosition(), this.moveSpeed, this.curRotation.toVector3F(), this.moveTargetPos.toVector3(), ms.getMapManager().getTimeTickDeep()));
	}
	
	boolean changeMapGrid(MapGrid grid)
	{
		if (grid != this.curMapGrid)
		{
			this.curMapGrid.delBlur(this);
			grid.addBlur(this);
			return true;
		}
		return false;
	}

	void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
//		this.setNewPosition(position);
		this.onStopMoveImpl(position, timeTick, broadcast);
	}
	
	void onStopMoveImpl(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.removeState(Behavior.EBMOVE);
		this.removeState(Behavior.EBRETREAT);
		this.forceFollow = false;
		this.moveSpeed = 0;
		this.moveTarget = null;
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_blur(this.getID(), this.getLogicPosition(), timeTick));
	}
	
	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterDetail> blurs = new ArrayList<>();
			blurs.add(this.getEnterDetail());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_blurs(blurs));
		}
	}
	
	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> lst = new ArrayList<>();
			lst.add(this.id);
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_blurs(lst));
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_blur(this.id, destory));
		}
	}

	void notifyUpdateHp(Set<Integer> rids)
	{
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_updatehp(this.id, this.curHP));
	}
	
//	void notifyBuffDamage(int rid, int attackerType, SBean.TimeTick timeTick)
//	{
//		ms.getRPCManager().sendStrPacket(rid, new SBean.nearby_blur_buffdamage(this.id, this.curHP, attackerType, timeTick));
//	}
	
	void notifyBuffDamage(Set<Integer> rids, int attackerType, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_buffdamage(this.id, this.curHP, attackerType, timeTick));
	}

	void notifySelfGetDamage(Set<Integer> rids, BaseRole attacker, int ownerID, SBean.DamageResult res, int skillID, int curDamageEventID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_ondamage(this.getID(), attacker.getID(), attacker.getEntityType(), ownerID, skillID, curDamageEventID, this.getCurHP(), res.dodge, res.deflect, res.crit, res.suckBlood, res.behead, res.remit, timeTick));
	}

	void notifySelfReduce(Set<Integer> rids, int reduce)
	{

	}

//	void notifySelfBeHead(Set<Integer> rids)
//	{
//		if (!rids.isEmpty())
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_behead(this.id));
//	}

	void onSelfDead(Set<Integer> rids, int killerID)
	{
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_dead(this.id, killerID));
	}

	void notifyAddBuff(Set<Integer> rids, int buffID, int realmLvl, int remainTime, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_addbuff(this.getID(), buffID, realmLvl, remainTime, timeTick));
	}

	void notifyRemoveBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_removebuff(this.getID(), buffID, timeTick));
	}

	void notifySelfShiftEnd(Set<Integer> rids, int skillID, GVector3 endpos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_shiftend_blur(skillID, this.getID(), endpos.toVector3(), timeTick));
	}

	void notifySelfRushStart(Set<Integer> rids, int skillID, SBean.Vector3 endPos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_rushstart(this.id, skillID, endPos, timeTick));
	}

	void addEbataunt(Buff buff, SBean.TimeTick timeTick)
	{
		this.forceTarget = new MapEntity(buff.attackerType, buff.attackerID, buff.attackOwnerID);
		BaseRole entity = this.getForceTarget();
		if (entity != null)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_addataunt_blur(this.getID(), buff.attackerID, buff.attackerType, buff.attackOwnerID, timeTick));
		}
	}

	void onCreateHandle()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfEnterNearBy(rids);
	}

	void onGetDamageHandler(BaseRole attacker, int damage)
	{
		if (attacker.owner != null && this.owner.effectPKState(attacker.owner))
			attacker.owner.resetPKState(this.owner.getNameColor() == GameData.NAME_COLOR_RED);
	}

	void onDeadHandle(int attackerType, int attackerID)
	{
		super.onDeadHandle(attackerType, attackerID);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfDead(rids, attackerID);
		
		this.owner.onSelfBlurDead(this);
		this.curMap.delBlurToRemoveCache(this.id);
	}

	//基础属性
	void updateBaseProps()
	{
		SBean.BlurCFGS cfg = GameData.getInstance().getBlurCFGS(this.configID);
		if (cfg == null)
			return;

		int lvl = this.level - 1;
		if (lvl < 0)
			lvl = 0;

		if (lvl >= cfg.maxHPs.size() - 1)
			lvl = cfg.maxHPs.size() - 1;

		int addMaxHp = 0;
		if (this.owner.propRole.getCurSpirits().contains(cfg.addMaxHpSpirit))
		{
			SBean.DBSpirit dbSpirit = this.owner.propRole.getSpirit(cfg.addMaxHpSpirit);
			if (dbSpirit != null)
			{
				int lay = dbSpirit.level / GameData.SPIRIT_LEVLE_PERLAY;
				if (lay >= 0 && lay < cfg.addHps.size())
					addMaxHp = cfg.addHps.get(lay);
			}
		}

		this.getPropBase().addFightPropFixValue(EPROPID_MAXHP, cfg.maxHPs.get(lvl) + addMaxHp);
		this.getPropBase().addFightPropFixValue(EPROPID_ACRN, cfg.acrNs.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_DEFN, cfg.defNs.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_ATR, cfg.atrs.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_CTR, cfg.ctrs.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_ACRN, cfg.acrNs.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_TOU, cfg.tous.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_ATKA, cfg.atkAs.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_ATKH, cfg.atkHs.get(lvl));
		this.getPropBase().addFightPropFixValue(EPROPID_SPEED, this.speed);
	}

	private void resetFightSkills()
	{
		this.fightSkills.clear();
		SBean.BlurCFGS blurCfg = GameData.getInstance().getBlurCFGS(this.getConfigID());
		if (blurCfg == null)
			return;

		for (Integer sid : blurCfg.attacks)
		{
			SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(sid);
			if (sCfg != null)
			{
				FightSkill skill = new FightSkill(sid, 1, 0, 0, Skill.eSG_Attack, 1, GameData.SKILL_USE_TYPE_ATTACK).copySkillData();
				if (skill != null)
					this.fightSkills.put(sid, skill);
			}
		}

		for (Integer sid : blurCfg.skills)
		{
			SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(sid);
			if (sCfg != null)
			{
				FightSkill skill = new FightSkill(sid, 1, 0, 0, Skill.eSG_Skill, 1, GameData.SKILL_USE_TYPE_CUR).copySkillData();
				if (skill != null)
					this.fightSkills.put(sid, skill);
			}
		}
	}

	void setCurSkillID()
	{
		if (this.curSkillID != null || this.attackList.isEmpty())
			return;

		SBean.BlurCFGS cfg = GameData.getInstance().getBlurCFGS(this.configID);
		if (cfg != null)
		{
			int attackNum = this.attackList.get(this.curAttackSeq);
			int rand = 0;
			if (attackNum == 0)
			{
				rand = GameRandom.getRandom().nextInt(cfg.attacks.size());
				this.curSkillID = cfg.attacks.get(rand);
			}
			else
			{
				if (attackNum <= cfg.skills.size())
					this.curSkillID = cfg.skills.get(attackNum - 1);
			}

			if (this.curSkillID == null || this.curSkillID == 0)
			{
				this.nextSkill();
				return;
			}

			this.setAttackRange();
		}
	}

	void nextSkill()
	{
		this.curAttackSeq++;
		if (this.curAttackSeq >= this.attackList.size())
			this.curAttackSeq = 0;

		this.curSkillID = null;
		this.setCurSkillID();
	}

	void onTrigChildSkill(int mainSkill, int skillID)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_usechildskill(this.id, mainSkill, skillID));
	}
	
	void onSelfTrigSkill(Set<Integer> rids, int skillID)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_usetrigskill(this.id, skillID));
	}

	boolean onUseSkill(int skillID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		FightSkill fSkill = this.fightSkills.get(skillID);
		if (fSkill == null)
			return false;

		if (!this.canUseSkill(fSkill))
			return false;

		this.forceTarget = new MapEntity(targetType, targetID, ownerID);
		BaseRole entity = this.getForceTarget();
		if (entity != null && entity != this)
			this.setCurRotation(entity.curPosition.diffence2D(this.getCurPosition()).normalize());

		long now = ms.getMapManager().getMapLogicTime();
		Skill skill = this.createNewSkill(fSkill.baseDataCfg.common, fSkill.baseDataCfg.fix, fSkill.lvlFixCfg, fSkill.level, fSkill.realmLvl, fSkill.skillGroup, now);
		if (skill == null)
			return false;

		this.attack = skill;
		this.curUseSkillAddCache.add(skill);
		fSkill.coolDownTime = now + (long) skill.lvlFixCfg.cool;
		if (skill.baseCommonCfg.canAttack == 0)
			this.addState(Behavior.EBDISATTACK);

		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_useskill(this.getID(), skillID, targetID, targetType, ownerID, this.getLogicPosition(), this.getCurRotation().toVector3F(), timeTick));
		return true;
	}
	
	void onEndSkillHandle(Skill skill)
	{
		if (skill.baseCommonCfg.canAttack == 0)
			this.curStates.remove(Behavior.EBDISATTACK);
		
		this.notifyEndSkill(skill);
	}
	
	void notifyEndSkill(Skill skill)
	{
		super.notifyEndSkill(skill);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_endskill(this.id, skill.id, ms.getMapManager().getTimeTickDeep()));
	}
	
	void notifyFinishAttack(Skill skill)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_finishattack(this.id, skill.id, ms.getMapManager().getTimeTickDeep()));
	}
	
	void notifyBreakSkill()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_blur_breakskill(this.id));
	}
	
	boolean checkCanBeAttack(BaseRole attacker)
	{
		return this.owner.checkCanBeAttack(attacker);
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
			List<BaseRole> friends = this.getRoleFriendEntityNearBy(this.owner);
			for (BaseRole r : friends)
			{
				if (!r.isDead())
					targets.add(r);
			}
			targets.add(this.owner);
			break;
		case GameData.eSE_Damage:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());

			//中立单位
			List<Trap> trapsNearBy = this.getTrapNearBy();
			for (Trap t : trapsNearBy)
			{
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

	BaseRole getRoleInCheckRange()
	{
		BaseRole entity = null;
		Iterator<BaseRole> it = this.enmityList.iterator();
		while (it.hasNext())
		{
			BaseRole r = it.next();
			if (!this.checkTargetValid(r) || !this.curMap.checkBaseRoleCanAttack(this, r))
			{
				it.remove();
				continue;
			}

			return r;
		}

//		if (this.isAttacked())
//			entity = this.getPlayerEntity(this.owner, this.getRoleNearBy(this.owner), this.getPetNearBy(), this.getMonsterNearBy(), this.getBlurNearBy());

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
	
	long standTime;
	private boolean forceFollow;
	private int curAttackSeq;
	boolean buffDead;
}
