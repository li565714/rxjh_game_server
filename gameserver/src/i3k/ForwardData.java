
package i3k;


import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

import ket.util.Stream;
import ket.util.Stream.BytesOutputStream;

public final class ForwardData
{

	public static SBean.ForwardData encodePacket(int dataType, int taskId, Stream.IStreamable bodyData)
	{
		BytesOutputStream bsos = new BytesOutputStream();
		Stream.AOStream os = new Stream.OStreamBE(bsos);
		os.pushInteger(taskId);
		os.push(bodyData);

		return new SBean.ForwardData(dataType, ByteBuffer.wrap(bsos.array(), 0, bsos.size()));
	}

	public static class ForwardTask
	{
		public int taskId;
		public Stream.IStreamable obj;
		public ForwardTask(int taskId, Stream.IStreamable obj)
		{
			this.taskId = taskId;
			this.obj = obj;
		}
	}
	public static ForwardTask decodePacket(SBean.ForwardData data)
	{
		byte[] bs = data.content.array();
		Stream.BytesInputStream bais = new Stream.BytesInputStream(bs, 0, bs.length);
		Stream.AIStream is = new Stream.IStreamBE(bais);
		try
		{
			int taskId = is.popInteger();
			switch( data.dataType )
			{
			case SBean.ForwardData.eReqSyncRole:
				{
					
					break;
				}
			case SBean.ForwardData.eReqSyncPage:
				{
					SBean.SyncPageCommentReq obj = new SBean.SyncPageCommentReq();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			case SBean.ForwardData.eResSyncPage:
				{
					SBean.SyncPageCommentRes obj = new SBean.SyncPageCommentRes();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			case SBean.ForwardData.eReqSendComment:
				{
					SBean.SendCommentReq obj = new SBean.SendCommentReq();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			case SBean.ForwardData.eResSendComment:
				{
					SBean.SendCommentRes obj = new SBean.SendCommentRes();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			case SBean.ForwardData.eReqLikeComment:
				{
					SBean.LikeCommentReq obj = new SBean.LikeCommentReq();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			case SBean.ForwardData.eResLikeComment:
				{
					SBean.LikeCommentRes obj = new SBean.LikeCommentRes();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			case SBean.ForwardData.eReqDislikeComment:
				{
					SBean.DislikeCommentReq obj = new SBean.DislikeCommentReq();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			case SBean.ForwardData.eResDislikeComment:
				{
					SBean.DislikeCommentRes obj = new SBean.DislikeCommentRes();
					obj.decode(is);
					return new ForwardTask(taskId, obj);
				}
			default:
				break;
			}
		}
		catch(Exception ex)
		{
		}
		return null;
	}
	
}
