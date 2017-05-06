
package i3k.gs;

import i3k.SBean;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TeamManager
{		
	public TeamManager(GameServer gs)
	{
		this.gs = gs;
	}
	
	public static class Team
	{
		final int id;
		List<Integer> members = new ArrayList<>();
		
		Team(int id, int leader, int member)
		{
			this.id = id;
			this.members.add(leader);
			this.members.add(member);
		}
		
		Team(int id, int leader, Collection<Integer> members)
		{
			this.id = id;
			this.members.add(leader);
			this.members.addAll(members);
		}
		
		public Team(int id, Collection<Integer> members)
		{
			this.id = id;
			this.members.addAll(members);
		}
		
		public int getID()
		{
			return this.id;
		}
		
		public int getLeader()
		{
			if (!isValid())
				return -1;
			return this.members.get(0);
		}
		
		public boolean isValid()
		{
			return this.members.size() > 1;
		}
		
		public void changeLeader()
		{
			if (!isValid())
				return;
			Integer oldLeader = this.members.remove(0);
			this.members.add(oldLeader);
		}
		
		void changeLeader(int rid)
		{
			if (!isValid())
				return;
			if (!this.members.contains(rid))
				return;
			Integer oldLeader = this.members.remove(0);
			this.members.remove(Integer.valueOf(rid));
			this.members.add(0, rid);
			this.members.add(oldLeader);
		}
		
		void addMember(int rid)
		{
			if (!isValid())
				return;
			if (this.members.contains(rid))
				return;
			this.members.add(rid);
		}
		
		public void removeMember(int rid)
		{
			if (!isValid())
				return;
			if (rid == this.members.get(0))
				return;
			this.members.remove(Integer.valueOf(rid));
		}
		
		public Collection<Integer> getAllMembers()
		{
			return this.members;
		}
		
		public SBean.Team toTeam()
		{
			List<Integer> lst = new ArrayList<>();
			lst.addAll(this.members);
			return new SBean.Team(this.id, this.getLeader(), lst);
		}
	}
	
	public synchronized boolean roleSendMsg(int memberId, SBean.MessageInfo msg)
	{
		Integer tidInt = mapr2t.get(memberId);
		if (tidInt == null)
			return false;
		Team team = mapteams.get(tidInt);
		if (team == null)
			return false;
		for (int rid : team.getAllMembers())
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.receiveMsg(msg);
			}
		}
		return true;
	}
	
	public synchronized void getRoleTeamRoles(Role queryRole)
	{
		Integer tidInt = mapr2t.get(queryRole.id);
		if (tidInt == null)
		{
			gs.getRPCManager().sendStrPacket(queryRole.netsid, new SBean.team_self_res(GameData.emptyList()));
			return;
		}
		Team team = mapteams.get(tidInt);
		if (team == null)
		{
			gs.getRPCManager().sendStrPacket(queryRole.netsid, new SBean.team_self_res(GameData.emptyList()));
			return;
		}
		
		List<SBean.RoleOverview> overviews = new ArrayList<>();
		for (int rid : team.getAllMembers())
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				overviews.add(role.getRoleOverview());
			}
		}
		gs.getRPCManager().sendStrPacket(queryRole.netsid, new SBean.team_self_res(overviews));
	}
	
	interface EnterMapCopy
	{
		void enter(Role role, int mapId, int instanceId, boolean mainSpawnPos);
	}
	
	public synchronized int forceCreateNewTeamStartMapCopy(Role leaderRole, Collection<Integer> allRoomRids, int mapId, int instanceId, boolean mainSpawnPos, boolean realTeam, EnterMapCopy enterFunc)
	{
		List<Integer> lst = new ArrayList<>();
		allRoomRids.stream().filter(rid -> rid != leaderRole.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				leaveTeamImpl(role);
				lst.add(role.id);
			}
		});
		leaveTeamImpl(leaderRole);
		Team team = createNewTeamImpl(leaderRole.id, lst, realTeam);
		int leaderLine = leaderRole.gameMapContext.getCurMapLine();
		
		for (int rid : allRoomRids)
		{
			Role role = rid == leaderRole.id ? leaderRole : gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.onSelfJoinTeam(team.toTeam(), leaderLine);
				enterFunc.enter(role, mapId, instanceId, mainSpawnPos);
			}
		}
		return team.id;
	}
	
	public synchronized int forceCreateNewTeam(Role leaderRole, Collection<Integer> allRoomRids, boolean realTeam)
	{
		List<Integer> lst = new ArrayList<>();
		allRoomRids.stream().filter(rid -> rid != leaderRole.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				leaveTeamImpl(role);
				lst.add(role.id);
			}
		});
		leaveTeamImpl(leaderRole);
		int leaderLine = leaderRole.gameMapContext.getCurMapLine();
		Team team = createNewTeamImpl(leaderRole.id, lst, realTeam);
		leaderRole.onSelfJoinTeam(team.toTeam(), leaderLine);
		allRoomRids.stream().filter(rid -> rid != leaderRole.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.onSelfJoinTeam(team.toTeam(), leaderLine);
			}
		});
		return team.id;
	}

	public synchronized int addTeamMember(Role maybeLeader, Role member)
	{
		if (maybeLeader == member)
			return GameData.PROTOCOL_OP_FAILED;
		if (!maybeLeader.testCanJoinTeam() || !member.testCanJoinTeam())
			return GameData.PROTOCOL_OP_FAILED;
		if (mapr2t.containsKey(member.id))
			return GameData.PROTOCOL_OP_TEAM_ALREADY_IN_TEAM;
		Integer tidInt = mapr2t.get(maybeLeader.id);
		if (tidInt == null)
		{
			int leaderLine = maybeLeader.gameMapContext.getCurMapLine();
			Team team = createNewTeamImpl(maybeLeader.id, member.id);
			maybeLeader.onSelfJoinTeam(team.toTeam(), leaderLine);
			member.onSelfJoinTeam(team.toTeam(), leaderLine);
			return team.id;
		}
		else
		{
			Team team = mapteams.get(tidInt);
			if (team == null)
				return GameData.PROTOCOL_OP_FAILED;
			if (team.getAllMembers().size() >= GameData.TEAM_MAX_COUNT)
				return GameData.PROTOCOL_OP_TEAM_TEAM_FULL;
			team.addMember(member.id);
			mapr2t.put(member.id, tidInt);
			team.getAllMembers().stream().filter(rid -> rid != member.id).forEach(rid -> {
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
				{
					role.onNewMemberJoinTeam(member);
				}
			});
			member.onSelfJoinTeam(team.toTeam(), maybeLeader.getLeaderLine());
			return team.id;
		}
	}
	
	public synchronized boolean teamMemberLeave(Role memberRole)
	{
		return leaveTeamImpl(memberRole);
	}
	
	public synchronized boolean kickTeamMember(Role leaderRole, Role kickedRole)
	{
		if (leaderRole == kickedRole)
			return false;
		Integer tidInt = mapr2t.get(leaderRole.id);
		if (tidInt == null)
			return false;
		Team team = mapteams.get(tidInt);
		if (team == null)
			return false;
		if (team.getLeader() != leaderRole.id)
			return false;
		if (!team.getAllMembers().contains(kickedRole.id))
			return false;
		team.removeMember(kickedRole.id);
		mapr2t.remove(kickedRole.id);
		kickedRole.onTeamMemberKicked(kickedRole);
		for (int rid : team.getAllMembers())
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.onTeamMemberKicked(kickedRole);
			}
		}	
		if (!team.isValid())
		{
			mapteams.remove(tidInt);
			for (int rid : team.getAllMembers())
			{
				mapr2t.remove(rid);
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
				{
					role.onTeamDissolve();
				}	
			}
		}
		return true;
	}
	
	public synchronized boolean dissolveTeam(Role leaderRole)
	{
		Integer tidInt = mapr2t.get(leaderRole.id);
		if (tidInt == null)
			return false;
		Team team = mapteams.get(tidInt);
		if (team == null)
			return false;
		if (team.getLeader() != leaderRole.id)
			return false;
		mapteams.remove(tidInt);
		for (int rid : team.getAllMembers())
		{
			mapr2t.remove(rid);
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.onTeamDissolve();
			}	
		}
		return true;
	}
	
	
	public synchronized boolean teamChangeLeader(Role leaderRole, Role newLeaderRole)
	{
		if (leaderRole == newLeaderRole)
			return false;
		Integer tidInt = mapr2t.get(leaderRole.id);
		if (tidInt == null)
			return false;
		Team team = mapteams.get(tidInt);
		if (team == null)
			return false;
		if (team.getLeader() != leaderRole.id)
			return false;
		if (!team.getAllMembers().contains(newLeaderRole.id))
			return false;
		team.changeLeader(newLeaderRole.id);
		int leaderLine = newLeaderRole.gameMapContext.getCurMapLine();
		for (int rid : team.getAllMembers())
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.onTeamLeaderChange(newLeaderRole, leaderLine);
			}	
		}
		return true;
	}
	
	public synchronized void createNewTeam(List<Integer> members)
	{
		Iterator<Integer> it = members.iterator();
		List<Role> roles = new ArrayList<>();
		while(it.hasNext())
		{
			int rid = it.next();
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if(role == null || role.isInTeam())
				it.remove();
			else
				roles.add(role);
		}
		
		if(roles.size() <= 1 || roles.size() > GameData.TEAM_MAX_COUNT)
			return;
		
		Team t = new Team(nextTeamID.incrementAndGet(), members);
		int tid = t.getID();
		mapteams.put(tid, t);
		
		int leaderLine = roles.get(0).gameMapContext.getCurMapLine();
		for(Role role: roles)
		{
			mapr2t.put(role.id, tid);
			if(role != null)
				role.onSelfJoinTeamImpl(t.toTeam(), leaderLine);
		}
	}
	
	private Team createNewTeamImpl(int leader, int member)
	{
		Team t = new Team(nextTeamID.incrementAndGet(), leader, member);
		int tid = t.getID();
		mapteams.put(tid, t);
		mapr2t.put(leader, tid);
		mapr2t.put(member, tid);
		return t;
	}
	
	private Team createNewTeamImpl(int leader, Collection<Integer> members, boolean realTeam)
	{
		int teamID = nextTeamID.incrementAndGet();
		Team t = new Team(realTeam ? teamID : -teamID, leader, members);
		int tid = t.getID();
		mapteams.put(tid, t);
		mapr2t.put(leader, tid);
		for (int member : members)
		{
			mapr2t.put(member, tid);
		}
		return t;
	}
	
	private boolean leaveTeamImpl(Role memberRole)
	{
		Integer tidInt = mapr2t.get(memberRole.id);
		if (tidInt == null)
			return false;
		
		Team team = mapteams.get(tidInt);
		if (team == null)
			return false;
		
		Role changedleaderRole = null;
		if (team.getLeader() == memberRole.id)
		{
			team.changeLeader();
			changedleaderRole = gs.getLoginManager().getOnGameRole(team.getLeader());
		}
		team.removeMember(memberRole.id);
		mapr2t.remove(memberRole.id);
		memberRole.onTeamMemberLeave(memberRole);
		if (team.isValid())
		{
			int leaderLine = changedleaderRole == null ? -1 : changedleaderRole.gameMapContext.getCurMapLine();
			for (int rid : team.getAllMembers())
			{
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
				{
					if (changedleaderRole != null)
						role.onTeamLeaderChange(changedleaderRole, leaderLine);
					role.onTeamMemberLeave(memberRole);
				}
			}
		}
		else
		{
			mapteams.remove(tidInt);
			for (int rid : team.getAllMembers())
			{
				mapr2t.remove(rid);
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
				{
					role.onTeamMemberLeave(memberRole);
					role.onTeamDissolve();
				}
			}
		}
		return true;
	}
	
	public synchronized int getTeamLeader(int teamId)
	{
		Team team = mapteams.get(teamId);
		if(team == null)
			return -1;
		return team.getLeader();
	}
	
	public synchronized int getRoleTeamLeader(int roleId)
	{
		Integer tid = mapr2t.get(roleId);
		if (tid == null)
			return -1;
		return getTeamLeader(tid);
	}
	
	public synchronized void notifyMemberUpdateLeaderLine(int teamID, int leaderLine)
	{
		Team team = mapteams.get(teamID);
		if(team == null)
			return;
		for(int memberID: team.getAllMembers())
		{
			Role member = gs.getLoginManager().getOnGameRole(memberID);
			if(member != null)
				member.updateLeaderLine(leaderLine);
		}
	}
	
	final GameServer gs;
	private AtomicInteger nextTeamID = new AtomicInteger();
	private ConcurrentMap<Integer, Team> mapteams = new ConcurrentHashMap<>();			//teamId - team
	private ConcurrentMap<Integer, Integer> mapr2t = new ConcurrentHashMap<>(); 	//roleId - teamId
}

