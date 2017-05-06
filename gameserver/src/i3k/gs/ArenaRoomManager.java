package i3k.gs;

import i3k.SBean;
import i3k.gs.Role.SuperArenaQuitCallBack;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ArenaRoomManager
{
	final GameServer gs;
	Map<Integer, ArenaRoomContainer> containers = new HashMap<>();
	ArenaRoomManager(GameServer gs)
	{
		this.gs = gs;
	}
	
	ArenaRoomManager init()
	{
		for(int arenaType: GameData.getInstance().getAllSuperArenaTypes())
			this.containers.put(arenaType, new ArenaRoomContainer(arenaType));
		
		return this;
	}
	
	public void onTimer(int timeTick)
	{
		//TODO
	}
	
	public int createArenaRoom(Role leaderRole, int arenaType)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		return container.createArenaRoom(leaderRole);
	}
	
	public void teamJoinMatch(Role leaderRole, int arenaType)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
		{
			gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_FAILED));
			return;
		}
		
		container.teamJoinMatch(leaderRole);
	}
	
	public void quitMatch(Role quitRole, SBean.ARoom room, SuperArenaQuitCallBack callback)
	{
		ArenaRoomContainer container = this.containers.get(room.type);
		if(container == null)
		{
			if(callback != null)
				callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		container.quitMatch(quitRole, room, callback);
	}
	
	public boolean roomMemberLeave(Role memberRole, int arenaType, boolean ready)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
			return false;
		
		return container.roomMemberLeave(memberRole, ready);
	}
	
	public void roleMatchSuccess(Role role, int arenaType)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
			return;
		
		container.roomMemberLeaveImpl(role, true);
	}
	
	public int addRoomMember(int inviteStartRoleID, int grade, int arenaType, Role newMember)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		return container.addRoomMember(inviteStartRoleID, grade, newMember);
	}
	
	public boolean kickRoomMember(Role leaderRole, Role kickedRole, int arenaType)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
			return false;
		
		return container.kickRoomMember(leaderRole, kickedRole);
	}
	
	public boolean aroomChangeLeader(Role leaderRole, Role newLeaderRole, int arenaType)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
			return false;
		
		return container.aroomChangeLeader(leaderRole, newLeaderRole);
	}
	
	public List<SBean.RoleOverview> getRoleRoomRoles(int queryRoleID, int grade, int arenaType, boolean ready)
	{
		ArenaRoomContainer container = this.containers.get(arenaType);
		if(container == null)
			return GameData.emptyList();
		
		return container.getRoleRoomRoles(queryRoleID, grade, ready);
	}
	
	public void clearReadyRoom()
	{
		this.containers.values().forEach(container -> container.clearReadyRoom());
	}
	
	public class ArenaRoomContainer
	{
		private AtomicInteger nextRoomID = new AtomicInteger(1);
		ConcurrentMap<Integer, ArenaRoomCluster> roomCluster = new ConcurrentHashMap<>();		//<grade, ArenaRoomCluster>
		ConcurrentMap<Integer, Integer> mapr2Room = new ConcurrentHashMap<>();					//<rid, roomID>
		final int arenaType;
		final SBean.SuperArenaTypeCFGS typeCfg;
		
		ArenaRoomContainer(int arenaType)
		{
			this.arenaType = arenaType;
			this.typeCfg = GameData.getInstance().getSuperArenaTypeCFG(arenaType);
		}
		
		public class ArenaRoomCluster
		{
			final int grade;
			ConcurrentMap<Integer, ArenaRoom> unReadyRooms = new ConcurrentHashMap<>();		//没有开始匹配的房间<roomID, ArenaRoom>
			TreeMap<Integer, ArenaRoom> readyRooms = new TreeMap<>();						//需要匹配的房间 <roomID, ArenaRoom>
			
			ArenaRoomCluster(int grade)
			{
				this.grade = grade;
			}
		}
		
		public class ArenaRoomPair
		{
			List<ArenaRoom> rooms = new ArrayList<>();
			ArenaRoomPair(ArenaRoom room1, ArenaRoom room2)
			{
				rooms.add(room1);
				rooms.add(room2);
			}
		}
		
		public class ArenaRoom
		{
			final private int id;
			final int grade;
			List<Integer> members = new ArrayList<>();
			int matchTimes;
			int roomPower;
			int joinTime;
			
			ArenaRoom(int id, int leader, int grade)
			{
				this.id = id;
				this.members.add(leader);
				this.grade = grade;
			}

			
			int getID()
			{
				return this.id;
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
				if (!this.members.contains(rid))
					return;
				Integer oldLeader = this.members.remove(0);
				this.members.remove(Integer.valueOf(rid));
				this.members.add(0, rid);
				this.members.add(oldLeader);
			}
			
			void addMember(int rid)
			{
				if (this.members.contains(rid))
					return;
				this.members.add(rid);
			}
			
			void removeMember(int rid)
			{
				if (rid == this.members.get(0) && this.members.size() > 1)
					return;
				this.members.remove(Integer.valueOf(rid));
			}
			
			boolean containsMember(int rid)
			{
				return this.members.contains(rid);
			}
			
			Collection<Integer> getAllMembers()
			{
				return this.members;
			}
			
			void setJoinTime(int joinTime)
			{
				this.joinTime = joinTime;
			}
			
			boolean isMatchTimeOut()
			{
				return GameTime.getTime() - this.joinTime >= GameData.getInstance().getSuperArenaCFGS().maxMatchTime;
			}
			
			SBean.ARoom toARoom()
			{
				return new SBean.ARoom(this.id, ArenaRoomContainer.this.arenaType, this.grade, this.getLeader(), new ArrayList<Integer>(this.members));
			}
			
			int getMemberCount()
			{
				return this.members.size();
			}
		}
		
		public synchronized void teamJoinMatch(Role leaderRole)
		{
			if(!GameData.isSuperArenaInOpenTime(this.typeCfg))
			{
				gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_FAILED));
				return;
			}
			
			if(GameData.getInstance().getSuperArenaGrade(leaderRole.arenaroom.type, leaderRole.level) != leaderRole.arenaroom.grade)
			{
				gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_AROOM_ROLE_GRADE_FAIL));
				return;
			}
			
			Integer roomID = mapr2Room.get(leaderRole.id);
			if(roomID == null || roomID != leaderRole.arenaroom.id)
			{
				gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_AROOM_ROOM_NOT_EXIST));
				return;
			}
			
			ArenaRoomCluster cluster = roomCluster.get(leaderRole.arenaroom.grade);
			if(cluster == null)
			{
				gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_AROOM_ROOM_NOT_EXIST));
				return;
			}
			
			ArenaRoom room = cluster.unReadyRooms.get(roomID);
			if(room == null)
			{
				gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_AROOM_ROOM_NOT_EXIST));
				return;
			}
			
//			if(room.members.size() != this.typeCfg.members || leaderRole.id != room.getLeader())
			if(leaderRole.id != room.getLeader())
			{
				gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_FAILED));
				return;
			}
			
			Set<Role> members = new HashSet<>();
			List<SBean.SuperArenaJoin> joinInfos = new ArrayList<>();
			joinInfos.add(leaderRole.getSuperArenaJoinInfo(this.typeCfg.type));
			for(int rid : room.getAllMembers())
			{
				if(rid != leaderRole.id)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if(role == null)
					{
						gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_AROOM_ROLE_OFFLINE));
						return;
					}
					
					if(GameData.getInstance().getSuperArenaGrade(role.arenaroom.type, leaderRole.level) != role.arenaroom.grade)
					{
						gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(GameData.PROTOCOL_OP_AROOM_ROLE_GRADE_FAIL));
						return;
					}
					members.add(role);
					joinInfos.add(role.getSuperArenaJoinInfo(this.typeCfg.type));
				}
			}
			
			gs.getFightService().teamJoinSuperArenaImpl(joinInfos, this.arenaType, leaderRole.arenaroom.grade, ok -> {
				synchronized(this)
				{
					if(ok > 0)
					{
						leaderRole.superarenaState = GameData.SUPERARENA_JOIN_TEAM;
						leaderRole.superarenaJoinType = this.arenaType;
						leaderRole.superarenaJoinGrade = leaderRole.arenaroom.grade;
						leaderRole.superarenaJoinTime = GameTime.getTime();
						gs.getFightService().addSuperArenaJoinRole(leaderRole.id);
						for(Role role: members)
						{
							role.superarenaState = GameData.SUPERARENA_JOIN_TEAM;
							role.superarenaJoinType = this.arenaType;
							role.superarenaJoinGrade = leaderRole.arenaroom.grade;
							role.superarenaJoinTime = GameTime.getTime();
							gs.getFightService().addSuperArenaJoinRole(role.id);
							gs.getRPCManager().sendStrPacket(role.netsid, new SBean.superarena_startmatch());
						}
						
						cluster.unReadyRooms.remove(roomID);
						cluster.readyRooms.put(roomID, room);
					}
				}
				gs.getRPCManager().sendStrPacket(leaderRole.netsid, new SBean.superarena_teamjoin_res(ok));
			});
		}
		
		public synchronized void quitMatch(Role quitRole, SBean.ARoom room, SuperArenaQuitCallBack callback)
		{
			ArenaRoom arenaroom = this.getRoom(room.grade, room.id, true);
			if(arenaroom == null)
			{
				if(callback != null)
					callback.onCallback(GameData.PROTOCOL_OP_FAILED);
				return;
			}
			
			gs.getFightService().teamQuitSuperArenaImpl(quitRole.id, arenaroom.getMemberCount(), quitRole.superarenaJoinType, quitRole.superarenaJoinGrade, ok -> {
				synchronized(this)
				{
					if(ok > 0)
					{
						quitRole.clearSuperArenaState();
						arenaroom.getAllMembers().forEach(rid -> 
						{
							Role role = gs.getLoginManager().getOnGameRole(rid);
							if(role != null)
							{
								synchronized (role)
								{
									if(rid != quitRole.id)
									{
										role.clearSuperArenaState();
										gs.getRPCManager().sendStrPacket(role.netsid, new SBean.superarena_join(GameData.PROTOCOL_OP_SUPERARENA_OTHERROLE_QUIT));
									}
								}
							}
						});
						
						this.delRoom(room.grade, room.id, true);
						if(!arenaroom.members.isEmpty())
							this.addRoom(arenaroom, false);
					}
					
					if(callback != null)
						callback.onCallback(ok);
				}
			});
		}
		
		private void addRoom(ArenaRoom room, boolean ready)
		{
			ArenaRoomCluster cluster = roomCluster.get(room.grade);
			if(cluster == null)
			{
				cluster = new ArenaRoomCluster(room.grade);
				roomCluster.put(room.grade, cluster);
			}
			
			if(ready)
				cluster.readyRooms.put(room.getID(), room);
			else
				cluster.unReadyRooms.put(room.getID(), room);
		}
		
		private ArenaRoom delRoom(int grade, int roomID, boolean ready)
		{
			ArenaRoomCluster cluster = roomCluster.get(grade);
			if(cluster == null)
				return null;
			
			return ready ? cluster.readyRooms.remove(roomID) : cluster.unReadyRooms.remove(roomID);
		}
		
		private ArenaRoom getRoom(int grade, int roomID, boolean ready)
		{
			ArenaRoomCluster cluster = roomCluster.get(grade);
			if(cluster == null)
				return null;
			
			return ready ? cluster.readyRooms.get(roomID) : cluster.unReadyRooms.get(roomID);
		}
		
		public synchronized List<SBean.RoleOverview> getRoleRoomRoles(int queryRoleID, int grade, boolean ready)
		{
			Integer roomID = mapr2Room.get(queryRoleID);
			if(roomID == null)
				return GameData.emptyList();
			
			List<SBean.RoleOverview> overviews = new ArrayList<>();
			ArenaRoom room = getRoom(grade, roomID, ready);
			if (room == null)
				return overviews;
			
			room.getAllMembers().forEach(rid -> {
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if(role != null)
					overviews.add(role.getRoleOverview());
			});
			
			return overviews;
		}
		
		public synchronized int createArenaRoom(Role leaderRole)
		{
			Integer roomID = mapr2Room.get(leaderRole.id);
			if(roomID != null)
				return GameData.PROTOCOL_OP_AROOM_ALREADY_IN_ROOM;
			
			ArenaRoom room = new ArenaRoom(nextRoomID.incrementAndGet(), leaderRole.id, GameData.getInstance().getSuperArenaGrade(this.arenaType, leaderRole.level));
			addRoom(room, false);
			mapr2Room.put(leaderRole.id, room.getID());
			leaderRole.onSelfJoinARoom(room.toARoom());
			
			return room.getID();
		}
		
		private boolean roomMemberLeaveImpl(Role memberRole, boolean ready)
		{
			Integer roomID = mapr2Room.get(memberRole.id);
			if(roomID == null)
				return false;
			
			ArenaRoom room = getRoom(memberRole.arenaroom.grade, roomID, ready);
			if(room == null)
				return false;
			
			Role changedleaderRole = null;
			if(room.getLeader() == memberRole.id && room.getAllMembers().size() > 1)
			{
				room.changeLeader();
				changedleaderRole = gs.getLoginManager().getOnGameRole(room.getLeader());
			}
			room.removeMember(memberRole.id);
			mapr2Room.remove(memberRole.id);
			memberRole.onARoomMemberLeave(memberRole);
			if(room.isValid())
			{
				for(int rid: room.members)
				{
					Role role = gs.getLoginManager().getOnGameRole(rid);
					if(role == null)
						continue;
					
					if(changedleaderRole != null)
						role.onARoomLeaderChange(changedleaderRole);
					
					role.onARoomMemberLeave(memberRole);
				}
			}
			else
			{
				delRoom(memberRole.arenaroom.grade, roomID, false);
			}
			
			return true;
		}
		
		public synchronized boolean roomMemberLeave(Role memberRole, boolean ready)
		{
			return roomMemberLeaveImpl(memberRole, ready);
		}
		
		private int addRoomMemberImpl(int grade, int roomID, Role newMember)
		{
			ArenaRoom room = getRoom(grade, roomID, false);
			if(room == null)
				return GameData.PROTOCOL_OP_AROOM_ROOM_NOT_EXIST;
			
			if(room.getAllMembers().size() >= this.typeCfg.members)
				return GameData.PROTOCOL_OP_AROOM_ROOM_FULL;
			
			if(room.containsMember(newMember.id))
				return GameData.PROTOCOL_OP_AROOM_ALREADY_IN_ROOM;
			
			roomMemberLeaveImpl(newMember, false);
			room.addMember(newMember.id);
			room.getAllMembers().stream().filter(rid -> rid != newMember.id).forEach(rid -> {
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if(role == null)
					return;
				
				role.onNewMemberJoinARoom(newMember);
			});
			
			newMember.onSelfJoinARoom(room.toARoom());
			mapr2Room.put(newMember.id, roomID);
			
			return roomID;
		}
		
		public synchronized int addRoomMember(int inviteStartRoleID, int grade, Role newMember)
		{
			Integer roomID = mapr2Room.get(inviteStartRoleID);
			if(roomID == null)
				return GameData.PROTOCOL_OP_AROOM_ROOM_NOT_EXIST;
			
			return addRoomMemberImpl(grade, roomID, newMember);
		}
		
		public synchronized boolean kickRoomMember(Role leaderRole, Role kickedRole)
		{
			if(leaderRole == kickedRole)
				return false;
			
			Integer roomID = mapr2Room.get(leaderRole.id);
			if(roomID == null)
				return false;
			
			ArenaRoom room = getRoom(leaderRole.arenaroom.grade, roomID, false);
			if(room == null || room.getLeader() != leaderRole.id || !room.containsMember(kickedRole.id))
				return false;
			
			room.removeMember(kickedRole.id);
			mapr2Room.remove(kickedRole.id);
			kickedRole.onARoomMemberKicked(kickedRole);
			room.getAllMembers().forEach(rid -> {
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if(role == null)
					return;

				role.onARoomMemberKicked(kickedRole);
			});
			
			return true;
		}
		
		public synchronized boolean aroomChangeLeader(Role leaderRole, Role newLeaderRole)
		{
			if (leaderRole == newLeaderRole)
				return false;
			
			Integer roomID = mapr2Room.get(leaderRole.id);
			if(roomID == null)
				return false;
			
			ArenaRoom room = getRoom(leaderRole.arenaroom.grade, roomID, false);
			if(room == null || room.getLeader() != leaderRole.id || !room.containsMember(newLeaderRole.id))
				return false;
			
			room.changeLeader(newLeaderRole.id);
			room.getAllMembers().stream().filter(rid -> rid != leaderRole.id).forEach(rid ->
			{
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role == null)
					return;
				
				role.onARoomLeaderChange(newLeaderRole);
			});
			
			leaderRole.arenaroom.leader = newLeaderRole.id;
			return true;
		}
		
		synchronized void clearReadyRoom()
		{
			this.roomCluster.values().forEach(clusetr -> 
			{
				clusetr.unReadyRooms.putAll(clusetr.readyRooms);
				clusetr.readyRooms.clear();
			});
			
		}
	}
}
