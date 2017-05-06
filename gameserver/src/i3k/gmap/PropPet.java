package i3k.gmap;

import i3k.SBean;
import i3k.gs.GameData;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PropPet extends PropFightRole
{	
	int id;
	int configID;
	PropRole owner;
	private int level;
	private int starLvl;
	private int coPracticeLvl;
	private int fightPower;
	
	private Map<Integer, Integer> breakSkills = new TreeMap<>();
	private Map<Integer, SBean.DBSkill> skills = new TreeMap<>();
	private SBean.PetHost pethost;
	private List<SBean.PetSpirit> curSpirits;
	
	private PetRate inheritRate = new PetRate();
	private PetRate starRate = new PetRate();
	private PetRate breakskillRate = new PetRate();

	private boolean updatePower;
	private FightProperties baseProperties = new FightProperties();
	private FightProperties levelProperties = new FightProperties();
	private FightProperties hostProperties = new FightProperties();
	private FightProperties inHeritProperties = new FightProperties();
	private FightProperties starProperties = new FightProperties();
	private FightProperties breakSkillProperties = new FightProperties();
	private FightProperties copracticeProperties = new FightProperties();
	private FightProperties spiritProperties = new FightProperties();

	static class PetRate
	{
		PetRate(){}
		
		double spiritRate;
		double weaponRate;
		
		void clear()
		{
			spiritRate = 0;
			weaponRate = 0;
		}
	}
	
	public PropPet(boolean updatePower)
	{
		this.updatePower = updatePower;
	}

	public PropPet createNew(SBean.FightPet fPet, PropRole owner, SBean.PetHost pethost)
	{
		if (owner == null)
			return null;

		this.id = fPet.id;
		this.configID = Math.abs(fPet.id);
		this.owner = owner;
		this.level = fPet.level;
		this.starLvl = fPet.star;
		this.breakSkills = fPet.breakSkills;
		this.coPracticeLvl = fPet.coPracticeLvl;
		this.pethost = pethost;
		this.curSpirits = fPet.curSpirits;

		SBean.PetCFGS petcfg = GameData.getInstance().getPetCFG(this.configID);
		SBean.PetCoPracticeCFGS coPracticeCfg = GameData.getInstance().getPetCoPracticeCFG(this.configID, this.coPracticeLvl);
		if (petcfg != null)
		{
			int sid;
			int lvl;
			for (int i = 0; i < petcfg.skills.size(); i++)
			{
				sid = petcfg.skills.get(i);
				lvl = coPracticeCfg == null ? 1 : coPracticeCfg.skillLevels.get(i);
				SBean.DBSkill skill = new SBean.DBSkill(sid, lvl, 0);
				this.skills.put(sid, skill);
			}
		}
		
		this.update();
		return this;
	}
	
	void updateAllProperties()
	{
		allProperties.clear();
		allProperties.merge(this.baseProperties);
		allProperties.merge(this.levelProperties);
		allProperties.merge(this.hostProperties);
		allProperties.merge(this.inHeritProperties);
		allProperties.merge(this.starProperties);
		allProperties.merge(this.breakSkillProperties);
		allProperties.merge(this.copracticeProperties);
		allProperties.merge(this.spiritProperties);
	}
	
	private double getSpiritTotalRate()
	{
		return this.inheritRate.spiritRate + this.starRate.spiritRate + this.breakskillRate.spiritRate;
	}
	
	private double getWeaponTotalRate()
	{
		return this.inheritRate.weaponRate + this.starRate.weaponRate + this.breakskillRate.weaponRate;
	}
	
	public int getFightPower()
	{
		return this.fightPower;
	}
	
	public int updateFightPower()
	{
		SBean.CommonFightPowerCFGS cfg = GameData.getInstance().getCommonCFG().fightPower;
		int spirit = cfg.spirit; //73
		int weapon = cfg.weapon; //118
		this.fightPower = (int) (allProperties.updatePetFightPower() + this.getSpiritTotalRate() * 100 * spirit + this.getWeaponTotalRate() * 100 * weapon);
		//技能战斗力
		for (SBean.DBSkill dbSkill : this.skills.values())
		{
			SBean.SkillCFGS skillCfg = GameData.getInstance().getSkillCFG(dbSkill.id);
			if (skillCfg == null)
				continue;

			SBean.SkillLevelCFGS skillLvl = GameData.getSkillLevelCFG(skillCfg, dbSkill.level);
			if (skillLvl == null)
				continue;

			this.fightPower += skillLvl.common.skillPower;
		}

		this.fightPower = (int) (0.5 * this.fightPower);
		return this.fightPower;
	}
	
	public void onUpdateInfo(SBean.FightPet fightPet)
	{
		if(this.onUpdateLvl(fightPet.level))
			return;
		
		if(this.onUpdateCoPracticeLvl(fightPet.coPracticeLvl))
			return;
		
		if(this.onUpdateStar(fightPet.star))
			return;
		
		if(this.onUpdateBreakSkills(fightPet.breakSkills))
			return;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void update()
	{
		this.updateBaseProperties(this.configID);
		this.updateInheritProperties(this.configID);
		this.updateHostProperties(this.pethost);
		this.updateLevelProperties(this.level);
		this.updateStarProperties(this.starLvl);
		this.updateBreakSkillProperties(this.breakSkills);
		this.updateCoPracticeProperties(this.configID, this.coPracticeLvl);
		this.updateSpiritProperties(this.curSpirits);
		this.updateAllProperties();
		
		if(updatePower)
			this.updateFightPower();
	}
	
	void updateBaseProperties(int configID)
	{
		this.baseProperties.clear();
		updateBaseProperties(this.baseProperties, configID);
	}
	
	void updateInheritProperties(int configID)
	{
		this.inHeritProperties.clear();
		updateInheritProperties(this.inHeritProperties, configID, this.inheritRate);
	}
	
	boolean onUpdateHost(SBean.PetHost petHost)
	{
		this.pethost = petHost;
		this.updateHostProperties(petHost);
		this.updateAllProperties();
		return true;
	}
	
	public int getSpiritTotalLays()
	{
		return this.pethost.spiritTotalLays;
	}
	
	public int getWeaponTotalLvls()
	{
		return this.pethost.weaponTotalLays;
	}
	
	void updateHostProperties(SBean.PetHost petHost)
	{
		this.hostProperties.clear();
		updateHostProperties(this.hostProperties, petHost);
	}
	
	boolean onUpdateLvl(int level)
	{
		if(this.level != level)
		{
			this.level = level;
			this.updateLevelProperties(level);
			this.updateAllProperties();
			
			if(updatePower)
				this.updateFightPower();
			return true;
		}
		return false;
	}
	
	void updateLevelProperties(int level)
	{
		this.levelProperties.clear();
		updateLevelProperties(this.levelProperties, this.configID, level);
	}
	
	boolean onUpdateCoPracticeLvl(int coPracticeLvl)
	{
		if(this.coPracticeLvl != coPracticeLvl)
		{
			this.coPracticeLvl = coPracticeLvl;
			this.updateCoPracticeProperties(this.configID, coPracticeLvl);
			this.updateAllProperties();

			if(updatePower)
				this.updateFightPower();
			return true;
		}
		
		return false;
	}
	
	int getCoPracticeLvl()
	{
		return this.coPracticeLvl;
	}
	
	boolean onUpdateStar(int starLvl)
	{
		if(this.starLvl != starLvl)
		{
			this.starLvl = starLvl;
			this.updateStarProperties(starLvl);
			this.updateAllProperties();
			
			if(updatePower)
				this.updateFightPower();
			return true;
		}
		
		return false;
	}
	
	void updateStarProperties(int starLvl)
	{
		this.starProperties.clear();
		updateStarProperties(this.starProperties, this.configID, starLvl, this.starRate);
	}
	
	int getStarLvl()
	{
		return this.starLvl;
	}
	
	boolean onUpdateBreakSkills(Map<Integer, Integer> breakSkills)
	{
		this.breakSkills = breakSkills;
		this.updateBreakSkillProperties(breakSkills);
		this.updateAllProperties();
		
		if(updatePower)
			this.updateFightPower();
		return true;
	}
	
	public List<SBean.PetSpirit> getCurSpirits()
	{
		return this.curSpirits;
	}
	
	public SBean.PetSpirit getSpirit(int index)
	{
		if(index <= 0 || index > this.curSpirits.size())
			return null;
		
		return this.curSpirits.get(index - 1);
	}
	
	public void onUpdatePetSpirit(int index, SBean.PetSpirit spirit)
	{
		if(index <= 0 || index > this.curSpirits.size())
			return;
		
		this.curSpirits.set(index - 1, spirit);
		this.updateSpiritProperties(this.curSpirits);
		this.updateAllProperties();
		
		if(updatePower)
			this.updateFightPower();
	}
	
	void updateSpiritProperties(List<SBean.PetSpirit> spirits)
	{
		this.spiritProperties.clear();
		updatePetSpiritProperties(this.spiritProperties, spirits, GameData.PET_SPIRIT_EFFECT_TYPE_PET_PROP);
	}
	
	void updateBreakSkillProperties(Map<Integer, Integer> breakSkills)
	{
		this.breakSkillProperties.clear();
		updateBreakSkillProperties(this.breakSkillProperties, breakSkills, this.breakskillRate);
	}
	
	void updateCoPracticeProperties(int petID, int coPracticeLvl)
	{
		this.copracticeProperties.clear();
		updateCoPracticeProperties(this.copracticeProperties, this.starLvl, petID, coPracticeLvl);
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	static void updateBaseProperties(FightProperties props, int configID)
	{
		SBean.PetCFGS petCfg = GameData.getInstance().getPetCFG(configID);
		if(petCfg != null)
			props.addPropertyFixValue(BaseRole.EPROPID_SPEED, petCfg.speed);
		props.addPropertyFixValue(BaseRole.EPROPID_MAXSP, GameData.getInstance().getCommonCFG().general.maxSP);
	}
	
	static void updateLevelProperties(FightProperties props, int configID, int level)
	{
		SBean.PetCFGS petCfg = GameData.getInstance().getPetCFG(configID);
		if(petCfg == null)
			return;
		
		double lvl = level - 1;
		props.addPropertyFixValue(BaseRole.EPROPID_MAXHP, petCfg.hp.org + petCfg.hp.incs1 * lvl * lvl + petCfg.hp.incs2 * lvl);
		props.addPropertyFixValue(BaseRole.EPROPID_ATKN, petCfg.atkN.org + petCfg.atkN.incs1 * lvl * lvl + petCfg.atkN.incs2 * lvl);
		props.addPropertyFixValue(BaseRole.EPROPID_DEFN, petCfg.defN.org + petCfg.defN.incs1 * lvl * lvl + petCfg.defN.incs2 * lvl);
		props.addPropertyFixValue(BaseRole.EPROPID_ATR, petCfg.atr.org + petCfg.atr.incs1 * lvl * lvl + petCfg.atr.incs2 * lvl);
		props.addPropertyFixValue(BaseRole.EPROPID_CTR, petCfg.ctr.org + petCfg.ctr.incs1 * lvl * lvl + petCfg.ctr.incs2 * lvl);
		props.addPropertyFixValue(BaseRole.EPROPID_ACRN, petCfg.acrN.org + petCfg.acrN.incs1 * lvl * lvl + petCfg.acrN.incs2 * lvl);
		props.addPropertyFixValue(BaseRole.EPROPID_TOU, petCfg.tou.org + petCfg.tou.incs1 * lvl * lvl + petCfg.tou.incs2 * lvl);
		props.addPropertyFixValue(BaseRole.EPROPID_ATKA, petCfg.atkA.org + petCfg.atkA.incs1 * lvl * lvl + petCfg.atkA.incs2 * lvl);
	}
	
	static void updateHostProperties(FightProperties props, SBean.PetHost pethost)
	{
		if(pethost == null)
			return;
		
		props.addPropertyFixValue(BaseRole.EPROPID_ATKC, pethost.hostAtkc);
		props.addPropertyFixValue(BaseRole.EPROPID_DEFC, pethost.hostDefc);
		props.addPropertyFixValue(BaseRole.EPROPID_ATKW, pethost.hostAtkw);
		props.addPropertyFixValue(BaseRole.EPROPID_DEFW, pethost.hostDefw);
		props.addPropertyFixValue(BaseRole.EPROPID_MASTERC, pethost.hostMasterC);
		props.addPropertyFixValue(BaseRole.EPROPID_MASTERW, pethost.hostMasterW);
	}
	
	static void updateInheritProperties(FightProperties props, int configID, PetRate inheritRate)
	{
		SBean.PetCFGS petCfg = GameData.getInstance().getPetCFG(configID);
		if(petCfg == null)
			return;
		
		props.addPropertyPercentValue(BaseRole.EPROPID_ATKC, (petCfg.spiritInherit - 1) * 10000);
		props.addPropertyPercentValue(BaseRole.EPROPID_DEFC, (petCfg.spiritInherit - 1) * 10000);
		props.addPropertyPercentValue(BaseRole.EPROPID_ATKW, (petCfg.weaponInherit - 1) * 10000);
		props.addPropertyPercentValue(BaseRole.EPROPID_DEFW, (petCfg.weaponInherit - 1) * 10000);
		
		inheritRate.clear();
		inheritRate.spiritRate = petCfg.spiritInherit;
		inheritRate.weaponRate = petCfg.weaponInherit;
	}
	
	static void updateStarProperties(FightProperties props, int configID, int starLvl, PetRate starRate)
	{
		SBean.PetCFGS petCfg = GameData.getInstance().getPetCFG(configID);
		if(petCfg == null)
			return;
		
		SBean.PetStarCFGS starCfg = GameData.getPetStarCFG(petCfg, starLvl);
		if (starCfg == null)
			return;

		props.addPropertyFixValue(BaseRole.EPROPID_DMGTO, starCfg.harmUpRate);
		props.addPropertyFixValue(BaseRole.EPROPID_DMGBY, starCfg.harmDownRate);

		props.addPropertyPercentValue(BaseRole.EPROPID_ATKC, starCfg.spiritRate * 10000);
		props.addPropertyPercentValue(BaseRole.EPROPID_DEFC, starCfg.spiritRate * 10000);
		props.addPropertyPercentValue(BaseRole.EPROPID_ATKW, starCfg.weaponRate * 10000);
		props.addPropertyPercentValue(BaseRole.EPROPID_DEFW, starCfg.weaponRate * 10000);
		
		starRate.clear();
		starRate.spiritRate = starCfg.spiritRate;
		starRate.weaponRate = starCfg.weaponRate;
	}
	
	static void updateBreakSkillProperties(FightProperties props, Map<Integer, Integer> breakSkills, PetRate breakskillRate)
	{
		breakskillRate.clear();
		for (Map.Entry<Integer, Integer> e: breakSkills.entrySet())
		{
			int bsSkillId = e.getKey();
			int bsSkillLvl = e.getValue();
			SBean.PetBreakSkillLevelCFGS tupoCfg = GameData.getInstance().getPetBreakSkillCFG(bsSkillId, bsSkillLvl);
			if(tupoCfg == null)
				continue;
			
			props.addPropertyFixValue(BaseRole.EPROPID_DMGTO, tupoCfg.harmUpRate);
			props.addPropertyFixValue(BaseRole.EPROPID_DMGBY, tupoCfg.harmDownRate);

			props.addPropertyPercentValue(BaseRole.EPROPID_ATKC, tupoCfg.spiritRate * 10000);
			props.addPropertyPercentValue(BaseRole.EPROPID_DEFC, tupoCfg.spiritRate * 10000);
			props.addPropertyPercentValue(BaseRole.EPROPID_ATKW, tupoCfg.weaponRate * 10000);
			props.addPropertyPercentValue(BaseRole.EPROPID_DEFW, tupoCfg.weaponRate * 10000);
			
			breakskillRate.spiritRate += tupoCfg.spiritRate;
			breakskillRate.weaponRate += tupoCfg.weaponRate;
		}
	}
	
	static void updateCoPracticeProperties(FightProperties props, int starLvl, int petID, int coPracticeLvl)
	{
		boolean isFullStar = starLvl == GameData.getInstance().getPetStarLimit(petID);
		SBean.PetCoPracticeCFGS coPracticeCfg = GameData.getInstance().getPetCoPracticeCFG(petID, coPracticeLvl);
		if (coPracticeCfg == null)
			return;

		for (SBean.AttrCFGS attr : coPracticeCfg.attris)
		{
			props.addPropertyFixValue(attr.id, ((isFullStar ? GameData.getInstance().getCommonCFG().pet.fullStarPropAdd / 10000.0 : 0) + 1) * attr.value);
		}
	}
}
