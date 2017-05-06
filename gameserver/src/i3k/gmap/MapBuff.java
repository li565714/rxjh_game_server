package i3k.gmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameTime;

public class MapBuff extends BaseRole
{

	MapBuff(int mapBuffID, MapServer ms)
	{
		super(ms, true);
		this.configID = mapBuffID;
	}

	MapBuff createNew(GVector3 position)
	{
		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_MAPBUFF;
		this.id = ms.getMapManager().getNextMapBuffID().incrementAndGet();
		this.curPosition.reset(position);
		this.birthPosition.reset(position);

		SBean.MapBuffCFGS cfg = GameData.getInstance().getMapBuffCFGS(this.getConfigID());
		if (cfg != null)
			this.rebirthCnt = cfg.rebirthCnt;

		return this;
	}

	private void checkReBirth(int timeTick)
	{
		if (this.rebirthCnt != 0 && this.disappear && timeTick > this.rebirthTime)
		{
			SBean.MapBuffCFGS cfg = GameData.getInstance().getMapBuffCFGS(this.getConfigID());
			if (cfg == null)
				return;

			if (this.rebirthCnt > 0)
				this.rebirthCnt--;

			this.disappear = false;
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			this.onSelfEnterNearBy(rids);
		}
	}

	private void onSecondTask(int timeTick)
	{
		if (timeTick > this.second)
		{
			this.checkReBirth(timeTick);
			this.second = timeTick;
		}
	}

	void onTimer(int timeTick)
	{
		this.onSecondTask(timeTick);
	}

	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterBase> mapbuffs = new ArrayList<>();
			mapbuffs.add(this.getEnterBase());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_mapbuffs(mapbuffs));
		}
	}
	
	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> mapBuffs = new ArrayList<>();
			mapBuffs.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_mapbuffs(mapBuffs));
		}
	}
	
	boolean isDead()
	{
		return this.disappear;
	}
	
	boolean onPickUpMapBuff(SBean.MapBuffCFGS mapBuffCfg, MapRole picker)
	{
		if (mapBuffCfg == null)
			return false;

		this.rebirthTime = GameTime.getTime() + mapBuffCfg.rebirthTime;
		this.disappear = true;

		if(!this.isInPrivateMap())
		{
			Set<Integer> rids = this.getRoleIDsNearBy(picker);
			this.onSelfLeaveNearBy(rids, 1);
		}
		
		if (this.rebirthCnt == 0)
			this.curMap.delMapBuff(this.id);
		
		return true;
	}

	private int rebirthTime;
	private int rebirthCnt;
	private int second;
	private boolean disappear = false;
}
