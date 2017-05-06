
package i3k.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.time.Instant;
import java.time.Clock;
import java.time.ZoneId;
import org.apache.log4j.Logger;



public class GameTime
{
	private static ThreadLocal<SimpleDateFormat> dateTimeFormat = new ThreadLocal<SimpleDateFormat>()
			{
				@Override
				protected SimpleDateFormat initialValue()
				{
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
			};
	private static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>()
				{
					@Override
					protected SimpleDateFormat initialValue()
					{
						return new SimpleDateFormat("yyyy-MM-dd");
					}
				};
	private static ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>()
				{
					@Override
					protected SimpleDateFormat initialValue()
					{
						return new SimpleDateFormat("HH:mm:ss");
					}
				};
	private static int timeOffset = 0;
	public GameTime()
	{
		
	}

	public static boolean setServerTimeOffset(int offset)
	{
		if (offset < timeOffset)
			return false;
		timeOffset = offset;
		return true;
	}
	
	public static int getServerTimeOffset()
	{
		return timeOffset;
	}

	public static long getTimeMillis()
	{
		return System.currentTimeMillis() + timeOffset * 1000;
	}
	
	private static Date getCurServerDate()
	{
		return new Date(new Date().getTime() + timeOffset * 1000);
	}
	
	private static int getLocalTimeOffset()
	{
		return Calendar.getInstance().get(Calendar.ZONE_OFFSET)/1000;
	}
	
	public static int getServerTimeFromGMTTime(int gmttime)
	{
		return gmttime + getLocalTimeOffset();
	}
	
	public static int getGMTTimeFromServerTime(int time)
	{
		return time - getLocalTimeOffset();
	}
	
	public static int getGMTTime(Date date)
	{
		return (int)(date.getTime()/1000);
	}
	
	public static int getGMTTime()
	{
		return getGMTTime(getCurServerDate());
	}
	
	public static int getTime(Date date)
	{
		return getGMTTime(date) + getLocalTimeOffset();
	}
	
	public static int getTime()
	{
		return getTime(getCurServerDate());
	}
	
	public static Date getDateTime(int time)
	{
		return new Date((long)(time-getLocalTimeOffset())* 1000L);
	}
	
	public static int getSecondOfDay(int time)
	{
		return (time%86400);
	}
	
	public static int getSecondOfDay()
	{
		return getSecondOfDay(getTime());
	}
	
	public static int getSecondOfDayByOffset(int time, int o)
	{
		return ((time-o)%86400);
	}
	
	public static int getSecondOfDayByOffset(int o)
	{
		return getSecondOfDayByOffset(getTime(), o);
	}
	
	public static int getDay(int t)
	{
		return t / 86400;
	}
	
	public static int getDay()
	{
		return getDay(getTime());
	}
	
	public static int getDayByOffset(int time, int o)
	{
		return getDay(time - o);
	}
	
	public static int getDayByOffset(int o)
	{
		return getDayByOffset(getTime(), o);
	}
	
	public static int getWeek(int day)
	{
		return (day-3)/7;
	}
	
	public static int getWeekByOffset(int time, int weekDay, int offsetDayTime)
	{
		return getWeek(getDayByOffset(time, offsetDayTime) - weekDay);
	}
	
	public static int getWeekByOffset(int weekDay, int offsetDayTime)
	{
		return getWeekByOffset(getTime(),  weekDay, offsetDayTime);
	}
	
	public static int getDayTimeSpan()
	{
		return 86400;
	}
	
	public static int getDayTime(int secondOfDay)
	{
		return getDayStartTime(getDay()) + secondOfDay;
	}
	
	public static int getDayTime(int day, int secondOfDay)
	{
		return getDayStartTime(day) + secondOfDay;
	}
	
	public static int getDayStartTime(int day)
	{
		return day * 86400;
	}
	
	public static int getHourTime(int time)
	{
		return (time/3600)*3600;
	}
	
	public static int getHourTime()
	{
		return (getTime()/3600)*3600;
	}
	
	public static int getTimeH0(int time)
	{
		return time - time % 86400;
	}
	
	public static int getTimeH0()
	{
		return getTimeH0(getTime());
	}
	
	public static int getTimeByMinuteOffset(int time, short m)
	{
		return getTimeH0(time) + m * 60;
	}
	
	public static int getTimeByMinuteOffset(short m)
	{
		return getTimeByMinuteOffset(getTime(), m);
	}

	public static int getWeekday(int time)
	{
		return (getDay(time)-3)%7;
	}
	
	public static int getWeekdayByOffset(int time, int o)
	{
		return getWeekday(time - o);
	}
	
	public static int getWeekday()
	{
		return getWeekday(getTime());
	}
	
	public static boolean isWeekend()
	{
		int w = getWeekday(getTime());
		return w == 0 || w == 6;
	}
	
	public static int getSecsOfWeek(int time)
	{
		int w = getWeekday(time);
		return w * 86400 + time % 86400; 
	}
	
	public static int getSecsOfWeek()
	{
		return getSecsOfWeek(getTime()); 
	}
	
	
	public static String getDateTimeStampStr()
	{
		return getDateTimeStampStr(getCurServerDate());
	}
	
	public static String getDateTimeStampStr(Date date)
	{
		return dateTimeFormat.get().format(date);
	} 
	
	public static String getDateTimeStampStr(int time)
	{
		return getDateTimeStampStr(getDateTime(time));
	}

	public static String getDateStampStr()
	{
		return getDateStampStr(getCurServerDate());
	}
	
	public static String getDateStampStr(Date date)
	{
		return dateFormat.get().format(date);
	}
	
	public static String getDateStampStr(int time)
	{
		return getDateStampStr(getDateTime(time));
	}

	public static String getTimeStampStr()
	{
		return getTimeStampStr(getCurServerDate());
	}
	
	public static String getTimeStampStr(Date date)
	{
		return timeFormat.get().format(date);
	}
	
	public static String getTimeStampStr(int time)
	{
		return getTimeStampStr(getDateTime(time));
	}
	
	public static int parseDate(String str) throws java.text.ParseException
	{
		Date date = dateFormat.get().parse(str);
		return getTime(date);
	}
	
	public static int parseDateTime(String str) throws java.text.ParseException
	{
		Date date = dateTimeFormat.get().parse(str);
		return getTime(date);
	}

	//用于读配置的当日相对于00:00:00的偏移秒数
	public static int parseSecondOfDay(String str)
	{
		String[] strs = str.split(":");
		if (strs == null || (strs.length != 2 && strs.length != 3))
			return -2;
		try
		{
			int h = Integer.parseInt(strs[0]);
			int m = Integer.parseInt(strs[1]);
			int s = strs.length == 3 ? Integer.parseInt(strs[2]) : 0;
			return (h*60+m)*60+s;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	//用于读配置的日期时间，返回的时间经过时区修正，可以直接和服务器getTime()时间一样使用
	public static int parseSecondOfDate(String str)
	{
		String[] lst = str.split("\\.");
	    if ((lst == null) || (lst.length != 3)) {
	      return -2;
	    }
	    try
	    {
	      int y = Integer.parseInt(lst[0]);
	      int m = Integer.parseInt(lst[1]);
	      int d = Integer.parseInt(lst[2]);
	      Calendar c = Calendar.getInstance();
	      c.clear();
	      c.set(y, m - 1, d);
	      return (int)(c.getTime().getTime() / 1000L) + getLocalTimeOffset();
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    return -1;
	}
	
	public static void main(String[] args)
	{
		//org.apache.log4j.helpers.LogLog.setInternalDebugging(true);
		org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
		ca.setName("AA");
		ca.setWriter(new java.io.PrintWriter(System.out));
		ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
		Logger.getRootLogger().addAppender(ca);
		try
		{
			int t1 = parseDate("2015-05-15");
			int t2 = parseDateTime("2015-05-15 03:45:22");
			Logger.getRootLogger().info(getDateTimeStampStr(t1));
			Logger.getRootLogger().info(getDateTimeStampStr(t2));
			Logger.getRootLogger().info(getDateTimeStampStr(getGMTTime()));
			Logger.getRootLogger().info(getDateTimeStampStr());
//			int t3 = parseDateTime("2015-05-15");
//			int t4 = parseDate("2015-05-15 03:45:22");
//			Logger.getRootLogger().info(getTimeStampStr(t3));
//			Logger.getRootLogger().info(getTimeStampStr(t4));
			Instant instant = Instant.now();
			Logger.getRootLogger().info(instant);
			Logger.getRootLogger().info(ZoneId.systemDefault());
			Logger.getRootLogger().info(Clock.systemUTC().millis());
			Logger.getRootLogger().info(Clock.systemDefaultZone().millis());
		}
		catch (Exception e)
		{
			Logger.getRootLogger().warn("exception :", e);
		}
		
	}

    public static boolean isSameDay(int time)
    {
        return isSameDay(time, getTime());
    }
    
    public static boolean isSameDay(int time, int datumTime)
    {
        return getDay(datumTime) == getDay(time);
    }
}
