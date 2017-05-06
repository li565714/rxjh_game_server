
package i3k.util;


import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;




public class FileWatchdog implements Runnable
{
	  protected String filename;
	  File file;
	  long lastModif = 0; 
	  FileWatchdogHandler fileChangeHandler;
	  Logger logger;
	  
	  public FileWatchdog(String filename, FileWatchdogHandler fileChangeHandler)
	  {
		  this.fileChangeHandler = fileChangeHandler;
		  setFile(filename); 
	  }
	  
	  public void setFile(String filename) 
	  {
		  this.filename = filename;
		  file = new File(filename);
		  checkAndHandleChange();
	  }
	  
	  public void setLogger(Logger logger)
	  {
		  this.logger = logger;
	  }

	  protected void checkAndHandleChange() 
	  {
		  if (file == null)
			  return;
		  boolean fileExists;
		  try 
		  {
			  fileExists = file.exists();
		  } 
		  catch(SecurityException  e) 
		  {
			  return;
		  }
		  if(fileExists)
		  {
			  long l = file.lastModified(); // this can also throw a SecurityException
			  long now = System.currentTimeMillis();
			  if (l/1000 == now/1000)
				  return;
			  if(l != lastModif) // however, if we reached this point this
			  {
				  lastModif = l;              // is very unlikely.
				  try
				  {
					  if (logger != null)
						  logger.debug("FileWatchdog: file("+file+") is changed ...");
					  if (fileChangeHandler != null)
						  fileChangeHandler.onFileChanged(filename);  
				  }
				  catch (Exception e)
				  {
					  if (logger != null)
						  logger.warn("FileWatchdog: handle file("+file+") change cause exception!", e);
				  }
			  }
		  }
		  else
		  {
			  if (logger != null)
				  logger.warn("FileWatchdog: file("+file+") is not exist!");
		  }
	  }

	@Override
	public void run()
	{
		checkAndHandleChange();
	}
	
	public static class WatchedFiles
	{
		private Map<String, FileWatchdog> fileWatchdogs = new TreeMap<>();
		Logger logger;
		
		public WatchedFiles()
		{
			
		}
		
		public synchronized void setLogger(Logger logger)
		{
			this.logger = logger;
//			if (!logger.getAllAppenders().hasMoreElements())
//			{
//				org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
//				ca.setName("_AA_");
//				ca.setWriter(new java.io.PrintWriter(System.out));
//				ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
//				logger.addAppender(ca);
//			}
			for (FileWatchdog fwd : fileWatchdogs.values())
			{
				fwd.setLogger(logger);
			}
		}
		
		public void checkFiles()
		{
			fileWatchdogs.values().forEach(FileWatchdog::run);
		}
		
		public synchronized void watchFile(String filename, FileWatchdogHandler dochange)
		{
			try
			{
				String filePath = new File(filename).getCanonicalPath();
				if (logger != null)
					logger.info("start  watch file (" + filePath +  ")");
				FileWatchdog fileWatchdog = new FileWatchdog(filePath, dochange);
				fileWatchdog.setLogger(logger);
				fileWatchdogs.put(filePath, fileWatchdog);
			}
			catch (Exception e)
			{
				if (logger != null)
					logger.warn("start  watch file " + filename + " cause exception!", e);	
			}
		}
		
		public synchronized void cancelWatch(String filename)
		{
			try
			{
				String filePath = new File(filename).getCanonicalPath();
				fileWatchdogs.remove(filePath);
				if (logger != null)
					logger.info("cancel watch file (" + filePath + ")");	
			}
			catch (Exception e)
			{
				if (logger != null)
					logger.warn("cancel watch file " + filename + " cause exception!", e);	
			}
		}
		
		public synchronized void clearAllWatch()
		{
			fileWatchdogs.clear();
		}
	}
	
	public static void main(String[] args)
	{
		//org.apache.log4j.helpers.LogLog.setInternalDebugging(true);
		org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
		ca.setName("AA");
		ca.setWriter(new java.io.PrintWriter(System.out));
		ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
		Logger.getRootLogger().addAppender(ca);
		WatchedFiles wfs = new WatchedFiles();
		wfs.setLogger(Logger.getRootLogger());
		wfs.watchFile("filename", null);
		wfs.cancelWatch("./filename");
	}
}
