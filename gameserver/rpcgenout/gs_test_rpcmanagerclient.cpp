// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

#include "rpcmanagerclient.h"

namespace I3K
{

	//// begin handlers.
	void RPCManagerClient::OnTCPGameClientOpen(TCPGameClient* /*pPeer*/)
	{
		// TODO
	}

	void RPCManagerClient::OnTCPGameClientOpenFailed(TCPGameClient* /*pPeer*/, KET::KIO::ErrorCode /*errcode*/)
	{
		// TODO
	}

	void RPCManagerClient::OnTCPGameClientClose(TCPGameClient* /*pPeer*/, KET::KIO::ErrorCode /*errcode*/)
	{
		// TODO
	}

	void RPCManagerClient::OnTCPGameClientRecvServerChallenge(TCPGameClient* /*pPeer*/, const I3K::Packet::S2C::ServerChallenge * /*pPacket*/)
	{
		// TODO
	}

	void RPCManagerClient::OnTCPGameClientRecvServerResponse(TCPGameClient* /*pPeer*/, const I3K::Packet::S2C::ServerResponse * /*pPacket*/)
	{
		// TODO
	}

	void RPCManagerClient::OnTCPGameClientRecvLuaChannel(TCPGameClient* /*pPeer*/, const I3K::Packet::S2C::LuaChannel * /*pPacket*/)
	{
		// TODO
	}

	void RPCManagerClient::OnTCPGameClientRecvLuaChannel2(TCPGameClient* /*pPeer*/, const I3K::Packet::S2C::LuaChannel2 * /*pPacket*/)
	{
		// TODO
	}

	void RPCManagerClient::OnTCPGameClientRecvStrChannel(TCPGameClient* /*pPeer*/, const I3K::Packet::S2C::StrChannel * /*pPacket*/)
	{
		// TODO
	}

	//// end handlers.
}

