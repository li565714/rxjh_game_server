// modified by ket.kio.RPCGen at Sat May 06 17:08:06 CST 2017.

package i3k.gs;

import ket.kio.PacketDecoder;
import ket.kio.PacketEncoder;
import ket.kio.TCPClient;
import ket.kio.SimplePacket;

import i3k.rpc.ABaseDencoder;
import i3k.rpc.Packet;

public class TCPFightClient extends TCPClient<SimplePacket>
{

	public TCPFightClient(RPCManager managerRPC)
	{
		super(managerRPC.getNetManager());
		this.managerRPC = managerRPC;
	}

	private class Dencoder extends ABaseDencoder
	{
		@Override
		public boolean doCheckPacketType(int ptype)
		{
			switch( ptype )
			{
			// fs to gs
			case Packet.eF2SPKTKeepAlive:
			case Packet.eF2SPKTSyncGSRankStart:
			case Packet.eF2SPKTSyncGSRank:
			case Packet.eF2SPKTSyncGSRankEnd:
			case Packet.eF2SPKTRoleJoinForceWarRes:
			case Packet.eF2SPKTRoleQuitForceWarRes:
			case Packet.eF2SPKTRoleEnterForceWar:
			case Packet.eF2SPKTSyncForceWarMapStart:
			case Packet.eF2SPKTSyncForceWarMapEnd:
			case Packet.eF2SPKTSyncMapCopyTimeOut:
			case Packet.eF2SPKTReceiveMsgFight:
			case Packet.eF2SPKTSingleJoinSuperArenaRes:
			case Packet.eF2SPKTSingleQuitSuperArenaRes:
			case Packet.eF2SPKTTeamJoinSuperArenaRes:
			case Packet.eF2SPKTTeamQuitSuperArenaRes:
			case Packet.eF2SPKTQueryTeamMembersRes:
			case Packet.eF2SPKTCreateMapCopy:
			case Packet.eF2SPKTRoleEnterSuperArena:
			case Packet.eF2SPKTSyncSuperArenaStart:
			case Packet.eF2SPKTSyncSuperArenaMapEnd:
			case Packet.eF2SPKTSuperArenaMatchResult:
			case Packet.eF2SPKTSyncRoleFightTeam:
			case Packet.eF2SPKTTeamLeaderChange:
			case Packet.eF2SPKTMemberLeaveTeam:
			case Packet.eF2SPKTTeamMemberUpdateHpTrans:
			case Packet.eF2SPKTFightTeamDissolve:
			case Packet.eF2SPKTQueryTeamMemberRes:
			case Packet.eF2SPKTEnterSuperArenaRace:
			case Packet.eF2SPKTForceWarMatchResult:
			case Packet.eF2SPKTTeamJoinForceWarRes:
			case Packet.eF2SPKTTeamQuitForceWarRes:
			case Packet.eF2SPKTSyncRoleDemonHoleRes:
			case Packet.eF2SPKTRoleJoinDemonHoleRes:
			case Packet.eF2SPKTRoleChangeDemonHoleFloorRes:
			case Packet.eF2SPKTRoleDemonHoleBattleRes:
			case Packet.eF2SPKTRoleEnterDemonHoleMap:
			case Packet.eF2SPKTSyncDemonHoleMapEnd:
			case Packet.eF2SPKTSyncGSCreateNewTeam:
			case Packet.eF2SPKTSyncRoleChatRoom:
				return true;
			default:
				break;
			}
			return false;
		}
	}

	@Override
	public PacketEncoder<SimplePacket> getEncoder()
	{
		return dencoder;
	}

	@Override
	public PacketDecoder<SimplePacket> getDecoder()
	{
		return dencoder;
	}

	@Override
	public void onOpen()
	{
		managerRPC.onTCPFightClientOpen(this);
	}

	@Override
	public void onOpenFailed(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightClientOpenFailed(this, errcode);
	}

	@Override
	public void onClose(ket.kio.ErrorCode errcode)
	{
		managerRPC.onTCPFightClientClose(this, errcode);
	}

	@Override
	public void onPacketRecv(SimplePacket packet)
	{
		switch( packet.getType() )
		{
		// fs to gs
		case Packet.eF2SPKTKeepAlive:
			{
				Packet.F2S.KeepAlive p = (Packet.F2S.KeepAlive)packet;
				managerRPC.onTCPFightClientRecvKeepAlive(this, p);
			}
			break;
		case Packet.eF2SPKTSyncGSRankStart:
			{
				Packet.F2S.SyncGSRankStart p = (Packet.F2S.SyncGSRankStart)packet;
				managerRPC.onTCPFightClientRecvSyncGSRankStart(this, p);
			}
			break;
		case Packet.eF2SPKTSyncGSRank:
			{
				Packet.F2S.SyncGSRank p = (Packet.F2S.SyncGSRank)packet;
				managerRPC.onTCPFightClientRecvSyncGSRank(this, p);
			}
			break;
		case Packet.eF2SPKTSyncGSRankEnd:
			{
				Packet.F2S.SyncGSRankEnd p = (Packet.F2S.SyncGSRankEnd)packet;
				managerRPC.onTCPFightClientRecvSyncGSRankEnd(this, p);
			}
			break;
		case Packet.eF2SPKTRoleJoinForceWarRes:
			{
				Packet.F2S.RoleJoinForceWarRes p = (Packet.F2S.RoleJoinForceWarRes)packet;
				managerRPC.onTCPFightClientRecvRoleJoinForceWarRes(this, p);
			}
			break;
		case Packet.eF2SPKTRoleQuitForceWarRes:
			{
				Packet.F2S.RoleQuitForceWarRes p = (Packet.F2S.RoleQuitForceWarRes)packet;
				managerRPC.onTCPFightClientRecvRoleQuitForceWarRes(this, p);
			}
			break;
		case Packet.eF2SPKTRoleEnterForceWar:
			{
				Packet.F2S.RoleEnterForceWar p = (Packet.F2S.RoleEnterForceWar)packet;
				managerRPC.onTCPFightClientRecvRoleEnterForceWar(this, p);
			}
			break;
		case Packet.eF2SPKTSyncForceWarMapStart:
			{
				Packet.F2S.SyncForceWarMapStart p = (Packet.F2S.SyncForceWarMapStart)packet;
				managerRPC.onTCPFightClientRecvSyncForceWarMapStart(this, p);
			}
			break;
		case Packet.eF2SPKTSyncForceWarMapEnd:
			{
				Packet.F2S.SyncForceWarMapEnd p = (Packet.F2S.SyncForceWarMapEnd)packet;
				managerRPC.onTCPFightClientRecvSyncForceWarMapEnd(this, p);
			}
			break;
		case Packet.eF2SPKTSyncMapCopyTimeOut:
			{
				Packet.F2S.SyncMapCopyTimeOut p = (Packet.F2S.SyncMapCopyTimeOut)packet;
				managerRPC.onTCPFightClientRecvSyncMapCopyTimeOut(this, p);
			}
			break;
		case Packet.eF2SPKTReceiveMsgFight:
			{
				Packet.F2S.ReceiveMsgFight p = (Packet.F2S.ReceiveMsgFight)packet;
				managerRPC.onTCPFightClientRecvReceiveMsgFight(this, p);
			}
			break;
		case Packet.eF2SPKTSingleJoinSuperArenaRes:
			{
				Packet.F2S.SingleJoinSuperArenaRes p = (Packet.F2S.SingleJoinSuperArenaRes)packet;
				managerRPC.onTCPFightClientRecvSingleJoinSuperArenaRes(this, p);
			}
			break;
		case Packet.eF2SPKTSingleQuitSuperArenaRes:
			{
				Packet.F2S.SingleQuitSuperArenaRes p = (Packet.F2S.SingleQuitSuperArenaRes)packet;
				managerRPC.onTCPFightClientRecvSingleQuitSuperArenaRes(this, p);
			}
			break;
		case Packet.eF2SPKTTeamJoinSuperArenaRes:
			{
				Packet.F2S.TeamJoinSuperArenaRes p = (Packet.F2S.TeamJoinSuperArenaRes)packet;
				managerRPC.onTCPFightClientRecvTeamJoinSuperArenaRes(this, p);
			}
			break;
		case Packet.eF2SPKTTeamQuitSuperArenaRes:
			{
				Packet.F2S.TeamQuitSuperArenaRes p = (Packet.F2S.TeamQuitSuperArenaRes)packet;
				managerRPC.onTCPFightClientRecvTeamQuitSuperArenaRes(this, p);
			}
			break;
		case Packet.eF2SPKTQueryTeamMembersRes:
			{
				Packet.F2S.QueryTeamMembersRes p = (Packet.F2S.QueryTeamMembersRes)packet;
				managerRPC.onTCPFightClientRecvQueryTeamMembersRes(this, p);
			}
			break;
		case Packet.eF2SPKTCreateMapCopy:
			{
				Packet.F2S.CreateMapCopy p = (Packet.F2S.CreateMapCopy)packet;
				managerRPC.onTCPFightClientRecvCreateMapCopy(this, p);
			}
			break;
		case Packet.eF2SPKTRoleEnterSuperArena:
			{
				Packet.F2S.RoleEnterSuperArena p = (Packet.F2S.RoleEnterSuperArena)packet;
				managerRPC.onTCPFightClientRecvRoleEnterSuperArena(this, p);
			}
			break;
		case Packet.eF2SPKTSyncSuperArenaStart:
			{
				Packet.F2S.SyncSuperArenaStart p = (Packet.F2S.SyncSuperArenaStart)packet;
				managerRPC.onTCPFightClientRecvSyncSuperArenaStart(this, p);
			}
			break;
		case Packet.eF2SPKTSyncSuperArenaMapEnd:
			{
				Packet.F2S.SyncSuperArenaMapEnd p = (Packet.F2S.SyncSuperArenaMapEnd)packet;
				managerRPC.onTCPFightClientRecvSyncSuperArenaMapEnd(this, p);
			}
			break;
		case Packet.eF2SPKTSuperArenaMatchResult:
			{
				Packet.F2S.SuperArenaMatchResult p = (Packet.F2S.SuperArenaMatchResult)packet;
				managerRPC.onTCPFightClientRecvSuperArenaMatchResult(this, p);
			}
			break;
		case Packet.eF2SPKTSyncRoleFightTeam:
			{
				Packet.F2S.SyncRoleFightTeam p = (Packet.F2S.SyncRoleFightTeam)packet;
				managerRPC.onTCPFightClientRecvSyncRoleFightTeam(this, p);
			}
			break;
		case Packet.eF2SPKTTeamLeaderChange:
			{
				Packet.F2S.TeamLeaderChange p = (Packet.F2S.TeamLeaderChange)packet;
				managerRPC.onTCPFightClientRecvTeamLeaderChange(this, p);
			}
			break;
		case Packet.eF2SPKTMemberLeaveTeam:
			{
				Packet.F2S.MemberLeaveTeam p = (Packet.F2S.MemberLeaveTeam)packet;
				managerRPC.onTCPFightClientRecvMemberLeaveTeam(this, p);
			}
			break;
		case Packet.eF2SPKTTeamMemberUpdateHpTrans:
			{
				Packet.F2S.TeamMemberUpdateHpTrans p = (Packet.F2S.TeamMemberUpdateHpTrans)packet;
				managerRPC.onTCPFightClientRecvTeamMemberUpdateHpTrans(this, p);
			}
			break;
		case Packet.eF2SPKTFightTeamDissolve:
			{
				Packet.F2S.FightTeamDissolve p = (Packet.F2S.FightTeamDissolve)packet;
				managerRPC.onTCPFightClientRecvFightTeamDissolve(this, p);
			}
			break;
		case Packet.eF2SPKTQueryTeamMemberRes:
			{
				Packet.F2S.QueryTeamMemberRes p = (Packet.F2S.QueryTeamMemberRes)packet;
				managerRPC.onTCPFightClientRecvQueryTeamMemberRes(this, p);
			}
			break;
		case Packet.eF2SPKTEnterSuperArenaRace:
			{
				Packet.F2S.EnterSuperArenaRace p = (Packet.F2S.EnterSuperArenaRace)packet;
				managerRPC.onTCPFightClientRecvEnterSuperArenaRace(this, p);
			}
			break;
		case Packet.eF2SPKTForceWarMatchResult:
			{
				Packet.F2S.ForceWarMatchResult p = (Packet.F2S.ForceWarMatchResult)packet;
				managerRPC.onTCPFightClientRecvForceWarMatchResult(this, p);
			}
			break;
		case Packet.eF2SPKTTeamJoinForceWarRes:
			{
				Packet.F2S.TeamJoinForceWarRes p = (Packet.F2S.TeamJoinForceWarRes)packet;
				managerRPC.onTCPFightClientRecvTeamJoinForceWarRes(this, p);
			}
			break;
		case Packet.eF2SPKTTeamQuitForceWarRes:
			{
				Packet.F2S.TeamQuitForceWarRes p = (Packet.F2S.TeamQuitForceWarRes)packet;
				managerRPC.onTCPFightClientRecvTeamQuitForceWarRes(this, p);
			}
			break;
		case Packet.eF2SPKTSyncRoleDemonHoleRes:
			{
				Packet.F2S.SyncRoleDemonHoleRes p = (Packet.F2S.SyncRoleDemonHoleRes)packet;
				managerRPC.onTCPFightClientRecvSyncRoleDemonHoleRes(this, p);
			}
			break;
		case Packet.eF2SPKTRoleJoinDemonHoleRes:
			{
				Packet.F2S.RoleJoinDemonHoleRes p = (Packet.F2S.RoleJoinDemonHoleRes)packet;
				managerRPC.onTCPFightClientRecvRoleJoinDemonHoleRes(this, p);
			}
			break;
		case Packet.eF2SPKTRoleChangeDemonHoleFloorRes:
			{
				Packet.F2S.RoleChangeDemonHoleFloorRes p = (Packet.F2S.RoleChangeDemonHoleFloorRes)packet;
				managerRPC.onTCPFightClientRecvRoleChangeDemonHoleFloorRes(this, p);
			}
			break;
		case Packet.eF2SPKTRoleDemonHoleBattleRes:
			{
				Packet.F2S.RoleDemonHoleBattleRes p = (Packet.F2S.RoleDemonHoleBattleRes)packet;
				managerRPC.onTCPFightClientRecvRoleDemonHoleBattleRes(this, p);
			}
			break;
		case Packet.eF2SPKTRoleEnterDemonHoleMap:
			{
				Packet.F2S.RoleEnterDemonHoleMap p = (Packet.F2S.RoleEnterDemonHoleMap)packet;
				managerRPC.onTCPFightClientRecvRoleEnterDemonHoleMap(this, p);
			}
			break;
		case Packet.eF2SPKTSyncDemonHoleMapEnd:
			{
				Packet.F2S.SyncDemonHoleMapEnd p = (Packet.F2S.SyncDemonHoleMapEnd)packet;
				managerRPC.onTCPFightClientRecvSyncDemonHoleMapEnd(this, p);
			}
			break;
		case Packet.eF2SPKTSyncGSCreateNewTeam:
			{
				Packet.F2S.SyncGSCreateNewTeam p = (Packet.F2S.SyncGSCreateNewTeam)packet;
				managerRPC.onTCPFightClientRecvSyncGSCreateNewTeam(this, p);
			}
			break;
		case Packet.eF2SPKTSyncRoleChatRoom:
			{
				Packet.F2S.SyncRoleChatRoom p = (Packet.F2S.SyncRoleChatRoom)packet;
				managerRPC.onTCPFightClientRecvSyncRoleChatRoom(this, p);
			}
			break;
		default:
			break;
		}
	}

	//todo
	private Dencoder dencoder = new Dencoder();
	private RPCManager managerRPC;
}
