
package i3k.gs;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;

import i3k.IDIP;
import i3k.util.GameTime;
import ket.kio.NetAddress;
import ket.kio.NetManager;
import ket.util.Stream;




public class IDIPService
{

	public IDIPService()
	{

	}
	
	public NetManager getNetManager() 
	{ 
		return managerNet; 
	}
	
	public void start(String host, int port)
	{
		System.out.println("IDIPService start ...");
		setProxyNetAddress(host, port);
		managerNet.start();
		bstart = true;
	}
	
	public void destroy()
	{
		System.out.println("IDIPService destroy ...");
		bstart = false;
		cancelAll();
		managerNet.destroy();
	}
	
	public void onTimer(int tick)
	{
		if (!bstart)
			return;
		managerNet.checkIdleConnections();
		checkTimeOutCmd(tick);
	}
	
	private int getNextSessionID() 
	{
		return nextSessionID.incrementAndGet();
	}
	
	void setProxyNetAddress(String host, int port)
	{
		proxyAddr.set(host, port);
		System.out.println("set proxy server net address : ("+host+":"+port+")");
	}
	
	public interface IDIPCallback
	{
		public void onCallback(IDIP.IdipHeader rspHeader, Stream.IStreamable rspBody);
	}
	
	public void sendIDIPCMDMessage(IDIP.IdipHeader header, Stream.IStreamable bodyData, final IDIPCallback callback)
	{
		if (!bstart)
		{
			System.out.println(GameTime.getTimeStampStr() + ": sendIDIPCMDMessage        : failed, for serice not start!");
			return;
		}
		int sid = getNextSessionID();
		TCPIDIPClient client = new TCPIDIPClient(this, sid, header, bodyData, callback);
		client.setServerAddr(proxyAddr);
		tasks.put(client.taskSID, client);
		client.open();
		System.out.println(GameTime.getTimeStampStr() + ": sendIDIPCMDMessage        : " + client);
	}

	public void onTCPIDIPClientOpen(TCPIDIPClient peer)
	{
		System.out.println(GameTime.getTimeStampStr() + ": onTCPIDIPClientOpen       : " + peer);
		peer.sendPacket(peer.taskPacket);
	}
	
	public void onTCPIDIPClientOpenFailed(TCPIDIPClient peer, ket.kio.ErrorCode errcode)
	{
		System.out.println(GameTime.getTimeStampStr() + ": onTCPIDIPClientOpenFailed : " + peer + ", " + errcode.toString());
		TCPIDIPClient client = tasks.remove(peer.taskSID);
		if (client != null)
		{
			String errMsg = "net work exception! " + errcode.toString();
			if (errcode == ket.kio.ErrorCode.eConnectRefused)
				errMsg = "net work exception! ps connect refused, check ps can work!";
			IDIP.IdipHeader rspHeader = client.createResHeader(IDIP.IDIP_HEADER_RESULT_NETWORK_EXCEPTION, errMsg, GameTime.getGMTTime()); 
			client.callback.onCallback(rspHeader, null);
			client.close();
		}
	}
	
	public void onTCPIDIPClientClose(TCPIDIPClient peer, ket.kio.ErrorCode errcode)
	{
		System.out.println(GameTime.getTimeStampStr() + ": onTCPIDIPClientClose      : " + peer + ", " + errcode.toString());
		TCPIDIPClient client = tasks.remove(peer.taskSID);
		if (client != null)
		{
			String errMsg = "net work exception! " + errcode.toString();
			if (errcode == ket.kio.ErrorCode.eOK)
				errMsg = "net work exception! ps close session, maybe gs connect refused or gs parse packet throw exception!";
			IDIP.IdipHeader rspHeader = client.createResHeader(IDIP.IDIP_HEADER_RESULT_NETWORK_EXCEPTION, errMsg, GameTime.getGMTTime()); 
			client.callback.onCallback(rspHeader, null);
			client.close();
		}
	}
	
	public void onTCPIDIPClientPacketRecv(TCPIDIPClient peer, IDIPPacket packet)
	{
		System.out.println(GameTime.getTimeStampStr() + ": onTCPIDIPClientPacketRecv : " + peer);
		TCPIDIPClient client = tasks.remove(peer.taskSID);
		if (client != null)
		{
			client.callback.onCallback(packet.header, IDIP.decodePacket(packet.header.Cmdid, packet.body));
			client.close();
		}
	}
	
	
	List<TCPIDIPClient> getTimeOutIDIPClients(int now)
	{
		List<TCPIDIPClient> timeoutTasks = new ArrayList<TCPIDIPClient>();
		Iterator<TCPIDIPClient> it = tasks.values().iterator();
		while (it.hasNext()) 
		{
			TCPIDIPClient client = it.next();
			if (now >= client.startTime + CMD_MAX_WAIT_TIME) 
			{
				client = tasks.remove(client.taskSID);
				if (client != null)
				{
					System.out.println(GameTime.getTimeStampStr() + ": getTimeOutIDIPClients     : " + client);
					timeoutTasks.add(client);	
				}
				//it.remove();
				
			}
		}
		return timeoutTasks;
	}
	
	void checkTimeOutCmd(int tick)
	{
		List<TCPIDIPClient> timeoutTasks = getTimeOutIDIPClients(tick);
		for (TCPIDIPClient client : timeoutTasks)
		{ 
			IDIP.IdipHeader rspHeader = client.createResHeader(IDIP.IDIP_HEADER_RESULT_TIMEOUT, "wait for response time out! check ps config valid and message packet valid!", GameTime.getGMTTime()); 
			client.callback.onCallback(rspHeader, null);
			client.close();
		}
	}
	
	void cancelAll()
	{
		System.out.println("IDIPService cancelAll request tasks");
		Iterator<TCPIDIPClient> it = tasks.values().iterator();
		while (it.hasNext()) 
		{
			TCPIDIPClient client = it.next();
			IDIP.IdipHeader rspHeader = client.createResHeader(IDIP.IDIP_HEADER_RESULT_TIMEOUT, "for idip service shut down, cancel all request!", GameTime.getGMTTime()); 
			client.callback.onCallback(rspHeader, null);
			client.close();
		}
		tasks.clear();
	}
	
	//TODO
	
	static IDIP.IdipHeader createHeader(int cmdid, int now)
	{
		IDIP.IdipHeader header = new IDIP.IdipHeader();
		header.PacketLen = 0;
		header.Cmdid = cmdid;
		header.Seqid = 0;
		header.ServiceName = "IDIP";
		header.SendTime = now;
		header.Version = 0;
		header.Authenticate = "ctx";
		header.Result = 0;
		header.RetErrMsg = "";
		return header;
	}
	
	static IDIP.IdipHeader createResHeader(IDIP.IdipHeader headerReq, int result, String retErrMsg, int now)
	{
		IDIP.IdipHeader headerRes = new IDIP.IdipHeader();
		headerRes.PacketLen = 0;
		headerRes.Cmdid = headerReq.Cmdid + 1;
		headerRes.Seqid = headerReq.Seqid;
		headerRes.ServiceName = headerReq.ServiceName;
		headerRes.SendTime = now;
		headerRes.Version = headerReq.Version;
		headerRes.Authenticate = headerReq.Authenticate;
		headerRes.Result = result;
		headerRes.RetErrMsg = retErrMsg;
		return headerRes;
	}
	

	static class IDIPResponse
	{
		IDIPResponse()
		{
			
		}
		
		IDIPResponse(IDIP.IdipHeader header, Stream.IStreamable body)
		{
			this.header = header;
			this.body = body;
		}
		
		IDIP.IdipHeader header;
		Stream.IStreamable body;
	}
	static class IDIPCmdCallback implements IDIPService.IDIPCallback
	{
		IDIPResponse response;
		public IDIPCmdCallback()
		{
			
		}
		
		public void onCallback(IDIP.IdipHeader rspHeader, Stream.IStreamable rspBody) 
		{
			this.response = new IDIPResponse(rspHeader, rspBody);
			synchronized (this)
			{
				this.notify();
			}
			
		}
	}
	
	public IDIPResponse sendIDIPCmdMessage(final IDIP.IdipHeader header, final Stream.IStreamable bodyData)
	{
		IDIPCmdCallback callback = new IDIPCmdCallback();
		synchronized (callback)
		{
			try
			{
				this.sendIDIPCMDMessage(header, bodyData, callback);
				callback.wait();
				return callback.response;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return new IDIPResponse();
	}
	
	public static class CommandResponseHeader
	{
		public int result = IDIP.IDIP_HEADER_RESULT_OTHER_ERROR;
		public String errMsg = "unknown error!";
		void setHeader(int result, String errMsg)
		{
			this.result = result;
			this.errMsg = errMsg;
		}
		public CommandResponseHeader(IDIP.IdipHeader header)
		{
			if (header != null)
				setHeader(header.Result, header.RetErrMsg);
		}
		public String toString()
		{
			return "(" + result + " " + errMsg + ")";
		}
	}
	
	public static class CommandResponse<T extends Stream.IStreamable>
	{
		public CommandResponseHeader header; 
		public T body;
		
		public CommandResponse(Class<T> kind, IDIPResponse rsp)
		{
			header = new CommandResponseHeader(rsp.header);
			body = kind.isInstance(rsp.body) ? (T)rsp.body : null;
		}
		
		public String toString()
		{
			return header.toString();
		}
	}
	
	public <Req extends Stream.IStreamable, Rsp extends Stream.IStreamable> CommandResponse<Rsp> doIDIPCommand(IDIP.IdipHeader header, Req req, Class<Rsp> kind)
	{
		IDIPResponse rsp = sendIDIPCmdMessage(header, req);
		return new CommandResponse<Rsp>(kind, rsp);
	}

	//TODO
	////begin commanders .
	public CommandResponse<IDIP.DoBanUsrRsp> doBanUsr(final int partition, final int roleId, final int leftTime, final String reason)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId + ", leftTime=" + leftTime + ", reason=" + reason;
		System.out.println("doBanUsr   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_BAN_USR_REQ, GameTime.getGMTTime());
		IDIP.DoBanUsrReq req = new IDIP.DoBanUsrReq();
		req.Partition = partition;
		req.RoleId = roleId;
		req.LeftTime = leftTime;
		req.Reason = reason == null ? new String() : reason;
		CommandResponse<IDIP.DoBanUsrRsp> rsp = doIDIPCommand(header, req, IDIP.DoBanUsrRsp.class);
		System.out.println("doBanUsr callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoUnBanUsrRsp> doUnBanUsr(final int partition, final int roleId)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId;
		System.out.println("doUnBanUsr   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_UNBAN_USR_REQ, GameTime.getGMTTime());
		IDIP.DoUnBanUsrReq req = new IDIP.DoUnBanUsrReq();
		req.Partition = partition;
		req.RoleId = roleId;
		CommandResponse<IDIP.DoUnBanUsrRsp> rsp = doIDIPCommand(header, req, IDIP.DoUnBanUsrRsp.class);
		System.out.println("doUnBanUsr callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoBanUsrChatRsp> doBanUsrChat(final int partition, final int roleId, final int leftTime, final String reason)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId + ", leftTime=" + leftTime + ", reason=" + reason;
		System.out.println("doBanUsrChat   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_BAN_USR_CHAT_REQ, GameTime.getGMTTime());
		IDIP.DoBanUsrChatReq req = new IDIP.DoBanUsrChatReq();
		req.Partition = partition;
		req.RoleId = roleId;
		req.LeftTime = leftTime;
		req.Reason = reason == null ? new String() : reason;
		CommandResponse<IDIP.DoBanUsrChatRsp> rsp = doIDIPCommand(header, req, IDIP.DoBanUsrChatRsp.class);
		System.out.println("doBanUsrChat callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoUnBanUsrChatRsp> doUnBanUsrChat(final int partition, final int roleId)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId;
		System.out.println("doUnBanUsrChat   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_UNBAN_USR_CHAT_REQ, GameTime.getGMTTime());
		IDIP.DoUnBanUsrChatReq req = new IDIP.DoUnBanUsrChatReq();
		req.Partition = partition;
		req.RoleId = roleId;
		CommandResponse<IDIP.DoUnBanUsrChatRsp> rsp = doIDIPCommand(header, req, IDIP.DoUnBanUsrChatRsp.class);
		System.out.println("doUnBanUsrChat callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoKickOnlineUsrRsp> doKickOnlineUsr(final int partition, final int roleId)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId;
		System.out.println("doKickOnlineUsr   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_KICK_ONLINE_USR_REQ, GameTime.getGMTTime());
		IDIP.DoKickOnlineUsrReq req = new IDIP.DoKickOnlineUsrReq();
		req.Partition = partition;
		req.RoleId = roleId;
		CommandResponse<IDIP.DoKickOnlineUsrRsp> rsp = doIDIPCommand(header, req, IDIP.DoKickOnlineUsrRsp.class);
		System.out.println("doKickOnlineUsr callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoPushMailAllRsp> doPushMailAll(final int partition, final String title, final String body, final int lifeTime, final int levelMin, final int levelMax, final int vipMin, final int vipMax, final int channelCount, final List<String> channelReq, final int attListCount, final List<IDIP.CommonItems> awards)
	{
		final String paramStr = "partition=" + partition + ", title=" + title + ", body=" + body + ", lifeTime=" + lifeTime + ", levelMin=" + levelMin + ", levelMax=" + levelMax + ", vipMin=" + vipMin + ", vipMax=" + vipMax + ", channelCount=" + channelCount + ", channelReq=" + channelReq + ", attListCount=" + attListCount;
		System.out.println("doPushMailAll   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_PUSH_MAIL_ALL_REQ, GameTime.getGMTTime());
		IDIP.DoPushMailAllReq req = new IDIP.DoPushMailAllReq();
		req.Partition = partition;
		req.Title = title == null ? new String() : title;
		req.Body = body == null ? new String() : body;
		req.LifeTime = lifeTime;
		req.LevelMin = levelMin;
		req.LevelMax = levelMax;
		req.VipMin = vipMin;
		req.VipMax = vipMax;
		req.ChannelCount = channelCount;
		req.ChannelReq = channelReq == null ? new ArrayList<String>() : channelReq;
		req.AttListCount = attListCount;
		req.Awards = awards == null ? new ArrayList<IDIP.CommonItems>() : awards;
		CommandResponse<IDIP.DoPushMailAllRsp> rsp = doIDIPCommand(header, req, IDIP.DoPushMailAllRsp.class);
		System.out.println("doPushMailAll callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoPushMailUsrRsp> doPushMailUsr(final int partition, final int roleId, final String title, final String body, final int lifeTime, final int attListCount, final List<IDIP.CommonItems> awards)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId + ", title=" + title + ", body=" + body + ", lifeTime=" + lifeTime + ", attListCount=" + attListCount;
		System.out.println("doPushMailUsr   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_PUSH_MAIL_USR_REQ, GameTime.getGMTTime());
		IDIP.DoPushMailUsrReq req = new IDIP.DoPushMailUsrReq();
		req.Partition = partition;
		req.RoleId = roleId;
		req.Title = title == null ? new String() : title;
		req.Body = body == null ? new String() : body;
		req.LifeTime = lifeTime;
		req.AttListCount = attListCount;
		req.Awards = awards == null ? new ArrayList<IDIP.CommonItems>() : awards;
		CommandResponse<IDIP.DoPushMailUsrRsp> rsp = doIDIPCommand(header, req, IDIP.DoPushMailUsrRsp.class);
		System.out.println("doPushMailUsr callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoGetChatRecordRsp> doGetChatRecord(final int partition)
	{
		final String paramStr = "partition=" + partition;
		System.out.println("doGetChatRecord   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_GET_CHAT_RECORD_REQ, GameTime.getGMTTime());
		IDIP.DoGetChatRecordReq req = new IDIP.DoGetChatRecordReq();
		req.Partition = partition;
		CommandResponse<IDIP.DoGetChatRecordRsp> rsp = doIDIPCommand(header, req, IDIP.DoGetChatRecordRsp.class);
		System.out.println("doGetChatRecord callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoAddRollNoticeRsp> doAddRollNotice(final int partition, final String body, final int cycle, final int startTime, final int leftTime)
	{
		final String paramStr = "partition=" + partition + ", body=" + body + ", cycle=" + cycle + ", startTime=" + startTime + ", leftTime=" + leftTime;
		System.out.println("doAddRollNotice   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_ADD_ROLL_NOTICE_REQ, GameTime.getGMTTime());
		IDIP.DoAddRollNoticeReq req = new IDIP.DoAddRollNoticeReq();
		req.Partition = partition;
		req.Body = body == null ? new String() : body;
		req.Cycle = cycle;
		req.startTime = startTime;
		req.LeftTime = leftTime;
		CommandResponse<IDIP.DoAddRollNoticeRsp> rsp = doIDIPCommand(header, req, IDIP.DoAddRollNoticeRsp.class);
		System.out.println("doAddRollNotice callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoDeleteRollNoticeRsp> doDeleteRollNotice(final int partition, final int noticeId)
	{
		final String paramStr = "partition=" + partition + ", noticeId=" + noticeId;
		System.out.println("doDeleteRollNotice   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_DELETE_ROLL_NOTICE_REQ, GameTime.getGMTTime());
		IDIP.DoDeleteRollNoticeReq req = new IDIP.DoDeleteRollNoticeReq();
		req.Partition = partition;
		req.NoticeId = noticeId;
		CommandResponse<IDIP.DoDeleteRollNoticeRsp> rsp = doIDIPCommand(header, req, IDIP.DoDeleteRollNoticeRsp.class);
		System.out.println("doDeleteRollNotice callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoInquiryRoleInfoRsp> doInquiryRoleInfo(final int partition, final int roleId)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId;
		System.out.println("doInquiryRoleInfo   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_INQUIRY_ROLE_INFO_REQ, GameTime.getGMTTime());
		IDIP.DoInquiryRoleInfoReq req = new IDIP.DoInquiryRoleInfoReq();
		req.Partition = partition;
		req.RoleId = roleId;
		CommandResponse<IDIP.DoInquiryRoleInfoRsp> rsp = doIDIPCommand(header, req, IDIP.DoInquiryRoleInfoRsp.class);
		System.out.println("doInquiryRoleInfo callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoInquiryRoleIdByNameRsp> doInquiryRoleIdByName(final int partition, final String roleName)
	{
		final String paramStr = "partition=" + partition + ", roleName=" + roleName;
		System.out.println("doInquiryRoleIdByName   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_REQ, GameTime.getGMTTime());
		IDIP.DoInquiryRoleIdByNameReq req = new IDIP.DoInquiryRoleIdByNameReq();
		req.Partition = partition;
		req.RoleName = roleName == null ? new String() : roleName;
		CommandResponse<IDIP.DoInquiryRoleIdByNameRsp> rsp = doIDIPCommand(header, req, IDIP.DoInquiryRoleIdByNameRsp.class);
		System.out.println("doInquiryRoleIdByName callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoQueryServerInfoRsp> doQueryServerInfo(final int partition)
	{
		final String paramStr = "partition=" + partition;
		System.out.println("doQueryServerInfo   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_QUERY_SERVER_INFO_REQ, GameTime.getGMTTime());
		IDIP.DoQueryServerInfoReq req = new IDIP.DoQueryServerInfoReq();
		req.Partition = partition;
		CommandResponse<IDIP.DoQueryServerInfoRsp> rsp = doIDIPCommand(header, req, IDIP.DoQueryServerInfoRsp.class);
		System.out.println("doQueryServerInfo callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoAddRegisterMailRsp> doAddRegisterMail(final int partition, final String title, final String body, final int channelCount, final List<String> channelReq, final int attListCount, final List<IDIP.CommonItems> awards)
	{
		final String paramStr = "partition=" + partition + ", title=" + title + ", body=" + body + ", channelCount=" + channelCount + ", channelReq=" + channelReq + ", attListCount=" + attListCount;
		System.out.println("doAddRegisterMail   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_REGISTER_MAIL_REQ, GameTime.getGMTTime());
		IDIP.DoAddRegisterMailReq req = new IDIP.DoAddRegisterMailReq();
		req.Partition = partition;
		req.Title = title == null ? new String() : title;
		req.Body = body == null ? new String() : body;
		req.ChannelCount = channelCount;
		req.ChannelReq = channelReq == null ? new ArrayList<String>() : channelReq;
		req.AttListCount = attListCount;
		req.Awards = awards == null ? new ArrayList<IDIP.CommonItems>() : awards;
		CommandResponse<IDIP.DoAddRegisterMailRsp> rsp = doIDIPCommand(header, req, IDIP.DoAddRegisterMailRsp.class);
		System.out.println("doAddRegisterMail callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoDelRegisterMailRsp> doDelRegisterMail(final int partition, final int remailId)
	{
		final String paramStr = "partition=" + partition + ", remailId=" + remailId;
		System.out.println("doDelRegisterMail   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_REGISTER_MAIL_DEL_REQ, GameTime.getGMTTime());
		IDIP.DoDelRegisterMailReq req = new IDIP.DoDelRegisterMailReq();
		req.Partition = partition;
		req.RemailId = remailId;
		CommandResponse<IDIP.DoDelRegisterMailRsp> rsp = doIDIPCommand(header, req, IDIP.DoDelRegisterMailRsp.class);
		System.out.println("doDelRegisterMail callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoChangeRoleLevelRsp> doChangeRoleLevel(final int partition, final int roleId, final int level)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId + ", level=" + level;
		System.out.println("doChangeRoleLevel   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_CHANGE_ROLE_LEVEL_REQ, GameTime.getGMTTime());
		IDIP.DoChangeRoleLevelReq req = new IDIP.DoChangeRoleLevelReq();
		req.Partition = partition;
		req.RoleId = roleId;
		req.Level = level;
		CommandResponse<IDIP.DoChangeRoleLevelRsp> rsp = doIDIPCommand(header, req, IDIP.DoChangeRoleLevelRsp.class);
		System.out.println("doChangeRoleLevel callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoChangeRoleVipPointRsp> doChangeRoleVipPoint(final int partition, final int roleId, final int point)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId + ", point=" + point;
		System.out.println("doChangeRoleVipPoint   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_CHANGE_ROLE_VIP_POINT_REQ, GameTime.getGMTTime());
		IDIP.DoChangeRoleVipPointReq req = new IDIP.DoChangeRoleVipPointReq();
		req.Partition = partition;
		req.RoleId = roleId;
		req.Point = point;
		CommandResponse<IDIP.DoChangeRoleVipPointRsp> rsp = doIDIPCommand(header, req, IDIP.DoChangeRoleVipPointRsp.class);
		System.out.println("doChangeRoleVipPoint callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoAddRoleGodPayRsp> doAddRoleGodPay(final int partition, final int roleId, final int payLevel)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId + ", payLevel=" + payLevel;
		System.out.println("doAddRoleGodPay   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_ADD_ROLE_GOD_PAY_REQ, GameTime.getGMTTime());
		IDIP.DoAddRoleGodPayReq req = new IDIP.DoAddRoleGodPayReq();
		req.Partition = partition;
		req.RoleId = roleId;
		req.PayLevel = payLevel;
		CommandResponse<IDIP.DoAddRoleGodPayRsp> rsp = doIDIPCommand(header, req, IDIP.DoAddRoleGodPayRsp.class);
		System.out.println("doAddRoleGodPay callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoQueryRolesRsp> doQueryRoles(final int partition, final String openId)
	{
		final String paramStr = "partition=" + partition + ", openId=" + openId;
		System.out.println("doQueryRoles   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_QUERY_ROLES_REQ, GameTime.getGMTTime());
		IDIP.DoQueryRolesReq req = new IDIP.DoQueryRolesReq();
		req.Partition = partition;
		req.OpenId = openId == null ? new String() : openId;
		CommandResponse<IDIP.DoQueryRolesRsp> rsp = doIDIPCommand(header, req, IDIP.DoQueryRolesRsp.class);
		System.out.println("doQueryRoles callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoSendRoleGiftRsp> doSendRoleGift(final int partition, final String openId, final int roleId, final int conditionType, final int conditionValue, final int attListCount, final List<IDIP.CommonItems> awards, final String action)
	{
		final String paramStr = "partition=" + partition + ", openId=" + openId + ", roleId=" + roleId + ", conditionType=" + conditionType + ", conditionValue=" + conditionValue + ", attListCount=" + attListCount + ", action=" + action;
		System.out.println("doSendRoleGift   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_SEND_ROLE_GIFT_REQ, GameTime.getGMTTime());
		IDIP.DoSendRoleGiftReq req = new IDIP.DoSendRoleGiftReq();
		req.Partition = partition;
		req.OpenId = openId == null ? new String() : openId;
		req.RoleId = roleId;
		req.ConditionType = conditionType;
		req.ConditionValue = conditionValue;
		req.AttListCount = attListCount;
		req.Awards = awards == null ? new ArrayList<IDIP.CommonItems>() : awards;
		req.Action = action == null ? new String() : action;
		CommandResponse<IDIP.DoSendRoleGiftRsp> rsp = doIDIPCommand(header, req, IDIP.DoSendRoleGiftRsp.class);
		System.out.println("doSendRoleGift callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	public CommandResponse<IDIP.DoAddRoleVipPointRsp> doAddRoleVipPoint(final int partition, final int roleId, final int point)
	{
		final String paramStr = "partition=" + partition + ", roleId=" + roleId + ", point=" + point;
		System.out.println("doAddRoleVipPoint   invoke:" + paramStr);
		final IDIP.IdipHeader header = createHeader(IDIP.IDIP_DO_ADD_ROLE_VIP_POINT_REQ, GameTime.getGMTTime());
		IDIP.DoAddRoleVipPointReq req = new IDIP.DoAddRoleVipPointReq();
		req.Partition = partition;
		req.RoleId = roleId;
		req.Point = point;
		CommandResponse<IDIP.DoAddRoleVipPointRsp> rsp = doIDIPCommand(header, req, IDIP.DoAddRoleVipPointRsp.class);
		System.out.println("doAddRoleVipPoint callback: " + paramStr + " ==> " + rsp);
		return rsp;
	}

	////end commanders .
	
	
	public static void main(String[] args)
	{
		System.out.println("idip test ...");
		IDIPService service  = new IDIPService();
		service.start("127.0.0.1", 7770);
		ScheduledExecutorService executor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor((r) -> Executors.defaultThreadFactory().newThread(r));
		ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
			try
			{
				service.onTimer(GameTime.getTime());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}, 5, 1, TimeUnit.SECONDS);
				
//		IDIPService.CommandResponse<IDIP.DoInquiryRoleInfoRsp> res = service.doInquiryRoleInfo(1, 1);
//		IDIPService.CommandResponse<IDIP.DoAddRollNoticeRsp> res = service.doAddRollNotice(1, "��������", 5, 60);
//		System.out.println(res.header.errMsg);
		service.destroy();
		future.cancel(true);
		executor.shutdown();
		try
		{
			while (!executor.awaitTermination(1, TimeUnit.SECONDS))
			{
			}
		}
		catch (Exception ignored)
		{
		}
		System.out.println("idip test end.");
	}
	
	boolean bstart;
	NetManager managerNet = new NetManager();
	NetAddress proxyAddr = new NetAddress("localhost", 7770); 
	AtomicInteger nextSessionID = new AtomicInteger();
	Map<Integer, TCPIDIPClient> tasks = new ConcurrentHashMap<Integer, TCPIDIPClient>();
	static final int CMD_MAX_WAIT_TIME = 2;
}
