package i3k.fight;

import i3k.SBean;
import i3k.fight.DemonHoleManager.ServerRole;
import i3k.fight.ForceWar.FWJoinTeam;
import i3k.fight.ForceWar.ForceMemberDetail;
import i3k.fight.SuperArenaManager.TmpTeam;
import i3k.fight.SuperArenaManager.TmpTeamPair;
import i3k.fight.SuperArenaManager.JoinTeam;
import i3k.gs.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalMapService
{
	private final FightServer fs;
	private GameMaps gmaps;
	private Map<Integer, ForceWarMatchInfo> forceWarMatchInfos = new HashMap<>();			//<mapInstance, ForceWarMatchInfo>
	private Map<Integer, SuperArenaMatchInfo> superArenaMatchInfos = new HashMap<>();		//<mapInstance, SuperArenaMatchInfo>
	private Map<Integer, DemonHoleInfo> demonHoleInfos = new HashMap<>();					//<mapInstance, DemonHoleInfo>
	
	public GlobalMapService(FightServer fs)
	{
		this.fs = fs;
	}
	
	synchronized void gmapStartWork(Set<Integer> maps)
	{
		gmaps = new GameMaps(fs);
		gmaps.init(maps);
	}
	
	void gmapStopWork(int sessionid)
	{
		synchronized(this)
		{
			if (gmaps != null)
			{
				fs.getLogger().info("global map session " + sessionid + " stop work !");
				gmaps.fini();
			}
		}
		fs.getDemonHoleManager().mapStopWork();
	}
	
	void onTimer(int timeTick)
	{
		if(gmaps != null)
			gmaps.onTimer(timeTick);
	}
	
	class ForceWarMatchInfo
	{
		Map<Integer, ForceMemberDetail> roles;
		List<FWJoinTeam> joinTeams;
		Set<Integer> gsids;
		int mapID;
		
		ForceWarMatchInfo(Map<Integer, ForceMemberDetail> roles, Set<Integer> gsids, int mapID, List<FWJoinTeam> joinTeams)
		{
			this.roles = roles;
			this.gsids = gsids;
			this.mapID = mapID;
			this.joinTeams = joinTeams;
		}
	}
	
	class SuperArenaMatchInfo
	{
		TmpTeamPair pair;
		Set<Integer> gsids;
		int mapID;
		
		SuperArenaMatchInfo(TmpTeamPair pair, Set<Integer> gsids, int mapID)
		{
			this.pair = pair;
			this.gsids = gsids;
			this.mapID = mapID;
		}
	}
	
	class DemonHoleInfo
	{
		final int grade;
		final int groupID;
		final int mapID;
		final int mapInstance;
		
		DemonHoleInfo(int grade, int groupID, int mapID, int mapInstance)
		{
			this.grade = grade;
			this.groupID = groupID;
			this.mapID = mapID;
			this.mapInstance = mapInstance;
		}
	}
	
	public synchronized void createForceWarMap(Map<Integer, ForceMemberDetail> roles, Set<Integer> gsids, int mapID, int mapInstance, List<FWJoinTeam> joinTeams)
	{
		this.forceWarMatchInfos.put(mapInstance, new ForceWarMatchInfo(roles, gsids, mapID, joinTeams));
		fs.getRPCManager().notifyGlobalMapCreateMapCopyReq(GameData.MAP_TYPE_MAPCOPY_FORCEWAR, mapID, mapInstance);
	}
	
	public synchronized void createSuperArenaMap(TmpTeamPair pair, Set<Integer> gsids, int mapID, int mapInstance)
	{
		this.superArenaMatchInfos.put(mapInstance, new SuperArenaMatchInfo(pair, gsids, mapID));
		fs.getRPCManager().notifyGlobalMapCreateMapCopyReq(GameData.MAP_TYPE_MAPCOPY_SUPERARENA, mapID, mapInstance);
	}
	
	public synchronized void createDemonHoleMap(int grade, int groupID, int mapID, int mapInstance)
	{
		this.demonHoleInfos.put(mapInstance, new DemonHoleInfo(grade, groupID, mapID, mapInstance));
		fs.getRPCManager().notifyGlobalMapCreateMapCopyReq(GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE, mapID, mapInstance);
	}
	
	//---------------------------------------------------------------------------------------
	public void handleCreateMapCopyRes(int mapType, int mapInstance)
	{
		switch (mapType)
		{
		case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
			this.handleCreateForceWarMapCopy(mapInstance);
			break;
		case GameData.MAP_TYPE_MAPCOPY_SUPERARENA:
			this.handleCreateSuperArenaMapCopy(mapInstance);
			break;
		case GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE:
			this.handleCreateDemonHoleMapCopy(mapInstance);
			break;
		default:
			break;
		}
	}
	
	public void handleCreateForceWarMapCopy(int mapInstance)
	{
		ForceWarMatchInfo info = null;
		synchronized(this)
		{
			info = this.forceWarMatchInfos.remove(mapInstance);
		}
		
		if(info == null)
			return;
		
		if(gmaps != null)
		{
			gmaps.createForceWarMapCopy(info.roles, info.gsids, info.mapID, mapInstance, info.joinTeams);
			for(ForceMemberDetail e: info.roles.values())
				fs.getRoleManager().roleEnterMap(e.joinInfo.overview, info.mapID, mapInstance);
		}
		
		String whiteChatRoom = fs.getFightManager().createChatRoomID();
		String blackChatRoom = fs.getFightManager().createChatRoomID();
		//notify gs role enter
		for(ForceMemberDetail e: info.roles.values())
		{
			fs.getRPCManager().notifyGSRoleEnterForceWarMap(e.joinInfo.overview.id, info.mapID, gmaps != null ? mapInstance : GameData.PROTOCOL_OP_FAILED, e.mainSpawn);
			String roomID = e.joinInfo.overview.bwType == GameData.BWTYPE_BLACK ? blackChatRoom : whiteChatRoom;
			fs.getRPCManager().notifyGSSyncRoleChatRoom(e.joinInfo.overview.id, info.mapID, mapInstance, roomID);
		}
		
		//notify gs start
		if(gmaps != null)
			gmaps.syncForceWarMapCopyStart(info.mapID, mapInstance);
	}
	
	public void handleCreateSuperArenaMapCopy(int mapInstance)
	{
		SuperArenaMatchInfo info = null;
		synchronized(this)
		{
			info = this.superArenaMatchInfos.remove(mapInstance);
		}
		
		if(info == null)
			return;
		
		if(gmaps != null)
		{
			gmaps.createSuperArenaMapCopy(info.pair, info.gsids, info.mapID, mapInstance);
			for(TmpTeam tt: info.pair.lst)
			{
				List<Integer> members = new ArrayList<>();
				for(JoinTeam t: tt.teams)
				{
					for(SBean.SuperArenaJoin e: t.members)
					{
						fs.getRoleManager().roleEnterMap(e.overview, info.mapID, mapInstance);
						members.add(e.overview.id);
					}
				}
				fs.getFightTeamManager().createTeam(members);
			}
		}
		
		//notify gs role enter
		for(TmpTeam tt: info.pair.lst)
		{
			String roomID = fs.getFightManager().createChatRoomID();
			for(JoinTeam t: tt.teams)
			{
				for(SBean.SuperArenaJoin e: t.members)
				{
					fs.getRPCManager().notifyGSRoleEnterSuperArenaMap(e.overview.id, info.mapID, mapInstance, tt.mainSpawnPos);
					fs.getRPCManager().notifyGSSyncRoleChatRoom(e.overview.id, info.mapID, mapInstance, roomID);
				}
			}
		}
		
		//notify gs start
		if(gmaps != null)
			gmaps.syncSuperArenaMapCopyStart(info.mapID, mapInstance);
	}
	
	public void handleCreateDemonHoleMapCopy(int mapInstance)
	{
		DemonHoleInfo info = null;
		synchronized(this)
		{
			info = this.demonHoleInfos.get(mapInstance);
		}
		
		if(info == null)
			return;
		
		fs.getDemonHoleManager().onCreateDemonHoleMapSuccess(info.grade, info.groupID, info.mapInstance);
	}
	
	public void handleSyncForceWarMapEnd(int mapID, int mapInstance, int winSide, int killedBoss, int whiteScore, int blackScore, Map<Integer, SBean.ForceWarOverview> whiteSide, Map<Integer, SBean.ForceWarOverview> blackSide)
	{
		if(gmaps != null)
			gmaps.syncForceWarMapCopyEnd(mapID, mapInstance, winSide, killedBoss, whiteScore, blackScore, new ArrayList<>(whiteSide.values()), new ArrayList<>(blackSide.values()));
	}
	
	public void handleForceWarSendMsg(int mapID, int mapInstance, int roleID, SBean.MessageInfo msgContent)
	{
		if(gmaps != null)
			gmaps.forceWarSendMsg(mapID, mapInstance, roleID, msgContent);
	}
	
	public void handleSyncSuperArenaMapEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result)
	{
		if(gmaps != null)
			gmaps.syncSuperArenaMapCopyEnd(mapID, mapInstance, result);
	}
	
	public void handleSyncHp(int roleID, int hp, int hpMax)
	{
		fs.getRoleManager().syncRoleHp(roleID, hp, hpMax);
	}
	
	public void handleSyncSuperArenaRaceEnd(int mapID, int mapInstance)
	{
		if(gmaps != null)
			gmaps.syncSuperArenaRaceEnd(mapID, mapInstance);
	}
	
	public void handleSyncDemonHoleKill(int mapID, int mapInstance, int killerID, int deaderID)
	{
		fs.getDemonHoleManager().syncRoleKill(killerID, deaderID);
	}
	
	public void roleLeaveMap(int roleID, int mapID, int mapInstance)
	{
		if(gmaps != null)
			gmaps.roleLeaveMap(roleID, mapID, mapInstance);
	}
	
	public int createDemonHoleMapImpl(int mapID, int mapInstance, int floor, Map<Integer, ServerRole> serverRoles)
	{
		if(gmaps != null)
			return gmaps.createDemonHoleMapCopy(mapID, mapInstance, floor, serverRoles);
		
		return 0;
	}
	
	public void roleEnterDemonHoleFloor(SBean.RoleOverview role, int mapID, int mapInstance)
	{
		if(gmaps != null)
		{
			gmaps.roleEnterDemonHoleMap(role.id, mapID, mapInstance);
			fs.getRoleManager().roleEnterMap(role, mapID, mapInstance);
		}
	}
	
	public void handleDemonHoleEnd(int mapID, int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		if(gmaps != null)
			gmaps.syncDemonHoleMapCopyEnd(mapID, mapInstance, curFloor, total);
	}
}
