package i3k.util;

public class OpenConnectFailCount
{
	private int failcount = 0;
	private boolean reportPerTimes = false;

	public boolean increaseCount()
	{
		if (reportPerTimes)
			return true;
		failcount++;
		if (failcount < 60)
		{
			switch (failcount)
			{
			case 1:
			case 5:
			case 10:
			case 20:
			case 30:
				return true;
			default:
				return false;
			}
		}
		else
		{
			return failcount % 60 == 0;
		}
	}

	public void resetCount()
	{
		failcount = 0;
	}
	
	public void setReportPerTimes(boolean reportPerTimes)
	{
		this.reportPerTimes = reportPerTimes;
		resetCount();
	}
}
