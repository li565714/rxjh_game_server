package i3k.gs;

import i3k.DBMailBox;
import i3k.DBRole;
import i3k.DBRoleShare;
import i3k.DBSect;
import i3k.DBUser;
import i3k.DBMarriageShare;
import i3k.IDIP;
import i3k.SBean;
import i3k.SBean.RoleOverview;
import i3k.gs.MailBox.SysMailType;
import i3k.gs.Role.Flower;
import i3k.gtool.ActiveKeyGen;
import i3k.gtool.CDKeyGen;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.management.ObjectName;

import ket.kdb.Table;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.kdb.Transaction.AutoInit;
import ket.kdb.Transaction.ErrorCode;
import ket.util.Stream;


public class LoginManager
{

	public interface GSStatMBean
	{
		int getOnlineCount();
		int getSessionCount();
	}
	
	public class GSStat implements GSStatMBean
	{
		public GSStat()
		{
			
		}
		
		public void start()
		{
			try
			{
				ManagementFactory.getPlatformMBeanServer().registerMBean(gsStat, new ObjectName("i3k.gs:type=GSStat"));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		@Override
		public int getOnlineCount()
		{
			return mapRoles.size();
		}
		
		@Override
		public int getSessionCount()
		{
			return maps2r.size();
		}				
	}
	
	public class LoginWhiteList
	{
		boolean whileListOn = false;
		Set<String> UIDWhiteList = new HashSet<>();
		LoginWhiteList ()
		{
			
		}
		
		synchronized void setWhileList(boolean whileliston, Set<String> lst)
		{
			whileListOn = whileliston;
			UIDWhiteList = lst;
		}
		
		synchronized boolean canLogin(String UID)
		{
			return !(UID == null || UID.isEmpty()) && (!whileListOn || UIDWhiteList.contains(UID.toLowerCase()));
		}
		
		synchronized boolean isPrivilegedAccount(String UID)
		{
			return UIDWhiteList.contains(UID.toLowerCase());
		}
		
		void setCfgFile(final String fileName)
		{
			gs.getResourceManager().addWatch(fileName, this::reloadFile);
		}
		
		void reloadFile(String filePath)
		{
			try 
			{
				boolean whitelistOn = false;
				Set<String> whiteList = new HashSet<>();
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
				for (String line = in.readLine(); line != null; line = in.readLine()) 
				{
					//System.out.println(line);
					String linetrim = line.trim();
					if (!linetrim.startsWith("#"))
					{
						if (linetrim.startsWith("WhiteList"))
						{
							String[] strs = linetrim.split("\\s+", 2);
							if (strs.length == 2 && strs[0].equals("WhiteList") && strs[1].toLowerCase().equals("on"))
							{
								whitelistOn = true;
							}
						}
						else if (!linetrim.isEmpty())
						{
							whiteList.add(linetrim.toLowerCase());
						}
					}
				}
				in.close();
				gs.getLogger().info(this.getClass().getSimpleName() + " : reloadFile whilelist " + (whitelistOn?"on":"off"));
				setWhileList(whitelistOn, whiteList);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public class ChannelBlackList
	{
		boolean blackListOn = false;
		Set<String> channelBlackList = new HashSet<>();
		ChannelBlackList()
		{
			
		}
		
		synchronized void setBlackList(boolean blackliston, Set<String> lst)
		{
			blackListOn = blackliston;
			channelBlackList = lst;
		}
		
		synchronized boolean isRestricted(String channel)
		{
			return (channel != null && !channel.isEmpty()) && (blackListOn && channelBlackList.contains(channel.toLowerCase()));
		}
		
		void setCfgFile(final String fileName)
		{
			gs.getResourceManager().addWatch(fileName, this::reloadFile);
		}
		
		void reloadFile(String filePath)
		{
			try 
			{
				boolean blacklistOn = false;
				Set<String> blackList = new HashSet<>();
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
				for (String line = in.readLine(); line != null; line = in.readLine()) 
				{
					//System.out.println(line);
					String linetrim = line.trim();
					if (!linetrim.startsWith("#"))
					{
						if (linetrim.startsWith("BlackList"))
						{
							String[] strs = linetrim.split("\\s+", 2);
							if (strs.length == 2 && strs[0].equals("BlackList") && strs[1].toLowerCase().equals("on"))
							{
								blacklistOn = true;
							}
						}
						else if (!linetrim.isEmpty())
						{
							blackList.add(linetrim.toLowerCase());
						}
					}
				}
				in.close();
				gs.getLogger().info(this.getClass().getSimpleName() + " : reloadFile blacklist " + (blacklistOn?"on":"off"));
				setBlackList(blacklistOn, blackList);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public class RegisterLimitList
	{
		boolean registerLimitOn = false;
		Map<Integer, Integer> dayRegisterLimitList = new TreeMap<>();
		RegisterLimitList ()
		{
			
		}
		
		synchronized void setRegisterLimitList(boolean registerLimitOn, Map<Integer, Integer> dayRegisterLimitList)
		{
			this.registerLimitOn = registerLimitOn;
			this.dayRegisterLimitList = dayRegisterLimitList;
			for (Map.Entry<Integer, Integer> e : dayRegisterLimitList.entrySet())
			{
				gs.getLogger().info("day register limit : (" + GameTime.getDateTimeStampStr(GameTime.getDayStartTime(e.getKey())) + "," + e.getValue() + ")");
			}
			//gs.getLogger().info("cur day " + gs.getDay() + " limit : " + getCurRegisterLimit() + " " + gs.getTime());
		}
		
		synchronized int getCurRegisterLimit()
		{
			int limit = Integer.MAX_VALUE;
			if (registerLimitOn)
			{
				Integer regesterLimit = dayRegisterLimitList.get(GameTime.getDay());
				if (regesterLimit != null)
					limit = regesterLimit;
					
			}
			return limit;
		}
		
		void setCfgFile(final String fileName)
		{
			gs.getResourceManager().addWatch(fileName, this::reloadFile);
		}
		
		void reloadFile(String filePath)
		{
			try 
			{
				boolean registerLimitOn = false;
				Map<Integer, Integer> dayRegisterLimitList = new TreeMap<>();
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
				int lineNo = 1;
				for (String line = in.readLine(); line != null; line = in.readLine()) 
				{
					//System.out.println(line);
					String linetrim = line.trim();
					if (!linetrim.startsWith("#"))
					{
						if (linetrim.startsWith("RegisterLimit"))
						{
							String[] strs = linetrim.split("\\s+", 2);
							if (strs.length == 2 && strs[0].equals("RegisterLimit") && strs[1].toLowerCase().equals("on"))
							{
								registerLimitOn = true;
							}
						}
						else if (!linetrim.isEmpty())
						{
							String[] strs = linetrim.split("\\s+", 2);
							if (strs.length == 2)
							{
								try
								{
									int dayTime = GameTime.parseDate(strs[0]);
									int limit = Integer.parseInt(strs[1]); 
									int key = GameTime.getDay(dayTime);
									dayRegisterLimitList.put(key, limit);
								}
								catch (Exception e)
								{
									gs.getLogger().warn("file line " + lineNo + ":" +  e.getMessage());
								}
							}
						}
					}
					lineNo++;
				}
				in.close();
				gs.getLogger().info(this.getClass().getSimpleName() + " : reloadFile register Limit list " + (registerLimitOn?"on":"off"));
				setRegisterLimitList(registerLimitOn, dayRegisterLimitList);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public class AssertIgnoreList
	{
		Set<String> assertIgnoreList = new HashSet<>();
		AssertIgnoreList()
		{
			
		}
		
		synchronized void setAssertIgnoreList(Set<String> lst)
		{
			assertIgnoreList = lst;
		}
		
		synchronized Set<String> getAssertIgnoreKeyWords()
		{
			return assertIgnoreList;
		}
		
		void setCfgFile(final String fileName)
		{
			gs.getResourceManager().addWatch(fileName, this::reloadFile);
		}
		
		void reloadFile(String filePath)
		{
			try 
			{
				Set<String> ignoreList = new HashSet<>();
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
				for (String line = in.readLine(); line != null; line = in.readLine()) 
				{
					//System.out.println(line);
					String linetrim = line.trim();
					if (!linetrim.startsWith("#"))
					{
						if (!linetrim.isEmpty())
						{
							ignoreList.add(linetrim);
						}
					}
				}
				in.close();
				gs.getLogger().info(this.getClass().getSimpleName() + " : reloadFile ignorelist ");
				setAssertIgnoreList(ignoreList);
				syncClientAssertIgnoreList(ignoreList);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	class LoginQueue 
	{
		class LoginEntity
		{
			public LoginEntity(int session, int p, SBean.DBRegisterID registerID, SBean.UserLoginInfo info, boolean checkAccountExist)
			{
				this.session = session;
				this.p = p;
				this.registerID = registerID;
				this.info = info;
				this.checkAccountExist = checkAccountExist;
			}
			
			public int session;
			public int p;
			public SBean.DBRegisterID registerID;
			public SBean.UserLoginInfo info;
			public boolean checkAccountExist;
		}
		
		public synchronized int size()
		{
			return queue.size();
		}
		
		public synchronized void add(int sid, SBean.DBRegisterID registerID, SBean.UserLoginInfo info, boolean checkAccountExist)
		{
			if (!queue.containsKey(sid))
				queue.put(sid, new LoginEntity(sid, queue.size()+1, registerID, info, checkAccountExist));
			//gs.getLogger().debug("login queue add session " + sid + ", uid " + registerID.uid + ", queue.size " + queue.size());
		}
		
		public synchronized void remove(int sid)
		{
			queue.remove(sid);
			//gs.getLogger().debug("login queue remove session " + sid + ", queue.size " + queue.size());
		}
		
		public synchronized LoginEntity pop()
		{
			LoginEntity e = null;
			Iterator<Map.Entry<Integer, LoginEntity>> iter = queue.entrySet().iterator();
			if( iter.hasNext() )
			{
				e = iter.next().getValue();
				iter.remove();
			}
			return e;
		}
		
		public synchronized LoginEntity getTop()
		{
			LoginEntity e = null;
			Iterator<Map.Entry<Integer, LoginEntity>> iter = queue.entrySet().iterator();
			if( iter.hasNext() )
			{
				e = iter.next().getValue();
			}
			return e;
		}
		
		public synchronized int queryPos(int sid)
		{
			LoginEntity pos = queue.get(sid);
			return pos == null ? 0 : pos.p;
		}
		
		private synchronized void reorder()
		{
			AtomicInteger i = new AtomicInteger(0);
			queue.values().stream().forEach(p->p.p = i.incrementAndGet());
		}
		
		public void onTimer(int timeTick)
		{
			reorder();
			int count = 0;
			while (getOnlineUserCount() < gs.getConfig().cap)
			{
				if (count >= 10)
					break;
				LoginEntity e = getTop();
				if (e == null)
					break;
				if (!userLogin(e.session, e.info, e.registerID, e.checkAccountExist))
					break;
				remove(e.session);
				++count; 
			}
		}
		
		public void onReceiveCancelQueue(int session)
		{
			remove(session);
		}
		
		private LinkedHashMap<Integer, LoginEntity> queue = new LinkedHashMap<>();
	}

	public static class LevelRoleCache
	{
		public static final int	MAX_LEVEL_ROLE_CACHE_LENGTH	= 200;
		public int level;
		public List<Integer> roles = new LinkedList<>();
		
		public LevelRoleCache(int lvl)
		{
			this.level = lvl;
		}

		public synchronized void updateRole(int rid)
		{
			roles.remove(new Integer(rid));
			if (roles.size() >= MAX_LEVEL_ROLE_CACHE_LENGTH)
				roles.remove(0);

			roles.add(rid);
		}

		public synchronized void removeRole(int rid)
		{
			roles.remove(new Integer(rid));
		}

		public synchronized Integer getRandomRole()
		{
			if (roles.isEmpty())
				return null;
			int index = GameRandom.getRandom().nextInt(roles.size());
			return roles.get(index);
		}
		
		public synchronized void getRandomLevelRoles(Set<Integer> notMatchRids, Set<Integer> recommondRids, int count)
		{
			int size = roles.size() > 10 ? 10 : roles.size();
			for(int i=0; i<size && recommondRids.size()<count; i++)
			{
				Integer rid = getRandomRole();
				if(rid != null && !notMatchRids.contains(rid))
				{
					recommondRids.add(rid);
					notMatchRids.add(rid);
				}
			}
		}
		
	}
	
	private LevelRoleCache tryGetCreateLevelRoleCache(int level)
	{
		synchronized (maplevel2rsCaches)
		{
			LevelRoleCache cache = this.maplevel2rsCaches.get(level);
			if (cache == null)
			{
				cache = new LevelRoleCache(level);
				this.maplevel2rsCaches.put(level, cache);
			}
			return cache;
		}
	}
	
	private LevelRoleCache getRandomLevelRoleCache(int levelMin, int levelMax)
	{
		synchronized (maplevel2rsCaches)
		{
			NavigableMap<Integer, LevelRoleCache> subMap = this.maplevel2rsCaches.subMap(levelMin, true, levelMax, true);
			if (subMap == null || subMap.isEmpty())
				return null;
			Object[] keys = subMap.keySet().toArray();
			int index = GameRandom.getRandom().nextInt(keys.length);
			int level = (int) keys[index];
			return subMap.get(level);
		}
	}
	
	void updateLevelRoleCache(int roleId, int level)
	{
		LevelRoleCache cache = tryGetCreateLevelRoleCache(level);
		cache.updateRole(roleId);
	}
	
	void updateLevelRoleCache(int roleId, int oldLvl, int newLvl)
	{
		LevelRoleCache oldLvlcache = this.maplevel2rsCaches.get(oldLvl);
		if (oldLvlcache != null)
			oldLvlcache.removeRole(roleId);
		LevelRoleCache newLvlcache = tryGetCreateLevelRoleCache(newLvl);
		newLvlcache.updateRole(roleId);
	}
	
	public Integer getRandomLevelRole(int level)
	{
		LevelRoleCache cache = this.maplevel2rsCaches.get(level);
		return cache == null ? null : cache.getRandomRole();
	}
	
	public Integer getRandomLevelRole(int levelMin, int levelMax)
	{
		LevelRoleCache cache = getRandomLevelRoleCache(levelMin, levelMax);
		return cache == null ? null : cache.getRandomRole();
	}
	
	public void getRandomLevelRoles(int lvl, Set<Integer> notMatchRids, Set<Integer> recommondRids, int count)
	{
		LevelRoleCache cache = this.maplevel2rsCaches.get(lvl);
		if(cache != null)
			cache.getRandomLevelRoles(notMatchRids, recommondRids, count);
	}
	
	public void getRandomLevelRoles(Set<Integer> notMatchRids, Set<Integer> recommondRids, int count)
	{
		getRandomLevelRoles(notMatchRids, recommondRids, count, null, null);
	}
	
	public void getRandomLevelRoles(Set<Integer> notMatchRids, Set<Integer> recommondRids, int count, Integer minLevel, Integer maxLevel)
	{
		if (this.maplevel2rsCaches.isEmpty())
			return;
		Integer minLvl = minLevel == null ? this.maplevel2rsCaches.firstKey() : minLevel;
		Integer maxLvl = maxLevel == null ? this.maplevel2rsCaches.lastKey() : maxLevel;
		
		if(minLvl == null || maxLvl == null)
			return;
		
		for(int i=0; i<10; i++)
		{
			LevelRoleCache cache = this.maplevel2rsCaches.get(GameRandom.getRandInt(minLvl, maxLvl + 1));
			if(cache != null)
				cache.getRandomLevelRoles(notMatchRids, recommondRids, count);
			
			if(recommondRids.size() >= count)
				break;
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	class QuizGiftActivity
	{
		public static final int QUIZ_RANK_COUNT = 50;
		public int startTime = 0;
		public List<Integer> questions = new ArrayList<>();
		public int curSeq = 0;
		public List<SBean.QuizGiftRankRole> ranks = new LinkedList<>();
		public QuizGiftActivity()
		{
			
		}
		
		int getRankCap()
		{
			int cap = QUIZ_RANK_COUNT < GameData.getInstance().getDailyQuizGift().maxRewardRank ? GameData.getInstance().getDailyQuizGift().maxRewardRank : QUIZ_RANK_COUNT;
			return cap;
		}
		
		int getCurQuestion()
		{
			int curQuestion = (this.curSeq > 0 && this.curSeq <= this.questions.size()) ? this.questions.get(curSeq-1) : 0;
			return curQuestion;
		}
		
		synchronized SBean.QuizGiftInfo getQuizGiftInfo(Role role)
		{
			return new SBean.QuizGiftInfo(this.startTime, this.curSeq, this.getCurQuestion(), role.getQuizGiftData(this.startTime).kdClone());
		}
		
		synchronized void onTimer(int now)
		{
			SBean.DailyQuizGiftCFGS cfg = GameData.getInstance().getDailyQuizGift();
			int dayOpenTime = GameTime.getDayTime(GameTime.getDay(now), cfg.openTime);
			if (this.startTime < dayOpenTime)
			{
				if (now >= dayOpenTime && now < dayOpenTime + cfg.questionsCount * cfg.maxAnswerTime)
				{
					this.startTime = now;
					this.questions = GameData.getInstance().getActivityRandomQuiz(cfg.questionsCount);
					this.curSeq = 1;
					this.ranks.clear();
					gs.getLogger().debug("daily quiz gift : start >>>>>>>>>>>>>>>>>>>>>>>>>");
				}
			}
			else
			{
				if (this.curSeq > 0)
				{
					int nowSeq = (now - this.startTime)/cfg.maxAnswerTime + 1;
					if (nowSeq > this.curSeq)
						this.curSeq = nowSeq;
					if (this.curSeq > cfg.questionsCount)
						this.curSeq = 0;
					gs.getLogger().debug("daily quiz gift : calc seq " + nowSeq + ", curSeq " + curSeq);
				}
				else
				{
					if (this.curSeq == 0)
					{
						gs.getLogger().debug("daily quiz gift : end <<<<<<<<<<<<<<<<<<<<<<<<<<<<");
						for (int i = 0; i < this.ranks.size(); ++i)
						{
							SBean.QuizGiftRankRole e = this.ranks.get(i);
							int rank = i + 1;
							SBean.QuizBonusRewardCFGS qbrCfg = GameData.getInstance().getQuizBonusRewardCFGS(rank);
							if(qbrCfg == null)
								return;
							
							List<SBean.GameItem> att = GameData.getInstance().toGameItems(qbrCfg.rewards);
							if(att.isEmpty() && qbrCfg.title == 0)
								continue;
							
							
							LoginManager.this.exeCommonRoleVisitor(e.roleId, false, new LoginManager.CommonRoleVisitor()
							{
								@Override
								public boolean visit(Role role, Role sameUserRole)
								{
									if (!att.isEmpty())
									{
										List<Integer> addinfo = new ArrayList<>();
										addinfo.add(rank);
										addinfo.add(e.bonus);
										
										MailBox mailbox = role.getMailBox();
										mailbox.addSysMail(MailBox.SysMailType.QuizActivity, MailBox.QUIZACTIVITY_MAIL_MAX_RESERVE_TIME, "", att, addinfo);
									}
									
									if(qbrCfg.title > 0)
										role.addRoleTitle(qbrCfg.title);
									
									return true;
								}
								
								@Override
								public void onCallback(boolean success)
								{
									gs.getLogger().debug("mail role " + e.roleId + " quiz rank reward " + ( success ? " success !" : " failed !"));
								}
							});
						}
						this.curSeq = -1;
					}
				}
			}
		}
		
		synchronized boolean tryAnswerQuestion(Role role, int startTime, int curSeq, int answer, boolean useDoubleBonus)
		{
			if (startTime != this.startTime || curSeq != this.curSeq)
				return false;
			SBean.QuestionCFGS questionCfg = GameData.getInstance().getActivityQuestion(getCurQuestion());
			if (questionCfg == null)
				return false;
			SBean.DailyQuizGiftCFGS cfg = GameData.getInstance().getDailyQuizGift();
			synchronized (role)
            {
				SBean.DBQuizGift data = role.getQuizGiftData(startTime);
				if (curSeq <= data.lastAnsweredQuestionSeq)
					return false;
				if (useDoubleBonus && data.doubleBonusUsed >= cfg.doubleBonusTimes)
					return false;
				int exp = cfg.answerQuestionExp * GameData.getInstance().getQuizGiftLevelBaseExp(role.level);
				if (questionCfg.answer == answer)
				{
					int now = GameTime.getTime();
					int useTime = (now - this.startTime)%cfg.maxAnswerTime;
					int quicklyAnswerAddBonus = GameData.getInstance().getAddBonusOnQuizAnswerQuickly(useTime);
					
					int continuous = data.continuousRightAnswer + 1;
					int continuousAnswerAddBonus = GameData.getInstance().getAddBonusOnQuizAnswerContinuous(continuous);
					
					int addBonus = cfg.baseBonus + quicklyAnswerAddBonus + continuousAnswerAddBonus;
					data.bonus += addBonus;
					if (useDoubleBonus)
						data.bonus += cfg.baseBonus;
					exp += cfg.answerRightExp * GameData.getInstance().getQuizGiftLevelBaseExp(role.level);
					updateRoleRank(role.id, role.name, data.bonus);
				}
				if (useDoubleBonus)
					data.doubleBonusUsed += 1;
				data.lastAnsweredQuestionSeq = this.curSeq;
				data.lastAnsweredQuestionResult = answer;
				int realAddExp = (int) role.syncAddExp(exp, GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_OTHER, 0);
				data.expReward += realAddExp;
				role.logTaskScheduleData(GameData.SCHEDULE_TYPE_QUESTION);
            }
			return true;
		}
		
		void updateRoleRank(int roleId, String roleName, int bonus)
		{
			int lastRankBonus = this.ranks.isEmpty() ? 0 : this.ranks.get(this.ranks.size()-1).bonus;
			int maxRank = getRankCap();
			if (bonus > lastRankBonus || this.ranks.size() < maxRank)
			{
				int rank = this.ranks.size();
				for (int i = 0; i < this.ranks.size(); ++i)
				{
					SBean.QuizGiftRankRole role = this.ranks.get(i);
					if (bonus > role.bonus)
					{
						rank = i;
						break;
					}
				}
				for (int i = rank; i < this.ranks.size(); ++i)
				{
					SBean.QuizGiftRankRole role = this.ranks.get(i);
					if (roleId == role.roleId)
					{
						this.ranks.remove(i);
						break;
					}
				}
				this.ranks.add(rank, new SBean.QuizGiftRankRole(bonus, roleId, roleName));
				
				if (this.ranks.size() > maxRank)
					this.ranks.subList(maxRank, this.ranks.size()).clear();
			}
		}
		
		synchronized List<SBean.QuizGiftRankRole> getRanks(int startTime)
		{
			if (startTime != this.startTime)
				return null;
			int len = this.ranks.size() > QUIZ_RANK_COUNT ? QUIZ_RANK_COUNT : this.ranks.size();
			return Stream.clone(new ArrayList<SBean.QuizGiftRankRole>(this.ranks.subList(0, len)));
		}
	}
	
	class RedEnvelope
	{
		private SBean.RedEnvelopeLevelCFGS levelCfg;
		private SBean.RedEnvelopeCFGS cfg;
		private int snatchedCount;
		private int startTime;
		private int id;
		public RedEnvelope(SBean.RedEnvelopeCFGS cfg, SBean.RedEnvelopeLevelCFGS levelCfg, int now, int seq)
		{
			this.cfg = cfg;
			this.levelCfg = levelCfg;
			this.snatchedCount = 0;
			this.startTime = now;
			this.id = seq;
		}
		
		public int getStartTime()
		{
			return this.startTime;
		}
		
		public int getId()
		{
			return this.id;
		}
		
		public boolean isValid(int now)
		{
			return now <= this.startTime + levelCfg.lifeTime;
		}
		
		public void broadcastRedEnvelopeInfo(int levelReq, int exceptRoleId)
		{
			addBroadcastTaskEvent(() ->
			{
				InvokeOnLineRoleFunction(role -> 
					{
						if (role.level >= levelReq && role.id != exceptRoleId)
							role.notifyRedEnvelopeInfo(startTime, id, levelCfg.payAmount);
					});
			});
		}
		
		public synchronized int snatchRedEnvelope(Role role)
		{
			if (this.snatchedCount >= this.levelCfg.count)
			{
				if (role.dayGetRedEnvelopesEmptyGift < GameData.getInstance().getRedEnvelopeCFGS().dayEmptyGiftTimes && role.reveiveRedEnvelopeEmptyGift())
					return GameData.PROTOCOL_OP_SNATCH_RED_ENEVLOPE_EMPTY_GIFT;
				return GameData.PROTOCOL_OP_SNATCH_RED_ENEVLOPE_FIALED;
			}
			float probability = role.isDaySnatchRedEnvelopesOverlimit() ? cfg.overlimitPercent : (this.snatchedCount <= this.levelCfg.count/2) ?  cfg.firstHalfPercent : cfg.secondHalfPercent;
			float r = GameRandom.getRandom().nextFloat();
			if (r >= probability)
			{
				if (role.dayGetRedEnvelopesEmptyGift < GameData.getInstance().getRedEnvelopeCFGS().dayEmptyGiftTimes && role.reveiveRedEnvelopeEmptyGift())
					return GameData.PROTOCOL_OP_SNATCH_RED_ENEVLOPE_EMPTY_GIFT;
				return GameData.PROTOCOL_OP_SNATCH_RED_ENEVLOPE_EMPTY;
			}
			int amount = GameRandom.getRandInt(levelCfg.minDiamond, levelCfg.maxDiamond+1);
			role.receiveRedEnvelope(amount);
			++this.snatchedCount;
			return amount;
		}
	}
	
	public class SnatchRedEnvelopeActivity
	{
		private static final int RED_ENVELOPE_CHECK_RAND_INTERVAL_MAX = 60;
		private int randTick;
		private int lastCheckTime;
		private int payTriggerCount;
		private AtomicInteger RedEnvelopeSeq = new AtomicInteger();
		private Map<Integer, RedEnvelope> redEnvelopes = new ConcurrentHashMap<>();
		public SnatchRedEnvelopeActivity()
		{
			this.randTick = GameRandom.getRandInt(0, RED_ENVELOPE_CHECK_RAND_INTERVAL_MAX);
		}
		
		public synchronized void onTimer(int now)
		{
			SBean.RedEnvelopeCFGS cfg = GameData.getInstance().getRedEnvelopeCFGS();
			int day = GameTime.getDay(now);
			int dayOpenTime = GameTime.getDayTime(day, cfg.beginTime);
			int dayCloseTime = GameTime.getDayTime(day, cfg.beginTime);
			if (now >= dayOpenTime && now < dayCloseTime)
			{
				if (now % RED_ENVELOPE_CHECK_RAND_INTERVAL_MAX == randTick)
				{
					if (lastCheckTime == 0)
					{
						lastCheckTime = dayOpenTime;
						payTriggerCount = cfg.checkThreshold;
					}
					int checkIntervalTimes = GameData.getTimesCost(cfg.checkInterval, day - GameTime.getDay(gs.getOpenTime())+ 1);
					int elapseInterval = (now -  dayOpenTime)/RED_ENVELOPE_CHECK_RAND_INTERVAL_MAX;
					int lastCheckInterval = (lastCheckTime - dayOpenTime)/RED_ENVELOPE_CHECK_RAND_INTERVAL_MAX;
					if (elapseInterval >= lastCheckInterval + checkIntervalTimes)
					{
						if (payTriggerCount < cfg.checkThreshold)
						{
							sendRedEnvelopeImpl(0, cfg, GameData.getRandomRedEnvelopeLevelCFGS(cfg.levelData), now);
							LoginManager.this.roleAddRollNotice(GameData.ROLLNOTICE_TYPE_SYS_RED_ENVELOPE, "");
						}
						lastCheckTime = now;
						payTriggerCount = 0;
					}
				}
			}
			Iterator<Map.Entry<Integer, RedEnvelope>> it = redEnvelopes.entrySet().iterator();
			while (it.hasNext())
			{
				RedEnvelope redEnvelope = it.next().getValue();
				if (!redEnvelope.isValid(now))
					it.remove();
			}
		}
		
		private void sendRedEnvelopeImpl(int triggerRoleId, SBean.RedEnvelopeCFGS cfg, SBean.RedEnvelopeLevelCFGS levelcfg, int timeTick)
		{
			RedEnvelope redEnvelope = new RedEnvelope(cfg, levelcfg, timeTick, RedEnvelopeSeq.incrementAndGet());
			redEnvelope.broadcastRedEnvelopeInfo(cfg.openLevel, triggerRoleId);
			redEnvelopes.put(redEnvelope.getId(), redEnvelope);
		}
		
		public synchronized void testSendPayRedEnvelope(int roleId, String roleName, int pay)
		{
			SBean.RedEnvelopeCFGS cfg = GameData.getInstance().getRedEnvelopeCFGS();
			SBean.RedEnvelopeLevelCFGS levelcfg = GameData.getPayRedEnvelopeLevelCFGS(cfg.levelData, pay);
			if (levelcfg != null)
			{
				sendRedEnvelopeImpl(roleId, cfg, levelcfg, GameTime.getTime());
				LoginManager.this.roleAddRollNotice((byte)levelcfg.rollNoticeId, roleName);
				++payTriggerCount;
			}
		}
		
		public int trySnatchRedEnvelope(Role role, int startTime, int id)
		{
			RedEnvelope redEnvelope = redEnvelopes.get(id);
			if (redEnvelope == null || redEnvelope.getStartTime() != startTime)
				return GameData.PROTOCOL_OP_SNATCH_RED_ENEVLOPE_TIMEOUT;
			return redEnvelope.snatchRedEnvelope(role);
		}
	}
	
	// 烟花
	public void sendPlayFirework(String roleName, int mapID, int fireworkID)
	{
		addBroadcastTaskEvent(() ->
		{
			InvokeOnLineRoleFunction(role -> 
				{
					role.notifyFirework(roleName, mapID, fireworkID);
				});
		});
		this.roleAddRollNotice(GameData.ROLLNOTICE_TYPE_FIREWORK, roleName + "|" + mapID + "|" + fireworkID);
	}
	
	// 发送角色结婚特效
	public void sendRoleMarriageFlash(int grade, int mapID, int line)
	{
		addBroadcastTaskEvent(() ->
		{
			InvokeOnLineRoleFunction(role -> 
				{
					if (role.gameMapContext.getCurMapId() == mapID && role.gameMapContext.getCurMapInstance() == line)
						gs.getRPCManager().sendStrPacket(role.netsid, new SBean.role_marriage_here(grade, mapID, line));
				});
		});
	}
	
	public void syncClientAssertIgnoreList(Set<String> list)
	{
		addBroadcastTaskEvent(() ->
		{
			InvokeOnLineRoleFunction(role -> 
				{
					gs.getRPCManager().sendStrPacket(role.netsid, new SBean.assert_ignore_list(list));
				});
		});
	}
	
//	//返回是否因人满而阻塞需要进入排队
//	public boolean tryUserLogin(int sessionid, SBean.DBRegisterID registerID, SBean.UserLoginInfo info, int loginroleid, SBean.CreateRoleParam createParam)
//	{
//		String username = GameData.getUserName(registerID);
//		if (this.getOnlineRoleCount() >= gs.getConfig().cap && this.getUserRole(username) == null && !this.getLoginWhiteList().isPrivilegedAccount(registerID.uid))
//		{
//			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_ONLINE_ROLE_FULL, 0, ""));
//			return false;
//		}
//		this.getLoginVerifier().verify(sessionid, info, registerID.uid, registerID.channel, (sid, success) -> {
//            if (!success)
//            {
//                gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_VERIFY_FAILED, 0, ""));
//                return;
//            }
//            gs.getRPCManager().setSessionAuthed(sessionid);
//            if (loginroleid == 0)
//            {
//                if (createParam == null)
//                {
//                	this.tryVerifyRegister(username, info.arg.exParam, (errCode) ->{
//                		if (errCode < 0)
//                		{
//                			gs.getRPCManager().sendStrPacket(sessionid, new SBean.user_login_res(GameData.USERLOGIN_VERIFY_REGISTER_FAILED, errCode, ""));
//                			return;
//                		}
//                		this.userLogin(sid, info, registerID, errCode > 0);
//                	});
//                }
//                else
//                {
//                    this.userCreateRole(sid, info, registerID.zoneId, username, createParam);
//                }
//            }
//            else
//            {
//                this.roleLogin(sid, info, username, loginroleid);
//            }
//        });
//		return true;
//	}
	
	public static class UserLoginStub
	{
		public UserLoginStub(int sid, SBean.UserLoginInfo loginInfo, UserLoginVerifierCallback callback)
		{
			time = GameTime.getTime();
			this.sid = sid;
			this.loginInfo = loginInfo;
			this.callback = callback;
		}
		
		public boolean isTimeout(int now)
		{
			return time + 30 < now;
		}
		
		public int time;
		public int sid;
		public SBean.UserLoginInfo loginInfo;
		public UserLoginVerifierCallback callback;
	}
	
	interface UserLoginVerifierCallback
	{
		void onCallback(int sid, boolean success);
	}
	public class UserLoginVerifier
	{
		private Midas midas;
		private ConcurrentMap<Integer, UserLoginStub> verifyMap = new ConcurrentHashMap<>();
		
		public UserLoginVerifier()
		{
			
		}
		
		public void startup()
		{
			this.midas = new Midas(gs);
			this.midas.start();
		}
		
		public void shutdown()
		{
			this.midas.destroy();
		}
		
		public void onTimer(int timeTick)
		{
			Iterator<Map.Entry<Integer, UserLoginStub>> it = verifyMap.entrySet().iterator();
			while( it.hasNext() )
			{
				UserLoginStub v = it.next().getValue();
				if( v.isTimeout(timeTick) )
				{
					it.remove();
					v.callback.onCallback(v.sid, false);
				}
			}
		}
		
		public void verify(int sid, SBean.UserLoginInfo info, String uid, String channel, UserLoginVerifierCallback callback)
		{
			UserLoginStub stub = new UserLoginStub(sid, info, callback);
			if (null != verifyMap.putIfAbsent(sid, stub))
			{
				callback.onCallback(sid, false);
			}
			Midas.UserInfo uinfo = new Midas.UserInfo(info.client.gameAppID, channel, uid, info.arg.loginKey);
			midas.loginVerify(uinfo, sid, info.arg.loginType == SBean.UserLoginParam.eLoginNormal, (sid1, success) -> {
                UserLoginStub stub1 = verifyMap.remove(sid1);
                if (stub1 == null)
                    return;
                stub1.callback.onCallback(stub1.sid, success);
            });
		}
		
	}
	
	interface VerifyRegisterCallback
	{
		void onCallback(int errCode);
	}
	public void tryVerifyRegister(String username, String cdkey, VerifyRegisterCallback callback)
	{
		if (gs.getConfig().registerVerify == 0)
		{
			callback.onCallback(GameData.USERLOGIN_ACTIVE_KEY_VERIFY_SUCCESS);
			return;
		}
		if (cdkey.isEmpty())
		{
			callback.onCallback(GameData.USERLOGIN_ACTIVE_KEY_ACCOUNT_CHECK_REQUIRE);
			return;
		}
		int batchId = ActiveKeyGen.getCheckBatchId(cdkey);
		if(batchId <= 0)
		{
			callback.onCallback(GameData.USERLOGIN_ACTIVE_KEY_INVALID);
			return;
		}
		if( gs.getConfig().registerKeyBatchIds == null || !gs.getConfig().registerKeyBatchIds.contains(batchId))
		{
			callback.onCallback(GameData.USERLOGIN_ACTIVE_KEY_BATCHID_INVALID);
			return;
		}
		gs.getGameWebService().doVerifyRegister(cdkey, gs.getConfig().id, username, (result) -> {
			callback.onCallback(result);
		});
	}
	
	public void genUserId(GenUserIDCallback callback)
	{
		gs.getDB().execute(new GenUserIDTrans(callback));
	}
	interface GenUserIDCallback
	{
		void onCallback(int uid);
	}
	public class GenUserIDTrans implements Transaction
	{
		public GenUserIDTrans(GenUserIDCallback callback)
		{
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			Integer maxid = maxids.get("autouserid");
			if (maxid == null)
				uid = 1;
			else
				uid = maxid + 1;
			maxids.put("autouserid", uid);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			this.callback.onCallback(uid);
		}

		@AutoInit
		public Table<String, Integer>	maxids;
		
		GenUserIDCallback callback;
		public int			uid	= -1;
	}
	
	//返回是否没有阻塞在最大人数限制
	public boolean userLogin(final int sid, final SBean.UserLoginInfo loginInfo, final SBean.DBRegisterID registerID, boolean checkAccountExist)
	{
		if (!gs.getRPCManager().checkSession(sid))//session断开直接返回
			return true;
		String username = GameData.getUserName(registerID);
		if (userLock.putIfAbsent(username, 0) != null)
		{
			gs.getLogger().debug("session " + sid + " userLogin user [" + username + "] other session being login!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_LOCK_BUSY, -1, ""));
			return true;
		}
		Integer oldrid = maps2r.putIfAbsent(sid, 0);
		if (oldrid != null) 
		{
			userLock.remove(username);
			gs.getLogger().debug("session " + sid + " userLogin user [" + username + "] session user already login!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ALREADY_LOGIN, -1, ""));
			return true;
		}
		
		Role oldrole = getUserRole(username);
		if (oldrole != null)
		{
			try
			{
				oldrole.lock();
				if (!oldrole.isClosed())//此处可能role已经移出容器变为无效对象了
				{
					int now = GameTime.getTime();
					if (oldrole.getBan().isBanLogin(now))
					{
						maps2r.remove(sid);
						userLock.remove(username);
						BanInfo banInfo = oldrole.getBan().getBanLoginInfo(now);
						gs.getLogger().warn("session " + sid + " userLogin user  [" + username + "] load user in db, but be banned, leftTime = " + banInfo.leftTime + "!");
	                	gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ROLE_BANNED, banInfo.leftTime, banInfo.reason));
						return true;
					}
					int oldsession = oldrole.changeSessionID(sid);//和role.onTimer()有竞争关系，需要在role锁内调用
					maps2r.remove(oldsession);
					gs.getRPCManager().sendStrPacket(oldsession, new SBean.user_force_close(GameData.FORCE_CLOSE_REPLACE));
					if (mapRoles.put(oldrole.id, oldrole) == null)
					{
						onlineRoles.merge(oldrole.getGameId(), 1, (ov, nv) -> ov + nv);
					}
					
					maps2r.put(sid, oldrole.id);	
					mapu2s.put(username, sid);
					userLock.remove(username, 0);
					gs.getRPCManager().sendStrPacket(sid, new SBean.server_info(GameTime.getTime(), gs.getConfig().id, GameTime.getDay(gs.getOpenTime())));
					oldrole.login(loginInfo, Role.ROLE_LOAD_FROM_MEMORY);
					return true;
				}
			}
			finally
			{
				oldrole.unlock();
			}
		}
		if (!this.noRoleMaps2u.containsKey(sid) && this.getOnlineUserCount() >= gs.getConfig().cap && !this.getLoginWhiteList().isPrivilegedAccount(registerID.uid))
		{
			if (this.getLoginQueue().size() >= gs.getConfig().queue)
			{
				maps2r.remove(sid);
	            userLock.remove(username);
				gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_QUEUE_ROLE_FULL, 0, ""));
				return true;
			}
			maps2r.remove(sid);
            userLock.remove(username);
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ONLINE_ROLE_FULL, 0, ""));
			return false;
		}
		noRoleMaps2u.put(sid, username);
		gs.getDB().execute(new UserLoginTrans(loginInfo, registerID, checkAccountExist, (errCode, banInfo, rolelist) -> {
            if (errCode < 0)
            {
            	maps2r.remove(sid);
                userLock.remove(username);
                if (banInfo == null)
                {
                	gs.getLogger().warn("session " + sid + " userLogin user [" + username + "] load user failed, errCode=" + errCode);
                	if (errCode == -1)
                		gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_NEED_VERIFY_REGISTER, errCode, ""));
                	else
                		gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_LOAD_USER_FAILED, errCode, ""));
                }
                else
                {
                	gs.getLogger().warn("session " + sid + " userLogin user  [" + username + "] load user in db, but be banned, leftTime = " + banInfo.leftTime + "!");
                	gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ROLE_BANNED, banInfo.leftTime, banInfo.reason));
                }
                return;
            }
            maps2r.remove(sid);
            userLock.remove(username);
            gs.getRPCManager().sendStrPacket(sid, new SBean.server_info(GameTime.getTime(), gs.getConfig().id, GameTime.getDay(gs.getOpenTime())));
            gs.getRPCManager().sendStrPacket(sid, new SBean.user_role_list(rolelist));
            gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_OK, rolelist.size(), ""));
//            if (errCode == 1)
//                gs.getTLogger().logUserRegister(registerID, loginInfo);
//            gs.getTLogger().logUserLogin(registerID, loginInfo);
        }));
		return true;
	}
	
	interface UserLoginTransCallback
	{
		void onCallback(int errCode, BanInfo banInfo, List<SBean.RoleBrief> rolelist);
	}
	public class UserLoginTrans implements Transaction
	{
		public UserLoginTrans(SBean.UserLoginInfo loginInfo, SBean.DBRegisterID registerID, boolean checkAccountExist, UserLoginTransCallback callback)
		{
			this.loginInfo = loginInfo;
			this.registerID = registerID;
			this.checkAccountExist = checkAccountExist;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			boolean userRegister = false;
			String username = GameData.getUserName(registerID);
			int now = GameTime.getTime();
			DBUser dbuser = user.get(username);
			if (dbuser == null)
			{
				if (checkAccountExist)
				{
					errCode = -1;
					return false;
				}
				dbuser = new DBUser().newUser(registerID, now, loginInfo.arg.loginKey);
				user.put(username, dbuser);
				userRegister = true;
			}
			else if ((loginInfo.arg.loginType & SBean.UserLoginParam.eLoginReconnect) == SBean.UserLoginParam.eLoginNormal)
			{
				dbuser.lastLoginKey = loginInfo.arg.loginKey;
				dbuser.lastLoginTime = now;
				user.put(username, dbuser);
			}
			else if (loginInfo.arg.loginType  == SBean.UserLoginParam.eLoginReconnect)
			{
				if (!dbuser.lastLoginKey.isEmpty())
				{
					if (!dbuser.lastLoginKey.equals(loginInfo.arg.loginKey) || dbuser.lastLoginTime + 86400 < now)
					{
						errCode = -2;
						return false;
					}
				}
			}
			Ban ban = new Ban(dbuser.ban);
			if (ban.isBanLogin(now))
            {
				errCode = -3;
				banInfo = ban.getBanLoginInfo(now);
				return false;
            }
			rolelist = new ArrayList<>();
			for (int rid : dbuser.rolesID)
			{
				DBRole dbrole = role.get(rid);
				if (dbrole != null)
					rolelist.add(dbrole.getRoleBrief());
				else
					rolesLoadFailedList.add(rid);
			}
			errCode = rolesLoadFailedList.isEmpty() ? (userRegister ? 1 : 0) : -4;
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			String username = GameData.getUserName(registerID);
			gs.getLogger().debug("UserLoginTrans (" + username + ") loginType=" + loginInfo.arg.loginType + ": errCode="+errCode + ", roles size=" + rolelist.size());
			if (!rolesLoadFailedList.isEmpty())
				gs.getLogger().error("UserLoginTrans (" + username + ") failed load role " + rolesLoadFailedList + ", may be db corruption!");
			callback.onCallback(errCode, banInfo, rolelist);
		}
		
		@AutoInit
		public Table<String, DBUser> user;
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		private SBean.UserLoginInfo loginInfo;
		private SBean.DBRegisterID registerID;
		private boolean checkAccountExist;
		private UserLoginTransCallback callback;
		private BanInfo banInfo;
		private List<SBean.RoleBrief> rolelist = new ArrayList<>();
		private List<Integer> rolesLoadFailedList = new ArrayList<>();
		private int errCode = -100;
	}
	
	
	public void roleLogin(final int sid, final SBean.UserLoginInfo loginInfo, final String username, final int roleid)
	{
		if (!gs.getRPCManager().checkSession(sid))//session断开直接返回
			return;
		if (userLock.putIfAbsent(username, 0) != null)
		{
			gs.getLogger().debug("session " + sid + " roleLogin user [" + username + "] select role(" + roleid + ") failed, other user session being login!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_LOCK_BUSY, -11, ""));
			return;
		}
		Integer srid = maps2r.putIfAbsent(sid, 0);
		if (srid != null)
		{
			userLock.remove(username);
			gs.getLogger().warn("session " + sid + " roleLogin user  [" + username + "] select role(" + roleid + ") failed, session already login role or being login!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ALREADY_LOGIN, -11, ""));
			return;
		}
		Integer usersid = mapu2s.get(username);
		if (usersid != null) 
		{
			maps2r.remove(sid);
			userLock.remove(username);
			gs.getLogger().warn("session " + sid + " roleLogin user  [" + username + "] select role(" + roleid + ") failed, other user session already login role!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_force_close(GameData.FORCE_CLOSE_REPLACE));
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ALREADY_LOGIN, -12, ""));
			return;
		}
		synchronized (roleLoadingLock) 
		{
			if (!roleLoadingSet.add(roleid))
			{
				maps2r.remove(sid);
				userLock.remove(username);
				gs.getLogger().debug("session " + sid + " roleLogin user [" + username + "] select role(" + roleid + ") failed, other user session being login!");
				gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_LOCK_BUSY, -12, ""));
				return;
			}
			gs.getDB().execute(new RoleLoginTrans(sid, username, roleid, (errorCode, banInfo, role, marriageShare, sect) -> {
                synchronized (roleLoadingLock)
                {
                    if (errorCode != 0)
                    {
                    	roleLoadingSet.remove(roleid);
                        roleLoadingLock.notifyAll();
                        maps2r.remove(sid);
                        userLock.remove(username);
                    	if (banInfo == null)
                    	{
                            gs.getLogger().warn("session " + sid + " roleLogin user  [" + username + "] select role(" + roleid + "), load role failed, error=" + errorCode);
                            gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_LOAD_ROLE_FAILED, errorCode, ""));
                    	}
                    	else
                    	{
                    		gs.getLogger().warn("session " + sid + " roleLogin user  [" + username + "] select role(" + roleid + "), but be banned, leftTime = " + banInfo.leftTime + "!");
                        	gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ROLE_BANNED, banInfo.leftTime, banInfo.reason));
                    	}
                        return;
                    }
                    try
                    {
                    	role.lock();
                    	if (mapRoles.put(roleid, role) == null)
                    	{
                    		onlineRoles.merge(role.getGameId(), 1, (ov, nv) -> ov + nv);
                    	}
                    	noRoleMaps2u.remove(sid);
                    	int state = maps2r.put(sid, role.id);
                        mapu2s.put(username, sid);
                        roleLoadingSet.remove(roleid);
                        roleLoadingLock.notifyAll();
                        userLock.remove(username);
                        if (marriageShare != null)
                        {
                        	synchronized (mapMarriageShare)
                            {
                            	MarriageShare realMarriageShare = mapMarriageShare.putIfAbsent(marriageShare.getId(), marriageShare);
                                if (realMarriageShare == null)
                                	realMarriageShare = marriageShare;
                                realMarriageShare.updateUseTime(GameTime.getTime());	
                            }	
                        }
                        gs.getSectManager().tryActiveSect(sect);
                        role.login(loginInfo, Role.ROLE_LOAD_FROM_DB);
                        if (state < 0 || !gs.getRPCManager().checkSession(sid))
                        	role.onDisconnect(sid);
                    }
                    finally
                    {
                    	role.unlock();
                    }
                }
            }), username.hashCode());
		}
	}
	
	interface RoleLoginTransCallback
	{
		void onCallback(int errorCode, BanInfo banInfo, Role role, MarriageShare warehouseShare, Sect sect);
	}
	public class RoleLoginTrans implements Transaction
	{
		public RoleLoginTrans(int sid, String username, int roleid, RoleLoginTransCallback callback)
		{
			this.sid = sid;
			this.username = username;
			this.roleid = roleid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBUser dbuser = user.get(username);
			if (dbuser == null)
			{
				errorCode = -1;//not found
				return false;
			}
			DBRoleShare dbroleshare = roleshare.get(username);
			if (dbroleshare == null)
			{
				errorCode = -2;//not found
				return false;
			}
			if (!dbuser.rolesID.contains(roleid))
			{
				errorCode = -3;//roleid not in user
				return false;
			}
			DBRole dbrole = role.get(roleid);
			if (dbrole == null)
			{
				errorCode = -4;//not found
				return false;
			}
			int now = GameTime.getTime();
			Ban ban = new Ban(dbuser.ban);
			if (ban.isBanLogin(now))
            {
				errorCode = -5;
				banInfo = ban.getBanLoginInfo(now);
				return false;
            }
			roledata = new Role(roleid, gs, sid).fromDBRole(new RoleShare(dbroleshare), dbrole, mail.get(roleid));
			roledata.updateBanInfo(new Ban(dbuser.ban));
			int sharedWarehouseId = roledata.getSharedWarehouseId();
			if (sharedWarehouseId > 0)
			{
				DBMarriageShare dbmarriageShare = marriageshare.get(sharedWarehouseId);
				if (dbmarriageShare != null)
				{
					int parterID = dbmarriageShare.manId == dbrole.id ? dbmarriageShare.ladyId : dbmarriageShare.manId;
					DBRole dbPartner = role.get(parterID);
					marriageShare = new MarriageShare(dbmarriageShare, dbrole.level, dbPartner == null ? 0 : dbPartner.level);
				}
			}
			List<SBean.FriendOverview> friends = new ArrayList<>();
			for (int fid: roledata.friend.getAllFriendsRoleID())
			{
				DBRole dbroleFriend = role.get(fid);
				if (dbroleFriend != null)
					friends.add(dbroleFriend.getFriendOverview());
			}
			roledata.friend.updateFriendsOverview(friends);
			
			List<SBean.RoleOverview> blackList = new ArrayList<>();
			for (int fid: roledata.friend.getBlackListIds())
			{
				DBRole dbroleFriend = role.get(fid);
				if (dbroleFriend != null)
					blackList.add(dbroleFriend.getRoleOverview());
			}
			roledata.friend.updateBlackListOverview(blackList);
			int sectId = roledata.getSectId();
			if (sectId > 0)
			{
				if (!gs.getSectManager().tryUpdateSectUseTime(sectId))
				{
					DBSect dbsect = sect.get(sectId);
					if (dbsect != null)
					{
						sectdata = new Sect(gs, dbsect.id).fromDB(dbsect);
					}
				}
			}
			errorCode = 0;
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("RoleLoginTrans (" + username + ", " + roleid + ")  return " + errorCode);
			callback.onCallback(errorCode, banInfo, roledata, marriageShare, sectdata);
		}
		
		@AutoInit
		public TableReadonly<String, DBUser> user;
		@AutoInit
		public TableReadonly<String, DBRoleShare> roleshare;
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		@AutoInit
		public TableReadonly<Integer, DBMailBox> mail;
		@AutoInit
		public TableReadonly<Integer, DBMarriageShare> marriageshare;
		@AutoInit
		public TableReadonly<Integer, DBSect> sect;
		
		private int sid;
		private String username;
		private int roleid;
		private RoleLoginTransCallback callback;
		private BanInfo banInfo;
		private Role roledata;
		private MarriageShare marriageShare;
		private Sect sectdata;
		public int errorCode = -100;
	}
	
	public void userCreateRole(final int sid, final SBean.UserLoginInfo loginInfo, final int zoneId, final String username, final SBean.CreateRoleParam createParam)
	{
		if (!gs.getRPCManager().checkSession(sid))//session断开直接返回
			return;
		if (userLock.putIfAbsent(username, 0) != null)
		{
			gs.getLogger().debug("session " + sid + " createRole user [" + username + "] create role(" + createParam.name + ", " + createParam.classType + ") failed, other session being login!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_LOCK_BUSY, -21, ""));
			return;
		}
		Integer srid = maps2r.putIfAbsent(sid, 0);
		if (srid != null)
		{
			userLock.remove(username);
			gs.getLogger().warn("session " + sid + " createRole user  [" + username + "] create role(" + createParam.name + ", " + createParam.classType + ") failed, session already login role or being login!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ALREADY_LOGIN, -21, ""));
			return;
		}
		Integer usersid = mapu2s.get(username);
		if (usersid != null) 
		{
			maps2r.remove(sid);
			userLock.remove(username);
			gs.getLogger().warn("session " + sid + " createRole user  [" + username + "] create role(" + createParam.name + ", " + createParam.classType + ") failed, other user session already login role!");
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_force_close(GameData.FORCE_CLOSE_REPLACE));
			gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ALREADY_LOGIN, -22, ""));
			return;
		}
		gs.getDB().execute(new UserCreateRoleTrans(sid, zoneId, username, createParam, this.getRegisterLimitList().getCurRegisterLimit(), (errCode, banInfo, role) -> {
            if (errCode != 0 || role == null)
            {
                maps2r.remove(sid);
                userLock.remove(username, 0);
                if (banInfo == null)
                {
                	gs.getLogger().warn("session " + sid + " createRole user  [" + username + "] create role(" + createParam.name + ", " + createParam.classType + ") failed, errCode=" + errCode);
                	gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(errCode == -2 ? GameData.USERLOGIN_CREATE_ROLE_NAME_USED : GameData.USERLOGIN_CREATE_ROLE_FAILED, errCode, ""));
                }
                else
                {
                	gs.getLogger().warn("session " + sid + " createRole user  [" + username + "] create role(" + createParam.name + ", " + createParam.classType + ") but be banned, leftTime = " + banInfo.leftTime + "!");
                	gs.getRPCManager().sendStrPacket(sid, new SBean.user_login_res(GameData.USERLOGIN_ROLE_BANNED, banInfo.leftTime, banInfo.reason));
                }
                return;
            }
            try
            {
            	role.lock();
            	if (mapRoles.put(role.id, role) == null)
            	{
            		onlineRoles.merge(role.getGameId(), 1, (ov, nv) -> ov + nv);
            	}
            	
            	
            	noRoleMaps2u.remove(sid);
            	int state = maps2r.put(sid, role.id);
                mapu2s.put(username, sid);	
                userLock.remove(username, 0);
                role.login(loginInfo, Role.ROLE_NEW);
                if (state < 0 || !gs.getRPCManager().checkSession(sid))
                	role.onDisconnect(sid);
            }
            finally
            {
            	role.unlock();
            }
        }), username.hashCode());
	}
	
	interface UserCreateRoleTransCallback
	{
		void onCallback(int errCode, BanInfo banInfo, Role role);
	}
	public class UserCreateRoleTrans implements Transaction
	{
		public UserCreateRoleTrans(int sid, int zoneId, String username, SBean.CreateRoleParam createParam, int curDayRegLimit, UserCreateRoleTransCallback callback)
		{
			this.sid = sid;
			this.zoneId = zoneId;
			this.username = username;
			this.createParam = createParam;
			this.curDayRegLimit = curDayRegLimit;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			int curDay = GameTime.getDay();
			Integer dayReglimitInt = dayreg.get(curDay);
			int dayReglimit = dayReglimitInt != null ? dayReglimitInt : 0;
			if (dayReglimit >= curDayRegLimit)
			{
				errorCode = -1;//reg limit 
				return false;
			}
			Integer ridInt = rolename.get(createParam.name);
			if (ridInt != null && ridInt != 0)
			{
				errorCode = -2;//role name already used
				return false;
			}
			DBUser dbuser = user.get(username);
			if (dbuser == null)
			{
				errorCode = -3;//dbuser not exist
				return false;
			}
			if (dbuser.rolesID.size() >= 4)
			{
				errorCode = -4;//role create max
				return false;
			}
			
			int now = GameTime.getTime();
			Ban ban = new Ban(dbuser.ban);
			if (ban.isBanLogin(now))
            {
				errorCode = -5;
				banInfo = ban.getBanLoginInfo(now);
				return false;
            }
			
			final String maxRoleIDKey = MaxRoleIDKey + "_" + zoneId;
			Integer maxid = maxids.get(maxRoleIDKey);
			int roleSeq = maxid == null ? 1 : maxid + 1;
			if (roleSeq >= GameData.getMaxGSRoleCount())
			{
				errorCode = -6;
				return false;
			}
			int roleid = GameData.createRoleId(zoneId, roleSeq);  
			dbuser.rolesID.add(roleid);
			
			DBRoleShare dbroleshare = roleshare.get(username);
			if (dbroleshare == null)
			{
				dbroleshare = new DBRoleShare().newCreate();
			}
			roleData = new Role(roleid, gs, sid).createRole(new RoleShare(dbroleshare), dbuser.register.id, createParam);
			roleData.updateBanInfo(new Ban(dbuser.ban));
			dayreg.put(curDay, dayReglimit+1);
			maxids.put(maxRoleIDKey, roleSeq);
			role.put(roleData.id, roleData.copyDBRoleWithoutLock());
			mail.put(roleData.id, roleData.getMailBox().toBDWithouLock());
			roleshare.put(username, roleData.copyDBRoleShareWithoutLock());
			user.put(username, dbuser);
			rolename.put(roleData.name, roleid);
			roleTotalCreate.getAndIncrement();
			errorCode = 0;
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().info("UserCreateRoleTrans (" + username + ", " + createParam.name + ") : errorCode=" + errorCode + (roleData != null ? (", create role id=" + roleData.id) : ", create failed!"));
			callback.onCallback(errorCode, banInfo, roleData);
		}
		
		@AutoInit
		public Table<String, DBUser> user;
		@AutoInit
		public Table<String, Integer> maxids;
		@AutoInit
		public Table<Integer, Integer> dayreg;
		@AutoInit
		public Table<String, DBRoleShare> roleshare;
		@AutoInit
		public Table<Integer, DBRole> role;
		@AutoInit
		public Table<String, Integer> rolename;
		@AutoInit
		public Table<Integer, DBMailBox> mail;
		
		private static final String MaxRoleIDKey = "roleid";
		
		private int sid;
		private int zoneId;
		private String username;
		private SBean.CreateRoleParam createParam;
		private int curDayRegLimit;
		private UserCreateRoleTransCallback callback;
		private BanInfo banInfo;
		private Role roleData;
		public int errorCode = -100;
	}
	
	interface QueryUerRoleListTransCallback
	{
		void onCallback(int errCode, List<SBean.RoleBrief> rolelist);
	}
	public class QueryUerRoleListTrans implements Transaction
	{
		public QueryUerRoleListTrans(String username, QueryUerRoleListTransCallback callback)
		{
			this.username = username;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBUser dbuser = user.get(username);
			if (dbuser == null)
			{
				errCode = -1;
				return false;
			}
			List<SBean.RoleBrief> rolelist = new ArrayList<>();
			for (int rid : dbuser.rolesID)
			{
				DBRole dbrole = role.get(rid);
				if (dbrole != null)
				{
					rolelist.add(dbrole.getRoleBrief());
				}
			}
			errCode = 0;
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("QueryUerRoleListTrans (" + username + ") : errCode="+errCode + ", roles size=" + rolelist.size());
			callback.onCallback(errCode, rolelist);
		}
		
		@AutoInit
		public Table<String, DBUser> user;
		@AutoInit
		public Table<Integer, DBRole> role;
		
		private String username;
		private QueryUerRoleListTransCallback callback;
		private List<SBean.RoleBrief> rolelist = new ArrayList<>();
		private int errCode = -100;
	}

	interface RoleRenameTransCallback
	{
		void onCallback(int errCode);
	}
	
	public class RoleRenameTrans implements Transaction
	{
		
		RoleRenameTrans(int rid, String oldName, String newName, RoleRenameTransCallback callback)
		{
			this.rid = rid;
			this.oldName = oldName;
			this.newName = newName;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			Integer ridInt = rolename.get(newName);
			if (ridInt != null && ridInt != 0)
			{
				errCode = -1;	//already used
				return false;
			}
			
			rolename.put(newName, rid);
			errCode = 0;	//success
			return true;
		}

		@Override
		public void onCallback(ErrorCode arg0)
		{
			gs.getLogger().info("role [" + rid + " " + oldName + "] rename " + newName + (errCode == 0 ? " success " : " failed"));
			callback.onCallback(errCode);
		}
		
		final private int rid;
		final private String oldName;
		final private String newName;
		final private RoleRenameTransCallback callback;
		private int errCode = -100;
		
		@AutoInit
		public Table<String, Integer> rolename;
	}
	
	public void roleRename(int rid, String oldName, String newName, RoleRenameTransCallback callback)
	{		
		gs.getDB().execute(new RoleRenameTrans(rid, oldName, newName, callback));
	}
	
//	public void forceKickRole(int rid)
//	{
//		Role role = mapRoles.get(rid);
//		if (role != null)
//		{
//			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.user_force_close(GameData.FORCE_CLOSE_KICK));
//		}
//	}
	
	UserLoginVerifier getLoginVerifier()
	{
		return loginVerifier;
	}
	
	LoginWhiteList getLoginWhiteList()
	{
		return loginWhiteList;
	}
	
	ChannelBlackList getChannelBlackList()
	{
		return channelBlackList;
	}
	
	RegisterLimitList getRegisterLimitList()
	{
		return registerLimitList;
	}
	
	AssertIgnoreList getAssertIgnoreList()
	{
		return assertIgnoreList;
	}
	
	LoginQueue getLoginQueue()
	{
		return loginQueue;
	}
	
	WorldMail getWorldMail()
	{
		return worldMail;
	}
	
	NewRoleSysMail getNewRoleSysMail()
	{
		return newRoleSysMail;
	}
	
	RollNotice getRollNotice()
	{
		return rollNotice;
	}

	MessageBoard getMessageBoard()
	{
		return messageBoard;
	}
	
	MarriageBespeak getMarriageBespeak()
	{
		return marriageBespeak;
	}
	
	public LoginManager(GameServer gs)
	{
		this.gs = gs;
		this.worldMail = new WorldMail(gs);
		this.newRoleSysMail = new NewRoleSysMail(gs);
		this.rollNotice = new RollNotice(gs);
		this.messageBoard = new MessageBoard(gs);
		this.marriageBespeak = new MarriageBespeak(gs);
	}
	
	public void start()
	{
		normalTaskExecutor = Executors.newSingleThreadExecutor();
		broadcastTaskExecutor = Executors.newSingleThreadExecutor();
		
		gsStat.start();
		loginVerifier.startup();
		loginWhiteList.setCfgFile(gs.getConfig().whitelistFileName);
		channelBlackList.setCfgFile(gs.getConfig().blacklistFileName);
		registerLimitList.setCfgFile(gs.getConfig().registerlimitFileName);
		assertIgnoreList.setCfgFile(gs.getConfig().assertIgnoreFileName);
		
		rollNotice.start(GameTime.getTime());
	}
	
	public void destroy()
	{
		try
		{
			normalTaskExecutor.shutdown();
			while (!normalTaskExecutor.awaitTermination(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
			gs.getLogger().warn("shutdown sect executor cause exception", e);
		}
		try
		{
			broadcastTaskExecutor.shutdown();
			while (!broadcastTaskExecutor.awaitTermination(1, TimeUnit.SECONDS));
		}
		catch (Exception e)
		{
			gs.getLogger().warn("shutdown sect executor cause exception", e);
		}
		loginVerifier.shutdown();
	}
	
	void addNormalTaskEvent(Runnable runnable)
	{
		if(this.normalTaskExecutor.isShutdown())
			return;
		
		this.normalTaskExecutor.execute(runnable);
	}
	
	void addBroadcastTaskEvent(Runnable runnable)
	{
		if(this.broadcastTaskExecutor.isShutdown())
			return;
		
		this.broadcastTaskExecutor.execute(runnable);
	}
	
	public void saveRoles()
	{
		int now = GameTime.getTime();
		for(Role role : mapRoles.values())
		{
			role.logout(now);
		}
	}
	
	public void onTimer(int timeTick)
	{
		//long timeNow = System.currentTimeMillis();
		loginVerifier.onTimer(timeTick);
		loginQueue.onTimer(timeTick);
		quiz.onTimer(timeTick);
		rolesOnTimer(timeTick);
		cacheOnTimer(timeTick);
		rollNoticeOnTimer(timeTick);
		sharedWarehouseOnTimer(timeTick);
		reportOnTimer(timeTick);
	}
	
	private void rolesOnTimer(int timeTick)
	{
		Iterator<Map.Entry<Integer, Role>> it = mapRoles.entrySet().iterator();
		while( it.hasNext() )
		{
			Role r = it.next().getValue();
			try 
			{
				r.lock();
				if (r.onTimer(timeTick))//和userlogin中替换登录role存在竞争关系，需要加在锁内
				{
					r.logout(timeTick);
					mapr2nCaches.put(r.id, new RoleId2UserNameCache(r.id, r.getUsername()));
					Integer sid = mapu2s.remove(r.getUsername());
					if (sid != null)
						maps2r.remove(sid);
					onlineRoles.merge(r.getGameId(), 0, (ov, nv) -> ov <= 0 ? 0 : ov - 1);
					
					it.remove();
					r.setClosed();	
				}
			}
			finally
			{
				r.unlock();
			}
		}
	}
	
	private void cacheOnTimer(int timeTick)
	{
		Iterator<Map.Entry<Integer, RoleId2UserNameCache>> it = mapr2nCaches.entrySet().iterator();
		while( it.hasNext() )
		{
			RoleId2UserNameCache cache = it.next().getValue();
			if (cache.isTimeout(timeTick))
			{
				it.remove();
			}
		}
	}
	
	private void rollNoticeOnTimer(int timeTick)
	{
		if (timeTick % 60 == 27)
			this.rollNotice.onTimer(timeTick);
	}
	
	private void sharedWarehouseOnTimer(int timeTick)
	{
		Iterator<Map.Entry<Integer, MarriageShare>> it = mapMarriageShare.entrySet().iterator();
		while (it.hasNext())
		{
			synchronized (mapMarriageShare)
			{
				MarriageShare warehouseShare = it.next().getValue();
				if (warehouseShare.onTimer(timeTick))
				{
					it.remove();
				}	
			}
		}
	}
	
	private void reportOnTimer(int timeTick)
	{
		int onlines = mapRoles.size();
		if (timeTick % 300 == 44)
		{
			gs.getTLogger().reportOnline(gs.getConfig().biGameAppId, onlines);
		}
		if (timeTick % 60 == 22)
		{
			for (Entry<String, Integer> gameId : this.onlineRoles.entrySet())
			{
				gs.getTLogger().logOnlineCount(gameId.getKey(), gameId.getValue());
			}
		}
		if (gs.getConfig().pOnlines != 0 && onlines > gs.getConfig().pOnlines)
		{
			if (timeTick % 10 == 7)
			{
				gs.getLogger().info("@@ ### gs server on line role count : " + onlines + ",  being enter user cout : " + noRoleMaps2u.size() + ", waiting queue session count : " +  this.getLoginQueue().size());
			}
		}
	}
	
	boolean roleLogout(int sid, Role role)
	{
		synchronized (roleLoadingLock) //确保其他线程不会在logout运行中间，状态不一致的时候，做数据库读和写操作(其他设备登录替换)
		{
			try
			{
				role.lock();//确保从mapRoles中取出来的role，在后面删除为无效时状态是一致的
				if (role.checkSessionID(sid))//role中的session可能在加role lock前被修改
				{
					role.logout(GameTime.getTime());
					noRoleMaps2u.put(sid, role.getUsername());
					if (mapRoles.remove(role.id) != null) 
					{
						onlineRoles.merge(role.getGameId(), 0, (ov, nv) -> ov <= 0 ? 0 : ov - 1);
					}
					maps2r.remove(sid);
					mapu2s.remove(role.getUsername());
					role.setClosed();
					return true;
				}
			}
			finally
			{
				role.unlock();
			}
		}
		return false;
	}
	
	public void sessionDisconnect(int sid)//此消息是此session在在消息处理线程中最后一个消息，只需要考虑前面消息响应后的在其他线程的异步执行影响
	{
		noRoleMaps2u.remove(sid);
		//如果session断开消息前是session登录的消息事件，则maps2r中rid应该为0，登录正处于db异步执行阶段，则修改session登录标志为-1，让登录异步回调线程执行完毕的时候再延迟执行session断开操作
		Integer rid = maps2r.compute(sid, (k, v)-> (v != null && v == 0) ? Integer.valueOf(-1) : v);
		//v为null时，(v != null && v == 0) ? -1 : v 这种写法会崩溃，而 !(v != null && v == 0) ? v : -1就不会崩溃，java万年老坑，语法规则和C#比就是弱鸡
		Role role = rid == null ? null : mapRoles.get(rid);
		if( role != null )
		{
			role.onDisconnect(sid);//不需要加role lock锁，因为不涉及到role移出容器并且，role.onDisconnect()是安全是，内部做了session校验，这段代码执行中session改变也没影响 
		}
		this.loginQueue.remove(sid);
	}
	
	public Role getUserRole(String username)
	{
		Integer sid = mapu2s.get(username);
		return sid == null ? null : getSessionRole(sid);
	}
	
	private Role getSessionRole(int sid)
	{
		Integer rid = maps2r.get(sid);
		return rid == null ? null : mapRoles.get(rid);
	}
	
	public Role getLoginRole(int sid)
	{
		Role role = getSessionRole(sid);
		if (role == null)
			return null;
		if( ! role.checkSessionID(sid) )
			return null;
		return role;
	}

	
	public Role getOnGameRole(int rid)
	{
		return rid > 0 ? mapRoles.get(rid) : mapRoleRobots.get(rid);
	}
	
//	interface RoleTask
//	{
//		void run(Role role);
//	}
//	public void doOnGameRoleTask(int rid, RoleTask task)
//	{
//		Role role = mapRoles.get(rid);
//		if (role != null)
//			this.getTaskExecutor().execute(new Runnable()
//			{
//				
//				@Override
//				public void run()
//				{
//					task.run(role);
//				}
//			});
//	}

	
	private void InvokeOnLineRoleFunction(Consumer<? super Role> action)
	{
		Iterator<Role> it = mapRoles.values().iterator();
		while( it.hasNext() )
		{
			Role role = it.next();
			action.accept(role);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void receiveWorldMsg(SBean.MessageInfo msginfo)
	{
		synchronized (worldMsgCache)
		{
			worldMsgCache.add(msginfo);
			if (worldMsgCache.size() > GameData.MAX_WORLD_MESSAGE_COUNT)
				worldMsgCache.subList(0, worldMsgCache.size() - GameData.MAX_WORLD_MESSAGE_COUNT).clear();	
		}
		this.addBroadcastTaskEvent(() ->
		{
			InvokeOnLineRoleFunction(role -> 
				{
					role.receiveMsg(msginfo);
				});
		});
	}
	
	public Collection<SBean.MessageInfo> getRecentWorldMsgs()
	{
		synchronized (worldMsgCache)
		{
			return new ArrayList<>(worldMsgCache);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public CDKeyGen getCDKeyGen()
	{
		return this.cdKeyGen;
	}
	
	public QuizGiftActivity getQuizActivity()
	{
		return this.quiz;
	}
	
	public SnatchRedEnvelopeActivity getSnatchRedEnvelopeActivity()
	{
		return this.redEnvelope;
	}
	
	public SpeedUpLevel getSpeedUp()
	{
		return this.speedUpLvl;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface  GetRoleIdByRoleNameCallback
	{
		void onCallback(String roleName, Integer roleId);
	}

	public class GetRoleIdByRoleNameTrans implements Transaction
	{
		public GetRoleIdByRoleNameTrans(String roleName, GetRoleIdByRoleNameCallback callback)
		{
			this.roleName = roleName;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			this.roleId = rolename.get(roleName);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get role id by name (" + roleName + ")" + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(roleName, roleId);
		}

		@AutoInit
		public TableReadonly<String, Integer> rolename;

		String roleName;
		Integer roleId;
		GetRoleIdByRoleNameCallback callback;
	}

	public void getRoleIdByRoleName(String roleName, GetRoleIdByRoleNameCallback callback)
	{
		gs.getDB().execute(new GetRoleIdByRoleNameTrans(roleName, callback));
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface  GetRolesByOpenIdCallback
	{
		void onCallback(Map<Integer, String> roles);
	}

	public class GetRolesByOpenIdTrans implements Transaction
	{
		public GetRolesByOpenIdTrans(String channel, String openId, GetRolesByOpenIdCallback callback)
		{
			this.channel = channel;
			this.openId = openId;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			for (int zoneId : gs.getConfig().zones)
			{
				String username = GameData.getUserName(zoneId, channel, openId);
				DBUser dbuser = user.get(username);
				if (dbuser != null)
				{
					for (int rid : dbuser.rolesID)
					{
						DBRole dbrole = role.get(rid);
						if (dbrole != null)
						{
							roles.put(rid, dbrole.name);
						}
					}
				}
			}
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get roles by openid (" + channel + "_" + openId + ")" + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(roles);
		}

		@AutoInit
		public TableReadonly<String, DBUser> user;
		@AutoInit
		public TableReadonly<Integer, DBRole> role;

		String channel;
		String openId;
		Map<Integer, String> roles = new TreeMap<>();
		GetRolesByOpenIdCallback callback;
	}

	public void getRolesByOpenId(String channel, String openId, GetRolesByOpenIdCallback callback)
	{
		gs.getDB().execute(new GetRolesByOpenIdTrans(channel, openId, callback));
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetRoleBriefCallback
	{
		void onCallback(SBean.RoleBrief brief, int teamID);
	}
	public class GetRoleBriefTrans implements Transaction
	{
		public GetRoleBriefTrans(int rid, GetRoleBriefCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole != null )
			{
				brief = dbrole.getRoleBrief();
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get role brief " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(brief, 0);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		int rid;
		SBean.RoleBrief brief;
		GetRoleBriefCallback callback;
	}
	
	//不能在role锁中调用
	void getRoleBrief(int rid, GetRoleBriefCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getRoleBrief(), role.getTeamId());
			return;
		}
		gs.getDB().execute(new GetRoleBriefTrans(rid, callback::onCallback));
	}

	public interface GetRoleOverviewCallback
	{
		void onCallback(SBean.RoleOverview overview);
	}
	public class GetRoleOverviewTrans implements Transaction
	{
		public GetRoleOverviewTrans(int rid, GetRoleOverviewCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole != null )
			{
				overview = dbrole.getRoleOverview();
			}
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get role overview " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(overview);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		int rid;
		SBean.RoleOverview overview;
		GetRoleOverviewCallback callback;
	}
	//不能在role锁中调用
	void getRoleOverview(int rid, GetRoleOverviewCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getRoleOverview());
			return;
		}
		gs.getDB().execute(new GetRoleOverviewTrans(rid, callback::onCallback));
	}
	
	
	public interface GetRoleFeatureCallback
	{
		void onCallback(SBean.RoleFeature feature);
	}
	public class GetRoleFeatureTrans implements Transaction
	{
		public GetRoleFeatureTrans(int rid, GetRoleFeatureCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole != null )
			{
				DBMarriageShare marriageData = marriageshare.get(dbrole.marriageData.marriageShareId);
				feature = dbrole.getRoleFeature(gs.getSectManager().getRoleSectAuras(dbrole.getSectBrief().sectID), marriageData == null ? 0 : marriageData.marriageLevel);
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get role brief " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(feature);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		@AutoInit
		public Table<Integer, DBMarriageShare> marriageshare;
		
		int rid;
		SBean.RoleFeature feature;
		GetRoleFeatureCallback callback;
	}
	
	//不能在role锁中调用
	void getRoleFeature(int rid, GetRoleFeatureCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getRoleFeature());
			return;
		}
		gs.getDB().execute(new GetRoleFeatureTrans(rid, callback::onCallback));
	}
	
	public interface GetRoleOverviewsCallback
	{
		void onCallback(Map<Integer, SBean.RoleOverview> roleOverviews);
	}
	public class GetRoleOverviewsTrans implements Transaction
	{
		public GetRoleOverviewsTrans(Collection<Integer> rids, GetRoleOverviewsCallback callback)
		{
			this.rids = rids;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			for (int rid : rids)
			{
				DBRole dbrole = role.get(rid);
				if( dbrole != null )
				{
					roleOverviews.put(rid, dbrole.getRoleOverview());
				}	
			}
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get roles overview " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(roleOverviews);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		Map<Integer, SBean.RoleOverview> roleOverviews = new TreeMap<>();
		Collection<Integer> rids;
		GetRoleOverviewsCallback callback;
	}
	
	//不能在role锁中调用
	void getRoleOverviews(Collection<Integer> rids, GetRoleOverviewsCallback callback)
	{
		Set<Integer> offlineRids = new TreeSet<>();
		Map<Integer, SBean.RoleOverview> overviews = new TreeMap<>();
		for (int rid : rids)
		{
			Role role = this.getOnGameRole(rid);
			if (role != null)
			{
				overviews.put(role.id, role.getRoleOverview());
			}
			else
			{
				offlineRids.add(rid);
			}
		}
		if (offlineRids.isEmpty())
		{
			callback.onCallback(overviews);
			return;
		}
		gs.getDB().execute(new GetRoleOverviewsTrans(offlineRids, roleOverviews -> {
            overviews.putAll(roleOverviews);
            callback.onCallback(overviews);
        }));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetFriendOverviewsCallback
	{
		void onCallback(List<SBean.FriendOverview> friendOverviews);
	}
	public class GetFriendOverviewsTrans implements Transaction
	{
		public GetFriendOverviewsTrans(Collection<Integer> fids, GetFriendOverviewsCallback callback)
		{
			this.fids = fids;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			for (int fid : fids)
			{
				DBRole dbrole = role.get(fid);
				if( dbrole != null )
				{
					friendOverviews.add(dbrole.getFriendOverview());
				}	
			}
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get friend overviews " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(friendOverviews);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		List<SBean.FriendOverview> friendOverviews = new ArrayList<>();
		Collection<Integer> fids;
		GetFriendOverviewsCallback callback;
	}
	
	//不能在role锁中调用
	void getFriendOverviews(Collection<Integer> fids, GetFriendOverviewsCallback callback)
	{
		Set<Integer> offlineFids = new TreeSet<>();
		List<SBean.FriendOverview> overviews = new ArrayList<>();
		for (int fid : fids)
		{
			Role role = this.getOnGameRole(fid);
			if (role != null)
			{
				overviews.add(role.getFriendOverview());
			}
			else
			{
				offlineFids.add(fid);
			}
		}
		if (offlineFids.isEmpty())
		{
			callback.onCallback(overviews);
			return;
		}
		gs.getDB().execute(new GetFriendOverviewsTrans(offlineFids, friendOverviews -> {
            overviews.addAll(friendOverviews);
            callback.onCallback(overviews);
        }));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetPetOverviewsCallback
	{
		void onCallback(List<SBean.PetOverview> pets);
	}
	
	public class GetPetOverviewsTrans implements Transaction
	{
		public GetPetOverviewsTrans(int rid, GetPetOverviewsCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole == null )
				return false;
			
			petOverviews = dbrole.getPetOverviews();
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get pets overview " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(petOverviews);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		List<SBean.PetOverview> petOverviews = new ArrayList<>();
		int rid;
		GetPetOverviewsCallback callback;
	}
	
	//不能在role锁中调用
	void getPetOverviews(int qRid, GetPetOverviewsCallback callback)
	{
		Role role = this.getOnGameRole(qRid);
		if (role != null)
		{
			callback.onCallback(role.getPetOverviews());
			return;
		}
		
		gs.getDB().execute(new GetPetOverviewsTrans(qRid, callback::onCallback));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetWeaponOverviewsCallback
	{
		void onCallback(List<SBean.WeaponOverview> weapons);
	}
	
	public class GetWeaponOverviewsTrans implements Transaction
	{
		public GetWeaponOverviewsTrans(int rid, GetWeaponOverviewsCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole == null )
				return false;
			
			weaponOverviews = dbrole.getWeaponOverviews();
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get weapos overview " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(weaponOverviews);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		List<SBean.WeaponOverview> weaponOverviews = new ArrayList<>();
		int rid;
		GetWeaponOverviewsCallback callback;
	}
	
	//不能在role锁中调用
	void getWeaponOverviews(int qRid, GetWeaponOverviewsCallback callback)
	{
		Role role = this.getOnGameRole(qRid);
		if (role != null)
		{
			callback.onCallback(role.getWeaponOverviews());
			return;
		}
		
		gs.getDB().execute(new GetWeaponOverviewsTrans(qRid, callback::onCallback));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//个人竞技场
	public interface GetBattleArrayOverviewCallback
	{
		void onCallback(SBean.BattleEnemyOverview overview, byte hideDefence);
	}
	public class GetArenaDefenceBattleArrayOverviewTrans implements Transaction
	{
		public GetArenaDefenceBattleArrayOverviewTrans(int rid, GetBattleArrayOverviewCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole != null )
			{
				SBean.RoleOverview roleOverview = dbrole.getRoleOverview();
				List<SBean.PetOverview> petsOverview = new ArrayList<>();
				for (int pid : dbrole.roleArenaData.normal.defencePets)
				{
					SBean.PetOverview pet = dbrole.getPetOverview(pid);
					if (pet != null)
					{
						petsOverview.add(pet);
						if(dbrole.roleArenaData.normal.hideDefence == 1)
							pet.id = 0;
					}
				}
				overview = new SBean.BattleEnemyOverview(new SBean.RoleSocial(roleOverview, dbrole.sectData.data.sectBrief.sectID, dbrole.sectData.data.sectBrief.sectName, dbrole.friendData.personalMsg), petsOverview, dbrole.roleArenaData.normal.hideDefence);
				hideDefence = dbrole.roleArenaData.normal.hideDefence;
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get arena defence battle overview " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(overview, hideDefence);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		SBean.BattleEnemyOverview overview;
		byte hideDefence = 0;
		int rid;
		GetBattleArrayOverviewCallback callback;
	}
	
	//不能在role锁中调用
	void getRoleArenaDefenceBattleArrayOverview(int rid, GetBattleArrayOverviewCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getArenaEnemyBattaleArrayOverview(), role.getArenaDefenceHide());
			return;
		}
		gs.getDB().execute(new GetArenaDefenceBattleArrayOverviewTrans(rid, callback::onCallback));
	}
	
	//不能在role锁中调用
	void getRoleArenaDefenceBattleArrayOverview(int rid, int rank, GetBattleArrayOverviewCallback callback)
	{
		if (rid < 0)
		{
			SBean.BattleEnemyOverview battleArrayOverview = GameData.getInstance().getArenaRobotBattleArrayOverview(rank, rid);
			callback.onCallback(battleArrayOverview, (byte)0);
			return;
		}
		this.getRoleArenaDefenceBattleArrayOverview(rid, callback);
	}
	
	public interface GetBattleArrayOverviewsCallback
	{
		void onCallback(Map<Integer, SBean.BattleEnemyOverview> overviews);
	}
	
	//个人竞技场
	public class GetArenaDefenceBattleArrayOverviewsTrans implements Transaction
	{
		public GetArenaDefenceBattleArrayOverviewsTrans(Collection<Integer> rids, GetBattleArrayOverviewsCallback callback)
		{
			this.rids = rids;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			for (int rid : rids)
			{
				DBRole dbrole = role.get(rid);
				if( dbrole != null )
				{
					SBean.RoleOverview roleOverview = dbrole.getRoleOverview();
					List<SBean.PetOverview> petsOverview = new ArrayList<>();
					for (int pid : dbrole.roleArenaData.normal.defencePets)
					{
						SBean.PetOverview pet = dbrole.getPetOverview(pid);
						if (pet != null)
						{
							petsOverview.add(pet);
							if(dbrole.roleArenaData.normal.hideDefence == 1)
								pet.id = 0;
						}
					}
					overviews.put(rid, new SBean.BattleEnemyOverview(new SBean.RoleSocial(roleOverview, dbrole.sectData.data.sectBrief.sectID, dbrole.sectData.data.sectBrief.sectName, dbrole.friendData.personalMsg), petsOverview, dbrole.roleArenaData.normal.hideDefence));
				}	
			}
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get arena defence battle overviews " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(overviews);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		Map<Integer, SBean.BattleEnemyOverview> overviews = new HashMap<>();
		Collection<Integer> rids;
		GetBattleArrayOverviewsCallback callback;
	}
	
	public interface GetBWArenaBattleArrayOverviewsCallback
	{
		void onCallback(Map<Integer, SBean.BattleArrayOverview> overviews, Map<Integer, Integer> bwarenalvls);
	}
	//正邪道场
	public class GetBWArenaBattleArrayOverviewsTrans implements Transaction
	{
		public GetBWArenaBattleArrayOverviewsTrans(Collection<Integer> rids, GetBWArenaBattleArrayOverviewsCallback callback)
		{
			this.rids = rids;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			for (int rid : rids)
			{
				DBRole dbrole = role.get(rid);
				if( dbrole != null )
				{
					SBean.RoleOverview roleOverview = dbrole.getRoleOverview();
					Map<Integer, SBean.PetOverview> petsOverview = new TreeMap<>();
					for (int pid : dbrole.roleArenaData.bwarena.pets)
					{
						SBean.PetOverview pet = dbrole.getPetOverview(pid);
						if (pet != null)
						{
							petsOverview.put(pet.id, pet);
						}
					}
					overviews.put(rid, new SBean.BattleArrayOverview(new SBean.RoleSocial(roleOverview, dbrole.sectData.data.sectBrief.sectID, dbrole.sectData.data.sectBrief.sectName, dbrole.friendData.personalMsg), petsOverview));
					bwarenalvls.put(rid, dbrole.roleArenaData.bwarena.lvl);
				}	
			}
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get bw arena battle overview " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(overviews, bwarenalvls);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		Map<Integer, SBean.BattleArrayOverview> overviews = new TreeMap<>();
		Map<Integer, Integer> bwarenalvls = new HashMap<>();
		Collection<Integer> rids;
		GetBWArenaBattleArrayOverviewsCallback callback;
	}
	
	//不能在role锁中调用
	void getArenaBattleArrayOverviews(Collection<Integer> rids, GetBattleArrayOverviewsCallback callback)
	{
		Set<Integer> offlineRids = new TreeSet<>();
		Map<Integer, SBean.BattleEnemyOverview> overviews = new TreeMap<>();
		for (int rid : rids)
		{
			Role role = this.getOnGameRole(rid);
			if (role != null)
			{
				overviews.put(role.id, role.getArenaEnemyBattaleArrayOverview());
			}
			else
			{
				offlineRids.add(rid);
			}
		}
		if (offlineRids.isEmpty())
		{
			callback.onCallback(overviews);
			return;
		}
		gs.getDB().execute(new GetArenaDefenceBattleArrayOverviewsTrans(offlineRids, offlineOverviews -> {
            overviews.putAll(offlineOverviews);
            callback.onCallback(overviews);
        }));
	}
	
	//不能在role锁中调用
	void getBWArenaBattleArrayOverviews(Collection<Integer> rids, GetBWArenaBattleArrayOverviewsCallback callback)
	{
		Set<Integer> offlineRids = new TreeSet<>();
		Map<Integer, SBean.BattleArrayOverview> overviews = new TreeMap<>();
		Map<Integer, Integer> bwarenalvls = new HashMap<>();
		for (int rid : rids)
		{
			Role role = this.getOnGameRole(rid);
			if (role != null)
			{
				overviews.put(role.id, role.getBWArenaMapBattaleArrayOverview());
				bwarenalvls.put(role.id, role.arenaInfo.roleArenaData.bwarena.lvl);
			}
			else
			{
				offlineRids.add(rid);
			}
		}
		if (offlineRids.isEmpty())
		{
			callback.onCallback(overviews, bwarenalvls);
			return;
		}
		gs.getDB().execute(new GetBWArenaBattleArrayOverviewsTrans(offlineRids, (offlineOverviews, offlineBWArenalvs) -> {
            overviews.putAll(offlineOverviews);
            bwarenalvls.putAll(offlineBWArenalvs);
            callback.onCallback(overviews, bwarenalvls);
        }));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetRoleArenaDefenceBattleArrayCallback
	{
		void onCallback(SBean.BattleArray ba);
	}
	public class GetRoleArenaDefenceBattleArrayTrans implements Transaction
	{
		public GetRoleArenaDefenceBattleArrayTrans(int rid, GetRoleArenaDefenceBattleArrayCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole != null )
			{
				DBMarriageShare marriageData = marriageshare.get(dbrole.marriageData.marriageShareId);
				SBean.FightRole fightRole = dbrole.getFightRole(gs.getSectManager().getRoleSectAuras(dbrole.getSectBrief().sectID),
																dbrole.getSectBrief(),
																marriageData == null ? 0 : marriageData.marriageLevel);
				Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
				for (int pid : dbrole.roleArenaData.normal.defencePets)
				{
					SBean.FightPet pet = dbrole.getFightPet(pid);
					if (pet != null)
						fightPets.put(pet.id, pet);
				}
				battleArray = new SBean.BattleArray(fightRole, fightPets, new ArrayList<>(dbrole.roleArenaData.normal.defencePets));
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get role arena defence data " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(battleArray);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		@AutoInit
		public Table<Integer, DBMarriageShare> marriageshare;
		
		SBean.BattleArray battleArray;
		int rid;
		GetRoleArenaDefenceBattleArrayCallback callback;
	}
	
	//不能在role锁中调用
	void getRoleArenaDefenceBattleArray(int rid, GetRoleArenaDefenceBattleArrayCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getArenaMapDefenceBattleArray());
			return;
		}
		gs.getDB().execute(new GetRoleArenaDefenceBattleArrayTrans(rid, callback::onCallback));
	}
	
	//不能在role锁中调用
	void getRoleArenaDefenceBattleArray(int rid, int rank, GetRoleArenaDefenceBattleArrayCallback callback)
	{
		if (rid < 0)
		{
			SBean.BattleArray battleArray = GameData.getInstance().getArenaRobotBattleArray(rank, rid);
			callback.onCallback(battleArray);
			return;
		}
		this.getRoleArenaDefenceBattleArray(rid, callback);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	public interface GetRoleBWArenaBattleArrayCallback
	{
		void onCallback(SBean.BattleArray ba, int bwarenaLvl);
	}
	public class GetRoleBWArenaBattleArrayTrans implements Transaction
	{
		public GetRoleBWArenaBattleArrayTrans(int rid, GetRoleBWArenaBattleArrayCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole != null )
			{
				DBMarriageShare marriageData = marriageshare.get(dbrole.marriageData.marriageShareId);
				SBean.FightRole fightRole = dbrole.getFightRole(gs.getSectManager().getRoleSectAuras(dbrole.getSectBrief().sectID), 
																dbrole.getSectBrief(),
																marriageData == null ? 0 : marriageData.marriageLevel);
				
				bwarenaLvl = dbrole.roleArenaData.bwarena.lvl;
				Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
				for (int pid : dbrole.roleArenaData.bwarena.pets)
				{
					SBean.FightPet pet = dbrole.getFightPet(pid);
					if (pet != null)
						fightPets.put(pet.id, pet);
				}
				battleArray = new SBean.BattleArray(fightRole, fightPets, new ArrayList<>());
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().debug("get role bw arena defence data " + (errcode == ErrorCode.eOK  ? "ok" : " error " + errcode));
			callback.onCallback(battleArray, bwarenaLvl);
		}
		
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		@AutoInit
		public Table<Integer, DBMarriageShare> marriageshare;
		
		SBean.BattleArray battleArray;
		int bwarenaLvl;
		int rid;
		GetRoleBWArenaBattleArrayCallback callback;
	}
	
	//不能在role锁中调用
	void getRoleBWArenaBattleArray(int rid, GetRoleBWArenaBattleArrayCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getBWArenaMapBattleAray(), role.arenaInfo.roleArenaData.bwarena.lvl);
			return;
		}
		gs.getDB().execute(new GetRoleBWArenaBattleArrayTrans(rid, callback::onCallback));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
//	public interface GetClanOreHarryEnemyFightCallback
//	{
//		void onCallback(SBean.BattleArray ba);
//	}
//	public class GetClanOreHarryEnemyFightTrans implements Transaction
//	{
//
//
//		public GetClanOreHarryEnemyFightTrans(int roleID, int clanId, int oreType, GetClanOreHarryEnemyFightCallback callback)
//		{
//			this.roleID = roleID;
//			this.clanId = clanId;
//			this.oreType = oreType;
//			this.callback = callback;
//		}
//		
//		@Override
//		public boolean doTransaction()
//		{
//			DBRole dbrole = role.get(roleID);
//			if( dbrole != null )
//			{
//				SBean.FightRole fightRole = dbrole.getFightRole(gs.getSectManager().getRoleSectAuras(dbrole.id), 
//																gs.getSectManager().getRoleSectBrief(dbrole.id));
//				Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
//				SBean.DBOreRobTeam oreRobTeam = null;
//				for(SBean.DBOreRobTeam ore : dbrole.clanData.occupyOres)
//				{
//					if(ore.clanId == clanId && ore.oreType == oreType)
//					{
//						oreRobTeam = ore;
//						break;
//					}
//				}
//				if(oreRobTeam == null)
//				{
//					return true;
//				}
//				
//				for(int petId : oreRobTeam.pets)
//				{
//					if(!dbrole.activePets.containsKey(petId))
//					{
//						return true;
//					}
//					SBean.FightPet pet = dbrole.getFightPet(petId);
//					fightPets.put(pet.id, pet);
//				}
//		
//				battleArray = new SBean.BattleArray(fightRole, fightPets);
//			}
//		
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(battleArray);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		
//		public final Integer roleID;
//		public final Integer clanId;
//		public final Integer oreType;
//		SBean.BattleArray battleArray;
//		public final GetClanOreHarryEnemyFightCallback callback;
//	}
//	public void getClanOreHarryEnemyFight(int rid, int clanId, int oreType, GetClanOreHarryEnemyFightCallback callback)
//	{
//		Role role = this.getOnGameRole(rid);
//		if(role != null)
//		{
//			SBean.FightRole fightRole = role.getMapFightRoleWithoutLock();
//			Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
//			SBean.DBOreRobTeam oreRobTeam = role.getOreRobTeam(clanId, oreType);
//			
//			SBean.BattleArray battleArray = null;
//			if(oreRobTeam != null)
//			{
//				for(int petId : oreRobTeam.pets)
//				{
//					if(!role.activePets.containsKey(petId))
//					{
//						callback.onCallback(null);
//						return;
//					}
//					SBean.FightPet pet = role.getMapFightPetWithoutLock(petId);
//					fightPets.put(pet.id, pet);
//				}
//				battleArray = new SBean.BattleArray(fightRole, fightPets);
//			}
//			
//			callback.onCallback(battleArray);
//		}else{
//			gs.getDB().execute(new GetClanOreHarryEnemyFightTrans(rid, clanId, oreType, callback::onCallback));
//		}
//		
//	}
//	
//	
//	//
//	public interface GetClanBattleBattleArrayCallback
//	{
//		void onCallback(SBean.BattleArray ba);
//	}
//	public class GetClanBattleBattleArrayTrans implements Transaction
//	{
//		public GetClanBattleBattleArrayTrans(int roleID, int type, GetClanBattleBattleArrayCallback callback)
//		{
//			this.roleID = roleID;
//			this.type = type;
//			this.callback = callback;
//		}
//		@Override
//		public boolean doTransaction()
//		{
//			DBRole dbrole = role.get(roleID);
//			if( dbrole != null )
//			{
//				SBean.FightRole fightRole = dbrole.getFightRole(gs.getSectManager().getRoleSectAuras(dbrole.id), 
//																gs.getSectManager().getRoleSectBrief(dbrole.id),
//																dbrole.clanData.attriAddition);
//				Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
//				 List<Integer> petList = new ArrayList<>();
//				 if(type == 1) 
//					 petList.addAll(dbrole.clanData.battle.attackPet.values());
//				 else
//					 petList.addAll(dbrole.clanData.battle.defendPet.values());				
//	
//				for(int petId : petList)
//				{
//					SBean.FightPet pet = dbrole.getFightPet(petId);
//					fightPets.put(pet.id, pet);
//				}
//		
//				battleArray = new SBean.BattleArray(fightRole, fightPets);
//			}
//		
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(battleArray);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		
//		public final Integer roleID;
//		public final Integer type;
//		SBean.BattleArray battleArray;
//		public final GetClanBattleBattleArrayCallback callback;
//	}
//	public void getClanBattleBattleArray(int rid, int type, GetClanBattleBattleArrayCallback callback)
//	{
//		gs.getDB().execute(new GetClanBattleBattleArrayTrans(rid, type, callback::onCallback));
//	}
//	
//	
//	//
//	public interface GetClanBattleEnemyCallback
//	{
//		void onCallback(List<SBean.PetOverview> enemyPets, SBean.RoleOverview overview);
//	}
//	public class GetClanBattleEnemyTrans implements Transaction
//	{
//		public GetClanBattleEnemyTrans(int roleID, GetClanBattleEnemyCallback callback)
//		{
//			this.roleID = roleID;
//			this.callback = callback;
//		}
//		@Override
//		public boolean doTransaction()
//		{
//			DBRole dbrole = role.get(roleID);
//			if( dbrole != null )
//			{
//				enemyPets = new ArrayList<>();
//				overview = dbrole.getRoleOverview();
//				for(int petId : dbrole.clanData.battle.defendPet.values())
//				{
//					SBean.DBPet pet = dbrole.activePets.get(petId);
//					if(pet == null)
//						return true;
//					enemyPets.add(dbrole.getPetOverview(petId));
//				}
//			}
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(enemyPets, overview);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		
//		public final Integer roleID;
//		List<SBean.PetOverview> enemyPets;
//		SBean.RoleOverview overview;
//		public final GetClanBattleEnemyCallback callback;
//	}
//	public void getClanBattleEnemy(int rid, GetClanBattleEnemyCallback callback)
//	{
//		Role role = this.getOnGameRole(rid);
//		if(role != null)
//		{
//			List<SBean.PetOverview> enemyPets = new ArrayList<>();
//			SBean.RoleOverview overview = null;
//			synchronized(role)
//			{
//				for(int petId : role.clanData.battle.defendPet.values())
//				{
//					if(!role.activePets.containsKey(petId))
//					{
//						callback.onCallback(null, null);
//						return;
//					}
//					enemyPets.add(role.getPetOverviewWithoutLock(petId));
//				}
//				overview = role.getRoleOverview();
//			}
//			callback.onCallback(enemyPets, overview);
//		}else{
//			gs.getDB().execute(new GetClanBattleEnemyTrans(rid, callback::onCallback));
//		}
//	}
//
//	
//	
//	//
//	public interface GetOnLoanPetCallback
//	{
//		void onCallback(SBean.FightPet ownerFightPet, SBean.PetHost ownerFightPetHost);
//	}
//	public class GetOnLoanPetTrans implements Transaction
//	{
//
//		public GetOnLoanPetTrans(int roleID, int petId, GetOnLoanPetCallback callback)
//		{
//			this.roleID = roleID;
//			this.petId = petId;
//			this.callback = callback;
//		}
//		
//		@Override
//		public boolean doTransaction()
//		{
//			DBRole dbrole = role.get(roleID);
//			if( dbrole != null )
//			{
//				ownerFightPetHost = dbrole.getPetHost(gs.getSectManager().getRoleSectAuras(dbrole.id), new SBean.ClanOwnerAttriAddition());
//				ownerFightPet = dbrole.getFightPet(petId);
//			}
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(ownerFightPet, ownerFightPetHost);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		
//		public final Integer roleID;
//		public final Integer petId;
//		SBean.FightPet ownerFightPet;
//		SBean.PetHost ownerFightPetHost;		
//		public final GetOnLoanPetCallback callback;
//	
//	}
//	public void getOnLoanPet(int rid, int petId, GetOnLoanPetCallback callback)
//	{
//		Role role = this.getOnGameRole(rid);
//		if(role != null)
//		{
//			callback.onCallback(role.getMapFightPetWithoutLock(petId), role.getMapPetHostWithoutLock());
//			return;
//		}
//		gs.getDB().execute(new GetOnLoanPetTrans(rid, petId, callback::onCallback));
//	}
//	
//	//
//	public interface GetClanTaskFightArrayCallback
//	{
//		void onCallback(SBean.BattleArray ba);
//	}
//	public class GetClanTaskFightArrayTrans implements Transaction
//	{
//		public GetClanTaskFightArrayTrans(int roleID, List<Integer> pets, GetClanTaskFightArrayCallback callback)
//		{
//			this.roleID = roleID;
//			this.pets = pets;
//			this.callback = callback;
//		}
//		@Override
//		public boolean doTransaction()
//		{
//			DBRole dbrole = role.get(roleID);
//			if( dbrole != null )
//			{
//				SBean.FightRole fightRole = dbrole.getFightRole(gs.getSectManager().getRoleSectAuras(dbrole.id), 
//																gs.getSectManager().getRoleSectBrief(dbrole.id),
//																dbrole.clanData.attriAddition);
//				Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
//				List<Integer> petList = new ArrayList<>();
//				petList.addAll(pets);			
//	
//				for(int petId : pets)
//				{
//					if(!dbrole.activePets.containsKey(petId))
//					{
//						return true;
//					}
//					SBean.FightPet pet = dbrole.getFightPet(petId);
//					fightPets.put(pet.id, pet);
//				}
//		
//				battleArray = new SBean.BattleArray(fightRole, fightPets);
//			}
//		
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(battleArray);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		
//		public final Integer roleID;
//		SBean.BattleArray battleArray;
//		List<Integer> pets;
//		public final GetClanTaskFightArrayCallback callback;
//	}
//	public void getClanTaskFightArray(int rid, List<Integer> pets, GetClanTaskFightArrayCallback callback)
//	{
//		gs.getDB().execute(new GetClanTaskFightArrayTrans(rid, pets, callback::onCallback));
//	}
//
//	
//	
//	
//	//
//	public interface GetRoleClanOreCallback
//	{
//		void onCallback(SBean.DBOreRobTeamGlobal oreInfo);
//	}
//	public class GetRoleClanOreTrans implements Transaction
//	{
//		public GetRoleClanOreTrans(int roleID, GetRoleClanOreCallback callback)
//		{
//			this.roleID = roleID;
//			this.callback = callback;
//		}
//		
//		@Override
//		public boolean doTransaction()
//		{
//			DBRole dbrole = role.get(roleID);
//			if( dbrole != null )
//			{
//				if(!dbrole.clanData.occupyOres.isEmpty())
//				{
//					int index = GameRandom.getRandInt(0, dbrole.clanData.occupyOres.size());
//					SBean.DBOreRobTeam oreTeam = dbrole.clanData.occupyOres.get(index);
//					List<SBean.PetOverview> petList = new ArrayList<>();
//					for (int petId : oreTeam.pets)
//					{
//						petList.add(dbrole.getPetOverview(petId));
//					}
//					oreInfo = new SBean.DBOreRobTeamGlobal(oreTeam.oreType, dbrole.id, 0, oreTeam.clanId,
//							  							   dbrole.name, oreTeam.startTime, dbrole.transformLevel, dbrole.totalPower, dbrole.level, petList, dbrole.headIcon);
//				}
//			}
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(oreInfo);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		
//		public final Integer roleID;
//		SBean.DBOreRobTeamGlobal oreInfo;
//		public final GetRoleClanOreCallback callback;
//	}
//	void getRoleClanOre(int rid, GetRoleClanOreCallback callback)
//	{
//		gs.getDB().execute(new GetRoleClanOreTrans(rid, callback::onCallback));
//	}
//	
//	//
//	public interface GetRoleClanDataCallback
//	{
//		void onCallback(SBean.DBRoleClanData clanData);
//	}
//	public class GetRoleClanDataTrans implements Transaction
//	{
//		public GetRoleClanDataTrans(int roleID, GetRoleClanDataCallback callback)
//		{
//			this.roleID = roleID;
//			this.callback = callback;
//		}
//		
//		@Override
//		public boolean doTransaction()
//		{
//			DBRole dbrole = role.get(roleID);
//			if( dbrole != null )
//			{
//				clanData = dbrole.clanData;
//			}
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(clanData);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		
//		public final Integer roleID;
//		SBean.DBRoleClanData clanData;
//		public final GetRoleClanDataCallback callback;
//	}
//	void getRoleClanData(int rid, GetRoleClanDataCallback callback)
//	{
//		gs.getDB().execute(new GetRoleClanDataTrans(rid, callback::onCallback));
//	}
//
//	//
	
	
	
	public interface GetRoleAndPetOverviewCallback
	{
		void onCallback(SBean.RoleOverview overview, List<SBean.PetOverview> pets);
	}
	public class GetRoleAndPetOverviewTrans implements Transaction
	{
		public GetRoleAndPetOverviewTrans(int roleID, GetRoleAndPetOverviewCallback callback)
		{
			this.roleID = roleID;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(roleID);
			if( dbrole != null )
			{
				overview = dbrole.getRoleOverview();
				for (SBean.DBPet pet : dbrole.activePets.values())
				{
					pets.add(DBRole.getPetOverview(pet));
				}
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			callback.onCallback(overview, pets);
		}
	
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		public final Integer roleID;
		public SBean.RoleOverview overview;
		public List<SBean.PetOverview> pets = new ArrayList<>();
		public final GetRoleAndPetOverviewCallback callback;
	}
	void getRoleAndPetOverview(int rid, GetRoleAndPetOverviewCallback callback)
	{
		gs.getDB().execute(new GetRoleAndPetOverviewTrans(rid, callback::onCallback));
	}
	
	
	
	//
	public interface GetRoleAndPetOverviewsCallback
	{
		void onCallback(SBean.RoleOverview overview, List<SBean.PetOverview> pets);
	}
	public class GetRoleAndPetOverviewsTrans implements Transaction
	{
		public GetRoleAndPetOverviewsTrans(int roleID, List<Integer> pets, GetRoleAndPetOverviewsCallback callback)
		{
			this.roleID = roleID;
			this.pets = pets;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(roleID);
			if( dbrole != null )
			{
				overview = dbrole.getRoleOverview();
				for (int petId : pets)
				{
					SBean.DBPet pet = dbrole.activePets.get(petId);
					if(pet != null)
						overviews.add(DBRole.getPetOverview(pet));
				}
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			callback.onCallback(overview, overviews);
		}
	
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		public final Integer roleID;
		public final List<Integer> pets;
		public SBean.RoleOverview overview;
		public List<SBean.PetOverview> overviews = new ArrayList<>();
		public final GetRoleAndPetOverviewsCallback callback;
	}
	void getRoleAndPetOverviews(int rid, List<Integer> pets, GetRoleAndPetOverviewsCallback callback)
	{
		gs.getDB().execute(new GetRoleAndPetOverviewsTrans(rid, pets, callback::onCallback));
	}

	//
//	public interface GetAcceptFlowerCallback
//	{
//		void onCallback(List<SBean.FlowerOverview> result);
//	}
//	public class GetAcceptFlowerTrans implements Transaction
//	{
//		public GetAcceptFlowerTrans(int roleID, GetAcceptFlowerCallback callback)
//		{
//			this.roleID = roleID;
//			this.callback = callback;
//		}
//		
//		@Override
//		public boolean doTransaction()
//		{
//			Map<Integer, Integer> acceptFlower = new TreeMap<Integer, Integer>();
//			Role r = this.getOnGameRole(roleID);
//			if(r == null)
//			{
//				DBRole dbrole = role.get(roleID);
//				if( dbrole != null )
//				{
//					acceptFlower.putAll(dbrole.friendData.acceptFlower);
//				}
//			}else
//			{
//				acceptFlower.putAll(r.friendData.acceptFlower);
//			}
//
//			for(Map.Entry<Integer, Integer> entry : acceptFlower.entrySet())
//			{
//				DBRole dbrole1 = role.get(entry.getKey());
//				if( dbrole1 != null )
//				{
//					DBRoleShare dbroleshare1 = roleshare.get(dbrole1.getUsername());
//					SBean.FlowerOverview flower = dbrole1.getRoleFlowerOverview(dbroleshare1.vipPay.vipLvl, entry.getValue());
//					flower.contribution = entry.getValue();
//					offlinegAcceptFlowers.add(flower);
//				}
//			}
//			
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(offlinegAcceptFlowers);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		@AutoInit
//		public TableReadonly<String, DBRoleShare> roleshare;
//		
//		public final Integer roleID;
//		public List<SBean.FlowerOverview> offlinegAcceptFlowers = new ArrayList<SBean.FlowerOverview>();
//		public final GetAcceptFlowerCallback callback;
//	}
//	
//	
//	//不能在role锁中调用
//	void getAcceptFlower(int rid, GetAcceptFlowerCallback callback)
//	{
//		Role role = this.getOnGameRole(rid);
//		if (role != null)
//		{
//			
//			return;
//		}
//		
//		gs.getDB().execute(new GetAcceptFlowerTrans(rid, callback::onCallback));
//	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetAcceptFlowerLogCallback
	{
		void onCallback(List<SBean.FlowerLog> log);
	}
	public class GetAcceptFlowerLogTrans implements Transaction
	{
		public GetAcceptFlowerLogTrans(int roleID, GetAcceptFlowerLogCallback callback)
		{
			this.roleID = roleID;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbRole = role.get(this.roleID);
			if(dbRole == null)
				return false;
			
			log = Flower.getFlowerLog(dbRole.friendData.acceptFlower, GameData.getInstance().getCommonCFG().flower.acceptShowCount);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			callback.onCallback(log);
		}
	
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		public final Integer roleID;
		public List<SBean.FlowerLog> log = new ArrayList<>();
		public final GetAcceptFlowerLogCallback callback;
	}
	
	//不能在role锁中调用
	void getAcceptFlowerLog(int rid, GetAcceptFlowerLogCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getAcceptFlowerLog());
			return;
		}
		
		gs.getDB().execute(new GetAcceptFlowerLogTrans(rid, callback::onCallback));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetGiveFlowerLogCallback
	{
		void onCallback(List<SBean.FlowerLog> log);
	}
	public class GetGiveFlowerLogTrans implements Transaction
	{
		public GetGiveFlowerLogTrans(int roleID, GetGiveFlowerLogCallback callback)
		{
			this.roleID = roleID;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbRole = role.get(this.roleID);
			if(dbRole == null)
				return false;
			
			log = Flower.getFlowerLog(dbRole.friendData.giveFlower, GameData.getInstance().getCommonCFG().flower.giveShowCount);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			callback.onCallback(log);
		}
	
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		public final Integer roleID;
		public List<SBean.FlowerLog> log = new ArrayList<>();
		public final GetGiveFlowerLogCallback callback;
	}
	
	
	//不能在role锁中调用
	void getGiveFlowerLog(int rid, GetGiveFlowerLogCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			callback.onCallback(role.getGiveFlowerLog());
			return;
		}
		
		gs.getDB().execute(new GetGiveFlowerLogTrans(rid, callback::onCallback));
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetFlowerOverviewsCallback
	{
		void onCallback(List<SBean.FlowerOverview> overviews);
	}
	
	public class GetFlowerOverviewsTrans implements Transaction
	{
		public GetFlowerOverviewsTrans(List<SBean.FlowerLog> logs, GetFlowerOverviewsCallback callback)
		{
			this.logs = logs;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			for(SBean.FlowerLog log: this.logs)
			{
				DBRole dbRole = role.get(log.roleID);
				if(dbRole == null)
					continue;
				
				DBRoleShare dbRoleshare = roleshare.get(dbRole.getUsername());
				if(dbRoleshare == null)
					continue;
				
				overviews.add(dbRole.getRoleFlowerOverview(dbRoleshare.vipPay.vipLvl, log.count));
			}
			
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			callback.onCallback(overviews);
		}
	
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		@AutoInit
		public TableReadonly<String, DBRoleShare> roleshare;
		
		public final List<SBean.FlowerLog> logs;
		public final GetFlowerOverviewsCallback callback;
		
		public List<SBean.FlowerOverview> overviews = new ArrayList<>();
	}
	
	
	//不能在role锁中调用
	void getFlowerOverviews(List<SBean.FlowerLog> logs, GetFlowerOverviewsCallback callback)
	{
		if(logs.isEmpty())
		{
			callback.onCallback(GameData.emptyList());
			return;
		}
		
		List<SBean.FlowerLog> offLine = new ArrayList<>();
		List<SBean.FlowerOverview> overviews = new ArrayList<>();
		for(SBean.FlowerLog log: logs)
		{
			Role role = this.getOnGameRole(log.roleID);
			if (role != null)
			{
				overviews.add(role.getRoleFlowerOverview(log.count));
			}
			else
			{
				offLine.add(log);
			}
		}
		
		if (offLine.isEmpty())
		{
			callback.onCallback(overviews);
			return;
		}
		
		gs.getDB().execute(new GetFlowerOverviewsTrans(offLine, offlineOverviews -> {
			overviews.addAll(offlineOverviews);
			callback.onCallback(overviews);
		}));
	}
	
	public interface GetGiveAndAcceptFlowerOverviewsCallback
	{
		void onCallback(List<SBean.FlowerOverview> giveOverviews, List<SBean.FlowerOverview> acceptOverviews);
	}
	
	//不能在role锁中调用
	void getGiveAndAcceptFlowerOverviews(List<SBean.FlowerLog> giveLogs, List<SBean.FlowerLog> acceptLogs, GetGiveAndAcceptFlowerOverviewsCallback callback)
	{
		getFlowerOverviews(giveLogs, giveOverviews -> {
			getFlowerOverviews(acceptLogs, acceptOverviews -> {
				callback.onCallback(giveOverviews, acceptOverviews);
			});
		});
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//
//	public interface GetOfflineFlowerListCallback
//	{
//		void onCallback(List<SBean.FlowerOverview> offlinegGiveFlowers, List<SBean.FlowerOverview> offlinegAcceptFlowers);
//	}
//	public class GetOfflineFlowerListTrans implements Transaction
//	{
//		public GetOfflineFlowerListTrans(List<Integer> offlinegGiveFlower, List<Integer> offlinegAcceptFlower, GetOfflineFlowerListCallback callback)
//		{
//			this.offlinegGiveFlower = offlinegGiveFlower;
//			this.offlinegAcceptFlower = offlinegAcceptFlower;
//			this.callback = callback;
//		}
//		
//		@Override
//		public boolean doTransaction()
//		{
//			for(int roleID : offlinegGiveFlower)
//			{
//				DBRole dbrole = role.get(roleID);
//				if( dbrole != null )
//				{
//					DBRoleShare dbroleshare = roleshare.get(dbrole.getUsername());
//					offlinegGiveFlowers.add(dbrole.getRoleFlowerOverview(dbroleshare.vipPay.vipLvl, 0));		//TODO
//				}
//			}
//			
//			for(int roleID : offlinegAcceptFlower)
//			{
//				DBRole dbrole = role.get(roleID);
//				if( dbrole != null)
//				{
//					DBRoleShare dbroleshare = roleshare.get(dbrole.getUsername());
//					offlinegAcceptFlowers.add(dbrole.getRoleFlowerOverview(dbroleshare.vipPay.vipLvl, 0));	//TODO
//				}
//			}
//			return true;
//		}
//
//		@Override
//		public void onCallback(ErrorCode errcode)
//		{
//			callback.onCallback(offlinegGiveFlowers, offlinegAcceptFlowers);
//		}
//	
//		@AutoInit
//		public TableReadonly<Integer, DBRole> role;
//		@AutoInit
//		public TableReadonly<String, DBRoleShare> roleshare;
//		
//		public final List<Integer> offlinegGiveFlower;
//		public final List<Integer> offlinegAcceptFlower;
//		
//		public List<SBean.FlowerOverview> offlinegGiveFlowers = new ArrayList<>();
//		public List<SBean.FlowerOverview> offlinegAcceptFlowers= new ArrayList<>();
//		public final GetOfflineFlowerListCallback callback;
//	}
//	void getOfflineFlowerList(List<Integer> offlinegGiveFlower, List<Integer> offlinegAcceptFlower, GetOfflineFlowerListCallback callback)
//	{
//		gs.getDB().execute(new GetOfflineFlowerListTrans(offlinegGiveFlower, offlinegAcceptFlower, callback::onCallback));
//	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void kickOnlineRole(int rid)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
			gs.getRPCManager().closeClientSession(role.netsid);
	}
	
	public interface ModifyRoleDataCallback
	{
		public void onCallback(int errcode);
	}
	
	public void banUser(final String username, final int second, final String reason, final ModifyRoleDataCallback callback)
	{
		modifyUserBan(username, (ban) -> 
        {
        	ban.banLogin(second, reason, GameTime.getTime());
        	return true;
        }, callback);
	}
	
	public void unbanUser(final String username, final ModifyRoleDataCallback callback)
	{
		 modifyUserBan(username, (ban) -> 
         {
         	ban.unbanLogin();
         	return false;
         }, callback);
	}
	
	public void banUserChat(final String username, final int second, final String reason, final ModifyRoleDataCallback callback)
	{
		modifyUserBan(username, (ban) -> 
        {
        	ban.banChat(second, reason, GameTime.getTime());
        	return true;
        }, callback);
	}
	
	public void unbanUserChat(final String username, final ModifyRoleDataCallback callback)
	{
		 modifyUserBan(username, (ban) -> 
         {
         	ban.unbanChat();
         	return false;
         }, callback);
	}
	
	interface ModifyUserBanFunc
	{
		boolean modify(Ban ban);
	}
	public class ModifyUserBanTrans implements Transaction
	{
		public ModifyUserBanTrans(String username, ModifyUserBanFunc func, ModifyRoleDataCallback callback)
		{
			this.username = username;
			this.func = func;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBUser dbuser = user.get(username);
			if (dbuser == null)
				return false;
			ban = new Ban(dbuser.ban);
			kick = func.modify(ban);
			dbuser.ban = ban.toDBDataWithoutLock();
			user.put(username, dbuser);
			return true;
		}	
			
		@Override
		public void onCallback(ErrorCode errCode)
		{
			if (errCode == ErrorCode.eOK)
			{
				Role role = getUserRole(username);
				if (role != null)
				{
					try
					{
						role.lock();
						role.updateBanInfo(ban);
						if (kick)
							gs.getRPCManager().closeClientSession(role.netsid);
					}
					finally
					{
						role.unlock();
					}
				}
			}
			callback.onCallback(errCode == ErrorCode.eOK ? IDIP.IDIP_HEADER_RESULT_SUCCESS : IDIP.IDIP_HEADER_RESULT_USER_NOT_EXIST);
		}
		
		@AutoInit
		public Table<String, DBUser> user;

		private String username;
		private ModifyUserBanFunc func;
		private ModifyRoleDataCallback callback;
		
		private Ban ban;
		private boolean kick;
	}
	private void modifyUserBan(String username, ModifyUserBanFunc func, ModifyRoleDataCallback callback)
	{
		gs.getDB().execute(new ModifyUserBanTrans(username, func, callback), username.hashCode());
	}
	
	interface AddMailCallback
	{
		void onCallback(int mailId);
	}
	interface DelMailCallback
	{
		void onCallback(boolean ok);
	}

	void sendWorldMail(int levelMin, int levelMax, int vipMin, int vipMax, Set<String> channels, int lifeTime, String title, String content, List<SBean.DummyGoods> attachment, AddMailCallback callback)
	{
		this.worldMail.addWorldMail(new SBean.DBWorldMail(0, (short) levelMin, (short) levelMax, (short) vipMin, (short) vipMax, channels, GameTime.getTime(), lifeTime, title, content, attachment), (mailId) -> callback.onCallback(mailId));
	}
	
	void addNewRoleSysMail(String title, String content, Set<String> channels, List<SBean.DummyGoods> attachment, AddMailCallback callback)
	{
		this.newRoleSysMail.addNewRoleSysMail(new SBean.DBWorldMail(0, (short) 1, (short) 1000, (short) 0, (short) 100, channels, GameTime.getTime(), 86400*365, title, content, attachment), (mailId) -> callback.onCallback(mailId));
	}
	void delNewRoleSysMail(int mailId, DelMailCallback callback)
	{
		this.newRoleSysMail.delNewRoleSysMail(mailId, (ok -> callback.onCallback(ok)));
	}
	
	void sendGMMail(int rid, int lifeTime, String title, String content, List<SBean.DummyGoods> attachment, AddMailCallback callback)
	{
		this.execCommonMailVisitor(rid, false, new CommonMailVisitor() 
		{
			int newMailId = 0;
			public boolean visit(MailBox mailbox)
			{
				newMailId = mailbox.addGmMail(lifeTime, title, content, GameData.getInstance().toGameItems(attachment), GameData.emptyList());
				return true;
			}
			public void onCallback(boolean success, boolean db)
			{
				callback.onCallback(success ? newMailId : 0);
			}
		});
	}
	interface SendRollNoticeCallback
	{
		void onCallback(int noticeId);
	}
	void sendRollNotice(int sendTime, int lifeTime, int freq, String content, SendRollNoticeCallback callback)
	{
		this.rollNotice.addRollNotice(new SBean.DBRollNotice(0, sendTime, freq, lifeTime, content, (byte) 0), (noticeId) -> callback.onCallback(noticeId));
	}
	interface CancelRollNoticeCallback
	{
		void onCallback(boolean ok);
	}
	void cancelRollNotice(int noticeId, CancelRollNoticeCallback callback)
	{
		this.rollNotice.delRollNotice(noticeId, (ok) -> callback.onCallback(ok));
	}
	
	interface QueryRoleInfoCallback
	{
		void onCallback(IDIP.DoInquiryRoleInfoRsp rsp);
	}
	void addOrReplaceMessageBoard(int side, int msgId, Role role, int time, String content, byte anonymous, AddOrReplaceMessageBoardCallback callback)
	{
		this.messageBoard.addOrReplaceMessageBoard(new SBean.DBMessageBoard(side, msgId, role.id, role.name, GameTime.getTime(), time, content, anonymous, 0, 0), (ok) -> callback.onCallback(ok));
	}
	public interface AddOrReplaceMessageBoardCallback
	{
		void onCallback(int noticeId);
	}
	public class QueryRoleInfoTrans implements Transaction
	{
		public QueryRoleInfoTrans(int rid, QueryRoleInfoCallback callback)
		{
			this.rid = rid;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole == null )
				return false;
			DBUser dbuser = user.get(dbrole.getUsername());
			if(dbuser == null)
				return false;
			Role sameUserRole = LoginManager.this.getUserRole(dbrole.getUsername());
			RoleShare roleShare = null;
			if (sameUserRole != null)
			{
				roleShare = sameUserRole.share;
			}
			else
			{
				DBRoleShare dbRoleShare = roleshare.get(dbrole.getUsername());
				if (dbRoleShare == null)
					return false;
				roleShare = new RoleShare(dbRoleShare);
			}
			rsp = new IDIP.DoInquiryRoleInfoRsp();
			rsp.RoleId = dbrole.id;
			rsp.RoleName = dbrole.name;
			rsp.ServerId = gs.getConfig().id;
			rsp.Level = dbrole.level;
			rsp.VipLevel = roleShare.getVipLevel();
			rsp.PayNum = roleShare.getPayPoints();
			rsp.CreateTime = GameTime.getGMTTimeFromServerTime(dbrole.createTime);
			rsp.Channel = dbrole.register.id.channel; // ?
			rsp.Uid = dbrole.register.id.uid;
			rsp.IsOnline = 0;//offline
			rsp.LastLoginTime = GameTime.getGMTTimeFromServerTime(dbrole.lastLoginTime);
			rsp.Money = dbrole.coinF;
			rsp.bindMoney = dbrole.coinR;
			rsp.Diamond = dbrole.diamondF;
			rsp.bindDiamond = dbrole.diamondR;
			Ban b = new Ban(dbuser.ban);
			rsp.BanStatus = b.isBanLogin(GameTime.getTime()) ? 1 : 0;
			rsp.DiamondFUseTotal = dbrole.diamondFUseTotal;
			rsp.DiamondRUseTotal = dbrole.diamondRUseTotal;
			rsp.ClassType = dbrole.classType;
			rsp.TransfromLvl = dbrole.transformLevel;
			rsp.BWType = dbrole.BWType;
			rsp.Gender = dbrole.gender;
			return false;
		}
		
		@Override
		public void onCallback(ErrorCode errCode)
		{
			callback.onCallback(rsp);
		}
		
		private int rid;
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		@AutoInit
		public TableReadonly<String, DBRoleShare> roleshare;
		@AutoInit
		public TableReadonly<String, DBUser> user;
		private QueryRoleInfoCallback callback;
		IDIP.DoInquiryRoleInfoRsp rsp;
		
	}
	public void queryRoleInfo(int rid, QueryRoleInfoCallback callback)
	{
		Role role = this.getOnGameRole(rid);
		if (role != null)
		{
			IDIP.DoInquiryRoleInfoRsp rsp = new IDIP.DoInquiryRoleInfoRsp();
			rsp.RoleId = role.id;
			rsp.RoleName = role.name;
			rsp.ServerId = gs.getConfig().id;
			rsp.Level = role.level;
			rsp.VipLevel = role.share.getVipLevel();
			rsp.PayNum = role.share.getPayPoints();
			rsp.CreateTime = GameTime.getGMTTimeFromServerTime(role.createTime);
			rsp.Channel = role.getChannel(); // ?
			rsp.Uid = role.getUid();
			rsp.IsOnline = 1;//online
			rsp.LastLoginTime = GameTime.getGMTTimeFromServerTime(role.lastLoginTime);
			rsp.Money = role.getCoin(true);
			rsp.bindMoney = role.getCoin(false);
			rsp.Diamond = role.getDiamond(true);
			rsp.bindDiamond = role.getDiamond(false);
			rsp.BanStatus = role.ban.isBanLogin(GameTime.getTime()) ? 1 : 0;
			rsp.DiamondFUseTotal = role.diamondFUseTotal;
			rsp.DiamondRUseTotal = role.diamondRUseTotal;
			rsp.ClassType = role.classType;
			rsp.TransfromLvl = role.transformLevel;
			rsp.BWType = role.BWType;
			rsp.Gender = role.gender;
			callback.onCallback(rsp);
			return;
		}
		gs.getDB().execute(new QueryRoleInfoTrans(rid, callback));
	}
	
	public List<Integer> queryServerInfo()
	{
		int openTime = GameTime.getGMTTimeFromServerTime(gs.getOpenTime());
		int onlineCount = this.mapRoles.size();
		int roleTotalCreateCount = this.getRoleTotalCreate();
		List<Integer> list = new ArrayList<Integer>();
		list.add(openTime);
		list.add(onlineCount);
		list.add(roleTotalCreateCount);
		return list;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public interface GetUserNameByRoleIDCallback
	{
		void onCallback(int roleId, String userName);
	}
	public class GetUserNameByRoleIDTrans implements Transaction
	{
		public GetUserNameByRoleIDTrans(int roleID, GetUserNameByRoleIDCallback callback)
		{
			this.roleID = roleID;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(roleID);
			if( dbrole == null )
				return false;
			userName = dbrole.getUsername();
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			callback.onCallback(roleID, userName);
		}
	
		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		
		public String userName;
		public final Integer roleID;
		public final GetUserNameByRoleIDCallback callback;
	}
	
	static class RoleId2UserNameCache
	{
		int roleId;
		String userName;
		int lastUseTime;
		
		RoleId2UserNameCache(int roleId, String userName)
		{
			this.roleId = roleId;
			this.userName = userName;
			this.lastUseTime = GameTime.getTime();
		}
		
		String getUserName()
		{
			this.lastUseTime = GameTime.getTime();
			return this.userName;
		}
		
		boolean isTimeout(int timeTick)
		{
			return this.lastUseTime +  60 * 60 <= timeTick;
		}
	}
	
	public void getUserNameByRoleId(final int roleId, final GetUserNameByRoleIDCallback callback)
	{
		RoleId2UserNameCache cache = mapr2nCaches.get(roleId);
		if (cache != null)
		{
			callback.onCallback(roleId, cache.getUserName());
			return;
		}
		gs.getDB().execute(new GetUserNameByRoleIDTrans(roleId, (roleId1, userName) -> {
            if (userName != null)
                mapr2nCaches.put(roleId1, new RoleId2UserNameCache(roleId1, userName));
            callback.onCallback(roleId1, userName);
        }));
	}
	
	public interface CommonMailVisitor
	{
//		public static byte ERR_OK = 0;
//		public static byte ERR_FAILED = -1;
		
		boolean visit(MailBox mailbox);
		void onCallback(boolean success, boolean db);
	}
	
	public static class CommonMailVisitTrans implements Transaction
	{
		public CommonMailVisitTrans(int rid, CommonMailVisitor vistor)
		{
			this.rid = rid;
			this.visitor = vistor;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBMailBox dbmailbox = mail.get(rid);
			if( dbmailbox == null )
				return false;
			MailBox mailbox = new MailBox().fromDB(dbmailbox);
			if (visitor.visit(mailbox))
				mail.put(rid, mailbox.toBDWithouLock());
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			visitor.onCallback(errcode == ErrorCode.eOK, true);
		}
		
		@AutoInit
		public Table<Integer, DBMailBox> mail;
		
		public final int rid;
		public final CommonMailVisitor visitor;
	}
		
	public void execCommonMailVisitor(final int rid, boolean saveAtOnce, final CommonMailVisitor visitor)
	{
		this.addNormalTaskEvent(() -> 
		{
			//加mapRoles访问锁并测试role是否在线，在线则直接对visit在线role的mailbox
			if (tryVisitOnlineRoleMailBox(rid, saveAtOnce, visitor))
				return;
			this.getUserNameByRoleId(rid, (roleId, userName) -> {
	            if (userName == null)
	            {
	                try
	                {
	                    visitor.onCallback(false, false);
	                }
	                catch (Exception ignored)
	                {

	                }
	                return;
	            }
	            exeCommonMailVisitor(userName, rid, saveAtOnce, visitor);
	        });
		});
	}	

	public void exeCommonMailVisitor(final String username, final int rid, boolean saveAtOnce, final CommonMailVisitor visitor)
	{
		synchronized (roleLoadingLock) 
		{
			try
			{
				while (roleLoadingSet.contains(rid))
					roleLoadingLock.wait();	
			}
			catch (Exception ignored)
			{
				visitor.onCallback(false, false);
				return;
			}
			if (tryVisitOnlineRoleMailBox(rid, saveAtOnce, visitor))
				return;
			//DBTrans访问数据库需保证相同账号的role访问在同一个线程，使用execute额外参数username.hashCode来保证
			gs.getDB().execute(new CommonMailVisitTrans(rid, visitor), username.hashCode());
		}
	}
	
	private boolean tryVisitOnlineRoleMailBox(int rid, boolean saveAtOnce, final CommonMailVisitor visitor)
	{
		Role role = this.mapRoles.get(rid);
		if (role == null)
			return false;
		try
		{
			role.lock();
			synchronized (role)
			{
				if (role.isClosed())
					return false;
				if (visitor.visit(role.getMailBox()))
				{
					if (saveAtOnce)
						role.save();
				}	
			}
		}
		finally
		{
			role.unlock();
		}
		visitor.onCallback(true, false);
		return true;
	}
	
	public void sysSendMail(int rid, SysMailType type, int lifeTime, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		this.sysSendMail(rid, type, lifeTime, "" , att, additionalInfo);
	}
	
	public void sysSendMail(int rid, SysMailType type, int lifeTime, String content, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		this.execCommonMailVisitor(rid, false, new CommonMailVisitor() {
				public boolean visit(MailBox mailbox)
				{
					mailbox.addSysMail(type, lifeTime, content, att, additionalInfo);
					return true;
				}
				public void onCallback(boolean success, boolean db)
				{
					
				}
			});
	}

	public void userSendMail(int rid, int srcId, String srcName, String title, String content)
	{
		this.execCommonMailVisitor(rid, false, new CommonMailVisitor()
		{
			public boolean visit(MailBox mailbox)
			{
				mailbox.addUserMail(srcId, srcName, title, content);
				return true;
			}
			public void onCallback(boolean success, boolean db)
			{

			}
		});
	}
	
	
	public interface CommonRoleVisitor
	{	
		boolean visit(Role role, Role sameUserRole);
		void onCallback(boolean success);
	}

	public class CommonRoleVisitTrans implements Transaction
	{
		public CommonRoleVisitTrans(String username, int rid, CommonRoleVisitor vistor)
		{
			this.username = username;
			this.rid = rid;
			this.visitor = vistor;
		}
		
		@Override
		public boolean doTransaction()
		{
			DBRole dbrole = role.get(rid);
			if( dbrole == null )
				return false;
			if (!username.equals(dbrole.getUsername()))
				return false;
			Role sameUserRole = LoginManager.this.getUserRole(dbrole.getUsername());
			RoleShare roleShare = null;//不需要使用role.lock()加锁，即使sameUserRole从容器中取出后，在线角色退出游戏，sameUserRole数据失效，也没关系，因为其中roleShare数据是最新的，并且sameUserRole只做通知用，不会修改其数据
			if (sameUserRole != null)
			{
				roleShare = sameUserRole.share;
			}
			else
			{
				DBRoleShare dbRoleShare = roleshare.get(dbrole.getUsername());
				if (dbRoleShare == null)
					return false;
				roleShare = new RoleShare(dbRoleShare);
			}
			Role roledata = new Role(rid, gs, 0).fromDBRole(roleShare, dbrole, mail.get(rid));
			if (visitor.visit(roledata, sameUserRole))
			{
				roleshare.put(dbrole.getUsername(), roledata.copyDBRoleShareWithoutLock());
				role.put(rid, roledata.copyDBRoleWithoutLock());
				mail.put(rid, roledata.getMailBox().toBDWithouLock());
			}
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			visitor.onCallback(errcode == ErrorCode.eOK);
		}
		
		@AutoInit
		public Table<Integer, DBMailBox> mail;
		@AutoInit
		public Table<String, DBRoleShare> roleshare;
		@AutoInit
		public Table<Integer, DBRole> role;
		
		public final String username;
		public final int rid;
		public final CommonRoleVisitor visitor;
	}
	
	public void readRoles(final List<Integer> roleIDs, Consumer<List<Role>> consumer)
	{
		List<Role> list = new ArrayList<>();
		if( roleIDs == null || roleIDs.isEmpty() )
		{
			consumer.accept(list);
			return;
		}
		final Set<Integer> idSet = new HashSet<>(roleIDs);
		AtomicInteger latch = new AtomicInteger(idSet.size()); 
		final Map<Integer, Role> roleMap = new HashMap<>();
		idSet.stream().forEach(roleID->
		{
			exeCommonRoleVisitor(roleID, false, new CommonRoleVisitor()
			{

				@Override
				public boolean visit(Role role, Role sameUserRole) {
					if( role != null )
						roleMap.put(roleID, role);
					return false;
				}

				@Override
				public void onCallback(boolean success) {
					if( latch.decrementAndGet() == 0 )
					{
						for(int id : roleIDs)
						{
							list.add(roleMap.get(id));
						}
						consumer.accept(list);
						
					}
				}
				
			});
		});
	}
	
	public void readRoleBriefs(final List<Integer> roleIDs, Consumer<List<SBean.RoleOverview>> consumer)
	{
		List<RoleOverview> list = new ArrayList<>();
		if( roleIDs == null || roleIDs.isEmpty() )
		{
			consumer.accept(list);
			return;
		}
		final Set<Integer> idSet = new HashSet<>(roleIDs);
		AtomicInteger latch = new AtomicInteger(idSet.size());
		final Map<Integer, RoleOverview> roleMap = new HashMap<>();
		idSet.stream().forEach(roleID->
		{
			exeCommonRoleVisitor(roleID, false, new CommonRoleVisitor()
			{

				@Override
				public boolean visit(Role role, Role sameUserRole) {
					if( role != null )
						roleMap.put(roleID, role.getRoleOverview());
					return false;
				}

				@Override
				public void onCallback(boolean success) {
					if( latch.decrementAndGet() == 0 )
					{
						for(int id : roleIDs)
						{
							list.add(roleMap.get(id));
						}
						consumer.accept(list);						
					}
				}
				
			});
		});
	}
	
	public void exeCommonRoleVisitor(final int rid, boolean saveAtOnce, final CommonRoleVisitor visitor)
	{
		this.addNormalTaskEvent(() -> 
		{
			//加mapRoles访问锁并测试role是否在线，在线则直接对visit在线role
			if (tryVisitOnlineRole(rid, null, saveAtOnce, visitor))
				return;
			//role不在线则必须先查找role对应的username和rid组合来执行role的访问
			this.getUserNameByRoleId(rid, (roleId, userName) -> {
	            if (userName == null)
	            {
	            	visitor.onCallback(false);
	            	return;
	            }
	            exeCommonRoleVisitor(userName, rid, saveAtOnce, visitor);
	        });
		});
	}
	
	public void exeCommonRoleVisitor(final String username, final int rid, boolean saveAtOnce, final CommonRoleVisitor visitor)
	{
		synchronized (roleLoadingLock) 
		{
			try
			{
				while (roleLoadingSet.contains(rid))
					roleLoadingLock.wait();	//此处db的loading线程还没有执行完，等待其执行完毕再来执行后续操作
			}
			catch (Exception ignored)
			{
				visitor.onCallback(false);
				return;
			}
			if (tryVisitOnlineRole(rid, username, saveAtOnce, visitor))
				return;
			//DBTrans访问数据库需保证相同账号的role访问在同一个线程，使用execute额外参数username.hashCode来保证
			gs.getDB().execute(new CommonRoleVisitTrans(username, rid, visitor), username.hashCode());
		}
	}
	
	private boolean tryVisitOnlineRole(int rid, String checkedUsername, boolean saveAtOnce, final CommonRoleVisitor visitor)
	{
		Role role = this.mapRoles.get(rid);
		if (role == null)
			return false;
		if (checkedUsername != null && !checkedUsername.equals(role.getUsername()))
		{
			visitor.onCallback(false);
			return true;
		}
		try
		{
			role.lock();
			synchronized(role)
			{
				if (role.isClosed())
					return false;
				
				if (visitor.visit(role, null))
	 			{
					if (saveAtOnce)
						role.save();	
				}
			}
		}
		finally
		{
			role.unlock();
		}
		visitor.onCallback(true);
		return true;
	}
	
	interface FinishPayCallback
	{
		void onCallback(int errCode);
	}
	public void finishPay(final int xid, final String channel, final String uid, final int gsid, final int roleid, final String goodsId, int payLevel, final String payExt, final String orderID, FinishPayCallback callback)
	{
		if (gsid != gs.getConfig().id || roleid <= 0 || goodsId.equals("") || payExt.equals("") ||orderID.equals("") )
		{
			callback.onCallback(-101);
			return;
		}
		try
		{
			int zoneId = GameData.getZoneIdFromRoleId(roleid);
			if (!gs.getConfig().zones.contains(zoneId))
			{
				callback.onCallback(-102);
				return;	
			}
			String username = GameData.getUserName(zoneId, channel, uid);
			this.exeCommonRoleVisitor(username, roleid, true, new CommonRoleVisitor()
			{
				int errCode = -100;//可能是visitor执行失败，具体可能是username和roleid不匹配导致
				@Override
				public boolean visit(Role role, Role sameUserRole)
				{
					if (role.userPay(orderID, payLevel, goodsId, sameUserRole))
					{
						errCode = 0;
						return true;
					}
					errCode = -104;//可能是重复的orderID
					return false;
				}
				
				@Override
				public void onCallback(boolean success)
				{
					callback.onCallback(errCode);
				}
			});
		}
		catch (Exception e)
		{
			callback.onCallback(-103);
			return;
		}
	}
	
	public void roleAddRollNotice(byte type, String paras)
	{
		this.rollNotice.roleAddRollNotice(type, paras);
	}

	public void commontMessageBoard(int side, int msgid, int comment, int sendtime, AddOrReplaceMessageBoardCallback callback)
	{
		this.messageBoard.changeMessageBoardByComment(side, msgid, comment, sendtime, callback);
	}

	public void changeMessageBoardContext(int side, int roleid, int msgid, String context, int sendtime, AddOrReplaceMessageBoardCallback callback)
	{
		this.messageBoard.changeMessageBoardByContext(side, roleid, msgid, context, sendtime, callback);
	}
	
	public void changeMessageBoardName(int roleid, String rolename)
	{
		this.messageBoard.changeMessageBoardByName(roleid, rolename);
	}
	
	public int getCurMsgRole(int side, int msgId)
	{
		return messageBoard.getCurMsgRole(side, msgId);
	}
	
	public void changeMarriageBespeakName(int marriageId, int roleid, String rolename)
	{
		this.marriageBespeak.changeMarriageBespeakName(marriageId, roleid, rolename);
	}
	
	public MarriageShare getSharedMarriage(int id)
	{
		return this.mapMarriageShare.get(id);
	}
	
	public void initMarriageShare(int grade, Role role, Role partner, CreateMarriageTransCallback callback)
	{
		gs.getDB().execute(new CreateMarriageTrans(grade, role, partner, marriage ->
		{
			if (marriage != null)
			{
				synchronized (mapMarriageShare)
				{
					mapMarriageShare.put(marriage.getId(), marriage);
				}
			}
			callback.onCallback(marriage);
		}));
	}

	interface CreateMarriageTransCallback
	{
		void onCallback(MarriageShare marriage);
	}
	
	public class CreateMarriageTrans implements Transaction
	{
		public CreateMarriageTrans(int grade, Role role1, Role role2, CreateMarriageTransCallback callback)
		{
			this.grade = grade;
			this.role1 = role1;
			this.role2 = role2;
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			int zoneId = GameData.getZoneIdFromRoleId(role1.id);
			final String maxMarriageIDKey = MaxMarriageIDKey + "_" + zoneId;
			Integer maxid = maxids.get(maxMarriageIDKey);
			int sectSeq = maxid == null ? 1 : maxid + 1;
			if (sectSeq >= GameData.getMaxGSMarriageCount())
				return false;
			int marriageId = GameData.createMarriageId(zoneId, sectSeq);  
			marriageData = new MarriageShare(marriageId, grade, role1, role2);
			maxids.put(maxMarriageIDKey, sectSeq);
			marriageshare.put(marriageData.getId(), marriageData.toDB());
			return true;
		}
		
		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(marriageData == null)
				gs.getLogger().info("create marriage [" + role1.name + "," + role2.name + "] : " + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode));
			else
				gs.getLogger().info("create marriage [" + role1.name + "," + role2.name + "] marriage id : " + marriageData.getId() + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode));
			this.callback.onCallback(marriageData);
		}
		
		@AutoInit
		public Table<String, Integer> maxids;
		@AutoInit
		public Table<Integer, DBMarriageShare> marriageshare;
		
		private static final String MaxMarriageIDKey = "marriageshareid";
		
		private int grade;
		private int endTime;
		private MarriageShare marriageData;
		private Role role1;
		private Role role2;
		private CreateMarriageTransCallback callback;
	}
	
	public void delMarriageShare(int id, int partnerId, DelMarriageTransCallback callback)
	{
		MarriageShare marriage = this.mapMarriageShare.get(id);
		if (marriage == null)
			callback.onCallback(0);
		gs.getDB().execute(new DelMarriageTrans(id, new DelMarriageTransCallback()
		{

			@Override
			public void onCallback(int errcode)
			{
				if (errcode > 0)
				{
					LoginManager.this.exeCommonRoleVisitor(partnerId, true, new LoginManager.CommonRoleVisitor()
					{
						@Override
						public boolean visit(Role role, Role sameUserRole)
						{
							role.divoceUpdateRole();
							return true;
						}

						@Override
						public void onCallback(boolean success)
						{
						}
					});
					synchronized (LoginManager.this.mapMarriageShare)
					{
						LoginManager.this.mapMarriageShare.remove(id);
					}
				}
				callback.onCallback(errcode);
			}
		}));
	}
	
	interface DelMarriageTransCallback
	{
		void onCallback(int errcode);
	}
	
	public class DelMarriageTrans implements Transaction
	{
		public DelMarriageTrans(int id, DelMarriageTransCallback callback)
		{
			this.id = id;
			this.callback = callback;
		}

		@Override
		public boolean doTransaction()
		{
			marriageshare.del(id);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			gs.getLogger().info("delete marriage [" + id + "] : " + (errcode == ErrorCode.eOK ? " ok" : " error " + errcode));
			if (callback != null)
				this.callback.onCallback(errcode == ErrorCode.eOK ? 1 : 0);
		}

		@AutoInit
		public Table<Integer, DBMarriageShare> marriageshare;

		private int id;
		private DelMarriageTransCallback callback;
	}
	
	interface ChangeRoleInfoCallback
	{
		void onCallback(boolean ok);
	}
	
	public void changeRoleInfo(int roleId, int num, int type, ChangeRoleInfoCallback callback)
	{
		this.exeCommonRoleVisitor(roleId, false, new LoginManager.CommonRoleVisitor()
		{
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				switch (type)
				{
				case GameData.IDIP_CHANGE_ROLE_LEVEL:
					return role.modifyLevel(num);
				case GameData.IDIP_CHANGE_ROLE_VIP_POINT:
					return role.modifyVipPoint(num);
				case GameData.IDIP_ADD_ROLE_PAY:
					return role.godPay(num);
				case GameData.IDIP_ADD_ROLE_VIP_POINT:
					return role.gmAddVipPoint(num);
				default:
					break;
				}
				return true;
			}

			@Override
			public void onCallback(boolean success)
			{
				callback.onCallback(success);
			}
		});
	}
	
	interface CheckAndSendGiftCallback
	{
		void onCallback(int ok);
	}
	
	public void checkAndSendGiftToRole(int roleId, String openId, int conditionType, int conditionValue, List<SBean.DummyGoods> awards, String action, CheckAndSendGiftCallback callback)
	{
		this.exeCommonRoleVisitor(roleId, true, new LoginManager.CommonRoleVisitor()
		{
			int errorCode = 0;
			@Override
			public boolean visit(Role role, Role sameUserRole)
			{
				if (!role.register.id.uid.equals(openId))
					return false;
				switch (conditionType)
				{
				case IDIP.YYB_GIFT_CONDITION_TYPE_NONE:
					break;
				case IDIP.YYB_GIFT_CONDITION_TYPE_LEVEL:
					if (role.level < conditionValue)
					{
						errorCode = IDIP.IDIP_HEADER_RESULT_ROLE_CONDITION_NOT_MATCH;
						return true;
					}
					break;
				case IDIP.YYB_GIFT_CONDITION_TYPE_VIP_POINT:
					if (role.share.getPayPoints() < conditionValue)
					{
						errorCode = IDIP.IDIP_HEADER_RESULT_ROLE_CONDITION_NOT_MATCH;
						return true;
					}
					break;
				default:
					errorCode = IDIP.IDIP_HEADER_RESULT_API_EXCEPTION;
					return true;
				}
				role.mailbox.addSysMail(MailBox.SysMailType.YYBGift, MailBox.MAX_RESERVE_TIME, "", GameData.getInstance().toGameItems(awards), GameData.emptyList());
				return true;
			}

			@Override
			public void onCallback(boolean success)
			{
				if (!success)
					errorCode = IDIP.IDIP_HEADER_RESULT_ROLE_NOT_EXIST;
				callback.onCallback(errorCode);
			}
		});
	}
	
	static class OnlineInfoStruct
	{
	    int allCnt;
	    int newCnt;
	}
	
	interface SpeedUpLvlUpdateCallback
	{
		void onCallback();
	}
	
	public class SpeedUpLevel
	{
		private int speedUpLvl;
		private int lastRefreshTime;
		
		public class SpeedUpLvlUpdateTrans implements Transaction
		{
			SpeedUpLvlUpdateTrans(SBean.DBSpeedUpLvl dbSpeedUp, SpeedUpLvlUpdateCallback callback)
			{
				this.dbSpeedUp = dbSpeedUp;
				this.callback = callback;
			}
			
			@Override
			public boolean doTransaction()
			{
				byte[] key = Stream.encodeStringLE("speeduplvl");
				byte[] data = Stream.encodeLE(dbSpeedUp);
				world.put(key, data);
				return true;
			}

			@Override
			public void onCallback(ErrorCode errcode)
			{
				if(errcode != ErrorCode.eOK )
				{
					gs.getLogger().warn("speed up lvl update save failed");
				}
				else
				{
					callback.onCallback();
				}
			}
			
			
			@AutoInit
			public Table<byte[], byte[]> world;
			
			private final SBean.DBSpeedUpLvl dbSpeedUp;
			private final SpeedUpLvlUpdateCallback callback;
		}
		
		SpeedUpLevel()
		{
			
		}
		
		SpeedUpLevel fromDB(SBean.DBSpeedUpLvl dbSpeedUp)
		{
			if(dbSpeedUp != null)
			{
				this.speedUpLvl = dbSpeedUp.speedUpLvl;
				this.lastRefreshTime = dbSpeedUp.lastRefreshTime;
			}
			return this;
		}
		
		public synchronized void updateSpeedUpLevel(int now, int speedUpLvl)
		{
			int nowDay = GameData.getDayByRefreshTimeOffset(now);
			int lastRefreshDay = GameData.getDayByRefreshTimeOffset(this.lastRefreshTime);
			if(nowDay != lastRefreshDay)
			{
				gs.getDB().execute(new SpeedUpLvlUpdateTrans(new SBean.DBSpeedUpLvl(speedUpLvl, now), ()-> 
				{
					this.speedUpLvl = speedUpLvl;
					this.lastRefreshTime = now;
				}));
			}
		}
		
		public synchronized int getSpeedUpLvl()
		{
			return this.speedUpLvl;
		}
	}
	
	public void updateSpeedUpLvl(int now, int speedUpLvl)
	{
		this.speedUpLvl.updateSpeedUpLevel(now, speedUpLvl);
	}
	
	public int getSpeedUpLvl()
	{
		return this.speedUpLvl.getSpeedUpLvl();
	}
	
	public void updateRoleSpeedLvl(Role role, int oldSpeedUpLvl)
	{
		int speedUpLvl = GameData.getInstance().getSpeedUpLvl(getSpeedUpLvl(), gs.getOpenDay());
		if(oldSpeedUpLvl != speedUpLvl)
			role.updateSpeedUpLvl(speedUpLvl);
	}
	
	public void tryNotifyMarriageBespeak(Role role)
	{
		SBean.DBMarriageBespeak bespeak = getMarriageBespeak().getMarriageBespeakByRoleId(role.marriageData.marriageShareId);
		if (bespeak != null)
			gs.getRPCManager().sendStrPacket(role.netsid, new SBean.role_marriage_bespeak_time(bespeak.timeIndex));
	}
	
	public void tryNotifyMarriagePartner(Role role)
	{
		MarriageShare marriageShare = mapMarriageShare.get(role.marriageData.marriageShareId);
		if (marriageShare != null)
			getRoleOverview(role.getMarriagePartnerId(), new GetRoleOverviewCallback()
			{
				@Override
				public void onCallback(RoleOverview roleOverview)
				{
					role.marriageData.partnerName = roleOverview.name;
					gs.getRPCManager().sendStrPacket(role.netsid, new SBean.role_marriage_partner_name(roleOverview.name));
				}
			});
	}
	
	public int getRoleTotalCreate()
	{
		return roleTotalCreate.get();
	}
	
	public void setRoleTotleCreate(int size)
	{
		roleTotalCreate.set(size);
	}
	
	public int getOnlineRoleCount()
	{
		return this.mapRoles.size();
	}
	
	public int getOnlineUserCount()
	{
		return this.noRoleMaps2u.size() + this.mapRoles.size();
	}
	
	public class MaxRoleIdTrans implements Transaction
	{
		MaxRoleIdTrans(MaxRoleIdCallback callback)
		{
			this.callback = callback;
		}
		
		@Override
		public boolean doTransaction()
		{
			for (int zone:gs.getConfig().zones)
			{
				final String maxRoleIDKey = MaxRoleIDKey + "_" + zone;
				Integer maxid = maxids.get(maxRoleIDKey);
				zone2size.put(zone, maxid == null ? 0 : maxid);
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("zone role size get fail");
				callback.onCallback(null);
			}
			else
			{
				callback.onCallback(zone2size);
			}
		}

		@AutoInit
		public Table<String, Integer> maxids;
		public Map<Integer, Integer> zone2size;
		MaxRoleIdCallback callback;
		private static final String MaxRoleIDKey = "roleid";
	}
	
	interface MaxRoleIdCallback
	{
		void onCallback(Map<Integer, Integer> zone2size);
	}
	
	public void getZoneRoleSizes(MaxRoleIdCallback callback)
	{
		gs.getDB().execute(new MaxRoleIdTrans(callback));
	}
	
	public Set<Integer> getLucklyStarRoles(Set<Integer> sendRoles, int size)
	{
		Set<Integer> lucklyRoles = new HashSet<>();
		if (this.maplevel2rsCaches.isEmpty())
			return lucklyRoles;
		List<Integer> levels = this.maplevel2rsCaches.keySet().stream().filter(level -> level >= GameData.getInstance().getLucklyStarCFGS().levelNeed).collect(Collectors.toList());
		
		if (levels.isEmpty())
			return lucklyRoles;

		for (int i = 0; i < 10; i++)
		{
			LevelRoleCache cache = this.maplevel2rsCaches.get(levels.get(GameRandom.getRandom().nextInt(levels.size())));
			if (cache != null)
				cache.getRandomLevelRoles(sendRoles, lucklyRoles, size);

			if (lucklyRoles.size() >= size)
				break;
		}
		return lucklyRoles;
	}
	
	public Role createRobot(Role src, int targetMapID)
	{
		int robotID = -nextRobotID.incrementAndGet();
		Role robot;
		synchronized(src)
		{
			robot = new RoleCopy(robotID, gs).fromDBRole(new RoleShare(src.copyDBRoleShareWithoutLock()), src.copyDBRoleWithoutLock(), null, targetMapID);
		}
		this.mapRoleRobots.put(robot.id, robot);
		gs.getLogger().debug("----------------------create robot, cur robots " + mapRoleRobots.keySet());
		return robot;
	}
	
	public void delRobot(int robotID)
	{
		Role robot = this.mapRoleRobots.remove(robotID);
		gs.getLogger().debug("----------------------remove robot, cur robots " + mapRoleRobots.keySet());
		addNormalTaskEvent(() -> 
		{
			if(robot != null)
				robot.leaveTeam();
		});
	}
	
	
	GameServer gs;
	private volatile AtomicInteger roleTotalCreate = new AtomicInteger(0);
	UserLoginVerifier loginVerifier = new UserLoginVerifier();
	
	ConcurrentMap<String, Integer> userLock = new ConcurrentHashMap<>();
	//sid -> username 
	ConcurrentMap<Integer, String> noRoleMaps2u = new ConcurrentHashMap<>();
	//username -> sid
	ConcurrentMap<String, Integer> mapu2s = new ConcurrentHashMap<>();
	// sid -> roleid
	ConcurrentMap<Integer, Integer> maps2r = new ConcurrentHashMap<>();
	// roleid -> role 
	ConcurrentMap<Integer, Role> mapRoles = new ConcurrentHashMap<>();
	// gameid
	ConcurrentMap<String, Integer> onlineRoles = new ConcurrentHashMap<>();
	
	Object roleLoadingLock = new Object();
	//roleid 
	Set<Integer> roleLoadingSet = new HashSet<>();
	
	ConcurrentMap<Integer, MarriageShare> mapMarriageShare = new ConcurrentHashMap<>();
	
	ConcurrentMap<Integer, RoleId2UserNameCache> mapr2nCaches = new ConcurrentHashMap<>();
	
	NavigableMap<Integer, LevelRoleCache> maplevel2rsCaches = new ConcurrentSkipListMap<>();
	WorldMail worldMail;
	NewRoleSysMail newRoleSysMail;
	RollNotice rollNotice;
	MessageBoard messageBoard;
	MarriageBespeak marriageBespeak;
	List<SBean.MessageInfo> worldMsgCache = new LinkedList<>();
	CDKeyGen cdKeyGen = new CDKeyGen();
	QuizGiftActivity quiz = new QuizGiftActivity();
	SnatchRedEnvelopeActivity redEnvelope = new SnatchRedEnvelopeActivity();
	SpeedUpLevel speedUpLvl = new SpeedUpLevel();
	
	AtomicInteger nextRobotID = new AtomicInteger();
	ConcurrentMap<Integer, Role> mapRoleRobots = new ConcurrentHashMap<>();
	
	private volatile int regNum = 0;
	private volatile int guestNum = 0;
	
	LoginWhiteList loginWhiteList = new LoginWhiteList();
	ChannelBlackList channelBlackList = new ChannelBlackList();
	RegisterLimitList registerLimitList = new RegisterLimitList();
	AssertIgnoreList assertIgnoreList = new AssertIgnoreList();

	LoginQueue loginQueue = new LoginQueue();
	
	GSStat gsStat = new GSStat();
	
	ExecutorService normalTaskExecutor;
	ExecutorService broadcastTaskExecutor;
	
	
	public static final int BAN_PLAY_TYPE_COUNT = 6;
	public static final int BAN_LOGIN_INDEX = 0;
	public static final int BAN_CHAT_INDEX = 1;
	public static final int BAN_BATTLE_INDEX = 2;
	public static final int BAN_MARCH_INDEX = 3;
	public static final int BAN_CHIBI_INDEX = 4;
	public static final int BAN_ARENA_INDEX = 5;
}
