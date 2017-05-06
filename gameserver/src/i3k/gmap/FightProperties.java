package i3k.gmap;

import i3k.SBean;
import i3k.gs.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FightProperties
{
	
	static class FightPropertyValue
	{
		int fixValue;
		int percentValue;
		FightPropertyValue()
		{
			
		}
		
		FightPropertyValue(FightPropertyValue other)
		{
			this(other.fixValue, other.percentValue);
		}
		
		FightPropertyValue(int fixValue, int percentValue)
		{
			this.fixValue = fixValue;
			this.percentValue = percentValue;
		}
		
		int getFixValue()
		{
			return this.fixValue;
		}
		
		int getFinalValue()
		{
			return (int)(this.fixValue*(1.0 + this.percentValue/10000.0f));
		}
		
		int getFinalValue(int decrease)
		{
			return (int)((this.fixValue - decrease)*(1.0 + this.percentValue/10000.0f));
		}
		
		FightPropertyValue setFixValue(int fixValue)
		{
			this.fixValue = fixValue;
			return this;
		}
		
		FightPropertyValue setPercentValue(int percentValue)
		{
			this.percentValue = percentValue;
			return this;
		}
		
		FightPropertyValue addFixValue(int fixValue)
		{
			this.fixValue += fixValue;
			return this;
		}
		
		FightPropertyValue clear()
		{
			this.fixValue = 0;
			this.percentValue = 0;
			return this;
		}
		
		FightPropertyValue addPercentValue(int percentValue)
		{
			this.percentValue += percentValue;
			return this;
		}
		
		FightPropertyValue merge(FightPropertyValue other)
		{
			this.fixValue += other.fixValue;
			this.percentValue += other.percentValue;
			return this;
		}
		
		public String toString()
		{
			return "{ fixValue " + this.fixValue + " percentValue " + this.percentValue + " }";
		}
	}
	
	
	
	final static FightPropertyValue defaultValue = new FightPropertyValue();
	Map<Integer, FightPropertyValue> props = new HashMap<>();
	FightProperties()
	{
		
	}
	
	FightPropertyValue getProperty(int propertyId)
	{
		return props.getOrDefault(propertyId, defaultValue);
	}
	
	
	public String toString()
	{
		return "props " + this.props;
	}
//	int getFinalValue(int propertyId)
//	{
//		return props.getOrDefault(propertyId, defaultValue).getFinalValue();
//	}
//	
//	int getPropertyFixValue(int propertyId)
//	{
//		return props.getOrDefault(propertyId, defaultValue).fixValue;
//	}
//	
//	float getPropertyPercentValue(int propertyId)
//	{
//		return props.getOrDefault(propertyId, defaultValue).percentValue/10000.0f;
//	}
	
	void setPropertyFixValue(int propertyId, double fvalue)
	{
		int value = (int)fvalue;
		props.compute(propertyId, (k, v) -> v == null ? new FightPropertyValue(value, 0) : v.setFixValue(value));
	}
	
	void setPropertyPercentValue(int propertyId, double fvalue)
	{
		int value = (int)fvalue;
		props.compute(propertyId, (k, v) -> v == null ? new FightPropertyValue(value, 0) : v.setPercentValue(value));
	}
	
	void addPropertyFixValue(int propertyId, double fvalue)
	{
		int value = (int)fvalue;
		props.compute(propertyId, (k, v) -> v == null ? new FightPropertyValue(value, 0) : v.addFixValue(value));
	}
	
	void addPropertyPercentValue(int propertyId, double fvalue)
	{
		int value = (int)fvalue;
		props.compute(propertyId, (k, v) -> v == null ? new FightPropertyValue(0, value) : v.addPercentValue(value));
	}
	
	void clear()
	{
		props.clear();
	}
	
	void merge(FightProperties other)
	{
		for (Map.Entry<Integer, FightPropertyValue> e : other.props.entrySet())
		{
			int pid = e.getKey();
			FightPropertyValue pvalue = e.getValue();
			this.props.compute(pid, (k, v) -> v == null ? new FightPropertyValue(pvalue) : v.merge(pvalue));
		}
	}
	
	int updateFightPower()
	{
		return calcFixFightPower(this, FightPropertyValue::getFixValue);
	}
	
	int updatePetFightPower()
	{
		return calcPetFixFightPower(this, FightPropertyValue::getFixValue);
	}
	
	interface GetPropertyValue
	{
		int getValue(FightPropertyValue fpv);
	}
	
	static int calcFixFightPower(FightProperties props, GetPropertyValue getValueFuncObj)
	{
		double power = 0;
		for(SBean.PropertyCFGS p: GameData.getInstance().getAllPropertyCFGS())
		{
			if(p.rolePower == 0)
				continue;
			
			double value = getValueFuncObj.getValue(props.props.getOrDefault(p.id, defaultValue));
			if(p.valueType == 1)
				value /= 10000;
			
			power += value * p.rolePower;
		}
		
		return (int)power;
	}
	
	static int calcPetFixFightPower(FightProperties props, GetPropertyValue getValueFuncObj)
	{
		double power = 0;
		for(SBean.PropertyCFGS p: GameData.getInstance().getAllPropertyCFGS())
		{
			if(p.petPower == 0)
				continue;
			
			double value = getValueFuncObj.getValue(props.props.getOrDefault(p.id, defaultValue));
			if(p.valueType == 1)
				value /= 10000.0;
			
			power += value * p.petPower;
		}
		
		return (int)power;
	}
	
	public static int calcDBEquipFightPower(SBean.DBEquip equip)
	{
		FightProperties prop = new FightProperties();
		SBean.CommonCFGS commonCfg = GameData.getInstance().getCommonCFG();
		if (equip.durability >= 0 && equip.durability <= commonCfg.equip.disableValue)
			return 0;
		
		SBean.EquipCFGS equipCfgs = GameData.getInstance().getEquipCFG(equip.id);
		//基础属性值
		double baseAdd = equip.durability < 0 ? 0 : GameData.getInstance().getLegenOneBaseAdd(equip.legends.get(0));
		for(SBean.EquipBasePropCFGS baseArg:equipCfgs.baseProp)
		{
			double value = baseArg.value * (1 + baseAdd);
			prop.addPropertyFixValue(baseArg.type, value);
		}
		
		//附加属性值
		double addtionAdd = equip.durability < 0 ? 0 : GameData.getInstance().getLegendTwoAddtionAdd(equip.legends.get(1));
		for(int i=0; i<equip.addValues.size(); i++)
		{
			SBean.EquipAdditPropCFGS additProp = equipCfgs.additProp.get(i);
			if(additProp.type == GameData.EQUIP_ADDPROP_TYPE1)
			{
				double value = equip.addValues.get(i) * (1 + addtionAdd);
				prop.addPropertyFixValue(additProp.arg, value);
			}
		}
		
		PropRole.updateEquipLegendThreeProp(prop, equip, equipCfgs.type);
		return calcFixFightPower(prop, FightPropertyValue::getFixValue);
	}
	
	private static int calcSkillFightPower(SBean.DBSkill skill, SBean.DBSealData seal)
	{
		int power = 0;
		SBean.SkillCFGS skillCfg = GameData.getInstance().getSkillCFG(skill.id);
		if (skillCfg != null)
		{
			int skillLvl = skill.level + seal.skills.getOrDefault(skill.id, 0);
			skillLvl = skillLvl > skillCfg.lvlDatas.size() ? skillCfg.lvlDatas.size() : skillLvl;
			SBean.SkillLevelCFGS lvlCfg = GameData.getSkillLevelCFG(skillCfg, skillLvl);
			if (lvlCfg != null)
			{
				power += lvlCfg.common.skillPower;
				if (skill.bourn >= 0 && skill.bourn < lvlCfg.common.realmPower.size())
					power += lvlCfg.common.realmPower.get(skill.bourn);
			}
		}
		return power;
	}
	
	static int calcSkillsFightPower(Collection<SBean.DBSkill> skills, SBean.DBSealData seal)
	{
		int power = 0;
		for (SBean.DBSkill skill : skills)
		{
			power += calcSkillFightPower(skill, seal);
		}
		return power;
	}
	
	private static int calcSpiritFightPower(SBean.DBSpirit spirit)
	{
		int power = 0;
		SBean.SpiritCFGS spiritCfg = GameData.getInstance().getSpiritCFGS(spirit.id);
		if (spiritCfg != null)
		{
			for (int level = 0; level <= spirit.level; level++)
			{
				SBean.SpiritGrowUpCFGS growCfg = spiritCfg.growups.get(level);
				if (growCfg != null)
					power += growCfg.addPower;
			}				
		}
		return power;
	}
	
	static int calcSpiritsFightPower(Collection<SBean.DBSpirit> spirits)
	{
		int power = 0;
		for (SBean.DBSpirit spirit : spirits)
		{
			power += calcSpiritFightPower(spirit);
		}
		return power;
	}
	
	static int calcEquipPower(int classType, SBean.DBSealData seal)
	{
		int power = 0;
		if(!seal.skills.isEmpty())
		{
			SBean.CLassTransformCFGS ctc = GameData.getInstance().getClassTrabsformCFGS(classType);
			if(ctc == null)
				return power;

			double N1 = GameData.getInstance().getCommonCFG().fightPower.seal.get(0);
			double N2 = GameData.getInstance().getCommonCFG().fightPower.seal.get(1);
			double N3 = GameData.getInstance().getCommonCFG().fightPower.seal.get(2);
			for(Map.Entry<Integer, Integer> e: seal.skills.entrySet())
			{
				int sid = e.getKey();
				int upLvl = e.getValue();
				int transfromLevel = ctc.skills.getOrDefault(sid, 0);
				
				power += (int) (upLvl * N1 * (N2 + transfromLevel * N3));
			}	
		}
		return power;
	}
	
	static int calcArmorTalentPower(SBean.ArmorFightData curArmor)
	{
		int power = 0;
		if(curArmor != null)
		{
			for(Map.Entry<Integer, Integer> e: curArmor.talentPoint.entrySet())
			{
				final SBean.ArmorTalentCFGS arc = GameData.getInstance().getArmorTalentCFGS(curArmor.id, e.getKey());
				if(arc == null)
					continue;
				
				power += PropFightRole.getListValue(arc.powers, e.getValue()) ;
			}
		}
		
		return power;
	}
	
	static int calcHorseSkillsPower(SBean.HorseInfo info, Map<Integer, Integer> allHorseSkills)
	{
		int power = 0;
		if(info != null)
		{
			SBean.HorseCFGS horseCfg = GameData.getInstance().getHorseCFGS(info.id);
			if(horseCfg != null)
			{
				Set<Integer> skills = new HashSet<>(info.curHorseSkills.values());
				SBean.HorseGrowUpCGFS growCfg = GameData.getHorseGrowUpCGFS(horseCfg, info.star);
				if (growCfg != null)
					if (growCfg.bornOpen > 0 && horseCfg.bornSkill > 0)
						skills.add(horseCfg.bornSkill);
				
				for (int skillID : skills)
				{
					Integer skillLvl = allHorseSkills.get(skillID);
					if(skillLvl == null)
						continue;
					
					SBean.HorseSkillUpdateCFGS lvlCFG = GameData.getInstance().getHorseSkillLvlCFGS(skillID, skillLvl);
					power += lvlCFG.fightPower;
				}
			}
		}
		
		return power;
	}
	
	static int calcArmorRunesPower(SBean.ArmorFightData curArmor)
	{
		int power = 0;
		if(curArmor != null)
		{
			for(SBean.SoltData e: curArmor.soltGroupData)
			{
				if(e.unlocked == 0)
					continue;
				
				for(int runeID: e.solts)
				{
					if(runeID == 0)
						continue;
					
					SBean.RuneCFGS rc = GameData.getInstance().getRuneCFGS(runeID);
					if(rc == null)
						continue;
					
					power += rc.power;
				}
			}
		}
		
		return power;
	}
}
