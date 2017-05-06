package i3k.fight;

import i3k.SBean;
import i3k.gs.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;


public class RoleManager
{
	FightServer fs;
	ConcurrentMap<Integer, ServerRole> serverRoles = new ConcurrentHashMap<>();
	
	class ServerRole
	{
		ConcurrentMap<Integer, RoleInfo> mapRoles = new ConcurrentHashMap<>();
		
		void addRole(RoleInfo role)
		{
			this.mapRoles.put(role.profile.overview.id, role);
		}
		
		RoleInfo delRole(int roleID)
		{
			RoleInfo role = this.mapRoles.remove(roleID);
			if(role != null)
				fs.getGlobalMapService().roleLeaveMap(roleID, role.mapID, role.mapInstance);
			
			return role;
		}
		
		RoleInfo getRole(int roleID)
		{
			return this.mapRoles.get(roleID);
		}
		
		RoleInfo syncRoleHp(int roleID, int hp, int hpMax)
		{
			RoleInfo ri = this.mapRoles.get(roleID);
			if(ri != null)
				ri.update(hp, hpMax);
			return ri;
		}
		
		List<RoleInfo> clearRoles()
		{
			List<RoleInfo> lst = new ArrayList<>();
			for(RoleInfo role: this.mapRoles.values())
			{
				fs.getGlobalMapService().roleLeaveMap(role.profile.overview.id, role.mapID, role.mapInstance);
				lst.add(role);
			}
			mapRoles.clear();
			return lst;
		}
	}
	
	class RoleInfo
	{
		SBean.RoleProfile profile;
		final int mapID;
		final int mapInstance;
		
		RoleInfo(SBean.RoleProfile profile, int mapID, int mapInstance)
		{
			this.profile = profile;
			this.mapID = mapID;
			this.mapInstance = mapInstance;
		}
		
		synchronized void update(int hp, int hpMax)
		{
			this.profile.curHp = hp;
			this.profile.maxHp = hpMax;
		}
	}
	
	RoleManager(FightServer fs)
	{
		this.fs = fs;
	}
	
	void roleEnterMap(SBean.RoleOverview ov, int mapID, int mapInstance)
	{
		Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(ov.id));
		if(serverID == null)
			return;
		
		ServerRole sr = this.serverRoles.get(serverID);
		if(sr == null)
		{
			sr = new ServerRole();
			this.serverRoles.put(serverID, sr);
		}
		sr.addRole(new RoleInfo(new SBean.RoleProfile(ov, 0, 0), mapID, mapInstance));
	}
	
	void roleLeaveMap(int roleID)
	{
		Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(serverID == null)
			return;
		
		ServerRole sr = this.serverRoles.get(serverID);
		if(sr != null)
			sr.delRole(roleID);
	}
	
	SBean.RoleProfile getRoleProfile(int roleID)
	{
		Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(serverID == null)
			return null;
		
		ServerRole sr = this.serverRoles.get(serverID);
		if(sr == null)
			return null;
		
		RoleInfo ri = sr.getRole(roleID);
		return ri == null ? null : ri.profile.kdClone(); 
	}
	
	SBean.RoleOverview getRoleOverview(int roleID)
	{
		Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(serverID == null)
			return null;
		
		ServerRole sr = this.serverRoles.get(serverID);
		if(sr == null)
			return null;
		
		RoleInfo ri = sr.getRole(roleID);
		return ri == null ? null : ri.profile.overview.kdClone();
	}
	
	void syncRoleHp(int roleID, int hp, int hpMax)
	{
		Integer serverID = fs.getRPCManager().getServerIDByZoneID(GameData.getZoneIdFromRoleId(roleID));
		if(serverID == null)
			return;
		
		ServerRole sr = this.serverRoles.get(serverID);
		if(sr == null)
			return;
		
		RoleInfo role = sr.syncRoleHp(roleID, hp, hpMax);
		if(role == null)
			return;
		
		if(role != null)
			fs.getFightTeamManager().notifyTeamMemberUpdateHp(roleID, hp, hpMax);
	}
	
	void clearServerRoles(int serverID)
	{
		ServerRole sr = this.serverRoles.remove(serverID);
		if(sr != null)
		{
			List<RoleInfo> lst = sr.clearRoles();
			lst.forEach(role -> fs.getFightTeamManager().clearServerRole(role.profile.overview));
		}
	}
}
