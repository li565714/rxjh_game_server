// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.exchange;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManager
{

	//// begin handlers.
	public int getTCPExchangeServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPExchangeServerOpen(TCPExchangeServer peer)
	{
		// TODO
	}

	public void onTCPExchangeServerOpenFailed(TCPExchangeServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPExchangeServerClose(TCPExchangeServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPExchangeServerSessionOpen(TCPExchangeServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPExchangeServerSessionClose(TCPExchangeServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPExchangeServerRecvKeepAlive(TCPExchangeServer peer, Packet.S2E.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPExchangeServerRecvWhoAmI(TCPExchangeServer peer, Packet.S2E.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPExchangeServerRecvSendMsg(TCPExchangeServer peer, Packet.S2E.SendMsg packet, int sessionid)
	{
		// TODO
	}

	public void onTCPExchangeServerRecvSocialMsgReq(TCPExchangeServer peer, Packet.S2E.SocialMsgReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPSocialClientOpen(TCPSocialClient peer)
	{
		// TODO
	}

	public void onTCPSocialClientOpenFailed(TCPSocialClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPSocialClientClose(TCPSocialClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPSocialClientRecvKeepAlive(TCPSocialClient peer, Packet.SS2E.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPSocialClientRecvForwardRes(TCPSocialClient peer, Packet.SS2E.ForwardRes packet)
	{
		// TODO
	}

	//// end handlers.
}
