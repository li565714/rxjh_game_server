package i3k.exchange;

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
import ket.kio.NetAddress;
import ket.util.ArgsMap;
import ket.util.SKVMap;
import ket.util.Stream;
import i3k.util.GameTime;
import i3k.util.ResourceWatcher;
import i3k.SBean;
import i3k.gs.GameData;

@SuppressWarnings("restriction")
public class ExchangeServer implements SignalHandler
{

	public static class Config
	{
		public NetAddress addrExchangeListen = new NetAddress("127.0.0.1", 7109);
		public NetAddress addrSocial = new NetAddress("127.0.0.1", 9102);
		public NetAddress addrAlarmListen = new NetAddress("127.0.0.1", 1191);
		
		public int areaId = 1;
		
		public int nIOThread = 1;
		public int pIOFailedPerTimes = 1;
		
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
			addrExchangeListen.host = skv.getString("ExchangeServer", "host", addrExchangeListen.host);
			addrExchangeListen.port = skv.getInteger("ExchangeServer", "port", addrExchangeListen.port);
			
			addrSocial.host = skv.getString("SocialClient", "host", addrSocial.host);
			addrSocial.port = skv.getInteger("SocialClient", "port", addrSocial.port);
			
			addrAlarmListen.host = skv.getString("AlarmServer", "host", addrAlarmListen.host);
			addrAlarmListen.port = skv.getInteger("AlarmServer", "port", addrAlarmListen.port);
			
			areaId = skv.getInteger("ExchangeServer", "area", areaId);
			
			nIOThread = skv.getInteger("KIO", "ioThreadCount", nIOThread);
			pIOFailedPerTimes = skv.getInteger("Debug", "pIOFailedPerTimes", pIOFailedPerTimes);
			
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
		managerExchange.onTimer(timeTick);
		resourceWatcher.onTimer(timeTick);
	}

	private Config loadConfig(String configFileName)
	{
		logger.info("load es config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{

			logger.info("load es config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read es config file failed .");
			return null;
		}

	}

	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			logger.info("set exchange server config");
			this.cfg = cfg;
		}
		else
		{
			logger.info("reset pIOFailedPerTimes " + cfg.pIOFailedPerTimes);
			this.cfg.pIOFailedPerTimes = cfg.pIOFailedPerTimes;
			getRPCManager().setAllCounter(cfg.pIOFailedPerTimes == 1);
			logger.info("reset es config. ");
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

	public ExchangeManager getExchangeManager()
	{
		return managerExchange;
	}
	
	public ExchangeServer()
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
		managerExchange = new ExchangeManager(this);
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

		start();
		return true;
	}


	void start()
	{
		logger.info("@@##>>> es begin start ...");
		managerExchange.start();
		managerRPC.start();
		timertask.future = executor.scheduleAtFixedRate(timertask, 3, 1, TimeUnit.SECONDS);
		logger.info("es start " + GameTime.getTime());
	}

	void destroy()
	{
		if (timertask.future != null)
			timertask.future.cancel(false);
		managerRPC.destroy();
		managerExchange.destroy();
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
		ExchangeServer es = new ExchangeServer();
		String fileCfg = am.get("-cfgfile", "es.cfg");
		if (!es.init(fileCfg))
		{
			System.err.println("server initialize failed ... ");
			return;
		}
		if (am.containsKey("bg"))
		{
			es.initSignal();
		}
		else
		{
			ket.util.FileSys.pauseWaitInput();
		}
		es.destroy();
	}

	private Config cfg;
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private ResourceWatcher resourceWatcher = new ResourceWatcher();
	private RPCManager managerRPC;
	private Logger logger = Logger.getLogger("esLogger");
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
	private ExchangeManager managerExchange;
}
