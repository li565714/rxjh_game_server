package i3k.auction;
import i3k.SBean;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService
{
	public GameService(AuctionServer as)
	{
		this.as = as;
	}
	
	void execTask(GameServiceTask task)
	{
		tasks.put(task.id, task);
		task.doTask();
	}
	
	void onTimer(int timeTick)
	{
		checkTimeOutTask(timeTick);
	}
	
	void checkTimeOutTask(int timeTick)
	{
		List<GameServiceTask> timeoutTasks = getTimeOutTasks(timeTick);
		timeoutTasks.forEach(GameServiceTask::onTimeout);
	}
	
	List<GameServiceTask> getTimeOutTasks(int timeTick)
	{
		List<GameServiceTask> timeoutTasks = new ArrayList<>();
		Iterator<GameServiceTask> it = tasks.values().iterator();
		while(it.hasNext())
		{
			GameServiceTask task = it.next();
			if(task.isTooOld(timeTick))
			{
				timeoutTasks.add(task);
				it.remove();
			}
		}
		
		return timeoutTasks;
	}
	
	public GameServiceTask peekService(int id)
	{
		GameServiceTask task = tasks.remove(id);
		if(task == null)
			as.getLogger().warn("GameService can't find task id : " + id);
		
		return task;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public abstract class GameServiceTask
	{
		final static int MAX_WAIT_TIME = 2;
		final static int TIME_OUT_ERROR = -100;
		final int id;
		int sendTime;
		
		GameServiceTask()
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
	public interface TimeOutPutOffItemsCallBack
	{
		void onCallback(int errCode);
	}
	
	public class TimeOutPutOffItemsTask extends GameServiceTask
	{
		int roleID;
		int cid;
		SBean.DBConsignItems items;
		TimeOutPutOffItemsCallBack callback;

		TimeOutPutOffItemsTask(int roleID, int cid, SBean.DBConsignItems items, TimeOutPutOffItemsCallBack callback)
		{
			this.roleID = roleID;
			this.cid = cid;
			this.items = items;
			this.callback = callback;
		}
		
		void onCallback(int errCode)
		{
			callback.onCallback(errCode);
		}	
		
		@Override
		void doTaskImpl() 
		{
			as.getRPCManager().notifyGSRoleTimeOutPutOffItems(this.id, roleID, cid, items);
		}

		@Override
		void onTimeout() 
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
	}
	
	public void timeOutPutOffItemsImpl(int roleID, int cid, SBean.DBConsignItems items, TimeOutPutOffItemsCallBack callback)
	{
		execTask(new TimeOutPutOffItemsTask(roleID, cid, items, callback));
	}
	
	public void handleTimeOutPutOffItemsResponse(int tagID, int errCode)
	{
		GameServiceTask task = this.peekService(tagID);
		if(task instanceof TimeOutPutOffItemsTask)
		{
			TimeOutPutOffItemsTask tt = TimeOutPutOffItemsTask.class.cast(task);
			if(tt != null)
				tt.onCallback(errCode);
		}
	}
	
	
	public interface CheckCanBuyCallBack
	{
		void onCallback(int errCode);
	}
	
	public class CheckCanBuyTask extends GameServiceTask
	{
		int sellerID;
		int cid;
		SBean.DBConsignItems items;
		CheckCanBuyCallBack callback;
		
		CheckCanBuyTask(int sellerID, int cid, SBean.DBConsignItems items, CheckCanBuyCallBack callback)
		{
			this.sellerID = sellerID;
			this.cid = cid;
			this.items = items;
			this.callback = callback;
		}
		
		void onCallback(int errCode)
		{
			callback.onCallback(errCode);
		}
		
		@Override
		void doTaskImpl() 
		{
			as.getRPCManager().notifyGSRoleCheckCanBuy(this.id, sellerID, cid, items);
		}

		@Override
		void onTimeout() 
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
	}
	
	public void checkCanBuyImpl(int sellerID, int cid, SBean.DBConsignItems items, CheckCanBuyCallBack callback)
	{
		execTask(new CheckCanBuyTask(sellerID, cid, items, callback));
	}
	
	public void handleCheckCanBuyResponse(int tagID, int errCode)
	{
		GameServiceTask task = this.peekService(tagID);
		if(task instanceof CheckCanBuyTask)
		{
			CheckCanBuyTask ck = CheckCanBuyTask.class.cast(task);
			if(ck != null)
				ck.onCallback(errCode);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private AuctionServer as;
	private AtomicInteger nextTaskID = new AtomicInteger();
	private ConcurrentMap<Integer, GameServiceTask> tasks = new ConcurrentHashMap<>();
}
