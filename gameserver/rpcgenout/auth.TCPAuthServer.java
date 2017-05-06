// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.auth;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPAuthServer extends TCPServer<SimplePacket>
{

	public TCPAuthServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPAuthServerMaxConnectionIdleTime();
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// server to auth
			case Packet.eS2AUPKTKeepAlive:
			case Packet.eS2AUPKTWhoAmI:
			case Packet.eS2AUPKTPayRes:
				return true;
			default:
				break;
			}
			return false;
		}
	}

	@Override
	public PacketEncoder<SimplePacket> getEncoder()
	{
		return dencoder;
	}

	@Override
	public PacketDecoder<SimplePacket> getDecoder()
	{
		return dencoder;
	}

	@Override
	public void onOpen()
	{
		managerRPC.onTCPAuthServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuthServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuthServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPAuthServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuthServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// server to auth
		case Packet.eS2AUPKTKeepAlive:
			{
				Packet.S2AU.KeepAlive p = (Packet.S2AU.KeepAlive)packet;
				managerRPC.onTCPAuthServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eS2AUPKTWhoAmI:
			{
				Packet.S2AU.WhoAmI p = (Packet.S2AU.WhoAmI)packet;
				managerRPC.onTCPAuthServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eS2AUPKTPayRes:
			{
				Packet.S2AU.PayRes p = (Packet.S2AU.PayRes)packet;
				managerRPC.onTCPAuthServerRecvPayRes(this, p, sessionid);
			}
			break;
		default:
			break;
		}
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private RPCManager managerRPC;
}
