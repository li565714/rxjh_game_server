
package i3k.proxy;


import i3k.util.GameTime;
import i3k.util.ResourceWatcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ket.kio.NetAddress;
import ket.util.ArgsMap;
import ket.util.SKVMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sun.misc.Signal;
import sun.misc.SignalHandler;

@SuppressWarnings("restriction")
public class IDIPProxyServer implements SignalHandler
{

	public static class Config
	{		
		public NetAddress addrIDIP = new NetAddress("127.0.0.1", 9101);
		
		public String log4jCfgFileName = "ps.log4j.properties";
		
		public String forwardTableFileName = "idiptable.xml";
		
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
			addrIDIP.host = skv.getString("IDIPServer", "host", addrIDIP.host);
			addrIDIP.port = skv.getInteger("IDIPServer", "port", addrIDIP.port);
			forwardTableFileName = skv.getString("IDIPServer", "forwardTableFile", forwardTableFileName);
			
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
		resourceWatcher.onTimer(timeTick);
	}
	
	private Config loadConfig(String configFileName)
	{
		logger.info("load ps config file : " + configFileName + " ......");
		Config cfg = new Config();
		if (cfg.load(configFileName))
		{
			logger.info("load ps config file success.");
			return cfg;
		}
		else
		{
			logger.warn("read ps config file failed .");
			return null;
		}

	}

	private void resetConfig(Config cfg)
	{
		if (this.cfg == null)
		{
			this.cfg = cfg;	
		}
		else
		{
			logger.info("reset ps config. ");
		}
	}
	
	
	public Config getConfig()
	{
		return cfg;
	}
	
	
	Logger getLogger()
	{
		return logger;
	}
	
	public ResourceWatcher getResourceManager()
	{
		return resourceWatcher;
	}
	
	ForwardTable getForwardTable()
	{
		return forwardtbl;
	}
	
	RPCManager getRPCManager()
	{
		return managerRPC;
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
	
	public IDIPProxyServer()
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
		forwardtbl = new ForwardTable(this);
		managerRPC = new RPCManager(this);
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

		start();

		return true;
	}
	
	void start()
	{
		logger.info("@@##>>>  idip proxy server begin start ...");
		forwardtbl.start();
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		managerRPC.start();
		logger.info("idip proxy server start");
	}
	
	void destroy()
	{
		if ( timertask.future != null )
			timertask.future.cancel(false);
		managerRPC.destroy();
		logger.info("main executor shutdown start");
		executor.shutdown();
		try
		{
			while( ! executor.awaitTermination(1, TimeUnit.SECONDS) ) { }
		}
		catch(Exception ex)
		{			
		}
		forwardtbl.destroy();
		logger.info("main executor shutdown ok");
	}
	
	
	public static void main(String[] args)
	{
		ArgsMap am = new ArgsMap(args);
		IDIPProxyServer ps = new IDIPProxyServer();
		String fileCfg = am.get("-cfgfile", "ps.cfg");
		if (!ps.init(fileCfg))
		{
			System.err.println("server initialize failed ... ");
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
	private Config cfg;
	private ForwardTable forwardtbl;
	private RPCManager managerRPC;
	private Logger logger = Logger.getLogger("psLogger");
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
}
