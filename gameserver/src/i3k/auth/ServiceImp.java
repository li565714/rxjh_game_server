
package i3k.auth;

import i3k.auth.Service.ServiceException;
import i3k.gs.LoginManager;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ket.kio.NetAddress;
import ket.util.FileSys;

public class ServiceImp implements Service
{
	public ServiceImp()
	{
	}
	
	@Override
	public void start(String host, int port)
	{
		aus.start(host, port);
	}

	@Override
	public void destroy()
	{
		aus.destroy();
	}
	

	@Override
	public int setPayResult(int gsid, String channel, String uid, int roleid, String goodsid, int paylevel, String orderid, String payext) throws ServiceException
	{
		return aus.getAuthManager().setPayResult(gsid, channel, uid, roleid, goodsid, paylevel, payext, orderid);
	}
	
	AuthServer aus = new AuthServer();
}
