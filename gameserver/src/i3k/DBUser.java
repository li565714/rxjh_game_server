
package i3k;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import ket.util.Stream;

public class DBUser implements Stream.IStreamable
{

	public DBUser() { }

	public DBUser(int createTime, int lastLoginTime)
	{
		
	}
	
	public DBUser newUser(SBean.DBRegisterID id, int createTime, String loginKey)
	{
		rolesID = new ArrayList<>();
		register = new SBean.DBRegister(id, createTime);
		
		ban = new HashMap<>();
		
		lastLoginTime = createTime;
		lastLoginKey = loginKey;
	
		return this;
	}

	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		rolesID = is.popIntegerList();
		
		if (register == null)
			register = new SBean.DBRegister();
		is.pop(register);
		
		ban = is.popIntegerHashMap(SBean.DBBanData.class);
		
		lastLoginTime = is.popInteger();
		lastLoginKey = is.popString();
		
		padding1 = is.popInteger();
		padding2 = is.popInteger();

	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushIntegerList(rolesID);
		os.push(register);
		
		os.pushIntegerMap(ban);
		
		os.pushInteger(lastLoginTime);
		os.pushString(lastLoginKey);
		
		os.pushInteger(padding1);
		os.pushInteger(padding2);
	
	}

	public List<Integer> rolesID;
	public SBean.DBRegister register;
	
	public Map<Integer, SBean.DBBanData> ban;
	
	public int lastLoginTime;
	public String lastLoginKey = "";
	
	public int padding1;
	public int padding2;
}