
package i3k.gs;


import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;

import ket.util.Stream;
import i3k.ForwardData;
import i3k.SBean;
import i3k.util.GameTime;


public class ExchangeService
{
	public ExchangeService(GameServer gs)
	{
		this.gs = gs;
	}
	
	void execTask(ExchangeServiceTask task)
	{
		tasks.put(task.id, task);
		task.doTask();
	}
	
	void onTimer(int timeTick)
	{
		checkTimeOutTask(timeTick);
	}
	
	void checkTimeOutTask(int now)
	{
		List<ExchangeServiceTask> timeoutTasks = getTimeOutTasks(now);
		timeoutTasks.forEach(ExchangeServiceTask::onTimeout);
	}
	
	List<ExchangeServiceTask> getTimeOutTasks(int now)
	{
		List<ExchangeServiceTask> timeoutTasks = new ArrayList<>();
		Iterator<ExchangeServiceTask> it = tasks.values().iterator();
		while (it.hasNext()) 
		{
			ExchangeServiceTask task = it.next();
			if (task.isTooOld(now)) 
			{
				task = tasks.remove(task.id);
				if (task != null)
				{
					timeoutTasks.add(task);	
				}				
			}
		}
		return timeoutTasks;
	}
	
	public ExchangeServiceTask peekTask(int id)
	{
		ExchangeServiceTask task = tasks.remove(id);
		if (task == null)
			gs.getLogger().warn("ExchangeService can't find Task id=" + id);
		return task;
	}
	
	public void onReceivePacket(int taskId, int type, Stream.IStreamable obj)
	{
		ExchangeServiceTask t = this.peekTask(taskId);
		if (t != null)
		{
			try
			{
				t.onHanldePacket(type, obj);
			}
			catch (Exception ex)
			{
				gs.getLogger().warn("ExchangeService handle packet taskId " + taskId + " type " + type);
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public abstract class ExchangeServiceTask
	{
		final static int MAX_WAIT_TIME = 2;
		final int id;
		int sendTime;
		ExchangeServiceTask()
		{
			this.id = nextTaskID.incrementAndGet();
		}
		
		boolean isTooOld(int now)
		{
			return sendTime + MAX_WAIT_TIME <= now;
		}
		
		void doTask()
		{
			sendTime = GameTime.getTime();
			doTaskImpl();
		}
		
		abstract void doTaskImpl();
		
		abstract void onTimeout();
		
		void onHanldePacket(int type, Stream.IStreamable obj)
		{
			switch (type)
			{
			case SBean.ForwardData.eResSyncPage:
				{
					SyncPageCommentTask task = (SyncPageCommentTask)this;
					task.onCallback((SBean.SyncPageCommentRes)obj);
				}
				break;
			case SBean.ForwardData.eResSendComment:
				{
					SendCommentTask task = (SendCommentTask)this;
					task.onCallback((SBean.SendCommentRes)obj);
				}
				break;
			case SBean.ForwardData.eResLikeComment:
				{
					LikeCommentTask task = (LikeCommentTask)this;
					task.onCallback((SBean.LikeCommentRes)obj);
				}
				break;
			case SBean.ForwardData.eResDislikeComment:
				{
					DislikeCommentTask task = (DislikeCommentTask)this;
					task.onCallback((SBean.DislikeCommentRes)obj);
				}
				break;
			default:
				break;
			} 
		}
	}
	
	public interface SendCommentCallback
	{
		void onCallback(int ok);
	}
	public class SendCommentTask extends ExchangeServiceTask
	{
		SBean.SendCommentReq req;
		SendCommentCallback callback;
		SendCommentTask(int gsid, String serverName, int roleId, String roleName, int themeType, int themeId, String comment, SendCommentCallback callback)
		{
			this.req = new SBean.SendCommentReq(gsid, roleId, serverName, roleName, themeType, themeId, comment);
			this.callback = callback;
		}

		void onCallback(SBean.SendCommentRes res)
		{
			callback.onCallback(res.ok);
		}
		
		void onTimeout()
		{
			callback.onCallback(-100);
		}
		
		void doTaskImpl()
		{
			gs.getRPCManager().sendExchangeSocialReq(id, SBean.ForwardData.eReqSendComment, this.req);
		}
	}

	public void roleSendSocialComment(int gsid, String serverName, int roleId, String roleName, int themeType, int themeId, String comment, SendCommentCallback callback)
	{
		execTask(new SendCommentTask(gsid, serverName, roleId, roleName, themeType, themeId, comment, callback));
	}
	//------------------------------------------------------------------------------------------------------------------------
	public interface LikeCommentCallback
	{
		void onCallback(int ok);
	}
	
	public class LikeCommentTask extends ExchangeServiceTask
	{
		SBean.LikeCommentReq req;
		LikeCommentCallback callback;
		
		LikeCommentTask(int gsid, String serverName, int roleId, String roleName, int themeType, int themeId, int commentId, LikeCommentCallback callback)
		{
			this.req = new SBean.LikeCommentReq(gsid, roleId, serverName, roleName, themeType, themeId, commentId);
			this.callback = callback;
		}
		
		void onCallback(SBean.LikeCommentRes res)
		{
			callback.onCallback(res.ok);
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().sendExchangeSocialReq(id, SBean.ForwardData.eReqLikeComment, this.req);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(-100);
		}
	}
	
	public void roleLikeComment(int gsid, String serverName, int roleId, String roleName, int themeType, int themeId, int commentId, LikeCommentCallback callback)
	{
		execTask(new LikeCommentTask(gsid, serverName, roleId, roleName, themeType, themeId, commentId, callback));
	}
	//------------------------------------------------------------------------------------------------------------------------
	public interface DislikeCommentCallback
	{
		void onCallback(int ok);
	}
	
	public class DislikeCommentTask extends ExchangeServiceTask
	{
		SBean.DislikeCommentReq req;
		DislikeCommentCallback callback;
		
		DislikeCommentTask(int gsid, String serverName, int roleId, String roleName, int themeType, int themeId, int commentId, DislikeCommentCallback callback)
		{
			this.req = new SBean.DislikeCommentReq(gsid, roleId, serverName, roleName, themeType, themeId, commentId);
			this.callback = callback;
		}
		
		void onCallback(SBean.DislikeCommentRes res)
		{
			callback.onCallback(res.ok);
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().sendExchangeSocialReq(id, SBean.ForwardData.eReqDislikeComment, this.req);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(-100);
		}
	}
	
	public void roleDislikeComment(int gsid, String serverName, int roleId, String roleName, int themeType, int themeId, int commentId, DislikeCommentCallback callback)
	{
		execTask(new DislikeCommentTask(gsid, serverName, roleId, roleName, themeType, themeId, commentId, callback));
	}
	//------------------------------------------------------------------------------------------------------------------------
	public interface SyncPageCommentCallback
	{
		void onCallback(List<SBean.SocialComment> comments);
	}
	
	public class SyncPageCommentTask extends ExchangeServiceTask
	{
		SBean.SyncPageCommentReq req;
		SyncPageCommentCallback callback;
		
		SyncPageCommentTask(int themeType, int themeId, int tag, int pageNo, int len, SyncPageCommentCallback callback)
		{
			this.req = new SBean.SyncPageCommentReq(themeType, themeId, tag, pageNo, len);
			this.callback = callback;
		}
		
		@Override
		void doTaskImpl()
		{
			gs.getRPCManager().sendExchangeSocialReq(id, SBean.ForwardData.eReqSyncPage, req);
		}

		@Override
		void onTimeout()
		{
			callback.onCallback(GameData.emptyList());
		}
		
		void onCallback(SBean.SyncPageCommentRes res)
		{
			callback.onCallback(res.comments);
		}
	}
	
	public void syncPageComment(int themeType, int themeId, int tag, int pageNo, int len, SyncPageCommentCallback callback)
	{
		execTask(new SyncPageCommentTask(themeType, themeId, tag, pageNo, len, callback));
	}
	//------------------------------------------------------------------------------------------------------------------------
	public void onHandleSocialRes(SBean.ForwardData data)
	{
		ForwardData.ForwardTask task = ForwardData.decodePacket(data);
		if (task != null)
		{
			onReceivePacket(task.taskId, data.dataType, task.obj);
		}
	}
	
	public void sendAllServerMsg(SBean.MessageInfo msg)
	{
		gs.getRPCManager().sendAllServerMsg(msg);
	}
	
	

	
	private GameServer gs;
	private AtomicInteger nextTaskID = new AtomicInteger();
	private ConcurrentMap<Integer, ExchangeServiceTask> tasks = new ConcurrentHashMap<>();
	static final int CMD_MAX_WAIT_TIME = 3;
}
