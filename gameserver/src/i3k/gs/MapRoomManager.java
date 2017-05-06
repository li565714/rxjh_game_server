package i3k.gs;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.gs.TeamManager.EnterMapCopy;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MapRoomManager 
{
	
	MapRoomManager(GameServer ms)
	{
		this.gs = ms;
	}

	class MapCopyCluster
	{
		public final int mapId;
		public List<Integer> lineRooms = new ArrayList<>();//for performance

		public MapCopyCluster(int mapId)
		{
			this.mapId = mapId;
		}
		
		public void addRoom(int roomId)
		{
			if (!lineRooms.contains(roomId))
				lineRooms.add(roomId);
		}
		
		public void removeRoom(int roomId)
		{
			lineRooms.remove(Integer.valueOf(roomId));
		}
		
		public int getRandomRoomId()
		{
			if (lineRooms.isEmpty())
				return -1;
			int index = GameRandom.getRandom().nextInt(lineRooms.size());
			return lineRooms.get(index);
		}
		
		public Collection<Integer> getAllRooms()
		{
			return this.lineRooms;
		}

	}
	
	
	private MapCopyCluster getOrCreateMapCopyCluster(int mapId)
	{
		MapCopyCluster mcc = mapcopys.get(mapId);
		if (mcc == null)
		{
			mcc = new MapCopyCluster(mapId);
			mapcopys.put(mapId, mcc);
		}
		return mcc;
	}	
	
	private static class Room
	{
		int targetMapId;
		int type;
		final int id;
		List<Integer> members = new ArrayList<>();
		final int createTime;
		
		int addRobotTime;
		
		Room(int mapId, int id, int leader, int createTime, int type)
		{
			this.targetMapId = mapId;
			this.id = id;
			this.members.add(leader);
			this.createTime = createTime;
			this.type = type;
		}

		int getID()
		{
			return this.id;
		}
		
		int getTargetMapId()
		{
			return this.targetMapId;
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
			for(int rid: this.members)
			{
				if(rid > 0)
					return true;
			}
			return false;
//			return this.members.size() > 0;
		}
		
		void changeLeader()
		{
			if (!isValid())
				return;
			Integer oldLeader = this.members.remove(0);
			this.members.add(oldLeader);
		}
		
		void changeLeader(int newLeadID)
		{
			if (!isValid())
				return;
			
			if (!this.members.contains(newLeadID))
				return;
			
			Integer oldLeader = this.members.remove(0);
			this.members.remove(Integer.valueOf(newLeadID));
			this.members.add(0, newLeadID);
			this.addMember(oldLeader);
//			this.members.add(oldLeader);
		}
		
		void addMember(int rid)
		{
			if (this.members.contains(rid))
				return;
			
			if(rid < 0)
			{
				this.members.add(rid);
			}
			else
			{
				boolean add = false;
				int size = this.members.size();
				for(int i = 0; i < size; i++)
				{
					if(this.members.get(i) < 0)
					{
						this.members.add(i, rid);
						add = true;
						break;
					}
				}
				
				if(!add)
					this.members.add(rid);
			}
			
			addRobotTime = 0;
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
		
		SBean.MRoom toMRoom()
		{
			List<Integer> lst = new ArrayList<>();
			lst.addAll(this.members);
			return new SBean.MRoom(this.id, this.type, this.targetMapId, this.getLeader(), lst, this.createTime);
		}
		
		boolean full()
		{
			return this.members.size() == GameData.MAP_ROOM_MAX_COUNT;
		}
		
		boolean toAddRobot(int timeTick)
		{
			if(!isValid() || full() || targetMapId <= 0)
				return false;
			
			if(addRobotTime == 0)
			{
				addRobotTime = timeTick + GameRandom.getRandInt(10, 20);	//TODO get cfg
				return false;
			}
			
			return timeTick > addRobotTime;
		}
		
		int randRobot()
		{
			List<Integer> realRoles = new ArrayList<>();
			for(int rid: members)
			{
				if(rid > 0)
					realRoles.add(rid);
			}
			
			addRobotTime = 0;
			return realRoles.isEmpty() ? 0 : realRoles.get(GameRandom.getRandInt(0, realRoles.size()));
		}
	}
	
	public synchronized void onTimer(int timeTick)
	{
//		tryAddRobot(timeTick);
	}
	
	private void tryAddRobot(int timeTick)
	{
		for(Room room: this.mapRooms.values())
		{
			if(!room.toAddRobot(timeTick))
				continue;
			
			int srcID = room.randRobot();
			if(srcID > 0)
			{
				Role robotSrc = gs.getLoginManager().getOnGameRole(srcID);
				if(robotSrc != null)
				{
					Role robot = gs.getLoginManager().createRobot(robotSrc, room.getTargetMapId());
					addRoomMemberImpl(room.id, robot);
					gs.getLogger().info("room " + room.getID() + " add robot " + robot.name);
				}
			}
		}
	}
	
	public synchronized List<SBean.RoleOverview> getRoleRoomRoles(int queryRoleId)
	{
		List<SBean.RoleOverview> overviews = new ArrayList<>();
		Integer roomIdInt = mapr2Room.get(queryRoleId);
		if (roomIdInt == null)
			return overviews;
		Room room = mapRooms.get(roomIdInt);
		if (room == null)
			return overviews;
		
		for (int rid : room.getAllMembers())
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				overviews.add(role.getRoleOverview());
			}
		}
		return overviews;
	}
	
	public synchronized List<SBean.TeamOverview> getMapRooms(int mapId)
	{
		List<SBean.TeamOverview> overviews = new ArrayList<>();
		MapCopyCluster mcc = mapcopys.get(mapId);
		if (mcc == null)
			return overviews;
		for (int roomId : mcc.getAllRooms())
		{
			Room room = mapRooms.get(roomId);
			if (room != null)
			{
				SBean.TeamOverview t = new SBean.TeamOverview();
				t.id = room.getID();
				t.leader = room.getLeader();
				t.memberCount = room.getAllMembers().size();
				Role leaderRole = gs.getLoginManager().getOnGameRole(t.leader);
				if (leaderRole == null)
					continue;
				t.leaderLvl = leaderRole.level;
				t.leaderName = leaderRole.name;
				overviews.add(t);
			}
		}
		return overviews;
	}
	
	private int roomMembersStartMapCopyImpl(Role leader, int mapId, EnterMapCopy enterFunc, Room room, int instanceId)
	{
		//不能直接使用room.getAllMembers()，下面遍历过程会修改room中的member
		Collection<Integer> allMembers = room.toMRoom().members;
		allMembers.stream().filter(rid -> rid != leader.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
				roomMemberLeave(role);
		});
		roomMemberLeave(leader);
		int tid = gs.getTeamManager().forceCreateNewTeamStartMapCopy(leader, allMembers, mapId, instanceId, true, true, enterFunc);
		gs.getMapService().syncMapCopyReady(mapId, instanceId);
		return tid;
	}
	
	private Room matchRoom(Role leader, int mapId)
	{
		Integer roomIdInt = mapr2Room.get(leader.id);
		if (roomIdInt == null)
			return null;
		Room room = mapRooms.get(roomIdInt);
		if (room == null)
			return null;
		if (room.getTargetMapId() != mapId)
			return null;
		if (room.getLeader() != leader.id)
			return null;
		if (room.getAllMembers().size() <= 1)
			return null;
		
		return room;
	}
	public synchronized boolean roomMembersStartPublicMapCopy(Role leader, int mapId)
	{
		SBean.MapCopyCFGS cfg = GameData.getInstance().getMapCopyCFGS(mapId);
		if (cfg == null || !GameData.isMapCopyInOpenTime(cfg.startTime, cfg.endTime))
			return false;
		
		Room room = matchRoom(leader, mapId);
		if(room == null || room.getType() != GameData.MAP_ROOM_TYPE_DEFAULT)
			return false;
		
		int instanceId = gs.getMapService().createNormalMapCopy(mapId);
		if (instanceId <= 0)
			return false;
		
		roomMembersStartMapCopyImpl(leader, mapId, Role::enterPublicMapCopy, room, instanceId);
		return true;
	}
	
	public synchronized int roomMembersStartJusticeMapCopy(Role leader)
	{
		SBean.JusticeMapCFGS cfg = GameData.getInstance().getJusticeMapCFGS();
		if (cfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		Integer roomIdInt = mapr2Room.get(leader.id);
		if (roomIdInt == null)
			return GameData.PROTOCOL_OP_FAILED;
		Room room = mapRooms.get(roomIdInt);
		if (room == null || room.type != GameData.MAP_ROOM_TYPE_DEFAULT || room.targetMapId != 0)
			return GameData.PROTOCOL_OP_FAILED;
		if (room.getLeader() != leader.id)
			return GameData.PROTOCOL_OP_FAILED;
		if (room.getAllMembers().size() <= 1)
			return GameData.PROTOCOL_OP_FAILED;
		int allLvl = 0;
		for (int memberId:room.getAllMembers())
		{
			Role member=gs.getLoginManager().getOnGameRole(memberId);
			if (member == null)
				return GameData.PROTOCOL_OP_JUSTICE_MEMBER_OUT_LINE;
			int memberCondition = member.checkCanEnterJusticeMap();
			if (memberCondition <= 0)
				return memberCondition;
			allLvl+=member.level;
		}

		int mapId = GameData.getInstance().getLevelMapId(cfg.level2mapId, allLvl / room.getAllMembers().size());
		int instanceId = gs.getMapService().createJusticeMapCopy(mapId);
		if (instanceId <= 0)
			return GameData.PROTOCOL_OP_FAILED;

		roomMembersStartMapCopyImpl(leader, mapId, Role::enterJusticeMapCopy, room, instanceId);
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int roomLeadedStartTowerDefence(Role leader, int mapID)
	{
		SBean.TowerDefenceMapCFGS mapCfg = GameData.getInstance().getTowerDefenceMapCFGS(mapID);
		if (mapCfg == null)
			return GameData.PROTOCOL_OP_MROOM_MAP_INVALID;
		
		SBean.TowerDefenceCFGS cfg = GameData.getInstance().getTowerDefenceCFGS(mapID);
		if(cfg == null || !GameData.isTowerDefenceInOpneTime(cfg.base))
			return GameData.PROTOCOL_OP_MROOM_NOT_IN_OPEN_TIME;
		
		Room room = matchRoom(leader, mapID);
		if (room == null || room.getType() != GameData.MAP_ROOM_TYPE_TOWER_DEFENCE)
			return GameData.PROTOCOL_OP_FAILED;

		int instanceID = gs.getMapService().createTowerDefenceMapCopy(mapID);
		if (instanceID <= 0)
			return GameData.PROTOCOL_OP_MROOM_MAP_CREATE_FAILED;

		roomMembersStartMapCopyImpl(leader, mapID, Role::enterTowerDefenceMapCopy, room, instanceID);
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public synchronized int roomMembersStartNpcMapCopy(Role leader, int mapId)
	{
		SBean.NpcMapCFGS cfg = GameData.getInstance().getNpcMapCFGS(mapId);
		if (cfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		Integer roomIdInt = mapr2Room.get(leader.id);
		if (roomIdInt == null)
			return GameData.PROTOCOL_OP_FAILED;
		Room room = mapRooms.get(roomIdInt);
		if (room == null || room.type != GameData.MAP_ROOM_TYPE_NPC_MAP || room.targetMapId != mapId)
			return GameData.PROTOCOL_OP_FAILED;
		if (room.getLeader() != leader.id)
			return GameData.PROTOCOL_OP_FAILED;
		if (room.getAllMembers().size() <= 1)
			return GameData.PROTOCOL_OP_FAILED;
		int allLvl = 0;
		for (int memberId:room.getAllMembers())
		{
			Role member=gs.getLoginManager().getOnGameRole(memberId);
			if (member == null)
				return GameData.PROTOCOL_OP_JUSTICE_MEMBER_OUT_LINE;
			int memberCondition = member.canEnterNPCMapRoom(mapId);
			if (memberCondition <= 0)
				return memberCondition;
			allLvl+=member.level;
		}

		int realMapId = GameData.getInstance().getLevelMapId(cfg.level2mapId, allLvl/room.getAllMembers().size());
		int instanceId = gs.getMapService().createJusticeMapCopy(realMapId);
		if (instanceId <= 0)
			return GameData.PROTOCOL_OP_FAILED;
		
		roomMembersStartMapCopyImpl(leader, realMapId, Role::enterJusticeMapCopy, room, instanceId);
		
		return GameData.PROTOCOL_OP_SUCCESS;
		
	}
	
	public synchronized int forceCreateNewTeamAndLeaveRoom(Role leader)
	{
		Integer roomIdInt = mapr2Room.get(leader.id);
		if (roomIdInt == null)
			return GameData.PROTOCOL_OP_FAILED;
		Room room = mapRooms.get(roomIdInt);
		if (room == null)
			return GameData.PROTOCOL_OP_FAILED;
		if (room.getLeader() != leader.id)
			return GameData.PROTOCOL_OP_FAILED;
		if (room.getAllMembers().size() <= 1)
			return GameData.PROTOCOL_OP_FAILED;
		//不能直接使用room.getAllMembers()，下面遍历过程会修改room中的member
		Collection<Integer> allMembers = room.toMRoom().members;
		allMembers.stream().filter(rid -> rid != leader.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
				roomMemberLeave(role);
		});
		roomMemberLeave(leader);
		return gs.getTeamManager().forceCreateNewTeam(leader, allMembers, true);
	}
	
	public synchronized int createMapCopyRoom(int mapId, Role leaderRole, int roomType)
	{
		Integer roomIdInt = mapr2Room.get(leaderRole.id);
		if (roomIdInt != null)
			return GameData.PROTOCOL_OP_MROOM_ALREADY_IN_ROOM;
		Room room = new Room(mapId, nextRoomID.incrementAndGet(), leaderRole.id, GameTime.getTime(), roomType);
		switch (roomType)
		{
		case GameData.MAP_ROOM_TYPE_NPC_MAP:
		case GameData.MAP_ROOM_TYPE_TOWER_DEFENCE:
			break;
		default:
			{
				MapCopyCluster mcc = getOrCreateMapCopyCluster(mapId);
				if (mcc != null)
					mcc.addRoom(room.getID());
			}
			break;
		}
		mapRooms.put(room.getID(), room);
		mapr2Room.put(leaderRole.id, room.getID());
		leaderRole.onSelfJoinMRoom(room.toMRoom());
		return room.id;
	}
	
	public synchronized boolean roomMemberLeave(Role memberRole)
	{
		return roomMemberLeaveImpl(memberRole);
	}
	
	private boolean roomMemberLeaveImpl(Role memberRole)
	{
		Integer roomIdInt = mapr2Room.get(memberRole.id);
		if (roomIdInt == null)
			return false;
		Room room = mapRooms.get(roomIdInt);
		if (room == null)
			return false;
		Role changedleaderRole = null;
		if (room.getLeader() == memberRole.id && room.getAllMembers().size() > 1)
		{
			room.changeLeader();
			changedleaderRole = gs.getLoginManager().getOnGameRole(room.getLeader());
		}
		room.removeMember(memberRole.id);
		mapr2Room.remove(memberRole.id);
		memberRole.onMRoomMemberLeave(memberRole);
		if (room.isValid())
		{
			for (int rid : room.getAllMembers())
			{
				Role role = gs.getLoginManager().getOnGameRole(rid);
				if (role != null)
				{
					if (changedleaderRole != null)
						role.onMRoomLeaderChange(changedleaderRole);
					role.onMRoomMemberLeave(memberRole);
				}
			}
		}
		else
		{
			mapRooms.remove(roomIdInt);
			MapCopyCluster mcc = getOrCreateMapCopyCluster(room.getTargetMapId());
			mcc.removeRoom(roomIdInt);
			
			for(int rid: room.getAllMembers())
			{
				if(rid < 0)
					gs.getLoginManager().delRobot(rid);
			}
		}
		return true;
	}
	
	public synchronized int addRoomMember(Role oldMember, Role newMember)
	{
		Integer roomIdInt = mapr2Room.get(oldMember.id);
		if (roomIdInt == null)
			return GameData.PROTOCOL_OP_MROOM_ROOM_NOT_EXIST;
		return addRoomMemberImpl(roomIdInt, newMember);
	}
	
	public synchronized int addRoomMember(int roomId, Role newMember)
	{
		return addRoomMemberImpl(roomId, newMember);
	}
	
	public synchronized int addMemberToRandomRoom(int mapId, Role newMember)
	{
		MapCopyCluster mcc = getOrCreateMapCopyCluster(mapId);
		int randomId = mcc.getRandomRoomId();
		if (randomId <= 0)
			return GameData.PROTOCOL_OP_MROOM_ROOM_NOT_EXIST;
		if (mapr2Room.containsKey(newMember.id))
			return GameData.PROTOCOL_OP_MROOM_ALREADY_IN_ROOM;
		return addRoomMemberImpl(randomId, newMember);
	}
	
	private int addRoomMemberImpl(int roomId, Role newMember)
	{
		Room room = mapRooms.get(roomId);
		if (room == null)
			return GameData.PROTOCOL_OP_MROOM_ROOM_NOT_EXIST;
		if (room.getAllMembers().size() >= GameData.MAP_ROOM_MAX_COUNT)
			return GameData.PROTOCOL_OP_MROOM_ROOM_FULL;
		if (room.containsMember(newMember.id))
			return GameData.PROTOCOL_OP_MROOM_ALREADY_IN_ROOM;
		roomMemberLeaveImpl(newMember);
		room.addMember(newMember.id);
		mapr2Room.put(newMember.id, room.getID());
		room.getAllMembers().stream().filter(rid -> rid != newMember.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.onNewMemberJoinMRoom(newMember);
			}
		});
		newMember.onSelfJoinMRoom(room.toMRoom());
		return room.id;
	}
	
	public synchronized boolean kickRoomMember(Role leaderRole, Role kickedRole)
	{
		if (leaderRole == kickedRole)
			return false;
		Integer roomIdInt = mapr2Room.get(leaderRole.id);
		if (roomIdInt == null)
			return false;
		Room room = mapRooms.get(roomIdInt);
		if (room == null)
			return false;
		if (room.getLeader() != leaderRole.id)
			return false;
		if (!room.getAllMembers().contains(kickedRole.id))
			return false;
		room.removeMember(kickedRole.id);
		mapr2Room.remove(kickedRole.id);
		kickedRole.onMRoomMemberKicked(kickedRole);
		for (int rid : room.getAllMembers())
		{
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role != null)
			{
				role.onMRoomMemberKicked(kickedRole);
			}
		}
		return true;
	}
	
	public synchronized boolean mroomChangeLeader(Role leaderRole, Role newLeaderRole)
	{
		if (leaderRole == newLeaderRole)
			return false;
		
		Integer roomID = mapr2Room.get(leaderRole.id);
		if(roomID == null)
			return false;
		
		Room room = mapRooms.get(roomID);
		if(room == null || room.getLeader() != leaderRole.id || !room.containsMember(newLeaderRole.id))
			return false;
		
		room.changeLeader(newLeaderRole.id);
		room.getAllMembers().stream().filter(rid -> rid != leaderRole.id).forEach(rid -> {
			Role role = gs.getLoginManager().getOnGameRole(rid);
			if (role == null)
				return;
			
			role.onMRoomLeaderChange(newLeaderRole);
		});
		
		leaderRole.room.leader = newLeaderRole.id;
		return true;
	}
	
	
	final GameServer gs;
	private AtomicInteger nextRoomID = new AtomicInteger();
	
	ConcurrentMap<Integer, MapCopyCluster> mapcopys = new ConcurrentHashMap<>();
	ConcurrentMap<Integer, Room> mapRooms = new ConcurrentHashMap<>();
	ConcurrentMap<Integer, Integer> mapr2Room = new ConcurrentHashMap<>();
	
}

