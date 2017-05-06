package i3k.fight;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import ket.kdb.DB;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.kio.NetAddress;
import ket.util.ArgsMap;
import ket.util.SKVMap;
import ket.util.Stream;
import i3k.util.GameTime;
import i3k.util.ResourceWatcher;
import i3k.DBDemonHoleGroup;
import i3k.DBFightRanks;
import i3k.SBean;
import i3k.gs.GameData;

@SuppressWarnings("restriction")
public class FightServer implements SignalHandler
{

	public static class Config
	{
		public NetAddress addrFightListen = new NetAddress("127.0.0.1", 7104);
		public NetAddress addrFightMapListen = new NetAddress("127.0.0.1", 7103);
		public NetAddress addrAlarmListen = new NetAddress("127.0.0.1", 1191);
		public int areaId = 1;

		public String dbcfgfile = "./dbfight/dbcfg.xml";
		public int nIOThread = 1;

		public String log4jCfgFileName = "fs.log4j.properties";
		public String rankMaskFileName = "rank.mask.cfg";
		public String GameCfgFileName = "server_cfg.dat";
		
		public int showForcewar = 0;
		
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
			addrFightListen.host = skv.getString("FightServer", "host", addrFightListen.host);
			addrFightListen.port = skv.getInteger("FightServer", "port", addrFightListen.port);
			addrFightMapListen.host = skv.getString("FightMapServer", "host", addrFightMapListen.host);
			addrFightMapListen.port = skv.getInteger("FightMapServer", "port", addrFightMapListen.port);
			
			addrAlarmListen.host = skv.getString("AlarmServer", "host", addrAlarmListen.host);
			addrAlarmListen.port = skv.getInteger("AlarmServer", "port", addrAlarmListen.port);
			
			areaId = skv.getInteger("FightServer", "area", areaId);

			dbcfgfile = skv.getString("KDB", "cfgFile", dbcfgfile);
			nIOThread = skv.getInteger("KIO", "ioThreadCount", nIOThread);

			log4jCfgFileName = skv.getString("Log4j", "cfgFile", log4jCfgFileName);
			rankMaskFileName = skv.getString("RankMask", "cfgFile", rankMaskFileName);
			
			GameCfgFileName = skv.getString("GameCfg", "cfgFile", GameCfgFileName);
			
			showForcewar = skv.getInteger("Debug", "showForcewar", showForcewar);
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
		managerRPC.onTimer(timeTick);
		resourceWatcher.onTimer(timeTick);
		managerFight.onTimer(timeTick);
		globalMapService.onTimer(timeTick);
		managerSuperArena.onTimer(timeTick);
		managerFightRank.onTimer(timeTick);
		managerDemonHole.onTimer(timeTick);
	}

	private Config loadConfig(String configFileName)
	{
		logger.info("load fs config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{

			logger.info("load fs config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read fs config file failed .");
			return null;
		}

	}

	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			logger.info("set fight server config");
			this.cfg = cfg;
		}
		else
		{
			logger.info("reset fs config. ");
			logger.info("reset showForcewar " + cfg.showForcewar);
			
			this.cfg.showForcewar = cfg.showForcewar;
		}
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

	public ResourceWatcher getResourceManager()
	{
		return resourceWatcher;
	}

	public RPCManager getRPCManager()
	{
		return managerRPC;
	}

	public FightManager getFightManager()
	{
		return managerFight;
	}
	
	public GlobalMapService getGlobalMapService()
	{
		return globalMapService;
	}
	
	public SuperArenaManager getSuperArenaManager()
	{
		return managerSuperArena;
	}
	
	public FightTeamManager getFightTeamManager()
	{
		return managerFightTeam;
	}
	
	public RoleManager getRoleManager()
	{
		return managerRole;
	}
	
	public FightRankManager getFightRankManager()
	{
		return managerFightRank;
	}
	
	public DemonHoleManager getDemonHoleManager()
	{
		return managerDemonHole;
	}
	
	public FightServer()
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
		managerRPC = new RPCManager(this);
		managerFight = new FightManager(this);
		globalMapService = new GlobalMapService(this);
		managerSuperArena = new SuperArenaManager(this);
		managerFightTeam = new FightTeamManager(this);
		managerRole = new RoleManager(this);
		managerFightRank = new FightRankManager(this);
		managerDemonHole = new DemonHoleManager(this);
	}

	boolean init(String fileCfg)
	{
		resourceWatcher.addWatch(fileCfg, filePath ->
		{
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

		resourceWatcher.addWatch(cfg.GameCfgFileName, filePath ->
		{
			GameData gamedata = loadGameData(filePath);
			if (gamedata != null)
			{
				GameData.setInstance(gamedata);
			}
		});
		if (GameData.getInstance() == null)
		{
			logger.error("server game config file load failed !");
			return false;
		}

		Thread.setDefaultUncaughtExceptionHandler((t, e) ->
		{
			logger.error("Uncaughted exception[" + e.getMessage() + "], throwed by thread[" + t.getName() + "]", e);
			System.exit(0);
		});
		// executor.

		db = ket.kdb.Factory.newDB();
		db.setLogger(logger);
		Path p = Paths.get(cfg.dbcfgfile);
		db.open(p.getParent(), p);

		db.execute(new FightInitTrans());

		return true;
	}

	public class FightInitTrans implements Transaction
	{
		public FightInitTrans()
		{
		}

		@Override
		public boolean doTransaction()
		{	
			{
				getFightRankManager().init(fightranks);
				logger.info("load fight ranks data");
			}
			
			//demon hole
			{
				managerDemonHole.init(demonholegroups);
				logger.info("load demon hole data");
			}
			return true;
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
				logger.error("fight db init failed");
				System.exit(0);
			}
		}
		
		@AutoInit
		public TableReadonly<Integer, DBFightRanks> fightranks;
		
		@AutoInit
		public TableReadonly<Integer, DBDemonHoleGroup> demonholegroups;
	}

	void start()
	{
		logger.info("@@##>>> fs begin start ...");
		managerFight.start();
		managerRPC.start();
		managerSuperArena.init();
		managerFight.init();
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		logger.info("fs start " + GameTime.getTime());
	}

	void destroy()
	{
		if (timertask.future != null)
			timertask.future.cancel(false);
		managerRPC.destroy();
		managerFight.destroy();
		managerFightRank.save();
		managerDemonHole.save();
		resourceWatcher.fini();
		logger.info("main executor shutdown start");
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
		db.close();
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
		FightServer fs = new FightServer();
		String fileCfg = am.get("-cfgfile", "fs.cfg");
		if (!fs.init(fileCfg))
		{
			System.err.println("server initialize failed ... ");
			return;
		}
		if (am.containsKey("bg"))
		{
			fs.initSignal();
		}
		else
		{
			ket.util.FileSys.pauseWaitInput();
		}
		fs.destroy();
	}

	private Config cfg;
	private DB db;
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private RPCManager managerRPC;
	private Logger logger = Logger.getLogger("fsLogger");
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
	private FightManager managerFight;
	private GlobalMapService globalMapService;
	private SuperArenaManager managerSuperArena;
	private FightTeamManager managerFightTeam;
	private RoleManager managerRole;
	private FightRankManager managerFightRank;
	private DemonHoleManager managerDemonHole;
}
