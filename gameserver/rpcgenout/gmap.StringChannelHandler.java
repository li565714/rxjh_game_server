// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gmap;

import ket.util.SStream;
import i3k.SBean;

public class StringChannelHandler
{

	//// begin handlers.
	public void onRecv_client_ping_start(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.client_ping_start packet = (SBean.client_ping_start)ipacket;
		// TODO
	}

	public void onRecv_sync_server_ping(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.sync_server_ping packet = (SBean.sync_server_ping)ipacket;
		// TODO
	}

	public void onRecv_set_monster_birthpos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.set_monster_birthpos packet = (SBean.set_monster_birthpos)ipacket;
		// TODO
	}

	public void onRecv_role_ride_horse(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_ride_horse packet = (SBean.role_ride_horse)ipacket;
		// TODO
	}

	public void onRecv_role_unride_horse(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_unride_horse packet = (SBean.role_unride_horse)ipacket;
		// TODO
	}

	public void onRecv_role_adjust_serverpos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_adjust_serverpos packet = (SBean.role_adjust_serverpos)ipacket;
		// TODO
	}

	public void onRecv_pet_adjust_serverpos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_adjust_serverpos packet = (SBean.pet_adjust_serverpos)ipacket;
		// TODO
	}

	public void onRecv_role_move(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_move packet = (SBean.role_move)ipacket;
		// TODO
	}

	public void onRecv_pet_move(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_move packet = (SBean.pet_move)ipacket;
		// TODO
	}

	public void onRecv_escortcar_move(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.escortcar_move packet = (SBean.escortcar_move)ipacket;
		// TODO
	}

	public void onRecv_role_stopmove(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_stopmove packet = (SBean.role_stopmove)ipacket;
		// TODO
	}

	public void onRecv_pet_stopmove(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_stopmove packet = (SBean.pet_stopmove)ipacket;
		// TODO
	}

	public void onRecv_escortcar_stopmove(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.escortcar_stopmove packet = (SBean.escortcar_stopmove)ipacket;
		// TODO
	}

	public void onRecv_query_roles_detail(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_roles_detail packet = (SBean.query_roles_detail)ipacket;
		// TODO
	}

	public void onRecv_query_traps_detail(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_traps_detail packet = (SBean.query_traps_detail)ipacket;
		// TODO
	}

	public void onRecv_query_pets_detail(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_pets_detail packet = (SBean.query_pets_detail)ipacket;
		// TODO
	}

	public void onRecv_query_forcewar_result(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_forcewar_result packet = (SBean.query_forcewar_result)ipacket;
		// TODO
	}

	public void onRecv_query_forcewar_members_pos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_forcewar_members_pos packet = (SBean.query_forcewar_members_pos)ipacket;
		// TODO
	}

	public void onRecv_role_usefollowskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_usefollowskill packet = (SBean.role_usefollowskill)ipacket;
		// TODO
	}

	public void onRecv_role_socialaction(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_socialaction packet = (SBean.role_socialaction)ipacket;
		// TODO
	}

	public void onRecv_role_breakskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_breakskill packet = (SBean.role_breakskill)ipacket;
		// TODO
	}

	public void onRecv_role_useskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_useskill packet = (SBean.role_useskill)ipacket;
		// TODO
	}

	public void onRecv_role_usemapskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_usemapskill packet = (SBean.role_usemapskill)ipacket;
		// TODO
	}

	public void onRecv_pet_useskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_useskill packet = (SBean.pet_useskill)ipacket;
		// TODO
	}

	public void onRecv_role_endskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_endskill packet = (SBean.role_endskill)ipacket;
		// TODO
	}

	public void onRecv_pet_endskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_endskill packet = (SBean.pet_endskill)ipacket;
		// TODO
	}

	public void onRecv_role_finishattack(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_finishattack packet = (SBean.role_finishattack)ipacket;
		// TODO
	}

	public void onRecv_pet_finishattack(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_finishattack packet = (SBean.pet_finishattack)ipacket;
		// TODO
	}

	public void onRecv_role_rushstart(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_rushstart packet = (SBean.role_rushstart)ipacket;
		// TODO
	}

	public void onRecv_pet_rushstart(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_rushstart packet = (SBean.pet_rushstart)ipacket;
		// TODO
	}

	public void onRecv_mineral_quit(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.mineral_quit packet = (SBean.mineral_quit)ipacket;
		// TODO
	}

	public void onRecv_trap_click(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.trap_click packet = (SBean.trap_click)ipacket;
		// TODO
	}

	public void onRecv_role_shift_start(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_shift_start packet = (SBean.role_shift_start)ipacket;
		// TODO
	}

	public void onRecv_pet_shift_start(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_shift_start packet = (SBean.pet_shift_start)ipacket;
		// TODO
	}

	public void onRecv_role_pickup_drops(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_pickup_drops packet = (SBean.role_pickup_drops)ipacket;
		// TODO
	}

	public void onRecv_role_pickup_mapbuff(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_pickup_mapbuff packet = (SBean.role_pickup_mapbuff)ipacket;
		// TODO
	}

	public void onRecv_role_motivate_weapon(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_motivate_weapon packet = (SBean.role_motivate_weapon)ipacket;
		// TODO
	}

	public void onRecv_privatemap_kill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_kill packet = (SBean.privatemap_kill)ipacket;
		// TODO
	}

	public void onRecv_privatemap_damage_reward(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_damage_reward packet = (SBean.privatemap_damage_reward)ipacket;
		// TODO
	}

	public void onRecv_privatemap_trap(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_trap packet = (SBean.privatemap_trap)ipacket;
		// TODO
	}

	public void onRecv_privatemap_weapon_master(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_weapon_master packet = (SBean.privatemap_weapon_master)ipacket;
		// TODO
	}

	public void onRecv_privatemap_role_updatehp(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_role_updatehp packet = (SBean.privatemap_role_updatehp)ipacket;
		// TODO
	}

	public void onRecv_privatemap_pet_updatehp(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_pet_updatehp packet = (SBean.privatemap_pet_updatehp)ipacket;
		// TODO
	}

	public void onRecv_privatemap_durability(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_durability packet = (SBean.privatemap_durability)ipacket;
		// TODO
	}

	public void onRecv_query_entity_nearby(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_entity_nearby packet = (SBean.query_entity_nearby)ipacket;
		// TODO
	}

	public void onRecv_send_towerdefence_alarm(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.send_towerdefence_alarm packet = (SBean.send_towerdefence_alarm)ipacket;
		// TODO
	}

	public void onRecv_query_map_damage_rank(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_map_damage_rank packet = (SBean.query_map_damage_rank)ipacket;
		// TODO
	}

	public void onRecv_query_sect_map_cur_info(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_sect_map_cur_info packet = (SBean.query_sect_map_cur_info)ipacket;
		// TODO
	}

	//// end handlers.
}
