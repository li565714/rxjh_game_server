package i3k.gs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;
import i3k.SBean;
import i3k.SBean.RankRole;
import i3k.gs.RankData.RankItemReader;
import i3k.gs.RankData.RankRoleReader;
import i3k.util.GameTime;

public class EmergencyManager
{
	private static final int EMERGENCY_SAVE_INTERVAL = 900;
	final static RankRoleReader RankRoleReader = new RankRoleReader();
	
	GameServer gs;
	private int lastSaveTime;
	private int rankday;

	private Map<Integer, Integer> joinRoles = new HashMap<>();
	private Map<Integer, SBean.EmergencyInfo> activityInfos = new HashMap<>();
	private EmergencyRank rank;
	private boolean isRankWeardSend;
	
	public class EmergencySaveTrans implements Transaction
	{
		EmergencySaveTrans(SBean.DBEmergency dbStele)
		{
			this.dbStele = dbStele;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("emergency");
			byte[] data = Stream.encodeLE(dbStele);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("emergency save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private final SBean.DBEmergency dbStele;
	}
	
	public static class EmergencyRank extends RankData.Ranks<SBean.RankRole>
	{
		public EmergencyRank(RankItemReader<RankRole> reader)
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
			return GameData.getInstance().getEmergencyCFGS().rankDisplayLimit;
		}
		
		int getRankMaxCnt()
		{
			return GameData.getInstance().getEmergencyCFGS().rankSaveLimit;
		}
		
		List<SBean.RankRole> getEmergencyShowRank()
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
	
	EmergencyManager(GameServer gs)
	{
		this.gs = gs;
		this.rank = new EmergencyRank(RankRoleReader);
	}
	
	public void init(SBean.DBEmergency dbEmergency)
	{
		if(dbEmergency == null)
		{
			return;
		}
		
		this.rankday = dbEmergency.rankday;
		
		this.isRankWeardSend = dbEmergency.isRankWeardSend == 1;
		this.rank.fromDB(dbEmergency.ranks);
		this.joinRoles = dbEmergency.joinRoles;
	}
	
	public void save()
	{
		gs.getDB().execute(new EmergencySaveTrans(toDB()));
		this.lastSaveTime = GameTime.getTime();
	}
	
	private synchronized SBean.DBEmergency toDB()
	{
		return new SBean.DBEmergency(rank.toDB(), this.rankday, isRankWeardSend ? (byte) 1 : (byte) 0, new HashMap<>(this.joinRoles));
	}
	
	public void onTimer(int timeTick)
	{
		int today = GameTime.getDay(timeTick);
		synchronized (this)
		{
			if (today != rankday)
				dayRefreash(today);
			if (GameTime.getSecondOfDay(timeTick) > GameData.getInstance().getEmergencyEndTime() && !isRankWeardSend)
				doReward();
		}
		if (timeTick > this.lastSaveTime + EMERGENCY_SAVE_INTERVAL)
			save();
	}

	private void dayRefreash(int today)
	{
		this.activityInfos.clear();
		this.rank.clearRank();
		this.isRankWeardSend = false;
		this.rankday = today;
	}

	//先发排名奖励，后发参与奖励
	private boolean doReward()
	{
		sendRankReward();
		sendJoinReward();

		if (isRankWeardSend && joinRoles.isEmpty())
		{
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
			int rid = EmergencyRank.getItemId(rankValue);
			this.joinRoles.remove(rid);
			
			List<SBean.GameItem> att = GameData.getInstance().toGameItems(GameData.getInstance().getEmergencyRankReward(rank));
			List<Integer> addinfo = new ArrayList<>();
			addinfo.add(rank);
			gs.getLoginManager().sysSendMail(rid, MailBox.SysMailType.EmergencyReward, MailBox.EMERGENCY_REWARD_MAIL_MAX_RESERVE_TIME, att, addinfo);
			rank++;
		}
		this.isRankWeardSend = true;
	}
	
	private void sendJoinReward()
	{
		int cnt = 0;
		final List<SBean.GameItem> att = GameData.getInstance().toGameItems(GameData.getInstance().getEmergencyJoinReward());
		List<Integer> addinfo = new ArrayList<>();
		addinfo.add(0);
		
		Iterator<Integer> it = joinRoles.keySet().iterator();
		while(it.hasNext() && cnt < 500)
		{
			int rid = it.next();
			it.remove();
			gs.getLoginManager().sysSendMail(rid, MailBox.SysMailType.EmergencyReward, MailBox.EMERGENCY_REWARD_MAIL_MAX_RESERVE_TIME, att, addinfo);
		}
	}
	
	
	public synchronized void tryUpdateRank(SBean.RoleOverview role, int prestige)
	{
		if (prestige <= 0)
			return;
		tryUpdateRank(new SBean.RankRole(role, GameData.createEmergencyRankKey(role.fightPower, this.joinRoles.merge(role.id, prestige, (ov, nv) -> ov + nv))));
	}
	
	private void tryUpdateRank(SBean.RankRole rankRole)
	{
		this.rank.tryUpdateRank(rankRole);
	}
	
	public synchronized SBean.RankRole getRoleRankRole(int roleID)
	{
		return this.rank.getRankRole(roleID);
	}
	
	public synchronized int getRolePrestige(int roleID)
	{
		return this.joinRoles.getOrDefault(roleID, 0);
	}
	
	public synchronized void syncEmergencyRank(Role role)
	{
		int roleRank = this.rank.getRoleRank(role.id);
		gs.getRPCManager().sendStrPacket(role.netsid, new SBean.emergency_rank_res(this.rank.getEmergencyShowRank(), roleRank));
	}
	
	public interface EmergencyIntCallBack
	{
		void callBack(int intPara);
	}
	
	public int getEmergencyMapInstance(int activityId)
	{
		SBean.EmergencyActivityCFGS eaCfgs = GameData.getInstance().getEmergencyActivityCFGS(activityId);
		if (eaCfgs == null)
			return 0;
		int instanceId = 0;
		SBean.TimeSpan timeSpan = null;
		SBean.EmergencyInfo info = this.activityInfos.get(activityId);
		if (info == null || info.instanceId == 0)
		{
			timeSpan = GameData.getCurEmergencyTime(eaCfgs.openTime);
			if (timeSpan != null && (info == null || info.isFinish == 0 || GameTime.getDayTime(timeSpan.startTime) != info.openTime))
				instanceId = gs.getMapService().createEmergencyMapCopy(eaCfgs.mapId, GameTime.getDayTime(timeSpan.endTime));
		}
		else
		{
			instanceId = this.activityInfos.get(activityId).instanceId;
		}
		if (instanceId == 0)
			return 0;
		if (timeSpan != null)
			this.activityInfos.merge(activityId, new SBean.EmergencyInfo(activityId, instanceId, 0, (byte)0, GameTime.getDayTime(timeSpan.startTime)), (ov, nv) -> new SBean.EmergencyInfo(ov.activityId, nv.instanceId, 0, (byte)0, nv.openTime));
		return this.activityInfos.get(activityId).instanceId;
	}

	public synchronized void checkCanEnterMapCopy(SBean.EmergencyActivityCFGS cfg, EmergencyIntCallBack callback)
	{
		if (cfg.maxRoleSize <= getActivityRoleSize(cfg.activityId))
		{
			callback.callBack(GameData.PROTOCOL_OP_EMERGENCY_MAP_ROLL_FULL);
			return;
		}
		if (gs.getEmergencyManager().getActivityIsFinish(cfg.activityId))
		{
			callback.callBack(GameData.PROTOCOL_OP_EMERGENCY_MAP_ROLL_FULL);
			return;
		}
		callback.callBack(getEmergencyMapInstance(cfg.activityId));
	}
	
	public int getActivityRoleSize(int activityId)
	{
		SBean.EmergencyInfo info = this.activityInfos.get(activityId);
		if (info == null || info.instanceId == 0)
			return 0;
		return info.roleSize;
	}

	public boolean getActivityIsFinish(int activityId)
	{
		SBean.EmergencyInfo info = this.activityInfos.get(activityId);
		if (info == null || info.instanceId == 0)
			return false;
		return info.isFinish == 1;
	}
	
	public synchronized void activityRoleJoin(int activityId)
	{
		SBean.EmergencyInfo info = this.activityInfos.get(activityId);
		if (info == null || info.instanceId == 0)
			return;
		info.roleSize++;
	}

	public synchronized void activityRoleLeave(int activityId)
	{
		SBean.EmergencyInfo info = this.activityInfos.get(activityId);
		if (info == null || info.instanceId == 0)
			return;
		info.roleSize--;
		if (info.roleSize < 0)
			info.roleSize = 0;
	}

	public synchronized void syncEmergencyInfos(int sessionId)
	{
		tryRefreshMapInfo();
		gs.getRPCManager().sendStrPacket(sessionId, new SBean.emergency_sync_res(Stream.clone(this.activityInfos)));
	}

	private void tryRefreshMapInfo()
	{
		Set<Integer> needRemove = new HashSet<>(); 
		for (SBean.EmergencyInfo info : this.activityInfos.values())
		{
			SBean.EmergencyActivityCFGS eaCfgs = GameData.getInstance().getEmergencyActivityCFGS(info.activityId);
			if (eaCfgs == null)
				continue;
			SBean.TimeSpan timeSpan = GameData.getCurEmergencyTime(eaCfgs.openTime);
			if (timeSpan != null && GameTime.getDayTime(timeSpan.startTime) != info.openTime)
				needRemove.add(info.activityId);
		}
		for (int activityId : needRemove)
			this.activityInfos.remove(activityId);
	}

	public synchronized void onEmergencyMapFinish(int mapId, int instanceId)
	{
		for (SBean.EmergencyInfo info : this.activityInfos.values())
		{
			SBean.EmergencyActivityCFGS eaCfgs = GameData.getInstance().getEmergencyActivityCFGS(info.activityId);
			if (eaCfgs.mapId == mapId && info.instanceId == instanceId)
			{
				info.isFinish = 1;
				info.instanceId = 0;
				
				gs.getLogger().info("emergency map manager map [" + mapId + ", " + instanceId + "] finish");
				return;
			}
		}
	}

}
