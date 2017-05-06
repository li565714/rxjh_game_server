// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs;

import ket.kio.NetAddress;
import ket.kio.SimplePacket;
import ket.kio.NetManager;
import i3k.rpc.Packet;
import i3k.SBean;

public class RPCManager
{

	//// begin handlers.
	public int getTCPGameServerMaxConnectionIdleTime()
	{
		// TODO
		return 0;
	}

	public void onTCPGameServerOpen(TCPGameServer peer)
	{
		// TODO
	}

	public void onTCPGameServerOpenFailed(TCPGameServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGameServerClose(TCPGameServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGameServerSessionOpen(TCPGameServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPGameServerSessionClose(TCPGameServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGameServerRecvClientResponse(TCPGameServer peer, Packet.C2S.ClientResponse packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGameServerRecvLuaChannel(TCPGameServer peer, Packet.C2S.LuaChannel packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGameServerRecvLuaChannel2(TCPGameServer peer, Packet.C2S.LuaChannel2 packet, int sessionid)
	{
		// TODO
	}

	public void onTCPGameServerRecvStrChannel(TCPGameServer peer, Packet.C2S.StrChannel packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerOpen(TCPMapServer peer)
	{
		// TODO
	}

	public void onTCPMapServerOpenFailed(TCPMapServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPMapServerClose(TCPMapServer peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPMapServerSessionOpen(TCPMapServer peer, int sessionid, NetAddress addrClient)
	{
		// TODO
	}

	public void onTCPMapServerSessionClose(TCPMapServer peer, int sessionid, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPMapServerRecvKeepAlive(TCPMapServer peer, Packet.M2S.KeepAlive packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvWhoAmI(TCPMapServer peer, Packet.M2S.WhoAmI packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvLuaChannel(TCPMapServer peer, Packet.M2S.LuaChannel packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvStrChannel(TCPMapServer peer, Packet.M2S.StrChannel packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvStrChannelBroadcast(TCPMapServer peer, Packet.M2S.StrChannelBroadcast packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvMapRoleReady(TCPMapServer peer, Packet.M2S.MapRoleReady packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvNearByRoleMove(TCPMapServer peer, Packet.M2S.NearByRoleMove packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvNearByRoleStopMove(TCPMapServer peer, Packet.M2S.NearByRoleStopMove packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvNearByRoleEnter(TCPMapServer peer, Packet.M2S.NearByRoleEnter packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvNearByRoleLeave(TCPMapServer peer, Packet.M2S.NearByRoleLeave packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncCommonMapCopyStart(TCPMapServer peer, Packet.M2S.SyncCommonMapCopyStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncCommonMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncCommonMapCopyEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSectMapCopyStart(TCPMapServer peer, Packet.M2S.SyncSectMapCopyStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSectMapCopyProgress(TCPMapServer peer, Packet.M2S.SyncSectMapCopyProgress packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncArenaMapCopyStart(TCPMapServer peer, Packet.M2S.SyncArenaMapCopyStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncArenaMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncArenaMapCopyEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncLocation(TCPMapServer peer, Packet.M2S.SyncLocation packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncHp(TCPMapServer peer, Packet.M2S.SyncHp packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvAddDrops(TCPMapServer peer, Packet.M2S.AddDrops packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvAddKill(TCPMapServer peer, Packet.M2S.AddKill packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncDurability(TCPMapServer peer, Packet.M2S.SyncDurability packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncEndMine(TCPMapServer peer, Packet.M2S.SyncEndMine packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvAddPKValue(TCPMapServer peer, Packet.M2S.AddPKValue packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncWorldMapBossProgress(TCPMapServer peer, Packet.M2S.SyncWorldMapBossProgress packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncBWArenaMapCopyStart(TCPMapServer peer, Packet.M2S.SyncBWArenaMapCopyStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncBWArenaMapCopyEnd(TCPMapServer peer, Packet.M2S.SyncBWArenaMapCopyEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncPetLifeMapCopyStart(TCPMapServer peer, Packet.M2S.SyncPetLifeMapCopyStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncCurRideHorse(TCPMapServer peer, Packet.M2S.SyncCurRideHorse packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncCarLocation(TCPMapServer peer, Packet.M2S.SyncCarLocation packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncCarHp(TCPMapServer peer, Packet.M2S.SyncCarHp packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvUpdateCarDamage(TCPMapServer peer, Packet.M2S.UpdateCarDamage packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncRoleRobSuccess(TCPMapServer peer, Packet.M2S.SyncRoleRobSuccess packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvUpdateRoleCarRobber(TCPMapServer peer, Packet.M2S.UpdateRoleCarRobber packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvKickRoleFromMap(TCPMapServer peer, Packet.M2S.KickRoleFromMap packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvRoleUseItemSkillSuc(TCPMapServer peer, Packet.M2S.RoleUseItemSkillSuc packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvUpdateRoleFightState(TCPMapServer peer, Packet.M2S.UpdateRoleFightState packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncRolePetHp(TCPMapServer peer, Packet.M2S.SyncRolePetHp packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncRoleSp(TCPMapServer peer, Packet.M2S.SyncRoleSp packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncWorldMapBossRecord(TCPMapServer peer, Packet.M2S.SyncWorldMapBossRecord packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncArmorVal(TCPMapServer peer, Packet.M2S.SyncArmorVal packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyStatus(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyStatus packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyResult(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyResult packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyStart(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyProgress(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyProgress packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSectGroupMapCopyAddKill(TCPMapServer peer, Packet.M2S.SyncSectGroupMapCopyAddKill packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncMapFlagCanTake(TCPMapServer peer, Packet.M2S.SyncMapFlagCanTake packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncWeaponMaster(TCPMapServer peer, Packet.M2S.SyncWeaponMaster packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvRolePickUpDrops(TCPMapServer peer, Packet.M2S.RolePickUpDrops packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSuperMonster(TCPMapServer peer, Packet.M2S.SyncSuperMonster packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncWorldMineral(TCPMapServer peer, Packet.M2S.SyncWorldMineral packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncMarriageParadeEnd(TCPMapServer peer, Packet.M2S.SyncMarriageParadeEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvRolePickUpRareDrops(TCPMapServer peer, Packet.M2S.RolePickUpRareDrops packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncWorldBossDamageRoles(TCPMapServer peer, Packet.M2S.SyncWorldBossDamageRoles packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncSteleRemainTimes(TCPMapServer peer, Packet.M2S.SyncSteleRemainTimes packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncRoleAddSteleCard(TCPMapServer peer, Packet.M2S.SyncRoleAddSteleCard packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncRefreshSteleMonster(TCPMapServer peer, Packet.M2S.SyncRefreshSteleMonster packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncEmergencyMapStart(TCPMapServer peer, Packet.M2S.SyncEmergencyMapStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncEmergencyMapKillMonster(TCPMapServer peer, Packet.M2S.SyncEmergencyMapKillMonster packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncEmergencyMapEnd(TCPMapServer peer, Packet.M2S.SyncEmergencyMapEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncFightNpcMapStart(TCPMapServer peer, Packet.M2S.SyncFightNpcMapStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncFightNpcMapEnd(TCPMapServer peer, Packet.M2S.SyncFightNpcMapEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncTowerDefenceMapStart(TCPMapServer peer, Packet.M2S.SyncTowerDefenceMapStart packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncTowerDefenceMapEnd(TCPMapServer peer, Packet.M2S.SyncTowerDefenceMapEnd packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncTowerDefenceSpawnCount(TCPMapServer peer, Packet.M2S.SyncTowerDefenceSpawnCount packet, int sessionid)
	{
		// TODO
	}

	public void onTCPMapServerRecvSyncTowerDefenceScore(TCPMapServer peer, Packet.M2S.SyncTowerDefenceScore packet, int sessionid)
	{
		// TODO
	}

	public void onTCPAuctionClientOpen(TCPAuctionClient peer)
	{
		// TODO
	}

	public void onTCPAuctionClientOpenFailed(TCPAuctionClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuctionClientClose(TCPAuctionClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvKeepAlive(TCPAuctionClient peer, Packet.Auction2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvAdjustTimeOffset(TCPAuctionClient peer, Packet.Auction2S.AdjustTimeOffset packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvPutOnItemRes(TCPAuctionClient peer, Packet.Auction2S.PutOnItemRes packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvTimeOutPutOffItemsReq(TCPAuctionClient peer, Packet.Auction2S.TimeOutPutOffItemsReq packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvPutOffItemsRes(TCPAuctionClient peer, Packet.Auction2S.PutOffItemsRes packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvBuyItemsRes(TCPAuctionClient peer, Packet.Auction2S.BuyItemsRes packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvCheckCanBuyReq(TCPAuctionClient peer, Packet.Auction2S.CheckCanBuyReq packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvAuctionItemsSyncRes(TCPAuctionClient peer, Packet.Auction2S.AuctionItemsSyncRes packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvSelfItemsSyncRes(TCPAuctionClient peer, Packet.Auction2S.SelfItemsSyncRes packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvItemPricesSyncRes(TCPAuctionClient peer, Packet.Auction2S.ItemPricesSyncRes packet)
	{
		// TODO
	}

	public void onTCPAuctionClientRecvSyncGroupBuyLog(TCPAuctionClient peer, Packet.Auction2S.SyncGroupBuyLog packet)
	{
		// TODO
	}

	public void onTCPAuthClientOpen(TCPAuthClient peer)
	{
		// TODO
	}

	public void onTCPAuthClientOpenFailed(TCPAuthClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuthClientClose(TCPAuthClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPAuthClientRecvKeepAlive(TCPAuthClient peer, Packet.AU2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPAuthClientRecvPayReq(TCPAuthClient peer, Packet.AU2S.PayReq packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientOpen(TCPGlobalMapClient peer)
	{
		// TODO
	}

	public void onTCPGlobalMapClientOpenFailed(TCPGlobalMapClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGlobalMapClientClose(TCPGlobalMapClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvKeepAlive(TCPGlobalMapClient peer, Packet.GM2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncGlobalMaps(TCPGlobalMapClient peer, Packet.GM2S.SyncGlobalMaps packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvLuaChannel(TCPGlobalMapClient peer, Packet.GM2S.LuaChannel packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvStrChannel(TCPGlobalMapClient peer, Packet.GM2S.StrChannel packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvMapRoleReady(TCPGlobalMapClient peer, Packet.GM2S.MapRoleReady packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncLocation(TCPGlobalMapClient peer, Packet.GM2S.SyncLocation packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncHp(TCPGlobalMapClient peer, Packet.GM2S.SyncHp packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvAddDrops(TCPGlobalMapClient peer, Packet.GM2S.AddDrops packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvAddKill(TCPGlobalMapClient peer, Packet.GM2S.AddKill packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncDurability(TCPGlobalMapClient peer, Packet.GM2S.SyncDurability packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncEndMine(TCPGlobalMapClient peer, Packet.GM2S.SyncEndMine packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvKickRoleFromMap(TCPGlobalMapClient peer, Packet.GM2S.KickRoleFromMap packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvRoleUseItemSkillSuc(TCPGlobalMapClient peer, Packet.GM2S.RoleUseItemSkillSuc packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvUpdateRoleFightState(TCPGlobalMapClient peer, Packet.GM2S.UpdateRoleFightState packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncRolePetHp(TCPGlobalMapClient peer, Packet.GM2S.SyncRolePetHp packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncArmorVal(TCPGlobalMapClient peer, Packet.GM2S.SyncArmorVal packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvSyncWeaponMaster(TCPGlobalMapClient peer, Packet.GM2S.SyncWeaponMaster packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvRolePickUpDrops(TCPGlobalMapClient peer, Packet.GM2S.RolePickUpDrops packet)
	{
		// TODO
	}

	public void onTCPGlobalMapClientRecvRolePickUpRareDrops(TCPGlobalMapClient peer, Packet.GM2S.RolePickUpRareDrops packet)
	{
		// TODO
	}

	public void onTCPFightClientOpen(TCPFightClient peer)
	{
		// TODO
	}

	public void onTCPFightClientOpenFailed(TCPFightClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightClientClose(TCPFightClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPFightClientRecvKeepAlive(TCPFightClient peer, Packet.F2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncGSRankStart(TCPFightClient peer, Packet.F2S.SyncGSRankStart packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncGSRank(TCPFightClient peer, Packet.F2S.SyncGSRank packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncGSRankEnd(TCPFightClient peer, Packet.F2S.SyncGSRankEnd packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleJoinForceWarRes(TCPFightClient peer, Packet.F2S.RoleJoinForceWarRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleQuitForceWarRes(TCPFightClient peer, Packet.F2S.RoleQuitForceWarRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleEnterForceWar(TCPFightClient peer, Packet.F2S.RoleEnterForceWar packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncForceWarMapStart(TCPFightClient peer, Packet.F2S.SyncForceWarMapStart packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncForceWarMapEnd(TCPFightClient peer, Packet.F2S.SyncForceWarMapEnd packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncMapCopyTimeOut(TCPFightClient peer, Packet.F2S.SyncMapCopyTimeOut packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvReceiveMsgFight(TCPFightClient peer, Packet.F2S.ReceiveMsgFight packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSingleJoinSuperArenaRes(TCPFightClient peer, Packet.F2S.SingleJoinSuperArenaRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSingleQuitSuperArenaRes(TCPFightClient peer, Packet.F2S.SingleQuitSuperArenaRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvTeamJoinSuperArenaRes(TCPFightClient peer, Packet.F2S.TeamJoinSuperArenaRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvTeamQuitSuperArenaRes(TCPFightClient peer, Packet.F2S.TeamQuitSuperArenaRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvQueryTeamMembersRes(TCPFightClient peer, Packet.F2S.QueryTeamMembersRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvCreateMapCopy(TCPFightClient peer, Packet.F2S.CreateMapCopy packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleEnterSuperArena(TCPFightClient peer, Packet.F2S.RoleEnterSuperArena packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncSuperArenaStart(TCPFightClient peer, Packet.F2S.SyncSuperArenaStart packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncSuperArenaMapEnd(TCPFightClient peer, Packet.F2S.SyncSuperArenaMapEnd packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSuperArenaMatchResult(TCPFightClient peer, Packet.F2S.SuperArenaMatchResult packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncRoleFightTeam(TCPFightClient peer, Packet.F2S.SyncRoleFightTeam packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvTeamLeaderChange(TCPFightClient peer, Packet.F2S.TeamLeaderChange packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvMemberLeaveTeam(TCPFightClient peer, Packet.F2S.MemberLeaveTeam packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvTeamMemberUpdateHpTrans(TCPFightClient peer, Packet.F2S.TeamMemberUpdateHpTrans packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvFightTeamDissolve(TCPFightClient peer, Packet.F2S.FightTeamDissolve packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvQueryTeamMemberRes(TCPFightClient peer, Packet.F2S.QueryTeamMemberRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvEnterSuperArenaRace(TCPFightClient peer, Packet.F2S.EnterSuperArenaRace packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvForceWarMatchResult(TCPFightClient peer, Packet.F2S.ForceWarMatchResult packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvTeamJoinForceWarRes(TCPFightClient peer, Packet.F2S.TeamJoinForceWarRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvTeamQuitForceWarRes(TCPFightClient peer, Packet.F2S.TeamQuitForceWarRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncRoleDemonHoleRes(TCPFightClient peer, Packet.F2S.SyncRoleDemonHoleRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleJoinDemonHoleRes(TCPFightClient peer, Packet.F2S.RoleJoinDemonHoleRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleChangeDemonHoleFloorRes(TCPFightClient peer, Packet.F2S.RoleChangeDemonHoleFloorRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleDemonHoleBattleRes(TCPFightClient peer, Packet.F2S.RoleDemonHoleBattleRes packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvRoleEnterDemonHoleMap(TCPFightClient peer, Packet.F2S.RoleEnterDemonHoleMap packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncDemonHoleMapEnd(TCPFightClient peer, Packet.F2S.SyncDemonHoleMapEnd packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncGSCreateNewTeam(TCPFightClient peer, Packet.F2S.SyncGSCreateNewTeam packet)
	{
		// TODO
	}

	public void onTCPFightClientRecvSyncRoleChatRoom(TCPFightClient peer, Packet.F2S.SyncRoleChatRoom packet)
	{
		// TODO
	}

	public void onTCPExchangeClientOpen(TCPExchangeClient peer)
	{
		// TODO
	}

	public void onTCPExchangeClientOpenFailed(TCPExchangeClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPExchangeClientClose(TCPExchangeClient peer, ket.kio.ErrorCode errcode)
	{
		// TODO
	}

	public void onTCPExchangeClientRecvKeepAlive(TCPExchangeClient peer, Packet.E2S.KeepAlive packet)
	{
		// TODO
	}

	public void onTCPExchangeClientRecvReceiveMsg(TCPExchangeClient peer, Packet.E2S.ReceiveMsg packet)
	{
		// TODO
	}

	public void onTCPExchangeClientRecvSocialMsgRes(TCPExchangeClient peer, Packet.E2S.SocialMsgRes packet)
	{
		// TODO
	}

	//// end handlers.
}
