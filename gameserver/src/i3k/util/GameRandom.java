
package i3k.util;


import java.util.Random;
import java.security.SecureRandom;



import org.apache.log4j.Logger;



public class GameRandom
{
	public static final int ROLE_RAND_INTERVAL_MAX = 60;
	private static Random random = new Random();
	private static SecureRandom srandom = new SecureRandom();
	public GameRandom()
	{
		
	}
	
	public static Random getRandom()
	{
		return random;
	}
	
	public static int getRandInt(int min, int max)
	{
		if( min == max )
			return min;
		if( min > max )
		{
			int t = max;
			max = min;
			min = t;
		}
		return random.nextInt(max-min) + min;
	}
	
	public static float getRandFloat(float min, float max)
	{
		if( min == max )
			return min;
		if( min > max )
		{
			float t = max;
			max = min;
			min = t;
		}
		
		return min + random.nextFloat() * (max - min);
	}
	
	public int getRoleRandInterval()
	{
		return random.nextInt(ROLE_RAND_INTERVAL_MAX);
	}
	
	public static byte[] secureRandBytes(int len)
	{
		byte[] r = new byte[len];
		srandom.nextBytes(r);
		return r;
	}
	
	
	public static void main(String[] args)
	{
		//org.apache.log4j.helpers.LogLog.setInternalDebugging(true);
		org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
		ca.setName("AA");
		ca.setWriter(new java.io.PrintWriter(System.out));
		ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
		Logger.getRootLogger().addAppender(ca);
		
	}
}
