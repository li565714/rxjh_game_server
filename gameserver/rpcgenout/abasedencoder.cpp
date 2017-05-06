// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

#include "abasedencoder.h"
#include "packet.h"

namespace I3K
{
	KET::KIO::SimplePacket* ABaseDencoder::CreatePacket(int ptype)
	{
		KET::KIO::SimplePacket *p = NULL;
		switch( ptype )
		{
		// server to client
		case Packet::eS2CPKTServerChallenge:
			p = new Packet::S2C::ServerChallenge();
			break;
		case Packet::eS2CPKTServerResponse:
			p = new Packet::S2C::ServerResponse();
			break;
		case Packet::eS2CPKTLuaChannel:
			p = new Packet::S2C::LuaChannel();
			break;
		case Packet::eS2CPKTStrChannel:
			p = new Packet::S2C::StrChannel();
			break;
		case Packet::eS2CPKTLuaChannel2:
			p = new Packet::S2C::LuaChannel2();
			break;

		// client to server
		case Packet::eC2SPKTClientResponse:
			p = new Packet::C2S::ClientResponse();
			break;
		case Packet::eC2SPKTLuaChannel:
			p = new Packet::C2S::LuaChannel();
			break;
		case Packet::eC2SPKTLuaChannel2:
			p = new Packet::C2S::LuaChannel2();
			break;
		case Packet::eC2SPKTStrChannel:
			p = new Packet::C2S::StrChannel();
			break;

		default:
			break;
		}
		return p;
	}
}
