// modified by i3k.gtool.QQMetaGen at Sat May 06 17:01:18 CST 2017.

package i3k;

import java.util.List;
import java.util.ArrayList;
import ket.util.Stream;

public final class IDIP
{


	public static final int IDIP_HEADER_RESULT_EMPTY_PACKET_SUCCESS = 1;
	public static final int IDIP_HEADER_RESULT_SUCCESS = 0;
	public static final int IDIP_HEADER_RESULT_NETWORK_EXCEPTION = -1;
	public static final int IDIP_HEADER_RESULT_TIMEOUT = -2;
	public static final int IDIP_HEADER_RESULT_DB_EXCEPTION = -3;
	public static final int IDIP_HEADER_RESULT_API_EXCEPTION = -4;
	public static final int IDIP_HEADER_RESULT_SERVER_BUSY = -5;
	public static final int IDIP_HEADER_RESULT_OTHER_ERROR = -100;
	public static final int IDIP_HEADER_RESULT_USER_NOT_EXIST = -101;
	public static final int IDIP_HEADER_RESULT_ROLE_NOT_EXIST = -102;
	public static final int IDIP_HEADER_RESULT_ROLE_CONDITION_NOT_MATCH = -103;
	public static final int YYB_GIFT_CONDITION_TYPE_NONE = 0;
	public static final int YYB_GIFT_CONDITION_TYPE_LEVEL = 1;
	public static final int YYB_GIFT_CONDITION_TYPE_VIP_POINT = 2;

	// IDIP命令编码
	// 禁止用户登陆请求
	public static final int IDIP_DO_BAN_USR_REQ = 0x1001;
	// 禁止用户登陆应答
	public static final int IDIP_DO_BAN_USR_RSP = 0x1002;
	// 解禁用户禁止登陆请求
	public static final int IDIP_DO_UNBAN_USR_REQ = 0x1003;
	// 解禁用户禁止登陆应答
	public static final int IDIP_DO_UNBAN_USR_RSP = 0x1004;
	// 对用户禁言请求
	public static final int IDIP_DO_BAN_USR_CHAT_REQ = 0x1005;
	// 对用户禁言应答
	public static final int IDIP_DO_BAN_USR_CHAT_RSP = 0x1006;
	// 解禁用户禁言请求
	public static final int IDIP_DO_UNBAN_USR_CHAT_REQ = 0x1007;
	// 解禁用户禁言应答
	public static final int IDIP_DO_UNBAN_USR_CHAT_RSP = 0x1008;
	// 在线踢人请求
	public static final int IDIP_DO_KICK_ONLINE_USR_REQ = 0x1009;
	// 在线踢人应答
	public static final int IDIP_DO_KICK_ONLINE_USR_RSP = 0x100a;
	// 全服邮件请求
	public static final int IDIP_DO_PUSH_MAIL_ALL_REQ = 0x100b;
	// 全服邮件应答
	public static final int IDIP_DO_PUSH_MAIL_ALL_RSP = 0x100c;
	// 向指定的玩家发送邮件请求
	public static final int IDIP_DO_PUSH_MAIL_USR_REQ = 0x100d;
	// 向指定玩家发送邮件应答
	public static final int IDIP_DO_PUSH_MAIL_USR_RSP = 0x100e;
	// 获取服务器最近50条消息请求
	public static final int IDIP_DO_GET_CHAT_RECORD_REQ = 0x100f;
	// 获取服务器最近50条消息应答
	public static final int IDIP_DO_GET_CHAT_RECORD_RSP = 0x1010;
	// 添加走马灯公告请求
	public static final int IDIP_DO_ADD_ROLL_NOTICE_REQ = 0x1011;
	// 添加走马灯公告应答
	public static final int IDIP_DO_ADD_ROLL_NOTICE_RSP = 0x1012;
	// 删除走马灯公告请求
	public static final int IDIP_DO_DELETE_ROLL_NOTICE_REQ = 0x1013;
	// 删除走马灯公告应答
	public static final int IDIP_DO_DELETE_ROLL_NOTICE_RSP = 0x1014;
	// 查询角色信息请求
	public static final int IDIP_DO_INQUIRY_ROLE_INFO_REQ = 0x1015;
	// 查询角色信息应答
	public static final int IDIP_DO_INQUIRY_ROLE_INFO_RSP = 0x1016;
	// 查询玩家roleID请求
	public static final int IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_REQ = 0x1017;
	// 查询玩家RoleID应答
	public static final int IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_RSP = 0x1018;
	// 获取服务器列表请求
	public static final int IDIP_DO_QUERY_SERVER_INFO_REQ = 0x1019;
	// 获取服务器列表应答
	public static final int IDIP_DO_QUERY_SERVER_INFO_RSP = 0x101a;
	// 添加注册邮件请求
	public static final int IDIP_DO_REGISTER_MAIL_REQ = 0x101b;
	// 添加注册邮件应答
	public static final int IDIP_DO_REGISTER_MAIL_RSP = 0x101c;
	// 删除注册邮件请求
	public static final int IDIP_DO_REGISTER_MAIL_DEL_REQ = 0x101d;
	// 删除注册邮件应答
	public static final int IDIP_DO_REGISTER_MAIL_DEL_RSP = 0x101e;
	// 修改角色等级请求
	public static final int IDIP_DO_CHANGE_ROLE_LEVEL_REQ = 0x1020;
	// 修改角色等级应答
	public static final int IDIP_DO_CHANGE_ROLE_LEVEL_RSP = 0x1021;
	// 修改角色VIP点数请求
	public static final int IDIP_DO_CHANGE_ROLE_VIP_POINT_REQ = 0x1022;
	// 修改角色VIP点数应答
	public static final int IDIP_DO_CHANGE_ROLE_VIP_POINT_RSP = 0x1023;
	// 添加角色充值请求
	public static final int IDIP_DO_ADD_ROLE_GOD_PAY_REQ = 0x1024;
	// 添加角色充值应答
	public static final int IDIP_DO_ADD_ROLE_GOD_PAY_RSP = 0x1025;
	// 查询账户角色请求
	public static final int IDIP_DO_QUERY_ROLES_REQ = 0x1026;
	// 查询账户角色应答
	public static final int IDIP_DO_QUERY_ROLES_RSP = 0x1027;
	// 应用宝发送奖励请求
	public static final int IDIP_DO_SEND_ROLE_GIFT_REQ = 0x1028;
	// 应用宝发送奖励应答
	public static final int IDIP_DO_SEND_ROLE_GIFT_RSP = 0x1029;
	// 增加角色VIP点数请求
	public static final int IDIP_DO_ADD_ROLE_VIP_POINT_REQ = 0x1030;
	// 增加角色VIP点数应答
	public static final int IDIP_DO_ADD_ROLE_VIP_POINT_RSP = 0x1031;

	// 系统宏
	// 封装包包体的最大值, 基本的数据包的大小在15K以下，只有查询邮件列表这个数据量比较大，在23K左右
	public static final int IDIP_BODY_LENGTH = 24000;
	// SERVICENAME的最大长度
	public static final int SERVICE_NAME_LENGTH = 16;
	// 加密串的最大长度
	public static final int AUTHENTICATE_LENGTH = 32;
	// 错误码
	public static final int ERROR_MSG_LENGTH = 100;
	// openid的长度
	public static final int MAX_OPENID_LEN = 64;
	// 聊天消息数组最大值
	public static final int MAX_MESSAGES_LEN = 1000;
	// 玩家名字最大长度
	public static final int MAX_ROLE_NAME_LEN = 30;
	// 渠道名字最大长度
	public static final int MAX_CHANNEL_NAME_LEN = 30;
	// UID最大长度
	public static final int MAX_UID_LEN = 64;
	// 聊天消息最大长度
	public static final int MAX_CHAT_MESSAGE_LEN = 200;
	// 邮件标题的长度
	public static final int MAX_MAILTITLE_LEN = 30;
	// 邮件内容的长度
	public static final int MAX_MAILCONTENT_LEN = 900;
	// 走马灯公告内容最大长度
	public static final int MAX_MOVING_NOTICE_LEN = 200;
	// 服务器名字最大长度
	public static final int MAX_SERVER_NAME_LEN = 30;
	// 封禁原因的长度
	public static final int MAX_REASON_LEN = 64;
	// 邮件附件物品列表的数组长度
	public static final int MAX_ATTLIST_NUM = 4;
	// 最大等级
	public static final int MAX_LEVEL = 1000;
	// 最小等级
	public static final int MIN_LEVEL = 1;
	// 最大VIP等级
	public static final int MAX_VIP_LEVEL = 100;
	// 最小VIP等级
	public static final int MIN_VIP_LEVEL = 0;
	// 最大角色数量
	public static final int MAX_ROLE_SIZE = 4;
	// 最大动作标识长度
	public static final int MAX_SEND_ACTION_LENGTH = 15;
	// 最大OPENID长度
	public static final int MAX_OPENID_LENGTH = 60;
	// 最大动作标识长度
	public static final int MAX_CHANNEL_LEN = 25;

	public static final int PACKET_HEADER_SIZE = 4 + 4 + 4 + SERVICE_NAME_LENGTH + 4 + 4 + AUTHENTICATE_LENGTH + 4 + ERROR_MSG_LENGTH;



	// IDIP消息头
	public static class IdipHeader implements Stream.IStreamable
	{

		public IdipHeader() { }

		public IdipHeader(int PacketLen, int Cmdid, int Seqid, String ServiceName, 
		                  int SendTime, int Version, String Authenticate, int Result, 
		                  String RetErrMsg)
		{
			this.PacketLen = PacketLen;
			this.Cmdid = Cmdid;
			this.Seqid = Seqid;
			this.ServiceName = ServiceName;
			this.SendTime = SendTime;
			this.Version = Version;
			this.Authenticate = Authenticate;
			this.Result = Result;
			this.RetErrMsg = RetErrMsg;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			PacketLen = is.popInteger();
			Cmdid = is.popInteger();
			Seqid = is.popInteger();
			ServiceName = is.popString(SERVICE_NAME_LENGTH);
			SendTime = is.popInteger();
			Version = is.popInteger();
			Authenticate = is.popString(AUTHENTICATE_LENGTH);
			Result = is.popInteger();
			RetErrMsg = is.popString(ERROR_MSG_LENGTH);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(PacketLen);
			os.pushInteger(Cmdid);
			os.pushInteger(Seqid);
			os.pushString(ServiceName, SERVICE_NAME_LENGTH);
			os.pushInteger(SendTime);
			os.pushInteger(Version);
			os.pushString(Authenticate, AUTHENTICATE_LENGTH);
			os.pushInteger(Result);
			os.pushString(RetErrMsg, ERROR_MSG_LENGTH);
		}

		// 包长
		public int PacketLen;
		// 命令ID
		public int Cmdid;
		// 流水号
		public int Seqid;
		// 服务名
		public String ServiceName = new String();
		// 发送时间YYYYMMDD对应的整数
		public int SendTime;
		// 版本号
		public int Version;
		// 加密串
		public String Authenticate = new String();
		// 错误码,返回码类型：0：处理成功，需要解开包体获得详细信息,1：处理成功，但包体返回为空，不需要处理包体（eg：查询用户角色，用户角色不存在等），-1: 网络通信异常,-2：超时,-3：数据库操作异常,-4：API返回异常,-5：服务器忙,-6：其他错误,小于-100 ：用户自定义错误，需要填写szRetErrMsg
		public int Result;
		// 错误信息
		public String RetErrMsg = new String();
	}

	// IDIP数据包
	public static class IdipDataPaket implements Stream.IStreamable
	{

		public IdipDataPaket() { }

		public IdipDataPaket(IdipHeader IdipHead, String IdipBody)
		{
			this.IdipHead = IdipHead;
			this.IdipBody = IdipBody;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			is.pop(IdipHead);
			IdipBody = is.popString(IDIP_BODY_LENGTH);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.push(IdipHead);
			os.pushString(IdipBody, IDIP_BODY_LENGTH);
		}

		// 包头信息
		public IdipHeader IdipHead = new IdipHeader();
		// 包体信息
		public String IdipBody = new String();
	}

	// 物品id + 物品count
	public static class CommonItems implements Stream.IStreamable
	{

		public CommonItems() { }

		public CommonItems(int id, int count)
		{
			this.id = id;
			this.count = count;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			id = is.popInteger();
			count = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(id);
			os.pushInteger(count);
		}

		// 物品ID
		public int id;
		// 物品数量
		public int count;
	}

	// 单条聊天消息内容
	public static class Message implements Stream.IStreamable
	{

		public Message() { }

		public Message(int roleId, String roleName, int roleTime, String body)
		{
			this.roleId = roleId;
			this.roleName = roleName;
			this.roleTime = roleTime;
			this.body = body;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			roleId = is.popInteger();
			roleName = is.popString(MAX_ROLE_NAME_LEN);
			roleTime = is.popInteger();
			body = is.popString(MAX_CHAT_MESSAGE_LEN);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(roleId);
			os.pushString(roleName, MAX_ROLE_NAME_LEN);
			os.pushInteger(roleTime);
			os.pushString(body, MAX_CHAT_MESSAGE_LEN);
		}

		// 玩家roleID
		public int roleId;
		// 玩家名字
		public String roleName = new String();
		// 发言时间
		public int roleTime;
		// 聊天信息内容
		public String body = new String();
	}

	// 服务器列表信息
	public static class ServerOverview implements Stream.IStreamable
	{

		public ServerOverview() { }

		public ServerOverview(int serverId, String serverName, int openTime, int onlineCount, 
		                      int roleTotalCreate)
		{
			this.serverId = serverId;
			this.serverName = serverName;
			this.openTime = openTime;
			this.onlineCount = onlineCount;
			this.roleTotalCreate = roleTotalCreate;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			serverId = is.popInteger();
			serverName = is.popString(MAX_SERVER_NAME_LEN);
			openTime = is.popInteger();
			onlineCount = is.popInteger();
			roleTotalCreate = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(serverId);
			os.pushString(serverName, MAX_SERVER_NAME_LEN);
			os.pushInteger(openTime);
			os.pushInteger(onlineCount);
			os.pushInteger(roleTotalCreate);
		}

		// 服务器ID
		public int serverId;
		// 服务器名字
		public String serverName = new String();
		// 服务器开服时间
		public int openTime;
		// 在线玩家数量
		public int onlineCount;
		// 在线玩家数量
		public int roleTotalCreate;
	}

	// 应用宝角色信息
	public static class YYBRoleInfo implements Stream.IStreamable
	{

		public YYBRoleInfo() { }

		public YYBRoleInfo(int roleId, String roleName)
		{
			this.roleId = roleId;
			this.roleName = roleName;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			roleId = is.popInteger();
			roleName = is.popString(MAX_ROLE_NAME_LEN);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(roleId);
			os.pushString(roleName, MAX_ROLE_NAME_LEN);
		}

		// 玩家roleID
		public int roleId;
		// 玩家名字
		public String roleName = new String();
	}

	// 禁止登陆请求
	public static class DoBanUsrReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_BAN_USR_REQ;

		public DoBanUsrReq() { }

		public DoBanUsrReq(int Partition, int RoleId, int LeftTime, String Reason)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
			this.LeftTime = LeftTime;
			this.Reason = Reason;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
			LeftTime = is.popInteger();
			Reason = is.popString(MAX_REASON_LEN);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
			os.pushInteger(LeftTime);
			os.pushString(Reason, MAX_REASON_LEN);
		}

		// 服务器ID
		public int Partition;
		// 角色ID，可以为多个以;隔开
		public int RoleId;
		public int LeftTime;
		// 封号原因
		public String Reason = new String();
	}

	// 禁止登陆应答
	public static class DoBanUsrRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_BAN_USR_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 解禁用户登陆请求
	public static class DoUnBanUsrReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_UNBAN_USR_REQ;

		public DoUnBanUsrReq() { }

		public DoUnBanUsrReq(int Partition, int RoleId)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
		}

		// 服务器ID
		public int Partition;
		// 角色ID，可以为多个以;隔开
		public int RoleId;
	}

	// 解禁用户登陆应答
	public static class DoUnBanUsrRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_UNBAN_USR_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 用户禁言请求
	public static class DoBanUsrChatReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_BAN_USR_CHAT_REQ;

		public DoBanUsrChatReq() { }

		public DoBanUsrChatReq(int Partition, int RoleId, int LeftTime, String Reason)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
			this.LeftTime = LeftTime;
			this.Reason = Reason;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
			LeftTime = is.popInteger();
			Reason = is.popString(MAX_REASON_LEN);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
			os.pushInteger(LeftTime);
			os.pushString(Reason, MAX_REASON_LEN);
		}

		// 服务器ID
		public int Partition;
		// 角色ID，可以为多个以;隔开
		public int RoleId;
		public int LeftTime;
		// 禁言原因
		public String Reason = new String();
	}

	// 用户禁言应答
	public static class DoBanUsrChatRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_BAN_USR_CHAT_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 解禁用户禁言请求
	public static class DoUnBanUsrChatReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_UNBAN_USR_CHAT_REQ;

		public DoUnBanUsrChatReq() { }

		public DoUnBanUsrChatReq(int Partition, int RoleId)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
		}

		// 服务器ID
		public int Partition;
		// 角色ID，可以为多个以;隔开
		public int RoleId;
	}

	// 解禁用户禁言应答
	public static class DoUnBanUsrChatRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_UNBAN_USR_CHAT_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 在线踢人请求
	public static class DoKickOnlineUsrReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_KICK_ONLINE_USR_REQ;

		public DoKickOnlineUsrReq() { }

		public DoKickOnlineUsrReq(int Partition, int RoleId)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
		}

		// 服务器ID
		public int Partition;
		// 角色ID，可以为多个以;隔开
		public int RoleId;
	}

	// 在线踢人应答
	public static class DoKickOnlineUsrRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_KICK_ONLINE_USR_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 全服邮件请求
	public static class DoPushMailAllReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_PUSH_MAIL_ALL_REQ;

		public DoPushMailAllReq() { }

		public DoPushMailAllReq(int Partition, String Title, String Body, int LifeTime, 
		                        int LevelMin, int LevelMax, int VipMin, int VipMax, 
		                        int ChannelCount, List<String> ChannelReq, int AttListCount, List<CommonItems> Awards)
		{
			this.Partition = Partition;
			this.Title = Title;
			this.Body = Body;
			this.LifeTime = LifeTime;
			this.LevelMin = LevelMin;
			this.LevelMax = LevelMax;
			this.VipMin = VipMin;
			this.VipMax = VipMax;
			this.ChannelCount = ChannelCount;
			this.ChannelReq = ChannelReq;
			this.AttListCount = AttListCount;
			this.Awards = Awards;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			Title = is.popString(MAX_MAILTITLE_LEN);
			Body = is.popString(MAX_MAILCONTENT_LEN);
			LifeTime = is.popInteger();
			LevelMin = is.popInteger();
			LevelMax = is.popInteger();
			VipMin = is.popInteger();
			VipMax = is.popInteger();
			ChannelCount = is.popInteger();
			ChannelReq = is.popStringList();
			AttListCount = is.popInteger();
			Awards = is.popList(CommonItems.class, AttListCount);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushString(Title, MAX_MAILTITLE_LEN);
			os.pushString(Body, MAX_MAILCONTENT_LEN);
			os.pushInteger(LifeTime);
			os.pushInteger(LevelMin);
			os.pushInteger(LevelMax);
			os.pushInteger(VipMin);
			os.pushInteger(VipMax);
			os.pushInteger(ChannelCount);
			os.pushStringList(ChannelReq);
			os.pushInteger(AttListCount);
			os.pushList(Awards, AttListCount);
		}

		// 服务器ID
		public int Partition;
		// 邮件标题
		public String Title = new String();
		// 内容
		public String Body = new String();
		// 邮件的生命周期
		public int LifeTime;
		// 等级限制
		public int LevelMin;
		// 等级限制
		public int LevelMax;
		// vip等级限制
		public int VipMin;
		// vip等级限制
		public int VipMax;
		// 可领取渠道数量(0-25)
		public int ChannelCount;
		// 可领取channel列表，格式2001;1002
		public List<String> ChannelReq = new ArrayList<String>();
		// 附件物品列表的最大数量(0-4)
		public int AttListCount;
		// 奖励列表,格式3001_1;3002_10
		public List<CommonItems> Awards = new ArrayList<CommonItems>();
	}

	// 全服邮件应答
	public static class DoPushMailAllRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_PUSH_MAIL_ALL_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 向玩家发送邮件请求
	public static class DoPushMailUsrReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_PUSH_MAIL_USR_REQ;

		public DoPushMailUsrReq() { }

		public DoPushMailUsrReq(int Partition, int RoleId, String Title, String Body, 
		                        int LifeTime, int AttListCount, List<CommonItems> Awards)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
			this.Title = Title;
			this.Body = Body;
			this.LifeTime = LifeTime;
			this.AttListCount = AttListCount;
			this.Awards = Awards;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
			Title = is.popString(MAX_MAILTITLE_LEN);
			Body = is.popString(MAX_MAILCONTENT_LEN);
			LifeTime = is.popInteger();
			AttListCount = is.popInteger();
			Awards = is.popList(CommonItems.class, AttListCount);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
			os.pushString(Title, MAX_MAILTITLE_LEN);
			os.pushString(Body, MAX_MAILCONTENT_LEN);
			os.pushInteger(LifeTime);
			os.pushInteger(AttListCount);
			os.pushList(Awards, AttListCount);
		}

		// 服务器ID
		public int Partition;
		// 用户id
		public int RoleId;
		// 邮件标题
		public String Title = new String();
		// 内容
		public String Body = new String();
		// 邮件生命周期
		public int LifeTime;
		// 附件物品列表的最大数量(0-4)
		public int AttListCount;
		// 奖励列表,格式3001_1;3002_10
		public List<CommonItems> Awards = new ArrayList<CommonItems>();
	}

	// 向玩家发送邮件应答
	public static class DoPushMailUsrRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_PUSH_MAIL_USR_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 获取服务器最近50条聊天消息请求
	public static class DoGetChatRecordReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_GET_CHAT_RECORD_REQ;

		public DoGetChatRecordReq() { }

		public DoGetChatRecordReq(int Partition)
		{
			this.Partition = Partition;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
		}

		// 服务器ID
		public int Partition;
	}

	// 获取服务器最近50条聊天消息应答
	public static class DoGetChatRecordRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_GET_CHAT_RECORD_RSP;

		public DoGetChatRecordRsp() { }

		public DoGetChatRecordRsp(int AttListCount, List<Message> Messages)
		{
			this.AttListCount = AttListCount;
			this.Messages = Messages;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			AttListCount = is.popInteger();
			Messages = is.popList(Message.class, AttListCount);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(AttListCount);
			os.pushList(Messages, AttListCount);
		}

		// List长度
		public int AttListCount;
		// 聊天消息数组
		public List<Message> Messages = new ArrayList<Message>();
	}

	// 添加走马灯公告请求
	public static class DoAddRollNoticeReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_ADD_ROLL_NOTICE_REQ;

		public DoAddRollNoticeReq() { }

		public DoAddRollNoticeReq(int Partition, String Body, int Cycle, int startTime, 
		                          int LeftTime)
		{
			this.Partition = Partition;
			this.Body = Body;
			this.Cycle = Cycle;
			this.startTime = startTime;
			this.LeftTime = LeftTime;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			Body = is.popString(MAX_MOVING_NOTICE_LEN);
			Cycle = is.popInteger();
			startTime = is.popInteger();
			LeftTime = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushString(Body, MAX_MOVING_NOTICE_LEN);
			os.pushInteger(Cycle);
			os.pushInteger(startTime);
			os.pushInteger(LeftTime);
		}

		// 服务器ID
		public int Partition;
		public String Body = new String();
		// 循环间隔-秒
		public int Cycle;
		// 开始时间
		public int startTime;
		// 持续时间（秒）
		public int LeftTime;
	}

	// 添加走马灯公告应答
	public static class DoAddRollNoticeRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_ADD_ROLL_NOTICE_RSP;

		public DoAddRollNoticeRsp() { }

		public DoAddRollNoticeRsp(int NoticeId)
		{
			this.NoticeId = NoticeId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			NoticeId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(NoticeId);
		}

		// 公告ID
		public int NoticeId;
	}

	// 删除走马灯公告请求
	public static class DoDeleteRollNoticeReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_DELETE_ROLL_NOTICE_REQ;

		public DoDeleteRollNoticeReq() { }

		public DoDeleteRollNoticeReq(int Partition, int NoticeId)
		{
			this.Partition = Partition;
			this.NoticeId = NoticeId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			NoticeId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(NoticeId);
		}

		// 服务器ID
		public int Partition;
		// 公告ID
		public int NoticeId;
	}

	// 删除走马灯公告应答
	public static class DoDeleteRollNoticeRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_DELETE_ROLL_NOTICE_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 查询角色信息请求
	public static class DoInquiryRoleInfoReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_INQUIRY_ROLE_INFO_REQ;

		public DoInquiryRoleInfoReq() { }

		public DoInquiryRoleInfoReq(int Partition, int RoleId)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
		}

		// 服务器ID
		public int Partition;
		// 玩家RoleID
		public int RoleId;
	}

	// 查询角色信息应答
	public static class DoInquiryRoleInfoRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_INQUIRY_ROLE_INFO_RSP;

		public DoInquiryRoleInfoRsp() { }

		public DoInquiryRoleInfoRsp(int RoleId, String RoleName, int ServerId, int Level, 
		                            int VipLevel, int PayNum, int CreateTime, String Channel, 
		                            String Uid, int IsOnline, int LastLoginTime, int Money, 
		                            int bindMoney, int Diamond, int bindDiamond, int BanStatus, 
		                            int DiamondFUseTotal, int DiamondRUseTotal, byte ClassType, byte TransfromLvl, 
		                            byte BWType, byte Gender)
		{
			this.RoleId = RoleId;
			this.RoleName = RoleName;
			this.ServerId = ServerId;
			this.Level = Level;
			this.VipLevel = VipLevel;
			this.PayNum = PayNum;
			this.CreateTime = CreateTime;
			this.Channel = Channel;
			this.Uid = Uid;
			this.IsOnline = IsOnline;
			this.LastLoginTime = LastLoginTime;
			this.Money = Money;
			this.bindMoney = bindMoney;
			this.Diamond = Diamond;
			this.bindDiamond = bindDiamond;
			this.BanStatus = BanStatus;
			this.DiamondFUseTotal = DiamondFUseTotal;
			this.DiamondRUseTotal = DiamondRUseTotal;
			this.ClassType = ClassType;
			this.TransfromLvl = TransfromLvl;
			this.BWType = BWType;
			this.Gender = Gender;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			RoleId = is.popInteger();
			RoleName = is.popString(MAX_ROLE_NAME_LEN);
			ServerId = is.popInteger();
			Level = is.popInteger();
			VipLevel = is.popInteger();
			PayNum = is.popInteger();
			CreateTime = is.popInteger();
			Channel = is.popString(MAX_CHANNEL_NAME_LEN);
			Uid = is.popString(MAX_UID_LEN);
			IsOnline = is.popInteger();
			LastLoginTime = is.popInteger();
			Money = is.popInteger();
			bindMoney = is.popInteger();
			Diamond = is.popInteger();
			bindDiamond = is.popInteger();
			BanStatus = is.popInteger();
			DiamondFUseTotal = is.popInteger();
			DiamondRUseTotal = is.popInteger();
			ClassType = is.popByte();
			TransfromLvl = is.popByte();
			BWType = is.popByte();
			Gender = is.popByte();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(RoleId);
			os.pushString(RoleName, MAX_ROLE_NAME_LEN);
			os.pushInteger(ServerId);
			os.pushInteger(Level);
			os.pushInteger(VipLevel);
			os.pushInteger(PayNum);
			os.pushInteger(CreateTime);
			os.pushString(Channel, MAX_CHANNEL_NAME_LEN);
			os.pushString(Uid, MAX_UID_LEN);
			os.pushInteger(IsOnline);
			os.pushInteger(LastLoginTime);
			os.pushInteger(Money);
			os.pushInteger(bindMoney);
			os.pushInteger(Diamond);
			os.pushInteger(bindDiamond);
			os.pushInteger(BanStatus);
			os.pushInteger(DiamondFUseTotal);
			os.pushInteger(DiamondRUseTotal);
			os.pushByte(ClassType);
			os.pushByte(TransfromLvl);
			os.pushByte(BWType);
			os.pushByte(Gender);
		}

		// 玩家RoleID
		public int RoleId;
		// 玩家名字
		public String RoleName = new String();
		// 服务器ID
		public int ServerId;
		// 玩家等级
		public int Level;
		// 玩家Vip等级
		public int VipLevel;
		// 总充值金额
		public int PayNum;
		// 角色创建时间
		public int CreateTime;
		// 渠道
		public String Channel = new String();
		// 渠道
		public String Uid = new String();
		// 是否在线
		public int IsOnline;
		// 最后登录时间
		public int LastLoginTime;
		// 角色当前金币数量
		public int Money;
		// 角色当前绑定金币数量
		public int bindMoney;
		// 角色当前钻石数量
		public int Diamond;
		// 角色当前绑定钻石数量
		public int bindDiamond;
		// 是否被封禁
		public int BanStatus;
		// 角色消耗总非绑定元宝数量
		public int DiamondFUseTotal;
		// 角色消耗总绑定元宝数量
		public int DiamondRUseTotal;
		// 职业
		public byte ClassType;
		// 转职等级
		public byte TransfromLvl;
		// 正邪
		public byte BWType;
		// 性别
		public byte Gender;
	}

	// 查询玩家roleID请求
	public static class DoInquiryRoleIdByNameReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_REQ;

		public DoInquiryRoleIdByNameReq() { }

		public DoInquiryRoleIdByNameReq(int Partition, String RoleName)
		{
			this.Partition = Partition;
			this.RoleName = RoleName;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleName = is.popString(MAX_ROLE_NAME_LEN);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushString(RoleName, MAX_ROLE_NAME_LEN);
		}

		// 服务器ID
		public int Partition;
		// 玩家名字
		public String RoleName = new String();
	}

	// 查询玩家roleID应答
	public static class DoInquiryRoleIdByNameRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_RSP;

		public DoInquiryRoleIdByNameRsp() { }

		public DoInquiryRoleIdByNameRsp(int RoleId)
		{
			this.RoleId = RoleId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			RoleId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(RoleId);
		}

		// 玩家roleID
		public int RoleId;
	}

	// 获取服务器列表请求
	public static class DoQueryServerInfoReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_QUERY_SERVER_INFO_REQ;

		public DoQueryServerInfoReq() { }

		public DoQueryServerInfoReq(int Partition)
		{
			this.Partition = Partition;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
		}

		// 服务器ID
		public int Partition;
	}

	// 获取服务器列表应答
	public static class DoQueryServerInfoRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_QUERY_SERVER_INFO_RSP;

		public DoQueryServerInfoRsp() { }

		public DoQueryServerInfoRsp(int OpenTime, int OnlineCount, int RoleTotalCreate)
		{
			this.OpenTime = OpenTime;
			this.OnlineCount = OnlineCount;
			this.RoleTotalCreate = RoleTotalCreate;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			OpenTime = is.popInteger();
			OnlineCount = is.popInteger();
			RoleTotalCreate = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(OpenTime);
			os.pushInteger(OnlineCount);
			os.pushInteger(RoleTotalCreate);
		}

		// 开服时间
		public int OpenTime;
		// 在线数量
		public int OnlineCount;
		// 在线数量
		public int RoleTotalCreate;
	}

	// 添加注册邮件请求
	public static class DoAddRegisterMailReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_REGISTER_MAIL_REQ;

		public DoAddRegisterMailReq() { }

		public DoAddRegisterMailReq(int Partition, String Title, String Body, int ChannelCount, 
		                            List<String> ChannelReq, int AttListCount, List<CommonItems> Awards)
		{
			this.Partition = Partition;
			this.Title = Title;
			this.Body = Body;
			this.ChannelCount = ChannelCount;
			this.ChannelReq = ChannelReq;
			this.AttListCount = AttListCount;
			this.Awards = Awards;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			Title = is.popString(MAX_MAILTITLE_LEN);
			Body = is.popString(MAX_MAILCONTENT_LEN);
			ChannelCount = is.popInteger();
			ChannelReq = is.popStringList();
			AttListCount = is.popInteger();
			Awards = is.popList(CommonItems.class, AttListCount);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushString(Title, MAX_MAILTITLE_LEN);
			os.pushString(Body, MAX_MAILCONTENT_LEN);
			os.pushInteger(ChannelCount);
			os.pushStringList(ChannelReq);
			os.pushInteger(AttListCount);
			os.pushList(Awards, AttListCount);
		}

		// 服务器ID
		public int Partition;
		// 标题
		public String Title = new String();
		// 内容
		public String Body = new String();
		// 可领取渠道数量(0-25)
		public int ChannelCount;
		// 可领取channel列表，格式2001;1002
		public List<String> ChannelReq = new ArrayList<String>();
		// 附件物品列表的最大数量(0-4)
		public int AttListCount;
		// 奖励列表,格式3001_1;3002_10
		public List<CommonItems> Awards = new ArrayList<CommonItems>();
	}

	// 添加注册邮件应答
	public static class DoAddRegisterMailRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_REGISTER_MAIL_RSP;

		public DoAddRegisterMailRsp() { }

		public DoAddRegisterMailRsp(int RemailId)
		{
			this.RemailId = RemailId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			RemailId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(RemailId);
		}

		// 邮件ID
		public int RemailId;
	}

	// 删除注册邮件请求
	public static class DoDelRegisterMailReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_REGISTER_MAIL_DEL_REQ;

		public DoDelRegisterMailReq() { }

		public DoDelRegisterMailReq(int Partition, int RemailId)
		{
			this.Partition = Partition;
			this.RemailId = RemailId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RemailId = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RemailId);
		}

		// 服务器ID
		public int Partition;
		// 邮件ID
		public int RemailId;
	}

	// 删除注册邮件应答
	public static class DoDelRegisterMailRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_REGISTER_MAIL_DEL_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 修改角色等级请求
	public static class DoChangeRoleLevelReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_CHANGE_ROLE_LEVEL_REQ;

		public DoChangeRoleLevelReq() { }

		public DoChangeRoleLevelReq(int Partition, int RoleId, int Level)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
			this.Level = Level;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
			Level = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
			os.pushInteger(Level);
		}

		// 服务器ID
		public int Partition;
		// 角色ID
		public int RoleId;
		// 等级
		public int Level;
	}

	// 修改角色等级应答
	public static class DoChangeRoleLevelRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_CHANGE_ROLE_LEVEL_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 修改角色VIP点数请求
	public static class DoChangeRoleVipPointReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_CHANGE_ROLE_VIP_POINT_REQ;

		public DoChangeRoleVipPointReq() { }

		public DoChangeRoleVipPointReq(int Partition, int RoleId, int Point)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
			this.Point = Point;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
			Point = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
			os.pushInteger(Point);
		}

		// 服务器ID
		public int Partition;
		// 角色ID
		public int RoleId;
		// 增量vip点
		public int Point;
	}

	// 修改角色VIP点数应答
	public static class DoChangeRoleVipPointRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_CHANGE_ROLE_VIP_POINT_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 修改角色VIP点数请求
	public static class DoAddRoleVipPointReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_ADD_ROLE_VIP_POINT_REQ;

		public DoAddRoleVipPointReq() { }

		public DoAddRoleVipPointReq(int Partition, int RoleId, int Point)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
			this.Point = Point;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
			Point = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
			os.pushInteger(Point);
		}

		// 服务器ID
		public int Partition;
		// 角色ID
		public int RoleId;
		// 增量vip点
		public int Point;
	}

	// 修改角色VIP点数应答
	public static class DoAddRoleVipPointRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_ADD_ROLE_VIP_POINT_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 增加角色充值请求
	public static class DoAddRoleGodPayReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_ADD_ROLE_GOD_PAY_REQ;

		public DoAddRoleGodPayReq() { }

		public DoAddRoleGodPayReq(int Partition, int RoleId, int PayLevel)
		{
			this.Partition = Partition;
			this.RoleId = RoleId;
			this.PayLevel = PayLevel;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			RoleId = is.popInteger();
			PayLevel = is.popInteger();
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushInteger(RoleId);
			os.pushInteger(PayLevel);
		}

		// 服务器ID
		public int Partition;
		// 角色ID
		public int RoleId;
		// 增量vip点
		public int PayLevel;
	}

	// 增加角色充值应答
	public static class DoAddRoleGodPayRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_ADD_ROLE_GOD_PAY_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	// 查询账户角色请求
	public static class DoQueryRolesReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_QUERY_ROLES_REQ;

		public DoQueryRolesReq() { }

		public DoQueryRolesReq(int Partition, String OpenId)
		{
			this.Partition = Partition;
			this.OpenId = OpenId;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			OpenId = is.popString(MAX_OPENID_LENGTH);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushString(OpenId, MAX_OPENID_LENGTH);
		}

		// 服务器ID
		public int Partition;
		// 账号ID
		public String OpenId = new String();
	}

	// 查询账户角色应答
	public static class DoQueryRolesRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_QUERY_ROLES_RSP;

		public DoQueryRolesRsp() { }

		public DoQueryRolesRsp(int AttListCount, List<YYBRoleInfo> Roles)
		{
			this.AttListCount = AttListCount;
			this.Roles = Roles;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			AttListCount = is.popInteger();
			Roles = is.popList(YYBRoleInfo.class, AttListCount);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(AttListCount);
			os.pushList(Roles, AttListCount);
		}

		// List长度
		public int AttListCount;
		// 聊天消息数组
		public List<YYBRoleInfo> Roles = new ArrayList<YYBRoleInfo>();
	}

	// 应用宝发送奖励请求
	public static class DoSendRoleGiftReq implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_SEND_ROLE_GIFT_REQ;

		public DoSendRoleGiftReq() { }

		public DoSendRoleGiftReq(int Partition, String OpenId, int RoleId, int ConditionType, 
		                         int ConditionValue, int AttListCount, List<CommonItems> Awards, String Action)
		{
			this.Partition = Partition;
			this.OpenId = OpenId;
			this.RoleId = RoleId;
			this.ConditionType = ConditionType;
			this.ConditionValue = ConditionValue;
			this.AttListCount = AttListCount;
			this.Awards = Awards;
			this.Action = Action;
		}

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
			Partition = is.popInteger();
			OpenId = is.popString(MAX_OPENID_LENGTH);
			RoleId = is.popInteger();
			ConditionType = is.popInteger();
			ConditionValue = is.popInteger();
			AttListCount = is.popInteger();
			Awards = is.popList(CommonItems.class, AttListCount);
			Action = is.popString(MAX_SEND_ACTION_LENGTH);
		}

		@Override
		public void encode(Stream.AOStream os)
		{
			os.pushInteger(Partition);
			os.pushString(OpenId, MAX_OPENID_LENGTH);
			os.pushInteger(RoleId);
			os.pushInteger(ConditionType);
			os.pushInteger(ConditionValue);
			os.pushInteger(AttListCount);
			os.pushList(Awards, AttListCount);
			os.pushString(Action, MAX_SEND_ACTION_LENGTH);
		}

		// 服务器ID
		public int Partition;
		// 账号ID
		public String OpenId = new String();
		// 角色ID
		public int RoleId;
		// 条件类型
		public int ConditionType;
		// 条件数值
		public int ConditionValue;
		// 奖励的最大数量(0-4)
		public int AttListCount;
		// 奖励列表,格式3001_1;3002_10
		public List<CommonItems> Awards = new ArrayList<CommonItems>();
		// 查询动作
		public String Action = new String();
	}

	// 应用宝发送奖励应答
	public static class DoSendRoleGiftRsp implements Stream.IStreamable
	{

		public static final int idipID = IDIP_DO_SEND_ROLE_GIFT_RSP;

		@Override
		public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
		{
		}

		@Override
		public void encode(Stream.AOStream os)
		{
		}

	}

	public static Stream.IStreamable decodePacket(int cmdID, byte[] bodyData)
	{
		Stream.BytesInputStream bais = new Stream.BytesInputStream(bodyData, 0, bodyData.length);
		Stream.AIStream is = new Stream.IStreamBE(bais);
		try
		{
			switch( cmdID )
			{
			case IDIP_DO_BAN_USR_REQ:
				{
					DoBanUsrReq obj = new DoBanUsrReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_BAN_USR_RSP:
				{
					DoBanUsrRsp obj = new DoBanUsrRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_UNBAN_USR_REQ:
				{
					DoUnBanUsrReq obj = new DoUnBanUsrReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_UNBAN_USR_RSP:
				{
					DoUnBanUsrRsp obj = new DoUnBanUsrRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_BAN_USR_CHAT_REQ:
				{
					DoBanUsrChatReq obj = new DoBanUsrChatReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_BAN_USR_CHAT_RSP:
				{
					DoBanUsrChatRsp obj = new DoBanUsrChatRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_UNBAN_USR_CHAT_REQ:
				{
					DoUnBanUsrChatReq obj = new DoUnBanUsrChatReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_UNBAN_USR_CHAT_RSP:
				{
					DoUnBanUsrChatRsp obj = new DoUnBanUsrChatRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_KICK_ONLINE_USR_REQ:
				{
					DoKickOnlineUsrReq obj = new DoKickOnlineUsrReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_KICK_ONLINE_USR_RSP:
				{
					DoKickOnlineUsrRsp obj = new DoKickOnlineUsrRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_PUSH_MAIL_ALL_REQ:
				{
					DoPushMailAllReq obj = new DoPushMailAllReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_PUSH_MAIL_ALL_RSP:
				{
					DoPushMailAllRsp obj = new DoPushMailAllRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_PUSH_MAIL_USR_REQ:
				{
					DoPushMailUsrReq obj = new DoPushMailUsrReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_PUSH_MAIL_USR_RSP:
				{
					DoPushMailUsrRsp obj = new DoPushMailUsrRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_GET_CHAT_RECORD_REQ:
				{
					DoGetChatRecordReq obj = new DoGetChatRecordReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_GET_CHAT_RECORD_RSP:
				{
					DoGetChatRecordRsp obj = new DoGetChatRecordRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_ADD_ROLL_NOTICE_REQ:
				{
					DoAddRollNoticeReq obj = new DoAddRollNoticeReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_ADD_ROLL_NOTICE_RSP:
				{
					DoAddRollNoticeRsp obj = new DoAddRollNoticeRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_DELETE_ROLL_NOTICE_REQ:
				{
					DoDeleteRollNoticeReq obj = new DoDeleteRollNoticeReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_DELETE_ROLL_NOTICE_RSP:
				{
					DoDeleteRollNoticeRsp obj = new DoDeleteRollNoticeRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_INQUIRY_ROLE_INFO_REQ:
				{
					DoInquiryRoleInfoReq obj = new DoInquiryRoleInfoReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_INQUIRY_ROLE_INFO_RSP:
				{
					DoInquiryRoleInfoRsp obj = new DoInquiryRoleInfoRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_REQ:
				{
					DoInquiryRoleIdByNameReq obj = new DoInquiryRoleIdByNameReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_RSP:
				{
					DoInquiryRoleIdByNameRsp obj = new DoInquiryRoleIdByNameRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_QUERY_SERVER_INFO_REQ:
				{
					DoQueryServerInfoReq obj = new DoQueryServerInfoReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_QUERY_SERVER_INFO_RSP:
				{
					DoQueryServerInfoRsp obj = new DoQueryServerInfoRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_REGISTER_MAIL_REQ:
				{
					DoAddRegisterMailReq obj = new DoAddRegisterMailReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_REGISTER_MAIL_RSP:
				{
					DoAddRegisterMailRsp obj = new DoAddRegisterMailRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_REGISTER_MAIL_DEL_REQ:
				{
					DoDelRegisterMailReq obj = new DoDelRegisterMailReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_REGISTER_MAIL_DEL_RSP:
				{
					DoDelRegisterMailRsp obj = new DoDelRegisterMailRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_CHANGE_ROLE_LEVEL_REQ:
				{
					DoChangeRoleLevelReq obj = new DoChangeRoleLevelReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_CHANGE_ROLE_LEVEL_RSP:
				{
					DoChangeRoleLevelRsp obj = new DoChangeRoleLevelRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_CHANGE_ROLE_VIP_POINT_REQ:
				{
					DoChangeRoleVipPointReq obj = new DoChangeRoleVipPointReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_CHANGE_ROLE_VIP_POINT_RSP:
				{
					DoChangeRoleVipPointRsp obj = new DoChangeRoleVipPointRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_ADD_ROLE_VIP_POINT_REQ:
				{
					DoAddRoleVipPointReq obj = new DoAddRoleVipPointReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_ADD_ROLE_VIP_POINT_RSP:
				{
					DoAddRoleVipPointRsp obj = new DoAddRoleVipPointRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_ADD_ROLE_GOD_PAY_REQ:
				{
					DoAddRoleGodPayReq obj = new DoAddRoleGodPayReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_ADD_ROLE_GOD_PAY_RSP:
				{
					DoAddRoleGodPayRsp obj = new DoAddRoleGodPayRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_QUERY_ROLES_REQ:
				{
					DoQueryRolesReq obj = new DoQueryRolesReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_QUERY_ROLES_RSP:
				{
					DoQueryRolesRsp obj = new DoQueryRolesRsp();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_SEND_ROLE_GIFT_REQ:
				{
					DoSendRoleGiftReq obj = new DoSendRoleGiftReq();
					obj.decode(is);
					return obj;
				}
			case IDIP_DO_SEND_ROLE_GIFT_RSP:
				{
					DoSendRoleGiftRsp obj = new DoSendRoleGiftRsp();
					obj.decode(is);
					return obj;
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
