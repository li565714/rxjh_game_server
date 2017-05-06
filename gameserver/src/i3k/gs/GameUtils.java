
package i3k.gs;

import i3k.SBean;


public class GameUtils
{
////////////////////////////////////////////////////////////////////////
	private static boolean testCanUse(int value, int amount)
	{
		return value >= amount;
	}
	
	private static int calSafeUse(int value, int amount)
	{
		if (amount > 0)
		{
			int finalVal = value - amount;
			if (finalVal < 0)
				finalVal = 0;
			return value - finalVal;
		}
		return 0;
	}
	
	private static int calSafeAdd(int value, int amount, int max)
	{
		if (amount > 0)
		{
			int finalVal = value + amount;
			if (finalVal > max || finalVal <= 0)
				finalVal = max;
			return finalVal - value;
		}
		return 0;
	}
	
	///////////////////////////////////////////////////////////////////////
	static int getValue(SBean.Counter counter)
	{
		return counter.fvalue + counter.rvalue;
	}
	
	static int getFValue(SBean.Counter counter)
	{
		return counter.fvalue;
	}
	
	static int getRValue(SBean.Counter counter)
	{
		return counter.rvalue;
	}
	
	static boolean canUse(SBean.Counter counter, int amount)
	{
		return testCanUse(getValue(counter), amount);
	}
	
	static boolean canUseF(SBean.Counter counter, int amount)
	{
		return testCanUse(getFValue(counter), amount);
	}
	
	static boolean canUseR(SBean.Counter counter, int amount)
	{
		return testCanUse(getRValue(counter), amount);
	}
	
	static int safeUse(SBean.Counter counter, int amount)
	{
		int ruse = safeUseR(counter, amount);
		int fuse = safeUseF(counter, amount-ruse);
		return ruse + fuse;
	}
	
	static int safeUseF(SBean.Counter counter, int amount)
	{
		int use = calSafeUse(getFValue(counter), amount);
		counter.fvalue -= use;
		return use;
	}
	
	static int safeUseR(SBean.Counter counter, int amount)
	{
		int use = calSafeUse(getRValue(counter), amount);
		counter.rvalue -= use;
		return use;
	}
	
	static int safeAddF(SBean.Counter counter, int amount, int max)
	{
		int add = calSafeAdd(getFValue(counter), amount, max);
		counter.fvalue += add;
		return add;
	}
	
	static int safeAddR(SBean.Counter counter, int amount, int max)
	{
		int add = calSafeAdd(getRValue(counter), amount, max);
		counter.rvalue += add;
		return add;
	}
////////////////////////////////////////////////////////////////////////	

}


