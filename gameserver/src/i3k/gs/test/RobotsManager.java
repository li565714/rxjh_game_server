
package i3k.gs.test;

import i3k.util.GameTime;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class RobotsManager
{
	
	public RobotsManager(GameClient gc)
	{
		this.gc = gc;
	}
	
	void startUp()
	{
		robotIDSeed.set(gc.getConfig().robotIDStart);
		taskExecutor = Executors.newFixedThreadPool(8);
		worker.future = gc.getExecutor().scheduleAtFixedRate(worker, 1000, 100, TimeUnit.MILLISECONDS);
	}
	
	void shutDown()
	{
		bDestroy = true;
		for (Robot robot : robots.values())
		{
			robot.destroy();
		}
		robots.clear();
		if ( worker.future != null )
			worker.future.cancel(false);
		try
		{
			taskExecutor.shutdown();
			while (!taskExecutor.awaitTermination(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
		}
	}
	
	void onTimer(int timeTick)
	{
		if( !bDestroy)
		{
			int count = gc.getConfig().nClient - robots.size();
			if (count > gc.getConfig().nLoginBatchSize)
				count = gc.getConfig().nLoginBatchSize;
			for(int i = 0; i < count; ++i)
			{
				this.createRobot();
			}
			int loginRobots = gc.getPerformanceStats().getLoginedRobots();
			if (loginRobots >= gc.getConfig().nClient && this.loginedRobotsCount < gc.getConfig().nClient)
				robots.values().forEach(robot -> robot.setMoveable(true));
			if (loginRobots < gc.getConfig().nClient && this.loginedRobotsCount >= gc.getConfig().nClient)
				robots.values().forEach(robot -> robot.setMoveable(false));
			if (this.loginedRobotsCount == loginRobots)
				this.sameLoginRobotsTimes++;
			else
				this.sameLoginRobotsTimes = 1;
			this.loginedRobotsCount = loginRobots;
		}
	}
	
	public boolean isLongTimeNotLogined()
	{
		return this.sameLoginRobotsTimes >= 10 && this.sameLoginRobotsTimes % 10 == 0 && this.loginedRobotsCount < gc.getConfig().nClient;
	}
	
	private class WorkerTask implements Runnable
	{
		@Override
		public void run()
		{
			int nowTime = GameTime.getTime();
			long timeMillis = GameTime.getTimeMillis();
			robots.values().forEach(robot -> {
				taskExecutor.execute(() -> robot.onTimer(nowTime, timeMillis));
			});
		}
		
		Future<?> future = null;
	}
	
	public void createRobot()
	{
		Robot robot = new Robot(gc, robotIDSeed.incrementAndGet());
		robot.start();
		robots.put(robot.getIDSeed(), robot);
	}
	
	private GameClient gc;
	volatile boolean bDestroy;
	AtomicInteger robotIDSeed = new AtomicInteger(0);
	WorkerTask worker = new WorkerTask();
	ConcurrentHashMap<Integer, Robot> robots = new ConcurrentHashMap<>();
	int loginedRobotsCount;
	int sameLoginRobotsTimes;
	ExecutorService taskExecutor;
}
