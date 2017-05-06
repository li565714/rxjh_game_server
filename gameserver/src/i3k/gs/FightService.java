package i3k.gs;

import i3k.SBean;
import i3k.SBean.MessageInfo;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FightService
{
	private final GameServer gs;
	private AtomicInteger nextTaskID = new AtomicInteger();
	private ConcurrentMap<Integer, FightServiceTask> tasks = new ConcurrentHashMap<>();
	
	private Map<Integer, SnapshotCache> snapshotCaches = new HashMap<>();
	private Set<Integer> forceWarJoinRoles = new HashSet<>();
	private Set<Integer> superArenaJoinRoles = new HashSet<>();
	
	//-------------------------------------------------
	class SnapshotCache
	{
		List<SBean.RankRole> snapshot;
		final int snapshotCreateTime;
		
		SnapshotCache(int snapshotCreateTime)
		{
			snapshot = new ArrayList<>();
			this.snapshotCreateTime = snapshotCreateTime;
		}
	}
	
	//-------------------------------------------------
	public abstract class FightServiceTask
	{
		private final static int MAX_WAIT_TIME = 2;
		final static int TIME_OUT_ERROR = -100;
		final int id;
		int sendTime;
		
		FightServiceTask()
		{
			this.id = nextTaskID.incrementAndGet();
		}
		
		boolean isTooOld(int now)
		{
			return sendTime + MAX_WAIT_TIME <= now;
		}
		
		void doTask()
		{
			sendTime = GameTime.getTime();
			doTaskImpl();
		}
		
		abstract void doTaskImpl();
		
		abstract void onTimeout();
	}
	//-------------------------------------------------
	
	public FightService(GameServer gs)
	{
		this.gs = gs;
	}
	
	void execTask(FightServiceTask task)
	{
		tasks.put(task.id, task);
		task.doTask();
	}
	
	void onTimer(int timeTick)
	{
		checkTimeOutTask(timeTick);
	}
	
	private void checkTimeOutTask(int timeTick)
	{
		List<FightServiceTask> timeoutTasks = getTimeOutTasks(timeTick);
		timeoutTasks.forEach(FightServiceTask::onTimeout);
	}
	
	private List<FightServiceTask> getTimeOutTasks(int timeTick)
	{
		List<FightServiceTask> timeoutTasks = new ArrayList<>();
		Iterator<FightServiceTask> it = tasks.values().iterator();
		while(it.hasNext())
		{
			FightServiceTask task = it.next();
			if(task.isTooOld(timeTick))
			{
				timeoutTasks.add(task);
				it.remove();
			}
		}
		return timeoutTasks;
	}
	
	public FightServiceTask peekTask(int id)
	{
		FightServiceTask task = tasks.remove(id);
		if(task == null)
			gs.getLogger().warn("FightServiceTask can't find task id : " + id);
		
		return task;
	}
	
	synchronized void addForceWarJoinRole(int roleID)
	{
		this.forceWarJoinRoles.add(roleID);
	}
	
	synchronized void delForceWarJoinRole(int roleID)
	{
		this.forceWarJoinRoles.remove(roleID);
	}
	
	synchronized void clearForceWarJoinRoles()
	{
		for(int rid: this.forceWarJoinRoles)
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if(role != null)
				role.syncRoleForceWarMatchImpl(GameData.PROTOCOL_OP_SUPERARENA_MATCH_TIMEOUT);
		}
	}
	
	synchronized void addSuperArenaJoinRole(int roleID)
	{
		this.superArenaJoinRoles.add(roleID);
	}
	
	synchronized void delSuperArenaJoinRole(int roleID)
	{
		this.superArenaJoinRoles.remove(roleID);
	}
	
	synchronized void clearSuperArenaJoinRoles()
	{
		for(int rid: this.superArenaJoinRoles)
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if(role != null)
			{
				role.clearFightTeam();
				role.superarenaState = 0;
				role.superarenaJoinTime = 0;
				role.superarenaJoinType = 0;
				role.superarenaJoinGrade = 0;
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.superarena_join(GameData.PROTOCOL_OP_SUPERARENA_MATCH_TIMEOUT));
			}
		}
	}
	//------------------------------------------------------------------------------------
	public interface RoleJoinForceWarReqCallBack
	{
		void onCallback(int ok);
	}
	
	public class RoleJoinForceWarTask extends FightServiceTask
	{
		final SBean.ForceWarJoin joinInfo;
		final int forcewarType;
		final RoleJoinForceWarReqCallBack callback;
		
		RoleJoinForceWarTask(SBean.ForceWarJoin joinInfo, int forcewarType, RoleJoinForceWarReqCallBack callback)
		{
			this.joinInfo = joinInfo;
			this.forcewarType = forcewarType;
			this.callback = callback;
		}
		
		void onCallback(int ok)
		{
			if(ok > 0)
				FightService.this.addForceWarJoinRole(joinInfo.overview.id);
			
			callback.onCallback(ok);
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightRoleJoinForceWar(this.id, joinInfo, forcewarType);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
	}
	
	public void roleJoinForceWarImpl(SBean.ForceWarJoin joinInfo, int forcewarType, RoleJoinForceWarReqCallBack callback)
	{
		execTask(new RoleJoinForceWarTask(joinInfo, forcewarType, callback));
	}
	
	public void handleRoleJoinForceWarRes(int tag, int ok)
	{
		FightServiceTask task = this.peekTask(tag);
		if(task == null)
			return;
		
		if(task instanceof RoleJoinForceWarTask)
		{
			RoleJoinForceWarTask rt = RoleJoinForceWarTask.class.cast(task);
			if(rt != null)
				rt.onCallback(ok);
		}
	}
	//---------------------------------------------------------------------------------------------------------
	public interface TeamJoinForceWarCallBack
	{
		void onCallback(int ok);
	}
	
	public class TeamJoinForceWarTask extends FightServiceTask
	{
		final List<SBean.ForceWarJoin> members;
		final byte bwType;
		final int forcewarType;
		final TeamJoinForceWarCallBack callback;
		
		TeamJoinForceWarTask(List<SBean.ForceWarJoin> members, byte bwType, int forcewarType, TeamJoinForceWarCallBack callback)
		{
			this.members = members;
			this.bwType = bwType;
			this.forcewarType = forcewarType;
			this.callback = callback;
		}
		
		void onCallback(int ok)
		{
			if(ok > 0)
			{
				for(SBean.ForceWarJoin e: this.members)
				{
					FightService.this.addForceWarJoinRole(e.overview.id);
				}
			}
			
			callback.onCallback(ok);
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightTeamJoinForceWar(this.id, this.members, this.bwType, this.forcewarType);
		}
		
		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
	}
	
	public void teamJoinForceWarImpl(List<SBean.ForceWarJoin> members, byte bwType, int forcewarType, TeamJoinForceWarCallBack callback)
	{
		execTask(new TeamJoinForceWarTask(members, bwType, forcewarType, callback));
	}
	
	public void handleTeamJoinForceWarRes(int tag, int ok)
	{
		FightServiceTask task = this.peekTask(tag);
		if(task == null)
			return;
		
		if(task instanceof TeamJoinForceWarTask)
		{
			TeamJoinForceWarTask tt = TeamJoinForceWarTask.class.cast(task);
			if(tt != null)
				tt.onCallback(ok);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------
	public interface RoleQuitForceWarCallBack
	{
		void onCallback(int ok);
	}
	
	public class RoleQuitForceWarTask extends FightServiceTask
	{
		final int roleID;
		final byte bwType;
		final int forcewarType;
		final RoleQuitForceWarCallBack callback;
		
		RoleQuitForceWarTask(int roleID, byte bwType, int forcewarType, RoleQuitForceWarCallBack callback)
		{
			this.roleID = roleID;
			this.bwType = bwType;
			this.forcewarType = forcewarType;
			this.callback = callback;
		}
		
		void onCallback(int ok)
		{
			FightService.this.delForceWarJoinRole(roleID);
			callback.onCallback(ok);
		}

		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightRoleQuitForceWar(this.id, roleID, bwType, forcewarType);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
	}
	
	public void roleQuitForceWarImpl(int roleID, byte bwType, int forcewarType, RoleQuitForceWarCallBack callback)
	{
		execTask(new RoleQuitForceWarTask(roleID, bwType, forcewarType, callback));
	}
	
	public void handleRoleQuitForceWarRes(int tag, int ok)
	{
		FightServiceTask task = this.peekTask(tag);
		if(task == null)
			return;
		
		if(task instanceof RoleQuitForceWarTask)
		{
			RoleQuitForceWarTask rt = RoleQuitForceWarTask.class.cast(task);
			if(rt != null)
				rt.onCallback(ok);
		}
	}
	//------------------------------------------------------------------------------------
	public interface TeamQuitForceWarCallBack
	{
		void onCallback(int ok);
	}
	
	public class TeamQuitForceWarTask extends FightServiceTask
	{
		final int roleID;
		final byte bwType;
		final List<Integer> members;
		final int forcewarType;
		final TeamQuitForceWarCallBack callback;
		
		TeamQuitForceWarTask(int roleID, byte bwType, List<Integer> members, int forcewarType, TeamQuitForceWarCallBack callback)
		{
			this.roleID = roleID;
			this.bwType = bwType;
			this.members = members;
			this.forcewarType = forcewarType;
			this.callback = callback;
		}
		
		void onCallback(int ok)
		{
			for(int rid: this.members)
				FightService.this.delForceWarJoinRole(rid);
			
			callback.onCallback(ok);
		}

		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightTeamQuitForceWar(this.id, roleID, bwType, this.members.size(), forcewarType);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
	}
	
	public void teamQuitForceWarImpl(int roleID, byte bwType, List<Integer> members, int forcewarType, TeamQuitForceWarCallBack callback)
	{
		execTask(new TeamQuitForceWarTask(roleID, bwType, members, forcewarType, callback));
	}
	
	public void handleTeamQuitForceWarRes(int tag, int ok)
	{
		FightServiceTask task = this.peekTask(tag);
		if(task == null)
			return;
		
		if(task instanceof TeamQuitForceWarTask)
		{
			TeamQuitForceWarTask tt = TeamQuitForceWarTask.class.cast(task);
			if(tt != null)
				tt.onCallback(ok);
		}
	}
	
	//------------------------------------------------------------------------------------
	public interface SingleJoinSuperArenaCallBack
	{
		void onCallback(int ok);
	}
	
	public class SingleJoinSuperArenaTask extends FightServiceTask
	{
		final SBean.SuperArenaJoin joinInfo;
		final int arenaType;
		final int grade;
		final SingleJoinSuperArenaCallBack callback;
		
		SingleJoinSuperArenaTask(SBean.SuperArenaJoin joinInfo, int arenaType, int grade, SingleJoinSuperArenaCallBack callback)
		{
			this.joinInfo = joinInfo;
			this.arenaType = arenaType;
			this.grade = grade;
			this.callback = callback;
		}

		@Override
		void doTaskImpl() 
		{
			gs.getRPCManager().notifyFightSingleJoinSuperArena(this.id, this.joinInfo, this.arenaType, this.grade);
		}

		@Override
		void onTimeout() 
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
		
		void onCallback(int ok)
		{
			this.callback.onCallback(ok);
		}
	}
	
	public void singleJoinSuperArenaImpl(SBean.SuperArenaJoin joinInfo, int arenaType, int grade, SingleJoinSuperArenaCallBack callback)
	{
		execTask(new SingleJoinSuperArenaTask(joinInfo, arenaType, grade, callback));
	}
	
	public void handleSingleJoinSuperArena(int tagID, int ok)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof SingleJoinSuperArenaTask)
		{
			SingleJoinSuperArenaTask st = SingleJoinSuperArenaTask.class.cast(task);
			if(st != null)
				st.onCallback(ok);
		}
	}
	//------------------------------------------------------------------------------------
	public interface SingleQuitSuperArenaCallBack
	{
		void onCallback(int ok);
	}
	
	public class SingleQuitSuperArenaTask extends FightServiceTask
	{
		final int roleID;
		final int arenaType;
		final int grade;
		final SingleQuitSuperArenaCallBack callback;
		
		SingleQuitSuperArenaTask(int roleID, int arenaType, int grade, SingleQuitSuperArenaCallBack callback)
		{
			this.roleID = roleID;
			this.arenaType = arenaType;
			this.grade = grade;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightSingleQuitSuperArena(this.id, roleID, arenaType, grade);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
		
		void onCallback(int ok)
		{
			this.callback.onCallback(ok);
		}
	}
	
	public void singleQuitSuperArenaImpl(int roleID, int arenaType, int grade, SingleQuitSuperArenaCallBack callback)
	{
		execTask(new SingleQuitSuperArenaTask(roleID, arenaType, grade, callback));
	}
	
	public void handleSingleQuitSuperArena(int tagID, int ok)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof FightServiceTask)
		{
			SingleQuitSuperArenaTask st = SingleQuitSuperArenaTask.class.cast(task);
			if(st != null)
				st.onCallback(ok);
		}
	}
	//------------------------------------------------------------------------------------
	public interface TeamJoinSuperArenaCallBack
	{
		void onCallback(int ok);
	}
	
	public class TeamJoinSuperArenaTask extends FightServiceTask
	{
		final List<SBean.SuperArenaJoin> members;
		final int arenaType;
		final int grade;
		final TeamJoinSuperArenaCallBack callback;
		
		TeamJoinSuperArenaTask(List<SBean.SuperArenaJoin> members, int arenaType, int grade, TeamJoinSuperArenaCallBack callback)
		{
			this.members = members;
			this.arenaType = arenaType;
			this.grade = grade;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightTeamJoinSuperArena(this.id, members, arenaType, grade);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
		
		void onCallback(int ok)
		{
			this.callback.onCallback(ok);
		}
	}
	
	public void teamJoinSuperArenaImpl(List<SBean.SuperArenaJoin> members, int arenaType, int grade, TeamJoinSuperArenaCallBack callback)
	{
		execTask(new TeamJoinSuperArenaTask(members, arenaType, grade, callback));
	}
	
	public void handleTeamJoinSuperArena(int tagID, int ok)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof TeamJoinSuperArenaTask)
		{
			TeamJoinSuperArenaTask tt = TeamJoinSuperArenaTask.class.cast(task);
			if(tt != null)
				tt.onCallback(ok);
		}
	}
	//------------------------------------------------------------------------------------
	public interface TeamQuitSuperArenaCallBack
	{
		void onCallback(int ok);
	}
	
	public class TeamQuitSuperArenaTask extends FightServiceTask
	{
		final int roleID;
		final int memberCount;
		final int arenaType;
		final int grade;
		final TeamQuitSuperArenaCallBack callback;
		
		TeamQuitSuperArenaTask(int roleID, int memberCount, int arenaType, int grade, TeamQuitSuperArenaCallBack callback)
		{
			this.roleID = roleID;
			this.memberCount = memberCount;
			this.arenaType = arenaType;
			this.grade = grade;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightTeamQuitSuperArena(this.id, roleID, memberCount, arenaType, grade);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR);
		}
		
		void onCallback(int ok)
		{
			this.callback.onCallback(ok);
		}
	}
	
	public void teamQuitSuperArenaImpl(int roleID, int memberCount, int arenaType, int grade, TeamQuitSuperArenaCallBack callback)
	{
		execTask(new TeamQuitSuperArenaTask(roleID, memberCount, arenaType, grade, callback));
	}
	
	public void handleTeamQuitSuperArena(int tagID, int ok)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof TeamQuitSuperArenaTask)
		{
			TeamQuitSuperArenaTask tt = TeamQuitSuperArenaTask.class.cast(task);
			if(tt != null)
				tt.onCallback(ok);
		}
	}
	//------------------------------------------------------------------------------------
	public interface QueryTeamMembersCallBack
	{
		void onCallback(List<SBean.RoleOverview> overviews);
	}
	
	public class QueryTeamMembersTask extends FightServiceTask
	{
		final int roleID;
		final QueryTeamMembersCallBack callback;
		
		QueryTeamMembersTask(int roleID, QueryTeamMembersCallBack callback)
		{
			this.roleID = roleID;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightQueryTeamMembers(this.id, roleID);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(GameData.emptyList());
		}
		
		void onCallback(List<SBean.RoleOverview> overviews)
		{
			this.callback.onCallback(overviews);
		}
	}
	
	public void queryTeamMembersImpl(int roleID, QueryTeamMembersCallBack callback)
	{
		execTask(new QueryTeamMembersTask(roleID, callback));
	}
	
	public void handleQueryTeamMembers(int tagID, List<SBean.RoleOverview> overviews)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof QueryTeamMembersTask)
		{
			QueryTeamMembersTask qt = QueryTeamMembersTask.class.cast(task);
			if(qt != null)
				qt.onCallback(overviews);
		}
	}
	//------------------------------------------------------------------------------------
	public interface QueryTeamMemberCallBack
	{
		void onCallback(SBean.RoleProfile profile);
	}
	
	public class QueryTeamMemberTask extends FightServiceTask
	{
		final int queryID;
		final QueryTeamMemberCallBack callback;
		
		QueryTeamMemberTask(int queryID, QueryTeamMemberCallBack callback)
		{
			this.queryID = queryID;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightQueryTeamMember(this.id, queryID);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(null);
		}
		
		void onCallback(SBean.RoleProfile profile)
		{
			this.callback.onCallback(profile);
		}
	}
	
	public void queryTeamMemberImpl(int queryID, QueryTeamMemberCallBack callback)
	{
		execTask(new QueryTeamMemberTask(queryID, callback));
	}
	
	public void handleQueryTeamMember(int tagID, SBean.RoleProfile profile)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof QueryTeamMemberTask)
		{
			QueryTeamMemberTask qt = QueryTeamMemberTask.class.cast(task);
			if(qt != null)
				qt.onCallback(profile);
		}
	}
	//------------------------------------------------------------------------------------	
	public interface SyncRoleDemonHoleReqCallBack
	{
		void onCallback(int curFloor, int grade);
	}
	
	public class SyncRoleDemonHoleReqTask extends FightServiceTask
	{
		final int roleID;
		final SyncRoleDemonHoleReqCallBack callback;
		
		SyncRoleDemonHoleReqTask(int roleID, SyncRoleDemonHoleReqCallBack callback)
		{
			this.roleID = roleID;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightSyncRoleDemonHoleReq(this.id, roleID);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(TIME_OUT_ERROR, 0);
		}
		
		void onCallBack(int curFloor, int grade)
		{
			callback.onCallback(curFloor, grade);
		}
	}
	
	public void syncRoleDemonHoleImpl(int roleID, SyncRoleDemonHoleReqCallBack callback)
	{
		execTask(new SyncRoleDemonHoleReqTask(roleID, callback));
	}
	
	public void handleSyncRoleDemonHoleRes(int tagID, int curFloor, int grade)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof SyncRoleDemonHoleReqTask)
		{
			SyncRoleDemonHoleReqTask st = SyncRoleDemonHoleReqTask.class.cast(task);
			if(st != null)
				st.onCallBack(curFloor, grade);
		}
	}
	//------------------------------------------------------------------------------------
	public interface RoleJoinDemonHoleReqCallBack
	{
		void onCallBack(int grade);
	}
	
	public class RoleJoinDemonHoleReqTask extends FightServiceTask
	{
		final SBean.RoleOverview role;
		final RoleJoinDemonHoleReqCallBack callback;
		
		RoleJoinDemonHoleReqTask(SBean.RoleOverview role, RoleJoinDemonHoleReqCallBack callback)
		{
			this.role = role;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightRoleJoinDemonHoleReq(this.id, role);
		}

		@Override
		void onTimeout()
		{
			callback.onCallBack(TIME_OUT_ERROR);
		}
		
		void onCallBack(int grade)
		{
			callback.onCallBack(grade);
		}
		
	}
	
	public void roleJoinDemonHoleReqImpl(SBean.RoleOverview role, RoleJoinDemonHoleReqCallBack callback)
	{
		execTask(new RoleJoinDemonHoleReqTask(role, callback));
	}
	
	public void handleRoleJoinDemonHoleRes(int tagID, int grade)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof RoleJoinDemonHoleReqTask)
		{
			RoleJoinDemonHoleReqTask rt = RoleJoinDemonHoleReqTask.class.cast(task);
			if(rt != null)
				rt.onCallBack(grade);
		}
	}
	//------------------------------------------------------------------------------------
	public interface RoleChangeDemonFloorReqCallBack
	{
		void onCallBack(int ok);
	}
	
	public class RoleChangeDemonFloorReqTask extends FightServiceTask
	{
		final SBean.RoleOverview role;
		final int floor;
		final RoleChangeDemonFloorReqCallBack callback;
		
		RoleChangeDemonFloorReqTask(SBean.RoleOverview role, int floor, RoleChangeDemonFloorReqCallBack callback)
		{
			this.role = role;
			this.floor = floor;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightRoleChangeDemonHoleFloorReq(this.id, role, floor);
		}

		@Override
		void onTimeout()
		{
			callback.onCallBack(TIME_OUT_ERROR);
		}
		
		void onCallBack(int ok)
		{
			callback.onCallBack(ok);
		}
	}
	
	public void roleChangeDemonFloorReqImpl(SBean.RoleOverview role, int floor, RoleChangeDemonFloorReqCallBack callback)
	{
		execTask(new RoleChangeDemonFloorReqTask(role, floor, callback));
	}
	
	public void handleRoleChangeDemonFloorRes(int tagID, int ok)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof RoleChangeDemonFloorReqTask)
		{
			RoleChangeDemonFloorReqTask rt = RoleChangeDemonFloorReqTask.class.cast(task);
			if(rt != null)
				rt.onCallBack(ok);
		}
	}
	//------------------------------------------------------------------------------------
	public interface RoleDemonHoleBattleReqCallBack
	{
		void onCallBack(List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total);
	}
	
	public class RoleDemonHoleBattleReqTask extends FightServiceTask
	{
		final int roleID;
		final RoleDemonHoleBattleReqCallBack callback;
		
		RoleDemonHoleBattleReqTask(int roleID, RoleDemonHoleBattleReqCallBack callback)
		{
			this.roleID = roleID;
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().notifyFightRoleDemonHoleBattleReq(this.id, roleID);
		}

		@Override
		void onTimeout()
		{
			callback.onCallBack(GameData.emptyList(), GameData.emptyList());
		}
		
		void onCallBack(List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
		{
			callback.onCallBack(curFloor, total);
		}
	}
	
	public void roleDemonHoleBattleReqImpl(int roleID, RoleDemonHoleBattleReqCallBack callback)
	{
		execTask(new RoleDemonHoleBattleReqTask(roleID, callback));
	}
	
	public void handleRoleDemonHoleBattleRes(int tagID, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		FightServiceTask task = this.peekTask(tagID);
		if(task == null)
			return;
		
		if(task instanceof RoleDemonHoleBattleReqTask)
		{
			RoleDemonHoleBattleReqTask rt = RoleDemonHoleBattleReqTask.class.cast(task);
			if(rt != null)
				rt.onCallBack(curFloor, total);
		}
	}
	
	//------------------------------------------------------------------------------------
	public void roleSendMsgFight(int id, int mapID, int mapInstance, MessageInfo messageInfo)
	{
		gs.getRPCManager().sendMsgFight(id, mapID, mapInstance, messageInfo);
	}
	
	public void roleSendMsgGlobalTeam(int roleID, MessageInfo messageInfo)
	{
		gs.getRPCManager().sendMsgGlobalTeam(roleID, messageInfo);
	}
	
	public void roleEnterDemonHoleFloor(SBean.RoleOverview role, int floor)
	{
		gs.getRPCManager().notifyFightRoleEnterDemonHoleMap(role, floor);
	}
	
	//------------------------------------------------------------------------------------
	public void handleRoleEnterForceWarMap(int roleID, int mapID, int mapInstance, boolean mainSpawn)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
//			role.syncRoleForceWarMatch(mapInstance > 0 ? GameData.PROTOCOL_OP_SUCCESS : mapInstance);
			if(mapInstance > 0)
				role.enterForceWarMapCopy(mapID, mapInstance, mainSpawn);
		}
	}
	
	public void handleStartForceWarMap(int mapID, int mapInstance)
	{
		gs.getMapService().handleSyncForceWarMapCopyStart(mapID, mapInstance);
	}
	
	public void handleSyncForceWarMapEnd(int mapID, int mapInstance, SBean.RankClearTime rankClearTime, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
	{
		gs.getMapService().handleSyncForceWarMapCopyEnd(mapID, mapInstance, rankClearTime, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
	}
	
	public void handleSyncDemonHoleMapEnd(int mapID, int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		gs.getMapService().handleSyncDemonHoleMapCopyEnd(mapID, mapInstance, curFloor, total);
	}
	
	public void handleSyncMapCopyTimeOut(int mapID, int mapInstance)
	{
		gs.getMapService().handleSyncMapCopyTimeOut(mapID, mapInstance);
	}
	
	public synchronized void handleSyncGSRankStart(int rankID, int snapshotCreateTime)
	{
		this.snapshotCaches.put(rankID, new SnapshotCache(snapshotCreateTime));
	}
	
	public synchronized void handleSyncGSRank(int rankID, List<SBean.RankRole> batch)
	{
		SnapshotCache cache = this.snapshotCaches.get(rankID);
		if(cache == null)
			return;
		
		cache.snapshot.addAll(batch);
	}
	
	public synchronized void handleSyncGSRankEnd(int rankID)
	{
		SnapshotCache cache = this.snapshotCaches.remove(rankID);
		if(cache == null)
			return;
		
		gs.getRankManager().updateRankSnapShot(rankID, cache.snapshot, cache.snapshotCreateTime);
	}
	
	public void handleCreateFightMapCopy(int mapID, int mapInstance)
	{
		gs.getMapService().createFightMapCopy(mapID, mapInstance);
	}
	
	public void handleRoleEnterSuperArenaMap(int roleID, int mapID, int mapInstance, boolean mainSpawnPos)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			if(mapInstance > 0)
				role.enterSuperArenaMapCopy(mapID, mapInstance, mainSpawnPos);
		}
	}
	
	public void handleSuperArenaMatchResult(int roleID, int arenaType, int grade, int result)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.superArenaMatchResult(result);
			gs.getArenaRoomManager().roleMatchSuccess(role, arenaType);
		}
	}
	
	public void handleSyncRoleFightTeam(int roleID, SBean.Team team)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.leaveTeam();
			role.onSelfJoinTeam(team, -1);
		}
	}
	
	public void handleSyncSuperArenaStart(int mapID, int mapInstance, Map<Integer, Integer> eloDiffs)
	{
		gs.getMapService().handleSyncSuperArenaMapCopyStart(mapID, mapInstance, eloDiffs);
	}
	
	public void handleSyncSuperArenaEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result, int rankClearTime)
	{
		gs.getMapService().handleSyncSuperArenaMapCopyEnd(mapID, mapInstance, result, rankClearTime);
	}
	
	public void handleTeamLeaderChange(int roleID, SBean.RoleOverview newLeader)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.onTeamLeaderChange(newLeader, -1);
		}
	}
	
	public void handleMemberLeaveTeam(int roleID, SBean.RoleOverview member)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.onTeamMemberLeave(member);
		}
	}
	
	public void handleTeamMemberUpdateHp(int roleID, int memberID, int memberHp, int memberHpMax)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.team_member_hp(memberID, memberHp, memberHpMax));
		}
	}
	
	public void handleFightTeamDissolve(int roleID)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.onTeamDissolve();
		}
	}
	
	public void handleSyncEnterSuperArenaRace(int mapID, int mapInstance)
	{
		gs.getMapService().handleSyncEnterSuperArenaRace(mapID, mapInstance);
	}
	
	public void handleSyncForceWarMatchResult(int roleID, int result)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.syncRoleForceWarMatch(result);
			if(result > 0)
				gs.getForceWarRoomManager().roomMemberLeave(role);
		}
	}
	
	public void handleRoleEnterDemonHoleMap(int roleID, int mapID, int mapInstance, int floor, int grade)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.enterDemonHoleMapCopy(mapID, mapInstance, floor, grade);
		}
	}
	
	public void handleCreateNewTeam(List<Integer> members)
	{
		gs.getTeamManager().createNewTeam(members);
	}
	
	public void handleSyncRoleChatRoom(int roleID, int mapID, int mapInstance, String roomID)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.syncFightChatRoom(mapID, mapInstance, roomID);
		}
	}
}
