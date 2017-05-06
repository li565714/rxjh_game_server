package i3k.gmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import i3k.SBean;
import i3k.gmap.BaseRole.*;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

public class Skill
{
	public static final int eSScopT_Owner 		= 1;		//自身
	public static final int eSScopT_Single 		= 2;		//单点
	public static final int eSScopT_CricleO 	= 3;		//自身圆心
	public static final int eSScopT_CricleT 	= 4;		//目标圆心
	public static final int eSScopT_SectorO 	= 5;		//自身前方扇形
	public static final int eSScopT_RectO 		= 6;		//自身前方矩形
	public static final int eSScopT_MulC 		= 7;		//自身周围多个圆形区域
	public static final int eSScopT_EllipseO	= 8;		//自身周围椭圆
	
	// 技能分组
	public static final int eSG_Attack		= 0;	// 普攻
	public static final int eSG_Skill		= 1;	// 技能
	public static final int eSG_TriSkill	= 2;	// 触发技能
	
	public static final int SHIFT_DISTANCE_FIX1 = 150;
	public static final int SHIFT_DISTANCE_FIX2 = 50;

	public static final int DIY_SKILL_ID = 9999999;
	public static final int SKILL_DURATION_MAX = 10000;

	int id;
	int level;
	int realmLvl;
	int skillGroup;
	long skillEndTime;
	int curDamageEventID;
	boolean isAttackEffect = false;
	GVector3 curPosition;
	GVector3 attackPosition;
	SBean.SkillBaseCommonCFGS baseCommonCfg;
	SBean.SkillBaseFixCFGS baseFixCfg;
	int skillDuration;
	SBean.SkillLevelFixCFGS lvlFixCfg;
	BaseRole hoster;
	int damageTick;
	int damageCount;
	BaseRole target;
	List<Skill> childern;
	GuideInfo guideInfo;
	RushInfo rushInfo;
	ShiftInfo shiftInfo;
	SummonInfo summonInfo;
	SBean.TimeTick clientRushStartTimeTick;
//	int skillSpeed;
	DmgToSkillInfo dmgToSkillInfo;
	DmgFixInfo dmgFixInfo;
	FlyInfo flyInfo;
	float dmgAddHp;				//根据伤害量额外回血
	float maxHpDMG;				//伤害时根据当前气血额外追加伤害
	ExtraDropInfo dropInfo;
	SlashInfo slashInfo;
	boolean touchTarget;
	AuraInfo auraInfo;
	int triSkillTarget;
	int followSkillSeq;
	boolean valid;
	boolean endSkillOnFinish;
	int mainSkillID;

	Skill(int id, int level, int realmLvl, int skillGroup, long skillEndTime, GVector3 curPosition, SBean.SkillBaseCommonCFGS baseCommonCfg, 
			SBean.SkillBaseFixCFGS baseFixCfg, SBean.SkillLevelFixCFGS lvlFixCfg, BaseRole hoster)
	{
		this.id = id;
		this.level = level;
		this.realmLvl = realmLvl;
		this.skillGroup = skillGroup;
		this.skillEndTime = skillEndTime;
		this.curPosition = new GVector3().reset(curPosition);
		this.baseCommonCfg = baseCommonCfg;
		this.baseFixCfg = baseFixCfg;
		this.skillDuration = baseFixCfg.duration;
		this.lvlFixCfg = lvlFixCfg;
		this.hoster = hoster;
		this.damageCount = 3;
		this.childern = new ArrayList<>();
		this.valid = true;
	}

	List<BaseRole> checkTarget(BaseRole attacker, List<BaseRole> targets, SBean.Scope scope, int maxTargets)
	{
		Iterator<BaseRole> itTargets = targets.iterator();
		int radius = 0;
		float distance = 0.0f;
		GVector3 dirVertical = GVector3.UNIT_Y.crossProduct(attacker.getCurRotation()).normalize(); //面朝方向的垂直方向

		GVector3 skillPosition = this.attackPosition == null ? attacker.curPosition : this.attackPosition;
		//技能范围类型
		switch (scope.type)
		{
		case eSScopT_CricleO: //自身圆心  0:半径
			while (itTargets.hasNext())
			{
				BaseRole role = itTargets.next();
				if(role.checkState(Behavior.EBRETREAT) && role.entityType == GameData.ENTITY_TYPE_MONSTER)
				{
					itTargets.remove();
					continue;
				}
				
				radius = attacker.getRadius() + role.getRadius();
				distance = skillPosition.distance(role.curPosition);
				if ((distance - radius) > (double) scope.args.get(0))
					itTargets.remove();
			}
			break;
		case eSScopT_CricleT: //目标圆心 0:距离  1:半径
			GVector3 offset = attacker.curRotation.scale(scope.args.get(0));
			GVector3 pos = skillPosition.sum(offset);
			while (itTargets.hasNext())
			{
				BaseRole role = itTargets.next();
				if(role.checkState(Behavior.EBRETREAT) && role.entityType == GameData.ENTITY_TYPE_MONSTER)
				{
					itTargets.remove();
					continue;
				}
				
				radius = role.getRadius() + scope.args.get(1);
				distance = pos.distance(role.getCurPosition());
				if (distance > radius)
					itTargets.remove();
			}
			break;
		case eSScopT_SectorO: //前方扇形  0：半径  1:角度  2:是否反向
			int skillradius = scope.args.get(0);
			float skillAngle = (float) (scope.args.get(1) / 180.0f * Math.PI);
			while (itTargets.hasNext())
			{
				BaseRole role = itTargets.next();
				if (role == attacker)
					continue;

				if(role.checkState(Behavior.EBRETREAT) && role.entityType == GameData.ENTITY_TYPE_MONSTER)
				{
					itTargets.remove();
					continue;
				}
				
				radius = attacker.getRadius() + role.getRadius();
				distance = skillPosition.distance(role.getCurPosition());
				if (distance - radius > skillradius)
				{
					itTargets.remove();
				}
				else
				{
					GVector3 rotation = attacker.curRotation.scale(skillradius + radius);
					GVector3 dirRToM = role.getCurPosition().diffence2D(skillPosition);
					GVector3 v1 = rotation.crossProduct(GVector3.ZERO.diffence(GVector3.UNIT_Y)).normalize();
					GVector3 v2 = rotation.crossProduct(GVector3.UNIT_Y).normalize();
					v1.selfSacle((skillradius + radius) * (float) Math.tan(skillAngle / 2));
					v2.selfSacle((skillradius + radius) * (float) Math.tan(skillAngle / 2));
					GVector3 dir1 = rotation.sum(v1);
					GVector3 dir2 = rotation.sum(v2);
					boolean sameDir = dirRToM.crossProduct(dir1).y * rotation.crossProduct(dir1).y >= 0;
					if (dirRToM.crossProduct(dir1).y * dirRToM.crossProduct(dir2).y > 0 || !sameDir)
					{
						itTargets.remove();
					}
				}
			}
			break;
		case eSScopT_RectO: //前方矩形 0:长度  1:宽度
			while (itTargets.hasNext())
			{
				BaseRole role = itTargets.next();
				if (role == attacker)
					continue;

				if(role.checkState(Behavior.EBRETREAT) && role.entityType == GameData.ENTITY_TYPE_MONSTER)
				{
					itTargets.remove();
					continue;
				}
				
				if (this.shiftInfo != null && this.shiftInfo.shiftTarget.contains(role))
					continue;

				radius = attacker.getRadius() + role.getRadius();
				GVector3 pos1 = attacker.getCurRotation().scale(scope.args.get(0) + radius).sum(skillPosition);

				GVector3 posR1 = dirVertical.scale(scope.args.get(1) + role.getRadius()).sum(skillPosition);
				GVector3 posR2 = dirVertical.scale(-scope.args.get(1) - role.getRadius()).sum(skillPosition);
				GVector3 posR3 = dirVertical.scale(scope.args.get(1) + role.getRadius()).sum(pos1);
				GVector3 posR4 = dirVertical.scale(-scope.args.get(1) - role.getRadius()).sum(pos1);
				
				if (notContain2(posR1, posR2, posR3, posR4, role.getCurPosition()))
					itTargets.remove();
			}
			break;
		case eSScopT_MulC: //自身周围多个圆形区域   lacking
			while (itTargets.hasNext())
			{
//				BaseRole role = itTargets.next();
//				itTargets.remove();
			}
			break;
		case eSScopT_EllipseO: //自身周围椭圆
			int a = scope.args.get(0); //长半径
			int b = scope.args.get(1); //短半径
			int c = 0; //焦距/2		
			while (itTargets.hasNext())
			{
				BaseRole role = itTargets.next();
				if(role.checkState(Behavior.EBRETREAT) && role.entityType == GameData.ENTITY_TYPE_MONSTER)
				{
					itTargets.remove();
					continue;
				}
				
				radius = attacker.getRadius() + role.getRadius();
				c = (int) Math.sqrt((a - radius) * (a - radius) - (b - radius) * (b - radius));
				GVector3 diffPos = dirVertical.scale(radius + c);

				//焦点
				GVector3 F1 = skillPosition.sum(diffPos);
				GVector3 F2 = skillPosition.sum(diffPos.scale(-1));
				if (role.curPosition.distance(F1) + role.curPosition.distance(F2) > 2 * (a + radius))
					itTargets.remove();
			}
			break;
		default:
			break;
		}

		if (maxTargets > 0 && targets.size() > maxTargets)
		{
			for (int i = targets.size(); i > maxTargets; i--)
			{
				targets.remove(i - 1);
			}
		}

		return targets;
	}

	public boolean notContain2(GVector3 v1, GVector3 v2, GVector3 v3, GVector3 v4, GVector3 v)
	{
		float d1 = multiply(v, v1, v2);
		float d2 = multiply(v, v3, v4);
		float d3 = multiply(v, v1, v3);
		float d4 = multiply(v, v2, v4);
		return !(d1 * d2 <= 0 && d3 * d4 <= 0);
	}

	//向量的外积（判断点在向量哪测）
	public float multiply(GVector3 v, GVector3 v1, GVector3 v2)
	{
		return ((v.z - v1.z) * (v.x - v2.x) - (v.z - v2.z) * (v.x - v1.x));
	}

	boolean autoProcessDamage(BaseRole attacker, long logicTime, SBean.TimeTick timeTick)
	{
		if (this.id <= 0 || this.damageTick >= this.damageCount)
			return false;

		if (this.guideInfo != null)
			return this.guideEvent(attacker, logicTime, timeTick);
		if(this.slashInfo != null)
			return this.slashEven(attacker, logicTime, timeTick);
		else
			return this.skillEvent(attacker, logicTime, timeTick);
	}

	boolean skillEvent(BaseRole attacker, long logicTime, SBean.TimeTick timeTick)
	{
		if (this.lvlFixCfg.events.isEmpty())
			return false;

		long skillStartTime = this.skillEndTime - this.skillDuration;
		{
			if (this.rushInfo == null || this.rushInfo.rushStart || logicTime >= this.rushInfo.rushEndTime)
			{
				List<SBean.SkillEventCFGS> events = this.lvlFixCfg.events;
				int triTime = events.get(this.damageTick).triTime;
				if (triTime > 0)
				{
					if ((logicTime >= skillStartTime + triTime) && (this.flyInfo == null || this.touchTarget))
					{
						if (this.valid)
							this.onProcessDamageHandler(attacker, this.curDamageEventID, attacker.getCurRotation(), timeTick);
						this.curDamageEventID++;
					}
				}
				else
					return false;
			}
		}
		return this.damageCount > this.damageTick;
	}

	boolean guideEvent(BaseRole attacker, long logicTime, SBean.TimeTick timeTick)
	{
//		long skillStartTime = this.skillEndTime - this.skillDuration;
		//		if(timeMillis > time + skillStartTime + this.guideInfo.duration)
		//			return false;
		
		if (this.guideInfo.damageCount > this.damageTick)
		{
			long skillStartTime = this.skillEndTime - this.skillDuration;
			int time = this.baseCommonCfg.spell.time + this.baseCommonCfg.charge.time;
			int triTime = (this.damageTick + 1) * this.guideInfo.interval;
			if (logicTime >= skillStartTime + time + triTime)
				this.onProcessDamageHandler(attacker, this.guideInfo.damageEventID, attacker.getCurRotation(), timeTick);
		}
		return this.guideInfo.damageCount > this.damageTick;
	}

	boolean slashEven(BaseRole attacker, long logicTime, SBean.TimeTick timeTick)
	{
		if(this.slashInfo.damageCount > this.damageTick)
		{
			long skillStartTime = this.skillEndTime - this.skillDuration;
			int time = this.baseCommonCfg.spell.time + this.baseCommonCfg.charge.time;
			int triTime = this.damageTick * this.slashInfo.interval;
			if (logicTime >= skillStartTime + time + triTime)
				return this.onProcessDamageHandler(attacker, 0, attacker.getCurRotation(), timeTick);		//默认伤害事件ID 0;
		}
		
		return this.slashInfo.damageCount > this.damageTick;
	}
	
	public boolean onProcessDamageHandler(BaseRole attacker, int curDamageEventID, GVector3 rotation, SBean.TimeTick timeTick)
	{
		this.damageTick++;
		this.curDamageEventID = curDamageEventID;
		attacker.setCurRotation(rotation);
		if (this.curDamageEventID >= 0 && this.curDamageEventID < 3)
		{
			List<BaseRole> targets = null;
			if(this.flyInfo != null)
			{
				targets = new ArrayList<>();
				if(this.target != null && attacker.checkTargetValid(this.target) && attacker.curMap.checkBaseRoleCanAttack(attacker, this.target))
					targets.add(this.target);
			}
			else
				targets = attacker.getTargets(this);
			
			if (this.shiftInfo != null)
			{
				Iterator<BaseRole> it = this.shiftInfo.shiftTarget.iterator();
				while(it.hasNext())
				{
					BaseRole t = it.next();
					if(!targets.contains(t) && attacker.checkTargetValid(t) && attacker.curMap.checkBaseRoleCanAttack(attacker, t))
						targets.add(t);
				}
			}
			
			if(targets.isEmpty() && this.guideInfo != null && this.guideInfo.autoChangeRotation)
			{
				BaseRole entity = attacker.getRoleInCheckRange();
				if(entity != null)
				{
					attacker.setCurRotation(entity.curPosition.diffence2D(attacker.getCurPosition()).normalize());
					attacker.notifySelfChangeRotation();
					
					targets = attacker.getTargets(this);
				}
			}
			
			if (this.valid)
				attacker.processDamage(this, targets, this.lvlFixCfg.events.get(this.curDamageEventID).damage, this.lvlFixCfg.events.get(this.curDamageEventID).status, timeTick, this.damageTick == 1 ? this.dropInfo : null);
			
			if(targets.isEmpty() && this.slashInfo != null)
				return false;
		}
		
		return true;
	}

	SBean.DamageResult getDamage(BaseRole attacker, BaseRole target, SBean.SubDamageCFGS subDamage, List<SBean.SubStatus> status)
	{
		SBean.DamageResult res = new SBean.DamageResult(0, 1, 0, 1, 0, 0, new ArrayList<>(), 0, 0, null);
		if (target == null || subDamage == null)
			return res;

		boolean lastDead = false;
		if (target.isDead() || target.isInProtectTime() || lastDead)
			return res;

		int skillType = this.baseFixCfg.type;
		int atr = MapManager.ATTACK_ATR; //命中值
		atr = this.isAtr(attacker, target, subDamage);
		if (skillType != GameData.eSE_Damage) //非伤害技能必命中
			atr = MapManager.ATTACK_ATR;
		
		if (atr != MapManager.ATTACK_DODGE)
		{
			res.dodge = 0;
			if (atr != MapManager.ATTACK_DEFLECT)
				setSkillBuff(attacker, target, res, status);	//buff
			
			if (target.getEntityType() == GameData.ENTITY_TYPE_BLUR)
			{
				if (skillType == GameData.eSE_Buff || GameData.checkRandom(subDamage.odds))
				{
					if (res.crit == 1)
						res.damage = 2;
					else
						res.damage = 1;
				}
			}
			else
			{
				if (GameData.checkRandom(subDamage.odds))
				{
					boolean crit = true;
					crit = isCrit(attacker, target, subDamage);

					if (skillType == GameData.eSE_Buff)			//治疗
						setSkillHealValue(attacker, target, res, subDamage, crit);
					else
						setSkillDamageValue(attacker, target, res, subDamage, atr, crit);
				}
				else if (skillType != GameData.eSE_Buff)
					res.dodge = 1;
			}
			
			if (skillType != GameData.eSE_Buff)
			{
				res.reduce = target.reduction(res.damage);
				res.damage = res.damage - res.reduce;
				target.setDamageLimit(res);
			}

			if(skillType == GameData.eSE_Damage)
			{
				res.damage = target.fixUnDeadDamage(res.damage);
			}
			
//			RoleState state = target.curStates.get(Behavior.EBUNDEAD);
//			if(state != null && skillType == GameData.eSE_Damage)
//			{
//				if(target.curHP - res.damage < state.value)
//					res.damage = target.curHP > state.value ? target.curHP - state.value : 0;
//			}
		}
		return res;
	}
	
	private void setSkillBuff(BaseRole attacker, BaseRole target, SBean.DamageResult res, List<SBean.SubStatus> status)
	{
		if(target.getEntityType() == GameData.ENTITY_TYPE_ESCORTCAR)
			return;
		
		for (SBean.SubStatus subState : status)
		{
			int odds = subState.odds;
			SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(subState.buffID);
			if (buffCfg != null)
			{
				int againstPropID = buffCfg.againstPropID;
				if (againstPropID > 0)
				{
					int against = attacker.getFightProp(againstPropID);
					if (buffCfg.owner != 1)
						against = target.getFightProp(againstPropID);
					odds = Math.max(0, odds - against);

				}
				if (GameData.checkRandom(odds))
					res.buffs.add(subState.buffID);
			}
		}
	}
	
	private void setSkillDamageValue(BaseRole attacker, BaseRole target, SBean.DamageResult res, SBean.SubDamageCFGS subDamage, int atr, boolean crit)
	{
		if(target.isInvincible())
		{
			res.damage = 0;
			res.dodge = 0;
			res.deflect = 0;
			res.suckBlood = 0;
		}
		else
		{
			double val = 0;
			int skillGroup = this.skillGroup;
			int skillType = this.baseFixCfg.type;
			SBean.CommonSkillCFGS commonSkillCfg = GameData.getInstance().getCommonCFG().skill;
			val = getBaseDamage(attacker, target, res, subDamage, atr, crit, commonSkillCfg);

			res.suckBlood = 0;
			//护体
			if (skillType == GameData.eSE_Damage && skillGroup != eSG_Attack)
				val = val - getShellValue(attacker, target, res, subDamage, crit, val, commonSkillCfg);
			
			//内甲伤害转移
			if(calcArmorDamage(attacker, target, res, val))
				val = (int) (val * (1.f - target.getArmorTransRate()));
			
			//计算吸血值
			if (skillType == GameData.eSE_Damage && skillGroup != eSG_Attack)
				setSkillBlood(attacker, target, res, subDamage, crit, val, commonSkillCfg);
			
			res.damage = Math.max(1, (int) val);
			
			if(target.isRemit(attacker, res.damage))
			{
				res.damage = 0;
				res.remit = 1;
			}
		}
	}
	
	private boolean calcArmorDamage(BaseRole attacker, BaseRole target, SBean.DamageResult res, double val)
	{
		int t_curArmor = target.getCurArmor();
		int t_armVal = target.getArmorValue();
		if(t_curArmor == 0 || t_armVal == 0)
			return false;
		
		res.armor = new SBean.ArmorDamage();
		float t_armTrans = target.getFightProp(BaseRole.EPROPID_ARMTRF);
		int t_lvl = target.level;
		SBean.CommonArmorCFGS cac = GameData.getInstance().getCommonCFG().armor;
		float transOdd = oddFix(t_armTrans / (cac.transParam1 * t_lvl + cac.transParam2), cac.transOddMin, cac.transOddMax, target.getEntityType() == GameData.ENTITY_TYPE_MONSTER);
		if(!GameData.checkRandom(transOdd))
			return false;
		
		float t_armDef = target.getFightProp(BaseRole.EPROPID_ARMDEF);
		float dmgFix = oddFix((1.f - t_armDef / (t_armDef + cac.defParam1 * t_lvl + cac.defParam2)), cac.dmgFixMin, cac.dmgFixMax, target.getEntityType() == GameData.ENTITY_TYPE_MONSTER);
		
		val = val * target.getArmorTransRate() * dmgFix;
		{
			SBean.ArmorTypeCFGS atc = GameData.getInstance().getArmorTypeCFGS(attacker.getCurArmor());
			if(atc != null)
			{
				float suck 		= attacker.getFightProp(BaseRole.EPROPID_ARMSUCK);
				float destroy 	= attacker.getFightProp(BaseRole.EPROPID_ARMDESTROY);
				float weak 		= attacker.getFightProp(BaseRole.EPROPID_ARMWEAK);
				
				if(atc.restrainType == t_curArmor)
				{
					val *= (1.f + target.getArmorDmgDeep());
					suck *= (1.f + cac.suck.add);
					destroy *= (1.f + cac.destroy.add);
					weak *= (1.f + cac.weak.add);
				}
				
				float dt = (float) (t_armVal - val);
				if(suck > 0 && dt > 1 && attacker.owner.trigArmorSuck())
				{
					suck = (int)Math.min(dt, suck);
					dt = dt - suck;
					res.armor.suck = (int)suck;
				}
				
				if(destroy > 0 && dt > 1 && attacker.owner.trigArmorDestroy())
				{
					destroy = (int)Math.min(dt, destroy);
					dt = dt - destroy;
					res.armor.destroy = (int)destroy;
				}
				
				if(weak > 0 && dt > 1 && attacker.owner.trigArmorWeak())
					res.armor.weak = (int)weak;
			}
		}
		
		res.armor.damage = Math.min((int)val, t_armVal);
		return true;
	}
	
	private float oddFix(float odd, float min, float max, boolean ignoreLimit)
	{
		if(ignoreLimit)
			return odd;
		
		if(odd < min)
			odd = min;
		
		if(odd > max)
			odd = max;
		
		return odd;
	}
	
	private double getBaseDamage(BaseRole attacker, BaseRole target, SBean.DamageResult res, SBean.SubDamageCFGS subDamage, int atr, boolean crit, SBean.CommonSkillCFGS commonSkillCfg)
	{
		double val = 0;
		int realmLevel = this.realmLvl;
		int skillType = this.baseFixCfg.type;
		
		int lvl		= attacker.level;
		int atkN	= attacker.getFightProp(BaseRole.EPROPID_ATKN);				//攻击力
		double atkA	= attacker.getFightPropF(BaseRole.EPROPID_ATKA);			//暴击伤害
		int atkH	= attacker.getFightProp(BaseRole.EPROPID_ATKH);				//神圣伤害
		int atkC	= attacker.getFightProp(BaseRole.EPROPID_ATKC);				//心法伤害
		int atkW	= attacker.getFightProp(BaseRole.EPROPID_ATKW);				//神兵伤害
		double atkD	= attacker.getFightPropF(BaseRole.EPROPID_ATKD);			//偏斜伤害
		int a_atkIF  = attacker.getFightProp(BaseRole.EPROPID_ATKIF);			//内力
		double a_armNADeep = skillGroup == eSG_Attack ? attacker.getFightPropF(BaseRole.EPROPID_NADEEP) : 0;	//普攻伤害加深
		
		int defN	= attacker.trigArmorIgnoreDef() ? 0 : target.getFightProp(BaseRole.EPROPID_DEFN);		//防御力
		double defA	= target.getFightPropF(BaseRole.EPROPID_DEFA);					//暴击抗性
		int defC	= target.getFightProp(BaseRole.EPROPID_DEFC);					//心法防御
		int defW	= target.getFightProp(BaseRole.EPROPID_DEFW);					//神兵防御
		int t_atkIF  = target.getFightProp(BaseRole.EPROPID_ATKIF);
	
		double F1 = 1.0 + this.getSpiritFix(attacker, target);	//心法档次差系数
		double F2 = 1.0 + this.getWeaponFix(attacker, target);	//神兵档次差系数

		double arg1 = commonSkillCfg.atk.get(0);
		double arg2 = commonSkillCfg.atk.get(1);
		double arg3 = commonSkillCfg.atk.get(2);

		val = Math.max(0, atkN - defN) * (subDamage.arg1 + a_armNADeep) * (1.0 + subDamage.realmAdd * realmLevel) + subDamage.arg2 * (1.0 + subDamage.realmAdd * realmLevel) + atkH + Math.max(0, atkC - defC) * F1 + Math.max(0, atkW - defW) * F2;
		val += a_atkIF > t_atkIF ? a_atkIF * 2.f : a_atkIF;			//内力
		if(this.maxHpDMG > 0)										//伤害时根据当前气血额外追加伤害(特化公式)
			val += attacker.getMaxHP() * this.maxHpDMG;
		
		if (atr == MapManager.ATTACK_DEFLECT) //偏斜伤害
		{
			val = val * atkD;
			res.dodge = 1;
			res.deflect = 0;
		}
		else if (crit) //暴击
		{
			double hpDmgToFix = this.hpDmgToTrigFix(attacker, target);
			val = val * Math.max(arg1, atkA - defA) * (1.0f + this.dmgToTrigFix(attacker, GameData.eSE_Damage, 1) + hpDmgToFix + this.stateDmgToTrigFix(attacker, target, GameData.eSE_Damage, 1));
			res.crit = 1;
			res.behead = hpDmgToFix > 0 ? 1 : 0;
		}
		else
		{
			double hpDmgToFix = this.hpDmgToTrigFix(attacker, target);
			val = val * (1.0f + this.dmgToTrigFix(attacker, GameData.eSE_Damage, 0) + hpDmgToFix + this.stateDmgToTrigFix(attacker, target, GameData.eSE_Damage, 0));
			res.behead = hpDmgToFix > 0 ? 1 : 0;
		}

		//伤害加成、减免
		float damageTo = 1.f;
		float damageBy = 1.f;
		damageTo += (attacker.getFightPropF(BaseRole.EPROPID_DMGTO) + this.getMonsterDMGTO(attacker, target.race));
		damageBy -= (target.getFightPropF(BaseRole.EPROPID_DMGBY) + this.getMonsterDMGBY(target, attacker.race));
		val = val * damageTo * damageBy;
		val = val * (1.0f + this.getDamageFix(attacker.curHP, attacker.getMaxHP())); //特化公式伤害修正
		double reduce = this.dmgByTrigFix(target, skillType, crit ? 1 : 0); //受伤者伤害修正
		val = Math.max(0, val - reduce);
		val = val * target.getClassReduce(attacker); //职业伤害减免
		float r = GameRandom.getRandFloat((float)arg2, (float)arg3);
		val =  Math.max(val, lvl * r);
		return val;
	}
	
	private double getShellValue(BaseRole attacker, BaseRole target, SBean.DamageResult res, SBean.SubDamageCFGS subDamage, boolean crit, double val, SBean.CommonSkillCFGS commonSkillCfg)
	{
		int lvl		= attacker.level;
		int shell	= target.getFightProp(BaseRole.EPROPID_SHELL);					//护体
		
		double arg1 = commonSkillCfg.shell.get(0);
		double arg2 = commonSkillCfg.shell.get(1);
		double F1 = arg1 * lvl + arg2;
		
		double shellVal = val * (shell / F1);
		if (shellVal > target.getMaxHP() * 0.25)
			shellVal = (target.getMaxHP() * 0.25);
		
		return shellVal;
	}
	
	private void setSkillBlood(BaseRole attacker, BaseRole target, SBean.DamageResult res, SBean.SubDamageCFGS subDamage, boolean crit, double val, SBean.CommonSkillCFGS commonSkillCfg)
	{
		double sbdVal = 0;
		int sbd		= attacker.getFightProp(BaseRole.EPROPID_SBD);					//吸血
		
		int tlvl	= target.level;
		
		double arg1 = commonSkillCfg.sbd.get(0);
		double arg2 = commonSkillCfg.sbd.get(1);

		double F1 = arg1 * tlvl + arg2;
		sbdVal = val * (sbd / F1);
		if (sbdVal > attacker.getMaxHP() * 0.25)
			sbdVal = (float) (attacker.getMaxHP() * 0.25);

		if (sbdVal > 0)
			res.suckBlood = Math.max(1, (int) sbdVal);
		else
			res.suckBlood = (int) sbdVal;
	}
	
	private void setSkillHealValue(BaseRole attacker, BaseRole target, SBean.DamageResult res, SBean.SubDamageCFGS subDamage, boolean crit)
	{
		double val = 0;
		int heg 	= attacker.getFightProp(BaseRole.EPROPID_HEALGAIN);			//治疗加成
		double beHeg	= target.getFightPropF(BaseRole.EPROPID_BEHEALGAIN);			//被治疗加成
		int realmLevel = this.realmLvl;
		
		val = (subDamage.arg2 * (1 + subDamage.realmAdd * realmLevel) + heg) * (1.0 + beHeg);
		if (crit)
		{
			val = val * 2.0 * (1.0 + this.dmgToTrigFix(attacker, GameData.eSE_Buff, 1) + this.stateDmgToTrigFix(attacker, target, GameData.eSE_Buff, 1));
			res.crit = 1;
		}
		else
		{
			val = val * (1.0 + this.dmgToTrigFix(attacker, GameData.eSE_Buff, 0) + this.stateDmgToTrigFix(attacker, target, GameData.eSE_Buff, 0));
		}
		res.damage = (int) val;
	}
	
	//是否命中
	int isAtr(BaseRole attacker, BaseRole target, SBean.SubDamageCFGS subDamage)
	{
		if (subDamage.atrType == MapManager.ATTACK_ATR || target.isInvincible())
			return MapManager.ATTACK_ATR;

		SBean.CommonSkillCFGS commonSkillCfg = GameData.getInstance().getCommonCFG().skill;
		if (commonSkillCfg == null)
			return MapManager.ATTACK_DODGE;
		double arg1 = commonSkillCfg.atr.get(0);
		double arg2 = commonSkillCfg.atr.get(1);
		double arg3 = commonSkillCfg.atr.get(2);

		int a_atr = attacker.getFightProp(BaseRole.EPROPID_ATR);
		int t_ctr = attacker.trigArmorIgnoreCtr() ? 0 : target.getFightProp(BaseRole.EPROPID_CTR);
		int t_lvl = target.level;

		float odds = (float) (arg1 + (a_atr - t_ctr) / (arg2 * t_lvl + arg3));
		if (this.id == Skill.DIY_SKILL_ID)
		{
			SBean.DBDIYSkillData diySkillData = attacker.getDIYSkill();
			if (diySkillData != null)
				odds += diySkillData.atrDecrease;
		}

		float min = 0.66f;
		float max = 1.0f;

		if (attacker.ctrlType == BaseRole.ECTRL_TYPE_PLAYER && target.ctrlType == BaseRole.ECTRL_TYPE_PLAYER)
			min = 0.33f;

		if (odds < min)
			odds = min;
		if (odds > max)
			odds = max;

		float rnd = GameRandom.getRandom().nextFloat();
		if (rnd <= odds) //命中
			return MapManager.ATTACK_ATR;

		rnd = GameRandom.getRandom().nextFloat();
		double deflect = attacker.getFightPropF(BaseRole.EPROPID_DEFLECT);
		if (rnd <= deflect) //偏斜
			return MapManager.ATTACK_DEFLECT;

		return MapManager.ATTACK_DODGE;
	}

	//是否暴击
	boolean isCrit(BaseRole attacker, BaseRole target, SBean.SubDamageCFGS subDamage)
	{
		if (subDamage.acrType == 1)
			return true;

		float odds = 0;
		SBean.CommonSkillCFGS commonSkillCfg = GameData.getInstance().getCommonCFG().skill;
		if (this.baseFixCfg.type == GameData.eSE_Buff) //治疗
		{
			double arg1 = commonSkillCfg.hel.get(0);
			double arg2 = commonSkillCfg.hel.get(1);

			int lvl = attacker.level;
			int healA = attacker.getFightProp(BaseRole.EPROPID_HEALA);

			double F1 = arg1 * lvl + arg2;
			odds = (float) (healA / F1);
		}
		else
		{
			double arg1 = commonSkillCfg.cri.get(0);
			double arg2 = commonSkillCfg.cri.get(1);
			double arg3 = commonSkillCfg.cri.get(2);

			int a_acrN = attacker.getFightProp(BaseRole.EPROPID_ACRN);
			int t_tou = attacker.trigArmorIgnoreTou() ? 0 : target.getFightProp(BaseRole.EPROPID_TOU);
			int t_lvl = target.level;

			if (this.id == Skill.DIY_SKILL_ID)
			{
				SBean.DBDIYSkillData diySkillData = attacker.getDIYSkill();
				if (diySkillData != null)
					odds += diySkillData.acrDecrease;
			}

			odds = (float) (arg1 + (a_acrN - t_tou) / (arg2 * t_lvl + arg3));
			float min = 0.01f;
			float max = 0.5f;
			if (attacker.ctrlType == BaseRole.ECTRL_TYPE_PLAYER && target.ctrlType == BaseRole.ECTRL_TYPE_PLAYER)
				max = 0.33f;

			if (odds < min)
				odds = min;
			if (odds > max)
				odds = max;
		}

		return GameData.checkRandom(odds);
	}

	//每次伤害结算时修正
	double dmgToTrigFix(BaseRole attacker, int damage, int isCrit)
	{
		TrigerAiCluster cluster = attacker.trigerAiMgr.trigerClusters.get(GameData.TRIG_EVENT_DGMTOFIX);
		if (cluster == null)
			return 0;

		double sum = 0;
		for (TrigerAi trigerAi : cluster.ais.values())
		{
			if (trigerAi.onDmgToFixTrig(damage, isCrit))
				sum += getDmgToTrigFix(trigerAi.bCfg);
		}
		return sum;
	}

	//对血量低于X%的单位造成伤害结算时
	double hpDmgToTrigFix(BaseRole attacker, BaseRole target)
	{
		TrigerAiCluster cluster = attacker.trigerAiMgr.trigerClusters.get(GameData.TRIG_EVENT_DMGTO_THP);
		if (cluster == null)
			return 0;

		int lowHpDmgType = target.getEventLowHpDmgType();
		int hp = (int) ((float)target.curHP / (float)target.getMaxHP() * 10000.0f);
		
		double sum = 0;
		for (TrigerAi trigerAi : cluster.ais.values())
		{
			if (trigerAi.onHpDmgToFixTrig(hp, lowHpDmgType))
				sum += getDmgToTrigFix(trigerAi.bCfg);
		}

		return sum;
	}

	//对持有指定状态的单位进行伤害结算时
	double stateDmgToTrigFix(BaseRole attacker, BaseRole target, int damage, int isCrit)
	{
		TrigerAiCluster cluster = attacker.trigerAiMgr.trigerClusters.get(GameData.TRIG_EVENT_DMGTO_STATE);
		if (cluster == null)
			return 0;

		Collection<Integer> states = target.curStates.keySet();
		if (states.isEmpty())
			return 0;

		for (TrigerAi trigerAi : cluster.ais.values())
		{
			if (trigerAi.onStateDmgToFixTrig(damage, isCrit, states))
				return getDmgToTrigFix(trigerAi.bCfg);
		}

		return 0f;
	}

	double getDmgToTrigFix(SBean.TrigBehaviorCFGS bCfg)
	{
		if (bCfg.param1 == 1)
			return bCfg.param2 / 10000.0;
		else if (bCfg.param1 == 2)
			return GameRandom.getRandInt(bCfg.param2, bCfg.param3) / 10000.0;

		return 0;
	}

	//受伤者伤害修正
	private double dmgByTrigFix(BaseRole target, int damage, int isCrit)
	{
		double reduce = 0;
		TrigerAiCluster cluster = target.trigerAiMgr.trigerClusters.get(GameData.TRIG_EVENT_DGMTOFIX);
		if (cluster == null)
			return 0;

		for (TrigerAi trigerAi : cluster.ais.values())
		{
			if (!trigerAi.isActived())
				continue;

			if (damage != trigerAi.eCfg.param1 || (isCrit != trigerAi.eCfg.param2 && trigerAi.eCfg.param2 != -1))
				continue;

			if (trigerAi.bCfg.behaviorType != GameData.TRIG_BEHAVIOR_DMGBYFIX)
				continue;

			if (trigerAi.checkRandom())
				reduce = target.getMaxHP() * trigerAi.bCfg.param1 / 10000.0;

			break;
		}

		return reduce;
	}

	private double getMonsterDMGTO(BaseRole attacker, int race)
	{
		int propID = GameData.getInstance().getMonsterDMGTOPropID(race);
		if (propID <= 0)
			return 0;

		return attacker.getFightPropF(propID);
	}

	private double getMonsterDMGBY(BaseRole target, int race)
	{
		int propID = GameData.getInstance().getMonsterDMGBYPropID(race);
		if (propID <= 0)
			return 0;

		return target.getFightPropF(propID);
	}

	//心法修正系数
	private double getSpiritFix(BaseRole attacker, BaseRole target)
	{
		if (!checkRoleFix(attacker, target))
			return 0.0;

		int diff = (int) (attacker.getSpiritTotalLays() * (1.0 + 0.01 * attacker.getFightProp(BaseRole.EPROPID_MASTERC)) - target.getSpiritTotalLays() * (1.0 + 0.01 * target.getFightProp(BaseRole.EPROPID_MASTERC)));

		return GameData.getInstance().getSpiritFix(diff);
	}

	private double getWeaponFix(BaseRole attacker, BaseRole target)
	{
		if (!checkRoleFix(attacker, target))
			return 0.0;
		
		int diff = (int) (attacker.getWeaponTotalLvls() * (1.0 + 0.01 * attacker.getFightProp(BaseRole.EPROPID_MASTERW)) - target.getWeaponTotalLvls() * (1.0 + 0.01 * target.getFightProp(BaseRole.EPROPID_MASTERW)));

		return GameData.getInstance().getWeaponFix(diff);
	}

	private boolean checkRoleFix(BaseRole attacker, BaseRole target)
	{
		return !(attacker.getEntityType() != GameData.ENTITY_TYPE_PLAYER && attacker.getEntityType() != GameData.ENTITY_TYPE_PET) && !(target.getEntityType() != GameData.ENTITY_TYPE_PLAYER && target.getEntityType() != GameData.ENTITY_TYPE_PET);

	}

	//特化公式伤害修正
	private double getDamageFix(int curHP, int maxHP)
	{
		if (this.dmgFixInfo == null)
			return 0.0;

		double fix = 0;
		double hp = (double) curHP / (double) maxHP;
		if (this.dmgFixInfo.condition == 2) //每损失xx百分比血量
		{
			int count = (int) ((100 - hp * 100) / this.dmgFixInfo.arg1);
			fix = count * this.dmgFixInfo.value;
		}
		else if (this.dmgFixInfo.condition == 1) //血量低于固定值
		{
			if (hp <= this.dmgFixInfo.arg1 / 100.0)
				fix = this.dmgFixInfo.value;
		}

		return fix;
	}

	//伤害减免
	int recharge(BaseRole target, int arg, int value)
	{
		
		int val = 1000;
		return val;
	}

	//伤害吸收
	int reduction(BaseRole target, int arg, int value)
	{
		//TODO
		int val = 1000;
		return val;
	}

	//伤害反弹
	int ironMaiden(BaseRole attacker, BaseRole target, int arg, int value)
	{
		//TODO
		int val = 1000;
		return val;
	}
}
