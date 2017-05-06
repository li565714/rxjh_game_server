//// modified by ket.kio.RPCGen at Wed Jun 29 19:13:54 CST 2016.
//
//package i3k.gs;
//
//import ket.kio.PacketDecoder;
//import ket.kio.PacketEncoder;
//import ket.kio.TCPClient;
//import ket.kio.SimplePacket;
//
//import i3k.rpc.ABaseDencoder;
//import i3k.rpc.Packet;
//
//public class TCPClanClient extends TCPClient<SimplePacket>
//{
//
//	public TCPClanClient(RPCManager managerRPC)
//	{
//		super(managerRPC.getNetManager());
//		this.managerRPC = managerRPC;
//	}
//
//	private class Dencoder extends ABaseDencoder
//	{
//		@Override
//		public boolean doCheckPacketType(int ptype)
//		{
//			switch( ptype )
//			{
//			// clan to server
//			case Packet.eClan2SPKTKeepAlive:
//			case Packet.eClan2SPKTAdjustTimeOffset:
//			case Packet.eClan2SPKTQueryRoleClansRes:
//			case Packet.eClan2SPKTSyncClanRes:
//			case Packet.eClan2SPKTQueryClansRes:
//			case Packet.eClan2SPKTQueryClanByIdRes:
//			case Packet.eClan2SPKTCreateClanRes:
//			case Packet.eClan2SPKTApplyAddClanRes:
//			case Packet.eClan2SPKTRatifyAddClanRes:
//			case Packet.eClan2SPKTGetClanApplicationsRes:
//			case Packet.eClan2SPKTGetClanMembersRes:
//			case Packet.eClan2SPKTClanKickMemberRes:
//			case Packet.eClan2SPKTClanMemberLeaveRes:
//			case Packet.eClan2SPKTClanDisbandRes:
//			case Packet.eClan2SPKTClanAppliedRes:
//			case Packet.eClan2SPKTClanAppointElderRes:
//			case Packet.eClan2SPKTClanUplevelRes:
//			case Packet.eClan2SPKTClanRecruitRes:
//			case Packet.eClan2SPKTClanCancelElderRes:
//			case Packet.eClan2SPKTClanShoutuRes:
//			case Packet.eClan2SPKTClanShoutuSpeedupRes:
//			case Packet.eClan2SPKTClanShoutuFinishRes:
//			case Packet.eClan2SPKTClanBiwuStartRes:
//			case Packet.eClan2SPKTClanBiwuSpeedupRes:
//			case Packet.eClan2SPKTClanBiwuFinishRes:
//			case Packet.eClan2SPKTClanBushiStartRes:
//			case Packet.eClan2SPKTClanChuandaoStartRes:
//			case Packet.eClan2SPKTClanRushTollgateToExpRes:
//			case Packet.eClan2SPKTClanRushTollgateToItemRes:
//			case Packet.eClan2SPKTClanSyncTaskLibRes:
//			case Packet.eClan2SPKTClanSyncSelfTaskRes:
//			case Packet.eClan2SPKTClanAutoRefreshTaskRes:
//			case Packet.eClan2SPKTClanReceiveTaskRes:
//			case Packet.eClan2SPKTClanFinishTaskRes:
//			case Packet.eClan2SPKTClanDiscardTaskRes:
//			case Packet.eClan2SPKTClanSyncHistoryRes:
//			case Packet.eClan2SPKTClanRecoverGenDiscipleRes:
//			case Packet.eClan2SPKTClanOreBuildUpLevelRes:
//			case Packet.eClan2SPKTClanSyncOreRes:
//			case Packet.eClan2SPKTClanOreOccupyRes:
//			case Packet.eClan2SPKTClanOreOccupyFinishRes:
//			case Packet.eClan2SPKTClanSearchOreSyncRes:
//			case Packet.eClan2SPKTClanOreOwnerPetSyncRes:
//			case Packet.eClan2SPKTClanOreBorrowPetRes:
//			case Packet.eClan2SPKTClanOreHarryRes:
//			case Packet.eClan2SPKTClanOwnerAttriAdditionRes:
//			case Packet.eClan2SPKTClanBuyDoPowerRes:
//			case Packet.eClan2SPKTClanGetEliteDiscipleRes:
//			case Packet.eClan2SPKTClanCancelDisbandRes:
//			case Packet.eClan2SPKTClanOnRoleLoginRes:
//			case Packet.eClan2SPKTClanGetBaseRankRes:
//			case Packet.eClan2SPKTClanModifyRankRes:
//			case Packet.eClan2SPKTClanSetAttackTeamRes:
//			case Packet.eClan2SPKTClanSetDefendTeamRes:
//			case Packet.eClan2SPKTClanFindEnemyRes:
//			case Packet.eClan2SPKTClanGetEnemyRes:
//			case Packet.eClan2SPKTClanBattleSeekhelpRes:
//			case Packet.eClan2SPKTClanBattleHelpRes:
//			case Packet.eClan2SPKTClanBattleHelpForwardReq:
//			case Packet.eClan2SPKTClanBattleHelpFightStartRes:
//			case Packet.eClan2SPKTClanBattleHelpFightStartForwardReq:
//			case Packet.eClan2SPKTClanBattleHelpFightEndRes:
//			case Packet.eClan2SPKTClanBattleHelpFightEndForwardReq:
//			case Packet.eClan2SPKTClanSearchOreRes:
//			case Packet.eClan2SPKTClanSearchOreForwardReq:
//			case Packet.eClan2SPKTClanBattleAttackRes:
//			case Packet.eClan2SPKTClanBattleAttackForwardReq:
//			case Packet.eClan2SPKTClanMovePositionRes:
//			case Packet.eClan2SPKTClanFinishTaskOwnerRewardForwardReq:
//			case Packet.eClan2SPKTClanBattleFightStartRes:
//			case Packet.eClan2SPKTClanBattleFightStartForwardReq:
//			case Packet.eClan2SPKTClanBattleFightEndRes:
//			case Packet.eClan2SPKTClanBattleFightEndForwardReq:
//			case Packet.eClan2SPKTClanBattleFightExitRes:
//			case Packet.eClan2SPKTClanBattleFightExitForwardReq:
//			case Packet.eClan2SPKTClanOwnerFightDataRes:
//			case Packet.eClan2SPKTClanOwnerFightDataForwardReq:
//			case Packet.eClan2SPKTClanEnemyFightDataRes:
//			case Packet.eClan2SPKTClanEnemyFightDataForwardReq:
//			case Packet.eClan2SPKTClanDiyskillShareRes:
//			case Packet.eClan2SPKTClanDiyskillCancelShareRes:
//			case Packet.eClan2SPKTClanDiyskillBorrowRes:
//			case Packet.eClan2SPKTClanDiyskillGetShareRes:
//			case Packet.eClan2SPKTClanDiyskillShareAwardRes:
//			case Packet.eClan2SPKTQueryClanByNameRes:
//			case Packet.eClan2SPKTClanBuyPrestigeRes:
//			case Packet.eClan2SPKTClanRatifyAddForwardReq:
//			case Packet.eClan2SPKTClanOreOwnerPetSyncForwardReq:
//			case Packet.eClan2SPKTClanOreBorrowPetForwardReq:
//			case Packet.eClan2SPKTClanGetTaskEnemyRes:
//			case Packet.eClan2SPKTClanGetTaskEnemyForwardReq:
//			case Packet.eClan2SPKTClanDayRefreshRes:
//			case Packet.eClan2SPKTApplyAddClanForwardReq:
//			case Packet.eClan2SPKTClanOreHarryForwardReq:
//			case Packet.eClan2SPKTClanGetEnemyForwardReq:
//			case Packet.eClan2SPKTClanGetNearbyClanRes:
//				return true;
//			default:
//				break;
//			}
//			return false;
//		}
//	}
//
//	@Override
//	public PacketEncoder<SimplePacket> getEncoder()
//	{
//		return dencoder;
//	}
//
//	@Override
//	public PacketDecoder<SimplePacket> getDecoder()
//	{
//		return dencoder;
//	}
//
//	@Override
//	public void onOpen()
//	{
//		managerRPC.onTCPClanClientOpen(this);
//	}
//
//	@Override
//	public void onOpenFailed(ket.kio.ErrorCode errcode)
//	{
//		managerRPC.onTCPClanClientOpenFailed(this, errcode);
//	}
//
//	@Override
//	public void onClose(ket.kio.ErrorCode errcode)
//	{
//		managerRPC.onTCPClanClientClose(this, errcode);
//	}
//
//	@Override
//	public void onPacketRecv(SimplePacket packet)
//	{
//		switch( packet.getType() )
//		{
//		// clan to server
//		case Packet.eClan2SPKTKeepAlive:
//			{
//				Packet.Clan2S.KeepAlive p = (Packet.Clan2S.KeepAlive)packet;
//				managerRPC.onTCPClanClientRecvKeepAlive(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTAdjustTimeOffset:
//			{
//				Packet.Clan2S.AdjustTimeOffset p = (Packet.Clan2S.AdjustTimeOffset)packet;
//				managerRPC.onTCPClanClientRecvAdjustTimeOffset(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTQueryRoleClansRes:
//			{
//				Packet.Clan2S.QueryRoleClansRes p = (Packet.Clan2S.QueryRoleClansRes)packet;
//				managerRPC.onTCPClanClientRecvQueryRoleClansRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTSyncClanRes:
//			{
//				Packet.Clan2S.SyncClanRes p = (Packet.Clan2S.SyncClanRes)packet;
//				managerRPC.onTCPClanClientRecvSyncClanRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTQueryClansRes:
//			{
//				Packet.Clan2S.QueryClansRes p = (Packet.Clan2S.QueryClansRes)packet;
//				managerRPC.onTCPClanClientRecvQueryClansRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTQueryClanByIdRes:
//			{
//				Packet.Clan2S.QueryClanByIdRes p = (Packet.Clan2S.QueryClanByIdRes)packet;
//				managerRPC.onTCPClanClientRecvQueryClanByIdRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTCreateClanRes:
//			{
//				Packet.Clan2S.CreateClanRes p = (Packet.Clan2S.CreateClanRes)packet;
//				managerRPC.onTCPClanClientRecvCreateClanRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTApplyAddClanRes:
//			{
//				Packet.Clan2S.ApplyAddClanRes p = (Packet.Clan2S.ApplyAddClanRes)packet;
//				managerRPC.onTCPClanClientRecvApplyAddClanRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTRatifyAddClanRes:
//			{
//				Packet.Clan2S.RatifyAddClanRes p = (Packet.Clan2S.RatifyAddClanRes)packet;
//				managerRPC.onTCPClanClientRecvRatifyAddClanRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTGetClanApplicationsRes:
//			{
//				Packet.Clan2S.GetClanApplicationsRes p = (Packet.Clan2S.GetClanApplicationsRes)packet;
//				managerRPC.onTCPClanClientRecvGetClanApplicationsRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTGetClanMembersRes:
//			{
//				Packet.Clan2S.GetClanMembersRes p = (Packet.Clan2S.GetClanMembersRes)packet;
//				managerRPC.onTCPClanClientRecvGetClanMembersRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanKickMemberRes:
//			{
//				Packet.Clan2S.ClanKickMemberRes p = (Packet.Clan2S.ClanKickMemberRes)packet;
//				managerRPC.onTCPClanClientRecvClanKickMemberRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanMemberLeaveRes:
//			{
//				Packet.Clan2S.ClanMemberLeaveRes p = (Packet.Clan2S.ClanMemberLeaveRes)packet;
//				managerRPC.onTCPClanClientRecvClanMemberLeaveRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDisbandRes:
//			{
//				Packet.Clan2S.ClanDisbandRes p = (Packet.Clan2S.ClanDisbandRes)packet;
//				managerRPC.onTCPClanClientRecvClanDisbandRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanAppliedRes:
//			{
//				Packet.Clan2S.ClanAppliedRes p = (Packet.Clan2S.ClanAppliedRes)packet;
//				managerRPC.onTCPClanClientRecvClanAppliedRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanAppointElderRes:
//			{
//				Packet.Clan2S.ClanAppointElderRes p = (Packet.Clan2S.ClanAppointElderRes)packet;
//				managerRPC.onTCPClanClientRecvClanAppointElderRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanUplevelRes:
//			{
//				Packet.Clan2S.ClanUplevelRes p = (Packet.Clan2S.ClanUplevelRes)packet;
//				managerRPC.onTCPClanClientRecvClanUplevelRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanRecruitRes:
//			{
//				Packet.Clan2S.ClanRecruitRes p = (Packet.Clan2S.ClanRecruitRes)packet;
//				managerRPC.onTCPClanClientRecvClanRecruitRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanCancelElderRes:
//			{
//				Packet.Clan2S.ClanCancelElderRes p = (Packet.Clan2S.ClanCancelElderRes)packet;
//				managerRPC.onTCPClanClientRecvClanCancelElderRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanShoutuRes:
//			{
//				Packet.Clan2S.ClanShoutuRes p = (Packet.Clan2S.ClanShoutuRes)packet;
//				managerRPC.onTCPClanClientRecvClanShoutuRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanShoutuSpeedupRes:
//			{
//				Packet.Clan2S.ClanShoutuSpeedupRes p = (Packet.Clan2S.ClanShoutuSpeedupRes)packet;
//				managerRPC.onTCPClanClientRecvClanShoutuSpeedupRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanShoutuFinishRes:
//			{
//				Packet.Clan2S.ClanShoutuFinishRes p = (Packet.Clan2S.ClanShoutuFinishRes)packet;
//				managerRPC.onTCPClanClientRecvClanShoutuFinishRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBiwuStartRes:
//			{
//				Packet.Clan2S.ClanBiwuStartRes p = (Packet.Clan2S.ClanBiwuStartRes)packet;
//				managerRPC.onTCPClanClientRecvClanBiwuStartRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBiwuSpeedupRes:
//			{
//				Packet.Clan2S.ClanBiwuSpeedupRes p = (Packet.Clan2S.ClanBiwuSpeedupRes)packet;
//				managerRPC.onTCPClanClientRecvClanBiwuSpeedupRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBiwuFinishRes:
//			{
//				Packet.Clan2S.ClanBiwuFinishRes p = (Packet.Clan2S.ClanBiwuFinishRes)packet;
//				managerRPC.onTCPClanClientRecvClanBiwuFinishRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBushiStartRes:
//			{
//				Packet.Clan2S.ClanBushiStartRes p = (Packet.Clan2S.ClanBushiStartRes)packet;
//				managerRPC.onTCPClanClientRecvClanBushiStartRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanChuandaoStartRes:
//			{
//				Packet.Clan2S.ClanChuandaoStartRes p = (Packet.Clan2S.ClanChuandaoStartRes)packet;
//				managerRPC.onTCPClanClientRecvClanChuandaoStartRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanRushTollgateToExpRes:
//			{
//				Packet.Clan2S.ClanRushTollgateToExpRes p = (Packet.Clan2S.ClanRushTollgateToExpRes)packet;
//				managerRPC.onTCPClanClientRecvClanRushTollgateToExpRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanRushTollgateToItemRes:
//			{
//				Packet.Clan2S.ClanRushTollgateToItemRes p = (Packet.Clan2S.ClanRushTollgateToItemRes)packet;
//				managerRPC.onTCPClanClientRecvClanRushTollgateToItemRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSyncTaskLibRes:
//			{
//				Packet.Clan2S.ClanSyncTaskLibRes p = (Packet.Clan2S.ClanSyncTaskLibRes)packet;
//				managerRPC.onTCPClanClientRecvClanSyncTaskLibRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSyncSelfTaskRes:
//			{
//				Packet.Clan2S.ClanSyncSelfTaskRes p = (Packet.Clan2S.ClanSyncSelfTaskRes)packet;
//				managerRPC.onTCPClanClientRecvClanSyncSelfTaskRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanAutoRefreshTaskRes:
//			{
//				Packet.Clan2S.ClanAutoRefreshTaskRes p = (Packet.Clan2S.ClanAutoRefreshTaskRes)packet;
//				managerRPC.onTCPClanClientRecvClanAutoRefreshTaskRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanReceiveTaskRes:
//			{
//				Packet.Clan2S.ClanReceiveTaskRes p = (Packet.Clan2S.ClanReceiveTaskRes)packet;
//				managerRPC.onTCPClanClientRecvClanReceiveTaskRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanFinishTaskRes:
//			{
//				Packet.Clan2S.ClanFinishTaskRes p = (Packet.Clan2S.ClanFinishTaskRes)packet;
//				managerRPC.onTCPClanClientRecvClanFinishTaskRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDiscardTaskRes:
//			{
//				Packet.Clan2S.ClanDiscardTaskRes p = (Packet.Clan2S.ClanDiscardTaskRes)packet;
//				managerRPC.onTCPClanClientRecvClanDiscardTaskRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSyncHistoryRes:
//			{
//				Packet.Clan2S.ClanSyncHistoryRes p = (Packet.Clan2S.ClanSyncHistoryRes)packet;
//				managerRPC.onTCPClanClientRecvClanSyncHistoryRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanRecoverGenDiscipleRes:
//			{
//				Packet.Clan2S.ClanRecoverGenDiscipleRes p = (Packet.Clan2S.ClanRecoverGenDiscipleRes)packet;
//				managerRPC.onTCPClanClientRecvClanRecoverGenDiscipleRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreBuildUpLevelRes:
//			{
//				Packet.Clan2S.ClanOreBuildUpLevelRes p = (Packet.Clan2S.ClanOreBuildUpLevelRes)packet;
//				managerRPC.onTCPClanClientRecvClanOreBuildUpLevelRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSyncOreRes:
//			{
//				Packet.Clan2S.ClanSyncOreRes p = (Packet.Clan2S.ClanSyncOreRes)packet;
//				managerRPC.onTCPClanClientRecvClanSyncOreRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreOccupyRes:
//			{
//				Packet.Clan2S.ClanOreOccupyRes p = (Packet.Clan2S.ClanOreOccupyRes)packet;
//				managerRPC.onTCPClanClientRecvClanOreOccupyRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreOccupyFinishRes:
//			{
//				Packet.Clan2S.ClanOreOccupyFinishRes p = (Packet.Clan2S.ClanOreOccupyFinishRes)packet;
//				managerRPC.onTCPClanClientRecvClanOreOccupyFinishRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSearchOreSyncRes:
//			{
//				Packet.Clan2S.ClanSearchOreSyncRes p = (Packet.Clan2S.ClanSearchOreSyncRes)packet;
//				managerRPC.onTCPClanClientRecvClanSearchOreSyncRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreOwnerPetSyncRes:
//			{
//				Packet.Clan2S.ClanOreOwnerPetSyncRes p = (Packet.Clan2S.ClanOreOwnerPetSyncRes)packet;
//				managerRPC.onTCPClanClientRecvClanOreOwnerPetSyncRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreBorrowPetRes:
//			{
//				Packet.Clan2S.ClanOreBorrowPetRes p = (Packet.Clan2S.ClanOreBorrowPetRes)packet;
//				managerRPC.onTCPClanClientRecvClanOreBorrowPetRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreHarryRes:
//			{
//				Packet.Clan2S.ClanOreHarryRes p = (Packet.Clan2S.ClanOreHarryRes)packet;
//				managerRPC.onTCPClanClientRecvClanOreHarryRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOwnerAttriAdditionRes:
//			{
//				Packet.Clan2S.ClanOwnerAttriAdditionRes p = (Packet.Clan2S.ClanOwnerAttriAdditionRes)packet;
//				managerRPC.onTCPClanClientRecvClanOwnerAttriAdditionRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBuyDoPowerRes:
//			{
//				Packet.Clan2S.ClanBuyDoPowerRes p = (Packet.Clan2S.ClanBuyDoPowerRes)packet;
//				managerRPC.onTCPClanClientRecvClanBuyDoPowerRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanGetEliteDiscipleRes:
//			{
//				Packet.Clan2S.ClanGetEliteDiscipleRes p = (Packet.Clan2S.ClanGetEliteDiscipleRes)packet;
//				managerRPC.onTCPClanClientRecvClanGetEliteDiscipleRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanCancelDisbandRes:
//			{
//				Packet.Clan2S.ClanCancelDisbandRes p = (Packet.Clan2S.ClanCancelDisbandRes)packet;
//				managerRPC.onTCPClanClientRecvClanCancelDisbandRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOnRoleLoginRes:
//			{
//				Packet.Clan2S.ClanOnRoleLoginRes p = (Packet.Clan2S.ClanOnRoleLoginRes)packet;
//				managerRPC.onTCPClanClientRecvClanOnRoleLoginRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanGetBaseRankRes:
//			{
//				Packet.Clan2S.ClanGetBaseRankRes p = (Packet.Clan2S.ClanGetBaseRankRes)packet;
//				managerRPC.onTCPClanClientRecvClanGetBaseRankRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanModifyRankRes:
//			{
//				Packet.Clan2S.ClanModifyRankRes p = (Packet.Clan2S.ClanModifyRankRes)packet;
//				managerRPC.onTCPClanClientRecvClanModifyRankRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSetAttackTeamRes:
//			{
//				Packet.Clan2S.ClanSetAttackTeamRes p = (Packet.Clan2S.ClanSetAttackTeamRes)packet;
//				managerRPC.onTCPClanClientRecvClanSetAttackTeamRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSetDefendTeamRes:
//			{
//				Packet.Clan2S.ClanSetDefendTeamRes p = (Packet.Clan2S.ClanSetDefendTeamRes)packet;
//				managerRPC.onTCPClanClientRecvClanSetDefendTeamRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanFindEnemyRes:
//			{
//				Packet.Clan2S.ClanFindEnemyRes p = (Packet.Clan2S.ClanFindEnemyRes)packet;
//				managerRPC.onTCPClanClientRecvClanFindEnemyRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanGetEnemyRes:
//			{
//				Packet.Clan2S.ClanGetEnemyRes p = (Packet.Clan2S.ClanGetEnemyRes)packet;
//				managerRPC.onTCPClanClientRecvClanGetEnemyRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleSeekhelpRes:
//			{
//				Packet.Clan2S.ClanBattleSeekhelpRes p = (Packet.Clan2S.ClanBattleSeekhelpRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleSeekhelpRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleHelpRes:
//			{
//				Packet.Clan2S.ClanBattleHelpRes p = (Packet.Clan2S.ClanBattleHelpRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleHelpRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleHelpForwardReq:
//			{
//				Packet.Clan2S.ClanBattleHelpForwardReq p = (Packet.Clan2S.ClanBattleHelpForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanBattleHelpForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleHelpFightStartRes:
//			{
//				Packet.Clan2S.ClanBattleHelpFightStartRes p = (Packet.Clan2S.ClanBattleHelpFightStartRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleHelpFightStartRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleHelpFightStartForwardReq:
//			{
//				Packet.Clan2S.ClanBattleHelpFightStartForwardReq p = (Packet.Clan2S.ClanBattleHelpFightStartForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanBattleHelpFightStartForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleHelpFightEndRes:
//			{
//				Packet.Clan2S.ClanBattleHelpFightEndRes p = (Packet.Clan2S.ClanBattleHelpFightEndRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleHelpFightEndRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleHelpFightEndForwardReq:
//			{
//				Packet.Clan2S.ClanBattleHelpFightEndForwardReq p = (Packet.Clan2S.ClanBattleHelpFightEndForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanBattleHelpFightEndForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSearchOreRes:
//			{
//				Packet.Clan2S.ClanSearchOreRes p = (Packet.Clan2S.ClanSearchOreRes)packet;
//				managerRPC.onTCPClanClientRecvClanSearchOreRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanSearchOreForwardReq:
//			{
//				Packet.Clan2S.ClanSearchOreForwardReq p = (Packet.Clan2S.ClanSearchOreForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanSearchOreForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleAttackRes:
//			{
//				Packet.Clan2S.ClanBattleAttackRes p = (Packet.Clan2S.ClanBattleAttackRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleAttackRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleAttackForwardReq:
//			{
//				Packet.Clan2S.ClanBattleAttackForwardReq p = (Packet.Clan2S.ClanBattleAttackForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanBattleAttackForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanMovePositionRes:
//			{
//				Packet.Clan2S.ClanMovePositionRes p = (Packet.Clan2S.ClanMovePositionRes)packet;
//				managerRPC.onTCPClanClientRecvClanMovePositionRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanFinishTaskOwnerRewardForwardReq:
//			{
//				Packet.Clan2S.ClanFinishTaskOwnerRewardForwardReq p = (Packet.Clan2S.ClanFinishTaskOwnerRewardForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanFinishTaskOwnerRewardForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleFightStartRes:
//			{
//				Packet.Clan2S.ClanBattleFightStartRes p = (Packet.Clan2S.ClanBattleFightStartRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleFightStartRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleFightStartForwardReq:
//			{
//				Packet.Clan2S.ClanBattleFightStartForwardReq p = (Packet.Clan2S.ClanBattleFightStartForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanBattleFightStartForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleFightEndRes:
//			{
//				Packet.Clan2S.ClanBattleFightEndRes p = (Packet.Clan2S.ClanBattleFightEndRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleFightEndRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleFightEndForwardReq:
//			{
//				Packet.Clan2S.ClanBattleFightEndForwardReq p = (Packet.Clan2S.ClanBattleFightEndForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanBattleFightEndForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleFightExitRes:
//			{
//				Packet.Clan2S.ClanBattleFightExitRes p = (Packet.Clan2S.ClanBattleFightExitRes)packet;
//				managerRPC.onTCPClanClientRecvClanBattleFightExitRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBattleFightExitForwardReq:
//			{
//				Packet.Clan2S.ClanBattleFightExitForwardReq p = (Packet.Clan2S.ClanBattleFightExitForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanBattleFightExitForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOwnerFightDataRes:
//			{
//				Packet.Clan2S.ClanOwnerFightDataRes p = (Packet.Clan2S.ClanOwnerFightDataRes)packet;
//				managerRPC.onTCPClanClientRecvClanOwnerFightDataRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOwnerFightDataForwardReq:
//			{
//				Packet.Clan2S.ClanOwnerFightDataForwardReq p = (Packet.Clan2S.ClanOwnerFightDataForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanOwnerFightDataForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanEnemyFightDataRes:
//			{
//				Packet.Clan2S.ClanEnemyFightDataRes p = (Packet.Clan2S.ClanEnemyFightDataRes)packet;
//				managerRPC.onTCPClanClientRecvClanEnemyFightDataRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanEnemyFightDataForwardReq:
//			{
//				Packet.Clan2S.ClanEnemyFightDataForwardReq p = (Packet.Clan2S.ClanEnemyFightDataForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanEnemyFightDataForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDiyskillShareRes:
//			{
//				Packet.Clan2S.ClanDiyskillShareRes p = (Packet.Clan2S.ClanDiyskillShareRes)packet;
//				managerRPC.onTCPClanClientRecvClanDiyskillShareRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDiyskillCancelShareRes:
//			{
//				Packet.Clan2S.ClanDiyskillCancelShareRes p = (Packet.Clan2S.ClanDiyskillCancelShareRes)packet;
//				managerRPC.onTCPClanClientRecvClanDiyskillCancelShareRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDiyskillBorrowRes:
//			{
//				Packet.Clan2S.ClanDiyskillBorrowRes p = (Packet.Clan2S.ClanDiyskillBorrowRes)packet;
//				managerRPC.onTCPClanClientRecvClanDiyskillBorrowRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDiyskillGetShareRes:
//			{
//				Packet.Clan2S.ClanDiyskillGetShareRes p = (Packet.Clan2S.ClanDiyskillGetShareRes)packet;
//				managerRPC.onTCPClanClientRecvClanDiyskillGetShareRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDiyskillShareAwardRes:
//			{
//				Packet.Clan2S.ClanDiyskillShareAwardRes p = (Packet.Clan2S.ClanDiyskillShareAwardRes)packet;
//				managerRPC.onTCPClanClientRecvClanDiyskillShareAwardRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTQueryClanByNameRes:
//			{
//				Packet.Clan2S.QueryClanByNameRes p = (Packet.Clan2S.QueryClanByNameRes)packet;
//				managerRPC.onTCPClanClientRecvQueryClanByNameRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanBuyPrestigeRes:
//			{
//				Packet.Clan2S.ClanBuyPrestigeRes p = (Packet.Clan2S.ClanBuyPrestigeRes)packet;
//				managerRPC.onTCPClanClientRecvClanBuyPrestigeRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanRatifyAddForwardReq:
//			{
//				Packet.Clan2S.ClanRatifyAddForwardReq p = (Packet.Clan2S.ClanRatifyAddForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanRatifyAddForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreOwnerPetSyncForwardReq:
//			{
//				Packet.Clan2S.ClanOreOwnerPetSyncForwardReq p = (Packet.Clan2S.ClanOreOwnerPetSyncForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanOreOwnerPetSyncForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreBorrowPetForwardReq:
//			{
//				Packet.Clan2S.ClanOreBorrowPetForwardReq p = (Packet.Clan2S.ClanOreBorrowPetForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanOreBorrowPetForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanGetTaskEnemyRes:
//			{
//				Packet.Clan2S.ClanGetTaskEnemyRes p = (Packet.Clan2S.ClanGetTaskEnemyRes)packet;
//				managerRPC.onTCPClanClientRecvClanGetTaskEnemyRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanGetTaskEnemyForwardReq:
//			{
//				Packet.Clan2S.ClanGetTaskEnemyForwardReq p = (Packet.Clan2S.ClanGetTaskEnemyForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanGetTaskEnemyForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanDayRefreshRes:
//			{
//				Packet.Clan2S.ClanDayRefreshRes p = (Packet.Clan2S.ClanDayRefreshRes)packet;
//				managerRPC.onTCPClanClientRecvClanDayRefreshRes(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTApplyAddClanForwardReq:
//			{
//				Packet.Clan2S.ApplyAddClanForwardReq p = (Packet.Clan2S.ApplyAddClanForwardReq)packet;
//				managerRPC.onTCPClanClientRecvApplyAddClanForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanOreHarryForwardReq:
//			{
//				Packet.Clan2S.ClanOreHarryForwardReq p = (Packet.Clan2S.ClanOreHarryForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanOreHarryForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanGetEnemyForwardReq:
//			{
//				Packet.Clan2S.ClanGetEnemyForwardReq p = (Packet.Clan2S.ClanGetEnemyForwardReq)packet;
//				managerRPC.onTCPClanClientRecvClanGetEnemyForwardReq(this, p);
//			}
//			break;
//		case Packet.eClan2SPKTClanGetNearbyClanRes:
//			{
//				Packet.Clan2S.ClanGetNearbyClanRes p = (Packet.Clan2S.ClanGetNearbyClanRes)packet;
//				managerRPC.onTCPClanClientRecvClanGetNearbyClanRes(this, p);
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//	//todo
//	private Dencoder dencoder = new Dencoder();
//	private RPCManager managerRPC;
//}
