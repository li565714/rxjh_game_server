
package i3k.logger;

import i3k.util.GameTime;
import i3k.util.ResourceWatcher;

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

@SuppressWarnings("restriction")
public class LogServer implements SignalHandler
{

	public static class Config
	{
		public NetAddress addrListen = new NetAddress("0.0.0.0", 9109);
		
		public int id = 1;
		
		public String log4jCfgFileName = "ls.log4j.properties";
		
		public String databaseDriver = "com.mysql.jdbc.Driver";
		public String databaseHost = "localhost";
		public String databasePort = "3306";
		public String databaseUser = "root";
		public String databasePassword = "";
		public String databaseName = "tlog";
		
		public String tlogCfgFileName = "qsg_tlog.xml";
		
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
			addrListen.host = skv.getString("LogServer", "host", addrListen.host);
			addrListen.port = skv.getInteger("LogServer", "port", addrListen.port);
			id = skv.getInteger("LogServer", "id", id);
			
			log4jCfgFileName = skv.getString("Log4j", "cfgFile", log4jCfgFileName);	
			
			databaseDriver = skv.getString("Database", "driver", databaseDriver);
			databaseHost = skv.getString("Database", "host", databaseHost);
			databasePort = skv.getString("Database", "port", databasePort);
			databaseUser = skv.getString("Database", "user", databaseUser);
			databasePassword = skv.getString("Database", "password", databasePassword);
			databaseName = skv.getString("Database", "database", databaseName);
			
			tlogCfgFileName = skv.getString("Tlog", "cfgFile", tlogCfgFileName);
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
		managerRPC.onTimer();
		logDatabase.onTimer();
	}
	
	private Config loadConfig(String configFileName)
	{
		logger.info("load ls config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{

			logger.info("load ls config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read ls config file failed .");
			return null;
		}

	}

	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			logger.info("logger server ID is " +  cfg.id);
			this.cfg = cfg;	
		}
		else
		{
			logger.info("reset ls config. ");
		}
	}
	
	
	public Config getConfig()
	{
		return cfg;
	}
	
	public ResourceWatcher getResourceManager()
	{
		return resourceWatcher;
	}
	
	Logger getLogger()
	{
		return logger;
	}
	
	Logger getFLogger()
	{
		return flogger;
	}
	
	Logger getTLogger()
	{
		return tlogger;
	}
	
	RPCManagerLogServer getRPCManager()
	{
		return managerRPC;
	}
	
	LogDB getLogDataBase()
	{
		return logDatabase;
	}
	
	LogDBTables getLogDBTables()
	{
		return logDBTables;
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
	
	public LogServer()
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
		managerRPC = new RPCManagerLogServer(this);	
		logDatabase = new LogDB(this);
		logDBTables = new LogDBTables(this);
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

		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Uncaughted exception[" + e.getMessage() + "], throwed by thread[" + t.getName() + "]", e);
            System.exit(0);
        });

		return start();
	}
	
	boolean start()
	{
		logger.info("@@##>>> ls begin start ...");
		if (!logDBTables.start())
			return false;
		if (!logDatabase.start())
			return false;
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		managerRPC.start();
		logger.info("ls start " + GameTime.getTime());
		return true;
	}
	
	void destroy()
	{
		if ( timertask.future != null )
			timertask.future.cancel(false);
		managerRPC.destroy();
		logDatabase.destroy();
		logger.info("ls main executor shutdown start");
		executor.shutdown();
		try
		{
			while( ! executor.awaitTermination(1, TimeUnit.SECONDS) ) { }
		}
		catch(Exception ex)
		{			
		}
		logger.info("ls main executor shutdown ok");
	}
	
	public static void main(String[] args)
	{
		ArgsMap am = new ArgsMap(args);
		LogServer ps = new LogServer();
		String fileCfg = am.get("-cfgfile", "ls.cfg");
		if (!ps.init(fileCfg))
		{
			System.err.println("log server initialize failed ... ");
			return;
		}
		if (am.containsKey("bg"))
		{
			ps.initSignal();
		}
		else
		{
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
		ps.destroy();
	}

	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private RPCManagerLogServer managerRPC;
	private LogDB logDatabase;
	private LogDBTables logDBTables;
	private Logger logger = Logger.getLogger("lsLogger");
	private Logger flogger = Logger.getLogger("fLogger");
	private Logger tlogger = Logger.getLogger("tLogger");
	Config cfg;
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
}
