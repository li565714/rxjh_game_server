// modified by ket.kio.RPCGen at Sat Jul 26 17:02:05 CST 2014.

package i3k.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ket.kio.NetAddress;
import ket.kio.NetManager;
import ket.util.Stream;
import i3k.IDIP;

public class RPCManager
{	
	public static class IDIPReqHeader implements Stream.IStreamable
	{

		public IDIPReqHeader() { }

		public IDIPReqHeader(int AreaId, byte PlatId, int Partition)
		{
//			this.AreaId = AreaId;
//			this.PlatId = PlatId;
			this.Partition = Partition;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
//			AreaId = is.popInteger();
//			PlatId = is.popByte();
			Partition = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
//			os.pushInteger(AreaId);
//			os.pushByte(PlatId);
			os.pushInteger(Partition);
		}

//		// 大区（1微信，2手Q）
//		public int AreaId;
//		// 平台（0ios，1安卓）
//		public byte PlatId;
		// 小区ID（-1 全部，*** 大于0的具体小区）
		public int Partition;
	}
	
	public static IDIPReqHeader decodeIDIPHeader(byte[] bodyData, int len)
	{
		Stream.BytesInputStream bais = new Stream.BytesInputStream(bodyData, 0, len);
		Stream.AIStream is = new Stream.IStreamBE(bais);
		try
		{
			IDIPReqHeader obj = new IDIPReqHeader();
			obj.decode(is);
			return obj;
		}
		catch(Exception ex)
		{
		}
		return null;
	}
	
	public RPCManager(IDIPProxyServer ps)
	{
		this.ps = ps;
	}
	
	public void start()
	{
		managerNet.start();
		
		idips.setListenAddr(ps.getConfig().addrIDIP, ket.kio.BindPolicy.eReuseTimewait);
		idips.setListenBacklog(16);
		idips.open();

	}
	
	public void destroy()
	{
		managerNet.destroy();
	}
	
	public void onTimer()
	{
		managerNet.checkIdleConnections();
	}

	public NetManager getNetManager() { return managerNet; }
	
	public int getTCPIDIPServerMaxConnectionIdleTime()
	{
		// TODO
		return 5 * 1000;
	}
	
	public void onTCPIDIPServerOpen(TCPIDIPServer peer)
	{
		ps.getLogger().info("tcpidipserver open on " + peer.getListenAddr());
	}

	public void onTCPIDIPServerOpenFailed(TCPIDIPServer peer, ket.kio.ErrorCode errcode)
	{
		ps.getLogger().warn("tcpidipserver open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPIDIPServerClose(TCPIDIPServer peer, ket.kio.ErrorCode errcode)
	{
		ps.getLogger().info("tcpidipserver close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}
	
	public void onTCPIDIPServerSessionOpen(TCPIDIPServer peer, final int sessionid, final NetAddress addrClient)
	{
		ps.getLogger().info("session " + sessionid + " on open from (" + addrClient.host + ":" + addrClient.port + ")");
	}
	
	public void onTCPIDIPServerSessionClose(TCPIDIPServer peer, final int sessionid)
	{	
		ps.getLogger().info("session " + sessionid + " on close");
		TCPIDIPClient client = proxyMap.remove(sessionid);
		if( client != null )
			client.close();
	}
	
	public void onTCPIDIPServerPacketRecv(TCPIDIPServer peer, final int sessionid, final IDIPPacket packet)
	{
		ps.getLogger().info("session " + sessionid + " on receive packet");
		IDIPReqHeader header = decodeIDIPHeader(packet.body, packet.len);
		if( header == null )
		{
			// TODO
			return;
		}
		//NetAddress addrRedirect = ps.getConfig().addrIDIPRedirect;
		// TODO search config xml and find redirect address by header.partition(gsid)
		NetAddress addrRedirect = ps.getForwardTable().getFowardAddress(header.Partition);
		if (addrRedirect != null)
		{
			ps.getLogger().info("session " + sessionid + " packet to gs[" + header.Partition + "] redirect to server(" + addrRedirect.host + ":" + addrRedirect.port  + ")");
			TCPIDIPClient client = new TCPIDIPClient(this, sessionid, packet);
			client.setServerAddr(addrRedirect);
			proxyMap.put(sessionid, client);
			client.open();
		}
		else
			ps.getLogger().warn("session " + sessionid + " packet to gs[" + header.Partition + "] redirect to server failed!");
	}
	
	public void onTCPIDIPClientOpen(TCPIDIPClient peer)
	{
		ps.getLogger().info("session " + peer.taskSID + " proxy client(" + peer.getClientAddr().host + ":" + peer.getClientAddr().port + ") to gs server(" + peer.getServerAddr().host + ":" + peer.getServerAddr().port +") on open success, forward req packet to gs server");
		peer.sendPacket(peer.taskPacket);
		//ps.getLogger().info("redirect to server packet of sid " + peer.taskSID);
	}
	
	public void onTCPIDIPClientOpenFailed(TCPIDIPClient peer, ket.kio.ErrorCode errcode)
	{
		ps.getLogger().info("session " + peer.taskSID + " proxy client to gs server(" + peer.getServerAddr().host + ":" + peer.getServerAddr().port +") on open failed");
		TCPIDIPClient client = proxyMap.remove(peer.taskSID);
		if( client != null )
			idips.closeSession(peer.taskSID);
	}
	
	public void onTCPIDIPClientClose(TCPIDIPClient peer, ket.kio.ErrorCode errcode)
	{
		ps.getLogger().info("session " + peer.taskSID + " proxy client(" + peer.getClientAddr().host + ":" + peer.getClientAddr().port + ") to gs server(" + peer.getServerAddr().host + ":" + peer.getServerAddr().port +") on close");
		TCPIDIPClient client = proxyMap.remove(peer.taskSID);
		if( client != null )
			idips.closeSession(peer.taskSID);
	}
	
	public void onTCPIDIPClientPacketRecv(TCPIDIPClient peer, IDIPPacket packet)
	{
		ps.getLogger().info("session " + peer.taskSID + " proxy client(" + peer.getClientAddr().host + ":" + peer.getClientAddr().port + ") receive rsp packet from gs server(" + peer.getServerAddr().host + ":" + peer.getServerAddr().port +"), forward rsp packet to session client");
		idips.sendPacket(peer.taskSID, packet);
	}

	NetManager managerNet = new NetManager();
	TCPIDIPServer idips = new TCPIDIPServer(this);
	IDIPProxyServer ps;
	Map<Integer, TCPIDIPClient> proxyMap = new ConcurrentHashMap<Integer, TCPIDIPClient>();
}
