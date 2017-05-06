// modified by ket.kio.RPCGen at Sat May 06 16:21:15 CST 2017.



package i3k.gs;

import java.util.ArrayList;
import java.util.TreeSet;

import ket.util.SStream;
import i3k.SBean;
import i3k.TLog;
import i3k.SBean.DBUserSurvey;
import i3k.gs.Role.RpcRes;

public class StringChannelHandler
{
	public StringChannelHandler(GameServer gs)
	{
		this.gs = gs;
	}

	//// begin handlers.
	public void onRecv_client_ping(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.client_ping packet = (SBean.client_ping) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.server_echo(packet.stamp + 1));
	}

	public void onRecv_keep_alive(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.keep_alive packet = (SBean.keep_alive) ipacket;
		// TODO
	}

	public void onRecv_user_login_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.user_login_req packet = (SBean.user_login_req) ipacket;
		//gs.getRPCManager().onUserLogin(sessionid, GameData.ensureNonull(packet.loginInfo), packet.roleId, GameData.ensureNonull(packet.roleName), packet.gender, 1, 1, packet.classType);
		if ((packet.loginInfo.arg.loginType & SBean.UserLoginParam.eLoginGod) > 0)
		{
			packet.loginInfo.system.deviceID = "IMM76D";
			packet.loginInfo.system.macAddr = "B8:97:5A:5F:81:09";
			packet.loginInfo.system.systemSoftware = "4.0.4";
			packet.loginInfo.system.systemHardware = "MX040";
			packet.loginInfo.system.cpuHardware = "armeabi-v7a";
			packet.loginInfo.system.density = 2.0f;
		}
		gs.getRPCManager().onUserLogin(sessionid, packet.gsId, GameData.ensureNonull(packet.openId), GameData.ensureNonull(packet.channel), GameData.ensureNonull(packet.loginInfo), packet.roleId, packet.createParam != null ? GameData.ensureNonull(packet.createParam) : packet.createParam);
	}

	public void onRecv_role_logout_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_logout_req packet = (SBean.role_logout_req)ipacket;
		gs.getRPCManager().onRoleLogout(sessionid, role);
	}

	public void onRecv_query_loginqueue_pos(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_loginqueue_pos packet = (SBean.query_loginqueue_pos)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.role_loginqueue_pos(gs.getLoginManager().getLoginQueue().queryPos(sessionid)));
	}

	public void onRecv_cancel_loginqueue(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cancel_loginqueue packet = (SBean.cancel_loginqueue)ipacket;
		gs.getLoginManager().getLoginQueue().onReceiveCancelQueue(sessionid);
	}

	public void onRecv_role_sync_map(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_sync_map packet = (SBean.role_sync_map)ipacket;
		role.roleSyncMap();
	}

	public void onRecv_msg_send_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.msg_send_req packet = (SBean.msg_send_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.msg_send_res(role.sendMsg(packet.type, packet.id, GameData.ensureNonull(packet.msg), GameData.ensureNonull(packet.gsName))));
	}

	public void onRecv_master_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_apply_req packet = (SBean.master_apply_req)ipacket;
		role.masterApplyReq(sessionid, packet.targetRoleID);
	}

	public void onRecv_master_offer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_offer_req packet = (SBean.master_offer_req)ipacket;
		role.masterOfferReq(sessionid, packet.targetRoleID);
	}

	public void onRecv_master_accept_offer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_accept_offer_req packet = (SBean.master_accept_offer_req)ipacket;
		role.masterAcceptOffer(sessionid, packet.targetRoleID);
	}

	public void onRecv_master_accept_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_accept_apply_req packet = (SBean.master_accept_apply_req)ipacket;
		role.masterAcceptApply(sessionid, packet);
	}

	public void onRecv_master_betray_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_betray_req packet = (SBean.master_betray_req)ipacket;
		role.masterActiveBetray(sessionid);
	}

	public void onRecv_master_graduate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_graduate_req packet = (SBean.master_graduate_req)ipacket;
		role.masterActiveReqGraduate(sessionid);
	}

	public void onRecv_master_agree_graduate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_agree_graduate_req packet = (SBean.master_agree_graduate_req)ipacket;
		role.masterActiveAgreeGraduate(sessionid, packet);
	}

	public void onRecv_master_dismiss_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_dismiss_req packet = (SBean.master_dismiss_req)ipacket;
		role.masterActiveDismiss(sessionid, packet.targetRoleID);
	}

	public void onRecv_master_get_announce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_get_announce_req packet = (SBean.master_get_announce_req)ipacket;
		String content = gs.getMasterManager().getAnnounce(role.id);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.master_get_announce_res(
				content != null ? GameData.PROTOCOL_OP_MASTER_OK : GameData.PROTOCOL_OP_MASTER_FAIL
						, content
				));
	}

	public void onRecv_master_set_announce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_set_announce_req packet = (SBean.master_set_announce_req)ipacket;
		int retCode = role.masterCanAcceptApply();
		if( retCode == GameData.PROTOCOL_OP_MASTER_OK )
		{
			if( ! gs.getMasterManager().setAnnounce(role.id, packet.content) )
				retCode = GameData.PROTOCOL_OP_MASTER_INVALID_ANNOUNCE;
		}
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.master_set_announce_res(
				retCode, packet.content));
	}

	public void onRecv_master_del_announce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_del_announce_req packet = (SBean.master_del_announce_req)ipacket;
		boolean bOK = gs.getMasterManager().removeAnnounce(role.id);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.master_del_announce_res(
				bOK ? GameData.PROTOCOL_OP_MASTER_OK : GameData.PROTOCOL_OP_MASTER_FAIL 
				));
	}

	public void onRecv_master_info_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_info_req packet = (SBean.master_info_req)ipacket;
		role.masterReqMasterInfo(sessionid);
	}

	public void onRecv_master_msg_list_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_msg_list_req packet = (SBean.master_msg_list_req)ipacket;
		role.masterListMsg(sessionid);
	}

	public void onRecv_master_remove_betray_msg_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_remove_betray_msg_req packet = (SBean.master_remove_betray_msg_req)ipacket;
		role.masterRemoveBetrayMsg(packet.roleID);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.master_remove_betray_msg_res(
				GameData.PROTOCOL_OP_MASTER_OK, packet.roleID));
	}

	public void onRecv_master_list_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_list_req packet = (SBean.master_list_req)ipacket;
		gs.getMasterManager().listMaster(sessionid, packet.lastStartIndex);
	}

	public void onRecv_master_list_apprentice_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_list_apprentice_req packet = (SBean.master_list_apprentice_req)ipacket;
		role.masterListApprentices(sessionid);
	}

	public void onRecv_master_tasks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_tasks_req packet = (SBean.master_tasks_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.master_tasks_res(
				GameData.PROTOCOL_OP_MASTER_OK
				, role.masterGetTasks()));		
	}

	public void onRecv_master_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_shopsync_req packet = (SBean.master_shopsync_req)ipacket;
		role.syncMasterShopInfo();
	}

	public void onRecv_master_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_shoprefresh_req packet = (SBean.master_shoprefresh_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.master_shoprefresh_res(role.userRefreshMasterShop(packet.times, packet.isSecondType)));
	}

	public void onRecv_master_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_shopbuy_req packet = (SBean.master_shopbuy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.master_shopbuy_res(role.buyMasterShopGoogs(packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_role_enter_map(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_enter_map packet = (SBean.role_enter_map) ipacket;
		role.clientEnterMap();
	}

	public void onRecv_waypoint_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.waypoint_enter_req packet = (SBean.waypoint_enter_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.waypoint_enter_res(role.enterWayPoint(packet.wid, packet.line) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_wrongpos_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.wrongpos_leave_req packet = (SBean.wrongpos_leave_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.wrongpos_leave_res(role.tryLeaveWrongPos() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_worldline_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.worldline_sync_req packet = (SBean.worldline_sync_req)ipacket;
		role.syncWorldLine();
	}

	public void onRecv_worldline_change_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.worldline_change_req packet = (SBean.worldline_change_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.worldline_change_res(role.changeWorldLine(packet.line)));
	}

	public void onRecv_query_rolebrief_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_rolebrief_req packet = (SBean.query_rolebrief_req) ipacket;
		role.queryRoleBrief(packet.rid);
	}

	public void onRecv_query_robot_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_robot_req packet = (SBean.query_robot_req)ipacket;
		role.queryRobotBrief(packet.rid, packet.rank);
	}

	public void onRecv_query_rolefeature_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_rolefeature_req packet = (SBean.query_rolefeature_req)ipacket;
		role.queryRoleFeature(packet.rid);
	}

	public void onRecv_query_petoverviews_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_petoverviews_req packet = (SBean.query_petoverviews_req)ipacket;
		role.queryPetOverviews(packet.rid);
	}

	public void onRecv_query_weaponoverviews_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_weaponoverviews_req packet = (SBean.query_weaponoverviews_req)ipacket;
		role.queryWeaponOverviews(packet.rid);
	}

	public void onRecv_role_revive_insitu_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_revive_insitu_req packet = (SBean.role_revive_insitu_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.role_revive_insitu_res(role.reviveInSitu(packet.useStone > 0) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_role_revive_other_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_revive_other_req packet = (SBean.role_revive_other_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.role_revive_other_res(role.reviveOther() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_role_revive_safe_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_revive_safe_req packet = (SBean.role_revive_safe_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.role_revive_safe_res(role.reviveSafe() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_role_transform_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_transform_req packet = (SBean.role_transform_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.role_transform_res(role.transform(packet.tlvl, packet.bwType) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_checkin_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.checkin_sync_req packet = (SBean.checkin_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.checkin_sync_res(role.syncCheckInInfo()));
	}

	public void onRecv_checkin_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.checkin_take_req packet = (SBean.checkin_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.checkin_take_res(role.takeCheckInRewards() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mall_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mall_sync_req packet = (SBean.mall_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mall_sync_res(role.syncMallInfo()));
	}

	public void onRecv_mall_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mall_buy_req packet = (SBean.mall_buy_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mall_buy_res(role.buyMallGoods(packet.effectiveTime, packet.id, packet.gid, packet.count, packet.free != 0, packet.price)));
	}

	public void onRecv_benefit_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.benefit_sync_req packet = (SBean.benefit_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.benefit_sync_res(role.testCheckIn() ? 1 : 0, role.testDailyOnlineGift() ? 1 : 0, role.testDailyVitReward() ? 1 : 0, role.syncCommonActivityInfo()));
	}

	public void onRecv_payactivity_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.payactivity_sync_req packet = (SBean.payactivity_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.payactivity_sync_res(role.syncPayActivityInfo()));
	}

	public void onRecv_firstpaygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.firstpaygift_sync_req packet = (SBean.firstpaygift_sync_req)ipacket;
		role.syncFirstPayGiftInfo(packet.id);
	}

	public void onRecv_firstpaygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.firstpaygift_take_req packet = (SBean.firstpaygift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.firstpaygift_take_res(role.takeFirstPayGiftReward(packet.effectiveTime, packet.id)));
	}


	public void onRecv_dailypaygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dailypaygift_sync_req packet = (SBean.dailypaygift_sync_req)ipacket;
		role.syncDailyPayGiftInfo(packet.id);
	}

	public void onRecv_dailypaygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dailypaygift_take_req packet = (SBean.dailypaygift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.dailypaygift_take_res(role.takeDailyPayGiftReward(packet.effectiveTime, packet.id)));
	}

	public void onRecv_lastpaygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lastpaygift_sync_req packet = (SBean.lastpaygift_sync_req)ipacket;
		role.syncLastPayGiftInfo(packet.id);
	}

	public void onRecv_lastpaygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lastpaygift_take_req packet = (SBean.lastpaygift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.lastpaygift_take_res(role.takeLastPayGiftReward(packet.effectiveTime, packet.id, packet.seq)));
	}

	public void onRecv_activitychallengegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitychallengegift_sync_req packet = (SBean.activitychallengegift_sync_req)ipacket;
		role.syncActivityChallengeGiftInfo(packet.id);
	}

	public void onRecv_activitychallengegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitychallengegift_take_req packet = (SBean.activitychallengegift_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.activitychallengegift_take_res(role.takeActivityChallengeGiftReward(packet.effectiveTime, packet.id, packet.activityId, packet.times)));
	}

	public void onRecv_upgradepurchase_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradepurchase_sync_req packet = (SBean.upgradepurchase_sync_req)ipacket;
		role.syncUpgradePurchaseInfo(packet.id);
	}

	public void onRecv_upgradepurchase_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradepurchase_buy_req packet = (SBean.upgradepurchase_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.upgradepurchase_buy_res(role.buyUpgradePurchaseGoods(packet.effectiveTime, packet.id)));
	}

	public void onRecv_paygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.paygift_sync_req packet = (SBean.paygift_sync_req) ipacket;
		role.syncPayGiftInfo(packet.id);
	}

	public void onRecv_paygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.paygift_take_req packet = (SBean.paygift_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.paygift_take_res(role.takePayGiftReward(packet.effectiveTime, packet.id, packet.payLevel)));
	}

	public void onRecv_consumegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.consumegift_sync_req packet = (SBean.consumegift_sync_req) ipacket;
		role.syncConsumeGiftInfo(packet.id);
	}

	public void onRecv_consumegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.consumegift_take_req packet = (SBean.consumegift_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.consumegift_take_res(role.takeConsumeGiftReward(packet.effectiveTime, packet.id, packet.consumeLevel)));
	}

	public void onRecv_upgradegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradegift_sync_req packet = (SBean.upgradegift_sync_req)ipacket;
		role.syncUpgradeGiftInfo(packet.id);
	}

	public void onRecv_upgradegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradegift_take_req packet = (SBean.upgradegift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.upgradegift_take_res(role.takeUpgradeGiftReward(packet.effectiveTime, packet.id, packet.level)));
	}

	public void onRecv_investmentfund_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.investmentfund_sync_req packet = (SBean.investmentfund_sync_req)ipacket;
		role.syncInvestmentFundInfo(packet.id);
	}

	public void onRecv_investmentfund_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.investmentfund_buy_req packet = (SBean.investmentfund_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.investmentfund_buy_res(role.buyInvestmentFund(packet.effectiveTime, packet.id)));
	}

	public void onRecv_investmentfund_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.investmentfund_take_req packet = (SBean.investmentfund_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.investmentfund_take_res(role.takeInvestmentFundReward(packet.effectiveTime, packet.id, packet.day)));
	}

	public void onRecv_growthfund_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.growthfund_sync_req packet = (SBean.growthfund_sync_req)ipacket;
		role.syncGrowthFundInfo(packet.id);
	}

	public void onRecv_growthfund_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.growthfund_buy_req packet = (SBean.growthfund_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.growthfund_buy_res(role.buyGrowthFund(packet.effectiveTime, packet.id)));
	}

	public void onRecv_growthfund_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.growthfund_take_req packet = (SBean.growthfund_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.growthfund_take_res(role.takeGrowthFundReward(packet.effectiveTime, packet.id, packet.level)));
	}

	public void onRecv_doubledrop_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.doubledrop_sync_req packet = (SBean.doubledrop_sync_req)ipacket;
		role.syncDoubleDropInfo(packet.id);
	}

	public void onRecv_extradrop_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.extradrop_sync_req packet = (SBean.extradrop_sync_req)ipacket;
		role.syncExtraDropInfo(packet.id);
	}

	public void onRecv_exchangegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.exchangegift_sync_req packet = (SBean.exchangegift_sync_req)ipacket;
		role.syncExchangeGiftInfo(packet.id);
	}

	public void onRecv_exchangegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.exchangegift_take_req packet = (SBean.exchangegift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.exchangegift_take_res(role.takeExchangeGiftReward(packet.effectiveTime, packet.id, packet.seq)));
	}

	public void onRecv_logingift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.logingift_sync_req packet = (SBean.logingift_sync_req)ipacket;
		role.syncLoginGiftInfo(packet.id);
	}

	public void onRecv_logingift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.logingift_take_req packet = (SBean.logingift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.logingift_take_res(role.takeLoginGiftReward(packet.effectiveTime, packet.id, packet.day)));
	}

	public void onRecv_giftpackage_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.giftpackage_sync_req packet = (SBean.giftpackage_sync_req)ipacket;
		role.syncGiftPackageInfo(packet.id);
	}

	public void onRecv_giftpackage_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.giftpackage_take_req packet = (SBean.giftpackage_take_req)ipacket;
		role.takeGiftPackageReward(packet.effectiveTime, packet.id, packet.key);
	}

	public void onRecv_pbtcashback_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pbtcashback_sync_req packet = (SBean.pbtcashback_sync_req)ipacket;
	    role.syncPBTCashbackInfo(packet.bid);
	}

	public void onRecv_pbtcashback_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pbtcashback_take_req packet = (SBean.pbtcashback_take_req)ipacket;
		role.takePBTCashbackReward(packet.bid);
	}

	public void onRecv_payrank_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.payrank_sync_req packet = (SBean.payrank_sync_req)ipacket;
		role.syncPayRankInfo();
	}

	public void onRecv_groupbuy_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.groupbuy_sync_req packet = (SBean.groupbuy_sync_req)ipacket;
		role.syncGroupInfo();
	}

	public void onRecv_groupbuy_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.groupbuy_buy_req packet = (SBean.groupbuy_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.groupbuy_buy_res(role.buyGroupBuyGoods(packet.effectiveTime, packet.id, packet.gid, packet.count)));
	}

	public void onRecv_flashsale_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		//SBean.flashsale_sync_req packet = (SBean.flashsale_sync_req)ipacket;
		role.syncFlashSaleInfo();
	}

	public void onRecv_flashsale_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.flashsale_buy_req packet = (SBean.flashsale_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.flashsale_buy_res(role.buyFlashSaleGoods(packet.effectiveTime, packet.id, packet.goodid)));
	}

	public void onRecv_luckyroll_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckyroll_sync_req packet = (SBean.luckyroll_sync_req)ipacket;
	    role.syncLuckyRollerInfo();
	}

	public void onRecv_luckyroll_play_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckyroll_play_req packet = (SBean.luckyroll_play_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.luckyroll_play_res(role.playLuckyRoller(packet.effectiveTime, packet.id)));
	}

	public void onRecv_directpurchase_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.directpurchase_sync_req packet = (SBean.directpurchase_sync_req)ipacket;
		role.syncDirectPurchaseInfo(packet.id);
	}

	public void onRecv_directpurchase_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.directpurchase_take_req packet = (SBean.directpurchase_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.directpurchase_take_res(role.takeDirectPurchaseReward(packet.effectiveTime, packet.id, packet.payLevel)));
	}

	public void onRecv_onearmbandit_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onearmbandit_sync_req packet = (SBean.onearmbandit_sync_req)ipacket;
		role.syncOneArmBanditInfo(packet.id);
	}

	public void onRecv_onearmbandit_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onearmbandit_take_req packet = (SBean.onearmbandit_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.onearmbandit_take_res(role.playOneArmBanditReward(packet.effectiveTime, packet.id)));	
	}

	public void onRecv_adver_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.adver_sync_req packet = (SBean.adver_sync_req)ipacket;
		role.syncAdversInfo(packet.id);
	}

	public void onRecv_mapcopy_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mapcopy_leave_req packet = (SBean.mapcopy_leave_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mapcopy_leave_res(role.tryLeaveMapCopy() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_normalmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.normalmap_start_req packet = (SBean.normalmap_start_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.normalmap_start_res(role.startNormalMapCopy(packet.mapId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_activitymap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitymap_start_req packet = (SBean.activitymap_start_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.activitymap_start_res(role.startActivityMapCopy(packet.mapId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_commonmap_selectcard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.commonmap_selectcard_req packet = (SBean.commonmap_selectcard_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.commonmap_selectcard_res(role.selectCommonMapCopyRewardCard(packet.cardNo)));
	}

	public void onRecv_normalmap_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.normalmap_buytimes_req packet = (SBean.normalmap_buytimes_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.normalmap_buytimes_res(role.buyNormalMapCopyEnterTimes(packet.mapId)? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_activitymap_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitymap_buytimes_req packet = (SBean.activitymap_buytimes_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.activitymap_buytimes_res(role.buyActivityMapCopyEnterTimes(packet.groupId)? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_privatemap_sweep_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_sweep_req packet = (SBean.privatemap_sweep_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.privatemap_sweep_res(role.sweepPrivateMap(packet.mapId, packet.times, packet.extraCard)));
	}

	public void onRecv_activity_sweep_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activity_sweep_req packet = (SBean.activity_sweep_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.activity_sweep_res(role.sweepActivityMap(packet.mapId, packet.times, packet.extraCard)));
	}

	public void onRecv_mail_syncsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_syncsys_req packet = (SBean.mail_syncsys_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_syncsys_res(role.getSysPageMails(packet.pageNO)));
	}

	public void onRecv_mail_synctmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_synctmp_req packet = (SBean.mail_synctmp_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_synctmp_res(role.getTmpPageMails(packet.pageNO)));
	}

	public void onRecv_mail_read_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_read_req packet = (SBean.mail_read_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_read_res(role.getSysMailDetail(packet.mailId)));
	}

	public void onRecv_mail_readsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_readsys_req packet = (SBean.mail_readsys_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_readsys_res(role.getSysMailDetail(packet.mailId)));
	}

	public void onRecv_mail_readtmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_readtmp_req packet = (SBean.mail_readtmp_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_readtmp_res(role.getTmpMailDetail(packet.mailId)));
	}

	public void onRecv_mail_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_take_req packet = (SBean.mail_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_take_res(role.takeSysMailAttachment(packet.mailId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mail_takesys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_takesys_req packet = (SBean.mail_takesys_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_takesys_res(role.takeSysMailAttachment(packet.mailId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mail_taketmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_taketmp_req packet = (SBean.mail_taketmp_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_taketmp_res(role.takeTmpMailAttachment(packet.mailId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mail_del_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_del_req packet = (SBean.mail_del_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_del_res(role.delSysMail(packet.mailId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mail_delsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_delsys_req packet = (SBean.mail_delsys_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_delsys_res(role.delSysMail(packet.mailId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mail_deltmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_deltmp_req packet = (SBean.mail_deltmp_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_deltmp_res(role.delTmpMail(packet.mailId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mail_takeallsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_takeallsys_req packet = (SBean.mail_takeallsys_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_takeallsys_res(role.takeAllSysMailAttachment()));
	}

	public void onRecv_mail_takealltmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_takealltmp_req packet = (SBean.mail_takealltmp_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mail_takealltmp_res(role.takeAllTmpMailAttachment()));
	}

	public void onRecv_rollnotice_query(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rollnotice_query packet = (SBean.rollnotice_query)ipacket;
		role.queryRollNotice(packet.noticeId);
	}

	public void onRecv_rank_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rank_sync_req packet = (SBean.rank_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rank_sync_res(role.syncRanksBrief()));
	}

	public void onRecv_sectrank_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectrank_sync_req packet = (SBean.sectrank_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sectrank_sync_res(role.syncSectRanksBrief()));
	}

	public void onRecv_rank_get_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rank_get_req packet = (SBean.rank_get_req)ipacket;
		role.getRankList(packet.id, packet.createTime, packet.index, packet.length);
	}

	public void onRecv_sectrank_get_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectrank_get_req packet = (SBean.sectrank_get_req)ipacket;
		role.getSectRankList(packet.id, packet.createTime, packet.index, packet.length);
	}

	public void onRecv_rank_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rank_self_req packet = (SBean.rank_self_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rank_self_res(role.getSelfRank(packet.id)));
	}

	public void onRecv_sectrank_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectrank_self_req packet = (SBean.sectrank_self_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sectrank_self_res(role.getSelfRank(packet.id)));
	}

	public void onRecv_buy_coin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_coin_req packet = (SBean.buy_coin_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.buy_coin_res(role.buyCoin(packet.times)));
	}

	public void onRecv_buy_vit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_vit_req packet = (SBean.buy_vit_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.buy_vit_res(role.buyVit() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pay_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pay_sync_req packet = (SBean.pay_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pay_sync_res(role.syncPayInfo()));
	}

	public void onRecv_pay_asgod_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pay_asgod_req packet = (SBean.pay_asgod_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pay_asgod_res(role.tryPayAsGod(packet.level)));
	}

	public void onRecv_vip_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.vip_take_req packet = (SBean.vip_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.vip_take_res(role.takeVipRewards(packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_expand_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_expand_req packet = (SBean.bag_expand_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_expand_res(role.expandBagCells(packet.times) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_sellequip_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellequip_req packet = (SBean.bag_sellequip_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_sellequip_res(role.sellBagEquip(packet.id, packet.guid) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_sellitem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellitem_req packet = (SBean.bag_sellitem_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_sellitem_res(role.sellBagItem(packet.id, packet.count) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_sellgem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellgem_req packet = (SBean.bag_sellgem_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_sellgem_res(role.sellBagGem(packet.id, packet.count) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_sellbook_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellbook_req packet = (SBean.bag_sellbook_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_sellbook_res(role.sellBagBook(packet.id, packet.count) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_batchsellequips_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellequips_req packet = (SBean.bag_batchsellequips_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_batchsellequips_res(role.batchSellBagEquips(packet.equips) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_batchsellitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellitems_req packet = (SBean.bag_batchsellitems_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_batchsellitems_res(role.batchSellBagItems(packet.items) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_batchsellgems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellgems_req packet = (SBean.bag_batchsellgems_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_batchsellgems_res(role.batchSellBagGems(packet.gems) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_batchsellbooks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellbooks_req packet = (SBean.bag_batchsellbooks_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_batchsellbooks_res(role.batchSellBagBooks(packet.books) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_useitemgift_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemgift_req packet = (SBean.bag_useitemgift_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemgift_res(role.useItemGiftBox(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemcoin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemcoin_req packet = (SBean.bag_useitemcoin_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemcoin_res(role.useItemCoinBag(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemdiamond_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemdiamond_req packet = (SBean.bag_useitemdiamond_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemdiamond_res(role.useItemDiamondBag(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemexp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemexp_req packet = (SBean.bag_useitemexp_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemexp_res(role.useItemExp(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemhp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemhp_req packet = (SBean.bag_useitemhp_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemhp_res(role.useItemHp(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemhppool_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemhppool_req packet = (SBean.bag_useitemhppool_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemhppool_res(role.useItemHpPool(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemchest_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemchest_req packet = (SBean.bag_useitemchest_req) ipacket;
		role.useItemChest(packet.id, packet.count);
	}

	public void onRecv_bag_useitemequipenergy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemequipenergy_req packet = (SBean.bag_useitemequipenergy_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemequipenergy_res(role.useItemEquipEnergy(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemgemenergy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemgemenergy_req packet = (SBean.bag_useitemgemenergy_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemgemenergy_res(role.useItemGemEnergy(packet.id, packet.count)));
	}

	public void onRecv_bag_useiteminspiration_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useiteminspiration_req packet = (SBean.bag_useiteminspiration_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useiteminspiration_res(role.useItemSpiritInspiration(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemvit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemvit_req packet = (SBean.bag_useitemvit_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemvit_res(role.useItemAsVit(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemfashion_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemfashion_req packet = (SBean.bag_useitemfashion_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemfashion_res(role.useItemFashion(packet.id)));
	}

	public void onRecv_bag_useitemexpcoinpool_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemexpcoinpool_req packet = (SBean.bag_useitemexpcoinpool_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemexpcoinpool_res(role.useItemAsExpCoinPool(packet.id, packet.count)));
	}

	public void onRecv_bag_usemonthlycard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_usemonthlycard_req packet = (SBean.bag_usemonthlycard_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_usemonthlycard_res(role.useMonthlyCard(packet.id)));
	}

	public void onRecv_bag_usevipcard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_usevipcard_req packet = (SBean.bag_usevipcard_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_usevipcard_res(role.useVipCard(packet.id)));
	}

	public void onRecv_bag_useitemfeat_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemfeat_req packet = (SBean.bag_useitemfeat_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemfeat_res(role.useItemFeatPlate(packet.id, packet.count)));
	}

	public void onRecv_bag_useitemskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemskill_req packet = (SBean.bag_useitemskill_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemskill_res(role.useItemSkill(packet.itemId, packet.pos, packet.rotation, packet.targetID, packet.targetType, packet.ownerID, packet.timeTick)));
	}

	public void onRecv_bag_useitemletter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemletter_req packet = (SBean.bag_useitemletter_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemletter_res(role.useItemLetter(packet.itemId)));
	}

	public void onRecv_bag_useitemevil_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemevil_req packet = (SBean.bag_useitemevil_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemevil_res(role.useItemEvilValue(packet.itemId, packet.count)));
	}

	public void onRecv_bag_piececompose_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_piececompose_req packet = (SBean.bag_piececompose_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_piececompose_res(role.pieceCompose(packet.composeId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_useitempropstrength_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitempropstrength_req packet = (SBean.bag_useitempropstrength_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitempropstrength_res(role.useItemPropStrength(packet.itemId, packet.count)));
	}

	public void onRecv_bag_useitemofflinefuncpoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemofflinefuncpoint_req packet = (SBean.bag_useitemofflinefuncpoint_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemofflinefuncpoint_res(role.useItemOfflineFunc(packet.itemId, packet.count)));
	}

	public void onRecv_bag_useitemtitle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemtitle_req packet = (SBean.bag_useitemtitle_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemtitle_res(role.useItemTitle(packet.itemId)));
	}

	public void onRecv_bag_useitemuskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemuskill_req packet = (SBean.bag_useitemuskill_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemuskill_res(role.useItemUniqueSkill(packet.itemId)));
	}

	public void onRecv_bag_useitemhead_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemhead_req packet = (SBean.bag_useitemhead_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_useitemhead_res(role.useItemHeadItem(packet.itemId)));
	}

	public void onRecv_equip_upwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_upwear_req packet = (SBean.equip_upwear_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_upwear_res(role.upWearEquip(packet.id, packet.guid, packet.pos) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_equip_downwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_downwear_req packet = (SBean.equip_downwear_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_downwear_res(role.downWearEquip(packet.guid, packet.pos) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_equip_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_levelup_req packet = (SBean.equip_levelup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_levelup_res(role.equipLevelUp(packet.pos, packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_equip_batchlevelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_batchlevelup_req packet = (SBean.equip_batchlevelup_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_batchlevelup_res(role.equipLevelUpBatch(packet.posLevels) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_equip_starup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_starup_req packet = (SBean.equip_starup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_starup_res(role.equipStartUp(packet.pos, packet.level)));
	}

	public void onRecv_equip_repair_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_repair_req packet = (SBean.equip_repair_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_repair_res(role.repairEquip(packet.pos)));
	}

	public void onRecv_equip_autoupwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_autoupwear_req packet = (SBean.equip_autoupwear_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_autoupwear_res(role.autoUpwearEquip(packet.equips) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_gem_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.gem_levelup_req packet = (SBean.gem_levelup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.gem_levelup_res(role.gemLevelUp(packet.pos, packet.seq, packet.toId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_gem_inlay_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.gem_inlay_req packet = (SBean.gem_inlay_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.gem_inlay_res(role.gemInlay(packet.pos, packet.seq, packet.gemId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_gem_unlay_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.gem_unlay_req packet = (SBean.gem_unlay_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.gem_unlay_res(role.gemUnlay(packet.pos, packet.seq, packet.gemId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_equip_refine_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_refine_req packet = (SBean.equip_refine_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.equip_refine_res(role.equipRefine(packet.id, packet.guid, packet.pos, packet.costItem)));
	}

	public void onRecv_legend_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_sync_req packet = (SBean.legend_sync_req)ipacket;
		role.syncLegendMake();
	}

	public void onRecv_legend_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_make_req packet = (SBean.legend_make_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.legend_make_res(role.doLegendMake(packet.id, packet.guid, packet.costItem)));
	}

	public void onRecv_legend_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_quit_req packet = (SBean.legend_quit_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.legend_quit_res(role.legendMakeQuit()));
	}

	public void onRecv_legend_save_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_save_req packet = (SBean.legend_save_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.legend_save_res(role.legendMakeSave()));
	}

	public void onRecv_skill_select_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_select_req packet = (SBean.skill_select_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.skill_select_res(role.selectSkill(packet.slotId, packet.skillId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_skill_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_levelup_req packet = (SBean.skill_levelup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.skill_levelup_res(role.skillLevelUp(packet.skillId, packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_skill_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_enhance_req packet = (SBean.skill_enhance_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.skill_enhance_res(role.skillEnhance(packet.skillId, packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_skill_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_unlock_req packet = (SBean.skill_unlock_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.skill_unlock_res(role.skillUnlock(packet.skillId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_uniqueskill_set_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.uniqueskill_set_req packet = (SBean.uniqueskill_set_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.uniqueskill_set_res(role.setCurUniqueSkill(packet.skillID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_spirit_learn_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_learn_req packet = (SBean.spirit_learn_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.spirit_learn_res(role.learnSpirit(packet.spiritId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_spirit_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_levelup_req packet = (SBean.spirit_levelup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.spirit_levelup_res(role.spiritLevelUp(packet.spiritId, packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_spirit_install_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_install_req packet = (SBean.spirit_install_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.spirit_install_res(role.installSpirit(packet.spiritId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_spirit_uninstall_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_uninstall_req packet = (SBean.spirit_uninstall_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.spirit_uninstall_res(role.uninstallSpirit(packet.spiritId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_weapon_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_make_req packet = (SBean.weapon_make_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_make_res(role.makeWeapon(packet.weaponId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_weapon_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_levelup_req packet = (SBean.weapon_levelup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_levelup_res(role.weaponLevelUp(packet.weaponId, packet.items) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_weapon_buylevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_buylevel_req packet = (SBean.weapon_buylevel_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_buylevel_res(role.weaponBuyLevel(packet.weaponId, packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_weapon_starup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_starup_req packet = (SBean.weapon_starup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_starup_res(role.weaponStarUp(packet.weaponId, packet.star, packet.itemCount, packet.altCount) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_weapon_select_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_select_req packet = (SBean.weapon_select_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_select_res(role.selectWeapon(packet.weaponId)));
	}

	public void onRecv_weapon_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_sync_req packet = (SBean.weapon_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_sync_res(role.syncWeaponInfo()));
	}

	public void onRecv_weapon_uskill_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_uskill_open_req packet = (SBean.weapon_uskill_open_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_uskill_open_res(role.openWeaponUSkill(packet.weaponID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_weapon_setform_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_setform_req packet = (SBean.weapon_setform_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_setform_res(role.setWeaponForm(packet.weaponID, (byte)packet.form) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_weaponmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weaponmap_start_req packet = (SBean.weaponmap_start_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weaponmap_start_res(role.startWeaponMapCopy() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_sync_req packet = (SBean.pet_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_sync_res(role.syncPetInfo()));
	}

	public void onRecv_pet_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_make_req packet = (SBean.pet_make_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_make_res(role.makePet(packet.petId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_transform_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_transform_req packet = (SBean.pet_transform_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_transform_res(role.petTransform(packet.petId, packet.tlvl) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_levelup_req packet = (SBean.pet_levelup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_levelup_res(role.petLevelUp(packet.petId, packet.items) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_buylevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_buylevel_req packet = (SBean.pet_buylevel_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_buylevel_res(role.petBuyLevel(packet.petId, packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_starup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_starup_req packet = (SBean.pet_starup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_starup_res(role.petStarUp(packet.petId, packet.star, packet.itemCount, packet.altCount) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_breakskillvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_breakskillvlup_req packet = (SBean.pet_breakskillvlup_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_breakskillvlup_res(role.petBreakSkillLevelUp(packet.petId, packet.skillId, packet.level, packet.itemCount, packet.altCount) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_worldmapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_worldmapset_req packet = (SBean.pet_worldmapset_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_worldmapset_res(role.setWorldMapFightPet(packet.petId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_privatemapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_privatemapset_req packet = (SBean.pet_privatemapset_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_privatemapset_res(role.setPrivateMapFightPet(packet.petsId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_sectmapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_sectmapset_req packet = (SBean.pet_sectmapset_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_sectmapset_res(role.setSectMapFightPet(packet.petsId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_activitymapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_activitymapset_req packet = (SBean.pet_activitymapset_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_activitymapset_res(role.setActivityMapFightPet(packet.petsId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_lifetaskmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lifetaskmap_start_req packet = (SBean.lifetaskmap_start_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.lifetaskmap_start_res(role.startPetLifeMapCopy(packet.mapId, packet.petId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_petspirit_lvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petspirit_lvlup_req packet = (SBean.petspirit_lvlup_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.petspirit_lvlup_res(role.petSpiritLvlUp(packet.spiritID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_petspirit_learn_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petspirit_learn_req packet = (SBean.petspirit_learn_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.petspirit_learn_res(role.petSpiritLearn(packet.petID, packet.spiritID, packet.index)));
	}

	public void onRecv_petspirit_replace_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petspirit_replace_req packet = (SBean.petspirit_replace_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.petspirit_replace_res(role.petSpiritRepleace(packet.petID, packet.index) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_dtask_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dtask_sync_req packet = (SBean.dtask_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.dtask_sync_res(role.syncDailyTasks()));
	}

	public void onRecv_dtask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dtask_take_req packet = (SBean.dtask_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.dtask_take_res(role.takeDailyTaskReward(packet.id) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_chtask_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.chtask_sync_req packet = (SBean.chtask_sync_req) ipacket;
		role.syncChallengeTasks();
		//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.chtask_sync_res(role.syncChallengeTasks()));
	}

	public void onRecv_chtask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.chtask_take_req packet = (SBean.chtask_take_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.chtask_take_res(role.takeChallengeTaskReward(packet.type, packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_fame_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fame_sync_req packet = (SBean.fame_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.fame_sync_res(role.syncFame(packet.level)));
	}

	public void onRecv_fame_upgrade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fame_upgrade_req packet = (SBean.fame_upgrade_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.fame_upgrade_res(role.upgradeFame(packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_fame_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fame_take_req packet = (SBean.fame_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.fame_take_res(role.takeFameRewards(packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_onlinegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onlinegift_sync_req packet = (SBean.onlinegift_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.onlinegift_sync_res(role.syncDailyOnlineGift()));
	}

	public void onRecv_onlinegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onlinegift_take_req packet = (SBean.onlinegift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.onlinegift_take_res(role.takeDailyOnlineGift(packet.minute) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_offlineexp_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.offlineexp_take_req packet = (SBean.offlineexp_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.offlineexp_take_res(role.takeOfflineAccExp(packet.accTime, packet.doubleExp != 0)));
	}

	public void onRecv_quizgift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.quizgift_sync_req packet = (SBean.quizgift_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.quizgift_sync_res(role.syncDailyQuizGiftInfo()));
	}

	public void onRecv_quizgift_qrank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.quizgift_qrank_req packet = (SBean.quizgift_qrank_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.quizgift_qrank_res(role.queryDailyQuizGiftRank(packet.startTime)));
	}

	public void onRecv_quizgift_answer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.quizgift_answer_req packet = (SBean.quizgift_answer_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.quizgift_answer_res(role.answerDailyQuizQuestion(packet.startTime, packet.seq, packet.answer, packet.useBoubleBonus != 0) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sync_luckywheel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_luckywheel_req packet = (SBean.sync_luckywheel_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_luckywheel_res(role.syncLuckyWheelDrawTimes()));
	}

	public void onRecv_luckywheel_ondraw_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckywheel_ondraw_req packet = (SBean.luckywheel_ondraw_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.luckywheel_ondraw_res(role.luckyWheelOnDraw()));
	}

	public void onRecv_luckywheel_buydrawtimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckywheel_buydrawtimes_req packet = (SBean.luckywheel_buydrawtimes_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.luckywheel_buydrawtimes_res(role.buyLuckyWheelDrawTimes(packet.times)? 1:0 ));
	}

	public void onRecv_play_firework_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.play_firework_req packet = (SBean.play_firework_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.play_firework_res(role.playFirework(packet.fireworkID)));
	}

	public void onRecv_redenvelope_snatch_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.redenvelope_snatch_req packet = (SBean.redenvelope_snatch_req)ipacket;
		RpcRes<Integer> res = role.trySnatchRedEnvelopes(packet.startTime, packet.id);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.redenvelope_snatch_res(res.errCode, res.info));
	}

	public void onRecv_mtask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mtask_take_req packet = (SBean.mtask_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mtask_take_res(role.takeMainTask(packet.taskId)));
	}

	public void onRecv_mtask_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mtask_quit_req packet = (SBean.mtask_quit_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mtask_quit_res(role.quitMainTask(packet.taskId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mtask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mtask_reward_req packet = (SBean.mtask_reward_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mtask_reward_res(role.takeMainTaskReward(packet.taskId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_wtask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.wtask_reward_req packet = (SBean.wtask_reward_req) ipacket;
		role.takeWeaponTaskReward(packet.taskId);
	}

	public void onRecv_ptask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.ptask_reward_req packet = (SBean.ptask_reward_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.ptask_reward_res(role.takePetTaskReward(packet.petId, packet.taskId, packet.isdiamond)));
	}

	public void onRecv_petlifetask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petlifetask_take_req packet = (SBean.petlifetask_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.petlifetask_take_res(role.takePetLifeTask(packet.petId, packet.taskId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_petlifetask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petlifetask_reward_req packet = (SBean.petlifetask_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.petlifetask_reward_res(role.takePetLifeTaskReward(packet.petId, packet.taskId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_task_useitem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_useitem_req packet = (SBean.task_useitem_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.task_useitem_res(role.taskUseItem(packet.taskCat, packet.ItemId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_task_submititem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_submititem_req packet = (SBean.task_submititem_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.task_submititem_res(role.taskSubmitItem(packet.taskCat, packet.petId, packet.ItemId, packet.ItemCount) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pettask_submititem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pettask_submititem_req packet = (SBean.pettask_submititem_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pettask_submititem_res(role.petTaskSubmitItem(packet.petId, packet.ItemId, packet.ItemCount) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_petlifetask_submititem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petlifetask_submititem_req packet = (SBean.petlifetask_submititem_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.petlifetask_submititem_res(role.petLifeTaskSubmitItem(packet.petId, packet.ItemId, packet.ItemCount) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_task_dialog_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_dialog_req packet = (SBean.task_dialog_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.task_dialog_res(role.taskNpcTalk(packet.npcId, packet.dialogId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_task_conveynpc_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_conveynpc_req packet = (SBean.task_conveynpc_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.task_conveynpc_res(role.taskConvoyNpc(packet.npcId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_task_conveyitem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_conveyitem_req packet = (SBean.task_conveyitem_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.task_conveyitem_res(role.taskConvoyItem() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_task_answer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_answer_req packet = (SBean.task_answer_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.task_answer_res(role.taskAnswer(packet.questionId, packet.answer) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_task_randquestion_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_randquestion_req packet = (SBean.task_randquestion_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.task_randquestion_res(role.taskRandQuestion(packet.taskCat) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_pet_revive_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_revive_req packet = (SBean.pet_revive_req) ipacket;
		//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_revive_res(role.reviveFightPetInSitu(packet.petId, packet.useStone > 0) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_role_mine_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_mine_req packet = (SBean.role_mine_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.role_mine_res(role.tryStartMine(packet.mineId, packet.mineInstance)));
	}

	public void onRecv_set_attackmode_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_attackmode_req packet = (SBean.set_attackmode_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.set_attackmode_res(role.setPKMode(packet.mode) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_team_query_member(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_query_member packet = (SBean.team_query_member) ipacket;
		role.queryRoleProfile(packet.roleId);
	}

	public void onRecv_team_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_invite_req packet = (SBean.team_invite_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_invite_res(role.teamInvite(packet.roleId)));
	}

	public void onRecv_team_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_invitedby_req packet = (SBean.team_invitedby_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_invitedby_res(role.teamInvitedby(packet.roleId, packet.accept)));
	}

	public void onRecv_team_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_apply_req packet = (SBean.team_apply_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_apply_res(role.teamApply(packet.teamId)));
	}

	public void onRecv_team_appliedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_appliedby_req packet = (SBean.team_appliedby_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_appliedby_res(role.teamAppliedby(packet.roleId, packet.accept > 0)));
	}

	public void onRecv_team_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_leave_req packet = (SBean.team_leave_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_leave_res(role.leaveTeam() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_team_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_kick_req packet = (SBean.team_kick_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_kick_res(role.teamKick(packet.roleId)));
	}

	public void onRecv_team_dissolve_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_dissolve_req packet = (SBean.team_dissolve_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_dissolve_res(role.teamDissolve()));
	}

	public void onRecv_team_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_change_leader_req packet = (SBean.team_change_leader_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_change_leader_res(role.teamChangeLeader(packet.roleId)));
	}

	public void onRecv_team_role_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_role_query_req packet = (SBean.team_role_query_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_role_query_res(role.queryRoleTeam(packet.roleId)));
	}

	public void onRecv_team_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_self_req packet = (SBean.team_self_req) ipacket;
		role.queryTeamRoles();
	}

	public void onRecv_team_mapt_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_mapt_req packet = (SBean.team_mapt_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_mapt_res(role.queryMapNearbyTeams()));
	}

	public void onRecv_team_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_mapr_req packet = (SBean.team_mapr_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.team_mapr_res(role.queryMapNearbyNoTeamRoles()));
	}

	public void onRecv_mroom_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_create_req packet = (SBean.mroom_create_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_create_res(role.mroomNewCreate(packet.mapId, packet.type)));
	}

	public void onRecv_mroom_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_enter_req packet = (SBean.mroom_enter_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_enter_res(role.enterMRoom(packet.mapId, packet.roomId, packet.roomType)));
	}

	public void onRecv_mroom_qenter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_qenter_req packet = (SBean.mroom_qenter_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_qenter_res(role.quickEnterMRoom(packet.mapId)));
	}

	public void onRecv_mroom_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_invite_req packet = (SBean.mroom_invite_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_invite_res(role.mroomInvite(packet.roleId)));
	}

	public void onRecv_mroom_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_invitedby_req packet = (SBean.mroom_invitedby_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_invitedby_res(role.mroomInvitedby(packet.roleId, packet.mapId, packet.roomId, packet.type, packet.accept)));
	}

	public void onRecv_mroom_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_leave_req packet = (SBean.mroom_leave_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_leave_res(role.leaveMRoom()));
	}

	public void onRecv_mroom_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_kick_req packet = (SBean.mroom_kick_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_kick_res(role.mroomKick(packet.roleId)));
	}

	public void onRecv_mroom_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_change_leader_req packet = (SBean.mroom_change_leader_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_change_leader_res(role.mroomChangeLeader(packet.roleId)));
	}

	public void onRecv_mroom_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_self_req packet = (SBean.mroom_self_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_self_res(role.queryMRoomRoles()));
	}

	public void onRecv_mroom_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_mapr_req packet = (SBean.mroom_mapr_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_mapr_res(role.queryMapNearbyNoMRoomRoles(Role::canEnterMRoom)));
	}

	public void onRecv_mroom_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_query_req packet = (SBean.mroom_query_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mroom_query_res(role.queryRooms(packet.mapId)));
	}

	public void onRecv_sect_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_sync_req packet = (SBean.sect_sync_req) ipacket;
		role.syncSectInfo();
	}

	public void onRecv_sect_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_create_req packet = (SBean.sect_create_req) ipacket;
		role.createNewSect(GameData.ensureNonull(packet.name), packet.icon, packet.useStone);
	}

	public void onRecv_sect_queryapplied_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_queryapplied_req packet = (SBean.sect_queryapplied_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_queryapplied_res(role.querySectSelfApplied(packet.sects)));
	}

	public void onRecv_sect_list_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_list_req packet = (SBean.sect_list_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_list_res(role.getRecentRects()));
	}

	public void onRecv_sect_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_query_req packet = (SBean.sect_query_req) ipacket;
		role.querySects(packet.sects);
	}

	public void onRecv_sect_searchbyid_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_searchbyid_req packet = (SBean.sect_searchbyid_req) ipacket;
		role.searchSect(packet.sectId);
	}

	public void onRecv_sect_searchbyname_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_searchbyname_req packet = (SBean.sect_searchbyname_req) ipacket;
		role.searchSect(packet.sectName);
	}

	public void onRecv_sect_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_apply_req packet = (SBean.sect_apply_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_apply_res(role.applyJoinSect(packet.sectId)));
	}

	public void onRecv_sect_qapply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_qapply_req packet = (SBean.sect_qapply_req) ipacket;
		//gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_qapply_res(role.fastApplyJoinSect()));
	}

	public void onRecv_sect_members_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_members_req packet = (SBean.sect_members_req) ipacket;
		role.getCurSectMembers();
	}

	public void onRecv_sect_applications_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_applications_req packet = (SBean.sect_applications_req) ipacket;
		role.getSectApplications();
	}

	public void onRecv_sect_history_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_history_req packet = (SBean.sect_history_req) ipacket;
		role.getSectHistory();
	}

	public void onRecv_sect_appliedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_appliedby_req packet = (SBean.sect_appliedby_req) ipacket;
		role.acceptOrRefuseSectApplication(packet.roleId, packet.accept == 1);
	}

	public void onRecv_sect_appliedbyall_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_appliedbyall_req packet = (SBean.sect_appliedbyall_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_appliedbyall_res(role.refuseAllSectApplication()));
	}

	public void onRecv_sect_appoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_appoint_req packet = (SBean.sect_appoint_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_appoint_res(role.appointSectMember(packet.roleId, packet.position)));
	}

	public void onRecv_sect_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_kick_req packet = (SBean.sect_kick_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_kick_res(role.kickSectMember(packet.roleId)));
	}

	public void onRecv_sect_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_leave_req packet = (SBean.sect_leave_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_leave_res(role.leaveSect()));
	}

	public void onRecv_sect_disband_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_disband_req packet = (SBean.sect_disband_req) ipacket;
		role.disbandSect();
	}

	public void onRecv_sect_changecreed_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_changecreed_req packet = (SBean.sect_changecreed_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_changecreed_res(role.updateSectCreed(packet.creed)));
	}

	public void onRecv_sect_changename_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_changename_req packet = (SBean.sect_changename_req) ipacket;
		role.changeSectName(packet.name);
	}

	public void onRecv_sect_changeicon_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_changeicon_req packet = (SBean.sect_changeicon_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_changeicon_res(role.changeSectIconAndFrame(packet.icon, packet.frame)));
	}

	public void onRecv_sect_joinlvl_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_joinlvl_req packet = (SBean.sect_joinlvl_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_joinlvl_res(role.changeJoinSectLevel(packet.level)));
	}

	public void onRecv_sect_sendemail_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_sendemail_req packet = (SBean.sect_sendemail_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_sendemail_res(role.sectSendMail(packet.content)));
	}

	public void onRecv_sect_upgrade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_upgrade_req packet = (SBean.sect_upgrade_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_upgrade_res(role.sectUpgrade()));
	}

	public void onRecv_sect_accelerate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_accelerate_req packet = (SBean.sect_accelerate_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_accelerate_res(role.sectAccelerateUpgradeCooling(packet.accTime)));
	}

	public void onRecv_sect_aurasync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_aurasync_req packet = (SBean.sect_aurasync_req) ipacket;
		role.syncGetSectAuras();
	}

	public void onRecv_sect_auraexpadd_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_auraexpadd_req packet = (SBean.sect_auraexpadd_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_auraexpadd_res(role.addsSectAuraExp(packet.auraId, packet.itemId, packet.itemCount)));
	}

	public void onRecv_sect_worship_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_worship_req packet = (SBean.sect_worship_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_worship_res(role.worshipSectMember(packet.roleId, packet.type)));
	}

	public void onRecv_sect_syncworshipreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_syncworshipreward_req packet = (SBean.sect_syncworshipreward_req) ipacket;
		role.syncWorshipReward();
	}

	public void onRecv_sect_takeworshipreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_takeworshipreward_req packet = (SBean.sect_takeworshipreward_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_takeworshipreward_res(role.takeWorshipReward()));
	}

	public void onRecv_sect_openbanquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_openbanquet_req packet = (SBean.sect_openbanquet_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_openbanquet_res(role.openSectBanquet(packet.type)));
	}

	public void onRecv_sect_listbanquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_listbanquet_req packet = (SBean.sect_listbanquet_req) ipacket;
		role.getSyncBanquets();
	}

	public void onRecv_sect_joinbanquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_joinbanquet_req packet = (SBean.sect_joinbanquet_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_joinbanquet_res(role.joinSectBanquet(packet.bid)));
	}

	public void onRecv_sect_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_shopsync_req packet = (SBean.sect_shopsync_req) ipacket;
		role.syncSectShopInfo();
	}

	public void onRecv_sect_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_shoprefresh_req packet = (SBean.sect_shoprefresh_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_shoprefresh_res(role.userRefreshSectShop(packet.times, packet.isSecondType)));
	}

	public void onRecv_sect_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_shopbuy_req packet = (SBean.sect_shopbuy_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_shopbuy_res(role.buySectShopGoogs(packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sectmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_start_req packet = (SBean.sectmap_start_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sectmap_start_res(role.startSectMapCopy(packet.mapId)));
	}

	public void onRecv_sectmap_status_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_status_req packet = (SBean.sectmap_status_req) ipacket;
		role.querySectMapsStatus();
	}

	public void onRecv_sectmap_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_query_req packet = (SBean.sectmap_query_req) ipacket;
		role.querySectMap(packet.mapId);
	}

	public void onRecv_sectmap_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_open_req packet = (SBean.sectmap_open_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sectmap_open_res(role.openSectMap(packet.mapId)));
	}

	public void onRecv_sectmap_rewards_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_rewards_req packet = (SBean.sectmap_rewards_req) ipacket;
		role.getSectMapRewardsLog();
	}

	public void onRecv_sectmap_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_apply_req packet = (SBean.sectmap_apply_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sectmap_apply_res(role.applySectMapRewards(packet.mapId, packet.rewardId)));
	}

	public void onRecv_sectmap_allocation_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_allocation_req packet = (SBean.sectmap_allocation_req) ipacket;
		role.getSectMapAllocation(packet.mapId);
	}

	public void onRecv_sectmap_damage_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_damage_req packet = (SBean.sectmap_damage_req) ipacket;
		role.getSectMapDamage(packet.mapId);
	}

	public void onRecv_sectmap_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_sync_req packet = (SBean.sectmap_sync_req) ipacket;
		role.getSectMapDetail(packet.mapId);
	}

	public void onRecv_sect_task_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_sync_req packet = (SBean.sect_task_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_task_sync_res(role.syncSectSelfTask()));

	}

	public void onRecv_sect_share_task_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_share_task_sync_req packet = (SBean.sect_share_task_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_share_task_sync_res(role.syncSectSharedTask() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));

	}

	public void onRecv_sect_finish_task_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_finish_task_sync_req packet = (SBean.sect_finish_task_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_finish_task_sync_res(role.syncSectFinishedSelfTask()));
	}

	public void onRecv_sect_task_receive_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_receive_req packet = (SBean.sect_task_receive_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_task_receive_res(role.sectTaskReceive(packet.ownerId, packet.sid)));
	}

	public void onRecv_sect_task_cancel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_cancel_req packet = (SBean.sect_task_cancel_req) ipacket;
		int result = role.sectTaskCancel(packet.ownerId, packet.sid);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_task_cancel_res(result > 0 ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED, result));
	}

	public void onRecv_sect_task_finish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_finish_req packet = (SBean.sect_task_finish_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_task_finish_res(role.sectTaskFinish(packet.ownerId, packet.sid) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sect_task_issuance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_issuance_req packet = (SBean.sect_task_issuance_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_task_issuance_res(role.sectTaskIssuanceShare(packet.sid) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sect_task_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_reset_req packet = (SBean.sect_task_reset_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_task_reset_res(role.sectTaskReset()));
	}

	public void onRecv_sect_task_done_rewards_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_done_rewards_req packet = (SBean.sect_task_done_rewards_req) ipacket;
		SBean.SectTaskReward taskReward = role.sectTaskShareDoneRewards();
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_task_done_rewards_res(taskReward != null ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED, taskReward));
	}

	public void onRecv_diyskill_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_sync_req packet = (SBean.diyskill_sync_req)ipacket;
		role.diySkillSync();
	}

	public void onRecv_diyskill_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_create_req packet = (SBean.diyskill_create_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.diyskill_create_res(role.diySkillCreateNew(packet.params, packet.trends)));
	}

	public void onRecv_diyskill_save_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_save_req packet = (SBean.diyskill_save_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.diyskill_save_res(role.diySkillSave(packet.name, packet.iconId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_diyskill_discard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_discard_req packet = (SBean.diyskill_discard_req)ipacket;
		role.diySkillDiscard(packet.skillPos);
	}

	public void onRecv_diyskill_selectuse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_selectuse_req packet = (SBean.diyskill_selectuse_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.diyskill_selectuse_res(role.diySkillSelectUse(packet.skillPos) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_diyskill_canceluse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_canceluse_req packet = (SBean.diyskill_canceluse_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.diyskill_canceluse_res(role.diySkillCancelUse(packet.skillPos) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_diyskill_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_unlock_req packet = (SBean.diyskill_unlock_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.diyskill_unlock_res(role.diySkillSlotUnlock() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_diyskill_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_buytimes_req packet = (SBean.diyskill_buytimes_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.diyskill_buytimes_res(role.diySkillBuyTimes(packet.times) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_diyskill_share_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_share_req packet = (SBean.diyskill_share_req)ipacket;
		role.diySkillShare(packet.skillPos);
	}

	public void onRecv_diyskill_cancelshare_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_cancelshare_req packet = (SBean.diyskill_cancelshare_req)ipacket;
		role.diySkillCancelShare(packet.skillPos);
	}

	public void onRecv_diyskill_borrow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_borrow_req packet = (SBean.diyskill_borrow_req)ipacket;
		role.diySkillBorrow(packet.roleId, packet.skillId);
	}

	public void onRecv_diyskill_flaunt_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_flaunt_req packet = (SBean.diyskill_flaunt_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.diyskill_flaunt_res(role.diySkillFlaunt(packet.channel, packet.skillPos, packet.icons) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));

	}

	public void onRecv_diyskill_shareaward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_shareaward_req packet = (SBean.diyskill_shareaward_req)ipacket;
		role.diySkillShareAward();
	}

	public void onRecv_sync_pet_can_use_pool(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_pet_can_use_pool packet = (SBean.sync_pet_can_use_pool)ipacket;
		role.changePetCanUsePool(packet.canUsePool);
	}

	public void onRecv_sect_push_application_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_push_application_req packet = (SBean.sect_push_application_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_push_application_res(role.changeSectPushApplication(packet.ok)));
	}

	public void onRecv_suite_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.suite_buy_req packet = (SBean.suite_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.suite_buy_res(role.buySuite(packet.suiteId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_store_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.store_buy_req packet = (SBean.store_buy_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.store_buy_res(role.storeBuy(packet.id, packet.count)));
	}

	public void onRecv_teleport_npc_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.teleport_npc_req packet = (SBean.teleport_npc_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.teleport_npc_res(role.teleportNpc(packet.mapId, packet.npcId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_teleport_monster_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.teleport_monster_req packet = (SBean.teleport_monster_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.teleport_monster_res(role.teleportMonster(packet.mapId, packet.spawnPointId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_teleport_mineral_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.teleport_mineral_req packet = (SBean.teleport_mineral_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.teleport_mineral_res(role.teleportMineral(packet.mapId, packet.mineralPointId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_arena_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_sync_req packet = (SBean.arena_sync_req) ipacket;
		role.syncArenaInfo();
	}

	public void onRecv_arena_setpets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_setpets_req packet = (SBean.arena_setpets_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.arena_setpets_res(role.setArenaDefencePets(packet.pets) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_arena_ranks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_ranks_req packet = (SBean.arena_ranks_req) ipacket;
		role.syncArenaRankList();
	}

	public void onRecv_arena_refresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_refresh_req packet = (SBean.arena_refresh_req) ipacket;
		role.refreshArenaEnemies();
	}

	public void onRecv_arena_defencepets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_defencepets_req packet = (SBean.arena_defencepets_req)ipacket;
		role.getArenaDefencePetsBattleArray(packet.rid, packet.rank);
	}

	public void onRecv_arena_resetcool_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_resetcool_req packet = (SBean.arena_resetcool_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.arena_resetcool_res(role.arenaResetCool() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_arena_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_buytimes_req packet = (SBean.arena_buytimes_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.arena_buytimes_res(role.arenaBuyTimes(packet.times) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_arena_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_shopsync_req packet = (SBean.arena_shopsync_req) ipacket;
		role.syncArenaShopInfo();
	}

	public void onRecv_arena_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_shoprefresh_req packet = (SBean.arena_shoprefresh_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.arena_shoprefresh_res(role.userRefreshArenaShop(packet.times, packet.isSecondType)));
	}

	public void onRecv_arena_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_shopbuy_req packet = (SBean.arena_shopbuy_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.arena_shopbuy_res(role.buyArenaShopGoogs(packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_arena_startattack_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_startattack_req packet = (SBean.arena_startattack_req) ipacket;
		role.startArenaBattle(packet.selfRank, packet.selfPets, packet.targetRoleId, packet.targetRank);
	}

	public void onRecv_arena_log_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_log_req packet = (SBean.arena_log_req) ipacket;
		role.syncArenaLogs();
	}

	public void onRecv_arena_scoresync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_scoresync_req packet = (SBean.arena_scoresync_req) ipacket;
		role.syncArenaScore();
	}

	public void onRecv_arena_takescore_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_takescore_req packet = (SBean.arena_takescore_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.arena_takescore_res(role.takeArenaScoreReward(packet.score) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	//	public void onRecv_pay_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	//	{
	//		SBean.pay_start_req packet = (SBean.pay_start_req)ipacket;
	//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pay_start_res(role.godPay(packet.level) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	//	}
	//

	public void onRecv_arena_hidedefence_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_hidedefence_req packet = (SBean.arena_hidedefence_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.arena_hidedefence_res(role.setArenaDefenceHide(packet.hide)));
	}

	public void onRecv_superarena_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_sync_req packet = (SBean.superarena_sync_req)ipacket;
		role.syncSuperArenaInfo();
	}

	public void onRecv_superarena_setpets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_setpets_req packet = (SBean.superarena_setpets_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.superarena_setpets_res(role.setSuperArenaPets(packet.pets) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_superarena_singlejoin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_singlejoin_req packet = (SBean.superarena_singlejoin_req)ipacket;
		role.singleJoinSuperArena(packet.type);
	}

	public void onRecv_superarena_teamjoin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_teamjoin_req packet = (SBean.superarena_teamjoin_req)ipacket;
		role.teamJoinSuperArena(packet.type);
	}

	public void onRecv_superarena_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_quit_req packet = (SBean.superarena_quit_req)ipacket;
		role.superarenaQuit(ok -> {
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.superarena_quit_res(ok));
		});
	}

	public void onRecv_superarena_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_shopsync_req packet = (SBean.superarena_shopsync_req)ipacket;
		role.syncSuperArenaShopInfo();
	}

	public void onRecv_superarena_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_shoprefresh_req packet = (SBean.superarena_shoprefresh_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.superarena_shoprefresh_res(role.userRefreshSuperArenaShop(packet.times, packet.isSecondType)));
	}

	public void onRecv_superarena_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_shopbuy_req packet = (SBean.superarena_shopbuy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.superarena_shopbuy_res(role.buySuperArenaShopGoogs(packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_aroom_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_create_req packet = (SBean.aroom_create_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_create_res(role.aroomNewCreate(packet.type)));
	}

	public void onRecv_aroom_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_invite_req packet = (SBean.aroom_invite_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_invite_res(role.aroomInvite(packet.roleID)));
	}

	public void onRecv_aroom_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_invitedby_req packet = (SBean.aroom_invitedby_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_invitedby_res(role.aroomInvitedby(packet.roleID, packet.roomID, packet.accept)));
	}

	public void onRecv_aroom_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_leave_req packet = (SBean.aroom_leave_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_leave_res(role.leaveARoom()));
	}

	public void onRecv_aroom_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_kick_req packet = (SBean.aroom_kick_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_kick_res(role.aroomKick(packet.roleID)));
	}

	public void onRecv_aroom_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_change_leader_req packet = (SBean.aroom_change_leader_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_change_leader_res(role.aroomChangeLeader(packet.roleId)));
	}

	public void onRecv_aroom_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_self_req packet = (SBean.aroom_self_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_self_res(role.queryARoomRoles()));
	}

	public void onRecv_aroom_query_member(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_query_member packet = (SBean.aroom_query_member)ipacket;
		role.queryARoomRole(packet.roleId);
	}

	public void onRecv_aroom_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_mapr_req packet = (SBean.aroom_mapr_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.aroom_mapr_res(role.queryMapNearbyNoARoomRoles(Role::canEnterARoom, role.arenaroom.type)));
	}

	public void onRecv_superarena_weekrank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_weekrank_req packet = (SBean.superarena_weekrank_req)ipacket;
//		role.syncSuperArenaWeekRank();
	}

	public void onRecv_superarena_dayrank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_dayrank_req packet = (SBean.superarena_dayrank_req)ipacket;
//		role.syncSuperArenaDayRank(packet.type);
	}

	public void onRecv_bwarena_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_sync_req packet = (SBean.bwarena_sync_req)ipacket;
		role.syncBWArenaInfo();
	}

	public void onRecv_bwarena_setpet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_setpet_req packet = (SBean.bwarena_setpet_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bwarena_setpet_res(role.setBwArenaPets(packet.pets) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bwarena_refresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_refresh_req packet = (SBean.bwarena_refresh_req)ipacket;
		role.refreshBwArenaEnemies();
	}

	public void onRecv_bwarena_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_buytimes_req packet = (SBean.bwarena_buytimes_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bwarena_buytimes_res(role.buyBWArenaTimes(packet.times) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bwarena_startattack_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_startattack_req packet = (SBean.bwarena_startattack_req)ipacket;
		role.startBWArenaMapCopy(packet.targetID);
	}

	public void onRecv_bwarena_takescore_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_takescore_req packet = (SBean.bwarena_takescore_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bwarena_takescore_res(role.takeBWArenaScoreReward() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bwarena_log_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_log_req packet = (SBean.bwarena_log_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bwarena_log_res(role.getBWArenaLog()));
	}

	public void onRecv_bwarena_ranks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_ranks_req packet = (SBean.bwarena_ranks_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bwarena_ranks_res(role.getBWArenaRanks(packet.bwtype, packet.index, packet.len)));
	}

	public void onRecv_forcewar_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.forcewar_sync_req packet = (SBean.forcewar_sync_req)ipacket;
		role.roleSyncForceWar();
	}

	public void onRecv_forcewar_join_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.forcewar_join_req packet = (SBean.forcewar_join_req)ipacket;
		role.roleJoinForceWar(packet.type);
	}

	public void onRecv_forcewar_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.forcewar_quit_req packet = (SBean.forcewar_quit_req)ipacket;
		role.roleQuitForceWar(ok -> {
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.forcewar_quit_res(ok));
		});
	}

	public void onRecv_froom_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_create_req packet = (SBean.froom_create_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.froom_create_res(role.froomNewCreate(packet.type)));
	}

	public void onRecv_froom_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_invite_req packet = (SBean.froom_invite_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.froom_invite_res(role.froomInvite(packet.roleID)));
	}

	public void onRecv_froom_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_invitedby_req packet = (SBean.froom_invitedby_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.froom_invitedby_res(role.froomInvitedby(packet.roleID, packet.roomID, packet.accept)));
	}

	public void onRecv_froom_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_leave_req packet = (SBean.froom_leave_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.froom_leave_res(role.leaveFRoom()));
	}

	public void onRecv_froom_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_kick_req packet = (SBean.froom_kick_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.froom_kick_res(role.froomKick(packet.roleID)));
	}

	public void onRecv_froom_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_change_leader_req packet = (SBean.froom_change_leader_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.froom_change_leader_res(role.froomChangeLeader(packet.roleID)));
	}

	public void onRecv_froom_query_member(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_query_member packet = (SBean.froom_query_member)ipacket;
		role.queryFRoomRole(packet.roleID);
	}

	public void onRecv_froom_query_members(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_query_members packet = (SBean.froom_query_members)ipacket;
		role.queryFRoomRoles();
	}

	public void onRecv_froom_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_mapr_req packet = (SBean.froom_mapr_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.froom_mapr_res(role.queryMapNearbyNoFRoomRoles(Role::canEnterFRoom, packet.type)));
	}

	public void onRecv_bosses_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bosses_sync_req packet = (SBean.bosses_sync_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bosses_sync_res(role.nextTransToBossTime, gs.getBossManager().syncAllBossesInfo()));
	}

	public void onRecv_walktoboss_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.walktoboss_req packet = (SBean.walktoboss_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.walktoboss_res(gs.getBossManager().walkToBoss(role, packet.bossID)));
	}

	public void onRecv_transtoboss_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.transtoboss_req packet = (SBean.transtoboss_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.transtoboss_res(gs.getBossManager().transToBoss(role, packet.bossID, packet.seq)));
	}

	public void onRecv_reset_transtime_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.reset_transtime_req packet = (SBean.reset_transtime_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.reset_transtime_res(role.resetTransTime() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_boss_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.boss_reward_req packet = (SBean.boss_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.boss_reward_res(gs.getBossManager().getBossRewardInfo(packet.bossID, packet.last == 1)));
	}

	public void onRecv_friend_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_sync_req packet = (SBean.friend_sync_req)ipacket;
		role.syncFriendData();
	}

	public void onRecv_friend_pluslist_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_pluslist_req packet = (SBean.friend_pluslist_req)ipacket;
		role.syncPlusFriends();
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_pluslist_res(role.getPlusFriends()));
	}

	public void onRecv_friend_recommend_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_recommend_req packet = (SBean.friend_recommend_req)ipacket;
		role.getSysRecommends();
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_recommend_res(role.getSysRecommends()));
	}

	public void onRecv_friend_add_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_add_req packet = (SBean.friend_add_req)ipacket;
		role.addFriend(packet.friendId);
	}

	public void onRecv_friend_search_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_search_req packet = (SBean.friend_search_req)ipacket;
		role.searchFriend(packet.name);
	}

	public void onRecv_friend_delete_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_delete_req packet = (SBean.friend_delete_req)ipacket;
		role.deleteFriend(packet.friendId);
	}

	public void onRecv_friend_givevit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_givevit_req packet = (SBean.friend_givevit_req)ipacket;
		role.giveFriendVit(packet.friendId, success -> {
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_givevit_res(success ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
		});
	}

	public void onRecv_friend_giveallvits_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_giveallvits_req packet = (SBean.friend_giveallvits_req)ipacket;
		role.giveFriendsVit(packet.friends);
	}

	public void onRecv_friend_receivevit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_receivevit_req packet = (SBean.friend_receivevit_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_receivevit_res(role.receiveFriendVit(packet.fids) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_friend_agreeadd_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_agreeadd_req packet = (SBean.friend_agreeadd_req)ipacket;
		role.agreeAddFriend(packet.friendId);
	}

	public void onRecv_friend_changemsg_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_changemsg_req packet = (SBean.friend_changemsg_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_changemsg_res(role.changeRoleMsg(packet.msg) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_friend_changehead_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_changehead_req packet = (SBean.friend_changehead_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_changehead_res(role.changeRoleHead(packet.headId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_friend_enemy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_enemy_req packet = (SBean.friend_enemy_req)ipacket;
		role.getFriendEnemys();
	}

	public void onRecv_friend_removeenemy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_removeenemy_req packet = (SBean.friend_removeenemy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_removeenemy_res(role.removeFriendEnemy(packet.enemyId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
		
	}

	public void onRecv_friend_setfocus_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_setfocus_req packet = (SBean.friend_setfocus_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.friend_setfocus_res(role.setFriendFocus(packet.friendId, packet.value) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_give_flower_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.give_flower_req packet = (SBean.give_flower_req)ipacket;
		role.giveFriendFlower(packet.rid, packet.count);	
	}

	public void onRecv_get_flowerlog_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.get_flowerlog_req packet = (SBean.get_flowerlog_req)ipacket;
		role.getFlowerOverviews();
	}

	public void onRecv_get_acceptlist_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.get_acceptlist_req packet = (SBean.get_acceptlist_req)ipacket;
		role.getAcceptFlowerList(packet.rid);
	}

	public void onRecv_auction_syncitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_syncitems_req packet = (SBean.auction_syncitems_req)ipacket;
		role.syncAuctionCommonItems(sessionid, packet.itemType, packet.classType, packet.rank, packet.level, packet.order, packet.page, packet.name);
	}

	public void onRecv_auction_syncequips_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_syncequips_req packet = (SBean.auction_syncequips_req)ipacket;
		role.syncAuctionCommonItems(sessionid, packet.itemType, packet.classType, packet.rank, packet.level, packet.order, packet.page, packet.name);
	}

	public void onRecv_auction_syncselfitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_syncselfitems_req packet = (SBean.auction_syncselfitems_req)ipacket;
		role.syncSelfAuctionItems(sessionid);
	}

	public void onRecv_auction_putonitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_putonitems_req packet = (SBean.auction_putonitems_req)ipacket;
		role.putOnNormalItems(sessionid, packet.id, packet.count, packet.price);
	}

	public void onRecv_auction_putonequip_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_putonequip_req packet = (SBean.auction_putonequip_req)ipacket;
		role.putOnEquip(sessionid, packet.id, packet.guid, packet.price);
	}

	public void onRecv_auction_putoffitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_putoffitems_req packet = (SBean.auction_putoffitems_req)ipacket;
		role.putOffAuctionItems(sessionid, packet.cid, packet.itemID, packet.count);
	}

	public void onRecv_auction_buyitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_buyitems_req packet = (SBean.auction_buyitems_req)ipacket;
		role.buyAuctionItems(sessionid, packet.sellerID, packet.cid, packet.items);
	}

	public void onRecv_auction_expand_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_expand_req packet = (SBean.auction_expand_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.auction_expand_res(role.expandAuctionCells(packet.times) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_auction_tradelog_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_tradelog_req packet = (SBean.auction_tradelog_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.auction_tradelog_res(role.syncAuctionTradeLogs()));
	}

	public void onRecv_auction_itemprices_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_itemprices_req packet = (SBean.auction_itemprices_req)ipacket;
		role.syncAuctionItemPrices(sessionid, packet.itemID);
	}

	public void onRecv_treasure_syncnpcs_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_syncnpcs_req packet = (SBean.treasure_syncnpcs_req)ipacket;
		role.syncTreasureNpcInfo();
	}

	public void onRecv_treasure_refreshnpc_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_refreshnpc_req packet = (SBean.treasure_refreshnpc_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_refreshnpc_res(role.refreshNpcInfo(packet.npcID, packet.times)));
	}

	public void onRecv_treasure_buypieces_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_buypieces_req packet = (SBean.treasure_buypieces_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_buypieces_res(role.buyTreasurePieces(packet.npcID, packet.pieceID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_treasure_npcreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_npcreward_req packet = (SBean.treasure_npcreward_req)ipacket;
		role.takeTreasureNpcReward(packet.npcID);
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_npcreward_res(role.takeTreasureNpcReward(packet.npcID)));
	}

	public void onRecv_treasure_syncmap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_syncmap_req packet = (SBean.treasure_syncmap_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_syncmap_res(role.syncTreasureMapInfo()));
	}

	public void onRecv_treasure_totalsearch_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_totalsearch_req packet = (SBean.treasure_totalsearch_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_totalsearch_res(role.treasureTotalSearch()));
	}

	public void onRecv_treasure_search_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_search_req packet = (SBean.treasure_search_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_search_res(role.searchTreasure(packet.pointIndex) ? packet.pointIndex : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_treasure_makemap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_makemap_req packet = (SBean.treasure_makemap_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_makemap_res(role.makeTreasureMap(packet.pieceID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_treasure_mapreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_mapreward_req packet = (SBean.treasure_mapreward_req)ipacket;
		role.takeTreasureMapReward();
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_mapreward_res(role.takeTreasureMapReward()));
	}

	public void onRecv_treasure_quitmap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_quitmap_req packet = (SBean.treasure_quitmap_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_quitmap_res(role.quitTreasureMap() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_treasure_medalgrow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_medalgrow_req packet = (SBean.treasure_medalgrow_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.treasure_medalgrow_res(role.medalGrow(packet.medalID, packet.type) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_tame_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_tame_req packet = (SBean.horse_tame_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_tame_res(role.tameHorse(packet.hid)));
	}

	public void onRecv_horse_use_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_use_req packet = (SBean.horse_use_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_use_res(role.setCurUseHorse(packet.hid) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_upstar_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_upstar_req packet = (SBean.horse_upstar_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_upstar_res(role.upStarHorse(packet.hid, packet.star) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_enhance_req packet = (SBean.horse_enhance_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_enhance_res(role.enhanceHorse(packet.hid, packet.locks, packet.isReplace)));
	}

	public void onRecv_horse_enhancesave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_enhancesave_req packet = (SBean.horse_enhancesave_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_enhancesave_res(role.saveEnhanceAttrs() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_changeshow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_changeshow_req packet = (SBean.horse_changeshow_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_changeshow_res(role.changeHorseShow(packet.hid, packet.showID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_activateshow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_activateshow_req packet = (SBean.horse_activateshow_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_activateshow_res(role.activateShow(packet.hid) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_learnskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_learnskill_req packet = (SBean.horse_learnskill_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_learnskill_res(role.learnHorseSkill(packet.skillID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sync_horse_skillLevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_horse_skillLevel_req packet = (SBean.sync_horse_skillLevel_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_horse_skillLevel_res(role.syncHorseSkillLevel()));
	}

	public void onRecv_horse_skill_up_level_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_skill_up_level_req packet = (SBean.horse_skill_up_level_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_skill_up_level_res(role.upLevelHorseSkill(packet.skillID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_setskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_setskill_req packet = (SBean.horse_setskill_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_setskill_res(role.setHorseSkill(packet.hid, packet.position, packet.skillID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_fashion_upwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fashion_upwear_req packet = (SBean.fashion_upwear_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.fashion_upwear_res(role.upWearFashionEquip(packet.fashionID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_fashion_setshow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fashion_setshow_req packet = (SBean.fashion_setshow_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.fashion_setshow_res(role.setFashionShow(packet.type, (byte)packet.isShow) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}
	
	public void onRecv_seal_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_make_req packet = (SBean.seal_make_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.seal_make_res(role.sealMake(packet.makeType) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_seal_upgrade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_upgrade_req packet = (SBean.seal_upgrade_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.seal_upgrade_res(role.sealUpGrade() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_seal_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_enhance_req packet = (SBean.seal_enhance_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.seal_enhance_res(role.sealEnhance(false)));
	}

	public void onRecv_seal_save_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_save_enhance_req packet = (SBean.seal_save_enhance_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.seal_save_enhance_res(role.saveSealEnhance(packet.isSave)));
	}

	public void onRecv_expcoin_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.expcoin_sync_req packet = (SBean.expcoin_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.expcoin_sync_res(role.expcoinSync()));
	}

	public void onRecv_expcoin_extract_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.expcoin_extract_req packet = (SBean.expcoin_extract_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.expcoin_extract_res(role.extractExpCoin() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_rarebook_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_sync_req packet = (SBean.rarebook_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rarebook_sync_res(role.rareBookSync()));
	}

	public void onRecv_rarebook_push_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_push_req packet = (SBean.rarebook_push_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rarebook_push_res(role.rareBookPush(packet.items) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_rarebook_pop_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_pop_req packet = (SBean.rarebook_pop_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rarebook_pop_res(role.rareBookPop(packet.books) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_rarebook_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_unlock_req packet = (SBean.rarebook_unlock_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rarebook_unlock_res(role.rareBookUnLock(packet.bookID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_rarebook_lvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_lvlup_req packet = (SBean.rarebook_lvlup_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rarebook_lvlup_res(role.rareBookLvlUp(packet.bookID) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_grasp_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.grasp_sync_req packet = (SBean.grasp_sync_req)ipacket;
		role.graspSync();
	}

	public void onRecv_grasp_impl_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.grasp_impl_req packet = (SBean.grasp_impl_req)ipacket;
		role.graspImpl(packet.graspID, packet.rid);
	}

	public void onRecv_grasp_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.grasp_reset_req packet = (SBean.grasp_reset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.grasp_reset_res(role.graspReset() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_dmgtransfer_buypoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_buypoint_req packet = (SBean.dmgtransfer_buypoint_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.dmgtransfer_buypoint_res(role.buyDMGTransfer(packet.discount == 1) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_dmgtransfer_putpoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_putpoint_req packet = (SBean.dmgtransfer_putpoint_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.dmgtransfer_putpoint_res(GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_dmgtransfer_lvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_lvlup_req packet = (SBean.dmgtransfer_lvlup_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.dmgtransfer_lvlup_res(role.lvlUpDMGTransfer(packet.id) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_dmgtransfer_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_reset_req packet = (SBean.dmgtransfer_reset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.dmgtransfer_reset_res(role.resetDMGTransfer() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_lead_info_set(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lead_info_set packet = (SBean.lead_info_set)ipacket;
		role.setLeadInfo(packet.id);
	}

	public void onRecv_lead_plot_set(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lead_plot_set packet = (SBean.lead_plot_set)ipacket;
		role.setLeadPlot(packet.id, packet.count);
	}

	public void onRecv_usersurvey_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.usersurvey_sync_req packet = (SBean.usersurvey_sync_req)ipacket;
		DBUserSurvey curUserSurvey = role.getUserSurvey();
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.usersurvey_sync_res(curUserSurvey.answers==null?0:curUserSurvey.answers.size(),curUserSurvey.reward));
	}

	public void onRecv_usersurvey_submit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.usersurvey_submit_req packet = (SBean.usersurvey_submit_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.usersurvey_submit_res(role.noteUserSurvey(role,packet.seq,packet.answer) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_usersurvey_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.usersurvey_reward_req packet = (SBean.usersurvey_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.usersurvey_reward_res(role.takeSurveyReward() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_cblogingift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblogingift_sync_req packet = (SBean.cblogingift_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.cblogingift_sync_res(role.getBetaLoginGifts()));
	}

	public void onRecv_cblogingift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblogingift_take_req packet = (SBean.cblogingift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.cblogingift_take_res(role.takeBetaLoginGifts() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_cblvlupgift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblvlupgift_sync_req packet = (SBean.cblvlupgift_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.cblvlupgift_sync_res(role.getBetaLvlupGift()));
	}

	public void onRecv_cblvlupgift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblvlupgift_take_req packet = (SBean.cblvlupgift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.cblvlupgift_take_res(role.takeBetaLvlupGift(packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_userdata_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.userdata_sync_req packet = (SBean.userdata_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.userdata_sync_res(role.getUserInfo()));
	}

	public void onRecv_userdata_modify_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.userdata_modify_req packet = (SBean.userdata_modify_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.userdata_modify_res(role.modifyUserInfo(packet) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_userdata_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.userdata_reward_req packet = (SBean.userdata_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.userdata_reward_res(role.takeUserInfoGift() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_cbcountdowngift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cbcountdowngift_sync_req packet = (SBean.cbcountdowngift_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.cbcountdowngift_sync_res(role.getLastBetaGift()));
	}

	public void onRecv_cbcountdowngift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cbcountdowngift_take_req packet = (SBean.cbcountdowngift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.cbcountdowngift_take_res(role.takeLastBetaGift(packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_ontimegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.ontimegift_sync_req packet = (SBean.ontimegift_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.ontimegift_sync_res(role.getOnTimeLoginGifts()));
	}

	public void onRecv_ontimegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.ontimegift_take_req packet = (SBean.ontimegift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.ontimegift_take_res(role.takeOnTimeLoginGifts() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_strengthengift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.strengthengift_sync_req packet = (SBean.strengthengift_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.strengthengift_sync_res(role.getStrengthenGifts()));
	}

	public void onRecv_strengthengift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.strengthengift_take_req packet = (SBean.strengthengift_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.strengthengift_take_res(role.takeStrengthenGifts(packet.strengthenNum) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_official_research_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.official_research_sync_req packet = (SBean.official_research_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.official_research_sync_res(role.getOfficialResearchGifts()));
	}

	public void onRecv_official_research_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.official_research_take_req packet = (SBean.official_research_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.official_research_take_res(role.takeOfficialResearchGifts() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_permanenttitle_set_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.permanenttitle_set_req packet = (SBean.permanenttitle_set_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.permanenttitle_set_res(role.setCurPermanentTitle(packet.id) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_timedtitle_set_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.timedtitle_set_req packet = (SBean.timedtitle_set_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.timedtitle_set_res(role.setCurTimedTitle(packet.id, packet.state) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}
	
	public void onRecv_titleslot_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.titleslot_unlock_req packet = (SBean.titleslot_unlock_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.titleslot_unlock_res(role.unlockTitleSlot() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_branch_task_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.branch_task_take_req packet = (SBean.branch_task_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.branch_task_take_res(role.accessBranchTask(packet.groupId)));
	}

	public void onRecv_branch_task_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.branch_task_quit_req packet = (SBean.branch_task_quit_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.branch_task_quit_res(role.quitBranchTask(packet.groupId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_branch_task_finish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.branch_task_finish_req packet = (SBean.branch_task_finish_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.branch_task_finish_res(role.takeBranchTaskReward(packet.groupId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sync_tower_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_tower_req packet = (SBean.sync_tower_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_tower_res(role.syncClimbTowerData()));
	}

	public void onRecv_tower_record_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_record_req packet = (SBean.tower_record_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.tower_record_res(role.getClimbTowerRecord()));
	}

	public void onRecv_tower_setpets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_setpets_req packet = (SBean.tower_setpets_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.tower_setpets_res(role.setClimbTowerPets(packet.pets) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_tower_startfight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_startfight_req packet = (SBean.tower_startfight_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.tower_startfight_res(role.startClimbTowerCopy(packet.groupId, packet.floor) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_tower_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_buytimes_req packet = (SBean.tower_buytimes_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.tower_buytimes_res(role.climbTowerBuyTimes(packet.times) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_tower_sweep_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_sweep_req packet = (SBean.tower_sweep_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.tower_sweep_res(role.sweepTowerMap(packet.groupId, packet.floor)));
	}

	public void onRecv_sync_towerfame_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_towerfame_req packet = (SBean.sync_towerfame_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_towerfame_res(role.syncClimbTowerFame()));
	}

	public void onRecv_tower_donate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_donate_req packet = (SBean.tower_donate_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.tower_donate_res(role.useItemTowerFame(packet.group, packet.itemId)));
	}

	public void onRecv_take_towerfame_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_towerfame_req packet = (SBean.take_towerfame_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.take_towerfame_res(role.takeTowerFameReward(packet.group, packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_enter_secretmap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.enter_secretmap_req packet = (SBean.enter_secretmap_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.enter_secretmap_res(role.enterSecretAreaMap() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_take_secretreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_secretreward_req packet = (SBean.take_secretreward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.take_secretreward_res(role.takeSecretReward(packet.id) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sect_deliver_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_sync_req packet = (SBean.sect_deliver_sync_req)ipacket;
		role.syncSectDeliver();
	}

	public void onRecv_sect_deliver_refresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_refresh_req packet = (SBean.sect_deliver_refresh_req)ipacket;
		role.refreshSectDeliver();
	}

	public void onRecv_sect_deliver_protect_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_protect_req packet = (SBean.sect_deliver_protect_req)ipacket;
		role.sectDeliverProtect();
	}

	public void onRecv_sect_deliver_begin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_begin_req packet = (SBean.sect_deliver_begin_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_deliver_begin_res(role.beginSectDeliver(packet.routeId, packet.vehicleId)));
	}

	public void onRecv_sect_deliver_search_help_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_search_help_req packet = (SBean.sect_deliver_search_help_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_deliver_search_help_res(role.searchHelpSectDeliver()));
	}

	public void onRecv_sect_deliver_on_help_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_on_help_req packet = (SBean.sect_deliver_on_help_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_deliver_on_help_res(role.onHelpSectDeliver(packet.roleId, packet.targetLocation, packet.line)));
	}

	public void onRecv_sect_deliver_cancel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_cancel_req packet = (SBean.sect_deliver_cancel_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_deliver_cancel_res(role.cancelSectDeliver()));
	}

	public void onRecv_sect_deliver_finish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_finish_req packet = (SBean.sect_deliver_finish_req)ipacket;
		role.finishSectDeliver();
	}
	
	public void onRecv_sect_deliver_sync_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_sync_wish_req packet = (SBean.sect_deliver_sync_wish_req)ipacket;
		role.syncWishSectDeliver();
	}

	public void onRecv_sect_deliver_add_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_add_wish_req packet = (SBean.sect_deliver_add_wish_req)ipacket;
		role.addWishSectDeliver();
	}

	public void onRecv_sect_deliver_save_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_save_wish_req packet = (SBean.sect_deliver_save_wish_req)ipacket;
		role.saveWishSectDeliver();
	}

	
	public void onRecv_sect_rob_task_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_rob_task_take_req packet = (SBean.sect_rob_task_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_rob_task_take_res(role.robTaskTake() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sect_rob_task_cancel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_rob_task_cancel_req packet = (SBean.sect_rob_task_cancel_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_rob_task_cancel_res(role.robTaskCancel() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sect_deliver_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_shopsync_req packet = (SBean.sect_deliver_shopsync_req)ipacket;
		role.syncSectDeliverShop();
	}

	public void onRecv_sect_deliver_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_shoprefresh_req packet = (SBean.sect_deliver_shoprefresh_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_deliver_shoprefresh_res(role.refreshSectDeliverShop(packet.times, packet.isSecondType)));
	}

	public void onRecv_sect_deliver_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_shopbuy_req packet = (SBean.sect_deliver_shopbuy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_deliver_shopbuy_res(role.buySectDeliverShop(packet.seq) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_produce_workshopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_workshopsync_req packet = (SBean.produce_workshopsync_req)ipacket;
		role.produceWorkShopSync();
	}

	public void onRecv_produce_createnewrecipe_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_createnewrecipe_req packet = (SBean.produce_createnewrecipe_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.produce_createnewrecipe_res(role.produceCreateNewRecipe(packet.reelID)));
	}

	public void onRecv_produce_produce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_produce_req packet = (SBean.produce_produce_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.produce_produce_res(role.produceTryProduce(packet.recipeID)));
	}

	public void onRecv_produce_split_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_split_req packet = (SBean.produce_split_req)ipacket;
		role.produceSplit( packet.equipid, packet.equipGuid);
	}

	public void onRecv_produce_fusion_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_fusion_req packet = (SBean.produce_fusion_req)ipacket;
		role.produceFusion(packet.consumeItems);
	}

	public void onRecv_produce_splitspbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_splitspbuy_req packet = (SBean.produce_splitspbuy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.produce_splitspbuy_res(role.produceSplitSPBuy(packet.times) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_bag_merge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_merge_req packet = (SBean.bag_merge_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.bag_merge_res(role.bagMerge(packet.itemId) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

//	public void onRecv_clan_helpprocess_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_helpprocess_req packet = (SBean.clan_helpprocess_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_helpprocess_res(role.clanHelpProcess()));
//	}
//
//	public void onRecv_clan_battlehelpfightstart_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_battlehelpfightstart_req packet = (SBean.clan_battlehelpfightstart_req)ipacket;
//		role.clanBattleHelpFightStart(packet.clanID);
//	}
//
//	public void onRecv_clan_searchbyname_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_searchbyname_req packet = (SBean.clan_searchbyname_req)ipacket;
//		
//	}
//
//	public void onRecv_clan_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_create_req packet = (SBean.clan_create_req) ipacket;
//		role.clanCreate(packet.name, packet.isFemale);
//	}
//
//	public void onRecv_clan_taskfight_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_taskfight_req packet = (SBean.clan_taskfight_req)ipacket;
//		role.clanTaskFightStart(packet.pets, packet.ownerPet);
//	}
//
//	public void onRecv_clan_moveposition_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_moveposition_req packet = (SBean.clan_moveposition_req)ipacket;
//		role.clanMovePosition();
//	}
//
//	public void onRecv_clan_getelitedisciple_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getelitedisciple_req packet = (SBean.clan_getelitedisciple_req)ipacket;
//		role.clanGetEliteDisciple(packet.clanId);
//	}
//
//	public void onRecv_clan_syncsearchore_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_syncsearchore_req packet = (SBean.clan_syncsearchore_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_syncsearchore_res(role.syncLastSearchOre(packet.clanId)));
//	}
//
//	public void onRecv_clan_findenemy_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_findenemy_req packet = (SBean.clan_findenemy_req)ipacket;
//		role.findEnemyClan(packet.clanId);
//	}
//
//	public void onRecv_clan_searchall_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_searchall_req packet = (SBean.clan_searchall_req) ipacket;
//		role.searchAllClans(packet.selfServer);
//	}
//
//	public void onRecv_clan_buydopower_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_buydopower_req packet = (SBean.clan_buydopower_req)ipacket;
//		role.clanBuyDoPower(packet.level);
//	}
//
//	public void onRecv_clan_battlefightstart_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_battlefightstart_req packet = (SBean.clan_battlefightstart_req)ipacket;
//		role.clanBattleFightStart();
//	}
//
//	public void onRecv_clan_autorefreshtask_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_autorefreshtask_req packet = (SBean.clan_autorefreshtask_req)ipacket;
//		role.autoRefreshClanTask(packet.taskId);
//	}
//
//	public void onRecv_clan_receivetask_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_receivetask_req packet = (SBean.clan_receivetask_req)ipacket;
//		role.clanTaskReceive(packet.taskId, packet.eliteDisciples, packet.clanId);
//	}
//
//	public void onRecv_clan_oreownerpetsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_oreownerpetsync_req packet = (SBean.clan_oreownerpetsync_req)ipacket;
//		role.clanOreOwnerPetSync(packet.clanId);
//	}
//
//	public void onRecv_clan_oreborrowpet_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_oreborrowpet_req packet = (SBean.clan_oreborrowpet_req)ipacket;
//		role.clanOreBorrowPet(packet.clanId, packet.petId);
//	}
//
//	public void onRecv_clan_applications_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_applications_req packet = (SBean.clan_applications_req) ipacket;
//		role.getClanApplications();
//	}
//
//	public void onRecv_clan_biwuspeedup_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_biwuspeedup_req packet = (SBean.clan_biwuspeedup_req)ipacket;
//		role.clanBiwuSpeedup(packet.count, packet.startTime);
//	}
//
//	public void onRecv_clan_chuandaostart_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_chuandaostart_req packet = (SBean.clan_chuandaostart_req)ipacket;
//		role.clanChuandaoStart();
//	}
//
//	public void onRecv_clan_setdefendteam_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_setdefendteam_req packet = (SBean.clan_setdefendteam_req)ipacket;
//		role.setClanDefendTeam(packet.pets);
//	}
//
//	public void onRecv_clan_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_query_req packet = (SBean.clan_query_req) ipacket;
//		role.queryClans();
//	}
//
//	public void onRecv_clan_shoutuspeedup_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_shoutuspeedup_req packet = (SBean.clan_shoutuspeedup_req)ipacket;
//		role.clanShoutuSpeedup(packet.count, packet.startTime);
//	}
//
//	public void onRecv_clan_getenemy_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getenemy_req packet = (SBean.clan_getenemy_req)ipacket;
//		role.clanBattleGetEnemy(packet.clanId);
//	}
//
//	public void onRecv_clan_battlehelp_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_battlehelp_req packet = (SBean.clan_battlehelp_req)ipacket;
//		role.clanBattleHelp(packet.clanId);
//	}
//
//	public void onRecv_clan_orebuilduplevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_orebuilduplevel_req packet = (SBean.clan_orebuilduplevel_req)ipacket;
//		role.clanOreBuildUpLevel(packet.type, packet.level);
//	}
//
//	public void onRecv_clan_getbaserank_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getbaserank_req packet = (SBean.clan_getbaserank_req)ipacket;
//		role.getClanBaseRank(packet.gType, packet.clanId, packet.rankType);
//	}
//
//	public void onRecv_clan_disband_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_disband_req packet = (SBean.clan_disband_req) ipacket;
//		role.clanDisband();
//	}
//
//	public void onRecv_clan_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_sync_req packet = (SBean.clan_sync_req) ipacket;
//		role.syncClanInfo(packet.clanId);
//	}
//
//	public void onRecv_clan_recovergendisciple_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_recovergendisciple_req packet = (SBean.clan_recovergendisciple_req)ipacket;
//		role.recoverGenDisciple();
//	}
//
//	public void onRecv_clan_members_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_members_req packet = (SBean.clan_members_req) ipacket;
//		role.getClanMembers(packet.clanId);
//	}
//
//	public void onRecv_clan_synctasklib_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_synctasklib_req packet = (SBean.clan_synctasklib_req)ipacket;
//		role.clanSyncLibTasks();
//	}
//
//	public void onRecv_clan_buyprestige_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_buyprestige_req packet = (SBean.clan_buyprestige_req)ipacket;
//		role.buyPrestige(packet.items);
//	}
//
//	public void onRecv_clan_getclanbattlelog_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getclanbattlelog_req packet = (SBean.clan_getclanbattlelog_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_getclanbattlelog_res(role.getClanBattleLog(packet.logType)));
//	}
//
//	public void onRecv_clan_battlekeek_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_battlekeek_req packet = (SBean.clan_battlekeek_req)ipacket;
//		role.clanBattleKeek(packet.clanID);
//	}
//
//	public void onRecv_clan_appointelder_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_appointelder_req packet = (SBean.clan_appointelder_req)ipacket;
//		role.clanAppointElder(packet.memberId, packet.memberGsId);
//	}
//
//	public void onRecv_clan_kickmember_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_kickmember_req packet = (SBean.clan_kickmember_req) ipacket;
//		role.clanKickMember(packet.memberId, packet.memberGsId);
//	}
//
//	public void onRecv_clan_getbattleteam_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getbattleteam_req packet = (SBean.clan_getbattleteam_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_getbattleteam_res(role.clanGetBattleTeam()));
//	}
//
//	public void onRecv_clan_getclantaskenemy_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getclantaskenemy_req packet = (SBean.clan_getclantaskenemy_req)ipacket;
//		role.clanGetClanTaskEnemy();
//	}
//
//	public void onRecv_clan_biwufinish_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_biwufinish_req packet = (SBean.clan_biwufinish_req)ipacket;
//		role.clanBiwuFinish();
//	}
//
//	public void onRecv_clan_setattackteam_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_setattackteam_req packet = (SBean.clan_setattackteam_req)ipacket;
//		role.setClanAttackTeam(packet.pets);
//	}
//
//	public void onRecv_clan_shoutu_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_shoutu_req packet = (SBean.clan_shoutu_req) ipacket;
//		role.clanShoutu();
//	}
//
//	public void onRecv_clan_searchore_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_searchore_req packet = (SBean.clan_searchore_req)ipacket;
//		role.clanSearchOre(packet.clanId);
//	}
//
//	public void onRecv_clan_orecancelborrowpet_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_orecancelborrowpet_req packet = (SBean.clan_orecancelborrowpet_req)ipacket;
//		role.clanOreCancelBorrowPet(packet.clanId, packet.petId);
//	}
//
//	public void onRecv_clan_rushtollgatetoexp_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_rushtollgatetoexp_req packet = (SBean.clan_rushtollgatetoexp_req)ipacket;
//		role.clanRushTollgateToExp(packet.dzid, packet.count, packet.useMoney);
//	}
//
//	public void onRecv_clan_battleseekhelp_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_battleseekhelp_req packet = (SBean.clan_battleseekhelp_req)ipacket;
//		role.clanBattleSeekhelp(packet.clanID);
//	}
//
//	public void onRecv_clan_applyadd_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_applyadd_req packet = (SBean.clan_applyadd_req) ipacket;
//		role.clanApplyAdd(packet.clanId);
//	}
//
//	public void onRecv_clan_syncselftask_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_syncselftask_req packet = (SBean.clan_syncselftask_req)ipacket;
//		role.clanSyncSelfTask(packet.clanId);
//	}
//
//	public void onRecv_clan_syncore_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_syncore_req packet = (SBean.clan_syncore_req)ipacket;
//		role.syncClanOre(packet.clanId);
//	}
//
//	public void onRecv_clan_ownerattriaddition_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_ownerattriaddition_req packet = (SBean.clan_ownerattriaddition_req)ipacket;
//		role.clanOwnerAttriAddition();
//	}
//
//	public void onRecv_clan_changeattackpoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_changeattackpoint_req packet = (SBean.clan_changeattackpoint_req)ipacket;
//		role.changeClanAttackPoint();
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_changeattackpoint_res());
//	}
//
//	public void onRecv_clan_cancelelder_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_cancelelder_req packet = (SBean.clan_cancelelder_req) ipacket;
//		role.clanCancelElder(packet.memberId, packet.memberGsId);
//	}
//
//	public void onRecv_clan_oreharry_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_oreharry_req packet = (SBean.clan_oreharry_req)ipacket;
//		role.clanOreHarry(packet.clanId, packet.serverId, packet.memberId, packet.oreType, packet.pets, packet.ownerPet);
//	}
//
//	public void onRecv_clan_oreharryrewards_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_oreharryrewards_req packet = (SBean.clan_oreharryrewards_req)ipacket;
//		role.clanOreHarryRewards();
//	}
//
//	public void onRecv_clan_memberleave_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_memberleave_req packet = (SBean.clan_memberleave_req) ipacket;
//		role.clanMemberLeave(packet.clanId);
//	}
//
//	public void onRecv_clan_battleattack_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_battleattack_req packet = (SBean.clan_battleattack_req)ipacket;
//		role.clanBattleAttack(packet.clanId);
//	}
//
//	public void onRecv_clan_uplevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_uplevel_req packet = (SBean.clan_uplevel_req) ipacket;
//		role.clanUplevel(packet.curLevel);
//	}
//
//	public void onRecv_clan_biwustart_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_biwustart_req packet = (SBean.clan_biwustart_req)ipacket;
//		role.clanBiwuStart();
//	}
//
//	public void onRecv_clan_attackprocess_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_attackprocess_req packet = (SBean.clan_attackprocess_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_attackprocess_res(role.clanAttackProcess()));
//	}
//
//	public void onRecv_clan_getnearbyclan_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getnearbyclan_req packet = (SBean.clan_getnearbyclan_req)ipacket;
//		role.getNearbyClan();
//	}
//
//	public void onRecv_clan_syncselfoccupyore_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_syncselfoccupyore_req packet = (SBean.clan_syncselfoccupyore_req)ipacket;
//		role.clanSyncSelfOccupyOre(packet.clanId);
//	}
//
//	public void onRecv_clan_finishtask_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_finishtask_req packet = (SBean.clan_finishtask_req)ipacket;
//		role.clanTaskFinish(packet.taskId, packet.clanId);
//	}
//
//	public void onRecv_clan_rushtollgatetoitem_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_rushtollgatetoitem_req packet = (SBean.clan_rushtollgatetoitem_req)ipacket;
//		role.clanRushTollgateToUseItem(packet.dzid, packet.items);
//	}
//
//	public void onRecv_clan_canceldisband_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_canceldisband_req packet = (SBean.clan_canceldisband_req)ipacket;
//		role.clanCancelDisband();
//	}
//
//	public void onRecv_clan_clanroboresuccessrewards_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_clanroboresuccessrewards_req packet = (SBean.clan_clanroboresuccessrewards_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_clanroboresuccessrewards_res(role.clanRobOreSuccessRewards()));
//	}
//
//	public void onRecv_clan_oreoccupy_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_oreoccupy_req packet = (SBean.clan_oreoccupy_req)ipacket;
//		role.clanOreOccupy(packet.clanId, packet.type, new TreeSet<Integer>(packet.pets));
//	}
//
//	public void onRecv_clan_ratifyadd_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_ratifyadd_req packet = (SBean.clan_ratifyadd_req) ipacket;
//		role.clanRatifyAdd(packet.memberId, packet.memberGsId, packet.isAgree);
//	}
//
//	public void onRecv_clan_applied_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_applied_req packet = (SBean.clan_applied_req) ipacket;
//		role.clanApplied(packet.clanIds);
//	}
//
//	public void onRecv_clan_oreoccupyfinish_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_oreoccupyfinish_req packet = (SBean.clan_oreoccupyfinish_req)ipacket;
//		role.clanOreOccupyFinish(packet.clanId, packet.type);
//	}
//
//	public void onRecv_clan_recruit_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_recruit_req packet = (SBean.clan_recruit_req) ipacket;
//		role.clanRecruit(packet.recruitType);
//	}
//
//	public void onRecv_clan_bushistart_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_bushistart_req packet = (SBean.clan_bushistart_req)ipacket;
//		role.clanBushiStart();
//	}
//
//	public void onRecv_clan_shoutufinish_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_shoutufinish_req packet = (SBean.clan_shoutufinish_req)ipacket;
//		role.clanShoutuFinish();
//	}
//
//	public void onRecv_clan_getclanroborelog_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_getclanroborelog_req packet = (SBean.clan_getclanroborelog_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_getclanroborelog_res(role.getClanRobOreLog()));
//	}
//
//	public void onRecv_clan_defendprocess_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_defendprocess_req packet = (SBean.clan_defendprocess_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.clan_defendprocess_res(role.clanDefendProcess()));
//	}
//
//	public void onRecv_clan_synchistory_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_synchistory_req packet = (SBean.clan_synchistory_req)ipacket;
//		role.syncClanHistory(packet.type, packet.clanId);
//	}
//
//	public void onRecv_clan_battlefightexit_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_battlefightexit_req packet = (SBean.clan_battlefightexit_req)ipacket;
//		role.clanBattleFightExit();
//	}
//
//	public void onRecv_clan_discardtask_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_discardtask_req packet = (SBean.clan_discardtask_req)ipacket;
//		role.clanTaskDiscard(packet.taskId, packet.clanId);
//	}
//
//	public void onRecv_clan_searchbyid_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.clan_searchbyid_req packet = (SBean.clan_searchbyid_req) ipacket;
//		role.searchClanById(packet.clanId);
//	}
//
	public void onRecv_rmactivity_takereward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rmactivity_takereward_req packet = (SBean.rmactivity_takereward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rmactivity_takereward_res(role.takeRemainActivityReward(packet.id) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}
	public void onRecv_role_rename_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_rename_req packet = (SBean.role_rename_req)ipacket;
		role.roleRename(packet.newName, packet.type);
	}

	public void onRecv_horse_ride_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_ride_req packet = (SBean.horse_ride_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_ride_res(role.roleRide() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_horse_unride_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_unride_req packet = (SBean.horse_unride_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_unride_res(role.roleUnRide() ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_mulhorse_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_invite_req packet = (SBean.mulhorse_invite_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mulhorse_invite_res(role.mulHorseInvite(packet.roleID)));
	}

	public void onRecv_mulhorse_invitehandle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_invitehandle_req packet = (SBean.mulhorse_invitehandle_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mulhorse_invitehandle_res(role.mulHorseInviteHandle(packet.inviter, packet.accept)));
	}

	public void onRecv_mulhorse_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_apply_req packet = (SBean.mulhorse_apply_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mulhorse_apply_res(role.mulHorseApply(packet.roleID)));
	}

	public void onRecv_mulhorse_applyhandle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_applyhandle_req packet = (SBean.mulhorse_applyhandle_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mulhorse_applyhandle_res(role.mulHorseApplyHandle(packet.roleID, packet.accept)));
	}

	public void onRecv_mulhorse_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_leave_req packet = (SBean.mulhorse_leave_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mulhorse_leave_res(role.mulHorseLeave()));
	}

	public void onRecv_mulhorse_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_kick_req packet = (SBean.mulhorse_kick_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mulhorse_kick_res(role.mulHorseKick(packet.roleID)));
	}

	public void onRecv_staywith_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.staywith_invite_req packet = (SBean.staywith_invite_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.staywith_invite_res(role.staywithInvite(packet.roleID)));
	}

	public void onRecv_staywith_invitehandle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.staywith_invitehandle_req packet = (SBean.staywith_invitehandle_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.staywith_invitehandle_res(role.staywithInviteHandle(packet.inviter, packet.accept)));
	}

	public void onRecv_staywith_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.staywith_leave_req packet = (SBean.staywith_leave_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.staywith_leave_res(role.staywithLeave()));
	}

	public void onRecv_sync_message_board_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_message_board_req packet = (SBean.sync_message_board_req)ipacket;
		role.syncMessageBoards();
	}

	public void onRecv_add_message_board_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.add_message_board_req packet = (SBean.add_message_board_req)ipacket;
		role.addOrReplaceMessageBoard(packet.side, packet.msgId, packet.content, packet.time, packet.anonymous, packet.isrewrite);
	}

	public void onRecv_comment_message_board_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.comment_message_board_req packet = (SBean.comment_message_board_req)ipacket;
		role.commentMessageBoard(packet.side, packet.msgId, packet.comment, packet.sendtime);
	}

	public void onRecv_change_message_board_content_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.change_message_board_content_req packet = (SBean.change_message_board_content_req)ipacket;
		role.changeMessageBoardContent(packet.side, packet.msgId, packet.content, packet.sendtime);
	}

	public void onRecv_schedule_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.schedule_sync_req packet = (SBean.schedule_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.schedule_sync_res(role.syncSchedule()));
	}

	public void onRecv_schedule_mapreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.schedule_mapreward_req packet = (SBean.schedule_mapreward_req)ipacket;
		role.bonusSchedule(packet.sid);
	}

//	public void onRecv_sect_push_application(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.sect_push_application packet = (SBean.sect_push_application)ipacket;
//		role.changeSectPushApplication(packet.ok);
//	}
//
	public void onRecv_save_guide_mapcopy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_guide_mapcopy_req packet = (SBean.save_guide_mapcopy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.save_guide_mapcopy_res(role.saveGuideMapcopy(packet.step)));
	}

	public void onRecv_sect_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_invite_req packet = (SBean.sect_invite_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_invite_res(role.inviteRoleInSect(packet.roleId)));
	}

	public void onRecv_sect_invite_response_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_invite_response_req packet = (SBean.sect_invite_response_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_invite_response_res(role.responseSectInvite(packet.inviteId, packet.response)));
	}

	public void onRecv_unlock_armor_type_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.unlock_armor_type_req packet = (SBean.unlock_armor_type_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.unlock_armor_type_res(role.unlockArmorType(packet.type)));
	}

	public void onRecv_armor_uprank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_uprank_req packet = (SBean.armor_uprank_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.armor_uprank_res(role.armorUpRank(packet.type, packet.nextRank)));
	}

	public void onRecv_armor_add_talent_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_add_talent_req packet = (SBean.armor_add_talent_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.armor_add_talent_res(role.armorAddTalent(packet.type, packet.talentId)));
	}

//	public void onRecv_sect_refuse_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.sect_refuse_invite_req packet = (SBean.sect_refuse_invite_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_refuse_invite_res(role.refuseSectInvite(packet.inviteId)));
//	}
//
//	public void onRecv_sect_accept_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.sect_accept_invite_req packet = (SBean.sect_accept_invite_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_accept_invite_res(role.acceptSectInvite(packet.inviteId, packet.sectId)));
//	}
//
	public void onRecv_armor_change_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_change_req packet = (SBean.armor_change_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.armor_change_res(role.armorChange(packet.type)));
	}

	public void onRecv_armor_up_level_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_up_level_req packet = (SBean.armor_up_level_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.armor_up_level_res(role.armorLevelUp(packet.type, packet.items)));
	}

	public void onRecv_rune_push_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rune_push_req packet = (SBean.rune_push_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rune_push_res(role.pushRuneToRuneBag(packet.runes)));
	}

	public void onRecv_rune_pop_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rune_pop_req packet = (SBean.rune_pop_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.rune_pop_res(role.popRuneToBag(packet.runes)));
	}

	public void onRecv_reset_talent_point_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.reset_talent_point_req packet = (SBean.reset_talent_point_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.reset_talent_point_res(role.resetTalentPoint(packet.type)));
	}

	public void onRecv_solt_group_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.solt_group_unlock_req packet = (SBean.solt_group_unlock_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.solt_group_unlock_res(role.unlockSoltGroup(packet.type, packet.soltGroupIndex)));
	}

	public void onRecv_solt_push_rune_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.solt_push_rune_req packet = (SBean.solt_push_rune_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.solt_push_rune_res(role.soltAddRune(packet.type, packet.soltGroupIndex, packet.soltIndex, packet.runeId)));
	}

	public void onRecv_lang_push_rune_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lang_push_rune_req packet = (SBean.lang_push_rune_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.lang_push_rune_res(role.soltUseLang(packet.type, packet.soltGroupIndex, packet.langId)));
	}

	public void onRecv_rune_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rune_wish_req packet = (SBean.rune_wish_req)ipacket;
		role.runeWish(packet.runes);
	}
	public void onRecv_put_in_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.put_in_warehouse_req packet = (SBean.put_in_warehouse_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.put_in_warehouse_res(role.putInWarehouse(packet.itemId, packet.itemCount, packet.warehouseType, packet.guid)));
	}

	public void onRecv_take_out_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_out_warehouse_req packet = (SBean.take_out_warehouse_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.take_out_warehouse_res(role.takeOutWarehouse(packet.itemId, packet.itemCount, packet.warehouseType, packet.guid)));
	}

	public void onRecv_expand_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.expand_warehouse_req packet = (SBean.expand_warehouse_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.expand_warehouse_res(role.expandWarehouse(packet.times, packet.warehouseType) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_sync_private_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_private_warehouse_req packet = (SBean.sync_private_warehouse_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_private_warehouse_res(role.syncPrivateWarehouse()));
	}

	public void onRecv_sync_public_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_public_warehouse_req packet = (SBean.sync_public_warehouse_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_public_warehouse_res(role.syncPublicWarehouse()));
	}

	public void onRecv_propose_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.propose_req packet = (SBean.propose_req)ipacket;
		Role.RpcRes<String> res = role.tryPropose(packet.grade);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.propose_res(res.errCode, res.info));
	}

	public void onRecv_propose_response_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.propose_response_req packet = (SBean.propose_response_req)ipacket;
		role.proposeResponse(packet.grade, packet.response);
	}

	public void onRecv_marriage_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_sync_req packet = (SBean.marriage_sync_req)ipacket;
		role.getMarriageOverview();
	}

	public void onRecv_divorce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.divorce_req packet = (SBean.divorce_req)ipacket;
		role.tryDivorce();
	}

	public void onRecv_marriage_skill_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_skill_levelup_req packet = (SBean.marriage_skill_levelup_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.marriage_skill_levelup_res(role.marriageSkillLevelUp(packet.skillId, packet.levelupTimes)));
	}

	public void onRecv_transform_to_partner_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.transform_to_partner_req packet = (SBean.transform_to_partner_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.transform_to_partner_res(role.transformToMarriage()));
	}

	public void onRecv_marriage_start_parade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_start_parade_req packet = (SBean.marriage_start_parade_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.marriage_start_parade_res(role.startMarriageParade()));
	}

	public void onRecv_marriage_start_banquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_start_banquet_req packet = (SBean.marriage_start_banquet_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.marriage_start_banquet_res(role.startMarriageBanquet()));
	}

	public void onRecv_exchange_item_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.exchange_item_req packet = (SBean.exchange_item_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.exchange_item_res(role.exchangeItemByNpc(packet.npcId, packet.exchangeId)));
	}

	public void onRecv_mrgseriestask_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrgseriestask_open_req packet = (SBean.mrgseriestask_open_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mrgseriestask_open_res(role.openMrgSeriesTask()));
	}

	public void onRecv_mrgseriestask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrgseriestask_take_req packet = (SBean.mrgseriestask_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mrgseriestask_take_res(role.takeMrgSeriesTask(packet.taskID)));
	}

	public void onRecv_mrgseriestask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrgseriestask_reward_req packet = (SBean.mrgseriestask_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mrgseriestask_reward_res(role.takeMrgSeriesTaskReward(packet.taskID)));
	}

	public void onRecv_mrglooptask_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrglooptask_open_req packet = (SBean.mrglooptask_open_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mrglooptask_open_res(role.openMrgLoopTask()));
	}

	public void onRecv_mrglooptask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrglooptask_take_req packet = (SBean.mrglooptask_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.mrglooptask_take_res(role.takeMrgLoopTask(packet.taskID)));
	}

	public void onRecv_mrglooptask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrglooptask_reward_req packet = (SBean.mrglooptask_reward_req)ipacket;
		role.takeMrgLoopTaskReward(packet.taskID, (ok, taskID) -> {
			gs.getRPCManager().sendStrPacket(sessionid, new SBean.mrglooptask_reward_res(ok, taskID));
		});
	}

	public void onRecv_save_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_skill_preset_req packet = (SBean.save_skill_preset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.save_skill_preset_res(role.saveSkillPreset(packet.index, packet.name, packet.skills, packet.diyskill, packet.uniqueSkill)));
	}

	public void onRecv_save_all_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_all_skill_preset_req packet = (SBean.save_all_skill_preset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.save_all_skill_preset_res(role.saveAllSkillPreset(packet.skills)));
	}

	public void onRecv_save_spirits_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_spirits_preset_req packet = (SBean.save_spirits_preset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.save_spirits_preset_res(role.saveSpiritsPreset(packet.index, packet.name, packet.spirits)));
	}

	public void onRecv_delete_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.delete_skill_preset_req packet = (SBean.delete_skill_preset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.delete_skill_preset_res(role.deleteSkillPreset(packet.index)));
	}

	public void onRecv_delete_spirits_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.delete_spirits_preset_req packet = (SBean.delete_spirits_preset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.delete_spirits_preset_res(role.deleteSpiritsPreset(packet.index)));
	}

	public void onRecv_change_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.change_skill_preset_req packet = (SBean.change_skill_preset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.change_skill_preset_res(role.changeSkillPreset(packet.index)));
	}

	public void onRecv_change_spirits_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.change_spirits_preset_req packet = (SBean.change_spirits_preset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.change_spirits_preset_res(role.changeSpiritsPreset(packet.index)));
	}

	public void onRecv_sect_group_map_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_group_map_open_req packet = (SBean.sect_group_map_open_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_group_map_open_res(role.openSectGroupMap(packet.mapId)));
	}

	public void onRecv_sect_group_map_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_group_map_enter_req packet = (SBean.sect_group_map_enter_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sect_group_map_enter_res(role.startSectGroupMapCopy(packet.mapId)));
	}

	public void onRecv_sect_group_map_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_group_map_sync_req packet = (SBean.sect_group_map_sync_req)ipacket;
		role.syncSectGroupMapCopyData();
	}

	public void onRecv_weapon_skill_level_up_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_skill_level_up_req packet = (SBean.weapon_skill_level_up_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_skill_level_up_res(role.weaponSkillLevelUp(packet.weaponId, packet.skillIndex, packet.level)));
	}

	public void onRecv_weapon_talent_level_up_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_talent_level_up_req packet = (SBean.weapon_talent_level_up_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_talent_level_up_res(role.weaponTalentLevelUp(packet.weaponId, packet.talentIndex)));
	}

	public void onRecv_weapon_talent_point_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_talent_point_buy_req packet = (SBean.weapon_talent_point_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_talent_point_buy_res(role.weaponGetTalentPoint(packet.weaponId)));
	}

	public void onRecv_weapon_talent_point_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_talent_point_reset_req packet = (SBean.weapon_talent_point_reset_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.weapon_talent_point_reset_res(role.weaponResetTalentPoint(packet.weaponId)));
	}
	public void onRecv_send_gift_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.send_gift_req packet = (SBean.send_gift_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.send_gift_res(role.sendItemToRole(packet.itemId, packet.itemNum, packet.roleId)));
	}

	public void onRecv_sync_big_map_flag_info_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_big_map_flag_info_req packet = (SBean.sync_big_map_flag_info_req)ipacket;
		role.syncAllFlags();
	}

	public void onRecv_pet_skill_level_up_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_skill_level_up_req packet = (SBean.pet_skill_level_up_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.pet_skill_level_up_res(role.petSkillLevelUp(packet.petId, packet.skillIndex, packet.level)));
	}

	public void onRecv_sync_special_card_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_special_card_req packet = (SBean.sync_special_card_req)ipacket;
		role.syncSpecialCard(packet.cardType);
	}

	public void onRecv_take_special_card_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_special_card_reward_req packet = (SBean.take_special_card_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.take_special_card_reward_res(role.takeSpecialCardReward(packet.cardType)));
	}

	public void onRecv_sync_daily_vit_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_daily_vit_reward_req packet = (SBean.sync_daily_vit_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_daily_vit_reward_res(role.SyncDailyVit()));
	}

	public void onRecv_take_daily_vit_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_daily_vit_reward_req packet = (SBean.take_daily_vit_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.take_daily_vit_reward_res(role.takeDailyVitReward(packet.vitId)));
	}

	public void onRecv_try_open_insight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_open_insight_req packet = (SBean.try_open_insight_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.try_open_insight_res(role.openWeaponInsight()));
	}

	public void onRecv_try_open_revenge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_open_revenge_req packet = (SBean.try_open_revenge_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.try_open_revenge_res(role.openWeaponRevenge()));
	}

	public void onRecv_try_sync_insight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_sync_insight_req packet = (SBean.try_sync_insight_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.try_sync_insight_res(role.syncWeaponInsight()));
	}

	public void onRecv_try_sync_revenge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_sync_revenge_req packet = (SBean.try_sync_revenge_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.try_sync_revenge_res(role.syncWeaponRevenge()));
	}

	public void onRecv_try_transform_insight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_transform_insight_req packet = (SBean.try_transform_insight_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.try_transform_insight_res(role.transformToWeaponInsight(packet.index)));
	}

	public void onRecv_try_transform_revenge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_transform_revenge_req packet = (SBean.try_transform_revenge_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.try_transform_revenge_res(role.transformToWeaponRevenge(packet.index)));
	}

	public void onRecv_heirloom_wipe_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.heirloom_wipe_req packet = (SBean.heirloom_wipe_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.heirloom_wipe_res(role.heirloomWipe(packet.colorSeq)));
	}

	public void onRecv_heirloom_takeout_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.heirloom_takeout_req packet = (SBean.heirloom_takeout_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.heirloom_takeout_res(role.heirloomTakeOut()));
	}

	public void onRecv_buy_offline_func_point_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_offline_func_point_req packet = (SBean.buy_offline_func_point_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.buy_offline_func_point_res(role.buyOfflineFuncPoint(packet.seq)));
	}

	public void onRecv_set_sect_qqgroup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_sect_qqgroup_req packet = (SBean.set_sect_qqgroup_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.set_sect_qqgroup_res(role.setSectQQGroup(packet.qqgroup)));
	}

	public void onRecv_set_heirloom_display_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_heirloom_display_req packet = (SBean.set_heirloom_display_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.set_heirloom_display_res(role.heirloomDisplay(packet.display)));
	}

	public void onRecv_feat_gambleshopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.feat_gambleshopsync_req packet = (SBean.feat_gambleshopsync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.feat_gambleshopsync_res(role.syncFeatGambleShopInfo()));
	}

	public void onRecv_feat_gambleshoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.feat_gambleshoprefresh_req packet = (SBean.feat_gambleshoprefresh_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.feat_gambleshoprefresh_res(role.userRefreshFeatGambleShop(packet.times)));
	}

	public void onRecv_feat_gambleshopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.feat_gambleshopbuy_req packet = (SBean.feat_gambleshopbuy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.feat_gambleshopbuy_res(role.buyFeatGambleShopGoogs(packet.seq)));
	}

	public void onRecv_add_marriage_bespeak_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.add_marriage_bespeak_req packet = (SBean.add_marriage_bespeak_req)ipacket;
		role.addMarriageBespeak(packet.line, packet.timeIndex);
	}

	public void onRecv_sync_marriage_bespeak_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_marriage_bespeak_req packet = (SBean.sync_marriage_bespeak_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_marriage_bespeak_res(role.syncMarriageBespeak()));
	}

	public void onRecv_base_dummygoods_quick_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.base_dummygoods_quick_buy_req packet = (SBean.base_dummygoods_quick_buy_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.base_dummygoods_quick_buy_res(role.quickBuyBaseDummyGoods(packet.buyItemId, packet.times)));
	}

	public void onRecv_activity_last_quick_done_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activity_last_quick_done_req packet = (SBean.activity_last_quick_done_req)ipacket;
		RpcRes<SBean.MapRewards> res = role.activityLastQuickFinish(packet.mapId, packet.seq);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.activity_last_quick_done_res(res.errCode, res.info));
	}

	public void onRecv_stele_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_sync_req packet = (SBean.stele_sync_req)ipacket;
		role.syncSteleInfo();
	}

	public void onRecv_stele_join_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_join_req packet = (SBean.stele_join_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.stele_join_res(role.joinStele()));
	}

	public void onRecv_stele_rank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_rank_req packet = (SBean.stele_rank_req)ipacket;
		role.syncSteleRank();
	}

	public void onRecv_stele_teleport_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_teleport_req packet = (SBean.stele_teleport_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.stele_teleport_res(gs.getSteleManager().teleportStele(role, packet.type, packet.index) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_blacklist_add_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.blacklist_add_req packet = (SBean.blacklist_add_req)ipacket;
		role.addBlackList(packet.rid);
	}

	public void onRecv_blacklist_del_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.blacklist_del_req packet = (SBean.blacklist_del_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.blacklist_del_res(role.deleteBlackList(packet.rid)));
	}

	public void onRecv_blacklist_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.blacklist_sync_req packet = (SBean.blacklist_sync_req)ipacket;
		role.syncBlackList();
	}

	public void onRecv_demonhole_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_sync_req packet = (SBean.demonhole_sync_req)ipacket;
		role.syncDemonHole();
	}

	public void onRecv_demonhole_join_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_join_req packet = (SBean.demonhole_join_req)ipacket;
		role.joinDemonHole();
	}

	public void onRecv_demonhole_changefloor_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_changefloor_req packet = (SBean.demonhole_changefloor_req)ipacket;
		role.changeDemonHoleFloor(packet.floor);
	}

	public void onRecv_demonhole_battle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_battle_req packet = (SBean.demonhole_battle_req)ipacket;
		role.queryDemonHoleBattle();
	}

	public void onRecv_justicemap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.justicemap_start_req packet = (SBean.justicemap_start_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.justicemap_start_res(role.startJusticeMapCopy()));
	}

	public void onRecv_emergency_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.emergency_sync_req packet = (SBean.emergency_sync_req)ipacket;
		role.syncEmergencyInfos();
	}

	public void onRecv_emergency_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.emergency_enter_req packet = (SBean.emergency_enter_req)ipacket;
		role.startEmergencyMapCopy(packet.activityId);
	}

	public void onRecv_emergency_rank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.emergency_rank_req packet = (SBean.emergency_rank_req)ipacket;
		role.syncEmergencyRank();
	}

	public void onRecv_lucklystar_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lucklystar_sync_req packet = (SBean.lucklystar_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.lucklystar_sync_res(role.syncLucklyStarInfo()));
	}

	public void onRecv_lucklystar_gift_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lucklystar_gift_req packet = (SBean.lucklystar_gift_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.lucklystar_gift_res(role.sendLucklyStarToRole(packet.roleId)));
	}
	
	public void onRecv_fightnpc_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fightnpc_start_req packet = (SBean.fightnpc_start_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.fightnpc_start_res(role.startFightNpcMapCopy()));
	}

	public void onRecv_fightnpc_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fightnpc_reward_req packet = (SBean.fightnpc_reward_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.fightnpc_reward_res(role.takeFightNpcReward()));
	}
	public void onRecv_packetreward_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.packetreward_sync_req packet = (SBean.packetreward_sync_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.packetreward_sync_res(role.syncPacketReward()));
	}

	public void onRecv_packetreward_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.packetreward_take_req packet = (SBean.packetreward_take_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.packetreward_take_res(role.takePacketReward(packet.packetIndex)));
	}

	public void onRecv_horse_enhance_prop_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_enhance_prop_unlock_req packet = (SBean.horse_enhance_prop_unlock_req) ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.horse_enhance_prop_unlock_res(role.unLockEnHanceProp(packet.hid, packet.index) ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
	}

	public void onRecv_buy_wizard_pet_time_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_wizard_pet_time_req packet = (SBean.buy_wizard_pet_time_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.buy_wizard_pet_time_res(role.buyWizardPetTime(packet.petId)));
	}

	public void onRecv_set_cur_wizard_pet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_cur_wizard_pet_req packet = (SBean.set_cur_wizard_pet_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.set_cur_wizard_pet_res(role.setCurWizardPet(packet.petId)));
	}

	public void onRecv_sync_item_unlock_head_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_item_unlock_head_req packet = (SBean.sync_item_unlock_head_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.sync_item_unlock_head_res(role.getUnlockHeads()));
	}

	public void onRecv_socialmsg_send_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_send_req packet = (SBean.socialmsg_send_req)ipacket;
		role.sendSocialComment(sessionid, packet.serverId, packet.serverName, packet.themeType, packet.themeId, packet.comment);
	}

	public void onRecv_socialmsg_like_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_like_req packet = (SBean.socialmsg_like_req)ipacket;
		role.likeSocialComment(sessionid, packet.serverId, packet.serverName, packet.themeType, packet.themeId, packet.commentId);
	}

	public void onRecv_socialmsg_dislike_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_dislike_req packet = (SBean.socialmsg_dislike_req)ipacket;
		role.dislikeSocialComment(sessionid, packet.serverId, packet.serverName, packet.themeType, packet.themeId, packet.commentId);
	}

	public void onRecv_socialmsg_pageinfo_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_pageinfo_req packet = (SBean.socialmsg_pageinfo_req)ipacket;
		role.syncPageSocialComment(sessionid, packet.themeType, packet.themeId, packet.tag, packet.pageNo, packet.len);
	}

	public void onRecv_npc_transfrom_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.npc_transfrom_req packet = (SBean.npc_transfrom_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.npc_transfrom_res(role.npcTransfrom(packet.transfromId)));
	}

//	public void onRecv_activities_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.activities_sync_req packet = (SBean.activities_sync_req) ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.activities_sync_res(role.syncActivityInfo()));
//	}
//
//	public void onRecv_take_monthly_card_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.take_monthly_card_reward_req packet = (SBean.take_monthly_card_reward_req)ipacket;
//		gs.getRPCManager().sendStrPacket(sessionid, new SBean.take_monthly_card_reward_res(role.takeMonthlyCardReward()));
//	}

	public void onRecv_share_success(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.share_success packet = (SBean.share_success)ipacket;
		role.logShareGift();
	}

	public void onRecv_unlock_head_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.unlock_head_req packet = (SBean.unlock_head_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.unlock_head_res(role.unlockHeadIcon(packet.headId)));
	}

	public void onRecv_start_npc_map_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.start_npc_map_req packet = (SBean.start_npc_map_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.start_npc_map_res(role.startNpcMapCopy(packet.mapId)));
	}

	public void onRecv_join_npc_pray_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.join_npc_pray_req packet = (SBean.join_npc_pray_req)ipacket;
		RpcRes<SBean.DummyGoods> res = role.joinNpcPray(packet.prayId, packet.dropId);
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.join_npc_pray_res(res.errCode, res.info));
	}

	public void onRecv_unlock_private_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.unlock_private_warehouse_req packet = (SBean.unlock_private_warehouse_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.unlock_private_warehouse_res(role.unlockWarehouse()));
	}

	public void onRecv_towerdefence_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.towerdefence_start_req packet = (SBean.towerdefence_start_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.towerdefence_start_res(role.startTowerDefenceMapCopy(packet.mapID)));
	}

	public void onRecv_towerdefence_selectcard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.towerdefence_selectcard_req packet = (SBean.towerdefence_selectcard_req)ipacket;
		gs.getRPCManager().sendStrPacket(sessionid, new SBean.towerdefence_selectcard_res(role.selectTowerDefenceMapCopyRewardCard(packet.cardNo)));
	}

//	public void onRecv_sync_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
//	{
//		SBean.sync_warehouse_req packet = (SBean.sync_warehouse_req)ipacket;
//		role.syncWarehouse();
//	}
//
	//// end handlers.

	final GameServer gs;
}
