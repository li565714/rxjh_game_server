
package i3k.logger;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import java.util.Date;

import i3k.util.DBUtil;

public class LogDB
{
	
	class SqlWriter
	{
		DBUtil dbUtil;
		Connection connection;
		int failedCount = 0;
		boolean busy = false;
		SqlWriter(DBUtil dbUtil)
		{
			this.dbUtil = dbUtil;
		}
		
		boolean init()
		{
			connection = dbUtil.connect();
			return connection != null;
		}
		
		void destory()
		{
			dbUtil.closeConnection(connection);
			connection = null;
		}
		
		void retryConnect()
		{
			if (connection != null)
				dbUtil.closeConnection(connection);
			connection = dbUtil.connect();
		}
		
		boolean tryWriteImpl(String sql)
		{
			boolean ret = false;
			try
			{
				if (connection == null)
					init();
				ret = dbUtil.executeUpdate(connection, sql);
			}
			catch (Exception e)
			{
				
			}
			return ret;
		}
		
		boolean tryWrite(String sql, int retryCount)
		{
			int retry = 0;
			while (!tryWriteImpl(sql))
			{
				if (++retry >= retryCount)
				{
					return false;
				}
				retryConnect();
			}
			return true;
		}
		
		boolean write(String sql)
		{
			if (!tryWrite(sql, 1))
			{
				++failedCount;
				if (failedCount >= 100)
				{
					failedCount = 0;
					retryConnect();
					return tryWrite(sql, 1);
				}
				return false;
			}
			return true;
		}
		
		boolean write(Collection<String> sqls)
		{
			for (String sql : sqls)
			{
				if (!this.write(sql))
					return false;
			}
			return true;
		}
		
		boolean trysetUsing()
		{
			if (!this.busy)
			{
				this.busy = true;
				return true;
			}
			return false;
		}
		
		void free()
		{
			this.busy = false;
		}
	}
	
	public interface WriteHandler
	{
		boolean doWrite(SqlWriter writer);
	}
	class SqlWriterPool
	{
		DBUtil dbUtil = new DBUtil(ls.getLogger());
		List<SqlWriter> writers = new ArrayList<SqlWriter>();
		int nextTry = 0;
		
		public SqlWriterPool()
		{
		}
		
		boolean start(int size)
		{
			dbUtil.init(ls.getConfig().databaseDriver, 
				    ls.getConfig().databaseHost, ls.getConfig().databasePort,
				    ls.getConfig().databaseUser, ls.getConfig().databasePassword,
				    ls.getConfig().databaseName);
			for (int i = 0; i < size; ++i)
			{
				SqlWriter writer = new SqlWriter(dbUtil);
				if (!writer.init())
				{
					while (--i >= 0)
					{
						writers.get(i).destory();
					}
					ls.getLogger().info("init dababase connect failed!");
					return false;
				}
				this.writers.add(writer);
			}
			ls.getLogger().info("init dababase connect ok.");
			return true;
		}
		
		void destory()
		{
			for (SqlWriter writer : writers)
			{
				writer.destory();
			}
		}
		
		boolean tryWrite(WriteHandler handler)
		{
			SqlWriter writer = getWriter();
			if (writer != null)
			{
				try
				{
					return handler.doWrite(writer);
				}
				catch (Exception e)
				{
					ls.getLogger().info("try write cause exception .", e);
					return false;
				}
				finally
				{
					writer.free();
				}
			}
			return false;
		}
		
		private synchronized SqlWriter getWriter()
		{
		    final int writersize = writers.size();
		    int index = nextTry;
		    int endCnt = 0;
		    nextTry = (nextTry+1)%writersize;
		    
		    do 
		    {
		        SqlWriter writer = writers.get(index++ % writersize);
		        endCnt += 1;
		        if (writer!=null && writer.trysetUsing())
		            return writer;
		    }
		    while(endCnt<writersize);
		    
			return null;
		}
	}
	
	public LogDB(LogServer ls)
	{
		this.ls = ls;
		pool = new SqlWriterPool();
	}
	
	public boolean start()
	{
		ls.getLogger().info("logDB start ...");
		if (!pool.start(MAX_SQL_INSTER_TASKS))
			return false;
		fetchLogExecutor.execute(() -> fetchLog());
		return true;
	}
	
	public void destroy()
	{
		ls.getLogger().info("logDB destroy ...");
		try
		{
			fetchLogExecutor.shutdownNow();
			insertLogSqlExecutor.awaitTermination(5, TimeUnit.SECONDS);
			List<Runnable> tasks = insertLogSqlExecutor.shutdownNow();
			ls.getLogger().info("cancel " + tasks.size() + " insert log sql task.");
		}
		catch (Exception e)
		{
			ls.getLogger().warn("shutdown executor cause exception", e);
		}
		finally
		{
			pool.destory();
		}
		ls.getLogger().info("logDB destroy end.");
	}
	
	public boolean writeLogToDB(String log)
	{
		try
		{
			Collection<String> sqls = ls.getLogDBTables().getLogSqls(log);
			if (sqls == null)
			{
				ls.getLogger().warn("try create sql failed! .. " + log);
				return false;	
			}
			return pool.tryWrite((writer) -> writer.write(sqls));
		}
		catch (Exception e)
		{
			ls.getLogger().warn("write log to db task exception :", e);
			return false;
		}
		finally
		{
			logCompletedTaskCount.incrementAndGet();
		}
	}
	
	
	void fetchLog()
	{
		final List<String> logs = new ArrayList<String>();
		try
		{
			do 
			{
				logs.clear();
				String flog = recvLogs.take();
				logs.add(flog);
				recvLogs.drainTo(logs);
				if (logTaskCount.get() == 0)
					ls.getLogger().debug("begin to add task ...");
				for (final String log : logs)
				{
					insertLogSqlExecutor.execute(() -> 
						{
							if (!writeLogToDB(log.trim()))
								ls.getFLogger().info(log);
						});
				}
				logTaskCount.addAndGet(logs.size());
				ls.getLogger().debug(new Date()+": add " + logs.size() + " log task ==>(" + logTaskCount + ", "  + logCompletedTaskCount + ")");
			}
			while (true);
		}
		catch (InterruptedException e)
		{
			ls.getLogger().warn("take log from queue by interrupted " + e.getMessage());
		}
		catch (Exception e)
		{
			ls.getLogger().warn("take log from queue exception :", e);
		}
	}
	
	void receiveLog(String log)
	{
		ls.getTLogger().info(log);
		try
		{
			recvLogs.put(log);
		}
		catch (Exception e)
		{
			ls.getLogger().warn("put log in queue exception :", e);
		}
	}
	
	public long getTaskCount()
	{
		return logTaskCount.get() - logCompletedTaskCount.get();
	}
	
	public void onTimer()
	{
		long curLogTaskCount = logTaskCount.get();
		long curLogCompletedTaskCount = logCompletedTaskCount.get();
		ls.getLogger().info("produce " + (curLogTaskCount-lastLogTaskCount) + " task, consume " + (curLogCompletedTaskCount-lastLogCompletedTaskCount) + " task, " + (curLogTaskCount-curLogCompletedTaskCount) + " task left.");
		lastLogTaskCount = curLogTaskCount;
		lastLogCompletedTaskCount = curLogCompletedTaskCount;
	}
	
	long lastLogTaskCount;
	long lastLogCompletedTaskCount;
	AtomicLong logTaskCount = new AtomicLong();
	AtomicLong logCompletedTaskCount = new AtomicLong();
	LogServer ls;
	SqlWriterPool pool;
	BlockingQueue<String> recvLogs = new LinkedBlockingQueue<String>();
	private ExecutorService fetchLogExecutor = Executors.newSingleThreadScheduledExecutor();
	private ExecutorService insertLogSqlExecutor = Executors.newFixedThreadPool(MAX_SQL_INSTER_TASKS);
	static final int MAX_SQL_INSTER_TASKS = 6;
	static final int LOGS_CACHE_CAP = 100;
}
