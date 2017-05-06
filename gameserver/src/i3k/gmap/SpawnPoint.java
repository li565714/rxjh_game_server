package i3k.gmap;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SpawnPoint
{
	SpawnPoint(int id, SpawnArea curArea)
	{
		this.spawnPointID = id;
		SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(id);
		if (pointCfg == null)
			return;

		GVector3 pos = new GVector3(pointCfg.position);
		this.spawnType = pointCfg.spawnType;
		this.spawnTimes = pointCfg.spawnTimes;
		this.spawnNums = pointCfg.spawnNum;

		if (this.spawnType == GameData.BYNUMBER)
		{
			this.totalCount = this.spawnTimes * this.spawnNums.get(0);
			this.keepCount = this.spawnNums.get(0);
		}
		else if (this.spawnType == GameData.BYTIME)
			this.checkSpawnTime = GameTime.getTimeMillis();

		this.spawnInterval = pointCfg.interval;
		this.position = pos;
		this.curArea = curArea;
	}

	void onTimer(long timeMillis, int timeTick)
	{
		if (this.curArea.curMap.isTimeOut())
			return;
		
		this.onCheck(timeMillis, timeTick);
	}

	void onCheck(long timeMillis, int timeTick)
	{
		if(timeTick > this.lastCheckTime)
		{
			this.checkByTime(timeMillis);
			this.checkByNumber(timeMillis);
			this.lastCheckTime = timeTick;
		}
	}
	
	private void checkByTime(long timeMillis)
	{
		if (this.curArea.curMap.isTimeOut())
			return;
		
		if (timeMillis - this.checkSpawnTime > this.spawnInterval)
		{
			if (this.spawnType != GameData.BYTIME)
				return;

			this.curSpawnSeq++;
			if (this.curSpawnSeq < this.spawnTimes || this.spawnTimes == -1)
			{
				//				int nextCount = pointCfg.spawnNum.get(this.curSpawnSeq);
				int nextCount = this.spawnNums.get(0);
				if (nextCount > 0)
				{
					//					this.curArea.createMonster(this, this.curSpawnSeq);
					this.curArea.createMonster(this, 0, 0);
				}
			}
			this.checkSpawnTime = timeMillis;
		}
	}

	private void checkByNumber(long timeMillis)
	{
		if (this.spawnType != GameData.BYNUMBER)
			return;

		if (this.totalCount == 0)
			return;

		int d = this.keepCount - this.monsters.size();
		if (d <= 0)
			return;

		if (this.checkSpawnTime == 0)
			this.checkSpawnTime = timeMillis;

		if (timeMillis - this.checkSpawnTime > this.spawnInterval)
		{
			if (this.totalCount > 0)
			{
				if (this.totalCount < d)
					d = this.totalCount;

				this.totalCount -= d;
			}

			for (int i = 0; i < d; i++)
			{
				this.curArea.createOneMonster(this, GameRandom.getRandFloat(0.f, (float) Math.PI * 2.f));
			}

			this.checkSpawnTime = 0;
		}
	}

	int getSpawnPointID()
	{
		return this.spawnPointID;
	}

	int getSpawnSeq()
	{
		return this.curSpawnSeq;
	}

	int getMonsterCount()
	{
		return this.monsters.size();
	}

	boolean hasMonster()
	{
		if(this.spawnType == GameData.BYNUMBER)
			return this.spawnTimes < 0 || this.monsters.size() > 0 || this.totalCount != 0;
		else
			return this.spawnTimes < 0 || this.curSpawnSeq < this.spawnTimes || this.monsters.size() > 0;
	}

	GVector3 getPosition()
	{
		return this.position;
	}

	void addMonster(Monster monster)
	{
		monsters.put(monster.getID(), monster);
		monster.setCurSpawnPoint(this.spawnPointID);
	}

	Monster delMonster(Monster monster)
	{
		this.onMonsterDead(monster);
		monster.setCurSpawnPoint(0);
		return monster;
	}

	public void curSpawnSeqAdd()
	{
		this.curSpawnSeq++;
	}
	
	void onMonsterDead(Monster monster)
	{
		monsters.remove(monster.getID());
		switch (this.spawnType)
		{
		case GameData.BYORDER:
			if (monsters.size() <= 0)
			{
				this.curSpawnSeq++;
				if (this.curSpawnSeq < this.spawnTimes)
				{
					int nextCount = this.spawnNums.get(this.curSpawnSeq);
					if (nextCount > 0)
					{
						this.curArea.createMonster(this, this.curSpawnSeq, 0);
					}
				}
			}
			break;
		case GameData.BYTIME:
			break;
		case GameData.BYNUMBER:
			//				int totalCount = pointCfg.spawnTimes * pointCfg.spawnNum.get(0);
			//				if(totalCount > this.curDeadMonster || pointCfg.spawnTimes == -1)
			//				{
			//					this.curArea.createOneMonster(this);
			//				}
			break;
		default:
			break;
		}
	}

	////
	SpawnArea curArea;
	int spawnPointID;
	GVector3 position;
	Map<Integer, Monster> monsters = new HashMap<>();

	////	
	private int curSpawnSeq;
	private int spawnTimes;
	private List<Integer> spawnNums;
	private long checkSpawnTime;
	private int spawnType;
	private int totalCount;
	private int keepCount;
	private int spawnInterval;
	private int lastCheckTime;
}
