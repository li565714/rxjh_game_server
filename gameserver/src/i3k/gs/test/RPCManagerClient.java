// modified by ket.kio.RPCGen at Wed Jul 29 14:42:36 CST 2015.

package i3k.gs.test;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;

import ket.kio.NetManager;
import ket.util.ARC4StreamSecurity;
import i3k.gs.GameData;
import i3k.rpc.Packet;

public class RPCManagerClient
{
	public RPCManagerClient(GameClient gc)
	{
		this.gc = gc;
	}
	
	public NetManager getNetManager() { return managerNet; }
	
	
	
	public void start()
	{
		managerNet.start();
	}
	
	public void destroy()
	{
		managerNet.destroy();
	}
	


	//// begin handlers.
	public void onTCPGameClientOpen(TCPGameClient peer)
	{
		gc.getLogger().debug("tcpgameclient on open " + peer.getServerAddr() + " success, client address : " + peer.getClientAddr());
		Robot robot = (Robot)peer;
		robot.onConnectSuccess();
	}

	public void onTCPGameClientOpenFailed(TCPGameClient peer, ket.kio.ErrorCode errcode)
	{
		gc.getLogger().debug("tcpgameclient on open " + peer.getServerAddr() + " failed " + errcode);
		Robot robot = (Robot)peer;
		robot.onConnectFailed();
	}

	public void onTCPGameClientClose(TCPGameClient peer, ket.kio.ErrorCode errcode)
	{
		gc.getLogger().debug("tcpgameclient " + peer.getClientAddr() + " on close to " + peer.getServerAddr() + errcode);
		Robot robot = (Robot)peer;
		robot.onConnectClose();
	}

	public void onTCPGameClientRecvServerChallenge(TCPGameClient peer, Packet.S2C.ServerChallenge packet)
	{
		gc.getLogger().debug("tcpgameclient " + peer.getClientAddr() + " on receive server challenge");
		if (packet.getFlag() == 1)
		{
			
			try
			{
				byte[] keyRand = new byte[packet.getKey().size()];
				for(int i = 0; i < keyRand.length; ++i)
				{
					keyRand[i] = packet.getKey().get(i);
				}
				byte[] keyC = GameData.getChallengeKey(keyRand, gc.getConfig().challengeFuncArg.getBytes("UTF-8"));	
				peer.setOutputSecurity(new ARC4StreamSecurity(keyC));
				for(int i = 0; i < keyRand.length; ++i)
				{
					keyRand[i] = 0;
				}
				byte[] keyS = GameData.getChallengeKey(keyRand, gc.getConfig().challengeFuncArg.getBytes("UTF-8"));
				peer.setInputSecurity(new ARC4StreamSecurity(keyS));
				List<Byte> keyRandS = new ArrayList<>();
				for (int i = 0; i < keyRand.length; ++i)
				{
					keyRandS.add(keyRand[i]);
				}
				peer.sendPacket(new Packet.C2S.ClientResponse(keyRandS));
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		Robot robot = (Robot)peer;
		robot.onChallenged();
	}

	public void onTCPGameClientRecvServerResponse(TCPGameClient peer, Packet.S2C.ServerResponse packet)
	{
		gc.getLogger().debug("tcpgameclient " + peer.getClientAddr() + " on receive server response");
	}

	public void onTCPGameClientRecvLuaChannel(TCPGameClient peer, Packet.S2C.LuaChannel packet)
	{
		gc.getLogger().debug("tcpgameclient " + peer.getClientAddr() + " on receive server lua channel " + packet.getData());
	}

	public void onTCPGameClientRecvLuaChannel2(TCPGameClient peer, Packet.S2C.LuaChannel2 packet)
	{
		gc.getLogger().debug("tcpgameclient " + peer.getClientAddr() + " on receive server lua channel2 " + packet.getData());
	}

	public void onTCPGameClientRecvStrChannel(TCPGameClient peer, Packet.S2C.StrChannel packet)
	{
		gc.getLogger().debug("tcpgameclient " + peer.getClientAddr() + " on receive server str channel " + packet.getData());
		Robot robot = (Robot)peer;
		robot.onReceiveStrPacket(packet);
	}

	//// end handlers.
	
	GameClient gc;
	NetManager managerNet = new NetManager();
}
