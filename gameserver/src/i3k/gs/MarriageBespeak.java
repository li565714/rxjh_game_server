package i3k.gs;

import i3k.SBean;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Iterator;
import java.util.stream.Collectors;

import ket.kdb.Transaction;
import ket.kdb.Table;
import ket.util.Stream;

public class MarriageBespeak
{

	public MarriageBespeak(GameServer gs)
	{
		this.gs = gs;
		this.bespeaks = new HashMap<Integer, SBean.DBMarriageBespeak>();
	}

	interface AddMarriageBespeakCallback
	{
		void onCallback(int msgId);
	}

	public class AddMarriageBespeakTrans implements Transaction
	{
		public AddMarriageBespeakTrans(SBean.DBMarriageBespeak bespeak, AddMarriageBespeakCallback callback)
		{
			this.bespeak = bespeak;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("marriageBespeak");
				byte[] data = world.get(key);
				List<SBean.DBMarriageBespeak> marriageBespeaks = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBMarriageBespeak.class, data);
				marriageBespeaks = marriageBespeaks.stream().filter(m -> (m.timeIndex != this.bespeak.timeIndex || m.line != this.bespeak.line) && m.time == GameTime.getDay()).collect(Collectors.toList());
				marriageBespeaks.add(this.bespeak);
				world.put(key, Stream.encodeListLE(marriageBespeaks));
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("add roll notice fail, exception[" + t.getMessage() + "], throwed by timer thread", t);
				return false;
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errorCode)
		{
			if (errorCode == ErrorCode.eOK)
			{
				onAddMarriageBespeak(bespeak);
				callback.onCallback(1);
			}
			else
			{
				callback.onCallback(0);
			}
		}

		@AutoInit
		public Table<byte[], byte[]> world;

		private SBean.DBMarriageBespeak bespeak;
		private AddMarriageBespeakCallback callback;
	}

	public void addMarriageBespeak(SBean.DBMarriageBespeak bespeak, AddMarriageBespeakCallback callback)
	{
		gs.getDB().execute(new AddMarriageBespeakTrans(bespeak, callback));
	}

	public synchronized MarriageBespeak fromDB(List<SBean.DBMarriageBespeak> marriageBespeaks)
	{
		this.bespeaks = marriageBespeaks.stream().collect(Collectors.toMap(m -> m.marriageId, m -> m));
		trySyncMarriageBespeak();
		return this;
	}

	private synchronized void onAddMarriageBespeak(SBean.DBMarriageBespeak bespeak)
	{
		this.bespeaks.put(bespeak.marriageId, bespeak);
		trySyncMarriageBespeak();
		gs.getLogger().info("add roll marriage bespeak ok, marriageId=" + bespeak.marriageId + ", line=" + bespeak.line + ", time index=" + bespeak.timeIndex);
	}

	private void trySyncMarriageBespeak()
	{
		Iterator<Entry<Integer, SBean.DBMarriageBespeak>> it = bespeaks.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<Integer, SBean.DBMarriageBespeak> bespeak = it.next();
			if (bespeak.getValue().time != GameTime.getDay())
			{
				it.remove();
			}
		}
	}

	public synchronized List<SBean.DBMarriageBespeak> syncMarriageBespeak()
	{
		trySyncMarriageBespeak();
		return bespeaks.values().stream().collect(Collectors.toList());
	}

	public synchronized SBean.DBMarriageBespeak getMarriageBespeakByRoleId(int marriageId)
	{
		trySyncMarriageBespeak();
		return bespeaks.get(marriageId);
	}

	public synchronized boolean testCanBespeak(int marriageId, int line, int timeIndex)
	{
		trySyncMarriageBespeak();
		return !bespeaks.values().stream().anyMatch(m -> m.marriageId == marriageId || (m.line == line && m.timeIndex == timeIndex));
	}
	
	public synchronized void changeMarriageBespeakName(int marriageId, int roleid, String rolename)
	{
		if (!bespeaks.containsKey(marriageId))
			return;
		SBean.DBMarriageBespeak bespeak = bespeaks.get(marriageId);
		if (bespeak.manId == roleid)
			bespeak.manName = rolename;
		if (bespeak.ladyId == roleid)
			bespeak.ladyName = rolename;
	}

	GameServer gs;
	Map<Integer, SBean.DBMarriageBespeak> bespeaks;
}
