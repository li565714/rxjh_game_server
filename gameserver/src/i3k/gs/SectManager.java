package i3k.gs;

import i3k.DBSect;
import i3k.SBean;
import i3k.TLog;
import i3k.gs.Role.RpcRes;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ket.kdb.Table;
import ket.kdb.TableEntry;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.kdb.Transaction.AutoInit;
import ket.util.Stream;

public class SectManager 
{
	
	private static final int MAX_SECT_IDLE_TIME = 3600 * 2;
	
	public SectManager(GameServer ms)
	{
		this.gs = ms;
	}
	
	public void setSectMaxLevel(int maxLevel)
	{
		this.maxLevel = maxLevel;
	}
	
	public synchronized boolean updateSectMaxLevel(int maxLevel)
	{
		if (maxLevel > this.maxLevel)
		{
			this.maxLevel = maxLevel;
			return true;
		}
		return false;
	}
	
	void onTimer(int timeTick)
	{
		Iterator<Map.Entry<Integer, Sect>> it = this.mapActiveSects.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Sect> kv = it.next();
			Sect sect = kv.getValue();
			synchronized (SectManager.this)
			{
				if (sect.isStandbyTimeOut(timeTick))
				{
					sect.save();
					it.remove();
					continue;
				}
			}
			sect.onTimer(timeTick);
		}
	}
	
	private Sect tryGetSect(int sectId)
	{
		synchronized (SectManager.this) 
		{
			Sect sect = mapActiveSects.get(sectId);
			if (sect != null)
				sect.updateUseTime();
			return sect;
		}
	}
	
	public boolean tryUpdateSectUseTime(int sectId)
	{
		synchronized (SectManager.this)
		{
			Sect sect = mapActiveSects.get(sectId);
			if (sect == null)
				return false;
			sect.updateUseTime();
			return true;
		}
	}
	
	public void tryActiveSect(Sect sect)
	{
		if (sect != null)
		{
			synchronized (SectManager.this)
			{
				Sect realCacheSect = mapActiveSects.putIfAbsent(sect.id, sect);
				if (realCacheSect != null)
				{
					sect.updateUseTime();
				}
			}	
		}
	}
	
	
	public void onRoleLogin(Role role)
	{
		Sect sect = tryGetSect(role.getSectId());
		if (sect != null && sect.onMemberLogin(role))
		{
			return;
		}
		else//防止批准加入帮派过程中，sect中没有保存下来加入帮派信息，而role保存下来了,此处为事后修正处理
		{
			role.updateSectInfo(0, "", -1, (short) 0, 0);
		}
	}
	
	
	public void onRoleLogout(Role role)
	{
		Sect sect = tryGetSect(role.getSectId());
		if (sect != null)
		{
			sect.onMemberLogout(role);
		}
	}

	public void onRoleInfoChanged(Role role)
	{
		Sect sect = tryGetSect(role.getSectId());
		if (sect != null)
		{
			sect.onMemberInfoChanged(role);
		}
	}
	
	public void onRoleOnlineUpdate(Role role)
	{
		Sect sect = tryGetSect(role.getSectId());
		if (sect != null)
		{
			sect.onMemberOnlineUpdate(role);
		}
	}

	public synchronized Map<Integer, Integer> getRoleSectAuras(int sectId)
	{
		Sect sect = tryGetSect(sectId);
		if (sect != null)
		{
			synchronized (sect) 
			{
				return sect.getSectAurasWithoutLock();
			}
		}
		return new TreeMap<>();
	}
	
	public void saveSectMaxLevel()
	{
		gs.getDB().execute(new SaveSectMaxLevelTrans(this.maxLevel, null));
	}
	interface SaveSectMaxLevelCallback
	{
		void onCallback(boolean ok);
	}
	public class SaveSectMaxLevelTrans implements Transaction
	{
		public SaveSectMaxLevelTrans(int maxLevel, SaveSectMaxLevelCallback callback)
		{
			this.maxLevel = maxLevel;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("sectmaxlevel");
			byte[] data = Stream.encodeIntegerLE(maxLevel);
			world.put(key, data);
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			boolean ok = errcode == ErrorCode.eOK;
			gs.getLogger().info("save sects max level " + maxLevel + " to DB " + (ok ? "ok" : " error " + errcode));
			if (this.callback != null)
				this.callback.onCallback(ok);
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private int maxLevel;
		private SaveSectMaxLevelCallback callback;
	}
	
	public interface CreateNewSectCallback
	{
		void onCallback(int sectId);
	}
	public void createNewSect(Role creater, String name, short icon, CreateNewSectCallback callback)
	{
		String sectname = name.trim().toLowerCase();
		if (!GameData.getInstance().checkInputStrValid(sectname, GameData.getInstance().getCommonCFG().input.maxSectNameLength, true) || GameData.checkInputIsDigit(sectname))
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_CREATE_NAME_INVALID);
			return;
		}
		gs.getDB().execute(new CreateSectTrans(creater, sectname, icon, (errCode, sect) -> {
            if (sect == null)
            {
                callback.onCallback(errCode == -1 ? GameData.PROTOCOL_OP_SECT_CREATE_NAME_USED : (errCode == -3 ? GameData.PROTOCOL_OP_SECT_ALREADY_JOIN : GameData.PROTOCOL_OP_FAILED));
                return;
            }
            synchronized (SectManager.this)
			{
            	mapActiveSects.put(sect.id, sect);
			}
            sect.onMemberLogin(creater);
            callback.onCallback(sect.id);
        }));
	}
	
	
	interface CreateSectTransCallback
	{
		void onCallback(int errCode, Sect sect);
	}
	public class CreateSectTrans implements Transaction
	{
		public CreateSectTrans(Role creater, String name, short icon, CreateSectTransCallback callback)
		{
			this.creater = creater;
			this.name = name;
			this.icon = icon;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			Integer sidInt = sectname.get(name);
			if (sidInt != null && sidInt != 0)
			{
				errorCode = -1;
				return false;
			}
			int zoneId = GameData.getZoneIdFromRoleId(creater.id);
			final String maxSectIDKey = MaxSectIDKey + "_" + zoneId;
			Integer maxid = maxids.get(maxSectIDKey);
			int sectSeq = maxid == null ? 1 : maxid + 1;
			if (sectSeq >= GameData.getMaxGSSectCount())
			{
				errorCode = -2;
				return false;
			}
			synchronized (creater)
			{
				if (creater.getSectId() > 0)
				{
					errorCode = -3;
					return false;
				}
				int sectId = GameData.createSectId(zoneId, sectSeq);
				sectData = new Sect(gs, sectId).newCreate(name, icon, creater.getRoleOverview());
				maxids.put(maxSectIDKey, sectSeq);
				sect.put(sectData.id, sectData.toDB());
				sectname.put(sectData.name, sectData.id);
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(sectData == null)
				gs.getLogger().info("create sect [" + name + "] : " + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode + "(" + errorCode + ")"));
			else
				gs.getLogger().info("create sect [" + name + "] sect id : " + sectData.id + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode + "(" + errorCode + ")"));
			this.callback.onCallback(errorCode, sectData);
		}
		
		@AutoInit
		public Table<String, Integer> maxids;
		@AutoInit
		public Table<Integer, DBSect> sect;
		@AutoInit
		public Table<String, Integer> sectname;
		
		private static final String MaxSectIDKey = "sectid";
		
		private Role creater;
		private String name;
		private short icon;
		private CreateSectTransCallback callback;
		private Sect sectData;
		public int errorCode = -100;
	}

	
	private Collection<DBSect> copyDBData()
	{
		List<DBSect> lst = new ArrayList<>();
		for (Sect sect : this.mapActiveSects.values())
		{
			lst.add(sect.toDB());
		}
		return lst;
	}

	public void saveSects()
	{
		gs.getDB().execute(new SaveAllSectsTrans(copyDBData(), this.maxLevel, null));
	}
	
	interface SaveAllSectsTransCallback
	{
		void onCallback(boolean ok);
	}
	public class SaveAllSectsTrans implements Transaction
	{
		public SaveAllSectsTrans(Collection<DBSect> dbsects, int maxLevel, SaveAllSectsTransCallback callback)
		{
			this.dbsects = dbsects;
			this.maxLevel = maxLevel;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			gs.getLogger().debug("save all sects " + dbsects.size() + " to DB");
			for (DBSect dbsect : dbsects)
			{
				sect.put(dbsect.id, dbsect);	
			}
			byte[] key = Stream.encodeStringLE("sectmaxlevel");
			byte[] data = Stream.encodeIntegerLE(maxLevel);
			world.put(key, data);
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			boolean ok = errcode == ErrorCode.eOK;
			gs.getLogger().info("save all sects " + dbsects.size() + " to DB " + (ok ? "ok" : " error " + errcode));
			if (this.callback != null)
				this.callback.onCallback(ok);
		}
		
		@AutoInit
		public Table<Integer, DBSect> sect;
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private Collection<DBSect> dbsects;
		private int maxLevel;
		private SaveAllSectsTransCallback callback;
	}
	
	
	interface DismissSectCallback
	{
		void onCallback(int errCode);
	}
	public void dismissSect(Role role, DismissSectCallback callback)
	{
		Sect sect = tryGetSect(role.getSectId());
		if (sect == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
			return;
		}
		int sectCheck = sect.roleDismissSect(role);
		if (sectCheck <= 0)
		{
			callback.onCallback(sectCheck);
			return;
		}
		this.mapActiveSects.remove(sect.id);
		gs.getDB().execute(new DeleteSectTrans(sect.id, null));
		callback.onCallback(GameData.PROTOCOL_OP_SUCCESS);
	}
	
	interface DeleteSectTransCallback
	{
		void onCallback(boolean ok);
	}
	public class DeleteSectTrans implements Transaction
	{
		public DeleteSectTrans(int sectId, DeleteSectTransCallback callback)
		{
			this.sectId = sectId;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			sect.del(sectId);
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().info("delete sect id : " + sectId + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode));
			if (this.callback != null)
				this.callback.onCallback(errcode == ErrorCode.eOK);
		}
		
		@AutoInit
		public Table<Integer, DBSect> sect;
		
		private int sectId;
		DeleteSectTransCallback callback;
	}
	
	
	public void getSectOverview(int sectId, GetSectOverviewCallback callback)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect != null)
		{
			callback.onCallback(sect.getSectOverview());
			return;
		}
		gs.getDB().execute(new GetSectOverviewTrans(sectId, callback));
	}
	interface GetSectOverviewCallback
	{
		void onCallback(SBean.SectOverview overview);
	}
	public class GetSectOverviewTrans implements Transaction
	{
		public GetSectOverviewTrans(int sectId, GetSectOverviewCallback callback)
		{
			this.sectId = sectId;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBSect dbsect = sect.get(sectId);
			if (dbsect != null)
			{
				overview = dbsect.getSectOverview();
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			this.callback.onCallback(overview);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBSect> sect;
		
		
		private int sectId;
		private GetSectOverviewCallback callback;
		private SBean.SectOverview overview;
	}
	
	public void getSectOverviews(Collection<Integer> sectsId, GetSectOverviewsCallback callback)
	{
		Set<Integer> notInCacheSects = new TreeSet<>();
		Map<Integer, SBean.SectOverview> overviews = new TreeMap<>();
		for (int sectId : sectsId)
		{
			Sect sect = this.mapActiveSects.get(sectId);
			if (sect != null)
			{
				overviews.put(sectId, sect.getSectOverview());
			}
			else
			{
				notInCacheSects.add(sectId);
			}
		}
		if (notInCacheSects.isEmpty())
		{
			callback.onCallback(overviews);
			return;
		}
		gs.getDB().execute(new GetSectOverviewsTrans(notInCacheSects, ovs -> {
			overviews.putAll(ovs);
			callback.onCallback(overviews);
		}));
	}
	interface GetSectOverviewsCallback
	{
		void onCallback(Map<Integer, SBean.SectOverview> overviews);
	}
	public class GetSectOverviewsTrans implements Transaction
	{
		public GetSectOverviewsTrans(Collection<Integer> sectsId, GetSectOverviewsCallback callback)
		{
			this.sectsId = sectsId;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			for (int sectId : sectsId)
			{
				DBSect dbsect = sect.get(sectId);
				if (dbsect != null)
				{
					overviews.put(dbsect.id, dbsect.getSectOverview());
				}	
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			this.callback.onCallback(overviews);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBSect> sect;
		
		
		private Collection<Integer> sectsId;
		private GetSectOverviewsCallback callback;
		private Map<Integer, SBean.SectOverview> overviews = new TreeMap<>();
	}
	
	public void getSectOverviewByName(String sectName, GetSectOverviewCallback callback)
	{
		gs.getDB().execute(new GetSectIdByNameTrans(sectName, sectId -> {
			if (sectId == null || sectId <= 0)
			{
				callback.onCallback(null);
				return;
			}
			getSectOverview(sectId, callback);
		}));
	}
	
	interface GetSectIdByNameCallback
	{
		void onCallback(Integer sectId);
	}
	public class GetSectIdByNameTrans implements Transaction
	{
		public GetSectIdByNameTrans(String name, GetSectIdByNameCallback callback)
		{
			this.name = name;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			sectId = sectname.get(name);
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			this.callback.onCallback(sectId);
		}
		
		@AutoInit
		public TableReadonly<String, Integer> sectname;
		
		
		private String name;
		private GetSectIdByNameCallback callback;
		private Integer sectId;
	}
	
	public void sectRename(int sectId, String oldName, String newName, SectRenameCallback callback)
	{
		gs.getDB().execute(new SectRenameTrans(sectId, oldName, newName, callback));
	}
	interface SectRenameCallback
	{
		void onCallback(int errCode);
	}
	public class SectRenameTrans implements Transaction
	{
		public SectRenameTrans(int sectId, String oldName, String newName, SectRenameCallback callback)
		{
			this.sectId = sectId;
			this.oldName = oldName;
			this.newName = newName;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			Integer sidInt = sectname.get(newName);
			if (sidInt != null && sidInt != 0)
			{
				errCode = -1;
				return false;
			}
			sectname.put(newName, sectId);
			errCode = 0;
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().info("sect " + oldName + " rename " + newName + (errCode == 0 ? " success " : " failed"));
			callback.onCallback(errCode);
		}
		
		@AutoInit
		public Table<String, Integer> sectname;
		
		final private int sectId;
		final private String oldName;
		final private String newName;
		private SectRenameCallback callback;
		private int errCode = -100;
	}
	//////////////////////////////////
	public Collection<Integer> getAllMembers(int sectId)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		return sect != null ? sect.getAllMembers() : null;
	}
	
	public boolean roleSendSectMsg(Role memberRole, SBean.MessageInfo msg)
	{
		Sect sect = this.tryGetSect(memberRole.getSectId());
		return sect != null && sect.memberSendSectMsg(memberRole.id, msg);
	}
	
	public void roleDedicateActivity(Role role, int vit)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect != null)
		{
			sect.roleMemeberDedicateActivity(role, vit);
		}
	}

	public void getRoleSectInfo(Role role)
	{
		SBean.RoleSectData roleSectData = null;
		SBean.SectDetail sectData = null;
		SBean.RoleSectStats roleSectStats = null;
		int sectId = role.getSectId();
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect != null)
		{
			synchronized (sect) 
			{
				synchronized (role)
				{
					roleSectData = role.getRoleSectDataWithoutLock();
					sect.onRoleSyncSect(role);
				}
				sectData = sect.getSectDetailWithoutLock();
				roleSectStats = sect.getSectMemberStatsWithoutLock(role.id);
			}
		}
		else
		{
			synchronized (role)
			{
				roleSectData = role.getRoleSectDataWithoutLock();
			}
		}
		final SBean.RoleSectData roleFinalSectData = roleSectData;
		final SBean.SectDetail sectFinalData = sectData;
		final SBean.RoleSectStats roleFinalSectStats = roleSectStats;
		gs.getFlagManager().getSectFlagNum(sectId, flagnum -> gs.getRPCManager().sendStrPacket(role.netsid, new SBean.sect_sync_res(new SBean.SectInfo(roleFinalSectData, sectFinalData, roleFinalSectStats, this.maxLevel, flagnum))));
	}
	
	public RpcRes<SBean.SectMembers> getRoleSectMembers(Role memberRole)
	{
		Sect sect = this.tryGetSect(memberRole.getSectId());
		if (sect == null)
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		return sect.roleGetMembers(memberRole.id);
	}
	
	public synchronized void graspGetSectMembers(Role self)
	{
		synchronized(self)
		{
			if(self.level < GameData.getInstance().getExpCoinBaseCFGS().lvlReq)
			{
				self.graspSync(GameData.PROTOCOL_OP_FAILED, GameData.emptyList());
				return;
			}
		}
		
		Sect sect = this.tryGetSect(self.getSectId());
		if (sect == null)
		{
			self.graspSync(GameData.PROTOCOL_OP_GRASP_SECT_NOT_EXIST, GameData.emptyList());
			return;
		}
		sect.syncRoleGraspInfo(self);
	}
	
	public synchronized void graspWithSectMember(int graspID, int memberID, Role self)
	{
		synchronized(self)
		{
			if(memberID == self.id || self.roleExpCoin.graspData.dayGraspTimes >= GameData.getInstance().getExpCoinBaseCFGS().graspBase.dayGraspTimes || self.level < GameData.getInstance().getExpCoinBaseCFGS().lvlReq)
			{
				gs.getRPCManager().sendStrPacket(self.netsid, new SBean.grasp_impl_res(GameData.PROTOCOL_OP_FAILED, 0));
				return;
			}
		}
		
		Sect sect = this.tryGetSect(self.getSectId());
		if (sect == null)
		{
			gs.getRPCManager().sendStrPacket(self.netsid, new SBean.grasp_impl_res(GameData.PROTOCOL_OP_GRASP_SECT_NOT_EXIST, 0));
			return;
		}

		if(!sect.members.containsKey(memberID))
		{
			gs.getRPCManager().sendStrPacket(self.netsid, new SBean.grasp_impl_res(GameData.PROTOCOL_OP_GRASP_MEMBER_NOT_JOINSECT, 0));
			return;
		}
		
		gs.getLoginManager().exeCommonRoleVisitor(memberID, false, new LoginManager.CommonRoleVisitor()
		{
			int targetDayFortune = 0;
			int targetLevel = 0;
			
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				targetDayFortune = role.roleExpCoin.graspData.dayFortune;
				targetLevel = role.level;
				return false;
			}
			
			@Override
			public void onCallback(boolean success)
			{	
				synchronized(self)
				{
					boolean suit = self.roleExpCoin.graspData.dayFortune == graspID;
					SBean.GraspCostCFGS costCfg = suit ? GameData.getInstance().getExpCoinBaseCFGS().graspSuitCost : GameData.getInstance().getExpCoinBaseCFGS().graspNormalCost;
					if(!self.canUseExpCoin(costCfg.expCoinCost) || !self.containsEnoughGameItems(costCfg.itemsCost))
					{
						gs.getRPCManager().sendStrPacket(self.netsid, new SBean.grasp_impl_res(GameData.PROTOCOL_OP_FAILED, 0));
						return;
					}
					
					TLogger.TLogEvent tlogEvent = gs.getTLogger().createNewEvent(TLog.AT_GRASP_IMPL);
					self.useExpCoin(costCfg.expCoinCost, tlogEvent.getGameItemRecords());
					self.delGameItems(costCfg.itemsCost, tlogEvent.getGameItemRecords());
					
					boolean same = suit && self.roleExpCoin.graspData.dayFortune == targetDayFortune && targetLevel >= GameData.getInstance().getExpCoinBaseCFGS().lvlReq;
					int addExp = same ? costCfg.addExp : GameData.getInstance().getExpCoinBaseCFGS().graspNormalCost.addExp;
					int addLvl = self.roleExpCoin.addGraspExp(graspID, addExp);
					self.roleExpCoin.graspData.dayGraspTimes++;
					self.roleExpCoin.graspData.dayGraspRids.add(memberID);
					self.roleExpCoin.graspData.lastGraspTime = GameTime.getTime();
					
					self.testGraspChallengeTask();
					gs.getTLogger().logRoleEventFlow(self, tlogEvent);
					gs.getRPCManager().sendStrPacket(self.netsid, new SBean.grasp_impl_res(GameData.PROTOCOL_OP_SUCCESS, same ? 1 : 0));
				}
			}
		});
	}
	
	public Set<Integer> roleQuerySectSelfApplied(Role role, List<Integer> sects)
	{
		Set<Integer> applied = new TreeSet<>();
		for (int sectId : sects)
		{
			Sect sect = this.mapActiveSects.get(sectId);
			if (sect != null)
			{
				if (sect.getAllApplicants().contains(role.id))
					applied.add(sectId);
			}
		}
		return applied;
	}
	
	public List<SBean.SectOverview> getRecentRects()
	{
		List<SBean.SectOverview> pageSect = new ArrayList<>();
		List<Integer> lst = new ArrayList<>();
		lst.addAll(this.mapActiveSects.keySet());
		Collections.shuffle(lst);
		int count = lst.size() <= 100 ? lst.size() : 100;
		for (int i = 0; i < count; ++i)
		{
			int sectId = lst.get(i);
			Sect sect = this.mapActiveSects.get(sectId);
			if (sect != null)
			{
				pageSect.add(sect.getSectOverview());
			}
		}
		return pageSect;
	}
	
	
	
	public int roleApplyJoinSect(Role role, int sectId)
	{
		Sect sect = this.tryGetSect(sectId);
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.roleTryApply(role);
	}
	
	public void getRoleSectApplications(Role role, Role.GetSectApplicationsCallback callback)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_NOT_EXIST, GameData.emptyList());
			return;
		}
		sect.getSectApplications(role, callback);
	}
	
	public Role.RpcRes<List<SBean.DBSectHistory>> getRoleSectHistory(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		return sect.getSectHistory();
	}
	
	interface JoinSectCallback
	{
		void onCallback(int errorCode);
	}
	public void roleAcceptApplicantJoinSect(Role role, SBean.RoleOverview applyRole, JoinSectCallback callback)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
			return;
		}
		sect.roleTryAcceptApplicant(role, applyRole, callback);
	}
	
	public int roleRefuseApplicantJoinSect(Role role, int applyRoleId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.roleTryRefuseApplicant(role, applyRoleId);
	}
	
	public int roleRefuseAllApplicantsJoinSect(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.roleTryRefuseAllApplicants(role);
	}

	public int roleKickSectMember(Role role, int kickedRoleId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.kickSectMember(role, kickedRoleId);
	}
	
	
	public int roleLeaveSect(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.leaveSect(role);
	}
	
	
	public int roleAppointSectMemberPositon(Role role, int appointedRoleId, int position)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.appointMemberPositon(role, appointedRoleId, position);
	}
	
	
	public int roleUpdateSectCreed(Role role, String creed)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.sectUpdateCreed(role, creed);
	}
	
	public int roleUpgradeSect(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		int updateTime = sect.upgradeSect(role);
		if (updateTime > 0)
		{
			if (this.updateSectMaxLevel(sect.level))
			{
				sect.save();
				saveSectMaxLevel();
			}
			gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_SECT_LEVELUP, sect.name + "|" + sect.level);
		}
		return updateTime;
	}
	
	
	public int roleAccelerateSectUpgradeCooling(Role role, int accTime)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.accelerateUpgradeCooling(role, accTime, this.maxLevel);
	}
	
	
	public int roleAddsSectAuraExp(Role role, int auraId, int itemId, int itemCount)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.addSectAuraExp(role, auraId, itemId, itemCount);
	}
	
	public Role.RpcRes<List<SBean.DBSectAura>> roleGetSectAuras(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		return sect.getSectAura(role);
	}
	
	public int roleWorshipSectMember(Role role, int memberId, int type)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.worshipSectMember(role, memberId, type);
	}
	
	public RpcRes<SBean.SectWorshipedData> roleSyncnWorshipReward(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		
		return sect.syncWorshipReward(role);
	}
	
	public int roleTakeWorshipReward(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.takeWorShipReward(role);
	}
	
	
	public int roleOpenSectBanquet(Role role, int type)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.openSectBanquet(role, type);
	}
	
	
	public RpcRes<List<SBean.SectBanquet>> roleGetBanquets(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		return sect.getBanquets();
	}
	
	
	public int roleJoinSectBanquet(Role role, int bId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.joinBanquet(role, bId);
	}
	
	
	public int roleOpenSectMap(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.openSectMap(role, mapId);
	}
	
	public int roleOpenSectGroupMap(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.openSectGroupMap(role, mapId);
	}
	
	public RpcRes<Map<Integer, Integer>> roleQuerySectMapsStatus(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		
		return sect.querySectMapsStatus(role);
	}
	
	
	public RpcRes<SBean.SectMapStatus> roleQuerySectMap(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		
		return sect.querySectMap(role, mapId);
	}
	
	public int roleStartSectMap(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.startSectMap(role, mapId);
	}
	
	public int roleStartSectGroupMap(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		return sect.startSectGroupMap(role, mapId);
	}
	
	public int roleLeaveSectMap(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.leaveSectMap(role, mapId);
	}
	
	
	public int roleApplySectMapRewards(Role role, int mapId, int rewardId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_SECT_NOT_EXIST;
		
		return sect.applySectMapRewards(role, mapId, rewardId);
	}
	
	public Role.RpcRes<SBean.SectMapAllocation> roleGetSectMapAllocation(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		
		return sect.getSectMapAllocation(role, mapId);
	}
	
	
	public Role.RpcRes<SBean.SectMapDamage> roleGetSectMapDamage(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		
		return sect.getSectMapDamage(role, mapId);
	}
	
	
	
	public Role.RpcRes<SBean.SectMapInfo> roleGetSectMapDetail(Role role, int mapId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		
		return sect.getSectMapDetail(role, mapId);
	}
	
	
	
	public Role.RpcRes<List<SBean.SectMapRewardsLog>> roleGetSectMapRewardsLog(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new Role.RpcRes<>(GameData.PROTOCOL_OP_SECT_NOT_EXIST);
		
		return sect.getSectMapRewardsLog(role);
	}
	
	public SBean.SectMapAttacker syncSectMapProgress(int sectId, int mapId, int spawnPoint, int progress, int damage)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return null;
		
		return sect.syncSectMapProgress(mapId, spawnPoint, progress, damage);
	}
	

	
	
	public SBean.SectSelfTaskInfo roleSyncSectSelfTask(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return null;
		return sect.syncSectSelfTask(role);
	}
	
	
	public boolean roleSyncSectSharedTask(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		return sect != null && sect.syncSectSharedTask(role);

	}
	
	public SBean.SectFinishedTaskRes roleSyncSectFinishedSelfTask(Role role, List<Integer> shared)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return null;
		
		return sect.syncSectFinishedSelfTask(role, shared);
	}
	
	
	public int sectTaskReceive(Role role, int ownerId, int sid)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return 0;
		
		return sect.sectTaskReceive(role, ownerId, sid);
	}
	
	
	public int sectTaskCancel(Role role, int ownerId, int sid)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return 0;
		
		return sect.sectTaskCancel(role, ownerId, sid);
	}
	
	
	public boolean sectTaskFinish(Role role, int ownerId, int sid)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		return sect != null && sect.sectTaskFinish(role, ownerId, sid);

	}
	
	
	public boolean sectTaskIssuanceShare(Role role, int sid, int taskId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		return sect != null && sect.sectTaskIssuanceShare(role, sid, taskId);

	}

	public List<SBean.SectTask> sectTaskReset(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return null;

		return sect.sectTaskReset(role);
	}
	
	public SBean.SectTaskReward sectTaskShareDoneRewards(Role role){
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return null;
		
		return sect.sectTaskShareDoneRewards(role);
	}
	
	
	public void changeSectName(Role role, String name, SectRenameCallback callback)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		sect.changeSectName(role, name, callback);
	}
	
	
	public int changeJoinSectLevel(Role role, int level)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		return sect.changeJoinSectLevel(role, level);
	}
	
	
	
	public int changeSectIconAndFrame(Role role, short icon, short frame)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		return sect.changeSectIconAndFrame(role, icon, frame);
	}
	
	
	public boolean sendMail(Role role, String content)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return false;
		
		return sect.chiefSendEmail(role, content);
	}
	
	public void setClimbTowerData(Role role, int groupId, int sectId, SBean.DBClimbTowerRecordDataCfg data)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return;
		sect.setClimbTowerData(groupId, data);
	}
	
	public Map<Integer, SBean.DBClimbTowerRecordDataCfg> getClimbTowerData(int sectId)
	{
		Sect sect = this.tryGetSect(sectId);
		if (sect == null)
			return null;
		return sect.getClimbTowerData();
	}
	
	//帮派运镖
	public void syncSectDeliver(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			role.syncSectDeliverOperate(null);
			return;
		}
		sect.syncSectDeliver(role);
	}

	public int beginSectDeliver(int routeId, int taskId, Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.beginSectDeliver(routeId, taskId, role);
	}

	public int getSectDeliverWishTime(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return 0;
		return sect.getSectDeliverWishTime();
	}
	
	public void modefySectDeliverWish(Role role, int exp, int money, int hp)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			role.saveWishSectDeliverOperate(false);
			return;
		}
		sect.modefySectDeliverWish(role, exp, money, hp);
	}

	public List<SBean.DBDeliverWishListItem> getDeliverWishRank(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return null;
		return sect.getDeliverWishRank();
	}
	
	public int sectDeliverOnCancel(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.sectDeliverOnCancel(role);
	}

	public void syncWishSectDeliver(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			role.syncWishSectDeliverOperate(null, null);
			return;
		}
		sect.syncWishSectDeliver(role);
	}

	public void addWishSectDeliver(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			role.addWishSectDeliverOperate(null, 0);
			return;
		}
		sect.addWishSectDeliver(role);
	}
	
	public void finishSectDeliver(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
		{
			role.finishSectDeliverOperate(null);
			return;
		}
		sect.finishSectDeliver(role);
	}

	
	public int sectDeliverSearchHelp(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.sectDeliverSearchHelp(role);
	}
	
	public int sectDeliverOnHelp(Role role, int roleId, SBean.MapLocation targetLocation, int line)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.sectDeliverOnHelp(role, roleId, targetLocation, line);
	}
	
	//自创武功
	public List<SBean.DBDiySkillShare> sectDiySkillGetShare(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return null;
		return sect.sectDiyskillGetShare(role.id);
	}
	
	//取消分享自创武功
	public int sectCancelShareDiySkill(Role role, int diySkillId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.sectDiyskillCancelShare(role.id, diySkillId);
	}
	
	//分享自创武功
	public int sectShareDiySkill(Role role, SBean.DBDiySkill diySkill)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.sectDiyskillShare(role.id, diySkill);
	}
	
	// 自创武功借用武功
	public SBean.DBDiySkill sectDiySkillBorrow(Role role, int roleId, int diyskillId)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return null;
		return sect.sectDiyskillBorrow(roleId, diyskillId);
	}

	public void roleAddSectContribution(Role role, int contribution)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return;
		sect.roleAddSectContribution(role.id, contribution);
	}

	public void updateRoleFightPower(Role role, int newPower)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return;
		sect.updateRoleFightPower(role, newPower);
	}

	public void addSectMapTime(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return;
		sect.addSectMapTime(role.id);
	}
	
	public int changeSectPushApplication(Role role, byte ok)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.changeSectPushApplication(role.id, ok);
	}

	public int acceptSectInvite(Role inviteRole, Role role)
	{
		Sect sect = this.tryGetSect(inviteRole.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.acceptSectInvite(inviteRole, role);
	}

	public boolean gmAddSectVit(Role role, int vit)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return false;
		return sect.gmAddSectVit(vit);
	}
	
	public int getSectGroupMapInstanceId(Role role, int mid)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return 0;
		return sect.getSectGroupMapInstanceId(mid);
	}

	public SBean.SyncGroupMapBean getSectGroupMapCopyData(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return new SBean.SyncGroupMapBean(null, null);
		return sect.getSectGroupMapCopyData();
	}

	public void setGroupMapProcess(int sectId, int mapId, int mapInstance, int progress)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.setGroupMapProcess(mapId, mapInstance, progress);
	}

	public void receiveGroupMapReward(int sectId, int mapId, int mapInstance, int progress)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.receiveGroupMapReward(mapId, mapInstance, progress);
	}

	public void groupMapProcessChanged(int sectId, int mapId, int mapInstance, int spawnPointId, int roleId, int monsterId, int progress, int damage)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.groupMapProcessChanged(mapId, mapInstance, spawnPointId, roleId, monsterId, progress, damage);
	}

	public void groupMapOnClose(int sectId, int mapId)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.groupMapOnClose(mapId);
	}
	
	public void roleLeaveSectGroupMap(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return;
		sect.roleLeaveSectGroupMap(role.id);
	}

	public void roleEnterSectGroupMap(Role role)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return;
		sect.roleEnterSectGroupMap(role.id);
	}

	public void groupMapMonsterAddKill(int sectId, int mapId, int mapInstance, int monsterId, int spawnPointId)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.groupMapMonsterAddKill(mapId, mapInstance, monsterId, spawnPointId);
	}
	
	public void notifyFlagLoss(int sectId, String sectName, String name, int mapId)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.logFlagLoss(mapId, sectName, name);
	}



	public void notifyFlagGet(int sectId, String sectName, String name, int mapId)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.logFlagGet(mapId, sectName, name);
	}

	public void sendFlagRoleReward(int sectId, int mapId, int rewardTime)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.memberGetFlagReward(mapId, rewardTime);
	}

	public void flagAddSectVit(int sectId, int mapId)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.addVitByFlagReward(mapId);
	}

	public void sendFlagEndRoleReward(int sectId, int mapId, int rewardTime)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.memberGetFlagEndReward(mapId, rewardTime);
	}

	public void flagEndAddSectVit(int sectId, int mapId)
	{
		Sect sect = this.mapActiveSects.get(sectId);
		if (sect == null)
			return;
		sect.addVitByFlagEndReward(mapId);
	}

	public SBean.MapFlagSectOverView getSectFlagOverview(int sectId)
	{
		Sect sect = this.tryGetSect(sectId);
		if (sect == null)
			return new SBean.MapFlagSectOverView(0, "", 0);
		return new SBean.MapFlagSectOverView(sect.id, sect.name, sect.icon);
	}

	public int setQQGroup(Role role, String qqgroup)
	{
		Sect sect = this.tryGetSect(role.getSectId());
		if (sect == null)
			return GameData.PROTOCOL_OP_FAILED;
		return sect.setSectQQGroup(role, qqgroup);
	}

	//锁的原则：sect锁包role锁，先在最内层锁中对role进行操作，然后返回退出role锁在sect中继续
	
	final GameServer gs;
//	private Object mapSectsLock = new Object();
	int maxLevel;
//	ConcurrentMap<Integer, Sect> mapSects = new ConcurrentHashMap<>();//sect id --> sect
//	ConcurrentMap<String, Integer> mapn2s = new ConcurrentHashMap<>();//sect name --> sect id
//	ConcurrentMap<Integer, Integer> mapr2s = new ConcurrentHashMap<>();//role id -->sect id
//	ConcurrentMap<Integer, Integer> mapRecentSects = new ConcurrentHashMap<>();//sect id --> updatetime
	ConcurrentMap<Integer, Sect> mapActiveSects = new ConcurrentHashMap<>();//sect id --> sect


}

