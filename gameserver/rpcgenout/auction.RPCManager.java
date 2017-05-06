// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.auction;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManager
{

	//// begin handlers.
	public int getTCPAuctionServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPAuctionServerOpen(TCPAuctionServer peer)
	{
		// TODO
	}

	public void onTCPAuctionServerOpenFailed(TCPAuctionServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuctionServerClose(TCPAuctionServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuctionServerSessionOpen(TCPAuctionServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPAuctionServerSessionClose(TCPAuctionServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvKeepAlive(TCPAuctionServer peer, Packet.S2Auction.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvWhoAmI(TCPAuctionServer peer, Packet.S2Auction.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvReportTimeOffset(TCPAuctionServer peer, Packet.S2Auction.ReportTimeOffset packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvPutOnItemReq(TCPAuctionServer peer, Packet.S2Auction.PutOnItemReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvTimeOutPutOffItemsRes(TCPAuctionServer peer, Packet.S2Auction.TimeOutPutOffItemsRes packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvPutOffItemsReq(TCPAuctionServer peer, Packet.S2Auction.PutOffItemsReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvBuyItemsReq(TCPAuctionServer peer, Packet.S2Auction.BuyItemsReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvCheckCanBuyRes(TCPAuctionServer peer, Packet.S2Auction.CheckCanBuyRes packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvAuctionItemsSyncReq(TCPAuctionServer peer, Packet.S2Auction.AuctionItemsSyncReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvSelfItemsSyncReq(TCPAuctionServer peer, Packet.S2Auction.SelfItemsSyncReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvItemPricesSyncReq(TCPAuctionServer peer, Packet.S2Auction.ItemPricesSyncReq packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvUpdateGroupBuyGoods(TCPAuctionServer peer, Packet.S2Auction.UpdateGroupBuyGoods packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionServerRecvSyncGroupBuyGoods(TCPAuctionServer peer, Packet.S2Auction.SyncGroupBuyGoods packet, int sessionid)
	{
		// TODO
	}

	//// end handlers.
}
