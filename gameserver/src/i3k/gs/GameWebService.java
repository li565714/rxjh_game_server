package i3k.gs;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.WebServiceException;

import i3k.GWebService;
import i3k.util.GameTime;


class WebServiceFactory
{
	 /**
     * get game web service port GService
     * @param wsdlUrl as http://localhost:8080/gs/gws?wsdl
     * @param targetNS as http://GameWebService.gs.i3k/[指定的targetNamespace命名空间URI]
     * @param serviceName as GWServiceImplService[实现类名称+Service]
     * @return interface GService, not null success
     */
    public static GWebService getWebService(String wsdlUrl, String targetNS, String serviceName) throws MalformedURLException, WebServiceException
    {
    	URL url = new URL(wsdlUrl);
		QName qname = new QName(targetNS, serviceName);
		Service service = Service.create(url, qname); 
		GWebService gws = service.getPort(GWebService.class);
		Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider)gws).getRequestContext();
		requestContext.put("com.sun.xml.internal.ws.connect.timeout", 5000);
		requestContext.put("com.sun.xml.internal.ws.request.timeout", 5000);
		return gws;
    }
    
    /**
     * get game web service port GService
     * @param host : web service ip
     * @param port : web service port
     * @return interface GService, not null success
     */
    public static GWebService getWebService(String host, int port) throws MalformedURLException, WebServiceException 
    {
    	String wsdlUrl = String.format("http://%s:%d/gs/gws?wsdl", host, port);
		String targetNS = "http://webservice.gameservice.joypiegame.com/";
		String serviceName = "ServiceImplService";
		return getWebService(wsdlUrl, targetNS, serviceName);
    }
}

public class GameWebService
{
	public GameWebService(GameServer gs)
	{
		this.gs = gs;
	}
	
	public void startup()
	{
		serviceClient.init(gs.getConfig().addrWebService.host, gs.getConfig().addrWebService.port);
	}
	
	public void shutdown()
	{
		serviceTaskManager.shutdown();
	}
	
	void useService(GWService service)
	{
		serviceTaskManager.startTask(serviceClient, service);
	}
	
	void onTimer(int timeTick)
	{
		serviceTaskManager.checkTimeOutTask(timeTick);
	}

	public void doUseCDKey(String cdkey, int bid, int gid, int maxusecnt, int gsid, int roleid, String rolename, String channel, String uid, UseCDKeyCallback callback)
	{
		useService(new UseCDKeyService(cdkey.toUpperCase(), bid, gid, maxusecnt, gsid, roleid, rolename, channel, uid, callback));
	}
	
	public void doVerifyRegister(String key, int gsid, String username, VerifyRegisterCallback callback)
	{
		useService(new VerifyRegisterService(key.toUpperCase(), gsid, username, callback));
	}
	
	public void doQueryCDKey(int bid, String cdkey, int gsid, int roleid, String channel, String gameapp, int level, int viplvl, QueryCDKeyCallback callback)
	{
		useService(new QueryCDKeyService(bid, cdkey.toUpperCase(), gsid, roleid, channel, gameapp, level, viplvl, callback));
	}
	
	public void doExchangeCDKey(int bid, String cdkey, int gsid, int roleid, String rolename, String channel, String uid, String gameapp, int level, int viplvl, int paypoint, ExchangeCDKeyCallback callback)
	{
		useService(new ExchangeCDKeyService(bid, cdkey.toUpperCase(), gsid, roleid, rolename, channel, uid, gameapp, level, viplvl, paypoint, callback));
	}
	
	public void doQueryCashBack(int bid, int gsid, String channel, String uid, int roleid, String roleName, QueryCashBackCallback callback)
	{
	    useService(new QueryCashBackService(bid, gsid, channel, uid, roleid, roleName, callback));
	}
	
	public void doExchangeCashBack(int bid, int gsid, String channel, String uid, int roleid, String roleName, TakeCashBackCallback callback)
	{
	    useService(new TakeCashBackService(bid, gsid, channel, uid, roleid, roleName, callback));
	}
	
	class GameWebServiceClient
	{
		String host = "localhost";
		int port = 8080;
		GWebService service;
		
		public GameWebServiceClient()
		{
			
		}
		
		public void init(String host, int port)
		{
			this.host = host;
			this.port = port;
			service = tryCreateWebService(host, port);
		}
		
		GWebService tryCreateWebService(String host, int port)
		{
			try
			{
				gs.getLogger().info("get web service from " + host + " : " + port);
				return WebServiceFactory.getWebService(host, port);
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				gs.getLogger().warn(e.getMessage());
			}
			return null;
		}
		
		
		public int useCDKey(String cdkey, int bid, int gid, int maxusecnt, int gsid, int roleid, String rolename, String channel, String uid)
		{
			try 
	    	{
				if (service == null)
					service = tryCreateWebService(host, port);
				if (service != null)
					return service.useCDKey(cdkey, bid, gid, maxusecnt, gsid, roleid, rolename, channel, uid);
	    	}
	    	catch (Exception e) 
	    	{
	    		//e.printStackTrace();
	    		gs.getLogger().warn(e.getMessage());
	    	}
			return GAME_WEB_SERVICE_NETWORK_EXCEPTION;
		}
		
		public int verifyKey(String key, int gsid, String username)
		{
			try 
	    	{
				if (service == null)
					service = tryCreateWebService(host, port);
				if (service != null)
					return service.verifyRegister(key, gsid, username);
	    	}
	    	catch (Exception e) 
	    	{
	    		//e.printStackTrace();
	    		gs.getLogger().warn(e.getMessage());
	    	}
			return GAME_WEB_SERVICE_NETWORK_EXCEPTION;
		}
		
		public String queryCDKey(int bid, String cdkey, int gsid, int roleid, String channel, String gameapp, int level, int viplvl)
		{
			try 
	    	{
				if (service == null)
					service = tryCreateWebService(host, port);
				if (service != null)
					return service.queryCDKey(bid, cdkey, gsid, roleid, channel, gameapp, level, viplvl);
	    	}
	    	catch (Exception e) 
	    	{
	    		//e.printStackTrace();
	    		gs.getLogger().warn(e.getMessage());
	    	}
			return Integer.toString(GAME_WEB_SERVICE_NETWORK_EXCEPTION);
		}
		
		public int exchangeCDKey(int bid, String cdkey, int gsid, int roleid, String rolename, String channel, String uid, String gameapp, int level, int viplvl,int paypoint)
		{
			try 
	    	{
				if (service == null)
					service = tryCreateWebService(host, port);
				if (service != null)
					return service.exchangeCDKey(bid, cdkey, gsid, roleid, rolename, channel, uid, gameapp, level, viplvl, paypoint);
	    	}
	    	catch (Exception e) 
	    	{
	    		//e.printStackTrace();
	    		gs.getLogger().warn(e.getMessage());
	    	}
			return GAME_WEB_SERVICE_NETWORK_EXCEPTION;
		}
		
		public int queryCashback(int bid, int gsid, String channel, String uid, int roleid, String roleName)
		{
			try 
	    	{
				if (service == null)
					service = tryCreateWebService(host, port);
				if (service != null)
					return service.queryCashBack(bid, gsid, channel, uid, roleid, roleName);
	    	}
	    	catch (Exception e) 
	    	{
	    		//e.printStackTrace();
	    		gs.getLogger().warn(e.getMessage());
	    	}
			return GAME_WEB_SERVICE_NETWORK_EXCEPTION;
		}
		
		public int exchangeCashback(int bid, int gsid, String channel, String uid, int roleid, String roleName)
		{
			try 
	    	{
				if (service == null)
					service = tryCreateWebService(host, port);
				if (service != null)
					return service.exchangeCashBack(bid, gsid, channel, uid, roleid, roleName);
	    	}
	    	catch (Exception e) 
	    	{
	    		//e.printStackTrace();
	    		gs.getLogger().warn(e.getMessage());
	    	}
			return GAME_WEB_SERVICE_NETWORK_EXCEPTION;
		}
	}
	
	interface UseCDKeyCallback
	{
		void onCallback(int result);
	}
	interface VerifyRegisterCallback
	{
		void onCallback(int result);
	}
	interface QueryCDKeyCallback
	{
		void onCallback(String result);
	}
	interface ExchangeCDKeyCallback
	{
		void onCallback(int result);
	}
	interface QueryCashBackCallback
	{
	    void onCallback(int result);
	}
	interface TakeCashBackCallback
	{
	    void onCallback(int result);
	}
	interface GWService
	{
		void useService(GameWebServiceClient client);
		void onSuccessCallback();
		void onErrorCallback(int errCode);
	}
	static class UseCDKeyService implements GWService
	{
		String cdkey;
		int bid;
		int gid;
		int maxusecnt;
		int gsid;
		int roleid;
		String rolename;
		String channel;
		String uid;
		UseCDKeyCallback callback;
		int result = -100;
		public UseCDKeyService(String cdkey, int bid, int gid, int maxusecnt, int gsid, int roleid, String rolename, String channel, String uid, UseCDKeyCallback callback)
		{
			this.cdkey = cdkey;
			this.bid = bid;
			this.gid = gid;
			this.maxusecnt = maxusecnt;
			this.gsid = gsid;
			this.roleid = roleid;
			this.rolename = rolename;
			this.channel = channel;
			this.uid = uid;
			this.callback = callback;
		}
		
		public void useService(GameWebServiceClient client)
		{
			result = client.useCDKey(cdkey, bid, gid, maxusecnt, gsid, roleid, rolename, channel, uid);
		}
		
		public void onSuccessCallback()
		{
			callback.onCallback(result);
		}
		
		public void onErrorCallback(int errCode)
		{
			callback.onCallback(errCode);
		}
		
		public String toString()
		{
			return "role " + roleid + " use cdkey (" + cdkey + "), batchID=" + bid + ", genID=" + gid + ", result=" + result;
		}
	}
	
	static class QueryCashBackService implements GWService
	{
	    int bid;
	    int gsid;
	    String channel;
	    String uid;
	    int roleid;
	    String roleName;
	    QueryCashBackCallback callback;
	    
	    int result;
	    
        @Override
        public void useService(GameWebServiceClient client)
        {
            result = client.queryCashback(bid, gsid, channel, uid, roleid, roleName);
        }

        @Override
        public void onSuccessCallback()
        {
            callback.onCallback(result);
        }

        @Override
        public void onErrorCallback(int errCode)
        {
            callback.onCallback(errCode);
        }
        
        public QueryCashBackService(int bid, int gsid, String channel, String uid, int roleid, String roleName, QueryCashBackCallback callback)
        {
            this.bid = bid;
            this.gsid = gsid;
            this.channel = channel;
            this.uid = uid;
            this.roleid = roleid;
            this.roleName = roleName;
            this.callback = callback;
        }
	    
		public String toString()
		{
			return "role " + roleid + " query takeCashback gsid " + gsid + ", channel=" + channel + ", uid=" + uid + ", result=" + result;
		}
	}
	
	static class TakeCashBackService implements GWService
	{
	    int bid;
	    int gsid;
	    String channel;
	    String uid;
	    int roleid;
	    String roleName;
	    TakeCashBackCallback callback;
	    
	    int result;
	    
        @Override
        public void useService(GameWebServiceClient client)
        {
            result = client.exchangeCashback(bid, gsid, channel, uid, roleid, roleName);
        }

        @Override
        public void onSuccessCallback()
        {
            callback.onCallback(result);
        }

        @Override
        public void onErrorCallback(int errCode)
        {
            callback.onCallback(errCode);
        }
	    
        public TakeCashBackService(int bid, int gsid, String channel, String uid, int roleid, String roleName, TakeCashBackCallback callback)
        {
            this.bid = bid;
            this.gsid = gsid;
            this.channel = channel;
            this.uid = uid;
            this.roleid = roleid;
            this.roleName = roleName;
            this.callback = callback;
        }
        
		public String toString()
		{
			return "role " + roleid + " use takeCashback gsid " + gsid + ", channel=" + channel + ", uid=" + uid + ", result=" + result;
		}
	}
	
	static class VerifyRegisterService implements GWService
	{
		String key;
		int gsid;
		String username;
		VerifyRegisterCallback callback;
		int result = -100;
		public VerifyRegisterService(String key, int gsid, String username, VerifyRegisterCallback callback)
		{
			this.key = key;
			this.gsid = gsid;
			this.username = username;
			this.callback = callback;
		}
		
		public void useService(GameWebServiceClient client)
		{
			result = client.verifyKey(key, gsid, username);
		}
		
		public void onSuccessCallback()
		{
			callback.onCallback(result);
		}
		
		public void onErrorCallback(int errCode)
		{
			callback.onCallback(errCode);
		}
		
		public String toString()
		{
			return "gs " + gsid + " username (" + username + ") verify register key (" + key + ") , result=" + result;
		}
	}
	
	static class QueryCDKeyService implements GWService
	{
		int bid;
		String cdkey;
		int gsid;
		int roleid;
		String channel;
		String gameapp;
		int level;
		int viplvl;
		QueryCDKeyCallback callback;
		String result = Integer.toString(-100);
		public QueryCDKeyService(int bid, String cdkey, int gsid, int roleid, String channel, String gameapp, int level, int viplvl, QueryCDKeyCallback callback)
		{
			this.bid = bid;
			this.cdkey = cdkey;
			this.gsid = gsid;
			this.roleid = roleid;
			this.channel = channel;
			this.gameapp = gameapp;
			this.level = level;
			this.viplvl = viplvl;
			this.callback = callback;
		}
		
		public void useService(GameWebServiceClient client)
		{
			result = client.queryCDKey(bid, cdkey, gsid, roleid, channel, gameapp, level, viplvl);
		}
		
		public void onSuccessCallback()
		{
			callback.onCallback(result);
		}
		
		public void onErrorCallback(int errCode)
		{
			callback.onCallback(Integer.toString(errCode));
		}
		
		public String toString()
		{
			return "role " + roleid + " query cdkey batchID=" + bid + " (" + cdkey + "), result=" + result;
		}
	}
	
	static class ExchangeCDKeyService implements GWService
	{
		int bid;
		String cdkey;
		int gsid;
		int roleid;
		String rolename;
		String channel;
		String uid;
		String gameapp;
		int level;
		int viplvl;
		int paypoint;
		ExchangeCDKeyCallback callback;
		int result = -100;
		public ExchangeCDKeyService(int bid, String cdkey, int gsid, int roleid, String rolename, String channel, String uid, String gameapp, int level, int viplvl, int paypoint, ExchangeCDKeyCallback callback)
		{
			this.bid = bid;
			this.cdkey = cdkey;
			this.gsid = gsid;
			this.roleid = roleid;
			this.rolename = rolename;
			this.channel = channel;
			this.uid = uid;
			this.gameapp = gameapp;
			this.level = level;
			this.viplvl = viplvl;
			this.paypoint = paypoint;
			this.callback = callback;
		}
		
		public void useService(GameWebServiceClient client)
		{
			result = client.exchangeCDKey(bid, cdkey, gsid, roleid, rolename, channel, uid, gameapp, level, viplvl, paypoint);
		}
		
		public void onSuccessCallback()
		{
			callback.onCallback(result);
		}
		
		public void onErrorCallback(int errCode)
		{
			callback.onCallback(errCode);
		}
		
		public String toString()
		{
			return "role " + roleid + " exchange cdkey batchID=" + bid + " (" + cdkey + "), result=" + result;
		}
	}
	
	static class GameWebServiceTask
	{
		static AtomicInteger NextTaskID = new AtomicInteger();
		int id;
		GWService service;
		int execTime;
		public GameWebServiceTask(GWService service)
		{
			this.id = NextTaskID.addAndGet(1);
			this.service = service;
		}
		
		public void setStart()
		{
			this.execTime = GameTime.getTime();
		}
		
		public boolean isTooOld(int now)
		{
			return execTime + GAME_WEB_SERVICE_MAX_WAIT_TIME <= now;
		}
		
		
		public void execTask(final GameWebServiceClient client, final GameServer gs)
		{
			try
			{
				service.useService(client);
			}
			catch (Exception e)
			{
				gs.getLogger().warn(e.getMessage());
			}
		}
		
		public void setCallResult(final GameServer gs)
		{
			try
			{
				service.onSuccessCallback();
			}
			catch (Exception e)
			{
				gs.getLogger().warn(e.getMessage());
			}
		}
		
		public void logTimeOutResult(final GameServer gs)
		{
			gs.getLogger().warn(service.toString() + " , but timeout before !!!");
		}
		
		public void setTimeOut(final GameServer gs)
		{
			try
			{
				service.onErrorCallback(GAME_WEB_SERVICE_TIME_OUT);
			}
			catch (Exception e)
			{
				gs.getLogger().warn(e.getMessage());
			}
		}
	}
	
	
	
	class ServiceTaskManager
	{
		ExecutorService executor = Executors.newCachedThreadPool();
		Map<Integer, GameWebServiceTask> tasks = new ConcurrentHashMap<Integer, GameWebServiceTask>();
		public ServiceTaskManager()
		{
			
		}
		
		void shutdown()
		{
			try
			{
				executor.shutdown();
				if(!executor.awaitTermination(3, TimeUnit.SECONDS))
				{
					executor.shutdownNow();
				}
			}
			catch(Exception ex)
			{			
			}
		}
		
		void startTask(final GameWebServiceClient client, GWService service)
		{
			final GameWebServiceTask task = new GameWebServiceTask(service);
			tasks.put(task.id, task);
			task.setStart();
			long taskstarttime = GameTime.getTimeMillis();
			executor.execute(new Runnable()
				{
					public void run()
					{
						long startruntime = GameTime.getTimeMillis();
						if (startruntime - taskstarttime >= 2000)
							gs.getLogger().warn(task.id + " wait start time too long : " + (startruntime - taskstarttime));
						if (!tasks.containsKey(task.id))
							return;
						task.execTask(client, gs);
						long finishtime = GameTime.getTimeMillis();
						if (finishtime - startruntime >= 2000)
							gs.getLogger().warn(task.id + " finish time too long : " + (finishtime - startruntime));
						GameWebServiceTask gst = tasks.remove(task.id);
						if (gst == task)
						{
							task.setCallResult(gs);
						}
						else
						{
							task.logTimeOutResult(gs);
						}
					}
				});
		}
		
		void checkTimeOutTask(int now)
		{
			List<GameWebServiceTask> timeoutTasks = getTimeOutTasks(now);
			for (GameWebServiceTask task : timeoutTasks)
			{
				task.setTimeOut(gs);
			}
		}
		
		List<GameWebServiceTask> getTimeOutTasks(int now)
		{
			List<GameWebServiceTask> timeoutTasks = new ArrayList<GameWebServiceTask>();
			Iterator<GameWebServiceTask> it = tasks.values().iterator();
			while (it.hasNext()) 
			{
				GameWebServiceTask task = it.next();
				if (task.isTooOld(now)) 
				{
					task = tasks.remove(task.id);
					if (task != null)
					{
						timeoutTasks.add(task);	
					}				
				}
			}
			return timeoutTasks;
		}
	}
	
	GameServer gs;
	GameWebServiceClient serviceClient = new GameWebServiceClient();
	ServiceTaskManager serviceTaskManager = new ServiceTaskManager();
	final static int GAME_WEB_SERVICE_MAX_WAIT_TIME = 3;
	final static int GAME_WEB_SERVICE_NETWORK_EXCEPTION = -101;
	final static int GAME_WEB_SERVICE_TIME_OUT = -102;
}


