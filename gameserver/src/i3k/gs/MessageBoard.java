package i3k.gs;

import i3k.SBean;
import i3k.SBean.DBMessageBoard;
import i3k.SBean.GameDataCFGS;
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

public class MessageBoard
{

	public MessageBoard(GameServer gs)
	{
		this.gs = gs;
	}

	interface commonMessageBoardCallback
	{
		void onCallback(int msgId);
	}

	public class AddOrReplaceMessageBoardTrans implements Transaction
	{
		public AddOrReplaceMessageBoardTrans(SBean.DBMessageBoard messageBoard, commonMessageBoardCallback callback)
		{
			this.messageBoard = messageBoard;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("messageBoards");
				byte[] data = world.get(key);
				List<SBean.DBMessageBoard> messageBoards = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBMessageBoard.class, data);
				messageBoards = messageBoards.stream().filter(m -> m.id != this.messageBoard.id && m.side != this.messageBoard.side).collect(Collectors.toList());
				messageBoards.add(this.messageBoard);
				int now = GameTime.getTime();
				messageBoards = messageBoards.stream().filter(m -> m.sendTime + m.lifeTime > now).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(messageBoards));
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("add message board fail, exception[" + t.getMessage() + "], throwed by timer thread", t);
				return false;
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errorCode)
		{
			if (errorCode == ErrorCode.eOK)
			{
				addMessageBoard(messageBoard);
				callback.onCallback(messageBoard.id);
			}
			else
			{
				callback.onCallback(0);
			}
		}

		@AutoInit
		public Table<byte[], byte[]> world;

		private SBean.DBMessageBoard messageBoard;
		private commonMessageBoardCallback callback;
	}

	public class changeMessageBoardTrans implements Transaction
	{
		public changeMessageBoardTrans(SBean.DBMessageBoard messageBoard, commonMessageBoardCallback callback)
		{
			this.messageBoard = messageBoard;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("messageBoards");
				byte[] data = world.get(key);
				List<SBean.DBMessageBoard> messageBoards = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBMessageBoard.class, data);
				messageBoards = messageBoards.stream().filter(m -> m.id != this.messageBoard.id && m.side != this.messageBoard.side).collect(Collectors.toList());
				messageBoards.add(this.messageBoard);
				int now = GameTime.getTime();
				messageBoards = messageBoards.stream().filter(m -> m.sendTime + m.lifeTime > now).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(messageBoards));
			}
			catch (Throwable t)
			{
				gs.getLogger().warn("add message board fail, exception[" + t.getMessage() + "], throwed by timer thread", t);
				return false;
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errorCode)
		{
			if (errorCode == ErrorCode.eOK)
			{
				addMessageBoard(messageBoard);
				callback.onCallback(messageBoard.id);
			}
			else
			{
				callback.onCallback(0);
			}
		}

		@AutoInit
		public Table<byte[], byte[]> world;

		private SBean.DBMessageBoard messageBoard;
		private commonMessageBoardCallback callback;
	}
	
	public void addOrReplaceMessageBoard(SBean.DBMessageBoard messageBoard, commonMessageBoardCallback callback)
	{
		gs.getDB().execute(new AddOrReplaceMessageBoardTrans(messageBoard, callback));
	}

	interface DelMessageBoardCallback
	{
		void onCallback(boolean ok);
	}

	public class DelMessageBoardTrans implements Transaction
	{
		public DelMessageBoardTrans(int side, int msgId, DelMessageBoardCallback callback)
		{
			this.side = side;
			this.msgId = msgId;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			try
			{
				byte[] key = Stream.encodeStringLE("messageBoards");
				byte[] data = world.get(key);
				List<SBean.DBMessageBoard> messageBoards = data == null ? new ArrayList<>() : Stream.decodeListLE(SBean.DBMessageBoard.class, data);
				int now = GameTime.getTime();
				messageBoards = messageBoards.stream().filter(m -> m.sendTime + m.lifeTime > now).filter(m -> !(m.side == side && m.id == msgId)).collect(Collectors.toList());
				world.put(key, Stream.encodeListLE(messageBoards));
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
				delMessageBoard(side, msgId);
				callback.onCallback(true);
			}
			else
			{
				callback.onCallback(false);
			}
		}

		@AutoInit
		public Table<byte[], byte[]> world;

		private int side;
		private int msgId;
		private DelMessageBoardCallback callback;
	}

	private void delMessageBoard(int side, int msgId)
	{
		this.messageBoards.remove(getMapKey(side, msgId));
	}

	public synchronized MessageBoard fromDB(List<SBean.DBMessageBoard> msgs)
	{
		for (DBMessageBoard msg : msgs)
		{
			messageBoards.put(getMapKey(msg.side, msg.id), msg);
		}
		return this;
	}

	public synchronized List<SBean.DBMessageBoard> syncMessageBoard()
	{
		return messageBoards.values().stream().filter(m -> m.sendTime + m.lifeTime > GameTime.getTime()).collect(Collectors.toList());
	}

	private synchronized void addMessageBoard(DBMessageBoard messageBoard)
	{
		messageBoards.put(getMapKey(messageBoard.side, messageBoard.id), messageBoard);
	}

	public boolean isMsgCanUse(int side, int msgId)
	{
		return !(messageBoards.containsKey(getMapKey(side, msgId)) && messageBoards.get(getMapKey(side, msgId)).sendTime + messageBoards.get(getMapKey(side, msgId)).lifeTime > GameTime.getTime());
	}

	private int getMapKey(int side, int msgId)
	{
		return side * 100 + msgId;
	}

	public boolean isTimeValid(int side, int msgId, int time)
	{
		SBean.MessageBoardCFGS cfg = GameData.getInstance().getMessageBoardCFGS(side, msgId);
		if (cfg == null || !cfg.timeCost.containsKey(time))
			return false;
		if (!isMsgCanUse(side, msgId) && (time < messageBoards.get(getMapKey(side, msgId)).lifeTime || messageBoards.get(getMapKey(side, msgId)).sendTime + cfg.protectTime > GameTime.getTime()))
			return false;
		return true;
	}

	public void changeMessageBoard(DBMessageBoard msg, LoginManager.AddOrReplaceMessageBoardCallback callback)
	{
		gs.getDB().execute(new changeMessageBoardTrans(msg, (ok) ->
		{
			callback.onCallback(ok);
		}), getMapKey(msg.side, msg.id));
	}
	
	public void changeMessageBoardByComment(int side, int msgid, int comment, int sendtime, LoginManager.AddOrReplaceMessageBoardCallback callback)
	{
		synchronized (this)
		{
			DBMessageBoard msg = messageBoards.get(getMapKey(side, msgid));
			if (msg == null)
				callback.onCallback(GameData.PROTOCOL_OP_MESSAGE_BOARD_INVAILD);
			if (isMsgCanUse(side, msgid))
				callback.onCallback(GameData.PROTOCOL_OP_MESSAGE_BOARD_UN_USED);
			if (msg.sendTime != sendtime)
				callback.onCallback(GameData.PROTOCOL_OP_MESSAGE_BOARD_HAS_CHANGED);
			DBMessageBoard clone = msg.kdClone();
			if (comment == GameData.MESSAGE_BOARD_COMMENT_TYPE_PRAISE)
				clone.praiseTime++;
			if (comment == GameData.MESSAGE_BOARD_COMMENT_TYPE_TREAD)
				clone.treadTime++;
			changeMessageBoard(clone, callback);
		}
	}
	
	public void changeMessageBoardByContext(int side, int roleid, int msgid, String context, int sendtime, LoginManager.AddOrReplaceMessageBoardCallback callback)
	{
		synchronized (this)
		{
			DBMessageBoard msg = messageBoards.get(getMapKey(side, msgid));
			if (msg == null)
				callback.onCallback(GameData.PROTOCOL_OP_MESSAGE_BOARD_INVAILD);
			if (msg.roleId != roleid)
				callback.onCallback(GameData.PROTOCOL_OP_MESSAGE_BOARD_IS_NOT_YOUR);
			if (msg.sendTime != sendtime)
				callback.onCallback(GameData.PROTOCOL_OP_MESSAGE_BOARD_HAS_CHANGED);
			DBMessageBoard clone = msg.kdClone();
			clone.content = context;
			changeMessageBoard(clone, callback);
		}
	}

	public synchronized void changeMessageBoardByName(int roleid, String rolename)
	{
		for (SBean.DBMessageBoard msg : messageBoards.values())
		{
			if (msg.roleId == roleid)
			{
				msg.roleName = rolename;
				changeMessageBoard(msg ,(ok) -> {});
			}
		}
	}

	public void setConSideLastChangeTime(int time)
	{
		conSideLastChangeTime = time;
	}

	public int getConSideLastChangeTime()
	{
		return conSideLastChangeTime;
	}

	public synchronized boolean hasConSideMsg()
	{
		return this.messageBoards.values().stream().anyMatch(item -> item.side == GameData.MESSAGE_BOARD_SIDE_CON && item.sendTime + item.lifeTime > GameTime.getTime());
	}
	
	public DBMessageBoard getMessageBoard(int side, int msgid)
	{
		return messageBoards.get(getMapKey(side, msgid));
	}
	
	public int getCurMsgRole(int side, int msgId)
	{
		return messageBoards.get(getMapKey(side, msgId)) == null ? 0 : messageBoards.get(getMapKey(side, msgId)).roleId;
	}
	
	GameServer gs;
	private Map<Integer, SBean.DBMessageBoard> messageBoards = new HashMap<>();
	private int conSideLastChangeTime = 1; //为了保证角色第一次上线若留言板从服务器开启无任何修改有红点显示
}
