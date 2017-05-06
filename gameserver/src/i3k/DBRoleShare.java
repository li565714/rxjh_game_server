
package i3k;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import ket.util.Stream;

public class DBRoleShare implements Stream.IStreamable
{
	public static int VERSION_NOW = 1;
	public DBRoleShare() { }
	
	public DBRoleShare(SBean.DBVipPay vipPay, SBean.DBCBOperations cbOperations, int cashback, byte cashbacked) 
	{
		this.vipPay = vipPay;
		this.cbOperations = cbOperations;
		this.cashback = cashback;
		this.cashbacked = cashbacked;
	}

	public DBRoleShare newCreate()
	{
		vipPay = new SBean.DBVipPay(0, 0, 0, 0, 0, new ArrayList<>(), new TreeMap<>());
		cbOperations = new SBean.DBCBOperations(new SBean.DBUserSurvey(new ArrayList<>(), (byte)0), 
												new HashSet<>(), 
												new HashSet<>(),
												new SBean.DBUserData("", "", (byte)0, (byte)0), 
												new HashSet<>(),
												new HashSet<>(),
												new HashSet<>(), (byte)0);
		return this;
	}

	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		int dbVersion = is.popInteger();
		
		if (vipPay == null)
			vipPay = new SBean.DBVipPay();
		is.pop(vipPay);
		
		if(cbOperations == null)
			cbOperations = new SBean.DBCBOperations();
		is.pop(cbOperations);
		
		padding1 = is.popInteger();
		padding2 = is.popInteger();
		padding3 = is.popInteger();
		padding4 = is.popInteger();
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.push(vipPay);
		os.push(cbOperations);
		
		os.pushInteger(padding1);
		os.pushInteger(padding2);
		os.pushInteger(padding3);
		os.pushInteger(padding4);
	}

	public SBean.DBVipPay vipPay;
	public SBean.DBCBOperations cbOperations;
	public int cashback;
	public byte cashbacked;
	
	public int padding1;
	public int padding2;
	public int padding3;
	public int padding4;
}