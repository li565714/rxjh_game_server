package i3k.gmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;

public class Npc extends BaseRole
{
	Npc(int npcID, MapServer ms)
	{
		super(ms, true);
		this.configID = npcID;
	}

	public Npc createNew(GVector3 position)
	{
		this.id = ms.getMapManager().getNextNpcID().incrementAndGet();
		this.curPosition.reset(position);
		this.birthPosition.reset(position);

		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_NPC;
		return this;
	}

	void onTimer(int timeTick)
	{
		
	}

	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterBase> npcs = new ArrayList<>();
			npcs.add(this.getEnterBase());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_npcs(npcs));
		}
	}

	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> npcs = new ArrayList<>();
			npcs.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_npcs(npcs));
		}
	}
}
