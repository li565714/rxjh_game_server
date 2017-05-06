// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs;

import ket.util.SStream;
import i3k.SBean;

public class StringChannelHandler
{

	//// begin handlers.
	public void onRecv_client_ping(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.client_ping packet = (SBean.client_ping)ipacket;
		// TODO
	}

	public void onRecv_keep_alive(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.keep_alive packet = (SBean.keep_alive)ipacket;
		// TODO
	}

	public void onRecv_user_login_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.user_login_req packet = (SBean.user_login_req)ipacket;
		// TODO
	}

	public void onRecv_role_logout_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_logout_req packet = (SBean.role_logout_req)ipacket;
		// TODO
	}

	public void onRecv_query_loginqueue_pos(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_loginqueue_pos packet = (SBean.query_loginqueue_pos)ipacket;
		// TODO
	}

	public void onRecv_cancel_loginqueue(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cancel_loginqueue packet = (SBean.cancel_loginqueue)ipacket;
		// TODO
	}

	public void onRecv_role_sync_map(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_sync_map packet = (SBean.role_sync_map)ipacket;
		// TODO
	}

	public void onRecv_msg_send_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.msg_send_req packet = (SBean.msg_send_req)ipacket;
		// TODO
	}

	public void onRecv_master_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_apply_req packet = (SBean.master_apply_req)ipacket;
		// TODO
	}

	public void onRecv_master_offer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_offer_req packet = (SBean.master_offer_req)ipacket;
		// TODO
	}

	public void onRecv_master_accept_offer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_accept_offer_req packet = (SBean.master_accept_offer_req)ipacket;
		// TODO
	}

	public void onRecv_master_accept_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_accept_apply_req packet = (SBean.master_accept_apply_req)ipacket;
		// TODO
	}

	public void onRecv_master_betray_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_betray_req packet = (SBean.master_betray_req)ipacket;
		// TODO
	}

	public void onRecv_master_graduate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_graduate_req packet = (SBean.master_graduate_req)ipacket;
		// TODO
	}

	public void onRecv_master_agree_graduate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_agree_graduate_req packet = (SBean.master_agree_graduate_req)ipacket;
		// TODO
	}

	public void onRecv_master_dismiss_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_dismiss_req packet = (SBean.master_dismiss_req)ipacket;
		// TODO
	}

	public void onRecv_master_get_announce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_get_announce_req packet = (SBean.master_get_announce_req)ipacket;
		// TODO
	}

	public void onRecv_master_set_announce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_set_announce_req packet = (SBean.master_set_announce_req)ipacket;
		// TODO
	}

	public void onRecv_master_del_announce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_del_announce_req packet = (SBean.master_del_announce_req)ipacket;
		// TODO
	}

	public void onRecv_master_info_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_info_req packet = (SBean.master_info_req)ipacket;
		// TODO
	}

	public void onRecv_master_msg_list_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_msg_list_req packet = (SBean.master_msg_list_req)ipacket;
		// TODO
	}

	public void onRecv_master_remove_betray_msg_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_remove_betray_msg_req packet = (SBean.master_remove_betray_msg_req)ipacket;
		// TODO
	}

	public void onRecv_master_list_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_list_req packet = (SBean.master_list_req)ipacket;
		// TODO
	}

	public void onRecv_master_list_apprentice_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_list_apprentice_req packet = (SBean.master_list_apprentice_req)ipacket;
		// TODO
	}

	public void onRecv_master_tasks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_tasks_req packet = (SBean.master_tasks_req)ipacket;
		// TODO
	}

	public void onRecv_master_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_shopsync_req packet = (SBean.master_shopsync_req)ipacket;
		// TODO
	}

	public void onRecv_master_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_shoprefresh_req packet = (SBean.master_shoprefresh_req)ipacket;
		// TODO
	}

	public void onRecv_master_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.master_shopbuy_req packet = (SBean.master_shopbuy_req)ipacket;
		// TODO
	}

	public void onRecv_role_enter_map(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_enter_map packet = (SBean.role_enter_map)ipacket;
		// TODO
	}

	public void onRecv_waypoint_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.waypoint_enter_req packet = (SBean.waypoint_enter_req)ipacket;
		// TODO
	}

	public void onRecv_wrongpos_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.wrongpos_leave_req packet = (SBean.wrongpos_leave_req)ipacket;
		// TODO
	}

	public void onRecv_worldline_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.worldline_sync_req packet = (SBean.worldline_sync_req)ipacket;
		// TODO
	}

	public void onRecv_worldline_change_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.worldline_change_req packet = (SBean.worldline_change_req)ipacket;
		// TODO
	}

	public void onRecv_query_rolebrief_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_rolebrief_req packet = (SBean.query_rolebrief_req)ipacket;
		// TODO
	}

	public void onRecv_query_robot_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_robot_req packet = (SBean.query_robot_req)ipacket;
		// TODO
	}

	public void onRecv_query_rolefeature_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_rolefeature_req packet = (SBean.query_rolefeature_req)ipacket;
		// TODO
	}

	public void onRecv_query_petoverviews_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_petoverviews_req packet = (SBean.query_petoverviews_req)ipacket;
		// TODO
	}

	public void onRecv_query_weaponoverviews_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.query_weaponoverviews_req packet = (SBean.query_weaponoverviews_req)ipacket;
		// TODO
	}

	public void onRecv_role_revive_insitu_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_revive_insitu_req packet = (SBean.role_revive_insitu_req)ipacket;
		// TODO
	}

	public void onRecv_role_revive_other_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_revive_other_req packet = (SBean.role_revive_other_req)ipacket;
		// TODO
	}

	public void onRecv_role_revive_safe_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_revive_safe_req packet = (SBean.role_revive_safe_req)ipacket;
		// TODO
	}

	public void onRecv_role_transform_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_transform_req packet = (SBean.role_transform_req)ipacket;
		// TODO
	}

	public void onRecv_checkin_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.checkin_sync_req packet = (SBean.checkin_sync_req)ipacket;
		// TODO
	}

	public void onRecv_checkin_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.checkin_take_req packet = (SBean.checkin_take_req)ipacket;
		// TODO
	}

	public void onRecv_mall_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mall_sync_req packet = (SBean.mall_sync_req)ipacket;
		// TODO
	}

	public void onRecv_mall_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mall_buy_req packet = (SBean.mall_buy_req)ipacket;
		// TODO
	}

	public void onRecv_benefit_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.benefit_sync_req packet = (SBean.benefit_sync_req)ipacket;
		// TODO
	}

	public void onRecv_payactivity_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.payactivity_sync_req packet = (SBean.payactivity_sync_req)ipacket;
		// TODO
	}

	public void onRecv_firstpaygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.firstpaygift_sync_req packet = (SBean.firstpaygift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_firstpaygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.firstpaygift_take_req packet = (SBean.firstpaygift_take_req)ipacket;
		// TODO
	}

	public void onRecv_dailypaygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dailypaygift_sync_req packet = (SBean.dailypaygift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_dailypaygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dailypaygift_take_req packet = (SBean.dailypaygift_take_req)ipacket;
		// TODO
	}

	public void onRecv_lastpaygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lastpaygift_sync_req packet = (SBean.lastpaygift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_lastpaygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lastpaygift_take_req packet = (SBean.lastpaygift_take_req)ipacket;
		// TODO
	}

	public void onRecv_activitychallengegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitychallengegift_sync_req packet = (SBean.activitychallengegift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_activitychallengegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitychallengegift_take_req packet = (SBean.activitychallengegift_take_req)ipacket;
		// TODO
	}

	public void onRecv_upgradepurchase_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradepurchase_sync_req packet = (SBean.upgradepurchase_sync_req)ipacket;
		// TODO
	}

	public void onRecv_upgradepurchase_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradepurchase_buy_req packet = (SBean.upgradepurchase_buy_req)ipacket;
		// TODO
	}

	public void onRecv_paygift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.paygift_sync_req packet = (SBean.paygift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_paygift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.paygift_take_req packet = (SBean.paygift_take_req)ipacket;
		// TODO
	}

	public void onRecv_consumegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.consumegift_sync_req packet = (SBean.consumegift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_consumegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.consumegift_take_req packet = (SBean.consumegift_take_req)ipacket;
		// TODO
	}

	public void onRecv_upgradegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradegift_sync_req packet = (SBean.upgradegift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_upgradegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.upgradegift_take_req packet = (SBean.upgradegift_take_req)ipacket;
		// TODO
	}

	public void onRecv_investmentfund_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.investmentfund_sync_req packet = (SBean.investmentfund_sync_req)ipacket;
		// TODO
	}

	public void onRecv_investmentfund_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.investmentfund_buy_req packet = (SBean.investmentfund_buy_req)ipacket;
		// TODO
	}

	public void onRecv_investmentfund_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.investmentfund_take_req packet = (SBean.investmentfund_take_req)ipacket;
		// TODO
	}

	public void onRecv_growthfund_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.growthfund_sync_req packet = (SBean.growthfund_sync_req)ipacket;
		// TODO
	}

	public void onRecv_growthfund_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.growthfund_buy_req packet = (SBean.growthfund_buy_req)ipacket;
		// TODO
	}

	public void onRecv_growthfund_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.growthfund_take_req packet = (SBean.growthfund_take_req)ipacket;
		// TODO
	}

	public void onRecv_doubledrop_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.doubledrop_sync_req packet = (SBean.doubledrop_sync_req)ipacket;
		// TODO
	}

	public void onRecv_extradrop_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.extradrop_sync_req packet = (SBean.extradrop_sync_req)ipacket;
		// TODO
	}

	public void onRecv_exchangegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.exchangegift_sync_req packet = (SBean.exchangegift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_exchangegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.exchangegift_take_req packet = (SBean.exchangegift_take_req)ipacket;
		// TODO
	}

	public void onRecv_logingift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.logingift_sync_req packet = (SBean.logingift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_logingift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.logingift_take_req packet = (SBean.logingift_take_req)ipacket;
		// TODO
	}

	public void onRecv_giftpackage_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.giftpackage_sync_req packet = (SBean.giftpackage_sync_req)ipacket;
		// TODO
	}

	public void onRecv_giftpackage_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.giftpackage_take_req packet = (SBean.giftpackage_take_req)ipacket;
		// TODO
	}

	public void onRecv_pbtcashback_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pbtcashback_sync_req packet = (SBean.pbtcashback_sync_req)ipacket;
		// TODO
	}

	public void onRecv_pbtcashback_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pbtcashback_take_req packet = (SBean.pbtcashback_take_req)ipacket;
		// TODO
	}

	public void onRecv_payrank_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.payrank_sync_req packet = (SBean.payrank_sync_req)ipacket;
		// TODO
	}

	public void onRecv_groupbuy_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.groupbuy_sync_req packet = (SBean.groupbuy_sync_req)ipacket;
		// TODO
	}

	public void onRecv_groupbuy_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.groupbuy_buy_req packet = (SBean.groupbuy_buy_req)ipacket;
		// TODO
	}

	public void onRecv_flashsale_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.flashsale_sync_req packet = (SBean.flashsale_sync_req)ipacket;
		// TODO
	}

	public void onRecv_flashsale_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.flashsale_buy_req packet = (SBean.flashsale_buy_req)ipacket;
		// TODO
	}

	public void onRecv_luckyroll_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckyroll_sync_req packet = (SBean.luckyroll_sync_req)ipacket;
		// TODO
	}

	public void onRecv_luckyroll_play_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckyroll_play_req packet = (SBean.luckyroll_play_req)ipacket;
		// TODO
	}

	public void onRecv_directpurchase_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.directpurchase_sync_req packet = (SBean.directpurchase_sync_req)ipacket;
		// TODO
	}

	public void onRecv_directpurchase_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.directpurchase_take_req packet = (SBean.directpurchase_take_req)ipacket;
		// TODO
	}

	public void onRecv_onearmbandit_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onearmbandit_sync_req packet = (SBean.onearmbandit_sync_req)ipacket;
		// TODO
	}

	public void onRecv_onearmbandit_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onearmbandit_take_req packet = (SBean.onearmbandit_take_req)ipacket;
		// TODO
	}

	public void onRecv_adver_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.adver_sync_req packet = (SBean.adver_sync_req)ipacket;
		// TODO
	}

	public void onRecv_mapcopy_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mapcopy_leave_req packet = (SBean.mapcopy_leave_req)ipacket;
		// TODO
	}

	public void onRecv_normalmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.normalmap_start_req packet = (SBean.normalmap_start_req)ipacket;
		// TODO
	}

	public void onRecv_activitymap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitymap_start_req packet = (SBean.activitymap_start_req)ipacket;
		// TODO
	}

	public void onRecv_commonmap_selectcard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.commonmap_selectcard_req packet = (SBean.commonmap_selectcard_req)ipacket;
		// TODO
	}

	public void onRecv_normalmap_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.normalmap_buytimes_req packet = (SBean.normalmap_buytimes_req)ipacket;
		// TODO
	}

	public void onRecv_activitymap_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activitymap_buytimes_req packet = (SBean.activitymap_buytimes_req)ipacket;
		// TODO
	}

	public void onRecv_privatemap_sweep_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_sweep_req packet = (SBean.privatemap_sweep_req)ipacket;
		// TODO
	}

	public void onRecv_activity_sweep_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activity_sweep_req packet = (SBean.activity_sweep_req)ipacket;
		// TODO
	}

	public void onRecv_mail_syncsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_syncsys_req packet = (SBean.mail_syncsys_req)ipacket;
		// TODO
	}

	public void onRecv_mail_synctmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_synctmp_req packet = (SBean.mail_synctmp_req)ipacket;
		// TODO
	}

	public void onRecv_mail_read_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_read_req packet = (SBean.mail_read_req)ipacket;
		// TODO
	}

	public void onRecv_mail_readsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_readsys_req packet = (SBean.mail_readsys_req)ipacket;
		// TODO
	}

	public void onRecv_mail_readtmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_readtmp_req packet = (SBean.mail_readtmp_req)ipacket;
		// TODO
	}

	public void onRecv_mail_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_take_req packet = (SBean.mail_take_req)ipacket;
		// TODO
	}

	public void onRecv_mail_takesys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_takesys_req packet = (SBean.mail_takesys_req)ipacket;
		// TODO
	}

	public void onRecv_mail_taketmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_taketmp_req packet = (SBean.mail_taketmp_req)ipacket;
		// TODO
	}

	public void onRecv_mail_del_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_del_req packet = (SBean.mail_del_req)ipacket;
		// TODO
	}

	public void onRecv_mail_delsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_delsys_req packet = (SBean.mail_delsys_req)ipacket;
		// TODO
	}

	public void onRecv_mail_deltmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_deltmp_req packet = (SBean.mail_deltmp_req)ipacket;
		// TODO
	}

	public void onRecv_mail_takeallsys_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_takeallsys_req packet = (SBean.mail_takeallsys_req)ipacket;
		// TODO
	}

	public void onRecv_mail_takealltmp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mail_takealltmp_req packet = (SBean.mail_takealltmp_req)ipacket;
		// TODO
	}

	public void onRecv_rollnotice_query(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rollnotice_query packet = (SBean.rollnotice_query)ipacket;
		// TODO
	}

	public void onRecv_rank_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rank_sync_req packet = (SBean.rank_sync_req)ipacket;
		// TODO
	}

	public void onRecv_sectrank_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectrank_sync_req packet = (SBean.sectrank_sync_req)ipacket;
		// TODO
	}

	public void onRecv_rank_get_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rank_get_req packet = (SBean.rank_get_req)ipacket;
		// TODO
	}

	public void onRecv_sectrank_get_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectrank_get_req packet = (SBean.sectrank_get_req)ipacket;
		// TODO
	}

	public void onRecv_rank_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rank_self_req packet = (SBean.rank_self_req)ipacket;
		// TODO
	}

	public void onRecv_sectrank_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectrank_self_req packet = (SBean.sectrank_self_req)ipacket;
		// TODO
	}

	public void onRecv_buy_coin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_coin_req packet = (SBean.buy_coin_req)ipacket;
		// TODO
	}

	public void onRecv_buy_vit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_vit_req packet = (SBean.buy_vit_req)ipacket;
		// TODO
	}

	public void onRecv_pay_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pay_sync_req packet = (SBean.pay_sync_req)ipacket;
		// TODO
	}

	public void onRecv_pay_asgod_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pay_asgod_req packet = (SBean.pay_asgod_req)ipacket;
		// TODO
	}

	public void onRecv_vip_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.vip_take_req packet = (SBean.vip_take_req)ipacket;
		// TODO
	}

	public void onRecv_bag_expand_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_expand_req packet = (SBean.bag_expand_req)ipacket;
		// TODO
	}

	public void onRecv_bag_sellequip_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellequip_req packet = (SBean.bag_sellequip_req)ipacket;
		// TODO
	}

	public void onRecv_bag_sellitem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellitem_req packet = (SBean.bag_sellitem_req)ipacket;
		// TODO
	}

	public void onRecv_bag_sellgem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellgem_req packet = (SBean.bag_sellgem_req)ipacket;
		// TODO
	}

	public void onRecv_bag_sellbook_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_sellbook_req packet = (SBean.bag_sellbook_req)ipacket;
		// TODO
	}

	public void onRecv_bag_batchsellequips_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellequips_req packet = (SBean.bag_batchsellequips_req)ipacket;
		// TODO
	}

	public void onRecv_bag_batchsellitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellitems_req packet = (SBean.bag_batchsellitems_req)ipacket;
		// TODO
	}

	public void onRecv_bag_batchsellgems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellgems_req packet = (SBean.bag_batchsellgems_req)ipacket;
		// TODO
	}

	public void onRecv_bag_batchsellbooks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_batchsellbooks_req packet = (SBean.bag_batchsellbooks_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemgift_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemgift_req packet = (SBean.bag_useitemgift_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemcoin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemcoin_req packet = (SBean.bag_useitemcoin_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemdiamond_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemdiamond_req packet = (SBean.bag_useitemdiamond_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemexp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemexp_req packet = (SBean.bag_useitemexp_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemhp_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemhp_req packet = (SBean.bag_useitemhp_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemhppool_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemhppool_req packet = (SBean.bag_useitemhppool_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemchest_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemchest_req packet = (SBean.bag_useitemchest_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemequipenergy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemequipenergy_req packet = (SBean.bag_useitemequipenergy_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemgemenergy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemgemenergy_req packet = (SBean.bag_useitemgemenergy_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useiteminspiration_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useiteminspiration_req packet = (SBean.bag_useiteminspiration_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemvit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemvit_req packet = (SBean.bag_useitemvit_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemfashion_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemfashion_req packet = (SBean.bag_useitemfashion_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemexpcoinpool_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemexpcoinpool_req packet = (SBean.bag_useitemexpcoinpool_req)ipacket;
		// TODO
	}

	public void onRecv_bag_usemonthlycard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_usemonthlycard_req packet = (SBean.bag_usemonthlycard_req)ipacket;
		// TODO
	}

	public void onRecv_bag_usevipcard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_usevipcard_req packet = (SBean.bag_usevipcard_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemfeat_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemfeat_req packet = (SBean.bag_useitemfeat_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemskill_req packet = (SBean.bag_useitemskill_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemletter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemletter_req packet = (SBean.bag_useitemletter_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemevil_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemevil_req packet = (SBean.bag_useitemevil_req)ipacket;
		// TODO
	}

	public void onRecv_bag_piececompose_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_piececompose_req packet = (SBean.bag_piececompose_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitempropstrength_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitempropstrength_req packet = (SBean.bag_useitempropstrength_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemofflinefuncpoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemofflinefuncpoint_req packet = (SBean.bag_useitemofflinefuncpoint_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemtitle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemtitle_req packet = (SBean.bag_useitemtitle_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemuskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemuskill_req packet = (SBean.bag_useitemuskill_req)ipacket;
		// TODO
	}

	public void onRecv_bag_useitemhead_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_useitemhead_req packet = (SBean.bag_useitemhead_req)ipacket;
		// TODO
	}

	public void onRecv_equip_upwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_upwear_req packet = (SBean.equip_upwear_req)ipacket;
		// TODO
	}

	public void onRecv_equip_downwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_downwear_req packet = (SBean.equip_downwear_req)ipacket;
		// TODO
	}

	public void onRecv_equip_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_levelup_req packet = (SBean.equip_levelup_req)ipacket;
		// TODO
	}

	public void onRecv_equip_batchlevelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_batchlevelup_req packet = (SBean.equip_batchlevelup_req)ipacket;
		// TODO
	}

	public void onRecv_equip_starup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_starup_req packet = (SBean.equip_starup_req)ipacket;
		// TODO
	}

	public void onRecv_equip_repair_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_repair_req packet = (SBean.equip_repair_req)ipacket;
		// TODO
	}

	public void onRecv_equip_autoupwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_autoupwear_req packet = (SBean.equip_autoupwear_req)ipacket;
		// TODO
	}

	public void onRecv_gem_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.gem_levelup_req packet = (SBean.gem_levelup_req)ipacket;
		// TODO
	}

	public void onRecv_gem_inlay_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.gem_inlay_req packet = (SBean.gem_inlay_req)ipacket;
		// TODO
	}

	public void onRecv_gem_unlay_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.gem_unlay_req packet = (SBean.gem_unlay_req)ipacket;
		// TODO
	}

	public void onRecv_equip_refine_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.equip_refine_req packet = (SBean.equip_refine_req)ipacket;
		// TODO
	}

	public void onRecv_legend_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_sync_req packet = (SBean.legend_sync_req)ipacket;
		// TODO
	}

	public void onRecv_legend_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_make_req packet = (SBean.legend_make_req)ipacket;
		// TODO
	}

	public void onRecv_legend_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_quit_req packet = (SBean.legend_quit_req)ipacket;
		// TODO
	}

	public void onRecv_legend_save_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.legend_save_req packet = (SBean.legend_save_req)ipacket;
		// TODO
	}

	public void onRecv_skill_select_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_select_req packet = (SBean.skill_select_req)ipacket;
		// TODO
	}

	public void onRecv_skill_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_levelup_req packet = (SBean.skill_levelup_req)ipacket;
		// TODO
	}

	public void onRecv_skill_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_enhance_req packet = (SBean.skill_enhance_req)ipacket;
		// TODO
	}

	public void onRecv_skill_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.skill_unlock_req packet = (SBean.skill_unlock_req)ipacket;
		// TODO
	}

	public void onRecv_uniqueskill_set_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.uniqueskill_set_req packet = (SBean.uniqueskill_set_req)ipacket;
		// TODO
	}

	public void onRecv_spirit_learn_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_learn_req packet = (SBean.spirit_learn_req)ipacket;
		// TODO
	}

	public void onRecv_spirit_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_levelup_req packet = (SBean.spirit_levelup_req)ipacket;
		// TODO
	}

	public void onRecv_spirit_install_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_install_req packet = (SBean.spirit_install_req)ipacket;
		// TODO
	}

	public void onRecv_spirit_uninstall_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.spirit_uninstall_req packet = (SBean.spirit_uninstall_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_make_req packet = (SBean.weapon_make_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_levelup_req packet = (SBean.weapon_levelup_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_buylevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_buylevel_req packet = (SBean.weapon_buylevel_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_starup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_starup_req packet = (SBean.weapon_starup_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_select_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_select_req packet = (SBean.weapon_select_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_sync_req packet = (SBean.weapon_sync_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_uskill_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_uskill_open_req packet = (SBean.weapon_uskill_open_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_setform_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_setform_req packet = (SBean.weapon_setform_req)ipacket;
		// TODO
	}

	public void onRecv_weaponmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weaponmap_start_req packet = (SBean.weaponmap_start_req)ipacket;
		// TODO
	}

	public void onRecv_pet_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_sync_req packet = (SBean.pet_sync_req)ipacket;
		// TODO
	}

	public void onRecv_pet_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_make_req packet = (SBean.pet_make_req)ipacket;
		// TODO
	}

	public void onRecv_pet_transform_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_transform_req packet = (SBean.pet_transform_req)ipacket;
		// TODO
	}

	public void onRecv_pet_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_levelup_req packet = (SBean.pet_levelup_req)ipacket;
		// TODO
	}

	public void onRecv_pet_buylevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_buylevel_req packet = (SBean.pet_buylevel_req)ipacket;
		// TODO
	}

	public void onRecv_pet_starup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_starup_req packet = (SBean.pet_starup_req)ipacket;
		// TODO
	}

	public void onRecv_pet_breakskillvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_breakskillvlup_req packet = (SBean.pet_breakskillvlup_req)ipacket;
		// TODO
	}

	public void onRecv_pet_worldmapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_worldmapset_req packet = (SBean.pet_worldmapset_req)ipacket;
		// TODO
	}

	public void onRecv_pet_privatemapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_privatemapset_req packet = (SBean.pet_privatemapset_req)ipacket;
		// TODO
	}

	public void onRecv_pet_sectmapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_sectmapset_req packet = (SBean.pet_sectmapset_req)ipacket;
		// TODO
	}

	public void onRecv_pet_activitymapset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_activitymapset_req packet = (SBean.pet_activitymapset_req)ipacket;
		// TODO
	}

	public void onRecv_lifetaskmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lifetaskmap_start_req packet = (SBean.lifetaskmap_start_req)ipacket;
		// TODO
	}

	public void onRecv_petspirit_lvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petspirit_lvlup_req packet = (SBean.petspirit_lvlup_req)ipacket;
		// TODO
	}

	public void onRecv_petspirit_learn_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petspirit_learn_req packet = (SBean.petspirit_learn_req)ipacket;
		// TODO
	}

	public void onRecv_petspirit_replace_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petspirit_replace_req packet = (SBean.petspirit_replace_req)ipacket;
		// TODO
	}

	public void onRecv_dtask_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dtask_sync_req packet = (SBean.dtask_sync_req)ipacket;
		// TODO
	}

	public void onRecv_dtask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dtask_take_req packet = (SBean.dtask_take_req)ipacket;
		// TODO
	}

	public void onRecv_chtask_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.chtask_sync_req packet = (SBean.chtask_sync_req)ipacket;
		// TODO
	}

	public void onRecv_chtask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.chtask_take_req packet = (SBean.chtask_take_req)ipacket;
		// TODO
	}

	public void onRecv_fame_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fame_sync_req packet = (SBean.fame_sync_req)ipacket;
		// TODO
	}

	public void onRecv_fame_upgrade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fame_upgrade_req packet = (SBean.fame_upgrade_req)ipacket;
		// TODO
	}

	public void onRecv_fame_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fame_take_req packet = (SBean.fame_take_req)ipacket;
		// TODO
	}

	public void onRecv_onlinegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onlinegift_sync_req packet = (SBean.onlinegift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_onlinegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.onlinegift_take_req packet = (SBean.onlinegift_take_req)ipacket;
		// TODO
	}

	public void onRecv_offlineexp_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.offlineexp_take_req packet = (SBean.offlineexp_take_req)ipacket;
		// TODO
	}

	public void onRecv_quizgift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.quizgift_sync_req packet = (SBean.quizgift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_quizgift_qrank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.quizgift_qrank_req packet = (SBean.quizgift_qrank_req)ipacket;
		// TODO
	}

	public void onRecv_quizgift_answer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.quizgift_answer_req packet = (SBean.quizgift_answer_req)ipacket;
		// TODO
	}

	public void onRecv_sync_luckywheel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_luckywheel_req packet = (SBean.sync_luckywheel_req)ipacket;
		// TODO
	}

	public void onRecv_luckywheel_ondraw_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckywheel_ondraw_req packet = (SBean.luckywheel_ondraw_req)ipacket;
		// TODO
	}

	public void onRecv_luckywheel_buydrawtimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.luckywheel_buydrawtimes_req packet = (SBean.luckywheel_buydrawtimes_req)ipacket;
		// TODO
	}

	public void onRecv_play_firework_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.play_firework_req packet = (SBean.play_firework_req)ipacket;
		// TODO
	}

	public void onRecv_redenvelope_snatch_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.redenvelope_snatch_req packet = (SBean.redenvelope_snatch_req)ipacket;
		// TODO
	}

	public void onRecv_mtask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mtask_take_req packet = (SBean.mtask_take_req)ipacket;
		// TODO
	}

	public void onRecv_mtask_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mtask_quit_req packet = (SBean.mtask_quit_req)ipacket;
		// TODO
	}

	public void onRecv_mtask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mtask_reward_req packet = (SBean.mtask_reward_req)ipacket;
		// TODO
	}

	public void onRecv_wtask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.wtask_reward_req packet = (SBean.wtask_reward_req)ipacket;
		// TODO
	}

	public void onRecv_ptask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.ptask_reward_req packet = (SBean.ptask_reward_req)ipacket;
		// TODO
	}

	public void onRecv_petlifetask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petlifetask_take_req packet = (SBean.petlifetask_take_req)ipacket;
		// TODO
	}

	public void onRecv_petlifetask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petlifetask_reward_req packet = (SBean.petlifetask_reward_req)ipacket;
		// TODO
	}

	public void onRecv_task_useitem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_useitem_req packet = (SBean.task_useitem_req)ipacket;
		// TODO
	}

	public void onRecv_task_submititem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_submititem_req packet = (SBean.task_submititem_req)ipacket;
		// TODO
	}

	public void onRecv_pettask_submititem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pettask_submititem_req packet = (SBean.pettask_submititem_req)ipacket;
		// TODO
	}

	public void onRecv_petlifetask_submititem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.petlifetask_submititem_req packet = (SBean.petlifetask_submititem_req)ipacket;
		// TODO
	}

	public void onRecv_task_dialog_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_dialog_req packet = (SBean.task_dialog_req)ipacket;
		// TODO
	}

	public void onRecv_task_conveynpc_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_conveynpc_req packet = (SBean.task_conveynpc_req)ipacket;
		// TODO
	}

	public void onRecv_task_conveyitem_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_conveyitem_req packet = (SBean.task_conveyitem_req)ipacket;
		// TODO
	}

	public void onRecv_task_answer_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_answer_req packet = (SBean.task_answer_req)ipacket;
		// TODO
	}

	public void onRecv_task_randquestion_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.task_randquestion_req packet = (SBean.task_randquestion_req)ipacket;
		// TODO
	}

	public void onRecv_pet_revive_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_revive_req packet = (SBean.pet_revive_req)ipacket;
		// TODO
	}

	public void onRecv_role_mine_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_mine_req packet = (SBean.role_mine_req)ipacket;
		// TODO
	}

	public void onRecv_set_attackmode_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_attackmode_req packet = (SBean.set_attackmode_req)ipacket;
		// TODO
	}

	public void onRecv_team_query_member(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_query_member packet = (SBean.team_query_member)ipacket;
		// TODO
	}

	public void onRecv_team_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_invite_req packet = (SBean.team_invite_req)ipacket;
		// TODO
	}

	public void onRecv_team_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_invitedby_req packet = (SBean.team_invitedby_req)ipacket;
		// TODO
	}

	public void onRecv_team_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_apply_req packet = (SBean.team_apply_req)ipacket;
		// TODO
	}

	public void onRecv_team_appliedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_appliedby_req packet = (SBean.team_appliedby_req)ipacket;
		// TODO
	}

	public void onRecv_team_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_leave_req packet = (SBean.team_leave_req)ipacket;
		// TODO
	}

	public void onRecv_team_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_kick_req packet = (SBean.team_kick_req)ipacket;
		// TODO
	}

	public void onRecv_team_dissolve_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_dissolve_req packet = (SBean.team_dissolve_req)ipacket;
		// TODO
	}

	public void onRecv_team_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_change_leader_req packet = (SBean.team_change_leader_req)ipacket;
		// TODO
	}

	public void onRecv_team_role_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_role_query_req packet = (SBean.team_role_query_req)ipacket;
		// TODO
	}

	public void onRecv_team_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_self_req packet = (SBean.team_self_req)ipacket;
		// TODO
	}

	public void onRecv_team_mapt_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_mapt_req packet = (SBean.team_mapt_req)ipacket;
		// TODO
	}

	public void onRecv_team_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.team_mapr_req packet = (SBean.team_mapr_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_create_req packet = (SBean.mroom_create_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_enter_req packet = (SBean.mroom_enter_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_qenter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_qenter_req packet = (SBean.mroom_qenter_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_invite_req packet = (SBean.mroom_invite_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_invitedby_req packet = (SBean.mroom_invitedby_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_leave_req packet = (SBean.mroom_leave_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_kick_req packet = (SBean.mroom_kick_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_change_leader_req packet = (SBean.mroom_change_leader_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_self_req packet = (SBean.mroom_self_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_mapr_req packet = (SBean.mroom_mapr_req)ipacket;
		// TODO
	}

	public void onRecv_mroom_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mroom_query_req packet = (SBean.mroom_query_req)ipacket;
		// TODO
	}

	public void onRecv_sect_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_sync_req packet = (SBean.sect_sync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_create_req packet = (SBean.sect_create_req)ipacket;
		// TODO
	}

	public void onRecv_sect_queryapplied_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_queryapplied_req packet = (SBean.sect_queryapplied_req)ipacket;
		// TODO
	}

	public void onRecv_sect_list_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_list_req packet = (SBean.sect_list_req)ipacket;
		// TODO
	}

	public void onRecv_sect_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_query_req packet = (SBean.sect_query_req)ipacket;
		// TODO
	}

	public void onRecv_sect_searchbyid_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_searchbyid_req packet = (SBean.sect_searchbyid_req)ipacket;
		// TODO
	}

	public void onRecv_sect_searchbyname_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_searchbyname_req packet = (SBean.sect_searchbyname_req)ipacket;
		// TODO
	}

	public void onRecv_sect_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_apply_req packet = (SBean.sect_apply_req)ipacket;
		// TODO
	}

	public void onRecv_sect_qapply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_qapply_req packet = (SBean.sect_qapply_req)ipacket;
		// TODO
	}

	public void onRecv_sect_members_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_members_req packet = (SBean.sect_members_req)ipacket;
		// TODO
	}

	public void onRecv_sect_applications_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_applications_req packet = (SBean.sect_applications_req)ipacket;
		// TODO
	}

	public void onRecv_sect_history_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_history_req packet = (SBean.sect_history_req)ipacket;
		// TODO
	}

	public void onRecv_sect_appliedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_appliedby_req packet = (SBean.sect_appliedby_req)ipacket;
		// TODO
	}

	public void onRecv_sect_appliedbyall_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_appliedbyall_req packet = (SBean.sect_appliedbyall_req)ipacket;
		// TODO
	}

	public void onRecv_sect_appoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_appoint_req packet = (SBean.sect_appoint_req)ipacket;
		// TODO
	}

	public void onRecv_sect_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_kick_req packet = (SBean.sect_kick_req)ipacket;
		// TODO
	}

	public void onRecv_sect_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_leave_req packet = (SBean.sect_leave_req)ipacket;
		// TODO
	}

	public void onRecv_sect_disband_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_disband_req packet = (SBean.sect_disband_req)ipacket;
		// TODO
	}

	public void onRecv_sect_changecreed_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_changecreed_req packet = (SBean.sect_changecreed_req)ipacket;
		// TODO
	}

	public void onRecv_sect_changename_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_changename_req packet = (SBean.sect_changename_req)ipacket;
		// TODO
	}

	public void onRecv_sect_changeicon_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_changeicon_req packet = (SBean.sect_changeicon_req)ipacket;
		// TODO
	}

	public void onRecv_sect_joinlvl_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_joinlvl_req packet = (SBean.sect_joinlvl_req)ipacket;
		// TODO
	}

	public void onRecv_sect_sendemail_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_sendemail_req packet = (SBean.sect_sendemail_req)ipacket;
		// TODO
	}

	public void onRecv_sect_upgrade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_upgrade_req packet = (SBean.sect_upgrade_req)ipacket;
		// TODO
	}

	public void onRecv_sect_accelerate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_accelerate_req packet = (SBean.sect_accelerate_req)ipacket;
		// TODO
	}

	public void onRecv_sect_aurasync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_aurasync_req packet = (SBean.sect_aurasync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_auraexpadd_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_auraexpadd_req packet = (SBean.sect_auraexpadd_req)ipacket;
		// TODO
	}

	public void onRecv_sect_worship_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_worship_req packet = (SBean.sect_worship_req)ipacket;
		// TODO
	}

	public void onRecv_sect_syncworshipreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_syncworshipreward_req packet = (SBean.sect_syncworshipreward_req)ipacket;
		// TODO
	}

	public void onRecv_sect_takeworshipreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_takeworshipreward_req packet = (SBean.sect_takeworshipreward_req)ipacket;
		// TODO
	}

	public void onRecv_sect_openbanquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_openbanquet_req packet = (SBean.sect_openbanquet_req)ipacket;
		// TODO
	}

	public void onRecv_sect_listbanquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_listbanquet_req packet = (SBean.sect_listbanquet_req)ipacket;
		// TODO
	}

	public void onRecv_sect_joinbanquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_joinbanquet_req packet = (SBean.sect_joinbanquet_req)ipacket;
		// TODO
	}

	public void onRecv_sect_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_shopsync_req packet = (SBean.sect_shopsync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_shoprefresh_req packet = (SBean.sect_shoprefresh_req)ipacket;
		// TODO
	}

	public void onRecv_sect_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_shopbuy_req packet = (SBean.sect_shopbuy_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_start_req packet = (SBean.sectmap_start_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_status_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_status_req packet = (SBean.sectmap_status_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_query_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_query_req packet = (SBean.sectmap_query_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_open_req packet = (SBean.sectmap_open_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_rewards_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_rewards_req packet = (SBean.sectmap_rewards_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_apply_req packet = (SBean.sectmap_apply_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_allocation_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_allocation_req packet = (SBean.sectmap_allocation_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_damage_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_damage_req packet = (SBean.sectmap_damage_req)ipacket;
		// TODO
	}

	public void onRecv_sectmap_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sectmap_sync_req packet = (SBean.sectmap_sync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_task_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_sync_req packet = (SBean.sect_task_sync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_share_task_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_share_task_sync_req packet = (SBean.sect_share_task_sync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_finish_task_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_finish_task_sync_req packet = (SBean.sect_finish_task_sync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_task_receive_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_receive_req packet = (SBean.sect_task_receive_req)ipacket;
		// TODO
	}

	public void onRecv_sect_task_cancel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_cancel_req packet = (SBean.sect_task_cancel_req)ipacket;
		// TODO
	}

	public void onRecv_sect_task_finish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_finish_req packet = (SBean.sect_task_finish_req)ipacket;
		// TODO
	}

	public void onRecv_sect_task_issuance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_issuance_req packet = (SBean.sect_task_issuance_req)ipacket;
		// TODO
	}

	public void onRecv_sect_task_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_reset_req packet = (SBean.sect_task_reset_req)ipacket;
		// TODO
	}

	public void onRecv_sect_task_done_rewards_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_task_done_rewards_req packet = (SBean.sect_task_done_rewards_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_sync_req packet = (SBean.diyskill_sync_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_create_req packet = (SBean.diyskill_create_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_save_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_save_req packet = (SBean.diyskill_save_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_discard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_discard_req packet = (SBean.diyskill_discard_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_selectuse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_selectuse_req packet = (SBean.diyskill_selectuse_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_canceluse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_canceluse_req packet = (SBean.diyskill_canceluse_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_unlock_req packet = (SBean.diyskill_unlock_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_buytimes_req packet = (SBean.diyskill_buytimes_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_share_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_share_req packet = (SBean.diyskill_share_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_cancelshare_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_cancelshare_req packet = (SBean.diyskill_cancelshare_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_borrow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_borrow_req packet = (SBean.diyskill_borrow_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_flaunt_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_flaunt_req packet = (SBean.diyskill_flaunt_req)ipacket;
		// TODO
	}

	public void onRecv_diyskill_shareaward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.diyskill_shareaward_req packet = (SBean.diyskill_shareaward_req)ipacket;
		// TODO
	}

	public void onRecv_sync_pet_can_use_pool(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_pet_can_use_pool packet = (SBean.sync_pet_can_use_pool)ipacket;
		// TODO
	}

	public void onRecv_sect_push_application_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_push_application_req packet = (SBean.sect_push_application_req)ipacket;
		// TODO
	}

	public void onRecv_suite_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.suite_buy_req packet = (SBean.suite_buy_req)ipacket;
		// TODO
	}

	public void onRecv_store_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.store_buy_req packet = (SBean.store_buy_req)ipacket;
		// TODO
	}

	public void onRecv_teleport_npc_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.teleport_npc_req packet = (SBean.teleport_npc_req)ipacket;
		// TODO
	}

	public void onRecv_teleport_monster_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.teleport_monster_req packet = (SBean.teleport_monster_req)ipacket;
		// TODO
	}

	public void onRecv_teleport_mineral_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.teleport_mineral_req packet = (SBean.teleport_mineral_req)ipacket;
		// TODO
	}

	public void onRecv_arena_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_sync_req packet = (SBean.arena_sync_req)ipacket;
		// TODO
	}

	public void onRecv_arena_setpets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_setpets_req packet = (SBean.arena_setpets_req)ipacket;
		// TODO
	}

	public void onRecv_arena_ranks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_ranks_req packet = (SBean.arena_ranks_req)ipacket;
		// TODO
	}

	public void onRecv_arena_refresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_refresh_req packet = (SBean.arena_refresh_req)ipacket;
		// TODO
	}

	public void onRecv_arena_defencepets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_defencepets_req packet = (SBean.arena_defencepets_req)ipacket;
		// TODO
	}

	public void onRecv_arena_resetcool_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_resetcool_req packet = (SBean.arena_resetcool_req)ipacket;
		// TODO
	}

	public void onRecv_arena_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_buytimes_req packet = (SBean.arena_buytimes_req)ipacket;
		// TODO
	}

	public void onRecv_arena_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_shopsync_req packet = (SBean.arena_shopsync_req)ipacket;
		// TODO
	}

	public void onRecv_arena_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_shoprefresh_req packet = (SBean.arena_shoprefresh_req)ipacket;
		// TODO
	}

	public void onRecv_arena_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_shopbuy_req packet = (SBean.arena_shopbuy_req)ipacket;
		// TODO
	}

	public void onRecv_arena_startattack_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_startattack_req packet = (SBean.arena_startattack_req)ipacket;
		// TODO
	}

	public void onRecv_arena_log_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_log_req packet = (SBean.arena_log_req)ipacket;
		// TODO
	}

	public void onRecv_arena_scoresync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_scoresync_req packet = (SBean.arena_scoresync_req)ipacket;
		// TODO
	}

	public void onRecv_arena_takescore_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_takescore_req packet = (SBean.arena_takescore_req)ipacket;
		// TODO
	}

	public void onRecv_arena_hidedefence_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.arena_hidedefence_req packet = (SBean.arena_hidedefence_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_sync_req packet = (SBean.superarena_sync_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_setpets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_setpets_req packet = (SBean.superarena_setpets_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_singlejoin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_singlejoin_req packet = (SBean.superarena_singlejoin_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_teamjoin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_teamjoin_req packet = (SBean.superarena_teamjoin_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_quit_req packet = (SBean.superarena_quit_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_shopsync_req packet = (SBean.superarena_shopsync_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_shoprefresh_req packet = (SBean.superarena_shoprefresh_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_shopbuy_req packet = (SBean.superarena_shopbuy_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_create_req packet = (SBean.aroom_create_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_invite_req packet = (SBean.aroom_invite_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_invitedby_req packet = (SBean.aroom_invitedby_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_leave_req packet = (SBean.aroom_leave_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_kick_req packet = (SBean.aroom_kick_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_change_leader_req packet = (SBean.aroom_change_leader_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_self_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_self_req packet = (SBean.aroom_self_req)ipacket;
		// TODO
	}

	public void onRecv_aroom_query_member(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_query_member packet = (SBean.aroom_query_member)ipacket;
		// TODO
	}

	public void onRecv_aroom_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.aroom_mapr_req packet = (SBean.aroom_mapr_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_weekrank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_weekrank_req packet = (SBean.superarena_weekrank_req)ipacket;
		// TODO
	}

	public void onRecv_superarena_dayrank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.superarena_dayrank_req packet = (SBean.superarena_dayrank_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_sync_req packet = (SBean.bwarena_sync_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_setpet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_setpet_req packet = (SBean.bwarena_setpet_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_refresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_refresh_req packet = (SBean.bwarena_refresh_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_buytimes_req packet = (SBean.bwarena_buytimes_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_startattack_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_startattack_req packet = (SBean.bwarena_startattack_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_takescore_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_takescore_req packet = (SBean.bwarena_takescore_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_log_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_log_req packet = (SBean.bwarena_log_req)ipacket;
		// TODO
	}

	public void onRecv_bwarena_ranks_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bwarena_ranks_req packet = (SBean.bwarena_ranks_req)ipacket;
		// TODO
	}

	public void onRecv_forcewar_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.forcewar_sync_req packet = (SBean.forcewar_sync_req)ipacket;
		// TODO
	}

	public void onRecv_forcewar_join_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.forcewar_join_req packet = (SBean.forcewar_join_req)ipacket;
		// TODO
	}

	public void onRecv_forcewar_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.forcewar_quit_req packet = (SBean.forcewar_quit_req)ipacket;
		// TODO
	}

	public void onRecv_froom_create_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_create_req packet = (SBean.froom_create_req)ipacket;
		// TODO
	}

	public void onRecv_froom_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_invite_req packet = (SBean.froom_invite_req)ipacket;
		// TODO
	}

	public void onRecv_froom_invitedby_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_invitedby_req packet = (SBean.froom_invitedby_req)ipacket;
		// TODO
	}

	public void onRecv_froom_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_leave_req packet = (SBean.froom_leave_req)ipacket;
		// TODO
	}

	public void onRecv_froom_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_kick_req packet = (SBean.froom_kick_req)ipacket;
		// TODO
	}

	public void onRecv_froom_change_leader_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_change_leader_req packet = (SBean.froom_change_leader_req)ipacket;
		// TODO
	}

	public void onRecv_froom_query_member(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_query_member packet = (SBean.froom_query_member)ipacket;
		// TODO
	}

	public void onRecv_froom_query_members(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_query_members packet = (SBean.froom_query_members)ipacket;
		// TODO
	}

	public void onRecv_froom_mapr_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.froom_mapr_req packet = (SBean.froom_mapr_req)ipacket;
		// TODO
	}

	public void onRecv_bosses_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bosses_sync_req packet = (SBean.bosses_sync_req)ipacket;
		// TODO
	}

	public void onRecv_walktoboss_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.walktoboss_req packet = (SBean.walktoboss_req)ipacket;
		// TODO
	}

	public void onRecv_transtoboss_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.transtoboss_req packet = (SBean.transtoboss_req)ipacket;
		// TODO
	}

	public void onRecv_reset_transtime_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.reset_transtime_req packet = (SBean.reset_transtime_req)ipacket;
		// TODO
	}

	public void onRecv_boss_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.boss_reward_req packet = (SBean.boss_reward_req)ipacket;
		// TODO
	}

	public void onRecv_friend_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_sync_req packet = (SBean.friend_sync_req)ipacket;
		// TODO
	}

	public void onRecv_friend_pluslist_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_pluslist_req packet = (SBean.friend_pluslist_req)ipacket;
		// TODO
	}

	public void onRecv_friend_recommend_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_recommend_req packet = (SBean.friend_recommend_req)ipacket;
		// TODO
	}

	public void onRecv_friend_add_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_add_req packet = (SBean.friend_add_req)ipacket;
		// TODO
	}

	public void onRecv_friend_search_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_search_req packet = (SBean.friend_search_req)ipacket;
		// TODO
	}

	public void onRecv_friend_delete_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_delete_req packet = (SBean.friend_delete_req)ipacket;
		// TODO
	}

	public void onRecv_friend_givevit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_givevit_req packet = (SBean.friend_givevit_req)ipacket;
		// TODO
	}

	public void onRecv_friend_giveallvits_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_giveallvits_req packet = (SBean.friend_giveallvits_req)ipacket;
		// TODO
	}

	public void onRecv_friend_receivevit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_receivevit_req packet = (SBean.friend_receivevit_req)ipacket;
		// TODO
	}

	public void onRecv_friend_agreeadd_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_agreeadd_req packet = (SBean.friend_agreeadd_req)ipacket;
		// TODO
	}

	public void onRecv_friend_changemsg_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_changemsg_req packet = (SBean.friend_changemsg_req)ipacket;
		// TODO
	}

	public void onRecv_friend_changehead_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_changehead_req packet = (SBean.friend_changehead_req)ipacket;
		// TODO
	}

	public void onRecv_friend_enemy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_enemy_req packet = (SBean.friend_enemy_req)ipacket;
		// TODO
	}

	public void onRecv_friend_removeenemy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_removeenemy_req packet = (SBean.friend_removeenemy_req)ipacket;
		// TODO
	}

	public void onRecv_friend_setfocus_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.friend_setfocus_req packet = (SBean.friend_setfocus_req)ipacket;
		// TODO
	}

	public void onRecv_give_flower_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.give_flower_req packet = (SBean.give_flower_req)ipacket;
		// TODO
	}

	public void onRecv_get_flowerlog_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.get_flowerlog_req packet = (SBean.get_flowerlog_req)ipacket;
		// TODO
	}

	public void onRecv_get_acceptlist_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.get_acceptlist_req packet = (SBean.get_acceptlist_req)ipacket;
		// TODO
	}

	public void onRecv_auction_syncitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_syncitems_req packet = (SBean.auction_syncitems_req)ipacket;
		// TODO
	}

	public void onRecv_auction_syncequips_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_syncequips_req packet = (SBean.auction_syncequips_req)ipacket;
		// TODO
	}

	public void onRecv_auction_syncselfitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_syncselfitems_req packet = (SBean.auction_syncselfitems_req)ipacket;
		// TODO
	}

	public void onRecv_auction_putonitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_putonitems_req packet = (SBean.auction_putonitems_req)ipacket;
		// TODO
	}

	public void onRecv_auction_putonequip_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_putonequip_req packet = (SBean.auction_putonequip_req)ipacket;
		// TODO
	}

	public void onRecv_auction_putoffitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_putoffitems_req packet = (SBean.auction_putoffitems_req)ipacket;
		// TODO
	}

	public void onRecv_auction_buyitems_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_buyitems_req packet = (SBean.auction_buyitems_req)ipacket;
		// TODO
	}

	public void onRecv_auction_expand_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_expand_req packet = (SBean.auction_expand_req)ipacket;
		// TODO
	}

	public void onRecv_auction_tradelog_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_tradelog_req packet = (SBean.auction_tradelog_req)ipacket;
		// TODO
	}

	public void onRecv_auction_itemprices_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.auction_itemprices_req packet = (SBean.auction_itemprices_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_syncnpcs_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_syncnpcs_req packet = (SBean.treasure_syncnpcs_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_refreshnpc_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_refreshnpc_req packet = (SBean.treasure_refreshnpc_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_buypieces_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_buypieces_req packet = (SBean.treasure_buypieces_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_npcreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_npcreward_req packet = (SBean.treasure_npcreward_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_syncmap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_syncmap_req packet = (SBean.treasure_syncmap_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_totalsearch_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_totalsearch_req packet = (SBean.treasure_totalsearch_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_search_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_search_req packet = (SBean.treasure_search_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_makemap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_makemap_req packet = (SBean.treasure_makemap_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_mapreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_mapreward_req packet = (SBean.treasure_mapreward_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_quitmap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_quitmap_req packet = (SBean.treasure_quitmap_req)ipacket;
		// TODO
	}

	public void onRecv_treasure_medalgrow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.treasure_medalgrow_req packet = (SBean.treasure_medalgrow_req)ipacket;
		// TODO
	}

	public void onRecv_horse_tame_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_tame_req packet = (SBean.horse_tame_req)ipacket;
		// TODO
	}

	public void onRecv_horse_use_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_use_req packet = (SBean.horse_use_req)ipacket;
		// TODO
	}

	public void onRecv_horse_upstar_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_upstar_req packet = (SBean.horse_upstar_req)ipacket;
		// TODO
	}

	public void onRecv_horse_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_enhance_req packet = (SBean.horse_enhance_req)ipacket;
		// TODO
	}

	public void onRecv_horse_enhancesave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_enhancesave_req packet = (SBean.horse_enhancesave_req)ipacket;
		// TODO
	}

	public void onRecv_horse_changeshow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_changeshow_req packet = (SBean.horse_changeshow_req)ipacket;
		// TODO
	}

	public void onRecv_horse_activateshow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_activateshow_req packet = (SBean.horse_activateshow_req)ipacket;
		// TODO
	}

	public void onRecv_horse_learnskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_learnskill_req packet = (SBean.horse_learnskill_req)ipacket;
		// TODO
	}

	public void onRecv_sync_horse_skillLevel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_horse_skillLevel_req packet = (SBean.sync_horse_skillLevel_req)ipacket;
		// TODO
	}

	public void onRecv_horse_skill_up_level_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_skill_up_level_req packet = (SBean.horse_skill_up_level_req)ipacket;
		// TODO
	}

	public void onRecv_horse_setskill_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_setskill_req packet = (SBean.horse_setskill_req)ipacket;
		// TODO
	}

	public void onRecv_fashion_upwear_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fashion_upwear_req packet = (SBean.fashion_upwear_req)ipacket;
		// TODO
	}

	public void onRecv_fashion_setshow_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fashion_setshow_req packet = (SBean.fashion_setshow_req)ipacket;
		// TODO
	}

	public void onRecv_seal_make_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_make_req packet = (SBean.seal_make_req)ipacket;
		// TODO
	}

	public void onRecv_seal_upgrade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_upgrade_req packet = (SBean.seal_upgrade_req)ipacket;
		// TODO
	}

	public void onRecv_seal_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_enhance_req packet = (SBean.seal_enhance_req)ipacket;
		// TODO
	}

	public void onRecv_seal_save_enhance_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.seal_save_enhance_req packet = (SBean.seal_save_enhance_req)ipacket;
		// TODO
	}

	public void onRecv_expcoin_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.expcoin_sync_req packet = (SBean.expcoin_sync_req)ipacket;
		// TODO
	}

	public void onRecv_expcoin_extract_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.expcoin_extract_req packet = (SBean.expcoin_extract_req)ipacket;
		// TODO
	}

	public void onRecv_rarebook_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_sync_req packet = (SBean.rarebook_sync_req)ipacket;
		// TODO
	}

	public void onRecv_rarebook_push_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_push_req packet = (SBean.rarebook_push_req)ipacket;
		// TODO
	}

	public void onRecv_rarebook_pop_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_pop_req packet = (SBean.rarebook_pop_req)ipacket;
		// TODO
	}

	public void onRecv_rarebook_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_unlock_req packet = (SBean.rarebook_unlock_req)ipacket;
		// TODO
	}

	public void onRecv_rarebook_lvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rarebook_lvlup_req packet = (SBean.rarebook_lvlup_req)ipacket;
		// TODO
	}

	public void onRecv_grasp_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.grasp_sync_req packet = (SBean.grasp_sync_req)ipacket;
		// TODO
	}

	public void onRecv_grasp_impl_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.grasp_impl_req packet = (SBean.grasp_impl_req)ipacket;
		// TODO
	}

	public void onRecv_grasp_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.grasp_reset_req packet = (SBean.grasp_reset_req)ipacket;
		// TODO
	}

	public void onRecv_dmgtransfer_buypoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_buypoint_req packet = (SBean.dmgtransfer_buypoint_req)ipacket;
		// TODO
	}

	public void onRecv_dmgtransfer_putpoint_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_putpoint_req packet = (SBean.dmgtransfer_putpoint_req)ipacket;
		// TODO
	}

	public void onRecv_dmgtransfer_lvlup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_lvlup_req packet = (SBean.dmgtransfer_lvlup_req)ipacket;
		// TODO
	}

	public void onRecv_dmgtransfer_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.dmgtransfer_reset_req packet = (SBean.dmgtransfer_reset_req)ipacket;
		// TODO
	}

	public void onRecv_lead_info_set(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lead_info_set packet = (SBean.lead_info_set)ipacket;
		// TODO
	}

	public void onRecv_lead_plot_set(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lead_plot_set packet = (SBean.lead_plot_set)ipacket;
		// TODO
	}

	public void onRecv_usersurvey_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.usersurvey_sync_req packet = (SBean.usersurvey_sync_req)ipacket;
		// TODO
	}

	public void onRecv_usersurvey_submit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.usersurvey_submit_req packet = (SBean.usersurvey_submit_req)ipacket;
		// TODO
	}

	public void onRecv_usersurvey_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.usersurvey_reward_req packet = (SBean.usersurvey_reward_req)ipacket;
		// TODO
	}

	public void onRecv_cblogingift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblogingift_sync_req packet = (SBean.cblogingift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_cblogingift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblogingift_take_req packet = (SBean.cblogingift_take_req)ipacket;
		// TODO
	}

	public void onRecv_cblvlupgift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblvlupgift_sync_req packet = (SBean.cblvlupgift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_cblvlupgift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cblvlupgift_take_req packet = (SBean.cblvlupgift_take_req)ipacket;
		// TODO
	}

	public void onRecv_userdata_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.userdata_sync_req packet = (SBean.userdata_sync_req)ipacket;
		// TODO
	}

	public void onRecv_userdata_modify_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.userdata_modify_req packet = (SBean.userdata_modify_req)ipacket;
		// TODO
	}

	public void onRecv_userdata_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.userdata_reward_req packet = (SBean.userdata_reward_req)ipacket;
		// TODO
	}

	public void onRecv_cbcountdowngift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cbcountdowngift_sync_req packet = (SBean.cbcountdowngift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_cbcountdowngift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.cbcountdowngift_take_req packet = (SBean.cbcountdowngift_take_req)ipacket;
		// TODO
	}

	public void onRecv_ontimegift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.ontimegift_sync_req packet = (SBean.ontimegift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_ontimegift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.ontimegift_take_req packet = (SBean.ontimegift_take_req)ipacket;
		// TODO
	}

	public void onRecv_strengthengift_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.strengthengift_sync_req packet = (SBean.strengthengift_sync_req)ipacket;
		// TODO
	}

	public void onRecv_strengthengift_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.strengthengift_take_req packet = (SBean.strengthengift_take_req)ipacket;
		// TODO
	}

	public void onRecv_official_research_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.official_research_sync_req packet = (SBean.official_research_sync_req)ipacket;
		// TODO
	}

	public void onRecv_official_research_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.official_research_take_req packet = (SBean.official_research_take_req)ipacket;
		// TODO
	}

	public void onRecv_permanenttitle_set_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.permanenttitle_set_req packet = (SBean.permanenttitle_set_req)ipacket;
		// TODO
	}

	public void onRecv_timedtitle_set_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.timedtitle_set_req packet = (SBean.timedtitle_set_req)ipacket;
		// TODO
	}

	public void onRecv_titleslot_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.titleslot_unlock_req packet = (SBean.titleslot_unlock_req)ipacket;
		// TODO
	}

	public void onRecv_branch_task_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.branch_task_take_req packet = (SBean.branch_task_take_req)ipacket;
		// TODO
	}

	public void onRecv_branch_task_quit_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.branch_task_quit_req packet = (SBean.branch_task_quit_req)ipacket;
		// TODO
	}

	public void onRecv_branch_task_finish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.branch_task_finish_req packet = (SBean.branch_task_finish_req)ipacket;
		// TODO
	}

	public void onRecv_sync_tower_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_tower_req packet = (SBean.sync_tower_req)ipacket;
		// TODO
	}

	public void onRecv_tower_record_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_record_req packet = (SBean.tower_record_req)ipacket;
		// TODO
	}

	public void onRecv_tower_setpets_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_setpets_req packet = (SBean.tower_setpets_req)ipacket;
		// TODO
	}

	public void onRecv_tower_startfight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_startfight_req packet = (SBean.tower_startfight_req)ipacket;
		// TODO
	}

	public void onRecv_tower_buytimes_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_buytimes_req packet = (SBean.tower_buytimes_req)ipacket;
		// TODO
	}

	public void onRecv_tower_sweep_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_sweep_req packet = (SBean.tower_sweep_req)ipacket;
		// TODO
	}

	public void onRecv_sync_towerfame_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_towerfame_req packet = (SBean.sync_towerfame_req)ipacket;
		// TODO
	}

	public void onRecv_tower_donate_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.tower_donate_req packet = (SBean.tower_donate_req)ipacket;
		// TODO
	}

	public void onRecv_take_towerfame_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_towerfame_req packet = (SBean.take_towerfame_req)ipacket;
		// TODO
	}

	public void onRecv_enter_secretmap_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.enter_secretmap_req packet = (SBean.enter_secretmap_req)ipacket;
		// TODO
	}

	public void onRecv_take_secretreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_secretreward_req packet = (SBean.take_secretreward_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_sync_req packet = (SBean.sect_deliver_sync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_refresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_refresh_req packet = (SBean.sect_deliver_refresh_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_protect_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_protect_req packet = (SBean.sect_deliver_protect_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_begin_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_begin_req packet = (SBean.sect_deliver_begin_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_search_help_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_search_help_req packet = (SBean.sect_deliver_search_help_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_on_help_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_on_help_req packet = (SBean.sect_deliver_on_help_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_cancel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_cancel_req packet = (SBean.sect_deliver_cancel_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_finish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_finish_req packet = (SBean.sect_deliver_finish_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_sync_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_sync_wish_req packet = (SBean.sect_deliver_sync_wish_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_add_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_add_wish_req packet = (SBean.sect_deliver_add_wish_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_save_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_save_wish_req packet = (SBean.sect_deliver_save_wish_req)ipacket;
		// TODO
	}

	public void onRecv_sect_rob_task_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_rob_task_take_req packet = (SBean.sect_rob_task_take_req)ipacket;
		// TODO
	}

	public void onRecv_sect_rob_task_cancel_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_rob_task_cancel_req packet = (SBean.sect_rob_task_cancel_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_shopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_shopsync_req packet = (SBean.sect_deliver_shopsync_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_shoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_shoprefresh_req packet = (SBean.sect_deliver_shoprefresh_req)ipacket;
		// TODO
	}

	public void onRecv_sect_deliver_shopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_deliver_shopbuy_req packet = (SBean.sect_deliver_shopbuy_req)ipacket;
		// TODO
	}

	public void onRecv_produce_workshopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_workshopsync_req packet = (SBean.produce_workshopsync_req)ipacket;
		// TODO
	}

	public void onRecv_produce_createnewrecipe_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_createnewrecipe_req packet = (SBean.produce_createnewrecipe_req)ipacket;
		// TODO
	}

	public void onRecv_produce_produce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_produce_req packet = (SBean.produce_produce_req)ipacket;
		// TODO
	}

	public void onRecv_produce_split_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_split_req packet = (SBean.produce_split_req)ipacket;
		// TODO
	}

	public void onRecv_produce_fusion_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_fusion_req packet = (SBean.produce_fusion_req)ipacket;
		// TODO
	}

	public void onRecv_produce_splitspbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.produce_splitspbuy_req packet = (SBean.produce_splitspbuy_req)ipacket;
		// TODO
	}

	public void onRecv_bag_merge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.bag_merge_req packet = (SBean.bag_merge_req)ipacket;
		// TODO
	}

	public void onRecv_rmactivity_takereward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rmactivity_takereward_req packet = (SBean.rmactivity_takereward_req)ipacket;
		// TODO
	}

	public void onRecv_role_rename_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.role_rename_req packet = (SBean.role_rename_req)ipacket;
		// TODO
	}

	public void onRecv_horse_ride_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_ride_req packet = (SBean.horse_ride_req)ipacket;
		// TODO
	}

	public void onRecv_horse_unride_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_unride_req packet = (SBean.horse_unride_req)ipacket;
		// TODO
	}

	public void onRecv_mulhorse_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_invite_req packet = (SBean.mulhorse_invite_req)ipacket;
		// TODO
	}

	public void onRecv_mulhorse_invitehandle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_invitehandle_req packet = (SBean.mulhorse_invitehandle_req)ipacket;
		// TODO
	}

	public void onRecv_mulhorse_apply_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_apply_req packet = (SBean.mulhorse_apply_req)ipacket;
		// TODO
	}

	public void onRecv_mulhorse_applyhandle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_applyhandle_req packet = (SBean.mulhorse_applyhandle_req)ipacket;
		// TODO
	}

	public void onRecv_mulhorse_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_leave_req packet = (SBean.mulhorse_leave_req)ipacket;
		// TODO
	}

	public void onRecv_mulhorse_kick_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mulhorse_kick_req packet = (SBean.mulhorse_kick_req)ipacket;
		// TODO
	}

	public void onRecv_staywith_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.staywith_invite_req packet = (SBean.staywith_invite_req)ipacket;
		// TODO
	}

	public void onRecv_staywith_invitehandle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.staywith_invitehandle_req packet = (SBean.staywith_invitehandle_req)ipacket;
		// TODO
	}

	public void onRecv_staywith_leave_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.staywith_leave_req packet = (SBean.staywith_leave_req)ipacket;
		// TODO
	}

	public void onRecv_sync_message_board_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_message_board_req packet = (SBean.sync_message_board_req)ipacket;
		// TODO
	}

	public void onRecv_add_message_board_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.add_message_board_req packet = (SBean.add_message_board_req)ipacket;
		// TODO
	}

	public void onRecv_comment_message_board_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.comment_message_board_req packet = (SBean.comment_message_board_req)ipacket;
		// TODO
	}

	public void onRecv_change_message_board_content_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.change_message_board_content_req packet = (SBean.change_message_board_content_req)ipacket;
		// TODO
	}

	public void onRecv_schedule_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.schedule_sync_req packet = (SBean.schedule_sync_req)ipacket;
		// TODO
	}

	public void onRecv_schedule_mapreward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.schedule_mapreward_req packet = (SBean.schedule_mapreward_req)ipacket;
		// TODO
	}

	public void onRecv_save_guide_mapcopy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_guide_mapcopy_req packet = (SBean.save_guide_mapcopy_req)ipacket;
		// TODO
	}

	public void onRecv_sect_invite_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_invite_req packet = (SBean.sect_invite_req)ipacket;
		// TODO
	}

	public void onRecv_sect_invite_response_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_invite_response_req packet = (SBean.sect_invite_response_req)ipacket;
		// TODO
	}

	public void onRecv_unlock_armor_type_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.unlock_armor_type_req packet = (SBean.unlock_armor_type_req)ipacket;
		// TODO
	}

	public void onRecv_armor_uprank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_uprank_req packet = (SBean.armor_uprank_req)ipacket;
		// TODO
	}

	public void onRecv_armor_add_talent_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_add_talent_req packet = (SBean.armor_add_talent_req)ipacket;
		// TODO
	}

	public void onRecv_armor_change_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_change_req packet = (SBean.armor_change_req)ipacket;
		// TODO
	}

	public void onRecv_armor_up_level_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.armor_up_level_req packet = (SBean.armor_up_level_req)ipacket;
		// TODO
	}

	public void onRecv_rune_push_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rune_push_req packet = (SBean.rune_push_req)ipacket;
		// TODO
	}

	public void onRecv_rune_pop_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rune_pop_req packet = (SBean.rune_pop_req)ipacket;
		// TODO
	}

	public void onRecv_reset_talent_point_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.reset_talent_point_req packet = (SBean.reset_talent_point_req)ipacket;
		// TODO
	}

	public void onRecv_solt_group_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.solt_group_unlock_req packet = (SBean.solt_group_unlock_req)ipacket;
		// TODO
	}

	public void onRecv_solt_push_rune_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.solt_push_rune_req packet = (SBean.solt_push_rune_req)ipacket;
		// TODO
	}

	public void onRecv_lang_push_rune_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lang_push_rune_req packet = (SBean.lang_push_rune_req)ipacket;
		// TODO
	}

	public void onRecv_rune_wish_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.rune_wish_req packet = (SBean.rune_wish_req)ipacket;
		// TODO
	}

	public void onRecv_put_in_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.put_in_warehouse_req packet = (SBean.put_in_warehouse_req)ipacket;
		// TODO
	}

	public void onRecv_take_out_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_out_warehouse_req packet = (SBean.take_out_warehouse_req)ipacket;
		// TODO
	}

	public void onRecv_expand_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.expand_warehouse_req packet = (SBean.expand_warehouse_req)ipacket;
		// TODO
	}

	public void onRecv_sync_private_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_private_warehouse_req packet = (SBean.sync_private_warehouse_req)ipacket;
		// TODO
	}

	public void onRecv_sync_public_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_public_warehouse_req packet = (SBean.sync_public_warehouse_req)ipacket;
		// TODO
	}

	public void onRecv_propose_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.propose_req packet = (SBean.propose_req)ipacket;
		// TODO
	}

	public void onRecv_propose_response_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.propose_response_req packet = (SBean.propose_response_req)ipacket;
		// TODO
	}

	public void onRecv_marriage_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_sync_req packet = (SBean.marriage_sync_req)ipacket;
		// TODO
	}

	public void onRecv_divorce_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.divorce_req packet = (SBean.divorce_req)ipacket;
		// TODO
	}

	public void onRecv_marriage_skill_levelup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_skill_levelup_req packet = (SBean.marriage_skill_levelup_req)ipacket;
		// TODO
	}

	public void onRecv_transform_to_partner_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.transform_to_partner_req packet = (SBean.transform_to_partner_req)ipacket;
		// TODO
	}

	public void onRecv_marriage_start_parade_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_start_parade_req packet = (SBean.marriage_start_parade_req)ipacket;
		// TODO
	}

	public void onRecv_marriage_start_banquet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.marriage_start_banquet_req packet = (SBean.marriage_start_banquet_req)ipacket;
		// TODO
	}

	public void onRecv_exchange_item_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.exchange_item_req packet = (SBean.exchange_item_req)ipacket;
		// TODO
	}

	public void onRecv_mrgseriestask_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrgseriestask_open_req packet = (SBean.mrgseriestask_open_req)ipacket;
		// TODO
	}

	public void onRecv_mrgseriestask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrgseriestask_take_req packet = (SBean.mrgseriestask_take_req)ipacket;
		// TODO
	}

	public void onRecv_mrgseriestask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrgseriestask_reward_req packet = (SBean.mrgseriestask_reward_req)ipacket;
		// TODO
	}

	public void onRecv_mrglooptask_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrglooptask_open_req packet = (SBean.mrglooptask_open_req)ipacket;
		// TODO
	}

	public void onRecv_mrglooptask_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrglooptask_take_req packet = (SBean.mrglooptask_take_req)ipacket;
		// TODO
	}

	public void onRecv_mrglooptask_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.mrglooptask_reward_req packet = (SBean.mrglooptask_reward_req)ipacket;
		// TODO
	}

	public void onRecv_save_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_skill_preset_req packet = (SBean.save_skill_preset_req)ipacket;
		// TODO
	}

	public void onRecv_save_all_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_all_skill_preset_req packet = (SBean.save_all_skill_preset_req)ipacket;
		// TODO
	}

	public void onRecv_save_spirits_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.save_spirits_preset_req packet = (SBean.save_spirits_preset_req)ipacket;
		// TODO
	}

	public void onRecv_delete_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.delete_skill_preset_req packet = (SBean.delete_skill_preset_req)ipacket;
		// TODO
	}

	public void onRecv_delete_spirits_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.delete_spirits_preset_req packet = (SBean.delete_spirits_preset_req)ipacket;
		// TODO
	}

	public void onRecv_change_skill_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.change_skill_preset_req packet = (SBean.change_skill_preset_req)ipacket;
		// TODO
	}

	public void onRecv_change_spirits_preset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.change_spirits_preset_req packet = (SBean.change_spirits_preset_req)ipacket;
		// TODO
	}

	public void onRecv_sect_group_map_open_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_group_map_open_req packet = (SBean.sect_group_map_open_req)ipacket;
		// TODO
	}

	public void onRecv_sect_group_map_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_group_map_enter_req packet = (SBean.sect_group_map_enter_req)ipacket;
		// TODO
	}

	public void onRecv_sect_group_map_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sect_group_map_sync_req packet = (SBean.sect_group_map_sync_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_skill_level_up_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_skill_level_up_req packet = (SBean.weapon_skill_level_up_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_talent_level_up_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_talent_level_up_req packet = (SBean.weapon_talent_level_up_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_talent_point_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_talent_point_buy_req packet = (SBean.weapon_talent_point_buy_req)ipacket;
		// TODO
	}

	public void onRecv_weapon_talent_point_reset_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.weapon_talent_point_reset_req packet = (SBean.weapon_talent_point_reset_req)ipacket;
		// TODO
	}

	public void onRecv_send_gift_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.send_gift_req packet = (SBean.send_gift_req)ipacket;
		// TODO
	}

	public void onRecv_sync_big_map_flag_info_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_big_map_flag_info_req packet = (SBean.sync_big_map_flag_info_req)ipacket;
		// TODO
	}

	public void onRecv_pet_skill_level_up_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.pet_skill_level_up_req packet = (SBean.pet_skill_level_up_req)ipacket;
		// TODO
	}

	public void onRecv_sync_special_card_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_special_card_req packet = (SBean.sync_special_card_req)ipacket;
		// TODO
	}

	public void onRecv_take_special_card_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_special_card_reward_req packet = (SBean.take_special_card_reward_req)ipacket;
		// TODO
	}

	public void onRecv_sync_daily_vit_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_daily_vit_reward_req packet = (SBean.sync_daily_vit_reward_req)ipacket;
		// TODO
	}

	public void onRecv_take_daily_vit_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.take_daily_vit_reward_req packet = (SBean.take_daily_vit_reward_req)ipacket;
		// TODO
	}

	public void onRecv_try_open_insight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_open_insight_req packet = (SBean.try_open_insight_req)ipacket;
		// TODO
	}

	public void onRecv_try_open_revenge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_open_revenge_req packet = (SBean.try_open_revenge_req)ipacket;
		// TODO
	}

	public void onRecv_try_sync_insight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_sync_insight_req packet = (SBean.try_sync_insight_req)ipacket;
		// TODO
	}

	public void onRecv_try_sync_revenge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_sync_revenge_req packet = (SBean.try_sync_revenge_req)ipacket;
		// TODO
	}

	public void onRecv_try_transform_insight_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_transform_insight_req packet = (SBean.try_transform_insight_req)ipacket;
		// TODO
	}

	public void onRecv_try_transform_revenge_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.try_transform_revenge_req packet = (SBean.try_transform_revenge_req)ipacket;
		// TODO
	}

	public void onRecv_heirloom_wipe_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.heirloom_wipe_req packet = (SBean.heirloom_wipe_req)ipacket;
		// TODO
	}

	public void onRecv_heirloom_takeout_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.heirloom_takeout_req packet = (SBean.heirloom_takeout_req)ipacket;
		// TODO
	}

	public void onRecv_buy_offline_func_point_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_offline_func_point_req packet = (SBean.buy_offline_func_point_req)ipacket;
		// TODO
	}

	public void onRecv_set_sect_qqgroup_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_sect_qqgroup_req packet = (SBean.set_sect_qqgroup_req)ipacket;
		// TODO
	}

	public void onRecv_set_heirloom_display_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_heirloom_display_req packet = (SBean.set_heirloom_display_req)ipacket;
		// TODO
	}

	public void onRecv_feat_gambleshopsync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.feat_gambleshopsync_req packet = (SBean.feat_gambleshopsync_req)ipacket;
		// TODO
	}

	public void onRecv_feat_gambleshoprefresh_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.feat_gambleshoprefresh_req packet = (SBean.feat_gambleshoprefresh_req)ipacket;
		// TODO
	}

	public void onRecv_feat_gambleshopbuy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.feat_gambleshopbuy_req packet = (SBean.feat_gambleshopbuy_req)ipacket;
		// TODO
	}

	public void onRecv_add_marriage_bespeak_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.add_marriage_bespeak_req packet = (SBean.add_marriage_bespeak_req)ipacket;
		// TODO
	}

	public void onRecv_sync_marriage_bespeak_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_marriage_bespeak_req packet = (SBean.sync_marriage_bespeak_req)ipacket;
		// TODO
	}

	public void onRecv_base_dummygoods_quick_buy_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.base_dummygoods_quick_buy_req packet = (SBean.base_dummygoods_quick_buy_req)ipacket;
		// TODO
	}

	public void onRecv_activity_last_quick_done_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.activity_last_quick_done_req packet = (SBean.activity_last_quick_done_req)ipacket;
		// TODO
	}

	public void onRecv_stele_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_sync_req packet = (SBean.stele_sync_req)ipacket;
		// TODO
	}

	public void onRecv_stele_join_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_join_req packet = (SBean.stele_join_req)ipacket;
		// TODO
	}

	public void onRecv_stele_rank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_rank_req packet = (SBean.stele_rank_req)ipacket;
		// TODO
	}

	public void onRecv_stele_teleport_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.stele_teleport_req packet = (SBean.stele_teleport_req)ipacket;
		// TODO
	}

	public void onRecv_blacklist_add_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.blacklist_add_req packet = (SBean.blacklist_add_req)ipacket;
		// TODO
	}

	public void onRecv_blacklist_del_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.blacklist_del_req packet = (SBean.blacklist_del_req)ipacket;
		// TODO
	}

	public void onRecv_blacklist_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.blacklist_sync_req packet = (SBean.blacklist_sync_req)ipacket;
		// TODO
	}

	public void onRecv_demonhole_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_sync_req packet = (SBean.demonhole_sync_req)ipacket;
		// TODO
	}

	public void onRecv_demonhole_join_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_join_req packet = (SBean.demonhole_join_req)ipacket;
		// TODO
	}

	public void onRecv_demonhole_changefloor_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_changefloor_req packet = (SBean.demonhole_changefloor_req)ipacket;
		// TODO
	}

	public void onRecv_demonhole_battle_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.demonhole_battle_req packet = (SBean.demonhole_battle_req)ipacket;
		// TODO
	}

	public void onRecv_justicemap_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.justicemap_start_req packet = (SBean.justicemap_start_req)ipacket;
		// TODO
	}

	public void onRecv_emergency_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.emergency_sync_req packet = (SBean.emergency_sync_req)ipacket;
		// TODO
	}

	public void onRecv_emergency_enter_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.emergency_enter_req packet = (SBean.emergency_enter_req)ipacket;
		// TODO
	}

	public void onRecv_emergency_rank_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.emergency_rank_req packet = (SBean.emergency_rank_req)ipacket;
		// TODO
	}

	public void onRecv_lucklystar_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lucklystar_sync_req packet = (SBean.lucklystar_sync_req)ipacket;
		// TODO
	}

	public void onRecv_lucklystar_gift_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.lucklystar_gift_req packet = (SBean.lucklystar_gift_req)ipacket;
		// TODO
	}

	public void onRecv_fightnpc_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fightnpc_start_req packet = (SBean.fightnpc_start_req)ipacket;
		// TODO
	}

	public void onRecv_fightnpc_reward_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.fightnpc_reward_req packet = (SBean.fightnpc_reward_req)ipacket;
		// TODO
	}

	public void onRecv_packetreward_sync_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.packetreward_sync_req packet = (SBean.packetreward_sync_req)ipacket;
		// TODO
	}

	public void onRecv_packetreward_take_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.packetreward_take_req packet = (SBean.packetreward_take_req)ipacket;
		// TODO
	}

	public void onRecv_horse_enhance_prop_unlock_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.horse_enhance_prop_unlock_req packet = (SBean.horse_enhance_prop_unlock_req)ipacket;
		// TODO
	}

	public void onRecv_buy_wizard_pet_time_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.buy_wizard_pet_time_req packet = (SBean.buy_wizard_pet_time_req)ipacket;
		// TODO
	}

	public void onRecv_set_cur_wizard_pet_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.set_cur_wizard_pet_req packet = (SBean.set_cur_wizard_pet_req)ipacket;
		// TODO
	}

	public void onRecv_sync_item_unlock_head_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.sync_item_unlock_head_req packet = (SBean.sync_item_unlock_head_req)ipacket;
		// TODO
	}

	public void onRecv_socialmsg_send_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_send_req packet = (SBean.socialmsg_send_req)ipacket;
		// TODO
	}

	public void onRecv_socialmsg_like_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_like_req packet = (SBean.socialmsg_like_req)ipacket;
		// TODO
	}

	public void onRecv_socialmsg_dislike_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_dislike_req packet = (SBean.socialmsg_dislike_req)ipacket;
		// TODO
	}

	public void onRecv_socialmsg_pageinfo_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.socialmsg_pageinfo_req packet = (SBean.socialmsg_pageinfo_req)ipacket;
		// TODO
	}

	public void onRecv_npc_transfrom_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.npc_transfrom_req packet = (SBean.npc_transfrom_req)ipacket;
		// TODO
	}

	public void onRecv_share_success(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.share_success packet = (SBean.share_success)ipacket;
		// TODO
	}

	public void onRecv_unlock_head_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.unlock_head_req packet = (SBean.unlock_head_req)ipacket;
		// TODO
	}

	public void onRecv_start_npc_map_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.start_npc_map_req packet = (SBean.start_npc_map_req)ipacket;
		// TODO
	}

	public void onRecv_join_npc_pray_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.join_npc_pray_req packet = (SBean.join_npc_pray_req)ipacket;
		// TODO
	}

	public void onRecv_unlock_private_warehouse_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.unlock_private_warehouse_req packet = (SBean.unlock_private_warehouse_req)ipacket;
		// TODO
	}

	public void onRecv_towerdefence_start_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.towerdefence_start_req packet = (SBean.towerdefence_start_req)ipacket;
		// TODO
	}

	public void onRecv_towerdefence_selectcard_req(int sessionid, Role role, SStream.IStreamable ipacket)
	{
		SBean.towerdefence_selectcard_req packet = (SBean.towerdefence_selectcard_req)ipacket;
		// TODO
	}

	//// end handlers.
}
