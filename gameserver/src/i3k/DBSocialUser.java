package i3k;

import ket.util.Stream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;

public class DBSocialUser implements Stream.IStreamable
{
	public static int VERSION_NOW = 1;
	@Override
	public void decode(Stream.AIStream is) throws EOFException, DecodeException
	{
		int dbVersion = is.popInteger();
		
		serverID = is.popInteger();
		serverName = is.popString();
		roleID = is.popInteger();
		roleName = is.popString();
		
		sendCount = is.popInteger();
		likesCount = is.popInteger();
		likedCount = is.popInteger();
		dislikesCount = is.popInteger();
		dislikedCount = is.popInteger();
		
		lastDayRefresh = is.popInteger();
		
		padding1 = is.popInteger();
		padding2 = is.popInteger();
		padding3 = is.popInteger();
		padding4 = is.popInteger();
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushInteger(serverID);
		os.pushString(serverName);
		os.pushInteger(roleID);
		os.pushString(roleName);
		
		os.pushInteger(sendCount);
		os.pushInteger(likesCount);
		os.pushInteger(likedCount);
		os.pushInteger(dislikesCount);
		os.pushInteger(dislikedCount);
		
		os.pushInteger(lastDayRefresh);
		
		os.pushInteger(padding1);
		os.pushInteger(padding2);
		os.pushInteger(padding3);
		os.pushInteger(padding4);
	}
	
	
	public int serverID;
	public String serverName;
	public int roleID;
	public String roleName;
	
	public int sendCount;
	public int likesCount;
	public int likedCount;
	public int dislikesCount;
	public int dislikedCount;
	
	public int lastDayRefresh;
	
	public int padding1;
	public int padding2;
	public int padding3;
	public int padding4;
}
