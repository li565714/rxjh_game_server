
package i3k.gs.test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ket.kio.NetAddress;
import ket.util.ArgsMap;
import ket.util.FileSys;
import ket.util.SKVMap;
import ket.util.Stream;
import i3k.SBean;
import i3k.gs.GameData;
import i3k.gs.GameServer.Config;
import i3k.util.GameTime;
import i3k.util.ResourceWatcher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sun.misc.Signal;
import sun.misc.SignalHandler;

@SuppressWarnings("restriction")
public class GameClient implements SignalHandler
{

	public static class Config
	{
		public int gsid = 1001;
		public NetAddress addrServer = new NetAddress("0.0.0.0", 1107);
		public int nIOThread = 8;
		public int nClient = 32;
		public int nLoginBatchSize = 200;
		public int reconnectInterval = 0;
		public int move = 1;
		public int robotIDStart = 0;
		public String gameAppId;
		public String channel;
		public int useOpenKey = 0;
		public String challengeFuncArg = "abcd1234efgh5678";
		public String randomUserNamePrefix = "B624064BA065E01CB73F83";
		public String randomRoleNamePrefix = "R_";
		
		
		public String loginKey;
		public String openKey;
		public String payToken;
		public String pf;
		public String pfKey;
		public int verPacket;
		public int verResource;
		
		public String log4jCfgFileName = "gc.log4j.properties";
		public String GameCfgFileName = "server_cfg.dat";
		
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
			gsid = skv.getInteger("GameServer", "id", gsid);
			addrServer.host = skv.getString("GameServer", "host", addrServer.host);
			addrServer.port = skv.getInteger("GameServer", "port", addrServer.port);
			
			nIOThread = skv.getInteger("KIO", "ioThreadCount", nIOThread);
			
			nClient = skv.getInteger("Robot", "nClient", nClient);
			nLoginBatchSize = skv.getInteger("Robot", "nLoginBatchSize", nLoginBatchSize);
			reconnectInterval = skv.getInteger("Robot", "reconnectInterval", reconnectInterval);
			move = skv.getInteger("Robot", "move", move);
			robotIDStart = skv.getInteger("Robot", "robotIDStart", robotIDStart);
			gameAppId = skv.getString("Robot", "gameAppId", gameAppId);
			channel = skv.getString("Robot", "channel", channel);
			useOpenKey = skv.getInteger("Robot", "useOpenKey", useOpenKey);
			challengeFuncArg = skv.getString("Robot", "challengeFuncArg", challengeFuncArg);
			randomUserNamePrefix = skv.getString("Robot", "userNamePrefix", randomUserNamePrefix);
			randomRoleNamePrefix = skv.getString("Robot", "roleNamePrefix", randomRoleNamePrefix);
			
			verPacket = skv.getInteger("Robot", "verPacket", verPacket);
			verResource = skv.getInteger("Robot", "verResource", verResource);
			
			log4jCfgFileName = skv.getString("Log4j", "cfgFile", log4jCfgFileName);
			GameCfgFileName = skv.getString("GameCfg", "cfgFile", GameCfgFileName);
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
		resourceWatcher.onTimer(timeTick);
		performanceStats.onTimer(timeTick);
		managerRobots.onTimer(timeTick);
	}
	
	public ResourceWatcher getResourceWatcher()
	{
		return resourceWatcher;
	}
	
	public GameClient()
	{
		if (!logger.getAllAppenders().hasMoreElements())
		{
			org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
			ca.setName("_AA_");
			ca.setWriter(new java.io.PrintWriter(System.out));
			ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
			logger.addAppender(ca);
		}
		performanceStats = new PerformanceStats(this);
		managerRobots = new RobotsManager(this); 
	}
	
	
	public Config getConfig()
	{
		return cfg;
	}
	
	
	private Config loadConfig(String configFileName)
	{
		logger.info("load gc config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{

			logger.info("load gc config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read gc config file failed .");
			return null;
		}
	}
	
	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			logger.info("server address is : " + cfg.addrServer);
			this.cfg = cfg;
		}
		else
		{
			logger.info("reset client config. ");
			this.cfg.nClient = cfg.nClient;
			this.cfg.nLoginBatchSize = cfg.nLoginBatchSize;
			this.cfg.reconnectInterval = cfg.reconnectInterval;
			this.cfg.move = cfg.move;
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
			System.err.println("test game client config file load failed !");
			return false;
			
		}
		
		resourceWatcher.addWatch(cfg.log4jCfgFileName, PropertyConfigurator::configure);
		
		GameData gamedata = loadGameData(cfg.GameCfgFileName);
		if (gamedata == null)
		{
			logger.error("server game config file load failed !");
			return false;
		}
        GameData.setInstance(gamedata.init(0));
        
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Uncaughted exception[" + e.getMessage() + "], throwed by thread[" + t.getName() + "]", e);
            System.exit(0);
        });
        return true;
	}
	
	void start()
	{
		logger.info("gc start ...");
		managerRPCs = new RPCManagerClient[cfg.nIOThread];
		for (int i = 0; i < managerRPCs.length; ++i)
			managerRPCs[i] = new RPCManagerClient(this);
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		performanceStats.start();
		for (RPCManagerClient managerRPC : managerRPCs)
			managerRPC.start();
		managerRobots.startUp();
		logger.info("gc start " + GameTime.getTime());
	}
	
	void destroy()
	{
		logger.info("gc begin destroy ...");
		if ( timertask.future != null )
			timertask.future.cancel(false);
		managerRobots.shutDown();
		for (RPCManagerClient managerRPC : managerRPCs)
			managerRPC.destroy();
		performanceStats.destroy();
		executor.shutdown();
		resourceWatcher.fini();
		System.out.println("after executor shutdown");
		try
		{
			while( ! executor.awaitTermination(1, TimeUnit.SECONDS) ) { }
		}
		catch(Exception ignored)
		{			
		}
		logger.info("gc destroy");
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
	
	PerformanceStats getPerformanceStats()
	{
		return performanceStats;
	}
	
	RPCManagerClient getRPCManager(int key)
	{
		return managerRPCs[key%managerRPCs.length];
	}
	
	RobotsManager getRobotsManager()
	{
		return managerRobots;
	}
	
	public ScheduledExecutorService getExecutor()
	{
		return executor;
	}
	
	
	public Logger getLogger()
	{
		return logger;
	}
	
	public static void main(String[] args)
	{
		ArgsMap am = new ArgsMap(args);
		GameClient gc = new GameClient();
		if (!gc.init(am.get("-cfgfile", "gc.cfg")))
		{
			System.err.println("game client initialize failed ... ");
			return;
		}
		gc.start();
		if (am.containsKey("bg"))
		{
			gc.initSignal();
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
				}
			}
			else
			{
				ket.util.FileSys.pauseWaitInput();
			}
		}
		gc.destroy();
	}
	
	Config cfg;
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private PerformanceStats performanceStats;
	private RPCManagerClient[] managerRPCs;
	private RobotsManager managerRobots;
	
	private Logger logger = Logger.getLogger("gcLogger");
	
	
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
}
