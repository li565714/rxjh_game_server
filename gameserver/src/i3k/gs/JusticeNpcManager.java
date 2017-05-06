package i3k.gs;

import i3k.SBean;
import i3k.util.GameRandom;

public class JusticeNpcManager
{
	public JusticeNpcManager(GameServer gs)
	{
		this.gs = gs;
	}

	public synchronized void onTimer(int timeTick)
	{
		if (!GameData.getInstance().isInJusticeDay())
			return;
		if (curNpcIndex != -1 && timeTick < curNpcEndTime)
			return;
		if (curNpcIndex != -1)
		{
			npcLeave();
			return;
		}
		int nextTime = GameData.getInstance().getNextJusticeNpcEndTime(timeTick);
		if (nextTime > 0)
		{
			npcShow(nextTime);
		}
	}
	
	public void mapStartInitJusticeNpc(int sessionid)
	{
		if (curNpcIndex >= 0)
			gs.getRPCManager().notifyMapJusticeNpcShow(sessionid, curNpcIndex);
	}

	private synchronized void npcShow(int nextTime)
	{
		curNpcIndex = GameRandom.getRandom().nextInt(GameData.getInstance().getJusticeNpcPointSize());
		curNpcEndTime = nextTime;
		int mapId = GameData.getInstance().getJusticeNpcPoint(curNpcIndex).mapID;
		gs.getMapService().syncJusticeNpcShow(mapId, curNpcIndex);
		gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_JUSTICE_NPC, GameData.getInstance().getJusticeMapCFGS().npcId + "|" + mapId);
	}
	
	private synchronized void npcLeave()
	{
		gs.getMapService().syncJusticeNpcLeave(GameData.getInstance().getJusticeNpcPoint(curNpcIndex).mapID, curNpcIndex);
		curNpcIndex = -1;
		curNpcEndTime = 0;
	}
	
	public int getCurIndex()
	{
		return curNpcIndex;
	}
	
	public SBean.MapLocation getCurLocation()
	{
		return GameData.getInstance().getJusticeNpcPoint(curNpcIndex);
	}
	
	GameServer gs;
	int curNpcIndex = -1;
	int curNpcEndTime = 0;
}
