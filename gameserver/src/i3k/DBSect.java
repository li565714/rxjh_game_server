
package i3k;


import java.util.List;
import java.util.Map;
import java.util.Set;

import ket.util.Stream;

public class DBSect implements Stream.IStreamable
{
	public static int VERSION_NOW = 1;
	public DBSect() { }


	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		int dbVersion = is.popInteger();
		
		id = is.popInteger();
		name = is.popString();
		level = is.popInteger();
		lastUpgradeTime = is.popInteger();
		creed = is.popString();
		icon = is.popShort();
		frame = is.popShort();
		joinLvlReq = is.popInteger();
		vitality = is.popInteger();
		weekVit = is.popInteger();
		rankClearTime = is.popInteger();
		
		chief = is.popInteger();
		deputy = is.popIntegerTreeSet();
		elder = is.popIntegerTreeSet();
		members = is.popIntegerTreeMap(SBean.DBSectMember.class);
		//applys = is.popIntegerTreeMap(SBean.RoleOverview.class);
		applys = is.popIntegerTreeSet();
		
		lastDayRefresh = is.popInteger();
		
		auras = is.popIntegerTreeMap(SBean.DBSectAura.class);
		history = is.popList(SBean.DBSectHistory.class);
		banquets = is.popIntegerTreeMap(SBean.SectBanquet.class);
		
		lastRewardsTime = is.popInteger();
		dayResetMapTimes = is.popIntegerIntegerTreeMap();
		openedMaps = is.popIntegerTreeMap(SBean.SectMapData.class);
		mapRewards = is.popIntegerTreeMap(SBean.SectMapRewards.class);
		mapRewardsLog = is.popList(SBean.SectMapRewardsLog.class);
		logFinishedMaps = is.popIntegerIntegerTreeMap();
		
		shareTaskLib = is.popIntegerTreeMap(SBean.SectRoleSharedTasks.class);
		
		dayMailTimes = is.popInteger();
		
//		if (climbTowerData ==null)
//			climbTowerData = new SBean.DBClimbTowerRecordDataCfg();
		climbTowerData = is.popIntegerTreeMap(SBean.DBClimbTowerRecordDataCfg.class);
		sectDeliver = is.popIntegerTreeMap(SBean.DBDeliverInSect.class);
		if(sectDeliverWish == null)
			sectDeliverWish = new SBean.DBDeliverWishInSect();
		is.pop(sectDeliverWish);
		lastRefreshDeliverWishTime = is.popInteger();
		deliverRank = is.popIntegerTreeMap(SBean.DBDeliverWishListItem.class);
		shares = is.popIntegerTreeMap(SBean.DBShareDiySkillList.class);
		applicationPush = is.popByte();
		groupMapData = is.popIntegerTreeMap(SBean.DBSectGroupMapData.class);
		qqgroup = is.popString();
		dayKickTimes = is.popInteger();
		
		padding1 = is.popInteger();
		padding2 = is.popInteger();
		padding3 = is.popInteger();
		padding4 = is.popInteger();
		padding5 = is.popInteger();
		
		
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushInteger(id);
		os.pushString(name);
		os.pushInteger(level);
		os.pushInteger(lastUpgradeTime);
		os.pushString(creed);
		os.pushShort(icon);
		os.pushShort(frame);
		os.pushInteger(joinLvlReq);
		os.pushInteger(vitality);
		os.pushInteger(weekVit);
		os.pushInteger(rankClearTime);
		
		os.pushInteger(chief);
		os.pushIntegerSet(deputy);
		os.pushIntegerSet(elder);
		os.pushIntegerMap(members);
		//os.pushIntegerMap(applys);
		os.pushIntegerSet(applys);
		
		os.pushInteger(lastDayRefresh);
		
		os.pushIntegerMap(auras);
		os.pushList(history);
		os.pushIntegerMap(banquets);
		
		os.pushInteger(lastRewardsTime);
		os.pushIntegerIntegerMap(dayResetMapTimes);
		os.pushIntegerMap(openedMaps);
		os.pushIntegerMap(mapRewards);
		os.pushList(mapRewardsLog);
		os.pushIntegerIntegerMap(logFinishedMaps);
		
		os.pushIntegerMap(shareTaskLib);
		
		os.pushInteger(dayMailTimes);
		
		os.pushIntegerMap(climbTowerData);
		os.pushIntegerMap(sectDeliver);
		os.push(sectDeliverWish);
		os.pushInteger(lastRefreshDeliverWishTime);
		os.pushIntegerMap(deliverRank);
		os.pushIntegerMap(shares);
		os.pushByte(applicationPush);
		os.pushIntegerMap(groupMapData);
		os.pushString(qqgroup);
		os.pushInteger(dayKickTimes);
		
		os.pushInteger(padding1);
		os.pushInteger(padding2);
		os.pushInteger(padding3);
		os.pushInteger(padding4);
		os.pushInteger(padding5);
		
		
	}

	private String getMemberName(int memberId) 
	{
		SBean.DBSectMember member = this.members.get(memberId);
		return member == null ? "" : member.data.role.name;
	}
	
	public SBean.SectOverview getSectOverview()
	{
		return new SBean.SectOverview(this.id, this.name, this.level, this.chief, this.getMemberName(this.chief), this.members.size(), this.creed, this.icon, this.frame, this.joinLvlReq);	
	}
	
	public int id;
	public String name;
	public int level;
	public int lastUpgradeTime;
	public String creed;//教义
	public short icon;
	public short frame;
	public int joinLvlReq; 
	public int vitality;
	public int weekVit;
	public int rankClearTime;
	
	public int chief;//帮主
	public Set<Integer> deputy;//副帮主
	public Set<Integer> elder;//长老
	public Map<Integer, SBean.DBSectMember> members;//大概每个120byte
	//public Map<Integer, SBean.RoleOverview> applys;		//申请加入
	public Set<Integer> applys;		//申请加入
	
	public int lastDayRefresh;
	
	public Map<Integer, SBean.DBSectAura> auras;	//帮派光环
	public List<SBean.DBSectHistory> history;	//历史
	
	public Map<Integer, SBean.SectBanquet> banquets;//宴席
	
	public int lastRewardsTime;//帮派副本奖励上次发放时间
	public Map<Integer, Integer> dayResetMapTimes;//帮派副本id-->当天重置的副本次数
	public Map<Integer, SBean.SectMapData> openedMaps;//当前开启的帮派副本id-->副本记录
	public Map<Integer, SBean.SectMapRewards> mapRewards;//副本的奖励以及申请记录
	public List<SBean.SectMapRewardsLog> mapRewardsLog;//副本奖励分配记录
	public Map<Integer, Integer> logFinishedMaps;//副本完成次数记录
	
	public Map<Integer, SBean.SectRoleSharedTasks> shareTaskLib;
	
	public int dayMailTimes;
	public Map<Integer, SBean.DBClimbTowerRecordDataCfg> climbTowerData;
	public Map<Integer, SBean.DBDeliverInSect> sectDeliver;  //帮派运镖
	public SBean.DBDeliverWishInSect sectDeliverWish;
	public int lastRefreshDeliverWishTime;
	public Map<Integer, SBean.DBDeliverWishListItem> deliverRank;
	public Map<Integer, SBean.DBShareDiySkillList> shares;
	public byte applicationPush;
	public Map<Integer, SBean.DBSectGroupMapData> groupMapData;
	public String qqgroup;
	public int dayKickTimes;
	
	public int padding1;
	public int padding2;
	public int padding3;
	public int padding4;
	public int padding5;
	
	
}