// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPAuthClient extends TCPClient<SimplePacket>
{

	public TCPAuthClient(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// auth to server
			case Packet.eAU2SPKTKeepAlive:
			case Packet.eAU2SPKTPayReq:
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
		managerRPC.onTCPAuthClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuthClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuthClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// auth to server
		case Packet.eAU2SPKTKeepAlive:
			{
				Packet.AU2S.KeepAlive p = (Packet.AU2S.KeepAlive)packet;
				managerRPC.onTCPAuthClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eAU2SPKTPayReq:
			{
				Packet.AU2S.PayReq p = (Packet.AU2S.PayReq)packet;
				managerRPC.onTCPAuthClientRecvPayReq(this, p);
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
