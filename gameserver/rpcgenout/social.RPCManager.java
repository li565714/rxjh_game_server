// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.social;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManager
{

	//// begin handlers.
	public int getTCPSocialServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPSocialServerOpen(TCPSocialServer peer)
	{
		// TODO
	}

	public void onTCPSocialServerOpenFailed(TCPSocialServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPSocialServerClose(TCPSocialServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPSocialServerSessionOpen(TCPSocialServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPSocialServerSessionClose(TCPSocialServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPSocialServerRecvKeepAlive(TCPSocialServer peer, Packet.E2SS.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPSocialServerRecvWhoAmI(TCPSocialServer peer, Packet.E2SS.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPSocialServerRecvForwardReq(TCPSocialServer peer, Packet.E2SS.ForwardReq packet, int sessionid)
	{
		// TODO
	}

	//// end handlers.
}
