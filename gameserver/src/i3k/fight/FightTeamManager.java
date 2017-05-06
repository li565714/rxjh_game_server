package i3k.fight;

import i3k.SBean;
import i3k.gs.FightService;
import i3k.gs.GameData;
import i3k.gs.TeamManager.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FightTeamManager
{
	FightServer fs;
	private AtomicInteger nextTeamID = new AtomicInteger();
	private ConcurrentMap<Integer, Integer> mapr2t = new ConcurrentHashMap<>(); 	//roleId - teamId
	private ConcurrentMap<Integer, Team> mapteams = new ConcurrentHashMap<>();		//teamId - team
	
	FightTeamManager(FightServer fs)
	{
		this.fs = fs;
	}
	
	private int getNextTeamID()
	{
		return -nextTeamID.incrementAndGet();
	}
	
	public synchronized Team createTeam(List<Integer> members)
	{
		if(members.size() <= 1)
			return null;
		
		Team team = new Team(getNextTeamID(), members);
		for(int roleID: members)
		{
			this.mapr2t.put(roleID, team.getID());
			fs.getRPCManager().notifyGSSyncRoleFightTeam(roleID, team.toTeam());
		}
		
		this.mapteams.put(team.getID(), team);
		return team;
	}
	
	public synchronized void roleLeaveTeam(int roleID)
	{
		SBean.RoleOverview member = fs.getRoleManager().getRoleOverview(roleID);
		if(member == null)
			return;
		
		Integer teamID = this.mapr2t.get(roleID);
		if(teamID == null)
			return;
		
		Team team = this.mapteams.get(teamID);
		if(team == null)
			return;
		
		this.roleLeaveTeamImpl(team, member);
	}
	
	private void roleLeaveTeamImpl(Team team, SBean.RoleOverview member)
	{
		SBean.RoleOverview changedLeader = null;
		if(member.id == team.getLeader())
		{
			team.changeLeader();
			changedLeader = fs.getRoleManager().getRoleOverview(team.getLeader());
		}
		
		team.removeMember(member.id);
		fs.getRPCManager().notifyGSMemberLeaveTeam(member.id, member);
		this.mapr2t.remove(member.id);
		if(team.isValid())
		{
			for(int rid : team.getAllMembers())
			{
				if(changedLeader != null)
					fs.getRPCManager().notifyGSTeamLeaderChange(rid, changedLeader);
				
				fs.getRPCManager().notifyGSMemberLeaveTeam(rid, member);
			}
		}
		else
		{
			this.mapteams.remove(team.getID());
			for(int rid : team.getAllMembers())
			{	
				this.mapr2t.remove(rid);
				fs.getRPCManager().notifyGSTeamDissolve(rid);
			}
		}
	}
	
	//TODO handle team member leave
	public synchronized void clearServerRole(SBean.RoleOverview role)
	{
		Integer teamID = this.mapr2t.remove(role.id);
		if(teamID == null)
			return;
		
		Team team = this.mapteams.get(teamID);
		if(team == null)
			return;
		
		this.roleLeaveTeamImpl(team, role);
	}
	
	public synchronized void queryTeamRoles(int roleID, FightService.QueryTeamMembersCallBack callback)
	{
		Integer teamID = this.mapr2t.get(roleID);
		if(teamID == null)
		{
			callback.onCallback(GameData.emptyList());
			return;
		}
		
		Team team = this.mapteams.get(teamID);
		if(team == null)
		{
			callback.onCallback(GameData.emptyList());
			return;
		}
		
		List<SBean.RoleOverview> ovs = new ArrayList<>();
		for(int rid: team.getAllMembers())
		{
			SBean.RoleOverview ov = fs.getRoleManager().getRoleOverview(rid);
			if(ov != null)
				ovs.add(ov);
		}
		callback.onCallback(ovs);
	}
	
	public synchronized void queryTeamRole(int queryID, FightService.QueryTeamMemberCallBack callback)
	{
		Integer teamID = this.mapr2t.get(queryID);
		if(teamID == null)
		{
			callback.onCallback(null);
			return;
		}
		
		Team team = this.mapteams.get(teamID);
		if(team == null)
		{
			callback.onCallback(null);
			return;
		}
		
		callback.onCallback(fs.getRoleManager().getRoleProfile(queryID));
	}
	
	public synchronized void notifyTeamMemberUpdateHp(int roleID, int hp, int hpMax)
	{
		Integer teamID = this.mapr2t.get(roleID);
		if(teamID == null)
			return;
		
		Team team = this.mapteams.get(teamID);
		if(team == null)
			return;
		
		for(int memberID: team.getAllMembers())
		{
			if(memberID != roleID)
				fs.getRPCManager().notifyGSTeamMemberUpdateHp(memberID, roleID, hp, hpMax);
		}
	}
	
	public synchronized void sendMsgGlobalTeam(int roleID, SBean.MessageInfo info)
	{
		Integer teamID = this.mapr2t.get(roleID);
		if(teamID == null)
			return;
		
		Team team = this.mapteams.get(teamID);
		if(team == null)
			return;
		
		for(int memberID: team.getAllMembers())
			fs.getRPCManager().notifyGSSendFightMsg(memberID, info);
	}
}
