// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPMapServer extends TCPServer<SimplePacket>
{

	public TCPMapServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getRecvBufferSize()
	{
		return 131072;
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// map to server
			case Packet.eM2SPKTKeepAlive:
			case Packet.eM2SPKTWhoAmI:
			case Packet.eM2SPKTLuaChannel:
			case Packet.eM2SPKTStrChannel:
			case Packet.eM2SPKTStrChannelBroadcast:
			case Packet.eM2SPKTMapRoleReady:
			case Packet.eM2SPKTNearByRoleMove:
			case Packet.eM2SPKTNearByRoleStopMove:
			case Packet.eM2SPKTNearByRoleEnter:
			case Packet.eM2SPKTNearByRoleLeave:
			case Packet.eM2SPKTSyncCommonMapCopyStart:
			case Packet.eM2SPKTSyncCommonMapCopyEnd:
			case Packet.eM2SPKTSyncSectMapCopyStart:
			case Packet.eM2SPKTSyncSectMapCopyProgress:
			case Packet.eM2SPKTSyncArenaMapCopyStart:
			case Packet.eM2SPKTSyncArenaMapCopyEnd:
			case Packet.eM2SPKTSyncLocation:
			case Packet.eM2SPKTSyncHp:
			case Packet.eM2SPKTAddDrops:
			case Packet.eM2SPKTAddKill:
			case Packet.eM2SPKTSyncDurability:
			case Packet.eM2SPKTSyncEndMine:
			case Packet.eM2SPKTAddPKValue:
			case Packet.eM2SPKTSyncWorldMapBossProgress:
			case Packet.eM2SPKTSyncBWArenaMapCopyStart:
			case Packet.eM2SPKTSyncBWArenaMapCopyEnd:
			case Packet.eM2SPKTSyncPetLifeMapCopyStart:
			case Packet.eM2SPKTSyncCurRideHorse:
			case Packet.eM2SPKTSyncCarLocation:
			case Packet.eM2SPKTSyncCarHp:
			case Packet.eM2SPKTUpdateCarDamage:
			case Packet.eM2SPKTSyncRoleRobSuccess:
			case Packet.eM2SPKTUpdateRoleCarRobber:
			case Packet.eM2SPKTKickRoleFromMap:
			case Packet.eM2SPKTRoleUseItemSkillSuc:
			case Packet.eM2SPKTUpdateRoleFightState:
			case Packet.eM2SPKTSyncRolePetHp:
			case Packet.eM2SPKTSyncRoleSp:
			case Packet.eM2SPKTSyncWorldMapBossRecord:
			case Packet.eM2SPKTSyncArmorVal:
			case Packet.eM2SPKTSyncSectGroupMapCopyStatus:
			case Packet.eM2SPKTSyncSectGroupMapCopyResult:
			case Packet.eM2SPKTSyncSectGroupMapCopyStart:
			case Packet.eM2SPKTSyncSectGroupMapCopyProgress:
			case Packet.eM2SPKTSyncSectGroupMapCopyAddKill:
			case Packet.eM2SPKTSyncMapFlagCanTake:
			case Packet.eM2SPKTSyncWeaponMaster:
			case Packet.eM2SPKTRolePickUpDrops:
			case Packet.eM2SPKTSyncSuperMonster:
			case Packet.eM2SPKTSyncWorldMineral:
			case Packet.eM2SPKTSyncMarriageParadeEnd:
			case Packet.eM2SPKTRolePickUpRareDrops:
			case Packet.eM2SPKTSyncWorldBossDamageRoles:
			case Packet.eM2SPKTSyncSteleRemainTimes:
			case Packet.eM2SPKTSyncRoleAddSteleCard:
			case Packet.eM2SPKTSyncRefreshSteleMonster:
			case Packet.eM2SPKTSyncEmergencyMapStart:
			case Packet.eM2SPKTSyncEmergencyMapKillMonster:
			case Packet.eM2SPKTSyncEmergencyMapEnd:
			case Packet.eM2SPKTSyncFightNpcMapStart:
			case Packet.eM2SPKTSyncFightNpcMapEnd:
			case Packet.eM2SPKTSyncTowerDefenceMapStart:
			case Packet.eM2SPKTSyncTowerDefenceMapEnd:
			case Packet.eM2SPKTSyncTowerDefenceSpawnCount:
			case Packet.eM2SPKTSyncTowerDefenceScore:
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
		managerRPC.onTCPMapServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPMapServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPMapServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPMapServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPMapServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// map to server
		case Packet.eM2SPKTKeepAlive:
			{
				Packet.M2S.KeepAlive p = (Packet.M2S.KeepAlive)packet;
				managerRPC.onTCPMapServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTWhoAmI:
			{
				Packet.M2S.WhoAmI p = (Packet.M2S.WhoAmI)packet;
				managerRPC.onTCPMapServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTLuaChannel:
			{
				Packet.M2S.LuaChannel p = (Packet.M2S.LuaChannel)packet;
				managerRPC.onTCPMapServerRecvLuaChannel(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTStrChannel:
			{
				Packet.M2S.StrChannel p = (Packet.M2S.StrChannel)packet;
				managerRPC.onTCPMapServerRecvStrChannel(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTStrChannelBroadcast:
			{
				Packet.M2S.StrChannelBroadcast p = (Packet.M2S.StrChannelBroadcast)packet;
				managerRPC.onTCPMapServerRecvStrChannelBroadcast(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTMapRoleReady:
			{
				Packet.M2S.MapRoleReady p = (Packet.M2S.MapRoleReady)packet;
				managerRPC.onTCPMapServerRecvMapRoleReady(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTNearByRoleMove:
			{
				Packet.M2S.NearByRoleMove p = (Packet.M2S.NearByRoleMove)packet;
				managerRPC.onTCPMapServerRecvNearByRoleMove(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTNearByRoleStopMove:
			{
				Packet.M2S.NearByRoleStopMove p = (Packet.M2S.NearByRoleStopMove)packet;
				managerRPC.onTCPMapServerRecvNearByRoleStopMove(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTNearByRoleEnter:
			{
				Packet.M2S.NearByRoleEnter p = (Packet.M2S.NearByRoleEnter)packet;
				managerRPC.onTCPMapServerRecvNearByRoleEnter(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTNearByRoleLeave:
			{
				Packet.M2S.NearByRoleLeave p = (Packet.M2S.NearByRoleLeave)packet;
				managerRPC.onTCPMapServerRecvNearByRoleLeave(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncCommonMapCopyStart:
			{
				Packet.M2S.SyncCommonMapCopyStart p = (Packet.M2S.SyncCommonMapCopyStart)packet;
				managerRPC.onTCPMapServerRecvSyncCommonMapCopyStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncCommonMapCopyEnd:
			{
				Packet.M2S.SyncCommonMapCopyEnd p = (Packet.M2S.SyncCommonMapCopyEnd)packet;
				managerRPC.onTCPMapServerRecvSyncCommonMapCopyEnd(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSectMapCopyStart:
			{
				Packet.M2S.SyncSectMapCopyStart p = (Packet.M2S.SyncSectMapCopyStart)packet;
				managerRPC.onTCPMapServerRecvSyncSectMapCopyStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSectMapCopyProgress:
			{
				Packet.M2S.SyncSectMapCopyProgress p = (Packet.M2S.SyncSectMapCopyProgress)packet;
				managerRPC.onTCPMapServerRecvSyncSectMapCopyProgress(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncArenaMapCopyStart:
			{
				Packet.M2S.SyncArenaMapCopyStart p = (Packet.M2S.SyncArenaMapCopyStart)packet;
				managerRPC.onTCPMapServerRecvSyncArenaMapCopyStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncArenaMapCopyEnd:
			{
				Packet.M2S.SyncArenaMapCopyEnd p = (Packet.M2S.SyncArenaMapCopyEnd)packet;
				managerRPC.onTCPMapServerRecvSyncArenaMapCopyEnd(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncLocation:
			{
				Packet.M2S.SyncLocation p = (Packet.M2S.SyncLocation)packet;
				managerRPC.onTCPMapServerRecvSyncLocation(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncHp:
			{
				Packet.M2S.SyncHp p = (Packet.M2S.SyncHp)packet;
				managerRPC.onTCPMapServerRecvSyncHp(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTAddDrops:
			{
				Packet.M2S.AddDrops p = (Packet.M2S.AddDrops)packet;
				managerRPC.onTCPMapServerRecvAddDrops(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTAddKill:
			{
				Packet.M2S.AddKill p = (Packet.M2S.AddKill)packet;
				managerRPC.onTCPMapServerRecvAddKill(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncDurability:
			{
				Packet.M2S.SyncDurability p = (Packet.M2S.SyncDurability)packet;
				managerRPC.onTCPMapServerRecvSyncDurability(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncEndMine:
			{
				Packet.M2S.SyncEndMine p = (Packet.M2S.SyncEndMine)packet;
				managerRPC.onTCPMapServerRecvSyncEndMine(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTAddPKValue:
			{
				Packet.M2S.AddPKValue p = (Packet.M2S.AddPKValue)packet;
				managerRPC.onTCPMapServerRecvAddPKValue(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncWorldMapBossProgress:
			{
				Packet.M2S.SyncWorldMapBossProgress p = (Packet.M2S.SyncWorldMapBossProgress)packet;
				managerRPC.onTCPMapServerRecvSyncWorldMapBossProgress(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncBWArenaMapCopyStart:
			{
				Packet.M2S.SyncBWArenaMapCopyStart p = (Packet.M2S.SyncBWArenaMapCopyStart)packet;
				managerRPC.onTCPMapServerRecvSyncBWArenaMapCopyStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncBWArenaMapCopyEnd:
			{
				Packet.M2S.SyncBWArenaMapCopyEnd p = (Packet.M2S.SyncBWArenaMapCopyEnd)packet;
				managerRPC.onTCPMapServerRecvSyncBWArenaMapCopyEnd(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncPetLifeMapCopyStart:
			{
				Packet.M2S.SyncPetLifeMapCopyStart p = (Packet.M2S.SyncPetLifeMapCopyStart)packet;
				managerRPC.onTCPMapServerRecvSyncPetLifeMapCopyStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncCurRideHorse:
			{
				Packet.M2S.SyncCurRideHorse p = (Packet.M2S.SyncCurRideHorse)packet;
				managerRPC.onTCPMapServerRecvSyncCurRideHorse(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncCarLocation:
			{
				Packet.M2S.SyncCarLocation p = (Packet.M2S.SyncCarLocation)packet;
				managerRPC.onTCPMapServerRecvSyncCarLocation(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncCarHp:
			{
				Packet.M2S.SyncCarHp p = (Packet.M2S.SyncCarHp)packet;
				managerRPC.onTCPMapServerRecvSyncCarHp(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTUpdateCarDamage:
			{
				Packet.M2S.UpdateCarDamage p = (Packet.M2S.UpdateCarDamage)packet;
				managerRPC.onTCPMapServerRecvUpdateCarDamage(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncRoleRobSuccess:
			{
				Packet.M2S.SyncRoleRobSuccess p = (Packet.M2S.SyncRoleRobSuccess)packet;
				managerRPC.onTCPMapServerRecvSyncRoleRobSuccess(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTUpdateRoleCarRobber:
			{
				Packet.M2S.UpdateRoleCarRobber p = (Packet.M2S.UpdateRoleCarRobber)packet;
				managerRPC.onTCPMapServerRecvUpdateRoleCarRobber(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTKickRoleFromMap:
			{
				Packet.M2S.KickRoleFromMap p = (Packet.M2S.KickRoleFromMap)packet;
				managerRPC.onTCPMapServerRecvKickRoleFromMap(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTRoleUseItemSkillSuc:
			{
				Packet.M2S.RoleUseItemSkillSuc p = (Packet.M2S.RoleUseItemSkillSuc)packet;
				managerRPC.onTCPMapServerRecvRoleUseItemSkillSuc(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTUpdateRoleFightState:
			{
				Packet.M2S.UpdateRoleFightState p = (Packet.M2S.UpdateRoleFightState)packet;
				managerRPC.onTCPMapServerRecvUpdateRoleFightState(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncRolePetHp:
			{
				Packet.M2S.SyncRolePetHp p = (Packet.M2S.SyncRolePetHp)packet;
				managerRPC.onTCPMapServerRecvSyncRolePetHp(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncRoleSp:
			{
				Packet.M2S.SyncRoleSp p = (Packet.M2S.SyncRoleSp)packet;
				managerRPC.onTCPMapServerRecvSyncRoleSp(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncWorldMapBossRecord:
			{
				Packet.M2S.SyncWorldMapBossRecord p = (Packet.M2S.SyncWorldMapBossRecord)packet;
				managerRPC.onTCPMapServerRecvSyncWorldMapBossRecord(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncArmorVal:
			{
				Packet.M2S.SyncArmorVal p = (Packet.M2S.SyncArmorVal)packet;
				managerRPC.onTCPMapServerRecvSyncArmorVal(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyStatus:
			{
				Packet.M2S.SyncSectGroupMapCopyStatus p = (Packet.M2S.SyncSectGroupMapCopyStatus)packet;
				managerRPC.onTCPMapServerRecvSyncSectGroupMapCopyStatus(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyResult:
			{
				Packet.M2S.SyncSectGroupMapCopyResult p = (Packet.M2S.SyncSectGroupMapCopyResult)packet;
				managerRPC.onTCPMapServerRecvSyncSectGroupMapCopyResult(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyStart:
			{
				Packet.M2S.SyncSectGroupMapCopyStart p = (Packet.M2S.SyncSectGroupMapCopyStart)packet;
				managerRPC.onTCPMapServerRecvSyncSectGroupMapCopyStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyProgress:
			{
				Packet.M2S.SyncSectGroupMapCopyProgress p = (Packet.M2S.SyncSectGroupMapCopyProgress)packet;
				managerRPC.onTCPMapServerRecvSyncSectGroupMapCopyProgress(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSectGroupMapCopyAddKill:
			{
				Packet.M2S.SyncSectGroupMapCopyAddKill p = (Packet.M2S.SyncSectGroupMapCopyAddKill)packet;
				managerRPC.onTCPMapServerRecvSyncSectGroupMapCopyAddKill(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncMapFlagCanTake:
			{
				Packet.M2S.SyncMapFlagCanTake p = (Packet.M2S.SyncMapFlagCanTake)packet;
				managerRPC.onTCPMapServerRecvSyncMapFlagCanTake(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncWeaponMaster:
			{
				Packet.M2S.SyncWeaponMaster p = (Packet.M2S.SyncWeaponMaster)packet;
				managerRPC.onTCPMapServerRecvSyncWeaponMaster(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTRolePickUpDrops:
			{
				Packet.M2S.RolePickUpDrops p = (Packet.M2S.RolePickUpDrops)packet;
				managerRPC.onTCPMapServerRecvRolePickUpDrops(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSuperMonster:
			{
				Packet.M2S.SyncSuperMonster p = (Packet.M2S.SyncSuperMonster)packet;
				managerRPC.onTCPMapServerRecvSyncSuperMonster(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncWorldMineral:
			{
				Packet.M2S.SyncWorldMineral p = (Packet.M2S.SyncWorldMineral)packet;
				managerRPC.onTCPMapServerRecvSyncWorldMineral(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncMarriageParadeEnd:
			{
				Packet.M2S.SyncMarriageParadeEnd p = (Packet.M2S.SyncMarriageParadeEnd)packet;
				managerRPC.onTCPMapServerRecvSyncMarriageParadeEnd(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTRolePickUpRareDrops:
			{
				Packet.M2S.RolePickUpRareDrops p = (Packet.M2S.RolePickUpRareDrops)packet;
				managerRPC.onTCPMapServerRecvRolePickUpRareDrops(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncWorldBossDamageRoles:
			{
				Packet.M2S.SyncWorldBossDamageRoles p = (Packet.M2S.SyncWorldBossDamageRoles)packet;
				managerRPC.onTCPMapServerRecvSyncWorldBossDamageRoles(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncSteleRemainTimes:
			{
				Packet.M2S.SyncSteleRemainTimes p = (Packet.M2S.SyncSteleRemainTimes)packet;
				managerRPC.onTCPMapServerRecvSyncSteleRemainTimes(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncRoleAddSteleCard:
			{
				Packet.M2S.SyncRoleAddSteleCard p = (Packet.M2S.SyncRoleAddSteleCard)packet;
				managerRPC.onTCPMapServerRecvSyncRoleAddSteleCard(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncRefreshSteleMonster:
			{
				Packet.M2S.SyncRefreshSteleMonster p = (Packet.M2S.SyncRefreshSteleMonster)packet;
				managerRPC.onTCPMapServerRecvSyncRefreshSteleMonster(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncEmergencyMapStart:
			{
				Packet.M2S.SyncEmergencyMapStart p = (Packet.M2S.SyncEmergencyMapStart)packet;
				managerRPC.onTCPMapServerRecvSyncEmergencyMapStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncEmergencyMapKillMonster:
			{
				Packet.M2S.SyncEmergencyMapKillMonster p = (Packet.M2S.SyncEmergencyMapKillMonster)packet;
				managerRPC.onTCPMapServerRecvSyncEmergencyMapKillMonster(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncEmergencyMapEnd:
			{
				Packet.M2S.SyncEmergencyMapEnd p = (Packet.M2S.SyncEmergencyMapEnd)packet;
				managerRPC.onTCPMapServerRecvSyncEmergencyMapEnd(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncFightNpcMapStart:
			{
				Packet.M2S.SyncFightNpcMapStart p = (Packet.M2S.SyncFightNpcMapStart)packet;
				managerRPC.onTCPMapServerRecvSyncFightNpcMapStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncFightNpcMapEnd:
			{
				Packet.M2S.SyncFightNpcMapEnd p = (Packet.M2S.SyncFightNpcMapEnd)packet;
				managerRPC.onTCPMapServerRecvSyncFightNpcMapEnd(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncTowerDefenceMapStart:
			{
				Packet.M2S.SyncTowerDefenceMapStart p = (Packet.M2S.SyncTowerDefenceMapStart)packet;
				managerRPC.onTCPMapServerRecvSyncTowerDefenceMapStart(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncTowerDefenceMapEnd:
			{
				Packet.M2S.SyncTowerDefenceMapEnd p = (Packet.M2S.SyncTowerDefenceMapEnd)packet;
				managerRPC.onTCPMapServerRecvSyncTowerDefenceMapEnd(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncTowerDefenceSpawnCount:
			{
				Packet.M2S.SyncTowerDefenceSpawnCount p = (Packet.M2S.SyncTowerDefenceSpawnCount)packet;
				managerRPC.onTCPMapServerRecvSyncTowerDefenceSpawnCount(this, p, sessionid);
			}
			break;
		case Packet.eM2SPKTSyncTowerDefenceScore:
			{
				Packet.M2S.SyncTowerDefenceScore p = (Packet.M2S.SyncTowerDefenceScore)packet;
				managerRPC.onTCPMapServerRecvSyncTowerDefenceScore(this, p, sessionid);
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
