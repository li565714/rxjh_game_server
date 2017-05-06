
package i3k.gs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import i3k.SBean;
import i3k.SBean.RankCFGS;
import i3k.SBean.RankSect;
import i3k.util.FileWatchdogHandler;
import i3k.util.GameRandom;
import i3k.util.GameTime;
import i3k.util.ResourceWatcher;

public class RankData
{
	public static class RankDataBase<T>
	{
		final static int RANK_RAND_INTERVAL_MAX = 60;
		final static int MAX_RANK_PAGE_LENGTH = 20;
		final static RankRoleReader RankRoleReader = new RankRoleReader();
		final static RankSectReader RankSectReader = new RankSectReader();
		int randTick;
		public int id;
		public Ranks<T> ranks;
		public RankSnapshot<T> ranksSnapshot;
		public int lastRewardTime;
		
		public SBean.RankCFGS cfg;
		RankItemReader<T> reader;
		RankSnapshotChangeHandler<T> snapshotChangeHandler;
		
		boolean blackListOn = false;
		Set<Integer> idBlackList = new HashSet<>();
		
		public final static Role.RpcRes SNAPSHOTINCONSISTENTERROR = new Role.RpcRes<List<?>>(-1);
		public final static Role.RpcRes UNKNOWNERROR = new Role.RpcRes<List<?>>(0);
		public final static Role.RpcRes EMPTYRESULT = new Role.RpcRes<List<?>>(GameData.emptyList());
		public RankDataBase(int id, SBean.RankCFGS cfg, RankItemReader<T> reader)
		{
			this.randTick = GameRandom.getRandInt(0, RANK_RAND_INTERVAL_MAX);
			this.id = id;
			this.cfg = cfg;
			ranks = new Ranks<T>(reader);
			ranksSnapshot = new RankSnapshot<T>(reader);
			this.reader = reader;
		}
		
		public int getId()
		{
			return this.id;
		}
		
		public RankSnapshot<T> getSnapshot()
		{
			return this.ranksSnapshot;
		}
		
		public void setRankSnapshotChangeHandler(RankSnapshotChangeHandler<T> snapshotChangeHandler)
		{
			this.snapshotChangeHandler = snapshotChangeHandler;
		}
		
		private int getRewardTimeUnit(int timeTick)
		{
			return cfg.rankType == 1 ? GameData.getDayByRefreshTimeOffset(timeTick) : GameData.getWeekByRefreshTimeOffset(timeTick);
		}
		
		public boolean isOpenOnLevel(int level)
		{
			return level >= cfg.lvlReq;
		}
		
		public synchronized void doRefresh(List<T> snapshot, int snapshotCreateTime)
		{
			this.ranksSnapshot.fromDB(snapshot, snapshotCreateTime);
		}
		
		public void onTimer(int timeTick)
		{
			if (timeTick % RANK_RAND_INTERVAL_MAX == this.randTick)
			{
				if (isCheckRefreshOnTimer())
					tryRefresh(timeTick);
				tryAcceptSnapshot(timeTick);
			}
		}
		
		//默认是本服数据排行榜
		public boolean isCheckRefreshOnTimer()
		{
			//TODO 根据rank类型返回是否需要在timer中检查刷新
			return true;
		}
		
		public synchronized void tryRefresh(int timeTick)
		{
			int lastRefreshTime = GameData.getLastRefreshTime(timeTick, cfg.refreshTime);
			if (lastRefreshTime > this.ranksSnapshot.getCreateTime())
			{
//				System.out.println(GameTime.getDateTimeStampStr(timeTick) + " " + this + " (size = " + ranks.rankItems.size() + " , minValue " + ranks.minValue + ") refresh !");
				this.ranksSnapshot = this.ranks.createSnapshot();
				doSync();
				if (snapshotChangeHandler != null)
					snapshotChangeHandler.onRankSnapshotChanged(timeTick, this.ranksSnapshot);
			}
		}
		
		
		public synchronized void tryAcceptSnapshot(int timeTick)
		{
			int lastReward = getRewardTimeUnit(this.lastRewardTime);
			int curReward = getRewardTimeUnit(timeTick);
			if (curReward > lastReward)
			{
				int lastRefreshTime = this.ranksSnapshot.getCreateTime();
				if (this.lastRewardTime < lastRefreshTime && lastRefreshTime <= timeTick)
				{
					doClear();
					doReward();
					this.lastRewardTime = timeTick;	
				}
			}
		}

		protected void doClear()
		{
			if (cfg.rankType == 2)//只有类型2才清档
				this.ranks.clearRank();
		}
		
		protected void doReward()
		{
			
		}
		
		protected void doSync()
		{
			
		}
		
		public synchronized void tryUpdateRank(int rankClearTime, T rankItem)
		{
			if (cfg.rankType != 2 || rankClearTime == this.lastRewardTime)
				this.updateRankImpl(rankItem);
		}
		
		public synchronized void tryUpdateRank(int rankClearTime, T newRankItem, T addRankItem)
		{
			if (cfg.rankType != 2 || rankClearTime == this.lastRewardTime)
				this.updateRankImpl(newRankItem);
			else
				this.updateRankImpl(addRankItem);
		}
		
		private void updateRankImpl(T rankItem)
		{
			if (reader.canUpdateRankItem(rankItem, this.cfg, this.blackListOn, this.idBlackList))
				this.ranks.tryUpdateRank(rankItem, cfg.length);
		}
		
		public synchronized Role.RpcRes<List<T>> getRanksSnapshot(int createTime, int index, int len) 
		{
			if (createTime != this.ranksSnapshot.getCreateTime())
				return SNAPSHOTINCONSISTENTERROR;
			if (index < 0 || len <= 0 || len > MAX_RANK_PAGE_LENGTH)
				return UNKNOWNERROR;
			if (index >= this.ranksSnapshot.getSnapshot().size())
				return EMPTYRESULT;
			int toIndex = index + len;
			if (toIndex > this.ranksSnapshot.getSnapshot().size())
				toIndex = this.ranksSnapshot.getSnapshot().size();
			return new Role.RpcRes<List<T>>(this.ranksSnapshot.getSnapshot().subList(index, toIndex));
		}
		
		public synchronized int getItemSnapshotRank(int itemID)
		{
			return this.ranksSnapshot.getItemRank(itemID);
		}
		
		public synchronized T getItemRank(int itemID)
		{
			return ranks.rankItems.get(itemID);
		}
		
		public synchronized SBean.RankBrief getRankBrief()
		{
			return new SBean.RankBrief(this.id, this.ranksSnapshot.getCreateTime(), this.ranksSnapshot.getSnapshot().size());
		}
		
		public synchronized int getRankRewardTime()
		{
			return this.lastRewardTime;
		}
		
		public synchronized void resetRankBlcakList(boolean blackListOn, Set<Integer> lst)
		{
			this.blackListOn = blackListOn;
			this.idBlackList = lst;
			if (blackListOn)
				ranks.removeRankInBlackList(lst);
		}
	}
	
	public static class RankSnapshot<T>
	{
		public List<T> ranksSnapshot = new ArrayList<>();
		private Map<Integer, Integer> item2rank = new HashMap<>();
		private int snapshotCreateTime;
		private RankItemReader<T> reader;
		public RankSnapshot(RankItemReader<T> reader)
		{
			this.reader = reader;
		}
		
		void fromDB(List<T> dbRanksSnapshot, int dbSnapshotCreateTime)
		{
			this.ranksSnapshot = dbRanksSnapshot;
			this.snapshotCreateTime = dbSnapshotCreateTime;
			
			item2rank.clear();
			for(int i=0; i<this.ranksSnapshot.size(); i++)
			{
				T item = this.ranksSnapshot.get(i);
				item2rank.put(reader.getRankItemId(item), i + 1);
			}
		}
		
		void addRankRole(T rankItem, int rank)
		{
			this.ranksSnapshot.add(rankItem);
			this.item2rank.put(reader.getRankItemId(rankItem), rank);
		}
		
		public List<T> getSnapshot()
		{
			return ranksSnapshot;
		}
		
		public int getCreateTime()
		{
			return snapshotCreateTime;
		}
		
		void setCreateTime(int timeNow)
		{
			this.snapshotCreateTime = timeNow;
		}
		
		int getItemRank(int itemID)
		{
			return this.item2rank.getOrDefault(itemID, 0);
		}
		
		Integer getRankKey(int rank)
		{
			rank = this.ranksSnapshot.size() < rank ? this.ranksSnapshot.size() : rank;
			return this.ranksSnapshot.isEmpty() ? null : reader.getRankItemKey(this.ranksSnapshot.get(rank - 1));
		}
	}
	
	public interface RankItemReader<T>
	{
		int getRankItemId(T rankItem);
		int getRankItemKey(T rankItem);
		
		boolean canUpdateRankItem(T rankItem, SBean.RankCFGS cfg, boolean blackListOn, Set<Integer> idBlackList);
	}
	
	public static class RankSectReader implements RankItemReader<SBean.RankSect>
	{

		@Override
		public int getRankItemId(RankSect rankItem)
		{
			return rankItem.sect.sectId;
		}

		@Override
		public int getRankItemKey(RankSect rankItem)
		{
			return rankItem.rankKey;
		}

		@Override
		public boolean canUpdateRankItem(RankSect rankItem, RankCFGS cfg, boolean blackListOn, Set<Integer> idBlackList)
		{
			return true;
		}
		
	}
	
	public static class RankRoleReader implements RankItemReader<SBean.RankRole>
	{
		public int getRankItemId(SBean.RankRole rankItem)
		{
			return rankItem.role.id;
		}
		
		public int getRankItemKey(SBean.RankRole rankItem)
		{
			return rankItem.rankKey;
		}
		
		public boolean canUpdateRankItem(SBean.RankRole rankItem, SBean.RankCFGS cfg, boolean blackListOn, Set<Integer> idBlackList)
		{
			return rankItem.role.level >= cfg.lvlReq && (!blackListOn || !idBlackList.contains(rankItem.role.id));
		}
	}
	
	public static class Ranks<T>
	{
		public TreeSet<Long> rankValues = new TreeSet<>();
		public Map<Integer, T> rankItems = new HashMap<>();
		int minValue;
		RankItemReader<T> reader;
		public Ranks(RankItemReader<T> reader)
		{
			this.reader = reader;
		}
		
		List<T> toDB()
		{
			return new ArrayList<T>(rankItems.values());
		}
		
		void fromDB(List<T> dbranks)
		{
			int minValue = Integer.MAX_VALUE;
			for (T e : dbranks)
			{
				int id = reader.getRankItemId(e);
				int key = reader.getRankItemKey(e);
				rankItems.put(id, e);
				rankValues.add(getRankItemValue(key, id));
				if (key < minValue)
					minValue = key;
			}
			this.minValue = minValue >= Integer.MAX_VALUE ? 0 : minValue;
		}
		
		void clearRank()
		{
			this.rankValues.clear();
			this.rankItems.clear();
			this.minValue = 0;
		}
		
		
		void removeRankInBlackList(Set<Integer> lst)
		{
			for (int id : lst)
			{
				this.removeItem(id);
			}
			this.updateMinValue();
		}
		
		public static long getRankItemValue(int rank, int rid)
		{
			return ((long)rank << 32) | ((long)rid & 0xffffffffL);
		}
		
		public static int getRank(long value)
		{
			return (int)((value >> 32) & 0xffffffffL);
		}
		
		public static int getItemId(long value)
		{
			return (int)(value & 0xffffffffL);
		}
		
		public void tryUpdateRankNoLength(T rankItem)
		{
			this.removeItem(reader.getRankItemId(rankItem));
			this.addNewItem(rankItem);
		}
		
		public void tryUpdateRank(T rankItem, int maxLength)
		{
			if (this.removeItem(reader.getRankItemId(rankItem)) != null || reader.getRankItemKey(rankItem) > minValue || rankItems.size() < maxLength)
			{
				this.addNewItem(rankItem);
				this.tryRemoveRankLast(maxLength);
				this.updateMinValue();
			}
		}
		
		private T removeItem(int id)
		{
			T oldRankItem = rankItems.remove(id);
			if (oldRankItem != null)
			{
				long oldRankValue = getRankItemValue(reader.getRankItemKey(oldRankItem), reader.getRankItemId(oldRankItem));
				rankValues.remove(oldRankValue);
			}
			return oldRankItem;
		}
		
		private void addNewItem(T rankItem)
		{
			int id = reader.getRankItemId(rankItem);
			if (rankItems.putIfAbsent(id, rankItem) == null)
			{
				long rankItemValue = getRankItemValue(reader.getRankItemKey(rankItem), id);
				rankValues.add(rankItemValue);	
			}
		}
		
		private void tryRemoveRankLast(int maxLength)
		{
			if (rankItems.size() > maxLength)
			{
				Long lastRankValue = rankValues.pollFirst();
				if (lastRankValue != null)
				{
					int lastRankItemId = getItemId(lastRankValue);
					rankItems.remove(lastRankItemId);
				}
			}
		}
		
		private void updateMinValue()
		{
			if (!rankValues.isEmpty())
			{
				Long minRankValue = rankValues.first();
				if (minRankValue != null)
				{
					minValue = getRank(minRankValue);
				}	
			}
			else
			{
				minValue = 0;
			}
		}
		
		public int getRankSize()
		{
			return rankItems.size();
		}
		
		public int getMinValue()
		{
			return minValue;
		}
		
		RankSnapshot<T> createSnapshot()
		{
			RankSnapshot<T> rankSnapShot =  new RankSnapshot<T>(this.reader);
			int rank = 1;
			for(Long k: rankValues.descendingSet())
			{
				T rr = rankItems.get(getItemId(k));
				if(rr != null)
				{
					rankSnapShot.addRankRole(rr, rank);
					rank++;
				}
			}
			rankSnapShot.setCreateTime(GameTime.getTime());
			return rankSnapShot;
		}
	}
	
	public interface RankSnapshotChangeHandler<T>
	{
		void onRankSnapshotChanged(int timeTick, RankSnapshot<T> snapshot);
	}
	
	public static class RoleRankDataBase extends RankDataBase<SBean.RankRole>
	{
		public RoleRankDataBase(int id)
		{
			super(id, GameData.getInstance().getRoleRankCFG(id).rank, RankRoleReader);
		}
		
		public void fromDB(SBean.DBRoleRanks dbranks)
		{
			this.ranks.fromDB(dbranks.ranks);
			this.lastRewardTime = dbranks.lastRewardTime;
			this.ranksSnapshot.fromDB(dbranks.snapshot, dbranks.snapshotCreateTime);
		}
		
		public synchronized SBean.DBRoleRanks toDB()
		{
			return new SBean.DBRoleRanks(id, ranks.toDB(), new ArrayList<>(ranksSnapshot.getSnapshot()), ranksSnapshot.getCreateTime(), this.lastRewardTime);
		}
		
		public boolean isCheckRefreshOnTimer()
		{
			switch (cfg.id)
			{
			case GameData.RANK_TYPE_ROLE_LEVEL:
			case GameData.RANK_TYPE_ROLE_POWER:
			case GameData.RANK_TYPE_PETS_POWER:
			case GameData.RANK_TYPE_WEAPONS_POWER:
			case GameData.RANK_TYPE_CHARM_FEMALE:
			case GameData.RANK_TYPE_CHARM_MALE:
			case GameData.RANK_TYPE_ROLE_ACHIEVE:
			case GameData.RANK_TYPE_ROLE_LEVEL_BALDE:
			case GameData.RANK_TYPE_ROLE_LEVEL_SWORD:
			case GameData.RANK_TYPE_ROLE_LEVEL_SPEAR:
			case GameData.RANK_TYPE_ROLE_LEVEL_ARROW:
			case GameData.RANK_TYPE_ROLE_LEVEL_HEAL:
			case GameData.RANK_TYPE_ROLE_POWER_BALDE:
			case GameData.RANK_TYPE_ROLE_POWER_SWORD:
			case GameData.RANK_TYPE_ROLE_POWER_SPEAR:
			case GameData.RANK_TYPE_ROLE_POWER_ARROW:
			case GameData.RANK_TYPE_ROLE_POWER_HEAL:
			case GameData.RANK_TYPE_ROLE_MASTER_REPUTATION:
				return true;
			default:
				break;
			}
			return false;
		}
		
		public String toString()
		{
			return "role rank " + this.id; 
		}
	}
	interface RoleRewarder
	{
		void doReward(int id, int roleId, SBean.RankTitle rtCfg);
	}
	public static class RoleRankData extends RoleRankDataBase
	{
		RoleRewarder roleRewarder;
		public RoleRankData(int id, RoleRewarder roleRewarder)
		{
			super(id);
			this.roleRewarder = roleRewarder;
		}
		
		protected void doReward()
		{
			if (this.roleRewarder != null)
			{
				for(int i=0; i<this.ranksSnapshot.getSnapshot().size(); i++)
				{
					if(i + 1 > cfg.titleLastRank)
						return;
					
					SBean.RankTitle rtCfg = GameData.getRankTitle(cfg.gainTitles, i + 1);
					if(rtCfg == null || rtCfg.title == 0)
						return;
					
					SBean.RankRole e = this.ranksSnapshot.getSnapshot().get(i);
					this.roleRewarder.doReward(RoleRankData.this.id, e.role.id, rtCfg);
				}				
			}
		}
	}
	
	public static class SectRankDataBase extends RankDataBase<SBean.RankSect>
	{

		public SectRankDataBase(int id)
		{
			super(id, GameData.getInstance().getSectRankCFG(id).rank, RankSectReader);
		}
		
		public void fromDB(SBean.DBSectRanks dbranks)
		{
			this.ranks.fromDB(dbranks.ranks);
			this.lastRewardTime = dbranks.lastRewardTime;
			this.ranksSnapshot.fromDB(dbranks.snapshot, dbranks.snapshotCreateTime);
		}
		
		public synchronized SBean.DBSectRanks toDB()
		{
			return new SBean.DBSectRanks(id, ranks.toDB(), new ArrayList<>(ranksSnapshot.getSnapshot()), ranksSnapshot.getCreateTime(), this.lastRewardTime);
		}
		
		public String toString()
		{
			return "sect rank " + this.id; 
		}
	}
	
	interface SectRewarder
	{
		void doReward(int id, int sectID);
	}
	
	public static class SectRankData extends SectRankDataBase
	{
		SectRewarder sectRewarder;
		public SectRankData(int id, SectRewarder sectRewarder)
		{
			super(id);
			this.sectRewarder = sectRewarder;
		}
		
		protected void doReward()
		{
			if (this.sectRewarder != null)
			{
			}
		}
	}
	
	public interface RankBlackListFileWatcher
	{
		void onSetBlackListFile(String fileName, FileWatchdogHandler handler);
	}
	public interface RankBlackListAffector
	{
		void onBlackListChanged(boolean blackListOn, Set<Integer> lst);
	}
	public static class RankBlackList
	{
		boolean blackListOn = false;
		Set<Integer> idBlackList = new HashSet<>();
		RankBlackListFileWatcher fileWatcher;
		RankBlackListAffector affector;
		public RankBlackList (RankBlackListFileWatcher fileWatcher, RankBlackListAffector affector)
		{
			this.fileWatcher = fileWatcher;
			this.affector = affector;
		}
		
		public void setBlackList(boolean blackListOn, Set<Integer> lst)
		{
			this.affector.onBlackListChanged(blackListOn, lst);
		}
		
		public void setCfgFile(final String fileName)
		{
			this.fileWatcher.onSetBlackListFile(fileName, this::reloadFile);
		}
		
		public void reloadFile(String filePath)
		{
			try 
			{
				boolean blacklistOn = false;
				Set<Integer> blackList = new TreeSet<>();
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
				for (String line = in.readLine(); line != null; line = in.readLine()) 
				{
					//System.out.println(line);
					String linetrim = line.trim();
					if (!linetrim.startsWith("#"))
					{
						if (linetrim.startsWith("BlackList"))
						{
							String[] strs = linetrim.split("\\s+", 2);
							if (strs.length == 2 && strs[0].equals("BlackList") && strs[1].toLowerCase().equals("on"))
							{
								blacklistOn = true;
							}
						}
						else if (!linetrim.isEmpty())
						{
							try
							{
								int id = Integer.parseInt(linetrim.toLowerCase());
								blackList.add(id);
							}
							catch (Exception e)
							{
								
							}
						}
					}
				}
				in.close();
				setBlackList(blacklistOn, blackList);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
}



	


