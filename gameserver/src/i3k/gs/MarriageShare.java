package i3k.gs;

import i3k.DBMarriageShare;
import i3k.SBean;
import i3k.SBean.GameItem;
import i3k.gs.RPCManager.GSNetStatMBean;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.Map;

import ket.util.Stream;


public class MarriageShare
{
	public MarriageShare(int id, int grade, Role role1, Role role2)
	{
		int now = GameTime.getTime();
		SBean.MarriageGradeCFGS gradecfgs = GameData.getInstance().getMarriageGradeCFGS(grade);
		this.randCheckTime = GameRandom.getRandom().nextInt(MAX_CHECK_TIME);
		this.id = id;
		this.collection = new ItemCellCollection(gradecfgs.publicWarehouseSize, 0);
		this.lastUseTime = now;
		this.marriageType = grade;
		this.marriageWeddingEndTime = 0;
		this.marriageExp = 0;
		this.marriageLevel = 1;
		this.marriageSkill = GameData.getInstance().initMarriageSkill(grade);
		this.marriageTime = GameTime.getTime();
		this.manId = role1.gender == GameData.ROLE_GENDER_MAN ? role1.id : role2.id;
		this.ladyId = role1.gender == GameData.ROLE_GENDER_WOMAN ? role1.id : role2.id;
		this.task = createTask();
		this.marriageStep = 1;
		this.lastDayRefresh = 0;
		this.minLevel = role1.level < role2.level ?  role1.level : role2.level;
	}
	
	public MarriageShare(DBMarriageShare warehouse, int selfLvl, int parnterLvl)
	{
		this.randCheckTime = GameRandom.getRandom().nextInt(MAX_CHECK_TIME);
		this.id = warehouse.id;
		this.collection = new ItemCellCollection(warehouse.warehouse);
		this.lastUseTime = GameTime.getTime();
		this.marriageType = warehouse.marriageType;
		this.marriageExp = warehouse.marriageExp;
		this.marriageLevel = warehouse.marriageLevel;
		this.marriageSkill = warehouse.marriageSkill;
		this.marriageTime = warehouse.marriageTime;
		this.manId = warehouse.manId;
		this.ladyId = warehouse.ladyId;
		this.task = warehouse.task;
		this.marriageStep = warehouse.marriageStep == 1 ? 1 : 0;
		this.marriageWeddingEndTime = 0;
		this.lastDayRefresh = warehouse.lastDayRefresh;
		this.updateMinLevel(selfLvl);
		this.updateMinLevel(parnterLvl);
	}
	
	public synchronized DBMarriageShare toDB()
	{
		return new DBMarriageShare(this.id, this.collection.toDB(), this.marriageType, this.marriageExp, this.marriageLevel, Stream.clone(this.marriageSkill), this.marriageTime, this.manId, this.ladyId, this.task.kdClone(), this.lastDayRefresh, this.marriageStep);
	}

	public synchronized int getId()
	{
		return this.id;
	}
	
	public synchronized SBean.DBItemCells getWarehouseShare()
	{
		return collection.toDB();
	}
	
	public synchronized void updateUseTime(int timeTick)
	{
		this.lastUseTime = timeTick;
	}
	
	public synchronized boolean onTimer(int timeTick)
	{
		if (timeTick % MAX_CHECK_TIME == randCheckTime)
		{
			if (marriageStep != 0 && marriageStep != 1 && marriageWeddingEndTime != 0 && marriageWeddingEndTime < timeTick)
				marriageStep = 0;
			
			return timeTick > lastUseTime + MAX_WAREHOUSE_IDLE_TIME;
		}
		return false;
	}
	
	public synchronized boolean dayRefresh(int now)
	{
		int nowday = GameData.getDayByRefreshTimeOffset(now);
		if(nowday != this.lastDayRefresh)
		{
			this.dayRefreshTask(now);
			this.lastDayRefresh = nowday;
			return true;
		}
		return false;
	}
	
	private void dayRefreshTask(int now)
	{
		if(!this.tryRefreshSeriesTask(now))
			this.tryRefreshLoopTask(now);
	}
	
	// false:系列任务已经做完
	private boolean tryRefreshSeriesTask(int now)
	{
		if(this.task.series.id > 0)
			return true;
		
		SBean.MrgSeriesTaskGroupCFGS groupCfg = GameData.getInstance().getMrgSeriesTaskGroupCFGS(this.task.series.group);
		if(groupCfg == null)
			return false;
		
		this.task.series.id = 1;
		this.task.series.value = 0;
		this.task.series.state = 0;
		this.task.series.receiveTime = now;
		this.task.open = 0;
		return true;
	}
	
	private void tryRefreshLoopTask(int now)
	{
		if(this.task.loop.id <= 0 && this.task.loop.leftCount > 0)
			this.task.loop.leftCount = 0;
		
		if(this.task.loop.id > 0 || this.task.loop.leftCount > 0)
			return;
		
		this.task.loop.id = GameData.getInstance().getMrgLoopRandTask(this.minLevel);
		this.task.loop.state = 0;
		this.task.loop.receiveTime = now;
		this.task.loop.leftCount = GameData.getInstance().getMarriageCFGS().task.dayLoopTasks - 1;
		this.task.open = 0;
	}
	
	public synchronized int getMarriageType()
	{
		return marriageType;
	}

	public synchronized int getMarriageExp()
	{
		return marriageExp;
	}

	public synchronized int getMarriageLevel()
	{
		return marriageLevel;
	}
	
	public ItemCellCollection getCollection()
	{
		return collection;
	}

	public int getManId()
	{
		return manId;
	}

	public int getLadyId()
	{
		return ladyId;
	}

	public int getParterId(int roleID)
	{
		return roleID == manId ? ladyId : manId;
	}
	
	public int getMarriageStep()
	{
		return marriageStep;
	}

	public synchronized Map<Integer, SBean.MarriageSkillInfo> getMarriageSkill()
	{
		return Stream.clone(marriageSkill);
	}

	public synchronized int getMarriageTime()
	{
		return marriageTime;
	}

	public synchronized SBean.DBMarriageTask getTask()
	{
		return task;
	}

	private SBean.DBMarriageTask createTask()
	{
		return new SBean.DBMarriageTask(new SBean.DBMarriageSeriesTask(1, 1, 0, (byte)0, GameTime.getTime()), 
										new SBean.DBMarriageLoopTask(0, 0, (byte)0, 0, 0), 
										(byte)0);
	}
	
	public synchronized void updateMinLevel(int level)
	{
		if(level > 0 && (level < this.minLevel || this.minLevel == 0))
			this.minLevel = level;
	}
	
	public synchronized boolean tryWarehousePutIn(GameItem gameitem)
	{
		if (!collection.canPutIn(gameitem.id, gameitem.count))
			return false;
		collection.putIn(gameitem);
		return true;
	}

	public synchronized boolean tryWarehouseDel(int id, int count, String guid)
	{
		if (!collection.containsEnough(id, count))
			return false;
		if (!guid.equals("0"))
			collection.del(id, guid);
		else
			collection.del(id, count);
		return true;
	}
	
	public synchronized boolean tryExpandWarehouse(int times)
	{
		if (this.collection.getExpandTimes() + 1 != times)
			return false;
		this.collection.expand(GameData.getInstance().getCommonCFG().warehouse.expandCells);
		return true;
	}
	
	static boolean checkParterNearBy(Role leader, Role partner)
	{
		if(leader.gameMapContext.getCurMapId() != partner.gameMapContext.getCurMapId() || 
				leader.gameMapContext.getCurMapInstance() != partner.gameMapContext.getCurMapInstance())
			return false;
		
		return GameData.testNearByPosition(leader.gameMapContext.getCurMapPosition(), partner.gameMapContext.getCurMapPosition(), GameData.getInstance().getMarriageCFGS().task.distance + 2000);
	}
	
	synchronized boolean checkTaskMine(Role role, int mineID)
	{
		return checkSeriesMine(role, mineID) || checkLoopMine(role, mineID);
	}
	
	private boolean checkSeriesMine(Role role, int mineID)
	{
		if(this.task.series.state == 0)
			return false;
		
		SBean.MrgSeriesTaskGroupCFGS groupCfg = GameData.getInstance().getMrgSeriesTaskGroupCFGS(this.task.series.group);
		if(groupCfg == null)
			return false;
		
		SBean.MrgSeriesTaskCFGS taskCfg = GameData.getMrgSeriesTaskCFGS(groupCfg, this.task.series.id);
		if(taskCfg == null)
			return false;
		
		if(taskCfg.cond.param1 != mineID || taskCfg.cond.type != GameData.TASK_TYPE_GATHER || role.isTaskFinished(taskCfg.cond, this.task.series.value))
			return false;
		
		return true;
	}
	
	private boolean checkLoopMine(Role role, int mineID)
	{
		if(this.task.loop.state == 0 || this.task.loop.id == 0)
			return false;
		
		SBean.MrgLoopTaskCFGS taskCfg = GameData.getInstance().getMrgLoopTaskCFGS(this.task.loop.id);
		if(taskCfg == null)
			return false;
		
		if(taskCfg.cond.param1 != mineID || taskCfg.cond.type != GameData.TASK_TYPE_GATHER || role.isTaskFinished(taskCfg.cond, this.task.series.value))
			return false;
		
		return true;
	}
	
	public synchronized int openSeriesTask(Role leader, Role partner)
	{
		if(this.task.open > 0)
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!checkParterNearBy(leader, partner))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		int takeNpc = GameData.getInstance().getMarriageCFGS().task.openNPC;
		if(!GameData.getInstance().testNearbyNPC(leader.gameMapContext.getCurMapContext().getMapLocation(), takeNpc, GameData.getInstance().getCommonCFG().task.npcRadius))
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!GameData.getInstance().testNearbyNPC(partner.gameMapContext.getCurMapContext().getMapLocation(), takeNpc, GameData.getInstance().getCommonCFG().task.npcRadius))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		SBean.MrgSeriesTaskGroupCFGS groupCfg = GameData.getInstance().getMrgSeriesTaskGroupCFGS(this.task.series.group);
		if(groupCfg != null)
		{
			SBean.MrgSeriesTaskCFGS taskCfg = GameData.getMrgSeriesTaskCFGS(groupCfg, this.task.series.id);
			if(taskCfg != null && taskCfg.startNPC <= 0)
				this.task.series.state = 1;
		}
		
		this.task.open = 1;
		this.task.series.receiveTime = GameTime.getTime(); 
		return this.task.series.receiveTime;
	}
	
	public synchronized int takeSeriesTask(Role leader, Role partner, int taskID)
	{
		if(this.task.series.state > 0 || this.task.series.id != taskID)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.MrgSeriesTaskGroupCFGS groupCfg = GameData.getInstance().getMrgSeriesTaskGroupCFGS(this.task.series.group);
		if(groupCfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.MrgSeriesTaskCFGS taskCfg = GameData.getMrgSeriesTaskCFGS(groupCfg, this.task.series.id);
		if(taskCfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!checkParterNearBy(leader, partner))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		if(taskCfg.startNPC > 0)
		{
			if(!GameData.getInstance().testNearbyNPC(leader.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.startNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_FAILED;
			
			if(!GameData.getInstance().testNearbyNPC(partner.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.startNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		}
		
		this.task.series.state = 1;
		this.task.series.receiveTime = GameTime.getTime();
		return this.task.series.receiveTime;
	}
	
	public synchronized int takeSeriesTaskReward(Role leader, Role partner, int taskID)
	{
		if(this.task.series.state == 0 || this.task.series.id != taskID)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.MrgSeriesTaskGroupCFGS groupCfg = GameData.getInstance().getMrgSeriesTaskGroupCFGS(this.task.series.group);
		if(groupCfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.MrgSeriesTaskCFGS taskCfg = GameData.getMrgSeriesTaskCFGS(groupCfg, this.task.series.id);
		if(taskCfg == null || !leader.isTaskFinished(taskCfg.cond, this.task.series.value))
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!checkParterNearBy(leader, partner))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		if(taskCfg.endNPC > 0)
		{
			if(!GameData.getInstance().testNearbyNPC(leader.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.endNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_FAILED;
			
			if(!GameData.getInstance().testNearbyNPC(partner.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.endNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		}
		
		if(!leader.canAddGameItems(taskCfg.rewards))
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!partner.canAddGameItems(taskCfg.rewards))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_BAG_FULL;
		
		boolean change = addMarriageExp(taskCfg.mrgExp);
		leader.addMrgSeriesTaskReward(taskCfg, this.task.series.group, this.task.series.id, change ? marriageLevel : -1);
		partner.addMrgSeriesTaskReward(taskCfg, this.task.series.group, this.task.series.id, change ? marriageLevel : -1);
		
		this.task.series.id = taskCfg.nextID;
		this.task.series.value = 0;
		this.task.series.state = 0;
		this.task.series.receiveTime = GameTime.getTime();
		SBean.MrgSeriesTaskCFGS nextCfg = GameData.getMrgSeriesTaskCFGS(groupCfg, taskCfg.nextID);
		if(nextCfg != null)
		{
			if(nextCfg.startNPC <= 0)
				this.task.series.state = 1;
		}
		else
		{
			this.task.series.group = taskCfg.nextGroup;
		}
		
		return this.task.series.receiveTime;
	}
	
	public synchronized boolean logSeriesTask(Role leader, Role partner, int condType, int addParam1, int addParam2, int addValue)
	{
		if(this.task.series.state == 0 || this.task.series.id == 0)
			return false;
		
		SBean.MrgSeriesTaskGroupCFGS groupCfg = GameData.getInstance().getMrgSeriesTaskGroupCFGS(this.task.series.group);
		if(groupCfg == null)
			return false;
		
		SBean.MrgSeriesTaskCFGS taskCfg = GameData.getMrgSeriesTaskCFGS(groupCfg, this.task.series.id);
		if(taskCfg == null || taskCfg.cond.type != condType)
			return false;
		
		if(!checkParterNearBy(leader, partner))
			return false;
		
		int newValue = leader.testLogTask(taskCfg.cond, addParam1, addParam2, addValue, this.task.series.value);
		if(this.task.series.value == newValue)
			return false;
		
		this.task.series.value = newValue;
		return true;
	}
	
	public synchronized boolean logLoopTask(Role leader, Role partner, int condType, int addParam1, int addParam2, int addValue)
	{
		if(this.task.loop.state == 0 || this.task.loop.id == 0)
			return false;
		
		SBean.MrgLoopTaskCFGS taskCfg = GameData.getInstance().getMrgLoopTaskCFGS(this.task.loop.id);
		if(taskCfg == null || taskCfg.cond.type != condType)
			return false;
		
		if(!checkParterNearBy(leader, partner))
			return false;
		
		int newValue = leader.testLogTask(taskCfg.cond, addParam1, addParam2, addValue, this.task.loop.value);
		if(this.task.loop.value == newValue)
			return false;
		
		this.task.loop.value = newValue;
		return true;
	}
	
	public synchronized int openLoopTask(Role leader, Role partner)
	{
		if(this.task.open > 0 || this.task.loop.id == 0)
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!checkParterNearBy(leader, partner))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		int takeNpc = GameData.getInstance().getMarriageCFGS().task.openNPC;
		if(!GameData.getInstance().testNearbyNPC(leader.gameMapContext.getCurMapContext().getMapLocation(), takeNpc, GameData.getInstance().getCommonCFG().task.npcRadius))
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!GameData.getInstance().testNearbyNPC(partner.gameMapContext.getCurMapContext().getMapLocation(), takeNpc, GameData.getInstance().getCommonCFG().task.npcRadius))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		SBean.MrgLoopTaskCFGS taskCfg = GameData.getInstance().getMrgLoopTaskCFGS(this.task.loop.id);
		if(taskCfg != null && taskCfg.startNPC <= 0)
			this.task.loop.state = 1;
		
		this.task.open = 1;
		this.task.loop.receiveTime = GameTime.getTime();
		return this.task.loop.receiveTime;
	}
	
	public synchronized int takeLoopTask(Role leader, Role partner, int taskID)
	{
		if(this.task.loop.state > 0 || this.task.loop.id != taskID)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.MrgLoopTaskCFGS taskCfg = GameData.getInstance().getMrgLoopTaskCFGS(taskID);
		if(taskCfg == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!checkParterNearBy(leader, partner))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		if(taskCfg.startNPC > 0)
		{
			if(!GameData.getInstance().testNearbyNPC(leader.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.startNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_FAILED;
			
			if(!GameData.getInstance().testNearbyNPC(partner.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.startNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		}
		
		this.task.loop.state = 1;
		this.task.loop.receiveTime = GameTime.getTime();
		return this.task.loop.receiveTime;
	}
	
	public synchronized int takeLoopTaskReward(Role leader, Role partner, int taskID)
	{
		if(this.task.loop.state == 0 || this.task.loop.id != taskID)
			return GameData.PROTOCOL_OP_FAILED;
		
		SBean.MrgLoopTaskCFGS taskCfg = GameData.getInstance().getMrgLoopTaskCFGS(taskID);
		if(taskCfg == null || !leader.isTaskFinished(taskCfg.cond, this.task.loop.value))
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!checkParterNearBy(leader, partner))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		
		if(taskCfg.endNPC > 0)
		{
			if(!GameData.getInstance().testNearbyNPC(leader.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.endNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_FAILED;
			
			if(!GameData.getInstance().testNearbyNPC(partner.gameMapContext.getCurMapContext().getMapLocation(), taskCfg.endNPC, GameData.getInstance().getCommonCFG().task.npcRadius))
				return GameData.PROTOCOL_OP_MRGTASK_PARTNER_FAR;
		}
		
		if(!leader.canAddGameItems(taskCfg.rewards))
			return GameData.PROTOCOL_OP_FAILED;
		
		if(!partner.canAddGameItems(taskCfg.rewards))
			return GameData.PROTOCOL_OP_MRGTASK_PARTNER_BAG_FULL;
			
		boolean change = addMarriageExp(taskCfg.mrgExp);
		leader.addMrgLoopTaskReward(taskCfg, this.task.loop.id, change ? marriageLevel : -1);
		partner.addMrgLoopTaskReward(taskCfg, this.task.loop.id, change ? marriageLevel : -1);
		this.task.loop.value = 0;
		this.task.loop.state = 0;
		this.task.loop.receiveTime = GameTime.getTime();
		
		if(this.task.loop.leftCount > 0)
		{
			this.task.loop.id = GameData.getInstance().getMrgLoopRandTask(this.minLevel);
			this.task.loop.leftCount--;
		}
		else
			this.task.loop.id = 0;
		
		SBean.MrgLoopTaskCFGS nextCfg = GameData.getInstance().getMrgLoopTaskCFGS(this.task.loop.id);
		if(nextCfg != null && nextCfg.startNPC <= 0)
			this.task.loop.state = 1;
		
		return this.task.loop.receiveTime;
	}
	public synchronized int startMarriageParade(int endTime)
	{
		if (this.marriageStep != GameData.MARRIAGE_STEP_START)
			return GameData.PROTOCOL_OP_MARRIAGE_STEP_ERROR;
		if (this.marriageWeddingEndTime == 0)
		{
			this.marriageWeddingEndTime = endTime;
		}
		if (this.marriageWeddingEndTime != 0 && this.marriageWeddingEndTime < GameTime.getTime())
		{
			this.marriageStep = 0;
			return GameData.PROTOCOL_OP_MARRIAGE_TO_LATE;
		}
		this.marriageStep++;
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int startMarriageBanquet()
	{

		if (this.marriageStep != GameData.MARRIAGE_STEP_PARADE)
			return GameData.PROTOCOL_OP_MARRIAGE_NO_PARADE;
		if (this.marriageWeddingEndTime != 0 && this.marriageWeddingEndTime < GameTime.getTime())
		{
			this.marriageStep = 0;
			return GameData.PROTOCOL_OP_MARRIAGE_TO_LATE;
		}
		this.marriageStep++;
		return GameData.PROTOCOL_OP_SUCCESS;
	}

	public synchronized int skillLevelUp(int skillId, int levelupTimes)
	{

		SBean.MarriageSkillInfo skill = marriageSkill.get(skillId);
		if (skill == null)
			return GameData.PROTOCOL_OP_FAILED;
		int levelTimes = GameData.getInstance().computeLevel(skillId, skill.kdClone(), levelupTimes, marriageLevel);
		if (levelTimes <= 0)
			return levelTimes;
		skill.skillLevel = levelTimes >> 16;
		skill.skillUpTimes = levelTimes & 65535;
		return GameData.PROTOCOL_OP_SUCCESS;
	}
	
	public SBean.MarriageSkillInfo getMarriageSkill(int skillId)
	{
		return marriageSkill.get(skillId);
	}
	
	public synchronized Map<Integer, Integer> getMarriageSkillMap()
	{
		Map<Integer, Integer> skillsMap = new HashMap<>();
		for (SBean.MarriageSkillInfo skill : marriageSkill.values())
		{
			skillsMap.put(skill.skillId, skill.skillLevel);
		}
		return skillsMap;
	}
	
	synchronized boolean gmAddMarriageExp(Role leader, Role partner, int addExp)
	{
		return addMarriageExp(addExp);
	}
	
	private boolean addMarriageExp(int addExp)
	{
		GameData.LevelExp lvlExp = GameData.getInstance().getAddMrgLvlExp(marriageLevel, marriageExp, addExp);
		boolean lvlChange = lvlExp.level > marriageLevel;
		if(lvlChange)
		{
			marriageLevel = lvlExp.level; 
		}
		
		marriageExp = (int) lvlExp.exp;
		return lvlChange;
	}
	
	//时间必须足够长,要大于role的存盘间隔，避免写db更新的后没多久就从内存中卸载，会引起role加载共享仓库是老的数据
	private static final int MAX_WAREHOUSE_IDLE_TIME = 3600;
	private static final int MAX_CHECK_TIME = 300;
	private int randCheckTime;
	private int lastUseTime;
	private int minLevel;
	private int marriageWeddingEndTime;
	
	//存盘数据
	private int id;
	private ItemCellCollection collection;
	private int marriageType;
	private int marriageExp;
	private int marriageLevel;
	private Map<Integer, SBean.MarriageSkillInfo> marriageSkill;
	private int marriageTime;
	private int manId;
	private int ladyId;
	private SBean.DBMarriageTask task;
	private int lastDayRefresh;
	private int marriageStep;

}
