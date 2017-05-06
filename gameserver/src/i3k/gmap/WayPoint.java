package i3k.gmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;

public class WayPoint extends BaseRole
{

	WayPoint(int wid, MapServer ms)
	{
		super(ms, true);
		this.configID = wid;
	}

	public WayPoint createNew(GVector3 position)
	{
		this.curPosition.reset(position);
		this.birthPosition.reset(position);
		this.curRotation.reset(GVector3.UNIT_X);
		this.id = this.configID;

		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_WAYPOINT;
		return this;
	}

	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterBase> wayPoints = new ArrayList<>();
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_waypoints(wayPoints));
		}
	}

	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> wayPoints = new ArrayList<>();
			wayPoints.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_waypoints(wayPoints));
		}
	}
}
