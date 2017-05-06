//
//package i3k.gs;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeMap;
//import java.util.TreeSet;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.ConcurrentHashMap;
//
//
//
//import java.util.concurrent.ConcurrentMap;
//
//import i3k.SBean;
//import i3k.SBean.BattleArray;
//import i3k.SBean.ClanBattleDefendDesc;
//import i3k.SBean.ClanCFGS;
//import i3k.SBean.DBClanBattleLog;
//import i3k.SBean.PetOverview;
//import i3k.SBean.RoleOverview;
//import i3k.clan.Clan;
//import i3k.rpc.Packet;
//import i3k.util.GameTime;
//
//public class ClanService
//{
//	public ClanService(GameServer gs)
//	{
//		this.gs = gs;
//	}
//
//	void execTask(ClanServiceTask task)
//	{
//		tasks.put(task.id, task);
//		task.doTask();
//	}
//	
//	void onTimer(int timeTick)
//	{
//		checkTimeOutTask(timeTick);
//	}
//	
//	void checkTimeOutTask(int now)
//	{
//		List<ClanServiceTask> timeoutTasks = getTimeOutTasks(now);
//		timeoutTasks.forEach(ClanServiceTask::onTimeout);
//	}
//	
//	List<ClanServiceTask> getTimeOutTasks(int now)
//	{
//		List<ClanServiceTask> timeoutTasks = new ArrayList<>();
//		Iterator<ClanServiceTask> it = tasks.values().iterator();
//		while (it.hasNext()) 
//		{
//			ClanServiceTask task = it.next();
//			if (task.isTooOld(now)) 
//			{
//				task = tasks.remove(task.id);
//				if (task != null)
//				{
//					timeoutTasks.add(task);	
//				}				
//			}
//		}
//		return timeoutTasks;
//	}
//	
//	public ClanServiceTask peekTask(int id)
//	{
//		ClanServiceTask task = tasks.remove(id);
//		if (task == null)
//			gs.getLogger().warn("MapService can't find Task id=" + id);
//		return task;
//	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	public abstract class ClanServiceTask
//	{
//		final static int MAX_WAIT_TIME = 2;
//		final int id;
//		int sendTime;
//		ClanServiceTask()
//		{
//			this.id = nextTaskID.incrementAndGet();
//		}
//		
//		boolean isTooOld(int now)
//		{
//			return sendTime + MAX_WAIT_TIME <= now;
//		}
//		
//		void doTask()
//		{
//			sendTime = GameTime.getTime();
//			doTaskImpl();
//		}
//		
//		abstract void doTaskImpl();
//		
//		abstract void onTimeout();
//	
//		QueryRoleClansTask asClanServiceTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to QueryRoleClansTask failed!");
//			return null;
//		}
//		SyncClanInfoTask asSyncClanInfoTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to SyncClanInfoTask failed!");
//			return null;
//		}
//		SearchAllClansTask asSearchAllClans()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to SearchAllClansTask failed!");
//			return null;
//		}
//		SearchClanByIdTask asSearchClanById()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to asSearchClanByName failed!");
//			return null;
//		}
//		public ClanModifyRankTask asClanModifyRankTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanModefyRankTask failed!");
//			return null;
//		}
//		public CreateClanTask asCreateClanTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to asCreateClanTask failed!");
//			return null;
//		}
//		public ApplyAddClanTask asApplyAddClanTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to asApplyAddClanTask failed!");
//			return null;
//		}
//		public RatifyAddClanTask asRatifyAddClanTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to asApplyAddClanTask failed!");
//			return null;
//		}
//		public GetClanApplicationsTask asGetClanApplicationsTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to GetClanApplicationsClanTask failed!");
//			return null;
//		}
//		public GetClanMemberTask asGetClanMemberTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to GetClanMemberTask failed!");
//			return null;
//		}
//		public ClanKickMemberTask asClanKickMemberTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanKickMemberTask failed!");
//			return null;
//		}
//		public ClanMemberLeaveTask asClanMemberLeaveTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanMemberLeaveTask failed!");
//			return null;
//		}
//		public ClanDisbandTask asClanDisbandTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanDisbandTask failed!");
//			return null;
//		}
//		public ClanAppliedTask asClanAppliedTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanAppliedTask failed!");
//			return null;
//		}
//		public ClanAppointElderTask asClanAppointElderTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanAppointElderTask failed!");
//			return null;
//		}
//		public ClanUplevelTask asClanUplevelTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanUplevelTask failed!");
//			return null;
//		}
//		public ClanRecruitTask asClanRecruitTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanRecruitTask failed!");
//			return null;
//		}
//		public ClanCancelElderTask asClanCancelElderTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanCancelElderTask failed!");
//			return null;
//		}
//		public ClanShoutuTask asClanShoutuTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanShoutuTask failed!");
//			return null;
//		}
//		public ClanShoutuSpeedupTask asClanShoutuSpeedupTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanShoutuSpeedupTask failed!");
//			return null;
//		}
//		public ClanShoutuFinishTask asClanShoutuFinishTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanShoutuFinishTask failed!");
//			return null;
//		}
//		public ClanBiwuStartTask asClanBiwuStartTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBiwuStartTask failed!");
//			return null;
//		}
//		public ClanBiwuSpeedupTask asClanBiwuSpeedupTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBiwuSpeedupTask failed!");
//			return null;
//		}
//		public ClanBiwuFinishTask asClanBiwuFinishTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBiwuFinishTask failed!");
//			return null;
//		}
//		public ClanBushiStartTask asClanBushiStartTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBushiStartTask failed!");
//			return null;
//		}
//		public ClanChuandaoStartTask asClanChuandaoStartTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanChuandaoStartTask failed!");
//			return null;
//		}
//		public ClanRushTollgateToExpTask asClanRushTollgateToExpTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanRushTollgateToExpTask failed!");
//			return null;
//		}
//		public ClanRushTollgateToItemTask asClanRushTollgateToItemTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanRushTollgateToItemTask failed!");
//			return null;
//		}
//		public ClanOwnerAttriAdditionTask asClanOwnerAttriAdditionTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanOwnerAttriAdditionTask failed!");
//			return null;
//		}
//		public SyncSelfClanTaskTask asSyncSelfClanTaskTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to SyncSelfClanTaskTask failed!");
//			return null;
//		}
//		public SyncClanTaskLibTask asSyncClanTaskLibTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to SyncClanTaskLibTask failed!");
//			return null;
//		}
//		public AutoRefreshTaskTask asAutoRefreshTaskTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to asAutoRefreshTaskTask failed!");
//			return null;
//		}
//		public ClanTaskReceiveTask asClanTaskReceiveTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanTaskReceiveTask failed!");
//			return null;
//		}
//		public ClanTaskFinishTask asClanTaskFinishTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanTaskFinishTask failed!");
//			return null;
//		}
//		public ClanTaskDiscardTask asClanTaskDiscardTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanTaskDiscardTask failed!");
//			return null;
//		}
//		public ClanSyncHistoryTask asClanSyncHistoryTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanSyncHistoryTask failed!");
//			return null;
//		}
//		public RecoverGenDiscipleTask asRecoverGenDiscipleTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to RecoverGenDiscipleTask failed!");
//			return null;
//		}
//		public ClanOreBuildUpLevelTask asClanOreBuildUpLevelTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanOreBuildUpLevelTask failed!");
//			return null;
//		}
//		public SyncClanOreTask asSyncClanOreTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to SyncClanOreTask failed!");
//			return null;
//		}
//		public ClanOreOccupyTask asClanOreOccupyTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanOreOccupyTask failed!");
//			return null;
//		}
//		public ClanOreOccupyFinishTask asClanOreOccupyFinishTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanOreOccupyFinishTask failed!");
//			return null;
//		}
//		public ClanSearchOreTask asClanSearchOreTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanSearchOreTask failed!");
//			return null;
//		}
//
//		public ClanSearchOreSyncTask asClanSearchOreSyncTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanSearchOreSyncTask failed!");
//			return null;
//		}
//		public ClanOreOwnerPetSyncTask asClanOreOwnerPetSyncTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanOreOwnerPetSyncTask failed!");
//			return null;
//		}
//		public ClanOreBorrowPetTask asClanOreBorrowPetTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanOreBorrowPetTask failed!");
//			return null;
//		}
//
//		public ClanOreHarryTask asClanOreHarryTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanOreHarryTask failed!");
//			return null;
//		}
//		public ClanBuyDoPowerTask asClanBuyDoPowerTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanSyncSelfOccupyOreTask failed!");
//			return null;
//		}
//		public ClanGetEliteDiscipleTask asClanGetEliteDiscipleTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanGetEliteDiscipleTask failed!");
//			return null;
//		}
//		public ClanCancelDisbandTask asClanCancelDisbandTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanCancelDisbandTask failed!");
//			return null;
//		}
//		public OnRoleLoginTask asOnRoleLoginTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to OnRoleLoginTask failed!");
//			return null;
//		}
//		public ClanGetBaseRankTask asClanGetBaseRankTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanGetBaseRankTask failed!");
//			return null;
//		}
//		
//		
//		public ClanSetAttackTeamTask asClanSetAttackTeamTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanSetAttackTeamTask failed!");
//			return null;
//		}
//		public ClanSetDefendTeamTask asClanSetDefendTeamTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanSetDefendTeamTask failed!");
//			return null;
//		}
//		public ClanFindEnemyTask asClanFindEnemyTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanFindEnemyTask failed!");
//			return null;
//		}
//		public ClanGetEnemyTask asClanGetEnemyTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanGetEnemyTask failed!");
//			return null;
//		}
//		public ClanBattleAttackTask asClanBattleAttackTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleAttackTask failed!");
//			return null;
//		}
//
//		public ClanBattleSeekhelpTask asClanBattleSeekhelpTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleSeekhelpTask failed!");
//			return null;
//		}
//		public ClanBattleHelpTask asClanBattleHelpTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleHelpTask failed!");
//			return null;
//		}
//		
//		public ClanMovePositionTask asClanMovePositionTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanMovePositionTask failed!");
//			return null;
//		}
//		
//		public ClanBattleFightStartTask asClanBattleFightStartTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleFightStartTask failed!");
//			return null;
//		}
//		public ClanBattleHelpFightStartTask asClanBattleHelpFightStartTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleHelpFightStartTask failed!");
//			return null;
//		}
//		public ClanBattleHelpFightEndTask asClanBattleHelpFightEndTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleHelpFightEndTask failed!");
//			return null;
//		}
//		public ClanBattleFightEndTask asClanBattleFightEndTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleFightEndTask failed!");
//			return null;
//		}
//		public ClanBattleFightExitTask asClanBattleFightExitTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBattleFightExitTask failed!");
//			return null;
//		}
//		public ClanGetOwnerFightDataTask asClanGetOwnerFightDataTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanGetOwnerFightDataTask failed!");
//			return null;
//		}
//		public ClanGetEnemyFightDataTask asClanGetEnemyFightDataTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanGetEnemyFightDataTask failed!");
//			return null;
//		}
//		public ClanShareDiySkillTask asClanShareDiySkillTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanShareDiySkillTask failed!");
//			return null;
//		}
//		public ClanCancelShareDiySkillTask asClanCancelShareDiySkillTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanCancelShareDiySkillTask failed!");
//			return null;
//		}
//		public ClanDiySkillBorrowTask asClanDiySkillBorrowTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanDiySkillBorrowTask failed!");
//			return null;
//		}
//		public ClanDiySkillGetShareTask asClanDiySkillGetShareTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanDiySkillGetShareTask failed!");
//			return null;
//		}
//		public ClanDiySkillShareAwardTask asClanDiySkillShareAwardTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanDiySkillShareAwardTask failed!");
//			return null;
//		}
//		public SearchClanByNameTask asSearchClanByName()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to SearchClanByNameTask failed!");
//			return null;
//		}
//		public ClanBuyPrestigeTask asClanBuyPrestigeTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanBuyPrestigeTask failed!");
//			return null;
//		}
//		public ClanGetClanTaskEnemyTask asClanGetClanTaskEnemyTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanGetClanTaskEnemyTask failed!");
//			return null;
//		}
//		public ClanDayRefreshTask asClanDayRefreshTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to ClanDayRefreshTask failed!");
//			return null;
//		}
//		public GetNearbyClanTask asGetNearbyClanTask()
//		{
//			gs.getLogger().warn("ClanServiceTask id=" + id + " cast to GetNearbyClanTask failed!");
//			return null;
//		}
//	}
//	
//	
//	
//	public interface OnRoleLoginCallback
//	{
//		void onCallback(int errCode, SBean.ClanOwnerAttriAddition addition, int createClan, List<Integer> addClans);
//	}
//	public class OnRoleLoginTask extends ClanServiceTask
//	{
//		SBean.RoleOverview overview;
//		OnRoleLoginCallback callback;
//		OnRoleLoginTask(SBean.RoleOverview overview, OnRoleLoginCallback callback)
//		{
//			this.overview = overview;
//			this.callback = callback;
//		}
//
//		public OnRoleLoginTask asOnRoleLoginTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanOwnerAttriAddition addition, int createClan, List<Integer> addClans)
//		{
//			callback.onCallback(errCode, addition, createClan, addClans);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, 0, GameData.emptyList());
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyOnRoleLoginTaskTask(this.id, overview);
//		}
//	}
//	public void handleOnRoleLoginTaskResponse(int tag, int errCode, SBean.ClanOwnerAttriAddition addition, int createClan, List<Integer> addClans)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			OnRoleLoginTask task = t.asOnRoleLoginTask();
//			if (task != null)
//				task.onCallback(errCode, addition, createClan, addClans);
//		}
//	}
//	void onRoleLogin(Role role)
//	{		
//		execTask(new OnRoleLoginTask(role.getRoleOverview(),  new ClanService.OnRoleLoginCallback() {
//			@Override
//			public void onCallback(int errCode, SBean.ClanOwnerAttriAddition addition, int createClan, List<Integer> addClans)
//			{
//				role.updateClanOwnerAttriAddition(addition);
//				gs.getMapService().syncRoleUpdateClanDiziTangAttr(role.id, role.gameMapContext.getCurMapId(), addition);
//				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.role_clan(createClan, addClans));
//			}
//		}));
//	}
//	//
//	
//	
//	public interface QueryRoleClansCallback
//	{
//		void onCallback(int errCode, List<SBean.ClanBrief> clans, int gsID);
//	}
//	public class QueryRoleClansTask extends ClanServiceTask
//	{
//		int roleId;
//		QueryRoleClansCallback callback;
//		QueryRoleClansTask(int roleId, QueryRoleClansCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//
//		public QueryRoleClansTask asClanServiceTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.ClanBrief> clans)
//		{
//			callback.onCallback(errCode, clans, 0);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanQueryRoleClans(this.id, roleId);
//		}
//	}
//	//handle函数都不需要加锁
//	public void handleQueryRoleClansTaskResponse(int tag, int errCode, List<SBean.ClanBrief> clans)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			QueryRoleClansTask task = t.asClanServiceTask();
//			if (task != null)
//				task.onCallback(errCode, clans);
//		}
//
//	}
//	void queryRoleClans(int roleId, QueryRoleClansCallback callback)
//	{
//		execTask(new QueryRoleClansTask(roleId, callback));
//	}
//	
//	
//	//------------------------------
//	public interface SyncClanInfoCallback
//	{
//		void onCallback(int errCode, SBean.ClanInfo clanInfo, int applyRedPoint);
//	}
//	public class SyncClanInfoTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		SyncClanInfoCallback callback;
//		SyncClanInfoTask(int roleId, int clanId, SyncClanInfoCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public SyncClanInfoTask asSyncClanInfoTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanInfo clanInfo,  int applyRedPoint)
//		{
//			callback.onCallback(errCode, clanInfo, applyRedPoint);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifySyncClanInfo(this.id, roleId, clanId);
//		}
//	}
//	public void handleSyncClanInfoTaskResponse(int tag, int errCode, SBean.ClanInfo clanInfo, int applyRedPoint)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			SyncClanInfoTask task = t.asSyncClanInfoTask();
//			if (task != null)
//				task.onCallback(errCode, clanInfo, applyRedPoint);
//		}
//
//	}
//	void syncClanInfo(int roleId, int clanId, SyncClanInfoCallback callback)
//	{
//		execTask(new SyncClanInfoTask(roleId, clanId, callback));
//	}
//	
//	
//	//------------------------------
//	public interface SearchAllClansCallback
//	{
//		void onCallback(int errCode, List<SBean.ClanOverview> clans);
//	}
//	public class SearchAllClansTask extends ClanServiceTask
//	{
//		int roleId;
//		int selfServer;
//		SearchAllClansCallback callback;
//		SearchAllClansTask(int roleId, int selfServer, SearchAllClansCallback callback)
//		{
//			this.roleId = roleId;
//			this.selfServer = selfServer;
//			this.callback = callback;
//		}
//		
//		@Override
//		public SearchAllClansTask asSearchAllClans()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.ClanOverview> clans)
//		{
//			callback.onCallback(errCode, clans);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifySearchAllClansTask(this.id, roleId, selfServer);
//		}
//	}
//	public void handleSearchAllClansTaskResponse(int tag, int errCode, List<SBean.ClanOverview> clans)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			SearchAllClansTask task = t.asSearchAllClans();
//			if (task != null)
//				task.onCallback(errCode, clans);
//		}
//
//	}
//	void searchAllClans(int roleId, int selfServer, SearchAllClansCallback callback)
//	{
//		execTask(new SearchAllClansTask(roleId, selfServer, callback));
//	}
//	
//	
//	//------------------------------
//	public interface SearchClanByIdCallback
//	{
//		void onCallback(int errCode, SBean.ClanOverview clan);
//	}
//	public class SearchClanByIdTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		SearchClanByIdCallback callback;
//		SearchClanByIdTask(int roleId, int clanId, SearchClanByIdCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public SearchClanByIdTask asSearchClanById()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanOverview clan)
//		{
//			callback.onCallback(errCode, clan);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifySearchClanByIdTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleSearchClanByIdTaskResponse(int tag, int errCode, SBean.ClanOverview clan)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			SearchClanByIdTask task = t.asSearchClanById();
//			if (task != null)
//				task.onCallback(errCode, clan);
//		}
//
//	}
//	void searchClanById(int roleId, int clanId, SearchClanByIdCallback callback)
//	{
//		execTask(new SearchClanByIdTask(roleId, clanId, callback));
//	}
//	
//	
//	
//	//------------------------------
//	public interface SearchClanByNameCallback
//	{
//		void onCallback(int errCode, SBean.ClanOverview clan);
//	}
//	public class SearchClanByNameTask extends ClanServiceTask
//	{
//		int roleId;
//		String name;
//		SearchClanByNameCallback callback;
//		SearchClanByNameTask(int roleId, String name, SearchClanByNameCallback callback)
//		{
//			this.roleId = roleId;
//			this.name = name;
//			this.callback = callback;
//		}
//		
//		@Override
//		public SearchClanByNameTask asSearchClanByName()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanOverview clan)
//		{
//			callback.onCallback(errCode, clan);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifySearchClanByNameTask(this.id, roleId, name);
//		}
//	}
//	public void handleSearchClanByNameTaskResponse(int tag, int errCode, SBean.ClanOverview clan)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			SearchClanByNameTask task = t.asSearchClanByName();
//			if (task != null)
//				task.onCallback(errCode, clan);
//		}
//
//	}
//	void searchClanByName(int roleId, String name, SearchClanByNameCallback callback)
//	{
//		execTask(new SearchClanByNameTask(roleId, name, callback));
//	}
//
//	
//	//----------------------------------------------------
//	public interface CreateClanCallback
//	{
//		void onCallback(int errCode, SBean.ClanInfo clanInfo, int taskId);
//	}
//	public class CreateClanTask extends ClanServiceTask
//	{
//		SBean.GlobalRoleOverview creater;
//		String name;
//		int isFemale;
//		CreateClanCallback callback;
//		CreateClanTask(SBean.GlobalRoleOverview creater, String name, int isFemale, CreateClanCallback callback)
//		{
//			this.creater = creater;
//			this.name = name;
//			this.isFemale = isFemale;
//			this.callback = callback;
//		}
//		
//		@Override
//		public CreateClanTask asCreateClanTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanInfo clanInfo, int taskId)
//		{
//			callback.onCallback(errCode, clanInfo, taskId);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyCreateClanTask(this.id, creater, name, isFemale);
//		}
//	}
//	public void handleCreateClanTaskTaskResponse(int tag, int errCode, SBean.ClanInfo clanInfo, int taskId)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			CreateClanTask task = t.asCreateClanTask();
//			if (task != null)
//				task.onCallback(errCode, clanInfo, taskId);
//		}
//
//	}
//	void createClan(SBean.GlobalRoleOverview creater, String name, int isFemale, CreateClanCallback callback)
//	{
//		execTask(new CreateClanTask(creater, name, isFemale, callback));
//	}
//	
//	
//	//----------------------------------------------------
//	public interface ApplyAddClanCallback
//	{
//		void onCallback(int errCode, List<Integer> rids);
//	}
//	public class ApplyAddClanTask extends ClanServiceTask
//	{
//		SBean.GlobalRoleOverview goverview;
//		int clanId;
//		ApplyAddClanCallback callback;
//		ApplyAddClanTask(SBean.GlobalRoleOverview goverview, int clanId, ApplyAddClanCallback callback)
//		{
//			this.goverview = goverview;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ApplyAddClanTask asApplyAddClanTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<Integer> rids)
//		{
//			callback.onCallback(errCode, rids);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyApplyAddTask(this.id, goverview, clanId);
//		}
//	}
//	public void handleApplyAddClanTaskTaskResponse(int tag, int errCode, List<Integer> rids)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ApplyAddClanTask task = t.asApplyAddClanTask();
//			if (task != null)
//				task.onCallback(errCode, rids);
//		}
//
//	}
//	void applyAddClan(SBean.GlobalRoleOverview goverview, int clanId,  ApplyAddClanCallback callback)
//	{
//		execTask(new ApplyAddClanTask(goverview, clanId, callback));
//	}
//	public void handleApplyAddClanForwardTaskResponse(Packet.Clan2S.ApplyAddClanForwardReq packet)
//	{
//		final Role role = gs.getLoginManager().getOnGameRole(packet.getEnemy().roleid);
//		if(role != null)
//		{
//			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.clan_applyadd_push());
//		}
//		gs.getRPCManager().notifyApplyAddClanForwardForwardTask(packet.getTagId(),packet.getSelf(), packet.getEnemy());
//	}
//	
//	
//	//----------------------------------------------------
//	public interface RatifyAddClanCallback
//	{
//		void onCallback(int errCode, int taskId, int clanId);
//	}
//	public class RatifyAddClanTask extends ClanServiceTask
//	{
//		int roleId;
//		int memberId;
//		int memberGsId;
//		int isAgree;
//		RatifyAddClanCallback callback;
//		RatifyAddClanTask(int roleId, int memberId, int memberGsId, int isAgree, RatifyAddClanCallback callback)
//		{
//			this.roleId = roleId;
//			this.memberId = memberId;
//			this.memberGsId = memberGsId;
//			this.isAgree = isAgree;
//			this.callback = callback;
//		}
//		
//		@Override
//		public RatifyAddClanTask asRatifyAddClanTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int taskId, int clanId)
//		{
//			callback.onCallback(errCode, taskId, clanId);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyRatifyAddClanTask(this.id, roleId, isAgree, memberGsId, memberId);
//		}
//	}
//	public void handleRatifyAddClanTaskResponse(int tag, int errCode, int taskId, int clanId)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			RatifyAddClanTask task = t.asRatifyAddClanTask();
//			if (task != null)
//				task.onCallback(errCode, taskId, clanId);
//		}
//
//	}
//	void ratifyAddClan(int roleId, int memberId, int memberGsId, int isAgree, RatifyAddClanCallback callback)
//	{
//		execTask(new RatifyAddClanTask(roleId, memberId, memberGsId, isAgree, callback));
//	}
//	void handleClanRatifyAddForwardResponse(int roleId, int taskId, int clanId)
//	{
//		SBean.ClanCFGS clanCFGS = GameData.getInstance().getClanCFGS();
//		gs.getLoginManager().exeCommonRoleVisitor(roleId, false, new LoginManager.CommonRoleVisitor()
//		{
//			@Override
//			public boolean visit(Role role, Role sameUserRole)
//			{
//				if(role.clanData.padding <= 0)		//padding 是否第一次加入宗门
//				{
//					role.clanData.data.attackPoint = 20;
//					role.clanData.data.xuantie = clanCFGS.others.xuetieInit;
//					role.clanData.data.yaocao = clanCFGS.others.yaocaoInit;
//					
//					role.clanData.tasks.put(clanId, new SBean.DBClanMemberTask(taskId,0,0,0,new TreeSet<Integer>(),0,0));
//				}
//				int nowTime = GameTime.getTime();
//				role.clanData.data.attackPointLastTime = nowTime;
//				return true;
//			}
//			@Override
//			public void onCallback(boolean success)
//			{
//			}
//		});	
//	}
//	
//	
//	
//	//----------------------------------------------------
//	public interface GetClanApplicationsCallback
//	{
//		void onCallback(int errCode, List<SBean.GlobalRoleOverview> apps);
//	}
//	public class GetClanApplicationsTask extends ClanServiceTask
//	{
//		int roleId;
//		GetClanApplicationsCallback callback;
//		GetClanApplicationsTask(int roleId, GetClanApplicationsCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public GetClanApplicationsTask asGetClanApplicationsTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.GlobalRoleOverview> apps)
//		{
//			callback.onCallback(errCode, apps);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyGetClanApplicationsTask(this.id, roleId);
//		}
//	}
//	public void handleGetClanApplicationsTaskResponse(int tag, int errCode, List<SBean.GlobalRoleOverview> apps)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			GetClanApplicationsTask task = t.asGetClanApplicationsTask();
//			if (task != null)
//				task.onCallback(errCode, apps);
//		}
//
//	}
//	void getClanApplications(int roleId, GetClanApplicationsCallback callback)
//	{
//		execTask(new GetClanApplicationsTask(roleId, callback));
//	}
//
//	
//	
//	//-------------------------------------------------------------------------
//	public interface GetClanMembersCallback
//	{
//		void onCallback(int errCode, List<SBean.ClanMember> members);
//	}
//	public class GetClanMemberTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		GetClanMembersCallback callback;
//		GetClanMemberTask(int roleId, int clanId, GetClanMembersCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public GetClanMemberTask asGetClanMemberTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.ClanMember> apps)
//		{
//			callback.onCallback(errCode, apps);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyGetClanMemberTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleGetClanMemberTaskResponse(int tag, int errCode, List<SBean.ClanMember> members)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			GetClanMemberTask task = t.asGetClanMemberTask();
//			if (task != null)
//				task.onCallback(errCode, members);
//		}
//
//	}
//	void getClanMemberTask(int roleId, int clanId, GetClanMembersCallback callback)
//	{
//		execTask(new GetClanMemberTask(roleId, clanId, callback));
//	}
//	
//	
//	//-------------------------------------------------------------------------------------------
//	public interface ClanKickMemberCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanKickMemberTask extends ClanServiceTask
//	{
//		int roleId;
//		int memberId;
//		int memberGsId;
//		ClanKickMemberCallback callback;
//		ClanKickMemberTask(int roleId, int memberId, int memberGsId, ClanKickMemberCallback callback)
//		{
//			this.roleId = roleId;
//			this.memberId = memberId;
//			this.memberGsId = memberGsId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanKickMemberTask asClanKickMemberTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanKickMemberTask(this.id, roleId, memberId, memberGsId);
//		}
//	}
//	public void handleClanKickMemberTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanKickMemberTask task = t.asClanKickMemberTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//
//	}
//	void clanKickMember(int roleId, int memberId, int memberGsId, ClanKickMemberCallback callback)
//	{
//		execTask(new ClanKickMemberTask(roleId, memberId, memberGsId, callback));
//	}
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanMemberLeaveCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanMemberLeaveTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		ClanMemberLeaveCallback callback;
//		ClanMemberLeaveTask(int roleId, int clanId, ClanMemberLeaveCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanMemberLeaveTask asClanMemberLeaveTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanMemberLeaveTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleClanMemberLeaveTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanMemberLeaveTask task = t.asClanMemberLeaveTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//
//	}
//	void clanMemberLeave(int roleId, int clanId, ClanMemberLeaveCallback callback)
//	{
//		execTask(new ClanMemberLeaveTask(roleId, clanId, callback));
//	}
//
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanDisbandCallback
//	{
//		void onCallback(int errCode, int disbandTime);
//	}
//	public class ClanDisbandTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanDisbandCallback callback;
//		ClanDisbandTask(int roleId, ClanDisbandCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanDisbandTask asClanDisbandTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int disbandTime)
//		{
//			callback.onCallback(errCode, disbandTime);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanDisbandTask(this.id, roleId);
//		}
//	}
//	public void handleClanDisbandTaskResponse(int tag, int errCode, int disbandTime)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanDisbandTask task = t.asClanDisbandTask();
//			if (task != null)
//				task.onCallback(errCode, disbandTime);
//		}
//
//	}
//	void clanDisband(int roleId, ClanDisbandCallback callback)
//	{
//		execTask(new ClanDisbandTask(roleId, callback));
//	}
//	
//	
//	//---------------------
//	public interface ClanCancelDisbandCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanCancelDisbandTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanCancelDisbandCallback callback;
//		ClanCancelDisbandTask(int roleId, ClanCancelDisbandCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanCancelDisbandTask asClanCancelDisbandTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanCancelDisbandTask(this.id, roleId);
//		}
//	}
//	public void handleClanCancelDisbandTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanCancelDisbandTask task = t.asClanCancelDisbandTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//
//	}
//	void clanCancelDisband(int roleId, ClanCancelDisbandCallback callback)
//	{
//		execTask(new ClanCancelDisbandTask(roleId, callback));
//	}
//	
//	
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanAppliedCallback
//	{
//		void onCallback(int errCode, List<Integer> clanIds);
//	}
//	public class ClanAppliedTask extends ClanServiceTask
//	{
//		int roleId;
//		List<Integer> clanIds;
//		ClanAppliedCallback callback;
//		ClanAppliedTask(int roleId, List<Integer> clanIds, ClanAppliedCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanIds = clanIds;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanAppliedTask asClanAppliedTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<Integer> clanIds)
//		{
//			callback.onCallback(errCode, clanIds);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanAppliedTask(this.id, roleId, clanIds);
//		}
//	}
//	public void handleClanAppliedTaskResponse(int tag, int errCode, List<Integer> clanIds)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanAppliedTask task = t.asClanAppliedTask();
//			if (task != null)
//				task.onCallback(errCode, clanIds);
//		}
//
//	}
//	void clanApplied(int roleId, List<Integer> clanIds, ClanAppliedCallback callback)
//	{
//		execTask(new ClanAppliedTask(roleId, clanIds, callback));
//	}
//
//	
//	//----------------------------------------------------------------------
//	public interface ClanAppointElderCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanAppointElderTask extends ClanServiceTask
//	{
//		int roleId;
//		int memberId;
//		int memberGsId;
//		ClanAppointElderCallback callback;
//		ClanAppointElderTask(int roleId, int memberId, int memberGsId, ClanAppointElderCallback callback)
//		{
//			this.roleId = roleId;
//			this.memberId = memberId;
//			this.memberGsId = memberGsId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanAppointElderTask asClanAppointElderTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanAppointElderTask(this.id, roleId, memberId, memberGsId);
//		}
//	}
//	public void handleClanAppointElderTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanAppointElderTask task = t.asClanAppointElderTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//
//	}
//	void clanAppointElder(int roleId, int memId, int memGsId, ClanAppointElderCallback callback)
//	{
//		execTask(new ClanAppointElderTask(roleId, memId, memGsId, callback));
//	}
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanUplevelCallback
//	{
//		void onCallback(int errCode, int level, SBean.ClanOwnerAttriAddition attriAddition);
//	}
//	public class ClanUplevelTask extends ClanServiceTask
//	{
//		int roleId;
//		int curLevel;
//		ClanUplevelCallback callback;
//		ClanUplevelTask(int roleId, int curLevel, ClanUplevelCallback callback)
//		{
//			this.roleId = roleId;
//			this.curLevel = curLevel;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanUplevelTask asClanUplevelTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int level, SBean.ClanOwnerAttriAddition attriAddition)
//		{
//			callback.onCallback(errCode, level, attriAddition);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanUplevelTask(this.id, roleId, curLevel);
//		}
//	}
//	public void handleClanUplevelTaskResponse(int tag, int errCode, int level, SBean.ClanOwnerAttriAddition attriAddition)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanUplevelTask task = t.asClanUplevelTask();
//			if (task != null)
//				task.onCallback(errCode, level, attriAddition);
//		}
//
//	}
//	void clanUplevel(int roleId, int curLevel, ClanUplevelCallback callback)
//	{
//		execTask(new ClanUplevelTask(roleId, curLevel, callback));
//	}
//
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanRecruitCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanRecruitTask extends ClanServiceTask
//	{
//		int roleId;
//		int type;
//		ClanRecruitCallback callback;
//		ClanRecruitTask(int roleId, int type, ClanRecruitCallback callback)
//		{
//			this.roleId = roleId;
//			this.type = type;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanRecruitTask asClanRecruitTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanRecruitTask(this.id, roleId, type);
//		}
//	}
//	public void handleClanRecruitTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanRecruitTask task = t.asClanRecruitTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//
//	}
//	void clanRecruit(int roleId, int type, ClanRecruitCallback callback)
//	{
//		execTask(new ClanRecruitTask(roleId, type, callback));
//	}
//		
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanCancelElderCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanCancelElderTask extends ClanServiceTask
//	{
//		int roleId;
//		int memberId;
//		int memberGsId;
//		ClanCancelElderCallback callback;
//		ClanCancelElderTask(int roleId, int memberId, int memberGsId, ClanCancelElderCallback callback)
//		{
//			this.roleId = roleId;
//			this.memberId = memberId;
//			this.memberGsId = memberGsId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanCancelElderTask asClanCancelElderTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanCancelElderTask(this.id, roleId, memberId, memberGsId);
//		}
//	}
//	public void handleClanCancelElderTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanCancelElderTask task = t.asClanCancelElderTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//
//	}
//	void clanCancelElder(int roleId, int memId, int memGsId, ClanCancelElderCallback callback)
//	{
//		execTask(new ClanCancelElderTask(roleId, memId, memGsId, callback));
//	}
//
//	
//	//----------------------------------------------------------------------
//	public interface ClanShoutuCallback
//	{
//		void onCallback(int errCode, int startTime);
//	}
//	public class ClanShoutuTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanShoutuCallback callback;
//		ClanShoutuTask(int roleId, ClanShoutuCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanShoutuTask asClanShoutuTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int startTime)
//		{
//			callback.onCallback(errCode, startTime);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanShoutuTask(this.id, roleId);
//		}
//	}
//	public void handleClanShoutuTaskResponse(int tag, int errCode, int startTime)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanShoutuTask task = t.asClanShoutuTask();
//			if (task != null)
//				task.onCallback(errCode, startTime);
//		}
//	}
//	void clanShoutu(int roleId, ClanShoutuCallback callback)
//	{
//		execTask(new ClanShoutuTask(roleId, callback));
//	}
//	
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanShoutuSpeedupCallback
//	{
//		void onCallback(int errCode, int dzTotal, int endTime);
//	}
//	public class ClanShoutuSpeedupTask extends ClanServiceTask
//	{
//		int roleId;
//		int count;
//		int startTime;
//		ClanShoutuSpeedupCallback callback;
//		ClanShoutuSpeedupTask(int roleId, int count, int startTime, ClanShoutuSpeedupCallback callback)
//		{
//			this.roleId = roleId;
//			this.count = count;
//			this.startTime = startTime;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanShoutuSpeedupTask asClanShoutuSpeedupTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int dzTotal, int endTime)
//		{
//			callback.onCallback(errCode, dzTotal, endTime);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanShoutuSpeedupTask(this.id, roleId, count, startTime);
//		}
//	}
//	public void handleClanShoutuSpeedupTaskResponse(int tag, int errCode, int dzTotal, int endTime)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanShoutuSpeedupTask task = t.asClanShoutuSpeedupTask();
//			if (task != null)
//				task.onCallback(errCode, dzTotal, endTime);
//		}
//	}
//	void clanShoutuSpeedup(int roleId, int count, int startTime, ClanShoutuSpeedupCallback callback)
//	{
//		execTask(new ClanShoutuSpeedupTask(roleId, count, startTime, callback));
//	}
//	
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanShoutuFinishCallback
//	{
//		void onCallback(int errCode, int dzTotal, int endTime);
//	}
//	public class ClanShoutuFinishTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanShoutuFinishCallback callback;
//		ClanShoutuFinishTask(int roleId, ClanShoutuFinishCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanShoutuFinishTask asClanShoutuFinishTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int dzTotal, int endTime)
//		{
//			callback.onCallback(errCode, dzTotal, endTime);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanShoutuFinishTask(this.id, roleId);
//		}
//	}
//	public void handleClanShoutuFinishTaskResponse(int tag, int errCode, int dzTotal, int endTime)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanShoutuFinishTask task = t.asClanShoutuFinishTask();
//			if (task != null)
//				task.onCallback(errCode, dzTotal, endTime);
//		}
//	}
//	void clanShoutuFinish(int roleId, ClanShoutuFinishCallback callback)
//	{
//		execTask(new ClanShoutuFinishTask(roleId, callback));
//	}
//	
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanBiwuStartCallback
//	{
//		void onCallback(int errCode, int startTime);
//	}
//	public class ClanBiwuStartTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanBiwuStartCallback callback;
//		ClanBiwuStartTask(int roleId, ClanBiwuStartCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBiwuStartTask asClanBiwuStartTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int startTime)
//		{
//			callback.onCallback(errCode, startTime);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBiwuStartTask(this.id, roleId);
//		}
//	}
//	public void handleClanBiwuStartTaskResponse(int tag, int errCode, int startTime)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBiwuStartTask task = t.asClanBiwuStartTask();
//			if (task != null)
//				task.onCallback(errCode, startTime);
//		}
//	}
//	void clanBiwuStart(int roleId, ClanBiwuStartCallback callback)
//	{
//		execTask(new ClanBiwuStartTask(roleId, callback));
//	}
//	
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanBiwuSpeedupCallback
//	{
//		void onCallback(int errCode, List<SBean.DBClanEliteDisciple> dzElites, int endTime);
//	}
//	public class ClanBiwuSpeedupTask extends ClanServiceTask
//	{
//		int roleId;
//		int count;
//		int startTime;
//		ClanBiwuSpeedupCallback callback;
//		ClanBiwuSpeedupTask(int roleId, int count, int startTime, ClanBiwuSpeedupCallback callback)
//		{
//			this.roleId = roleId;
//			this.count = count;
//			this.startTime = startTime;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBiwuSpeedupTask asClanBiwuSpeedupTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.DBClanEliteDisciple> dzElites, int endTime)
//		{
//			callback.onCallback(errCode, dzElites, endTime);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBiwuSpeedupTask(this.id, roleId, count, startTime);
//		}
//	}
//	public void handleClanBiwuSpeedupTaskResponse(int tag, int errCode, List<SBean.DBClanEliteDisciple> dzElites, int endTime)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBiwuSpeedupTask task = t.asClanBiwuSpeedupTask();
//			if (task != null)
//				task.onCallback(errCode, dzElites, endTime);
//		}
//	}
//	void clanBiwuSpeedup(int roleId, int count, int startTime, ClanBiwuSpeedupCallback callback)
//	{
//		execTask(new ClanBiwuSpeedupTask(roleId, count, startTime, callback));
//	}
//
//	//----------------------------------------------------------------------
//	public interface ClanBiwuFinishCallback
//	{
//		void onCallback(int errCode, List<SBean.DBClanEliteDisciple> dzElites, int endTime, SBean.ClanOwnerAttriAddition attriAddition);
//	}
//	public class ClanBiwuFinishTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanBiwuFinishCallback callback;
//		ClanBiwuFinishTask(int roleId, ClanBiwuFinishCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBiwuFinishTask asClanBiwuFinishTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.DBClanEliteDisciple> dzElites, int endTime, SBean.ClanOwnerAttriAddition attriAddition)
//		{
//			callback.onCallback(errCode, dzElites, endTime, attriAddition);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, 0, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBiwuFinishTask(this.id, roleId);
//		}
//	}
//	public void handleClanBiwuFinishTaskResponse(int tag, int errCode, List<SBean.DBClanEliteDisciple> dzElites, int endTime, SBean.ClanOwnerAttriAddition attriAddition)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBiwuFinishTask task = t.asClanBiwuFinishTask();
//			if (task != null)
//				task.onCallback(errCode, dzElites, endTime, attriAddition);
//		}
//	}
//	void clanBiwuFinish(int roleId, ClanBiwuFinishCallback callback)
//	{
//		execTask(new ClanBiwuFinishTask(roleId, callback));
//	}
//	
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanBushiStartCallback
//	{
//		void onCallback(int errCode, int dzTotal, int endTime);
//	}
//	public class ClanBushiStartTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanBushiStartCallback callback;
//		ClanBushiStartTask(int roleId, ClanBushiStartCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBushiStartTask asClanBushiStartTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int dzTotal, int endTime)
//		{
//			callback.onCallback(errCode, dzTotal, endTime);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBushiStartTask(this.id, roleId);
//		}
//	}
//	public void handleClanBushiStartTaskResponse(int tag, int errCode, int dzTotal, int endTime)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBushiStartTask task = t.asClanBushiStartTask();
//			if (task != null)
//				task.onCallback(errCode, dzTotal, endTime);
//		}
//	}
//	void clanBushiStart(int roleId, ClanBushiStartCallback callback)
//	{
//		execTask(new ClanBushiStartTask(roleId, callback));
//	}
//	
//	
//	
//	//----------------------------------------------------------------------
//	public interface ClanChuandaoStartCallback
//	{
//		void onCallback(int errCode, int startTime, List<SBean.DBClanEliteDisciple> dzElites, SBean.ClanOwnerAttriAddition attriAddition);
//	}
//	public class ClanChuandaoStartTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanChuandaoStartCallback callback;
//		ClanChuandaoStartTask(int roleId, ClanChuandaoStartCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanChuandaoStartTask asClanChuandaoStartTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int startTime, List<SBean.DBClanEliteDisciple> dzElites, SBean.ClanOwnerAttriAddition attriAddition)
//		{
//			callback.onCallback(errCode, startTime, dzElites, attriAddition);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanChuandaoStartTask(this.id, roleId);
//		}
//	}
//	public void handleClanChuandaoStartTaskResponse(int tag, int errCode, int startTime, List<SBean.DBClanEliteDisciple> dzElites, SBean.ClanOwnerAttriAddition attriAddition)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanChuandaoStartTask task = t.asClanChuandaoStartTask();
//			if (task != null)
//				task.onCallback(errCode, startTime, dzElites, attriAddition);
//		}
//	}
//	void clanChuandaoStart(int roleId, ClanChuandaoStartCallback callback)
//	{
//		execTask(new ClanChuandaoStartTask(roleId, callback));
//	}
//	
//	
//	//-----------------------------------------------------
//	public interface ClanRushTollgateToExpCallback
//	{
//		void onCallback(int errCode, SBean.DBClanEliteDisciple attriValue, SBean.ClanOwnerAttriAddition attriAddition);
//	}
//	public class ClanRushTollgateToExpTask extends ClanServiceTask
//	{
//		int roleId;
//		int dzid;
//		int count;
//		int useMoney;
//		ClanRushTollgateToExpCallback callback;
//		ClanRushTollgateToExpTask(int roleId, int dzid, int count, int useMoney, ClanRushTollgateToExpCallback callback)
//		{
//			this.roleId = roleId;
//			this.dzid = dzid;
//			this.count = count;
//			this.useMoney = useMoney;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanRushTollgateToExpTask asClanRushTollgateToExpTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.DBClanEliteDisciple attriValue, SBean.ClanOwnerAttriAddition attriAddition)
//		{
//			callback.onCallback(errCode, attriValue, attriAddition);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanRushTollgateToExpTask(this.id, roleId, dzid, count, useMoney);
//		}
//	}
//	public void handleClanRushTollgateToExpTaskResponse(int tag, int errCode, SBean.DBClanEliteDisciple attriValue, SBean.ClanOwnerAttriAddition attriAddition)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanRushTollgateToExpTask task = t.asClanRushTollgateToExpTask();
//			if (task != null)
//				task.onCallback(errCode, attriValue, attriAddition);
//		}
//	}
//	void clanRushTollgateToExp(int roleId, int dzid, int count, int useMoney, ClanRushTollgateToExpCallback callback)
//	{
//		execTask(new ClanRushTollgateToExpTask(roleId, dzid, count, useMoney, callback));
//	}
//	
//	
//	
//	//-----------------------------------------------------
//	public interface ClanRushTollgateToItemCallback
//	{
//		void onCallback(int errCode, SBean.DBClanEliteDisciple attriValue, SBean.ClanOwnerAttriAddition attriAddition);
//	}
//	public class ClanRushTollgateToItemTask extends ClanServiceTask
//	{
//		int roleId;
//		int dzid;
//		List<Integer> items;
//		ClanRushTollgateToItemCallback callback;
//		ClanRushTollgateToItemTask(int roleId, int dzid, List<Integer> items, ClanRushTollgateToItemCallback callback)
//		{
//			this.roleId = roleId;
//			this.dzid = dzid;
//			this.items = items;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanRushTollgateToItemTask asClanRushTollgateToItemTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.DBClanEliteDisciple attriValue, SBean.ClanOwnerAttriAddition attriAddition)
//		{
//			callback.onCallback(errCode, attriValue, attriAddition);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanRushTollgateToItemTask(this.id, roleId, dzid, items);
//		}
//	}
//	public void handleClanRushTollgateToItemTaskResponse(int tag, int errCode, SBean.DBClanEliteDisciple attriValue, SBean.ClanOwnerAttriAddition attriAddition)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanRushTollgateToItemTask task = t.asClanRushTollgateToItemTask();
//			if (task != null)
//				task.onCallback(errCode, attriValue, attriAddition);
//		}
//	}
//	void clanRushTollgateToItem(int roleId, int dzid, List<Integer> items, ClanRushTollgateToItemCallback callback)
//	{
//		execTask(new ClanRushTollgateToItemTask(roleId, dzid, items, callback));
//	}
//	
//	
//	//-----------------------------------------------------------------------------------------
//	public interface ClanOwnerAttriAdditionCallback
//	{
//		void onCallback(int errCode, SBean.ClanOwnerAttriAddition attriAddition);
//	}
//	public class ClanOwnerAttriAdditionTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanOwnerAttriAdditionCallback callback;
//		ClanOwnerAttriAdditionTask(int roleId, ClanOwnerAttriAdditionCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanOwnerAttriAdditionTask asClanOwnerAttriAdditionTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanOwnerAttriAddition attriAddition)
//		{
//			callback.onCallback(errCode, attriAddition);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanOwnerAttriAdditionTask(this.id, roleId);
//		}
//	}
//	public void handleClanOwnerAttriAdditionTaskResponse(int tag, int errCode, SBean.ClanOwnerAttriAddition attriAddition)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanOwnerAttriAdditionTask task = t.asClanOwnerAttriAdditionTask();
//			if (task != null)
//				task.onCallback(errCode, attriAddition);
//		}
//	}
//	void clanOwnerAttriAddition(int roleId, ClanOwnerAttriAdditionCallback callback)
//	{
//		execTask(new ClanOwnerAttriAdditionTask(roleId, callback));
//	}
//	
//	
//	
//	//-------------------------------------------------------任务-----------------------------
//	public interface SyncSelfClanTaskCallback
//	{
//		void onCallback(int errCode, int taskId, int basedz, int usedz);
//	}
//	public class SyncSelfClanTaskTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		int taskId;
//		SyncSelfClanTaskCallback callback;
//		SyncSelfClanTaskTask(int roleId, int clanId, int taskId, SyncSelfClanTaskCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId =clanId;
//			this.taskId = taskId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public SyncSelfClanTaskTask asSyncSelfClanTaskTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int taskId, int basedz, int usedz)
//		{
//			callback.onCallback(errCode, taskId, basedz, usedz);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifySyncSelfClanTaskTask(this.id, roleId, clanId, taskId);
//		}
//	}
//	public void handleSyncSelfClanTaskTaskResponse(int tag, int errCode, int taskId, int basedz, int usedz)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			SyncSelfClanTaskTask task = t.asSyncSelfClanTaskTask();
//			if (task != null)
//				task.onCallback(errCode, taskId, basedz, usedz);
//		}
//	}
//	void clanTaskSyncSelf(int roleId, int clanId, int taskId, SyncSelfClanTaskCallback callback)
//	{
//		execTask(new SyncSelfClanTaskTask(roleId, clanId, taskId, callback));
//	}
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface SyncClanTaskLibCallback
//	{
//		void onCallback(int errCode, Map<Integer, Integer> tasks);
//	}
//	public class SyncClanTaskLibTask extends ClanServiceTask
//	{
//		int roleId;
//		SyncClanTaskLibCallback callback;
//		SyncClanTaskLibTask(int roleId, SyncClanTaskLibCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public SyncClanTaskLibTask asSyncClanTaskLibTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, Map<Integer, Integer> tasks)
//		{
//			callback.onCallback(errCode, tasks);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifySyncClanTaskLibTask(this.id, roleId);
//		}
//	}
//	public void handleSyncClanTaskLibTaskResponse(int tag, int errCode, Map<Integer, Integer> tasks)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			SyncClanTaskLibTask task = t.asSyncClanTaskLibTask();
//			if (task != null)
//				task.onCallback(errCode, tasks);
//		}
//	}
//	void clanTaskLibTask(int roleId, SyncClanTaskLibCallback callback)
//	{
//		execTask(new SyncClanTaskLibTask(roleId, callback));
//	}
//	
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface AutoRefreshTaskCallback
//	{
//		void onCallback(int errCode, int taskId);
//	}
//	public class AutoRefreshTaskTask extends ClanServiceTask
//	{
//		int roleId;
//		int taskId;
//		AutoRefreshTaskCallback callback;
//		AutoRefreshTaskTask(int roleId, int taskId, AutoRefreshTaskCallback callback)
//		{
//			this.roleId = roleId;
//			this.taskId = taskId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public AutoRefreshTaskTask asAutoRefreshTaskTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int taskId)
//		{
//			callback.onCallback(errCode, taskId);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyAutoRefreshTaskTask(this.id, roleId, taskId);
//		}
//	}
//	public void handleAutoRefreshTaskTaskResponse(int tag, int errCode, int taskId)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			AutoRefreshTaskTask task = t.asAutoRefreshTaskTask();
//			if (task != null)
//				task.onCallback(errCode, taskId);
//		}
//	}
//	void autoRefreshTask(int roleId, int taskId, AutoRefreshTaskCallback callback)
//	{
//		execTask(new AutoRefreshTaskTask(roleId, taskId, callback));
//	}
//	
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface ClanTaskReceiveCallback
//	{
//		void onCallback(int errCode, int useDZTotal, int useTime, int genDisciple, int usedGenDisciple, SBean.DBTaskEnemy clanTaskEnemy);
//	}
//	public class ClanTaskReceiveTask extends ClanServiceTask
//	{
//		int roleId;
//		int taskId;
//		int clanId;
//		List<Integer> eliteDisciples;
//		List<Integer> pets;
//		ClanTaskReceiveCallback callback;
//		ClanTaskReceiveTask(int roleId, int taskId, int clanId, List<Integer> eliteDisciples, List<Integer> pets, ClanTaskReceiveCallback callback)
//		{
//			this.roleId = roleId;
//			this.taskId = taskId;
//			this.clanId = clanId;
//			this.eliteDisciples = eliteDisciples;
//			this.pets = pets;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanTaskReceiveTask asClanTaskReceiveTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int useDZTotal, int useTime, int genDisciple, int usedGenDisciple, SBean.DBTaskEnemy clanTaskEnemy)
//		{
//			callback.onCallback(errCode, useDZTotal, useTime, genDisciple, usedGenDisciple, clanTaskEnemy);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0, 0, 0, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanTaskReceiveTask(this.id, roleId, taskId, eliteDisciples, pets, clanId);
//		}
//	}
//	public void handleClanTaskReceiveTaskResponse(int tag, int errCode, int useDZTotal, int useTime, int genDisciple, int usedGenDisciple, SBean.DBTaskEnemy clanTaskEnemy)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanTaskReceiveTask task = t.asClanTaskReceiveTask();
//			if (task != null)
//				task.onCallback(errCode, useDZTotal, useTime, genDisciple, usedGenDisciple, clanTaskEnemy);
//		}
//	}
//	void receiveTask(int roleId, int taskId, int clanId, List<Integer> eliteDisciples, List<Integer> pets, ClanTaskReceiveCallback callback)
//	{
//		execTask(new ClanTaskReceiveTask(roleId, taskId, clanId, eliteDisciples, pets, callback));
//	}
//	public interface ClanGetTaskBattleEnemyCallback
//	{
//		void onCallback(int errCode, int useDZTotal, int useTime, int genDisciple, int usedGenDisciple, SBean.DBTaskEnemy clanTaskEnemy);
//	}
//
//	//
//	public interface ClanGetOwnerFightDataCallback
//	{
//		void onCallback(int errCode, SBean.FightPet ownerFightPet, SBean.PetHost ownerFightPetHost);
//	}
//	public class ClanGetOwnerFightDataTask extends ClanServiceTask
//	{
//		int roleId;
//		int ownerId;
//		int ownerServerId;
//		int ownerPet;
//		ClanGetOwnerFightDataCallback callback;
//		ClanGetOwnerFightDataTask(int roleId, int ownerId, int ownerServerId, int ownerPet, ClanGetOwnerFightDataCallback callback)
//		{
//			this.roleId = roleId;
//			this.ownerId = ownerId;
//			this.ownerPet = ownerPet;
//			this.ownerServerId = ownerServerId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanGetOwnerFightDataTask asClanGetOwnerFightDataTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.FightPet ownerFightPet, SBean.PetHost ownerFightPetHost)
//		{
//			callback.onCallback(errCode, ownerFightPet, ownerFightPetHost);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanGetOwnerFightDataTask(this.id, roleId, ownerId, ownerServerId, ownerPet);
//		}
//	}
//	public void handleClanGetOwnerFightDataTaskResponse(int tag, int errCode, SBean.FightPet ownerFightPet, SBean.PetHost ownerFightPetHost)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanGetOwnerFightDataTask task = t.asClanGetOwnerFightDataTask();
//			if (task != null)
//				task.onCallback(errCode, ownerFightPet, ownerFightPetHost);
//		}
//	}
//	public void handleClanGetOwnerFightDataForwardTaskResponse(Packet.Clan2S.ClanOwnerFightDataForwardReq packet)
//	{
//		final Role role = gs.getLoginManager().getOnGameRole(packet.getOwnerId());
//		if(role != null)
//		{
//			SBean.FightPet ownerFightPet = role.getMapFightPetWithoutLock(packet.getOwnerPet());
//			SBean.PetHost ownerFightPetHost = role.getMapPetHostWithoutLock();
//			gs.getRPCManager().notifyClanGetOwnerFightDataForwardTask(packet, ownerFightPet, ownerFightPetHost);
//		}else
//		{
//			gs.getLoginManager().getOnLoanPet(packet.getOwnerId(), packet.getOwnerPet(), (ownerFightPet, ownerFightPetHost) -> gs.getRPCManager().notifyClanGetOwnerFightDataForwardTask(packet, ownerFightPet, ownerFightPetHost));
//		}
//	}
//	void clanGetOwnerFightData(int roleId, int ownerId, int ownerServerId, int ownerPet, ClanGetOwnerFightDataCallback callback)
//	{
//		execTask(new ClanGetOwnerFightDataTask(roleId, ownerId, ownerServerId, ownerPet, callback));
//	}
//	//
//	public interface ClanGetEnemyFightDataCallback
//	{
//		void onCallback(int errCode, SBean.BattleArray ba);
//	}
//	public class ClanGetEnemyFightDataTask extends ClanServiceTask
//	{
//		int roleId;
//		int enemyId;
//		int enemyServerId;
//		List<Integer> enemyPet;
//		ClanGetEnemyFightDataCallback callback;
//		ClanGetEnemyFightDataTask(int roleId, int enemyId, int enemyServerId, List<Integer> enemyPet, ClanGetEnemyFightDataCallback callback)
//		{
//			this.roleId = roleId;
//			this.enemyId = enemyId;
//			this.enemyServerId = enemyServerId;
//			this.enemyPet = enemyPet;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanGetEnemyFightDataTask asClanGetEnemyFightDataTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.BattleArray ba)
//		{
//			callback.onCallback(errCode, ba);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanGetEnemyFightDataTask(this.id, roleId, enemyId, enemyServerId, enemyPet);
//		}
//	}
//	public void handleClanGetEnemyFightDataTaskResponse(int tag, int errCode, SBean.BattleArray ba)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanGetEnemyFightDataTask task = t.asClanGetEnemyFightDataTask();
//			if (task != null)
//				task.onCallback(errCode, ba);
//		}
//	}
//	void clanGetEnemyFightData(int roleId, int enemyId, int enemyServerId, List<Integer> enemyPet, ClanGetEnemyFightDataCallback callback)
//	{
//		execTask(new ClanGetEnemyFightDataTask(roleId, enemyId, enemyServerId, enemyPet, callback));
//	}
//	public void handleClanGetEnemyFightDataForwardTaskResponse(Packet.Clan2S.ClanEnemyFightDataForwardReq packet)
//	{
//		final Role role = gs.getLoginManager().getOnGameRole(packet.getEnemyId());
//		if(role != null)
//		{
//			SBean.FightRole fightRole = role.getMapFightRoleWithoutLock();
//			Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
//			for(int petId : packet.getEnemyPet())
//			{
//				SBean.FightPet pet = role.getMapFightPetWithoutLock(petId);
//				fightPets.put(pet.id, pet);
//			}
//	
//			BattleArray battleArray = new SBean.BattleArray(fightRole, fightPets);
//			gs.getRPCManager().notifyClanGetEnemyFightDataForwardTask(packet, battleArray);
//		}else{
//			gs.getLoginManager().getClanTaskFightArray(packet.getEnemyId(), packet.getEnemyPet(), ba -> gs.getRPCManager().notifyClanGetEnemyFightDataForwardTask(packet, ba));
//		}
//	}
//	
//	
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface ClanTaskFinishCallback
//	{
//		void onCallback(int errCode, int ownerId, int serverId, int taskId);
//	}
//	public class ClanTaskFinishTask extends ClanServiceTask
//	{
//		int roleId;
//		int taskId;
//		int clanId;
//		Set<Integer> jydzs;
//		ClanTaskFinishCallback callback;
//		ClanTaskFinishTask(int roleId, int taskId, int clanId, Set<Integer> jydzs, ClanTaskFinishCallback callback)
//		{
//			this.roleId = roleId;
//			this.taskId = taskId;
//			this.clanId = clanId;
//			this.jydzs = jydzs;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanTaskFinishTask asClanTaskFinishTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int ownerId, int serverId, int taskId)
//		{
//			callback.onCallback(errCode, ownerId, serverId, taskId);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanTaskFinishTask(this.id, roleId, taskId, clanId, jydzs);
//		}
//	}
//	public void handleClanTaskFinishTaskResponse(int tag, int errCode, int ownerId, int serverId, int taskId)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanTaskFinishTask task = t.asClanTaskFinishTask();
//			if (task != null)
//				task.onCallback(errCode, ownerId, serverId, taskId);
//		}
//	}
//	void finishTask(int roleId, int taskId, int clanId, Set<Integer> jydzs, ClanTaskFinishCallback callback)
//	{
//		execTask(new ClanTaskFinishTask(roleId, taskId, clanId, jydzs, callback));
//	}
//	public void handleClanFinishTaskOwnerRewardForwardTaskResponse(int roleId, int xuantie, int caoyao)
//	{
//		Role roleOnline = gs.getLoginManager().getOnGameRole(roleId);
//		if(roleOnline != null)
//		{
//			roleOnline.syncAddOre(Clan.CLAN_ORE_TYPE_IRON, xuantie);
//			roleOnline.syncAddOre(Clan.CLAN_ORE_TYPE_HERB, caoyao);
//		}else{
//			gs.getLoginManager().exeCommonRoleVisitor(roleId, false, new LoginManager.CommonRoleVisitor()
//			{
//				@Override
//				public boolean visit(Role role, Role sameUserRole)
//				{
//					role.addClanOre(Clan.CLAN_ORE_TYPE_IRON, xuantie);
//					role.addClanOre(Clan.CLAN_ORE_TYPE_HERB, caoyao);
//					return true;
//				}
//				@Override
//				public void onCallback(boolean success)
//				{
//					
//				}
//			});	
//		}
//	}
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface ClanTaskDiscardCallback
//	{
//		void onCallback(int errCode, int taskId);
//	}
//	public class ClanTaskDiscardTask extends ClanServiceTask
//	{
//		int roleId;
//		int taskId;
//		int clanId;
//		int useDZ;
//		ClanTaskDiscardCallback callback;
//		ClanTaskDiscardTask(int roleId, int taskId, int clanId, int useDZ, ClanTaskDiscardCallback callback)
//		{
//			this.roleId = roleId;
//			this.taskId = taskId;
//			this.clanId = clanId;
//			this.useDZ = useDZ;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanTaskDiscardTask asClanTaskDiscardTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int taskId)
//		{
//			callback.onCallback(errCode, taskId);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanTaskDiscardTask(this.id, roleId, taskId, clanId, useDZ);
//		}
//	}
//	public void handleClanTaskDiscardTaskResponse(int tag, int errCode, int taskId)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanTaskDiscardTask task = t.asClanTaskDiscardTask();
//			if (task != null)
//				task.onCallback(errCode, taskId);
//		}
//	}
//	void discardTask(int roleId, int taskId, int clanId, int useDZ, ClanTaskDiscardCallback callback)
//	{
//		execTask(new ClanTaskDiscardTask(roleId, taskId, clanId, useDZ, callback));
//	}
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface ClanSyncHistoryCallback
//	{
//		void onCallback(int errCode, List<SBean.DBClanHistory> historys);
//	}
//	public class ClanSyncHistoryTask extends ClanServiceTask
//	{
//		int roleId;
//		int type;
//		int clanId;
//		ClanSyncHistoryCallback callback;
//		ClanSyncHistoryTask(int roleId, int type, int clanId, ClanSyncHistoryCallback callback)
//		{
//			this.roleId = roleId;
//			this.type = type;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanSyncHistoryTask asClanSyncHistoryTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.DBClanHistory> historys)
//		{
//			callback.onCallback(errCode, historys);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanSyncHistoryTask(this.id, roleId, type, clanId);
//		}
//	}
//	public void handleClanSyncHistoryTaskResponse(int tag, int errCode, List<SBean.DBClanHistory> historys)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanSyncHistoryTask task = t.asClanSyncHistoryTask();
//			if (task != null)
//				task.onCallback(errCode, historys);
//		}
//	}
//	void syncClanHistory(int roleId, int type, int clanId, ClanSyncHistoryCallback callback)
//	{
//		execTask(new ClanSyncHistoryTask(roleId, type, clanId, callback));
//	}
//	
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface RecoverGenDiscipleCallback
//	{
//		void onCallback(int errCode, int genDisciple);
//	}
//	public class RecoverGenDiscipleTask extends ClanServiceTask
//	{
//		int roleId;
//		RecoverGenDiscipleCallback callback;
//		RecoverGenDiscipleTask(int roleId, RecoverGenDiscipleCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public RecoverGenDiscipleTask asRecoverGenDiscipleTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int genDisciple)
//		{
//			callback.onCallback(errCode, genDisciple);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, -1);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyRecoverGenDiscipleTask(this.id, roleId);
//		}
//	}
//	public void handleRecoverGenDiscipleTaskResponse(int tag, int errCode, int genDisciple)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			RecoverGenDiscipleTask task = t.asRecoverGenDiscipleTask();
//			if (task != null)
//				task.onCallback(errCode, genDisciple);
//		}
//	}
//	void recoverGenDisciple(int roleId, RecoverGenDiscipleCallback callback)
//	{
//		execTask(new RecoverGenDiscipleTask(roleId, callback));
//	}
//	
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface ClanOreBuildUpLevelCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanOreBuildUpLevelTask extends ClanServiceTask
//	{
//		int roleId;
//		int type;
//		int level;
//		ClanOreBuildUpLevelCallback callback;
//		ClanOreBuildUpLevelTask(int roleId, int type, int level, ClanOreBuildUpLevelCallback callback)
//		{
//			this.roleId = roleId;
//			this.type = type;
//			this.level = level;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanOreBuildUpLevelTask asClanOreBuildUpLevelTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanOreBuildUpLevelTask(this.id, roleId, type, level);
//		}
//	}
//	public void handleClanOreBuildUpLevelTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanOreBuildUpLevelTask task = t.asClanOreBuildUpLevelTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanOreBuildUpLevel(int roleId, int type, int level, ClanOreBuildUpLevelCallback callback)
//	{
//		execTask(new ClanOreBuildUpLevelTask(roleId, type, level, callback));
//	}
//		
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface SyncClanOreCallback
//	{
//		void onCallback(int errCode, List<SBean.DBClanOre> ores);
//	}
//	public class SyncClanOreTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		SyncClanOreCallback callback;
//		SyncClanOreTask(int roleId, int clanId, SyncClanOreCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public SyncClanOreTask asSyncClanOreTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.DBClanOre> ores)
//		{
//			callback.onCallback(errCode, ores);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifySyncClanOreTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleSyncClanOreTaskResponse(int tag, int errCode, List<SBean.DBClanOre> ores)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			SyncClanOreTask task = t.asSyncClanOreTask();
//			if (task != null)
//				task.onCallback(errCode, ores);
//		}
//	}
//	void syncClanOre(int roleId, int clanId, SyncClanOreCallback callback)
//	{
//		execTask(new SyncClanOreTask(roleId, clanId, callback));
//	}
//
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface ClanOreOccupyCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanOreOccupyTask extends ClanServiceTask
//	{
//		int roleId;
//		int type;
//		int clanId;
//		ClanOreOccupyCallback callback;
//		ClanOreOccupyTask(int roleId, int type, int clanId, ClanOreOccupyCallback callback)
//		{
//			this.roleId = roleId;
//			this.type = type;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanOreOccupyTask asClanOreOccupyTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanOreOccupyTask(this.id, roleId, type, clanId);
//		}
//	}
//	public void handleClanOreOccupyTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanOreOccupyTask task = t.asClanOreOccupyTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanOreOccupy(int roleId, int type, int clanId, ClanOreOccupyCallback callback)
//	{
//		execTask(new ClanOreOccupyTask(roleId, type, clanId, callback));
//	}
//	
//	
//	
//	//------------------------------------------------------------------------------------
//	public interface ClanOreOccupyFinishCallback
//	{
//		void onCallback(int errCode, int clanLevel);
//	}
//	public class ClanOreOccupyFinishTask extends ClanServiceTask
//	{
//		int roleId;
//		int type;
//		int clanId;
//		ClanOreOccupyFinishCallback callback;
//		ClanOreOccupyFinishTask(int roleId, int type, int clanId, ClanOreOccupyFinishCallback callback)
//		{
//			this.roleId = roleId;
//			this.type = type;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanOreOccupyFinishTask asClanOreOccupyFinishTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int clanLevel)
//		{
//			callback.onCallback(errCode, clanLevel);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, -1);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanOreOccupyFinishTask(this.id, roleId, type, clanId);
//		}
//	}
//	public void handleClanOreOccupyFinishTaskResponse(int tag, int errCode, int clanLevel)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanOreOccupyFinishTask task = t.asClanOreOccupyFinishTask();
//			if (task != null)
//				task.onCallback(errCode, clanLevel);
//		}
//	}
//	void clanOreOccupyFinish(int roleId, int type, int clanId, ClanOreOccupyFinishCallback callback)
//	{
//		execTask(new ClanOreOccupyFinishTask(roleId, type, clanId, callback));
//	}
//		
//		
//	
//	
//	//
//	public interface ClanSearchOreCallback
//	{
//		void onCallback(int errCode, SBean.DBOreRobTeamGlobal ore);
//	}
//	public class ClanSearchOreTask extends ClanServiceTask
//	{
//		int roleId;
//		int level;
//		ClanSearchOreCallback callback;
//		ClanSearchOreTask(int roleId, int level, ClanSearchOreCallback callback)
//		{
//			this.roleId = roleId;
//			this.level = level;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanSearchOreTask asClanSearchOreTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.DBOreRobTeamGlobal ore)
//		{
//			callback.onCallback(errCode, ore);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanSearchOreTask(this.id, roleId, level);
//		}
//	}
//	public void handleClanSearchOreTaskResponse(int tag, int errCode, SBean.DBOreRobTeamGlobal ore)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanSearchOreTask task = t.asClanSearchOreTask();
//			if (task != null)
//				task.onCallback(errCode, ore);
//		}
//	}
//	void clanSearchOre(int roleId, int level, ClanSearchOreCallback callback)
//	{
//		execTask(new ClanSearchOreTask(roleId, level, callback));
//	}
//	
//	//
//	public void handleClanSearchOreForwardTaskResponse(int level, int tag, int serverId, int oreServerId)
//	{
//		Integer roleId = gs.getLoginManager().getRandomLevelRole(level - 1, level + 1);
//		if(roleId != null)
//		{
//			final Role role = gs.getLoginManager().getOnGameRole(roleId);
//			if(role != null)
//			{
//				//在线处理
//				SBean.DBOreRobTeamGlobal ore = null;
//				if(!role.clanData.occupyOres.isEmpty())
//				{
//					ore = role.getOreRobTeamGlobal();
//					ore.serverId = oreServerId;
//				}
//				gs.getRPCManager().notifyClanSearchOreForwardTask(tag, serverId, oreServerId, ore);
//			}else{
//				//不在线处理
//				gs.getLoginManager().getRoleClanOre(roleId, ore -> {
//                    if(ore != null)
//                    {
//                        ore.serverId = oreServerId;
//                    }
//                    gs.getRPCManager().notifyClanSearchOreForwardTask(tag, serverId, oreServerId, ore);
//                });
//			}
//		}
//	}
//	
//	
//	//获取搜索到的矿信息
//	public interface ClanSearchOreSyncCallback
//	{
//		void onCallback(int errCode, List<SBean.DBClanPetInfo> pets);
//	}
//	public class ClanSearchOreSyncTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		int memberId;
//		int serverId;
//		int oreType;
//		ClanSearchOreSyncCallback callback;
//		ClanSearchOreSyncTask(int roleId, int clanId, int memberId, int serverId, int oreType, ClanSearchOreSyncCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.memberId = memberId;
//			this.serverId = serverId;
//			this.callback = callback;
//			this.oreType = oreType;
//		}
//		
//		@Override
//		public ClanSearchOreSyncTask asClanSearchOreSyncTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.DBClanPetInfo> pets)
//		{
//			callback.onCallback(errCode, pets);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanSearchOreSyncTask(this.id, roleId, clanId, memberId, serverId, oreType);
//		}
//	}
//	public void handleClanSearchOreSyncTaskResponse(int tag, int errCode, List<SBean.DBClanPetInfo> pets)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanSearchOreSyncTask task = t.asClanSearchOreSyncTask();
//			if (task != null)
//				task.onCallback(errCode, pets);
//		}
//	}
//	void clanSearchSyncOre(int roleId, int clanId, int memberId, int serverId, int oreType, ClanSearchOreSyncCallback callback)
//	{
//		execTask(new ClanSearchOreSyncTask(roleId, clanId, memberId, serverId, oreType, callback));
//	}
//	
//	
//	//获取宗主的佣兵，
//	public interface ClanOreOwnerPetSyncCallback
//	{
//		void onCallback(int errCode, int roleId, List<PetOverview> pets);
//	}
//	public class ClanOreOwnerPetSyncTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		ClanOreOwnerPetSyncCallback callback;
//		ClanOreOwnerPetSyncTask(int roleId, int clanId, ClanOreOwnerPetSyncCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanOreOwnerPetSyncTask asClanOreOwnerPetSyncTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int roleId, List<PetOverview> pets)
//		{
//			callback.onCallback(errCode, roleId, pets);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, -1, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanOreOwnerPetSyncTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleClanOreOwnerPetSyncTaskResponse(int tag, int errCode, int roleId, List<PetOverview> pets)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanOreOwnerPetSyncTask task = t.asClanOreOwnerPetSyncTask();
//			if (task != null)
//				task.onCallback(errCode, roleId, pets);
//		}
//	}
//	void clanOwnerPetSync(int roleId, int clanId, ClanOreOwnerPetSyncCallback callback)
//	{
//		execTask(new ClanOreOwnerPetSyncTask(roleId, clanId, callback));
//	}
//	public void notifyClanOreOwnerPetSyncForwardResponse(Packet.Clan2S.ClanOreOwnerPetSyncForwardReq packet)
//	{
//		final ClanCFGS clanCFGS = GameData.getInstance().getClanCFGS();
//		Role role = gs.getLoginManager().getOnGameRole(packet.getOwner().roleid);
//		if (role != null)
//		{
//			List<SBean.PetOverview> petList = new ArrayList<SBean.PetOverview>();
//			synchronized(role)
//			{
//				role.activePets.values().stream().filter(pet -> pet.fightPet.level > clanCFGS.occupyOre.petLevel).forEach(pet -> petList.add(role.getPetOverviewWithoutLock(pet.fightPet.id)));
////				for (Map.Entry<Integer, SBean.DBPet> pet : role.activePets.entrySet())
////				{
////					if(pet.getValue().fightPet.level >= clanCFGS.occupyOre.petLevel)
////					{
////						petList.add(role.getPetOverviewWithoutLock(pet.getValue().id));
////					}
////				}
//			}
//			gs.getRPCManager().notifyClanOreOwnerPetSyncForwardTask(packet.getTagId(), packet.getSelf(), packet.getOwner(), petList);
//		}
//		else
//		{
//			gs.getLoginManager().getRoleAndPetOverview(packet.getOwner().roleid, new LoginManager.GetRoleAndPetOverviewCallback()
//			{
//				@Override
//				public void onCallback(RoleOverview overview, List<PetOverview> petOverviews) {
//					List<SBean.PetOverview> petList = new ArrayList<SBean.PetOverview>();
//					for (SBean.PetOverview pet : petOverviews)
//					{
//						if(pet.level >= clanCFGS.occupyOre.petLevel)
//						{
//							petList.add(pet);
//						}
//					}
//					gs.getRPCManager().notifyClanOreOwnerPetSyncForwardTask(packet.getTagId(), packet.getSelf(), packet.getOwner(), petList);
//				}
//			});
//		}
//	}
//	
//	
//	//借佣兵
//	public interface ClanOreBorrowPetCallback
//	{
//		void onCallback(int errCode, int serverId, int roleId);
//	}
//	public class ClanOreBorrowPetTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		int petId;
//		ClanOreBorrowPetCallback callback;
//		ClanOreBorrowPetTask(int roleId, int clanId, int petId, ClanOreBorrowPetCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.petId = petId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanOreBorrowPetTask asClanOreBorrowPetTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int serverId, int roleId)
//		{
//			callback.onCallback(errCode, serverId, roleId);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, -1, -1);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanOreBorrowPetTask(this.id, roleId, clanId, petId);
//		}
//	}
//	public void handleClanBorrowPetTaskResponse(int tag, int errCode, int serverId, int roleId)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanOreBorrowPetTask task = t.asClanOreBorrowPetTask();
//			if (task != null)
//				task.onCallback(errCode, serverId, roleId);
//		}
//	}
//	void clanOreBorrowPet(int roleId, int clanId, int petId, ClanOreBorrowPetCallback callback)
//	{
//		execTask(new ClanOreBorrowPetTask(roleId, clanId, petId, callback));
//	}
//	public void notifyClanOreBorrowPetForwardResponse(Packet.Clan2S.ClanOreBorrowPetForwardReq packet)
//	{
//		final ClanCFGS clanCFGS = GameData.getInstance().getClanCFGS();
//		Role role = gs.getLoginManager().getOnGameRole(packet.getOwner().roleid);
//		if (role != null)
//		{
//			int result = GameData.PROTOCOL_OP_SUCCESS;
//			synchronized(role)
//			{
//				SBean.DBPet pet = role.activePets.get(packet.getPetId());
//				if (pet == null || pet.fightPet.level < clanCFGS.occupyOre.petLevel)
//				{
//					result = GameData.PROTOCOL_OP_FAILED;
//				}
//			}
//			gs.getRPCManager().notifyClanOreBorrowPetForwardTask(packet.getTagId(), packet.getSelf(), packet.getOwner(), result);
//		}else
//		{
//			gs.getLoginManager().getRoleAndPetOverview(packet.getOwner().roleid,  new LoginManager.GetRoleAndPetOverviewCallback()
//			{
//				@Override
//				public void onCallback(RoleOverview overview, List<PetOverview> petOverviews)
//				{
//					int result = GameData.PROTOCOL_OP_FAILED;
//					if(petOverviews.size() > 0)
//					{
//						int petId = packet.getPetId();
//						for(SBean.PetOverview petOverview : petOverviews)
//						{
//							if(petOverview.id == petId && petOverview.level >= clanCFGS.occupyOre.petLevel)
//							{
//								result = GameData.PROTOCOL_OP_SUCCESS;
//								break;
//							}
//						}
//					}
//					
//					gs.getRPCManager().notifyClanOreBorrowPetForwardTask(packet.getTagId(), packet.getSelf(), packet.getOwner(), result);
//				}
//			});
//		}
//	}
//	
//		
//	//---------------------------------------------------------------------------------	
//	public interface ClanOreHarryCallback
//	{
//		void onCallback(int errCode, int owner, int oreLevel, BattleArray ba);
//	}
//	public class ClanOreHarryTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		int memberId;
//		int serverId;
//		int oreType;
//		ClanOreHarryCallback callback;
//		ClanOreHarryTask(int roleId, int clanId, int memberId, int serverId, int oreType, ClanOreHarryCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.memberId = memberId;
//			this.serverId = serverId;
//			this.oreType = oreType;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanOreHarryTask asClanOreHarryTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int owner, int oreLevel, BattleArray ba)
//		{
//			callback.onCallback(errCode, owner, oreLevel, ba);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanOreHarryTask(this.id, roleId, clanId, memberId, serverId, oreType);
//		}
//	}
//	public void handleClanOreHarryTaskResponse(int tag, int errCode, int owner, int oreLevel, BattleArray ba)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanOreHarryTask task = t.asClanOreHarryTask();
//			if (task != null)
//				task.onCallback(errCode, owner, oreLevel, ba);
//		}
//	}
//	void clanOreHarry(int roleId, int clanId, int serverId, int memberId, int oreType, ClanOreHarryCallback callback)
//	{
//		execTask(new ClanOreHarryTask(roleId, clanId, memberId, serverId, oreType, callback));
//	}	
//	public void handleClanOreHarryForwardTaskResponse(Packet.Clan2S.ClanOreHarryForwardReq packet)
//	{
//		gs.getLoginManager().getClanOreHarryEnemyFight(packet.getEnemy().roleid, packet.getClanId(), packet.getOreType(), new LoginManager.GetClanOreHarryEnemyFightCallback()
//		{
//			@Override
//			public void onCallback(BattleArray ba)
//			{
//				gs.getRPCManager().notifyClanOreHarryForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy(), ba);
//			}
//		});
//	}
//	
//	
//	//----------------------------------------购买行动力-----------------------------------------	
//	public interface ClanBuyDoPowerCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanBuyDoPowerTask extends ClanServiceTask
//	{
//		int roleId;
//		int level;
//		ClanBuyDoPowerCallback callback;
//		ClanBuyDoPowerTask(int roleId, int level, ClanBuyDoPowerCallback callback)
//		{
//			this.roleId = roleId;
//			this.level = level;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBuyDoPowerTask asClanBuyDoPowerTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBuyDoPowerTask(this.id, roleId, level);
//		}
//	}
//	public void handleClanBuyDoPowerTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBuyDoPowerTask task = t.asClanBuyDoPowerTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanBuyDoPower(int roleId, int level, ClanBuyDoPowerCallback callback)
//	{
//		execTask(new ClanBuyDoPowerTask(roleId, level, callback));
//	}
//	
//	
//	//----------------------------------------获取精英弟子-----------------------------------------	
//	public interface ClanGetEliteDiscipleCallback
//	{
//		void onCallback(int errCode, List<SBean.DBClanEliteDisciple> eds);
//	}
//	public class ClanGetEliteDiscipleTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		ClanGetEliteDiscipleCallback callback;
//		ClanGetEliteDiscipleTask(int roleId, int clanId, ClanGetEliteDiscipleCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanGetEliteDiscipleTask asClanGetEliteDiscipleTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.DBClanEliteDisciple> eds)
//		{
//			callback.onCallback(errCode, eds);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanGetEliteDiscipleTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleClanGetEliteDiscipleTaskResponse(int tag, int errCode, List<SBean.DBClanEliteDisciple> eds)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanGetEliteDiscipleTask task = t.asClanGetEliteDiscipleTask();
//			if (task != null)
//				task.onCallback(errCode, eds);
//		}
//	}
//	void clanGetEliteDisciple(int roleId, int clanId, ClanGetEliteDiscipleCallback callback)
//	{
//		execTask(new ClanGetEliteDiscipleTask(roleId, clanId, callback));
//	}
//	
//	
//	
//	
//	
//	
//		
//	
//	/////////////////////////////////宗门战//////////////////////////////////////
//	public interface ClanGetBaseRankCallback
//	{
//		void onCallback(int errCode, List<SBean.ClanRankBaseInfo> ranks);
//	}
//	public class ClanGetBaseRankTask extends ClanServiceTask
//	{
//		int roleId;
//		int gType;
//		int clanId;
//		int rankType;
//		ClanGetBaseRankCallback callback;
//		ClanGetBaseRankTask(int roleId, int gType, int clanId, int rankType, ClanGetBaseRankCallback callback)
//		{
//			this.roleId = roleId;
//			this.gType = gType;
//			this.clanId = clanId;
//			this.rankType = rankType;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanGetBaseRankTask asClanGetBaseRankTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.ClanRankBaseInfo> ranks)
//		{
//			callback.onCallback(errCode, ranks);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanGetBaseRankTask(this.id, roleId, gType, clanId, rankType);
//		}
//	}
//	public void handleClanGetBaseRankTaskResponse(int tag, int errCode, List<SBean.ClanRankBaseInfo> ranks)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanGetBaseRankTask task = t.asClanGetBaseRankTask();
//			if (task != null)
//				task.onCallback(errCode, ranks);
//		}
//	}
//	void getClanBaseRank(int roleId, int gType, int clanId, int rankType, ClanGetBaseRankCallback callback)
//	{
//		execTask(new ClanGetBaseRankTask(roleId, gType, clanId, rankType, callback));
//	}
//	
//	//修改宗门排行榜
//	public interface ClanModifyRankCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanModifyRankTask extends ClanServiceTask
//	{
//		int roleId;
//		int level;
//		int fightPower;
//		int charm;
//		ClanModifyRankCallback callback;
//		ClanModifyRankTask(int roleId, int level, int fightPower, int charm, ClanModifyRankCallback callback)
//		{
//			this.roleId = roleId;
//			this.level = level;
//			this.fightPower = fightPower;
//			this.charm = charm;
//			this.callback = callback;
//		}
//		
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		@Override
//		public ClanModifyRankTask asClanModifyRankTask()
//		{
//			return this;
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanModifyRankTask(this.id, roleId, level, fightPower, charm);
//		}
//	}
//	public void handleClanModefyRankTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanModifyRankTask task = t.asClanModifyRankTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void modifyClanRank(int roleId, int level, int fightPower, int charm, ClanModifyRankCallback callback)
//	{
//		execTask(new ClanModifyRankTask(roleId, level, fightPower, charm, callback));
//	}
//	
//	
//	//	
//	public interface ClanSetAttackTeamCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanSetAttackTeamTask extends ClanServiceTask
//	{
//		int roleId;
//		Map<Integer, Integer> pets;
//		ClanSetAttackTeamCallback callback;
//		ClanSetAttackTeamTask(int roleId, Map<Integer, Integer> pets, ClanSetAttackTeamCallback callback)
//		{
//			this.roleId = roleId;
//			this.pets = pets;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanSetAttackTeamTask asClanSetAttackTeamTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanSetAttackTeamTask(this.id, roleId, pets);
//		}
//	}
//	public void handleClanSetAttackTeamTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanSetAttackTeamTask task = t.asClanSetAttackTeamTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void setAttackTeam(int roleId, Map<Integer, Integer> pets, ClanSetAttackTeamCallback callback)
//	{
//		execTask(new ClanSetAttackTeamTask(roleId, pets, callback));
//	}
//
//	
//	
//	public interface ClanSetDefendTeamCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanSetDefendTeamTask extends ClanServiceTask
//	{
//		int roleId;
//		Map<Integer, Integer> pets;
//		ClanSetDefendTeamCallback callback;
//		ClanSetDefendTeamTask(int roleId, Map<Integer, Integer> pets, ClanSetDefendTeamCallback callback)
//		{
//			this.roleId = roleId;
//			this.pets = pets;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanSetDefendTeamTask asClanSetDefendTeamTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanSetDefendTeamTask(this.id, roleId, pets);
//		}
//	}
//	public void handleClanSetDefendTeamTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanSetDefendTeamTask task = t.asClanSetDefendTeamTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void setDefendTeam(int roleId, Map<Integer, Integer> pets, ClanSetDefendTeamCallback callback)
//	{
//		execTask(new ClanSetDefendTeamTask(roleId, pets, callback));
//	}
//
//	
//	
//	public interface ClanFindEnemyCallback
//	{
//		void onCallback(int errCode,List<SBean.ClanBattleInfoRes> clans);
//	}
//	public class ClanFindEnemyTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		ClanFindEnemyCallback callback;
//		ClanFindEnemyTask(int roleId, int clanId, ClanFindEnemyCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanFindEnemyTask asClanFindEnemyTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.ClanBattleInfoRes> clans)
//		{
//			callback.onCallback(errCode, clans);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanFindEnemyTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleClanFindEnemyTaskResponse(int tag, int errCode, List<SBean.ClanBattleInfoRes> clans)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanFindEnemyTask task = t.asClanFindEnemyTask();
//			if (task != null)
//				task.onCallback(errCode, clans);
//		}
//	}
//	void clanFindEnemy(int roleId, int clanId, ClanFindEnemyCallback callback)
//	{
//		execTask(new ClanFindEnemyTask(roleId, clanId, callback));
//	}
//	//
//	
//	public interface GetNearbyClanCallback
//	{
//		void onCallback(int errCode, SBean.ClanBattleInfoRes selfClan, SBean.ClanBattleInfoRes enemyClan);
//	}
//	public class GetNearbyClanTask extends ClanServiceTask
//	{
//		int roleId;
//		GetNearbyClanCallback callback;
//		GetNearbyClanTask(int roleId, GetNearbyClanCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public GetNearbyClanTask asGetNearbyClanTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanBattleInfoRes selfClan, SBean.ClanBattleInfoRes enemyClan)
//		{
//			callback.onCallback(errCode, selfClan, enemyClan);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyGetNearbyClanTask(this.id, roleId);
//		}
//	}
//	public void handleGetNearbyClanTaskResponse(int tag, int errCode, SBean.ClanBattleInfoRes selfClan, SBean.ClanBattleInfoRes enemyClan)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			GetNearbyClanTask task = t.asGetNearbyClanTask();
//			if (task != null)
//				task.onCallback(errCode, selfClan, enemyClan);
//		}
//	}
//	void getNearbyClan(int roleId, GetNearbyClanCallback callback)
//	{
//		execTask(new GetNearbyClanTask(roleId, callback));
//	}
//
//	
//	
//	public interface ClanGetEnemyCallback
//	{
//		void onCallback(int errCode, int ownerId, List<SBean.PetOverview> enemyPets, SBean.RoleOverview overview);
//	}
//	public class ClanGetEnemyTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		ClanGetEnemyCallback callback;
//		ClanGetEnemyTask(int roleId, int clanId, ClanGetEnemyCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanGetEnemyTask asClanGetEnemyTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int ownerId, List<SBean.PetOverview> enemyPets, SBean.RoleOverview overview)
//		{
//			callback.onCallback(errCode, ownerId, enemyPets, overview);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanGetEnemyTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleClanGetEnemyTaskResponse(int tag, int errCode, int ownerId, List<SBean.PetOverview> enemyPets, SBean.RoleOverview overview)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanGetEnemyTask task = t.asClanGetEnemyTask();
//			if (task != null)
//				task.onCallback(errCode, ownerId, enemyPets, overview);
//		}
//	}
//	void clanGetEnemy(int roleId, int clanId, ClanGetEnemyCallback callback)
//	{
//		execTask(new ClanGetEnemyTask(roleId, clanId, callback));
//	}
//	public void handleClanGetEnemyForwardTaskResponse(Packet.Clan2S.ClanGetEnemyForwardReq packet)
//	{
//		gs.getLoginManager().getClanBattleEnemy(packet.getEnemy().roleid, new LoginManager.GetClanBattleEnemyCallback() {
//			@Override
//			public void onCallback(List<SBean.PetOverview> enemyPets, SBean.RoleOverview overview)
//			{
//				gs.getRPCManager().notifyClanGetEnemyForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy(), overview, enemyPets);
//			}
//		});
//	}
//
//	
//	
//	//
//	public interface ClanBattleAttackCallback
//	{
//		void onCallback(int errCode, int cdTime, SBean.ClanBattleInfo self, SBean.ClanBattleInfo enemy, List<Integer> value, List<PetOverview> pets);
//	}
//	public class ClanBattleAttackTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		Map<Integer, SBean.PetOverview> pets;
//		int roleLvl;
//		int transform;
//		ClanBattleAttackCallback callback;
//		ClanBattleAttackTask(int roleId, int clanId, Map<Integer, SBean.PetOverview> pets, int roleLvl, int transform, ClanBattleAttackCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.pets = pets;
//			this.roleLvl = roleLvl;
//			this.transform = transform;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleAttackTask asClanBattleAttackTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int cdTime, SBean.ClanBattleInfo self, SBean.ClanBattleInfo enemy, List<Integer> value, List<PetOverview> pets)
//		{
//			callback.onCallback(errCode, cdTime, self, enemy, value, pets);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, null, null, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleAttackTask(this.id, roleId, clanId, pets, roleLvl, transform);
//		}
//	}
//	public void handleClanBattleAttackTaskResponse(int tag, int errCode, int cdTime, SBean.ClanBattleInfo self, SBean.ClanBattleInfo enemy, List<Integer> value, List<PetOverview> pets)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleAttackTask task = t.asClanBattleAttackTask();
//			if (task != null)
//				task.onCallback(errCode, cdTime, self, enemy, value, pets);
//		}
//	}
//	void clanBattleAttack(int roleId, int clanId, Map<Integer, SBean.PetOverview> pets, int roleLvl, int transform, ClanBattleAttackCallback callback)
//	{
//		execTask(new ClanBattleAttackTask(roleId, clanId, pets, roleLvl, transform, callback));
//	}
//	// 设置被攻击方的role
//	public void handleClanBattleAttackForwardTaskResponse(Packet.Clan2S.ClanBattleAttackForwardReq packet)
//	{
//		SBean.ClanCFGS clanCFGS = GameData.getInstance().getClanCFGS();
//		List<SBean.PetOverview> selfPets = new ArrayList<>();
//		// 在线或者不在线都通过这个来处理
//		gs.getLoginManager().exeCommonRoleVisitor(packet.getEnemy().roleid, false, new LoginManager.CommonRoleVisitor()
//		{
//			@Override
//			public synchronized boolean visit(Role role, Role sameUserRole)
//			{	
//				if(role != null)
//				{
//					SBean.ClanCFGS cfg = GameData.getInstance().getClanCFGS();
//					int xuantie = role.clanData.data.xuantie; 
//					int yaocao = role.clanData.data.yaocao;
//					if(role.clanData.clanBattleData == null)
//						role.initClanBattleData();
//					int attackClanId = packet.getAttack().clanId;
//	//				int attackClanLvl = packet.getAttack().lvl;
//					int defendClanLvl = packet.getDefend().lvl;
//					int upLvlNeedPrestige = cfg.clanLevels.get(defendClanLvl).shengwang;
//					SBean.ClanBattleDefendDesc desc = new SBean.ClanBattleDefendDesc();
//					desc.prestige = (int) Math.ceil(upLvlNeedPrestige * cfg.normalBattle.failRatePrestige);
//					desc.xuantie = (int) Math.ceil(xuantie * clanCFGS.normalBattle.failRateXuantie);
//					desc.yaocao = (int) Math.ceil(yaocao * clanCFGS.normalBattle.failRateyaocao);
//					desc.attackTime = GameTime.getTime();
//					desc.cdTime = packet.getCdTime();
//					desc.defendInfo = packet.getAttack();//攻击我的人的信息
//					desc.enemyPets = packet.getPets();
//					desc.isKeek = 0;
//					desc.isSeekhelp = 0;
//					desc.roleLvl = packet.getRoleLvl();
//					desc.transform = packet.getTransform(); // 攻击我的人的转职信息
//					
//					if(role.clanData.clanBattleData.defend == null)
//					{
//						role.clanData.clanBattleData.defend = new HashMap<Integer,SBean.ClanBattleDefendDesc>();
//					}
//					role.clanData.clanBattleData.defend.put(attackClanId, desc);
//					
//					
//					for(int petId : role.clanData.battle.defendPet.values())
//					{
//						selfPets.add(role.getPetOverviewWithoutLock(petId));
//					}
//					gs.getRPCManager().notifyClanBattleAttackForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy(), 
//							role.clanData.data.xuantie, role.clanData.data.yaocao, packet.getCdTime(), role.clanData.battle.defendPet, selfPets);
//					return true;
//				}
//				gs.getRPCManager().notifyClanBattleAttackForwardTask(packet.getTagId(), null, null, 0, 0, 0, null, null);
//				return false;
//			}
//			
//			@Override
//			public void onCallback(boolean success)
//			{
//				
//			}
//		});
//		
//	}
//		
//	
//	public interface ClanBattleSeekhelpCallback
//	{
//		void onCallback(int errCode, List<Integer> roles, SBean.ClanBattleInfo selfInfo);
//	}
//	public class ClanBattleSeekhelpTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanBattleSeekhelpCallback callback;
//		ClanBattleSeekhelpTask(int roleId, ClanBattleSeekhelpCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleSeekhelpTask asClanBattleSeekhelpTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<Integer> roles,SBean.ClanBattleInfo selfInfo)
//		{
//			callback.onCallback(errCode, roles, selfInfo);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100,null,null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleSeekhelpTask(this.id, roleId);
//		}
//	}
//	public void handleClanBattleSeekhelpTaskResponse(int tag, int errCode, List<Integer> roles, SBean.ClanBattleInfo selfInfo)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleSeekhelpTask task = t.asClanBattleSeekhelpTask();
//			if (task != null)
//				task.onCallback(errCode, roles, selfInfo);
//		}
//	}
//	void clanBattleSeekhelp(int roleId, ClanBattleSeekhelpCallback callback)
//	{
//		execTask(new ClanBattleSeekhelpTask(roleId, callback));
//	}
//	
//	
//	
//	
//	public interface ClanBattleHelpCallback
//	{
//		void onCallback(int errCode, SBean.ClanBattleInfo attack, SBean.ClanBattleInfo defend);
//	}
//	public class ClanBattleHelpTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		Map<Integer, Integer> pets;
//		ClanBattleHelpCallback callback;
//		ClanBattleHelpTask(int roleId, int clanId, Map<Integer, Integer> pets, ClanBattleHelpCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.pets = pets;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleHelpTask asClanBattleHelpTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.ClanBattleInfo attack, SBean.ClanBattleInfo defend)
//		{
//			callback.onCallback(errCode, attack, defend);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleHelpTask(this.id, roleId, clanId, pets);
//		}
//	}
//	public void handleClanBattleHelpTaskResponse(int tag, int errCode, SBean.ClanBattleInfo attack, SBean.ClanBattleInfo defend)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleHelpTask task = t.asClanBattleHelpTask();
//			if (task != null)
//				task.onCallback(errCode, attack, defend);
//		}
//	}
//	void clanBattleHelp(int roleId, int clanId, Map<Integer, Integer> pets, ClanBattleHelpCallback callback)
//	{
//		execTask(new ClanBattleHelpTask(roleId, clanId, pets, callback));
//	}
//	public void handleClanBattleHelpForwardTaskResponse(Packet.Clan2S.ClanBattleHelpForwardReq packet)
//	{
//		final Role role = gs.getLoginManager().getOnGameRole(packet.getEnemy().roleid);
////		if(role != null)
////		{
////			synchronized(role)
////			{
////				//在线处理
////				if(role.clanData.clanBattleData.battleType != Clan.CLAN_BATTLE_TYPE_ATTACK || 
////						!role.clanData.clanBattleData.seekRoles.contains(packet.getSelf().roleid))
////				{
////					gs.getRPCManager().notifyClanBattleHelpForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_FAILED, 
////							packet.getSelf(), packet.getEnemy(), null, null);
////					return;
////				}
////				if(role.clanData.clanBattleData.help != null)
////				{
////					gs.getRPCManager().notifyClanBattleHelpForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_CLAN_BATTLE_HELP_LATE,
////							packet.getSelf(), packet.getEnemy(), null, null);
////					return;
////				}
////				role.clanData.clanBattleData.help = packet.getHelp();
////				//计算失败后损失的资源
////			}
////			gs.getRPCManager().notifyClanBattleHelpForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_SUCCESS, packet.getSelf(), packet.getEnemy(), 
////					role.clanData.clanBattleData.attack, role.clanData.clanBattleData.defend);
////			
////		}else{
////			//不在线 失败，不可以战斗
////			gs.getRPCManager().notifyClanBattleHelpForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_CLAN_BATTLE_HELP_OFFLINE,
////					packet.getSelf(), packet.getEnemy(), null, null);
////		}
//	}
//	
//	//宗门支援战开始
//	public interface ClanBattleHelpFightStartCallback
//	{
//		void onCallback(int errCode, SBean.BattleArray ba, SBean.ClanBattleInfo selfInfo);
//	}
//	public class ClanBattleHelpFightStartTask extends ClanServiceTask
//	{
//		int roleId;
//		SBean.GlobalRoleId helpRole;
//		int clanID;
//		ClanBattleHelpFightStartCallback callback;
//		ClanBattleHelpFightStartTask(int roleId, SBean.GlobalRoleId helpRole, int clanID, ClanBattleHelpFightStartCallback callback)
//		{
//			this.roleId = roleId;
//			this.helpRole = helpRole;
//			this.clanID = clanID;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleHelpFightStartTask asClanBattleHelpFightStartTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.BattleArray ba, SBean.ClanBattleInfo selfInfo)
//		{
//			callback.onCallback(errCode, ba, selfInfo);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleHelpFightStartTask(this.id, roleId, clanID, helpRole);
//		}
//	}
//	public void handleClanBattleHelpFightStartTaskResponse(int tag, int errCode, SBean.BattleArray ba, SBean.ClanBattleInfo selfInfo)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleHelpFightStartTask task = t.asClanBattleHelpFightStartTask();
//			if (task != null)
//				task.onCallback(errCode, ba, selfInfo);
//		}
//	}
//	void clanBattleHelpFightStart(int roleId, SBean.GlobalRoleId helpRole, int clanID, ClanBattleHelpFightStartCallback callback)
//	{
//		execTask(new ClanBattleHelpFightStartTask(roleId, helpRole, clanID, callback));
//	}
//	//支援战，通知攻击者,添加helpClanInfo。否则战报那边特麻烦
//	public void handleClanBattleHelpFightStartForwardTaskResponse(Packet.Clan2S.ClanBattleHelpFightStartForwardReq packet)
//	{
//		gs.getLoginManager().exeCommonRoleVisitor(packet.getEnemy().roleid, false, new LoginManager.CommonRoleVisitor()
//		{
//			@Override
//			public synchronized boolean visit(Role role, Role sameUserRole)
//			{
//				if(role.clanData.clanBattleData == null || role.clanData.clanBattleData.helpClan != 0 )//有人支援了
//				{
//					SBean.BattleArray battleArray = role.getClanBattleMapAttackBattleArray();
//					gs.getRPCManager().notifyClanBattleHelpFightStartForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_CLAN_BATTLE_HELPED,
//								packet.getSelf(), packet.getEnemy(), battleArray, packet.getSelfInfo());
//					return false;
//				}
//				if(role.clanData.clanBattleData.battleType == 0)//撤退了
//				{
//					SBean.BattleArray battleArray = role.getClanBattleMapAttackBattleArray();
//					gs.getRPCManager().notifyClanBattleHelpFightStartForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_CLAN_BATTLE_CANCEL,
//								packet.getSelf(), packet.getEnemy(), battleArray, packet.getSelfInfo());
//					return false;
//				}
//				role.clanData.clanBattleData.helpClan = packet.getClanID();//此次攻击，我的宗门ID，标志有人响应了支援
//				role.clanData.clanBattleData.helpClanInfo = packet.getSelfInfo();
//				SBean.BattleArray battleArray = role.getClanBattleMapAttackBattleArray();
//				gs.getRPCManager().notifyClanBattleHelpFightStartForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_SUCCESS,
//							packet.getSelf(), packet.getEnemy(), battleArray, packet.getSelfInfo());
//				return true;
//			}
//
//			@Override
//			public void onCallback(boolean success) {
//				
//			}
//		});
//		
//	}
//	//支援战结束
//	public interface ClanBattleHelpFightEndCallback
//	{
//		void onCallback(int errCode, int pre);
//	}
//	public class ClanBattleHelpFightEndTask extends ClanServiceTask
//	{
//		int roleId;
//		int value;
//		int selfClanId;
//		int helpClanId;
//		int defendClanId;
//		int defendGsId;
//		int win;
//		SBean.BattleArrayHp defend;
//		ClanBattleHelpFightEndCallback callback;
//		ClanBattleHelpFightEndTask(int roleId, int value, int selfClanId, int helpClanId,int defendClanId,int defendGsId, int win, SBean.BattleArrayHp defend, ClanBattleHelpFightEndCallback callback)
//		{
//			this.roleId = roleId;
//			this.value = value;
//			this.selfClanId = selfClanId;
//			this.helpClanId = helpClanId;
//			this.defendClanId = defendClanId;
//			this.win = win;
//			this.defend = defend;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleHelpFightEndTask asClanBattleHelpFightEndTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int pre)
//		{
//			callback.onCallback(errCode, pre);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleHelpFightEndTask(this.id, roleId, selfClanId, helpClanId, defendClanId, defendGsId, value, win, defend);
//		}
//	}
//	public void handleClanBattleHelpFightEndTaskResponse(int tag, int errCode,int pre)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleHelpFightEndTask task = t.asClanBattleHelpFightEndTask();
//			if (task != null)
//				task.onCallback(errCode, pre);
//		}
//	}
//	void clanBattleHelpFightEnd(int roleId, int value, int selfClanId, int helpClanId, int defendClanId,int defendGsId, int win, SBean.BattleArrayHp defend, ClanBattleHelpFightEndCallback callback)
//	{
//		execTask(new ClanBattleHelpFightEndTask(roleId, value, selfClanId, helpClanId, defendClanId, defendGsId, win, defend, callback));
//	}
//	//支援战战斗结束(通知攻击者和防守者添加战报)
//	public void handleClanBattleHelpFightEndForwardTaskResponse(Packet.Clan2S.ClanBattleHelpFightEndForwardReq packet)
//	{
//		if(packet.getIsDefender() == 0)
//		{
//			
//			gs.getLoginManager().exeCommonRoleVisitor(packet.getHelp().roleid, false, new LoginManager.CommonRoleVisitor()
//			{
//				int event = packet.getWin() > 0 ? DBClanBattleLog.EventAttackHelpFail : DBClanBattleLog.EventAttackHelpWin;
//				@Override
//				public synchronized boolean visit(Role role, Role sameUserRole)
//				{
//					//此处role为进攻者。
//					if(role.clanData.clanBattleData != null)
//					{
//						if(role.clanData.clanBattleData.battleType == Clan.CLAN_BATTLE_TYPE_ATTACK){
//							int prestige = packet.getPresitage();
//							role.addClanBattleAttackLog(event, role.clanData.clanBattleData.attack.clanName,-1,(byte)-1, prestige, 0,0,role.clanData.clanBattleData.helpClanInfo.clanName,(byte)packet.getWin(),null);
//							Collection<Integer> petIds = role.clanData.clanBattleData.attack.pets.values();
//							for(int petId : petIds)
//							{
//								SBean.Hp hp = packet.getDefend().petsHp.get(petId);
//								if(hp != null)
//								{
//									if(hp.curValue == 0)
//									{
//										role.clanData.clanBattleData.attack.pets.put(petId, (int)Math.floor(hp.maxValue / 2));
//									}else{
//										role.clanData.clanBattleData.attack.pets.put(petId, hp.curValue);
//									}
//								}
//							}
//							if (event == DBClanBattleLog.EventAttackHelpWin)
//							{
//								//支援者获胜，则
//							}
//							gs.getRPCManager().notifyClanBattleHelpFightEndForwardTask(packet.getTagId(), packet.getSelf(), packet.getHelp(), packet.getPresitage());
//							return true;
//						}
//						else{
//							//支援战期间撤退，同样返回协议，但是不做任何修改。
//							gs.getRPCManager().notifyClanBattleHelpFightEndForwardTask(packet.getTagId(), packet.getSelf(), packet.getHelp(), packet.getPresitage());
//							return false;
//						}
//					}else
//					{
//						return false;
//					}
//				}
//	
//				@Override
//				public void onCallback(boolean success) {
//					
//				}
//			});
//		}else
//		{
//			//防守者
//			gs.getLoginManager().exeCommonRoleVisitor(packet.getHelp().roleid, false, new LoginManager.CommonRoleVisitor()
//			{
//				int event = packet.getWin() > 0 ? DBClanBattleLog.EventDefendHelpWin : DBClanBattleLog.EventDefendHelpFail;
//				int attackClanId = packet.getAttackClanId();
//				@Override
//				public synchronized boolean visit(Role role, Role sameUserRole)
//				{
//					if(role.clanData.clanBattleData != null)
//					{
//						SBean.ClanBattleDefendDesc desc = role.clanData.clanBattleData.defend.get(attackClanId);
//						if(desc != null)
//						{
//							role.addClanBattleDefendLog(event, desc.defendInfo.clanName,-1,(byte)-1, packet.getPresitage(), 0,0,packet.getHelperClanName(),(byte)packet.getWin(),null);
//							if(packet.getWin() > 0)
//							{
//								int attackClanId = packet.getAttackClanId();
//								role.clanData.clanBattleData.defend.remove(attackClanId);
//							}
//							gs.getRPCManager().notifyClanBattleHelpFightEndForwardTask(packet.getTagId(), packet.getSelf(), packet.getHelp(), packet.getPresitage());
//							return true;
//						}else
//						{
//							return false;
//						}
//					}else
//					{
//						return false;
//					}
//				}
//	
//				@Override
//				public void onCallback(boolean success) {
//					
//				}
//			});
//		}
//
//	}
//
//	
//	
//	
//	//
//	public interface ClanMovePositionCallback
//	{
//		void onCallback(int errCode, int x, int y);
//	}
//	public class ClanMovePositionTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanMovePositionCallback callback;
//		ClanMovePositionTask(int roleId, ClanMovePositionCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanMovePositionTask asClanMovePositionTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int x, int y)
//		{
//			callback.onCallback(errCode, x, y);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanMovePositionTask(this.id, roleId);
//		}
//	}
//	public void handleClanMovePositionTaskResponse(int tag, int errCode, int x, int y)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanMovePositionTask task = t.asClanMovePositionTask();
//			if (task != null)
//				task.onCallback(errCode, x, y);
//		}
//	}
//	void clanMovePosition(int roleId, ClanMovePositionCallback callback)
//	{
//		execTask(new ClanMovePositionTask(roleId, callback));
//	}
//
//	
//	
//	//宗门战 战斗开始
//	public interface ClanBattleFightStartCallback
//	{
//		void onCallback(int errCode, SBean.BattleArray battleArray, int roleLvl);
//	}
//	public class ClanBattleFightStartTask extends ClanServiceTask
//	{
//		int roleId;
//		SBean.GlobalRoleId enemyRole;
//		ClanBattleFightStartCallback callback;
//		ClanBattleFightStartTask(int roleId, SBean.GlobalRoleId enemyRole, ClanBattleFightStartCallback callback)
//		{
//			this.roleId = roleId;
//			this.enemyRole = enemyRole;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleFightStartTask asClanBattleFightStartTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.BattleArray battleArray, int roleLvl)
//		{
//			callback.onCallback(errCode, battleArray, roleLvl);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleFightStartTask(this.id, roleId, enemyRole);
//		}
//	}
//	public void handleClanBattleFightStartTaskResponse(int tag, int errCode, SBean.BattleArray battleArray, int roleLvl)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleFightStartTask task = t.asClanBattleFightStartTask();
//			if (task != null)
//				task.onCallback(errCode, battleArray, roleLvl);
//		}
//	}
//	void clanBattleFightStart(int roleId, SBean.GlobalRoleId enemyRole, ClanBattleFightStartCallback callback)
//	{
//		execTask(new ClanBattleFightStartTask(roleId, enemyRole, callback));
//	}
//	public void handleClanBattleFightStartForwardTaskResponse(Packet.Clan2S.ClanBattleFightStartForwardReq packet)
//	{
//		gs.getLoginManager().exeCommonRoleVisitor(packet.getEnemy().roleid, false, new LoginManager.CommonRoleVisitor()
//		{
//			@Override
//			public synchronized boolean visit(Role role, Role sameUserRole)
//			{
//				SBean.BattleArray battleArray = role.getClanBattleMapDefenceBattleArray();
//				gs.getRPCManager().notifyClanBattleFightStartForwardTask(packet.getTagId(), GameData.PROTOCOL_OP_SUCCESS,
//						packet.getSelf(), packet.getEnemy(), battleArray, role.level);
//				return false;
//			}
//			@Override
//			public void onCallback(boolean success)
//			{
//				
//			}
//		});
//	}
//	//战斗结束
//	public interface ClanBattleFightEndCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanBattleFightEndTask extends ClanServiceTask
//	{
//		int roleId;
//		SBean.GlobalRoleId enemyRole;
//		int value;
//		int win;
//		int clanID;
//		SBean.ClanBattleLogDesc desc;
//		ClanBattleFightEndCallback callback;
//		ClanBattleFightEndTask(int roleId, SBean.GlobalRoleId enemyRole, int value, int win, int clanID, SBean.ClanBattleLogDesc desc,ClanBattleFightEndCallback callback)
//		{
//			this.roleId = roleId;
//			this.enemyRole = enemyRole;
//			this.value = value;
//			this.win = win;
//			this.clanID = clanID;
//			this.desc = desc;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleFightEndTask asClanBattleFightEndTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleFightEndTask(this.id, roleId, enemyRole, value, win, clanID, desc);
//		}
//	}
//	public void handleClanBattleFightEndTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleFightEndTask task = t.asClanBattleFightEndTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanBattleFightEnd(int roleId, SBean.GlobalRoleId enemyRole, int value, int win, int clanID, SBean.ClanBattleLogDesc desc, ClanBattleFightEndCallback callback)
//	{
//		execTask(new ClanBattleFightEndTask(roleId, enemyRole, value, win, clanID, desc, callback));
//	}
//	public void handleClanBattleFightEndForwardTaskResponse(Packet.Clan2S.ClanBattleFightEndForwardReq packet)
//	{
//		final int event = packet.getWin() <= 0 ? DBClanBattleLog.EventDefendWin : DBClanBattleLog.EventDefendFail;
//		gs.getLoginManager().exeCommonRoleVisitor(packet.getEnemy().roleid, false, new LoginManager.CommonRoleVisitor()
//		{
//			@Override
//			public synchronized boolean visit(Role role, Role sameUserRole)
//			{
//				int prestige = 0;
//				int clanID = packet.getClanId();
//				SBean.ClanBattleDefendDesc desc = role.clanData.clanBattleData.defend.get(clanID);
//				if(desc == null)
//				{
//					gs.getRPCManager().notifyClanBattleFightEndForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy(), prestige);
//					return false;
//				}
//				if(packet.getWin() > 0)
//				{
//					role.useClanXuantieImpl(desc.xuantie);
//					role.useClanYaocaoImpl(desc.yaocao);
//					prestige = 0;
//				}
//				role.addClanBattleDefendLog(event, desc.defendInfo.clanName, desc.defendInfo.bwType ,(byte)packet.getWin(), prestige, desc.xuantie, desc.yaocao, "", (byte)(-1),packet.getDesc());
//				role.clanData.clanBattleData.defend.remove(clanID);
//				gs.getRPCManager().notifyClanBattleFightEndForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy(), prestige);
//				return true;
//			}
//			@Override
//			public void onCallback(boolean success)
//			{
//				
//			}
//		});
//	}
//
//	
//	//撤退
//	public interface ClanBattleFightExitCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanBattleFightExitTask extends ClanServiceTask
//	{
//		int roleId;
//		SBean.GlobalRoleId enemyRole;
//		int clanID;
//		ClanBattleFightExitCallback callback;
//		ClanBattleFightExitTask(int roleId, SBean.GlobalRoleId enemyRole,int clanID, ClanBattleFightExitCallback callback)
//		{
//			this.roleId = roleId;
//			this.enemyRole = enemyRole;
//			this.clanID = clanID;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBattleFightExitTask asClanBattleFightExitTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBattleFightExitTask(this.id, roleId, clanID, enemyRole);
//		}
//	}
//	public void handleClanBattleFightExitTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBattleFightExitTask task = t.asClanBattleFightExitTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanBattleFightExit(int roleId, SBean.GlobalRoleId enemyRole, int clanID, ClanBattleFightExitCallback callback)
//	{
//		execTask(new ClanBattleFightExitTask(roleId, enemyRole, clanID, callback));
//	}
//	public void handleClanBattleFightExitForwardTaskResponse(Packet.Clan2S.ClanBattleFightExitForwardReq packet)
//	{
//		gs.getLoginManager().exeCommonRoleVisitor(packet.getEnemy().roleid, false, new LoginManager.CommonRoleVisitor()
//		{
//			@Override
//			public synchronized boolean visit(Role role, Role sameUserRole)
//			{
//				int clanId = packet.getClanID();
//				role.clanData.clanBattleData.defend.remove(clanId);
//				gs.getRPCManager().notifyClanBattleFightExitForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy());
//				return true;
//			}
//			@Override
//			public void onCallback(boolean success)
//			{
//			}
//		});
//	}
//	
//	
//
//	public interface ClanShareDiySkillCallback
//	{
//		void onCallback(int errCode, int shareCount);
//	}
//	public class ClanShareDiySkillTask extends ClanServiceTask
//	{
//		int roleId;
//		SBean.DBDiySkill diySkill;
//	 	ClanShareDiySkillCallback callback;
//		ClanShareDiySkillTask(int roleId, SBean.DBDiySkill diySkill, ClanShareDiySkillCallback callback)
//		{
//			this.roleId = roleId;
//			this.diySkill = diySkill;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanShareDiySkillTask asClanShareDiySkillTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int shareCount)
//		{
//			callback.onCallback(errCode, shareCount);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanShareDiySkillTask(this.id, roleId, diySkill);
//		}
//	}
//	public void handleClanShareDiySkillTaskResponse(int tag, int errCode, int shareCount)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanShareDiySkillTask task = t.asClanShareDiySkillTask();
//			if (task != null)
//				task.onCallback(errCode, shareCount);
//		}
//	}
//	void clanShareDiySkill(int roleId, SBean.DBDiySkill diySkill, ClanShareDiySkillCallback callback)
//	{
//		execTask(new ClanShareDiySkillTask(roleId, diySkill, callback));
//	}
//
//	
//	public interface ClanCancelShareDiySkillCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanCancelShareDiySkillTask extends ClanServiceTask
//	{
//		int roleId;
//		int diyskillId;
//	 	ClanCancelShareDiySkillCallback callback;
//		ClanCancelShareDiySkillTask(int roleId, int diyskillId, ClanCancelShareDiySkillCallback callback)
//		{
//			this.roleId = roleId;
//			this.diyskillId = diyskillId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanCancelShareDiySkillTask asClanCancelShareDiySkillTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanCancelShareDiySkillTask(this.id, roleId, diyskillId);
//		}
//	}
//	public void handleClanCancelShareDiySkillTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanCancelShareDiySkillTask task = t.asClanCancelShareDiySkillTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanCancelShareDiySkill(int roleId, int diyskillId, ClanCancelShareDiySkillCallback callback)
//	{
//		execTask(new ClanCancelShareDiySkillTask(roleId, diyskillId, callback));
//	}
//
//	
//	public interface ClanDiySkillBorrowCallback
//	{
//		void onCallback(int errCode, SBean.DBDiySkill diyskill);
//	}
//	public class ClanDiySkillBorrowTask extends ClanServiceTask
//	{
//		int roleId;
//		int diyskillId;
//		int memId;
//		int memGsid;
//		int clanId;
//		ClanDiySkillBorrowCallback callback;
//	 	ClanDiySkillBorrowTask(int roleId, int memId, int memGsid, int diyskillId, int clanId,  ClanDiySkillBorrowCallback callback)
//		{
//			this.roleId = roleId;
//			this.memId = memId;
//			this.memGsid = memGsid;
//			this.diyskillId = diyskillId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanDiySkillBorrowTask asClanDiySkillBorrowTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, SBean.DBDiySkill diyskill)
//		{
//			callback.onCallback(errCode, diyskill);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanDiySkillBorrowTaskTask(this.id, roleId, memId, memGsid, diyskillId, clanId);
//		}
//	}
//	public void handleClanDiySkillBorrowTaskResponse(int tag, int errCode, SBean.DBDiySkill diyskill)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanDiySkillBorrowTask task = t.asClanDiySkillBorrowTask();
//			if (task != null)
//				task.onCallback(errCode, diyskill);
//		}
//	}
//	void clanDiySkillBorrow(int roleId, int memId, int memGsid, int diyskillId, int clanId, ClanDiySkillBorrowCallback callback)
//	{
//		execTask(new ClanDiySkillBorrowTask(roleId, memId, memGsid, diyskillId, clanId, callback));
//	}
//	
//	
//	
//	public interface ClanDiySkillGetShareCallback
//	{
//		void onCallback(int errCode, List<SBean.DBDiySkillShare> diySkillShare);
//	}
//	public class ClanDiySkillGetShareTask extends ClanServiceTask
//	{
//		int roleId;
//		int clanId;
//		ClanDiySkillGetShareCallback callback;
//	 	ClanDiySkillGetShareTask(int roleId, int clanId, ClanDiySkillGetShareCallback callback)
//		{
//			this.roleId = roleId;
//			this.clanId = clanId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanDiySkillGetShareTask asClanDiySkillGetShareTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, List<SBean.DBDiySkillShare> diySkillShare)
//		{
//			callback.onCallback(errCode, diySkillShare);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanDiySkillGetShareTask(this.id, roleId, clanId);
//		}
//	}
//	public void handleClanDiySkillGetShareTaskResponse(int tag, int errCode, List<SBean.DBDiySkillShare> diySkillShare)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanDiySkillGetShareTask task = t.asClanDiySkillGetShareTask();
//			if (task != null)
//				task.onCallback(errCode, diySkillShare);
//		}
//	}
//	void clanDiySkillGetShare(int roleId, int clanId, ClanDiySkillGetShareCallback callback)
//	{
//		execTask(new ClanDiySkillGetShareTask(roleId, clanId, callback));
//	}
//
//	
//	public interface ClanDiySkillShareAwardCallback
//	{
//		void onCallback(int errCode, int awardCount);
//	}
//	public class ClanDiySkillShareAwardTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanDiySkillShareAwardCallback callback;
//		ClanDiySkillShareAwardTask(int roleId, ClanDiySkillShareAwardCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanDiySkillShareAwardTask asClanDiySkillShareAwardTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, int awardCount)
//		{
//			callback.onCallback(errCode, awardCount);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, 0);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanDiySkillShareAwardTask(this.id, roleId);
//		}
//	}
//	public void handleClanDiySkillShareAwardTaskResponse(int tag, int errCode, int awardCount)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanDiySkillShareAwardTask task = t.asClanDiySkillShareAwardTask();
//			if (task != null)
//				task.onCallback(errCode, awardCount);
//		}
//	}
//	void clanDiySkillShareAward(int roleId, ClanDiySkillShareAwardCallback callback)
//	{
//		execTask(new ClanDiySkillShareAwardTask(roleId, callback));
//	}
//
//	
//	//
//	public interface ClanBuyPrestigeCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanBuyPrestigeTask extends ClanServiceTask
//	{
//		int roleId;
//		List<SBean.DummyGoods> items;
//		ClanBuyPrestigeCallback callback;
//		ClanBuyPrestigeTask(int roleId, List<SBean.DummyGoods> items, ClanBuyPrestigeCallback callback)
//		{
//			this.roleId = roleId;
//			this.items = items;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanBuyPrestigeTask asClanBuyPrestigeTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanBuyPrestigeTask(this.id, roleId, items);
//		}
//	}
//	public void handleClanBuyPrestigeTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanBuyPrestigeTask task = t.asClanBuyPrestigeTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanBuyPrestige(int roleId, List<SBean.DummyGoods> items, ClanBuyPrestigeCallback callback)
//	{
//		execTask(new ClanBuyPrestigeTask(roleId, items, callback));
//	}
//	
//	
//	
//	//
//	public interface ClanGetClanTaskEnemyCallback
//	{
//		void onCallback(int errCode, RoleOverview overview, List<PetOverview> pets);
//	}
//	public class ClanGetClanTaskEnemyTask extends ClanServiceTask
//	{
//		int roleId;
//		int enemyId;
//		int enemySevId;
//		List<Integer> pets;
//		ClanGetClanTaskEnemyCallback callback;
//		ClanGetClanTaskEnemyTask(int roleId, int enemyId, int enemySevId, List<Integer> pets, ClanGetClanTaskEnemyCallback callback)
//		{
//			this.roleId = roleId;
//			this.enemyId = enemyId;
//			this.enemySevId = enemySevId;
//			this.pets = pets;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanGetClanTaskEnemyTask asClanGetClanTaskEnemyTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode, RoleOverview overview, List<PetOverview> petOverviews)
//		{
//			callback.onCallback(errCode, overview, petOverviews);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null, null);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanGetClanTaskEnemyTask(this.id, roleId, enemyId, enemySevId, pets);
//		}
//	}
//	public void handleClanGetClanTaskEnemyTaskResponse(int tag, int errCode, RoleOverview overview, List<PetOverview> petOverviews)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanGetClanTaskEnemyTask task = t.asClanGetClanTaskEnemyTask();
//			if (task != null)
//				task.onCallback(errCode, overview, petOverviews);
//		}
//	}
//	public void handleClanGetClanTaskEnemyTaskForwardResponse(Packet.Clan2S.ClanGetTaskEnemyForwardReq packet)
//	{
//
//		Role enemyRole = gs.getLoginManager().getOnGameRole(packet.getEnemy().roleid);
//		if(enemyRole != null)
//		{
//			synchronized(enemyRole)
//			{
//				List<SBean.PetOverview> petOverviews = new ArrayList<SBean.PetOverview>();
//				for(int petId : packet.getPets())
//				{
//					SBean.PetOverview overview = enemyRole.getPetOverviewWithoutLock(petId);
//					petOverviews.add(overview);
//				}
//				SBean.RoleOverview overview = enemyRole.getRoleOverview();
//				gs.getRPCManager().notifyClanGetClanEnemyTaskForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy(), overview, petOverviews);
//			}
//		}else
//		{
//			gs.getLoginManager().getRoleAndPetOverviews(packet.getEnemy().roleid, packet.getPets(), new LoginManager.GetRoleAndPetOverviewsCallback() {
//				@Override
//				public void onCallback(RoleOverview overview, List<PetOverview> petOverviews)
//				{
//					gs.getRPCManager().notifyClanGetClanEnemyTaskForwardTask(packet.getTagId(), packet.getSelf(), packet.getEnemy(), overview, petOverviews);
//				}
//			});
//		}
//	}
//	void clanGetClanTaskEnemy(int roleId, int enemyId, int enemySevId, List<Integer> pets, ClanGetClanTaskEnemyCallback callback)
//	{
//		execTask(new ClanGetClanTaskEnemyTask(roleId, enemyId, enemySevId, pets, callback));
//	}
//
//	
//	
//	
//	public interface ClanDayRefreshCallback
//	{
//		void onCallback(int errCode);
//	}
//	public class ClanDayRefreshTask extends ClanServiceTask
//	{
//		int roleId;
//		ClanDayRefreshCallback callback;
//		ClanDayRefreshTask(int roleId, ClanDayRefreshCallback callback)
//		{
//			this.roleId = roleId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public ClanDayRefreshTask asClanDayRefreshTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int errCode)
//		{
//			callback.onCallback(errCode);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			gs.getRPCManager().notifyClanDayRefreshTask(this.id, roleId);
//		}
//	}
//	public void handleClanDayRefreshTaskResponse(int tag, int errCode)
//	{
//		ClanServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			ClanDayRefreshTask task = t.asClanDayRefreshTask();
//			if (task != null)
//				task.onCallback(errCode);
//		}
//	}
//	void clanDayRefresh(int roleId, ClanDayRefreshCallback callback)
//	{
//		execTask(new ClanDayRefreshTask(roleId, callback));
//	}
//
//	
//	
//	private GameServer gs;
//	private AtomicInteger nextTaskID = new AtomicInteger();
//	private ConcurrentMap<Integer, ClanServiceTask> tasks = new ConcurrentHashMap<>();
//
//
//}
//
