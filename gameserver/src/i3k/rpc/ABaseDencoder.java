// modified by ket.kio.RPCGen at Wed May 03 13:22:31 CST 2017.

package i3k.rpc;

import ket.kio.SimplePacket;
import ket.kio.SimpleDencoder;

public abstract class ABaseDencoder extends SimpleDencoder
{

	@Override
	public SimplePacket createPacket(int ptype)
	{
		SimplePacket packet = null;
		switch( ptype )
		{
		// server to client
		case Packet.eS2CPKTServerChallenge:
			packet = new Packet.S2C.ServerChallenge();
			break;
		case Packet.eS2CPKTServerResponse:
			packet = new Packet.S2C.ServerResponse();
			break;
		case Packet.eS2CPKTLuaChannel:
			packet = new Packet.S2C.LuaChannel();
			break;
		case Packet.eS2CPKTStrChannel:
			packet = new Packet.S2C.StrChannel();
			break;
		case Packet.eS2CPKTLuaChannel2:
			packet = new Packet.S2C.LuaChannel2();
			break;

		// client to server
		case Packet.eC2SPKTClientResponse:
			packet = new Packet.C2S.ClientResponse();
			break;
		case Packet.eC2SPKTLuaChannel:
			packet = new Packet.C2S.LuaChannel();
			break;
		case Packet.eC2SPKTLuaChannel2:
			packet = new Packet.C2S.LuaChannel2();
			break;
		case Packet.eC2SPKTStrChannel:
			packet = new Packet.C2S.StrChannel();
			break;

		// server to map
		case Packet.eS2MPKTKeepAlive:
			packet = new Packet.S2M.KeepAlive();
			break;
		case Packet.eS2MPKTSyncTimeOffset:
			packet = new Packet.S2M.SyncTimeOffset();
			break;
		case Packet.eS2MPKTLuaChannel:
			packet = new Packet.S2M.LuaChannel();
			break;
		case Packet.eS2MPKTStrChannel:
			packet = new Packet.S2M.StrChannel();
			break;
		case Packet.eS2MPKTSyncDoubleDropCfg:
			packet = new Packet.S2M.SyncDoubleDropCfg();
			break;
		case Packet.eS2MPKTSyncExtraDropCfg:
			packet = new Packet.S2M.SyncExtraDropCfg();
			break;
		case Packet.eS2MPKTSyncWorldNum:
			packet = new Packet.S2M.SyncWorldNum();
			break;
		case Packet.eS2MPKTStartMapCopy:
			packet = new Packet.S2M.StartMapCopy();
			break;
		case Packet.eS2MPKTEndMapCopy:
			packet = new Packet.S2M.EndMapCopy();
			break;
		case Packet.eS2MPKTMapCopyReady:
			packet = new Packet.S2M.MapCopyReady();
			break;
		case Packet.eS2MPKTResetSectMap:
			packet = new Packet.S2M.ResetSectMap();
			break;
		case Packet.eS2MPKTResetArenaMap:
			packet = new Packet.S2M.ResetArenaMap();
			break;
		case Packet.eS2MPKTResetBWArenaMap:
			packet = new Packet.S2M.ResetBWArenaMap();
			break;
		case Packet.eS2MPKTEnterMap:
			packet = new Packet.S2M.EnterMap();
			break;
		case Packet.eS2MPKTLeaveMap:
			packet = new Packet.S2M.LeaveMap();
			break;
		case Packet.eS2MPKTResetLocation:
			packet = new Packet.S2M.ResetLocation();
			break;
		case Packet.eS2MPKTUpdateActive:
			packet = new Packet.S2M.UpdateActive();
			break;
		case Packet.eS2MPKTUpdateEquip:
			packet = new Packet.S2M.UpdateEquip();
			break;
		case Packet.eS2MPKTUpdateEquipPart:
			packet = new Packet.S2M.UpdateEquipPart();
			break;
		case Packet.eS2MPKTUpdateSkill:
			packet = new Packet.S2M.UpdateSkill();
			break;
		case Packet.eS2MPKTUpdateCurSkills:
			packet = new Packet.S2M.UpdateCurSkills();
			break;
		case Packet.eS2MPKTUpdateBuff:
			packet = new Packet.S2M.UpdateBuff();
			break;
		case Packet.eS2MPKTUpdateLevel:
			packet = new Packet.S2M.UpdateLevel();
			break;
		case Packet.eS2MPKTAddHp:
			packet = new Packet.S2M.AddHp();
			break;
		case Packet.eS2MPKTUpdateWeapon:
			packet = new Packet.S2M.UpdateWeapon();
			break;
		case Packet.eS2MPKTUpdateCurWeapon:
			packet = new Packet.S2M.UpdateCurWeapon();
			break;
		case Packet.eS2MPKTUpdateSpirit:
			packet = new Packet.S2M.UpdateSpirit();
			break;
		case Packet.eS2MPKTUpdateCurSpirit:
			packet = new Packet.S2M.UpdateCurSpirit();
			break;
		case Packet.eS2MPKTStartMine:
			packet = new Packet.S2M.StartMine();
			break;
		case Packet.eS2MPKTRoleRevive:
			packet = new Packet.S2M.RoleRevive();
			break;
		case Packet.eS2MPKTUpdatePet:
			packet = new Packet.S2M.UpdatePet();
			break;
		case Packet.eS2MPKTUpdateTeam:
			packet = new Packet.S2M.UpdateTeam();
			break;
		case Packet.eS2MPKTChangeCurPets:
			packet = new Packet.S2M.ChangeCurPets();
			break;
		case Packet.eS2MPKTUpdateSectAura:
			packet = new Packet.S2M.UpdateSectAura();
			break;
		case Packet.eS2MPKTResetSectAuras:
			packet = new Packet.S2M.ResetSectAuras();
			break;
		case Packet.eS2MPKTUpdatePKInfo:
			packet = new Packet.S2M.UpdatePKInfo();
			break;
		case Packet.eS2MPKTUpdateCurDIYSkill:
			packet = new Packet.S2M.UpdateCurDIYSkill();
			break;
		case Packet.eS2MPKTUpdateTransformInfo:
			packet = new Packet.S2M.UpdateTransformInfo();
			break;
		case Packet.eS2MPKTCreateWorldMapBoss:
			packet = new Packet.S2M.CreateWorldMapBoss();
			break;
		case Packet.eS2MPKTDestroyWorldMapBoss:
			packet = new Packet.S2M.DestroyWorldMapBoss();
			break;
		case Packet.eS2MPKTInitWorldBoss:
			packet = new Packet.S2M.InitWorldBoss();
			break;
		case Packet.eS2MPKTCreateWorldMapSuperMonster:
			packet = new Packet.S2M.CreateWorldMapSuperMonster();
			break;
		case Packet.eS2MPKTCreateWorldMapMineral:
			packet = new Packet.S2M.CreateWorldMapMineral();
			break;
		case Packet.eS2MPKTGainNewSuite:
			packet = new Packet.S2M.GainNewSuite();
			break;
		case Packet.eS2MPKTUpdateSectBrief:
			packet = new Packet.S2M.UpdateSectBrief();
			break;
		case Packet.eS2MPKTUpdateHorseInfo:
			packet = new Packet.S2M.UpdateHorseInfo();
			break;
		case Packet.eS2MPKTUpdateCurUseHorse:
			packet = new Packet.S2M.UpdateCurUseHorse();
			break;
		case Packet.eS2MPKTUpdateMedal:
			packet = new Packet.S2M.UpdateMedal();
			break;
		case Packet.eS2MPKTUpWearFashion:
			packet = new Packet.S2M.UpWearFashion();
			break;
		case Packet.eS2MPKTUpdateAlterState:
			packet = new Packet.S2M.UpdateAlterState();
			break;
		case Packet.eS2MPKTChangeHorseShow:
			packet = new Packet.S2M.ChangeHorseShow();
			break;
		case Packet.eS2MPKTAddBuff:
			packet = new Packet.S2M.AddBuff();
			break;
		case Packet.eS2MPKTUpdateSealGrade:
			packet = new Packet.S2M.UpdateSealGrade();
			break;
		case Packet.eS2MPKTUpdateSealSkills:
			packet = new Packet.S2M.UpdateSealSkills();
			break;
		case Packet.eS2MPKTSyncRolePetLack:
			packet = new Packet.S2M.SyncRolePetLack();
			break;
		case Packet.eS2MPKTUpdateRoleGrasp:
			packet = new Packet.S2M.UpdateRoleGrasp();
			break;
		case Packet.eS2MPKTUpdateRareBook:
			packet = new Packet.S2M.UpdateRareBook();
			break;
		case Packet.eS2MPKTUpdateRoleTitle:
			packet = new Packet.S2M.UpdateRoleTitle();
			break;
		case Packet.eS2MPKTUpdateRoleCurTitle:
			packet = new Packet.S2M.UpdateRoleCurTitle();
			break;
		case Packet.eS2MPKTUpdatePetAchieve:
			packet = new Packet.S2M.UpdatePetAchieve();
			break;
		case Packet.eS2MPKTUpdateCurUniqueSkill:
			packet = new Packet.S2M.UpdateCurUniqueSkill();
			break;
		case Packet.eS2MPKTSetPetAlter:
			packet = new Packet.S2M.SetPetAlter();
			break;
		case Packet.eS2MPKTCarEnterMap:
			packet = new Packet.S2M.CarEnterMap();
			break;
		case Packet.eS2MPKTCarLeaveMap:
			packet = new Packet.S2M.CarLeaveMap();
			break;
		case Packet.eS2MPKTCarUpdateTeamCarCnt:
			packet = new Packet.S2M.CarUpdateTeamCarCnt();
			break;
		case Packet.eS2MPKTUpdateRoleCarBehavior:
			packet = new Packet.S2M.UpdateRoleCarBehavior();
			break;
		case Packet.eS2MPKTRoleUseItemSkill:
			packet = new Packet.S2M.RoleUseItemSkill();
			break;
		case Packet.eS2MPKTRoleRename:
			packet = new Packet.S2M.RoleRename();
			break;
		case Packet.eS2MPKTAddPetHp:
			packet = new Packet.S2M.AddPetHp();
			break;
		case Packet.eS2MPKTSyncCurRideHorse:
			packet = new Packet.S2M.SyncCurRideHorse();
			break;
		case Packet.eS2MPKTUpdateMulHorse:
			packet = new Packet.S2M.UpdateMulHorse();
			break;
		case Packet.eS2MPKTChangeArmor:
			packet = new Packet.S2M.ChangeArmor();
			break;
		case Packet.eS2MPKTUpdateArmorLevel:
			packet = new Packet.S2M.UpdateArmorLevel();
			break;
		case Packet.eS2MPKTUpdateArmorRank:
			packet = new Packet.S2M.UpdateArmorRank();
			break;
		case Packet.eS2MPKTUpdateArmorRune:
			packet = new Packet.S2M.UpdateArmorRune();
			break;
		case Packet.eS2MPKTUpdateTalentPoint:
			packet = new Packet.S2M.UpdateTalentPoint();
			break;
		case Packet.eS2MPKTSpawnSceneMonster:
			packet = new Packet.S2M.SpawnSceneMonster();
			break;
		case Packet.eS2MPKTClearSceneMonster:
			packet = new Packet.S2M.ClearSceneMonster();
			break;
		case Packet.eS2MPKTResetSectGroupMap:
			packet = new Packet.S2M.ResetSectGroupMap();
			break;
		case Packet.eS2MPKTUpdateHorseSkill:
			packet = new Packet.S2M.UpdateHorseSkill();
			break;
		case Packet.eS2MPKTUpdateStayWith:
			packet = new Packet.S2M.UpdateStayWith();
			break;
		case Packet.eS2MPKTUpdateRoleWeaponSkill:
			packet = new Packet.S2M.UpdateRoleWeaponSkill();
			break;
		case Packet.eS2MPKTUpdateRoleWeaponTalent:
			packet = new Packet.S2M.UpdateRoleWeaponTalent();
			break;
		case Packet.eS2MPKTCreateWorldMapFlag:
			packet = new Packet.S2M.CreateWorldMapFlag();
			break;
		case Packet.eS2MPKTInitWorldMapFlag:
			packet = new Packet.S2M.InitWorldMapFlag();
			break;
		case Packet.eS2MPKTSyncMapFlagInfo:
			packet = new Packet.S2M.SyncMapFlagInfo();
			break;
		case Packet.eS2MPKTSyncRoleItemProps:
			packet = new Packet.S2M.SyncRoleItemProps();
			break;
		case Packet.eS2MPKTSyncTaskDrop:
			packet = new Packet.S2M.SyncTaskDrop();
			break;
		case Packet.eS2MPKTUpdateRolePetSkill:
			packet = new Packet.S2M.UpdateRolePetSkill();
			break;
		case Packet.eS2MPKTSyncWeaponOpen:
			packet = new Packet.S2M.SyncWeaponOpen();
			break;
		case Packet.eS2MPKTWorldBossPop:
			packet = new Packet.S2M.WorldBossPop();
			break;
		case Packet.eS2MPKTPickUpResult:
			packet = new Packet.S2M.PickUpResult();
			break;
		case Packet.eS2MPKTUnSummonCurPets:
			packet = new Packet.S2M.UnSummonCurPets();
			break;
		case Packet.eS2MPKTUpdateRolePerfectDegree:
			packet = new Packet.S2M.UpdateRolePerfectDegree();
			break;
		case Packet.eS2MPKTUpdateCurPetSpirit:
			packet = new Packet.S2M.UpdateCurPetSpirit();
			break;
		case Packet.eS2MPKTStartMarriageParade:
			packet = new Packet.S2M.StartMarriageParade();
			break;
		case Packet.eS2MPKTUpdateRoleHeirloomDisplay:
			packet = new Packet.S2M.UpdateRoleHeirloomDisplay();
			break;
		case Packet.eS2MPKTSetWeaponForm:
			packet = new Packet.S2M.SetWeaponForm();
			break;
		case Packet.eS2MPKTStartMarriageBanquet:
			packet = new Packet.S2M.StartMarriageBanquet();
			break;
		case Packet.eS2MPKTUpdateRoleMarriageSkillInfo:
			packet = new Packet.S2M.UpdateRoleMarriageSkillInfo();
			break;
		case Packet.eS2MPKTUpdateRoleMarriageSkillLevel:
			packet = new Packet.S2M.UpdateRoleMarriageSkillLevel();
			break;
		case Packet.eS2MPKTMarriageLevelChange:
			packet = new Packet.S2M.MarriageLevelChange();
			break;
		case Packet.eS2MPKTRoleDMGTransferUpdate:
			packet = new Packet.S2M.RoleDMGTransferUpdate();
			break;
		case Packet.eS2MPKTCreateRobotHero:
			packet = new Packet.S2M.CreateRobotHero();
			break;
		case Packet.eS2MPKTDestroyRobotHero:
			packet = new Packet.S2M.DestroyRobotHero();
			break;
		case Packet.eS2MPKTSyncCreateStele:
			packet = new Packet.S2M.SyncCreateStele();
			break;
		case Packet.eS2MPKTSyncDestroyStele:
			packet = new Packet.S2M.SyncDestroyStele();
			break;
		case Packet.eS2MPKTSyncJusticeNpcShow:
			packet = new Packet.S2M.SyncJusticeNpcShow();
			break;
		case Packet.eS2MPKTSyncJusticeNpcLeave:
			packet = new Packet.S2M.SyncJusticeNpcLeave();
			break;
		case Packet.eS2MPKTSyncEmergencyLastTime:
			packet = new Packet.S2M.SyncEmergencyLastTime();
			break;
		case Packet.eS2MPKTSyncRoleVipLevel:
			packet = new Packet.S2M.SyncRoleVipLevel();
			break;
		case Packet.eS2MPKTSyncRoleCurWizardPet:
			packet = new Packet.S2M.SyncRoleCurWizardPet();
			break;
		case Packet.eS2MPKTUpdateRoleSpecialCardAttr:
			packet = new Packet.S2M.UpdateRoleSpecialCardAttr();
			break;
		case Packet.eS2MPKTRoleShowProps:
			packet = new Packet.S2M.RoleShowProps();
			break;
		case Packet.eS2MPKTRoleRedNamePunish:
			packet = new Packet.S2M.RoleRedNamePunish();
			break;
		case Packet.eS2MPKTGMCommand:
			packet = new Packet.S2M.GMCommand();
			break;

		// map to server
		case Packet.eM2SPKTKeepAlive:
			packet = new Packet.M2S.KeepAlive();
			break;
		case Packet.eM2SPKTWhoAmI:
			packet = new Packet.M2S.WhoAmI();
			break;
		case Packet.eM2SPKTLuaChannel:
			packet = new Packet.M2S.LuaChannel();
			break;
		case Packet.eM2SPKTStrChannel:
			packet = new Packet.M2S.StrChannel();
			break;
		case Packet.eM2SPKTStrChannelBroadcast:
			packet = new Packet.M2S.StrChannelBroadcast();
			break;
		case Packet.eM2SPKTMapRoleReady:
			packet = new Packet.M2S.MapRoleReady();
			break;
		case Packet.eM2SPKTNearByRoleMove:
			packet = new Packet.M2S.NearByRoleMove();
			break;
		case Packet.eM2SPKTNearByRoleStopMove:
			packet = new Packet.M2S.NearByRoleStopMove();
			break;
		case Packet.eM2SPKTNearByRoleEnter:
			packet = new Packet.M2S.NearByRoleEnter();
			break;
		case Packet.eM2SPKTNearByRoleLeave:
			packet = new Packet.M2S.NearByRoleLeave();
			break;
		case Packet.eM2SPKTSyncCommonMapCopyStart:
			packet = new Packet.M2S.SyncCommonMapCopyStart();
			break;
		case Packet.eM2SPKTSyncCommonMapCopyEnd:
			packet = new Packet.M2S.SyncCommonMapCopyEnd();
			break;
		case Packet.eM2SPKTSyncSectMapCopyStart:
			packet = new Packet.M2S.SyncSectMapCopyStart();
			break;
		case Packet.eM2SPKTSyncSectMapCopyProgress:
			packet = new Packet.M2S.SyncSectMapCopyProgress();
			break;
		case Packet.eM2SPKTSyncArenaMapCopyStart:
			packet = new Packet.M2S.SyncArenaMapCopyStart();
			break;
		case Packet.eM2SPKTSyncArenaMapCopyEnd:
			packet = new Packet.M2S.SyncArenaMapCopyEnd();
			break;
		case Packet.eM2SPKTSyncBWArenaMapCopyStart:
			packet = new Packet.M2S.SyncBWArenaMapCopyStart();
			break;
		case Packet.eM2SPKTSyncBWArenaMapCopyEnd:
			packet = new Packet.M2S.SyncBWArenaMapCopyEnd();
			break;
		case Packet.eM2SPKTSyncPetLifeMapCopyStart:
			packet = new Packet.M2S.SyncPetLifeMapCopyStart();
			break;
		case Packet.eM2SPKTSyncLocation:
			packet = new Packet.M2S.SyncLocation();
			break;
		case Packet.eM2SPKTSyncHp:
			packet = new Packet.M2S.SyncHp();
			break;
		case Packet.eM2SPKTAddDrops:
			packet = new Packet.M2S.AddDrops();
			break;
		case Packet.eM2SPKTAddKill:
			packet = new Packet.M2S.AddKill();
			break;
		case Packet.eM2SPKTSyncDurability:
			packet = new Packet.M2S.SyncDurability();
			break;
		case Packet.eM2SPKTSyncEndMine:
			packet = new Packet.M2S.SyncEndMine();
			break;
		case Packet.eM2SPKTAddPKValue:
			packet = new Packet.M2S.AddPKValue();
			break;
		case Packet.eM2SPKTSyncWorldMapBossProgress:
			packet = new Packet.M2S.SyncWorldMapBossProgress();
			break;
		case Packet.eM2SPKTSyncCurRideHorse:
			packet = new Packet.M2S.SyncCurRideHorse();
			break;
		case Packet.eM2SPKTSyncCarLocation:
			packet = new Packet.M2S.SyncCarLocation();
			break;
		case Packet.eM2SPKTSyncCarHp:
			packet = new Packet.M2S.SyncCarHp();
			break;
		case Packet.eM2SPKTUpdateCarDamage:
			packet = new Packet.M2S.UpdateCarDamage();
			break;
		case Packet.eM2SPKTSyncRoleRobSuccess:
			packet = new Packet.M2S.SyncRoleRobSuccess();
			break;
		case Packet.eM2SPKTUpdateRoleCarRobber:
			packet = new Packet.M2S.UpdateRoleCarRobber();
			break;
		case Packet.eM2SPKTKickRoleFromMap:
			packet = new Packet.M2S.KickRoleFromMap();
			break;
		case Packet.eM2SPKTRoleUseItemSkillSuc:
			packet = new Packet.M2S.RoleUseItemSkillSuc();
			break;
		case Packet.eM2SPKTUpdateRoleFightState:
			packet = new Packet.M2S.UpdateRoleFightState();
			break;
		case Packet.eM2SPKTSyncRolePetHp:
			packet = new Packet.M2S.SyncRolePetHp();
			break;
		case Packet.eM2SPKTSyncRoleSp:
			packet = new Packet.M2S.SyncRoleSp();
			break;
		case Packet.eM2SPKTSyncWorldMapBossRecord:
			packet = new Packet.M2S.SyncWorldMapBossRecord();
			break;
		case Packet.eM2SPKTSyncArmorVal:
			packet = new Packet.M2S.SyncArmorVal();
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyStatus:
			packet = new Packet.M2S.SyncSectGroupMapCopyStatus();
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyResult:
			packet = new Packet.M2S.SyncSectGroupMapCopyResult();
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyStart:
			packet = new Packet.M2S.SyncSectGroupMapCopyStart();
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyProgress:
			packet = new Packet.M2S.SyncSectGroupMapCopyProgress();
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyAddKill:
			packet = new Packet.M2S.SyncSectGroupMapCopyAddKill();
			break;
		case Packet.eM2SPKTSyncMapFlagCanTake:
			packet = new Packet.M2S.SyncMapFlagCanTake();
			break;
		case Packet.eM2SPKTSyncWeaponMaster:
			packet = new Packet.M2S.SyncWeaponMaster();
			break;
		case Packet.eM2SPKTRolePickUpDrops:
			packet = new Packet.M2S.RolePickUpDrops();
			break;
		case Packet.eM2SPKTSyncSuperMonster:
			packet = new Packet.M2S.SyncSuperMonster();
			break;
		case Packet.eM2SPKTSyncWorldMineral:
			packet = new Packet.M2S.SyncWorldMineral();
			break;
		case Packet.eM2SPKTSyncMarriageParadeEnd:
			packet = new Packet.M2S.SyncMarriageParadeEnd();
			break;
		case Packet.eM2SPKTRolePickUpRareDrops:
			packet = new Packet.M2S.RolePickUpRareDrops();
			break;
		case Packet.eM2SPKTSyncWorldBossDamageRoles:
			packet = new Packet.M2S.SyncWorldBossDamageRoles();
			break;
		case Packet.eM2SPKTSyncSteleRemainTimes:
			packet = new Packet.M2S.SyncSteleRemainTimes();
			break;
		case Packet.eM2SPKTSyncRoleAddSteleCard:
			packet = new Packet.M2S.SyncRoleAddSteleCard();
			break;
		case Packet.eM2SPKTSyncRefreshSteleMonster:
			packet = new Packet.M2S.SyncRefreshSteleMonster();
			break;
		case Packet.eM2SPKTSyncEmergencyMapStart:
			packet = new Packet.M2S.SyncEmergencyMapStart();
			break;
		case Packet.eM2SPKTSyncEmergencyMapKillMonster:
			packet = new Packet.M2S.SyncEmergencyMapKillMonster();
			break;
		case Packet.eM2SPKTSyncEmergencyMapEnd:
			packet = new Packet.M2S.SyncEmergencyMapEnd();
			break;
		case Packet.eM2SPKTSyncFightNpcMapStart:
			packet = new Packet.M2S.SyncFightNpcMapStart();
			break;
		case Packet.eM2SPKTSyncFightNpcMapEnd:
			packet = new Packet.M2S.SyncFightNpcMapEnd();
			break;
		case Packet.eM2SPKTSyncTowerDefenceMapStart:
			packet = new Packet.M2S.SyncTowerDefenceMapStart();
			break;
		case Packet.eM2SPKTSyncTowerDefenceMapEnd:
			packet = new Packet.M2S.SyncTowerDefenceMapEnd();
			break;
		case Packet.eM2SPKTSyncTowerDefenceSpawnCount:
			packet = new Packet.M2S.SyncTowerDefenceSpawnCount();
			break;
		case Packet.eM2SPKTSyncTowerDefenceScore:
			packet = new Packet.M2S.SyncTowerDefenceScore();
			break;

		// server to auction
		case Packet.eS2AuctionPKTKeepAlive:
			packet = new Packet.S2Auction.KeepAlive();
			break;
		case Packet.eS2AuctionPKTWhoAmI:
			packet = new Packet.S2Auction.WhoAmI();
			break;
		case Packet.eS2AuctionPKTReportTimeOffset:
			packet = new Packet.S2Auction.ReportTimeOffset();
			break;
		case Packet.eS2AuctionPKTPutOnItemReq:
			packet = new Packet.S2Auction.PutOnItemReq();
			break;
		case Packet.eS2AuctionPKTTimeOutPutOffItemsRes:
			packet = new Packet.S2Auction.TimeOutPutOffItemsRes();
			break;
		case Packet.eS2AuctionPKTPutOffItemsReq:
			packet = new Packet.S2Auction.PutOffItemsReq();
			break;
		case Packet.eS2AuctionPKTBuyItemsReq:
			packet = new Packet.S2Auction.BuyItemsReq();
			break;
		case Packet.eS2AuctionPKTCheckCanBuyRes:
			packet = new Packet.S2Auction.CheckCanBuyRes();
			break;
		case Packet.eS2AuctionPKTAuctionItemsSyncReq:
			packet = new Packet.S2Auction.AuctionItemsSyncReq();
			break;
		case Packet.eS2AuctionPKTSelfItemsSyncReq:
			packet = new Packet.S2Auction.SelfItemsSyncReq();
			break;
		case Packet.eS2AuctionPKTItemPricesSyncReq:
			packet = new Packet.S2Auction.ItemPricesSyncReq();
			break;
		case Packet.eS2AuctionPKTUpdateGroupBuyGoods:
			packet = new Packet.S2Auction.UpdateGroupBuyGoods();
			break;
		case Packet.eS2AuctionPKTSyncGroupBuyGoods:
			packet = new Packet.S2Auction.SyncGroupBuyGoods();
			break;

		// auction to server
		case Packet.eAuction2SPKTKeepAlive:
			packet = new Packet.Auction2S.KeepAlive();
			break;
		case Packet.eAuction2SPKTAdjustTimeOffset:
			packet = new Packet.Auction2S.AdjustTimeOffset();
			break;
		case Packet.eAuction2SPKTPutOnItemRes:
			packet = new Packet.Auction2S.PutOnItemRes();
			break;
		case Packet.eAuction2SPKTTimeOutPutOffItemsReq:
			packet = new Packet.Auction2S.TimeOutPutOffItemsReq();
			break;
		case Packet.eAuction2SPKTPutOffItemsRes:
			packet = new Packet.Auction2S.PutOffItemsRes();
			break;
		case Packet.eAuction2SPKTBuyItemsRes:
			packet = new Packet.Auction2S.BuyItemsRes();
			break;
		case Packet.eAuction2SPKTCheckCanBuyReq:
			packet = new Packet.Auction2S.CheckCanBuyReq();
			break;
		case Packet.eAuction2SPKTAuctionItemsSyncRes:
			packet = new Packet.Auction2S.AuctionItemsSyncRes();
			break;
		case Packet.eAuction2SPKTSelfItemsSyncRes:
			packet = new Packet.Auction2S.SelfItemsSyncRes();
			break;
		case Packet.eAuction2SPKTItemPricesSyncRes:
			packet = new Packet.Auction2S.ItemPricesSyncRes();
			break;
		case Packet.eAuction2SPKTSyncGroupBuyLog:
			packet = new Packet.Auction2S.SyncGroupBuyLog();
			break;

		// server to auth
		case Packet.eS2AUPKTKeepAlive:
			packet = new Packet.S2AU.KeepAlive();
			break;
		case Packet.eS2AUPKTWhoAmI:
			packet = new Packet.S2AU.WhoAmI();
			break;
		case Packet.eS2AUPKTPayRes:
			packet = new Packet.S2AU.PayRes();
			break;

		// auth to server
		case Packet.eAU2SPKTKeepAlive:
			packet = new Packet.AU2S.KeepAlive();
			break;
		case Packet.eAU2SPKTPayReq:
			packet = new Packet.AU2S.PayReq();
			break;

		// gs to global ms
		case Packet.eS2GMPKTKeepAlive:
			packet = new Packet.S2GM.KeepAlive();
			break;
		case Packet.eS2GMPKTWhoAmI:
			packet = new Packet.S2GM.WhoAmI();
			break;
		case Packet.eS2GMPKTReportTimeOffset:
			packet = new Packet.S2GM.ReportTimeOffset();
			break;
		case Packet.eS2GMPKTLuaChannel:
			packet = new Packet.S2GM.LuaChannel();
			break;
		case Packet.eS2GMPKTStrChannel:
			packet = new Packet.S2GM.StrChannel();
			break;
		case Packet.eS2GMPKTEnterMap:
			packet = new Packet.S2GM.EnterMap();
			break;
		case Packet.eS2GMPKTLeaveMap:
			packet = new Packet.S2GM.LeaveMap();
			break;
		case Packet.eS2GMPKTUpdateActive:
			packet = new Packet.S2GM.UpdateActive();
			break;
		case Packet.eS2GMPKTAddHp:
			packet = new Packet.S2GM.AddHp();
			break;
		case Packet.eS2GMPKTRoleUseItemSkill:
			packet = new Packet.S2GM.RoleUseItemSkill();
			break;
		case Packet.eS2GMPKTAddPetHp:
			packet = new Packet.S2GM.AddPetHp();
			break;
		case Packet.eS2GMPKTStartMine:
			packet = new Packet.S2GM.StartMine();
			break;
		case Packet.eS2GMPKTResetLocation:
			packet = new Packet.S2GM.ResetLocation();
			break;
		case Packet.eS2GMPKTUpdateCurSkills:
			packet = new Packet.S2GM.UpdateCurSkills();
			break;
		case Packet.eS2GMPKTUpdateCurSpirit:
			packet = new Packet.S2GM.UpdateCurSpirit();
			break;
		case Packet.eS2GMPKTPickUpResult:
			packet = new Packet.S2GM.PickUpResult();
			break;
		case Packet.eS2GMPKTUpdateRoleMarriageSkillInfo:
			packet = new Packet.S2GM.UpdateRoleMarriageSkillInfo();
			break;
		case Packet.eS2GMPKTUpdateRoleMarriageSkillLevel:
			packet = new Packet.S2GM.UpdateRoleMarriageSkillLevel();
			break;
		case Packet.eS2GMPKTRoleRevive:
			packet = new Packet.S2GM.RoleRevive();
			break;

		// global ms to gs
		case Packet.eGM2SPKTKeepAlive:
			packet = new Packet.GM2S.KeepAlive();
			break;
		case Packet.eGM2SPKTSyncGlobalMaps:
			packet = new Packet.GM2S.SyncGlobalMaps();
			break;
		case Packet.eGM2SPKTLuaChannel:
			packet = new Packet.GM2S.LuaChannel();
			break;
		case Packet.eGM2SPKTStrChannel:
			packet = new Packet.GM2S.StrChannel();
			break;
		case Packet.eGM2SPKTMapRoleReady:
			packet = new Packet.GM2S.MapRoleReady();
			break;
		case Packet.eGM2SPKTSyncLocation:
			packet = new Packet.GM2S.SyncLocation();
			break;
		case Packet.eGM2SPKTSyncHp:
			packet = new Packet.GM2S.SyncHp();
			break;
		case Packet.eGM2SPKTAddDrops:
			packet = new Packet.GM2S.AddDrops();
			break;
		case Packet.eGM2SPKTAddKill:
			packet = new Packet.GM2S.AddKill();
			break;
		case Packet.eGM2SPKTSyncDurability:
			packet = new Packet.GM2S.SyncDurability();
			break;
		case Packet.eGM2SPKTSyncEndMine:
			packet = new Packet.GM2S.SyncEndMine();
			break;
		case Packet.eGM2SPKTKickRoleFromMap:
			packet = new Packet.GM2S.KickRoleFromMap();
			break;
		case Packet.eGM2SPKTRoleUseItemSkillSuc:
			packet = new Packet.GM2S.RoleUseItemSkillSuc();
			break;
		case Packet.eGM2SPKTUpdateRoleFightState:
			packet = new Packet.GM2S.UpdateRoleFightState();
			break;
		case Packet.eGM2SPKTSyncRolePetHp:
			packet = new Packet.GM2S.SyncRolePetHp();
			break;
		case Packet.eGM2SPKTSyncArmorVal:
			packet = new Packet.GM2S.SyncArmorVal();
			break;
		case Packet.eGM2SPKTSyncWeaponMaster:
			packet = new Packet.GM2S.SyncWeaponMaster();
			break;
		case Packet.eGM2SPKTRolePickUpDrops:
			packet = new Packet.GM2S.RolePickUpDrops();
			break;
		case Packet.eGM2SPKTRolePickUpRareDrops:
			packet = new Packet.GM2S.RolePickUpRareDrops();
			break;

		// gs to fs
		case Packet.eS2FPKTKeepAlive:
			packet = new Packet.S2F.KeepAlive();
			break;
		case Packet.eS2FPKTWhoAmI:
			packet = new Packet.S2F.WhoAmI();
			break;
		case Packet.eS2FPKTReportTimeOffset:
			packet = new Packet.S2F.ReportTimeOffset();
			break;
		case Packet.eS2FPKTRoleJoinForceWarReq:
			packet = new Packet.S2F.RoleJoinForceWarReq();
			break;
		case Packet.eS2FPKTRoleQuitForceWarReq:
			packet = new Packet.S2F.RoleQuitForceWarReq();
			break;
		case Packet.eS2FPKTUpdateFightRank:
			packet = new Packet.S2F.UpdateFightRank();
			break;
		case Packet.eS2FPKTSendMsgFight:
			packet = new Packet.S2F.SendMsgFight();
			break;
		case Packet.eS2FPKTSingleJoinSuperArenaReq:
			packet = new Packet.S2F.SingleJoinSuperArenaReq();
			break;
		case Packet.eS2FPKTSingleQuitSuperArenaReq:
			packet = new Packet.S2F.SingleQuitSuperArenaReq();
			break;
		case Packet.eS2FPKTTeamJoinSuperArenaReq:
			packet = new Packet.S2F.TeamJoinSuperArenaReq();
			break;
		case Packet.eS2FPKTTeamQuitSuperArenaReq:
			packet = new Packet.S2F.TeamQuitSuperArenaReq();
			break;
		case Packet.eS2FPKTQueryTeamMembersReq:
			packet = new Packet.S2F.QueryTeamMembersReq();
			break;
		case Packet.eS2FPKTRoleLeaveTeam:
			packet = new Packet.S2F.RoleLeaveTeam();
			break;
		case Packet.eS2FPKTQueryTeamMemberReq:
			packet = new Packet.S2F.QueryTeamMemberReq();
			break;
		case Packet.eS2FPKTLeaveMap:
			packet = new Packet.S2F.LeaveMap();
			break;
		case Packet.eS2FPKTSendMsgGlobalTeam:
			packet = new Packet.S2F.SendMsgGlobalTeam();
			break;
		case Packet.eS2FPKTTeamJoinForceWarReq:
			packet = new Packet.S2F.TeamJoinForceWarReq();
			break;
		case Packet.eS2FPKTTeamQuitForceWarReq:
			packet = new Packet.S2F.TeamQuitForceWarReq();
			break;
		case Packet.eS2FPKTSyncRoleDemonHoleReq:
			packet = new Packet.S2F.SyncRoleDemonHoleReq();
			break;
		case Packet.eS2FPKTRoleJoinDemonHoleReq:
			packet = new Packet.S2F.RoleJoinDemonHoleReq();
			break;
		case Packet.eS2FPKTRoleChangeDemonHoleFloorReq:
			packet = new Packet.S2F.RoleChangeDemonHoleFloorReq();
			break;
		case Packet.eS2FPKTRoleDemonHoleBattleReq:
			packet = new Packet.S2F.RoleDemonHoleBattleReq();
			break;
		case Packet.eS2FPKTRoleEnterDemonHoleFloor:
			packet = new Packet.S2F.RoleEnterDemonHoleFloor();
			break;

		// fs to gs
		case Packet.eF2SPKTKeepAlive:
			packet = new Packet.F2S.KeepAlive();
			break;
		case Packet.eF2SPKTSyncGSRankStart:
			packet = new Packet.F2S.SyncGSRankStart();
			break;
		case Packet.eF2SPKTSyncGSRank:
			packet = new Packet.F2S.SyncGSRank();
			break;
		case Packet.eF2SPKTSyncGSRankEnd:
			packet = new Packet.F2S.SyncGSRankEnd();
			break;
		case Packet.eF2SPKTRoleJoinForceWarRes:
			packet = new Packet.F2S.RoleJoinForceWarRes();
			break;
		case Packet.eF2SPKTRoleQuitForceWarRes:
			packet = new Packet.F2S.RoleQuitForceWarRes();
			break;
		case Packet.eF2SPKTRoleEnterForceWar:
			packet = new Packet.F2S.RoleEnterForceWar();
			break;
		case Packet.eF2SPKTSyncForceWarMapStart:
			packet = new Packet.F2S.SyncForceWarMapStart();
			break;
		case Packet.eF2SPKTSyncForceWarMapEnd:
			packet = new Packet.F2S.SyncForceWarMapEnd();
			break;
		case Packet.eF2SPKTSyncMapCopyTimeOut:
			packet = new Packet.F2S.SyncMapCopyTimeOut();
			break;
		case Packet.eF2SPKTReceiveMsgFight:
			packet = new Packet.F2S.ReceiveMsgFight();
			break;
		case Packet.eF2SPKTSingleJoinSuperArenaRes:
			packet = new Packet.F2S.SingleJoinSuperArenaRes();
			break;
		case Packet.eF2SPKTSingleQuitSuperArenaRes:
			packet = new Packet.F2S.SingleQuitSuperArenaRes();
			break;
		case Packet.eF2SPKTTeamJoinSuperArenaRes:
			packet = new Packet.F2S.TeamJoinSuperArenaRes();
			break;
		case Packet.eF2SPKTTeamQuitSuperArenaRes:
			packet = new Packet.F2S.TeamQuitSuperArenaRes();
			break;
		case Packet.eF2SPKTQueryTeamMembersRes:
			packet = new Packet.F2S.QueryTeamMembersRes();
			break;
		case Packet.eF2SPKTCreateMapCopy:
			packet = new Packet.F2S.CreateMapCopy();
			break;
		case Packet.eF2SPKTRoleEnterSuperArena:
			packet = new Packet.F2S.RoleEnterSuperArena();
			break;
		case Packet.eF2SPKTSyncSuperArenaStart:
			packet = new Packet.F2S.SyncSuperArenaStart();
			break;
		case Packet.eF2SPKTSyncSuperArenaMapEnd:
			packet = new Packet.F2S.SyncSuperArenaMapEnd();
			break;
		case Packet.eF2SPKTSuperArenaMatchResult:
			packet = new Packet.F2S.SuperArenaMatchResult();
			break;
		case Packet.eF2SPKTSyncRoleFightTeam:
			packet = new Packet.F2S.SyncRoleFightTeam();
			break;
		case Packet.eF2SPKTTeamLeaderChange:
			packet = new Packet.F2S.TeamLeaderChange();
			break;
		case Packet.eF2SPKTMemberLeaveTeam:
			packet = new Packet.F2S.MemberLeaveTeam();
			break;
		case Packet.eF2SPKTTeamMemberUpdateHpTrans:
			packet = new Packet.F2S.TeamMemberUpdateHpTrans();
			break;
		case Packet.eF2SPKTFightTeamDissolve:
			packet = new Packet.F2S.FightTeamDissolve();
			break;
		case Packet.eF2SPKTQueryTeamMemberRes:
			packet = new Packet.F2S.QueryTeamMemberRes();
			break;
		case Packet.eF2SPKTEnterSuperArenaRace:
			packet = new Packet.F2S.EnterSuperArenaRace();
			break;
		case Packet.eF2SPKTForceWarMatchResult:
			packet = new Packet.F2S.ForceWarMatchResult();
			break;
		case Packet.eF2SPKTTeamJoinForceWarRes:
			packet = new Packet.F2S.TeamJoinForceWarRes();
			break;
		case Packet.eF2SPKTTeamQuitForceWarRes:
			packet = new Packet.F2S.TeamQuitForceWarRes();
			break;
		case Packet.eF2SPKTSyncRoleDemonHoleRes:
			packet = new Packet.F2S.SyncRoleDemonHoleRes();
			break;
		case Packet.eF2SPKTRoleJoinDemonHoleRes:
			packet = new Packet.F2S.RoleJoinDemonHoleRes();
			break;
		case Packet.eF2SPKTRoleChangeDemonHoleFloorRes:
			packet = new Packet.F2S.RoleChangeDemonHoleFloorRes();
			break;
		case Packet.eF2SPKTRoleDemonHoleBattleRes:
			packet = new Packet.F2S.RoleDemonHoleBattleRes();
			break;
		case Packet.eF2SPKTRoleEnterDemonHoleMap:
			packet = new Packet.F2S.RoleEnterDemonHoleMap();
			break;
		case Packet.eF2SPKTSyncDemonHoleMapEnd:
			packet = new Packet.F2S.SyncDemonHoleMapEnd();
			break;
		case Packet.eF2SPKTSyncGSCreateNewTeam:
			packet = new Packet.F2S.SyncGSCreateNewTeam();
			break;
		case Packet.eF2SPKTSyncRoleChatRoom:
			packet = new Packet.F2S.SyncRoleChatRoom();
			break;

		// fs to global ms
		case Packet.eF2GMPKTKeepAlive:
			packet = new Packet.F2GM.KeepAlive();
			break;
		case Packet.eF2GMPKTCreateMapCopyReq:
			packet = new Packet.F2GM.CreateMapCopyReq();
			break;
		case Packet.eF2GMPKTEndMapCopy:
			packet = new Packet.F2GM.EndMapCopy();
			break;

		// global ms to fs
		case Packet.eGM2FPKTKeepAlive:
			packet = new Packet.GM2F.KeepAlive();
			break;
		case Packet.eGM2FPKTWhoAmI:
			packet = new Packet.GM2F.WhoAmI();
			break;
		case Packet.eGM2FPKTCreateMapCopyRes:
			packet = new Packet.GM2F.CreateMapCopyRes();
			break;
		case Packet.eGM2FPKTSyncForceWarMapEnd:
			packet = new Packet.GM2F.SyncForceWarMapEnd();
			break;
		case Packet.eGM2FPKTSyncSuperArenaMapEnd:
			packet = new Packet.GM2F.SyncSuperArenaMapEnd();
			break;
		case Packet.eGM2FPKTSyncHp:
			packet = new Packet.GM2F.SyncHp();
			break;
		case Packet.eGM2FPKTSyncSuperArenaRaceEnd:
			packet = new Packet.GM2F.SyncSuperArenaRaceEnd();
			break;
		case Packet.eGM2FPKTSyncDemonHoleKill:
			packet = new Packet.GM2F.SyncDemonHoleKill();
			break;

		// es to gs
		case Packet.eE2SPKTKeepAlive:
			packet = new Packet.E2S.KeepAlive();
			break;
		case Packet.eE2SPKTReceiveMsg:
			packet = new Packet.E2S.ReceiveMsg();
			break;
		case Packet.eE2SPKTSocialMsgRes:
			packet = new Packet.E2S.SocialMsgRes();
			break;

		// gs ms to es
		case Packet.eS2EPKTKeepAlive:
			packet = new Packet.S2E.KeepAlive();
			break;
		case Packet.eS2EPKTWhoAmI:
			packet = new Packet.S2E.WhoAmI();
			break;
		case Packet.eS2EPKTSendMsg:
			packet = new Packet.S2E.SendMsg();
			break;
		case Packet.eS2EPKTSocialMsgReq:
			packet = new Packet.S2E.SocialMsgReq();
			break;

		// es to ss
		case Packet.eSS2EPKTKeepAlive:
			packet = new Packet.SS2E.KeepAlive();
			break;
		case Packet.eSS2EPKTForwardRes:
			packet = new Packet.SS2E.ForwardRes();
			break;

		// gs ms to ss
		case Packet.eE2SSPKTKeepAlive:
			packet = new Packet.E2SS.KeepAlive();
			break;
		case Packet.eE2SSPKTWhoAmI:
			packet = new Packet.E2SS.WhoAmI();
			break;
		case Packet.eE2SSPKTForwardReq:
			packet = new Packet.E2SS.ForwardReq();
			break;

		default:
			break;
		}
		return packet;
	}

	@Override
	public boolean doCheckPacketType(int ptype)
	{
		return true;
	}
}
