// modified by ket.kio.RPCGen at Fri Mar 31 18:59:59 CST 2017.

package i3k.fight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ket.kio.NetAddress;
import ket.kio.NetManager;
import i3k.alarm.TCPAlarmServer;
import i3k.gs.GameData;
import i3k.rpc.Packet;
import i3k.util.GameTime;
import i3k.util.GameServerTable;
import i3k.SBean;

public class RPCManager
{
	public RPCManager(FightServer as)
	{
		this.fs = as;
	}
	
	public void onTimer(int timeTick)
	{
		if (managerNet != null)
			managerNet.checkIdleConnections();
		if (talarms != null)
			talarms.onTimer();
	}
	
	public void start()
	{
		if( fs.getConfig().nIOThread == 1 )
			managerNet = new NetManager(NetManager.NetManagerType.eSelectNetManager, fs.getConfig().nIOThread);
		else
			managerNet = new NetManager(NetManager.NetManagerType.eMTSelectNetManager, fs.getConfig().nIOThread);
		managerNet.start();
		
		tfs = new TCPFightServer(this);
		tfms = new TCPFightMapServer(this);
		talarms = new TCPAlarmServer(this.getNetManager(), fs.getConfig().addrAlarmListen, fs.getLogger());
		
		tfs.setListenAddr(fs.getConfig().addrFightListen, ket.kio.BindPolicy.eReuseTimewait);
		tfs.setListenBacklog(128);
		tfs.open();
		
		tfms.setListenAddr(fs.getConfig().addrFightMapListen, ket.kio.BindPolicy.eReuseTimewait);
		tfms.setListenBacklog(128);
		tfms.open();
		
		talarms.start();
	}
	public void destroy()
	{
		if (managerNet != null)
			managerNet.destroy();
	}
	
	public NetManager getNetManager() { return managerNet; }
	
	//// begin handlers.
	public int getTCPFightServerMaxConnectionIdleTime()
	{
		return 30 * 1000;
	}

	public void onTCPFightServerOpen(TCPFightServer peer)
	{
		fs.getLogger().info("TCPFightServer open on " + peer.getListenAddr());
	}

	public void onTCPFightServerOpenFailed(TCPFightServer peer, ket.kio.ErrorCode errcode)
	{
		fs.getLogger().warn("TCPFightServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPFightServerClose(TCPFightServer peer, ket.kio.ErrorCode errcode)
	{
		fs.getLogger().info("TCPFightServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPFightServerSessionOpen(TCPFightServer peer, int sessionid, NetAddress addrClient)
	{
		fs.getLogger().info("TCPFightServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPFightServerSessionClose(TCPFightServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		fs.getLogger().info("TCPFightServer on session " + sessionid  + " close, errcode=" + errcode);
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID != null)
		{
			fs.getFightManager().clearRolesByServerID(serverID);
			fs.getRoleManager().clearServerRoles(serverID);
			fs.getSuperArenaManager().clearServerRoles(serverID);
		}
		table.onSessionClose(sessionid);
	}

	public void onTCPFightServerRecvKeepAlive(TCPFightServer peer, Packet.S2F.KeepAlive packet, int sessionid)
	{
		fs.getLogger().debug("receive gs session " + sessionid + " gameserver " + packet.getHello() + " keepalive packet");
	}

	public void onTCPFightServerRecvWhoAmI(TCPFightServer peer, Packet.S2F.WhoAmI packet, int sessionid)
	{
		fs.getLogger().debug("receive gs session " + sessionid + " gameserver " + packet.getServerId() + " zones " + " whoami packet");
		if (GameData.getAreaIdFromGSId(packet.getServerId()) != fs.getConfig().areaId)
		{
			fs.getLogger().warn("close gs session " + sessionid + " for gameserver " + packet.getServerId() + " not match area id " + fs.getConfig().areaId);
			tfs.closeSession(sessionid);
			return;
		}
		
		Integer oldSession = table.tryCloseSessionByServerID(packet.getServerId());
		if(oldSession != null)
		{
			fs.getLogger().warn("close gs session " + oldSession + " on gameserver ]" + packet.getServerId() + " " + sessionid + "] announce");
			tfs.closeSession(oldSession);
		}
		
		if (!table.onSessionAnnounce(sessionid, packet.getServerId(), packet.getZones()))
		{
			fs.getLogger().warn("close gs session " + sessionid + " for gameserver " + packet.getServerId() + " or zones " + packet.getZones() + " clash");
			tfs.closeSession(sessionid);
			return;
		}
		fs.getFightRankManager().onGameServerConnect(packet.getServerId());
	}

	public void onTCPFightServerRecvReportTimeOffset(TCPFightServer peer, Packet.S2F.ReportTimeOffset packet, int sessionid)
	{
		fs.getLogger().info("receive gs session " + sessionid + " report gs time offset " + packet.getTimeOffset() + ", current time offset " + GameTime.getServerTimeOffset());
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if (serverID == null)
			return;
		GameTime.setServerTimeOffset(packet.getTimeOffset());
	}

	public void onTCPFightServerRecvRoleJoinForceWarReq(TCPFightServer peer, Packet.S2F.RoleJoinForceWarReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getJoinInfo().overview.id + " join force war");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if (serverID == null)
			return;
		
		fs.getFightManager().roleJoin(packet.getJoinInfo(), packet.getForcewarType(), ok -> 
		{
			fs.getRPCManager().notifyGSRoleJoinForceWarRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvRoleQuitForceWarReq(TCPFightServer peer, Packet.S2F.RoleQuitForceWarReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " quit force war");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if (serverID == null)
			return;
		
		fs.getFightManager().roleQuit(packet.getRoleID(), packet.getBwType(), packet.getForcewarType(), ok -> 
		{
			fs.getRPCManager().notifyGSRoleQuitForceWarRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvUpdateFightRank(TCPFightServer peer, Packet.S2F.UpdateFightRank packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRankRole().role.id + " update rank " + packet.getRankID() + " rankRole ...");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if (serverID == null)
			return;
		
		fs.getFightRankManager().tryUpdateRank(serverID, packet.getRankID(),  packet.getRankClearTime(), packet.getRankRole());
	}

	public void onTCPFightServerRecvSendMsgFight(TCPFightServer peer, Packet.S2F.SendMsgFight packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleId() + " sends force war msg");
		fs.getGlobalMapService().handleForceWarSendMsg(packet.getMapId(), packet.getMapInstance(), packet.getRoleId(), packet.getMsgContent());
	}

	public void onTCPFightServerRecvSingleJoinSuperArenaReq(TCPFightServer peer, Packet.S2F.SingleJoinSuperArenaReq packet, int sessionid)
	{
		fs.getLogger().debug("receive gs session " + sessionid + " role single join super arena");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if (serverID == null)
			return;
		
		fs.getSuperArenaManager().singleJoin(serverID, packet.getJoinInfo(), packet.getGrade(), packet.getArenaType(), ok -> {
			notifyGSSingleJoinSuperArenaRes(sessionid, packet.getTagID(), ok);
		});
	}
	
	public void onTCPFightServerRecvSingleQuitSuperArenaReq(TCPFightServer peer, Packet.S2F.SingleQuitSuperArenaReq packet, int sessionid)
	{
		fs.getLogger().debug("receive gs session " + sessionid + " role single quit super arena");
		fs.getSuperArenaManager().singleQuit(packet.getRoleID(), packet.getGrade(), packet.getArenaType(), ok -> {
			notifyGSSingleQuitSuperArenaRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvTeamJoinSuperArenaReq(TCPFightServer peer, Packet.S2F.TeamJoinSuperArenaReq packet, int sessionid)
	{
		fs.getLogger().debug("receive gs session " + sessionid + " role team join super arena");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if (serverID == null)
			return;
		
		fs.getSuperArenaManager().teamJoin(serverID, packet.getMembers(), packet.getGrade(), packet.getArenaType(), ok -> {
			notifyGSTeamJoinSuperArenaRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvTeamQuitSuperArenaReq(TCPFightServer peer, Packet.S2F.TeamQuitSuperArenaReq packet, int sessionid)
	{
		fs.getLogger().debug("receive gs session " + sessionid + " role " + packet.getRoleID() + " team quit super arena");
		fs.getSuperArenaManager().teamQuit(packet.getRoleID(), packet.getMemberCount(), packet.getArenaType(), packet.getGrade(), ok -> {
			notifyGSTeamQuitSuperArenaRes(sessionid, packet.getTagID(), ok);
		});
//		fs.getFightArenaRoomManager().teamQuit(packet.getRoleID(), packet.getArenaType(), packet.getGrade(), ok -> {
//			notifyGSTeamQuitSuperArenaRes(sessionid, packet.getTagID(), ok);
//		});
	}

	public void onTCPFightServerRecvQueryTeamMembersReq(TCPFightServer peer, Packet.S2F.QueryTeamMembersReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " query team roles");
		fs.getFightTeamManager().queryTeamRoles(packet.getRoleID(), overviews -> {
			notifyGSQueryTeamRolesRes(sessionid, packet.getTagID(), overviews);
		});
	}

	public void onTCPFightServerRecvRoleLeaveTeam(TCPFightServer peer, Packet.S2F.RoleLeaveTeam packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " leave team");
		fs.getFightTeamManager().roleLeaveTeam(packet.getRoleID());
	}

	public void onTCPFightServerRecvQueryTeamMemberReq(TCPFightServer peer, Packet.S2F.QueryTeamMemberReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " query team member " + packet.getQueryID() + " profile");
		fs.getFightTeamManager().queryTeamRole(packet.getQueryID(), profile -> {
			if(profile != null)
				notifyGSQueryTeamMemberRes(sessionid, packet.getTagID(), profile);
		});
	}

	public void onTCPFightServerRecvLeaveMap(TCPFightServer peer, Packet.S2F.LeaveMap packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " leave map");
		fs.getRoleManager().roleLeaveMap(packet.getRoleID());
	}

	public void onTCPFightServerRecvSendMsgGlobalTeam(TCPFightServer peer, Packet.S2F.SendMsgGlobalTeam packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleId() + " send global team msg");
		fs.getFightTeamManager().sendMsgGlobalTeam(packet.getRoleId(), packet.getMsgContent());
	}

	public void onTCPFightServerRecvTeamJoinForceWarReq(TCPFightServer peer, Packet.S2F.TeamJoinForceWarReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role team join force war");
		fs.getFightManager().roleJoin(packet.getMembers(), packet.getBwType(), packet.getForcewarType(), ok -> 
		{
			fs.getRPCManager().notifyGSTeamJoinForceWarRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvTeamQuitForceWarReq(TCPFightServer peer, Packet.S2F.TeamQuitForceWarReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role team quit force war");
		fs.getFightManager().roleQuit(packet.getRoleID(), packet.getBwType(), packet.getCount(), packet.getForcewarType(), ok -> 
		{
			fs.getRPCManager().notifyGSTeamQuitForceWarRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvSyncRoleDemonHoleReq(TCPFightServer peer, Packet.S2F.SyncRoleDemonHoleReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " sync role demon hole req");
		fs.getDemonHoleManager().syncRoleDemonHole(packet.getRoleID(), (curFloor, grade) -> {
			notifyGSSyncRoleDemonHoleRes(sessionid, packet.getTagID(), curFloor, grade);
		});
	}

	public void onTCPFightServerRecvRoleJoinDemonHoleReq(TCPFightServer peer, Packet.S2F.RoleJoinDemonHoleReq packet, int sessionid)
	{
		fs.getLogger().info("receive gs session " + sessionid + " role " + packet.getRole().id + " join demon hole req");
		fs.getDemonHoleManager().roleJoin(packet.getRole(), ok -> {
			notifyGSRoleJoinDemonHoleRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvRoleChangeDemonHoleFloorReq(TCPFightServer peer, Packet.S2F.RoleChangeDemonHoleFloorReq packet, int sessionid)
	{
		fs.getLogger().info("receive gs session " + sessionid + " role " + packet.getRole().id + " change demon hole floor req");
		fs.getDemonHoleManager().roleChangeFloor(packet.getRole(), packet.getFloor(), ok -> {
			notifyGSRoleChangeDemonFloorRes(sessionid, packet.getTagID(), ok);
		});
	}

	public void onTCPFightServerRecvRoleDemonHoleBattleReq(TCPFightServer peer, Packet.S2F.RoleDemonHoleBattleReq packet, int sessionid)
	{
		fs.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " query demon hole battle req");
		fs.getDemonHoleManager().queryBattleInfo(packet.getRoleID(), (curFloor, total) -> {
			notifyGSRoleDemonHoleBattleRes(sessionid, packet.getTagID(), curFloor, total);
		});
	}

	public void onTCPFightServerRecvRoleEnterDemonHoleFloor(TCPFightServer peer, Packet.S2F.RoleEnterDemonHoleFloor packet, int sessionid)
	{
		fs.getLogger().info("receive gs session " + sessionid + " role " + packet.getRole().id + " enter demon hole floor " + packet.getFloor());
		fs.getDemonHoleManager().roleEnterDemonHoleFloor(packet.getRole(), packet.getFloor());
	}

	public int getTCPFightMapServerMaxConnectionIdleTime()
	{
		return 30 * 1000;
	}

	public void onTCPFightMapServerOpen(TCPFightMapServer peer)
	{
		fs.getLogger().info("TCPFightMapServer open on " + peer.getListenAddr());
	}

	public void onTCPFightMapServerOpenFailed(TCPFightMapServer peer, ket.kio.ErrorCode errcode)
	{
		fs.getLogger().warn("TCPFightMapServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPFightMapServerClose(TCPFightMapServer peer, ket.kio.ErrorCode errcode)
	{
		fs.getLogger().info("TCPFightMapServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPFightMapServerSessionOpen(TCPFightMapServer peer, int sessionid, NetAddress addrClient)
	{
		fs.getLogger().info("TCPFightMapServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPFightMapServerSessionClose(TCPFightMapServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		if(sessionid != this.globalMapSessionID)
			return;
		
		fs.getLogger().info("TCPFightMapServer on session " + sessionid  + " close, errcode=" + errcode);
		fs.getGlobalMapService().gmapStopWork(sessionid);
		this.globalMapSessionID = null;
	}

	public void onTCPFightMapServerRecvKeepAlive(TCPFightMapServer peer, Packet.GM2F.KeepAlive packet, int sessionid)
	{
		fs.getLogger().debug("receive gmap session " + sessionid + " global map " + packet.getHello() + " keepalive packet");
	}

	public void onTCPFightMapServerRecvWhoAmI(TCPFightMapServer peer, Packet.GM2F.WhoAmI packet, int sessionid)
	{
		fs.getLogger().info("receive gmap session " + sessionid + " areaId " + packet.getAreaId() + " global map ready to work, resister serve maps: " + packet.getMaps());
		if (packet.getAreaId() != fs.getConfig().areaId || (this.globalMapSessionID != null))
		{
			fs.getLogger().warn("gms server register area id = " + packet.getAreaId() + " invalid!");
			tfms.closeSession(sessionid);
			return;
		}
		this.globalMapSessionID = sessionid;
		fs.getGlobalMapService().gmapStartWork(packet.getMaps());
	}

	public void onTCPFightMapServerRecvCreateMapCopyRes(TCPFightMapServer peer, Packet.GM2F.CreateMapCopyRes packet, int sessionid)
	{
		fs.getLogger().info("receive gmap session " + sessionid + " create map copy responce " + packet.getMapInstance());
		fs.getGlobalMapService().handleCreateMapCopyRes(packet.getMapType(), packet.getMapInstance());
	}

	public void onTCPFightMapServerRecvSyncForceWarMapEnd(TCPFightMapServer peer, Packet.GM2F.SyncForceWarMapEnd packet, int sessionid)
	{
		fs.getLogger().info("receive gmap session " + sessionid + " force war map[" + packet.getMapID() + " ,  " + packet.getMapInstance() + "] end winSide " + packet.getWinSide() + " killedBoss " + packet.getKilledBoss() + " whiteSideScore" + packet.getWhiteScore() + " blackSideScore " + packet.getBlackScore());
		fs.getGlobalMapService().handleSyncForceWarMapEnd(packet.getMapID(), packet.getMapInstance(), packet.getWinSide(), packet.getKilledBoss(), packet.getWhiteScore(), packet.getBlackScore(), packet.getWhiteSide(), packet.getBlackSide());
	}
	public void onTCPFightMapServerRecvSyncSuperArenaMapEnd(TCPFightMapServer peer, Packet.GM2F.SyncSuperArenaMapEnd packet, int sessionid)
	{
		fs.getLogger().info("receive gmap session " + sessionid + " super arena map[" + packet.getMapID() + " ,  " + packet.getMapInstance() + "] end");
		fs.getGlobalMapService().handleSyncSuperArenaMapEnd(packet.getMapID(), packet.getMapInstance(), packet.getResult());
	}

	public void onTCPFightMapServerRecvSyncHp(TCPFightMapServer peer, Packet.GM2F.SyncHp packet, int sessionid)
	{
		fs.getLogger().trace("receive gmap session " + sessionid + " sync role " + packet.getRoleID() + " hp " + packet.getHp() + " , " + packet.getHpMax());
		fs.getGlobalMapService().handleSyncHp(packet.getRoleID(), packet.getHp(), packet.getHpMax());
	}
	
	public void onTCPFightMapServerRecvSyncSuperArenaRaceEnd(TCPFightMapServer peer, Packet.GM2F.SyncSuperArenaRaceEnd packet, int sessionid)
	{
		fs.getLogger().trace("receive gmap session " + sessionid + " sync super arena race [" + packet.getMapID() + " , " + packet.getMapInstance() + "] end");
		fs.getGlobalMapService().handleSyncSuperArenaRaceEnd(packet.getMapID(), packet.getMapInstance());
	}
	public void onTCPFightMapServerRecvSyncDemonHoleKill(TCPFightMapServer peer, Packet.GM2F.SyncDemonHoleKill packet, int sessionid)
	{
		fs.getLogger().trace("receive gmap session " + sessionid + " sync demon hole map[" + packet.getMapID() + " , " + packet.getMapInstance() + " kill killer " + packet.getKillerID() + " deader " + packet.getDeaderID());
		fs.getGlobalMapService().handleSyncDemonHoleKill(packet.getMapID(), packet.getMapInstance(), packet.getKillerID(), packet.getDeaderID());
	}

	//// end handlers.
	
	//F2S--------------------------------------------------------------------------------------------------------------------------------------------------------------
	void notifyGSRoleJoinForceWarRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " role join force war res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.RoleJoinForceWarRes(tagID, ok));
	}
	
	void notifyGSTeamJoinForceWarRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " role team join force war res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.TeamJoinForceWarRes(tagID, ok));
	}
	
	void notifyGSRoleQuitForceWarRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " role quit force war res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.RoleQuitForceWarRes(tagID, ok));
	}
	
	void notifyGSTeamQuitForceWarRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " role team quit force war res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.TeamQuitForceWarRes(tagID, ok));
	}
	
	void notifyGSRoleEnterForceWarMap(int roleID, int mapID, int mapInstance, boolean mainSpawn)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().debug("notify gs session " + sessionid + " role " + roleID + " enter force war map[" + mapID + " , " + mapInstance + "] mainSpawn " + mainSpawn);
			tfs.sendPacket(sessionid, new Packet.F2S.RoleEnterForceWar(roleID, mapID, mapInstance, (byte)(mainSpawn ? 1 : 0)));
		}
	}
	
	void notifyGSSyncForceWarMapStart(int serverID, int mapID, int mapInstance)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " start force war map[" + mapID + " , " + mapInstance + "]");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncForceWarMapStart(mapID, mapInstance));
		}
	}
	
	void notifyGSSyncForceWarMapEnd(int serverID, int mapID, int mapInstance, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide, SBean.RankClearTime rankClearTime)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " force war map[" + mapID + " , " + mapInstance + "] end killedBoss " + killedBoss + " whiteSideScore " + whiteScore + " blackSideScore " + blackScore);
			tfs.sendPacket(sessionid, new Packet.F2S.SyncForceWarMapEnd(mapID, mapInstance, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide, rankClearTime));
		}
	}
	
	void notifyGSSyncMapCopyTimeOut(int serverID, int mapID, int mapInstance)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " map copy [" + mapID + " , " + mapInstance + "] time out end");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncMapCopyTimeOut(mapID, mapInstance));
		}
	}
	
	void notifyGSSyncRankStart(int serverID, int rankID, int snapshotCreateTime)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " sync force war rank " + rankID + " start ");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncGSRankStart(rankID, snapshotCreateTime));
		}
	}
	
	void notifyGSSyncRank(int serverID, int rankID, List<SBean.RankRole> batch)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " sync force war rank " + rankID + " batch " + batch.size());
			tfs.sendPacket(sessionid, new Packet.F2S.SyncGSRank(rankID, batch));
		}
	}
	
	
	void notifyGSSyncRankEnd(int serverID, int rankID)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " sync force war rank " + rankID + " end ");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncGSRankEnd(rankID));
		}
	}
	
	void notifyGSSendFightMsg(int receiver, SBean.MessageInfo msgContent)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(receiver));
		if(sessionid != null)
		{
			fs.getLogger().trace("notify gs session " + sessionid + " role forcewar msg...");
			tfs.sendPacket(sessionid, new Packet.F2S.ReceiveMsgFight(receiver, msgContent));
		}
	}
	
	void notifyGSSingleJoinSuperArenaRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().debug("notify gs session " + sessionid + " single join super arena res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.SingleJoinSuperArenaRes(tagID, ok));
	}
	
	void notifyGSSingleQuitSuperArenaRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().debug("notify gs session " + sessionid + " single quit super arena res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.SingleQuitSuperArenaRes(tagID, ok));
	}
	
	void notifyGSTeamJoinSuperArenaRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().debug("notify gs session " + sessionid + " team join super arena res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.TeamJoinSuperArenaRes(tagID, ok));
	}
	
	void notifyGSQueryTeamRolesRes(int sessionid, int tagID, List<SBean.RoleOverview> overviews)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " query team roles res");
		tfs.sendPacket(sessionid, new Packet.F2S.QueryTeamMembersRes(tagID, overviews));
	}
	
	void notifyGSQueryTeamMemberRes(int sessionid, int tagID, SBean.RoleProfile profile)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " query team member res");
		tfs.sendPacket(sessionid, new Packet.F2S.QueryTeamMemberRes(tagID, profile));
	}
	
	void notifyGSTeamLeaderChange(int roleID, SBean.RoleOverview newLeader)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().trace("notify gs session " + sessionid + " sync role " + roleID + " team leader change");
			tfs.sendPacket(sessionid, new Packet.F2S.TeamLeaderChange(roleID, newLeader));
		}
	}
	
	void notifyGSMemberLeaveTeam(int roleID, SBean.RoleOverview member)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().trace("notify gs session " + sessionid + " sync role " + roleID + " team member leave");
			tfs.sendPacket(sessionid, new Packet.F2S.MemberLeaveTeam(roleID, member));
		}
	}
	
	void notifyGSTeamQuitSuperArenaRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().info("notify gs session " + sessionid + " team quit arena res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.TeamQuitSuperArenaRes(tagID, ok));
	}
	
	void notifyGSCreateMapCopy(int serverID, int mapID, int mapInstance)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " create map copy " + mapID + " , " + mapInstance);
			tfs.sendPacket(sessionid, new Packet.F2S.CreateMapCopy(mapID, mapInstance));
		}
	}
	
	void notifyGSRoleEnterSuperArenaMap(int roleID, int mapID, int mapInstance, boolean mainSpawnPos)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
//		if(sessionid == null && roleID < 0)
//			sessionid = table.getSessionIDByServerID(1001);
		
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " role " + roleID + " enter super arena map " + mapID + " , " + mapInstance);
			tfs.sendPacket(sessionid, new Packet.F2S.RoleEnterSuperArena(roleID, mapID, mapInstance, mainSpawnPos ? (byte)1 : (byte)0));
		}
	}
	
	void notifyGSSyncSuperArenaMapStart(int serverID, int mapID, int mapInstance, Map<Integer, Integer> eloDiffs)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " sync super arena map " + mapID + " , " + mapInstance + " start");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncSuperArenaStart(mapID, mapInstance, eloDiffs));
		}
	}
	
	void notifyGSSyncSuperArenaMapEnd(int serverID, int mapID, int mapInstance, SBean.SuperArenaBattleResult result, int rankClearTime)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " sync super arena map " + mapID + " , " + mapInstance + " end");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncSuperArenaMapEnd(mapID, mapInstance, result, rankClearTime));
		}
	}
	
	void nottifyGSEnterSuperArenaRace(int serverID, int mapID, int mapInstance)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " sync enter super arena [" + mapID + " , " + mapInstance + "] race");
			tfs.sendPacket(sessionid, new Packet.F2S.EnterSuperArenaRace(mapID, mapInstance));
		}
	}
	
	void notifyGSSuperArenaMatchResult(int roleID, int arenaType, int grade, int result)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " role " + roleID + " super arena match result " + result);
			tfs.sendPacket(sessionid, new Packet.F2S.SuperArenaMatchResult(roleID, arenaType, grade, result));
		}
	}
	
	void notifyGSForceWarMatchResult(int roleID, int result)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " role " + roleID + " force war match success " + result);
			tfs.sendPacket(sessionid, new Packet.F2S.ForceWarMatchResult(roleID, result));
		}
	}
	
	void notifyGSSyncRoleFightTeam(int roleID, SBean.Team team)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " role " + roleID + " fight team " + team.id);
			tfs.sendPacket(sessionid, new Packet.F2S.SyncRoleFightTeam(roleID, team));
		}
	}
	
	// notify role, member update hp
	void notifyGSTeamMemberUpdateHp(int roleID, int memberID, int memberHp, int memberHpMax)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().trace("notify gs session " + sessionid + " role " + roleID + " meember " + memberID + " update hp " + memberHp + " , " + memberHpMax);
			tfs.sendPacket(sessionid, new Packet.F2S.TeamMemberUpdateHpTrans(roleID, memberID, memberHp, memberHpMax));
		}
	}
	
	void notifyGSTeamDissolve(int roleID)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().trace("notify gs session " + sessionid + " sync role " + roleID + " team dissolve");
			tfs.sendPacket(sessionid, new Packet.F2S.FightTeamDissolve(roleID));
		}
	}
	
	void notifyGSSyncRoleDemonHoleRes(int sessionid, int tagID, int curFloor, int grade)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " sync role demon hole curFloor " + curFloor);
		tfs.sendPacket(sessionid, new Packet.F2S.SyncRoleDemonHoleRes(tagID, curFloor, grade));
	}
	
	void notifyGSRoleJoinDemonHoleRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().info("notify gs session " + sessionid + " role join demon hole res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.RoleJoinDemonHoleRes(tagID, ok));
	}
	
	void notifyGSRoleChangeDemonFloorRes(int sessionid, int tagID, int ok)
	{
		fs.getLogger().info("notify gs session " + sessionid + " role change demon floor res " + ok);
		tfs.sendPacket(sessionid, new Packet.F2S.RoleChangeDemonHoleFloorRes(tagID, ok));
	}
	
	void notifyGSRoleDemonHoleBattleRes(int sessionid, int tagID, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		fs.getLogger().trace("notify gs session " + sessionid + " role demon hole battle res");
		tfs.sendPacket(sessionid, new Packet.F2S.RoleDemonHoleBattleRes(tagID, curFloor, total));
	}
	
	void notifyGSRoleEnterDemonHole(int roleID, int mapID, int mapInstance, int floor, int grade)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " role " + roleID + " enter demon hole [" + mapID + " ," + mapInstance + "]");
			tfs.sendPacket(sessionid, new Packet.F2S.RoleEnterDemonHoleMap(roleID, mapID, mapInstance, floor, grade));
		}
	}
	
	void notifyGSSyncDemonHoleMapEnd(int serverID, int mapID, int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().info("notify gs session " + sessionid + " demon hole [" + mapID + " ," + mapInstance + "] end");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncDemonHoleMapEnd(mapID, mapInstance, curFloor, total));
		}
	}
	
	public void notifyGSSyncCreateNewTeam(int serverID, List<Integer> members)
	{
		Integer sessionid = table.getSessionIDByServerID(serverID);
		if(sessionid != null)
		{
			fs.getLogger().trace("notify gs session " + sessionid + " create new team");
			tfs.sendPacket(sessionid, new Packet.F2S.SyncGSCreateNewTeam(members));
		}
	}
	
	public void notifyGSSyncRoleChatRoom(int roleID, int mapID, int mapInstance, String roomID)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid != null)
		{
			fs.getLogger().trace("notify gs session " + sessionid + " role " + roleID + " chat room " + roomID);
			tfs.sendPacket(sessionid, new Packet.F2S.SyncRoleChatRoom(roleID, mapID, mapInstance, roomID));
		}
	}
	
	//F2GlobalMap--------------------------------------------------------------------------------------------------------------------------------------------------------------
	void notifyGlobalMapCreateMapCopyReq(int mapType, int mapID, int mapInstance)
	{
		if(globalMapSessionID == null)
			return;
		
		fs.getLogger().info("notify global session " + globalMapSessionID + " map create map[" + mapID + " , " + mapInstance + "]");
		tfms.sendPacket(globalMapSessionID, new Packet.F2GM.CreateMapCopyReq(mapType, mapID, mapInstance));
	}
	
	void notifyGlobalMapEndMapCopy(int mapID, int mapInstance)
	{
		if(globalMapSessionID == null)
			return;
		
		fs.getLogger().info("notify global session" + globalMapSessionID + " map destroy map copy[" + mapID + " , " + mapInstance + "]");
		tfms.sendPacket(globalMapSessionID, new Packet.F2GM.EndMapCopy(mapID, mapInstance));
	}
	
	public Integer getServerIDByZoneID(int zoneID)
	{
		return table.getServerIDByZoneID(zoneID);
	}
	
	public Set<Integer> getAllServers()
	{
		return table.getAllServers();
	}
	
	NetManager managerNet;
	TCPFightServer tfs;
	TCPFightMapServer tfms;
	TCPAlarmServer talarms;
	FightServer fs;
	
	GameServerTable table = new GameServerTable();
	private Integer globalMapSessionID;
}
