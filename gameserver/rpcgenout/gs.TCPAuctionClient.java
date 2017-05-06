// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPAuctionClient extends TCPClient<SimplePacket>
{

	public TCPAuctionClient(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// auction to server
			case Packet.eAuction2SPKTKeepAlive:
			case Packet.eAuction2SPKTAdjustTimeOffset:
			case Packet.eAuction2SPKTPutOnItemRes:
			case Packet.eAuction2SPKTTimeOutPutOffItemsReq:
			case Packet.eAuction2SPKTPutOffItemsRes:
			case Packet.eAuction2SPKTBuyItemsRes:
			case Packet.eAuction2SPKTCheckCanBuyReq:
			case Packet.eAuction2SPKTAuctionItemsSyncRes:
			case Packet.eAuction2SPKTSelfItemsSyncRes:
			case Packet.eAuction2SPKTItemPricesSyncRes:
			case Packet.eAuction2SPKTSyncGroupBuyLog:
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
		managerRPC.onTCPAuctionClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuctionClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPAuctionClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// auction to server
		case Packet.eAuction2SPKTKeepAlive:
			{
				Packet.Auction2S.KeepAlive p = (Packet.Auction2S.KeepAlive)packet;
				managerRPC.onTCPAuctionClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eAuction2SPKTAdjustTimeOffset:
			{
				Packet.Auction2S.AdjustTimeOffset p = (Packet.Auction2S.AdjustTimeOffset)packet;
				managerRPC.onTCPAuctionClientRecvAdjustTimeOffset(this, p);
			}
			break;
		case Packet.eAuction2SPKTPutOnItemRes:
			{
				Packet.Auction2S.PutOnItemRes p = (Packet.Auction2S.PutOnItemRes)packet;
				managerRPC.onTCPAuctionClientRecvPutOnItemRes(this, p);
			}
			break;
		case Packet.eAuction2SPKTTimeOutPutOffItemsReq:
			{
				Packet.Auction2S.TimeOutPutOffItemsReq p = (Packet.Auction2S.TimeOutPutOffItemsReq)packet;
				managerRPC.onTCPAuctionClientRecvTimeOutPutOffItemsReq(this, p);
			}
			break;
		case Packet.eAuction2SPKTPutOffItemsRes:
			{
				Packet.Auction2S.PutOffItemsRes p = (Packet.Auction2S.PutOffItemsRes)packet;
				managerRPC.onTCPAuctionClientRecvPutOffItemsRes(this, p);
			}
			break;
		case Packet.eAuction2SPKTBuyItemsRes:
			{
				Packet.Auction2S.BuyItemsRes p = (Packet.Auction2S.BuyItemsRes)packet;
				managerRPC.onTCPAuctionClientRecvBuyItemsRes(this, p);
			}
			break;
		case Packet.eAuction2SPKTCheckCanBuyReq:
			{
				Packet.Auction2S.CheckCanBuyReq p = (Packet.Auction2S.CheckCanBuyReq)packet;
				managerRPC.onTCPAuctionClientRecvCheckCanBuyReq(this, p);
			}
			break;
		case Packet.eAuction2SPKTAuctionItemsSyncRes:
			{
				Packet.Auction2S.AuctionItemsSyncRes p = (Packet.Auction2S.AuctionItemsSyncRes)packet;
				managerRPC.onTCPAuctionClientRecvAuctionItemsSyncRes(this, p);
			}
			break;
		case Packet.eAuction2SPKTSelfItemsSyncRes:
			{
				Packet.Auction2S.SelfItemsSyncRes p = (Packet.Auction2S.SelfItemsSyncRes)packet;
				managerRPC.onTCPAuctionClientRecvSelfItemsSyncRes(this, p);
			}
			break;
		case Packet.eAuction2SPKTItemPricesSyncRes:
			{
				Packet.Auction2S.ItemPricesSyncRes p = (Packet.Auction2S.ItemPricesSyncRes)packet;
				managerRPC.onTCPAuctionClientRecvItemPricesSyncRes(this, p);
			}
			break;
		case Packet.eAuction2SPKTSyncGroupBuyLog:
			{
				Packet.Auction2S.SyncGroupBuyLog p = (Packet.Auction2S.SyncGroupBuyLog)packet;
				managerRPC.onTCPAuctionClientRecvSyncGroupBuyLog(this, p);
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
