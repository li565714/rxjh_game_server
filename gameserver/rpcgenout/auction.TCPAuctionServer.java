// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.auction;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.NetAddress;
import ket.kio.TCPServer;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPAuctionServer extends TCPServer<SimplePacket>
{

	public TCPAuctionServer(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	@Override
	public int getMaxConnectionIdleTime()
	{
		return managerRPC.getTCPAuctionServerMaxConnectionIdleTime();
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// server to auction
			case Packet.eS2AuctionPKTKeepAlive:
			case Packet.eS2AuctionPKTWhoAmI:
			case Packet.eS2AuctionPKTReportTimeOffset:
			case Packet.eS2AuctionPKTPutOnItemReq:
			case Packet.eS2AuctionPKTTimeOutPutOffItemsRes:
			case Packet.eS2AuctionPKTPutOffItemsReq:
			case Packet.eS2AuctionPKTBuyItemsReq:
			case Packet.eS2AuctionPKTCheckCanBuyRes:
			case Packet.eS2AuctionPKTAuctionItemsSyncReq:
			case Packet.eS2AuctionPKTSelfItemsSyncReq:
			case Packet.eS2AuctionPKTItemPricesSyncReq:
			case Packet.eS2AuctionPKTUpdateGroupBuyGoods:
			case Packet.eS2AuctionPKTSyncGroupBuyGoods:
				return true;
			default:
				break;
			}
			return false;
		}
	}

	@Override
	public PacketEncoder<SimplePacket> getEncoder()
	{
		return dencoder;
	}

	@Override
	public PacketDecoder<SimplePacket> getDecoder()
	{
		return dencoder;
	}

	@Override
	public void onOpen()
	{
		managerRPC.onTCPAuctionServerOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuctionServerOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuctionServerClose(this, errcode);
	}

	@Override
	public void onPacketSessionOpen(int sessionid, NetAddress addrClient)
	{
		managerRPC.onTCPAuctionServerSessionOpen(this, sessionid, addrClient);
	}

	@Override
	public void onPacketSessionClose(int sessionid, ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuctionServerSessionClose(this, sessionid, errcode);
	}

	@Override
	public void onPacketSessionRecv(int sessionid, SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// server to auction
		case Packet.eS2AuctionPKTKeepAlive:
			{
				Packet.S2Auction.KeepAlive p = (Packet.S2Auction.KeepAlive)packet;
				managerRPC.onTCPAuctionServerRecvKeepAlive(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTWhoAmI:
			{
				Packet.S2Auction.WhoAmI p = (Packet.S2Auction.WhoAmI)packet;
				managerRPC.onTCPAuctionServerRecvWhoAmI(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTReportTimeOffset:
			{
				Packet.S2Auction.ReportTimeOffset p = (Packet.S2Auction.ReportTimeOffset)packet;
				managerRPC.onTCPAuctionServerRecvReportTimeOffset(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTPutOnItemReq:
			{
				Packet.S2Auction.PutOnItemReq p = (Packet.S2Auction.PutOnItemReq)packet;
				managerRPC.onTCPAuctionServerRecvPutOnItemReq(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTTimeOutPutOffItemsRes:
			{
				Packet.S2Auction.TimeOutPutOffItemsRes p = (Packet.S2Auction.TimeOutPutOffItemsRes)packet;
				managerRPC.onTCPAuctionServerRecvTimeOutPutOffItemsRes(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTPutOffItemsReq:
			{
				Packet.S2Auction.PutOffItemsReq p = (Packet.S2Auction.PutOffItemsReq)packet;
				managerRPC.onTCPAuctionServerRecvPutOffItemsReq(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTBuyItemsReq:
			{
				Packet.S2Auction.BuyItemsReq p = (Packet.S2Auction.BuyItemsReq)packet;
				managerRPC.onTCPAuctionServerRecvBuyItemsReq(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTCheckCanBuyRes:
			{
				Packet.S2Auction.CheckCanBuyRes p = (Packet.S2Auction.CheckCanBuyRes)packet;
				managerRPC.onTCPAuctionServerRecvCheckCanBuyRes(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTAuctionItemsSyncReq:
			{
				Packet.S2Auction.AuctionItemsSyncReq p = (Packet.S2Auction.AuctionItemsSyncReq)packet;
				managerRPC.onTCPAuctionServerRecvAuctionItemsSyncReq(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTSelfItemsSyncReq:
			{
				Packet.S2Auction.SelfItemsSyncReq p = (Packet.S2Auction.SelfItemsSyncReq)packet;
				managerRPC.onTCPAuctionServerRecvSelfItemsSyncReq(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTItemPricesSyncReq:
			{
				Packet.S2Auction.ItemPricesSyncReq p = (Packet.S2Auction.ItemPricesSyncReq)packet;
				managerRPC.onTCPAuctionServerRecvItemPricesSyncReq(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTUpdateGroupBuyGoods:
			{
				Packet.S2Auction.UpdateGroupBuyGoods p = (Packet.S2Auction.UpdateGroupBuyGoods)packet;
				managerRPC.onTCPAuctionServerRecvUpdateGroupBuyGoods(this, p, sessionid);
			}
			break;
		case Packet.eS2AuctionPKTSyncGroupBuyGoods:
			{
				Packet.S2Auction.SyncGroupBuyGoods p = (Packet.S2Auction.SyncGroupBuyGoods)packet;
				managerRPC.onTCPAuctionServerRecvSyncGroupBuyGoods(this, p, sessionid);
			}
			break;
		default:
			break;
		}
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private RPCManager managerRPC;
}
