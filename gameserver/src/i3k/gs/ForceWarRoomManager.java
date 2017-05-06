package i3k.gs;

import i3k.SBean;
import i3k.gs.FightService.TeamJoinForceWarCallBack;
import i3k.gs.Role.ForceWarQuitCallBack;
import i3k.util.GameTime;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ForceWarRoomManager
{
	GameServer gs;
	Map<Integer, ForceWarRoom> rooms = new HashMap<>();
	Map<Integer, Integer> role2room = new HashMap<>();
	private AtomicInteger nextRoomID = new AtomicInteger();
	
	ForceWarRoomManager(GameServer gs)
	{
		this.gs = gs;
	}
	
	public class ForceWarRoom
	{
		final int id;
		final byte BWType;
		final int type;
		List<Integer> members = new ArrayList<>();
		
		ForceWarRoom(int id, byte BWType, int leader, int type)
		{
			this.id = id;
			this.BWType = BWType;
			this.type = type;
			this.members.add(leader);
		}
		
		int getID()
		{
			return this.id;
		}
		
		byte getBWType()
		{
			return this.BWType;
		}
		
		int getType()
		{
			return this.type;
		}
		
		int getLeader()
		{
			if (!isValid())
				return -1;
			return this.members.get(0);
		}
		
		boolean isValid()
		{
			return this.members.size() > 0;
		}
		
		void changeLeader()
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
			if (!this.containsMember(rid))
				return;
			Integer oldLeader = this.members.remove(0);
			this.members.remove(Integer.valueOf(rid));
			this.members.add(0, rid);
			this.members.add(oldLeader);
		}
		
		SBean.FRoom toFRoom()
		{
			return new SBean.FRoom(this.id, this.type, this.getLeader(), new ArrayList<Integer>(this.members));
		}
		
		int getMemberCount()
		{
			return this.members.size();
		}
		
		boolean containsMember(int rid)
		{
			for(int memberID: this.members)
			{
				if(memberID == rid)
					return true;
			}
			
			return false;
		}
		
		void addMember(int rid)
		{
			if (containsMember(rid))
				return;
			this.members.add(rid);
		}
		
		void removeMember(int rid)
		{
			if (rid == this.members.get(0) && this.members.size() > 1)
				return;
			this.members.remove(Integer.valueOf(rid));
		}
		
		Collection<Integer> getAllMembers()
		{
			return this.members;
		}
	}
	
	public synchronized int createRoom(Role leader, int type)
	{
		Integer roomID = this.role2room.get(leader.id);
		if(roomID != null)
			return GameData.PROTOCOL_OP_FAILED;
		
		ForceWarRoom room = new ForceWarRoom(nextRoomID.incrementAndGet(), leader.BWType, leader.id, type);
		this.rooms.put(room.getID(), room);
		this.role2room.put(leader.id, room.getID());
		leader.onSelfJoinFRoom(room.toFRoom());
		
		return room.getID();
	}
	
	public synchronized int addRoomMember(int inviteStartRoleID, Role member)
	{
		Integer roomID = this.role2room.get(inviteStartRoleID);
		if(roomID == null)
			return GameData.PROTOCOL_OP_FROOM_ROOM_NOT_EXIST;
		
		return addRoomMemberImpl(roomID, member);
	}
	
	private int addRoomMemberImpl(int roomID, Role member)
	{
		ForceWarRoom room = this.rooms.get(roomID);
		if(room == null)
			return GameData.PROTOCOL_OP_FROOM_ROOM_NOT_EXIST;
		
		if(room.getMemberCount() >= GameData.FORCEWAR_ROOM_MAX_COUNT)
			return GameData.PROTOCOL_OP_FROOM_ROOM_FULL;
		
		if(room.containsMember(member.id))
			return GameData.PROTOCOL_OP_FROOM_ALREADY_IN_ROOM;
		
		roomMemberLeaveImpl(member);
		room.addMember(member.id);
		room.getAllMembers().stream().filter(rid -> rid != member.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if(role == null)
				return;
			
			role.onNewMemberJoinFRoom(member);
		});
		member.onSelfJoinFRoom(room.toFRoom());
		this.role2room.put(member.id, roomID);
		return roomID;
	}
	
	private boolean roomMemberLeaveImpl(Role member)
	{
		Integer roomID = this.role2room.get(member.id);
		if(roomID == null)
			return false;
		
		ForceWarRoom room = this.rooms.get(roomID);
		if(room == null)
			return false;
		
		Role changedleaderRole = null;
		if(room.getLeader() == member.id && room.getMemberCount() > 1)
		{
			room.changeLeader();
			changedleaderRole = gs.getLoginManager().getOnGameRole(room.getLeader());
		}
		
		room.removeMember(member.id);
		this.role2room.remove(member.id);
		member.onFRoomMemberLeave(member);
		if(room.isValid())
		{
			for(int rid: room.getAllMembers())
			{
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if(role == null)
					continue;
				
				if(changedleaderRole != null)
					role.onFRoomLeaderChange(changedleaderRole);
				
				role.onFRoomMemberLeave(member);
			}
		}
		else
		{
			this.rooms.remove(roomID);
		}
		
		return true;
	}
	
	public synchronized boolean kickRoomMember(Role leader, Role kickedRole)
	{
		if(leader.id == kickedRole.id)
			return false;
		
		Integer roomID = this.role2room.get(leader.id);
		if(roomID == null)
			return false;
		
		ForceWarRoom room = this.rooms.get(roomID);
		if(room == null || room.getLeader() != leader.id || !room.containsMember(kickedRole.id))
			return false;
		
		room.removeMember(kickedRole.id);
		this.role2room.remove(kickedRole.id);
		kickedRole.onFRoomMemberKicked(kickedRole);
		room.getAllMembers().forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if(role == null)
				return;

			role.onFRoomMemberKicked(kickedRole);
		});
		
		return true;
	}
	
	public synchronized boolean roomMemberLeave(Role member)
	{
		return roomMemberLeaveImpl(member);
	}
	
	public synchronized boolean froomChangeLeader(Role leader, Role newLeader)
	{
		if (leader.id == newLeader.id)
			return false;
		
		Integer roomID = this.role2room.get(leader.id);
		if(roomID == null)
			return false;
		
		ForceWarRoom room = this.rooms.get(roomID);
		if(room == null || room.getLeader() != leader.id || !room.containsMember(newLeader.id))
			return false;
		
		room.changeLeader(newLeader.id);
		room.getAllMembers().stream().filter(rid -> rid != leader.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role == null)
				return;
			
			role.onFRoomLeaderChange(newLeader);
		});
		
		leader.froom.leader = newLeader.id;
		return true;
	}
	
	public synchronized void getRoleRoomRoles(Role queryRole)
	{
		Integer roomID = this.role2room.get(queryRole.id);
		if(roomID == null)
		{
			gs.getRPCManager().sendStrPacket(queryRole.netsid, new SBean.froom_members_overview(GameData.emptyList(), GameData.emptyMap()));
			return;
		}
		
		
		ForceWarRoom room = this.rooms.get(roomID);
		if (room == null)
		{
			gs.getRPCManager().sendStrPacket(queryRole.netsid, new SBean.froom_members_overview(GameData.emptyList(), GameData.emptyMap()));
			return;
		}
		
		List<SBean.RoleOverview> overviews = new ArrayList<>();
		Map<Integer, Integer> states = new HashMap<>();
		room.getAllMembers().forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if(role != null)
			{
				overviews.add(role.getRoleOverview());
				states.put(role.id, role.netsid);
			}
		});
		
		gs.getRPCManager().sendStrPacket(queryRole.netsid, new SBean.froom_members_overview(overviews, states));
	}
	
	public synchronized void teamJoin(int roleID, TeamJoinForceWarCallBack callback)
	{
		Integer roomID = this.role2room.get(roleID);
		if(roomID == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FROOM_ROOM_NOT_EXIST);
			return;
		}
		
		ForceWarRoom room = this.rooms.get(roomID);
		if(room == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FROOM_ROOM_NOT_EXIST);
			return;
		}
		
		if(room.getLeader() != roleID)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		List<SBean.ForceWarJoin> members = new ArrayList<>();
		for(int memberID: room.getAllMembers())
		{
			Role member = gs.getLoginManager().getOnGameRole(memberID);
			if(member == null)
			{
				callback.onCallback(GameData.PROTOCOL_OP_FROOM_ROLE_OFFLINE);
				return;
			}
			
			members.add(member.getForceWarJoin());
		}
		gs.getFightService().teamJoinForceWarImpl(members, room.getBWType(), room.getType(), ok -> 
		{
			if(ok > 0)
			{
				for(SBean.ForceWarJoin e: members)
				{
					if(e.overview.id == roleID)
						continue;
					
					Role member = gs.getLoginManager().getOnGameRole(e.overview.id);
					if(member != null)
					{
						synchronized(member)
						{
							member.forceWarInfo.joinTime = GameTime.getTime();
							member.forceWarInfo.joinType = room.getType();
						}
						gs.getRPCManager().sendStrPacket(member.netsid, new SBean.forcewar_startmatch());  
					}
				}
			}
			
			callback.onCallback(ok);
		});
	}
	
	public synchronized void teamQuit(Role role, ForceWarQuitCallBack callback)
	{
		Integer roomID = this.role2room.get(role.id);
		if(roomID == null)
		{
			if(callback != null)
				callback.onCallback(GameData.PROTOCOL_OP_FROOM_ROOM_NOT_EXIST);
			return;
		}
		
		ForceWarRoom room = this.rooms.get(roomID);
		if(room == null)
		{
			if(callback != null)
				callback.onCallback(GameData.PROTOCOL_OP_FROOM_ROOM_NOT_EXIST);
			return;
		}
		
		List<Integer> members = new ArrayList<>(room.getAllMembers());
		gs.getFightService().teamQuitForceWarImpl(role.id, room.getBWType(), members, room.getType(), ok -> 
		{
			synchronized(role)
			{
				role.forceWarInfo.joinTime = 0;
				role.forceWarInfo.joinType = 0;
			}
			
			for(int memberID: members)
			{
				if(memberID != role.id)
				{
					Role member = gs.getLoginManager().getOnGameRole(memberID);
					if(member != null)
					{
						synchronized(member)
						{
							if(member.forceWarInfo.joinTime != 0)
								gs.getRPCManager().sendStrPacket(member.netsid, new SBean.forcewar_other_quit(role.id, role.name));
							
							member.forceWarInfo.joinTime = 0;
						}
					}
					
				}
			}
			
			if(callback != null)
				callback.onCallback(ok);
		});
	}
}
