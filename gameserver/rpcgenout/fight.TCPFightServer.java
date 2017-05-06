// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.fight;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPFightServer extends TCPServer<SimplePacket>
{

	public TCPFightServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPFightServerMaxConnectionIdleTime();
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// gs to fs
			case Packet.eS2FPKTKeepAlive:
			case Packet.eS2FPKTWhoAmI:
			case Packet.eS2FPKTReportTimeOffset:
			case Packet.eS2FPKTRoleJoinForceWarReq:
			case Packet.eS2FPKTRoleQuitForceWarReq:
			case Packet.eS2FPKTUpdateFightRank:
			case Packet.eS2FPKTSendMsgFight:
			case Packet.eS2FPKTSingleJoinSuperArenaReq:
			case Packet.eS2FPKTSingleQuitSuperArenaReq:
			case Packet.eS2FPKTTeamJoinSuperArenaReq:
			case Packet.eS2FPKTTeamQuitSuperArenaReq:
			case Packet.eS2FPKTQueryTeamMembersReq:
			case Packet.eS2FPKTRoleLeaveTeam:
			case Packet.eS2FPKTQueryTeamMemberReq:
			case Packet.eS2FPKTLeaveMap:
			case Packet.eS2FPKTSendMsgGlobalTeam:
			case Packet.eS2FPKTTeamJoinForceWarReq:
			case Packet.eS2FPKTTeamQuitForceWarReq:
			case Packet.eS2FPKTSyncRoleDemonHoleReq:
			case Packet.eS2FPKTRoleJoinDemonHoleReq:
			case Packet.eS2FPKTRoleChangeDemonHoleFloorReq:
			case Packet.eS2FPKTRoleDemonHoleBattleReq:
			case Packet.eS2FPKTRoleEnterDemonHoleFloor:
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
		managerRPC.onTCPFightServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPFightServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// gs to fs
		case Packet.eS2FPKTKeepAlive:
			{
				Packet.S2F.KeepAlive p = (Packet.S2F.KeepAlive)packet;
				managerRPC.onTCPFightServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTWhoAmI:
			{
				Packet.S2F.WhoAmI p = (Packet.S2F.WhoAmI)packet;
				managerRPC.onTCPFightServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTReportTimeOffset:
			{
				Packet.S2F.ReportTimeOffset p = (Packet.S2F.ReportTimeOffset)packet;
				managerRPC.onTCPFightServerRecvReportTimeOffset(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTRoleJoinForceWarReq:
			{
				Packet.S2F.RoleJoinForceWarReq p = (Packet.S2F.RoleJoinForceWarReq)packet;
				managerRPC.onTCPFightServerRecvRoleJoinForceWarReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTRoleQuitForceWarReq:
			{
				Packet.S2F.RoleQuitForceWarReq p = (Packet.S2F.RoleQuitForceWarReq)packet;
				managerRPC.onTCPFightServerRecvRoleQuitForceWarReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTUpdateFightRank:
			{
				Packet.S2F.UpdateFightRank p = (Packet.S2F.UpdateFightRank)packet;
				managerRPC.onTCPFightServerRecvUpdateFightRank(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTSendMsgFight:
			{
				Packet.S2F.SendMsgFight p = (Packet.S2F.SendMsgFight)packet;
				managerRPC.onTCPFightServerRecvSendMsgFight(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTSingleJoinSuperArenaReq:
			{
				Packet.S2F.SingleJoinSuperArenaReq p = (Packet.S2F.SingleJoinSuperArenaReq)packet;
				managerRPC.onTCPFightServerRecvSingleJoinSuperArenaReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTSingleQuitSuperArenaReq:
			{
				Packet.S2F.SingleQuitSuperArenaReq p = (Packet.S2F.SingleQuitSuperArenaReq)packet;
				managerRPC.onTCPFightServerRecvSingleQuitSuperArenaReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTTeamJoinSuperArenaReq:
			{
				Packet.S2F.TeamJoinSuperArenaReq p = (Packet.S2F.TeamJoinSuperArenaReq)packet;
				managerRPC.onTCPFightServerRecvTeamJoinSuperArenaReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTTeamQuitSuperArenaReq:
			{
				Packet.S2F.TeamQuitSuperArenaReq p = (Packet.S2F.TeamQuitSuperArenaReq)packet;
				managerRPC.onTCPFightServerRecvTeamQuitSuperArenaReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTQueryTeamMembersReq:
			{
				Packet.S2F.QueryTeamMembersReq p = (Packet.S2F.QueryTeamMembersReq)packet;
				managerRPC.onTCPFightServerRecvQueryTeamMembersReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTRoleLeaveTeam:
			{
				Packet.S2F.RoleLeaveTeam p = (Packet.S2F.RoleLeaveTeam)packet;
				managerRPC.onTCPFightServerRecvRoleLeaveTeam(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTQueryTeamMemberReq:
			{
				Packet.S2F.QueryTeamMemberReq p = (Packet.S2F.QueryTeamMemberReq)packet;
				managerRPC.onTCPFightServerRecvQueryTeamMemberReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTLeaveMap:
			{
				Packet.S2F.LeaveMap p = (Packet.S2F.LeaveMap)packet;
				managerRPC.onTCPFightServerRecvLeaveMap(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTSendMsgGlobalTeam:
			{
				Packet.S2F.SendMsgGlobalTeam p = (Packet.S2F.SendMsgGlobalTeam)packet;
				managerRPC.onTCPFightServerRecvSendMsgGlobalTeam(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTTeamJoinForceWarReq:
			{
				Packet.S2F.TeamJoinForceWarReq p = (Packet.S2F.TeamJoinForceWarReq)packet;
				managerRPC.onTCPFightServerRecvTeamJoinForceWarReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTTeamQuitForceWarReq:
			{
				Packet.S2F.TeamQuitForceWarReq p = (Packet.S2F.TeamQuitForceWarReq)packet;
				managerRPC.onTCPFightServerRecvTeamQuitForceWarReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTSyncRoleDemonHoleReq:
			{
				Packet.S2F.SyncRoleDemonHoleReq p = (Packet.S2F.SyncRoleDemonHoleReq)packet;
				managerRPC.onTCPFightServerRecvSyncRoleDemonHoleReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTRoleJoinDemonHoleReq:
			{
				Packet.S2F.RoleJoinDemonHoleReq p = (Packet.S2F.RoleJoinDemonHoleReq)packet;
				managerRPC.onTCPFightServerRecvRoleJoinDemonHoleReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTRoleChangeDemonHoleFloorReq:
			{
				Packet.S2F.RoleChangeDemonHoleFloorReq p = (Packet.S2F.RoleChangeDemonHoleFloorReq)packet;
				managerRPC.onTCPFightServerRecvRoleChangeDemonHoleFloorReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTRoleDemonHoleBattleReq:
			{
				Packet.S2F.RoleDemonHoleBattleReq p = (Packet.S2F.RoleDemonHoleBattleReq)packet;
				managerRPC.onTCPFightServerRecvRoleDemonHoleBattleReq(this, p, sessionid);
			}
			break;
		case Packet.eS2FPKTRoleEnterDemonHoleFloor:
			{
				Packet.S2F.RoleEnterDemonHoleFloor p = (Packet.S2F.RoleEnterDemonHoleFloor)packet;
				managerRPC.onTCPFightServerRecvRoleEnterDemonHoleFloor(this, p, sessionid);
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
