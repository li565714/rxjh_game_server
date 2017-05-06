// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.rpc;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;

import ket.util.Stream;
import ket.kio.SimplePacket;

import i3k.SBean;
public abstract class Packet
{

	// server to client
	public static final int eS2CPKTServerChallenge = 1;
	public static final int eS2CPKTServerResponse = 2;
	public static final int eS2CPKTLuaChannel = 5;
	public static final int eS2CPKTStrChannel = 6;
	public static final int eS2CPKTLuaChannel2 = 7;

	// client to server
	public static final int eC2SPKTClientResponse = 10001;
	public static final int eC2SPKTLuaChannel = 10004;
	public static final int eC2SPKTLuaChannel2 = 10005;
	public static final int eC2SPKTStrChannel = 10006;

	// server to map
	public static final int eS2MPKTKeepAlive = 20001;
	public static final int eS2MPKTSyncTimeOffset = 20002;
	public static final int eS2MPKTLuaChannel = 20003;
	public static final int eS2MPKTStrChannel = 20004;
	public static final int eS2MPKTSyncDoubleDropCfg = 20005;
	public static final int eS2MPKTSyncExtraDropCfg = 20006;
	public static final int eS2MPKTSyncWorldNum = 20007;
	public static final int eS2MPKTStartMapCopy = 20011;
	public static final int eS2MPKTEndMapCopy = 20012;
	public static final int eS2MPKTMapCopyReady = 20013;
	public static final int eS2MPKTResetSectMap = 20014;
	public static final int eS2MPKTResetArenaMap = 20015;
	public static final int eS2MPKTResetBWArenaMap = 20020;
	public static final int eS2MPKTEnterMap = 20030;
	public static final int eS2MPKTLeaveMap = 20031;
	public static final int eS2MPKTResetLocation = 20032;
	public static final int eS2MPKTUpdateActive = 20033;
	public static final int eS2MPKTUpdateEquip = 20036;
	public static final int eS2MPKTUpdateEquipPart = 20037;
	public static final int eS2MPKTUpdateSkill = 20038;
	public static final int eS2MPKTUpdateCurSkills = 20039;
	public static final int eS2MPKTUpdateBuff = 20040;
	public static final int eS2MPKTUpdateLevel = 20041;
	public static final int eS2MPKTAddHp = 20042;
	public static final int eS2MPKTUpdateWeapon = 20043;
	public static final int eS2MPKTUpdateCurWeapon = 20044;
	public static final int eS2MPKTUpdateSpirit = 20045;
	public static final int eS2MPKTUpdateCurSpirit = 20046;
	public static final int eS2MPKTStartMine = 20047;
	public static final int eS2MPKTRoleRevive = 20048;
	public static final int eS2MPKTUpdatePet = 20049;
	public static final int eS2MPKTUpdateTeam = 20050;
	public static final int eS2MPKTChangeCurPets = 20052;
	public static final int eS2MPKTUpdateSectAura = 20053;
	public static final int eS2MPKTResetSectAuras = 20054;
	public static final int eS2MPKTUpdatePKInfo = 20055;
	public static final int eS2MPKTUpdateCurDIYSkill = 20056;
	public static final int eS2MPKTUpdateTransformInfo = 20057;
	public static final int eS2MPKTCreateWorldMapBoss = 20058;
	public static final int eS2MPKTDestroyWorldMapBoss = 20059;
	public static final int eS2MPKTInitWorldBoss = 20060;
	public static final int eS2MPKTCreateWorldMapSuperMonster = 20061;
	public static final int eS2MPKTCreateWorldMapMineral = 20062;
	public static final int eS2MPKTGainNewSuite = 20070;
	public static final int eS2MPKTUpdateSectBrief = 20071;
	public static final int eS2MPKTUpdateHorseInfo = 20072;
	public static final int eS2MPKTUpdateCurUseHorse = 20073;
	public static final int eS2MPKTUpdateMedal = 20074;
	public static final int eS2MPKTUpWearFashion = 20075;
	public static final int eS2MPKTUpdateAlterState = 20076;
	public static final int eS2MPKTChangeHorseShow = 20077;
	public static final int eS2MPKTAddBuff = 20078;
	public static final int eS2MPKTUpdateSealGrade = 20080;
	public static final int eS2MPKTUpdateSealSkills = 20081;
	public static final int eS2MPKTSyncRolePetLack = 20082;
	public static final int eS2MPKTUpdateRoleGrasp = 20084;
	public static final int eS2MPKTUpdateRareBook = 20085;
	public static final int eS2MPKTUpdateRoleTitle = 20086;
	public static final int eS2MPKTUpdateRoleCurTitle = 20087;
	public static final int eS2MPKTUpdatePetAchieve = 20088;
	public static final int eS2MPKTUpdateCurUniqueSkill = 20089;
	public static final int eS2MPKTSetPetAlter = 20090;
	public static final int eS2MPKTCarEnterMap = 20091;
	public static final int eS2MPKTCarLeaveMap = 20092;
	public static final int eS2MPKTCarUpdateTeamCarCnt = 20093;
	public static final int eS2MPKTUpdateRoleCarBehavior = 20094;
	public static final int eS2MPKTRoleUseItemSkill = 20095;
	public static final int eS2MPKTRoleRename = 20096;
	public static final int eS2MPKTAddPetHp = 20097;
	public static final int eS2MPKTSyncCurRideHorse = 20098;
	public static final int eS2MPKTUpdateMulHorse = 20099;
	public static final int eS2MPKTChangeArmor = 20100;
	public static final int eS2MPKTUpdateArmorLevel = 20101;
	public static final int eS2MPKTUpdateArmorRank = 20102;
	public static final int eS2MPKTUpdateArmorRune = 20103;
	public static final int eS2MPKTUpdateTalentPoint = 20104;
	public static final int eS2MPKTSpawnSceneMonster = 20105;
	public static final int eS2MPKTClearSceneMonster = 20106;
	public static final int eS2MPKTResetSectGroupMap = 20107;
	public static final int eS2MPKTUpdateHorseSkill = 20108;
	public static final int eS2MPKTUpdateStayWith = 20109;
	public static final int eS2MPKTUpdateRoleWeaponSkill = 20110;
	public static final int eS2MPKTUpdateRoleWeaponTalent = 20111;
	public static final int eS2MPKTCreateWorldMapFlag = 20112;
	public static final int eS2MPKTInitWorldMapFlag = 20113;
	public static final int eS2MPKTSyncMapFlagInfo = 20114;
	public static final int eS2MPKTSyncRoleItemProps = 20115;
	public static final int eS2MPKTSyncTaskDrop = 20116;
	public static final int eS2MPKTUpdateRolePetSkill = 20117;
	public static final int eS2MPKTSyncWeaponOpen = 20118;
	public static final int eS2MPKTWorldBossPop = 20119;
	public static final int eS2MPKTPickUpResult = 20120;
	public static final int eS2MPKTUnSummonCurPets = 20121;
	public static final int eS2MPKTUpdateRolePerfectDegree = 20122;
	public static final int eS2MPKTUpdateCurPetSpirit = 20123;
	public static final int eS2MPKTStartMarriageParade = 20124;
	public static final int eS2MPKTUpdateRoleHeirloomDisplay = 20125;
	public static final int eS2MPKTSetWeaponForm = 20126;
	public static final int eS2MPKTStartMarriageBanquet = 20127;
	public static final int eS2MPKTUpdateRoleMarriageSkillInfo = 20128;
	public static final int eS2MPKTUpdateRoleMarriageSkillLevel = 20129;
	public static final int eS2MPKTMarriageLevelChange = 20130;
	public static final int eS2MPKTRoleDMGTransferUpdate = 20131;
	public static final int eS2MPKTCreateRobotHero = 20132;
	public static final int eS2MPKTDestroyRobotHero = 20133;
	public static final int eS2MPKTSyncCreateStele = 20134;
	public static final int eS2MPKTSyncDestroyStele = 20135;
	public static final int eS2MPKTSyncJusticeNpcShow = 20136;
	public static final int eS2MPKTSyncJusticeNpcLeave = 20137;
	public static final int eS2MPKTSyncEmergencyLastTime = 20138;
	public static final int eS2MPKTSyncRoleVipLevel = 20139;
	public static final int eS2MPKTSyncRoleCurWizardPet = 20140;
	public static final int eS2MPKTUpdateRoleSpecialCardAttr = 20141;
	public static final int eS2MPKTRoleShowProps = 20142;
	public static final int eS2MPKTRoleRedNamePunish = 20143;
	public static final int eS2MPKTGMCommand = 20144;

	// map to server
	public static final int eM2SPKTKeepAlive = 30001;
	public static final int eM2SPKTWhoAmI = 30002;
	public static final int eM2SPKTLuaChannel = 31001;
	public static final int eM2SPKTStrChannel = 31002;
	public static final int eM2SPKTStrChannelBroadcast = 31003;
	public static final int eM2SPKTMapRoleReady = 31100;
	public static final int eM2SPKTNearByRoleMove = 31101;
	public static final int eM2SPKTNearByRoleStopMove = 31102;
	public static final int eM2SPKTNearByRoleEnter = 31103;
	public static final int eM2SPKTNearByRoleLeave = 31104;
	public static final int eM2SPKTSyncCommonMapCopyStart = 30010;
	public static final int eM2SPKTSyncCommonMapCopyEnd = 30011;
	public static final int eM2SPKTSyncSectMapCopyStart = 30012;
	public static final int eM2SPKTSyncSectMapCopyProgress = 30013;
	public static final int eM2SPKTSyncArenaMapCopyStart = 30014;
	public static final int eM2SPKTSyncArenaMapCopyEnd = 30015;
	public static final int eM2SPKTSyncBWArenaMapCopyStart = 30026;
	public static final int eM2SPKTSyncBWArenaMapCopyEnd = 30027;
	public static final int eM2SPKTSyncPetLifeMapCopyStart = 30028;
	public static final int eM2SPKTSyncLocation = 30031;
	public static final int eM2SPKTSyncHp = 30032;
	public static final int eM2SPKTAddDrops = 30034;
	public static final int eM2SPKTAddKill = 30037;
	public static final int eM2SPKTSyncDurability = 30038;
	public static final int eM2SPKTSyncEndMine = 30039;
	public static final int eM2SPKTAddPKValue = 30040;
	public static final int eM2SPKTSyncWorldMapBossProgress = 30041;
	public static final int eM2SPKTSyncCurRideHorse = 30042;
	public static final int eM2SPKTSyncCarLocation = 30045;
	public static final int eM2SPKTSyncCarHp = 30046;
	public static final int eM2SPKTUpdateCarDamage = 30047;
	public static final int eM2SPKTSyncRoleRobSuccess = 30048;
	public static final int eM2SPKTUpdateRoleCarRobber = 30049;
	public static final int eM2SPKTKickRoleFromMap = 30050;
	public static final int eM2SPKTRoleUseItemSkillSuc = 30051;
	public static final int eM2SPKTUpdateRoleFightState = 30052;
	public static final int eM2SPKTSyncRolePetHp = 30053;
	public static final int eM2SPKTSyncRoleSp = 30054;
	public static final int eM2SPKTSyncWorldMapBossRecord = 30055;
	public static final int eM2SPKTSyncArmorVal = 30056;
	public static final int eM2SPKTSyncSectGroupMapCopyStatus = 30057;
	public static final int eM2SPKTSyncSectGroupMapCopyResult = 30058;
	public static final int eM2SPKTSyncSectGroupMapCopyStart = 30059;
	public static final int eM2SPKTSyncSectGroupMapCopyProgress = 30060;
	public static final int eM2SPKTSyncSectGroupMapCopyAddKill = 30061;
	public static final int eM2SPKTSyncMapFlagCanTake = 30062;
	public static final int eM2SPKTSyncWeaponMaster = 30063;
	public static final int eM2SPKTRolePickUpDrops = 30064;
	public static final int eM2SPKTSyncSuperMonster = 30065;
	public static final int eM2SPKTSyncWorldMineral = 30066;
	public static final int eM2SPKTSyncMarriageParadeEnd = 30067;
	public static final int eM2SPKTRolePickUpRareDrops = 30068;
	public static final int eM2SPKTSyncWorldBossDamageRoles = 30069;
	public static final int eM2SPKTSyncSteleRemainTimes = 30070;
	public static final int eM2SPKTSyncRoleAddSteleCard = 30071;
	public static final int eM2SPKTSyncRefreshSteleMonster = 30072;
	public static final int eM2SPKTSyncEmergencyMapStart = 30073;
	public static final int eM2SPKTSyncEmergencyMapKillMonster = 30074;
	public static final int eM2SPKTSyncEmergencyMapEnd = 30075;
	public static final int eM2SPKTSyncFightNpcMapStart = 30076;
	public static final int eM2SPKTSyncFightNpcMapEnd = 30077;
	public static final int eM2SPKTSyncTowerDefenceMapStart = 30078;
	public static final int eM2SPKTSyncTowerDefenceMapEnd = 30079;
	public static final int eM2SPKTSyncTowerDefenceSpawnCount = 30080;
	public static final int eM2SPKTSyncTowerDefenceScore = 30081;

	// server to auction
	public static final int eS2AuctionPKTKeepAlive = 60001;
	public static final int eS2AuctionPKTWhoAmI = 60002;
	public static final int eS2AuctionPKTReportTimeOffset = 60003;
	public static final int eS2AuctionPKTPutOnItemReq = 60004;
	public static final int eS2AuctionPKTTimeOutPutOffItemsRes = 60005;
	public static final int eS2AuctionPKTPutOffItemsReq = 60006;
	public static final int eS2AuctionPKTBuyItemsReq = 60007;
	public static final int eS2AuctionPKTCheckCanBuyRes = 60008;
	public static final int eS2AuctionPKTAuctionItemsSyncReq = 60009;
	public static final int eS2AuctionPKTSelfItemsSyncReq = 60010;
	public static final int eS2AuctionPKTItemPricesSyncReq = 60011;
	public static final int eS2AuctionPKTUpdateGroupBuyGoods = 60012;
	public static final int eS2AuctionPKTSyncGroupBuyGoods = 60013;

	// auction to server
	public static final int eAuction2SPKTKeepAlive = 70001;
	public static final int eAuction2SPKTAdjustTimeOffset = 70002;
	public static final int eAuction2SPKTPutOnItemRes = 70003;
	public static final int eAuction2SPKTTimeOutPutOffItemsReq = 70004;
	public static final int eAuction2SPKTPutOffItemsRes = 70005;
	public static final int eAuction2SPKTBuyItemsRes = 70006;
	public static final int eAuction2SPKTCheckCanBuyReq = 70007;
	public static final int eAuction2SPKTAuctionItemsSyncRes = 70008;
	public static final int eAuction2SPKTSelfItemsSyncRes = 70009;
	public static final int eAuction2SPKTItemPricesSyncRes = 70010;
	public static final int eAuction2SPKTSyncGroupBuyLog = 70011;

	// server to auth
	public static final int eS2AUPKTKeepAlive = 80001;
	public static final int eS2AUPKTWhoAmI = 80002;
	public static final int eS2AUPKTPayRes = 80003;

	// auth to server
	public static final int eAU2SPKTKeepAlive = 90001;
	public static final int eAU2SPKTPayReq = 90002;

	// gs to global ms
	public static final int eS2GMPKTKeepAlive = 100001;
	public static final int eS2GMPKTWhoAmI = 100002;
	public static final int eS2GMPKTReportTimeOffset = 100003;
	public static final int eS2GMPKTLuaChannel = 100004;
	public static final int eS2GMPKTStrChannel = 100005;
	public static final int eS2GMPKTEnterMap = 100030;
	public static final int eS2GMPKTLeaveMap = 100031;
	public static final int eS2GMPKTUpdateActive = 100033;
	public static final int eS2GMPKTAddHp = 100034;
	public static final int eS2GMPKTRoleUseItemSkill = 100035;
	public static final int eS2GMPKTAddPetHp = 100036;
	public static final int eS2GMPKTStartMine = 100037;
	public static final int eS2GMPKTResetLocation = 100038;
	public static final int eS2GMPKTUpdateCurSkills = 100039;
	public static final int eS2GMPKTUpdateCurSpirit = 100040;
	public static final int eS2GMPKTPickUpResult = 100041;
	public static final int eS2GMPKTUpdateRoleMarriageSkillInfo = 100042;
	public static final int eS2GMPKTUpdateRoleMarriageSkillLevel = 100043;
	public static final int eS2GMPKTRoleRevive = 100044;

	// global ms to gs
	public static final int eGM2SPKTKeepAlive = 110001;
	public static final int eGM2SPKTSyncGlobalMaps = 110002;
	public static final int eGM2SPKTLuaChannel = 110004;
	public static final int eGM2SPKTStrChannel = 110005;
	public static final int eGM2SPKTMapRoleReady = 111000;
	public static final int eGM2SPKTSyncLocation = 111001;
	public static final int eGM2SPKTSyncHp = 111002;
	public static final int eGM2SPKTAddDrops = 111003;
	public static final int eGM2SPKTAddKill = 111004;
	public static final int eGM2SPKTSyncDurability = 111005;
	public static final int eGM2SPKTSyncEndMine = 111006;
	public static final int eGM2SPKTKickRoleFromMap = 111007;
	public static final int eGM2SPKTRoleUseItemSkillSuc = 111008;
	public static final int eGM2SPKTUpdateRoleFightState = 111009;
	public static final int eGM2SPKTSyncRolePetHp = 111010;
	public static final int eGM2SPKTSyncArmorVal = 111011;
	public static final int eGM2SPKTSyncWeaponMaster = 111012;
	public static final int eGM2SPKTRolePickUpDrops = 111013;
	public static final int eGM2SPKTRolePickUpRareDrops = 111014;

	// gs to fs
	public static final int eS2FPKTKeepAlive = 120001;
	public static final int eS2FPKTWhoAmI = 120002;
	public static final int eS2FPKTReportTimeOffset = 120003;
	public static final int eS2FPKTRoleJoinForceWarReq = 120011;
	public static final int eS2FPKTRoleQuitForceWarReq = 120012;
	public static final int eS2FPKTUpdateFightRank = 120013;
	public static final int eS2FPKTSendMsgFight = 120014;
	public static final int eS2FPKTSingleJoinSuperArenaReq = 120015;
	public static final int eS2FPKTSingleQuitSuperArenaReq = 120016;
	public static final int eS2FPKTTeamJoinSuperArenaReq = 120017;
	public static final int eS2FPKTTeamQuitSuperArenaReq = 120018;
	public static final int eS2FPKTQueryTeamMembersReq = 120019;
	public static final int eS2FPKTRoleLeaveTeam = 120020;
	public static final int eS2FPKTQueryTeamMemberReq = 120022;
	public static final int eS2FPKTLeaveMap = 120023;
	public static final int eS2FPKTSendMsgGlobalTeam = 120024;
	public static final int eS2FPKTTeamJoinForceWarReq = 120025;
	public static final int eS2FPKTTeamQuitForceWarReq = 120026;
	public static final int eS2FPKTSyncRoleDemonHoleReq = 120027;
	public static final int eS2FPKTRoleJoinDemonHoleReq = 120028;
	public static final int eS2FPKTRoleChangeDemonHoleFloorReq = 120029;
	public static final int eS2FPKTRoleDemonHoleBattleReq = 120030;
	public static final int eS2FPKTRoleEnterDemonHoleFloor = 120031;

	// fs to gs
	public static final int eF2SPKTKeepAlive = 130001;
	public static final int eF2SPKTSyncGSRankStart = 130009;
	public static final int eF2SPKTSyncGSRank = 130010;
	public static final int eF2SPKTSyncGSRankEnd = 130011;
	public static final int eF2SPKTRoleJoinForceWarRes = 130012;
	public static final int eF2SPKTRoleQuitForceWarRes = 130013;
	public static final int eF2SPKTRoleEnterForceWar = 130016;
	public static final int eF2SPKTSyncForceWarMapStart = 130017;
	public static final int eF2SPKTSyncForceWarMapEnd = 130018;
	public static final int eF2SPKTSyncMapCopyTimeOut = 130019;
	public static final int eF2SPKTReceiveMsgFight = 130020;
	public static final int eF2SPKTSingleJoinSuperArenaRes = 130021;
	public static final int eF2SPKTSingleQuitSuperArenaRes = 130022;
	public static final int eF2SPKTTeamJoinSuperArenaRes = 130023;
	public static final int eF2SPKTTeamQuitSuperArenaRes = 130024;
	public static final int eF2SPKTQueryTeamMembersRes = 130025;
	public static final int eF2SPKTCreateMapCopy = 130027;
	public static final int eF2SPKTRoleEnterSuperArena = 130028;
	public static final int eF2SPKTSyncSuperArenaStart = 130029;
	public static final int eF2SPKTSyncSuperArenaMapEnd = 130030;
	public static final int eF2SPKTSuperArenaMatchResult = 130031;
	public static final int eF2SPKTSyncRoleFightTeam = 130032;
	public static final int eF2SPKTTeamLeaderChange = 130033;
	public static final int eF2SPKTMemberLeaveTeam = 130034;
	public static final int eF2SPKTTeamMemberUpdateHpTrans = 130035;
	public static final int eF2SPKTFightTeamDissolve = 130036;
	public static final int eF2SPKTQueryTeamMemberRes = 130037;
	public static final int eF2SPKTEnterSuperArenaRace = 130038;
	public static final int eF2SPKTForceWarMatchResult = 130039;
	public static final int eF2SPKTTeamJoinForceWarRes = 130040;
	public static final int eF2SPKTTeamQuitForceWarRes = 130041;
	public static final int eF2SPKTSyncRoleDemonHoleRes = 130042;
	public static final int eF2SPKTRoleJoinDemonHoleRes = 130043;
	public static final int eF2SPKTRoleChangeDemonHoleFloorRes = 130044;
	public static final int eF2SPKTRoleDemonHoleBattleRes = 130045;
	public static final int eF2SPKTRoleEnterDemonHoleMap = 130046;
	public static final int eF2SPKTSyncDemonHoleMapEnd = 130047;
	public static final int eF2SPKTSyncGSCreateNewTeam = 130048;
	public static final int eF2SPKTSyncRoleChatRoom = 130049;

	// fs to global ms
	public static final int eF2GMPKTKeepAlive = 140001;
	public static final int eF2GMPKTCreateMapCopyReq = 140011;
	public static final int eF2GMPKTEndMapCopy = 140012;

	// global ms to fs
	public static final int eGM2FPKTKeepAlive = 150001;
	public static final int eGM2FPKTWhoAmI = 150002;
	public static final int eGM2FPKTCreateMapCopyRes = 150011;
	public static final int eGM2FPKTSyncForceWarMapEnd = 150012;
	public static final int eGM2FPKTSyncSuperArenaMapEnd = 150013;
	public static final int eGM2FPKTSyncHp = 150014;
	public static final int eGM2FPKTSyncSuperArenaRaceEnd = 150015;
	public static final int eGM2FPKTSyncDemonHoleKill = 150016;

	// es to gs
	public static final int eE2SPKTKeepAlive = 160001;
	public static final int eE2SPKTReceiveMsg = 160003;
	public static final int eE2SPKTSocialMsgRes = 160004;

	// gs ms to es
	public static final int eS2EPKTKeepAlive = 170001;
	public static final int eS2EPKTWhoAmI = 170002;
	public static final int eS2EPKTSendMsg = 170003;
	public static final int eS2EPKTSocialMsgReq = 170004;

	// es to ss
	public static final int eSS2EPKTKeepAlive = 180001;
	public static final int eSS2EPKTForwardRes = 180002;

	// gs ms to ss
	public static final int eE2SSPKTKeepAlive = 190001;
	public static final int eE2SSPKTWhoAmI = 190002;
	public static final int eE2SSPKTForwardReq = 190003;

	// server to client
	public static class S2C
	{

		public static class ServerChallenge extends SimplePacket
		{
			public ServerChallenge() { }

			public ServerChallenge(int istate, String sstate, int flag, List<Byte> key)
			{
				this.istate = istate;
				this.sstate = sstate;
				this.flag = flag;
				this.key = key;
			}

			@Override
			public int getType()
			{
				return Packet.eS2CPKTServerChallenge;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				istate = is.popInteger();
				sstate = is.popString();
				flag = is.popInteger();
				key = is.popByteList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(istate);
				os.pushString(sstate);
				os.pushInteger(flag);
				os.pushByteList(key);
			}

			public int getIstate()
			{
				return istate;
			}

			public void setIstate(int istate)
			{
				this.istate = istate;
			}

			public String getSstate()
			{
				return sstate;
			}

			public void setSstate(String sstate)
			{
				this.sstate = sstate;
			}

			public int getFlag()
			{
				return flag;
			}

			public void setFlag(int flag)
			{
				this.flag = flag;
			}

			public List<Byte> getKey()
			{
				return key;
			}

			public void setKey(List<Byte> key)
			{
				this.key = key;
			}

			private int istate;
			private String sstate;
			private int flag;
			private List<Byte> key;
		}

		public static class ServerResponse extends SimplePacket
		{
			public ServerResponse() { }

			public ServerResponse(int res)
			{
				this.res = res;
			}

			@Override
			public int getType()
			{
				return Packet.eS2CPKTServerResponse;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				res = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(res);
			}

			public int getRes()
			{
				return res;
			}

			public void setRes(int res)
			{
				this.res = res;
			}

			private int res;
		}

		public static class LuaChannel extends SimplePacket
		{
			public LuaChannel() { }

			public LuaChannel(String data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2CPKTLuaChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushString(data);
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private String data;
		}

		public static class StrChannel extends SimplePacket
		{
			public StrChannel() { }

			public StrChannel(String data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2CPKTStrChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushString(data);
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private String data;
		}

		public static class LuaChannel2 extends SimplePacket
		{
			public LuaChannel2() { }

			public LuaChannel2(List<String> data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2CPKTLuaChannel2;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				data = is.popStringList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushStringList(data);
			}

			public List<String> getData()
			{
				return data;
			}

			public void setData(List<String> data)
			{
				this.data = data;
			}

			private List<String> data;
		}

	}

	// client to server
	public static class C2S
	{

		public static class ClientResponse extends SimplePacket
		{
			public ClientResponse() { }

			public ClientResponse(List<Byte> key)
			{
				this.key = key;
			}

			@Override
			public int getType()
			{
				return Packet.eC2SPKTClientResponse;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				key = is.popByteList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushByteList(key);
			}

			public List<Byte> getKey()
			{
				return key;
			}

			public void setKey(List<Byte> key)
			{
				this.key = key;
			}

			private List<Byte> key;
		}

		public static class LuaChannel extends SimplePacket
		{
			public LuaChannel() { }

			public LuaChannel(String data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eC2SPKTLuaChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushString(data);
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private String data;
		}

		public static class LuaChannel2 extends SimplePacket
		{
			public LuaChannel2() { }

			public LuaChannel2(List<String> data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eC2SPKTLuaChannel2;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				data = is.popStringList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushStringList(data);
			}

			public List<String> getData()
			{
				return data;
			}

			public void setData(List<String> data)
			{
				this.data = data;
			}

			private List<String> data;
		}

		public static class StrChannel extends SimplePacket
		{
			public StrChannel() { }

			public StrChannel(String data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eC2SPKTStrChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushString(data);
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private String data;
		}

	}

	// server to map
	public static class S2M
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class SyncTimeOffset extends SimplePacket
		{
			public SyncTimeOffset() { }

			public SyncTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncTimeOffset;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				timeOffset = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(timeOffset);
			}

			public int getTimeOffset()
			{
				return timeOffset;
			}

			public void setTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			private int timeOffset;
		}

		public static class LuaChannel extends SimplePacket
		{
			public LuaChannel() { }

			public LuaChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTLuaChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class StrChannel extends SimplePacket
		{
			public StrChannel() { }

			public StrChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTStrChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class SyncDoubleDropCfg extends SimplePacket
		{
			public SyncDoubleDropCfg() { }

			public SyncDoubleDropCfg(List<SBean.DoubleDropCfg> cfgs)
			{
				this.cfgs = cfgs;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncDoubleDropCfg;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				cfgs = is.popList(SBean.DoubleDropCfg.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushList(cfgs);
			}

			public List<SBean.DoubleDropCfg> getCfgs()
			{
				return cfgs;
			}

			public void setCfgs(List<SBean.DoubleDropCfg> cfgs)
			{
				this.cfgs = cfgs;
			}

			private List<SBean.DoubleDropCfg> cfgs;
		}

		public static class SyncExtraDropCfg extends SimplePacket
		{
			public SyncExtraDropCfg() { }

			public SyncExtraDropCfg(List<SBean.ExtraDropCfg> cfgs)
			{
				this.cfgs = cfgs;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncExtraDropCfg;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				cfgs = is.popList(SBean.ExtraDropCfg.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushList(cfgs);
			}

			public List<SBean.ExtraDropCfg> getCfgs()
			{
				return cfgs;
			}

			public void setCfgs(List<SBean.ExtraDropCfg> cfgs)
			{
				this.cfgs = cfgs;
			}

			private List<SBean.ExtraDropCfg> cfgs;
		}

		public static class SyncWorldNum extends SimplePacket
		{
			public SyncWorldNum() { }

			public SyncWorldNum(int worldNum, Map<Integer, Integer> extraWorldNum)
			{
				this.worldNum = worldNum;
				this.extraWorldNum = extraWorldNum;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncWorldNum;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				worldNum = is.popInteger();
				extraWorldNum = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(worldNum);
				os.pushIntegerIntegerMap(extraWorldNum);
			}

			public int getWorldNum()
			{
				return worldNum;
			}

			public void setWorldNum(int worldNum)
			{
				this.worldNum = worldNum;
			}

			public Map<Integer, Integer> getExtraWorldNum()
			{
				return extraWorldNum;
			}

			public void setExtraWorldNum(Map<Integer, Integer> extraWorldNum)
			{
				this.extraWorldNum = extraWorldNum;
			}

			private int worldNum;
			private Map<Integer, Integer> extraWorldNum;
		}

		public static class StartMapCopy extends SimplePacket
		{
			public StartMapCopy() { }

			public StartMapCopy(int mapID, int instanceID)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTStartMapCopy;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			private int mapID;
			private int instanceID;
		}

		public static class EndMapCopy extends SimplePacket
		{
			public EndMapCopy() { }

			public EndMapCopy(int mapID, int instanceID)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTEndMapCopy;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			private int mapID;
			private int instanceID;
		}

		public static class MapCopyReady extends SimplePacket
		{
			public MapCopyReady() { }

			public MapCopyReady(int mapID, int instanceID)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTMapCopyReady;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			private int mapID;
			private int instanceID;
		}

		public static class ResetSectMap extends SimplePacket
		{
			public ResetSectMap() { }

			public ResetSectMap(int mapID, int instanceID, Map<Integer, Integer> progress)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
				this.progress = progress;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTResetSectMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
				progress = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
				os.pushIntegerIntegerMap(progress);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			public Map<Integer, Integer> getProgress()
			{
				return progress;
			}

			public void setProgress(Map<Integer, Integer> progress)
			{
				this.progress = progress;
			}

			private int mapID;
			private int instanceID;
			private Map<Integer, Integer> progress;
		}

		public static class ResetArenaMap extends SimplePacket
		{
			public ResetArenaMap() { }

			public ResetArenaMap(int mapID, int instanceID, SBean.BattleArray enemy)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
				this.enemy = enemy;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTResetArenaMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
				if( enemy == null )
					enemy = new SBean.BattleArray();
				is.pop(enemy);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
				os.push(enemy);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			public SBean.BattleArray getEnemy()
			{
				return enemy;
			}

			public void setEnemy(SBean.BattleArray enemy)
			{
				this.enemy = enemy;
			}

			private int mapID;
			private int instanceID;
			private SBean.BattleArray enemy;
		}

		public static class ResetBWArenaMap extends SimplePacket
		{
			public ResetBWArenaMap() { }

			public ResetBWArenaMap(int mapID, int instanceID, SBean.BattleArray enemy, byte petLack)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
				this.enemy = enemy;
				this.petLack = petLack;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTResetBWArenaMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
				if( enemy == null )
					enemy = new SBean.BattleArray();
				is.pop(enemy);
				petLack = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
				os.push(enemy);
				os.pushByte(petLack);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			public SBean.BattleArray getEnemy()
			{
				return enemy;
			}

			public void setEnemy(SBean.BattleArray enemy)
			{
				this.enemy = enemy;
			}

			public byte getPetLack()
			{
				return petLack;
			}

			public void setPetLack(byte petLack)
			{
				this.petLack = petLack;
			}

			private int mapID;
			private int instanceID;
			private SBean.BattleArray enemy;
			private byte petLack;
		}

		public static class EnterMap extends SimplePacket
		{
			public EnterMap() { }

			public EnterMap(SBean.FightRole role, int mapId, int mapInstance, SBean.Location location, 
			                int hp, int sp, int armorVal, Map<Integer, SBean.DBBuff> buffs, 
			                Map<Integer, SBean.FightPet> pets, List<Integer> petSeq, SBean.PetHost pethost, SBean.PKInfo pkInfo, 
			                SBean.Team team, int curRideHorse, SBean.MulRoleInfo mulRoleInfo, SBean.DBAlterState alterState, 
			                byte mainSpawnPos, int dayFailedStreak, int vipLevel, int curWizardPet, 
			                byte canTakeDrop)
			{
				this.role = role;
				this.mapId = mapId;
				this.mapInstance = mapInstance;
				this.location = location;
				this.hp = hp;
				this.sp = sp;
				this.armorVal = armorVal;
				this.buffs = buffs;
				this.pets = pets;
				this.petSeq = petSeq;
				this.pethost = pethost;
				this.pkInfo = pkInfo;
				this.team = team;
				this.curRideHorse = curRideHorse;
				this.mulRoleInfo = mulRoleInfo;
				this.alterState = alterState;
				this.mainSpawnPos = mainSpawnPos;
				this.dayFailedStreak = dayFailedStreak;
				this.vipLevel = vipLevel;
				this.curWizardPet = curWizardPet;
				this.canTakeDrop = canTakeDrop;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTEnterMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( role == null )
					role = new SBean.FightRole();
				is.pop(role);
				mapId = is.popInteger();
				mapInstance = is.popInteger();
				if( location == null )
					location = new SBean.Location();
				is.pop(location);
				hp = is.popInteger();
				sp = is.popInteger();
				armorVal = is.popInteger();
				buffs = is.popIntegerTreeMap(SBean.DBBuff.class);
				pets = is.popIntegerTreeMap(SBean.FightPet.class);
				petSeq = is.popIntegerList();
				pethost = is.popNullable(SBean.PetHost.class);
				pkInfo = is.popNullable(SBean.PKInfo.class);
				if( team == null )
					team = new SBean.Team();
				is.pop(team);
				curRideHorse = is.popInteger();
				if( mulRoleInfo == null )
					mulRoleInfo = new SBean.MulRoleInfo();
				is.pop(mulRoleInfo);
				if( alterState == null )
					alterState = new SBean.DBAlterState();
				is.pop(alterState);
				mainSpawnPos = is.popByte();
				dayFailedStreak = is.popInteger();
				vipLevel = is.popInteger();
				curWizardPet = is.popInteger();
				canTakeDrop = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(role);
				os.pushInteger(mapId);
				os.pushInteger(mapInstance);
				os.push(location);
				os.pushInteger(hp);
				os.pushInteger(sp);
				os.pushInteger(armorVal);
				os.pushIntegerMap(buffs);
				os.pushIntegerMap(pets);
				os.pushIntegerList(petSeq);
				os.pushNullable(pethost);
				os.pushNullable(pkInfo);
				os.push(team);
				os.pushInteger(curRideHorse);
				os.push(mulRoleInfo);
				os.push(alterState);
				os.pushByte(mainSpawnPos);
				os.pushInteger(dayFailedStreak);
				os.pushInteger(vipLevel);
				os.pushInteger(curWizardPet);
				os.pushByte(canTakeDrop);
			}

			public SBean.FightRole getRole()
			{
				return role;
			}

			public void setRole(SBean.FightRole role)
			{
				this.role = role;
			}

			public int getMapId()
			{
				return mapId;
			}

			public void setMapId(int mapId)
			{
				this.mapId = mapId;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.Location getLocation()
			{
				return location;
			}

			public void setLocation(SBean.Location location)
			{
				this.location = location;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			public int getSp()
			{
				return sp;
			}

			public void setSp(int sp)
			{
				this.sp = sp;
			}

			public int getArmorVal()
			{
				return armorVal;
			}

			public void setArmorVal(int armorVal)
			{
				this.armorVal = armorVal;
			}

			public Map<Integer, SBean.DBBuff> getBuffs()
			{
				return buffs;
			}

			public void setBuffs(Map<Integer, SBean.DBBuff> buffs)
			{
				this.buffs = buffs;
			}

			public Map<Integer, SBean.FightPet> getPets()
			{
				return pets;
			}

			public void setPets(Map<Integer, SBean.FightPet> pets)
			{
				this.pets = pets;
			}

			public List<Integer> getPetSeq()
			{
				return petSeq;
			}

			public void setPetSeq(List<Integer> petSeq)
			{
				this.petSeq = petSeq;
			}

			public SBean.PetHost getPethost()
			{
				return pethost;
			}

			public void setPethost(SBean.PetHost pethost)
			{
				this.pethost = pethost;
			}

			public SBean.PKInfo getPkInfo()
			{
				return pkInfo;
			}

			public void setPkInfo(SBean.PKInfo pkInfo)
			{
				this.pkInfo = pkInfo;
			}

			public SBean.Team getTeam()
			{
				return team;
			}

			public void setTeam(SBean.Team team)
			{
				this.team = team;
			}

			public int getCurRideHorse()
			{
				return curRideHorse;
			}

			public void setCurRideHorse(int curRideHorse)
			{
				this.curRideHorse = curRideHorse;
			}

			public SBean.MulRoleInfo getMulRoleInfo()
			{
				return mulRoleInfo;
			}

			public void setMulRoleInfo(SBean.MulRoleInfo mulRoleInfo)
			{
				this.mulRoleInfo = mulRoleInfo;
			}

			public SBean.DBAlterState getAlterState()
			{
				return alterState;
			}

			public void setAlterState(SBean.DBAlterState alterState)
			{
				this.alterState = alterState;
			}

			public byte getMainSpawnPos()
			{
				return mainSpawnPos;
			}

			public void setMainSpawnPos(byte mainSpawnPos)
			{
				this.mainSpawnPos = mainSpawnPos;
			}

			public int getDayFailedStreak()
			{
				return dayFailedStreak;
			}

			public void setDayFailedStreak(int dayFailedStreak)
			{
				this.dayFailedStreak = dayFailedStreak;
			}

			public int getVipLevel()
			{
				return vipLevel;
			}

			public void setVipLevel(int vipLevel)
			{
				this.vipLevel = vipLevel;
			}

			public int getCurWizardPet()
			{
				return curWizardPet;
			}

			public void setCurWizardPet(int curWizardPet)
			{
				this.curWizardPet = curWizardPet;
			}

			public byte getCanTakeDrop()
			{
				return canTakeDrop;
			}

			public void setCanTakeDrop(byte canTakeDrop)
			{
				this.canTakeDrop = canTakeDrop;
			}

			private SBean.FightRole role;
			private int mapId;
			private int mapInstance;
			private SBean.Location location;
			private int hp;
			private int sp;
			private int armorVal;
			private Map<Integer, SBean.DBBuff> buffs;
			private Map<Integer, SBean.FightPet> pets;
			private List<Integer> petSeq;
			private SBean.PetHost pethost;
			private SBean.PKInfo pkInfo;
			private SBean.Team team;
			private int curRideHorse;
			private SBean.MulRoleInfo mulRoleInfo;
			private SBean.DBAlterState alterState;
			private byte mainSpawnPos;
			private int dayFailedStreak;
			private int vipLevel;
			private int curWizardPet;
			private byte canTakeDrop;
		}

		public static class LeaveMap extends SimplePacket
		{
			public LeaveMap() { }

			public LeaveMap(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTLeaveMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class ResetLocation extends SimplePacket
		{
			public ResetLocation() { }

			public ResetLocation(int roleID, SBean.Location location)
			{
				this.roleID = roleID;
				this.location = location;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTResetLocation;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( location == null )
					location = new SBean.Location();
				is.pop(location);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(location);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.Location getLocation()
			{
				return location;
			}

			public void setLocation(SBean.Location location)
			{
				this.location = location;
			}

			private int roleID;
			private SBean.Location location;
		}

		public static class UpdateActive extends SimplePacket
		{
			public UpdateActive() { }

			public UpdateActive(int roleID, byte active)
			{
				this.roleID = roleID;
				this.active = active;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateActive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				active = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushByte(active);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getActive()
			{
				return active;
			}

			public void setActive(byte active)
			{
				this.active = active;
			}

			private int roleID;
			private byte active;
		}

		public static class UpdateEquip extends SimplePacket
		{
			public UpdateEquip() { }

			public UpdateEquip(int roleID, int wid, SBean.DBEquip equip)
			{
				this.roleID = roleID;
				this.wid = wid;
				this.equip = equip;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateEquip;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				wid = is.popInteger();
				equip = is.popNullable(SBean.DBEquip.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(wid);
				os.pushNullable(equip);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getWid()
			{
				return wid;
			}

			public void setWid(int wid)
			{
				this.wid = wid;
			}

			public SBean.DBEquip getEquip()
			{
				return equip;
			}

			public void setEquip(SBean.DBEquip equip)
			{
				this.equip = equip;
			}

			private int roleID;
			private int wid;
			private SBean.DBEquip equip;
		}

		public static class UpdateEquipPart extends SimplePacket
		{
			public UpdateEquipPart() { }

			public UpdateEquipPart(int roleID, SBean.DBEquipPart equipPart)
			{
				this.roleID = roleID;
				this.equipPart = equipPart;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateEquipPart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( equipPart == null )
					equipPart = new SBean.DBEquipPart();
				is.pop(equipPart);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(equipPart);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBEquipPart getEquipPart()
			{
				return equipPart;
			}

			public void setEquipPart(SBean.DBEquipPart equipPart)
			{
				this.equipPart = equipPart;
			}

			private int roleID;
			private SBean.DBEquipPart equipPart;
		}

		public static class UpdateSkill extends SimplePacket
		{
			public UpdateSkill() { }

			public UpdateSkill(int roleID, SBean.DBSkill skill)
			{
				this.roleID = roleID;
				this.skill = skill;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( skill == null )
					skill = new SBean.DBSkill();
				is.pop(skill);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(skill);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBSkill getSkill()
			{
				return skill;
			}

			public void setSkill(SBean.DBSkill skill)
			{
				this.skill = skill;
			}

			private int roleID;
			private SBean.DBSkill skill;
		}

		public static class UpdateCurSkills extends SimplePacket
		{
			public UpdateCurSkills() { }

			public UpdateCurSkills(int roleID, List<Integer> skills)
			{
				this.roleID = roleID;
				this.skills = skills;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateCurSkills;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				skills = is.popIntegerList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerList(skills);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public List<Integer> getSkills()
			{
				return skills;
			}

			public void setSkills(List<Integer> skills)
			{
				this.skills = skills;
			}

			private int roleID;
			private List<Integer> skills;
		}

		public static class UpdateBuff extends SimplePacket
		{
			public UpdateBuff() { }

			public UpdateBuff(int roleID, SBean.DBBuff buff)
			{
				this.roleID = roleID;
				this.buff = buff;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateBuff;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( buff == null )
					buff = new SBean.DBBuff();
				is.pop(buff);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(buff);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBBuff getBuff()
			{
				return buff;
			}

			public void setBuff(SBean.DBBuff buff)
			{
				this.buff = buff;
			}

			private int roleID;
			private SBean.DBBuff buff;
		}

		public static class UpdateLevel extends SimplePacket
		{
			public UpdateLevel() { }

			public UpdateLevel(int roleID, int level)
			{
				this.roleID = roleID;
				this.level = level;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateLevel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				level = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(level);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getLevel()
			{
				return level;
			}

			public void setLevel(int level)
			{
				this.level = level;
			}

			private int roleID;
			private int level;
		}

		public static class AddHp extends SimplePacket
		{
			public AddHp() { }

			public AddHp(int roleID, int hp)
			{
				this.roleID = roleID;
				this.hp = hp;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTAddHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				hp = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(hp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			private int roleID;
			private int hp;
		}

		public static class UpdateWeapon extends SimplePacket
		{
			public UpdateWeapon() { }

			public UpdateWeapon(int roleID, SBean.DBWeapon weapon)
			{
				this.roleID = roleID;
				this.weapon = weapon;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateWeapon;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( weapon == null )
					weapon = new SBean.DBWeapon();
				is.pop(weapon);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(weapon);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBWeapon getWeapon()
			{
				return weapon;
			}

			public void setWeapon(SBean.DBWeapon weapon)
			{
				this.weapon = weapon;
			}

			private int roleID;
			private SBean.DBWeapon weapon;
		}

		public static class UpdateCurWeapon extends SimplePacket
		{
			public UpdateCurWeapon() { }

			public UpdateCurWeapon(int roleID, int curWeapon)
			{
				this.roleID = roleID;
				this.curWeapon = curWeapon;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateCurWeapon;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				curWeapon = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(curWeapon);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getCurWeapon()
			{
				return curWeapon;
			}

			public void setCurWeapon(int curWeapon)
			{
				this.curWeapon = curWeapon;
			}

			private int roleID;
			private int curWeapon;
		}

		public static class UpdateSpirit extends SimplePacket
		{
			public UpdateSpirit() { }

			public UpdateSpirit(int roleID, SBean.DBSpirit spirit)
			{
				this.roleID = roleID;
				this.spirit = spirit;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateSpirit;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( spirit == null )
					spirit = new SBean.DBSpirit();
				is.pop(spirit);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(spirit);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBSpirit getSpirit()
			{
				return spirit;
			}

			public void setSpirit(SBean.DBSpirit spirit)
			{
				this.spirit = spirit;
			}

			private int roleID;
			private SBean.DBSpirit spirit;
		}

		public static class UpdateCurSpirit extends SimplePacket
		{
			public UpdateCurSpirit() { }

			public UpdateCurSpirit(int roleID, Set<Integer> curSpirit)
			{
				this.roleID = roleID;
				this.curSpirit = curSpirit;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateCurSpirit;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				curSpirit = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerSet(curSpirit);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Set<Integer> getCurSpirit()
			{
				return curSpirit;
			}

			public void setCurSpirit(Set<Integer> curSpirit)
			{
				this.curSpirit = curSpirit;
			}

			private int roleID;
			private Set<Integer> curSpirit;
		}

		public static class StartMine extends SimplePacket
		{
			public StartMine() { }

			public StartMine(int roleID, int mineID, int mineInstance)
			{
				this.roleID = roleID;
				this.mineID = mineID;
				this.mineInstance = mineInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTStartMine;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mineID = is.popInteger();
				mineInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mineID);
				os.pushInteger(mineInstance);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMineID()
			{
				return mineID;
			}

			public void setMineID(int mineID)
			{
				this.mineID = mineID;
			}

			public int getMineInstance()
			{
				return mineInstance;
			}

			public void setMineInstance(int mineInstance)
			{
				this.mineInstance = mineInstance;
			}

			private int roleID;
			private int mineID;
			private int mineInstance;
		}

		public static class RoleRevive extends SimplePacket
		{
			public RoleRevive() { }

			public RoleRevive(int roleID, byte fullHp)
			{
				this.roleID = roleID;
				this.fullHp = fullHp;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTRoleRevive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				fullHp = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushByte(fullHp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getFullHp()
			{
				return fullHp;
			}

			public void setFullHp(byte fullHp)
			{
				this.fullHp = fullHp;
			}

			private int roleID;
			private byte fullHp;
		}

		public static class UpdatePet extends SimplePacket
		{
			public UpdatePet() { }

			public UpdatePet(int roleID, SBean.FightPet pet)
			{
				this.roleID = roleID;
				this.pet = pet;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdatePet;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( pet == null )
					pet = new SBean.FightPet();
				is.pop(pet);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(pet);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.FightPet getPet()
			{
				return pet;
			}

			public void setPet(SBean.FightPet pet)
			{
				this.pet = pet;
			}

			private int roleID;
			private SBean.FightPet pet;
		}

		public static class UpdateTeam extends SimplePacket
		{
			public UpdateTeam() { }

			public UpdateTeam(int roleID, SBean.Team team)
			{
				this.roleID = roleID;
				this.team = team;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateTeam;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( team == null )
					team = new SBean.Team();
				is.pop(team);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(team);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.Team getTeam()
			{
				return team;
			}

			public void setTeam(SBean.Team team)
			{
				this.team = team;
			}

			private int roleID;
			private SBean.Team team;
		}

		public static class ChangeCurPets extends SimplePacket
		{
			public ChangeCurPets() { }

			public ChangeCurPets(int roleID, Map<Integer, SBean.FightPet> pets)
			{
				this.roleID = roleID;
				this.pets = pets;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTChangeCurPets;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				pets = is.popIntegerTreeMap(SBean.FightPet.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerMap(pets);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, SBean.FightPet> getPets()
			{
				return pets;
			}

			public void setPets(Map<Integer, SBean.FightPet> pets)
			{
				this.pets = pets;
			}

			private int roleID;
			private Map<Integer, SBean.FightPet> pets;
		}

		public static class UpdateSectAura extends SimplePacket
		{
			public UpdateSectAura() { }

			public UpdateSectAura(int roleID, int auraID, int auraLvl)
			{
				this.roleID = roleID;
				this.auraID = auraID;
				this.auraLvl = auraLvl;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateSectAura;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				auraID = is.popInteger();
				auraLvl = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(auraID);
				os.pushInteger(auraLvl);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getAuraID()
			{
				return auraID;
			}

			public void setAuraID(int auraID)
			{
				this.auraID = auraID;
			}

			public int getAuraLvl()
			{
				return auraLvl;
			}

			public void setAuraLvl(int auraLvl)
			{
				this.auraLvl = auraLvl;
			}

			private int roleID;
			private int auraID;
			private int auraLvl;
		}

		public static class ResetSectAuras extends SimplePacket
		{
			public ResetSectAuras() { }

			public ResetSectAuras(int roleID, Map<Integer, Integer> auras)
			{
				this.roleID = roleID;
				this.auras = auras;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTResetSectAuras;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				auras = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(auras);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getAuras()
			{
				return auras;
			}

			public void setAuras(Map<Integer, Integer> auras)
			{
				this.auras = auras;
			}

			private int roleID;
			private Map<Integer, Integer> auras;
		}

		public static class UpdatePKInfo extends SimplePacket
		{
			public UpdatePKInfo() { }

			public UpdatePKInfo(int roleID, int PKMode, int PKValue)
			{
				this.roleID = roleID;
				this.PKMode = PKMode;
				this.PKValue = PKValue;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdatePKInfo;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				PKMode = is.popInteger();
				PKValue = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(PKMode);
				os.pushInteger(PKValue);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPKMode()
			{
				return PKMode;
			}

			public void setPKMode(int PKMode)
			{
				this.PKMode = PKMode;
			}

			public int getPKValue()
			{
				return PKValue;
			}

			public void setPKValue(int PKValue)
			{
				this.PKValue = PKValue;
			}

			private int roleID;
			private int PKMode;
			private int PKValue;
		}

		public static class UpdateCurDIYSkill extends SimplePacket
		{
			public UpdateCurDIYSkill() { }

			public UpdateCurDIYSkill(int roleID, SBean.DBDIYSkillData curDIYSkill)
			{
				this.roleID = roleID;
				this.curDIYSkill = curDIYSkill;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateCurDIYSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( curDIYSkill == null )
					curDIYSkill = new SBean.DBDIYSkillData();
				is.pop(curDIYSkill);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(curDIYSkill);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBDIYSkillData getCurDIYSkill()
			{
				return curDIYSkill;
			}

			public void setCurDIYSkill(SBean.DBDIYSkillData curDIYSkill)
			{
				this.curDIYSkill = curDIYSkill;
			}

			private int roleID;
			private SBean.DBDIYSkillData curDIYSkill;
		}

		public static class UpdateTransformInfo extends SimplePacket
		{
			public UpdateTransformInfo() { }

			public UpdateTransformInfo(int roleID, byte transformLevel, byte BWType)
			{
				this.roleID = roleID;
				this.transformLevel = transformLevel;
				this.BWType = BWType;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateTransformInfo;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				transformLevel = is.popByte();
				BWType = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushByte(transformLevel);
				os.pushByte(BWType);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getTransformLevel()
			{
				return transformLevel;
			}

			public void setTransformLevel(byte transformLevel)
			{
				this.transformLevel = transformLevel;
			}

			public byte getBWType()
			{
				return BWType;
			}

			public void setBWType(byte BWType)
			{
				this.BWType = BWType;
			}

			private int roleID;
			private byte transformLevel;
			private byte BWType;
		}

		public static class CreateWorldMapBoss extends SimplePacket
		{
			public CreateWorldMapBoss() { }

			public CreateWorldMapBoss(int mapID, int mapInstanceID, int bossID, int seq, 
			                          int curHP)
			{
				this.mapID = mapID;
				this.mapInstanceID = mapInstanceID;
				this.bossID = bossID;
				this.seq = seq;
				this.curHP = curHP;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCreateWorldMapBoss;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstanceID = is.popInteger();
				bossID = is.popInteger();
				seq = is.popInteger();
				curHP = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstanceID);
				os.pushInteger(bossID);
				os.pushInteger(seq);
				os.pushInteger(curHP);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstanceID()
			{
				return mapInstanceID;
			}

			public void setMapInstanceID(int mapInstanceID)
			{
				this.mapInstanceID = mapInstanceID;
			}

			public int getBossID()
			{
				return bossID;
			}

			public void setBossID(int bossID)
			{
				this.bossID = bossID;
			}

			public int getSeq()
			{
				return seq;
			}

			public void setSeq(int seq)
			{
				this.seq = seq;
			}

			public int getCurHP()
			{
				return curHP;
			}

			public void setCurHP(int curHP)
			{
				this.curHP = curHP;
			}

			private int mapID;
			private int mapInstanceID;
			private int bossID;
			private int seq;
			private int curHP;
		}

		public static class DestroyWorldMapBoss extends SimplePacket
		{
			public DestroyWorldMapBoss() { }

			public DestroyWorldMapBoss(int mapID, int mapInstanceID, int bossID)
			{
				this.mapID = mapID;
				this.mapInstanceID = mapInstanceID;
				this.bossID = bossID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTDestroyWorldMapBoss;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstanceID = is.popInteger();
				bossID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstanceID);
				os.pushInteger(bossID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstanceID()
			{
				return mapInstanceID;
			}

			public void setMapInstanceID(int mapInstanceID)
			{
				this.mapInstanceID = mapInstanceID;
			}

			public int getBossID()
			{
				return bossID;
			}

			public void setBossID(int bossID)
			{
				this.bossID = bossID;
			}

			private int mapID;
			private int mapInstanceID;
			private int bossID;
		}

		public static class InitWorldBoss extends SimplePacket
		{
			public InitWorldBoss() { }

			public InitWorldBoss(SBean.DBBoss dbBoss)
			{
				this.dbBoss = dbBoss;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTInitWorldBoss;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( dbBoss == null )
					dbBoss = new SBean.DBBoss();
				is.pop(dbBoss);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(dbBoss);
			}

			public SBean.DBBoss getDbBoss()
			{
				return dbBoss;
			}

			public void setDbBoss(SBean.DBBoss dbBoss)
			{
				this.dbBoss = dbBoss;
			}

			private SBean.DBBoss dbBoss;
		}

		public static class CreateWorldMapSuperMonster extends SimplePacket
		{
			public CreateWorldMapSuperMonster() { }

			public CreateWorldMapSuperMonster(int mapID, int superMonsterID, int standTime)
			{
				this.mapID = mapID;
				this.superMonsterID = superMonsterID;
				this.standTime = standTime;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCreateWorldMapSuperMonster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				superMonsterID = is.popInteger();
				standTime = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(superMonsterID);
				os.pushInteger(standTime);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getSuperMonsterID()
			{
				return superMonsterID;
			}

			public void setSuperMonsterID(int superMonsterID)
			{
				this.superMonsterID = superMonsterID;
			}

			public int getStandTime()
			{
				return standTime;
			}

			public void setStandTime(int standTime)
			{
				this.standTime = standTime;
			}

			private int mapID;
			private int superMonsterID;
			private int standTime;
		}

		public static class CreateWorldMapMineral extends SimplePacket
		{
			public CreateWorldMapMineral() { }

			public CreateWorldMapMineral(int mapID, int worldMineral, int standTime)
			{
				this.mapID = mapID;
				this.worldMineral = worldMineral;
				this.standTime = standTime;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCreateWorldMapMineral;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				worldMineral = is.popInteger();
				standTime = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(worldMineral);
				os.pushInteger(standTime);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getWorldMineral()
			{
				return worldMineral;
			}

			public void setWorldMineral(int worldMineral)
			{
				this.worldMineral = worldMineral;
			}

			public int getStandTime()
			{
				return standTime;
			}

			public void setStandTime(int standTime)
			{
				this.standTime = standTime;
			}

			private int mapID;
			private int worldMineral;
			private int standTime;
		}

		public static class GainNewSuite extends SimplePacket
		{
			public GainNewSuite() { }

			public GainNewSuite(int roleID, int suiteID)
			{
				this.roleID = roleID;
				this.suiteID = suiteID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTGainNewSuite;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				suiteID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(suiteID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getSuiteID()
			{
				return suiteID;
			}

			public void setSuiteID(int suiteID)
			{
				this.suiteID = suiteID;
			}

			private int roleID;
			private int suiteID;
		}

		public static class UpdateSectBrief extends SimplePacket
		{
			public UpdateSectBrief() { }

			public UpdateSectBrief(int roleID, SBean.SectBrief sectBrief)
			{
				this.roleID = roleID;
				this.sectBrief = sectBrief;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateSectBrief;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( sectBrief == null )
					sectBrief = new SBean.SectBrief();
				is.pop(sectBrief);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(sectBrief);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.SectBrief getSectBrief()
			{
				return sectBrief;
			}

			public void setSectBrief(SBean.SectBrief sectBrief)
			{
				this.sectBrief = sectBrief;
			}

			private int roleID;
			private SBean.SectBrief sectBrief;
		}

		public static class UpdateHorseInfo extends SimplePacket
		{
			public UpdateHorseInfo() { }

			public UpdateHorseInfo(int roleID, SBean.HorseInfo info)
			{
				this.roleID = roleID;
				this.info = info;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateHorseInfo;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( info == null )
					info = new SBean.HorseInfo();
				is.pop(info);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(info);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.HorseInfo getInfo()
			{
				return info;
			}

			public void setInfo(SBean.HorseInfo info)
			{
				this.info = info;
			}

			private int roleID;
			private SBean.HorseInfo info;
		}

		public static class UpdateCurUseHorse extends SimplePacket
		{
			public UpdateCurUseHorse() { }

			public UpdateCurUseHorse(int roleID, int hid)
			{
				this.roleID = roleID;
				this.hid = hid;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateCurUseHorse;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				hid = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(hid);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getHid()
			{
				return hid;
			}

			public void setHid(int hid)
			{
				this.hid = hid;
			}

			private int roleID;
			private int hid;
		}

		public static class UpdateMedal extends SimplePacket
		{
			public UpdateMedal() { }

			public UpdateMedal(int roleID, int medal, byte state)
			{
				this.roleID = roleID;
				this.medal = medal;
				this.state = state;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateMedal;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				medal = is.popInteger();
				state = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(medal);
				os.pushByte(state);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMedal()
			{
				return medal;
			}

			public void setMedal(int medal)
			{
				this.medal = medal;
			}

			public byte getState()
			{
				return state;
			}

			public void setState(byte state)
			{
				this.state = state;
			}

			private int roleID;
			private int medal;
			private byte state;
		}

		public static class UpWearFashion extends SimplePacket
		{
			public UpWearFashion() { }

			public UpWearFashion(int roleID, int fashionType, int fashionID, int isShow)
			{
				this.roleID = roleID;
				this.fashionType = fashionType;
				this.fashionID = fashionID;
				this.isShow = isShow;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpWearFashion;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				fashionType = is.popInteger();
				fashionID = is.popInteger();
				isShow = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(fashionType);
				os.pushInteger(fashionID);
				os.pushInteger(isShow);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getFashionType()
			{
				return fashionType;
			}

			public void setFashionType(int fashionType)
			{
				this.fashionType = fashionType;
			}

			public int getFashionID()
			{
				return fashionID;
			}

			public void setFashionID(int fashionID)
			{
				this.fashionID = fashionID;
			}

			public int getIsShow()
			{
				return isShow;
			}

			public void setIsShow(int isShow)
			{
				this.isShow = isShow;
			}

			private int roleID;
			private int fashionType;
			private int fashionID;
			private int isShow;
		}

		public static class UpdateAlterState extends SimplePacket
		{
			public UpdateAlterState() { }

			public UpdateAlterState(int roleID, SBean.DBAlterState alterState)
			{
				this.roleID = roleID;
				this.alterState = alterState;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateAlterState;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( alterState == null )
					alterState = new SBean.DBAlterState();
				is.pop(alterState);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(alterState);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBAlterState getAlterState()
			{
				return alterState;
			}

			public void setAlterState(SBean.DBAlterState alterState)
			{
				this.alterState = alterState;
			}

			private int roleID;
			private SBean.DBAlterState alterState;
		}

		public static class ChangeHorseShow extends SimplePacket
		{
			public ChangeHorseShow() { }

			public ChangeHorseShow(int roleID, int hid, int showID)
			{
				this.roleID = roleID;
				this.hid = hid;
				this.showID = showID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTChangeHorseShow;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				hid = is.popInteger();
				showID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(hid);
				os.pushInteger(showID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getHid()
			{
				return hid;
			}

			public void setHid(int hid)
			{
				this.hid = hid;
			}

			public int getShowID()
			{
				return showID;
			}

			public void setShowID(int showID)
			{
				this.showID = showID;
			}

			private int roleID;
			private int hid;
			private int showID;
		}

		public static class AddBuff extends SimplePacket
		{
			public AddBuff() { }

			public AddBuff(int roleID, int buffID)
			{
				this.roleID = roleID;
				this.buffID = buffID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTAddBuff;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				buffID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(buffID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getBuffID()
			{
				return buffID;
			}

			public void setBuffID(int buffID)
			{
				this.buffID = buffID;
			}

			private int roleID;
			private int buffID;
		}

		public static class UpdateSealGrade extends SimplePacket
		{
			public UpdateSealGrade() { }

			public UpdateSealGrade(int roleID, int sealGrade)
			{
				this.roleID = roleID;
				this.sealGrade = sealGrade;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateSealGrade;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				sealGrade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(sealGrade);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getSealGrade()
			{
				return sealGrade;
			}

			public void setSealGrade(int sealGrade)
			{
				this.sealGrade = sealGrade;
			}

			private int roleID;
			private int sealGrade;
		}

		public static class UpdateSealSkills extends SimplePacket
		{
			public UpdateSealSkills() { }

			public UpdateSealSkills(int roleID, Map<Integer, Integer> skills)
			{
				this.roleID = roleID;
				this.skills = skills;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateSealSkills;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				skills = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(skills);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getSkills()
			{
				return skills;
			}

			public void setSkills(Map<Integer, Integer> skills)
			{
				this.skills = skills;
			}

			private int roleID;
			private Map<Integer, Integer> skills;
		}

		public static class SyncRolePetLack extends SimplePacket
		{
			public SyncRolePetLack() { }

			public SyncRolePetLack(int roleID, byte petLack)
			{
				this.roleID = roleID;
				this.petLack = petLack;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncRolePetLack;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				petLack = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushByte(petLack);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getPetLack()
			{
				return petLack;
			}

			public void setPetLack(byte petLack)
			{
				this.petLack = petLack;
			}

			private int roleID;
			private byte petLack;
		}

		public static class UpdateRoleGrasp extends SimplePacket
		{
			public UpdateRoleGrasp() { }

			public UpdateRoleGrasp(int roleID, int graspID, int level)
			{
				this.roleID = roleID;
				this.graspID = graspID;
				this.level = level;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleGrasp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				graspID = is.popInteger();
				level = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(graspID);
				os.pushInteger(level);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getGraspID()
			{
				return graspID;
			}

			public void setGraspID(int graspID)
			{
				this.graspID = graspID;
			}

			public int getLevel()
			{
				return level;
			}

			public void setLevel(int level)
			{
				this.level = level;
			}

			private int roleID;
			private int graspID;
			private int level;
		}

		public static class UpdateRareBook extends SimplePacket
		{
			public UpdateRareBook() { }

			public UpdateRareBook(int roleID, int bookID, int level)
			{
				this.roleID = roleID;
				this.bookID = bookID;
				this.level = level;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRareBook;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				bookID = is.popInteger();
				level = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(bookID);
				os.pushInteger(level);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getBookID()
			{
				return bookID;
			}

			public void setBookID(int bookID)
			{
				this.bookID = bookID;
			}

			public int getLevel()
			{
				return level;
			}

			public void setLevel(int level)
			{
				this.level = level;
			}

			private int roleID;
			private int bookID;
			private int level;
		}

		public static class UpdateRoleTitle extends SimplePacket
		{
			public UpdateRoleTitle() { }

			public UpdateRoleTitle(int roleID, int titleID, byte add)
			{
				this.roleID = roleID;
				this.titleID = titleID;
				this.add = add;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleTitle;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				titleID = is.popInteger();
				add = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(titleID);
				os.pushByte(add);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getTitleID()
			{
				return titleID;
			}

			public void setTitleID(int titleID)
			{
				this.titleID = titleID;
			}

			public byte getAdd()
			{
				return add;
			}

			public void setAdd(byte add)
			{
				this.add = add;
			}

			private int roleID;
			private int titleID;
			private byte add;
		}

		public static class UpdateRoleCurTitle extends SimplePacket
		{
			public UpdateRoleCurTitle() { }

			public UpdateRoleCurTitle(int roleID, int titleID, int titleType)
			{
				this.roleID = roleID;
				this.titleID = titleID;
				this.titleType = titleType;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleCurTitle;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				titleID = is.popInteger();
				titleType = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(titleID);
				os.pushInteger(titleType);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getTitleID()
			{
				return titleID;
			}

			public void setTitleID(int titleID)
			{
				this.titleID = titleID;
			}

			public int getTitleType()
			{
				return titleType;
			}

			public void setTitleType(int titleType)
			{
				this.titleType = titleType;
			}

			private int roleID;
			private int titleID;
			private int titleType;
		}

		public static class UpdatePetAchieve extends SimplePacket
		{
			public UpdatePetAchieve() { }

			public UpdatePetAchieve(int roleID, Set<Integer> achieves)
			{
				this.roleID = roleID;
				this.achieves = achieves;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdatePetAchieve;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				achieves = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerSet(achieves);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Set<Integer> getAchieves()
			{
				return achieves;
			}

			public void setAchieves(Set<Integer> achieves)
			{
				this.achieves = achieves;
			}

			private int roleID;
			private Set<Integer> achieves;
		}

		public static class UpdateCurUniqueSkill extends SimplePacket
		{
			public UpdateCurUniqueSkill() { }

			public UpdateCurUniqueSkill(int roleID, int curUniqueSkill)
			{
				this.roleID = roleID;
				this.curUniqueSkill = curUniqueSkill;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateCurUniqueSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				curUniqueSkill = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(curUniqueSkill);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getCurUniqueSkill()
			{
				return curUniqueSkill;
			}

			public void setCurUniqueSkill(int curUniqueSkill)
			{
				this.curUniqueSkill = curUniqueSkill;
			}

			private int roleID;
			private int curUniqueSkill;
		}

		public static class SetPetAlter extends SimplePacket
		{
			public SetPetAlter() { }

			public SetPetAlter(int roleID, SBean.FightPet pet, SBean.PetHost host)
			{
				this.roleID = roleID;
				this.pet = pet;
				this.host = host;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSetPetAlter;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( pet == null )
					pet = new SBean.FightPet();
				is.pop(pet);
				if( host == null )
					host = new SBean.PetHost();
				is.pop(host);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(pet);
				os.push(host);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.FightPet getPet()
			{
				return pet;
			}

			public void setPet(SBean.FightPet pet)
			{
				this.pet = pet;
			}

			public SBean.PetHost getHost()
			{
				return host;
			}

			public void setHost(SBean.PetHost host)
			{
				this.host = host;
			}

			private int roleID;
			private SBean.FightPet pet;
			private SBean.PetHost host;
		}

		public static class CarEnterMap extends SimplePacket
		{
			public CarEnterMap() { }

			public CarEnterMap(SBean.DBEscortCar carInfo, int ownerID, String ownerName, int teamCarCnt, 
			                   SBean.Team team, int sectID)
			{
				this.carInfo = carInfo;
				this.ownerID = ownerID;
				this.ownerName = ownerName;
				this.teamCarCnt = teamCarCnt;
				this.team = team;
				this.sectID = sectID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCarEnterMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( carInfo == null )
					carInfo = new SBean.DBEscortCar();
				is.pop(carInfo);
				ownerID = is.popInteger();
				ownerName = is.popString();
				teamCarCnt = is.popInteger();
				if( team == null )
					team = new SBean.Team();
				is.pop(team);
				sectID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(carInfo);
				os.pushInteger(ownerID);
				os.pushString(ownerName);
				os.pushInteger(teamCarCnt);
				os.push(team);
				os.pushInteger(sectID);
			}

			public SBean.DBEscortCar getCarInfo()
			{
				return carInfo;
			}

			public void setCarInfo(SBean.DBEscortCar carInfo)
			{
				this.carInfo = carInfo;
			}

			public int getOwnerID()
			{
				return ownerID;
			}

			public void setOwnerID(int ownerID)
			{
				this.ownerID = ownerID;
			}

			public String getOwnerName()
			{
				return ownerName;
			}

			public void setOwnerName(String ownerName)
			{
				this.ownerName = ownerName;
			}

			public int getTeamCarCnt()
			{
				return teamCarCnt;
			}

			public void setTeamCarCnt(int teamCarCnt)
			{
				this.teamCarCnt = teamCarCnt;
			}

			public SBean.Team getTeam()
			{
				return team;
			}

			public void setTeam(SBean.Team team)
			{
				this.team = team;
			}

			public int getSectID()
			{
				return sectID;
			}

			public void setSectID(int sectID)
			{
				this.sectID = sectID;
			}

			private SBean.DBEscortCar carInfo;
			private int ownerID;
			private String ownerName;
			private int teamCarCnt;
			private SBean.Team team;
			private int sectID;
		}

		public static class CarLeaveMap extends SimplePacket
		{
			public CarLeaveMap() { }

			public CarLeaveMap(int carID)
			{
				this.carID = carID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCarLeaveMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				carID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(carID);
			}

			public int getCarID()
			{
				return carID;
			}

			public void setCarID(int carID)
			{
				this.carID = carID;
			}

			private int carID;
		}

		public static class CarUpdateTeamCarCnt extends SimplePacket
		{
			public CarUpdateTeamCarCnt() { }

			public CarUpdateTeamCarCnt(int carID, int carCnt)
			{
				this.carID = carID;
				this.carCnt = carCnt;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCarUpdateTeamCarCnt;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				carID = is.popInteger();
				carCnt = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(carID);
				os.pushInteger(carCnt);
			}

			public int getCarID()
			{
				return carID;
			}

			public void setCarID(int carID)
			{
				this.carID = carID;
			}

			public int getCarCnt()
			{
				return carCnt;
			}

			public void setCarCnt(int carCnt)
			{
				this.carCnt = carCnt;
			}

			private int carID;
			private int carCnt;
		}

		public static class UpdateRoleCarBehavior extends SimplePacket
		{
			public UpdateRoleCarBehavior() { }

			public UpdateRoleCarBehavior(int roleID, byte carOwner, byte carRobber)
			{
				this.roleID = roleID;
				this.carOwner = carOwner;
				this.carRobber = carRobber;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleCarBehavior;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				carOwner = is.popByte();
				carRobber = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushByte(carOwner);
				os.pushByte(carRobber);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getCarOwner()
			{
				return carOwner;
			}

			public void setCarOwner(byte carOwner)
			{
				this.carOwner = carOwner;
			}

			public byte getCarRobber()
			{
				return carRobber;
			}

			public void setCarRobber(byte carRobber)
			{
				this.carRobber = carRobber;
			}

			private int roleID;
			private byte carOwner;
			private byte carRobber;
		}

		public static class RoleUseItemSkill extends SimplePacket
		{
			public RoleUseItemSkill() { }

			public RoleUseItemSkill(int roleID, int itemID, SBean.Vector3 pos, SBean.Vector3F rotation, 
			                        int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
			{
				this.roleID = roleID;
				this.itemID = itemID;
				this.pos = pos;
				this.rotation = rotation;
				this.targetID = targetID;
				this.targetType = targetType;
				this.ownerID = ownerID;
				this.timeTick = timeTick;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTRoleUseItemSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				itemID = is.popInteger();
				if( pos == null )
					pos = new SBean.Vector3();
				is.pop(pos);
				if( rotation == null )
					rotation = new SBean.Vector3F();
				is.pop(rotation);
				targetID = is.popInteger();
				targetType = is.popInteger();
				ownerID = is.popInteger();
				if( timeTick == null )
					timeTick = new SBean.TimeTick();
				is.pop(timeTick);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(itemID);
				os.push(pos);
				os.push(rotation);
				os.pushInteger(targetID);
				os.pushInteger(targetType);
				os.pushInteger(ownerID);
				os.push(timeTick);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getItemID()
			{
				return itemID;
			}

			public void setItemID(int itemID)
			{
				this.itemID = itemID;
			}

			public SBean.Vector3 getPos()
			{
				return pos;
			}

			public void setPos(SBean.Vector3 pos)
			{
				this.pos = pos;
			}

			public SBean.Vector3F getRotation()
			{
				return rotation;
			}

			public void setRotation(SBean.Vector3F rotation)
			{
				this.rotation = rotation;
			}

			public int getTargetID()
			{
				return targetID;
			}

			public void setTargetID(int targetID)
			{
				this.targetID = targetID;
			}

			public int getTargetType()
			{
				return targetType;
			}

			public void setTargetType(int targetType)
			{
				this.targetType = targetType;
			}

			public int getOwnerID()
			{
				return ownerID;
			}

			public void setOwnerID(int ownerID)
			{
				this.ownerID = ownerID;
			}

			public SBean.TimeTick getTimeTick()
			{
				return timeTick;
			}

			public void setTimeTick(SBean.TimeTick timeTick)
			{
				this.timeTick = timeTick;
			}

			private int roleID;
			private int itemID;
			private SBean.Vector3 pos;
			private SBean.Vector3F rotation;
			private int targetID;
			private int targetType;
			private int ownerID;
			private SBean.TimeTick timeTick;
		}

		public static class RoleRename extends SimplePacket
		{
			public RoleRename() { }

			public RoleRename(int roleID, String newName)
			{
				this.roleID = roleID;
				this.newName = newName;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTRoleRename;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				newName = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(newName);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getNewName()
			{
				return newName;
			}

			public void setNewName(String newName)
			{
				this.newName = newName;
			}

			private int roleID;
			private String newName;
		}

		public static class AddPetHp extends SimplePacket
		{
			public AddPetHp() { }

			public AddPetHp(int roleID, int petID, int hp)
			{
				this.roleID = roleID;
				this.petID = petID;
				this.hp = hp;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTAddPetHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				petID = is.popInteger();
				hp = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(petID);
				os.pushInteger(hp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPetID()
			{
				return petID;
			}

			public void setPetID(int petID)
			{
				this.petID = petID;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			private int roleID;
			private int petID;
			private int hp;
		}

		public static class SyncCurRideHorse extends SimplePacket
		{
			public SyncCurRideHorse() { }

			public SyncCurRideHorse(int roleID, int horseID)
			{
				this.roleID = roleID;
				this.horseID = horseID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncCurRideHorse;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				horseID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(horseID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getHorseID()
			{
				return horseID;
			}

			public void setHorseID(int horseID)
			{
				this.horseID = horseID;
			}

			private int roleID;
			private int horseID;
		}

		public static class UpdateMulHorse extends SimplePacket
		{
			public UpdateMulHorse() { }

			public UpdateMulHorse(int leaderID, int pos, int memberID)
			{
				this.leaderID = leaderID;
				this.pos = pos;
				this.memberID = memberID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateMulHorse;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				leaderID = is.popInteger();
				pos = is.popInteger();
				memberID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(leaderID);
				os.pushInteger(pos);
				os.pushInteger(memberID);
			}

			public int getLeaderID()
			{
				return leaderID;
			}

			public void setLeaderID(int leaderID)
			{
				this.leaderID = leaderID;
			}

			public int getPos()
			{
				return pos;
			}

			public void setPos(int pos)
			{
				this.pos = pos;
			}

			public int getMemberID()
			{
				return memberID;
			}

			public void setMemberID(int memberID)
			{
				this.memberID = memberID;
			}

			private int leaderID;
			private int pos;
			private int memberID;
		}

		public static class ChangeArmor extends SimplePacket
		{
			public ChangeArmor() { }

			public ChangeArmor(int roleID, SBean.ArmorFightData armor)
			{
				this.roleID = roleID;
				this.armor = armor;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTChangeArmor;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( armor == null )
					armor = new SBean.ArmorFightData();
				is.pop(armor);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(armor);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.ArmorFightData getArmor()
			{
				return armor;
			}

			public void setArmor(SBean.ArmorFightData armor)
			{
				this.armor = armor;
			}

			private int roleID;
			private SBean.ArmorFightData armor;
		}

		public static class UpdateArmorLevel extends SimplePacket
		{
			public UpdateArmorLevel() { }

			public UpdateArmorLevel(int roleID, int armorLevel)
			{
				this.roleID = roleID;
				this.armorLevel = armorLevel;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateArmorLevel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				armorLevel = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(armorLevel);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getArmorLevel()
			{
				return armorLevel;
			}

			public void setArmorLevel(int armorLevel)
			{
				this.armorLevel = armorLevel;
			}

			private int roleID;
			private int armorLevel;
		}

		public static class UpdateArmorRank extends SimplePacket
		{
			public UpdateArmorRank() { }

			public UpdateArmorRank(int roleID, int armorRank)
			{
				this.roleID = roleID;
				this.armorRank = armorRank;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateArmorRank;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				armorRank = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(armorRank);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getArmorRank()
			{
				return armorRank;
			}

			public void setArmorRank(int armorRank)
			{
				this.armorRank = armorRank;
			}

			private int roleID;
			private int armorRank;
		}

		public static class UpdateArmorRune extends SimplePacket
		{
			public UpdateArmorRune() { }

			public UpdateArmorRune(int roleID, int soltGroupIndex, List<Integer> armorRune)
			{
				this.roleID = roleID;
				this.soltGroupIndex = soltGroupIndex;
				this.armorRune = armorRune;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateArmorRune;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				soltGroupIndex = is.popInteger();
				armorRune = is.popIntegerList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(soltGroupIndex);
				os.pushIntegerList(armorRune);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getSoltGroupIndex()
			{
				return soltGroupIndex;
			}

			public void setSoltGroupIndex(int soltGroupIndex)
			{
				this.soltGroupIndex = soltGroupIndex;
			}

			public List<Integer> getArmorRune()
			{
				return armorRune;
			}

			public void setArmorRune(List<Integer> armorRune)
			{
				this.armorRune = armorRune;
			}

			private int roleID;
			private int soltGroupIndex;
			private List<Integer> armorRune;
		}

		public static class UpdateTalentPoint extends SimplePacket
		{
			public UpdateTalentPoint() { }

			public UpdateTalentPoint(int roleID, Map<Integer, Integer> armorTalentPoint)
			{
				this.roleID = roleID;
				this.armorTalentPoint = armorTalentPoint;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateTalentPoint;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				armorTalentPoint = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(armorTalentPoint);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getArmorTalentPoint()
			{
				return armorTalentPoint;
			}

			public void setArmorTalentPoint(Map<Integer, Integer> armorTalentPoint)
			{
				this.armorTalentPoint = armorTalentPoint;
			}

			private int roleID;
			private Map<Integer, Integer> armorTalentPoint;
		}

		public static class SpawnSceneMonster extends SimplePacket
		{
			public SpawnSceneMonster() { }

			public SpawnSceneMonster(int mapID, int mapInstance, int roleID, int pointID)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.roleID = roleID;
				this.pointID = pointID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSpawnSceneMonster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				roleID = is.popInteger();
				pointID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(roleID);
				os.pushInteger(pointID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPointID()
			{
				return pointID;
			}

			public void setPointID(int pointID)
			{
				this.pointID = pointID;
			}

			private int mapID;
			private int mapInstance;
			private int roleID;
			private int pointID;
		}

		public static class ClearSceneMonster extends SimplePacket
		{
			public ClearSceneMonster() { }

			public ClearSceneMonster(int roleID, int monsterID)
			{
				this.roleID = roleID;
				this.monsterID = monsterID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTClearSceneMonster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				monsterID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(monsterID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMonsterID()
			{
				return monsterID;
			}

			public void setMonsterID(int monsterID)
			{
				this.monsterID = monsterID;
			}

			private int roleID;
			private int monsterID;
		}

		public static class ResetSectGroupMap extends SimplePacket
		{
			public ResetSectGroupMap() { }

			public ResetSectGroupMap(int mapID, int instanceID, Map<Integer, Integer> progress, Map<Integer, Integer> killNum, 
			                         Map<Integer, SBean.RoleDamageDetail> damageRank)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
				this.progress = progress;
				this.killNum = killNum;
				this.damageRank = damageRank;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTResetSectGroupMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
				progress = is.popIntegerIntegerTreeMap();
				killNum = is.popIntegerIntegerTreeMap();
				damageRank = is.popIntegerTreeMap(SBean.RoleDamageDetail.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
				os.pushIntegerIntegerMap(progress);
				os.pushIntegerIntegerMap(killNum);
				os.pushIntegerMap(damageRank);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			public Map<Integer, Integer> getProgress()
			{
				return progress;
			}

			public void setProgress(Map<Integer, Integer> progress)
			{
				this.progress = progress;
			}

			public Map<Integer, Integer> getKillNum()
			{
				return killNum;
			}

			public void setKillNum(Map<Integer, Integer> killNum)
			{
				this.killNum = killNum;
			}

			public Map<Integer, SBean.RoleDamageDetail> getDamageRank()
			{
				return damageRank;
			}

			public void setDamageRank(Map<Integer, SBean.RoleDamageDetail> damageRank)
			{
				this.damageRank = damageRank;
			}

			private int mapID;
			private int instanceID;
			private Map<Integer, Integer> progress;
			private Map<Integer, Integer> killNum;
			private Map<Integer, SBean.RoleDamageDetail> damageRank;
		}

		public static class UpdateHorseSkill extends SimplePacket
		{
			public UpdateHorseSkill() { }

			public UpdateHorseSkill(int roleID, int skillID, int skillLvl)
			{
				this.roleID = roleID;
				this.skillID = skillID;
				this.skillLvl = skillLvl;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateHorseSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				skillID = is.popInteger();
				skillLvl = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(skillID);
				os.pushInteger(skillLvl);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getSkillID()
			{
				return skillID;
			}

			public void setSkillID(int skillID)
			{
				this.skillID = skillID;
			}

			public int getSkillLvl()
			{
				return skillLvl;
			}

			public void setSkillLvl(int skillLvl)
			{
				this.skillLvl = skillLvl;
			}

			private int roleID;
			private int skillID;
			private int skillLvl;
		}

		public static class UpdateStayWith extends SimplePacket
		{
			public UpdateStayWith() { }

			public UpdateStayWith(int leaderID, SBean.MulRoleInfo mulRoleInfo)
			{
				this.leaderID = leaderID;
				this.mulRoleInfo = mulRoleInfo;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateStayWith;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				leaderID = is.popInteger();
				if( mulRoleInfo == null )
					mulRoleInfo = new SBean.MulRoleInfo();
				is.pop(mulRoleInfo);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(leaderID);
				os.push(mulRoleInfo);
			}

			public int getLeaderID()
			{
				return leaderID;
			}

			public void setLeaderID(int leaderID)
			{
				this.leaderID = leaderID;
			}

			public SBean.MulRoleInfo getMulRoleInfo()
			{
				return mulRoleInfo;
			}

			public void setMulRoleInfo(SBean.MulRoleInfo mulRoleInfo)
			{
				this.mulRoleInfo = mulRoleInfo;
			}

			private int leaderID;
			private SBean.MulRoleInfo mulRoleInfo;
		}

		public static class UpdateRoleWeaponSkill extends SimplePacket
		{
			public UpdateRoleWeaponSkill() { }

			public UpdateRoleWeaponSkill(int roleID, int weaponID, List<Integer> skills)
			{
				this.roleID = roleID;
				this.weaponID = weaponID;
				this.skills = skills;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleWeaponSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				weaponID = is.popInteger();
				skills = is.popIntegerList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(weaponID);
				os.pushIntegerList(skills);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getWeaponID()
			{
				return weaponID;
			}

			public void setWeaponID(int weaponID)
			{
				this.weaponID = weaponID;
			}

			public List<Integer> getSkills()
			{
				return skills;
			}

			public void setSkills(List<Integer> skills)
			{
				this.skills = skills;
			}

			private int roleID;
			private int weaponID;
			private List<Integer> skills;
		}

		public static class UpdateRoleWeaponTalent extends SimplePacket
		{
			public UpdateRoleWeaponTalent() { }

			public UpdateRoleWeaponTalent(int roleID, int weaponID, List<Integer> talents)
			{
				this.roleID = roleID;
				this.weaponID = weaponID;
				this.talents = talents;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleWeaponTalent;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				weaponID = is.popInteger();
				talents = is.popIntegerList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(weaponID);
				os.pushIntegerList(talents);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getWeaponID()
			{
				return weaponID;
			}

			public void setWeaponID(int weaponID)
			{
				this.weaponID = weaponID;
			}

			public List<Integer> getTalents()
			{
				return talents;
			}

			public void setTalents(List<Integer> talents)
			{
				this.talents = talents;
			}

			private int roleID;
			private int weaponID;
			private List<Integer> talents;
		}

		public static class CreateWorldMapFlag extends SimplePacket
		{
			public CreateWorldMapFlag() { }

			public CreateWorldMapFlag(int mapID, SBean.Vector3 flagPoint, int flagId, List<Integer> monsterPointId, 
			                          SBean.MapFlagSectOverView sect)
			{
				this.mapID = mapID;
				this.flagPoint = flagPoint;
				this.flagId = flagId;
				this.monsterPointId = monsterPointId;
				this.sect = sect;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCreateWorldMapFlag;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				if( flagPoint == null )
					flagPoint = new SBean.Vector3();
				is.pop(flagPoint);
				flagId = is.popInteger();
				monsterPointId = is.popIntegerList();
				if( sect == null )
					sect = new SBean.MapFlagSectOverView();
				is.pop(sect);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.push(flagPoint);
				os.pushInteger(flagId);
				os.pushIntegerList(monsterPointId);
				os.push(sect);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public SBean.Vector3 getFlagPoint()
			{
				return flagPoint;
			}

			public void setFlagPoint(SBean.Vector3 flagPoint)
			{
				this.flagPoint = flagPoint;
			}

			public int getFlagId()
			{
				return flagId;
			}

			public void setFlagId(int flagId)
			{
				this.flagId = flagId;
			}

			public List<Integer> getMonsterPointId()
			{
				return monsterPointId;
			}

			public void setMonsterPointId(List<Integer> monsterPointId)
			{
				this.monsterPointId = monsterPointId;
			}

			public SBean.MapFlagSectOverView getSect()
			{
				return sect;
			}

			public void setSect(SBean.MapFlagSectOverView sect)
			{
				this.sect = sect;
			}

			private int mapID;
			private SBean.Vector3 flagPoint;
			private int flagId;
			private List<Integer> monsterPointId;
			private SBean.MapFlagSectOverView sect;
		}

		public static class InitWorldMapFlag extends SimplePacket
		{
			public InitWorldMapFlag() { }

			public InitWorldMapFlag(Map<Integer, SBean.MapFlagInfo> mapflags)
			{
				this.mapflags = mapflags;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTInitWorldMapFlag;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapflags = is.popIntegerTreeMap(SBean.MapFlagInfo.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushIntegerMap(mapflags);
			}

			public Map<Integer, SBean.MapFlagInfo> getMapflags()
			{
				return mapflags;
			}

			public void setMapflags(Map<Integer, SBean.MapFlagInfo> mapflags)
			{
				this.mapflags = mapflags;
			}

			private Map<Integer, SBean.MapFlagInfo> mapflags;
		}

		public static class SyncMapFlagInfo extends SimplePacket
		{
			public SyncMapFlagInfo() { }

			public SyncMapFlagInfo(int mapID, SBean.MapFlagSectOverView sect)
			{
				this.mapID = mapID;
				this.sect = sect;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncMapFlagInfo;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				if( sect == null )
					sect = new SBean.MapFlagSectOverView();
				is.pop(sect);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.push(sect);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public SBean.MapFlagSectOverView getSect()
			{
				return sect;
			}

			public void setSect(SBean.MapFlagSectOverView sect)
			{
				this.sect = sect;
			}

			private int mapID;
			private SBean.MapFlagSectOverView sect;
		}

		public static class SyncRoleItemProps extends SimplePacket
		{
			public SyncRoleItemProps() { }

			public SyncRoleItemProps(int roleID, Map<Integer, Integer> props)
			{
				this.roleID = roleID;
				this.props = props;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncRoleItemProps;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				props = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(props);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getProps()
			{
				return props;
			}

			public void setProps(Map<Integer, Integer> props)
			{
				this.props = props;
			}

			private int roleID;
			private Map<Integer, Integer> props;
		}

		public static class SyncTaskDrop extends SimplePacket
		{
			public SyncTaskDrop() { }

			public SyncTaskDrop(int roleID, Map<Integer, Integer> taskDrop)
			{
				this.roleID = roleID;
				this.taskDrop = taskDrop;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncTaskDrop;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				taskDrop = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(taskDrop);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getTaskDrop()
			{
				return taskDrop;
			}

			public void setTaskDrop(Map<Integer, Integer> taskDrop)
			{
				this.taskDrop = taskDrop;
			}

			private int roleID;
			private Map<Integer, Integer> taskDrop;
		}

		public static class UpdateRolePetSkill extends SimplePacket
		{
			public UpdateRolePetSkill() { }

			public UpdateRolePetSkill(int roleID, int petId, List<Integer> skills)
			{
				this.roleID = roleID;
				this.petId = petId;
				this.skills = skills;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRolePetSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				petId = is.popInteger();
				skills = is.popIntegerList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(petId);
				os.pushIntegerList(skills);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPetId()
			{
				return petId;
			}

			public void setPetId(int petId)
			{
				this.petId = petId;
			}

			public List<Integer> getSkills()
			{
				return skills;
			}

			public void setSkills(List<Integer> skills)
			{
				this.skills = skills;
			}

			private int roleID;
			private int petId;
			private List<Integer> skills;
		}

		public static class SyncWeaponOpen extends SimplePacket
		{
			public SyncWeaponOpen() { }

			public SyncWeaponOpen(int roleID, int weaponID)
			{
				this.roleID = roleID;
				this.weaponID = weaponID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncWeaponOpen;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				weaponID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(weaponID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getWeaponID()
			{
				return weaponID;
			}

			public void setWeaponID(int weaponID)
			{
				this.weaponID = weaponID;
			}

			private int roleID;
			private int weaponID;
		}

		public static class WorldBossPop extends SimplePacket
		{
			public WorldBossPop() { }

			public WorldBossPop(int mapID, int mapInstance, int bossID, int index)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.bossID = bossID;
				this.index = index;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTWorldBossPop;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				bossID = is.popInteger();
				index = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(bossID);
				os.pushInteger(index);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getBossID()
			{
				return bossID;
			}

			public void setBossID(int bossID)
			{
				this.bossID = bossID;
			}

			public int getIndex()
			{
				return index;
			}

			public void setIndex(int index)
			{
				this.index = index;
			}

			private int mapID;
			private int mapInstance;
			private int bossID;
			private int index;
		}

		public static class PickUpResult extends SimplePacket
		{
			public PickUpResult() { }

			public PickUpResult(int roleID, Set<Integer> dropIDs, int success)
			{
				this.roleID = roleID;
				this.dropIDs = dropIDs;
				this.success = success;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTPickUpResult;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				dropIDs = is.popIntegerTreeSet();
				success = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerSet(dropIDs);
				os.pushInteger(success);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Set<Integer> getDropIDs()
			{
				return dropIDs;
			}

			public void setDropIDs(Set<Integer> dropIDs)
			{
				this.dropIDs = dropIDs;
			}

			public int getSuccess()
			{
				return success;
			}

			public void setSuccess(int success)
			{
				this.success = success;
			}

			private int roleID;
			private Set<Integer> dropIDs;
			private int success;
		}

		public static class UnSummonCurPets extends SimplePacket
		{
			public UnSummonCurPets() { }

			public UnSummonCurPets(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUnSummonCurPets;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class UpdateRolePerfectDegree extends SimplePacket
		{
			public UpdateRolePerfectDegree() { }

			public UpdateRolePerfectDegree(int roleID, int perfectDegree)
			{
				this.roleID = roleID;
				this.perfectDegree = perfectDegree;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRolePerfectDegree;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				perfectDegree = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(perfectDegree);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPerfectDegree()
			{
				return perfectDegree;
			}

			public void setPerfectDegree(int perfectDegree)
			{
				this.perfectDegree = perfectDegree;
			}

			private int roleID;
			private int perfectDegree;
		}

		public static class UpdateCurPetSpirit extends SimplePacket
		{
			public UpdateCurPetSpirit() { }

			public UpdateCurPetSpirit(int roleID, int petID, int index, SBean.PetSpirit spirit)
			{
				this.roleID = roleID;
				this.petID = petID;
				this.index = index;
				this.spirit = spirit;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateCurPetSpirit;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				petID = is.popInteger();
				index = is.popInteger();
				if( spirit == null )
					spirit = new SBean.PetSpirit();
				is.pop(spirit);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(petID);
				os.pushInteger(index);
				os.push(spirit);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPetID()
			{
				return petID;
			}

			public void setPetID(int petID)
			{
				this.petID = petID;
			}

			public int getIndex()
			{
				return index;
			}

			public void setIndex(int index)
			{
				this.index = index;
			}

			public SBean.PetSpirit getSpirit()
			{
				return spirit;
			}

			public void setSpirit(SBean.PetSpirit spirit)
			{
				this.spirit = spirit;
			}

			private int roleID;
			private int petID;
			private int index;
			private SBean.PetSpirit spirit;
		}

		public static class StartMarriageParade extends SimplePacket
		{
			public StartMarriageParade() { }

			public StartMarriageParade(int mapID, int mapInstance, int carID, SBean.RoleOverview man, 
			                           SBean.RoleOverview woman)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.carID = carID;
				this.man = man;
				this.woman = woman;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTStartMarriageParade;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				carID = is.popInteger();
				if( man == null )
					man = new SBean.RoleOverview();
				is.pop(man);
				if( woman == null )
					woman = new SBean.RoleOverview();
				is.pop(woman);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(carID);
				os.push(man);
				os.push(woman);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getCarID()
			{
				return carID;
			}

			public void setCarID(int carID)
			{
				this.carID = carID;
			}

			public SBean.RoleOverview getMan()
			{
				return man;
			}

			public void setMan(SBean.RoleOverview man)
			{
				this.man = man;
			}

			public SBean.RoleOverview getWoman()
			{
				return woman;
			}

			public void setWoman(SBean.RoleOverview woman)
			{
				this.woman = woman;
			}

			private int mapID;
			private int mapInstance;
			private int carID;
			private SBean.RoleOverview man;
			private SBean.RoleOverview woman;
		}

		public static class UpdateRoleHeirloomDisplay extends SimplePacket
		{
			public UpdateRoleHeirloomDisplay() { }

			public UpdateRoleHeirloomDisplay(int roleID, int display)
			{
				this.roleID = roleID;
				this.display = display;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleHeirloomDisplay;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				display = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(display);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getDisplay()
			{
				return display;
			}

			public void setDisplay(int display)
			{
				this.display = display;
			}

			private int roleID;
			private int display;
		}

		public static class SetWeaponForm extends SimplePacket
		{
			public SetWeaponForm() { }

			public SetWeaponForm(int roleID, int weaponID, byte form)
			{
				this.roleID = roleID;
				this.weaponID = weaponID;
				this.form = form;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSetWeaponForm;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				weaponID = is.popInteger();
				form = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(weaponID);
				os.pushByte(form);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getWeaponID()
			{
				return weaponID;
			}

			public void setWeaponID(int weaponID)
			{
				this.weaponID = weaponID;
			}

			public byte getForm()
			{
				return form;
			}

			public void setForm(byte form)
			{
				this.form = form;
			}

			private int roleID;
			private int weaponID;
			private byte form;
		}

		public static class StartMarriageBanquet extends SimplePacket
		{
			public StartMarriageBanquet() { }

			public StartMarriageBanquet(int roleID, int mapID, int mapInstance, int banquet)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.banquet = banquet;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTStartMarriageBanquet;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				banquet = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(banquet);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getBanquet()
			{
				return banquet;
			}

			public void setBanquet(int banquet)
			{
				this.banquet = banquet;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int banquet;
		}

		public static class UpdateRoleMarriageSkillInfo extends SimplePacket
		{
			public UpdateRoleMarriageSkillInfo() { }

			public UpdateRoleMarriageSkillInfo(int roleID, Map<Integer, Integer> marriageSkills, int marriagePartnerId)
			{
				this.roleID = roleID;
				this.marriageSkills = marriageSkills;
				this.marriagePartnerId = marriagePartnerId;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleMarriageSkillInfo;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				marriageSkills = is.popIntegerIntegerTreeMap();
				marriagePartnerId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(marriageSkills);
				os.pushInteger(marriagePartnerId);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getMarriageSkills()
			{
				return marriageSkills;
			}

			public void setMarriageSkills(Map<Integer, Integer> marriageSkills)
			{
				this.marriageSkills = marriageSkills;
			}

			public int getMarriagePartnerId()
			{
				return marriagePartnerId;
			}

			public void setMarriagePartnerId(int marriagePartnerId)
			{
				this.marriagePartnerId = marriagePartnerId;
			}

			private int roleID;
			private Map<Integer, Integer> marriageSkills;
			private int marriagePartnerId;
		}

		public static class UpdateRoleMarriageSkillLevel extends SimplePacket
		{
			public UpdateRoleMarriageSkillLevel() { }

			public UpdateRoleMarriageSkillLevel(int roleID, int skillId, int skillLevel)
			{
				this.roleID = roleID;
				this.skillId = skillId;
				this.skillLevel = skillLevel;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleMarriageSkillLevel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				skillId = is.popInteger();
				skillLevel = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(skillId);
				os.pushInteger(skillLevel);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getSkillId()
			{
				return skillId;
			}

			public void setSkillId(int skillId)
			{
				this.skillId = skillId;
			}

			public int getSkillLevel()
			{
				return skillLevel;
			}

			public void setSkillLevel(int skillLevel)
			{
				this.skillLevel = skillLevel;
			}

			private int roleID;
			private int skillId;
			private int skillLevel;
		}

		public static class MarriageLevelChange extends SimplePacket
		{
			public MarriageLevelChange() { }

			public MarriageLevelChange(int roleID, int newLevel)
			{
				this.roleID = roleID;
				this.newLevel = newLevel;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTMarriageLevelChange;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				newLevel = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(newLevel);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getNewLevel()
			{
				return newLevel;
			}

			public void setNewLevel(int newLevel)
			{
				this.newLevel = newLevel;
			}

			private int roleID;
			private int newLevel;
		}

		public static class RoleDMGTransferUpdate extends SimplePacket
		{
			public RoleDMGTransferUpdate() { }

			public RoleDMGTransferUpdate(int roleID, Map<Integer, Integer> pointLvls)
			{
				this.roleID = roleID;
				this.pointLvls = pointLvls;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTRoleDMGTransferUpdate;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				pointLvls = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(pointLvls);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getPointLvls()
			{
				return pointLvls;
			}

			public void setPointLvls(Map<Integer, Integer> pointLvls)
			{
				this.pointLvls = pointLvls;
			}

			private int roleID;
			private Map<Integer, Integer> pointLvls;
		}

		public static class CreateRobotHero extends SimplePacket
		{
			public CreateRobotHero() { }

			public CreateRobotHero(SBean.FightRole role, int mapID, int mapInstance, Map<Integer, SBean.FightPet> pets, 
			                       int spawnPoint)
			{
				this.role = role;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.pets = pets;
				this.spawnPoint = spawnPoint;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTCreateRobotHero;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( role == null )
					role = new SBean.FightRole();
				is.pop(role);
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				pets = is.popIntegerTreeMap(SBean.FightPet.class);
				spawnPoint = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(role);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushIntegerMap(pets);
				os.pushInteger(spawnPoint);
			}

			public SBean.FightRole getRole()
			{
				return role;
			}

			public void setRole(SBean.FightRole role)
			{
				this.role = role;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public Map<Integer, SBean.FightPet> getPets()
			{
				return pets;
			}

			public void setPets(Map<Integer, SBean.FightPet> pets)
			{
				this.pets = pets;
			}

			public int getSpawnPoint()
			{
				return spawnPoint;
			}

			public void setSpawnPoint(int spawnPoint)
			{
				this.spawnPoint = spawnPoint;
			}

			private SBean.FightRole role;
			private int mapID;
			private int mapInstance;
			private Map<Integer, SBean.FightPet> pets;
			private int spawnPoint;
		}

		public static class DestroyRobotHero extends SimplePacket
		{
			public DestroyRobotHero() { }

			public DestroyRobotHero(int mapID, int mapInstance, int roleID)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTDestroyRobotHero;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(roleID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int mapID;
			private int mapInstance;
			private int roleID;
		}

		public static class SyncCreateStele extends SimplePacket
		{
			public SyncCreateStele() { }

			public SyncCreateStele(int steleType, int index, int remainTimes)
			{
				this.steleType = steleType;
				this.index = index;
				this.remainTimes = remainTimes;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncCreateStele;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				steleType = is.popInteger();
				index = is.popInteger();
				remainTimes = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(steleType);
				os.pushInteger(index);
				os.pushInteger(remainTimes);
			}

			public int getSteleType()
			{
				return steleType;
			}

			public void setSteleType(int steleType)
			{
				this.steleType = steleType;
			}

			public int getIndex()
			{
				return index;
			}

			public void setIndex(int index)
			{
				this.index = index;
			}

			public int getRemainTimes()
			{
				return remainTimes;
			}

			public void setRemainTimes(int remainTimes)
			{
				this.remainTimes = remainTimes;
			}

			private int steleType;
			private int index;
			private int remainTimes;
		}

		public static class SyncDestroyStele extends SimplePacket
		{
			public SyncDestroyStele() { }

			public SyncDestroyStele(int steleType, int index)
			{
				this.steleType = steleType;
				this.index = index;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncDestroyStele;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				steleType = is.popInteger();
				index = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(steleType);
				os.pushInteger(index);
			}

			public int getSteleType()
			{
				return steleType;
			}

			public void setSteleType(int steleType)
			{
				this.steleType = steleType;
			}

			public int getIndex()
			{
				return index;
			}

			public void setIndex(int index)
			{
				this.index = index;
			}

			private int steleType;
			private int index;
		}

		public static class SyncJusticeNpcShow extends SimplePacket
		{
			public SyncJusticeNpcShow() { }

			public SyncJusticeNpcShow(int posIndex)
			{
				this.posIndex = posIndex;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncJusticeNpcShow;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				posIndex = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(posIndex);
			}

			public int getPosIndex()
			{
				return posIndex;
			}

			public void setPosIndex(int posIndex)
			{
				this.posIndex = posIndex;
			}

			private int posIndex;
		}

		public static class SyncJusticeNpcLeave extends SimplePacket
		{
			public SyncJusticeNpcLeave() { }

			public SyncJusticeNpcLeave(int posIndex)
			{
				this.posIndex = posIndex;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncJusticeNpcLeave;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				posIndex = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(posIndex);
			}

			public int getPosIndex()
			{
				return posIndex;
			}

			public void setPosIndex(int posIndex)
			{
				this.posIndex = posIndex;
			}

			private int posIndex;
		}

		public static class SyncEmergencyLastTime extends SimplePacket
		{
			public SyncEmergencyLastTime() { }

			public SyncEmergencyLastTime(int mapID, int mapInstanceId, int lastTime)
			{
				this.mapID = mapID;
				this.mapInstanceId = mapInstanceId;
				this.lastTime = lastTime;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncEmergencyLastTime;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstanceId = is.popInteger();
				lastTime = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstanceId);
				os.pushInteger(lastTime);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstanceId()
			{
				return mapInstanceId;
			}

			public void setMapInstanceId(int mapInstanceId)
			{
				this.mapInstanceId = mapInstanceId;
			}

			public int getLastTime()
			{
				return lastTime;
			}

			public void setLastTime(int lastTime)
			{
				this.lastTime = lastTime;
			}

			private int mapID;
			private int mapInstanceId;
			private int lastTime;
		}

		public static class SyncRoleVipLevel extends SimplePacket
		{
			public SyncRoleVipLevel() { }

			public SyncRoleVipLevel(int roleID, int vipLevel)
			{
				this.roleID = roleID;
				this.vipLevel = vipLevel;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncRoleVipLevel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				vipLevel = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(vipLevel);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getVipLevel()
			{
				return vipLevel;
			}

			public void setVipLevel(int vipLevel)
			{
				this.vipLevel = vipLevel;
			}

			private int roleID;
			private int vipLevel;
		}

		public static class SyncRoleCurWizardPet extends SimplePacket
		{
			public SyncRoleCurWizardPet() { }

			public SyncRoleCurWizardPet(int roleID, int wizardPet)
			{
				this.roleID = roleID;
				this.wizardPet = wizardPet;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTSyncRoleCurWizardPet;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				wizardPet = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(wizardPet);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getWizardPet()
			{
				return wizardPet;
			}

			public void setWizardPet(int wizardPet)
			{
				this.wizardPet = wizardPet;
			}

			private int roleID;
			private int wizardPet;
		}

		public static class UpdateRoleSpecialCardAttr extends SimplePacket
		{
			public UpdateRoleSpecialCardAttr() { }

			public UpdateRoleSpecialCardAttr(int roleID, Map<Integer, Integer> attrs)
			{
				this.roleID = roleID;
				this.attrs = attrs;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTUpdateRoleSpecialCardAttr;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				attrs = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(attrs);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getAttrs()
			{
				return attrs;
			}

			public void setAttrs(Map<Integer, Integer> attrs)
			{
				this.attrs = attrs;
			}

			private int roleID;
			private Map<Integer, Integer> attrs;
		}

		public static class RoleShowProps extends SimplePacket
		{
			public RoleShowProps() { }

			public RoleShowProps(int roleID, int propID)
			{
				this.roleID = roleID;
				this.propID = propID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTRoleShowProps;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				propID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(propID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPropID()
			{
				return propID;
			}

			public void setPropID(int propID)
			{
				this.propID = propID;
			}

			private int roleID;
			private int propID;
		}

		public static class RoleRedNamePunish extends SimplePacket
		{
			public RoleRedNamePunish() { }

			public RoleRedNamePunish(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTRoleRedNamePunish;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class GMCommand extends SimplePacket
		{
			public GMCommand() { }

			public GMCommand(int roleID, String iType, int iArg1, int iArg2, 
			                 int iArg3, String sArg)
			{
				this.roleID = roleID;
				this.iType = iType;
				this.iArg1 = iArg1;
				this.iArg2 = iArg2;
				this.iArg3 = iArg3;
				this.sArg = sArg;
			}

			@Override
			public int getType()
			{
				return Packet.eS2MPKTGMCommand;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				iType = is.popString();
				iArg1 = is.popInteger();
				iArg2 = is.popInteger();
				iArg3 = is.popInteger();
				sArg = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(iType);
				os.pushInteger(iArg1);
				os.pushInteger(iArg2);
				os.pushInteger(iArg3);
				os.pushString(sArg);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getIType()
			{
				return iType;
			}

			public void setIType(String iType)
			{
				this.iType = iType;
			}

			public int getIArg1()
			{
				return iArg1;
			}

			public void setIArg1(int iArg1)
			{
				this.iArg1 = iArg1;
			}

			public int getIArg2()
			{
				return iArg2;
			}

			public void setIArg2(int iArg2)
			{
				this.iArg2 = iArg2;
			}

			public int getIArg3()
			{
				return iArg3;
			}

			public void setIArg3(int iArg3)
			{
				this.iArg3 = iArg3;
			}

			public String getSArg()
			{
				return sArg;
			}

			public void setSArg(String sArg)
			{
				this.sArg = sArg;
			}

			private int roleID;
			private String iType;
			private int iArg1;
			private int iArg2;
			private int iArg3;
			private String sArg;
		}

	}

	// map to server
	public static class M2S
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int groupID, int serverID, Set<Integer> maps)
			{
				this.groupID = groupID;
				this.serverID = serverID;
				this.maps = maps;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				groupID = is.popInteger();
				serverID = is.popInteger();
				maps = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(groupID);
				os.pushInteger(serverID);
				os.pushIntegerSet(maps);
			}

			public int getGroupID()
			{
				return groupID;
			}

			public void setGroupID(int groupID)
			{
				this.groupID = groupID;
			}

			public int getServerID()
			{
				return serverID;
			}

			public void setServerID(int serverID)
			{
				this.serverID = serverID;
			}

			public Set<Integer> getMaps()
			{
				return maps;
			}

			public void setMaps(Set<Integer> maps)
			{
				this.maps = maps;
			}

			private int groupID;
			private int serverID;
			private Set<Integer> maps;
		}

		public static class LuaChannel extends SimplePacket
		{
			public LuaChannel() { }

			public LuaChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTLuaChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class StrChannel extends SimplePacket
		{
			public StrChannel() { }

			public StrChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTStrChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class StrChannelBroadcast extends SimplePacket
		{
			public StrChannelBroadcast() { }

			public StrChannelBroadcast(Set<Integer> roles, String data)
			{
				this.roles = roles;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTStrChannelBroadcast;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roles = is.popIntegerTreeSet();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushIntegerSet(roles);
				os.pushString(data);
			}

			public Set<Integer> getRoles()
			{
				return roles;
			}

			public void setRoles(Set<Integer> roles)
			{
				this.roles = roles;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private Set<Integer> roles;
			private String data;
		}

		public static class MapRoleReady extends SimplePacket
		{
			public MapRoleReady() { }

			public MapRoleReady(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTMapRoleReady;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class NearByRoleMove extends SimplePacket
		{
			public NearByRoleMove() { }

			public NearByRoleMove(Set<Integer> rids, int id, SBean.Vector3 pos, int speed, 
			                      SBean.Vector3F rotation, SBean.Vector3 target, SBean.TimeTick timeTick)
			{
				this.rids = rids;
				this.id = id;
				this.pos = pos;
				this.speed = speed;
				this.rotation = rotation;
				this.target = target;
				this.timeTick = timeTick;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTNearByRoleMove;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				rids = is.popIntegerTreeSet();
				id = is.popInteger();
				if( pos == null )
					pos = new SBean.Vector3();
				is.pop(pos);
				speed = is.popInteger();
				if( rotation == null )
					rotation = new SBean.Vector3F();
				is.pop(rotation);
				if( target == null )
					target = new SBean.Vector3();
				is.pop(target);
				if( timeTick == null )
					timeTick = new SBean.TimeTick();
				is.pop(timeTick);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushIntegerSet(rids);
				os.pushInteger(id);
				os.push(pos);
				os.pushInteger(speed);
				os.push(rotation);
				os.push(target);
				os.push(timeTick);
			}

			public Set<Integer> getRids()
			{
				return rids;
			}

			public void setRids(Set<Integer> rids)
			{
				this.rids = rids;
			}

			public int getId()
			{
				return id;
			}

			public void setId(int id)
			{
				this.id = id;
			}

			public SBean.Vector3 getPos()
			{
				return pos;
			}

			public void setPos(SBean.Vector3 pos)
			{
				this.pos = pos;
			}

			public int getSpeed()
			{
				return speed;
			}

			public void setSpeed(int speed)
			{
				this.speed = speed;
			}

			public SBean.Vector3F getRotation()
			{
				return rotation;
			}

			public void setRotation(SBean.Vector3F rotation)
			{
				this.rotation = rotation;
			}

			public SBean.Vector3 getTarget()
			{
				return target;
			}

			public void setTarget(SBean.Vector3 target)
			{
				this.target = target;
			}

			public SBean.TimeTick getTimeTick()
			{
				return timeTick;
			}

			public void setTimeTick(SBean.TimeTick timeTick)
			{
				this.timeTick = timeTick;
			}

			private Set<Integer> rids;
			private int id;
			private SBean.Vector3 pos;
			private int speed;
			private SBean.Vector3F rotation;
			private SBean.Vector3 target;
			private SBean.TimeTick timeTick;
		}

		public static class NearByRoleStopMove extends SimplePacket
		{
			public NearByRoleStopMove() { }

			public NearByRoleStopMove(Set<Integer> rids, int id, SBean.Vector3 pos, int speed, 
			                          SBean.TimeTick timeTick)
			{
				this.rids = rids;
				this.id = id;
				this.pos = pos;
				this.speed = speed;
				this.timeTick = timeTick;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTNearByRoleStopMove;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				rids = is.popIntegerTreeSet();
				id = is.popInteger();
				if( pos == null )
					pos = new SBean.Vector3();
				is.pop(pos);
				speed = is.popInteger();
				if( timeTick == null )
					timeTick = new SBean.TimeTick();
				is.pop(timeTick);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushIntegerSet(rids);
				os.pushInteger(id);
				os.push(pos);
				os.pushInteger(speed);
				os.push(timeTick);
			}

			public Set<Integer> getRids()
			{
				return rids;
			}

			public void setRids(Set<Integer> rids)
			{
				this.rids = rids;
			}

			public int getId()
			{
				return id;
			}

			public void setId(int id)
			{
				this.id = id;
			}

			public SBean.Vector3 getPos()
			{
				return pos;
			}

			public void setPos(SBean.Vector3 pos)
			{
				this.pos = pos;
			}

			public int getSpeed()
			{
				return speed;
			}

			public void setSpeed(int speed)
			{
				this.speed = speed;
			}

			public SBean.TimeTick getTimeTick()
			{
				return timeTick;
			}

			public void setTimeTick(SBean.TimeTick timeTick)
			{
				this.timeTick = timeTick;
			}

			private Set<Integer> rids;
			private int id;
			private SBean.Vector3 pos;
			private int speed;
			private SBean.TimeTick timeTick;
		}

		public static class NearByRoleEnter extends SimplePacket
		{
			public NearByRoleEnter() { }

			public NearByRoleEnter(int id, List<SBean.EnterDetail> roles)
			{
				this.id = id;
				this.roles = roles;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTNearByRoleEnter;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				id = is.popInteger();
				roles = is.popList(SBean.EnterDetail.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(id);
				os.pushList(roles);
			}

			public int getId()
			{
				return id;
			}

			public void setId(int id)
			{
				this.id = id;
			}

			public List<SBean.EnterDetail> getRoles()
			{
				return roles;
			}

			public void setRoles(List<SBean.EnterDetail> roles)
			{
				this.roles = roles;
			}

			private int id;
			private List<SBean.EnterDetail> roles;
		}

		public static class NearByRoleLeave extends SimplePacket
		{
			public NearByRoleLeave() { }

			public NearByRoleLeave(int id, Set<Integer> roles, int destory)
			{
				this.id = id;
				this.roles = roles;
				this.destory = destory;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTNearByRoleLeave;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				id = is.popInteger();
				roles = is.popIntegerTreeSet();
				destory = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(id);
				os.pushIntegerSet(roles);
				os.pushInteger(destory);
			}

			public int getId()
			{
				return id;
			}

			public void setId(int id)
			{
				this.id = id;
			}

			public Set<Integer> getRoles()
			{
				return roles;
			}

			public void setRoles(Set<Integer> roles)
			{
				this.roles = roles;
			}

			public int getDestory()
			{
				return destory;
			}

			public void setDestory(int destory)
			{
				this.destory = destory;
			}

			private int id;
			private Set<Integer> roles;
			private int destory;
		}

		public static class SyncCommonMapCopyStart extends SimplePacket
		{
			public SyncCommonMapCopyStart() { }

			public SyncCommonMapCopyStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncCommonMapCopyStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncCommonMapCopyEnd extends SimplePacket
		{
			public SyncCommonMapCopyEnd() { }

			public SyncCommonMapCopyEnd(int mapID, int mapInstance, int score)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.score = score;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncCommonMapCopyEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				score = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(score);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getScore()
			{
				return score;
			}

			public void setScore(int score)
			{
				this.score = score;
			}

			private int mapID;
			private int mapInstance;
			private int score;
		}

		public static class SyncSectMapCopyStart extends SimplePacket
		{
			public SyncSectMapCopyStart() { }

			public SyncSectMapCopyStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSectMapCopyStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncSectMapCopyProgress extends SimplePacket
		{
			public SyncSectMapCopyProgress() { }

			public SyncSectMapCopyProgress(int mapID, int mapInstance, int spawnPointId, int damage, 
			                               int hpLostBP)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.spawnPointId = spawnPointId;
				this.damage = damage;
				this.hpLostBP = hpLostBP;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSectMapCopyProgress;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				spawnPointId = is.popInteger();
				damage = is.popInteger();
				hpLostBP = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(spawnPointId);
				os.pushInteger(damage);
				os.pushInteger(hpLostBP);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getSpawnPointId()
			{
				return spawnPointId;
			}

			public void setSpawnPointId(int spawnPointId)
			{
				this.spawnPointId = spawnPointId;
			}

			public int getDamage()
			{
				return damage;
			}

			public void setDamage(int damage)
			{
				this.damage = damage;
			}

			public int getHpLostBP()
			{
				return hpLostBP;
			}

			public void setHpLostBP(int hpLostBP)
			{
				this.hpLostBP = hpLostBP;
			}

			private int mapID;
			private int mapInstance;
			private int spawnPointId;
			private int damage;
			private int hpLostBP;
		}

		public static class SyncArenaMapCopyStart extends SimplePacket
		{
			public SyncArenaMapCopyStart() { }

			public SyncArenaMapCopyStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncArenaMapCopyStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncArenaMapCopyEnd extends SimplePacket
		{
			public SyncArenaMapCopyEnd() { }

			public SyncArenaMapCopyEnd(int mapID, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, 
			                           SBean.BattleArrayHp defendingSideHp)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.win = win;
				this.attackingSideHp = attackingSideHp;
				this.defendingSideHp = defendingSideHp;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncArenaMapCopyEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				win = is.popInteger();
				if( attackingSideHp == null )
					attackingSideHp = new SBean.BattleArrayHp();
				is.pop(attackingSideHp);
				if( defendingSideHp == null )
					defendingSideHp = new SBean.BattleArrayHp();
				is.pop(defendingSideHp);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(win);
				os.push(attackingSideHp);
				os.push(defendingSideHp);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWin()
			{
				return win;
			}

			public void setWin(int win)
			{
				this.win = win;
			}

			public SBean.BattleArrayHp getAttackingSideHp()
			{
				return attackingSideHp;
			}

			public void setAttackingSideHp(SBean.BattleArrayHp attackingSideHp)
			{
				this.attackingSideHp = attackingSideHp;
			}

			public SBean.BattleArrayHp getDefendingSideHp()
			{
				return defendingSideHp;
			}

			public void setDefendingSideHp(SBean.BattleArrayHp defendingSideHp)
			{
				this.defendingSideHp = defendingSideHp;
			}

			private int mapID;
			private int mapInstance;
			private int win;
			private SBean.BattleArrayHp attackingSideHp;
			private SBean.BattleArrayHp defendingSideHp;
		}

		public static class SyncBWArenaMapCopyStart extends SimplePacket
		{
			public SyncBWArenaMapCopyStart() { }

			public SyncBWArenaMapCopyStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncBWArenaMapCopyStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncBWArenaMapCopyEnd extends SimplePacket
		{
			public SyncBWArenaMapCopyEnd() { }

			public SyncBWArenaMapCopyEnd(int mapID, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, 
			                             SBean.BattleArrayHp defendingSideHp)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.win = win;
				this.attackingSideHp = attackingSideHp;
				this.defendingSideHp = defendingSideHp;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncBWArenaMapCopyEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				win = is.popInteger();
				if( attackingSideHp == null )
					attackingSideHp = new SBean.BattleArrayHp();
				is.pop(attackingSideHp);
				if( defendingSideHp == null )
					defendingSideHp = new SBean.BattleArrayHp();
				is.pop(defendingSideHp);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(win);
				os.push(attackingSideHp);
				os.push(defendingSideHp);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWin()
			{
				return win;
			}

			public void setWin(int win)
			{
				this.win = win;
			}

			public SBean.BattleArrayHp getAttackingSideHp()
			{
				return attackingSideHp;
			}

			public void setAttackingSideHp(SBean.BattleArrayHp attackingSideHp)
			{
				this.attackingSideHp = attackingSideHp;
			}

			public SBean.BattleArrayHp getDefendingSideHp()
			{
				return defendingSideHp;
			}

			public void setDefendingSideHp(SBean.BattleArrayHp defendingSideHp)
			{
				this.defendingSideHp = defendingSideHp;
			}

			private int mapID;
			private int mapInstance;
			private int win;
			private SBean.BattleArrayHp attackingSideHp;
			private SBean.BattleArrayHp defendingSideHp;
		}

		public static class SyncPetLifeMapCopyStart extends SimplePacket
		{
			public SyncPetLifeMapCopyStart() { }

			public SyncPetLifeMapCopyStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncPetLifeMapCopyStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncLocation extends SimplePacket
		{
			public SyncLocation() { }

			public SyncLocation(int roleID, int mapID, int mapInstance, SBean.Location location)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.location = location;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncLocation;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				if( location == null )
					location = new SBean.Location();
				is.pop(location);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.push(location);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.Location getLocation()
			{
				return location;
			}

			public void setLocation(SBean.Location location)
			{
				this.location = location;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private SBean.Location location;
		}

		public static class SyncHp extends SimplePacket
		{
			public SyncHp() { }

			public SyncHp(int roleID, int mapID, int mapInstance, int hp, 
			              int hpMax)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.hp = hp;
				this.hpMax = hpMax;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				hp = is.popInteger();
				hpMax = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(hp);
				os.pushInteger(hpMax);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			public int getHpMax()
			{
				return hpMax;
			}

			public void setHpMax(int hpMax)
			{
				this.hpMax = hpMax;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int hp;
			private int hpMax;
		}

		public static class AddDrops extends SimplePacket
		{
			public AddDrops() { }

			public AddDrops(int roleID, int mapID, int mapInstance, Map<Integer, Integer> drops)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.drops = drops;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTAddDrops;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				drops = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushIntegerIntegerMap(drops);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public Map<Integer, Integer> getDrops()
			{
				return drops;
			}

			public void setDrops(Map<Integer, Integer> drops)
			{
				this.drops = drops;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private Map<Integer, Integer> drops;
		}

		public static class AddKill extends SimplePacket
		{
			public AddKill() { }

			public AddKill(int roleID, int mapID, int mapInstance, int targetType, 
			               int targetID, float weaponAdd, int killRole)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.targetType = targetType;
				this.targetID = targetID;
				this.weaponAdd = weaponAdd;
				this.killRole = killRole;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTAddKill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				targetType = is.popInteger();
				targetID = is.popInteger();
				weaponAdd = is.popFloat();
				killRole = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(targetType);
				os.pushInteger(targetID);
				os.pushFloat(weaponAdd);
				os.pushInteger(killRole);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getTargetType()
			{
				return targetType;
			}

			public void setTargetType(int targetType)
			{
				this.targetType = targetType;
			}

			public int getTargetID()
			{
				return targetID;
			}

			public void setTargetID(int targetID)
			{
				this.targetID = targetID;
			}

			public float getWeaponAdd()
			{
				return weaponAdd;
			}

			public void setWeaponAdd(float weaponAdd)
			{
				this.weaponAdd = weaponAdd;
			}

			public int getKillRole()
			{
				return killRole;
			}

			public void setKillRole(int killRole)
			{
				this.killRole = killRole;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int targetType;
			private int targetID;
			private float weaponAdd;
			private int killRole;
		}

		public static class SyncDurability extends SimplePacket
		{
			public SyncDurability() { }

			public SyncDurability(int roleID, int mapID, int mapInstance, int wid, 
			                      int durability)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.wid = wid;
				this.durability = durability;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncDurability;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				wid = is.popInteger();
				durability = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(wid);
				os.pushInteger(durability);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWid()
			{
				return wid;
			}

			public void setWid(int wid)
			{
				this.wid = wid;
			}

			public int getDurability()
			{
				return durability;
			}

			public void setDurability(int durability)
			{
				this.durability = durability;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int wid;
			private int durability;
		}

		public static class SyncEndMine extends SimplePacket
		{
			public SyncEndMine() { }

			public SyncEndMine(int roleID, int mapID, int mapInstance, int mineId, 
			                   int mineInstance, int ok)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.mineId = mineId;
				this.mineInstance = mineInstance;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncEndMine;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				mineId = is.popInteger();
				mineInstance = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(mineId);
				os.pushInteger(mineInstance);
				os.pushInteger(ok);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getMineId()
			{
				return mineId;
			}

			public void setMineId(int mineId)
			{
				this.mineId = mineId;
			}

			public int getMineInstance()
			{
				return mineInstance;
			}

			public void setMineInstance(int mineInstance)
			{
				this.mineInstance = mineInstance;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int mineId;
			private int mineInstance;
			private int ok;
		}

		public static class AddPKValue extends SimplePacket
		{
			public AddPKValue() { }

			public AddPKValue(int roleID, int mapID, int mapInstance, int value)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.value = value;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTAddPKValue;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				value = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(value);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getValue()
			{
				return value;
			}

			public void setValue(int value)
			{
				this.value = value;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int value;
		}

		public static class SyncWorldMapBossProgress extends SimplePacket
		{
			public SyncWorldMapBossProgress() { }

			public SyncWorldMapBossProgress(int bossID, int hp, String killerName, int killerId)
			{
				this.bossID = bossID;
				this.hp = hp;
				this.killerName = killerName;
				this.killerId = killerId;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncWorldMapBossProgress;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				bossID = is.popInteger();
				hp = is.popInteger();
				killerName = is.popString();
				killerId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(bossID);
				os.pushInteger(hp);
				os.pushString(killerName);
				os.pushInteger(killerId);
			}

			public int getBossID()
			{
				return bossID;
			}

			public void setBossID(int bossID)
			{
				this.bossID = bossID;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			public String getKillerName()
			{
				return killerName;
			}

			public void setKillerName(String killerName)
			{
				this.killerName = killerName;
			}

			public int getKillerId()
			{
				return killerId;
			}

			public void setKillerId(int killerId)
			{
				this.killerId = killerId;
			}

			private int bossID;
			private int hp;
			private String killerName;
			private int killerId;
		}

		public static class SyncCurRideHorse extends SimplePacket
		{
			public SyncCurRideHorse() { }

			public SyncCurRideHorse(int roleID, int mapID, int mapInstance, int hid)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.hid = hid;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncCurRideHorse;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				hid = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(hid);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getHid()
			{
				return hid;
			}

			public void setHid(int hid)
			{
				this.hid = hid;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int hid;
		}

		public static class SyncCarLocation extends SimplePacket
		{
			public SyncCarLocation() { }

			public SyncCarLocation(int roleID, int mapID, int mapInstance, SBean.Location location)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.location = location;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncCarLocation;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				if( location == null )
					location = new SBean.Location();
				is.pop(location);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.push(location);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.Location getLocation()
			{
				return location;
			}

			public void setLocation(SBean.Location location)
			{
				this.location = location;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private SBean.Location location;
		}

		public static class SyncCarHp extends SimplePacket
		{
			public SyncCarHp() { }

			public SyncCarHp(int roleID, int mapID, int mapInstance, int hp)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.hp = hp;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncCarHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				hp = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(hp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int hp;
		}

		public static class UpdateCarDamage extends SimplePacket
		{
			public UpdateCarDamage() { }

			public UpdateCarDamage(int roleID, int mapID, int mapInstance, int damageRole, 
			                       int damage)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.damageRole = damageRole;
				this.damage = damage;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTUpdateCarDamage;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				damageRole = is.popInteger();
				damage = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(damageRole);
				os.pushInteger(damage);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getDamageRole()
			{
				return damageRole;
			}

			public void setDamageRole(int damageRole)
			{
				this.damageRole = damageRole;
			}

			public int getDamage()
			{
				return damage;
			}

			public void setDamage(int damage)
			{
				this.damage = damage;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int damageRole;
			private int damage;
		}

		public static class SyncRoleRobSuccess extends SimplePacket
		{
			public SyncRoleRobSuccess() { }

			public SyncRoleRobSuccess(int roleID, int carID)
			{
				this.roleID = roleID;
				this.carID = carID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncRoleRobSuccess;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				carID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(carID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getCarID()
			{
				return carID;
			}

			public void setCarID(int carID)
			{
				this.carID = carID;
			}

			private int roleID;
			private int carID;
		}

		public static class UpdateRoleCarRobber extends SimplePacket
		{
			public UpdateRoleCarRobber() { }

			public UpdateRoleCarRobber(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTUpdateRoleCarRobber;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class KickRoleFromMap extends SimplePacket
		{
			public KickRoleFromMap() { }

			public KickRoleFromMap(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTKickRoleFromMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class RoleUseItemSkillSuc extends SimplePacket
		{
			public RoleUseItemSkillSuc() { }

			public RoleUseItemSkillSuc(int roleID, int itemID, int ok)
			{
				this.roleID = roleID;
				this.itemID = itemID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTRoleUseItemSkillSuc;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				itemID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(itemID);
				os.pushInteger(ok);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getItemID()
			{
				return itemID;
			}

			public void setItemID(int itemID)
			{
				this.itemID = itemID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int roleID;
			private int itemID;
			private int ok;
		}

		public static class UpdateRoleFightState extends SimplePacket
		{
			public UpdateRoleFightState() { }

			public UpdateRoleFightState(int roleID, int mapID, int mapInstance, byte fightState)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.fightState = fightState;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTUpdateRoleFightState;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				fightState = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushByte(fightState);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public byte getFightState()
			{
				return fightState;
			}

			public void setFightState(byte fightState)
			{
				this.fightState = fightState;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private byte fightState;
		}

		public static class SyncRolePetHp extends SimplePacket
		{
			public SyncRolePetHp() { }

			public SyncRolePetHp(int roleID, int petID, int mapID, int mapInstance, 
			                     SBean.Hp hpState)
			{
				this.roleID = roleID;
				this.petID = petID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.hpState = hpState;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncRolePetHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				petID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				if( hpState == null )
					hpState = new SBean.Hp();
				is.pop(hpState);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(petID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.push(hpState);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPetID()
			{
				return petID;
			}

			public void setPetID(int petID)
			{
				this.petID = petID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.Hp getHpState()
			{
				return hpState;
			}

			public void setHpState(SBean.Hp hpState)
			{
				this.hpState = hpState;
			}

			private int roleID;
			private int petID;
			private int mapID;
			private int mapInstance;
			private SBean.Hp hpState;
		}

		public static class SyncRoleSp extends SimplePacket
		{
			public SyncRoleSp() { }

			public SyncRoleSp(int roleID, int mapID, int mapInstance, int sp)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.sp = sp;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncRoleSp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				sp = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(sp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getSp()
			{
				return sp;
			}

			public void setSp(int sp)
			{
				this.sp = sp;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int sp;
		}

		public static class SyncWorldMapBossRecord extends SimplePacket
		{
			public SyncWorldMapBossRecord() { }

			public SyncWorldMapBossRecord(int bossID, SBean.BossRecord record)
			{
				this.bossID = bossID;
				this.record = record;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncWorldMapBossRecord;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				bossID = is.popInteger();
				if( record == null )
					record = new SBean.BossRecord();
				is.pop(record);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(bossID);
				os.push(record);
			}

			public int getBossID()
			{
				return bossID;
			}

			public void setBossID(int bossID)
			{
				this.bossID = bossID;
			}

			public SBean.BossRecord getRecord()
			{
				return record;
			}

			public void setRecord(SBean.BossRecord record)
			{
				this.record = record;
			}

			private int bossID;
			private SBean.BossRecord record;
		}

		public static class SyncArmorVal extends SimplePacket
		{
			public SyncArmorVal() { }

			public SyncArmorVal(int roleID, int mapID, int mapInstance, int armorVal, 
			                    int armorValMax)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.armorVal = armorVal;
				this.armorValMax = armorValMax;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncArmorVal;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				armorVal = is.popInteger();
				armorValMax = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(armorVal);
				os.pushInteger(armorValMax);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getArmorVal()
			{
				return armorVal;
			}

			public void setArmorVal(int armorVal)
			{
				this.armorVal = armorVal;
			}

			public int getArmorValMax()
			{
				return armorValMax;
			}

			public void setArmorValMax(int armorValMax)
			{
				this.armorValMax = armorValMax;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int armorVal;
			private int armorValMax;
		}

		public static class SyncSectGroupMapCopyStatus extends SimplePacket
		{
			public SyncSectGroupMapCopyStatus() { }

			public SyncSectGroupMapCopyStatus(int mapID, int mapInstance, int progress)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.progress = progress;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSectGroupMapCopyStatus;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				progress = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(progress);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getProgress()
			{
				return progress;
			}

			public void setProgress(int progress)
			{
				this.progress = progress;
			}

			private int mapID;
			private int mapInstance;
			private int progress;
		}

		public static class SyncSectGroupMapCopyResult extends SimplePacket
		{
			public SyncSectGroupMapCopyResult() { }

			public SyncSectGroupMapCopyResult(int mapID, int mapInstance, int progress)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.progress = progress;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSectGroupMapCopyResult;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				progress = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(progress);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getProgress()
			{
				return progress;
			}

			public void setProgress(int progress)
			{
				this.progress = progress;
			}

			private int mapID;
			private int mapInstance;
			private int progress;
		}

		public static class SyncSectGroupMapCopyStart extends SimplePacket
		{
			public SyncSectGroupMapCopyStart() { }

			public SyncSectGroupMapCopyStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSectGroupMapCopyStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncSectGroupMapCopyProgress extends SimplePacket
		{
			public SyncSectGroupMapCopyProgress() { }

			public SyncSectGroupMapCopyProgress(int mapID, int mapInstance, int spawnPointId, int roleId, 
			                                    int monsterId, int damage, int hpLostBP)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.spawnPointId = spawnPointId;
				this.roleId = roleId;
				this.monsterId = monsterId;
				this.damage = damage;
				this.hpLostBP = hpLostBP;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSectGroupMapCopyProgress;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				spawnPointId = is.popInteger();
				roleId = is.popInteger();
				monsterId = is.popInteger();
				damage = is.popInteger();
				hpLostBP = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(spawnPointId);
				os.pushInteger(roleId);
				os.pushInteger(monsterId);
				os.pushInteger(damage);
				os.pushInteger(hpLostBP);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getSpawnPointId()
			{
				return spawnPointId;
			}

			public void setSpawnPointId(int spawnPointId)
			{
				this.spawnPointId = spawnPointId;
			}

			public int getRoleId()
			{
				return roleId;
			}

			public void setRoleId(int roleId)
			{
				this.roleId = roleId;
			}

			public int getMonsterId()
			{
				return monsterId;
			}

			public void setMonsterId(int monsterId)
			{
				this.monsterId = monsterId;
			}

			public int getDamage()
			{
				return damage;
			}

			public void setDamage(int damage)
			{
				this.damage = damage;
			}

			public int getHpLostBP()
			{
				return hpLostBP;
			}

			public void setHpLostBP(int hpLostBP)
			{
				this.hpLostBP = hpLostBP;
			}

			private int mapID;
			private int mapInstance;
			private int spawnPointId;
			private int roleId;
			private int monsterId;
			private int damage;
			private int hpLostBP;
		}

		public static class SyncSectGroupMapCopyAddKill extends SimplePacket
		{
			public SyncSectGroupMapCopyAddKill() { }

			public SyncSectGroupMapCopyAddKill(int mapID, int mapInstance, int monsterId, int spawnPointId)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.monsterId = monsterId;
				this.spawnPointId = spawnPointId;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSectGroupMapCopyAddKill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				monsterId = is.popInteger();
				spawnPointId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(monsterId);
				os.pushInteger(spawnPointId);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getMonsterId()
			{
				return monsterId;
			}

			public void setMonsterId(int monsterId)
			{
				this.monsterId = monsterId;
			}

			public int getSpawnPointId()
			{
				return spawnPointId;
			}

			public void setSpawnPointId(int spawnPointId)
			{
				this.spawnPointId = spawnPointId;
			}

			private int mapID;
			private int mapInstance;
			private int monsterId;
			private int spawnPointId;
		}

		public static class SyncMapFlagCanTake extends SimplePacket
		{
			public SyncMapFlagCanTake() { }

			public SyncMapFlagCanTake(int mapID)
			{
				this.mapID = mapID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncMapFlagCanTake;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			private int mapID;
		}

		public static class SyncWeaponMaster extends SimplePacket
		{
			public SyncWeaponMaster() { }

			public SyncWeaponMaster(int roleID, int mapID, int mapInstance, int weaponID)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.weaponID = weaponID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncWeaponMaster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				weaponID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(weaponID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWeaponID()
			{
				return weaponID;
			}

			public void setWeaponID(int weaponID)
			{
				this.weaponID = weaponID;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int weaponID;
		}

		public static class RolePickUpDrops extends SimplePacket
		{
			public RolePickUpDrops() { }

			public RolePickUpDrops(int roleID, int mapID, int mapInstance, Map<Integer, SBean.DummyGoods> drops)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.drops = drops;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTRolePickUpDrops;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				drops = is.popIntegerTreeMap(SBean.DummyGoods.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushIntegerMap(drops);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public Map<Integer, SBean.DummyGoods> getDrops()
			{
				return drops;
			}

			public void setDrops(Map<Integer, SBean.DummyGoods> drops)
			{
				this.drops = drops;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private Map<Integer, SBean.DummyGoods> drops;
		}

		public static class SyncSuperMonster extends SimplePacket
		{
			public SyncSuperMonster() { }

			public SyncSuperMonster(SBean.ActivityEntity monster, byte add)
			{
				this.monster = monster;
				this.add = add;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSuperMonster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( monster == null )
					monster = new SBean.ActivityEntity();
				is.pop(monster);
				add = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(monster);
				os.pushByte(add);
			}

			public SBean.ActivityEntity getMonster()
			{
				return monster;
			}

			public void setMonster(SBean.ActivityEntity monster)
			{
				this.monster = monster;
			}

			public byte getAdd()
			{
				return add;
			}

			public void setAdd(byte add)
			{
				this.add = add;
			}

			private SBean.ActivityEntity monster;
			private byte add;
		}

		public static class SyncWorldMineral extends SimplePacket
		{
			public SyncWorldMineral() { }

			public SyncWorldMineral(SBean.ActivityEntity mineral, byte add)
			{
				this.mineral = mineral;
				this.add = add;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncWorldMineral;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( mineral == null )
					mineral = new SBean.ActivityEntity();
				is.pop(mineral);
				add = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(mineral);
				os.pushByte(add);
			}

			public SBean.ActivityEntity getMineral()
			{
				return mineral;
			}

			public void setMineral(SBean.ActivityEntity mineral)
			{
				this.mineral = mineral;
			}

			public byte getAdd()
			{
				return add;
			}

			public void setAdd(byte add)
			{
				this.add = add;
			}

			private SBean.ActivityEntity mineral;
			private byte add;
		}

		public static class SyncMarriageParadeEnd extends SimplePacket
		{
			public SyncMarriageParadeEnd() { }

			public SyncMarriageParadeEnd(int manID, int womanID)
			{
				this.manID = manID;
				this.womanID = womanID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncMarriageParadeEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				manID = is.popInteger();
				womanID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(manID);
				os.pushInteger(womanID);
			}

			public int getManID()
			{
				return manID;
			}

			public void setManID(int manID)
			{
				this.manID = manID;
			}

			public int getWomanID()
			{
				return womanID;
			}

			public void setWomanID(int womanID)
			{
				this.womanID = womanID;
			}

			private int manID;
			private int womanID;
		}

		public static class RolePickUpRareDrops extends SimplePacket
		{
			public RolePickUpRareDrops() { }

			public RolePickUpRareDrops(int roleID, int mapID, int mapInstance, int dropId, 
			                           SBean.DummyGoods drop, int monsterId)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.dropId = dropId;
				this.drop = drop;
				this.monsterId = monsterId;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTRolePickUpRareDrops;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				dropId = is.popInteger();
				if( drop == null )
					drop = new SBean.DummyGoods();
				is.pop(drop);
				monsterId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(dropId);
				os.push(drop);
				os.pushInteger(monsterId);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getDropId()
			{
				return dropId;
			}

			public void setDropId(int dropId)
			{
				this.dropId = dropId;
			}

			public SBean.DummyGoods getDrop()
			{
				return drop;
			}

			public void setDrop(SBean.DummyGoods drop)
			{
				this.drop = drop;
			}

			public int getMonsterId()
			{
				return monsterId;
			}

			public void setMonsterId(int monsterId)
			{
				this.monsterId = monsterId;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int dropId;
			private SBean.DummyGoods drop;
			private int monsterId;
		}

		public static class SyncWorldBossDamageRoles extends SimplePacket
		{
			public SyncWorldBossDamageRoles() { }

			public SyncWorldBossDamageRoles(int bossID, int killer, Map<Integer, Integer> damageRoles)
			{
				this.bossID = bossID;
				this.killer = killer;
				this.damageRoles = damageRoles;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncWorldBossDamageRoles;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				bossID = is.popInteger();
				killer = is.popInteger();
				damageRoles = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(bossID);
				os.pushInteger(killer);
				os.pushIntegerIntegerMap(damageRoles);
			}

			public int getBossID()
			{
				return bossID;
			}

			public void setBossID(int bossID)
			{
				this.bossID = bossID;
			}

			public int getKiller()
			{
				return killer;
			}

			public void setKiller(int killer)
			{
				this.killer = killer;
			}

			public Map<Integer, Integer> getDamageRoles()
			{
				return damageRoles;
			}

			public void setDamageRoles(Map<Integer, Integer> damageRoles)
			{
				this.damageRoles = damageRoles;
			}

			private int bossID;
			private int killer;
			private Map<Integer, Integer> damageRoles;
		}

		public static class SyncSteleRemainTimes extends SimplePacket
		{
			public SyncSteleRemainTimes() { }

			public SyncSteleRemainTimes(int steleType, int index, int remainTimes)
			{
				this.steleType = steleType;
				this.index = index;
				this.remainTimes = remainTimes;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncSteleRemainTimes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				steleType = is.popInteger();
				index = is.popInteger();
				remainTimes = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(steleType);
				os.pushInteger(index);
				os.pushInteger(remainTimes);
			}

			public int getSteleType()
			{
				return steleType;
			}

			public void setSteleType(int steleType)
			{
				this.steleType = steleType;
			}

			public int getIndex()
			{
				return index;
			}

			public void setIndex(int index)
			{
				this.index = index;
			}

			public int getRemainTimes()
			{
				return remainTimes;
			}

			public void setRemainTimes(int remainTimes)
			{
				this.remainTimes = remainTimes;
			}

			private int steleType;
			private int index;
			private int remainTimes;
		}

		public static class SyncRoleAddSteleCard extends SimplePacket
		{
			public SyncRoleAddSteleCard() { }

			public SyncRoleAddSteleCard(int roleID, int addCards, int addType)
			{
				this.roleID = roleID;
				this.addCards = addCards;
				this.addType = addType;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncRoleAddSteleCard;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				addCards = is.popInteger();
				addType = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(addCards);
				os.pushInteger(addType);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getAddCards()
			{
				return addCards;
			}

			public void setAddCards(int addCards)
			{
				this.addCards = addCards;
			}

			public int getAddType()
			{
				return addType;
			}

			public void setAddType(int addType)
			{
				this.addType = addType;
			}

			private int roleID;
			private int addCards;
			private int addType;
		}

		public static class SyncRefreshSteleMonster extends SimplePacket
		{
			public SyncRefreshSteleMonster() { }

			public SyncRefreshSteleMonster(int roleID, String roleName, int mapID, int mapLine, 
			                               int steleType, int index, int monsterID)
			{
				this.roleID = roleID;
				this.roleName = roleName;
				this.mapID = mapID;
				this.mapLine = mapLine;
				this.steleType = steleType;
				this.index = index;
				this.monsterID = monsterID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncRefreshSteleMonster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				roleName = is.popString();
				mapID = is.popInteger();
				mapLine = is.popInteger();
				steleType = is.popInteger();
				index = is.popInteger();
				monsterID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(roleName);
				os.pushInteger(mapID);
				os.pushInteger(mapLine);
				os.pushInteger(steleType);
				os.pushInteger(index);
				os.pushInteger(monsterID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getRoleName()
			{
				return roleName;
			}

			public void setRoleName(String roleName)
			{
				this.roleName = roleName;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapLine()
			{
				return mapLine;
			}

			public void setMapLine(int mapLine)
			{
				this.mapLine = mapLine;
			}

			public int getSteleType()
			{
				return steleType;
			}

			public void setSteleType(int steleType)
			{
				this.steleType = steleType;
			}

			public int getIndex()
			{
				return index;
			}

			public void setIndex(int index)
			{
				this.index = index;
			}

			public int getMonsterID()
			{
				return monsterID;
			}

			public void setMonsterID(int monsterID)
			{
				this.monsterID = monsterID;
			}

			private int roleID;
			private String roleName;
			private int mapID;
			private int mapLine;
			private int steleType;
			private int index;
			private int monsterID;
		}

		public static class SyncEmergencyMapStart extends SimplePacket
		{
			public SyncEmergencyMapStart() { }

			public SyncEmergencyMapStart(int mapID, int instanceID)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncEmergencyMapStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			private int mapID;
			private int instanceID;
		}

		public static class SyncEmergencyMapKillMonster extends SimplePacket
		{
			public SyncEmergencyMapKillMonster() { }

			public SyncEmergencyMapKillMonster(Set<Integer> roles, int monsterID)
			{
				this.roles = roles;
				this.monsterID = monsterID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncEmergencyMapKillMonster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roles = is.popIntegerTreeSet();
				monsterID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushIntegerSet(roles);
				os.pushInteger(monsterID);
			}

			public Set<Integer> getRoles()
			{
				return roles;
			}

			public void setRoles(Set<Integer> roles)
			{
				this.roles = roles;
			}

			public int getMonsterID()
			{
				return monsterID;
			}

			public void setMonsterID(int monsterID)
			{
				this.monsterID = monsterID;
			}

			private Set<Integer> roles;
			private int monsterID;
		}

		public static class SyncEmergencyMapEnd extends SimplePacket
		{
			public SyncEmergencyMapEnd() { }

			public SyncEmergencyMapEnd(int mapID, int instanceID)
			{
				this.mapID = mapID;
				this.instanceID = instanceID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncEmergencyMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				instanceID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(instanceID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getInstanceID()
			{
				return instanceID;
			}

			public void setInstanceID(int instanceID)
			{
				this.instanceID = instanceID;
			}

			private int mapID;
			private int instanceID;
		}

		public static class SyncFightNpcMapStart extends SimplePacket
		{
			public SyncFightNpcMapStart() { }

			public SyncFightNpcMapStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncFightNpcMapStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncFightNpcMapEnd extends SimplePacket
		{
			public SyncFightNpcMapEnd() { }

			public SyncFightNpcMapEnd(int mapID, int mapInstance, byte win)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.win = win;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncFightNpcMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				win = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushByte(win);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public byte getWin()
			{
				return win;
			}

			public void setWin(byte win)
			{
				this.win = win;
			}

			private int mapID;
			private int mapInstance;
			private byte win;
		}

		public static class SyncTowerDefenceMapStart extends SimplePacket
		{
			public SyncTowerDefenceMapStart() { }

			public SyncTowerDefenceMapStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncTowerDefenceMapStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncTowerDefenceMapEnd extends SimplePacket
		{
			public SyncTowerDefenceMapEnd() { }

			public SyncTowerDefenceMapEnd(int mapID, int mapInstance, int count)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.count = count;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncTowerDefenceMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				count = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(count);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getCount()
			{
				return count;
			}

			public void setCount(int count)
			{
				this.count = count;
			}

			private int mapID;
			private int mapInstance;
			private int count;
		}

		public static class SyncTowerDefenceSpawnCount extends SimplePacket
		{
			public SyncTowerDefenceSpawnCount() { }

			public SyncTowerDefenceSpawnCount(int mapID, int mapInstance, int count)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.count = count;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncTowerDefenceSpawnCount;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				count = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(count);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getCount()
			{
				return count;
			}

			public void setCount(int count)
			{
				this.count = count;
			}

			private int mapID;
			private int mapInstance;
			private int count;
		}

		public static class SyncTowerDefenceScore extends SimplePacket
		{
			public SyncTowerDefenceScore() { }

			public SyncTowerDefenceScore(int mapID, int mapInstance, int roleID, int monsterID)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.roleID = roleID;
				this.monsterID = monsterID;
			}

			@Override
			public int getType()
			{
				return Packet.eM2SPKTSyncTowerDefenceScore;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				roleID = is.popInteger();
				monsterID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(roleID);
				os.pushInteger(monsterID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMonsterID()
			{
				return monsterID;
			}

			public void setMonsterID(int monsterID)
			{
				this.monsterID = monsterID;
			}

			private int mapID;
			private int mapInstance;
			private int roleID;
			private int monsterID;
		}

	}

	// server to auction
	public static class S2Auction
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int serverId, Set<Integer> zones)
			{
				this.serverId = serverId;
				this.zones = zones;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				serverId = is.popInteger();
				zones = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(serverId);
				os.pushIntegerSet(zones);
			}

			public int getServerId()
			{
				return serverId;
			}

			public void setServerId(int serverId)
			{
				this.serverId = serverId;
			}

			public Set<Integer> getZones()
			{
				return zones;
			}

			public void setZones(Set<Integer> zones)
			{
				this.zones = zones;
			}

			private int serverId;
			private Set<Integer> zones;
		}

		public static class ReportTimeOffset extends SimplePacket
		{
			public ReportTimeOffset() { }

			public ReportTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTReportTimeOffset;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				timeOffset = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(timeOffset);
			}

			public int getTimeOffset()
			{
				return timeOffset;
			}

			public void setTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			private int timeOffset;
		}

		public static class PutOnItemReq extends SimplePacket
		{
			public PutOnItemReq() { }

			public PutOnItemReq(int tagID, int roleID, SBean.DBConsignItems items)
			{
				this.tagID = tagID;
				this.roleID = roleID;
				this.items = items;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTPutOnItemReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
				if( items == null )
					items = new SBean.DBConsignItems();
				is.pop(items);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
				os.push(items);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.DBConsignItems getItems()
			{
				return items;
			}

			public void setItems(SBean.DBConsignItems items)
			{
				this.items = items;
			}

			private int tagID;
			private int roleID;
			private SBean.DBConsignItems items;
		}

		public static class TimeOutPutOffItemsRes extends SimplePacket
		{
			public TimeOutPutOffItemsRes() { }

			public TimeOutPutOffItemsRes(int tagID, int errCode)
			{
				this.tagID = tagID;
				this.errCode = errCode;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTTimeOutPutOffItemsRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				errCode = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(errCode);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getErrCode()
			{
				return errCode;
			}

			public void setErrCode(int errCode)
			{
				this.errCode = errCode;
			}

			private int tagID;
			private int errCode;
		}

		public static class PutOffItemsReq extends SimplePacket
		{
			public PutOffItemsReq() { }

			public PutOffItemsReq(int tagID, int roleID, int cid)
			{
				this.tagID = tagID;
				this.roleID = roleID;
				this.cid = cid;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTPutOffItemsReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
				cid = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
				os.pushInteger(cid);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getCid()
			{
				return cid;
			}

			public void setCid(int cid)
			{
				this.cid = cid;
			}

			private int tagID;
			private int roleID;
			private int cid;
		}

		public static class BuyItemsReq extends SimplePacket
		{
			public BuyItemsReq() { }

			public BuyItemsReq(int tagID, int sellerID, int cid, int price)
			{
				this.tagID = tagID;
				this.sellerID = sellerID;
				this.cid = cid;
				this.price = price;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTBuyItemsReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				sellerID = is.popInteger();
				cid = is.popInteger();
				price = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(sellerID);
				os.pushInteger(cid);
				os.pushInteger(price);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getSellerID()
			{
				return sellerID;
			}

			public void setSellerID(int sellerID)
			{
				this.sellerID = sellerID;
			}

			public int getCid()
			{
				return cid;
			}

			public void setCid(int cid)
			{
				this.cid = cid;
			}

			public int getPrice()
			{
				return price;
			}

			public void setPrice(int price)
			{
				this.price = price;
			}

			private int tagID;
			private int sellerID;
			private int cid;
			private int price;
		}

		public static class CheckCanBuyRes extends SimplePacket
		{
			public CheckCanBuyRes() { }

			public CheckCanBuyRes(int tagID, int errCode)
			{
				this.tagID = tagID;
				this.errCode = errCode;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTCheckCanBuyRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				errCode = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(errCode);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getErrCode()
			{
				return errCode;
			}

			public void setErrCode(int errCode)
			{
				this.errCode = errCode;
			}

			private int tagID;
			private int errCode;
		}

		public static class AuctionItemsSyncReq extends SimplePacket
		{
			public AuctionItemsSyncReq() { }

			public AuctionItemsSyncReq(int tagID, int itemType, int classType, int rank, 
			                           int level, int order, int page, String name)
			{
				this.tagID = tagID;
				this.itemType = itemType;
				this.classType = classType;
				this.rank = rank;
				this.level = level;
				this.order = order;
				this.page = page;
				this.name = name;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTAuctionItemsSyncReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				itemType = is.popInteger();
				classType = is.popInteger();
				rank = is.popInteger();
				level = is.popInteger();
				order = is.popInteger();
				page = is.popInteger();
				name = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(itemType);
				os.pushInteger(classType);
				os.pushInteger(rank);
				os.pushInteger(level);
				os.pushInteger(order);
				os.pushInteger(page);
				os.pushString(name);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getItemType()
			{
				return itemType;
			}

			public void setItemType(int itemType)
			{
				this.itemType = itemType;
			}

			public int getClassType()
			{
				return classType;
			}

			public void setClassType(int classType)
			{
				this.classType = classType;
			}

			public int getRank()
			{
				return rank;
			}

			public void setRank(int rank)
			{
				this.rank = rank;
			}

			public int getLevel()
			{
				return level;
			}

			public void setLevel(int level)
			{
				this.level = level;
			}

			public int getOrder()
			{
				return order;
			}

			public void setOrder(int order)
			{
				this.order = order;
			}

			public int getPage()
			{
				return page;
			}

			public void setPage(int page)
			{
				this.page = page;
			}

			public String getName()
			{
				return name;
			}

			public void setName(String name)
			{
				this.name = name;
			}

			private int tagID;
			private int itemType;
			private int classType;
			private int rank;
			private int level;
			private int order;
			private int page;
			private String name;
		}

		public static class SelfItemsSyncReq extends SimplePacket
		{
			public SelfItemsSyncReq() { }

			public SelfItemsSyncReq(int tagID, int roleID)
			{
				this.tagID = tagID;
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTSelfItemsSyncReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int tagID;
			private int roleID;
		}

		public static class ItemPricesSyncReq extends SimplePacket
		{
			public ItemPricesSyncReq() { }

			public ItemPricesSyncReq(int tagID, int itemID)
			{
				this.tagID = tagID;
				this.itemID = itemID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTItemPricesSyncReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				itemID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(itemID);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getItemID()
			{
				return itemID;
			}

			public void setItemID(int itemID)
			{
				this.itemID = itemID;
			}

			private int tagID;
			private int itemID;
		}

		public static class UpdateGroupBuyGoods extends SimplePacket
		{
			public UpdateGroupBuyGoods() { }

			public UpdateGroupBuyGoods(int activityID, int endTime, int gid, int count)
			{
				this.activityID = activityID;
				this.endTime = endTime;
				this.gid = gid;
				this.count = count;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTUpdateGroupBuyGoods;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				activityID = is.popInteger();
				endTime = is.popInteger();
				gid = is.popInteger();
				count = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(activityID);
				os.pushInteger(endTime);
				os.pushInteger(gid);
				os.pushInteger(count);
			}

			public int getActivityID()
			{
				return activityID;
			}

			public void setActivityID(int activityID)
			{
				this.activityID = activityID;
			}

			public int getEndTime()
			{
				return endTime;
			}

			public void setEndTime(int endTime)
			{
				this.endTime = endTime;
			}

			public int getGid()
			{
				return gid;
			}

			public void setGid(int gid)
			{
				this.gid = gid;
			}

			public int getCount()
			{
				return count;
			}

			public void setCount(int count)
			{
				this.count = count;
			}

			private int activityID;
			private int endTime;
			private int gid;
			private int count;
		}

		public static class SyncGroupBuyGoods extends SimplePacket
		{
			public SyncGroupBuyGoods() { }

			public SyncGroupBuyGoods(int activityID, int endTime, Map<Integer, Integer> log)
			{
				this.activityID = activityID;
				this.endTime = endTime;
				this.log = log;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AuctionPKTSyncGroupBuyGoods;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				activityID = is.popInteger();
				endTime = is.popInteger();
				log = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(activityID);
				os.pushInteger(endTime);
				os.pushIntegerIntegerMap(log);
			}

			public int getActivityID()
			{
				return activityID;
			}

			public void setActivityID(int activityID)
			{
				this.activityID = activityID;
			}

			public int getEndTime()
			{
				return endTime;
			}

			public void setEndTime(int endTime)
			{
				this.endTime = endTime;
			}

			public Map<Integer, Integer> getLog()
			{
				return log;
			}

			public void setLog(Map<Integer, Integer> log)
			{
				this.log = log;
			}

			private int activityID;
			private int endTime;
			private Map<Integer, Integer> log;
		}

	}

	// auction to server
	public static class Auction2S
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class AdjustTimeOffset extends SimplePacket
		{
			public AdjustTimeOffset() { }

			public AdjustTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTAdjustTimeOffset;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				timeOffset = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(timeOffset);
			}

			public int getTimeOffset()
			{
				return timeOffset;
			}

			public void setTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			private int timeOffset;
		}

		public static class PutOnItemRes extends SimplePacket
		{
			public PutOnItemRes() { }

			public PutOnItemRes(int tagID, int cid)
			{
				this.tagID = tagID;
				this.cid = cid;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTPutOnItemRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				cid = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(cid);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getCid()
			{
				return cid;
			}

			public void setCid(int cid)
			{
				this.cid = cid;
			}

			private int tagID;
			private int cid;
		}

		public static class TimeOutPutOffItemsReq extends SimplePacket
		{
			public TimeOutPutOffItemsReq() { }

			public TimeOutPutOffItemsReq(int tagID, int roleID, int cid, SBean.DBConsignItems items)
			{
				this.tagID = tagID;
				this.roleID = roleID;
				this.cid = cid;
				this.items = items;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTTimeOutPutOffItemsReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
				cid = is.popInteger();
				if( items == null )
					items = new SBean.DBConsignItems();
				is.pop(items);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
				os.pushInteger(cid);
				os.push(items);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getCid()
			{
				return cid;
			}

			public void setCid(int cid)
			{
				this.cid = cid;
			}

			public SBean.DBConsignItems getItems()
			{
				return items;
			}

			public void setItems(SBean.DBConsignItems items)
			{
				this.items = items;
			}

			private int tagID;
			private int roleID;
			private int cid;
			private SBean.DBConsignItems items;
		}

		public static class PutOffItemsRes extends SimplePacket
		{
			public PutOffItemsRes() { }

			public PutOffItemsRes(int tagID, int errCode, SBean.DBConsignItems items)
			{
				this.tagID = tagID;
				this.errCode = errCode;
				this.items = items;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTPutOffItemsRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				errCode = is.popInteger();
				items = is.popNullable(SBean.DBConsignItems.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(errCode);
				os.pushNullable(items);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getErrCode()
			{
				return errCode;
			}

			public void setErrCode(int errCode)
			{
				this.errCode = errCode;
			}

			public SBean.DBConsignItems getItems()
			{
				return items;
			}

			public void setItems(SBean.DBConsignItems items)
			{
				this.items = items;
			}

			private int tagID;
			private int errCode;
			private SBean.DBConsignItems items;
		}

		public static class BuyItemsRes extends SimplePacket
		{
			public BuyItemsRes() { }

			public BuyItemsRes(int tagID, SBean.DBConsignItems items)
			{
				this.tagID = tagID;
				this.items = items;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTBuyItemsRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				items = is.popNullable(SBean.DBConsignItems.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushNullable(items);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public SBean.DBConsignItems getItems()
			{
				return items;
			}

			public void setItems(SBean.DBConsignItems items)
			{
				this.items = items;
			}

			private int tagID;
			private SBean.DBConsignItems items;
		}

		public static class CheckCanBuyReq extends SimplePacket
		{
			public CheckCanBuyReq() { }

			public CheckCanBuyReq(int tagID, int sellerID, int cid, SBean.DBConsignItems items)
			{
				this.tagID = tagID;
				this.sellerID = sellerID;
				this.cid = cid;
				this.items = items;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTCheckCanBuyReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				sellerID = is.popInteger();
				cid = is.popInteger();
				if( items == null )
					items = new SBean.DBConsignItems();
				is.pop(items);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(sellerID);
				os.pushInteger(cid);
				os.push(items);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getSellerID()
			{
				return sellerID;
			}

			public void setSellerID(int sellerID)
			{
				this.sellerID = sellerID;
			}

			public int getCid()
			{
				return cid;
			}

			public void setCid(int cid)
			{
				this.cid = cid;
			}

			public SBean.DBConsignItems getItems()
			{
				return items;
			}

			public void setItems(SBean.DBConsignItems items)
			{
				this.items = items;
			}

			private int tagID;
			private int sellerID;
			private int cid;
			private SBean.DBConsignItems items;
		}

		public static class AuctionItemsSyncRes extends SimplePacket
		{
			public AuctionItemsSyncRes() { }

			public AuctionItemsSyncRes(int tagID, List<SBean.DetailConsignItems> items, int lastPage)
			{
				this.tagID = tagID;
				this.items = items;
				this.lastPage = lastPage;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTAuctionItemsSyncRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				items = is.popList(SBean.DetailConsignItems.class);
				lastPage = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushList(items);
				os.pushInteger(lastPage);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public List<SBean.DetailConsignItems> getItems()
			{
				return items;
			}

			public void setItems(List<SBean.DetailConsignItems> items)
			{
				this.items = items;
			}

			public int getLastPage()
			{
				return lastPage;
			}

			public void setLastPage(int lastPage)
			{
				this.lastPage = lastPage;
			}

			private int tagID;
			private List<SBean.DetailConsignItems> items;
			private int lastPage;
		}

		public static class SelfItemsSyncRes extends SimplePacket
		{
			public SelfItemsSyncRes() { }

			public SelfItemsSyncRes(int tagID, Map<Integer, SBean.DBConsignItems> items)
			{
				this.tagID = tagID;
				this.items = items;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTSelfItemsSyncRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				items = is.popIntegerTreeMap(SBean.DBConsignItems.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushIntegerMap(items);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public Map<Integer, SBean.DBConsignItems> getItems()
			{
				return items;
			}

			public void setItems(Map<Integer, SBean.DBConsignItems> items)
			{
				this.items = items;
			}

			private int tagID;
			private Map<Integer, SBean.DBConsignItems> items;
		}

		public static class ItemPricesSyncRes extends SimplePacket
		{
			public ItemPricesSyncRes() { }

			public ItemPricesSyncRes(int tagID, List<SBean.DBConsignItems> items)
			{
				this.tagID = tagID;
				this.items = items;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTItemPricesSyncRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				items = is.popList(SBean.DBConsignItems.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushList(items);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public List<SBean.DBConsignItems> getItems()
			{
				return items;
			}

			public void setItems(List<SBean.DBConsignItems> items)
			{
				this.items = items;
			}

			private int tagID;
			private List<SBean.DBConsignItems> items;
		}

		public static class SyncGroupBuyLog extends SimplePacket
		{
			public SyncGroupBuyLog() { }

			public SyncGroupBuyLog(int activityID, Map<Integer, Integer> log)
			{
				this.activityID = activityID;
				this.log = log;
			}

			@Override
			public int getType()
			{
				return Packet.eAuction2SPKTSyncGroupBuyLog;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				activityID = is.popInteger();
				log = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(activityID);
				os.pushIntegerIntegerMap(log);
			}

			public int getActivityID()
			{
				return activityID;
			}

			public void setActivityID(int activityID)
			{
				this.activityID = activityID;
			}

			public Map<Integer, Integer> getLog()
			{
				return log;
			}

			public void setLog(Map<Integer, Integer> log)
			{
				this.log = log;
			}

			private int activityID;
			private Map<Integer, Integer> log;
		}

	}

	// server to auth
	public static class S2AU
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AUPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int serverId, Set<Integer> zones)
			{
				this.serverId = serverId;
				this.zones = zones;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AUPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				serverId = is.popInteger();
				zones = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(serverId);
				os.pushIntegerSet(zones);
			}

			public int getServerId()
			{
				return serverId;
			}

			public void setServerId(int serverId)
			{
				this.serverId = serverId;
			}

			public Set<Integer> getZones()
			{
				return zones;
			}

			public void setZones(Set<Integer> zones)
			{
				this.zones = zones;
			}

			private int serverId;
			private Set<Integer> zones;
		}

		public static class PayRes extends SimplePacket
		{
			public PayRes() { }

			public PayRes(int xid, int ok)
			{
				this.xid = xid;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eS2AUPKTPayRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				xid = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(xid);
				os.pushInteger(ok);
			}

			public int getXid()
			{
				return xid;
			}

			public void setXid(int xid)
			{
				this.xid = xid;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int xid;
			private int ok;
		}

	}

	// auth to server
	public static class AU2S
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eAU2SPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class PayReq extends SimplePacket
		{
			public PayReq() { }

			public PayReq(int xid, String orderId, String channel, String uid, 
			              int gsid, int roleId, String goodsId, int payLevel, 
			              String payext)
			{
				this.xid = xid;
				this.orderId = orderId;
				this.channel = channel;
				this.uid = uid;
				this.gsid = gsid;
				this.roleId = roleId;
				this.goodsId = goodsId;
				this.payLevel = payLevel;
				this.payext = payext;
			}

			@Override
			public int getType()
			{
				return Packet.eAU2SPKTPayReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				xid = is.popInteger();
				orderId = is.popString();
				channel = is.popString();
				uid = is.popString();
				gsid = is.popInteger();
				roleId = is.popInteger();
				goodsId = is.popString();
				payLevel = is.popInteger();
				payext = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(xid);
				os.pushString(orderId);
				os.pushString(channel);
				os.pushString(uid);
				os.pushInteger(gsid);
				os.pushInteger(roleId);
				os.pushString(goodsId);
				os.pushInteger(payLevel);
				os.pushString(payext);
			}

			public int getXid()
			{
				return xid;
			}

			public void setXid(int xid)
			{
				this.xid = xid;
			}

			public String getOrderId()
			{
				return orderId;
			}

			public void setOrderId(String orderId)
			{
				this.orderId = orderId;
			}

			public String getChannel()
			{
				return channel;
			}

			public void setChannel(String channel)
			{
				this.channel = channel;
			}

			public String getUid()
			{
				return uid;
			}

			public void setUid(String uid)
			{
				this.uid = uid;
			}

			public int getGsid()
			{
				return gsid;
			}

			public void setGsid(int gsid)
			{
				this.gsid = gsid;
			}

			public int getRoleId()
			{
				return roleId;
			}

			public void setRoleId(int roleId)
			{
				this.roleId = roleId;
			}

			public String getGoodsId()
			{
				return goodsId;
			}

			public void setGoodsId(String goodsId)
			{
				this.goodsId = goodsId;
			}

			public int getPayLevel()
			{
				return payLevel;
			}

			public void setPayLevel(int payLevel)
			{
				this.payLevel = payLevel;
			}

			public String getPayext()
			{
				return payext;
			}

			public void setPayext(String payext)
			{
				this.payext = payext;
			}

			private int xid;
			private String orderId;
			private String channel;
			private String uid;
			private int gsid;
			private int roleId;
			private String goodsId;
			private int payLevel;
			private String payext;
		}

	}

	// gs to global ms
	public static class S2GM
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int serverId, Set<Integer> zones)
			{
				this.serverId = serverId;
				this.zones = zones;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				serverId = is.popInteger();
				zones = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(serverId);
				os.pushIntegerSet(zones);
			}

			public int getServerId()
			{
				return serverId;
			}

			public void setServerId(int serverId)
			{
				this.serverId = serverId;
			}

			public Set<Integer> getZones()
			{
				return zones;
			}

			public void setZones(Set<Integer> zones)
			{
				this.zones = zones;
			}

			private int serverId;
			private Set<Integer> zones;
		}

		public static class ReportTimeOffset extends SimplePacket
		{
			public ReportTimeOffset() { }

			public ReportTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTReportTimeOffset;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				timeOffset = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(timeOffset);
			}

			public int getTimeOffset()
			{
				return timeOffset;
			}

			public void setTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			private int timeOffset;
		}

		public static class LuaChannel extends SimplePacket
		{
			public LuaChannel() { }

			public LuaChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTLuaChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class StrChannel extends SimplePacket
		{
			public StrChannel() { }

			public StrChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTStrChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class EnterMap extends SimplePacket
		{
			public EnterMap() { }

			public EnterMap(SBean.FightRole role, int mapId, int mapInstance, SBean.Location location, 
			                int hp, int sp, int armorVal, Map<Integer, SBean.DBBuff> buffs, 
			                Map<Integer, SBean.FightPet> pets, List<Integer> petSeq, SBean.PetHost pethost, SBean.PKInfo pkInfo, 
			                SBean.Team team, int curRideHorse, SBean.MulRoleInfo mulRoleInfo, SBean.DBAlterState alterState, 
			                byte mainSpawnPos, int dayFailedStreak, int vipLevel, int curWizardPet, 
			                byte canTakeDrop)
			{
				this.role = role;
				this.mapId = mapId;
				this.mapInstance = mapInstance;
				this.location = location;
				this.hp = hp;
				this.sp = sp;
				this.armorVal = armorVal;
				this.buffs = buffs;
				this.pets = pets;
				this.petSeq = petSeq;
				this.pethost = pethost;
				this.pkInfo = pkInfo;
				this.team = team;
				this.curRideHorse = curRideHorse;
				this.mulRoleInfo = mulRoleInfo;
				this.alterState = alterState;
				this.mainSpawnPos = mainSpawnPos;
				this.dayFailedStreak = dayFailedStreak;
				this.vipLevel = vipLevel;
				this.curWizardPet = curWizardPet;
				this.canTakeDrop = canTakeDrop;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTEnterMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( role == null )
					role = new SBean.FightRole();
				is.pop(role);
				mapId = is.popInteger();
				mapInstance = is.popInteger();
				if( location == null )
					location = new SBean.Location();
				is.pop(location);
				hp = is.popInteger();
				sp = is.popInteger();
				armorVal = is.popInteger();
				buffs = is.popIntegerTreeMap(SBean.DBBuff.class);
				pets = is.popIntegerTreeMap(SBean.FightPet.class);
				petSeq = is.popIntegerList();
				pethost = is.popNullable(SBean.PetHost.class);
				pkInfo = is.popNullable(SBean.PKInfo.class);
				if( team == null )
					team = new SBean.Team();
				is.pop(team);
				curRideHorse = is.popInteger();
				if( mulRoleInfo == null )
					mulRoleInfo = new SBean.MulRoleInfo();
				is.pop(mulRoleInfo);
				if( alterState == null )
					alterState = new SBean.DBAlterState();
				is.pop(alterState);
				mainSpawnPos = is.popByte();
				dayFailedStreak = is.popInteger();
				vipLevel = is.popInteger();
				curWizardPet = is.popInteger();
				canTakeDrop = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(role);
				os.pushInteger(mapId);
				os.pushInteger(mapInstance);
				os.push(location);
				os.pushInteger(hp);
				os.pushInteger(sp);
				os.pushInteger(armorVal);
				os.pushIntegerMap(buffs);
				os.pushIntegerMap(pets);
				os.pushIntegerList(petSeq);
				os.pushNullable(pethost);
				os.pushNullable(pkInfo);
				os.push(team);
				os.pushInteger(curRideHorse);
				os.push(mulRoleInfo);
				os.push(alterState);
				os.pushByte(mainSpawnPos);
				os.pushInteger(dayFailedStreak);
				os.pushInteger(vipLevel);
				os.pushInteger(curWizardPet);
				os.pushByte(canTakeDrop);
			}

			public SBean.FightRole getRole()
			{
				return role;
			}

			public void setRole(SBean.FightRole role)
			{
				this.role = role;
			}

			public int getMapId()
			{
				return mapId;
			}

			public void setMapId(int mapId)
			{
				this.mapId = mapId;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.Location getLocation()
			{
				return location;
			}

			public void setLocation(SBean.Location location)
			{
				this.location = location;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			public int getSp()
			{
				return sp;
			}

			public void setSp(int sp)
			{
				this.sp = sp;
			}

			public int getArmorVal()
			{
				return armorVal;
			}

			public void setArmorVal(int armorVal)
			{
				this.armorVal = armorVal;
			}

			public Map<Integer, SBean.DBBuff> getBuffs()
			{
				return buffs;
			}

			public void setBuffs(Map<Integer, SBean.DBBuff> buffs)
			{
				this.buffs = buffs;
			}

			public Map<Integer, SBean.FightPet> getPets()
			{
				return pets;
			}

			public void setPets(Map<Integer, SBean.FightPet> pets)
			{
				this.pets = pets;
			}

			public List<Integer> getPetSeq()
			{
				return petSeq;
			}

			public void setPetSeq(List<Integer> petSeq)
			{
				this.petSeq = petSeq;
			}

			public SBean.PetHost getPethost()
			{
				return pethost;
			}

			public void setPethost(SBean.PetHost pethost)
			{
				this.pethost = pethost;
			}

			public SBean.PKInfo getPkInfo()
			{
				return pkInfo;
			}

			public void setPkInfo(SBean.PKInfo pkInfo)
			{
				this.pkInfo = pkInfo;
			}

			public SBean.Team getTeam()
			{
				return team;
			}

			public void setTeam(SBean.Team team)
			{
				this.team = team;
			}

			public int getCurRideHorse()
			{
				return curRideHorse;
			}

			public void setCurRideHorse(int curRideHorse)
			{
				this.curRideHorse = curRideHorse;
			}

			public SBean.MulRoleInfo getMulRoleInfo()
			{
				return mulRoleInfo;
			}

			public void setMulRoleInfo(SBean.MulRoleInfo mulRoleInfo)
			{
				this.mulRoleInfo = mulRoleInfo;
			}

			public SBean.DBAlterState getAlterState()
			{
				return alterState;
			}

			public void setAlterState(SBean.DBAlterState alterState)
			{
				this.alterState = alterState;
			}

			public byte getMainSpawnPos()
			{
				return mainSpawnPos;
			}

			public void setMainSpawnPos(byte mainSpawnPos)
			{
				this.mainSpawnPos = mainSpawnPos;
			}

			public int getDayFailedStreak()
			{
				return dayFailedStreak;
			}

			public void setDayFailedStreak(int dayFailedStreak)
			{
				this.dayFailedStreak = dayFailedStreak;
			}

			public int getVipLevel()
			{
				return vipLevel;
			}

			public void setVipLevel(int vipLevel)
			{
				this.vipLevel = vipLevel;
			}

			public int getCurWizardPet()
			{
				return curWizardPet;
			}

			public void setCurWizardPet(int curWizardPet)
			{
				this.curWizardPet = curWizardPet;
			}

			public byte getCanTakeDrop()
			{
				return canTakeDrop;
			}

			public void setCanTakeDrop(byte canTakeDrop)
			{
				this.canTakeDrop = canTakeDrop;
			}

			private SBean.FightRole role;
			private int mapId;
			private int mapInstance;
			private SBean.Location location;
			private int hp;
			private int sp;
			private int armorVal;
			private Map<Integer, SBean.DBBuff> buffs;
			private Map<Integer, SBean.FightPet> pets;
			private List<Integer> petSeq;
			private SBean.PetHost pethost;
			private SBean.PKInfo pkInfo;
			private SBean.Team team;
			private int curRideHorse;
			private SBean.MulRoleInfo mulRoleInfo;
			private SBean.DBAlterState alterState;
			private byte mainSpawnPos;
			private int dayFailedStreak;
			private int vipLevel;
			private int curWizardPet;
			private byte canTakeDrop;
		}

		public static class LeaveMap extends SimplePacket
		{
			public LeaveMap() { }

			public LeaveMap(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTLeaveMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class UpdateActive extends SimplePacket
		{
			public UpdateActive() { }

			public UpdateActive(int roleID, byte active)
			{
				this.roleID = roleID;
				this.active = active;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTUpdateActive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				active = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushByte(active);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getActive()
			{
				return active;
			}

			public void setActive(byte active)
			{
				this.active = active;
			}

			private int roleID;
			private byte active;
		}

		public static class AddHp extends SimplePacket
		{
			public AddHp() { }

			public AddHp(int roleID, int hp)
			{
				this.roleID = roleID;
				this.hp = hp;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTAddHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				hp = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(hp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			private int roleID;
			private int hp;
		}

		public static class RoleUseItemSkill extends SimplePacket
		{
			public RoleUseItemSkill() { }

			public RoleUseItemSkill(int roleID, int itemID, SBean.Vector3 pos, SBean.Vector3F rotation, 
			                        int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
			{
				this.roleID = roleID;
				this.itemID = itemID;
				this.pos = pos;
				this.rotation = rotation;
				this.targetID = targetID;
				this.targetType = targetType;
				this.ownerID = ownerID;
				this.timeTick = timeTick;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTRoleUseItemSkill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				itemID = is.popInteger();
				if( pos == null )
					pos = new SBean.Vector3();
				is.pop(pos);
				if( rotation == null )
					rotation = new SBean.Vector3F();
				is.pop(rotation);
				targetID = is.popInteger();
				targetType = is.popInteger();
				ownerID = is.popInteger();
				if( timeTick == null )
					timeTick = new SBean.TimeTick();
				is.pop(timeTick);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(itemID);
				os.push(pos);
				os.push(rotation);
				os.pushInteger(targetID);
				os.pushInteger(targetType);
				os.pushInteger(ownerID);
				os.push(timeTick);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getItemID()
			{
				return itemID;
			}

			public void setItemID(int itemID)
			{
				this.itemID = itemID;
			}

			public SBean.Vector3 getPos()
			{
				return pos;
			}

			public void setPos(SBean.Vector3 pos)
			{
				this.pos = pos;
			}

			public SBean.Vector3F getRotation()
			{
				return rotation;
			}

			public void setRotation(SBean.Vector3F rotation)
			{
				this.rotation = rotation;
			}

			public int getTargetID()
			{
				return targetID;
			}

			public void setTargetID(int targetID)
			{
				this.targetID = targetID;
			}

			public int getTargetType()
			{
				return targetType;
			}

			public void setTargetType(int targetType)
			{
				this.targetType = targetType;
			}

			public int getOwnerID()
			{
				return ownerID;
			}

			public void setOwnerID(int ownerID)
			{
				this.ownerID = ownerID;
			}

			public SBean.TimeTick getTimeTick()
			{
				return timeTick;
			}

			public void setTimeTick(SBean.TimeTick timeTick)
			{
				this.timeTick = timeTick;
			}

			private int roleID;
			private int itemID;
			private SBean.Vector3 pos;
			private SBean.Vector3F rotation;
			private int targetID;
			private int targetType;
			private int ownerID;
			private SBean.TimeTick timeTick;
		}

		public static class AddPetHp extends SimplePacket
		{
			public AddPetHp() { }

			public AddPetHp(int roleID, int petID, int hp)
			{
				this.roleID = roleID;
				this.petID = petID;
				this.hp = hp;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTAddPetHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				petID = is.popInteger();
				hp = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(petID);
				os.pushInteger(hp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPetID()
			{
				return petID;
			}

			public void setPetID(int petID)
			{
				this.petID = petID;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			private int roleID;
			private int petID;
			private int hp;
		}

		public static class StartMine extends SimplePacket
		{
			public StartMine() { }

			public StartMine(int roleID, int mineID, int mineInstance)
			{
				this.roleID = roleID;
				this.mineID = mineID;
				this.mineInstance = mineInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTStartMine;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mineID = is.popInteger();
				mineInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mineID);
				os.pushInteger(mineInstance);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMineID()
			{
				return mineID;
			}

			public void setMineID(int mineID)
			{
				this.mineID = mineID;
			}

			public int getMineInstance()
			{
				return mineInstance;
			}

			public void setMineInstance(int mineInstance)
			{
				this.mineInstance = mineInstance;
			}

			private int roleID;
			private int mineID;
			private int mineInstance;
		}

		public static class ResetLocation extends SimplePacket
		{
			public ResetLocation() { }

			public ResetLocation(int roleID, SBean.Location location)
			{
				this.roleID = roleID;
				this.location = location;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTResetLocation;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( location == null )
					location = new SBean.Location();
				is.pop(location);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(location);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.Location getLocation()
			{
				return location;
			}

			public void setLocation(SBean.Location location)
			{
				this.location = location;
			}

			private int roleID;
			private SBean.Location location;
		}

		public static class UpdateCurSkills extends SimplePacket
		{
			public UpdateCurSkills() { }

			public UpdateCurSkills(int roleID, List<Integer> skills)
			{
				this.roleID = roleID;
				this.skills = skills;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTUpdateCurSkills;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				skills = is.popIntegerList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerList(skills);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public List<Integer> getSkills()
			{
				return skills;
			}

			public void setSkills(List<Integer> skills)
			{
				this.skills = skills;
			}

			private int roleID;
			private List<Integer> skills;
		}

		public static class UpdateCurSpirit extends SimplePacket
		{
			public UpdateCurSpirit() { }

			public UpdateCurSpirit(int roleID, Set<Integer> curSpirit)
			{
				this.roleID = roleID;
				this.curSpirit = curSpirit;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTUpdateCurSpirit;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				curSpirit = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerSet(curSpirit);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Set<Integer> getCurSpirit()
			{
				return curSpirit;
			}

			public void setCurSpirit(Set<Integer> curSpirit)
			{
				this.curSpirit = curSpirit;
			}

			private int roleID;
			private Set<Integer> curSpirit;
		}

		public static class PickUpResult extends SimplePacket
		{
			public PickUpResult() { }

			public PickUpResult(int roleID, Set<Integer> dropIDs, int success)
			{
				this.roleID = roleID;
				this.dropIDs = dropIDs;
				this.success = success;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTPickUpResult;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				dropIDs = is.popIntegerTreeSet();
				success = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerSet(dropIDs);
				os.pushInteger(success);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Set<Integer> getDropIDs()
			{
				return dropIDs;
			}

			public void setDropIDs(Set<Integer> dropIDs)
			{
				this.dropIDs = dropIDs;
			}

			public int getSuccess()
			{
				return success;
			}

			public void setSuccess(int success)
			{
				this.success = success;
			}

			private int roleID;
			private Set<Integer> dropIDs;
			private int success;
		}

		public static class UpdateRoleMarriageSkillInfo extends SimplePacket
		{
			public UpdateRoleMarriageSkillInfo() { }

			public UpdateRoleMarriageSkillInfo(int roleID, Map<Integer, Integer> marriageSkills, int marriagePartnerId)
			{
				this.roleID = roleID;
				this.marriageSkills = marriageSkills;
				this.marriagePartnerId = marriagePartnerId;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTUpdateRoleMarriageSkillInfo;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				marriageSkills = is.popIntegerIntegerTreeMap();
				marriagePartnerId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushIntegerIntegerMap(marriageSkills);
				os.pushInteger(marriagePartnerId);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public Map<Integer, Integer> getMarriageSkills()
			{
				return marriageSkills;
			}

			public void setMarriageSkills(Map<Integer, Integer> marriageSkills)
			{
				this.marriageSkills = marriageSkills;
			}

			public int getMarriagePartnerId()
			{
				return marriagePartnerId;
			}

			public void setMarriagePartnerId(int marriagePartnerId)
			{
				this.marriagePartnerId = marriagePartnerId;
			}

			private int roleID;
			private Map<Integer, Integer> marriageSkills;
			private int marriagePartnerId;
		}

		public static class UpdateRoleMarriageSkillLevel extends SimplePacket
		{
			public UpdateRoleMarriageSkillLevel() { }

			public UpdateRoleMarriageSkillLevel(int roleID, int skillId, int skillLevel)
			{
				this.roleID = roleID;
				this.skillId = skillId;
				this.skillLevel = skillLevel;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTUpdateRoleMarriageSkillLevel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				skillId = is.popInteger();
				skillLevel = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(skillId);
				os.pushInteger(skillLevel);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getSkillId()
			{
				return skillId;
			}

			public void setSkillId(int skillId)
			{
				this.skillId = skillId;
			}

			public int getSkillLevel()
			{
				return skillLevel;
			}

			public void setSkillLevel(int skillLevel)
			{
				this.skillLevel = skillLevel;
			}

			private int roleID;
			private int skillId;
			private int skillLevel;
		}

		public static class RoleRevive extends SimplePacket
		{
			public RoleRevive() { }

			public RoleRevive(int roleID, byte fullHp)
			{
				this.roleID = roleID;
				this.fullHp = fullHp;
			}

			@Override
			public int getType()
			{
				return Packet.eS2GMPKTRoleRevive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				fullHp = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushByte(fullHp);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getFullHp()
			{
				return fullHp;
			}

			public void setFullHp(byte fullHp)
			{
				this.fullHp = fullHp;
			}

			private int roleID;
			private byte fullHp;
		}

	}

	// global ms to gs
	public static class GM2S
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class SyncGlobalMaps extends SimplePacket
		{
			public SyncGlobalMaps() { }

			public SyncGlobalMaps(int serverID, Set<Integer> maps)
			{
				this.serverID = serverID;
				this.maps = maps;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncGlobalMaps;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				serverID = is.popInteger();
				maps = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(serverID);
				os.pushIntegerSet(maps);
			}

			public int getServerID()
			{
				return serverID;
			}

			public void setServerID(int serverID)
			{
				this.serverID = serverID;
			}

			public Set<Integer> getMaps()
			{
				return maps;
			}

			public void setMaps(Set<Integer> maps)
			{
				this.maps = maps;
			}

			private int serverID;
			private Set<Integer> maps;
		}

		public static class LuaChannel extends SimplePacket
		{
			public LuaChannel() { }

			public LuaChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTLuaChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class StrChannel extends SimplePacket
		{
			public StrChannel() { }

			public StrChannel(int roleID, String data)
			{
				this.roleID = roleID;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTStrChannel;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				data = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushString(data);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public String getData()
			{
				return data;
			}

			public void setData(String data)
			{
				this.data = data;
			}

			private int roleID;
			private String data;
		}

		public static class MapRoleReady extends SimplePacket
		{
			public MapRoleReady() { }

			public MapRoleReady(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTMapRoleReady;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class SyncLocation extends SimplePacket
		{
			public SyncLocation() { }

			public SyncLocation(int roleID, int mapID, int mapInstance, SBean.Location location)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.location = location;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncLocation;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				if( location == null )
					location = new SBean.Location();
				is.pop(location);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.push(location);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.Location getLocation()
			{
				return location;
			}

			public void setLocation(SBean.Location location)
			{
				this.location = location;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private SBean.Location location;
		}

		public static class SyncHp extends SimplePacket
		{
			public SyncHp() { }

			public SyncHp(int roleID, int mapID, int mapInstance, int hp, 
			              int hpMax)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.hp = hp;
				this.hpMax = hpMax;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				hp = is.popInteger();
				hpMax = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(hp);
				os.pushInteger(hpMax);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			public int getHpMax()
			{
				return hpMax;
			}

			public void setHpMax(int hpMax)
			{
				this.hpMax = hpMax;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int hp;
			private int hpMax;
		}

		public static class AddDrops extends SimplePacket
		{
			public AddDrops() { }

			public AddDrops(int roleID, int mapID, int mapInstance, Map<Integer, Integer> drops)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.drops = drops;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTAddDrops;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				drops = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushIntegerIntegerMap(drops);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public Map<Integer, Integer> getDrops()
			{
				return drops;
			}

			public void setDrops(Map<Integer, Integer> drops)
			{
				this.drops = drops;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private Map<Integer, Integer> drops;
		}

		public static class AddKill extends SimplePacket
		{
			public AddKill() { }

			public AddKill(int roleID, int mapID, int mapInstance, int targetType, 
			               int targetID, float weaponAdd, int killRole)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.targetType = targetType;
				this.targetID = targetID;
				this.weaponAdd = weaponAdd;
				this.killRole = killRole;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTAddKill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				targetType = is.popInteger();
				targetID = is.popInteger();
				weaponAdd = is.popFloat();
				killRole = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(targetType);
				os.pushInteger(targetID);
				os.pushFloat(weaponAdd);
				os.pushInteger(killRole);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getTargetType()
			{
				return targetType;
			}

			public void setTargetType(int targetType)
			{
				this.targetType = targetType;
			}

			public int getTargetID()
			{
				return targetID;
			}

			public void setTargetID(int targetID)
			{
				this.targetID = targetID;
			}

			public float getWeaponAdd()
			{
				return weaponAdd;
			}

			public void setWeaponAdd(float weaponAdd)
			{
				this.weaponAdd = weaponAdd;
			}

			public int getKillRole()
			{
				return killRole;
			}

			public void setKillRole(int killRole)
			{
				this.killRole = killRole;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int targetType;
			private int targetID;
			private float weaponAdd;
			private int killRole;
		}

		public static class SyncDurability extends SimplePacket
		{
			public SyncDurability() { }

			public SyncDurability(int roleID, int mapID, int mapInstance, int wid, 
			                      int durability)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.wid = wid;
				this.durability = durability;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncDurability;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				wid = is.popInteger();
				durability = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(wid);
				os.pushInteger(durability);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWid()
			{
				return wid;
			}

			public void setWid(int wid)
			{
				this.wid = wid;
			}

			public int getDurability()
			{
				return durability;
			}

			public void setDurability(int durability)
			{
				this.durability = durability;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int wid;
			private int durability;
		}

		public static class SyncEndMine extends SimplePacket
		{
			public SyncEndMine() { }

			public SyncEndMine(int roleID, int mapID, int mapInstance, int mineId, 
			                   int mineInstance, int ok)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.mineId = mineId;
				this.mineInstance = mineInstance;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncEndMine;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				mineId = is.popInteger();
				mineInstance = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(mineId);
				os.pushInteger(mineInstance);
				os.pushInteger(ok);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getMineId()
			{
				return mineId;
			}

			public void setMineId(int mineId)
			{
				this.mineId = mineId;
			}

			public int getMineInstance()
			{
				return mineInstance;
			}

			public void setMineInstance(int mineInstance)
			{
				this.mineInstance = mineInstance;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int mineId;
			private int mineInstance;
			private int ok;
		}

		public static class KickRoleFromMap extends SimplePacket
		{
			public KickRoleFromMap() { }

			public KickRoleFromMap(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTKickRoleFromMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class RoleUseItemSkillSuc extends SimplePacket
		{
			public RoleUseItemSkillSuc() { }

			public RoleUseItemSkillSuc(int roleID, int itemID, int ok)
			{
				this.roleID = roleID;
				this.itemID = itemID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTRoleUseItemSkillSuc;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				itemID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(itemID);
				os.pushInteger(ok);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getItemID()
			{
				return itemID;
			}

			public void setItemID(int itemID)
			{
				this.itemID = itemID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int roleID;
			private int itemID;
			private int ok;
		}

		public static class UpdateRoleFightState extends SimplePacket
		{
			public UpdateRoleFightState() { }

			public UpdateRoleFightState(int roleID, int mapID, int mapInstance, byte fightState)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.fightState = fightState;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTUpdateRoleFightState;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				fightState = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushByte(fightState);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public byte getFightState()
			{
				return fightState;
			}

			public void setFightState(byte fightState)
			{
				this.fightState = fightState;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private byte fightState;
		}

		public static class SyncRolePetHp extends SimplePacket
		{
			public SyncRolePetHp() { }

			public SyncRolePetHp(int roleID, int petID, int mapID, int mapInstance, 
			                     SBean.Hp hpState)
			{
				this.roleID = roleID;
				this.petID = petID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.hpState = hpState;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncRolePetHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				petID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				if( hpState == null )
					hpState = new SBean.Hp();
				is.pop(hpState);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(petID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.push(hpState);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getPetID()
			{
				return petID;
			}

			public void setPetID(int petID)
			{
				this.petID = petID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.Hp getHpState()
			{
				return hpState;
			}

			public void setHpState(SBean.Hp hpState)
			{
				this.hpState = hpState;
			}

			private int roleID;
			private int petID;
			private int mapID;
			private int mapInstance;
			private SBean.Hp hpState;
		}

		public static class SyncArmorVal extends SimplePacket
		{
			public SyncArmorVal() { }

			public SyncArmorVal(int roleID, int mapID, int mapInstance, int armorVal, 
			                    int armorValMax)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.armorVal = armorVal;
				this.armorValMax = armorValMax;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncArmorVal;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				armorVal = is.popInteger();
				armorValMax = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(armorVal);
				os.pushInteger(armorValMax);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getArmorVal()
			{
				return armorVal;
			}

			public void setArmorVal(int armorVal)
			{
				this.armorVal = armorVal;
			}

			public int getArmorValMax()
			{
				return armorValMax;
			}

			public void setArmorValMax(int armorValMax)
			{
				this.armorValMax = armorValMax;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int armorVal;
			private int armorValMax;
		}

		public static class SyncWeaponMaster extends SimplePacket
		{
			public SyncWeaponMaster() { }

			public SyncWeaponMaster(int roleID, int mapID, int mapInstance, int weaponID)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.weaponID = weaponID;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTSyncWeaponMaster;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				weaponID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(weaponID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWeaponID()
			{
				return weaponID;
			}

			public void setWeaponID(int weaponID)
			{
				this.weaponID = weaponID;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int weaponID;
		}

		public static class RolePickUpDrops extends SimplePacket
		{
			public RolePickUpDrops() { }

			public RolePickUpDrops(int roleID, int mapID, int mapInstance, Map<Integer, SBean.DummyGoods> drops)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.drops = drops;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTRolePickUpDrops;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				drops = is.popIntegerTreeMap(SBean.DummyGoods.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushIntegerMap(drops);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public Map<Integer, SBean.DummyGoods> getDrops()
			{
				return drops;
			}

			public void setDrops(Map<Integer, SBean.DummyGoods> drops)
			{
				this.drops = drops;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private Map<Integer, SBean.DummyGoods> drops;
		}

		public static class RolePickUpRareDrops extends SimplePacket
		{
			public RolePickUpRareDrops() { }

			public RolePickUpRareDrops(int roleID, int mapID, int mapInstance, int dropId, 
			                           SBean.DummyGoods drop, int monsterId)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.dropId = dropId;
				this.drop = drop;
				this.monsterId = monsterId;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2SPKTRolePickUpRareDrops;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				dropId = is.popInteger();
				if( drop == null )
					drop = new SBean.DummyGoods();
				is.pop(drop);
				monsterId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(dropId);
				os.push(drop);
				os.pushInteger(monsterId);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getDropId()
			{
				return dropId;
			}

			public void setDropId(int dropId)
			{
				this.dropId = dropId;
			}

			public SBean.DummyGoods getDrop()
			{
				return drop;
			}

			public void setDrop(SBean.DummyGoods drop)
			{
				this.drop = drop;
			}

			public int getMonsterId()
			{
				return monsterId;
			}

			public void setMonsterId(int monsterId)
			{
				this.monsterId = monsterId;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int dropId;
			private SBean.DummyGoods drop;
			private int monsterId;
		}

	}

	// gs to fs
	public static class S2F
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int serverId, Set<Integer> zones)
			{
				this.serverId = serverId;
				this.zones = zones;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				serverId = is.popInteger();
				zones = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(serverId);
				os.pushIntegerSet(zones);
			}

			public int getServerId()
			{
				return serverId;
			}

			public void setServerId(int serverId)
			{
				this.serverId = serverId;
			}

			public Set<Integer> getZones()
			{
				return zones;
			}

			public void setZones(Set<Integer> zones)
			{
				this.zones = zones;
			}

			private int serverId;
			private Set<Integer> zones;
		}

		public static class ReportTimeOffset extends SimplePacket
		{
			public ReportTimeOffset() { }

			public ReportTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTReportTimeOffset;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				timeOffset = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(timeOffset);
			}

			public int getTimeOffset()
			{
				return timeOffset;
			}

			public void setTimeOffset(int timeOffset)
			{
				this.timeOffset = timeOffset;
			}

			private int timeOffset;
		}

		public static class RoleJoinForceWarReq extends SimplePacket
		{
			public RoleJoinForceWarReq() { }

			public RoleJoinForceWarReq(int tagID, SBean.ForceWarJoin joinInfo, int forcewarType)
			{
				this.tagID = tagID;
				this.joinInfo = joinInfo;
				this.forcewarType = forcewarType;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTRoleJoinForceWarReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				if( joinInfo == null )
					joinInfo = new SBean.ForceWarJoin();
				is.pop(joinInfo);
				forcewarType = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.push(joinInfo);
				os.pushInteger(forcewarType);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public SBean.ForceWarJoin getJoinInfo()
			{
				return joinInfo;
			}

			public void setJoinInfo(SBean.ForceWarJoin joinInfo)
			{
				this.joinInfo = joinInfo;
			}

			public int getForcewarType()
			{
				return forcewarType;
			}

			public void setForcewarType(int forcewarType)
			{
				this.forcewarType = forcewarType;
			}

			private int tagID;
			private SBean.ForceWarJoin joinInfo;
			private int forcewarType;
		}

		public static class RoleQuitForceWarReq extends SimplePacket
		{
			public RoleQuitForceWarReq() { }

			public RoleQuitForceWarReq(int tagID, int roleID, byte bwType, int forcewarType)
			{
				this.tagID = tagID;
				this.roleID = roleID;
				this.bwType = bwType;
				this.forcewarType = forcewarType;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTRoleQuitForceWarReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
				bwType = is.popByte();
				forcewarType = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
				os.pushByte(bwType);
				os.pushInteger(forcewarType);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getBwType()
			{
				return bwType;
			}

			public void setBwType(byte bwType)
			{
				this.bwType = bwType;
			}

			public int getForcewarType()
			{
				return forcewarType;
			}

			public void setForcewarType(int forcewarType)
			{
				this.forcewarType = forcewarType;
			}

			private int tagID;
			private int roleID;
			private byte bwType;
			private int forcewarType;
		}

		public static class UpdateFightRank extends SimplePacket
		{
			public UpdateFightRank() { }

			public UpdateFightRank(int rankID, SBean.RankRole rankRole, int rankClearTime)
			{
				this.rankID = rankID;
				this.rankRole = rankRole;
				this.rankClearTime = rankClearTime;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTUpdateFightRank;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				rankID = is.popInteger();
				if( rankRole == null )
					rankRole = new SBean.RankRole();
				is.pop(rankRole);
				rankClearTime = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(rankID);
				os.push(rankRole);
				os.pushInteger(rankClearTime);
			}

			public int getRankID()
			{
				return rankID;
			}

			public void setRankID(int rankID)
			{
				this.rankID = rankID;
			}

			public SBean.RankRole getRankRole()
			{
				return rankRole;
			}

			public void setRankRole(SBean.RankRole rankRole)
			{
				this.rankRole = rankRole;
			}

			public int getRankClearTime()
			{
				return rankClearTime;
			}

			public void setRankClearTime(int rankClearTime)
			{
				this.rankClearTime = rankClearTime;
			}

			private int rankID;
			private SBean.RankRole rankRole;
			private int rankClearTime;
		}

		public static class SendMsgFight extends SimplePacket
		{
			public SendMsgFight() { }

			public SendMsgFight(int roleId, int mapId, int mapInstance, SBean.MessageInfo msgContent)
			{
				this.roleId = roleId;
				this.mapId = mapId;
				this.mapInstance = mapInstance;
				this.msgContent = msgContent;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTSendMsgFight;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleId = is.popInteger();
				mapId = is.popInteger();
				mapInstance = is.popInteger();
				if( msgContent == null )
					msgContent = new SBean.MessageInfo();
				is.pop(msgContent);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleId);
				os.pushInteger(mapId);
				os.pushInteger(mapInstance);
				os.push(msgContent);
			}

			public int getRoleId()
			{
				return roleId;
			}

			public void setRoleId(int roleId)
			{
				this.roleId = roleId;
			}

			public int getMapId()
			{
				return mapId;
			}

			public void setMapId(int mapId)
			{
				this.mapId = mapId;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.MessageInfo getMsgContent()
			{
				return msgContent;
			}

			public void setMsgContent(SBean.MessageInfo msgContent)
			{
				this.msgContent = msgContent;
			}

			private int roleId;
			private int mapId;
			private int mapInstance;
			private SBean.MessageInfo msgContent;
		}

		public static class SingleJoinSuperArenaReq extends SimplePacket
		{
			public SingleJoinSuperArenaReq() { }

			public SingleJoinSuperArenaReq(int tagID, SBean.SuperArenaJoin joinInfo, int arenaType, int grade)
			{
				this.tagID = tagID;
				this.joinInfo = joinInfo;
				this.arenaType = arenaType;
				this.grade = grade;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTSingleJoinSuperArenaReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				if( joinInfo == null )
					joinInfo = new SBean.SuperArenaJoin();
				is.pop(joinInfo);
				arenaType = is.popInteger();
				grade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.push(joinInfo);
				os.pushInteger(arenaType);
				os.pushInteger(grade);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public SBean.SuperArenaJoin getJoinInfo()
			{
				return joinInfo;
			}

			public void setJoinInfo(SBean.SuperArenaJoin joinInfo)
			{
				this.joinInfo = joinInfo;
			}

			public int getArenaType()
			{
				return arenaType;
			}

			public void setArenaType(int arenaType)
			{
				this.arenaType = arenaType;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			private int tagID;
			private SBean.SuperArenaJoin joinInfo;
			private int arenaType;
			private int grade;
		}

		public static class SingleQuitSuperArenaReq extends SimplePacket
		{
			public SingleQuitSuperArenaReq() { }

			public SingleQuitSuperArenaReq(int tagID, int roleID, int arenaType, int grade)
			{
				this.tagID = tagID;
				this.roleID = roleID;
				this.arenaType = arenaType;
				this.grade = grade;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTSingleQuitSuperArenaReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
				arenaType = is.popInteger();
				grade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
				os.pushInteger(arenaType);
				os.pushInteger(grade);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getArenaType()
			{
				return arenaType;
			}

			public void setArenaType(int arenaType)
			{
				this.arenaType = arenaType;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			private int tagID;
			private int roleID;
			private int arenaType;
			private int grade;
		}

		public static class TeamJoinSuperArenaReq extends SimplePacket
		{
			public TeamJoinSuperArenaReq() { }

			public TeamJoinSuperArenaReq(int tagID, List<SBean.SuperArenaJoin> members, int arenaType, int grade)
			{
				this.tagID = tagID;
				this.members = members;
				this.arenaType = arenaType;
				this.grade = grade;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTTeamJoinSuperArenaReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				members = is.popList(SBean.SuperArenaJoin.class);
				arenaType = is.popInteger();
				grade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushList(members);
				os.pushInteger(arenaType);
				os.pushInteger(grade);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public List<SBean.SuperArenaJoin> getMembers()
			{
				return members;
			}

			public void setMembers(List<SBean.SuperArenaJoin> members)
			{
				this.members = members;
			}

			public int getArenaType()
			{
				return arenaType;
			}

			public void setArenaType(int arenaType)
			{
				this.arenaType = arenaType;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			private int tagID;
			private List<SBean.SuperArenaJoin> members;
			private int arenaType;
			private int grade;
		}

		public static class TeamQuitSuperArenaReq extends SimplePacket
		{
			public TeamQuitSuperArenaReq() { }

			public TeamQuitSuperArenaReq(int tagID, int roleID, int memberCount, int arenaType, 
			                             int grade)
			{
				this.tagID = tagID;
				this.roleID = roleID;
				this.memberCount = memberCount;
				this.arenaType = arenaType;
				this.grade = grade;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTTeamQuitSuperArenaReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
				memberCount = is.popInteger();
				arenaType = is.popInteger();
				grade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
				os.pushInteger(memberCount);
				os.pushInteger(arenaType);
				os.pushInteger(grade);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMemberCount()
			{
				return memberCount;
			}

			public void setMemberCount(int memberCount)
			{
				this.memberCount = memberCount;
			}

			public int getArenaType()
			{
				return arenaType;
			}

			public void setArenaType(int arenaType)
			{
				this.arenaType = arenaType;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			private int tagID;
			private int roleID;
			private int memberCount;
			private int arenaType;
			private int grade;
		}

		public static class QueryTeamMembersReq extends SimplePacket
		{
			public QueryTeamMembersReq() { }

			public QueryTeamMembersReq(int tagID, int roleID)
			{
				this.tagID = tagID;
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTQueryTeamMembersReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int tagID;
			private int roleID;
		}

		public static class RoleLeaveTeam extends SimplePacket
		{
			public RoleLeaveTeam() { }

			public RoleLeaveTeam(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTRoleLeaveTeam;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class QueryTeamMemberReq extends SimplePacket
		{
			public QueryTeamMemberReq() { }

			public QueryTeamMemberReq(int tagID, int queryID)
			{
				this.tagID = tagID;
				this.queryID = queryID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTQueryTeamMemberReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				queryID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(queryID);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getQueryID()
			{
				return queryID;
			}

			public void setQueryID(int queryID)
			{
				this.queryID = queryID;
			}

			private int tagID;
			private int queryID;
		}

		public static class LeaveMap extends SimplePacket
		{
			public LeaveMap() { }

			public LeaveMap(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTLeaveMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class SendMsgGlobalTeam extends SimplePacket
		{
			public SendMsgGlobalTeam() { }

			public SendMsgGlobalTeam(int roleId, SBean.MessageInfo msgContent)
			{
				this.roleId = roleId;
				this.msgContent = msgContent;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTSendMsgGlobalTeam;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleId = is.popInteger();
				if( msgContent == null )
					msgContent = new SBean.MessageInfo();
				is.pop(msgContent);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleId);
				os.push(msgContent);
			}

			public int getRoleId()
			{
				return roleId;
			}

			public void setRoleId(int roleId)
			{
				this.roleId = roleId;
			}

			public SBean.MessageInfo getMsgContent()
			{
				return msgContent;
			}

			public void setMsgContent(SBean.MessageInfo msgContent)
			{
				this.msgContent = msgContent;
			}

			private int roleId;
			private SBean.MessageInfo msgContent;
		}

		public static class TeamJoinForceWarReq extends SimplePacket
		{
			public TeamJoinForceWarReq() { }

			public TeamJoinForceWarReq(int tagID, List<SBean.ForceWarJoin> members, byte bwType, int forcewarType)
			{
				this.tagID = tagID;
				this.members = members;
				this.bwType = bwType;
				this.forcewarType = forcewarType;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTTeamJoinForceWarReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				members = is.popList(SBean.ForceWarJoin.class);
				bwType = is.popByte();
				forcewarType = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushList(members);
				os.pushByte(bwType);
				os.pushInteger(forcewarType);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public List<SBean.ForceWarJoin> getMembers()
			{
				return members;
			}

			public void setMembers(List<SBean.ForceWarJoin> members)
			{
				this.members = members;
			}

			public byte getBwType()
			{
				return bwType;
			}

			public void setBwType(byte bwType)
			{
				this.bwType = bwType;
			}

			public int getForcewarType()
			{
				return forcewarType;
			}

			public void setForcewarType(int forcewarType)
			{
				this.forcewarType = forcewarType;
			}

			private int tagID;
			private List<SBean.ForceWarJoin> members;
			private byte bwType;
			private int forcewarType;
		}

		public static class TeamQuitForceWarReq extends SimplePacket
		{
			public TeamQuitForceWarReq() { }

			public TeamQuitForceWarReq(int tagID, int roleID, byte bwType, int count, 
			                           int forcewarType)
			{
				this.tagID = tagID;
				this.roleID = roleID;
				this.bwType = bwType;
				this.count = count;
				this.forcewarType = forcewarType;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTTeamQuitForceWarReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
				bwType = is.popByte();
				count = is.popInteger();
				forcewarType = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
				os.pushByte(bwType);
				os.pushInteger(count);
				os.pushInteger(forcewarType);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public byte getBwType()
			{
				return bwType;
			}

			public void setBwType(byte bwType)
			{
				this.bwType = bwType;
			}

			public int getCount()
			{
				return count;
			}

			public void setCount(int count)
			{
				this.count = count;
			}

			public int getForcewarType()
			{
				return forcewarType;
			}

			public void setForcewarType(int forcewarType)
			{
				this.forcewarType = forcewarType;
			}

			private int tagID;
			private int roleID;
			private byte bwType;
			private int count;
			private int forcewarType;
		}

		public static class SyncRoleDemonHoleReq extends SimplePacket
		{
			public SyncRoleDemonHoleReq() { }

			public SyncRoleDemonHoleReq(int tagID, int roleID)
			{
				this.tagID = tagID;
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTSyncRoleDemonHoleReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int tagID;
			private int roleID;
		}

		public static class RoleJoinDemonHoleReq extends SimplePacket
		{
			public RoleJoinDemonHoleReq() { }

			public RoleJoinDemonHoleReq(int tagID, SBean.RoleOverview role)
			{
				this.tagID = tagID;
				this.role = role;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTRoleJoinDemonHoleReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				if( role == null )
					role = new SBean.RoleOverview();
				is.pop(role);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.push(role);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public SBean.RoleOverview getRole()
			{
				return role;
			}

			public void setRole(SBean.RoleOverview role)
			{
				this.role = role;
			}

			private int tagID;
			private SBean.RoleOverview role;
		}

		public static class RoleChangeDemonHoleFloorReq extends SimplePacket
		{
			public RoleChangeDemonHoleFloorReq() { }

			public RoleChangeDemonHoleFloorReq(int tagID, SBean.RoleOverview role, int floor)
			{
				this.tagID = tagID;
				this.role = role;
				this.floor = floor;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTRoleChangeDemonHoleFloorReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				if( role == null )
					role = new SBean.RoleOverview();
				is.pop(role);
				floor = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.push(role);
				os.pushInteger(floor);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public SBean.RoleOverview getRole()
			{
				return role;
			}

			public void setRole(SBean.RoleOverview role)
			{
				this.role = role;
			}

			public int getFloor()
			{
				return floor;
			}

			public void setFloor(int floor)
			{
				this.floor = floor;
			}

			private int tagID;
			private SBean.RoleOverview role;
			private int floor;
		}

		public static class RoleDemonHoleBattleReq extends SimplePacket
		{
			public RoleDemonHoleBattleReq() { }

			public RoleDemonHoleBattleReq(int tagID, int roleID)
			{
				this.tagID = tagID;
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTRoleDemonHoleBattleReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(roleID);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int tagID;
			private int roleID;
		}

		public static class RoleEnterDemonHoleFloor extends SimplePacket
		{
			public RoleEnterDemonHoleFloor() { }

			public RoleEnterDemonHoleFloor(SBean.RoleOverview role, int floor)
			{
				this.role = role;
				this.floor = floor;
			}

			@Override
			public int getType()
			{
				return Packet.eS2FPKTRoleEnterDemonHoleFloor;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( role == null )
					role = new SBean.RoleOverview();
				is.pop(role);
				floor = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(role);
				os.pushInteger(floor);
			}

			public SBean.RoleOverview getRole()
			{
				return role;
			}

			public void setRole(SBean.RoleOverview role)
			{
				this.role = role;
			}

			public int getFloor()
			{
				return floor;
			}

			public void setFloor(int floor)
			{
				this.floor = floor;
			}

			private SBean.RoleOverview role;
			private int floor;
		}

	}

	// fs to gs
	public static class F2S
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class SyncGSRankStart extends SimplePacket
		{
			public SyncGSRankStart() { }

			public SyncGSRankStart(int rankID, int snapshotCreateTime)
			{
				this.rankID = rankID;
				this.snapshotCreateTime = snapshotCreateTime;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncGSRankStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				rankID = is.popInteger();
				snapshotCreateTime = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(rankID);
				os.pushInteger(snapshotCreateTime);
			}

			public int getRankID()
			{
				return rankID;
			}

			public void setRankID(int rankID)
			{
				this.rankID = rankID;
			}

			public int getSnapshotCreateTime()
			{
				return snapshotCreateTime;
			}

			public void setSnapshotCreateTime(int snapshotCreateTime)
			{
				this.snapshotCreateTime = snapshotCreateTime;
			}

			private int rankID;
			private int snapshotCreateTime;
		}

		public static class SyncGSRank extends SimplePacket
		{
			public SyncGSRank() { }

			public SyncGSRank(int rankID, List<SBean.RankRole> batch)
			{
				this.rankID = rankID;
				this.batch = batch;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncGSRank;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				rankID = is.popInteger();
				batch = is.popList(SBean.RankRole.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(rankID);
				os.pushList(batch);
			}

			public int getRankID()
			{
				return rankID;
			}

			public void setRankID(int rankID)
			{
				this.rankID = rankID;
			}

			public List<SBean.RankRole> getBatch()
			{
				return batch;
			}

			public void setBatch(List<SBean.RankRole> batch)
			{
				this.batch = batch;
			}

			private int rankID;
			private List<SBean.RankRole> batch;
		}

		public static class SyncGSRankEnd extends SimplePacket
		{
			public SyncGSRankEnd() { }

			public SyncGSRankEnd(int rankID)
			{
				this.rankID = rankID;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncGSRankEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				rankID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(rankID);
			}

			public int getRankID()
			{
				return rankID;
			}

			public void setRankID(int rankID)
			{
				this.rankID = rankID;
			}

			private int rankID;
		}

		public static class RoleJoinForceWarRes extends SimplePacket
		{
			public RoleJoinForceWarRes() { }

			public RoleJoinForceWarRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleJoinForceWarRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class RoleQuitForceWarRes extends SimplePacket
		{
			public RoleQuitForceWarRes() { }

			public RoleQuitForceWarRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleQuitForceWarRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class RoleEnterForceWar extends SimplePacket
		{
			public RoleEnterForceWar() { }

			public RoleEnterForceWar(int roleID, int mapID, int mapInstance, byte mainSpawn)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.mainSpawn = mainSpawn;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleEnterForceWar;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				mainSpawn = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushByte(mainSpawn);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public byte getMainSpawn()
			{
				return mainSpawn;
			}

			public void setMainSpawn(byte mainSpawn)
			{
				this.mainSpawn = mainSpawn;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private byte mainSpawn;
		}

		public static class SyncForceWarMapStart extends SimplePacket
		{
			public SyncForceWarMapStart() { }

			public SyncForceWarMapStart(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncForceWarMapStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		// />
		public static class SyncForceWarMapEnd extends SimplePacket
		{
			public SyncForceWarMapEnd() { }

			public SyncForceWarMapEnd(int mapID, int mapInstance, int winSide, int killedBoss, 
			                          int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide, 
			                          SBean.RankClearTime rankClearTime)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.winSide = winSide;
				this.killedBoss = killedBoss;
				this.whiteScore = whiteScore;
				this.blackScore = blackScore;
				this.whiteSide = whiteSide;
				this.blackSide = blackSide;
				this.rankClearTime = rankClearTime;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncForceWarMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				winSide = is.popInteger();
				killedBoss = is.popInteger();
				whiteScore = is.popInteger();
				blackScore = is.popInteger();
				whiteSide = is.popList(SBean.ForceWarOverview.class);
				blackSide = is.popList(SBean.ForceWarOverview.class);
				if( rankClearTime == null )
					rankClearTime = new SBean.RankClearTime();
				is.pop(rankClearTime);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(winSide);
				os.pushInteger(killedBoss);
				os.pushInteger(whiteScore);
				os.pushInteger(blackScore);
				os.pushList(whiteSide);
				os.pushList(blackSide);
				os.push(rankClearTime);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWinSide()
			{
				return winSide;
			}

			public void setWinSide(int winSide)
			{
				this.winSide = winSide;
			}

			public int getKilledBoss()
			{
				return killedBoss;
			}

			public void setKilledBoss(int killedBoss)
			{
				this.killedBoss = killedBoss;
			}

			public int getWhiteScore()
			{
				return whiteScore;
			}

			public void setWhiteScore(int whiteScore)
			{
				this.whiteScore = whiteScore;
			}

			public int getBlackScore()
			{
				return blackScore;
			}

			public void setBlackScore(int blackScore)
			{
				this.blackScore = blackScore;
			}

			public List<SBean.ForceWarOverview> getWhiteSide()
			{
				return whiteSide;
			}

			public void setWhiteSide(List<SBean.ForceWarOverview> whiteSide)
			{
				this.whiteSide = whiteSide;
			}

			public List<SBean.ForceWarOverview> getBlackSide()
			{
				return blackSide;
			}

			public void setBlackSide(List<SBean.ForceWarOverview> blackSide)
			{
				this.blackSide = blackSide;
			}

			public SBean.RankClearTime getRankClearTime()
			{
				return rankClearTime;
			}

			public void setRankClearTime(SBean.RankClearTime rankClearTime)
			{
				this.rankClearTime = rankClearTime;
			}

			private int mapID;
			private int mapInstance;
			private int winSide;
			private int killedBoss;
			private int whiteScore;
			private int blackScore;
			private List<SBean.ForceWarOverview> whiteSide;
			private List<SBean.ForceWarOverview> blackSide;
			private SBean.RankClearTime rankClearTime;
		}

		public static class SyncMapCopyTimeOut extends SimplePacket
		{
			public SyncMapCopyTimeOut() { }

			public SyncMapCopyTimeOut(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncMapCopyTimeOut;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class ReceiveMsgFight extends SimplePacket
		{
			public ReceiveMsgFight() { }

			public ReceiveMsgFight(int roleId, SBean.MessageInfo msgContent)
			{
				this.roleId = roleId;
				this.msgContent = msgContent;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTReceiveMsgFight;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleId = is.popInteger();
				if( msgContent == null )
					msgContent = new SBean.MessageInfo();
				is.pop(msgContent);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleId);
				os.push(msgContent);
			}

			public int getRoleId()
			{
				return roleId;
			}

			public void setRoleId(int roleId)
			{
				this.roleId = roleId;
			}

			public SBean.MessageInfo getMsgContent()
			{
				return msgContent;
			}

			public void setMsgContent(SBean.MessageInfo msgContent)
			{
				this.msgContent = msgContent;
			}

			private int roleId;
			private SBean.MessageInfo msgContent;
		}

		public static class SingleJoinSuperArenaRes extends SimplePacket
		{
			public SingleJoinSuperArenaRes() { }

			public SingleJoinSuperArenaRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSingleJoinSuperArenaRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class SingleQuitSuperArenaRes extends SimplePacket
		{
			public SingleQuitSuperArenaRes() { }

			public SingleQuitSuperArenaRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSingleQuitSuperArenaRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class TeamJoinSuperArenaRes extends SimplePacket
		{
			public TeamJoinSuperArenaRes() { }

			public TeamJoinSuperArenaRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTTeamJoinSuperArenaRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class TeamQuitSuperArenaRes extends SimplePacket
		{
			public TeamQuitSuperArenaRes() { }

			public TeamQuitSuperArenaRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTTeamQuitSuperArenaRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class QueryTeamMembersRes extends SimplePacket
		{
			public QueryTeamMembersRes() { }

			public QueryTeamMembersRes(int tagID, List<SBean.RoleOverview> overviews)
			{
				this.tagID = tagID;
				this.overviews = overviews;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTQueryTeamMembersRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				overviews = is.popList(SBean.RoleOverview.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushList(overviews);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public List<SBean.RoleOverview> getOverviews()
			{
				return overviews;
			}

			public void setOverviews(List<SBean.RoleOverview> overviews)
			{
				this.overviews = overviews;
			}

			private int tagID;
			private List<SBean.RoleOverview> overviews;
		}

		public static class CreateMapCopy extends SimplePacket
		{
			public CreateMapCopy() { }

			public CreateMapCopy(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTCreateMapCopy;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class RoleEnterSuperArena extends SimplePacket
		{
			public RoleEnterSuperArena() { }

			public RoleEnterSuperArena(int roleID, int mapID, int mapInstance, byte mainSpawnPos)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.mainSpawnPos = mainSpawnPos;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleEnterSuperArena;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				mainSpawnPos = is.popByte();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushByte(mainSpawnPos);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public byte getMainSpawnPos()
			{
				return mainSpawnPos;
			}

			public void setMainSpawnPos(byte mainSpawnPos)
			{
				this.mainSpawnPos = mainSpawnPos;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private byte mainSpawnPos;
		}

		public static class SyncSuperArenaStart extends SimplePacket
		{
			public SyncSuperArenaStart() { }

			public SyncSuperArenaStart(int mapID, int mapInstance, Map<Integer, Integer> eloDiffs)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.eloDiffs = eloDiffs;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncSuperArenaStart;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				eloDiffs = is.popIntegerIntegerTreeMap();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushIntegerIntegerMap(eloDiffs);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public Map<Integer, Integer> getEloDiffs()
			{
				return eloDiffs;
			}

			public void setEloDiffs(Map<Integer, Integer> eloDiffs)
			{
				this.eloDiffs = eloDiffs;
			}

			private int mapID;
			private int mapInstance;
			private Map<Integer, Integer> eloDiffs;
		}

		public static class SyncSuperArenaMapEnd extends SimplePacket
		{
			public SyncSuperArenaMapEnd() { }

			public SyncSuperArenaMapEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result, int rankClearTime)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.result = result;
				this.rankClearTime = rankClearTime;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncSuperArenaMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				if( result == null )
					result = new SBean.SuperArenaBattleResult();
				is.pop(result);
				rankClearTime = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.push(result);
				os.pushInteger(rankClearTime);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.SuperArenaBattleResult getResult()
			{
				return result;
			}

			public void setResult(SBean.SuperArenaBattleResult result)
			{
				this.result = result;
			}

			public int getRankClearTime()
			{
				return rankClearTime;
			}

			public void setRankClearTime(int rankClearTime)
			{
				this.rankClearTime = rankClearTime;
			}

			private int mapID;
			private int mapInstance;
			private SBean.SuperArenaBattleResult result;
			private int rankClearTime;
		}

		public static class SuperArenaMatchResult extends SimplePacket
		{
			public SuperArenaMatchResult() { }

			public SuperArenaMatchResult(int roleID, int arenaType, int grade, int result)
			{
				this.roleID = roleID;
				this.arenaType = arenaType;
				this.grade = grade;
				this.result = result;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSuperArenaMatchResult;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				arenaType = is.popInteger();
				grade = is.popInteger();
				result = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(arenaType);
				os.pushInteger(grade);
				os.pushInteger(result);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getArenaType()
			{
				return arenaType;
			}

			public void setArenaType(int arenaType)
			{
				this.arenaType = arenaType;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			public int getResult()
			{
				return result;
			}

			public void setResult(int result)
			{
				this.result = result;
			}

			private int roleID;
			private int arenaType;
			private int grade;
			private int result;
		}

		public static class SyncRoleFightTeam extends SimplePacket
		{
			public SyncRoleFightTeam() { }

			public SyncRoleFightTeam(int roleID, SBean.Team team)
			{
				this.roleID = roleID;
				this.team = team;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncRoleFightTeam;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( team == null )
					team = new SBean.Team();
				is.pop(team);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(team);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.Team getTeam()
			{
				return team;
			}

			public void setTeam(SBean.Team team)
			{
				this.team = team;
			}

			private int roleID;
			private SBean.Team team;
		}

		public static class TeamLeaderChange extends SimplePacket
		{
			public TeamLeaderChange() { }

			public TeamLeaderChange(int roleID, SBean.RoleOverview newLeader)
			{
				this.roleID = roleID;
				this.newLeader = newLeader;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTTeamLeaderChange;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( newLeader == null )
					newLeader = new SBean.RoleOverview();
				is.pop(newLeader);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(newLeader);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.RoleOverview getNewLeader()
			{
				return newLeader;
			}

			public void setNewLeader(SBean.RoleOverview newLeader)
			{
				this.newLeader = newLeader;
			}

			private int roleID;
			private SBean.RoleOverview newLeader;
		}

		public static class MemberLeaveTeam extends SimplePacket
		{
			public MemberLeaveTeam() { }

			public MemberLeaveTeam(int roleID, SBean.RoleOverview member)
			{
				this.roleID = roleID;
				this.member = member;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTMemberLeaveTeam;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				if( member == null )
					member = new SBean.RoleOverview();
				is.pop(member);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.push(member);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public SBean.RoleOverview getMember()
			{
				return member;
			}

			public void setMember(SBean.RoleOverview member)
			{
				this.member = member;
			}

			private int roleID;
			private SBean.RoleOverview member;
		}

		public static class TeamMemberUpdateHpTrans extends SimplePacket
		{
			public TeamMemberUpdateHpTrans() { }

			public TeamMemberUpdateHpTrans(int roleID, int memberID, int memberHp, int memberHpMax)
			{
				this.roleID = roleID;
				this.memberID = memberID;
				this.memberHp = memberHp;
				this.memberHpMax = memberHpMax;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTTeamMemberUpdateHpTrans;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				memberID = is.popInteger();
				memberHp = is.popInteger();
				memberHpMax = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(memberID);
				os.pushInteger(memberHp);
				os.pushInteger(memberHpMax);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMemberID()
			{
				return memberID;
			}

			public void setMemberID(int memberID)
			{
				this.memberID = memberID;
			}

			public int getMemberHp()
			{
				return memberHp;
			}

			public void setMemberHp(int memberHp)
			{
				this.memberHp = memberHp;
			}

			public int getMemberHpMax()
			{
				return memberHpMax;
			}

			public void setMemberHpMax(int memberHpMax)
			{
				this.memberHpMax = memberHpMax;
			}

			private int roleID;
			private int memberID;
			private int memberHp;
			private int memberHpMax;
		}

		public static class FightTeamDissolve extends SimplePacket
		{
			public FightTeamDissolve() { }

			public FightTeamDissolve(int roleID)
			{
				this.roleID = roleID;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTFightTeamDissolve;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			private int roleID;
		}

		public static class QueryTeamMemberRes extends SimplePacket
		{
			public QueryTeamMemberRes() { }

			public QueryTeamMemberRes(int tagID, SBean.RoleProfile queryMember)
			{
				this.tagID = tagID;
				this.queryMember = queryMember;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTQueryTeamMemberRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				if( queryMember == null )
					queryMember = new SBean.RoleProfile();
				is.pop(queryMember);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.push(queryMember);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public SBean.RoleProfile getQueryMember()
			{
				return queryMember;
			}

			public void setQueryMember(SBean.RoleProfile queryMember)
			{
				this.queryMember = queryMember;
			}

			private int tagID;
			private SBean.RoleProfile queryMember;
		}

		public static class EnterSuperArenaRace extends SimplePacket
		{
			public EnterSuperArenaRace() { }

			public EnterSuperArenaRace(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTEnterSuperArenaRace;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class ForceWarMatchResult extends SimplePacket
		{
			public ForceWarMatchResult() { }

			public ForceWarMatchResult(int roleID, int result)
			{
				this.roleID = roleID;
				this.result = result;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTForceWarMatchResult;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				result = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(result);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getResult()
			{
				return result;
			}

			public void setResult(int result)
			{
				this.result = result;
			}

			private int roleID;
			private int result;
		}

		public static class TeamJoinForceWarRes extends SimplePacket
		{
			public TeamJoinForceWarRes() { }

			public TeamJoinForceWarRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTTeamJoinForceWarRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class TeamQuitForceWarRes extends SimplePacket
		{
			public TeamQuitForceWarRes() { }

			public TeamQuitForceWarRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTTeamQuitForceWarRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class SyncRoleDemonHoleRes extends SimplePacket
		{
			public SyncRoleDemonHoleRes() { }

			public SyncRoleDemonHoleRes(int tagID, int curFloor, int grade)
			{
				this.tagID = tagID;
				this.curFloor = curFloor;
				this.grade = grade;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncRoleDemonHoleRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				curFloor = is.popInteger();
				grade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(curFloor);
				os.pushInteger(grade);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getCurFloor()
			{
				return curFloor;
			}

			public void setCurFloor(int curFloor)
			{
				this.curFloor = curFloor;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			private int tagID;
			private int curFloor;
			private int grade;
		}

		public static class RoleJoinDemonHoleRes extends SimplePacket
		{
			public RoleJoinDemonHoleRes() { }

			public RoleJoinDemonHoleRes(int tagID, int grade)
			{
				this.tagID = tagID;
				this.grade = grade;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleJoinDemonHoleRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				grade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(grade);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			private int tagID;
			private int grade;
		}

		public static class RoleChangeDemonHoleFloorRes extends SimplePacket
		{
			public RoleChangeDemonHoleFloorRes() { }

			public RoleChangeDemonHoleFloorRes(int tagID, int ok)
			{
				this.tagID = tagID;
				this.ok = ok;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleChangeDemonHoleFloorRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				ok = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushInteger(ok);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public int getOk()
			{
				return ok;
			}

			public void setOk(int ok)
			{
				this.ok = ok;
			}

			private int tagID;
			private int ok;
		}

		public static class RoleDemonHoleBattleRes extends SimplePacket
		{
			public RoleDemonHoleBattleRes() { }

			public RoleDemonHoleBattleRes(int tagID, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
			{
				this.tagID = tagID;
				this.curFloor = curFloor;
				this.total = total;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleDemonHoleBattleRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				tagID = is.popInteger();
				curFloor = is.popList(SBean.RoleDemonHole.class);
				total = is.popList(SBean.RoleDemonHole.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(tagID);
				os.pushList(curFloor);
				os.pushList(total);
			}

			public int getTagID()
			{
				return tagID;
			}

			public void setTagID(int tagID)
			{
				this.tagID = tagID;
			}

			public List<SBean.RoleDemonHole> getCurFloor()
			{
				return curFloor;
			}

			public void setCurFloor(List<SBean.RoleDemonHole> curFloor)
			{
				this.curFloor = curFloor;
			}

			public List<SBean.RoleDemonHole> getTotal()
			{
				return total;
			}

			public void setTotal(List<SBean.RoleDemonHole> total)
			{
				this.total = total;
			}

			private int tagID;
			private List<SBean.RoleDemonHole> curFloor;
			private List<SBean.RoleDemonHole> total;
		}

		public static class RoleEnterDemonHoleMap extends SimplePacket
		{
			public RoleEnterDemonHoleMap() { }

			public RoleEnterDemonHoleMap(int roleID, int mapID, int mapInstance, int floor, 
			                             int grade)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.floor = floor;
				this.grade = grade;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTRoleEnterDemonHoleMap;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				floor = is.popInteger();
				grade = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(floor);
				os.pushInteger(grade);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getFloor()
			{
				return floor;
			}

			public void setFloor(int floor)
			{
				this.floor = floor;
			}

			public int getGrade()
			{
				return grade;
			}

			public void setGrade(int grade)
			{
				this.grade = grade;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private int floor;
			private int grade;
		}

		public static class SyncDemonHoleMapEnd extends SimplePacket
		{
			public SyncDemonHoleMapEnd() { }

			public SyncDemonHoleMapEnd(int mapID, int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.curFloor = curFloor;
				this.total = total;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncDemonHoleMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				curFloor = is.popList(SBean.RoleDemonHole.class);
				total = is.popList(SBean.RoleDemonHole.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushList(curFloor);
				os.pushList(total);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public List<SBean.RoleDemonHole> getCurFloor()
			{
				return curFloor;
			}

			public void setCurFloor(List<SBean.RoleDemonHole> curFloor)
			{
				this.curFloor = curFloor;
			}

			public List<SBean.RoleDemonHole> getTotal()
			{
				return total;
			}

			public void setTotal(List<SBean.RoleDemonHole> total)
			{
				this.total = total;
			}

			private int mapID;
			private int mapInstance;
			private List<SBean.RoleDemonHole> curFloor;
			private List<SBean.RoleDemonHole> total;
		}

		public static class SyncGSCreateNewTeam extends SimplePacket
		{
			public SyncGSCreateNewTeam() { }

			public SyncGSCreateNewTeam(List<Integer> members)
			{
				this.members = members;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncGSCreateNewTeam;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				members = is.popIntegerList();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushIntegerList(members);
			}

			public List<Integer> getMembers()
			{
				return members;
			}

			public void setMembers(List<Integer> members)
			{
				this.members = members;
			}

			private List<Integer> members;
		}

		public static class SyncRoleChatRoom extends SimplePacket
		{
			public SyncRoleChatRoom() { }

			public SyncRoleChatRoom(int roleID, int mapID, int mapInstance, String roomID)
			{
				this.roleID = roleID;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.roomID = roomID;
			}

			@Override
			public int getType()
			{
				return Packet.eF2SPKTSyncRoleChatRoom;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				roomID = is.popString();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushString(roomID);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public String getRoomID()
			{
				return roomID;
			}

			public void setRoomID(String roomID)
			{
				this.roomID = roomID;
			}

			private int roleID;
			private int mapID;
			private int mapInstance;
			private String roomID;
		}

	}

	// fs to global ms
	public static class F2GM
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eF2GMPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class CreateMapCopyReq extends SimplePacket
		{
			public CreateMapCopyReq() { }

			public CreateMapCopyReq(int mapType, int mapID, int mapInstance)
			{
				this.mapType = mapType;
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eF2GMPKTCreateMapCopyReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapType = is.popInteger();
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapType);
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapType()
			{
				return mapType;
			}

			public void setMapType(int mapType)
			{
				this.mapType = mapType;
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapType;
			private int mapID;
			private int mapInstance;
		}

		public static class EndMapCopy extends SimplePacket
		{
			public EndMapCopy() { }

			public EndMapCopy(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eF2GMPKTEndMapCopy;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

	}

	// global ms to fs
	public static class GM2F
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int areaId, Set<Integer> maps)
			{
				this.areaId = areaId;
				this.maps = maps;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				areaId = is.popInteger();
				maps = is.popIntegerTreeSet();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(areaId);
				os.pushIntegerSet(maps);
			}

			public int getAreaId()
			{
				return areaId;
			}

			public void setAreaId(int areaId)
			{
				this.areaId = areaId;
			}

			public Set<Integer> getMaps()
			{
				return maps;
			}

			public void setMaps(Set<Integer> maps)
			{
				this.maps = maps;
			}

			private int areaId;
			private Set<Integer> maps;
		}

		public static class CreateMapCopyRes extends SimplePacket
		{
			public CreateMapCopyRes() { }

			public CreateMapCopyRes(int mapType, int mapInstance)
			{
				this.mapType = mapType;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTCreateMapCopyRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapType = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapType);
				os.pushInteger(mapInstance);
			}

			public int getMapType()
			{
				return mapType;
			}

			public void setMapType(int mapType)
			{
				this.mapType = mapType;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapType;
			private int mapInstance;
		}

		public static class SyncForceWarMapEnd extends SimplePacket
		{
			public SyncForceWarMapEnd() { }

			public SyncForceWarMapEnd(int mapID, int mapInstance, int winSide, int killedBoss, 
			                          int whiteScore, int blackScore, Map<Integer, SBean.ForceWarOverview> whiteSide, Map<Integer, SBean.ForceWarOverview> blackSide)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.winSide = winSide;
				this.killedBoss = killedBoss;
				this.whiteScore = whiteScore;
				this.blackScore = blackScore;
				this.whiteSide = whiteSide;
				this.blackSide = blackSide;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTSyncForceWarMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				winSide = is.popInteger();
				killedBoss = is.popInteger();
				whiteScore = is.popInteger();
				blackScore = is.popInteger();
				whiteSide = is.popIntegerTreeMap(SBean.ForceWarOverview.class);
				blackSide = is.popIntegerTreeMap(SBean.ForceWarOverview.class);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(winSide);
				os.pushInteger(killedBoss);
				os.pushInteger(whiteScore);
				os.pushInteger(blackScore);
				os.pushIntegerMap(whiteSide);
				os.pushIntegerMap(blackSide);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getWinSide()
			{
				return winSide;
			}

			public void setWinSide(int winSide)
			{
				this.winSide = winSide;
			}

			public int getKilledBoss()
			{
				return killedBoss;
			}

			public void setKilledBoss(int killedBoss)
			{
				this.killedBoss = killedBoss;
			}

			public int getWhiteScore()
			{
				return whiteScore;
			}

			public void setWhiteScore(int whiteScore)
			{
				this.whiteScore = whiteScore;
			}

			public int getBlackScore()
			{
				return blackScore;
			}

			public void setBlackScore(int blackScore)
			{
				this.blackScore = blackScore;
			}

			public Map<Integer, SBean.ForceWarOverview> getWhiteSide()
			{
				return whiteSide;
			}

			public void setWhiteSide(Map<Integer, SBean.ForceWarOverview> whiteSide)
			{
				this.whiteSide = whiteSide;
			}

			public Map<Integer, SBean.ForceWarOverview> getBlackSide()
			{
				return blackSide;
			}

			public void setBlackSide(Map<Integer, SBean.ForceWarOverview> blackSide)
			{
				this.blackSide = blackSide;
			}

			private int mapID;
			private int mapInstance;
			private int winSide;
			private int killedBoss;
			private int whiteScore;
			private int blackScore;
			private Map<Integer, SBean.ForceWarOverview> whiteSide;
			private Map<Integer, SBean.ForceWarOverview> blackSide;
		}

		public static class SyncSuperArenaMapEnd extends SimplePacket
		{
			public SyncSuperArenaMapEnd() { }

			public SyncSuperArenaMapEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.result = result;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTSyncSuperArenaMapEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				if( result == null )
					result = new SBean.SuperArenaBattleResult();
				is.pop(result);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.push(result);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public SBean.SuperArenaBattleResult getResult()
			{
				return result;
			}

			public void setResult(SBean.SuperArenaBattleResult result)
			{
				this.result = result;
			}

			private int mapID;
			private int mapInstance;
			private SBean.SuperArenaBattleResult result;
		}

		public static class SyncHp extends SimplePacket
		{
			public SyncHp() { }

			public SyncHp(int roleID, int hp, int hpMax)
			{
				this.roleID = roleID;
				this.hp = hp;
				this.hpMax = hpMax;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTSyncHp;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				roleID = is.popInteger();
				hp = is.popInteger();
				hpMax = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(roleID);
				os.pushInteger(hp);
				os.pushInteger(hpMax);
			}

			public int getRoleID()
			{
				return roleID;
			}

			public void setRoleID(int roleID)
			{
				this.roleID = roleID;
			}

			public int getHp()
			{
				return hp;
			}

			public void setHp(int hp)
			{
				this.hp = hp;
			}

			public int getHpMax()
			{
				return hpMax;
			}

			public void setHpMax(int hpMax)
			{
				this.hpMax = hpMax;
			}

			private int roleID;
			private int hp;
			private int hpMax;
		}

		public static class SyncSuperArenaRaceEnd extends SimplePacket
		{
			public SyncSuperArenaRaceEnd() { }

			public SyncSuperArenaRaceEnd(int mapID, int mapInstance)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTSyncSuperArenaRaceEnd;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			private int mapID;
			private int mapInstance;
		}

		public static class SyncDemonHoleKill extends SimplePacket
		{
			public SyncDemonHoleKill() { }

			public SyncDemonHoleKill(int mapID, int mapInstance, int killerID, int deaderID)
			{
				this.mapID = mapID;
				this.mapInstance = mapInstance;
				this.killerID = killerID;
				this.deaderID = deaderID;
			}

			@Override
			public int getType()
			{
				return Packet.eGM2FPKTSyncDemonHoleKill;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				mapID = is.popInteger();
				mapInstance = is.popInteger();
				killerID = is.popInteger();
				deaderID = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(mapID);
				os.pushInteger(mapInstance);
				os.pushInteger(killerID);
				os.pushInteger(deaderID);
			}

			public int getMapID()
			{
				return mapID;
			}

			public void setMapID(int mapID)
			{
				this.mapID = mapID;
			}

			public int getMapInstance()
			{
				return mapInstance;
			}

			public void setMapInstance(int mapInstance)
			{
				this.mapInstance = mapInstance;
			}

			public int getKillerID()
			{
				return killerID;
			}

			public void setKillerID(int killerID)
			{
				this.killerID = killerID;
			}

			public int getDeaderID()
			{
				return deaderID;
			}

			public void setDeaderID(int deaderID)
			{
				this.deaderID = deaderID;
			}

			private int mapID;
			private int mapInstance;
			private int killerID;
			private int deaderID;
		}

	}

	// es to gs
	public static class E2S
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eE2SPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class ReceiveMsg extends SimplePacket
		{
			public ReceiveMsg() { }

			public ReceiveMsg(SBean.MessageInfo msg)
			{
				this.msg = msg;
			}

			@Override
			public int getType()
			{
				return Packet.eE2SPKTReceiveMsg;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( msg == null )
					msg = new SBean.MessageInfo();
				is.pop(msg);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(msg);
			}

			public SBean.MessageInfo getMsg()
			{
				return msg;
			}

			public void setMsg(SBean.MessageInfo msg)
			{
				this.msg = msg;
			}

			private SBean.MessageInfo msg;
		}

		public static class SocialMsgRes extends SimplePacket
		{
			public SocialMsgRes() { }

			public SocialMsgRes(SBean.ForwardData data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eE2SPKTSocialMsgRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( data == null )
					data = new SBean.ForwardData();
				is.pop(data);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(data);
			}

			public SBean.ForwardData getData()
			{
				return data;
			}

			public void setData(SBean.ForwardData data)
			{
				this.data = data;
			}

			private SBean.ForwardData data;
		}

	}

	// gs ms to es
	public static class S2E
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eS2EPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int serverId)
			{
				this.serverId = serverId;
			}

			@Override
			public int getType()
			{
				return Packet.eS2EPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				serverId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(serverId);
			}

			public int getServerId()
			{
				return serverId;
			}

			public void setServerId(int serverId)
			{
				this.serverId = serverId;
			}

			private int serverId;
		}

		public static class SendMsg extends SimplePacket
		{
			public SendMsg() { }

			public SendMsg(SBean.MessageInfo msg)
			{
				this.msg = msg;
			}

			@Override
			public int getType()
			{
				return Packet.eS2EPKTSendMsg;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( msg == null )
					msg = new SBean.MessageInfo();
				is.pop(msg);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(msg);
			}

			public SBean.MessageInfo getMsg()
			{
				return msg;
			}

			public void setMsg(SBean.MessageInfo msg)
			{
				this.msg = msg;
			}

			private SBean.MessageInfo msg;
		}

		public static class SocialMsgReq extends SimplePacket
		{
			public SocialMsgReq() { }

			public SocialMsgReq(SBean.ForwardData data)
			{
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eS2EPKTSocialMsgReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				if( data == null )
					data = new SBean.ForwardData();
				is.pop(data);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.push(data);
			}

			public SBean.ForwardData getData()
			{
				return data;
			}

			public void setData(SBean.ForwardData data)
			{
				this.data = data;
			}

			private SBean.ForwardData data;
		}

	}

	// es to ss
	public static class SS2E
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eSS2EPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class ForwardRes extends SimplePacket
		{
			public ForwardRes() { }

			public ForwardRes(int gsid, SBean.ForwardData data)
			{
				this.gsid = gsid;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eSS2EPKTForwardRes;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				gsid = is.popInteger();
				if( data == null )
					data = new SBean.ForwardData();
				is.pop(data);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(gsid);
				os.push(data);
			}

			public int getGsid()
			{
				return gsid;
			}

			public void setGsid(int gsid)
			{
				this.gsid = gsid;
			}

			public SBean.ForwardData getData()
			{
				return data;
			}

			public void setData(SBean.ForwardData data)
			{
				this.data = data;
			}

			private int gsid;
			private SBean.ForwardData data;
		}

	}

	// gs ms to ss
	public static class E2SS
	{

		public static class KeepAlive extends SimplePacket
		{
			public KeepAlive() { }

			public KeepAlive(int hello)
			{
				this.hello = hello;
			}

			@Override
			public int getType()
			{
				return Packet.eE2SSPKTKeepAlive;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				hello = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(hello);
			}

			public int getHello()
			{
				return hello;
			}

			public void setHello(int hello)
			{
				this.hello = hello;
			}

			private int hello;
		}

		public static class WhoAmI extends SimplePacket
		{
			public WhoAmI() { }

			public WhoAmI(int serverId)
			{
				this.serverId = serverId;
			}

			@Override
			public int getType()
			{
				return Packet.eE2SSPKTWhoAmI;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				serverId = is.popInteger();
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(serverId);
			}

			public int getServerId()
			{
				return serverId;
			}

			public void setServerId(int serverId)
			{
				this.serverId = serverId;
			}

			private int serverId;
		}

		public static class ForwardReq extends SimplePacket
		{
			public ForwardReq() { }

			public ForwardReq(int gsid, SBean.ForwardData data)
			{
				this.gsid = gsid;
				this.data = data;
			}

			@Override
			public int getType()
			{
				return Packet.eE2SSPKTForwardReq;
			}

			@Override
			public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
			{
				gsid = is.popInteger();
				if( data == null )
					data = new SBean.ForwardData();
				is.pop(data);
			}

			@Override
			public void encode(Stream.AOStream os)
			{
				os.pushInteger(gsid);
				os.push(data);
			}

			public int getGsid()
			{
				return gsid;
			}

			public void setGsid(int gsid)
			{
				this.gsid = gsid;
			}

			public SBean.ForwardData getData()
			{
				return data;
			}

			public void setData(SBean.ForwardData data)
			{
				this.data = data;
			}

			private int gsid;
			private SBean.ForwardData data;
		}

	}

}
