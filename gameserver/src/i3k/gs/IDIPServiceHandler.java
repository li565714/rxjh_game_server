
package i3k.gs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import i3k.SBean;
import i3k.IDIP;
import i3k.gs.LoginManager.CommonRoleVisitor;
import i3k.util.GameTime;
import ket.util.Stream;


public class IDIPServiceHandler
{

	public IDIPServiceHandler(GameServer gs)
	{
		this.gs = gs;
	}
	
//	private static int getIDIPResult(int errcode)
//	{
//		int result = IDIP.IDIP_HEADER_RESULT_OTHER_ERROR;
//		switch (errcode)
//		{
//		case CommonRoleVisitor.ERR_NULL:
//			result = IDIP.IDIP_HEADER_RESULT_SERVER_BUSY;
//			break;
//		case CommonRoleVisitor.ERR_FAILED:
//			result = IDIP.IDIP_HEADER_RESULT_OTHER_ERROR;
//			break;
//		case CommonRoleVisitor.ERR_OK:
//			result = IDIP.IDIP_HEADER_RESULT_SUCCESS;
//			break;
//		}
//		return result;
//	}

	private static String getIDIPErrMsg(int errcode)
	{
		String msg = "not known error";
		switch (errcode)
		{
		case IDIP.IDIP_HEADER_RESULT_SUCCESS:
			msg = "success";
			break;
		case IDIP.IDIP_HEADER_RESULT_EMPTY_PACKET_SUCCESS:
		    msg = "can't find ";
		    break;
		case IDIP.IDIP_HEADER_RESULT_NETWORK_EXCEPTION:
			msg = "network exception";
			break;
		case IDIP.IDIP_HEADER_RESULT_TIMEOUT:
			msg = "time out";
			break;
		case IDIP.IDIP_HEADER_RESULT_DB_EXCEPTION:
			msg = "Database busy";
			break;
		case IDIP.IDIP_HEADER_RESULT_API_EXCEPTION:
			msg = "api invoke error";
			break;
		case IDIP.IDIP_HEADER_RESULT_SERVER_BUSY:
			msg = "server busy";
			break;
		case IDIP.IDIP_HEADER_RESULT_OTHER_ERROR:
			msg = "not known other error";
			break;
		default:
			break;
		}
		return msg;
	}
	
	//// begin request handler.
	public void handleIDIPRequest(TCPIDIPServer from, final int sessionid, final IDIPPacket packet)
	{
		IDIP.IdipHeader headerReq = packet.header;
		gs.getLogger().info("idip req, sessionid=" + sessionid + ", type=0x" + Integer.toHexString(headerReq.Cmdid) + ", size = " + headerReq.PacketLen);
		Stream.IStreamable reqstream = IDIP.decodePacket(headerReq.Cmdid, packet.body);
		if (reqstream == null)
		{
			gs.getLogger().warn("idip req, sessionid=" + sessionid + ", type=0x" + Integer.toHexString(headerReq.Cmdid) + ", size = " + headerReq.PacketLen + ", decode failed");
			return;
		}
		switch( headerReq.Cmdid )
		{
			case IDIP.IDIP_DO_BAN_USR_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoBanUsrReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_UNBAN_USR_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoUnBanUsrReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_BAN_USR_CHAT_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoBanUsrChatReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_UNBAN_USR_CHAT_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoUnBanUsrChatReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_KICK_ONLINE_USR_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoKickOnlineUsrReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_PUSH_MAIL_ALL_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoPushMailAllReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_PUSH_MAIL_USR_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoPushMailUsrReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_GET_CHAT_RECORD_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoGetChatRecordReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_ADD_ROLL_NOTICE_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoAddRollNoticeReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_DELETE_ROLL_NOTICE_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoDeleteRollNoticeReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_INQUIRY_ROLE_INFO_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoInquiryRoleInfoReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_INQUIRY_ROLE_ID_BY_NAME_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoInquiryRoleIdByNameReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_QUERY_SERVER_INFO_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoQueryServerInfoReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_REGISTER_MAIL_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoAddRegisterMailReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_REGISTER_MAIL_DEL_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoDelRegisterMailReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_CHANGE_ROLE_LEVEL_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoChangeRoleLevelReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_CHANGE_ROLE_VIP_POINT_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoChangeRoleVipPointReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_ADD_ROLE_GOD_PAY_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoAddRoleGodPayReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_QUERY_ROLES_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoQueryRolesReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_SEND_ROLE_GIFT_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoSendRoleGiftReq)reqstream);
				}
				break;
			case IDIP.IDIP_DO_ADD_ROLE_VIP_POINT_REQ:
				{
					onHandleIDIPReq(from, sessionid, headerReq, (IDIP.DoAddRoleVipPointReq)reqstream);
				}
				break;
			default:
				gs.getLogger().warn("idip req, type=" + Integer.toHexString(headerReq.Cmdid) + ", size = " + headerReq.PacketLen + ", can't find handler!");
				break;
		}
	}
	////end request handler.
	
	////begin idip handler.
	
	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoBanUsrReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		String errMsg = new String();
		if (errMsg.isEmpty())
		{
			if (req.Reason.length() >= IDIP.MAX_REASON_LEN)
				errMsg = "the reason max length is " + IDIP.MAX_REASON_LEN + "!";
			if (req.LeftTime <= 0)
				errMsg = "the ban user time should bigger than 0!";
		}
		if (!errMsg.isEmpty())
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);// error in header args
			IDIP.DoBanUsrRsp res = new IDIP.DoBanUsrRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrReq: result=" + headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId= " + req.RoleId + ")");
			return;
		}
		gs.getLoginManager().getUserNameByRoleId(req.RoleId, new LoginManager.GetUserNameByRoleIDCallback()
		{
			@Override
			public void onCallback(int roleId, String userName) {
				if(userName == null)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_USER_NOT_EXIST, "unable to find roleName! RoleID="+req.RoleId);
					IDIP.DoBanUsrRsp res = new IDIP.DoBanUsrRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
					return ;
				}
				gs.getLoginManager().banUser(userName, req.LeftTime, req.Reason, new LoginManager.ModifyRoleDataCallback() {
					@Override
					public void onCallback(int errcode) {
						//EndTime is a delt TIME
						if(errcode == IDIP.IDIP_HEADER_RESULT_SUCCESS && req.LeftTime > 0)
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "banUsr success! RoleID="+req.RoleId);
							IDIP.DoBanUsrRsp res = new IDIP.DoBanUsrRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
						}else
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "banUsr failed! RoleID="+req.RoleId);
							IDIP.DoBanUsrRsp res = new IDIP.DoBanUsrRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
						}
					}
				} );
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoUnBanUsrReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		gs.getLoginManager().getUserNameByRoleId(req.RoleId, new LoginManager.GetUserNameByRoleIDCallback()
		{
			@Override
			public void onCallback(int roleId, String userName) {
				if(userName == null)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_USER_NOT_EXIST, "unable to find roleName! RoleID="+req.RoleId);
					IDIP.DoUnBanUsrRsp res = new IDIP.DoUnBanUsrRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
					return ;
				}
				gs.getLoginManager().unbanUser(userName, new LoginManager.ModifyRoleDataCallback() {
					@Override
					public void onCallback(int errcode) {
						// TODO Auto-generated method stub
						if(errcode == IDIP.IDIP_HEADER_RESULT_SUCCESS )
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "unBan role success! RoleID="+req.RoleId);
							IDIP.DoUnBanUsrRsp res = new IDIP.DoUnBanUsrRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
							return ;
						}else
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "unBan role failed! RoleID="+req.RoleId);
							IDIP.DoUnBanUsrRsp res = new IDIP.DoUnBanUsrRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
							return ;
						}
					}
				});
			}
		});
		
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoBanUsrChatReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrChatReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrChatReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		String errMsg = new String();
		if (errMsg.isEmpty())
		{
			if (req.Reason.length() >= IDIP.MAX_REASON_LEN)
				errMsg = "the reason max length is " + IDIP.MAX_REASON_LEN + "!";
			if (req.LeftTime <= 0)
				errMsg = "the ban user time should bigger than 0!";
		}
		if (!errMsg.isEmpty())
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);// error in header args
			IDIP.DoBanUsrChatRsp res = new IDIP.DoBanUsrChatRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrChatReq: result=" + headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId= " + req.RoleId + ")");
			return;
		}
		gs.getLoginManager().getUserNameByRoleId(req.RoleId, new LoginManager.GetUserNameByRoleIDCallback()
		{
			@Override
			public void onCallback(int roleId, String userName) {
				if(userName == null)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_USER_NOT_EXIST, "unable to find roleName! RoleID="+req.RoleId);
					IDIP.DoBanUsrChatRsp res = new IDIP.DoBanUsrChatRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrChatReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
					return ;
				}
				gs.getLoginManager().banUserChat(userName, req.LeftTime, req.Reason, new LoginManager.ModifyRoleDataCallback() {
					@Override
					public void onCallback(int errcode) {
						if(errcode == IDIP.IDIP_HEADER_RESULT_SUCCESS )
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "ban usr chat success! RoleID="+req.RoleId);
							IDIP.DoBanUsrChatRsp res = new IDIP.DoBanUsrChatRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrChatReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
							return ;
						}else
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "ban usr chat failed! RoleID="+req.RoleId);
							IDIP.DoBanUsrChatRsp res = new IDIP.DoBanUsrChatRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoBanUsrChatReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
							return ;
						}
					}
				});
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoUnBanUsrChatReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrChatReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrChatReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		gs.getLoginManager().getUserNameByRoleId(req.RoleId, new LoginManager.GetUserNameByRoleIDCallback()
		{
			@Override
			public void onCallback(int roleId, String userName) {
				if(userName == null)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_USER_NOT_EXIST, "unable to find roleName! RoleID="+req.RoleId);
					IDIP.DoUnBanUsrChatRsp res = new IDIP.DoUnBanUsrChatRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrChatReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
					return ;
				}
				gs.getLoginManager().unbanUserChat(userName,  new LoginManager.ModifyRoleDataCallback() {
					@Override
					public void onCallback(int errcode) {
						if(errcode ==IDIP.IDIP_HEADER_RESULT_SUCCESS)
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "unban usr chat success! RoleID="+req.RoleId);
							IDIP.DoUnBanUsrChatRsp res = new IDIP.DoUnBanUsrChatRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrChatReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
							return ;
						}else
						{
							IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "unban usr chat failed! RoleID="+req.RoleId);
							IDIP.DoUnBanUsrChatRsp res = new IDIP.DoUnBanUsrChatRsp();
							idips.sendRes(sessionid, headerRes, res);
							gs.getLogger().info("idip sessionid=" + sessionid + ", DoUnBanUsrChatReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
							return ;
						}
					}
				});
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoKickOnlineUsrReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoKickOnlineUsrReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoKickOnlineUsrReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		gs.getLoginManager().getUserNameByRoleId(req.RoleId, new LoginManager.GetUserNameByRoleIDCallback()
		{
			@Override
			public void onCallback(int roleId, String userName) {
				if(userName == null)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_USER_NOT_EXIST, "unable to find roleName! RoleID="+req.RoleId);
					IDIP.DoKickOnlineUsrRsp res = new IDIP.DoKickOnlineUsrRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoKickOnlineUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
					return ;
				}
				gs.getLoginManager().kickOnlineRole(roleId);
				IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "kick online role success! RoleID="+req.RoleId);
				IDIP.DoKickOnlineUsrRsp res = new IDIP.DoKickOnlineUsrRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoKickOnlineUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId=" + req.RoleId + ")");
				return ;
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoPushMailAllReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailAllReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailAllReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		//check req values
		String errMsg = req.Awards.size() > IDIP.MAX_ATTLIST_NUM ? "the mail DummyGoods size is " + IDIP.MAX_ATTLIST_NUM : "" ;
		if(errMsg.isEmpty())
		{
			if(req.Title.isEmpty() || req.Body.isEmpty()) 
				errMsg = "the mail title or body is empty";
			if(req.Title.length() > IDIP.MAX_MAILTITLE_LEN) 
				errMsg = "the mail title max length is " + IDIP.MAX_MAILTITLE_LEN;
			if(req.Body.length() > IDIP.MAX_MAILCONTENT_LEN) 
				errMsg = "the mail context max length is " + IDIP.MAX_MAILCONTENT_LEN;
			if(req.LifeTime <= 0 )  
				errMsg = "the mail lifeTime should bigger than 0!";
			if(req.LevelMin < IDIP.MIN_LEVEL || req.LevelMin > IDIP.MAX_LEVEL || req.LevelMin > req.LevelMax) 
				errMsg = "the mail LevelReq is unvalid!";
			if(req.VipMin < IDIP.MIN_VIP_LEVEL || req.VipMin > IDIP.MAX_VIP_LEVEL || req.VipMin > req.VipMax) 
				errMsg = "the mail VipReq is unvalid!";
			for( IDIP.CommonItems d : req.Awards )
			{
				if(!GameData.getInstance().checkEntityIdValid(d.id))
				{
					errMsg = "itemid is invalid!";
					break;
				}
			}
		}
		if( ! errMsg.isEmpty() )
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);// error in header args
			IDIP.DoPushMailAllRsp res = new IDIP.DoPushMailAllRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailAllReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId= nullValue" + ")");
			return ;
		}
		
		List<SBean.DummyGoods> awards = new ArrayList<>();
		for( IDIP.CommonItems d : req.Awards )
		{
			awards.add(new SBean.DummyGoods(d.id, d.count));
		}
		gs.getLoginManager().sendWorldMail(req.LevelMin, req.LevelMax, req.VipMin, req.VipMax, req.ChannelReq.stream().collect(Collectors.toSet()), req.LifeTime, req.Title, req.Body, awards, new LoginManager.AddMailCallback() {
			
			@Override
			public void onCallback(int mailId) 
			{
				if (mailId > 0)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "push mail all success!");
					IDIP.DoPushMailAllRsp res = new IDIP.DoPushMailAllRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailAllReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (Title= " + req.Title + ")");
					return ;
				}
				else
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "push mail all failed!");
					IDIP.DoPushMailAllRsp res = new IDIP.DoPushMailAllRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailAllReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (Title= " + req.Title + ")");
					return ;
				}
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoPushMailUsrReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailUsrReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailUsrReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		//check req values
		String errMsg = req.Awards.size() > IDIP.MAX_ATTLIST_NUM ? "the mail DummyGoods size is " + IDIP.MAX_ATTLIST_NUM : "" ;
		if(errMsg.isEmpty())
		{
			if(req.Title.isEmpty() || req.Body.isEmpty()) 
				errMsg = "the mail title or body is empty";
			if(req.Title.length() > IDIP.MAX_MAILTITLE_LEN) 
				errMsg = "the mail title max length is " + IDIP.MAX_MAILTITLE_LEN;
			if(req.Body.length() > IDIP.MAX_MAILCONTENT_LEN) 
				errMsg = "the mail context max length is " + IDIP.MAX_MAILCONTENT_LEN;
			if(req.LifeTime <= 0 )  
				errMsg = "the mail lifeTime should bigger than 0!";
			if(req.RoleId <= 0)
				errMsg = "roleID should bigger than 0";
			for( IDIP.CommonItems d : req.Awards )
			{
				if(!GameData.getInstance().checkEntityIdValid(d.id))
				{
					errMsg = "itemid is invalid!";
					break;
				}
			}
		}
		if( ! errMsg.isEmpty() )
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);// error in header args
			IDIP.DoPushMailUsrRsp res = new IDIP.DoPushMailUsrRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId= nullValue" + ")");
			return ;
		}
		List<SBean.DummyGoods> awards = new ArrayList<>();
		for( IDIP.CommonItems d : req.Awards )
		{
			awards.add(new SBean.DummyGoods(d.id, d.count));
		}
		gs.getLoginManager().sendGMMail(req.RoleId, req.LifeTime, req.Title, req.Body, awards, new LoginManager.AddMailCallback() {
			
			@Override
			public void onCallback(int mailId) {
				if (mailId > 0)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "push mail to user success!");
					IDIP.DoPushMailUsrRsp res = new IDIP.DoPushMailUsrRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId= " + req.RoleId + ", Title= " + req.Title + ")");
					return ;
				}
				else
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "push mail to user failed!");
					IDIP.DoPushMailUsrRsp res = new IDIP.DoPushMailUsrRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoPushMailUsrReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleId= " + req.RoleId + ", Title= " + req.Title + ")");
					return ;
				}
			}
		});
	}


	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoGetChatRecordReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoGetChatRecordReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoGetChatRecordReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		Collection<SBean.MessageInfo> messages = gs.getLoginManager().getRecentWorldMsgs();
		Collection<IDIP.Message> chatRecords = new ArrayList<>();
		for(SBean.MessageInfo m : messages)
		{
			chatRecords.add(new IDIP.Message(m.srcId, m.srcName, GameTime.getGMTTimeFromServerTime(m.time), m.content.msg));
		}
		IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "get chat records success!");
		IDIP.DoGetChatRecordRsp res = new IDIP.DoGetChatRecordRsp();
		res.AttListCount = chatRecords.size();
		res.Messages.addAll(chatRecords);
		idips.sendRes(sessionid, headerRes, res);
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoGetChatRecordReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
		return ;
	}
	
	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoAddRollNoticeReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRollNoticeReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRollNoticeReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		String errMsg = new String();
		if(errMsg.isEmpty())
		{
			if(req.Body.length() > IDIP.MAX_MOVING_NOTICE_LEN) 
				errMsg = "the roll notice max length is " + IDIP.MAX_MOVING_NOTICE_LEN;
			if(req.LeftTime <= 0)  
				errMsg = "req.LifeTime <= 0, send roll notice failed!";
		}
		if(!errMsg.isEmpty()){
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);
			IDIP.DoAddRollNoticeRsp res = new IDIP.DoAddRollNoticeRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRollNoticeReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
			return ;
		}
		gs.getLoginManager().sendRollNotice(GameTime.getServerTimeFromGMTTime(req.startTime), req.LeftTime, req.Cycle, req.Body, new LoginManager.SendRollNoticeCallback(){
			
			@Override
			public void onCallback(int noticeId) {
				if(noticeId <= 0)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "send roll notice failed!");
					IDIP.DoAddRollNoticeRsp res = new IDIP.DoAddRollNoticeRsp();
					idips.sendRes(sessionid, headerRes, res);
					res.NoticeId = noticeId;
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRollNoticeReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
					return ;
				}
				IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "send roll notice success!");
				IDIP.DoAddRollNoticeRsp res = new IDIP.DoAddRollNoticeRsp();
				res.NoticeId = noticeId;
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRollNoticeReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
				return ;
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoDeleteRollNoticeReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoDeleteRollNoticeReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoDeleteRollNoticeReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		if(req.NoticeId <= 0)
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, "req.NoticeId <= 0, delete roll notice failed!");
			IDIP.DoDeleteRollNoticeRsp res = new IDIP.DoDeleteRollNoticeRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoDeleteRollNoticeReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
			return ;
		}
		gs.getLoginManager().cancelRollNotice(req.NoticeId, new LoginManager.CancelRollNoticeCallback() {
			
			@Override
			public void onCallback(boolean ok) {
				if(!ok){
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "delete roll notice failed!");
					IDIP.DoDeleteRollNoticeRsp res = new IDIP.DoDeleteRollNoticeRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoDeleteRollNoticeReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
					return ;
				}
				IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "delete roll notice success!");
				IDIP.DoDeleteRollNoticeRsp res = new IDIP.DoDeleteRollNoticeRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoDeleteRollNoticeReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
				return ;
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoInquiryRoleInfoReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleInfoReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleInfoReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		if(req.RoleId <= 0)
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, "req.roleId <= 0, inquiry role info failed!");
			IDIP.DoInquiryRoleInfoRsp res = new IDIP.DoInquiryRoleInfoRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleInfoReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
			return ;
		}
		gs.getLoginManager().queryRoleInfo(req.RoleId, new LoginManager.QueryRoleInfoCallback() {
			
			@Override
			public void onCallback(IDIP.DoInquiryRoleInfoRsp rsp) {
				if(rsp == null)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "get role failed, inquiry role info failed!");
					IDIP.DoInquiryRoleInfoRsp res = new IDIP.DoInquiryRoleInfoRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleInfoReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
					return ;
				}
				IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "inquiry role info success!");
				IDIP.DoInquiryRoleInfoRsp res = new IDIP.DoInquiryRoleInfoRsp();
				res = rsp;
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleInfoReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
				return ;
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoInquiryRoleIdByNameReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleIdByNameReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleIdByNameReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		String errMsg = new String();
		if(errMsg.isEmpty())
		{
			if(req.RoleName.length() > IDIP.MAX_ROLE_NAME_LEN) 
				errMsg = "the rolename max length is " + IDIP.MAX_ROLE_NAME_LEN;
		}
		if( ! errMsg.isEmpty() )
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);// error in header args
			IDIP.DoInquiryRoleIdByNameRsp res = new IDIP.DoInquiryRoleIdByNameRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleIdByNameReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + " (RoleName= " + req.RoleName + ")");
			return ;
		}
		if(req.RoleName.isEmpty())
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, "req.RoleName is null, inquiry role id failed!");
			IDIP.DoInquiryRoleIdByNameRsp res = new IDIP.DoInquiryRoleIdByNameRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleIdByNameReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
			return ;
		}
		gs.getLoginManager().getRoleIdByRoleName(req.RoleName, new LoginManager.GetRoleIdByRoleNameCallback() {
			
			@Override
			public void onCallback(String roleName, Integer roleId) {
				if(roleName == null || roleId == null)
				{
					IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "inquiry role id failed!");
					IDIP.DoInquiryRoleIdByNameRsp res = new IDIP.DoInquiryRoleIdByNameRsp();
					idips.sendRes(sessionid, headerRes, res);
					gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleIdByNameReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
					return ;
				}
				IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "inquiry role id success!");
				IDIP.DoInquiryRoleIdByNameRsp res = new IDIP.DoInquiryRoleIdByNameRsp();
				res.RoleId = roleId;
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoInquiryRoleIdByNameReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg  + " (RoleId= " + roleId + ")");
				return ;
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoQueryServerInfoReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoGetServerListReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoGetServerListReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		List<Integer> list = gs.getLoginManager().queryServerInfo();
		IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "get server info success!");
		IDIP.DoQueryServerInfoRsp res = new IDIP.DoQueryServerInfoRsp();
		res.OpenTime = list.get(0);
		res.OnlineCount = list.get(1);
		res.RoleTotalCreate = list.get(2);
		idips.sendRes(sessionid, headerRes, res);
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoGetServerListReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg );
		return ;
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoAddRegisterMailReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRegisterMailReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRegisterMailReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		String errMsg = req.Awards.size() > IDIP.MAX_ATTLIST_NUM ? "the mail DummyGoods size is " + IDIP.MAX_ATTLIST_NUM : "" ;
		if(errMsg.isEmpty())
		{
			if(req.Title.isEmpty() || req.Body.isEmpty()) 
				errMsg = "the mail title or body is empty";
			if(req.Title.length() > IDIP.MAX_MAILTITLE_LEN) 
				errMsg = "the mail title max length is " + IDIP.MAX_MAILTITLE_LEN;
			if(req.Body.length() > IDIP.MAX_MAILCONTENT_LEN) 
				errMsg = "the mail context max length is " + IDIP.MAX_MAILCONTENT_LEN;
			for( IDIP.CommonItems d : req.Awards )
			{
				if(!GameData.getInstance().checkEntityIdValid(d.id))
				{
					errMsg = "itemid is invalid!";
					break;
				}
			}
		}
		if( ! errMsg.isEmpty() )
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);// error in header args
			IDIP.DoAddRegisterMailRsp res = new IDIP.DoAddRegisterMailRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRegisterMailReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			return ;
		}
		List<SBean.DummyGoods> awards = new ArrayList<>();
		for( IDIP.CommonItems d : req.Awards )
		{
			awards.add(new SBean.DummyGoods(d.id, d.count));
		}
		gs.getLoginManager().addNewRoleSysMail(req.Title, req.Body, req.ChannelReq.stream().collect(Collectors.toSet()), awards, new LoginManager.AddMailCallback()
		{
			
			@Override
			public void onCallback(int mailId)
			{
				IDIP.IdipHeader headerRes;
				if(mailId <= 0)
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "Add register mail fail !");
				}
				else 
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				}
				IDIP.DoAddRegisterMailRsp res = new IDIP.DoAddRegisterMailRsp(mailId);
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRegisterMailReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoDelRegisterMailReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoDelRegisterMailReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoDelRegisterMailReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		if(req.RemailId <= 0)
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "Unvalid mail id !");// error in header args
			IDIP.DoDelRegisterMailRsp res = new IDIP.DoDelRegisterMailRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoDelRegisterMailReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			return ;
		}
		gs.getLoginManager().delNewRoleSysMail(req.RemailId, new LoginManager.DelMailCallback()
		{
			
			@Override
			public void onCallback(boolean ok)
			{
				IDIP.IdipHeader headerRes;
				if(ok)
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				}
				else 
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "Delete register mail fail !");
				}
				IDIP.DoDelRegisterMailRsp res = new IDIP.DoDelRegisterMailRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoDelRegisterMailReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoChangeRoleLevelReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleLevelReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleLevelReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		if (req.Level <= 0 || req.Level > GameData.getInstance().getRoleLevelLimit())
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, "Role level invalid, must between " + 1 + " and " + GameData.getInstance().getRoleLevelLimit());// error in header args
			IDIP.DoChangeRoleLevelRsp res = new IDIP.DoChangeRoleLevelRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleLevelReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			return ;
		}
		gs.getLoginManager().changeRoleInfo(req.RoleId , req.Level, GameData.IDIP_CHANGE_ROLE_LEVEL, new LoginManager.ChangeRoleInfoCallback()
		{
			
			@Override
			public void onCallback(boolean ok)
			{
				IDIP.IdipHeader headerRes;
				if(ok)
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				}
				else 
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "Change role level fail !");
				}
				IDIP.DoChangeRoleLevelRsp res = new IDIP.DoChangeRoleLevelRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleLevelReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoChangeRoleVipPointReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleVipPointReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleVipPointReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		
		if (req.Point < 0 || req.Point > GameData.GMVIPPOINT_MAX_NUM)
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, "Role vip point invalid, must between " + 0 + " and " + GameData.GMVIPPOINT_MAX_NUM);// error in header args
			IDIP.DoChangeRoleLevelRsp res = new IDIP.DoChangeRoleLevelRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleVipPointReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			return ;
		}
		gs.getLoginManager().changeRoleInfo(req.RoleId , req.Point, GameData.IDIP_CHANGE_ROLE_VIP_POINT, new LoginManager.ChangeRoleInfoCallback()
		{
			
			@Override
			public void onCallback(boolean ok)
			{
				IDIP.IdipHeader headerRes;
				if(ok)
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				}
				else 
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "Change role vip point fail !");
				}
				IDIP.DoChangeRoleVipPointRsp res = new IDIP.DoChangeRoleVipPointRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoChangeRoleVipPointReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoAddRoleGodPayReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRoleGodPayReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRoleGodPayReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		gs.getLoginManager().changeRoleInfo(req.RoleId , req.PayLevel, GameData.IDIP_ADD_ROLE_PAY, new LoginManager.ChangeRoleInfoCallback()
		{
			
			@Override
			public void onCallback(boolean ok)
			{
				IDIP.IdipHeader headerRes;
				if(ok)
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				}
				else 
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "Add role god pay fail !");
				}
				IDIP.DoAddRoleGodPayRsp res = new IDIP.DoAddRoleGodPayRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRoleGodPayRsp: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoQueryRolesReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoQueryRolesReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoQueryRolesReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		gs.getLoginManager().getRolesByOpenId(GameData.YYB_CHANNEL, req.OpenId, new LoginManager.GetRolesByOpenIdCallback()
		{
			
			@Override
			public void onCallback(Map<Integer, String> roles)
			{
				IDIP.IdipHeader headerRes;
				IDIP.DoQueryRolesRsp res = new IDIP.DoQueryRolesRsp();
				headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				res.AttListCount = roles.size();
				res.Roles = new ArrayList<>();
				for (Entry<Integer, String> role:roles.entrySet())
				{
					res.Roles.add(new IDIP.YYBRoleInfo(role.getKey(), role.getValue()));
				}
				res.Roles.sort((a, b) -> a.roleId - b.roleId);
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoQueryRolesRsp: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg + ",RoleSize=" + res.AttListCount);
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoSendRoleGiftReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoSendRoleGiftReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoSendRoleGiftReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		String errMsg = req.Awards.size() > IDIP.MAX_ATTLIST_NUM ? "item size invalid!" : "" ;
		if(errMsg.isEmpty())
		{
			for( IDIP.CommonItems d : req.Awards )
			{
				if(!GameData.getInstance().checkEntityIdValid(d.id))
				{
					errMsg = "item is invaild!";
					break;
				}
			}
		}
		if( ! errMsg.isEmpty() )
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, errMsg);// error in header args
			IDIP.DoAddRegisterMailRsp res = new IDIP.DoAddRegisterMailRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRegisterMailReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			return ;
		}
		List<SBean.DummyGoods> awards = new ArrayList<>();
		for( IDIP.CommonItems d : req.Awards )
		{
			awards.add(new SBean.DummyGoods(d.id, d.count));
		}
		gs.getLoginManager().checkAndSendGiftToRole(req.RoleId , req.OpenId, req.ConditionType, req.ConditionValue, awards, req.Action, new LoginManager.CheckAndSendGiftCallback()
		{
			
			@Override
			public void onCallback(int ok)
			{
				IDIP.IdipHeader headerRes;
				if(ok == IDIP.IDIP_HEADER_RESULT_SUCCESS)
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				}
				else 
				{
					headerRes = idips.getResHeader(headerReq, ok, "");
				}
				IDIP.DoSendRoleGiftRsp res = new IDIP.DoSendRoleGiftRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoSendRoleGiftRsp: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			}
		});
	}

	private void onHandleIDIPReq(final TCPIDIPServer idips, final int sessionid, final IDIP.IdipHeader headerReq, final IDIP.DoAddRoleVipPointReq req)
	{
		gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRoleVipPointReq: Partition=" + req.Partition);
		if (GameData.getAreaIdFromGSId(req.Partition) != GameData.getAreaIdFromGSId(gs.getConfig().id) || !gs.getConfig().zones.contains(GameData.getRawZoneIdFromGSId(req.Partition)))
		{
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRoleVipPointReq: discard packet for id is not match(gs Partition=" + gs.getConfig().id + ")");
			return;
		}
		if (req.Point < 0 || req.Point > GameData.GMVIPPOINT_MAX_NUM)
		{
			IDIP.IdipHeader headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_API_EXCEPTION, "Role vip point invalid, must between " + 0 + " and " + GameData.GMVIPPOINT_MAX_NUM);// error in header args
			IDIP.DoChangeRoleLevelRsp res = new IDIP.DoChangeRoleLevelRsp();
			idips.sendRes(sessionid, headerRes, res);
			gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRoleVipPointReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			return ;
		}
		gs.getLoginManager().changeRoleInfo(req.RoleId , req.Point, GameData.IDIP_ADD_ROLE_VIP_POINT, new LoginManager.ChangeRoleInfoCallback()
		{
			
			@Override
			public void onCallback(boolean ok)
			{
				IDIP.IdipHeader headerRes;
				if(ok)
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_SUCCESS, "");
				}
				else 
				{
					headerRes = idips.getResHeader(headerReq, IDIP.IDIP_HEADER_RESULT_OTHER_ERROR, "Add role vip point fail !");
				}
				IDIP.DoAddRoleVipPointRsp res = new IDIP.DoAddRoleVipPointRsp();
				idips.sendRes(sessionid, headerRes, res);
				gs.getLogger().info("idip sessionid=" + sessionid + ", DoAddRoleVipPointReq: result="+ headerRes.Result + ",RetErrMsg=" + headerRes.RetErrMsg);
			}
		});
	}

	////end idip handler.
	
	private GameServer gs;
}
