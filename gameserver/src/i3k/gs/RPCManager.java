// modified by ket.kio.RPCGen at Thu Apr 20 14:32:31 CST 2017.




package i3k.gs;

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.ObjectName;

import org.apache.log4j.Logger;

import ket.util.ARC4StreamSecurity;
import ket.util.SStream;
import ket.util.Stream;
import ket.kio.ErrorCode;
import ket.kio.NetAddress;
import ket.kio.NetManager;
import ket.kio.Statistic;
import i3k.alarm.AlarmPacket;
import i3k.alarm.TCPAlarmServer;
import i3k.gtool.ActiveKeyGen;
import i3k.rpc.Packet;
import i3k.ForwardData;
import i3k.SBean;
import i3k.SBean.MapFlagInfo;
import i3k.SBean.MessageInfo;
import i3k.SBean.Vector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;
import i3k.util.OpenConnectFailCount;



public class RPCManager
{
	
	// mbean
	public interface GSNetStatMBean
	{
		int getTGSOpenCount();
		int getTGSCloseCount();
		
		int getSamplinginterval();
		int getRecvMapClientStrMsgCountPerSecond();
		int getRecvMapClientNormalMsgCountPerSecond();
		int getRecvMapServerMsgCountPerSecond();
		
		int getSessionSendPacketCountPerSecond();
		int getSessionSendTimesCountPerSecond();
		int getSessionRecvTimesCountPerSecond();
		int getSessionPacketTaskAddedPerSecond();
		int getSessionSendKBytesPerSecond();
		
		int getSessionSendPacketTotal();
		int getSessionSendTimesTotal();
		int getSessionRecvTimesTotal();
		int getSessionPacketTaskQueue();
		int getSessionSendBytes();
	}	

	public RPCManager(GameServer gs)
	{
		this.gs = gs;
		
		strChannelHandler = new StringChannelHandler(gs);
		noRoleStringPackets.add("user_login_req");
		noRoleStringPackets.add("keep_alive");
		noRoleStringPackets.add("query_loginqueue_pos");
		noRoleStringPackets.add("cancel_loginqueue");
		idipsHandler = new IDIPServiceHandler(gs);
	}
	
	public void setAllCounter(boolean reportPerTimes)
	{
		authcount.setReportPerTimes(reportPerTimes);
		auctioncount.setReportPerTimes(reportPerTimes);
		globalmapcount.setReportPerTimes(reportPerTimes);
		fightcount.setReportPerTimes(reportPerTimes);
		exchangecount.setReportPerTimes(reportPerTimes);
	}
	
	public void start()
	{
		setAllCounter(gs.getConfig().pIOFailedPerTimes == 1);
		stat.start();
		if( gs.getConfig().nIOThread == 1 )
			managerNet = new NetManager(NetManager.NetManagerType.eSelectNetManager, gs.getConfig().nIOThread);
		else
			managerNet = new NetManager(NetManager.NetManagerType.eMTSelectNetManager, gs.getConfig().nIOThread);
		
		managerNet.start();
		
		this.tgs = new TCPGameServer(this);
		this.tms = new TCPMapServer(this);
		this.udpLogger = new UDPLogger(this);
		this.idips = new TCPIDIPServer(this);
		this.taucs = new TreeMap<>();
//		this.tcc = new TCPClanClient(this);
		this.tac = new TCPAuctionClient(this);
		this.tgmc = new TCPGlobalMapClient(this);
		this.tfc = new TCPFightClient(this);
		this.tec = new TCPExchangeClient(this);
		this.talarms = new TCPAlarmServer(this.getNetManager(), gs.getConfig().addrAlarmListen, gs.getLogger());
		
		tgs.setListenAddr(gs.getConfig().addrListen, ket.kio.BindPolicy.eReuseTimewait);
		tgs.setListenBacklog(128);
		tgs.open();
		
		tms.setListenAddr(gs.getConfig().addrMapListen, ket.kio.BindPolicy.eReuseTimewait);
		tms.setListenBacklog(128);
		tms.open();
		
		idips.setListenAddr(gs.getConfig().addrIDIPListen, ket.kio.BindPolicy.eReuseTimewait);
		idips.setListenBacklog(16);
		idips.open();
		
		for (NetAddress na : gs.getConfig().addrAuths)
		{
			if (!this.taucs.containsKey(na.toString()))
			{
				TCPAuthClient tauc = new TCPAuthClient(this);
				tauc.setServerAddr(na);
				tauc.open();
				this.taucs.put(na.toString(), tauc);				
			}
		}
		
//		tcc.setServerAddr(gs.getConfig().addrClan);
//		tcc.open();
		
		tac.setServerAddr(gs.getConfig().addrAuction);
		tac.open();
		
		tgmc.setServerAddr(gs.getConfig().addrGlobalMap);
		tgmc.open();
		
		tfc.setServerAddr(gs.getConfig().addrFight);
		tfc.open();
		
		tec.setServerAddr(gs.getConfig().addrExchange);
		tec.open();
		
		udpLogger.setConnectAddr(gs.getConfig().addrTLog);
		udpLogger.open();
		
		talarms.start();
		
	}
	
	public void destroy()
	{
		if (managerNet != null)
			managerNet.destroy();
	}
	
	public void onTimer(int timeTick)
	{
		sps.set(0);
		if( gs.getConfig().pps > 0 )
		{
			for(SessionInfo e : sessions.values())
			{
				e.pps.set(0);
			}
		}
		if (managerNet != null)
			managerNet.checkIdleConnections();
		if (taucs!= null)
			for (TCPAuthClient tauc : taucs.values())
			{
				if (!tauc.isOpen())
					tauc.open();
			}
//		if (tcc!= null && !tcc.isOpen())
//			tcc.open();
		if (tac!= null && !tac.isOpen())
			tac.open();
		if (tgmc!= null && !tgmc.isOpen())
			tgmc.open();
		if (tfc!= null && !tfc.isOpen())
			tfc.open();
		if (tec!= null && !tec.isOpen())
			tec.open();
		if (talarms != null)
			talarms.onTimer();
		keepAlive(timeTick);
	}
	
	public NetManager getNetManager() { return managerNet; }
	
	public void resetTcpAuthClient(List<NetAddress> lst)
	{
		Map<String, TCPAuthClient> taucs = new TreeMap<>();
		Map<String, TCPAuthClient> curTaucs = new TreeMap<>(this.taucs);
		for (NetAddress na : lst)
		{
			TCPAuthClient tauc = curTaucs.remove(na.toString());
			if (tauc == null)
			{
				tauc = new TCPAuthClient(this);
				tauc.setServerAddr(na);
				tauc.open();
				taucs.put(na.toString(), tauc);
			}
			taucs.put(na.toString(), tauc);
		}
		for (TCPAuthClient tauc : curTaucs.values())
		{
			tauc.close();
		}
		this.taucs = taucs;
	}
	
	public void onTCPIDIPServerOpen(TCPIDIPServer peer)
	{
		gs.getLogger().info("tcpidipserver open on " + peer.getListenAddr());
	}

	public void onTCPIDIPServerOpenFailed(TCPIDIPServer peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpidipserver open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPIDIPServerClose(TCPIDIPServer peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().info("tcpidipserver close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}
	
	public void onTCPIDIPServerSessionOpen(TCPIDIPServer peer, int sessionid, NetAddress addrClient)
	{
		gs.getLogger().debug("tcpidipserver: session " + sessionid + " open, client addr " + addrClient);
	}
	
	public void onTCPIDIPServerSessionClose(TCPIDIPServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().debug("tcpidipserver: session " + sessionid + " close, errcode=" + errcode);
	}
	
		
	//// begin handlers.
	public int getTCPGameServerMaxConnectionIdleTime()
	{
		return 900 * 1000;
	}

	public void onTCPGameServerOpen(TCPGameServer peer)
	{
		gs.getLogger().info("tcpgameserver open on " + peer.getListenAddr());
	}

	public void onTCPGameServerOpenFailed(TCPGameServer peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpgameserver open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPGameServerClose(TCPGameServer peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpgameserver close on " + peer.getListenAddr() + ", errcode=" + errcode);
		if (errcode == ket.kio.ErrorCode.eError)
		{
			gs.getLogger().warn("tcpgameserver try restart ...");
			tgs.open();
		}
	}

	public void onTCPGameServerSessionOpen(TCPGameServer peer, int sessionid, NetAddress addrClient)
	{
		stat.tgsOpenCount.incrementAndGet();
		if( bDisconnectMode )
		{
			tgs.closeSession(sessionid);
			return;
		}
		if( sps.incrementAndGet() >= gs.getConfig().sps )
		{
			tgs.closeSession(sessionid);
			return;
		}
		gs.getLogger().debug("tcpgameserver: session " + sessionid + " open, client addr " + addrClient);
		//synchronized (this)
		{
			sessions.put(sessionid, new SessionInfo(addrClient));
		}
		List<Byte> keyRandList = new ArrayList<>();
		if( gs.getConfig().challengeFlag == 1 )
		{
			byte[] keyRand = GameRandom.secureRandBytes(16);
			for(byte e : keyRand)
			{
				keyRandList.add(e);
			}
			try
			{
				byte[] keyC = GameData.getChallengeKey(keyRand, gs.getConfig().challengeFuncArg.getBytes("UTF-8"));
				peer.setInputSecurity(sessionid, new ARC4StreamSecurity(keyC));
			}
			catch (UnsupportedEncodingException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//gs.getLogger().debug("tcpgameserver: send session " + sessionid + " ServerChallenge");
		tgs.sendPacket(sessionid, new Packet.S2C.ServerChallenge(0, "", gs.getConfig().challengeFlag, keyRandList));
	}

	public void onTCPGameServerSessionClose(TCPGameServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().debug("tcpgameserver: session " + sessionid + " close, errcode=" + errcode);
		gs.getLoginManager().sessionDisconnect(sessionid);
		//synchronized (this)
		{
			SessionInfo sinfo = sessions.remove(sessionid);
			if (sinfo != null && sinfo.firePPSValue > 0)
				gs.getLogger().warn("pps trigger game client session " + sessionid + " close, val=" + sinfo.firePPSValue);
		}
		stat.tgsCloseCount.incrementAndGet();
	}

	public void onTCPGameServerRecvClientResponse(TCPGameServer peer, Packet.C2S.ClientResponse packet, int sessionid)
	{
		//gs.getLogger().debug("tcpgameserver: session " + sessionid + " receve client challenge response");
		if( ! incSessionPackets(sessionid) )
			return;
		List<Byte> keyRandC = packet.getKey();
		if( keyRandC.isEmpty() || keyRandC.size() > 32 )
			return;
		byte[] keyRand = new byte[keyRandC.size()];
		for(int i = 0; i < keyRand.length; ++i)
		{
			keyRand[i] = keyRandC.get(i);
		}
		try
		{
			byte[] keyS = GameData.getChallengeKey(keyRand, gs.getConfig().challengeFuncArg.getBytes("UTF-8"));
			peer.setOutputSecurity(sessionid, new ARC4StreamSecurity(keyS));
			//gs.getLogger().debug("tcpgameserver: send session " + sessionid + " ServerResponse");
			tgs.sendPacket(sessionid, new Packet.S2C.ServerResponse(0));
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}

	public void onTCPGameServerRecvLuaChannel(TCPGameServer peer, Packet.C2S.LuaChannel packet, int sessionid)
	{
		if( ! incSessionPackets(sessionid) )
			return;
		try
		{
			//gs.getLogger().debug("receive session " + sessionid + " lua channel packet : " + packet.getData());
			String data = packet.getData();
			gs.getLogger().warn("server close client session " + sessionid + " for receive deprecated lua channel msg : " + data);
			tgs.closeSession(sessionid);
			return;
//			if (data.startsWith("map|"))
//			{
//				gs.getLogger().trace("receive session " + sessionid + " lua channel packet to map : " + data);
//			}
//			else
//			{
//				gs.getLogger().debug("receive session " + sessionid + " lua channel packet : " + data);
//				String[] msg = LuaPacket.decode(data);
//				onLuaChannelList(sessionid, msg);	
//			}
		}
		catch(Exception ex)
		{
			gs.getLogger().warn(ex.getMessage(), ex);
		}
	}

	public void onTCPGameServerRecvLuaChannel2(TCPGameServer peer, Packet.C2S.LuaChannel2 packet, int sessionid)
	{
		if( ! incSessionPackets(sessionid) )
			return;
		try
		{
			gs.getLogger().debug("receive session " + sessionid + " lua channel2 packet : " + packet.getData());
			String[] msg = new String[packet.getData().size()];
			msg = packet.getData().toArray(msg);
			gs.getLogger().warn("server close client session " + sessionid + " for receive deprecated lua channel2 msg : " + msg);
			tgs.closeSession(sessionid);
			return;
			//onLuaChannelList(sessionid, msg);
		}
		catch(Exception ex)
		{			
			gs.getLogger().warn(ex.getMessage(), ex);
		}
	}

	public void onTCPGameServerRecvStrChannel(TCPGameServer peer, Packet.C2S.StrChannel packet, int sessionid)
	{
		if( ! incSessionPackets(sessionid) )
			return;
		
		try
		{
			String data = packet.getData();
			String packetName = SStream.detectPacketName(data);
			if( packetName == null )
				return;
			
			String channelName = SBean.getStrPacketChannel(packetName);
			if( channelName == null )
				return;
			
			Role role = gs.getLoginManager().getLoginRole(sessionid);
			if (role == null && !noRoleStringPackets.contains(packetName))
			{
				if (isSessionAuthed(sessionid))
					return;
				gs.getLogger().warn("server close client session " + sessionid + " for receive unknown lua str packet : " + data);
				tgs.closeSession(sessionid);
				return;
			}
			
			if( channelName.equals("C2M") )
			{
				gs.getLogger().trace("receive session " + sessionid + " map lua str packet : " + data);
				role.onRecevieMapStrPacket(data);
				return;
			}
			
			if(role == null)
				gs.getLogger().debug("receive session " + sessionid + " lua str packet : " + packet.getData());
			else
				gs.getLogger().debug("receive session " + sessionid + " role " + role.id + " lua str packet : " + packet.getData());
			@SuppressWarnings("rawtypes")
			Class cls = SBean.getStrPacketClass(packetName);
			if( cls == null )
				return;
			
			Method mtd = StringChannelHandler.class.getMethod("onRecv_" + packetName, int.class, Role.class, SStream.IStreamable.class);
			mtd.invoke(strChannelHandler, sessionid, role, SStream.decode(data, cls));
		}
		catch(Exception ex)
		{			
			gs.getLogger().warn(ex.getMessage(), ex);
		}
	}

	public void onTCPMapServerOpen(TCPMapServer peer)
	{
		gs.getLogger().info("TCPMAPServer open on " + peer.getListenAddr());
	}

	public void onTCPMapServerOpenFailed(TCPMapServer peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("TCPMAPServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPMapServerClose(TCPMapServer peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().info("TCPMAPServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPMapServerSessionOpen(TCPMapServer peer, int sessionid, NetAddress addrClient)
	{
		gs.getLogger().info("TCPMAPServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPMapServerSessionClose(TCPMapServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().info("TCPMAPServer on session " + sessionid  + " close, errcode=" + errcode);
		gs.getMapService().mapStopWork(sessionid);
	}

	public void onTCPMapServerRecvKeepAlive(TCPMapServer peer, Packet.M2S.KeepAlive packet, int sessionid)
	{
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getLogger().debug("receive map session " + sessionid + " mapserver " + packet.getHello() + " keepalive packet");
	}

	public void onTCPMapServerRecvWhoAmI(TCPMapServer peer, Packet.M2S.WhoAmI packet, int sessionid)
	{
		gs.getLogger().info("receive map session " + sessionid + " mapserver " + packet.getServerID() + " ready to work, register serve maps: " + packet.getMaps());
		stat.recvMapServerMsgCounter.incrementAndGet();
		if (packet.getGroupID() != gs.getConfig().id)
		{
			gs.getLogger().warn("map server register group id =" + packet.getGroupID() + " invalid!");
			tms.closeSession(sessionid);
			return;
		}
		this.notifyMapSyncServerTimeOffset(sessionid, GameTime.getServerTimeOffset());
		gs.getMapService().mapStartWork(sessionid, packet.getServerID(), packet.getMaps());
		gs.getBossManager().mapStartInitBoss(sessionid, packet.getMaps());
		gs.getFlagManager().mapStartInitMapFlag(sessionid);
		gs.getSteleManager().mapStartSyncSteles(sessionid, packet.getMaps());
		gs.getJusticeManager().mapStartInitJusticeNpc(sessionid);
	}

	public void onTCPMapServerRecvLuaChannel(TCPMapServer peer, Packet.M2S.LuaChannel packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " forward role " + packet.getRoleID() + " lua channel packet : " + packet.getData());
		stat.recvMapClientStrMsgCounter.incrementAndGet();
		gs.getMapService().handleForwardLuaChannelPacket(packet.getRoleID(), packet.getData());
	}

	public void onTCPMapServerRecvStrChannel(TCPMapServer peer, Packet.M2S.StrChannel packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " forward role " + packet.getRoleID() + " str channel packet : " + packet.getData());
		stat.recvMapClientStrMsgCounter.incrementAndGet();
		gs.getMapService().handleForwardStrChannelPacket(packet.getRoleID(), packet.getData());
	}

	public void onTCPMapServerRecvStrChannelBroadcast(TCPMapServer peer, Packet.M2S.StrChannelBroadcast packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " forward roles str channel broadcast packet : " + packet.getData());
		stat.recvMapClientStrMsgCounter.incrementAndGet();
		gs.getMapService().handleForwardStrChannelBroadcastPacket(packet.getRoles(), packet.getData());
	}

	public void onTCPMapServerRecvMapRoleReady(TCPMapServer peer, Packet.M2S.MapRoleReady packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " map role " + packet.getRoleID() + " ready");
		stat.recvMapClientNormalMsgCounter.incrementAndGet();
		gs.getMapService().handleMapRoleReady(packet.getRoleID());
	}

	public void onTCPMapServerRecvNearByRoleMove(TCPMapServer peer, Packet.M2S.NearByRoleMove packet, int sessionid)
	{
//		gs.getLogger().debug("receive map session " + sessionid + " nearby role " + packet.getId() + " move");
		stat.recvMapClientNormalMsgCounter.incrementAndGet();
		gs.getMapService().handleForwardStrChannelNearByRoleMove(packet.getRids(), packet.getId(), packet.getPos(), packet.getSpeed(), packet.getRotation(), packet.getTarget(), packet.getTimeTick());
	}

	public void onTCPMapServerRecvNearByRoleStopMove(TCPMapServer peer, Packet.M2S.NearByRoleStopMove packet, int sessionid)
	{
//		gs.getLogger().trace("receive map session " + sessionid + " nearby role " + packet.getId() + " stop move");
		stat.recvMapClientNormalMsgCounter.incrementAndGet();
		gs.getMapService().handleForwardStrChannelNearByRoleStopMove(packet.getRids(), packet.getId(), packet.getPos(), packet.getSpeed(), packet.getTimeTick());
	}

	public void onTCPMapServerRecvNearByRoleEnter(TCPMapServer peer, Packet.M2S.NearByRoleEnter packet, int sessionid)
	{
//		gs.getLogger().trace("receive map session " + sessionid + " nearby role " + packet.getId() + " enter");
		stat.recvMapClientNormalMsgCounter.incrementAndGet();
		gs.getMapService().handleForwardStrChannelNearByRoleEnter(packet.getId(), packet.getRoles());
	}

	public void onTCPMapServerRecvNearByRoleLeave(TCPMapServer peer, Packet.M2S.NearByRoleLeave packet, int sessionid)
	{
//		gs.getLogger().trace("receive map session " + sessionid + " nearby role " + packet.getId() + " leave");
		stat.recvMapClientNormalMsgCounter.incrementAndGet();
		gs.getMapService().handleForwardStrChannelNearByRoleLeave(packet.getId(), packet.getRoles(), packet.getDestory());
	}

	public void onTCPMapServerRecvSyncCommonMapCopyStart(TCPMapServer peer, Packet.M2S.SyncCommonMapCopyStart packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync common map " + packet.getMapID() + " , " + packet.getMapInstance() + " start");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncCommonMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncCommonMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncCommonMapCopyEnd packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync common map " + packet.getMapID() + " , " + packet.getMapInstance() + " end");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncCommonMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getScore());
	}

	public void onTCPMapServerRecvSyncSectMapCopyStart(TCPMapServer peer, Packet.M2S.SyncSectMapCopyStart packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync sect map " + packet.getMapID() + " , " + packet.getMapInstance() + " start");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSectMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncSectMapCopyProgress(TCPMapServer peer, Packet.M2S.SyncSectMapCopyProgress packet, int sessionid)
	{
		gs.getLogger().debug(" receive map session " + sessionid + " sync sect map " + packet.getMapID() + " , " + packet.getMapInstance() + " spawn point " + packet.getSpawnPointId() + " lost hp " + packet.getHpLostBP() + " damage " + packet.getDamage());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSectMapCopyProgress(packet.getMapID(), packet.getMapInstance(), packet.getSpawnPointId(), packet.getHpLostBP(), packet.getDamage());
	}

	public void onTCPMapServerRecvSyncArenaMapCopyStart(TCPMapServer peer, Packet.M2S.SyncArenaMapCopyStart packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync arena map " + packet.getMapID() + " , " + packet.getMapInstance() + " start");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncArenaMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncArenaMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncArenaMapCopyEnd packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync arena map " + packet.getMapID() + " , " + packet.getMapInstance() + " end result " + packet.getWin());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncArenaMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getWin(), packet.getAttackingSideHp(), packet.getDefendingSideHp());
	}
	
	public void onTCPMapServerRecvSyncLocation(TCPMapServer peer, Packet.M2S.SyncLocation packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync role " + packet.getRoleID() + " at map["+ packet.getMapID()  + ", " + packet.getMapInstance() +"] location " + GameData.toString(packet.getLocation().position));
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncLocation(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getLocation());
	}

	public void onTCPMapServerRecvSyncHp(TCPMapServer peer, Packet.M2S.SyncHp packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync role " + packet.getRoleID() + " at map["+ packet.getMapID()  + ", " + packet.getMapInstance() +"] hp : " + packet.getHp() + ", " + packet.getHpMax());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncHp(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getHp(), packet.getHpMax());
	}

	public void onTCPMapServerRecvAddDrops(TCPMapServer peer, Packet.M2S.AddDrops packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " add role " + packet.getRoleID() + " drops size: " + packet.getDrops());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleAddDrops(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getDrops());
	}

	public void onTCPMapServerRecvAddKill(TCPMapServer peer, Packet.M2S.AddKill packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync role " + packet.getRoleID() + " kill : " + packet.getTargetType() + ", " + packet.getTargetID());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleAddKill(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getTargetType(), packet.getTargetID(), packet.getWeaponAdd(), packet.getKillRole());
	}

	public void onTCPMapServerRecvSyncDurability(TCPMapServer peer, Packet.M2S.SyncDurability packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync role " + packet.getRoleID() + " wid: " + packet.getWid() + " durability: " + packet.getDurability());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncDurability(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getWid(), packet.getDurability());
	}

	public void onTCPMapServerRecvSyncEndMine(TCPMapServer peer, Packet.M2S.SyncEndMine packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync role " + packet.getRoleID() + " end mine " + packet.getMineId() + ", " + packet.getMineInstance() +  (packet.getOk() > 0 ? " success" : " failed"));
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncEndMine(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getMineId(), packet.getMineInstance(), packet.getOk() > 0);
	}

	public void onTCPMapServerRecvAddPKValue(TCPMapServer peer, Packet.M2S.AddPKValue packet, int sessionid)
	{
		gs.getLogger().trace(" receive map session " + sessionid + " add role " + packet.getRoleID() + " pk value : " + packet.getValue());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleAddPKValue(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getValue());
	}

	public void onTCPMapServerRecvSyncWorldMapBossProgress(TCPMapServer peer, Packet.M2S.SyncWorldMapBossProgress packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " SyncWorldMapBossProgress " + packet.getBossID() + " , " + packet.getHp() + " , " + packet.getKillerName()+ " , " +packet.getKillerId());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncWorldMapBossProgress(packet.getBossID(), packet.getHp(), packet.getKillerName(),packet.getKillerId());
	}

	public void onTCPMapServerRecvSyncBWArenaMapCopyStart(TCPMapServer peer, Packet.M2S.SyncBWArenaMapCopyStart packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " SyncBWArenaMapCopyStart " + packet.getMapID() + " , " + packet.getMapInstance());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncBWArenaMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncBWArenaMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncBWArenaMapCopyEnd packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " SyncBWArenaMapCopyEnd " + packet.getMapID() + " , " + packet.getMapInstance() + " win " + packet.getWin());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncBWArenaMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getWin(), packet.getAttackingSideHp(), packet.getDefendingSideHp());
	}

	public void onTCPMapServerRecvSyncPetLifeMapCopyStart(TCPMapServer peer, Packet.M2S.SyncPetLifeMapCopyStart packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " SyncPetLifeMapCopyStart " + packet.getMapID() + " , " + packet.getMapInstance());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncPetLifeMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncCurRideHorse(TCPMapServer peer, Packet.M2S.SyncCurRideHorse packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync cur ride horse " + packet.getHid());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncCurRideHorse(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getHid());
	}

	public void onTCPMapServerRecvSyncCarLocation(TCPMapServer peer, Packet.M2S.SyncCarLocation packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync car " + packet.getRoleID() + " at map["+ packet.getMapID()  + ", " + packet.getMapInstance() + "]location " + GameData.toString(packet.getLocation().position));
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncCarLocation(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getLocation());
	}

	public void onTCPMapServerRecvSyncCarHp(TCPMapServer peer, Packet.M2S.SyncCarHp packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync car " + packet.getRoleID() + " at map["+ packet.getMapID()  + ", " + packet.getMapInstance() + "] curHP " + packet.getHp());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncCarCurHP(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getHp());
	}

	public void onTCPMapServerRecvUpdateCarDamage(TCPMapServer peer, Packet.M2S.UpdateCarDamage packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync car " + packet.getRoleID() + " at map["+ packet.getMapID()  + ", " + packet.getMapInstance() + "] damageRole " + packet.getDamageRole() + " damage " + packet.getDamage());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleUpdateCarDamageRole(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getDamageRole(), packet.getDamage());
	}

	public void onTCPMapServerRecvSyncRoleRobSuccess(TCPMapServer peer, Packet.M2S.SyncRoleRobSuccess packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync role " + packet.getRoleID() + " rob car " + packet.getCarID() + " success");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncRoleRobSuccess(packet.getRoleID(), packet.getCarID());
	}

	public void onTCPMapServerRecvUpdateRoleCarRobber(TCPMapServer peer, Packet.M2S.UpdateRoleCarRobber packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " role " + packet.getRoleID() + " update car robber");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleUpdateRoleCarRobber(packet.getRoleID());
	}

	public void onTCPMapServerRecvKickRoleFromMap(TCPMapServer peer, Packet.M2S.KickRoleFromMap packet, int sessionid)
	{
		gs.getLogger().info("receive map session " + sessionid + " kick role " + packet.getRoleID() + " from map");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleKickRoleFromMap(packet.getRoleID());
	}

	public void onTCPMapServerRecvRoleUseItemSkillSuc(TCPMapServer peer, Packet.M2S.RoleUseItemSkillSuc packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " role " + packet.getRoleID() + " item " + packet.getItemID() + " skill suc " + packet.getOk());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleRoleUseItemSkill(packet.getRoleID(), packet.getItemID(), packet.getOk());
	}
	
	public void onTCPMapServerRecvUpdateRoleFightState(TCPMapServer peer, Packet.M2S.UpdateRoleFightState packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " role " + packet.getRoleID() + " update fight state " + packet.getFightState());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleRoleUpdateFightState(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getFightState() == 1);
	}

	public void onTCPMapServerRecvSyncRolePetHp(TCPMapServer peer, Packet.M2S.SyncRolePetHp packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " role " + packet.getRoleID() + " sync pet " + packet.getPetID() + " hp");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleRoleSyncPetHp(packet.getRoleID(), packet.getPetID(), packet.getMapID(), packet.getMapInstance(), packet.getHpState());
	}

	public void onTCPMapServerRecvSyncRoleSp(TCPMapServer peer, Packet.M2S.SyncRoleSp packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync role " + packet.getRoleID() + " sp " + packet.getSp());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSp(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getSp());
	}

	public void onTCPMapServerRecvSyncWorldMapBossRecord(TCPMapServer peer, Packet.M2S.SyncWorldMapBossRecord packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync world boss " + packet.getBossID() + " record");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleWorldBossRecord(packet.getBossID(), packet.getRecord());
	}

	public void onTCPMapServerRecvSyncArmorVal(TCPMapServer peer, Packet.M2S.SyncArmorVal packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync role " + packet.getRoleID() + " armor val " + packet.getArmorVal() + " , " + packet.getArmorValMax());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncArmorVal(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getArmorVal(), packet.getArmorValMax());
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyStatus(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyStatus packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync sect group map status mapId " +packet.getMapID() +", instance "+packet.getMapInstance()+ ", process : " + packet.getProgress());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSectGroupMapCopyStatus(packet.getMapID(), packet.getMapInstance(), packet.getProgress());
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyResult(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyResult packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync sect group map end mapId " +packet.getMapID() +", instance "+packet.getMapInstance()+ ", process : " + packet.getProgress());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSectGroupMapCopyResult(packet.getMapID(), packet.getMapInstance(), packet.getProgress());
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyStart(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyStart packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync sect group map start mapId " +packet.getMapID() +", instance "+packet.getMapInstance());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSectGroupMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyProgress(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyProgress packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync sect group map progress mapId " +packet.getMapID() +", instance "+packet.getMapInstance());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSectGroupMapCopyProgress(packet.getMapID(), packet.getMapInstance(), packet.getSpawnPointId(), packet.getRoleId(), packet.getMonsterId(), packet.getHpLostBP(), packet.getDamage());
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyAddKill(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyAddKill packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync sect group map add kill mapId " +packet.getMapID() +", instance "+packet.getMapInstance());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSectGroupMapCopyAddKill(packet.getMapID(), packet.getMapInstance(), packet.getMonsterId(), packet.getSpawnPointId());
	}

	public void onTCPMapServerRecvSyncMapFlagCanTake(TCPMapServer peer, Packet.M2S.SyncMapFlagCanTake packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync map " +packet.getMapID() +" flag can take");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncMapFlagCanTake(packet.getMapID());
	}

	public void onTCPMapServerRecvSyncWeaponMaster(TCPMapServer peer, Packet.M2S.SyncWeaponMaster packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync role " + packet.getRoleID() + " weapon " + packet.getWeaponID() + " master");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncRoleWeaponMaster(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getWeaponID());
	}

	public void onTCPMapServerRecvRolePickUpDrops(TCPMapServer peer, Packet.M2S.RolePickUpDrops packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync role " + packet.getRoleID() + " pick up drops ");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handlePickUpDrops(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getDrops());
	}

	public void onTCPMapServerRecvSyncSuperMonster(TCPMapServer peer, Packet.M2S.SyncSuperMonster packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync world map[" + packet.getMonster().mapID + " ," + packet.getMonster().mapLine + "]super monster " + packet.getMonster().id + " state " + packet.getAdd());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSuperMonster(packet.getMonster(), packet.getAdd() == 1);
	}

	public void onTCPMapServerRecvSyncWorldMineral(TCPMapServer peer, Packet.M2S.SyncWorldMineral packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync world map[" + packet.getMineral().mapID + " ," + packet.getMineral().mapLine + "]mineral " + packet.getMineral().id + " state " + packet.getAdd());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncWorldMineral(packet.getMineral(), packet.getAdd() == 1);
	}

	public void onTCPMapServerRecvSyncMarriageParadeEnd(TCPMapServer peer, Packet.M2S.SyncMarriageParadeEnd packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync man " + packet.getManID() + " woman " + packet.getWomanID() + " marriage end");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncRoleMarriageParadeEnd(packet.getManID(), packet.getWomanID());
	}

	public void onTCPMapServerRecvRolePickUpRareDrops(TCPMapServer peer, Packet.M2S.RolePickUpRareDrops packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync role " + packet.getRoleID() + " pick up rare drops ");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handlePickUpRareDrops(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getDropId(), packet.getDrop(), packet.getMonsterId());
	}

	public void onTCPMapServerRecvSyncWorldBossDamageRoles(TCPMapServer peer, Packet.M2S.SyncWorldBossDamageRoles packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync world " + packet.getBossID() + " damage roles ");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleWorldBossDamageRoles(packet.getBossID(), packet.getKiller(), packet.getDamageRoles());
	}

	public void onTCPMapServerRecvSyncSteleRemainTimes(TCPMapServer peer, Packet.M2S.SyncSteleRemainTimes packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync stele [" + packet.getSteleType() + ", " + packet.getIndex() + "] remainTimes " + packet.getRemainTimes());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncSteleRemainTimes(packet.getSteleType(), packet.getIndex(), packet.getRemainTimes());
	}

	public void onTCPMapServerRecvSyncRoleAddSteleCard(TCPMapServer peer, Packet.M2S.SyncRoleAddSteleCard packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync role " + packet.getRoleID() + " add stele card " + packet.getAddCards() + " add type " + packet.getAddType());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncRoleAddSteleCards(packet.getRoleID(), packet.getAddCards(), packet.getAddType());
	}

	public void onTCPMapServerRecvSyncRefreshSteleMonster(TCPMapServer peer, Packet.M2S.SyncRefreshSteleMonster packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync role " + packet.getRoleID() + " refresh stele monster");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncRefreshSteleMonster(packet.getRoleID(), packet.getRoleName(), packet.getMapID(), packet.getMapLine(), packet.getSteleType(), packet.getIndex(), packet.getMonsterID());
	}

	public void onTCPMapServerRecvSyncEmergencyMapStart(TCPMapServer peer, Packet.M2S.SyncEmergencyMapStart packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync emergency start mapId " +packet.getMapID() +", instance "+packet.getInstanceID());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncEmergencyMapCopyStart(packet.getMapID(), packet.getInstanceID());
	}

	public void onTCPMapServerRecvSyncEmergencyMapKillMonster(TCPMapServer peer, Packet.M2S.SyncEmergencyMapKillMonster packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync emergency role " + packet.getRoles() + " kill " + packet.getMonsterID());
		stat.recvMapServerMsgCounter.incrementAndGet();
		for (int roleId : packet.getRoles())
		{
			Role role = gs.getLoginManager().getOnGameRole(roleId);
			if (role != null)
			{
				role.addEmergencyKill(packet.getRoles().size(), packet.getMonsterID());
			}
		}
	}

	public void onTCPMapServerRecvSyncEmergencyMapEnd(TCPMapServer peer, Packet.M2S.SyncEmergencyMapEnd packet, int sessionid)
	{
		gs.getLogger().trace("receive map session " + sessionid + " sync emergency end mapId " +packet.getMapID() +", instance "+packet.getInstanceID());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncEmergencyMapCopyEnd(packet.getMapID(), packet.getInstanceID());
	}

	public void onTCPMapServerRecvSyncFightNpcMapStart(TCPMapServer peer, Packet.M2S.SyncFightNpcMapStart packet, int sessionid)
	{
		gs.getLogger().info("receive map session " + sessionid + " sync fight npc map copy [" + packet.getMapID() +", " + packet.getMapInstance() + "] start");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncFightNpcMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncFightNpcMapEnd(TCPMapServer peer, Packet.M2S.SyncFightNpcMapEnd packet, int sessionid)
	{
		gs.getLogger().info("receive map session " + sessionid + " sync fight npc map copy [" + packet.getMapID() +", " + packet.getMapInstance() + "] end win " + packet.getWin());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncFightNpcMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getWin() == 1);
	}

	public void onTCPMapServerRecvSyncTowerDefenceMapStart(TCPMapServer peer, Packet.M2S.SyncTowerDefenceMapStart packet, int sessionid)
	{
		gs.getLogger().info("receive map session " + sessionid + " sync tower defence mapcopy [" + packet.getMapID() +", " + packet.getMapInstance() + "] start");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncTowerDefenceMapCopyStart(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPMapServerRecvSyncTowerDefenceMapEnd(TCPMapServer peer, Packet.M2S.SyncTowerDefenceMapEnd packet, int sessionid)
	{
		gs.getLogger().info("receive map session " + sessionid + " sync tower defence map copy [" + packet.getMapID() +", " + packet.getMapInstance() + "] end result " + packet.getCount());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncTowerDefenceMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getCount());
	}

	public void onTCPMapServerRecvSyncTowerDefenceSpawnCount(TCPMapServer peer, Packet.M2S.SyncTowerDefenceSpawnCount packet, int sessionid)
	{
		gs.getLogger().info("receive map session " + sessionid + " sync tower defence map copy [" + packet.getMapID() +", " + packet.getMapInstance() + "] spawn count " + packet.getCount());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncTowerDefenceSpawnCount(packet.getMapID(), packet.getMapInstance(), packet.getCount());
	}

	public void onTCPMapServerRecvSyncTowerDefenceScore(TCPMapServer peer, Packet.M2S.SyncTowerDefenceScore packet, int sessionid)
	{
		gs.getLogger().debug("receive map session " + sessionid + " sync role " + packet.getRoleID() + " tower defence map copy [" + packet.getMapID() +", " + packet.getMapInstance() + "] score monster " + packet.getMonsterID());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncTowerDefenceScore(packet.getMapID(), packet.getMapInstance(), packet.getRoleID(), packet.getMonsterID());
	}

	public void onTCPAuctionClientOpen(TCPAuctionClient peer)
	{
		gs.getLogger().info("tcpauctionclient open connect to " + peer.getServerAddr());
		peer.sendPacket(new Packet.S2Auction.WhoAmI(gs.getConfig().id, gs.getConfig().zones));
		notifyAuctionReportServerTimeOffset(GameTime.getServerTimeOffset());
		gs.getGameConf().syncAuctionGroupBuyLog();
		auctioncount.resetCount();
	}


	public void onTCPAuctionClientOpenFailed(TCPAuctionClient peer, ket.kio.ErrorCode errcode)
	{
		if (auctioncount.increaseCount())
			gs.getLogger().warn("tcpauctionclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPAuctionClientClose(TCPAuctionClient peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpauctionclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
	}

	public void onTCPAuctionClientRecvKeepAlive(TCPAuctionClient peer, Packet.Auction2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvAdjustTimeOffset(TCPAuctionClient peer, Packet.Auction2S.AdjustTimeOffset packet)
	{
		gs.getLogger().info("receive auction adjust time offset " + packet.getTimeOffset() + ", current time offset " + GameTime.getServerTimeOffset());
		if (packet.getTimeOffset() > GameTime.getServerTimeOffset())
		{
			gs.getMapService().syncAllMapsTimeOffset(packet.getTimeOffset());
			GameTime.setServerTimeOffset(packet.getTimeOffset());
		}
	}

	public void onTCPAuctionClientRecvPutOnItemRes(TCPAuctionClient peer, Packet.Auction2S.PutOnItemRes packet)
	{
		gs.getLogger().debug("gs receive auction PutOnItemRes");
		gs.getAuctionService().handlePutOnItemTaskResponse(packet.getTagID(), packet.getCid());
	}

	public void onTCPAuctionClientRecvTimeOutPutOffItemsReq(TCPAuctionClient peer, Packet.Auction2S.TimeOutPutOffItemsReq packet)
	{
		gs.getLogger().debug("gs receive auction role " + packet.getRoleID() + " TimeOutPutOffItemsReq " + packet.getCid());
		gs.getAuctionService().handleTimeOutPutOffItemsReq(packet.getTagID(), packet.getRoleID(), packet.getCid(), packet.getItems());
	}

	public void onTCPAuctionClientRecvPutOffItemsRes(TCPAuctionClient peer, Packet.Auction2S.PutOffItemsRes packet)
	{
		gs.getLogger().debug("gs receive auction put off items res " + packet.getItems() != null);
		gs.getAuctionService().handlePutOffItemsResponse(packet.getTagID(), packet.getErrCode(), packet.getItems());
	}

	public void onTCPAuctionClientRecvBuyItemsRes(TCPAuctionClient peer, Packet.Auction2S.BuyItemsRes packet)
	{
		gs.getLogger().debug("receive auction buy items res " + packet.getItems() != null);
		gs.getAuctionService().handleBuyItemsResponse(packet.getTagID(), packet.getItems());
	}

	public void onTCPAuctionClientRecvCheckCanBuyReq(TCPAuctionClient peer, Packet.Auction2S.CheckCanBuyReq packet)
	{
		gs.getLogger().debug("receive auction check seller " + packet.getSellerID() + "'s items cid " + packet.getCid() + " can buy");
		gs.getAuctionService().handleCheckCanBuy(packet.getTagID(), packet.getSellerID(), packet.getCid(), packet.getItems());
	}

	public void onTCPAuctionClientRecvAuctionItemsSyncRes(TCPAuctionClient peer, Packet.Auction2S.AuctionItemsSyncRes packet)
	{
		gs.getLogger().debug("receive auction items sync res success " + packet.getItems() == null ? false : true + " lastPage " + packet.getLastPage());
		gs.getAuctionService().handleAuctionItemsSyncRes(packet.getTagID(), packet.getItems(), packet.getLastPage());
	}

	public void onTCPAuctionClientRecvSelfItemsSyncRes(TCPAuctionClient peer, Packet.Auction2S.SelfItemsSyncRes packet)
	{
		gs.getLogger().debug("receive auction sync self items res success " + packet.getItems() != null);
		gs.getAuctionService().handleSelfItemsSyncResponse(packet.getTagID(), packet.getItems());
	}
	
	public void onTCPAuctionClientRecvItemPricesSyncRes(TCPAuctionClient peer, Packet.Auction2S.ItemPricesSyncRes packet)
	{
		gs.getLogger().debug("receive auction item prices sync response " + packet.getItems().size());
		gs.getAuctionService().handleItemPricesSyncResponse(packet.getTagID(), packet.getItems());
	}

	public void onTCPAuctionClientRecvSyncGroupBuyLog(TCPAuctionClient peer, Packet.Auction2S.SyncGroupBuyLog packet)
	{
		gs.getLogger().debug("receive auction sync group buy activity " + packet.getActivityID() + " buy log");
		gs.getGameConf().handleSyncGroupBuyLog(packet.getActivityID(), packet.getLog());
	}

	public void onTCPAuthClientOpen(TCPAuthClient peer)
	{
		gs.getLogger().info("tcpauthclient open connect to " + peer.getServerAddr());
		peer.sendPacket(new Packet.S2AU.WhoAmI(gs.getConfig().id, gs.getConfig().zones));
	}

	public void onTCPAuthClientOpenFailed(TCPAuthClient peer, ket.kio.ErrorCode errcode)
	{
		if (authcount.increaseCount())
			gs.getLogger().warn("tcpauthclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPAuthClientClose(TCPAuthClient peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpauthclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
	}

	public void onTCPAuthClientRecvKeepAlive(TCPAuthClient peer, Packet.AU2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPAuthClientRecvPayReq(TCPAuthClient peer, Packet.AU2S.PayReq packet)
	{
		gs.getLogger().info("receive auth pay req : xid=" + packet.getXid() + ", channel=" + packet.getChannel() + ", uid=" + packet.getUid() + ", gsid=" + packet.getGsid() + ", roleid=" + packet.getRoleId() + ", goodsid=" + packet.getGoodsId() + ", paylevel=" + packet.getPayLevel() + ", payext=" + packet.getPayext() + ", orderid=" + packet.getOrderId());
		gs.getLoginManager().finishPay(packet.getXid(), packet.getChannel(), packet.getUid(), packet.getGsid(), packet.getRoleId(), packet.getGoodsId(), packet.getPayLevel(), packet.getPayext(), packet.getOrderId(), (errCode) -> {
			peer.sendPacket(new Packet.S2AU.PayRes(packet.getXid(), errCode));
			gs.getLogger().info("handle auth pay req : xid=" + packet.getXid() + ", errcode=" + errCode);
		});
	}

	public void onTCPGlobalMapClientOpen(TCPGlobalMapClient peer)
	{
		gs.getLogger().info("tcpglobalmapclient open connect to " + peer.getServerAddr());
		peer.sendPacket(new Packet.S2GM.WhoAmI(gs.getConfig().id, gs.getConfig().zones));
		notifyGlobalMapReportServerTimeOffset(GameTime.getServerTimeOffset());
		globalmapcount.resetCount();
	}

	public void onTCPGlobalMapClientOpenFailed(TCPGlobalMapClient peer, ket.kio.ErrorCode errcode)
	{
		if (globalmapcount.increaseCount())
			gs.getLogger().warn("tcpglobalmapclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPGlobalMapClientClose(TCPGlobalMapClient peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpglobalmapclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
		gs.getMapService().mapStopWork(0);
	}

	public void onTCPGlobalMapClientRecvKeepAlive(TCPGlobalMapClient peer, Packet.GM2S.KeepAlive packet)
	{
		
	}

	public void onTCPGlobalMapClientRecvSyncGlobalMaps(TCPGlobalMapClient peer, Packet.GM2S.SyncGlobalMaps packet)
	{
		gs.getLogger().info("receive global mapserver register serve maps: " + packet.getMaps());
		gs.getMapService().mapStartWork(0, packet.getServerID(), packet.getMaps());
	}

	public void onTCPGlobalMapClientRecvLuaChannel(TCPGlobalMapClient peer, Packet.GM2S.LuaChannel packet)
	{
		
	}

	public void onTCPGlobalMapClientRecvStrChannel(TCPGlobalMapClient peer, Packet.GM2S.StrChannel packet)
	{
		gs.getLogger().trace("receive global map server forward role " + packet.getRoleID() + " str channel packet : " + packet.getData());
		gs.getMapService().handleForwardStrChannelPacket(packet.getRoleID(), packet.getData());
	}

	public void onTCPGlobalMapClientRecvMapRoleReady(TCPGlobalMapClient peer, Packet.GM2S.MapRoleReady packet)
	{
		gs.getLogger().debug("receive global map server map role " + packet.getRoleID() + " ready");
		gs.getMapService().handleMapRoleReady(packet.getRoleID());
	}

	public void onTCPGlobalMapClientRecvSyncLocation(TCPGlobalMapClient peer, Packet.GM2S.SyncLocation packet)
	{
		gs.getLogger().trace("receive global map sync role " + packet.getRoleID() + " at map["+ packet.getMapID()  + ", " + packet.getMapInstance() +"] location " + GameData.toString(packet.getLocation().position));
		gs.getMapService().handleSyncLocation(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getLocation());
	}

	public void onTCPGlobalMapClientRecvSyncHp(TCPGlobalMapClient peer, Packet.GM2S.SyncHp packet)
	{
		gs.getLogger().trace("receive global map sync role " + packet.getRoleID() + " at map["+ packet.getMapID()  + ", " + packet.getMapInstance() +"] hp : " + packet.getHp() + ", " + packet.getHpMax());
		gs.getMapService().handleSyncHp(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getHp(), packet.getHpMax());
	}

	public void onTCPGlobalMapClientRecvAddDrops(TCPGlobalMapClient peer, Packet.GM2S.AddDrops packet)
	{
		gs.getLogger().trace("receive global map add role " + packet.getRoleID() + " drops size: " + packet.getDrops());
		gs.getMapService().handleAddDrops(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getDrops());
	}

	public void onTCPGlobalMapClientRecvAddKill(TCPGlobalMapClient peer, Packet.GM2S.AddKill packet)
	{
		gs.getLogger().trace("receive global map sync role " + packet.getRoleID() + " kill : " + packet.getTargetType() + ", " + packet.getTargetID());
		gs.getMapService().handleAddKill(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getTargetType(), packet.getTargetID(), packet.getWeaponAdd(), packet.getKillRole());
	}

	public void onTCPGlobalMapClientRecvSyncDurability(TCPGlobalMapClient peer, Packet.GM2S.SyncDurability packet)
	{
		gs.getLogger().trace("receive global map sync role " + packet.getRoleID() + " wid: " + packet.getWid() + " durability: " + packet.getDurability());
		gs.getMapService().handleSyncDurability(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getWid(), packet.getDurability());
	}

	public void onTCPGlobalMapClientRecvSyncEndMine(TCPGlobalMapClient peer, Packet.GM2S.SyncEndMine packet)
	{
		gs.getLogger().trace("receive global map sync role " + packet.getRoleID() + " end mine " + packet.getMineId() + ", " + packet.getMineInstance() +  (packet.getOk() > 0 ? " success" : " failed"));
		gs.getMapService().handleSyncEndMine(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getMineId(), packet.getMineInstance(), packet.getOk() > 0);
	}

	public void onTCPGlobalMapClientRecvKickRoleFromMap(TCPGlobalMapClient peer, Packet.GM2S.KickRoleFromMap packet)
	{
		gs.getLogger().info("receive global map kick role " + packet.getRoleID() + " from map");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleKickRoleFromMap(packet.getRoleID());
	}

	public void onTCPGlobalMapClientRecvRoleUseItemSkillSuc(TCPGlobalMapClient peer, Packet.GM2S.RoleUseItemSkillSuc packet)
	{
		gs.getLogger().debug("receive global map role " + packet.getRoleID() + " item " + packet.getItemID() + " skill suc " + packet.getOk());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleRoleUseItemSkill(packet.getRoleID(), packet.getItemID(), packet.getOk());
	}

	public void onTCPGlobalMapClientRecvUpdateRoleFightState(TCPGlobalMapClient peer, Packet.GM2S.UpdateRoleFightState packet)
	{
		gs.getLogger().trace("receive global map role " + packet.getRoleID() + " update fight state " + packet.getFightState());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleRoleUpdateFightState(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getFightState() == 1);
	}

	public void onTCPGlobalMapClientRecvSyncRolePetHp(TCPGlobalMapClient peer, Packet.GM2S.SyncRolePetHp packet)
	{
		gs.getLogger().trace("receive global map role " + packet.getRoleID() + " sync pet " + packet.getPetID() + " hp");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleRoleSyncPetHp(packet.getRoleID(), packet.getPetID(), packet.getMapID(), packet.getMapInstance(), packet.getHpState());
	}

	public void onTCPGlobalMapClientRecvSyncArmorVal(TCPGlobalMapClient peer, Packet.GM2S.SyncArmorVal packet)
	{
		gs.getLogger().trace("receive global map sync role " + packet.getRoleID() + " armor val " + packet.getArmorVal() + " , " + packet.getArmorValMax());
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncArmorVal(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getArmorVal(), packet.getArmorValMax());
	}

	public void onTCPGlobalMapClientRecvSyncWeaponMaster(TCPGlobalMapClient peer, Packet.GM2S.SyncWeaponMaster packet)
	{
		gs.getLogger().trace("receive global map sync role " + packet.getRoleID() + " weapon " + packet.getWeaponID() + " master");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handleSyncRoleWeaponMaster(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getWeaponID());
	}

	public void onTCPGlobalMapClientRecvRolePickUpDrops(TCPGlobalMapClient peer, Packet.GM2S.RolePickUpDrops packet)
	{
		gs.getLogger().debug("receive global map sync role " + packet.getRoleID() + " pick up drops ");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handlePickUpDrops(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getDrops());
	}

	public void onTCPGlobalMapClientRecvRolePickUpRareDrops(TCPGlobalMapClient peer, Packet.GM2S.RolePickUpRareDrops packet)
	{
		gs.getLogger().debug("receive global map " + " sync role " + packet.getRoleID() + " pick up rare drops ");
		stat.recvMapServerMsgCounter.incrementAndGet();
		gs.getMapService().handlePickUpRareDrops(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getDropId(), packet.getDrop(), packet.getMonsterId());
	}

	public void onTCPFightClientOpen(TCPFightClient peer)
	{
		gs.getLogger().info("tcpfightclient open connect to " + peer.getServerAddr());
		peer.sendPacket(new Packet.S2F.WhoAmI(gs.getConfig().id, gs.getConfig().zones));
		notifyFightReportServerTimeOffset(GameTime.getServerTimeOffset());
		fightcount.resetCount();
	}

	public void onTCPFightClientOpenFailed(TCPFightClient peer, ket.kio.ErrorCode errcode)
	{
		if (fightcount.increaseCount())
			gs.getLogger().warn("tcpfightclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPFightClientClose(TCPFightClient peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpfightclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
		gs.getMapService().clearGlobalMapRoles();
		gs.getFightService().clearForceWarJoinRoles();
		gs.getFightService().clearSuperArenaJoinRoles();
		gs.getArenaRoomManager().clearReadyRoom();
	}

	public void onTCPFightClientRecvKeepAlive(TCPFightClient peer, Packet.F2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncGSRankStart(TCPFightClient peer, Packet.F2S.SyncGSRankStart packet)
	{
		gs.getLogger().info("receive fs sync rank " + packet.getRankID() + " , " + packet.getSnapshotCreateTime() + " start");
		gs.getFightService().handleSyncGSRankStart(packet.getRankID(), packet.getSnapshotCreateTime());
	}

	public void onTCPFightClientRecvSyncGSRank(TCPFightClient peer, Packet.F2S.SyncGSRank packet)
	{
		gs.getLogger().info("receive fs sync rank " + packet.getRankID() + " batch size " + packet.getBatch().size());
		gs.getFightService().handleSyncGSRank(packet.getRankID(), packet.getBatch());
	}

	public void onTCPFightClientRecvSyncGSRankEnd(TCPFightClient peer, Packet.F2S.SyncGSRankEnd packet)
	{
		gs.getLogger().info("receive fs sync rank " + packet.getRankID() + " end !");
		gs.getFightService().handleSyncGSRankEnd(packet.getRankID());
	}
	
	public void onTCPFightClientRecvRoleJoinForceWarRes(TCPFightClient peer, Packet.F2S.RoleJoinForceWarRes packet)
	{
		gs.getLogger().trace("receive fs role join force war responce " + packet.getOk());
		gs.getFightService().handleRoleJoinForceWarRes(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvRoleQuitForceWarRes(TCPFightClient peer, Packet.F2S.RoleQuitForceWarRes packet)
	{
		gs.getLogger().trace("receive fs role quit force war responce " + packet.getOk());
		gs.getFightService().handleRoleQuitForceWarRes(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvRoleEnterForceWar(TCPFightClient peer, Packet.F2S.RoleEnterForceWar packet)
	{
		gs.getLogger().debug("receive fs role " + packet.getRoleID() + " enter force war map[" + packet.getMapID() + " , " + packet.getMapInstance() + "]");
		gs.getFightService().handleRoleEnterForceWarMap(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getMainSpawn() == 1);
	}

	public void onTCPFightClientRecvSyncForceWarMapStart(TCPFightClient peer, Packet.F2S.SyncForceWarMapStart packet)
	{
		gs.getLogger().info("receive fs sync force war map [" + packet.getMapID() + " , " + packet.getMapInstance() + "] start");
		gs.getFightService().handleStartForceWarMap(packet.getMapID(), packet.getMapInstance());
	}
	
	public void onTCPFightClientRecvSyncForceWarMapEnd(TCPFightClient peer, Packet.F2S.SyncForceWarMapEnd packet)
	{
		gs.getLogger().info("receive fs sync force war map[" + packet.getMapID() + " , " + packet.getMapInstance() + "] end winSide " + packet.getWinSide() + " whiteSideScore " + packet.getWhiteScore() + " blackSideScore " + packet.getBlackScore());
		gs.getFightService().handleSyncForceWarMapEnd(packet.getMapID(), packet.getMapInstance(), packet.getRankClearTime(), packet.getWinSide(), packet.getKilledBoss(), packet.getWhiteScore(), packet.getBlackScore(), packet.getWhiteSide(), packet.getBlackSide());
	}

	public void onTCPFightClientRecvSyncMapCopyTimeOut(TCPFightClient peer, Packet.F2S.SyncMapCopyTimeOut packet)
	{
		gs.getLogger().info("receive fs sync map copy[" + packet.getMapID() + " , " + packet.getMapInstance() + "] time out");
		gs.getFightService().handleSyncMapCopyTimeOut(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPFightClientRecvReceiveMsgFight(TCPFightClient peer, Packet.F2S.ReceiveMsgFight packet)
	{
		gs.getLogger().trace("receive fightServer role send message");
		Role receiveRole = gs.getLoginManager().getOnGameRole(packet.getRoleId());
		if (receiveRole != null)
			receiveRole.receiveMsg(packet.getMsgContent());
	}

	public void onTCPFightClientRecvSingleJoinSuperArenaRes(TCPFightClient peer, Packet.F2S.SingleJoinSuperArenaRes packet)
	{
		gs.getLogger().debug("receive fs single join super arena res " + packet.getOk());
		gs.getFightService().handleSingleJoinSuperArena(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvSingleQuitSuperArenaRes(TCPFightClient peer, Packet.F2S.SingleQuitSuperArenaRes packet)
	{
		gs.getLogger().debug("receive fs single quit super arena res " + packet.getOk());
		gs.getFightService().handleSingleQuitSuperArena(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvTeamJoinSuperArenaRes(TCPFightClient peer, Packet.F2S.TeamJoinSuperArenaRes packet)
	{
		gs.getLogger().debug("receive fs team join super arena res " + packet.getOk());
		gs.getFightService().handleTeamJoinSuperArena(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvTeamQuitSuperArenaRes(TCPFightClient peer, Packet.F2S.TeamQuitSuperArenaRes packet)
	{
		gs.getLogger().debug("receive fs team quit super arena res " + packet.getOk());
		gs.getFightService().handleTeamQuitSuperArena(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvQueryTeamMembersRes(TCPFightClient peer, Packet.F2S.QueryTeamMembersRes packet)
	{
		gs.getLogger().trace("receive fs query team member res");
		gs.getFightService().handleQueryTeamMembers(packet.getTagID(), packet.getOverviews());
	}

	public void onTCPFightClientRecvCreateMapCopy(TCPFightClient peer, Packet.F2S.CreateMapCopy packet)
	{
		gs.getLogger().info("receive fs create map copy " + packet.getMapID() + " , " + packet.getMapInstance());
		gs.getFightService().handleCreateFightMapCopy(packet.getMapID(), packet.getMapInstance());
	}

	public void onTCPFightClientRecvRoleEnterSuperArena(TCPFightClient peer, Packet.F2S.RoleEnterSuperArena packet)
	{
		gs.getLogger().info("receive fs role " + packet.getRoleID() + " enter super arena map " + packet.getMapID() + " , " + packet.getMapInstance() + " mainSpawnPos " + packet.getMainSpawnPos());
		gs.getFightService().handleRoleEnterSuperArenaMap(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getMainSpawnPos() == 1);
	}

	public void onTCPFightClientRecvSyncSuperArenaStart(TCPFightClient peer, Packet.F2S.SyncSuperArenaStart packet)
	{
		gs.getLogger().info("receive fs sync super arena map " + packet.getMapID() + " , " + packet.getMapInstance() + " start");
		gs.getFightService().handleSyncSuperArenaStart(packet.getMapID(), packet.getMapInstance(), packet.getEloDiffs());
	}

	public void onTCPFightClientRecvSyncSuperArenaMapEnd(TCPFightClient peer, Packet.F2S.SyncSuperArenaMapEnd packet)
	{
		gs.getLogger().info("receive fs sync super arena map " + packet.getMapID() + " , " + packet.getMapInstance() + " end");
		gs.getFightService().handleSyncSuperArenaEnd(packet.getMapID(), packet.getMapInstance(), packet.getResult(), packet.getRankClearTime());
	}
	
	public void onTCPFightClientRecvSuperArenaMatchResult(TCPFightClient peer, Packet.F2S.SuperArenaMatchResult packet)
	{
		gs.getLogger().info("receive fs role " + packet.getRoleID() + " super arena match result " + packet.getResult());
		gs.getFightService().handleSuperArenaMatchResult(packet.getRoleID(), packet.getArenaType(), packet.getGrade(), packet.getResult());
	}
	
	public void onTCPFightClientRecvSyncRoleFightTeam(TCPFightClient peer, Packet.F2S.SyncRoleFightTeam packet)
	{
		gs.getLogger().info("receive fs sync role " + packet.getRoleID() + " fight team " + packet.getTeam().id);
		gs.getFightService().handleSyncRoleFightTeam(packet.getRoleID(), packet.getTeam());
	}
	
	public void onTCPFightClientRecvTeamLeaderChange(TCPFightClient peer, Packet.F2S.TeamLeaderChange packet)
	{
		gs.getLogger().trace("receive fs sync role " + packet.getRoleID() + " team leader change");
		gs.getFightService().handleTeamLeaderChange(packet.getRoleID(), packet.getNewLeader());
	}

	public void onTCPFightClientRecvMemberLeaveTeam(TCPFightClient peer, Packet.F2S.MemberLeaveTeam packet)
	{
		gs.getLogger().trace("receive fs sync role " + packet.getRoleID() + " member " + packet.getMember().id + " leave team");
		gs.getFightService().handleMemberLeaveTeam(packet.getRoleID(), packet.getMember());
	}
	
	public void onTCPFightClientRecvTeamMemberUpdateHpTrans(TCPFightClient peer, Packet.F2S.TeamMemberUpdateHpTrans packet)
	{
		gs.getLogger().trace("receive fs sync role " + packet.getRoleID() + " member " + packet.getMemberID() + " hp " + packet.getMemberHp() + " , " + packet.getMemberHpMax());
		gs.getFightService().handleTeamMemberUpdateHp(packet.getRoleID(), packet.getMemberID(), packet.getMemberHp(), packet.getMemberHpMax());
	}
	
	public void onTCPFightClientRecvFightTeamDissolve(TCPFightClient peer, Packet.F2S.FightTeamDissolve packet)
	{
		gs.getLogger().trace("receive fs sync role " + packet.getRoleID() + " team dissolve");
		gs.getFightService().handleFightTeamDissolve(packet.getRoleID());
	}
	
	public void onTCPFightClientRecvQueryTeamMemberRes(TCPFightClient peer, Packet.F2S.QueryTeamMemberRes packet)
	{
		gs.getLogger().trace("receive fs query team member res");
		gs.getFightService().handleQueryTeamMember(packet.getTagID(), packet.getQueryMember());
	}
	
	public void onTCPFightClientRecvEnterSuperArenaRace(TCPFightClient peer, Packet.F2S.EnterSuperArenaRace packet)
	{
		gs.getLogger().info("receive fs sync enter super arena race [" + packet.getMapID() + " , " + packet.getMapInstance() + "] end");
		gs.getFightService().handleSyncEnterSuperArenaRace(packet.getMapID(), packet.getMapInstance());
	}
	
	public void onTCPFightClientRecvForceWarMatchResult(TCPFightClient peer, Packet.F2S.ForceWarMatchResult packet)
	{
		gs.getLogger().info("receive fs sync role " + packet.getRoleID() + " force war match result " + packet.getResult());
		gs.getFightService().handleSyncForceWarMatchResult(packet.getRoleID(), packet.getResult());
	}
	
//	public void onTCPClanClientRecvClanProduceRes(TCPClanClient peer, Packet.Clan2S.ClanProduceRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanProduceRes " + packet.getErrCode());
//		gs.getClanService().handleClanProduceTaskResponse(packet.getTagID(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanSplitRes(TCPClanClient peer, Packet.Clan2S.ClanSplitRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSplitRes " + packet.getErrCode());
//		gs.getClanService().handleClanSplitTaskResponse(packet.getTagID(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanCreateNewRecipeRes(TCPClanClient peer, Packet.Clan2S.ClanCreateNewRecipeRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanCreateNewRecipeRes " + packet.getErrCode());
//		gs.getClanService().handleClanCreateNewRecipeTaskResponse(packet.getTagID(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanWorkShopSyncRes(TCPClanClient peer, Packet.Clan2S.ClanWorkShopSyncRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanWorkShopSyncRes " + packet.getErrCode());
//		gs.getClanService().handleClanWorkShopSyncTaskResponse(packet.getTagID(), packet.getErrCode(), packet.getRecipes());
//	}
//
//	public void onTCPClanClientRecvClanOreOwnerPetSyncRes(TCPClanClient peer, Packet.Clan2S.ClanOreOwnerPetSyncRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreOwnerPetSyncRes");
//		gs.getClanService().handleClanOreOwnerPetSyncTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getRoleId(), packet.getPets());
//	}
//
//	public void onTCPClanClientRecvClanBushiStartRes(TCPClanClient peer, Packet.Clan2S.ClanBushiStartRes packet)
//	{
//		
//		gs.getLogger().debug("receive clan ClanBushiStartRes");
//		gs.getClanService().handleClanBushiStartTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getDzTotal(), packet.getEndTime());
//	}
//	public void onTCPMapServerRecvSyncClanTaskMapCopyStart(TCPMapServer peer, Packet.M2S.SyncClanTaskMapCopyStart packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanTaskMapCopyStart " + packet.getMapID() + " , " + packet.getMapInstance());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanTaskMapCopyStart(packet.getMapID(), packet.getMapInstance());
//	}
//
//	public void onTCPClanClientRecvClanBattleAttackRes(TCPClanClient peer, Packet.Clan2S.ClanBattleAttackRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleAttackRes");
//		gs.getClanService().handleClanBattleAttackTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getCdTime(), packet.getSelf(), packet.getEnemy(), packet.getValue(), packet.getPets());
//	}
//
//	public void onTCPClanClientRecvClanChuandaoStartRes(TCPClanClient peer, Packet.Clan2S.ClanChuandaoStartRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanChuandaoStartRes");
//		gs.getClanService().handleClanChuandaoStartTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getStartTime(), packet.getDzElites(), packet.getAttriAddition());
//	}
//
//	public void onTCPClanClientRecvClanBiwuSpeedupRes(TCPClanClient peer, Packet.Clan2S.ClanBiwuSpeedupRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBiwuSpeedupRes");
//		gs.getClanService().handleClanBiwuSpeedupTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getDzElites(), packet.getEndTime());
//	}
//
//	public void onTCPClanClientRecvClanReceiveTaskRes(TCPClanClient peer, Packet.Clan2S.ClanReceiveTaskRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanReceiveTaskRes");
//		gs.getClanService().handleClanTaskReceiveTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getUseDZTotal(), 
//				packet.getUseTime(), packet.getGenDisciple(), packet.getUsedGenDisciple(), packet.getClanTaskEnemy());
//	}
//
//	public void onTCPClanClientRecvClanSetAttackTeamRes(TCPClanClient peer, Packet.Clan2S.ClanSetAttackTeamRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSetAttackTeamRes");
//		gs.getClanService().handleClanSetAttackTeamTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanBattleAttackForwardReq(TCPClanClient peer, Packet.Clan2S.ClanBattleAttackForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleAttackForwardReq");
//		gs.getClanService().handleClanBattleAttackForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanBattleFightStartRes(TCPClanClient peer, Packet.Clan2S.ClanBattleFightStartRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleFightStartRes");
//		gs.getClanService().handleClanBattleFightStartTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getBattleArray(), packet.getRoleLvl());
//	}
//
//	public void onTCPClanClientRecvRatifyAddClanRes(TCPClanClient peer, Packet.Clan2S.RatifyAddClanRes packet)
//	{
//		gs.getLogger().debug("receive clan RatifyAddClanRes");
//		gs.getClanService().handleRatifyAddClanTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getTaskId(), packet.getClanId());
//	}
//
//	public void onTCPClanClientRecvClanAppointElderRes(TCPClanClient peer, Packet.Clan2S.ClanAppointElderRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanAppointElderRes");
//		gs.getClanService().handleClanAppointElderTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvGetClanApplicationsRes(TCPClanClient peer, Packet.Clan2S.GetClanApplicationsRes packet)
//	{
//		gs.getLogger().debug("receive clan GetClanApplicationsRes");
//		gs.getClanService().handleGetClanApplicationsTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getApplications());
//	}
//
//	public void onTCPClanClientRecvClanDiscardTaskRes(TCPClanClient peer, Packet.Clan2S.ClanDiscardTaskRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDiscardTaskRes");
//		gs.getClanService().handleClanTaskDiscardTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getTaskId());
//	}
//
//	public void onTCPClanClientRecvClanDisbandRes(TCPClanClient peer, Packet.Clan2S.ClanDisbandRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDisbandRes");
//		gs.getClanService().handleClanDisbandTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getDisbandTime());
//	}
//
//	
//	public void onTCPClanClientRecvClanOreOccupyRes(TCPClanClient peer, Packet.Clan2S.ClanOreOccupyRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreOccupyRes");
//		gs.getClanService().handleClanOreOccupyTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanFinishTaskOwnerRewardForwardReq(TCPClanClient peer, Packet.Clan2S.ClanFinishTaskOwnerRewardForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanFinishTaskOwnerRewardForwardReq");
//		gs.getClanService().handleClanFinishTaskOwnerRewardForwardTaskResponse(packet.getMroleId(), packet.getXuantie(), packet.getCaoyao());
//	}
//
//	public void onTCPClanClientRecvClanRushTollgateToExpRes(TCPClanClient peer, Packet.Clan2S.ClanRushTollgateToExpRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanRushTollgateToExpRes");
//		gs.getClanService().handleClanRushTollgateToExpTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getAttriValue(), packet.getAttriAddition());
//	}
//
//	public void onTCPClanClientRecvQueryClansRes(TCPClanClient peer, Packet.Clan2S.QueryClansRes packet)
//	{
//		gs.getLogger().debug("receive clan QueryClansRes");
//		gs.getClanService().handleSearchAllClansTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getClans());
//	}
//
//	public void onTCPClanClientRecvClanBattleHelpFightEndForwardReq(TCPClanClient peer, Packet.Clan2S.ClanBattleHelpFightEndForwardReq packet)
//	{
//		gs.getClanService().handleClanBattleHelpFightEndForwardTaskResponse(packet);
//	}
//
//	public void onTCPMapServerRecvSyncClanOreMapCopyStart(TCPMapServer peer, Packet.M2S.SyncClanOreMapCopyStart packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanOreMapCopyStart " + packet.getMapID() + " , " + packet.getMapInstance());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanOreMapCopyStart(packet.getMapID(), packet.getMapInstance());
//	}
//
//	public void onTCPClanClientRecvClanCancelElderRes(TCPClanClient peer, Packet.Clan2S.ClanCancelElderRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanCancelElderRes");
//		gs.getClanService().handleClanCancelElderTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanGetTaskEnemyRes(TCPClanClient peer, Packet.Clan2S.ClanGetTaskEnemyRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanGetTaskEnemyRes");
//		gs.getClanService().handleClanGetClanTaskEnemyTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getOverview(), packet.getPetOverviews());
//	}
//
//	public void onTCPClanClientRecvApplyAddClanForwardReq(TCPClanClient peer, Packet.Clan2S.ApplyAddClanForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ApplyAddClanForwardReq");
//		gs.getClanService().handleApplyAddClanForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanSyncSelfTaskRes(TCPClanClient peer, Packet.Clan2S.ClanSyncSelfTaskRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSyncSelfTaskRes");
//		gs.getClanService().handleSyncSelfClanTaskTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getTaskId(), packet.getBasedz(), packet.getUsedz());
//	}
//
//	public void onTCPClanClientRecvClanFindEnemyRes(TCPClanClient peer, Packet.Clan2S.ClanFindEnemyRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanFindEnemyRes");
//		gs.getClanService().handleClanFindEnemyTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getClans());
//	}
//
//	public void onTCPClanClientRecvClanGetNearbyClanRes(TCPClanClient peer, Packet.Clan2S.ClanGetNearbyClanRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanGetNearbyClanRes");
//		gs.getClanService().handleGetNearbyClanTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getSelfClan(), packet.getEnemyClan());
//	}
//
//	public void onTCPClanClientRecvClanSyncTaskLibRes(TCPClanClient peer, Packet.Clan2S.ClanSyncTaskLibRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSyncTaskLibRes");
//		gs.getClanService().handleSyncClanTaskLibTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getTasks());
//	}
//	public void onTCPClanClientRecvClanRushTollgateToItemRes(TCPClanClient peer, Packet.Clan2S.ClanRushTollgateToItemRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanRushTollgateToItemRes");
//		gs.getClanService().handleClanRushTollgateToItemTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getAttriValue(), packet.getAttriAddition());
//	}
//
//	public void onTCPMapServerRecvSyncClanBattleHelpMapCopyStart(TCPMapServer peer, Packet.M2S.SyncClanBattleHelpMapCopyStart packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanBattleHelpMapCopyStart " + packet.getMapID() + " , " + packet.getMapInstance());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanBattleHelpMapCopyStart(packet.getMapID(), packet.getMapInstance());
//	}
//
//	public void onTCPClanClientRecvClanOreHarryForwardReq(TCPClanClient peer, Packet.Clan2S.ClanOreHarryForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreHarryForwardReq");
//		gs.getClanService().handleClanOreHarryForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanKickMemberRes(TCPClanClient peer, Packet.Clan2S.ClanKickMemberRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanKickMemberRes");
//		gs.getClanService().handleClanKickMemberTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvGetClanMembersRes(TCPClanClient peer, Packet.Clan2S.GetClanMembersRes packet)
//	{
//		gs.getLogger().debug("receive clan GetClanMembersRes");
//		gs.getClanService().handleGetClanMemberTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getMembers());
//	}
//
//	public void onTCPClanClientRecvClanBuyDoPowerRes(TCPClanClient peer, Packet.Clan2S.ClanBuyDoPowerRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBuyDoPowerRes");
//		gs.getClanService().handleClanBuyDoPowerTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//	
//	public void onTCPClanClientRecvClanOnRoleLoginRes(TCPClanClient peer, Packet.Clan2S.ClanOnRoleLoginRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOnRoleLoginRes");
//		gs.getClanService().handleOnRoleLoginTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getAddition(), packet.getCreateClan(), packet.getAddClans());
//	}
//
//	public void onTCPClanClientRecvClanOreBuildUpLevelRes(TCPClanClient peer, Packet.Clan2S.ClanOreBuildUpLevelRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreBuildUpLevelRes");
//		gs.getClanService().handleClanOreBuildUpLevelTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanOreHarryRes(TCPClanClient peer, Packet.Clan2S.ClanOreHarryRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreHarryRes");
//		gs.getClanService().handleClanOreHarryTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getOwner(), packet.getOreLevel(), packet.getBa());
//	}
//
//	public void onTCPClanClientRecvClanOreOwnerPetSyncForwardReq(TCPClanClient peer, Packet.Clan2S.ClanOreOwnerPetSyncForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreOwnerPetSyncForwardReq");
//		gs.getClanService().notifyClanOreOwnerPetSyncForwardResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanOreOccupyFinishRes(TCPClanClient peer, Packet.Clan2S.ClanOreOccupyFinishRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreOccupyFinishRes");
//		gs.getClanService().handleClanOreOccupyFinishTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getClanLevel());
//	}
//
//	public void onTCPClanClientRecvClanGetBaseRankRes(TCPClanClient peer, Packet.Clan2S.ClanGetBaseRankRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanGetBaseRankRes");
//		gs.getClanService().handleClanGetBaseRankTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getRank());
//	}
//
//
//	public void onTCPClanClientRecvClanSetDefendTeamRes(TCPClanClient peer, Packet.Clan2S.ClanSetDefendTeamRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSetDefendTeamRes");
//		gs.getClanService().handleClanSetDefendTeamTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanBattleHelpFightStartForwardReq(TCPClanClient peer, Packet.Clan2S.ClanBattleHelpFightStartForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleHelpFightStartForwardReq");
//		gs.getClanService().handleClanBattleHelpFightStartForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientOpen(TCPClanClient peer)
//	{
//		gs.getLogger().info("tcpclanclient open connect to " + peer.getServerAddr());
//		peer.sendPacket(new Packet.S2Clan.WhoAmI(gs.getConfig().id));
//		notifyClanReportServerTimeOffset(GameTime.getServerTimeOffset());
//		//clancount.resetCount();
//	}
//
//	public void onTCPClanClientRecvKeepAlive(TCPClanClient peer, Packet.Clan2S.KeepAlive packet)
//	{
//		
//	}
//
//	public void onTCPMapServerRecvSyncClanBattleMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncClanBattleMapCopyEnd packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanBattleMapCopyEnd " + packet.getMapID() + " , " + packet.getMapInstance());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanBattleMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getWin(), packet.getAttackingSideHp(), packet.getDefendingSideHp());
//	}
//
//	public void onTCPClanClientRecvClanRecoverGenDiscipleRes(TCPClanClient peer, Packet.Clan2S.ClanRecoverGenDiscipleRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanRecoverGenDiscipleRes");
//		gs.getClanService().handleRecoverGenDiscipleTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getGenDisciple());
//	}
//
//	public void onTCPClanClientRecvClanBattleHelpFightStartRes(TCPClanClient peer, Packet.Clan2S.ClanBattleHelpFightStartRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleHelpFightStartRes");
//		gs.getClanService().handleClanBattleHelpFightStartTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getBa(), packet.getSelfInfo());
//	}
//
//	public void onTCPClanClientRecvClanAppliedRes(TCPClanClient peer, Packet.Clan2S.ClanAppliedRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanAppliedRes");
//		gs.getClanService().handleClanAppliedTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getClanIds());
//	}
//
//	public void onTCPClanClientRecvClanOwnerAttriAdditionRes(TCPClanClient peer, Packet.Clan2S.ClanOwnerAttriAdditionRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOwnerAttriAdditionRes");
//		gs.getClanService().handleClanOwnerAttriAdditionTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getAttriAddition());
//	}
//	public void onTCPClanClientRecvClanBattleFightStartForwardReq(TCPClanClient peer, Packet.Clan2S.ClanBattleFightStartForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleFightStartForwardReq");
//		gs.getClanService().handleClanBattleFightStartForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanBattleHelpRes(TCPClanClient peer, Packet.Clan2S.ClanBattleHelpRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleHelpRes");
//		gs.getClanService().handleClanBattleHelpTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getAttack(), packet.getDefend());
//	}
//
//	public void onTCPClanClientRecvClanDiyskillShareAwardRes(TCPClanClient peer, Packet.Clan2S.ClanDiyskillShareAwardRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDiyskillShareAwardRes");
//		gs.getClanService().handleClanDiySkillShareAwardTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getAwardCount());
//	}
//
//	public void onTCPClanClientRecvClanBattleFightEndForwardReq(TCPClanClient peer, Packet.Clan2S.ClanBattleFightEndForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleFightEndForwardReq");
//		gs.getClanService().handleClanBattleFightEndForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanEnemyFightDataForwardReq(TCPClanClient peer, Packet.Clan2S.ClanEnemyFightDataForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanEnemyFightDataForwardReq");
//		gs.getClanService().handleClanGetEnemyFightDataForwardTaskResponse(packet);
//	}
//
//	public void onTCPMapServerRecvSyncClanTaskMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncClanTaskMapCopyEnd packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanTaskMapCopyEnd " + packet.getMapID() + " , " + packet.getMapInstance());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanTaskMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getWin(), packet.getAttackingSideHp(), packet.getDefendingSideHp());
//	}
//
//	public void onTCPClanClientRecvClanSyncOreRes(TCPClanClient peer, Packet.Clan2S.ClanSyncOreRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSyncOreRes");
//		gs.getClanService().handleSyncClanOreTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getOres());
//	}
//
//	public void onTCPClanClientRecvClanFinishTaskRes(TCPClanClient peer, Packet.Clan2S.ClanFinishTaskRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanFinishTaskRes");
//		gs.getClanService().handleClanTaskFinishTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getOwnerId(), packet.getServerId(), packet.getTaskId());
//	}
//
//	public void onTCPClanClientRecvClanGetEnemyForwardReq(TCPClanClient peer, Packet.Clan2S.ClanGetEnemyForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanGetEnemyForwardReq");
//		gs.getClanService().handleClanGetEnemyForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanShoutuSpeedupRes(TCPClanClient peer, Packet.Clan2S.ClanShoutuSpeedupRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanShoutuSpeedupRes");
//		gs.getClanService().handleClanShoutuSpeedupTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getDzTotal(), packet.getEndTime());
//	}
//
//	public void onTCPClanClientRecvClanGetEliteDiscipleRes(TCPClanClient peer, Packet.Clan2S.ClanGetEliteDiscipleRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanGetEliteDiscipleRes");
//		gs.getClanService().handleClanGetEliteDiscipleTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getEds());
//	}
//
//	public void onTCPClanClientRecvClanDiyskillShareRes(TCPClanClient peer, Packet.Clan2S.ClanDiyskillShareRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDiyskillShareRes");
//		gs.getClanService().handleClanShareDiySkillTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getShareCount());
//	}
//
//	public void onTCPClanClientRecvClanShoutuFinishRes(TCPClanClient peer, Packet.Clan2S.ClanShoutuFinishRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanShoutuFinishRes");
//		gs.getClanService().handleClanShoutuFinishTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getDzTotal(), packet.getEndTime());
//	}
//
//	public void onTCPClanClientRecvAdjustTimeOffset(TCPClanClient peer, Packet.Clan2S.AdjustTimeOffset packet)
//	{
//		gs.getLogger().info("receive clan adjust time offset " + packet.getTimeOffset() + ", current time offset " + GameTime.getServerTimeOffset());
//		if (packet.getTimeOffset() > GameTime.getServerTimeOffset())
//		{
//			gs.getMapService().syncAllMapsTimeOffset(packet.getTimeOffset());
//			GameTime.setServerTimeOffset(packet.getTimeOffset());
//		}
//	}
//
//	public void onTCPClanClientRecvClanSyncHistoryRes(TCPClanClient peer, Packet.Clan2S.ClanSyncHistoryRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSyncHistoryRes");
//		gs.getClanService().handleClanSyncHistoryTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getHistorys());
//	}
//
//	public void onTCPClanClientRecvSyncClanRes(TCPClanClient peer, Packet.Clan2S.SyncClanRes packet)
//	{
//		gs.getLogger().debug("receive clan SyncClanRes");
//		gs.getClanService().handleSyncClanInfoTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getInfo(), packet.getApplyRedPoint());
//	}
//
//	public void onTCPClanClientRecvClanDayRefreshRes(TCPClanClient peer, Packet.Clan2S.ClanDayRefreshRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDayRefreshRes");
//		gs.getClanService().handleClanDayRefreshTaskResponse(packet.getTagID(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanAutoRefreshTaskRes(TCPClanClient peer, Packet.Clan2S.ClanAutoRefreshTaskRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanAutoRefreshTaskRes");
//		gs.getClanService().handleAutoRefreshTaskTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getTaskId());
//	}
//
//	public void onTCPClanClientRecvClanRatifyAddForwardReq(TCPClanClient peer, Packet.Clan2S.ClanRatifyAddForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanRatifyAddForwardReq");
//		gs.getClanService().handleClanRatifyAddForwardResponse(packet.getMember().roleid, packet.getTaskId(), packet.getClanId());
//	}
//
//	public void onTCPClanClientRecvClanModifyRankRes(TCPClanClient peer, Packet.Clan2S.ClanModifyRankRes packet)
//	{
//	
//	}
//
//	public void onTCPClanClientRecvQueryRoleClansRes(TCPClanClient peer, Packet.Clan2S.QueryRoleClansRes packet)
//	{
//		gs.getLogger().debug("receive clan QueryRoleClansRes");
//		gs.getClanService().handleQueryRoleClansTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getClans());
//	}
//
//	public void onTCPClanClientRecvClanGetTaskEnemyForwardReq(TCPClanClient peer, Packet.Clan2S.ClanGetTaskEnemyForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanGetTaskEnemyForwardReq");
//		gs.getClanService().handleClanGetClanTaskEnemyTaskForwardResponse(packet);
//	}
//
//	public void onTCPClanClientRecvQueryClanByIdRes(TCPClanClient peer, Packet.Clan2S.QueryClanByIdRes packet)
//	{
//		gs.getLogger().debug("receive clan QueryClanByIdRes");
//		gs.getClanService().handleSearchClanByIdTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getClan());
//	}
//
//	public void onTCPClanClientRecvClanSearchOreForwardReq(TCPClanClient peer, Packet.Clan2S.ClanSearchOreForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanSearchOreForwardReq");
//		gs.getClanService().handleClanSearchOreForwardTaskResponse(packet.getLvl(), packet.getTagId(), packet.getServId(), packet.getOreServId());
//	}
//
//	
//	public void onTCPMapServerRecvSyncClanOreMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncClanOreMapCopyEnd packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanOreMapCopyEnd " + packet.getMapID() + " , " + packet.getMapInstance() + " , " + packet.getWin());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanOreMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getWin(), packet.getAttackingSideHp(), packet.getDefendingSideHp());
//	}
//
//	public void onTCPClanClientRecvClanBattleFightEndRes(TCPClanClient peer, Packet.Clan2S.ClanBattleFightEndRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleFightEndRes");
//		gs.getClanService().handleClanBattleFightEndTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientOpenFailed(TCPClanClient peer, ket.kio.ErrorCode errcode)
//	{
////		if (clancount.increaseCount())
////			gs.getLogger().warn("tcpclanclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
//	}
//
//	public void onTCPClanClientRecvClanBiwuStartRes(TCPClanClient peer, Packet.Clan2S.ClanBiwuStartRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBiwuStartRes");
//		gs.getClanService().handleClanBiwuStartTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getStartTime());
//	}
//	public void onTCPClanClientRecvClanOreBorrowPetRes(TCPClanClient peer, Packet.Clan2S.ClanOreBorrowPetRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreBorrowPetRes");
//		gs.getClanService().handleClanBorrowPetTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getServerId(), packet.getRoleId());
//	}
//
//
//	public void onTCPClanClientRecvClanBattleFightExitForwardReq(TCPClanClient peer, Packet.Clan2S.ClanBattleFightExitForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleFightExitForwardReq");
//		gs.getClanService().handleClanBattleFightExitForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanOwnerFightDataForwardReq(TCPClanClient peer, Packet.Clan2S.ClanOwnerFightDataForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanOwnerFightDataForwardReq");
//		gs.getClanService().handleClanGetOwnerFightDataForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanDiyskillBorrowRes(TCPClanClient peer, Packet.Clan2S.ClanDiyskillBorrowRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDiyskillBorrowRes");
//		gs.getClanService().handleClanDiySkillBorrowTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getDiySkill());
//	}
//
//	public void onTCPClanClientRecvClanMemberLeaveRes(TCPClanClient peer, Packet.Clan2S.ClanMemberLeaveRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanMemberLeaveRes");
//		gs.getClanService().handleClanMemberLeaveTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanEnemyFightDataRes(TCPClanClient peer, Packet.Clan2S.ClanEnemyFightDataRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanEnemyFightDataRes");
//		gs.getClanService().handleClanGetEnemyFightDataTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getBa());
//	}
//
//	public void onTCPClanClientClose(TCPClanClient peer, ket.kio.ErrorCode errcode)
//	{
//		gs.getLogger().warn("tcpclanclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
//	}
//
//	public void onTCPClanClientRecvCreateClanRes(TCPClanClient peer, Packet.Clan2S.CreateClanRes packet)
//	{
//		gs.getLogger().debug("receive clan CreateClanRes");
//		gs.getClanService().handleCreateClanTaskTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getInfo(), packet.getTaskId());
//	}
//
//	public void onTCPClanClientRecvClanBattleSeekhelpRes(TCPClanClient peer, Packet.Clan2S.ClanBattleSeekhelpRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleSeekhelpRes");
//		gs.getClanService().handleClanBattleSeekhelpTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getRoles(), packet.getSelfInfo());
//	}
//
//	public void onTCPClanClientRecvClanSearchOreSyncRes(TCPClanClient peer, Packet.Clan2S.ClanSearchOreSyncRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSearchOreSyncRes");
//		gs.getClanService().handleClanSearchOreSyncTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getPets());
//	}
//
//	public void onTCPClanClientRecvClanOreBorrowPetForwardReq(TCPClanClient peer, Packet.Clan2S.ClanOreBorrowPetForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanOreBorrowPetForwardReq");
//		gs.getClanService().notifyClanOreBorrowPetForwardResponse(packet);
//	}
//
//	public void onTCPClanClientRecvQueryClanByNameRes(TCPClanClient peer, Packet.Clan2S.QueryClanByNameRes packet)
//	{
//		gs.getLogger().debug("receive clan QueryClanByNameRes");
//		gs.getClanService().handleSearchClanByNameTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getClan());
//	}
//
//	public void onTCPClanClientRecvClanDiyskillGetShareRes(TCPClanClient peer, Packet.Clan2S.ClanDiyskillGetShareRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDiyskillGetShareRes");
//		gs.getClanService().handleClanDiySkillGetShareTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getShares());
//	}
//
//	public void onTCPMapServerRecvSyncClanBattleHelpMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncClanBattleHelpMapCopyEnd packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanBattleHelpMapCopyEnd " + packet.getMapID() + " , " + packet.getMapInstance());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanBattleHelpMapCopyEnd(packet.getMapID(), packet.getMapInstance(), packet.getWin(), packet.getAttackingSideHp(), packet.getDefendingSideHp());
//	}
//
//	public void onTCPClanClientRecvApplyAddClanRes(TCPClanClient peer, Packet.Clan2S.ApplyAddClanRes packet)
//	{
//		gs.getLogger().debug("receive clan ApplyAddClanRes");
//		gs.getClanService().handleApplyAddClanTaskTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getRids());
//	}
//
//	public void onTCPClanClientRecvClanBattleHelpFightEndRes(TCPClanClient peer, Packet.Clan2S.ClanBattleHelpFightEndRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleHelpFightEndRes");
//		gs.getClanService().handleClanBattleHelpFightEndTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getPre());
//	}
//
//	public void onTCPClanClientRecvClanUplevelRes(TCPClanClient peer, Packet.Clan2S.ClanUplevelRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanUplevelRes");
//		gs.getClanService().handleClanUplevelTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getLevel(), packet.getAttriAddition());
//	}
//
//	public void onTCPClanClientRecvClanBuyPrestigeRes(TCPClanClient peer, Packet.Clan2S.ClanBuyPrestigeRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBuyPrestigeRes");
//		gs.getClanService().handleClanBuyPrestigeTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanBattleHelpForwardReq(TCPClanClient peer, Packet.Clan2S.ClanBattleHelpForwardReq packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleHelpForwardReq");
//		gs.getClanService().handleClanBattleHelpForwardTaskResponse(packet);
//	}
//
//	public void onTCPClanClientRecvClanBiwuFinishRes(TCPClanClient peer, Packet.Clan2S.ClanBiwuFinishRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBiwuFinishRes");
//		gs.getClanService().handleClanBiwuFinishTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getDzElites(), packet.getEndTime(), packet.getAttriAddition());
//	}
//	public void onTCPClanClientRecvClanGetEnemyRes(TCPClanClient peer, Packet.Clan2S.ClanGetEnemyRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanGetEnemyRes");
//		gs.getClanService().handleClanGetEnemyTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getOwnerId(), packet.getEnemyPets(), packet.getEnemy());
//	}
//
//	public void onTCPClanClientRecvClanMovePositionRes(TCPClanClient peer, Packet.Clan2S.ClanMovePositionRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanMovePositionRes");
//		gs.getClanService().handleClanMovePositionTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getX(), packet.getY());
//	}
//
//	public void onTCPClanClientRecvClanRecruitRes(TCPClanClient peer, Packet.Clan2S.ClanRecruitRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanRecruitRes");
//		gs.getClanService().handleClanRecruitTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanOwnerFightDataRes(TCPClanClient peer, Packet.Clan2S.ClanOwnerFightDataRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanOwnerFightDataRes");
//		gs.getClanService().handleClanGetOwnerFightDataTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getOwnerFightPet(), packet.getOwnerFightPetHost());
//	}
//
//	public void onTCPClanClientRecvClanDiyskillCancelShareRes(TCPClanClient peer, Packet.Clan2S.ClanDiyskillCancelShareRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanDiyskillCancelShareRes");
//		gs.getClanService().handleClanCancelShareDiySkillTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanBattleFightExitRes(TCPClanClient peer, Packet.Clan2S.ClanBattleFightExitRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanBattleFightExitRes");
//		gs.getClanService().handleClanBattleFightExitTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPMapServerRecvSyncClanBattleMapCopyStart(TCPMapServer peer, Packet.M2S.SyncClanBattleMapCopyStart packet, int sessionid)
//	{
//		gs.getLogger().debug("receive map session " + sessionid + " SyncClanBattleMapCopyStart " + packet.getMapID() + " , " + packet.getMapInstance());
//		stat.recvMapServerMsgCounter.incrementAndGet();
//		gs.getMapService().handleSyncClanBattleMapCopyStart(packet.getMapID(), packet.getMapInstance());
//	}
//
//	public void onTCPClanClientRecvClanCancelDisbandRes(TCPClanClient peer, Packet.Clan2S.ClanCancelDisbandRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanCancelDisbandRes");
//		gs.getClanService().handleClanCancelDisbandTaskResponse(packet.getTagId(), packet.getErrCode());
//	}
//
//	public void onTCPClanClientRecvClanShoutuRes(TCPClanClient peer, Packet.Clan2S.ClanShoutuRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanShoutuRes");
//		gs.getClanService().handleClanShoutuTaskResponse(packet.getTagId(), packet.getErrCode(), packet.getStartTime());
//	}
//
//	public void onTCPClanClientRecvClanSearchOreRes(TCPClanClient peer, Packet.Clan2S.ClanSearchOreRes packet)
//	{
//		gs.getLogger().debug("receive clan ClanSearchOreRes");
//		gs.getClanService().handleClanSearchOreTaskResponse(packet.getTagId(), GameData.PROTOCOL_OP_SUCCESS, packet.getOre());
//	}
	public void onTCPFightClientRecvTeamJoinForceWarRes(TCPFightClient peer, Packet.F2S.TeamJoinForceWarRes packet)
	{
		gs.getLogger().trace("receive fs role team join force war res " + packet.getOk());
		gs.getFightService().handleTeamJoinForceWarRes(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvTeamQuitForceWarRes(TCPFightClient peer, Packet.F2S.TeamQuitForceWarRes packet)
	{
		gs.getLogger().trace("receive fs role team quit force war res " + packet.getOk());
		gs.getFightService().handleTeamQuitForceWarRes(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvSyncRoleDemonHoleRes(TCPFightClient peer, Packet.F2S.SyncRoleDemonHoleRes packet)
	{
		gs.getLogger().trace("receive fs sync role demon hole info");
		gs.getFightService().handleSyncRoleDemonHoleRes(packet.getTagID(), packet.getCurFloor(), packet.getGrade());
	}

	public void onTCPFightClientRecvRoleJoinDemonHoleRes(TCPFightClient peer, Packet.F2S.RoleJoinDemonHoleRes packet)
	{
		gs.getLogger().info("receive fs role join demon hole res " + packet.getGrade());
		gs.getFightService().handleRoleJoinDemonHoleRes(packet.getTagID(), packet.getGrade());
	}

	public void onTCPFightClientRecvRoleChangeDemonHoleFloorRes(TCPFightClient peer, Packet.F2S.RoleChangeDemonHoleFloorRes packet)
	{
		gs.getLogger().info("receive fs role change demon hole floor res " + packet.getOk());
		gs.getFightService().handleRoleChangeDemonFloorRes(packet.getTagID(), packet.getOk());
	}

	public void onTCPFightClientRecvRoleDemonHoleBattleRes(TCPFightClient peer, Packet.F2S.RoleDemonHoleBattleRes packet)
	{
		gs.getLogger().trace("receive fs role demon hole battle info");
		gs.getFightService().handleRoleDemonHoleBattleRes(packet.getTagID(), packet.getCurFloor(), packet.getTotal());
	}

	public void onTCPFightClientRecvRoleEnterDemonHoleMap(TCPFightClient peer, Packet.F2S.RoleEnterDemonHoleMap packet)
	{
		gs.getLogger().info("receive fs role " + packet.getRoleID() + " enter demon hole map[" + packet.getMapID() + " , " + packet.getMapInstance() + "] ");
		gs.getFightService().handleRoleEnterDemonHoleMap(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getFloor(), packet.getGrade());
	}

	public void onTCPFightClientRecvSyncDemonHoleMapEnd(TCPFightClient peer, Packet.F2S.SyncDemonHoleMapEnd packet)
	{
		gs.getLogger().info("receive fs demon hole map[" + packet.getMapID() + " , " + packet.getMapInstance() + "] end");
		gs.getFightService().handleSyncDemonHoleMapEnd(packet.getMapID(), packet.getMapInstance(), packet.getCurFloor(), packet.getTotal());
	}

	public void onTCPFightClientRecvSyncGSCreateNewTeam(TCPFightClient peer, Packet.F2S.SyncGSCreateNewTeam packet)
	{
		gs.getLogger().trace("receive fs create new team");
		gs.getFightService().handleCreateNewTeam(packet.getMembers());
	}

	public void onTCPFightClientRecvSyncRoleChatRoom(TCPFightClient peer, Packet.F2S.SyncRoleChatRoom packet)
	{
		gs.getLogger().debug("receive fs sync role " + packet.getRoleID() + " chat room " + packet.getRoomID());
		gs.getFightService().handleSyncRoleChatRoom(packet.getRoleID(), packet.getMapID(), packet.getMapInstance(), packet.getRoomID());
	}

	public void onTCPExchangeClientOpen(TCPExchangeClient peer)
	{
		gs.getLogger().info("tcpexchangeclient open connect to " + peer.getServerAddr());
		peer.sendPacket(new Packet.S2E.WhoAmI(gs.getConfig().id));
		exchangecount.resetCount();
	}

	public void onTCPExchangeClientOpenFailed(TCPExchangeClient peer, ket.kio.ErrorCode errcode)
	{
		if (exchangecount.increaseCount())
			gs.getLogger().warn("tcpexchangeclient open connect to " + peer.getServerAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPExchangeClientClose(TCPExchangeClient peer, ket.kio.ErrorCode errcode)
	{
		gs.getLogger().warn("tcpexchangeclient close on " + peer.getServerAddr() + " , errcode=" + errcode);
	}

	public void onTCPExchangeClientRecvKeepAlive(TCPExchangeClient peer, Packet.E2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPExchangeClientRecvReceiveMsg(TCPExchangeClient peer, Packet.E2S.ReceiveMsg packet)
	{
		gs.getLogger().trace("receive exchangeserver role " + packet.getMsg().srcId + " all server msg: server name is " + packet.getMsg().gsName + ", content is " + packet.getMsg().content.msg);
		gs.getLoginManager().receiveWorldMsg(packet.getMsg());
	}

	public void onTCPExchangeClientRecvSocialMsgRes(TCPExchangeClient peer, Packet.E2S.SocialMsgRes packet)
	{
		gs.getLogger().debug("receive exchangeserver forward social packet type " + packet.getData().dataType);
		gs.getExchangeService().onHandleSocialRes(packet.getData());
	}

	//// end handlers.
	
	void keepAlive(int timeTick)
	{
		if (timeTick % 15 ==  0)
		{
			for (TCPAuthClient tauc : taucs.values())
				tauc.sendPacket(new Packet.S2AU.KeepAlive(gs.getConfig().id));
			//tcc.sendPacket(new Packet.S2Clan.KeepAlive(gs.getConfig().id));
			tac.sendPacket(new Packet.S2Auction.KeepAlive(gs.getConfig().id));
			tgmc.sendPacket(new Packet.S2GM.KeepAlive(gs.getConfig().id));
			tfc.sendPacket(new Packet.S2F.KeepAlive(gs.getConfig().id));
			tec.sendPacket(new Packet.S2E.KeepAlive(gs.getConfig().id));
		}
	}
	
	public void notifyMapSyncServerTimeOffset(int session, int timeOffset)
	{
		gs.getLogger().info("notify session " + session + " map server sync time offset " + timeOffset);
		tms.sendPacket(session, new Packet.S2M.SyncTimeOffset(timeOffset));
	}
	
	public void notifyMapSyncDoubleDropCfg(int session, List<SBean.DoubleDropCfg> cfgs)
	{
		gs.getLogger().info("notify session " + session + " map server sync double drop cfg");
		tms.sendPacket(session, new Packet.S2M.SyncDoubleDropCfg(cfgs));
	}
	
	public void notifyMapSyncExtraDropCfg(int session, List<SBean.ExtraDropCfg> cfgs)
	{
		gs.getLogger().info("notify session " + session + " map server sync extra drop cfg");
		tms.sendPacket(session, new Packet.S2M.SyncExtraDropCfg(cfgs));
	}
	
	public void notifyMapSyncWorldNum(int session, int worldNum, Map<Integer, Integer> extraWorldNum)
	{
		gs.getLogger().info("notify session " + session + " map server sync world num " + worldNum + " extra ");
		tms.sendPacket(session, new Packet.S2M.SyncWorldNum(worldNum, extraWorldNum));
	}
	
//	public void notifyMapCreateMapInstance(int session, int tag, int mapID)
//	{
//		gs.getLogger().debug("notify map create map " + mapID + " instance ");
//		tms.sendPacket(session, new Packet.S2M.CreateMapInstance(tag, mapID));
//	}
	public void notifyMapStartMapCopy(int session, int mapId, int mapInstance)
	{
		gs.getLogger().debug("notify map session " + session + " start map copy " + mapId + " " + mapInstance);
		tms.sendPacket(session, new Packet.S2M.StartMapCopy(mapId, mapInstance));
	}
	
	public void notifyMapEndMapCopy(int session, int mapId, int mapInstance)
	{
		gs.getLogger().debug("notify map session " + session + " end map copy " + mapId + " " + mapInstance);
		tms.sendPacket(session, new Packet.S2M.EndMapCopy(mapId, mapInstance));
	}
	
	public void notifyMapMapCopyReady(int session, int mapId, int mapInstance)
	{
		gs.getLogger().debug("notify map session " + session + " map copy " + mapId + " " + mapInstance + " ready !");
		tms.sendPacket(session, new Packet.S2M.MapCopyReady(mapId, mapInstance));
	}
	
	public void notifyMapResetSectMap(int session, int mapId, int mapInstance, Map<Integer, Integer> progress)
	{
		gs.getLogger().debug("notify map reset sect map " + mapId + " " + mapInstance);
		tms.sendPacket(session, new Packet.S2M.ResetSectMap(mapId, mapInstance, progress));
	}
	
	public void notifyMapResetSectGroupMap(int session, int mapId, int mapInstance, Map<Integer, Integer> process, Map<Integer, Integer> killNum, Map<Integer, SBean.RoleDamageDetail> damageRank)
	{
		gs.getLogger().debug("notify map session " + session + " reset sect group map " + mapId + " " + mapInstance);
		tms.sendPacket(session, new Packet.S2M.ResetSectGroupMap(mapId, mapInstance, process, killNum, damageRank));
	}
	
	public void notifyMapResetArenaMap(int session, int mapId, int mapInstance, SBean.BattleArray battleArray)
	{
		gs.getLogger().debug("notify map session " + session + " reset arena map " + mapId + " " + mapInstance);
		tms.sendPacket(session, new Packet.S2M.ResetArenaMap(mapId, mapInstance, battleArray));
	}
	
	public void notifyMapResetBWArenaMap(int session, int mapId, int mapInstance, SBean.BattleArray battleArray, boolean petLack)
	{
		gs.getLogger().debug("notify map session " + session + " reset bw arena map " + mapId + " " + mapInstance);
		tms.sendPacket(session, new Packet.S2M.ResetBWArenaMap(mapId, mapInstance, battleArray, petLack ? (byte) 1 : (byte) 0));
	}
	
//	public void notifyMapResetClanOreMap(int session, int mapId, int mapInstance, SBean.BattleArray battleArray)
//	{
//		gs.getLogger().debug("notify map reset clan ore map " + mapId + " " + mapInstance);
//		tms.sendPacket(session, new Packet.S2M.ResetClanOreMap(mapId, mapInstance, battleArray));
//	}
//	
//	public void notifyMapResetClanBattleHelpMap(int session, int mapId, int mapInstance, SBean.BattleArray battleArray)
//	{
//		gs.getLogger().debug("notify map reset clan battle help map " + mapId + " " + mapInstance);
//		tms.sendPacket(session, new Packet.S2M.ResetClanBattleHelpMap(mapId, mapInstance, battleArray));
//	}
//	
//	
//	public void notifyMapResetClanBattleMap(int session, int mapId, int mapInstance, SBean.BattleArray battleArray)
//	{
//		gs.getLogger().debug("notify map reset clan battle  map " + mapId + " " + mapInstance);
//		tms.sendPacket(session, new Packet.S2M.ResetClanBattleMap(mapId, mapInstance, battleArray));
//	}
//	
//	public void notifyMapResetClanTaskMap(int session, int mapId, int mapInstance, SBean.BattleArray battleArray)
//	{
//		gs.getLogger().debug("notify map reset clan task map " + mapId + " " + mapInstance);
//		tms.sendPacket(session, new Packet.S2M.ResetClanTaskMap(mapId, mapInstance, battleArray));
//	}
	
	public void notifyMapCreateWorldMapBoss(int session, int mapID, int mapInstanceID, int bossID, int seq, int curHP)
	{
		gs.getLogger().debug("notify map session " + session + " world map [" + mapID + " , " + mapInstanceID + "] create boss " + bossID + " seq " + seq);
		tms.sendPacket(session, new Packet.S2M.CreateWorldMapBoss(mapID, mapInstanceID, bossID, seq, curHP));
	}
	
	public void notifyMapDestroyWorldMapBoss(int session, int mapID, int mapInstanceID, int bossID)
	{
		gs.getLogger().debug("notify map session " + session + " map [" + mapID + " , " + mapInstanceID + "] destroy world map boss " + bossID);
		tms.sendPacket(session, new Packet.S2M.DestroyWorldMapBoss(mapID, mapInstanceID, bossID));
	}
	
	public void notifyMapWorldBossPop(int session, int mapID, int mapInstance, int bossID, int index)
	{
		gs.getLogger().debug("notify map session " + session + " map [" + mapID + " , " + mapInstance + "] world map boss " + bossID + " pop " + index);
		tms.sendPacket(session, new Packet.S2M.WorldBossPop(mapID, mapInstance, bossID, index));
	}
	
	public void notifyMapInitWorldBoss(int session, SBean.DBBoss dbBoss)
	{
		gs.getLogger().debug("notify map session " + session + " map init world boss");
		tms.sendPacket(session, new Packet.S2M.InitWorldBoss(dbBoss));
	}
	
	public void notifyMapPickUpResult(int session, int roleID, Set<Integer> dropIDs, int success)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " pick up " + dropIDs + " success " + success);
		tms.sendPacket(session, new Packet.S2M.PickUpResult(roleID, dropIDs, success));
	}
	
	public void notifyMapInitWorldMapFlag(int session, Map<Integer, MapFlagInfo> mapFlags)
	{
		gs.getLogger().debug("notify map session " + session + " init world map flag");
		tms.sendPacket(session, new Packet.S2M.InitWorldMapFlag(mapFlags));
	}
	
	public void notifyMapCreateSuperMonster(int session, int mapID, int superMonsterID, int standTime)
	{
		gs.getLogger().debug("notify map session " + session + " world map " + mapID + " create super monster " + superMonsterID + " standTime " + standTime);
		tms.sendPacket(session, new Packet.S2M.CreateWorldMapSuperMonster(mapID, superMonsterID, standTime));
	}
	
	public void notifyMapCreateMapFlag(int session, int mapId, Vector3 flagPoint, int flagId, List<Integer> monsterPointId, SBean.MapFlagSectOverView sect)
	{
		gs.getLogger().debug("notify map session " + session + " world map " + mapId + " create map flag sectId " + sect.sectId);
		tms.sendPacket(session, new Packet.S2M.CreateWorldMapFlag(mapId, flagPoint, flagId, monsterPointId, sect));
	}
	
	public void notifyMapSyncMapFlagInfo(int session, int mapId, SBean.MapFlagSectOverView sect)
	{
		gs.getLogger().debug("notify map session " + session + " world map " + mapId + " create map flag sectId " + sect.sectId);
		tms.sendPacket(session, new Packet.S2M.SyncMapFlagInfo(mapId, sect));
	}
	
	public void notifyMapCreateMineral(int session, int mapID, int worldMineralID, int standTime)
	{
		gs.getLogger().debug("notify map session " + session + " world map " + mapID + " create world mineral " + worldMineralID + " standTime " + standTime);
		tms.sendPacket(session, new Packet.S2M.CreateWorldMapMineral(mapID, worldMineralID, standTime));
	}
	
	public void notifyMapCarEnterMap(int session, SBean.DBEscortCar carInfo, int ownerID, String ownerName, int teamCarCnt, SBean.Team team, int sectID)
	{
		gs.getLogger().debug("notify map session " + session + " car " + ownerID + " enter map [" + carInfo.mapID + " , " + carInfo.mapInstance + "]");
		tms.sendPacket(session, new Packet.S2M.CarEnterMap(carInfo, ownerID, ownerName, teamCarCnt, team, sectID));
	}
	
	public void notifyMapCarLeaveMap(int session, int carID, int mapID, int mapInstance)
	{
		gs.getLogger().debug("notify map session " + session + " car " + carID + " leave current map [" + mapID + " , " + mapInstance + "]");
		tms.sendPacket(session, new Packet.S2M.CarLeaveMap(carID));
	}

	public void notifyMapCarUpdateTeamCarCnt(int session, int carID, int carCnt)
	{
		gs.getLogger().debug("notify map session " + session + " update car " + carID + " new cnt is [" + carCnt + "]");
		tms.sendPacket(session, new Packet.S2M.CarUpdateTeamCarCnt(carID,carCnt));
	}
	
	public void notifyMapRoleUpdateCarBehavior(int session, int roleID, byte carOwner, byte carRobber)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update car behavior " + carOwner + " , " + carRobber);
		tms.sendPacket(session, new Packet.S2M.UpdateRoleCarBehavior(roleID, carOwner, carRobber));
	}
	
	public void notifyMapRoleEnterMap(int session, SBean.FightRole fightRole, int mapId, int mapInstance, SBean.Location location, int hp, int sp, 
			int armorval, Map<Integer, SBean.DBBuff> buffs, Map<Integer, SBean.FightPet> pets, List<Integer> petSeq, SBean.PetHost host, 
			SBean.PKInfo pkInfo, SBean.Team team, int curRideHorse, SBean.MulRoleInfo mulRoleInfo, SBean.DBAlterState alterState, boolean isMainSpwanPos, int dayFailedStreak, int vipLevel, int curWizard, boolean canTakeDrop)
	{
		gs.getLogger().debug("notify map session " + session + " role " + fightRole.base.roleID + " enter new map " + mapId + " " + mapInstance);
		tms.sendPacket(session, new Packet.S2M.EnterMap(fightRole, mapId, mapInstance, location, hp, sp, armorval, buffs, pets, petSeq, host, pkInfo, team, curRideHorse, mulRoleInfo,
														alterState, isMainSpwanPos ? (byte)1 : (byte)0, dayFailedStreak, vipLevel, curWizard, (byte) (canTakeDrop ? 1 : 0)));
	}
	
	public void notifyMapRoleLeaveMap(int session, int roleID, int mapId, int instanceId)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " leave current map " + mapId + " " + instanceId);
		tms.sendPacket(session, new Packet.S2M.LeaveMap(roleID));
	}
	
	public void notifyMapRoleResetLocation(int session, int roleID, int mapId, int instanceId, SBean.Location location)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " reset current map " + mapId + " " + instanceId + " location");
		tms.sendPacket(session, new Packet.S2M.ResetLocation(roleID, location));
	}
	
	public void notifyMapRoleUpdateActive(int session, int roleID, boolean active)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " update active " + active);
		tms.sendPacket(session, new Packet.S2M.UpdateActive(roleID, active ? (byte)1 : (byte)0));
	}
	
	public void notifyMapRoleSyncPetLack(int session, int roleID, boolean petLack)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " sync map petlack " + petLack);
		tms.sendPacket(session, new Packet.S2M.SyncRolePetLack(roleID, petLack ? (byte)1 : (byte)0));
	}
	
//	public void notifyMapRoleSyncClanBattleHurt(int session, int roleID, boolean hurt)
//	{
//		gs.getLogger().debug("notify map role " + roleID + " sync map clan battle hurt " + hurt);
//		tms.sendPacket(session, new Packet.S2M.SyncRoleClanBattleHurt(roleID, hurt ? (byte)1 : (byte)0));
//	}
	
	public void notifyMapRoleSetPetAlter(int session, int roleID, SBean.FightPet fightPet, SBean.PetHost petHost)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " set pet alter !");
		tms.sendPacket(session, new Packet.S2M.SetPetAlter(roleID, fightPet, petHost));
	}
	
	public void notifyMapRoleUseItemSkill(int session, int roleID, int itemID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " use item " + itemID + " skill");
		tms.sendPacket(session, new Packet.S2M.RoleUseItemSkill(roleID, itemID, pos, rotation, targetID, targetType, ownerID, timeTick));
	}
	
	public void notifyMapRoleRename(int session, int roleID, String newName)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " rename " + newName);
		tms.sendPacket(session, new Packet.S2M.RoleRename(roleID, newName));
	}
	
	public void sendMapLuaChannelPacket(int session, int roleID, String data)
	{
		gs.getLogger().trace("forward map session " + session + " packet: role " + roleID + " lua channel packet:[" + data + "]");
		tms.sendPacket(session, new Packet.S2M.LuaChannel(roleID, data));
	}
	
	public void sendMapStrChannelPacket(int session, int roleID, String data)
	{
		gs.getLogger().trace("forward map session " + session + " packet: role " + roleID + " str channel packet:[" + data + "]");
		tms.sendPacket(session, new Packet.S2M.StrChannel(roleID, data));
	}
	
//	public void notifyMapRoleRemoveEquip(int session, int roleID, int wid)
//	{
//		gs.getLogger().debug("notify map role " + roleID + " remove equip");
//		tms.sendPacket(session, new Packet.S2M.RemoveEquip(roleID, wid));
//	}
	
	public void notifyMapRoleGainNewSuite(int session, int roleID, int suiteID)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " gain new suite " + suiteID);
		tms.sendPacket(session, new Packet.S2M.GainNewSuite(roleID, suiteID));
	}
	
	public void notifyMapRoleUpdateEquip(int session, int roleID, int wid, SBean.DBEquip equip)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update equip");
		tms.sendPacket(session, new Packet.S2M.UpdateEquip(roleID, wid, equip));
	}
	
	public void notifyMapRoleUpdateEquipPart(int session, int roleID, SBean.DBEquipPart equipPart)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update equip part");
		tms.sendPacket(session, new Packet.S2M.UpdateEquipPart(roleID, equipPart));
	}
	
	public void notifyMapRoleUpdateSealGrade(int session, int roleID, int sealGrade)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update seal grade " + sealGrade);
		tms.sendPacket(session, new Packet.S2M.UpdateSealGrade(roleID, sealGrade));
	}
	
	public void notifyMapRoleUpdateSealSkills(int session, int roleID, Map<Integer, Integer> skills)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update seak skills " + skills);
		tms.sendPacket(session, new Packet.S2M.UpdateSealSkills(roleID, skills));
	}
	
	public void notifyMapRoleUpdateWeapon(int session, int roleID, SBean.DBWeapon weapon)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update weapon");
		tms.sendPacket(session, new Packet.S2M.UpdateWeapon(roleID, weapon));
	}
	
	public void notifyMapRoleUpdateCurWeapon(int session, int roleID, int curWeapon)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update cur weapon");
		tms.sendPacket(session, new Packet.S2M.UpdateCurWeapon(roleID, curWeapon));
	}
	
	public void notifyMapRoleSetWeaponForm(int session, int roleID, int weaponID, byte form)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " set weapon " + weaponID + " form " + form);
		tms.sendPacket(session, new Packet.S2M.SetWeaponForm(roleID, weaponID, form));
	}
	
	public void notifyMapRoleUpdatePet(int session, int roleID, SBean.FightPet fightPet)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update pet");
		tms.sendPacket(session, new Packet.S2M.UpdatePet(roleID, fightPet));
	}
	
	public void notifyMapRoleUpdateCurPetSpirit(int session, int roleID, int petID, int index, SBean.PetSpirit spirit)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update pet " + petID + " spirit ");
		tms.sendPacket(session, new Packet.S2M.UpdateCurPetSpirit(roleID, petID, index, spirit));
	}
	
	public void notifyMapRoleUpdateSpirit(int session, int roleID, SBean.DBSpirit spirit)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update spirit");
		tms.sendPacket(session, new Packet.S2M.UpdateSpirit(roleID, spirit));
	}
	
	public void notifyMapRoleUpdateCurSpirit(int session, int roleID, Set<Integer> curSpirit){
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update cur spirit");
		tms.sendPacket(session, new Packet.S2M.UpdateCurSpirit(roleID, curSpirit));
	}
	
	public void notifyMapRoleUpdateSkill(int session, int roleID, SBean.DBSkill skill)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update skill");
		tms.sendPacket(session, new Packet.S2M.UpdateSkill(roleID, skill));
	}
	
	public void notifyMapRoleUpdateCurSkills(int session, int roleID, List<Integer> skills)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update current skills");
		tms.sendPacket(session, new Packet.S2M.UpdateCurSkills(roleID, skills));
	}
	
	public void notifyMapRoleUpdateCurUniqueSkill(int session, int roleID, int curUniqueSkill)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update cur uniqueskill " + curUniqueSkill);
		tms.sendPacket(session, new Packet.S2M.UpdateCurUniqueSkill(roleID, curUniqueSkill));
	}
	
	public void notifyMapRoleUpdateBuff(int session, int roleID, SBean.DBBuff buff)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update buff");
		tms.sendPacket(session, new Packet.S2M.UpdateBuff(roleID, buff));
	}
	
	public void notifyMapRoleAddBuff(int session, int roleID, int buffID)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " add buff " + buffID);
		tms.sendPacket(session, new Packet.S2M.AddBuff(roleID, buffID));
	}
	
	public void notifyMapRoleUpdateLevel(int session, int roleID, int level)
	{
		gs.getLogger().debug("notify map role " + roleID + " update level " + level);
		tms.sendPacket(session, new Packet.S2M.UpdateLevel(roleID, level));
	}
	
	public void notifyMapRoleAddHp(int session, int roleID, int hp)
	{
		gs.getLogger().debug("notify map session " + session + " role " + roleID + " add hp");
		tms.sendPacket(session, new Packet.S2M.AddHp(roleID, hp));
	}

	public void notifyMapRolePetAddHp(int session, int roleID, int petId, int hp)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " add pet " + petId + " hp");
		tms.sendPacket(session, new Packet.S2M.AddPetHp(roleID, petId, hp));
	}
	
	public void notifyMapRoleStartMine(int session, int roleID, int mineId, int mineInstance)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " start mine " + mineId + ", " + mineInstance);
		tms.sendPacket(session, new Packet.S2M.StartMine(roleID, mineId, mineInstance));
	}
	
	public void notifyMapRoleRevive(int session, int roleID, boolean fullHp)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " revive fullHp ");
		tms.sendPacket(session, new Packet.S2M.RoleRevive(roleID, (byte)(fullHp ? 1 : 0)));
	}
	
	public void notifyMapRoleUpdateTeam(int session, int roleID, SBean.Team team)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " update team " + team.id);
		tms.sendPacket(session, new Packet.S2M.UpdateTeam(roleID, team));
	}
	
//	public void notifyMapTeamDissolve(int session, int teamId)
//	{
//		gs.getLogger().debug("notify map dissolve team " + teamId);
//		tms.sendPacket(session, new Packet.S2M.TeamDissolve(teamId));
//	}
	
//	public void notifyMapRoleSummonFightPet(int session, int roleID, Map<Integer, SBean.FightPet> pets)
//	{
//		gs.getLogger().debug("notify map role " + roleID + " summon fight pets ");
//		tms.sendPacket(session, new Packet.S2M.SummonFightPets(roleID, pets));
//	}

	public void notifyMapRoleChangeCurPets(int session, int roleID, Map<Integer, SBean.FightPet> pets)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " change fight pets ");
		tms.sendPacket(session, new Packet.S2M.ChangeCurPets(roleID, pets));
	}
	
	public void notifyMapRoleUnSummonPets(int session, int roleID)
	{
		gs.getLogger().trace("notify map session " + session + " role " + roleID + " unsummon pets");
		tms.sendPacket(session, new Packet.S2M.UnSummonCurPets(roleID));
	}
	
//	public void notifyMapRoleReviveFightPet(int session, int roleID, int petID)
//	{
//		gs.getLogger().debug("notify map role " + roleID + " revive fight pet ");
//		tms.sendPacket(session, new Packet.S2M.ReviveFightPet(roleID, petID));
//	}
	
	public void notifyMapRoleUpdateSectBrief(int sessionid, int roleID, SBean.SectBrief sectBrief)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update sect brief : id " + sectBrief.sectID + " name "+ sectBrief.sectName + " position " + sectBrief.sectPosition);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateSectBrief(roleID, sectBrief));
	}
	
	public void notifyMapRoleUpdateSectAura(int sessionid, int roleID, int auraID, int auraLvl)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update aura " + auraID + " , " + auraLvl);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateSectAura(roleID, auraID, auraLvl));
	}
	
	public void notifyMapRoleResetSectAuras(int sessionid, int roleID, Map<Integer, Integer> sectAuras)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " sync sectaura ...");
		tms.sendPacket(sessionid, new Packet.S2M.ResetSectAuras(roleID, sectAuras));
	}
	
//	public void notifyMapRoleUpdateClanDiziTangAttr(int sessionid, int roleID, SBean.ClanOwnerAttriAddition attr)
//	{
//		gs.getLogger().trace("notify map role " + roleID + " update clan dizitang attr ...");
//		tms.sendPacket(sessionid, new Packet.S2M.UpdateClanDiziTang(roleID, attr));
//	}
	
	public void notifyMapRoleUpdatePKInfo(int sessionid, int roleID, int pkMode, int value)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " sync pk info mode=" + pkMode + ", value=" + value);
		tms.sendPacket(sessionid, new Packet.S2M.UpdatePKInfo(roleID, pkMode, value));
	}
	
	public void notifyMapRoleUpdateCurDIYSkill(int sessionid, int roleID, SBean.DBDIYSkillData curDIYSkill)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update curDIYSkill ...");
		tms.sendPacket(sessionid, new Packet.S2M.UpdateCurDIYSkill(roleID, curDIYSkill));
	}
	
	public void notifyMapRoleUpdateTransformInfo(int sessionid, int roleID, byte transformLevel, byte BWType)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " UpdateTransformInfo " + transformLevel + " , " + BWType);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateTransformInfo(roleID, transformLevel, BWType));
	}
	
	public void notifyMapRoleUpdateHorse(int sessionid, int roleID, SBean.HorseInfo info)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update horse " + info.id + " info");
		tms.sendPacket(sessionid, new Packet.S2M.UpdateHorseInfo(roleID, info));
	}
	
	public void notifyMapRoleUpdateHorseSkill(int sessionid, int roleID, int skillID, int skillLvl)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update horse skill " + skillID + " , " + skillLvl);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateHorseSkill(roleID, skillID, skillLvl));
	}
	
	public void notifyMapRoleUpdateCurUseHorse(int sessionid, int roleID, int hid)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update cur use horse " + hid);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateCurUseHorse(roleID, hid));
	}
	
	public void notifyMapRoleChangeHorseShow(int sessionid, int roleID, int hid, int showID)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " change horse show " + showID);
		tms.sendPacket(sessionid, new Packet.S2M.ChangeHorseShow(roleID, hid, showID));
	}
	
	public void notifyMapRoleUpdateMedal(int sessionid, int roleID, int medal, byte state)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update medal " + medal + " , " + state);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateMedal(roleID, medal, state));
	}
	
	public void notifyMapRoleUpWearFashions(int sessionid, int roleID, int type, int fashionID, int isShow)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " up wear fashions " + type + " , " + fashionID + " show " + isShow);
		tms.sendPacket(sessionid, new Packet.S2M.UpWearFashion(roleID, type, fashionID, isShow));
	}
	
	public void notifyMapRoleUpdateAlterState(int sessionid, int roleID, SBean.DBAlterState alterState)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update alter state [" + alterState.alterID + " , " + alterState.attrEndTime + "]");
		tms.sendPacket(sessionid, new Packet.S2M.UpdateAlterState(roleID, alterState));
	}
	
	public void notifyMapRoleUpdateGrasp(int sessionid, int roleID, int graspID, int level)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update grasp " + graspID + " , " + level);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateRoleGrasp(roleID, graspID, level));
	}
	
	public void notifyMapRoleUpdateRareBook(int sessionid, int roleID, int bookID, int level)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update rare book " + bookID + " , " + level);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateRareBook(roleID, bookID, level));
	}
	
	public void notifyMapRoleUpdateTitle(int sessionid, int roleID, int titleID, boolean add)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update title " + titleID + " add  " + add);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateRoleTitle(roleID, titleID, add ? (byte)1 : (byte) 0));
	}
	
	public void notifyMapRoleUpdateCurTitle(int sessionid, int roleID, int titleID, int titleType)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update cur title " + titleID + " titleType  " + titleType);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateRoleCurTitle(roleID, titleID, titleType));
	}
	
	public void notifyMapRoleUpdatePetAchieve(int sessionid, int roleID, Set<Integer> achieves)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update pet achieves ");
		tms.sendPacket(sessionid, new Packet.S2M.UpdatePetAchieve(roleID, achieves));
	}
	
	public void notifyMapRoleSyncCurRideHorse(int sessionid, int roleID, int horseID)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " sync cur ride horse " + horseID);
		tms.sendPacket(sessionid, new Packet.S2M.SyncCurRideHorse(roleID, horseID));
	}
	
	public void notifyMapRoleUpdateMulHorse(int sessionid, int leaderID, int pos, int memberID)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + leaderID + " update mulHorse " + pos + " , " + memberID);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateMulHorse(leaderID, pos, memberID));
	}
	
	public void notifyMapRoleSpawnSceneMonster(int sessionid, int mapID, int mapInstance, int roleID, int pointID)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " spawn scene monster " + pointID);
		tms.sendPacket(sessionid, new Packet.S2M.SpawnSceneMonster(mapID, mapInstance, roleID, pointID));
	}
	
	public void notifyMapClearSpawnSceneMonster(int sessionid, int roleID, int monsterID)
	{
		gs.getLogger().trace("notify map session " + sessionid + " clear role " + roleID + " scene monster " + monsterID);
		tms.sendPacket(sessionid, new Packet.S2M.ClearSceneMonster(roleID, monsterID));
	}
	
	public void notifyMapRoleChangeArmor(int sessionid, int roleID, SBean.ArmorFightData armor)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " change armor");
		tms.sendPacket(sessionid, new Packet.S2M.ChangeArmor(roleID, armor));
	}

	public void notifyMapRoleUpdateArmorLevel(int sessionid, int roleID, int level)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update armor level " + level);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateArmorLevel(roleID, level));
	}
	
	public void notifyMapRoleUpdateArmorRank(int sessionid, int roleID, int rank)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update armor rank " + rank);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateArmorRank(roleID, rank));
	}
	
	public void notifyMapRoleUpdateArmorRune(int sessionid, int roleID, int groupindex, List<Integer> runes)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update armor solt group " + groupindex);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateArmorRune(roleID, groupindex, runes));
	}
	
	public void notifyMapRoleUpdateArmorTalentPoint(int sessionid, int roleID, Map<Integer, Integer> talentPoint)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update talent point");
		tms.sendPacket(sessionid, new Packet.S2M.UpdateTalentPoint(roleID, talentPoint));
	}
	
	public void notifyMapRoleUpdateStayWith(int sessionid, int roleID, SBean.MulRoleInfo mulRoleInfo)
	{
		gs.getLogger().trace("notify map session " + sessionid + " role " + roleID + " update stay with info leaderID " + mulRoleInfo.leader);
		tms.sendPacket(sessionid, new Packet.S2M.UpdateStayWith(roleID, mulRoleInfo));
	}

	public void notifyMapUpdateRoleWeaponSkills(int sid, int roleId, int weaponId, List<Integer> skills)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update weapon " + weaponId + " skills");
		tms.sendPacket(sid, new Packet.S2M.UpdateRoleWeaponSkill(roleId, weaponId, skills));
	}
	
	public void notifyMapUpdateRolePerfectDegree(int sid, int roleId, int perfectDegree)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update perfect degree " + perfectDegree);
		tms.sendPacket(sid, new Packet.S2M.UpdateRolePerfectDegree(roleId, perfectDegree));
	}
	
	public void notifyMapUpdateMarriageSkillLevel(int sid, int roleId, int skillId, int skillLevel)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update marriage skill " + skillId + " level " + skillLevel);
		tms.sendPacket(sid, new Packet.S2M.UpdateRoleMarriageSkillLevel(roleId, skillId, skillLevel));
	}
	
	public void notifyMapUpdateMarriageSkillInfo(int sid, int roleId, Map<Integer, Integer> skills, int partnerId)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update marriage skills");
		tms.sendPacket(sid, new Packet.S2M.UpdateRoleMarriageSkillInfo(roleId, skills, partnerId));
	}
	
	public void notifyMapMarriageLevelChange(int sid, int roleID, int newLevel)
	{
		gs.getLogger().debug("notify map session " + sid + " role " + roleID + " marriage level change " + newLevel);
		tms.sendPacket(sid, new Packet.S2M.MarriageLevelChange(roleID, newLevel));
	}
	
	public void notifyMapUpdateRoleHeirloomDisplay(int sid, int roleId, int dispaly)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update heirloom diaplay " + (dispaly == 1));
		tms.sendPacket(sid, new Packet.S2M.UpdateRoleHeirloomDisplay(roleId, dispaly));
	}

	public void notifyMapUpdateRolePetSkills(int sid, int roleId, int petId, List<Integer> skills)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update pet " + petId + " skills");
		tms.sendPacket(sid, new Packet.S2M.UpdateRolePetSkill(roleId, petId, skills));
	}

	public void notifyMapUpdateRoleWeaponTalents(int sid, int roleId, int weaponId, List<Integer> talent)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update weapon " + weaponId + " talents");
		tms.sendPacket(sid, new Packet.S2M.UpdateRoleWeaponTalent(roleId, weaponId, talent));
	}
	
	public void notifyMapSyncRoleItemProps(int sid, int roleId, HashMap<Integer, Integer> itemProps)
	{
		gs.getLogger().trace("notify map session " + sid + " role " + roleId + " update item props");
		tms.sendPacket(sid, new Packet.S2M.SyncRoleItemProps(roleId, itemProps));
	}
	
	public void notifyMapSyncRoleTaskDrop(int sid, int roleID, Map<Integer, Integer> taskDrop)
	{
		gs.getLogger().debug("notify map session " + sid + " role " + roleID +" sync task drop " + taskDrop);
		tms.sendPacket(sid, new Packet.S2M.SyncTaskDrop(roleID, taskDrop));
	}
	
	public void notifyMapSyncRoleWeaponOpen(int sid, int roleID, int weaponID)
	{
		gs.getLogger().info("notify map session " + sid + " role " + roleID + " sync role weapon " + weaponID + " open");
		tms.sendPacket(sid, new Packet.S2M.SyncWeaponOpen(roleID, weaponID));
	}
	
	public void notifyMapStartMarriageParade(int sid, int mapID, int mapInstance, int carID, SBean.RoleOverview man, SBean.RoleOverview woman)
	{
		gs.getLogger().info("notify map session " + sid + " man " + man.id + " woman " + woman.id + " start marriage parad carID " + carID);
		tms.sendPacket(sid, new Packet.S2M.StartMarriageParade(mapID, mapInstance, carID, man, woman));
	}
	
	public void notifyMapStartMarriageBanquet(int sid, int roleID, int mapID, int mapInstance, int banquet)
	{
		gs.getLogger().info("notify map session " + sid + " role " + roleID + " start marriage banquet " + banquet);
		tms.sendPacket(sid, new Packet.S2M.StartMarriageBanquet(roleID, mapID, mapInstance, banquet));
	}
	
	public void notifyMapRoleDMGTransferPointLvlsUpdate(int sid, int roleID, Map<Integer, Integer> pointLvls)
	{
		gs.getLogger().debug("notify map session " + sid + " role " + roleID + " dmgTransfer point lvls update ");
		tms.sendPacket(sid, new Packet.S2M.RoleDMGTransferUpdate(roleID, pointLvls));
	}
	
	public void notifyMapCreateRobotHero(int sid, int mapID, int mapInstance, SBean.FightRole robot, Map<Integer, SBean.FightPet> curFightPets, int spawnPoint)
	{
		gs.getLogger().debug("notify map session " + sid + " map[" + mapID + " , " + mapInstance + "] create robot hero " + robot.base.roleID + " in spawnPoint " + spawnPoint);
		tms.sendPacket(sid, new Packet.S2M.CreateRobotHero(robot, mapID, mapInstance, curFightPets, spawnPoint));
	}
	
	public void notifyMapDestroyRobotHero(int sid, int mapID, int mapInstance, int roleID)
	{
		gs.getLogger().debug("notify map session " + sid + " map[" + mapID + " , " + mapInstance + "] destroy robot hero " + roleID);
		tms.sendPacket(sid, new Packet.S2M.DestroyRobotHero(mapID, mapInstance, roleID));
	}
	
	public void notifyMapCreateStele(int sid, int steleType, int index, int remainTimes)
	{
		gs.getLogger().info("notify map session " + sid + " create stele [" + steleType + " , " + index + "] remainTimes " + remainTimes);
		tms.sendPacket(sid, new Packet.S2M.SyncCreateStele(steleType, index, remainTimes));
	}
	
	public void notifyMapDestroyStele(int sid, int steleType, int index)
	{
		gs.getLogger().info("notify map session " + sid + " destroy stele [" + steleType + " , " + index + "]");
		tms.sendPacket(sid, new Packet.S2M.SyncDestroyStele(steleType, index));
	}
	
	public void notifyMapJusticeNpcShow(int sid, int posIndex)
	{
		gs.getLogger().info("notify map session " + sid + " show justice npc [" + posIndex + "]");
		tms.sendPacket(sid, new Packet.S2M.SyncJusticeNpcShow(posIndex));
	}
	
	public void notifyMapJusticeNpcLeave(int sid, int posIndex)
	{
		gs.getLogger().info("notify map session " + sid + " delete justice npc [" + posIndex + "]");
		tms.sendPacket(sid, new Packet.S2M.SyncJusticeNpcLeave(posIndex));
	}
	
	public void syncRoleVipLevel(int sid, int roleId, int vipLevel)
	{
		gs.getLogger().debug("notify map session " + sid + " sync role " + roleId + " viplevel " + vipLevel);
		tms.sendPacket(sid, new Packet.S2M.SyncRoleVipLevel(roleId, vipLevel));
	}
	
	public void syncRoleWizardPet(int sid, int roleId, int petId)
	{
		gs.getLogger().debug("notify map session " + sid + " sync role " + roleId + " cur wizard pet " + petId);
		tms.sendPacket(sid, new Packet.S2M.SyncRoleCurWizardPet(roleId, petId));
	}
	
	public void syncEmergencyLastTime(int sid, int mapId, int mapInstance, int endTime)
	{
		gs.getLogger().info("notify map session " + sid + " sync emergency last time " + endTime);
		tms.sendPacket(sid, new Packet.S2M.SyncEmergencyLastTime(mapId, mapInstance, endTime));
	}
	
	public void notifyMapUpdateRoleSpecialCardAttr(int sid, int roleID, Map<Integer, Integer> attrs)
	{
		gs.getLogger().debug("notify map session " + sid + " role " + roleID + " update special card attr");
		tms.sendPacket(sid, new Packet.S2M.UpdateRoleSpecialCardAttr(roleID, attrs));
	}
	
	public void notifyMapRoleShowProps(int sid, int roleID, int propID)
	{
		gs.getLogger().debug("notify map session " + sid + " role " + roleID + " show prop " + propID);
		tms.sendPacket(sid, new Packet.S2M.RoleShowProps(roleID, propID));
	}
	
	public void notifyMapGMCommond(int sid, int roleID, String iType, int iArg1, int iArg2, int iArg3, String sArg)
	{
		gs.getLogger().debug("notify map session " + sid + " map gm commond role " + roleID + " iArg1 " + iArg1 + " iArgs " + iArg2 + " iArg3 " + iArg3 + " sArg " + sArg);
		tms.sendPacket(sid, new Packet.S2M.GMCommand(roleID, iType, iArg1, iArg2, iArg3, sArg));
	}
	
	public void notifyMapRoleRedNamePunish(int sid, int roleID)
	{
		gs.getLogger().debug("notify map session " + sid + " role " + roleID + " red name punish");
		tms.sendPacket(sid, new Packet.S2M.RoleRedNamePunish(roleID));
	}
	
//	public void notifyMapCreateMapInstance(int session, int tag, int mapID)
//	{
//		gs.getLogger().debug("notify map create map " + mapID + " instance ");
//		tms.sendPacket(session, new Packet.S2M.CreateMapInstance(tag, mapID));
//	}
 
	
//	public void notifyClanReportServerTimeOffset(int timeOffset)
//	{
//		gs.getLogger().info("notify clan report gs time offset " + timeOffset);
//		tcc.sendPacket(new Packet.S2Clan.ReportTimeOffset(timeOffset));
//	}
//	
//	public void notifyOnRoleLoginTaskTask(int tag, SBean.RoleOverview overview)
//	{
//		gs.getLogger().debug("notify clan role " + overview.id + " ClanOnRoleLoginReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOnRoleLoginReq(tag, overview));
//	}
//	
//	public void notifyClanQueryRoleClans(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " QueryRoleClansReq...");
//		tcc.sendPacket(new Packet.S2Clan.QueryRoleClansReq(tag, roleId));
//	}
//	public void notifySyncClanInfo(int tag, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " SyncClanReq...");
//		tcc.sendPacket(new Packet.S2Clan.SyncClanReq(tag, roleId, clanId));
//	}
//	public void notifySearchAllClansTask(int tag, int roleId, int selfServer)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " QueryClansReq...");
//		tcc.sendPacket(new Packet.S2Clan.QueryClansReq(tag, roleId, selfServer));
//	}
//	public void notifySearchClanByIdTask(int tag, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " QueryClanByIdReq...");
//		tcc.sendPacket(new Packet.S2Clan.QueryClanByIdReq(tag, roleId, clanId));
//	}
//	public void notifySearchClanByNameTask(int tag, int roleId, String name)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " QueryClanByNameReq...");
//		tcc.sendPacket(new Packet.S2Clan.QueryClanByNameReq(tag, roleId, name));
//	}
//	public void notifyCreateClanTask(int tag, SBean.GlobalRoleOverview creater, String name, int isFemale)
//	{
//		gs.getLogger().debug("notify clan role " + creater.role.id + " CreateClanReq...");
//		tcc.sendPacket(new Packet.S2Clan.CreateClanReq(tag, creater, name, isFemale));
//	}
//	public void notifyApplyAddTask(int tag, SBean.GlobalRoleOverview goverview, int clanId){
//		gs.getLogger().debug("notify clan role " + goverview.role.id + " ApplyAddClanReq...");
//		tcc.sendPacket(new Packet.S2Clan.ApplyAddClanReq(tag, goverview, clanId));
//	}
//	public void notifyRatifyAddClanTask(int tag, int roleId, int isAgree, int memGsId, int memId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " RatifyAddClanReq...");
//		tcc.sendPacket(new Packet.S2Clan.RatifyAddClanReq(tag, roleId, 0, isAgree, memGsId, memId));
//	}
//	public void notifyGetClanApplicationsTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " GetClanApplicationsReq...");
//		tcc.sendPacket(new Packet.S2Clan.GetClanApplicationsReq(tag, roleId));
//	}
//	public void notifyGetClanMemberTask(int tag, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " GetClanMembersReq...");
//		tcc.sendPacket(new Packet.S2Clan.GetClanMembersReq(tag, roleId, clanId));
//	}
//	public void notifyClanKickMemberTask(int tag, int roleId, int memberId, int memberGsId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanKickMemberReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanKickMemberReq(tag, roleId, memberId, memberGsId));
//	}
//	public void notifyClanMemberLeaveTask(int tag, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanMemberLeaveReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanMemberLeaveReq(tag, roleId, clanId));
//	}
//	public void notifyClanDisbandTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDisbandReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDisbandReq(tag, roleId));
//	}
//	public void notifyClanCancelDisbandTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanCancelDisbandReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanCancelDisbandReq(tag, roleId));
//	}
//	public void notifyClanAppliedTask(int tag, int roleId, List<Integer> clanIds)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanAppliedReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanAppliedReq(tag, roleId, clanIds));
//	}
//	public void notifyClanAppointElderTask(int tag, int roleId, int memId, int memGsId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanAppointElderReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanAppointElderReq(tag, roleId, memId, memGsId));
//	}
//	public void notifyClanUplevelTask(int tag, int roleId, int curLevel)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanUplevelReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanUplevelReq(tag, roleId, curLevel));
//	}
//	public void notifyClanRecruitTask(int tag, int roleId, int type)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanRecruitReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanRecruitReq(tag, roleId, type));
//	}
//	public void notifyClanCancelElderTask(int tag, int roleId, int memberId, int memberGsId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanCancelElderReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanCancelElderReq(tag, roleId, memberId, memberGsId));
//	}
//	public void notifyClanShoutuTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanShoutuReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanShoutuReq(tag, roleId));
//	}
//	public void notifyClanShoutuSpeedupTask(int tag, int roleId, int count, int start)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanShoutuSpeedupReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanShoutuSpeedupReq(tag, roleId, count, start));
//	}
//	public void notifyClanShoutuFinishTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanShoutuFinishReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanShoutuFinishReq(tag, roleId));
//	}
//	public void notifyClanBiwuStartTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBiwuStartReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBiwuStartReq(tag, roleId));
//	}
//	public void notifyClanBiwuSpeedupTask(int tag, int roleId, int count, int startTime)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBiwuSpeedupReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBiwuSpeedupReq(tag, roleId, count, startTime));
//	}
//	public void notifyClanBiwuFinishTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBiwuFinishReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBiwuFinishReq(tag, roleId));
//	}
//	public void notifyClanBushiStartTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBushiStartReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBushiStartReq(tag, roleId));
//	}
//	public void notifyClanChuandaoStartTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanChuandaoStartReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanChuandaoStartReq(tag, roleId));
//	}
//	public void notifyClanRushTollgateToExpTask(int tag, int roleId, int dzid, int count, int useMoney)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanRushTollgateToExpReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanRushTollgateToExpReq(tag, roleId, dzid, count, useMoney));
//	}
//	public void notifyClanRushTollgateToItemTask(int tag, int roleId, int dzid, List<Integer> items)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanRushTollgateToItemReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanRushTollgateToItemReq(tag, roleId, dzid, items));
//	}
//	
//	public void notifyClanOwnerAttriAdditionTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOwnerAttriAdditionReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOwnerAttriAdditionReq(tag, roleId));
//	}
//	
//	public void notifySyncSelfClanTaskTask(int tag, int roleId, int clanId, int taskId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSyncSelfTaskReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSyncSelfTaskReq(tag, roleId, clanId, taskId));
//	}
//	public void notifySyncClanTaskLibTask(int tag, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSyncTaskLibReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSyncTaskLibReq(tag, roleId));
//	}
//	public void notifyAutoRefreshTaskTask(int tag, int roleId, int taskId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanAutoRefreshTaskReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanAutoRefreshTaskReq(tag, roleId, taskId));
//	}
//	public void notifyClanTaskReceiveTask(int tag, int roleId, int taskId, List<Integer> jydzs, List<Integer> pets, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanReceiveTaskReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanReceiveTaskReq(tag, roleId, taskId, jydzs, pets, clanId));
//	}
//	public void notifyClanGetOwnerFightDataTask(int tag, int roleId, int ownerId, int ownerServerId, int ownerPet)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOwnerFightDataReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOwnerFightDataReq(tag, roleId, ownerId, ownerServerId, ownerPet));
//	}
//	public void notifyClanGetOwnerFightDataForwardTask(Packet.Clan2S.ClanOwnerFightDataForwardReq packet, SBean.FightPet ownerFightPet, SBean.PetHost ownerFightPetHost)
//	{
//		gs.getLogger().debug("notify clan role " + packet.getRoleId() + " ClanOwnerFightDataForwardRes...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOwnerFightDataForwardRes(packet.getTagId(), packet.getRoleId(), packet.getServerId(), ownerFightPet, ownerFightPetHost));
//	}
//	
//	public void notifyClanGetEnemyFightDataTask(int tag, int roleId, int enemyId, int enemyServerId, List<Integer> enemyPet)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanEnemyFightDataReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanEnemyFightDataReq(tag, roleId, enemyId, enemyServerId, enemyPet));
//	}
//	
//	public void notifyClanGetEnemyFightDataForwardTask(Packet.Clan2S.ClanEnemyFightDataForwardReq packet, BattleArray ba)
//	{
//		gs.getLogger().debug("notify clan role " + packet.getRoleId() + " ClanEnemyFightDataForwardReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanEnemyFightDataForwardRes(packet.getTagId(), packet.getRoleId(), packet.getServerId(), ba));
//	}
//	
//	
//	public void notifyClanTaskFinishTask(int tag, int roleId, int taskId, int clanId, Set<Integer> jydzs)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanFinishTaskReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanFinishTaskReq(tag, roleId, taskId, clanId, jydzs));
//	}
//	public void notifyClanTaskDiscardTask(int tag, int roleId, int taskId, int clanId, int useDZ)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDiscardTaskReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDiscardTaskReq(tag, roleId, taskId, clanId, useDZ));
//	}
//	public void notifyClanSyncHistoryTask(int tag, int roleId, int type, int clanId){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSyncHistoryReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSyncHistoryReq(tag, roleId, type, clanId));
//	}
//	public void notifyRecoverGenDiscipleTask(int tag, int roleId){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanRecoverGenDiscipleReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanRecoverGenDiscipleReq(tag, roleId));
//	}
//	
//	public void notifyClanOreBuildUpLevelTask(int tag, int roleId, int type, int level){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOreBuildUpLevelReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOreBuildUpLevelReq(tag, roleId, type, level));
//	}
//	public void notifySyncClanOreTask(int tag, int roleId, int clanId){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSyncOreReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSyncOreReq(tag, roleId, clanId));
//	}
//	public void notifyClanOreOccupyTask(int tag, int roleId, int type, int clanId){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOreOccupyReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOreOccupyReq(tag, roleId, type, clanId));
//	}
//	public void notifyClanOreOccupyFinishTask(int tag, int roleId, int type, int clanId){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOreOccupyFinishReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOreOccupyFinishReq(tag, roleId, type, clanId));
//	}
//	public void notifyClanSearchOreTask(int tag, int roleId, int level){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSearchOreReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSearchOreReq(tag, roleId, level));
//	}
//	public void notifyClanSearchOreForwardTask(int tag, int serverId, int oreServerId, SBean.DBOreRobTeamGlobal ore)
//	{
//		gs.getLogger().debug("notify clan role ClanSearchOreForwardRes...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSearchOreForwardRes(tag, serverId, oreServerId, ore));
//	}
//	
//	public void notifyClanOreHarryForwardTask(int tag, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, BattleArray ba)
//	{
//		gs.getLogger().debug("notify clan role ClanOreHarryForwardRes...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOreHarryForwardRes(tag, self, enemy, ba));
//	}
//	
//
//	
//	public void notifyClanSearchOreSyncTask(int tag, int roleId, int clanId, int memberId, int serverId, int oreType){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSearchOreSyncReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSearchOreSyncReq(tag, roleId, clanId, memberId, serverId, oreType));
//	}
//	public void notifyClanOreOwnerPetSyncTask(int tag, int roleId, int clanId){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOreOwnerPetSyncReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOreOwnerPetSyncReq(tag, roleId, clanId));
//	}
//	public void notifyClanOreBorrowPetTask(int tag, int roleId, int clanId, int petId){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOreBorrowPetReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOreBorrowPetReq(tag, roleId, clanId, petId));
//	}
//	public void notifyClanOreHarryTask(int tag, int roleId, int clanId, int memberId, int serverId, int oreType){
//		gs.getLogger().debug("notify clan role " + roleId + " ClanOreHarryReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanOreHarryReq(tag, roleId, clanId, memberId, serverId, oreType));
//	}
//	
//	public void notifyClanBuyDoPowerTask(int tag, int roleId, int level)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBuyDoPowerReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBuyDoPowerReq(tag, roleId, level));
//	}
//	
//	public void notifyClanGetEliteDiscipleTask(int tag, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanGetEliteDiscipleReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanGetEliteDiscipleReq(tag, roleId, clanId));
//	}
//	
//	public void notifyClanGetBaseRankTask(int tagId, int roleId, int gType, int clanId, int rankType)
//	{
//		gs.getLogger().debug("notify clan " + clanId + " role " + roleId + " ClanGetBaseRankReq ...");
//		tcc.sendPacket(new Packet.S2Clan.ClanGetBaseRankReq(tagId, roleId, gType, clanId, rankType));
//	}
//	
//	public void notifyClanModifyRankTask(int tagId, int roleId, int level ,int fightPower, int charm)
//	{
//		gs.getLogger().debug("notify role fightpower " + fightPower  + " role " + roleId + " ClanGetBaseRankReq ...");
//		tcc.sendPacket(new Packet.S2Clan.ClanModifyRankReq(tagId, roleId, level, fightPower, charm));
//	}
//	
//	public void notifyClanSetAttackTeamTask(int tagId, int roleId, Map<Integer, Integer> pets)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSetAttackTeamReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSetAttackTeamReq(tagId, roleId, pets));
//	}
//	
//	public void notifyClanSetDefendTeamTask(int tagId, int roleId, Map<Integer, Integer> pets)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanSetDefendTeamReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanSetDefendTeamReq(tagId, roleId, pets));
//	}
//	
//	public void notifyClanFindEnemyTask(int tagId, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan " + clanId + " role " + roleId + " ClanFindEnemyReq ...");
//		tcc.sendPacket(new Packet.S2Clan.ClanFindEnemyReq(tagId, roleId, clanId));
//	}
//	
//	public void notifyGetNearbyClanTask(int tagId, int roleId)
//	{
//		gs.getLogger().debug("notify role " + roleId + " ClanGetNearbyClanReq ...");
//		tcc.sendPacket(new Packet.S2Clan.ClanGetNearbyClanReq(tagId, roleId));
//	}
//	
//	public void notifyClanGetEnemyTask(int tagId, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan " + clanId + " role " + roleId + " ClanGetEnemyReq ...");
//		tcc.sendPacket(new Packet.S2Clan.ClanGetEnemyReq(tagId, roleId, clanId));
//	}
//	
//	public void notifyClanBattleAttackTask(int tagId, int roleId, int clanId, Map<Integer, SBean.PetOverview> pets, int roleLvl, int transform)
//	{
//		gs.getLogger().debug("notify clan " + clanId + " role " + roleId + " ClanBattleAttackReq ...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleAttackReq(tagId, roleId, clanId, pets, roleLvl, transform));
//	}
//	public void notifyClanBattleAttackForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, int xuantie, int yaocao, 
//			int cdTime, Map<Integer, Integer> enemyPets, List<SBean.PetOverview> selfPets)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleAttackForwardRes(tagId, self, enemy, xuantie, yaocao, cdTime, enemyPets, selfPets));
//	}
//	
//	public void notifyClanGetEnemyForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, SBean.RoleOverview overview, List<SBean.PetOverview> enemyPets)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanGetEnemyForwardRes(tagId, self, enemy, overview, enemyPets));
//	}
//	
//	public void notifyClanBattleSeekhelpTask(int tagId, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBattleSeekhelpReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleSeekhelpReq(tagId, roleId));
//	}
//	public void notifyClanBattleHelpTask(int tagId, int roleId, int clanId, Map<Integer, Integer> pets)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBattleHelpReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleHelpReq(tagId, roleId, clanId, pets));
//	}
//	public void notifyClanBattleHelpForwardTask(int tagId, int code, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, SBean.ClanBattleInfo attack, 
//            SBean.ClanBattleInfo defend)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleHelpForwardRes(tagId, code, self, enemy, attack, defend));
//	}
//	
//	public void notifyClanBattleHelpFightStartTask(int tagId, int roleId, int clanID, SBean.GlobalRoleId helpRole)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBattleHelpFightStartReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleHelpFightStartReq(tagId, roleId, clanID, helpRole));
//	}
//	
//	public void notifyClanBattleHelpFightEndTask(int tagId, int roleId, int selfClanId, int helpClanId,int defendClanId, int defendGsId, int value, int win, SBean.BattleArrayHp defend)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBattleHelpFightStartReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleHelpFightEndReq(tagId, roleId, selfClanId, helpClanId, defendClanId, defendGsId, value, win, defend));
//	}
//	
//	public void notifyClanBattleHelpFightStartForwardTask(int tagId, int code, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, 
//            SBean.BattleArray ba, SBean.ClanBattleInfo selfInfo)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleHelpFightStartForwardRes(tagId, code, self, enemy, ba, selfInfo));
//	}
//	public void notifyClanBattleHelpFightEndForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId help, int value)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleHelpFightEndForwardRes(tagId, self, help, value));
//	}
//	
//	public void notifyClanGetClanEnemyTaskForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, SBean.RoleOverview overview, List<SBean.PetOverview> petOverviews)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanGetTaskEnemyForwardRes(tagId, self, enemy, overview, petOverviews));
//	}
//	
//	public void notifyClanBattleFightExitForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleFightExitForwardRes(tagId, self, enemy));
//	}
//	
//	public void notifyApplyAddClanForwardForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ApplyAddClanForwardRes(tagId, self, enemy));
//	}
//	
//	public void notifyClanMovePositionTask(int tagId, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanMovePositionReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanMovePositionReq(tagId, roleId));
//	}
//	
//	public void notifyClanBattleFightEndForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, int value)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleFightEndForwardRes(tagId, self, enemy, value));
//	}
//	
//	public void notifyClanBattleFightStartTask(int tagId, int roleId, SBean.GlobalRoleId enemyRole)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBattleFightStartReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleFightStartReq(tagId, roleId, enemyRole));
//	}
//	public void notifyClanBattleFightStartForwardTask(int tagId, int code, SBean.GlobalRoleId self, SBean.GlobalRoleId enemy, 
//            SBean.BattleArray ba, int roleLvl)
//	{
//		gs.getLogger().debug("notify clan role ClanBattleFightStartForwardRes...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleFightStartForwardRes(tagId, code, self, enemy, ba, roleLvl));
//	}
//	
//	public void notifyClanBattleFightEndTask(int tagId, int roleId, SBean.GlobalRoleId enemyRole, int value, int win, int clanID, SBean.ClanBattleLogDesc desc)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBattleFightEndReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleFightEndReq(tagId, roleId, enemyRole, value, win, clanID, desc));
//	}
//	
//	public void notifyClanBattleFightExitTask(int tagId, int roleId, int clanID, SBean.GlobalRoleId enemyRole)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBattleFightExitReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBattleFightExitReq(tagId, roleId, clanID, enemyRole));
//	}
//	
//	public void notifyClanShareDiySkillTask(int tagId, int roleId, SBean.DBDiySkill diySkill)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDiyskillShareReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDiyskillShareReq(tagId, roleId, diySkill));
//	}
//	
//	public void notifyClanCancelShareDiySkillTask(int tagId, int roleId, int diyskillId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDiyskillCancelShareReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDiyskillCancelShareReq(tagId, roleId, diyskillId));
//	}
//	
//	public void notifyClanDiySkillBorrowTaskTask(int tagId, int roleId, int memId, int memGsid, int diyskillId, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDiyskillBorrowReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDiyskillBorrowReq(tagId, roleId, memId, memGsid, diyskillId, clanId));
//	}
//	
//	public void notifyClanDiySkillGetShareTask(int tagId, int roleId, int clanId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDiyskillGetShareReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDiyskillGetShareReq(tagId, roleId, clanId));
//	}
//	
//	public void notifyClanDiySkillShareAwardTask(int tagId, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDiyskillShareAwardReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDiyskillShareAwardReq(tagId, roleId));
//	}
//	
//	public void notifyClanBuyPrestigeTask(int tagId, int roleId, List<SBean.DummyGoods> items)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanBuyPrestigeReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanBuyPrestigeReq(tagId, roleId, items));
//	}
//	
//	
//	public void notifyClanGetClanTaskEnemyTask(int tagId, int roleId, int enemyId, int enemySevId, List<Integer> pets)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanGetTaskEnemyReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanGetTaskEnemyReq(tagId, roleId, enemyId, enemySevId, pets));
//	}
//	
//	public void notifyClanOreOwnerPetSyncForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId owner, List<SBean.PetOverview> pets)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanOreOwnerPetSyncForwardRes(tagId, self, owner, pets));
//	}
//	
//	public void notifyClanOreBorrowPetForwardTask(int tagId, SBean.GlobalRoleId self, SBean.GlobalRoleId owner, int ok)
//	{
//		tcc.sendPacket(new Packet.S2Clan.ClanOreBorrowPetForwardRes(tagId, self, owner, ok));
//	}
//	
//	
//	public void notifyClanDayRefreshTask(int tagId, int roleId)
//	{
//		gs.getLogger().debug("notify clan role " + roleId + " ClanDayRefreshReq...");
//		tcc.sendPacket(new Packet.S2Clan.ClanDayRefreshReq(tagId, roleId));
//	}
	
///////////////////////////////notify auction server/////////////////////////////////////////////////////////
	public void notifyAuctionReportServerTimeOffset(int timeOffset)
	{
		gs.getLogger().info("notify auction report gs time offset " + timeOffset);
		tac.sendPacket(new Packet.S2Auction.ReportTimeOffset(timeOffset));
	}
	
	public void notifyAuctionPutOnItems(int tagID, int roleID, SBean.DBConsignItems items)
	{
		gs.getLogger().debug("notify auction role " + roleID + " put on items");
		tac.sendPacket(new Packet.S2Auction.PutOnItemReq(tagID, roleID, items));
	}
	
	public void nofifyAuctionTimeOutPutOffItemsRes(int tagID, int errCode)
	{
		gs.getLogger().debug("notify auction time out put off items res " + errCode);
		tac.sendPacket(new Packet.S2Auction.TimeOutPutOffItemsRes(tagID, errCode));
	}
	
	public void notifyAuctionPutOffItems(int tagID, int roleID, int cid)
	{
		gs.getLogger().debug("notify auction role " + roleID + " put off items " + cid);
		tac.sendPacket(new Packet.S2Auction.PutOffItemsReq(tagID, roleID, cid));
	}
	
	public void notifyAuctionBuyItems(int tagID, int sellerID, int cid, int price)
	{
		gs.getLogger().debug("notify auction role buy seller " + sellerID + "'s items cid " + cid + " price " + price);
		tac.sendPacket(new Packet.S2Auction.BuyItemsReq(tagID, sellerID, cid, price));
	}
	
	public void notifyAuctionCheckCanBuyRes(int tagID, int errCode)
	{
		gs.getLogger().debug("notify auction check can buy errCode " + errCode);
		tac.sendPacket(new Packet.S2Auction.CheckCanBuyRes(tagID, errCode));
	}
	
	public void notifyAuctionItemsSync(int tagID, int itemType, int classType, int rank, int level, int order, int page, String name)
	{
		gs.getLogger().debug("notify auction sync type " + itemType + " items of page " + page);
		tac.sendPacket(new Packet.S2Auction.AuctionItemsSyncReq(tagID, itemType, classType, rank, level, order, page, name));
	}
	
	public void notifyAuctionSyncSelfItems(int tagID, int roleID)
	{
		gs.getLogger().debug("notify auction sync role " + roleID + "'s items ");
		tac.sendPacket(new Packet.S2Auction.SelfItemsSyncReq(tagID, roleID));
	}
	
	public void notifyAuctionItemPricesSyncReq(int tagID, int itemID)
	{
		gs.getLogger().debug("notify auction item " + itemID + " prices sync req");
		tac.sendPacket(new Packet.S2Auction.ItemPricesSyncReq(tagID, itemID));
	}
	
	public void notifyAuctionUpdateGroupBuyGoods(int activityID, int endTime, int gid, int count)
	{
		gs.getLogger().debug("notify auction group buy activity " + activityID + " update goods " + gid + " , " + count);
		tac.sendPacket(new Packet.S2Auction.UpdateGroupBuyGoods(activityID, endTime, gid, count));
	}
	
	public void notifyAuctionSyncGroupBuyLog(int activityID, int endTime, Map<Integer, Integer> log)
	{
		gs.getLogger().debug("notify auction sync group buy activity " + activityID + " log on server start");
		tac.sendPacket(new Packet.S2Auction.SyncGroupBuyGoods(activityID, endTime, log));
	}
///////////////////////////////notify auction server end/////////////////////////////////////////////////////
	public void notifyGlobalMapReportServerTimeOffset(int timeOffset)
	{
		gs.getLogger().info("notify global map report gs time offset " + timeOffset);
		tgmc.sendPacket(new Packet.S2GM.ReportTimeOffset(timeOffset));
	}
	
	public void sendGlobalMapLuaChannelPacket(int roleID, String data)
	{
		gs.getLogger().trace("forward global map packet: role " + roleID + " lua channel packet:[" + data + "]");
		tgmc.sendPacket(new Packet.S2GM.LuaChannel(roleID, data));
	}
	
	public void sendGlobalMapStrChannelPacket(int roleID, String data)
	{
		gs.getLogger().trace("forward global map packet: role " + roleID + " str channel packet:[" + data + "]");
		tgmc.sendPacket(new Packet.S2GM.StrChannel(roleID, data));
	}
	
	public void notifyGlobalMapRoleEnterMap(SBean.FightRole fightRole, int mapId, int mapInstance, SBean.Location location, int hp, int sp, int armorVal, 
			Map<Integer, SBean.DBBuff> buffs, Map<Integer, SBean.FightPet> pets, List<Integer> petSeq, SBean.PetHost host, SBean.PKInfo pkInfo, 
			SBean.Team team, int curRideHorse, SBean.MulRoleInfo mulRoleInfo, SBean.DBAlterState alterState, boolean isMainSpwanPos, int dayFailedStreak, int vipLevel, int curWizard, boolean canTakeDrop)
	{
		gs.getLogger().info("notify global map role " + fightRole.base.roleID + " enter new map " + mapId + " " + mapInstance);
		tgmc.sendPacket(new Packet.S2GM.EnterMap(fightRole, mapId, mapInstance, location, hp, sp, armorVal, buffs, pets, petSeq, host, pkInfo, team, curRideHorse, mulRoleInfo, 
												 alterState, isMainSpwanPos ? (byte)1 : (byte)0, dayFailedStreak, vipLevel, curWizard, (byte) (canTakeDrop ? 1 : 0)));
	}
	
	public void notifyGlobalMapRoleLeaveMap(int roleID, int mapId, int instanceId)
	{
		gs.getLogger().info("notify global map role " + roleID + " leave current map " + mapId + " " + instanceId);
		tgmc.sendPacket(new Packet.S2GM.LeaveMap(roleID));
	}
	
	public void notifyGlobalMapRoleUpdateActive(int roleID, boolean active)
	{
		gs.getLogger().info("notify global map role " + roleID + " update active " + active);
		tgmc.sendPacket(new Packet.S2GM.UpdateActive(roleID, active ? (byte)1 : (byte)0));
	}
	
	public void notifyGlobalMapRoleAddHp(int roleID, int hp)
	{
		gs.getLogger().trace("notify global map role " + roleID + " add hp");
		tgmc.sendPacket(new Packet.S2GM.AddHp(roleID, hp));
	}

	public void notifyGlobalMapRolePetAddHp(int roleID, int petId, int hp)
	{
		gs.getLogger().trace("notify global map role " + roleID + " add pet " + petId + " hp");
		tgmc.sendPacket(new Packet.S2GM.AddPetHp(roleID, petId, hp));
	}
	
	public void notifyGlobalMapRoleUseItemSkill(int roleID, int itemID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		gs.getLogger().trace("notify global map role " + roleID + " use item " + itemID + " skill");
		tgmc.sendPacket(new Packet.S2GM.RoleUseItemSkill(roleID, itemID, pos, rotation, targetID, targetType, ownerID, timeTick));
	}
	
	public void notifyGlobalMapRoleStartMine(int roleID, int mineID, int mineInstance)
	{
		gs.getLogger().debug("notify global map role " + roleID + " start mine " + mineID + ", " + mineInstance);
		tgmc.sendPacket(new Packet.S2GM.StartMine(roleID, mineID, mineInstance));
	}
	
	public void notifyGlobalMapRoleResetLocation(int roleID, int mapId, int instanceId, SBean.Location location)
	{
		gs.getLogger().info("notify global map role " + roleID + " reset current map " + mapId + " " + instanceId + " location");
		tgmc.sendPacket(new Packet.S2GM.ResetLocation(roleID, location));
	}
	
	public void notifyGlobalMapRoleUpdateCurSkills(int roleID, List<Integer> skills)
	{
		gs.getLogger().trace("notify global map role " + roleID + " update current skills");
		tgmc.sendPacket(new Packet.S2GM.UpdateCurSkills(roleID, skills));
	}
	
	public void notifyGlobalMapRoleUpdateCurSpirit(int roleID, Set<Integer> curSpirit)
	{
		gs.getLogger().trace("notify map role " + roleID + " update cur spirit");
		tgmc.sendPacket(new Packet.S2GM.UpdateCurSpirit(roleID, curSpirit));
	}
	
	public void notifyGlobalMapPickUpResult(int roleID, Set<Integer> dropIDs, int success)
	{
		gs.getLogger().debug("notify global map role " + roleID + " pick up " + dropIDs + " success " + success);
		tgmc.sendPacket(new Packet.S2GM.PickUpResult(roleID, dropIDs, success));
	}
	
	public void notifyGlobalMapUpdateMarriageSkillLevel(int roleId, int skillId, int skillLevel)
	{
		gs.getLogger().trace("notify global map role " + roleId + " update marriage skill " + skillId + " level " + skillLevel);
		tgmc.sendPacket(new Packet.S2GM.UpdateRoleMarriageSkillLevel(roleId, skillId, skillLevel));
	}
	
	public void notifyGlobalMapUpdateMarriageSkillInfo(int roleId, Map<Integer, Integer> skills, int partnerId)
	{
		gs.getLogger().trace("notify global map role " + roleId + " update marriage skills");
		tgmc.sendPacket(new Packet.S2GM.UpdateRoleMarriageSkillInfo(roleId, skills, partnerId));
	}
	
	public void notifyGlobalMapRoleRevive(int roleID, boolean fullHp)
	{
		gs.getLogger().trace("notify global map role " + roleID + " revive fullHp ");
		tgmc.sendPacket(new Packet.S2GM.RoleRevive(roleID, (byte)(fullHp ? 1 : 0)));
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void notifyFightReportServerTimeOffset(int timeOffset)
	{
		gs.getLogger().info("notify fight server report gs time offset " + timeOffset);
		tfc.sendPacket(new Packet.S2F.ReportTimeOffset(timeOffset));
	}
	
	public void notifyFightRoleJoinForceWar(int tagID, SBean.ForceWarJoin joinInfo, int forcewarType)
	{
		gs.getLogger().trace("notify fight server role " + joinInfo.overview.id + " join force war");
		tfc.sendPacket(new Packet.S2F.RoleJoinForceWarReq(tagID, joinInfo, forcewarType));
	}
	
	public void notifyFightTeamJoinForceWar(int tagID, List<SBean.ForceWarJoin> members, byte bwType, int forcewarType)
	{
		gs.getLogger().trace("notify fight server team join force war ");
		tfc.sendPacket(new Packet.S2F.TeamJoinForceWarReq(tagID, members, bwType, forcewarType));
	}
	
	public void notifyFightRoleQuitForceWar(int tagID, int roleID, byte bwType, int forcewarType)
	{
		gs.getLogger().trace("notify fight server role " + roleID + " quit force war");
		tfc.sendPacket(new Packet.S2F.RoleQuitForceWarReq(tagID, roleID, bwType, forcewarType));
	}
	
	public void notifyFightTeamQuitForceWar(int tagID, int roleID, byte bwType, int memberCount, int forcewarType)
	{
		gs.getLogger().trace("notify fight server role " + roleID + " team quit force war");
		tfc.sendPacket(new Packet.S2F.TeamQuitForceWarReq(tagID, roleID, bwType, memberCount, forcewarType));
	}
	
	public void sendMsgFight(int id, int mapID, int mapInstance, MessageInfo messageInfo)
	{
		gs.getLogger().trace("send fight server role " + id + " send a massage");
		tfc.sendPacket(new Packet.S2F.SendMsgFight(id, mapID, mapInstance, messageInfo));
	}
	
	public void sendMsgGlobalTeam(int roleID, MessageInfo messageInfo)
	{
		gs.getLogger().trace("notify fight server role " + roleID + " send global team msg");
		tfc.sendPacket(new Packet.S2F.SendMsgGlobalTeam(roleID, messageInfo));
	}
	
	public void notifyFightUpdateRankRole(int rankID, SBean.RankRole rankRole, int rankClearTime)
	{
		gs.getLogger().trace("notify fight server role " + rankRole.role.id + " update rank " + rankID + " rankRole ...");
		tfc.sendPacket(new Packet.S2F.UpdateFightRank(rankID, rankRole, rankClearTime));
	}
	
	public void notifyFightSingleJoinSuperArena(int tagID, SBean.SuperArenaJoin joinInfo, int arenaType, int grade)
	{
		gs.getLogger().debug("notify fight server role " + joinInfo.overview.id + " single join super arena");
		tfc.sendPacket(new Packet.S2F.SingleJoinSuperArenaReq(tagID, joinInfo, arenaType, grade));
	}
	
	public void notifyFightSingleQuitSuperArena(int tagID, int roleID, int arenaType, int grade)
	{
		gs.getLogger().debug("notify fight server role " + roleID + " single quit super arena");
		tfc.sendPacket(new Packet.S2F.SingleQuitSuperArenaReq(tagID, roleID, arenaType, grade));
	}
	
	public void notifyFightTeamJoinSuperArena(int tagID, List<SBean.SuperArenaJoin> members, int arenaType, int grade)
	{
		gs.getLogger().debug("notify fight server role team join super arena");
		tfc.sendPacket(new Packet.S2F.TeamJoinSuperArenaReq(tagID, members, arenaType, grade));
	}
	
	public void notifyFightTeamQuitSuperArena(int tagID, int roleID, int memberCount, int arenaType, int grade)
	{
		gs.getLogger().debug("notify fight server role " + roleID + " team quit super arena");
		tfc.sendPacket(new Packet.S2F.TeamQuitSuperArenaReq(tagID, roleID, memberCount, arenaType, grade));
	}
	
	public void notifyFightQueryTeamMembers(int tagID, int roleID)
	{
		gs.getLogger().trace("notify fight server role " + roleID + " query team members");
		tfc.sendPacket(new Packet.S2F.QueryTeamMembersReq(tagID, roleID));
	}
	
	public void notifyFightQueryTeamMember(int tagID, int queryID)
	{
		gs.getLogger().trace("notify fight server role " + queryID + " query team member " + queryID);
		tfc.sendPacket(new Packet.S2F.QueryTeamMemberReq(tagID, queryID));
	}
	
	public void notifyFightRoleLeaveTeam(int roleID)
	{
		gs.getLogger().debug("notify fight server role " + roleID + " leave team");
		tfc.sendPacket(new Packet.S2F.RoleLeaveTeam(roleID));
	}
	
	public void notifyFightRoleLeaveMap(int roleID, int mapId, int instanceId)
	{
		gs.getLogger().info("notify fight server role " + roleID + " leave current map " + mapId + " " + instanceId);
		tfc.sendPacket(new Packet.S2F.LeaveMap(roleID));
	}
	
	public void notifyFightSyncRoleDemonHoleReq(int tagID, int roleID)
	{
		gs.getLogger().trace("notify fight server sync role " + roleID + " demon hole info req");
		tfc.sendPacket(new Packet.S2F.SyncRoleDemonHoleReq(tagID, roleID));
	}
	
	public void notifyFightRoleJoinDemonHoleReq(int tagID, SBean.RoleOverview role)
	{
		gs.getLogger().info("notify fight server role " + role.id + " join demon hole req");
		tfc.sendPacket(new Packet.S2F.RoleJoinDemonHoleReq(tagID, role));
	}
	
	public void notifyFightRoleChangeDemonHoleFloorReq(int tagID, SBean.RoleOverview role, int floor)
	{
		gs.getLogger().info("notify fight server role " + role.id + " change demon hole floor " + floor);
		tfc.sendPacket(new Packet.S2F.RoleChangeDemonHoleFloorReq(tagID, role, floor));
	}
	
	public void notifyFightRoleDemonHoleBattleReq(int tagID, int roleID)
	{
		gs.getLogger().trace("notify fight server role " + roleID + " demon hole battle info");
		tfc.sendPacket(new Packet.S2F.RoleDemonHoleBattleReq(tagID, roleID));
	}
	
	public void notifyFightRoleEnterDemonHoleMap(SBean.RoleOverview role, int floor)
	{
		gs.getLogger().info("notify fight server role " + role.id + " enter demon hole floor " + floor);
		tfc.sendPacket(new Packet.S2F.RoleEnterDemonHoleFloor(role, floor));
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void sendAllServerMsg(SBean.MessageInfo msg)
	{
		gs.getLogger().trace("send exchange server role " + msg.srcId + " send a massage: server name is " + msg.gsName + ", content is " + msg.content.msg);
		tec.sendPacket(new Packet.S2E.SendMsg(msg));
	}
	
	public void sendExchangeSocialReq(int taskId, int dataType, Stream.IStreamable bodyData)
	{
		gs.getLogger().debug("send exchange server social data type " + dataType);
		tec.sendPacket(new Packet.S2E.SocialMsgReq(ForwardData.encodePacket(dataType, taskId, bodyData)));
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void closeClientSession(int sid)
	{
		gs.getLogger().warn("server close client session " + sid + " for kick or ban user");
		tgs.closeSession(sid);
	}
	public void sendLuaPacket(int sid, String data)
	{
		if (data.startsWith("|map|"))
			gs.getLogger().trace("forward session " + sid + " map lua channel packet:[" + data + "]");
		else
			gs.getLogger().debug("send session " + sid + " lua channel packet:[" + data + "]");
		tgs.sendPacket(sid, new Packet.S2C.LuaChannel(data));
	}
	
	public void sendLuaPacket(int sid, List<String> data)
	{
		gs.getLogger().debug("send session " + sid + " lua channel2 packet:[" + data + "]");
		tgs.sendPacket(sid, new Packet.S2C.LuaChannel2(data));
	}
	
	public void sendStrPacket(int sid, SStream.IStrPacket packet)
	{
		String data = SStream.encode(packet);
		gs.getLogger().debug("send session " + sid + " lua str packet:[" + data + "]");
		tgs.sendPacket(sid, new Packet.S2C.StrChannel(data));
	}
	
	public void sendStrPacket(int sid, String data)
	{
		gs.getLogger().trace("send session " + sid + " map lua str packet:[" + data + "]");
		tgs.sendPacket(sid, new Packet.S2C.StrChannel(data));
	}
	
	public void broadcastStrPacket(List<Integer> sids, String data)
	{
		gs.getLogger().trace("boradcast map lua str packet:[" + data + "]");
		if(gs.getConfig().challengeFlag == 1)
			tgs.broadcastPacketWithOutputSecurity(sids, new Packet.S2C.StrChannel(data));
		else
			tgs.broadcastPacket(sids, new Packet.S2C.StrChannel(data));
//		for (int sid : sids)
//			tgs.sendPacket(sid, new Packet.S2C.StrChannel(data));
	}
	
	public void broadcastStrPacket(List<Integer> sids, SStream.IStrPacket packet)
	{
		String data = SStream.encode(packet);
		gs.getLogger().trace("boradcast map lua str packet:[" + data + "]");
		if(gs.getConfig().challengeFlag == 1)
			tgs.broadcastPacketWithOutputSecurity(sids, new Packet.S2C.StrChannel(data));
		else
			tgs.broadcastPacket(sids, new Packet.S2C.StrChannel(data));
//		for (int sid : sids)
//			tgs.sendPacket(sid, new Packet.S2C.StrChannel(data));
	}
	
//	void onForwardMapLuaChannel(int sessionid, String data)
//	{
//		Role role = gs.getLoginManager().getLoginRole(sessionid);
//		if (role == null)
//			tgs.closeSession(sessionid);
//		else
//			gs.getMapService().syncRoleLuaChannelPacket(role.id, data);
//	}
//	
//	void onForwardMapStrPacket(int sessionid, String data)
//	{
//		gs.getLogger().trace("receive session " + sessionid + " map lua str packet : " + data);
//		Role role = gs.getLoginManager().getLoginRole(sessionid);
//		if (role == null)
//			tgs.closeSession(sessionid);
//		else
//			gs.getMapService().syncRoleStrChannelPacket(role.id, data);
//	}
	
	public void onModifyServerTimeOffset(int timeOffset)
	{
//		this.notifyClanReportServerTimeOffset(timeOffset);
		this.notifyAuctionReportServerTimeOffset(timeOffset);
		this.notifyGlobalMapReportServerTimeOffset(timeOffset);
		this.notifyFightReportServerTimeOffset(timeOffset);
	}
	
	public void resetServerTimeOffset(int timeOffset)
	{
		GameTime.setServerTimeOffset(timeOffset);
		gs.getMapService().syncAllMapsTimeOffset(timeOffset);
//		for (int sid : sessions.keySet())
//		{
//			this.sendStrPacket(sid, new SBean.server_info(GameTime.getTime(), gs.getConfig().id, GameTime.getDay(gs.getOpenTime())));
//		}
	}
	
	void onLuaChannelList(int sessionid, String[] msg)
	{
	}
	
	void onUserLogin(int sessionid, final int logingsid, final String uid, final String channel, final SBean.UserLoginInfo info, final int loginroleid, final SBean.CreateRoleParam createParam)
	{
		SessionInfo sinfo = sessions.get(sessionid);
		if (sinfo != null && sinfo.addrClient != null && sinfo.addrClient.host != null)
			info.system.loginIP = sinfo.addrClient.host;
		GameServer.Config cfg = gs.getConfig();
		if (cfg.verPacket != 0 && info.client.clientVerPacket != 0 && cfg.verPacket != info.client.clientVerPacket)
		{
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_force_close(GameData.FORCE_CLOSE_VERSION_CODE));
			return;
		}
		if (cfg.verResource != 0 && info.client.clientVerResource != 0 && cfg.verResource != info.client.clientVerResource)
		{
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_force_close(GameData.FORCE_CLOSE_VERSION_RES));
			return;
		}
		int zoneId = GameData.getRawZoneIdFromGSId(logingsid);
		if (!cfg.zones.contains(zoneId))
		{
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_ZONE_ID_INVALID,  0, ""));
			return;
		}
		if (channel.isEmpty() || !GameData.isDigit(channel))
		{
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_GAME_CHANNEL_INVALID, 0, ""));
			return;
		}
		if (uid.isEmpty())
		{
			gs.getLoginManager().genUserId((userid) -> {
				gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_USER_NAME_EMPTY, userid, ""));
			});
			return;
		}
		
		if( (! gs.getLoginManager().getLoginWhiteList().canLogin(uid) || gs.getLoginManager().getChannelBlackList().isRestricted(channel)) && !gs.getLoginManager().getLoginWhiteList().isPrivilegedAccount(uid))
		{
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_NOT_INSERVICE, 0, ""));
			return;
		}
		
		final SBean.DBRegisterID registerID = GameData.getInstance().getRegisterID(info.client.gameAppID, zoneId, uid, channel);
		if ( registerID == null)
		{
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_ID_INVALID, 0, ""));
			return;
		}
		if (loginroleid == 0 && createParam != null)
		{
			if (GameData.getInstance().getClassRoleCFG(createParam.classType) == null)
			{
				gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_CLASSTYPE_INVALID, 0, ""));
				return;
			}
			if (GameData.getInstance().getRoleHeadIcon(createParam.gender, createParam.face, createParam.hair) == 0)
			{
				gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_GENDER_INVALID, 0, ""));
				return;
			}
			createParam.name = createParam.name.trim().toLowerCase();
			if (!GameData.getInstance().checkInputStrValid(createParam.name, GameData.getInstance().getCommonCFG().input.maxRoleNameLength, true))
			{
				gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_ROLENAME_INVALID, 0, ""));
				return;	
			}
		}
//		if (gs.getLoginManager().tryUserLogin(sessionid, registerID, info, loginroleid, createParam))
//			gs.getLoginManager().getLoginQueue().remove(sessionid);
//		else
//			gs.getLoginManager().getLoginQueue().add(sessionid, registerID, info, loginroleid, createParam);
//		if (gs.getLoginManager().getOnlineRoleCount() >= gs.getConfig().cap && gs.getLoginManager().getUserRole(username) == null && !gs.getLoginManager().getLoginWhiteList().isPrivilegedAccount(uid))
//		{
//			gs.getLoginManager().getLoginQueue().add(sessionid);
//			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_ONLINE_ROLE_FULL, 0, ""));
//			return;
//		}
		String username = GameData.getUserName(registerID);
		gs.getLoginManager().getLoginVerifier().verify(sessionid, info, registerID.uid, registerID.channel, (sid, success) -> {
            if (!success)
            {
                gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_VERIFY_FAILED, 0, ""));
                return;
            }
            setSessionAuthed(sessionid);
            if (loginroleid == 0)
            {
                if (createParam == null)
                {
                	gs.getLoginManager().tryVerifyRegister(username, info.arg.exParam, (errCode) ->{
                		if (errCode < 0)
                		{
                			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_VERIFY_REGISTER_FAILED, errCode, ""));
                			return;
                		}
                		if (gs.getLoginManager().userLogin(sid, info, registerID, errCode > 0))
                			gs.getLoginManager().getLoginQueue().remove(sessionid);
                		else
                			gs.getLoginManager().getLoginQueue().add(sessionid, registerID, info, errCode > 0);
                	});
                }
                else
                {
                    gs.getLoginManager().userCreateRole(sid, info, zoneId, username, createParam);
                }
            }
            else
            {
                gs.getLoginManager().roleLogin(sid, info, username, loginroleid);
            }
        });
		
	}
	
	void onRoleLogout(int sessionid, Role role)
	{
		boolean ok = gs.getLoginManager().roleLogout(sessionid, role);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.role_logout_res(ok ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}
	
	
	public void onIDIPReq(final TCPIDIPServer from, final int sessionid, final IDIPPacket packet)
	{
		idipsHandler.handleIDIPRequest(from, sessionid, packet);
	}
	
	static class SessionInfo
	{
		NetAddress addrClient;
		AtomicInteger pps = new AtomicInteger(0);
		int firePPSValue;
		boolean authed;
		
		SessionInfo(NetAddress addrClient)
		{
			this.addrClient = addrClient;
		}
	}
	
	public boolean checkSession(int sid)
	{
		return sessions.get(sid) != null;
	}
	
	public boolean isSessionAuthed(int sid)
	{
		SessionInfo sinfo = sessions.get(sid);
		return sinfo == null ? false : sinfo.authed;
	}
	
	public void setSessionAuthed(int sid)
	{
		SessionInfo sinfo = sessions.get(sid);
		if (sinfo != null)
			sinfo.authed = true;
	}
	
	public boolean incSessionPackets(int sid)
	{
		final int ppsMax = gs.getConfig().pps;
		if( ppsMax <= 0 )
			return true;
		SessionInfo sinfo = sessions.get(sid);
		if( sinfo == null )
			return true;		
		boolean bOK = sinfo.pps.incrementAndGet() < ppsMax;
		if( ! bOK )
		{
			//gs.getLogger().warn("pps trigger session " + sid + " close, val=" + sinfo.pps.get());
			sinfo.firePPSValue = sinfo.pps.get();
			if (sinfo.firePPSValue == ppsMax)
				gs.getLogger().warn("server close client session " + sid + " for pps >= " + ppsMax);
			tgs.closeSession(sid);
		}
		return bOK;
	}
	
	public void setDisconnectMode(boolean bDisconnectMode)
	{
		if( bDisconnectMode && ! this.bDisconnectMode )
		{
			for(int sid : sessions.keySet())
			{
				tgs.closeSession(sid);
			}
		}
		this.bDisconnectMode = bDisconnectMode;	
	}
	
	public void sendTLog(String log)
	{
		udpLogger.sendString(log);
	}

	
	class GSNetStat implements GSNetStatMBean
	{
		AtomicInteger tgsOpenCount = new AtomicInteger();
		AtomicInteger tgsCloseCount = new AtomicInteger();
		AtomicLong recvMapClientStrMsgCounter = new AtomicLong(0);
		AtomicLong recvMapClientNormalMsgCounter = new AtomicLong(0);
		AtomicLong recvMapServerMsgCounter = new AtomicLong(0);
		Statistic sessionStat = new Statistic();
		
		long profileTime;
		long profileInterval;
		long lastProfileRecvMapClientStrMsgTotalCount;
		long lastProfileRecvMapClinetNormalMsgTotalCount;
		long lastProfileRecvMapServerMsgTotalCount;
		int lastProfileSessionSendPacketTotalCount;
		int lastProfileSessionSendTimesTotalCount;
		int lastProfileSessionRecvTimesTotalCount;
		int lastProfileSessionSendPacketTaskCount;
		int lastProfileSessionSendBytes;
		
		int curProfileRecvMapClientStrMsgCount;
		int curProfileRecvMapClientNormalMsgCount;
		int curProfileRecvMapServerMsgCount;
		int curProfileSessionSendPacketCount;
		int curProfileSessionSendTimesCount;
		int curProfileSessionRecvTimesCount;
		int curProfileSessionSendPacketTaskAdded;
		int curProfileSessionSendBytes;
		public GSNetStat()
		{
			profileTime = GameTime.getTimeMillis();
		}
		
		public void start()
		{
			try
			{
				ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName("i3k.gs:type=GSNetStat"));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		@Override
		public int getTGSOpenCount()
		{
			return tgsOpenCount.get();
		}
		
		@Override
		public int getTGSCloseCount()
		{
			return tgsCloseCount.get();
		}
		
		@Override
		public int getSamplinginterval()
		{
			return (int)profileInterval;
		}
		
		@Override
		public int getRecvMapClientStrMsgCountPerSecond()
		{
			return (int)(curProfileRecvMapClientStrMsgCount*1000/profileInterval);
		}
		
		@Override
		public int getRecvMapClientNormalMsgCountPerSecond()
		{
			return (int)(curProfileRecvMapClientNormalMsgCount*1000/profileInterval);
		}
		
		@Override
		public int getRecvMapServerMsgCountPerSecond()
		{
			return (int)(curProfileRecvMapServerMsgCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionSendPacketCountPerSecond()
		{
			return (int)(curProfileSessionSendPacketCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionSendTimesCountPerSecond()
		{
			return (int)(curProfileSessionSendTimesCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionRecvTimesCountPerSecond()
		{
			return (int)(curProfileSessionRecvTimesCount*1000/profileInterval);
		}
		
		@Override
		public int getSessionPacketTaskAddedPerSecond()
		{
			return (int)(curProfileSessionSendPacketTaskAdded*1000/profileInterval);
		}
		
		@Override
		public int getSessionSendKBytesPerSecond()
		{
			return (int)(curProfileSessionSendBytes/profileInterval);
		}
		
		@Override
		public int getSessionSendPacketTotal()
		{
			return lastProfileSessionSendPacketTotalCount;
		}
		
		@Override
		public int getSessionSendTimesTotal()
		{
			return lastProfileSessionSendTimesTotalCount;
		}
		
		@Override
		public int getSessionRecvTimesTotal()
		{
			return lastProfileSessionRecvTimesTotalCount;
		}
		
		@Override
		public int getSessionPacketTaskQueue()
		{
			return lastProfileSessionSendPacketTaskCount;
		}
		
		@Override
		public int getSessionSendBytes()
		{
			return lastProfileSessionSendBytes;
		}
		
		
		public void profile()
		{
			long now = GameTime.getTimeMillis();
			profileInterval = now - profileTime;
			profileTime = now;
			if (profileInterval < 100)
				gs.getLogger().warn("profile timer tick is less than 100ms");
			if (profileInterval == 0)
				profileInterval = 1;
			
			long curProfileRecvMapClientStrMsgTotalCount = recvMapClientStrMsgCounter.get();
			long curProfileRecvMapClinetNormalMsgTotalCount = recvMapClientNormalMsgCounter.get();
			long curProfileRecvMapServerMsgTotalCount = recvMapServerMsgCounter.get();
			tgs.getStatistic(sessionStat);
			
			curProfileRecvMapClientStrMsgCount = (int)(curProfileRecvMapClientStrMsgTotalCount - lastProfileRecvMapClientStrMsgTotalCount);
			curProfileRecvMapClientNormalMsgCount = (int)(curProfileRecvMapClinetNormalMsgTotalCount - lastProfileRecvMapClinetNormalMsgTotalCount);
			curProfileRecvMapServerMsgCount = (int)(curProfileRecvMapServerMsgTotalCount - lastProfileRecvMapServerMsgTotalCount);
			curProfileSessionSendPacketCount = (int)(sessionStat.nPacketsSend - lastProfileSessionSendPacketTotalCount);
			curProfileSessionSendTimesCount = (int)(sessionStat.nSendTimes - lastProfileSessionSendTimesTotalCount);
			curProfileSessionRecvTimesCount = (int)(sessionStat.nRecvTimes - lastProfileSessionRecvTimesTotalCount);
			curProfileSessionSendPacketTaskAdded = (int)(sessionStat.nSendPacketAccumlate - lastProfileSessionSendPacketTaskCount);
			curProfileSessionSendBytes = (int)(sessionStat.nBytesSend - lastProfileSessionSendBytes);
			
			lastProfileRecvMapClientStrMsgTotalCount = curProfileRecvMapClientStrMsgTotalCount;
			lastProfileRecvMapClinetNormalMsgTotalCount = curProfileRecvMapClinetNormalMsgTotalCount;
			lastProfileRecvMapServerMsgTotalCount = curProfileRecvMapServerMsgTotalCount;
			lastProfileSessionSendPacketTotalCount = sessionStat.nPacketsSend;
			lastProfileSessionSendTimesTotalCount = sessionStat.nSendTimes;
			lastProfileSessionRecvTimesTotalCount = sessionStat.nRecvTimes;
			lastProfileSessionSendPacketTaskCount = sessionStat.nSendPacketAccumlate;
			lastProfileSessionSendBytes = sessionStat.nBytesSend;
			
			if (gs.getConfig().pInfo != 0)
			{
				gs.getLogger().info(String.format("SI=%d, strmsg=%d, othmsg=%d, svrmsg=%d, sc=%d, st=%d, rt=%d, qa=%d, q=%d, skbps=%d, sm=%d, sct=%d, stt=%d, rtt=%d",
						getSamplinginterval(), getRecvMapClientStrMsgCountPerSecond(), getRecvMapClientNormalMsgCountPerSecond(), getRecvMapServerMsgCountPerSecond(),
						getSessionSendPacketCountPerSecond(), getSessionSendTimesCountPerSecond(), getSessionRecvTimesCountPerSecond(),
						getSessionPacketTaskAddedPerSecond(), getSessionPacketTaskQueue(), 
						getSessionSendKBytesPerSecond(), getSessionSendBytes()/1000000, 
						getSessionSendPacketTotal(), getSessionSendTimesTotal(), getSessionRecvTimesTotal()));	
			}
		}
		
	}
	
	public GSNetStat getStat()
	{
		return stat;
	}
	
	GameServer gs;
	GSNetStat stat = new GSNetStat();
	NetManager managerNet = null;
	TCPGameServer tgs = null;
	TCPMapServer tms = null;
	TCPIDIPServer idips = null;
	Map<String, TCPAuthClient> taucs = null;
//	TCPClanClient tcc = null;
	TCPAuctionClient tac = null;
	TCPGlobalMapClient tgmc = null;
	TCPFightClient tfc = null;
	TCPExchangeClient tec = null;
	UDPLogger udpLogger = null;
	TCPAlarmServer talarms = null;
	
	
	StringChannelHandler strChannelHandler;
	Set<String> noRoleStringPackets = new TreeSet<>();
	IDIPServiceHandler idipsHandler;
	
	ConcurrentMap<Integer, SessionInfo> sessions = new ConcurrentHashMap<>();
	AtomicInteger sps = new AtomicInteger(0);
	boolean bDisconnectMode = false;
	OpenConnectFailCount authcount = new OpenConnectFailCount();
	OpenConnectFailCount auctioncount = new OpenConnectFailCount();
	//OpenConnectFailCount clancount = new OpenConnectFailCount();
	OpenConnectFailCount globalmapcount = new OpenConnectFailCount();
	OpenConnectFailCount fightcount = new OpenConnectFailCount();
	OpenConnectFailCount exchangecount = new OpenConnectFailCount();
	
}
