package i3k.gmap;

import i3k.SBean;
import i3k.gs.GameData;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PropRole extends PropFightRole
{
	public static final double SUIT_SELF_RATE 		= 1.0;
	public static final double SUIT_OTHER_RATE 		= 0.2;
	
	private FightProperties baseProperties = new FightProperties();
	private FightProperties levelProperties = new FightProperties();
	private FightProperties transformProperties = new FightProperties();
	private FightProperties equipProperties = new FightProperties();
	private FightProperties suiteProperties = new FightProperties();
	private FightProperties fashionProperties = new FightProperties();
	private FightProperties sealProperties = new FightProperties();
	private FightProperties spiritProperties = new FightProperties();
	private Map<Integer, FightProperties> weaponProperties = new HashMap<>();
	private Map<Integer, FightProperties> horseProperties = new HashMap<>();
	private FightProperties curHorseProperties = new FightProperties();
	private FightProperties medalProperties = new FightProperties();
	private FightProperties sectAuraProperties = new FightProperties();
//	private FightProperties clanDiziTangProperties = new FightProperties();
	private FightProperties rareBookProperties = new FightProperties();
	private FightProperties graspProperties = new FightProperties();
	private FightProperties titlesProperties = new FightProperties();
	private FightProperties petAchieveProperties = new FightProperties();
	private FightProperties petCoPracticesProperties = new FightProperties();
	private FightProperties curFightPetsProperties = new FightProperties();
	private FightProperties armorProperties = new FightProperties();
	private FightProperties armorLangProperties = new FightProperties();
	private FightProperties itemProperties = new FightProperties();
	private FightProperties perfectDegreeProperties = new FightProperties();
	private FightProperties marriageProperties =new FightProperties();
	private FightProperties dmgTransferProperties = new FightProperties();
	private FightProperties specialCardProperties = new FightProperties();
	
	private int id;
	private byte configID;
	private byte transformLevel;
	private byte BWType;
	private int level;
	
//	public Map<Integer, Integer> fightPowers;
	private Map<Integer, SBean.DBWearEquip> wearEquips = new HashMap<>();
	private List<SBean.DBEquipPart> wearParts = new ArrayList<>();
	private Set<Integer> suites = new HashSet<>();
	private Map<Integer, SBean.DBSkill> skills = new TreeMap<>();
	private Map<Integer, SBean.DBWeapon> weapons = new TreeMap<>();
	private Map<Integer, SBean.DBSpirit> spirits = new TreeMap<>();
	private Set<Integer> curSpirits = new HashSet<>();
	private Map<Integer, Integer> sectAuras;
	private Map<Integer, SBean.HorseInfo> horses = new HashMap<>();
	//private SBean.ClanOwnerAttriAddition clanDiziTang;
	private SBean.DBSealData sealData;
	private Map<Integer, Integer> titles = new HashMap<>();			//<type, title>
	private Set<Integer> petAchieves = new HashSet<>();
	private Map<Integer, Integer> petCoPractices = new HashMap<>();
	private Map<Integer, SBean.FightPet> curFightPets = new HashMap<>(); 	//<configID, FightPet>	负ID为借调的佣兵
	
	private int curUseHorse;
	private Map<Integer, Integer> allHorseSkills = new HashMap<>();
	//int speed;
	private Map<Integer, Byte> medals = new HashMap<>();
	private Map<Integer, Integer> fashions = new HashMap<>();
	private Map<Integer, Integer> rareBooks = new HashMap<>();
	private Map<Integer, Integer> grasps = new HashMap<>();
	private SBean.ArmorFightData armor;
	private Map<Integer, Integer> itemProps = new HashMap<>();
	private int perfectDegree = 0;
	private int marriageLevel;
	private Map<Integer, Integer> transferPointLvls = new HashMap<>();
	private Map<Integer, Integer> petStar = new HashMap<>();
	Map<Integer, Integer> specialCardAttr = new HashMap<>();
	
	private int spiritTotalLays; //心法总层级
	private int weaponTotalLvls; //神兵总品阶
	private int graspTotalRaise;	 //参悟 藏书效果领悟提升率
	
	private boolean updatePower;
	private int extraSkillsPower;
	private int extraSpiritPower;
	private int extraEquipPower;
	private int extraArmorPower;
	private int extraHorsePower;
	private int totalPower;
	private int equipsPower;
	private int skillsPower;
	private int weaponsPower;
	private int sectPower;
	private int clanPower;
	private int armorPower;
//	private Map<Integer, Integer> weaponPower = new HashMap<>();

	public PropRole(boolean updatePower)
	{
		this.updatePower = updatePower;
	}

	public PropRole createNew(SBean.BasePlayer basePlayer, Map<Integer, SBean.FightPet> curFightPets, boolean isInArena)
	{
		this.id = basePlayer.roleID;
		this.configID = basePlayer.classType;
		this.transformLevel = basePlayer.transformLevel;
		this.BWType = basePlayer.BWType;
		this.level = basePlayer.level;
		this.suites = basePlayer.suites;
		this.wearEquips = basePlayer.equips;
		this.wearParts.addAll(basePlayer.equipParts);
		this.sealData = basePlayer.sealData;
		this.skills = basePlayer.skills;
		this.weapons = basePlayer.weapons;
		this.spirits = basePlayer.spirits;
		this.curSpirits = basePlayer.curSpirits;
		this.sectAuras = basePlayer.sectAuras;
		this.horses = basePlayer.horseData.horses;
		this.curUseHorse = basePlayer.horseData.inuseHorse;
		this.allHorseSkills  = basePlayer.horseData.allHorseSkills;
		this.medals = basePlayer.medals;
		this.fashions = basePlayer.curFashions;
		//this.clanDiziTang = basePlayer.clanDiziTang;
		this.rareBooks = basePlayer.rarebooks;
		this.grasps = basePlayer.grasps;
		for(int tid: basePlayer.title.titles.keySet())
		{
			SBean.TitleCFGS titleCfg = GameData.getInstance().getTitleCFGS(tid);
			if(this.isBetterTitle(titleCfg))
				this.titles.put(titleCfg.type, tid);
		}
		
		this.petAchieves = basePlayer.petAchieves;
		this.petCoPractices = basePlayer.petCoPractices;
		this.curFightPets = curFightPets;
		this.armor = basePlayer.armor;
		this.itemProps = basePlayer.itemProps;
		this.perfectDegree = basePlayer.perfectDegree;
		this.marriageLevel = basePlayer.marriageLevel;
		this.transferPointLvls = basePlayer.transferPointLvls;
		this.petStar = basePlayer.petStar;
		this.specialCardAttr = basePlayer.specialCardAttr;
		
		this.update(isInArena);
		return this;
	}
	
	boolean isBetterTitle(SBean.TitleCFGS titleCfg)
	{
		if(titleCfg == null)
			return false;
		
		Integer curTid = this.titles.get(titleCfg.type);
		if(curTid == null)
			return true;
		
		SBean.TitleCFGS curTitleCfg = GameData.getInstance().getTitleCFGS(curTid);
		if(curTitleCfg == null || titleCfg.rank < curTitleCfg.rank)
			return true;
		
		return false;
	}
	
	int getTransformLevel()
	{
		return this.transformLevel;
	}
	
	byte getBWType()
	{
		return this.BWType;
	}
	
	SBean.DBSkill getSkill(int skillId)
	{
		return this.skills.get(skillId);
	}
	
	SBean.DBSpirit getSpirit(int spiritId)
	{
		return this.spirits.get(spiritId);
	}
	
	Collection<Integer> getCurSpirits()
	{
		return this.curSpirits;
	}
	
	boolean isCurSpirit(int id)
	{
		return this.curSpirits.contains(id);
	}
	
	Collection<Integer> getAllSealSkills()
	{
		return this.sealData.skills.keySet();
	}
	
	int getSealSkillLevel(int skillId)
	{
		return this.sealData.skills.getOrDefault(skillId, 0);
	}
	
	SBean.DBWeapon getWeapon(int weaponId)
	{
		return this.weapons.get(weaponId);
	}
	
	Collection<SBean.DBWeapon> getAllWeapons()
	{
		return this.weapons.values();
	}
	
	int getWearEquipID(int wid)
	{
		return getWearEquip(wid) == null ? 0 : getWearEquip(wid).equip.id;
	}
	
	Collection<SBean.DBWearEquip> getAllWearEquip()
	{
		return this.wearEquips.values();
	}
	
	SBean.DBWearEquip getWearEquip(int wid)
	{
		return this.wearEquips.get(wid);
	}
	
	SBean.HorseInfo getHorse(int hid)
	{
		return this.horses.get(hid);
	}
	
	int getCurHorseId()
	{
		return this.curUseHorse;
	}
	
	Map<Integer, Integer> getAllHorseSkills()
	{
		return this.allHorseSkills;
	}
	
	int getHorseSkillLvl(int skillID)
	{
		return this.allHorseSkills.getOrDefault(skillID, 0);
	}
	
	Map<Integer, Integer> getWearEquipIds()
	{
		return this.wearEquips.values().stream().collect(Collectors.toMap(e -> e.wid, e -> e.equip.id));
	}
	
	List<SBean.EquipPart> getWearParts()
	{
		return this.wearParts.stream().map(e -> new SBean.EquipPart(e.id, e.eqGrowLvl, e.eqEvoLvl)).collect(Collectors.toList());
	}
	
	Map<Integer, Integer> getCurFashions()
	{
		return new HashMap<>(this.fashions);
	}
	
	int getHeirLoomPerfect()
	{
		return this.perfectDegree;
	}
	
	public void update(boolean isInArena)
	{
		if (isInArena && this.id < 0)
		{
			this.updateRobotProperties(-this.id);
		}
		else
		{
			this.updateBaseProperties(this.configID);
			this.updateLevelProperties(this.configID, this.level);
			this.updateTransformProperties(this.configID, this.transformLevel, this.BWType);
			this.updateEquipProperties(this.wearParts, this.wearEquips);
			this.updateSuiteProperties(this.suites, this.configID);
			this.updateFashionProperties(this.fashions.values());
			this.updateSealProperties(this.transformLevel, this.sealData);
			this.updateSkillProperties(this.skills.values(), this.sealData);
			this.updateSpiritProperties(this.spirits.values(), this.curSpirits);
			this.updateWeaponProperties(this.weapons.values());
			this.updateHorseProperties(this.horses, this.curUseHorse, this.allHorseSkills);
			this.updateMedalsProperties(this.medals);
			this.updateSectAuraProperties(this.sectAuras);
			//this.updateClanDiziTangProperties(this.clanDiziTang);
			this.updateGraspProperties(this.grasps, this.level);
			this.updateRareBookProperties(this.rareBooks, this.graspTotalRaise);
			this.updateTitleProperties(this.titles.values());
			this.updatePetAchieveProperties(this.petAchieves);
			this.updatePetCoPracticesProperties(this.petCoPractices);
			this.updateCurFightPetProperties(this.curFightPets.values());
			this.updateCurFightPetProperties(this.curFightPets.values());
			this.updateArmorProperties(this.armor);
			this.updateItemProperties(this.itemProps);
			this.updatePerfectDegreeProperties(this.perfectDegree);
			this.updateMarriageProperties(marriageLevel);
			this.updateDmgTransferProperties(this.transferPointLvls);
			this.updateSpecialCardFightProperties();
		}
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateRoleFightPower();
			this.updateEquipsPower();
			this.updateSkillsPower();
			this.updateWeaponsPower();
			this.updateSectPower();
//			this.updateClanPower();
		}
	}
	
////////////////////////////////////////////////基础属性////////////////////////////////////////////////
	public void onUpdateLvl(int level)
	{
		this.level = level;
		this.updateLevelProperties(this.configID, this.level);
		this.updateGrasp();
		this.updateSpecialCardFightProperties();
		this.updateAllProperties();
		
		if (this.updatePower)
			this.updateRoleFightPower();
	}
////////////////////////////////////////////////基础属性////////////////////////////////////////////////
	
////////////////////////////////////////////////装备属性////////////////////////////////////////////////
	public void onUpdateEquip(int wid, SBean.DBEquip equip)
	{
		this.wearEquips.compute(wid, (k, v) -> equip == null ? null : new SBean.DBWearEquip(wid, equip));
		this.updateEquipProperties(this.wearParts, this.wearEquips);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateEquipsPower();
			this.updateRoleFightPower();	
		}
	}

	public void onUpdateEquipPart(SBean.DBEquipPart equipPart)
	{
		this.wearParts.set(equipPart.id - 1, equipPart);
		this.updateEquipProperties(this.wearParts, this.wearEquips);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateEquipsPower();
			this.updateRoleFightPower();	
		}
	}
	
	public boolean onGainNewSuite(int suiteID)
	{
		if(this.suites.add(suiteID))
		{
			this.updateSuiteProperties(this.suites, this.configID);
			this.updateAllProperties();
			
			if (this.updatePower)
			{
				this.updateEquipsPower();
				this.updateRoleFightPower();	
			}
			
			return true;
		}
		
		return false;
	}

	public boolean onUpWearFashion(int type, int fashionID)
	{
		int oldFashion = this.fashions.getOrDefault(type, 0);
		if(oldFashion == fashionID)
			return false;
		
		this.fashions.put(type, fashionID);
		this.updateFashionProperties(this.fashions.values());
		this.updateAllProperties();
		if (this.updatePower)
		{
			this.updateEquipsPower();
			this.updateRoleFightPower();	
		}
		return true;
	}
	
	public void onUpdateSealGrade(int grade)
	{
		this.sealData.grade = grade;
		this.updateSealProperties(this.transformLevel, this.sealData);
		this.updateSkillProperties(this.skills.values(), this.sealData);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateEquipsPower();
			this.updateSkillsPower();
			this.updateRoleFightPower();	
		}
	}
	
	public void onUpdateSealSkill(Map<Integer, Integer> skills)
	{
		this.sealData.skills = skills;
		this.updateSealProperties(this.transformLevel, this.sealData);
		this.updateSkillProperties(this.skills.values(), this.sealData);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateEquipsPower();
			this.updateSkillsPower();
			this.updateRoleFightPower();	
		}
	}
////////////////////////////////////////////////装备属性////////////////////////////////////////////////
	
////////////////////////////////////////////////技能属性////////////////////////////////////////////////
	public void onUpdateSkill(SBean.DBSkill skill)
	{
		this.skills.put(skill.id, skill);
		this.updateSkillProperties(this.skills.values(), this.sealData);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateSkillsPower();
			this.updateRoleFightPower();	
		}
	}

	public void onUpdateTransformInfo(byte transformLevel, byte BWType)
	{
		this.transformLevel = transformLevel;
		this.BWType = BWType;
		
		this.updateTransformProperties(this.configID, this.transformLevel, this.BWType);
		this.updateSealProperties(this.transformLevel, this.sealData);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateEquipsPower();
			this.updateRoleFightPower();	
		}
	}

	public void onUpdateSpirit(SBean.DBSpirit spirit)
	{
		this.spirits.put(spirit.id, spirit);
		this.updateSpiritProperties(this.spirits.values(), this.curSpirits);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateSkillsPower();
			this.updateRoleFightPower();	
		}
	}

	public void onUpdateCurSpirit(Set<Integer> curSpirits)
	{
		this.curSpirits = curSpirits;
		this.updateSpiritProperties(this.spirits.values(), this.curSpirits);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateSkillsPower();
			this.updateRoleFightPower();	
		}
	}
////////////////////////////////////////////////技能属性////////////////////////////////////////////////
	
////////////////////////////////////////////////神兵属性////////////////////////////////////////////////
	public boolean onUpdateWeapon(SBean.DBWeapon weapon)
	{
		this.weapons.put(weapon.id, weapon);
		this.updateWeaponProperties(this.weapons.values());
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateWeaponsPower();
			this.updateRoleFightPower();	
		}
		return GameData.getInstance().isFullStar(weapon);
	}
	
	public void onUpdateWeaponSkill(int weaponID, List<Integer> skills)
	{
		SBean.DBWeapon weapon = this.weapons.get(weaponID);
		if(weapon == null)
			return;
		
		weapon.skills = skills;
	}
	
	public void onUpdateWeaponTalent(int weaponID, List<Integer> talents)
	{
		SBean.DBWeapon weapon = this.weapons.get(weaponID);
		if(weapon == null)
			return;
		
		weapon.talent = talents;
	}
	
	public void onWeaponOpen(int weaponID)
	{
		SBean.DBWeapon weapon = this.weapons.get(weaponID);
		if(weapon == null)
			return;
		
		weapon.uniqueSkill.open = 1;
	}
////////////////////////////////////////////////神兵属性////////////////////////////////////////////////	
	
////////////////////////////////////////////////坐骑属性////////////////////////////////////////////////
	public void onUpdateHorse(SBean.HorseInfo info)
	{
		this.horses.put(info.id, info);
		this.updateHorseProperties(this.horses, this.curUseHorse, this.allHorseSkills);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
//			if(info.id == this.curUseHorse)
//				this.updateHorseExtraPower(info, this.allHorseSkills);
			
			this.updateRoleFightPower();
		}
	}

	public void onUpdateCurUseHorse(int hid)
	{
		this.curUseHorse = hid;
		this.updateHorseProperties(this.horses, this.curUseHorse, this.allHorseSkills);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
//			this.updateHorseExtraPower(this.horses.get(this.curUseHorse), allHorseSkills);
			this.updateRoleFightPower();
		}
	}
	
	public void onUpdateHorseSkill(int skillID, int skillLvl)
	{
		this.allHorseSkills.put(skillID, skillLvl);
//		this.updateHorseProperties(this.horses, this.curUseHorse, this.allHorseSkills);
		
		if (this.updatePower)
		{
			this.updateHorseExtraPower(this.horses.get(this.curUseHorse), allHorseSkills);
			this.updateRoleFightPower();
		}
	}
////////////////////////////////////////////////坐骑属性////////////////////////////////////////////////

////////////////////////////////////////////////帮派属性////////////////////////////////////////////////
	public void onUpdateSectAura(int auraID, int auraLvl)
	{
		this.sectAuras.put(auraID, auraLvl);
		this.updateSectAuraProperties(this.sectAuras);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateSectPower();
			this.updateRoleFightPower();	
		}
	}

	public void onUpdateSectAuras(Map<Integer, Integer> sectAuras)
	{
		this.sectAuras = sectAuras;
		this.updateSectAuraProperties(this.sectAuras);
		this.updateAllProperties();
		
		if (this.updatePower)
		{
			this.updateSectPower();
			this.updateRoleFightPower();
		}
	}
////////////////////////////////////////////////帮派属性////////////////////////////////////////////////	
	
////////////////////////////////////////////////宗门属性////////////////////////////////////////////////
//	public void onUpdateClanDiziTangAttr(SBean.ClanOwnerAttriAddition attr)
//	{
//		this.clanDiziTang = attr;
//		this.updateClanDiziTangProperties(this.clanDiziTang);
//		this.updateAllProperties();
//		
//		if (this.updatePower)
//		{
//			this.updateClanPower();
//			this.updateRoleFightPower();
//		}
//	}
////////////////////////////////////////////////宗门属性////////////////////////////////////////////////
	
////////////////////////////////////////////////收藏品属性////////////////////////////////////////////////
	public void onUpdateMedal(int medalID, byte state)
	{
		this.medals.put(medalID, state);
		this.updateMedalsProperties(this.medals);
		this.updateAllProperties();
		
		if (this.updatePower)
			this.updateRoleFightPower();
	}
////////////////////////////////////////////////收藏品属性////////////////////////////////////////////////

////////////////////////////////////////////////参悟属性////////////////////////////////////////////////
	public void onUpdateGrasp(int graspID, int lvl)
	{
		this.grasps.put(graspID, lvl);
		this.updateGrasp();
		this.updateAllProperties();
		if (this.updatePower)
		this.updateRoleFightPower();
	}
	
	private void updateGrasp()
	{
		int oldGraspTotalRaise = this.graspTotalRaise;
		this.updateGraspProperties(this.grasps, this.level);
		if(oldGraspTotalRaise != this.graspTotalRaise)
			this.updateRareBookProperties(this.rareBooks, this.graspTotalRaise);
	}
////////////////////////////////////////////////参悟属性////////////////////////////////////////////////
	
////////////////////////////////////////////////藏书属性////////////////////////////////////////////////
	public void onUpdateRareBook(int bookID, int lvl)
	{
		this.rareBooks.put(bookID, lvl);
		this.updateRareBookProperties(this.rareBooks, this.graspTotalRaise);
		
		this.updateAllProperties();
		if (this.updatePower)
			this.updateRoleFightPower();
	}
////////////////////////////////////////////////乾坤属性////////////////////////////////////////////////
	public void onDMGTransferPointLvlsUpdate(Map<Integer, Integer> pointLvls)
	{
		this.transferPointLvls = pointLvls;
		this.updateDmgTransferProperties(pointLvls);
		
		this.updateAllProperties();
		if(this.updatePower)
			this.updateRoleFightPower();
	}
	
////////////////////////////////////////////////称号属性////////////////////////////////////////////////
	public void onUpdateTitle(int id, boolean add)
	{
		SBean.TitleCFGS titleCfg = GameData.getInstance().getTitleCFGS(id);
		if(titleCfg == null)
			return;
		
		if(add)
		{
			if(!this.isBetterTitle(titleCfg))
				return;
			
			this.titles.put(titleCfg.type, titleCfg.id);
		}
		else
		{
			this.titles.remove(titleCfg.type);
		}
		
		this.updateTitleProperties(this.titles.values());
		this.updateAllProperties();
		
		if(this.updatePower)
			this.updateRoleFightPower();
	}
////////////////////////////////////////////////称号属性////////////////////////////////////////////////
	
////////////////////////////////////////////////随从成就属性////////////////////////////////////////////////
	public void onUpdatePetAchieves(Set<Integer> achieves)
	{
		this.petAchieves = achieves;
		this.updatePetAchieveProperties(this.petAchieves);
		this.updateAllProperties();
		
		if(this.updatePower)
			this.updateRoleFightPower();
	}
////////////////////////////////////////////////随从成就属性////////////////////////////////////////////////
	
////////////////////////////////////////////////随从合修属性////////////////////////////////////////////////
	public boolean onUpdatePetCoPractice(int pid, int coPracticeLvl, boolean isFullStar)
	{
		int oldLvl = this.petCoPractices.getOrDefault(pid, 0);
		if(oldLvl != coPracticeLvl || isFullStar)
		{
			this.petCoPractices.put(pid, coPracticeLvl);
			this.updatePetCoPracticesProperties(this.petCoPractices);
			this.updateAllProperties();
			
			if(this.updatePower)
				this.updateRoleFightPower();
			
			return true;
		}
		
		return false;
	}
////////////////////////////////////////////////随从合修属性////////////////////////////////////////////////
	
////////////////////////////////////////////////出战的随从////////////////////////////////////////////////
	boolean isCurFightPet(int pid)
	{
		return this.curFightPets.containsKey(pid);
	}
	
	boolean isCurFightPetEmpty()
	{
		return this.curFightPets.isEmpty();
	}
	
	Collection<SBean.FightPet> getCurFightPets()
	{
		return this.curFightPets.values();
	}
	
	public void onChangeCurFightPet(Map<Integer, SBean.FightPet> curFightPets)
	{
		this.curFightPets = curFightPets;
		this.onCurFightPetChangeImpl();
	}
	
	void clearCurFightPets()
	{
		this.curFightPets.clear();
		this.onCurFightPetChangeImpl();
	}
	
	public void onUpdateCurPetSpirit(int petID, int index, SBean.PetSpirit spirit)
	{
		SBean.FightPet pet = this.curFightPets.get(petID);
		if(pet == null || index <= 0 || index > pet.curSpirits.size())
			return;
		
		pet.curSpirits.set(index - 1, spirit);
		this.onCurFightPetChangeImpl();
	}
	
	void onCurFightPetChangeImpl()
	{
		this.updateCurFightPetProperties(this.curFightPets.values());
		this.updateAllProperties();
		
		if(this.updatePower)
			this.updateRoleFightPower();
	}
	
////////////////////////////////////////////////内甲属性////////////////////////////////////////////////
	public SBean.ArmorFightData getCurArmor()
	{
		return this.armor;
	}
	
	public void onChangeCurArmor(SBean.ArmorFightData curArmor)
	{
		this.armor = curArmor;
		this.updateArmorProperties(curArmor);
		this.updateAllProperties();
		
		if(this.updatePower)
		{
			this.updateArmorExtraPower();
			this.updateArmorPower();
			this.updateRoleFightPower();	
		}
	}
	
	
	public void onUpdateArmorLevel(int armorLevel)
	{
		if(this.armor == null || this.armor.level == armorLevel)
			return;
		
		this.armor.level = armorLevel;
		this.updateArmorNormalProperties(this.armor);
		this.updateAllProperties();
		
		if(this.updatePower)
		{
			this.updateArmorPower();
			this.updateRoleFightPower();
		}
	}
	
	public void onUpdateArmorRank(int armorRank)
	{
		if(this.armor == null || this.armor.rank == armorRank)
			return;
		
		this.armor.rank = armorRank;
		this.updateArmorNormalProperties(this.armor);
		this.updateAllProperties();
		
		if(this.updatePower)
		{
			this.updateArmorPower();
			this.updateRoleFightPower();
		}
	}
	
	public void onUpdateArmorRune(int index, List<Integer> armorRunes)
	{
		if(this.armor == null || index <= 0 || index > this.armor.soltGroupData.size())
			return;
		
		this.armor.soltGroupData.get(index - 1).unlocked = 1;
		this.armor.soltGroupData.get(index - 1).solts = armorRunes;
		this.updateArmorLangProperties(this.armor);
		this.updateAllProperties();
		
		if(this.updatePower)
		{
			this.updateArmorExtraPower();
			this.updateRoleFightPower();
		}
	}
	
	public void onUpdateArmorTalent(Map<Integer, Integer> talentPoint)
	{
		if(this.armor == null)
			return;
		
		this.armor.talentPoint = talentPoint;
		if(this.updatePower)
		{
			this.updateArmorExtraPower();
			this.updateRoleFightPower();
		}
	}
////////////////////////////////////////////////道具永久属性////////////////////////////////////////////////
	public void onUpdateItemProps(Map<Integer, Integer> itemProps)
	{
		this.itemProps = itemProps;
		this.updateItemProperties(itemProps);
		this.updateAllProperties();
		
		if(this.updatePower)
			this.updateRoleFightPower();
	}

///////////////////////////////////////////////传家宝属性////////////////////////////////////////////////
	public void onUpdatePerfectDegree(int perfectDegree)
	{
		this.perfectDegree = perfectDegree;
		this.updatePerfectDegreeProperties(perfectDegree);
		this.updateAllProperties();
		
		if(this.updatePower)
			this.updateRoleFightPower();
	}
///////////////////////////////////////////////姻缘属性////////////////////////////////////////////////
	public void onMarriageLevelChange(int marriageLevel)
	{
		this.marriageLevel = marriageLevel;
		this.updateMarriageProperties(marriageLevel);
		this.updateAllProperties();
		
		if(this.updatePower)
			this.updateRoleFightPower();
	}
	
///////////////////////////////////////////////特权卡属性////////////////////////////////////////////////	
	Map<Integer, Integer> getSpecialCardAttr()
	{
		return this.specialCardAttr;
	}
	
	public void onUpdateSpecialCardAttr(Map<Integer, Integer> attrs)
	{
		this.specialCardAttr = attrs;
		this.updateSpecialCardFightProperties();
		
		this.updateAllProperties();
		if (this.updatePower)
			this.updateRoleFightPower();
	}
	
	
	public int getFightPower()
	{
		return this.totalPower;
	}
	
	public int getSpiritTotalLays()
	{
		return this.spiritTotalLays;
	}
	
	public int getWeaponTotalLvls()
	{
		return this.weaponTotalLvls;
	}
	
	public Map<Integer, Integer> getRoleProperties()
	{
		Map<Integer, Integer> props = new HashMap<>();
		for(int propID = BaseRole.EPROPID_MAXHP; propID <= BaseRole.EPROPID_MASTERW; propID++)
			props.put(propID, this.getBaseProps(propID));
		return props;
	}
	
	public SBean.RolePowerDetail getRolePowerDetail()
	{
		return new SBean.RolePowerDetail(
				this.equipsPower,
				this.skillsPower,
				this.weaponsPower,
				0,
				this.sectPower,
				this.clanPower);
	}
	
	public SBean.PetHost getMapPetHost()
	{
		return new SBean.PetHost(this.getBaseProps(BaseRole.EPROPID_ATKW),
								this.getBaseProps(BaseRole.EPROPID_DEFW),
								this.getBaseProps(BaseRole.EPROPID_ATKC),
								this.getBaseProps(BaseRole.EPROPID_DEFC),
								this.getBaseProps(BaseRole.EPROPID_MASTERW),
								this.getBaseProps(BaseRole.EPROPID_MASTERC),
								this.getSpiritTotalLays(),
								this.getWeaponTotalLvls());
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void updateRoleFightPower()
	{
		this.totalPower = allProperties.updateFightPower() + this.extraEquipPower + this.extraSkillsPower + this.extraSpiritPower + this.extraArmorPower + this.extraHorsePower;
	}
	
	void updateEquipsPower()
	{
		FightProperties wearProperties = new FightProperties();
		
		wearProperties.merge(this.equipProperties);
		wearProperties.merge(this.suiteProperties);
		wearProperties.merge(this.fashionProperties);
		wearProperties.merge(this.sealProperties);
		this.equipsPower = wearProperties.updateFightPower() + this.extraEquipPower;
	}
	
	void updateSkillsPower()
	{
		FightProperties skillProperties = new FightProperties();

		skillProperties.merge(this.spiritProperties);
		this.skillsPower = skillProperties.updateFightPower() + this.extraSkillsPower + this.extraSpiritPower;
	}
	
	void updateWeaponsPower()
	{
		this.weaponsPower = 0;
		for (SBean.DBWeapon weapon : this.weapons.values())
		{
			FightProperties props = this.weaponProperties.get(weapon.id);
			if(props == null)
				continue;
			
			int power = props.updateFightPower();
			weapon.fightPower = power;
			this.weaponsPower += power;
		}
		
//		FightProperties weaponsProperties = new FightProperties();
//		
//		for (FightProperties e : this.weaponProperties.values())
//			weaponsProperties.merge(e);
//		this.weaponsPower = weaponsProperties.updateFightPower();
	}
	
	void updateSectPower()
	{
		FightProperties sectProperties = new FightProperties();
		
		sectProperties.merge(this.sectAuraProperties);
		this.sectPower = sectProperties.updateFightPower();
	}
	
	void updateArmorPower()
	{
		FightProperties armorProperties = new FightProperties();
		armorProperties.merge(this.armorProperties);
		armorProperties.merge(this.armorLangProperties);
		this.armorPower = armorProperties.updateFightPower();
		this.armorPower += this.extraArmorPower;
	}
	
	void updateArmorExtraPower()
	{
		this.extraArmorPower = FightProperties.calcArmorTalentPower(this.armor) + FightProperties.calcArmorRunesPower(this.armor);
	}
	
	void updateHorseExtraPower(SBean.HorseInfo info, Map<Integer, Integer> allHorseSkills)
	{
		this.extraHorsePower = FightProperties.calcHorseSkillsPower(info, allHorseSkills);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void updateAllProperties()
	{
		allProperties.clear();
		allProperties.merge(this.baseProperties);
		allProperties.merge(this.levelProperties);
		allProperties.merge(this.transformProperties);
		allProperties.merge(this.equipProperties);
		allProperties.merge(this.suiteProperties);
		allProperties.merge(this.fashionProperties);
		allProperties.merge(this.sealProperties);
		allProperties.merge(this.spiritProperties);
		for (FightProperties e : this.weaponProperties.values())
			allProperties.merge(e);
		for (FightProperties e : this.horseProperties.values())
			allProperties.merge(e);
		allProperties.merge(this.curHorseProperties);
		allProperties.merge(this.medalProperties);
		allProperties.merge(this.sectAuraProperties);
//		allProperties.merge(this.clanDiziTangProperties);
		allProperties.merge(this.graspProperties);
		allProperties.merge(this.rareBookProperties);
		allProperties.merge(this.titlesProperties);
		allProperties.merge(this.petAchieveProperties);
		allProperties.merge(this.petCoPracticesProperties);
		allProperties.merge(this.curFightPetsProperties);
		allProperties.merge(this.armorProperties);
		allProperties.merge(this.armorLangProperties);
		allProperties.merge(this.itemProperties);
		allProperties.merge(this.perfectDegreeProperties);
		allProperties.merge(this.marriageProperties);
		allProperties.merge(this.dmgTransferProperties);
		allProperties.merge(this.specialCardProperties);
	}
	
	void printProp(int propID)
	{
		printProp(this.baseProperties, propID, "baseProperties");
		printProp(this.levelProperties, propID, "levelProperties");
		printProp(this.transformProperties, propID , "transformProperties");
		printProp(this.equipProperties, propID, "equipProperties");
		printProp(this.suiteProperties, propID, "suiteProperties");
		printProp(this.fashionProperties, propID, "fashionProperties");
		printProp(this.sealProperties, propID, "sealProperties");
		printProp(this.spiritProperties, propID, "spiritProperties");
		for (FightProperties e : this.weaponProperties.values())
			printProp(e, propID, "weaponProperties");
		for (FightProperties e : this.horseProperties.values())
			printProp(e, propID, "horseProperties");
		printProp(this.curHorseProperties, propID, "curHorseProperties");
		printProp(this.medalProperties, propID, "medalProperties");
		printProp(this.sectAuraProperties, propID, "sectAuraProperties");
		printProp(this.graspProperties, propID, "graspProperties");
		printProp(this.rareBookProperties, propID, "rareBookProperties");
		printProp(this.titlesProperties, propID, "titlesProperties");
		printProp(this.petAchieveProperties, propID, "petAchieveProperties");
		printProp(this.petCoPracticesProperties, propID, "petCoPracticesProperties");
		printProp(this.curFightPetsProperties, propID, "curFightPetsProperties");
		printProp(this.armorProperties, propID, "armorProperties");
		printProp(this.armorLangProperties, propID, "armorLangProperties");
		printProp(this.itemProperties, propID, "itemProperties");
		printProp(this.perfectDegreeProperties, propID, "perfectDegreeProperties");
		printProp(this.marriageProperties, propID, "marriageProperties");
		printProp(this.dmgTransferProperties, propID, "dmgTransferProperties");
		printProp(this.specialCardProperties, propID, "specialCardProperties");
	}
	
	void printProp(FightProperties prop, int propID, String str)
	{
		System.err.println(str + " prop " + propID + " value : " + prop.getProperty(propID));
	}
	
	void updateRobotProperties(int robotConfigId)
	{
		this.baseProperties.clear();
		updateRobotProperties(this.baseProperties, robotConfigId);
	}
	
	void updateBaseProperties(int configID)
	{
		this.baseProperties.clear();
		updateBaseProperties(this.baseProperties, configID);
	}
	
	void updateLevelProperties(int configID, int level)
	{
		this.levelProperties.clear();
		updateLevelProperties(this.levelProperties, configID, level);
	}
	
	void updateTransformProperties(int configID, int transformLevel, int bwType)
	{
		this.transformProperties.clear();
		updateTransformProperties(this.transformProperties, configID, transformLevel, bwType);
	}
	
	void updateEquipProperties(List<SBean.DBEquipPart> equipParts, Map<Integer, SBean.DBWearEquip> wearEquips)
	{
		this.equipProperties.clear();
		updateEquipNormalProperties(this.equipProperties, equipParts, wearEquips.values());
		updateEquipRewardProperties(this.equipProperties, equipParts);
	}
	
	void updateSuiteProperties(Collection<Integer> suites, int classType)
	{
		this.suiteProperties.clear();
		updateSuiteProperties(this.suiteProperties, suites, classType);
	}
	
	void updateFashionProperties(Collection<Integer> fashions)
	{
		this.fashionProperties.clear();
		updateFashionProperties(this.fashionProperties, fashions);
	}
	
	void updateSealProperties(int transformLevel, SBean.DBSealData seal)
	{
		this.sealProperties.clear();
		updateSealProperties(this.sealProperties, seal.grade);
		
		if(this.updatePower)
			this.extraEquipPower = FightProperties.calcEquipPower(configID, seal);
	}
	
	void updateSkillProperties(Collection<SBean.DBSkill> skills, SBean.DBSealData seal)
	{
		if(this.updatePower)
			this.extraSkillsPower = FightProperties.calcSkillsFightPower(skills, seal);
	}
	
	void updateSpiritProperties(Collection<SBean.DBSpirit> spirits, Collection<Integer> curSpirits)
	{
		this.spiritProperties.clear();
		this.spiritTotalLays = updateSpiritProperties(this.spiritProperties, spirits, curSpirits);
		
		if(this.updatePower)
			this.extraSpiritPower = FightProperties.calcSpiritsFightPower(spirits);
	}
	
	void updateWeaponProperties(Collection<SBean.DBWeapon> weapons)
	{
		this.weaponProperties.clear();
//		this.weaponPower.clear();
		int weaponTotalLvls = 0;
		for (SBean.DBWeapon weapon : weapons)
		{
			FightProperties props = new FightProperties();
			weaponTotalLvls += updateWeaponProperties(props, weapon);
			this.weaponProperties.put(weapon.id, props);
//			int power = props.updateFightPower();
//			weapon.fightPower = power;
//			this.weaponPower.put(weapon.id, power);
		}
		this.weaponTotalLvls = weaponTotalLvls;
	}
	
	void updateHorseProperties(Map<Integer, SBean.HorseInfo> horses, int curHorseId, Map<Integer, Integer> allHorseSkills)
	{
		this.horseProperties.clear();
		this.curHorseProperties.clear();
		for (SBean.HorseInfo horse : horses.values())
		{
			FightProperties props = new FightProperties();
			updateHorseProperties(props, horse, 1.0);
			this.horseProperties.put(horse.id, props);
		}
		SBean.HorseInfo curHorse = horses.get(curHorseId);
		if (curHorse != null)
		{
			updateCurHorseProperties(this.curHorseProperties, curHorse, allHorseSkills);
			
			if(this.updatePower)
				this.updateHorseExtraPower(curHorse, allHorseSkills);
		}
	}
	
	void updateMedalsProperties(Map<Integer, Byte> medals)
	{
		this.medalProperties.clear();
		updateMedalsProperties(this.medalProperties, medals);
	}
	
	void updateSectAuraProperties(Map<Integer, Integer> sectAuras)
	{
		this.sectAuraProperties.clear();
		updateSectAuraProperties(this.sectAuraProperties, sectAuras);
	}
	
	void updateGraspProperties(Map<Integer, Integer> grasps, int level)
	{
		this.graspProperties.clear();
		this.graspTotalRaise = updateGraspProperties(this.graspProperties, grasps, level);
	}
	
	void updateRareBookProperties(Map<Integer, Integer> rarebooks, int graspTotalRaise)
	{
		this.rareBookProperties.clear();
		updateRareBookProperties(this.rareBookProperties, rarebooks, graspTotalRaise);
	}
	
	void updateTitleProperties(Collection<Integer> titles)
	{
		this.titlesProperties.clear();
		updateTitleProperties(this.titlesProperties, titles);
	}
	
	void updatePetAchieveProperties(Set<Integer> petAchieves)
	{
		this.petAchieveProperties.clear();
		updatePetAchieveProperties(this.petAchieveProperties, petAchieves);
	}
	
	void updatePetCoPracticesProperties(Map<Integer, Integer> coPractices)
	{
		this.petCoPracticesProperties.clear();
		updatePetCoPracticesProperties(this.petCoPracticesProperties, this.petStar, coPractices);
	}
	
	void updateCurFightPetProperties(Collection<SBean.FightPet> fightPets)
	{
		this.curFightPetsProperties.clear();
		updateCurFightPetProperties(this.curFightPetsProperties, fightPets);
	}
	
	void updateArmorProperties(SBean.ArmorFightData curArmor)
	{
		this.updateArmorNormalProperties(curArmor);
		this.updateArmorLangProperties(curArmor);
		
		if(this.updatePower)
			this.updateArmorExtraPower();
	}
	
	void updateArmorNormalProperties(SBean.ArmorFightData curArmor)
	{
		this.armorProperties.clear();
		if(curArmor == null)
			return;
		
		updateArmorLevelProperties(this.armorProperties, curArmor);
		updateArmorRankProperties(this.armorProperties, curArmor);
	}
	
	void updateArmorLangProperties(SBean.ArmorFightData curArmor)
	{
		this.armorLangProperties.clear();
		if(curArmor == null)
			return;
		
		updateArmorRuneLangProperties(this.armorLangProperties, curArmor);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void updateItemProperties(Map<Integer, Integer> itemProps)
	{
		this.itemProperties.clear();
		updateItemProperties(this.itemProperties, itemProps);
	}
	
	void updatePerfectDegreeProperties(int perfectDegree)
	{
		this.perfectDegreeProperties.clear();
		updatePerfectDegreeProperties(this.perfectDegreeProperties, perfectDegree);
	}
	
	void updateMarriageProperties(int marriageLevel)
	{
		this.marriageProperties.clear();
		updateMarriageProperties(this.marriageProperties, marriageLevel);
	}
	
	void updateDmgTransferProperties(Map<Integer, Integer> pointLvls)
	{
		this.dmgTransferProperties.clear();
		updateDmgTransferProperties(this.dmgTransferProperties, pointLvls);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateSpecialCardFightProperties()
	{
		this.specialCardProperties.clear();
		
		updateSpecialCardFightProperties(this.specialCardProperties, this.specialCardAttr, this.level);
	}
	
	static void updateBaseProperties(FightProperties props, int configID)
	{
		SBean.ClassRoleCFGS classRolecfg = GameData.getInstance().getClassRoleCFG(configID);
		if (classRolecfg != null)
			props.addPropertyFixValue(BaseRole.EPROPID_SPEED, classRolecfg.speed);
		props.addPropertyFixValue(BaseRole.EPROPID_MAXSP, GameData.getInstance().getCommonCFG().general.maxSP);
	}
	
	static void updateLevelProperties(FightProperties props, int configID, double level)
	{
		level = level - 1;
		SBean.ClassRoleCFGS classRolecfg = GameData.getInstance().getClassRoleCFG(configID);
		if (classRolecfg != null)
		{
			props.addPropertyFixValue(BaseRole.EPROPID_MAXHP, classRolecfg.hp.org + classRolecfg.hp.incs1 * level * level + classRolecfg.hp.incs2 * level);
			props.addPropertyFixValue(BaseRole.EPROPID_ATKN, classRolecfg.atkN.org + classRolecfg.atkN.incs1 * level * level + classRolecfg.atkN.incs2 * level);
			props.addPropertyFixValue(BaseRole.EPROPID_DEFN, classRolecfg.defN.org + classRolecfg.defN.incs1 * level * level + classRolecfg.defN.incs2 * level);
			props.addPropertyFixValue(BaseRole.EPROPID_ATR, classRolecfg.atr.org + classRolecfg.atr.incs1 * level * level + classRolecfg.atr.incs2 * level);
			props.addPropertyFixValue(BaseRole.EPROPID_CTR, classRolecfg.ctr.org + classRolecfg.ctr.incs1 * level * level + classRolecfg.ctr.incs2 * level);
			props.addPropertyFixValue(BaseRole.EPROPID_ACRN, classRolecfg.acrN.org + classRolecfg.acrN.incs1 * level * level + classRolecfg.acrN.incs2 * level);
			props.addPropertyFixValue(BaseRole.EPROPID_TOU, classRolecfg.tou.org + classRolecfg.tou.incs1 * level * level + classRolecfg.tou.incs2 * level);
			props.addPropertyFixValue(BaseRole.EPROPID_ATKA, classRolecfg.atkA.org + classRolecfg.atkA.incs1 * level * level + classRolecfg.atkA.incs2 * level);
		}
	}
	
	static void updateRobotProperties(FightProperties props, int robotConfigId)
	{
		SBean.ArenaRobotCFGS robotCfg = GameData.getInstance().getArenaRobotCFGS(robotConfigId);
		props.addPropertyFixValue(BaseRole.EPROPID_MAXHP, robotCfg.attrs.get(BaseRole.EPROPID_MAXHP));
		props.addPropertyFixValue(BaseRole.EPROPID_ATKN, robotCfg.attrs.get(BaseRole.EPROPID_ATKN));
		props.addPropertyFixValue(BaseRole.EPROPID_DEFN, robotCfg.attrs.get(BaseRole.EPROPID_DEFN));
		props.addPropertyFixValue(BaseRole.EPROPID_ATR, robotCfg.attrs.get(BaseRole.EPROPID_ATR));
		props.addPropertyFixValue(BaseRole.EPROPID_CTR, robotCfg.attrs.get(BaseRole.EPROPID_CTR));
		props.addPropertyFixValue(BaseRole.EPROPID_ACRN, robotCfg.attrs.get(BaseRole.EPROPID_ACRN));
		props.addPropertyFixValue(BaseRole.EPROPID_TOU, robotCfg.attrs.get(BaseRole.EPROPID_TOU));
		props.addPropertyFixValue(BaseRole.EPROPID_ATKA, robotCfg.attrs.get(BaseRole.EPROPID_ATKA));
		props.addPropertyFixValue(BaseRole.EPROPID_DEFA, robotCfg.attrs.get(BaseRole.EPROPID_DEFA));
		props.addPropertyFixValue(BaseRole.EPROPID_ATKH, robotCfg.attrs.get(BaseRole.EPROPID_ATKH));
		props.addPropertyFixValue(BaseRole.EPROPID_ATKC, robotCfg.attrs.get(BaseRole.EPROPID_ATKC));
		props.addPropertyFixValue(BaseRole.EPROPID_DEFC, robotCfg.attrs.get(BaseRole.EPROPID_DEFC));
		props.addPropertyFixValue(BaseRole.EPROPID_ATKW, robotCfg.attrs.get(BaseRole.EPROPID_ATKW));
		props.addPropertyFixValue(BaseRole.EPROPID_DEFW, robotCfg.attrs.get(BaseRole.EPROPID_DEFW));
		props.addPropertyFixValue(BaseRole.EPROPID_MASTERC, robotCfg.attrs.get(BaseRole.EPROPID_MASTERC));
		props.addPropertyFixValue(BaseRole.EPROPID_MASTERW, robotCfg.attrs.get(BaseRole.EPROPID_MASTERW));
		props.addPropertyFixValue(BaseRole.EPROPID_HEALA, robotCfg.attrs.get(BaseRole.EPROPID_HEALA));
		
		updateBaseProperties(props, robotCfg.classType);
	}
	
	static void updateTransformProperties(FightProperties props, int configID, int transformLevel, int bwType)
	{
		for (int lvl = 1; lvl <= transformLevel; lvl++)
		{
			SBean.TransformCFGS transformCfg = GameData.getInstance().getTransformCFGS(configID, lvl, lvl > 1 ? bwType : 0);
			if (transformCfg != null)
				transformCfg.attrs.stream().filter(attr -> attr.id > 0).forEach(attr -> props.addPropertyFixValue(attr.id, attr.value));
		}
	}
	
	static void updateEquipNormalProperties(FightProperties props, SBean.DBEquipPart equipPart, SBean.DBWearEquip wearEquip)
	{
		if (wearEquip != null)
		{
			if (wearEquip.equip.durability < 0 || wearEquip.equip.durability > GameData.getInstance().getCommonCFG().equip.disableValue)
			{
				SBean.EquipCFGS equipCfgs = GameData.getInstance().getEquipCFG(wearEquip.equip.id);
				if(equipCfgs != null)
				{
					double baseAdd = wearEquip.equip.durability < 0 ? 0 : GameData.getInstance().getLegenOneBaseAdd(wearEquip.equip.legends.get(0));
					double addtionAdd = wearEquip.equip.durability < 0 ? 0 : GameData.getInstance().getLegendTwoAddtionAdd(wearEquip.equip.legends.get(1));
					
					updateEquipBaseProp(props, baseAdd, equipCfgs, equipPart, wearEquip);
					updateEquipAddProp(props, addtionAdd, equipCfgs, wearEquip);
					updateEquipRefineProp(props, wearEquip.equip);
					updateEquipLegendThreeProp(props, wearEquip.equip, equipCfgs.type);
				}
			}
		}
	}
	
	static void updateEquipBaseProp(FightProperties props, double baseAdd, SBean.EquipCFGS equipCfgs, SBean.DBEquipPart equipPart, SBean.DBWearEquip wearEquip)
	{
		//基础属性值
		for (SBean.EquipBasePropCFGS baseArg : equipCfgs.baseProp)
		{
			double value = baseArg.value;
			if (equipPart.eqGrowLvl > 0)
				value += baseArg.growUp.get(equipPart.eqGrowLvl - 1);
			if (baseArg.advEffect == 1)
			{
				SBean.EquipUpStarCFGS equipStarCfg = GameData.getInstance().getEquipUpStarCFGS(equipPart.eqEvoLvl);
				if (equipStarCfg != null)
				{
					double per = 0;
					int fixed = 0;
					switch (baseArg.type)
					{
					case BaseRole.EPROPID_ATKN:
						per = equipStarCfg.atkNUp;
						fixed = (int) equipStarCfg.atkFixedUp;
						break;
					case BaseRole.EPROPID_DEFN:
						per = equipStarCfg.defNUp;
						fixed = (int) equipStarCfg.defFixedUp;
						break;
					case BaseRole.EPROPID_MAXHP:
						per = equipStarCfg.maxHpUp;
						fixed = (int) equipStarCfg.maxHpFixedUp;
						break;
					default:
						break;
					}
					value *= (1 + per);
					value += fixed;
				}
			}
			value *= 1 + baseAdd;
			props.addPropertyFixValue(baseArg.type, value);
		}
		//装备部位表，升星附加属性值
		SBean.EquipStarAddPropCFGS starAddCfg = GameData.getInstance().getEquipStartAddPropCFGS(equipPart.id);
		if(starAddCfg != null)
		{
			for(Map.Entry<Integer, SBean.StarAdditionProp> e: starAddCfg.additionProp.entrySet())
			{
				if(equipPart.eqEvoLvl < e.getKey())
					continue;
				
				for(SBean.AttrCFGS attr: e.getValue().prop)
					props.addPropertyFixValue(attr.id, attr.value);
			}
		}
	}
	
	static void updateEquipAddProp(FightProperties props, double addtionAdd, SBean.EquipCFGS equipCfgs, SBean.DBWearEquip wearEquip)
	{
		for (int i = 0; i < wearEquip.equip.addValues.size(); i++)
		{
			SBean.EquipAdditPropCFGS additProp = equipCfgs.additProp.get(i);
			if (additProp.type == GameData.EQUIP_ADDPROP_TYPE1)
			{
				int value = (int) (wearEquip.equip.addValues.get(i) * (1 + addtionAdd));
				props.addPropertyFixValue(additProp.arg, value);
			}
		}
	}
	
	static void updateEquipRefineProp(FightProperties props, SBean.DBEquip equip)
	{
		for(SBean.Prop p: equip.refine)
			props.addPropertyFixValue(p.id, p.value);
	}
	
	static void updateEquipLegendThreeProp(FightProperties props, SBean.DBEquip equip, int partID)
	{
		int legendThree = equip.legends.get(2);
		if(legendThree > 0)
		{
			SBean.LegendThreeCFGS ltCfg = GameData.getInstance().getLegengThreeCFGS(partID, legendThree);
			if(ltCfg != null && ltCfg.type == GameData.LEGEND_EQUIP_THREE_TYPE_ADDPROP)
			{
				int propID = ltCfg.params.get(0);
				int valueTyp = ltCfg.params.get(1);
				int value = ltCfg.params.get(2);
				
				if(valueTyp == GameData.VALUE_TYPE_FIXED)
					props.addPropertyFixValue(propID, value);
				else
					props.addPropertyPercentValue(propID, value);
			}
		}
	}
	
	static void updateEquipNormalProperties(FightProperties props, List<SBean.DBEquipPart> equipParts, Collection<SBean.DBWearEquip> wearEquips)
	{
		//装备属性
		for (SBean.DBWearEquip wearEquip : wearEquips)
		{
			updateEquipNormalProperties(props, equipParts.get(wearEquip.wid - 1), wearEquip);
		}
		
		for(SBean.DBEquipPart equipPart: equipParts)
		{
			updateEquipPartProperties(props, equipPart);
		}
	}
	
	static void updateEquipPartProperties(FightProperties props, SBean.DBEquipPart equipPart)
	{
		for (int stoneID : equipPart.eqSlots)
		{
			SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(stoneID);
			if (cfg != null)
				props.addPropertyFixValue(cfg.propID, cfg.propVal);
		}
	}
	
	static void updateEquipRewardProperties(FightProperties props, List<SBean.DBEquipPart> equipParts)
	{
		//宝石属性值、升星、强化奖励、宝石奖励
		int awardPartNum = GameData.getInstance().getCommonCFG().equip.awardPartNum;
		int awardSlotNum = GameData.getInstance().getCommonCFG().equip.awardSlotNum;
		int minGrowLvl = Integer.MAX_VALUE;
		int minStarLvl = Integer.MAX_VALUE;
		int minStoneLvl = Integer.MAX_VALUE;
		int partsGrowCount = 0;
		int partsStartCount = 0;
		int partsStoneCount = 0;
		for (SBean.DBEquipPart equipPart : equipParts)
		{
			if (equipPart.eqGrowLvl > 0)
			{
				++partsGrowCount;
				if (equipPart.eqGrowLvl < minGrowLvl)
					minGrowLvl = equipPart.eqGrowLvl;
			}
			if (equipPart.eqEvoLvl > 0)
			{
				++partsStartCount;
				if (equipPart.eqEvoLvl < minStarLvl)
					minStarLvl = equipPart.eqEvoLvl;
			}
			int slotInlayCount = 0;
			int slotInlayMinStoneLvl = Integer.MAX_VALUE;
			for (int stoneID : equipPart.eqSlots)
			{
				SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(stoneID);
				if (cfg != null)
				{
					++slotInlayCount;
					if (cfg.level < slotInlayMinStoneLvl)
						slotInlayMinStoneLvl = cfg.level;
				}
			}
			if (slotInlayCount >= awardSlotNum)
			{
				++partsStoneCount;
				if (slotInlayMinStoneLvl < minStoneLvl)
					minStoneLvl = slotInlayMinStoneLvl;
			}
		}
		if (partsGrowCount >= awardPartNum)
		{
			List<SBean.PropAwardCFGS> growLst = GameData.getInstance().getPropTypeAward(GameData.EQUIP_PROP_AWARD_GROW);
			if (growLst != null)
			{
				for (SBean.PropAwardCFGS p : growLst)
				{
					if (minGrowLvl < p.conditionArg)
						break;
					for (SBean.AttrCFGS a : p.awards)
						props.addPropertyFixValue(a.id, a.value);
				}
			}
		}
		if (partsStartCount >= awardPartNum)
		{
			List<SBean.PropAwardCFGS> starLst = GameData.getInstance().getPropTypeAward(GameData.EQUIP_PROP_AWARD_STAR);
			if (starLst != null)
			{
				for (SBean.PropAwardCFGS p : starLst)
				{
					if (minStarLvl < p.conditionArg)
						break;

					for (SBean.AttrCFGS a : p.awards)
						props.addPropertyFixValue(a.id, a.value);
				}
			}
		}
		if (partsStoneCount >= awardPartNum)
		{
			List<SBean.PropAwardCFGS> stoneLst = GameData.getInstance().getPropTypeAward(GameData.EQUIP_PROP_AWARD_STONE);
			if (stoneLst != null)
			{
				for (SBean.PropAwardCFGS p : stoneLst)
				{
					if (minStoneLvl < p.conditionArg)
						break;

					for (SBean.AttrCFGS a : p.awards)
						props.addPropertyFixValue(a.id, a.value);
				}
			}
		}
	}
	
	static void updateSuiteProperties(FightProperties props, Collection<Integer> suites, int classType)
	{
		for (int suitesId : suites)
		{
			SBean.SuiteCFGS cfg = GameData.getInstance().getSuites(suitesId);
			if (cfg != null)
			{
				double rate = classType == cfg.classType ? SUIT_SELF_RATE : SUIT_OTHER_RATE;
				cfg.attris.forEach(attr -> props.addPropertyFixValue(attr.id, attr.value * rate));
			}
		}
	}
	
	static void updateFashionProperties(FightProperties props, Collection<Integer> fashions)
	{
		for (int fid : fashions)
		{
			SBean.FashionCFGS fashionCfg = GameData.getInstance().getFashionCFGS(fid);
			if (fashionCfg != null)
				fashionCfg.attrs.forEach(attr -> props.addPropertyFixValue(attr.id, attr.value));
		}
	}

	static void updateSealProperties(FightProperties props, int sealLevel)
	{
		SBean.SealGradeCFGS gradeCfg = GameData.getInstance().getSealGradeCFGS(sealLevel);
		if(gradeCfg != null)
			gradeCfg.attrs.forEach(attr -> props.addPropertyFixValue(attr.id, attr.value));
	}
	
	static int updateSpiritProperties(FightProperties props, SBean.DBSpirit spirit)
	{
		int lays = 0;
		SBean.SpiritCFGS cfg = GameData.getInstance().getSpiritCFGS(spirit.id);
		if (cfg != null)
		{
			for (int level = 0; level <= spirit.level; level++)
			{
				SBean.SpiritGrowUpCFGS growCfg = cfg.growups.get(level);
				if (growCfg != null)
					growCfg.attrs.stream().filter(attr -> attr.id > 0).forEach(attr -> props.addPropertyFixValue(attr.id, attr.value));
			}
			lays = spirit.level / GameData.SPIRIT_LEVLE_PERLAY;					
		}
		return lays;
	}
	
	static int updateSpiritProperties(FightProperties props, Collection<SBean.DBSpirit> spirits, Collection<Integer> curSpirits)
	{
		int spiritTotalLays = 0;
		for (SBean.DBSpirit dbSpirit : spirits)
		{
			spiritTotalLays += updateSpiritProperties(props, dbSpirit);
			if (curSpirits.contains(dbSpirit.id))
				updateCurSpiritProperties(props, dbSpirit);
		}
		return spiritTotalLays;
	}
	
	static void updateCurSpiritProperties(FightProperties props, SBean.DBSpirit spirit)
	{
		SBean.SpiritCFGS cfg = GameData.getInstance().getSpiritCFGS(spirit.id);
		if (cfg != null)
		{
			SBean.SpiritGrowUpCFGS curGrowCfg = cfg.growups.get(spirit.level);
			if (curGrowCfg != null)
			{
				for (Integer eid : curGrowCfg.effectsIds)
				{
					SBean.SpiritEffectCFGS effectCfg = GameData.getInstance().getSpiritEffectCFGS(eid);
					if (effectCfg != null && effectCfg.type == GameData.SPIRIT_EFFECT_PROP)
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

	static int updateWeaponProperties(FightProperties props, SBean.DBWeapon weapon)
	{
		//神兵属性，暂时加到装备属性
		int weaponTotalLvls = 0;
		SBean.WeaponCFGS cfg = GameData.getInstance().getWeaponCFGS(weapon.id);
		if (cfg != null)
		{
			//等级属性
			if (weapon.level > 0 && weapon.level <= cfg.weaponLevel.size())
			{
				for (SBean.AttrCFGS attr : cfg.weaponLevel.get(weapon.level - 1).attrs)
					props.addPropertyFixValue(attr.id, attr.value);
			}

			//星级属性
			if (weapon.star >= 0 && weapon.star < cfg.weaponLevel.size())
			{
				SBean.WeaponStarCFGS starcfg = cfg.weaponStar.get(weapon.star);
				for (SBean.AttrCFGS attr : starcfg.attrs)
					props.addPropertyFixValue(attr.id, attr.value);
				weaponTotalLvls += starcfg.quality;
			}	
		}
		return weaponTotalLvls;
	}
	
	static void updateHorseProperties(FightProperties props, SBean.HorseInfo info, double multiplue)
	{
		SBean.HorseCFGS horseCfg = GameData.getInstance().getHorseCFGS(info.id);
		if (horseCfg != null)
		{
			//升星属性
			SBean.HorseGrowUpCGFS g = GameData.getHorseGrowUpCGFS(horseCfg, info.star);
			if (g != null)
			{
				for (SBean.AttrCFGS attr : g.attrs)
				{
					props.addPropertyFixValue(attr.id, attr.value * multiplue);
				}
			}

			//洗练属性
			for (SBean.Prop enhance : info.enhanceAttrs)
			{
				if (enhance.id != 0)
					props.addPropertyFixValue(enhance.id, enhance.value * multiplue);
			}
		}
	}
	

	static void updateCurHorseProperties(FightProperties props, SBean.HorseInfo info, Map<Integer, Integer> allHorseSkills)
	{
		updateCurHorseMultipleProperties(props, info);
//		updateCurHorseSkillPropperties(props, info, allHorseSkills);
	}
	
	static void updateCurHorseMultipleProperties(FightProperties props, SBean.HorseInfo info)
	{
		double multiple = GameData.getInstance().getHorseCommonCFGS().inuseAdd / 10000.0 - 1.0;
		updateHorseProperties(props, info, multiple);
	}
	
	static void updateMedalsProperties(FightProperties props, Map<Integer, Byte> medals)
	{
		for (Map.Entry<Integer, Byte> e : medals.entrySet())
		{
			int medalID = e.getKey();
			SBean.MedalCFGS medalCfg = GameData.getInstance().getMedalCFGS(medalID);
			if (medalCfg != null)
			{
				double add = e.getValue() == 1 ? 2.0 : 1.0;
				for (SBean.AttrCFGS attr : medalCfg.attrs)
					props.addPropertyFixValue(attr.id, attr.value * add);				
			}
		}
	}
	
	static void updateSectAuraProperties(FightProperties props, Map<Integer, Integer> sectAuras)
	{
		for (Map.Entry<Integer, Integer> e : sectAuras.entrySet())
		{
			SBean.SectSkillCFGS skillCfg = GameData.getInstance().getSectSkillCFGS(e.getKey(), e.getValue());
			if (skillCfg != null)
				props.addPropertyFixValue(skillCfg.attri, skillCfg.attriValue);
		}
	}
	
//	static void updateClanDiziTangProperties(FightProperties props, SBean.ClanOwnerAttriAddition clanDiziTang)
//	{
//		if(clanDiziTang != null)
//		{
//			props.addPropertyFixValue(BaseRole.EPROPID_ATKH, clanDiziTang.ssAddHarm);
//			props.addPropertyFixValue(BaseRole.EPROPID_ATKC, clanDiziTang.xfAddHarm);
//			props.addPropertyFixValue(BaseRole.EPROPID_ATKW, clanDiziTang.sbAddHarm);
//			props.addPropertyFixValue(BaseRole.EPROPID_MAXHP, clanDiziTang.qxAddHarm);
//		}
//	}
	
	static int updateGraspProperties(FightProperties props, Map<Integer, Integer> grasps, int level)
	{
		if(level < GameData.getInstance().getExpCoinBaseCFGS().lvlReq)
			return 0;
		
		int raise = 0;
		for(Map.Entry<Integer, Integer> e: grasps.entrySet())
		{
			SBean.GraspCFGS cfg = GameData.getInstance().getGraspCFGS(e.getKey(), e.getValue());
			if(cfg == null)
				continue;
			
			props.addPropertyFixValue(cfg.attr.id, cfg.attr.value);
			raise += cfg.rarebookRaise;
		}
		
		return raise;
	}
	
	static void updateRareBookProperties(FightProperties props, Map<Integer, Integer> rarebooks, int graspTotalRaise)
	{
		double raise = graspTotalRaise / 10_000.0;
		for(Map.Entry<Integer, Integer> e: rarebooks.entrySet())
		{
			SBean.RareBookCFGS cfg = GameData.getInstance().getRareBookCFGS(e.getKey(), e.getValue());
			if(cfg == null)
				continue;
			
			for(SBean.AttrCFGS attr: cfg.attrs)
				props.addPropertyFixValue(attr.id, attr.value * (1.0 + raise));
		}
	}
	
	static void updateTitleProperties(FightProperties props, Collection<Integer> titles)
	{
		for(int id: titles)
		{
			SBean.TitleCFGS cfg = GameData.getInstance().getTitleCFGS(id);
			if(cfg == null)
				continue;
			
			for(SBean.AttrCFGS attr: cfg.attrs)
				props.addPropertyFixValue(attr.id, attr.value);
		}
	}
	
	static void updatePetAchieveProperties(FightProperties props, Set<Integer> petAchieves)
	{
		for(int id: petAchieves)
		{
			SBean.PetAchieveCFGS cfg = GameData.getInstance().getPetAchieveCFGS(id);
			if(cfg == null)
				continue;
			
			for(SBean.AttrCFGS attr: cfg.attrs)
				props.addPropertyFixValue(attr.id, attr.value);
		}
	}
	
	static void updatePetCoPracticesProperties(FightProperties props, Map<Integer, Integer> star, Map<Integer, Integer> coPractices)
	{
		for(Map.Entry<Integer, Integer> e: coPractices.entrySet())
			PropPet.updateCoPracticeProperties(props, star.getOrDefault(e.getKey(), 0), e.getKey(), e.getValue());
	}
	
	static void updateCurFightPetProperties(FightProperties props, Collection<SBean.FightPet> fightPets)
	{
		for(SBean.FightPet p: fightPets)
			updatePetSpiritProperties(props, p.curSpirits, GameData.PET_SPIRIT_EFFECT_TYPE_ROLE_PROP);
	}
	
	static void updateItemProperties(FightProperties props, Map<Integer, Integer> itemProps)
	{
		for(Map.Entry<Integer, Integer> e: itemProps.entrySet())
		{
			props.addPropertyFixValue(e.getKey(), e.getValue());
		}
	}
	
	static void updatePerfectDegreeProperties(FightProperties props, int perfectDegree)
	{
		List<SBean.AttrCFGS> attrs = GameData.getInstance().getHeirloomProps(perfectDegree);
		for(SBean.AttrCFGS e: attrs)
		{
			props.addPropertyFixValue(e.id, e.value);
		}
	}
	
	static void updateMarriageProperties(FightProperties props, int marriageLevel)
	{
		SBean.MarriageAttributeCFGS cfg = GameData.getInstance().getMarriageAttrCFGS(marriageLevel);
		if(cfg != null)
		{
			for(SBean.AttrCFGS attr: cfg.properties)
			{
				props.addPropertyFixValue(attr.id, attr.value);
			}
		}
	}
	
	static void updateDmgTransferProperties(FightProperties props, Map<Integer, Integer> pointLvls)
	{
		for(Map.Entry<Integer, Integer> e: pointLvls.entrySet())
		{
			int id = e.getKey();
			int lvl = e.getValue();
			SBean.DMGTransferCFGS cfg = GameData.getInstance().getDMGTransferCFGS(id, lvl);
			if(cfg == null)
				continue;
			
			if(cfg.value > 0)
				props.addPropertyFixValue(cfg.propID, cfg.value);
		}
	}
	
	public boolean onUpdatePet(SBean.FightPet pet)
	{
		if (pet.star != this.petStar.getOrDefault(pet.id, 0))
		{
			this.petStar.put(pet.id, pet.star);
			return pet.star == GameData.getInstance().getPetStarLimit(pet.id);
		}
		return false;
	}
}
