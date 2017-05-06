// modified by ket.kio.RPCGen at Fri Mar 31 18:59:59 CST 2017.

package i3k.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ket.kio.NetAddress;
import ket.kio.NetManager;
import i3k.alarm.TCPAlarmServer;
import i3k.gs.GameData;
import i3k.rpc.Packet;
import i3k.util.GameTime;
import i3k.util.GameServerTable;
import i3k.SBean;


public class RPCManager
{
	public RPCManager(AuctionServer as)
	{
		this.as = as;
	}
	
	public void onTimer(int timeTick)
	{
		if (managerNet != null)
			managerNet.checkIdleConnections();
		if (talarms != null)
			talarms.onTimer();
	}
	
	public void start()
	{
		if( as.getConfig().nIOThread == 1 )
			managerNet = new NetManager(NetManager.NetManagerType.eSelectNetManager, as.getConfig().nIOThread);
		else
			managerNet = new NetManager(NetManager.NetManagerType.eMTSelectNetManager, as.getConfig().nIOThread);
		
		tas = new TCPAuctionServer(this);
		talarms = new TCPAlarmServer(this.getNetManager(), as.getConfig().addrAlarmListen, as.getLogger());
		
		managerNet.start();
		
		tas.setListenAddr(as.getConfig().addrAcutionListen, ket.kio.BindPolicy.eReuseTimewait);
		tas.setListenBacklog(128);
		tas.open();
		
		talarms.start();
	}
	public void destroy()
	{
		if (managerNet != null)
			managerNet.destroy();
	}
	
	public NetManager getNetManager() { return managerNet; }
	
	//// begin handlers.
	public int getTCPAuctionServerMaxConnectionIdleTime()
	{
		return 30 * 1000;
	}

	public void onTCPAuctionServerOpen(TCPAuctionServer peer)
	{
		as.getLogger().info("TCPAuctionServer open on " + peer.getListenAddr());
	}

	public void onTCPAuctionServerOpenFailed(TCPAuctionServer peer, ket.kio.ErrorCode errcode)
	{
		as.getLogger().warn("TCPAuctionServer open on " + peer.getListenAddr() + " failed, errcode=" + errcode);
	}

	public void onTCPAuctionServerClose(TCPAuctionServer peer, ket.kio.ErrorCode errcode)
	{
		as.getLogger().info("TCPAuctionServer close on " + peer.getListenAddr() + ", errcode=" + errcode);
	}

	public void onTCPAuctionServerSessionOpen(TCPAuctionServer peer, int sessionid, NetAddress addrClient)
	{
		as.getLogger().info("TCPAuctionServer on session " + sessionid  + " open, client " + addrClient);
	}

	public void onTCPAuctionServerSessionClose(TCPAuctionServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		as.getLogger().info("TCPAuctionServer on session " + sessionid  + " close, errcode=" + errcode);
		
		Set<Integer> zones = table.getZonesBySessionID(sessionid);
		if(zones != null)
			as.getAuctionManager().unActivateContainer(zones);
		table.onSessionClose(sessionid);
	}

	public void onTCPAuctionServerRecvKeepAlive(TCPAuctionServer peer, Packet.S2Auction.KeepAlive packet, int sessionid)
	{
		as.getLogger().debug("receive gs server session " + sessionid + " " + packet.getHello() + " keepalive packet");
	}

	public void onTCPAuctionServerRecvWhoAmI(TCPAuctionServer peer, Packet.S2Auction.WhoAmI packet, int sessionid)
	{
		as.getLogger().debug("receive gs server session " + sessionid + " server " + packet.getServerId() + " whoami packet");
		if (GameData.getAreaIdFromGSId(packet.getServerId()) != as.getConfig().areaId)
		{
			as.getLogger().warn("close gs server session " + sessionid + " for server " + packet.getServerId() + " not match area id " + as.getConfig().areaId);
			tas.closeSession(sessionid);
			return;
		}
		
		Integer oldSession = table.tryCloseSessionByServerID(packet.getServerId());
		if(oldSession != null)
		{
			as.getLogger().warn("close gs server session " + oldSession + " on server [" + packet.getServerId() + " " + sessionid + "] announce");
			tas.closeSession(oldSession);
		}
		
		if (!table.onSessionAnnounce(sessionid, packet.getServerId(), packet.getZones()))
		{
			as.getLogger().warn("close gs server session " + sessionid + " for server " + packet.getServerId() + " or zones " + packet.getZones() + " clash");
			tas.closeSession(sessionid);
			return;
		}
		as.getAuctionManager().activateContainer(packet.getZones());
	}

	public void onTCPAuctionServerRecvReportTimeOffset(TCPAuctionServer peer, Packet.S2Auction.ReportTimeOffset packet, int sessionid)
	{
		as.getLogger().info("receive gs server session " + sessionid + " report gs time offset " + packet.getTimeOffset() + ", current time offset " + GameTime.getServerTimeOffset());
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID == null)
			return;
		if (packet.getTimeOffset() > GameTime.getServerTimeOffset())
		{
			for (int gsid : table.getAllServers())
			{
				if (gsid != serverID)
				{
					Integer sid = table.getSessionIDByServerID(serverID);
					if (sid != null)
					as.getRPCManager().notifyGameServerAdjustTimeOffset(sid, packet.getTimeOffset());
				}
			}
			GameTime.setServerTimeOffset(packet.getTimeOffset());
		}
	}

	public void onTCPAuctionServerRecvPutOnItemReq(TCPAuctionServer peer, Packet.S2Auction.PutOnItemReq packet, int sessionid)
	{
		as.getLogger().debug("auction receive gs server session " + sessionid + " role " + packet.getRoleID() + " PutOnItemReq ");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID == null)
			return;
		
		as.getAuctionManager().handlePutOnItem(packet.getRoleID(), packet.getItems(), cid -> notifyGSPutOnItemResonse(sessionid, packet.getTagID(), cid));
	}

	public void onTCPAuctionServerRecvTimeOutPutOffItemsRes(TCPAuctionServer peer, Packet.S2Auction.TimeOutPutOffItemsRes packet, int sessionid)
	{
		as.getLogger().debug("receive gs time out put off items response errCode " + packet.getErrCode());
		as.getGameService().handleTimeOutPutOffItemsResponse(packet.getTagID(), packet.getErrCode());
	}

	public void onTCPAuctionServerRecvPutOffItemsReq(TCPAuctionServer peer, Packet.S2Auction.PutOffItemsReq packet, int sessionid)
	{
		as.getLogger().debug("receive gs put off role " + packet.getRoleID() + " items " + packet.getCid() + " req");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID == null)
			return;
		
		as.getAuctionManager().handlePutOffItems(packet.getRoleID(), packet.getCid(), (errCode, items) -> notifyGSPutOffItemsResponse(sessionid, packet.getTagID(), errCode, items));
	}

	public void onTCPAuctionServerRecvBuyItemsReq(TCPAuctionServer peer, Packet.S2Auction.BuyItemsReq packet, int sessionid)
	{
		as.getLogger().info("receive gs session " + sessionid + " role buy seller " + packet.getSellerID() + "'s items cid " + packet.getCid() + " price " + packet.getPrice());
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID == null)
			return;
		
		as.getAuctionManager().handleBuyItems(packet.getSellerID(), packet.getCid(), packet.getPrice(), items -> notifyGSBuyItemsResponse(sessionid, packet.getTagID(), items));
	}

	public void onTCPAuctionServerRecvCheckCanBuyRes(TCPAuctionServer peer, Packet.S2Auction.CheckCanBuyRes packet, int sessionid)
	{
		as.getLogger().info("receive gs session " + sessionid + " check can buy errCode " + packet.getErrCode());
		as.getGameService().handleCheckCanBuyResponse(packet.getTagID(), packet.getErrCode());
	}

	public void onTCPAuctionServerRecvAuctionItemsSyncReq(TCPAuctionServer peer, Packet.S2Auction.AuctionItemsSyncReq packet, int sessionid)
	{
		as.getLogger().trace("receive gs session " + sessionid + " sync type " + packet.getItemType() + " items of page " + packet.getPage() + " order " + packet.getOrder());
		as.getAuctionManager().handleItemsSync(packet.getItemType(), packet.getClassType(), packet.getRank(), packet.getLevel(), packet.getOrder(), packet.getPage(), packet.getName(), (items, lastPage) -> notifyGSAuctionItemsSyncResponse(sessionid, packet.getTagID(), items, lastPage));
	}

	public void onTCPAuctionServerRecvSelfItemsSyncReq(TCPAuctionServer peer, Packet.S2Auction.SelfItemsSyncReq packet, int sessionid)
	{
		as.getLogger().trace("receive gs session " + sessionid + " role " + packet.getRoleID() + " sync self items");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID == null)
			return;
		
		as.getAuctionManager().handleSelfItemsSync(packet.getRoleID(), items -> notifyGSSelfItemsSyncResponse(sessionid, packet.getTagID(), items));
	}

	public void onTCPAuctionServerRecvItemPricesSyncReq(TCPAuctionServer peer, Packet.S2Auction.ItemPricesSyncReq packet, int sessionid)
	{
		as.getLogger().trace("receive gs session " + sessionid + " item " + packet.getItemID() + " prices sync req");
		notifyGSItemPricesSyncResponse(sessionid, packet.getTagID(), as.getAuctionManager().getItemPrices(packet.getItemID()));
	}

	public void onTCPAuctionServerRecvUpdateGroupBuyGoods(TCPAuctionServer peer, Packet.S2Auction.UpdateGroupBuyGoods packet, int sessionid)
	{
		as.getLogger().trace("receive gs session " + sessionid + " update group buy activity " + packet.getActivityID() + " goods " + packet.getGid() + " , " + packet.getCount());
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID == null)
			return;
		
		as.getGroupBuyManager().updateGroupBuyGoods(packet.getActivityID(), serverID, packet.getGid(), packet.getCount(), packet.getEndTime());
	}

	public void onTCPAuctionServerRecvSyncGroupBuyGoods(TCPAuctionServer peer, Packet.S2Auction.SyncGroupBuyGoods packet, int sessionid)
	{
		as.getLogger().trace("receive gs session " + sessionid + " sync group buy activity " + packet.getActivityID() + " log on server start");
		Integer serverID = table.getServerIDBySessionID(sessionid);
		if(serverID == null)
			return;
		
		as.getGroupBuyManager().updateGroupBuyGoods(packet.getActivityID(), serverID, packet.getLog(), packet.getEndTime());
	}

	//// end handlers.
	
///////////////////////////////notify game server/////////////////////////////////////////////////////////	
	public void notifyGameServerAdjustTimeOffset(int session, int timeOffset)
	{
		as.getLogger().debug("notify session " + session + " gs adjust time offset " + timeOffset);
		tas.sendPacket(session, new Packet.Auction2S.AdjustTimeOffset(timeOffset));
	}
	
	void notifyGSPutOnItemResonse(int sessionid, int tagID, int cid)
	{
		as.getLogger().debug("response gs session " + sessionid + " put on items cid " + cid);
		tas.sendPacket(sessionid, new Packet.Auction2S.PutOnItemRes(tagID, cid));
	}
	
	void notifyGSPutOffItemsResponse(int sessionid, int tagID, int errCode, SBean.DBConsignItems items)
	{
		as.getLogger().debug("response gs session " + sessionid + " put off items errCode " + errCode);
		tas.sendPacket(sessionid, new Packet.Auction2S.PutOffItemsRes(tagID, errCode, items));
	}
	
	void notifyGSBuyItemsResponse(int sessionid, int tagID, SBean.DBConsignItems items)
	{
		as.getLogger().info("response gs session " + sessionid + " buy items success " + (items != null));
		tas.sendPacket(sessionid, new Packet.Auction2S.BuyItemsRes(tagID, items));
	}
	
	void notifyGSAuctionItemsSyncResponse(int sessionid, int tagID, List<SBean.DetailConsignItems> items, int lastPage)
	{
		as.getLogger().trace("response gs session " + sessionid + " auction items sync success " + (items != null) + " lastPage " + lastPage);
		tas.sendPacket(sessionid, new Packet.Auction2S.AuctionItemsSyncRes(tagID, items, lastPage));
	}
	
	void notifyGSSelfItemsSyncResponse(int sessionid, int tagID, Map<Integer, SBean.DBConsignItems> items)
	{
		as.getLogger().trace("response gs session " + sessionid + " self items sync success " + (items != null));
		tas.sendPacket(sessionid, new Packet.Auction2S.SelfItemsSyncRes(tagID, items));
	}
	
	void notifyGSRoleTimeOutPutOffItems(int tagID, int roleID, int cid, SBean.DBConsignItems items)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(sessionid == null)
			return;
		
		as.getLogger().debug("notify gs session " + sessionid + " role " + roleID + " time out put off items cid " + cid);
		tas.sendPacket(sessionid, new Packet.Auction2S.TimeOutPutOffItemsReq(tagID, roleID, cid, items));
	}
	
	void notifyGSRoleCheckCanBuy(int tagID, int sellerID, int cid, SBean.DBConsignItems items)
	{
		Integer sessionid = table.getSessionIDByZoneID(GameData.getZoneIdFromRoleId(sellerID));
		if(sessionid == null)
			return;
		
		as.getLogger().debug("notify gs session " + sessionid + " seller " + sellerID + " check can buy cid " + cid + " price " + items.price);
		tas.sendPacket(sessionid, new Packet.Auction2S.CheckCanBuyReq(tagID, sellerID, cid, items));
	}
	
	void notifyGSItemPricesSyncResponse(int sessionid, int tagID, List<SBean.DBConsignItems> prices)
	{
		as.getLogger().trace("notify gs session " + sessionid + " item prices sync response");
		tas.sendPacket(sessionid, new Packet.Auction2S.ItemPricesSyncRes(tagID, prices));
	}
	
	void notifyAllGSSyncGroupBuyLog(int activityID, Map<Integer, Integer> log)
	{
		for(int gsid: table.getAllServers())
		{
			Integer sessionid = table.getSessionIDByServerID(gsid);
			if (sessionid != null)
				notifyGSSyncGroupBuyLog(sessionid, activityID, log);
		}
	}
	
	void notifyGSSyncGroupBuyLog(int sessionid, int activityID, Map<Integer, Integer> log)
	{
		as.getLogger().trace("notify gs session " + sessionid + " sync group buy activity " + activityID + " buy log !");
		tas.sendPacket(sessionid, new Packet.Auction2S.SyncGroupBuyLog(activityID, log));
	}
//////////////////////////////notify game server end//////////////////////////////////////////////////////

	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	int keepaliveRandomTime;
	NetManager managerNet;
	TCPAuctionServer tas;
	TCPAlarmServer talarms;
	AuctionServer as;
	
	GameServerTable table = new GameServerTable();
}
