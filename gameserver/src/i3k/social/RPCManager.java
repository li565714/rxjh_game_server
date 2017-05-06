// modified by ket.kio.RPCGen at Fri Mar 31 18:59:59 CST 2017.

package i3k.social;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ket.kio.NetAddress;
import ket.kio.NetManager;
import ket.util.Stream;
import i3k.alarm.TCPAlarmServer;
import i3k.gs.GameData;
import i3k.rpc.Packet;
import i3k.util.GameTime;
import i3k.util.ServerTable;
import i3k.SBean;
import i3k.ForwardData;

public class RPCManager
{
	public RPCManager(SocialServer ss)
	{
		this.ss = ss;
		this.table = new ServerTable();
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
		if( ss.getConfig().nIOThread == 1 )
			managerNet = new NetManager(NetManager.NetManagerType.eSelectNetManager, ss.getConfig().nIOThread);
		else
			managerNet = new NetManager(NetManager.NetManagerType.eMTSelectNetManager, ss.getConfig().nIOThread);
		managerNet.start();
		
		tss = new TCPSocialServer(this);
		tss.setListenAddr(ss.getConfig().addrSocialListen, ket.kio.BindPolicy.eReuseTimewait);
		tss.setListenBacklog(128);
		tss.open();
		
		talarms = new TCPAlarmServer(this.getNetManager(), ss.getConfig().addrAlarmListen, ss.getLogger());
		talarms.start();
		
	}
	public void destroy()
	{
		if (managerNet != null)
			managerNet.destroy();
	}
	
	public NetManager getNetManager() { return managerNet; }
	
	//// begin handlers.
	public int getTCPSocialServerMaxConnectionIdleTime()
	{
		return 30 * 1000;
	}

	public void onTCPSocialServerOpen(TCPSocialServer peer)
	{
		ss.getLogger().info("TCPSocialServer open on " + peer.getListenAddr());
	}

	public void onTCPSocialServerOpenFailed(TCPSocialServer peer, ket.kio.ErrorCode errcode)
	{
		ss.getLogger().warn("TCPSocialServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPSocialServerClose(TCPSocialServer peer, ket.kio.ErrorCode errcode)
	{
		ss.getLogger().info("TCPSocialServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPSocialServerSessionOpen(TCPSocialServer peer, int sessionid, NetAddress addrClient)
	{
		ss.getLogger().info("TCPSocialServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPSocialServerSessionClose(TCPSocialServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		ss.getLogger().info("TCPSocialServer on session " + sessionid  + " close, errcode=" + errcode);
		table.onSessionClose(sessionid);
	}

	public void onTCPSocialServerRecvKeepAlive(TCPSocialServer peer, Packet.E2SS.KeepAlive packet, int sessionid)
	{
		ss.getLogger().debug("receive es session " + sessionid + " exchange server " + packet.getHello() + " keepalive packet");
	}

	public void onTCPSocialServerRecvWhoAmI(TCPSocialServer peer, Packet.E2SS.WhoAmI packet, int sessionid)
	{
		ss.getLogger().info("receive es session " + sessionid + " exchange server " + packet.getServerId() + " whoami packet");
		Integer oldSession = table.tryCloseSessionByServerID(packet.getServerId());
		if(oldSession != null)
		{
			ss.getLogger().warn("close es server session " + oldSession + " on server [" + packet.getServerId() + " " + sessionid + "] announce");
			tss.closeSession(oldSession);
		}
		
		if (!table.onSessionAnnounce(sessionid, packet.getServerId()))
		{
			ss.getLogger().warn("close es server session " + sessionid + " for server " + packet.getServerId() + " clash");
			tss.closeSession(sessionid);
			return;
		}
	}

	public void onTCPSocialServerRecvForwardReq(TCPSocialServer peer, Packet.E2SS.ForwardReq packet, int sessionid)
	{
		ss.getLogger().debug("receive es session " + sessionid + " exchange server forward packet type " + packet.getData().dataType);
		onHandleSocialReq(sessionid, packet.getGsid(), packet.getData());
	}

	//// end handlers.
	
	void onHandleSocialReq(int sessionid, int gsid, SBean.ForwardData data)
	{
		ForwardData.ForwardTask task = ForwardData.decodePacket(data);
		if (task != null)
		{
			switch (data.dataType)
			{
			case SBean.ForwardData.eReqSyncRole:
				break;
			case SBean.ForwardData.eReqSyncPage:
				handleSyncPageComment(sessionid, gsid, task);
				break;
			case SBean.ForwardData.eReqSendComment:
				handleSendCommentReq(sessionid, gsid, task);
				break;
			case SBean.ForwardData.eReqLikeComment:
				handleLikeComment(sessionid, gsid, task);
				break;
			case SBean.ForwardData.eReqDislikeComment:
				handleDislikeComment(sessionid, gsid, task);
				break;
			default:
				break;
			}	
		}
	}
	
	void sendExchangeSocialRes(int sessionid, int gsid, int taskId, int dataType, Stream.IStreamable bodyData)
	{
		ss.getLogger().debug("send exchange server " + sessionid + " social data type " + dataType);
		tss.sendPacket(sessionid, new Packet.SS2E.ForwardRes(gsid, ForwardData.encodePacket(dataType, taskId, bodyData)));
	}
	
	void handleSendCommentReq(int sessionid, int gsid, ForwardData.ForwardTask task)
	{
		SBean.SendCommentReq req = (SBean.SendCommentReq)task.obj;
		if(req == null)
			return;
		
		ss.getSocialManager().roleSendComment(req.serverId, req.serverName, req.roleId, req.roleName, req.themeType, req.themeId, req.comment, ok -> 
		{
			sendExchangeSocialRes(sessionid, gsid, task.taskId, SBean.ForwardData.eResSendComment, new SBean.SendCommentRes(ok));
		});
	}
	
	void handleSyncPageComment(int sessionid, int gsid, ForwardData.ForwardTask task)
	{
		SBean.SyncPageCommentReq req = (SBean.SyncPageCommentReq)task.obj;
		if(req == null)
			return;
		
		ss.getSocialManager().syncPageComment(req.themeType, req.themeId, req.tag, req.pageNo, req.len, comments -> 
		{
			sendExchangeSocialRes(sessionid, gsid, task.taskId, SBean.ForwardData.eResSyncPage, new SBean.SyncPageCommentRes(comments));
		});
	}
	
	void handleLikeComment(int sessionid, int gsid, ForwardData.ForwardTask task)
	{
		SBean.LikeCommentReq req = (SBean.LikeCommentReq)task.obj;
		if(req == null)
			return;
		
		ss.getSocialManager().likeComment(req.serverId, req.serverName, req.roleId, req.roleName, req.themeType, req.themeId, req.commentId, ok ->
		{
			sendExchangeSocialRes(sessionid, gsid, task.taskId, SBean.ForwardData.eResLikeComment, new SBean.LikeCommentRes(ok));
		});
	}
	
	void handleDislikeComment(int sessionid, int gsid, ForwardData.ForwardTask task)
	{
		SBean.DislikeCommentReq req = (SBean.DislikeCommentReq)task.obj;
		if(req == null)
			return;
		
		ss.getSocialManager().dislikeComment(req.serverId, req.serverName, req.roleId, req.roleName, req.themeType, req.themeId, req.commentId, ok ->
		{
			sendExchangeSocialRes(sessionid, gsid, task.taskId, SBean.ForwardData.eResDislikeComment, new SBean.DislikeCommentRes(ok));
		});
	}
	//E2SS--------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	NetManager managerNet;
	TCPSocialServer tss;
	TCPAlarmServer talarms;
	SocialServer ss;
	
	ServerTable table;
}
