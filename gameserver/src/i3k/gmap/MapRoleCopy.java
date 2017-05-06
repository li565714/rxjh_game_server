package i3k.gmap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

public class MapRoleCopy extends MapRole
{
	private long activeTime = 0;
	private long toReviveTime;
	
	private Map<Integer, Integer> area2index = new HashMap<>();
	private Map<Integer, Integer> index2area = new HashMap<>();
	SpawnAreaInfo curAreaInfo;			//正在前往的刷怪区域
	SpawnAreaInfo tarAreaInfo;			//目标刷怪区域
	
	int curPosIndex;
	int tarPosIndex;
	GVector3 curLinePos;
	GVector3 nextLinePos;
	float lineDistance;
	float dtY;
	
	class SpawnAreaInfo
	{
		int index;
		int areaID;
		
		SpawnAreaInfo()
		{
			
		}
		
		void reset()
		{
			this.index = 0;
			this.areaID = 0;
		}
		
		void set(int index, int areaID)
		{
			this.index = index;
			this.areaID = areaID;
		}
		
		void set(SpawnAreaInfo info)
		{
			this.index = info.index;
			this.areaID = info.areaID;
		}
		
		int getIndex()
		{
			return index;
		}
		
		int getAreaID()
		{
			return areaID;
		}
	}
	
	MapRoleCopy(MapServer ms, long now)
	{
		super(ms, false);
		this.robot = true;
		this.active = false;
		this.activeTime = now + 2500;
		
		this.maxTraceRange = 15000;
		this.curAreaInfo = new SpawnAreaInfo();
		this.tarAreaInfo = new SpawnAreaInfo();
	}
	
	MapRoleCopy fromDBWitoutLock(int mapId, SBean.Location location, SBean.FightRole fightRole, Map<Integer, SBean.FightPet> curFightPets, List<Integer> petSeqs, SBean.PetHost pethost, 
			int hp, int sp, int armorVal, Map<Integer, SBean.DBBuff> dbBuffs, SBean.PKInfo pkInfo, int curRideHorse, SBean.DBAlterState alterState, SBean.MulRoleInfo mulRoleInfo, int dayFailedStreak, int vipLevel, int curWizardPet, byte canTakeDrop)
	{
		super.fromDBWitoutLock(mapId, location, fightRole, curFightPets, petSeqs, pethost, hp, sp, armorVal, dbBuffs, pkInfo, curRideHorse, alterState, mulRoleInfo, dayFailedStreak, vipLevel, curWizardPet, canTakeDrop);
		this.curLinePos = new GVector3(location.position);
		
		SBean.MapClusterCFGS clusterCfg = GameData.getInstance().getMapClusterCFGS(mapId);
		if(clusterCfg != null)
		{
			for(int i = 1; i <= clusterCfg.spawnAreas.size(); i++)
			{
				this.index2area.put(i, clusterCfg.spawnAreas.get(i - 1));
				this.area2index.put(clusterCfg.spawnAreas.get(i - 1), i);
			}
		}
		return this;
	}
	
	boolean onTimer(int timeTick, long logicTime)
	{
		if(this.active)
		{
			int mapAreaID = Math.abs(this.curMap.getCurOrNextSpawnArea());
			if(mapAreaID != tarAreaInfo.getAreaID() && !this.isDead() && !this.checkState(Behavior.EBREVIVE))
				updateSpawnArea(mapAreaID);
			
			super.onTimer(timeTick, logicTime);
			if(this.isDead() && this.curMap.getMapType() != GameData.MAP_TYPE_MAPCOPY_SUPERARENA)
				this.tryRevive(logicTime);
		}
		else
		{
			tryActive(logicTime);
		}
		
		return false;
	}
	
	private void updateSpawnArea(int mapAreaID)
	{
		int index = this.area2index.getOrDefault(mapAreaID, 0);
		if (this.addState(Behavior.EBMOVE))
		{
			this.removeState(Behavior.EBMOVE);
			tarAreaInfo.set(index, mapAreaID);
			tryMoveToNextSpawn();
		}
	}
	
	private boolean tryMoveToNextSpawn()
	{
		int nextAreaID = this.index2area.getOrDefault(this.curAreaInfo.getIndex() + 1, 0);
		if(nextAreaID == 0)
		{
			this.curAreaInfo.set(this.tarAreaInfo);
			return false;
		}
		
		SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(nextAreaID);
		if(areaCfg == null || areaCfg.spawnPoint.isEmpty())
		{
			this.curAreaInfo.set(this.tarAreaInfo);
			return false;
		}
		
		SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(areaCfg.spawnPoint.get(0));
		if(pointCfg == null)
		{
			this.curAreaInfo.set(this.tarAreaInfo);
			return false;
		}
		
		this.setNextArea();
		if(areaCfg.paths.isEmpty())
		{
//			ms.getLogger().debug("curAreaIndex " + this.curAreaIndex + " tarAreaIndex " + this.tarAreaInfo.getIndex());
			if(this.curAreaInfo.getIndex() < this.tarAreaInfo.getIndex())
				return tryMoveToNextSpawn();
			
			this.curAreaInfo.set(this.tarAreaInfo);
			return false;
		}
		
		this.breakSkill();
		int index = 1;
		this.tarPosIndex = areaCfg.paths.size();
		moveToNextPosImpl(areaCfg, index);
		this.birthPosition.fromVector3(pointCfg.position);
		return true;
	}
	
	private void setNextArea()
	{
		int index = this.curAreaInfo.getIndex() + 1;
		int areaID = this.index2area.getOrDefault(index, 0);
		this.curAreaInfo.set(index, areaID);
	}
	
	public void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		if(this.curPosIndex != this.tarPosIndex)
		{
			if(tryMoveToNextPos())
				return;
			
			this.curPosIndex = this.tarPosIndex;
		}
		
		if(this.curAreaInfo.getIndex() != this.tarAreaInfo.getIndex())
		{
			if(this.curAreaInfo.getIndex() < this.tarAreaInfo.getIndex())
			{
				if(tryMoveToNextSpawn())
					return;
			}
			
			this.curAreaInfo.set(this.tarAreaInfo);
		}
		
		this.setClientLastPos(this.curMap.fixPos(position, this));
		this.setNewPosition(position);
		this.onStopMoveImpl(position, timeTick, true);
	}
	
	private boolean tryMoveToNextPos()
	{
		int index = this.curPosIndex + 1;
		SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(this.curAreaInfo.getAreaID());
		if(areaCfg != null && !areaCfg.spawnPoint.isEmpty() && areaCfg.paths.size() >= index && index > 0)
		{
			if (this.addState(Behavior.EBMOVE))	  //第一次移动直接广播
			{
				this.curLinePos.reset(this.nextLinePos);
				moveToNextPosImpl(areaCfg, index);
			}
			return true;
		}
		return false;
	}
	
	private void moveToNextPosImpl(SBean.SpawnAreaCFGS areaCfg, int index)
	{
		this.nextLinePos = new GVector3(areaCfg.paths.get(index - 1));
		this.nextLinePos.x += GameRandom.getRandInt(-150, 150);
		this.nextLinePos.z += GameRandom.getRandInt(-150, 150);
		this.curPosIndex = index;
		this.updateLine();
		
		this.removeState(Behavior.EBMOVE);
		this.addState(Behavior.EBRETREAT);
		this.moveTargetPos.reset(this.nextLinePos);
		this.moveTarget = null;
		this.setCurRotation(this.moveTargetPos.diffence2D(this.getCurPosition()).normalize());
		this.startMove(ms.getMapManager().getMapLogicTime());
	}
	
	private void tryActive(long logicTime)
	{
		if(logicTime < this.activeTime || this.curMap == null)
			return;
		
		this.active = true;
		int gridX = this.getCurMapGrid().getGridX();
		int gridZ = this.getCurMapGrid().getGridZ();
		EnterInfo enterInfo = this.curMap.getEntitiesNearBy(gridX, gridZ, this);
		if(!enterInfo.roles.isEmpty())
		{
			Set<Integer> rids = new HashSet<>();
			for(int rid: enterInfo.roles.keySet())
			{
				if(rid > 0 && rid != this.id)
				{
					MapRole r = this.curMap.getRole(rid);
					if(r != null && !r.robot)
						rids.add(rid);
				}
			}
			
			if(!rids.isEmpty())
			{
				this.filterAndAddNearByRoleIDs(rids, false);
				this.onSelfEnterNearBy(rids);
			}
		}
	}
	
	private void tryRevive(long logicTime)
	{
		if(logicTime < this.toReviveTime)
			return;
		
		this.randRevive();
	}
	
	private void randRevive()
	{
		boolean full = GameRandom.getRandInt(0, 2) == 1;
		if(!full)
		{
			this.curAreaInfo.reset();
			this.tarAreaInfo.reset();
			SBean.MapClusterCFGS mcCfg = GameData.getInstance().getMapClusterCFGS(this.curMap.getMapID());
			if(mcCfg != null)
				this.setNewPosition(new GVector3(mcCfg.revivePos));
		}
		this.onRoleRevive(full);
	}
	
	void onAutoFight(long logicTime)
	{
		if(this.isDead() || this.isInProtectTime())
			return;

		if(this.checkState(Behavior.EBPREPAREFIGHT))
			return;

		onAutoFightImpl(logicTime);
	}
	
	boolean isInCheckRange(float distance)
	{
		return true;
//		return this.maxCheckRange <= 0 || distance < this.maxCheckRange || (this.owner != null && this.curMap.isFightMap());
	}
	
//	boolean checkOutRange()
//	{
//		if(this.checkState(Behavior.EBRETREAT))
//			return false;
//		
//		return super.checkOutRange();
//	}
	
	void moveToNearest(long logicTime)
	{
		if(checkOutRange())
			return;
		
		BaseRole entity = this.getForceTarget();
		if (entity == null || !this.checkTargetValid(entity))
			entity = this.getRoleInCheckRange();
		
		if (entity != null && !entity.isDead() && !entity.isInProtectTime())
		{
			float range = this.attackRange + this.getRadius() + entity.getRadius();
			float distance = entity.getCurPosition().distance(this.getCurPosition());
			if ((distance - range) <= 5)
			{
				if(!this.checkState(Behavior.EBDISATTACK))
					this.attackNearest(entity, logicTime);
			}
			else
			{
				if (this.addState(Behavior.EBMOVE))	  //第一次移动直接广播
				{
					this.removeState(Behavior.EBMOVE);
					GVector3 dir = this.curPosition.diffence2D(entity.curPosition).normalize();
					this.moveTargetRealPos.reset(entity.curPosition);
					if (distance - range < range / 4.f)
						this.moveTargetPos = entity.curPosition.sum(dir.scale(range));
					else
						this.moveTargetPos = this.getRandomTargetPos(entity.curPosition, dir, range, GameData.getInstance().getPathAngel(range));
					
//					this.moveTargetPos.reset(entity.getCurPosition());
					this.moveTarget = entity;
					this.setCurRotation(this.moveTargetPos.diffence2D(this.getCurPosition()).normalize());

					this.startMove(ms.getMapManager().getMapLogicTime());
				}
			}
			
		}
	}
	
	public int setCurHP(int curHP)
	{
		if (this.isDead())
			return 0;

		if (curHP <= 0)
		{
			if(this.deadTrig())
			{
				SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
				ms.getRPCManager().sendStrPacket(this.id, new SBean.role_addstate(Behavior.EBGOTODEAD, timeTick));
			}
			curHP = 0;
		}
		else if (curHP > this.getMaxHP())
			curHP = this.getMaxHP();

		if (this.curHP != curHP)
		{
			this.curHP = curHP;
			ms.getRPCManager().syncRoleHP(this.getID(), this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
		}
		return this.curHP;
	}
	
	void notifyClientMaxHPUpdate()
	{
		boolean out = false;
		if (this.curHP > this.getMaxHP())
		{
			this.setCurHP(this.getMaxHP());
			out = true;
		}

		ms.getRPCManager().syncRoleHP(this.id, this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
		if(!this.isInPrivateMap())
		{
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			this.notifyUpdateMaxHp(rids);
			if(out)
				this.notifyUpdateHp(rids);
		}
	}
	
	void onDeadHandle(int attackerType, int attackerID)
	{
		super.onDeadHandle(attackerType, attackerID);
		this.toReviveTime = ms.getMapManager().getMapLogicTime() + GameRandom.getRandInt(2000, 5000);
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
	
	void notifySelfMove()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().nearbyRoleMove(rids, this.id, this.getLogicPosition(), this.moveSpeed, this.getCurRotation().toVector3F(), this.moveTargetPos.toVector3(), ms.getMapManager().getTimeTickDeep());
	}
}
