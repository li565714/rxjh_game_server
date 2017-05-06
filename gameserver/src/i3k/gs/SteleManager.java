package i3k.gs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;
import i3k.SBean;
import i3k.SBean.RankRole;
import i3k.gs.RankData.RankItemReader;
import i3k.gs.RankData.RankRoleReader;
import i3k.util.GameTime;

/**
 * 太玄碑文
 * 排行榜，每天5点清
 * 排行奖励和参与奖励 活动结束后 发邮件
 */

public class SteleManager
{
	public static final int STELT_CARD_MAX_COUNT = 60000;
	
	private static final int STELE_SAVE_INTERVAL = 900;
	private static final int RANK_SHOW_COUNT_LIMIT 	= 50;
	private static final int RANK_MAX_COUNT_LIMIT 	= 150;
	final static RankRoleReader RankRoleReader = new RankRoleReader();
	
	GameServer gs;
	private int lastSaveTime;
	private int lastDayRefresh;
	
	private SteleRank rank;
	private boolean isRankWeardSend;
	
	private int lastCreateSteleTime;
	private int type;
	private List<Integer> remainTimes = new ArrayList<>();
	
	public class SteleSaveTrans implements Transaction
	{
		SteleSaveTrans(SBean.DBStele dbStele)
		{
			this.dbStele = dbStele;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("stele");
			byte[] data = Stream.encodeLE(dbStele);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("stele save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private final SBean.DBStele dbStele;
	}
	
	public static class SteleRank extends RankData.Ranks<SBean.RankRole>
	{
		public SteleRank(RankItemReader<RankRole> reader)
		{
			super(reader);
		}
		
		boolean isEmpty()
		{
			return this.rankItems.isEmpty();
		}
		
		int getRoleRank(int roleID)
		{
			if(!this.rankItems.containsKey(roleID))
				return 0;
			
			return getRoleRankImpl(roleID);
		}
		
		int getRoleRankImpl(int queryRoleID)
		{
			int rank = 1;
			for(long rankValue: this.rankValues.descendingSet())
			{
				int rid = getItemId(rankValue);
				if(rid == queryRoleID)
					return rank;
				rank++;
			}
			
			return 0;
		}
		
		int getRankShowCnt()
		{
			return GameData.getInstance().getSteleCFGS().base.showRanks > RANK_SHOW_COUNT_LIMIT ? RANK_SHOW_COUNT_LIMIT : GameData.getInstance().getSteleCFGS().base.showRanks;
		}
		
		int getRankMaxCnt()
		{
			return GameData.getInstance().getSteleCFGS().base.maxRanks > RANK_MAX_COUNT_LIMIT ? RANK_MAX_COUNT_LIMIT : GameData.getInstance().getSteleCFGS().base.maxRanks;
		}
		
		List<SBean.RankRole> getSteleShowRank()
		{
			int showCnt = getRankShowCnt();
			List<SBean.RankRole> showRank = new ArrayList<>();
			for(long rankValue: this.rankValues.descendingSet())
			{
				int rid = getItemId(rankValue);
				SBean.RankRole rr = this.rankItems.get(rid);
				if(rr != null)
					showRank.add(rr.kdClone());
				
				if(showRank.size() == showCnt)
					break;
			}
			
			return showRank;
		}
		
		void tryUpdateRank(SBean.RankRole rankRole)
		{
			super.tryUpdateRank(rankRole, getRankMaxCnt());
		}
		
		SBean.RankRole getRankRole(int roleID)
		{
			return this.rankItems.get(roleID);
		}
	}
	
	SteleManager(GameServer gs)
	{
		this.gs = gs;
		this.rank = new SteleRank(RankRoleReader);
	}
	
	public void init(SBean.DBStele dbStele)
	{
		if(dbStele == null)
			return;
		
		this.lastDayRefresh = dbStele.lastDayRefresh;
		
		this.lastCreateSteleTime = dbStele.lastCreateSteleTime;
		this.type = dbStele.type;
		this.remainTimes = dbStele.remainTimes;
		
		this.isRankWeardSend = dbStele.isRankWeardSend == 1;
		this.rank.fromDB(dbStele.ranks);
	}
	
	public void save()
	{
		gs.getDB().execute(new SteleSaveTrans(toDB()));
		this.lastSaveTime = GameTime.getTime();
	}
	
	public void mapStartSyncSteles(int sid, Set<Integer> maps)
	{
		if(this.type == 0 || this.remainTimes.isEmpty())
			return;
		
		SBean.SteleMineralTypeCFGS typeCfg = GameData.getInstance().getSteleMineralTypeCFGS(type);
		if(typeCfg == null)
			return;
		
		for(int i = 0; i < typeCfg.minerals.size(); i++)
		{
			if(i < 0 || i >= this.remainTimes.size())
				continue;
			
			SBean.SteleMineralCFGS e = typeCfg.minerals.get(i);
			if((maps.contains(e.mapLocation.mapID)))
				gs.getMapService().syncCreateStele(e.mapLocation.mapID, type, i + 1, this.remainTimes.get(i));
		}
	}
	
	private synchronized SBean.DBStele toDB()
	{
		return new SBean.DBStele(lastDayRefresh, 
								 lastCreateSteleTime, 
								 type, 
								 new ArrayList<>(remainTimes), 
								 rank.toDB(), 
								 isRankWeardSend ? (byte) 1: (byte) 0);
	}
	
	public void onTimer(int timeTick)
	{
		dayRefresh(timeTick);
		tryUpdateStele(timeTick);
		
		if(timeTick > this.lastSaveTime + STELE_SAVE_INTERVAL)
			save();
	}
	
	private synchronized void dayRefresh(int timeTick)
	{
		int curDay = GameData.getDayByRefreshTimeOffset(timeTick);
		if(curDay != this.lastDayRefresh)
		{
			//异常处理,发上次活动的奖励
			if(!this.remainTimes.isEmpty())
				notifyMapClearStele();
			
			if(this.type > 0 && ((!this.isRankWeardSend && !this.rank.isEmpty())))
			{
				if(!doReward())
					return;
			}
			this.rank.clearRank();
			this.lastDayRefresh = curDay;
		}
	}
	
	private synchronized void tryUpdateStele(int timeTick)
	{
		if(GameData.getInstance().checkSteleInOpenTime(timeTick))
			tryCreateStele(timeTick);
		else
			tryDestroyStele(timeTick);
	}
	
	private void tryCreateStele(int timeTick)
	{
		if(this.type > 0)
			return;
		
		int type = GameData.getInstance().randSteleMineralType();
		SBean.SteleMineralTypeCFGS typeCfg = GameData.getInstance().getSteleMineralTypeCFGS(type);
		if(typeCfg == null)
			return;
		
		this.type = type;
		this.remainTimes.clear();
		this.isRankWeardSend = false;
		this.rank.clearRank();
		
		for(int i = 0; i < typeCfg.minerals.size(); i++)
		{
			SBean.SteleMineralCFGS m = typeCfg.minerals.get(i);
			this.remainTimes.add(m.mineralTimes);
			gs.getMapService().syncCreateStele(m.mapLocation.mapID, type, i + 1, m.mineralTimes);
		}

		this.lastCreateSteleTime = timeTick;
	}
	
	private void tryDestroyStele(int timeTick)
	{
		if(this.type == 0)
			return;
		
		notifyMapClearStele();
		doReward();
	}
	
	//先发排名奖励，后发参与奖励
	private boolean doReward()
	{
		sendRankReward();
		
		if(isRankWeardSend)
		{
			this.type = 0;
			return true;
		}
		return false;
	}
	
	private void sendRankReward()
	{
		if(this.isRankWeardSend)
			return;

		int rank = 1;
		for(long rankValue: this.rank.rankValues.descendingSet())
		{
			SBean.SteleRewardCFGS reward = GameData.getInstance().getSteleRankReward(rank);
			if(reward == null)
				break;
			
			int rid = SteleRank.getItemId(rankValue);
			
			List<SBean.GameItem> att = GameData.getInstance().toGameItems(reward.rewards);
			List<Integer> addinfo = new ArrayList<>();
			addinfo.add(rank);
			
			gs.getLoginManager().sysSendMail(rid, MailBox.SysMailType.SteleReward, MailBox.STELE_REWARD_MAIL_MAX_RESERVE_TIME, att, addinfo);
			gs.getLogger().debug("stele send role " + rid + " rank reward");
			rank++;
		}
		this.isRankWeardSend = true;
	}
	
	private void notifyMapClearStele()
	{
		if(!this.remainTimes.isEmpty())
		{
			SBean.SteleMineralTypeCFGS typeCfg = GameData.getInstance().getSteleMineralTypeCFGS(this.type);
			if(typeCfg != null)
			{
				for(int i = 0; i < typeCfg.minerals.size(); i++)
				{
					SBean.SteleMineralCFGS m = typeCfg.minerals.get(i);
					gs.getMapService().syncDestroyStele(m.mapLocation.mapID, this.type, i + 1);
				}
			}
		}
		this.remainTimes.clear();
	}
	
	public synchronized void syncSteleRemainTimes(int steleType, int index, int remainTimes)
	{
		if(steleType != this.type)
			return;
		
		if(index <= 0 || index > this.remainTimes.size())
			return;
		
		this.remainTimes.set(index - 1, remainTimes);
	}

	public synchronized void syncRoleAddSteleCards(Role role, int addCards, int addType)
	{
		if(this.type == 0 || this.remainTimes.isEmpty() || this.lastCreateSteleTime == 0)
			return;
		
		int cards = role.addSteleCards(addCards, addType);
		if(cards > 0)
			tryUpdateRank(new SBean.RankRole(role.getRoleOverview(), GameData.createSteleCardRankKey(~getUseTime(), cards)));
	}
	
	public synchronized void tryUpdateRank(SBean.RoleOverview role, int cards)
	{
		if(this.type == 0 || this.remainTimes.isEmpty() || this.lastCreateSteleTime == 0 || cards == 0)
			return;
		
		tryUpdateRank(new SBean.RankRole(role, GameData.createSteleCardRankKey(~getUseTime(), cards)));
	}
	
	private void tryUpdateRank(SBean.RankRole rankRole)
	{
		this.rank.tryUpdateRank(rankRole);
	}
	
	public synchronized SBean.RankRole getRoleRankRole(int roleID)
	{
		return this.rank.getRankRole(roleID);
	}
	
	private int getUseTime()
	{
		int time = GameTime.getTime() - this.lastCreateSteleTime;
		if(time > 0xffff)
			time = 0xffff;
		
		return time;
	}
	
	public synchronized void tryRefreshRoleStele(Role role)
	{
		SBean.DBRoleStele roleSteleInfo = role.tryRefreshStele(this.type == 0 || this.remainTimes.isEmpty());
		if(roleSteleInfo.index > 0)
		{
			boolean canMineral = roleSteleInfo.index > this.remainTimes.size() ?  false : this.remainTimes.get(roleSteleInfo.index - 1) > 0;
			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.role_stele(this.type, roleSteleInfo, canMineral ? (byte)1 : (byte)0));
		}
	}
	
	public synchronized void syncSteleInfo(Role role)
	{
		SBean.DBRoleStele roleSteleInfo = role.tryRefreshStele(this.type == 0 || this.remainTimes.isEmpty());
		gs.getRPCManager().sendStrPacket(role.netsid, new SBean.stele_sync_res(this.remainTimes.isEmpty() ? 0 : this.type, new ArrayList<>(this.remainTimes), roleSteleInfo));
	}
	
	public synchronized int roleJoinStele(Role role)
	{
		if(this.type == 0 || this.remainTimes.isEmpty())
			return GameData.PROTOCOL_OP_STELE_NOT_IN_OPENTIME;
		
		SBean.SteleMineralTypeCFGS typeCfg = GameData.getInstance().getSteleMineralTypeCFGS(this.type);
		if(typeCfg == null)
			return GameData.PROTOCOL_OP_STELE_NOT_IN_OPENTIME;
		
		if(!role.canJoinStele())
			return GameData.PROTOCOL_OP_FAILED;
		
		return role.joinSteleSuccess();
	}
	
	public synchronized void syncSteleRank(Role role)
	{
		int roleRank = this.rank.getRoleRank(role.id);
		gs.getRPCManager().sendStrPacket(role.netsid, new SBean.stele_rank_res(this.rank.getSteleShowRank(), roleRank));
	}
	
	public synchronized void syncRoleMineSteleSuccess(Role role, int mineID)
	{
		if(this.type > 0)
			role.mineSteleSuccess(this.type, mineID);
	}
	
	public synchronized int roleTryMineStele(Role role, int mineID)
	{
		return role.tryMineStele(this.type, this.remainTimes, mineID);
	}
	
	public void syncRefreshSteleMonster(int roleID, String roleName, int mapID, int mapLine, int steleType, int index, int monsterID)
	{
		StringBuilder paras = new StringBuilder();
		paras.append(roleID);
		paras.append("|");
		paras.append(roleName);
		paras.append("|");
		paras.append(mapID);
		paras.append("|");
		paras.append(mapLine);
		paras.append("|");
		paras.append(steleType);
		paras.append("|");
		paras.append(index);
		paras.append("|");
		paras.append(monsterID);
		
		gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_STELE_MONSTER, paras.toString());
		gs.getLogger().debug("roll notice refresh stele monster " + paras.toString());
	}
	
	public synchronized boolean teleportStele(Role role, int steleType, int index)
	{
		if(this.type != steleType || this.remainTimes.isEmpty())
			return false;
		
		return role.teleportStele(steleType, index);
	}
}
