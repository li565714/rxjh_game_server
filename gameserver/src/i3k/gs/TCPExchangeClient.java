// modified by ket.kio.RPCGen at Thu Mar 16 10:10:28 CST 2017.

package i3k.gs;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPExchangeClient extends TCPClient<SimplePacket>
{

	public TCPExchangeClient(RPCManager managerRPC)
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
			// es to gs
			case Packet.eE2SPKTKeepAlive:
			case Packet.eE2SPKTReceiveMsg:
			case Packet.eE2SPKTSocialMsgRes:
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
		managerRPC.onTCPExchangeClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPExchangeClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPExchangeClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// es to gs
		case Packet.eE2SPKTKeepAlive:
			{
				Packet.E2S.KeepAlive p = (Packet.E2S.KeepAlive)packet;
				managerRPC.onTCPExchangeClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eE2SPKTReceiveMsg:
			{
				Packet.E2S.ReceiveMsg p = (Packet.E2S.ReceiveMsg)packet;
				managerRPC.onTCPExchangeClientRecvReceiveMsg(this, p);
			}
			break;
		case Packet.eE2SPKTSocialMsgRes:
			{
				Packet.E2S.SocialMsgRes p = (Packet.E2S.SocialMsgRes)packet;
				managerRPC.onTCPExchangeClientRecvSocialMsgRes(this, p);
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
