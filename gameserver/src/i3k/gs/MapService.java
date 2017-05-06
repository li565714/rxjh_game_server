
package i3k.gs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;



import java.util.concurrent.ConcurrentMap;

import ket.util.SStream;
import i3k.IDIP;
import i3k.SBean;
import i3k.SBean.Vector3;
import i3k.util.GameTime;

public class MapService
{
	public MapService(GameServer gs)
	{
		this.gs = gs;
	}

	void execTask(MapServiceTask task)
	{
		tasks.put(task.id, task);
		task.doTask();
	}
	
	void onTimer(int timeTick)
	{
		//Map<Integer, Set<Integer>> timeoutMaps = new TreeMap<Integer, Set<Integer>>();
		Iterator<Map.Entry<Integer, GameMaps>> it = maps2m.entrySet().iterator();
		while (it.hasNext())
		{
			GameMaps maps = it.next().getValue();
			maps.onTimer(timeTick);
//			timeoutMaps.putAll(maps.getTimeOutMapCopys(timeTick));
		}
//		for (Map.Entry<Integer, Set<Integer>> kv : timeoutMaps.entrySet())
//		{
//			int mapId = kv.getKey();
//			for (int instanceId : kv.getValue())
//			{
//				List<Integer> roles = new ArrayList<Integer>(getMapRoles(mapId, instanceId));
//				gs.getLogger().debug("map " + mapId + " " + instanceId + " timeout, remove all map roles " +  roles + " ... ");
//				for (int rid : roles)
//				{
//					Role role = gs.getLoginManager().getOnGameRole(rid);
//					role.leaveMapCopy();
//				}
//				endMapCopy(mapId, instanceId);
//			}
//		}
		checkTimeOutTask(timeTick);
	}
	
	void checkTimeOutTask(int now)
	{
		List<MapServiceTask> timeoutTasks = getTimeOutTasks(now);
		timeoutTasks.forEach(MapServiceTask::onTimeout);
	}
	
	List<MapServiceTask> getTimeOutTasks(int now)
	{
		List<MapServiceTask> timeoutTasks = new ArrayList<>();
		Iterator<MapServiceTask> it = tasks.values().iterator();
		while (it.hasNext()) 
		{
			MapServiceTask task = it.next();
			if (task.isTooOld(now)) 
			{
				task = tasks.remove(task.id);
				if (task != null)
				{
					timeoutTasks.add(task);	
				}				
			}
		}
		return timeoutTasks;
	}
	
	public MapServiceTask peekTask(int id)
	{
		MapServiceTask task = tasks.remove(id);
		if (task == null)
			gs.getLogger().warn("MapService can't find Task id=" + id);
		return task;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public abstract class MapServiceTask
	{
		final static int MAX_WAIT_TIME = 2;
		final int id;
		int sendTime;
		MapServiceTask()
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
		
//		EnterMapTask asEnterMapTask()
//		{
//			gs.getLogger().warn("MapServiceTask id=" + id + " cast to EnterMapTask failed!");
//			return null;
//		}
//		
//		public LeaveMapTask asLeaveMapTask()
//		{
//			gs.getLogger().warn("MapServiceTask id=" + id + " cast to LeaveMapTask failed!");
//			return null;
//		}
		
//		public CreateMapInstanceTask asCreateMapInstanceTask()
//		{
//			gs.getLogger().warn("MapServiceTask id=" + id + " cast to CreateMapInstanceTask failed!");
//			return null;
//		}
	}
	
	
//	public interface EnterMapCallback
//	{
//		void onCallback(int roleID);
//	}
//	public class EnterMapTask extends MapServiceTask
//	{
//		int roleID;
//		SBean.DBClassCharacter character;
//		SBean.MapLocation mapLocation;
//		EnterMapCallback callback;
//		EnterMapTask(int roleID, SBean.DBClassCharacter character, SBean.MapLocation mapLocation, EnterMapCallback callback)
//		{
//			this.roleID = roleID;
//			this.character = character;
//			this.mapLocation = mapLocation;
//			this.callback = callback;
//		}
//
//		public EnterMapTask asEnterMapTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int roleID)
//		{
//			callback.onCallback(roleID != this.roleID ? -1 : this.roleID);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			//gs.getRPCManager().sendMapPacket(this.id, roleID, character, mapLocation);
//		}
//	}
//	
//	public interface LeaveMapCallback
//	{
//		void onCallback(int roleID, SBean.DBClassCharacter character);
//	}
//	public class LeaveMapTask extends MapServiceTask
//	{
//		int roleID;
//		LeaveMapCallback callback;
//		LeaveMapTask(int roleID, LeaveMapCallback callback)
//		{
//			this.roleID = roleID;
//			this.callback = callback;
//		}
//
//		public LeaveMapTask asLeaveMapTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int roleID, SBean.DBClassCharacter character)
//		{
//			callback.onCallback(roleID != this.roleID ? -1 : this.roleID, character);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100, null);
//		}
//		
//		void doTaskImpl()
//		{
//			//gs.getRPCManager().sendMapPacket(this.id, roleID);
//		}
//	}
//	
//	public interface CreateMapInstanceCallback
//	{
//		void onCallback(int mapInstance);
//	}
//	
//	public class CreateMapInstanceTask extends MapServiceTask
//	{
//		int mapID;
//		CreateMapInstanceCallback callback;
//		CreateMapInstanceTask(int mapID, CreateMapInstanceCallback callback)
//		{
//			this.mapID = mapID;
//			this.callback = callback;
//		}
//
//		public CreateMapInstanceTask asCreateMapInstanceTask()
//		{
//			return this;
//		}
//		
//		void onCallback(int mapInstance)
//		{
//			callback.onCallback(mapInstance);
//		}
//		
//		void onTimeout()
//		{
//			callback.onCallback(-100);
//		}
//		
//		void doTaskImpl()
//		{
//			sendCreateMapInstanceReq(this.id, mapID);
//		}
//	}
	
//	public void doTask(int roleID, SBean.DBClassCharacter character, SBean.MapLocation mapLocation, EnterMapCallback callback)
//	{
//		execTask(new EnterMapTask(roleID, character, mapLocation, callback));
//	}
//	
//	public void handleTaskResponse(int tag, int roleID)
//	{
//		MapServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			EnterMapTask task = t.asEnterMapTask();
//			if (task != null)
//				task.onCallback(roleID);
//		}
//	}
//	
//	public void doTask(int roleID, LeaveMapCallback callback)
//	{
//		execTask(new LeaveMapTask(roleID, callback));
//	}
//	
//	public void handleTaskResponse(int tag, int roleID, SBean.DBClassCharacter character)
//	{
//		MapServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			LeaveMapTask task = t.asLeaveMapTask();
//			if (task != null)
//				task.onCallback(roleID, character);
//		}
//	}
	
//	public void createMapInstance(int mapID, CreateMapInstanceCallback callback)
//	{
//		execTask(new CreateMapInstanceTask(mapID, callback));
//	}
//	
//	public void handleCreateMapInstanceResponse(int tag, int mapInstance)
//	{
//		MapServiceTask t = this.peekTask(tag);
//		if (t != null)
//		{
//			CreateMapInstanceTask task = t.asCreateMapInstanceTask();
//			if (task != null)
//				task.onCallback(mapInstance);
//		}
//	}
	
//	public void handleDestoryMapInstance(int mapID, int mapInstance)
//	{
//		SBean.MapCFGS mapCfg = GameData.getInstance().getMapCFGS(mapID);
//		if(mapCfg != null)
//		{
//			if(mapCfg.openType == GameData.MAPCOPY_TYPE_PRIVATE)	//单机副本
//			{
//				Role role = gs.getLoginManager().getOnGameRole(mapInstance);	//单机副本 roleID = mapInstance
//				if (role != null)
//				{
//					SBean.MapLocation location = role.leavePrivateMap();
//					gs.getRPCManager().sendLuaPacket(role.netsid, LuaPacket.encodeLeavePrivateMap(location != null));
//					if(location != null)
//						gs.getRPCManager().notifyChangeMap(role.netsid, location);
//				}
//			}
//			else
//			{
//				Collection<Integer> roles = gs.getMapCopyManager().endMapCopy(mapID, mapInstance);
//				if(roles != null)
//				{
//					for(Integer roleID: roles)
//					{
//						Role role = gs.getLoginManager().getOnGameRole(roleID);
//						if (role != null)
//						{
//							SBean.MapLocation location = role.leaveMapCopy_willDeprecated();
//							gs.getRPCManager().sendLuaPacket(role.netsid, LuaPacket.encodeLeaveMapCopy(location != null));
//							if(location != null)
//								gs.getRPCManager().notifyChangeMap(role.netsid, location);
//						}	
//					}
//				}
//			}
//		}
//	}
	
	//handle函数都不需要加锁
	public void handleForwardLuaChannelPacket(int roleID, String data)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
		{
			int session = role.getMapRoleClientSession();
			if (session > 0)
			{
				gs.getRPCManager().sendLuaPacket(session, data);
//				gs.getLogger().debug(GameData.MAP_CLIENT_TAG + roleID + " at " + GameTime.getTimeMillis() + " " + data);
			}
		}
	}
	public void handleForwardStrChannelPacket(int roleID, String data)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
		{
			int session = role.getMapRoleClientSession();
			if (session > 0)
			{
				gs.getRPCManager().sendStrPacket(session, data);
//				gs.getLogger().debug(GameData.MAP_CLIENT_TAG + roleID + " at " + GameTime.getTimeMillis() + " " + data);
			}
		}
	}
	public void handleForwardStrChannelBroadcastPacket(Set<Integer> rolesId, String data)
	{
		List<Integer> rolesSession = new ArrayList<Integer>();
		for (int roleID : rolesId)
		{
			Role role = gs.getLoginManager().getOnGameRole(roleID);
			if (role != null)
			{
				int session = role.getMapRoleClientSession();
				if (session > 0)
				{
					rolesSession.add(session);
//					gs.getLogger().debug(GameData.MAP_CLIENT_TAG + roleID + " at " + GameTime.getTimeMillis() + " " + data);
				}
			}
		}
		if (!rolesSession.isEmpty())
			gs.getRPCManager().broadcastStrPacket(rolesSession, data);
	}
	
	public void handleMapRoleReady(int roleId)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleId);
		if (role != null)
			role.mapRoleReady();
	}
	
	public void handleForwardStrChannelNearByRoleMove(Set<Integer> rids, int moveRoleID, SBean.Vector3 pos, int speed, SBean.Vector3F rotation, SBean.Vector3 target, SBean.TimeTick timeTick)
	{
//		String data = SStream.encode(new SBean.nearby_move_role(moveRoleID, pos, speed, rotation, target, timeTick));
		List<Integer> rolesSession = new ArrayList<Integer>();
		for (int roleID : rids)
		{
			Role role = gs.getLoginManager().getOnGameRole(roleID);
			if (role != null)
			{
				int session = role.getMapRoleClientSession();
				if (session > 0)
				{
					rolesSession.add(session);
//					gs.getLogger().debug(GameData.MAP_CLIENT_TAG + roleID + " at " + GameTime.getTimeMillis() + " " + data);
				}
			}
		}
		if (!rolesSession.isEmpty())
			gs.getRPCManager().broadcastStrPacket(rolesSession, new SBean.nearby_move_role(moveRoleID, pos, speed, rotation, target, timeTick));
	}
	
	public void handleForwardStrChannelNearByRoleStopMove(Set<Integer> rids, int stopRoleID, SBean.Vector3 pos, int speed, SBean.TimeTick timeTick)
	{
//		String data = SStream.encode(new SBean.nearby_stopmove_role(stopRoleID, pos, speed, timeTick));
		List<Integer> rolesSession = new ArrayList<Integer>();
		for (int roleID : rids)
		{
			Role role = gs.getLoginManager().getOnGameRole(roleID);
			if (role != null)
			{
				int session = role.getMapRoleClientSession();
				if (session > 0)
				{
					rolesSession.add(session);
//					gs.getLogger().debug(GameData.MAP_CLIENT_TAG + roleID + " at " + GameTime.getTimeMillis() + " " + data);
				}
			}
		}
		if (!rolesSession.isEmpty())
			gs.getRPCManager().broadcastStrPacket(rolesSession, new SBean.nearby_stopmove_role(stopRoleID, pos, speed, timeTick));
	}
	
	public void handleForwardStrChannelNearByRoleEnter(int rid, List<SBean.EnterDetail> roles)
	{
		Role role = gs.getLoginManager().getOnGameRole(rid);
		if(role != null)
		{
//			String data = SStream.encode(new SBean.nearby_enter_roles(roles));
			int session = role.getMapRoleClientSession();
			if (session > 0)
			{
				gs.getRPCManager().sendStrPacket(session, new SBean.nearby_enter_roles(roles));
//				gs.getLogger().debug(GameData.MAP_CLIENT_TAG + rid + " at " + GameTime.getTimeMillis() + " " + data);
			}
		}
	}
	
	public void handleForwardStrChannelNearByRoleLeave(int rid, Set<Integer> roles, int destory)
	{
		Role role = gs.getLoginManager().getOnGameRole(rid);
		if(role != null)
		{
//			String data = SStream.encode(new SBean.nearby_leave_roles(new ArrayList<>(roles), destory));
			int session = role.getMapRoleClientSession();
			if (session > 0)
			{
				gs.getRPCManager().sendStrPacket(session, new SBean.nearby_leave_roles(new ArrayList<>(roles), destory));
//				gs.getLogger().debug(GameData.MAP_CLIENT_TAG + rid + " at " + GameTime.getTimeMillis() + " " + data);
			}
		}
	}
	
	public void handleSyncCommonMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncCommonMapCopyStart(mapID, mapInstance);
			}
		}
	}
	
	public void handleSyncCommonMapCopyEnd(int mapID, int mapInstance, int score)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncCommonMapCopyEnd(mapID, mapInstance, score);
			}
		}
	}

	public void handleSyncSectMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSectMapCopyStart(mapID, mapInstance);
			}
		}
	}


	public void handleSyncSectMapCopyProgress(int mapID, int mapInstance, int spawnPoint, int lostHp, int damage)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSectMapCopyProgress(mapID, mapInstance, spawnPoint, lostHp, damage);
			}
		}
	}
	
	public void handleSyncArenaMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncArenaMapCopyStart(mapID, mapInstance);
			}
		}
	}
	
	public void handleSyncArenaMapCopyEnd(int mapID, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncArenaMapCopyEnd(mapID, mapInstance, win != 0, attackingSideHp, defendingSideHp);
			}
		}
	}
	
	public void handleSyncSuperArenaMapCopyStart(int mapID, int mapInstance, Map<Integer, Integer> eloDiffs)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession == 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSuperArenaMapCopyStart(mapID, mapInstance, eloDiffs);
			}
		}
	}
	
	public void handleSyncSuperArenaMapCopyEnd(int mapID, int mapInstance, SBean.SuperArenaBattleResult result, int rankClearTime)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession == 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSuperArenaMapCopyEnd(mapID, mapInstance, result, rankClearTime);
			}
		}
	}
	
	public void handleSyncEnterSuperArenaRace(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession == 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSuperEnterArenaRace(mapID, mapInstance);
			}
		}
	}
	
	public void handleSyncBWArenaMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncBWArenaMapCopyStart(mapID, mapInstance);
			}
		}
	}
	
	public void handleSyncBWArenaMapCopyEnd(int mapID, int mapInstance, int win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncBWArenaMapCopyEnd(mapID, mapInstance, win != 0, attackingSideHp, defendingSideHp);
			}
		}
	}
	
	public void handleSyncForceWarMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession == 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncForceWarMapCopyStart(mapID, mapInstance);
			}
		}
	}
	
	public void handleSyncForceWarMapCopyEnd(int mapID, int mapInstance, SBean.RankClearTime rankClearTime, int winSide, int killedBoss, int whiteScore, int blackScore, List<SBean.ForceWarOverview> whiteSide, List<SBean.ForceWarOverview> blackSide)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession == 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncForceWarMapCopyEnd(mapID, mapInstance, rankClearTime, winSide, killedBoss, whiteScore, blackScore, whiteSide, blackSide);
			}
		}
	}
	
	public void handleSyncDemonHoleMapCopyEnd(int mapID, int mapInstance, List<SBean.RoleDemonHole> curFloor, List<SBean.RoleDemonHole> total)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession == 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				gmaps.syncDemonHoleMapCopyEnd(mapID, mapInstance, curFloor, total);
			}
		}
	}
	
	public void handleSyncFightNpcMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				gmaps.syncFightNpcMapCopyStart(mapID, mapInstance);
			}
		}
	}
	
	public void handleSyncFightNpcMapCopyEnd(int mapID, int mapInstance, boolean win)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				gmaps.syncFightNpcMapCopyEnd(mapID, mapInstance, win);
			}
		}
	}
	
	public void handleSyncTowerDefenceMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				gmaps.syncTowerDefenceMapCopyStart(mapID, mapInstance);
			}
		}
	}
	
	public void handleSyncTowerDefenceMapCopyEnd(int mapID, int mapInstance, int count)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				gmaps.syncTowerDefenceMapCopyEnd(mapID, mapInstance, count);
			}
		}
	}
	
	public void handleSyncTowerDefenceSpawnCount(int mapID, int mapInstance, int count)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				gmaps.syncTowerDefenceSpawnCount(mapID, mapInstance, count);
			}
		}
	}
	
	public void handleSyncTowerDefenceScore(int mapID, int mapInstance, int roleID, int monsterID)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
			role.syncTowerDefenceScore(mapID, mapInstance, monsterID);
	}
	
	public void handleSyncMapCopyTimeOut(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession == 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
				gmaps.syncMapCopyTimeOut(mapID, mapInstance);
		}
	}
	
	public void handleSyncPetLifeMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncPetLifeMapCopyStart(mapID, mapInstance);
			}
		}
	}
	
	
	public void handleSyncLocation(int roleID, int mapId, int mapInstance, SBean.Location location)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncLocation(mapId, mapInstance, location);
	}
	
	public void handleSyncCurRideHorse(int roleID, int mapId, int mapInstance, int hid)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncCurRideHorse(mapId, mapInstance, hid);
	}
	
	public void handleSyncHp(int roleID, int mapId, int mapInstance, int hp, int hpMax)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncHp(mapId, mapInstance, hp, hpMax);
	}
	
	public void handleSyncSp(int roleID, int mapID, int mapInstance, int sp)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncSp(mapID, mapInstance, sp);
	}
	
	public void handleSyncArmorVal(int roleID, int mapID, int mapInstance, int armorVal, int armorValMax)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncArmorVal(mapID, mapInstance, armorVal, armorValMax);
	}
	
//	public void handleAddExp(int roleID, int mapId, int mapInstance, int exp)
//	{
//		Role role = gs.getLoginManager().getOnGameRole(roleID);
//		if (role != null)
//			role.addExp(mapId, mapInstance, exp);
//	}
	public void handleAddDrops(int roleID, int mapId, int mapInstance, Map<Integer, Integer> drops)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.addDrops(mapId, mapInstance, drops);
	}
	
	public void handlePickUpDrops(int roleID, int mapID, int mapInstance, Map<Integer, SBean.DummyGoods> drops)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.pickUpDrops(mapID, mapInstance, drops);
	}
	
	public void handlePickUpRareDrops(int roleID, int mapID, int mapInstance, int dropId, SBean.DummyGoods drop, int monsterId)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.pickUpRareDrops(mapID, mapInstance, dropId, drop, monsterId);
	}
	
	public void handleAddKill(int roleID, int mapId, int mapInstance, int killType, int killID, float weaponAdd, int killRole)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.addKill(mapId, mapInstance, killType, killID, 1, weaponAdd, killRole);
	}
	
	public void handleSyncDurability(int roleID, int mapId, int mapInstance, int wid, int durability)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncEquipDurability(mapId, mapInstance, wid, durability);
	}
	
	public void handleSyncEndMine(int roleID, int mapId, int mapInstance, int mineId, int mineInstance, boolean success)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
		{
			role.syncEndMine(mapId, mapInstance, mineId, mineInstance, success);
			if(success)
				gs.getSteleManager().syncRoleMineSteleSuccess(role, mineId);
		}
	}
	
	public void handleAddPKValue(int roleID, int mapId, int mapInstance, int pkValue)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.addPKValue(mapId, mapInstance, pkValue);
	}
	
	public void handleSyncWorldMapBossProgress(int bossID, int hp, String killerName, int killerId)
	{
		gs.getBossManager().syncBossProgress(bossID, hp, killerId, killerName);
	}
	
	public void handleWorldBossRecord(int bossID, SBean.BossRecord record)
	{
		gs.getBossManager().syncBossRecord(bossID, record);
	}
	
	public void handleWorldBossDamageRoles(int bossID, int killer, Map<Integer, Integer> damageRoles)
	{
	    gs.getBossManager().syncBossDamage(bossID, killer, damageRoles);
	}
	
	public void handleSyncSuperMonster(SBean.ActivityEntity monster, boolean add)
	{
		gs.getBossManager().syncSuperMonster(monster, add);
	}
	
	public void handleSyncWorldMineral(SBean.ActivityEntity mineral, boolean add)
	{
		gs.getBossManager().syncWorldMineral(mineral, add);
	}
	
	public void handleSyncCarLocation(int roleID, int mapID, int mapInstance, SBean.Location location)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncCarLocation(mapID, mapInstance, location);
	}
	
	public void handleSyncCarCurHP(int roleID, int mapID, int mapInstance, int curHP)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncCarHP(mapID, mapInstance, curHP);
	}
	
	public void handleUpdateCarDamageRole(int roleID, int mapID, int mapInstance, int damageRole, int damage)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.updateCarDamageRole(mapID, mapInstance, damageRole, damage);
	}
	
	public void handleSyncRoleRobSuccess(int roleID, int carID)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.syncRoleRobSuccess(carID);
	}
	
	public void handleUpdateRoleCarRobber(int roleID)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.updateRoleCarRobber();
	}	
	
	public void handleKickRoleFromMap(int roleID)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
			role.kickFromMap();
	}
	
	public void handleRoleUseItemSkill(int roleID, int itemID, int ok)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
		{
			role.onRoleUseItemSkill(itemID, ok);
		}
	}
	
	public void handleRoleUpdateFightState(int roleID, int mapID, int mapInstance, boolean fightState)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
		{
			role.updateFightState(mapID, mapInstance, fightState);
		}
	}
	
	public void handleRoleSyncPetHp(int roleID, int petID, int mapID, int mapInstance, SBean.Hp hpState)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if (role != null)
		{
			role.syncCurPetHp(mapID, mapInstance, petID, hpState);
		}
	}
	
	public void handleSyncRoleWeaponMaster(int roleID, int mapID, int mapInstance, int weaponID)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
		{
			role.syncWeaponMaster(mapID, mapInstance, weaponID);
		}
	}
	
	//游街结束
	public void handleSyncRoleMarriageParadeEnd(int manID, int womanID)
	{
		Role man = gs.getLoginManager().getOnGameRole(manID);
		if(man != null)
		{
			//TODO
		}
		
		Role woman = gs.getLoginManager().getOnGameRole(womanID);
		if(woman != null)
		{
			//TODO
		}
	}
	
	public void handleSyncSteleRemainTimes(int steleType, int index, int remainTimes)
	{
		gs.getSteleManager().syncSteleRemainTimes(steleType, index, remainTimes);
	}
				
	public void handleSyncRoleAddSteleCards(int roleID, int addCards, int addType)
	{
		Role role = gs.getLoginManager().getOnGameRole(roleID);
		if(role != null)
			gs.getSteleManager().syncRoleAddSteleCards(role, addCards, addType);
	}
	
	public void handleSyncRefreshSteleMonster(int roleID, String roleName, int mapID, int mapLine, int steleType, int index, int monsterID)
	{
		gs.getSteleManager().syncRefreshSteleMonster(roleID, roleName, mapID, mapLine, steleType, index, monsterID);
	}
	
	//handle end...
	
//	synchronized void sendCreateMapInstanceReq(int tag, int mapID)
//	{
//		int sid = getMapSession(mapID);
//		if (sid > 0)
//			gs.getRPCManager().notifyMapCreateMapInstance(sid, tag, mapID);
//	}
	
	void syncStartMapCopy(int sid, int mapId, int mapInstance)
	{
		gs.getRPCManager().notifyMapStartMapCopy(sid, mapId, mapInstance);
	}
	
	void syncEndMapCopy(int sid, int mapId, int mapInstance)
	{
		if(sid > 0)
			gs.getRPCManager().notifyMapEndMapCopy(sid, mapId, mapInstance);
	}
	
	void syncMapCopyReady(int sid, int mapId, int mapInstance)
	{
		gs.getRPCManager().notifyMapMapCopyReady(sid, mapId, mapInstance);
	}
	
	void syncResetSectMap(int sid, int mapId, int mapInstance, Map<Integer, Integer> progress)
	{
		gs.getRPCManager().notifyMapResetSectMap(sid, mapId, mapInstance, progress);
	}
	
	void syncResetArenaMap(int sessionid, int mapID, int instanceID, SBean.BattleArray enemy)
	{
		gs.getRPCManager().notifyMapResetArenaMap(sessionid, mapID, instanceID, enemy);
	}
	
	void syncResetBWArenaMap(int sessionid, int mapID, int instanceID, SBean.BattleArray enemy, boolean petLack)
	{
		gs.getRPCManager().notifyMapResetBWArenaMap(sessionid, mapID, instanceID, enemy, petLack);
	}
	
//	void syncResetClanOreMap(int sessionid, int mapID, int instanceID, SBean.BattleArray enemy)
//	{
//		gs.getRPCManager().notifyMapResetClanOreMap(sessionid, mapID, instanceID, enemy);
//	}
//	
//	void syncResetClanBattleHelpMap(int sessionid, int mapID, int instanceID, SBean.BattleArray enemy)
//	{
//		gs.getRPCManager().notifyMapResetClanBattleHelpMap(sessionid, mapID, instanceID, enemy);
//	}
//	
//	void syncResetClanBattleMap(int sessionid, int mapID, int instanceID, SBean.BattleArray enemy)
//	{
//		gs.getRPCManager().notifyMapResetClanBattleMap(sessionid, mapID, instanceID, enemy);
//	}
//	
//	void syncResetClanTaskMap(int sessionid, int mapID, int instanceID, SBean.BattleArray enemy)
//	{
//		gs.getRPCManager().notifyMapResetClanTaskMap(sessionid, mapID, instanceID, enemy);
//	}
	
	boolean syncCreateWorldMapBoss(int mapID, int mapInstanceID, int bossID, int seq, int curHP)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
		{
			gs.getRPCManager().notifyMapCreateWorldMapBoss(sid, mapID, mapInstanceID, bossID, seq, curHP);
			return true;
		}
		
		return false;
	}
	
	void syncCreateSuperMonster(int mapID, int superMonsterID, int standTime)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapCreateSuperMonster(sid, mapID, superMonsterID, standTime);
	}
	
	void syncCreateWorldMineral(int mapID, int worldMineralID, int standTime)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapCreateMineral(sid, mapID, worldMineralID, standTime);
	}
	
	void syncDestroyWorldMapBoss(int mapID, int mapInstanceID, int bossID)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapDestroyWorldMapBoss(sid, mapID, mapInstanceID, bossID);
	}
	
	void syncWorldBossPop(int mapID, int mapInstance, int bossID, int index)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapWorldBossPop(sid, mapID, mapInstance, bossID, index);
	}
	
	void syncRolePickUpResult(int mapID, int roleID, Set<Integer> dropIDs, int success)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapPickUpResult(sid, roleID, dropIDs, success);
		else
			gs.getRPCManager().notifyGlobalMapPickUpResult(roleID, dropIDs, success);
	}
	
	void syncCarEnterMap(SBean.DBEscortCar carInfo, int ownerID, String ownerName, int teamCarCnt, SBean.Team team, int sectID)
	{
		int sid = getMapSession(carInfo.mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapCarEnterMap(sid, carInfo, ownerID, ownerName, teamCarCnt, team, sectID);
	}
	
	void syncCarLeaveMap(int carID, int mapID, int mapInstance)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapCarLeaveMap(sid, carID, mapID, mapInstance);
	}

	void syncCarUpdateTeamCarCnt(int carID, int mapID, int carCnt)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapCarUpdateTeamCarCnt(sid, carID, carCnt);
	}
	
	void syncRoleUpdateCarBehavior(int roleID, int mapID, byte carOwner, byte carRobber)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCarBehavior(sid, roleID, carOwner, carRobber);
	}
	
	void syncRoleEnterMap(int sid, SBean.FightRole fightRole, int mapId, int mapInstance, SBean.Location location, int hp, int sp, int armorVal, Map<Integer, SBean.DBBuff> buffs, 
			Map<Integer, SBean.FightPet> pets, List<Integer> petSeq, SBean.PetHost host, SBean.PKInfo pkInfo, SBean.Team team, int curRideHorse, 
			SBean.MulRoleInfo mulRoleInfo, SBean.DBAlterState alterState, boolean isMainSpwanPos, int dayFailedStreak, int vipLevel, int curWizard, boolean canTakeDrop)
	{
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleEnterMap(sid, fightRole, mapId, mapInstance, location, hp, sp, armorVal, buffs, pets, petSeq, host, pkInfo, team, curRideHorse, mulRoleInfo, alterState, isMainSpwanPos, dayFailedStreak, vipLevel, curWizard, canTakeDrop);
		else
			gs.getRPCManager().notifyGlobalMapRoleEnterMap(fightRole, mapId, mapInstance, location, hp, sp, armorVal, buffs, pets, petSeq, host, pkInfo, team, curRideHorse, mulRoleInfo, alterState, isMainSpwanPos, dayFailedStreak, vipLevel, curWizard, canTakeDrop);
	}
	
	void syncRoleLeaveMap(int sid, int roleID, int mapId, int instanceId)
	{
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleLeaveMap(sid, roleID, mapId, instanceId);
		else
		{
			gs.getRPCManager().notifyGlobalMapRoleLeaveMap(roleID, mapId, instanceId);
			gs.getRPCManager().notifyFightRoleLeaveMap(roleID, mapId, instanceId);
		}
	}
	
	void syncRoleUpdateActive(int roleID, int mapId, boolean active)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateActive(sid, roleID, active);
		else
			gs.getRPCManager().notifyGlobalMapRoleUpdateActive(roleID, active);
	}
	
	void syncRolePetLack(int roleID, int mapID, boolean petLack)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleSyncPetLack(sid, roleID, petLack);
	}
	
//	void syncRoleClanBattleHurt(int roleID, int mapID, boolean hurt)
//	{
//		int sid = getMapSession(mapID);
//		if (sid > 0)
//			gs.getRPCManager().notifyMapRoleSyncClanBattleHurt(sid, roleID, hurt);
//	}
	
	void syncRoleSetPetAlter(int roleID, int mapID, SBean.FightPet fightPet, SBean.PetHost petHost)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleSetPetAlter(sid, roleID, fightPet, petHost);
	}
	
	void syncMapRoleUseItemSkill(int roleID, int mapID, int itemID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUseItemSkill(sid, roleID, itemID, pos, rotation, targetID, targetType, ownerID, timeTick);
		else
			gs.getRPCManager().notifyGlobalMapRoleUseItemSkill(roleID, itemID, pos, rotation, targetID, targetType, ownerID, timeTick);
	}
	
	void syncMapRoleRename(int mapID, int roleID, String newName)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleRename(sid, roleID, newName);
	}
	
	void syncRoleLuaChannelPacket(int roleID, int mapId, String data)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().sendMapLuaChannelPacket(sid, roleID, data);
		else
			gs.getRPCManager().sendGlobalMapLuaChannelPacket(roleID, data);
			
	}
	
	void syncRoleStrChannelPacket(int roleID, int mapId, String data)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().sendMapStrChannelPacket(sid, roleID, data);
		else
			gs.getRPCManager().sendGlobalMapStrChannelPacket(roleID, data);
	}
	
	void syncRoleGainNewSuite(int roleID, int mapId, int suiteID)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleGainNewSuite(sid, roleID, suiteID);
	}
	
	void syncRoleUpdateEquip(int roleID, int mapId, int wid, SBean.DBEquip equip)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateEquip(sid, roleID, wid, equip);
	}
	
	void syncRoleUpdateEquipPart(int roleID, int mapId, SBean.DBEquipPart equipPart)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateEquipPart(sid, roleID, equipPart);
	}
	
	void syncRoleUpdateSealGrade(int roleID, int mapID, int sealGrade)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateSealGrade(sid, roleID, sealGrade);
	}
	
	void syncRoleUpdateSealSkills(int roleID, int mapID, Map<Integer, Integer> skills)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateSealSkills(sid, roleID, skills);
	}
	
	void syncRoleUpdateUpdateWeapon(int roleID, int mapId, SBean.DBWeapon weapon)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateWeapon(sid, roleID, weapon);
	}
	
	
	void syncRoleUpdateUpdateCurWeapon(int roleID, int mapId, int curId)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurWeapon(sid, roleID, curId);
	}
	
	void syncRoleSetWeaponForm(int mapID, int roleID, int weaponID, byte form)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleSetWeaponForm(sid, roleID, weaponID, form);
	}
	
	void syncRoleUpdatePet(int roleID, int mapId, SBean.FightPet fightPet)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdatePet(sid, roleID, fightPet);
	}
	
	void syncRoleUpdateCurPetSpirit(int mapID, int roleID, int petID, int index, SBean.PetSpirit spirit)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurPetSpirit(sid, roleID, petID, index, spirit);
	}
	
	void syncRoleUpdateSkill(int roleID, int mapId, SBean.DBSkill skill)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateSkill(sid, roleID, skill);
	}
	
	void syncRoleUpdateSpirit(int roleID, int mapId, SBean.DBSpirit spirit)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateSpirit(sid, roleID, spirit);
	}
	
	void syncRoleUpdateCurSpirit(int roleID, int mapId, Set<Integer> curSpirits)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurSpirit(sid, roleID, curSpirits);
		else
			gs.getRPCManager().notifyGlobalMapRoleUpdateCurSpirit(roleID, curSpirits);
	}
	
	
	void syncRoleUpdateCurSkills(int roleID, int mapId, List<Integer> skills)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurSkills(sid, roleID, skills);
		else
			gs.getRPCManager().notifyGlobalMapRoleUpdateCurSkills(roleID, skills);
	}
	
	void syncRoleUpdateCurUniqueSkill(int roleID, int mapID, int curUniqueSkill)
	{
		int sid = getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurUniqueSkill(sid, roleID, curUniqueSkill);
	}
	
	//TO DELETE
	void syncRoleUpdateBuff(int roleID, int mapId, SBean.DBBuff buff)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateBuff(sid, roleID, buff);
	}
	
	void syncRoleAddBuff(int roleID, int mapId, int buffID)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleAddBuff(sid, roleID, buffID);
	}
	
	void syncRoleUpdateLevel(int roleID, int mapId, int level)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateLevel(sid, roleID, level);
	}
	
	void syncRoleAddHp(int roleID, int mapId, int hp)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleAddHp(sid, roleID, hp);
		else
			gs.getRPCManager().notifyGlobalMapRoleAddHp(roleID, hp);
	}
	
	void syncRolePetAddHp(int roleID, int mapId, int petId, int hp)
	{
		int sid = getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapRolePetAddHp(sid, roleID, petId, hp);
		else
			gs.getRPCManager().notifyGlobalMapRolePetAddHp(roleID, petId, hp);
	}
	
	void syncRoleStartMine(int roleID, int mapId, int mineId, int mineInstance)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleStartMine(sid, roleID, mineId, mineInstance);
		else
			gs.getRPCManager().notifyGlobalMapRoleStartMine(roleID, mineId, mineInstance);
	}
	
	void syncRoleRevive(int roleID, int mapId, boolean fullHp)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleRevive(sid, roleID, fullHp);
		else
			gs.getRPCManager().notifyGlobalMapRoleRevive(roleID, fullHp);
	}
	
	void syncRoleUpdateTeam(int roleID, int mapId, SBean.Team team)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateTeam(sid, roleID, team);
	}
	
	void syncRoleChangeCurPets(int roleID, int mapId, Map<Integer, SBean.FightPet> pets)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleChangeCurPets(sid, roleID, pets);
	}
	
	void syncRoleUnSummonPets(int roleID, int mapID)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUnSummonPets(sid, roleID);
	}
	
	void syncRoleUpdateSectBrief(int roleID, int mapId, SBean.SectBrief sectBrief)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateSectBrief(sid, roleID, sectBrief);
	}
	
	void syncRoleUpdateSectAura(int roleID, int mapId, int auraID, int auraLvl)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateSectAura(sid, roleID, auraID, auraLvl);
	}
	
	void syncRoleResetSectAuras(int roleID, int mapId, Map<Integer, Integer> sectAuras)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleResetSectAuras(sid, roleID, sectAuras);
	}
	
//	void syncRoleUpdateClanDiziTangAttr(int roleID, int mapID, SBean.ClanOwnerAttriAddition attr)
//	{
//		if(attr == null)
//			return;
//		int sid  = this.getMapSession(mapID);
//		if(sid > 0)
//			gs.getRPCManager().notifyMapRoleUpdateClanDiziTangAttr(sid, roleID, attr);
//	}
	
	void syncRoleUpdatePKInfo(int roleID, int mapId, int mode, int value)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdatePKInfo(sid, roleID, mode, value);
	}
	
	void syncRoleUpdateCurDIYSkill(int roleID, int mapId, SBean.DBDIYSkillData curDIYSkill)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurDIYSkill(sid, roleID, curDIYSkill);
	}
	
	void syncRoleUpdateTransformInfo(int roleID, int mapId, byte transformLevel, byte BWType)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateTransformInfo(sid, roleID, transformLevel, BWType);
	}
	
	void syncRoleUpdateHorseInfo(int mapID, int roleID, SBean.HorseInfo info)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateHorse(sid, roleID, info);
	}
	
	void syncRoleUpdateHorseSkill(int mapID, int roleID, int skillID, int skillLvl)
	{
		int sid = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateHorseSkill(sid, roleID, skillID, skillLvl);
	}
	
	void syncRoleUpdateCurUseHorse(int mapID, int roleID, int hid)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurUseHorse(sid, roleID, hid);
	}
	
	void syncRoleChangeHorseShow(int mapID, int roleID, int hid, int showID)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleChangeHorseShow(sid, roleID, hid, showID);
	}
	
	void syncRoleUpdateMedal(int mapID, int roleID, int medal , byte state)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateMedal(sid, roleID, medal, state);
	}
	
	void syncRoleUpWearFashions(int mapID, int roleID, int type, int fashionID, int isShow)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpWearFashions(sid, roleID, type, fashionID, isShow);
	}
	
	void syncRoleUpdateAlterState(int mapID, int roleID, SBean.DBAlterState alterState)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateAlterState(sid, roleID, alterState);
	}
	
	void syncRoleUpdateGrasp(int mapID, int roleID, int graspID, int level)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateGrasp(sid, roleID, graspID, level);
	}
	
	void syncRoleUpdateRareBook(int mapID, int roleID, int bookID, int level)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateRareBook(sid, roleID, bookID, level);
	}
	
	void syncRoleUpdateTitle(int mapID, int roleID, int titleID, boolean add)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateTitle(sid, roleID, titleID, add);
	}
	
	void syncRoleUpdateCurTitle(int mapID, int roleID, int titleID, int titleType)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateCurTitle(sid, roleID, titleID, titleType);
	}
	
	void syncRoleUpdatePetAchieve(int mapID, int roleID, Set<Integer> achieves)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdatePetAchieve(sid, roleID, achieves);
	}
	
	void syncRoleCurRideHorse(int mapID, int roleID, int horseID)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleSyncCurRideHorse(sid, roleID, horseID);
	}
	
	void syncRoleUpdateMulHorse(int mapID, int leaderID, int pos, int memberID)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateMulHorse(sid, leaderID, pos, memberID);
	}

	void syncRoleChangeArmor(int mapID, int roleId, SBean.ArmorFightData armor)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleChangeArmor(sid, roleId, armor);
	}

	void syncRoleUpdateArmorLevel(int mapID, int roleId, int level)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateArmorLevel(sid, roleId, level);
	}

	void syncRoleUpdateArmorRank(int mapID, int roleId, int rank)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateArmorRank(sid, roleId, rank);
	}

	void syncRoleUpdateArmorRune(int mapID, int roleId, int soltindex, List<Integer> runes)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateArmorRune(sid, roleId, soltindex, runes);
	}

	void syncRoleUpdateArmorTalentPoint(int mapID, int roleId, Map<Integer, Integer> talentPoint)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateArmorTalentPoint(sid, roleId, talentPoint);
	}
	
	void syncRoleUpdateStayWith(int mapID, int roleID, SBean.MulRoleInfo mulRoleInfo)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleUpdateStayWith(sid, roleID, mulRoleInfo);
	}
	
	void syncRoleSpawnSceneMonster(int mapID, int mapInstance, int roleID, int pointID)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleSpawnSceneMonster(sid, mapID, mapInstance, roleID, pointID);
	}
	
	void syncRoleClearSceneMonster(int mapID, int roleID, int monsterID)
	{
		int sid  = this.getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapClearSpawnSceneMonster(sid, roleID, monsterID);
	}
	
	private int getMapSession(int mapID)
	{
		Integer session = mapm2s.get(mapID);
		return session == null ? -1 : session;
	}
	
	synchronized void syncMapDoubleDropCfg(List<SBean.DoubleDropCfg> cfgs)
	{
		for (int sid : maps2m.keySet())
		{
			gs.getRPCManager().notifyMapSyncDoubleDropCfg(sid, cfgs);
		}
	}
	
	synchronized void syncMapExtraDropCfg(List<SBean.ExtraDropCfg> cfgs)
	{
		for (int sid : maps2m.keySet())
		{
			gs.getRPCManager().notifyMapSyncExtraDropCfg(sid, cfgs);	
		}
	}
	
	synchronized void syncMapWorldNum(int worldNum, Map<Integer, Integer> extralWorldNum)
	{
		for (int sid : maps2m.keySet())
		{
			gs.getRPCManager().notifyMapSyncWorldNum(sid, worldNum, extralWorldNum);
		}
	}
	
	synchronized void syncAllMapsTimeOffset(int timeOffset)
	{
		for (int sid : maps2m.keySet())
		{
			gs.getRPCManager().notifyMapSyncServerTimeOffset(sid, timeOffset);
		}
	}
	
	//启动时由map连接之后初始化
	synchronized void mapStartWork(int sid, int msid, Set<Integer> maps)
	{		
		GameMaps gmaps = new GameMaps(sid, msid, gs);
		gmaps.init(maps);
		maps2m.put(sid, gmaps);
		for (int mid : maps)
		{
			mapm2s.put(mid, sid);
			gs.getBossManager().mapStopWork(mid);
		}
		
		if(sid > 0)
		{
			gs.getRPCManager().notifyMapSyncDoubleDropCfg(sid, gs.getGameConf().getDoubleDropActivities().getAllConfigs());
			gs.getRPCManager().notifyMapSyncExtraDropCfg(sid, gs.getGameConf().getExtraDropActivities().getAllConfigs());
			gs.getRPCManager().notifyMapSyncWorldNum(sid, gs.getConfig().worldNum, new HashMap<>(gs.getConfig().extraWorldNum));
		}
	}
	
	//map断开连接时清除所有role
	synchronized void mapStopWork(int sid)
	{
		GameMaps gmaps = maps2m.remove(sid);
		if (gmaps != null)
		{
			gs.getLogger().info("map server " + gmaps.getID() + " stop work !");
			for (int mapId : gmaps.getAllMaps())
			{
				mapm2s.remove(mapId);
			}
			gmaps.fini();
		}
	}
	
	synchronized void clearGlobalMapRoles()
	{
		GameMaps gmaps = maps2m.get(0);
		if (gmaps != null)
		{
			gs.getLogger().info("clear global map role");
			gmaps.reset();
		}
	}
	
	//在role锁内调用
	int createNormalMapCopy(int mapId)
	{
		gs.getLogger().debug("try create new normal map copy " + mapId);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createNormalMapCopy(mapId);
			}
		}
		return 0;
	}
	
	//在role锁内调用
	int createJusticeMapCopy(int mapId)
	{
		gs.getLogger().debug("try create new justice map copy " + mapId);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createJusticeMapCopy(mapId);
			}
		}
		return 0;
	}
	
	int createTowerDefenceMapCopy(int mapID)
	{
		gs.getLogger().debug("try create new tower defence map copy " + mapID);
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createTowerDefence(mapID);
			}
		}
		return 0;
	}
	
	//在role锁内调用
	int createActivityMapCopy(int mapId)
	{
		gs.getLogger().debug("try create new activity map copy " + mapId);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createActivityMapCopy(mapId);
			}
		}
		return 0;
	}
	
	//在role锁内调用
	int createWeaponMapCopy(int mapID)
	{
		gs.getLogger().debug("try create new weapon map copy " + mapID);
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				return gmaps.createWeaponMapCopy(mapID);
			}
		}
		
		return 0;
	}
	
	//在role锁内调用
	int createSectMapCopy(int mapId, int sectId, Map<Integer, Integer> progress)
	{
		gs.getLogger().debug("try create sect " + sectId + " map " + mapId);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createSectMapCopy(mapId, sectId, progress);
			}
		}
		return 0;
	}
	
	//在sect锁内调用
	int createSectGroupMapCopy(int mapId, int sectId, int startTime, Map<Integer, Integer> process, Map<Integer, Integer> killNum, Map<Integer, SBean.RoleDamageDetail> damageRank)
	{
		gs.getLogger().debug("try create sect " + sectId + " group map " + mapId + " start time " + startTime);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createSectGroupMapCopy(mapId, sectId, startTime, process, killNum, damageRank);
			}
		}
		return 0;
	}
	
	//在manager锁内调用
	int createEmergencyMapCopy(int mapId, int endTime)
	{
		gs.getLogger().debug("try create emergency map " + mapId + " end time " + endTime);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createEmergencyMapCopy(mapId, endTime);
			}
		}
		return 0;
	}
	
	//在role锁内调用
	int createArenaMapCopy(int mapId, SBean.BattleArray enemy)
	{
		gs.getLogger().debug("try create arena map " + mapId + ", enemy role Id" +  enemy.fightRole.base.roleID);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createArenaMapCopy(mapId, enemy);
			}
		}
		return 0;
	}
	
	int createBWArenaMapCopy(int mapID, SBean.BattleArray enemy, boolean petLack, boolean sameBWArenaLvl)
	{
		gs.getLogger().debug("try create bw arena map " + mapID + ", enemy role ID" +  enemy.fightRole.base.roleID);
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createBWArenaMapCopy(mapID, enemy, petLack, sameBWArenaLvl);
			}
		}
		return 0;
	}
	
	int createFightMapCopy(int mapID, int mapInstance)
	{
		gs.getLogger().debug("try create fight map copy [" + mapID + " , " + mapInstance + "]");
		if(mapInstance > 0)
		{
			int mapSession = getMapSession(mapID);
			if(mapSession == 0)	//global map
			{
				GameMaps gmaps = maps2m.get(mapSession);
				if(gmaps != null)
				{
					return gmaps.createFightMapCopy(mapID, mapInstance);
				}
			}
		}
		return 0;
	}
	
	int createFightNpcMapCopy(int mapID)
	{
		gs.getLogger().debug("try create fight npc map " + mapID);
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createFightNpcMapCopy(mapID);
			}
		}
		return 0;
	}
	
//	//夺矿战
//	int createClanOreMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		gs.getLogger().debug("try create clan ore map " + mapId + ", enemy role Id" +  enemy.fightRole.base.roleID);
//		int mapSession = getMapSession(mapId);
//		if (mapSession > 0)
//		{
//			GameMaps gmaps = maps2m.get(mapSession);
//			if (gmaps != null)
//			{
//				return gmaps.createClanOreMapCopy(mapId, enemy);
//			}
//		}
//		return 0;
//	}
//	
//	//宗门战支援战
//	int createClanBattleHelpMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		gs.getLogger().debug("try create clan battle help map " + mapId + ", enemy role Id" +  enemy.fightRole.base.roleID);
//		int mapSession = getMapSession(mapId);
//		if (mapSession > 0)
//		{
//			GameMaps gmaps = maps2m.get(mapSession);
//			if (gmaps != null)
//			{
//				return gmaps.createClanBattleHelpMapCopy(mapId, enemy);
//			}
//		}
//		return 0;
//	}
//	//宗门战
//	int createClanBattleMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		gs.getLogger().debug("try create clan battle map " + mapId + ", enemy role Id" +  enemy.fightRole.base.roleID);
//		int mapSession = getMapSession(mapId);
//		if (mapSession > 0)
//		{
//			GameMaps gmaps = maps2m.get(mapSession);
//			if (gmaps != null)
//			{
//				return gmaps.createClanBattleMapCopy(mapId, enemy);
//			}
//		}
//		return 0;
//	}
//
//	
//	//遭遇战
//	int createClanTaskMapCopy(int mapId, SBean.BattleArray enemy)
//	{
//		gs.getLogger().debug("try create clan task map " + mapId + ", enemy role Id" +  enemy.fightRole.base.roleID);
//		int mapSession = getMapSession(mapId);
//		if (mapSession > 0)
//		{
//			GameMaps gmaps = maps2m.get(mapSession);
//			if (gmaps != null)
//			{
//				return gmaps.createClanTaskMapCopy(mapId, enemy);
//			}
//		}
//		return 0;
//	}
	
	//随从身世副本
	int createPetLifeMapCopy(int mapID)
	{
		gs.getLogger().debug("try create new pet life map copy " + mapID);
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.createPetLifeMapCopy(mapID);
			}
		}
		return 0;
	}
	
	//爬塔副本
	int createClimbTowerCopy(int mapId)
	{
		gs.getLogger().debug("try create new climb tower map copy " + mapId);
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
				return gmaps.createClimbTowerMapCopy(mapId);
		}
		return 0;
	}
	
	void syncMapCopyReady(int mapId, int mapInstance)
	{
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncMapCopyReady(mapId, mapInstance);
			}
		}
	}
	
	void onWorldLineNumChange(int num, Map<Integer, Integer> extralWorldNum)
	{
		for(GameMaps gmaps: maps2m.values())
		{
			gmaps.onWorldLineNumChange(num, extralWorldNum);
		}
	}
	
	//在role锁中调用
	boolean isWorldMapFull(int mapID, int line)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				return gmaps.isWorldMapFull(mapID, line);
			}
		}
		return false;
	}
	
	//在role锁中调用
	int getMinWorld(int mapID, int roleID, int line, int lastLine)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				return gmaps.getMinWorld(mapID, roleID, line, lastLine);
			}
		}
		
		return -1;
	}
	
	//在role锁中调用
	int getWorldLineNum(int mapID)
	{
		int mapSession = getMapSession(mapID);
		if(mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if(gmaps != null)
			{
				return gmaps.getWorldLineNum(mapID);
			}
		}
		
		return -1;
	}
	
	//在role锁中调用
	boolean roleEnterMap(SBean.FightRole fightRole, int mapId, int mapInstance, SBean.Location location, int hp, int sp, int armorVal, Map<Integer, SBean.DBBuff> buffs, 
			Map<Integer, SBean.FightPet> pets, List<Integer> petSeq, SBean.PetHost host, SBean.PKInfo pkInfo, SBean.Team team, int curRideHorse, 
			SBean.MulRoleInfo mulRoleInfo, SBean.DBAlterState alterState, boolean isMainSpwanPos, int dayFailedStreak, int vipLevel, int curWizard, boolean canTakeDrop)
	{
		int mapSession = getMapSession(mapId);
		if (mapSession >= 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				if (gmaps.roleEnterMap(fightRole.base.roleID, mapId, mapInstance))
				{
					syncRoleEnterMap(mapSession, fightRole, mapId, mapInstance, location, hp, sp, armorVal, buffs, pets, petSeq, host, pkInfo, team, curRideHorse, mulRoleInfo, alterState, isMainSpwanPos, dayFailedStreak, vipLevel, curWizard, canTakeDrop);
					return true;
				}
			}
		}
		return false;
	}
	
	//在role锁中调用
	boolean roleLeaveMap(int rid, int mapId, int instanceId)
	{
		int mapSession = getMapSession(mapId);
		if (mapSession >= 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				syncRoleLeaveMap(mapSession, rid, mapId, instanceId);
				gmaps.roleLeaveMap(rid, mapId, instanceId);
			}
		}
		return true;
	}
	
	//在role锁中调用
	boolean roleResetMapLocation(int rid, int mapId, int instanceId, SBean.Location location)
	{
		int sid  = this.getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapRoleResetLocation(sid, rid, mapId, instanceId, location);
		else
			gs.getRPCManager().notifyGlobalMapRoleResetLocation(rid, mapId, instanceId, location);
		return false;
	}
	
	Collection<Integer> getWolrdMapRoles(int mapId, int mapInstance)
	{
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.getWorldMapRoles(mapId, mapInstance);
			}
		}
		return GameData.emptyList();
	}
	
	Collection<Integer> getSectGroupMapRoles(int mapId, int mapInstance)
	{
		int mapSession = getMapSession(mapId);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				return gmaps.getSectGroupMapRoles(mapId, mapInstance);
			}
		}
		return GameData.emptyList();
	}
	
	static class RoleOnMapInfo
	{
		public int rid;
		public int mapServerID;
		public int mapID;
	}

	public void handleSyncSectGroupMapCopyStatus(int mapID, int mapInstance, int progress)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSectGroupMapCopyStatus(mapID, mapInstance, progress);
			}
		}
	}

	public void handleSyncSectGroupMapCopyResult(int mapID, int mapInstance, int progress)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSectGroupMapCopyResult(mapID, mapInstance, progress);
			}
		}
	}
	
	public void handleSyncSectGroupMapCopyProgress(int mapID, int mapInstance, int spawnPoint, int roleId, int monsterId, int lostHp, int damage)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSectGroupMapCopyProgress(mapID, mapInstance, spawnPoint, roleId, monsterId, lostHp, damage);
			}
		}
	}
	
	public void handleSyncSectGroupMapCopyAddKill(int mapID, int mapInstance, int monsterId, int spawnPointId)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSectGroupMapCopyAddKill(mapID, mapInstance, monsterId, spawnPointId);
			}
		}
	}
	
	public void handleSyncMapFlagCanTake(int mapID)
	{
		gs.getFlagManager().changeFlagCanTake(mapID);
	}
	
	public void handleSyncSectGroupMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncSectGroupMapCopyStart(mapID, mapInstance);
			}
		}
	}

	public void handleSyncEmergencyMapCopyStart(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncEmergencyMapCopyStart(mapID, mapInstance);
			}
		}
	}

	public void handleSyncEmergencyMapCopyEnd(int mapID, int mapInstance)
	{
		int mapSession = getMapSession(mapID);
		if (mapSession > 0)
		{
			GameMaps gmaps = maps2m.get(mapSession);
			if (gmaps != null)
			{
				gmaps.syncEmergencyMapCopyEnd(mapID, mapInstance);
			}
		}
	}
	
	void syncResetSectGroupMap(int sid, int mapId, int mapInstance, Map<Integer, Integer> process, Map<Integer, Integer> killNum, Map<Integer, SBean.RoleDamageDetail> damageRank)
	{
		gs.getRPCManager().notifyMapResetSectGroupMap(sid, mapId, mapInstance, process, killNum, damageRank);
	}
	
	public void syncRoleUpdateWeaponSkills(int roleId, int mapId, int weaponId, List<Integer> skills)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateRoleWeaponSkills(sid, roleId, weaponId, skills);
	}
	
	public void syncRoleUpdatePetSkills(int roleId, int mapId, int petId, List<Integer> skills)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateRolePetSkills(sid, roleId, petId, skills);
	}

	public void syncRoleUpdateWeaponTalents(int roleId, int mapId, int weaponId, List<Integer> talent)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateRoleWeaponTalents(sid, roleId, weaponId, talent);
	}
	
	public void syncAddMapFlag(int mapId, Vector3 flagPoint, int flagId, List<Integer> monsterPointId, SBean.MapFlagSectOverView sect)
	{
		int sid = getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapCreateMapFlag(sid, mapId, flagPoint, flagId, monsterPointId, sect);
	}
	
	public void syncSyncMapFlagInfo(int mapId, SBean.MapFlagSectOverView sect)
	{
		int sid = getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapSyncMapFlagInfo(sid, mapId, sect);
	}
	
	public void syncRoleItemProps(int mapId, int roleId, HashMap<Integer, Integer> itemProps)
	{
		int sid = getMapSession(mapId);
		if(sid > 0)
			gs.getRPCManager().notifyMapSyncRoleItemProps(sid, roleId, itemProps);
	}
	
	public void syncRoleTaskDrop(int mapID, int roleID, Map<Integer, Integer> taskDrop)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapSyncRoleTaskDrop(sid, roleID, taskDrop);
	}
	
	public void syncRoleWeaponOpen(int mapID, int roleID, int weaponID)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapSyncRoleWeaponOpen(sid, roleID, weaponID);
	}
	
	public void syncRoleStartMarriageParade(int mapID, int mapInstance, int carID, SBean.RoleOverview man, SBean.RoleOverview woman)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapStartMarriageParade(sid, mapID, mapInstance, carID, man, woman);
	}
	
	public void syncRoleStartMarriageBanquet(int roleID, int mapID, int mapInstance, int banquet)
	{
		int sid = getMapSession(mapID);
		if(sid > 0)
			gs.getRPCManager().notifyMapStartMarriageBanquet(sid, roleID, mapID, mapInstance, banquet);
	}
	
	public void syncRoleUpdatePerfectDegree(int roleId, int mapId, int perfectDegree)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateRolePerfectDegree(sid, roleId, perfectDegree);
	}
	
	public void syncRoleUpdateMarriageSkillLevel(int roleId, int mapId, int skillId, int skillLevel)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateMarriageSkillLevel(sid, roleId, skillId, skillLevel);
		else if (sid == 0)
			gs.getRPCManager().notifyGlobalMapUpdateMarriageSkillLevel(roleId, skillId, skillLevel);
	}
	
	public void syncRoleUpdateMarriageSkillInfo(int roleId, int mapId, Map<Integer, Integer> skills, int partnerID)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateMarriageSkillInfo(sid, roleId, skills, partnerID);
		else if (sid == 0)
			gs.getRPCManager().notifyGlobalMapUpdateMarriageSkillInfo(roleId, skills, partnerID);
	}
	
	public void syncRoleMarriageLevelChange(int mapID, int roleID, int newLevel)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapMarriageLevelChange(sid, roleID, newLevel);
	}
	
	public void syncRoleUpdateHeirloomDisplay(int roleId, int mapId, int display)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateRoleHeirloomDisplay(sid, roleId, display);
	}
	
	public void syncRoleDMGTransferPointLvlsUpdate(int mapID, int roleID, Map<Integer, Integer> pointLvls)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleDMGTransferPointLvlsUpdate(sid, roleID, pointLvls);
	}
	
	public void syncCreateRobotHero(int mapID, int mapInstance, SBean.FightRole robot, Map<Integer, SBean.FightPet> curFightPets, int spawnPoint)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapCreateRobotHero(sid, mapID, mapInstance, robot, curFightPets, spawnPoint);
	}
	
	public void syncDestroyRobotHero(int mapID, int mapInstance, int roleID)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapDestroyRobotHero(sid, mapID, mapInstance, roleID);
	}
	
	public void syncCreateStele(int mapID, int steleType, int index, int remainTimes)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapCreateStele(sid, steleType, index, remainTimes);
	}
	
	public void syncDestroyStele(int mapID, int steleType, int index)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapDestroyStele(sid, steleType, index);
	}
	
	public void syncJusticeNpcShow(int mapID, int posIndex)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapJusticeNpcShow(sid, posIndex);
	}
	
	public void syncJusticeNpcLeave(int mapID, int posIndex)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapJusticeNpcLeave(sid, posIndex);
	}
	
	public void syncEmergencyLastTime(int mapId, int mapInstance, int endTime)
	{
		int sid = this.getMapSession(mapId);
		if (sid > 0)
			gs.getRPCManager().syncEmergencyLastTime(sid, mapId, mapInstance, endTime);
	}

	public void syncRoleVipLevel(int curMapId, int roleId, int useableVipLvl)
	{
		int sid = this.getMapSession(curMapId);
		if (sid > 0)
			gs.getRPCManager().syncRoleVipLevel(sid, roleId, useableVipLvl);
	}
	
	public void syncRoleWizardPet(int curMapId, int roleId, int petId)
	{
		int sid = this.getMapSession(curMapId);
		if (sid > 0)
			gs.getRPCManager().syncRoleWizardPet(sid, roleId, petId);
	}
	
	public void updateRoleSpecialCardAttr(int mapID, int roleID, Map<Integer, Integer> attrs)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapUpdateRoleSpecialCardAttr(sid, roleID, attrs);
	}
	
	public void roleShowProps(int mapID, int roleID, int propID)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleShowProps(sid, roleID, propID);
	}
	
	public void gmCommandToMap(int mapID, int roleID, String iType, int iArg1, int iArg2, int iArg3, String sArg)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapGMCommond(sid, roleID, iType, iArg1, iArg2, iArg3, sArg);
	}
	
	public void roleRedNamePunish(int mapID, int roleID)
	{
		int sid = this.getMapSession(mapID);
		if (sid > 0)
			gs.getRPCManager().notifyMapRoleRedNamePunish(sid, roleID);
	}
	
	private GameServer gs;
	private AtomicInteger nextTaskID = new AtomicInteger();
	private ConcurrentMap<Integer, MapServiceTask> tasks = new ConcurrentHashMap<>();
	
	
	//map id --> sid
	private ConcurrentMap<Integer, Integer> mapm2s = new ConcurrentHashMap<>();
	//sid --> maps
	private ConcurrentMap<Integer, GameMaps> maps2m = new ConcurrentHashMap<>();
	//rid -- > sid
	//private ConcurrentMap<Integer, Integer> mapr2s = new ConcurrentHashMap<Integer, Integer>();
}

