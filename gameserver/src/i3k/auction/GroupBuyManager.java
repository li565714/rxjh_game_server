package i3k.auction;

import i3k.util.GameRandom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GroupBuyManager
{
	AuctionServer as;
	private ConcurrentMap<Integer, GroupGuyActivity> activities = new ConcurrentHashMap<>();		//<activityID, GroupGuyActivity>
	GroupBuyManager(AuctionServer as)
	{
		this.as = as;
	}
	
	void onTimer(int timeTick)
	{
		Iterator<GroupGuyActivity> it = activities.values().iterator();
		while(it.hasNext())
		{
			GroupGuyActivity gga = it.next();
			if(gga.onTimer(timeTick))
				it.remove();
		}
	}

	class GroupGuyActivity
	{	
		private final static int RAND_INTERVAL_MAX 		= 60;
		private final static int UPDATE_COUNT_INTERVAL 	= 60 * 10;
		final int randTick;
		
		private int lastSyncTime;
		private int activityID;
		private int endTime;
		private Map<Integer, ServerGroupBuy> servers = new HashMap<>();			//<serverID, ServerGroupBuy>
		private Map<Integer, Integer> globalBuyLogs = new HashMap<>();			//<gid, total>
		
		GroupGuyActivity(int activityID, int endTime)
		{
			this.activityID = activityID;
			this.endTime = endTime;
			randTick = GameRandom.getRandInt(0, RAND_INTERVAL_MAX);
		}
		
		synchronized boolean onTimer(int timeTick)
		{
			if(timeTick % RAND_INTERVAL_MAX == randTick)
			{
				if(timeTick > lastSyncTime + UPDATE_COUNT_INTERVAL)
				{
					syncGSBuyLog();
					lastSyncTime = timeTick;
				}
			}
			
			return timeTick > endTime;
		}
		
		void syncGSBuyLog()
		{
			if(globalBuyLogs.isEmpty())
				return;
			
			as.getRPCManager().notifyAllGSSyncGroupBuyLog(activityID, new HashMap<>(globalBuyLogs));
		}
		
		synchronized void updateGroupBuyGoods(int serverID, int gid, int totalCount)
		{
			ServerGroupBuy sgb = servers.get(serverID);
			if(sgb == null)
			{
				sgb = new ServerGroupBuy();
				servers.put(serverID, sgb);
			}
			
			int add = sgb.updateGroupBuyGoods(gid, totalCount);
			globalBuyLogs.compute(gid, (k,v) -> v == null ? add : add + v);
		}
		
		void updateGroupBuyGoods(int serverID, Map<Integer, Integer> logs)
		{
			for(Map.Entry<Integer, Integer> e: logs.entrySet())
				updateGroupBuyGoods(serverID, e.getKey(), e.getValue());
			
			as.getRPCManager().notifyGSSyncGroupBuyLog(serverID, activityID, new HashMap<>(globalBuyLogs));
		}
		
		synchronized void updateEndTime(int endTime)
		{
			this.endTime = endTime;
		}
	}
	
	class ServerGroupBuy
	{
		private Map<Integer, Integer> buyLogs = new HashMap<>();
		
		int updateGroupBuyGoods(int gid, int totalCount)
		{
			int old = buyLogs.getOrDefault(gid, 0); 
			buyLogs.put(gid, totalCount);
			return totalCount - old > 0 ? totalCount - old : 0;
		}
	}
	
	void updateGroupBuyGoods(int activityID, int serverID, int gid, int count, int endTime)
	{
		GroupGuyActivity ggc = activities.get(activityID);
		if(ggc == null)
		{
			ggc = new GroupGuyActivity(activityID, endTime);
			activities.put(activityID, ggc);
		}
		
		ggc.updateGroupBuyGoods(serverID, gid, count);
		ggc.updateEndTime(endTime);
	}
	
	void updateGroupBuyGoods(int activityID, int serverID, Map<Integer, Integer> logs, int endTime)
	{
		GroupGuyActivity ggc = activities.get(activityID);
		if(ggc == null)
		{
			ggc = new GroupGuyActivity(activityID, endTime);
			activities.put(activityID, ggc);
		}
		
		ggc.updateGroupBuyGoods(serverID, logs);
		ggc.updateEndTime(endTime);
	}
}
