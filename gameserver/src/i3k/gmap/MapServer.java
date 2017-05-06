package i3k.gmap;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import ket.kio.NetAddress;
import ket.util.ArgsMap;
import ket.util.SKVMap;
import ket.util.Stream;
import i3k.util.GameTime;
import i3k.util.ResourceWatcher;
import i3k.SBean;
import i3k.gs.GameData;

@SuppressWarnings("restriction")
public class MapServer implements SignalHandler
{

	public static class Config
	{
		public NetAddress addrMap = new NetAddress("127.0.0.1", 1107);
		public NetAddress addrGlobalMapListen = new NetAddress("127.0.0.1", 7105);
		public NetAddress addrFightMap = new NetAddress("127.0.0.1", 7103);
		public NetAddress addrAlarmListen = new NetAddress("127.0.0.1", 1191);

		public int group = 1;
		public int id = 1;
		public int maxRole = 20;
		public int specialLineMaxRole = 15;
		public int nIOThread = 1;
		public int pIOFailedPerTimes = 1;

		public String log4jCfgFileName = "ms.log4j.properties";
		public String GameCfgFileName = "server_cfg.dat";
		public String mapDeployFileName = "mapdepoly.xml";

		public int pInfo = 0;
		
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
			group = skv.getInteger("MapServer", "group", group);
			id = skv.getInteger("MapServer", "id", id);
			maxRole = skv.getInteger("MapServer", "maxrole", maxRole);
			specialLineMaxRole = skv.getInteger("MapServer", "specialLineMaxRole", specialLineMaxRole);
			nIOThread = skv.getInteger("KIO", "ioThreadCount", nIOThread);
			pIOFailedPerTimes = skv.getInteger("Debug", "pIOFailedPerTimes", pIOFailedPerTimes);

			addrMap.host = skv.getString("LocalMapClient", "host", addrMap.host);
			addrMap.port = skv.getInteger("LocalMapClient", "port", addrMap.port);
			
			addrGlobalMapListen.host = skv.getString("GlobalMapServer", "host", addrGlobalMapListen.host);
			addrGlobalMapListen.port = skv.getInteger("GlobalMapServer", "port", addrGlobalMapListen.port);
			
			addrFightMap.host = skv.getString("FightMapClient", "host", addrFightMap.host);
			addrFightMap.port = skv.getInteger("FightMapClient", "port", addrFightMap.port);
			
			addrAlarmListen.host = skv.getString("AlarmServer", "host", addrAlarmListen.host);
			addrAlarmListen.port = skv.getInteger("AlarmServer", "port", addrAlarmListen.port);

			log4jCfgFileName = skv.getString("Log4j", "cfgFile", log4jCfgFileName);

			GameCfgFileName = skv.getString("GameCfg", "cfgFile", GameCfgFileName);

			mapDeployFileName = skv.getString("Deploy", "cfgFile", mapDeployFileName);
			
			pInfo = skv.getInteger("Debug", "pInfo", pInfo);
		}
		
		int getWorldMaxRole(boolean specialLine)
		{
			return specialLine ? specialLineMaxRole : maxRole;
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

	private class Timer10MillisTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				onTimer();
			}
			catch (Throwable t)
			{
				logger.error("Uncaughted exception[" + t.getMessage() + "], throwed by timer 10millis thread", t);
				System.exit(0);
			}
		}

		public ScheduledFuture<?> future = null;
	}

	private void onTimer(int timeTick)
	{
		//getLogger().info("timeTick="+timeTick);
		managerRPC.onTimer(timeTick);
		resourceWatcher.onTimer(timeTick);
		managerRPC.getStats().profile();
		managerMap.getStats().profile();
	}

	private void onTimer()
	{
		//getLogger().info("timeMillis="+timeMillis +", timeTick="+timeTick);
		managerMap.addTimerEvent(() ->
		{
//			try
//			{
				long timeMillis = GameTime.getTimeMillis();
				int timeTick = GameTime.getTime();
				managerMap.onTimer(timeMillis, timeTick);
//			}
//			catch (Throwable t)
//			{
//				logger.error("Uncaughted exception[" + t.getMessage() + "], throwed by timer map manager onTimer", t);
//			}
		});
	}

	private Config loadConfig(String configFileName)
	{
		logger.info("load ms config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{

			logger.info("load ms config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read ms config file failed .");
			return null;
		}

	}

	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			logger.info("game map server ID is " + cfg.id);
			logger.info("game map server serve " + (cfg.group < GameData.MAX_AREA_GS_COUNT ? "area ": "gs ") + cfg.group + ", map run as " + (cfg.group < GameData.MAX_AREA_GS_COUNT ? "global" : "local") + " map server .");
			logger.info("game map server max broadcast roles " + cfg.maxRole);
			logger.info("game map server max broadcast specialLineMaxRole " + cfg.specialLineMaxRole);
			this.cfg = cfg;	
		}
		else
		{
			logger.info("reset ms config .");
			logger.info("reset map server max broadcast roles " + cfg.maxRole);
			logger.info("reset map server max broadcast specialLineMaxRole " + cfg.specialLineMaxRole);
			logger.info("reset pIOFailedPerTimes " + cfg.pIOFailedPerTimes);
			this.cfg.maxRole = cfg.maxRole;
			this.cfg.specialLineMaxRole = cfg.specialLineMaxRole;
			this.cfg.pInfo = cfg.pInfo;
			this.cfg.pIOFailedPerTimes = cfg.pIOFailedPerTimes;
			getRPCManager().setAllCounter(cfg.pIOFailedPerTimes == 1);
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
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public Config getConfig()
	{
		return cfg;
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

	public DeployConf getDeployConf()
	{
		return deployConf;
	}

	public RPCManager getRPCManager()
	{
		return managerRPC;
	}

	public MapManager getMapManager()
	{
		return managerMap;
	}

	public int getOpenDay()
	{
		return openDay;
	}

	public void setOpenDay(int day)
	{
		this.openDay = day;
	}

	public MapServer()
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
		deployConf = new DeployConf(this);
		managerRPC = new RPCManager(this);
		managerMap = new MapManager(this);
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

		resourceWatcher.addWatch(cfg.GameCfgFileName, filePath ->
		{
			GameData gamedata = loadGameData(filePath);
			if (gamedata != null)
			{
				GameData.setInstance(gamedata.init(cfg.group));
			}
		});
		if (GameData.getInstance() == null)
		{
			logger.error("server game config file load failed !");
			return false;
		}

		resourceWatcher.addWatch(cfg.mapDeployFileName, filePath ->
		{
			Map<Integer, Integer> cfgs = deployConf.loadConfigs(filePath);
			if (cfgs != null)
			{
				deployConf.setConfigs(cfgs);
			}
		});
		if (deployConf.getMapDelpoy() == null)
		{
			logger.error("server map deploy config file load failed !");
			return false;
		}

		Thread.setDefaultUncaughtExceptionHandler((t, e) ->
		{
			logger.error("Uncaughted exception[" + e.getMessage() + "], throwed by thread[" + t.getName() + "]", e);
			System.exit(0);
		});
		//executor.

		start();

		return true;
	}

	void start()
	{
		logger.info("@@##>>> ms begin start ...");
		managerMap.start();
		managerRPC.start();
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		timer10millistask.future = executor.scheduleAtFixedRate(timer10millistask, 1000, TIME_TICK_INTERVAL, TimeUnit.MILLISECONDS);
		logger.info("ms start " + GameTime.getTime());
	}

	void destroy()
	{
		logger.info("ms destroy ...");
		if (timer10millistask.future != null)
			timer10millistask.future.cancel(false);
		if (timertask.future != null)
			timertask.future.cancel(false);
		managerRPC.destroy();
		managerMap.destroy();
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
		MapServer ms = new MapServer();
		String fileCfg = am.get("-cfgfile", "ms.cfg");
		if (!ms.init(fileCfg))
		{
			System.err.println("server initialize failed ... ");
			return;
		}
		if (am.containsKey("bg"))
		{
			ms.initSignal();
		}
		else
		{
			ket.util.FileSys.pauseWaitInput();
		}
		ms.destroy();
	}

	private Config cfg;
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private DeployConf deployConf;
	private RPCManager managerRPC;
	private Logger logger = Logger.getLogger("msLogger");
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
	private final Timer10MillisTask timer10millistask = new Timer10MillisTask();
	public static final int TIME_TICK_INTERVAL = 40;
	private MapManager managerMap;

	private int openDay;

}
