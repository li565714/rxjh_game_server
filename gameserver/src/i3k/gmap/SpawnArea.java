package i3k.gmap;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnArea
{

	SpawnArea(int id, BaseMap map)
	{
		this.spawnAreaID = id;
		this.curMap = map;
	}

	int getID()
	{
		return this.spawnAreaID;
	}

	SpawnArea createFromCfg()
	{
		SBean.SpawnAreaCFGS spawnAreaCfg = GameData.getInstance().getSpawnArea(this.spawnAreaID);
		if (spawnAreaCfg != null)
		{
			for (int pointID : spawnAreaCfg.spawnPoint)
			{
				SpawnPoint spawnPoint = new SpawnPoint(pointID, this);
				if (spawnPoint != null)
					this.addSpawnPoint(spawnPoint);
			}
			this.alterRadius = spawnAreaCfg.checkRadius;
		}
		this.delaySpawn = -1;
		return this;
	}

	void addSpawnPoint(SpawnPoint p)
	{
		areaSpawnPoints.put(p.getSpawnPointID(), p);
		this.curMap.addSpawnPoint(p);
		GVector3 pos = p.getPosition();
		if (pos.x < this.minx)
			this.minx = pos.x;
		if (pos.x > this.maxx)
			this.maxx = pos.x;
		if (pos.z < this.minz)
			this.minz = pos.z;
		if (pos.z > this.maxz)
			this.maxz = pos.z;
	}

	void onTimer(long timeMillis, int timeTick)
	{
		if (timeMillis - this.checkTime >= 500)
		{
			this.checkRoleInSpawArea();
			this.checkTime = timeMillis;
		}

		if (!this.isTri)
			return;

		if(this.delaySpawn > timeMillis)
			return;
		
		this.checkDelaySpawn(timeMillis, timeTick);
	}
	
	private void checkDelaySpawn(long timeMillis, int timeTick)
	{
		if (this.delaySpawn < 0)
		{
			for (SpawnPoint p : areaSpawnPoints.values())
			{
				p.onTimer(timeMillis, timeTick);
			}
		}
		else
		{
			for (SpawnPoint spawnPoint : areaSpawnPoints.values())
				this.createMonster(spawnPoint, 0, 0);

			this.delaySpawn = -1;
		}
	}
	
	boolean isEmpty()
	{
		if (inWorldMap)
			return false;

		for (SpawnPoint point : this.areaSpawnPoints.values())
		{
			if (point.hasMonster())
				return false;
		}

		return this.isTri && this.delaySpawn < 0;
	}

	void addMonsters(Map<Integer, Integer> progress)
	{
		SBean.SpawnAreaCFGS areaCfg = GameData.getInstance().getSpawnArea(this.spawnAreaID);
		if (areaCfg == null)
			return;

		for (Integer tid : areaCfg.spawnClose)
		{
			Trap t = this.curMap.getConfigTrap(tid);
			if (t != null)
				t.setTrapState(MapManager.ESTRAP_ACTIVE);
		}

		for (Integer tid : areaCfg.spawnOpen)
		{
			Trap t = this.curMap.getConfigTrap(tid);
			if (t != null)
				t.setTrapState(MapManager.ESTRAP_TRIG);
		}

		if (areaCfg.delaySpawnTime > 0)
		{
			this.delaySpawn = GameTime.getTimeMillis() + areaCfg.delaySpawnTime;
		}
		else
		{
			for (SpawnPoint spawnPoint : areaSpawnPoints.values())
			{
				this.createMonster(spawnPoint, 0, progress.getOrDefault(spawnPoint.spawnPointID, 0));
			}
		}

		this.isTri = true;
	}

	void createMonster(SpawnPoint spawnPoint, int spawnSeq, int isDeadNum)
	{
		SBean.SpawnPointCFGS pointCfgs = GameData.getInstance().getSpawnPoint(spawnPoint.getSpawnPointID());
		if (pointCfgs != null)
		{
			int count = pointCfgs.spawnNum.get(spawnSeq) - isDeadNum;
			if (count <= 0)
			{
				spawnPoint.curSpawnSeqAdd();
				return;
			}
			float unit = (float) (Math.PI * 2.f) / count;
			for (int i = 0; i < count; i++)
			{
				float angle = GameRandom.getRandFloat(unit * i, unit * i + unit * 0.8f);
				this.createOneMonster(spawnPoint, angle);
			}
		}
	}

	void createOneMonster(SpawnPoint spawnPoint, float angle)
	{
		SBean.SpawnPointCFGS pointCfgs = GameData.getInstance().getSpawnPoint(spawnPoint.getSpawnPointID());
		if (pointCfgs != null)
		{
			GVector3 spawnPosition = new GVector3().reset(spawnPoint.getPosition());
			if (pointCfgs.isRandom == 1)
			{
				float radius = GameRandom.getRandFloat(0, pointCfgs.randomRadius / 2.f) + pointCfgs.randomRadius / 2.f;
				spawnPosition.selfSum(new GVector3((float) (radius * Math.cos(angle)), 0.0f, (float) (radius * Math.sin(angle))));
			}
			GVector3 rotation = pointCfgs.rotationType == GameData.MONSTER_ROTATION_RANDOM ? GVector3.randomRotation() : new GVector3(pointCfgs.rotation);
				
			Monster monster = this.curMap.createMonster(pointCfgs.monsterID, spawnPosition, rotation, pointCfgs.rotationType != GameData.MONSTER_ROTATION_INTITAL_FIXED, -1, -1);
			if(monster != null)
				spawnPoint.addMonster(monster);
		}
	}

	void checkRoleInSpawArea()
	{
		if (this.isTri)
			return;
		
		int minGridX = this.curMap.calcGridCoordinateX((int) (this.minx - this.alterRadius));
		int maxGridX = this.curMap.calcGridCoordinateX((int) (this.maxx + this.alterRadius));
		int minGridZ = this.curMap.calcGridCoordinateZ((int) (this.minz - this.alterRadius));
		int maxGridZ = this.curMap.calcGridCoordinateZ((int) (this.maxz + this.alterRadius));

		for (MapRole role : this.curMap.getRoleByIndex(minGridX, maxGridX, minGridZ, maxGridZ))
		{
			GVector3 pos = role.getCurPosition();
			if (pos.x >= this.minx - this.alterRadius && pos.x <= this.maxx + this.alterRadius && pos.z >= this.minz - this.alterRadius && pos.z <= this.maxz + this.alterRadius)
			{
				this.addMonsters(new HashMap<>());
				break;
			}
		}
	}

	//	private MapServer ms;
	BaseMap curMap;
	private int spawnAreaID;
	private float alterRadius;

	private boolean isTri = false;
	private float minx = GameData.MAX_NUMBER;
	private float minz = GameData.MAX_NUMBER;
	private float maxx = -GameData.MAX_NUMBER;
	private float maxz = -GameData.MAX_NUMBER;
	private long checkTime;

	private ConcurrentHashMap<Integer, SpawnPoint> areaSpawnPoints = new ConcurrentHashMap<>();
	boolean inWorldMap = false;
	private long delaySpawn;
}
