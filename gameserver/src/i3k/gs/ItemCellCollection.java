
package i3k.gs;

import i3k.SBean;

import java.util.*;


public class ItemCellCollection
{	
	public ItemCellCollection(int cellSize, int expandTimes)
	{
		this.cellSize = cellSize;
		this.expandTimes = expandTimes;
	}
	
	public ItemCellCollection(SBean.DBItemCells cells)
	{
		this(cells.cellSize, cells.expandTimes, cells.items.values());
	}
	
	public ItemCellCollection(int cellSize, int expandTimes, Collection<SBean.GameItem> items)
	{
		this.cellSize = cellSize;
		this.expandTimes = expandTimes;
		this.container.clear();
		this.cellUsedSize = 0;
		for (SBean.GameItem e : items)
		{
			CellItem bi = CellItem.fromGameItem(e);
			if (bi != null)
			{
				this.container.put(bi.id, bi);
				this.cellUsedSize += calcUseCellSize(bi.getCount(), bi.getCellStackMax());
			}
		}
	}
	
	public SBean.DBItemCells toDB()
	{
		return new SBean.DBItemCells(this.cellSize, this.expandTimes, this.copyAllCellItemsWithoutLock());
	}
	
	public int getCellSize()
	{
		return this.cellSize;
	}
	
	public int getExpandTimes()
	{
		return this.expandTimes;
	}
	
	public void expand(int count)
	{
		cellSize += count;
		expandTimes++;
	}
	
	
	public Map<Integer, SBean.GameItem> copyAllCellItemsWithoutLock()
	{
		Map<Integer, SBean.GameItem> map = new TreeMap<>();
		for (CellItem bi : container.values())
		{
			SBean.GameItem gi = bi.toGameItem();
			if (gi != null)
				map.put(gi.id, gi);
		}
		return map;
	}
	
	public interface ItemIdFilter
	{
		boolean filter(int id);
	}
	public Set<Integer> getItemTypeIds(ItemIdFilter filter)
	{
		Set<Integer> itemids = new HashSet<Integer>();
		for (int id : container.keySet())
		{
			int absid = id < 0 ? -id : id;
			if (!filter.filter(absid))
				itemids.add(absid);
		}
		return itemids;
	}
	
	public int getItemTypeCount(int id)
	{
		return getCount(id) + getCount(-id);
	}
	
	public CellItem getCellItem(int id)
	{
		return container.get(id);
	}

	
	public int getCount(int id)
	{
		CellItem bi = container.get(id);
		return bi == null ? 0 : bi.getCount();
	}
	
	private int calcCellUseIncreaseIfAdd(int id, int count)
	{
		int stackMax = getCellItemStackMax(id);
		return calcCellUseIncreaseIfAdd(getCount(id), count, stackMax);
	}
	
	public boolean canPutIn(int id, int count)
	{
		return calcCellUseIncreaseIfAdd(id, count) + cellUsedSize <= cellSize;
	}
	
	public boolean canPutIn(Map<Integer, Integer> counter)
	{
		return cellUseIfPutIn(counter) + cellUsedSize <= cellSize;
	}
	
	public boolean canPutIn(List<SBean.GameItem> gis)
	{
		return cellUseIfPutIn(GameData.toCounter(gis)) + cellUsedSize <= cellSize;
	}
	
	public boolean canPutIn(Collection<SBean.DummyGoods> dgs)
	{
		return cellUseIfPutIn(GameData.toCounter(dgs)) + cellUsedSize <= cellSize;
	}
	
	private int cellUseIfPutIn(Map<Integer, Integer> counter)
	{
		int cellUseAdd = 0;
		for (Map.Entry<Integer, Integer> e : counter.entrySet())
		{
			cellUseAdd += calcCellUseIncreaseIfAdd(e.getKey(), e.getValue());
		}
		return cellUseAdd;
	}
	
	int putIn(SBean.GameItem item)
	{
		if (item == null || item.count <= 0)
			return 0;
		int stackMax = getCellItemStackMax(item.id);
		if (stackMax <= 0)
			return 0;
		int addCount = 0;
		CellItem bi = container.get(item.id);
		if (bi == null)
		{
			bi = CellItem.fromGameItem(item);
			int useCellAdd = calcUseCellSize(bi.getCount(), stackMax);
			container.put(bi.id, bi);
			cellUsedSize += useCellAdd;
			addCount = bi.getCount();
		}
		else
		{
			int beforeAddCount = bi.getCount();
			addCount = bi.add(item);
			cellUsedSize += calcCellUseIncreaseIfAdd(beforeAddCount, addCount, stackMax);
		}
		return addCount;
	}

	public int getCanUseSize()
	{
		return this.cellSize - this.cellUsedSize;
	}
	
	public boolean containsEnough(int id, int count)
	{
		if (id > 0)
			return this.getCount(id) + this.getCount(-id) >= count;
		else
			return this.getCount(id) >= count;
	}
	
	public boolean containsEquip(int id, String guid)
	{
		CellItem bi = container.get(id);
		return bi != null && bi.contains(guid);
	}

	public int calcSafeDel(int id, int count)
	{
		int hasCount = this.getCount(id);
		return hasCount > count ? count : hasCount;
	}
	
	public int del(int id, int count)
	{
		if (count <= 0)
			return 0;
		CellItem bi = container.get(id);
		if (bi == null || bi.getCount() < count)
			return 0;
		int stackMax = getCellItemStackMax(bi.getID());
		if (stackMax <= 0)
			return 0;
		int beforeUseSize = calcUseCellSize(bi.getCount(), stackMax);
		int delCount = bi.del(count);
		cellUsedSize -= beforeUseSize - calcUseCellSize(bi.getCount(), stackMax);
		if (bi.getCount() == 0)
			container.remove(id);
		return delCount;
	}
	
	public void setEquipLock(SBean.DBEquip equip)
	{
		if(equip.id > 0)
			return;
		
		this.del(equip.id, equip.guid);
		equip.id = -equip.id;
		this.putIn(GameData.toGameItem(equip));
	}
	
	public SBean.DBEquip del(int id, String guid)
	{
		CellItem bi = container.get(id);
		if (bi == null || !bi.contains(guid))
			return null;
		int stackMax = getCellItemStackMax(bi.getID());
		if (stackMax <= 0)
			return null;
		int beforeUseSize = calcUseCellSize(bi.getCount(), stackMax);
		SBean.DBEquip equip = bi.del(guid);
		cellUsedSize -= beforeUseSize - calcUseCellSize(bi.getCount(), stackMax);
		if (bi.getCount() == 0)
			container.remove(id);
		return equip;
	}
	
	public SBean.GameItem del(int id, Set<String> guids)
	{
		CellItem bi = container.get(id);
		if (bi == null)
			return null;
		int stackMax = getCellItemStackMax(bi.getID());
		if (stackMax <= 0)
			return null;
		Map<String, SBean.DBEquip> equips = new TreeMap<>();
		int beforeUseSize = calcUseCellSize(bi.getCount(), stackMax);
		for (String guid : guids)
		{
			SBean.DBEquip equip = bi.del(guid);
			if (equip != null)
				equips.put(guid, equip);
		}
		cellUsedSize -= beforeUseSize - calcUseCellSize(bi.getCount(), stackMax);
		if (bi.getCount() == 0)
			container.remove(id);
		return new SBean.GameItem(id, equips.size(), equips);
	}
	
	private static int calcUseCellSize(int count, int stackMax)
	{
		return stackMax > 0 ? (count+stackMax-1)/stackMax : 0;
	}
	
	private static int calcCellUseIncreaseIfAdd(int nowCount, int addCount, int stackMax)
	{
		return stackMax > 0 ? (nowCount+addCount+stackMax-1)/stackMax - (nowCount+stackMax-1)/stackMax : 0;
	}
	
//	private static int calcBagCellUseDecreaseIfDel(int nowCount, int delCount, int stackMax)
//	{
//		return stackMax > 0 ? (nowCount+stackMax-1)/stackMax - (nowCount-delCount+stackMax-1)/stackMax : 0;
//	}
	
	private static int getCellItemStackMax(int id)
	{
		int stackMax = -1;
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
			case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
				{
					stackMax = 0;
				}
				break;
			case GameData.COMMON_ITEM_ID_ITEM_PLANE:
				{
					SBean.ItemCFGS cfg = GameData.getInstance().getItemCFG(id);
					if (cfg != null)
						stackMax = cfg.maxStack;
				}
				break;
			case GameData.COMMON_ITEM_ID_GEM_PLANE:
				{
					SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(id);
					if (cfg != null)
						stackMax = cfg.maxStack;
				}
			case GameData.COMMON_ITEM_ID_BOOK_PLANE:
				{
					SBean.BookCFGS cfg = GameData.getInstance().getBookCFG(id);
					if (cfg != null)
						stackMax = cfg.maxStack;
				}
				break;
			default:
				if (id > GameData.COMMON_ITEM_ID_EQUIP_MIN || id < -GameData.COMMON_ITEM_ID_EQUIP_MIN)
				{
					SBean.EquipCFGS cfg = GameData.getInstance().getEquipCFG(id);
					if (cfg != null)
						stackMax = 1;
				}
				break;
		}
		return stackMax;
	}
	
	public boolean isEmpty()
	{
		return container.isEmpty();
	}
	
	 
	private int cellSize;
	private int expandTimes;
	
	private int cellUsedSize;
	private Map<Integer, CellItem> container = new HashMap<>();
}

