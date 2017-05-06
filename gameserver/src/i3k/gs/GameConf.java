
package i3k.gs;

import i3k.SBean;
import i3k.SBean.TimeSpan;
import i3k.gs.GameServer.Config;
import i3k.util.GameRandom;
import i3k.util.GameTime;
import i3k.util.XmlElement;
import i3k.util.XmlElement.XmlNodeNotFoundException;
import i3k.util.XmlElement.XmlReadException;

import java.io.File;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import ket.kdb.Table;
import ket.kdb.Transaction;

public class GameConf
{
	public enum ConfigErrCode
	{
		Valid(0) 
		{
			public String toString()
			{
				return "valid";
			}
		},
		TimeInvalid(-1)
		{
			public String toString()
			{
				return "invalid(time config invalid)";
			}
		};
		
		@SuppressWarnings("unused")
        private int value;
		ConfigErrCode(int value)
		{
			this.value = value;
		}
	}
	
	public static final int FIRST_PAY_GIFT_MAX_GIFTS_COUNT = 6;
	public static final int PAY_GIFT_LEVEL_MAX_GIFTS_COUNT = 3;
	public static final int CONSUME_GIFT_LEVEL_MAX_GIFTS_COUNT = 3;
	public static final int UPGRADE_GIFT_LEVEL_MAX_GIFTS_COUNT = 3;
	public static final int EXCHANGE_GIFT_MAX_ITEMS_COUNT = 3;
	public static final int LOGIN_GIFT_MAX_ITEMS_COUNT = 3;
	public static final int DAILY_PAY_GIFT_MAX_GIFTS_COUNT = 7;
	public static final int LAST_PAY_GIFT_MAX_GIFTS_COUNT = 4;
	public static final int ACT_CHALLENGE_GIFT_MAX_GIFTS_COUNT = 4;
	public static final int UPGRADE_PURCHASE_GOODS_MAX_COUNT = 4;
	public static final int DIRECT_PURCHASE_GOODS_MAX_COUNT = 5;

	//单独入口
	public static final int CONFIG_TYPE_MALL = -1;
	public static final int CONFIG_TYPE_GROUP_BUY	= -2;
	public static final int CONFIG_TYPE_LUCKY_ROLLER = -3;
	public static final int CONFIG_TYPE_FLASH_SALE = -4;
	public static final int CONFIG_TYPE_PAY_RANK = -5;

	//列表入口
	public static final int CONFIG_TYPE_CONSUME_GIFT 			= 3;
	public static final int CONFIG_TYPE_UPGRADE_GIFT 			= 4;
	public static final int CONFIG_TYPE_INVESTMENT_FUND 		= 5;
	public static final int CONFIG_TYPE_GROWTH_FUND 			= 6;
	public static final int CONFIG_TYPE_DOUBLE_DROP 			= 7;
	public static final int CONFIG_TYPE_EXTRA_DROP 				= 8;
	public static final int CONFIG_TYPE_EXCHANGE_GIFT 			= 9;
	public static final int CONFIG_TYPE_LOGIN_GIFT 				= 10;
	public static final int CONFIG_TYPE_GIFT_PACKAGE 			= 11;
	public static final int CONFIG_TYPE_ACTIVITY_CHALLENGE_GIFT	= 14;
	public static final int CONFIG_TYPE_UPGRADE_PURCHASE 		= 15;
	public static final int CONFIG_TYPE_ONE_ARM_BANDIT 			= 17;
	public static final int CONFIG_TYPE_ADVERTISING             = 18;
	
	//充值相关活动
	public static final int CONFIG_TYPE_FIRST_PAY_GIFT 			= 1;
	public static final int CONFIG_TYPE_PAY_GIFT 				= 2;
	public static final int CONFIG_TYPE_DIRECT_PURCHASE 		= 16;
	public static final int CONFIG_TYPE_LAST_PAY_GIFT			= 13;
	public static final int CONFIG_TYPE_DAILY_PAY_GIFT 			= 12;

	private static final int GAMECONF_SAVE_INTERVAL = 900;
	
	GameServer gs;
	MallConfigImpl mallConf;
	
	Map<Integer, ActivityConfigImpl<? extends ActivityConfig>> commonActivityConfs = new TreeMap<>();
	Map<Integer, ActivityConfigImpl<? extends ActivityConfig>> payActivityConfs = new TreeMap<>();
	
	GroupBuyConfigImpl groupBuyConf;
	FlashSaleConfigImpl flashSaleConf;
	LuckyRollerConfigImpl luckyRollerConf;
	PayRankConfigImpl payRankConf;
	
	private int saveTime;
	
	public class SaveTrans implements Transaction 
	{
		SaveTrans(SBean.DBGameConfData dbGameConf)
		{
			this.dbGameConf = dbGameConf;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = ket.util.Stream.encodeStringLE("gameconf");
			byte[] data = ket.util.Stream.encodeLE(dbGameConf);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("game conf save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private final SBean.DBGameConfData dbGameConf;
	}
	
	public GameConf(GameServer gs)
	{
		this.gs = gs;
		mallConf = new MallConfigImpl();
		groupBuyConf = new GroupBuyConfigImpl();
		flashSaleConf = new FlashSaleConfigImpl();
		luckyRollerConf = new LuckyRollerConfigImpl();
		payRankConf = new PayRankConfigImpl();
		
		commonActivityConfs.put(CONFIG_TYPE_CONSUME_GIFT, new ConsumeGiftConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_UPGRADE_GIFT, new UpgradeGiftConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_INVESTMENT_FUND, new InvestmentFundConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_GROWTH_FUND, new GrowthFundConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_DOUBLE_DROP, new DoubleDropConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_EXTRA_DROP, new ExtraDropConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_EXCHANGE_GIFT, new ExchangeGiftConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_LOGIN_GIFT, new LoginGiftConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_GIFT_PACKAGE, new GiftPackageConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_ACTIVITY_CHALLENGE_GIFT, new ActivityChallengeGiftConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_UPGRADE_PURCHASE, new UpgradePurchaseConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_ONE_ARM_BANDIT, new OneArmBanditConfigImpl());
		commonActivityConfs.put(CONFIG_TYPE_ADVERTISING, new AdversConfigImpl());
		
		payActivityConfs.put(CONFIG_TYPE_FIRST_PAY_GIFT, new FirstPayGiftConfigImpl());
		payActivityConfs.put(CONFIG_TYPE_PAY_GIFT, new PayGiftConfigImpl());
		payActivityConfs.put(CONFIG_TYPE_DIRECT_PURCHASE, new DirectPurchaseConfigImpl());
		payActivityConfs.put(CONFIG_TYPE_LAST_PAY_GIFT, new LastPayGiftConfigImpl());
		payActivityConfs.put(CONFIG_TYPE_DAILY_PAY_GIFT, new DailyPayGiftConfigImpl());
	}
	
	public void start()
	{
		gs.getLogger().info("game confs init start ...........................................................................................................\n");
		mallConf.start();
		groupBuyConf.start();
		flashSaleConf.start();
		luckyRollerConf.start();
		commonActivityConfs.values().forEach(GameConfigImpl::start);
		payActivityConfs.values().forEach(GameConfigImpl::start);
		gs.getLogger().info("game confs init  end .............................................................................................................");
	}
	
	public void init(SBean.DBGameConfData dbGameConf)
	{
		if (dbGameConf != null)
		{
			this.groupBuyConf.init(dbGameConf.groupBuy);
			this.getGiftPackageActivities().init(dbGameConf.giftPack);	
		}
	}
	
	private SBean.DBGameConfData toDB()
	{
		Map<Integer, SBean.DBGroupBuy> groupbuy = this.groupBuyConf.toDB();
		Map<Integer, SBean.DBGiftPack> giftpack = this.getGiftPackageActivities().toDB();
		return new SBean.DBGameConfData(groupbuy, giftpack, new TreeMap<>(), new TreeMap<>(), (byte)0, (byte)0, 0);
	}
	
	void save()
	{
		gs.getDB().execute(new SaveTrans(toDB()));
		saveTime = GameTime.getTime(); 
	}
	
	public void onTimer(int timeTick)
	{
		boolean groupByDoSave = groupBuyConf.onTimer(timeTick);
		boolean payRankDoSave = payRankConf.onTimer(timeTick);
		if(timeTick > saveTime + GAMECONF_SAVE_INTERVAL || groupByDoSave || payRankDoSave)
			save();
	}
	
	public void updateGroupBuyLog(int activityID, int rid, int gid, int count)
	{
		GroupBuyConfig gbc = getGroupConfig(GameTime.getTime());
		if(gbc == null || activityID != gbc.getId())
			return;
		
		int total = this.groupBuyConf.updateBuyLogs(gbc.getId(), rid, gid, count);
		if(gbc.isCrossServer())
			gs.getRPCManager().notifyAuctionUpdateGroupBuyGoods(gbc.getId(), gbc.getTimeSpan().endTime, gid, total);
	}
	
	public void handleSyncGroupBuyLog(int activityID, Map<Integer, Integer> log)
	{
		this.groupBuyConf.syncGlobalBuyLog(activityID, log);
	}
	
	public void syncAuctionGroupBuyLog()
	{
		GroupBuyConfig cfg = getGroupConfig(GameTime.getTime());
		if(cfg == null || !cfg.isCrossServer())
			return;
		
		this.groupBuyConf.syncAuctionGroupBuyLog(cfg.getId(), cfg.getTimeSpan().endTime);
	}

	public void updatePayRankLogs(int activityID, SBean.RankRole rank, PayRankConfig cfg)
    {
        this.payRankConf.updatePayRankLogs(activityID, rank, cfg);
    }

    public List<SBean.RankRole> getPayRankShowList(int activityID, PayRankConfig cfg)
    {
        return this.payRankConf.getShowList(activityID, cfg);
    }

    public int getRoleRank(int activityID, int rid)
    {
        return this.payRankConf.getRoleRank(activityID, rid);
    }

	public MallConfig getMallConfig(int now)
	{
		List<MallConfig> cfgs = mallConf.getOpenedConfigs(now);
		return cfgs.isEmpty() ? null : cfgs.get(0);
	}
	
	public GroupBuyConfig getGroupConfig(int now)
	{
		List<GroupBuyConfig> cfgs = groupBuyConf.getOpenedConfigs(now);
		return cfgs.isEmpty() ? null : cfgs.get(0);
	}

    public PayRankConfig getPayRankConfig(int now)
    {
        List<PayRankConfig> cfgs = payRankConf.getOpenedConfigs(now);
        return cfgs.isEmpty() ? null : cfgs.get(0);
    }
	
	public FlashSaleConfig getFlashSaleConfig(int now)
	{
	    List<FlashSaleConfig> cfgs = flashSaleConf.getOpenedConfigs(now);
		return cfgs.isEmpty()?null:cfgs.get(0);
	}
	
	public LuckyRollerConfig getLuckyRollerConfig(int now)
	{
		 List<LuckyRollerConfig> cfgs = luckyRollerConf.getOpenedConfigs(now);
		return cfgs.isEmpty()?null:cfgs.get(0);
	}
	
	public Map<Integer, Integer> getGroupBuyCounts(int activityID)
	{
		return groupBuyConf.getBuyCount(activityID);
	}
	
	public List<ActivityConfig> getCommonActivityConfig(int now)
	{
		return commonActivityConfs.values().stream().flatMap(e -> e.getOpenedConfigs(now).stream()).collect(Collectors.toList());
	}
	
	public List<ActivityConfig> getPayActivityConfig(int now)
	{
		return payActivityConfs.values().stream().flatMap(e -> e.getOpenedConfigs(now).stream()).collect(Collectors.toList());
	}
	
	//-----------------------普通活动---------------------------------
	public ConsumeGiftConfigImpl getConsumeGiftActivities()
	{
		return (ConsumeGiftConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_CONSUME_GIFT);
	}
	
	public UpgradeGiftConfigImpl getUpgradeGiftActivities()
	{
		return (UpgradeGiftConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_UPGRADE_GIFT);
	}
	
	public InvestmentFundConfigImpl getInvestmentFundActivities()
	{
		return (InvestmentFundConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_INVESTMENT_FUND);
	}
	
	public GrowthFundConfigImpl getGrowthFundActivities()
	{
		return (GrowthFundConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_GROWTH_FUND);
	}

	public DoubleDropConfigImpl getDoubleDropActivities()
	{
		return (DoubleDropConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_DOUBLE_DROP);
	}
	
	public ExtraDropConfigImpl getExtraDropActivities()
	{
		return (ExtraDropConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_EXTRA_DROP);
	}
	
	public ExchangeGiftConfigImpl getExchangeGiftActivities()
	{
		return (ExchangeGiftConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_EXCHANGE_GIFT);
	}

	public LoginGiftConfigImpl getLoginGiftActivities()
	{
		return (LoginGiftConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_LOGIN_GIFT);
	}

	public GiftPackageConfigImpl getGiftPackageActivities()
	{
		return (GiftPackageConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_GIFT_PACKAGE);
	}

	public ActivityChallengeGiftConfigImpl getActivityChallengeGiftActivities()
	{
		return (ActivityChallengeGiftConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_ACTIVITY_CHALLENGE_GIFT);
	}
	
	public UpgradePurchaseConfigImpl getUpgradePurchaseActivities()
	{
		return (UpgradePurchaseConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_UPGRADE_PURCHASE);
	}
	
	public OneArmBanditConfigImpl getOneArmBanditActivities()
	{
		return (OneArmBanditConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_ONE_ARM_BANDIT);
	}
	
	public AdversConfigImpl getAdvertisingActivities()
	{
		return (AdversConfigImpl)this.commonActivityConfs.get(CONFIG_TYPE_ADVERTISING);
	}
	
	//-----------------------充值相关活动---------------------------------
	public FirstPayGiftConfigImpl getFirstPayGiftActivities()
	{
		return (FirstPayGiftConfigImpl)this.payActivityConfs.get(CONFIG_TYPE_FIRST_PAY_GIFT);
	}

	public PayGiftConfigImpl getPayGiftActivities()
	{
		return (PayGiftConfigImpl)this.payActivityConfs.get(CONFIG_TYPE_PAY_GIFT);
	}
	
	public DirectPurchaseConfigImpl getDirectPurchaseActivities()
	{
		return (DirectPurchaseConfigImpl)this.payActivityConfs.get(CONFIG_TYPE_DIRECT_PURCHASE);
	}
	
	public LastPayGiftConfigImpl getLastPayGiftActivities()
	{
		return (LastPayGiftConfigImpl)this.payActivityConfs.get(CONFIG_TYPE_LAST_PAY_GIFT);
	}
	
	public DailyPayGiftConfigImpl getDailyPayGiftActivities()
	{
		return (DailyPayGiftConfigImpl)this.payActivityConfs.get(CONFIG_TYPE_DAILY_PAY_GIFT);
	}
	
	public static abstract class GameConfig
	{
		private int type;
		private int effectiveTime;
		public GameConfig(int type, int time)
		{
			this.type = type;
			this.effectiveTime = time;
		}
		
		public int getType()
		{
			return this.type;
		}
		
		public int getEffectiveTime()
		{
			return this.effectiveTime;
		}
		
		public int getId()
		{
			return getTimeSpan().startTime;
		}
		
		abstract boolean getOpenConf();
		abstract SBean.TimeSpan getTimeSpan();
		abstract SBean.TimeSpan getRoleEffectiveTimeSpan(int createTime);
		
		abstract void checkValid() throws Exception;
		boolean isExclusion(GameConfig other)
		{
			return this.getId() == other.getId();
		}
		public static boolean isOpened(SBean.TimeSpan time, int now)
		{
			return now >= time.startTime && now < time.endTime; 
		}
		boolean isOpened(int now)
		{
			return getOpenConf() && isOpened(getTimeSpan(), now);
		}
		
		public boolean isInRoleEffectiveTimeSpan(int now ,int roleCreateTime)
		{
			return isOpened(getRoleEffectiveTimeSpan(roleCreateTime), now);
		}
		
		public String toOutline()
		{
			SBean.TimeSpan timeSpan = getTimeSpan();
			return "config Id=" + timeSpan.startTime + " [" + (getOpenConf() ? "open" : "close") + "] (" + GameConfig.getTimeSpanStr(timeSpan) + ")";
		}
		
		static boolean checkTimeSpanValid(SBean.TimeSpan timeSpan)
		{
			return timeSpan.startTime > 0 && timeSpan.endTime > 0 && timeSpan.startTime < timeSpan.endTime;
		}
		static boolean checkTimeSpanInclusive(SBean.TimeSpan outer, SBean.TimeSpan inner)
		{
			return inner.startTime >= outer.startTime && inner.endTime <= outer.endTime;
		}
		static boolean checkTimeSpanIntersectant(SBean.TimeSpan ts1, SBean.TimeSpan ts2)
		{
			return ts1.startTime < ts2.endTime && ts2.startTime < ts1.endTime;
		}
		static String getTimeSpanStr(SBean.TimeSpan time)
		{
			return GameTime.getDateTimeStampStr(time.startTime) + " -- " + GameTime.getDateTimeStampStr(time.endTime);
		}
		static int getDaySpan(SBean.TimeSpan time)
		{
			return GameTime.getDay(time.endTime) - GameTime.getDay(time.startTime) + 1;
		}
		public static boolean testInConfTime(SBean.TimeSpan timeSpan, int time)
		{
			return time >= timeSpan.startTime && time < timeSpan.endTime;
		}
		
		public static int getConfRealTime(int relativeTime, int baseTime, int time)
		{
			return time == 0 ? 0 : (relativeTime == 0 ? time : time + (relativeTime - baseTime));
		}
		
		public static SBean.TimeSpan toConfRealTimeSpan(int relativeTime, int baseTime, SBean.TimeSpan timeSpan)
		{
			if (timeSpan == null)
				return null;
			timeSpan.startTime = getConfRealTime(relativeTime, baseTime, timeSpan.startTime);
			timeSpan.endTime = getConfRealTime(relativeTime, baseTime, timeSpan.endTime);
			return timeSpan;
		}
		
		public static boolean checkRoleLifeDaySpanValid(SBean.RoleLifeDaySpan daySpan)
		{
			return daySpan.offset >= 0 && daySpan.duration >= 1;
		}
		
		static SBean.TimeSpan getTimeSpanProperty(XmlElement root, String name) throws XmlElement.XmlNodeNotFoundException, XmlElement.XmlReadException
		{
			XmlElement element = root.getChildByName(name);
			if (element == null)
				throw new XmlElement.XmlNodeNotFoundException("no <" + name + "> element be found in all <" + root.getName() + "> element's children !");
			try
			{
				int startTime = element.getTimeProperty("starttime");
				int endTime = element.getTimeProperty("endtime");
				return new SBean.TimeSpan(startTime, endTime);	
			}
			catch (XmlElement.XmlNodeNotFoundException e)
			{
				throw new XmlElement.XmlReadException(e.getMessage());
			}
		}
		
		static SBean.TimeSpan getTimeSpanProperty(XmlElement root, String name, SBean.TimeSpan defaultValue) throws XmlElement.XmlReadException
		{
			try
			{
				return getTimeSpanProperty(root, name);
			}
			catch(XmlElement.XmlNodeNotFoundException ex)
			{
				return defaultValue;
			}
		}
		
		static SBean.RoleLifeDaySpan getRoleLifeDaySpanProperty(XmlElement root, String name) throws XmlElement.XmlReadException
		{
			int startOffset = 0;
			int dayDuration = 5000;
			XmlElement element = root.getChildByName(name);
			if (element != null)
			{
				startOffset = element.getIntegerProperty("startoffset", startOffset);
				dayDuration = element.getIntegerProperty("dayduration", dayDuration);
			}
			return new SBean.RoleLifeDaySpan(startOffset, dayDuration);
		}
	}
	
	//带标题和内容描述的动态配置活动
	public static abstract class ActivityConfig extends GameConfig
	{
		public ActivityConfig(int type, int time)
		{
			super(type, time);
		}
		
		abstract String getTitle();
		abstract String getContent();
		abstract boolean isNoRewardsLeft(Role role, int now);
		
		abstract boolean isCanTakeRewards(Role role, int now);
		SBean.ActivityInfo getBrief(Role role, int now)
		{
			return new SBean.ActivityInfo(this.getType(), this.getId(), getTitle(), isCanTakeRewards(role, now) ? 1 : 0);
		}
		
		static void checkActivityBase(SBean.TimeSpan timeSpan, String title, String content) throws Exception
		{
			if (!GameConfig.checkTimeSpanValid(timeSpan))
				throw new Exception("activity time " + GameConfig.getTimeSpanStr(timeSpan) + " is invalid!");
			if (title.equals(""))
				throw new Exception("activity title is not set !");
			if (content.equals(""))
				throw new Exception("activity content is not set !");
		}
		
		static void checkActivityRoleParticipationDaySpan(SBean.RoleLifeDaySpan daySpan)throws Exception
		{
			if (!GameConfig.checkRoleLifeDaySpanValid(daySpan))
				throw new Exception("activity role life day span offset=" + daySpan.offset + ", duration=" + daySpan.duration + " is invalid!");
		}
		abstract SBean.RoleLifeDaySpan getRoleLifeDaySpan();
		public SBean.TimeSpan getRoleEffectiveTimeSpan(int createTime)
		{
			SBean.TimeSpan timespan = getTimeSpan();
			int startTime = timespan.startTime;
			int endTime = timespan.endTime;
			SBean.RoleLifeDaySpan lifedayspan = getRoleLifeDaySpan();
			if (lifedayspan != null)
			{
				int participationStartTime = GameTime.getDayStartTime((GameTime.getDay(createTime) + lifedayspan.offset));
				int participationEndTime = participationStartTime + GameTime.getDayTimeSpan() * lifedayspan.duration;
				if (participationEndTime <= 0)
					participationEndTime = Integer.MAX_VALUE;
				if (startTime < participationStartTime)
					startTime = participationStartTime;
				if (endTime > participationEndTime)
					endTime = participationEndTime;	
			}
			return new SBean.TimeSpan(startTime, endTime);
		}
	}
	
	public abstract class GameConfigImpl<T extends GameConfig>
	{
		private List<T> configs = new ArrayList<>();
		public GameConfigImpl()
		{
			
		}
		abstract Class<T> getConfigClassType();
		abstract String getConfigFileName(GameServer.Config cfg);
		
		public String getConfigFilePath()
		{
			GameServer.Config cfg = gs.getConfig();
			return cfg.gameConfDir + File.separator + getConfigFileName(cfg) + (cfg.gameConfFileUseGsId != 0 ?  ("." + cfg.id) : "") + GameData.GAME_CONF_FILE_EXTENSION_NAME;
		}
		
		protected synchronized List<T> getConfigs()
		{
			return this.configs;
		}
		
		protected void setConfigs(List<T> cfgs)
		{
			this.configs = cfgs;
			onConfigsChanged();
			gs.getLogger().info(getConfigClassType().getSimpleName() + " will be changed. " + (cfgs.isEmpty() ? "no valid config is active !!!" : "") + "\n");
		}
		
		protected void onConfigsChanged()
		{
			
		}
		
		public void start()
		{
			gs.getResourceManager().addWatch(getConfigFilePath(), filePath -> {
                List<T> cfgs = loadConfigs(filePath);
                setConfigs(checkConfigs(cfgs));
            });
		}
		
		public List<T> loadConfigs(String filePath)
		{
			gs.getLogger().info("try load " + getConfigClassType().getSimpleName() + " config file ...");
			try
			{
				XmlElement root = XmlElement.parseXml(filePath);
				List<T> cfgs = parseConfigs(GameTime.getTime(), root);
				gs.getLogger().info("load " + getConfigClassType().getSimpleName() + " config file success.");
				return cfgs;
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("load " + getConfigClassType().getSimpleName() + " config file failed !!!", t);
			}
			return null;
		}
		
		abstract List<T> parseConfigs(int time, XmlElement element) throws Exception;
		
		List<T> checkConfigs(List<T> configs)
		{
			List<T> validcfgs = new ArrayList<>();
			if (configs != null)
			{
				for (T cfg : configs)
				{
					try
					{
						cfg.checkValid();
						gs.getLogger().info(getConfigClassType().getSimpleName() + " " + cfg.toOutline() + " is valid.");
						for (T v : validcfgs)
						{
							if (cfg.isExclusion(v))
							{
								gs.getLogger().warn("find exclusive " + getConfigClassType().getSimpleName() + " :" + cfg.toOutline() + " <==> " + v.toOutline());
								throw new Exception("find cfgs exclusive !");
							}
						}
						validcfgs.add(cfg);
					}
					catch (Exception e)
					{
						gs.getLogger().warn(cfg.toOutline() + " is invalid ==> " + e.getMessage());
					}
				}
			}
			return validcfgs;
		}
		

		private Stream<T> getOpendConfigStream(int now)
		{
			List<T> configsCopy =  this.getConfigs();
			return configsCopy.stream().filter(cfg -> cfg.isOpened(now));
		}
		
		public List<T> getOpenedConfigs(int now)
		{
			return getOpendConfigStream(now).collect(Collectors.toList());
		}

		public T getFirstOpenedCofig()
		{
			return getOpendConfigStream(GameTime.getTime()).findFirst().orElse(null);
		}
		
		public T getOpendConfigById(int id)
		{
			return getOpendConfigStream(GameTime.getTime()).filter(cfg -> cfg.getId() == id).findAny().orElse(null);
		}
		
	}
	
	public abstract class ActivityConfigImpl<T extends ActivityConfig> extends GameConfigImpl<T>
	{
		public ActivityConfigImpl()
		{
			
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class MallConfig extends GameConfig
	{
		SBean.Mall mall;
		
		public MallConfig(int time, SBean.Mall mall)
		{
			super(CONFIG_TYPE_MALL, time);
			this.mall = mall;
		}
		
		public SBean.Mall getConfigData()
		{
			return this.mall;
		}
		
		public boolean getOpenConf()
		{
			return this.mall.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.mall.time;
		}
		
		public SBean.TimeSpan getRoleEffectiveTimeSpan(int createTime)
		{
			return this.mall.time;
		}
		
		public void checkValid() throws Exception
		{
			checkMall(this.mall);
		}
		
		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other) || GameConfig.checkTimeSpanIntersectant(this.getTimeSpan(), other.getTimeSpan());
		}
		
		public SBean.MallGoods getMallGoods(int gid, boolean free)
		{
			for (SBean.MallGoods e : free ? this.mall.fGoods : this.mall.rGoods)
			{
				if (e.id == gid)
					return e;
			}
			return null;
		}
		
		public static void checkMall(SBean.Mall mall) throws Exception
		{
			if (!GameConfig.checkTimeSpanValid(mall.time))
				throw new Exception("mall sale season " + GameConfig.getTimeSpanStr(mall.time) + " is invalid!");
			checkGoodsListValid(mall.time, mall.fGoods);
			checkGoodsListValid(mall.time, mall.rGoods);
		}
		
		public static void checkGoodsListValid(SBean.TimeSpan saleSeason, List<SBean.MallGoods> goodslst) throws Exception
		{
			Set<Integer> goodsIds = new TreeSet<>();
			for (SBean.MallGoods e : goodslst)
			{
				checkGoodsValid(saleSeason, e);
				if (!goodsIds.add(e.id))
					throw new Exception("goods id " + e.id + " is duplicate !");
			}
		}
		
		public static void checkGoodsValid(SBean.TimeSpan saleSeason, SBean.MallGoods goods) throws Exception
		{
			if (!GameData.getInstance().checkBagItemIdValid(goods.iid))
				throw new Exception("goods id " + goods.id + " : bag item id " + goods.iid + " is invalid !");
			if (goods.icount <= 0)
				throw new Exception("goods id " + goods.id + " : bag item count " + goods.icount + " is invalid !");
			if (goods.price <= 0)
				throw new Exception("goods id " + goods.id + " : price " + goods.price + " is invalid !");
			SBean.TimeSpan goodsSaleTime = saleSeason;
			if (goods.time != null)
			{
				if (!GameConfig.checkTimeSpanValid(goods.time))
					throw new Exception("goods id " + goods.id + " : time span " + GameConfig.getTimeSpanStr(goods.time) + " is invalid!");
				if (!GameConfig.checkTimeSpanInclusive(saleSeason, goods.time))
					throw new Exception("goods id " + goods.id + " : time span " + GameConfig.getTimeSpanStr(goods.discount.time) + " is not in sale season time span !");
				goodsSaleTime = goods.time;
			}
			if (goods.vipReq < 0)
				throw new Exception("goods id " + goods.id + " : vipReq " + goods.vipReq + " is invalid !");
			if (goods.levelReq < 0)
				throw new Exception("goods id " + goods.id + " : levelReq " + goods.levelReq + " is invalid !");
			if (goods.discount != null)
			{
				if (goods.discount.price <= 0)
					throw new Exception("goods id " + goods.id + " : discount price " + goods.discount.price + " is invalid !");
				if (goods.discount.price >= goods.price)
					throw new Exception("goods id " + goods.id + " : discount price " + goods.discount.price + " is greater than normal goods price !");
				if (goods.discount.time != null)
				{
					if (!GameConfig.checkTimeSpanValid(goods.discount.time))
						throw new Exception("goods id " + goods.id + " : discount time span " + GameConfig.getTimeSpanStr(goods.discount.time) + " is invalid!");
					if (!GameConfig.checkTimeSpanInclusive(goodsSaleTime, goods.discount.time))
						throw new Exception("goods id " + goods.id + " : discount time span " + GameConfig.getTimeSpanStr(goods.discount.time) + " is not in normal goods time span !");	
				}
			}
			if (goods.restriction != null)
			{
				if (goods.restriction.times <= 0)
					throw new Exception("goods id " + goods.id + " : restriction times " + goods.restriction.times + " is invalid !");
				if (goods.restriction.time != null)
				{
					if (!GameConfig.checkTimeSpanValid(goods.restriction.time))
						throw new Exception("goods id " + goods.id + " : restriction time span " + GameConfig.getTimeSpanStr(goods.restriction.time) + " is invalid!");
					if (!GameConfig.checkTimeSpanInclusive(goodsSaleTime, goods.restriction.time))
						throw new Exception("goods id " + goods.id + " : restriction time span " + GameConfig.getTimeSpanStr(goods.restriction.time) + " is not in normal goods time span !");	
				}
			}
		}
	}
	
	public class MallConfigImpl extends GameConfigImpl<MallConfig>
	{
		public MallConfigImpl()
		{
			
		}
		
		public Class<MallConfig> getConfigClassType()
		{
			return MallConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.mallConfFileName;
		}
		
		public List<MallConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<MallConfig> cfgs = new ArrayList<>();
			List<SBean.Mall> rawcfgs = parseMallConfigs(element);
			for (SBean.Mall e : rawcfgs)
			{
				cfgs.add(new MallConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.Mall> parseMallConfigs(XmlElement root) throws Exception
		{
			List<SBean.Mall> malls = new ArrayList<>();
			if (root.getName().equals("mall"))
			{
				for (XmlElement e : root.getChildrenByName("saleseason"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					List<SBean.MallGoods> fgoodslst = parseMallGoodsListConfig(e.getChildByName("free"), relativeTime, baseTime);
					List<SBean.MallGoods> rgoodslst = parseMallGoodsListConfig(e.getChildByName("restrictive"), relativeTime, baseTime);
					malls.add(new SBean.Mall(open ? 1 : 0, timeSpan, fgoodslst, rgoodslst));
				}
			}
			return malls;
		}

		List<SBean.MallGoods> parseMallGoodsListConfig(XmlElement root, int relativeTime, int baseTime) throws Exception
		{
			List<SBean.MallGoods> goodslst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("goods"))
				{
					goodslst.add(parseMallGoodsConfig(ee, relativeTime, baseTime));
				}
			}
			return goodslst;
		}
		
		SBean.MallGoods parseMallGoodsConfig(XmlElement root, int relativeTime, int baseTime) throws Exception
		{
			int id = root.getIntegerAttribute("id");
			int iid = root.getIntegerAttribute("iid");
			int icount = root.getIntegerAttribute("icount");
			int price = root.getIntegerAttribute("price");
			SBean.TimeSpan gTimeSpan = GameConfig.getTimeSpanProperty(root, "timespan", null);
			gTimeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, gTimeSpan);
			int vipReq = root.getIntegerProperty("vipreq", 0);
			int levelReq = root.getIntegerProperty("levelreq", 0);
			boolean bestseller = root.getBooleanProperty("bestseller", false);
			boolean strengthen = root.getBooleanProperty("strengthen", false);
			boolean figure = root.getBooleanProperty("figure", false);
			int attribute = ((bestseller ? SBean.MallGoods.eATypeBestSeller : 0) | (strengthen ? SBean.MallGoods.eATypeStrengthen : 0) | (figure ? SBean.MallGoods.eATypefigure : 0));
			SBean.MallGoodsDiscount discount = null;
			XmlElement dnode = root.getChildByName("discount");
			if (dnode != null)
			{
				int dprice = dnode.getIntegerAttribute("price");
				SBean.TimeSpan dTimeSpan = GameConfig.getTimeSpanProperty(dnode, "timespan", null);
				dTimeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, dTimeSpan);
				discount = new SBean.MallGoodsDiscount(dprice, dTimeSpan);
			}
			SBean.MallGoodsRestriction restriction = null;
			XmlElement rnode = root.getChildByName("restriction");
			if (rnode != null)
			{
				int times = rnode.getIntegerAttribute("times");
				boolean weekPeriod = rnode.getBooleanAttribute("weekperiod");
				SBean.TimeSpan rTimeSpan = GameConfig.getTimeSpanProperty(rnode, "timespan", null);
				rTimeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, rTimeSpan);
				restriction = new SBean.MallGoodsRestriction(times, weekPeriod ? 1 : 0, rTimeSpan);
			}
			return new SBean.MallGoods(id, iid, icount, price, gTimeSpan, vipReq, levelReq, attribute, discount, restriction);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class FirstPayGiftConfig extends ActivityConfig
	{
		SBean.FirstPayGift firstpaygift;
		public FirstPayGiftConfig(int time, SBean.FirstPayGift firstpaygift)
		{
			super(CONFIG_TYPE_FIRST_PAY_GIFT, time);
			this.firstpaygift = firstpaygift;
		}
		
		public boolean getOpenConf()
		{
			return this.firstpaygift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.firstpaygift.time;
		}
		
		public String getTitle()
		{
			return firstpaygift.title;
		}
		public String getContent()
		{
			return firstpaygift.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return firstpaygift.phase;
		}
		
		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoFirstPayGiftLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeFirstPayGift(this, now);
		}

		public SBean.FirstPayGift getInnerConfig()
		{
			return this.firstpaygift;
		}
		
		public SBean.RoleFirstPayGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleFirstPayGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), firstpaygift.title, firstpaygift.content, firstpaygift.biggift, firstpaygift.gifts);
		}
		
		public void checkValid() throws Exception
		{
			checkFirstPayGift(this.firstpaygift);
		}
		
		public static void checkFirstPayGift(SBean.FirstPayGift firstpaygift) throws Exception
		{
			checkActivityBase(firstpaygift.time, firstpaygift.title, firstpaygift.content);
			checkActivityRoleParticipationDaySpan(firstpaygift.phase);
			checkGiftsValid(firstpaygift.biggift, firstpaygift.gifts);
		}
		
		public static void checkGiftsValid(SBean.DummyGoods biggift, List<SBean.ClassTypeReward> gifts) throws Exception
		{
			if (biggift == null)
				throw new Exception("first pay gift : big gift empty is invalid !");
			if (!GameData.getInstance().checkEntityIdValid(biggift.id))
				throw new Exception("first pay gift : big gift item id " + biggift.id + " is invalid !");
			if (biggift.count <= 0)
				throw new Exception("first pay gift : big gift item count " + biggift.count + " is invalid !");
			if (gifts.size() > FIRST_PAY_GIFT_MAX_GIFTS_COUNT)
				throw new Exception("first pay gift : gifts type count " + gifts.size() + " is invalid !");
			for (SBean.ClassTypeReward e : gifts)
			{
				if (e.ids.size() !=  GameData.getInstance().getClassRoleCount())
					throw new Exception("first pay gift : gift class count " + e.ids.size() + " can't match class count " + GameData.getInstance().getClassRoleCount() + " !");
				for (int iid : e.ids)
				{
					if (!GameData.getInstance().checkEntityIdValid(iid))
						throw new Exception("first pay gift : gift item id " + iid + " is invalid !");	
				}
				if (e.count <= 0)
					throw new Exception("first pay gift : gift item count " + e.count + " is invalid !");
			}
		}
	}
	
	public class FirstPayGiftConfigImpl extends ActivityConfigImpl<FirstPayGiftConfig>
	{
		public FirstPayGiftConfigImpl()
		{
			
		}
		
		public Class<FirstPayGiftConfig> getConfigClassType()
		{
			return FirstPayGiftConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.firstPayGiftConfFileName;
		}
		
		public List<FirstPayGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<FirstPayGiftConfig> cfgs = new ArrayList<>();
			List<SBean.FirstPayGift> rawcfgs = parseFirstPayGiftConfigs(element);
			for (SBean.FirstPayGift e : rawcfgs)
			{
				cfgs.add(new FirstPayGiftConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.FirstPayGift> parseFirstPayGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.FirstPayGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("firstpaygift"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					SBean.DummyGoods biggift = parseBigGiftConfig(e);
					List<SBean.ClassTypeReward> lst = parseGiftsConfig(e);
					cfgs.add(new SBean.FirstPayGift(open ? 1 : 0, timeSpan, title, content, daySpan, biggift, lst));
				}
			}
			return cfgs;
		}

		SBean.DummyGoods parseBigGiftConfig(XmlElement root) throws Exception
		{
			SBean.DummyGoods biggift = null;
			XmlElement node = root.getChildByName("biggift");
			if (node != null)
			{
				int iid = node.getIntegerAttribute("iid");
				int icount = node.getIntegerAttribute("icount");
				biggift = new SBean.DummyGoods(iid, icount);
			}
			return biggift;
		}
		
		List<SBean.ClassTypeReward> parseGiftsConfig(XmlElement root) throws Exception
		{
			List<SBean.ClassTypeReward> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int icount = e.getIntegerAttribute("icount");
				List<Integer> ids = new ArrayList<>();
				for (XmlElement ee : e.getChildrenByName("class"))
				{
					int iid = ee.getIntegerAttribute("iid");
					ids.add(iid);
				}
				gifts.add(new SBean.ClassTypeReward(ids, icount));
			}
			return gifts;
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class PayGiftConfig extends ActivityConfig
	{
		SBean.PayGift paygift;
		public PayGiftConfig(int time, SBean.PayGift paygift)
		{
			super(CONFIG_TYPE_PAY_GIFT, time);
			this.paygift = paygift;
		}
		
		public boolean getOpenConf()
		{
			return this.paygift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.paygift.time;
		}
		
		public String getTitle()
		{
			return paygift.title;
		}
		public String getContent()
		{
			return paygift.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return paygift.phase;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakePayGift(this, now);
		}

		public SBean.PayGift getInnerConfig()
		{
			return this.paygift;
		}
		
		public SBean.RolePayGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RolePayGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), paygift.title, paygift.content, paygift.levelGifts);
		}
		
		public void checkValid() throws Exception
		{
			checkPayGift(this.paygift);
		}
		
		public static void checkPayGift(SBean.PayGift paygift) throws Exception
		{
			checkActivityBase(paygift.time, paygift.title, paygift.content);
			checkActivityRoleParticipationDaySpan(paygift.phase);
			checkLevelGiftListValid(paygift.levelGifts);
		}
		
		public static void checkLevelGiftListValid(List<SBean.PayLevelGift> lst) throws Exception
		{
			if (lst.isEmpty())
				throw new Exception("paygift activity level gifts is empty !");
			Set<Integer> payLvls = new TreeSet<>();
			for (SBean.PayLevelGift e : lst)
			{
				checkGiftsValid(e.pay, e.gifts);
				if (!payLvls.add(e.pay))
					throw new Exception("gift pay level " + e.pay + " is duplicate !");
			}
		}
		
		public static void checkGiftsValid(int payLevel, List<SBean.DummyGoods> gifts) throws Exception
		{
			if (gifts.size() <= 0 || gifts.size() > PAY_GIFT_LEVEL_MAX_GIFTS_COUNT)
				throw new Exception("gift pay level " + payLevel + " : gifts type count " + gifts.size() + " is invalid !");
			for (SBean.DummyGoods e : gifts)
			{
				if (!GameData.getInstance().checkEntityIdValid(e.id))
					throw new Exception("gift pay level " + payLevel + " : gift item id " + e.id + " is invalid !");
				if (e.count <= 0)
					throw new Exception("gift pay level " + payLevel + " : gift item count " + e.count + " is invalid !");
			}
		}
	}
	
	public class PayGiftConfigImpl extends ActivityConfigImpl<PayGiftConfig>
	{
		public PayGiftConfigImpl()
		{
			
		}
		
		public Class<PayGiftConfig> getConfigClassType()
		{
			return PayGiftConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.payGiftConfFileName;
		}
		
		public List<PayGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<PayGiftConfig> cfgs = new ArrayList<>();
			List<SBean.PayGift> rawcfgs = parsePayGiftConfigs(element);
			for (SBean.PayGift e : rawcfgs)
			{
				cfgs.add(new PayGiftConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.PayGift> parsePayGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.PayGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("paygift"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					List<SBean.PayLevelGift> lst = parseLevelGiftsListConfig(e);
					cfgs.add(new SBean.PayGift(open ? 1 : 0, timeSpan, title, content, daySpan, lst));
				}
			}
			return cfgs;
		}

		List<SBean.PayLevelGift> parseLevelGiftsListConfig(XmlElement root) throws Exception
		{
			List<SBean.PayLevelGift> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("level"))
				{
					lst.add(parseLevelGiftsConfig(ee));
				}
			}
			return lst;
		}
		
		SBean.PayLevelGift parseLevelGiftsConfig(XmlElement root) throws Exception
		{
			int pay = root.getIntegerAttribute("pay");
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return new SBean.PayLevelGift(pay, gifts);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class ConsumeGiftConfig extends ActivityConfig
	{
		SBean.ConsumeGift consumegift;
		public ConsumeGiftConfig(int time, SBean.ConsumeGift consumegift)
		{
			super(CONFIG_TYPE_CONSUME_GIFT, time);
			this.consumegift = consumegift;
		}
		
		public boolean getOpenConf()
		{
			return this.consumegift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.consumegift.time;
		}
		
		public String getTitle()
		{
			return consumegift.title;
		}
		public String getContent()
		{
			return consumegift.content;
		}

		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return consumegift.phase;
		}
		
		public boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeConsumeGift(this, now);
		}

		public SBean.ConsumeGift getInnerConfig()
		{
			return this.consumegift;
		}
		
		public SBean.RoleConsumeGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleConsumeGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), consumegift.title, consumegift.content, consumegift.levelGifts);
		}
		
		public void checkValid() throws Exception
		{
			checkConsumeGift(this.consumegift);
		}
		
		public static void checkConsumeGift(SBean.ConsumeGift consumegift) throws Exception
		{
			checkActivityBase(consumegift.time, consumegift.title, consumegift.content);
			checkActivityRoleParticipationDaySpan(consumegift.phase);
			checkLevelGiftListValid(consumegift.levelGifts);
		}
		
		public static void checkLevelGiftListValid(List<SBean.ConsumeLevelGift> lst) throws Exception
		{
			if (lst.isEmpty())
				throw new Exception("consumegift activity level gifts is empty !");
			Set<Integer> consumeLvls = new TreeSet<>();
			for (SBean.ConsumeLevelGift e : lst)
			{
				checkGiftsValid(e.consume, e.gifts);
				if (!consumeLvls.add(e.consume))
					throw new Exception("gift consume level " + e.consume + " is duplicate !");
			}
		}
		
		public static void checkGiftsValid(int consumeLevel, List<SBean.DummyGoods> gifts) throws Exception
		{
			if (gifts.size() <= 0 || gifts.size() > CONSUME_GIFT_LEVEL_MAX_GIFTS_COUNT)
				throw new Exception("gift consume level " + consumeLevel + " : gifts type count " + gifts.size() + " is invalid !");
			for (SBean.DummyGoods e : gifts)
			{
				if (!GameData.getInstance().checkEntityIdValid(e.id))
					throw new Exception("gift consume level " + consumeLevel + " : gift item id " + e.id + " is invalid !");
				if (e.count <= 0)
					throw new Exception("gift consume level " + consumeLevel + " : gift item count " + e.count + " is invalid !");
			}
		}
	}
	
	public class ConsumeGiftConfigImpl extends ActivityConfigImpl<ConsumeGiftConfig>
	{
		public ConsumeGiftConfigImpl()
		{
			
		}
		
		public Class<ConsumeGiftConfig> getConfigClassType()
		{
			return ConsumeGiftConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.consumeGiftConfFileName;
		}
		
		public List<ConsumeGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<ConsumeGiftConfig> cfgs = new ArrayList<>();
			List<SBean.ConsumeGift> rawcfgs = parseConsumeGiftConfigs(element);
			for (SBean.ConsumeGift e : rawcfgs)
			{
				cfgs.add(new ConsumeGiftConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.ConsumeGift> parseConsumeGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.ConsumeGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("consumegift"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					List<SBean.ConsumeLevelGift> lst = parseLevelGiftsListConfig(e);
					cfgs.add(new SBean.ConsumeGift(open ? 1 : 0, timeSpan, title, content, daySpan, lst));
				}
			}
			return cfgs;
		}

		List<SBean.ConsumeLevelGift> parseLevelGiftsListConfig(XmlElement root) throws Exception
		{
			List<SBean.ConsumeLevelGift> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("level"))
				{
					lst.add(parseLevelGiftsConfig(ee));
				}
			}
			return lst;
		}
		
		SBean.ConsumeLevelGift parseLevelGiftsConfig(XmlElement root) throws Exception
		{
			int consume = root.getIntegerAttribute("consume");
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return new SBean.ConsumeLevelGift(consume, gifts);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class UpgradeGiftConfig extends ActivityConfig
	{
		SBean.UpgradeGift upgradegift;
		public UpgradeGiftConfig(int time, SBean.UpgradeGift upgradegift)
		{
			super(CONFIG_TYPE_UPGRADE_GIFT, time);
			this.upgradegift = upgradegift;
		}
		
		public boolean getOpenConf()
		{
			return this.upgradegift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.upgradegift.time;
		}
		
		public String getTitle()
		{
			return upgradegift.title;
		}
		public String getContent()
		{
			return upgradegift.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return upgradegift.phase;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeUpgradeGift(this, now);
		}

		public SBean.UpgradeGift getInnerConfig()
		{
			return this.upgradegift;
		}
		
		public SBean.RoleUpgradeGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleUpgradeGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), upgradegift.title, upgradegift.content, upgradegift.limitedTime, upgradegift.levelGifts);
		}
		
		public void checkValid() throws Exception
		{
			checkUpgradeGift(this.upgradegift);
		}
		
		public static void checkUpgradeGift(SBean.UpgradeGift upgradegift) throws Exception
		{
			checkActivityBase(upgradegift.time, upgradegift.title, upgradegift.content);
			checkActivityRoleParticipationDaySpan(upgradegift.phase);
			if (upgradegift.limitedTime <= 0)
				throw new Exception("upgradegift activity limited time " + upgradegift.limitedTime + " is invalid !");
			checkLevelGiftListValid(upgradegift.levelGifts);
		}
		
		public static void checkLevelGiftListValid(List<SBean.UpgradeLevelGift> lst) throws Exception
		{
			if (lst.isEmpty())
				throw new Exception("upgradegift activity level gifts is empty !");
			Set<Integer> upgradeLvls = new TreeSet<>();
			for (SBean.UpgradeLevelGift e : lst)
			{
				checkGiftsValid(e.level, e.gifts, e.giftEx);
				if (!upgradeLvls.add(e.level))
					throw new Exception("gift upgrade level " + e.level + " is duplicate !");
			}
		}
		
		public static void checkGiftsValid(int upgradeLevel, List<SBean.DummyGoods> gifts, SBean.DummyGoods giftEx) throws Exception
		{
			if (gifts.size() <= 0 || gifts.size() > UPGRADE_GIFT_LEVEL_MAX_GIFTS_COUNT)
				throw new Exception("gift upgrade level " + upgradeLevel + " : gifts type count " + gifts.size() + " is invalid !");
			for (SBean.DummyGoods e : gifts)
			{
				if (!GameData.getInstance().checkEntityIdValid(e.id))
					throw new Exception("gift upgrade level " + upgradeLevel + " : gift item id " + e.id + " is invalid !");
				if (e.count <= 0)
					throw new Exception("gift upgrade level " + upgradeLevel + " : gift item count " + e.count + " is invalid !");
			}
			if (giftEx != null)
			{
				if (!GameData.getInstance().checkEntityIdValid(giftEx.id))
					throw new Exception("gift upgrade level " + upgradeLevel + " : giftEx item id " + giftEx.id + " is invalid !");
				if (giftEx.count <= 0)
					throw new Exception("gift upgrade level " + upgradeLevel + " : giftEx item count " + giftEx.count + " is invalid !");
			}
		}
	}
	
	public class UpgradeGiftConfigImpl extends ActivityConfigImpl<UpgradeGiftConfig>
	{
		public UpgradeGiftConfigImpl()
		{
			
		}
		
		public Class<UpgradeGiftConfig> getConfigClassType()
		{
			return UpgradeGiftConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.upgradeGiftConfFileName;
		}
		
		public List<UpgradeGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<UpgradeGiftConfig> cfgs = new ArrayList<>();
			List<SBean.UpgradeGift> rawcfgs = parseUpgradeGiftConfigs(element);
			for (SBean.UpgradeGift e : rawcfgs)
			{
				cfgs.add(new UpgradeGiftConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.UpgradeGift> parseUpgradeGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.UpgradeGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("upgradegift"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					int limitedTime = e.getIntegerProperty("limitedtime", 0) * 3600;
					List<SBean.UpgradeLevelGift> lst = parseLevelGiftsListConfig(e);
					cfgs.add(new SBean.UpgradeGift(open ? 1 : 0, timeSpan, title, content, daySpan, limitedTime, lst));
				}
			}
			return cfgs;
		}

		List<SBean.UpgradeLevelGift> parseLevelGiftsListConfig(XmlElement root) throws Exception
		{
			List<SBean.UpgradeLevelGift> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("level"))
				{
					lst.add(parseLevelGiftsConfig(ee));
				}
			}
			return lst;
		}
		
		SBean.UpgradeLevelGift parseLevelGiftsConfig(XmlElement root) throws Exception
		{
			int level = root.getIntegerAttribute("levelreq");
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			SBean.DummyGoods giftEx = null;
			XmlElement giftExNode = root.getChildByName("giftex");
			if (giftExNode != null)
			{
				int iid = giftExNode.getIntegerAttribute("iid");
				int icount = giftExNode.getIntegerAttribute("icount");
				giftEx = new SBean.DummyGoods(iid, icount);
			}
			return new SBean.UpgradeLevelGift(level, gifts, giftEx);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class InvestmentFundConfig extends ActivityConfig
	{
		SBean.InvestmentFund investmentfund;
		public InvestmentFundConfig(int time, SBean.InvestmentFund investmentfund)
		{
			super(CONFIG_TYPE_INVESTMENT_FUND, time);
			this.investmentfund = investmentfund;
		}
		
		public boolean getOpenConf()
		{
			return this.investmentfund.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.investmentfund.time;
		}
		
		public String getTitle()
		{
			return investmentfund.title;
		}
		public String getContent()
		{
			return investmentfund.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return investmentfund.phase;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoInvestmentFundGiftLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeInvestmentFundGift(this, now);
		}

		public SBean.InvestmentFund getInnerConfig()
		{
			return this.investmentfund;
		}
		
		public SBean.RoleInvestmentFundCfg toRoleCfg(Role role)
		{
			return new SBean.RoleInvestmentFundCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), investmentfund.title, investmentfund.content, investmentfund.price, investmentfund.buyEndTime, investmentfund.returns, investmentfund.levelNeed, investmentfund.vipLevelNeed);
		}
		
		public void checkValid() throws Exception
		{
			checkInvestmentFund(this.investmentfund);
		}
		
		public static void checkInvestmentFund(SBean.InvestmentFund investmentfund) throws Exception
		{
			checkActivityBase(investmentfund.time, investmentfund.title, investmentfund.content);
			checkActivityRoleParticipationDaySpan(investmentfund.phase);
			if (investmentfund.price <= 0)
				throw new Exception("investmentfund activity buy price " + investmentfund.price + " is invalid !");
			if (investmentfund.buyEndTime <= investmentfund.time.startTime || investmentfund.buyEndTime >= investmentfund.time.endTime)
				throw new Exception("investmentfund activity buy end time " + GameTime.getDateTimeStampStr(investmentfund.buyEndTime) + " is invalid !");
			checkFundReturnsListValid(investmentfund.time, investmentfund.buyEndTime, investmentfund.returns);
		}
		
		public static void checkFundReturnsListValid(SBean.TimeSpan time, int buyEndTime, List<SBean.FundDayReturn> lst) throws Exception
		{
			if (lst.isEmpty())
				throw new Exception("investmentfund activity returns is empty !");
			Set<Integer> days = new TreeSet<>();
			int lastReturnDay = -1;
			for (SBean.FundDayReturn e : lst)
			{
				checkReturnValid(time, buyEndTime, e.daySeq, e.fundReturn);
				if (!days.add(e.daySeq))
					throw new Exception("investment fund return day " + e.daySeq + " is duplicate !");
				if (e.daySeq <= lastReturnDay)
					throw new Exception("investment fund return day " + e.daySeq + " is not increase !");
				lastReturnDay = e.daySeq;
			}
		}
		
		public static void checkReturnValid(SBean.TimeSpan time, int buyEndTime, int daySeq, SBean.DummyGoods fundReturn) throws Exception
		{
			int maxLastReturnTime = GameTime.getTimeH0(buyEndTime) + daySeq * GameTime.getDayTimeSpan();
			if (daySeq < 0 || maxLastReturnTime >= time.endTime)
				throw new Exception("investment fund max last return day " + daySeq + " : " +  GameTime.getDateTimeStampStr(maxLastReturnTime) + " is invalid !");
			if (!GameData.getInstance().checkEntityIdValid(fundReturn.id))
				throw new Exception("fund return day " + daySeq + " : gift item id " + fundReturn.id + " is invalid !");
			if (fundReturn.count <= 0)
				throw new Exception("fund return day " + daySeq + " : gift item count " + fundReturn.count + " is invalid !");
		}
	}
	
	public class InvestmentFundConfigImpl extends ActivityConfigImpl<InvestmentFundConfig>
	{
		public InvestmentFundConfigImpl()
		{
			
		}
		
		public Class<InvestmentFundConfig> getConfigClassType()
		{
			return InvestmentFundConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.investmentFundConfFileName;
		}
		
		public List<InvestmentFundConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<InvestmentFundConfig> cfgs = new ArrayList<>();
			List<SBean.InvestmentFund> rawcfgs = parseInvestmentFundConfigs(element);
			for (SBean.InvestmentFund e : rawcfgs)
			{
				cfgs.add(new InvestmentFundConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.InvestmentFund> parseInvestmentFundConfigs(XmlElement root) throws Exception
		{
			List<SBean.InvestmentFund> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("investmentfund"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					int levelreq = e.getIntegerProperty("levelreq", 1);
					int vipreq = e.getIntegerProperty("vipreq", 0);
					int price = e.getIntegerProperty("price", 0);
					int buyEndTime = GameConfig.getConfRealTime(relativeTime, baseTime, e.getTimeProperty("buyendtime", 0));
					List<SBean.FundDayReturn> lst = parseFundDayReturnListConfig(e.getChildByName("return"));
					cfgs.add(new SBean.InvestmentFund(open ? 1 : 0, timeSpan, title, content, daySpan, price, buyEndTime, lst, levelreq, vipreq));
				}
			}
			return cfgs;
		}

		List<SBean.FundDayReturn> parseFundDayReturnListConfig(XmlElement root) throws Exception
		{
			List<SBean.FundDayReturn> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("day"))
				{
					lst.add(parseFundDayReturnConfig(ee));
				}
			}
			return lst;
		}
		
		SBean.FundDayReturn parseFundDayReturnConfig(XmlElement root) throws Exception
		{
			int dayseq = root.getIntegerAttribute("dayseq");
			int iid = root.getIntegerAttribute("iid");
			int icount = root.getIntegerAttribute("icount");
			return new SBean.FundDayReturn(dayseq, new SBean.DummyGoods(iid, icount));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class GrowthFundConfig extends ActivityConfig
	{
		SBean.GrowthFund growthfund;
		public GrowthFundConfig(int time, SBean.GrowthFund growthfund)
		{
			super(CONFIG_TYPE_GROWTH_FUND, time);
			this.growthfund = growthfund;
		}
		
		public boolean getOpenConf()
		{
			return this.growthfund.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.growthfund.time;
		}
		
		public String getTitle()
		{
			return growthfund.title;
		}
		public String getContent()
		{
			return growthfund.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return growthfund.phase;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoGrowthFundGiftLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeGrowthFundGift(this, now);
		}

		public SBean.GrowthFund getInnerConfig()
		{
			return this.growthfund;
		}
		
		public SBean.RoleGrowthFundCfg toRoleCfg(Role role)
		{
			return new SBean.RoleGrowthFundCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), growthfund.title, growthfund.content, growthfund.price, growthfund.buyEndTime, growthfund.returns, growthfund.levelNeed, growthfund.vipLevelNeed);
		}
		
		public void checkValid() throws Exception
		{
			checkGrowthFund(this.growthfund);
		}
		
		public static void checkGrowthFund(SBean.GrowthFund growthfund) throws Exception
		{
			checkActivityBase(growthfund.time, growthfund.title, growthfund.content);
			checkActivityRoleParticipationDaySpan(growthfund.phase);
			if (growthfund.price <= 0)
				throw new Exception("growthfund activity buy price " + growthfund.price + " is invalid !");
			if (growthfund.buyEndTime <= growthfund.time.startTime || growthfund.buyEndTime >= growthfund.time.endTime)
				throw new Exception("growthfund activity buy end time " + GameTime.getDateTimeStampStr(growthfund.buyEndTime) + " is invalid !");
			checkFundReturnsListValid(growthfund.returns);
		}
		
		public static void checkFundReturnsListValid(List<SBean.FundLevelReturn> lst) throws Exception
		{
			if (lst.isEmpty())
				throw new Exception("growthfund activity returns is empty !");
			Set<Integer> days = new TreeSet<>();
			int lastReturnLevel = 0;
			for (SBean.FundLevelReturn e : lst)
			{
				checkReturnValid(e.levelReq, e.fundReturn);
				if (!days.add(e.levelReq))
					throw new Exception("investment fund return level " + e.levelReq + " is duplicate !");
				if (e.levelReq <= lastReturnLevel)
					throw new Exception("investment fund return level " + e.levelReq + " is not increase !");
				lastReturnLevel = e.levelReq;
			}
		}
		
		public static void checkReturnValid(int level, SBean.DummyGoods fundReturn) throws Exception
		{
			if (level <= 0 )
				throw new Exception("investment fund return level " + level + " is invalid !");
			if (!GameData.getInstance().checkEntityIdValid(fundReturn.id))
				throw new Exception("fund return level " + level + " : gift item id " + fundReturn.id + " is invalid !");
			if (fundReturn.count <= 0)
				throw new Exception("fund return level " + level + " : gift item count " + fundReturn.count + " is invalid !");
		}
	}
	
	public class GrowthFundConfigImpl extends ActivityConfigImpl<GrowthFundConfig>
	{
		public GrowthFundConfigImpl()
		{
			
		}
		
		public Class<GrowthFundConfig> getConfigClassType()
		{
			return GrowthFundConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.growthFundConfFileName;
		}
		
		public List<GrowthFundConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<GrowthFundConfig> cfgs = new ArrayList<>();
			List<SBean.GrowthFund> rawcfgs = parseGrowthFundConfigs(element);
			for (SBean.GrowthFund e : rawcfgs)
			{
				cfgs.add(new GrowthFundConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.GrowthFund> parseGrowthFundConfigs(XmlElement root) throws Exception
		{
			List<SBean.GrowthFund> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("growthfund"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					int levelreq = e.getIntegerProperty("levelreq", 1);
					int vipreq = e.getIntegerProperty("vipreq", 0);
					int price = e.getIntegerProperty("price", 0);
					int buyEndTime = GameConfig.getConfRealTime(relativeTime, baseTime, e.getTimeProperty("buyendtime", 0));
					List<SBean.FundLevelReturn> lst = parseFundLevelReturnListConfig(e.getChildByName("return"));
					cfgs.add(new SBean.GrowthFund(open ? 1 : 0, timeSpan, title, content, daySpan, price, buyEndTime, lst, levelreq, vipreq));
				}
			}
			return cfgs;
		}

		List<SBean.FundLevelReturn> parseFundLevelReturnListConfig(XmlElement root) throws Exception
		{
			List<SBean.FundLevelReturn> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("level"))
				{
					lst.add(parseFundLevelReturnConfig(ee));
				}
			}
			return lst;
		}
		
		SBean.FundLevelReturn parseFundLevelReturnConfig(XmlElement root) throws Exception
		{
			int levelReq = root.getIntegerAttribute("levelreq");
			int iid = root.getIntegerAttribute("iid");
			int icount = root.getIntegerAttribute("icount");
			return new SBean.FundLevelReturn(levelReq, new SBean.DummyGoods(iid, icount));
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class DoubleDropConfig extends ActivityConfig
	{
		SBean.DoubleDrop doubledrop;
		SBean.DoubleDropCfg mapDoubleDrop;
		Map<Integer, SBean.RewardRatio> mapcopyRewardsRatio;
		public DoubleDropConfig(int time, SBean.DoubleDrop doubledrop)
		{
			super(CONFIG_TYPE_DOUBLE_DROP, time);
			this.doubledrop = doubledrop;
			this.mapcopyRewardsRatio = toMapcopyRewardRatioLookupTable();
			this.mapDoubleDrop = toMapDoubleDropLookupTable();
		}

		private SBean.DoubleDropCfg toMapDoubleDropLookupTable()
		{
			SBean.DoubleDropCfg cfg = new SBean.DoubleDropCfg(this.doubledrop.time, new TreeMap<>(), new TreeMap<>());
			for (SBean.MapcopyDropRatio e : this.doubledrop.mapcopys)
			{
				cfg.mapcopys.put(e.mapId, e.drop);
			}
			for (SBean.MonsterDropRatio e : this.doubledrop.monsters)
			{
				cfg.monsters.put(e.monsterId, e.ratio);
			}
			return cfg;
		}

		private Map<Integer, SBean.RewardRatio> toMapcopyRewardRatioLookupTable()
		{
			Map<Integer, SBean.RewardRatio> tbl = new HashMap<>();
			for (SBean.MapcopyDropRatio e : this.doubledrop.mapcopys)
			{
				tbl.put(e.mapId, e.reward);
			}
			return tbl;
		}

		public boolean getOpenConf()
		{
			return this.doubledrop.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.doubledrop.time;
		}
		
		public String getTitle()
		{
			return doubledrop.title;
		}
		public String getContent()
		{
			return doubledrop.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return null;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return false;
		}

		public SBean.DoubleDrop getInnerConfig()
		{
			return this.doubledrop;
		}

		public  SBean.RewardRatio getMapcopyRewardRatio(int mapId)
		{
			return this.mapcopyRewardsRatio.get(mapId);
		}

		public SBean.DoubleDropCfg getMapDoubleDropCfg()
		{
			return this.mapDoubleDrop;
		}

		public void checkValid() throws Exception
		{
			checkDoubleDrop(this.doubledrop);
		}
		
		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other) || GameConfig.checkTimeSpanIntersectant(this.getTimeSpan(), other.getTimeSpan());
		}
		
		public static void checkDoubleDrop(SBean.DoubleDrop doubledrop) throws Exception
		{
			checkActivityBase(doubledrop.time, doubledrop.title, doubledrop.content);
			checkMapcopyDropRatioValid(doubledrop.mapcopys);
			checkMonsterDropRatioValid(doubledrop.monsters);
			if (doubledrop.mapcopys.isEmpty() && doubledrop.monsters.isEmpty())
				throw new Exception("double drop : mapcopy and monster empty is invalid !");
		}
		
		public static void checkMapcopyDropRatioValid(List<SBean.MapcopyDropRatio> dropRatios) throws Exception
		{
			Set<Integer> maps = new TreeSet<>();
			for (SBean.MapcopyDropRatio e : dropRatios)
			{
				if (GameData.getInstance().getMapCopyCFGS(e.mapId) == null && GameData.getInstance().getActivityMapCFGS(e.mapId) == null)
					throw new Exception("double drop : mapcopy id " + e.mapId + " is invalid !");
				if (e.drop == null && e.reward == null)
					throw new Exception("double drop : mapcopy id " + e.mapId + " drop ratio and reward ratio all empty is invalid !");
				checkDropRatioValid("mapcopy", e.mapId, e.drop);
				checkRewardRatioValid(e.mapId, e.reward);
				if (!maps.add(e.mapId))
					throw new Exception("double drop : mapcopy id " + e.mapId + " is duplicate !");
			}
		}
		
		public static void checkMonsterDropRatioValid(List<SBean.MonsterDropRatio> dropRatios) throws Exception
		{
			Set<Integer> monsters = new TreeSet<>();
			for (SBean.MonsterDropRatio e : dropRatios)
			{
				if (GameData.getInstance().getMonsterCFGS(e.monsterId) == null)
					throw new Exception("double drop : monster id " + e.monsterId + " is invalid !");
				if (e.ratio == null)
					throw new Exception("double drop : monster id " + e.monsterId + " drop ratio empty is invalid !");
				checkDropRatioValid("monster", e.monsterId, e.ratio);
				if (!monsters.add(e.monsterId))
					throw new Exception("double drop : monster id " + e.monsterId + " is duplicate !");
			}
		}
		
		public static void checkDropRatioValid(String typeName, int typeId, SBean.DropRatio dropratio) throws Exception
		{
			if (dropratio.exp < 1)
				throw new Exception("double drop : " + typeName + " id " + typeId + " exp drop ratio " + dropratio.exp + " is invalid !");
			if (dropratio.fixedDrop < 1)
				throw new Exception("double drop : " + typeName + " id " + typeId + " fixedDrop drop ratio " + dropratio.fixedDrop + " is invalid !");
			if (dropratio.randomDrop < 1)
				throw new Exception("double drop : " + typeName + " id " + typeId + " randomDrop drop ratio " + dropratio.randomDrop + " is invalid !");
		}

		public static void checkRewardRatioValid(int typeId, SBean.RewardRatio rewardratio) throws Exception
		{
			if (rewardratio.exp < 1)
				throw new Exception("double drop : mapcopy id " + typeId + " exp drop ratio " + rewardratio.exp + " is invalid !");
			if (rewardratio.fixedDrop < 1)
				throw new Exception("double drop : mapcopy id " + typeId + " fixedDrop drop ratio " + rewardratio.fixedDrop + " is invalid !");
		}
	}
	
	public class DoubleDropConfigImpl extends ActivityConfigImpl<DoubleDropConfig>
	{
		public DoubleDropConfigImpl()
		{
			
		}
		
		public Class<DoubleDropConfig> getConfigClassType()
		{
			return DoubleDropConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.doubleDropConfFileName;
		}
		
		protected void onConfigsChanged()
		{
			gs.getMapService().syncMapDoubleDropCfg(getAllConfigs());
		}
		
		public List<SBean.DoubleDropCfg> getAllConfigs()
		{
			List<SBean.DoubleDropCfg> cfgs = new ArrayList<>();
			for (DoubleDropConfig cfg : this.getConfigs())
			{
				cfgs.add(cfg.getMapDoubleDropCfg());
			}
			return cfgs;
		}

		public List<DoubleDropConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<DoubleDropConfig> cfgs = new ArrayList<>();
			List<SBean.DoubleDrop> rawcfgs = parseDoubleDropConfigs(element);
			for (SBean.DoubleDrop e : rawcfgs)
			{
				cfgs.add(new DoubleDropConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.DoubleDrop> parseDoubleDropConfigs(XmlElement root) throws Exception
		{
			List<SBean.DoubleDrop> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("doubledrop"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					List<SBean.MapcopyDropRatio> mopcopyLst = parseMapcopyDropRatioConfig(e);
					List<SBean.MonsterDropRatio> monsterLst = parseMonsterDropRatioConfig(e);
					cfgs.add(new SBean.DoubleDrop(open ? 1 : 0, timeSpan, title, content, mopcopyLst, monsterLst));
				}
			}
			return cfgs;
		}

		List<SBean.MapcopyDropRatio> parseMapcopyDropRatioConfig(XmlElement root) throws Exception
		{
			List<SBean.MapcopyDropRatio> lst = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("mapcopy"))
			{
				int mapId = e.getIntegerAttribute("id");
				SBean.DropRatio drop = parseDropRatioConfig(e.getChildByName("dropratio"));
				SBean.RewardRatio reward = parseRewardRatioConfig(e.getChildByName("rewardratio"));
				lst.add(new SBean.MapcopyDropRatio(mapId, drop, reward));
			}
			return lst;
		}
		
		List<SBean.MonsterDropRatio> parseMonsterDropRatioConfig(XmlElement root) throws Exception
		{
			List<SBean.MonsterDropRatio> lst = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("monster"))
			{
				int mapId = e.getIntegerAttribute("id");
				SBean.DropRatio ratio = parseDropRatioConfig(e.getChildByName("dropratio"));
				lst.add(new SBean.MonsterDropRatio(mapId, ratio));
			}
			return lst;
		}
		
		SBean.DropRatio parseDropRatioConfig(XmlElement root) throws Exception
		{
			SBean.DropRatio ratio = null;
			if (root != null)
			{
				float expRatio = root.getFloatProperty("exp");
				int fixeddropRatio = root.getIntegerProperty("fixeddrop");
				int randomdropRatio = root.getIntegerProperty("randomdrop");
				ratio = new SBean.DropRatio(expRatio, fixeddropRatio, randomdropRatio);
			}
			return ratio;
		}

		SBean.RewardRatio parseRewardRatioConfig(XmlElement root) throws Exception
		{
			SBean.RewardRatio ratio = null;
			if (root != null)
			{
				float expRatio = root.getFloatProperty("exp");
				int fixeddropRatio = root.getIntegerProperty("fixeddrop");
				ratio = new SBean.RewardRatio(expRatio, fixeddropRatio);
			}
			return ratio;
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class ExtraDropConfig extends ActivityConfig
	{
		SBean.ExtraDrop extra;
		SBean.ExtraDropCfg mapExtraDropCfg;
		Set<Integer> possibleDropItems;
		public ExtraDropConfig(int time, SBean.ExtraDrop extra)
		{
			super(CONFIG_TYPE_EXTRA_DROP, time);
			this.extra = extra;
			this.mapExtraDropCfg = toMapExtraDropLookupTable();
			this.possibleDropItems = toPossibleDropItemSet();
		}

		public SBean.ExtraDropCfg toMapExtraDropLookupTable()
		{
			SBean.ExtraDropCfg cfg = new SBean.ExtraDropCfg(this.extra.time, new TreeMap<>(), new TreeMap<>());
			for (SBean.MapcopyExtraDrop e : this.extra.mapcopys)
			{
				cfg.mapcopys.put(e.mapId, e.drop);
			}
			for (SBean.MonsterExtraDrop e : this.extra.monsters)
			{
				cfg.monsters.put(e.monsterId, e.drop);
			}
			return cfg;
		}

		public Set<Integer> toPossibleDropItemSet()
		{
			Set<Integer> set = new TreeSet<>();
			for (SBean.MapcopyExtraDrop e : this.extra.mapcopys)
			{
				for (SBean.DropEntry ee : e.drop.drops)
				{
					set.add(ee.drop.did);
				}
			}
			for (SBean.MonsterExtraDrop e : this.extra.monsters)
			{
				for (SBean.DropEntry ee : e.drop.drops)
				{
					set.add(ee.drop.did);
				}
			}
			return set;
		}

		public boolean getOpenConf()
		{
			return this.extra.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.extra.time;
		}
		
		public String getTitle()
		{
			return extra.title;
		}
		public String getContent()
		{
			return extra.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return null;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return false;
		}

		public SBean.ExtraDrop getInnerConfig()
		{
			return this.extra;
		}

		public SBean.ExtraDropCfg getMapExtraDropCfg()
		{
			return this.mapExtraDropCfg;
		}

		public Set<Integer> getPossibleDropItems()
		{
			return this.possibleDropItems;
		}

		public void checkValid() throws Exception
		{
			checkExtraDrop(this.extra);
		}
		
		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other) || GameConfig.checkTimeSpanIntersectant(this.getTimeSpan(), other.getTimeSpan());
		}
		
		public static void checkExtraDrop(SBean.ExtraDrop extra) throws Exception
		{
			checkActivityBase(extra.time, extra.title, extra.content);
			checkMapcopyExtraDropValid(extra.mapcopys);
			checkMonsterExtraDropValid(extra.monsters);
			if (extra.mapcopys.isEmpty() && extra.monsters.isEmpty())
				throw new Exception("extra drop : mapcopy and monster empty is invalid !");
		}
		
		public static void checkMapcopyExtraDropValid(List<SBean.MapcopyExtraDrop> drops) throws Exception
		{
			Set<Integer> maps = new TreeSet<>();
			for (SBean.MapcopyExtraDrop e : drops)
			{
				if (GameData.getInstance().getMapCopyCFGS(e.mapId) == null && GameData.getInstance().getActivityMapCFGS(e.mapId) == null)
					throw new Exception("extra drop : mapcopy id " + e.mapId + " is invalid !");
				checkExtraDropValid("mapcopy", e.mapId, e.drop);
				if (!maps.add(e.mapId))
					throw new Exception("extra drop : mapcopy id " + e.mapId + " is duplicate !");
			}
		}
		
		public static void checkMonsterExtraDropValid(List<SBean.MonsterExtraDrop> drops) throws Exception
		{
			Set<Integer> monsters = new TreeSet<>();
			for (SBean.MonsterExtraDrop e : drops)
			{
				if (GameData.getInstance().getMonsterCFGS(e.monsterId) == null)
					throw new Exception("extra drop : monster id " + e.monsterId + " is invalid !");
				checkExtraDropValid("monster", e.monsterId, e.drop);
				if (!monsters.add(e.monsterId))
					throw new Exception("extra drop : monster id " + e.monsterId + " is duplicate !");
			}
		}
		
		public static void checkExtraDropValid(String typeName, int typeId, SBean.ExtraDropTbl drops) throws Exception
		{
			if (drops == null || drops.drops.isEmpty())
				throw new Exception("extra drop : " + typeName + " id " + typeId + " drop table empty is invalid !");
			float probabilitySum = 0;
			for (SBean.DropEntry e : drops.drops)
			{
				if (e.probability <= 0 || e.probability > 1)
					throw new Exception("extra drop : " + typeName + " id " + typeId + ", drop item probability " + e.probability + " is invalid !");
				probabilitySum += e.probability;
				checkDropEntryValid(typeName, typeId, e.drop);
				e.probability = probabilitySum;
			}
			if (probabilitySum > 1)
				throw new Exception("extra drop : " + typeName + " id " + typeId + ", all drop item probability sum is greater than 1 !");
		}
		
		public static void checkDropEntryValid(String typeName, int typeId, SBean.DropEntity dropEntity) throws Exception
		{
			if (!GameData.getInstance().checkEntityIdValid(dropEntity.did))
				throw new Exception("extra drop : " + typeName + " id " + typeId + ", drop item id " + dropEntity.did + " is invalid !");
			if (dropEntity.minCount <= 0 || dropEntity.maxCount < dropEntity.minCount)
				throw new Exception("extra drop : " + typeName + " id " + typeId + ", drop item id " + dropEntity.did + ", drop count range [" + dropEntity.minCount + ", " + dropEntity.maxCount + "] is invalid !");
		}
	}
	
	public class ExtraDropConfigImpl extends ActivityConfigImpl<ExtraDropConfig>
	{
		public ExtraDropConfigImpl()
		{
			
		}
		
		public Class<ExtraDropConfig> getConfigClassType()
		{
			return ExtraDropConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.extraDropConfFileName;
		}
		
		protected void onConfigsChanged()
		{
			gs.getMapService().syncMapExtraDropCfg(getAllConfigs());
		}
		
		public List<SBean.ExtraDropCfg> getAllConfigs()
		{
			List<SBean.ExtraDropCfg> mcfgs = new ArrayList<>();
			for (ExtraDropConfig cfg : this.getConfigs())
			{
				mcfgs.add(cfg.getMapExtraDropCfg());
			}
			return mcfgs;
		}
		
		public List<ExtraDropConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<ExtraDropConfig> cfgs = new ArrayList<>();
			List<SBean.ExtraDrop> rawcfgs = parseExtraDropConfigs(element);
			for (SBean.ExtraDrop e : rawcfgs)
			{
				cfgs.add(new ExtraDropConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.ExtraDrop> parseExtraDropConfigs(XmlElement root) throws Exception
		{
			List<SBean.ExtraDrop> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("extradrop"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					List<SBean.MapcopyExtraDrop> mopcopyLst = parseMapcopyExtraDropConfig(e);
					List<SBean.MonsterExtraDrop> monsterLst = parseMonsterExtraDropConfig(e);
					cfgs.add(new SBean.ExtraDrop(open ? 1 : 0, timeSpan, title, content, mopcopyLst, monsterLst));
				}
			}
			return cfgs;
		}

		List<SBean.MapcopyExtraDrop> parseMapcopyExtraDropConfig(XmlElement root) throws Exception
		{
			List<SBean.MapcopyExtraDrop> lst = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("mapcopy"))
			{
				int mapId = e.getIntegerAttribute("id");
				SBean.ExtraDropTbl dropTbl = parseExtraDropTblConfig(e.getChildByName("drops"));
				lst.add(new SBean.MapcopyExtraDrop(mapId, dropTbl));
			}
			return lst;
		}
		
		List<SBean.MonsterExtraDrop> parseMonsterExtraDropConfig(XmlElement root) throws Exception
		{
			List<SBean.MonsterExtraDrop> lst = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("monster"))
			{
				int mapId = e.getIntegerAttribute("id");
				SBean.ExtraDropTbl dropTbl = parseExtraDropTblConfig(e.getChildByName("drops"));
				lst.add(new SBean.MonsterExtraDrop(mapId, dropTbl));
			}
			return lst;
		}
		
		SBean.ExtraDropTbl parseExtraDropTblConfig(XmlElement root) throws Exception
		{
			SBean.ExtraDropTbl dropTbl = null;
			if (root != null)
			{
				List<SBean.DropEntry> dropEntrys = new ArrayList<>();
				for (XmlElement ee : root.getChildrenByName("dropentry"))
				{
					dropEntrys.add(parseExtraDropConfig(ee));
				}
				dropTbl = new SBean.ExtraDropTbl(dropEntrys);
			}
			return dropTbl;
		}
		
		SBean.DropEntry parseExtraDropConfig(XmlElement root) throws Exception
		{
			float probability = root.getFloatAttribute("probability");
			int iid = root.getIntegerProperty("iid");
			int icountmin = root.getIntegerProperty("icountmin");
			int icountmax = root.getIntegerProperty("icountmax");
			return new SBean.DropEntry(new SBean.DropEntity(iid, icountmin, icountmax), probability);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class ExchangeGiftConfig extends ActivityConfig
	{
		SBean.ExchangeGift exchangegift;
		public ExchangeGiftConfig(int time, SBean.ExchangeGift exchangegift)
		{
			super(CONFIG_TYPE_EXCHANGE_GIFT, time);
			this.exchangegift = exchangegift;
		}
		
		public boolean getOpenConf()
		{
			return this.exchangegift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.exchangegift.time;
		}
		
		public String getTitle()
		{
			return exchangegift.title;
		}
		public String getContent()
		{
			return exchangegift.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return exchangegift.phase;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeExchangeGift(this, now);
		}

		public SBean.ExchangeGift getInnerConfig()
		{
			return this.exchangegift;
		}
		
		public SBean.RoleExchangeGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleExchangeGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), exchangegift.title, exchangegift.content, exchangegift.itemGifts);
		}
		
		public void checkValid() throws Exception
		{
			checkExchangeGift(this.exchangegift);
		}
		
		public static void checkExchangeGift(SBean.ExchangeGift exchangegift) throws Exception
		{
			checkActivityBase(exchangegift.time, exchangegift.title, exchangegift.content);
			checkActivityRoleParticipationDaySpan(exchangegift.phase);
			checkItemGiftListValid(exchangegift.itemGifts);
		}
		
		public static void checkItemGiftListValid(List<SBean.ExchangeItemGift> lst) throws Exception
		{
			if (lst.isEmpty())
				throw new Exception("exchangegift activity item gifts is empty !");
			for (int index = 0; index < lst.size(); ++index)
			{
				SBean.ExchangeItemGift e = lst.get(index);
				checkGiftValid(e.seq, e.gift);
				checkItemsValid(e.seq, e.items);
			}
		}
		
		public static void checkGiftValid(int seq, SBean.DummyGoods gift) throws Exception
		{
			if (!GameData.getInstance().checkEntityIdValid(gift.id))
				throw new Exception("exchange items seq " + seq + " : gift id " + gift.id + " is invalid !");
			if (gift.count <= 0)
				throw new Exception("exchange items seq " + seq + " : gift count " + gift.count + " is invalid !");
		}
		
		public static void checkItemsValid(int seq, List<SBean.DummyGoods> items) throws Exception
		{
			if (items.size() <= 0 || items.size() > EXCHANGE_GIFT_MAX_ITEMS_COUNT)
				throw new Exception("exchange items seq " + seq + " : items type count " + items.size() + " is invalid !");
			for (SBean.DummyGoods e : items)
			{
				if (!GameData.getInstance().checkEntityIdValid(e.id))
					throw new Exception("exchange items seq " + seq + " : item id " + e.id + " is invalid !");
				if (e.count <= 0)
					throw new Exception("exchange items seq " + seq + " : item count " + e.count + " is invalid !");
			}
		}
	}
	
	public class ExchangeGiftConfigImpl extends ActivityConfigImpl<ExchangeGiftConfig>
	{
		public ExchangeGiftConfigImpl()
		{
			
		}
		
		public Class<ExchangeGiftConfig> getConfigClassType()
		{
			return ExchangeGiftConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.exchangeGiftConfFileName;
		}
		
		public List<ExchangeGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<ExchangeGiftConfig> cfgs = new ArrayList<>();
			List<SBean.ExchangeGift> rawcfgs = parseExchangeGiftConfigs(element);
			for (SBean.ExchangeGift e : rawcfgs)
			{
				cfgs.add(new ExchangeGiftConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.ExchangeGift> parseExchangeGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.ExchangeGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("exchangegift"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					List<SBean.ExchangeItemGift> lst = parseItemGiftsListConfig(e);
					cfgs.add(new SBean.ExchangeGift(open ? 1 : 0, timeSpan, title, content, daySpan, lst));
				}
			}
			return cfgs;
		}

		List<SBean.ExchangeItemGift> parseItemGiftsListConfig(XmlElement root) throws Exception
		{
			List<SBean.ExchangeItemGift> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement e : root.getChildrenByName("gift"))
				{
					lst.add(parseItemGiftsConfig(lst.size()+1, e));
				}
			}
			return lst;
		}
		
		SBean.ExchangeItemGift parseItemGiftsConfig(int seq, XmlElement root) throws Exception
		{
			int gid = root.getIntegerAttribute("iid");
			int gcount = root.getIntegerAttribute("icount");
			int maxExchange = root.getIntegerAttribute("maxexchange", 0);
			List<SBean.DummyGoods> items = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("item"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				items.add(new SBean.DummyGoods(iid, icount));
			}
			return new SBean.ExchangeItemGift(seq, maxExchange, new SBean.DummyGoods(gid, gcount), items);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class LoginGiftConfig extends ActivityConfig
	{
		SBean.LoginGift logingift;
		public LoginGiftConfig(int time, SBean.LoginGift logingift)
		{
			super(CONFIG_TYPE_LOGIN_GIFT, time);
			this.logingift = logingift;
		}

		public boolean getOpenConf()
		{
			return this.logingift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.logingift.time;
		}

		public String getTitle()
		{
			return logingift.title;
		}
		public String getContent()
		{
			return logingift.content;
		}

		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return logingift.phase;
		}
		
		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoLoginGiftLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeLoginGift(this, now);
		}

		public SBean.LoginGift getInnerConfig()
		{
			return this.logingift;
		}
		
		public SBean.RoleLoginGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleLoginGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), logingift.title, logingift.content, logingift.dayGifts);
		}

		public void checkValid() throws Exception
		{
			checkLoginGift(this.logingift);
		}

		public static void checkLoginGift(SBean.LoginGift logingift) throws Exception
		{
			checkActivityBase(logingift.time, logingift.title, logingift.content);
			checkActivityRoleParticipationDaySpan(logingift.phase);
			checkLoginDayGiftListValid(logingift.dayGifts);
		}

		public static void checkLoginDayGiftListValid(List<SBean.LoginDayGift> lst) throws Exception
		{
			if (lst.isEmpty())
				throw new Exception("logingift activity day gifts is empty !");
			Set<Integer> loginDays = new TreeSet<>();
			int lastLoginDay = 0;
			for (SBean.LoginDayGift e : lst)
			{
				checkGiftsValid(e.dayReq, e.gifts);
				if (!loginDays.add(e.dayReq))
					throw new Exception("gift login days " + e.dayReq + " is duplicate !");
				if (e.dayReq <= lastLoginDay)
					throw new Exception("gift login days " + e.dayReq + " is not increase !");
				lastLoginDay = e.dayReq;
			}
		}

		public static void checkGiftsValid(int dayReq, List<SBean.DummyGoods> gifts) throws Exception
		{
			if (gifts.size() <= 0 || gifts.size() > LOGIN_GIFT_MAX_ITEMS_COUNT)
				throw new Exception("gift login days " + dayReq + " : gifts type count " + gifts.size() + " is invalid !");
			for (SBean.DummyGoods e : gifts)
			{
				if (!GameData.getInstance().checkEntityIdValid(e.id))
					throw new Exception("gift login days " + dayReq + " : gift item id " + e.id + " is invalid !");
				if (e.count <= 0)
					throw new Exception("gift login days " + dayReq + " : gift item count " + e.count + " is invalid !");
			}
		}
	}

	public class LoginGiftConfigImpl extends ActivityConfigImpl<LoginGiftConfig>
	{
		public LoginGiftConfigImpl()
		{

		}

		public Class<LoginGiftConfig> getConfigClassType()
		{
			return LoginGiftConfig.class;
		}

		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.loginGiftConfFileName;
		}

		public List<LoginGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<LoginGiftConfig> cfgs = new ArrayList<>();
			List<SBean.LoginGift> rawcfgs = parseLoginGiftConfigs(element);
			for (SBean.LoginGift e : rawcfgs)
			{
				cfgs.add(new LoginGiftConfig(time, e));
			}
			return cfgs;
		}

		List<SBean.LoginGift> parseLoginGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.LoginGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("logingift"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					List<SBean.LoginDayGift> lst = parseDayGiftsListConfig(e);
					cfgs.add(new SBean.LoginGift(open ? 1 : 0, timeSpan, title, content, daySpan, lst));
				}
			}
			return cfgs;
		}

		List<SBean.LoginDayGift> parseDayGiftsListConfig(XmlElement root) throws Exception
		{
			List<SBean.LoginDayGift> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("day"))
				{
					lst.add(parseDayGiftsConfig(ee));
				}
			}
			return lst;
		}

		SBean.LoginDayGift parseDayGiftsConfig(XmlElement root) throws Exception
		{
			int dayreq = root.getIntegerAttribute("dayreq");
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return new SBean.LoginDayGift(dayreq, gifts);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class GiftPackageConfig extends ActivityConfig
	{
		SBean.GiftPackage giftpackage;
		Map<String, SBean.GiftPack> specialPacks = new TreeMap<>();
		Map<Integer, Map<Integer, SBean.GiftPack>> giftPacks = new TreeMap<>();
		public GiftPackageConfig(int time, SBean.GiftPackage giftpackage)
		{
			super(CONFIG_TYPE_GIFT_PACKAGE, time);
			this.giftpackage = giftpackage;
			for (SBean.SpecialPack e : giftpackage.specialPacks)
			{
				specialPacks.put(e.shorcode, new SBean.GiftPack(e.maxUse, e.channel, e.time, e.pack, e.levelNeed, e.vipLevelNeed));
			}
			for (SBean.BatchPacks e : giftpackage.giftPacks)
			{
				Map<Integer, SBean.GiftPack> packs = new TreeMap<>();
				for (SBean.SequencePack ee : e.packs)
				{
					packs.put(ee.seq, new SBean.GiftPack(e.maxUse, e.channel, ee.time, ee.pack, e.levelNeed, e.vipLevelNeed));
				}
				this.giftPacks.put(e.batch, packs);
			}
		}

		public boolean getOpenConf()
		{
			return this.giftpackage.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.giftpackage.time;
		}

		public String getTitle()
		{
			return giftpackage.title;
		}
		public String getContent()
		{
			return giftpackage.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return null;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return false;
		}

		public SBean.GiftPackage getInnerConfig()
		{
			return this.giftpackage;
		}

		public SBean.GiftPack getShortGiftPack(String shortcode)
		{
			return this.specialPacks.get(shortcode);
		}
		
		public SBean.GiftPack getGiftPack(int batch, int seq)
		{
			Map<Integer, SBean.GiftPack> giftPacks = this.giftPacks.get(batch);
			return giftPacks == null ? null : giftPacks.get(seq);
		}
		

		public void checkValid() throws Exception
		{
			checkGiftPackage(this.giftpackage);
		}
		boolean isExclusion(GameConfig other)
		{
			return true;
		}

		public static void checkGiftPackage(SBean.GiftPackage giftpackage) throws Exception
		{
			checkActivityBase(giftpackage.time, giftpackage.title, giftpackage.content);
			Set<Integer> set = new TreeSet<>();
			for (SBean.BatchPacks e : giftpackage.giftPacks)
			{
				if (!set.add(e.batch))
					throw new Exception("gift package batch id " + e.batch + " is duplicate!");
				checkBatchPacksValid(giftpackage.time, e);
			}
			
			Set<String> shortCodes = new TreeSet<>();
			for (SBean.SpecialPack e : giftpackage.specialPacks)
			{
			    String shortCode = e.shorcode.trim();
			    if ("".equals(shortCode))
			    {
					throw new Exception("special package code is not set!");
			    }
			    
			    if (shortCode.length()>=16)
			    {
					throw new Exception("special package code "+ shortCode + " is beyond size!");
			    }
			    
			    if (!shortCodes.add(shortCode))
			    {
					throw new Exception("special package code " + e.shorcode + " is duplicate!");
			    }
			    
			    checkSpecialPacksValid(giftpackage.time, e);
			}
		}
		
		public static void checkSpecialPacksValid(SBean.TimeSpan baseTime, SBean.SpecialPack specialPacks) throws Exception
		{
			if (!GameConfig.checkTimeSpanValid(specialPacks.time))
				throw new Exception("activity time " + GameConfig.getTimeSpanStr(baseTime) + " is invalid!");
			
			if (!GameConfig.checkTimeSpanInclusive(baseTime, specialPacks.time))
				throw new Exception("special pack " + specialPacks.shorcode + " valid time " + GameConfig.getTimeSpanStr(specialPacks.time) + " is not in base activity time " + GameConfig.getTimeSpanStr(baseTime));
		    
		    if (specialPacks.maxUse <= 0 && specialPacks.maxUse != -1)
		    {
				throw new Exception("special packs " + specialPacks.shorcode + " max use count " + specialPacks.maxUse + " is invalid");
		    }
		    
		    if (specialPacks.levelNeed < 0 || specialPacks.vipLevelNeed < 0)
		    {
				throw new Exception("special packs " + specialPacks.shorcode + " level or viplevel config error!");
		    }
		    
		    if (specialPacks.pack.gifts.isEmpty())
		    {
				throw new Exception("special packs " + specialPacks.shorcode + " packs is empty!");
		    }
		    
		    Set<Integer> goodSet = new TreeSet<>();
		    for (SBean.DummyGoods good : specialPacks.pack.gifts)
		    {
		        if (!goodSet.add(good.id))
		        {
					throw new Exception("special gift package code  " + specialPacks.shorcode + " dummygood " + good.id + " is duplicate!");
		        }
		        
				if (!GameData.getInstance().checkEntityIdValid(good.id))
				{
					throw new Exception("special gift package code " + specialPacks.shorcode + "  gift item id " + good.id + " is invalid !");
				}
				if (good.count <= 0)
				{
					throw new Exception("special gift package code  " + specialPacks.shorcode + " dummygood " + good.id + " count below than 0!");
				}
		    }
		}

		public static void checkBatchPacksValid(SBean.TimeSpan baseTime, SBean.BatchPacks batchPacks) throws Exception
		{
			if (batchPacks.maxUse < 0)
				throw new Exception("batch packs " + batchPacks.batch + " max use count " + batchPacks.maxUse + " is invalid");
			if (batchPacks.packs.isEmpty())
				throw new Exception("batch packs " + batchPacks.batch + " sequence packs is empty!");
			Set<Integer> set = new TreeSet<>();
			for (SBean.SequencePack e : batchPacks.packs)
			{
				if (!set.add(e.seq))
					throw new Exception("gift package batch  " + batchPacks.batch + " sequence pack " + e.seq + " is duplicate!");
				checkSequencePacksValid(baseTime, e);
			}
		}

		public static void checkSequencePacksValid(SBean.TimeSpan baseTime, SBean.SequencePack sequencePack) throws Exception
		{
			if (!GameConfig.checkTimeSpanInclusive(baseTime, sequencePack.time))
				throw new Exception("sequence pack " + sequencePack.seq + " valid time " + GameConfig.getTimeSpanStr(sequencePack.time) + " is not in base activity time " + GameConfig.getTimeSpanStr(baseTime));
			checkPackValid(sequencePack.seq, sequencePack.pack);
		}

		public static void checkPackValid(int seq, SBean.Pack pack) throws Exception
		{
//			if (pack.title.equals(""))
//				throw new Exception("sequence pack " + seq + " title is not set !");
//			if (pack.content.equals(""))
//				throw new Exception("sequence pack " + seq + " content is not set !");
			for (SBean.DummyGoods e : pack.gifts)
			{
				if (!GameData.getInstance().checkEntityIdValid(e.id))
					throw new Exception("sequence pack " + seq + " : gift item id " + e.id + " is invalid !");
				if (e.count <= 0)
					throw new Exception("sequence pack " + seq + " : gift item count " + e.count + " is invalid !");
			}
		}
	}

	public class GiftPackageConfigImpl extends ActivityConfigImpl<GiftPackageConfig>
	{
		private Map<Integer, SBean.DBGiftPack> data = new HashMap<>();
		public GiftPackageConfigImpl()
		{

		}

		public Class<GiftPackageConfig> getConfigClassType()
		{
			return GiftPackageConfig.class;
		}

		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.giftPackageConfFileName;
		}

		public synchronized void init(Map<Integer, SBean.DBGiftPack> data)
		{
			this.data = data;
		}
		
		public synchronized Map<Integer, SBean.DBGiftPack> toDB()
		{
			return ket.util.Stream.clone(this.data);
		}
		
		public synchronized boolean tryUseSpecialPack(int id, String shortcode, int maxUse)
		{
			SBean.DBGiftPack db = data.get(id);
			if (db == null)
			{
				db = new SBean.DBGiftPack(id, new TreeMap<>());
				data.put(id, db);
			}
			int curUse = db.useCount.getOrDefault(shortcode, 0);
			if (curUse >= maxUse)
				return false;
			db.useCount.put(shortcode, curUse+1);
			return true;
		}
		
		public List<GiftPackageConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<GiftPackageConfig> cfgs = new ArrayList<>();
			List<SBean.GiftPackage> rawcfgs = parseGiftPackageConfigs(element);
			for (SBean.GiftPackage e : rawcfgs)
			{
				cfgs.add(new GiftPackageConfig(time, e));
			}
			return cfgs;
		}

		List<SBean.GiftPackage> parseGiftPackageConfigs(XmlElement root) throws Exception
		{
			List<SBean.GiftPackage> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("giftpackage"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					List<SBean.SpecialPack> lsts = parseSpecialPackssConfig(relativeTime, e);
					List<SBean.BatchPacks> lstb = parseBatchPacksListConfig(relativeTime, e);
					cfgs.add(new SBean.GiftPackage(open ? 1 : 0, timeSpan, title, content, lsts, lstb));
				}
			}
			return cfgs;
		}

		List<SBean.SpecialPack> parseSpecialPackssConfig(int relativeTime, XmlElement root) throws Exception
		{
			List<SBean.SpecialPack> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("special"))
				{
					lst.add(parseSpecialPackConfig(relativeTime, ee));
				}
			}
			return lst;
		}
		List<SBean.BatchPacks> parseBatchPacksListConfig(int relativeTime, XmlElement root) throws Exception
		{
			List<SBean.BatchPacks> lst = new ArrayList<>();
			if (root != null)
			{
				for (XmlElement ee : root.getChildrenByName("batch"))
				{
					lst.add(parseBathPacksConfig(relativeTime, ee));
				}
			}
			return lst;
		}

		SBean.SpecialPack parseSpecialPackConfig(int relativeTime, XmlElement root) throws Exception
		{
			String code = root.getStringAttribute("code").toLowerCase();
			int maxUse = root.getIntegerAttribute("maxuse");
			String channel = root.getStringAttribute("channel", "");
			Set<String> channelsSet = new HashSet<>(); 
			if (!channel.isEmpty())
			{
				channelsSet.addAll(Arrays.asList(channel.split(";")));
			}
			int levelreq = root.getIntegerAttribute("levelreq", 1);
			int vipreq = root.getIntegerAttribute("vipreq", 0);
			SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(root, "timespan");
			int baseTime = timeSpan.startTime;
			timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
			SBean.Pack pack = parsePackConfig(root.getChildByName("pack"));
			return new SBean.SpecialPack(code, maxUse, channelsSet, timeSpan, pack, levelreq, vipreq);
		}
		
		SBean.BatchPacks parseBathPacksConfig(int relativeTime, XmlElement root) throws Exception
		{
			int batch = root.getIntegerAttribute("batch");
			int maxUse = root.getIntegerAttribute("maxuse");
			String channel = root.getStringAttribute("channel", "");
			Set<String> channelsSet = new HashSet<>(); 
			if (!channel.isEmpty())
			{
				channelsSet.addAll(Arrays.asList(channel.split(";")));
			}
			int levelreq = root.getIntegerProperty("levelreq", 1);
			int vipreq = root.getIntegerProperty("vipreq", 0);
			List<SBean.SequencePack> sequencePacks = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("sequence"))
			{
				sequencePacks.add(parseSequencePackConfig(relativeTime, e));
			}
			return new SBean.BatchPacks(batch, maxUse, channelsSet, sequencePacks, levelreq, vipreq);
		}

		SBean.SequencePack parseSequencePackConfig(int relativeTime, XmlElement root) throws Exception
		{
			int seq = root.getIntegerAttribute("seq");
			SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(root, "timespan");
			int baseTime = timeSpan.startTime;
			timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
			SBean.Pack pack = parsePackConfig(root.getChildByName("pack"));
			return new SBean.SequencePack(seq, timeSpan, pack);
		}

		SBean.Pack parsePackConfig(XmlElement root) throws Exception
		{
			String title = root.getChildText("title");
			String content = root.getChildText("content");
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return new SBean.Pack(title, content, gifts);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class DailyPayGiftConfig extends ActivityConfig
	{
		SBean.DailyPayGift dailypaygift;
		public DailyPayGiftConfig(int time, SBean.DailyPayGift dailypaygift)
		{
			super(CONFIG_TYPE_DAILY_PAY_GIFT, time);
			this.dailypaygift = dailypaygift;
		}
		
		public boolean getOpenConf()
		{
			return this.dailypaygift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.dailypaygift.time;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return dailypaygift.phase;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoDailyPayGiftLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeDailyPayGift(this, now);
		}

		@Override
		String getTitle()
		{
			return this.dailypaygift.gifts.get(GameTime.getTimeH0()) == null ? null : this.dailypaygift.gifts.get(GameTime.getTimeH0()).title;
		}

		@Override
		String getContent()
		{
			return "";
		}
		
		public SBean.DailyPayGift getInnerConfig()
		{
			return this.dailypaygift;
		}
		
		public SBean.RoleDailyPayGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleDailyPayGiftCfg(this.getRoleEffectiveTimeSpan(role.createTime), dailypaygift.gifts);
		}
		
		public void checkValid() throws Exception
		{
			checkDailyPayGift(this.dailypaygift);
		}
		
		public static void checkDailyPayGift(SBean.DailyPayGift dailypaygift) throws Exception
		{
			checkActivityRoleParticipationDaySpan(dailypaygift.phase);
			for (SBean.DayPayGift daygift: dailypaygift.gifts.values())
				checkActivityBase(dailypaygift.time, daygift.title, daygift.content);
			checkGiftsValid(dailypaygift.gifts);
		}
		
		public static void checkGiftsValid(Map<Integer, SBean.DayPayGift> gifts) throws Exception
		{
			if (gifts == null)
				throw new Exception("daily pay gift : big gift empty is invalid !");
			for (SBean.DayPayGift daygift : gifts.values())
			{
				if (!GameData.getInstance().checkEntityIdValid(daygift.biggift.id))
					throw new Exception("daily pay gift : biggift item id " + daygift.biggift.id + " is invalid !");
				if (daygift.biggift.count <= 0)
					throw new Exception("daily pay gift : biggift item count " + daygift.biggift.count + " is invalid !");
				if (daygift.gifts.size() > DAILY_PAY_GIFT_MAX_GIFTS_COUNT)
					throw new Exception("daily pay gift : gifts type count " + gifts.size() + " is invalid !");
				for (SBean.DummyGoods e : daygift.gifts)
				{
					if (!GameData.getInstance().checkEntityIdValid(e.id))
						throw new Exception("daily pay gift : gift item id " + e.id + " is invalid !");
					if (e.count <= 0)
						throw new Exception("daily pay gift : gift item count " + e.count + " is invalid !");
				}
			}
		}
	}
	
	public class DailyPayGiftConfigImpl extends ActivityConfigImpl<DailyPayGiftConfig>
	{
		public DailyPayGiftConfigImpl()
		{
			
		}
		
		public Class<DailyPayGiftConfig> getConfigClassType()
		{
			return DailyPayGiftConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.dailyPayGiftConfFileName;
		}
		
		public List<DailyPayGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<DailyPayGiftConfig> cfgs = new ArrayList<>();
			List<SBean.DailyPayGift> rawcfgs = parseDailyPayGiftConfigs(element);
			for (SBean.DailyPayGift e : rawcfgs)
			{
				cfgs.add(new DailyPayGiftConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.DailyPayGift> parseDailyPayGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.DailyPayGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("dailypaygift"))
				{
					Map<Integer, SBean.DayPayGift> daypaygiftMap = new HashMap<>();
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					for (XmlElement daycfg : e.getChildrenByName("activityday"))
					{
						String title = daycfg.getChildText("title");
						String content = daycfg.getChildText("content");
						SBean.DayPayGift daypaygift = new SBean.DayPayGift(Integer.parseInt(daycfg.getChildAttribute("paynum", "num")), title, content, parseBigGiftConfig(daycfg), parseGiftsConfig(daycfg));
						int day = daycfg.getIntegerAttribute("day");
						daypaygiftMap.put(day, daypaygift);
					}
					cfgs.add(new SBean.DailyPayGift(open ? 1 : 0, timeSpan, daySpan, daypaygiftMap));
				}
			}
			return cfgs;
		}

		SBean.DummyGoods parseBigGiftConfig(XmlElement root) throws Exception
		{
			SBean.DummyGoods biggift = null;
			XmlElement node = root.getChildByName("biggift");
			if (node != null)
			{
				int iid = node.getIntegerAttribute("iid");
				int icount = node.getIntegerAttribute("icount");
				biggift = new SBean.DummyGoods(iid, icount);
			}
			return biggift;
		}
		
		List<SBean.DummyGoods> parseGiftsConfig(XmlElement root) throws Exception
		{
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return gifts;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class GroupBuyConfig extends ActivityConfig
	{
		SBean.GroupBuy groupBuy;
		
		public GroupBuyConfig(int time, SBean.GroupBuy groupBuy)
		{
			super(CONFIG_TYPE_GROUP_BUY, time);
			this.groupBuy = groupBuy;
		}

		public SBean.GroupBuy getConfigData()
		{
			return this.groupBuy;
		}
		
		public boolean getOpenConf()
		{
			return this.groupBuy.open != 0;
		}
		
		@Override
		SBean.TimeSpan getTimeSpan()
		{
			return this.groupBuy.time;
		}

		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return null;
		}
		
        @Override
        String getTitle()
        {
            return groupBuy.title;
        }

        @Override
        String getContent()
        {
            return groupBuy.content;
        }
        
		@Override
		void checkValid() throws Exception
		{
			checkGroupBuy(this.groupBuy);
		}
		
		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other) || GameConfig.checkTimeSpanIntersectant(this.getTimeSpan(), other.getTimeSpan());
		}
		
		boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}
		
		boolean isCanTakeRewards(Role role, int now)
		{
			return false;
		}
		
		public SBean.GroupBuyGoods getGroupBuyGoods(int gid)
		{
			for(SBean.GroupBuyGoods g: this.groupBuy.goods)
			{
				if(g.id == gid)
					return g;
			}
			
			return null;
		}
		
		public boolean isCrossServer()
		{
			return groupBuy.crossserver == 1;
		}
		
		public boolean isInBuyTime(int now)
		{
			return now >= groupBuy.time.startTime && now < groupBuy.buyEndTime;
		}
		
		public int getReturnTime()
		{
			return groupBuy.buyEndTime;
		}
		
		public static void checkGroupBuy(SBean.GroupBuy groupBuy) throws Exception
		{
			checkGroupBuyBase(groupBuy);
			checkGoodsListValid(groupBuy.goods);
		}
		
		static void checkGroupBuyBase(SBean.GroupBuy groupBuy) throws Exception
		{
			checkActivityBase(groupBuy.time, groupBuy.title, groupBuy.content);
			
			if(groupBuy.buyEndTime <= groupBuy.time.startTime || groupBuy.buyEndTime >= groupBuy.returnTime)
				throw new Exception("group buy activity buy end time " + GameTime.getDateTimeStampStr(groupBuy.buyEndTime) + " is invalid !");
			
			//返还时间至少在购买结束时间半小时之后
			if(groupBuy.returnTime <= (groupBuy.buyEndTime + 30 * 60) || groupBuy.returnTime >= groupBuy.time.endTime)
				throw new Exception("group buy activity return time " + GameTime.getDateTimeStampStr(groupBuy.returnTime) + " is invalid !");
		}
		
		static void checkGoodsListValid(List<SBean.GroupBuyGoods> goodsList) throws Exception
		{
			Set<Integer> goodsIDs = new HashSet<>();
			for(SBean.GroupBuyGoods g: goodsList)
			{
				checkGoodsValid(g);
				if(!goodsIDs.add(g.id))
					throw new Exception("group buy activity goods id " + g.id + " is duplicate !");
				
				int discount = 10;
				int countReq = 0;
				for(SBean.GroupBuyDiscount d: g.discounts)
				{
					if(d.discount >= discount || d.discount >= 10)
						throw new Exception("group buy activity goods id " + g.id + " discount " + d.discount + " invalid");
					
					if(d.countReq <= countReq)
						throw new Exception("group buy activity goods id " + g.id + " countReq " + d.countReq + " invalid");
					
					discount = d.discount;
					countReq = d.countReq;
				}
			}
		}
		
		static void checkGoodsValid(SBean.GroupBuyGoods goods) throws Exception
		{
			if (!GameData.getInstance().checkBagItemIdValid(goods.iid))
				throw new Exception("group buy activity goods id " + goods.id + " : bag item id " + goods.iid + " is invalid !");
			
			if (goods.icount <= 0)
				throw new Exception("group buy activity goods id " + goods.id + " : bag item count " + goods.icount + " is invalid !");
			
			if (goods.price <= 0)
				throw new Exception("group buy activity goods id " + goods.id + " : price " + goods.price + " is invalid !");
			
			if (goods.vipReq < 0)
				throw new Exception("group buy activity goods id " + goods.id + " : vipReq " + goods.vipReq + " is invalid !");
			
			if (goods.levelReq < 0)
				throw new Exception("group buy activity goods id " + goods.id + " : levelReq " + goods.levelReq + " is invalid !");
			
			if(goods.discounts.isEmpty())
				throw new Exception("group buy activity goods id " + goods.id + " : discounts is empty !");
			
			int lastCountReq = 0;
			int lastDiscount = 10;
			for(int seq = 0; seq < goods.discounts.size(); seq++)
			{
				SBean.GroupBuyDiscount d = goods.discounts.get(seq);
				if(d.countReq <= lastCountReq)
					throw new Exception("group buy activity goods id " + goods.id + " : seq " + seq + " countReq " + d.countReq + " is valid!");
			
				if(d.discount >= lastDiscount)
					throw new Exception("group buy activity goods id " + goods.id + " : seq " + seq + " discount " + d.discount + " is valid!");
				
				lastCountReq = d.countReq;
				lastDiscount = d.discount;
			}
			
			if(goods.restriction != null)
			{
				if(goods.restriction.times <= 0)
					throw new Exception("group buy activity goods id " + goods.id + " : restriction times " + goods.restriction.times + " is invalid !");
			}
		}
	}
	
	public class GroupBuyConfigImpl extends GameConfigImpl<GroupBuyConfig>
	{
		private final static int GROUPBUY_UPDATE_INTERVAL 	= 60;
		Map<Integer, GroupBuyData> data = new HashMap<>();
		
		@Override
		Class<GroupBuyConfig> getConfigClassType()
		{
			return GroupBuyConfig.class;
		}

		@Override
		String getConfigFileName(Config cfg)
		{
			return cfg.groupBuyConfFileName;
		}

		@Override
		List<GroupBuyConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<GroupBuyConfig> cfgs = new ArrayList<>();
			List<SBean.GroupBuy> rawcfgs = parseGroupBuyConfigs(element);
			for(SBean.GroupBuy e: rawcfgs)
			{
				cfgs.add(new GroupBuyConfig(time, e));
			}
			return cfgs;
		}
		
		@Override
		protected void onConfigsChanged()
		{
//			List<GroupBuyConfig> cfgs = groupBuyConf.getOpenedConfigs(GameTime.getTime());
//			if(!cfgs.isEmpty())
//			{
//				for(GroupBuyData e: data.values())
//					e.onConfChange(cfgs.get(0));
//			}
		}
		
		List<SBean.GroupBuy> parseGroupBuyConfigs(XmlElement root) throws Exception
		{
			List<SBean.GroupBuy> groupBuys = new ArrayList<>();
			if(root.getName().equals("activity"))
			{
				for(XmlElement e: root.getChildrenByName("groupbuy"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					int buyEndTime = GameConfig.getConfRealTime(relativeTime, baseTime, e.getTimeProperty("buyendtime", 0));
					int returnTime = GameConfig.getConfRealTime(relativeTime, baseTime, e.getTimeProperty("returntime", 0));
					boolean crossServer = e.getBooleanProperty("crossserver", false);
					List<SBean.GroupBuyGoods> goodList = parseGroupBuyGoodsListConfig(e, relativeTime, baseTime);
					
					groupBuys.add(new SBean.GroupBuy(open ? 1 : 0, timeSpan, title, content, buyEndTime, returnTime, crossServer ? (byte) 1 : 0, goodList));
				}
			}
			return groupBuys;
		}
		
		List<SBean.GroupBuyGoods> parseGroupBuyGoodsListConfig(XmlElement root, int relativeTime, int baseTime) throws Exception
		{
			List<SBean.GroupBuyGoods> goodList = new ArrayList<>();
			if(root != null)
			{
				for(XmlElement e: root.getChildrenByName("goods"))
				{
					goodList.add(parseGroupBuyGoodsConfig(e, relativeTime, baseTime));
				}
			}
			
			return goodList;
		}
		
		SBean.GroupBuyGoods parseGroupBuyGoodsConfig(XmlElement root, int relativeTime, int baseTime) throws Exception
		{
			int id = root.getIntegerAttribute("id");
			int iid = root.getIntegerAttribute("iid");
			int icount = root.getIntegerAttribute("icount");
			int price = root.getIntegerAttribute("price");
			
			int vipReq = root.getIntegerProperty("vipreq", 0);
			int levelReq = root.getIntegerProperty("levelreq", 0);
			
			List<SBean.GroupBuyDiscount> discounts = parseGroupBuyDiscountConfigs(root.getChildByName("discount"));
			SBean.GroupBuyRestriction restriction = null;
			XmlElement rnode = root.getChildByName("restriction");
			if(rnode != null)
			{
				int times = rnode.getIntegerAttribute("times", 0);
				boolean dayRefresh = rnode.getBooleanAttribute("dayrefesh", false);
				restriction = new SBean.GroupBuyRestriction(times, dayRefresh ? (byte ) 1 : 0);
			}
			
			return new SBean.GroupBuyGoods(id, iid, icount, price, vipReq, levelReq, discounts, restriction);
		}
		
		List<SBean.GroupBuyDiscount> parseGroupBuyDiscountConfigs(XmlElement root) throws Exception
		{
			List<SBean.GroupBuyDiscount> discounts = new ArrayList<>();
			for(XmlElement e: root.getChildrenByName("seq"))
			{
				discounts.add(parseGroupBuyDiscountConfig(e));
			}
			return discounts;
		}
		
		SBean.GroupBuyDiscount parseGroupBuyDiscountConfig(XmlElement root) throws Exception
		{
			int countReq = root.getIntegerAttribute("countreq");
			int discount = root.getIntegerAttribute("discount");
			return new SBean.GroupBuyDiscount(countReq, discount);
		}
		
		GroupBuyConfig getCurOpenedCfg(int now)
		{
			List<GroupBuyConfig> cfgs = groupBuyConf.getOpenedConfigs(now);
			return cfgs.isEmpty() ? null : cfgs.get(0);
		}
		
		void init(Map<Integer, SBean.DBGroupBuy> dbGroupBuy)
		{
			if(dbGroupBuy != null)
			{
				for(SBean.DBGroupBuy e: dbGroupBuy.values())
				{
					data.put(e.id, new GroupBuyData(e.id).init(e));
				}
			}
		}
		
		synchronized boolean onTimer(int timeTick)
		{
			if(timeTick % GROUPBUY_UPDATE_INTERVAL == 0)
			{
				GroupBuyConfig cfg = getCurOpenedCfg(timeTick);
				if(cfg != null && data.containsKey(cfg.getId()))
				{
					return data.get(cfg.getId()).onTimer(timeTick, cfg);
				}
			}
			
			return false;
		}
		
		synchronized Map<Integer, SBean.DBGroupBuy> toDB()
		{
			Map<Integer, SBean.DBGroupBuy> dbData = new HashMap<>();
			for(Map.Entry<Integer, GroupBuyData> e: data.entrySet())
			{
				if(e.getValue().needSave())
					dbData.put(e.getKey(), e.getValue().toDB());
			}
			return dbData;
		}
		
		synchronized int updateBuyLogs(int activityID, int rid, int gid, int count)
		{
			GroupBuyData g = data.get(activityID);
			if(g == null)
			{
				g = new GroupBuyData(activityID);
				data.put(activityID, g);
			}
			
			return g.updateBuyLogs(rid, gid, count);
		}
		
		synchronized void syncGlobalBuyLog(int activityID, Map<Integer, Integer> log)
		{
			GroupBuyData g = data.get(activityID);
			if(g == null)
			{
				g = new GroupBuyData(activityID);
				data.put(activityID, g);
			}
			
			g.syncGlobalBuyLog(activityID, log);
		}
		
		synchronized void syncAuctionGroupBuyLog(int activityID, int endTime)
		{
			GroupBuyData g = data.get(activityID);
			if(g != null)
				g.syncAuctionGroupBuyLog(activityID, endTime);
		}
		
		synchronized Map<Integer, Integer> getBuyCount(int activityID)
		{
			GroupBuyData g = data.get(activityID);
			return g == null ? GameData.emptyMap() : g.getBuyCount();
		}
	}
	
	public class GroupBuyData
	{
		final int id;
		Map<Integer, Integer> buyLogs;
		Set<Integer> buyRoles;
		Map<Integer, Integer> globalBuyLos;
		boolean finishReturn = false;
		
		GroupBuyData(int id)
		{
			this.id = id;
			this.buyLogs = new HashMap<>();
			this.buyRoles = new HashSet<>();
			this.globalBuyLos = new HashMap<>();
		}
		
		GroupBuyData init(SBean.DBGroupBuy dbGroupBuy)
		{
			if(dbGroupBuy != null)
			{
				this.buyLogs = dbGroupBuy.buyLogs;
				this.buyRoles = dbGroupBuy.buyRoles;
			}
			return this;
		}
		
		synchronized int getGoodsBuyCount(int gid)
		{
			int count = this.buyLogs.getOrDefault(gid, 0);
			int globalCount = this.globalBuyLos.getOrDefault(gid, 0);
			return globalCount > count ? globalCount : count; 
		}
		
		boolean onTimer(int timeTick, GroupBuyConfig cfg)
		{
			return update(timeTick, cfg);
		}
		
		private boolean update(int timeTick, GroupBuyConfig cfg)
		{
			return tryDoReturn(timeTick, cfg);
		}
		
		private boolean tryDoReturn(int timeTick, GroupBuyConfig cfg)
		{
			if(timeTick > cfg.getReturnTime() && !finishReturn)
			{
				int index = 500;
				Iterator<Integer> it = this.buyRoles.iterator();
				while(it.hasNext() && index > 0)
				{
					int rid = it.next();
					gs.getLoginManager().exeCommonRoleVisitor(rid, false, new LoginManager.CommonRoleVisitor()
					{
						@Override
						public boolean visit(Role role, Role sameUserRole)
						{
							SBean.DBRoleGroupBuyLog log = role.groupBuyLogs.get(GroupBuyData.this.id);
							if(log != null)
							{
								for(SBean.DBGoodsGroupBuyLog e: log.logs.values())
								{
									SBean.GroupBuyGoods goods = cfg.getGroupBuyGoods(e.id);
									if(goods == null)
										continue;
									
									int discount = getGroupBuyGoodsDiscount(goods.discounts, GroupBuyData.this.getGoodsBuyCount(e.id));
									if(discount < 0)
										continue;
									
									int discountPrice = (int) ((discount / 10.f) * goods.price);
									int returnDiamond = (goods.price - discountPrice) * e.allBuyTimes;
									List<SBean.GameItem> att = new ArrayList<>();
									att.add(GameData.getInstance().toGameItem(-GameData.COMMON_ITEM_ID_DIAMOND, returnDiamond));
									
//									gs.getLogger().debug("group buy goods [" + goods.id + ", " + goods.price + "]buy count " + GroupBuyData.this.getGoodsBuyCount(e.id) + " discount " + discount + " discountPrice " + discountPrice);
									List<Integer> addinfo = new ArrayList<>();
									addinfo.add(goods.iid);
									addinfo.add(goods.price);
									addinfo.add(discountPrice);
									addinfo.add(e.allBuyTimes);
									addinfo.add(returnDiamond);
									
									MailBox mailbox = role.getMailBox();
									mailbox.addSysMail(MailBox.SysMailType.GroupBuyReturn, MailBox.GROUPBUY_RETURN_MAIL_MAX_RESERVE_TIME, "", att, addinfo);
								}
							}
							return true;
						}
						
						@Override
						public void onCallback(boolean success)
						{
							gs.getLogger().debug("try mail role " + rid + " group buy activity discount return " + ( success ? " success !" : " failed !"));
						}
					});
					
					it.remove();
					index--;
				}
				
				finishReturn = this.buyRoles.isEmpty();
				return true;
			}
			return false;
		}
		
		int getGroupBuyGoodsDiscount(List<SBean.GroupBuyDiscount> discounts, int buyCount)
		{
			if(buyCount <= 0 || discounts.isEmpty())
				return -1;
			
			for(int i = discounts.size(); i > 0; i--)
			{
				if(buyCount >= discounts.get(i - 1).countReq)
					return discounts.get(i - 1).discount;
			}
			
			return -1;
		}
		
		int updateBuyLogs(int rid, int gid, int count)
		{
			this.buyRoles.add(rid);
			this.buyLogs.compute(gid, (k,v) -> v == null ? count : v + count);
			return this.buyLogs.get(gid);
		}
		
		void syncGlobalBuyLog(int activityID, Map<Integer, Integer> log)
		{
			if(this.id == activityID)
			{
				this.globalBuyLos = log;
			}
		}
		
		boolean needSave()
		{
			return !finishReturn;
		}
		
		SBean.DBGroupBuy toDB()
		{
			return new SBean.DBGroupBuy(this.id, new HashMap<>(buyLogs), new HashSet<>(buyRoles));
		}
		
		Map<Integer, Integer> getBuyCount()
		{
			Map<Integer, Integer> log = new HashMap<>(this.buyLogs);
			for(Map.Entry<Integer, Integer> e: this.globalBuyLos.entrySet())
			{
				int count = log.getOrDefault(e.getKey(), 0);
				if(e.getValue() > count)
					log.put(e.getKey(), e.getValue());
			}
			
			return log;
		}
		
		void syncAuctionGroupBuyLog(int activityID, int endTime)
		{
			if(activityID != this.id || this.buyLogs.isEmpty())
				return;
			
			gs.getRPCManager().notifyAuctionSyncGroupBuyLog(activityID, endTime, new HashMap<>(this.buyLogs));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class PayRankConfig extends ActivityConfig
    {
        SBean.PayRank payRank;

        public PayRankConfig(int time, SBean.PayRank payRank)
        {
            super(CONFIG_TYPE_PAY_RANK, time);
            this.payRank = payRank;
        }

        public SBean.RolePayRankCfg getConfigData()
        {
            return new SBean.RolePayRankCfg(this.getId(), this.getTimeSpan(), this.payRank.title, this.payRank.content, this.payRank.recordEndTime, this.payRank.rankList);
        }

        public boolean getOpenConf()
        {
            return this.payRank.open != 0;
        }

        @Override
        SBean.TimeSpan getTimeSpan()
        {
            return this.payRank.time;
        }

        public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
        {
            return null;
        }

        @Override
        String getTitle()
        {
            return payRank.title;
        }

        @Override
        String getContent()
        {
            return payRank.content;
        }

        @Override
        void checkValid() throws Exception
        {
            checkPayRank(this.payRank);
        }

        public boolean isExclusion(GameConfig other)
        {
            return super.isExclusion(other) || GameConfig.checkTimeSpanIntersectant(this.getTimeSpan(), other.getTimeSpan());
        }

        boolean isNoRewardsLeft(Role role, int now)
        {
            return false;
        }

        boolean isCanTakeRewards(Role role, int now)
        {
            return false;
        }

        public boolean isInRecordTime(int now)
        {
            return now >= payRank.time.startTime && now < payRank.recordEndTime;
        }

        public int getReturnTime()
        {
            return payRank.recordEndTime;
        }

        public static void checkPayRank(SBean.PayRank payRank) throws Exception
        {
            checkPayRankBase(payRank);
            checkGoodsListValid(payRank.rankList);
        }

        static void checkPayRankBase(SBean.PayRank payRank) throws Exception
        {
            checkActivityBase(payRank.time, payRank.title, payRank.content);

            if(payRank.time.endTime <= payRank.time.startTime || payRank.recordEndTime >= payRank.time.endTime)
                throw new Exception("pay rank activity record end time " + GameTime.getDateTimeStampStr(payRank.recordEndTime) + " is invalid !");

        }

        static void checkGoodsListValid(List<SBean.PayLevelRank> payLevelRanks) throws Exception
        {
            for (SBean.PayLevelRank paylevelRank : payLevelRanks)
            {
                Set<Integer> goodsIDs = new HashSet<>();
                for(SBean.DummyGoods g: paylevelRank.gifts)
                {
                    checkGoodsValid(g);
                    if(!goodsIDs.add(g.id))
                        throw new Exception("pay rank activity goods id " + g.id + " is duplicate !");
                }
            }
        }

        static void checkGoodsValid(SBean.DummyGoods goods) throws Exception
        {
            if (!GameData.getInstance().checkBagItemIdValid(goods.id))
                throw new Exception("pay rank activity goods id " + goods.id + " : bag item id " + goods.id + " is invalid !");

            if (goods.count <= 0)
                throw new Exception("pay rank activity goods id " + goods.id + " : bag item count " + goods.count + " is invalid !");
        }
    }

    public class PayRankConfigImpl extends GameConfigImpl<PayRankConfig>
    {
        private final static int PAYRANK_UPDATE_INTERVAL 	= 60;
        Map<Integer, PayActivityRankData> data = new HashMap<>();

        @Override
        Class<PayRankConfig> getConfigClassType()
        {
            return PayRankConfig.class;
        }

        @Override
        String getConfigFileName(Config cfg)
        {
            return cfg.payRankConfFileName;
        }

        @Override
        List<PayRankConfig> parseConfigs(int time, XmlElement element) throws Exception
        {
            List<PayRankConfig> cfgs = new ArrayList<>();
            List<SBean.PayRank> rawcfgs = parsePayRankConfigs(element);
            for(SBean.PayRank e: rawcfgs)
            {
                cfgs.add(new PayRankConfig(time, e));
            }
            return cfgs;
        }

        @Override
        protected void onConfigsChanged()
        {
            //			List<GroupBuyConfig> cfgs = groupBuyConf.getOpenedConfigs(GameTime.getTime());
            //			if(!cfgs.isEmpty())
            //			{
            //				for(GroupBuyData e: data.values())
            //					e.onConfChange(cfgs.get(0));
            //			}
        }

        List<SBean.PayRank> parsePayRankConfigs(XmlElement root) throws Exception
        {
            List<SBean.PayRank> payRanks = new ArrayList<>();
            if(root.getName().equals("activity"))
            {
                for(XmlElement e: root.getChildrenByName("payrank"))
                {
                    boolean open = e.getBooleanAttribute("open");
                    int relativeTime = e.getIntegerAttribute("relativetime", 0);
                    relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
                    SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
                    int baseTime = timeSpan.startTime;
                    timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
                    String title = e.getChildText("title");
                    String content = e.getChildText("content");
                    int recordendtime = GameConfig.getConfRealTime(relativeTime, baseTime, e.getTimeProperty("recordendtime", 0));
                    XmlElement rankList = e.getChildByName("ranklist");
                    int maxLength = rankList.getIntegerAttribute("length");
                    int showCount = rankList.getIntegerAttribute("showcount");
                    List<SBean.PayLevelRank> payLevelRank = parsePayRankLevelListConfig(rankList);

                    payRanks.add(new SBean.PayRank(open? 1 : 0, timeSpan, title, content, recordendtime, maxLength, showCount, payLevelRank));
                }
            }
            return payRanks;
        }

        List<SBean.PayLevelRank> parsePayRankLevelListConfig(XmlElement root) throws Exception
        {
            List<SBean.PayLevelRank> levelList = new ArrayList<>();
            if(root != null)
            {
                for(XmlElement e: root.getChildrenByName("bonus"))
                {
                    int rank = e.getIntegerAttribute("rank");
                    List<SBean.DummyGoods> goods = new ArrayList<>();
                    for(XmlElement f: e.getChildrenByName("gift"))
                    {
                        int id = f.getIntegerAttribute("iid");
                        int count = f.getIntegerAttribute("icount");
                        goods.add(new SBean.DummyGoods(id, count));
                    }

                    levelList.add(new SBean.PayLevelRank(rank, goods));
                }
            }

            return levelList;
        }

        PayRankConfig getCurOpenedCfg(int now)
        {
            List<PayRankConfig> cfgs = payRankConf.getOpenedConfigs(now);
            return cfgs.isEmpty() ? null : cfgs.get(0);
        }

        void init(Map<Integer, SBean.DBActivityRank> dbpayRank)
        {
            if(dbpayRank != null)
            {
                for(SBean.DBActivityRank e: dbpayRank.values())
                {
                    data.put(e.id, new PayActivityRankData(e.id).init(e));
                }
            }
        }

        synchronized boolean onTimer(int timeTick)
        {
            if(timeTick % PAYRANK_UPDATE_INTERVAL == 0)
            {
                PayRankConfig cfg = getCurOpenedCfg(timeTick);
                if(cfg != null && data.containsKey(cfg.getId()))
                {
                    return data.get(cfg.getId()).onTimer(timeTick, cfg);
                }
            }
            return false;
        }

        synchronized Map<Integer, SBean.DBActivityRank> toDB()
        {
            Map<Integer, SBean.DBActivityRank> dbData = new HashMap<>();
            for(Map.Entry<Integer, PayActivityRankData> e: data.entrySet())
            {
                if(e.getValue().needSave())
                    dbData.put(e.getKey(), e.getValue().toDB());
            }
            return dbData;
        }

        synchronized void updatePayRankLogs(int activityID, SBean.RankRole rank, PayRankConfig cfg)
        {
            PayActivityRankData g = data.get(activityID);
            if(g == null)
            {
                g = new PayActivityRankData(activityID);
                data.put(activityID, g);
            }
            g.tryUpdateRank(rank, cfg.payRank.maxLength);
        }

        synchronized  List<SBean.RankRole> getShowList(int activityID, PayRankConfig cfg)
        {
            PayActivityRankData g = data.get(activityID);
            if(g == null)
            {
                return new ArrayList<>();
            }
            return g.rank.getActivityRank(cfg.payRank.showCount);
        }

        synchronized int getRoleRank(int activityID, int rid)
        {
            PayActivityRankData g = data.get(activityID);
            if(g == null)
            {
                return 0;
            }
            return g.rank.getRoleRank(rid);
        }
    }

    public class PayActivityRankData
    {
        final int id;
        ActivityRank rank;
        final RankData.RankRoleReader RankRoleReader = new RankData.RankRoleReader();
        boolean finishReturn = false;
        int lastTipMailDay = 0;

        PayActivityRankData(int id)
        {
            this.id = id;
            this.rank = new ActivityRank(this.RankRoleReader);
        }

        PayActivityRankData init(SBean.DBActivityRank dbActivityRank)
        {
            rank.fromDB(dbActivityRank.ranks);
            finishReturn = this.rank.isEmpty();
            lastTipMailDay = dbActivityRank.lastTipMailDay;
            return this;
        }

        boolean onTimer(int timeTick, PayRankConfig cfg)
        {
            return update(timeTick, cfg);
        }

        private boolean update(int timeTick, PayRankConfig cfg)
        {
            return tryDoReturn(timeTick, cfg) || trySendTipMail(timeTick, cfg);
        }

        private boolean tryDoReturn(int timeTick, PayRankConfig cfg)
        {
            if(timeTick > cfg.getReturnTime() && !finishReturn)
            {
                int index = 1;
                for(long rankValue: this.rank.rankValues.descendingSet())
                {
                    int rid = this.rank.getItemId(rankValue);
                    final SBean.PayLevelRank levelRank = new SBean.PayLevelRank(0, new ArrayList<>());
                    for (SBean.PayLevelRank paylevelRank : cfg.payRank.rankList) {
                        if (index >= paylevelRank.rank) {
                            levelRank.rank = paylevelRank.rank;
                            levelRank.gifts = paylevelRank.gifts;
                        }
                    }
                    levelRank.rank = index;
                    gs.getLoginManager().exeCommonRoleVisitor(rid, false, new LoginManager.CommonRoleVisitor()
                    {
                        @Override
                        public boolean visit(Role role, Role sameUserRole)
                        {
                            List<SBean.GameItem> att = new ArrayList<>();
                            levelRank.gifts.forEach(dummyGoods -> {
                                att.add(GameData.getInstance().toGameItem(dummyGoods.id, dummyGoods.count));
                            });

                            List<Integer> addinfo = new ArrayList<>();
                            addinfo.add(levelRank.rank);

                            MailBox mailbox = role.getMailBox();
                            mailbox.addSysMail(MailBox.SysMailType.PayRankReward, MailBox.STELE_REWARD_MAIL_MAX_RESERVE_TIME, "", att, addinfo);
                            return true;
                        }

                        @Override
                        public void onCallback(boolean success)
                        {
                            gs.getLogger().debug("try mail role " + rid + " group buy activity discount return " + ( success ? " success !" : " failed !"));
                        }
                    });
                    index++;
                }
                finishReturn = this.rank.isEmpty();
                this.rank.clearRank();
                return true;
            }
            return false;
        }

        boolean trySendTipMail(int timeTick, PayRankConfig cfg)
        {
            int nowDay = GameTime.getDay(timeTick);
            if (nowDay != lastTipMailDay && !finishReturn) {
                lastTipMailDay = nowDay;
                int index = 1;
                for(long rankValue: this.rank.rankValues.descendingSet())
                {
                    int rid = this.rank.getItemId(rankValue);
                    final List<Integer> addinfo = new ArrayList<>();
                    addinfo.add(index);
                    gs.getLoginManager().exeCommonRoleVisitor(rid, false, new LoginManager.CommonRoleVisitor()
                    {
                        @Override
                        public boolean visit(Role role, Role sameUserRole)
                        {
                            List<SBean.GameItem> att = new ArrayList<>();
                            MailBox mailbox = role.getMailBox();
                            mailbox.addSysMail(MailBox.SysMailType.PayRankTip, MailBox.STELE_REWARD_MAIL_MAX_RESERVE_TIME, "", att, addinfo);
                            return true;
                        }

                        @Override
                        public void onCallback(boolean success)
                        {
                        }
                    });
                    index++;
                }
                return true;
            }
            return false;
        }

        void tryUpdateRank(SBean.RankRole role, int maxLength)
        {
            this.rank.tryUpdateRank(role, maxLength);
        }

        boolean needSave()
        {
            return !finishReturn;
        }

        SBean.DBActivityRank toDB()
        {
            return new SBean.DBActivityRank(this.id, lastTipMailDay, this.rank.toDB());
        }
    }

    public static class ActivityRank extends RankData.Ranks<SBean.RankRole>
    {
        public ActivityRank(RankData.RankItemReader<SBean.RankRole> reader)
        {
            super(reader);
        }

        boolean isEmpty()
        {
            return this.rankItems.isEmpty();
        }

        int getRoleRank(int roleID)
        {
            if(!this.rankItems.containsKey(roleID))
                return 0;

            return getRoleRankImpl(roleID);
        }

        int getRoleRankImpl(int queryRoleID)
        {
            int rank = 1;
            for(long rankValue: this.rankValues.descendingSet())
            {
                int rid = getItemId(rankValue);
                if(rid == queryRoleID)
                    return rank;
                rank++;
            }

            return 0;
        }

        List<SBean.RankRole> getActivityRank(int showCnt)
        {
            List<SBean.RankRole> showRank = new ArrayList<>();
            for(long rankValue: this.rankValues.descendingSet())
            {
                int rid = getItemId(rankValue);
                SBean.RankRole rr = this.rankItems.get(rid);
                if(rr != null)
                    showRank.add(rr.kdClone());

                if(showRank.size() == showCnt)
                    break;
            }

            return showRank;
        }

        public void tryUpdateRank(SBean.RankRole rankRole, int maxLength)
        {
            super.tryUpdateRank(rankRole, maxLength);
        }

        SBean.RankRole getRankRole(int roleID)
        {
            return this.rankItems.get(roleID);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class AdversConfig extends ActivityConfig
	{
	    public AdversConfig(int type, int time, SBean.Advers advers)
	    {
	        super(type, time);
	        this.advers = advers;
	    }

        @Override
        boolean getOpenConf()
        {
            return advers.open!=0;
        }

        @Override
        TimeSpan getTimeSpan()
        {
            return advers.time;
        }

		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return null;
		}
		
		public SBean.Advers getInnerConfig()
		{
			return this.advers;
		}
		
        @Override
        void checkValid() throws Exception
        {
            for (Integer icon : advers.icons)
            {
                if (!GameData.getInstance().getAdversBackgroundCfg().contains(icon))
                {
                    throw new Exception(" icon " + icon + " not exist" );
                }
            }
        }

        @Override
        String getTitle()
        {
            return advers.title;
        }

        @Override
        String getContent()
        {
            return advers.content;
        }

        @Override
        boolean isNoRewardsLeft(Role role, int now)
        {
            return false;
        }

        @Override
        boolean isCanTakeRewards(Role role, int now)
        {
            return false;
        }
        
        List<Integer> getIcons()
        {
            return advers.icons;
        }
        
        SBean.Advers advers;
	}
	
	public class AdversConfigImpl extends ActivityConfigImpl<AdversConfig>
	{

        @Override
        Class<AdversConfig> getConfigClassType()
        {
            return AdversConfig.class;
        }

        @Override
        String getConfigFileName(Config cfg)
        {
            return cfg.adversConfFileName;
        }

        @Override
        List<AdversConfig> parseConfigs(int time, XmlElement root) throws Exception
        {
            List<AdversConfig> advers = new ArrayList<>();
            if (root.getName().equals("activity"))
            {
                for (XmlElement e : root.getChildrenByName("advertising"))
                {
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					
					List<Integer>  icons = parseAdvers(e);
					
					advers.add(new AdversConfig(CONFIG_TYPE_ADVERTISING, baseTime, new SBean.Advers(open?1:0, time, timeSpan, title, content, icons)));
                }
            }
            return advers;
        }

        private List<Integer> parseAdvers(XmlElement adversEle) throws XmlNodeNotFoundException, XmlReadException
        {
            List<Integer> icons = new ArrayList<>();
            for (XmlElement e : adversEle.getChildrenByName("backgroud"))
            {
                Integer icon = e.getIntegerAttribute("icon");
                if (icon!=null)
                {
                    icons.add(icon);
                }
            }
            return icons;
        }
	    
	}
	
	public static class FlashSaleConfig extends ActivityConfig
	{
	    SBean.FlashSale flashSale;

		public FlashSaleConfig(int time, SBean.FlashSale groupBuy)
		{
			super(CONFIG_TYPE_FLASH_SALE, time);
			this.flashSale = groupBuy;
		}

		public boolean getOpenConf()
		{
			return this.flashSale.open != 0;
		}
		
	    @Override
	    SBean.TimeSpan getTimeSpan()
	    {
	        return flashSale.time;
	    }
	
        @Override
        String getTitle()
        {
            return flashSale.title;
        }

        @Override
        String getContent()
        {
            return flashSale.content;
        }
        
        public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return flashSale.phase;
		}
        
		public SBean.FlashSale getInnerConfig()
		{
			return this.flashSale;
		}
        
		public SBean.RoleFlashSaleCfg toRoleCfg(Role role)
		{
			return new SBean.RoleFlashSaleCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), flashSale.title, flashSale.content, flashSale.buyStartTime, flashSale.buyEndTime, flashSale.goods);
		}
        
	    @Override
	    void checkValid() throws Exception
	    {
            checkFlashSaleBase(flashSale);
	        checkAllGoodsExistsAndIconResource();
	    }
	    
		boolean isNoRewardsLeft(Role role, int now)
		{
			return false;
		}
		
		boolean isCanTakeRewards(Role role, int now)
		{
			return false;
		}
	    
		static void checkFlashSaleBase(SBean.FlashSale flashSale) throws Exception
		{
			checkActivityBase(flashSale.time, flashSale.title, flashSale.content);
			checkActivityRoleParticipationDaySpan(flashSale.phase);
			
			if(flashSale.buyStartTime <= flashSale.time.startTime)
				throw new Exception("flash sale activity buy end time " + GameTime.getDateTimeStampStr(flashSale.buyStartTime) + " is invalid !");
			
			if(flashSale.buyEndTime >= flashSale.time.endTime)
				throw new Exception("flash sale activity buy end time " + GameTime.getDateTimeStampStr(flashSale.buyEndTime) + " is invalid !");
			
			if(flashSale.buyStartTime >= flashSale.buyEndTime)
				throw new Exception("flash sale activity buy start time after than end time is invalid") ;
				
			if (flashSale.goods==null || flashSale.goods.size()<1)
			{
			    throw new Exception("flash sale activity goods is not config ! activity is invalid");
			}

			Set<Integer> goodSet = new HashSet<>();
			for (SBean.FlashSaleGoods good : flashSale.goods)
			{
                if (!goodSet.add(good.id))
				{
					throw new Exception(" good id conflict id " + good.id);
				}

			    int preVip = -1;
			    int preTime = -1;
			    
			    if ("".equals(good.goodsname))
			    {
			        throw new Exception(" good id " + good.id + " have no goodsname ! invalid config");
			    }
			    
			    if (good.v2t.size()<1)
			    {
			        throw new Exception(" good id " + good.id + " have no vip - times config ! invalid config");
			    }
			    
			    for(SBean.FlashSaleVip2Times v2t : good.v2t)
			    {
			        if (v2t.vip < preVip || v2t.times < preTime)
			            throw new Exception(" good id " + good.id + " vip-times seq invalid, vip " + v2t.vip + " times " + v2t.times );
			    }
			    
			    if (good.levelReq < 0)
			    {
			        throw new Exception(" good id " + good.id + " levelReq invalid, level " + good.levelReq +" is invalid");
			    }
			}
		}

        private void checkAllGoodsExistsAndIconResource() throws Exception
        {
            for (SBean.FlashSaleGoods good : flashSale.goods)
            {
                Set<Integer> goodIds = new HashSet<>();
                for (SBean.DummyGoods item : good.items)
                {
					if (!GameData.getInstance().checkEntityIdValid(item.id))
						throw new Exception("group buy activity goods id " + item.id + " : bag item id " + item.id + " is invalid !");
					
					if (!goodIds.add(item.id))
					{
					    throw new Exception("dumplicate good configed at good id " + good.id + " and item is " + item.id + "");
					}
					
					if (item.count < 1)
					{
					    throw new Exception("item count error ! good configed at good id " + good.id + " and item is " + item.id + " and count is " + item.count);
					}
                }
                checkIconResource(good.icon);
            }
        }
        
        private void checkIconResource(int icon) throws Exception
        {
            if (!GameData.getInstance().getFlashSaleConfig().contains(icon))
            {
                throw new Exception(" icon " + icon + " not exist" );
            }
        }

        public boolean isInBuyTime(int now)
        {
			return now >= flashSale.buyStartTime && now <= flashSale.buyEndTime;
        }

        public SBean.FlashSaleGoods getGoods(int wantId)
        {
            return flashSale.goods.stream().filter( x-> x.id==wantId).findFirst().orElseGet(null);
        }

		public Collection<SBean.FlashSaleGoods> getGoodConfigs ()
		{
			return flashSale.goods;
		}

        public int getMaxBuyTimes(SBean.FlashSaleGoods goodCfg, int vipLevel)
        {
            int canBuyTimes = 0;
            for (SBean.FlashSaleVip2Times e : goodCfg.v2t)     //asc sort
            {
                if (e.vip <= vipLevel)
                    canBuyTimes = e.times;
                else
                	break;
            }
            return canBuyTimes;
        }
	}
	
	public class FlashSaleConfigImpl extends GameConfigImpl<FlashSaleConfig>
	{
        @Override
        Class<FlashSaleConfig> getConfigClassType()
        {
            return FlashSaleConfig.class;
        }

        @Override
        String getConfigFileName(Config cfg)
        {
            return cfg.flashSaleConfFileName;
        }
        
        private List<SBean.FlashSale> parseFlashSaleConfigs(XmlElement root) throws Exception
        {
			List<SBean.FlashSale> flashSales = new ArrayList<>();
			if(root.getName().equals("activity"))
			{
				for(XmlElement e: root.getChildrenByName("flashsale"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					int buyStartTime = GameConfig.getConfRealTime(relativeTime, baseTime, e.getTimeProperty("buystarttime", 0));
					int buyEndTime = GameConfig.getConfRealTime(relativeTime, baseTime, e.getTimeProperty("buyendtime", 0));
					List<SBean.FlashSaleGoods> goodList = parseFlashSaleGoodsListConfig(e);
					flashSales.add(new SBean.FlashSale(open ? 1 : 0, timeSpan, title, content, daySpan, buyStartTime, buyEndTime,  goodList));
				}
			}
			return flashSales;
        }
        
        private List<SBean.FlashSaleGoods> parseFlashSaleGoodsListConfig(XmlElement element) throws Exception
        {
            List<SBean.FlashSaleGoods> goodsList = new ArrayList<SBean.FlashSaleGoods>();
            
            for (XmlElement good : element.getChildrenByName("goods"))
            {
                int id = good.getIntegerAttribute("id");
                String name = good.getStringAttribute("name", "");
                int moneyid = good.getIntegerAttribute("moneyid");
                int origprice = good.getIntegerAttribute("origprice");
                int nowprice = good.getIntegerAttribute("nowprice");
                int icon = good.getIntegerAttribute("icon");
                int levelreq = good.getIntegerAttribute("levelreq", 0);
                
                List<SBean.FlashSaleVip2Times> v2t = new ArrayList<>();
                XmlElement vip2timeEle = good.getChildByName("viptimes");
                if (vip2timeEle!=null)
                {
                    for (XmlElement seqEle : vip2timeEle.getChildrenByName("seq"))
                    {
                        v2t.add(new SBean.FlashSaleVip2Times(seqEle.getIntegerAttribute("vip"), seqEle.getIntegerAttribute("times")));
                    }
                }
                
                List<SBean.DummyGoods> items = new ArrayList<>();
                XmlElement itemEle = good.getChildByName("items");
                if (itemEle!=null)
                {
                    for (XmlElement item : itemEle.getChildrenByName("item"))
                    {
                        items.add(new SBean.DummyGoods(item.getIntegerAttribute("iid"), item.getIntegerAttribute("icount")));
                    }
                }
                
                goodsList.add(new SBean.FlashSaleGoods(id, name, moneyid, origprice, nowprice, icon, levelreq, v2t, items));
            }
            
            return goodsList;
        }

        @Override
        List<FlashSaleConfig> parseConfigs(int time, XmlElement element) throws Exception
        {
			List<FlashSaleConfig> cfgs = new ArrayList<>();
			List<SBean.FlashSale> rawcfgs = parseFlashSaleConfigs(element);
			for(SBean.FlashSale e: rawcfgs)
			{
				cfgs.add(new FlashSaleConfig(time, e));
			}
			return cfgs;
        }
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class LastPayGiftConfig extends ActivityConfig
	{
		SBean.LastPayGift lastpaygift;
		public LastPayGiftConfig(int time, SBean.LastPayGift lastpaygift)
		{
			super(CONFIG_TYPE_LAST_PAY_GIFT, time);
			this.lastpaygift = lastpaygift;
		}
		
		public boolean getOpenConf()
		{
			return this.lastpaygift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.lastpaygift.time;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoLastPayGiftLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeLastPayGift(this, now);
		}

		@Override
		String getTitle()
		{
			return this.lastpaygift.title;
		}

		@Override
		String getContent()
		{
			return this.lastpaygift.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return lastpaygift.phase;
		}
		
		public SBean.LastPayGift getInnerConfig()
		{
			return this.lastpaygift;
		}
		
		public SBean.RoleLastPayGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleLastPayGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), lastpaygift.title, lastpaygift.content, lastpaygift.lastPayNum, lastpaygift.fromRegister, lastpaygift.gifts);
		}
		
		public void checkValid() throws Exception
		{
			checkLastPayGift(this.lastpaygift);
		}
		
		public static void checkLastPayGift(SBean.LastPayGift lastpaygift) throws Exception
		{
			checkActivityBase(lastpaygift.time, lastpaygift.title, lastpaygift.content);
			checkActivityRoleParticipationDaySpan(lastpaygift.phase);
			checkGiftsValid(lastpaygift.gifts);
		}
		
		public static void checkGiftsValid(Map<Integer, SBean.DummyGoodList> gifts) throws Exception
		{
			if (gifts == null)
				throw new Exception("last pay gift : gift empty is invalid !");
			for (SBean.DummyGoodList gift : gifts.values())
			{
				if (gift.gifts.size() > ACT_CHALLENGE_GIFT_MAX_GIFTS_COUNT)
					throw new Exception("last pay gift : gift size " + gift.gifts.size() + " limit " + LAST_PAY_GIFT_MAX_GIFTS_COUNT + " !");
				for (SBean.DummyGoods e : gift.gifts)
				{
					if (!GameData.getInstance().checkEntityIdValid(e.id))
						throw new Exception("last pay gift : gift item id " + e.id + " is invalid !");
					if (e.count <= 0)
						throw new Exception("last pay gift : gift item count " + e.count + " is invalid !");
				}
			}
		}

	}
	
	public class LastPayGiftConfigImpl extends ActivityConfigImpl<LastPayGiftConfig>
	{
		public LastPayGiftConfigImpl()
		{
			
		}
		
		public Class<LastPayGiftConfig> getConfigClassType()
		{
			return LastPayGiftConfig.class;
		}
		
		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.lastPayGiftConfFileName;
		}
		
		public List<LastPayGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<LastPayGiftConfig> cfgs = new ArrayList<>();
			List<SBean.LastPayGift> rawcfgs = parseLastPayGiftConfigs(element);
			for (SBean.LastPayGift e : rawcfgs)
			{
				cfgs.add(new LastPayGiftConfig(time, e));
			}
			return cfgs;
		}
		
		List<SBean.LastPayGift> parseLastPayGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.LastPayGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("lastpaygift"))
				{
					Map<Integer, SBean.DummyGoodList> lastpaygiftMap = new HashMap<>();
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					String register = e.getChildText("register");
					int lastpaynum = Integer.parseInt(e.getChildText("lastpaynum", "0"));
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					for (XmlElement daycfg : e.getChildrenByName("daygift"))
					{
						SBean.DummyGoodList lastpaygift = parseGiftsConfig(daycfg);
						int day = daycfg.getIntegerAttribute("day");
						lastpaygiftMap.put(day, lastpaygift);
					}
					cfgs.add(new SBean.LastPayGift(open ? 1 : 0, timeSpan, title, content, daySpan, lastpaynum, register.equals("true")? 1 : 0, lastpaygiftMap));
				}
			}
			return cfgs;
		}

		SBean.DummyGoodList parseGiftsConfig(XmlElement root) throws Exception
		{
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return new SBean.DummyGoodList(gifts);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class ActivityChallengeGiftConfig extends ActivityConfig
	{
		SBean.ActivityChallengeGift activityChallengeGift;

		public ActivityChallengeGiftConfig(int time, SBean.ActivityChallengeGift activityChallengeGift)
		{
			super(CONFIG_TYPE_ACTIVITY_CHALLENGE_GIFT, time);
			this.activityChallengeGift = activityChallengeGift;
		}

		public boolean getOpenConf()
		{
			return this.activityChallengeGift.open != 0;
		}
		
		public SBean.TimeSpan getTimeSpan()
		{
			return this.activityChallengeGift.time;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoActivityChallengeGiftLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeActivityChallengeGift(this, now);
		}

		@Override
		String getTitle()
		{
			return this.activityChallengeGift.title;
		}

		@Override
		String getContent()
		{
			return this.activityChallengeGift.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return activityChallengeGift.phase;
		}

		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other) || GameConfig.checkTimeSpanIntersectant(this.getTimeSpan(), other.getTimeSpan());
		}

		public SBean.ActivityChallengeGift getInnerConfig()
		{
			return this.activityChallengeGift;
		}

		public SBean.RoleActivityChallengeGiftCfg toRoleCfg(Role role)
		{
			return new SBean.RoleActivityChallengeGiftCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), activityChallengeGift.title, activityChallengeGift.content, activityChallengeGift.levelReq, activityChallengeGift.vipReq, activityChallengeGift.gifts);
		}
		
		public void checkValid() throws Exception
		{
			checkActivityChallengeGift(this.activityChallengeGift);
		}

		public static void checkActivityChallengeGift(SBean.ActivityChallengeGift activityChallengeGift) throws Exception
		{
			checkActivityBase(activityChallengeGift.time, activityChallengeGift.title, activityChallengeGift.content);
			checkActivityRoleParticipationDaySpan(activityChallengeGift.phase);
			checkGiftsValid(activityChallengeGift.gifts);
		}

		public static void checkGiftsValid(List<SBean.ChallengeGift> gifts) throws Exception
		{
			if (gifts == null)
				throw new Exception("activity challenge gift : gifts empty is invalid !");
			Set<Integer> ids = new HashSet<>();
			for (SBean.ChallengeGift gift : gifts)
			{
				int lasttimes = 0;
				if (ids.contains(gift.id))
					throw new Exception("activity challenge gift : activity id " + gift.id + " is early have !");
				ids.add(gift.id);
				for (SBean.ChallengeTimeGift e : gift.gifts)
				{
					if (e.times <= lasttimes)
						throw new Exception("activity challenge gift : activity times " + e.times + " is small than before !");
					lasttimes = e.times;
					if (e.gifts.size() > ACT_CHALLENGE_GIFT_MAX_GIFTS_COUNT)
						throw new Exception("activity challenge gift : gift size " + e.gifts.size() + " limit " + ACT_CHALLENGE_GIFT_MAX_GIFTS_COUNT + " !");
					for (SBean.DummyGoods ee : e.gifts)
					{
						if (!GameData.getInstance().checkEntityIdValid(ee.id))
							throw new Exception("activity challenge gift : gift item id " + ee.id + " is invalid !");
						if (ee.count <= 0)
							throw new Exception("activity challenge gift : gift item count " + ee.count + " is invalid !");
					}
				}
			}
		}

	}

	public class ActivityChallengeGiftConfigImpl extends ActivityConfigImpl<ActivityChallengeGiftConfig>
	{
		public ActivityChallengeGiftConfigImpl()
		{

		}

		public Class<ActivityChallengeGiftConfig> getConfigClassType()
		{
			return ActivityChallengeGiftConfig.class;
		}

		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.challengeGiftConfFileName;
		}

		public List<ActivityChallengeGiftConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<ActivityChallengeGiftConfig> cfgs = new ArrayList<>();
			List<SBean.ActivityChallengeGift> rawcfgs = parseActivityChallengeGiftConfigs(element);
			for (SBean.ActivityChallengeGift e : rawcfgs)
			{
				cfgs.add(new ActivityChallengeGiftConfig(time, e));
			}
			return cfgs;
		}

		List<SBean.ActivityChallengeGift> parseActivityChallengeGiftConfigs(XmlElement root) throws Exception
		{
			List<SBean.ActivityChallengeGift> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("challengegift"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					int vipReq = e.getIntegerProperty("vipreq", 0);
					int levelReq = e.getIntegerProperty("levelreq", 1);
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					List<SBean.ChallengeGift> activityChallengeGiftMap = new ArrayList<>();
					for (XmlElement challengecfg : e.getChildrenByName("challenge"))
					{
						SBean.ChallengeGift activitychallengegift = parseRealConfig(challengecfg);
						activityChallengeGiftMap.add(activitychallengegift);
					}
					cfgs.add(new SBean.ActivityChallengeGift(open ? 1 : 0, timeSpan, title, content, daySpan, levelReq, vipReq, activityChallengeGiftMap));
				}
			}
			return cfgs;
		}

		private SBean.ChallengeGift parseRealConfig(XmlElement challengecfg) throws Exception
		{
			int activityId = challengecfg.getIntegerAttribute("activityid");
			List<SBean.ChallengeTimeGift> gifts = new ArrayList<>();
			for (XmlElement times : challengecfg.getChildrenByName("times"))
			{
				int timenum = times.getIntegerAttribute("entertimes");
				SBean.ChallengeTimeGift challengegift = new SBean.ChallengeTimeGift(timenum, parseGiftsConfig(times));
				gifts.add(challengegift);
			}
			return new SBean.ChallengeGift(activityId, gifts);
		}

		List<SBean.DummyGoods> parseGiftsConfig(XmlElement root) throws Exception
		{
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return gifts;
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class LuckyRollerConfig extends ActivityConfig
	{
	    public LuckyRollerConfig(int time, SBean.LuckyRoller luckyRollerCfg)
        {
            super(CONFIG_TYPE_LUCKY_ROLLER, time);
            this.luckRoller = luckyRollerCfg;
        }

	    public boolean getOpenConf()
		{
			return this.luckRoller.open != 0;
		}
	    
	    @Override
	    SBean.TimeSpan getTimeSpan()
	    {
	        return luckRoller.time;
	    }
	    
        @Override
	    String getTitle()
	    {
	        return luckRoller.title;
	    }
	
	    @Override
	    String getContent()
	    {
	        return luckRoller.content;
	    }
	
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return luckRoller.phase;
		}
		
	    @Override
	    void checkValid() throws Exception
	    {
	        checkLuckyRollerValid(luckRoller);
	    }

	    int getPrice()
	    {
	        return luckRoller.price;
	    }

	    int getDayMaxPlayTimes()
	    {
	        return luckRoller.dayMaxPlayTimes;
	    }
	    
	    List<SBean.DummyGoods> getGiftReward()
	    {
	        return luckRoller.gifts.stream().map(x->x.gift).collect(Collectors.toList());
	    }

	    SBean.DummyGoods getExReward()
	    {
	        return luckRoller.giftex;
	    }
	    
	    private SBean.LuckyRollerGift getFixHitGift(int seq)
	    {
	    	Integer giftId = luckRoller.maxRoll.get(seq);
	    	return giftId != null ? luckRoller.gifts.get(giftId-1) : null;
	    }

	    private SBean.LuckyRollerGift getRandomGift()
	    {
	    	 float r = new Random().nextFloat();
	         for (SBean.LuckyRollerGift gift : luckRoller.gifts)
	         {
	           	if (r <= gift.probability)
	           	{
	           		return gift;
	           	}
	         }
	         return null;
	    }
	    
	    public SBean.LuckyRollerGift rollGift(int seq)
	    {
	        SBean.LuckyRollerGift gift = this.getFixHitGift(seq);
	        if (gift != null)
	        	return gift;
	       
	        while (true)
	        {
	            gift = this.getRandomGift();
	            if (gift!= null && seq >= gift.minRoll)
	            {
	                break;
	            }
	        }
	        return gift;
	    }
	    
	    public int getMaxLogLength() 
	    {
	        return luckRoller.maxLogLength;
	    }
	    
	    public SBean.LuckyRoller getInnerConfig()
	    {
	    	return luckRoller;
	    }
	    
		public SBean.RoleLuckyRollerCfg toRoleCfg(Role role)
		{
			return new SBean.RoleLuckyRollerCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), luckRoller.title, luckRoller.content, luckRoller.price, luckRoller.gifts, luckRoller.giftex, luckRoller.dayMaxPlayTimes);
		}
		
        private static void checkLuckyRollerValid(SBean.LuckyRoller luckRoller) throws Exception
        {
        	checkActivityBase(luckRoller.time, luckRoller.title, luckRoller.content);
			checkActivityRoleParticipationDaySpan(luckRoller.phase);
            checkLuckyRollerBase(luckRoller.maxLogLength);
            checkGiftsValid(luckRoller.gifts, luckRoller.giftex);
            checkLuckyRollerLogicRight(luckRoller);
        }

        private static void checkGiftsValid(List<SBean.LuckyRollerGift> gifts, SBean.DummyGoods giftex) throws Exception
        {
			if (gifts == null || gifts.size()==0)
				throw new Exception("lucky roller gift : gifts count invalid !");
			int index = 0;
			for (SBean.LuckyRollerGift gift : gifts)
			{
			    if (gift.id != ++index)
			    {
					throw new Exception("activity lucky roll gift : gift id  must asc and amplitude is 1 , first id is 1");
			    }
				if (!GameData.getInstance().checkEntityIdValid(gift.gift.id))
					throw new Exception("activity lucky roll gift : gift item id " + gift.gift.id + " is invalid !");
				if (gift.gift.count <= 0)
					throw new Exception("activity lucky roll : gift item count " + gift.gift.count + " is invalid !");
			}
			
			if (!GameData.getInstance().checkEntityIdValid(giftex.id))
				throw new Exception("activity challenge gift : gift item id " + giftex.id + " is invalid !");
			
			if (giftex.count <= 0)
				throw new Exception("activity challenge gift : gift item count " + giftex.count + " is invalid !");
        }

        private static void checkLuckyRollerBase(int maxLogLength) throws Exception
        {
			if (maxLogLength > 30 || maxLogLength < 0)
				throw new Exception("activity luckyroller maxloglength is " + maxLogLength + " but require between 0 and 30 !");
        }
        
        private static void checkLuckyRollerLogicRight(SBean.LuckyRoller luckRoller) throws Exception
        {
            if (luckRoller.maxRoll.keySet().size() > 1)
            {
                throw new Exception("activity luckyroller fixed gift id error, fixed gift can only have 1 item");
            }
            for (Map.Entry<Integer, Integer> entry : luckRoller.maxRoll.entrySet())
            {
                int key = entry.getKey();
                int value = entry.getValue();
                if (key<=1)
                {
                    throw new Exception("activity luckyroller gifts maxRoll key error, max great than 1");
                }
                
                if (value>luckRoller.gifts.size() || value < 1)
                {
                    throw new Exception("activity luckyroller gifts maxRoll value error, must in id set");
                }
                
                SBean.LuckyRollerGift fixedGift = luckRoller.gifts.get(value-1);
                if (fixedGift.valuable ==0 )
                {
                    throw new Exception("activity luckyroller fixed gift id error, fixed gift must be valuable");
                }
            }
            
            
            // 必须至少存在一个minRoll 为 0 或者 1 的条目, 否则将陷入重试死循环
            boolean has1TimesGotGift = false;
            int totalNeedMinRollItems = 0;
            for (SBean.LuckyRollerGift gift : luckRoller.gifts)
            {
                if (gift.minRoll<=1)
                {
                    has1TimesGotGift = true;
                    break;
                }
                else 
                {
                    if (++totalNeedMinRollItems >= 4)
                    {
                        throw new Exception("activity luckyroller gifts, need minRollTimes item must below than 4");
                    }
                }
            }
            if (!has1TimesGotGift)
            {
                throw new Exception("activity luckyroller gifts list have no a item who's minRoll below than 2, if this dead loop will occur");
            }
        }
        
        public List<SBean.LuckyRollerRecord> getLuckyRollRecordsCopy()
        {
            synchronized (logs)
            {
                return new ArrayList<>(logs);
            }
        }
        
        public void addLuckyRollRecordsCopy(SBean.LuckyRollerRecord newRecord)
        {
            synchronized (logs)
            {
                logs.add(newRecord);
                int size = logs.size();
                int maxLength = luckRoller.maxLogLength;
                if (size > maxLength)
                {
                    logs.subList(0, size-maxLength).clear();
                }
            }
        }
        
        public boolean needResetRollTimes(int id)
        {
            return luckRoller.maxRoll.containsValue(id);
        }

        @Override
        boolean isNoRewardsLeft(Role role, int now)
        {
            return false;
        }

        @Override
        boolean isCanTakeRewards(Role role, int now)
        {
            return false;
        }
        
	    SBean.LuckyRoller luckRoller;
	    private List<SBean.LuckyRollerRecord> logs = new LinkedList<>();
	}
	
	public class LuckyRollerConfigImpl extends ActivityConfigImpl<LuckyRollerConfig>
	{

        @Override
        Class<LuckyRollerConfig> getConfigClassType()
        {
            return LuckyRollerConfig.class;
        }

        @Override
        String getConfigFileName(Config cfg)
        {
            return cfg.luckyRollerConfFileName;
        }

        @Override
        List<LuckyRollerConfig> parseConfigs(int time, XmlElement root) throws Exception
        {
			List<LuckyRollerConfig> luckRollerCfgs = new ArrayList<>();
			List<SBean.LuckyRoller> luckRollers = parseLuckRollers(root);
			for (SBean.LuckyRoller luckRoller : luckRollers)
			{
			    luckRollerCfgs.add(new LuckyRollerConfig(time, luckRoller));
			}
			return luckRollerCfgs;
        }
        
        private List<SBean.LuckyRoller> parseLuckRollers(XmlElement root) throws Exception
        {
			List<SBean.LuckyRoller> luckRollers = new ArrayList<>();
			if(root.getName().equals("activity"))
			{
				for(XmlElement e: root.getChildrenByName("luckyroller"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					int price = 0; 
					int maxLogLength = e.getIntegerAttribute("loglength");
					int dayplaytime = e.getIntegerAttribute("dayplaytime", -1);
					
					XmlElement giftEle = e.getChildByName("gifts");
					List<SBean.LuckyRollerGift> gifts = null;
					if (giftEle != null)
					{
					    price = giftEle.getIntegerAttribute("price");
					    gifts = parseLuckyRollerGiftList(giftEle);
					}
					
					Map<Integer, Integer> maxRoll = parseMaxRoll(e);
					SBean.DummyGoods giftEx = parseGiftEx(e);
					
					luckRollers.add(new SBean.LuckyRoller(open ? 1 : 0, timeSpan, title, content, daySpan, price, gifts, giftEx, maxRoll, maxLogLength, dayplaytime));
				}
			}
			return luckRollers;
        }
	    
        private SBean.DummyGoods parseGiftEx(XmlElement e) throws XmlNodeNotFoundException, XmlReadException
        {
            XmlElement giftexEle = e.getChildByName("giftex");
            if (giftexEle != null)
            {
                return new SBean.DummyGoods(giftexEle.getIntegerAttribute("iid"), giftexEle.getIntegerAttribute("icount")) ;
            }
            return null;
        }

        private Map<Integer, Integer> parseMaxRoll(XmlElement e) throws XmlNodeNotFoundException, XmlReadException
        {
            Map<Integer, Integer> maxRoll = new HashMap<>();
            XmlElement maxRollEle = e.getChildByName("maxroll");
            if (maxRollEle != null)
            {
                int seq = maxRollEle.getIntegerAttribute("seq");
                int id = maxRollEle.getIntegerAttribute("id");
                maxRoll.put(seq, id);
            }
            return maxRoll;
        }

        private List<SBean.LuckyRollerGift> parseLuckyRollerGiftList(XmlElement giftsEle) throws XmlNodeNotFoundException, XmlReadException
        {
            List<SBean.LuckyRollerGift> gifts = new ArrayList<>();
            int totalWeight = 0;
            for (XmlElement giftEle : giftsEle.getChildrenByName("gift"))
            {
                int id = giftEle.getIntegerAttribute("id");
                int itemId = giftEle.getIntegerAttribute("iid");
                int count = giftEle.getIntegerAttribute("icount");
                int weight = giftEle.getIntegerAttribute("weight");
                boolean valuable = giftEle.getBooleanAttribute("valuable", false);
                int minroll = giftEle.getIntegerAttribute("minroll", 0);
                gifts.add(new SBean.LuckyRollerGift(id, new SBean.DummyGoods(itemId, count), weight, valuable?1:0, minroll));
                totalWeight += weight;
            }
            float sp = 0;
            for (SBean.LuckyRollerGift gift : gifts)
            {
            	sp += gift.probability/totalWeight;
            	gift.probability = sp;
            }
            return gifts;
        }
	}
	
	public static class UpgradePurchaseConfig extends ActivityConfig
	{
		SBean.UpgradePurchase upgradePurchase;

		public UpgradePurchaseConfig(int time, SBean.UpgradePurchase UpgradePurchase)
		{
			super(CONFIG_TYPE_UPGRADE_PURCHASE, time);
			this.upgradePurchase = UpgradePurchase;
		}

		public SBean.TimeSpan getTimeSpan()
		{
			return this.upgradePurchase.time;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoUpgradePurchaseLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeUpgradePurchase(this, now);
		}

		public boolean getOpenConf()
		{
			return this.upgradePurchase.open != 0;
		}
		
		@Override
		String getTitle()
		{
			return this.upgradePurchase.title;
		}

		@Override
		String getContent()
		{
			return this.upgradePurchase.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return upgradePurchase.phase;
		}

		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other);
		}

		public SBean.UpgradePurchase getInnerConfig()
		{
			return this.upgradePurchase;
		}
		
		public SBean.RoleUpgradePurchaseCfg toRoleCfg(Role role)
		{
			return new SBean.RoleUpgradePurchaseCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), upgradePurchase.title, upgradePurchase.content, upgradePurchase.levelPurchases);
		}

		public void checkValid() throws Exception
		{
			checkUpgradePurchase(this.upgradePurchase);
		}

		public static void checkUpgradePurchase(SBean.UpgradePurchase upgradePurchase) throws Exception
		{
			checkActivityBase(upgradePurchase.time, upgradePurchase.title, upgradePurchase.content);
			checkActivityRoleParticipationDaySpan(upgradePurchase.phase);
			checkGiftsValid(upgradePurchase.levelPurchases);
		}

		public static void checkGiftsValid(SBean.UpgradeLevelPurchase levelPurchases) throws Exception
		{
			if (levelPurchases == null)
				throw new Exception("upgrade purchase : level purchase empty is invalid !");
			if (levelPurchases.price <= 0)
				throw new Exception("upgrade purchase : level purchase price is invalid !");
			if (levelPurchases.goods.isEmpty() || levelPurchases.goods.size() > UPGRADE_PURCHASE_GOODS_MAX_COUNT)
				throw new Exception("upgrade purchase : level purchase goods size " + levelPurchases.goods.size() + " is invalid !");
			for (SBean.DummyGoods ee : levelPurchases.goods)
			{
				if (!GameData.getInstance().checkEntityIdValid(ee.id))
					throw new Exception("upgrade purchase good : good item id " + ee.id + " is invalid !");
				if (ee.count <= 0)
					throw new Exception("upgrade purchase good : good item count " + ee.count + " is invalid !");
			}
		}

	}

	public class UpgradePurchaseConfigImpl extends ActivityConfigImpl<UpgradePurchaseConfig>
	{
		public UpgradePurchaseConfigImpl()
		{

		}

		public Class<UpgradePurchaseConfig> getConfigClassType()
		{
			return UpgradePurchaseConfig.class;
		}

		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.upgradePurchaseConfFileName;
		}

		public List<UpgradePurchaseConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<UpgradePurchaseConfig> cfgs = new ArrayList<>();
			List<SBean.UpgradePurchase> rawcfgs = parseUpgradePurchaseConfigs(element);
			for (SBean.UpgradePurchase e : rawcfgs)
			{
				cfgs.add(new UpgradePurchaseConfig(time, e));
			}
			return cfgs;
		}

		List<SBean.UpgradePurchase> parseUpgradePurchaseConfigs(XmlElement root) throws Exception
		{
			List<SBean.UpgradePurchase> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("upgradepurchase"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					SBean.UpgradeLevelPurchase upgradeLevelPurchase = parseLevelPurchase(e.getChildByName("level"));
					cfgs.add(new SBean.UpgradePurchase(open ? 1 : 0, timeSpan, title, content, daySpan, upgradeLevelPurchase));
				}
			}
			return cfgs;
		}
		
		SBean.UpgradeLevelPurchase parseLevelPurchase(XmlElement root) throws Exception
		{
			if (root != null)
			{
				int level = root.getIntegerAttribute("levelreq");
				int limitedTime = root.getIntegerAttribute("limitedtime");
				int price = root.getIntegerAttribute("price");
				List<SBean.DummyGoods> goods = parseGiftsConfig(root);
				return new SBean.UpgradeLevelPurchase(level, limitedTime, goods, price);	
			}
			return null;
		}

		List<SBean.DummyGoods> parseGiftsConfig(XmlElement root) throws Exception
		{
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("goods"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return gifts;
		}
	}
	
	
	
	public static class DirectPurchaseConfig extends ActivityConfig
	{
		SBean.DirectPurchase directPurchase;

		public DirectPurchaseConfig(int time, SBean.DirectPurchase directPurchase)
		{
			super(CONFIG_TYPE_DIRECT_PURCHASE, time);
			this.directPurchase = directPurchase;
		}

		public SBean.TimeSpan getTimeSpan()
		{
			return this.directPurchase.time;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoDirectPurchaseLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanTakeDirectPurchase(this, now);
		}

		public boolean getOpenConf()
		{
			return this.directPurchase.open != 0;
		}
		
		@Override
		String getTitle()
		{
			return this.directPurchase.title;
		}

		@Override
		String getContent()
		{
			return this.directPurchase.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return directPurchase.phase;
		}

		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other) || GameConfig.checkTimeSpanIntersectant(this.getTimeSpan(), other.getTimeSpan());
		}

		public SBean.DirectPurchase getInnerConfig()
		{
			return this.directPurchase;
		}
		
		public SBean.RoleDirectPurchaseCfg toRoleCfg(Role role)
		{
			return new SBean.RoleDirectPurchaseCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), directPurchase.title, directPurchase.content, directPurchase.levelPurchases);
		}

		public void checkValid() throws Exception
		{
			checkDirectPurchase(this.directPurchase);
		}

		public static void checkDirectPurchase(SBean.DirectPurchase directPurchase) throws Exception
		{
			checkActivityBase(directPurchase.time, directPurchase.title, directPurchase.content);
			checkActivityRoleParticipationDaySpan(directPurchase.phase);
			checkLevelPurchasesValid(directPurchase.levelPurchases);
		}

		public static void checkLevelPurchasesValid(List<SBean.DirectLevelPurchase> levelPurchases) throws Exception
		{
			if (levelPurchases.isEmpty())
				throw new Exception("direct purchase : level purchase empty is invalid !");
			Set<Integer> levelsIds = new HashSet<>();
			for (SBean.DirectLevelPurchase dlp : levelPurchases)
			{
				checkLevelPurchaseValid(dlp);
				if (!levelsIds.add(dlp.payLevel))
					throw new Exception("direct purchase : paylevel " + dlp.payLevel + " is duplicate !");
			}
		}

		public static void checkLevelPurchaseValid(SBean.DirectLevelPurchase levelPurchase) throws Exception
		{
			SBean.PayLevelCFGS cfg = GameData.getInstance().getPayLevelCFGS(levelPurchase.payLevel);
			if (cfg == null || cfg.type != GameData.GAME_PAY_GOODS_TYPE_GIFT)
				throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " is invalid !");
			if (levelPurchase.cardReq < 0 || levelPurchase.cardReq > 4)
				throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " : cardReq " + levelPurchase.cardReq + " is invalid !");
			if (levelPurchase.levelReq <= 0)
				throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " : levelReq " + levelPurchase.levelReq + " is invalid !");
			if (levelPurchase.vipReq < 0)
				throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " : vipReq " + levelPurchase.vipReq + " is invalid !");
			if (levelPurchase.dayBuyTimes < 0)
				throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " : dayBuyTimes " + levelPurchase.dayBuyTimes + " is invalid !");
			if (levelPurchase.gifts.isEmpty() || levelPurchase.gifts.size() > DIRECT_PURCHASE_GOODS_MAX_COUNT)
				throw new Exception("upgrade purchase : level purchase gifts size " + levelPurchase.gifts.size() + " is invalid !");
			Set<Integer> goodsIds = new HashSet<>();
			for (SBean.DummyGoods ee : levelPurchase.gifts)
			{
				if (!GameData.getInstance().checkEntityIdValid(ee.id))
					throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " : goods item id " + ee.id + " is invalid !");
				if (ee.count <= 0)
					throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " : goods item count " + ee.count + " is invalid !");
				if (!goodsIds.add(ee.id))
					throw new Exception("direct level purchase paylevel " + levelPurchase.payLevel + " : goods id " + ee.id + " is duplicate !");
			}
		}
	}
	
	
	public class DirectPurchaseConfigImpl extends ActivityConfigImpl<DirectPurchaseConfig>
	{
		public DirectPurchaseConfigImpl()
		{

		}

		public Class<DirectPurchaseConfig> getConfigClassType()
		{
			return DirectPurchaseConfig.class;
		}

		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.directPurchaseConfFileName;
		}

		public List<DirectPurchaseConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<DirectPurchaseConfig> cfgs = new ArrayList<>();
			List<SBean.DirectPurchase> rawcfgs = parseDirectPurchaseConfigs(element);
			for (SBean.DirectPurchase e : rawcfgs)
			{
				cfgs.add(new DirectPurchaseConfig(time, e));
			}
			return cfgs;
		}

		List<SBean.DirectPurchase> parseDirectPurchaseConfigs(XmlElement root) throws Exception
		{
			List<SBean.DirectPurchase> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("directpurchase"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					List<SBean.DirectLevelPurchase> directLevelPurchases = parseLevelPurchases(e);
					cfgs.add(new SBean.DirectPurchase(open ? 1 : 0, timeSpan, title, content, daySpan, directLevelPurchases));
				}
			}
			return cfgs;
		}
		
		List<SBean.DirectLevelPurchase> parseLevelPurchases(XmlElement root) throws Exception
		{
			List<SBean.DirectLevelPurchase> directLevelPurchases = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("level"))
			{
				int payLevel = e.getIntegerAttribute("paylevel");
				int cardReq = e.getIntegerAttribute("cardreq", 0);
				int levelReq = e.getIntegerAttribute("levelreq", 1);
				int vipReq = e.getIntegerAttribute("vipreq", 0);
				int dayBuyTimes = e.getIntegerAttribute("daybuytimes");
				List<SBean.DummyGoods> goods = parseGiftsConfig(e);
				directLevelPurchases.add(new SBean.DirectLevelPurchase(payLevel, cardReq, levelReq, vipReq, dayBuyTimes, goods));
			}
			return directLevelPurchases;
		}

		List<SBean.DummyGoods> parseGiftsConfig(XmlElement root) throws Exception
		{
			List<SBean.DummyGoods> gifts = new ArrayList<>();
			for (XmlElement e : root.getChildrenByName("gift"))
			{
				int iid = e.getIntegerAttribute("iid");
				int icount = e.getIntegerAttribute("icount");
				gifts.add(new SBean.DummyGoods(iid, icount));
			}
			return gifts;
		}
	}
	
	public static class OneArmBanditConfig extends ActivityConfig
	{
		SBean.OneArmBandit oneArmBandit;

		public OneArmBanditConfig(int time, SBean.OneArmBandit oneArmBandit)
		{
			super(CONFIG_TYPE_ONE_ARM_BANDIT, time);
			this.oneArmBandit = oneArmBandit;
		}

		public SBean.TimeSpan getTimeSpan()
		{
			return this.oneArmBandit.time;
		}

		public boolean isNoRewardsLeft(Role role, int now)
		{
			return role.isNoOneArmBanditLeft(this, now);
		}

		public boolean isCanTakeRewards(Role role, int now)
		{
			return role.isCanPlayOneArmBandit(this, now);
		}

		public boolean getOpenConf()
		{
			return this.oneArmBandit.open != 0;
		}
		
		@Override
		String getTitle()
		{
			return this.oneArmBandit.title;
		}

		@Override
		String getContent()
		{
			return this.oneArmBandit.content;
		}
		
		public SBean.RoleLifeDaySpan getRoleLifeDaySpan()
		{
			return oneArmBandit.phase;
		}

		public boolean isExclusion(GameConfig other)
		{
			return super.isExclusion(other);
		}

		public int getLevelTimesReq()
		{
			return this.oneArmBandit.levelTimesReq;
		}
		
		public int getPayTimesReq()
		{
			return this.oneArmBandit.payTimesReq;
		}
		
		public List<SBean.OneArmBanditDayLeft> getDayLeftTable()
		{
			return this.oneArmBandit.dayLeft;
		}
		
		public int getRandomGift()
		{
			float r = GameRandom.getRandom().nextFloat();
			int minLimit = 1;
			int maxLimit = 1;
			for (SBean.ProbabilityEntity e : this.oneArmBandit.probability)
			{
				minLimit = maxLimit;
				maxLimit = e.limit; 
				if (r < e.probability)
				{
					break;
				}
			}
//			if (maxLimit < 1000)
//			{
//				minLimit = maxLimit;
//				maxLimit = 1000;
//			}
			return GameRandom.getRandInt(minLimit, maxLimit);
		}

		public SBean.OneArmBandit getInnerConfig()
		{
			return this.oneArmBandit;
		}
		
		public SBean.RoleOneArmBanditCfg toRoleCfg(Role role)
		{
			return new SBean.RoleOneArmBanditCfg(this.getId(), this.getRoleEffectiveTimeSpan(role.createTime), oneArmBandit.title, oneArmBandit.content, oneArmBandit.levelTimesReq, oneArmBandit.payTimesReq, oneArmBandit.dayLeft);
		}
		
		public void checkValid() throws Exception
		{
			checkDirectPurchase(this.oneArmBandit);
		}
		
		public static void checkDirectPurchase(SBean.OneArmBandit oneArmBandit) throws Exception
		{
			checkActivityBase(oneArmBandit.time, oneArmBandit.title, oneArmBandit.content);
			checkActivityRoleParticipationDaySpan(oneArmBandit.phase);
			checkOneArmBanditValid(oneArmBandit.levelTimesReq, oneArmBandit.payTimesReq, oneArmBandit.probability);
			checkOneArmBanditDayLeftValid(GameConfig.getDaySpan(oneArmBandit.time), oneArmBandit.dayLeft);
		}

		public static void checkOneArmBanditValid(int levelTimesReq, int payTimesReq, List<SBean.ProbabilityEntity> probability) throws Exception
		{
			if (levelTimesReq < 0)
				throw new Exception("one arm bandit : levelTimesReq is invalid !");
			if (payTimesReq < 0)
				throw new Exception("one arm bandit : payTimesReq is invalid !");
			
			checkOneArmBanditProbabilityTableValid(probability);
		}

		public static void checkOneArmBanditProbabilityTableValid(List<SBean.ProbabilityEntity> tbl) throws Exception
		{
			if (tbl == null)
				throw new Exception("one arm bandit probability table is null !");
			int lastUpperLimit = 1;
			float probabilitySum = 0;
			for (SBean.ProbabilityEntity e : tbl)
			{
				if (e.limit <= lastUpperLimit || e.limit > 1000)
					throw new Exception("one arm bandit upper limit : " + e.limit + " is invalid !");
				if (e.probability < 0)
					throw new Exception("one arm bandit probability : " + e.probability + " is invalid !");
				lastUpperLimit = e.limit;
				probabilitySum += e.probability;
				e.probability += probabilitySum;
			}
			if (probabilitySum > 1)
				throw new Exception("one arm bandit probability : all probability entity sum is greater than 1 !");
			if (probabilitySum < 1)
				tbl.add(new SBean.ProbabilityEntity(1000, 1));
		}
		
		public static void checkOneArmBanditDayLeftValid(int daySpan, List<SBean.OneArmBanditDayLeft> dayLeft) throws Exception
		{
			if (dayLeft == null)
				throw new Exception("one arm bandit day left table is null !");
			int daySeq = 0;
			float left = Float.MAX_VALUE;
			for (SBean.OneArmBanditDayLeft e : dayLeft)
			{
				if (e.daySeq <= daySeq || e.daySeq > daySpan)
					throw new Exception("one arm bandit day left : dayseq " + e.daySeq + " is invalid  !");
				if (e.left > left)
					throw new Exception("one arm bandit day left : dayseq " + e.daySeq + " left " + e.left + " is invalid  !");
				daySeq = e.daySeq;
				left = e.left;
			}
		}
	}
	
	
	public class OneArmBanditConfigImpl extends ActivityConfigImpl<OneArmBanditConfig>
	{
		public OneArmBanditConfigImpl()
		{

		}

		public Class<OneArmBanditConfig> getConfigClassType()
		{
			return OneArmBanditConfig.class;
		}

		public String getConfigFileName(GameServer.Config cfg)
		{
			return cfg.oneArmBanditConfFileName;
		}

		public List<OneArmBanditConfig> parseConfigs(int time, XmlElement element) throws Exception
		{
			List<OneArmBanditConfig> cfgs = new ArrayList<>();
			List<SBean.OneArmBandit> rawcfgs = parseOneArmBanditConfigs(element);
			for (SBean.OneArmBandit e : rawcfgs)
			{
				cfgs.add(new OneArmBanditConfig(time, e));
			}
			return cfgs;
		}

		List<SBean.OneArmBandit> parseOneArmBanditConfigs(XmlElement root) throws Exception
		{
			List<SBean.OneArmBandit> cfgs = new ArrayList<>();
			if (root.getName().equals("activity"))
			{
				for (XmlElement e : root.getChildrenByName("onearmbandit"))
				{
					boolean open = e.getBooleanAttribute("open");
					int relativeTime = e.getIntegerAttribute("relativetime", 0);
					relativeTime = (relativeTime != 0) ? GameData.getConfUseServerOpenTime(GameTime.getDay(gs.getOpenTime()), relativeTime) : 0;
					SBean.TimeSpan timeSpan = GameConfig.getTimeSpanProperty(e, "timespan");
					String title = e.getChildText("title");
					String content = e.getChildText("content");
					int baseTime = timeSpan.startTime;
					timeSpan = GameConfig.toConfRealTimeSpan(relativeTime, baseTime, timeSpan);
					SBean.RoleLifeDaySpan daySpan = GameConfig.getRoleLifeDaySpanProperty(e, "dayspan");
					
					int levelReq = e.getIntegerProperty("levelreq");
					int payReq = e.getIntegerProperty("payreq");
					List<SBean.ProbabilityEntity> levelProbabilitys = parseLevelProbabilitys(e.getChildByName("probability"));
					List<SBean.OneArmBanditDayLeft> dayleft = parseDayLeftConfig(e.getChildByName("dayleft"));
					cfgs.add(new SBean.OneArmBandit(open ? 1 : 0, timeSpan, title, content, daySpan, levelReq, payReq, levelProbabilitys, dayleft));
				}
			}
			return cfgs;
		}
		
		List<SBean.ProbabilityEntity> parseLevelProbabilitys(XmlElement root) throws Exception
		{
			if (root != null)
			{
				List<SBean.ProbabilityEntity> levelProbabilitys = new ArrayList<>();
				for (XmlElement e : root.getChildrenByName("level"))
				{
					int gift = e.getIntegerAttribute("gift");
					float probability = e.getFloatAttribute("probability");
					levelProbabilitys.add(new SBean.ProbabilityEntity(gift, probability));
				}
				return levelProbabilitys;	
			}
			return null;
		}

		List<SBean.OneArmBanditDayLeft> parseDayLeftConfig(XmlElement root) throws Exception
		{
			if (root != null)
			{
				List<SBean.OneArmBanditDayLeft> lefts = new ArrayList<>();
				for (XmlElement e : root.getChildrenByName("day"))
				{
					int daySeq = e.getIntegerAttribute("dayseq");
					float left = e.getFloatAttribute("left");
					lefts.add(new SBean.OneArmBanditDayLeft(daySeq, left));
				}	
				return lefts;
			}
			return null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

