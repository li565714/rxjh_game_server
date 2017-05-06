// modified by ket.kio.RPCGen at Tue Jul 22 19:45:24 CST 2014.

package i3k.alarm;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.util.Stream;
import ket.util.Stream.AIStream;
import ket.util.Stream.AOStream;

public class TCPAlarmClient extends TCPClient<AlarmPacket>
{

	public TCPAlarmClient(AlarmDetector detector, String msg)
	{
		super(detector.getNetManager());
		this.setServerAddr(detector.getServerAddr());
		this.sendMsg = msg;
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
	public void onOpen()
	{
		this.sendPacket(AlarmPacket.createAlarmPacket(isBigEndian(), sendMsg));
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		this.close();
		this.latch.countDown();
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		this.latch.countDown();
	}

	@Override
	public void onPacketRecv(AlarmPacket packet)
	{
		this.recvPacket = packet;
		this.close();
		this.latch.countDown();
	}
	
	public String sendDetectionMsg()
	{
		try
		{
			this.open();
			this.latch.await(5, TimeUnit.SECONDS);
			return AlarmPacket.tryDecodeMsgPacket(recvPacket);
		}
		catch (InterruptedException e)
		{
			
		}
		return null;
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private final String sendMsg;
	private AlarmPacket recvPacket;
	private CountDownLatch latch = new CountDownLatch(1);
}
