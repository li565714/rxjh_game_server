
package i3k.gs;

import i3k.SBean;
import i3k.util.GameTime;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.stream.Collectors;

import ket.kdb.Transaction;
import ket.kdb.Table;
import ket.util.Stream;

public class WorldMail
{

	public WorldMail(GameServer gs)
	{
		this.gs = gs;
	}
	
	interface AddWorldMailCallback
	{
		void onCallback(int mailId);
	}
	public class AddWorldMailTrans implements Transaction
	{
		AddWorldMailTrans(SBean.DBWorldMail mail, AddWorldMailCallback callback)
		{
			this.mail = mail;
			this.callback = callback;
		}
		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("worldMails");
				byte[] data = world.get(key);
				List<SBean.DBWorldMail> worldMails = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBWorldMail.class, data);
				int newid = 1000;
				Integer maxid = maxids.get("worldmail");
				if( maxid != null )
					newid = maxid.intValue()+1;
				maxids.put("worldmail", newid);
				this.mail.id = newid;
				worldMails.add(this.mail);
				int now = GameTime.getTime();
				worldMails = worldMails.stream().filter(m -> m.sendTime + m.lifeTime > now).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(worldMails));
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("add world mail fail, exception[" + t.getMessage() + "], throwed by timer thread", t);
				return false;
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			if (errcode == ErrorCode.eOK)
			{
				addWorldMail(mail);
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
		private AddWorldMailCallback callback;
	}
	
	public void addWorldMail(SBean.DBWorldMail mail, AddWorldMailCallback callback)
	{
		gs.getDB().execute(new AddWorldMailTrans(mail, callback));
	}
	
	public synchronized WorldMail fromDB(List<SBean.DBWorldMail> worldMails)
	{
		for (SBean.DBWorldMail m : worldMails)
		{
			this.worldMails.add(m);
			if (m.id > this.lastWorldMailID)
				this.lastWorldMailID = m.id;
		}
		return this;
	}
	
	
	public synchronized int getLastWorldMailID()
	{
		return this.lastWorldMailID;
	}
	
	private synchronized void addWorldMail(SBean.DBWorldMail mail)
	{
		this.worldMails.add(mail);
		this.lastWorldMailID = mail.id;
		gs.getLogger().info("add world mail ok, id=" + mail.id + ", expiration time=" + GameTime.getDateTimeStampStr(mail.sendTime+mail.lifeTime) + ", title=" + mail.title);
	}
	
	public synchronized List<SBean.DBWorldMail> syncWorldMail(int lastSyncID)
	{
		if (lastSyncID < lastWorldMailID)
		{
			int now = GameTime.getTime();
			Iterator<SBean.DBWorldMail> it = worldMails.iterator();
			if (it.hasNext())
			{
				SBean.DBWorldMail mail = it.next();
				if (mail.sendTime + mail.lifeTime < now)
					it.remove();
			}
			List<SBean.DBWorldMail> lst = worldMails.stream().filter(m -> m.id > lastSyncID).collect(Collectors.toList());
			return lst;
		}
		return null;
	}

	GameServer gs;
	private List<SBean.DBWorldMail> worldMails = new ArrayList<>();
	private int lastWorldMailID = 0;
}

