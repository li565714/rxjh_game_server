
package i3k;


import java.util.HashMap;
import java.util.Map;

import ket.util.Stream;

public class DBRoleConsignments implements Stream.IStreamable
{
	public static int VERSION_NOW = 1;
	public DBRoleConsignments() {}
	public DBRoleConsignments(int roleID) 
	{ 
		this.roleID = roleID;
		this.consignitems = new HashMap<>();
	}


	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		int dbVersion = is.popInteger();
		
		roleID = is.popInteger();
		consignitems = is.popIntegerTreeMap(SBean.DBConsignItems.class);
		
		padding1 = is.popInteger();
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushInteger(roleID);
		os.pushIntegerMap(consignitems);
		
		os.pushInteger(padding1);
	}

	public int roleID;
	public Map<Integer, SBean.DBConsignItems> consignitems;
	public int padding1;

}