package i3k.gmap;

import i3k.SBean;
import i3k.gmap.BaseRole.Buff;
import i3k.gmap.TrigerAiMgr.BuffStep;
import i3k.gs.GameData;
import i3k.util.GameTime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TrigerAi
{
	int id;
	SBean.AiTrigerCFGS aiTrigCfg;
	SBean.TrigEventCFGS eCfg;
	SBean.TrigBehaviorCFGS bCfg;
	long coolDown;

	Buff buff;
	int spiritEffectID;
	int count;
	long preTrigTime;

	TrigerAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
	{
		this.id = id;
		this.aiTrigCfg = aiTrigCfg;
		this.spiritEffectID = spiritEffectID;
		this.buff = buff;
		this.coolDown = (long) aiTrigCfg.coolDown;
	}

	TrigerAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
	{
		this.eCfg = eventCfg.kdClone();
		this.bCfg = behaviorCfg.kdClone();
		switch (this.eCfg.eventType)
		{
		case GameData.TRIG_EVENT_INTERVAL: //每隔一段时间
			this.preTrigTime = GameTime.getTimeMillis();
			break;
		default:
			break;
		}

		this.coolDown = 0;
		return this;
	}

	boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
	{
		return false;
	}
	
	boolean tryTrig(TrigerAiMgr mgr, BaseRole role, int param1, int param2)
	{
		if(canTrig(mgr, param1, param2))
		{
			role.trigBehavior(this.bCfg);
			return true;
		}
		return false;
	}
	
	int getSelfBuffID()
	{
		return this.buff == null ? -1 : this.buff.id;
	}

	boolean isActived()
	{
		return this.coolDown < GameTime.getTimeMillis();
	}

	void setCoolTime()
	{
		this.coolDown = GameTime.getTimeMillis() + this.aiTrigCfg.coolDown;
	}

	boolean check(BaseRole hoster)
	{
//		TrigerAiMgr mgr = hoster.trigerAiMgr;
		switch (this.eCfg.eventType)
		{
		case GameData.TRIG_EVENT_EDEADCOUNT:
		case GameData.TRIG_EVENT_SDMGBY_VALUE:
		case GameData.TRIG_EVENT_ENEMYARROUND:
		case GameData.TRIG_EVENT_LOSEHP:
		case GameData.TRIG_EVENT_IDLE:
		case GameData.TRIG_EVENT_SHPLOWER: 			//自身生命值低于指定值
			return this.onSelfHpLow(hoster.curHP, hoster.getMaxHP());
		case GameData.TRIG_EVENT_INTERVAL: 			//每隔一段时间
			return this.onIntervalTrig();
		default:
			break;
		}

		return false;
	}

	boolean checkRandom()
	{
		if(GameData.checkRandom(this.aiTrigCfg.odds))
		{
			this.setCoolTime();
			return true;
		}

		return false;
	}

	boolean onIntervalTrig()
	{
		if (!this.isActived())
			return false;

		long now = GameTime.getTimeMillis();
		if (now > this.preTrigTime + this.eCfg.param1 && this.checkRandom())
		{
			this.preTrigTime = now;
			return true;
		}

		return false;
	}
	
	boolean onSelfHpLow(int curHP, int maxHP)
	{
		if (!this.isActived())
			return false;

		if (this.eCfg.param1 == GameData.VALUE_TYPE_FIXED)
		{
			if (curHP < this.eCfg.param2)
				return this.checkRandom();
		}
		else if (this.eCfg.param1 == GameData.VALUE_TYPE_PERCENT)
		{
			if (curHP < this.eCfg.param2 / 10000.0f * maxHP)
				return this.checkRandom();
		}

		return false;
	}

	int getDamageCount(TrigerAiMgr mgr)
	{
		if (this.eCfg.eventType != GameData.TRIG_EVENT_DMGTOCOUNT_D)
			return 0;

		if (this.eCfg.param3 == GameData.eSE_Damage)
		{
			switch (this.eCfg.param2)
			{
			case 0:
				return mgr.dmgToCount - mgr.critdmgToCount;
			case 1:
				return mgr.critdmgToCount;
			default:
				break;
			}

			return mgr.dmgToCount;
		}
		else if (this.eCfg.param3 == GameData.eSE_Buff)
		{
			switch (this.eCfg.param2)
			{
			case 0:
				return mgr.healCount - mgr.critHealCount;
			case 1:
				return mgr.critHealCount;
			default:
				break;
			}

			return mgr.healCount;
		}
		return 0;
	}

	//每次伤害结算时修正  attacker 伤害结算触发
	boolean onDmgToFixTrig(int damage, int isCrit)
	{
		if (!this.isActived())
			return false;

		if (this.bCfg.behaviorType != GameData.TRIG_BEHAVIOR_DMGTOFIX)
			return false;

		if (damage == this.eCfg.param1 && (isCrit == this.eCfg.param2 || this.eCfg.param2 == -1))
		{
			if (this.checkRandom())
				return true;
		}

		return false;
	}

	//对血量低于X%的单位造成伤害结算时  attacker 伤害结算触发
	boolean onHpDmgToFixTrig(int hp, int lowHpDmgType)
	{
		if (!this.isActived())
			return false;

		if (this.bCfg.behaviorType != GameData.TRIG_BEHAVIOR_DMGTOFIX)
			return false;

		if (hp > this.eCfg.param2)
			return false;

		if((this.eCfg.param1 & lowHpDmgType ) != 0)
		{
			if (this.checkRandom())
				return true;
		}

		return false;
	}

	//每次伤害结算时修正  attacker 伤害结算触发
	boolean onStateDmgToFixTrig(int damage, int isCrit, Collection<Integer> states)
	{
		if (!this.isActived())
			return false;

		if (!states.contains(this.eCfg.param3))
			return false;

		if (damage != this.eCfg.param1)
			return false;

		return !(this.eCfg.param2 != -1 && this.eCfg.param2 != isCrit) && this.checkRandom();

	}
	
	public static class SkillCountTrigAi extends TrigerAi
	{
		SkillCountTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		SkillCountTrigAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
		{
			super.createNew(mgr, eventCfg, behaviorCfg);
			this.count = mgr.getUseSkillCount(eCfg.param1, eCfg.param3);
			return this;
		}
		
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;
			
			int mgrCount = mgr.getUseSkillCount(this.eCfg.param3, this.eCfg.param1);
			if(mgrCount - this.count >= this.eCfg.param2)
			{
				this.count = mgrCount;
				return this.checkRandom();
			}
			
			return false;
		}
	}
	
	// attacker 触发行为
	public static class OnDmgToTrigAi extends TrigerAi
	{
		OnDmgToTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		OnDmgToTrigAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
		{
			super.createNew(mgr, eventCfg, behaviorCfg);
			this.count = this.getDamageCount(mgr);
			return this;
		}
		
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;

			int mgrCount = this.getDamageCount(mgr);

			if (mgrCount - this.count >= this.eCfg.param1)
			{
				this.count = mgrCount;
				return this.checkRandom();
			}
			return false;
		}
	}
	
	//受到直接、间接伤害触发  target 触发行为
	public static class OnDmgByTrigAi extends TrigerAi
	{
		OnDmgByTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		OnDmgByTrigAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
		{
			super.createNew(mgr, eventCfg, behaviorCfg);
			
			if(this.eCfg.eventType == GameData.TRIG_EVENT_DMGBYCOUNT_D)
			{
				if (this.eCfg.param2 == 0)
					this.count = mgr.dmgByCount - mgr.critdmgByCount;
				else if (this.eCfg.param2 == 1)
					this.count = mgr.critdmgByCount;
				else if (this.eCfg.param2 == -1)
					this.count = mgr.dmgByCount;
			}
			else if(this.eCfg.eventType == GameData.TRIG_EVENT_DMGBYCOUNT_I)
			{
				int buffID = eCfg.param1 == -1 ? this.getSelfBuffID() : eCfg.param1;
				this.count = mgr.getBuffDamageCount(buffID);
			}
			return this;
		}
		
		//param1 buffID
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;

			if (this.eCfg.eventType == GameData.TRIG_EVENT_DMGBYCOUNT_D && param1 == 0)
			{
				int c = mgr.dmgByCount - this.count;
				if (this.eCfg.param2 == 0)
					c = mgr.dmgByCount - mgr.critdmgByCount - this.count;
				else if (this.eCfg.param2 == 1)
					c = mgr.critdmgByCount - this.count;

				if (c >= this.eCfg.param1)
				{
					this.count = mgr.dmgByCount;
					if (this.eCfg.param2 == 0)
						this.count = mgr.dmgByCount - mgr.critdmgByCount;
					else if (this.eCfg.param2 == 1)
						this.count = mgr.critdmgByCount;

					return this.checkRandom();
				}
			}
			else if (this.eCfg.eventType == GameData.TRIG_EVENT_DMGBYCOUNT_I && param1 > 0)
			{
				int bid = eCfg.param1 == -1 ? this.getSelfBuffID() : eCfg.param1;
				int mgrCount = mgr.getBuffDamageCount(bid);
				int count = mgrCount - this.count;
				if (count >= this.eCfg.param2)
				{
					this.count = mgrCount;
					return this.checkRandom();
				}
			}

			return false;
		}
	}
	
	//hp :血量万分比  受到伤害 血量低触发	 target 触发行为
	public static class HPLowDmgByTrigAi extends TrigerAi
	{
		HPLowDmgByTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		//param1 hp, param2 crit
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;

			if (param1 > this.eCfg.param1)
				return false;
			
			if(param2 != this.eCfg.param2 && this.eCfg.param2 != -1)
				return false;
			
			return this.checkRandom();
		}
	}
	
	//BUFF 添加/移除/数值变化 触发
	public static class BuffChangeTrigAi extends TrigerAi
	{
		BuffStep buffStep;
		
		BuffChangeTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		BuffChangeTrigAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
		{
			super.createNew(mgr, eventCfg, behaviorCfg);
			int buffID = eCfg.param2 == -1 ? this.getSelfBuffID() : eCfg.param2;
			this.buffStep = new BuffStep(mgr.buffSteps.get(buffID));
			return this;
		}
		
		//param1 type, param2 buffID
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;

			if (this.eCfg.param1 == param1)
			{
				int bid = eCfg.param2 == -1 ? this.getSelfBuffID() : eCfg.param2;
				if (bid != param2)
					return false;

				if (this.buffStep == null)
					this.buffStep = new BuffStep(mgr.buffSteps.get(param2));

				if (this.buffStep == null)
					return false;

				int count = mgr.getBuffChangeCount(param2, param1) - this.buffStep.getStep(param1);
				if (count > 0)
				{
					this.buffStep = new BuffStep(mgr.buffSteps.get(param2));
					return this.checkRandom();
				}
			}
			return false;
		}
	}
	
	//target 触发行为
	public static class DodgeTrigAi extends TrigerAi
	{
		DodgeTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		DodgeTrigAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
		{
			super.createNew(mgr, eventCfg, behaviorCfg);
			this.count = mgr.dodgeCount;
			
			return this;
		}
		
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;

			int c = mgr.dodgeCount - this.count;
			if (c >= this.eCfg.param1)
			{
				this.count = mgr.dodgeCount;
				return this.checkRandom();
			}

			return false;
		}
	}
	
	//attacker 触发行为
	public static class MissTrigAi extends TrigerAi
	{
		MissTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		MissTrigAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
		{
			super.createNew(mgr, eventCfg, behaviorCfg);
			this.count = mgr.missCount;
			return this;
		}
		
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;

			int c = mgr.missCount - this.count;
			if (c >= this.eCfg.param1)
			{
				this.count = mgr.missCount;
				if (this.checkRandom())
					return true;
			}

			return false;
		}
	}
	
	public static class SelfDeadTrigAi extends TrigerAi
	{
		SelfDeadTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			return this.isActived() && this.checkRandom();
		}
		
		boolean tryTrig(TrigerAiMgr mgr, BaseRole role, int param1, int param2)
		{
			if(canTrig(mgr, param1, param2))
			{
				role.addState(Behavior.EBGOTODEAD);
				role.trigBehavior(this.bCfg);
				return true;
			}
			
			return false;
		}
	}
	
	public static class WeaponMotivateTrigAi extends TrigerAi
	{
		int weaponID;
		int type;
		
		WeaponMotivateTrigAi(int id, SBean.AiTrigerCFGS aiTrigCfg, int spiritEffectID, Buff buff)
		{
			super(id, aiTrigCfg, spiritEffectID, buff);
		}
		
		WeaponMotivateTrigAi createNew(TrigerAiMgr mgr, SBean.TrigEventCFGS eventCfg, SBean.TrigBehaviorCFGS behaviorCfg)
		{
			super.createNew(mgr, eventCfg, behaviorCfg);
			this.weaponID = eCfg.param2;
			this.type = eCfg.param1;
			return this;
		}
		
		boolean canTrig(TrigerAiMgr mgr, int param1, int param2)
		{
			if (!this.isActived())
				return false;
			
			int mgrCount = mgr.getWeaponMotivateCnt(this.weaponID, this.type);
			if(mgrCount - this.count > 0)
			{
				this.count = mgrCount;
				return this.checkRandom();
			}
			
			return false;
		}
	}
}


class TrigerAiCluster
{
	Map<Integer, TrigerAi> ais;

	TrigerAiCluster()
	{
		ais = new HashMap<>();
	}
}