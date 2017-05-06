
package i3k.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;

import i3k.IDIP;
import ket.kio.ErrorCode;
import ket.kio.NetAddress;
import ket.kio.NetManager;
import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPServer;
import ket.util.Stream;
import ket.util.Stream.AIStream;
import ket.util.Stream.AOStream;
import ket.util.Stream.BytesOutputStream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;

class IDIPPacket implements ket.util.Stream.IStreamable
{	

	@Override
	public void encode(AOStream os)
	{
		os.push(header);
		os.pushBytes(body, 0, len);
	}

	@Override
	public void decode(AIStream is) throws EOFException, DecodeException
	{
		if( header == null )
			header = new IDIP.IdipHeader();
		is.pop(header);
		if (header.PacketLen - IDIP.PACKET_HEADER_SIZE > IDIP.IDIP_BODY_LENGTH)
		{
			System.out.println("header invalid: PacketLen=" + header.PacketLen + ", Cmdid=" + header.Cmdid + ", Seqid=" + header.Seqid);
			throw new DecodeException();
		}
		body = is.popBytes(header.PacketLen - IDIP.PACKET_HEADER_SIZE);
		len = body.length;
	}
	
	public IDIP.IdipHeader header;
	public byte[] body;
	public int len;
}

public class TCPIDIPServer extends TCPServer<IDIPPacket>
{

	public TCPIDIPServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}
	
	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPIDIPServerMaxConnectionIdleTime();
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
	public void onOpen()
	{
		managerRPC.onTCPIDIPServerOpen(this);
	}

	@Override
	public void onOpenFailed(ErrorCode errcode)
	{
		managerRPC.onTCPIDIPServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ErrorCode errcode)
	{
		managerRPC.onTCPIDIPServerClose(this, errcode);
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
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{	
		managerRPC.onTCPIDIPServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ErrorCode errcode)
	{	
		managerRPC.onTCPIDIPServerSessionClose(this, sessionid);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, IDIPPacket packet)
	{	
		managerRPC.onTCPIDIPServerPacketRecv(this, sessionid, packet);
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private RPCManager managerRPC;
}
