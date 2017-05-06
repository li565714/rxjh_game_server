package i3k.gs;

import i3k.SBean;
import i3k.SBean.BattleArray;
import i3k.SBean.BattleArrayOverview;
import i3k.util.GameTime;
import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;

import java.util.*;

public class ArenaManager 
{
	private static final int ARENA_SAVE_INTERVAL 		= 900;
	private static final int ARENA_RANK_SEE_COUNT	 	= 50;
	
	public class SaveTrans implements Transaction
	{
		public SaveTrans(SBean.DBArena arena)
		{
			this.arena = arena;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("arena");
			byte[] data = Stream.encodeLE(arena);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if( errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("arena save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private final SBean.DBArena arena;
	}
	
	public static class Reward
	{
		public Reward(int roleId, int rank, Map<Integer, Integer> att)
		{
			this.roleId = roleId;
			this.rank = rank;
			this.att = att;
		}
		int roleId;
		int rank;
		Map<Integer, Integer> att;
	}
	
	public static class Arena
	{	
		public Arena()
		{
			role2rank = new HashMap<>();
			rank2role = new HashMap<>();
		}
		
		public Arena(Map<Integer,Integer> ranks)
		{
			role2rank = new HashMap<>();
			rank2role = new HashMap<>();
			for(Map.Entry<Integer, Integer> e: ranks.entrySet())
			{
				rank2role.put(e.getKey(), e.getValue());
				role2rank.put(e.getValue(), e.getKey());
			}
		}
		
		public Arena copy()
		{
			Arena n = new Arena();
			n.role2rank.putAll(this.role2rank);
			n.rank2role.putAll(this.rank2role);
			return n;
		}
		
		public Map<Integer, Integer> getDBData()
		{
			return new HashMap<>(this.rank2role);
		}
		
		Map<Integer, Integer> rank2role;
		Map<Integer, Integer> role2rank;
	}
	
	
	public ArenaManager(GameServer gs)
	{
		this.gs = gs;
	}
	
	public void init(SBean.DBArena arena)
	{
		if(arena == null)
		{
			current = new Arena();
			arenaReward = new SBean.DBArenaReward(GameTime.getDayTime(GameData.getInstance().getArenaCFGS().refreshTime) - GameTime.getDayTimeSpan(), new HashMap<>());
			return;
		}
		
		current = new Arena(arena.normal);
		arenaReward = arena.reward;
	}
	
	public void save()
	{
		SBean.DBArena arena = new SBean.DBArena();
		synchronized (this) 
		{
			arena.normal = this.current.getDBData();
			arena.reward = this.getDBRewardData();
		}
		gs.getDB().execute(new SaveTrans(arena));
		this.saveTime = GameTime.getTime();
	}
	
	public void onTimer(int timeTick)
	{
		boolean bsave = false;
		int newstamp = GameTime.getDayTime(GameData.getInstance().getArenaCFGS().refreshTime);
		List<Reward> rewards = new ArrayList<>();
		synchronized (this)
		{
			if(newstamp > this.arenaReward.rewardTime && timeTick > newstamp)
			{
				this.arenaReward.rewardTime = newstamp;
				this.sendRewards(rewards);
				bsave = true;
			}
			else if(!this.arenaReward.rewardRoles.isEmpty())
				this.sendRewards(rewards);

		}
		
		for (Reward r: rewards)
		{
			List<SBean.GameItem> att = GameData.getInstance().toGameItems(r.att);
			if(att.isEmpty())
				continue;
			
			List<Integer> addinfo = new ArrayList<>();
			addinfo.add(r.rank);
	
			gs.getLoginManager().exeCommonRoleVisitor(r.roleId, false, new LoginManager.CommonRoleVisitor()
			{
				@Override
				public boolean visit(Role role, Role sameUserRole)
				{
					role.onArenaReward(r.rank);
					MailBox mailbox = role.getMailBox();
					
					mailbox.addSysMail(MailBox.SysMailType.ArenaMap, MailBox.ARENAMAP_MAIL_MAX_RESERVE_TIME, "", att, addinfo);
					return true;
				}
				
				@Override
				public void onCallback(boolean success)
				{
					gs.getLogger().debug("mail role " + r.roleId + " arena rank " + r.rank + " reward " + ( success ? " success !" : " failed !"));
				}
			});
		}
		
		if(bsave)
			this.save();
		
		if (this.saveTime + ARENA_SAVE_INTERVAL < timeTick)
			this.save();
		
	}
	
	public SBean.DBArenaReward getDBRewardData()
	{
		return new SBean.DBArenaReward(this.arenaReward.rewardTime, new HashMap<>(this.arenaReward.rewardRoles));
	}
	
	public void sendRewards(List<Reward> rewardMails)
	{
		if(rewardMails == null)
			return;
		
		int rid = 0;
		int rank = 0;
		if(this.arenaReward.rewardRoles.isEmpty())
		{
			for (Map.Entry<Integer, Integer> e : current.role2rank.entrySet())
			{
				rid = e.getKey();
				if (rid > 0)
					this.arenaReward.rewardRoles.put(rid, e.getValue());
			}				
		}
		
		List<SBean.ArenaRewardCFGS> cfgs = GameData.getInstance().getArenaCFGS().rankRewards;
		int i = 0;
		Iterator<Map.Entry<Integer, Integer>> it = this.arenaReward.rewardRoles.entrySet().iterator();
		while(it.hasNext() && i++ < 1000)
		{
			Map.Entry<Integer, Integer> entry = it.next();
			rid = entry.getKey();
			rank = entry.getValue();
			it.remove();
			
			if(rid < 0)
				continue;
			
			SBean.ArenaRewardCFGS reward = null;
			for(SBean.ArenaRewardCFGS e: cfgs)
			{
				if( rank <= e.floor)
				{
					reward = e;
					break;
				}
			}
		
			if(reward == null)
				return;
		
			Map<Integer, Integer> att = new TreeMap<>();
			if(reward.money > 0)
				att.merge(GameData.COMMON_ITEM_ID_COIN, reward.money, (ov, nv)->ov+nv);
			
			if(reward.stone > 0)
				att.merge(GameData.COMMON_ITEM_ID_DIAMOND, reward.stone, (ov, nv)->ov+nv);
			
			if(reward.point > 0)
				att.merge(GameData.COMMON_ITEM_ID_ARENA_MONEY, reward.point, (ov, nv)->ov+nv);
			
			GameData.mergeCounter(att, reward.items);
			
			rewardMails.add(new Reward(rid, rank, att));	
		}
	}
	
	public int getRoleRank(int rid)
	{
		Integer rank = current.role2rank.get(rid);
		return rank == null ? GameData.getInstance().getArenaCFGS().rankMax : rank;
	}
	
	public int getRankRole(int rank)
	{
		Integer rid = current.rank2role.get(rank);
		return rid == null ? -rank : rid;
	}
	
	public synchronized void roleSyncRanks(Role role)
	{

		Map<Integer, Integer> rank2roles = new TreeMap<>();
		for (int rank = 1; rank <= ARENA_RANK_SEE_COUNT; ++rank)
		{
			int rid = this.getRankRole(rank);
			if(rid < 0)
				rid = GameData.getInstance().getArenaRobotID(rank);
			rank2roles.put(rank, rid);
		}
		
		this.getArenaRankRoleBattleArrayOverviews(rank2roles, oveviews -> {
            List<SBean.RoleSocial> ranks = new ArrayList<>();
            for (SBean.BattleEnemyOverview e : oveviews.values())
            {
                ranks.add(e.roleSocial);
                int fightPower = e.roleSocial.role.fightPower;
                for(SBean.PetOverview p: e.pets)
                {
                    fightPower += p.fightPower;
                }
                e.roleSocial.role.fightPower = fightPower;
            }
            gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_ranks_res(ranks));
        });
	}
	
	public synchronized void getRoleArenaInfo(Role role)
	{
		synchronized (role)
		{
			if (role.level < GameData.getInstance().getArenaCFGS().lvlReq)
			{
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_sync_res(null));
				return;
			}
			int rankNow = this.getRoleRank(role.id);
			this.searchArenaEnemies(rankNow, oveviews -> {
                role.changeArenaEnemies(oveviews);
                gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_sync_res(role.getArenaInfo(rankNow, oveviews)));
            });
		}
	}
	
	private void searchArenaEnemies(int searchRoleRank, GetArenaRankRoleBattleArrayOverviewsCallback callback)
	{
		List<Integer> enemiesRank = GameData.getInstance().searchArenaEnemies(searchRoleRank);
		Map<Integer, Integer> rank2roles = new TreeMap<>();
		for (int rank : enemiesRank)
		{
			int rid = this.getRankRole(rank);
			if (rid < 0)
				rid = GameData.getInstance().getArenaRobotID(rank);
			rank2roles.put(rank, rid);
		}
		this.getArenaRankRoleBattleArrayOverviews(rank2roles, callback);
	}
	
	interface GetArenaRankRoleBattleArrayOverviewsCallback
	{
		void onCallback(Map<Integer, SBean.BattleEnemyOverview> oveviews);
	}
	private void getArenaRankRoleBattleArrayOverviews(Map<Integer, Integer> rank2roles, GetArenaRankRoleBattleArrayOverviewsCallback callback)
	{
		Map<Integer, SBean.BattleEnemyOverview> oveviews = new TreeMap<>();
		Iterator<Map.Entry<Integer, Integer>> it = rank2roles.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Integer> e = it.next();
			int rank = e.getKey();
			int rid = e.getValue();
			if (rid < 0)
			{
				oveviews.put(rank, GameData.getInstance().getArenaRobotBattleArrayOverview(rank, rid));
				it.remove();
			}
		}
		if (rank2roles.isEmpty())
		{
			callback.onCallback(oveviews);
			return;
		}
		gs.getLoginManager().getArenaBattleArrayOverviews(rank2roles.values(), baOverviews -> {
            for (Map.Entry<Integer, Integer> e : rank2roles.entrySet())
            {
                SBean.BattleEnemyOverview ov = baOverviews.get(e.getValue());
                if (ov != null)
                {
                    oveviews.put(e.getKey(), ov);
                }
            }
            callback.onCallback(oveviews);
        });
	}
	
	public synchronized void roleRefreshArenaEnemies(Role role)
	{
		int rankNow = this.getRoleRank(role.id);
		this.searchArenaEnemies(rankNow, oveviews -> {
            role.changeArenaEnemies(oveviews);
            gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_refresh_res(oveviews));
        });
	}

	
	public synchronized void roleStartArenaBattle(Role role, int roleRank, List<Integer> fightPets, int targetRid, int targetRank)
	{
		synchronized (role)
		{
			if (!role.checkCanStartArenaMapCopy(fightPets, targetRid, targetRank))
			{
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_startattack_res(GameData.PROTOCOL_OP_FAILED));
				return;
			}
			
			int selfRankNow = this.getRoleRank(role.id);
			if (selfRankNow != roleRank)
			{
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_startattack_res(GameData.PROTOCOL_OP_ARENA_RANKCHANGE));
				return;
			}
			int nowRankRid = this.getRankRole(targetRank);
			if (nowRankRid > 0 && nowRankRid != targetRid)//不是机器人，则保证此rank上role必须和客户端看到的role相同
			{
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_startattack_res(GameData.PROTOCOL_OP_ARENA_RANKCHANGE));
				return;
			}

			int attacking = targetRid < 0 ? -targetRank : targetRid;
			if (!this.inBattle.add(attacking))
			{
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_startattack_res(GameData.PROTOCOL_OP_ARENA_TARGET_IN_BATTLE));
				return;
			}
		}			
		gs.getLoginManager().getRoleArenaDefenceBattleArray(targetRid, targetRank, new LoginManager.GetRoleArenaDefenceBattleArrayCallback()
			{
				
				@Override
				public void onCallback(BattleArray ba)
				{
					synchronized (this)
					{
						synchronized (role)
						{
							int attacking = targetRid < 0 ? -targetRank : targetRid;
							if (ba == null)
							{
								ArenaManager.this.inBattle.remove(attacking);
								gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_startattack_res(GameData.PROTOCOL_OP_FAILED));
								return;
							}
							boolean ok = role.startArenaMapCopy(fightPets, ba);
							if (ok)
								ArenaManager.this.battles.put(role.id, new SBean.ArenaBattleInfo(targetRank, role.getBattaleArrayOverview(new HashSet<>(fightPets)), GameData.getBattleArrayOverviewFromBattleArray(ba)));
							else
								ArenaManager.this.inBattle.remove(attacking);
							gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_startattack_res(ok ? GameData.PROTOCOL_OP_SUCCESS : GameData.PROTOCOL_OP_FAILED));
						}
					}
				}
			});
	}
	
	public synchronized SBean.ArenaBattleResult onArenaBattleEnd(boolean win, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
	{
		SBean.ArenaBattleInfo info = this.battles.remove(attackingSideHp.roleId);
		if (info == null || info.defendingSide.roleSocial.role.id != defendingSideHp.roleId)
			return null;
		int attacking = defendingSideHp.roleId < 0 ? -info.defendingSideRank : defendingSideHp.roleId;
		this.inBattle.remove(attacking);
		int attackingSideRankNow = this.getRoleRank(attackingSideHp.roleId);
		int defendingSideRankNow = this.getRoleRank(defendingSideHp.roleId);
		if (defendingSideHp.roleId < 0)
			defendingSideRankNow = info.defendingSideRank;
		
		int attackingSideRankFinal = attackingSideRankNow;
		int defendingSideRankFinal = defendingSideRankNow;
		if (win)
		{
			if (attackingSideRankNow > defendingSideRankNow)
			{
				attackingSideRankFinal = defendingSideRankNow;
				defendingSideRankFinal = attackingSideRankNow;
				
				this.current.rank2role.put(attackingSideRankFinal, attackingSideHp.roleId);
				this.current.role2rank.put(attackingSideHp.roleId, attackingSideRankFinal);
				if (defendingSideHp.roleId > 0 && defendingSideRankFinal < GameData.getInstance().getArenaCFGS().rankMax)
				{
					this.current.rank2role.put(defendingSideRankFinal, defendingSideHp.roleId);
					this.current.role2rank.put(defendingSideHp.roleId, defendingSideRankFinal);
				}
				else
				{
					this.current.rank2role.remove(defendingSideRankFinal);
					this.current.role2rank.remove(defendingSideHp.roleId);
				}
			}
		}
		
		SBean.ArenaBattleResult result = new SBean.ArenaBattleResult(win ? 1 : 0, attackingSideRankFinal, defendingSideRankFinal, info.defendingSide.roleSocial.role.kdClone());
		int defendRoleFightPower = result.defendingSide.fightPower;
		for(SBean.PetOverview p: info.defendingSide.pets.values())
		{
			defendRoleFightPower += p.fightPower;
		}
		result.defendingSide.fightPower = defendRoleFightPower;
		
		SBean.BattleArrayProfile attackingSideFinalInfo = GameData.getBattleArrayProfileFromBattleArrayOverviewHP(info.attackingSide, attackingSideHp);
		SBean.BattleArrayProfile defendingSideFinalInfo = GameData.getBattleArrayProfileFromBattleArrayOverviewHP(info.defendingSide, defendingSideHp);
		SBean.DBRoleArenaLog log = new SBean.DBRoleArenaLog(GameTime.getTime(), (byte)(win ? 1 : 0), attackingSideRankFinal, defendingSideRankFinal, attackingSideFinalInfo, defendingSideFinalInfo);
		gs.getLoginManager().exeCommonRoleVisitor(attackingSideHp.roleId, false, new LoginManager.CommonRoleVisitor()
		{
			
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				role.arenaAddLog(log);
				return true;
			}
			
			@Override
			public void onCallback(boolean success)
			{
				
			}
		});
//		final int attackedFinalRank = defendingSideRankFinal;
		gs.getLoginManager().exeCommonRoleVisitor(defendingSideHp.roleId, false, new LoginManager.CommonRoleVisitor()
		{
			
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				role.arenaAddLog(log);
				if(win)
				{
					ArenaManager.this.attacked.add(role.id);
					gs.getRPCManager().sendStrPacket(role.netsid, new SBean.arena_attacked());
				}
				return true;
			}
			
			@Override
			public void onCallback(boolean success)
			{
				
			}
		});
		
		return result;
	}
	
	public synchronized void roleLeaveArenaMap(Role role)
	{
		SBean.ArenaBattleInfo info = this.battles.remove(role.id);
		if (info != null)
		{
			int attacking = info.defendingSide.roleSocial.role.id < 0 ? -info.defendingSideRank : info.defendingSide.roleSocial.role.id;
			this.inBattle.remove(attacking);	
		}
	}
	
	public synchronized void clearAttacked(Role role)
	{
		attacked.remove(role.id);
		role.notifyRoleArenaLogs();
	}
	
	public synchronized void roleAttacked(Role role)
	{
		if(attacked.contains(role.id))
			role.notifyArenaAttacked();
	}
	
	int saveTime;
	GameServer gs;
	Arena current;
	SBean.DBArenaReward arenaReward;
	Set<Integer> inBattle = new HashSet<>();
	Set<Integer> attacked = new HashSet<>();
	Map<Integer, SBean.ArenaBattleInfo> battles = new HashMap<>();
}
