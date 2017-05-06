package i3k.gs;

import i3k.SBean;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import ket.kdb.Transaction;
import ket.kdb.Table;
import ket.kdb.Transaction.AutoInit;
import ket.kdb.Transaction.ErrorCode;
import ket.util.Stream;

public class RollNotice
{

	public RollNotice(GameServer gs)
	{
		this.gs = gs;
	}

	RollNotice start(int now)
	{
		updateRollNoticeLastAddTime(GameData.ROLLNOTICE_TYPE_TRANSFER, now);
		updateRollNoticeLastAddTime(GameData.ROLLNOTICE_TYPE_WIN_HIGH_SCORE, now);
		
		return this;
	}
	
	interface AddRollNoticeCallback
	{
		void onCallback(int msgId);
	}

	public class AddRollNoticeTrans implements Transaction
	{
		public AddRollNoticeTrans(SBean.DBRollNotice notice, AddRollNoticeCallback callback)
		{
			this.notice = notice;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("rollNotices");
				byte[] data = world.get(key);
				List<SBean.DBRollNotice> rollNotices = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBRollNotice.class, data);
				int newid = 1000;
				Integer maxid = maxids.get("rollnotice");
				if (maxid != null)
					newid = maxid.intValue() + 1;
				maxids.put("rollnotice", newid);
				this.notice.id = newid;
				rollNotices.add(this.notice);
				int now = GameTime.getTime();
				rollNotices = rollNotices.stream().filter(m -> m.sendTime + m.lifeTime > now).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(rollNotices));
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
				onAddRollNotice(notice);
				callback.onCallback(notice.id);
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

		private SBean.DBRollNotice notice;
		private AddRollNoticeCallback callback;
	}

	public void addRollNotice(SBean.DBRollNotice notice, AddRollNoticeCallback callback)
	{
		gs.getDB().execute(new AddRollNoticeTrans(notice, callback));
	}

	interface DelRollNoticeCallback
	{
		void onCallback(boolean ok);
	}

	public class DelRollNoticeTrans implements Transaction
	{
		public DelRollNoticeTrans(int noticeId, DelRollNoticeCallback callback)
		{
			this.noticeId = noticeId;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("rollNotices");
				byte[] data = world.get(key);
				List<SBean.DBRollNotice> rollNotices = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBRollNotice.class, data);
				int now = GameTime.getTime();
				rollNotices = rollNotices.stream().filter(m -> m.sendTime + m.lifeTime > now).filter(m -> m.id != noticeId).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(rollNotices));
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("del roll notice fail, exception[" + t.getMessage() + "], throwed by timer thread", t);
				return false;
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errorCode)
		{
			if (errorCode == ErrorCode.eOK)
			{
				onDelRollNotice(noticeId);
				callback.onCallback(true);
			}
			else
			{
				callback.onCallback(false);
			}
		}

		@AutoInit
		public Table<byte[], byte[]> world;

		private int noticeId;
		private DelRollNoticeCallback callback;
	}

	public void delRollNotice(int noticeId, DelRollNoticeCallback callback)
	{
		gs.getDB().execute(new DelRollNoticeTrans(noticeId, callback));
	}

	public synchronized RollNotice fromDB(List<SBean.DBRollNotice> rollNotices)
	{
		this.delayNotices.addAll(rollNotices);
		trySyncRollNotice(GameTime.getTime());
		return this;
	}

	public synchronized void roleAddRollNotice(byte type, String paras)
	{
		int now = GameTime.getTime();
		onAddRollNotice(new SBean.DBRollNotice(this.mintempid--, now, 0, 60, paras, type));
		updateRollNoticeLastAddTime(type, now);
	}
	
	private void updateRollNoticeLastAddTime(int type, int now)
	{
		switch (type)
		{
		case GameData.ROLLNOTICE_TYPE_TRANSFER:
		case GameData.ROLLNOTICE_TYPE_WIN_HIGH_SCORE:
			int interval = GameData.getInstance().getIdleNoticeInterval(type, GameTime.getSecondOfDay(now));
			if(interval > 0)
				this.nextIdleNoticeAddTimes.put(type, now + interval);
			break;
		default:
			break;
		}
	}
	
	private void idleAddRollNotice(int timeTick)
	{
		for(Map.Entry<Integer, Integer> e: this.nextIdleNoticeAddTimes.entrySet())
		{
			int type = e.getKey();
			int nextTime = e.getValue();
			int openDay = GameData.getInstance().getRobotOpenDayByType(type);
			if((gs.getOpenDay() + 1) >= openDay)
			{
				if(timeTick > nextTime)
				{
					String context = GameData.getInstance().getIdleNoticeContext(type);
					if(context != null)
					{
						roleAddRollNotice((byte)type, context);
						gs.getLogger().debug("@@@@@@@@@@@@@@@@@@@@@@@@idle add roll notice " + context + " type " + type);
					}
				}
			}
		}
	}
	
	private synchronized void onAddRollNotice(SBean.DBRollNotice notice)
	{
		this.delayNotices.add(notice);
		trySyncRollNotice(GameTime.getTime());
		if (notice.id > 0)
			gs.getLogger().info("add roll notice ok, id=" + notice.id + ", life time (" + GameTime.getDateTimeStampStr(notice.sendTime) + "--" + GameTime.getDateTimeStampStr(notice.sendTime + notice.lifeTime) + "), title=" + notice.content);
	}

	private synchronized void onDelRollNotice(int noticeId)
	{
		this.delayNotices.removeIf(n -> n.id == noticeId);
		tryDelRollNotice(noticeId);
		gs.getLogger().info("del roll notice ok, id=" + noticeId);
	}

	private void tryDelRollNotice(int noticeId)
	{
		if (this.rollNotices.removeIf(n -> n.id == noticeId))
		{
			this.lastRollNoticesModifyTime = GameTime.getTime();
		}
	}

	private void trySyncRollNotice(int now)
	{
		{
			Iterator<SBean.DBRollNotice> it = rollNotices.iterator();
			while (it.hasNext())
			{
				SBean.DBRollNotice notice = it.next();
				if (notice.sendTime + notice.lifeTime < now)
				{
					it.remove();
				}
			}
		}
		{
			Iterator<SBean.DBRollNotice> it = delayNotices.iterator();
			while (it.hasNext())
			{
				SBean.DBRollNotice notice = it.next();
				if (notice.sendTime <= now)
				{
					rollNotices.add(notice);
					this.lastRollNoticesModifyTime = now;
					it.remove();
				}
			}
		}
	}

	public synchronized void onTimer(int timeTick)
	{
		trySyncRollNotice(timeTick);
		idleAddRollNotice(timeTick);
	}

	public synchronized List<Integer> syncRollNotice(int lastSyncTime)
	{
		if (lastSyncTime <= this.lastRollNoticesModifyTime)
		{
			return rollNotices.stream().map((n) -> n.id).collect(Collectors.toList());
		}
		return null;
	}

	public synchronized SBean.DBRollNotice queryRollNotice(int noticeId)
	{
		for (SBean.DBRollNotice n : rollNotices)
		{
			if (n.id == noticeId)
				return n;
		}
		return null;
	}

	GameServer gs;
	private List<SBean.DBRollNotice> delayNotices = new ArrayList<>();
	private List<SBean.DBRollNotice> rollNotices = new ArrayList<>();
	private int lastRollNoticesModifyTime;
	private int mintempid = -1;
	
	private Map<Integer, Integer> nextIdleNoticeAddTimes = new HashMap<>();
}
