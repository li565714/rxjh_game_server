// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

#include "tcpgameclient.h"
#include "rpcmanagerclient.h"
#include "packet.h"

namespace I3K
{

	TCPGameClient::TCPGameClient(KET::KIO::NetManager &managerNet)
		: KET::KIO::TCPClient<KET::KIO::SimplePacket>(managerNet)
	{
	}

	bool TCPGameClient::DoCheckPacketType(int ptype)
	{
		switch( ptype )
		{
		// server to client
		case Packet::eS2CPKTServerChallenge:
		case Packet::eS2CPKTServerResponse:
		case Packet::eS2CPKTLuaChannel:
		case Packet::eS2CPKTLuaChannel2:
		case Packet::eS2CPKTStrChannel:
			return true;
		default:
			break;
		}
		return false;
	}

	void TCPGameClient::OnOpen()
	{
		m_pRPCManagerClient->AddTCPGameClientOpenMessage();
	}

	void TCPGameClient::OnOpenFailed(KET::KIO::ErrorCode errcode)
	{
		m_pRPCManagerClient->AddTCPGameClientOpenFailedMessage(errcode);
	}

	void TCPGameClient::OnClose(KET::KIO::ErrorCode errcode)
	{
		m_pRPCManagerClient->AddTCPGameClientCloseMessage(errcode);
	}

	void TCPGameClient::OnPacketRecv(const KET::KIO::SimplePacket *pPacket)
	{
		m_pRPCManagerClient->AddTCPGameClientPacket(pPacket);
	}
	void TCPGameClient::OnPacketRecvParse(const KET::KIO::SimplePacket *pPacket)
	{
		switch( pPacket->GetType() )
		{
		case Packet::eS2CPKTServerChallenge:
			{
				const Packet::S2C::ServerChallenge *p = dynamic_cast<const Packet::S2C::ServerChallenge*>(pPacket);
				m_pRPCManagerClient->OnTCPGameClientRecvServerChallenge(this, p);
			}
			break;
		case Packet::eS2CPKTServerResponse:
			{
				const Packet::S2C::ServerResponse *p = dynamic_cast<const Packet::S2C::ServerResponse*>(pPacket);
				m_pRPCManagerClient->OnTCPGameClientRecvServerResponse(this, p);
			}
			break;
		case Packet::eS2CPKTLuaChannel:
			{
				const Packet::S2C::LuaChannel *p = dynamic_cast<const Packet::S2C::LuaChannel*>(pPacket);
				m_pRPCManagerClient->OnTCPGameClientRecvLuaChannel(this, p);
			}
			break;
		case Packet::eS2CPKTLuaChannel2:
			{
				const Packet::S2C::LuaChannel2 *p = dynamic_cast<const Packet::S2C::LuaChannel2*>(pPacket);
				m_pRPCManagerClient->OnTCPGameClientRecvLuaChannel2(this, p);
			}
			break;
		case Packet::eS2CPKTStrChannel:
			{
				const Packet::S2C::StrChannel *p = dynamic_cast<const Packet::S2C::StrChannel*>(pPacket);
				m_pRPCManagerClient->OnTCPGameClientRecvStrChannel(this, p);
			}
			break;
		default:
			break;
		}
	}

}

