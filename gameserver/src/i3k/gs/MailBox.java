
package i3k.gs;

import i3k.DBMailBox;
import i3k.SBean;
import i3k.util.GameTime;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import ket.util.Stream;



public class MailBox
{
	public enum SysMailType
	{
		Drop(SBean.DBMail.eTypeTmpDrop),//additionalInfo：副本ID
		Task(SBean.DBMail.eTypeTmpTask),//additionalInfo：任务类型, 任务组ID, 任务ID
		User(SBean.DBMail.eTypeUser),
		World(SBean.DBMail.eTypeSysWorld),
		GM(SBean.DBMail.eTypeSysGM),
		SectMap(SBean.DBMail.eTypeSysSectMap),//additionalInfo：奖励道具ID
		ArenaMap(SBean.DBMail.eTypeSysArenaMap),//additionalInfo：竞技场Rank
		AuctionBack(SBean.DBMail.eTypeSysAuctionBack),//additionalInfo：寄售的道具ID
		AuctionSale(SBean.DBMail.eTypeSysAuctionSale),//additionalInfo：寄售的道具ID, 卖出的钱
		QuizActivity(SBean.DBMail.eTypeSysQuizActivity),//additionalInfo：全服答题排名Rank, 全服答题积分
		BWArenaMap(SBean.DBMail.eTypeSysBWArenaMap),//additionalInfo：排行榜上成员roleID, 竞技场Rank,正邪
		SectDeliverFail(SBean.DBMail.eTypeSysSectDeliverFail),
		FriendRename(SBean.DBMail.eTypeSysFriendRename),	//content: oldName|newName
		BeRewrite(SBean.DBMail.eTypeSysBeRewrite),	//content: 
		Marriage(SBean.DBMail.eTypeSysMarriage),	//content: partnerName
		SectGroupMapFinish(SBean.DBMail.eTypeSysSectGroupMapFinish),	//additionalInfo: 完成进度
		SectGroupMapQuickFinish(SBean.DBMail.eTypeSysSectGroupMapQuickFinish),	//additionalInfo: 完成时间, 完成进度
		SectGroupMapPerson(SBean.DBMail.eTypeSysSectGroupMapPerson),	//additionalInfo: 完成进度, 伤害排名
		SectGroupMapQuickPerson(SBean.DBMail.eTypeSysSectGroupMapQuickPerson),	//additionalInfo: 完成时间, 伤害排名, 完成进度
		VipMissVitGet(SBean.DBMail.eTypeSysVipMissVitGet),	//additionalInfo: 时间戳, 对应日常任务类型
		ChiefTimeout(SBean.DBMail.eTypeSysChiefTimeout),	//content: 离线天数|新帮主名称
		GetSectChiefByTimeout(SBean.DBMail.eTypeSysGetSectChiefByTimeout),	//additionalInfo: 
		SectFlagReward(SBean.DBMail.eTypeSysSectFlagReward),	//additionalInfo: mapId, 奖励时间
		SectFlagEndReward(SBean.DBMail.eTypeSysSectFlagEndReward),	//additionalInfo: mapId
		GroupBuyReturn(SBean.DBMail.eTypeSysGroupBuyReturn),	//additionalInfo: 物品ID,原价,最终价格,购买数量,返回的差价
		SteleReward(SBean.DBMail.eTypeSysSteleReward),			//additionalInfo: rank
		EmergencyReward(SBean.DBMail.eTypeSysEmergencyReward),			//additionalInfo: rank
		DivoceItems(SBean.DBMail.eTypeSysDivoceItems),			//content: partnerName
		LegendMake(SBean.DBMail.eTypeSysLegendMake),
		YYBGift(SBean.DBMail.eTypeSysYYBGift),
		MasterAccept(SBean.DBMail.eTypeSysMasterAccept),	//content: masterID|masterName;
		MasterRefuseGraduate(SBean.DBMail.eTypeSysMasterRefuseGraduate),	//content: masterID|masterName;
		MasterGraduateMasterReward(SBean.DBMail.eTypeSysMasterGraduateMasterReward),	//content: apprenticeID|apprenticeName;
		MasterGraduateApprenticeReward(SBean.DBMail.eTypeSysMasterGraduateApprenticeReward),	//content: masterID|masterName;
		MasterDismiss(SBean.DBMail.eTypeSysMasterDismiss),	//content: masterID|masterName;
		PayRankReward(SBean.DBMail.eTypeSysPayRankReward),  //content: rankIndex
		PayRankTip(SBean.DBMail.eTypeSysPayRankTip),  //content: rankIndex
		ConsumeRankReward(SBean.DBMail.eTypeSysConsumeRankReward),  //content: rankIndex
		ConsumeRankTip(SBean.DBMail.eTypeSysConsumeRankTip);  //content: rankIndex

		private byte value;
		SysMailType(byte value)
		{
			this.value = value;
		}
	}
	public static int MAX_RESERVE_TIME = 86400 * 7;
	public static int TEMP_MAIL_MAX_RESERVE_TIME = 86400 * 3;
	public static int SECTMAP_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int ARENAMAP_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int BWARENAMAP_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int AUCTIONMAP_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int QUIZACTIVITY_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int SECT_DELIVER_FAIL_MAIL_MAX_RESERVE_TIME = 86400 * 3;
	public static int FRIEND_RENAME_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int BE_REWRITE_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int MARRIAGE_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int SECT_GROUP_MAP_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int GROUPBUY_RETURN_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int VIP_MISS_VIT_GET_MAIL_MAX_RESERVE_TIME = 86400 * 3;
	public static int STELE_REWARD_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int EMERGENCY_REWARD_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int DIVOCE_MAIL_MAX_RESERVE_TIME = 86400 * 7;
	public static int MAX_MAIL_COUNT = 50;
	public static int MAIL_ATTACHMENT_COUNT = 4;
	public MailBox()
	{
	}
	
	public MailBox createNew(int lastSyncWorldMailID)
	{
		this.lastSyncWorldMailID = lastSyncWorldMailID;
		return this;
	}
	
	public MailBox fromDB(DBMailBox mailbox)
	{
		if (mailbox != null)
		{
			this.nextID = mailbox.nextId;
			this.lastSyncWorldMailID = mailbox.lastSyncWorldMailID;
			this.lastSyncTime = mailbox.lastSyncTime;
			this.recvLastMailTime = mailbox.recvLastMailTime;
			this.sysMails = mailbox.sysMails;	
			this.tempMails = mailbox.tempMails;	
			this.delayMails = mailbox.delayedMails;
		}
		return this;
	}
	
	public DBMailBox toBDWithouLock()
	{
		DBMailBox mailbox = new DBMailBox(nextID, this.lastSyncWorldMailID, this.lastSyncTime, this.recvLastMailTime,
		        Stream.clone(this.sysMails), Stream.clone(this.tempMails), Stream.clone(this.delayMails));
		return mailbox;
	}



	public int addGmMail(int lifeTime, String title, String content, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		return addMail(SysMailType.GM.value, 0, "", lifeTime, title, content, att, additionalInfo);
	}

	public int addWorldMail(int lifeTime, String title, String content, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		return addMail(SysMailType.World.value, 0, "", lifeTime, title, content, att, additionalInfo);
	}

	public int addSysMail(SysMailType type, int lifeTime, String content, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		return addMail(type.value, 0, "", lifeTime, "", content, att, additionalInfo);
	}
	
	public int addDelaySysMail(SysMailType type, int sendTime, int lifeTime, String content, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		return addDelayedMail(type.value, 0, "", sendTime, lifeTime, "", content, att, additionalInfo);
	}

	public int addUserMail(int srcId, String srcName, String title, String content)
	{
		return addMail(SysMailType.User.value, srcId, srcName, MAX_RESERVE_TIME, title, content, GameData.emptyList(), GameData.emptyList());
	}
	
	public int addDelayedMail(byte type, int srcId, String srcName, int sendTime, int lifeTime, String title, String content, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		int newID = ++nextID;
		SBean.DBMail mail = new SBean.DBMail(newID, type, (byte)0, srcId, srcName, sendTime, lifeTime, title, content, att, additionalInfo);
		this.delayMails.add(mail);
		return newID;
	}
	
	private int addMail(byte type, int srcId, String srcName, int lifeTime, String title, String content, List<SBean.GameItem> att, List<Integer> additionalInfo)
	{
		int newID = ++nextID;
		int now = GameTime.getTime();
		SBean.DBMail mail = new SBean.DBMail(newID, type, (byte)0, srcId, srcName, now, lifeTime, title, content, att, additionalInfo);
		addMail(mail);
		return newID;
	}
	
	private void addMail(SBean.DBMail mail)
	{
		int now = GameTime.getTime();
		this.recvLastMailTime = now;
		if (mail.type < 0)
		{
			this.tempMails.add(mail);
			doClean(tempMails, now);
		}
		else
		{
			this.sysMails.add(mail);
			doClean(sysMails, now);
		}
	}
	
	public int getLastSyncWorldMailID()
	{
		return this.lastSyncWorldMailID;
	}
	
	public void updateLastSyncWordMailID(int id)
	{
		if (id > this.lastSyncWorldMailID)
			this.lastSyncWorldMailID = id;
	}
	
	public boolean testNewMail()
	{
		return this.lastSyncTime < this.recvLastMailTime;
	}
	
	public boolean triggerDelayedMailSend()
	{
	    int now = GameTime.getTime();
	    boolean triggered = false;
	    //synchronized (delayMails)
	    //{
	        Iterator<SBean.DBMail> interator = delayMails.iterator();
		    while (interator.hasNext())
		    {
		        SBean.DBMail mail = interator.next();
		        if (isMailInvalid(mail, now))
		        {
		            interator.remove();
		            continue;
		        }
		        
		        if (mail.sendTime <= now)
		        {
		            interator.remove();
		            addMail(mail);
		            triggered = true;
		        }
		 //   }
	    }
	    return triggered;
	}
	
	public boolean hasDelayedMail()
	{
	    //synchronized (delayMails)
        //{
		    triggerDelayedMailSend();
		    return delayMails.size()!=0;
        //}
	}
	
	public List<SBean.DBMail> getAllMails(boolean tmpMails)
	{
		int now = GameTime.getTime();
		if (tmpMails)
			doClean(tempMails, now);
		else
			doClean(sysMails, now);
		this.lastSyncTime = now;
		return tmpMails ? tempMails : sysMails;
	}
	
	boolean isTempMailFull()
	{
		return this.tempMails.size() >= MAX_MAIL_COUNT;
	}
	
	private void doClean(List<SBean.DBMail> mails, int now)
	{
		Iterator<SBean.DBMail> it = mails.iterator();
		while (it.hasNext())
		{
			SBean.DBMail m = it.next();
			if (isMailInvalid(m, now))
				it.remove();
		}
		if (mails.size() > MAX_MAIL_COUNT)
			mails.subList(0, mails.size() - MAX_MAIL_COUNT).clear();
	}
	
	public SBean.DBMail searchSysMail(int id)
	{
		return searchMail(this.sysMails, id);
	}
	
	public SBean.DBMail searchTmpMail(int id)
	{
		return searchMail(this.tempMails, id);
	}
	
	private SBean.DBMail searchMail(List<SBean.DBMail> mails, int id)
	{
		int now = GameTime.getTime();
		Iterator<SBean.DBMail> it = mails.iterator();
		while (it.hasNext())
		{
			SBean.DBMail m = it.next();
			if (isMailInvalid(m, now))
				it.remove();
			if (m.id == id)
				return m;
		}
		return null;
	}
	
	public boolean delSysMail(int id)
	{
		return deleteMail(this.sysMails, id);
	}
	
	public boolean delTmpMail(int id)
	{
		return deleteMail(this.tempMails, id);
	}
	
	public boolean deleteMail(SBean.DBMail mail)
	{
		if(mail.type < 0)
			return delTmpMail(mail.id);
		else
			return delSysMail(mail.id);
	}
	
	private static boolean deleteMail(List<SBean.DBMail> mails, int id)
	{
		Iterator<SBean.DBMail> it = mails.iterator();
		while (it.hasNext())
		{
			SBean.DBMail m = it.next();
			if (m.id == id)
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	public static boolean isMailInvalid(SBean.DBMail mail, int now)
	{
		return mail.sendTime + mail.lifeTime < now || (mail.type < 0 && isMailAttachmentTaken(mail));
	}
	
	public static void setMailRead(SBean.DBMail mail)
	{
		mail.state |= SBean.DBMail.eStateRead;
	}
	
	public static void setMailAttachmentTaken(SBean.DBMail mail)
	{
		mail.state |= (SBean.DBMail.eStateRead | SBean.DBMail.eStateAttTaken);
	}
	
	public static boolean isMailRead(SBean.DBMail mail)
	{
		return (mail.state &  SBean.DBMail.eStateRead) != 0; 
	}
	
	public static boolean isMailAttachmentTaken(SBean.DBMail mail)
	{
		return (mail.state &  SBean.DBMail.eStateAttTaken) != 0; 
	}
	
	GameServer gs;
	private int nextID;
	private int lastSyncWorldMailID;
	private int lastSyncTime;
	private int recvLastMailTime;
	private List<SBean.DBMail> sysMails = new ArrayList<>();
	private List<SBean.DBMail> tempMails = new ArrayList<>();
    private List<SBean.DBMail> delayMails = new ArrayList<>();
	
}

