package i3k.gs;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.sound.midi.MidiDevice.Info;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;
import i3k.SBean;
import i3k.SBean.DBMapFlagInfo;
import i3k.SBean.FlagBattleMapCFGS;
import i3k.SBean.MapFlagInfo;
import i3k.SBean.MapFlagSectOverView;
import i3k.util.GameRandom;
import i3k.util.GameTime;

public class FlagManager
{
	private static final int FLAG_SAVE_INTERVAL = 900;
	private static final int MINUTE_SECOND = 60;

	public class SaveTrans implements Transaction
	{
		public SaveTrans(Map<Integer, SBean.DBMapFlagInfo> mapFlags)
		{
			this.mapFlags = new SBean.DBMapFlag(mapFlags);
		}

		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("mapFlags");
			byte[] data = Stream.encodeLE(mapFlags);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if (errcode != ErrorCode.eOK)
			{
				gs.getLogger().warn("world map flag save failed");
			}
		}

		@AutoInit
		public Table<byte[], byte[]> world;

		public final SBean.DBMapFlag mapFlags;
	}

	public FlagManager(GameServer gs)
	{
		this.gs = gs;
	}

	public void init(SBean.DBMapFlag mapFlags)
	{
		int now = GameTime.getTime();
		if (mapFlags != null)
		{
			this.mapFlags = toInfo(mapFlags.mapFlags);
		}
		lastSaveTime = now;
		checkRandom = GameRandom.getRandInt(0, MINUTE_SECOND);
	}

	private Map<Integer, SBean.MapFlagInfo> toInfo(Map<Integer, SBean.DBMapFlagInfo> mapFlags)
	{
		Map<Integer, SBean.MapFlagInfo> info = new HashMap<>();
		for (SBean.DBMapFlagInfo db : mapFlags.values())
		{
			info.put(db.mapId, new SBean.MapFlagInfo(db.mapId, gs.getSectManager().getSectFlagOverview(db.curSectId), db.occupyTime, db.lastRoleRewardTime, db.lastSectRewardTime, (byte) 0));
		}
		return info;
	}

	private Map<Integer, SBean.DBMapFlagInfo> toDB(Map<Integer, SBean.MapFlagInfo> mapFlags)
	{
		Map<Integer, SBean.DBMapFlagInfo> db = new HashMap<>();
		for (SBean.MapFlagInfo info : mapFlags.values())
		{
			db.put(info.mapId, new SBean.DBMapFlagInfo(info.mapId, info.curSect.sectId, info.occupyTime, info.lastRoleRewardTime, info.lastSectRewardTime));
		}
		return db;
	}

	public void save()
	{
		gs.getDB().execute(new SaveTrans(Stream.clone(toDB(mapFlags))));
		this.lastSaveTime = GameTime.getTime();
	}

	public void onTimer(int timeTick)
	{
		this.minuteTask(timeTick);
		if (this.lastSaveTime + FLAG_SAVE_INTERVAL <= timeTick)
			this.save();
	}

	public synchronized void minuteTask(int timeTick)
	{
		if (timeTick % MINUTE_SECOND != checkRandom)
			return;
		this.trySendReward(timeTick);
		this.sectRefresh();
	}
	
	private void trySendReward(int timeTick)
	{
		SBean.FlagBattleCFGS flagCFGS = GameData.getInstance().getFlagBattleCFGS();
		for (SBean.MapFlagInfo flag : this.mapFlags.values())
		{
			if (flag.curSect.sectId == 0)
				continue;
			boolean refreash = false;
			while (flag.lastRoleRewardTime + flagCFGS.roleRewardTime < timeTick || (GameTime.getDay(flag.lastRoleRewardTime) == GameTime.getDay(timeTick) && GameTime.getSecondOfDay(timeTick) > flagCFGS.endTime))
			{
				int nextRewardTime = flag.lastRoleRewardTime + flagCFGS.roleRewardTime;
				if (GameTime.getSecondOfDay(nextRewardTime) < flagCFGS.endTime && GameTime.getDay(nextRewardTime) == GameTime.getDay(flag.lastRoleRewardTime))
				{
					gs.getSectManager().sendFlagRoleReward(flag.curSect.sectId, flag.mapId, flag.lastRoleRewardTime + flagCFGS.roleRewardTime);
					flag.lastRoleRewardTime += flagCFGS.roleRewardTime;
					refreash = true;
				}
				else
				{
					gs.getSectManager().sendFlagEndRoleReward(flag.curSect.sectId, flag.mapId, GameTime.getDayTime(GameTime.getDay(flag.lastRoleRewardTime), flagCFGS.endTime));
					flag.lastRoleRewardTime = GameTime.getDayTime(GameTime.getDay(flag.lastRoleRewardTime) + 1, flagCFGS.startTime);
					refreash = true;
				}
			}
			if (refreash)
				gs.getMapService().syncAddMapFlag(flag.mapId, flagCFGS.flags.get(flag.mapId).flagPoint, flagCFGS.flags.get(flag.mapId).flagId, flagCFGS.flags.get(flag.mapId).monsterPointId, flag.curSect);
			while (flag.lastSectRewardTime + flagCFGS.sectVitRewardTime < timeTick || (GameTime.getDay(flag.lastSectRewardTime) == GameTime.getDay(timeTick) && GameTime.getSecondOfDay(timeTick) > flagCFGS.endTime))
			{
				int nextRewardTime = flag.lastSectRewardTime + flagCFGS.sectVitRewardTime;
				if (GameTime.getSecondOfDay(nextRewardTime) < flagCFGS.endTime && GameTime.getDay(nextRewardTime) == GameTime.getDay(flag.lastSectRewardTime))
				{
					gs.getSectManager().flagAddSectVit(flag.curSect.sectId, flag.mapId);
					flag.lastSectRewardTime += flagCFGS.sectVitRewardTime;
				}
				else
				{
					gs.getSectManager().flagEndAddSectVit(flag.curSect.sectId, flag.mapId);
					flag.lastSectRewardTime = GameTime.getDayTime(GameTime.getDay(flag.lastSectRewardTime) + 1, flagCFGS.startTime);
				}
			}
		}
	}

	public synchronized void roleGetFlag(Role role, int sectId, int mapId)
	{
		SBean.FlagBattleCFGS cfgs = GameData.getInstance().getFlagBattleCFGS();
		if (!cfgs.flags.containsKey(mapId))
			return;
		if (getSectFlagNum(sectId) >= cfgs.sectMaxFlagNum)
			return;
		SBean.MapFlagInfo curFlag = this.mapFlags.get(mapId);
		int now = GameTime.getTime();
		gs.getLoginManager().addNormalTaskEvent(() ->
			{
				if (curFlag != null && curFlag.curSect.sectId != 0)
					gs.getSectManager().notifyFlagLoss(curFlag.curSect.sectId, role.getSectName(), role.name, mapId);
				gs.getSectManager().notifyFlagGet(sectId, curFlag.curSect.sectName, role.name, mapId);
			}
		);
		SBean.MapFlagInfo newflag = new SBean.MapFlagInfo(mapId, gs.getSectManager().getSectFlagOverview(sectId), now, now, now, (byte) 0);
		gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_BATTLE_FLAG, newflag.curSect.sectName + "|" + mapId);
		this.mapFlags.put(mapId, newflag);
		gs.getMapService().syncAddMapFlag(newflag.mapId, cfgs.flags.get(mapId).flagPoint, cfgs.flags.get(mapId).flagId, cfgs.flags.get(mapId).monsterPointId, newflag.curSect);
	}

	public boolean flagCanTake(int curMapId)
	{
		return mapFlags.containsKey(curMapId) && mapFlags.get(curMapId).cantake == 1;
	}
	
	public boolean isSectDif(int curMapId, int sectId)
	{
		return !mapFlags.containsKey(curMapId) || sectId != mapFlags.get(curMapId).curSect.sectId;
	}

	public synchronized void changeFlagCanTake(int mapID)
	{
		if (mapFlags.containsKey(mapID))
			mapFlags.get(mapID).cantake = 1;
		else
			this.mapFlags.put(mapID, new SBean.MapFlagInfo(mapID, gs.getSectManager().getSectFlagOverview(0), 0, 0, 0, (byte) 1));
	}
	
	public synchronized void syncAllFlags(int sessionId)
	{
		gs.getRPCManager().sendStrPacket(sessionId, new SBean.sync_big_map_flag_info_res(Stream.clone(mapFlags)));
	}
	
	public void mapStartInitMapFlag(int sessionid)
	{
		gs.getRPCManager().notifyMapInitWorldMapFlag(sessionid, Stream.clone(mapFlags));
	}
	
	public interface EmergencyIntCallBack
	{
		void callBack(int intPara);
	}
	
	public synchronized void getSectFlagNum(int sectId, EmergencyIntCallBack callback)
	{
		callback.callBack(getSectFlagNum(sectId));
	}
	
	public synchronized int getSectFlagNum(int sectId)
	{
		return mapFlags.values().stream().filter(item -> item.curSect.sectId == sectId).collect(Collectors.toList()).size();
	}
	
	public SBean.MapFlagSectOverView getFlagSect(int mapId)
	{
		sectRefresh();
		return mapFlags.get(mapId) == null ? new SBean.MapFlagSectOverView(0, "", 0) : mapFlags.get(mapId).curSect;
	}

	void sectRefresh()
	{
		for (SBean.MapFlagInfo flag:mapFlags.values())
		{
			SBean.MapFlagSectOverView oldOverView = flag.curSect;
			flag.curSect = gs.getSectManager().getSectFlagOverview(flag.curSect.sectId);
			if(flag.curSect.sectId!=oldOverView.sectId||!flag.curSect.sectName.equals(oldOverView.sectName)||flag.curSect.sectIcon!=oldOverView.sectIcon)
				gs.getMapService().syncSyncMapFlagInfo(flag.mapId, flag.curSect);
		}
	}
	
	int lastSaveTime;
	GameServer gs;
	Map<Integer, SBean.MapFlagInfo> mapFlags = new TreeMap<>();
	int checkRandom;

}
