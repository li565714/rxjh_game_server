// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.fight;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPFightMapServer extends TCPServer<SimplePacket>
{

	public TCPFightMapServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPFightMapServerMaxConnectionIdleTime();
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// global ms to fs
			case Packet.eGM2FPKTKeepAlive:
			case Packet.eGM2FPKTWhoAmI:
			case Packet.eGM2FPKTCreateMapCopyRes:
			case Packet.eGM2FPKTSyncForceWarMapEnd:
			case Packet.eGM2FPKTSyncSuperArenaMapEnd:
			case Packet.eGM2FPKTSyncHp:
			case Packet.eGM2FPKTSyncSuperArenaRaceEnd:
			case Packet.eGM2FPKTSyncDemonHoleKill:
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
		managerRPC.onTCPFightMapServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightMapServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightMapServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPFightMapServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightMapServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// global ms to fs
		case Packet.eGM2FPKTKeepAlive:
			{
				Packet.GM2F.KeepAlive p = (Packet.GM2F.KeepAlive)packet;
				managerRPC.onTCPFightMapServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eGM2FPKTWhoAmI:
			{
				Packet.GM2F.WhoAmI p = (Packet.GM2F.WhoAmI)packet;
				managerRPC.onTCPFightMapServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eGM2FPKTCreateMapCopyRes:
			{
				Packet.GM2F.CreateMapCopyRes p = (Packet.GM2F.CreateMapCopyRes)packet;
				managerRPC.onTCPFightMapServerRecvCreateMapCopyRes(this, p, sessionid);
			}
			break;
		case Packet.eGM2FPKTSyncForceWarMapEnd:
			{
				Packet.GM2F.SyncForceWarMapEnd p = (Packet.GM2F.SyncForceWarMapEnd)packet;
				managerRPC.onTCPFightMapServerRecvSyncForceWarMapEnd(this, p, sessionid);
			}
			break;
		case Packet.eGM2FPKTSyncSuperArenaMapEnd:
			{
				Packet.GM2F.SyncSuperArenaMapEnd p = (Packet.GM2F.SyncSuperArenaMapEnd)packet;
				managerRPC.onTCPFightMapServerRecvSyncSuperArenaMapEnd(this, p, sessionid);
			}
			break;
		case Packet.eGM2FPKTSyncHp:
			{
				Packet.GM2F.SyncHp p = (Packet.GM2F.SyncHp)packet;
				managerRPC.onTCPFightMapServerRecvSyncHp(this, p, sessionid);
			}
			break;
		case Packet.eGM2FPKTSyncSuperArenaRaceEnd:
			{
				Packet.GM2F.SyncSuperArenaRaceEnd p = (Packet.GM2F.SyncSuperArenaRaceEnd)packet;
				managerRPC.onTCPFightMapServerRecvSyncSuperArenaRaceEnd(this, p, sessionid);
			}
			break;
		case Packet.eGM2FPKTSyncDemonHoleKill:
			{
				Packet.GM2F.SyncDemonHoleKill p = (Packet.GM2F.SyncDemonHoleKill)packet;
				managerRPC.onTCPFightMapServerRecvSyncDemonHoleKill(this, p, sessionid);
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
