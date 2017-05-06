// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPGlobalMapClient extends TCPClient<SimplePacket>
{

	public TCPGlobalMapClient(RPCManager managerRPC)
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
			// global ms to gs
			case Packet.eGM2SPKTKeepAlive:
			case Packet.eGM2SPKTSyncGlobalMaps:
			case Packet.eGM2SPKTLuaChannel:
			case Packet.eGM2SPKTStrChannel:
			case Packet.eGM2SPKTMapRoleReady:
			case Packet.eGM2SPKTSyncLocation:
			case Packet.eGM2SPKTSyncHp:
			case Packet.eGM2SPKTAddDrops:
			case Packet.eGM2SPKTAddKill:
			case Packet.eGM2SPKTSyncDurability:
			case Packet.eGM2SPKTSyncEndMine:
			case Packet.eGM2SPKTKickRoleFromMap:
			case Packet.eGM2SPKTRoleUseItemSkillSuc:
			case Packet.eGM2SPKTUpdateRoleFightState:
			case Packet.eGM2SPKTSyncRolePetHp:
			case Packet.eGM2SPKTSyncArmorVal:
			case Packet.eGM2SPKTSyncWeaponMaster:
			case Packet.eGM2SPKTRolePickUpDrops:
			case Packet.eGM2SPKTRolePickUpRareDrops:
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
		managerRPC.onTCPGlobalMapClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPGlobalMapClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPGlobalMapClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// global ms to gs
		case Packet.eGM2SPKTKeepAlive:
			{
				Packet.GM2S.KeepAlive p = (Packet.GM2S.KeepAlive)packet;
				managerRPC.onTCPGlobalMapClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncGlobalMaps:
			{
				Packet.GM2S.SyncGlobalMaps p = (Packet.GM2S.SyncGlobalMaps)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncGlobalMaps(this, p);
			}
			break;
		case Packet.eGM2SPKTLuaChannel:
			{
				Packet.GM2S.LuaChannel p = (Packet.GM2S.LuaChannel)packet;
				managerRPC.onTCPGlobalMapClientRecvLuaChannel(this, p);
			}
			break;
		case Packet.eGM2SPKTStrChannel:
			{
				Packet.GM2S.StrChannel p = (Packet.GM2S.StrChannel)packet;
				managerRPC.onTCPGlobalMapClientRecvStrChannel(this, p);
			}
			break;
		case Packet.eGM2SPKTMapRoleReady:
			{
				Packet.GM2S.MapRoleReady p = (Packet.GM2S.MapRoleReady)packet;
				managerRPC.onTCPGlobalMapClientRecvMapRoleReady(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncLocation:
			{
				Packet.GM2S.SyncLocation p = (Packet.GM2S.SyncLocation)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncLocation(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncHp:
			{
				Packet.GM2S.SyncHp p = (Packet.GM2S.SyncHp)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncHp(this, p);
			}
			break;
		case Packet.eGM2SPKTAddDrops:
			{
				Packet.GM2S.AddDrops p = (Packet.GM2S.AddDrops)packet;
				managerRPC.onTCPGlobalMapClientRecvAddDrops(this, p);
			}
			break;
		case Packet.eGM2SPKTAddKill:
			{
				Packet.GM2S.AddKill p = (Packet.GM2S.AddKill)packet;
				managerRPC.onTCPGlobalMapClientRecvAddKill(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncDurability:
			{
				Packet.GM2S.SyncDurability p = (Packet.GM2S.SyncDurability)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncDurability(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncEndMine:
			{
				Packet.GM2S.SyncEndMine p = (Packet.GM2S.SyncEndMine)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncEndMine(this, p);
			}
			break;
		case Packet.eGM2SPKTKickRoleFromMap:
			{
				Packet.GM2S.KickRoleFromMap p = (Packet.GM2S.KickRoleFromMap)packet;
				managerRPC.onTCPGlobalMapClientRecvKickRoleFromMap(this, p);
			}
			break;
		case Packet.eGM2SPKTRoleUseItemSkillSuc:
			{
				Packet.GM2S.RoleUseItemSkillSuc p = (Packet.GM2S.RoleUseItemSkillSuc)packet;
				managerRPC.onTCPGlobalMapClientRecvRoleUseItemSkillSuc(this, p);
			}
			break;
		case Packet.eGM2SPKTUpdateRoleFightState:
			{
				Packet.GM2S.UpdateRoleFightState p = (Packet.GM2S.UpdateRoleFightState)packet;
				managerRPC.onTCPGlobalMapClientRecvUpdateRoleFightState(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncRolePetHp:
			{
				Packet.GM2S.SyncRolePetHp p = (Packet.GM2S.SyncRolePetHp)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncRolePetHp(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncArmorVal:
			{
				Packet.GM2S.SyncArmorVal p = (Packet.GM2S.SyncArmorVal)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncArmorVal(this, p);
			}
			break;
		case Packet.eGM2SPKTSyncWeaponMaster:
			{
				Packet.GM2S.SyncWeaponMaster p = (Packet.GM2S.SyncWeaponMaster)packet;
				managerRPC.onTCPGlobalMapClientRecvSyncWeaponMaster(this, p);
			}
			break;
		case Packet.eGM2SPKTRolePickUpDrops:
			{
				Packet.GM2S.RolePickUpDrops p = (Packet.GM2S.RolePickUpDrops)packet;
				managerRPC.onTCPGlobalMapClientRecvRolePickUpDrops(this, p);
			}
			break;
		case Packet.eGM2SPKTRolePickUpRareDrops:
			{
				Packet.GM2S.RolePickUpRareDrops p = (Packet.GM2S.RolePickUpRareDrops)packet;
				managerRPC.onTCPGlobalMapClientRecvRolePickUpRareDrops(this, p);
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
