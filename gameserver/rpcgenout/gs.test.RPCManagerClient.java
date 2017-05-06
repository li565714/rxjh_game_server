// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs.test;

import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManagerClient
{

	//// begin handlers.
	public void onTCPGameClientOpen(TCPGameClient peer)
	{
		// TODO
	}

	public void onTCPGameClientOpenFailed(TCPGameClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGameClientClose(TCPGameClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGameClientRecvServerChallenge(TCPGameClient peer, Packet.S2C.ServerChallenge packet)
	{
		// TODO
	}

	public void onTCPGameClientRecvServerResponse(TCPGameClient peer, Packet.S2C.ServerResponse packet)
	{
		// TODO
	}

	public void onTCPGameClientRecvLuaChannel(TCPGameClient peer, Packet.S2C.LuaChannel packet)
	{
		// TODO
	}

	public void onTCPGameClientRecvLuaChannel2(TCPGameClient peer, Packet.S2C.LuaChannel2 packet)
	{
		// TODO
	}

	public void onTCPGameClientRecvStrChannel(TCPGameClient peer, Packet.S2C.StrChannel packet)
	{
		// TODO
	}

	//// end handlers.
}
