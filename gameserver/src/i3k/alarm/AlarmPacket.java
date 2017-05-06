
package i3k.alarm;

import java.io.IOException;
import java.nio.ByteBuffer;

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

public class AlarmPacket implements ket.util.Stream.IStreamable
{	

	@Override
	public void encode(AOStream os)
	{
		os.pushInteger(type);
		os.pushInteger(len);
		os.pushBytes(body, 0, len);
	}

	@Override
	public void decode(AIStream is) throws EOFException, DecodeException
	{
		type = is.popInteger();
		len = is.popInteger();
		if (len > MAX_PACKET_SIZE)
			throw new DecodeException();
		body = is.popBytes(len);
	}
	
	public int type;
	public int len;
	public byte[] body;
	private final static int MAX_PACKET_SIZE = 1024;
	
	public static final int ALARM_MSG_TYPE_STRING = 1;
	
	public static Stream.IStreamable decodePacket(int type, byte[] bodyData)
	{
		try
		{
			Stream.BytesInputStream bais = new Stream.BytesInputStream(bodyData, 0, bodyData.length);
			Stream.AIStream is = new Stream.IStreamBE(bais);
			switch (type)
			{
			case ALARM_MSG_TYPE_STRING:
				AlarmStringMsg obj = new AlarmStringMsg();
				obj.decode(is);
				return obj;
			default:
				break;
			}
		}
		catch (Exception e)
		{
			
		}
		return null;
	}
	
	public static String tryDecodeMsgPacket(AlarmPacket packet)
	{
		if (packet != null)
		{
			Stream.IStreamable obj = AlarmPacket.decodePacket(packet.type, packet.body);
			if (obj != null) {
				AlarmPacket.AlarmStringMsg strmsg = (AlarmPacket.AlarmStringMsg) obj;
				return strmsg.msg;
			}
		}
		return null;
	}
	
	public static AlarmPacket createAlarmPacket(boolean bigEndian, String msg)
	{
		return createAlarmPacket(bigEndian, ALARM_MSG_TYPE_STRING, new AlarmStringMsg(msg));
	}
	
	private static AlarmPacket createAlarmPacket(boolean bigEndian, int type, Stream.IStreamable bodyData)
	{
		BytesOutputStream bsos = new BytesOutputStream();
		Stream.AOStream os = bigEndian ? new Stream.OStreamBE(bsos) : new Stream.OStreamLE(bsos);
		os.push(bodyData);

		AlarmPacket packet = new AlarmPacket();
		packet.type = type;
		packet.len = bsos.size();
		packet.body = bsos.array();
		return packet;
	}
	
	public static class AlarmStringMsg implements Stream.IStreamable
	{
		public AlarmStringMsg()
		{
			
		}
		
		public AlarmStringMsg(String msg)
		{
			this.msg = msg;
		}
		
		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			this.msg = is.popString();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushString(this.msg);
		}
		
		public String msg = new String();
	}
}

