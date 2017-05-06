// modified by ket.kio.RPCGen at Fri Mar 31 18:59:59 CST 2017.

package i3k.gmap;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPGlobalMapServer extends TCPServer<SimplePacket>
{

	public TCPGlobalMapServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPGlobalMapServerMaxConnectionIdleTime();
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// gs to global ms
			case Packet.eS2GMPKTKeepAlive:
			case Packet.eS2GMPKTWhoAmI:
			case Packet.eS2GMPKTReportTimeOffset:
			case Packet.eS2GMPKTLuaChannel:
			case Packet.eS2GMPKTStrChannel:
			case Packet.eS2GMPKTEnterMap:
			case Packet.eS2GMPKTLeaveMap:
			case Packet.eS2GMPKTUpdateActive:
			case Packet.eS2GMPKTAddHp:
			case Packet.eS2GMPKTRoleUseItemSkill:
			case Packet.eS2GMPKTAddPetHp:
			case Packet.eS2GMPKTStartMine:
			case Packet.eS2GMPKTResetLocation:
			case Packet.eS2GMPKTUpdateCurSkills:
			case Packet.eS2GMPKTUpdateCurSpirit:
			case Packet.eS2GMPKTPickUpResult:
			case Packet.eS2GMPKTUpdateRoleMarriageSkillInfo:
			case Packet.eS2GMPKTUpdateRoleMarriageSkillLevel:
			case Packet.eS2GMPKTRoleRevive:
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
		managerRPC.onTCPGlobalMapServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPGlobalMapServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPGlobalMapServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPGlobalMapServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPGlobalMapServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// gs to global ms
		case Packet.eS2GMPKTKeepAlive:
			{
				Packet.S2GM.KeepAlive p = (Packet.S2GM.KeepAlive)packet;
				managerRPC.onTCPGlobalMapServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTWhoAmI:
			{
				Packet.S2GM.WhoAmI p = (Packet.S2GM.WhoAmI)packet;
				managerRPC.onTCPGlobalMapServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTReportTimeOffset:
			{
				Packet.S2GM.ReportTimeOffset p = (Packet.S2GM.ReportTimeOffset)packet;
				managerRPC.onTCPGlobalMapServerRecvReportTimeOffset(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTLuaChannel:
			{
				Packet.S2GM.LuaChannel p = (Packet.S2GM.LuaChannel)packet;
				managerRPC.onTCPGlobalMapServerRecvLuaChannel(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTStrChannel:
			{
				Packet.S2GM.StrChannel p = (Packet.S2GM.StrChannel)packet;
				managerRPC.onTCPGlobalMapServerRecvStrChannel(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTEnterMap:
			{
				Packet.S2GM.EnterMap p = (Packet.S2GM.EnterMap)packet;
				managerRPC.onTCPGlobalMapServerRecvEnterMap(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTLeaveMap:
			{
				Packet.S2GM.LeaveMap p = (Packet.S2GM.LeaveMap)packet;
				managerRPC.onTCPGlobalMapServerRecvLeaveMap(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTUpdateActive:
			{
				Packet.S2GM.UpdateActive p = (Packet.S2GM.UpdateActive)packet;
				managerRPC.onTCPGlobalMapServerRecvUpdateActive(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTAddHp:
			{
				Packet.S2GM.AddHp p = (Packet.S2GM.AddHp)packet;
				managerRPC.onTCPGlobalMapServerRecvAddHp(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTRoleUseItemSkill:
			{
				Packet.S2GM.RoleUseItemSkill p = (Packet.S2GM.RoleUseItemSkill)packet;
				managerRPC.onTCPGlobalMapServerRecvRoleUseItemSkill(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTAddPetHp:
			{
				Packet.S2GM.AddPetHp p = (Packet.S2GM.AddPetHp)packet;
				managerRPC.onTCPGlobalMapServerRecvAddPetHp(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTStartMine:
			{
				Packet.S2GM.StartMine p = (Packet.S2GM.StartMine)packet;
				managerRPC.onTCPGlobalMapServerRecvStartMine(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTResetLocation:
			{
				Packet.S2GM.ResetLocation p = (Packet.S2GM.ResetLocation)packet;
				managerRPC.onTCPGlobalMapServerRecvResetLocation(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTUpdateCurSkills:
			{
				Packet.S2GM.UpdateCurSkills p = (Packet.S2GM.UpdateCurSkills)packet;
				managerRPC.onTCPGlobalMapServerRecvUpdateCurSkills(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTUpdateCurSpirit:
			{
				Packet.S2GM.UpdateCurSpirit p = (Packet.S2GM.UpdateCurSpirit)packet;
				managerRPC.onTCPGlobalMapServerRecvUpdateCurSpirit(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTPickUpResult:
			{
				Packet.S2GM.PickUpResult p = (Packet.S2GM.PickUpResult)packet;
				managerRPC.onTCPGlobalMapServerRecvPickUpResult(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTUpdateRoleMarriageSkillInfo:
			{
				Packet.S2GM.UpdateRoleMarriageSkillInfo p = (Packet.S2GM.UpdateRoleMarriageSkillInfo)packet;
				managerRPC.onTCPGlobalMapServerRecvUpdateRoleMarriageSkillInfo(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTUpdateRoleMarriageSkillLevel:
			{
				Packet.S2GM.UpdateRoleMarriageSkillLevel p = (Packet.S2GM.UpdateRoleMarriageSkillLevel)packet;
				managerRPC.onTCPGlobalMapServerRecvUpdateRoleMarriageSkillLevel(this, p, sessionid);
			}
			break;
		case Packet.eS2GMPKTRoleRevive:
			{
				Packet.S2GM.RoleRevive p = (Packet.S2GM.RoleRevive)packet;
				managerRPC.onTCPGlobalMapServerRecvRoleRevive(this, p, sessionid);
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
