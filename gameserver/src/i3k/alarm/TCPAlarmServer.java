
package i3k.alarm;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import i3k.util.GameTime;
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

public class TCPAlarmServer extends TCPServer<AlarmPacket>
{

	public TCPAlarmServer(NetManager managerNet, NetAddress addrListen, Logger logger)
	{
		super(managerNet);
		this.logger = logger;
		this.setListenAddr(addrListen, ket.kio.BindPolicy.eReuseTimewait);
		this.setListenBacklog(16);
	}
	
	@Override
	public boolean isBigEndian() { return true; }
	
	private class Dencoder implements PacketEncoder<AlarmPacket>, PacketDecoder<AlarmPacket>
	{

		@Override
		public AlarmPacket decode(AIStream is) throws ket.kio.PacketDecoder.Exception
		{
			AlarmPacket packet = null;
			try
			{
				is.getInputStream().mark(Integer.MAX_VALUE);
				packet = new AlarmPacket();
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
		public void encode(AlarmPacket packet, AOStream os)
		{
			os.push(packet);		
		}
		
	}

	@Override
	public void onOpen()
	{
		logger.info("tcpalarmserver open on " + this.getListenAddr());
	}

	@Override
	public void onOpenFailed(ErrorCode errcode)
	{
		logger.warn("tcpalarmserver open on " + this.getListenAddr() + " failed, errcode=" + errcode);
	}

	@Override
	public void onClose(ErrorCode errcode)
	{
		logger.info("tcpalarmserver close on " + this.getListenAddr() + ", errcode=" + errcode);
	}

	@Override
	public PacketEncoder<AlarmPacket> getEncoder()
	{
		return dencoder;
	}

	@Override
	public PacketDecoder<AlarmPacket> getDecoder()
	{
		return dencoder;
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		logger.debug("tcpalarmserver: session " + sessionid + " open, client addr " + addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ErrorCode errcode)
	{
		logger.debug("tcpalarmserver: session " + sessionid + " close, errcode=" + errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, AlarmPacket packet)
	{	
		String msg = AlarmPacket.tryDecodeMsgPacket(packet);
		if (msg != null)
			msgs.put(nextMsgId.incrementAndGet(), new TestActiveMsg(sessionid, msg));
		else
			this.closeSession(sessionid);
	}
	
	public void start()
	{
		if (!this.isOpen())
			this.open();	
	}
	
	public void onTimer()
	{
		if (!this.isOpen())
			this.open();
		Iterator<Map.Entry<Integer, TestActiveMsg>> it = msgs.entrySet().iterator();
		while (it.hasNext())
		{
			TestActiveMsg tmsg = it.next().getValue();
			sendRes(tmsg.session, tmsg.msg);
			it.remove();
		}
	}
	
	private void sendRes(int sessionid, String msg)
	{
		sendPacket(sessionid, AlarmPacket.createAlarmPacket(isBigEndian(), msg));
	}
	
	static class TestActiveMsg
	{
		int session;
		String msg;
		TestActiveMsg(int session, String msg)
		{
			this.session = session;
			this.msg = msg;
		}
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private Logger logger;
	private ConcurrentMap<Integer, TestActiveMsg> msgs = new ConcurrentHashMap<>();
	private AtomicInteger nextMsgId = new AtomicInteger();
}
