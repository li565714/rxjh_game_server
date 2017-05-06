package i3k.gs;

import i3k.DBSect;
import i3k.SBean;
import i3k.SBean.CommonSectCFGS;
import i3k.SBean.DBSectHistory;
import i3k.SBean.DBSectMember;
import i3k.TLog;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;
import i3k.gs.LoginManager;
import i3k.gs.Role.RpcRes;
import i3k.gs.SectManager.SectRenameCallback;

public class Sect 
{
	private static final int SECT_RAND_INTERVAL_MAX = 60;
	private static final int SAVE_SECT_INTERVAL_RAND = 300;
	private static final int SECT_SAVE_INTERVAL = 900;
	private static final int SECT_STANDY_INTERVAL = 3600*24*7;

	public static final int SECT_MEMBER_CHIEF = 1;
	public static final int SECT_MEMBER_DEPUTY = 2;
	public static final int SECT_MEMBER_ELDER = 3;
	public static final int SECT_MEMBER_OTHER = 4;

	public static final int SECT_AUTH_SETJOB = 1;
	public static final int SECT_AUTH_ACCEPT = 2;
	public static final int SECT_AUTH_SECTBOOS = 3;
	public static final int SECT_AUTH_SECTMAPCOPY = 4;
	public static final int SECT_AUTH_SECTUPLVL = 5;
	public static final int SECT_AUTH_SECTFIGHT = 6;
	public static final int SECT_AUTH_MANOR = 7;
	public static final int SECT_AUTH_OPEN_MAPCOPY = 8;
	public static final int SECT_AUTH_CREED = 9;
	public static final int SECT_AUTH_MAIL = 10;
	public static final int SECT_AUTH_QQGROUP = 11;
	public static final int SECT_AUTH_SECT_NAME = 12;
	public static final int SECT_AUTH_SECT_ICON = 13;
	public static final int SECT_AUTH_JOIN_LVL = 14;
	public static final int SECT_AUTH_APPLY_OPTION = 15;
	public static final int SECT_AUTH_KICK_MEMBER = 16;
	
	public Sect(GameServer gs, int id) 
	{
		this.gs = gs;
		this.id = id;

		this.randTick = GameRandom.getRandInt(0, SECT_RAND_INTERVAL_MAX);
		int now = GameTime.getTime();
		this.lastSaveTime = now;
		this.recentUseTime = now;
		//this.lastUpdateTime = now;
	}

	public synchronized void updateUseTime()
	{
		this.recentUseTime = GameTime.getTime();
	}
	
	public synchronized boolean isStandbyTimeOut(int timeTick)
	{
		return this.recentUseTime + SECT_STANDY_INTERVAL < timeTick;
	}
	
	public Sect newCreate(String name, short icon, SBean.RoleOverview creater) 
	{
		this.name = name;
		this.level = 1;
		this.lastUpgradeTime = GameTime.getTime();
		this.creed = "";	
		this.icon = icon;
		this.frame = 0;
		this.joinLvlReq =  GameData.getInstance().getCommonCFG().sect.joinLvlReq;
		this.vitality = GameData.getInstance().getCommonCFG().sect.startVit;
		this.weekVit = 0;
		this.rankClearTime = 0;

		this.chief = creater.id;
		this.deputy = new TreeSet<>();
		this.elder = new TreeSet<>();
		this.members = new TreeMap<>();
		this.members.put(creater.id, createNewMember(creater));
		//this.applys = new TreeMap<Integer, SBean.RoleOverview>();
		this.applys = new TreeSet<>();
		
		this.lastDayRefresh = 0;
		this.lastRewardsTime = 0;

		this.auras = new TreeMap<>();
		this.history = new ArrayList<>();
		this.banquets = new TreeMap<>();

		this.dayResetMapTimes = new TreeMap<>();
		this.openedMaps = new TreeMap<>();
		this.mapRewards = new TreeMap<>();
		this.mapRewardsLog = new ArrayList<>();
		this.mapCurAttacker = new TreeMap<>();
		this.logFinishedMaps = new TreeMap<>();
		this.shareTaskLib = new TreeMap<>();

		//this.onlineMembers = new TreeSet<Integer>();
		
		this.openSectSkill();
		
		this.dayMailTimes = 0;
		this.climbTowerData = new TreeMap<>();//new SBean.DBClimbTowerRecordDataCfg(0, "", 0);
		this.updateSectHistory(SBean.DBSectHistory.SectEventCreate, "", creater.name, false, 0, 0);
		this.sectDeliver = new TreeMap<>();
		this.sectDeliverWish = initDeliverWish();
		this.lastRefreshDeliverWishTime = 0;
		this.deliverRank = new TreeMap<>();
		this.shares = new TreeMap<>();
		this.applicationPush = 1;
		this.groupMapData = new TreeMap<>();
		this.groupMapCurInfo = new SBean.SectGroupMapCurInfo(0, 0, -1, new HashSet<>());
		this.qqgroup = "";
		this.dayKickTimes = 0;
		gs.getTLogger().logSectEventFlow(this, TLog.SECT_CREATE);
		return this;
	}
	
	
	
	public Sect fromDB(DBSect dbSect) 
	{
		this.name = dbSect.name;
		this.level = dbSect.level;
		this.lastUpgradeTime = dbSect.lastUpgradeTime;
		this.creed = dbSect.creed;
		this.icon = dbSect.icon;
		this.frame = dbSect.frame;
		this.joinLvlReq = dbSect.joinLvlReq; 
		this.vitality = dbSect.vitality;
		this.weekVit = dbSect.weekVit;
		this.rankClearTime = dbSect.rankClearTime;

		this.chief = dbSect.chief;
		this.deputy = dbSect.deputy;
		this.elder = dbSect.elder;
		this.members = dbSect.members;
		this.applys = dbSect.applys;
		
		this.lastDayRefresh = dbSect.lastDayRefresh;
		this.lastRewardsTime = dbSect.lastRewardsTime;

		this.auras = dbSect.auras;
		this.history = dbSect.history;
		this.banquets = dbSect.banquets;

		this.dayResetMapTimes = dbSect.dayResetMapTimes;
		this.openedMaps = dbSect.openedMaps;
		this.mapRewards = dbSect.mapRewards;
		this.mapRewardsLog = dbSect.mapRewardsLog;
		this.mapCurAttacker = new TreeMap<>();
		this.logFinishedMaps = dbSect.logFinishedMaps;
		this.shareTaskLib = dbSect.shareTaskLib;
		
		this.dayMailTimes = dbSect.dayMailTimes;
		this.climbTowerData = dbSect.climbTowerData;
		//this.onlineMembers = new TreeSet<Integer>();
		this.sectDeliver = dbSect.sectDeliver;
		this.sectDeliverWish = dbSect.sectDeliverWish;
		this.lastRefreshDeliverWishTime = dbSect.lastRefreshDeliverWishTime;
		this.deliverRank = dbSect.deliverRank;
		this.shares = dbSect.shares;
		this.applicationPush = dbSect.applicationPush;
		this.banquets.keySet().stream().filter(bid -> bid > this.nextBanquetID.get()).forEach(bid -> this.nextBanquetID.set(bid + 1));
		this.groupMapData = dbSect.groupMapData;
		this.qqgroup = dbSect.qqgroup;
		this.dayKickTimes = dbSect.dayKickTimes;
		return this;
	}

	public DBSect toDB() 
	{
		DBSect dbSect = new DBSect();

		dbSect.id = this.id;
		dbSect.name = this.name;
		dbSect.level = this.level;
		dbSect.lastUpgradeTime = this.lastUpgradeTime;
		dbSect.creed = this.creed;
		dbSect.icon = this.icon;
		dbSect.frame = this.frame;
		dbSect.joinLvlReq = this.joinLvlReq; 
		dbSect.vitality = this.vitality;
		dbSect.weekVit = this.weekVit;
		dbSect.rankClearTime = this.rankClearTime;

		dbSect.chief = this.chief;
		dbSect.deputy = new TreeSet<>(this.deputy);
		dbSect.elder = new TreeSet<>(this.elder);
		dbSect.members = Stream.clone(this.members);
		//dbSect.applys = Stream.clone(this.applys);
		dbSect.applys = new TreeSet<>(this.applys);
		
		dbSect.lastDayRefresh = this.lastDayRefresh;
		dbSect.lastRewardsTime = this.lastRewardsTime;

		dbSect.auras = Stream.clone(this.auras);
		dbSect.history = Stream.clone(this.history);
		dbSect.banquets = Stream.clone(this.banquets);

		dbSect.dayResetMapTimes = new TreeMap<>(this.dayResetMapTimes);
		dbSect.openedMaps = Stream.clone(this.openedMaps);
		dbSect.mapRewards = Stream.clone(this.mapRewards);
		dbSect.mapRewardsLog = Stream.clone(this.mapRewardsLog);
		dbSect.logFinishedMaps = new TreeMap<>(this.logFinishedMaps);
		dbSect.shareTaskLib = new TreeMap<>(this.shareTaskLib);
		
		dbSect.dayMailTimes = this.dayMailTimes;
		dbSect.climbTowerData = new TreeMap<>(this.climbTowerData);
		dbSect.sectDeliver = new TreeMap<>(this.sectDeliver);
		dbSect.sectDeliverWish = this.sectDeliverWish;
		dbSect.lastRefreshDeliverWishTime = this.lastRefreshDeliverWishTime;
		dbSect.deliverRank = new TreeMap<>(this.deliverRank);
		dbSect.shares = new TreeMap<>(this.shares);
		dbSect.applicationPush = this.applicationPush;
		dbSect.groupMapData = new TreeMap<>(this.groupMapData);
		dbSect.qqgroup = this.qqgroup;
		dbSect.dayKickTimes = this.dayKickTimes;
		return dbSect;
	}

	private SBean.DBSectMember createNewMember(SBean.RoleOverview overview) 
	{
		return new SBean.DBSectMember(new SBean.SectMember(overview, new SBean.RoleSectStats(0, 0, 0, 0), GameTime.getTime(), GameTime.getTime()), new SBean.SectWorshipedData(0, 0), 0);
	}

	interface SaveSectTransCallback 
	{
		void onCallback(boolean ok);
	}

	public class SaveSectTrans implements Transaction 
	{
		public SaveSectTrans(DBSect dbsect, SaveSectTransCallback callback) 
		{
			this.dbsect = dbsect;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction() 
		{
			gs.getLogger().debug("save sect " + dbsect.id + " to DB");
			sect.put(dbsect.id, dbsect);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode) 
		{
			boolean ok = errcode == ErrorCode.eOK;
			gs.getLogger().info("save sect " + dbsect.id + " to DB " + (ok ? "ok" : " error " + errcode));
			if (this.callback != null)
				this.callback.onCallback(ok);
		}

		@AutoInit
		public Table<Integer, DBSect> sect;

		private DBSect dbsect;
		private SaveSectTransCallback callback;
	}

	private void doSave() 
	{
		gs.getDB().execute(new SaveSectTrans(this.toDB(), null), this.id); 
		this.lastSaveTime = GameTime.getTime();
	}
	
	public synchronized void save()
	{
		this.doSave();
	}

	public synchronized boolean onMemberLogin(Role role) 
	{
		SBean.DBSectMember member = this.members.get(role.id);
		if (member == null)
			return false;
		role.updateSectAuras(this.getSectAurasWithoutLock());
		role.updateSectInfo(this.id, this.name, this.getMemberPosition(role.id), this.icon, this.level);
		if (this.getSectManagement().contains(role.id) && this.applicationPush == GameData.SECT_APPLICATION_PUSH_FLAG_YES)
			role.notifyApplicationNum(this.applys.size());
		if(member.data.joinTime > role.sectData.data.lastJoinTime)
			role.sectData.data.lastJoinTime = member.data.joinTime;
		initSectDeliverData(role);
		int now = GameTime.getTime();
		member.data.role = role.getRoleOverview();
		//member.data.lastLogoutTime = now;
		return true;
	}

	private void initSectDeliverData(Role role)
	{
		if (!sectDeliver.containsKey(role.id) || sectDeliver.get(role.id).isDeliver == 0)
			role.tryClearSectDeliverData();
		else if (role.sectDeliver.startTime <= 0)
			sectDeliver.put(role.id, new SBean.DBDeliverInSect(0));

		sectDeliverOnTimeOut(role);
		role.loginSyncSectDeliver();
	}

	public synchronized void onMemberLogout(Role role) 
	{
		SBean.DBSectMember member = this.members.get(role.id);
		if (member == null)
			return;
		int now = GameTime.getTime();
		member.data.role = role.getRoleOverview();
		member.data.lastLogoutTime = now;
	}

	public synchronized  void onMemberInfoChanged(Role role)
	{
		SBean.DBSectMember member = this.members.get(role.id);
		if (member == null)
			return;
		member.data.role = role.getRoleOverview();
	}
	
	public synchronized void onMemberOnlineUpdate(Role role)
	{
		SBean.DBSectMember member = this.members.get(role.id);
		if (member == null)
			return;
		int now = GameTime.getTime();
		member.data.role = role.getRoleOverview();
		member.data.lastLogoutTime = now;
	}

	
	public synchronized void onTimer(int timeTick) 
	{
		if (timeTick % SECT_RAND_INTERVAL_MAX == this.randTick)
		{
			this.tryAllocateMapRewards(timeTick);	
			this.checkChiefLeave(timeTick);
			if (this.groupMapCurInfo == null)
			{
				tryInitCurGroupMapInfo();
				return;
			}
		}
		if (this.lastSaveTime + SECT_SAVE_INTERVAL <= timeTick)
		{
			doSave();
			//gs.getTLogger().logSectInfoFlow(gs.getConfig().id, gs.getConfig().gameAppId, this.id, this.level, this.members.size());
		}
	}
	
	private void checkChiefLeave(int timeTick)
	{
		int chiefLastLogoutTime = this.members.get(this.chief).data.lastLogoutTime;
		SBean.CommonSectCFGS sectCommonCFGS = GameData.getInstance().getCommonCFG().sect;
		if (chiefLastLogoutTime + sectCommonCFGS.chiefTimeout < timeTick)
		{
			int logoutDay = (timeTick - chiefLastLogoutTime) / GameTime.getDayTimeSpan();
			for (int deputyId : this.deputy)
			{
				if (this.members.get(deputyId).data.lastLogoutTime + sectCommonCFGS.receiveChiefLastTime > timeTick)
				{
					changeChiefByTimeOut(deputyId, logoutDay);
					return;
				}
			}
			for (int elderId : this.elder)
			{
				if (this.members.get(elderId).data.lastLogoutTime + sectCommonCFGS.receiveChiefLastTime > timeTick)
				{
					changeChiefByTimeOut(elderId, logoutDay);
					return;
				}
			}
			for (SBean.DBSectMember member : members.values())
			{
				if (member.data.lastLogoutTime + sectCommonCFGS.receiveChiefLastTime > timeTick)
				{
					changeChiefByTimeOut(member.data.role.id, logoutDay);
					return;
				}
			}
		}
	}

	private void changeChiefByTimeOut(int roleId, int logoutDay)
	{
		gs.getLoginManager().sysSendMail(this.chief, MailBox.SysMailType.ChiefTimeout, MailBox.MAX_RESERVE_TIME, logoutDay + "|" + members.get(roleId).data.role.name, new ArrayList<>(), new ArrayList<>());
		gs.getLoginManager().sysSendMail(roleId, MailBox.SysMailType.GetSectChiefByTimeout, MailBox.MAX_RESERVE_TIME, new ArrayList<>(), new ArrayList<>());
		updateSectHistory(SBean.DBSectHistory.SectEventChangeChiefByTimeout, members.get(roleId).data.role.name, members.get(this.chief).data.role.name, false, logoutDay, 0);
		setMemberPosition(roleId, SECT_MEMBER_CHIEF);
		gs.getLoginManager().exeCommonRoleVisitor(roleId, false, new LoginManager.CommonRoleVisitor() 
		{
			@Override
			public boolean visit(Role role, Role sameUserRole) 
			{
				role.updateSectInfo(Sect.this.id, Sect.this.name, SECT_MEMBER_CHIEF, Sect.this.icon, Sect.this.level);
				return true;
			}
			@Override
			public void onCallback(boolean success) 
			{	
			}
		});
	}

	public synchronized int getDayMailTimes()
	{
		return this.dayMailTimes;
	}
	
	private void dayRefresh()
	{
		int nowday = GameData.getDayByRefreshTimeOffset(GameTime.getTime());
		if (nowday != this.lastDayRefresh)
		{
			this.banquets.clear();
			this.nextBanquetID.set(0);
			this.dayResetMapTimes.clear();
			for (SBean.DBSectMember m : this.members.values())
			{
				m.data.stats.dayVitality = 0;
				m.worshiped.dayWorshipedTimes = 0;
				if ((nowday - 3) / 7 != (lastDayRefresh - 3) / 7)
				{
					m.data.stats.weekVitality = 0;
					m.data.stats.weekSectMapTime = 0;
				}
			}
			if ((nowday - 3) / 7 != (lastDayRefresh - 3) / 7)
			{
				this.sectDeliverWish = initDeliverWish();
				this.deliverRank.clear();
			}
			this.shareTaskLib.clear();
			this.dayRefreshDiySkill();
			this.lastDayRefresh = nowday;
			this.dayKickTimes = 0;
		}
	}
	
	
	private void tryAllocateMapRewards(int timeTick)
	{
		int daySecond = GameTime.getSecondOfDay(timeTick);
		if (daySecond >= GameData.getInstance().getCommonCFG().sect.mapRecordsAllocateStartTime && daySecond <= GameData.getInstance().getCommonCFG().sect.mapRecordsAllocateEndTime)
		{
			int curRewardTime = GameTime.getHourTime(timeTick) + 1800;
			if (timeTick > curRewardTime && this.lastRewardsTime < curRewardTime)
			{
				gs.getLogger().info("start allocate sect maps rewards ...");
				for (Map.Entry<Integer, SBean.SectMapRewards> e : this.mapRewards.entrySet())
				{
					SBean.SectMapRewards rewards = e.getValue();
					gs.getLogger().info("allocate sect map " + e.getKey() + " rewards count " + rewards.rewards.size());
					for (SBean.SectMapRewardRecord ee : rewards.rewards.values())
					{
						gs.getLogger().debug("allocate sect map " + e.getKey() + " reward " + ee.rewardId + ", count=" + ee.count + ", applicants=" + ee.applicants.size());
						if (ee.count > 0 && ee.applicants.size() > 0)
						{
							int applicantSeq = 1;
							int rewardSeq = 1;
							while (rewardSeq <= ee.count)
							{
								if (applicantSeq <= ee.applicants.size())
								{
									int rid = ee.applicants.get(applicantSeq-1);
									++applicantSeq;
									SBean.DBSectMember member = this.members.get(rid);
									if (member != null)
									{
										rewardSeq++;
										mapRewardsLog.add(new SBean.SectMapRewardsLog(timeTick, ee.rewardId, rid, member.data.role.name));
										SBean.GameItem gi = GameData.getInstance().toGameItem(ee.rewardId, 1);
										if (gi != null)
										{
											List<SBean.GameItem> att = new ArrayList<>();
											att.add(gi);
											List<Integer> addinfo = new ArrayList<>();
											addinfo.add(ee.rewardId);
											gs.getLoginManager().sysSendMail(rid, MailBox.SysMailType.SectMap, MailBox.SECTMAP_MAIL_MAX_RESERVE_TIME, att, addinfo);
											updateSectHistory(SBean.DBSectHistory.SectEventAssignRewards, "", member.data.role.name, false, ee.rewardId, 0);
											gs.getLogger().trace("allocate sect map " + e.getKey() + " reward " + ee.rewardId + " to " + " role " + rid + " mail box!");
										}
										break;
									}
								}
								else
								{
									break;
								}
							}
							ee.count -= (rewardSeq - 1);
							ee.applicants.subList(0, applicantSeq-1).clear();	
						}
					}
				}
				if (mapRewardsLog.size() > 50)
					mapRewardsLog.subList(0, mapRewardsLog.size()-50).clear();
				this.lastRewardsTime = timeTick;
			}
		}
	}

	// get功能小函数
	private String getMemberName(int memberId) 
	{
		SBean.DBSectMember member = this.members.get(memberId);
		return member == null ? "" : member.data.role.name;
	}
	
	public Collection<Integer> getAllMembers()
	{
		return this.members.keySet();
	}

	public Collection<Integer> getAllApplicants()
	{
		//return this.applys.keySet();
		return this.applys;
	}
	
	public String getName()
	{
		return this.name;
	}

	public synchronized SBean.SectOverview getSectOverview()
	{
		return getSectOverviewWithoutLock();
	}
	
	public SBean.SectOverview getSectOverviewWithoutLock() 
	{
		return new SBean.SectOverview(this.id, this.name, this.level, this.chief, this.getMemberName(this.chief), this.members.size(), this.creed, this.icon, this.frame, this.joinLvlReq);
	}

	public SBean.SectDetail getSectDetailWithoutLock() 
	{
		return new SBean.SectDetail(this.getSectOverviewWithoutLock(), this.lastUpgradeTime, this.vitality, this.applys.size(), this.dayMailTimes, this.applicationPush, new String(this.qqgroup), this.dayKickTimes);
	}

	public List<SBean.DBSectAura> getSectAuraListWithoutLock() 
	{
		List<SBean.DBSectAura> auras = new ArrayList<>();
		for (SBean.DBSectAura e : this.auras.values())
		{
			auras.add(e.kdClone());
		}
		return auras;
	}

	public SBean.RoleSectStats getSectMemberStatsWithoutLock(int memberId) 
	{
		SBean.DBSectMember member = this.members.get(memberId);
		return member == null ? null : member.data.stats.kdClone();
	}
	
	public void onRoleSyncSect(Role role)
	{
		this.syncRoleMemberContribution(role);
		this.testSectApplication(role);
		this.testSectBanquetNotice(role);
		this.testSectShareTaskDoneRewards(role);
		role.testSectTaskNotice();
	}
	
	private void testSectShareTaskDoneRewards(Role role)
	{
		if(this.shareTaskLib.get(role.id) != null && this.shareTaskLib.get(role.id).tasks.values().stream().anyMatch(item -> item.leftRewardTimes > 0))
			role.notifySectSharedTaskReward();
	}

	Map<Integer, Integer> getSectAurasWithoutLock() 
	{
		Map<Integer, Integer> auras = new TreeMap<>();
		this.auras.values().stream().filter(e -> e.level > 0).forEach(e -> auras.put(e.id, e.level));
		return auras;
	}
	
	// set 功能小函数	
	private void addVit(int val)
	{
		if (val <= 0)
			return;
		this.vitality += val;
		
		int rankClearTime = gs.getRankManager().getSectRankClearTime(GameData.RANK_TYPE_SECT_WEEK_VIT);
		if(rankClearTime != this.rankClearTime)
		{
			this.rankClearTime = rankClearTime;
			this.weekVit = 0;
		}
		this.weekVit += val;
		gs.getRankManager().tryUpdateSectRank(GameData.RANK_TYPE_SECT_WEEK_VIT, new SBean.RankSect(getSectOverviewWithoutLock(), getWeekVitRankKey()), this.rankClearTime);
	}
	
	//前22位：帮派周活跃度(400w)	 后10位：每周分钟数（10分钟为单位）
	private int getWeekVitRankKey()
	{
		return (int)(((this.weekVit << 10) & 0xfffffc00L) | (getRankTimeKey() & 0x000003ffL));
	}
	
	private int getRankTimeKey()
	{
		return ~(((GameTime.getTime() - 3600 * 5) % (86400 * 7)) / (10 * 60));
	}
	
	
	private void useVit(int val)
	{
		if (val <= 0)
			return;
		this.vitality -= val;
		if (this.vitality < 0)
			this.vitality = 0;
	}
	
	private boolean canUseVit(int val)
	{
		return this.vitality >= val;
	}
	
	
	public void removeApplys(int memId)
	{
		this.applys.remove(memId);
	}
	
//	private void setSectMemberLastLogoutTime(int memberId, int time) 
//	{
//		SBean.DBSectMember member = this.members.get(memberId);
//		if (member != null) 
//		{
//			member.data.lastLogoutTime = time;
//		}
//	}
	
	public synchronized boolean memberSendSectMsg(int memberId, SBean.MessageInfo msg)
	{
		if (this.dismissed)
			return false;
		if (!this.members.containsKey(memberId))
			return false;
		sendSectMsg(msg);
		return true;
	}
	
	private void sendSectMsg(SBean.MessageInfo msg)
	{
		for (int mid : this.members.keySet())
		{
			Role role = gs.getLoginManager().getOnGameRole(mid);
			if (role != null)
			{
				role.receiveMsg(msg);
			}
		}
	}

	private Collection<Integer> getSectManagement()
	{
		List<Integer> lst = new ArrayList<>();
		if (GameData.getInstance().getSectAuthorityCFGS(GameData.SECT_MEMBER_TYPE_CHIEF).accept == 1)
			lst.add(this.chief);
		if (GameData.getInstance().getSectAuthorityCFGS(GameData.SECT_MEMBER_TYPE_DEPUTY).accept == 1)
			lst.addAll(this.deputy);
		if (GameData.getInstance().getSectAuthorityCFGS(GameData.SECT_MEMBER_TYPE_ELDER).accept == 1)
			lst.addAll(this.elder);
		return lst;
	}
	
	int getMemberPosition(int memberId)
	{
		if (this.chief == memberId)
			return Sect.SECT_MEMBER_CHIEF;
		else if (this.deputy.contains(memberId))
			return Sect.SECT_MEMBER_DEPUTY;
		else if (this.elder.contains(memberId))
			return Sect.SECT_MEMBER_ELDER;
		else if (this.members.containsKey(memberId))
			return Sect.SECT_MEMBER_OTHER;
		else
			return -1;
	}
	
//	public int getMemeberInSectOfJobType(int memberId){
//		return this.getMemberPosition(memberId);  
//	}
	
	private void setMemberPosition(int memberId, int position)
	{
		if (this.deputy.contains(memberId))
			this.deputy.remove(memberId);
		
		if(this.elder.contains(memberId))
			this.elder.remove(memberId);
		
		if (position == Sect.SECT_MEMBER_CHIEF)
			this.chief = memberId;
		else if (position == Sect.SECT_MEMBER_DEPUTY)
			this.deputy.add(memberId);
		else if (position == Sect.SECT_MEMBER_ELDER)
			this.elder.add(memberId);
		else if(position == Sect.SECT_MEMBER_OTHER){
			//this.members.remove(memberId);
			if (this.deputy.contains(memberId))
				this.deputy.remove(memberId);
			
			if(this.elder.contains(memberId))
				this.elder.remove(memberId);
		}
	}
	/*
	 * other 如果是检查是否有职位任免权限 需要传 职位id， 其他情况为0
	 */
	public boolean checkAuth(int operatorPosition, int authType, int authArg) 
	{
		SBean.SectAuthorityCFGS authCFGS = GameData.getInstance().getSectAuthorityCFGS(operatorPosition);
		if (authCFGS == null)
			return false;

		switch (authType) {
			case SECT_AUTH_SETJOB :
				return authCFGS.authority.contains(authArg);
			case SECT_AUTH_ACCEPT :
				return  (authCFGS.accept == 1);
			case SECT_AUTH_SECTBOOS :
				return (authCFGS.sectBoss == 1);
			case SECT_AUTH_SECTMAPCOPY :
				return (authCFGS.sectMapCopy == 1);
			case SECT_AUTH_SECTUPLVL :
				return (authCFGS.sectUpLvl == 1);
			case SECT_AUTH_SECTFIGHT :
				return (authCFGS.sectFight == 1);
			case SECT_AUTH_MANOR :
				return (authCFGS.manorFight == 1);
			case SECT_AUTH_OPEN_MAPCOPY :
				return (authCFGS.mapReset == 1);
			case SECT_AUTH_CREED :
				return (authCFGS.creed == 1);
			case SECT_AUTH_MAIL :
				return (authCFGS.sectMail == 1);
			case SECT_AUTH_QQGROUP :
				return (authCFGS.qqGroup == 1);
			case SECT_AUTH_SECT_NAME :
				return (authCFGS.changeSectName == 1);
			case SECT_AUTH_SECT_ICON :
				return (authCFGS.changeSectIcon == 1);
			case SECT_AUTH_JOIN_LVL :
				return (authCFGS.changeJoinLvl == 1);
			case SECT_AUTH_APPLY_OPTION :
				return (authCFGS.applyOption == 1);
			case SECT_AUTH_KICK_MEMBER :
				return authCFGS.kickMember == 1;
			default:
				return false;
		}
	}
	
	private void updateSectHistory(int event, String eventRoleName, String operatorName, boolean broadcast, int arg, int arg2)
	{
		SBean.DBSectHistory history = new SBean.DBSectHistory(GameTime.getTime(), event, eventRoleName, operatorName, arg, arg2);
		this.history.add(history);
		int maxExventCount = GameData.getInstance().getCommonCFG().sect.eventMax;
		if (this.history.size() > maxExventCount)
			this.history.subList(0, this.history.size() - maxExventCount).clear();
		if (broadcast)
			boradcastSectHistory(history);
	}

	private void boradcastSectHistory(DBSectHistory history)
	{
		for (int rid : members.keySet())
		{
			Role member = gs.getLoginManager().getOnGameRole(rid);
			if (member != null)
				member.pushHistoryBroadcast(history);
		}
	}

	public synchronized int roleDismissSect(Role role)
	{
		this.dayRefresh();
		if (role.id != this.chief)
			return GameData.PROTOCOL_OP_FAILED;
		if(this.members.size() > 1)
			return GameData.PROTOCOL_OP_FAILED;
		if (!this.mapCurAttacker.isEmpty())
			return GameData.PROTOCOL_OP_SECT_BEING_ATTACK_MAP;

		this.dismissed = true;
		role.exitFromSect(false);
		gs.getTLogger().logSectEventFlow(this, TLog.SECT_DISMISS);
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized void syncRoleMemberContribution(Role role)
	{
		this.dayRefresh();
		SBean.DBSectMember sectMember = this.members.get(role.id);
		if (sectMember == null)
			return;
		sectMember.data.role = role.getRoleOverview();
		//this.lastUpdateTime = GameTime.getTime();
	}
	
	public synchronized void roleMemeberDedicateActivity(Role role, int value)
	{
		TLogger.TLogEvent tlogEvent = gs.getTLogger().createNewEvent(TLog.AT_ROLE_MEMEBER_USE_VIE);
		this.dayRefresh();
		SBean.DBSectMember sectMember = this.members.get(role.id);
		if (sectMember == null)
			return;
		tlogEvent.setArg(value, role.id);
		role.syncAddSectContribution(value, tlogEvent.getGameItemRecords());
		int maxDayVitality = GameData.getInstance().getCommonCFG().sect.maxDayVitality;
		if (sectMember.data.stats.dayVitality < maxDayVitality)
		{
			int addVitality = sectMember.data.stats.dayVitality + value > maxDayVitality ? maxDayVitality - sectMember.data.stats.dayVitality : value;
			sectMember.data.stats.dayVitality += addVitality;
			sectMember.data.stats.weekVitality += addVitality;
			this.addVit(addVitality);
		}
		gs.getTLogger().logRoleEventFlow(role, tlogEvent);
		//this.lastUpdateTime = GameTime.getTime();
	}

	public synchronized RpcRes<SBean.SectMembers> roleGetMembers(int roleId) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return new RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(roleId))
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		List<SBean.SectMember> members = new ArrayList<>();
		for (SBean.DBSectMember e : this.members.values()) 
		{
			SBean.SectMember clone = e.data.kdClone();
			Role memberRole = gs.getLoginManager().getOnGameRole(e.data.role.id);
			if (memberRole != null)
				clone.lastLogoutTime = 0;
			members.add(clone);
		}
		return new RpcRes<>(new SBean.SectMembers(this.chief, new TreeSet<>(this.deputy), new TreeSet<>(this.elder), members));
	}

	public synchronized void syncRoleGraspInfo(Role self)
	{
		this.dayRefresh();
		if (this.dismissed)
		{
			self.graspSync(GameData.PROTOCOL_OP_GRASP_SECT_NOT_EXIST, GameData.emptyList());
			return;
		}
		
		if (!this.members.containsKey(self.id))
		{
			self.graspSync(GameData.PROTOCOL_OP_GRASP_MEMBER_NOT_JOINSECT, GameData.emptyList());
			return;
		}
		
		LinkedList<SBean.RoleOverview> members = new LinkedList<>();
		for (SBean.DBSectMember e : this.members.values())
		{
			if(self.id == e.data.role.id || self.roleExpCoin.graspData.dayGraspRids.contains(e.data.role.id))
				continue;
			
			Role memberRole = gs.getLoginManager().getOnGameRole(e.data.role.id);
			if(memberRole != null)
				members.addFirst(e.data.role.kdClone());
			else
				members.addLast(e.data.role.kdClone());
		}
		
		self.graspSync(GameData.PROTOCOL_OP_SUCCESS, members);
	}
	
	public synchronized void getSectApplications(Role role, Role.GetSectApplicationsCallback callback) 
	{
			
		this.dayRefresh();
		if (this.dismissed)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED, GameData.emptyList());
		}
		if (!this.members.containsKey(role.id))
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED, GameData.emptyList());
		}
		gs.getLoginManager().getRoleOverviews(new ArrayList<>(this.applys), roleOverviews -> callback.onCallback(GameData.PROTOCOL_OP_SUCCESS, new ArrayList<>(roleOverviews.values())));
	}

	public synchronized Role.RpcRes<List<SBean.DBSectHistory>> getSectHistory() 
	{
		this.dayRefresh();
		if (this.dismissed)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		return new Role.RpcRes<>(Stream.clone(history));
	}
	
	void testSectApplication(Role role)
	{
		if (!this.applys.isEmpty())
			notifySectApplication(role);
	}
	
	void notifySectApplication(Role role)
	{
		int rolePosition = getMemberPosition(role.id);
		if(checkAuth(rolePosition, Sect.SECT_AUTH_ACCEPT, 0))
			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.sect_notice_application());	
	}

	public synchronized int roleTryApply(Role role) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.applys.contains(role.id))
			return GameData.PROTOCOL_OP_SECT_ALREADY_APPLY;
		if (this.members.containsKey(role.id))
		{
			this.applys.remove(role.id);
			return GameData.PROTOCOL_OP_SECT_ALREADY_JOIN;
		}
		if(role.level < this.joinLvlReq)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.applys.size() >=  GameData.getInstance().getCommonCFG().sect.sectMaxApplicant)
			return GameData.PROTOCOL_OP_SECT_APPLY_FULL;
			
		this.applys.add(role.id);
		for(int roleId : this.getSectManagement())
		{
			Role mRole = gs.getLoginManager().getOnGameRole(roleId);
			if (mRole != null)
			{
				notifySectApplication(mRole);
				if (this.applicationPush == GameData.SECT_APPLICATION_PUSH_FLAG_YES)
					mRole.notifyApplicationNum(this.applys.size());
			}
		}
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
//	public synchronized int roleTryFastApply(Role role) 
//	{
//		if (this.members.size() < GameData.getInstance().getCommonCFG().sect.fastApplyRole)
//			return false;
//		if (this.vitality < GameData.getInstance().getCommonCFG().sect.fastApplyActive)
//			return false;
//		
//		return roleTryApply(role);
//	}


	public synchronized void roleTryAcceptApplicant(Role role, SBean.RoleOverview applicant, SectManager.JoinSectCallback callback)
	{
		this.dayRefresh();
		if (this.dismissed)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		if (this.members.containsKey(applicant.id))
		{
			this.removeApplys(applicant.id);
			callback.onCallback(GameData.PROTOCOL_OP_SECT_ALREADY_JOIN);
			return;
		}
		SBean.SectUpLevelCFGS upLevelCFGS = GameData.getInstance().getSectUpLevel(this.level);
		if (upLevelCFGS == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		if (this.members.size() + nowJoinMemberSize >= upLevelCFGS.roleCount)
		{
			this.removeApplys(applicant.id);
			callback.onCallback(GameData.PROTOCOL_OP_SECT_MEMBERS_FULL);    //帮派已满
			return;
		}
		SBean.DBSectMember operator = this.members.get(role.id);
		if (operator == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
			return;
		}
		int rolePosition = getMemberPosition(role.id);
		if (!this.checkAuth(rolePosition, Sect.SECT_AUTH_ACCEPT, 0))
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED);
			return;
		}
		if (!this.applys.remove(applicant.id))
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_NOT_IN_APPLY);
			return;
		}
		nowJoinMemberSize ++;
		int position = getNextMemberPosition();
		gs.getLoginManager().exeCommonRoleVisitor(applicant.id, true, new LoginManager.CommonRoleVisitor() 
		{
			private int errCode;
		    int applicantId;
		    String channelId; 
		    String gameId; 
		    String uid;
		    int createTime;
		    int level;
		    int vip;
		    
			@Override
			public boolean visit(Role applicantRole, Role sameUserRole)
			{
				if (applicantRole.getSectId() != 0)//小于 0可能正在创建帮派
				{
					errCode = GameData.PROTOCOL_OP_SECT_EXIST_SECT;
					return false;
				}
				applicantRole.updateSectInfo(Sect.this.id, Sect.this.name, position, Sect.this.icon, Sect.this.level);
				applicantRole.updateSectAuras(Sect.this.getSectAurasWithoutLock());
				applicantRole.sectData.data.lastJoinTime = GameTime.getTime();
				applicantRole.logTasks(GameData.TASK_TYPE_JOIN_FACTION, 0, 0, 0);
				errCode = GameData.PROTOCOL_OP_SUCCESS;
				scratchRoleInfoForRecord(applicantRole);
				return true;
			}

			@Override
			public void onCallback(boolean success)
			{
				if (errCode == GameData.PROTOCOL_OP_SUCCESS)
				{
					synchronized (Sect.this)
					{
						Sect.this.members.put(applicant.id, Sect.this.createNewMember(applicant));
						Sect.this.updateSectHistory(SBean.DBSectHistory.SectEventJoin, applicant.name, role.name, false, 0, 0);
						Sect.this.setMemberPosition(applicant.id, position);
						switch (position)
						{
						case SECT_MEMBER_DEPUTY:
							Sect.this.updateSectHistory(SBean.DBSectHistory.SectEventAsDeputy, applicant.name, role.name, false, 0, 0);
							break;
						case SECT_MEMBER_ELDER:
							Sect.this.updateSectHistory(SBean.DBSectHistory.SectEventUpElder, applicant.name, role.name, false, 0, 0);
							break;
						default:
							break;
						}
					}
					Sect.this.save();
					gs.getTLogger().logSectEventFlow(Sect.this, TLog.ROLE_JOIN_SECT);
				}
				callback.onCallback(errCode);
				if (success)
				{
					gs.getTLogger().logRoleJoinSect(applicantId, channelId, gameId, role.getChannelOpenId(), uid, createTime, level, vip, Sect.this.id, TLog.JOINSECT_BEACCEPTED);
				}
				nowJoinMemberSize--;
			}
			
			private void scratchRoleInfoForRecord(Role applicant)
			{
			    applicantId = applicant.id;
			    channelId = applicant.getChannel();
			    gameId = applicant.getGameId();
			    uid = applicant.getUid();
			    createTime = applicant.createTime;
			    level = role.level;
			    vip = role.share.getVipLevel();
			}
		});
	}
	private int getNextMemberPosition()
	{
		int position = SECT_MEMBER_OTHER;
		switch (this.members.size())
		{
		case 1:
			position = SECT_MEMBER_DEPUTY;
			break;
		case 2:
			if (this.elder.isEmpty())
				position = SECT_MEMBER_ELDER;
			break;
		default:
			break;
		}
		return position;
	}
//	public synchronized int roleTryAcceptApplicant(Role role, SBean.RoleOverview applicant, boolean withApply) 
//	{
//		this.dayRefresh();
//		if (this.dismissed)
//			return GameData.PROTOCOL_OP_FAILED;
//		if (this.members.containsKey(applicant.id))
//		{
//			this.removeApplys(applicant.id);
//			return GameData.PROTOCOL_OP_SECT_ALREADY_JOIN;
//		}
//		SBean.SectUpLevelCFGS upLevelCFGS = GameData.getInstance().getSectUpLevel(this.level);
//		if (upLevelCFGS == null)
//			return GameData.PROTOCOL_OP_FAILED;
//		if (this.members.size() >= upLevelCFGS.roleCount)
//		{
//			this.removeApplys(applicant.id);
//			return GameData.PROTOCOL_OP_SECT_MEMBERS_FULL;    //帮派已满
//		}
//		SBean.DBSectMember operator = this.members.get(role.id);
//		if (operator == null)
//			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
//		int rolePosition = getMemberPosition(role.id);
//		if (!this.checkAuth(rolePosition, Sect.SECT_AUTH_ACCEPT, 0))
//			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
//		if (withApply && !this.applys.remove(applicant.id))
//			return GameData.PROTOCOL_OP_SECT_NOT_IN_APPLY;
//		this.members.put(applicant.id, this.createNewMember(applicant));
//		this.updateSectHistory(SBean.DBSectHistory.SectEventJoin, applicant.name, operator.data.role.name, false, 0, 0);
//		gs.getTLogger().logSectEventFlow(this, TLog.ROLE_JOIN_SECT);
//		int position = SECT_MEMBER_OTHER;
//		if (this.members.size() == 2)
//		{
//			position = SECT_MEMBER_DEPUTY;
//			this.setMemberPosition(applicant.id, SECT_MEMBER_DEPUTY);
//			this.updateSectHistory(SBean.DBSectHistory.SectEventAsDeputy, applicant.name, role.name, false, 0, 0);
//		}
//		else if (this.members.size() == 3 && this.elder.isEmpty())
//		{
//			position = SECT_MEMBER_ELDER;
//			this.setMemberPosition(applicant.id, SECT_MEMBER_ELDER);
//			this.updateSectHistory(SBean.DBSectHistory.SectEventUpElder, applicant.name, role.name, false, 0, 0);
//		}
//		final int positionF = position;
//		gs.getLoginManager().exeCommonRoleVisitor(applicant.id, true, new LoginManager.CommonRoleVisitor() 
//		{
//			@Override
//			public boolean visit(Role role, Role sameUserRole) 
//			{
//				role.updateSectInfo(Sect.this.id, Sect.this.name, positionF, Sect.this.icon, Sect.this.level);
//				role.updateSectAuras(Sect.this.getSectAurasWithoutLock());
//				role.sectData.data.lastJoinTime = GameTime.getTime();
//				return true;
//			}
//			@Override
//			public void onCallback(boolean success) 
//			{	
//			}
//		});
//		this.doSave();
//		//this.lastUpdateTime = GameTime.getTime();
//		return GameData.PROTOCOL_OP_SUCCESS;
//	}
	
	public synchronized int roleTryRefuseApplicant(Role role, int applicant) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember operator = this.members.get(role.id);
		if (operator == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		int rolePosition = getMemberPosition(role.id);
		if (!this.checkAuth(rolePosition, Sect.SECT_AUTH_ACCEPT, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		this.applys.remove(applicant);
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized int roleTryRefuseAllApplicants(Role role) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember operator = this.members.get(role.id);
		if (operator == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		int rolePosition = getMemberPosition(role.id);
		if (!this.checkAuth(rolePosition, Sect.SECT_AUTH_ACCEPT, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		this.applys.clear();
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int setSectQQGroup(Role role, String qqgroup) 
	{	
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = getMemberPosition(role.id);
		if (!this.checkAuth(rolePosition, Sect.SECT_AUTH_QQGROUP, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		if (this.qqgroup.length() > GameData.getInstance().getCommonCFG().sect.qqGroupLength)
			return GameData.PROTOCOL_OP_SECT_QQGROUP_TO_LONG;
		this.qqgroup = qqgroup;
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int kickSectMember(Role role, int memberId) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = getMemberPosition(role.id);
		if (!this.checkAuth(rolePosition, Sect.SECT_AUTH_KICK_MEMBER, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		SBean.SectUpLevelCFGS levelCFGS = GameData.getInstance().getSectUpLevel(this.level);
		if (levelCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.dayKickTimes >= levelCFGS.dayKickTimes)
			return GameData.PROTOCOL_OP_SECT_KICK_TIMES_NEED;
		if (!this.members.containsKey(memberId))
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		
		for (SBean.SectMapAttacker attacker : this.mapCurAttacker.values())
		{
			if (attacker.roleId == memberId)
				return GameData.PROTOCOL_OP_SECT_BEING_ATTACK_MAP;
		}
		
		if (this.groupMapCurInfo.curRoles.contains(memberId))
			return GameData.PROTOCOL_OP_SECT_BEING_ATTACK_GROUP_MAP;
		
		SBean.DBSectMember kickedMember = this.members.remove(memberId);
		this.sectDiySkillRemove(memberId);
		this.setMemberPosition(memberId, Sect.SECT_MEMBER_OTHER);
		gs.getTLogger().logSectEventFlow(this, TLog.ROLE_LEAVE_SECT_BY_KICK);
		gs.getLoginManager().exeCommonRoleVisitor(memberId, false, new LoginManager.CommonRoleVisitor() 
		{
			
			@Override
			public boolean visit(Role role, Role sameUserRole) 
			{
				role.exitFromSect(true);
				return true;
			}
			
			@Override
			public void onCallback(boolean success) 
			{	
			}
		});
		Role kickedRole = gs.getLoginManager().getOnGameRole(memberId);
		if (kickedRole != null)
		{
			kickedRole.updateSectAuras(GameData.emptyMap());
			kickedRole.clearSectTask();
		}
		this.dayKickTimes ++;
		updateSectHistory(SBean.DBSectHistory.SectEventKick, kickedMember.data.role.name, role.name, false, 0, 0);
		this.doSave();
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int leaveSect(Role role) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.chief == role.id && this.members.size() > 1)
			return GameData.PROTOCOL_OP_FAILED;

		if (!this.members.containsKey(role.id))
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
	
		for (SBean.SectMapAttacker attacker : this.mapCurAttacker.values())
		{
			if (attacker.roleId == role.id)
				return GameData.PROTOCOL_OP_SECT_BEING_ATTACK_MAP;
		}
		
		this.sectDiySkillRemove(role.id);
		this.members.remove(role.id);
		this.setMemberPosition(role.id, Sect.SECT_MEMBER_OTHER);
		role.exitFromSect(false);
		gs.getTLogger().logSectEventFlow(this, TLog.ROLE_LEAVE_SECT);
		updateSectHistory(SBean.DBSectHistory.SectEventLeave, role.name, role.name, false, 0, 0);
		this.doSave();
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int appointMemberPositon(Role role, int memberId, int position) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember sectMember = this.members.get(memberId);
		if (sectMember == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;

		if (role.id == memberId)
			return GameData.PROTOCOL_OP_FAILED;
		int memberPosition = getMemberPosition(memberId);
		if (memberPosition == position)
			return GameData.PROTOCOL_OP_FAILED;
		
		int rolePosition = getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_SETJOB, position) || !this.checkAuth(rolePosition, Sect.SECT_AUTH_SETJOB, memberPosition))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		
		SBean.SectUpLevelCFGS upLevel = GameData.getInstance().getSectUpLevel(this.level);
		if(upLevel == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		int event = 0;
		switch (position)
		{
		case SECT_MEMBER_CHIEF:
			if(getMemberPosition(role.id) != SECT_MEMBER_CHIEF)
			{
				return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
			}
			if(getMemberPosition(memberId) != SECT_MEMBER_DEPUTY)
			{
				return GameData.PROTOCOL_OP_SECT_APPOINT_FAILED;
			}
			event = SBean.DBSectHistory.SectEventAsChief;
			break;
		case SECT_MEMBER_DEPUTY:
			if (this.deputy.size() >= upLevel.deputyCount)
				return GameData.PROTOCOL_OP_SECT_DEPUTY_FULL;
			event = SBean.DBSectHistory.SectEventAsDeputy;
			break;
		case SECT_MEMBER_ELDER:
			if (this.elder.size() >= upLevel.elderCount)
				return GameData.PROTOCOL_OP_SECT_ELDER_FULL;
			event = memberPosition > position ? SBean.DBSectHistory.SectEventUpElder : SBean.DBSectHistory.SectEventDownElder;
			break;
		case SECT_MEMBER_OTHER:
			event = SBean.DBSectHistory.SectEventAsMember;
			break;
		default:
			return GameData.PROTOCOL_OP_FAILED;
		}
		this.setMemberPosition(memberId, position);
		this.updateSectHistory(event, sectMember.data.role.name, role.name, false, 0, 0);
		//this.lastUpdateTime = GameTime.getTime();
		gs.getLoginManager().exeCommonRoleVisitor(memberId, false, new LoginManager.CommonRoleVisitor() 
		{
			@Override
			public boolean visit(Role role, Role sameUserRole) 
			{
				role.updateSectInfo(Sect.this.id, Sect.this.name, position, Sect.this.icon, Sect.this.level);
				return true;
			}
			@Override
			public void onCallback(boolean success) 
			{	
			}
		});

		//this.setMemberPosition(role.id, Sect.SECT_MEMBER_OTHER);
		if(position == SECT_MEMBER_CHIEF)
		{
			role.updateSectInfo(this.id, this.name, Sect.SECT_MEMBER_OTHER, this.icon, this.level);
		}
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int sectUpdateCreed(Role role, String creed)
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_CREED, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		this.creed = creed;
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	

	private void openSectSkill()
	{
		SBean.SectUpLevelCFGS upLevelCFGS = GameData.getInstance().getSectUpLevel(this.level);
		if(upLevelCFGS == null)
			return;

		// 开启技能
		upLevelCFGS.skillOpenLevel.stream().filter(skillId -> this.auras.get(skillId) == null).forEach(skillId -> {
			SBean.DBSectAura sectAura = new SBean.DBSectAura(skillId, 0, new TreeMap<>());
			this.auras.put(skillId, sectAura);
		});
	}
	
	public synchronized int upgradeSect(Role role) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.SectUpLevelCFGS upLevelCFGS = GameData.getInstance().getSectUpLevel(this.level+1);
		if (upLevelCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		int nowTime = GameTime.getTime();
		if (nowTime - this.lastUpgradeTime < upLevelCFGS.upTimes)
			return GameData.PROTOCOL_OP_FAILED;
		
		int rolePosition = getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_SECTUPLVL, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;

		
/*		int useUpLvlDiamond = GameData.getInstance().getCommonCFG().sect.sectUpLvlDiamond;
		synchronized (role) 
		{
			if (!role.canUseDiamond(useUpLvlDiamond, true))
				return GameData.PROTOCOL_OP_FAILED;
			
			role.useDiamond(useUpLvlDiamond, true);
		}
*/		
		this.lastUpgradeTime = nowTime;
		this.level += 1;
		openSectSkill();
	
		//升级帮派新增的帮派光环等级都为0级不需要同步出去
		
		updateSectHistory(SBean.DBSectHistory.SectEventSectUpLvl, "", role.name, false, this.level, 0);
		this.notifyMembersUpdateSectInfo();
		gs.getTLogger().logSectEventFlow(this, TLog.SECT_LEVEL_UP);
		//this.lastUpdateTime = GameTime.getTime();
		return this.lastUpgradeTime;
	}

	private void notifyMembersUpdateSectInfo()
	{
		for (int rid : this.members.keySet())
		{
			gs.getLoginManager().exeCommonRoleVisitor(rid, false, new LoginManager.CommonRoleVisitor() 
			{
				@Override
				public boolean visit(Role role, Role sameUserRole) 
				{
					role.updateSectInfo(Sect.this.id, Sect.this.name, Sect.this.getMemberPosition(rid), Sect.this.icon, Sect.this.level);
					return true;
				}
				@Override
				public void onCallback(boolean success) 
				{	
				}
			});
		}
	}

	public synchronized int accelerateUpgradeCooling(Role role, int accTime, int maxLevel) 
	{
		TLogger.TLogEvent tlogEvent = gs.getTLogger().createNewEvent(TLog.AT_ACCELERATE_UPGRADE_COOLING);
		tlogEvent.setArg(role.id, accTime, maxLevel);
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.level >= maxLevel)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = this.getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_SECTUPLVL, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		SBean.SectUpLevelCFGS upLevelCFGS = GameData.getInstance().getSectUpLevel(this.level + 1);
		if (upLevelCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		int nowTime = GameTime.getTime();
		int timeDifference = upLevelCFGS.upTimes - (nowTime - this.lastUpgradeTime);
		if (timeDifference <= 0 || timeDifference > accTime)
			return GameData.PROTOCOL_OP_FAILED;
		int useDiamond = (int) Math.ceil((accTime * upLevelCFGS.diamond) / 10000.f);
		synchronized (role) 
		{
			if (!role.canUseDiamond(useDiamond, true))
				return GameData.PROTOCOL_OP_FAILED;
			
			role.useDiamond(useDiamond, true, tlogEvent.getGameItemRecords());
		}
		
		this.lastUpgradeTime -= accTime;

		updateSectHistory(SBean.DBSectHistory.SectEventAccelerate, "", role.name, false, useDiamond, 0);
		//this.lastUpdateTime = GameTime.getTime();
		gs.getTLogger().logRoleEventFlow(role, tlogEvent);
		return this.lastUpgradeTime;
	}

	public synchronized int addSectAuraExp(Role role, int auraId, int itemId, int itemCount) 
	{
		TLogger.TLogEvent tlogEvent = gs.getTLogger().createNewEvent(TLog.AT_ADD_SECT_AURA_EXP);
		tlogEvent.setArg(role.id, auraId, itemId, itemCount);
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember sectMember = this.members.get(role.id);
		if (sectMember == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		
		SBean.DBSectAura sectAura = this.auras.get(auraId);
		if (sectAura == null)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.SectSkillCFGS sectSkill = GameData.getInstance().getSectSkillCFGS(auraId, sectAura.level + 1);
		if (sectSkill == null)
			return GameData.PROTOCOL_OP_FAILED;
		if(this.level < sectSkill.sectLvl)
			return GameData.PROTOCOL_OP_SECT_SECT_LEVEL_NEED;
		
		SBean.SectItemCFGS sectItem = sectSkill.items.get(itemId);
		if (sectItem == null || itemCount <= 0)
			return GameData.PROTOCOL_OP_FAILED;
		Integer curItemCount = sectAura.items.get(itemId);
		if (curItemCount == null)
			curItemCount = 0;
		int canAddItem = sectItem.count - curItemCount;
		if (canAddItem <= 0)
			return GameData.PROTOCOL_OP_FAILED;

		int realAddItemCount = canAddItem >= itemCount ? itemCount : canAddItem;
		synchronized (role)
		{
			if (!role.containsEnoughGameItem(itemId, realAddItemCount))
				return GameData.PROTOCOL_OP_FAILED;
			role.delGameItem(itemId, realAddItemCount, tlogEvent.getGameItemRecords());
			role.syncAddSectContribution(sectItem.value * realAddItemCount, tlogEvent.getGameItemRecords());
		}
		//sectMember.data.stats.contributionTotal += sectItem.value * realAddItemCount;

		// 加物品,加帮贡
		sectAura.items.put(itemId, curItemCount + realAddItemCount);
		
		updateSectHistory(SBean.DBSectHistory.SectEventAuraAddExp, "", role.name, false, itemId, 0);

		boolean isUpLevel = true;
		for (SBean.SectItemCFGS entry : sectSkill.items.values()) 
		{
			Integer tmpCount = sectAura.items.get(entry.id);
			if (tmpCount == null || tmpCount < entry.count) {
				isUpLevel = false;
				break;
			}
		}

		if (isUpLevel) 
		{
			sectAura.level += 1;
			sectAura.items.clear();
			for (int rid : this.members.keySet())
			{
				Role memberRole = gs.getLoginManager().getOnGameRole(rid);
				if (memberRole != null)
				{
					memberRole.updateSectAura(sectAura.id, sectAura.level);	
				}
			}
			//是否开启帮派任务
			/*synchronized (role) {
				role.checkSectTaskIsOpen(sectAura.level);
			}*/
			updateSectHistory(SBean.DBSectHistory.SectEventSkillUpLvl, sectMember.data.role.name, role.name, false, sectAura.level, auraId);
		}
		//this.lastUpdateTime = GameTime.getTime();
		
		gs.getTLogger().logRoleEventFlow(role, tlogEvent);
		return realAddItemCount;
	}
	
	public synchronized void roleAddSectContribution(int roleId, int contribution)
	{
		this.members.get(roleId).data.stats.contributionTotal += contribution;
	}
	
	public synchronized Role.RpcRes<List<SBean.DBSectAura>> getSectAura(Role role)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(role.id))
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		return new Role.RpcRes<>(this.getSectAuraListWithoutLock());
	}

	public synchronized int worshipSectMember(Role role, int memberId, int type) 
	{
		this.dayRefresh();
		Role worshipRole = gs.getLoginManager().getOnGameRole(memberId);
		if (worshipRole == null)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember operatorMember = this.members.get(role.id);
		SBean.DBSectMember worshipMember = this.members.get(memberId);
		SBean.SectWorshipCFGS worshipCFGS = GameData.getInstance().getSectWorshipCFGSByVipLevel(worshipRole.getUseableVipLvl());
		if (operatorMember == null || worshipMember == null || worshipCFGS == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		
		int contributionAdd = role.tryWorshipSectMember(type, worshipMember.data.role.level);
		if (contributionAdd <= 0)
			return contributionAdd;
		
		if(worshipMember.worshiped.dayWorshipedTimes < worshipCFGS.maxWorshipedTimes)
		{
			worshipMember.worshiped.dayWorshipedTimes += 1;
			worshipMember.worshiped.worshipReward += contributionAdd;
		}
		
		Role onlinerole = gs.getLoginManager().getOnGameRole(memberId);
		if (onlinerole != null)
			gs.getRPCManager().sendStrPacket(onlinerole.netsid, new SBean.sect_notice_worship());
		updateSectHistory(SBean.DBSectHistory.SectEventWorship, worshipMember.data.role.name, role.name, false, type, 0);
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized RpcRes<SBean.SectWorshipedData> syncWorshipReward(Role role)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		
		SBean.DBSectMember sectMember = this.members.get(role.id);
		if (sectMember == null)
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		
		return new RpcRes<>(sectMember.worshiped.kdClone());
	}
	
	public synchronized int takeWorShipReward(Role role)
	{
		TLogger.TLogEvent tlogEvent = gs.getTLogger().createNewEvent(TLog.AT_TAKE_WORSHIP_REWARD);
		tlogEvent.setArg(role.id);
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.DBSectMember sectMember = this.members.get(role.id);
		if (sectMember == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		role.syncAddSectContribution(sectMember.worshiped.worshipReward, tlogEvent.getGameItemRecords());
		sectMember.worshiped.worshipReward = 0;
		gs.getTLogger().logRoleEventFlow(role, tlogEvent);
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public void testSectBanquetNotice(Role role)
	{
		this.dayRefresh();
		if (this.dismissed)
			return;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return;
		for (SBean.SectBanquet e : this.banquets.values())
		{
			if (!e.roles.contains(role.id))
			{
				SBean.SectBanquetCFGS banquetCFGS = GameData.getInstance().getSectBanquetCFGS(e.type);
				if (e.roles.size() < banquetCFGS.total && GameTime.getTime() - e.openTime < banquetCFGS.time)
				{
					role.testSectBanquetNotice();
					return;
				}
			}
		}
	}

	public synchronized int openSectBanquet(Role role, int type) 
	{	
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		SBean.SectBanquetCFGS banquetCFGS = GameData.getInstance().getSectBanquetCFGS(type);
		if (banquetCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		if (!role.tryOpenSectBanquet(banquetCFGS))
			return GameData.PROTOCOL_OP_FAILED;

		SBean.SectBanquet selfBanquet = new SBean.SectBanquet(nextBanquetID.incrementAndGet(), role.id, role.name, type, GameTime.getTime(), new TreeSet<>());
		this.banquets.put(selfBanquet.bid, selfBanquet);
		updateSectHistory(SBean.DBSectHistory.SectEventBanquet, "", role.name, true, type, 0);
		//this.lastUpdateTime = GameTime.getTime();
		
		for (int rid : this.members.keySet())
		{
			Role memberRole = gs.getLoginManager().getOnGameRole(rid);
			if (memberRole != null)
			{
				memberRole.testSectBanquetNotice();	
			}
		}
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized RpcRes<List<SBean.SectBanquet>> getBanquets() 
	{
		this.dayRefresh();
		if (this.dismissed)
			return new RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		
		List<SBean.SectBanquet> banquetList = new ArrayList<>();
		for (SBean.SectBanquet e : this.banquets.values())
		{
			banquetList.add(e.kdClone());
		}
		return new RpcRes<>(banquetList);
	}

	public synchronized int joinBanquet(Role role, int bId) 
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;

		SBean.SectBanquet banquet = this.banquets.get(bId);
		if (banquet == null)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember member = this.members.get(role.id);
		if (member == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;

		SBean.SectBanquetCFGS banquetCFGS = GameData.getInstance().getSectBanquetCFGS(banquet.type);
		if (banquetCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		if (banquet.openTime + banquetCFGS.time < GameTime.getTime())
			return GameData.PROTOCOL_OP_SECT_BANQUET_CD;
		if (banquet.roles.contains(role.id))
			return GameData.PROTOCOL_OP_SECT_BANQUET_EXIST;
		//宴席能被几个人吃
		if(banquet.roles.size() >= banquetCFGS.total)
			return GameData.PROTOCOL_OP_SECT_BANQUET_FULL;
		
		int result = role.tryJoinSectBanquet(banquetCFGS);
		if (result > 0)
		{
			banquet.roles.add(role.id);
			updateSectHistory(SBean.DBSectHistory.SectEventJoinBanquet, banquet.openRoleName, role.name, false, banquet.type, 0);	
		}
		return result;
	}

	public synchronized int openSectMap(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = this.getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_OPEN_MAPCOPY, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		
		SBean.SectMapCFGS sectMapCFGS = GameData.getInstance().getSectMapCFGS(mapId);
		if(sectMapCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		if (this.level < GameData.getInstance().getCommonCFG().sect.mapOpenLvl)
			return GameData.PROTOCOL_OP_FAILED;
		
		int daySecond = GameTime.getSecondOfDay();
		if (daySecond < sectMapCFGS.startTime || daySecond > sectMapCFGS.endTime)
			return GameData.PROTOCOL_OP_FAILED;
		
		if(sectMapCFGS.preMapId != -1 && !this.logFinishedMaps.containsKey(sectMapCFGS.preMapId))
			return GameData.PROTOCOL_OP_SECT_MAP_PRE_MAP_NEED;
		
		Integer resetTimes = this.dayResetMapTimes.get(mapId);
		resetTimes = resetTimes == null ? 0 : resetTimes;
		if (resetTimes >= sectMapCFGS.resetTimes)
			return GameData.PROTOCOL_OP_FAILED;
		
		if (this.mapCurAttacker.containsKey(mapId))
			return GameData.PROTOCOL_OP_SECT_BEING_ATTACK_MAP;
		
		if (!this.canUseVit(sectMapCFGS.openCostVit))
			return GameData.PROTOCOL_OP_FAILED;
		
		this.useVit(sectMapCFGS.openCostVit);
		SBean.SectMapData sectMapData = new SBean.SectMapData(mapId, GameTime.getTime(), new TreeMap<>(), new TreeMap<>(), new TreeMap<>());
		this.openedMaps.put(sectMapData.id, sectMapData);
		this.dayResetMapTimes.put(mapId, resetTimes+1);
		
		gs.getTLogger().logSectMapFlow(this, GameData.getInstance().getSectMapLevel(mapId), mapId, TLog.SECT_MAP_PERSON, TLog.SECT_ACT_START);
		updateSectHistory(SBean.DBSectHistory.SectEventResetMap, "", role.name, true, mapId, 0);
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int openSectGroupMap(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = this.getMemberPosition(role.id);
		if (!this.checkAuth(rolePosition, Sect.SECT_AUTH_OPEN_MAPCOPY, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		SBean.SectGroupMapCFGS sectGroupMapCFGS = GameData.getInstance().getSectGroupMapCFGS(mapId);
		if (sectGroupMapCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.groupMapCurInfo.curMapId != 0)
			return GameData.PROTOCOL_OP_SECT_EARLY_START_GROUP_MAP;
		if (!role.containsEnoughGameItem(sectGroupMapCFGS.openCostId, sectGroupMapCFGS.openCostCount))
			return GameData.PROTOCOL_OP_SECT_GROUP_MAP_COST_NEED;
		int memberNum = 0;
		for (SBean.DBSectMember member : this.members.values())
			if (member.data.role.level >= sectGroupMapCFGS.memberLevelNeed && gs.getLoginManager().getOnGameRole(member.data.role.id) != null)
				memberNum++;
		if (memberNum < sectGroupMapCFGS.memberNumNeed)
			return GameData.PROTOCOL_OP_SECT_GROUP_MAP_MEMBER_NEED;
		if (sectGroupMapCFGS.preMapId != -1 && (!this.groupMapData.containsKey(sectGroupMapCFGS.preMapId) || this.groupMapData.get(sectGroupMapCFGS.preMapId).lastEndTime == 0))
			return GameData.PROTOCOL_OP_SECT_MAP_PRE_MAP_NEED;
		SBean.DBSectGroupMapData mapData = this.groupMapData.get(mapId);
		if (mapData!=null && mapData.lastEndTime + sectGroupMapCFGS.resetCD > GameTime.getTime())
			return GameData.PROTOCOL_OP_SECT_GROUP_MAP_CD;
		if (mapData!=null && mapData.lastStartTime > mapData.lastEndTime)
			return GameData.PROTOCOL_OP_SECT_GROUP_MAP_NOT_END;
		TLogger.TLogEvent tlogEvent = gs.getTLogger().createNewEvent(TLog.AT_OPEN_SECT_GROUP_MAP);
		role.delGameItem(sectGroupMapCFGS.openCostId, sectGroupMapCFGS.openCostCount, tlogEvent.getGameItemRecords());
		gs.getTLogger().logRoleEventFlow(role, tlogEvent);
		this.groupMapData.merge(mapId, new SBean.DBSectGroupMapData(GameTime.getTime(), 0, (byte) 0, new HashMap<>(), new HashMap<>(), new HashMap<>()), (ov, nv) -> new SBean.DBSectGroupMapData(nv.lastStartTime, ov.lastEndTime, ov.isfinish, nv.killNum, nv.monsterProcess, nv.damageRank));
		this.groupMapCurInfo.curMapId = mapId;
		this.groupMapCurInfo.curInstance = gs.getMapService().createSectGroupMapCopy(mapId, this.id, GameTime.getTime(), new HashMap<>(), new HashMap<>(), this.toRoleOverviewDamageRank(new HashMap<>()));
		gs.getTLogger().logSectMapFlow(this, GameData.getInstance().getSectGroupMapLevel(mapId), mapId, TLog.SECT_MAP_GROUP, TLog.SECT_ACT_START);
		updateSectHistory(SBean.DBSectHistory.SectEventResetGroupMap, "", role.name, true, mapId, 0);
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	
	
	public RpcRes<Map<Integer, Integer>> querySectMapsStatus(Role role)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(role.id))
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		Map<Integer, Integer> maps = new TreeMap<>();
		for (int mid : this.logFinishedMaps.keySet())
		{
			maps.put(mid, 1);
		}
		for (SBean.SectMapData sectMapData : this.openedMaps.values())
		{
			SBean.SectMapCFGS sectMapCFGS = GameData.getInstance().getSectMapCFGS(sectMapData.id);
			if(sectMapCFGS != null)
			{
				Integer tenThousandBossHp = sectMapData.progress.get(sectMapCFGS.boss);
				tenThousandBossHp =  tenThousandBossHp == null ? 0 : tenThousandBossHp;
				if (tenThousandBossHp < 10000)
				{
					maps.compute(sectMapData.id, (k,v)-> v == null ? 0x02 : v | 0x02);
				}
			}
		}
		return new RpcRes<>(maps);
	}
	
	public synchronized RpcRes<SBean.SectMapStatus> querySectMap(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(role.id))
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		
		SBean.SectMapCFGS sectMapCFGS = GameData.getInstance().getSectMapCFGS(mapId);
		if(sectMapCFGS == null)
			return new RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		
		SBean.SectMapData sectMapData = this.openedMaps.get(mapId);
		
		//Integer tenThousandBossHp = (sectMapLog == null) ? -1 : (sectMapLog.progress.get(sectMapCFGS.boss));//-1帮派副本从未开启过
		Integer tenThousandBossHp = -1;
		if (sectMapData != null)
			tenThousandBossHp = sectMapData.progress.get(sectMapCFGS.boss);
		tenThousandBossHp =  tenThousandBossHp == null ? 0 : tenThousandBossHp;
		
		Integer dayResetTimes = this.dayResetMapTimes.get(mapId);
		dayResetTimes = dayResetTimes == null ? 0 : dayResetTimes;
		return new RpcRes<>(GameData.PROTOCOL_OP_SUCCESS, new SBean.SectMapStatus(tenThousandBossHp, dayResetTimes));
	}
	
	public synchronized int startSectMap(Role role, int mapId)
	{	
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.SectMapCFGS sectMapCFGS = GameData.getInstance().getSectMapCFGS(mapId);
		if(sectMapCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.SectMapData sectMapData = this.openedMaps.get(mapId);
		if(sectMapData == null)
			return GameData.PROTOCOL_OP_FAILED;

		//是否在开放时间段内
		int daySecond = GameTime.getSecondOfDay();
		if (daySecond < sectMapCFGS.startTime || daySecond > sectMapCFGS.endTime)
			return GameData.PROTOCOL_OP_SECT_MAP_NOT_IN_TIME;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		if(this.mapCurAttacker.containsKey(mapId))
			return GameData.PROTOCOL_OP_SECT_BEING_ATTACK_MAP;
		
		Integer lastLostHp = sectMapData.progress.get(sectMapCFGS.boss);
		if (lastLostHp == null)
			lastLostHp = 0;
		
		if (lastLostHp >= 10000)
			return GameData.PROTOCOL_OP_SECT_MAP_END;
		
		if (!role.startSectMapCopy(mapId, this.id, new TreeMap<>(sectMapData.progress)))
			return GameData.PROTOCOL_OP_FAILED;
		
		this.mapCurAttacker.put(mapId, new SBean.SectMapAttacker(role.id, 0, lastLostHp, lastLostHp, 0, 0, 0, 0));
		
		updateSectHistory(SBean.DBSectHistory.SectEventFightMap, "", role.name, false, mapId, 0);
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int startSectGroupMap(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectGroupMapData sectMapData = this.groupMapData.get(mapId);
		if (sectMapData == null || this.groupMapCurInfo.curMapId != mapId)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.groupMapCurInfo.curMapId != 0 && this.groupMapCurInfo.curMapId != mapId)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.groupMapCurInfo.curInstance == 0 && sectMapData.lastStartTime > sectMapData.lastEndTime)
		{
			this.groupMapCurInfo.curMapId = mapId;
			this.groupMapCurInfo.curInstance = gs.getMapService().createSectGroupMapCopy(mapId, this.id, sectMapData.lastStartTime, sectMapData.monsterProcess, sectMapData.killNum, this.toRoleOverviewDamageRank(sectMapData.damageRank));
			if (this.groupMapCurInfo.curInstance == 0)
				return GameData.PROTOCOL_OP_FAILED;
		}
		SBean.SectGroupMapCFGS sectMapCFGS = GameData.getInstance().getSectGroupMapCFGS(mapId);
		if (sectMapCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;

		if (!role.startSectGroupMapCopy(mapId, this.groupMapCurInfo.curInstance, sectMapData.lastStartTime))
			return GameData.PROTOCOL_OP_FAILED;
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	//中断离开
	public synchronized int leaveSectMap(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		//离开的时候不能判断是否还在帮派，否则玩家无法离开
		if (!this.members.containsKey(role.id))
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		
		SBean.SectMapCFGS cfg = GameData.getInstance().getSectMapCFGS(mapId);
		if(cfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.SectMapData sectMapData = this.openedMaps.get(mapId);
		if(sectMapData == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.SectMapAttacker attacker = this.mapCurAttacker.get(mapId);
		if (attacker == null || attacker.roleId != role.id)
			return GameData.PROTOCOL_OP_FAILED;
		
		//主动离开，如未结算则进行结算，但不弹Role自身的结算框
		balanceSectMap(cfg, sectMapData, attacker);
		
		this.mapCurAttacker.remove(mapId);
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized int applySectMapRewards(Role role, int mapId, int rewardId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		if (!this.members.containsKey(role.id))
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		
		SBean.SectMapCFGS cfg = GameData.getInstance().getSectMapCFGS(mapId);
		if (cfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		if (!GameData.testSectMapContainsReward(cfg, rewardId))
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.SectMapData sectMapData = this.openedMaps.get(mapId);
		if(sectMapData == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.SectMapRewards sectMapRewards = this.mapRewards.get(mapId);
		if (sectMapRewards == null)
			sectMapRewards = new SBean.SectMapRewards(new TreeMap<>());
		
		SBean.SectMapRewardRecord record = sectMapRewards.rewards.get(rewardId);
		if (record != null && record.applicants.contains(role.id))
			return GameData.PROTOCOL_OP_FAILED;
		
		Iterator<Map.Entry<Integer, SBean.SectMapRewardRecord>> it = sectMapRewards.rewards.entrySet().iterator();
		while (it.hasNext())
		{
			SBean.SectMapRewardRecord e = it.next().getValue();
			e.applicants.remove(Integer.valueOf(role.id));
//			if (e.applicants.isEmpty() && e.count == 0)
//				it.remove();
		}
		
		if (record == null)
		{
			record = new SBean.SectMapRewardRecord(rewardId, 0, new ArrayList<>());
			sectMapRewards.rewards.put(rewardId, record);
		}
		record.applicants.add(role.id);
		this.mapRewards.put(mapId, sectMapRewards);
		
		//this.lastUpdateTime = GameTime.getTime();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	
	
	public synchronized Role.RpcRes<SBean.SectMapAllocation> getSectMapAllocation(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(role.id))
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		
		SBean.SectMapRewards sectMapRewards = this.mapRewards.get(mapId);
		if(sectMapRewards == null)
			sectMapRewards = new SBean.SectMapRewards(new TreeMap<>());
		
		
		Map<Integer, SBean.RoleOverview> members = new TreeMap<>();
		for (SBean.SectMapRewardRecord e : sectMapRewards.rewards.values())
		{
			e.applicants.stream().filter(rid -> !members.containsKey(rid)).forEach(rid -> {
				SBean.DBSectMember member = this.members.get(rid);
				if (member != null)
					members.put(rid, member.data.role.kdClone());
			});
		}
	
		return new Role.RpcRes<>(new SBean.SectMapAllocation(Stream.clone(sectMapRewards.rewards), members));
	}
	
	
	
	public synchronized Role.RpcRes<SBean.SectMapDamage> getSectMapDamage(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(role.id))
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		
		SBean.SectMapData sectMapData = this.openedMaps.get(mapId);
		if(sectMapData == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		
		Map<Integer, Integer> accDamage = new TreeMap<>(sectMapData.accDamage);
		Map<Integer, Integer> maxDamage = new TreeMap<>(sectMapData.maxDamage);
		Map<Integer, SBean.RoleOverview> members = new TreeMap<>();
		accDamage.keySet().stream().filter(rid -> !members.containsKey(rid)).forEach(rid -> {
			SBean.DBSectMember member = this.members.get(rid);
			if (member != null)
				members.put(rid, member.data.role.kdClone());
		});
		maxDamage.keySet().stream().filter(rid -> !members.containsKey(rid)).forEach(rid -> {
			SBean.DBSectMember member = this.members.get(rid);
			if (member != null)
				members.put(rid, member.data.role.kdClone());
		});

		return new Role.RpcRes<>(new SBean.SectMapDamage(accDamage, maxDamage, members));
	}
	
	
	
	public synchronized Role.RpcRes<SBean.SectMapInfo> getSectMapDetail(Role role, int mapId)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(role.id))
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_JOIN);
		
		SBean.SectMapData sectMapData = this.openedMaps.get(mapId);
		if(sectMapData == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		
		SBean.SectMapAttacker attacker = this.mapCurAttacker.get(mapId);
		SBean.RoleOverview roleOverview = null;
		if (attacker != null)
		{
			SBean.DBSectMember member = this.members.get(attacker.roleId);
			if (member != null)
			{
				roleOverview = member.data.role.kdClone();
			}
		}

		return new Role.RpcRes<>(new SBean.SectMapInfo(new TreeMap<>(sectMapData.progress), roleOverview));
	}
	
	
	
	public synchronized Role.RpcRes<List<SBean.SectMapRewardsLog>> getSectMapRewardsLog(Role role)
	{
		this.dayRefresh();
		if (this.dismissed)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_FAILED);
		if (!this.members.containsKey(role.id))
			return null;
		
		return new Role.RpcRes<>(Stream.clone(this.mapRewardsLog));
	}
	
	public synchronized SBean.SectMapAttacker syncSectMapProgress(int mapId, int spawnPointId, int progress, int damage)
	{
		this.dayRefresh();
		if (this.dismissed)
			return null;
		SBean.SectMapData sectMapData = this.openedMaps.get(mapId);
		if(sectMapData == null)
			return null;
		SBean.SectMapAttacker attacker = this.mapCurAttacker.get(mapId);
		if (attacker == null)
			return null;
		
		SBean.SectMapCFGS cfg = GameData.getInstance().getSectMapCFGS(mapId);
		if (cfg == null)
			return null;
		
		if (spawnPointId == 0)//role死亡
		{
			balanceSectMap(cfg, sectMapData, attacker);
			return attacker;
		}
		else if (spawnPointId < 0)//超时结束
		{
			balanceSectMap(cfg, sectMapData, attacker);
			return attacker;
		}
		else//怪掉血
		{
			Integer lastLostHp = sectMapData.progress.get(spawnPointId);
			if (lastLostHp == null)
				lastLostHp = 0;
			if (progress > lastLostHp)
			{
				sectMapData.progress.put(spawnPointId, progress);
				if (spawnPointId == cfg.boss)
				{
					attacker.accDamage += damage;
					if (progress >= 10000)
					{
						balanceSectMap(cfg, sectMapData, attacker);
						this.logFinishedMaps.compute(mapId, (k,v)-> v == null ? 1 : v + 1);
						gs.getTLogger().logSectMapFlow(this, GameData.getInstance().getSectMapLevel(mapId), mapId, TLog.SECT_MAP_PERSON, TLog.SECT_ACT_FINISH);
						return attacker;
					}
				}
			}
		}
		
		//this.lastUpdateTime = GameTime.getTime();
		return null;
	}
	
	private int getExtraReward(SBean.SectMapCFGS cfg, int beforeProgress, int curProgress)
	{
		int exReward = 0;
		for (Map.Entry<Integer, Integer> e : cfg.extraRewards.entrySet())
		{
			int rewardProgress = e.getKey();
			if (beforeProgress < rewardProgress && curProgress >= rewardProgress)
			{
				exReward += e.getValue();
			}
		}
		return exReward;
	}
	
	private void calcProgressReward(SBean.SectMapCFGS cfg, int beforeProgress, int curProgress)
	{
		SBean.SectMapRewards rewards = this.mapRewards.compute(cfg.id, (k,v)-> v == null ? new SBean.SectMapRewards(new TreeMap<>()) : v);
		for (Map.Entry<Integer, SBean.ProgressRewards> e : cfg.progressRewards.entrySet())
		{
			int rewardProgress = e.getKey();
			if (beforeProgress < rewardProgress && curProgress >= rewardProgress)
			{
				for (Map.Entry<Integer, Integer> ee : e.getValue().rewards.entrySet())
				{
					int rewardId = ee.getKey();
					int rewardCnt = ee.getValue();
					rewards.rewards.compute(rewardId, (k, v)->{
						if (v == null)
							v = new SBean.SectMapRewardRecord(rewardId, 0, new ArrayList<>());
						v.count += rewardCnt;
						return v;
					});
				}
			}
		}
	}
	
//	public synchronized boolean syncSectMapEnd(int mapId)
//	{
//		if (this.dismissed)
//			return false;
//		SBean.SectMapLog sectMap = this.openedMaps.get(mapId);
//		if(sectMap == null)
//			return false;
//		SBean.SectMapAttacker attacker = this.mapCurAttacker.get(mapId);
//		if (attacker == null)
//			return false;
//		
//		SBean.SectMapCFGS cfg = GameData.getInstance().getSectMapCFGS(mapId);
//		if (cfg == null)
//			return false;
//		Role role = gs.getLoginManager().getOnGameRole(attacker.roleId);
//		balanceSectMap(cfg, sectMap, attacker, role);
//		//this.lastUpdateTime = GameTime.getTime();
//		return true;
//	}
	
	private void balanceSectMap(SBean.SectMapCFGS cfg, SBean.SectMapData sectMapData, SBean.SectMapAttacker attacker)
	{
		if (attacker.endProgressTime == 0)
		{
			Integer lastLostHp = sectMapData.progress.get(cfg.boss);
			if (lastLostHp == null)
				lastLostHp = 0;
			attacker.endProgress = lastLostHp;
			int exReward = getExtraReward(cfg, attacker.startProgress, lastLostHp);
			attacker.exReward = exReward;
			calcProgressReward(cfg, attacker.startProgress, lastLostHp);
			int accDamage = sectMapData.accDamage.compute(attacker.roleId, (k ,v)-> v == null ? attacker.accDamage : v + attacker.accDamage);
			int maxDamage = sectMapData.maxDamage.compute(attacker.roleId, (k ,v)-> v == null ? attacker.accDamage : (attacker.accDamage > v ? attacker.accDamage : v));
			int accRank = 1;
			for (Map.Entry<Integer, Integer> e : sectMapData.accDamage.entrySet())
			{
				if (e.getKey() != attacker.roleId && e.getValue() > accDamage)
				{
					accRank += 1;
				}
			}
			int maxRank = 1;
			for (Map.Entry<Integer, Integer> e : sectMapData.maxDamage.entrySet())
			{
				if (e.getKey() != attacker.roleId && e.getValue() > maxDamage)
				{
					maxRank += 1;
				}
			}
			attacker.accDamageRank = accRank;
			attacker.maxDamageRank = maxRank;
			attacker.endProgressTime = GameTime.getTime();	
		}
	}
	
	public synchronized SBean.SectSelfTaskInfo syncSectSelfTask(Role role)
	{
		if (this.dismissed)
			return null;
		if (!this.members.containsKey(role.id))
			return null;
		if (this.level < GameData.getInstance().getCommonCFG().sect.taskOpenLvl)
			return null;
		return role.syncSectSelfTaskImpl();
	}
	
	public synchronized boolean syncSectSharedTask(Role role)
	{
		if (this.dismissed)
			return false;
		if (!this.members.containsKey(role.id))
			return false;
		
		synchronized (role) 
		{
			role.tryRefreshSectTasksLib();
			List<SBean.SectSharedTask> alltasks = new ArrayList<>();
			//不显示自己共享
			//并排除自己已接
			//如果共享任务完成不显示在列表里， 共享任务只能被接受一次
			//还能被接取
			this.shareTaskLib.values().stream().filter(e -> e.ownerId != role.id).forEach(e -> {
				List<SBean.SectTask> tasks = new ArrayList<>();
				for (SBean.SectRoleSharedTask ee : e.tasks.values())
				{
					//并排除自己已接
					if (ee.task.sid == role.sectData.task.curTask.task.sid)
						continue;
					//如果共享任务完成不显示在列表里， 共享任务只能被接受一次
					SBean.SectFinishedSharedTask sharedTask = role.sectData.task.finishedSharedTasks.get(e.ownerId);
					if (sharedTask != null && sharedTask.tasks.contains(ee.task.sid))
						continue;

					if (ee.receivedByTimes < 3)//还能被接取
						tasks.add(ee.task.kdClone());
				}
				if (!tasks.isEmpty())
					alltasks.add(new SBean.SectSharedTask(e.ownerId, e.ownerName, tasks));
			});
			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.sect_share_task_sync_start(role.sectData.task.stCancelTime));
			final int BATCH_SIZE = 10; 
			int start = 0;
			while (start < alltasks.size())
			{
				int end = start + BATCH_SIZE;
				if (end > alltasks.size())
					end = alltasks.size();
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.sect_share_task_sync_info(alltasks.subList(start, end)));
				start = end;
			}
			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.sect_share_task_sync_end());
		}
		return true;
	}
	
	public synchronized SBean.SectFinishedTaskRes syncSectFinishedSelfTask(Role role, List<Integer> shared)
	{
		if (this.dismissed)
			return null;
		if (!this.members.containsKey(role.id))
			return null;
		Map<Integer, Integer> shareTaskRemainCount = new TreeMap<>();		//剩余完成次数
		SBean.SectRoleSharedTasks sharedTasks = shareTaskLib.get(role.id);
		if(sharedTasks != null)
		{
			for(Integer sId : shared){
				SBean.SectRoleSharedTask sectTask = sharedTasks.tasks.get(sId);
				if(sectTask == null)
					continue;
				shareTaskRemainCount.put(sId, GameData.getInstance().getCommonCFG().sect.taskShareMakeCount - sectTask.receivedByTimes);
			}
		}
		
		return role.getFinishedSectSelfTasks(shareTaskRemainCount);
	}
	
	
	public synchronized int sectTaskReceive(Role role, int ownerId, int sid)
	{
		if (this.dismissed)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return GameData.PROTOCOL_OP_SECT_NOT_JOIN;
		
		String ownerMember = "";
		SBean.SectRoleSharedTask sharedTask = null;
		int newTaskId = 0;
		if(ownerId != role.id){  //检查共享任务库
			SBean.SectRoleSharedTasks tSharedTasks = shareTaskLib.get(ownerId);
			if(tSharedTasks == null)
				return GameData.PROTOCOL_OP_SECT_SHARE_TASK_UN_USEABLE;
			sharedTask = tSharedTasks.tasks.get(sid);
			if(sharedTask == null)
				return GameData.PROTOCOL_OP_SECT_SHARE_TASK_UN_USEABLE;
			int times = GameData.getInstance().getCommonCFG().sect.taskShareMakeCount;
			if(sharedTask.receivedByTimes >= times)
				return GameData.PROTOCOL_OP_SECT_SHARE_TASK_TIME_MAX;
			newTaskId = sharedTask.task.taskId;
			ownerMember = tSharedTasks.ownerName;
		}
		int result = role.sectTaskReceiveCB(ownerId, sid, newTaskId, ownerMember);
		if(result > 0 && ownerId != role.id){
			sharedTask.receivedByTimes += 1;
		}
		
		return result;
	}
	
	
	public synchronized int sectTaskCancel(Role role, int ownerId, int sid)
	{
		if (this.dismissed)
			return 0;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return 0;
		SBean.SectRoleSharedTask sharedTask = null;
		if(ownerId != role.id){  //检查共享任务库
			SBean.SectRoleSharedTasks tSharedTasks = shareTaskLib.get(ownerId);
			if(tSharedTasks == null)
				return 0;
			sharedTask = tSharedTasks.tasks.get(sid);
			if(sharedTask == null)
				return 0;
		}
		int result = role.sectTaskCancelCB(ownerId, sid);
		if(result > 0 && ownerId != role.id){
			sharedTask.receivedByTimes -= 1;
		}
		return result;
	}
	
	
	public synchronized boolean sectTaskFinish(Role role, int ownerId, int sid)
	{
		if (this.dismissed)
			return false;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return false;

		SBean.SectRoleSharedTask sharedTask = null;
		if (ownerId != role.id)
		{ //检查共享任务库
			SBean.SectRoleSharedTasks tSharedTasks = shareTaskLib.get(ownerId);
			if (tSharedTasks == null)
				return false;
			sharedTask = tSharedTasks.tasks.get(sid);
			if (sharedTask == null)
				return false;
		}

		int contribution = role.sectTaskFinishCB(ownerId, sid);
		if (contribution <= 0)
			return false;

		if (ownerId != role.id)
		{
			sharedTask.leftRewardTimes += 1;
			Role ownerRole = gs.getLoginManager().getOnGameRole(ownerId);
			if (ownerRole != null && this.members.containsKey(ownerId))
				ownerRole.notifySectSharedTaskReward();
				
		}
		role.sectData.dayFinishTime++;
		return true;
	}
	
	
	
	public synchronized boolean sectTaskIssuanceShare(Role role, int sid, int taskId){
		if (this.dismissed)
			return false;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return false;
		
		boolean result = false;
		SBean.SectRoleSharedTasks tSharedTasks = shareTaskLib.get(role.id);
		if(tSharedTasks == null){
			tSharedTasks = new SBean.SectRoleSharedTasks(role.id, role.name, new TreeMap<>());
			shareTaskLib.put(role.id, tSharedTasks);
		}
		
		result = role.sectTaskIssuanceShareCB(sid);
		if(result){
			tSharedTasks.tasks.put(sid, new SBean.SectRoleSharedTask(new SBean.SectTask(sid, taskId), GameTime.getTime(), 0, 0));
		}
		return result;
	}
	
	
	public synchronized List<SBean.SectTask> sectTaskReset(Role role){
		if (this.dismissed)
			return null;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return null;
		if(this.level < 1)
			return null;
		
		List<SBean.SectTask> result = null;
		result = role.sectTaskResetCB();
		return result;
	}
	
	
	
	public synchronized SBean.SectTaskReward sectTaskShareDoneRewards(Role role){
		
		if (this.dismissed)
			return null;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return null;
		SBean.SectRoleSharedTasks sharedTasks = this.shareTaskLib.get(role.id);
		if(sharedTasks == null)
			return null;
		
		Map<Integer, Integer> taskCount = new TreeMap<>();
		List<Integer> ids = new ArrayList<Integer>();
		for(Map.Entry<Integer, SBean.SectRoleSharedTask> entry : sharedTasks.tasks.entrySet()){
			SBean.SectRoleSharedTask shareTask = entry.getValue();
			if(shareTask.leftRewardTimes <= 0)
				continue;			
			taskCount.put(shareTask.task.taskId, shareTask.leftRewardTimes);
			ids.add(entry.getKey());
		}
		if(taskCount.size() <= 0)
			return null;
		
		SBean.SectTaskReward sectTaskReward = null;
		sectTaskReward = role.sectTaskShareDoneRewardsCB(taskCount);
		if(sectTaskReward == null)
			return null;
		
		for(int id : ids)
		{
			SBean.SectRoleSharedTask shareTask = sharedTasks.tasks.get(id);
			shareTask.leftRewardTimes = 0;
		}
		
		sectTaskReward.tasks.putAll(taskCount);
		return sectTaskReward;
	}
	
	
	public synchronized void changeSectName(Role role, String name, SectRenameCallback callback)
	{
		if (this.dismissed)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		if (this.name.equals(name))
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		int rolePosition = this.getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_SECT_NAME, 0))
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED);
			return;
		}
		synchronized (role)
		{
			if (!GameData.getInstance().checkInputStrValid(name, GameData.getInstance().getCommonCFG().input.maxSectNameLength, true))
			{
				callback.onCallback(GameData.PROTOCOL_OP_SECT_CREATE_NAME_INVALID);
				return;
			}
			if (!role.canUseDiamond(GameData.getInstance().getCommonCFG().sect.changeNameDiamond, false))
			{
				callback.onCallback(GameData.PROTOCOL_OP_FAILED);
				return;
			}
			SBean.Counter counter = role.lockDiamond(GameData.getInstance().getCommonCFG().sect.changeNameDiamond, false);
			gs.getSectManager().sectRename(this.id, this.name, name, (errCode) -> {
				if (errCode == 0) // success
				{
					onSectNameChanged(name);
				}
				TLogger.TLogEvent tlogEvent = gs.getTLogger().createNewEvent(TLog.AT_CHANGE_SECT_NAME_CB);
				tlogEvent.setArg(GameData.getInstance().getCommonCFG().sect.changeNameDiamond);
				synchronized (role)
				{
					role.useLockedDiamond(counter, errCode < 0, tlogEvent.getGameItemRecords());
					if (errCode == 0)	//success
					{
						gs.getTLogger().logRoleEventFlow(role, tlogEvent);
					}	
				}
				callback.onCallback(errCode == 0 ? GameData.PROTOCOL_OP_SUCCESS : (errCode == -1 ? GameData.PROTOCOL_OP_SECT_CREATE_NAME_USED : errCode));
			});
		}
		
	}
	
	public synchronized void onSectNameChanged(String name)
	{
		this.name = name;
		gs.getTLogger().logSectEventFlow(this, TLog.SECT_RENAME);
		this.notifyMembersUpdateSectInfo();
	}
	
	
	public synchronized int changeJoinSectLevel(Role role, int level)
	{
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = this.getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_JOIN_LVL, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		SBean.CommonSectCFGS commonSectCFGS = GameData.getInstance().getCommonCFG().sect;
		if(commonSectCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		if(level <= commonSectCFGS.joinLvlReq)
			return GameData.PROTOCOL_OP_FAILED;
		
		this.joinLvlReq = level;
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	
	
	public synchronized int changeSectIconAndFrame(Role role, short icon, short frame){
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.DBSectMember selfMember = this.members.get(role.id);
		if (selfMember == null)
			return GameData.PROTOCOL_OP_FAILED;
		int rolePosition = this.getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_SECT_ICON, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		
		if(!GameData.getInstance().checkSectIcon(this.level, icon))
			return GameData.PROTOCOL_OP_FAILED;
				
		this.icon = icon;
		this.frame = frame;

		this.notifyMembersUpdateSectInfo();
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	
	public synchronized boolean chiefSendEmail(Role role, String content)
	{
		if (this.dismissed)
			return false;
		int rolePosition = getMemberPosition(role.id);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_MAIL, 0))
			return false;
		if(this.dayMailTimes >= GameData.getInstance().getCommonCFG().sectMail.freeCount)
		{
			if(!role.sectSendMailCB())
				return false;
		}
		
		for(SBean.DBSectMember member :  this.members.values())
		{
			gs.getLoginManager().userSendMail(member.data.role.id , role.id, role.name, "帮派邮件", content);
		}
		this.dayMailTimes ++;
		return true;
	}
	
	//帮派运镖
	public SBean.DBDeliverWishInSect initDeliverWish()
	{
		int initExp = GameData.getInstance().getSectDeliverWishCfgs(1).initValue;
		int initMoney = GameData.getInstance().getSectDeliverWishCfgs(2).initValue;
		int initHp = GameData.getInstance().getSectDeliverWishCfgs(3).initValue;
		return new SBean.DBDeliverWishInSect(0, initExp, initMoney, initHp);
	}
	
	public SBean.DBDeliverWishInSect getSectDeliverWish()
	{
		if (this.dismissed)
			return null;
		return this.sectDeliverWish.kdClone();
	}

	public synchronized void syncSectDeliver(Role role)
	{
		role.syncSectDeliverOperate(getSectDeliverWish());
	}

	public int beginSectDeliver(int routeId, int taskId, Role role)
	{
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		SBean.SectDeliverCFGS cfg = GameData.getInstance().getSectDeliverCfgs();
		if (cfg == null || this.level < cfg.startLevel)
			return GameData.PROTOCOL_OP_FAILED;
		if(this.sectDeliver.containsKey(role.id))
		{
			SBean.DBDeliverInSect info = this.sectDeliver.get(role.id);
			if(info.isDeliver == 1)
				return GameData.PROTOCOL_OP_FAILED;
		}
		int ret = role.beginSectDeliverOperate(routeId, taskId, getSectDeliverWish());
		if (ret > 0)
			this.sectDeliver.put(role.id, new SBean.DBDeliverInSect(1));
		return ret;
	}

	public synchronized int getSectDeliverWishTime()
	{
		int wishtime = 0;
		for (SBean.DBDeliverWishListItem rank : this.deliverRank.values())
		{
			wishtime += rank.wishTimes;
		}
		return wishtime;
	}

	public synchronized void modefySectDeliverWish(Role role, int exp, int money, int hp)
	{
		if (role.saveWishSectDeliverOperate(true))
		{
			this.sectDeliverWish.exp = exp > this.sectDeliverWish.exp ? exp : this.sectDeliverWish.exp;
			this.sectDeliverWish.money = money > this.sectDeliverWish.money ? money : this.sectDeliverWish.money;
			this.sectDeliverWish.hp = hp > this.sectDeliverWish.hp ? hp : this.sectDeliverWish.hp;
			this.sectDeliverWish.lastChangeTime = GameTime.getTime();
		}
	}
	
	public synchronized void updateWishTimes(Role role)
	{
		this.deliverRank.merge(role.id, new SBean.DBDeliverWishListItem(role.name, getMemberPosition(role.id), 1), (ov ,nv) -> new SBean.DBDeliverWishListItem(role.name, getMemberPosition(role.id), ov.wishTimes + 1));
	}

	public synchronized List<SBean.DBDeliverWishListItem> getDeliverWishRank()
	{
		List<SBean.DBDeliverWishListItem> list = new ArrayList<>();
		list.addAll(this.deliverRank.values());
		list.sort((arg0, arg1) -> arg1.wishTimes - arg0.wishTimes);
		return list;
	}
	
	public synchronized int sectDeliverOnCancel(Role role)
	{
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		if (!this.sectDeliver.containsKey(role.id))
			return GameData.PROTOCOL_OP_FAILED;
		if (this.sectDeliver.get(role.id).isDeliver != 1)
			return GameData.PROTOCOL_OP_FAILED;
		int ret = role.cancelSectDeliverOperate();
		if (ret > 0)
			this.sectDeliver.get(role.id).isDeliver = 0;
		return ret;
	}

	public void sectDeliverOnTimeOut(Role role)
	{
		if (this.dismissed)
			return;
		if (!this.sectDeliver.containsKey(role.id))
			return;
		SBean.DBDeliverInSect deliver = this.sectDeliver.get(role.id);
		SBean.SectDeliverCFGS cfg = GameData.getInstance().getSectDeliverCfgs();
		if (cfg != null && role.sectDeliver.startTime > 0 && GameTime.getTime() - this.members.get(role.id).data.lastLogoutTime > cfg.faildByTime)
		{
			if (deliver.isDeliver != 1)
				return;
			if (role.sectDeliverTimeOut())
				deliver.isDeliver = 0;
		}
	}

	public synchronized void syncWishSectDeliver(Role role)
	{
		role.syncWishSectDeliverOperate(getSectDeliverWish(), getDeliverWishRank());
	}
	
	public synchronized void addWishSectDeliver(Role role)
	{
		if(role.addWishSectDeliverOperate(getSectDeliverWish(), getSectDeliverWishTime()))
		{
			updateWishTimes(role);
		}
	}

	public synchronized void finishSectDeliver(Role role)
	{
		if (this.dismissed)
		{
			role.finishSectDeliverOperate(null);
			return;
		}
		if (!this.sectDeliver.containsKey(role.id))
		{
			role.finishSectDeliverOperate(null);
			return;
		}
		if (this.sectDeliver.get(role.id).isDeliver != 1)
		{
			role.finishSectDeliverOperate(null);
			return;
		}
		if (role.finishSectDeliverOperate(getSectDeliverWish()))
			this.sectDeliver.get(role.id).isDeliver = 0;
	}
	
	public synchronized int sectDeliverSearchHelp(Role role)
	{
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		if (!this.sectDeliver.containsKey(role.id))
			return GameData.PROTOCOL_OP_FAILED;
		if (this.sectDeliver.get(role.id).isDeliver != 1)
			return GameData.PROTOCOL_OP_FAILED;
		List<Integer> list = new ArrayList<>();
		for (SBean.DBSectMember m : this.members.values())
		{
			if (sectDeliver.get(m.data.role.id) == null || sectDeliver.get(m.data.role.id).isDeliver == 0)
				list.add(m.data.role.id);
		}
		return role.searchHelpSectDeliverOperate(list);
	}
	
	public synchronized int sectDeliverOnHelp(Role role, int targetId, SBean.MapLocation targetLocation, int line)
	{
		if (this.dismissed)
			return GameData.PROTOCOL_OP_SECT_HELP_TARGET_INVALID;
		if (!this.sectDeliver.containsKey(targetId) || this.sectDeliver.get(targetId).isDeliver != 1)
			return GameData.PROTOCOL_OP_SECT_HELP_TARGET_INVALID;
		return role.onHelpSectDeliverOperate(targetId, targetLocation, line);
	}

	public synchronized void setClimbTowerData(int groupId, SBean.DBClimbTowerRecordDataCfg data)
	{
		SBean.DBClimbTowerRecordDataCfg cfg = climbTowerData.get(groupId);
		if (cfg == null || data.floor > cfg.floor)
			climbTowerData.put(groupId, data);
	}
	
	public synchronized Map<Integer, SBean.DBClimbTowerRecordDataCfg> getClimbTowerData()
	{
		return Stream.clone(this.climbTowerData);
	}
	
	// 自创武功
	public synchronized void checkShareDiySkillInvalid(int roleId)
	{
		SBean.DBShareDiySkillList list =  this.shares.get(roleId);
		if(list == null)
			return;
		Iterator<SBean.DBShareDiySkill> iter = list.diyskills.iterator();
		while(iter.hasNext())
		{
			SBean.DBShareDiySkill sd = iter.next();
			if(sd.shareTime >= GameData.getInstance().getDIYSkillUniqueCFGS().saveDays)
				iter.remove();
		}
		if(list.diyskills.size() <= 0)
			this.shares.remove(roleId);
	}
	
	// 当有帮派成员离开时，清空其分享的武功
	public synchronized void sectDiySkillRemove(int roleId)
	{
		this.shares.remove(roleId);
	}
	
	//获取此帮派中所有玩家分享的技能列表
	public synchronized List<SBean.DBDiySkillShare> sectDiyskillGetShare(int roleId)
	{
		this.checkShareDiySkillInvalid(roleId);
		
		List<SBean.DBDiySkillShare> result = new ArrayList<>();
		for(Map.Entry<Integer, SBean.DBShareDiySkillList> entry : this.shares.entrySet())
		{
			SBean.DBShareDiySkillList roleShareList = entry.getValue(); //该玩家分享的技能列表
			SBean.DBSectMember sectMember = this.members.get(entry.getKey());
			for(SBean.DBShareDiySkill share : roleShareList.diyskills)
			{
				//TODO gameServer ID  = 1 
				SBean.DBDiySkillShare shareRes = new SBean.DBDiySkillShare(share, sectMember.data.role.id, 1, sectMember.data.role.name);
				result.add(shareRes);
			}
		}
		return result;
	}
	
	//自创武功每日刷新
	public synchronized void dayRefreshDiySkill()
	{
		Iterator<Map.Entry<Integer, SBean.DBShareDiySkillList>> it = this.shares.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Integer, SBean.DBShareDiySkillList> entry = it.next();
			SBean.DBShareDiySkillList value = entry.getValue();
			Iterator<SBean.DBShareDiySkill> itList = value.diyskills.iterator();
			while(itList.hasNext())
			{
				SBean.DBShareDiySkill skill = itList.next();
				skill.shareTime++;
				if(skill.shareTime > GameData.getInstance().getDIYSkillUniqueCFGS().saveDays)
				{
					itList.remove();
				}
			}
			if(value.diyskills.size() == 0)
			{
				it.remove();
			}
		}
	}
	
	//检测自创武功分享权限
	public boolean checkShareDiySkillPermission(int memberId)
	{
		int pos = this.getMemberPosition(memberId);
		return (pos == Sect.SECT_MEMBER_CHIEF) || (pos == Sect.SECT_MEMBER_DEPUTY) || (pos == Sect.SECT_MEMBER_ELDER);
	}
	
	//分享自创武功
	public synchronized int sectDiyskillShare(int roleId, SBean.DBDiySkill diySkill)
	{
		SBean.DBSectMember sectMember = this.members.get(roleId);
		SBean.DIYSkillUniqueCFGS skillUniqueCFGS = GameData.getInstance().getDIYSkillUniqueCFGS();
		int shareMax = GameData.getInstance().getDIYSkillUniqueCFGS().shareMax;
		if(sectMember == null || skillUniqueCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		if(!checkShareDiySkillPermission(roleId))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED; //没有权限
		
		int shareTotal = 0;
		for(SBean.DBShareDiySkillList cm : this.shares.values())
		{
			shareTotal += cm.diyskills.size();
		}
		if(shareTotal >= skillUniqueCFGS.sectMaxShareCount)
			return GameData.PROTOCOL_OP_SECT_DIYSKILL_FULL;
		
		SBean.DBShareDiySkillList list =  this.shares.get(roleId);
		if(list != null)
		{
			if(list.diyskills.size() >= shareMax)
				return GameData.PROTOCOL_OP_FAILED;
			for(SBean.DBShareDiySkill sd : list.diyskills)
			{
				if(sd.skill.id == diySkill.id)
					return GameData.PROTOCOL_OP_FAILED;
			}
		}else{
			list = new SBean.DBShareDiySkillList(new ArrayList<>());
			this.shares.put(roleId, list);
		}
		
		SBean.DBShareDiySkill shareDiySkill = new SBean.DBShareDiySkill(diySkill, 0, 0, 0);
		list.diyskills.add(shareDiySkill);// 引用，java全是引用。。。
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	//取消分享自创武功
	public synchronized int sectDiyskillCancelShare(int roleId, int diySkillId)
	{
		SBean.DBSectMember sectMember = this.members.get(roleId);
		SBean.DBShareDiySkillList list =  this.shares.get(roleId);
		if(sectMember == null || list == null)
			return GameData.PROTOCOL_OP_FAILED;
		int gpos = -1;
		for(int index = 0; index < list.diyskills.size(); index ++)
		{
			SBean.DBShareDiySkill sd = list.diyskills.get(index);
			if(sd.skill.id == diySkillId)
			{
				gpos = index;
				break;
			}
		}
		if(gpos == -1)
			return GameData.PROTOCOL_OP_FAILED;
		list.diyskills.remove(gpos);
		if(list.diyskills.size() == 0)
			this.shares.remove(roleId);
		
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	//借用自创武功
	public synchronized SBean.DBDiySkill sectDiyskillBorrow(int roleId, int diySkillId)
	{
		SBean.DBSectMember sectMember = this.members.get(roleId);
		SBean.DBShareDiySkillList list =  this.shares.get(roleId);
		if(sectMember == null || list == null)
			return null;
		SBean.DBShareDiySkill shareDiySkill = null;
		int listIndex = 0;
		for(int index = 0; index < list.diyskills.size(); index ++)
		{
			SBean.DBShareDiySkill sd = list.diyskills.get(index);
			if(sd.skill.id == diySkillId)
			{
				shareDiySkill = sd;
				listIndex = index;
				break;
			}
		}
		if(shareDiySkill == null)
			return null;
		if(shareDiySkill.takeCount >= GameData.getInstance().getDIYSkillUniqueCFGS().takeCount)// - shareDiySkill.awardCount)
		{
			return null;
		}
		if(shareDiySkill.shareTime >= GameData.getInstance().getDIYSkillUniqueCFGS().saveDays)
			return null;
		
		shareDiySkill.takeCount += 1;
		if(shareDiySkill.takeCount >= GameData.getInstance().getDIYSkillUniqueCFGS().takeCount)
		{
			this.shares.get(roleId).diyskills.remove(listIndex);
		}
		return shareDiySkill.skill;
	}

	public synchronized void addSectMapTime(int roleid)
	{
		this.members.get(roleid).data.stats.weekSectMapTime ++;
	}

	public synchronized void updateRoleFightPower(Role role, int newPower)
	{
		if (this.members.get(role.id) == null)
			return;
		this.members.get(role.id).data.role.fightPower = newPower;
	}

	public synchronized int changeSectPushApplication(int roleId, byte ok)
	{
		int rolePosition = this.getMemberPosition(roleId);
		if(!this.checkAuth(rolePosition, Sect.SECT_AUTH_APPLY_OPTION, 0))
			return GameData.PROTOCOL_OP_SECT_AUTH_CHECK_FAILED;
		this.applicationPush = ok;
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized int acceptSectInvite(Role inviteRole, Role beinviteRole)
	{
		this.dayRefresh();
		if (this.dismissed)
			return GameData.PROTOCOL_OP_FAILED;
		if (!this.members.containsKey(inviteRole.id))
			return GameData.PROTOCOL_OP_SECT_INVITE_ROLE_NOT_EXIST;
		SBean.SectUpLevelCFGS upLevelCFGS = GameData.getInstance().getSectUpLevel(this.level);
		if (upLevelCFGS == null)
			return GameData.PROTOCOL_OP_FAILED;
		if (this.members.size() >= upLevelCFGS.roleCount)
			return GameData.PROTOCOL_OP_SECT_MEMBERS_FULL;
		
		this.removeApplys(beinviteRole.id);
		if (this.members.containsKey(beinviteRole.id))
		{
			return GameData.PROTOCOL_OP_SECT_ALREADY_JOIN;
		}
		
		Collection<Integer> management = getSectManagement();
		if (management.contains(inviteRole.id))
		{
			synchronized (beinviteRole)
			{
				if (beinviteRole.getSectId() != 0)//小于 0可能正在创建帮派
				{
					return GameData.PROTOCOL_OP_SECT_EXIST_SECT;
				}
				int position = getNextMemberPosition();
				Sect.this.members.put(beinviteRole.id, Sect.this.createNewMember(beinviteRole.getRoleOverview()));
				Sect.this.updateSectHistory(SBean.DBSectHistory.SectEventJoin, beinviteRole.name, inviteRole.name, false, 0, 0);
				Sect.this.setMemberPosition(beinviteRole.id, position);
				switch (position)
				{
				case SECT_MEMBER_DEPUTY:
					Sect.this.updateSectHistory(SBean.DBSectHistory.SectEventAsDeputy, beinviteRole.name, inviteRole.name, false, 0, 0);
					break;
				case SECT_MEMBER_ELDER:
					Sect.this.updateSectHistory(SBean.DBSectHistory.SectEventUpElder, beinviteRole.name, inviteRole.name, false, 0, 0);
					break;
				default:
					break;
				}
				
				beinviteRole.updateSectInfo(Sect.this.id, Sect.this.name, position, Sect.this.icon, Sect.this.level);
				beinviteRole.updateSectAuras(Sect.this.getSectAurasWithoutLock());
				beinviteRole.sectData.data.lastJoinTime = GameTime.getTime();
				beinviteRole.logTasks(GameData.TASK_TYPE_JOIN_FACTION, 0, 0, 0);
				beinviteRole.save();
			}
			this.doSave();
			gs.getTLogger().logSectEventFlow(this, TLog.ROLE_JOIN_SECT);
		}
		else
		{
			if (this.applys.contains(beinviteRole.id))
			{
				return GameData.PROTOCOL_OP_SECT_ALREADY_APPLY;
			}
			if (beinviteRole.level < this.joinLvlReq)
			{
				return GameData.PROTOCOL_OP_FAILED;
			}
			if (this.applys.size() >=  GameData.getInstance().getCommonCFG().sect.sectMaxApplicant)
				return GameData.PROTOCOL_OP_SECT_APPLY_FULL;
				
			this.applys.add(beinviteRole.id);
			for(int roleId : management)
			{
				Role mRole = gs.getLoginManager().getOnGameRole(roleId);
				if (mRole != null)
				{
					notifySectApplication(mRole);
					if (this.applicationPush == GameData.SECT_APPLICATION_PUSH_FLAG_YES)
						mRole.notifyApplicationNum(this.applys.size());
				}
			}
		}
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized boolean gmAddSectVit(int vit)
	{
		addVit(vit);
		return true;
	}
	
	public int getSectGroupMapInstanceId(int mid)
	{
		if (this.groupMapCurInfo.curMapId == mid)
			return this.groupMapCurInfo.curInstance;
		return 0;
	}

	public synchronized Map<Integer, SBean.DBSectGroupMapData> syncSectGroupMapCopyData()
	{
		testAnyMapEnd();
		return Stream.clone(groupMapData);
	}

	private void testAnyMapEnd()
	{
		if (this.groupMapCurInfo == null)
		{
			tryInitCurGroupMapInfo();
			return;
		}
		int now = GameTime.getTime();
		boolean allFinish = true;
		for (Entry<Integer, SBean.DBSectGroupMapData> sectGroupMapIdData: groupMapData.entrySet())
		{
			int mapId = sectGroupMapIdData.getKey();
			SBean.DBSectGroupMapData sectGroupMapData = sectGroupMapIdData.getValue();
			if (sectGroupMapData.lastStartTime > sectGroupMapData.lastEndTime)
			{
				SBean.SectGroupMapCFGS mapCfgs = GameData.getInstance().getSectGroupMapCFGS(mapId);
				if (mapCfgs == null)
				{
					sectGroupMapData.lastEndTime = now;
					return;
				}
				if (sectGroupMapData.lastStartTime + mapCfgs.maxTime < now)
				{
					sectGroupMapData.lastEndTime = sectGroupMapData.lastStartTime + mapCfgs.maxTime;
					this.receiveGroupMapReward(mapId, 0);
				}
				allFinish = false;
			}
		}
		if (allFinish)
		{
			this.groupMapCurInfo = new SBean.SectGroupMapCurInfo(0, 0, -1, new HashSet<>());
		}
	}

	public synchronized List<Integer> getMemberLevels()
	{
		return members.values().stream().filter(member -> gs.getLoginManager().getOnGameRole(member.data.role.id) != null).map(member -> member.data.role.level).collect(Collectors.toList());
	}

	public synchronized void receiveGroupMapReward(int mapId, int mapInstance, int progress)
	{
		if (this.groupMapCurInfo.curMapId != mapId || this.groupMapCurInfo.curInstance != mapInstance)
			return;
		this.groupMapCurInfo.curProgress = progress;
		receiveGroupMapReward(mapId, mapInstance);
		gs.getTLogger().logSectMapFlow(this, GameData.getInstance().getSectGroupMapLevel(mapId), mapId, TLog.SECT_MAP_GROUP, progress < 10000 ? TLog.SECT_ACT_FAIL : TLog.SECT_ACT_FINISH);
	}

	public synchronized void receiveGroupMapReward(int mapId, int mapInstance)
	{
		SBean.DBSectGroupMapData curGroupMapData = this.groupMapData.get(mapId);
		if (curGroupMapData == null)
			return;
		int progress = this.groupMapCurInfo.curProgress;
		SBean.SectGroupMapCFGS mapCfgs = GameData.getInstance().getSectGroupMapCFGS(mapId);
		SBean.SectGroupMapPersonRewardCFGS rewardCfgs = GameData.getInstance().getSectGroupMapRewardCFGS(mapId);
		if (mapCfgs == null || rewardCfgs == null)
			return;
		int rank = 1;
		List<Entry<Integer, Integer>> rankList = curGroupMapData.damageRank.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).collect(Collectors.toList());
		if (progress == 10000)
			curGroupMapData.isfinish = 1;
		curGroupMapData.lastEndTime = GameTime.getTime();
		int finishTime = curGroupMapData.lastEndTime - curGroupMapData.lastStartTime;
		this.addVitBySectGroupMapFinish(progress, mapCfgs, finishTime);
		for (Entry<Integer, Integer> rankItem : rankList)
		{
			int roleId = rankItem.getKey();
			if (!members.containsKey(roleId))
				continue;
			if (progress == 10000)
			{
				SBean.SectGroupMapRankRewardCFGS rankReward = getSectGroupMapPersonReward(rank, rewardCfgs);
				sendSectGroupMapFinishReward(roleId, mapCfgs.finishReward, rankReward == null ? null : rankReward.finishReward, rank, progress);
				if(finishTime < mapCfgs.maxTime / 2)
				{
					List<Integer> addition = new ArrayList<>();
					addition.add(finishTime);
					addition.add(progress);
					gs.getLoginManager().sysSendMail(roleId, MailBox.SysMailType.SectGroupMapQuickFinish, MailBox.SECT_GROUP_MAP_MAIL_MAX_RESERVE_TIME, GameData.getInstance().toGameItems(mapCfgs.quickFinishReward), new ArrayList<>(addition));
					addition.clear();
					addition.add(finishTime);
					addition.add(rank);
					addition.add(progress);
					gs.getLoginManager().sysSendMail(roleId, MailBox.SysMailType.SectGroupMapQuickPerson, MailBox.SECT_GROUP_MAP_MAIL_MAX_RESERVE_TIME, GameData.getInstance().toGameItems(rankReward.quickFinishReward), new ArrayList<>(addition));
				}
			}
			else if (progress >= 7500)
			{
				SBean.SectGroupMapRankRewardCFGS rankReward = getSectGroupMapPersonReward(rank, rewardCfgs);
				sendSectGroupMapFinishReward(roleId, mapCfgs.finish100percentReward, rankReward == null ? null : rankReward.finish100percentReward, rank, progress);
			}
			else if (progress >= 5000)
			{
				SBean.SectGroupMapRankRewardCFGS rankReward = getSectGroupMapPersonReward(rank, rewardCfgs);
				sendSectGroupMapFinishReward(roleId, mapCfgs.finish75percentReward, rankReward == null ? null : rankReward.finish75percentReward, rank, progress);
			}
			else if (progress >= 1000)
			{
				SBean.SectGroupMapRankRewardCFGS rankReward = getSectGroupMapPersonReward(rank, rewardCfgs);
				sendSectGroupMapFinishReward(roleId, mapCfgs.finish50percentReward, rankReward == null ? null : rankReward.finish50percentReward, rank, progress);
			}
			else
			{
				SBean.SectGroupMapRankRewardCFGS rankReward = getSectGroupMapPersonReward(rank, rewardCfgs);
				sendSectGroupMapFinishReward(roleId, mapCfgs.finish10percentReward, rankReward == null ? null : rankReward.finish10percentReward, rank, progress);
			}
			Role role = gs.getLoginManager().getOnGameRole(roleId);
			if (role != null)
				role.syncSectGroupMapEnd(mapId, finishTime, progress, rank);
			rank++;
		}
		for (SBean.DBSectMember sectMember : this.members.values())
		{
			int roleId = sectMember.data.role.id;
			if (curGroupMapData.damageRank.containsKey(roleId))
				continue;
			if (sectMember.data.role.level < mapCfgs.enterLevel)
				continue;
			if (progress == 10000)
			{
				sendJustSectGroupMapFinishReward(roleId, mapCfgs.finishReward, progress);

				if (finishTime < mapCfgs.maxTime / 2)
				{
					List<Integer> addition = new ArrayList<>();
					addition.add(finishTime);
					addition.add(progress);
					gs.getLoginManager().sysSendMail(roleId, MailBox.SysMailType.SectGroupMapQuickFinish, MailBox.SECT_GROUP_MAP_MAIL_MAX_RESERVE_TIME, GameData.getInstance().toGameItems(mapCfgs.quickFinishReward), new ArrayList<>(addition));
				}
			}
			else if (progress >= 7500)
			{
				sendJustSectGroupMapFinishReward(roleId, mapCfgs.finish100percentReward, progress);
			}
			else if (progress >= 5000)
			{
				sendJustSectGroupMapFinishReward(roleId, mapCfgs.finish75percentReward, progress);
			}
			else if (progress >= 1000)
			{
				sendJustSectGroupMapFinishReward(roleId, mapCfgs.finish50percentReward, progress);
			}
			else
			{
				sendJustSectGroupMapFinishReward(roleId, mapCfgs.finish10percentReward, progress);
			}
			if (this.groupMapCurInfo.curRoles.contains(roleId))
			{
				Role role = gs.getLoginManager().getOnGameRole(roleId);
				if (role != null)
					role.syncSectGroupMapEnd(mapId, finishTime, progress, 0);
			}
		}
		this.groupMapCurInfo = new SBean.SectGroupMapCurInfo(0, 0, -1, new HashSet<>());
	}
	
	public void addVitBySectGroupMapFinish(int progress, SBean.SectGroupMapCFGS mapCfgs, int finishTime)
	{
		if (progress == 10000)
		{
			this.addVit(mapCfgs.finishActivites);
			if (finishTime < mapCfgs.maxTime / 2)
			{
				this.addVit(mapCfgs.quickFinishActivites);
			}
		}
		else if (progress >= 7500)
		{
			this.addVit(mapCfgs.finish100percentActivites);
		}
		else if (progress >= 5000)
		{
			this.addVit(mapCfgs.finish75percentActivites);
		}
		else if (progress >= 1000)
		{
			this.addVit(mapCfgs.finish50percentActivites);
		}
		else
		{
			this.addVit(mapCfgs.finish10percentActivites);
		}
	}
	
	public void sendSectGroupMapFinishReward(int roleId, List<SBean.DummyGoods> finishReward, List<SBean.DummyGoods> rankReward, int rank, int progress)
	{
		List<Integer> addition = new ArrayList<>();
		addition.add(progress);
		gs.getLoginManager().sysSendMail(roleId, MailBox.SysMailType.SectGroupMapFinish, MailBox.SECT_GROUP_MAP_MAIL_MAX_RESERVE_TIME, GameData.getInstance().toGameItems(finishReward), new ArrayList<>(addition));
		if (rankReward != null)
		{
			addition.clear();
			addition.add(progress);
			addition.add(rank);
			gs.getLoginManager().sysSendMail(roleId, MailBox.SysMailType.SectGroupMapPerson, MailBox.SECT_GROUP_MAP_MAIL_MAX_RESERVE_TIME, GameData.getInstance().toGameItems(rankReward), new ArrayList<>(addition));
		}
	}
	
	public void sendJustSectGroupMapFinishReward(int roleId, List<SBean.DummyGoods> finishReward, int progress)
	{
		List<Integer> addition = new ArrayList<>();
		addition.add(progress);
		gs.getLoginManager().sysSendMail(roleId, MailBox.SysMailType.SectGroupMapFinish, MailBox.SECT_GROUP_MAP_MAIL_MAX_RESERVE_TIME, GameData.getInstance().toGameItems(finishReward), addition);
	}
	
	private SBean.SectGroupMapRankRewardCFGS getSectGroupMapPersonReward(int rank, SBean.SectGroupMapPersonRewardCFGS rewardCfgs)
	{
		SBean.SectGroupMapRankRewardCFGS rankReward = null;
		for (int i = 0;i<rewardCfgs.rewards.size();i++)
		{
			if(rewardCfgs.rewards.get(i).rank > rank)
				break;
			rankReward = rewardCfgs.rewards.get(i);
		}
		return rankReward;
	}

	public synchronized void setGroupMapProcess(int mapId, int mapInstance, int progress)
	{
		if (this.groupMapCurInfo.curMapId == mapId && this.groupMapCurInfo.curInstance == mapInstance && this.groupMapData.containsKey(mapId))
			this.groupMapCurInfo.curProgress = progress;
	}
	
	public SBean.SyncGroupMapBean getSectGroupMapCopyData()
	{
		return new SBean.SyncGroupMapBean(syncSectGroupMapCopyData(), getMemberLevels());
	}

	public synchronized void groupMapProcessChanged(int mapId, int mapInstance, int spawnPointId, int roleId, int monsterId, int progress, int damage)
	{
		SBean.DBSectGroupMapData curGroupMapData = this.groupMapData.get(mapId);
		if (this.groupMapCurInfo.curMapId != mapId || this.groupMapCurInfo.curInstance != mapInstance || curGroupMapData == null)
			return;
		SBean.DBSectMember member = members.get(roleId);
		if (member != null)
			curGroupMapData.damageRank.merge(roleId, damage, (ov, nv) -> ov + nv);
	}

	private void tryInitCurGroupMapInfo()
	{
		int now = GameTime.getTime();
		boolean allFinish = true;
		for (Entry<Integer, SBean.DBSectGroupMapData> sectGroupMapIdData: groupMapData.entrySet())
		{
			int mapId = sectGroupMapIdData.getKey();
			SBean.DBSectGroupMapData sectGroupMapData = sectGroupMapIdData.getValue();
			if (sectGroupMapData.lastStartTime > sectGroupMapData.lastEndTime)
			{
				SBean.SectGroupMapCFGS mapCfgs = GameData.getInstance().getSectGroupMapCFGS(mapId);
				if (mapCfgs == null)
				{
					sectGroupMapData.lastEndTime = now;
					return;
				}
				SBean.MapClusterCFGS mapClusterCFGS = GameData.getInstance().getMapClusterCFGS(mapId);
				int progress = 0;
				for (Integer areaID : mapClusterCFGS.spawnAreas)
				{
					SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(areaID);
					if (areaCfg == null)
						continue;
					boolean allClear = true;
					for (Integer pointID : areaCfg.spawnPoint)
					{
						SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(pointID);
						if (pointCfg == null)
							continue;
						if (sectGroupMapData.monsterProcess.getOrDefault(pointCfg.id, 0) < 10000)
							allClear = false;
					}
					if (allClear)
						progress++;
					else
						break;
				}
				int curProcess = progress * 10000 / mapClusterCFGS.spawnAreas.size();
				this.groupMapCurInfo = new SBean.SectGroupMapCurInfo(mapId, 0, curProcess, new HashSet<>());
				if (sectGroupMapData.lastStartTime + mapCfgs.maxTime < now)
				{
					sectGroupMapData.lastEndTime = sectGroupMapData.lastStartTime + mapCfgs.maxTime;
					this.receiveGroupMapReward(mapId, 0);
				}
				allFinish = false;
			}
		}
		if (allFinish)
		{
			this.groupMapCurInfo = new SBean.SectGroupMapCurInfo(0, 0, -1, new HashSet<>());
		}
	}

	public synchronized void roleLeaveSectGroupMap(int rid)
	{
		this.groupMapCurInfo.curRoles.remove(rid);
	}
	
	public synchronized void groupMapOnClose(int mapId)
	{
		if (this.groupMapCurInfo.curMapId != mapId)
			return;
		this.groupMapCurInfo.curInstance = 0;
	}

	private Map<Integer, SBean.RoleDamageDetail> toRoleOverviewDamageRank(Map<Integer, Integer> damageRank)
	{
		Map<Integer, SBean.RoleDamageDetail> roleDamageRank = new HashMap<>();
		for (Entry<Integer, Integer> roleDamageItem : damageRank.entrySet())
		{
			int roleId = roleDamageItem.getKey();
			if (!this.members.containsKey(roleId))
				continue;
			roleDamageRank.put(roleDamageItem.getKey(), new SBean.RoleDamageDetail(this.members.get(roleId).data.role, roleDamageItem.getValue()));
		}
		return roleDamageRank;
	}

	public synchronized void roleEnterSectGroupMap(int roleId)
	{
		this.groupMapCurInfo.curRoles.add(roleId);
	}

	public void groupMapMonsterAddKill(int mapId, int mapInstance, int monsterId, int spawnPointId)
	{
		SBean.DBSectGroupMapData curGroupMapData = this.groupMapData.get(mapId);
		if (this.groupMapCurInfo.curMapId != mapId || this.groupMapCurInfo.curInstance != mapInstance || curGroupMapData == null)
			return;
		curGroupMapData.killNum.merge(monsterId, 1, (ov, nv) -> ov + nv);
		curGroupMapData.monsterProcess.merge(spawnPointId, 1, (ov, nv) -> ov + nv);
	}

	public void logFlagLoss(int mapId, String sectName, String roleName)
	{
		this.updateSectHistory(SBean.DBSectHistory.SectEventFlagLoss, sectName, roleName, true, mapId, 0);
	}

	public void logFlagGet(int mapId, String sectName, String roleName)
	{
		this.updateSectHistory(SBean.DBSectHistory.SectEventFlagGet, sectName, roleName, true, mapId, 0);
	}

	public void memberGetFlagReward(int mapId, int rewardTime)
	{
		SBean.FlagBattleCFGS flagCfgs = GameData.getInstance().getFlagBattleCFGS();
		if (!flagCfgs.flags.containsKey(mapId))
			return;
		SBean.FlagBattleMapCFGS flag = flagCfgs.flags.get(mapId);
		List<Integer> addition = new ArrayList<>();
		addition.add(mapId);
		addition.add(rewardTime);
		for (SBean.DBSectMember member : members.values())
		{
			if (member.data.role.level >= flagCfgs.roleRewardLevel && member.data.joinTime + flagCfgs.roleJoinSectTimeLimit < rewardTime)
			{
				gs.getLoginManager().sysSendMail(member.data.role.id, MailBox.SysMailType.SectFlagReward, MailBox.MAX_RESERVE_TIME, GameData.getInstance().toGameItems(flag.timeRoleReward), addition);
			}
		}
	}

	public void addVitByFlagReward(int mapId)
	{
		SBean.FlagBattleCFGS flagCfgs = GameData.getInstance().getFlagBattleCFGS();
		if (!flagCfgs.flags.containsKey(mapId))
			return;
		this.addVit(flagCfgs.flags.get(mapId).timeSectVitReward);
	}

	public void memberGetFlagEndReward(int mapId, int rewardTime)
	{
		SBean.FlagBattleCFGS flagCfgs = GameData.getInstance().getFlagBattleCFGS();
		if (!flagCfgs.flags.containsKey(mapId))
			return;
		SBean.FlagBattleMapCFGS flag = flagCfgs.flags.get(mapId);
		List<Integer> addition = new ArrayList<>();
		addition.add(mapId);
		for (SBean.DBSectMember member : members.values())
		{
			if (member.data.role.level >= flagCfgs.roleRewardLevel && member.data.joinTime + flagCfgs.roleJoinSectTimeLimit < rewardTime)
			{
				gs.getLoginManager().sysSendMail(member.data.role.id, MailBox.SysMailType.SectFlagEndReward, MailBox.MAX_RESERVE_TIME, GameData.getInstance().toGameItems(flag.endRoleReward), addition);
			}
		}
		this.updateSectHistory(SBean.DBSectHistory.SectEventFlagReward, "", "", false, mapId, 0);
	}

	public void addVitByFlagEndReward(int mapId)
	{
		SBean.FlagBattleCFGS flagCfgs = GameData.getInstance().getFlagBattleCFGS();
		if (!flagCfgs.flags.containsKey(mapId))
			return;
		this.addVit(flagCfgs.flags.get(mapId).endSectVitReward);
	}

	//////////////////////////////////////////////////////////
	final GameServer gs;

	private int randTick;
	private int lastSaveTime;
	private int recentUseTime; // 最近使用时间
	
	private boolean dismissed;
	private AtomicInteger nextBanquetID = new AtomicInteger();

	final int id;
	String name;
	int level;
	int lastUpgradeTime;
	String creed;
	short icon;
	short frame;
	int joinLvlReq; 
	int vitality;
	int weekVit;		//周活跃，每周清
	int rankClearTime;	

	int chief;
	Set<Integer> deputy;
	Set<Integer> elder;
	Map<Integer, SBean.DBSectMember> members;
	//Map<Integer, SBean.RoleOverview> applys;
	Set<Integer> applys;
	int lastDayRefresh;
	int lastRewardsTime;
	String qqgroup;
	int dayKickTimes;

	Map<Integer, SBean.DBSectAura> auras; // 帮派技能
	List<SBean.DBSectHistory> history;

	Map<Integer, SBean.SectBanquet> banquets; // 宴席

	Map<Integer, Integer> dayResetMapTimes;
	Map<Integer, SBean.SectMapData> openedMaps;
	Map<Integer, SBean.SectMapRewards> mapRewards;
	Map<Integer, SBean.DBSectGroupMapData> groupMapData;
	List<SBean.SectMapRewardsLog> mapRewardsLog;
	Map<Integer, Integer> logFinishedMaps;
	Map<Integer, SBean.SectMapAttacker> mapCurAttacker;
	
	
	Map<Integer, SBean.SectRoleSharedTasks> shareTaskLib;	

	int dayMailTimes;
	
	Map<Integer, SBean.DBClimbTowerRecordDataCfg> climbTowerData;
	Map<Integer, SBean.DBDeliverInSect> sectDeliver;  //帮派运镖
	SBean.DBDeliverWishInSect sectDeliverWish; //帮派运镖祝福
	int lastRefreshDeliverWishTime;
	Map<Integer, SBean.DBDeliverWishListItem> deliverRank;
	Map<Integer, SBean.DBShareDiySkillList> shares; // 自创武功分享
	byte applicationPush;
	SBean.SectGroupMapCurInfo groupMapCurInfo;
	int nowJoinMemberSize = 0;


}
