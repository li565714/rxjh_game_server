// modified by ket.kio.RPCGen at Fri Mar 31 18:59:59 CST 2017.

package i3k.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ket.kio.NetAddress;
import ket.kio.NetManager;
import ket.util.Stream;
import i3k.alarm.TCPAlarmServer;
import i3k.gmap.TCPMapClient;
import i3k.gs.GameData;
import i3k.rpc.Packet;
import i3k.util.GameTime;
import i3k.util.OpenConnectFailCount;
import i3k.util.ServerTable;
import i3k.ForwardData;
import i3k.SBean;

public class RPCManager
{
	public RPCManager(ExchangeServer es)
	{
		this.es = es;
		this.table = new ServerTable();
	}
	
	public void onTimer(int timeTick)
	{
		if (managerNet != null)
			managerNet.checkIdleConnections();
		if (tsc !=null && !tsc.isOpen())
			tsc.open();
		if (talarms != null)
			talarms.onTimer();
		
		keepAlive(timeTick);
	}
	
	void keepAlive(int timeTick)
	{
		if (timeTick % 15 ==  0)
		{
			tsc.sendPacket(new Packet.E2SS.KeepAlive(es.getConfig().areaId));
		}
	}
	
	public void setAllCounter(boolean reportPerTimes)
	{
		openFailCount.setReportPerTimes(reportPerTimes);
	}
	
	public void start()
	{
		setAllCounter(es.getConfig().pIOFailedPerTimes == 1);
		if( es.getConfig().nIOThread == 1 )
			managerNet = new NetManager(NetManager.NetManagerType.eSelectNetManager, es.getConfig().nIOThread);
		else
			managerNet = new NetManager(NetManager.NetManagerType.eMTSelectNetManager, es.getConfig().nIOThread);
		managerNet.start();
		
		tes = new TCPExchangeServer(this);
		tes.setListenAddr(es.getConfig().addrExchangeListen, ket.kio.BindPolicy.eReuseTimewait);
		tes.setListenBacklog(128);
		tes.open();
		
		talarms = new TCPAlarmServer(this.getNetManager(), es.getConfig().addrAlarmListen, es.getLogger());
		talarms.start();
		
		tsc = new TCPSocialClient(this);
		
		tsc.setServerAddr(es.getConfig().addrSocial);
		tsc.open();	
		
	}
	public void destroy()
	{
		if (managerNet != null)
			managerNet.destroy();
	}
	
	public NetManager getNetManager() { return managerNet; }
	
	//// begin handlers.
	public int getTCPExchangeServerMaxConnectionIdleTime()
	{
		return 30 * 1000;
	}

	public void onTCPExchangeServerOpen(TCPExchangeServer peer)
	{
		es.getLogger().info("TCPExchangeServer open on " + peer.getListenAddr());
	}

	public void onTCPExchangeServerOpenFailed(TCPExchangeServer peer, ket.kio.ErrorCode errcode)
	{
		es.getLogger().warn("TCPExchangeServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPExchangeServerClose(TCPExchangeServer peer, ket.kio.ErrorCode errcode)
	{
		es.getLogger().info("TCPExchangeServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPExchangeServerSessionOpen(TCPExchangeServer peer, int sessionid, NetAddress addrClient)
	{
		es.getLogger().info("TCPExchangeServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPExchangeServerSessionClose(TCPExchangeServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		es.getLogger().info("TCPExchangeServer on session " + sessionid  + " close, errcode=" + errcode);
		table.onSessionClose(sessionid);
	}

	public void onTCPExchangeServerRecvKeepAlive(TCPExchangeServer peer, Packet.S2E.KeepAlive packet, int sessionid)
	{
		es.getLogger().debug("receive gs session " + sessionid + " gameserver " + packet.getHello() + " keepalive packet");
	}

	public void onTCPExchangeServerRecvWhoAmI(TCPExchangeServer peer, Packet.S2E.WhoAmI packet, int sessionid)
	{
		es.getLogger().info("receive gs session " + sessionid + " gameserver " + packet.getServerId() + " whoami packet");
		Integer oldSession = table.tryCloseSessionByServerID(packet.getServerId());
		if(oldSession != null)
		{
			es.getLogger().warn("close gs server session " + oldSession + " on server [" + packet.getServerId() + " " + sessionid + "] announce");
			tes.closeSession(oldSession);
		}
		
		if (!table.onSessionAnnounce(sessionid, packet.getServerId()))
		{
			es.getLogger().warn("close gs server session " + sessionid + " for server " + packet.getServerId() + " clash");
			tes.closeSession(sessionid);
			return;
		}
	}

	public void onTCPExchangeServerRecvSendMsg(TCPExchangeServer peer, Packet.S2E.SendMsg packet, int sessionid)
	{
		es.getLogger().debug("receive gs session " + sessionid + " gameserver " + packet.getMsg().gsName + " send a msg: " + packet.getMsg().content.msg);
		tes.broadcastPacket(table.getAllServerSessions(), new Packet.E2S.ReceiveMsg(packet.getMsg()));
	}

	public void onTCPExchangeServerRecvSocialMsgReq(TCPExchangeServer peer, Packet.S2E.SocialMsgReq packet, int sessionid)
	{
		es.getLogger().debug("receive gs session " + sessionid + " gameserver social packet type " + packet.getData().dataType);
		Integer gsid = table.getServerIDBySessionID(sessionid);
		if (gsid != null)
		{
			tsc.sendPacket(new Packet.E2SS.ForwardReq(gsid, packet.getData()));
		}
	}

	public void onTCPSocialClientOpen(TCPSocialClient peer)
	{
		es.getLogger().info("tcpsocialclient open connect to " + peer.getServerAddr());
		peer.sendPacket(new Packet.E2SS.WhoAmI(es.getConfig().areaId));
		openFailCount.resetCount();
	}

	public void onTCPSocialClientOpenFailed(TCPSocialClient peer, ket.kio.ErrorCode errcode)
	{
		if (openFailCount.increaseCount())
			es.getLogger().warn("tcpsocialclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPSocialClientClose(TCPSocialClient peer, ket.kio.ErrorCode errcode)
	{
		es.getLogger().warn("tcpsocialclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
	}

	public void onTCPSocialClientRecvKeepAlive(TCPSocialClient peer, Packet.SS2E.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPSocialClientRecvForwardRes(TCPSocialClient peer, Packet.SS2E.ForwardRes packet)
	{
		es.getLogger().debug("receive ss forward res packet type "+ packet.getData().dataType + " to gs " + packet.getGsid());
		Integer sessionid = table.getSessionIDByServerID(packet.getGsid());
		if (sessionid != null)
		{
			tes.sendPacket(sessionid, new Packet.E2S.SocialMsgRes(packet.getData()));
		}
	}

	//// end handlers.
	
	
	//E2S--------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	NetManager managerNet;
	TCPExchangeServer tes;
	TCPAlarmServer talarms;
	TCPSocialClient tsc;
	ExchangeServer es;
	
	ServerTable table;
	OpenConnectFailCount openFailCount = new OpenConnectFailCount();
}
