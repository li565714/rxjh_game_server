package i3k.fight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import ket.kdb.Table;
import ket.kdb.TableEntry;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import i3k.DBFightRanks;
import i3k.SBean;
import i3k.fight.GlobalRankData.GlobalRoleRankData;
import i3k.fight.GlobalRankData.RankDataSyncer;
import i3k.gs.GameData;
import i3k.gs.RankData;
import i3k.util.GameTime;

public class FightRankManager
{
	private static final int RANK_SAVE_INTERVAL 	= 900;
	FightServer fs;
	int saveTime;
	
	RankDataSyncer syncer;
	RankData.RankBlackList rankRoleBlackList;
	ConcurrentMap<Integer, FightRank> allServerRanks = new ConcurrentHashMap<>();
	
	class FightRank
	{
		Map<Integer, GlobalRoleRankData> allRoleRanks = new HashMap<>();
		
		FightRank()
		{
			
		}
		
		FightRank init(int gsid, DBFightRanks fightRanks, RankDataSyncer syncer)
		{
			if(fightRanks != null && fightRanks.ranks != null)
			{
				for(Map.Entry<Integer, SBean.DBRoleRanks> e: fightRanks.ranks.entrySet())
				{
					int rankID = e.getKey();
					GlobalRoleRankData grrd = new GlobalRoleRankData(rankID, gsid, syncer);
					grrd.fromDB(e.getValue());
					allRoleRanks.put(rankID, grrd);
				}
			}
			
			for (int rankID = 1; rankID <= GameData.getInstance().getRoleRankCount(); ++rankID)
			{
				if(allRoleRanks.containsKey(rankID))
					continue;
				
				switch (rankID)
				{
				case GameData.RANK_TYPE_GLOBAL_FORCEWAR_WHITE:
				case GameData.RANK_TYPE_GLOBAL_FORCEWAR_BLACK:
					//TODO ÔÝÊ±Ã»ÓÐ
					break;
				case GameData.RANK_TYPE_LOCAL_FORCEWAR_WHITE:
				case GameData.RANK_TYPE_LOCAL_FORCEWAR_BLACK:
				case GameData.RANK_TYPE_SUPER_ARENA_HISTORY:
				case GameData.RANK_TYPE_SUPER_ARENA_WEEK:
					allRoleRanks.put(rankID, new GlobalRoleRankData(rankID, gsid, syncer));
					break;
				default:
					break;
				}
			}
			
			return this;
		}
		
		DBFightRanks toDB()
		{
			DBFightRanks dbRank = new DBFightRanks();
			dbRank.ranks = this.allRoleRanks.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toDB()));
			return dbRank;
		}
		
		void onTimer(int timeTick)
		{
			this.allRoleRanks.values().forEach(rd -> rd.onTimer(timeTick));
		}
		
		void doSync()
		{
			this.allRoleRanks.values().forEach(grd -> grd.doSync());
		}
		
		void tryUpdateRank(int rankID, int rankClearTime, SBean.RankRole newRankRole)
		{
			GlobalRoleRankData grd = this.allRoleRanks.get(rankID);
			if(grd == null)
				return;
			
			grd.tryUpdateRank(rankClearTime, newRankRole);
		}
		
		void tryUpdateRank(int rankID, int rankClearTime, SBean.RankRole newRankRole, SBean.RankRole addRankRole)
		{
			GlobalRoleRankData grd = this.allRoleRanks.get(rankID);
			if(grd == null)
				return;
			
			grd.tryUpdateRank(rankClearTime, newRankRole, addRankRole);
		}
		
		SBean.RankClearTime getForceWarRankCurClearTime()
		{
			int white = this.allRoleRanks.get(GameData.RANK_TYPE_LOCAL_FORCEWAR_WHITE) == null ? 0 : this.allRoleRanks.get(GameData.RANK_TYPE_LOCAL_FORCEWAR_WHITE).lastRewardTime;
			int black = this.allRoleRanks.get(GameData.RANK_TYPE_LOCAL_FORCEWAR_BLACK) == null ? 0 : this.allRoleRanks.get(GameData.RANK_TYPE_LOCAL_FORCEWAR_BLACK).lastRewardTime;
			
			return new SBean.RankClearTime(white, black);
		}
		
		int getSuperArenaCurClearTime()
		{
			return this.allRoleRanks.get(GameData.RANK_TYPE_SUPER_ARENA_WEEK) == null ? 0 : this.allRoleRanks.get(GameData.RANK_TYPE_SUPER_ARENA_WEEK).lastRewardTime;
		}
		
		void resetRankBlcakList(boolean blackListOn, Set<Integer> lst)
		{
			this.allRoleRanks.values().forEach(rd -> rd.resetRankBlcakList(blackListOn, lst));
		}
	}
	
	public class SaveFightRankTrans implements Transaction
	{
		SaveFightRankTrans(Map<Integer, DBFightRanks> serverRanks)
		{
			this.serverRanks = serverRanks;
		}
		
		@Override
		public boolean doTransaction()
		{
			for(Map.Entry<Integer, DBFightRanks> e: serverRanks.entrySet())
			{
				fightranks.put(e.getKey(), e.getValue());
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				fs.getLogger().warn("fight server ranks save failed");
			}
		}
		
		@AutoInit
		public Table<Integer, DBFightRanks> fightranks;
		
		private final Map<Integer, DBFightRanks> serverRanks;
	}
	
	public FightRankManager(FightServer fs)
	{
		this.fs = fs;
		this.syncer = (gsid, rankData) -> {
			if (gsid > 0)
			{
				syncGSFightRank(gsid, rankData);
			}
			else
			{
				for(int serverID: fs.getRPCManager().getAllServers())
					this.syncGSFightRank(serverID, rankData);
			}
		};
		
		this.rankRoleBlackList = new RankData.RankBlackList(
				(fileName, handler) -> 
				{
					fs.getResourceManager().addWatch(fileName, handler);
				}, 
				(blackListOn, lst) ->
				{
					fs.getLogger().info(this.getClass().getSimpleName() + " : reloadFile blacklist " + (blackListOn?"on":"off"));
					resetRankRoleBlcakList(blackListOn, lst);
				});
	}
	
	public void init(TableReadonly<Integer, DBFightRanks> dbServerRanks)
	{
		if(dbServerRanks != null)
		{
			for(TableEntry<Integer, DBFightRanks> e: dbServerRanks)
			{
				int gsid = e.getKey();
				this.allServerRanks.put(gsid, new FightRank().init(gsid, e.getValue(), this.syncer));
			}
		}
		
		this.rankRoleBlackList.setCfgFile(fs.getConfig().rankMaskFileName);
	}
	
	public void save()
	{
		fs.getDB().execute(new SaveFightRankTrans(toDB()));
		this.saveTime = GameTime.getTime();
	}
	
	Map<Integer, DBFightRanks> toDB()
	{
		return this.allServerRanks.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toDB()));
	}
	
	public void onTimer(int timeTick)
	{
		this.allServerRanks.values().forEach(sr -> sr.onTimer(timeTick));
		
		if (this.saveTime + RANK_SAVE_INTERVAL < timeTick)
			this.save();
	}
	
	void syncGSFightRank(int gsid, GlobalRankData.GlobalRoleRankData roleRankData)
	{
		final int BATCH_SIZE = 50;
		fs.getRPCManager().notifyGSSyncRankStart(gsid, roleRankData.getId(), roleRankData.getSnapshot().getCreateTime());
		int start = 0;
		while(start < roleRankData.getSnapshot().getSnapshot().size())
		{
			int end = start + BATCH_SIZE;
			if(end > roleRankData.getSnapshot().getSnapshot().size())
				end = roleRankData.getSnapshot().getSnapshot().size();
			
			if(start != end)
				fs.getRPCManager().notifyGSSyncRank(gsid, roleRankData.getId(), new ArrayList<>(roleRankData.getSnapshot().getSnapshot().subList(start, end)));
			start = end;
		}
		fs.getRPCManager().notifyGSSyncRankEnd(gsid, roleRankData.getId());
	}
	
	public void onGameServerConnect(int gsid)
	{
		FightRank fr = this.allServerRanks.get(gsid);
		if(fr == null)
		{
			fr = new FightRank().init(gsid, null, syncer);
			this.allServerRanks.put(gsid, fr);
		}
		
		fr.doSync();
	}
	
	public void tryUpdateRank(int gsid, int rankID, int rankClearTime, SBean.RankRole newRankRole)
	{
		FightRank fr = this.allServerRanks.get(gsid);
		if(fr == null)
			return;
		
		fr.tryUpdateRank(rankID, rankClearTime, newRankRole);
	}
	
	public void tryUpdateRank(int gsid, int rankID, int rankClearTime, SBean.RankRole newRankRole, SBean.RankRole addRankRole)
	{
		FightRank fr = this.allServerRanks.get(gsid);
		if(fr == null)
			return;
		
		fr.tryUpdateRank(rankID, rankClearTime, newRankRole, addRankRole);
	}
	
	public SBean.RankClearTime getForceWarRankCurClearTime(int gsid)
	{
		FightRank fr = this.allServerRanks.get(gsid);
		if(fr == null)
			return new SBean.RankClearTime(0, 0); 
		
		return fr.getForceWarRankCurClearTime();
	}
	
	public int getSuperArenaCurClearTime(int gsid)
	{
		FightRank fr = this.allServerRanks.get(gsid);
		if(fr == null)
			return 0;
		
		return fr.getSuperArenaCurClearTime();
	}
	
	private void resetRankRoleBlcakList(boolean blackListOn, Set<Integer> lst)
	{
		this.allServerRanks.values().forEach(sr -> sr.resetRankBlcakList(blackListOn, lst));
	}
}
