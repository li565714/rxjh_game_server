
package i3k.auction;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
import i3k.DBRoleConsignments;
import i3k.SBean;
import i3k.gs.GameData;

public class AuctionServer implements SignalHandler
{

	public static class Config
	{
		public NetAddress addrAcutionListen = new NetAddress("127.0.0.1", 7108);
		public NetAddress addrAlarmListen = new NetAddress("127.0.0.1", 1191);
		public int areaId = 1;
		
		public String dbcfgfile = "./dbauction/dbcfg.xml";
		public int nIOThread = 1;
		
		public String log4jCfgFileName = "as.log4j.properties";
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
			addrAcutionListen.host = skv.getString("AuctionServer", "host", addrAcutionListen.host);
			addrAcutionListen.port = skv.getInteger("AuctionServer", "port", addrAcutionListen.port);
			
			addrAlarmListen.host = skv.getString("AlarmServer", "host", addrAlarmListen.host);
			addrAlarmListen.port = skv.getInteger("AlarmServer", "port", addrAlarmListen.port);
			
			areaId = skv.getInteger("AuctionServer", "area", areaId);
			
			dbcfgfile = skv.getString("KDB", "cfgFile", dbcfgfile);
			nIOThread = skv.getInteger("KIO", "ioThreadCount", nIOThread);
			
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
			catch(Throwable t)
			{
				logger.error("Uncaughted exception[" + t.getMessage() + "], throwed by timer thread", t);
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
		managerAuction.onTimer(timeTick);
		gameService.onTimer(timeTick);
		managerGroupBuy.onTimer(timeTick);
	}
	
	
	private Config loadConfig(String configFileName)
	{
		logger.info("load as config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{
			logger.info("load as config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read as config file failed .");
			return null;
		}
		
	}

	
	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			logger.info("set acution server config");
			this.cfg = cfg;	
		}
		else
		{
			logger.info("reset as config. ");
		}
	}
	
	private GameData loadGameData(String filePath)
	{
		try
		{
			logger.info("try load gamedata : " + filePath + " ...");
			SBean.GameDataCFGS gamedata = new SBean.GameDataCFGS();
			if(  Stream.loadObjLE(gamedata, new File(filePath)) )
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
		catch(Exception ex)
		{
			ex.printStackTrace();
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
	
	public ResourceWatcher getResourceWatcher()
	{
		return resourceWatcher;
	}
	
	public RPCManager getRPCManager()
	{
		return managerRPC;
	}
	
	public AuctionManager getAuctionManager()
	{
		return managerAuction;
	}
	
	public GameService getGameService()
	{
		return gameService;
	}
	
	public GroupBuyManager getGroupBuyManager()
	{
		return managerGroupBuy;
	}
	
	public AuctionServer()	
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
		managerAuction = new AuctionManager(this);
		gameService = new GameService(this);
		managerGroupBuy = new GroupBuyManager(this);
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
                GameData.setInstance(gamedata);
            }
        });
		if (GameData.getInstance() == null)
		{
			logger.error("server game config file load failed !");
			return false;
		}

		
		Thread.setDefaultUncaughtExceptionHandler(
				(t, e) -> {
                    logger.error("Uncaughted exception[" + e.getMessage() + "], throwed by thread[" + t.getName() + "]", e);
                    System.exit(0);
                });
		
		db = ket.kdb.Factory.newDB();
		db.setLogger(logger);
		Path p = Paths.get(cfg.dbcfgfile);
		db.open(p.getParent(), p);
		db.execute(new AuctionInitTrans());
		
		//start();
		
		return true;
	}
	
	public class AuctionInitTrans implements Transaction
	{	
		public AuctionInitTrans()
		{
		}

		@Override
		public boolean doTransaction()
		{	
			getAuctionManager().init(consignments);
			logger.info("load all auction consignments ");
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if( errcode == ErrorCode.eOK )
			{
				start();
			}
			else
			{
				logger.error("auction consignments init failed");
				System.exit(0);
			}
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRoleConsignments> consignments;
	}
	
	void start()
	{
		logger.info("@@##>>> as begin start ...");
		managerAuction.start();
		managerRPC.start();
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		logger.info("as start " + GameTime.getTime());
	}
	
	void destroy()
	{
		if ( timertask.future != null )
			timertask.future.cancel(false);
		managerRPC.destroy();
		managerAuction.destroy();
		resourceWatcher.fini();
		logger.info("main executor shutdown start");
		executor.shutdown();
		try
		{
			while( ! executor.awaitTermination(1, TimeUnit.SECONDS) ) { }
		}
		catch(Exception ignored)
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
		if( bSingalHandled )
			return;
		if( sig.getName().equals("TERM") )
		{
			System.out.println("recieved term");
			bSingalHandled = true;
			latch.countDown();
		}
	}
	

	
	public static void main(String[] args)
	{
		
		ArgsMap am = new ArgsMap(args);
		AuctionServer as = new AuctionServer();
		String fileCfg = am.get("-cfgfile", "as.cfg");
		if (!as.init(fileCfg))
		{
			System.err.println("server initialize failed ... ");
			return;
		}
		if( am.containsKey("bg") )
		{
			as.initSignal();
		}
		else
		{
			ket.util.FileSys.pauseWaitInput();
		}
		as.destroy();
	}

	private Config cfg;
	private DB db;
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private RPCManager managerRPC;
	private Logger logger = Logger.getLogger("asLogger");
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
	private AuctionManager managerAuction;
	private GameService gameService;
	private GroupBuyManager managerGroupBuy;
}
