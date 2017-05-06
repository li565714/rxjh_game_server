// modified by ket.kio.RPCGen at Tue Jul 22 19:45:24 CST 2014.

package i3k.proxy;

import java.io.IOException;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.util.Stream;
import ket.util.Stream.AIStream;
import ket.util.Stream.AOStream;

public class TCPIDIPClient extends TCPClient<IDIPPacket>
{

	public TCPIDIPClient(RPCManager managerRPC, int sid, IDIPPacket packet)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
		this.taskSID = sid;
		this.taskPacket = packet;
	}

	@Override
	public boolean isBigEndian() { return true; }
	
	private class Dencoder implements PacketEncoder<IDIPPacket>, PacketDecoder<IDIPPacket>
	{

		@Override
		public IDIPPacket decode(AIStream is) throws ket.kio.PacketDecoder.Exception
		{
			IDIPPacket packet = null;
			try
			{
				is.getInputStream().mark(Integer.MAX_VALUE);
				packet = new IDIPPacket();
				is.pop(packet);
			}
			catch(Stream.EOFException ex)
			{
				try
				{
					is.getInputStream().reset();
				}
				catch(IOException ex2)
				{
					throw new Exception();
				}
				packet = null;
			}
			catch(Stream.DecodeException ex)
			{
				try
				{
					is.getInputStream().reset();
				}
				catch(IOException ex2)
				{
				}
				throw new Exception();
			}
			return packet;
		}

		@Override
		public void encode(IDIPPacket packet, AOStream os)
		{
			os.push(packet);		
		}
		
	}

	@Override
	public PacketEncoder<IDIPPacket> getEncoder()
	{
		return dencoder;
	}

	@Override
	public PacketDecoder<IDIPPacket> getDecoder()
	{
		return dencoder;
	}

	@Override
	public void onOpen()
	{
		managerRPC.onTCPIDIPClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPIDIPClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPIDIPClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(IDIPPacket packet)
	{
		managerRPC.onTCPIDIPClientPacketRecv(this, packet);
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private RPCManager managerRPC;
	public final int taskSID;
	public final IDIPPacket taskPacket;
}
