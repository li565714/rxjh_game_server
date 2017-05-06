
package i3k.gs;

import i3k.util.FileWatchdog;

import i3k.util.FileWatchdogHandler;


public class ResourceManager
{
	public ResourceManager(GameServer gs)
	{
		this.gs = gs;
		fileWatchdogs.setLogger(gs.getLogger());
	}
	
	public void setLogger()
	{
		fileWatchdogs.setLogger(gs.getLogger());
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
	
	private GameServer gs;
	private FileWatchdog.WatchedFiles fileWatchdogs = new FileWatchdog.WatchedFiles();
}