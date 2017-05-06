package i3k.auction;

import i3k.SBean;
import i3k.DBRoleConsignments;
import i3k.auction.AuctionItemCluster.ConsignItemsInfo;
import i3k.gs.AuctionService;
import i3k.gs.GameData;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import ket.kdb.Table;
import ket.kdb.TableEntry;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.util.Stream;

public class AuctionManager 
{		
	public static final int ITEM_COUNT_PERPAGE			= 5;
	
	interface PutOnItemsTransCallBack
	{
		void onCallback(int cid, DBRoleConsignments roleConsignments);
	}
	
	public class PutOnItemsTrans implements Transaction
	{
		PutOnItemsTrans(int roleID, SBean.DBConsignItems items, PutOnItemsTransCallBack callback)
		{
			this.roleID = roleID;
			this.items = items;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			roleConsignments = consignments.get(roleID);
			if(roleConsignments == null)
				roleConsignments = new DBRoleConsignments(roleID);
			
			Integer maxID = maxids.get(MaxConsignIDKey);
			cid = maxID == null ? 1 : maxID + 1;
			roleConsignments.consignitems.put(cid, items);
			
			maxids.put(MaxConsignIDKey, cid);
			consignments.put(roleID, roleConsignments);
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			as.getLogger().info("role " + roleID + " put on items[" + items.id + ", " + items.count + "] cid " + cid + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode));
			callback.onCallback(cid, roleConsignments);
		}
		
		@AutoInit
		public Table<String, Integer> maxids;
		@AutoInit
		public Table<Integer, DBRoleConsignments> consignments;
		
		private static final String MaxConsignIDKey = "consignid";
		private int roleID;
		private SBean.DBConsignItems items;
		private PutOnItemsTransCallBack callback;
		
		private DBRoleConsignments roleConsignments;
		private int cid;
	}
	
	interface DelItemsTransCallBack
	{
		void onCallback(SBean.DBConsignItems items);
	}
	
	public class DelItemsTrans implements Transaction
	{
		
		DelItemsTrans(int roleID, int cid, DelItemsTransCallBack callback)
		{
			this.roleID = roleID;
			this.cid = cid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction() 
		{
			DBRoleConsignments roleConsignments = consignments.get(roleID);
			if(roleConsignments == null)
				return false;
			
			items = roleConsignments.consignitems.remove(cid);
			if(items == null)
				return false;
			
			consignments.put(roleID, roleConsignments);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			as.getLogger().info("del role " + roleID + " items[" + (items == null ? 0 : items.id) + ", " + (items == null ? 0 : items.count) + "] cid " + cid + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode));
			callback.onCallback(items);
		}
		
		@AutoInit
		public Table<Integer, DBRoleConsignments> consignments;

		private int roleID;
		private int cid;
		private DelItemsTransCallBack callback;
		
		private SBean.DBConsignItems items;
	}
	
	public static class ConsignmentsContainer
	{
		Map<Integer, DBRoleConsignments> mapr2c;						//<roleID, DBRoleConsignments>
		ConsignmentsContainer()
		{
			this.mapr2c = new HashMap<>();
		}
	}
	
	AuctionManager(AuctionServer as)
	{
		this.as = as;
		this.unActivitedMapz2c = new HashMap<>();
		this.activitedMapz2c = new HashMap<>();
	}
	
	void start()
	{	
		
	}
	
	void destroy()
	{

	}
	
	void onTimer(int timeTick)
	{
		this.checkTimeOutConsignments(timeTick);
		this.clusters.values().forEach(c -> c.onTimer(timeTick));
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
	void init(TableReadonly<Integer, DBRoleConsignments> consignments)
	{
		for (TableEntry<Integer, DBRoleConsignments> e : consignments)
		{
			DBRoleConsignments dbRoleConsignments = e.getValue();
			int zoneID = GameData.getZoneIdFromRoleId(dbRoleConsignments.roleID);
			ConsignmentsContainer container = this.unActivitedMapz2c.get(zoneID);
			if(container == null)
			{
				container = new ConsignmentsContainer();
				this.unActivitedMapz2c.put(zoneID, container);
			}
			container.mapr2c.put(dbRoleConsignments.roleID, dbRoleConsignments);
		}
		
		for(int type: GameData.getInstance().getAllAuctionTypes())
			this.clusters.put(type, new AuctionItemCluster(this.as));
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////
	synchronized void activateContainer(Set<Integer> zones)
	{
		for(int zoneID: zones)
		{
			ConsignmentsContainer container = this.unActivitedMapz2c.remove(zoneID);
			if(container == null)
				container = new ConsignmentsContainer();
			
			this.activitedMapz2c.put(zoneID, container);
			for(DBRoleConsignments roleConsignment: container.mapr2c.values())
			{
				for(Map.Entry<Integer, SBean.DBConsignItems> e: roleConsignment.consignitems.entrySet())
				{
					int cid = e.getKey();
					SBean.DBConsignItems items = e.getValue();
					int type = GameData.getInstance().getBagItemAuctionType(items.id);
					ConsignItemsInfo info = new ConsignItemsInfo(new SBean.DetailConsignItems(roleConsignment.roleID, cid, items)).create();
					
					AuctionItemCluster cluster = this.clusters.get(type);
					if(cluster == null)
						continue;
					
					cluster.putOnItems(cid, info);
				}
			}
		}
	}
	
	synchronized void unActivateContainer(Set<Integer> zones)
	{
		for(int zoneID: zones)
		{
			ConsignmentsContainer container = this.activitedMapz2c.remove(zoneID);
			if(container != null)
			{
				this.unActivitedMapz2c.put(zoneID, container);
				for(DBRoleConsignments roleConsignment: container.mapr2c.values())
				{
					for(Map.Entry<Integer, SBean.DBConsignItems> e: roleConsignment.consignitems.entrySet())
					{
						SBean.DBConsignItems items = e.getValue();
						AuctionItemCluster cluster = this.clusters.get(GameData.getInstance().getBagItemAuctionType(items.id));
						if(cluster != null)
							cluster.delItems(e.getKey());
					}
				}
			}
		}
	}
	
	void checkTimeOutConsignments(int timeTick)
	{
		Map<Integer, DBRoleConsignments> timeoutConsignments = getTimeOutConsignments(timeTick);
		for(DBRoleConsignments roleConsignments: timeoutConsignments.values())
		{
			for(Map.Entry<Integer, SBean.DBConsignItems> e: roleConsignments.consignitems.entrySet())
			{
				int roleID = roleConsignments.roleID;
				int zoneID = GameData.getZoneIdFromRoleId(roleConsignments.roleID);
				int cid = e.getKey();
				as.getGameService().timeOutPutOffItemsImpl(roleID, cid, e.getValue(), errCode -> {
                    if(errCode == -100)		//³¬Ê±
                    {
                    	synchronized(this)
                    	{
                    		ConsignmentsContainer container = AuctionManager.this.activitedMapz2c.get(zoneID);
                            if(container == null)
                                container = AuctionManager.this.unActivitedMapz2c.get(zoneID);

                            if(container != null)
                            {
                                DBRoleConsignments r = container.mapr2c.get(roleID);
                                if(r != null)
                                {
                                    //TODO
                                    e.getValue().time = timeTick + 10 * 60;
                                    r.consignitems.put(cid, e.getValue());
                                }
                            }
                    	}
                    }
                    else
					{
						as.getDB().execute(new DelItemsTrans(roleID, cid, items ->
						{
							synchronized(this)
							{
								if (items != null)
								{
									int type = GameData.getInstance().getBagItemAuctionType(items.id);
									AuctionItemCluster cluster = AuctionManager.this.clusters.get(type);
									if (cluster != null)
										cluster.delItems(cid);
								}
							}
						}));
					}
                });
			}
		}
	}
	
	synchronized Map<Integer, DBRoleConsignments> getTimeOutConsignments(int timeTick)
	{
		Map<Integer, DBRoleConsignments> timeoutConsignments = new HashMap<>();
		for(ConsignmentsContainer container: this.activitedMapz2c.values())
		{
			Iterator<DBRoleConsignments> it1 = container.mapr2c.values().iterator();
			while(it1.hasNext())
			{
				DBRoleConsignments roleConsignments = it1.next();
				Iterator<Map.Entry<Integer, SBean.DBConsignItems>> it2 = roleConsignments.consignitems.entrySet().iterator();
				while(it2.hasNext())
				{
					Map.Entry<Integer, SBean.DBConsignItems> e = it2.next();
					int cid = e.getKey();
					SBean.DBConsignItems items = e.getValue();
					if(timeTick > items.time)
					{
						DBRoleConsignments t = timeoutConsignments.get(roleConsignments.roleID);
						if(t == null)
						{
							t = new DBRoleConsignments(roleConsignments.roleID);
							timeoutConsignments.put(roleConsignments.roleID, t);
						}
						t.consignitems.put(cid, items);
						it2.remove();
					}
				}
			}
		}
		
		return timeoutConsignments;
	}
	
	synchronized void handlePutOnItem(int roleID, SBean.DBConsignItems items, AuctionService.PutOnItemCallBack callback)
	{
		int zoneID = GameData.getZoneIdFromRoleId(roleID);
		ConsignmentsContainer container = this.activitedMapz2c.get(zoneID);
		if(container == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}

		int type = GameData.getInstance().getBagItemAuctionType(items.id);
        AuctionItemCluster cluster = AuctionManager.this.clusters.get(type);
        if(cluster == null)
        {
        	callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
        }
		
        int now = GameTime.getTime();
		items.time = now + GameData.getInstance().getCommonCFG().auction.consignTime;
		items.showTime = now + GameRandom.getRandInt(GameData.getInstance().getCommonCFG().auction.putOnDelayMin, GameData.getInstance().getCommonCFG().auction.putOnDelayMax);
		as.getDB().execute(new PutOnItemsTrans(roleID, items, (cid, roleConsignments) -> {
            if(roleConsignments == null)
            {
                callback.onCallback(GameData.PROTOCOL_OP_FAILED);
                return;
            }

            synchronized(AuctionManager.this)
            {
                container.mapr2c.put(roleID, roleConsignments);
                cluster.putOnItems(cid, new ConsignItemsInfo(new SBean.DetailConsignItems(roleID, cid, items)).create());
            }

            callback.onCallback(cid);
        }));
	}
	
	synchronized void handlePutOffItems(int roleID, Integer cid, AuctionService.PutOffItemsCallBack callback)
	{
		int zoneID = GameData.getZoneIdFromRoleId(roleID);
		ConsignmentsContainer container = this.activitedMapz2c.get(zoneID);
		if(container == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_AUCTION_ALREAD_BUY, null);
			return;
		}
		
		DBRoleConsignments roleConsignments = container.mapr2c.get(roleID);
		if(roleConsignments == null || !roleConsignments.consignitems.containsKey(cid))
		{
			callback.onCallback(GameData.PROTOCOL_OP_AUCTION_ALREAD_BUY, null);
			return;
		}
		
		as.getDB().execute(new DelItemsTrans(roleID, cid, items ->
		{
            if(items == null)
            {
                callback.onCallback(GameData.PROTOCOL_OP_AUCTION_ALREAD_BUY, null);
                return;
            }

            synchronized(AuctionManager.this)
            {
                roleConsignments.consignitems.remove(cid);
                int type = GameData.getInstance().getBagItemAuctionType(items.id);
                AuctionItemCluster cluster = AuctionManager.this.clusters.get(type);
                if(cluster != null)
                    cluster.delItems(cid);
            }

            callback.onCallback(GameData.PROTOCOL_OP_SUCCESS, items);
        }));
	}
	
	synchronized void handleBuyItems(int sellerID, Integer cid, int price, AuctionService.BuyItemsCallBack callback)
	{
		int sellerZoneID = GameData.getZoneIdFromRoleId(sellerID);
		ConsignmentsContainer container = this.activitedMapz2c.get(sellerZoneID);
		if(container == null)
		{
			callback.onCallback(null);
			return;
		}
		
		DBRoleConsignments roleConsignments = container.mapr2c.get(sellerID);
		if(roleConsignments == null)
		{
			callback.onCallback(null);
			return;
		}
		
		SBean.DBConsignItems items = roleConsignments.consignitems.get(cid);
		if(items == null || price != items.price)
		{
			callback.onCallback(null);
			return;
		}
		roleConsignments.consignitems.remove(cid);

		//check can buy
		as.getGameService().checkCanBuyImpl(sellerID, cid, items, errCode ->
		{
        	 if(errCode == -100)		//³¬Ê±
        	 {
        		 synchronized(AuctionManager.this)
        		 {
        			 roleConsignments.consignitems.put(cid, items);
                     callback.onCallback(null);
        		 }
        	 }
        	 else
        	 {
        		 as.getDB().execute(new DelItemsTrans(sellerID, cid, delItems -> {
        			 synchronized(AuctionManager.this)
        			 {
        				 if(delItems != null)
                         {
                             int type = GameData.getInstance().getBagItemAuctionType(delItems.id);
                             AuctionItemCluster cluster = AuctionManager.this.clusters.get(type);
                             if(cluster != null)
                                 cluster.delItems(cid);
                         }

                         callback.onCallback(errCode > 0 ? delItems : null);
        			 }
                 }));
        	 }
        });
	}
	
	void handleItemsSync(int itemType, int classType, int rank, int level, int order, int page, String name, AuctionService.AuctionItemsSyncCallBack callback)
	{
		AuctionItemCluster cluster = this.clusters.get(itemType);
		if(cluster == null)
		{
			callback.onCallback(GameData.emptyList(), 1);
			return;
		}
		
		boolean reverse = order < 0;
		List<SBean.DetailConsignItems> items = cluster.getPageItems(classType, rank, level, order, page, name, reverse);
		int lastPage = 0;
		if(items.size() > ITEM_COUNT_PERPAGE)
			items.remove(items.size() - 1);
		else
			lastPage = 1;

		callback.onCallback(items, lastPage);
	}
	
	synchronized void handleSelfItemsSync(int roleID, AuctionService.SelfItemsSyncCallBack callback)
	{
		int zoneID = GameData.getZoneIdFromRoleId(roleID);
		ConsignmentsContainer container = this.activitedMapz2c.get(zoneID);
		if(container == null)
		{
			callback.onCallback(GameData.emptyMap());
			return;
		}
		
		DBRoleConsignments roleConsignments = container.mapr2c.get(roleID);
		if(roleConsignments == null)
		{
			callback.onCallback(GameData.emptyMap());
			return;
		}
		
		callback.onCallback(Stream.clone(roleConsignments.consignitems));
	}
	
	List<SBean.DBConsignItems> getItemPrices(int itemID)
	{
		int type = GameData.getInstance().getBagItemAuctionType(itemID);
		AuctionItemCluster cluster = this.clusters.get(type);
		return cluster == null ? GameData.emptyList() : cluster.getItemPrices(itemID);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////
	AuctionServer as;
	Map<Integer, ConsignmentsContainer> unActivitedMapz2c;
	Map<Integer, ConsignmentsContainer> activitedMapz2c;
	
	//
	Map<Integer, AuctionItemCluster> clusters = new HashMap<>();
}
