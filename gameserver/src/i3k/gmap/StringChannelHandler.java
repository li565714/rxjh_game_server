// modified by ket.kio.RPCGen at Sat May 06 15:19:06 CST 2017.

package i3k.gmap;

import ket.util.SStream;
import i3k.SBean;
import i3k.util.GVector3;

public class StringChannelHandler
{	
	public StringChannelHandler(MapServer ms)
	{
		this.ms = ms;
	}
	
	private boolean checkTimeOut(SBean.TimeTick timeTick)
	{
		if(timeTick == null)
			return true;
		
//		SBean.TimeTick curTimeTick = ms.getMapManager().getTimeTick();
//		if(timeTick.tickLine > curTimeTick.tickLine)
//			return true;
//		else if(timeTick.tickLine == curTimeTick.tickLine && timeTick.outTick > curTimeTick.outTick)
//			return true;
//			
//		if(curTimeTick.tickLine - timeTick.tickLine >= TIME_OUT_TICK_COUNT)
//		{
//			ms.getLogger().debug("timeout curTimeTick.tickLine " + curTimeTick.tickLine + " timeTick.tickLine " + timeTick.tickLine);
//			return true;
//		}
		
		return false;
	}
	//// begin handlers.
	public void onRecv_client_ping_start(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.client_ping_start packet = (SBean.client_ping_start)ipacket;
		role.onRecvPingStart(packet.timeTick, packet.ping);
	}

	public void onRecv_sync_server_ping(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.sync_server_ping packet = (SBean.sync_server_ping)ipacket;
		role.onRecvPingSync(packet.ping);
	}

	public void onRecv_set_monster_birthpos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.set_monster_birthpos packet = (SBean.set_monster_birthpos)ipacket;
		role.setMonsterToBirthPos(packet.mid);
	}

	public void onRecv_role_ride_horse(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_ride_horse packet = (SBean.role_ride_horse)ipacket;
		if(role.isDead())
			return;
		
//		role.roleRideHorse();
	}

	public void onRecv_role_unride_horse(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_unride_horse packet = (SBean.role_unride_horse)ipacket;
//		role.roleUnRideHorse(false);
	}

	public void onRecv_role_adjust_serverpos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_adjust_serverpos packet = (SBean.role_adjust_serverpos)ipacket;
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead())
			return;
		
		role.clientAdjustRolePos(packet.pos);
	}

	public void onRecv_pet_adjust_serverpos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_adjust_serverpos packet = (SBean.pet_adjust_serverpos)ipacket;
		if(role.isInPrivateMap())
			return;
		
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster == null)
			return;
		
		Pet pet = cluster.pets.get(packet.pid);
		if(pet == null || pet.isDead() || !pet.active)
			return;
		
		pet.clientAdjustPetPos(packet.pos);
	}

	public void onRecv_role_move(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_move packet = (SBean.role_move)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT) || role.isInProtectTime())
			return;

//		role.onMoveMent( new GVector3(packet.pos), new GVector3(packet.target), packet.speed, packet.rotation, packet.timeTick);
		role.onMoveMent(packet.speed, packet.rotation, new GVector3(packet.pos), new GVector3(packet.target), packet.timeTick);
	}

	public void onRecv_pet_move(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_move packet = (SBean.pet_move)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster != null)
		{
			Pet pet = cluster.pets.get(packet.cfgid);
			if(pet != null && !pet.isDead() && pet.active && !pet.isInProtectTime())
			{
				pet.onMoveMent(packet.speed, packet.rotation, new GVector3(packet.pos), new GVector3(packet.target), packet.timeTick);
//				pet.onMoveMent(new GVector3(packet.pos), new GVector3(packet.target), packet.speed, packet.timeTick);
			}
		}
	}

	public void onRecv_escortcar_move(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.escortcar_move packet = (SBean.escortcar_move)ipacket;
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		EscortCar car = ms.getMapManager().getMapCar(role.id);
		if(car == null)
			return;
		
		car.onMoveMent(packet.speed, packet.rotation, new GVector3(packet.pos), new GVector3(packet.target), packet.timeTick);
	}

	public void onRecv_role_stopmove(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_stopmove packet = (SBean.role_stopmove)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT) || role.isInProtectTime())
			return;
		
		role.onStopMove(new GVector3(packet.pos), packet.timeTick, true);
	}

	public void onRecv_pet_stopmove(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_stopmove packet = (SBean.pet_stopmove)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster != null)
		{
			Pet pet = cluster.pets.get(packet.cfgid);
			if(pet != null && !pet.isDead() && pet.active && !pet.isInProtectTime())
				pet.onStopMove(new GVector3(packet.pos), packet.timeTick, true);
		}
	}

	public void onRecv_escortcar_stopmove(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.escortcar_stopmove packet = (SBean.escortcar_stopmove)ipacket;
		if(checkTimeOut(packet.timeTick))
			return;

		if(role.isInPrivateMap())
			return;

		EscortCar car = ms.getMapManager().getMapCar(role.id);
		if(car == null)
			return;

		car.onStopMove(new GVector3(packet.pos), packet.timeTick, true);
	}

	public void onRecv_query_roles_detail(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_roles_detail packet = (SBean.query_roles_detail)ipacket;
		if(role.isInPrivateMap() || packet.roles.isEmpty())
			return;
		
		role.onQueryRolesDetail(packet.roles);
	}

	public void onRecv_query_traps_detail(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_traps_detail packet = (SBean.query_traps_detail)ipacket;
		if(role.isInPrivateMap() || packet.traps.isEmpty())
			return;
		role.onQueryTrapsDetail(packet.traps);
	}

	public void onRecv_query_pets_detail(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_pets_detail packet = (SBean.query_pets_detail)ipacket;
		if(role.isInPrivateMap() || packet.pets.isEmpty())
			return;
		role.onQueryPetsDetail(packet.pets);
	}

	public void onRecv_query_forcewar_result(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_forcewar_result packet = (SBean.query_forcewar_result)ipacket;
		role.onQueryForceWarResult();
	}

	public void onRecv_query_forcewar_members_pos(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_forcewar_members_pos packet = (SBean.query_forcewar_members_pos)ipacket;
		role.onQueryForceWarMemberPos();
	}

	public void onRecv_role_usefollowskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_usefollowskill packet = (SBean.role_usefollowskill)ipacket;
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT))
			return;
		
		role.onUseFollowSkill(packet.skillID, packet.seq, packet.timeTick);
	}

	public void onRecv_role_socialaction(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_socialaction packet = (SBean.role_socialaction)ipacket;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT))
			return;
		
		role.roleSocailAction(packet.actionID);
	}

	public void onRecv_role_breakskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_breakskill packet = (SBean.role_breakskill)ipacket;
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT))
			return;
		
		role.breakSkill();
	}

	public void onRecv_role_useskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_useskill packet = (SBean.role_useskill)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT))
			return;
		
		if(!role.onUseSkill(packet.skillID, packet.pos, packet.rotation, packet.targetID, packet.targetType, packet.ownerID, packet.timeTick))
			role.removeState(Behavior.EBATTACK);
	}

	public void onRecv_role_usemapskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_usemapskill packet = (SBean.role_usemapskill)ipacket;

		if(checkTimeOut(packet.timeTick))
			return;
		
		if(!role.isInJusticeMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT))
			return;
		
		if(!role.onUseMapSkill(packet.skillID, packet.pos, packet.rotation, packet.targetID, packet.targetType, packet.ownerID, packet.timeTick))
			role.removeState(Behavior.EBATTACK);
	}

	public void onRecv_pet_useskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_useskill packet = (SBean.pet_useskill)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		PetCluster cluster = role.curMap.getPetCluster(role.id);
		if(cluster == null)
			return;
		
		Pet pet = cluster.pets.get(packet.cfgid);
		if(pet == null)
			return;
		
		if(pet.isDead() || !pet.active)
			return;
		
		pet.onUseSkill(packet.skillID, packet.pos, packet.rotation, packet.targetID, packet.targetType, packet.ownerID, packet.timeTick);
	}

	public void onRecv_role_endskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_endskill packet = (SBean.role_endskill)ipacket;
		if(role.isInPrivateMap())
			return;
		
//		role.onEndSkill(packet.skillID);
	}

	public void onRecv_pet_endskill(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_endskill packet = (SBean.pet_endskill)ipacket;
		if(role.isInPrivateMap())
			return;
		
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster == null)
			return;
		
		Pet pet = cluster.pets.get(packet.cfgid);
		if(pet == null || !pet.active)
			return;
		
//		pet.onEndSkill(packet.skillID);
	}

	public void onRecv_role_finishattack(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_finishattack packet = (SBean.role_finishattack)ipacket;
//		role.onFinishAttack();
	}

	public void onRecv_pet_finishattack(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_finishattack packet = (SBean.pet_finishattack)ipacket;
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster == null)
			return;
		
		Pet pet = cluster.pets.get(packet.cfgid);
		if(pet == null)
			return;
		
//		pet.onFinishAttack();
	}

	public void onRecv_role_rushstart(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_rushstart packet = (SBean.role_rushstart)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead() || role.checkState(Behavior.EBPREPAREFIGHT))
			return;
		
		role.onRushStart(packet.skillID, packet.endPos, packet.timeTick);
	}

	public void onRecv_pet_rushstart(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_rushstart packet = (SBean.pet_rushstart)ipacket;
		
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster == null)
			return;
		
		Pet pet = cluster.pets.get(packet.cfgid);
		if(pet == null)
			return;
		
		pet.onRushStart(packet.skillID, packet.endPos, packet.timeTick);
	}

	public void onRecv_mineral_quit(MapRole role, SStream.IStreamable ipacket)
	{
		if(role.isInPrivateMap())
			return;
		
		SBean.mineral_quit packet = (SBean.mineral_quit)ipacket;
		role.onMineralQuit();
	}

	public void onRecv_trap_click(MapRole role, SStream.IStreamable ipacket)
	{
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead())
			return;
		
		SBean.trap_click packet = (SBean.trap_click)ipacket;
		int trapID = packet.trapID;
		Trap t = role.curMap.getTrap(trapID);
		if(t != null)
			t.onClicked(role);
	}

	public void onRecv_role_shift_start(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_shift_start packet = (SBean.role_shift_start)ipacket;
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
		
		if(role.isDead())
			return;
		
		role.onShiftStart(packet.skillID, packet.targetID, packet.targetType, packet.ownerID, new GVector3(packet.endpos), packet.timeTick);
	}

	public void onRecv_pet_shift_start(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.pet_shift_start packet = (SBean.pet_shift_start)ipacket;
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isInPrivateMap())
			return;
	
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster == null)
			return;
		
		Pet pet = cluster.pets.get(packet.attackpid);
		if(pet == null)
			return;
		
		if(pet.isDead())
			return;
		
		pet.onShiftStart(packet.skillID, packet.targetID, packet.targetType, packet.ownerID, new GVector3(packet.endpos), packet.timeTick);
	}

	public void onRecv_role_pickup_drops(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_pickup_drops packet = (SBean.role_pickup_drops)ipacket;
		role.onPickUpDrops(packet.drops);
	}

	public void onRecv_role_pickup_mapbuff(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_pickup_mapbuff packet = (SBean.role_pickup_mapbuff)ipacket;
		role.onPickUpMapBuff(packet.mapBuffID);
	}

	public void onRecv_role_motivate_weapon(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.role_motivate_weapon packet = (SBean.role_motivate_weapon)ipacket;
		if(checkTimeOut(packet.timeTick))
			return;
		
		if(role.isDead())
			return;
		
		role.onMotivateWeapon(packet.timeTick);
	}

	public void onRecv_privatemap_kill(MapRole role, SStream.IStreamable ipacket)
	{
		if(!role.isInPrivateMap())
			return;
		
		if(role.isDead())
			return;
		
		SBean.privatemap_kill packet = (SBean.privatemap_kill)ipacket;
		role.onPrivateMapKill(packet.spawnPointID, packet.position, packet.weaponID > 0);
		role.onPrivateMapSaveDamageRank(packet.damageRank);
	}

	public void onRecv_privatemap_damage_reward(MapRole role, SStream.IStreamable ipacket)
	{
		if(!role.isInPrivateMap())
			return;
		
		if(role.isDead())
			return;
		
		SBean.privatemap_damage_reward packet = (SBean.privatemap_damage_reward)ipacket;
		role.onPrivateMapMonsterDamageDrop(packet.spawnPointID, packet.position, packet.index);
	}

	public void onRecv_privatemap_trap(MapRole role, SStream.IStreamable ipacket)
	{
		if(!role.isInPrivateMap())
			return;
		
		SBean.privatemap_trap packet = (SBean.privatemap_trap)ipacket;
		int trapID = packet.trapID;
		int state = packet.trapState;
		role.onPrivateMapTrapTrig(trapID, state);
	}

	public void onRecv_privatemap_weapon_master(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_weapon_master packet = (SBean.privatemap_weapon_master)ipacket;
		role.onPrivateMapWeaponMaster();
	}

	public void onRecv_privatemap_role_updatehp(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_role_updatehp packet = (SBean.privatemap_role_updatehp)ipacket;
		if(!role.isInPrivateMap())
		return;
			
		int hp = packet.hp;
		role.onPrivateMapUpdateHp(hp);
	}

	public void onRecv_privatemap_pet_updatehp(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.privatemap_pet_updatehp packet = (SBean.privatemap_pet_updatehp)ipacket;
		if(!role.isInPrivateMap())
			return;
		
		if(role.isDead())
			return;
		
		PetCluster cluster = role.curMap.getPetCluster(role.getID());
		if(cluster == null)
			return;
		
		Pet pet = cluster.pets.get(packet.cfgID);
		if(pet == null)
			return;
		
		pet.onPrivateMapUpdateHP(packet.hp);
	}

	public void onRecv_privatemap_durability(MapRole role, SStream.IStreamable ipacket)
	{
		if(!role.isInPrivateMap())
			return;
		
		SBean.privatemap_durability packet = (SBean.privatemap_durability)ipacket;
		int wid = packet.wid;
		role.onSyncPrivateMapDurability(wid);
	}
	
	public void onRecv_query_entity_nearby(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_entity_nearby packet = (SBean.query_entity_nearby)ipacket;
		role.queryEntityNearBy(packet.id, packet.type);
	}
	
	public void onRecv_send_towerdefence_alarm(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.send_towerdefence_alarm packet = (SBean.send_towerdefence_alarm)ipacket;
		role.sendTowerDefenceAlarm(packet.type);
	}

	public void onRecv_query_map_damage_rank(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_map_damage_rank packet = (SBean.query_map_damage_rank)ipacket;
		role.syncMapDamageRank();
	}

	public void onRecv_query_sect_map_cur_info(MapRole role, SStream.IStreamable ipacket)
	{
		SBean.query_sect_map_cur_info packet = (SBean.query_sect_map_cur_info)ipacket;
		role.syncSectGroupMapInfo();
	}

	//// end handlers.
	
	final MapServer ms;
	private static final int TIME_OUT_TICK_COUNT = 30;
}
