
package i3k.gs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;
import i3k.SBean;
import i3k.gs.Role.RpcRes;
import i3k.util.GameTime;

public class RankManager
{
	private static final int RANK_SAVE_INTERVAL 	= 900;
	public final static int RANK_RAND_INTERVAL_MAX = 60;
	public final static int MAX_RANK_PAGE_LENGTH = 20;
	GameServer gs;
	int saveTime;

	RankData.RankBlackList rankRoleBlackList;
	//个人排行榜
	RankData.RoleRewarder roleRewarder;
	List<RankData.RoleRankData> allRoleRanks = new ArrayList<>();
	
	//帮派排行榜
	RankData.SectRewarder sectRewarder;
	List<RankData.SectRankData> allSectRanks = new ArrayList<>();
	
	public RankManager(GameServer gs)
	{
		this.gs = gs;
		this.rankRoleBlackList = new RankData.RankBlackList(
				(filename, handler) -> 
				{
					gs.getResourceManager().addWatch(filename, handler);
				}, 
				(blackListOn, lst) ->
				{
					gs.getLogger().info(this.getClass().getSimpleName() + " : reloadFile blacklist " + (blackListOn?"on":"off"));
					resetRankRoleBlcakList(blackListOn, lst);
				});
		this.roleRewarder = (int id, int roleId, SBean.RankTitle rtCfg) ->
		{
			gs.getLoginManager().exeCommonRoleVisitor(roleId, false, new LoginManager.CommonRoleVisitor()
			{
				@Override
				public boolean visit(Role role, Role sameUserRole)
				{
					role.addRoleTitle(rtCfg.title);
					return true;
				}
				
				@Override
				public void onCallback(boolean success)
				{
					if(success)
						gs.getLogger().debug("rank " + id + " reward role " + roleId + " title " + rtCfg.title + " success");
				}
			});
		};
		
		this.sectRewarder = (int rankID, int sectID) ->
		{
			//TODO
		};
	}
	
	public class SaveTrans implements Transaction
	{
		public SaveTrans(SBean.DBRanks dbRanks)
		{
			this.dbRanks = dbRanks;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("ranks");
			byte[] data = Stream.encodeLE(dbRanks);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if( errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("ranks save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private final SBean.DBRanks dbRanks;
	}
	
	public void init(SBean.DBRanks dbranks)
	{
		this.initRoleRanks(dbranks == null ? null : dbranks.roleRanks);
		this.initSectRanks(dbranks == null ? null : dbranks.sectRanks);
		this.rankRoleBlackList.setCfgFile(gs.getConfig().rankMaskFileName);
	}
	
	private void initRoleRanks(List<SBean.DBRoleRanks> dbRoleRanks)
	{
		if (dbRoleRanks != null)
		{
			for (SBean.DBRoleRanks e : dbRoleRanks)
			{
				allRoleRanks.add(createRoleRankDatafromDB(e));
			}
		}
		
		for (int i = allRoleRanks.size() + 1; i <= GameData.getInstance().getRoleRankCount(); ++i)
		{
			allRoleRanks.add(new RankData.RoleRankData(i, roleRewarder));
		}
		
		RankData.RoleRankData rrd = this.allRoleRanks.get(GameData.RANK_TYPE_ROLE_LEVEL - 1);
		rrd.setRankSnapshotChangeHandler((timeTick, snapshot)->
		{
			Integer rankKey = snapshot.getRankKey(100);
			if(rankKey != null)
				gs.getLoginManager().updateSpeedUpLvl(timeTick, GameData.getRoleLevelFromRoleLevelRankKey(rankKey));
		});
	}
	
	RankData.RoleRankData createRoleRankDatafromDB(SBean.DBRoleRanks dbranks)
	{
		RankData.RoleRankData data = new RankData.RoleRankData(dbranks.id, roleRewarder);
		data.fromDB(dbranks);
		return data;
	}
	
	List<SBean.DBRoleRanks> getRoleRanksDB()
	{
		return allRoleRanks.stream().map(RankData.RoleRankData::toDB).collect(Collectors.toList());
	}
	
	SBean.DBRanks toDB()
	{
		return new SBean.DBRanks(getRoleRanksDB(), getSectRanksDB(), 0, 0);
	}
	
	public void save()
	{
		gs.getDB().execute(new SaveTrans(toDB()));
		this.saveTime = GameTime.getTime();
	}
	
	void onTimer(int timeTick)
	{
		this.allRoleRanks.forEach(rd -> rd.onTimer(timeTick));
		this.allSectRanks.forEach(rd -> rd.onTimer(timeTick));
		if (this.saveTime + RANK_SAVE_INTERVAL < timeTick)
			this.save();
	}
	
	public void updateRankSnapShot(int rankID, List<SBean.RankRole> snapshot, int snapshotCreateTime)
	{
		if (rankID <= 0 || rankID > this.allRoleRanks.size())
			return;
		RankData.RoleRankData rankData = this.allRoleRanks.get(rankID - 1);
		rankData.doRefresh(snapshot, snapshotCreateTime);
	}
	
	public void tryUpdateRoleRank(int id, SBean.RankRole rankRole)
	{
		if (id > 0 && id <= this.allRoleRanks.size() && rankRole.rankKey > 0)
			this.allRoleRanks.get(id-1).tryUpdateRank(0, rankRole);
	}
	
	public List<SBean.RankBrief> getRoleRankBriefs(int level)
	{
		return this.allRoleRanks.stream().filter(e -> e.isOpenOnLevel(level)).map(RankData.RoleRankData::getRankBrief).collect(Collectors.toList());
	}
	
	public Role.RpcRes<List<SBean.RankRole>> getRanksSnapshotById(int id, int createTime, int index, int len)
	{
		return Optional.<RankData.RoleRankData>ofNullable(id <= 0 || id > this.allRoleRanks.size() ? null : this.allRoleRanks.get(id-1)).map(rd -> rd.getRanksSnapshot(createTime, index, len)).orElse(RankData.RoleRankData.UNKNOWNERROR);
	}
	
	//快照排名
	public int getRoleSnapshotRank(int id, int roleID)
	{
		if(id <= 0 || id > this.allRoleRanks.size())
			return -1;
		
		return this.allRoleRanks.get(id-1).getItemSnapshotRank(roleID);
	}
	
	//实时排名
	public int getRankRoleRank(int id, int roleId)
	{
		if(id <= 0 || id > this.allRoleRanks.size())
			return -1;
		
		SBean.RankRole rankRole = this.allRoleRanks.get(id - 1).getItemRank(roleId);
		return rankRole == null ? 0 : rankRole.rankKey;
	}
	
	
	private void resetRankRoleBlcakList(boolean blackListOn, Set<Integer> lst)
	{
		this.allRoleRanks.forEach(rd -> rd.resetRankBlcakList(blackListOn, lst));
	}
	//-------------------------------------------------sect rank------------------------------------------------
	private void initSectRanks(List<SBean.DBSectRanks> dbSectRanks)
	{
		if(dbSectRanks != null)
		{
			for(SBean.DBSectRanks e: dbSectRanks)
			{
				this.allSectRanks.add(createSectRankDatafromDB(e));
			}
		}
		
		for (int i = this.allSectRanks.size() + 1; i <= GameData.getInstance().getSectRankConut(); ++i)
		{
			this.allSectRanks.add(new RankData.SectRankData(i, sectRewarder));
		}
	}
	
	RankData.SectRankData createSectRankDatafromDB(SBean.DBSectRanks dbSectRanks)
	{
		RankData.SectRankData data = new RankData.SectRankData(dbSectRanks.id, sectRewarder);
		data.fromDB(dbSectRanks);
		return data;
	}
	
	List<SBean.DBSectRanks> getSectRanksDB()
	{
		return allSectRanks.stream().map(RankData.SectRankData::toDB).collect(Collectors.toList());
	}
	
	public void tryUpdateSectRank(int rankID, SBean.RankSect rankSect, int rankClearTime)
	{
		if(rankID > 0 && rankID <= this.allSectRanks.size() && rankSect.rankKey > 0)
			this.allSectRanks.get(rankID - 1).tryUpdateRank(rankClearTime, rankSect);
	}
	
	public void tryUpdateSectRank(int rankID, SBean.RankSect rankSect)
	{
		tryUpdateSectRank(rankID, rankSect, 0);
	}
	
	public List<SBean.RankBrief> getSectRankBriefs(int roleLevel)
	{
		return this.allSectRanks.stream().filter(e -> e.isOpenOnLevel(roleLevel)).map(RankData.SectRankData::getRankBrief).collect(Collectors.toList());
	}
	
	public Role.RpcRes<List<SBean.RankSect>> getSectRanksSnapshotById(int id, int createTime, int index, int len)
	{
		return Optional.<RankData.SectRankData>ofNullable(id <= 0 || id > this.allSectRanks.size() ? null : this.allSectRanks.get(id-1)).map(rd -> rd.getRanksSnapshot(createTime, index, len)).orElse(RankData.SectRankData.UNKNOWNERROR);
	}
	
	public int getSectSnapshotRank(int rankID, int sectID)
	{
		if(rankID <= 0 || rankID > this.allRoleRanks.size())
			return -1;
		
		return this.allSectRanks.get(rankID - 1).getItemSnapshotRank(sectID);
	}
	
	public int getSectRankClearTime(int rankID)
	{
		if(rankID <= 0 || rankID > this.allRoleRanks.size())
			return -1;
		
		return this.allSectRanks.get(rankID - 1).getRankRewardTime();
	}
}
