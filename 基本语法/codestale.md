# 一些可以改进的代码风格

## 1.显示类型限定可以不写
**修改前**
```java {.line-numbers}
dbRole.worldMapPets = new TreeSet<Integer>(this.worldMapPets);
dbRole.privateMapPets = new TreeSet<Integer>(this.privateMapPets);
dbRole.sectMapPets = new TreeSet<Integer>(this.sectMapPets);
dbRole.activityMapPets = new TreeSet<Integer>(this.activityMapPets);
```
**修改后**
```java {.line-numbers}
dbRole.worldMapPets = new TreeSet<>(this.worldMapPets);
dbRole.privateMapPets = new TreeSet<>(this.privateMapPets);
dbRole.sectMapPets = new TreeSet<>(this.sectMapPets);
dbRole.activityMapPets = new TreeSet<>(this.activityMapPets);
```
## 2.静态方法在lambda表达式里面可以换一种调用方式
**修改前**
```java {.line-numbers}
List<SBean.EquipPart> equipParts = this.wearParts.stream().map(e -> 
	GameData.toEquipPart(e)).collect(Collectors.toList());
```
**修改后**
```java {.line-numbers}
List<SBean.EquipPart> equipParts = this.wearParts.stream()
    .map(GameData::toEquipPart).collect(Collectors.toList());
```

<div STYLE="page-break-after: always;"></div>

## 3.用lambda表达式来取代匿名内部类
**修改前**
```java {.line-numbers}
gs.getLoginManager().addNormalTaskEvent(new Runnable() {
	@Override
	public void run()
	{
        gs.getHotSpringManager().onRoleEnterMap(Role.this,
            Role.this.gameMapContext.getAbsCurMapId());
	}
});
```
**修改后**
```java {.line-numbers}
gs.getLoginManager().addNormalTaskEvent(() ->gs.getHotSpringManager()
    .onRoleEnterMap(Role.this,Role.this.gameMapContext.getAbsCurMapId()));
```
**修改前**
```java {.line-numbers}
public void disbandSect()
{
	gs.getSectManager().dismissSect(this, 
        new SectManager.DismissSectCallback()
    {
        @Override
        public void onCallback(int errCode)
        {
        	gs.getRPCManager().sendStrPacket(Role.this.netsid, 
                new SBean.sect_disband_res(errCode));
        }
    });
}
```
**修改后**
```java {.line-numbers}
gs.getSectManager().dismissSect(this, errCode -> 
    gs.getRPCManager().sendStrPacket 
    (Role.this.netsid, new SBean.sect_disband_res(errCode)));
```

<div STYLE="page-break-after: always;"></div>

## 4.lambda表达式可以写的更简洁
**修改前**
```java {.line-numbers}
gs.getLoginManager().addNormalTaskEvent(()->tryAddKillSwornValue());
```
**修改后**
```java {.line-numbers}
gs.getLoginManager().addNormalTaskEvent(Role.this::tryAddKillSwornValue);
```
**修改前**
```java {.line-numbers}
Map<Integer, Integer> leftRewardTimes = info.keySet().stream().collect(
    Collectors.toMap(lvl->lvl, lvl->getDirectPurchaseLeftRewardTimes(lvl)));
```
**修改后**
```java {.line-numbers}
Map<Integer, Integer> leftRewardTimes = info.keySet().stream().collect(
    Collectors.toMap(lvl->lvl, this::getDirectPurchaseLeftRewardTimes));
```
**修改前**
```java {.line-numbers}
gs.getExchangeService().sendCrossFriendMsg(targetID, sendMsg, (int ok, 
    SBean.MessageInfo receiveMsg) -> 
    {
        callback.onCallBack(ok, receiveMsg);
    });
```
**修改后**
```java {.line-numbers}
gs.getExchangeService().sendCrossFriendMsg(targetID, sendMsg, callback);
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java {.line-numbers}
return cfg.getUseItemActCfg().levels.stream().anyMatch(lcfg -> {
	return log.log.getOrDefault(lcfg.uid, 0) >= lcfg.ucount && 
        !log.takedRewards.contains(lcfg.levelid);
});
```
**修改后**
```java {.line-numbers}
return cfg.getUseItemActCfg().levels.stream().anyMatch(lcfg -> 
    log.log.getOrDefault(lcfg.uid, 0)>= lcfg.ucount && 
        !log.takedRewards.contains(lcfg.levelid));
```
<div STYLE="page-break-after: always;"></div>

## 5.用jdk内定义的静态变量和封装的方法
**修改前**
```java {.line-numbers}
try {
    byte[] src = msg.getBytes("UTF-8");
    byte[] res = new MD5Digester().digest(src, 0, src.length);
    enable = Arrays.equals(res, pwd); 
    return enable; 
} catch (UnsupportedEncodingException e) {}
```
**修改后**
```java {.line-numbers}
byte[] src = msg.getBytes(StandardCharsets.UTF_8);
byte[] res = new MD5Digester().digest(src, 0, src.length);
enable = Arrays.equals(res, pwd); 
return enable; 
```
**修改前**
```java {.line-numbers}
synchronized( this )
{
	Iterator<LMMaster.DBMasterNotice> iter = master.betrayList.iterator();
    while( iter.hasNext() )
    {
        if( iter.next().roleID == appRoleID )
        {
        	iter.remove();
        }
	}
}
```
**修改后**
```java {.line-numbers}
synchronized( this )
{
    master.betrayList.removeIf(dbMasterNotice -> dbMasterNotice.roleID == appRoleID);
}
```

<div STYLE="page-break-after: always;"></div>

**修改前**
```java {.line-numbers}
StringBuilder sb = new StringBuilder();
for (Map.Entry<Integer, Integer> entry : exps.entrySet())
{
    if (sb.length() > 0)
    	sb.append(", ");
    sb.append(entry.getKey() + ":" + entry.getValue());
    this.syncAddExp(entry.getValue(), GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_DAILY_TASK, entry.getKey());
}
```
**修改后**
```java {.line-numbers}
StringBuilder sb = new StringBuilder();
for (Map.Entry<Integer, Integer> entry : exps.entrySet())
{
    if (sb.length() > 0)
    	sb.append(", ");
    sb.append(entry.getKey()).append(":").append(entry.getValue());
    this.syncAddExp(entry.getValue(), GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_DAILY_TASK, entry.getKey());
}
```

<div STYLE="page-break-after: always;"></div>

## 6.错误的或无用的if判断
**修改前**
```java  {.line-numbers}
MapCopyContext context = this.gameMapContext.getMapCopyContext();
if (context instanceof CommonMapCopyContext)
{
	CommonMapCopyContext commonContext = CommonMapCopyContext.class.cast(context);
	if (commonContext != null) //这里commonContext肯定不会是null 可以去掉if
		commonContext.addGetMine(mineId, 1);
}
else if(context instanceof GlobalPVEMapCopyContext)
{
    GlobalPVEMapCopyContext gpcc = GlobalPVEMapCopyContext.class.cast(context);
    gpcc.onEndMine(minralCfg);
}
```
**修改前**
```java  {.line-numbers}
TreeSet<Long> realRanks = null;
if(ranks instanceof TreeSet)
{
	realRanks = TreeSet.class.cast(ranks);
}
else
{
	realRanks = new TreeSet<>(ranks);
}
//realRanks == null明显不会为null了
if (realRanks == null || realRanks.isEmpty()) 
	return GameData.emptyList();
```
**修改前**
```java  {.line-numbers}
//if (this.level >= GameData.BWARENA_LVL_ADVANCE)
if (this.level >= (GameData.getInstance().getBWArenaCFGS().base.lvlReq -
   GameData.getInstance().getBWArenaCFGS().base.lvlReq - GameData.BWARENA_LVL_ADVANCE))
```

<div STYLE="page-break-after: always;"></div>

**修改前**
```java  {.line-numbers}
synchronized boolean takeVipRewards(int level)
{
    int vipRewardChangeTimes = 1;
    SBean.VipCFGS cfg = GameData.getInstance().getVipCFGS(level);
    if (cfg == null || !this.canAddGameItems(cfg.rewards))
    	return false;
    int costDiamond = cfg.vipRewardPrice;
    if (GameTime.getTime() < cfg.discountEndTime + 86400) {
        //vipRewardChangeTimes == 1应该一直是true
        if ((vipRewardChangeTimes == 1 && this.vipRewards.contains(level))) 
        	costDiamond = (int) Math.floor(costDiamond * cfg.discountRate / 10000d);
        //vipRewardChangeTimes > 1应该永远都是false吧    
        else if (vipRewardChangeTimes > 1) {
            SBean.DBVipRewardTakeLog log = this.vipRewardTakeLog.get(vipRewardChangeTimes - 1);
            if (log != null && log.takedRewards.contains(level))
                costDiamond = (int) Math.floor(costDiamond * cfg.discountRate / 10000d);
   		}
	}
```
**修改前**
```java  {.line-numbers}
context = this.gameMapContext.endMapCopy();
if(context == null || context.isEarlyLeave())
	context.earlyLeave();//如果代码走到这里 可能会报空指针异常了吧
return context == null ? GameData.PROTOCOL_OP_FAILED : GameData.PROTOCOL_OP_SUCCESS;
```
**修改前**
```java  {.line-numbers}
SBean.HorseCFGS curHorseCfg = GameData.getInstance().getHorseCFGS(this.dbHorse.inuseHorse);
if(curHorseCfg != null)
{
    int orgShowID = GameData.getHorseShow(curHorseCfg, GameData.HORSE_BIND_SHOW_ORIGINAL);
    //curHorseCfg != null上面已经判断过了
    if(curHorseCfg != null && this.dbHorse.show.showIDs.containsKey(orgShowID))
    	this.dbHorse.show.curShowID = orgShowID;
}
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java  {.line-numbers}
private boolean checkLineValid(int line)
{
    if(line == this.gameMapContext.getCurMapLine())
    	return false;
    if(line == 0)
    {
        SBean.WorldMapCFGS wmCfg = GameData.getInstance()
            .getWorldMapCFGS(this.gameMapContext.getAbsCurMapId());
        if(wmCfg != null && wmCfg.pkType == GameData.MAP_PKTYPE_NORMAL)
            return true;
    }
    if(line <= 0 || 
        line > gs.getMapService().getWorldLineNum(this.gameMapContext.getCurMapId()))
    	return false;
    return true;
}
```
**修改后**
```java {.line-numbers}
private boolean checkLineValid(int line)
{
    if(line == this.gameMapContext.getCurMapLine())
    	return false;
    if(line == 0)
    {
        SBean.WorldMapCFGS wmCfg = GameData.getInstance().getWorldMapCFGS(this.gameMapContext.getAbsCurMapId());
        if(wmCfg != null && wmCfg.pkType == GameData.MAP_PKTYPE_NORMAL)
            return true;
    }
    return line > 0 && line <= 
        gs.getMapService().getWorldLineNum(this.gameMapContext.getCurMapId());
}
```

<div STYLE="page-break-after: always;"></div>

**修改前**
```java  {.line-numbers}
if (petCFGS == null || petCFGS.lifeMapCopyId != cfg.id || dbPet.fightPet.level < 
	GameData.getInstance().getCommonCFG().pet.coPracticeOpenLvl || isPetLifeTaskFinish(pid))
	return false;
return true;
```
**修改后**
```java {.line-numbers}
return petCFGS != null && petCFGS.lifeMapCopyId == cfg.id && 
    dbPet.fightPet.level >= GameData.getInstance().getCommonCFG().pet.coPracticeOpenLvl 
    && !isPetLifeTaskFinish(pid);
```
**修改前**
```java  {.line-numbers}
public synchronized boolean testCanJoinTeam()
{
    if (!canJoinTeamMap())
        return false;
    return true;
}
```
**修改后**
```java {.line-numbers}
public synchronized boolean testCanJoinTeam()
{
   return canJoinTeamMap();
}
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java  {.line-numbers}
for (int i = 0; i < equipCfg.additProp.size(); i++) {
    SBean.EquipAdditPropCFGS additPropcfg = equipCfg.additProp.get(i);
    int addValue = addValues.get(i);
    if (addValue >= Math.floor(additPropcfg.valMax * quenchCFG.maxPercent)) {
        continue;
    } else {
        isAllPropMax = false;
        break;
    }
}
```
**修改后**
```java {.line-numbers}
for (int i = 0; i < equipCfg.additProp.size(); i++) {
    SBean.EquipAdditPropCFGS additPropcfg = equipCfg.additProp.get(i);
    int addValue = addValues.get(i);
    if (addValue < Math.floor(additPropcfg.valMax * quenchCFG.maxPercent)) {
        isAllPropMax = false;
        break;
    }
}
```
**修改前**
```java {.line-numbers}
int useExpCoin(int value, TLogger.GameItemRecords records)
{
	if (value <= 0)
		return 0;
	int use = this.roleExpCoin.useExpCoin(value, records);
		return use;
}
```
**修改后**
```java {.line-numbers}
int useExpCoin(int value, TLogger.GameItemRecords records)
{
    return value <= 0 ? 0 : this.roleExpCoin.useExpCoin(value, records);
}
```

<div STYLE="page-break-after: always;"></div>

## 7.无用的局部变量定义
**修改前**
```java {.line-numbers}
SBean.ArenaStateInfo arenaInfo = new SBean.ArenaStateInfo
(this.arenaInfo.roleArenaData.normal.point, rankNow,
	this.arenaInfo.roleArenaData.normal.bestRank, 
    this.arenaInfo.roleArenaData.normal.timesUsed, 
     this.arenaInfo.roleArenaData.normal.timesBuy, 
     this.arenaInfo.roleArenaData.normal.lastFightTime, new 
     ArrayList<>(this.arenaInfo.roleArenaData.normal.defencePets), enemies, 
     this.testArenaScoreReward() ? 1 : 0, 
     this.arenaInfo.roleArenaData.normal.hideDefence);
return arenaInfo;
```
**修改后**
```java {.line-numbers}
return new SBean.ArenaStateInfo(this.arenaInfo.roleArenaData.normal.point, 
    rankNow, this.arenaInfo.roleArenaData.normal.bestRank, 
    this.arenaInfo.roleArenaData.normal.timesUsed, 
    this.arenaInfo.roleArenaData.normal.timesBuy, 
    this.arenaInfo.roleArenaData.normal.lastFightTime, new 
    ArrayList<>(this.arenaInfo.roleArenaData.normal.defencePets), enemies, 
    this.testArenaScoreReward() ? 1
   			 : 0, this.arenaInfo.roleArenaData.normal.hideDefence);
```
**修改前**
```java {.line-numbers}
public synchronized void guardForceWarMap(int mapID, int mapInstance, int startTime)
{
    boolean mainSpawn = true;//无用的局部变量定义
    ForceWarMapCopyContext context = tryPrepareForceWarMapCopyContext(
        mapID, mapInstance, mainSpawn);
    if (context == null)
    	return;
    context.setGuard(mainSpawn);
    this.gameMapContext.startMapCopy(context);
    context.guardForceWar(mapID, mapInstance, startTime);
}
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java {.line-numbers}
synchronized RpcRes<SBean.RoleSharedPayInfo> syncSharedPayInfoImpl(int bid)
{
    int now = GameTime.getTime();//now完全没用的局部变量  
    GameConf.SharedPayConfig cfg = gs.getGameConf().getSharedPayActivities()
        .getOpendConfigById(bid);
    if (cfg == null)
    	return new RpcRes<SBean.RoleSharedPayInfo>
            (GameData.PROTOCOL_OP_CONF_SHARED_PAY_NOT_FOUND);
    SBean.DBRoleSharedPayLog log = syncSharedPayLog(cfg.getId());
    return new RpcRes<SBean.RoleSharedPayInfo>(new SBean.RoleSharedPayInfo
        (cfg.getEffectiveTime(), cfg.getConfigData(),  
        gs.getGameConf().getSharedPayActivities().
            getPayRoleCnt(cfg.getId()), log.kdClone()));
}
```

<div STYLE="page-break-after: always;"></div>

## 8.Map List初始化赋值
**修改前**
```java {.line-numbers}
List<SBean.TeamOverview> lst = new ArrayList<SBean.TeamOverview>();
lst.addAll(teams.values());
```
**修改后**
```java {.line-numbers}
List<SBean.TeamOverview> lst = new ArrayList<SBean.TeamOverview>(teams.values());
```
**修改前**
```java {.line-numbers}
Map<Integer, Integer> mergeCounters = new HashMap<>();
mergeCounters = GameData.mergeCounters(counters);
```
**修改后**
```java {.line-numbers}
Map<Integer, Integer> mergeCounters = GameData.mergeCounters(counters);
```
**修改前**
```java {.line-numbers}
for(Integer pid: ba.fightPets.keySet())
	ba.petSeq.add(pid);
```
**修改后**
```java {.line-numbers}
ba.petSeq.addAll(ba.fightPets.keySet());
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java {.line-numbers}
gs.getLoginManager().getRoleOverviews(fans, overviews ->
{
	gs.getRPCManager().sendStrPacket(this.netsid, new 
        SBean.friend_pluslist_res(overviews.values().stream().collect(Collectors.toList())));
});
```
**修改后**
```java {.line-numbers}
gs.getLoginManager().getRoleOverviews(fans, overviews -> 
	gs.getRPCManager().sendStrPacket(this.netsid, new SBean.friend_pluslist_res(
        new ArrayList<>(overviews.values()))));
```

<div STYLE="page-break-after: always;"></div>

## 9.switch的应用
**修改前**
```java {.line-numbers}
if (groupID == 0)
	return true;
switch (groupID)
{
    case 0:
    	return true;
    case 1:
    	fashion.enhanceProps = enhanceProps1;
    	break;
    case 2:
        fashion.enhanceProps = enhanceProps2;
        break;
    default:
    	return false;
}
```
**修改后**
```java {.line-numbers}
switch (groupID)
{
    case 0:
    	return true;
    case 1:
    	fashion.enhanceProps = enhanceProps1;
    	break;
    case 2:
        fashion.enhanceProps = enhanceProps2;
        break;
    default:
    	return false;
}
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java {.line-numbers}
void setLegend(int partID, int legendThree, boolean add)
{
    SBean.LegendThreeCFGS ltCfg = GameData.getInstance().getLegengThreeCFGS(partID, legendThree);
    if(ltCfg == null)
    	return;
    switch (ltCfg.type)
    {
        case GameData.LEGEND_EQUIP_THREE_TYPE_POOLHPCD:
            updateHpPoolCDReduce(ltCfg.params.get(0), add);
            break;
        default:
        	break;
    }
}
```
**修改后**
```java {.line-numbers}
void setLegend(int partID, int legendThree, boolean add)
{
    if (ltCfg != null && ltCfg.type == GameData.LEGEND_EQUIP_THREE_TYPE_POOLHPCD) 
        updateHpPoolCDReduce(ltCfg.params.get(0), add);
}
```

<div STYLE="page-break-after: always;"></div>

## 10.java8流stream的应用
**修改前**
```java {.line-numbers}
private int getClimbTowerMaxFloor()
{
    final int maxFloor[] = {0};
    this.climbTowerData.roleClimbTowerData.history.forEach((groupId, groupMaxFloor) ->
    {
    	maxFloor[0] = Math.max(maxFloor[0], groupMaxFloor);
    });  
    return maxFloor[0];
}
```
**修改后**
```java {.line-numbers}
private int getClimbTowerMaxFloor()
{
    final int[] maxFloor = {0};
    return this.climbTowerData.roleClimbTowerData.history.values()
        .stream().max(Comparator.comparing(Integer::valueOf)).orElse(0);
}
```
**修改前**
```java {.line-numbers}
private int getSuperArenaTotalJoinTimes()
{
    final int totalTimes[] = {0};
    this.arenaInfo.roleArenaData.superarena.logs.forEach( (logType, logs) -> {
    	totalTimes[0] += logs.enterTimes;
    });
    return totalTimes[0];
}
```
**修改后**
```java {.line-numbers}
private int getSuperArenaTotalJoinTimes()
{
    final int[] totalTimes = {0};
    return this.arenaInfo.roleArenaData.superarena.logs.values().stream().mapToInt(logs -> 
        logs.enterTimes).sum();
}
```
**修改前**
```java {.line-numbers}
List<Integer> skills = new ArrayList<>();
List<Integer> talents = new ArrayList<>();
for (int skillId : weaponCFGS.skills)
{
	skills.add(1);
}
for (int i = 0; i < weaponCFGS.talents.size(); i++)
{
	talents.add(0);
}
```
**修改后**
```java {.line-numbers}
List<Integer> skills = weaponCFGS.skills.stream().
    map(ignored -> 1).collect(Collectors.toList());
List<Integer> talents = weaponCFGS.talents..stream().
    map(ignored -> 0).collect(Collectors.toList());
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java {.line-numbers}
gs.getLoginManager().readRoleBriefs(roleIDs, briefs->
{
    if( briefs != null)
    {
    	briefs.stream().forEach(brief->
        {
            if( brief != null )
            {
            	roles.put(brief.id, brief);
        	}
		});
	}
    appList.stream().forEach(app->
    {
        SBean.RoleOverview brief = roles.get(app.roleID);
        if( brief != null )
        	res.applyList.add(new LMMaster.MasterApplyEntry(brief, app.applyTime));
    });
    betrayList.stream().forEach(app->
    {
        SBean.RoleOverview brief = roles.get(app.roleID);
        if( brief != null )
        	res.betrayList.add(new LMMaster.MasterApplyEntry(brief, app.eventTime));
    });
    graReqList.stream().forEach(app->
    {
        SBean.RoleOverview brief = roles.get(app.roleID);
        if( brief != null )
        	res.graduateReqList.add(new LMMaster.MasterApplyEntry(brief, app.applyTime));
    });
	gs.getRPCManager().sendStrPacket(sid, res);
});
```

<div STYLE="page-break-after: always;"></div>

**修改后**
```java {.line-numbers}
gs.getLoginManager().readRoleBriefs(roleIDs, briefs->
{
    if( briefs != null)
    {
    	briefs.stream().filter(Objects::nonNull).forEach(brief -> roles.put(brief.id, brief));
	}
    appList.stream().filter(app -> roles.containsKey(app.roleID)).forEach(app -> 
        res.applyList.add(new LMMaster.MasterApplyEntry(
        roles.get(app.roleID), app.applyTime)));
    
	betrayList.stream().filter(app -> roles.containsKey(app.roleID)).forEach(app -> 
        res.betrayList.add(new LMMaster.MasterApplyEntry(
        roles.get(app.roleID), app.eventTime)));
	graReqList.stream().filter(app -> roles.containsKey(app.roleID)).forEach(app -> 
        res.graduateReqList.add(new LMMaster.MasterApplyEntry(
        roles.get(app.roleID), app.applyTime)));
	gs.getRPCManager().sendStrPacket(sid, res);
});
```
<div STYLE="page-break-after: always;"></div>

**修改前**
```java {.line-numbers}
master.tasks.stream().forEach(task->
{
    LMMaster.MasterGraduateTaskCFGS tcfg = cfg.graduateTasks.get(task.taskType);
    if( tcfg != null && task.taskProgress >= tcfg.target )
    	score.addAndGet(tcfg.score);
});
```
**修改后**
```java {.line-numbers}
master.tasks.forEach(task->
{
    LMMaster.MasterGraduateTaskCFGS tcfg = cfg.graduateTasks.get(task.taskType);
    if( tcfg != null && task.taskProgress >= tcfg.target )
    	score.addAndGet(tcfg.score);
});
```

<div STYLE="page-break-after: always;"></div>

## 11.其他问题
**修改前**
```java {.line-numbers}
filteredmsg = GameData.getInstance().checkFilterInputStr(
    remarks.substring(0, remarks.length()), 
    "1",GameData.getInstance().getThumbTack().remarksLengh);
```
**修改后**
```java {.line-numbers}
filteredmsg = GameData.getInstance().checkFilterInputStr(remarks, 
    "1",GameData.getInstance().getThumbTack().remarksLengh);
```

**修改前**
```java {.line-numbers}
public synchronized boolean uninstallSpirit(int id)
{
    SBean.SpiritCFGS cfg = GameData.getInstance().getSpiritCFGS(id);
    if (cfg == null)
    	return false;
    SBean.DBSpirit spirit = spirits.get(id);
    if (spirit == null)
    	return false;
    if (!curSpirits.contains(id))
    	return false;
    //这里没有必要包装一下id, curSpirits.remove(id);
    curSpirits.remove((Integer.valueOf(id)));
    this.roleProperties.onUpdateCurSpirit(curSpirits);
    gs.getMapService().syncRoleUpdateCurSpirit(this.id, this.gameMapContext.getCurMapId(),
         this.gameMapContext.getCurMapInstance(), curSpirits);
    return true;
}
```