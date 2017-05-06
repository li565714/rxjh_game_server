
package i3k.logger;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import ket.kio.ErrorCode;
import ket.kio.NetAddress;
import ket.kio.NetManager;

public class UDPLogServer extends NetManager.UDPPeer
{
	public UDPLogServer(RPCManagerLogServer managerRPC)
	{
		managerRPC.getNetManager().super();
		this.managerRPC = managerRPC;
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
		try {
			managerRPC.onDataRecv(addrRemote, new String(data, offset, len, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onOpen()
	{
		managerRPC.onUDPLogServerOpen(this);
	}

	@Override
	public void onOpenFailed(ErrorCode errcode)
	{
		managerRPC.onUDPLogServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ErrorCode errcode)
	{
		managerRPC.onUDPLogServerClose(this, errcode);
	}
	
	RPCManagerLogServer managerRPC;
}
