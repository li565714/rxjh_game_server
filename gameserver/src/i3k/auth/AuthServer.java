
package i3k.auth;


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
import i3k.SBean;
import i3k.gs.GameData;

public class AuthServer
{

	public static class Config
	{
		public NetAddress addrAuthListen = new NetAddress("127.0.0.1", 9100);
		
		public int nIOThread = 1;
		
		public Config()
		{
			
		}
		
		
		void reset(String host, int port)
		{
			addrAuthListen.host = host;
			addrAuthListen.port = port;
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
			}
		}
		
		public ScheduledFuture<?> future = null;
	}
	
	private void onTimer(int timeTick)
	{
		//getLogger().info("timeTick="+timeTick);
		managerRPC.onTimer(timeTick);
		managerAuth.onTimer(timeTick);
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
	
	public RPCManager getRPCManager()
	{
		return managerRPC;
	}
	
	public AuthManager getAuthManager()
	{
		return managerAuth;
	}
	
	
	public AuthServer()	
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
		}
		managerRPC = new RPCManager(this);	
		managerAuth = new AuthManager(this);
	}
	
	void start(String host, int port)
	{
		logger.info("@@##>>> auth begin start ...");
		cfg.reset(host, port);
		managerAuth.start();
		managerRPC.start();
		timertask.future = executor.scheduleAtFixedRate(timertask, 1, 1, TimeUnit.SECONDS);
		logger.info("as start " + GameTime.getTime());
	}
	
	void destroy()
	{
		if ( timertask.future != null )
			timertask.future.cancel(false);
		managerRPC.destroy();
		managerAuth.destroy();
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
	}
	
	

	private Config cfg = new Config();
	private volatile boolean bSingalHandled = false;
	private CountDownLatch latch = new CountDownLatch(1);
	private RPCManager managerRPC;
	private Logger logger = Logger.getLogger("ausLogger");
	private ScheduledExecutorService executor = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("TimerThread");
	private final TimerTask timertask = new TimerTask();
	private AuthManager managerAuth;
	
}
