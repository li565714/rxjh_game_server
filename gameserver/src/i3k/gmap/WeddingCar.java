package i3k.gmap;

import i3k.SBean;
import i3k.gmap.BaseMap.WorldMap;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeddingCar extends BaseRole
{
	int curIndex;
	int lineID;
	GVector3 curLinePos;
	GVector3 nextLinePos;
	float lineDistance;
	float dtY;
	
	final SBean.RoleOverview man;
	final SBean.RoleOverview woman;
	
	WeddingCar(MapServer ms, int lineID, int carID, SBean.RoleOverview man, SBean.RoleOverview woman)
	{
		super(ms, true);
		this.configID = carID;
		
		this.lineID = lineID;
		this.curIndex = 0;
		this.man = man;
		this.woman = woman;
		this.speed = GameData.getInstance().getMarriageBaseCFGS().carMoveSpeed;
	}

	WeddingCar createNew(SBean.Vector3 curPos)
	{
		this.id = ms.getMapManager().getNextWeddingCarID();
		this.curPosition = new GVector3(curPos);
		this.curRotation.reset(GVector3.UNIT_X);
		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_WEEDINGCAR;
		
		this.curLinePos = new GVector3(curPos);
		return this;
	}
	
	void onTimer(int timeTick, long logicTime)
	{
		if(!this.isMoving())
		{
			this.tryStartMove();
		}
		else
		{
			this.updateMovePosition(logicTime);
		}
	}
	
	void onCreateHandle()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		rids.remove(this.man.id);
		rids.remove(this.woman.id);
		
		this.onSelfEnterNearBy(rids);
	}
	
	void onDeadHandle(Set<Integer> rids)
	{
		MapRole roleMan = this.curMap.getRole(this.man.id);
		if(roleMan != null)
		{
			roleMan.onWeddingCarDead(this);
		}
		
		MapRole roleWoman = this.curMap.getRole(this.woman.id);
		if(roleWoman != null)
		{
			roleWoman.onWeddingCarDead(this);
		}
		
		rids.remove(this.man.id);
		rids.remove(this.woman.id);
		if(!rids.isEmpty())
			this.onSelfLeaveNearBy(rids, 1);
	}
	
	SBean.EnterWeddingCar getEnterWeddingCar()
	{
		return new SBean.EnterWeddingCar(this.getID(), 
										 this.getConfigID(), 
										 new SBean.Location(this.getLogicPosition(), this.curRotation.toVector3F()), 
										 this.man.id, 
										 this.woman.id,
										 this.man.name, 
										 this.woman.name); 
	}
	
	boolean setNewPosition(GVector3 newPos)
	{
		MapRole roleMan = this.curMap.getRole(man.id);
		if(roleMan != null)
			roleMan.setNewPosition(newPos);
		
		MapRole roleWoman = this.curMap.getRole(woman.id);
		if(roleWoman != null)
			roleWoman.setNewPosition(newPos);
		
		return super.setNewPosition(newPos);
	}
	
	void changePosition(int curGridX, int curGridZ, int newGridX, int newGridZ, int destory)
	{
		Set<Integer> enterRids = this.curMap.getSelfEnterRoleIDsNearby(curGridX, curGridZ, newGridX, newGridZ, this.owner, this.owner != null);
		enterRids.remove(this.man.id);
		enterRids.remove(this.woman.id);
		this.onSelfEnterNearBy(enterRids);
		Set<Integer> leaveRids = this.curMap.getSelfLeaveRoleIDsNearby(curGridX, curGridZ, newGridX, newGridZ, this.owner, this.owner != null);
		leaveRids.remove(this.man.id);
		leaveRids.remove(this.woman.id);
		this.onSelfLeaveNearBy(leaveRids, destory);
	}
	
	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterWeddingCar> cars = new ArrayList<>();
			cars.add(this.getEnterWeddingCar());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_weddingcars(cars));
			
//			ms.getLogger().debug("@@@@@notify rids " + rids + " nearby_enter_weddingcars");
		}
	}

	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> cars = new ArrayList<>();
			cars.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_weddingcars(cars));
			
//			ms.getLogger().debug("@@@@@notify rids " + rids + " nearby_leave_weddingcars");
		}
	}
	
	void tryStartMove()
	{
		if(this.lineID <= 0 || this.curIndex < 0)
			return;
		
		SBean.Vector3 nextPos = GameData.getInstance().getMarriageLinePos(this.lineID, this.curIndex + 1);
		if(nextPos == null)
		{
			this.curIndex = -1;
			return;
		}

		GVector3 t = new GVector3(nextPos);
		GVector3 rotation = t.diffence2D(this.curPosition).normalize();
		if (!super.onMoveMent(this.speed, rotation.toVector3F(), this.curPosition, t, ms.getMapManager().getTimeTickDeep()))
			return;
		
		this.nextLinePos = new GVector3(nextPos);
		this.updateLine();
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_weddingcar(this.getID(), this.getLogicPosition(), this.moveSpeed, rotation.toVector3F(), ms.getMapManager().getTimeTickDeep()));
		
		tryRefreshGiftBox(this.getCurPosition());
//		ms.getLogger().debug("@@@@@notify rids " + rids + " nearby_move_weddingcar curIndex " + this.curIndex + " moveSpeed " + this.moveSpeed + " distance " + this.curPosition.distance(this.moveTargetPos) + " need " + (this.curPosition.distance(this.moveTargetPos)/this.moveSpeed));
	}
	
	void tryRefreshGiftBox(GVector3 position)
	{
		SBean.MarriageCarCFGS carCfg = GameData.getInstance().getMarriageCarCFGS(this.getConfigID());
		if(carCfg == null || carCfg.giftBoxID == 0 || carCfg.giftBoxCnt == 0 || !carCfg.refreshPonits.contains(this.curIndex + 1))
			return;

		int mineralID = carCfg.giftBoxID;
		int count = carCfg.giftBoxCnt;
		int r = carCfg.radius;
		
		float unit = (float) (Math.PI * 2.f) / count;
		for (int i = 0; i < count; i++)
		{
			float angle = GameRandom.getRandFloat(unit * i, unit * i + unit * 0.8f);
			GVector3 mPos = new GVector3(r * (float) Math.cos(angle), 0, r * (float)Math.sin(angle)).selfSum(position);
			Mineral mineral = new Mineral(mineralID, this.ms).createNew(mPos, carCfg.standByTime > 0 ? carCfg.standByTime * 1000L: -1);
			mineral.curRotation.reset(GVector3.UNIT_X);
			this.curMap.addMineral(mineral);
			mineral.onCreate();
		}
	}
	
	public void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.curIndex++;
		this.tryRefreshGiftBox(position);
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		SBean.Vector3 nextPos = GameData.getInstance().getMarriageLinePos(this.lineID, this.curIndex + 1);
		if(nextPos == null)
		{
			this.curIndex = -1;
			if(this.isMoving())
			{
				this.moveSpeed = 0;
				this.removeState(Behavior.EBMOVE);
				if(!rids.isEmpty())
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_stopmove_weddingcar(this.getID(), this.getLogicPosition(), ms.getMapManager().getTimeTickDeep()));
				
//				ms.getLogger().debug("@@@@@notify rids " + rids + " nearby_stopmove_weddingcar curIndex " + this.curIndex + " " + this.getCurPosition());
				this.onDeadHandle(new HashSet<>(rids));
				if(this.curMap instanceof WorldMap)
				{
					WorldMap wm = WorldMap.class.cast(this.curMap);
					wm.delWeddingCarToRemoveCache(this.getID());
				}
				ms.getRPCManager().notifyGSRoleMarriageParadeEnd(this.man.id, this.woman.id);
			}
		}
		else
		{
			this.curLinePos.reset(this.nextLinePos);
			this.nextLinePos = new GVector3(nextPos);
			this.updateLine();
			
			GVector3 t = new GVector3(nextPos);
			GVector3 rotation = t.diffence2D(this.curPosition).normalize();
			super.onMoveMent(this.speed, rotation.toVector3F(), this.curPosition, t, ms.getMapManager().getTimeTickDeep());
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_weddingcar(this.getID(), this.getLogicPosition(), this.moveSpeed, rotation.toVector3F(), ms.getMapManager().getTimeTickDeep()));
			
//			ms.getLogger().debug("@@@@@notify rids " + rids + " nearby_move_weddingcar curIndex " + this.curIndex + "curPos " + this.curPosition + " next pos " + GameData.toString(nextPos));
//			ms.getLogger().debug("@@@@@notify rids " + rids + " nearby_move_weddingcar curIndex " + this.curIndex + " moveSpeed " + this.moveSpeed + " distance " + this.curPosition.distance(this.moveTargetPos) + " need " + (this.curPosition.distance(this.moveTargetPos)/this.moveSpeed));
		}
	}
	
	void notifySelfMove()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_move_weddingcar(this.getID(), this.getLogicPosition(), this.moveSpeed, this.getCurRotation().toVector3F(), ms.getMapManager().getTimeTickDeep()));
		
//		ms.getLogger().debug("####notify rids " + rids + " nearby_move_weddingcar " + this.curPosition + " targetPos " + this.moveTargetPos);
	}
	
	boolean changeMapGrid(MapGrid grid)
	{
		if (grid != this.curMapGrid)
		{
			this.curMapGrid.delWeddingCar(this);
			grid.addWeddingCar(this);
			return true;
		}
		return false;
	}
	
	public void setCurPosition(GVector3 curPosition)
	{
		super.setCurPosition(curPosition);
		if(this.curLinePos != null && this.nextLinePos != null && Math.abs(this.dtY) > 10)
		{
			float dist = this.curPosition.distance(this.curLinePos);
			if(dist < 10 || this.lineDistance < 10)
				return;
			
			this.curPosition.y = this.curLinePos.y + dist / lineDistance * this.dtY;
			if(this.dtY > 0)
			{
				if(this.curPosition.y < this.curLinePos.y)
					this.curPosition.y = this.curLinePos.y;
				
				if(this.curPosition.y > this.nextLinePos.y)
					this.curPosition.y = this.nextLinePos.y;
			}
			else
			{
				if(this.curPosition.y > this.curLinePos.y)
					this.curPosition.y = this.curLinePos.y;
				
				if(this.curPosition.y < this.nextLinePos.y)
					this.curPosition.y = this.nextLinePos.y;
			}
		}
	}
	
	private void updateLine()
	{
		if(this.curLinePos != null && this.nextLinePos != null)
		{
			this.lineDistance = this.nextLinePos.distance(this.curLinePos);
			this.dtY = this.nextLinePos.y - this.curLinePos.y;
		}
	}
	
	Set<Integer> getRoleIDsNearBy(MapRole role)
	{
		Set<Integer> rids = super.getRoleIDsNearBy(role);
		MapRole roleMan = this.curMap.getRole(man.id);
		if(roleMan != null && roleMan.active)
			rids.add(man.id);
		
		MapRole roleWoman = this.curMap.getRole(woman.id);
		if(roleWoman != null && roleWoman.active)
			rids.add(woman.id);
		
		return rids;
	}
}
