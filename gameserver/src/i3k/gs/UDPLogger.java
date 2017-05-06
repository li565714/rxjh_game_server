
package i3k.gs;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import ket.kio.ErrorCode;
import ket.kio.NetAddress;
import ket.kio.NetManager;

public class UDPLogger extends NetManager.UDPPeer
{
	public UDPLogger(RPCManager managerRPC)
	{
		managerRPC.getNetManager().super();
	}
	
	public static ByteBuffer wrap(String s) throws UnsupportedEncodingException
	{
		return ByteBuffer.wrap(s.getBytes("UTF-8"));
	}
	
	public void sendString(String log)
	{
		try
		{
			sendData(wrap(log));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onDataRecv(NetAddress addrRemote, byte[] data, int offset, int len)
	{
		// TODO
	}

	@Override
	public void onOpen()
	{	
	}

	@Override
	public void onOpenFailed(ErrorCode errcode)
	{	
	}

	@Override
	public void onClose(ErrorCode errcode)
	{	
	}
}
