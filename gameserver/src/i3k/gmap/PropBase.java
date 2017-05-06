package i3k.gmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import i3k.SBean;
import i3k.gs.GameData;

public class PropBase
{
	FightProperties allProperties = new FightProperties();
	public PropBase()
	{
		
	}
	
	public int getBaseProps(int propID)
	{
		return allProperties.getProperty(propID).getFinalValue();
	}
	
	//子类会重写
	public int getFinalProps(int propID)
	{
		return allProperties.getProperty(propID).getFinalValue();
	}
	
	public int getFinalProps(int propID, int fixDecrease)
	{
		return allProperties.getProperty(propID).getFinalValue(fixDecrease);
	}
	
	public void setFightPropFixValue(int propID, double fvalue)
	{
		this.allProperties.setPropertyFixValue(propID, fvalue);
	}
	
	public void setFightPropPercentValue(int propID, double fvalue)
	{
		this.allProperties.setPropertyPercentValue(propID, fvalue);
	}
	
	public void addFightPropFixValue(int propID, double fvalue)
	{
		this.allProperties.addPropertyFixValue(propID, fvalue);
	}
	
	public void addFightPropPercentValue(int propID, double fvalue)
	{
		this.allProperties.addPropertyPercentValue(propID, fvalue);
	}
	
	public void clearBuffProp()
	{
	}
	
	public void addBuffPropFixValue(int propID, double fvalue)
	{
	}
	
	public void addBuffPropPercentValue(int propID, double fvalue)
	{
	}
	
	public void clearSpProp()
	{
	}
	
	public void addSpPropFixValue(int propID, double fvalue)
	{
	}
	
	public void addSpPropPercentValue(int propID, double fvalue)
	{
	}
}

class PropFightRole extends PropBase
{
	FightProperties buffProperties = new FightProperties();
	FightProperties spProperties = new FightProperties();
	FightProperties armorFightProperties = new FightProperties();
	FightProperties horseFightProperties = new FightProperties();
	FightProperties weaponFightProperties = new FightProperties();
	FightProperties weaponUSkillProperties = new FightProperties();
	
	FightProperties.FightPropertyValue calcHelper = new FightProperties.FightPropertyValue();
	public PropFightRole()
	{
		
	}
	
	public int getFinalProps(int propID)
	{
		return getFinalProps(propID, 0);
	}
	
	public int getFinalProps(int propID, int fixDecrease)
	{
		this.calcHelper.clear();
		this.calcHelper.merge(allProperties.getProperty(propID));
		this.calcHelper.merge(buffProperties.getProperty(propID));
		this.calcHelper.merge(spProperties.getProperty(propID));
		this.calcHelper.merge(armorFightProperties.getProperty(propID));
		this.calcHelper.merge(horseFightProperties.getProperty(propID));
		this.calcHelper.merge(weaponFightProperties.getProperty(propID));
		this.calcHelper.merge(weaponUSkillProperties.getProperty(propID));
		
		return this.calcHelper.getFinalValue(fixDecrease);
//		return this.calcHelper.clear().merge(allProperties.getProperty(propID)).merge(buffProperties.getProperty(propID)).merge(spProperties.getProperty(propID)).
//				merge(armorFightProperties.getProperty(propID)).merge(horseFightProperties.getProperty(propID)).merge(weaponFightProperties.getProperty(propID)).getFinalValue(fixDecrease);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		FightProperties prop = new FightProperties();
		prop.merge(allProperties);
		prop.merge(buffProperties);
		prop.merge(spProperties);
		prop.merge(armorFightProperties);
		prop.merge(horseFightProperties);
		prop.merge(weaponFightProperties);
		prop.merge(weaponUSkillProperties);
		
		sb.append("\r\n");
		sb.append("------------------------------------");
		for(SBean.PropertyCFGS p: GameData.getInstance().getAllPropertyCFGS())
		{
			int value = prop.getProperty(p.id).getFinalValue();
			if(value == 0)
				continue;
			
			sb.append("\r\n");
			sb.append("propID " + p.id + " " + p.desc + " value " + value);
		}
		return sb.toString();
	}
	
	public void clearBuffProp()
	{
		this.buffProperties.clear();
	}
	
	public void addBuffPropFixValue(int propID, double fvalue)
	{
		this.buffProperties.addPropertyFixValue(propID, fvalue);
	}
	
	public void addBuffPropPercentValue(int propID, double fvalue)
	{
		this.buffProperties.addPropertyPercentValue(propID, fvalue);
	}
	
	public void clearSpProp()
	{
		this.spProperties.clear();
	}
	
	public void addSpPropFixValue(int propID, double fvalue)
	{
		this.spProperties.addPropertyFixValue(propID, fvalue);
	}
	
	public void addSpPropPercentValue(int propID, double fvalue)
	{
		this.spProperties.addPropertyPercentValue(propID, fvalue);
	}
	
	public void updateArmorFightProperties(SBean.ArmorFightData curArmor)
	{
		this.armorFightProperties.clear();
		if(curArmor == null)
			return;
		
		updateArmorTalentProperties(this.armorFightProperties, curArmor);
		updateArmorSoltProperties(this.armorFightProperties, curArmor);
	}
	
	public void updateHorseFightProperties(SBean.HorseInfo info, Map<Integer, Integer> allHorseSkills)
	{
		this.horseFightProperties.clear();
		
		if(info != null)
			updateCurHorseSkillPropperties(this.horseFightProperties, info, allHorseSkills);
	}
	
	public void updateWeaponFightProperties(SBean.DBWeapon dbWeapon)
	{
		this.weaponFightProperties.clear();
		if(dbWeapon != null)
			updateWeaponFightProperties(this.weaponFightProperties, dbWeapon);
	}
	
	public void updateWeaponUSkillProperties(Map<Integer, Boolean> uniqueSkills)
	{
		this.weaponUSkillProperties.clear();
		updateWeaponUSkillProperties(this.weaponUSkillProperties, uniqueSkills);
	}
	
	static void updateArmorLevelProperties(FightProperties props, SBean.ArmorFightData curArmor)
	{
		final SBean.ArmorLevelCFGS alc = GameData.getInstance().getArmorLevelCFGS(curArmor.id, curArmor.level);
		if(alc == null)
			return;
		
		final SBean.ArmorRankCFGS arc = GameData.getInstance().getArmorRankCFGS(curArmor.id, curArmor.rank);
		double add = arc == null ? 0 : arc.propertyRate;
		for(SBean.AttrCFGS attr: alc.properties)
			props.addPropertyFixValue(attr.id, attr.value * (1.0 + add));
	}
	
	static void updateArmorRankProperties(FightProperties props, SBean.ArmorFightData curArmor)
	{
		SBean.ArmorRankCFGS arc = GameData.getInstance().getArmorRankCFGS(curArmor.id, curArmor.rank);
		if(arc == null)
			return;
		
		for(SBean.AttrCFGS attr: arc.properties)
			props.addPropertyFixValue(attr.id, attr.value);
	}
	
	static void updateArmorTalentProperties(FightProperties props, SBean.ArmorFightData curArmor)
	{
		for(Map.Entry<Integer, Integer> e: curArmor.talentPoint.entrySet())
		{
			final SBean.ArmorTalentCFGS arc = GameData.getInstance().getArmorTalentCFGS(curArmor.id, e.getKey());
			if(arc == null || arc.effectType != GameData.ARMOR_TALENT_TYPE_PROP)
				continue;
			
			if(arc.addAttrType == GameData.VALUE_TYPE_FIXED)
				props.addPropertyFixValue(arc.attrId, getListValue(arc.addAttrNum, e.getValue()));
			else if(arc.addAttrType == GameData.VALUE_TYPE_PERCENT)
				props.addPropertyPercentValue(arc.attrId, getListValue(arc.addAttrNum, e.getValue()));
		}
	}
	
	static void updateArmorSoltProperties(FightProperties props, SBean.ArmorFightData curArmor)
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
				
				props.addPropertyFixValue(rc.property.id, rc.property.value);
			}
		}
	}
	
	static void updateArmorRuneLangProperties(FightProperties props, SBean.ArmorFightData curArmor)
	{
		for(SBean.SoltData e: curArmor.soltGroupData)
		{
			if(e.unlocked == 0)
				continue;
			
			SBean.RuneLangCFGS rlc = GameData.getInstance().getRuneLangCFGS(e.solts);
			if(rlc == null)
				continue;
			
			for(SBean.AttrCFGS attr: rlc.properties)
				props.addPropertyFixValue(attr.id, attr.value);
		}
	}
	
	static int getListValue(List<Integer> values, int index)
	{
		if(index <= 0 || index > values.size())
			return 0;
		
		return values.get(index - 1);
	}
	
	static void updateCurHorseSkillPropperties(FightProperties props, SBean.HorseInfo info, Map<Integer, Integer> allHorseSkills)
	{
		SBean.HorseCFGS horseCfg = GameData.getInstance().getHorseCFGS(info.id);
		if (horseCfg != null)
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
				
				SBean.HorseEffectDataCFGS effectDataCfg = GameData.getInstance().getHorseEffectDataCFGS(info.id, skillLvl, skillID);
				if (effectDataCfg != null)
				{
					for(int effectID: effectDataCfg.effectIDs)
					{
						SBean.SpiritEffectCFGS effectCfg = GameData.getInstance().getSpiritEffectCFGS(effectID);
						if(effectCfg != null && effectCfg.type == GameData.SPIRIT_EFFECT_PROP)
						{
							for (Integer pid : effectCfg.param1)
								if (effectCfg.param2 == GameData.VALUE_TYPE_PERCENT)
									props.addPropertyPercentValue(pid, effectCfg.param3);
								else
									props.addPropertyFixValue(pid, effectCfg.param3);	
						}
					}	
				}
			}
		}
	}
	
	static void updateWeaponFightProperties(FightProperties props, SBean.DBWeapon dbWeapon)
	{
		SBean.WeaponCFGS wCfg = GameData.getInstance().getWeaponCFGS(dbWeapon.id);
		if(wCfg == null)
			return;
		
		for(int index = 0; index < dbWeapon.talent.size(); index++)
		{
			SBean.WeaponTalentCFGS tCfg = GameData.getInstance().getWeaponTalentCFGS(wCfg, index + 1);
			if(tCfg == null)
				continue;
			
			if(tCfg.talentEffectType != GameData.WEAPON_TALENT_TYPE_PROP)
				continue;
			
			int value = getListValue(tCfg.talentPropNum, dbWeapon.talent.get(index));
			if(tCfg.talentPropType == GameData.VALUE_TYPE_PERCENT)
				props.addPropertyPercentValue(tCfg.talentPropId, value);
			else
				props.addPropertyFixValue(tCfg.talentPropId, value);
		}
	}
	
	static void updateWeaponUSkillProperties(FightProperties props, Map<Integer, Boolean> uniqueSkills)
	{
		for(Entry<Integer, Boolean> uniqueSkillID: uniqueSkills.entrySet())
		{
			SBean.WeaponUniqueSkillCFGS cfg = GameData.getInstance().getWeaponUSkillCFGS(uniqueSkillID.getKey());
			if(cfg == null || (cfg.type != GameData.WEAPON_USKILL_TYPE_WITHPET_PROP && cfg.type != GameData.WEAPON_USKILL_TYPE_WITHHOURSE_PROP))
				continue;
			
			if(cfg.param4 == GameData.VALUE_TYPE_PERCENT)
			{
				if (uniqueSkillID.getValue())
					props.addPropertyPercentValue(cfg.fullStarParam3, cfg.fullStarParam5);
				else
					props.addPropertyPercentValue(cfg.param3, cfg.param5);
			}
			else
			{
				if (uniqueSkillID.getValue())
					props.addPropertyFixValue(cfg.fullStarParam3, cfg.fullStarParam5);
				else
					props.addPropertyFixValue(cfg.param3, cfg.param5);
			}
		}
	}
	
	static void updatePetSpiritProperties(FightProperties props, List<SBean.PetSpirit> spirits, int needType)
	{
		for(SBean.PetSpirit s: spirits)
		{
			if(s.id == 0)
				continue;
			
			SBean.PetSpiritCFGS cfg = GameData.getInstance().getPetSpiritCFGS(s.id, s.level);
			if(cfg == null || cfg.effectType != needType)
				continue;
			
			if(cfg.params.get(1) == GameData.VALUE_TYPE_FIXED)
				props.addPropertyFixValue(cfg.params.get(0), cfg.params.get(2));
			else
				props.addPropertyPercentValue(cfg.params.get(0), cfg.params.get(2));
		}
	}
	
	static void updateSpecialCardFightProperties(FightProperties props, Map<Integer, Integer> attrs, int level)
	{
		for(Map.Entry<Integer, Integer> e: attrs.entrySet())
			props.addPropertyFixValue(e.getKey(), e.getValue() * level);
	}
}