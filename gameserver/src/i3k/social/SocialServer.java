package i3k.social;

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
import ket.kdb.Table;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.kdb.Transaction.AutoInit;
import ket.kdb.Transaction.ErrorCode;
import ket.kio.NetAddress;
import ket.util.ArgsMap;
import ket.util.SKVMap;
import ket.util.Stream;
import i3k.util.GameTime;
import i3k.util.ResourceWatcher;
import i3k.DBDemonHoleGroup;
import i3k.DBFightRanks;
import i3k.DBSocialTheme;
import i3k.DBSocialUser;
import i3k.SBean;
import i3k.fight.FightServer.FightInitTrans;
import i3k.gs.GameData;

@SuppressWarnings("restriction")
public class SocialServer implements SignalHandler
{

	public static class Config
	{
		public NetAddress addrSocialListen = new NetAddress("127.0.0.1", 9102);
		public NetAddress addrAlarmListen = new NetAddress("127.0.0.1", 1191);
		
		public String dbcfgfile = "./dbsocial/dbcfg.xml";
		public int nIOThread = 1;
		
		public String log4jCfgFileName = "es.log4j.properties";

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
			addrSocialListen.host = skv.getString("SocialServer", "host", addrSocialListen.host);
			addrSocialListen.port = skv.getInteger("SocialServer", "port", addrSocialListen.port);
			
			addrAlarmListen.host = skv.getString("AlarmServer", "host", addrAlarmListen.host);
			addrAlarmListen.port = skv.getInteger("AlarmServer", "port", addrAlarmListen.port);
			
			dbcfgfile = skv.getString("KDB", "cfgFile", dbcfgfile);
			nIOThread = skv.getInteger("KIO", "ioThreadCount", nIOThread);
			
			log4jCfgFileName = skv.getString("Log4j", "cfgFile", log4jCfgFileName);
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
		managerSocial.onTimer(timeTick);
		resourceWatcher.onTimer(timeTick);
	}

	private Config loadConfig(String configFileName)
	{
		logger.info("load ss config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{

			logger.info("load ss config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read ss config file failed .");
			return null;
		}

	}

	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			logger.info("set social server config");
			this.cfg = cfg;
		}
		else
		{
			logger.info("reset ss config. ");
		}
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

	public RPCManager getRPCManager()
	{
		return managerRPC;
	}

	public SocialManager getSocialManager()
	{
		return managerSocial;
	}
	
	public SocialServer()
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
		managerSocial = new SocialManager(this);
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

		Thread.setDefaultUncaughtExceptionHandler((t, e) ->
		{
			logger.error("Uncaughted exception[" + e.getMessage() + "], throwed by thread[" + t.getName() + "]", e);
			System.exit(0);
		});

		db = ket.kdb.Factory.newDB();
		db.setLogger(logger);
		Path p = Paths.get(cfg.dbcfgfile);
		db.open(p.getParent(), p);

		db.execute(new SocialInitTrans());
		return true;
	}

	public class SocialInitTrans implements Transaction
	{
		public SocialInitTrans()
		{
		}

		@Override
		public boolean doTransaction()
		{	
			{
				managerSocial.init(users, themes);
				logger.info("load social data");
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
				logger.error("social db init failed");
				System.exit(0);
			}
		}
		
		
		@AutoInit
		public TableReadonly<Long, DBSocialUser> users;
		@AutoInit
		public TableReadonly<Integer, DBSocialTheme> themes;
	}

	void start()
	{
		logger.info("@@##>>> ss begin start ...");
		managerSocial.start();
		managerRPC.start();
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		logger.info("ss start " + GameTime.getTime());
	}

	void destroy()
	{
		if (timertask.future != null)
			timertask.future.cancel(false);
		managerRPC.destroy();
		managerSocial.destroy();
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
	
	DB getDB()
	{
		return db;
	}
	
	public static void main(String[] args)
	{

		ArgsMap am = new ArgsMap(args);
		SocialServer ss = new SocialServer();
		String fileCfg = am.get("-cfgfile", "ss.cfg");
		if (!ss.init(fileCfg))
		{
			System.err.println("server initialize failed ... ");
			return;
		}
		if (am.containsKey("bg"))
		{
			ss.initSignal();
		}
		else
		{
			ket.util.FileSys.pauseWaitInput();
		}
		ss.destroy();
	}

	private Config cfg;
	private DB db;
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private RPCManager managerRPC;
	private Logger logger = Logger.getLogger("ssLogger");
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
	private SocialManager managerSocial;
}
