package i3k;

import ket.util.Stream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;

import i3k.SBean;
import java.util.Map;

public class DBFightRanks implements Stream.IStreamable
{
	public static int VERSION_NOW = 1;
	public DBFightRanks()
	{
		
	}
	
	@Override
	public void decode(Stream.AIStream is) throws EOFException, DecodeException
	{
		int dbVersion = is.popInteger();
		
		ranks = is.popIntegerHashMap(SBean.DBRoleRanks.class);
		
		padding1 = is.popInteger();
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushIntegerMap(ranks);
		
		os.pushInteger(padding1);
	}
	
	public Map<Integer, SBean.DBRoleRanks> ranks;
	public int padding1;
}
