
package i3k.util;

import i3k.util.FileWatchdog;

import i3k.util.FileWatchdogHandler;

import org.apache.log4j.Logger;

public class ResourceWatcher
{
	public ResourceWatcher()
	{
		
	}
	
	public void setLogger(Logger logger)
	{
		fileWatchdogs.setLogger(logger);
	}
	
	public void fini()
	{
		fileWatchdogs.clearAllWatch();
	}
	
	public void onTimer(int timeTick)
	{
		//每分钟的30秒检查
		if ( (timeTick + 30) % 60 == 0 )
		{
			fileWatchdogs.checkFiles();
		}
	}
	
	public void addWatch(String fileName, FileWatchdogHandler fileChangeHandler)
	{
		fileWatchdogs.watchFile(fileName, fileChangeHandler);
	}
	
	public void removeWatch(String fileName)
	{
		fileWatchdogs.cancelWatch(fileName);
	}
	
	private FileWatchdog.WatchedFiles fileWatchdogs = new FileWatchdog.WatchedFiles();
}