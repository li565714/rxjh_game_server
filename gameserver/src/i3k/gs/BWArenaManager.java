package i3k.gs;import i3k.SBean;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;

public class BWArenaManager
{
	private static final int BWARENA_SAVE_INTERVAL 		= 900;
	private static final int RANK_MAX_COUNT = 300;
	public final static int MAX_RANK_PAGE_LENGTH = 20;
	
	final GameServer gs;
	int saveTime;
	
	BWArenaRank whiteRank = new BWArenaRank();
	BWArenaRankSnapshot whiteRankSnapshot = new BWArenaRankSnapshot();
//	List<SBean.BWArenaRankRole> whiteSnapShot  = new ArrayList<>();
	BWArenaRank blackRank = new BWArenaRank();
	BWArenaRankSnapshot blackRankSnapshot = new BWArenaRankSnapshot();
//	List<SBean.BWArenaRankRole> blackSnapShot  = new ArrayList<>();
	int rankRefreshStamp;
	int rankRewardStamp;
	int lastScoreClearWeek;
	Map<Integer, SBean.BWArenaRewardRole> whiteRankRewards = new TreeMap<>();	// key > 0: <sectID, BWArenaRewardRole>  key < 0: <roleID, BWArenaRewardRole>
	Map<Integer, SBean.BWArenaRewardRole> blackRankRewards = new TreeMap<>();
	
	ConcurrentMap<Integer, BWArenaLvlRoleCache> mapBWArenalvl2rsCaches = new ConcurrentHashMap<>();	//正邪道场等级cache
	
	Map<Integer, SBean.ArenaBattleInfo> battles = new HashMap<>();
	
	public static class BWArenaRankSnapshot
	{
		List<SBean.BWArenaRankRole> snapshot  = new ArrayList<>();
		Map<Integer, Integer> role2rank = new HashMap<>();
		
		public BWArenaRankSnapshot()
		{
			
		}
		
		void fromDB(List<SBean.BWArenaRankRole> dbSnapShot)
		{
			this.snapshot = dbSnapShot;
			
			this.role2rank.clear();
			for(int i=0; i<snapshot.size(); i++)
				this.role2rank.put(snapshot.get(i).roleSocial.role.id, i + 1);
		}
		
		void addRankRole(SBean.BWArenaRankRole rankRole, int rank)
		{
			this.snapshot.add(rankRole);
			this.role2rank.put(rankRole.roleSocial.role.id, rank);
		}
		
		List<SBean.BWArenaRankRole> getSnapshot()
		{
			return this.snapshot;
		}
		
		int getRoleRank(int roleID)
		{
			return this.role2rank.getOrDefault(roleID, 0);
		}
	}
	
	public static class BWArenaRank
	{
		TreeSet<Long> rankValues = new TreeSet<Long>();
		Map<Integer, SBean.BWArenaRankRole> rankRoles = new HashMap<>();
		int minValueKey;
//		
//		List<SBean.BWArenaRankRole> snapShot  = new ArrayList<>();
//		Map<Integer, Integer> role2rank = new HashMap<>();
		
		List<SBean.BWArenaRankRole> toDB()
		{
			return new ArrayList<>(rankRoles.values());
		}
		
		void fromDB(List<SBean.BWArenaRankRole> dbRankRoles)
		{
			int minKey = Integer.MAX_VALUE;
			for(SBean.BWArenaRankRole e: dbRankRoles)
			{
				rankRoles.put(e.roleSocial.role.id, e);
				int rankKey = getRankKey(e.lvl, e.score);
				rankValues.add(getRankRoleValue(rankKey, e.roleSocial.role.id));
				if(rankKey < minKey)
					minKey = rankKey;
			}

			this.minValueKey = minKey >= Integer.MAX_VALUE ? 0 : minKey;
		}
		
		void clearData()
		{
			this.rankValues.clear();
			this.rankRoles.clear();
			this.minValueKey = 0;
		}
		
		public static long getRankRoleValue(int lvl, int score, int rid)
		{
			return ((long)getRankKey(lvl, score) << 32) | ((long)rid & 0xffffffffL);
		}
		
		public static long getRankRoleValue(int rankKey, int rid)
		{
			return ((long)rankKey << 32) | ((long)rid & 0xffffffffL);
		}
		
		public static int getRankKey(int lvl, int score)
		{
			return ((lvl << 16) | (int)(score & 0xffffL));
		}
		
		public static int getRankKey(long value)
		{
			return (int)((value >> 32) & 0xffffffffL);
		}
		
		public static int getRoleID(long value)
		{
			return (int)(value & 0xffffffffL);
		}
		
		public void tryUpdateRank(SBean.BWArenaRankRole rankRole, int maxLength)
		{
			if(this.removeRole(rankRole.roleSocial.role.id) || getRankKey(rankRole.lvl, rankRole.score) > minValueKey || rankRoles.size() < maxLength)
			{
				this.addNewRankRole(rankRole);
				this.tryRemoveRankLast(maxLength);
				this.updateMinValue();
			}
		}
		
		private boolean removeRole(int rid)
		{
			SBean.BWArenaRankRole oldRankRole = rankRoles.remove(rid);
			if(oldRankRole != null)
			{
				long oldRankValue = getRankRoleValue(oldRankRole.lvl, oldRankRole.score, oldRankRole.roleSocial.role.id);
				rankValues.remove(oldRankValue);
				return true;
			}
			return false;
		}
		
		private void addNewRankRole(SBean.BWArenaRankRole rankRole)
		{
			if(rankRoles.putIfAbsent(rankRole.roleSocial.role.id, rankRole) == null)
			{
				long value = getRankRoleValue(rankRole.lvl, rankRole.score, rankRole.roleSocial.role.id);
				rankValues.add(value);
			}
		}
		
		private void tryRemoveRankLast(int maxLength)
		{
			if(rankValues.size() > maxLength)
			{
				Long lastValue = rankValues.pollFirst();
				if(lastValue != null)
				{
					int lastValueRid = getRoleID(lastValue);
					rankRoles.remove(lastValueRid);
				}
			}
		}
		
		private void updateMinValue()
		{
			if(!rankValues.isEmpty())
			{
				Long minValue = rankValues.first();
				if(minValue != null)
				{
					minValueKey = getRankKey(minValue);
				}
			}
		}
		
		BWArenaRankSnapshot createSnapshot()
		{
			BWArenaRankSnapshot snapshot = new BWArenaRankSnapshot();
			int rank = 1;
			for(Long rankValue: rankValues.descendingSet())
			{
				int rid = getRoleID(rankValue);
				SBean.BWArenaRankRole rankRole = rankRoles.get(rid);
				if(rankRole != null)
				{
					snapshot.addRankRole(rankRole, rank);
					rank++;
				}
			}
			
			return snapshot;
		}
	}
	
	public static class BWArenaLvlRoleCache
	{
		private static final int MAX_BWARENA_LVL_ROLE_CACHE_LENGTH	= 200;
		public int level;
		public List<Integer>	roles = new LinkedList<>();
		Map<Integer, Byte> mapr2bw = new HashMap<>();
		
		public BWArenaLvlRoleCache(int lvl)
		{
			this.level = lvl;
		}

		public synchronized SBean.DBBWArenaLvlCache toDB()
		{
			return new SBean.DBBWArenaLvlCache(mapr2bw);
		}
		
		public synchronized void updateRole(int rid, byte BWType)
		{
			roles.remove(new Integer(rid));
			if (roles.size() >= MAX_BWARENA_LVL_ROLE_CACHE_LENGTH)
			{
				Integer firstRid = roles.remove(0);
				mapr2bw.remove(firstRid);
			}

			roles.add(rid);
			mapr2bw.put(rid, BWType);
		}

		public synchronized void removeRole(int rid)
		{
			roles.remove(new Integer(rid));
			mapr2bw.remove(rid);
		}

		public synchronized Integer getRandomRole()
		{
			if (roles.isEmpty())
				return null;
			int index = GameRandom.getRandom().nextInt(roles.size());
			return roles.get(index);
		}
		
		public synchronized Set<Integer> getBWArenaRoles(int selfRid, byte selfBWType, int count, Set<Integer> enemies, Set<Integer> curEnemies)
		{
			int size = roles.size() > 20 ? 20 : roles.size();
			for(int i=0; i<size && enemies.size()<count; i++)
			{
				Integer rid = getRandomRole();
				Byte BWType = mapr2bw.get(rid);
				if(BWType != null && BWType > 0 && BWType != selfBWType && selfRid != rid && !curEnemies.contains(rid))
					enemies.add(rid);
			}
			
			return enemies;
		}
	}

	private BWArenaLvlRoleCache tryGetCreateBWArenaLvlRoleCache(int level)
	{
		synchronized (mapBWArenalvl2rsCaches)
		{
			BWArenaLvlRoleCache cache = this.mapBWArenalvl2rsCaches.get(level);
			if (cache == null)
			{
				cache = new BWArenaLvlRoleCache(level);
				this.mapBWArenalvl2rsCaches.put(level, cache);
			}
			return cache;
		}
	}
	
	void updateBWArenaLvlRoleCache(Role role)
	{
		if(role.level >= GameData.getInstance().getBWArenaCFGS().base.lvlReq - GameData.BWARENA_LVL_ADVANCE && role.BWType > 0)
		{
			BWArenaLvlRoleCache cache = tryGetCreateBWArenaLvlRoleCache(role.arenaInfo.roleArenaData.bwarena.lvl);
			cache.updateRole(role.id, role.BWType);
		}
	}
	
	void updateBWArenaLvlRoleCache(int level, int roleID, int oldLvl, int newLvl, byte BWType)
	{
		if(level >= GameData.getInstance().getBWArenaCFGS().base.lvlReq - GameData.BWARENA_LVL_ADVANCE && BWType > 0)
		{
			BWArenaLvlRoleCache oldLvlcache = this.mapBWArenalvl2rsCaches.get(oldLvl);
			if (oldLvlcache != null)
				oldLvlcache.removeRole(roleID);
			BWArenaLvlRoleCache newLvlcache = tryGetCreateBWArenaLvlRoleCache(newLvl);
			newLvlcache.updateRole(roleID, BWType);
		}
	}
	
	public Set<Integer> getBWArenaEnemies(int roleID, int level, byte BWType, int count, Set<Integer> curEnemies)
	{
		Set<Integer> enemies = new HashSet<>();
		for(int i=0; i<GameData.BWARENA_LVL_ADVANCE && (level - i) >= 0; i++)
		{
			BWArenaLvlRoleCache cache = this.mapBWArenalvl2rsCaches.get(level - i);
			if(cache == null)
				continue;
			
			cache.getBWArenaRoles(roleID, BWType, count, enemies, curEnemies);
			if(enemies.size() >= count)
				return enemies;
		}
		
		return enemies;
	}
	
	public class SaveBWArenaTrans implements Transaction
	{
		SaveBWArenaTrans(SBean.DBBWArena dbBWArena)
		{
			this.dbBWArena = dbBWArena;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("bwarena");
			byte[] data = Stream.encodeLE(dbBWArena);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if( errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("bw arena save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		SBean.DBBWArena dbBWArena;
	}
	
	public static class BWArenaReward
	{
		BWArenaReward(int roleID, String roleName, int rank, List<SBean.GameItem> att, int roleTitle)
		{
			this.roleID = roleID;
			this.roleName = roleName;
			this.rank = rank;
			this.att = att;
			this.roleTitle = roleTitle;
		}
		
		int roleID;
		String roleName;
		int rank;
		List<SBean.GameItem> att;
		int roleTitle;
	}
	
	BWArenaManager(GameServer gs)
	{
		this.gs = gs;
	}
	
	public void init(SBean.DBBWArena dbBWArena)
	{
		if(dbBWArena == null)
		{
			this.rankRewardStamp = GameTime.getTime();
			this.lastScoreClearWeek = GameData.getWeekByRefreshTimeOffset(this.rankRewardStamp);
			return;
		}
		
		this.rankRefreshStamp = dbBWArena.rankRefreshStamp;
		this.rankRewardStamp = dbBWArena.rankRewardStamp;
		this.lastScoreClearWeek =  GameData.getWeekByRefreshTimeOffset(this.rankRewardStamp);
		
		this.whiteRank.fromDB(dbBWArena.whiteRanks);
		this.whiteRankSnapshot.fromDB(dbBWArena.whiteSnapshot);
		this.whiteRankRewards = dbBWArena.whiteRankRewards;
		
		this.blackRank.fromDB(dbBWArena.blackRanks);
		this.blackRankSnapshot.fromDB(dbBWArena.blackSnapshot);
		this.blackRankRewards = dbBWArena.blackRankRewards;
		
		for(Map.Entry<Integer, SBean.DBBWArenaLvlCache> e: dbBWArena.lvlCaches.entrySet())
		{
			BWArenaLvlRoleCache cache = new BWArenaLvlRoleCache(e.getKey());
			this.mapBWArenalvl2rsCaches.put(e.getKey(), cache);
			
			for(Map.Entry<Integer, Byte> c: e.getValue().cache.entrySet())
				cache.updateRole(c.getKey(), c.getValue());
		}
	}
	
	public void save()
	{
		SBean.DBBWArena dbBWArena = new SBean.DBBWArena();
		synchronized(this)
		{
			dbBWArena.whiteRanks = this.whiteRank.toDB();
			dbBWArena.whiteSnapshot = new ArrayList<>(this.whiteRankSnapshot.getSnapshot());
			dbBWArena.blackRanks = this.blackRank.toDB();
			dbBWArena.blackSnapshot = new ArrayList<>(this.blackRankSnapshot.getSnapshot());
			dbBWArena.whiteRankRewards = new TreeMap<>(this.whiteRankRewards);
			dbBWArena.blackRankRewards = new TreeMap<>(this.blackRankRewards);
			dbBWArena.rankRefreshStamp = this.rankRefreshStamp;
			dbBWArena.rankRewardStamp = this.rankRewardStamp;
			dbBWArena.lvlCaches = new HashMap<>();
			for(BWArenaLvlRoleCache cache: this.mapBWArenalvl2rsCaches.values())
			{
				dbBWArena.lvlCaches.put(cache.level, cache.toDB());
			}
		}
		
		gs.getDB().execute(new SaveBWArenaTrans(dbBWArena));
		this.saveTime = GameTime.getTime();
	}
	
	public void onTimer(int timeTick)
	{
//		int newstamp = GameTime.getDayTime(GameData.getInstance().getBWArenaCFGS().base.mailRewardTime);
		int lastRefreshTime = GameData.getLastRefreshTime(timeTick, GameData.getInstance().getBWArenaCFGS().base.rankRefreshTime);
		boolean save = false;
		Map<Integer, BWArenaReward> whiteReward = new TreeMap<>();
		Map<Integer, BWArenaReward> blackReward = new TreeMap<>();
		synchronized (this)
		{
			if (lastRefreshTime > this.rankRefreshStamp)
			{
				this.doRefresh(timeTick);
				save = true;
			}
			
//			if(newstamp > this.rankRewardStamp && timeTick > newstamp)
			int lastRewardWeek = GameData.getWeekByRefreshTimeOffset(this.rankRewardStamp);
			int week = GameData.getWeekByRefreshTimeOffset(timeTick);
			if(week > lastRewardWeek)
			{
				this.rankRewardStamp = timeTick;
				
				this.setWhiteReward(whiteReward);
				this.setBlackReward(blackReward);
				this.clearRankScore();
				this.lastScoreClearWeek = week;
				save = true;
			}
			else
			{
				if(!this.whiteRankRewards.isEmpty())
					this.setWhiteReward(whiteReward);
				
				if(!this.blackRankRewards.isEmpty())
					this.setBlackReward(blackReward);
			}
		}
		
		if(!whiteReward.isEmpty())
			this.sendReward(whiteReward, 1);
		
		if(!blackReward.isEmpty())
			this.sendReward(blackReward, 2);
		
		if(save)
			this.save();
		
		if(this.saveTime + BWARENA_SAVE_INTERVAL < timeTick)
			this.save();
	}
	
	private void sendReward(Map<Integer, BWArenaReward> rewardMails, int bwType)
	{
		for(Map.Entry<Integer, BWArenaReward> e: rewardMails.entrySet())
		{
			int key = e.getKey();
			BWArenaReward reward = e.getValue();
			if(reward.att.isEmpty() && reward.roleTitle == 0)
				continue;
			
			Set<Integer> rids = new TreeSet<>();
			Sect sect = null;
			if(key > 0)				//setcID
			{
				Collection<Integer> members = gs.getSectManager().getAllMembers(key);
				if(members != null)
				{
					rids.addAll(members);
					rids.remove(reward.roleID);
				}
			}
			List<Integer> addinfo = new ArrayList<>();
			addinfo.add(reward.roleID);
			addinfo.add(reward.rank);
			addinfo.add(bwType);
			final String sectName = sect == null ? "" : sect.getName();
			
			if(!reward.att.isEmpty() && reward.roleTitle != 0)
				this.sendRewardImpl(reward.roleID, reward, sectName, addinfo);
			
			if(!reward.att.isEmpty())
			{
				for(int rid: rids)
					this.sendRewardImpl(rid, reward, sectName, addinfo);
			}
		}
	}
	
	private void sendRewardImpl(int rid, BWArenaReward reward, String sectName, List<Integer> addinfo)
	{
		gs.getLoginManager().exeCommonRoleVisitor(rid, false, new LoginManager.CommonRoleVisitor()
		{
			boolean send = false;
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				if(role.level < GameData.getInstance().getBWArenaCFGS().base.mailRewardLvlReq)
				{
					send = false;
					return false;
				}
				
				if(!reward.att.isEmpty())
				{
					MailBox mailbox = role.getMailBox();
					
					StringBuilder content = new StringBuilder();
					content.append(sectName);
					content.append("|");
					content.append(reward.roleName);

					mailbox.addSysMail(MailBox.SysMailType.BWArenaMap, MailBox.BWARENAMAP_MAIL_MAX_RESERVE_TIME, content.toString(), reward.att, addinfo);
				}
				
				if(reward.roleID == rid && reward.roleTitle > 0)
					role.addRoleTitle(reward.roleTitle);
				
				send = true;
				return true;
			}
			
			@Override
			public void onCallback(boolean success)
			{
				if(send)
					gs.getLogger().debug("mail role " + rid + " bw arena rank reward success !");
			}
		});
	}
	
	private void setWhiteReward(Map<Integer, BWArenaReward> rewardMails)
	{
		if(rewardMails == null)
			return;
		
		if(rewardMails.isEmpty())
		{
			for(int rank=0; rank<this.whiteRankSnapshot.getSnapshot().size(); rank++)
			{
				SBean.BWArenaRankRole rankRole = this.whiteRankSnapshot.getSnapshot().get(rank);
				int rid = rankRole.roleSocial.role.id;
				int sectID = rankRole.roleSocial.sectId;
				if(sectID > 0)
					this.whiteRankRewards.putIfAbsent(sectID, new SBean.BWArenaRewardRole(rid, rankRole.roleSocial.role.name, rank + 1));
				else
					this.whiteRankRewards.put(-rid, new SBean.BWArenaRewardRole(rid, rankRole.roleSocial.role.name, rank + 1));
			}
		}
		
		int i=0;
		Iterator<Map.Entry<Integer, SBean.BWArenaRewardRole>> it = this.whiteRankRewards.entrySet().iterator();
		while(it.hasNext() && i++<1000)
		{
			Map.Entry<Integer, SBean.BWArenaRewardRole> e = it.next();
			SBean.BWArenaRewardRole r = e.getValue();
			it.remove();
			
			SBean.BWArenaRankCFGS cfg = GameData.getInstance().getBWArenaRankCFGS(r.rank);
			if(cfg == null)
				continue;
			
			List<SBean.GameItem> att = GameData.getInstance().toGameItems(cfg.whiteRankReward);
			rewardMails.put(e.getKey(), new BWArenaReward(r.id, r.name, r.rank, att, cfg.roleWhiteTitle));
		}
	}
	
	private void setBlackReward(Map<Integer, BWArenaReward> rewardMails)
	{
		if(rewardMails == null)
			return;
		
		if(rewardMails.isEmpty())
		{
			for(int rank=0; rank<this.blackRankSnapshot.getSnapshot().size(); rank++)
			{
				SBean.BWArenaRankRole rankRole = this.blackRankSnapshot.getSnapshot().get(rank);
				int rid = rankRole.roleSocial.role.id;
				int sectID = rankRole.roleSocial.sectId;
				if(sectID > 0)
					this.blackRankRewards.putIfAbsent(sectID, new SBean.BWArenaRewardRole(rid, rankRole.roleSocial.role.name, rank + 1));
				else
					this.blackRankRewards.put(-rid, new SBean.BWArenaRewardRole(rid, rankRole.roleSocial.role.name, rank + 1));
			}
		}
		
		int i=0;
		Iterator<Map.Entry<Integer, SBean.BWArenaRewardRole>> it = this.blackRankRewards.entrySet().iterator();
		while(it.hasNext() && i++<1000)
		{
			Map.Entry<Integer, SBean.BWArenaRewardRole> e = it.next();
			SBean.BWArenaRewardRole r = e.getValue();
			it.remove();
			
			SBean.BWArenaRankCFGS cfg = GameData.getInstance().getBWArenaRankCFGS(r.rank);
			if(cfg == null)
				continue;

			List<SBean.GameItem> att = GameData.getInstance().toGameItems(cfg.blackRankReward);
			rewardMails.put(e.getKey(), new BWArenaReward(r.id, r.name, r.rank, att, cfg.roleBlackTitle));
		}
	}
	
	private void clearRankScore()
	{
		this.whiteRank.clearData();
		this.blackRank.clearData();
	}
	
	private void doRefresh(int timeTick)
	{
		this.whiteRankSnapshot = this.whiteRank.createSnapshot();
		this.blackRankSnapshot = this.blackRank.createSnapshot();
		
		gs.getLogger().debug("bwareba white rank list (size = " + whiteRank.rankRoles.size() + ", minValue= " + whiteRank.minValueKey + ") refresh !");
		gs.getLogger().debug("bwareba black rank list (size = " + blackRank.rankRoles.size() + ", minValue= " + blackRank.minValueKey + ") refresh !");
		this.rankRefreshStamp = timeTick;
	}
	
	public synchronized void syncRoleBWArenaInfo(Role role)
	{
		synchronized(role)
		{
			if(role.level < GameData.getInstance().getBWArenaCFGS().base.lvlReq || role.BWType <= 0)
			{
				gs.getRPCManager().sendStrPacket(role.netsid, new SBean.bwarena_sync_res(null));
				return;
			}
			
			if(role.arenaInfo.roleArenaData.bwarena.curEnemies.isEmpty())
				role.refreshBwArenaEnemiesImpl();
		}
		
		int rank = this.getSnapshotRank(role.BWType == 1, role.id);
		gs.getLoginManager().getBWArenaBattleArrayOverviews(role.arenaInfo.roleArenaData.bwarena.curEnemies.keySet(), (overviews, bwarenalvls) ->{
			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.bwarena_sync_res(role.getBWArenaInfo(rank, overviews, bwarenalvls)));
		});
		
	}
	
	public synchronized void startBWArenaBattle(Role attacker, int targetID)
	{
		synchronized(attacker)
		{
			int canEnter = attacker.checkCanEnterBWArenaMapCopy(targetID);
			if(canEnter <= 0)
			{
				gs.getRPCManager().sendStrPacket(attacker.netsid, new SBean.bwarena_startattack_res(canEnter));
				return;
			}
		}
		
		gs.getLoginManager().getRoleBWArenaBattleArray(targetID, new LoginManager.GetRoleBWArenaBattleArrayCallback()
		{
			@Override
			public void onCallback(SBean.BattleArray ba, int bwarenaLvl)
			{
				if(ba == null)
				{
					gs.getRPCManager().sendStrPacket(attacker.netsid, new SBean.bwarena_startattack_res(GameData.PROTOCOL_OP_FAILED));
					return;
				}
				
				Set<Integer> fightPets = GameData.emptySet();
				synchronized(attacker)
				{
					SBean.BWArenaLvlCFGS lvlCfg = GameData.getInstance().getBWArenaLvlCFGS(attacker.arenaInfo.roleArenaData.bwarena.lvl);
					int maxCount = lvlCfg == null ? GameData.BWARENA_PET_USE_COUNT : lvlCfg.petCount;
					boolean selfPetLack = attacker.arenaInfo.roleArenaData.bwarena.pets.size() < maxCount;
					fightPets = GameData.getBWArenaFightPets(new ArrayList<>(attacker.arenaInfo.roleArenaData.bwarena.pets), maxCount);
					if(!attacker.startBWArenaMapCopy(ba, bwarenaLvl, fightPets, selfPetLack))
					{
						gs.getRPCManager().sendStrPacket(attacker.netsid, new SBean.bwarena_startattack_res(GameData.PROTOCOL_OP_FAILED));
						return;
					}
				}
				BWArenaManager.this.setBattleInfo(attacker.id, new SBean.ArenaBattleInfo(0, attacker.getBattaleArrayOverview(fightPets), GameData.getBattleArrayOverviewFromBattleArray(ba)));
				gs.getRPCManager().sendStrPacket(attacker.netsid, new SBean.bwarena_startattack_res(GameData.PROTOCOL_OP_SUCCESS));
			}
		});
	}
	
	public synchronized int onBWArenaBattleEnd(Role attacker, boolean win, boolean sameBWArenaLvl, SBean.BattleArrayHp attackingSideHp, SBean.BattleArrayHp defendingSideHp)
	{
		SBean.ArenaBattleInfo info = this.battles.remove(attackingSideHp.roleId);
		if (info == null || info.defendingSide.roleSocial.role.id != defendingSideHp.roleId)
			return 0;

		SBean.BWArenaBaseCFGS baseCfg =  GameData.getInstance().getBWArenaCFGS().base;
		int addExp = 0;
		int addScore = 0;
//		int winScore = sameBWArenaLvl ? baseCfg.winScore.get(1) : baseCfg.winScore.get(0);
		if (win)
		{
			addScore = sameBWArenaLvl ? baseCfg.winScore.get(1) : baseCfg.winScore.get(0);
			addExp = sameBWArenaLvl ? baseCfg.winExp.get(1) : baseCfg.winExp.get(0);
		}
		else
		{
			addScore = sameBWArenaLvl ? baseCfg.loseScore.get(1) : baseCfg.loseScore.get(0);
			addExp = sameBWArenaLvl ? baseCfg.loseExp.get(1) : baseCfg.loseExp.get(0);
		}
		
		if(addExp > 0)
		{
			int oldLvl = attacker.arenaInfo.roleArenaData.bwarena.lvl;
			if(attacker.addBWArenaExp(addExp))
				this.updateBWArenaLvlRoleCache(attacker.level, attacker.id, oldLvl, attacker.arenaInfo.roleArenaData.bwarena.lvl, attacker.BWType);
		}
		
		this.updateRank(attacker, addScore);
		SBean.BattleArrayProfile attackingSideFinalInfo = GameData.getBattleArrayProfileFromBattleArrayOverviewHP(info.attackingSide, attackingSideHp);
		SBean.BattleArrayProfile defendingSideFinalInfo = GameData.getBattleArrayProfileFromBattleArrayOverviewHP(info.defendingSide, defendingSideHp);
		SBean.DBRoleArenaLog log = new SBean.DBRoleArenaLog(GameTime.getTime(), (byte)(win ? 1 : 0), 0, 0, attackingSideFinalInfo, defendingSideFinalInfo);
		attacker.bwarenaAddLog(log);
		return addScore;
	}
	
	public synchronized List<SBean.BWArenaRankRole> getWhiteRanksSnapshot(int index, int len)
	{
		if (index < 0 || len <= 0 || len > MAX_RANK_PAGE_LENGTH)
			return null;
		
		if(index >= this.whiteRankSnapshot.getSnapshot().size())
			return GameData.emptyList();
		
		int toIndex = index + len;
		if(toIndex > this.whiteRankSnapshot.getSnapshot().size())
			toIndex = this.whiteRankSnapshot.getSnapshot().size();
		return this.whiteRankSnapshot.getSnapshot().subList(index, toIndex);
	}
	
	public synchronized List<SBean.BWArenaRankRole> getBlackRanksSnapshot(int index, int len)
	{
		if (index < 0 || len <= 0 || len > MAX_RANK_PAGE_LENGTH)
			return null;
		
		if(index >= this.blackRankSnapshot.getSnapshot().size())
			return GameData.emptyList();
		
		int toIndex = index + len;
		if(toIndex > this.blackRankSnapshot.getSnapshot().size())
			toIndex = this.blackRankSnapshot.getSnapshot().size();
		return this.blackRankSnapshot.getSnapshot().subList(index, toIndex);
	}
	
	public int getSnapshotRank(boolean white, int rid)
	{
		if(white)
			return this.whiteRankSnapshot.getRoleRank(rid);
		else
			return this.blackRankSnapshot.getRoleRank(rid);
	}
	
	public synchronized void updateRank(Role role, int addScore)
	{
		int score = role.onBWArenaEndImpl(addScore, this.lastScoreClearWeek);
		if(score > 0)
			this.updateRank(role.isWhite(), role.getBWArenaRankRole());
	}
	
	public void updateRank(boolean white, SBean.BWArenaRankRole rankRole)
	{
		if(white)
			this.whiteRank.tryUpdateRank(rankRole, RANK_MAX_COUNT);
		else
			this.blackRank.tryUpdateRank(rankRole, RANK_MAX_COUNT);
	}
	
	public synchronized void setBattleInfo(int rid, SBean.ArenaBattleInfo info)
	{
		this.battles.put(rid, info);
	}
	
	public synchronized void roleLeaveBWArenaMap(int rid)
	{
		this.battles.remove(rid);
	}
}
