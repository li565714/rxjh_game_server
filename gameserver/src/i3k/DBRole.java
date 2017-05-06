package i3k;

import i3k.gmap.PropRole;
import i3k.gs.GameData;
import i3k.gs.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

import ket.util.Stream;

public class DBRole implements Stream.IStreamable
{
	public static int VERSION_NOW = 2;
	
	public DBRole() { }

	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		int dbVersion = is.popInteger();
		
		id = is.popInteger();
		if (register == null)
			register = new SBean.DBRegister();
		is.pop(register);
		name = is.popString();
		createTime = is.popInteger();
		lastLoginTime = is.popInteger();
		lastLogoutTime = is.popInteger();
		lastOnlineTime = is.popInteger();
		totalOnlineTime = is.popInteger();
		
		headIcon = is.popShort();
		gender = is.popByte();
		face = is.popByte();
		hair = is.popByte();
		
		classType = is.popByte();
		transformLevel = is.popByte();
		BWType = is.popByte();
		
		
		if(worldMapLocation == null)
			worldMapLocation = new SBean.MapLocation();
		is.pop(worldMapLocation);
		
		
		lastDayRefresh = is.popInteger();
		loginDays = is.popInteger();
		

		level = is.popInteger();
		exp = is.popLong();
		lastLevelUpTime = is.popInteger();
		expVolume = is.popLong();
		hp = is.popInteger();
		lastUseHpTime = is.popInteger();
		lastSpawnReviveTime = is.popInteger();
		lastDiamondReviveTime = is.popInteger();
		diamondReviveTimes = is.popInteger();
		hpPool = is.popInteger();
		lastUseHpPoolTime = is.popInteger();
		vit = is.popInteger();
		vitRevertTime = is.popInteger();
		pkValue = is.popInteger();
		pkValueTime = is.popInteger();
		totalPower = is.popInteger();
		historyHighestPower = is.popInteger();
		
		//recoverPoints = is.popList(SBean.DBRecoverPoint.class);
		
		equipEnergy = is.popInteger();
		gemEnergy = is.popInteger();
		bookInspiration = is.popInteger();
		
		totalPayPoint = is.popInteger();
		payLevelTimes = is.popIntegerIntegerTreeMap();
		payRewardTimes = is.popIntegerIntegerTreeMap();
		diamondF = is.popInteger();
		diamondR = is.popInteger();
		coinF = is.popInteger();
		coinR = is.popInteger();
		credit = is.popInteger();
		
		diamondFUseTotal = is.popInteger();
		diamondRUseTotal = is.popInteger();
		coinFUseTotal = is.popInteger();
		coinRUseTotal = is.popInteger();
		creditUseTotal = is.popInteger();

		historyMaxGemLevel = is.popInteger();
		historyMaxGemNum = is.popInteger();
		
		dayBuyCoinTimes = is.popInteger();
		dayBuyVitTimes = is.popInteger();
		dayUseVit = is.popInteger();
		dayUseItemsTimes = is.popIntegerIntegerTreeMap();

		specialCards = is.popList(SBean.DBSpecialCardData.class);
		if(tempVIP==null)
			tempVIP = new SBean.DBTempVIP();
		is.pop(tempVIP);
		vipRewards = is.popIntegerTreeSet();
		
		if(bag == null)
			bag = new SBean.DBItemCells();
		is.pop(bag);
		
		
		wearParts = is.popList(SBean.DBEquipPart.class);
		wearEquips = is.popIntegerTreeMap(SBean.DBWearEquip.class);
		suites = is.popIntegerTreeMap(SBean.DBSuite.class);
		logWearEquips = is.popIntegerIntegerHashMap();
		if(sealData == null)
			sealData = new SBean.DBSealData();
		is.pop(sealData);
		if(legendMake == null)
			legendMake = new SBean.DBLegendMake();
		is.pop(legendMake);
		
		skills = is.popIntegerTreeMap(SBean.DBSkill.class);
		curSkills = is.popIntegerList();
		curUniqueSkill = is.popInteger();
		
		spirits = is.popIntegerTreeMap(SBean.DBSpirit.class);
		curSpirits = is.popIntegerHashSet();
		
		buffs = is.popIntegerTreeMap(SBean.DBBuff.class);
		
		if(weaponTask == null)
			weaponTask = new SBean.DBWeaponTask();
		is.pop(weaponTask);		
		weapons = is.popIntegerTreeMap(SBean.DBWeapon.class);
		curWeapon = is.popInteger();
		if(weaponMapLog == null)
			weaponMapLog = new SBean.DBWeaponMapLog();
		is.pop(weaponMapLog);
		
		activePets = is.popIntegerTreeMap(SBean.DBPet.class);
		worldMapPets = is.popIntegerTreeSet();
		privateMapPets = is.popIntegerTreeSet();
		sectMapPets = is.popIntegerTreeSet(); 
		activityMapPets = is.popIntegerTreeSet();
		petSpirits = is.popIntegerIntegerHashMap();

		
		if(mainTask == null)
			mainTask = new SBean.DBMainTask();
		is.pop(mainTask);
		
		branchTask = is.popIntegerHashMap(SBean.DBBranchTask.class);
		
		if(alterState == null)
			alterState = new SBean.DBAlterState();
		is.pop(alterState);
		
		normalMapCopyLogs = is.popIntegerHashMap(SBean.DBNormalMapCopyLog.class);
		activityMapGroupLogs = is.popIntegerHashMap(SBean.DBActivityMapGroupLog.class);

		if(checkinLog == null)
			checkinLog = new SBean.DBCheckInLog();
		is.pop(checkinLog);	
		
		mallBuyLogs = is.popIntegerHashMap(SBean.DBRoleMallLog.class); 
		firstPayGiftLogs = is.popIntegerHashMap(SBean.DBRoleFirstPayGiftLog.class); 
		payGiftLogs = is.popIntegerHashMap(SBean.DBRolePayGiftLog.class); 
		consumeGiftLogs = is.popIntegerHashMap(SBean.DBRoleConsumeGiftLog.class); 
		upgradeGiftLogs = is.popIntegerHashMap(SBean.DBRoleUpgradeGiftLog.class); 
		investmentFundLogs = is.popIntegerHashMap(SBean.DBRoleInvestmentFundLog.class); 
		growthFundLogs = is.popIntegerHashMap(SBean.DBRoleGrowthFundLog.class);
		exchangeGiftLogs = is.popIntegerHashMap(SBean.DBRoleExchangeGiftLog.class);
		loginGiftLogs = is.popIntegerHashMap(SBean.DBRoleLoginGiftLog.class);
		dailyPayGiftLogs = is.popIntegerHashMap(SBean.DBRoleDailyPayGiftLog.class);
		groupBuyLogs = is.popIntegerHashMap(SBean.DBRoleGroupBuyLog.class);
		flashSaleLogs = is.popIntegerHashMap(SBean.DBRoleFlashSaleLog.class);
		lastPayGiftLogs = is.popIntegerHashMap(SBean.DBRoleLastPayGiftLog.class);
		activityChallengeGiftLogs = is.popIntegerHashMap(SBean.DBRoleActivityChallengeGiftLog.class);
		luckyRollerLogs = is.popIntegerHashMap(SBean.DBRoleLuckyRollerLog.class);
		upgradePurchaseLogs = is.popIntegerHashMap(SBean.DBRoleUpgradePurchaseLog.class);
		giftPackageLogs = is.popIntegerHashMap(SBean.DBRoleGiftPackageLog.class);
		directPurchaseLogs = is.popIntegerHashMap(SBean.DBRoleDirectPurchaseLog.class);
		oneArmBanditLogs = is.popIntegerHashMap(SBean.DBRoleOneArmBanditLog.class);

		shops = is.popIntegerTreeMap(SBean.DBShop.class);
		gambleShops = is.popIntegerTreeMap(SBean.DBGambleShop.class);
		dailyTasks = is.popIntegerTreeMap(SBean.DBDailyTask.class);
		challengeTasks = is.popIntegerTreeMap(SBean.DBChallengeTask.class);
		achPoints = is.popIntegerIntegerTreeMap();
		if (fame == null)
			fame = new SBean.DBFame();
		is.pop(fame);
		if (dailyOnlineGift == null)
			dailyOnlineGift = new SBean.DBDailyOnlineGift();
		is.pop(dailyOnlineGift);
		if (offlineExp == null)
			offlineExp = new SBean.DBOfflineExp();
		is.pop(offlineExp);
		quizGift = is.popIntegerTreeMap(SBean.DBQuizGift.class);
		
		luckyWheelSelectPos = is.popIntegerList();
		if(luckyWheel == null)
			luckyWheel = new SBean.DBLuckyWheel();
		is.pop(luckyWheel);
		
		daySnatchRedEnvelopes = is.popInteger();
		dayGetRedEnvelopesEmptyGift = is.popInteger();
		
		if(sectData == null)
			sectData = new SBean.DBRoleSectData();
		is.pop(sectData);
		
		
		if(diySkillData == null)
			diySkillData = new SBean.DBRoleDiySkillData();
		is.pop(diySkillData);

		if(roleArenaData == null)
			roleArenaData = new SBean.DBRoleArenaData();
		is.pop(roleArenaData);
		
		if(friendData == null)
			friendData = new SBean.DBFriendData();
		is.pop(friendData);
		
		if(auctionInfo == null)
			auctionInfo = new SBean.DBAuctionInfo();
		is.pop(auctionInfo);
		
		if(horseData == null)
			horseData = new SBean.DBHorse();
		is.pop(horseData);
		
		if(treasureData == null)
			treasureData = new SBean.DBTreasure();
		is.pop(treasureData);
		
		if(expCoinData == null)
			expCoinData = new SBean.DBExpCoinData();
		is.pop(expCoinData);
		
		if(rarebookData == null)
			rarebookData = new SBean.DBRareBook();
		is.pop(rarebookData);
		
		if(graspData == null)
			graspData = new SBean.DBGrasp();
		is.pop(graspData);
		
		if(dmgTransfer == null)
			dmgTransfer = new SBean.DBDMGTransfer();
		is.pop(dmgTransfer);
		
		if(fashionEquip == null)
			fashionEquip = new SBean.DBRoleFashion();
		is.pop(fashionEquip);
		if(roleTitles == null)
			roleTitles = new SBean.DBRoleTitle();
		is.pop(roleTitles);
		
		leadInfo = is.popIntegerHashSet();
		leadPlot = is.popIntegerIntegerHashMap();
		isTreasureGuide = is.popByte();
		
		if (climbTowerData == null)
			climbTowerData = new SBean.DBClimbTower();
		is.pop(climbTowerData);
		
		climbTowerRecordData = is.popIntegerHashMap(SBean.DBClimbTowerRecordData.class);
		climbTowerFameData = is.popIntegerHashMap(SBean.DBClimbTowerFame.class);
		
		towerCards = is.popIntegerList();
		if (secretAreaTaskData == null)
			secretAreaTaskData = new SBean.DBSecretTaskData();
		is.pop(secretAreaTaskData);
		
		if(sectDeliver == null)
			sectDeliver = new SBean.DBSectDeliver();
		is.pop(sectDeliver);
		
		if(escortCar == null)
			escortCar = new SBean.DBEscortCar();
		is.pop(escortCar);
		
		if(produce == null)
			produce = new SBean.DBProduceData();
		is.pop(produce);
		
		isRob = is.popByte();
		if(forcewar == null)
			forcewar = new SBean.DBRoleForceWar();
		is.pop(forcewar);
		
		rmactivity = is.popInteger();
		
		messageBoardDayCommentTimes = is.popInteger();
		guidestep = is.popInteger();
		
		if(schedule == null)
			schedule = new SBean.DBSchedule();
		is.pop(schedule);
		
		if(armor == null)
			armor = new SBean.DBArmor();
		is.pop(armor);
		armorVal = is.popInteger();
		
		if(warehouse == null)
			warehouse = new SBean.DBItemCells();
		is.pop(warehouse);
		
		if(marriageData == null)
			marriageData = new SBean.DBRoleMarriageData();
		is.pop(marriageData);
		exchangeTimes = is.popIntegerIntegerHashMap();
		skillPreset = is.popList(SBean.DBSkillPreset.class);
		spiritsPreset = is.popList(SBean.DBSpiritsPreset.class);
		if(dailyVit==null)
			dailyVit = new SBean.DBDailyVitData();
		is.pop(dailyVit);
		lifeUse = is.popIntegerIntegerHashMap();
		itemGetProp = is.popIntegerIntegerHashMap();
		if(insightData==null)
			insightData = new SBean.DBInsightData();
		is.pop(insightData);
		if(revengeData==null)
			revengeData = new SBean.DBRevengeData();
		is.pop(revengeData);
		if(heirloomData==null)
			heirloomData = new SBean.DBHeirloom();
		is.pop(heirloomData);
		if(offlineWizard==null)
			offlineWizard = new SBean.DBOfflineWizard();
		is.pop(offlineWizard);
		if(activityLast==null)
			activityLast = new SBean.DBActivityLastData();
		is.pop(activityLast);
		
		if(stele == null)
			stele = new SBean.DBRoleStele();
		is.pop(stele);
		dayJusticeJoinTime = is.popInteger();

		if(demonHole == null)
			demonHole = new SBean.RoleDemonHoleLog();
		is.pop(demonHole);
		levelUpTimeMap = is.popIntegerIntegerTreeMap();
		
		if(lucklyStar == null)
			lucklyStar = new SBean.DBLucklyStar();
		is.pop(lucklyStar);
		
		if(fightNpc == null)
			fightNpc = new SBean.DBFightNpc();
		is.pop(fightNpc);
		packetReward = is.popInteger();
		itemUnlockHead = is.popIntegerHashSet();

		roleSocialComment = is.popIntegerHashMap(SBean.DBRoleSocialComment.class);
		showFashionTypes = is.popIntegerIntegerHashMap();
		socailpadding1_3 = is.popByte();
		socailpadding1_4 = is.popByte();
		socailpadding2   = is.popInteger();
		socailpadding3   = is.popInteger();
		
		redNamePunish = is.popNullable(SBean.DBRedNamePunish.class);
		dayNpcPrayTimes = is.popIntegerIntegerHashMap();
		dayNpcCpoyTimes = is.popIntegerIntegerHashMap();
		towerDefence = is.popNullable(SBean.DBRoleTowerDefence.class);
		
		fusionData = is.popNullable(SBean.DBFusionData.class);
		byte bsMaster = is.popByte();
		master = new DBMaster();
		if( bsMaster == 0 )
			master.makeNew();
		else
			is.pop(master);
		payRankLogs = is.popList(SBean.DBRolePayRankLog.class);
		padding2_4 = is.popByte();
		padding3 = is.popInteger();
		padding4 = is.popInteger();
		padding5 = is.popInteger();
		padding6 = is.popInteger();
		padding7 = is.popInteger();
		padding8 = is.popInteger();
		padding9 = is.popInteger();
		padding10 = is.popInteger();
		
		onDBVersionChange(dbVersion);
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushInteger(id);
		os.push(register);
		os.pushString(name);
		os.pushInteger(createTime);
		os.pushInteger(lastLoginTime);
		os.pushInteger(lastLogoutTime);
		os.pushInteger(lastOnlineTime);
		os.pushInteger(totalOnlineTime);
		
		os.pushShort(headIcon);
		os.pushByte(gender);
		os.pushByte(face);
		os.pushByte(hair);
		
		
		os.pushByte(classType);		
		os.pushByte(transformLevel);
		os.pushByte(BWType);
		
		os.push(worldMapLocation);
		
		os.pushInteger(lastDayRefresh);
		os.pushInteger(loginDays);
		
		os.pushInteger(level);
		os.pushLong(exp);
		os.pushInteger(lastLevelUpTime);
		os.pushLong(expVolume);
		os.pushInteger(hp);
		os.pushInteger(lastUseHpTime);
		os.pushInteger(lastSpawnReviveTime);
		os.pushInteger(lastDiamondReviveTime);
		os.pushInteger(diamondReviveTimes);
		os.pushInteger(hpPool);
		os.pushInteger(lastUseHpPoolTime);
		os.pushInteger(vit);
		os.pushInteger(vitRevertTime);
		os.pushInteger(pkValue);
		os.pushInteger(pkValueTime);
		os.pushInteger(totalPower);
		os.pushInteger(historyHighestPower);
		
		//os.pushList(recoverPoints);

		os.pushInteger(equipEnergy);
		os.pushInteger(gemEnergy);
		os.pushInteger(bookInspiration);
		
		os.pushInteger(totalPayPoint);
		os.pushIntegerIntegerMap(payLevelTimes);
		os.pushIntegerIntegerMap(payRewardTimes);
		os.pushInteger(diamondF);
		os.pushInteger(diamondR);
		os.pushInteger(coinF);
		os.pushInteger(coinR);
		os.pushInteger(credit);
		
		os.pushInteger(diamondFUseTotal);
		os.pushInteger(diamondRUseTotal);
		os.pushInteger(coinFUseTotal);
		os.pushInteger(coinRUseTotal);
		os.pushInteger(creditUseTotal);

		os.pushInteger(historyMaxGemLevel);
		os.pushInteger(historyMaxGemNum);
		
		os.pushInteger(dayBuyCoinTimes);
		os.pushInteger(dayBuyVitTimes);
		os.pushInteger(dayUseVit);
		os.pushIntegerIntegerMap(dayUseItemsTimes);

		os.pushList(specialCards);
		os.push(tempVIP);
		os.pushIntegerSet(vipRewards);
		
		os.push(bag);
		
		os.pushList(wearParts);
		os.pushIntegerMap(wearEquips);
		os.pushIntegerMap(suites);
		os.pushIntegerIntegerMap(logWearEquips);
		os.push(sealData);
		os.push(legendMake);
		
		os.pushIntegerMap(skills);
		os.pushIntegerList(curSkills);
		os.pushInteger(curUniqueSkill);
		
		os.pushIntegerMap(spirits);
		os.pushIntegerSet(curSpirits);
		
		os.pushIntegerMap(buffs);
		
		os.push(weaponTask);
		os.pushIntegerMap(weapons);
		os.pushInteger(curWeapon);
		os.push(weaponMapLog);
		
		os.pushIntegerMap(activePets);
		os.pushIntegerSet(worldMapPets);
		os.pushIntegerSet(privateMapPets);
		os.pushIntegerSet(sectMapPets);
		os.pushIntegerSet(activityMapPets);
		os.pushIntegerIntegerMap(petSpirits);

		os.push(mainTask);
		os.pushIntegerMap(branchTask);
		os.push(alterState);
		
		os.pushIntegerMap(normalMapCopyLogs);
		os.pushIntegerMap(activityMapGroupLogs);

		os.push(checkinLog);
		
		os.pushIntegerMap(mallBuyLogs);
		os.pushIntegerMap(firstPayGiftLogs);
		os.pushIntegerMap(payGiftLogs);
		os.pushIntegerMap(consumeGiftLogs);
		os.pushIntegerMap(upgradeGiftLogs);
		os.pushIntegerMap(investmentFundLogs);
		os.pushIntegerMap(growthFundLogs);
		os.pushIntegerMap(exchangeGiftLogs);
		os.pushIntegerMap(loginGiftLogs);
		os.pushIntegerMap(dailyPayGiftLogs);
		os.pushIntegerMap(groupBuyLogs);
		os.pushIntegerMap(flashSaleLogs);
		os.pushIntegerMap(lastPayGiftLogs);
		os.pushIntegerMap(activityChallengeGiftLogs);
		os.pushIntegerMap(luckyRollerLogs);
		os.pushIntegerMap(upgradePurchaseLogs);
		os.pushIntegerMap(giftPackageLogs);
		os.pushIntegerMap(directPurchaseLogs);
		os.pushIntegerMap(oneArmBanditLogs);

		os.pushIntegerMap(shops);
		os.pushIntegerMap(gambleShops);
		os.pushIntegerMap(dailyTasks);
		os.pushIntegerMap(challengeTasks);
		os.pushIntegerIntegerMap(achPoints);
		os.push(fame);
		os.push(dailyOnlineGift);
		os.push(offlineExp);
		os.pushIntegerMap(quizGift);
		os.pushIntegerList(luckyWheelSelectPos);
		os.push(luckyWheel);
		
		os.pushInteger(daySnatchRedEnvelopes);
		os.pushInteger(dayGetRedEnvelopesEmptyGift);
		
		os.push(sectData);
		
		os.push(diySkillData);
		
		os.push(roleArenaData);
		os.push(friendData);
		os.push(auctionInfo);
		os.push(horseData);
		os.push(treasureData);
		os.push(expCoinData);
		os.push(rarebookData);
		os.push(graspData);
		os.push(dmgTransfer);
		os.push(fashionEquip);
		os.push(roleTitles);
		os.pushIntegerSet(leadInfo);
		os.pushIntegerIntegerMap(leadPlot);
		os.pushByte(isTreasureGuide);
		
		os.push(climbTowerData);
		os.pushIntegerMap(climbTowerRecordData);
		os.pushIntegerMap(climbTowerFameData);
		os.pushIntegerList(towerCards);
		os.push(secretAreaTaskData);
		
		os.push(sectDeliver);
		os.push(escortCar);
		os.push(produce);
		
		os.pushByte(isRob);
		os.push(forcewar);
		os.pushInteger(rmactivity);
		
		os.pushInteger(messageBoardDayCommentTimes);
		os.pushInteger(guidestep);
		os.push(schedule);
		os.push(armor);
		os.pushInteger(armorVal);
		os.push(warehouse);
		os.push(marriageData);
		os.pushIntegerIntegerMap(exchangeTimes);
		os.pushList(skillPreset);
		os.pushList(spiritsPreset);
		os.push(dailyVit);
		os.pushIntegerIntegerMap(lifeUse);
		os.pushIntegerIntegerMap(itemGetProp);
		os.push(insightData);
		os.push(revengeData);
		os.push(heirloomData);
		os.push(offlineWizard);
		os.push(activityLast);
		os.push(stele);
		os.pushInteger(dayJusticeJoinTime);
		os.push(demonHole);
		os.pushIntegerIntegerMap(levelUpTimeMap);
		os.push(lucklyStar);
		os.push(fightNpc);
		os.pushInteger(packetReward);
		os.pushIntegerSet(itemUnlockHead);
		
		os.pushIntegerMap(roleSocialComment);
		os.pushIntegerIntegerMap(showFashionTypes);
		os.pushByte(socailpadding1_3);
		os.pushByte(socailpadding1_4);
		os.pushInteger(socailpadding2);
		os.pushInteger(socailpadding3);
		
		os.pushNullable(redNamePunish);
		os.pushIntegerIntegerMap(dayNpcPrayTimes);
		os.pushIntegerIntegerMap(dayNpcCpoyTimes);
		os.pushNullable(towerDefence);
		
		os.pushNullable(fusionData);
		os.pushByte((byte)1);
		os.push(master);
		os.pushList(payRankLogs);
		os.pushByte(padding2_4);
		os.pushInteger(padding3);
		os.pushInteger(padding4);
		os.pushInteger(padding5);
		os.pushInteger(padding6);
		os.pushInteger(padding7);
		os.pushInteger(padding8);
		os.pushInteger(padding9);
		os.pushInteger(padding10);
	}
	
	private void onDBVersionChange(int dbVersion)
	{
		switch (dbVersion)
		{
		case 1:
			//TODO
			break;
		default:
			break;
		}
	}

	public String getUsername()
	{
		return GameData.getUserName(this.register.id);
	}
	
	public SBean.RoleBrief getRoleBrief()
	{
		return new SBean.RoleBrief(this.getRoleOverview(), this.getRoleModel());
	}
	
	public SBean.RoleModel getRoleModel()
	{
		Map<Integer, Integer> equips = new HashMap<>();
		for (int i = 0; i < GameData.EQUIP_MAX_PARTNUM; ++i)
			equips.put(i + 1, Optional.<SBean.DBWearEquip>ofNullable(this.wearEquips.getOrDefault(i+1, null)).map(e -> e.equip.id).orElse(0));
		
		List<SBean.EquipPart> equipParts = new ArrayList<>();
		for(SBean.DBEquipPart part: this.wearParts)
			equipParts.add(new SBean.EquipPart(part.id, part.eqGrowLvl, part.eqEvoLvl));
		return new SBean.RoleModel(this.face, this.hair, equips, equipParts, new HashMap<>(this.fashionEquip.curFashions), new HashMap<>(this.showFashionTypes),
								   Role.getArmorBriefWithoutLock(this.armor.allArmors, this.armor.curArmor), Role.getHeirloomBriefWithoutLock(this.heirloomData));
	}
	
	public SBean.RoleOverview getRoleOverview()
	{
		return new SBean.RoleOverview(this.id, this.name, this.gender, this.headIcon, this.classType, this.transformLevel, this.BWType, this.level, this.totalPower);
	}
	
	public SBean.RoleWearDetail getRoleWearDetail()
	{
		return new SBean.RoleWearDetail(this.face, this.hair, new HashMap<>(this.fashionEquip.curFashions), new HashMap<>(this.showFashionTypes), this.sealData.kdClone(), Stream.clone(this.wearEquips), Stream.clone(this.wearParts), Role.getArmorBriefWithoutLock(this.armor.allArmors, this.armor.curArmor), Role.getHeirloomBriefWithoutLock(this.heirloomData));
	}
	
	private PropRole createPropRole(Map<Integer, Integer> sectAuras, int marriageLevel)
	{
		return new PropRole(true).createNew(this.getBasePlayer(sectAuras, marriageLevel), getWorldFightPet(), false);
	}
	
	private int getSkillsLevelSum()
	{
		int sum = 0;
		for(SBean.DBSkill skill : skills.values())
		{
			sum += skill.level;
		}
		return sum;
	}
	
	private int getSpiritLevelSum()
	{
		int sum = 0;
		for(SBean.DBSpirit spirit : spirits.values())
		{
			sum += spirit.level;
		}
		return sum;
	}
	
	private int getUskillLevel()
	{
		SBean.DBSkill skill = this.skills.get(this.curUniqueSkill);
		if (skill == null)
			return 0;
		return skill.level;
	}
	
	public SBean.RoleAchievement getRoleAchievement()
	{
		return new SBean.RoleAchievement(this.weapons.size(), this.activePets.size(), this.treasureData.medals.size(), this.getSkillsLevelSum(), this.getSpiritLevelSum(), this.getUskillLevel());
	}
	
	public SBean.RoleFeature getRoleFeature(Map<Integer, Integer> sectAuras, int marriageLevel)
	{
		PropRole propRole = createPropRole(sectAuras, marriageLevel);
		return new SBean.RoleFeature(this.getRoleOverview(), this.getRoleWearDetail(), propRole.getRoleProperties(), propRole.getRolePowerDetail(), this.getRoleAchievement(), this.getRoleRelationship());
	}
	
	public SBean.RoleRelationship getRoleRelationship()
	{
		return new SBean.RoleRelationship(this.sectData.data.sectBrief.sectName, this.marriageData.partnerName);
	}
	
	public SBean.PetOverview getPetOverview(int pid)
	{
		SBean.DBPet pet = this.activePets.get(pid);
		return pet == null ? null : getPetOverview(pet);
	}
	
	public static SBean.PetOverview getPetOverview(SBean.DBPet pet)
	{
		return new SBean.PetOverview(pet.fightPet.id, pet.fightPet.level, pet.fightPet.star, pet.fightPet.fightPower);
	}
	
	public List<SBean.PetOverview> getPetOverviews()
	{
		return this.activePets.values().stream().map(DBRole::getPetOverview).collect(Collectors.toList());
	}
	
	public SBean.FightRole getFightRole(Map<Integer, Integer> sectAuras, SBean.SectBrief sectBrief, int marriageLevel)
	{
		SBean.FightRole role = new SBean.FightRole(this.getBasePlayer(sectAuras, marriageLevel), this.name, this.gender, this.headIcon, this.face, this.hair, 
													new ArrayList<>(this.curSkills), this.curUniqueSkill, this.curWeapon, this.getCurDIYSkillData(), 
													sectBrief, this.totalPower, new HashMap<>(this.showFashionTypes), this.heirloomData.display);
		return role;
	}
	
	public static SBean.WeaponOverview getWeaponOverview(SBean.DBWeapon dbWeapon)
	{
		return new SBean.WeaponOverview(dbWeapon.id, dbWeapon.level, dbWeapon.star, dbWeapon.fightPower);
	}
	
	public List<SBean.WeaponOverview> getWeaponOverviews()
	{
		return this.weapons.values().stream().map(DBRole::getWeaponOverview).collect(Collectors.toList());
	}
	
	public SBean.DBDIYSkillData getCurDIYSkillData()
	{
		if(diySkillData.curSkillId <= 0 || diySkillData.curSkillId > diySkillData.diySkills.size())
			return null;
		SBean.DBDiySkill diySkill = this.diySkillData.diySkills.get(this.diySkillData.curSkillId - 1);
		if(diySkill == null)
			return null;
		return diySkill.diySkillData;
	}
	
	public Map<Integer, SBean.FightPet> getWorldFightPet()
	{
		return this.worldMapPets.stream().collect(Collectors.toMap(pid -> pid, pid -> getFightPet(pid)));
	}
	
	public SBean.FightPet getFightPet(int pid)
	{
		SBean.DBPet pet = this.activePets.get(pid);
		if (pet == null)
			return null;
		
		return pet.fightPet.kdClone();
	}

	public SBean.PetHost getPetHost(Map<Integer, Integer> sectAuras, int marriageLevel)
	{
		return createPropRole(sectAuras, marriageLevel).getMapPetHost();
	}
	
	
	public SBean.BasePlayer getBasePlayer(Map<Integer, Integer> sectAuras, int marriageLevel)
	{
		return new SBean.BasePlayer(this.id, this.classType, this.transformLevel, this.BWType, this.level, 
				GameData.getInstance().getCollectSuites(Stream.clone(this.suites)), Stream.clone(this.wearEquips), 
				Stream.clone(this.wearParts), Stream.clone(this.skills), Stream.clone(this.weapons), 
				Stream.clone(this.spirits), new HashSet<>(this.curSpirits), sectAuras, 
				this.horseData.kdClone(), new HashMap<>(this.treasureData.medals), new HashMap<Integer, Integer>(this.fashionEquip.curFashions),
				Stream.clone(this.sealData), new HashMap<>(this.rarebookData.books), this.getGrasps(), Role.initRoleTitle(this.roleTitles),
				Role.getNewPetAchieves(this.activePets), Role.getPetCoPractices(this.activePets), Role.getArmorFightData(this.armor.allArmors, this.armor.curArmor), 
				new HashMap<>(this.itemGetProp), this.heirloomData.isOpen == 0 ? 0 : this.heirloomData.perfectDegree, marriageLevel, 
				new HashMap<>(this.dmgTransfer.levels), this.activePets.values().stream().collect(Collectors.toMap((pet) -> pet.fightPet.id, (pet) -> pet.fightPet.star)),
				Role.getSpecialCardAttrs(this.specialCards));
	}
	
	public Map<Integer, Integer> getGrasps()
	{
		return this.graspData.grasps.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().lvl));
	}
	
	public SBean.FriendOverview getFriendOverview()
	{
		return new SBean.FriendOverview(this.getRoleOverview(), this.friendData.personalMsg, this.lastLogoutTime, (byte)0);
	}
	
	public SBean.FlowerOverview getRoleFlowerOverview(int vipLevel, int contribution)
	{
		return new SBean.FlowerOverview(this.getRoleOverview(), vipLevel, this.friendData.charm, contribution);
	}

	public SBean.SectBrief getSectBrief()
	{
		return this.sectData.data.sectBrief;
	}
	
	//注册账号相关数据
	public int id;
	public SBean.DBRegister register;
	public String name;
	public int createTime;
	public int lastLoginTime;
	public int lastLogoutTime;
	public int lastOnlineTime;//用于程序崩溃未能及时保存logoutTime的情形
	public int totalOnlineTime;
	
	public short headIcon;
	public byte gender;
	public byte face;
	public byte hair;
	
	public byte classType;
	public byte transformLevel;
	public byte BWType;
	
	public SBean.MapLocation worldMapLocation;
	
	public int lastDayRefresh;
	public int loginDays;
	
	public int level;
	public long exp;
	public int lastLevelUpTime;
	public long expVolume;
	public int hp;
	public int lastUseHpTime;
	public int lastSpawnReviveTime;
	public int lastDiamondReviveTime;
	public int diamondReviveTimes;
	public int hpPool;
	public int lastUseHpPoolTime;
	public int vit;
	public int vitRevertTime;
	public int pkValue;
	public int pkValueTime;
	public int totalPower;
	public int historyHighestPower;
	
	public SBean.DBRoleArenaData roleArenaData;	
	public int equipEnergy;
	public int gemEnergy;
	public int bookInspiration;
	
	public int totalPayPoint;
	public Map<Integer, Integer> payLevelTimes;
	public Map<Integer, Integer> payRewardTimes;
	public int diamondF;
	public int diamondR;
	public int coinF;
	public int coinR;
	public int credit;
	public SBean.DBItemCells bag;
	
	public int diamondFUseTotal;
	public int diamondRUseTotal;
	public int coinFUseTotal;
	public int coinRUseTotal;
	public int creditUseTotal;
	public int historyMaxGemLevel;
	public int historyMaxGemNum;
	
	public int dayBuyCoinTimes;
	public int dayBuyVitTimes;
	public int dayUseVit;
	public Map<Integer, Integer> dayUseItemsTimes;

	public List<SBean.DBSpecialCardData> specialCards;
	public SBean.DBTempVIP tempVIP;
	public Set<Integer> vipRewards;
	
	
	public List<SBean.DBEquipPart> wearParts;
	public Map<Integer, SBean.DBWearEquip> wearEquips;
	public Map<Integer, SBean.DBSuite> suites;
	public Map<Integer, Integer> logWearEquips;
	public SBean.DBSealData sealData;
	public SBean.DBLegendMake legendMake;
	
	public Map<Integer, SBean.DBSkill> skills;
	public List<Integer> curSkills;
	public int curUniqueSkill;
	
	public Map<Integer, SBean.DBSpirit> spirits;
	public Set<Integer> curSpirits;
	
	public Map<Integer, SBean.DBBuff> buffs;
	
	public SBean.DBWeaponTask weaponTask;
	public Map<Integer, SBean.DBWeapon> weapons;
	public int curWeapon;
	public SBean.DBWeaponMapLog weaponMapLog;
	
	public Map<Integer, SBean.DBPet> activePets;
	public Set<Integer> worldMapPets;
	public Set<Integer> privateMapPets;
	public Set<Integer> sectMapPets;
	public Set<Integer> activityMapPets;
	public Map<Integer, Integer> petSpirits;
	
	
	public SBean.DBMainTask mainTask;
	public Map<Integer, SBean.DBBranchTask> branchTask;
	public SBean.DBAlterState alterState;
	
	public Map<Integer, SBean.DBNormalMapCopyLog> normalMapCopyLogs;
	public Map<Integer, SBean.DBActivityMapGroupLog> activityMapGroupLogs;
	public SBean.DBCheckInLog checkinLog;
	public Map<Integer, SBean.DBRoleMallLog> mallBuyLogs;
	public Map<Integer, SBean.DBRoleFirstPayGiftLog> firstPayGiftLogs;
	public Map<Integer, SBean.DBRolePayGiftLog> payGiftLogs;
	public Map<Integer, SBean.DBRoleConsumeGiftLog> consumeGiftLogs;
	public Map<Integer, SBean.DBRoleUpgradeGiftLog> upgradeGiftLogs;
	public Map<Integer, SBean.DBRoleInvestmentFundLog> investmentFundLogs;
	public Map<Integer, SBean.DBRoleGrowthFundLog> growthFundLogs;
	public Map<Integer, SBean.DBRoleExchangeGiftLog> exchangeGiftLogs;
	public Map<Integer, SBean.DBRoleLoginGiftLog> loginGiftLogs;
	public Map<Integer, SBean.DBRoleDailyPayGiftLog> dailyPayGiftLogs;
	public Map<Integer, SBean.DBRoleGroupBuyLog> groupBuyLogs;
	public Map<Integer, SBean.DBRoleFlashSaleLog> flashSaleLogs;
	public Map<Integer, SBean.DBRoleLastPayGiftLog> lastPayGiftLogs;
	public Map<Integer, SBean.DBRoleActivityChallengeGiftLog> activityChallengeGiftLogs;
	public Map<Integer, SBean.DBRoleLuckyRollerLog> luckyRollerLogs;
	public Map<Integer, SBean.DBRoleUpgradePurchaseLog> upgradePurchaseLogs;
	public Map<Integer, SBean.DBRoleGiftPackageLog> giftPackageLogs;
	public Map<Integer, SBean.DBRoleDirectPurchaseLog> directPurchaseLogs;
	public Map<Integer, SBean.DBRoleOneArmBanditLog> oneArmBanditLogs;

	public Map<Integer, SBean.DBShop> shops;
	public Map<Integer, SBean.DBGambleShop> gambleShops;
	
	public Map<Integer, SBean.DBDailyTask> dailyTasks;
	public Map<Integer, SBean.DBChallengeTask> challengeTasks;
	public Map<Integer, Integer> achPoints;
	public SBean.DBFame fame;
	public SBean.DBDailyOnlineGift dailyOnlineGift;
	public SBean.DBOfflineExp offlineExp;
	public Map<Integer, SBean.DBQuizGift> quizGift;
	public List<Integer> luckyWheelSelectPos;
	public SBean.DBLuckyWheel luckyWheel;
	public int daySnatchRedEnvelopes;
	public int dayGetRedEnvelopesEmptyGift;
	
	public SBean.DBRoleSectData sectData;
	
	
	public SBean.DBRoleDiySkillData diySkillData;
	
	public SBean.DBFriendData friendData;
	
	public SBean.DBAuctionInfo auctionInfo;
	public SBean.DBHorse horseData;
	public SBean.DBTreasure treasureData;
	public SBean.DBExpCoinData expCoinData;
	public SBean.DBRareBook rarebookData;
	public SBean.DBGrasp graspData;
	public SBean.DBDMGTransfer dmgTransfer;
	public SBean.DBRoleFashion fashionEquip;
	public SBean.DBRoleTitle roleTitles;
	
	public Set<Integer> leadInfo;
	public Map<Integer, Integer> leadPlot;
	public byte isTreasureGuide;
	
	public SBean.DBClimbTower climbTowerData;
	public Map<Integer, SBean.DBClimbTowerRecordData> climbTowerRecordData;
	public Map<Integer, SBean.DBClimbTowerFame> climbTowerFameData;
	public List<Integer> towerCards;
	public SBean.DBSecretTaskData secretAreaTaskData;
	public SBean.DBSectDeliver sectDeliver;
	public SBean.DBEscortCar escortCar;
	public SBean.DBProduceData produce;
	
	public byte isRob;
	public SBean.DBRoleForceWar forcewar;
	public int rmactivity;
	public int messageBoardDayCommentTimes;
	public int guidestep;
	public SBean.DBSchedule schedule;
	public SBean.DBArmor armor;//内甲
	public int armorVal;
	
	public SBean.DBItemCells warehouse;
	public SBean.DBRoleMarriageData marriageData;
	public Map<Integer, Integer> exchangeTimes;
	public List<SBean.DBSkillPreset> skillPreset;
	public List<SBean.DBSpiritsPreset> spiritsPreset;
	public SBean.DBDailyVitData dailyVit;
	public Map<Integer, Integer> lifeUse;
	public Map<Integer, Integer> itemGetProp;
	public SBean.DBInsightData insightData;
	public SBean.DBRevengeData revengeData;
	public SBean.DBHeirloom heirloomData;
	public SBean.DBOfflineWizard offlineWizard;
	public SBean.DBActivityLastData activityLast; //活动补做次数
	public SBean.DBRoleStele stele;
	public int dayJusticeJoinTime;
	public SBean.RoleDemonHoleLog demonHole;
	public Map<Integer, Integer> levelUpTimeMap;
	public SBean.DBLucklyStar lucklyStar;
	public SBean.DBFightNpc fightNpc;
	public int packetReward;
	public Set<Integer> itemUnlockHead;
	public Map<Integer, SBean.DBRoleSocialComment> roleSocialComment;	//	byte socailpadding1_1;
	public Map<Integer, Integer> showFashionTypes;							//	byte socailpadding1_2;
	byte socailpadding1_3;
	byte socailpadding1_4;
	int socailpadding2;
	int socailpadding3;
	
	public SBean.DBRedNamePunish redNamePunish;
	public Map<Integer, Integer> dayNpcPrayTimes;
	public Map<Integer, Integer> dayNpcCpoyTimes;
	public SBean.DBRoleTowerDefence towerDefence;
	
	//public int fusionPoint; //public int padding2;
	public SBean.DBFusionData fusionData; //public int padding2_1;
	public DBMaster master; //public int padding2_2;
	public List<SBean.DBRolePayRankLog> payRankLogs;
	public byte padding2_4;
	public int padding3;
	public int padding4;
	public int padding5;
	public int padding6;
	public int padding7;
	public int padding8;
	public int padding9;
	public int padding10;

}
