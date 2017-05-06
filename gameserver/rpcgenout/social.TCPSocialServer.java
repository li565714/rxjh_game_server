// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.social;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPSocialServer extends TCPServer<SimplePacket>
{

	public TCPSocialServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPSocialServerMaxConnectionIdleTime();
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// gs ms to ss
			case Packet.eE2SSPKTKeepAlive:
			case Packet.eE2SSPKTWhoAmI:
			case Packet.eE2SSPKTForwardReq:
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
		managerRPC.onTCPSocialServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPSocialServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPSocialServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPSocialServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPSocialServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// gs ms to ss
		case Packet.eE2SSPKTKeepAlive:
			{
				Packet.E2SS.KeepAlive p = (Packet.E2SS.KeepAlive)packet;
				managerRPC.onTCPSocialServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eE2SSPKTWhoAmI:
			{
				Packet.E2SS.WhoAmI p = (Packet.E2SS.WhoAmI)packet;
				managerRPC.onTCPSocialServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eE2SSPKTForwardReq:
			{
				Packet.E2SS.ForwardReq p = (Packet.E2SS.ForwardReq)packet;
				managerRPC.onTCPSocialServerRecvForwardReq(this, p, sessionid);
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
