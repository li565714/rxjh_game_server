
package i3k;
import i3k.SBean.DBMail;

import java.util.List;

import ket.util.Stream;

public class DBMailBox implements Stream.IStreamable
{

	public DBMailBox() { }
	
	public DBMailBox(int lastSyncWorldMailID) 
	{
		this.lastSyncWorldMailID = lastSyncWorldMailID;
	}

	public DBMailBox(int nextId, int recvWorldMailID, int lastSyncTime, int recvLastMailTime, List<DBMail> sysMails, List<DBMail> tempMails, List<DBMail> delayedMails)
	{
		this.nextId = nextId;
		this.lastSyncWorldMailID = recvWorldMailID;
		this.lastSyncTime = lastSyncTime;
		this.recvLastMailTime = recvLastMailTime;
		this.sysMails = sysMails;
		this.tempMails = tempMails;
		this.delayedMails = delayedMails;
	}

	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		version = is.popInteger();
		nextId = is.popInteger();
		lastSyncWorldMailID = is.popInteger();
		lastSyncTime = is.popInteger();
		recvLastMailTime = is.popInteger();
		sysMails = is.popList(DBMail.class);
		tempMails = is.popList(DBMail.class);
		delayedMails = is.popList(DBMail.class);
		
		padding1 = is.popInteger();
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(version);
		os.pushInteger(nextId);
		os.pushInteger(lastSyncWorldMailID);
		os.pushInteger(lastSyncTime);
		os.pushInteger(recvLastMailTime);
		os.pushList(sysMails);
		os.pushList(tempMails);
		os.pushList(delayedMails);
		
		os.pushInteger(padding1);
	}

	public int version;
	public int nextId;
	public int lastSyncWorldMailID;
	public int lastSyncTime;
	public int recvLastMailTime;
	public List<DBMail> sysMails;
	public List<DBMail> tempMails;
	public List<DBMail> delayedMails;
	public int padding1;
}