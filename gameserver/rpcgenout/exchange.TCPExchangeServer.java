// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.exchange;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPExchangeServer extends TCPServer<SimplePacket>
{

	public TCPExchangeServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPExchangeServerMaxConnectionIdleTime();
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// gs ms to es
			case Packet.eS2EPKTKeepAlive:
			case Packet.eS2EPKTWhoAmI:
			case Packet.eS2EPKTSendMsg:
			case Packet.eS2EPKTSocialMsgReq:
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
		managerRPC.onTCPExchangeServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPExchangeServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPExchangeServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPExchangeServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPExchangeServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// gs ms to es
		case Packet.eS2EPKTKeepAlive:
			{
				Packet.S2E.KeepAlive p = (Packet.S2E.KeepAlive)packet;
				managerRPC.onTCPExchangeServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eS2EPKTWhoAmI:
			{
				Packet.S2E.WhoAmI p = (Packet.S2E.WhoAmI)packet;
				managerRPC.onTCPExchangeServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eS2EPKTSendMsg:
			{
				Packet.S2E.SendMsg p = (Packet.S2E.SendMsg)packet;
				managerRPC.onTCPExchangeServerRecvSendMsg(this, p, sessionid);
			}
			break;
		case Packet.eS2EPKTSocialMsgReq:
			{
				Packet.S2E.SocialMsgReq p = (Packet.S2E.SocialMsgReq)packet;
				managerRPC.onTCPExchangeServerRecvSocialMsgReq(this, p, sessionid);
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
