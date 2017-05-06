package i3k.gs;

import i3k.DBRoleShare;
import i3k.SBean;
import i3k.SBean.DBEquipPart;
import i3k.SBean.DBUserData;
import i3k.SBean.DailyLoginCFGS;
import i3k.SBean.DummyGoods;
import i3k.SBean.LastBetaRewardCFGS;
import i3k.SBean.LvlUpRewardCFGS;
import i3k.SBean.onTimeRewardCFGS;
import i3k.SBean.strengthenRewardCFGS;
import i3k.SBean.userdata_modify_req;
import i3k.TLog;
import i3k.SBean.DBUserSurvey;
import i3k.SBean.IntSet;
import i3k.gs.TLogger.TLogEvent;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.TreeSet;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class RoleShare
{
	public RoleShare(DBRoleShare roleShare)
	{
		this.vipPay = roleShare.vipPay;
		this.cbOperations = roleShare.cbOperations;
		this.cashback = roleShare.cashback;
		this.cashbacked = roleShare.cashbacked;
	}

	public synchronized DBRoleShare toDB()
	{
		return new DBRoleShare(this.vipPay.kdClone(), this.cbOperations.kdClone(), cashback, cashbacked);
	}

	public synchronized DBUserSurvey getUserSurveyUnlock()
	{
		return this.cbOperations.userSurvey.kdClone();
	}

	public synchronized int getVipLevel()
	{
		return this.vipPay.vipLvl;
	}

	public synchronized void setVipLevel(int level)
	{
		this.vipPay.vipLvl = level;
	}

	public synchronized int getVipPoints()
	{
		return this.vipPay.payPoints + this.vipPay.presentPoints + this.vipPay.gmPoints;
	}

	public synchronized int getPayPoints()
	{
		return this.vipPay.payPoints;
	}

	public synchronized SBean.PayInfo getPayInfo(int cfgId, List<SBean.PayLevelInfo> payLvlsInfo, Set<Integer> vipRewards)
	{
		this.doClean(GameTime.getTime());
		return new SBean.PayInfo(cfgId, payLvlsInfo, this.vipPay.vipLvl, this.getVipPoints(), new TreeMap<>(this.vipPay.payLvlLog), vipRewards);
	}

	private SBean.DBPayLog getPayLog(String orderId)
	{
		for (SBean.DBPayLog e : this.vipPay.paylog)
		{
			if (e.orderId.equals(orderId))
			{
				return e;
			}
		}
		return null;
	}

	private void doClean(int now)
	{
		Iterator<SBean.DBPayLog> it = this.vipPay.paylog.iterator();
		while (it.hasNext())
		{
			SBean.DBPayLog log = it.next();
			if (log.timeStamp + GameTime.getDayTimeSpan() * 3 < now)
				it.remove();
		}
	}

	public synchronized boolean tryAddPay(Role role, String orderId, SBean.PayLevelCFGS cfg, Role sameUserRole, boolean godPay)
	{
		if (godPay)
		{
			addPay(role, orderId, cfg, sameUserRole);
		}
		else
		{
			if (this.getPayLog(orderId) != null)
				return false;
			this.vipPay.paylog.add(new SBean.DBPayLog(orderId, role.id, cfg.level, GameTime.getTime()));
			addPay(role, orderId, cfg, sameUserRole);
		}
		return true;
	}

	public synchronized void addPay(Role role, String orderId, SBean.PayLevelCFGS cfg, Role sameUserRole)
	{
		int payLvlTimes = this.vipPay.payLvlLog.getOrDefault(cfg.level, 0);
		payLvlTimes += 1;
		int presentDiamond = GameData.getPayLevelPresentDiamond(cfg, payLvlTimes);
		int addDiamond = (cfg.diamond + presentDiamond);
		int addCredit = cfg.credit;

		this.vipPay.payPoints += cfg.points;
		this.vipPay.payDiamond += cfg.diamond;
		this.vipPay.payLvlLog.put(cfg.level, payLvlTimes);
		int beforeVip = this.vipPay.vipLvl;
		this.vipPay.vipLvl = GameData.getInstance().getVipLevel(this.getVipPoints());

		Role onlineRole = sameUserRole != null ? sameUserRole : role;
		onlineRole.SyncVipInfo(this.vipPay.vipLvl, this.getVipPoints());
		role.onAddPay(orderId, cfg.level, payLvlTimes, cfg.money, cfg.points, cfg.type, cfg.param, this.vipPay.payPoints, addDiamond, addCredit, beforeVip, this.vipPay.vipLvl);
	}

	public synchronized boolean noteUserSurvey(Role role, int seq, Set<Integer> answer, TLogEvent tlogEvent)
	{
		SBean.BetaActivityCFGS cfg = GameData.getInstance().getBetaActivity();
		int cursize = cbOperations.userSurvey.answers.size();
		if (seq - 1 != cursize)
			return false;
		if (cursize == cfg.questionNum && cbOperations.userSurvey.reward == 0)
			return false;
		if (cursize < cfg.questionNum && role.level < cfg.surveyNeedLvl)
			return false;
		if (cursize >= cfg.questionNum && role.level < cfg.surveyNeedLvl2)
			return false;
		cbOperations.userSurvey.answers.add(new SBean.IntSet(answer));
		List<SBean.DummyGoods> reward = cursize < cfg.questionNum ? cfg.everyQuestionReward : cfg.everyQuestionReward2;
		if (role.canAddGameItems(reward))
			role.syncAddGameItems(GameData.getInstance().toGameItems(reward), tlogEvent);
		return true;
	}

	public synchronized boolean takeSurveyReward(Role role, TLogEvent tlogEvent)
	{
		SBean.BetaActivityCFGS cfg = GameData.getInstance().getBetaActivity();
		int cursize = cbOperations.userSurvey.answers.size();
		if (cursize != cfg.questionNum && cursize != cfg.questionNum2)
			return false;
		if ((cursize == cfg.questionNum && cbOperations.userSurvey.reward != 0) || (cursize == cfg.questionNum2 && cbOperations.userSurvey.reward != 1))
			return false;
		List<SBean.DummyGoods> surveyReward = cursize == cfg.questionNum ? cfg.surveyFinalReward : cfg.surveyFinalReward2;
		if (!role.canAddGameItems(surveyReward))
			return false;
		role.syncAddGameItems(GameData.getInstance().toGameItems(surveyReward), tlogEvent);
		cbOperations.userSurvey.reward = (byte) (cursize == cfg.questionNum ? 1 : 2);
		return true;
	}

	public synchronized Set<Integer> getBetaLoginGifts()
	{
		return cbOperations.dailyLogin;
	}

	public synchronized boolean takeBetaLoginGifts(Role role, int dayNum, TLogEvent tlogEvent)
	{
		DailyLoginCFGS cfg = GameData.getInstance().getBetaActivity().dailyLogin.get(dayNum);
		if (cfg == null || this.cbOperations.dailyLogin.contains(dayNum))
			return false;
		if (!role.canAddGameItems(cfg.rewards))
			return false;
		role.syncAddGameItems(GameData.getInstance().toGameItems(cfg.rewards), tlogEvent);
		cbOperations.dailyLogin.add(dayNum);
		return true;
	}

	public synchronized Set<Integer> getBetaLvlupGift()
	{
		return cbOperations.lvlUpRewards;
	}

	public synchronized boolean takeBetaLvlupGift(Role role, int seq, TLogEvent tlogEvent)
	{
		LvlUpRewardCFGS cfg = GameData.getInstance().getBetaActivity().lvlUpReward.get(seq);
		if (cfg == null || this.cbOperations.lvlUpRewards.contains(seq))
			return false;
		if (!role.canAddGameItems(cfg.rewards))
			return false;
		role.syncAddGameItems(GameData.getInstance().toGameItems(cfg.rewards), tlogEvent);
		int oldVip = getVipLevel();
		addVipPresentPoint(cfg.vipPoint);
		role.SyncVipInfo(this.vipPay.vipLvl, getVipPoints());
		cbOperations.lvlUpRewards.add(seq);
		int newVip = getVipLevel();
		role.trigerVipTitleReward(oldVip, newVip);
		return true;
	}

	public synchronized DBUserData getUserInfo()
	{
		return cbOperations.useinfo;
	}

	public synchronized boolean modifyUserInfo(userdata_modify_req packet)
	{
		cbOperations.useinfo.cellphone = packet.cellphone;
		cbOperations.useinfo.qq = packet.qq;
		cbOperations.useinfo.isOldUser = packet.isOldUser;
		return true;
	}

	public synchronized boolean takeUserInfoGift(Role role, TLogEvent tlogEvent)
	{
		if (this.cbOperations.useinfo.reward == 1)
			return false;
		if (!role.canAddGameItems(GameData.getInstance().getBetaActivity().completUserInfoReward))
			return false;
		role.syncAddGameItems(GameData.getInstance().toGameItems(GameData.getInstance().getBetaActivity().completUserInfoReward), tlogEvent);
		this.cbOperations.useinfo.reward = 1;
		return true;
	}

	public synchronized Set<Integer> getLastBetaGift()
	{
		return cbOperations.countdown;
	}

	public synchronized boolean takeLastBetaGift(Role role, int openDay, int seq, TLogEvent tlogEvent)
	{
		if (openDay < GameData.getInstance().getBetaActivity().lastBetaStartDay || GameData.getInstance().getBetaActivity().lastBetaStartDay + seq - 1 > openDay)
			return false;
		LastBetaRewardCFGS cfg = GameData.getInstance().getBetaActivity().lastBetaReward.get(seq);
		if (cfg == null || cbOperations.countdown.contains(seq))
			return false;
		if (!role.canAddGameItems(cfg.rewards))
			return false;
		role.syncAddGameItems(GameData.getInstance().toGameItems(cfg.rewards), tlogEvent);
		cbOperations.countdown.add(seq);
		return true;
	}

	public synchronized Set<Integer> getOnTimeLoginGifts()
	{
		return cbOperations.onTime;
	}

	public synchronized boolean takeOnTimeLoginGifts(Role role, int dayNum, TLogEvent tlogEvent)
	{
		onTimeRewardCFGS cfg = GameData.getInstance().getBetaActivity().onTimeReward.get(dayNum);
		if (cfg == null || this.cbOperations.onTime.contains(dayNum) || !checkOnTime())
			return false;
		if (!role.canAddGameItems(cfg.rewards))
			return false;
		role.syncAddGameItems(GameData.getInstance().toGameItems(cfg.rewards), tlogEvent);
		cbOperations.onTime.add(dayNum);
		return true;
	}

	public Set<Integer> getStrengthenGifts()
	{
		return cbOperations.strengthen;
	}

	public synchronized boolean takeStrengthenGifts(Role role, int strengthenNum, TLogEvent tlogEvent)
	{
		strengthenRewardCFGS cfg = GameData.getInstance().getBetaActivity().strengthenReward.get(strengthenNum);
		if (cfg == null || this.cbOperations.strengthen.contains(strengthenNum))
			return false;
		if (!role.canAddGameItems(cfg.rewards))
			return false;
		role.syncAddGameItems(GameData.getInstance().toGameItems(cfg.rewards), tlogEvent);
		cbOperations.strengthen.add(strengthenNum);
		return true;
	}

	private boolean checkOnTime()
	{
		int nowtimer = GameTime.getSecondOfDay();
		return nowtimer > GameData.getInstance().getBetaActivity().onTimeStartTime && nowtimer < GameData.getInstance().getBetaActivity().onTimeEndTime;
	}

	public synchronized boolean testOnTimeReward(Integer k)
	{
		return checkOnTime() && !cbOperations.onTime.contains(k);
	}

	public synchronized boolean testLastBetaReward(Integer k)
	{
		return !cbOperations.countdown.contains(k);
	}

	public synchronized boolean testUseinfo(int level)
	{
		return level >= GameData.getInstance().getBetaActivity().userDataLvl && cbOperations.useinfo.reward == 0;
	}

	public synchronized boolean testLvlUpRewards(int level)
	{
		Set<Integer> rewardLvls = GameData.getInstance().getBetaActivity().lvlUpReward.keySet();
		for (Integer k : rewardLvls)
			if (k <= level && !cbOperations.lvlUpRewards.contains(k))
			{
				return true;
			}
		return false;
	}

	public synchronized boolean testDailyLogin(int openDays)
	{
		return GameData.getInstance().getBetaActivity().dailyLogin.containsKey(openDays) && !cbOperations.dailyLogin.contains(openDays);
	}

	public synchronized boolean testSurvey(int level)
	{
		return (level >= GameData.getInstance().getBetaActivity().surveyNeedLvl && cbOperations.userSurvey.reward == 0) || (level >= GameData.getInstance().getBetaActivity().surveyNeedLvl2 && cbOperations.userSurvey.reward == 1);// && cbOperations.userSurvey.answers.size() == GameData.getInstance().getBetaActivity().surveyQuestions.size();
	}

	public boolean testStrengthenReward(int curstrengthen)
	{
		return GameData.getInstance().getBetaActivity().strengthenReward.keySet().stream().anyMatch(condition -> curstrengthen >= condition && !cbOperations.strengthen.contains(condition));
	}
	
	public byte getOfficialResearchGift()
	{
		return cbOperations.officialResearch;
	}

	public synchronized boolean takeOfficialResearchGifts(Role role, TLogEvent tlogEvent)
	{
		if (this.cbOperations.officialResearch == 1)
			return false;
		SBean.DummyGoods reward = GameData.getInstance().getBetaActivity().officialResearchReward;
		if (!role.canAddGameItem(reward.id, reward.count))
			return false;
		role.syncAddGameItem(GameData.getInstance().toGameItem(reward.id, reward.count), tlogEvent);
		this.cbOperations.officialResearch = 1;
		return true;
	}
	
	public synchronized void setVipGMPoint(Role role, int vipPoint)
	{
		this.vipPay.gmPoints = vipPoint;
		this.vipPay.vipLvl = GameData.getInstance().getVipLevel(getVipPoints());
		role.SyncVipInfo(this.vipPay.vipLvl, getVipPoints());
	}
	
	public synchronized void addVipGMPoint(Role role, int vipPoint)
	{
		this.vipPay.gmPoints += vipPoint;
		if (this.vipPay.gmPoints > GameData.GMVIPPOINT_MAX_NUM)
			this.vipPay.gmPoints = GameData.GMVIPPOINT_MAX_NUM;
		this.vipPay.vipLvl = GameData.getInstance().getVipLevel(getVipPoints());
		role.SyncVipInfo(this.vipPay.vipLvl, getVipPoints());
	}
	
	public synchronized void addVipPresentPoint(int vipPoint)
	{
	    this.vipPay.presentPoints += vipPoint;
	    this.vipPay.vipLvl = GameData.getInstance().getVipLevel(getVipPoints());
	}

	public boolean testOfficialResearch()
	{
		return cbOperations.officialResearch == 0;// && cbOperations.userSurvey.answers.size() == GameData.getInstance().getBetaActivity().surveyQuestions.size();
	}

	private SBean.DBVipPay vipPay;
	private SBean.DBCBOperations cbOperations;
	
	public int cashback;
	public byte cashbacked;
}
