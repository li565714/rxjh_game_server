package i3k.gmap;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameTime;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RobotHero extends MapRole
{
	private static final int AUTO_REVIVE_TIME = 5;
	
	private int deadTime;
	private int second;
	
	int standByTime;
	
	RobotHero(MapServer ms)
	{
		super(ms, true);
	}
	
	RobotHero createRobotRole(SBean.FightRole robot, Map<Integer, SBean.FightPet> curFightPets, int mapID, SBean.SpawnPointCFGS spawnPointCFGS)
	{
		this.fromDB(new SBean.Location(spawnPointCFGS.position, new SBean.Vector3F(1, 0, 0)));
		this.armorMgr = new ArmorManager(this.maxArmorValue);
		this.setCommonProps(robot, curFightPets, false);
		this.birthPosition = new GVector3(spawnPointCFGS.position);
		this.maxTraceRange = 1500;
		
		this.curMapID = mapID;
		this.pkInfo = new SBean.PKInfo(GameData.ATTACK_MODE_PEACE, 0);
		this.robot = true;
		
		this.active = true;
		this.curAttackSeq = 0;
		this.setCurSkillID();
		
		this.standByTime = GameTime.getTime() + 60;
		return this;
	}
	
	boolean onTimer(int timeTick, long logicTime)
	{
		super.onTimer(timeTick, logicTime);
		onSecondTask(timeTick);
		
		return timeTick > this.standByTime;
	}
	
	void onSecondTask(int timeTick)
	{
		if (timeTick > this.second)
		{
			onCheckAutoRevive(timeTick);
			this.second = timeTick;
		}
	}
	
	void onCheckAutoRevive(int timeTick)
	{
		if(!this.isDead())
			return;
		
		if(timeTick < this.deadTime + AUTO_REVIVE_TIME)
			return;
		
		this.onRoleRevive(true);
	}
	
	boolean canAddMarriageBuff()
	{
		return this.active && !this.isDead() && !this.checkState(Behavior.EBREVIVE);
	}
	
	void notifyEnterInfo(EnterInfo enterInfo)
	{
		if (!enterInfo.roles.isEmpty())
			this.enterSetNearByRoles(enterInfo.roles);
	}
	
	void notifyLeaveInfo(LeaveInfo leaveInfo, int destory)
	{
		if(!leaveInfo.roles.isEmpty())
			this.leaveSetNearByRoles(leaveInfo.roles);
	}
	
	void checkMovePosition(int timeTick)
	{
		
	}
	
	void lossDurabilityOnUseSkill()
	{
		
	}
	
	void onCreate()
	{
		int gridX = this.getCurMapGrid().getGridX();
		int gridZ = this.getCurMapGrid().getGridZ();
		EnterInfo enterInfo = this.curMap.getEntitiesNearBy(gridX, gridZ, this);
		
		if(!enterInfo.roles.isEmpty())
		{
			Set<Integer> rids = new HashSet<>();
			for(int rid: enterInfo.roles.keySet())
			{
				if(rid != this.id)
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
	
	void onDeadHandle(int attackerType, int attackerID)
	{
		super.onDeadHandle(attackerType, attackerID);
		this.deadTime = GameTime.getTime();
	}
}
