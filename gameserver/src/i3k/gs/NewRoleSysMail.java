
package i3k.gs;

import i3k.SBean;
import i3k.util.GameTime;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import ket.kdb.Transaction;
import ket.kdb.Table;
import ket.util.Stream;

public class NewRoleSysMail
{

	public NewRoleSysMail(GameServer gs)
	{
		this.gs = gs;
	}
	
	interface AddNewRoleSysMailCallback
	{
		void onCallback(int mailId);
	}
	public class AddNewRoleSysMailTrans implements Transaction
	{
		AddNewRoleSysMailTrans(SBean.DBWorldMail mail, AddNewRoleSysMailCallback callback)
		{
			this.mail = mail;
			this.callback = callback;
		}
		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("newRoleSysMails");
				byte[] data = world.get(key);
				List<SBean.DBWorldMail> newRoleSysMails = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBWorldMail.class, data);
				int newid = 1000;
				Integer maxid = maxids.get("newrolesysmail");
				if( maxid != null )
					newid = maxid.intValue()+1;
				maxids.put("newrolesysmail", newid);
				this.mail.id = newid;
				newRoleSysMails.add(this.mail);
//				int now = GameTime.getTime();
//				newRoleSysMails = newRoleSysMails.stream().filter(m -> m.sendTime + m.lifeTime > now).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(newRoleSysMails));
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("add new role sys mail fail, exception[" + t.getMessage() + "], throwed by timer thread", t);
				return false;
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			if (errcode == ErrorCode.eOK)
			{
				addNewRoleSysMail(mail);
				callback.onCallback(mail.id);
			}
			else
			{
				callback.onCallback(0);
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		@AutoInit
		public Table<String, Integer> maxids;
		
		private SBean.DBWorldMail mail;
		private AddNewRoleSysMailCallback callback;
	}
	
	interface DelNewRoleSysMailCallback
	{
		void onCallback(boolean ok);
	}
	public class DelNewRoleSysMailTrans implements Transaction
	{
		DelNewRoleSysMailTrans(int mailId, DelNewRoleSysMailCallback callback)
		{
			this.mailId = mailId;
			this.callback = callback;
		}
		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("newRoleSysMails");
				byte[] data = world.get(key);
				List<SBean.DBWorldMail> newRoleSysMails = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBWorldMail.class, data);
				newRoleSysMails = newRoleSysMails.stream().filter(m -> m.id != mailId).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(newRoleSysMails));
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("del new role sys mail fail, exception[" + t.getMessage() + "], throwed by timer thread", t);
				return false;
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			if (errcode == ErrorCode.eOK)
			{
				delNewRoleSysMail(mailId);
				callback.onCallback(true);
			}
			else
			{
				callback.onCallback(false);
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private int mailId;
		private DelNewRoleSysMailCallback callback;
	}
	
	public void addNewRoleSysMail(SBean.DBWorldMail mail, AddNewRoleSysMailCallback callback)
	{
		gs.getDB().execute(new AddNewRoleSysMailTrans(mail, callback));
	}
	public void delNewRoleSysMail(int mailId, DelNewRoleSysMailCallback callback)
	{
		if (newRoleSysMails.stream().anyMatch((m) -> m.id == mailId))
			gs.getDB().execute(new DelNewRoleSysMailTrans(mailId, callback));
		else
			callback.onCallback(true);
	}
	
	public NewRoleSysMail fromDB(List<SBean.DBWorldMail> worldMails)
	{
		newRoleSysMails.addAll(worldMails);
		return this;
	}
	
	private synchronized void addNewRoleSysMail(SBean.DBWorldMail mail)
	{
		this.newRoleSysMails.add(mail);
		gs.getLogger().info("add new role sys mail ok, id=" + mail.id + ", expiration time=" + GameTime.getDateTimeStampStr(mail.sendTime+mail.lifeTime) + ", title=" + mail.title);
	}
	
	private synchronized void delNewRoleSysMail(int mailId)
	{
		this.newRoleSysMails.removeIf(m -> m.id == mailId);
		gs.getLogger().info("del new role sys mail ok, id=" + mailId);
	}
	
	public synchronized List<SBean.DBWorldMail> getNewRoleSysMails()
	{
		return newRoleSysMails;
	}

	GameServer gs;
	private List<SBean.DBWorldMail> newRoleSysMails = new CopyOnWriteArrayList<>();
}

