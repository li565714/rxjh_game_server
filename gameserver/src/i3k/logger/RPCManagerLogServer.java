// modified by ket.kio.RPCGen at Fri Dec 05 13:08:36 CST 2014.

package i3k.logger;

import ket.kio.NetAddress;
import ket.kio.NetManager;

public class RPCManagerLogServer
{
			
	public RPCManagerLogServer(LogServer ls)
	{
		this.ls = ls;
	}

	public NetManager getNetManager()
	{
		return managerNet;
	}
	
	public void onTimer()
	{
		// TODO managerNet.checkIdleConnections();
	}
	
	public void start()
	{
		ls.getLogger().info("udp log sever start open "+ ls.getConfig().addrListen.toString() +"...");
		managerNet.start();
		uls.setBindAddr(ls.getConfig().addrListen, ket.kio.BindPolicy.eReuseTimewait);
		uls.open();
	}
	
	public void destroy()
	{
		managerNet.destroy();
	}
	
	public void onDataRecv(NetAddress addrRemote, String logStr)
	{
		ls.getLogger().debug("recv log from [" + addrRemote.toString() + "]: " + logStr);
		try
		{
			ls.getLogDataBase().receiveLog(logStr);
		}
		catch (Exception e)
		{
			ls.getLogger().warn("caught handle log exception: ", e);
		}
	}

	//// begin handlers.
	public void onUDPLogServerOpen(UDPLogServer peer)
	{
		ls.getLogger().info("UDP Log Server "+peer.getBindAddr()+" Open success!");
	}

	public void onUDPLogServerOpenFailed(UDPLogServer peer, ket.kio.ErrorCode errcode)
	{
		ls.getLogger().warn("UDP Log Server "+peer.getBindAddr()+" Open failed " + errcode.toString());
	}

	public void onUDPLogServerClose(UDPLogServer peer, ket.kio.ErrorCode errcode)
	{
		ls.getLogger().info("UDP Log Server "+peer.getBindAddr()+" close!");
	}

	//// end handlers.
	
	NetManager managerNet = new NetManager();
	UDPLogServer uls = new UDPLogServer(this);
	LogServer ls;
}
