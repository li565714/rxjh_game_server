// modified by ket.kio.RPCGen at Tue Apr 19 15:38:40 CST 2016.

package i3k.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import ket.kio.NetAddress;
import ket.kio.NetManager;
import i3k.SBean;
import i3k.rpc.Packet;
import i3k.util.GameTime;

public class AuthManager
{
	
	public AuthManager(AuthServer aus)
	{
		this.aus = aus;
	}
	
	public void start()
	{
	}
	
	public void destroy()
	{
	}
	
	public void onTimer(int now)
	{
		Iterator<Map.Entry<Integer, InvokeStub>> it = invokeStubs.entrySet().iterator();
		while( it.hasNext() )
		{
			InvokeStub stub = it.next().getValue();
			if( stub.timeout < now )
			{
				stub.ret = INVOKE_ERROR_GAME_SERVER_RESPONSE_TIMEOUT;
				stub.latch.countDown();
				it.remove();
			}
		}
	}
	
	public int setPayResult(int gsid, String channel, String uid, int roleid, String goodsid, int payLevel, String orderid, String payext)
	{
		if( gsid <= 0 || channel == null || channel.isEmpty() || uid == null || uid.isEmpty() || roleid <= 0 || goodsid == null || goodsid.isEmpty() || payext == null || payext.isEmpty() || orderid == null || orderid.isEmpty())
			return INVOKE_ERROR_PARAM_INVALID;
		
		InvokeStub stub = new InvokeStub();
		invokeStubs.put(stub.xid, stub);
		if (!aus.getRPCManager().notifyGameServerPayResult(stub.xid, gsid, channel, uid, roleid, goodsid, payLevel, orderid, payext))
		{
			invokeStubs.remove(stub.xid);
			return INVOKE_ERROR_GAME_SERVER_NOT_FOUND;
		}
		try
		{
			stub.latch.await();
		}
		catch(Exception ex)
		{			
		}
		return stub.ret;
	}
	
	public void onSetPayResultRes(int xid, int ret)
	{
		InvokeStub stub = invokeStubs.remove(xid);
		if( stub != null )
		{
			stub.ret = ret;
			stub.latch.countDown();
		}
	}
	
	class InvokeStub
	{
		public InvokeStub()
		{
			this.latch = new CountDownLatch(1);
			this.xid = xidSeed.incrementAndGet();
			this.timeout = GameTime.getTime() + 10;
			this.ret = -1000;
		}
		public CountDownLatch latch;
		public int xid;
		public int timeout;
		public int ret;
	}
	
	AuthServer aus;
	
	AtomicInteger xidSeed = new AtomicInteger(0);
	ConcurrentHashMap<Integer, InvokeStub> invokeStubs = new ConcurrentHashMap<>();
	
	static final int INVOKE_ERROR_PARAM_INVALID = -1;
	static final int INVOKE_ERROR_GAME_SERVER_NOT_FOUND = -2;
	static final int INVOKE_ERROR_GAME_SERVER_RESPONSE_TIMEOUT = -3;
}
