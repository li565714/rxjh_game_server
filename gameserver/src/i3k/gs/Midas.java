
package i3k.gs;

import i3k.util.GameTime;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import sun.misc.BASE64Encoder;

import org.json.JSONObject;





import ket.util.ArgsMap;

import javax.crypto.Mac;  
import javax.crypto.spec.SecretKeySpec;

class HMACSHA1 {  
  
    private static final String HMAC_SHA1 = "HmacSHA1";  
  
    /** 
     * 生成签名数据 
     *  
     * @param data 待加密的数据 
     * @param key  加密使用的key 
     * @return 生成MD5编码的字符串  
     * @throws InvalidKeyException 
     * @throws NoSuchAlgorithmException 
     */  
    public static String getSignature(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {  
        SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);  
        Mac mac = Mac.getInstance(HMAC_SHA1);  
        mac.init(signingKey);
        return new BASE64Encoder().encode(mac.doFinal(data));  
    }  
    
    public static String get(String key, String src)
    {
    	try
    	{
    		return getSignature(src.getBytes("UTF-8"), key.getBytes("UTF-8"));
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return null;
    	
    }
}  


class HttpUtil
{

	public static final int CONNECT_TIMEOUT = 5 * 1000;
	public static final int READ_TIMEOUT = 5 * 1000;
	
	public static String httpGet(String strUrl, String sid, String stype, String path, GameServer gs)
	{
		HttpURLConnection conn=null;
		try {
			URL url = new URL(strUrl);
			conn = (HttpURLConnection)url.openConnection();
			
			//
			String cookie = "session_id=" + sid + ";session_type=" + stype + ";org_loc=" + path;
			gs.getLogger().debug("cookie=" + cookie);
			conn.setRequestProperty("Cookie", cookie);
			conn.setRequestProperty("Connection", "Keep-Alive");
						
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			conn.connect();
			
			return recv(conn);			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (conn!=null)
				conn.disconnect();
		}
		return null;
	}
	
	public static String recv(HttpURLConnection conn)
	{
		InputStream is=null;
		try {
			is = conn.getInputStream();
			int pageSize = 256;
			int readNum = 0;
			String response = new String();
			byte[] data = new byte[pageSize];
			do {
				readNum = is.read(data,0,pageSize);
				if (readNum>0)
				{
					response += new String(data,0,readNum, "UTF-8");
				}
			} while (readNum<pageSize && readNum>0);
			//parse json
			return response;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				if (is!=null)
					is.close();
			}
			catch (Exception ignored) { }
		}
	}
}

public class Midas
{
	
	public static class UserInfo
	{
		public UserInfo()
		{
			
		}
		
		public UserInfo(String gameID, String gameChannel, String uid, String token)
		{
			this.gameID = gameID;
			this.gameChannel = gameChannel;
			this.uid = uid;
			this.token = token;
		}
		
		
		public String gameID = "520050";
		public String gameChannel = "2001";
		public String uid = "4C7B9D7F3214BD3D23C088E37B26B314";
		public String token = "EC3770C26313C12DE2098BF56A91A799";
	}
	
	public static class BalanceResult
	{		
		public BalanceResult(int errCode)
		{
			this.errCode = errCode;
		}
		
		public int errCode;
		public int ret;
		public int balance;
		public int gen_balance;
		public int first_save;
		public int save_amt;
	}
	
	public static interface GetBalanceCallback
	{
		public void onCallback(UserInfo uinfo, BalanceResult res);
	}
	
	public interface Task
	{
		public static final int ERR_BADKEY = -5;
		public static final int ERR_CLOSE = -4;
		public static final int ERR_PARSE = -3;
		public static final int ERR_RECV = -2;
		public static final int ERR_CONN = -1;
		public static final int ERR_OK = 0;
		
		public void runHTTP();
		public void runCallback();
	}
	
	public Midas(GameServer gs)
	{
		this.gs = gs;
		GameServer.Config cfg = gs.getConfig();
		if( cfg.loginHttpThreadCount <= 0 )
			executorHTTP = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("MidasHTTP");
		else
			executorHTTP = ket.util.ExecutorTool.newScheduledThreadPool(cfg.loginHttpThreadCount, "MidasHTTP");
			
		if( cfg.loginHttpCallbackThreadCount <= 0 )
			executorCallback = ket.util.ExecutorTool.newSingleThreadScheduledExecutor("MidasCallback");
		else
			executorCallback = ket.util.ExecutorTool.newScheduledThreadPool(cfg.loginHttpCallbackThreadCount, "MidasCallback");
	}
	
	public void start()
	{
	}
	
	public int getHTTPTaskQueueSize()
	{
		return taskCountHTTP.get();
	}
	
	public int getCallbackTaskQueueSize()
	{
		return taskCountCallback.get();
	}
	
	public int getHTTPRejectedTaskQueueSize()
	{
		return taskRejectCountHTTP.get();
	}
	
	public int getCallbackRejectedTaskQueueSize()
	{
		return taskRejectCountCallback.get();
	}
		
	void exec(final Task task, final boolean bCallback)
	{
		try
		{
			taskCountHTTP.incrementAndGet();
			executorHTTP.execute(() -> {
                task.runHTTP();
                if( bCallback )
                {
                    try
                    {
                        taskCountCallback.incrementAndGet();
                        executorCallback.execute(() -> {
                            task.runCallback();
                            taskCountCallback.decrementAndGet();
                        });
                    }
                    catch(RejectedExecutionException rex)
                    {
                        taskRejectCountCallback.incrementAndGet();
                        taskCountCallback.decrementAndGet();
                        task.runCallback();
                    }
                }
                taskCountHTTP.decrementAndGet();
            });
		}
		catch(RejectedExecutionException rex)
		{
			taskRejectCountHTTP.incrementAndGet();
			taskCountHTTP.decrementAndGet();
			if( bCallback )
				task.runCallback();
		}
	}
	
	public void destroy()
	{
		executorHTTP.shutdown();
		try
		{
			boolean bFinish = false;
			for(int i = 0; i < 3; ++i)
			{
				bFinish = executorHTTP.awaitTermination(1, TimeUnit.SECONDS);
				if( bFinish )
				{
					break;
				}
			}
			if( ! bFinish )
			{
				gs.getLogger().warn("midas http shutdownNow, size=" + taskCountHTTP);
				executorHTTP.shutdownNow();	
			}
		}
		catch(Exception ex)
		{			
			ex.printStackTrace();
		}
		executorCallback.shutdown();
		try
		{
			boolean bFinish = false;
			for(int i = 0; i < 3; ++i)
			{
				bFinish = executorCallback.awaitTermination(1, TimeUnit.SECONDS);
				if( bFinish )
				{
					break;
				}
			}
			if( ! bFinish )
			{
				gs.getLogger().warn("midas httpcallback shutdownNow, size=" + taskCountCallback);
				executorHTTP.shutdownNow();	
			}
		}
		catch(Exception ex)
		{			
			ex.printStackTrace();
		}
	}	
	
	public static interface LoginVerifyCallback
	{
		public void onCallback(int sid, boolean success);
	}
	
	public void loginVerify(final UserInfo uinfo, final int sid, boolean loginNormal, final LoginVerifyCallback callback)
	{
		if( uinfo.uid == null || uinfo.token == null || uinfo.gameID == null )
		{
			callback.onCallback(sid, false);
			return;
		}
		gs.getLogger().debug("loginVerify, uid=" + uinfo.uid + ",  token=" + uinfo.token);
		
		if ( gs.getConfig().loginVerify == 0 || !loginNormal || getHTTPTaskQueueSize() >= gs.getConfig().loginHttpTaskWaterline)
		{
			callback.onCallback(sid, true);
			return;
		}
		
		exec(new Task()
		{	
			int resultNum = 0;
			String retMsg = "";
			@Override
			public void runHTTP()
			{				
				try
				{	
					long timeStamp = GameTime.getTimeMillis();
					String url = gs.getConfig().loginVerifyURL + "/Check/index";
					//String params = "{\"token\":\"" + uinfo.token + "\",\"uid\":\"" + uinfo.uid + "\",\"serverid\":\"" + gs.getConfig().id + "\",\"channelid\":\"" + uinfo.channelID + "\",\"gameid\":\"" + uinfo.gameID + "\"}";
					Map<String, String> paramsmap = new HashMap<>();
					paramsmap.put("token", uinfo.token);
					paramsmap.put("uid", uinfo.uid);
					paramsmap.put("serverid", String.valueOf(gs.getConfig().id));
					paramsmap.put("channelid", uinfo.gameChannel);
					paramsmap.put("gameid", uinfo.gameID);
					gs.getLogger().debug("loginVerify, url=" + url + ", params=" + paramsmap);
					
					String retString = i3k.util.HttpUtils.readContentFromPost(url, paramsmap, 5000, 5000);
					
					timeStamp = GameTime.getTimeMillis() - timeStamp;
					if (timeStamp > 500)
						gs.getLogger().info("loginVerify, url=" + url + ", params=" + paramsmap + ", ret=" + retString + ", cost time " + timeStamp);
					else
						gs.getLogger().debug("loginVerify, url=" + url + ", params=" + paramsmap + ", ret=" + retString);
					JSONObject jo = new JSONObject(retString); 
					if (jo.getString("code").equals("100"))
						resultNum = 1;
					else
						resultNum = -2;
					retMsg = jo.getString("msg");
						
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

			@Override
			public void runCallback()
			{
				if(resultNum==1)
				{
					callback.onCallback(sid, true);
					gs.getLogger().debug("login verify succ, gameid= " + uinfo.gameID + ", channelid= " + uinfo.gameChannel + ", uid=" + uinfo.uid + ", ret=" + resultNum + ",msg=" + retMsg);
				}
				else
				{
					callback.onCallback(sid, false);
					gs.getLogger().warn("login verify failed, gameid= " + uinfo.gameID + ", channelid= " + uinfo.gameChannel + ", uid=" + uinfo.uid + ", ret=" + resultNum + ",msg=" + retMsg);
				}
			}

		}, true);
	}
	
	private final ExecutorService executorHTTP;
	private final ExecutorService executorCallback;
	private GameServer gs;
	
	private AtomicInteger taskCountHTTP = new AtomicInteger();
	private AtomicInteger taskCountCallback = new AtomicInteger();
	
	private AtomicInteger taskRejectCountHTTP = new AtomicInteger();
	private AtomicInteger taskRejectCountCallback = new AtomicInteger();
	
	public static void main(String[] args)
	{
		ArgsMap am = new ArgsMap(args);
		final Midas midas = new Midas(new GameServer());
		midas.start();
		System.out.println("start");
		ket.util.FileSys.pauseWaitInput();
		midas.destroy();
	}
}
