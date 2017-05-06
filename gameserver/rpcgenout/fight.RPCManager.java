// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.fight;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManager
{

	//// begin handlers.
	public int getTCPFightServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPFightServerOpen(TCPFightServer peer)
	{
		// TODO
	}

	public void onTCPFightServerOpenFailed(TCPFightServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightServerClose(TCPFightServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightServerSessionOpen(TCPFightServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPFightServerSessionClose(TCPFightServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightServerRecvKeepAlive(TCPFightServer peer, Packet.S2F.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvWhoAmI(TCPFightServer peer, Packet.S2F.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvReportTimeOffset(TCPFightServer peer, Packet.S2F.ReportTimeOffset packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvRoleJoinForceWarReq(TCPFightServer peer, Packet.S2F.RoleJoinForceWarReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvRoleQuitForceWarReq(TCPFightServer peer, Packet.S2F.RoleQuitForceWarReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvUpdateFightRank(TCPFightServer peer, Packet.S2F.UpdateFightRank packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvSendMsgFight(TCPFightServer peer, Packet.S2F.SendMsgFight packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvSingleJoinSuperArenaReq(TCPFightServer peer, Packet.S2F.SingleJoinSuperArenaReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvSingleQuitSuperArenaReq(TCPFightServer peer, Packet.S2F.SingleQuitSuperArenaReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvTeamJoinSuperArenaReq(TCPFightServer peer, Packet.S2F.TeamJoinSuperArenaReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvTeamQuitSuperArenaReq(TCPFightServer peer, Packet.S2F.TeamQuitSuperArenaReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvQueryTeamMembersReq(TCPFightServer peer, Packet.S2F.QueryTeamMembersReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvRoleLeaveTeam(TCPFightServer peer, Packet.S2F.RoleLeaveTeam packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvQueryTeamMemberReq(TCPFightServer peer, Packet.S2F.QueryTeamMemberReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvLeaveMap(TCPFightServer peer, Packet.S2F.LeaveMap packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvSendMsgGlobalTeam(TCPFightServer peer, Packet.S2F.SendMsgGlobalTeam packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvTeamJoinForceWarReq(TCPFightServer peer, Packet.S2F.TeamJoinForceWarReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvTeamQuitForceWarReq(TCPFightServer peer, Packet.S2F.TeamQuitForceWarReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvSyncRoleDemonHoleReq(TCPFightServer peer, Packet.S2F.SyncRoleDemonHoleReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvRoleJoinDemonHoleReq(TCPFightServer peer, Packet.S2F.RoleJoinDemonHoleReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvRoleChangeDemonHoleFloorReq(TCPFightServer peer, Packet.S2F.RoleChangeDemonHoleFloorReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvRoleDemonHoleBattleReq(TCPFightServer peer, Packet.S2F.RoleDemonHoleBattleReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightServerRecvRoleEnterDemonHoleFloor(TCPFightServer peer, Packet.S2F.RoleEnterDemonHoleFloor packet, int sessionid)
	{
		// TODO
	}

	public int getTCPFightMapServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPFightMapServerOpen(TCPFightMapServer peer)
	{
		// TODO
	}

	public void onTCPFightMapServerOpenFailed(TCPFightMapServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightMapServerClose(TCPFightMapServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightMapServerSessionOpen(TCPFightMapServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPFightMapServerSessionClose(TCPFightMapServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvKeepAlive(TCPFightMapServer peer, Packet.GM2F.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvWhoAmI(TCPFightMapServer peer, Packet.GM2F.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvCreateMapCopyRes(TCPFightMapServer peer, Packet.GM2F.CreateMapCopyRes packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvSyncForceWarMapEnd(TCPFightMapServer peer, Packet.GM2F.SyncForceWarMapEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvSyncSuperArenaMapEnd(TCPFightMapServer peer, Packet.GM2F.SyncSuperArenaMapEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvSyncHp(TCPFightMapServer peer, Packet.GM2F.SyncHp packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvSyncSuperArenaRaceEnd(TCPFightMapServer peer, Packet.GM2F.SyncSuperArenaRaceEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPFightMapServerRecvSyncDemonHoleKill(TCPFightMapServer peer, Packet.GM2F.SyncDemonHoleKill packet, int sessionid)
	{
		// TODO
	}

	//// end handlers.
}
