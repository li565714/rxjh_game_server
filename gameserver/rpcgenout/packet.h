// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

#ifndef __I3K__PACKET_H
#define __I3K__PACKET_H

#include <ket/kio/packet.h>

#include "sbean.h"

namespace I3K
{

	namespace Packet
	{

		enum
		{
			// server to client
			eS2CPKTServerChallenge = 1,
			eS2CPKTServerResponse = 2,
			eS2CPKTLuaChannel = 5,
			eS2CPKTStrChannel = 6,
			eS2CPKTLuaChannel2 = 7,

			// client to server
			eC2SPKTClientResponse = 10001,
			eC2SPKTLuaChannel = 10004,
			eC2SPKTLuaChannel2 = 10005,
			eC2SPKTStrChannel = 10006,

		};

		// server to client
		namespace S2C
		{

			class ServerChallenge : public KET::KIO::SimplePacket
			{
			public:
				ServerChallenge() { }

				ServerChallenge(int istate, const std::string& sstate, int flag, const std::vector<unsigned char>& key)
				    : m_istate(istate), m_sstate(sstate), m_flag(flag), m_key(key)
				{
				}

			public:
				virtual int GetType() const
				{
					return eS2CPKTServerChallenge;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_istate >> m_sstate >> m_flag >> m_key;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_istate << m_sstate << m_flag << m_key;
				}

			public:
				int GetIstate() const
				{
					return m_istate;
				}

				void SetIstate(int istate)
				{
					m_istate = istate;
				}

				const std::string& GetSstate() const
				{
					return m_sstate;
				}

				std::string& GetSstate()
				{
					return m_sstate;
				}

				void SetSstate(const std::string& sstate)
				{
					m_sstate = sstate;
				}

				int GetFlag() const
				{
					return m_flag;
				}

				void SetFlag(int flag)
				{
					m_flag = flag;
				}

				const std::vector<unsigned char>& GetKey() const
				{
					return m_key;
				}

				std::vector<unsigned char>& GetKey()
				{
					return m_key;
				}

				void SetKey(const std::vector<unsigned char>& key)
				{
					m_key = key;
				}

			private:
				int m_istate;
				std::string m_sstate;
				int m_flag;
				std::vector<unsigned char> m_key;
			};

			class ServerResponse : public KET::KIO::SimplePacket
			{
			public:
				ServerResponse() { }

				ServerResponse(int res)
				    : m_res(res)
				{
				}

			public:
				virtual int GetType() const
				{
					return eS2CPKTServerResponse;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_res;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_res;
				}

			public:
				int GetRes() const
				{
					return m_res;
				}

				void SetRes(int res)
				{
					m_res = res;
				}

			private:
				int m_res;
			};

			class LuaChannel : public KET::KIO::SimplePacket
			{
			public:
				LuaChannel() { }

				LuaChannel(const std::string& data)
				    : m_data(data)
				{
				}

			public:
				virtual int GetType() const
				{
					return eS2CPKTLuaChannel;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_data;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_data;
				}

			public:
				const std::string& GetData() const
				{
					return m_data;
				}

				std::string& GetData()
				{
					return m_data;
				}

				void SetData(const std::string& data)
				{
					m_data = data;
				}

			private:
				std::string m_data;
			};

			class StrChannel : public KET::KIO::SimplePacket
			{
			public:
				StrChannel() { }

				StrChannel(const std::string& data)
				    : m_data(data)
				{
				}

			public:
				virtual int GetType() const
				{
					return eS2CPKTStrChannel;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_data;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_data;
				}

			public:
				const std::string& GetData() const
				{
					return m_data;
				}

				std::string& GetData()
				{
					return m_data;
				}

				void SetData(const std::string& data)
				{
					m_data = data;
				}

			private:
				std::string m_data;
			};

			class LuaChannel2 : public KET::KIO::SimplePacket
			{
			public:
				LuaChannel2() { }

				LuaChannel2(const std::vector<std::string>& data)
				    : m_data(data)
				{
				}

			public:
				virtual int GetType() const
				{
					return eS2CPKTLuaChannel2;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_data;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_data;
				}

			public:
				const std::vector<std::string>& GetData() const
				{
					return m_data;
				}

				std::vector<std::string>& GetData()
				{
					return m_data;
				}

				void SetData(const std::vector<std::string>& data)
				{
					m_data = data;
				}

			private:
				std::vector<std::string> m_data;
			};

		}

		// client to server
		namespace C2S
		{

			class ClientResponse : public KET::KIO::SimplePacket
			{
			public:
				ClientResponse() { }

				ClientResponse(const std::vector<unsigned char>& key)
				    : m_key(key)
				{
				}

			public:
				virtual int GetType() const
				{
					return eC2SPKTClientResponse;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_key;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_key;
				}

			public:
				const std::vector<unsigned char>& GetKey() const
				{
					return m_key;
				}

				std::vector<unsigned char>& GetKey()
				{
					return m_key;
				}

				void SetKey(const std::vector<unsigned char>& key)
				{
					m_key = key;
				}

			private:
				std::vector<unsigned char> m_key;
			};

			class LuaChannel : public KET::KIO::SimplePacket
			{
			public:
				LuaChannel() { }

				LuaChannel(const std::string& data)
				    : m_data(data)
				{
				}

			public:
				virtual int GetType() const
				{
					return eC2SPKTLuaChannel;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_data;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_data;
				}

			public:
				const std::string& GetData() const
				{
					return m_data;
				}

				std::string& GetData()
				{
					return m_data;
				}

				void SetData(const std::string& data)
				{
					m_data = data;
				}

			private:
				std::string m_data;
			};

			class LuaChannel2 : public KET::KIO::SimplePacket
			{
			public:
				LuaChannel2() { }

				LuaChannel2(const std::vector<std::string>& data)
				    : m_data(data)
				{
				}

			public:
				virtual int GetType() const
				{
					return eC2SPKTLuaChannel2;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_data;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_data;
				}

			public:
				const std::vector<std::string>& GetData() const
				{
					return m_data;
				}

				std::vector<std::string>& GetData()
				{
					return m_data;
				}

				void SetData(const std::vector<std::string>& data)
				{
					m_data = data;
				}

			private:
				std::vector<std::string> m_data;
			};

			class StrChannel : public KET::KIO::SimplePacket
			{
			public:
				StrChannel() { }

				StrChannel(const std::string& data)
				    : m_data(data)
				{
				}

			public:
				virtual int GetType() const
				{
					return eC2SPKTStrChannel;
				}

				virtual void Decode(KET::Util::Stream::AIStream &istream)
				{
					istream >> m_data;
				}

				virtual void Encode(KET::Util::Stream::AOStream &ostream) const
				{
					ostream << m_data;
				}

			public:
				const std::string& GetData() const
				{
					return m_data;
				}

				std::string& GetData()
				{
					return m_data;
				}

				void SetData(const std::string& data)
				{
					m_data = data;
				}

			private:
				std::string m_data;
			};

		}

	}
}

#endif
