// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gmap;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManager
{

	//// begin handlers.
	public void onTCPMapClientOpen(TCPMapClient peer)
	{
		// TODO
	}

	public void onTCPMapClientOpenFailed(TCPMapClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPMapClientClose(TCPMapClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPMapClientRecvKeepAlive(TCPMapClient peer, Packet.S2M.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncTimeOffset(TCPMapClient peer, Packet.S2M.SyncTimeOffset packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvLuaChannel(TCPMapClient peer, Packet.S2M.LuaChannel packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvStrChannel(TCPMapClient peer, Packet.S2M.StrChannel packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncDoubleDropCfg(TCPMapClient peer, Packet.S2M.SyncDoubleDropCfg packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncExtraDropCfg(TCPMapClient peer, Packet.S2M.SyncExtraDropCfg packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncWorldNum(TCPMapClient peer, Packet.S2M.SyncWorldNum packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvStartMapCopy(TCPMapClient peer, Packet.S2M.StartMapCopy packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvEndMapCopy(TCPMapClient peer, Packet.S2M.EndMapCopy packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvMapCopyReady(TCPMapClient peer, Packet.S2M.MapCopyReady packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvResetSectMap(TCPMapClient peer, Packet.S2M.ResetSectMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvResetArenaMap(TCPMapClient peer, Packet.S2M.ResetArenaMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvResetBWArenaMap(TCPMapClient peer, Packet.S2M.ResetBWArenaMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvEnterMap(TCPMapClient peer, Packet.S2M.EnterMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvLeaveMap(TCPMapClient peer, Packet.S2M.LeaveMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvResetLocation(TCPMapClient peer, Packet.S2M.ResetLocation packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateActive(TCPMapClient peer, Packet.S2M.UpdateActive packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateEquip(TCPMapClient peer, Packet.S2M.UpdateEquip packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateEquipPart(TCPMapClient peer, Packet.S2M.UpdateEquipPart packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateSkill(TCPMapClient peer, Packet.S2M.UpdateSkill packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateCurSkills(TCPMapClient peer, Packet.S2M.UpdateCurSkills packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateBuff(TCPMapClient peer, Packet.S2M.UpdateBuff packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateLevel(TCPMapClient peer, Packet.S2M.UpdateLevel packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvAddHp(TCPMapClient peer, Packet.S2M.AddHp packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateWeapon(TCPMapClient peer, Packet.S2M.UpdateWeapon packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateCurWeapon(TCPMapClient peer, Packet.S2M.UpdateCurWeapon packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateSpirit(TCPMapClient peer, Packet.S2M.UpdateSpirit packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateCurSpirit(TCPMapClient peer, Packet.S2M.UpdateCurSpirit packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvStartMine(TCPMapClient peer, Packet.S2M.StartMine packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvRoleRevive(TCPMapClient peer, Packet.S2M.RoleRevive packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdatePet(TCPMapClient peer, Packet.S2M.UpdatePet packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateTeam(TCPMapClient peer, Packet.S2M.UpdateTeam packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvChangeCurPets(TCPMapClient peer, Packet.S2M.ChangeCurPets packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateSectAura(TCPMapClient peer, Packet.S2M.UpdateSectAura packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvResetSectAuras(TCPMapClient peer, Packet.S2M.ResetSectAuras packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdatePKInfo(TCPMapClient peer, Packet.S2M.UpdatePKInfo packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateCurDIYSkill(TCPMapClient peer, Packet.S2M.UpdateCurDIYSkill packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateTransformInfo(TCPMapClient peer, Packet.S2M.UpdateTransformInfo packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCreateWorldMapBoss(TCPMapClient peer, Packet.S2M.CreateWorldMapBoss packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvDestroyWorldMapBoss(TCPMapClient peer, Packet.S2M.DestroyWorldMapBoss packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvInitWorldBoss(TCPMapClient peer, Packet.S2M.InitWorldBoss packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCreateWorldMapSuperMonster(TCPMapClient peer, Packet.S2M.CreateWorldMapSuperMonster packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCreateWorldMapMineral(TCPMapClient peer, Packet.S2M.CreateWorldMapMineral packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvGainNewSuite(TCPMapClient peer, Packet.S2M.GainNewSuite packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateSectBrief(TCPMapClient peer, Packet.S2M.UpdateSectBrief packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateHorseInfo(TCPMapClient peer, Packet.S2M.UpdateHorseInfo packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateCurUseHorse(TCPMapClient peer, Packet.S2M.UpdateCurUseHorse packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateMedal(TCPMapClient peer, Packet.S2M.UpdateMedal packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpWearFashion(TCPMapClient peer, Packet.S2M.UpWearFashion packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateAlterState(TCPMapClient peer, Packet.S2M.UpdateAlterState packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvChangeHorseShow(TCPMapClient peer, Packet.S2M.ChangeHorseShow packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvAddBuff(TCPMapClient peer, Packet.S2M.AddBuff packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateSealGrade(TCPMapClient peer, Packet.S2M.UpdateSealGrade packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateSealSkills(TCPMapClient peer, Packet.S2M.UpdateSealSkills packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncRolePetLack(TCPMapClient peer, Packet.S2M.SyncRolePetLack packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleGrasp(TCPMapClient peer, Packet.S2M.UpdateRoleGrasp packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRareBook(TCPMapClient peer, Packet.S2M.UpdateRareBook packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleTitle(TCPMapClient peer, Packet.S2M.UpdateRoleTitle packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleCurTitle(TCPMapClient peer, Packet.S2M.UpdateRoleCurTitle packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdatePetAchieve(TCPMapClient peer, Packet.S2M.UpdatePetAchieve packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateCurUniqueSkill(TCPMapClient peer, Packet.S2M.UpdateCurUniqueSkill packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSetPetAlter(TCPMapClient peer, Packet.S2M.SetPetAlter packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCarEnterMap(TCPMapClient peer, Packet.S2M.CarEnterMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCarLeaveMap(TCPMapClient peer, Packet.S2M.CarLeaveMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCarUpdateTeamCarCnt(TCPMapClient peer, Packet.S2M.CarUpdateTeamCarCnt packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleCarBehavior(TCPMapClient peer, Packet.S2M.UpdateRoleCarBehavior packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvRoleUseItemSkill(TCPMapClient peer, Packet.S2M.RoleUseItemSkill packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvRoleRename(TCPMapClient peer, Packet.S2M.RoleRename packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvAddPetHp(TCPMapClient peer, Packet.S2M.AddPetHp packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncCurRideHorse(TCPMapClient peer, Packet.S2M.SyncCurRideHorse packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateMulHorse(TCPMapClient peer, Packet.S2M.UpdateMulHorse packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvChangeArmor(TCPMapClient peer, Packet.S2M.ChangeArmor packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateArmorLevel(TCPMapClient peer, Packet.S2M.UpdateArmorLevel packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateArmorRank(TCPMapClient peer, Packet.S2M.UpdateArmorRank packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateArmorRune(TCPMapClient peer, Packet.S2M.UpdateArmorRune packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateTalentPoint(TCPMapClient peer, Packet.S2M.UpdateTalentPoint packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSpawnSceneMonster(TCPMapClient peer, Packet.S2M.SpawnSceneMonster packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvClearSceneMonster(TCPMapClient peer, Packet.S2M.ClearSceneMonster packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvResetSectGroupMap(TCPMapClient peer, Packet.S2M.ResetSectGroupMap packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateHorseSkill(TCPMapClient peer, Packet.S2M.UpdateHorseSkill packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateStayWith(TCPMapClient peer, Packet.S2M.UpdateStayWith packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleWeaponSkill(TCPMapClient peer, Packet.S2M.UpdateRoleWeaponSkill packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleWeaponTalent(TCPMapClient peer, Packet.S2M.UpdateRoleWeaponTalent packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCreateWorldMapFlag(TCPMapClient peer, Packet.S2M.CreateWorldMapFlag packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvInitWorldMapFlag(TCPMapClient peer, Packet.S2M.InitWorldMapFlag packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncMapFlagInfo(TCPMapClient peer, Packet.S2M.SyncMapFlagInfo packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncRoleItemProps(TCPMapClient peer, Packet.S2M.SyncRoleItemProps packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncTaskDrop(TCPMapClient peer, Packet.S2M.SyncTaskDrop packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRolePetSkill(TCPMapClient peer, Packet.S2M.UpdateRolePetSkill packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncWeaponOpen(TCPMapClient peer, Packet.S2M.SyncWeaponOpen packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvWorldBossPop(TCPMapClient peer, Packet.S2M.WorldBossPop packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvPickUpResult(TCPMapClient peer, Packet.S2M.PickUpResult packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUnSummonCurPets(TCPMapClient peer, Packet.S2M.UnSummonCurPets packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRolePerfectDegree(TCPMapClient peer, Packet.S2M.UpdateRolePerfectDegree packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateCurPetSpirit(TCPMapClient peer, Packet.S2M.UpdateCurPetSpirit packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvStartMarriageParade(TCPMapClient peer, Packet.S2M.StartMarriageParade packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleHeirloomDisplay(TCPMapClient peer, Packet.S2M.UpdateRoleHeirloomDisplay packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSetWeaponForm(TCPMapClient peer, Packet.S2M.SetWeaponForm packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvStartMarriageBanquet(TCPMapClient peer, Packet.S2M.StartMarriageBanquet packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleMarriageSkillInfo(TCPMapClient peer, Packet.S2M.UpdateRoleMarriageSkillInfo packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleMarriageSkillLevel(TCPMapClient peer, Packet.S2M.UpdateRoleMarriageSkillLevel packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvMarriageLevelChange(TCPMapClient peer, Packet.S2M.MarriageLevelChange packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvRoleDMGTransferUpdate(TCPMapClient peer, Packet.S2M.RoleDMGTransferUpdate packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvCreateRobotHero(TCPMapClient peer, Packet.S2M.CreateRobotHero packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvDestroyRobotHero(TCPMapClient peer, Packet.S2M.DestroyRobotHero packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncCreateStele(TCPMapClient peer, Packet.S2M.SyncCreateStele packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncDestroyStele(TCPMapClient peer, Packet.S2M.SyncDestroyStele packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncJusticeNpcShow(TCPMapClient peer, Packet.S2M.SyncJusticeNpcShow packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncJusticeNpcLeave(TCPMapClient peer, Packet.S2M.SyncJusticeNpcLeave packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncEmergencyLastTime(TCPMapClient peer, Packet.S2M.SyncEmergencyLastTime packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncRoleVipLevel(TCPMapClient peer, Packet.S2M.SyncRoleVipLevel packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvSyncRoleCurWizardPet(TCPMapClient peer, Packet.S2M.SyncRoleCurWizardPet packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvUpdateRoleSpecialCardAttr(TCPMapClient peer, Packet.S2M.UpdateRoleSpecialCardAttr packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvRoleShowProps(TCPMapClient peer, Packet.S2M.RoleShowProps packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvRoleRedNamePunish(TCPMapClient peer, Packet.S2M.RoleRedNamePunish packet)
	{
		// TODO
	}

	public void onTCPMapClientRecvGMCommand(TCPMapClient peer, Packet.S2M.GMCommand packet)
	{
		// TODO
	}

	public int getTCPGlobalMapServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPGlobalMapServerOpen(TCPGlobalMapServer peer)
	{
		// TODO
	}

	public void onTCPGlobalMapServerOpenFailed(TCPGlobalMapServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGlobalMapServerClose(TCPGlobalMapServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGlobalMapServerSessionOpen(TCPGlobalMapServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPGlobalMapServerSessionClose(TCPGlobalMapServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvKeepAlive(TCPGlobalMapServer peer, Packet.S2GM.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvWhoAmI(TCPGlobalMapServer peer, Packet.S2GM.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvReportTimeOffset(TCPGlobalMapServer peer, Packet.S2GM.ReportTimeOffset packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvLuaChannel(TCPGlobalMapServer peer, Packet.S2GM.LuaChannel packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvStrChannel(TCPGlobalMapServer peer, Packet.S2GM.StrChannel packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvEnterMap(TCPGlobalMapServer peer, Packet.S2GM.EnterMap packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvLeaveMap(TCPGlobalMapServer peer, Packet.S2GM.LeaveMap packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvUpdateActive(TCPGlobalMapServer peer, Packet.S2GM.UpdateActive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvAddHp(TCPGlobalMapServer peer, Packet.S2GM.AddHp packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvRoleUseItemSkill(TCPGlobalMapServer peer, Packet.S2GM.RoleUseItemSkill packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvAddPetHp(TCPGlobalMapServer peer, Packet.S2GM.AddPetHp packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvStartMine(TCPGlobalMapServer peer, Packet.S2GM.StartMine packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvResetLocation(TCPGlobalMapServer peer, Packet.S2GM.ResetLocation packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvUpdateCurSkills(TCPGlobalMapServer peer, Packet.S2GM.UpdateCurSkills packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvUpdateCurSpirit(TCPGlobalMapServer peer, Packet.S2GM.UpdateCurSpirit packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvPickUpResult(TCPGlobalMapServer peer, Packet.S2GM.PickUpResult packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvUpdateRoleMarriageSkillInfo(TCPGlobalMapServer peer, Packet.S2GM.UpdateRoleMarriageSkillInfo packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvUpdateRoleMarriageSkillLevel(TCPGlobalMapServer peer, Packet.S2GM.UpdateRoleMarriageSkillLevel packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGlobalMapServerRecvRoleRevive(TCPGlobalMapServer peer, Packet.S2GM.RoleRevive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapClientOpen(TCPFightMapClient peer)
	{
		// TODO
	}

	public void onTCPFightMapClientOpenFailed(TCPFightMapClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightMapClientClose(TCPFightMapClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightMapClientRecvKeepAlive(TCPFightMapClient peer, Packet.F2GM.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPFightMapClientRecvCreateMapCopyReq(TCPFightMapClient peer, Packet.F2GM.CreateMapCopyReq packet)
	{
		// TODO
	}

	public void onTCPFightMapClientRecvEndMapCopy(TCPFightMapClient peer, Packet.F2GM.EndMapCopy packet)
	{
		// TODO
	}

	//// end handlers.
}
