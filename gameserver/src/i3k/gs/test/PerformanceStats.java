package i3k.gs.test;

import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceStats
{

	public PerformanceStats(GameClient gc)
	{
		this.gc = gc;
	}

	void start()
	{
	}

	void destroy()
	{
	}

	void onTimer(int timeTick)
	{
		++tick;
		gc.getLogger().info(tick + " STAT CONNECT : CURCONNECT=" + (this.nConnectSuccess.get() - this.nConnectClose.get()) + ", DOCONNECT=" + this.nDoConnect.get() + "(reconnect times = " + this.nReconnect.get() + ", reconnect units = " + this.nReconnectUnits + "), SUCCESS=" + this.nConnectSuccess + ", READY=" + this.nConnectReady + ", CLOSE=" + this.nConnectClose + ", FAILED=" + this.nConnectFailed);
		gc.getLogger().info(tick + " STAT LOGIN : CURLOGIN=" + (this.nLoginOK.get() - this.nLogout.get()) + ", LOGOUT=" + this.nLogout.get() + ", SUCCESS=" + this.nLoginOK + ", ERROR=" + this.nLoginErr + ", PINGMODE=" + this.nPingMode + ", ADJUSTPOS=" + this.nAdjustPS);
		gc.getLogger().info("\n");
		this.nAdjustPS.set(0);
	}

	public void logStatDoConnect(boolean bReconnect, boolean firstTimeReconnect)
	{
		if (bReconnect)
			this.nReconnect.incrementAndGet();
		if (firstTimeReconnect)
			this.nReconnectUnits.incrementAndGet();
		this.nDoConnect.incrementAndGet();
	}

	public void logStatConnecteSuccess()
	{
		this.nConnectSuccess.incrementAndGet();
	}

	public void logStatClose()
	{
		this.nConnectClose.incrementAndGet();
	}

	public void logStatConnectFailed()
	{
		this.nConnectFailed.incrementAndGet();
	}
	
	public void logStatConnecteReady()
	{
		this.nConnectReady.incrementAndGet();
	}

	public void logStatLoginOk()
	{
		this.nLoginOK.incrementAndGet();
	}

	public void logStatLoginError()
	{
		this.nLoginErr.incrementAndGet();
	}

	public void logStatLogout()
	{
		this.nLogout.incrementAndGet();
	}

	public int getLoginedRobots()
	{
		return this.nLoginOK.get() - this.nLogout.get();
	}
	
	public void logStatPingMode(boolean pm)
	{
		if (pm)
			this.nPingMode.incrementAndGet();
		else
			this.nPingMode.decrementAndGet();
	}
	
	public void logStatAdjustPos()
	{
		this.nAdjustPS.incrementAndGet();
	}

	private GameClient gc;

	private volatile int tick = 0;

	private AtomicInteger nDoConnect = new AtomicInteger(0);
	private AtomicInteger nReconnect = new AtomicInteger(0);
	private AtomicInteger nReconnectUnits = new AtomicInteger(0);
	private AtomicInteger nConnectSuccess = new AtomicInteger(0);
	private AtomicInteger nConnectFailed = new AtomicInteger(0);
	private AtomicInteger nConnectClose = new AtomicInteger(0);
	private AtomicInteger nConnectReady = new AtomicInteger(0);

	private AtomicInteger nLoginOK = new AtomicInteger(0);
	private AtomicInteger nLoginErr = new AtomicInteger(0);
	private AtomicInteger nLogout = new AtomicInteger(0);
	private AtomicInteger nPingMode = new AtomicInteger(0);
	private AtomicInteger nAdjustPS = new AtomicInteger(0);

}
