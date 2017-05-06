package i3k.gmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;

public class Mineral extends BaseRole
{

	Mineral(int configID, MapServer ms)
	{
		super(ms, true);
		this.configID = configID;
	}

	public Mineral createNew(GVector3 position, long standTime)
	{
		this.id = ms.getMapManager().getNextMineralID().incrementAndGet();
		this.curPosition.reset(position);
		this.birthPosition.reset(position);

		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_MINERAL;
		SBean.MineralCFGS mineralCfg = GameData.getInstance().getMineralCFGS(this.configID);
		if (mineralCfg != null)
			this.mineralCount = mineralCfg.mineralCount;

		if(standTime < 0)
			this.standTime = standTime;
		else
			this.standTime = ms.getMapManager().getMapLogicTime() + standTime;
		return this;
	}

	boolean onTimer(int timeTick, long logicTime)
	{
		this.checkRefresh(logicTime);
		
		return this.standTime > 0 && logicTime > this.standTime;
	}

	private void checkRefresh(long logicTime)
	{
		if (this.mineralCount == 0 && logicTime >= this.refreshTime)
		{
			SBean.MineralCFGS mineralCfg = GameData.getInstance().getMineralCFGS(this.configID);
			if (mineralCfg != null)
			{
				this.mineralCount = mineralCfg.mineralCount;
				Set<Integer> rids = this.getRoleIDsNearBy(null);
				this.onSelfEnterNearBy(rids);
			}
		}
	}

	MineralInfo onMineralStart(int roleID)
	{
		MineralInfo info = null;
		if (this.mineralCount == -1 || this.mineralCount > 0)
		{
			SBean.MineralCFGS cfg = GameData.getInstance().getMineralCFGS(this.getConfigID());
			if (cfg != null)
			{
				info = new MineralInfo(roleID, ms.getMapManager().getMapLogicTime() + (long) cfg.mineralTime);
				this.mineralInfos.put(info.roleID, info);
			}
		}

		return info;
	}

	MineralInfo onMineralEnd(int roleID)
	{
		MineralInfo info = null;
		if (this.mineralCount > 0)
		{
			this.mineralCount--;
			info = this.mineralInfos.get(roleID);
		}
		else if (this.mineralCount == -1)
		{
			info = this.mineralInfos.get(roleID);
		}
		this.mineralInfos.remove(roleID);
		
		if (this.mineralCount == 0)
		{
			this.onMineralBreakByNone();
			this.mineralInfos.clear();
			
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			SBean.MineralCFGS mineralCfg = GameData.getInstance().getMineralCFGS(this.configID);
			if (mineralCfg != null && mineralCfg.refreshInterval > 0)
				this.refreshTime = ms.getMapManager().getMapLogicTime() + (long) mineralCfg.refreshInterval;
			else
				this.curMap.delMineral(this.getID());

			//¿óÏûÊ§
			if (this.isInPrivateMap())
			{
				List<Integer> minerals = new ArrayList<>();
				minerals.add(this.getID());
				ms.getRPCManager().sendStrPacket(roleID, new SBean.nearby_leave_minerals(minerals));
//				ms.getRPCManager().sendStrPacket(roleID, new SBean.nearby_leave_mineral(this.getID()));
			}
			else
			{
				this.onSelfLeaveNearBy(rids, mineralCfg.refreshInterval <= 0 ? 1 : 0);
			}
		}
		
		return info;
	}
	
	boolean checkTimesEnough()
	{
		return true;
	}
	
	int getState()
	{
		return 1;
	}
	
	void onStateNone()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_mineral_updatestate(this.getID(), getState()));
		onMineralBreakByNone();
	}
	
	void onMineralBreakByNone()
	{
		for (MineralInfo f : this.mineralInfos.values())
		{
			MapRole role = this.curMap.getRole(f.roleID);
			if (role != null)
				role.onMineralBreakByNone();
		}
	}
	
	MineralInfo onMineralBreak(int roleID)
	{
		return this.mineralInfos.remove(roleID);
	}

	SBean.EnterMineral getEnterMineral()
	{
		return new SBean.EnterMineral(this.getID(), this.getConfigID(),  new SBean.Location(this.getLogicPosition(), this.curRotation.toVector3F()), getState());
	}

	void onCreate()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfEnterNearBy(rids);
	}
	
	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterMineral> minerals =  new ArrayList<>();
			minerals.add(this.getEnterMineral());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_minerals(minerals));
		}
	}

	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> minerals = new ArrayList<>();
			minerals.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_minerals(minerals));
//			ms.getLogger().debug("@@@@@ mineral " + this.id + " , " + this.configID + " leaves rids " + rids);
		}
	}
	
	//
	int mineralCount;
	Map<Integer, MineralInfo> mineralInfos = new TreeMap<>();
	private long refreshTime;
	long standTime;
	int worldMineralID;

	//
	public static class MineralInfo
	{
		public MineralInfo(int roleID, long endTime)
		{
			this.roleID = roleID;
			this.endTime = endTime;
		}

		int roleID;
		long endTime;
	}
}
