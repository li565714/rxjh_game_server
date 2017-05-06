// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

#ifndef __I3K__RPCMANAGERCLIENT_H
#define __I3K__RPCMANAGERCLIENT_H

#include <ket/kio/netmanager.h>

#include "packet.h"
#include "tcpgameclient.h"

namespace I3K
{

	class RPCManagerClient
	{
	public:

		//// begin handlers.
		void OnTCPGameClientOpen(TCPGameClient* pPeer);
		void OnTCPGameClientOpenFailed(TCPGameClient* pPeer, KET::KIO::ErrorCode errcode);
		void OnTCPGameClientClose(TCPGameClient* pPeer, KET::KIO::ErrorCode errcode);
		void OnTCPGameClientRecvServerChallenge(TCPGameClient *pPeer, const I3K::Packet::S2C::ServerChallenge *pPacket);
		void OnTCPGameClientRecvServerResponse(TCPGameClient *pPeer, const I3K::Packet::S2C::ServerResponse *pPacket);
		void OnTCPGameClientRecvLuaChannel(TCPGameClient *pPeer, const I3K::Packet::S2C::LuaChannel *pPacket);
		void OnTCPGameClientRecvLuaChannel2(TCPGameClient *pPeer, const I3K::Packet::S2C::LuaChannel2 *pPacket);
		void OnTCPGameClientRecvStrChannel(TCPGameClient *pPeer, const I3K::Packet::S2C::StrChannel *pPacket);
		//// end handlers.
	};
}

#endif
