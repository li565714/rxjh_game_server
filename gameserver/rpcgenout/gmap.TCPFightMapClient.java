// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gmap;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPFightMapClient extends TCPClient<SimplePacket>
{

	public TCPFightMapClient(RPCManager managerRPC)
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
			// fs to global ms
			case Packet.eF2GMPKTKeepAlive:
			case Packet.eF2GMPKTCreateMapCopyReq:
			case Packet.eF2GMPKTEndMapCopy:
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
		managerRPC.onTCPFightMapClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightMapClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightMapClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// fs to global ms
		case Packet.eF2GMPKTKeepAlive:
			{
				Packet.F2GM.KeepAlive p = (Packet.F2GM.KeepAlive)packet;
				managerRPC.onTCPFightMapClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eF2GMPKTCreateMapCopyReq:
			{
				Packet.F2GM.CreateMapCopyReq p = (Packet.F2GM.CreateMapCopyReq)packet;
				managerRPC.onTCPFightMapClientRecvCreateMapCopyReq(this, p);
			}
			break;
		case Packet.eF2GMPKTEndMapCopy:
			{
				Packet.F2GM.EndMapCopy p = (Packet.F2GM.EndMapCopy)packet;
				managerRPC.onTCPFightMapClientRecvEndMapCopy(this, p);
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
