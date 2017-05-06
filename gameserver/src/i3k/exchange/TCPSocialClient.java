// modified by ket.kio.RPCGen at Wed Mar 15 16:01:41 CST 2017.

package i3k.exchange;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPSocialClient extends TCPClient<SimplePacket>
{

	public TCPSocialClient(RPCManager managerRPC)
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
			// es to ss
			case Packet.eSS2EPKTKeepAlive:
			case Packet.eSS2EPKTForwardRes:
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
		managerRPC.onTCPSocialClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPSocialClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPSocialClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// es to ss
		case Packet.eSS2EPKTKeepAlive:
			{
				Packet.SS2E.KeepAlive p = (Packet.SS2E.KeepAlive)packet;
				managerRPC.onTCPSocialClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eSS2EPKTForwardRes:
			{
				Packet.SS2E.ForwardRes p = (Packet.SS2E.ForwardRes)packet;
				managerRPC.onTCPSocialClientRecvForwardRes(this, p);
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
