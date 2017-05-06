package i3k;

import ket.util.Stream;
import ket.util.Stream.AIStream;
import ket.util.Stream.AOStream;
import ket.util.Stream.DecodeException;
import ket.util.Stream.EOFException;

import java.util.List;

public class DBDemonHoleGroup implements Stream.IStreamable
{
	public static int VERSION_NOW = 1;
	
	@Override
	public void decode(AIStream is) throws EOFException, DecodeException
	{
		int dbVersion = is.popInteger();
		
		floors = is.popList(SBean.DBDemonHoleFloor.class);
		lastOpenTime = is.popInteger();
		lastEndTime = is.popInteger();
		
		padding = is.popInteger();
	}

	@Override
	public void encode(AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushList(floors);
		os.pushInteger(lastOpenTime);
		os.pushInteger(lastEndTime);
		
		os.pushInteger(padding);
	}
	
	public List<SBean.DBDemonHoleFloor> floors;
	public int lastOpenTime;
	public int lastEndTime;
	
	public int padding;
}
