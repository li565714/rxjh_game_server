// modified by ket.kio.RPCGen at Tue Jul 22 19:45:24 CST 2014.

package i3k.alarm;

import java.io.IOException;
import java.util.Objects;

import ket.kio.NetAddress;
import ket.kio.NetManager;
import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.util.Stream;
import ket.util.Stream.AIStream;
import ket.util.Stream.AOStream;

public class AlarmDetector
{

	public AlarmDetector()
	{
		
	}
	
	public void start(String host, int port)
	{
		alarmAddr.set(host, port);
		managerNet.start();
	}
	
	public void destroy()
	{
		managerNet.destroy();
	}
	
	NetManager getNetManager()
	{
		return managerNet;
	}
	
	NetAddress getServerAddr()
	{
		return alarmAddr;
	}
	
	public String detectServer(String msg)
	{
		TCPAlarmClient client = new TCPAlarmClient(this, msg);
		return client.sendDetectionMsg();
	}
	

	NetManager managerNet = new NetManager();
	NetAddress alarmAddr = new NetAddress("localhost", 1191);
	
	
	public static void main(String[] args)
	{
		String host = "127.0.0.1";
		int port = 1191;
		String srvname = "server";
		for (String arg : args)
		{
			if (arg.startsWith("-h="))
				host = arg.substring("-h=".length());
			else if (arg.startsWith("-p="))
				port = Integer.parseInt(arg.substring("-p=".length()));
			else if (arg.startsWith("-s="))
				srvname = arg.substring("-s=".length());
		}
		AlarmDetector detector = new AlarmDetector();
		detector.start(host, port);
		String msg = "detect " +  host + ":" + port;
		String res = detector.detectServer(msg);
		detector.destroy();
		System.out.println(msg + "  -> " + srvname + (Objects.equals(res, msg) ? " active " : " inactive"));
		System.exit(Objects.equals(res, msg) ? 0 : -1);
	}
}
