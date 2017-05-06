
package i3k.gs;

import java.io.IOException;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.NetManager;
import ket.util.Stream;
import ket.util.Stream.AIStream;
import ket.util.Stream.AOStream;
import ket.util.Stream.BytesOutputStream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;
import i3k.IDIP;
import i3k.util.GameTime;



public class TCPIDIPClient extends TCPClient<IDIPPacket>
{	
	public TCPIDIPClient(IDIPService service, int sid, IDIP.IdipHeader header, Stream.IStreamable bodyData, IDIPService.IDIPCallback callback)
	{
		super(service.getNetManager());
		this.service = service;
		this.taskSID = sid;
		header.Seqid = sid;
		this.taskPacket = getIDIPPacket(header, bodyData);
		this.callback = callback;
		this.startTime = GameTime.getGMTTime();
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
		service.onTCPIDIPClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		service.onTCPIDIPClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		service.onTCPIDIPClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(IDIPPacket packet)
	{
		service.onTCPIDIPClientPacketRecv(this, packet);
	}

	public IDIP.IdipHeader createResHeader(int result, String retErrMsg, int now)
	{
		return IDIPService.createResHeader(taskPacket.header, result, retErrMsg, now);
	}
	
	public IDIPPacket getIDIPPacket(IDIP.IdipHeader header, Stream.IStreamable bodyData)
	{
		IDIPPacket packet = new IDIPPacket();
		packet.header = header;
		
		
		BytesOutputStream bsos = new BytesOutputStream();
		Stream.AOStream os = isBigEndian() ? new Stream.OStreamBE(bsos) : new Stream.OStreamLE(bsos);
		if (bodyData != null)
			os.push(bodyData);
		else
			os.pushInteger(0);
		
		int len = bsos.size();
		header.PacketLen = IDIP.PACKET_HEADER_SIZE + len;
		packet.body = bsos.array();
		packet.len = len;
		
		return packet;
	}
	
//	public IDIPPacket getRspPacket(int headerResult, int bodyResult, String errMsg, int now)
//	{
//		IDIP.IdipHeader header = createResHeader(headerResult, errMsg, now);
//		Stream.IStreamable body = IDIPService.createResBody(header.Cmdid, bodyResult, errMsg);
//		return getIDIPPacket(header, body);
//	}
	
	public String toString()
	{
		return "sid=" + taskSID + ", cmdID=0x" + Integer.toHexString(taskPacket.header.Cmdid) + ", seqID=" + taskPacket.header.Seqid;
	}
	
	//todo
	private Dencoder dencoder = new Dencoder();
	private IDIPService service;
	public final int taskSID;
	public final IDIPPacket taskPacket;
	public final IDIPService.IDIPCallback callback;
	public final int startTime; 
}
