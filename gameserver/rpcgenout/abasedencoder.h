// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

#ifndef __I3K__ABASEDENCODER_H
#define __I3K__ABASEDENCODER_H

#include <ket/kio/packet.h>

namespace I3K
{

	class ABaseDencoder : public KET::KIO::SimpleDencoder
	{
	public:
		ABaseDencoder() { }
		virtual ~ABaseDencoder() { }

	public:
		virtual bool DoCheckPacketType(int /*ptype*/) { return true; }
		virtual KET::KIO::SimplePacket* CreatePacket(int /*ptype*/);
		virtual void DestroyPacket(KET::KIO::SimplePacket * /*pPacket*/) { }

	};
}

#endif
