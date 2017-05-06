
package i3k.gs;

import i3k.SBean;

import java.util.Set;
import java.util.Map;
import java.util.TreeMap;

import ket.util.Stream;


public abstract class CellItem
{	
	protected final int id;

	public CellItem(int id)
	{
		this.id = id;
	}
	
	public final int getID()
	{
		return id;
	}
	
	public abstract int getCount();
	public abstract boolean contains(String guid);
	public abstract boolean contains(Set<String> guids);
	public abstract SBean.GameItem toGameItem();
	public abstract int getCellStackMax();
	protected abstract int add(SBean.GameItem gi);
	protected abstract int delImpl(int count);
	protected abstract SBean.DBEquip delImpl(String guid);

	public static CellItem fromGameItem(SBean.GameItem item)
	{
		CellItem bi = null;
		int idPlane = GameData.getVirtualItemIDPlane(item.id);
		switch (idPlane)
		{
			case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
				break;
			case GameData.COMMON_ITEM_ID_ITEM_PLANE:
				{
					SBean.ItemCFGS cfg = GameData.getInstance().getItemCFG(item.id);
					if (cfg != null && item.count > 0)
						bi = new CellMiscellaneous(item.id, item.count);
				}
				break;
			case GameData.COMMON_ITEM_ID_GEM_PLANE:
				{
					SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(item.id);
					if (cfg != null && item.count > 0)
						bi = new CellGem(item.id, item.count);
				}
				break;
			case GameData.COMMON_ITEM_ID_BOOK_PLANE:
				{
					SBean.BookCFGS cfg = GameData.getInstance().getBookCFG(item.id);
					if (cfg != null && item.count > 0)
						bi = new CellBook(item.id, item.count);
				}
				break;
			default:
				{
					SBean.EquipCFGS cfg = GameData.getInstance().getEquipCFG(item.id);
					if (cfg != null && item.count > 0 && !item.equips.isEmpty())
						bi = new CellEquip(item.id, item.equips);
				}
				break;
		}
		return bi;
	}
	
	public int del(int count)
	{
		return delImpl(count);
	}
	
	public SBean.DBEquip del(String guid)
	{
		return delImpl(guid);
	}
	
	private static int calcAdd(int nowCount, int addCount)
	{
		if (addCount <= 0)
			return 0;
		int finalVal = nowCount + addCount;
		if (finalVal > Integer.MAX_VALUE || finalVal <= 0)
			finalVal = Integer.MAX_VALUE;
		return finalVal - nowCount;
	}
	
	private static int calcDel(int nowCount, int delCount)
	{
		if (delCount <= 0)
			return 0;
		int finalVal = nowCount - delCount;
		if ( finalVal < 0)
			finalVal = 0;
		return nowCount - finalVal;
	}

	
	public boolean canTrade()
	{
		return false;
	}
	
	public Salable getSalable()
	{
		return null;
	}
	
	public Useable getUseable()
	{
		return null;
	}
	
	public Composable getComposable()
	{
		return null;
	}
	
	public Inlaidable getInlaidable()
	{
		return null;
	}
	
	public Wearable getWearable()
	{
		return null;
	}
//虚拟道具的行为接口
//////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public interface Salable
	{
		int getSalePriceUnitID();
		int getSalePrice();
	}
	
	public interface DiamondAdder
	{
		int getAmount();
		boolean isFree();
	}
	
	public interface CoinAdder
	{
		int getAmount();
		boolean isFree();
	}
	
	public interface ExpAdder
	{
		int getExp();
	}

	public interface VitAdder
	{
		int getVit();
	}

	public interface GiftBox
	{
		int getGiftID();
		int getCostItemID();
		int getCostItemCount();
	}
//	public interface SkillScroll
//	{
//		int getSkillType();
//	}
	
	public interface RecipeReel
	{
		int getRecipeID();
	}
	
	public interface HpPotion
	{
		int getHpAddValue();
	}
	
	public interface HPPool
	{
		int getHPPoolAddValue();
	}
	
	public interface TreasureChest
	{
		Map<Integer, Integer> getRandomDrop();
		int getCostItemID();
		int getCostItemCount();
	}
	
	public interface WeaponExpAdder
	{
		int getExp();
	}
	
	public interface PetExpAdder
	{
		int getExp();
	}
	
	public interface EquipEnergyAdder
	{
		int getEnergy();
	}
	
	public interface GemEnergyAdder
	{
		int getEnergy();
	}
	
	public interface SpiritInspirationAdder
	{
		int getInspiration();
	}
	
	public interface FashionAdder
	{
		int getFashion();
	}
	
	public interface FlowerAdder
	{
		int getFlower();
	}
	
	public interface ExpCoinPool
	{
		int getVolume();
	}

	public interface VipCard
	{
		int getVipLvl();
		int getTime();
	}
	
	public interface TowerFameItem
	{
		int getUpFame();
	}
	
	public interface SpecialCard
	{
		int getTime();
		int getDiamonds();
		int getType();
	}
	
	public interface FeatAdder
	{
		int getFeat();
	}

	public interface SkillItem
	{
		int getSkillId();
		int getSkillLv();
	}
	
	public interface Letter
	{
		int getTaskId();
		int getNeedDel();
	}
	
	public interface EvilValue
	{
		int getReduceValue();
	}
	
	public interface ArmorExpItem
	{
		int getExp();
	}
	
	public interface Rune
	{
		int getRuneId();
	}

	public interface PropStrengthItem
	{
		int getPropId();
		int getPropNum();
	}
	
	public interface EquipRefineItem
	{
		int getRefineGroupID();
		int getPropCount();
	}
	
	public interface FireWork
	{
		boolean needBroadCast();
	}
	
	public interface OfflineFuncPoint
	{
		int getFuncPoint();
	}
	
	public interface TitleItem
	{
		int getTitleId();
	}
	
	public interface USkillItem
	{
		int getUSkillId();
	}
	
	public interface HeadItem
	{
		int getHeadId();
	}
	
	public interface FusionableItem
	{
	    int getFusionPoint();
	}
	
	public interface Useable
	{
		boolean canUse(int vipLvl, int level, int count);
		boolean testExceedLimit(int count);
		boolean logRoleUseTimes();
		int getMaxCanUseTimes(int vipLvl);
		boolean canGift();
		DiamondAdder useAsDiamondAdder();
		CoinAdder useAsCoinAdder();
		ExpAdder useAsExpAdder();
		VitAdder useAsVitAdder();
		GiftBox useAsGiftBox();
		RecipeReel useAsRecipeReel();
		HpPotion useAsPotion();
		
		HPPool useAsHPPool();
		TreasureChest useAsTreasureChest();
		WeaponExpAdder useAsWeaponExpAdder();
		PetExpAdder useAsPetExpAdder();
		
		EquipEnergyAdder useAsEquipEnergyAdder();
		GemEnergyAdder useAsGemEnergyAdder();
		SpiritInspirationAdder useAsSpiritInspirationAdder();
		
		FashionAdder useAsFashionAdder();
		FlowerAdder useAsFlowerAdder();
		ExpCoinPool useAsExpCoinPool();
		SpecialCard useAsSpecialCard();
		VipCard useAsVipCard();
		TowerFameItem useAsTowerFameItem();
		FeatAdder useAsFeatAdder();
		SkillItem useAsSkillItem();
		Letter useAsLetter();
		EvilValue useAsEvilValue();
		ArmorExpItem useAsArmorExpItem();
		Rune useAsRune();
		PropStrengthItem useAsPropStrengthItem();
		EquipRefineItem useAsEquipRefineItem();
		FireWork useAsFireWork();
		OfflineFuncPoint useAsOfflineFuncPoint();
		TitleItem useAsTitleItem();
		USkillItem useAsUSkillItem();
		HeadItem useAsHeadItem();
		FusionableItem useAsFusionableItem();
	}
	
	public interface Composable
	{
		int getComposeItemID();
		int getComposeReqCount();
	}
	
	public interface Inlaidable
	{
		int getInlaidType();
	}
	
	public interface Wearable
	{
		boolean canWear(int level, int tlvl, int bwType, int classType);
		int getLvlReq();
		SBean.DBEquip getEquipData(String guid);
		int getWearPartID();
		int getRank();
		SBean.DummyGoods getRefineFixCost();
		boolean isRefineCostItem(int itemID);
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static abstract class CellStackItem extends CellItem
	{
		private int count;
		public CellStackItem(int id, int count)
		{
			super(id);
			this.count = calcAdd(0, count);
		}
		
		public int getCount()
		{
			return this.count;
		}
		
		public boolean contains(String guid)
		{
			return false;
		}
		
		public boolean contains(Set<String> guids)
		{
			return false;
		}
		
		public SBean.GameItem toGameItem()
		{
			return new SBean.GameItem(this.id, this.count, GameData.emptyMap());
		}
		
		public int add(SBean.GameItem gi)
		{
			if (gi.id != this.id)
				return 0;
			int addCount = calcAdd(this.count, gi.count);
			this.count += addCount;
			return addCount;
		}
		
		protected int delImpl(int count)
		{
			int delCount = calcDel(this.count, count);
			this.count -= delCount;
			return delCount;
		}
		
		protected SBean.DBEquip delImpl(String guid)
		{
			return null;
		}
	}
	
	private static class CellMiscellaneous extends CellStackItem
	{
		public CellMiscellaneous(int id, int count)
		{
			super(id, count);
		}
		
		public int getCellStackMax()
		{
			SBean.ItemCFGS cfg = GameData.getInstance().getItemCFG(id);
			return cfg == null ? 1 : cfg.maxStack;
		}
		
		public boolean canTrade()
		{
			SBean.ItemCFGS cfg = GameData.getInstance().getItemCFG(id);
			return id < 0 && cfg != null && cfg.canTrade != 0;
		}
		
		public Salable getSalable()
		{
			SBean.ItemCFGS cfg = GameData.getInstance().getItemCFG(id);
			return cfg == null ? null : cfg.saleGold <= 0 ? null : new Salable()//卖出价小于等于0表示不可卖出
			{
				public int getSalePriceUnitID()
				{
					return GameData.COMMON_ITEM_ID_COIN;
				}
				
				public int getSalePrice()
				{
					return cfg.saleGold;
				}
			};
		}
		
		public Useable getUseable()
		{
			SBean.ItemCFGS cfg = GameData.getInstance().getItemCFG(id);
			return cfg == null ? null : new Useable()
			{
				public boolean canUse(int vipLvl, int level, int count)
				{
					return cfg.viplvlReq <= vipLvl && cfg.lvlReq <= level && CellMiscellaneous.this.getCount() >= count;
				}

				public boolean testExceedLimit(int count)
				{
					return cfg.roleCanUseTimes < 0 || cfg.roleCanUseTimes >= count;
				}
				
				public boolean logRoleUseTimes()
				{
					return cfg.roleCanUseTimes >= 0;
				}
				
				public int getMaxCanUseTimes(int vipLvl)
				{
					return cfg.dayUseTimes.isEmpty() ? Integer.MAX_VALUE : cfg.dayUseTimes.get(vipLvl);
				}
			
				public boolean canGift()
				{
					return cfg.canGift > 0;
				}
				
				public DiamondAdder useAsDiamondAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_DIAMOND)
						return null;
					return new DiamondAdder()
					{
						public int getAmount()
						{
							return cfg.arg1;
						}
						
						public boolean isFree()
						{
							return cfg.arg2 != 0;
						}
					};
				}
				
				public CoinAdder useAsCoinAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_COIN)
						return null;
					return new CoinAdder()
					{
						public int getAmount()
						{
							return cfg.arg1;
						}
						
						public boolean isFree()
						{
							return cfg.arg2 != 0;
						}
					};
				}
				
				public ExpAdder useAsExpAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_EXP)
						return null;
					return () -> cfg.arg1;
				}

				public VitAdder useAsVitAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_VIT)
						return null;
					return () -> cfg.arg1;
				}

				public GiftBox useAsGiftBox()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_GIFT)
						return null;
					
					return new GiftBox()
					{
						public int getGiftID()
						{
							return cfg.arg1;
						}

						public int getCostItemID()
						{
							return cfg.arg2;
						}

						@Override
						public int getCostItemCount()
						{
							return cfg.arg3;
						}
					};
				}
				
				public RecipeReel useAsRecipeReel()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_RECIPEREEL)
						return null;
					return () -> cfg.arg1;
				}
				
				public HpPotion useAsPotion()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_HP)
						return null;
					return () -> cfg.arg1;
				}
				
				public HPPool useAsHPPool()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_HPPOOL)
						return null;
					return () -> cfg.arg1;
				}
				
				public TreasureChest useAsTreasureChest()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_CHEST)
						return null;
					
					return new TreasureChest()
					{
						public Map<Integer, Integer> getRandomDrop()
						{
							return GameData.getInstance().getMergedRandomDrop(cfg.arg1, 1, 1);
						}

						public int getCostItemID()
						{
							return cfg.arg2;
						}

						public int getCostItemCount()
						{
							return cfg.arg3;
						}
						
					};
				}
				
				public WeaponExpAdder useAsWeaponExpAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_WEAPON_EXP)
						return null;
					return () -> cfg.arg1;
				}
				
				public PetExpAdder useAsPetExpAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_PET_EXP)
						return null;
					return () -> cfg.arg1;
				}
				public EquipEnergyAdder useAsEquipEnergyAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_EQUIP_ENERGY)
						return null;
					return () -> cfg.arg1;
				}
				
				public GemEnergyAdder useAsGemEnergyAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_GEM_ENERGY)
						return null;
					return () -> cfg.arg1;
				}
				
				public SpiritInspirationAdder useAsSpiritInspirationAdder()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_SPIRIT_INSPIRATION)
						return null;
					return () -> cfg.arg1;
				}
				
				public FashionAdder useAsFashionAdder()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_FASHION)
						return null;
					
					return () -> cfg.arg1;
				}
				
				public FlowerAdder useAsFlowerAdder()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_FLOWER)
						return null;
					
					return () -> cfg.arg1;
				}
				
				public ExpCoinPool useAsExpCoinPool()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_EXPCOIN_POOL)
						return null;
					
					return () -> cfg.arg1;
				}

				public VipCard useAsVipCard()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_VIP_CARD)
						return null;
					
					return new VipCard()
					{
						@Override
						public int getVipLvl()
						{
							return cfg.arg2;
						}
						
						@Override
						public int getTime()
						{
							return cfg.arg1;
						}
					};
				}
				
				public TowerFameItem useAsTowerFameItem()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_TOWER_FAME)
						return null;
					
					return new TowerFameItem()
					{
						@Override
						public int getUpFame()
						{
							return cfg.arg1;
						}
					};
				}
				
				public SpecialCard useAsSpecialCard()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_SPECIAL_CARD)
						return null;
					
					return new SpecialCard()
					{
						@Override
						public int getTime()
						{
							return cfg.arg1;
						}
						
						@Override
						public int getDiamonds()
						{
							return cfg.arg2;
						}

						@Override
						public int getType()
						{
							return cfg.arg3;
						}
					};
				}
				
				public FeatAdder useAsFeatAdder()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_FEAT)
						return null;
					
					return () -> cfg.arg1;
				}

				@Override
				public SkillItem useAsSkillItem()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_SKILL)
						return null;
					
					return new SkillItem()
					{
						
						@Override
						public int getSkillLv()
						{
							return cfg.arg2;
						}
						
						@Override
						public int getSkillId()
						{
							return cfg.arg1;
						}
					};
				}

				@Override
				public Letter useAsLetter()
				{
					return new Letter()
					{

						@Override
						public int getTaskId()
						{
							return cfg.arg2;
						}

						@Override
						public int getNeedDel()
						{
							return cfg.arg4;
						}

					};
				}
				
				@Override
				public EvilValue useAsEvilValue()
				{
					return new EvilValue()
					{
						@Override
						public int getReduceValue()
						{
							return cfg.arg1;
						}
					};
				}

				@Override
				public ArmorExpItem useAsArmorExpItem()
				{
					return new ArmorExpItem()
					{
						@Override
						public int getExp()
						{
							return cfg.arg1;
						}
					};
				}

				@Override
				public Rune useAsRune()
				{
					return new Rune()
					{
						@Override
						public int getRuneId()
						{
							return cfg.arg1;
						}
					};
				}

				@Override
				public PropStrengthItem useAsPropStrengthItem()
				{
					return new PropStrengthItem()
					{
						@Override
						public int getPropId()
						{
							return cfg.arg1;
						}

						@Override
						public int getPropNum()
						{
							return cfg.arg2;
						}
					};
				}

				@Override
				public EquipRefineItem useAsEquipRefineItem()
				{
					if (cfg.type != GameData.GAME_ITEM_TYPE_ENCHANT)
						return null;
					
					return new EquipRefineItem()
					{
						@Override
						public int getRefineGroupID()
						{
							return cfg.arg1;
						}

						@Override
						public int getPropCount()
						{
							return cfg.arg2;
						}
						
					};
				}

				@Override
				public FireWork useAsFireWork()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_FIREWORK)
						return null;
					
					return new FireWork()
					{
						@Override
						public boolean needBroadCast()
						{
							return cfg.arg2 == GameData.FIREWORK_ALL_MAP;
						}
						
					};
				}

				@Override
				public OfflineFuncPoint useAsOfflineFuncPoint()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_OFFLINE_FUNC_POINT)
						return null;

					return new OfflineFuncPoint()
					{
						@Override
						public int getFuncPoint()
						{
							return cfg.arg1;
						}
						
					};
				}

				@Override
				public TitleItem useAsTitleItem()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_TITLE_ITEM)
						return null;

					return new TitleItem()
					{
						
						@Override
						public int getTitleId()
						{
							return cfg.arg1;
						}
					};
				}

				@Override
				public USkillItem useAsUSkillItem()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_USKILL_ITEM)
						return null;

					return new USkillItem()
					{
						@Override
						public int getUSkillId()
						{
							return cfg.arg1;
						}
					};
				}
				
				@Override
				public HeadItem useAsHeadItem()
				{
					if(cfg.type != GameData.GAME_ITEM_TYPE_HEAD_ITEM)
						return null;
					
					return new HeadItem()
					{
						@Override
						public int getHeadId()
						{
							return cfg.arg1;
						}
					};
				}
				
				@Override
				public FusionableItem useAsFusionableItem()
				{
				    if (cfg.canFusion == 0)
				        return null;
				    
				    return new FusionableItem()
				    {
				        @Override
				        public int getFusionPoint()
				        {
				            return cfg.fusionPoint;
				        }
				    };
				}
				
			};
		}
		
		public Composable getComposable()
		{
			SBean.ItemCFGS cfg = GameData.getInstance().getItemCFG(id);
			if (cfg == null || cfg.composeId == 0)
				return null;
			return new Composable()
			{
				public int getComposeItemID()
				{
					return cfg.composeId;
				}
				public int getComposeReqCount()
				{
					return cfg.composeCntReq;
				}
			};
		}
	}
	
	private static class CellGem extends CellStackItem
	{
		public CellGem(int id, int count)
		{
			super(id,  count);
		}
		
		public int getCellStackMax()
		{
			SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(id);
			return cfg == null ? 1 : cfg.maxStack;
		}
		
		public boolean canTrade()
		{
			SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(id);
			return id < 0 && cfg != null && cfg.canTrade != 0;
		}
		
		public Salable getSalable()
		{
			SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(id);
			return cfg == null ? null : cfg.saleEnergy <= 0 ? null : new Salable()//卖出价小于等于0表示不可卖出
			{
				public int getSalePriceUnitID()
				{
					return GameData.COMMON_ITEM_ID_GEM_ENERGY;
				}
				
				public int getSalePrice()
				{
					return cfg.saleEnergy;
				}
			};
		}
		
		public Inlaidable getInlaidable()
		{
			SBean.GemCFGS cfg = GameData.getInstance().getGemCFG(id);
			return cfg == null ? null : (Inlaidable) () -> cfg.type;
		}
	}
	
	
	private static class CellBook extends CellStackItem
	{
		public CellBook(int id, int count)
		{
			super(id,  count);
		}
		
		public int getCellStackMax()
		{
			SBean.BookCFGS cfg = GameData.getInstance().getBookCFG(id);
			return cfg == null ? 1 : cfg.maxStack;
		}
		
		public boolean canTrade()
		{
			SBean.BookCFGS cfg = GameData.getInstance().getBookCFG(id);
			return id <0 && cfg != null && cfg.canTrade != 0;
		}
		
		public Salable getSalable()
		{
			SBean.BookCFGS cfg = GameData.getInstance().getBookCFG(id);
			return cfg == null ? null : cfg.saleSpirit <= 0 ? null : new Salable()//卖出价小于等于0表示不可卖出
			{
				public int getSalePriceUnitID()
				{
					return GameData.COMMON_ITEM_ID_BOOK_INSPIRATION;
				}
				
				public int getSalePrice()
				{
					return cfg.saleSpirit;
				}
			};
		}

	}
	
	private static class CellEquip extends CellItem
	{
		private Map<String, SBean.DBEquip> equips = new TreeMap<>();
		public CellEquip(SBean.DBEquip equip)
		{
			super(equip.id);
			this.equips.put(equip.guid, equip);
		}
		public CellEquip(int id, Map<String, SBean.DBEquip> equips)
		{
			super(id);
			this.equips.putAll(equips);
		}

		
		public int getCount()
		{
			return this.equips.size();
		}
		
		public int getCellStackMax()
		{
			return 1;
		}
		
		public boolean contains(String guid)
		{
			return equips.containsKey(guid);
		}
		
		public boolean contains(Set<String> guids)
		{
			for (String guid :  guids)
			{
				if (!equips.containsKey(guid))
					return false;
			}
			return true;
		}
		
		public SBean.GameItem toGameItem()
		{
			return new SBean.GameItem(this.id, this.equips.size(), Stream.clone(this.equips));
		}
		
		public int add(SBean.GameItem gi)
		{
			if (gi.id != this.id)
				return 0;
			this.equips.putAll(gi.equips);
			return gi.equips.size();
		}
		
		protected int delImpl(int count)
		{
			return 0;
		}
		
		protected SBean.DBEquip delImpl(String guid)
		{
			return equips.remove(guid);
		}

		
		public boolean canTrade()
		{
			SBean.EquipCFGS cfg = GameData.getInstance().getEquipCFG(id);
			return id <0 && cfg != null && cfg.canTrade != 0;
		}
		
		public Salable getSalable()
		{
			SBean.EquipCFGS cfg = GameData.getInstance().getEquipCFG(id);
			return cfg == null ? null : cfg.saleEnergy <= 0 ? null : new Salable()//卖出价小于等于0表示不可卖出
			{
				public int getSalePriceUnitID()
				{
					return GameData.COMMON_ITEM_ID_EQUIP_ENERGY;
				}
				
				public int getSalePrice()
				{
					return cfg.saleEnergy;
				}
			};
		}
		
		public Wearable getWearable()
		{
			SBean.EquipCFGS cfg = GameData.getInstance().getEquipCFG(id);
			return cfg == null ? null : new Wearable()
			{
				public boolean canWear(int level, int tlvl, int bwType, int classType)
				{
					return cfg.lvlReq <= level && cfg.tlvlReq <= tlvl && (cfg.bwTypeReq == 0 || cfg.bwTypeReq == bwType ) && (cfg.classType == 0 || cfg.classType == classType);
				}
				
				public int getLvlReq()
				{
					return cfg.lvlReq;
				}
				
				public SBean.DBEquip getEquipData(String guid)
				{
					return equips.get(guid);
				}
				
				public int getWearPartID()
				{
					return cfg.type;
				}
				
				public SBean.DummyGoods getRefineFixCost()
				{
					return cfg.refineFixCost;
				}
				
				public boolean isRefineCostItem(int itemID)
				{
					return cfg.refineCosts.contains(itemID);
				}
				
				public int getRank()
				{
					return cfg.rank;
				}
			};
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}


