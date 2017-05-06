package i3k.gmap;

import java.util.HashMap;
import java.util.Map;

import i3k.gs.GameData;

public class TrigerAiMgr
{
	Map<Integer, TrigerAiCluster> trigerClusters;
	Map<Integer, Integer> allTrigerEventType;
	int dmgByCount;
	int dmgToCount;
	int critdmgByCount;
	int critdmgToCount;
	int dodgeCount;
	int missCount;
	int healCount;
	int critHealCount;
	Map<Integer, Integer> buffdmgByCount;
	Map<Integer, BuffStep> buffSteps;
	Map<Integer, SkillCount> skillsCount;
	Map<Integer, WeaponStep> weaponSteps;
	
	public static class SkillCount
	{
		Map<Integer, Integer> counts;
		SkillCount()
		{
			counts = new HashMap<>();
		}
		
		void postCount(int useType)
		{
			counts.compute(useType, (k,v) -> v == null ? 1 : v + 1);
			if(useType != GameData.SKILL_USE_TYPE_ALL)
				counts.compute(GameData.SKILL_USE_TYPE_ALL, (k,v) -> v == null ? 1 : v + 1);
		}
		
		int getCount(int useType)
		{
			return counts.getOrDefault(useType, 0);
		}
	}
	
	public static class WeaponStep
	{
		private Map<Integer, Integer> steps;
		
		WeaponStep()
		{
			steps = new HashMap<>(); 
		}
		
		WeaponStep(Map<Integer, Integer> steps)
		{
			if(steps != null)
			{
				this.steps = steps;
			}
			else
			{
				this.steps = new HashMap<>();
			}
		}
		
		void postCount(int type)
		{
			steps.compute(type, (k,v) -> v == null ? 1 : v + 1);
			if(type != GameData.WEAPON_MOTIVATE_ALL)
			{
				steps.compute(GameData.WEAPON_MOTIVATE_ALL, (k,v) -> v == null ? 1 : v + 1);
			}
		}
		
		Map<Integer, Integer> getSteps()
		{
			return new HashMap<>(steps);
		}
		
		int getStep(int type)
		{
			return steps.getOrDefault(type, 0);
		}
	}
	
	TrigerAiMgr()
	{
		trigerClusters = new HashMap<>();
		allTrigerEventType = new HashMap<>();
		buffdmgByCount = new HashMap<>();
		buffSteps = new HashMap<>();
		skillsCount = new HashMap<>();
		weaponSteps = new HashMap<>();
	}

	//造成伤害次数
	void postEvent(int damageType, boolean crit)
	{
		if (damageType == GameData.eSE_Damage)
		{
			this.dmgToCount++;
			if (crit)
				this.critdmgToCount++;
		}
		else if (damageType == GameData.eSE_Buff)
		{
			this.healCount++;
			if (crit)
				this.critHealCount++;
		}

	}

	//受到伤害次数
	void postEvent(int buffID, int count, boolean crit)
	{
		if (buffID == 0)
		{
			this.dmgByCount += count;
			if (crit)
				this.critdmgByCount += count;
		}
		else
			this.buffdmgByCount.compute(buffID, (k, v) -> v == null ? v = count : v + count);
	}

	void postEvent(int buffID, int type, int value)
	{
		BuffStep buffStep = this.buffSteps.get(buffID);
		if (buffStep == null)
		{
			buffStep = new BuffStep();
			this.buffSteps.put(buffID, buffStep);
		}
		buffStep.value = value;
		buffStep.steps.compute(type, (k, v) -> v == null ? 1 : ++v);
	}

	void postDodgeEvent()
	{
		this.dodgeCount++;
	}

	void postMissEvent()
	{
		this.missCount++;
	}
	
	void postSkillEvevt(int damageType, int useType)
	{
		SkillCount sc = this.skillsCount.get(damageType);
		if(sc == null)
		{
			sc = new SkillCount();
			this.skillsCount.put(damageType, sc);
		}
		sc.postCount(useType);
		
		if(damageType != -1)
		{
			SkillCount sca = this.skillsCount.get(-1);
			if(sca == null)
			{
				sca = new SkillCount();
				this.skillsCount.put(-1, sca);
			}
			sca.postCount(useType);
		}
	}
	
	int getUseSkillCount(int damageType, int useType)
	{
		SkillCount sc = this.skillsCount.get(damageType);
		return sc == null ? 0 : sc.getCount(useType);
	}
	
	void postWeaponEvent(int weaponID, int type)
	{
		postWeaponEventImpl(weaponID, type);
		
		if(weaponID != -1)
			postWeaponEventImpl(-1, type);
	}
	
	private void postWeaponEventImpl(int weaponID, int type)
	{
		WeaponStep ws = this.weaponSteps.get(weaponID);
		if(ws == null)
		{
			ws = new WeaponStep();
			this.weaponSteps.put(weaponID, ws);
		}
		ws.postCount(type);
	}
	
	int getWeaponMotivateCnt(int weaponID, int type)
	{
		WeaponStep ws = this.weaponSteps.get(weaponID);
		if(ws == null)
			return 0;
		
		return ws.getStep(type);
	}
	
	Map<Integer, Integer> getWeaponSteps(int weaponID)
	{
		WeaponStep ws = this.weaponSteps.get(weaponID);
		return ws == null ? null : ws.getSteps();
	}
	
	void removeTrigerAi(int id)
	{
		Integer eventType = this.allTrigerEventType.remove(id);
		if (eventType == null)
			return;

		TrigerAiCluster cluster = this.trigerClusters.get(eventType);
		if (cluster == null)
			return;

		cluster.ais.remove(id);
		if (cluster.ais.isEmpty())
			this.trigerClusters.remove(eventType);
	}

	int getBuffDamageCount(int buffID)
	{
		Integer count = this.buffdmgByCount.get(buffID);

		return count == null ? 0 : count;
	}

	int getBuffChangeCount(int buffID, int type)
	{
		BuffStep buffStep = this.buffSteps.get(buffID);
		if (buffStep == null)
			return 0;

		return buffStep.getStep(type);
	}
	
	boolean aiTrig(BaseRole role, int eventID, int param1, int param2)
	{
		TrigerAiCluster aiCluster = this.trigerClusters.get(eventID);
		if (aiCluster == null)
			return false;

		for(TrigerAi ai: aiCluster.ais.values())
		{
			if(ai.tryTrig(this, role, param1, param2))
				return true;
		}
		
		return false;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class BuffStep
	{
		Map<Integer, Integer> steps;
		int value;

		BuffStep()
		{
			steps = new HashMap<>();
		}

		BuffStep(BuffStep step)
		{
			if (step != null)
			{
				this.value = step.value;
				this.steps = new HashMap<>(step.steps);
			}
			else
				steps = new HashMap<>();
		}

		int getStep(int type)
		{
			switch (type)
			{
			case GameData.BUFF_CHANGETYPE_ADD:
				return this.steps.getOrDefault(type, 0);
			case GameData.BUFF_CHANGETYPE_REMOVE:
				return this.steps.getOrDefault(type, 0);
			case GameData.BUFF_CHANGETYPE_VALUECHANGE:
				return this.value;
			default:
				break;
			}

			return 0;
		}
	}
}
