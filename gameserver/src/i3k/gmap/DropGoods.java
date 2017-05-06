package i3k.gmap;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import i3k.SBean;

public class DropGoods
{

	public DropGoods(int roleID)
	{
		this.roleID = roleID;
	}

	void addDropItem(DropItem dropItem)
	{
		this.allDrops.put(dropItem.dropID, dropItem);
	}

	DropItem delDropItem(int dropID)
	{
		return this.allDrops.remove(dropID);
	}

	DropItem getDropItem(int dropID)
	{
		return this.allDrops.get(dropID);
	}
	
	DropItem pickDropItem(int dropID)
	{
		DropItem dropItem = this.allDrops.get(dropID);
		if(dropItem == null || dropItem.picking)
			return null;
		
		dropItem.picking = true;
		return dropItem;
	}
	
	void resetDropItem(int dropID)
	{
		DropItem dropItem = this.allDrops.get(dropID);
		if(dropItem != null)
			dropItem.picking = false;
	}
	
	List<DropItem> getDropItemList()
	{
		List<DropItem> lst = new ArrayList<>();
		for (DropItem dropItem : allDrops.values())
		{
			lst.add(dropItem);
		}
		return lst;
	}

	int getMaxDropID()
	{
		return allDrops.isEmpty() ? 0 : allDrops.lastKey();
	}

	public int roleID;
	public TreeMap<Integer, DropItem> allDrops = new TreeMap<>();

	public static class DropItem
	{
		DropItem(int dropID, SBean.DummyGoods item, long clearTime, SBean.Vector3 position, int dropMonsterId)
		{
			this.dropID = dropID;
			this.item = item;
			this.clearTime = clearTime;
			this.position = position;
			this.dropMonsterId = dropMonsterId;
		}

		public int dropID;
		public SBean.DummyGoods item = new SBean.DummyGoods();
		public SBean.Vector3 position = new SBean.Vector3();
		public long clearTime;
		public int dropMonsterId;
		public boolean picking = false;;
	}
}
