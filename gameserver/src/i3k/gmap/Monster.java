package i3k.gmap;

import i3k.SBean;
import i3k.gmap.BaseMap.ActivityMapCopy;
import i3k.gmap.BaseMap.ForceWarMap;
import i3k.gmap.BaseMap.SectGroupMap;
import i3k.gmap.BaseMap.SectMap;
import i3k.gmap.BaseMap.TeamMapCopy;
import i3k.gmap.BaseRole.ArmorManager;
import i3k.gmap.BaseRole.ExtraDropInfo;
import i3k.gmap.DropGoods.DropItem;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ket.util.Stream;

public class Monster extends BaseRole
{
	public Monster(int configID, MapServer ms)
	{
		super(ms, true);
		this.configID = configID;
	}

	public Monster createNew(GVector3 position, GVector3 rotation, boolean canRotation, int standByTime)
	{
		this.id = ms.getMapManager().getNextMonsterID().incrementAndGet();
		this.curPosition.reset(position);
		this.birthPosition.reset(position);
//		this.curRotation.reset(GVector3.UNIT_X);
		this.setCurRotation(rotation);
		this.canRotation = canRotation;

		SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(this.configID);
		if (monsterCfg != null)
		{
			if (standByTime < 0)
				this.standByTime = (long) standByTime;
			else
				this.standByTime = ms.getMapManager().getMapLogicTime() + (long) standByTime;
			this.level = monsterCfg.level;
			this.bossType = monsterCfg.bossType;
			this.bwType = monsterCfg.bwType;
			if(this.bwType > 0)
				this.forceType = this.bwType;
			
			this.ctrlType = ECTRL_TYPE_AI;
			this.entityType = GameData.ENTITY_TYPE_MONSTER;
			this.curHP = (int) monsterCfg.maxHP;
			this.configActive = monsterCfg.isActive == 1;
			this.race = monsterCfg.race;
			this.patrolInterval = monsterCfg.patrolInterval;
			this.patrolRadius = monsterCfg.patrolRadius;
			this.patrolSpeed = monsterCfg.patrolSpeed;
			this.maxTraceRange = monsterCfg.maxTraceRange;
			this.setMonsterCheckCenter(position);
			for (Integer sid : monsterCfg.attacks)
			{
				SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(sid);
				if (sCfg != null)
				{
					FightSkill skill = new FightSkill(sid, sCfg.baseData.common.maxLvl, 0, 0, Skill.eSG_Attack, 1, GameData.SKILL_USE_TYPE_ATTACK).copySkillData();
					if (skill != null)
						this.fightSkills.put(sid, skill);
				}
			}
			monsterCfg.skills.stream().filter(mskill -> mskill.id > 0).forEach(mskill ->
			{
				SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(mskill.id);
				if (sCfg != null)
				{
					FightSkill skill = new FightSkill(mskill.id, mskill.lvl, 0, 0, Skill.eSG_Skill, 1, GameData.SKILL_USE_TYPE_CUR).copySkillData();
					if (skill != null)
						this.fightSkills.put(skill.id, skill);
				}

			});

			//添加永久状态
			monsterCfg.foreverState.forEach(this::addState);

			this.setPropBase(new PropFightRole());
			this.getPropBase().addFightPropFixValue(EPROPID_MAXHP, monsterCfg.maxHP);
			this.getPropBase().addFightPropFixValue(EPROPID_ATKN, monsterCfg.atkN);
			this.getPropBase().addFightPropFixValue(EPROPID_DEFN, monsterCfg.defN);
			this.getPropBase().addFightPropFixValue(EPROPID_ATR, monsterCfg.atr);
			this.getPropBase().addFightPropFixValue(EPROPID_CTR, monsterCfg.ctr);
			this.getPropBase().addFightPropFixValue(EPROPID_ACRN, monsterCfg.acrN);
			this.getPropBase().addFightPropFixValue(EPROPID_TOU, monsterCfg.tou);
			this.getPropBase().addFightPropFixValue(EPROPID_ATKA, monsterCfg.atkA);
			this.getPropBase().addFightPropFixValue(EPROPID_ATKC, monsterCfg.atkC);
			this.getPropBase().addFightPropFixValue(EPROPID_DEFC, monsterCfg.defC);
			this.getPropBase().addFightPropFixValue(EPROPID_ATKW, monsterCfg.atkW);
			this.getPropBase().addFightPropFixValue(EPROPID_DEFW, monsterCfg.defW);
			this.getPropBase().addFightPropFixValue(EPROPID_SPEED, monsterCfg.speed);
			if(monsterCfg.armorMaxVal > 0)
			{
				this.getPropBase().addFightPropFixValue(EPROPID_ARMMAXHP, monsterCfg.armorMaxVal);
				this.maxArmorValue = monsterCfg.armorMaxVal;
				this.armorMgr = new ArmorManager(monsterCfg.armorMaxVal);
			}
			
			if(monsterCfg.armorTransVal > 0)
				this.getPropBase().addFightPropFixValue(EPROPID_ARMTRF, monsterCfg.armorTransVal);
			
			this.armorID = monsterCfg.armorID;
			this.armorTransRate = monsterCfg.armorTransRate;
			this.armorDmgDeep = monsterCfg.armorDmgDeep;
			
			if(!monsterCfg.birthBuffs.isEmpty())
				this.addBirthBuff(monsterCfg.birthBuffs);
			
			this.updateMaxHp();
			this.speed = monsterCfg.speed;
			this.monsterCheckRadius = monsterCfg.checkRange;
			this.radius = monsterCfg.radius;

			this.attackList = monsterCfg.attackList;
			if (!this.attackList.isEmpty())
				this.setCurSkillID();
			if (!monsterCfg.percentDrop.isEmpty())
				this.percentDrop = Stream.clone(monsterCfg.percentDrop);
			this.logDamage = monsterCfg.logDamage == 1;
		}
		return this;
	}

	private void addBirthBuff(List<Integer> birthBuffs)
	{
		for(int buffID: birthBuffs)
			this.addBuffByID(buffID, this, null);
	}
	
	private void onSecondTask(int timeTick)
	{
		if (timeTick > this.second)
		{
			this.autoBackBlood();
			this.second = timeTick;
		}
		if (this.logDamage && this.curHP <= this.maxHp * 0.2)
		{
			this.healHp();
		}
	}

	private void autoBackBlood()
	{
		if (!this.isInWorld())
			return;

		if ((this.checkState(Behavior.EBRETREAT) || this.checkState(Behavior.EBPATROL)) && this.curHP < this.getMaxHP() && this.monsterMapType == 0 && !this.curMap.isFightMap())
		{
			int add = (int) (this.getMaxHP() * GameData.getInstance().getCommonCFG().skill.bloodrate);
			this.setCurHP(this.curHP + add);
			if (this.curHP == this.getMaxHP())
				this.damageRoles.clear();

			Set<Integer> rids = this.getRoleIDsNearBy(null);
			this.notifyUpdateHp(rids);
		}
	}
	
	void healHp()
	{
		this.setCurHP(this.maxHp);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.notifyUpdateHp(rids);
	}
	
	void onMilliSecondTask(long logicTime)
	{
		super.onMilliSecondTask(logicTime);
		this.checkStandTime(logicTime);
		if(this.armorMgr != null)
			this.onCheckArmor(logicTime);
		this.lastLogicTime = logicTime;
	}
	
	public void setCurRotation(GVector3 curRotation)
	{
		if ((curRotation.x == 0 && curRotation.z == 0) || !canRotation)
			return;
		this.curRotation.reset(curRotation.normalize());
	}
	
	void updateBuffProps()
	{
		super.updateBuffProps();
		if(this.moveSpeed > 0 && this.moveSpeed != this.getFightProp(BaseRole.EPROPID_SPEED) && !this.checkState(Behavior.EBPATROL))
			this.onStopMove(this.curPosition, ms.getMapManager().getTimeTickDeep(), true);
	} 
	
	void onAutoFight(long logicTime)
	{
		this.checkFear(logicTime);

		if (this.curFear != null)
		{
			if (this.addState(Behavior.EBMOVE))
				this.processMove(logicTime);
			return;
		}
		
		this.refreshCurUseSkills();
		if(this.curUseSkills.size() > 0)
			this.onAutoProcessDamage(logicTime);
		
		if(!this.checkState(Behavior.EBATTACK))
		{
			if (!isMoving())
				this.moveToNearest(logicTime);
			else
				this.onMoving(logicTime);

			if (logicTime - collisionTime >= 500)
			{
				//this.collision();
				collisionTime = logicTime;
			}
		}
	}

	boolean onTimer(int timeTick, long logicTime)
	{
		if (this.curMap == null || this.curMapGrid == null)
			return true;

		if (this.curMap.isTimeOut() || this.curMap.isMapAlreadyFinish || this.isDead() || this.curMap.isInPrepared(logicTime))
			return true;

		if (this.isInProtectTime())
			return false;

		this.onSecondTask(timeTick);
		this.onMilliSecondTask(logicTime);

		return this.isDead() || (this.standByTime >= 0 && logicTime > this.standByTime);
	}

	void collision()
	{
		int gridX = this.getCurMapGrid().getGridX();
		int gridZ = this.getCurMapGrid().getGridZ();
		List<Monster> monsterAround = this.curMap.getMonsterAround(gridX, gridZ, this);
		monsterAround.stream().filter(m -> !m.isDead()).forEach(m -> this.solveCollsion(m, this));
	}

	void solveCollsion(Monster m1, Monster m2)
	{
		float minDist = m1.getRadius() + m2.getRadius();
		GVector3 p1 = m1.getCurPosition();
		GVector3 p2 = m2.getCurPosition();
		GVector3 p3 = p1.sum(p2);
		float tempDist = p1.distance(p2);
		if (tempDist < minDist)
		{
			float angle = (float) Math.atan2(p3.z, p3.x);
			float distance = minDist - tempDist + 1.0f;
			float distance1 = 0.5f * distance;
			float distance2 = distance - distance1;
			m1.setCurPosition(this.vec3FRotateByAngle(p1.sum(new GVector3(distance1, 0.0f, 0.0f)), p1, angle));
			m2.setCurPosition(this.vec3FRotateByAngle(p2.sum(new GVector3(-distance2, 0.0f, 0.0f)), p2, angle));
		}
	}
	
	public int setCurHP(int curHP)
	{
		if(this.curHP == curHP)
			return 0;
		
		super.setCurHP(curHP);
		if(this.curMap instanceof ForceWarMap)
		{
			ForceWarMap fwm = ForceWarMap.class.cast(this.curMap);
			fwm.updateStatues(this);
		}
		
		towerDefencePop();
		return this.curHP;
	}
	
	private void towerDefencePop()
	{
		float curPercent = (float)(this.curHP) / this.getMaxHP();
		if(this.curHP > 0 && this.towerDefencePops != null)
		{
			int percent = 0;
			if(!this.towerDefencePops.isEmpty())
			{
				Iterator<Float> it = this.towerDefencePops.iterator();
				while(it.hasNext())
				{
					float req = it.next();
					if(curPercent > req)
						break;
					
					it.remove();
					percent = (int) (req * 100);
				}
			}
			
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
			{
				if(percent > 0)
				{
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.towerdefence_npc_pop(this.getID(), percent));
				}
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.towerdefence_npc_info(this.curHP, this.getMaxHP()));
			}
		}
	}
	
	void moveToNearest(long logicTime)
	{
		if (this.curPosition.distance(this.getBirthPosition()) > this.maxTraceRange) //超出移动距离
		{
			if (!this.checkState(Behavior.EBRETREAT) && this.addState(Behavior.EBMOVE))
				this.moveToBirth(logicTime);
			return;
		}
		
		if(this.isAttacked() || (this.configActive && (logicTime - this.tryAttackTime) > 1000))
			this.tryAttack(logicTime);
		else			//随机走动(巡逻)
			this.onCheckPatrol(logicTime);
	}
	
	private void tryAttack(long logicTime)
	{
		this.tryAttackTime = logicTime;
		BaseRole entity = this.getRoleInCheckRange();
		if (entity != null && !this.attackList.isEmpty())
		{
			this.removeState(Behavior.EBPATROL);
			float range = this.attackRange + this.getRadius() + entity.getRadius();
			float distance = entity.getCurPosition().distance(this.getCurPosition());
			if ((distance <= this.monsterCheckRadius || range >= this.monsterCheckRadius) && (distance - range) <= 5) //攻击范围内
			{
				if(this.checkState(Behavior.EBDISATTACK))
					return;
				
				SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
				if (this.isMoving())
					this.onStopMove(this.curPosition, timeTick, true);

				if (this.checkState(Behavior.EBATTACK))
					return;

				if (this.stupidTime > logicTime)
					return;

				this.onUseSkill(logicTime, timeTick, entity);
				this.enmityList.add(entity);
				this.setAttacked(true);
			}
			else if(this.getFightProp(EPROPID_SPEED) > 0)
			//移动
			{
				if (this.addState(Behavior.EBMOVE))
				{
					this.moveSpeed = this.getFightProp(EPROPID_SPEED);
					GVector3 dir = this.curPosition.diffence2D(entity.curPosition).normalize();
					this.moveTargetRealPos.reset(entity.curPosition);
					if (distance - range < range / 4.f)
						this.moveTargetPos = entity.curPosition.sum(dir.scale(range));
					else
						this.moveTargetPos = this.getRandomTargetPos(entity.curPosition, dir, range, GameData.getInstance().getPathAngel(range));

					this.moveTarget = entity;
					this.startMove(logicTime);
				}
			}
		}
		else		//随机走动(巡逻)
			this.onCheckPatrol(logicTime);
	}
	
	void onCheckPatrol(long logicTime)
	{
		if (this.patrolSpeed > 0 && this.getFightProp(EPROPID_SPEED) > 0 && this.patrolRadius > 0 && this.patrolInterval != null)
		{
			int rand = GameRandom.getRandInt(0, this.patrolInterval.size());
			if (logicTime - this.idleTime > this.patrolInterval.get(rand))
			{
				idleTime = logicTime;
				if (!this.checkHasRoleNearBy(1, IDLE_VALID_DISTANCE) && !configActive && this.curHP == this.getMaxHP())
					return;
				
				if (this.addState(Behavior.EBPATROL))
					this.onPatrol(logicTime);
			}
		}
	}
	
	void startMove(long logicTime)
	{
		this.moveTickLine = logicTime;
		this.preMoveTime = logicTime;

		this.setCurRotation(this.moveTargetPos.diffence2D(this.getCurPosition()).normalize());
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
		{
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_monster(this.getID(), this.getLogicPosition(), this.moveSpeed, this.curRotation.toVector3F(), this.moveTargetPos.toVector3(), ms.getMapManager().getTimeTickDeep()));
//			if(moveTargetPos.equals(this.curPosition))
//				ms.getLogger().debug("@@@@@@@@nearby_move_monster " + this.id + " , " + this.configID + " speed " + this.moveSpeed + " curPosition " + this.curPosition + " targetPos " + this.moveTargetPos + " rids " + rids);
		}
	}
	
	boolean checkCanBeAttack(BaseRole attacker)
	{
		if(this.getMapType() == GameData.MAP_TYPE_MAP_WORLD && this.getSectId() == attacker.getSectId())
			return false;
		
		if(!canBeSeen(attacker.owner))
			return false;
		
		return true;
	}
	
	boolean canBeSeen(MapRole role)
	{		
		if(this.spawnRole > 0 && (role == null || this.spawnRole != role.id))
			return false;
		
		return true;
	}
	
	int getMonsterSpawnRole()
	{
		return this.spawnRole;
	}
	
	BaseRole getRoleInCheckRange()
	{
		BaseRole entity = null;
		float distance = 0;
//		float alterRange = this.maxTraceRange;
		Iterator<BaseRole> it = this.enmityList.iterator();
		while (it.hasNext())
		{
			BaseRole r = it.next();
			if (!this.checkTargetValid(r) /* || distance > alterRange */)
			{
				it.remove();
				continue;
			}

			return r;
		}

		this.setAttacked(false);
		if(!this.configActive)
			return null;
		
		float checkRange = this.monsterCheckRadius + this.getRadius();
		float minDistance = GameData.MAX_NUMBER;
		for (BaseRole r : this.getEnemiesNearBy())
		{
			distance = r.getCurPosition().distance(this.getCurPosition());
			if (distance < checkRange && !r.isInProtectTime() && this.curMap.checkBaseRoleCanAttack(this, r))
			{
				if (distance < minDistance)
				{
					minDistance = distance;
					entity = r;
				}
			}
		}
		
		return entity;
	}
	
	void onPatrol(long logicTime)
	{
		this.moveSpeed = this.patrolSpeed;
		GVector3 dirBToM = this.curPosition.diffence2D(this.getBirthPosition()).normalize();
		//GVector3 dirBToM = this.getBirthPosition().diffence2D(this.curPosition).normalize();
		this.moveTargetPos = this.getRandomTargetPos(this.getBirthPosition(), dirBToM, this.patrolRadius, (float) Math.PI);
		this.moveTarget = null;
		this.setAttacked(false);

		this.moveTickLine = logicTime;
		this.preMoveTime = logicTime;

		this.startMove(logicTime);
	}

	void moveToBirth(long logicTime)
	{	
		int speed = this.getFightProp(EPROPID_SPEED);
		if (!this.addState(Behavior.EBRETREAT) || speed <= 0)
			return;
		
		if(this.curFear != null)
			this.curFear = null;
		this.clearAllBuff();
		this.onUpdateCurBuff(logicTime);
		
		this.moveSpeed = speed;
		GVector3 dirCToM = this.getBirthPosition().diffence2D(this.curPosition).normalize();
		int range = this.attackRange + this.getRadius();
		this.moveTargetPos = this.getRandomTargetPos(this.getBirthPosition(), dirCToM, range, (float) Math.PI / 3.f);
		this.moveTarget = null;
		this.setAttacked(false);
		this.enmityList.clear();

		this.startMove(logicTime);
	}

	void onMoving(long logicTime)
	{
		//		BaseRole role = this.getRoleInCheckRange();
		//		if(role != null && (this.isAttacked() || this.configActive) && this.attackList.size() > 0)
		//		{
		//			float range = this.attackRange + this.getRadius() + role.getRadius();
		//			float distance = role.getCurPosition().distance(this.getCurPosition());
		//			if(distance < this.monsterCheckRadius && distance < range)		//攻击范围内
		//			{
		//				this.onStopMove(this.curPosition, ms.getMapManager().getTimeTick());
		//				this.nearestTarget = role;
		//				this.onUseSkill(logicTime, ms.getMapManager().getTimeTick());
		//				return;
		//			}
		//		}

		this.processMove(logicTime);
	}

	boolean randMoveTarget()
	{
		return true;
	}
	
	void processMove(long logicTime)
	{
		this.updateMoveTarget();
		SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
		if (this.curPosition.distance(this.moveTargetPos) <= 5)
		{
			if (this.moveTarget != null && (this.isAttacked() || this.configActive) && !this.attackList.isEmpty())
			{
				BaseRole entity = this.moveTarget;
				float range = this.attackRange + this.getRadius() + entity.getRadius();
				float distance = entity.curPosition.distance(this.curPosition);
				if (distance < (this.monsterCheckRadius + this.getRadius() + entity.getRadius()) && Math.abs(distance - range) < 5) //攻击范围内
				{
					if(this.isMoving())
						this.onStopMove(this.curPosition, timeTick, true);
					
					if (this.stupidTime > logicTime)
						return;
					
					this.onUseSkill(logicTime, timeTick, entity);
				}
				else
					this.moveToNearest(logicTime);
			}
			else
				this.onStopMove(this.curPosition, timeTick, true);
		}
		else
		{
			float length = (this.moveSpeed * (logicTime - this.lastLogicTime) / 1000.0f);
			GVector3 moveDir = this.moveTargetPos.diffence2D(this.curPosition).normalize();
			GVector3 newPosition = this.curPosition.sum(moveDir.scale(length));
			this.fixNewPosition(moveDir, newPosition, this.moveTargetPos);
			if (newPosition.distance(this.getBirthPosition()) > this.maxTraceRange) //超出追踪半径
			{
				if (this.curFear != null)
				{
					int range = this.attackRange + this.getRadius();
					GVector3 targetPos = this.getRandomTargetPos(this.getBirthPosition(), moveDir, range, (float) Math.PI / 3.f);
					this.curFear.forceSetTargetPos(targetPos);
					this.moveTargetPos = targetPos;
				}

				if (!this.checkState(Behavior.EBRETREAT) && this.addState(Behavior.EBMOVE))
				{
					this.moveToBirth(logicTime);
					return;
				}
			}
			int curGridX = this.curMapGrid.getGridX();
			int curGridZ = this.curMapGrid.getGridZ();
			int newGridX = this.curMap.calcGridCoordinateX((int) newPosition.x);
			int newGridZ = this.curMap.calcGridCoordinateZ((int) newPosition.z);
			MapGrid newGrid = this.curMap.getGrid(newGridX, newGridZ);
			if (newGrid != null)
			{
				boolean change = this.changeMapGrid(newGrid);
				if (change)
					this.changePosition(curGridX, curGridZ, newGridX, newGridZ, 0);

				this.setCurPosition(newPosition);
				this.setCurRotation(moveDir);
				if (logicTime - this.preMoveTime >= MapManager.PROTOCOL_AUTOSEND_INTERVAL && this.moveSpeed > 0)
				{
					if(this.curPosition.equals(this.moveTargetPos))
						this.onStopMove(this.curPosition, timeTick, true);
					else
						this.startMove(logicTime);
				}	
			}
			else	//超出地图
				this.onStopMove(this.curPosition, timeTick, true);
		}
	}

	void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.onStopMoveImpl(position, timeTick, broadcast);
	}

	void onStopMoveImpl(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.removeState(Behavior.EBMOVE);
		if (this.checkState(Behavior.EBRETREAT))
		{
			this.removeState(Behavior.EBRETREAT);
			this.addState(Behavior.EBPATROL);
		}

		long logicTime = ms.getMapManager().getMapLogicTime();
		this.moveTickLine = logicTime;
		this.moveSpeed = 0;
		this.idleTime = logicTime;
		this.moveTarget = null;
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_monster(this.getID(), position.toVector3(), timeTick));
	}
	
	void onTrigChildSkill(int mainSkill, int skillID)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_usechildskill(this.id, mainSkill, skillID));
	}
	
	void onSelfTrigSkill(Set<Integer> rids, int skillID)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_usetrigskill(this.id, skillID));
	}

	void onUseSkill(long logicTime, SBean.TimeTick timeTick, BaseRole target)
	{
		if (this.curSkillID == null)
		{
			this.nextSkill();
			return;
		}

		if ((!this.isAttacked && !this.configActive) || this.attackList.isEmpty())
			return;

		if (this.isInProtectTime())
			return;

		FightSkill fSkill = this.fightSkills.get(this.curSkillID);
		if (fSkill == null)
			return;

		if (!this.canUseSkill(fSkill))
		{
			this.removeState(Behavior.EBATTACK);
			this.nextSkill();
			return;
		}
		
		int targetID = 0;
		int targetType = 0;
		int ownerID = 0;

		if (target != null)
		{
			targetID = target.getID();
			targetType = target.getEntityType();
			ownerID = target.owner == null ? 0 : target.owner.getID();
			this.setCurRotation(target.getCurPosition().diffence2D(this.getCurPosition()).normalize());
		}
		Skill skill = this.createNewSkill(fSkill.baseDataCfg.common, fSkill.baseDataCfg.fix, fSkill.lvlFixCfg, fSkill.level, fSkill.realmLvl, Skill.eSG_TriSkill, logicTime);
		if (skill == null)
		{
			this.removeState(Behavior.EBATTACK);
			return;
		}

		skill.target = target;
		this.attack = skill;
		this.curUseSkillAddCache.add(skill);
//		fSkill.coolDownTime = logicTime + (long) (skill.lvlFixCfg.cool * fSkill.coolDownPercent / 100.0f);
		if (skill.baseCommonCfg.canAttack == 0)
			this.addState(Behavior.EBDISATTACK);

		int skillID = skill.id;
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_useskill(this.getID(), skillID, targetID, targetType, ownerID, this.getLogicPosition(), this.getCurRotation().toVector3F(), timeTick));
	}

	void onEndSkillHandle(Skill skill)
	{
		if (skill.baseCommonCfg.canAttack == 0)
			this.curStates.remove(Behavior.EBDISATTACK);

		long now = ms.getMapManager().getMapLogicTime();
		this.stupidTime = now + GameRandom.getRandInt(GameData.getInstance().getCommonCFG().skill.stupidMin, GameData.getInstance().getCommonCFG().skill.stupidMax);
		this.notifyEndSkill(skill);
	}

	void notifyEndSkill(Skill skill)
	{
		super.notifyEndSkill(skill);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_endskill(this.id, skill.id, ms.getMapManager().getTimeTickDeep()));
	}
	
	void notifyFinishAttack(Skill skill)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(skill.slashInfo != null && skill.damageTick < skill.damageCount)
			rids.add(this.id);
		
		long now = ms.getMapManager().getMapLogicTime();
		this.stupidTime = now + GameRandom.getRandInt(GameData.getInstance().getCommonCFG().skill.stupidMin, GameData.getInstance().getCommonCFG().skill.stupidMax);
		
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_finishattack(this.id, skill.id, ms.getMapManager().getTimeTickDeep()));
	}
	
	void notifyBreakSkill()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_breakskill(this.id));
	}
	
	private void setCurSkillID()
	{
		if (this.curSkillID != null || this.attackList.isEmpty())
			return;

		SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(this.configID);
		if (monsterCfg != null)
		{
			int attackNum = this.attackList.get(this.curAttackSeq);
			int rand = 0;
			if (attackNum == 0)
			{
				rand = GameRandom.getRandom().nextInt(monsterCfg.attacks.size());
				this.curSkillID = monsterCfg.attacks.get(rand);
			}
			else
			{
				if (attackNum <= monsterCfg.skills.size())
					this.curSkillID = monsterCfg.skills.get(attackNum - 1).id;
			}

			if (this.curSkillID == null || this.curSkillID == 0)
			{
				this.nextSkill();
				return;
			}

			this.setAttackRange();
		}
	}

	private void checkStandTime(long logicTime)
	{
		if (!this.isDead() && this.standByTime > 0 && logicTime > this.standByTime)
		{
//			Set<Integer> rids = this.getRoleIDsNearBy(null);
//			this.onSelfLeaveNearBy(rids, 1);

			this.onDeadHandle(0, 0);
		}
	}

	void onGetDamageHandler(BaseRole attacker, int damage)
	{
		if (attacker.getEntityType() == GameData.ENTITY_TYPE_SKILL)
			this.addEnemy(attacker.owner);
		else
			this.addEnemy(attacker);

		if (this.isMoving())
			this.onStopMove(this.curPosition, ms.getMapManager().getTimeTickDeep(), true);
		
		if(damage > 0)
			this.onSpa(damage);
		
		this.tryUpdateDamageRank(attacker.getEntityType() == GameData.ENTITY_TYPE_SKILL ? GameData.ENTITY_TYPE_PLAYER : attacker.getEntityType(), attacker.getEntityType() == GameData.ENTITY_TYPE_SKILL ? attacker.owner.getID() : attacker.getID(), damage);
		this.updateDamage(damage, attacker.owner == null ? 0 : attacker.owner.getID(), attacker.getName(), attacker.getClassType());
	}
	
	private void tryUpdateDamageRank(int entityType, int entityId, int damage)
	{
		if (damage <= 0)
			return;
		if (entityType != GameData.ENTITY_TYPE_PET && entityType != GameData.ENTITY_TYPE_PLAYER)
			return;
		if (this.getMapType() == GameData.MAP_TYPE_MAPCOPY_ACTIVITY)
		{
			if(this.curMap instanceof ActivityMapCopy)
			{
				ActivityMapCopy am = ActivityMapCopy.class.cast(this.curMap);
				am.onMonsterGetDamage(entityType, entityId, damage);
			}
		}
		else if (this.getMapType() == GameData.MAP_TYPE_MAPCOPY_NORMAL)
		{
			if(this.curMap instanceof TeamMapCopy)
			{
				TeamMapCopy tm = TeamMapCopy.class.cast(this.curMap);
				tm.onMonsterGetDamage(entityId, damage);
			}
		}
		else if (this.getMapType() == GameData.MAP_TYPE_MAPCOPY_SECT)
		{
			if(this.curMap instanceof SectMap)
			{
				SectMap sm = SectMap.class.cast(this.curMap);
				sm.onMonsterGetDamage(entityType, entityId, damage);
			}
		}
	}

	private void tryPercentDrop(MapRole attacker)
	{
		if (attacker == null)
			return;
		Iterator<Entry<Integer, SBean.PercentDropCFGS>> percents = percentDrop.entrySet().iterator();
		while (percents.hasNext())
		{
			Entry<Integer, SBean.PercentDropCFGS> drop = percents.next();
			if (this.curHP * 10000l / this.maxHp < drop.getKey())
			{
				SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(this.getConfigID());
				List<MapRole> membersNearBy = this.getTeamMemberNearBy(attacker, GameData.getInstance().getCommonCFG().team.expAddDistance);

				membersNearBy.add(attacker);
				if (monsterCfg != null)
				{
					SBean.DropRatio dropRatio = GameData.getMonsterDoubleDropRatio(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentDoubleDropConfig());
					SBean.ExtraDropTbl extraDropTbl = GameData.getMonsterExtraDropTable(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentExtralDropConfig());
					for (MapRole member : membersNearBy)
					{
						int weaponDropRatio = (member.id == attacker.id) ? attacker.getWeaponMultipleDrop() : 0;
						int fixedDropRatio = dropRatio.fixedDrop + weaponDropRatio;
						int randomDropRatio = dropRatio.randomDrop + weaponDropRatio;
						List<DropItem> dropItems = member.curMap.getDropItemList(member.id, 0, drop.getValue().randomDropId, drop.getValue().randomDropTimes, fixedDropRatio, randomDropRatio, extraDropTbl, this.getLogicPosition(), this.getConfigID(), this.getEntityType(), member.canTakeDrop);
						if (dropItems.size() > 0)
						{
							List<SBean.DropInfo> dropInfo = new ArrayList<>();
							for (DropItem d : dropItems)
								dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));
							ms.getRPCManager().sendStrPacket(member.getID(), new SBean.role_sync_drops(this.getLogicPosition(), dropInfo));
						}
					}
				}
				percents.remove();
			}
		}
		if (this.percentDrop.isEmpty())
			this.percentDrop = null;
	}
	
	public void trySpecialDrop(MapRole attacker, ExtraDropInfo extraDropInfo)
	{
		if (extraDropInfo == null)
			return;
		SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(this.getConfigID());
		if (monsterCfg == null)
			return;
		switch(extraDropInfo.monsterType)
		{
		case GameData.EXTRA_DROP_MONSTER_TYPE_NORMAL:
			if (monsterCfg.bossType != GameData.MONSTER_BOSSTYPE_COMMON)
				return;
			break;
		case GameData.EXTRA_DROP_MONSTER_TYPE_BOSS:
			if (monsterCfg.bossType != GameData.MONSTER_BOSSTYPE_NORMALBOSS && monsterCfg.bossType != GameData.MONSTER_BOSSTYPE_FINALBOSS)
				return;
			break;
		case GameData.EXTRA_DROP_MONSTER_TYPE_ALL:
			break;
		case GameData.EXTRA_DROP_MONSTER_TYPE_ID:
			if (!extraDropInfo.monsterId.contains(this.getConfigID()))
				return;
			break;
		default:
			return;
		}
		
		List<MapRole> membersNearBy;
		if (extraDropInfo.teamShare == 1)
			membersNearBy = this.getTeamMemberNearBy(attacker, GameData.getInstance().getCommonCFG().team.expAddDistance);
		else
			membersNearBy = new ArrayList<MapRole>();

		membersNearBy.add(attacker);
		for (MapRole member : membersNearBy)
		{
			List<DropItem> dropItems = member.curMap.getDropItemList(member.id, 0, extraDropInfo.dropId, extraDropInfo.dropTime, 1, 1, null, this.getLogicPosition(), this.getConfigID(), this.getEntityType(), member.canTakeDrop);
			if (dropItems.size() > 0)
			{
				List<SBean.DropInfo> dropInfo = new ArrayList<>();
				for (DropItem d : dropItems)
					dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));
				ms.getRPCManager().sendStrPacket(member.getID(), new SBean.role_sync_drops(this.getLogicPosition(), dropInfo));
			}
		}
	}

	void onSpa(int damage)
	{
		SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(this.getConfigID());
		if (monsterCfg == null)
			return;
		SBean.CommonSkillCFGS commonSkillCfg = GameData.getInstance().getCommonCFG().skill;
		long endTime = ms.getMapManager().getMapLogicTime() + (long) commonSkillCfg.spa;
		int curHp = this.getCurHP();
		int maxHp = this.getMaxHP();
		float spa = (float) curHp / maxHp;
		if (curHp > 0 && maxHp > 0)
		{
			if ((spa < monsterCfg.spaHP))
			{
				if (GameData.checkRandom(monsterCfg.spaOdd) && this.addState(Behavior.EBSPASTICITY))
				{
					this.specialState.put(Behavior.EBSPASTICITY, endTime);
					Set<Integer> rids = this.getRoleIDsNearBy(null);
					if(!rids.isEmpty())
						ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_addspa_monster(this.id, ms.getMapManager().getTimeTickDeep()));
					this.breakSkill();
				}
			}
		}
	}

	void onGetBuffDamageHandler(Buff buff, int value)
	{
		if(value >= 0)
			return;

		this.tryUpdateDamageRank(buff.attackerType, buff.attackOwnerID, Math.abs(value));
		this.updateDamage(Math.abs(value), buff.attackOwnerID, buff.attackName, buff.attackClassType);
	}
	
	void updateDamage(int damage, int attackerID, String attackerName, int attackerClassType)
	{
		if(damage > 0)
		{
			float hpLostBP = (float) (this.getMaxHP() - this.curHP) / (float) this.getMaxHP() * 10000.0f;
			switch (this.monsterMapType)
			{
			case GameData.SECTMAP_MONSTER_PROGRESS:
				ms.getRPCManager().syncSectMapProgress(this.getMapID(), this.getMapInstanceID(), this.spawnPointID, damage, (int) hpLostBP);
				break;
			case GameData.SECTMAP_BOSS_PROGRESS:
				if (this.isDead())
					this.curMap.processMapFinish();
				ms.getRPCManager().syncSectMapProgress(this.getMapID(), this.getMapInstanceID(), this.spawnPointID, damage, (int) hpLostBP);
				break;
			case GameData.WORLDMAP_BOSS:
				ms.getRPCManager().syncWorldBossProgress(this.mapBossID, this.curHP, attackerName, attackerID);
				break;
			default:
				break;
			}
			if (this.getMapType() == GameData.MAP_TYPE_MAPCOPY_SECT_GROUP)
			{
				if(this.curMap instanceof SectGroupMap)
				{
					SectGroupMap sgm = SectGroupMap.class.cast(this.curMap);
					sgm.onMonsterGetDamage(attackerID, damage);
				}
				ms.getRPCManager().syncSectGroupMapProgress(this.getMapID(), this.getMapInstanceID(), this.spawnPointID, attackerID, this.configID, damage, (int) hpLostBP);
			}
			
			if (this.percentDrop != null)
			{
				MapRole attacker = this.curMap.getRole(attackerID);
				if(attacker != null)
					this.tryPercentDrop(attacker);
			}
		}
		
		if (attackerID > 0)
		{
			boolean first = false;
			DamageRole dr = this.damageRoles.get(attackerID);
			if(dr != null)
			{
				if(this.mapBossID > 0)
				{
					switch (this.monsterMapType)
					{
					case GameData.WORLDMAP_BOSS:
					case GameData.DEMONHOLEMAP_BOSS:
						this.damageRank.remove(GameData.getLongTypeValue(dr.info.damage, dr.info.roleID));
						break;
					default:
						break;
					}
				}
			}
			else
			{
				dr = new DamageRole(new SBean.DamageInfo(attackerID, attackerName, 0), attackerClassType);
				this.damageRoles.put(attackerID, dr);
				first = true;
			}
			dr.info.damage += damage;
			
			if(this.mapBossID > 0)
			{
				switch (this.monsterMapType)
				{
				case GameData.WORLDMAP_BOSS:
				case GameData.DEMONHOLEMAP_BOSS:
					updateBossDamageRank(attackerID, dr, first);
					break;
				default:
					break;
				}
			}
		}
	}
	
	private void updateBossDamageRank(int attackerID, DamageRole dr, boolean first)
	{
		if(dr.info.damage > 0)
			this.damageRank.put(GameData.getLongTypeValue(dr.info.damage, dr.info.roleID), dr.info.roleID);
		
		if(this.damageRank.isEmpty())
			return;
		
		if(ms.getMapManager().getMapLogicTime() - this.lastDamageNotifyTime > 1500)
		{
			Set<Integer> notifyRids = new HashSet<>();
			for(int rid: this.damageRoles.keySet())
			{
				MapRole role = this.curMap.getRole(rid);
				if(role == null || role.curPosition.distance(this.curPosition) > 2000)
					continue;
				
				notifyRids.add(rid);
			}
			
			if(!notifyRids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(notifyRids, new SBean.boss_damage_rank(this.mapBossID, getDamageRank()));
			
			this.lastDamageNotifyTime = ms.getMapManager().getMapLogicTime();
		}
		else if(first)
		{
			ms.getRPCManager().sendStrPacket(attackerID, new SBean.boss_damage_rank(this.mapBossID, getDamageRank()));
		}
	}
	
	List<SBean.DamageInfo> getDamageRank()
	{
		return this.damageRank.descendingMap().values().stream().filter(rid -> this.damageRoles.containsKey(rid)).limit(damageRankCount > 0 ? damageRankCount : DAMAGE_RANK_SHOW_COUNT).map(rid -> this.damageRoles.get(rid).info).collect(Collectors.toList());
	}
	
	SBean.EnterMonster getEnterMonster()
	{
		return new SBean.EnterMonster(getEnterBase(), this.curHP, this.maxHp, this.getArmorValue(), this.getArmorValMax(), this.isDead() ? 1 : 0);
	}
	
	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterMonster> monsters = new ArrayList<>();
			monsters.add(this.getEnterMonster());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_monsters(monsters));
		}
	}
	
	void onSelfSpawnNearBy(Set<Integer> rids)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_spawn_monster(this.getEnterMonster()));
	}

	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> monsters = new ArrayList<>();
			monsters.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_monsters(monsters));
			
			if(this.mapBossID > 0)
			{
				switch (this.monsterMapType)
				{
				case GameData.WORLDMAP_BOSS:
				case GameData.DEMONHOLEMAP_BOSS:
					Set<Integer> notifyRids = new HashSet<>();
					for(int rid: rids)
					{
						if(this.damageRoles.containsKey(rid))
							notifyRids.add(rid);
					}
					this.notifyCloseDamage(notifyRids);
					break;
				default:
					break;
				}
			}
		}
	}
	
	void notifyCloseDamage(Set<Integer> rids)
	{
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.boss_damage_close(this.mapBossID));
	}

	void onSelfDead(Set<Integer> rids, int killerID)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_dead(this.getID(), killerID));
	}

	void notifyAddBuff(Set<Integer> rids, int buffID, int realmLvl, int remainTime, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_addbuff(this.getID(), buffID, realmLvl, remainTime, timeTick));
	}

	void notifyRemoveBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_removebuff(this.getID(), buffID, timeTick));
	}

	void notifyDispelBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_dispelbuff(this.getID(), buffID, timeTick));
	}

	void notifyUpdateHp(Set<Integer> rids)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_updatehp(this.getID(), this.getCurHP()));
	}

	void notifyBuffDamage(Set<Integer> rids, int attackerType, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_buffdamage(this.getID(), this.getCurHP(), attackerType, timeTick));
	}

	void notifySelfGetDamage(Set<Integer> rids, BaseRole attacker, int ownerID, SBean.DamageResult res, int skillID, int curDamageEventID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_ondamage(this.getID(), attacker.getID(), attacker.getEntityType(), ownerID, skillID, curDamageEventID, this.getCurHP(), res.dodge, res.deflect, res.crit, res.suckBlood, res.behead, res.remit, res.armor, timeTick));
		
//		if(res.armor != null)
//			ms.getLogger().debug("@@@ nearby_monster_ondamage " + getArmorDamage(res.armor));
	}

//	void notifySelfSuckBlood(Set<Integer> rids)
//	{
//		if (!rids.isEmpty())
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_suckblood(this.getID(), this.curHP));
//	}
	
	void notifySelfReduce(Set<Integer> rids, int reduce)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_reduce(this.id, reduce));
	}

//	void notifySelfBeHead(Set<Integer> rids)
//	{
//		if (!rids.isEmpty())
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_behead(this.id));
//	}

	void notifySelfShiftEnd(Set<Integer> rids, int skillID, GVector3 endpos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_shiftend_monster(skillID, this.getID(), endpos.toVector3(), timeTick));
	}

	void notifySelfRushStart(Set<Integer> rids, int skillID, SBean.Vector3 endPos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_monster_rushstart(this.id, skillID, endPos, timeTick));
	}

	void addEbataunt(Buff buff, SBean.TimeTick timeTick)
	{
		this.forceTarget = new MapEntity(buff.attackerType, buff.attackerID, buff.attackOwnerID);
		BaseRole entity = this.getForceTarget();
		if (entity != null)
		{
			if (this.enmityList.contains(entity))
				this.enmityList.remove(entity);

			this.enmityList.add(0, entity);
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_addataunt_monster(this.getID(), buff.attackerID, buff.attackerType, buff.attackOwnerID, timeTick));
		}
	}

	void onDeadHandle(int attackerType, int attackerID)
	{
		super.onDeadHandle(attackerType, attackerID);
		this.clearSummonMonster(attackerID);
		if (this.caller != null)
			this.caller.summonMonsters.remove(this);
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfDead(rids, attackerID);
		if(this.towerDefencePops != null && !rids.isEmpty())
		{
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.towerdefence_npc_dead());
		}
		if (attackerType == GameData.ENTITY_TYPE_PLAYER)
		{
			Set<Integer> killRoles = new HashSet<>();
			MapRole attacker = this.curMap.getRole(attackerID);
			if (attacker != null)
			{
				attacker.roleTaskDrop(this.getConfigID(), this.getLogicPosition());
				SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(this.getConfigID());
				List<MapRole> membersNearBy = this.getTeamMemberNearBy(attacker, GameData.getInstance().getCommonCFG().team.expAddDistance);
				membersNearBy.add(attacker);
				if (monsterCfg != null)
				{
					SBean.DropRatio dropRatio = GameData.getMonsterDoubleDropRatio(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentDoubleDropConfig());
					SBean.ExtraDropTbl extraDropTbl = GameData.getMonsterExtraDropTable(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentExtralDropConfig());
					for (MapRole member : membersNearBy)
					{
						if (!member.canTakeDrop)
							continue;
						int weaponDropRatio = (member.id == attacker.id) ? attacker.getWeaponMultipleDrop() : 0;
						int vipDropRatio = this.curMap.getMapVipDropRatio(member.vipLevel);
						int fixedDropRatio = dropRatio.fixedDrop + weaponDropRatio + vipDropRatio;
						int randomDropRatio =  dropRatio.randomDrop + weaponDropRatio + vipDropRatio;
						List<DropItem> dropItems = member.curMap.getDropItemList(member.id, monsterCfg.fixedDropID, monsterCfg.randomDropIDs.get(member.configID - 1), monsterCfg.randomDropCnt, fixedDropRatio, randomDropRatio, extraDropTbl, this.getLogicPosition(), this.configID, this.entityType, member.canTakeDrop);
						if (dropItems.size() > 0)
						{
							List<SBean.DropInfo> dropInfo = new ArrayList<>();
							for (DropItem d : dropItems)
							{
								dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));
							}
							ms.getRPCManager().sendStrPacket(member.getID(), new SBean.role_sync_drops(this.getLogicPosition(), dropInfo));
						}

						killRoles.add(member.id);
					}

					//出场景BUFF
					List<Integer> dropBuffs = GameData.getInstance().getBuffDrops(monsterCfg.buffDropID, monsterCfg.buffDropCnt);
					for (Integer bid : dropBuffs)
					{
						MapBuff mapBuff = new MapBuff(bid, this.ms).createNew(this.createPosition(300));
						this.curMap.addMapBuff(mapBuff);
					}
					
					//怪物死亡计数逻辑
					this.syncKill(attacker, killRoles, monsterCfg == null ? 0 : monsterCfg.countType);
				}
				
				this.curMap.addKillMonster(attacker, this);
			}
		}
		this.curMap.delMonsterToCache(this);
		if(this.mapBossID > 0)
		{
			switch (this.monsterMapType)
			{
			case GameData.WORLDMAP_BOSS:
				onWorldBossDead(attackerType, attackerID);
				break;
			case GameData.DEMONHOLEMAP_BOSS:
				onDemonHoleBossDead(attackerType, attackerID);
				break;
			default:
				break;
			}
		}
	}
	
	private void onWorldBossDead(int attackerType, int attackerID)
	{
		Map<Integer, Integer> temp = new HashMap<>();
		Set<Integer> notifyRids = new HashSet<>();
		for(Map.Entry<Integer, DamageRole> e: this.damageRoles.entrySet())
		{
			MapRole r = this.curMap.getRole(e.getKey());
			if(r != null)
				notifyRids.add(e.getKey());
			
			temp.put(e.getKey(), e.getValue().info.damage);
		}
		this.notifyCloseDamage(notifyRids);
		this.setWorldBossRecord(attackerType, attackerID);
		ms.getRPCManager().syncWorldBossDamageRoles(this.mapBossID, attackerID, temp);
	}
	
	private void onDemonHoleBossDead(int attackerType, int attackerID)
	{
		Set<Integer> notifyRids = new HashSet<>();
		for(Map.Entry<Integer, DamageRole> e: this.damageRoles.entrySet())
		{
			MapRole r = this.curMap.getRole(e.getKey());
			if(r != null)
				notifyRids.add(e.getKey());
		}
		this.notifyCloseDamage(notifyRids);
		this.setDemonHoleBossReward(attackerType, attackerID);
	}
	
	private void setWorldBossRecord(int attackerType, int attackerID)
	{
		final SBean.WorldBossCFGS bossCfg = GameData.getInstance().getWorldBossCFGS(this.mapBossID);
		if(bossCfg == null)
			return;
		
		SBean.BossRecord record = new SBean.BossRecord(new SBean.BossReward(new SBean.DamageInfo(0, "", 0), new HashMap<>()), new ArrayList<>());
		DamageRole attacker = this.damageRoles.get(attackerID);
		if(attacker != null)
			this.setBossKillerReward(bossCfg.killDrop, record, attacker);
		
		this.setBossRankReward(bossCfg.rankDrops, record, attackerID);
		this.setWorldBossJoinReward(bossCfg, attackerID);
		ms.getRPCManager().syncWorldBossRecord(this.mapBossID, record);
	}
	
	private void setDemonHoleBossReward(int attackerType, int attackerID)
	{
		final SBean.DemonHoleBossCFGS bossCfg = GameData.getInstance().getDemonHoleBossCFGS(this.mapBossID);
		if(bossCfg == null)
			return;

		this.setBossRankReward(bossCfg.rankDrops, null, attackerID);
	}
	
	private void setBossKillerReward(final SBean.ClassTypeDrop killDrop, SBean.BossRecord record, DamageRole attacker)
	{
		Map<Integer, Integer> reward = record == null ? null : new HashMap<>();
		MapRole killer = ms.getMapManager().getMapRole(attacker.info.roleID);
		boolean teamKill = false;
		if(killer != null)
		{
			this.setBossDropRewardImpl(killer, killDrop, killDrop.count, reward);
			ms.getLogger().info("@@@@@map boss " + this.mapBossID + " killer " + killer.getID() + " , " + killer.roleName + " drop -----------------------------------");
			for(int memberID: killer.getTeamMember())
			{
				if(memberID == killer.id)
					continue;
				
				teamKill = true;
				MapRole member = ms.getMapManager().getMapRole(memberID);
				if(member != null)
				{
					this.setBossDropRewardImpl(member, killDrop, killDrop.count, reward);
					ms.getLogger().info("@@@@@map boss " + this.mapBossID + " killer team member " + member.getID() + " , " + member.roleName + " drop -----------------------------------");
				}
			}
		}
		
		if(record != null && reward != null)
		{
			ms.getLogger().info("@@@@@map boss " + this.mapBossID + " kill drop reward " + reward);
			reward = GameData.getInstance().filterReward(reward, BOSS_REWARD_SHOW_COUNT);
			ms.getLogger().info("@@@@@map boss " + this.mapBossID + " kill drop reward " + reward + " ------------after filter--------------------------");
			if(attacker.info.roleID > 0)
			{
				record.killer.damage = attacker.info.kdClone();
				record.killer.reward = reward;
				if(teamKill)	//队伍击杀id 为负
					record.killer.damage.roleID = -record.killer.damage.roleID;
			}
		}
	}
	
	private void setBossDropRewardImpl(MapRole role, final SBean.ClassTypeDrop classDrop, final int randomDropCount, Map<Integer, Integer> reward)
	{
		List<DropItem> memberDrops = this.curMap.getDropItemList(role.getID(), 0, GameData.getClassTypeDropTblID(classDrop, role.getClassType()), randomDropCount, 1, 1, null, this.getLogicPosition(), this.getConfigID(), this.getEntityType(), role.canTakeDrop);
		if(!memberDrops.isEmpty())
		{
			List<SBean.DropInfo> dropInfo = new ArrayList<>();
			for(DropItem dropItem: memberDrops)
			{
				dropInfo.add(new SBean.DropInfo(dropItem.dropID, dropItem.item.id, dropItem.item.count));
				
				if(reward != null)
					reward.merge(dropItem.item.id, dropItem.item.count, (ov, nv) -> ov + nv);
			}
			
			if(role.getMapID() == this.getMapID() && role.getMapInstanceID() == this.getMapInstanceID())
				ms.getRPCManager().sendStrPacket(role.getID(), new SBean.role_sync_drops(this.getLogicPosition(), dropInfo));
		}
	}
	
	private void setBossRankReward(final List<SBean.ClassTypeDrop> rankDrops, SBean.BossRecord record, int attackerID)
	{
		if(attackerID > 0)
		{
			int rank = 0;
			for(int rid: this.damageRank.descendingMap().values())
			{
				if(!this.damageRoles.containsKey(rid))
					continue;
				
				final SBean.ClassTypeDrop cd = GameData.getRankDrop(rankDrops, ++rank);
				if(cd == null)
					break;
				
				MapRole role = ms.getMapManager().getMapRole(rid);
				if(role != null)
				{
					SBean.BossReward rankReward = record == null ? null : new SBean.BossReward(this.damageRoles.get(rid).info.kdClone(), new HashMap<>());
					this.setBossDropRewardImpl(role, cd, cd.count, rankReward == null ? null : rankReward.reward);
					ms.getLogger().info("#####map boss " + this.mapBossID + " drop role " + role.id + " , " + role.roleName + " rank " + rank + " drop-----------------------------------------");
					if(rankReward != null)
					{
						ms.getLogger().info("#####map boss " + this.mapBossID + " rank " + rank + " drop reward " + rankReward.reward + " rank role " + role.id + " , " + role.roleName + "--------------------------------------------------");
						rankReward.reward = GameData.getInstance().filterReward(rankReward.reward, BOSS_REWARD_SHOW_COUNT);
						ms.getLogger().info("#####map boss " + this.mapBossID + " rank " + rank + " drop reward " + rankReward.reward + " ------------after filter--------------------------");
					}
					
					if(record != null && rankReward != null)
						record.rank.add(rankReward);
				}
				else
				{
					ms.getLogger().info("#####map boss " + this.mapBossID + " rank " + rank + " role " + rid + " already leava map, no reward");
				}
			}
		}
		else
		{
			ms.getLogger().info("#####map boss " + this.mapBossID + " dead time out");
		}
	}
	
	private void setWorldBossJoinReward(final SBean.WorldBossCFGS bossCfg, int attackerID)
	{
		if(attackerID > 0)
		{
			ms.getLogger().info("#####send map boss " + this.mapBossID + " join reward role");
			for(int rid: this.damageRank.descendingMap().values())
			{
				if(!this.damageRoles.containsKey(rid))
					continue;
				
				MapRole role = ms.getMapManager().getMapRole(rid);
				if(role != null && role.getMapID() == this.getMapID() && role.getMapInstanceID() == this.getMapInstanceID())
				{
					if(role.getCurPosition().distance(this.getCurPosition()) < 2000)
						this.setBossDropRewardImpl(role, bossCfg.joinDrop, bossCfg.joinDrop.count, null);
				}
			}
		}
	}
	
	//怪物死亡计数逻辑
	void syncKill(MapRole attacker, Set<Integer> killRoles, int countType)
	{
		Set<Integer> assistRole = new HashSet<>();
		if (this.monsterMapType != GameData.WORLDMAP_BOSS && this.monsterMapType != GameData.WORLDMAP_SUPERMONSTER && countType == GameData.MONSTER_COUNTTYPE_DAMAGE)
		{
			int d = GameData.getInstance().getCommonCFG().team.expAddDistance;
			for (Integer rid : this.damageRoles.keySet())
			{
				if (killRoles.contains(rid) || assistRole.contains(rid))
					continue;

				MapRole r = this.curMap.getRole(rid);
				if (r != null && r.curPosition.distance(this.curPosition) <= d)
				{
					assistRole.add(r.id);
					List<MapRole> sameMap = this.getTeamMemberSameMap(r);
					for (MapRole m : sameMap)
					{
						if (m.curPosition.distance(this.curPosition) <= d)
							assistRole.add(m.id);
					}
				}
			}
		}
		
		for (int rid : killRoles)
		{
			if(rid == attacker.id)
				ms.getRPCManager().addRoleKill(rid, this.getMapID(), this.getMapInstanceID(), this.getEntityType(), this.getConfigID(), attacker.getWeaponAddExp(), attacker.id); 
			else
				ms.getRPCManager().addRoleKill(rid, this.getMapID(), this.getMapInstanceID(), this.getEntityType(), this.getConfigID(), 1, attacker.id);
		}
		
		for (int rid : assistRole)
			ms.getRPCManager().addRoleKill(rid, this.getMapID(), this.getMapInstanceID(), this.getEntityType(), this.getConfigID(), 1, 0);
	}

	void nextSkill()
	{
		this.curAttackSeq++;
		if (this.curAttackSeq >= this.attackList.size())
			this.curAttackSeq = 0;
		
		this.curSkillID = null;
		this.setCurSkillID();
	}

	public int getFightSkillLevel(int sid)
	{
		FightSkill skill = this.fightSkills.get(sid);
		if (skill == null)
			return 0;
		return skill.level;
	}

	List<BaseRole> getEnemiesNearBy()
	{
		switch (this.getMapType())
		{
		case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
		case GameData.MAP_TYPE_MAPCOPY_TOWER_DEFENCE:	
			return super.getEnemiesNearBy();
		default:
			return this.getPlayerEnemiesNearBy();
		}
	}

	//获取目标
	List<BaseRole> getTargets(Skill skill)
	{
		List<BaseRole> targets = new ArrayList<>();
		SBean.Scope scope = skill.baseFixCfg.scope;
		int skillType = skill.baseFixCfg.type;
		int maxTargets = skill.baseFixCfg.maxTargets;

		//技能范围类型（自身、单点技能）
		if (scope.type == Skill.eSScopT_Owner)
		{
			if (!this.isDead() && !this.isInProtectTime())
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
				entity = this.getRoleInCheckRange();

			if (entity != null)
			{
				//				this.nearestTarget = entity;
				distance = this.curPosition.distance(entity.curPosition);
				if (distance <= skillRange + entity.getRadius())
					targets.add(entity);
			}

			return targets;
		}

		//技能效果类型
		switch (skillType)
		{
		case GameData.eSE_Buff:
			//己方单位
			targets.addAll(this.getMonsterNearBy());
			break;
		case GameData.eSE_Damage:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());
			//中立单位
			break;
		case GameData.eSE_DBuff:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());
			break;
		default:
			break;
		}
		targets = skill.checkTarget(this, targets, scope, maxTargets);

		return targets;
	}

	void changePosition(int curGridX, int curGridZ, int newGridX, int newGridZ, int destory)
	{
		Set<Integer> enterRids = this.curMap.getSelfEnterRoleIDsNearby(curGridX, curGridZ, newGridX, newGridZ, this.owner, this.owner != null);
		filterSpawnRole(enterRids);
		this.onSelfEnterNearBy(enterRids);
		
		Set<Integer> leaveRids = this.curMap.getSelfLeaveRoleIDsNearby(curGridX, curGridZ, newGridX, newGridZ, this.owner, this.owner != null);
		filterSpawnRole(leaveRids);
		this.onSelfLeaveNearBy(leaveRids, destory);
	}
	
	void filterSpawnRole(Set<Integer> rids)
	{
		if(this.spawnRole <= 0)
			return;
		
		if(!rids.contains(this.spawnRole))
		{
			rids.clear();
			return;
		}
		
		rids.clear();
		rids.add(this.spawnRole);
	}
	
	boolean changeMapGrid(MapGrid grid)
	{
		if (grid != this.curMapGrid)
		{
			this.curMapGrid.delMonster(this);
			grid.addMonster(this);
			return true;
		}
		return false;
	}

	public SpawnPoint getCurSpawnPoint()
	{
		return this.curMap.getSpawnPoint(spawnPointID);
	}

	public void setCurSpawnPoint(int id)
	{
		this.spawnPointID = id;
	}

	public int getMoveSpeed()
	{
		return this.moveSpeed;
	}

	public GVector3 getMonsterCheckCenter()
	{
		return this.monsterCheckCenter;
	}

	public void setMonsterCheckCenter(GVector3 monsterCheckCenter)
	{
		this.monsterCheckCenter.reset(monsterCheckCenter);
	}

	public float getMonsterCheckRadius()
	{
		return this.monsterCheckRadius;
	}

	public void setMonsterCheckRadius(float monsterCheckRadius)
	{
		this.monsterCheckRadius = monsterCheckRadius;
	}

	public int getSpawnPointID()
	{
		return this.spawnPointID;
	}

	public int getSpawnSeq()
	{
		SpawnPoint point = this.curMap.getSpawnPoint(spawnPointID);
		if (point != null)
		{
			return point.getSpawnSeq();
		}
		return 1;
	}

	public long getMoveTickLine()
	{
		return this.moveTickLine;
	}

	public int getEventLowHpDmgType()
	{
		switch (this.bossType)
		{
		case GameData.MONSTER_BOSSTYPE_NORMALBOSS:
		case GameData.MONSTER_BOSSTYPE_FINALBOSS:
			return GameData.DMGTO_THP_TYPE_BOSS_MONSTER;
		default:
			return GameData.DMGTO_THP_TYPE_MONSTER;
		}
	}
	
	int getBossType()
	{
		return this.bossType;
	}
	
	byte getBWType()
	{
		return this.bwType;
	}
	
	int getSpawnRole()
	{
		return Math.abs(spawnRole);
	}
	
	public int getSectId()
	{
		return this.sectId;
	}

	public void setSectId(int sectId)
	{
		this.sectId = sectId;
	}
	
	public void setDamageRankCount(int count)
	{
		if(count > DAMAGE_RANK_MAX_COUNT)
			count = DAMAGE_RANK_MAX_COUNT;
		
		this.damageRankCount = count;
	}
	
	Set<Integer> getRoleIDsNearBy(MapRole role)
	{
		if(this.spawnRole > 0)
		{
			Set<Integer> rids = new HashSet<>();
			rids.add(this.spawnRole);
			return rids;
		}
		
		return super.getRoleIDsNearBy(role);
	}
	
	public void setToBirthPos()
	{
		this.setNewPosition(this.birthPosition);
	}
	
	public class DamageRole
	{
		SBean.DamageInfo info;
		int classType;
		
		DamageRole(SBean.DamageInfo info, int classType)
		{
			this.info = info;
			this.classType = classType;
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------
	void worldBossPop(int popIndex)
	{
		if(this.mapBossID <= 0 || this.monsterMapType != GameData.WORLDMAP_BOSS)
			return;
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
		{
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.world_boss_pop(this.id, this.mapBossID, popIndex));
			ms.getLogger().debug("notify rids " + rids + " map boss " + this.mapBossID + " pop " + popIndex);
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	
	@Override
	boolean isDead()
	{
		return !this.logDamage && super.isDead();
	}
	
	//对目标类型的内甲伤害转移
	float getArmorTransRate()
	{
		return this.armorTransRate;
	}
	
	//对目标类型的内甲伤害加深
	float getArmorDmgDeep()
	{
		return this.armorDmgDeep;
	}
	
	int getCurArmor()
	{
		return this.armorID;
	}
	
	//////
	private static final int IDLE_VALID_DISTANCE 		= 800;
	private static final int DAMAGE_RANK_SHOW_COUNT 	= 10;
	private static final int BOSS_REWARD_SHOW_COUNT 	= 5;
	
	private static final int DAMAGE_RANK_MAX_COUNT 		= 25;
	
	int spawnPointID;
	private int bossType; //小怪、小boss、最终boss
	private byte bwType;
	private long collisionTime;
	private boolean configActive;
	private int curAttackSeq;
	private long tryAttackTime;
	private boolean canRotation = true;
	
	//不同地图特有的怪物类型
	int monsterMapType;
	int mapBossID;
	int spawnRole;
	private long lastDamageNotifyTime;

	private long moveTickLine;
	private float monsterCheckRadius;
	private GVector3 monsterCheckCenter = new GVector3();
	//
	private long idleTime;
	private List<Integer> patrolInterval = new ArrayList<>();
	private int patrolRadius;
	private int patrolSpeed;
	private long standByTime;
	private int second;
	
	Map<Integer, DamageRole> damageRoles = new HashMap<>();		//<roleID, DamageRole>
	private TreeMap<Long, Integer> damageRank = new TreeMap<>();				//<damage, roleID>
	private long stupidTime;
	private int sectId = -1;
	private Map<Integer, SBean.PercentDropCFGS> percentDrop;		//<roleID, DamageRole>
	private int damageRankCount = 0;
	private boolean logDamage = false;
	List<Float> towerDefencePops;
	
	int armorID;
	float armorTransRate;
	float armorDmgDeep;
}
