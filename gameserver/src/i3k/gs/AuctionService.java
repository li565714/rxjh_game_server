package i3k.gs;
import i3k.SBean;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AuctionService
{
	public AuctionService(GameServer gs)
	{
		this.gs = gs;
	}
	
	void execTask(AuctionServiceTask task)
	{
		tasks.put(task.id, task);
		task.doTask();
	}
	
	void onTimer(int timeTick)
	{
		checkTimeOutTask(timeTick);
	}
	
	private void checkTimeOutTask(int timeTick)
	{
		List<AuctionServiceTask> timeoutTasks = getTimeOutTasks(timeTick);
		timeoutTasks.forEach(AuctionServiceTask::onTimeout);
	}
	
	private List<AuctionServiceTask> getTimeOutTasks(int timeTick)
	{
		List<AuctionServiceTask> timeoutTasks = new ArrayList<>();
		Iterator<AuctionServiceTask> it = tasks.values().iterator();
		while(it.hasNext())
		{
			AuctionServiceTask task = it.next();
			if(task.isTooOld(timeTick))
			{
				timeoutTasks.add(task);
				it.remove();
			}
		}
		
		return timeoutTasks;
	}
	
	public AuctionServiceTask peekTask(int id)
	{
		AuctionServiceTask task = tasks.remove(id);
		if(task == null)
			gs.getLogger().warn("AuctionService can't find task id : " + id);
		
		return task;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public abstract class AuctionServiceTask
	{
		final static int MAX_WAIT_TIME = 5;
		final static int TIME_OUT_ERROR = -100;
		final int id;
		int sendTime;
		AuctionServiceTask()
		{
			this.id = nextTaskID.incrementAndGet();
		}
		
		boolean isTooOld(int now)
		{
			return sendTime + MAX_WAIT_TIME <= now;
		}
		
		void doTask()
		{
			sendTime = GameTime.getTime();
			doTaskImpl();
		}
		
		abstract void doTaskImpl();
		
		abstract void onTimeout();
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface PutOnItemCallBack
	{
		void onCallback(int cid);
	}
	
	public class PutOnItemTask extends AuctionServiceTask
	{
		int roleID;
		SBean.DBConsignItems items;
		PutOnItemCallBack callback;
		
		PutOnItemTask(int roleID, SBean.DBConsignItems items, PutOnItemCallBack callback)
		{
			this.roleID = roleID;
			this.items = items;
			this.callback = callback;
		}
		
		void onCallback(int cid)
		{
			callback.onCallback(cid);
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyAuctionPutOnItems(this.id, roleID, items);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
	}
	
	public void putOnItemImpl(int roleID, SBean.DBConsignItems items, PutOnItemCallBack callback)
	{
		execTask(new PutOnItemTask(roleID, items, callback));
	}
	
	public void handlePutOnItemTaskResponse(int tag, int cid)
	{
		AuctionServiceTask task = this.peekTask(tag);
		if(task == null)
			return;
		
		if(task instanceof PutOnItemTask)
		{
			PutOnItemTask pt = PutOnItemTask.class.cast(task);
			if(pt != null)
				pt.onCallback(cid);
		}
	}
	
	
	public interface PutOffItemsCallBack
	{
		void onCallback(int errCode, SBean.DBConsignItems items);
	}
	
	public class PutOffItemsTask extends AuctionServiceTask
	{
		int roleID;
		int cid;
		PutOffItemsCallBack callback;
		
		PutOffItemsTask(int roleID, int cid, PutOffItemsCallBack callback)
		{
			this.roleID = roleID;
			this.cid = cid;
			this.callback = callback;
		}
		
		void onCallback(int errCode, SBean.DBConsignItems items)
		{
			callback.onCallback(errCode, items);
		}
		
		@Override
		void doTaskImpl() 
		{
			gs.getRPCManager().notifyAuctionPutOffItems(this.id, roleID, cid);
		}

		@Override
		void onTimeout() 
		{
			callback.onCallback(TIME_OUT_ERROR, null);
		}
	}
	
	public void putOffItemsImpl(int roleID, int cid, PutOffItemsCallBack callback)
	{
		execTask(new PutOffItemsTask(roleID, cid, callback));
	}
	
	public void handlePutOffItemsResponse(int tagID, int errCode, SBean.DBConsignItems items)
	{
		AuctionServiceTask task = this.peekTask(tagID);
		if(task instanceof PutOffItemsTask)
		{
			PutOffItemsTask pt = PutOffItemsTask.class.cast(task);
			if(pt != null)
				pt.onCallback(errCode, items);
		}
	}
	
	
	public interface BuyItemsCallBack
	{
		void onCallback(SBean.DBConsignItems items);
	}
	
	public class BuyItemsTask extends AuctionServiceTask
	{
		int sellerServerID;
		int sellerID;
		int cid;
		int price;
		BuyItemsCallBack callback;
		
		BuyItemsTask( int sellerID, int cid, int price, BuyItemsCallBack callback)
		{
			this.sellerID = sellerID;
			this.cid = cid;
			this.price = price;
			this.callback = callback;
		}
		
		void onCallback(SBean.DBConsignItems items)
		{
			callback.onCallback(items);
		}
		
		@Override
		void doTaskImpl() 
		{
			gs.getRPCManager().notifyAuctionBuyItems(this.id, sellerID, cid, price);
		}

		@Override
		void onTimeout() 
		{
			callback.onCallback(null);
		}
	}
	
	public void buyItemsImpl(int sellerID, int cid, int price, BuyItemsCallBack callback)
	{
		execTask(new BuyItemsTask(sellerID, cid, price, callback));
	}
	
	public void handleBuyItemsResponse(int tagID, SBean.DBConsignItems items)
	{
		AuctionServiceTask task = this.peekTask(tagID);
		if(task instanceof BuyItemsTask)
		{
			BuyItemsTask bt = BuyItemsTask.class.cast(task);
			if(bt != null)
				bt.onCallback(items);
		}
	}
	
	public interface AuctionItemsSyncCallBack
	{
		void onCallback(List<SBean.DetailConsignItems> items, int lastPage);
	}
	
	public class AuctionItemsSyncTask extends AuctionServiceTask
	{
		int itemType;
		int classType;
		int rank;
		int level;
		int order;
		int page;
		String name;
		AuctionItemsSyncCallBack callback;
		
		AuctionItemsSyncTask(int itemType, int classType, int rank, int level, int order, int page, String name, AuctionItemsSyncCallBack callback)
		{
			this.itemType = itemType;
			this.classType = classType;
			this.rank = rank;
			this.level = level;
			this.order = order;
			this.page = page;
			this.name = name;
			this.callback = callback;
		}
		
		void onCallback(List<SBean.DetailConsignItems> items, int lastPage)
		{
			callback.onCallback(items, lastPage);
		}
		
		@Override
		void doTaskImpl() 
		{
			gs.getRPCManager().notifyAuctionItemsSync(this.id, itemType, classType, rank, level, order, page, name);
		}

		@Override
		void onTimeout() 
		{
			callback.onCallback(null, 1);
		}
	}
	
	public void auctionItemsSyncImpl(int itemType, int classType, int rank, int level, int order, int page, String name, AuctionItemsSyncCallBack callback)
	{
		execTask(new AuctionItemsSyncTask(itemType, classType, rank, level, order, page, name, callback));
	}
	
	public void handleAuctionItemsSyncRes(int tagID, List<SBean.DetailConsignItems> items, int lastPage)
	{
		AuctionServiceTask task = this.peekTask(tagID);
		if(task instanceof AuctionItemsSyncTask)
		{
			AuctionItemsSyncTask at = AuctionItemsSyncTask.class.cast(task);
			if(at != null)
				at.onCallback(items, lastPage);
		}
	}
	
	public interface SelfItemsSyncCallBack
	{
		void onCallback(Map<Integer, SBean.DBConsignItems> items);
	}
	
	public class SelfItemsSyncTask extends AuctionServiceTask
	{
		int roleID;
		SelfItemsSyncCallBack callback;
		
		SelfItemsSyncTask(int roleID, SelfItemsSyncCallBack callbakc)
		{
			this.roleID = roleID;
			this.callback = callbakc;
		}
		
		void onCallback(Map<Integer, SBean.DBConsignItems> items)
		{
			callback.onCallback(items);
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyAuctionSyncSelfItems(this.id, roleID);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(null);
		}
	}
	
	public void selfItemSyncImpl(int roleID, SelfItemsSyncCallBack callback)
	{
		execTask(new SelfItemsSyncTask(roleID, callback));
	}
	
	public void handleSelfItemsSyncResponse(int tagID, Map<Integer, SBean.DBConsignItems> items)
	{
		AuctionServiceTask task = this.peekTask(tagID);
		if(task instanceof SelfItemsSyncTask)
		{
			SelfItemsSyncTask st = SelfItemsSyncTask.class.cast(task);
			if(st != null)
				st.onCallback(items);
		}
	}
	
	public interface ItemPricesSyncCallBack
	{
		void onCallbakc(List<SBean.DBConsignItems> prices);
	}
	
	public class ItemPricesSyncTask extends AuctionServiceTask
	{
		int itemID;
		ItemPricesSyncCallBack callback;
		
		ItemPricesSyncTask(int itemID, ItemPricesSyncCallBack callback)
		{
			this.itemID = itemID;
			this.callback = callback;
		}
		
		void onCallback(List<SBean.DBConsignItems> prices)
		{
			callback.onCallbakc(prices);
		}

		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyAuctionItemPricesSyncReq(this.id, itemID);
		}

		@Override
		void onTimeout()
		{
			callback.onCallbakc(null);
		}
	}
	
	public void itemPricesSyncImpl(int itemID, ItemPricesSyncCallBack callback)
	{
		execTask(new ItemPricesSyncTask(itemID, callback));
	}
	
	public void handleItemPricesSyncResponse(int tagID, List<SBean.DBConsignItems> prices)
	{
		AuctionServiceTask task = this.peekTask(tagID);
		if(task instanceof ItemPricesSyncTask)
		{
			ItemPricesSyncTask ipst = ItemPricesSyncTask.class.cast(task);
			if(ipst != null)
				ipst.onCallback(prices);
		}
	}
//--------------------------------------------------------------------------------------------------------------------------------------------	
	public void handleCheckCanBuy(int tagID, int sellerID, int cid, SBean.DBConsignItems items)
	{
		gs.getLoginManager().exeCommonRoleVisitor(sellerID, true, new LoginManager.CommonRoleVisitor()
		{
			boolean canBuy = false;
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				canBuy = role.checkAuctionItemsCanBuy(cid, items);
				return canBuy;
			}
			
			@Override
			public void onCallback(boolean success)
			{
				gs.getRPCManager().notifyAuctionCheckCanBuyRes(tagID, canBuy ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED);
			}
		});
	}
	
	public void handleTimeOutPutOffItemsReq(int tagID, int roleID, int cid, SBean.DBConsignItems items)
	{
		gs.getLoginManager().exeCommonRoleVisitor(roleID, true, new LoginManager.CommonRoleVisitor()
		{
			int errCode = GameData.PROTOCOL_OP_FAILED;
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				errCode = role.timeOutPutOffItems(cid, items);
				return errCode > 0;
			}
			
			@Override
			public void onCallback(boolean success)
			{
				gs.getRPCManager().nofifyAuctionTimeOutPutOffItemsRes(tagID, errCode);
			}
		});
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private GameServer gs;
	private AtomicInteger nextTaskID = new AtomicInteger();
	private ConcurrentMap<Integer, AuctionServiceTask> tasks = new ConcurrentHashMap<>();
}
