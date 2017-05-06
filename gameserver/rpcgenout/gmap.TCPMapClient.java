// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gmap;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPMapClient extends TCPClient<SimplePacket>
{

	public TCPMapClient(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// server to map
			case Packet.eS2MPKTKeepAlive:
			case Packet.eS2MPKTSyncTimeOffset:
			case Packet.eS2MPKTLuaChannel:
			case Packet.eS2MPKTStrChannel:
			case Packet.eS2MPKTSyncDoubleDropCfg:
			case Packet.eS2MPKTSyncExtraDropCfg:
			case Packet.eS2MPKTSyncWorldNum:
			case Packet.eS2MPKTStartMapCopy:
			case Packet.eS2MPKTEndMapCopy:
			case Packet.eS2MPKTMapCopyReady:
			case Packet.eS2MPKTResetSectMap:
			case Packet.eS2MPKTResetArenaMap:
			case Packet.eS2MPKTResetBWArenaMap:
			case Packet.eS2MPKTEnterMap:
			case Packet.eS2MPKTLeaveMap:
			case Packet.eS2MPKTResetLocation:
			case Packet.eS2MPKTUpdateActive:
			case Packet.eS2MPKTUpdateEquip:
			case Packet.eS2MPKTUpdateEquipPart:
			case Packet.eS2MPKTUpdateSkill:
			case Packet.eS2MPKTUpdateCurSkills:
			case Packet.eS2MPKTUpdateBuff:
			case Packet.eS2MPKTUpdateLevel:
			case Packet.eS2MPKTAddHp:
			case Packet.eS2MPKTUpdateWeapon:
			case Packet.eS2MPKTUpdateCurWeapon:
			case Packet.eS2MPKTUpdateSpirit:
			case Packet.eS2MPKTUpdateCurSpirit:
			case Packet.eS2MPKTStartMine:
			case Packet.eS2MPKTRoleRevive:
			case Packet.eS2MPKTUpdatePet:
			case Packet.eS2MPKTUpdateTeam:
			case Packet.eS2MPKTChangeCurPets:
			case Packet.eS2MPKTUpdateSectAura:
			case Packet.eS2MPKTResetSectAuras:
			case Packet.eS2MPKTUpdatePKInfo:
			case Packet.eS2MPKTUpdateCurDIYSkill:
			case Packet.eS2MPKTUpdateTransformInfo:
			case Packet.eS2MPKTCreateWorldMapBoss:
			case Packet.eS2MPKTDestroyWorldMapBoss:
			case Packet.eS2MPKTInitWorldBoss:
			case Packet.eS2MPKTCreateWorldMapSuperMonster:
			case Packet.eS2MPKTCreateWorldMapMineral:
			case Packet.eS2MPKTGainNewSuite:
			case Packet.eS2MPKTUpdateSectBrief:
			case Packet.eS2MPKTUpdateHorseInfo:
			case Packet.eS2MPKTUpdateCurUseHorse:
			case Packet.eS2MPKTUpdateMedal:
			case Packet.eS2MPKTUpWearFashion:
			case Packet.eS2MPKTUpdateAlterState:
			case Packet.eS2MPKTChangeHorseShow:
			case Packet.eS2MPKTAddBuff:
			case Packet.eS2MPKTUpdateSealGrade:
			case Packet.eS2MPKTUpdateSealSkills:
			case Packet.eS2MPKTSyncRolePetLack:
			case Packet.eS2MPKTUpdateRoleGrasp:
			case Packet.eS2MPKTUpdateRareBook:
			case Packet.eS2MPKTUpdateRoleTitle:
			case Packet.eS2MPKTUpdateRoleCurTitle:
			case Packet.eS2MPKTUpdatePetAchieve:
			case Packet.eS2MPKTUpdateCurUniqueSkill:
			case Packet.eS2MPKTSetPetAlter:
			case Packet.eS2MPKTCarEnterMap:
			case Packet.eS2MPKTCarLeaveMap:
			case Packet.eS2MPKTCarUpdateTeamCarCnt:
			case Packet.eS2MPKTUpdateRoleCarBehavior:
			case Packet.eS2MPKTRoleUseItemSkill:
			case Packet.eS2MPKTRoleRename:
			case Packet.eS2MPKTAddPetHp:
			case Packet.eS2MPKTSyncCurRideHorse:
			case Packet.eS2MPKTUpdateMulHorse:
			case Packet.eS2MPKTChangeArmor:
			case Packet.eS2MPKTUpdateArmorLevel:
			case Packet.eS2MPKTUpdateArmorRank:
			case Packet.eS2MPKTUpdateArmorRune:
			case Packet.eS2MPKTUpdateTalentPoint:
			case Packet.eS2MPKTSpawnSceneMonster:
			case Packet.eS2MPKTClearSceneMonster:
			case Packet.eS2MPKTResetSectGroupMap:
			case Packet.eS2MPKTUpdateHorseSkill:
			case Packet.eS2MPKTUpdateStayWith:
			case Packet.eS2MPKTUpdateRoleWeaponSkill:
			case Packet.eS2MPKTUpdateRoleWeaponTalent:
			case Packet.eS2MPKTCreateWorldMapFlag:
			case Packet.eS2MPKTInitWorldMapFlag:
			case Packet.eS2MPKTSyncMapFlagInfo:
			case Packet.eS2MPKTSyncRoleItemProps:
			case Packet.eS2MPKTSyncTaskDrop:
			case Packet.eS2MPKTUpdateRolePetSkill:
			case Packet.eS2MPKTSyncWeaponOpen:
			case Packet.eS2MPKTWorldBossPop:
			case Packet.eS2MPKTPickUpResult:
			case Packet.eS2MPKTUnSummonCurPets:
			case Packet.eS2MPKTUpdateRolePerfectDegree:
			case Packet.eS2MPKTUpdateCurPetSpirit:
			case Packet.eS2MPKTStartMarriageParade:
			case Packet.eS2MPKTUpdateRoleHeirloomDisplay:
			case Packet.eS2MPKTSetWeaponForm:
			case Packet.eS2MPKTStartMarriageBanquet:
			case Packet.eS2MPKTUpdateRoleMarriageSkillInfo:
			case Packet.eS2MPKTUpdateRoleMarriageSkillLevel:
			case Packet.eS2MPKTMarriageLevelChange:
			case Packet.eS2MPKTRoleDMGTransferUpdate:
			case Packet.eS2MPKTCreateRobotHero:
			case Packet.eS2MPKTDestroyRobotHero:
			case Packet.eS2MPKTSyncCreateStele:
			case Packet.eS2MPKTSyncDestroyStele:
			case Packet.eS2MPKTSyncJusticeNpcShow:
			case Packet.eS2MPKTSyncJusticeNpcLeave:
			case Packet.eS2MPKTSyncEmergencyLastTime:
			case Packet.eS2MPKTSyncRoleVipLevel:
			case Packet.eS2MPKTSyncRoleCurWizardPet:
			case Packet.eS2MPKTUpdateRoleSpecialCardAttr:
			case Packet.eS2MPKTRoleShowProps:
			case Packet.eS2MPKTRoleRedNamePunish:
			case Packet.eS2MPKTGMCommand:
				return true;
			default:
				break;
			}
			return false;
		}
	}

	@Override
	public PacketEncoder<SimplePacket> getEncoder()
	{
		return dencoder;
	}

	@Override
	public PacketDecoder<SimplePacket> getDecoder()
	{
		return dencoder;
	}

	@Override
	public void onOpen()
	{
		managerRPC.onTCPMapClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPMapClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPMapClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// server to map
		case Packet.eS2MPKTKeepAlive:
			{
				Packet.S2M.KeepAlive p = (Packet.S2M.KeepAlive)packet;
				managerRPC.onTCPMapClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eS2MPKTSyncTimeOffset:
			{
				Packet.S2M.SyncTimeOffset p = (Packet.S2M.SyncTimeOffset)packet;
				managerRPC.onTCPMapClientRecvSyncTimeOffset(this, p);
			}
			break;
		case Packet.eS2MPKTLuaChannel:
			{
				Packet.S2M.LuaChannel p = (Packet.S2M.LuaChannel)packet;
				managerRPC.onTCPMapClientRecvLuaChannel(this, p);
			}
			break;
		case Packet.eS2MPKTStrChannel:
			{
				Packet.S2M.StrChannel p = (Packet.S2M.StrChannel)packet;
				managerRPC.onTCPMapClientRecvStrChannel(this, p);
			}
			break;
		case Packet.eS2MPKTSyncDoubleDropCfg:
			{
				Packet.S2M.SyncDoubleDropCfg p = (Packet.S2M.SyncDoubleDropCfg)packet;
				managerRPC.onTCPMapClientRecvSyncDoubleDropCfg(this, p);
			}
			break;
		case Packet.eS2MPKTSyncExtraDropCfg:
			{
				Packet.S2M.SyncExtraDropCfg p = (Packet.S2M.SyncExtraDropCfg)packet;
				managerRPC.onTCPMapClientRecvSyncExtraDropCfg(this, p);
			}
			break;
		case Packet.eS2MPKTSyncWorldNum:
			{
				Packet.S2M.SyncWorldNum p = (Packet.S2M.SyncWorldNum)packet;
				managerRPC.onTCPMapClientRecvSyncWorldNum(this, p);
			}
			break;
		case Packet.eS2MPKTStartMapCopy:
			{
				Packet.S2M.StartMapCopy p = (Packet.S2M.StartMapCopy)packet;
				managerRPC.onTCPMapClientRecvStartMapCopy(this, p);
			}
			break;
		case Packet.eS2MPKTEndMapCopy:
			{
				Packet.S2M.EndMapCopy p = (Packet.S2M.EndMapCopy)packet;
				managerRPC.onTCPMapClientRecvEndMapCopy(this, p);
			}
			break;
		case Packet.eS2MPKTMapCopyReady:
			{
				Packet.S2M.MapCopyReady p = (Packet.S2M.MapCopyReady)packet;
				managerRPC.onTCPMapClientRecvMapCopyReady(this, p);
			}
			break;
		case Packet.eS2MPKTResetSectMap:
			{
				Packet.S2M.ResetSectMap p = (Packet.S2M.ResetSectMap)packet;
				managerRPC.onTCPMapClientRecvResetSectMap(this, p);
			}
			break;
		case Packet.eS2MPKTResetArenaMap:
			{
				Packet.S2M.ResetArenaMap p = (Packet.S2M.ResetArenaMap)packet;
				managerRPC.onTCPMapClientRecvResetArenaMap(this, p);
			}
			break;
		case Packet.eS2MPKTResetBWArenaMap:
			{
				Packet.S2M.ResetBWArenaMap p = (Packet.S2M.ResetBWArenaMap)packet;
				managerRPC.onTCPMapClientRecvResetBWArenaMap(this, p);
			}
			break;
		case Packet.eS2MPKTEnterMap:
			{
				Packet.S2M.EnterMap p = (Packet.S2M.EnterMap)packet;
				managerRPC.onTCPMapClientRecvEnterMap(this, p);
			}
			break;
		case Packet.eS2MPKTLeaveMap:
			{
				Packet.S2M.LeaveMap p = (Packet.S2M.LeaveMap)packet;
				managerRPC.onTCPMapClientRecvLeaveMap(this, p);
			}
			break;
		case Packet.eS2MPKTResetLocation:
			{
				Packet.S2M.ResetLocation p = (Packet.S2M.ResetLocation)packet;
				managerRPC.onTCPMapClientRecvResetLocation(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateActive:
			{
				Packet.S2M.UpdateActive p = (Packet.S2M.UpdateActive)packet;
				managerRPC.onTCPMapClientRecvUpdateActive(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateEquip:
			{
				Packet.S2M.UpdateEquip p = (Packet.S2M.UpdateEquip)packet;
				managerRPC.onTCPMapClientRecvUpdateEquip(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateEquipPart:
			{
				Packet.S2M.UpdateEquipPart p = (Packet.S2M.UpdateEquipPart)packet;
				managerRPC.onTCPMapClientRecvUpdateEquipPart(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateSkill:
			{
				Packet.S2M.UpdateSkill p = (Packet.S2M.UpdateSkill)packet;
				managerRPC.onTCPMapClientRecvUpdateSkill(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateCurSkills:
			{
				Packet.S2M.UpdateCurSkills p = (Packet.S2M.UpdateCurSkills)packet;
				managerRPC.onTCPMapClientRecvUpdateCurSkills(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateBuff:
			{
				Packet.S2M.UpdateBuff p = (Packet.S2M.UpdateBuff)packet;
				managerRPC.onTCPMapClientRecvUpdateBuff(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateLevel:
			{
				Packet.S2M.UpdateLevel p = (Packet.S2M.UpdateLevel)packet;
				managerRPC.onTCPMapClientRecvUpdateLevel(this, p);
			}
			break;
		case Packet.eS2MPKTAddHp:
			{
				Packet.S2M.AddHp p = (Packet.S2M.AddHp)packet;
				managerRPC.onTCPMapClientRecvAddHp(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateWeapon:
			{
				Packet.S2M.UpdateWeapon p = (Packet.S2M.UpdateWeapon)packet;
				managerRPC.onTCPMapClientRecvUpdateWeapon(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateCurWeapon:
			{
				Packet.S2M.UpdateCurWeapon p = (Packet.S2M.UpdateCurWeapon)packet;
				managerRPC.onTCPMapClientRecvUpdateCurWeapon(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateSpirit:
			{
				Packet.S2M.UpdateSpirit p = (Packet.S2M.UpdateSpirit)packet;
				managerRPC.onTCPMapClientRecvUpdateSpirit(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateCurSpirit:
			{
				Packet.S2M.UpdateCurSpirit p = (Packet.S2M.UpdateCurSpirit)packet;
				managerRPC.onTCPMapClientRecvUpdateCurSpirit(this, p);
			}
			break;
		case Packet.eS2MPKTStartMine:
			{
				Packet.S2M.StartMine p = (Packet.S2M.StartMine)packet;
				managerRPC.onTCPMapClientRecvStartMine(this, p);
			}
			break;
		case Packet.eS2MPKTRoleRevive:
			{
				Packet.S2M.RoleRevive p = (Packet.S2M.RoleRevive)packet;
				managerRPC.onTCPMapClientRecvRoleRevive(this, p);
			}
			break;
		case Packet.eS2MPKTUpdatePet:
			{
				Packet.S2M.UpdatePet p = (Packet.S2M.UpdatePet)packet;
				managerRPC.onTCPMapClientRecvUpdatePet(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateTeam:
			{
				Packet.S2M.UpdateTeam p = (Packet.S2M.UpdateTeam)packet;
				managerRPC.onTCPMapClientRecvUpdateTeam(this, p);
			}
			break;
		case Packet.eS2MPKTChangeCurPets:
			{
				Packet.S2M.ChangeCurPets p = (Packet.S2M.ChangeCurPets)packet;
				managerRPC.onTCPMapClientRecvChangeCurPets(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateSectAura:
			{
				Packet.S2M.UpdateSectAura p = (Packet.S2M.UpdateSectAura)packet;
				managerRPC.onTCPMapClientRecvUpdateSectAura(this, p);
			}
			break;
		case Packet.eS2MPKTResetSectAuras:
			{
				Packet.S2M.ResetSectAuras p = (Packet.S2M.ResetSectAuras)packet;
				managerRPC.onTCPMapClientRecvResetSectAuras(this, p);
			}
			break;
		case Packet.eS2MPKTUpdatePKInfo:
			{
				Packet.S2M.UpdatePKInfo p = (Packet.S2M.UpdatePKInfo)packet;
				managerRPC.onTCPMapClientRecvUpdatePKInfo(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateCurDIYSkill:
			{
				Packet.S2M.UpdateCurDIYSkill p = (Packet.S2M.UpdateCurDIYSkill)packet;
				managerRPC.onTCPMapClientRecvUpdateCurDIYSkill(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateTransformInfo:
			{
				Packet.S2M.UpdateTransformInfo p = (Packet.S2M.UpdateTransformInfo)packet;
				managerRPC.onTCPMapClientRecvUpdateTransformInfo(this, p);
			}
			break;
		case Packet.eS2MPKTCreateWorldMapBoss:
			{
				Packet.S2M.CreateWorldMapBoss p = (Packet.S2M.CreateWorldMapBoss)packet;
				managerRPC.onTCPMapClientRecvCreateWorldMapBoss(this, p);
			}
			break;
		case Packet.eS2MPKTDestroyWorldMapBoss:
			{
				Packet.S2M.DestroyWorldMapBoss p = (Packet.S2M.DestroyWorldMapBoss)packet;
				managerRPC.onTCPMapClientRecvDestroyWorldMapBoss(this, p);
			}
			break;
		case Packet.eS2MPKTInitWorldBoss:
			{
				Packet.S2M.InitWorldBoss p = (Packet.S2M.InitWorldBoss)packet;
				managerRPC.onTCPMapClientRecvInitWorldBoss(this, p);
			}
			break;
		case Packet.eS2MPKTCreateWorldMapSuperMonster:
			{
				Packet.S2M.CreateWorldMapSuperMonster p = (Packet.S2M.CreateWorldMapSuperMonster)packet;
				managerRPC.onTCPMapClientRecvCreateWorldMapSuperMonster(this, p);
			}
			break;
		case Packet.eS2MPKTCreateWorldMapMineral:
			{
				Packet.S2M.CreateWorldMapMineral p = (Packet.S2M.CreateWorldMapMineral)packet;
				managerRPC.onTCPMapClientRecvCreateWorldMapMineral(this, p);
			}
			break;
		case Packet.eS2MPKTGainNewSuite:
			{
				Packet.S2M.GainNewSuite p = (Packet.S2M.GainNewSuite)packet;
				managerRPC.onTCPMapClientRecvGainNewSuite(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateSectBrief:
			{
				Packet.S2M.UpdateSectBrief p = (Packet.S2M.UpdateSectBrief)packet;
				managerRPC.onTCPMapClientRecvUpdateSectBrief(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateHorseInfo:
			{
				Packet.S2M.UpdateHorseInfo p = (Packet.S2M.UpdateHorseInfo)packet;
				managerRPC.onTCPMapClientRecvUpdateHorseInfo(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateCurUseHorse:
			{
				Packet.S2M.UpdateCurUseHorse p = (Packet.S2M.UpdateCurUseHorse)packet;
				managerRPC.onTCPMapClientRecvUpdateCurUseHorse(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateMedal:
			{
				Packet.S2M.UpdateMedal p = (Packet.S2M.UpdateMedal)packet;
				managerRPC.onTCPMapClientRecvUpdateMedal(this, p);
			}
			break;
		case Packet.eS2MPKTUpWearFashion:
			{
				Packet.S2M.UpWearFashion p = (Packet.S2M.UpWearFashion)packet;
				managerRPC.onTCPMapClientRecvUpWearFashion(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateAlterState:
			{
				Packet.S2M.UpdateAlterState p = (Packet.S2M.UpdateAlterState)packet;
				managerRPC.onTCPMapClientRecvUpdateAlterState(this, p);
			}
			break;
		case Packet.eS2MPKTChangeHorseShow:
			{
				Packet.S2M.ChangeHorseShow p = (Packet.S2M.ChangeHorseShow)packet;
				managerRPC.onTCPMapClientRecvChangeHorseShow(this, p);
			}
			break;
		case Packet.eS2MPKTAddBuff:
			{
				Packet.S2M.AddBuff p = (Packet.S2M.AddBuff)packet;
				managerRPC.onTCPMapClientRecvAddBuff(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateSealGrade:
			{
				Packet.S2M.UpdateSealGrade p = (Packet.S2M.UpdateSealGrade)packet;
				managerRPC.onTCPMapClientRecvUpdateSealGrade(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateSealSkills:
			{
				Packet.S2M.UpdateSealSkills p = (Packet.S2M.UpdateSealSkills)packet;
				managerRPC.onTCPMapClientRecvUpdateSealSkills(this, p);
			}
			break;
		case Packet.eS2MPKTSyncRolePetLack:
			{
				Packet.S2M.SyncRolePetLack p = (Packet.S2M.SyncRolePetLack)packet;
				managerRPC.onTCPMapClientRecvSyncRolePetLack(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleGrasp:
			{
				Packet.S2M.UpdateRoleGrasp p = (Packet.S2M.UpdateRoleGrasp)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleGrasp(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRareBook:
			{
				Packet.S2M.UpdateRareBook p = (Packet.S2M.UpdateRareBook)packet;
				managerRPC.onTCPMapClientRecvUpdateRareBook(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleTitle:
			{
				Packet.S2M.UpdateRoleTitle p = (Packet.S2M.UpdateRoleTitle)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleTitle(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleCurTitle:
			{
				Packet.S2M.UpdateRoleCurTitle p = (Packet.S2M.UpdateRoleCurTitle)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleCurTitle(this, p);
			}
			break;
		case Packet.eS2MPKTUpdatePetAchieve:
			{
				Packet.S2M.UpdatePetAchieve p = (Packet.S2M.UpdatePetAchieve)packet;
				managerRPC.onTCPMapClientRecvUpdatePetAchieve(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateCurUniqueSkill:
			{
				Packet.S2M.UpdateCurUniqueSkill p = (Packet.S2M.UpdateCurUniqueSkill)packet;
				managerRPC.onTCPMapClientRecvUpdateCurUniqueSkill(this, p);
			}
			break;
		case Packet.eS2MPKTSetPetAlter:
			{
				Packet.S2M.SetPetAlter p = (Packet.S2M.SetPetAlter)packet;
				managerRPC.onTCPMapClientRecvSetPetAlter(this, p);
			}
			break;
		case Packet.eS2MPKTCarEnterMap:
			{
				Packet.S2M.CarEnterMap p = (Packet.S2M.CarEnterMap)packet;
				managerRPC.onTCPMapClientRecvCarEnterMap(this, p);
			}
			break;
		case Packet.eS2MPKTCarLeaveMap:
			{
				Packet.S2M.CarLeaveMap p = (Packet.S2M.CarLeaveMap)packet;
				managerRPC.onTCPMapClientRecvCarLeaveMap(this, p);
			}
			break;
		case Packet.eS2MPKTCarUpdateTeamCarCnt:
			{
				Packet.S2M.CarUpdateTeamCarCnt p = (Packet.S2M.CarUpdateTeamCarCnt)packet;
				managerRPC.onTCPMapClientRecvCarUpdateTeamCarCnt(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleCarBehavior:
			{
				Packet.S2M.UpdateRoleCarBehavior p = (Packet.S2M.UpdateRoleCarBehavior)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleCarBehavior(this, p);
			}
			break;
		case Packet.eS2MPKTRoleUseItemSkill:
			{
				Packet.S2M.RoleUseItemSkill p = (Packet.S2M.RoleUseItemSkill)packet;
				managerRPC.onTCPMapClientRecvRoleUseItemSkill(this, p);
			}
			break;
		case Packet.eS2MPKTRoleRename:
			{
				Packet.S2M.RoleRename p = (Packet.S2M.RoleRename)packet;
				managerRPC.onTCPMapClientRecvRoleRename(this, p);
			}
			break;
		case Packet.eS2MPKTAddPetHp:
			{
				Packet.S2M.AddPetHp p = (Packet.S2M.AddPetHp)packet;
				managerRPC.onTCPMapClientRecvAddPetHp(this, p);
			}
			break;
		case Packet.eS2MPKTSyncCurRideHorse:
			{
				Packet.S2M.SyncCurRideHorse p = (Packet.S2M.SyncCurRideHorse)packet;
				managerRPC.onTCPMapClientRecvSyncCurRideHorse(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateMulHorse:
			{
				Packet.S2M.UpdateMulHorse p = (Packet.S2M.UpdateMulHorse)packet;
				managerRPC.onTCPMapClientRecvUpdateMulHorse(this, p);
			}
			break;
		case Packet.eS2MPKTChangeArmor:
			{
				Packet.S2M.ChangeArmor p = (Packet.S2M.ChangeArmor)packet;
				managerRPC.onTCPMapClientRecvChangeArmor(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateArmorLevel:
			{
				Packet.S2M.UpdateArmorLevel p = (Packet.S2M.UpdateArmorLevel)packet;
				managerRPC.onTCPMapClientRecvUpdateArmorLevel(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateArmorRank:
			{
				Packet.S2M.UpdateArmorRank p = (Packet.S2M.UpdateArmorRank)packet;
				managerRPC.onTCPMapClientRecvUpdateArmorRank(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateArmorRune:
			{
				Packet.S2M.UpdateArmorRune p = (Packet.S2M.UpdateArmorRune)packet;
				managerRPC.onTCPMapClientRecvUpdateArmorRune(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateTalentPoint:
			{
				Packet.S2M.UpdateTalentPoint p = (Packet.S2M.UpdateTalentPoint)packet;
				managerRPC.onTCPMapClientRecvUpdateTalentPoint(this, p);
			}
			break;
		case Packet.eS2MPKTSpawnSceneMonster:
			{
				Packet.S2M.SpawnSceneMonster p = (Packet.S2M.SpawnSceneMonster)packet;
				managerRPC.onTCPMapClientRecvSpawnSceneMonster(this, p);
			}
			break;
		case Packet.eS2MPKTClearSceneMonster:
			{
				Packet.S2M.ClearSceneMonster p = (Packet.S2M.ClearSceneMonster)packet;
				managerRPC.onTCPMapClientRecvClearSceneMonster(this, p);
			}
			break;
		case Packet.eS2MPKTResetSectGroupMap:
			{
				Packet.S2M.ResetSectGroupMap p = (Packet.S2M.ResetSectGroupMap)packet;
				managerRPC.onTCPMapClientRecvResetSectGroupMap(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateHorseSkill:
			{
				Packet.S2M.UpdateHorseSkill p = (Packet.S2M.UpdateHorseSkill)packet;
				managerRPC.onTCPMapClientRecvUpdateHorseSkill(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateStayWith:
			{
				Packet.S2M.UpdateStayWith p = (Packet.S2M.UpdateStayWith)packet;
				managerRPC.onTCPMapClientRecvUpdateStayWith(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleWeaponSkill:
			{
				Packet.S2M.UpdateRoleWeaponSkill p = (Packet.S2M.UpdateRoleWeaponSkill)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleWeaponSkill(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleWeaponTalent:
			{
				Packet.S2M.UpdateRoleWeaponTalent p = (Packet.S2M.UpdateRoleWeaponTalent)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleWeaponTalent(this, p);
			}
			break;
		case Packet.eS2MPKTCreateWorldMapFlag:
			{
				Packet.S2M.CreateWorldMapFlag p = (Packet.S2M.CreateWorldMapFlag)packet;
				managerRPC.onTCPMapClientRecvCreateWorldMapFlag(this, p);
			}
			break;
		case Packet.eS2MPKTInitWorldMapFlag:
			{
				Packet.S2M.InitWorldMapFlag p = (Packet.S2M.InitWorldMapFlag)packet;
				managerRPC.onTCPMapClientRecvInitWorldMapFlag(this, p);
			}
			break;
		case Packet.eS2MPKTSyncMapFlagInfo:
			{
				Packet.S2M.SyncMapFlagInfo p = (Packet.S2M.SyncMapFlagInfo)packet;
				managerRPC.onTCPMapClientRecvSyncMapFlagInfo(this, p);
			}
			break;
		case Packet.eS2MPKTSyncRoleItemProps:
			{
				Packet.S2M.SyncRoleItemProps p = (Packet.S2M.SyncRoleItemProps)packet;
				managerRPC.onTCPMapClientRecvSyncRoleItemProps(this, p);
			}
			break;
		case Packet.eS2MPKTSyncTaskDrop:
			{
				Packet.S2M.SyncTaskDrop p = (Packet.S2M.SyncTaskDrop)packet;
				managerRPC.onTCPMapClientRecvSyncTaskDrop(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRolePetSkill:
			{
				Packet.S2M.UpdateRolePetSkill p = (Packet.S2M.UpdateRolePetSkill)packet;
				managerRPC.onTCPMapClientRecvUpdateRolePetSkill(this, p);
			}
			break;
		case Packet.eS2MPKTSyncWeaponOpen:
			{
				Packet.S2M.SyncWeaponOpen p = (Packet.S2M.SyncWeaponOpen)packet;
				managerRPC.onTCPMapClientRecvSyncWeaponOpen(this, p);
			}
			break;
		case Packet.eS2MPKTWorldBossPop:
			{
				Packet.S2M.WorldBossPop p = (Packet.S2M.WorldBossPop)packet;
				managerRPC.onTCPMapClientRecvWorldBossPop(this, p);
			}
			break;
		case Packet.eS2MPKTPickUpResult:
			{
				Packet.S2M.PickUpResult p = (Packet.S2M.PickUpResult)packet;
				managerRPC.onTCPMapClientRecvPickUpResult(this, p);
			}
			break;
		case Packet.eS2MPKTUnSummonCurPets:
			{
				Packet.S2M.UnSummonCurPets p = (Packet.S2M.UnSummonCurPets)packet;
				managerRPC.onTCPMapClientRecvUnSummonCurPets(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRolePerfectDegree:
			{
				Packet.S2M.UpdateRolePerfectDegree p = (Packet.S2M.UpdateRolePerfectDegree)packet;
				managerRPC.onTCPMapClientRecvUpdateRolePerfectDegree(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateCurPetSpirit:
			{
				Packet.S2M.UpdateCurPetSpirit p = (Packet.S2M.UpdateCurPetSpirit)packet;
				managerRPC.onTCPMapClientRecvUpdateCurPetSpirit(this, p);
			}
			break;
		case Packet.eS2MPKTStartMarriageParade:
			{
				Packet.S2M.StartMarriageParade p = (Packet.S2M.StartMarriageParade)packet;
				managerRPC.onTCPMapClientRecvStartMarriageParade(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleHeirloomDisplay:
			{
				Packet.S2M.UpdateRoleHeirloomDisplay p = (Packet.S2M.UpdateRoleHeirloomDisplay)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleHeirloomDisplay(this, p);
			}
			break;
		case Packet.eS2MPKTSetWeaponForm:
			{
				Packet.S2M.SetWeaponForm p = (Packet.S2M.SetWeaponForm)packet;
				managerRPC.onTCPMapClientRecvSetWeaponForm(this, p);
			}
			break;
		case Packet.eS2MPKTStartMarriageBanquet:
			{
				Packet.S2M.StartMarriageBanquet p = (Packet.S2M.StartMarriageBanquet)packet;
				managerRPC.onTCPMapClientRecvStartMarriageBanquet(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleMarriageSkillInfo:
			{
				Packet.S2M.UpdateRoleMarriageSkillInfo p = (Packet.S2M.UpdateRoleMarriageSkillInfo)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleMarriageSkillInfo(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleMarriageSkillLevel:
			{
				Packet.S2M.UpdateRoleMarriageSkillLevel p = (Packet.S2M.UpdateRoleMarriageSkillLevel)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleMarriageSkillLevel(this, p);
			}
			break;
		case Packet.eS2MPKTMarriageLevelChange:
			{
				Packet.S2M.MarriageLevelChange p = (Packet.S2M.MarriageLevelChange)packet;
				managerRPC.onTCPMapClientRecvMarriageLevelChange(this, p);
			}
			break;
		case Packet.eS2MPKTRoleDMGTransferUpdate:
			{
				Packet.S2M.RoleDMGTransferUpdate p = (Packet.S2M.RoleDMGTransferUpdate)packet;
				managerRPC.onTCPMapClientRecvRoleDMGTransferUpdate(this, p);
			}
			break;
		case Packet.eS2MPKTCreateRobotHero:
			{
				Packet.S2M.CreateRobotHero p = (Packet.S2M.CreateRobotHero)packet;
				managerRPC.onTCPMapClientRecvCreateRobotHero(this, p);
			}
			break;
		case Packet.eS2MPKTDestroyRobotHero:
			{
				Packet.S2M.DestroyRobotHero p = (Packet.S2M.DestroyRobotHero)packet;
				managerRPC.onTCPMapClientRecvDestroyRobotHero(this, p);
			}
			break;
		case Packet.eS2MPKTSyncCreateStele:
			{
				Packet.S2M.SyncCreateStele p = (Packet.S2M.SyncCreateStele)packet;
				managerRPC.onTCPMapClientRecvSyncCreateStele(this, p);
			}
			break;
		case Packet.eS2MPKTSyncDestroyStele:
			{
				Packet.S2M.SyncDestroyStele p = (Packet.S2M.SyncDestroyStele)packet;
				managerRPC.onTCPMapClientRecvSyncDestroyStele(this, p);
			}
			break;
		case Packet.eS2MPKTSyncJusticeNpcShow:
			{
				Packet.S2M.SyncJusticeNpcShow p = (Packet.S2M.SyncJusticeNpcShow)packet;
				managerRPC.onTCPMapClientRecvSyncJusticeNpcShow(this, p);
			}
			break;
		case Packet.eS2MPKTSyncJusticeNpcLeave:
			{
				Packet.S2M.SyncJusticeNpcLeave p = (Packet.S2M.SyncJusticeNpcLeave)packet;
				managerRPC.onTCPMapClientRecvSyncJusticeNpcLeave(this, p);
			}
			break;
		case Packet.eS2MPKTSyncEmergencyLastTime:
			{
				Packet.S2M.SyncEmergencyLastTime p = (Packet.S2M.SyncEmergencyLastTime)packet;
				managerRPC.onTCPMapClientRecvSyncEmergencyLastTime(this, p);
			}
			break;
		case Packet.eS2MPKTSyncRoleVipLevel:
			{
				Packet.S2M.SyncRoleVipLevel p = (Packet.S2M.SyncRoleVipLevel)packet;
				managerRPC.onTCPMapClientRecvSyncRoleVipLevel(this, p);
			}
			break;
		case Packet.eS2MPKTSyncRoleCurWizardPet:
			{
				Packet.S2M.SyncRoleCurWizardPet p = (Packet.S2M.SyncRoleCurWizardPet)packet;
				managerRPC.onTCPMapClientRecvSyncRoleCurWizardPet(this, p);
			}
			break;
		case Packet.eS2MPKTUpdateRoleSpecialCardAttr:
			{
				Packet.S2M.UpdateRoleSpecialCardAttr p = (Packet.S2M.UpdateRoleSpecialCardAttr)packet;
				managerRPC.onTCPMapClientRecvUpdateRoleSpecialCardAttr(this, p);
			}
			break;
		case Packet.eS2MPKTRoleShowProps:
			{
				Packet.S2M.RoleShowProps p = (Packet.S2M.RoleShowProps)packet;
				managerRPC.onTCPMapClientRecvRoleShowProps(this, p);
			}
			break;
		case Packet.eS2MPKTRoleRedNamePunish:
			{
				Packet.S2M.RoleRedNamePunish p = (Packet.S2M.RoleRedNamePunish)packet;
				managerRPC.onTCPMapClientRecvRoleRedNamePunish(this, p);
			}
			break;
		case Packet.eS2MPKTGMCommand:
			{
				Packet.S2M.GMCommand p = (Packet.S2M.GMCommand)packet;
				managerRPC.onTCPMapClientRecvGMCommand(this, p);
			}
			break;
		default:
			break;
		}
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private RPCManager managerRPC;
}
