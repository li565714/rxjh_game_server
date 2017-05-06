// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.auth;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManager
{

	//// begin handlers.
	public int getTCPAuthServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPAuthServerOpen(TCPAuthServer peer)
	{
		// TODO
	}

	public void onTCPAuthServerOpenFailed(TCPAuthServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuthServerClose(TCPAuthServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuthServerSessionOpen(TCPAuthServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPAuthServerSessionClose(TCPAuthServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuthServerRecvKeepAlive(TCPAuthServer peer, Packet.S2AU.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuthServerRecvWhoAmI(TCPAuthServer peer, Packet.S2AU.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuthServerRecvPayRes(TCPAuthServer peer, Packet.S2AU.PayRes packet, int sessionid)
	{
		// TODO
	}

	//// end handlers.
}
