
package i3k.gs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import i3k.SBean;
import i3k.util.GameTime;

public class Ban
{
	public Ban()
	{
		
	}
	
	public Ban(Map<Integer, SBean.DBBanData> ban)
	{
		this.allBanData = ban;
	}
	
	public Map<Integer, SBean.DBBanData> toDBDataWithoutLock()
	{
		return allBanData;
	}
	
	
	static int calcBanEndTime(int banSecond, int curTime)
	{
		int banEndTime = -1;
		if (banSecond > 0)
		{
			banEndTime = curTime + banSecond;
			if (banEndTime < 0)
				banEndTime = Integer.MAX_VALUE;
		}
		return banEndTime;
	}
	
	public static boolean isInBan(int banEndTime, int curTime)
	{
		return banEndTime < 0 || curTime < banEndTime;
	}
	
	boolean isBan(int typeID, int curTime)
	{
		SBean.DBBanData banData = allBanData.get(typeID);
		return banData != null && isInBan(banData.banEndTime, curTime);
	}
	
	String getBanReason(int typeID)
	{
		SBean.DBBanData banData = allBanData.get(typeID);
		if (banData == null)
			return "";
		return banData.banReason;
	}
	
	void ban(int typeID, int second, String reason, int curTime)
	{
		int banEndTime = calcBanEndTime(second, curTime);
		SBean.DBBanData banData = new SBean.DBBanData(banEndTime, reason);
		allBanData.put(typeID, banData);
	}
	
	void unban(int typeID)
	{
		allBanData.remove(typeID);
	}
	
	int getBanEndTime(int typeID)
	{
		SBean.DBBanData banData = allBanData.get(typeID);
		if (banData == null)
			return 0;
	    return banData.banEndTime;
	}
	
	int getBanLeftTime(int typeID, int curTime)
	{
		int endTime = getBanEndTime(typeID);
		int leftTime = endTime > curTime ? (endTime-curTime) : (endTime < 0 ? -1 : 0);
		return leftTime;
	}
	
	boolean isBanLogin(int curTime)
	{
		return isBan(BAN_TYPE_LOGIN, curTime);
	}
	
	int getBanLoginLeftTime(int curTime)
	{
		return getBanLeftTime(BAN_TYPE_LOGIN, curTime);
	}
	
	int getBanLoginEndTime()
	{
		return getBanEndTime(BAN_TYPE_LOGIN);
	}
	
	String getBanLoginReason()
	{
		return getBanReason(BAN_TYPE_LOGIN);
	}
	
	BanInfo getBanLoginInfo(int now)
	{
		return new BanInfo(getBanLeftTime(BAN_TYPE_LOGIN, now), getBanReason(BAN_TYPE_LOGIN));
	}
	
	void banLogin(int second, String reason, int curTime)
	{
		ban(BAN_TYPE_LOGIN, second, reason, curTime);
	}
	
	void unbanLogin()
	{
		unban(BAN_TYPE_LOGIN);
	}
	
	void banChat(int second, String reason, int curTime)
	{
		ban(BAN_TYPE_CHAT, second, reason, curTime);
	}

	boolean isBanChat(int curTime)
	{
		return isBan(BAN_TYPE_CHAT, curTime);
	}
	
	void unbanChat()
	{
		unban(BAN_TYPE_CHAT);
	}
	
	BanInfo getBanChatInfo(int now)
	{
		return new BanInfo(getBanLeftTime(BAN_TYPE_CHAT, now), getBanReason(BAN_TYPE_CHAT));
	}

	
	
	Map<Integer, SBean.DBBanData> allBanData = new TreeMap<>();
	public static final int BAN_TYPE_LOGIN = 0;
	public static final int BAN_TYPE_CHAT = 1;
}

class BanInfo
{
	public int leftTime;
	public String reason;
	
	public BanInfo(int leftTime, String reason)
	{
		this.leftTime = leftTime;
		this.reason = reason;
	}
}