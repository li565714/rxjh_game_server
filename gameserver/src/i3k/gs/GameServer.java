package i3k.gs;

import i3k.DBSect;
import i3k.SBean;
import i3k.util.GameTime;
import i3k.util.ResourceWatcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ket.kdb.DB;
import ket.kdb.Table;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.kio.NetAddress;
import ket.util.ArgsMap;
import ket.util.SKVMap;
import ket.util.Stream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class GameServer implements SignalHandler
{

	public static class Config
	{
		public NetAddress addrListen = new NetAddress("0.0.0.0", 1106);

		public NetAddress addrMapListen = new NetAddress("127.0.0.1", 1107);
		
		public NetAddress addrIDIPListen = new NetAddress("127.0.0.1", 1101);
		
		public NetAddress addrAlarmListen = new NetAddress("127.0.0.1", 1191);

		public NetAddress addrFight = new NetAddress("127.0.0.1", 7104);
		
		public NetAddress addrGlobalMap = new NetAddress("127.0.0.1", 7105);
		
		public NetAddress addrAuction = new NetAddress("127.0.0.1", 7108);
		
		public List<NetAddress> addrAuths = new ArrayList<>();//NetAddress("127.0.0.1", 9100);

		public NetAddress addrTLog = new NetAddress("127.0.0.1", 9109);

		public NetAddress addrWebService = new NetAddress("127.0.0.1", 8080);

		public NetAddress addrExchange = new NetAddress("127.0.0.1", 7109);
		
		public String dbcfgfile = "./db/dbcfg.xml";
		public int nIOThread = 1;

		public String loginVerifyURL = "http://msdktest.qq.com";
		public int loginVerify = 0;
		public int loginHttpThreadCount = 4;
		public int loginHttpCallbackThreadCount = 1;
		public int loginHttpTaskWaterline = 30;

		public int verPacket = 0;
		public int verResource = 0;
		
		
		public int id = 1;
		public Set<Integer> zones;
		
		public int queue = 1000;
		public int cap = 5000;
		public int sps = 1000;
		public int pps = 50;
		public int worldNum = 4;
		public Map<Integer, Integer> extraWorldNum;
		public int mapMaxRoles = 500;

		public int luaChannelVerifyFlag = 1;
		public int challengeFlag = 1;
		public String challengeFuncArg = "abcd1234efgh5678";

		public String log4jCfgFileName = "gs.log4j.properties";
		public String GameCfgFileName = "server_cfg.dat";
		public String whitelistFileName = "login.allow.cfg";
		public String blacklistFileName = "channel.forbid.cfg";
		public String registerlimitFileName = "register.limit.cfg";
		public String assertIgnoreFileName = "assert.ignore.cfg";
		public String rankMaskFileName = "rank.mask.cfg";
		
		public int registerVerify = 0;
		public List<Integer> registerKeyBatchIds;
		
		public String allowUID = "";
		
		public String biGameAppId = "10086";
		public int biFreq = 1;
		public String dcAgentAppId = "ED8822ACF01D77E1FB2124AE178E7EE9X";
		public String dcAgentBaseDir = "dcagent";
		
		public String gameConfDir = "conf";
		public int gameConfFileUseGsId = 0;
		public String mallConfFileName = "mall";
		public String firstPayGiftConfFileName = "firstpaygift";
		public String payGiftConfFileName = "paygift";
		public String consumeGiftConfFileName = "consumegift";
		public String upgradeGiftConfFileName = "upgradegift";
		public String investmentFundConfFileName = "investmentfund";
		public String growthFundConfFileName = "growthfund";
		public String doubleDropConfFileName = "doubledrop";
		public String extraDropConfFileName = "extradrop";
		public String exchangeGiftConfFileName = "exchangegift";
		public String loginGiftConfFileName = "logingift";
		public String giftPackageConfFileName = "giftpackage";
		public String dailyPayGiftConfFileName = "dailypaygift";
		public String groupBuyConfFileName = "groupbuy";
		public String lastPayGiftConfFileName = "lastpaygift";
		public String challengeGiftConfFileName = "challengegift";
		public String flashSaleConfFileName = "flashsale";
		public String adversConfFileName = "advertising";
		public String luckyRollerConfFileName = "luckyroller";
		public String upgradePurchaseConfFileName = "upgradepurchase";
		public String directPurchaseConfFileName = "directpurchase";
		public String oneArmBanditConfFileName = "onearmbandit";
		public String payRankConfFileName = "payRank";

		public int pInfo = 0;
		public int pMapRoles = 500;
		public int pOnlines = 0;
		public int pIOFailedPerTimes = 1;
		public int newRoleGuide = 1;
		public int showWebLink = 1;
		public int godMode = 0;
		public int keep = 0;
		public int mainTaskId = 0;
		public int stoneF = 100000;
		public int stoneR = 500000;
		public int moneyF = 1000000;
		public int moneyR = 5000000;
		public List<Integer> itemList;
		public List<Integer> gemList;
		public List<Integer> equipList;
		public List<Integer> bookList;
		public int level = 50;
		public int skillLevel = 50;
		public int bwType = 0;
		public int lead = 1;
		public List<Integer> petList;
		public int petLifeComplete = 0;
		public int uniqueSkill = 0;

		public Config()
		{
			
		}

		public boolean load(String filePath)
		{
			SKVMap skv = new SKVMap();
			if (!skv.load(filePath))
				return false;

			reset(skv);
			return true;
		}

		void reset(SKVMap skv)
		{
			id = skv.getInteger("GameServer", "id", id);
			List<Integer> zonelst = skv.getIntegerList("GameServer", "zones", ":");
			zones = zonelst == null ? new HashSet<>() : new HashSet<>(zonelst);

			addrListen.host = skv.getString("GameServer", "host", addrListen.host);
			addrListen.port = skv.getInteger("GameServer", "port", addrListen.port);

			verPacket = skv.getInteger("GameServer", "verPacket", verPacket);
			verResource = skv.getInteger("GameServer", "verResource", verResource);
			
			queue = skv.getInteger("GameServer", "queue", queue);
			cap = skv.getInteger("GameServer", "cap", cap);
			sps = skv.getInteger("GameServer", "sps", sps);
			pps = skv.getInteger("GameServer", "pps", pps);
			worldNum = skv.getInteger("GameServer", "worldNum", worldNum);
			
			Map<Integer, Integer> extralNum = skv.getIntegerIntegerMap("GameServer", "extraWorldNum", ",", ":"); 
			extraWorldNum = extralNum == null ? new HashMap<>() : extralNum;
			
			mapMaxRoles = skv.getInteger("GameServer", "mapMaxRoles", mapMaxRoles);

			luaChannelVerifyFlag = skv.getInteger("GameServer", "luaChannelVerifyFlag", luaChannelVerifyFlag);
			challengeFlag = skv.getInteger("GameServer", "challengeFlag", challengeFlag);
			challengeFuncArg = skv.getString("GameServer", "challengeFuncArg", challengeFuncArg);

			addrMapListen.host = skv.getString("MapServer", "host", addrMapListen.host);
			addrMapListen.port = skv.getInteger("MapServer", "port", addrMapListen.port);
			
			addrIDIPListen.host = skv.getString("IDIPServer", "host", addrIDIPListen.host);
			addrIDIPListen.port = skv.getInteger("IDIPServer", "port", addrIDIPListen.port);

			addrAlarmListen.host = skv.getString("AlarmServer", "host", addrAlarmListen.host);
			addrAlarmListen.port = skv.getInteger("AlarmServer", "port", addrAlarmListen.port);
			
			addrGlobalMap.host = skv.getString("GlobalMapClient", "host", addrGlobalMap.host);
			addrGlobalMap.port = skv.getInteger("GlobalMapClient", "port", addrGlobalMap.port);
			
			addrFight.host = skv.getString("FightClient", "host", addrFight.host);
			addrFight.port = skv.getInteger("FightClient", "port", addrFight.port);

			addrExchange.host = skv.getString("ExchangeClient", "host", addrExchange.host);
			addrExchange.port = skv.getInteger("ExchangeClient", "port", addrExchange.port);
			
			addrAuction.host = skv.getString("AuctionClient", "host", addrAuction.host);
			addrAuction.port = skv.getInteger("AuctionClient", "port", addrAuction.port);
			
			addrAuths = toNetAddressCollection(skv.getStringList("AuthClient", "hosts", "\\|"));
//			addrAuth.host = skv.getString("AuthClient", "host", addrAuth.host);
//			addrAuth.port = skv.getInteger("AuthClient", "port", addrAuth.port);

			addrTLog.host = skv.getString("TLogClient", "host", addrTLog.host);
			addrTLog.port = skv.getInteger("TLogClient", "port", addrTLog.port);

			addrWebService.host = skv.getString("WebService", "host", addrWebService.host);
			addrWebService.port = skv.getInteger("WebService", "port", addrWebService.port);
			
			nIOThread = skv.getInteger("KIO", "ioThreadCount", nIOThread);

			loginVerify = skv.getInteger("Login", "verify", loginVerify);
			loginVerifyURL = skv.getString("Login", "url", loginVerifyURL);
			loginHttpThreadCount = skv.getInteger("Login", "httpThreadCount", loginHttpThreadCount);
			loginHttpCallbackThreadCount = skv.getInteger("Login", "httpCallbackThreadCount", loginHttpCallbackThreadCount);
			loginHttpTaskWaterline = skv.getInteger("Login", "httpTaskWaterline", loginHttpTaskWaterline);

			dbcfgfile = skv.getString("KDB", "cfgFile", dbcfgfile);
			log4jCfgFileName = skv.getString("Log4j", "cfgFile", log4jCfgFileName);

			GameCfgFileName = skv.getString("GameCfg", "cfgFile", GameCfgFileName);

			whitelistFileName = skv.getString("WhiteList", "cfgFile", whitelistFileName);
			blacklistFileName = skv.getString("BlackList", "cfgFile", blacklistFileName);
			registerlimitFileName = skv.getString("RegisterLimit", "cfgFile", registerlimitFileName);
			assertIgnoreFileName = skv.getString("AssertIgnore", "cfgFile", assertIgnoreFileName);
			rankMaskFileName = skv.getString("RankMask", "cfgFile", rankMaskFileName);

			registerVerify = skv.getInteger("Register", "verify", registerVerify);
			registerKeyBatchIds = skv.getIntegerList("Register", "batchIds", ",");
			
			allowUID = skv.getString("Debug", "allowUID", allowUID);
			
			biGameAppId = skv.getString("BI", "gameAppId", biGameAppId);
			biFreq = skv.getInteger("BI", "freq", biFreq);
			biFreq = biFreq <= 0 ? 1 : biFreq;
			dcAgentAppId = skv.getString("DCAgent", "appId", dcAgentAppId);
			dcAgentBaseDir = skv.getString("DCAgent", "baseDir", dcAgentBaseDir);
			
			gameConfDir = skv.getString("Conf", "dir", gameConfDir);
			gameConfFileUseGsId = skv.getInteger("Conf", "usegsid", gameConfFileUseGsId);
			mallConfFileName = skv.getString("Conf", "mallFile", mallConfFileName);
			firstPayGiftConfFileName = skv.getString("Conf", "firstPayGiftFile", firstPayGiftConfFileName);
			payGiftConfFileName = skv.getString("Conf", "payGiftFile", payGiftConfFileName);
			consumeGiftConfFileName = skv.getString("Conf", "consumeGiftFile", consumeGiftConfFileName);
			upgradeGiftConfFileName = skv.getString("Conf", "upgradeGiftFile", upgradeGiftConfFileName);
			investmentFundConfFileName = skv.getString("Conf", "investmentFundFile", investmentFundConfFileName);
			growthFundConfFileName = skv.getString("Conf", "growthFundFile", growthFundConfFileName);
			doubleDropConfFileName = skv.getString("Conf", "doubleDropFile", doubleDropConfFileName);
			extraDropConfFileName = skv.getString("Conf", "extraDropFile", extraDropConfFileName);
			exchangeGiftConfFileName = skv.getString("Conf", "exchangeGiftFile", exchangeGiftConfFileName);
			loginGiftConfFileName = skv.getString("Conf", "loginGiftFile", loginGiftConfFileName);
			giftPackageConfFileName = skv.getString("Conf", "giftPackageFile", giftPackageConfFileName);
			dailyPayGiftConfFileName = skv.getString("Conf", "dailyPayGiftFile", dailyPayGiftConfFileName);
			
			groupBuyConfFileName = skv.getString("Conf", "groupBuyFile", groupBuyConfFileName);
			lastPayGiftConfFileName = skv.getString("Conf", "lastPayGiftFile", lastPayGiftConfFileName);
			challengeGiftConfFileName = skv.getString("Conf", "challengeGiftFile", challengeGiftConfFileName);
			flashSaleConfFileName = skv.getString("Conf", "flashSaleFile", flashSaleConfFileName);
			luckyRollerConfFileName = skv.getString("Conf", "luckyRollerFile", luckyRollerConfFileName);
			upgradePurchaseConfFileName = skv.getString("Conf", "upgradePurchaseFile", upgradePurchaseConfFileName);
			directPurchaseConfFileName = skv.getString("Conf", "directPurchaseFile", directPurchaseConfFileName);
			oneArmBanditConfFileName = skv.getString("Conf", "oneArmBanditFile", oneArmBanditConfFileName);
			adversConfFileName = skv.getString("Conf", "adversitingFile", adversConfFileName);
			
			pInfo = skv.getInteger("Debug", "pInfo", pInfo);
			pMapRoles = skv.getInteger("Debug", "pMapRoles", pMapRoles);
			pOnlines = skv.getInteger("Debug", "pOnlines", pOnlines);
			pIOFailedPerTimes = skv.getInteger("Debug", "pIOFailedPerTimes", pIOFailedPerTimes);
			newRoleGuide = skv.getInteger("Debug", "newRoleGuide", newRoleGuide);
			showWebLink = skv.getInteger("Debug", "showWebLink", newRoleGuide);
			godMode = skv.getInteger("Debug", "godMode", godMode);
			keep = skv.getInteger("GodMode", "keep", keep);
			mainTaskId = skv.getInteger("GodMode", "maintask", mainTaskId);
			stoneF = skv.getInteger("GodMode", "stoneF", stoneF);
			stoneR = skv.getInteger("GodMode", "stoneR", stoneR);
			moneyF = skv.getInteger("GodMode", "moneyF", moneyF);
			moneyR = skv.getInteger("GodMode", "moneyR", moneyR);
			itemList = skv.getIntegerList("GodMode", "items", ",");
			gemList = skv.getIntegerList("GodMode", "gems", ",");
			equipList = skv.getIntegerList("GodMode", "equips", ",");
			bookList = skv.getIntegerList("GodMode", "books", ",");
			level = skv.getInteger("GodMode", "level", level);
			skillLevel = skv.getInteger("GodMode", "skillLevel", skillLevel);
			bwType = skv.getInteger("GodMode", "bwType", bwType);
			lead = skv.getInteger("GodMode", "lead", lead);
			petList = skv.getIntegerList("GodMode", "pets", ",");
			petLifeComplete = skv.getInteger("GodMode", "petLifeComplete", 0);
			uniqueSkill = skv.getInteger("GodMode", "uniqueSkill", 0);
		}
		
		public int getWorldLineNum(int mapID)
		{
			return this.worldNum + this.extraWorldNum.getOrDefault(mapID, 0);
		}
		
		private static List<NetAddress> toNetAddressCollection(List<String> lst)
		{
			List<NetAddress> addrAuthsHosts = new ArrayList<>();
			if (lst != null)
			{
				try
				{
					for (String s : lst)
					{
						NetAddress netaddress = NetAddress.parse(s);
						if (netaddress != null)
							addrAuthsHosts.add(netaddress);
					}
				}
				catch (Exception e)
				{
					
				}
			}
			return addrAuthsHosts;
		}
	}

	private class TimerTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				onTimer(GameTime.getTime());
			}
			catch (Throwable t)
			{
				logger.error("Uncaughted exception[" + t.getMessage() + "], throwed by timer thread", t);
				System.exit(0);
			}
		}

		public ScheduledFuture<?> future = null;
	}

	private void onTimer(int timeTick)
	{
		managerLogin.onTimer(timeTick);
		managerRPC.onTimer(timeTick);
		resourceWatcher.onTimer(timeTick);
		mapService.onTimer(timeTick);
//		clanService.onTimer(timeTick);
		managerSect.onTimer(timeTick);
		managerArena.onTimer(timeTick);
		managerBWArena.onTimer(timeTick);
		managerArenaRoom.onTimer(timeTick);
		managerBoss.onTimer(timeTick);
		managerRank.onTimer(timeTick);
		gameWebService.onTimer(timeTick);
		managerClimbTower.onTimer(timeTick);
		managerMaster.onTimer(timeTick);
		auctionService.onTimer(timeTick);
		fightService.onTimer(timeTick);
		exchangeService.onTimer(timeTick);
		managerRPC.getStat().profile();
		managerMapFlag.onTimer(timeTick);
		managerStele.onTimer(timeTick);
		managerEmergency.onTimer(timeTick);
		gameConf.onTimer(timeTick);
		managerJustice.onTimer(timeTick);
		managerLucklyStar.onTimer(timeTick);
		managerMapRoom.onTimer(timeTick);
	}

	private Config loadConfig(String configFileName)
	{
		logger.info("load gs config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{

			logger.info("load gs config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read gs config file failed .");
			return null;
		}

	}

	private void resetConfig(Config cfg)
	{
		{
			int defaultZoneId = GameData.getRawZoneIdFromGSId(cfg.id);
			if (!cfg.zones.contains(defaultZoneId))
			{
				logger.warn("check config invalid, game server " + cfg.id + " default zone " + defaultZoneId  + " is not in server zones!");
				return;
			}
		}
		if (this.cfg == null)
		{
			logger.info("game server id is " + cfg.id);
			logger.info("game server zones is " + cfg.zones);
			logger.info("server verions is " + cfg.verPacket + "." + cfg.verResource);
			logger.info("kio thread count is " + cfg.nIOThread);
			logger.info("dcagent appID is " + cfg.dcAgentAppId);
			logger.info("godMode is " + cfg.godMode);
			this.cfg = cfg;
		}
		else
		{
			logger.info("reset gs config .");
			logger.info("reset verResource " + cfg.verResource);
			logger.info("reset queue " + cfg.queue);
			logger.info("reset cap " + cfg.cap);
			logger.info("reset pps " + cfg.pps);
			logger.info("reset loginVerify " + cfg.loginVerify);
			logger.info("reset registerVerify " + cfg.registerVerify);
			logger.info("reset registerKeyBatchIds " + cfg.registerKeyBatchIds);
			logger.info("reset addrAuths " + cfg.addrAuths);
			logger.info("reset pInfo " + cfg.pInfo);
			logger.info("reset mapMaxRoles " + cfg.mapMaxRoles);
			logger.info("reset pOnlines " + cfg.pOnlines);
			logger.info("reset pIOFailedPerTimes " + cfg.pIOFailedPerTimes);
			logger.info("reset godMode " + cfg.godMode);
			logger.info("reset newRoleGuide " + cfg.newRoleGuide);
			logger.info("reset showWebLink " + cfg.showWebLink);
			logger.info("reset pMapRoles " + cfg.pMapRoles);
			logger.info("reset biGameAppId " + cfg.biGameAppId);
			logger.info("reset BIfreq " + cfg.biFreq);
			this.cfg.verResource = cfg.verResource;
			this.cfg.cap = cfg.cap;
			this.cfg.pps = cfg.pps;
			this.cfg.loginVerify = cfg.loginVerify;
			this.cfg.registerVerify = cfg.registerVerify;
			this.cfg.registerKeyBatchIds = cfg.registerKeyBatchIds;
			this.cfg.pInfo = cfg.pInfo;
			this.cfg.mapMaxRoles = cfg.mapMaxRoles;
			this.cfg.pOnlines = cfg.pOnlines;
			this.cfg.pIOFailedPerTimes = cfg.pIOFailedPerTimes;
			this.cfg.godMode = cfg.godMode;
			this.cfg.newRoleGuide = cfg.newRoleGuide;
			this.cfg.showWebLink = cfg.showWebLink;
			this.cfg.pMapRoles = cfg.pMapRoles;
			this.cfg.biGameAppId = cfg.biGameAppId;
			this.cfg.biFreq = cfg.biFreq;
			
			getRPCManager().setAllCounter(cfg.pIOFailedPerTimes == 1);
			getRPCManager().resetTcpAuthClient(cfg.addrAuths);
			boolean updateWolrdNum = resetExtralWorldNum(cfg.extraWorldNum);
			if(updateWolrdNum)
				logger.info("reset extralWorldNum " + cfg.extraWorldNum);
			
			if(this.cfg.worldNum < cfg.worldNum)
			{
				updateWolrdNum = true;
				logger.info("reset worldNum " + cfg.worldNum );
				this.cfg.worldNum = cfg.worldNum;
				
			}
			if(updateWolrdNum)
			{
				getMapService().onWorldLineNumChange(cfg.worldNum, cfg.extraWorldNum);
				getBossManager().onWorldLineNumChange(cfg.worldNum, cfg.extraWorldNum);
			}
		}
	}

	boolean resetExtralWorldNum(Map<Integer, Integer> extralWorldNum)
	{
		boolean updateWolrdNum = false;
		for(Map.Entry<Integer, Integer> e: extralWorldNum.entrySet())
		{
			int mapID = e.getKey();
			int num = e.getValue();
			
			if(num > this.cfg.extraWorldNum.getOrDefault(mapID, 0))
			{
				this.cfg.extraWorldNum.put(mapID, num);
				updateWolrdNum = true;
			}
		}
		
		return updateWolrdNum;
	}
	
	private GameData loadGameData(String filePath)
	{
		try
		{
			logger.info("try load gamedata : " + filePath + " ...");
			SBean.GameDataCFGS gamedata = new SBean.GameDataCFGS();
			if (Stream.loadObjLE(gamedata, new File(filePath)))
			{
				GameData gameData = new GameData(gamedata);
				logger.info("load gamedata success");
				return gameData;
			}
			else
			{
				logger.warn("read gamedata failed.");
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return null;
	}

	public Config getConfig()
	{
		return cfg;
	}

	DB getDB()
	{
		return db;
	}

	public void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}

	public Logger getLogger()
	{
		return logger;
	}

	public TLogger getTLogger()
	{
		return tlogger;
	}

	public ResourceWatcher getResourceManager()
	{
		return resourceWatcher;
	}
	
	public GameConf getGameConf()
	{
		return gameConf;
	}

	public RPCManager getRPCManager()
	{
		return managerRPC;
	}

	public LoginManager getLoginManager()
	{
		return managerLogin;
	}

	public FlagManager getFlagManager()
	{
		return managerMapFlag;
	}
	
	public ArenaManager getArenaManager()
	{
		return managerArena;
	}
	
	public BWArenaManager getBWArenaManager()
	{
		return managerBWArena;
	}
	
	public BossManager getBossManager()
	{
		return managerBoss;
	}
	
	public JusticeNpcManager getJusticeManager()
	{
		return managerJustice;
	}

	public LucklyStarManager getLucklyStarManager()
	{
		return managerLucklyStar;
	}
	
	public SteleManager getSteleManager()
	{
		return managerStele;
	}
	
	public EmergencyManager getEmergencyManager()
	{
		return managerEmergency;
	}
	
	public RankManager getRankManager()
	{
		return managerRank;
	}
	
	public TeamManager getTeamManager()
	{
		return managerTeam;
	}

	public MapService getMapService()
	{
		return mapService;
	}

	public MapRoomManager getMapCopyManager()
	{
		return managerMapRoom;
	}
	
	public ArenaRoomManager getArenaRoomManager()
	{
		return managerArenaRoom;
	}
	
	public ForceWarRoomManager getForceWarRoomManager()
	{
		return managerForceWarRoom;
	}
	
	public SectManager getSectManager()
	{
		return managerSect;
	}

	public ClimbTowerManager getClimbTowerManager()
	{
		return managerClimbTower;
	}
	
	public MasterManager getMasterManager()
	{
		return managerMaster;
	}
	
//	public ClanService getClanService()
//	{
//		return clanService;
//	}

	public AuctionService getAuctionService()
	{
		return auctionService;
	}
	
	public FightService getFightService()
	{
		return fightService;
	}
	
	GameWebService getGameWebService()
	{
		return gameWebService;
	}
	
	public ExchangeService getExchangeService()
	{
		return exchangeService;
	}

	public int getOpenTime()
	{
		return openTime;
	}
	
	public void setOpenTime(int time)
	{
		this.openTime = time;
	}
	
	//开服第几天,从0开始
	public int getOpenDay()
	{
		return GameTime.getDay() - GameTime.getDay(this.openTime);
	}
	
	public GameServer()
	{
		{
			if (!logger.getAllAppenders().hasMoreElements())
			{
				org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
				ca.setName("_AA_");
				ca.setWriter(new java.io.PrintWriter(System.out));
				ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
				logger.addAppender(ca);
			}
			resourceWatcher.setLogger(logger);
		}
		gameConf = new GameConf(this);
		managerRPC = new RPCManager(this);
		managerLogin = new LoginManager(this);
		managerArena = new ArenaManager(this);
		managerBWArena = new BWArenaManager(this);
		managerBoss = new BossManager(this);
		managerRank = new RankManager(this);
		mapService = new MapService(this);
		managerMapRoom = new MapRoomManager(this);
		managerArenaRoom = new ArenaRoomManager(this);
		managerForceWarRoom = new ForceWarRoomManager(this);
		managerTeam = new TeamManager(this);
		managerSect = new SectManager(this);
//		clanService = new ClanService(this);
		auctionService = new AuctionService(this);
		fightService = new FightService(this);
		gameWebService = new GameWebService(this);
		managerClimbTower = new ClimbTowerManager(this);
		managerMaster = new MasterManager(this);
		managerMapFlag = new FlagManager(this);
		managerStele = new SteleManager(this);
		managerEmergency = new EmergencyManager(this);
		exchangeService = new ExchangeService(this);
		managerJustice = new JusticeNpcManager(this);
		managerLucklyStar = new LucklyStarManager(this);
	}

	boolean init(String fileCfg)
	{
		resourceWatcher.addWatch(fileCfg, filePath -> {
			Config ncfg = loadConfig(fileCfg);
			if (ncfg != null)
			{
				resetConfig(ncfg);
			}
		});
		if (this.cfg == null)
		{
			System.err.println("server config file load failed !");
			return false;

		}

		resourceWatcher.addWatch(cfg.log4jCfgFileName, PropertyConfigurator::configure);

		resourceWatcher.addWatch(cfg.GameCfgFileName, filePath -> {
            GameData gamedata = loadGameData(filePath);
            if (gamedata != null)
            {
                GameData.setInstance(gamedata.init(cfg.id));
            }
        });
		
		if (GameData.getInstance() == null)
		{
			logger.error("server game config file load failed !");
			return false;
		}

		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Uncaughted exception[" + e.getMessage() + "], throwed by thread[" + t.getName() + "]", e);
            System.exit(0);
        });
		// executor.

		db = ket.kdb.Factory.newDB();
		db.setLogger(logger);
		Path p = Paths.get(cfg.dbcfgfile);
		db.open(p.getParent(), p);

		db.execute(new WorldInitTrans());

		return true;
	}

	public class WorldInitTrans implements Transaction
	{
		public WorldInitTrans()
		{
		}

		@Override
		public boolean doTransaction()
		{
			try
			{
				{
					byte[] key = Stream.encodeStringLE("openDay");
					byte[] data = world.get(key);
					if (data == null)
					{
						int openTime = GameTime.getTime();
						byte[] newdata = Stream.encodeIntegerLE(openTime);
						world.put(key, newdata);
						setOpenTime(openTime);
						logger.info("write open time = " + openTime);
					}
					else
					{
						int openTime = Stream.decodeIntegerLE(data);
						setOpenTime(openTime);
						logger.info("read open time = " + openTime);
					}
				}
				{
					int roleTotleCreate = 0;
					for (int zoneId : cfg.zones)
					{
						final String maxRoleIDKey = "roleid" + "_" + zoneId;
						Integer maxid = maxids.get(maxRoleIDKey);
						int roleCreate = maxid == null ? 0 : maxid;
						roleTotleCreate += roleCreate;
					}
					getLoginManager().setRoleTotleCreate(roleTotleCreate);
				}
				{
					byte[] key = Stream.encodeStringLE("worldMails");
					byte[] data = world.get(key);
					if (data == null)
					{
						logger.info("load world mails: 0");
					}
					else
					{
						List<SBean.DBWorldMail> lst = Stream.decodeListLE(SBean.DBWorldMail.class, data);
						getLoginManager().getWorldMail().fromDB(lst);
						logger.info("load world mails: " + lst.size());
					}
				}
				{
					byte[] key = Stream.encodeStringLE("rollNotices");
					byte[] data = world.get(key);
					if (data == null)
					{
						logger.info("load roll notices: 0");
					}
					else
					{
						List<SBean.DBRollNotice> lst = Stream.decodeListLE(SBean.DBRollNotice.class, data);
						getLoginManager().getRollNotice().fromDB(lst);
						logger.info("load roll notices: " + lst.size());
					}
				}
				{
					byte[] key = Stream.encodeStringLE("arena");
					byte[] data = world.get(key);
					if (data == null)
					{
						managerArena.init(null);
					}
					else
					{
						SBean.DBArena arena = new SBean.DBArena();
						Stream.decodeLE(arena, data);
						managerArena.init(arena);

					}
					logger.info("load arena ok");
				}
				{
					byte[] key = Stream.encodeStringLE("bwarena");
					byte[] data = world.get(key);
					if (data == null)
					{
						managerBWArena.init(null);
					}
					else
					{
						SBean.DBBWArena dbBWArena = new SBean.DBBWArena();
						Stream.decodeLE(dbBWArena, data);
						managerBWArena.init(dbBWArena);
					}
				}
				{
					byte[] key = Stream.encodeStringLE("boss");
					byte[] data = world.get(key);
					SBean.DBBoss dbBoss = null;
					if(data != null)
					{
						dbBoss = new SBean.DBBoss();
						Stream.decodeLE(dbBoss, data);
					}
					managerBoss.init(dbBoss);
					logger.info("load boss ok");
				}
				{
					byte[] key = Stream.encodeStringLE("climbTower");
					byte[] data = world.get(key);
					if (data == null)
					{
						managerClimbTower.init(null);
					}
					else
					{
						SBean.DBClimbTowerServerRecordDataCfg climbTower = new SBean.DBClimbTowerServerRecordDataCfg();
						Stream.decodeLE(climbTower, data);
						managerClimbTower.init(climbTower.datas);

					}
					logger.info("load climbTower ok");
				}
				{
					byte[] key = Stream.encodeStringLE("ranks");
					byte[] data = world.get(key);
					if(data == null)
					{
						getRankManager().init(null);
					}
					else
					{
						SBean.DBRanks ranks = new SBean.DBRanks();
						Stream.decodeLE(ranks, data);
						getRankManager().init(ranks);
						
					}
					logger.info("load rank ok");
				}
				{
					byte[] key = Stream.encodeStringLE("sectmaxlevel");
					byte[] data = world.get(key);
					int sectmaxlevel = 1;
					if (data != null)
					{
						sectmaxlevel = Stream.decodeIntegerLE(data);
						logger.info("read max sect level = " + sectmaxlevel);
					}
					getSectManager().setSectMaxLevel(sectmaxlevel);
//					getSectManager().init(sect);
//					logger.info("load all sects ");
				}
				{
					byte[] key = Stream.encodeStringLE("messageBoards");
					byte[] data = world.get(key);
					if (data == null)
					{
						logger.info("load message boards: 0");
					}
					else
					{
						List<SBean.DBMessageBoard> lst = Stream.decodeListLE(SBean.DBMessageBoard.class, data);
						getLoginManager().getMessageBoard().fromDB(lst);
						logger.info("load message boards: " + lst.size());
					}
				}
				{
					byte[] key = Stream.encodeStringLE("mapFlags");
					byte[] data = world.get(key);
					SBean.DBMapFlag dbMapFlag = null;
					if(data != null)
					{
						dbMapFlag = new SBean.DBMapFlag();
						Stream.decodeLE(dbMapFlag, data);
					}
					if (dbMapFlag != null)
						for (SBean.DBMapFlagInfo flag : dbMapFlag.mapFlags.values())
						{
							DBSect dbsect = sect.get(flag.curSectId);
							if (dbsect != null)
							{
								managerSect.tryActiveSect(new Sect(GameServer.this, dbsect.id).fromDB(dbsect));
							}
						}
					managerMapFlag.init(dbMapFlag);
					logger.info("load flag ok");
				}
				{
					byte[] key = Stream.encodeStringLE("gameconf");
					byte[] data = world.get(key);
					SBean.DBGameConfData dbGameConf = null;
					if(data != null)
					{
						dbGameConf = new SBean.DBGameConfData();
						Stream.decodeLE(dbGameConf, data);
					}
					gameConf.init(dbGameConf);
					logger.info("load gameconf ok");
				}
				{
					byte[] key = Stream.encodeStringLE("speeduplvl");
					byte[] data = world.get(key);
					SBean.DBSpeedUpLvl dbSpeedUp = null;
					if(data != null)
					{
						dbSpeedUp = new SBean.DBSpeedUpLvl();
						Stream.decodeLE(dbSpeedUp, data);
					}
					managerLogin.getSpeedUp().fromDB(dbSpeedUp);
					logger.info("load speed up lvl ok");
				}
				{
					byte[] key = Stream.encodeStringLE("marriageBespeak");
					byte[] data = world.get(key);
					if (data == null)
					{
						logger.info("load marriage bespeaks: 0");
					}
					else
					{
						List<SBean.DBMarriageBespeak> lst = Stream.decodeListLE(SBean.DBMarriageBespeak.class, data);
						getLoginManager().getMarriageBespeak().fromDB(lst);
						logger.info("load marriage bespeaks: " + lst.size());
					}
				}
				{
					byte[] key = Stream.encodeStringLE("stele");
					byte[] data = world.get(key);
					if(data == null)
					{
						managerStele.init(null);
					}
					else
					{
						SBean.DBStele dbStele = new SBean.DBStele();
						Stream.decodeLE(dbStele, data);
						managerStele.init(dbStele);
					}
					logger.info("load stele ok");
				}
				{
					byte[] key = Stream.encodeStringLE("emergency");
					byte[] data = world.get(key);
					if(data != null)
					{
						SBean.DBEmergency dbEmergency = new SBean.DBEmergency();
						Stream.decodeLE(dbEmergency, data);
						managerEmergency.init(dbEmergency);
					}
					logger.info("load stele ok");
				}
				{
					byte[] key = Stream.encodeStringLE(MasterManager.DB_KEY_NAME);
					byte[] data = world.get(key);
					if(data != null)
					{
						SBean.MasterAnnounce ma = new SBean.MasterAnnounce();
						Stream.decodeLE(ma, data);
						managerMaster.init(ma);
					}
					logger.info("load master list ok");
				}
				return true;
			}
			catch (Stream.EOFException e)
			{
				return false;
			}
			catch (Stream.DecodeException e)
			{
				return false;
			}
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if (errcode == ErrorCode.eOK)
			{
				start();
			}
			else
			{
				logger.error("world init failed");
				System.exit(0);
			}
		}

		@AutoInit
		public Table<byte[], byte[]> world;

		@AutoInit
		public TableReadonly<String, Integer> maxids;

		@AutoInit
		public TableReadonly<Integer, DBSect> sect;
	}

	void start()
	{
		logger.info("@@##>>> gs begin start ...");
		tlogger.startUp();
		gameConf.start();
		managerLogin.start();
		gameWebService.startup();
		managerRPC.start();
		managerArenaRoom.init();
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		logger.info("gs start " + GameTime.getTime());
	}
	
	void cancelTimer()
	{
		if (timertask.future != null)
			timertask.future.cancel(false);
	}
	
	void setMemoryDB()
	{
		bMemoryDB = true;
	}
	
	boolean isMemroyDB()
	{
		return bMemoryDB;
	}

	void destroy()
	{
		if (timertask.future != null)
			timertask.future.cancel(false);
		managerRPC.destroy();
		resourceWatcher.fini();
		logger.info("main executor shutdown start");
		gameWebService.shutdown();
		executor.shutdown();
		try
		{
			while (!executor.awaitTermination(1, TimeUnit.SECONDS))
			{
			}
		}
		catch (Exception ignored)
		{
		}
		logger.info("main executor shutdown ok");
		managerLogin.saveRoles();
		managerSect.saveSects();
		managerArena.save();
		managerBWArena.save();
		managerBoss.save();
		managerRank.save();
		managerClimbTower.save();
		managerMaster.save();
		managerMapFlag.save();
		managerStele.save();
		managerEmergency.save();
		managerLogin.destroy();
		gameConf.save();
		db.close();
		tlogger.shutDown();
		logger.info("db close ok");
	}

	public void initSignal()
	{
		Signal.handle(new Signal("TERM"), this);
		ket.util.FileSys.pauseWaitSingal(latch);
	}

	@Override
	public void handle(Signal sig)
	{
		// TODO Auto-generated method stub
		if (bSingalHandled)
			return;
		if (sig.getName().equals("TERM"))
		{
			System.out.println("recieved term");
			bSingalHandled = true;
			latch.countDown();
		}
	}

	public static void main(String[] args)
	{

		ArgsMap am = new ArgsMap(args);
		GameServer gs = new GameServer();
		String fileCfg = am.get("-cfgfile", "gs.cfg");
		if (!gs.init(fileCfg))
		{
			System.err.println("server initialize failed ... ");
			return;
		}
		if (am.containsKey("bg"))
		{
			gs.initSignal();
		}
		else
		{
			// TODO
			// ket.util.FileSys.pauseWaitInput();
			if (System.console() != null)
			{
				while (true)
				{
					String line = System.console().readLine();
					System.out.println("read line is [" + line + "]");
					if (line.equals(""))
						break;
					if (line.equals("1"))
						gs.getRPCManager().setDisconnectMode(true);
					if (line.equals("2"))
						gs.getRPCManager().setDisconnectMode(false);
				}
			}
			else
			{
				ket.util.FileSys.pauseWaitInput();
			}
		}
		gs.destroy();
	}

	private Config cfg;
	private DB db;
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private GameConf gameConf;
	private RPCManager managerRPC;
	private LoginManager managerLogin;
	private ArenaManager managerArena;
	private BWArenaManager managerBWArena;
	private BossManager managerBoss;
	private RankManager managerRank;
	private MapService mapService;
	private MapRoomManager managerMapRoom;
	private TeamManager managerTeam;
	private ArenaRoomManager managerArenaRoom;
	private ForceWarRoomManager managerForceWarRoom;
	private SectManager managerSect;
	private ClimbTowerManager managerClimbTower;
	private MasterManager managerMaster;
//	private ClanService clanService;
	private AuctionService auctionService;
	private FightService fightService;
	private GameWebService gameWebService;
	private ExchangeService exchangeService;
	private Logger logger = Logger.getLogger("gsLogger");
	private TLogger tlogger = new TLogger(this);
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();

	private int openTime;
	private boolean bMemoryDB = false;
	private FlagManager managerMapFlag;
	private SteleManager managerStele;
	private EmergencyManager managerEmergency;
	private JusticeNpcManager managerJustice;
	private LucklyStarManager managerLucklyStar;
}
