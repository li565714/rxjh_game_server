package i3k.gs;

import com.dataeye.sdk.proto.DCServerSync;
import org.apache.log4j.Logger;

import i3k.SBean;
import i3k.SBean.UserLoginInfo;
import i3k.TLog;
import i3k.util.GameTime;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.dataeye.sdk.client.DCAgent;
import com.dataeye.sdk.proto.DCServerSync.DCMessage.DCUserInfo;
import com.dataeye.sdk.proto.DCServerSync.DCMessage.PlatformType;
import com.dataeye.sdk.proto.DCServerSync.DCMessage.AccountType;
import com.dataeye.sdk.proto.DCServerSync.DCMessage.Gender;
import com.dataeye.sdk.proto.DCServerSync.DCMessage.NetType;
import com.dataeye.sdk.proto.DCServerSync.DCMessage.DCRoleInfo;
import com.dataeye.sdk.client.domain.DCItem;
import com.dataeye.sdk.client.domain.DCCoin;

public class TLogger 
{
	private static final int MAX_EQUIP_LOG_STRING_LENGTH = 2000; 
	private GameServer gs;
	private volatile AtomicInteger seq = new AtomicInteger(0);
	private Logger filelogger = Logger.getLogger("tLogger");
	private Logger reportBaseLogger = Logger.getLogger("rbLogger") ;
	private Logger reportConsumeLogger = Logger.getLogger("rcLogger") ;
	
	private static final int ROLE_LOGOUT = 0;
	private static final int ROLE_LOGIN_NEW = 1;
	private static final int ROLE_LOGIN_RECONNECT = 2;

	public TLogger(GameServer gs)
	{
		this.gs = gs;
	}
	
	public void startUp()
	{
		DCAgent.setBaseConf(String.valueOf(gs.getConfig().id), gs.getConfig().dcAgentBaseDir, 2, 3);
		DCAgent.getInstance(gs.getConfig().dcAgentAppId);
	}
	
	public void shutDown()
	{
		DCAgent.stopAll();
	}
	
	int getNextSeq()
	{
		int curSeq = 0;
		curSeq = seq.incrementAndGet();
		return curSeq;
	}
	
	private void send(String log)
	{
		gs.getRPCManager().sendTLog(log);
	}	
	
	public void log(String log)
	{
		send(log);
		filelogger.info(log);
	}
	
	public void logVerbose(String log)
	{
		send(log);
		filelogger.debug(log);
	}
	
	public void logReportBase(String log)
	{
		reportBaseLogger.info(log);
	}
	
	public void logReportConsume(String log)
	{
		reportConsumeLogger.debug(log);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	public void logUserRegister(SBean.DBRegisterID registerID, SBean.UserLoginInfo loginInfo)
//	{
//		log(toTLogUserRegister(getNextSeq(), registerID, loginInfo).toString());
//	}
//	
//	public void logUserLogin(SBean.DBRegisterID registerID, SBean.UserLoginInfo loginInfo)
//	{
//		log(toTLogUserLogin(getNextSeq(), registerID, loginInfo).toString());
//	} 
//	
//	public void logUserLogout(SBean.DBRegisterID registerID, SBean.UserLoginInfo loginInfo, int onlineTime)
//	{
//		log(toTLogUserLogout(getNextSeq(), registerID, loginInfo, onlineTime).toString());
//	}
	
	public void logCreateRole(Role role, SBean.UserLoginInfo loginInfo)
	{
		log(toTLogRoleCreate(getNextSeq(), role, loginInfo).toString());
		this.reportRoleNew(role);
	}
	
	private void logRoleLevelUpByLoginoutFlow(Role role)
	{
		logVerbose(toTLogRoleLoginout(getNextSeq(), role, 3, 0).toString());
	}
	
	public void logRoleLogin(Role role, int loginType, int now)
	{
	    int onlineTime = 0;
	    if (loginType==Role.ROLE_NEW || loginType==Role.ROLE_LOAD_FROM_DB)
	    {
	        loginType = TLogger.ROLE_LOGIN_NEW;
	    }
	    else 
	    {
	        loginType = TLogger.ROLE_LOGIN_RECONNECT;
	        onlineTime = now - role.lastLoginTime;
	    }
		log(toTLogRoleLoginout(getNextSeq(), role, loginType, onlineTime).toString());
		this.reportUserLogin(role);
		this.reportRoleLogin(role, 1);
	} 
	
	public void logRoleLogout(Role role, int onlineTime)
	{
		log(toTLogRoleLoginout(getNextSeq(), role, TLogger.ROLE_LOGOUT, onlineTime).toString());
		this.reportRoleLogin(role, 2);
	}
	
	public void logRolePay(Role role, int vipBefore, int vipFinal, int addPayPoint, int userPayPoint, int rolePayPoint, int payLvl, int lvlTimes, int payAmount, int addDiamond, String orderId, TLogEvent event)
	{
		int seq = getNextSeq();
		logVerbose(event.toTLogRoleEventFlow(seq, role).toString());
		if (!orderId.equals(GameData.GAME_GOD_PAY_ORDER_ID))
		{
			log(toTlogRolePay(seq, role, vipBefore, vipFinal, addPayPoint, userPayPoint, rolePayPoint, payLvl, lvlTimes, payAmount, addDiamond, orderId).toString());
			this.reportUserPayment(role, payAmount, addDiamond, orderId);
			logDCRolePay(role, payLvl, payAmount, orderId);
		}
	}

	public void logDCRolePay(Role role, int payLvl, int payAmount, String orderId)
	{
		DCServerSync.DCMessage.DCPay payInfo = DCServerSync.DCMessage.DCPay.newBuilder()
				.setPayTime(GameTime.getTime())
				.setPayType("" + payLvl)
				.setCurrencyAmount(payAmount)
				.setCurrencyType("CNY")
				.setOrderId(orderId).build();

		DCAgent.getInstance(gs.getConfig().dcAgentAppId).pay(role.getDCRole().getUserInfo(),
				payInfo, role.getDCRole().getRoleInfo());
	}
	
	public void logRoleEventFlow(Role role, TLogEvent event)
	{
	    int seq = getNextSeq();
		logVerbose(event.toTLogRoleEventFlow(seq, role).toString());
		if (seq % gs.getConfig().biFreq == 0)
			reportItemsChange(role, event);
		logCurrencyFlow(role, seq, event);
	}
	
	public void logCurrencyFlow(Role role, int sequence, TLogEvent event)
    {
		Collection<TLogItem> itemGet = event.getGameItemRecords().getProduceItems();
		Collection<TLogItem> itemCost = event.getGameItemRecords().getConsumeItems();
		
		String timeStamp = GameTime.getDateTimeStampStr();
		
		for (TLogItem get : itemGet)
		{
		    switch (get.id)
		    {
		        case GameData.COMMON_ITEM_ID_DIAMOND:
		        	logVerbose(toTlogCurrencyChangeFlow_R(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_DIAMOND, get.changeCount, get.finalCount).toString());
		            break;
		        case -GameData.COMMON_ITEM_ID_DIAMOND:
		        	logVerbose(toTlogCurrencyChangeFlow_F(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_DIAMOND, get.changeCount, get.finalCount).toString());
		            break;
		        case GameData.COMMON_ITEM_ID_COIN:
		        	logVerbose(toTlogCurrencyChangeFlow_R(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_COIN, get.changeCount, get.finalCount).toString());
		            break;
		        case -GameData.COMMON_ITEM_ID_COIN:
		        	logVerbose(toTlogCurrencyChangeFlow_F(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_COIN, get.changeCount, get.finalCount).toString());
		            break;
		        default:
		    }
		}
		
		for (TLogItem cost : itemCost)
		{
		    switch (cost.id)
		    {
		        case GameData.COMMON_ITEM_ID_DIAMOND:
		        	logVerbose(toTlogCurrencyChangeFlow_R(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_DIAMOND, cost.changeCount, cost.finalCount).toString());
		            break;
		        case -GameData.COMMON_ITEM_ID_DIAMOND:
		        	logVerbose(toTlogCurrencyChangeFlow_F(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_DIAMOND, cost.changeCount, cost.finalCount).toString());
		            break;
		        case GameData.COMMON_ITEM_ID_COIN:
		        	logVerbose(toTlogCurrencyChangeFlow_R(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_COIN, cost.changeCount, cost.finalCount).toString());
		            break;
		        case -GameData.COMMON_ITEM_ID_COIN:
		        	logVerbose(toTlogCurrencyChangeFlow_F(timeStamp, sequence, role, event.eventID, GameData.COMMON_ITEM_ID_COIN, cost.changeCount, cost.finalCount).toString());
		            break;
		        default:
		    }
		}
    }

    public void logRoleLevelUp(Role role, int oldlevel, int level)
	{
		this.reportLevelUp(role, 6);
		logRoleLevelUpByLoginoutFlow(role);
	}
	
    public void logHorseEnhance(Role role, int horseId, int finalHorseLvl)
    {
		logVerbose(toTlogRoleHorseDevelopFlow(role, TLog.HORSE_CLEAR, horseId, 0, finalHorseLvl).toString());
    }

    public void logHorseStarUp(Role role, int horseId, int star)
    {
		logVerbose(toTlogRoleHorseDevelopFlow(role, TLog.HORSE_STAR_UP, horseId, 0, star).toString());
    }
    
    public void logHorseSkillLearned(Role role, int inuseHorseId, int skillID)
    {
		logVerbose(toTlogRoleHorseDevelopFlow(role, TLog.HORSE_SKILL_LEARN, inuseHorseId, skillID, 1).toString());
    }

    public void logHorseSkillLevelUp(Role role, int inuseHorse, int skillID, int skillLevel)
    {
		logVerbose(toTlogRoleHorseDevelopFlow(role, TLog.HORSE_SKILL_UP, inuseHorse, skillID, skillLevel).toString());
    }

    public void logHorseUnlock(Role role, int horseId)
    {
		logVerbose(toTlogRoleHorseDevelopFlow(role, TLog.HORSE_UNLOCK, horseId, 0, 1).toString());
    }
    
    public void logWeaponMake(Role role, int wid, int weaponLevel)
    {
    	logVerbose(toTlogRoleWeaponDevelopFlow(role, TLog.WEAPON_UNLOCK, wid, weaponLevel).toString());
    }

    public void logWeaponLevelUp(Role role, int wid, int level)
    {
    	logVerbose(toTlogRoleWeaponDevelopFlow(role, TLog.WEAPON_LEVEL_UP, wid, level).toString());
    }

    public void logWeaponStarUp(Role role, int wid, int star)
    {
    	logVerbose(toTlogRoleWeaponDevelopFlow(role, TLog.WEAPON_STAR_UP, wid, star).toString());
    }
    
    public void logWeaponTalentPoint(Role role, int wid, int allTalentPoint)
    {
        log(toTlogRoleWeaponDevelopFlow(role, TLog.WEAPON_TALENT_UP, wid, allTalentPoint).toString());
    }
    
    public void logWeaponEquip(Role role, int oldWid, int newWid)
    {
        log(toTlogRoleWeaponDevelopFlow(role, TLog.WEAPON_TALENT_UP, oldWid, newWid).toString());
    }
    
    public void logArmorDevelopFlow(Role role, int type, int wid, int level)
    {
        log(toTlogRoleArmorDevelopFlow(role, type, wid, level).toString());
    }

    public void logPetMake(Role role, int petId, int startLevel)
    {
    	logVerbose(toTlogRolePetDevelopFlow(role, TLog.PET_UNLOCK, petId, startLevel).toString());
    }

    public void logPetLevelUp(Role role, int petId, int level)
    {
    	logVerbose(toTlogRolePetDevelopFlow(role, TLog.PET_LEVEL_UP, petId, level).toString());
    }

    public void logPetStarUp(Role role, int petId, int star)
    {
    	logVerbose(toTlogRolePetDevelopFlow(role, TLog.PET_STAR_UP, petId, star).toString());
    }

    public void logPetCoPractice(Role role, int petId, int petCoPracticeLvl)
    {
    	logVerbose(toTlogRolePetDevelopFlow(role, TLog.PET_COPRATICE, petId, petCoPracticeLvl).toString());
    }

    public void logPetTransform(Role role, int petId, int petTransformLvl)
    {
    	logVerbose(toTlogRolePetDevelopFlow(role, TLog.PET_TRANSFORM, petId, petTransformLvl).toString());
    }

    public void logEquipStrength(Role role, int pos, int seq, int type, int target, boolean success)
    {
    	logVerbose(toTlogEquipStrength(role, pos, seq, type, target, success).toString());
    }
    
    public void logSkillUnlock(Role role, int id)
	{
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.SKILL_UNLOCK, id, 1).toString());
	}

    public void logSkillLevelUp(Role role, int id, int level)
	{
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.SKILL_LEVEL_UP, id, level).toString());
	}
    
    public void logUniqueSkillLevelUp(Role role, int id, int level)
    {
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.UNIQUESKILL_LEVEL_UP, id, level).toString());
    }
    
    public void logUniqueSkillSet(Role role, int id, int level)
    {
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.UNIQUESKILL_SET, id, level).toString());
    }

    public void logSkillRankUp(Role role, int id, int level)
    {
    	logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.SKILL_RANK_UP, id, level).toString());
    }

    public void logSpiritLearn(Role role, int spiritId, int initLevel)
    {
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.SPIRIT_UNLOCK, spiritId, initLevel).toString());
    }

    public void logSpiritLevelUp(Role role, int spiritId, int level)
    {
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.SPIRIT_LEVEL_UP, spiritId, level).toString());
    }

    public void logRareBookUnlock(Role role, int bookID, int level)
    {
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.RAREBOOK_UNLOCK, bookID, level).toString());
    }
    
    public void logRareBookLevelUp(Role role, int bookId, int level)
    {
		logVerbose(toTlogRoleSkillDevelopFlow(role, TLog.RAREBOOK_LEVEL_UP, bookId, level).toString());
    }

//    public void logGrasp(Role role, int graspID, int addLvl)
//    {
//        log(toTlogStrengthen(role, getNextSeq(), EnhanceType.GRASP_LEVELUP.getIndex(), graspID, "", addLvl).toString());
//    }
//    
//    // log table don't contain this item
//    public void logItemStrengthen(Role role, int itemId, int count, int propId, int changeValue, int finalProp)
//    {
//        log(toTlogStrengthen(role, getNextSeq(), EnhanceType.ROLE_STRENGTHEN.getIndex(), propId, String.valueOf(changeValue), finalProp).toString());
//    }

    public void logMarketPutOn(Role role, int itemId, int count, int price)
    {
    	logVerbose(toTlogMaketFlow(role, TLog.MARKET_TYPE_PUTON, itemId, count, price, 1).toString());
    }

    public void logMarketBuy(Role role, int itemId, int count, int price)
    {
    	logVerbose(toTlogMaketFlow(role, TLog.MARKET_TYPE_BUY, itemId, count, price, 1).toString());
    }

    public void logProduceProduce(Role role, int itemType, int itemId, int itemNum)
    {
    	logVerbose(toTlogProduceFlow(role, TLog.PRODUCT_TYPE_CREATE, itemType, itemId, itemNum).toString());
    }

    public void logProduceSplit(Role role, int itemType, int itemId, int itemNum)
    {
    	logVerbose(toTlogProduceFlow(role, TLog.PRODUCT_TYPE_SPLITE, itemType, itemId, itemNum).toString());
    }

    public void logSectEventFlow(Sect sect, int type)
    {
    	logVerbose(toTlogSectEventFlow(sect, type).toString());
    }

    public void logSectMapFlow(Sect sect, int mapLevel, int mapId, int mapType, int operateType)
    {
    	logVerbose(toTlogSectMapFlow(sect, mapLevel, mapId, mapType, operateType).toString());
    }

    public void logRoleSectMapJoinFlow(Role role, int mapLevel, int mapId, int mapType)
    {
    	logVerbose(toTlogRoleSectMapJoinFlow(role, mapLevel, mapId, mapType).toString());
    }

    public void logRoleSectDeliverFlow(Role role, int deliverType, int operateType, int operateArg)
    {
    	logVerbose(toTlogRoleSectDeliverFlow(role, deliverType, operateType, operateArg).toString());
    }

    public void logRoleSectDiySkillFlow(Role role, int diySkillType)
    {
    	logVerbose(toTlogRoleSectDiySkillFlow(role, diySkillType).toString());
    }

    public void logRoleFriendInteractionFlow(Role role, int interactionType, int interactionArg)
    {
    	logVerbose(toTlogRoleFriendInteractionFlow(role, interactionType, interactionArg).toString());
    }

    public void logRoleDayEquipFlow(Role role, int equip1, int equip2, int equip3, int equip4, int equip5, int equip6)
    {
    	logVerbose(toTlogRoleDayEquipFlow(role, equip1, equip2, equip3, equip4, equip5, equip6).toString());
    }

    public void logRoleDayBagItemFlow(Role role, int itemId, int itemNum)
    {
    	logVerbose(toTlogRoleDayBagItemFlow(role, itemId, itemNum).toString());
    }

    public void logRoleDayGemFlow(Role role, int solt, int gem1, int gem2, int gem3)
    {
    	logVerbose(toTlogRoleDayGemFlow(role, solt, gem1, gem2, gem3).toString());
    }

    public void logRoleDayBagGemFlow(Role role, int gemId, int gemNum)
    {
    	logVerbose(toTlogRoleDayBagGemFlow(role, gemId, gemNum).toString());
    }
    
    public void logRoleJoinTeamFlow(Role role)
    {
    	logVerbose(toTlogRoleJoinTeamFlow(role).toString());
    }

    public void logRoleStoreBuyFlow(Role role, int storeType, int buyId, int count, int consumeId, int consumeCnt, int produceId, int produceCnt)
    {
    	logVerbose(toTlogStoreBuy(role, storeType, buyId, count, consumeId, consumeCnt, produceId, produceCnt).toString());
    }

    public void logRoleMallBuyFlow(Role role, int buyId, int count, int consumeId, int consumeCnt, int produceId, int produceCnt)
    {
    	logVerbose(toTlogMallBuy(role, buyId, count, consumeId, consumeCnt, produceId, produceCnt).toString());
    }

    public void logArenaFlow(Role role, int mapId, int event, boolean win)
    {
    	logVerbose(toTlogArenaFlow(role, mapId, event, win).toString());
    }
    
    public void logArenaRank(Role role, int rank)
    {
    	logVerbose(toTlogArenaRank(role, rank).toString());
    }
    
    public void logSuperArenaFlow(Role role, int mapId, int type, int event,  int win)
    {
    	logVerbose(toTlogSuperArenaFlow(role, mapId, type, event, win).toString());
    }
	
    public void logOnlineCount(String gameId, int count)
	{
	    log(toTlogGameServerState(gameId, count).toString());
	}
	
	public void logCommonMapCopyEnd(Role role, int mapId, boolean finish)
	{
		this.reportCheckPointMission(role, mapId, finish);
	}
	
	/////////////////////////////////////////////// log the task bi message //////////////////////////////////////
	
    public void logMainTaskFlow(Role role, int taskID, byte state, int taskEvent)
    {
    	logVerbose(toTlogRoleMainTaskFlow(role, taskID, state, taskEvent).toString());
    }

	public void logBranchTaskFlow(Role role, int groupID, int taskID, int taskState, int taskEvent)
	{
		logVerbose(toTLogBranchTaskFlow(role, groupID, taskID, taskState, taskEvent).toString());
	}

	public void logSectTaskFlow(Role role, int sid, int taskID, int taskEvent)
	{
		logVerbose(toTLogSectTaskFlow(role, sid, taskID, 0, taskEvent).toString());
	}
	
    public void logSectDeliverTask(Role role, int sid, int routeId, int taskId, int state, int taskEvent)
	{
    	logVerbose(toTLogRoleSectDeliverTaskFlow(role, sid, routeId, taskId, state, taskEvent).toString());
	}
	
    public void logSectRobTask(Role role, int sid, int taskId, int state, int taskEvent)
    {
    	logVerbose(toTLogSectRobTaskFlow(role, sid, taskId, state, taskEvent).toString());
    }

    public void logDailyTaskFlow(Role role, int id, int state, int taskEvent)
    {
    	logVerbose(toTLogDailyTaskFlow(role, id, state, taskEvent).toString());
    }

    public void logScheduleTaskFlow(Role role, int sid)
    {
    	logVerbose(toTLogScheduleTaskFlow(role, sid).toString());
    }

    public void logPrivateNormalCopyFlow(Role role, int mapId, int copyLevel, int taskEvent, int sweep)
    {
    	logVerbose(toTLogPrivateNormalCopyFlow(role, mapId, copyLevel, taskEvent, sweep).toString());
    }

    public void logPublicNormalCopyFlow(Role role, int mapId, int copyLevel, int taskEvent)
    {
    	logVerbose(toTLogPublicNormalCopyFlow(role, mapId, copyLevel, taskEvent).toString());
    }
    
    public void logActiveCopyFlow(Role role, int mid, int copyLevel, int taskEvent, int arg)
    {
    	logVerbose(toTLogActiveCopyFlow(role, mid, copyLevel, taskEvent, arg).toString());
    }

    public void logForceWarFlow(Role role, int mapId, int event, boolean win)
    {
    	logVerbose(toTLogForceWarCopyFlow(role, mapId, event, win).toString());
    }

    public void logBWArenaCopyFlow(Role role, int mapId, int event, boolean win)
    {
    	logVerbose(toTLogBWArenaCopyFlow(role, mapId, event, win).toString());
    }

    public void logClimbTowerCopyFlow(Role role, int groupId, int mapId, int event, boolean win)
    {
    	logVerbose(toTLogClimbCopyFlow(role, groupId, mapId, event, win).toString());
    }
    
    public void logBossTaskFlow(Role role, int bossID, int type, int kill, int damage)
    {
    	logVerbose(toTLogBossTaskFlow(role, bossID, type, kill,  damage).toString());
    }

    public void logGuideDone(Role role, int id)
    {
    	logVerbose(toTlogGuideDone(role, id).toString());
    }
    
    public void logRoleChatFlow(Role role, int targetid, int chatTypeWorld, String msg)
    {
    	logVerbose(toRoleChatFlow(role, targetid, chatTypeWorld, msg).toString());
    }

    public void logMarriageFlow(Role role, Role partner, int marriageId, int type, int response)
    {
    	logVerbose(toLogMarriageFlow(role, partner, marriageId, type, response).toString());
    }
    
    public void logPBTCashBackTakeFlow(Role role, int bid, int result, int scoreLevel, int arg)
    {
        log(toPBTCashBackTakeFlow(role, bid, result, scoreLevel, arg).toString());
    }

    public void logJusticeActivityFlow(Role role, int mapId, int event)
	{
    	logVerbose(toJusticeActivityFlow(role, mapId, event).toString());
	}

	public void logSteleActivityFlow(Role role, int event)
	{
		logVerbose(toSteleActivityFlow(role, event).toString());
	}

	public void logEmergencyActivityFlow(Role role, int mapId, int event)
	{
		logVerbose(toEmergencyActivityFlow(role, mapId, event).toString());
	}

	public void logFightNpcActivityFlow(Role role, int mapId, int event, int win)
	{
		logVerbose(toFightNcpActivityFlow(role, mapId, event, win).toString());
	}

	private TLog.RoleFightNpcActivityFlow toFightNcpActivityFlow(Role role, int mapId, int event, int win)
	{
		return new TLog.RoleFightNpcActivityFlow(
            GameTime.getDateTimeStampStr(),
            role.getGameId(),
            role.getChannelOpenId(),
            gs.getConfig().id,
            role.getChannel(),
            role.getUid(),
            role.id,
            role.level,
            role.share.getVipLevel(),
            event,
			win,
            mapId
		);
	}

	private TLog.RoleEmergencyActivityFlow toEmergencyActivityFlow(Role role, int mapId, int event)
	{
		return new TLog.RoleEmergencyActivityFlow(
				GameTime.getDateTimeStampStr(),
				role.getGameId(),
				role.getChannelOpenId(),
				gs.getConfig().id,
				role.getChannel(),
				role.getUid(),
				role.id,
				role.level,
				role.share.getVipLevel(),
				event,
				mapId
		);
	}

	private TLog.RoleSteleActivityFlow toSteleActivityFlow(Role role, int event)
	{
		return new TLog.RoleSteleActivityFlow(
				GameTime.getDateTimeStampStr(),
				role.getGameId(),
				role.getChannelOpenId(),
				gs.getConfig().id,
				role.getChannel(),
				role.getUid(),
				role.id,
				role.level,
				role.share.getVipLevel(),
				event,
				0
		);
	}

	private TLog.RoleJusticeActivityFlow toJusticeActivityFlow (Role role, int mapId, int event)
	{
		return new TLog.RoleJusticeActivityFlow(
				GameTime.getDateTimeStampStr(),
				role.getGameId(),
				role.getChannelOpenId(),
				gs.getConfig().id,
				role.getChannel(),
				role.getUid(),
				role.id,
				role.level,
				role.share.getVipLevel(),
				event,
				mapId
		);
	}

    private TLog.RoleTakePBTCashBackFlow toPBTCashBackTakeFlow(Role role, int bid, int result, int scoreLevel, int arg)
    {
        return new TLog.RoleTakePBTCashBackFlow(
                GameTime.getDateTimeStampStr(),
                role.getGameId(),
                role.getChannelOpenId(),
                gs.getConfig().id,
                role.getChannel(),
                role.getUid(),
                role.id,
                role.level,
                role.share.getVipLevel(),
                bid,
                result,
                scoreLevel,
                arg);
    }


	private TLog.RoleMarriageFlow toLogMarriageFlow(Role role, Role partner, int marriageId, int type, int response)
    {
        return new TLog.RoleMarriageFlow(
                GameTime.getDateTimeStampStr(),
                role.getGameId(),
                role.getChannelOpenId(),
                gs.getConfig().id,
                role.getChannel(),
                role.getUid(),
                role.id,
                role.level,
                role.share.getVipLevel(),
                partner.id,
                partner.level,
                partner.share.getVipLevel(),
                marriageId,
                type,
                response);
    }

    private TLog.RoleChatFlow toRoleChatFlow(Role role, int targetid, int chatTypeWorld, String msg)
    {
        return new TLog.RoleChatFlow(
                GameTime.getDateTimeStampStr(),
                role.getGameId(),
                role.getChannelOpenId(),
                gs.getConfig().id,
                role.getChannel(),
                role.getUid(),
                role.id,
                role.level,
                role.share.getVipLevel(),
                chatTypeWorld,
                targetid,
                msg.replaceAll("\\|", "").replaceAll("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", ""));
    }

    private TLog.RoleCreate toTLogRoleCreate(int sequence, Role role, UserLoginInfo loginInfo)
	{
	    return new TLog.RoleCreate(
	    		GameTime.getDateTimeStampStr(),
	            sequence,
	            gs.getConfig().id,
	            role.getGameId(),
	            role.getChannelOpenId(),
	            role.getChannel(),
	            role.getUid(),
	            role.id,
	            role.name,
	            role.classType,
	            role.gender,
	            role.share.getVipLevel(),
	            loginInfo.system.loginIP,
	            loginInfo.system.macAddr,
	            loginInfo.system.deviceID,
	            loginInfo.system.systemSoftware,
	            loginInfo.system.systemHardware,
	            loginInfo.system.cpuHardware,
	            loginInfo.system.network);
	}
	
	private TLog.RoleLoginout toTLogRoleLoginout(int sequence, Role role, int logType, int onlineTime)
	{
		return new TLog.RoleLoginout(
				GameTime.getDateTimeStampStr(), 
				sequence, 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.getChannel(),
				role.getUid(),
				role.id,
				GameTime.getDateTimeStampStr(role.createTime),
				role.name,
				role.classType,
				role.transformLevel,
				role.BWType,
				role.level,
				role.share.getVipLevel(),
				role.totalPayPoint,
				role.getDiamond(true),
				role.getDiamond(false),
				role.getCoin(true),
				role.getCoin(false),
				role.getSectId(),
				role.marriageData.marriageShareId,
				role.friend.getCurFriendCnt(),
				logType,
				onlineTime,
				role.loginInfo.client.clientVerPacket + "." + role.loginInfo.client.clientVerResource,
				role.loginInfo.system.loginIP,
				role.loginInfo.system.macAddr,
				role.loginInfo.system.deviceID,
				role.loginInfo.system.network,
				role.loginInfo.system.systemHardware,
				role.loginInfo.system.systemSoftware,
				role.loginInfo.system.cpuHardware);
	}
	
	public TLog.RolePayFlow toTlogRolePay(int sequence, Role role, int vipBefore, int vipFinal, int addPayPoint, int userPayPoint, int rolePayPoint, int payLvl, int lvlTimes, int payAmount, int addDiamond, String orderId)
	{
		return new TLog.RolePayFlow(
				GameTime.getDateTimeStampStr(),
				sequence, 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.getChannel(),
				role.getUid(), 
				role.id,
				GameTime.getDateStampStr(role.createTime),
				role.level,
				vipBefore,
				vipFinal,
				addPayPoint,
				userPayPoint,
				rolePayPoint,
				payLvl,
				lvlTimes,
				payAmount,
				addDiamond,
				role.getDiamond(true), 
				orderId);
	}
	
	private TLog.GameSvrState toTlogGameServerState(String gameAppId, int count)
	{
		return new TLog.GameSvrState(
					GameTime.getDateTimeStampStr(), 
					gs.getConfig().id,
					gameAppId,
					count,
					gs.getConfig().addrIDIPListen.host);
	}
	
    private TLog.RoleSkillDevelopFlow toTlogRoleSkillDevelopFlow(Role role, int type, int skillId, int afterLevel)
    {
		return new TLog.RoleSkillDevelopFlow(
				GameTime.getDateTimeStampStr(), 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				role.share.getVipLevel(),
				GameTime.getDateTimeStampStr(role.createTime),
				skillId, 
				type, 
				afterLevel);
    }
	
    private TLog.RolePetDevelopFlow toTlogRolePetDevelopFlow(Role role, int type, int petId, int afterLevel)
    {
		return new TLog.RolePetDevelopFlow(
				GameTime.getDateTimeStampStr(), 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				role.share.getVipLevel(),
				GameTime.getDateTimeStampStr(role.createTime),
				petId, 
				type, 
				afterLevel);
    }
    
    private TLog.RoleArmorDevelopFlow toTlogRoleArmorDevelopFlow(Role role, int type, int armorId, int afterLevel)
    {
        return new TLog.RoleArmorDevelopFlow(
				GameTime.getDateTimeStampStr(), 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				role.share.getVipLevel(),
				GameTime.getDateTimeStampStr(role.createTime),
				armorId, 
				type, 
				afterLevel);
    }
	
    private TLog.RoleWeaponDevelopFlow toTlogRoleWeaponDevelopFlow(Role role, int type, int weaponId, int afterLevel)
    {
		return new TLog.RoleWeaponDevelopFlow(
				GameTime.getDateTimeStampStr(), 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				role.share.getVipLevel(),
				GameTime.getDateTimeStampStr(role.createTime),
				weaponId, 
				type, 
				afterLevel);
    }
	
    private TLog.RoleHorseDevelopFlow toTlogRoleHorseDevelopFlow(Role role, int type, int horseId, int skillId, int afterLevel)
    {
		return new TLog.RoleHorseDevelopFlow(
				GameTime.getDateTimeStampStr(), 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				role.share.getVipLevel(),
				GameTime.getDateTimeStampStr(role.createTime),
				horseId,
				type, 
				skillId, 
				afterLevel);
    }

	private TLog.RoleMarketStoreFlow toTlogMaketFlow(Role role, int type, int itemId, int num, int price, int status)
    {
        return new TLog.RoleMarketStoreFlow(
        		GameTime.getDateTimeStampStr(), 
        		gs.getConfig().id, 
        		role.getGameId(),
        		role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				type,
				itemId,
				price,
				num,
				status);
    }

	private TLog.RoleProduceFlow toTlogProduceFlow(Role role, int type, int itemType, int itemId, int itemNum)
	{
	    return new TLog.RoleProduceFlow(
	    		GameTime.getDateTimeStampStr(),
	            gs.getConfig().id,
	            role.getGameId(),
	            role.getChannelOpenId(),
	            role.getChannel(),
	            role.getUid(),
	            role.id,
	            GameTime.getDateTimeStampStr(role.createTime),
	            role.level,
	            role.share.getVipLevel(),
	            role.produce.produceLvl,
	            type,
	            itemType,
	            itemId,
	            itemNum);
	}
	
	private TLog.RoleBuyStoreFlow toTlogStoreBuy(Role role, int storeType, int buyId, int count, int consumeId, int consumeCnt, int produceId, int produceCnt)
    {
		return new TLog.RoleBuyStoreFlow(
				GameTime.getDateTimeStampStr(), 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				storeType, 
				consumeId, 
				consumeCnt, 
				buyId, 
				count,
				produceId, 
				produceCnt);
    }
	
	private TLog.RoleBuyMallFlow toTlogMallBuy(Role role, int buyId, int count, int consumeId, int consumeCnt, int produceId, int produceCnt)
    {
		return new TLog.RoleBuyMallFlow(
				GameTime.getDateTimeStampStr(), 
				gs.getConfig().id, 
				role.getGameId(),
				role.getChannelOpenId(),
				role.register.id.channel, 
				role.register.id.uid, 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				consumeId, 
				consumeCnt,
				buyId, 
				count,
				produceId, 
				produceCnt);
    }
	
    private TLog.RoleCurrencyChangeFlow_F toTlogCurrencyChangeFlow_F(String timeStamp, int iSeq, Role role, int eventID, int currency, int changeCount, 
            int finalCount)
    {
        return new TLog.RoleCurrencyChangeFlow_F(timeStamp,
                iSeq,
                gs.getConfig().id, 
                role.getGameId(), 
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                role.level,
                role.share.getVipLevel(),
                eventID,
                currency,
                changeCount,
                finalCount);
    }
    
    private TLog.RoleCurrencyChangeFlow_R toTlogCurrencyChangeFlow_R(String timeStamp, int iSeq, Role role, int eventID, int currency, int changeCount, 
            int finalCount)
    {
        return new TLog.RoleCurrencyChangeFlow_R(timeStamp,
                iSeq,
                gs.getConfig().id, 
                role.getGameId(), 
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                role.level,
                role.share.getVipLevel(),
                eventID,
                currency,
                changeCount,
                finalCount);
    }
    
    private TLog.ArenaRank toTlogArenaRank(Role role, int rank)
    {
    	return new TLog.ArenaRank(GameTime.getDateTimeStampStr(), 
    							  gs.getConfig().id, 
    							  role.getGameId(), 
    							  role.getChannelOpenId(),
    							  role.register.id.channel, 
    							  role.getUid(), 
    							  role.id, 
    							  GameTime.getDateTimeStampStr(role.createTime), 
    							  role.level, 
    							  role.share.getVipLevel(), 
    							  rank, 
    							  0);
    }
    
    private TLog.ArenaFlow toTlogArenaFlow(Role role, int mapId, int event, boolean win)
    {
        return new TLog.ArenaFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.register.id.channel,
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                event,
                win?1:0);
    }
    
    private TLog.SuperArenaFlow toTlogSuperArenaFlow(Role role, int mapId, int arenaType, int event, int win)
    {
        return new TLog.SuperArenaFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                arenaType,
                event,
                win);
    }

    private TLog.RoleMainTaskFlow toTlogRoleMainTaskFlow(Role role, int taskId, byte taskState, int taskEvent)
    {
        return new TLog.RoleMainTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.register.createTime),
                role.level,
                role.share.getVipLevel(),
                taskId,
                taskState,
                taskEvent);
    }
    
	private TLog.RoleBranchTaskFlow toTLogBranchTaskFlow(Role role, int groupId, int taskId, int taskState, int taskEvent)
    {
        return new TLog.RoleBranchTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(), 
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                groupId,
                taskId,
                taskState,
                taskEvent);
    }
	
	private TLog.RoleSectTaskFlow toTLogSectTaskFlow(Role role, int sid, int taskID, int taskState, int taskEvent)
    {
        return new TLog.RoleSectTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                sid,
                taskID,
                taskState,
                taskEvent);
    }
	
	private TLog.RoleSectDeliverTaskFlow toTLogRoleSectDeliverTaskFlow(Role role, int sid, int routeId, int taskId, int taskState, int taskEvent)
    {
        return new TLog.RoleSectDeliverTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                sid,
                taskId,
                taskState,
                taskEvent);
    }

	private TLog.RoleSectRobTaskFlow toTLogSectRobTaskFlow(Role role, int sid, int taskId, int state, int taskEvent)
    {
        return new TLog.RoleSectRobTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                sid,
                taskId,
                state,
                taskEvent);
    }
	
    private TLog.RoleDailyTaskFlow toTLogDailyTaskFlow(Role role, int id, int state, int taskEvent)
    {
        return new TLog.RoleDailyTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                id,
                state,
                taskEvent);
    }
    
    private TLog.RoleScheduleTaskFlow toTLogScheduleTaskFlow(Role role, int sid)
    {
        return new TLog.RoleScheduleTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                sid,
                sid,
                TLog.TASKEVENT_FINISH);
    }

    private TLog.RolePrivateNormalCopyFlow toTLogPrivateNormalCopyFlow(Role role, int mapId, int copyLevel, int taskEvent, int arg)
    {
        return new TLog.RolePrivateNormalCopyFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                mapId,
                taskEvent,
                arg);
    }
    
    private TLog.RolePublicNormalCopyFlow toTLogPublicNormalCopyFlow(Role role, int mapId, int copyLevel, int taskEvent)
    {
        return new TLog.RolePublicNormalCopyFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                mapId,
                taskEvent);
    }
    
    private TLog.RoleActiveCopyFlow toTLogActiveCopyFlow(Role role, int mid, int copyLevel, int taskEvent, int arg)
    {
        return new TLog.RoleActiveCopyFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                mid,
                taskEvent,
                arg);
    }
    
    private TLog.ForceWarFlow toTLogForceWarCopyFlow(Role role, int mapId, int event, boolean win)
    {
        return new TLog.ForceWarFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                role.BWType,
                event,
                win?1:0);
    }

    private TLog.BWArenaWarFlow toTLogBWArenaCopyFlow(Role role, int mapId, int event, boolean win)
    {
        return new TLog.BWArenaWarFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                role.BWType,
                event,
                win?1:0);
    }

    private TLog.ClimbCopyFlow toTLogClimbCopyFlow(Role role, int groupId, int mapId, int event, boolean win)
    {
        return new TLog.ClimbCopyFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                groupId,
                mapId,
                event,
                win?1:0);
    }

    private TLog.RoleBossTaskFlow toTLogBossTaskFlow(Role role, int bossID, int type, int kill, int damage)
    {
        return new TLog.RoleBossTaskFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.level,
                role.share.getVipLevel(),
                bossID,
                type,
                kill,
                damage);
    }

	private TLog.RoleGuideFlow toTlogGuideDone(Role role, int guideId)
    {
        return new TLog.RoleGuideFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		gs.getConfig().id,
        		role.getChannelOpenId(),
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				guideId);
    }
	
    private TLog.RoleEquipStrengthenFlow toTlogEquipStrength(Role role, int pos, int slot, int type, int target, boolean success)
    {
        return new TLog.RoleEquipStrengthenFlow(GameTime.getDateTimeStampStr(),
                gs.getConfig().id,
                role.getGameId(),
                role.getChannelOpenId(),
                role.getChannel(),
                role.getUid(),
                role.id,
                GameTime.getDateTimeStampStr(role.createTime),
                role.share.getVipLevel(),
                role.level,
                type,
                pos,
                slot,
                target,
                success?1:0);
    }

	private TLog.SectEventFlow toTlogSectEventFlow(Sect sect, int type)
    {
        return new TLog.SectEventFlow(
        		GameTime.getDateTimeStampStr(), 
        		gs.getConfig().id,
        		sect.id, 
				sect.name, 
				sect.members.size(), 
				sect.level, 
				type);
    }

	private TLog.SectMapFlow toTlogSectMapFlow(Sect sect, int mapLevel, int mapId, int mapType, int operateType)
    {
        return new TLog.SectMapFlow(
        		GameTime.getDateTimeStampStr(), 
        		gs.getConfig().id,
        		sect.id, 
				sect.name, 
				sect.members.size(), 
				sect.level, 
				mapLevel,
				mapId,
				mapType,
				operateType);
    }

	private TLog.RoleSectMapJoinFlow toTlogRoleSectMapJoinFlow(Role role, int mapLevel, int mapId, int mapType)
    {
        return new TLog.RoleSectMapJoinFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				role.sectData.data.sectBrief.sectID, 
				mapLevel,
				mapId,
				mapType);
    }

	private TLog.RoleSectDeliverFlow toTlogRoleSectDeliverFlow(Role role, int deliverType, int operateType, int operateArg)
    {
        return new TLog.RoleSectDeliverFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				deliverType,
				operateType,
				operateArg);
    }

	private TLog.RoleSectDiySkillFlow toTlogRoleSectDiySkillFlow(Role role, int diySkillType)
    {
        return new TLog.RoleSectDiySkillFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				diySkillType);
    }

	private TLog.RoleFriendInteractionFlow toTlogRoleFriendInteractionFlow(Role role, int interactionType, int interactionArg)
    {
        return new TLog.RoleFriendInteractionFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				interactionType,
				interactionArg);
    }

	private TLog.RoleDayEquipFlow toTlogRoleDayEquipFlow(Role role, int equip1, int equip2, int equip3, int equip4, int equip5, int equip6)
    {
        return new TLog.RoleDayEquipFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				equip1,
				equip2,
				equip3,
				equip4,
				equip5,
				equip6);
    }

	private TLog.RoleDayBagItemFlow toTlogRoleDayBagItemFlow(Role role, int itemId, int itemNum)
    {
        return new TLog.RoleDayBagItemFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				itemId,
				itemNum);
    }

	private TLog.RoleDayGemFlow toTlogRoleDayGemFlow(Role role, int solt, int gem1, int gem2, int gem3)
    {
        return new TLog.RoleDayGemFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				solt,
				gem1,
				gem2,
				gem3);
    }

	private TLog.RoleDayBagGemFlow toTlogRoleDayBagGemFlow(Role role, int gemId, int gemNum)
    {
        return new TLog.RoleDayBagGemFlow(
        		GameTime.getDateTimeStampStr(), 
        		role.getGameId(), 
        		role.getChannelOpenId(),
        		gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel(), 
				gemId,
				gemNum);
    }
	
	private TLog.RoleJoinTeamFlow toTlogRoleJoinTeamFlow(Role role)
	{
		return new TLog.RoleJoinTeamFlow(
				GameTime.getDateTimeStampStr(), 
				role.getGameId(), 
				role.getChannelOpenId(),
				gs.getConfig().id,
				role.register.id.channel, 
				role.getUid(), 
				role.id, 
				GameTime.getDateTimeStampStr(role.createTime),
				role.level, 
				role.share.getVipLevel());
	}
	
	public void logRoleJoinSect(int roleId, String channelId, String gameId, String channelOpenId, String uid, int createTime, int level, int vip, int sectId, int eventType)
	{
		logVerbose(toTlogRoleJoinSectFlow(roleId, channelId, gameId, channelOpenId, uid, createTime, level, vip, sectId, eventType).toString());
	}
	
	private TLog.RoleJoinSectFlow toTlogRoleJoinSectFlow(int roleId, String channelId, String gameId, String channelOpenId, String uid, int createTime, int level, int vip, int sectId, int eventType)
	{
	    return new TLog.RoleJoinSectFlow(
	            GameTime.getDateTimeStampStr(),
	            gs.getConfig().id,
	            gameId,
	            channelOpenId,
	            channelId,
	            uid,
	            roleId,
	            GameTime.getDateTimeStampStr(createTime),
	            level,
	            vip,
	            sectId,
	            eventType);
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	TLogEvent createNewEvent(int eventID)
	{
		return new TLogEvent(eventID);
	}
	
	public class TLogEvent
	{
		public int eventID;
		public GameItemRecords viRecords = new GameItemRecords();
		public int arg1 = 0;
		public int arg2 = 0;
		public int arg3 = 0;
		public int arg4 = 0;
		public String strArg = "";
		public TLogEvent(int eventID)
		{
			this.eventID = eventID;
		}
		
		public void setArg(int arg1)
		{
			this.arg1 = arg1;
		}
		
		public void setArg(int arg1, int arg2)
		{
			this.arg1 = arg1;
			this.arg2 = arg2;
		}
		
		public void setArg(int arg1, int arg2, int arg3)
		{
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.arg3 = arg3;
		}
		
		public void setArg(int arg1, int arg2, int arg3, int arg4)
		{
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.arg3 = arg3;
			this.arg4 = arg4;
		}
		
		public void setArg(String arg)
		{
			this.strArg = arg;
		}
		
		public GameItemRecords getGameItemRecords()
		{
			return this.viRecords;
		}
		
		public TLog.RoleEventFlow toTLogRoleEventFlow(int sequence, Role role)
		{
			return new TLog.RoleEventFlow(
					GameTime.getDateTimeStampStr(), 
					sequence, 
					gs.getConfig().id, 
					role.getGameId(),
					role.getChannelOpenId(),
					role.getChannel(),
					role.getUid(),
					role.id,
					role.level,
					role.share.getVipLevel(),
					eventID, 
					viRecords.consumeItemsToString(), 
					viRecords.produceItemsToString(), 
					arg1, 
					arg2, 
					arg3, 
					arg4, 
					strArg);
		}
	}

	static class TLogItem
	{
		public int id;
		public int changeCount;
		public int finalCount;
		public TLogItem(int id, int changeCount, int finalCount)
		{
			this.id = id;
			this.changeCount = changeCount;
			this.finalCount = finalCount;
		}
		
		public TLogItem merge(TLogItem o)
		{
			if (this.id == o.id)
			{
				this.changeCount += o.changeCount;
				this.finalCount = o.finalCount;	
			}
			return this;
		}
		
		static public String toString(Collection<TLogItem> items)
		{
			StringBuilder sb = new StringBuilder();
			for (TLogItem e : items)
			{
				if (sb.length() != 0)
					sb.append(GameItemRecords.TLOG_LEVEL_2_SEP);
				sb.append(e.id).append(GameItemRecords.TLOG_LEVEL_1_SEP).append(e.changeCount).append(GameItemRecords.TLOG_LEVEL_1_SEP).append(e.finalCount);
			}
			return sb.toString();
		}
	}

	static class TLogEquip
	{
		public int id;
		public List<String> guids = new ArrayList<>();
		public TLogEquip(int id, String guid)
		{
			this.id = id;
			this.guids.add(guid);
		}
		
		public TLogEquip addEqiup(String guid)
		{
			this.guids.add(guid);
			return this;
		}
		
		static public String toString(Collection<TLogEquip> equips)
		{
			StringBuilder sb = new StringBuilder();
			{
				StringBuilder ssb = new StringBuilder();
				for (TLogEquip e : equips)
				{
					ssb.delete(0, ssb.length());
					if (sb.length() != 0)
						ssb.append(GameItemRecords.TLOG_LEVEL_2_SEP);
					ssb.append(e.id).append(GameItemRecords.TLOG_LEVEL_1_SEP);
					for (String guid : e.guids)
					{
						ssb.append(e.id).append(GameItemRecords.TLOG_LEVEL_1_SEP);
						ssb.append(guid);
					}
					if (sb.length() + ssb.length() > MAX_EQUIP_LOG_STRING_LENGTH)
						break;
					sb.append(ssb);
				}	
			}
			return sb.toString();
		}
	}
	
	static class ConsumeItem
	{
		
	}

	static class GameItemRecords
	{
		public static final char TLOG_LEVEL_1_SEP = '_';
		public static final char TLOG_LEVEL_2_SEP = ',';
		Map<Integer, TLogItem> consumeItems = new HashMap<>();
		Map<Integer, TLogItem> produceItems = new HashMap<>();
		Map<Integer, TLogEquip> consumeEquips = new HashMap<>();
		Map<Integer, TLogEquip> produceEquips = new HashMap<>();
		
		public GameItemRecords()
		{
			
		}
		
		public void addConsume(int id, int changeCount, int finalCount)
		{
			if (changeCount > 0)
				consumeItems.merge(id, new TLogItem(id, -changeCount, finalCount), (ov, nv) -> ov.merge(nv));
		}

		public void addProduce(int id, int changeCount, int finalCount)
		{
			if (changeCount > 0)
				produceItems.merge(id, new TLogItem(id, changeCount, finalCount), (ov, nv) -> ov.merge(nv));
		}
		
		public Collection<TLogItem> getConsumeItems()
		{
			return consumeItems.values();
		}
		
		public Collection<TLogItem> getProduceItems()
		{
			return produceItems.values();
		}
		 
		public String consumeItemsToString()
		{
			return TLogItem.toString(consumeItems.values());
		}
		
		public String produceItemsToString()
		{
			return TLogItem.toString(produceItems.values());
		}
		
		public String consumeEquipsToString()
		{
			return TLogEquip.toString(consumeEquips.values());
		}
		
		public String produceEquipsToString()
		{
			return TLogEquip.toString(produceEquips.values());
		}
	}

//	TLogPayRecord createNewPayRecord(int vipB, int payB)
//	{
//		return new TLogPayRecord(vipB, payB);
//	}
//
//	
//	public class TLogPayRecord
//	{
//		int vipB;
//		int vipA;
//		int payB;
//		int payA;
//		int payLevel;
//		int payAmount;
//		int diamond;
//		int diamondFinal;
//		String orderId;
//		
//		public TLogPayRecord(int vipB, int payB)
//		{
//			this.vipB = vipB;
//			this.payB = payB;
//		}
//		
//		public void setPayInfo(int payLevel, int payAmount, int diamond, String orderId)
//		{
//			this.payLevel = payLevel;
//			this.payAmount = payAmount;
//			this.diamond = diamond;
//			this.orderId = orderId;
//		}
//		
//		public TLog.RolePayFlow toTlogRolePay(int sequence, Role role)
//		{
//			return new TLog.RolePayFlow(
//					GameTime.getDateTimeStampStr(),
//					sequence, 
//					gs.getConfig().id, 
//					role.getGameId(),
//					role.getChannel(),
//					role.getUid(), 
//					role.id,
//					role.level,
//					this.vipB,
//					role.share.getVipLevel(),
//					this.payB,
//					role.totalPayPoint,
//					payLevel,
//					payAmount,
//					diamond,
//					role.getDiamond(true), 
//					orderId);
//		}
//	}
	
	
	
///////////////////////////////////Data eye 日志/////////////////////////////////////////////////////////////////////////////////////////	
	static class DCAgentUtil
	{
		static NetType netTypeValueOf(int value)
		{
			switch (value)
			{
			case 0:
				return NetType.OTHER;
			case 1:
				return NetType.WIFI;
			case 2:
				return NetType._2G;			
			case 3:
				return NetType._3G;
			case 4:
				return NetType._4G;
			default:
				break;
			}
			return NetType.OTHER;
		}
		
		static String resolutionValueOf(int width, int height)
		{
			return String.valueOf(width) + "*" + String.valueOf(height);
		}
		
		static DCItem getItemChangeInfo(int eventID, TLogItem e)
		{
			return DCItem.newBuilder().itemId(String.valueOf(e.id)).itemType(String.valueOf(e.id > 0 ? e.id : -e.id)).itemCnt(e.changeCount > e.changeCount ? e.changeCount : -e.changeCount).reason(String.valueOf(eventID)).build();
		}
		
		static DCCoin getCoinChangeInfo(int eventID, TLogItem e)
		{
			return DCCoin.newBuilder().coinType(String.valueOf(e.id)).coinNum(e.changeCount > 0 ? e.changeCount : -e.changeCount).totalCoin(e.finalCount).type(String.valueOf(eventID)).build();
		}
		
		static DCItem getItemBuyInfo(int eventID, GameItemRecords record)
		{
			TLogItem buyItem = record.getProduceItems().stream().findAny().orElse(null);
			TLogItem buyCost = record.getConsumeItems().stream().findAny().orElse(null);
			return (buyItem == null || buyCost == null ) ? null : DCItem.newBuilder().itemId(String.valueOf(buyItem.id)).itemType(String.valueOf(buyItem.id > 0 ? buyItem.id : -buyItem.id)).itemCnt(buyItem.changeCount).coinNum(buyCost.changeCount).coinType(String.valueOf(buyCost.id)).build();
		}
		
		
	}
	
	static class DCRole
	{
		GameServer gs;
		Role role;
		DCUserInfo userInfo; 
		DCRoleInfo roleInfo;
		int sampleLevel;
		
		public DCRole(GameServer gs, Role role)
		{
			this.gs = gs;
			this.role = role;
			this.updateUserInfo();
			this.updateRoleInfo();
		}
		
		private void updateUserInfo()
		{
			DCServerSync.DCMessage.DCUserInfo.Builder userinfoBuilder = DCUserInfo.newBuilder();
			if (role.loginInfo != null)
			{
				userinfoBuilder.setMac(role.loginInfo.system.macAddr)
					.setImei(role.loginInfo.system.deviceID)
					.setBrand(role.loginInfo.system.systemHardware)
					.setIp(role.loginInfo.system.loginIP)
					.setNetType(DCAgentUtil.netTypeValueOf(role.loginInfo.system.network))
					.setOsVersion(role.loginInfo.system.systemSoftware)
					.setResolution(DCAgentUtil.resolutionValueOf(role.loginInfo.system.screenWidth, role.loginInfo.system.screenHeight));

			}
			this.userInfo =
					userinfoBuilder
					.setAccountId(role.getUsername() + "_" + role.id)
					//.setMac(role.loginInfo.system.macAddr)
					//.setImei(role.loginInfo.system.deviceID)
					.setPlatform(PlatformType.ADR)
					.setAccountType(AccountType.Anonymous)
					.setAge(25)
					//.setBrand(role.loginInfo.system.systemHardware)
					.setChannel(role.getChannel())
					.setCountry("china")
					.setGameRegion(String.valueOf(gs.getConfig().id))
					.setGender(Gender.UNKNOWN)
					//.setIp(role.loginInfo.system.loginIP)
					.setLanguage("cn")
					//.setNetType(DCAgentUtil.netTypeValueOf(role.loginInfo.system.network))
					.setOperators("china telecom")
					//.setOsVersion(role.loginInfo.system.systemSoftware)
					.setProvince("unknown")
					//.setResolution(DCAgentUtil.resolutionValueOf(role.loginInfo.system.screenWidth, role.loginInfo.system.screenHeight))
					.build();
		}
		private void updateRoleInfo()
		{
			
			{
				this.sampleLevel = this.role.level;
				this.roleInfo = DCRoleInfo.newBuilder().setRoleId(String.valueOf(this.role.id)).setRoleClass(String.valueOf(this.role.classType)).setRoleRace("").setLevel(this.role.level).build();	
			}
		}
		
		public DCUserInfo getUserInfo()
		{
			return userInfo;
		}
		
		public DCRoleInfo getRoleInfo()
		{
			if (this.role.level != this.sampleLevel)
				this.updateRoleInfo();
			return this.roleInfo;
		}

		static String getPowerLevel(int power)
		{
			int level = power/5000;
			return new StringBuilder(level*5000).append("-").append((level+1)*5000 - 1).toString();
		}
	}
	
//	public void logDCRoleDayEvent(Role role)
//	{
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("战力段", DCRole.getPowerLevel(role.roleProperties.getRoleFightPower()));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "战力", map, 0, role.getDCRole().getRoleInfo());
//		}
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("挑战次数", String.valueOf(role.arenaInfo.roleArenaData.normal.timesUsed));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "单人竞技场", map, 0, role.getDCRole().getRoleInfo());
//		}
//		for (int pid : role.arenaInfo.roleArenaData.normal.defencePets)
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("防守佣兵", String.valueOf(pid));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "单人竞技场", map, 0, role.getDCRole().getRoleInfo());
//		}
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("挑战次数", String.valueOf(role.arenaInfo.roleArenaData.normal.enterTimes));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "单人竞技场", map, 0, role.getDCRole().getRoleInfo());
//		}
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("消耗体力值", String.valueOf(role.dayUseVit));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "体力", map, 0, role.getDCRole().getRoleInfo());
//		}
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("简单副本", String.valueOf(role.normalMapCopyLogs.entrySet().stream().filter(e -> GameData.getInstance().getMapCopyDifficulty(e.getKey()) == GameData.MAPCOPY_DIFFICULT_EASY).mapToInt(e -> e.getValue().dayEnterTimes).sum()));
//			map.put("困难副本", String.valueOf(role.normalMapCopyLogs.entrySet().stream().filter(e -> GameData.getInstance().getMapCopyDifficulty(e.getKey()) == GameData.MAPCOPY_DIFFICULT_HARD).mapToInt(e -> e.getValue().dayEnterTimes).sum()));
//			map.put("组队副本", String.valueOf(role.normalMapCopyLogs.entrySet().stream().filter(e -> GameData.getInstance().getMapCopyDifficulty(e.getKey()) < GameData.MAPCOPY_DIFFICULT_TEAM).mapToInt(e -> e.getValue().dayEnterTimes).sum()));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "普通副本参与情况", map, 0, role.getDCRole().getRoleInfo());
//		}
//		for (Map.Entry<Integer, SBean.DBActivityMapGroupLog> e : role.activityMapGroupLogs.entrySet())
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("活动副本" + e.getKey(), String.valueOf(e.getValue().dayEnterTimes));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "活动副本参与情况", map, 0, role.getDCRole().getRoleInfo());
//		}
//		{
//			Map<String, String> map = new HashMap<>();
//			map.put("参与次数", String.valueOf(role.sectData.data.daySectMapEnterTimes.values().stream().mapToInt(Integer::valueOf).sum()));
//			DCAgent.getInstance(gs.getConfig().dcAgentAppId).onEvent(role.getDCRole().getUserInfo(), "帮派副本", map, 0, role.getDCRole().getRoleInfo());
//		}
//	}
	
//	public void logDCRoleItemFlow(Role role, TLogEvent event)
//	{
//		if (role.getDCRole() != null)
//		{
//			if(event.eventID > 70 && event.eventID < 80)
//			{
//				DCItem dcitem = DCAgentUtil.getItemBuyInfo(event.eventID, event.getGameItemRecords());
//				if (dcitem != null)
//					DCAgent.getInstance(gs.getConfig().dcAgentAppId).itemBuy(role.getDCRole().getUserInfo(), dcitem, role.getDCRole().getRoleInfo());
//			}
//			else
//			{
//				for (TLogItem e : event.getGameItemRecords().getConsumeItems())
//				{
//					int idPlane = GameData.getVirtualItemIDPlane(e.id);
//					if (idPlane == GameData.COMMON_ITEM_ID_RESERVED_PLANE)
//						DCAgent.getInstance(gs.getConfig().dcAgentAppId).coinLost(role.getDCRole().getUserInfo(), DCAgentUtil.getCoinChangeInfo(event.eventID, e), role.getDCRole().getRoleInfo());
//					else
//						DCAgent.getInstance(gs.getConfig().dcAgentAppId).itemUse(role.getDCRole().getUserInfo(), DCAgentUtil.getItemChangeInfo(event.eventID, e), role.getDCRole().getRoleInfo());
//				}
//				for (TLogItem e : event.getGameItemRecords().getProduceItems())
//				{
//					int idPlane = GameData.getVirtualItemIDPlane(e.id);
//					if (idPlane == GameData.COMMON_ITEM_ID_RESERVED_PLANE)
//						DCAgent.getInstance(gs.getConfig().dcAgentAppId).coinGain(role.getDCRole().getUserInfo(), DCAgentUtil.getCoinChangeInfo(event.eventID, e), role.getDCRole().getRoleInfo());
//					else
//						DCAgent.getInstance(gs.getConfig().dcAgentAppId).itemGet(role.getDCRole().getUserInfo(), DCAgentUtil.getItemChangeInfo(event.eventID, e), role.getDCRole().getRoleInfo());
//				}
//			}
//		}
//	}
/////////////////////////////////////////BIlog生成/////////////////////////////////////////////////////////////////////
	public void reportUserLogin(Role role)
	{
		logReportBase(toReportUserLogin(role));
	}

	public void reportRoleNew(Role role)
	{
		logReportBase(toReportRoleNew(role));
	}

	public void reportRoleLogin(Role role, int type)
	{
		logReportBase(toReportRoleLogin(role, type));
	}

	public void reportLevelUp(Role role, int type)
	{
		logReportBase(toReportLevelUp(role, type));
	}

	public void reportOnline(String gameId, int onlineCount)
	{
		logReportBase(toReportOnline(gameId, onlineCount));
	}
	
	private void reportUserPayment(Role role, int payAmount, int diamond, String orderId)
	{
		logReportBase(toReportPayment(role, payAmount, diamond, orderId));
	}

	public void reportItemsChange(Role role, TLogEvent event)
	{
		Collection<TLogItem> itemGet = event.getGameItemRecords().getProduceItems();
		Collection<TLogItem> itemCost = event.getGameItemRecords().getConsumeItems();
		boolean diamondbuy = false;
		TLogItem buycostb=new TLogItem(GameData.COMMON_ITEM_ID_DIAMOND, 0, role.getDiamond(false));
		TLogItem buycostfb=new TLogItem(-GameData.COMMON_ITEM_ID_DIAMOND, 0, role.getDiamond(true));
		Map<Integer, Integer> changeNum = new HashMap<Integer, Integer>();
		for (TLogItem cost : itemCost)
		{
			int key = cost.id > 0 ? cost.id : -cost.id;
			if (changeNum.containsKey(key))
				changeNum.put(key, changeNum.get(key) + cost.changeCount);
			else
				changeNum.put(key, cost.changeCount);
		}
		for(TLogItem cost:itemCost)
		{
			switch (cost.id)
			{
			case GameData.COMMON_ITEM_ID_DIAMOND:
				buycostb = cost;
				diamondbuy = true;
				if (itemGet.size() > 0)
					logReportBase(toReportConsume(role, itemGet.stream().findAny().get(), -changeNum.get(GameData.COMMON_ITEM_ID_DIAMOND)));
				else
					logReportBase(toReportConsume(role, new TLogItem(event.eventID, 1, 0), -changeNum.get(GameData.COMMON_ITEM_ID_DIAMOND)));
				break;
			case -GameData.COMMON_ITEM_ID_DIAMOND:
				buycostfb = cost;
				if (!diamondbuy)
				{
					diamondbuy = true;
					if (itemGet.size() > 0)
						logReportBase(toReportConsume(role, itemGet.stream().findAny().get(), -changeNum.get(GameData.COMMON_ITEM_ID_DIAMOND)));
					else
						logReportBase(toReportConsume(role, new TLogItem(event.eventID, 1, 0), -changeNum.get(GameData.COMMON_ITEM_ID_DIAMOND)));
				}
				break;
			case GameData.COMMON_ITEM_ID_COIN:
			case -GameData.COMMON_ITEM_ID_COIN:
				if (itemGet.size() > 0)
					logReportConsume(toReportGoldConsume(role, cost, itemGet.stream().findAny().get(), 0, event.eventID, event.eventID, -changeNum.get(GameData.COMMON_ITEM_ID_COIN), cost.id > 0));
				else
					logReportConsume(toReportGoldConsume(role, cost, null, 0, event.eventID, event.eventID, -changeNum.get(GameData.COMMON_ITEM_ID_COIN), cost.id > 0));
				break;
			default:
				int plane = GameData.getVirtualItemIDPlane(cost.id);
				if (plane == GameData.COMMON_ITEM_ID_RESERVED_PLANE)
				{
					if (itemGet.size() > 0)
						logReportConsume(toReportOtherConsume(role, cost, itemGet.stream().findAny().get(), 0, event.eventID, event.eventID, -changeNum.get(cost.id > 0 ? cost.id : -cost.id)));
					else
						logReportConsume(toReportOtherConsume(role, cost, null, 0, event.eventID, event.eventID, -changeNum.get(cost.id > 0 ? cost.id : -cost.id)));
				}
				else
					logReportConsume(toReportPropsConsume(role, cost, event.eventID, event.eventID));
				break;
			}
		}
		if (diamondbuy)
		{
			if (itemGet.size() > 0)
				logReportConsume(toReportDiamondConsume(role, buycostb, buycostfb, itemGet.stream().findAny().get(), 0, event.eventID, event.eventID));
			else
				logReportConsume(toReportDiamondConsume(role, buycostb, buycostfb, null, 0, event.eventID, event.eventID));
		}
		for(TLogItem get:itemGet)
		{
			switch (get.id)
			{
			case GameData.COMMON_ITEM_ID_DIAMOND:
				logReportConsume(toReportBindDiamondGet(role, get, event.eventID, event.eventID));
				break;
			case -GameData.COMMON_ITEM_ID_DIAMOND:
				logReportConsume(toReportDiamondGet(role, get, 0, event.eventID, event.eventID));
				break;
			case GameData.COMMON_ITEM_ID_COIN:
			case -GameData.COMMON_ITEM_ID_COIN:
				logReportConsume(toReportGoldGet(role, get, 0, event.eventID, event.eventID, get.id > 0));
				break;
			default:
				int plane = GameData.getVirtualItemIDPlane(get.id);
				if(plane == GameData.COMMON_ITEM_ID_RESERVED_PLANE && itemGet.size()>0)
					logReportConsume(toReportOtherGet(role, get, 0, event.eventID, event.eventID));
				else 
					logReportConsume(toReportPropsGet(role, get, event.eventID, event.eventID));
				break;
			}
		}
	}
	public void reportCheckPointMission(Role role, int id, boolean ok)
	{
		logReportBase(toReportMission(role, GameData.BI_MISSION_TYPE_MAPCOPY, id, id, ok));
	}
	public void reportBossMission(Role role, int id, boolean ok)
	{
		logReportBase(toReportMission(role, GameData.BI_MISSION_TYPE_BOSS, id, id, ok));
	}
	public void reportPVPMission(Role role, int id, boolean ok)
	{
		logReportBase(toReportMission(role, GameData.BI_MISSION_TYPE_PVP, id, id, ok));
	}
////////////////BI日志字符串拼接
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String generateBIArg(String key, Object value)
	{
		return "|" + key + "{" + value.toString() + "}";
	}

	
	private String toReportUserLogin(Role role)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_login");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("device", role.loginInfo.system.systemHardware))
		.append(generateBIArg("OS", role.loginInfo.system.systemSoftware))
		.append(generateBIArg("MAC", role.loginInfo.system.macAddr.replace(":", "")))
		.append(generateBIArg("login_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("login_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("login_time", GameTime.getTimeStampStr(date)));
		return sb.toString();
	}

	private String toReportRoleNew(Role role)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_role_new");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("rolename", role.name))
		.append(generateBIArg("school", ""))
		.append(generateBIArg("role_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("role_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("role_time", GameTime.getTimeStampStr(date)));
		return sb.toString();
	}

	private String toReportRoleLogin(Role role,int type)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_role_login");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("type", type))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("online_time", GameTime.getTime() - role.lastLoginTime))
		.append(generateBIArg("rolelogin_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("rolelogin_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("rolelogin_time", GameTime.getTimeStampStr(date)));
		return sb.toString();
	}

	private String toReportLevelUp(Role role,int type)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_levelup");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("kingdom", type))
		.append(generateBIArg("levelup_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("levelup_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("levelup_time", GameTime.getTimeStampStr(date)));
		return sb.toString();
	}

	private String toReportOnline(String gameId, int onlineCount)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_online");
		sb.append(generateBIArg("gameid", gameId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("online_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("users", onlineCount))
		.append(generateBIArg("online_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("online_time", GameTime.getTimeStampStr(date)));
		return sb.toString();
	}

	private String toReportPayment(Role role, int payAmount, int diamond, String orderId)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_payment");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("amount", payAmount))
		.append(generateBIArg("currency", "CNY"))
		.append(generateBIArg("val", diamond))
		.append(generateBIArg("transactionid", orderId))
		.append(generateBIArg("payment_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("payment_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("payment_time", GameTime.getTimeStampStr(date)));
		return sb.toString();
	}

	private String toReportConsume(Role role, TLogItem buyItem, int consumesum)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_consume");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("consume_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("consume_sum", consumesum))
		.append(generateBIArg("own_after", role.diamondF + role.diamondR))
		.append(generateBIArg("goodsid", buyItem.id))
		.append(generateBIArg("goodsprice", consumesum/buyItem.changeCount))
		.append(generateBIArg("goodsnum", buyItem.changeCount))
		.append(generateBIArg("consume_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("consume_time", GameTime.getTimeStampStr(date)));
		return sb.toString();
	}

	private String toReportMission(Role role, int type, int level, int id, boolean ok)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_mission");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("mission_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("event_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("event_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("mission_type", type))
		.append(generateBIArg("mission_level", level))
		.append(generateBIArg("event_name", id))
		.append(generateBIArg("event_ID", id))
		.append(generateBIArg("event_OK", ok ? 1 : 2));
		return sb.toString();
	}
	
	////////////////////////////////消耗上报日志/////////////////////////////
	private String toReportPropsGet(Role role, TLogItem getItem, int getway, int getwayclassid)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_props_get");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("get_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("get_sum", getItem.changeCount))
		.append(generateBIArg("own_after", getItem.finalCount))
		.append(generateBIArg("propsid", getItem.id < 0 ? -getItem.id : getItem.id))
		.append(generateBIArg("get_wayid", getway))
		.append(generateBIArg("get_wayclassid", getwayclassid))
		.append(generateBIArg("get_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("get_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("type", getItem.id<0?"unbind":"bind"))
		.append(generateBIArg("extend_1", ""))
		.append(generateBIArg("extend_2", ""));
		return sb.toString();
	}

	private String toReportPropsConsume(Role role, TLogItem consumeItem, int consumeway, int consumewayclassid)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_props_consume");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("consume_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("consume_sum", - consumeItem.changeCount))
		.append(generateBIArg("own_after", consumeItem.finalCount))
		.append(generateBIArg("propsid", consumeItem.id < 0 ? -consumeItem.id : consumeItem.id))
		.append(generateBIArg("consume_wayid", consumeway))
		.append(generateBIArg("consume_wayclassid", consumewayclassid))
		.append(generateBIArg("consume_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("consume_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("extend_1", ""))
		.append(generateBIArg("extend_2", ""));
		return sb.toString();
	}

	private String toReportGoldGet(Role role, TLogItem getItem, int poundage, int getway, int getwayclassid, boolean isbind)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_gold_get");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("gold_sum", getItem.changeCount))
		.append(generateBIArg("gold_total", getItem.finalCount))
		.append(generateBIArg("poundage", poundage))
		.append(generateBIArg("get_wayid", isbind ? getway : -getway))
		.append(generateBIArg("get_wayclassid", getwayclassid))
		.append(generateBIArg("get_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("get_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("get_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("extend_1", ""))
		.append(generateBIArg("extend_2", ""));
		return sb.toString();
	}

	private String toReportGoldConsume(Role role, TLogItem consumeItem, TLogItem buyItem, int poundage, int consumeway, int consumewayclassid, int consumesum, boolean isbind)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_gold_consume");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("gold_sum", -consumeItem.changeCount))
		.append(generateBIArg("gold_total", consumeItem.finalCount))
		.append(generateBIArg("poundage", poundage))
		.append(generateBIArg("consume_wayid", isbind ? consumeway : -consumeway))
		.append(generateBIArg("consume_wayclassid", consumewayclassid))
		.append(generateBIArg("goodsid", buyItem != null ? buyItem.id : consumewayclassid))
		.append(generateBIArg("goodsprice", buyItem !=null ? consumesum / buyItem.changeCount : consumesum))
		.append(generateBIArg("goodsnum", buyItem != null ? buyItem.changeCount : 1))
		.append(generateBIArg("consume_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("consume_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("consume_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("extend_1", ""))
		.append(generateBIArg("extend_2", ""));
		return sb.toString();
	}

	private String toReportBindDiamondGet(Role role, TLogItem getItem, int getway, int getwayclassid)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_blue_diamond_get");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("bdiamond_sum", getItem.changeCount))
		.append(generateBIArg("bdiamond_total", getItem.finalCount))
		.append(generateBIArg("bdiamond_wayid", getway))
		.append(generateBIArg("bdiamond_wayclassid", getwayclassid))
		.append(generateBIArg("get_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("get_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("get_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("extend_1", ""))
		.append(generateBIArg("extend_2", ""));
		return sb.toString();
	}

	private String toReportDiamondGet(Role role, TLogItem getItem, int poundage, int getway, int getwayclassid)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_yellow_diamond_get");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("diamond_sum", getItem.changeCount))
		.append(generateBIArg("diamond_total", getItem.finalCount))
		.append(generateBIArg("poundage", poundage))
		.append(generateBIArg("diamond_wayid", getway))
		.append(generateBIArg("diamond_wayclassid", getwayclassid))
		.append(generateBIArg("get_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("get_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("get_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("extend_1", ""))
		.append(generateBIArg("extend_2", ""));
		return sb.toString();
	}

	private String toReportDiamondConsume(Role role, TLogItem consumebItem, TLogItem consumefbItem, TLogItem buyItem, int poundage, int consumeway, int consumewayclassid)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_consume_d");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("consume_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("consume_sum_b", -consumebItem.changeCount))
		.append(generateBIArg("own_after_b", consumebItem.finalCount))
		.append(generateBIArg("consume_sum_fb", -consumefbItem.changeCount))
		.append(generateBIArg("own_after_fb", consumefbItem.finalCount))
		.append(generateBIArg("poundage", poundage))
		.append(generateBIArg("goodsid", buyItem != null ? buyItem.id : consumewayclassid))
		.append(generateBIArg("goodsprice", buyItem != null ? -(consumebItem.changeCount + consumefbItem.changeCount) / buyItem.changeCount : -(consumebItem.changeCount + consumefbItem.changeCount)))
		.append(generateBIArg("goodsnum", buyItem != null ? buyItem.changeCount : 1))
		.append(generateBIArg("consume_wayid", consumeway))
		.append(generateBIArg("consume_wayclassid", consumewayclassid))
		.append(generateBIArg("consume_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("consume_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("extend_1", ""))
		.append(generateBIArg("extend_2", ""));
		return sb.toString();
	}

	private String toReportOtherGet(Role role, TLogItem getItem, int poundage, int getway, int getwayclassid)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_other_get");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("other_sum", getItem.changeCount))
		.append(generateBIArg("other_total", getItem.finalCount))
		.append(generateBIArg("poundage", poundage))
		.append(generateBIArg("get_wayid", getway))
		.append(generateBIArg("get_wayclassid", getwayclassid))
		.append(generateBIArg("get_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("get_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("get_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("currency_type", getItem.id))
		.append(generateBIArg("extend_1", ""));
		return sb.toString();
	}

	private String toReportOtherConsume(Role role, TLogItem consumeItem, TLogItem buyItem, int poundage, int consumeway, int consumewayclassid, int consumesum)
	{
		Date date = new Date();
		StringBuilder sb = new StringBuilder("BI_other_consume");
		sb.append(generateBIArg("IP", role.loginInfo.system.loginIP))
		.append(generateBIArg("gameid", gs.getConfig().biGameAppId))
		.append(generateBIArg("clientid", gs.getConfig().id))
		.append(generateBIArg("snid", role.getChannel()))
		.append(generateBIArg("openid", role.getChannelOpenId()))
		.append(generateBIArg("roleid", role.id))
		.append(generateBIArg("level", role.level))
		.append(generateBIArg("vip_level", role.share.getVipLevel()))
		.append(generateBIArg("other_sum", -consumeItem.changeCount))
		.append(generateBIArg("other_total", consumeItem.finalCount))
		.append(generateBIArg("poundage", poundage))
		.append(generateBIArg("consume_wayid", consumeway))
		.append(generateBIArg("consume_wayclassid", consumewayclassid))
		.append(generateBIArg("goodsid", buyItem != null ? buyItem.id : consumewayclassid))
		.append(generateBIArg("goodsprice", buyItem !=null ? consumesum / buyItem.changeCount : consumesum))
		.append(generateBIArg("goodsnum", buyItem != null ? buyItem.changeCount : 1))
		.append(generateBIArg("consume_timestamp", GameTime.getGMTTime(date)))
		.append(generateBIArg("consume_date", GameTime.getDateStampStr(date)))
		.append(generateBIArg("consume_time", GameTime.getTimeStampStr(date)))
		.append(generateBIArg("currency_type", consumeItem.id))
		.append(generateBIArg("extend_1", ""));
		return sb.toString();
	}

}