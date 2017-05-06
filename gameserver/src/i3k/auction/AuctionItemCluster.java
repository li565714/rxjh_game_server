package i3k.auction;


import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AuctionItemCluster
{
	public static final int ITEM_COMMON_RANK 		= 0;
	public static final int ITEM_COMMON_LEVEL 		= 0;
	public static final int ITEM_COMMON_CLASSTYPE 	= 0;
	
	public static final int IIEM_ORDER_PRICE	 		= 1;		//价格
	public static final int IIEM_ORDER_POWERORRANK	 	= 2;		//战力or品质

	public static final int RAND_INTERVAL_MAX			= 5;
	
	final int randTick;
	AuctionServer as;
	
	Map<Integer, ConsignItemsInfo> delayItems = new HashMap<>();
	Map<Integer, ConsignItemsInfo> showItems = new HashMap<>();
	
	Map<Integer, RankItems> rankItems = new HashMap<>();
	Map<Integer, ItemPrice> itemPrices = new HashMap<>();			//推荐价格
	
	AuctionItemCluster(AuctionServer as)
	{
		randTick = GameRandom.getRandInt(0, RAND_INTERVAL_MAX);
		this.as = as;
	}
	
	synchronized void onTimer(int timeTick)
	{
		if(timeTick % RAND_INTERVAL_MAX == randTick)
		{
			trySetItemsShow(timeTick);
		}
	}
	
	private void trySetItemsShow(int timeTick)
	{
		Iterator<Map.Entry<Integer, ConsignItemsInfo>> it = delayItems.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Integer, ConsignItemsInfo> e = it.next();
			if(timeTick < e.getValue().dItems.items.showTime)
				continue;
			
			setItemsShow(e.getKey(), e.getValue());
			it.remove();
		}
	}
	
	private ConsignItemsInfo getItems(int cid)
	{
		if(delayItems.containsKey(cid))
			return delayItems.get(cid);
		
		return showItems.get(cid);
	}
	
	private void setItemsShow(int cid, ConsignItemsInfo cItems)
	{
		this.showItems.put(cid, cItems);
		this.putOnItems(cid, cItems);
		ItemPrice prices = itemPrices.get(cItems.dItems.items.id);
		if(prices == null)
		{
			prices = new ItemPrice();
			itemPrices.put(cItems.dItems.items.id, prices);
		}
		prices.addItem(cid, cItems.dItems.items);
		
		
		setItemsShowImpl(cid, cItems, ITEM_COMMON_RANK);
		if(cItems.dItems.items.equip != null)
			setItemsShowImpl(cid, cItems, cItems.rank);
	}
	
	synchronized void putOnItems(int cid, ConsignItemsInfo cItems)
	{
		this.delayItems.put(cid, cItems);
	}
	
	synchronized void delItems(int cid)
	{
		if(delayItems.remove(cid) != null)
			return;
		
		ConsignItemsInfo cItems = showItems.remove(cid);
		if(cItems == null)
			return;
		
		ItemPrice ip =  itemPrices.get(cItems.dItems.items.id);
		if(ip != null)
			ip.delItem(cid, cItems.dItems.items.price / cItems.dItems.items.count);
		
		delItemsImpl(cid, cItems, ITEM_COMMON_RANK);
		if(cItems.dItems.items.equip != null)
			delItemsImpl(cid, cItems, cItems.rank);
	}
	
	private void setItemsShowImpl(int cid, ConsignItemsInfo citems, int rank)
	{
		RankItems ri = rankItems.get(rank);
		if(ri == null)
		{
			ri = new RankItems();
			rankItems.put(rank, ri);
		}
		
		ri.addItems(cid, citems);
	}
	
	private void delItemsImpl(int cid, ConsignItemsInfo cItems, int rank )
	{
		RankItems ri = rankItems.get(rank);
		if(ri == null)
			return;
		
		ri.delItems(cid, cItems);
	}
	
	synchronized List<SBean.DetailConsignItems> getPageItems(int classType, int rank, int level, int order, int page, String name, boolean reverse)
	{
		RankItems ri = rankItems.get(rank);
		if(ri == null)
			return new ArrayList<>();
		
		return ri.getPageItems(classType, level, order, page, name, reverse);
	}
	
	synchronized List<SBean.DBConsignItems> getItemPrices(int itemID)
	{
		ItemPrice ip =  itemPrices.get(itemID);
		return ip == null ? GameData.emptyList() : ip.getShowItems();
	}
	
	//-------------------------------------------------------------------------------------------
	class RankItems
	{
		Map<Integer, LevelItems> lvlItems = new HashMap<>();
		
		void addItems(int cid, ConsignItemsInfo cItems)
		{
			addItemsImpl(cid, cItems, ITEM_COMMON_LEVEL);
			if(cItems.dItems.items.equip != null)
				addItemsImpl(cid, cItems, cItems.level);
		}
		
		void delItems(int cid, ConsignItemsInfo cItems)
		{
			delItemsImpl(cid, cItems, ITEM_COMMON_LEVEL);
			if(cItems.dItems.items.equip != null)
				delItemsImpl(cid, cItems, cItems.level);
		}
		
		void addItemsImpl(int cid, ConsignItemsInfo cItems, int lvl)
		{
			LevelItems li = lvlItems.get(lvl);
			if(li == null)
			{
				li = new LevelItems();
				lvlItems.put(lvl, li);
			}
			
			li.addItems(cid, cItems);
		}
		
		void delItemsImpl(int cid, ConsignItemsInfo cItems, int lvl)
		{
			LevelItems li = lvlItems.get(lvl);
			if(li == null)
				return;
			
			li.delItems(cid, cItems);
		}
		
		List<SBean.DetailConsignItems> getPageItems(int classType, int level, int order, int page, String name, boolean reverse)
		{
			LevelItems li = lvlItems.get(level);
			if(li == null)
				return new ArrayList<>();
			
			return li.getPageItems(classType, order, page, name, reverse);
		}
	}
	
	
	//-------------------------------------------------------------------------------------------
	class LevelItems
	{
		Map<Integer, ClassTypeItems> ctItems = new HashMap<>();
		
		void addItems(int cid, ConsignItemsInfo cItems)
		{
			addItemsImpl(cid, cItems, ITEM_COMMON_CLASSTYPE);
			if(cItems.classType > 0)
			{
				addItemsImpl(cid, cItems, cItems.classType);
			}
			else if(cItems.classType == -1)
			{
				for(int classType = GameData.CLASS_TYPE_START; classType <= GameData.CLASS_TYPE_END; classType ++)
				{
					addItemsImpl(cid, cItems, classType);
				}
			}
		}
		
		void delItems(int cid, ConsignItemsInfo cItems)
		{
			delItemsImpl(cid, cItems, ITEM_COMMON_CLASSTYPE);
			if(cItems.classType > 0)
			{
				delItemsImpl(cid, cItems, cItems.classType);
			}
			else if(cItems.classType == -1)
			{
				for(int classType = GameData.CLASS_TYPE_START; classType <= GameData.CLASS_TYPE_END; classType ++)
				{
					delItemsImpl(cid, cItems, classType);
				}
			}
		}
		
		void addItemsImpl(int cid, ConsignItemsInfo cItems, int classType)
		{
			ClassTypeItems cti = ctItems.get(classType);
			if(cti == null)
			{
				cti = new ClassTypeItems();
				ctItems.put(classType, cti);
			}
			
			cti.addItems(cid, cItems);
		}
		
		void delItemsImpl(int cid, ConsignItemsInfo cItems, int classType)
		{
			ClassTypeItems cti = ctItems.get(classType);
			if(cti == null)
				return;
			
			cti.delItems(cid, cItems);
		}
		
		List<SBean.DetailConsignItems> getPageItems(int classType, int order, int page, String name, boolean reverse)
		{
			ClassTypeItems cti = ctItems.get(classType);
			if(cti == null)
				return new ArrayList<>();
			
			return cti.getPageItems(order, page, name, reverse);
		}
	}
	
	//-------------------------------------------------------------------------------------------
	public static class ItemPattern
	{
		Pattern namePattern;
		int count;
		ItemPattern(String itemName)
		{
			namePattern = Pattern.compile(Pattern.quote(itemName));
			count = 1;
		}
		
		ItemPattern addItem()
		{
			count++;
			return this;
		}
		
		boolean delItem()
		{
			return --count <= 0;
		}
		
		boolean isEmpty()
		{
			return count <= 0;
		}
	}
	
	class ClassTypeItems
	{
		TreeMap<Integer, OrderItems> priceItems = new TreeMap<>();
		TreeMap<Integer, OrderItems> powerOrRankItems = new TreeMap<>();
		Map<String, Integer> itemNames = new HashMap<>();
		private int totalCnt;
		
		void addItems(int cid, ConsignItemsInfo cItems)
		{
			int price = cItems.dItems.items.price;
			int powerOrRank = cItems.dItems.items.equip == null ? cItems.rank : cItems.fightPower;
			
			OrderItems pi = priceItems.get(price);
			if(pi == null)
			{
				pi = new OrderItems();
				priceItems.put(price, pi);
			}
			pi.addItems(powerOrRank, cid);
			
			OrderItems pori = powerOrRankItems.get(powerOrRank);
			if(pori == null)
			{
				pori = new OrderItems();
				powerOrRankItems.put(powerOrRank, pori);
			}
			pori.addItems(price, cid);
			
			itemNames.compute(cItems.name, (k,v) -> v == null ? 1 : v + 1);
			totalCnt++;
		}
		
		void delItems(int cid, ConsignItemsInfo cItems)
		{
			int price = cItems.dItems.items.price;
			int powerOrRank = cItems.dItems.items.equip == null ? cItems.rank : cItems.fightPower;
			
			boolean del = false;
			OrderItems pi = priceItems.get(price);
			if(pi != null)
			{
				if(pi.delItems(powerOrRank, cid))
					del = true;
				
				if(pi.isEmpty())
					priceItems.remove(price);
			}
			
			OrderItems pori = powerOrRankItems.get(powerOrRank);
			if(pori != null)
			{
				if(pori.delItems(price, cid))
					del = true;
				
				if(pori.isEmpty())
					powerOrRankItems.remove(powerOrRank);
			}
			
			Integer count = itemNames.get(cItems.name);
			if(count != null)
			{
				count--;
				if(count > 0)
					itemNames.put(cItems.name, count);
				else
					itemNames.remove(cItems.name);
			}
			
			if(del)
			{
				totalCnt--;
			}
			else
			{
				AuctionItemCluster.this.as.getLogger().warn("del auction item " + cItems + " failed, totalCnt " + totalCnt);
			}
		}
		
		List<SBean.DetailConsignItems> getPageItems(int order, int page, String name, boolean reverse)
		{		
			List<SBean.DetailConsignItems> items = new ArrayList<>();
			Pattern p = name.isEmpty() ? null : Pattern.compile(Pattern.quote(name));
			if(p != null && !checkHasItem(name, p))
				return items;
			
			int fromIndex = (page - 1) * AuctionManager.ITEM_COUNT_PERPAGE + 1;
			Map<Integer, OrderItems> need = reverse ? priceItems.descendingMap() : priceItems;
			if(Math.abs(order) == IIEM_ORDER_POWERORRANK)
				need = reverse ? powerOrRankItems.descendingMap() : powerOrRankItems;
			
			if(fromIndex > totalCnt)
				return items;
			
			int index = 1;
			for(OrderItems e: need.values())
			{
				for(IntTreeSet its: e.map.values())
				{
					for(int cid: its.all)
					{
						ConsignItemsInfo cItems = AuctionItemCluster.this.getItems(cid);
						if(cItems == null)
							continue;
						
						if(p != null && !isMatch(p, cItems.name))
							continue;
						
						if(index >= fromIndex)
							items.add(cItems.dItems);
						
						if(items.size() >= AuctionManager.ITEM_COUNT_PERPAGE + 1)
							return items;
						
						index++;
					}
				}
			}
			
			return items;
		}
		
		boolean checkHasItem(String needItemName, Pattern p)
		{
			if(itemNames.containsKey(needItemName))
				return true;
			
			for(String name: itemNames.keySet())
			{
				if(isMatch(p, name))
					return true;
			}
			
			return false;
		}
		
		boolean isMatch(Pattern p, String lowCaseName)
		{
			Matcher m = p.matcher(lowCaseName);
			return m.find();
		}
	}
	
	//-------------------------------------------------------------------------------------------
	class OrderItems
	{
		TreeMap<Integer, IntTreeSet> map = new TreeMap<>();
		void addItems(int key, int cid)
		{
			IntTreeSet its = map.get(key);
			if(its == null)
			{
				its = new IntTreeSet();
				map.put(key, its);
			}
			its.all.add(cid);
		}
		
		boolean delItems(int key, int cid)
		{
			IntTreeSet its = map.get(key);
			if(its == null)
				return false;
			
			boolean has = its.all.remove(cid);
			if(its.all.isEmpty())
				map.remove(key);
			return has;
		}
		
		boolean isEmpty()
		{
			return map.isEmpty();
		}
	}
	
	//-------------------------------------------------------------------------------------------
	class IntTreeSet
	{
		TreeSet<Integer> all = new TreeSet<>();
	}
	
	//-------------------------------------------------------------------------------------------
	class ItemPrice
	{
		private static final int MAX_ITEM_COUNT 	= 30;
		private static final int SHOW_ITEM_COUNT 	= 5;
		
		TreeMap<Long, SBean.DBConsignItems> prices;		//<price, DBConsignItems>
		ItemPrice()
		{
			prices = new TreeMap<>();
		}
		
		void addItem(int cid, SBean.DBConsignItems items)
		{
			int price = items.price / items.count;
			prices.put(GameData.getLongTypeValue(price, cid), items);
			if(prices.size() > MAX_ITEM_COUNT)
				prices.pollLastEntry();
		}
		
		void delItem(int cid, int price)
		{
			prices.remove(GameData.getLongTypeValue(price, cid));
		}
		
		List<SBean.DBConsignItems> getShowItems()
		{
			return prices.values().stream().limit(SHOW_ITEM_COUNT).collect(Collectors.toList());
		}
	}
	
	//-------------------------------------------------------------------------------------------
	public static class ConsignItemsInfo
	{
		int fightPower = 0;
		int rank = 0;
		int level = 0;
		int classType = 0;			//0表示只在全部页签显示，-1表示在全部页签和每个子页签都显示
		SBean.DetailConsignItems dItems;
		String name;
		
		ConsignItemsInfo(SBean.DetailConsignItems dItems)
		{
			this.dItems = dItems;
		}
		
		ConsignItemsInfo create()
		{
			this.name = GameData.getInstance().getItemName(dItems.items.id);
			this.name = this.name == null ? "" : this.name;
			
			this.classType = GameData.getInstance().getItemClassType(dItems.items.id);
			this.rank = GameData.getInstance().getItemRank(dItems.items.id);
			if(this.dItems.items.equip != null)
			{
				this.fightPower = GameData.getInstance().getEquipFghtPower(this.dItems.items.equip);
				SBean.EquipCFGS equip = GameData.getInstance().getEquipCFG(dItems.items.id);
				if(equip != null)
					this.level = GameData.getInstance().getAuctionEquipGrade(equip.lvlReq);
			}
			return this;
		}
		
		public String toString()
		{
			return "cid " + dItems.cid + " rid " + dItems.roleID + " name " + this.name + " itemID " + dItems.items.id + " price " + dItems.items.price + " classType " + classType + " rank " + rank + " level " + level + " showTime " + GameTime.getDateTimeStampStr(dItems.items.showTime) + " outTime " + GameTime.getDateTimeStampStr(dItems.items.time);
		}
	}
}
