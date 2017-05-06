// modified by ket.kio.RPCGen at Fri Mar 31 18:59:59 CST 2017.

package i3k.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.gs.GameData;
import i3k.rpc.Packet;
import i3k.SBean;
import i3k.util.ServerTable;

public class RPCManager
{

	RPCManager(AuthServer aus)
	{
		this.aus = aus;
		this.managerNet = new NetManager();
		this.taus = new TCPAuthServer(this);
		this.table = new ServerTable();
	}
	
	public NetManager getNetManager()
	{
		return managerNet;
	}
	
	void start()
	{
		managerNet.start();
		taus.setListenAddr(aus.getConfig().addrAuthListen, ket.kio.BindPolicy.eReuseTimewait);
		taus.setListenBacklog(128);
		taus.open();
	}
	
	void destroy()
	{
		managerNet.destroy();
	}
	
	void onTimer(int timeTick)
	{
		
	}
	
	//// begin handlers.
	public int getTCPAuthServerMaxConnectionIdleTime()
	{
		return 30 * 1000;
	}

	public void onTCPAuthServerOpen(TCPAuthServer peer)
	{
		aus.getLogger().info("TCPAucthServer open on " + peer.getListenAddr());
	}

	public void onTCPAuthServerOpenFailed(TCPAuthServer peer, ket.kio.ErrorCode errcode)
	{
		aus.getLogger().warn("TCPAucthServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPAuthServerClose(TCPAuthServer peer, ket.kio.ErrorCode errcode)
	{
		aus.getLogger().info("TCPAucthServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPAuthServerSessionOpen(TCPAuthServer peer, int sessionid, NetAddress addrClient)
	{
		aus.getLogger().info("TCPAucthServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPAuthServerSessionClose(TCPAuthServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		aus.getLogger().info("TCPAucthServer on session " + sessionid  + " close, errcode=" + errcode);
		table.onSessionClose(sessionid);
	}

	public void onTCPAuthServerRecvKeepAlive(TCPAuthServer peer, Packet.S2AU.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuthServerRecvWhoAmI(TCPAuthServer peer, Packet.S2AU.WhoAmI packet, int sessionid)
	{
		aus.getLogger().info("receive gs server session " + sessionid + " server " + packet.getServerId() + " zones " + packet.getZones() +" whoami packet");
		Integer oldSession = table.tryCloseSessionByServerID(packet.getServerId());
		if(oldSession != null)
		{
			aus.getLogger().warn("close gs server session " + oldSession + " on server [" + packet.getServerId() + " " + sessionid + "] announce");
			taus.closeSession(oldSession);
		}
			
		
		if (!table.onSessionAnnounce(sessionid, packet.getServerId()))
		{
			aus.getLogger().warn("close gs server session " + sessionid + " for server " + packet.getServerId() + " or zones " + packet.getZones() + " clash");
			taus.closeSession(sessionid);
			return;
		}
	}

	public void onTCPAuthServerRecvPayRes(TCPAuthServer peer, Packet.S2AU.PayRes packet, int sessionid)
	{
		aus.getLogger().info("receive gs server session " + sessionid + " pay result res : xid=" + packet.getXid() + ", ret=" + packet.getOk());
		aus.getAuthManager().onSetPayResultRes(packet.getXid(), packet.getOk());
	}

	//// end handlers.
	
	
	public boolean notifyGameServerPayResult(int xid, int gsid, String channel, String uid, int roleid, String goodsid, int paylevel, String orderid, String payext)
	{
		Integer sid = table.getSessionIDByServerID(gsid);
		if (sid == null)
		{
			aus.getLogger().warn("discard pay result for can not find raw gs " + gsid + " server (xid=" + xid + ", roleid=" + roleid + ", goodsid=" + goodsid + ", paylevel=" + paylevel + ", payext=" + payext + ", orderid=" + orderid + ")");
			return false;
		}
		aus.getLogger().info("send raw gs server " + gsid + " session " + sid + " pay result : xid=" + xid + ", roleid=" + roleid + ", goodsid=" + goodsid + ", paylevel=" + paylevel + ", payext=" + payext + ", orderid=" + orderid);
		taus.sendPacket(sid, new Packet.AU2S.PayReq(xid, orderid, channel, uid, gsid, roleid, goodsid, paylevel, payext));
		return true;
	}
	

	AuthServer aus;
	NetManager managerNet;
	TCPAuthServer taus;
	
	ServerTable table;
}
