package i3k.fight;

import i3k.SBean;
import i3k.gs.FightService;
import i3k.gs.GameData;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FightManager 
{
	FightManager(FightServer fs)
	{
		this.fs = fs;
	}
	
	FightManager init()
	{
		for(SBean.ForceWarCFGS cfg: GameData.getInstance().getAllForceWarCFGS())
		{
			switch (cfg.type)
			{
			case GameData.FORCEWAR_TYPE_BWTYPE:
				this.forcewars.put(cfg.type, new ForceWarBWType(this.fs, cfg.mapIDs));
				break;
			case GameData.FORCEWAR_TYPE_MESS:
				this.forcewars.put(cfg.type, new ForceWarMess(this.fs, cfg.mapIDs));
				break;
			default:
				break;
			}
		}
		
		return this;
	}
	
	void start()
	{	
		
	}
	
	void destroy()
	{
		
	}
	
	public int getNextChatRoomID()
	{
		return nextChatRoomID.incrementAndGet();
	}
	
	public String createChatRoomID()
	{
		return fs.getConfig().areaId + "_" + GameData.CHAT_ROOM_TYPE_GLOBAL + "_" + getNextChatRoomID();
	}
	
	void onTimer(int timeTick)
	{
		this.forcewars.values().forEach(f -> f.onTimer(timeTick));
	}
	
	public void clearRolesByServerID(int serverID)
	{
		this.forcewars.values().forEach(f -> f.clearRolesByServerID(serverID));
	}
	
	void roleJoin(SBean.ForceWarJoin member, int forcewarType, FightService.RoleJoinForceWarReqCallBack callback)
	{
		ForceWar fw = forcewars.get(forcewarType);
		if(fw == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FORCEWAR_TYPE_INVALID);
			return;
		}
		
		fw.roleJoin(member, callback);
	}
	
	void roleJoin(List<SBean.ForceWarJoin> members, int bwType, int forcewarType, FightService.TeamJoinForceWarCallBack callback)
	{
		ForceWar fw = forcewars.get(forcewarType);
		if(fw == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FORCEWAR_TYPE_INVALID);
			return;
		}
		
		fw.roleJoin(members, bwType, callback);
	}
	
	void roleQuit(int roleID, byte bwType, int forcewarType, FightService.RoleQuitForceWarCallBack callback)
	{
		ForceWar fw = forcewars.get(forcewarType);
		if(fw == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FORCEWAR_TYPE_INVALID);
			return;
		}
		
		fw.roleQuit(roleID, bwType, callback);
	}
	
	void roleQuit(int roleID, byte bwType, int cnt, int forcewarType, FightService.TeamQuitForceWarCallBack callback)
	{
		ForceWar fw = forcewars.get(forcewarType);
		if(fw == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FORCEWAR_TYPE_INVALID);
			return;
		}
		
		fw.roleQuit(roleID, bwType, cnt, callback);
	}
	
	
	FightServer fs;
	Map<Integer, ForceWar> forcewars = new HashMap<>();
	AtomicInteger nextChatRoomID = new AtomicInteger();
}
