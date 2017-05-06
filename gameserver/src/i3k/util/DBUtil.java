
package i3k.util;

import org.apache.log4j.Logger;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.Stack;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

public class DBUtil
{
	Logger logger;
	String driver = "com.mysql.jdbc.Driver";
	String dbURL = "";
	public DBUtil(Logger logger)
	{
		this.logger = logger;
	}
	
	public void init(String driver, String host, String port, String userName, String password, String dbname)
	{
		if (logger != null)
			logger.info("init database init param [" + host + ":" + port +"]");
		setDriver(driver);
		setDBConnectionUrl(host, port, userName, password, dbname);
	}
	
	void setDriver(String driver)
	{
		this.driver = driver; 
		regeisterDriver();
	}
	
	private void regeisterDriver()
	{
		try
		{
			Class.forName(driver);
			if (logger != null)
				logger.debug("register database driver success.");
		}
		catch (Exception e)
		{
			if (logger != null)
				logger.warn("caught register database driver exception :", e);
		}
	}
	
	static String getUrl(String host, String port)
	{
		return "jdbc:mysql://" + host + ":" + port + "/";
	}
	
	static String getUser(String userName, String password)
    {
    	return "user=" + userName + (password.isEmpty() ? "" : "&password=" + password);
    }
	
	static String getDbConnectionUrl(String host, String port, String userName, String password, String dbname)
    {
    	return getUrl(host, port) + dbname + "?" + getUser(userName, password) + "&useUnicode=true&characterEncoding=UTF8&autoReconnect=true";
    }
	
	void setDBConnectionUrl(String host, String port, String userName, String password, String dbname)
	{
		dbURL = getDbConnectionUrl(host, port, userName, password, dbname);
	}
	
	String getDbConnectionUrl()
	{
		return dbURL;
	}
	
	public Connection connect() 
	{
        Connection con = null;
        try 
        {
        	String dburl = getDbConnectionUrl();
            con = DriverManager.getConnection(dburl);
            if (logger != null)
            	logger.info("connect to database success !");
        } 
        catch (Exception e) 
        {
        	if (logger != null)
        		logger.warn("caught database connection exception :", e);
        } 
        return con;
    }
	
	public void closeConnection(Connection con) 
	{
        try 
        {
        	if (con != null && !con.isClosed())
        	{
        		con.close();
        		if (logger != null)
        			logger.info("close database connect success !");
        	}
        } 
        catch (Exception e) 
        {
        	if (logger != null)
        		logger.warn("caught close database connection exception :", e);
        }
    }
	
	public boolean executeUpdate(Connection con, String sql)
	{
		Statement stm = null;
    	try 
    	{
    		stm = con.createStatement();
            stm.executeUpdate(sql);
    	}
    	catch (SQLException e)
    	{
    		if (logger != null)
    			logger.warn("caught execute update sql(" + sql + ") exception :", e);
    		return false;
    	}
    	finally
    	{
    		try
    		{
    			if(stm != null) 
	    			stm.close();
    		}
    		catch (Exception e)
    		{
    			if (logger != null)
    				logger.warn("caught close sql statement exception :", e);
    		}
    	}
    	return true;
    }
}
