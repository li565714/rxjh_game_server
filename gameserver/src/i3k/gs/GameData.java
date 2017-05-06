package i3k.gs;

import i3k.SBean;
import i3k.SBean.MapSkillCFGS;
import i3k.gmap.BaseRole;
import i3k.gmap.FightProperties;
import i3k.gmap.Skill;
import i3k.util.GVector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;
import ket.util.MD5Digester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GameData
{
	static class EmptyList<E> extends ArrayList<E>
	{

		@Override
		public void add(int index, E element)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c)
		{
			throw new UnsupportedOperationException();
		}

	}
	
	static class EmptyMap<K, V> extends TreeMap<K, V>
	{
		public V put(K key, V value)
		{
			throw new UnsupportedOperationException();
		}

		public void putAll(Map<? extends K, ? extends V> m)
		{
			throw new UnsupportedOperationException();
		}
	    
		@Override
		public V putIfAbsent(K key, V value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V replace(K key, V value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
		{
			throw new UnsupportedOperationException();
		}
	}
	
	static class EmptySet<E> extends TreeSet<E>
	{
		public boolean add(E e)
		{
			throw new UnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends E> c)
		{
			throw new UnsupportedOperationException();
		}
	}
	
	public static final String MAP_CLIENT_TAG = "$$$$$m2c";
	
	public static final int GAME_PLAT_IOS = 0;
	public static final int GAME_PLAT_ANDROID = 1;
	
	public static final int GAME_DAY_REFRESH_TIME = 5;
	
	public static final String GAME_PLAT_IOS_IDENTIFIER = "i";
	public static final String GAME_PLAT_ANDROID_IDENTIFIER = "a";
	
	public static final int MAX_AREA_GS_COUNT = 1000;
	public static final int MAX_GS_ROLE_COUNT = 1000000;
	public static final int MAX_GS_SECT_COUNT = 1000000;
	public static final int MAX_GS_MARRIAGE_COUNT = 1000000;
	
	public static final int MONTHLY_CARD_DAY_SPAN = 30;
	
	public static final String YYB_CHANNEL = "2112";
	
	public static final String GAME_GOD_PAY_ORDER_ID = "godpay";
	public static final int GAME_PAY_GOODS_TYPE_DIAMOND = 1;
	public static final int GAME_PAY_GOODS_TYPE_CARD = 2;
	public static final int GAME_PAY_GOODS_TYPE_GIFT = 3;
	
	
	public static final String GAME_CONF_FILE_EXTENSION_NAME = ".xml";

	static final int MONEY_TYPE_GOLD = 0;
	static final int MONEY_TYPE_DIAMODE = 1;
	
	static final int ROLE_GENDER_MAN = 1;
	static final int ROLE_GENDER_WOMAN = 2;

	public static final byte FORCE_CLOSE_KICK = -1;
	public static final byte FORCE_CLOSE_REPLACE = -2;
	public static final byte FORCE_CLOSE_VERSION_CODE = -3;
	public static final byte FORCE_CLOSE_VERSION_RES = -4;
	
	//称号初始值
	public static final int INIT_TITLESLOT_SIZE = 1;       //默认初始称号槽数量
	public static final int MAX_TITLESLOT_SIZE = 7;       //默认初始称号槽数量

	//NPC位置校验距离误差
	public static final int CHECK_NPC_POS_ERROR = 800;
	
	public static final long ROLE_LEVEL_EXP_MAX = 1L << 45;
	
	// 登录返回值错误码
	public static final byte USERLOGIN_ROLE_OK = 1;
	public static final byte USERLOGIN_OK = 0;
	public static final byte USERLOGIN_NOT_INSERVICE = -1;
	public static final byte USERLOGIN_ID_INVALID = -2;
	public static final byte USERLOGIN_VERIFY_FAILED = -3;
	public static final byte USERLOGIN_ALREADY_LOGIN = -4;
	public static final byte USERLOGIN_NOT_LOGIN = -5;
	public static final byte USERLOGIN_LOAD_USER_FAILED = -6;
	public static final byte USERLOGIN_LOCK_BUSY = -7;
	public static final byte USERLOGIN_ROLE_BANNED = -8;// arg banEndTime, args banReason
	public static final byte USERLOGIN_LOAD_ROLE_FAILED = -9;
	public static final byte USERLOGIN_CLASSTYPE_INVALID = -10;
	public static final byte USERLOGIN_ROLENAME_INVALID = -11;
	public static final byte USERLOGIN_GENDER_INVALID = -12;
	public static final byte USERLOGIN_CREATE_ROLE_NAME_USED = -13;
	public static final byte USERLOGIN_CREATE_ROLE_FAILED = -14;
	public static final byte USERLOGIN_USER_NAME_EMPTY = -15;
	public static final byte USERLOGIN_ZONE_ID_INVALID = -16;
	public static final byte USERLOGIN_GAME_CHANNEL_INVALID = -17;
	public static final byte USERLOGIN_NEED_VERIFY_REGISTER = -18;
	public static final byte USERLOGIN_VERIFY_REGISTER_FAILED = -19;
	public static final byte USERLOGIN_ONLINE_ROLE_FULL = -20;
	public static final byte USERLOGIN_QUEUE_ROLE_FULL = -21;
	
	//登陆激活码相关
	public static final int USERLOGIN_ACTIVE_KEY_ACCOUNT_CHECK_REQUIRE = 1;
	public static final int USERLOGIN_ACTIVE_KEY_VERIFY_SUCCESS = 0;
	public static final int USERLOGIN_ACTIVE_KEY_INVALID = -1;
	public static final int USERLOGIN_ACTIVE_KEY_BATCHID_INVALID = -2;
	public static final int USERLOGIN_ACTIVE_KEY_DB_ERROR = -100;
	public static final int USERLOGIN_ACTIVE_KEY_DB_NOT_CONTAIN_KEY = -1001;
	public static final int USERLOGIN_ACTIVE_KEY_DB_KEY_USED = -1002;

	// 协议错误码成功和失败
	public static final int PROTOCOL_OP_SUCCESS = 1;
	public static final int PROTOCOL_OP_FAILED = 0;
	
	public static final int PROTOCOL_OP_NPC_TO_FAR = -1001;
	
	public static final int PROTOCOL_OP_PAY_SERVER_NOT_GOD_MOD = -1;
	
	public static final int PROTOCOL_OP_BAG_NOT_ENOUGH_CELLS = -1;
	public static final int PROTOCOL_OP_BAG_ROLL_LIFE_MAX_USE = -2;
	
	public static final int PROTOCOL_OP_CONF_CONFIG_CHANGED = -1;
	public static final int PROTOCOL_OP_CONF_MALL_GOODS_RESTRICTION = -2;
	public static final int PROTOCOL_OP_CONF_MALL_GOODS_DISCOUNT = -3;
	public static final int PROTOCOL_OP_CONF_BAG_NEED = -3;

	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_CDKEY_INVALID = -1;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_CDKEY_NOT_UNLOCK = -2;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_CDKEY_NOT_GO_INTO_EFFECT = -3;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_BAG_NOT_ENOUGH_CELLS = -4;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_CHANNEL_NOT_MATCH = -5;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_ROLE_LEVEL_NEED = -6;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_SHORT_CODE_GIFT_NOT_LEFT = -7;
	
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_CDKEY_NOT_FOUND = -1001;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_SELF_ALREADY_USED = -1002;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_ALREADY_USED_BY_OTHER = -1003;
	public static final int PROTOCOL_OP_CONF_GIFT_PACKAGE_ALREADY_USE_SAME_BATCH = -1004;
	
	public static final int PROTOCOL_OP_CONF_PBT_NO_RECORD = -1001;
	public static final int PROTOCOL_OP_CONF_PBT_HAVE_TAKED = -1002;
	public static final int PROTOCOL_OP_CONF_PBT_HAVETAKED_BUTFAIL = -1003;
	public static final int PROTOCOL_OP_CONF_PBT_BEYOND_TIMES = -1004;
	public static final int PROTOCOL_OP_CONF_PBT_NO_SUCH_BATCH = -1005;
	public static final int PROTOCOL_OP_CONF_PBT_BAG_FULL = -1007;
	
	public static final int PROTOCOL_OP_CONF_GIFT_CDKEY_CDKEY_INVALID = -21;
	public static final int PROTOCOL_OP_CONF_GIFT_CDKEY_QUERY_RESULT_INVALID = -22;
	public static final int PROTOCOL_OP_CONF_GIFT_CDKEY_BAG_NOT_ENOUGH_CELLS = -23;
	
	public static final int PROTOCOL_OP_CONF_GROUPBUY_NOT_FOUND 		= -101;
	public static final int PROTOCOL_OP_CONF_GROUPBUY_OVER_BUYENDTIME 	= -102;
	public static final int PROTOCOL_OP_CONF_GROUPBUY_RESTRICTION 		= -103;
	
	public static final int PROTOCOL_OP_CONF_FLASHSALE_NOT_FOUND 		    = -201;
	public static final int PROTOCOL_OP_CONF_FLASHSALE_BEFORE_BUYSTARTTIME 	= -202;
	public static final int PROTOCOL_OP_CONF_FLASHSALE_OVER_BUYENDTIME 	   = -203;
	public static final int PROTOCOL_OP_CONF_FLASHSALE_TIME_LIMIT 	       = -204;
	
	public static final int PROTOCOL_OP_GRASP_SELF_NOT_JOINSECT 		= -1; // 自己已经不在帮派中
	public static final int PROTOCOL_OP_GRASP_MEMBER_NOT_JOINSECT 		= -2; //成员已经不在帮派中
	public static final int PROTOCOL_OP_GRASP_SECT_NOT_EXIST 			= -3; //帮派不存在
	
	public static final byte SECT_INVITE_RESPONSE_TYPE_ACCEPT = 1;
	public static final byte SECT_INVITE_RESPONSE_TYPE_REFUSE = 2;
	public static final byte SECT_INVITE_RESPONSE_TYPE_BUSY   = -1;
	
	//换线
	public static final int PROTOCOL_OP_CHANGE_WORLD_LINE_FULL	= -1;		//当前线人数满
	
	// 帮派相关错误码-1 -- -10 基本错误
	public static final int PROTOCOL_OP_SECT_NOT_JOIN = -1; // 已经不在帮派中
	public static final int PROTOCOL_OP_SECT_NOT_EXIST = -2; // 帮派不存在
	public static final int PROTOCOL_OP_SECT_ALREADY_JOIN = -3; // 已经加入本帮派或其它帮派
	public static final int PROTOCOL_OP_SECT_CREATE_NAME_INVALID = -4; // 创建帮派名字非法
	public static final int PROTOCOL_OP_SECT_CREATE_NAME_USED = -5; // 创建帮派名字已经被使用
	public static final int PROTOCOL_OP_SECT_AUTH_CHECK_FAILED = -6; // 没有权限
	public static final int PROTOCOL_OP_SECT_MEMBERS_FULL = -7; // 帮派已满
	public static final int PROTOCOL_OP_SECT_ALREADY_APPLY = -8; // 已经申请了
	public static final int PROTOCOL_OP_SECT_NOT_IN_APPLY = -9; // 不在申请列表了
	public static final int PROTOCOL_OP_SECT_DEPUTY_FULL = -10; // 副帮主超员了
	public static final int PROTOCOL_OP_SECT_ELDER_FULL = -11; // 长老超员了
	public static final int PROTOCOL_OP_SECT_CREED_INVALID = -12; // 帮派宗旨文本段非法
	public static final int PROTOCOL_OP_SECT_APPLY_FULL = -13; // 帮派申请列表满
	public static final int PROTOCOL_OP_SECT_EXIST_SECT = -14; // 已经在别的帮派了
	public static final int PROTOCOL_OP_SECT_KICK_TIMES_NEED = -15; // 踢人次数已满
	public static final int PROTOCOL_OP_SECT_QQGROUP_TO_LONG = -16; // 帮派QQ群长度超限制
	public static final int PROTOCOL_OP_SECT_SECT_LEVEL_NEED = -17; // 帮派QQ群长度超限制
	public static final int PROTOCOL_OP_SECT_APPLY_TIME_FAILD = -41; // 申请加入帮派，但是出于退帮惩罚时间
	public static final int PROTOCOL_OP_SECT_DIYSKILL_FULL = -42;// 帮派分享的自创武功数量达到上限
	public static final int PROTOCOL_OP_SECT_APPOINT_FAILED = -43; //只能由帮主转给副帮主
	public static final int PROTOCOL_OP_LEVEL_UPPER_THAN_MEMBER = -44; //膜拜等级超过成员等级
	public static final int PROTOCOL_OP_SECT_MAP_NOT_IN_TIME = -45; //帮派副本不在开启时间段
	public static final int PROTOCOL_OP_SECT_BEINVITE_ROLE_NOT_ONGAME = -46; //被邀请角色不在线
	public static final int PROTOCOL_OP_SECT_INVITE_ROLE_NOT_EXIST = -47; //邀请人不属于帮派成员
	public static final int PROTOCOL_OP_SECT_INVITE_IS_INVALID = -48; //邀请已失效
	public static final int PROTOCOL_OP_SECT_ROLE_LEVEL_NEED = -49; // 角色等级不足
	public static final int PROTOCOL_OP_SECT_MAIL_CONTENT_INVALID = -50; // 帮派邮件内容非法

	// 帮派基础功能错误码-20---30
	public static final int PROTOCOL_OP_SECT_BANQUET_FULL = -20; // 帮派请客吃饭参加人员已满
	public static final int PROTOCOL_OP_SECT_BANQUET_EXIST = -21; //已经吃过
	public static final int PROTOCOL_OP_SECT_BANQUET_CD = -22; //超时
	public static final int PROTOCOL_OP_SECT_BANQUET_TYPEERROR = -24; //类型错误
	public static final int PROTOCOL_OP_SECT_BANQUET_ROLECOUNT = -25; //每个人吃的次数
	// 帮派副本相关错误码 -30---40
	public static final int PROTOCOL_OP_SECT_BEING_ATTACK_MAP = -30; // 正在打帮派副本
	public static final int PROTOCOL_OP_SECT_MAP_END = -31; // 当前帮派副本已经结束，需要重置
	public static final int PROTOCOL_OP_SECT_GROUP_MAP_MEMBER_NEED = -32; // 当前帮派副本成员前置条件不足
	public static final int PROTOCOL_OP_SECT_MAP_PRE_MAP_NEED = -33; // 当前帮派副本未完成前置副本
	public static final int PROTOCOL_OP_SECT_GROUP_MAP_CD = -34; // 当前帮派副本处于冷却中
	public static final int PROTOCOL_OP_SECT_GROUP_MAP_NOT_END = -35; // 当前帮派副本还未结束
	public static final int PROTOCOL_OP_SECT_GROUP_MAP_COST_NEED = -36; // 当前帮派副本消耗物品不足
	public static final int PROTOCOL_OP_SECT_BEING_ATTACK_GROUP_MAP = -37; // 正在打帮派团队副本
	public static final int PROTOCOL_OP_SECT_EARLY_START_GROUP_MAP = -38; // 已有正在开启的团队副本
	// 帮派运镖相关错误码 -50---60
	public static final int PROTOCOL_OP_SECT_HELP_IS_DELIVER = -50; //正在运镖无法援助他人
	public static final int PROTOCOL_OP_SECT_HELP_IS_DEAD = -51; //角色已死亡
	public static final int PROTOCOL_OP_SECT_HELP_TARGET_INVALID = -52; //目标失效，已结束运镖
	public static final int PROTOCOL_OP_SECT_HELP_IS_NOT_WORLDMAP = -53; //不在大地图无法援助
	// 帮派任务错误码 -60---70
	public static final int PROTOCOL_OP_SECT_SHARE_TASK_UN_USEABLE = -60; //共享任务不存在
	public static final int PROTOCOL_OP_SECT_SHARE_TASK_TIME_MAX = -61; //共享任务分享次数已达上限

	// 宗门相关错误码
	public static final int PROTOCOL_OP_CLAN_EXIST = -1; // 宗门已经存在
	public static final int PROTOCOL_OP_CLAN_NOTADD = -2; // 没有加入宗门
	public static final int PROTOCOL_OP_CLAN_FULL = -3; // 宗门成员已满
	public static final int PROTOCOL_OP_CLAN_NOTFIND = -4; // 没有这个宗门
	public static final int PROTOCOL_OP_CLAN_MEMEXIST = -5; // 已经加入这个宗门，不能申请
	public static final int PROTOCOL_OP_CLAN_ADDFULL = -6; // 加入帮派已满
	public static final int PROTOCOL_OP_CLAN_NOTAUTH = -7; // 没有权限
	public static final int PROTOCOL_OP_CLAN_KICKSIZE = -8; // 宗主每天踢5个人
	public static final int PROTOCOL_OP_CLAN_ZXLLACK = -9; // 执行力不足
	public static final int PROTOCOL_OP_CLAN_APPLIED = -10; // 已经申请过
	public static final int PROTOCOL_OP_CLAN_ELDERFULL = -11; // 长老满了
	public static final int PROTOCOL_OP_CLAN_LEVEL = -12; // 等级不够
	public static final int PROTOCOL_OP_CLAN_CDTIME = -13; // 冷却时间
	public static final int PROTOCOL_OP_CLAN_DZLACK = -14; // 弟子不够
	public static final int PROTOCOL_OP_CLAN_ITEMLACK = -15; // 道具不够
	public static final int PROTOCOL_OP_CLAN_JYDZLACK = -16; // 精英弟子不够
	public static final int PROTOCOL_OP_CLAN_ONE_NOTOPEN = -17; // 矿未开启
	public static final int PROTOCOL_OP_CLAN_PRESTIGE = -18; // 声望不够
	public static final int PROTOCOL_OP_CLAN_NOT_MEET_ENEMY = -19; // 没遇到敌人
	public static final int PROTOCOL_OP_CLAN_MEET_ENEMY = -20; // 遇到敌人，开始战斗
	public static final int PROTOCOL_OP_CLAN_TIMEOUT = -21; // 超时
	public static final int PROTOCOL_OP_CLAN_DIAMOND = -22; // 钻石不够
	public static final int PROTOCOL_OP_TASMCOUNT_NOT = -23; // 任务次数不够
	public static final int PROTOCOL_OP_CLANFULL_NOT = -24; // 最多加入3个宗门
	public static final int PROTOCOL_OP_CREATE_CLAN = -25; // 已经创建了宗门
	
	public static final int PROTOCOL_OP_CLAN_JYDZEXP = -26; //精英弟子经验不够
	public static final int PROTOCOL_OP_CLAN_JYDZRATE = -27; //冲关概率失败
	public static final int PROTOCOL_OP_CLAN_WX = -28; //冲关悟性不足
	public static final int PROTOCOL_OP_CLAN_COUNT = -29; //次数不足
	public static final int PROTOCOL_OP_CLAN_ROLELEVEL = -30; //角色等级不够
	public static final int PROTOCOL_OP_CLAN_LEVELFULL = -31; //宗门等级满
	public static final int PROTOCOL_OP_CLAN_ADDTYPE = -32; //宗门加入类型错误
	
	//宗门战相关
	public static final int PROTOCOL_OP_CLAN_BATTLE_SEEKHELP_FIN = -40; //宗门战求援， 已经求过了。
	public static final int PROTOCOL_OP_CLAN_BATTLE_CD_FIN = -41; 		//宗门战，时间已经完了
	public static final int PROTOCOL_OP_CLAN_BATTLE_HELPCOUNT_FIN = -42; 		//宗门战，今天支援次数完成
	public static final int PROTOCOL_OP_CLAN_BATTLE_HELP_LATE = -43; 		//宗门战，帮助来晚了
	public static final int PROTOCOL_OP_CLAN_BATTLE_HELP_OFFLINE = -44; 		//宗门战，支援，攻击方不在线
	public static final int PROTOCOL_OP_CLAN_ORE_SIZES = -45; 		//宗门占矿次数错误
	public static final int PROTOCOL_OP_CLAN_ORE_OREEND = -46; 		//宗门占矿结束
	public static final int PROTOCOL_OP_CLAN_BATTLEING = -47; 		//参与宗门中不能迁移
	public static final int PROTOCOL_OP_CLAN_SEEK_ENEMY_CANCEL = -48;//侦查敌情，攻击方撤退了。
	public static final int PROTOCOL_OP_CLAN_SEEK_TIMEOUT = -49;//侦查，攻击状态已经超时
	public static final int PROTOCOL_OP_CLAN_BATTLE_HELP_TIMEOUT = -50;// 超过了发起支援的最小时间
	public static final int PROTOCOL_OP_NEED_DIAMOND_FAILD = -51;//侦查消耗元宝不足
	public static final int PROTOCOL_OP_CLAN_BATTLE_CANCEL = -52;//宗门战支援时查询role发现撤退了
	public static final int PROTOCOL_OP_CLAN_BATTLE_HELPED = -53;//支援战有人支援过了
	public static final int PROTOCOL_OP_CLAN_BATTLE_HELPED_EDFENDED = -54 ; //支援战，我正处于防守状态。
	
	// 竞技场相关错误码
	public static final int PROTOCOL_OP_ARENA_RANKCHANGE = -1; // 选中的玩家或者自己的名次已经发生变化
	public static final int PROTOCOL_OP_ARENA_INFOOUT = -2; // 选中的玩家信息失效
	public static final int PROTOCOL_OP_ARENA_PETNOTFIND = -3; // 佣兵信息错误
	public static final int PROTOCOL_OP_ARENA_COOLTIME = -4; // 挑战冷却时间
	public static final int PROTOCOL_OP_ARENA_NOTIMES = -5; // 没有挑战次数
	public static final int PROTOCOL_OP_ARENA_NOTINWORLD = -6; // 在副本或者房间
	public static final int PROTOCOL_OP_ARENA_MAPFAILED = -7; // 创建地图失败
	public static final int PROTOCOL_OP_ARENA_TARGET_IN_BATTLE = -8; //正在战斗

	//拍卖行相关错误码
	public static final int PROTOCOL_OP_AUCTION_NOT_EXSIT 		= -1; //商品已下架
	public static final int PROTOCOL_OP_AUCTION_ALREAD_BUY 		= -2; //商品已被购买
	public static final int PROTOCOL_OP_AUCTION_LACK_CREDIT 	= -3; //商誉值不足
	
	//藏宝图相关错误码
	public static final int PROTOCOL_OP_TREASURE_BAG_FULL 	= -1; //背包满
	
	//装备升星
	public static final int PROTOCOL_OP_EQUIP_UP_STAR_RATE_FAIL 	= -1; //概率失败
	
	//传送点
	public static final int WAYPOINT_FUNC_NORMAL 		= 0; //普通传送点
	public static final int WAYPOINT_FUNC_EMERGENCY 	= 1; //江湖告急活动传送点
	
	
	//好友相关错误码
	public static final int PROTOCOL_OP_FRIEND_REPEAT 		= -1; //已经送过
	public static final int PROTOCOL_OP_NOT_FRIEND 			= -2; //已经不是好友
	public static final int PROTOCOL_OP_FRIEND_FULL 		= -3; //好友已满
	public static final int PROTOCOL_OP_NOT_EACH_FRIEND 	= -4; //不是互为好友
	public static final int PROTOCOL_OP_IN_BLACK_LIST	 	= -5; //在黑名单中
	public static final int PROTOCOL_OP_IN_ROLE_BLACK_LIST	= -6; //在对方黑名单中
	
	//信件相关错误码
	public static final int PROTOCOL_OP_LETTER_CAN_NOT_USE 			= -101; //信件无法使用
	public static final int PROTOCOL_OP_LETTER_USETIME_MAX 			= -102; //物品到达最大使用次数
	public static final int PROTOCOL_OP_LETTER_EARLY_HAS_TASK 		= -103; //已经拥有或完成了该信件任务
	public static final int PROTOCOL_OP_LETTER_CAN_NOT_ACCESS_TASK 	= -104; //无法接取任务
	
	//留言板相关错误码
	public static final int PROTOCOL_OP_MESSAGE_BOARD_USED 			= -1; //留言板已使用
	public static final int PROTOCOL_OP_MESSAGE_BOARD_UN_USED 		= -2; //留言板未使用，无需覆盖
	public static final int PROTOCOL_OP_MESSAGE_BOARD_TIME_INVALID 	= -3; //时间无效，或覆盖时间小于现有时间
	public static final int PROTOCOL_OP_MESSAGE_BOARD_NOT_ENOUGH_MONEY 	= -4; //货币不足
	public static final int PROTOCOL_OP_MESSAGE_BOARD_EARLY_COMMONT 	= -5; //已经评论过该条留言
	public static final int PROTOCOL_OP_MESSAGE_BOARD_IS_NOT_YOUR 	= -6; //修改的不是自己的留言
	public static final int PROTOCOL_OP_MESSAGE_BOARD_CONTENT_NOT_VALID = -7; //文本超过最大长度或包含敏感字
	public static final int PROTOCOL_OP_MESSAGE_BOARD_NEED_COMMONT_TIME = -8; //评论次数不够
	public static final int PROTOCOL_OP_MESSAGE_BOARD_INVAILD 		= -9; //留言板无效
	public static final int PROTOCOL_OP_MESSAGE_BOARD_LEVEL_NEED	= -10; //未达到等级限制
	public static final int PROTOCOL_OP_MESSAGE_BOARD_HAS_CHANGED	= -11; //留言板已修改
	
	//活动补做相关错误码
	public static final int PROTOCOL_OP_ACTIVITY_LAST_SEQ_ERROR			= -1; //错误的序号
	public static final int PROTOCOL_OP_ACTIVITY_LAST_TIMES_NEED 		= -2; //补做次数不足
	public static final int PROTOCOL_OP_ACTIVITY_LAST_ITEM_NEED 		= -3; //物品不足
	public static final int PROTOCOL_OP_ACTIVITY_LAST_MAP_CONDITION_NEED	= -4; //地图未参与或未通关过
	
	//幸运星相关错误码
	public static final int PROTOCOL_OP_LUCKLYSTAR_LEVEL_NEED			= -1; //等级不足
	public static final int PROTOCOL_OP_LUCKLYSTAR_BAG_FULL		 		= -2; //背包已满
	public static final int PROTOCOL_OP_LUCKLYSTAR_NO_TIME_LEFT 		= -3; //赠送次数不足
	public static final int PROTOCOL_OP_LUCKLYSTAR_TAR_LEVEL_NEED		= -4; //目标等级不足
	public static final int PROTOCOL_OP_LUCKLYSTAR_TAR_ALREADY_RECV		= -5; //目标已经接受过幸运星
	public static final int PROTOCOL_OP_LUCKLYSTAR_TAR_OFFLINE			= -6; //目标已经离线
	
	public static final byte ARMOR_UNLOCK_STATUS_LOCKED = 0;
	public static final byte ARMOR_UNLOCK_STATUS_UNLOCK = 1;
	
	public static final byte ARMOR_TALENT_TYPE_PROP		= 1;
	public static final byte ARMOR_TALENT_TYPE_SKILL	= 2;
	
	public static final byte ARMOR_RUNE_LANG_SIZE		= 6;
	
	public static final byte WEAPON_TALENT_TYPE_PROP		= 1;
	public static final byte WEAPON_TALENT_TYPE_SKILL		= 2;
	
	public static final int WEAPON_USKILL_TYPE_ITEM						= 1;		//赠送道具
	public static final int WEAPON_USKILL_TYPE_WITHPET_PROP	 			= 2;		//随从与神兵均出战时，提升属性
	public static final int WEAPON_USKILL_TYPE_KILL_EXP					= 3;		//杀怪经验增加
	public static final int WEAPON_USKILL_TYPE_KILL_DROP				= 4;		//杀怪有几率多倍掉落
	public static final int WEAPON_USKILL_TYPE_USKILL	 	 			= 5;		//赠送绝技
	public static final int WEAPON_USKILL_TYPE_INSIGHT	 	 			= 6;		//开启洞察功能
	public static final int WEAPON_USKILL_TYPE_REVENGE	 	 			= 7;		//开启追仇功能
	public static final int WEAPON_USKILL_TYPE_WITHHOURSE_PROP 	 		= 8;		//坐骑与神兵同时出战，提升属性
	public static final int WEAPON_USKILL_TYPE_ADD_AI 	 				= 9;		//挂载AI触发公式
	public static final int WEAPON_USKILL_TYPE_SET_FORM 	 			= 10;		//解锁进阶形态
	public static final int WEAPON_USKILL_TYPE_UP_CRYSTAL_EQUIP 		= 11;		//提升获取水晶装备的概率
	public static final int WEAPON_USKILL_TYPE_REDUCE_FIX_EQUIP_PRICE 	= 12;		//减少水晶装备的修理价格
	public static final int WEAPON_USKILL_TYPE_ENTER_MAPCOPY 			= 13;		//开启神兵特技副本
	
	public static final int WEAPON_USKILL_EFFECT_TYPE_OPEN		= 1;	//激活绝技时执行
	public static final int WEAPON_USKILL_EFFECT_TYPE_ALL_TIME	= 2;	//一直生效
	public static final int WEAPON_USKILL_EFFECT_TYPE_EQUIP		= 3;	//装备神兵后生效
	public static final int WEAPON_USKILL_EFFECT_TYPE_MOTIVATE	= 4;	//装备神兵并变身后生效
	
	public static final byte WEAPON_FORM_DEFAULT 	= 1;		//默认形态
	public static final byte WEAPON_FORM_RANK 		= 2;		//进阶形态
	
	public static final int WEAPON_MOTIVATE_START 	= 1;		//进入变身
	public static final int WEAPON_MOTIVATE_END 	= 2;		//结束变身
	public static final int WEAPON_MOTIVATE_ALL 	= 3;		//全部
	
	//内甲相关错误码
	public static final int PROTOCOL_OP_ARMOR_NEED_UNLOCK_CONDITION = -1; //解锁条件不满足
	public static final int PROTOCOL_OP_ARMOR_NOT_ENOUGH_ITEM		= -2; //没有足够的物品
	public static final int PROTOCOL_OP_ARMOR_AREADY_UNLOCK			= -3; //内甲已经解锁了
	public static final int PROTOCOL_OP_ARMOR_NOT_UNLOCK			= -4; //内甲还未解锁
	public static final int PROTOCOL_OP_ARMOR_TYPE_INVALID			= -5; //类型无效
	public static final int PROTOCOL_OP_ARMOR_RANK_MAX				= -6; //内甲已达到最大阶位
	public static final int PROTOCOL_OP_ARMOR_NO_TALENT_POINT_VALID	= -7; //无可用天赋点
	public static final int PROTOCOL_OP_ARMOR_NEED_TALENT_POINT		= -8; //需要投入更多天赋点
	public static final int PROTOCOL_OP_ARMOR_NEED_PRE_TALENT		= -9; //未达到前置天赋条件
	public static final int PROTOCOL_OP_ARMOR_TALENT_MAX_LEVEL		= -10; //天赋已达到最高级别
	public static final int PROTOCOL_OP_ARMOR_NOT_ENOUGH_RUNE		= -11; //符文背包内符文数量不足
	public static final int PROTOCOL_OP_ARMOR_NOT_USE_TALENT_POINT	= -12; //未使用天赋无需重置
	public static final int PROTOCOL_OP_ARMOR_SLOT_AREADY_UNLOCK	= -13; //插槽已经解锁了
	public static final int PROTOCOL_OP_ARMOR_SLOT_NOT_UNLOCK		= -14; //插槽还未解锁
	public static final int PROTOCOL_OP_ARMOR_SLOT_GROUP_INDEX_INVALID	= -15; //插槽组序号无效
	public static final int PROTOCOL_OP_ARMOR_SLOT_INDEX_INVALID	= -16; //插槽序号无效
	public static final int PROTOCOL_OP_ARMOR_RUNE_ID_INVALID		= -17; //符文ID无效
	public static final int PROTOCOL_OP_ARMOR_RUNE_HAS_MUTEX		= -18; //已装备互斥符文
	public static final int PROTOCOL_OP_ARMOR_LANG_ID_UNVALID		= -19; //符文之语ID无效
	public static final int PROTOCOL_OP_ARMOR_RUNE_NOT_ENOUGH		= -20; //符文不足
	public static final int PROTOCOL_OP_ARMOR_WISH_NUM_INVALID		= -21; //符文祝福投入的符文数量不正确

	public static final int ARMOR_RANK_UP_SUCCESS		= 1; //内甲升阶成功
	public static final int ARMOR_RANK_UP_FAIL			= 2; //内甲升阶失败
	
	//日程表相关错误码
	
	public static final int PROTOCOL_OP_SCHEDULE_CONDITION_NEED		= -1; //领取条件未达到
	public static final int PROTOCOL_OP_SCHEDULE_BAG_FULL			= -2; //背包已满
	
	//聊天相关错误码
	public static final int PROTOCOL_OP_CHAT_BE_BAN					= -1; //已被禁言
	public static final int PROTOCOL_OP_CHAT_ITEM_NEED				= -2; //所需道具不足
	public static final int PROTOCOL_OP_CHAT_IN_CD					= -3; //处于聊天CD中
	public static final int PROTOCOL_OP_CHAT_TARGET_OUTLINE			= -4; //私聊对象不在线
	public static final int PROTOCOL_OP_CHAT_IN_TARGET_BLACKLIST	= -5; //私聊对象已将你加入黑名单
	public static final int PROTOCOL_OP_CHAT_WRONE_AREA				= -6; //处在错误的区域
	public static final int PROTOCOL_OP_CHAT_VIP_LEVEL_NEED			= -7; //VIP等级不足
	public static final int PROTOCOL_OP_CHAT_TARGET_IN_BLACKLIST	= -8; //私聊在你的黑名单
	public static final int PROTOCOL_OP_CHAT_INPUT_INVALID			= -9; //输入信息有误或带有屏蔽字符
	
	//仓库相关错误码
	public static final int PROTOCOL_OP_WAREHOUSE_BAG_ITEM_NOT_ENOUGH	= -1; //背包道具不足
	public static final int PROTOCOL_OP_WAREHOUSE_BAG_CELL_NOT_ENOUGH	= -2; //背包道具不足
	public static final int PROTOCOL_OP_WAREHOUSE_ITEM_NOT_ENOUGH		= -3; //仓库道具不足
	public static final int PROTOCOL_OP_WAREHOUSE_CELL_NOT_ENOUGH		= -4; //仓库空间不足
	public static final int PROTOCOL_OP_WAREHOUSE_BIND_ITEM				= -5; //绑定道具不能存入仓库
	public static final int PROTOCOL_OP_WAREHOUSE_ALREADY_DIVORCE		= -6; //已经离婚
	
	public static final int WAREHOUSE_TYPE_PRIVATE			= 1; //私人仓库
	public static final int WAREHOUSE_TYPE_MARRIAGE			= 2; //结婚仓库
	
	//结婚相关错误码
	public static final int PROTOCOL_OP_MARRIAGE_LEVEL_NEED					= -1; //等级不足
	public static final int PROTOCOL_OP_MARRIAGE_FRIEND_POINT_NEED			= -2; //魅力值不足
	public static final int PROTOCOL_OP_MARRIAGE_PK_POINT_NEED				= -3; //善恶度过高
	public static final int PROTOCOL_OP_MARRIAGE_SINGEL_NEED				= -4; //双方必须为单身
	public static final int PROTOCOL_OP_MARRIAGE_IN_DIVORCE_CD				= -5; //有一方处于离婚CD中
	public static final int PROTOCOL_OP_MARRIAGE_ITEM_NEED					= -6; //结婚所需道具不足
	public static final int PROTOCOL_OP_MARRIAGE_PROPOSE_INVALID			= -7; //求婚已失效
	public static final int PROTOCOL_OP_MARRIAGE_SAVE_FAILD					= -8; //结婚数据存储失败
	public static final int PROTOCOL_OP_MARRIAGE_STEP_ERROR					= -9; //结婚步骤错误
	public static final int PROTOCOL_OP_MARRIAGE_TO_LATE					= -10; //结婚流程时间已过
	public static final int PROTOCOL_OP_MARRIAGE_SKILL_LEVEL_MAX			= -11; //技能已达到最大等级
	public static final int PROTOCOL_OP_MARRIAGE_MARRIAGE_LEVEL_NEED		= -12; //升级所需的姻缘等级不足
	public static final int PROTOCOL_OP_MARRIAGE_PARTNER_NOT_ONLINE			= -13; //配偶不在线或不在大地图
	public static final int PROTOCOL_OP_MARRIAGE_TRANSMIT_IN_CD				= -14; //传送正在冷却中
	public static final int PROTOCOL_OP_MARRIAGE_TOO_FAR_TO_NPC				= -15; //离NPC太远
	public static final int PROTOCOL_OP_MARRIAGE_GRADE_NEED					= -16; //婚礼档次不足
	public static final int PROTOCOL_OP_MARRIAGE_MAKE_FRIEND_NEED			= -17; //结婚需双方互为好友
	public static final int PROTOCOL_OP_MARRIAGE_BESPEAK_INVALID			= -18; //结婚预约无效
	public static final int PROTOCOL_OP_MARRIAGE_BEFORE_BESPEAK_TIME		= -19; //未到预约时间
	public static final int PROTOCOL_OP_MARRIAGE_TIME_IS_TOO_LATE			= -20; //已过预约时间
	public static final int PROTOCOL_OP_MARRIAGE_NO_BESPEAK_LINE			= -21; //不在预约线内
	public static final int PROTOCOL_OP_MARRIAGE_NO_MARRIAGE				= -22; //未结婚
	public static final int PROTOCOL_OP_MARRIAGE_NOT_PARTNER				= -23; //队友不是结婚对象
	public static final int PROTOCOL_OP_MARRIAGE_NO_BESPEAK					= -24; //无预约
	public static final int PROTOCOL_OP_MARRIAGE_NO_PARADE					= -25; //宴席前未进行游街
	public static final int PROTOCOL_OP_MARRIAGE_PUBLIC_IS_NOT_EMPTY		= -27; //公共仓库不是空的
	public static final int PROTOCOL_OP_MARRIAGE_IN_MULROLESTATE			= -28; //多人状态(多人坐骑或者相依相偎)
	
	public static final int MARRIAGE_STEP_FINISH           = 0; //结婚步骤已结束
	public static final int MARRIAGE_STEP_START	           = 1; //结婚步骤开始
	public static final int MARRIAGE_STEP_PARADE	       = 2; //结婚游行
	public static final int MARRIAGE_STEP_BANQUET          = 3; //结婚宴席
	
	public static final int MARRIAGE_OFFLINEEXP_TYPE_SERIES   	= 1; //离线经验发放参数系列任务
	public static final int MARRIAGE_OFFLINEEXP_TYPE_LOOP   	= 2; //离线经验发放参数环任务
	
	public static final int PROTOCOL_OP_MRGTASK_NO_TEAM 			= -1;		//没有队伍
	public static final int PROTOCOL_OP_MRGTASK_TEAM_CNT 			= -2;		//队伍人数非法
	public static final int PROTOCOL_OP_MRGTASK_PARTNER_FAR 		= -3;		//队友离的太远
	public static final int PROTOCOL_OP_MRGTASK_PARTNER_BAG_FULL 	= -4;		//队友背包满
	
	//道具兑换错误码
	public static final int PROTOCOL_OP_EXCHANGE_FAR_FROM_NPC			= -1; //距离NPC太远
	public static final int PROTOCOL_OP_EXCHANGE_ITEM_NEED				= -2; //道具不足
	public static final int PROTOCOL_OP_EXCHANGE_BAG_NEED				= -3; //背包空间不足
	public static final int PROTOCOL_OP_EXCHANGE_NEED_TIMES				= -4; //兑换次数不足
	public static final int PROTOCOL_OP_EXCHANGE_NPC_CAN_NOT_OFFER		= -5; //NPC不提供该种物品的兑换

	//技能预设错误码
	public static final int PROTOCOL_OP_SKILL_PRESET_MAX_SIZE			= -1; //技能预设已达到上限
	public static final int PROTOCOL_OP_SKILL_PRESET_DO_NOT_HAVE		= -2; //未持有当前技能
	public static final int PROTOCOL_OP_SKILL_PRESET_NAME_INVALID		= -3; //名称含有敏感字符

	//神兵技能升级错误码
	public static final int PROTOCOL_OP_WEAPON_SKILL_ITEM_NEED			= -1; //升级所需道具不足
	public static final int PROTOCOL_OP_WEAPON_SKILL_LEVEL_NEED			= -2; //升级所需等级不足
	
	//神兵天赋升级错误码
	public static final int PROTOCOL_OP_WEAPON_TALENT_POINT_NEED			= -1; //天赋点不足
	public static final int PROTOCOL_OP_WEAPON_TALENT_PRE_POINT_NEED		= -2; //升级所需前置天赋点不足
	public static final int PROTOCOL_OP_WEAPON_TALENT_EARLY_MAX_LEVEL		= -3; //天赋已达到最高等级
	public static final int PROTOCOL_OP_WEAPON_TALENT_POINT_MAX				= -4; //天赋点已满
	public static final int PROTOCOL_OP_WEAPON_TALENT_POINT_BUY_NEED_ITEMS	= -5; //购买天赋所需道具不足
	public static final int PROTOCOL_OP_WEAPON_TALENT_POINT_RESET_NEED_ITEMS= -6; //重置天赋所需道具不足
	
	//赠送物品错误码
	public static final int PROTOCOL_OP_GIFT_CAN_NOT_SEND					= -1; //物品无法赠送
	public static final int PROTOCOL_OP_GIFT_ITEM_NEED						= -2; //道具不足
	public static final int PROTOCOL_OP_GIFT_FRIEND_BAG_FULL				= -3; //赠送目标包裹已满
	public static final int PROTOCOL_OP_GIFT_FRIEND_NOT_ONLINE				= -4; //赠送目标不在线

	//采集旗帜错误码
	public static final int PROTOCOL_OP_FLAG_NO_SECT						= -101; //需要加入帮派
	public static final int PROTOCOL_OP_FLAG_MONSTER_CLEAR					= -102; //需要清除怪物
	public static final int PROTOCOL_OP_FLAG_LEVEL_NEED						= -103; //等级不足
	public static final int PROTOCOL_OP_FLAG_SECT_LEVEL_NEED				= -104; //帮派等级不足
	public static final int PROTOCOL_OP_FLAG_JOIN_TIME_NEED					= -105; //进入帮派时间不足
	public static final int PROTOCOL_OP_FLAG_NOT_ACTIVITY_TIME				= -106; //不在活动时间
	public static final int PROTOCOL_OP_FLAG_SELF							= -107; //不能采集己方的旗帜
	public static final int PROTOCOL_OP_FLAG_FLAG_MAX						= -108; //达到最大占领数量

	//采集结婚相关错误码
	public static final int PROTOCOL_OP_MARRIAGE_GIFTBOX_NO_TIMES			= -201;//没有采集结婚游街礼盒的次数
	public static final int PROTOCOL_OP_MARRIAGE_BANQUET_NO_TIMES			= -202;//没有采集结婚宴席的次数
	
	//采集太玄碑文相关错误码
	public static final int PROTOCOL_OP_STELE_NOT_JOIN						= -301;//未参加活动
	public static final int PROTOCOL_OP_STELE_ALL_FINISH					= -302;//已经全部完成
	public static final int PROTOCOL_OP_STELE_NOT_OPEN						= -303;//不在活动时间
	public static final int PROTOCOL_OP_STELE_NO_TIMES						= -304;//太玄碑文，耐久为0
	public static final int PROTOCOL_OP_STELE_NO_FIT						= -305;//太玄碑文不匹配
	
	//随从技能升级错误码
	public static final int PROTOCOL_OP_PET_SKILL_ITEM_NEED				= -1; //升级所需道具不足
	public static final int PROTOCOL_OP_PET_SKILL_LEVEL_NEED			= -2; //升级所需等级不足

	//神兵特技错误码
	public static final int PROTOCOL_OP_U_SKILL_IN_CD				= -1; //特技在CD中
	public static final int PROTOCOL_OP_U_SKILL_HAVE_NOT_SKILL		= -2; //没有该项特技
	public static final int PROTOCOL_OP_U_SKILL_NEED_WORLD_MAP		= -3; //需要在世界地图中
	public static final int PROTOCOL_OP_U_SKILL_TARGET_EMPTY		= -4; //未找到目标
	public static final int PROTOCOL_OP_U_SKILL_SKILL_END			= -5; //技能已结束
	public static final int PROTOCOL_OP_U_SKILL_TAR_INVALID			= -6; //目标位置目前无法传送
	public static final int PROTOCOL_OP_U_SKILL_IN_SEARCH			= -7; //处于追踪状态无法切换神兵

	//传家宝错误码
	public static final int PROTOCOL_OP_HEIRLOOM_CONDITION_NEED				= -1; //前置条件不满足
	public static final int PROTOCOL_OP_HEIRLOOM_PERFECT_MAX				= -2; //完美度已达到最大值
	public static final int PROTOCOL_OP_HEIRLOOM_WIPE_TIME_NEED				= -3; //擦拭次数已用完
	public static final int PROTOCOL_OP_HEIRLOOM_IS_OPEN					= -4; //传家宝已开启
	public static final int PROTOCOL_OP_HEIRLOOM_PERFECT_NEED				= -5; //完美度不足

	//离线精灵错误码
	public static final int PROTOCOL_OP_OFFLINE_WIZARD_BAG_NEED				= -1; //背包不足
	public static final int PROTOCOL_OP_OFFLINE_WIZARD_FUNC_POINT_NEED		= -2; //修炼点不足
	
	//江湖告急错误码
	public static final int PROTOCOL_OP_EMERGENCY_NOT_IN_OPEN_DAY				= -1; //不在开放日
	public static final int PROTOCOL_OP_EMERGENCY_NOT_IN_OPEN_TIME				= -2; //不在开放时间
	public static final int PROTOCOL_OP_EMERGENCY_LEVEL_NEED					= -3; //等级不足
	public static final int PROTOCOL_OP_EMERGENCY_MAP_ROLL_FULL					= -4; //副本人数已满
	public static final int PROTOCOL_OP_EMERGENCY_MAP_AREADY_FINISH				= -5; //副本已完成

	//封测活动类型
	public static final int BETA_ACTIVITY_TYPE_USER_SURVEY				= 1; //有奖调研
	public static final int BETA_ACTIVITY_TYPE_LOGIN_GIFT				= 2; //封测巨献
	public static final int BETA_ACTIVITY_TYPE_LEVEL_UP_GIFT			= 3; //等级冲刺
	public static final int BETA_ACTIVITY_TYPE_LAST_BETA_GIFT			= 4; //最后冲刺
	public static final int BETA_ACTIVITY_TYPE_OFFICIAL_RESEARCH		= 5; //官网调研
	public static final int BETA_ACTIVITY_TYPE_ON_TIME_LOGIN_GIFT		= 6; //限时放送
	public static final int BETA_ACTIVITY_TYPE_STRENG_THEN_GIFT			= 7; //强化比拼
	public static final int BETA_ACTIVITY_TYPE_USER_INFO				= 8; //使命召唤
	
	public final static int MAX_CURRENCY_AMOUNT = 999999999;
	public final static int MAX_NUMBER = 999999999;
	public final static int MAX_CHARM = 999999;
	public final static int MAX_WORLD_MESSAGE_COUNT = 100;
	
	// 商城类型
	public static final int SHOP_TYPE_SECT = 1;
	public static final int SHOP_TYPE_ARENA = 2;
	public static final int SHOP_TYPE_SUPERARENA = 3;
	public static final int SHOP_TYPE_SECT_DELIVER = 4;
	public static final int SHOP_TYPE_MASTER = 5;
	
	// 赌博商城类型
	public static final int GAMBLE_SHOP_TYPE_JUSTICE = 1;
	public static final int GAMBLE_SHOP_TYPE_EVIL = 2;
	
	// 赌博商城前置条件类型
	public static final int GAMBLE_SHOP_CONDITION_FEAT = 1;
	
	public static final int COMMON_ITEM_ID_NULL = 0;
	public static final int COMMON_ITEM_ID_DIAMOND = 1;
	public static final int COMMON_ITEM_ID_COIN = 2;
	public static final int COMMON_ITEM_ID_SECT_MONEY = 3;
	public static final int COMMON_ITEM_ID_ARENA_MONEY = 4;
	public static final int COMMON_ITEM_ID_SUPERARENA_MONEY = 5;
	public static final int COMMON_ITEM_ID_ROB_MONEY = 6;
	public static final int COMMON_ITEM_ID_CREDIT = 7;
	public static final int COMMON_ITEM_ID_MASTER_POINT = 8;
	public static final int COMMON_ITEM_ID_MASTER_REPUTATION = 9;
	public static final int COMMON_ITEM_ID_VIT = 10;
	public static final int COMMON_ITEM_ID_SPLITSP = 11;
	public static final int COMMON_ITEM_ID_EQUIP_ENERGY = 31;
	public static final int COMMON_ITEM_ID_GEM_ENERGY = 32;
	public static final int COMMON_ITEM_ID_BOOK_INSPIRATION = 33;
	public static final int COMMON_ITEM_ID_XUANTIE = 41;
	public static final int COMMON_ITEM_ID_YAOCAO = 42;
	public static final int COMMON_ITEM_ID_EXPCOIN = 43;
	public static final int COMMON_ITEM_ID_OFFLINE_FUNC_POINT = 45;

	public static final int COMMON_ITEM_ID_RESERVED_PLANE = 0;
	public static final int COMMON_ITEM_ID_ITEM_PLANE = 1;
	public static final int COMMON_ITEM_ID_GEM_PLANE = 2;
	public static final int COMMON_ITEM_ID_BOOK_PLANE = 3;
	public static final int COMMON_ITEM_ID_EQUIP_MIN = 10000000;

	public static final int GAME_ITEM_TYPE_DIAMOND = 1;
	public static final int GAME_ITEM_TYPE_COIN = 2;
	public static final int GAME_ITEM_TYPE_EXP = 3;
	public static final int GAME_ITEM_TYPE_GIFT = 4;
	public static final int GAME_ITEM_TYPE_RECIPEREEL = 5;
	public static final int GAME_ITEM_TYPE_HP = 6;
	public static final int GAME_ITEM_TYPE_MATERIAL = 7;
	public static final int GAME_ITEM_TYPE_HPPOOL = 8;
	public static final int GAME_ITEM_TYPE_CHEST = 9;
	public static final int GAME_ITEM_TYPE_TASK_EFFECT = 10;
	public static final int GAME_ITEM_TYPE_WEAPON_EXP = 11;
	public static final int GAME_ITEM_TYPE_PET_EXP = 12;
	public static final int GAME_ITEM_TYPE_EQUIP_ENERGY = 13;
	public static final int GAME_ITEM_TYPE_GEM_ENERGY = 14;
	public static final int GAME_ITEM_TYPE_SPIRIT_INSPIRATION = 15;
	public static final int GAME_ITEM_TYPE_VIT = 16;
	public static final int GAME_ITEM_TYPE_FASHION = 17;
	public static final int GAME_ITEM_TYPE_FLOWER = 18;
	public static final int GAME_ITEM_TYPE_EXPCOIN_POOL = 19;			//历练瓶
	public static final int GAME_ITEM_TYPE_RARE_BOOK 	= 20;			//藏书
	public static final int GAME_ITEM_TYPE_SPECIAL_CARD = 21;			//特权卡体验卡
	public static final int GAME_ITEM_TYPE_VIP_CARD 	= 22;			//VIP体验卡
	public static final int GAME_ITEM_TYPE_TOWER_FAME 	= 23;			//爬塔五绝声望提升道具
	public static final int GAME_ITEM_TYPE_FEAT 		= 24;			//加武勋道具
	public static final int GAME_ITEM_TYPE_SKILL		= 25;			//技能道具
	public static final int GAME_ITEM_TYPE_LETTER		= 26;			//书信
	public static final int GAME_ITEM_TYPE_PIECE		= 27;			//碎片
	public static final int GAME_ITEM_TYPE_ARMOR_EXP_ITEM	= 28;			//内甲升级道具
	public static final int GAME_ITEM_TYPE_RUNE			= 29;			//符文
	public static final int GAME_ITEM_TYPE_EVIL_VALUE	= 30;			//善恶道具
	public static final int GAME_ITEM_TYPE_FIREWORK     = 31;           //烟花
	public static final int GAME_ITEM_TYPE_ENCHANT	    = 32;           //精练消耗
	public static final int GAME_ITEM_TYPE_PROP_STRENGTH   = 33;           //属性强化
	public static final int GAME_ITEM_TYPE_OFFLINE_FUNC_POINT   = 34;           //添加离线修炼点
	public static final int GAME_ITEM_TYPE_TITLE_ITEM   = 35;           //称号道具
	public static final int GAME_ITEM_TYPE_USKILL_ITEM  = 36;           //绝技道具
	public static final int GAME_ITEM_TYPE_HEAD_ITEM  	= 37;           //头像激活道具

	public static final int MAX_STAR_LVL = 24;
	public static final int MAX_TOUGHEN_LVL = 70;

	public static final int SPIRIT_TYPE_JOB = 1;
	public static final int SPIRIT_TYPE_BULIM = 3;
	public static final int SPIRIT_TYPE_GANG = 2;

	public static final int SPIRIT_TYPE_JOB_USE_TOTAL = 1;
	public static final int SPIRIT_TYPE_GANG_USE_TOTAL = 3;
	public static final int SPIRIT_TYPE_BULIM_USE_TOTAL = 1;
	
	public static final int EXTRA_DROP_MONSTER_TYPE_NORMAL			= 1;	//技能特化公式额外掉落怪物类型小怪
	public static final int EXTRA_DROP_MONSTER_TYPE_BOSS			= 2;	//技能特化公式额外掉落怪物类型BOSS
	public static final int EXTRA_DROP_MONSTER_TYPE_ALL				= 3;	//技能特化公式额外掉落怪物类型所有
	public static final int EXTRA_DROP_MONSTER_TYPE_ID				= 4;	//技能特化公式额外掉落怪物类型ID

	public static final int SPIRIT_EFFECT_PROP			= 1;	//增加属性
	public static final int SPIRIT_EFFECT_ADDAI			= 2;	//挂载AI
	public static final int SPIRIT_EFFECT_SKILLPASV		= 3;	//技能变被动
	public static final int SPIRIT_EFFECT_ADDBUFF		= 4;	//挂载BUFF
	public static final int SPIRIT_EFFECT_FIXSUBDAMAGE	= 5;	//改变技能子事件数值
	public static final int SPIRIT_EFFECT_FIXBASESKILL	= 6;	//改变技能常规数值
	public static final int SPIRIT_EFFECT_FIXAI			= 7;	//更改AI触发公式
	
	public static final int FIX_BASESKILL_DURATION		= 1;	//技能时长
	public static final int FIX_BASESKILL_COOLDOWN		= 2;	//冷却时间
	public static final int FIX_BASESKILL_RUSHDISTANCE	= 3;	//更改冲锋距离
	public static final int FIX_BASESKILL_SHIFTODDS		= 4;	//更改击退概率
	public static final int FIX_BASESKILL_GUIDETIME		= 5;	//延长引导技能持续时间
	public static final int FIX_BASESKILL_AURAINFO		= 6;	//更改光环存在时间
	public static final int FIX_BASESKILL_SKILLSPEED	= 7;	//更改阵法移动速度
	
	public static final int FIX_SUBDAMAGE_TRIGTIME		= 1;	//伤害子时间时间点
	public static final int FIX_SUBDAMAGE_ODDS			= 2;	//伤害子事件概率
	public static final int FIX_SUBDAMAGE_MULT			= 3;	//伤害子事件伤害乘数
	public static final int FIX_SUBDAMAGE_PLUS			= 4;	//伤害子事件伤害加数
	public static final int FIX_SUBDAMAGE_STATUS1		= 5;	//状态子事件一概率
	public static final int FIX_SUBDAMAGE_STATUS2		= 6;	//状态子事件二概率
	
	public static final int FIX_TRIGAI_EVENT			= 0;	//触发条件
	public static final int FIX_TRIGAI_BEHAVIOR			= 1;	//触发行为
	
	public static final int FIX_TRIGAI_PARAM1			= 1;
	public static final int FIX_TRIGAI_PARAM2			= 2;
	public static final int FIX_TRIGAI_PARAM3			= 3;
	public static final int FIX_TRIGAI_PARAM4			= 4;
	public static final int FIX_TRIGAI_PARAM5			= 5;
	
	public static final int FIX_VALUE_TYPE_PLUS			= 0;	//相加
	public static final int FIX_VALUE_TYPE_MULT			= 1;	//乘以
	public static final int FIX_VALUE_TYPE_REPLACE		= 2;	//替换
	
	public static final int SPIRIT_LEVLE_PERLAY			= 7;	//心法每个层级的等级数

	public static final int TASK_CATEGORY_MAIN 			= 1;
	public static final int TASK_CATEGORY_WEAPON 		= 2;
	public static final int TASK_CATEGORY_PET 			= 3;
	public static final int TASK_CATEGORY_SECT 			= 4;
	public static final int TASK_CATEGORY_BRANCH 		= 5;
	public static final int TASK_CATEGORY_PET_LIFE 		= 6;
	public static final int TASK_CATEGORY_MRG_SERIES 	= 9;
	public static final int TASK_CATEGORY_MRG_LOOP 		= 10;
	
	public static final int TASK_TYPE_KILL 					= 1;  // 杀怪
	public static final int TASK_TYPE_GATHER 				= 2;  // 采集
	public static final int TASK_TYPE_USE_ITEM 				= 3;  // 定点使用道具
	public static final int TASK_TYPE_LOGIN_DAYS 			= 4;  // 登陆天数
	public static final int TASK_TYPE_ROLE_LEVEL 			= 5;  // 达到x级
//	public static final int TASK_TYPE_MAIN_NPC_TALK 		= 6;  // 主线npc对话
	public static final int TASK_TYPE_SUBMIT_ITEM 			= 7;  // 提交道具
	public static final int TASK_TYPE_FINISH_MAPCOPY 		= 8;  // 通关副本
	public static final int TASK_TYPE_GAIN_PET 				= 9;  // 拥有佣兵
	public static final int TASK_TYPE_POWER_LEVEL			= 10; // 拥有佣兵
	public static final int TASK_TYPE_TRANSFROM_LEVEL 		= 11; // 转职等级
	public static final int TASK_TYPE_NPC_TALK 				= 12; //npc对话
	public static final int TASK_TYPE_GOTO_ACTIVITYGROUP	= 13; //参与活动本组
	public static final int TASK_TYPE_GOTO_NORMALARENA		= 14; //参与个人竞技场次数
	public static final int TASK_TYPE_CONVOY_NPC			= 15; //护送NPC
	public static final int TASK_TYPE_CONVOY_ITEM			= 16; //护送物件
	public static final int TASK_TYPE_ANSWER				= 17; //文字答题
	public static final int TASK_TYPE_RAND_QUESTION			= 18; //随机答题（假答题）
	public static final int TASK_TYPE_JOIN_FACTION			= 22; //加入帮派
	public static final int TASK_TYPE_ENTER_WAYPOINT		= 23; //加入帮派
	public static final int TASK_TYPE_ENTER_PRIVATEMAP		= 24; //进入单人本
	
	//支线任务接取条件
	public static final int BRANCH_TASK_ACCESS_TYPE_LEVEL			= 1;//等级达标
	public static final int BRANCH_TASK_ACCESS_TYPE_POWER			= 2;//战力达标
	public static final int BRANCH_TASK_ACCESS_TYPE_MAIN_TASK		= 3;//完成指定主线任务
	public static final int BRANCH_TASK_ACCESS_TYPE_BRANCH_GROUP	= 4;//完成指定整组支线任务
	public static final int BRANCH_TASK_ACCESS_TYPE_CLIENT			= 5;//客户端触发
	public static final int BRANCH_TASK_ACCESS_TYPE_LOGINDAYS		= 6;//登录天数

	//
	public static final int ENTITY_TYPE_UNKNOWN 	= 0; // 无效
	public static final int ENTITY_TYPE_PLAYER 		= 1; // 玩家
	public static final int ENTITY_TYPE_MONSTER 	= 2; // 怪物
	public static final int ENTITY_TYPE_TRAP 		= 3; // 陷阱
	public static final int ENTITY_TYPE_BLUR 		= 4; // 残影
	public static final int ENTITY_TYPE_PET 		= 5; // 佣兵
	public static final int ENTITY_TYPE_MERCENARY 	= 1000; // 佣兵1(废弃)
	public static final int ENTITY_TYPE_NPC 		= 6; // NPC
	public static final int ENTITY_TYPE_MINERAL 	= 7; // 采矿
	public static final int ENTITY_TYPE_WAYPOINT 	= 8; // 传送点
	public static final int ENTITY_TYPE_MAPBUFF 	= 9; // 场景BUFF
	public static final int ENTITY_TYPE_SKILL	 	= 10; // 技能(法阵)
	public static final int ENTITY_TYPE_ESCORTCAR	= 13; // 镖车
	public static final int ENTITY_TYPE_WEEDINGCAR	= 14; // 婚车
	

	public static final int ENTITY_TYPE_MAPAWARD 	= 19; // 副本最终奖励
	public static final int ENTITY_TYPE_ITEMDROP 	= 101; // 掉落

	public static final int PET_MAKE_CONDITION_MAIN_TASK = 1; // 完成任务
	public static final int PET_MAKE_CONDITION_FIGHT_POWER = 2; // 战力达到
	public static final int PET_MAKE_CONDITION_FINISH_MAPCOPY = 3; // 通关副本
	public static final int PET_MAKE_CONDITION_ROLE_LEVEL = 4; // 等级达到
	
	public static final int PET_TRANSFORM_CONDITION_NONE = 0;
	public static final int PET_TRANSFORM_CONDITION_PET_FINISH_MAPCOPY = 1; // 随从通关

	// 装备相关
	public static final int EQUIP_ADDPROP_TYPE1 = 1; // 属性提升
	public static final int EQUIP_ADDPROP_TYPE2 = 2; // 技能等级提升
	public static final int EQUIP_ADDPROP_TYPE3 = 3; // 技能CD减少
	public static final int EQUIP_ADDPROP_TYPE4 = 4; // 附加技能

	//属性奖励对应类型
	public static final int EQUIP_PROP_AWARD_GROW	= 1;
	public static final int EQUIP_PROP_AWARD_STAR	= 2;
	public static final int EQUIP_PROP_AWARD_STONE	= 3;
	
	public static final int EQUIP_MAX_PARTNUM 		= 6; // 装备部位数量

	public static final int EQUIP_LEGEND_NUM		= 3; //传世装备x号位
	
	public static final int LEGEND_EQUIP_THREE_TYPE_ADDAI 		= 1;
	public static final int LEGEND_EQUIP_THREE_TYPE_ADDPROP 	= 2;
	public static final int LEGEND_EQUIP_THREE_TYPE_ADDBUFF 	= 3;
	public static final int LEGEND_EQUIP_THREE_TYPE_POOLHPCD 	= 4;
	public static final int LEGEND_EQUIP_THREE_TYPE_DODGECD 	= 5;
	
//	public static final int AUCTION_ITEM_TYPE_NULL		= 0;
	public static final int AUCTION_ITEM_TYPE_EQUIP		= 10;
	public static final int AUCTION_ITEM_TYPE_GROW		= 11;	//强化
	public static final int AUCTION_ITEM_TYPE_SKILL		= 12;	//技能
	public static final int AUCTION_ITEM_TYPE_WEAPON	= 13;	//神兵
	public static final int AUCTION_ITEM_TYPE_PET		= 14;	//随从
	public static final int AUCTION_ITEM_TYPE_CLAN		= 15;	//宗门
	public static final int AUCTION_ITEM_TYPE_FASHION	= 16;	//形象
	public static final int AUCTION_ITEM_TYPE_DRUG		= 17;	//药品
	public static final int AUCTION_ITEM_TYPE_SPIRIT	= 18;	//心法书
	public static final int AUCTION_ITEM_TYPE_OTHER		= 100;	//其他
	
	public static final int SECT_ICON_TYPE_ICON = 1;
	public static final int SECT_ICON_TYPE_FRAME = 2;

	// 地图类型
	public static final int MAP_TYPE_MAP_WORLD 					= 0;
	public static final int MAP_TYPE_MAPCOPY_NORMAL 			= 1;
	public static final int MAP_TYPE_MAPCOPY_SECT 				= 2;
	public static final int MAP_TYPE_MAPCOPY_ARENA 				= 3;
	public static final int MAP_TYPE_MAPCOPY_CLAN_TASK 			= 4;	//遭遇战（规则同个人竞技场）
	public static final int MAP_TYPE_MAPCOPY_CLAN_ORE 			= 5;	//夺矿战（规则同个人竞技场）
	public static final int MAP_TYPE_MAPCOPY_ACTIVITY 			= 6;
	public static final int MAP_TYPE_MAPCOPY_CLAN_BATTLE 		= 7;	//宗门战（规则同个人竞技场）
	public static final int MAP_TYPE_MAPCOPY_CLAN_BATTLEHELP 	= 8;	//支援战（超时算胜利，断线算失败）
	public static final int MAP_TYPE_MAPCOPY_SUPERARENA 		= 9;
	public static final int MAP_TYPE_MAPCOPY_BWARENA 			= 10;
	public static final int MAP_TYPE_MAPCOPY_PETLIFE 			= 11;
	public static final int MAP_TYPE_MAPCOPY_CLIMBTOWER	 		= 12;
	public static final int MAP_TYPE_MAPCOPY_FORCEWAR	 		= 13;
	public static final int MAP_TYPE_MAPCOPY_NOVICE_GUIDE	 	= 14;
	public static final int MAP_TYPE_MAPCOPY_SECT_GROUP			= 15;
	public static final int MAP_TYPE_MAPCOPY_WEAPON				= 16;
	public static final int MAP_TYPE_MAPCOPY_DEMON_HOLE			= 17;	
	public static final int MAP_TYPE_MAPCOPY_JUSTICE			= 18;
	public static final int MAP_TYPE_MAPCOPY_EMERGENCY			= 19;
	public static final int MAP_TYPE_MAPCOPY_FIGHTNPC			= 20;
	public static final int MAP_TYPE_MAPCOPY_TOWER_DEFENCE		= 21;

	public static final int MAPCOPY_OPEN_TYPE_PRIVATE = 0;
	public static final int MAPCOPY_OPEN_TYPE_PUBLIC = 1;
	
	public static final int MAPCOPY_DIFFICULT_MASTER	= -3;	//师徒
	public static final int MAPCOPY_DIFFICULT_TEAM		= 0;	//组队
	public static final int MAPCOPY_DIFFICULT_STORY		= 1;	//剧情
	public static final int MAPCOPY_DIFFICULT_EASY		= 2;	//普通
	public static final int MAPCOPY_DIFFICULT_HARD		= 3;	//困难

	public static final int MAPCOPY_SUPERARENA_NORMAL		= 1;	//普通
	public static final int MAPCOPY_SUPERARENA_THREEBEST	= 2;	//2V2(三局两胜)
	
	public static final int MAPCOPY_NORMAL_FINISH_TYPE_KILL_BOSSES = 1;
	public static final int MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT = 2;
	public static final int MAPCOPY_NORMAL_FINISH_TYPE_KILL_MONSTER_COUNT = 3;

	// 副本评分
//	public static final int MAPCOPY_FINISH_SCORE_SSS = 5;
//	public static final int MAPCOPY_FINISH_SCORE_SS = 4;
//	public static final int MAPCOPY_FINISH_SCORE_S = 3;
//	public static final int MAPCOPY_FINISH_SCORE_A = 2;
//	public static final int MAPCOPY_FINISH_SCORE_B = 1;
//	public static final int MAPCOPY_FINISH_SCORE_FAILED = 0;
//	public static final int MAPCOPY_FINISH_NO_SCORE = -1;

	public static final int MAPCOPY_FINISH_SCORE_SSS = 3;
	public static final int MAPCOPY_FINISH_SCORE_SS = 2;
	public static final int MAPCOPY_FINISH_SCORE_S = 1;
	public static final int MAPCOPY_FINISH_SCORE_FAILED = 0;
	public static final int MAPCOPY_FINISH_NO_SCORE = -1;
	
	// ///
	public static final int BYORDER = 1; // 顺序刷怪
	public static final int BYTIME = 2; // 按刷新间隔刷怪
	public static final int BYNUMBER = 3; // 按怪物数量刷怪

	public static final int TASK_GROUP_MAIN = 1;
	public static final int TASK_GROUP_WEAPON = 2;
	public static final int TASK_GROUP_FACTION = 3;
	public static final int TASK_GROUP_CORTEGE = 4;

	public static final int TEAM_MAX_COUNT = 4;

	public static final int PROTOCOL_OP_TEAM_ROLE_OFFLINE = -1;
	public static final int PROTOCOL_OP_TEAM_TEAM_FULL = -2;
	public static final int PROTOCOL_OP_TEAM_ALREADY_IN_TEAM = -3;
	public static final int PROTOCOL_OP_TEAM_TEAM_NOT_EXIST = -4;
	public static final int PROTOCOL_OP_TEAM_ROLE_IN_MAPCOPY = -5;
	public static final int PROTOCOL_OP_TEAM_ROLE_IN_AROOM_MATCHING = -6;
	public static final int PROTOCOL_OP_TEAM_SELF_LEVEL_LIMIT = -7;			//自己等级不满足
	public static final int PROTOCOL_OP_TEAM_OTHER_LEVEL_LIMIT = -8;		//对方等级不满足

	public static final int TEAM_MAX_NEARBY_MAP_ROLE_NUM = 99;
	public static final int TEAM_MAX_NEARBY_MAP_TEAM_NUM = 99;

	public static final int MAP_ROOM_MAX_COUNT 		= 4;
	public static final int FORCEWAR_ROOM_MAX_COUNT = 4;

	public static final int MAP_ROOM_TYPE_DEFAULT		 = 1;
	public static final int MAP_ROOM_TYPE_NPC_MAP		 = 4;	//NPC副本
	public static final int MAP_ROOM_TYPE_TOWER_DEFENCE	 = 5;	//守护泫勃派
	
	public static final int PROTOCOL_OP_MROOM_ROLE_OFFLINE = -1;
	public static final int PROTOCOL_OP_MROOM_ROOM_FULL = -2;
	public static final int PROTOCOL_OP_MROOM_ROOM_NOT_EXIST = -3;
	public static final int PROTOCOL_OP_MROOM_ALREADY_IN_ROOM = -4;
	public static final int PROTOCOL_OP_MROOM_ROLE_IN_MAPCOPY = -5;
	public static final int PROTOCOL_OP_MROOM_ROLE_JOIN_COND_FAILED = -6;
	public static final int PROTOCOL_OP_MROOM_NOT_IN_OPEN_TIME		= -7;
	
	
	public static final int PROTOCOL_OP_MROOM_MAP_INVALID 			= -101;
	public static final int PROTOCOL_OP_MROOM_MAP_CREATE_FAILED 	= -102;
	
	public static final int PROTOCOL_OP_AROOM_ROLE_OFFLINE 			= -1;	//离线
	public static final int PROTOCOL_OP_AROOM_ROOM_FULL 			= -2;	//房间满
	public static final int PROTOCOL_OP_AROOM_ROOM_NOT_EXIST 		= -3;	//房间不存在
	public static final int PROTOCOL_OP_AROOM_ALREADY_IN_ROOM 		= -4;	//已经在房间
	public static final int PROTOCOL_OP_AROOM_ROLE_LEVEL_FAIL		= -5;	//队友等级不足
	public static final int PROTOCOL_OP_AROOM_ROLE_GRADE_FAIL		= -6;	//队友等级段不一致
	public static final int PROTOCOL_OP_AROOM_ROLE_MATCHING			= -7;	//正在匹配中
	
	public static final int PROTOCOL_OP_SUPERARENA_MATCH_TIMEOUT 	= -21;		//超时
	public static final int PROTOCOL_OP_SUPERARENA_OTHERROLE_QUIT 	= -22;		//有队友取消匹配

	public static final int SUPERARENA_JOIN_SINGLE 			= 1;
	public static final int SUPERARENA_JOIN_TEAM	 		= 2;
	
	public static final int SUPER_ARENA_RESULT_LOSE = 0;
	public static final int SUPER_ARENA_RESULT_WIN 	= 1;
	public static final int SUPER_ARENA_RESULT_NONE = 2;
	
	public static final int SUPER_ARENA_ELO_LIMIT	= 1 << 16;
	
	//势力战类型
	public static final int FORCEWAR_TYPE_BWTYPE		= 1;		//正邪对战
	public static final int FORCEWAR_TYPE_MESS			= 2;		//不区分正邪
	
//	//势力类型
	public static final int FORCEWAR_TYPE_ONE		= 1;		//
	public static final int FORCEWAR_TYPE_TWO		= 2;		//
	
	public static final int BWARENA_SAME_LEVEL_COUNT		= 3;	//同等级对手数量
	public static final int BWARENA_HIGH_LEVEL_COUNT		= 1;	//高一级对手数量
	
	public static final int BWARENA_PET_USE_COUNT			= 3; 	//佣兵上阵数量
//	public static final int BWARENA_PET_RANDOM_COUNT		= 5; 	//佣兵随机数量
	
	public static final int BWARENA_LVL_ADVANCE				= 10;	//提前加入到cache等级
	
	public static final int PROTOCOL_OP_BWARENA_ENEMY_NOT_EXIT 	= -1;	//对手不存在
	
	public static final int PROTOCOL_OP_FORCEWAR_MATCH_TIMEOUT 	= -1;		//超时
	public static final int PROTOCOL_OP_FORCEWAR_OUT_OPENTIME 	= -2;		//已经过了开放时间
	
	public static final int PROTOCOL_OP_FORCEWAR_TYPE_INVALID 	= -101;		//势力战类型非法
	
	public static final int PROTOCOL_OP_FROOM_ROLE_OFFLINE 			= -1;	//离线
	public static final int PROTOCOL_OP_FROOM_ROOM_FULL 			= -2;	//房间满
	public static final int PROTOCOL_OP_FROOM_ROOM_NOT_EXIST 		= -3;	//房间不存在
	public static final int PROTOCOL_OP_FROOM_ALREADY_IN_ROOM 		= -4;	//已经在房间
	public static final int PROTOCOL_OP_FROOM_ROLE_COND_FAIL		= -5;	//队友等级、正邪
	public static final int PROTOCOL_OP_FROOM_ROLE_PUNISH			= -6;	//队友在惩罚时间
	public static final int PROTOCOL_OP_FROOM_ROLE_MATCHING			= -7;	//对方正在匹配
	
	public static final int BWTYPE_NONE 	= 0;	//无
	public static final int BWTYPE_WHITE 	= 1;	//正
	public static final int BWTYPE_BLACK 	= 2;	//邪
	public static final int BWTYPE_SAFE 	= 3;
	
	// 职业类型
	public static final int CLASS_TYPE_START 	= 1; 
	public static final int CLASS_TYPE_END 		= 5; 
	
	public static final int CLASS_TYPE_BLADE 	= 1; // 刀客
	public static final int CLASS_TYPE_SWORD 	= 2; // 剑士
	public static final int CLASS_TYPE_SPEAR 	= 3; // 枪豪
	public static final int CLASS_TYPE_ARROW 	= 4; // 弓手
	public static final int CLASS_TYPE_HEAL 	= 5; // 医者
	
	// 日程表
	public static final int SCHEDULE_TYPE_ACTIVITY_MAP = 1; // 活动本
	public static final int SCHEDULE_TYPE_TEAM_MAP_COPY = 2; // 组队本
	public static final int SCHEDULE_TYPE_NORMAL_MAP_COPY = 3; // 普通本
	public static final int SCHEDULE_TYPE_DIFF_MAP_COPY = 4; // 困难本
	public static final int SCHEDULE_TYPE_TREASUREMAP = 5; // 宝藏图
	public static final int SCHEDULE_TYPE_CLIMB_TOWER = 6; // 五绝试炼
	public static final int SCHEDULE_TYPE_DIYSKILL = 7; // 自创武功
	public static final int SCHEDULE_TYPE_SECT_MAPCOPY = 8; // 帮派副本
	public static final int SCHEDULE_TYPE_SECT_ESCORTCAR = 9; // 帮派运镖
	public static final int SCHEDULE_TYPE_FINISH_ANY_SECT_TASK = 10; // 帮派任务
	public static final int SCHEDULE_TYPE_NORMAL_ARENA = 11; // 普通竞技
	public static final int SCHEDULE_TYPE_BW_MAPCOPY= 12; // 正邪道场
	public static final int SCHEDULE_TYPE_SUPER_ARENA = 13; // 会武
	public static final int SCHEDULE_TYPE_FORCEWAR = 14; // 势力战
	public static final int SCHEDULE_TYPE_PRODUCE = 15; // 生产物品
	public static final int SCHEDULE_TYPE_SELL_EQUIPS = 16; // 分解装备
	public static final int SCHEDULE_TYPE_KILL_MAPBOSS = 17; // 击杀魔王
	public static final int SCHEDULE_TYPE_EXCHANGE = 18; // 物物交换
	public static final int SCHEDULE_TYPE_MARRIAGE_TASK = 19; // 姻缘任务
	public static final int SCHEDULE_TYPE_QUESTION = 20; // 科举任务
	public static final int SCHEDULE_TYPE_JUSTICE_MAP = 21; // 正义之心
	public static final int SCHEDULE_TYPE_STELE = 22; // 太玄碑文
	public static final int SCHEDULE_TYPE_EMERGENCY = 23; // 江湖告急
	public static final int SCHEDULE_TYPE_SECT_FLAG_BATTLE = 24; // 帮派夺旗战
	public static final int SCHEDULE_TYPE_TOWER_DEFENCE = 25; // 塔防
	public static final int SCHEDULE_TYPE_NPC_MAP = 26; // NPC副本
	public static final int SCHEDULE_TYPE_NPC_PRAY = 27; // NPC祈福

	// 战斗能量触发类型
	public static final int FIGHTSP_TRIG_ONDAMAGE = 1; // 受到伤害
	public static final int FIGHTSP_TRIG_PROCESSDAMAGE = 2; // 造成伤害
	public static final int FIGHTSP_TRIG_USESKILL = 3; // 使用技能

	public static final int FIGHTSP_PROP = 1; // 增加属性
	public static final int FIGHTSP_BLUR = 2; // 召唤残影

	// 活动挑战类型
	public static final int ACTIVITY_CHALLENGE_TYPE_JJC 			= 1; // 竞技场
	public static final int ACTIVITY_CHALLENGE_TYPE_ZXDC 			= 2; // 正邪道场
	public static final int ACTIVITY_CHALLENGE_TYPE_HWC				= 3; // 会武场
	public static final int ACTIVITY_CHALLENGE_TYPE_ZXSLZ			= 4; // 正邪势力战
	public static final int ACTIVITY_CHALLENGE_TYPE_ACTIVITY_MAP	= 5; // 活动副本
	public static final int ACTIVITY_CHALLENGE_TYPE_WORLD_BOSS		= 6; // 参与BOSS
	
	//
	public static final int SECTMAP_MONSTER_NORMAL 		= 1; // 帮派普通怪
	public static final int SECTMAP_MONSTER_PROGRESS 	= 2; // 帮派进度怪
	public static final int SECTMAP_BOSS_PROGRESS 		= 3; // 帮派进度BOSS
	public static final int WORLDMAP_BOSS				= 4; // 世界BOSS
	public static final int WORLDMAP_SUPERMONSTER		= 5; // 世界地图精英怪
	public static final int SECTGROUPMAP_MONSTER_PROGRESS 	= 6; // 帮派团队本小怪
	public static final int DEMONHOLEMAP_BOSS 			= 7; //伏魔洞boss

	//怪物计数类型
	public static final int MONSTER_COUNTTYPE_DAMAGE	= 1;	//造成过伤害后，怪物死亡对其计数
	public static final int MONSTER_COUNTTYPE_KILL		= 2;	//必须打最后一下才计数
	
	//怪物boss类型
	public static final int MONSTER_BOSSTYPE_COMMON			= 0;		//小怪
	public static final int MONSTER_BOSSTYPE_NORMALBOSS		= 1;		//小BOSS
	public static final int MONSTER_BOSSTYPE_FINALBOSS		= 2;		//最终BOSS
	public static final int MONSTER_BOSSTYPE_NPC			= 3;		//势力战NPC
	public static final int MONSTER_BOSSTYPE_NORMALSTATUE	= 4;		//势力战普通雕像
	public static final int MONSTER_BOSSTYPE_BIGSTATUE		= 5;		//势力战大雕像
	public static final int MONSTER_BOSSTYPE_BOSSSTATUE		= 6;		//势力战水晶
	public static final int MONSTER_BOSSTYPE_EMERGENCY_NORMAL	= 7;		//江湖告急小怪
	public static final int MONSTER_BOSSTYPE_EMERGENCY_NORMALBOSS	= 8;		//江湖告急小BOSS
	public static final int MONSTER_BOSSTYPE_EMERGENCY_BOSS		= 9;		//江湖告急BOSS
	
	public static final int MONSTER_ROTATION_RANDOM					= 0;		//随机朝向，即忽略所配角度
	public static final int MONSTER_ROTATION_INITAL_CHANGLE			= 1;		//默认朝向，但进入战斗会改变朝向
	public static final int MONSTER_ROTATION_INTITAL_FIXED			= 2;		//默认朝向，进入战斗不会改变朝向
	
	public static final int PET_TYPE_WILD_MAP_MAX_USE = 1;
	public static final int PET_TYPE_ONLY_COPYMAP_MAX_USE = 3;
	public static final int PET_TYPE_SECT_COPYMAP_MAX_USE = 3;
	public static final int PET_TYPE_ARENA_MAX_USE = 3;
	public static final int PET_TYPE_TOWER_MAX_USE = 3;		//爬塔系统最大出战佣兵数量
	public static final int PET_FIGHT_MAX_USE = 3;			//个人竞技场、正邪道场最大出战佣兵
	public static final int PET_TYPE_SUPERARENA_MAX_USE = 1;

	public static final int DAILY_TASK_ID_FINISH_ANY_NORMAL_PRIVATE_MAPCOPY 	= 1;
	public static final int DAILY_TASK_ID_FINISH_ANY_NORMAL_PUBLIC_MAPCOPY 		= 2;
	public static final int DAILY_TASK_ID_FINISH_ANY_SECT_TASK 					= 3;
	public static final int DAILY_TASK_ID_START_ANY_NORMAL_ARENA				= 4;
	public static final int DAILY_TASK_ID_RECEIVE_MONTHLY_CARD					= 5;
	public static final int DAILY_TASK_ID_RECEIVE_VIT_1							= 6;
	public static final int DAILY_TASK_ID_RECEIVE_VIT_2							= 7;
	public static final int DAILY_TASK_ID_RECEIVE_VIT_3							= 8;
	public static final int DAILY_TASK_ID_FINISH_ANY_ACTIVITY_MAPCOPY_1			= 9;
	public static final int DAILY_TASK_ID_START_ANY_SECT_MAPCOPY				= 10;
	public static final int DAILY_TASK_ID_BUY_COINS								= 11;
	public static final int DAILY_TASK_ID_FINISH_ANY_NORMAL_HARD_MAPCOPY		= 12;
	public static final int DAILY_TASK_ID_FINISH_ANY_NORMAL_MAPCOPY 			= 13;
	public static final int DAILY_TASK_ID_FINISH_ANY_ACTIVITY_MAPCOPY_2			= 14;
	public static final int DAILY_TASK_ID_REFRESH_TREASURE_INFO					= 15;
	public static final int DAILY_TASK_ID_FINISH_ANY_BW_MAPCOPY					= 16;
	public static final int DAILY_TASK_ID_SECT_ESCORTCAR						= 17;
	public static final int DAILY_TASK_ID_ENTER_ANY_SUPER_ARENA					= 18;
	public static final int DAILY_TASK_ID_ENTER_ANY_CLIMB_TOWER					= 19;
	public static final int DAILY_TASK_ID_GIVE_FRIEND_VIT						= 20;
	public static final int DAILY_TASK_ID_SHARE_GIFT							= 21;

	//挑战任务
	public static final int CHALLENGE_TASK_ID_LEVEL						= 1;		//等级达标
	public static final int CHALLENGE_TASK_ID_FIGHTPOWER				= 2;		//战力达标
	public static final int CHALLENGE_TASK_ID_WEAR_PEQUIP				= 3;		//装备X件紫装
	public static final int CHALLENGE_TASK_ID_WEAR_OEQUIP				= 4;		//装备X件橙装
	public static final int CHALLENGE_TASK_ID_ONE_STORY_MAPCOPY			= 5;		//通关xx剧情副本
	public static final int CHALLENGE_TASK_ID_ONE_EASY_MAPCOPY			= 6;		//通关xx普通副本
	public static final int CHALLENGE_TASK_ID_ONE_HARD_MAPCOPY			= 7;		//通关xx困难副本
	public static final int CHALLENGE_TASK_ID_ONE_TEAM_MAPCOPY			= 8;		//通关xx组队副本
	public static final int CHALLENGE_TASK_ID_ANY_PRIVATE_MAPCOPY		= 9;		//通关任意单人副本XX次
	public static final int CHALLENGE_TASK_ID_ANY_PUBLIC_MAPCOPY		= 10;		//通关任意组队副本XX次
	public static final int CHALLENGE_TASK_ID_NORMALARENA_TOP100		= 11;		//连续X天个人竞技场进入前100
	public static final int CHALLENGE_TASK_ID_SUPERARENA_TOP100			= 12;		//连续X天4vs4竞技场进入前100（待定） TODO
	public static final int CHALLENGE_TASK_ID_USE_BIND_COIN				= 13;		//消耗绑定铜钱达标
	public static final int CHALLENGE_TASK_ID_USE_BIND_DIAMOND			= 14;		//消耗绑定元宝达标
	public static final int CHALLENGE_TASK_ID_USE_FREE_COIN 			= 15;		//消耗铜钱达标
	public static final int CHALLENGE_TASK_ID_USE_FREE_DIAMOND 			= 16;		//消耗元宝达标
	public static final int CHALLENGE_TASK_ID_GAIN_WEAPON				= 17;		//获得X件神兵
	public static final int CHALLENGE_TASK_ID_WEAPON_LEVEL				= 18;		//X件神兵达到Y级
	public static final int CHALLENGE_TASK_ID_WEAPON_STAR				= 19;		//X件神兵达到满星
	public static final int CHALLENGE_TASK_ID_GAIN_PET					= 20;		//获得X个随从
	public static final int CHALLENGE_TASK_ID_PET_LEVEL					= 21;		//X个随从达到Y级
	public static final int CHALLENGE_TASK_ID_PET_STAR					= 22;		//X个随从达到满星
	public static final int CHALLENGE_TASK_ID_FRIEND_COUNT				= 23;		//好友数量达到X人  TODO
	public static final int CHALLENGE_TASK_ID_OPSEX_FRIEND_COUNT		= 24;		//异性好友数量达到X人  TODO
	public static final int CHALLENGE_TASK_ID_MARRY						= 25;		//结婚  TODO
	public static final int CHALLENGE_TASK_ID_SWORN						= 26;		//结义  TODO
	public static final int CHALLENGE_TASK_ID_JOIN_SECT					= 27;		//加入帮派 
	public static final int CHALLENGE_TASK_ID_EQUIP_STRENGTHEN			= 28;		//全身强化等级 
	public static final int CHALLENGE_TASK_ID_EQUIP_STAR_UP				= 29;		//全身升星等级 
	public static final int CHALLENGE_TASK_ID_SKILL_LEVEL				= 30;		//技能总等级
	public static final int CHALLENGE_TASK_ID_UNIQUE_SKILL_LEVEL		= 31;		//绝技总等级 
//	public static final int CHALLENGE_TASK_ID_CLAN_TASK					= 23;		//完成X个宗门任务 TODO
//	public static final int CHALLENGE_TASK_ID_CLAN_LEVEL				= 24;		//宗门达到X级（仅针对宗门创始人，后来转的不算） TODO
//	public static final int CHALLENGE_TASK_ID_CLAN_ORE					= 30;		//宗门夺矿  TODO
	public static final int CHALLENGE_TASK_ID_SPIRIT                    = 32;       //掌握X个心法
	public static final int CHALLENGE_TASK_ID_EMBED                     = 33;       //镶嵌X个宝石
	public static final int CHALLENGE_TASK_ID_ANY_GEMLEVEL              = 34;       //任意宝石达到X级
	public static final int CHALLENGE_TASK_ID_TREASURE                  = 35;       //集齐X个藏宝图藏品
	public static final int CHALLENGE_TASK_ID_RIDING_LEVEL              = 36;       //骑术达到x级
	public static final int CHALLENGE_TASK_ID_SUPERARENA_TIMES          = 37;       //参加X场会武
	public static final int CHALLENGE_TASK_ID_FORCEWAR_TIMES            = 38;       //参加X场势力战
	public static final int CHALLENGE_TASK_ID_ANY_TOWER_LEVEL           = 39;       //五绝试炼之一达到X层
	public static final int CHALLENGE_TASK_ID_BW_COPY_LEVEL             = 40;       //正邪道场等级达到X级
	public static final int CHALLENGE_TASK_ID_RAREBOOK                  = 41;       //解锁x本书
	public static final int CHALLENGE_TASK_ID_GRASP_LEVEL               = 42;       //参悟总等级达到X级
	public static final int CHALLENGE_TASK_ID_PRODUCE_LEVEL             = 43;       //生产等级达到x级
	public static final int CHALLENGE_TASK_ID_SELL_OR_BUY               = 44;       //成功购买或售卖x件商品
	public static final int CHALLENGE_TASK_ID_ANY_ARMORLEVEL            = 45;       //任意内甲达到X级
	public static final int CHALLENGE_TASK_ID_ANY_ARMORRANK             = 46;       //任意内甲达到X阶
	
	public static final int ACHIEVEMENT_TYPE_BUDO                  = 1;
	public static final int ACHIEVEMENT_TYPE_GEWU                  = 2;
	public static final int ACHIEVEMENT_TYPE_XIAMING               = 3;
	public static final int ACHIEVEMENT_TYPE_CITIES                = 4;
	//Attention ! when add achievement type, modify the archpoints initial process in the Role class 
	
	public static final int FAME_ID_ROLE_LEVEL						= 1;
	public static final int FAME_ID_HIGHEST_POWER					= 2;
	public static final int FAME_ID_JOIN_SECT						= 3;
	public static final int FAME_ID_MAX_FRIENDS						= 4;
	public static final int FAME_ID_TOTAL_CHARM						= 5;
	public static final int FAME_ID_TOTAL_SECT_CONTRIBUTION			= 6;
	public static final int FAME_ID_TOTAL_AREA_POINT				= 7;
	public static final int FAME_ID_TOTAL_SUPERAREA_HONOR			= 8;
	public static final int FAME_ID_TOTAL_ROBMONEY					= 9;
	public static final int FAME_ID_TOTAL_DELIVER					= 10;
	public static final int FAME_ID_TOTAL_OPEN_BANQUETS				= 11;
	public static final int FAME_ID_TOTAL_DIY_SKILLS				= 12;
	public static final int FAME_ID_TOTAL_ENTER_ACTIVITY_MAPCOPYS	= 13;
	public static final int FAME_ID_ALL_TOWER_LEVEL					= 14;
	
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_OTHER = 0;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_KILL_MONSTER = 1;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_DAILY_TASK 	= 2;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_ACTIVITY_MAPCOPY 	= 3;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_MAIN_TASK 	= 4;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_SECT_TASK 	= 5;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_BRANCH_TASK	= 6;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_SECT_DELIVER = 7;
	public static final int OFFLINE_EXP_DISTRIBUTE_TYPE_MARRIAGE_TASK = 8;
	
	public final static int MAX_RANK_LIST_SIZE 	= 600;
	public final static int RANK_TYPE_ROLE_LEVEL 				= 1;
	public final static int RANK_TYPE_ROLE_POWER 				= 2;
	public final static int RANK_TYPE_PETS_POWER 				= 3;
	public final static int RANK_TYPE_WEAPONS_POWER 			= 4;
	public final static int RANK_TYPE_SUPER_ARENA_HISTORY	 	= 5;
	public final static int RANK_TYPE_SUPER_ARENA_WEEK		 	= 6;
	public final static int RANK_TYPE_CHARM_FEMALE				= 7;
	public final static int RANK_TYPE_CHARM_MALE				= 8;
	public final static int RANK_TYPE_LOCAL_FORCEWAR_WHITE		= 9;
	public final static int RANK_TYPE_LOCAL_FORCEWAR_BLACK		= 10;
	public final static int RANK_TYPE_GLOBAL_FORCEWAR_WHITE		= 11;	//TODO
	public final static int RANK_TYPE_GLOBAL_FORCEWAR_BLACK		= 12;	//TODO
	public final static int RANK_TYPE_ROLE_ACHIEVE				= 13;
	public final static int RANK_TYPE_ROLE_LEVEL_BALDE			= 14;
	public final static int RANK_TYPE_ROLE_LEVEL_SWORD			= 15;
	public final static int RANK_TYPE_ROLE_LEVEL_SPEAR			= 16;
	public final static int RANK_TYPE_ROLE_LEVEL_ARROW			= 17;
	public final static int RANK_TYPE_ROLE_LEVEL_HEAL			= 18;
	public final static int RANK_TYPE_ROLE_POWER_BALDE 			= 19;
	public final static int RANK_TYPE_ROLE_POWER_SWORD			= 20;
	public final static int RANK_TYPE_ROLE_POWER_SPEAR 			= 21;
	public final static int RANK_TYPE_ROLE_POWER_ARROW			= 22;
	public final static int RANK_TYPE_ROLE_POWER_HEAL			= 23;
	public final static int RANK_TYPE_ROLE_MASTER_REPUTATION	= 24;
	
	public final static int RANK_TYPE_SECT_WEEK_VIT 			= 1;
	
	
	public static final int EQUIP_RANK_WHILE		= 1;	//白
	public static final int EQUIP_RANK_GREEN		= 2;	//绿	
	public static final int EQUIP_RANK_BLUE			= 3;	//蓝
	public static final int EQUIP_RANK_PURPLE		= 4;	//紫
	public static final int EQUIP_RANK_ORANGE		= 5;	//橙
	
	// 场景BUFF范围类型
	public static final int MAPBUFF_SCOPE_SINGLE = 0; // 单体BUFF
	public static final int MAPBUFF_SCOPE_MULTI = 1; // 群体BUFF

	// 矿物类型
	public static final int MINERAL_TYPE_TASK 			= 1; // 任务矿
	public static final int MINERAL_TYPE_MAPCOPY 		= 2; // 副本矿
	public static final int MINERAL_TYPE_COLOREGG 		= 3; // 彩蛋矿
	public static final int MINERAL_TYPE_WORLDRAND 		= 4; // 野外矿
	public static final int MINERAL_TYPE_FLAG	 		= 5; // 旗帜
	public static final int MINERAL_TYPE_BANQUET		= 6; // 宴席
	public static final int MINERAL_TYPE_WEDDINF_BOX	= 7; // 结婚礼盒
	public static final int MINERAL_TYPE_STELE			= 8; // 太玄碑文

	// PK
	public static final int ATTACK_MODE_PEACE = 0; 	// 和平模式
	public static final int ATTACK_MODE_ALL = 1; 	// 全体模式
	public static final int ATTACK_MODE_BW = 2;		// 善恶模式
	public static final int ATTACK_MODE_SECT = 3; 	// 帮派模式
	
	public static final int MAP_PKTYPE_NORMAL 	= 1;
	public static final int MAP_PKTYPE_SAFE 	= 2;
	public static final int MAP_PKTYPE_SECT 	= 3;
	public static final int MAP_PKTYPE_KILL 	= 4;

	public static final int NAME_COLOR_WHITE = 1;
	public static final int NAME_COLOR_ORANGE = 2;
	public static final int NAME_COLOR_RED = 3;

	public static final int RED_NAME_MIN = 2;
	public static final int RED_NAME_MAX = 9999;

	// 红名分档
	public static final int RED_NAME_GRADE0 = 0;
	public static final int RED_NAME_GRADE1 = 1;
	public static final int RED_NAME_GRADE2 = 2;
	public static final int RED_NAME_GRADE3 = 3;
	public static final int RED_NAME_GRADE4 = 4;

	// 自创武功
	public static final int DIY_SKILL_POWER = 1; // 劲
	public static final int DIY_SKILL_INCISE = 2; // 切
	public static final int DIY_SKILL_COMBO = 3; // 连
	public static final int DIY_SKILL_CDSP = 4; // 御
	public static final int DIY_SKILL_WIND = 5; // 缠
	public static final int DIY_SKILL_CHASE = 6; // 追
	public static final int DIY_SKILL_BREAK = 7; // 破
	public static final int DIY_SKILL_CONTROL = 8; // 控
	public static final int DIY_SKILL_UNREAL = 9; // 幻

	public static final int DIY_BUFF_BREAK = 1; // 破
	public static final int DIY_BUFF_CONTROL = 2; // 控
	public static final int DIY_BUFF_UNREAL = 3; // 幻
	
	//buff效果类型
	public static final int EBUFF_PROP		= 1;	//属性
	public static final int EBUFF_STATUS	= 2;	//状态

	//buff 伤害类型
	public static final int BUFF_DAMAGETYPE_UNKNOEN		= 0;	//不可驱散
	public static final int BUFF_DAMAGETYPE_BUFF		= 1;	//正面BUFF
	public static final int BUFF_DAMAGETYPE_DBUFF		= 2;	//负面BUFF
	
	//数值类型
	public static final int VALUE_TYPE_FIXED	= 1;	//固定值
	public static final int VALUE_TYPE_PERCENT	= 2;	//万分比
	
	//目标类型
	public static final int POSITION_TYPE_TARGET 	= 1;	//目标位置
	public static final int POSITION_TYPE_OWNER		= 2;	//自身位置
	
	//召唤类型
	public static final int SUMMON_TYPE_MONSTER		= 1;	//召唤怪物
	public static final int SUMMON_TYPE_SKILL		= 2;	//召唤法阵
	
	//击退类型
	public static final int SHIFT_TYPE_ONGETDAMAGEF	= 1;		//造成伤害时正向击退
	public static final int SHIFT_TYPE_ONUSESKILL	= 2;		//造成伤害前(使用技能时)正向击退
	public static final int SHIFT_TYPE_ONGETDAMAGER	= 3;		//造成伤害时反向向击退
	
	//触发条件类型
	public static final int TRIG_EVENT_EDEADCOUNT		= 1;	//敌人死亡N人
	public static final int TRIG_EVENT_SDEAD			= 2;	//自己死亡
	public static final int TRIG_EVENT_SKILLCOUNT		= 3;	//自己释放N个技能
	public static final int TRIG_EVENT_SHPLOWER			= 4;	//生命值低于指定值
	public static final int TRIG_EVENT_DMGBYCOUNT_D		= 5;	//受到N次直接伤害
	public static final int TRIG_EVENT_SDMGBY_VALUE		= 6;	//受到一定伤害
	public static final int TRIG_EVENT_ENEMYARROUND		= 7;	//周围有敌方
	public static final int TRIG_EVENT_LOSEHP			= 8;	//损失气血
	public static final int TRIG_EVENT_IDLE				= 9;	//待机
	public static final int TRIG_EVENT_DMGBYCOUNT_I		= 10;	//受到N次间接伤害
	public static final int TRIG_EVENT_INTERVAL			= 11;	//每隔一段时间
	public static final int TRIG_EVENT_DGMTOFIX			= 12;	//每次伤害结算时
	public static final int TRIG_EVENT_BUFFCHANGE		= 13;	//BUFF 添加/移除/数值变化
	public static final int TRIG_EVENT_DODGE			= 14;	//当自己闪避一定次数
	public static final int TRIG_EVENT_DMGTOCOUNT_D		= 15;	//造成N次直接伤害
	public static final int TRIG_EVENT_DMGBY_HP			= 16;	//血量低于XX%时，受到伤害时
	public static final int TRIG_EVENT_DMGTO_THP		= 17;	//对血量低于X%的单位造成伤害结算时
	public static final int TRIG_EVENT_ANY_MISS			= 18;	//N次未命中
	public static final int TRIG_EVENT_DMGTO_STATE		= 19;	//对持有指定状态的单位进行伤害结算时
	public static final int TRIG_EVENT_WEAPON_MOTIVATE	= 20;	//进入变身&结束变身
	
	//触发行为类型
	public static final int TRIG_BEHAVIOR_USESKILL			= 2;	//释放技能
	public static final int TRIG_BEHAVIOR_USEBUFF			= 3;	//释放BUFF
	public static final int TRIG_BEHAVIOR_DMGTOFIX			= 5;	//伤害修正
	public static final int TRIG_BEHAVIOR_DMGBYFIX			= 6;	//受伤伤害修正
	public static final int TRIG_BEHAVIOR_QUICKCOOL_SKILL	= 7;	//使自身指定技能结束冷却加快
	
	public static final int DMGTO_THP_TYPE_MONSTER			 	= 1;		//小怪
	public static final int DMGTO_THP_TYPE_BOSS_MONSTER		= 2;		//魔王
	public static final int DMGTO_THP_TYPE_HERO			 		= 4;		//玩家
	
	public static final int SCENE_TRIG_EVENT_TAKE_TASK 			= 1;		//接取任务
	public static final int SCENE_TRIG_EVENT_TASK_REWARD 		= 2;		//领取奖励
	public static final int SCENE_TRIG_EVENT_TASK_USE_ITEM 		= 3;		//任务使用道具
	public static final int SCENE_TRIG_EVENT_TASK_MINERAL 		= 4;		//任务采矿
	public static final int SCENE_TRIG_EVENT_TALK_NPC 			= 5;		//同NPC说话
	public static final int SCENE_TRIG_EVENT_TASK_FINISH 		= 6;		//任务完成
	public static final int SCENE_TRIG_EVENT_PUT_NPC 			= 7;		//摆放npc
	
	
	public static final int SCENE_TRIG_BEHAVIOR_NPC_ACTION		= 1;		//Npc表现
	public static final int SCENE_TRIG_BEHAVIOR_ROLE_ACTION 	= 2;		//角色表现
	public static final int SCENE_TRIG_BEHAVIOR_EFFECT 			= 3;		//触发特效
	public static final int SCENE_TRIG_BEHAVIOR_ANIMATION 		= 4;		//触发动画
	public static final int SCENE_TRIG_BEHAVIOR_SCALE	 		= 5;		//触发场景镜头缩放
	public static final int SCENE_TRIG_BEHAVIOR_SPAWN_MONSTER 	= 6;		//刷怪
	public static final int SCENE_TRIG_BEHAVIOR_TASK_ALTER 		= 7;		//触发角色变身
	public static final int SCENE_TRIG_BEHAVIOR_QUIT_ALTER 		= 8;		//解除角色变身
	public static final int SCENE_TRIG_BEHAVIOR_NPC_PUTACTION	 = 9;		//NPC摆放动作
	public static final int SCENE_TRIG_BEHAVIOR_NPC_RECOVER	 	= 10;		//NPC恢复动作
	public static final int SCENE_TRIG_BEHAVIOR_OPEN_UI	 		= 11;		//触发UI界面
	
	
	//主动技能类型
	public static final int CUR_USE_SKILL_COUNT			= 4;
	
	public static final int SKILL_USE_TYPE_CUR			= 1;	//4个主动之一
	public static final int SKILL_USE_TYPE_DODGE		= 2;	//轻功
	public static final int SKILL_USE_TYPE_DIY			= 3;	//自创武功
	public static final int SKILL_USE_TYPE_UNIQUE		= 4;	//绝技
	public static final int SKILL_USE_TYPE_ATTACK		= 5;	//普通攻击
	public static final int SKILL_USE_TYPE_WEAPON		= 6;	//神兵攻击
	public static final int SKILL_USE_TYPE_ALL			= -1;	//所有主动技能
	
	public static final int BUFF_CHANGETYPE_ADD			= 1;	//BUFF 添加
	public static final int BUFF_CHANGETYPE_REMOVE		= 2;	//BUFF 移除
	public static final int BUFF_CHANGETYPE_VALUECHANGE	= 3;	//BUFF 数值变化
	
	public static final int eSE_Damage 	= 1;				//伤害
	public static final int eSE_Buff 	= 2;				//祝福
	public static final int eSE_DBuff 	= 3;				//诅咒
	
	public static final int SKILL_SPECIAL_NONE			= 0;  
	public static final int SKILL_SPECIAL_GUIDE			= 1;  //引导
	public static final int SKILL_SPECIAL_RUSH			= 2;  //冲锋
	public static final int SKILL_SPECIAL_SHIFT			= 3;  //击退
	public static final int SKILL_SPECIAL_SUMMON		= 4;  //召唤
	public static final int SKILL_SPECIAL_STARGER		= 5;  //触发技能目标替换
	public static final int SKILL_SPECIAL_PDAM_USESKILL	= 6;  //实施伤害时触发技能
	public static final int SKILL_SPECIAL_FIX_DAMVALUE	= 7;  //修改伤害数值
	public static final int SKILL_SPECIAL_FLY_SKILL		= 8;  //飞行技能
	public static final int SKILL_SPECIAL_AURA			= 9;  //光环
	public static final int SKILL_SPECIAL_WEAPONSP		= 10; //加减神兵能量
	public static final int SKILL_SPECIAL_DMGADDHP		= 11; //根据伤害量额外回血（与吸血独立）
	public static final int SKILL_SPECIAL_OMNISLASH		= 12; //无敌斩
	public static final int SKILL_SPECIAL_MAXHPDMG		= 13; //伤害时根据当前气血额外追加伤害
	public static final int SKILL_SPECIAL_EXTRA_DROP	= 16; //伤害时产生额外掉落
	
	//光环目标类型
	public static final int AURA_TARGET_OWNER			= 1;	//友方
	public static final int AURA_TARGET_ENEMY			= 2;	//敌方
	
	public static final int TARGET_TYPE_TARGET			= 0;	//当前伤害过的目标
	public static final int TARGET_TYPE_OWNER			= 1;	//自身
	
	public static final int WEAPON_TYPE_GENERAL 	= 0;
	public static final int WEAPON_TYPE_LOOP 		= 1;

	public static final int GUIDE_SKILL_BYCOUNT	= 1;	//按每隔一定时间触发一次伤害，直到触发次数用尽
	public static final int GUIDE_SKILL_BYTIME	= 2;	//按每隔一定时间触发一次伤害，直到持续时间用尽
	
	//骑术类型
	public static final int HORSE_SKILL_TYPE_BORN	= 1;	//先天
	public static final int HORSE_SKILL_TYPE_LEANR	= 2;	//后天
	
	
	//藏宝图情报点类型
	public static final int TREASURE_INFOPOINT_KILLMONSTER	= 1;	//概率杀怪
	public static final int TREASURE_INFOPOINT_DIALOGUE		= 2;	//对白类
	public static final int TREASURE_INFOPOINT_MINERAL		= 3;	//挖掘类
	public static final int TREASURE_INFOPOINT_SECRET_BOX	= 4;	//秘匣类
	
	public static final int TREASURE_MEDAL_GROW_NORMAL		= 1;	//普通装裱
	public static final int TREASURE_MEDAL_GROW_DIAMOND		= 2;	//元宝装裱
	
	public static final int WEEK_DAY_SATURDAY						= 6;
	public static final int WEEK_DAY_SUNDAY							= 0;
	
	public static final int VALUE_TYPE_INT						= 0;
	public static final int VALUE_TYPE_FLOAT					= 1;
	
	
	//龙印
	public static final int SEAL_MAKE_TYPE_NORMAL	= 1;		//消耗道具合成
	public static final int SEAL_MAKE_TYPE_DIAMOND	= 2;		//黑市购买合成
	
	//好友相关
	public static final int FRIEND_TYPE_GENERAL						= 2;
	public static final int FRIEND_TYPE_VIP							= 3;
	public static final int FRIEND_TYPE_HEAD_ITEM					= 4;
	public static final int FRIEND_TYPE_USE_ITEM					= 5;
	
	// 幸运大转盘
	public static final int DAYS_IN_A_WEEK                    = 7;
	
	//随从成就
	public static final int PET_ACHIEVE_START_TYPE          = 1;
	public static final int PET_ACHIEVE_END_TYPE 			= 3;
	public static final int PET_ACHIEVE_TYPE_LEVEL 			= 1;
	public static final int PET_ACHIEVE_TYPE_STAR	 		= 2;
	public static final int PET_ACHIEVE_TYPE_BREAKSKILL	 	= 3;
	
	public static final int PET_SPIRIT_EFFECT_TYPE_PET_PROP 		= 1;
	public static final int PET_SPIRIT_EFFECT_TYPE_REDUCE_STUPID 	= 2;
	public static final int PET_SPIRIT_EFFECT_TYPE_ROLE_PROP 		= 3;
	public static final int PET_SPIRIT_EFFECT_TYPE_PET_AI 			= 4;
	
	//帮派运镖
	public static final int SECT_DELIVER_NO_ENOUGH_DIAMOND = -1; // 元宝不足
	public static final int SECT_DELIVER_IS_PROTECTED      = -2; // 正处于投保的状态
	public static final int SECT_DELIVER_NO_ENOUGH_VIT     = -3; // 体力不足
	public static final int SECT_DELIVER_IN_ROOM           = -4; // 正处于房间中
	public static final int SECT_DELIVER_IN_SECT_BATTLE    = -5; // 正处于帮战队列中
	public static final int SECT_DELIVER_NOT_IN_EXP_MAP    = -6; // 不在练级地图
	public static final int SECT_DELIVER_IN_4V4_MAP        = -7; // 在4v4竞技场队列中
	public static final int SECT_DELIVER_ROLE_DEAD         = -8; // 玩家死亡
	public static final int SECT_DELIVER_NO_TEMP_WISH_DATA = -9; // 保存帮派祝福时，没有找到临时存储的值。
	public static final int SECT_DELIVER_SERARCH_TIME_FAILED = -10; // 求援的时候，小于最小求援时间
	public static final int SECT_DELIVER_IS_NOT_PROTECTED_TIME = -11; // 正处于投保的状态
	public static final int SECT_DELIVER_SERARCH_NO_MEMBER = -12; // 求援无可响应成员
	public static final int SECT_DELIVER_NEAR_MAX          = -13; // 求援无可响应成员

	public static final byte SECT_IS_ROB = 1;                    //是劫镖状态
	public static final byte SECT_IS_NOT_ROB = 0;                //不是劫镖状态
	
	public static final byte ROLLNOTICE_TYPE_HOURSE_STARUP = 1;           //坐骑升星
	public static final byte ROLLNOTICE_TYPE_WEAPON_STARUP = 2;           //神兵升星
	public static final byte ROLLNOTICE_TYPE_EQUIP_STARUP  = 3;           //装备升星
	public static final byte ROLLNOTICE_TYPE_EQUIP_STRENGTHEN = 4;        //装备强化
	public static final byte ROLLNOTICE_TYPE_TRANSFER      = 5;           //转职
	public static final byte ROLLNOTICE_TYPE_SECT_CREATE   = 6;           //帮派创建
	public static final byte ROLLNOTICE_TYPE_SECT_LEVELUP  = 7;           //帮派升级
	public static final byte ROLLNOTICE_TYPE_PET_STARUP    = 8;           //随从升星
	public static final byte ROLLNOTICE_TYPE_ADD_MESSAGE_BOARD = 9;       //发布留言板
	public static final byte ROLLNOTICE_TYPE_BOSS_REFRESH 	= 10;         //世界boss刷新
	public static final byte ROLLNOTICE_TYPE_BOSS_KILLED 	= 11;         //世界boss被击杀
	public static final byte ROLLNOTICE_TYPE_MARRIAGE 		= 12;         //结婚
	public static final byte ROLLNOTICE_TYPE_SUPERMONSTER1 	= 13;     	  //精英怪活动
	public static final byte ROLLNOTICE_TYPE_SUPERMONSTER2 	= 14;     	  //精英怪活动
	public static final byte ROLLNOTICE_TYPE_SUPERMONSTER3 	= 15;     	  //精英怪活动
	public static final byte ROLLNOTICE_TYPE_FIREWORK		= 16;         //燃放烟花
	public static final byte ROLLNOTICE_TYPE_SYS_RED_ENVELOPE 	= 17;     	  //系统触发红包活动
	public static final byte ROLLNOTICE_TYPE_PAY_RED_ENVELOPE1 	= 18;     	  //充值红包活动
	public static final byte ROLLNOTICE_TYPE_PAY_RED_ENVELOPE2 	= 19;     	  //充值红包活动
	public static final byte ROLLNOTICE_TYPE_PAY_RED_ENVELOPE3 	= 20;     	  //充值红包活动
	public static final byte ROLLNOTICE_TYPE_PAY_RED_ENVELOPE4 	= 21;     	  //充值红包活动
	public static final byte ROLLNOTICE_TYPE_WIN_FIRST	 	= 22;     	  //获得竞技场第一
	public static final byte ROLLNOTICE_TYPE_WIN_HIGH_SCORE 	= 23;     	  //获得竞技场高名次
	public static final byte ROLLNOTICE_TYPE_GET_ORANGE 	= 24;     	  //获得橙色装备
	public static final byte ROLLNOTICE_TYPE_BATTLE_FLAG 	= 25;     	  //夺旗战占领
	public static final byte ROLLNOTICE_TYPE_STELE_MONSTER 	= 26;     	  //刷新太玄碑文怪物
	public static final byte ROLLNOTICE_TYPE_JUSTICE_NPC 	= 27;     	  //刷新正义之心NPC
	public static final byte ROLLNOTICE_TYPE_LUCKLYSTAR_FULL= 28;     	  //幸运星发送结束
	public static final byte ROLLNOTICE_TYPE_LEGEND_MAKE	= 29;     	  //传世装备打造
	public static final byte ROLLNOTICE_TYPE_REDNAME_PUNISH	= 30;     	  //红名惩罚
	
	private static final int SECT_DELIVER_TASK_SIZE        = 3;           //运镖任务数量
	
	//帮派职位
	public static final int SECT_MEMBER_TYPE_CHIEF        = 1;           //帮主
	public static final int SECT_MEMBER_TYPE_DEPUTY       = 2;           //副帮主
	public static final int SECT_MEMBER_TYPE_ELDER        = 3;           //长老
	
	public static final int REMAIN_ACTIVITY_TYPE_LEVEL			= 1;
	public static final int REMAIN_ACTIVITY_TYPE_LOGINDAYS		= 2;
	
	//改名
	public static final int RENAME_TYPE_DIAMOND 	= 1;		//非绑元宝改名
	public static final int RENAME_TYPE_ITEM 		= 2;		//道具改名
	
	public static final int PROTOCOL_OP_RENAME_ALREADY_USED = -1;	//已经被使用
	public static final int PROTOCOL_OP_RENAME_INVALID 		= -2;	//非法
	
	public static final int PROTOCOL_OP_SNATCH_RED_ENEVLOPE_FIALED	= -1;	//运气不好没抢到
	public static final int PROTOCOL_OP_SNATCH_RED_ENEVLOPE_TIMEOUT	= -2;	//超时过期
	public static final int PROTOCOL_OP_SNATCH_RED_ENEVLOPE_EMPTY	= -3;	//红包抢完
	public static final int PROTOCOL_OP_SNATCH_RED_ENEVLOPE_EMPTY_GIFT	= -100;	//红包抢完获取到保底礼袋

	//多人类型
	public static final int MULROLE_TYPE_HORSE 		= 1;		//多人坐骑
	public static final int MULROLE_TYPE_STAYWITH 	= 2;		//相依相偎
	
	//多人骑乘
	public static final int PROTOCOL_OP_MULHORSE_OFFLINE 		= -1;		//对方已下线			gs
	public static final int PROTOCOL_OP_MULHORSE_INVALID		= -2;		//无效				gs
	public static final int PROTOCOL_OP_MULHORSE_SELF_FULL 		= -3;		//自己坐骑已满		gs and map
	public static final int PROTOCOL_OP_MULHORSE_OTHER_FULL 	= -4;		//对方坐骑已满		gs and map
	public static final int PROTOCOL_OP_MULHORSE_SELF_RIDE 		= -5;		//自己已在坐骑上		gs and map
	public static final int PROTOCOL_OP_MULHORSE_OTHER_RIDE 	= -6;		//对方已在坐骑上		gs and map
	public static final int PROTOCOL_OP_MULHORSE_SELF_UNRIDE 	= -7;		//自己没在骑乘状态	gs and map
	public static final int PROTOCOL_OP_MULHORSE_OTHER_UNRIDE 	= -8;		//对方没在骑乘状态	gs and map
	public static final int PROTOCOL_OP_MULHORSE_TIME_OUT 		= -9;		//超时				gs
	
	public static final int PROTOCOL_OP_MULHORSE_TOO_FAR 		= -20;		//离得太远			map
	public static final int PROTOCOL_OP_MULHORSE_IN_FIGHT		= -21;		//战斗状态			map
	public static final int PROTOCOL_OP_MULHORSE_LEAD			= -30;		//指引状态
	public static final int PROTOCOL_OP_MULHORSE_BUSY			= -31;		//忙
	
	//相依相偎
	public static final int PROTOCOL_OP_STAYWITH_OFFLINE 		= -1;		//对方已下线			
	public static final int PROTOCOL_OP_STAYWITH_INVALID		= -2;		//无效				
	public static final int PROTOCOL_OP_STAYWITH_SELF_MULSTATE 	= -3;		//自己已在多人状态（多人坐骑、相依相偎）
	public static final int PROTOCOL_OP_STAYWITH_OTHER_MULSTATE = -4;		//对方已在多人状态（多人坐骑、相依相偎）
	public static final int PROTOCOL_OP_STAYWITH_SELF_RIDE 		= -5;		//自己正在骑乘状态
	public static final int PROTOCOL_OP_STAYWITH_OTHER_RIDE 	= -6;		//对方正在骑乘状态
	public static final int PROTOCOL_OP_STAYWITH_TIME_OUT 		= -7;		//超时

	
	public static final int PROTOCOL_OP_STAYWITH_TOO_FAR 		= -20;		//离得太远		
	public static final int PROTOCOL_OP_STAYWITH_IN_FIGHT		= -21;		//战斗状态		
	public static final int PROTOCOL_OP_STAYWITH_LEAD			= -30;		//指引状态
	public static final int PROTOCOL_OP_STAYWITH_BUSY			= -31;		//忙

	//正义之心相关错误码
	public static final int PROTOCOL_OP_JUSTICE_MEMBER_OUT_LINE 		= -1;		//有队友离线	
	public static final int PROTOCOL_OP_JUSTICE_MEMBER_NEED_TIMES 		= -2;		//有队友次数不够	
	public static final int PROTOCOL_OP_JUSTICE_MEMBER_FAR_FROM_NPC		= -3;		//有队友距离NPC太远	
	public static final int PROTOCOL_OP_JUSTICE_NOT_IN_TIME				= -4;		//NPC已消失	
	
	public static final int DIAMOND_REFRESH_MAX_TIMES			= 20;		//有偿刷新最大次数
	
	public static final int MESSAGE_BOARD_COMMENT_TYPE_PRAISE   = 1;        //留言板评论类型赞
	public static final int MESSAGE_BOARD_COMMENT_TYPE_TREAD    = 2;        //留言板评论类型踩

	public static final int MESSAGE_BOARD_SIDE_PRO   = 1;        //留言板正面
	public static final int MESSAGE_BOARD_SIDE_CON   = 2;        //留言板反面

	public static final byte SECT_APPLICATION_PUSH_FLAG_NO    = 0;        //不自动推送帮派申请
	public static final byte SECT_APPLICATION_PUSH_FLAG_YES   = 1;        //自动推送帮派申请
	
	public static final int FIREWORK_SELF				= 1; //燃放烟花效果仅自己可见
	public static final int FIREWORK_ALL_LINE 			= 2; //燃放烟花效果单地图所有线
	public static final int FIREWORK_ALL_MAP			= 3; //燃放烟花效果全服所有地图
	
	public static final int ACTIVITY_ENTITY_TYPE_WORLDBOSS			= 1; //活动类型世界BOSS
	public static final int ACTIVITY_ENTITY_TYPE_LITTLEBOSS			= 2; //活动类型稀有精英
	public static final int ACTIVITY_ENTITY_TYPE_BOX				= 3; //活动类型活动矿
	
	//npc 功能ID
	public static final int NPC_FUNCTION_ENTER_WEAPONMAP 			= 17;
	
	//
	public static final int DMG_TRANSFER_TO		= 1;		//造成伤害加深
	public static final int DMG_TRANSFER_BY		= 2;		//收到伤害加深
	
	
	public static final int MARRIAGE_RESPONSE_SUCCESS		= 1;		//求婚成功
	public static final int MARRIAGE_RESPONSE_FAILED		= 2;		//求婚失败
	
	// 活动副本类型
	public static final int ACTIVITY_MAP_TYPE_EXP		= 1;
	public static final int ACTIVITY_MAP_TYPE_COIN		= 2;
	
	//太玄碑文
	public static final int PROTOCOL_OP_STELE_NOT_IN_OPENTIME	= -1;
	
	public static final int STELE_ADD_CARD_TYPE_SELF			= 1;
	public static final int STELE_ADD_CARD_TYPE_MEMBER			= 2;
	
	//伏魔洞
	public static final int PROTOCOL_OP_DEMON_HOLE_LEVELREQ			= -1;	//等级不满足
	public static final int PROTOCOL_OP_DEMON_HOLE_NO_TIMES			= -2;	//没有次数
	public static final int PROTOCOL_OP_DEMON_HOLE_NOT_IN_OPENTIME	= -3;	//不在开放时间
	public static final int PROTOCOL_OP_DEMON_HOLE_NOT_JOIN			= -4;	//没有参加活动
	public static final int PROTOCOL_OP_DEMON_HOLE_OUT_FLOOR		= -5;	//层数越界
	public static final int PROTOCOL_OP_DEMON_HOLE_TLEVELREQ		= -6;	//转职等级不满足
	
	public static final int PROTOCOL_OP_DEMON_HOLE_ROLE_NOT_JOIN	= -1001;
	
	//约战NPC
	public static final int FIGHT_NPC_COND_TYPE_LEVEL					= 1;
	public static final int FIGHT_NPC_COND_TYPE_POWER					= 2;
	public static final int FIGHT_NPC_COND_TYPE_SPIRIT_LVL_SUM			= 3;
	public static final int FIGHT_NPC_COND_TYPE_WEAPON_STAR_SUM			= 4;
	
	public static final int PROTOCOL_OP_FIGHT_NPC_ALREADY_WIN			= -1;	//已经挑战过
	public static final int PROTOCOL_OP_FIGHT_NPC_ALL_FINISH			= -2;	//已经完成全部挑战
	public static final int PROTOCOL_OP_FIGHT_NPC_COND_REQ				= -3;	//条件不满足
	public static final int PROTOCOL_OP_FIGHT_NPC_COOL_TIME				= -4;	//还在挑战冷却时间
	
	public static final int PROTOCOL_OP_FIGHT_NPC_NOT_WIN				= -5;	//挑战未成功不能领奖
	public static final int PROTOCOL_OP_FIGHT_NPC_BAG_FULL				= -6;	//背包满
	
	
	//IDIP修改角色信息类型
	public static final int IDIP_CHANGE_ROLE_LEVEL				= 1; //idip修改角色等级
	public static final int IDIP_CHANGE_ROLE_VIP_POINT			= 2; //idip修改角色VIP点
	public static final int IDIP_ADD_ROLE_PAY					= 3; //idip添加角色充值
	public static final int IDIP_ADD_ROLE_VIP_POINT				= 4; //idip修改角色VIP点
	
	public static final int GMVIPPOINT_MAX_NUM					= 10000000; //GM设置最大VIP点数
	
	public static final int WORLD_GRADE_ROLES		= 50;

	public static final int SOCIAL_COMMENT_LATEST_COUNT_LIMIT	= 10000;
	public static final int SOCIAL_COMMENT_LIKE_COUNT_LIMIT		= 200;
	public static final int SOCIAL_COMMENT_DISLIKE_COUNT_LIMIT	= 200;
	
	public static final int SOCIAL_COMMENT_TAG_LAST			= 1;		//最新评论
	public static final int SOCIAL_COMMENT_TAG_LIKE			= 2;		//点赞最多
	public static final int SOCIAL_COMMENT_TAG_DISLIKE		= 3;		//鄙视最多
	
	public static final int SOCIAL_COMMENT_THEME_PET		= 1;		//佣兵评论
	public static final int SOCIAL_COMMENT_THEME_WEAPON		= 2;		//神兵评论
	
	public static final int SOCIAL_COMMENT_THEME_START		= 1;
	public static final int SOCIAL_COMMENT_THEME_END		= 2;
	
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_NOT_EXIT				= -1;	//评论不存在
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_NO_TIMES				= -2;	//评论次数不足
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_INVALID				= -3;	//非法字符
	
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_LVL_REQ				= -101;	//等级不足
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_SERVERID_INVALID		= -102;	//服务器ID非法
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_NO_THEME				= -103;	//没有宠物或神兵
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_NOT_VALID			= -104;	//内容非法
	public static final int PROTOCOL_OP_SOCIAL_COMMENT_TYPE_INVALID			= -105;	//类型非法
	
	// 师徒系统相关
	public static final int PROTOCOL_OP_MASTER_OK							=  0;	//操作成功
	public static final int PROTOCOL_OP_MASTER_FAIL							= -1;	//未知错误
	public static final int PROTOCOL_OP_MASTER_APPRENTICE_LEVEL				= -2;	//徒弟等级不符合条件
	public static final int PROTOCOL_OP_MASTER_MASTER_LEVEL					= -3;	//师父等级不符合条件
	public static final int PROTOCOL_OP_MASTER_MASTER_EXIST					= -4;	//已有师父
	public static final int PROTOCOL_OP_MASTER_APPRENTICE_EXIST				= -5;	//已有徒弟
	public static final int PROTOCOL_OP_MASTER_TOO_MANY_APPRENTICE			= -6;	//徒弟已满
	public static final int PROTOCOL_OP_MASTER_TOO_MANY_MSG					= -7;	//消息已满
	public static final int PROTOCOL_OP_MASTER_DISMISS_COOL					= -8;	//开除冷却期
	public static final int PROTOCOL_OP_MASTER_BETRAY_COOL					= -9;	//叛师冷却期
	public static final int PROTOCOL_OP_MASTER_APPLY_EXIST					= -10;	//申请已经存在
	public static final int PROTOCOL_OP_MASTER_NOT_FOUND					= -11;	//目标不存在
	public static final int PROTOCOL_OP_MASTER_REQ_GRADUATE_COOL			= -12;	//申请出师冷却期
	public static final int PROTOCOL_OP_MASTER_GRADUATE_LEVEL				= -13;	//出师等级不符合条件
	public static final int PROTOCOL_OP_MASTER_MASTER_NOT_EXIST				= -14;	//没有师父
	public static final int PROTOCOL_OP_MASTER_APPRENTICE_NOT_EXIST			= -15;	//没有此徒弟
	public static final int PROTOCOL_OP_MASTER_OFFER_NOT_EXIST				= -16;	//邀请已过期
	public static final int PROTOCOL_OP_MASTER_INVALID_ANNOUNCE				= -17;	//非法声明
	
	public static final int MASTER_GRADUATE_TASK_ACTIVITY					=  1;	//累积活跃
	public static final int MASTER_GRADUATE_TASK_ARENA						=  2;	//个人竞技场
	public static final int MASTER_GRADUATE_TASK_SUPER_ARENA				=  3;	//会武
	public static final int MASTER_GRADUATE_TASK_FORCE_WAR					=  4;	//势力战
	public static final int MASTER_GRADUATE_TASK_MASTER_DUNGEON				=  5;	//师父副本
	
	public static final int FASHION_TYPE_WEAPON		=	1;		//时装武器类型
	public static final int FASHION_TYPE_CLOTHES	=	2;		//时装形象类型
	public static final int FASHION_TYPE_OTHER		=	3;		//时装其他类型
	
	public static final int NPC_TRANSFROM_CONDITION_TYPE_VIP_LEVEL		= 1;		//NPC传送条件VIP等级
	public static final int NPC_TRANSFROM_CONDITION_TYPE_ROLE_LEVEL		= 2;		//NPC传送条件角色等级
	
	public static final int CHAT_ROOM_TYPE_GLOBAL						= 1;		
	public static final int CHAT_ROOM_TYPE_LOCAL						= 2;
	
	//玩家改名类型
	public static final int ROLE_RENAME_COST_TYPE_DIAMOND				= 1;
	public static final int ROLE_RENAME_COST_TYPE_CARD					= 2;
	
	//特权卡类型
	public static final int SPECIALCARD_TYPE_MONTH		= 1;	//月卡
	public static final int SPECIALCARD_TYPE_WEEK		= 2;	//周卡
	
	//bi任务上报类型
	public static final int BI_MISSION_TYPE_MAPCOPY		= 1;	//副本
	public static final int BI_MISSION_TYPE_BOSS		= 2;	//BOSS
	public static final int BI_MISSION_TYPE_PVP			= 3;	//PVP
	
	private static final Map EMPTY_MAP = new EmptyMap<>();
	private static final List EMPTY_LIST = new EmptyList<>();
	private static final Set EMPTY_SET = new EmptySet<>();
	private static final SBean.DropRatio defaultDropRatio = new SBean.DropRatio(1.0f, 1, 1);
	private static final SBean.RewardRatio defaultRewardRatio = new SBean.RewardRatio(1.0f, 1);
	private static GameData instance;

	public static GameData getInstance()
	{
		return instance;
	}

	public static void setInstance(GameData inst)
	{
		if (instance == null)
		{
			instance = inst;
		}
		else
		{
			System.out.println("----------reset game data");
			System.out.println("game data reset fixedDrops");
			System.out.println("game data reset randomDrops");
			System.out.println("game data reset noDuplicateDrops");
			instance.fixedDrops = inst.fixedDrops;
			instance.randomDrops = inst.randomDrops;
			instance.noDuplicateDrops = inst.noDuplicateDrops;
//			instance.buffDrops = inst.buffDrops;
		}
	}
	
	//引用的道具ID, 被引用的掉落ID
//	public static boolean resetDropTbls(GameData inst)
//	{
//		for(SBean.FixedDropTableCFGS fixTbl: inst.fixedDrops.values())
//		{
//			for(SBean.DropEntity dropEntity: fixTbl.drops)
//			{
//				if(!instance.checkEntityIdValid(dropEntity.did))
//					return false;
//			}
//		}
//		
//		for(SBean.RandomDropTableCFGS randTbl: inst.randomDrops.values())
//		{
//			for(SBean.DropEntry dropEntry: randTbl.drops)
//			{
//				if(!instance.checkEntityIdValid(dropEntry.drop.did))
//					return false;
//			}
//		}
//		
//		for(SBean.NoDuplicateDropTableCFGS noDupTbl: inst.noDuplicateDrops.values())
//		{
//			for(SBean.DropItemCFGS dropItem: noDupTbl.drops)
//			{
//				if(!instance.checkEntityIdValid(dropItem.did))
//					return false;
//			}
//		}
//		
//		for(SBean.BuffDropTableCFGS buffDropTbl: inst.buffDrops.values())
//		{
//			for(SBean.DropBuffCFGS dropBuff: buffDropTbl.drops)
//			{
//				if(!instance.mapBuffs.containsKey(dropBuff.mapBuffID))
//					return false;
//			}
//		}
//		
//		instance.fixedDrops = inst.fixedDrops;
//		instance.randomDrops = inst.randomDrops;
//		instance.noDuplicateDrops = inst.noDuplicateDrops;
//		instance.buffDrops = inst.buffDrops;
//		return true;
//	}

	public GameData init(int gsid)
	{
		this.gsid = gsid;
		return this;
	}

	public static byte[] getChallengeKey(byte[] keyRand, byte[] arg)
	{
		// TODO
		byte[] data = new byte[keyRand.length + 4 + arg.length];
		System.arraycopy(keyRand, 0, data, 0, keyRand.length);
		System.arraycopy(arg, 0, data, keyRand.length + 4, arg.length);
		data[keyRand.length] = (byte) 1;
		data[keyRand.length + 1] = (byte) 2;
		data[keyRand.length + 2] = (byte) 0;
		data[keyRand.length + 3] = (byte) 3;
		return new MD5Digester().digest(data, 0, data.length);
	}

	public static String getGUID(int gsid, int roleid, int createTime, int seqNum)
	{
		return new StringBuilder().append(gsid).append("_").append(roleid).append("_").append(createTime).append("_").append(seqNum).toString();
	}

	public static String toString(SBean.Vector3 v)
	{
		return "(" + v.x + ", " + v.y + ", " + v.z + ")";
	}
	
	public static String toString(SBean.Vector3F v)
	{
		return "(" + v.x + ", " + v.y + ", " + v.z + ")";
	}
	
	public static int getConfUseServerOpenTime(int openDay, int offset)
	{
		return openDay * GameTime.getDayTimeSpan() + offset;//配置使用适当偏移一个时间段
	}

	public static <K, V> Map<K, V> emptyMap()
	{
		return (Map<K, V>)EMPTY_MAP;
	}
	
	public static <E> List<E> emptyList()
	{
		return (List<E>)EMPTY_LIST;
	}
	
	public static <E> Set<E> emptySet()
	{
		return (Set<E>)EMPTY_SET;
	}
	
	public static int getAreaIdFromGSId(int gsid)
	{
		return gsid / MAX_AREA_GS_COUNT;
	}
	
	public static int getRawZoneIdFromGSId(int gsid)
	{
		return gsid % MAX_AREA_GS_COUNT;
	}
	
	public static int getZoneIdFromRoleId(int roleId)
	{
		return roleId / MAX_GS_ROLE_COUNT;
	}
	
//	public static int getGSId(int areaId, int roleId)
//	{
//		return areaId * MAX_AREA_GS_COUNT + getZoneIdFromRoleId(roleId);
//	}
	
	public static int getMaxGSRoleCount()
	{
		return MAX_GS_ROLE_COUNT;
	}
	
	public static int createRoleId(int zoneid, int roleSeq)
	{
		return zoneid * MAX_GS_ROLE_COUNT + roleSeq;
	}
	
	public static int getMaxGSSectCount()
	{
		return MAX_GS_SECT_COUNT;
	}
	
	public static int createSectId(int zoneid, int sectSeq)
	{
		return zoneid * MAX_GS_SECT_COUNT + sectSeq;
	}

	public static int getMaxGSMarriageCount()
	{
		return MAX_GS_MARRIAGE_COUNT;
	}
	
	public static int createMarriageId(int zoneid, int marriageSeq)
	{
		return zoneid * MAX_GS_MARRIAGE_COUNT + marriageSeq;
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public GameData(SBean.GameDataCFGS cfg) throws Exception
	{
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Pattern.quote("'"));
			sb.append("|");
			sb.append(Pattern.quote("\""));
			sb.append("|");
			sb.append(Pattern.quote("\\"));
			sb.append("|");
			sb.append(Pattern.quote("|"));
			sb.append("|");
			sb.append(Pattern.quote("#"));
			sb.append("|");
			sb.append(Pattern.quote("@"));
			sb.append("|");
			sb.append(Pattern.quote(">"));
			sb.append("|");
			sb.append(Pattern.quote("<"));
			reservedCharsPattern = Pattern.compile(sb.toString());
		}
		{
			StringBuilder sb = new StringBuilder();
			sb.append(" ");//英文空格
			sb.append("|");
			sb.append(" ");//中文空格
			sb.append("|");
			sb.append("\n");//换行符
			sb.append("|");
			sb.append(",");//英文逗号
			sb.append("|");
			sb.append(new String(new byte[] {-62, -91},  "UTF-8"));
			spaceCharsPattern = Pattern.compile(sb.toString());
		}
		{
			int maxLen = 0;
			for (String str : cfg.badstrs)
			{
				invalidStrPatterns.put(str, Pattern.compile(Pattern.quote(str)));
				if (str.length() > maxLen)
					maxLen = str.length();
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= maxLen; ++i)
			{
				sb.append("*");
			}
			maxLengthInvalidReplaceStr = sb.toString();
		}
		{
			msgPattern = Pattern.compile("#I(-?\\d+,(?:\\d+_)*\\d*)#|#M(\\d+,-?\\d+,-?\\d+,-?\\d+,\\d+)#|#T(\\d+)#|#(\\d+)|#V(\\w+://\\S+)#|#R(\\d+,\\d+,\\d+)#|#S(\\d+,\\S+)#|#DPS(\\d+,\\d+,\\d+,\\d+)#");
		}
		
		{
			StringBuilder sb = new StringBuilder();
			sb.append("^role_map_welcome|");
			sb.append("^role_skills_cooldown|");
			sb.append("^role_spawn_point|");
			sb.append("^role_weaponlefttime|");
			sb.append("^role_taskalter|");
			sb.append("^role_petalter|");
			sb.append("^role_escortcar|");
			sb.append("^nearby_role_alterstate|");
			sb.append("^role_adjust_pos|");
			sb.append("^nearby_escortcar_updatestate|");
			sb.append("^nearby_escortcar_robbed|");
			sb.append("^nearby_monster_rushstart|");
			sb.append("^nearby_blur_rushstart|");
			sb.append("^nearby_trap_changestate|");
			sb.append("^nearby_role_addstate|");
			sb.append("^nearby_role_removestate|");
			sb.append("^role_addstate|");
			sb.append("^role_removestate|");
			sb.append("^pet_addstate|");
			sb.append("^pet_removestate|");
			sb.append("^nearby_shiftend_.*|");
			sb.append("^role_summon_pet|");
			sb.append("^role_revive_pet|");
			sb.append("^role_weddingcar|");
			
//			sb.append("^role_curridehorse|");
//			sb.append("^nearby_enter_roles|");
//			sb.append("^nearby_leave_roles|");
//			sb.append("^nearby_move_weddingcar|");
//			sb.append("^nearby_move_role|");
//			sb.append("^boss_damage_close|");
//			sb.append("^nearby_leave_monsters|");
//			sb.append("^nearby_enter_monsters|");
//			sb.append("^role_armorval_update|");
//			sb.append("^nearby_role_ondamage|");
//			sb.append("^role_armorfreeze_update|");
//			sb.append("^role_armorweak_update|");
//			sb.append("^nearby_role_armorweak|");
			
//			sb.append("^role_mulhorse|");
//			sb.append("^role_leave_mulhorse|");
//			sb.append("^nearby_update_mulhorse|");
//			sb.append("^role_update_mulhorse|");
			
			sb.append("^nearby_enter_trap");
//			sb.append("^role_update_timetick|");
//			sb.append("^client_ping_end|");
//			sb.append("^nearby_enter|");
//			sb.append("^nearby_leave|");
//			sb.append("^nearby_spawn_monster|");
//			sb.append("^nearby_move|");
//			sb.append("^nearby_stopmove|");
//			sb.append("^nearby_.*.useskill|");
//			sb.append("^nearby_.*.ondamage|");
//			sb.append("^nearby_processdamage_end|");
//			sb.append("^nearby_.*.finishattack|");
//			sb.append("^nearby_.*.endskill");
			mapSendPattern = Pattern.compile(sb.toString());
		}
		
		{
			StringBuilder sb = new StringBuilder();
			sb.append("^role_rushstart|");
			sb.append("^pet_rushstart|");
			sb.append("^role_shift_start|");
			sb.append("^pet_shift_start");
//			sb.append("^client_ping_start|");
//			sb.append("^role_ping_end|");
//			sb.append("^role_move|");
//			sb.append("^pet_move|");
//			sb.append("^role_stopmove|");
//			sb.append("^pet_stopmove");
			mapRecvPattern = Pattern.compile(sb.toString());
		}
		
		this.headIcons = cfg.headIcons;

		this.properties = cfg.properties;
		this.states = cfg.states;
		this.buffs = cfg.buffs;
		this.mapBuffs = cfg.mapbuffs;

		this.base = cfg.base;
		this.items = cfg.items;
		this.equips = cfg.equips;
		this.equipRefineGroups = cfg.equipRefines;
		this.legend = cfg.legend;
		this.gems = cfg.gems;
		this.books = cfg.books;
		
		this.gifts = cfg.gifts;
		this.checkins = cfg.checkins;
		
		this.fixedDrops = cfg.fixedDropTbl;
		this.randomDrops = cfg.randomDropTbl;
		this.noDuplicateDrops = cfg.noDuplicateDropTbl;
		this.buffDrops = cfg.buffDropTbl;

		this.skills = cfg.skills;
		this.skillBourns = cfg.skillBourns;
		this.skillSpecial = cfg.skillSpecial;

		this.spirits = cfg.spirits;
		this.spiritEffects = cfg.spiritEffects;
		
		this.monsters = cfg.monsters;
		this.npcs = cfg.npcs;
		this.minerals = cfg.minerals;
		this.traps = cfg.traps;
		
		this.spawnAreas = cfg.spawnAreas;
		this.spawnPoints = cfg.spawnPoints;
		this.npcPoints = cfg.npcPoints;
		this.mineralPoints = cfg.mineralPoints;
		this.wayPoints = cfg.wayPoints;
		this.mapBuffPoints = cfg.mapBuffPoints;
		this.mapClusters = cfg.mapClusters;
		this.worldMaps = cfg.worldMaps;
		this.mapcopys = cfg.mapcopys;
		this.sectMapcopys = cfg.sectmaps;
		this.arenaMaps = cfg.arenamaps;
		this.superArenaMaps = cfg.superArenaMaps;
		this.bwarenaMaps = cfg.bwarenamaps;
		this.activityMaps = cfg.activitymaps;
//		this.clanTaskMaps = cfg.clanTaskMaps;
//		this.clanOreMaps = cfg.clanOreMaps;
//		this.clanBattleMaps = cfg.clanBattleMaps;
//		this.clanBattleHelpMaps = cfg.clanBattleHelpMaps;
		this.petLifeMaps = cfg.petlifeMaps;
		this.climbTowerMaps = cfg.climbTowerMaps;
		this.forcewarMaps = cfg.forcewarMaps;
		this.weaponMaps = cfg.weaponMaps;
		this.weaponMapGrades = cfg.weaponMapGrades;
		this.demonHoleMaps = cfg.demonHoleMaps;
		this.justiceMaps = cfg.justiceMaps;
		this.fightNpcMaps = cfg.fightNpcMaps;
		
		this.actMapGroups = cfg.actMapGroups;

		this.mainTasks = cfg.mainTasks;
		this.bwTypeMainTasks = cfg.bwTypeMaintasks;
		this.weaponTasks = cfg.weaponTasks;
		this.petTasks = cfg.petTasks;
		this.sectTasks = cfg.sectTasks;
		this.alters = cfg.alters;
		this.mrgSeriesTaskGroups = cfg.mrgSeriesTaskGroups;
		this.mrgLoopTasks = cfg.mrgLoopTasks;
		
		this.classRoles = cfg.classRoles;
		this.classRoleFashions = cfg.classRoleFashions;
		this.levels = cfg.levels;
		this.levelLimit = cfg.levelLimit;

		this.weapons = cfg.weapons;
		this.weaponUSkills = cfg.weaponUSkills;
		this.pets = cfg.pets;
		this.petLvls = cfg.petLvls;
//		this.petFriends = cfg.petFriends;
		this.petCoPractices = cfg.petCoPractices;
		this.petTransforms = cfg.petTransforms;
		this.petBreakSkills = cfg.petBreakSkills;
		this.petLifeTask = cfg.petLifeTasks;
		this.petSpiritGroups = cfg.petSpiritGroups;
		this.petExploits = cfg.petExploit;
		
		
		for (SBean.ShopCFGS e : cfg.shops)
		{
			for (SBean.ShopGoodsLevelCFGS ee : e.levels.values())
			{
				for (SBean.ShopGoodsGroupCFGS eee : ee.groups)
				{
					for (SBean.ShopGoodsCFGS eeee : eee.goods)
					{
						e.allGoods.put(eeee.id, eeee);
					}
				}
			}
		}
		for (SBean.GambleShopCFGS e : cfg.gambleShops)
		{
			for (SBean.GambleShopGoodsLevelCFGS ee : e.levels.values())
			{
				for (SBean.GambleShopGoodsGroupCFGS eee : ee.groups)
				{
					for (SBean.GambleShopGoodsCFGS eeee : eee.goods)
					{
						e.allGoods.put(eeee.id, eeee);
					}
				}
			}
		}
		shops.addAll(cfg.shops);
		gambleShops.addAll(cfg.gambleShops);
		roleRanks.addAll(cfg.roleRanks);
		sectRanks.addAll(cfg.sectRanks);
		dailyTasks.putAll(cfg.dailyTasks);
		dailyActivities.putAll(cfg.dailyActivities);
		challengeTasksGroup.putAll(cfg.challengeTaskGroups);
		fames = cfg.fames;
		dailyOnlineGift.putAll(cfg.dailyOnlineGift);
		offlineExp = cfg.offlineExp;
		dailyQuizGift = cfg.dailyQuizGift;
		activityQuestionsBank = cfg.activityQuestionsBank;
		luckyWheel = cfg.luckyWheel;
		redEnvelope = cfg.redEnvelope;
		activityChallengeType = cfg.activityChallengeType;
		
		for (SBean.EquipPurgatoryCFGS ep : cfg.purgatory)
		{
			purgatorys.put(ep.lvl, ep);
		}

		toughens.addAll(cfg.toughen);
		upStars.addAll(cfg.upStar);

		
		slots.addAll(cfg.slots);
		starAdditionProp.addAll(cfg.starAdditionProp);

		List<SBean.PropAwardCFGS> growLst = new ArrayList<>();
		List<SBean.PropAwardCFGS> evoLst = new ArrayList<>();
		List<SBean.PropAwardCFGS> stoneLst = new ArrayList<>();
		for (SBean.PropAwardCFGS p : cfg.propaward)
		{
			switch (p.conditionType)
			{
			case 1:
				growLst.add(p);
				break;
			case 2:
				evoLst.add(p);
				break;
			case 3:
				stoneLst.add(p);
				break;
			default:
				break;
			}
		}
		this.propAwards.put(1, growLst);
		this.propAwards.put(2, evoLst);
		this.propAwards.put(3, stoneLst);



		this.transforms = cfg.transforms;

		for (SBean.SuiteCFGS suiteCFGS : cfg.suites.values())
		{
			this.suites.put(suiteCFGS.id, suiteCFGS);
			for (int eid : suiteCFGS.parts)
			{
				this.equipToSuites.put(eid, suiteCFGS.id);
			}
		}
		for (SBean.SectAuthorityCFGS entry : cfg.sectAuthority)
		{
			this.sectAuthority.put(entry.id, entry);
		}
		for (SBean.SectUpLevelCFGS entry : cfg.sectUpLevel)
		{
			this.sectUpLevel.put(entry.lvl, entry);
		}
		for (SBean.SectGroupSkillCFGS entry : cfg.sectSkill)
		{
			this.sectGroupSkill.put(entry.groupId, entry);
		}
		for (SBean.SectWorshipCFGS entry : cfg.sectWorship)
		{
			this.sectWorship.put(entry.type, entry);
		}
		for (SBean.SectWorshipExpCFGS entry : cfg.sectWorshipExp)
		{
			this.sectWorshipExp.put(entry.level, entry);
		}
		for (SBean.SectBanquetCFGS entry : cfg.sectBanquet)
		{
			this.sectBanquet.put(entry.type, entry);
		}
		for (SBean.FightSPCFGS f : cfg.fightSP)
		{
			this.fightSP.put(f.id, f);
		}

		for (SBean.BlurCFGS b : cfg.blurs)
		{
			this.blurs.put(b.id, b);
		}
		this.sectIcons = cfg.sectIcons;

		this.diySkills = cfg.diySkills;

		for (SBean.DIYSkillGradeCFGS d : cfg.diySkillGrades)
		{
			this.diySkillGrades.put(d.id, d);
		}

		this.diySkillUnique = cfg.diySkillUnique;

		for (SBean.DIYSkillActionCFGS d : cfg.diySkillActions)
		{
			this.diySkillActions.put(d.id, d);
		}

		for (SBean.DIYBUFFCFGS b : cfg.diybuffs)
		{
			DIYBuffLib lib = this.diyBuffLibs.get(b.libType);
			if (lib == null)
			{
				lib = new DIYBuffLib(b.libType);
				this.diyBuffLibs.put(b.libType, lib);
			}
			lib.buffs.add(b);
		}

		for (SBean.DiySkillSlotUnblockCFGS d : cfg.diySkillSlotUnblock)
		{
			this.slotUnblock.put(d.id, d);
		}
		this.vips = cfg.vips;
		this.payId = cfg.payId;
		this.pays = cfg.pays;
		this.channels = cfg.channels;
		this.specialCards = cfg.specialCards;

//		this.clanCFGS = cfg.clan;
//		this.clanValueCFGS = cfg.clanValue;
//		
//		this.clanTasks.putAll(cfg.clanTask);
//		this.clanGroupTasks.putAll(cfg.clanGroupTask); 
//		
//		for (SBean.ClanDiscipleNameCFGS name : cfg.clanDiscipleName)
//		{
//			this.discipleName.put(name.id, name);
//		}
		

		this.arenaCfg = cfg.arena;
		this.arenaRobots = cfg.arenaRobots;
		this.arenaRobotGroup = cfg.arenaRobotGroup;
		this.superarena = cfg.superarena;
		this.superarenaTypes = cfg.superarenaTypes;
		this.bwarena = cfg.bwarena;
		this.forcewar = cfg.forcewar;
		this.forcewarBase = cfg.forcewarbase;

		this.randomNames = cfg.randomNames;
		this.pkSystem = cfg.pkSystem;
		
		this.produce = cfg.produce;
		this.produceRecipes = cfg.prodeceRecipes;
		this.fusion = cfg.fusion;
		
		this.aitrigers = cfg.aitrigers;
		this.trigEvents = cfg.trigEvents;
		this.trigBehaviors = cfg.trigBehaviors;
		
		this.worldBosses = cfg.worldBosses;
		this.worldSuperMonsters = cfg.worldSuperMonsters;
		this.worldMinerals = cfg.worldMinerals;
		this.monsterDamage = cfg.monsterDamages;
		this.spiritFactors = cfg.spiritFactors;
		this.weaponFactors = cfg.weaponFactors;
		
		this.stores = cfg.stores;
		this.horses = cfg.horses;
		this.horseEnHances = cfg.horseEnHances;
		this.horseEnhanceLvls = cfg.horseEnhanceLvls;
		this.horseShows = cfg.horseShows;
		this.horseSkills = cfg.horseSkills;
		this.horseSkillUpdate = cfg.horseSkillUpdate;
		this.horseEffectCfgs = cfg.horseEffects;
		this.horseCommon = cfg.horseCommon;
		this.horseEnhanceLvlcommons = cfg.horseEnhanceLvlcommons;
		this.horseEnhanceTypes = cfg.horseEnhanceTypes;
		this.horseEnhanceWeights = cfg.horseEnhanceWeights;

		this.treasureBase = cfg.treasureBase;
		this.treasureMaps = cfg.treasureMaps;
		this.treasurePieces = cfg.treasurePieces;
		this.infoPoints = cfg.infoPoints;
		this.medals = cfg.medals;
		this.treasureNpcs = cfg.treasureNpcs;
		
		this.friendGives = cfg.friendGive;
		this.charms = cfg.charms;
		this.friendHeads = cfg.friendHead;
		this.fashions = cfg.fashions;
		for(SBean.FashionCFGS f: cfg.fashions.values())
		{
			this.allFashionTypes.add(f.type);
		}		
		this.socialActions = cfg.socialActions;
		this.sealBase = cfg.sealBase;
		this.sealGrades = cfg.sealGrades;
		this.sealEnhanceTypes = cfg.sealEnhanceTypes;
		this.leadGroups = cfg.leadGroups;
		this.expCoinBase = cfg.expCoinBase;
		this.rarebookGroups = cfg.rarebookGroups;
		this.graspGroups = cfg.graspGroups;
		this.graspIDs = new ArrayList<>(this.graspGroups.keySet());
		this.dmgTransfers = cfg.dmgTransfers;
		this.dmgTransferBuys = cfg.dmgTransferBuys;
		this.titles = cfg.titles;
		this.common = cfg.common;
		this.betaActivity = cfg.betaActivity;
		this.branchTask = cfg.branchTask;
		this.petAchieveGroups = cfg.petAchieveGroups;
		this.climbTowerFloor = cfg.climbTowerFloor;
		this.climbTowerFame = cfg.climbTowerFame;
		this.climbTowerBaseData = cfg.climbTowerBaseData;
		this.secretAreaData = cfg.secretAreaData;
		for(SBean.PetAchieveGroupCFGS group: this.petAchieveGroups.values())
		{
			for(SBean.PetAchieveCFGS pa: group.achieves)
				this.petAchieves.put(pa.id, pa);
		}
		
		this.uniqueSkills = cfg.uniqueSkills;
		for(SBean.UniqueSkillCFG e: this.uniqueSkills.values())
		{
			for(int skillID: e.skills)
				this.uniqueSkillIDs.add(skillID);
		}
		this.sectDeliver = cfg.sectDeliver;
		this.sectDeliverTask = cfg.sectDeliverTask;
		this.sectDeliverVehicle = cfg.sectDeliverVehicle;
		this.sectDeliverRoute = cfg.sectDeliverRoute;
		this.sectDeliverWish = cfg.sectDeliverWish;
		this.rollNotice = cfg.rollNotice;
		this.rmActivitys = cfg.remainActivitys;
		this.composes = cfg.composes;
		this.messageBoards = cfg.messageBoards;
		this.messageBoardCommon = cfg.messageBoardCommon;
		
		this.scheduleRewards = cfg.scheduleRewards;
		this.scheduleData = cfg.scheduleData;
		this.armor = cfg.armor;
		this.sceneTrigs = cfg.sceneTrigs;
		this.sceneSpawnPoints = cfg.sceneSpawnPoint;
		this.marriage = cfg.marriage;
		this.exchange = cfg.exchange;
		this.sectGroupMaps = cfg.sectGroupMaps;
		this.sectGroupMapRankRewards = cfg.sectGroupMapRankRewards;
		this.weaponTalent = cfg.weaponTalent;
		this.flagBattle = cfg.flagBattle;
		this.activityMapProcess = cfg.activityMapProcess;
		this.heirloom = cfg.heirloom;
		this.robot = cfg.robot;
		this.performanceSpawns = cfg.performanceSpawns;
		this.auction = cfg.auction;
		this.activityLast = cfg.activityLast;
		this.flashsaleResources = cfg.flashsaleresource;
		this.adverBackgrounds = cfg.adversresource;
		this.stele = cfg.stele;
		this.justice = cfg.justice;
		this.demonHole = cfg.demonHole;
		this.battleMap = cfg.battleMap;
		this.emergency = cfg.emergency;
		this.lucklyStar = cfg.lucklyStar;
		this.fightNpcGroups = cfg.fightNpcGroups;
		this.wizardPet = cfg.wizardPet;
		this.npcTransfromFunc = cfg.npcTransfromFunc;
		
		this.npcMaps = cfg.npcMaps;
		this.npcPrays = cfg.npcPrays;
		this.prayDrops = cfg.prayDrops;
		this.towerDefenceMaps = cfg.towerDefenceMaps;
		this.towerDefences = cfg.towerDefences;
		this.master = cfg.master;	
		this.mapSkills = cfg.mapSkills;	
	}
	
	public SBean.FriendHeadCFGS getFriendHeadCFGS(int headId)
	{
		return this.friendHeads.get(headId);
	}
	
	public SBean.FriendGiveRewardCFGS getFriendGiveReward(int level)
	{
		if(level <= 0 || level > this.friendGives.size())
			return null;
		return this.friendGives.get(level - 1);
	}
	
	public LevelExp getAddFriendLvlExp(int curLvl, int curExp, int addExp)
	{
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for (; lvl <= friendGives.size(); ++lvl)
		{
			SBean.FriendGiveRewardCFGS cfg = friendGives.get(lvl - 1);
			if (exp >= cfg.exp)
			{
				exp -= cfg.exp;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= levels.size())
		{
			curLvl = levels.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}
	
	public int getCharmLvl(int charm)
	{	
		for(int i=0; i<this.charms.size(); i++)
		{
			if(charm < this.charms.get(i).charmReq)
				return i;
		}
		
		return this.charms.size();
	}
	
	public SBean.CharmCFGS getCharmCFGS(int lvl)
	{
		if(lvl <= 0 || lvl > this.charms.size())
			return null;
		
		return this.charms.get(lvl - 1);
	}
	
	public int getDayUseTimes(int vip, List<Integer> useTimes)
	{
		if(useTimes.size() <= 0)
			return 0;
		return useTimes.get(vip);
	}
	
	public SBean.StoreCFGS getStoreCFGS(int id)
	{
		return this.stores.get(id);
	}
	
	
//	public int getClanOreMapId(){
//		if(clanOreMaps.size() > 1)
//		{
//			int index = GameRandom.getRandInt(0, clanOreMaps.size());
//			return clanOreMaps.get(index).id;
//		}else
//		{
//			return clanOreMaps.get(0).id;
//		}
//	}
	
	
//	public SBean.ClanDiscipleNameCFGS getClanDiscipleName(Collection<SBean.DBClanEliteDisciple> names){
//		SBean.ClanDiscipleNameCFGS discipleNameCFGS = null;
//		Map<Integer, SBean.ClanDiscipleNameCFGS> tmpDiscipleName = new TreeMap<>(this.discipleName);
//		for(SBean.DBClanEliteDisciple key : names){
//			tmpDiscipleName.remove(key.name);
//		}
//		
//		Object allName[] = (tmpDiscipleName.keySet().toArray());
//		int index = GameRandom.getRandInt(0, allName.length - 1);
//		discipleNameCFGS = tmpDiscipleName.get((allName[index]));
//		return discipleNameCFGS;
//	}
	
	public static int getDayByRefreshTimeOffset(int now)
	{
		return GameTime.getDayByOffset(now, 3600 * GameData.GAME_DAY_REFRESH_TIME);
	}

	public static int getOffsetDaysToNow(int time)
	{
		return GameData.getDayByRefreshTimeOffset(GameTime.getTime()) - GameData.getDayByRefreshTimeOffset(time);
	}
	
	//第几周
	public static int getWeekByRefreshTimeOffset(int now)
	{
		return GameTime.getWeekByOffset(now, 0, 3600 * GameData.GAME_DAY_REFRESH_TIME);
	}

	private static int getRefreshTimeByDayTime(int datetime)
	{
		return datetime + 3600 * GameData.GAME_DAY_REFRESH_TIME;
	}

	public SBean.VipCFGS getVipCFGS(int vip)
	{
		if (vip < 0 || vip >= vips.size())
			return null;
		return vips.get(vip);
	}
	
	public int getVipLevel(int points)
	{
		int level = 0;
		for (SBean.VipCFGS cfg : this.vips)
		{
			if (points >= cfg.points)
				level = cfg.level;
			else
				break;
		}
		return level;
	}
	
	private static String getPlatName(String channel)
	{
		if (channel == null || channel.length() <= 1)
			return "";
		return channel.substring(0, 1); 
	}
	
	private static String getChannelName(String channel)
	{
		if (channel == null || channel.length() <= 1)
			return "";
		return channel.substring(1);
	}
	
	
	private static int getPlatId(String platName)
	{
		int platId = GameData.GAME_PLAT_ANDROID;
		switch (platName)
		{
		case GameData.GAME_PLAT_IOS_IDENTIFIER:
			platId = GameData.GAME_PLAT_IOS;
			break;
		case GameData.GAME_PLAT_ANDROID_IDENTIFIER:
			platId = GameData.GAME_PLAT_ANDROID;
			break;
		default:
			break;
		}
		return platId;
	}
	
	private static int getPlatIdByChannel(String channel)
	{
		return getPlatId(getPlatName(channel));
	}
	
	
	public SBean.DBRegisterID getRegisterID(String gameId, int zoneId, String uid, String channel)
	{
		if (!GameData.getInstance().checkInputAccountValid(uid, this.getCommonCFG().input.maxUserNameLength, true))
			return null;
		return new SBean.DBRegisterID(gameId.trim().toLowerCase(), zoneId, uid.trim().toLowerCase(), channel.trim().toLowerCase());
	}
	
	public static String getUserName(SBean.DBRegisterID registerId)
	{
		return getUserName(registerId.zoneId, registerId.channel, registerId.uid);
	}
	
	public static String getChannelOpenId(SBean.DBRegisterID registerId)
	{
		return registerId.channel + "_" + registerId.uid;
	}
	
	public static String getUserName(int zoneId, String channel, String uid)
	{
		return zoneId + "_" + channel + "_" + uid;
	}
	
//	public static String getPayGodOrderId(int gsId, int roleId, int payLvl)
//	{
//		return gsId + "_" + roleId + "_" + payLvl + "_godpay_" + UUID.randomUUID().toString();
//	}
	
//	public int getChannlePayId(String gameAppId, String channel)
//	{
//		SBean.ChannelCFGS cfg = this.channels.get(channel);
//		if (cfg == null)
//			cfg = this.channels.get(gameAppId);
//		return cfg == null ? -1 : cfg.payId;
//	}
//	
//	public SBean.PayCFGS getChannelPayCFG(String gameAppId, String channel)
//	{
//		int payId = getChannlePayId(gameAppId, channel);
//		return this.pays.get(payId);
//	}
//	
//	public static SBean.PayLevelCFGS getPayLevelCFGS(SBean.PayCFGS cfg, int payLevel)
//	{
//		return cfg.payLevels.get(payLevel);
//	}
	
	public int getSpecialCardCount()
	{
		return specialCards.size();
	}
	
	public SBean.SpecialCardCFGS getSpecialCardCFGS(int id)
	{
		if(id <= 0 || id > specialCards.size())
			return null;
		
		return specialCards.get(id - 1);
	}
	
	public SBean.PayCFGS getChannelPayCFG()
	{
		return this.pays.get(this.payId);
	}
	
	public SBean.PayLevelCFGS getPayLevelCFGS(int payLevel)
	{
		SBean.PayCFGS cfg = this.getChannelPayCFG();
		return cfg == null ? null : cfg.payLevels.get(payLevel);
	}
	
	public static SBean.PayLevelInfo getSpecailCardPayInfo(SBean.PayCFGS cfg, int cardType)
	{
		for (SBean.PayLevelCFGS e : cfg.payLevels.values())
		{
			if (e.type == GAME_PAY_GOODS_TYPE_CARD && e.param == cardType)
				return new SBean.PayLevelInfo(e.level, e.id, e.buyNum, e.worth, e.points, e.credit, e.price, e.rebates);
		}
		
		return null;
	}
	
	public static Map<Integer, SBean.PayLevelInfo> getPayLevelsInfoByType(SBean.PayCFGS cfg, int type)
	{
		Map<Integer, SBean.PayLevelInfo> info = new TreeMap<>();
		for (SBean.PayLevelCFGS e : cfg.payLevels.values())
		{
			if (e.type == type)
				info.put(e.level, new SBean.PayLevelInfo(e.level, e.id, e.buyNum, e.worth, e.points, e.credit, e.price, e.rebates));
		}
		return info;
	}
	
	public static int getPayLevelPresentDiamond(SBean.PayLevelCFGS cfg, int payTimes)
	{
		int presentDiamond = 0;
		if (payTimes > 0 && payTimes <= cfg.rebates.size())
			presentDiamond = cfg.rebates.get(payTimes-1);
		return presentDiamond;
	}

	public static String ensureNonull(String str)
	{
		return str == null ? "" : str;
	}
	
	private static String ensureSafeStr(String str, int maxLen, String invalidStrs)
	{
		str = str.replaceAll(invalidStrs, "");
		int len = str.length() > maxLen ? maxLen : str.length();
		return str.substring(0, len);
	}

	public static SBean.UserLoginInfo ensureNonull(SBean.UserLoginInfo info)
	{
		info.arg.loginKey = ensureNonull(info.arg.loginKey);
		info.arg.exParam = ensureNonull(info.arg.exParam);
		info.client.gameAppID = ensureNonull(info.client.gameAppID);
		info.system.deviceID = ensureSafeStr(ensureNonull(info.system.deviceID), 64, "\\|");
		info.system.systemHardware = ensureSafeStr(ensureNonull(info.system.systemHardware), 64, "\\|");
		info.system.systemSoftware = ensureSafeStr(ensureNonull(info.system.systemSoftware), 64, "\\|");
		info.system.cpuHardware = ensureSafeStr(ensureNonull(info.system.cpuHardware), 64, "\\|");
		info.system.macAddr = ensureSafeStr(ensureNonull(info.system.macAddr), 64, "\\|");
		info.system.loginIP = ensureNonull(info.system.loginIP);

		return info;
	}

	public static SBean.CreateRoleParam ensureNonull(SBean.CreateRoleParam param)
	{
		param.name = ensureNonull(param.name);
		return param;
	}

	public boolean checkSectIcon(int level, int iconId)
	{
		return this.sectIcons.size() >= iconId && iconId > 0 && this.sectIcons.get(iconId - 1).open <= level;
	}

	public Matcher getMsgPatternMatcher(String msg)
	{
		return this.msgPattern.matcher(msg);
	}

	public boolean containsReservedChars(String str)
	{
		Matcher matcher = this.reservedCharsPattern.matcher(str);
		return matcher.find();
	}
	
	public boolean containsSpaceChars(String str)
	{
		Matcher matcher = this.spaceCharsPattern.matcher(str);
		return matcher.find();
	}
	
	public boolean containsMapSendChars(String str)
	{
		Matcher matcher = this.mapSendPattern.matcher(str);
		return matcher.lookingAt();
	}
	
	public boolean containsMapRecvChars(String str)
	{
		Matcher matcher = this.mapRecvPattern.matcher(str);
		return matcher.lookingAt();
	}
	
	public static boolean isDigit(String str)
	{
		for (int i = 0; i < str.length(); ++i)
		{
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}
		return true;
	}
	
	private boolean isInvalidStr(String str)
	{
		String lowcasestr = str.toLowerCase();
		for (Pattern p : this.invalidStrPatterns.values())
		{
			Matcher matcher = p.matcher(lowcasestr);
			if (matcher.find())
				return true;
		}
		return false;
	}

	private String filterInvalidStr(String str)
	{
		for (Map.Entry<String, Pattern> kv : this.invalidStrPatterns.entrySet())
		{
			Matcher matcher = kv.getValue().matcher(str);
			if (matcher.find())
				str = matcher.replaceAll(maxLengthInvalidReplaceStr.substring(0, kv.getKey().length()));
		}
		return str;
	}

	public boolean checkInputAccountValid(String account, int maxLength, boolean isName)
	{
		if (account == null || account.isEmpty() || account.length() != account.codePointCount(0, account.length()))
			return false;
		return account.length() <= maxLength && !((isName && containsSpaceChars(account)) || containsReservedChars(account));
	}
	
	public boolean checkInputStrValid(String str, int maxLength, boolean isName)
	{
		if (str == null || str.isEmpty() || str.length() != str.codePointCount(0, str.length()))
			return false;
		return str.length() <= maxLength && !((isName && containsSpaceChars(str)) || containsReservedChars(str) || isInvalidStr(str));
	}
	
//	public boolean checkMsgStrValid(String str, int maxLength)
//	{
//		if (str == null || str.isEmpty())
//			return false;
//		return str.length() <= maxLength && !(containsReservedChars(str));
//	}

	public String checkFilterInputStr(String originalStr, String displayStr, int maxLength)
	{
		if (originalStr == null || originalStr.isEmpty() || displayStr == null || displayStr.isEmpty())// || displayStr.length() != displayStr.codePointCount(0, displayStr.length()))
			return null;
		if (displayStr.length() > maxLength)
			return null;
		if (containsReservedChars(displayStr))
			return null;
		return filterInvalidStr(originalStr);
	}
	
	public static boolean checkInputIsDigit(String str)
	{
		for (int i = 0; i < str.length(); ++i)
		{
			char ch = str.charAt(i);
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	public static int getRoleHeadKey(byte gender, byte face, byte hair)
	{
		return ((int) gender << 16) | (int) face << 8 | hair;
	}

	short getRoleHeadIcon(byte gender, byte face, byte hair)
	{
		int key = getRoleHeadKey(gender, face, hair);
		Short icon = this.headIcons.get(key);
		return icon == null ? 0 : icon;
	}

	public SBean.SectBanquetCFGS getSectBanquetCFGS(int type)
	{
		return this.sectBanquet.get(type);
	}

	public SBean.SectWorshipExpCFGS getSectWorshipExpCFGS(int level)
	{
		return this.sectWorshipExp.get(level);
	}

	public int getSectWorshipTypeCount()
	{
		return this.sectWorship.size();
	}

	public SBean.SectWorshipCFGS getSectWorshipCFGS(int type)
	{
		return this.sectWorship.get(type);
	}

	public SBean.SectWorshipCFGS getSectWorshipCFGSByVipLevel(int viplevel)
	{
		SBean.SectWorshipCFGS cfgs = null;
		for (int i = 1; i <= this.sectWorship.size(); i++)
		{
			if (viplevel >= this.sectWorship.get(i).vipOpenLimit)
				cfgs = this.sectWorship.get(i);
			else
				break;
		}
		return cfgs;
	}
	
	public SBean.SectSkillCFGS getSectSkillCFGS(int skillId, int level)
	{
		SBean.SectGroupSkillCFGS group = sectGroupSkill.get(skillId);
		if (group == null)
			return null;
		return group.sectGroupSkill.get(level);
	}

	public SBean.SectUpLevelCFGS getSectUpLevel(int level)
	{
		return sectUpLevel.get(level);
	}

	public int getEquipToSuites(int equipId)
	{
		Integer suiteId = equipToSuites.get(equipId);
		return suiteId == null ? 0 : suiteId;
	}

	public SBean.SuiteCFGS getSuites(int suitesId)
	{
		return suites.get(suitesId);
	}

	public SBean.CLassTransformCFGS getClassTrabsformCFGS(int classType)
	{
		if (classType <= 0 || classType > this.transforms.size())
			return null;
		return this.transforms.get(classType - 1);
	}
	
	public SBean.TransformCFGS getTransformCFGS(int id, int tlvl, int bwType)
	{
		if (id <= 0 || id > this.transforms.size())
			return null;
		SBean.CLassTransformCFGS ccfgs = this.transforms.get(id - 1);
		if (tlvl <= 0 || tlvl > ccfgs.transforms.size())
			return null;
		SBean.BWTransformCFGS bwcfgs = ccfgs.transforms.get(tlvl - 1);
		return bwcfgs.transforms.get(bwType);
	}

	public List<Integer> getAvaiableSpirit(int id, int tlvl, int bwType)
	{
		if (tlvl == 0)
		{
			SBean.ClassRoleCFGS ccfg = getClassRoleCFG(id);
			if (ccfg == null)
				return null;
			return ccfg.spirits;
		}
		else
		{
			SBean.TransformCFGS tcfg = getTransformCFGS(id, tlvl, bwType);
			if (tcfg == null)
				return null;
			return tcfg.spirits;
		}
	}

	public SBean.WeaponCFGS getWeaponCFGS(int id)
	{
		return this.weapons.get(id);
	}
	
	public SBean.WeaponUniqueSkillCFGS getWeaponUSkillCFGS(int uniqueSkillID)
	{
		return this.weaponUSkills.get(uniqueSkillID);
	}
	
	public SBean.WeaponTalentCFGS getWeaponTalentCFGS(SBean.WeaponCFGS weapon, int index)
	{
		if(index <= 0 || index > weapon.talents.size())
			return null;
		
		return weapon.talents.get(index - 1);
	}
	
	public static LevelExp getAddWeaponExpLvlExp(SBean.WeaponCFGS cfg, int curLvl, int curExp, int addExp)
	{
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for (; lvl <= cfg.weaponLevel.size(); ++lvl)
		{
			SBean.WeaponLevelCFGS lvlcfg = cfg.weaponLevel.get(lvl - 1);
			if (exp >= lvlcfg.exp)
			{
				exp -= lvlcfg.exp;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= cfg.weaponLevel.size())
		{
			curLvl = cfg.weaponLevel.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}


	public SBean.SpiritCFGS getSpiritCFGS(int id)
	{
		return this.spirits.get(id);
	}
	
	public SBean.SpiritGrowUpCFGS getSpiritGrowUpCFGS(int spiritID, int level)
	{
		SBean.SpiritCFGS cfg = GameData.getInstance().getSpiritCFGS(spiritID);
		if (cfg == null)
			return null;

		if(level < 0 || level >= cfg.growups.size())
			return null;
		
		return cfg.growups.get(level);
	}
	
	public SBean.SpiritEffectCFGS getSpiritEffectCFGS(int id)
	{
		return this.spiritEffects.get(id);
	}
	
	public int getSpiritUseTotalByType(int type)
	{
		SBean.CommonSpiritCFGS spiritCFGS  = this.getCommonCFG().spirits;
		if (type == SPIRIT_TYPE_JOB)
		{
			return spiritCFGS.jobTotal;
		}
		else if (type == SPIRIT_TYPE_GANG)
		{
			return spiritCFGS.gangTotal;
		}
		else if (type == SPIRIT_TYPE_BULIM)
		{
			return spiritCFGS.bulimTotal;
		}
		else
		{
			return -1;
		}
	}

	public static SBean.MailBrief toMailBrief(SBean.DBMail mail)
	{
		return new SBean.MailBrief(mail.id, mail.type, mail.state, mail.fromID, mail.fromName, mail.sendTime, mail.lifeTime, mail.title, mail.attachment.size(), mail.additional);
	}

	public static int getVirtualItemIDPlane(int id)
	{
		int realID = id < 0 ? -id : id;
		int plane = (realID >> 16) & 0xffff;
		return plane;
	}

	public static SBean.GameItem toGameItem(SBean.DBEquip equip)
	{
		Map<String, SBean.DBEquip> equips = new TreeMap<>();
		equips.put(equip.guid, equip);
		return new SBean.GameItem(equip.id, 1, equips);
	}

	public static SBean.GameItem toGameItem(SBean.DBMiscellaneous miscellaneous)
	{
		return new SBean.GameItem(miscellaneous.id, miscellaneous.count, GameData.emptyMap());
	}

	public SBean.GameItem toGameItem(SBean.DBConsignItems items)
	{
		if(items.equip != null)
			return toGameItem(items.equip);
		
		return toGameItem(items.id, items.count, 0);
	}
	
	public SBean.GameItem toGameItem(int id, int count)
	{
		return toGameItem(id, count, 0);
	}
	
	public SBean.GameItem toGameItem(int id, int count, float prAdjust)
	{
		SBean.GameItem gi = null;
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
		case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			{
				switch (id)
				{
				case GameData.COMMON_ITEM_ID_DIAMOND:
				case -GameData.COMMON_ITEM_ID_DIAMOND:
				case GameData.COMMON_ITEM_ID_COIN:
				case -GameData.COMMON_ITEM_ID_COIN:
				case GameData.COMMON_ITEM_ID_SECT_MONEY:
				case GameData.COMMON_ITEM_ID_ARENA_MONEY:
				case GameData.COMMON_ITEM_ID_SUPERARENA_MONEY:
				case GameData.COMMON_ITEM_ID_MASTER_POINT:
				case GameData.COMMON_ITEM_ID_MASTER_REPUTATION:
				case GameData.COMMON_ITEM_ID_EQUIP_ENERGY:
				case GameData.COMMON_ITEM_ID_GEM_ENERGY:
				case GameData.COMMON_ITEM_ID_BOOK_INSPIRATION:
				case GameData.COMMON_ITEM_ID_XUANTIE:
				case GameData.COMMON_ITEM_ID_YAOCAO:
				case GameData.COMMON_ITEM_ID_VIT:
				case GameData.COMMON_ITEM_ID_SPLITSP:
				case GameData.COMMON_ITEM_ID_EXPCOIN:
				case GameData.COMMON_ITEM_ID_ROB_MONEY:
				case GameData.COMMON_ITEM_ID_CREDIT:
				case GameData.COMMON_ITEM_ID_OFFLINE_FUNC_POINT:
					gi = new SBean.GameItem(id, count, GameData.emptyMap());
					break;
				default:
					break;
				}					
			}
			break;
		case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				if (this.getItemCFG(id) != null)
					gi = new SBean.GameItem(id, count, GameData.emptyMap());
			}
			break;
		case GameData.COMMON_ITEM_ID_GEM_PLANE:
			{
				if (this.getGemCFG(id) != null)
					gi = new SBean.GameItem(id, count, GameData.emptyMap());
			}
			break;
		case GameData.COMMON_ITEM_ID_BOOK_PLANE:
			{
				if (this.getBookCFG(id) != null)
					gi = new SBean.GameItem(id, count, GameData.emptyMap());
			}
			break;
		default:
			{
				if (this.getEquipCFG(id) != null)
				{
					Map<String, SBean.DBEquip> equips = new TreeMap<>();
					for (int i = 0; i < count; ++i)
					{
						SBean.DBEquip equip = createNewEquip(id, prAdjust);
						if (equip != null)
							equips.put(equip.guid, equip);
					}
					gi = new SBean.GameItem(id, equips.size(), equips);
				}
			}
			break;
		}
		if (gi == null)
			System.err.println("create GameItem failed : id = " + id);
		return gi;
	}
	
	public static void mergeGameItem(Map<Integer, SBean.GameItem> gis, SBean.DBEquip equip)
	{
		gis.compute(equip.id, (k, v) ->
		{
			if (v == null)
				return toGameItem(equip);
			v.equips.put(equip.guid, equip);
			v.count = v.equips.size();
			return v;
		});
	}

	public static void mergeGameItem(Map<Integer, SBean.GameItem> gis, SBean.DBMiscellaneous miscellaneous)
	{
		gis.compute(miscellaneous.id, (k, v) ->
		{
			if (v == null)
				return toGameItem(miscellaneous);
			v.count += miscellaneous.count;
			return v;
		});
	}

	public List<SBean.GameItem> toGameItems(Collection<SBean.DummyGoods> dgs)
	{
		return toGameItems(dgs, 0);
	}
	
	//prAdjust 只有掉落生效
	public List<SBean.GameItem> toGameItems(Collection<SBean.DummyGoods> dgs, float prAdjust)
	{
		List<SBean.GameItem> gis = new ArrayList<>();
		Map<Integer, Integer> counter = GameData.toCounter(dgs);
		for (Map.Entry<Integer, Integer> e : counter.entrySet())
		{
			SBean.GameItem gi = toGameItem(e.getKey(), e.getValue(), prAdjust);
			if (gi != null)
				gis.add(gi);
		}
		return gis;
	}
	
	public List<SBean.GameItem> toGameItems(Map<Integer, Integer> counter)
	{
		return toGameItems(counter, 0);
	}
	
	public List<SBean.GameItem> toGameItems(Map<Integer, Integer> counter, float prAdjust)
	{
		List<SBean.GameItem> gis = new ArrayList<>();
		for (Map.Entry<Integer, Integer> e : counter.entrySet())
		{
			SBean.GameItem gi = toGameItem(e.getKey(), e.getValue(), prAdjust);
			if (gi != null)
				gis.add(gi);
		}
		return gis;
	}

//	public static SBean.DBDummyItems toDummyItems(SBean.GameItem gi)
//	{
//		SBean.DBDummyItems items = new SBean.DBDummyItems(new ArrayList<SBean.DBEquip>(), new ArrayList<SBean.DBMiscellaneous>());
//		if (!gi.equips.isEmpty())
//		{
//			items.equips.addAll(gi.equips.values());
//		}
//		else
//		{
//			items.others.add(new SBean.DBMiscellaneous(gi.id, gi.count));
//		}
//		return items;
//	}
//
//	public static SBean.DBDummyItems toDummyItems(Collection<SBean.GameItem> gis)
//	{
//		SBean.DBDummyItems items = new SBean.DBDummyItems(new ArrayList<SBean.DBEquip>(), new ArrayList<SBean.DBMiscellaneous>());
//		for (SBean.GameItem gi : gis)
//		{
//			if (!gi.equips.isEmpty())
//			{
//				items.equips.addAll(gi.equips.values());
//			}
//			else
//			{
//				items.others.add(new SBean.DBMiscellaneous(gi.id, gi.count));
//			}
//		}
//		return items;
//	}

	public static Map<Integer, Integer> toCounter(List<SBean.GameItem> gis)
	{
		Map<Integer, Integer> counter = new TreeMap<>();
		for (SBean.GameItem e : gis)
		{
			counter.merge(e.id, e.count, (ov, nv)->ov+nv);
		}
		return counter;
	}
	
	public static Map<Integer, Integer> toCounter(Collection<SBean.DummyGoods> dgs)
	{
		Map<Integer, Integer> counter = new TreeMap<>();
		for (SBean.DummyGoods e : dgs)
		{
			counter.merge(e.id, e.count, (ov, nv)->ov+nv);
		}
		return counter;
	}
	
	public static List<SBean.DummyGoods> toDummGoods(Map<Integer, Integer> counter)
	{
		List<SBean.DummyGoods> dgs = new ArrayList<>();
		for (Map.Entry<Integer, Integer> e : counter.entrySet())
		{
			dgs.add(new SBean.DummyGoods(e.getKey(), e.getValue()));
		}
		return dgs;
	}

	
	@SafeVarargs
	public static Map<Integer, Integer> mergeCounters(Map<Integer, Integer>... counter)
	{
		Map<Integer, Integer> all = new TreeMap<>();
		for (Map<Integer, Integer> e : counter)
		{
			for (Map.Entry<Integer, Integer> ee : e.entrySet())
			{
				all.merge(ee.getKey(), ee.getValue(), (ov, nv)->ov+nv);
			}
		}
		return all;
	}
	
	public static Map<Integer, Integer> mergeCounter(Map<Integer, Integer> counter, Map<Integer, Integer> counterAdd)
	{
		for (Map.Entry<Integer, Integer> e : counterAdd.entrySet())
		{
			counter.merge(e.getKey(), e.getValue(), (ov, nv)->ov+nv);
		}
		return counter;
	}
	
	public static Map<Integer, Integer> mergeCounter(Map<Integer, Integer> counter, Collection<SBean.DummyGoods> dgs)
	{
		for (SBean.DummyGoods e : dgs)
		{
			counter.merge(e.id, e.count, (ov, nv)->ov+nv);
		}
		return counter;
	}

	public static Map<Integer, Integer> toCounter(Map<Integer, SBean.GameItem> gis)
	{
		Map<Integer, Integer> counter = new TreeMap<>();
		for (SBean.GameItem e : gis.values())
		{
			counter.merge(e.id, e.count, (ov, nv)->ov+nv);
		}
		return counter;
	}

//	public static Map<Integer, Integer> toCounter(SBean.DBDummyItems dis)
//	{
//		Map<Integer, Integer> counter = new TreeMap<Integer, Integer>();
//		for (SBean.DBEquip e : dis.equips)
//		{
//			counter.compute(e.id, (k, v) -> v == null ? 1 : v + 1);
//		}
//		for (SBean.DBMiscellaneous e : dis.others)
//		{
//			counter.compute(e.id, (k, v) -> v == null ? e.count : v + e.count);
//		}
//		return counter;
//	}

	public SBean.DBEquip createNewEquip(int id, float prAdjust, int randLegend)
	{
		SBean.EquipCFGS cfg = this.getEquipCFG(id);
		if (cfg == null)
			return null;
		List<Integer> additPropVals = new ArrayList<>();
		for (SBean.EquipAdditPropCFGS e : cfg.additProp)
		{
			additPropVals.add(GameRandom.getRandInt(e.valMin, e.valMax));
		}
		String guid = getEquipGuid(id, gsid, GameTime.getTime(), equipSeqNum.incrementAndGet());

		int durability = getRandomEquipDurability(cfg, prAdjust);
		List<Integer> legends = new ArrayList<>();
		if(durability < 0)
		{
			for(int i = 0; i < EQUIP_LEGEND_NUM; i++)
				legends.add(0);
		}
		else
		{
			legends = createEquipLegend(cfg.type, randEquipLegend(randLegend));
		}
		return new SBean.DBEquip(guid, id, additPropVals, durability, new ArrayList<>(), legends);
	}
	
	public SBean.DBEquip createNewEquip(int id, float prAdjust)
	{
		return createNewEquip(id, prAdjust, 0);
	}
	
	public String createEquipGuid(int id, int gsid)
	{
		return getEquipGuid(id, gsid, GameTime.getTime(), equipSeqNum.incrementAndGet());
	}
	
	private static String getEquipGuid(int id, int gsid, int createTime, int seqNum)
	{
		return new StringBuilder().append(gsid).append("_").append(createTime).append("_").append(seqNum).toString();
	}

//	public static int getEquipIdFromGuid(String guid)
//	{
//		int index = guid.indexOf("_");
//		return index <= 0 ? 0 : Integer.parseInt(guid.substring(0, index));
//	}

	public static boolean isEquip(int id)
	{
		return id > GameData.COMMON_ITEM_ID_EQUIP_MIN || id < -GameData.COMMON_ITEM_ID_EQUIP_MIN;
	}

	public SBean.EquipCFGS getEquipCFG(int id)
	{
		id = id < 0 ? -id : id;
		return equips.get(id);
	}
	
	public Collection<SBean.EquipCFGS> getAllEquipCFGS()
	{
		return equips.values();
	}
	
	
	public List<Integer> createEquipLegend(int partID)
	{
		return createEquipLegend(partID, randEquipLegend());
	}
	
	public List<Integer> createEquipLegend(int partID, List<Byte> rand)
	{
		List<Integer> legends = new ArrayList<>();
		if(rand.size() != EQUIP_LEGEND_NUM)
		{
			for(int i = 0; i < EQUIP_LEGEND_NUM; i++)
				legends.add(0);
			
			return legends;
		}
		
		legends.add(getLegendOne(rand.get(0) == 1));
		legends.add(getLegendTwo(rand.get(1) == 1));
		legends.add(getLegendThree(rand.get(2) == 1, partID));
		return legends;
	}
	
	public List<Byte> randEquipLegend(int randLenegd)
	{
		if(randLenegd <= 0 || randLenegd > this.legend.rands.size())
			return randEquipLegend();
		
		return this.legend.rands.get(randLenegd - 1).values;
	}
	
	public List<Byte> randEquipLegend()
	{
		float r = GameRandom.getRandFloat(0, 1);
		for(SBean.LegendEquipRandCFGS e: this.legend.rands)
		{
			if(r <= e.weight)
				return e.values;
		}
		
		return this.legend.rands.get(this.legend.rands.size() - 1).values;
	}
	
	int getLegendOne(boolean has)
	{
		if(!has)
			return 0;
		
		float r = GameRandom.getRandFloat(0, 1);
		for(int i = 1; i <= this.legend.legendOnes.size(); i++)
		{
			if(r <= this.legend.legendOnes.get(i - 1).weight)
				return i;
		}
		return this.legend.legendOnes.size();
	}
	
	public double getLegenOneBaseAdd(int id)
	{
		if(id <= 0 || id > this.legend.legendOnes.size())
			return 0;
		
		return this.legend.legendOnes.get(id - 1).baseAdd;
	}
	
	int getLegendTwo(boolean has)
	{
		if(!has)
			return 0;
		
		float r = GameRandom.getRandFloat(0, 1);
		for(int i = 1; i <= this.legend.legendTwos.size(); i++)
		{
			if(r <= this.legend.legendTwos.get(i - 1).weight)
				return i;
		}
		return this.legend.legendTwos.size();
	}
	
	public double getLegendTwoAddtionAdd(int id)
	{
		if(id <= 0 || id > this.legend.legendTwos.size())
			return 0;
		
		return this.legend.legendTwos.get(id - 1).addtionAdd;
	}
	
	int getLegendThree(boolean has, int partID)
	{
		if(!has)
			return 0;
		
		SBean.LegendThreePartCFGS part = this.legend.legendThreeParts.get(partID);
		if(part == null || part.effects.isEmpty())
			return 0;
		
		return GameRandom.getRandInt(0, part.effects.size()) + 1;
	}
	
	public SBean.LegendThreeCFGS getLegengThreeCFGS(int partID, int id)
	{
		SBean.LegendThreePartCFGS p = this.legend.legendThreeParts.get(partID);
		if(p == null || id <= 0 || id > p.effects.size())
			return null;
		
		return p.effects.get(id - 1);
	}
	
	public boolean isLegengThreeValid(SBean.DBEquip equip)
	{
		return equip != null && equip.durability > this.common.equip.disableValue && equip.legends.get(2) > 0;
	}
	
	public SBean.LegendMakeCFGS getLegendMakeCFGS()
	{
		return legend.make;
	}
	
	public Set<Integer> getLegendMakeCostItems(int rank, int partID, int lvlReq)
	{
		int key = createEquipRankPartKey(rank, partID);
		SBean.LegendMakeCostCFGS cost = legend.make.makeCost.get(key);
		if(cost == null)
			return null;
		
		for(int i = 0; i < legend.make.lvlReqs.size(); i++)
		{
			if(lvlReq <= legend.make.lvlReqs.get(i))
				return cost.lvls.get(i).set;
		}
		
		return cost.lvls.get(cost.lvls.size() - 1).set;
	}
	
	public SBean.EquipRefineGroupCFGS getEquipRefineGroupCFGS(int groupID)
	{
		return this.equipRefineGroups.get(groupID);
	}
	
	public static List<SBean.Prop> createRefineProp(SBean.EquipRefineGroupCFGS groupCfg, int propCount, int equipLevel)
	{
		List<SBean.Prop> props = new ArrayList<>();
		if(propCount <= 0 || groupCfg.refines.isEmpty())
			return props;
		
		for(int i = 0; i < propCount; ++i)
			props.add(randProp(groupCfg, equipLevel));
		
		return props;
	}
	
	static SBean.Prop randProp(SBean.EquipRefineGroupCFGS groupCfg, int equipLevel)
	{
		float odds = GameRandom.getRandFloat(0, 1.f);
		for(SBean.EquipRefineCFGS e: groupCfg.refines)
		{
			if(odds < e.weight)
				return createProp(e, equipLevel);
		}
		
		return createProp(groupCfg.refines.get(groupCfg.refines.size() - 1), equipLevel);
	}
	
	static SBean.Prop createProp(SBean.EquipRefineCFGS cfg, int equipLevel)
	{
		int value = GameRandom.getRandInt(cfg.minValue, cfg.maxValue);
		for(int i = 0; i < cfg.levels.size(); ++i)
		{
			if(equipLevel <= cfg.levels.get(i))
				return new SBean.Prop(cfg.propID, (int)(value * cfg.multiples.get(i)));
		}
		
		return new SBean.Prop(cfg.propID, (int)(value * cfg.multiples.get(cfg.multiples.size() - 1)));
	}
	
	public SBean.GemCFGS getGemCFG(int id)
	{
		id = id < 0 ? -id : id;
		return gems.get(id);
	}

	public List<SBean.GemCFGS> getGemCFGS()
	{
	    List<SBean.GemCFGS> list = new ArrayList<>(gems.size());
	    gems.forEach((id, cfg) -> list.add(cfg));
	    return list;
	}
	
	public Collection<SBean.GemCFGS> getAllGemCFGS()
	{
		return gems.values();
	}
	
	public SBean.ItemCFGS getItemCFG(int id)
	{
		id = id < 0 ? -id : id;
		return items.get(id);
	}
	
	public Collection<SBean.ItemCFGS> getAllItemCFGS()
	{
		return items.values();
	}
	
	public SBean.BookCFGS getBookCFG(int id)
	{
		id = id < 0 ? -id : id;
		return books.get(id);
	}

	public Collection<SBean.BookCFGS> getAllBookCFGS()
	{
		return books.values();
	}
	
	public SBean.BaseDummyItemCFGS getBaseCFG(int id)
	{
		id = id < 0 ? -id : id;
		return base.get(id);
	}

	public SBean.EquipSlotCFGS getEquipSlotCFG(int id)
	{
		if (id <= 0 || id > slots.size())
			return null;
		return slots.get(id - 1);
	}

	public List<Integer> getEquipSlotType(int id, int seq)
	{
		List<Integer> types = new ArrayList<>();
		if (id <= 0 || id > slots.size())
			return types;
		SBean.EquipSlotCFGS slot = slots.get(id - 1);
		if (seq <= 0 || seq > slot.slot.size())
			return types;
		int slottype = slot.slot.get(seq - 1);
		for (int i = 1; slottype != 0; i++)
		{
			if ((slottype & 1) > 0)
				types.add(i);
			slottype >>= 1;
		}
		return types;
	}

	public SBean.CommonCFGS getCommonCFG()
	{
		return this.common;
	}
	
	public Set<Integer> getFullPackets()
	{
		return battleMap.allPackets;
	}
	
//	public SBean.ClanCFGS getClanCFGS()
//	{
//		return this.clanCFGS;
//	}
//	
//	public SBean.ClanOthersCFGS getClanOthersCFGS()
//	{
//		return this.clanCFGS.others;
//	}
//	
//	public SBean.ClanValueCFGS getClanValueCFGS()
//	{
//		return this.clanValueCFGS;
//	}
//
//	public SBean.ClanTaskCFGS getClanTaskCFGS(int id)
//	{
//		return this.clanTasks.get(id);
//	}
//	
//	public int getClanTaskStar(int id)
//	{
//		return this.clanTasks.get(id).star;
//	}
//
//	public SBean.ClanTaskGroupCFGS getClanTaskGroupCFGS(int level)
//	{
//		return this.clanGroupTasks.get(level);
//	}
//
//	public SBean.ClanItemUpAttriRateCFGS getItemUpAttriRate(int value)
//	{
//		SBean.ClanItemUpAttriRateCFGS itemUpAttriRateCFGS = null;
//		for (int index = 1; index < clanValueCFGS.itemUpAttri.size(); index++)
//		{
//			SBean.ClanItemUpAttriRateCFGS ciprBefore = clanValueCFGS.itemUpAttri.get(index-1);
//			SBean.ClanItemUpAttriRateCFGS ciprRear = clanValueCFGS.itemUpAttri.get(index);
//			if (ciprRear.value <= value || ciprBefore.value <= value)
//			{
//				if (value <= ciprBefore.value)
//				{
//					itemUpAttriRateCFGS = ciprBefore;
//					break;
//				}
//				else if (value <= ciprRear.value)
//				{
//					itemUpAttriRateCFGS = ciprRear;
//					break;
//				}
//			}
//		}
//
//		return itemUpAttriRateCFGS;
//	}
//	
//	public SBean.ClanAttriTotalCFGS getAttriTotalValue(int totalValue)
//	{
//		SBean.ClanAttriTotalCFGS cat = null;
//		for (int index = 0; index < this.clanValueCFGS.attriTotal.size(); index++)
//		{
//			cat = this.clanValueCFGS.attriTotal.get(index);
//			if (cat.attriTotal >= totalValue)
//			{
//				if (index != 0)
//					cat = this.clanValueCFGS.attriTotal.get(index - 1);
//				break;
//			}
//		}
//		return cat;
//	}
//	
//	public SBean.DummyGoods getClanUpLevelDummyGood(int type, SBean.ClanOccupyOreLvlCFGS levelCFGS)
//	{
//		SBean.DummyGoods good = null;
//		if (type == Clan.CLAN_ORE_TYPE_IRON)
//		{
//			if (levelCFGS.ironItemId > 0)
//			{
//				good = new SBean.DummyGoods(levelCFGS.ironItemId, levelCFGS.ironItemCount);
//			}
//			return good;
//		}
//		else if (type == Clan.CLAN_ORE_TYPE_HERB)
//		{
//			if (levelCFGS.herbItemId > 0)
//			{
//				good = new SBean.DummyGoods(levelCFGS.herbItemId, levelCFGS.herbItemCount);
//			}
//			return good;
//		}
//		else if (type == Clan.CLAN_ORE_TYPE_THORPE)
//		{
//			if (levelCFGS.thorpeItemId > 0)
//			{
//				good = new SBean.DummyGoods(levelCFGS.thorpeItemId, levelCFGS.thorpeItemCount);
//			}
//			return good;
//		}
//		return null;
//	}
//	
//	//检查占矿条件
//	public int checkCondClanOreOccupy(Set<Integer> pets, int clanId, int type, int maxLevel, int vipLvl, List<SBean.DBOreRobTeam> occupyOres, Map<Integer, SBean.DBPet> activePets)
//	{
//		if (GameData.getInstance().getVipCFGS(vipLvl).dayClanOccupyOreMaxTimes <= occupyOres.size())
//		{
//			return GameData.PROTOCOL_OP_CLAN_ORE_SIZES;
//		}
//		List<Integer> usePets = new ArrayList<Integer>();
//		for (SBean.DBOreRobTeam ore : occupyOres)
//		{
//			usePets.addAll(ore.pets);
//			if (ore.clanId == clanId && ore.oreType == type)
//			{
//				return GameData.PROTOCOL_OP_FAILED;
//			}
//		}
//		for (int petId : pets)
//		{
//			SBean.DBPet pet = activePets.get(petId);
//			if (pet == null || usePets.contains(Integer.valueOf(petId)))
//			{
//				return GameData.PROTOCOL_OP_FAILED;
//			}
//			if (pet.fightPet.level < maxLevel)
//			{
//				return GameData.PROTOCOL_OP_FAILED;
//			}
//		}
//		return GameData.PROTOCOL_OP_SUCCESS;
//	}
//	
////	//占矿完成条件判断
////	public boolean checkCondClanOreOccupyFinish(int clanId, int type,  List<SBean.DBOreRobTeam> occupyOres)
////	{
////		if(occupyOres == null)
////			return false;;
////		for (SBean.DBOreRobTeam ore : occupyOres)
////		{
////			if (ore.clanId == clanId && ore.oreType == type)
////			{
////				return true;
////			}
////		}
////		return false;
////	}
//	
//	//检查矿是否有效，同一个矿两个小时之内只能被同一个人夺取一次
//	public boolean checkClanOreValidity(int oreType, int serverId, int roleId, List<SBean.DBRobOreRecord> robOreRecords)
//	{
//		int nowTime = GameTime.getTime();
//		Iterator<SBean.DBRobOreRecord> it = robOreRecords.iterator();
//		while(it.hasNext())
//		{
//			SBean.DBRobOreRecord record = it.next();
//			if(nowTime - record.startTime > 2 * 3600) //TODO read this value from excel
//			{
//				it.remove();
//			}
//		}
//		
//		for (SBean.DBRobOreRecord ore : robOreRecords)
//		{
//			if (ore.oreType == oreType && ore.serverId == serverId && ore.roleId == roleId)
//			{
//				if (nowTime - ore.startTime < 2 * 3600)
//				{
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//	
//	//夺矿验证佣兵是否符合条件，佣兵有最低等级限制
//	public boolean checkClanOreHarry(List<Integer> pets, int ownerPet, int maxLevel, Map<Integer, SBean.DBPet> activePets, int ownerPetId)
//	{
//		for (int petId : pets)
//		{
//			SBean.DBPet pet = activePets.get(petId);
//			if (pet == null || pet.fightPet.level < maxLevel)
//			{
//				return false;
//			}
//		}
//		if (ownerPetId > 0 && ownerPet > 0)
//		{
//			if (ownerPetId != ownerPet)
//			{
//				return false;
//			}
//			if (pets.size() + 1 > 3)
//			{
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	public int getAattriAddValue(int type, int totalValue)
//	{
//		List<SBean.ClanDizdtangAttriCFGS> doAttriCFGSList = new ArrayList<>();
//		switch (type) 
//		{
//			case Clan.CLAN_LONGMEN_DZ_ATTRI_WG : 
//			{
//				doAttriCFGSList.addAll(this.clanCFGS.dizitang.wgValue);
//				break;
//			}
//			case Clan.CLAN_LONGMEN_DZ_ATTRI_XF : 
//			{
//				doAttriCFGSList.addAll(this.clanCFGS.dizitang.xfValue);
//				break;
//			}
//			case Clan.CLAN_LONGMEN_DZ_ATTRI_YQ : 
//			{
//				doAttriCFGSList.addAll(this.clanCFGS.dizitang.yqValue);
//				break;
//			}
//			case Clan.CLAN_LONGMEN_DZ_ATTRI_WX :
//			{
//				doAttriCFGSList.addAll(this.clanCFGS.dizitang.tjValue);
//				break;
//			}
//			
//			default:
//				break;
//		}
//		
//		for(SBean.ClanDizdtangAttriCFGS wg : doAttriCFGSList)
//		{
//			if (wg.value >= totalValue)
//			{
//				return wg.value;
//			}
//		}
//		return 0;
//	}
//	
//	//宗门占矿结果计算
//	public int getClanOreBaseValue(int type, int clanLevel, int beRobbedTimes)
//	{
//		if(clanLevel < 1 || clanLevel > this.clanCFGS.occupyOre.oreLvl.size())
//			return 0;
//		int c = 0;
//		float additionRate = 0;
//		float robRate = this.clanCFGS.occupyOre.harryBase;
//		if(type == Clan.CLAN_ORE_TYPE_IRON)
//		{
//			c  = this.clanCFGS.occupyOre.ironBaseValue;
//			additionRate = this.clanCFGS.occupyOre.oreLvl.get(clanLevel - 1).ironAdditionRate;
//		}
//		if(type ==  Clan.CLAN_ORE_TYPE_HERB)
//		{
//			c = this.clanCFGS.occupyOre.herbBaseValue;
//			additionRate = this.clanCFGS.occupyOre.oreLvl.get(clanLevel - 1).herbAdditionRate;
//		}
//		if(type == Clan.CLAN_ORE_TYPE_THORPE)
//		{
//			c = this.clanCFGS.occupyOre.thorpeBaseValue;
//			additionRate = this.clanCFGS.occupyOre.oreLvl.get(clanLevel - 1).thorpeAdditionRate;
//		}
//		int result = (int) Math.ceil(40 * c * (1 + additionRate) * (Math.pow((1 - robRate), beRobbedTimes )));
//		return result;
//	}

	public SBean.ArenaCFGS getArenaCFGS()
	{
		return this.arenaCfg;
	}
	
	public int getSuperArenaGradeSize(int arenaType)
	{
		switch (arenaType)
		{
		case GameData.MAPCOPY_SUPERARENA_NORMAL:
			return this.superarena.grades4v4.size();
		case GameData.MAPCOPY_SUPERARENA_THREEBEST:
			return this.superarena.grades2v2.size();
		default:
			break;
		}
		
		return this.superarena.grades4v4.size();
	}
	
	public int getELOGrade(int elo, int arenaType)
	{
		switch (arenaType)
		{
		case GameData.MAPCOPY_SUPERARENA_NORMAL:
			return getELOGrade(elo, this.superarena.elos4v4);
		case GameData.MAPCOPY_SUPERARENA_THREEBEST:
			return getELOGrade(elo, this.superarena.elos2v2);
		default:
			return getELOGrade(elo, this.superarena.elos4v4);
		}
	}
	
	public static int getELOGrade(int elo, List<SBean.ELOGradeCFG> elos)
	{
		for(int eloGrade = 1; eloGrade <= elos.size(); eloGrade++)
		{
			if(elo <= elos.get(eloGrade - 1).ceiling)
				return eloGrade;
		}
		
		return elos.size();
	}
	
	public List<SBean.ELOGradeCFG> getAllELOGradeByType(int arenaType)
	{
		switch (arenaType)
		{
		case GameData.MAPCOPY_SUPERARENA_NORMAL:
			return this.superarena.elos4v4;
		case GameData.MAPCOPY_SUPERARENA_THREEBEST:
			return this.superarena.elos2v2;
		default:
			return this.superarena.elos4v4;
		}
	}
	
	public SBean.SuperArenaCFGS getSuperArenaCFGS()
	{
		return this.superarena;
	}
	
	public int getSuperArenaFailedBuff(int failedStreak)
	{
		if(failedStreak <= 0)
			return 0;
		
		failedStreak = failedStreak > this.superarena.buff.failedBuffs.size() ? this.superarena.buff.failedBuffs.size() : failedStreak;
		return this.superarena.buff.failedBuffs.get(failedStreak - 1);
	}
	
	public int getSuperArenaGrade(int arenaType, int lvl)
	{
		switch (arenaType)
		{
		case GameData.MAPCOPY_SUPERARENA_NORMAL:
			for(int i=0; i<this.superarena.grades4v4.size(); i++)
			{
				if(lvl >= this.superarena.grades4v4.get(i).min && lvl <= this.superarena.grades4v4.get(i).max)
					return i + 1;
			}
			
			return this.superarena.grades4v4.size();
		case GameData.MAPCOPY_SUPERARENA_THREEBEST:
			for(int i=0; i<this.superarena.grades2v2.size(); i++)
			{
				if(lvl >= this.superarena.grades2v2.get(i).min && lvl <= this.superarena.grades2v2.get(i).max)
					return i + 1;
			}
			
			return this.superarena.grades2v2.size();
		default:
			break;
		}
		
		return 0;
	}
	
	public List<Integer> searchArenaEnemies(int rankSelf)
	{
		SBean.ArenaTargetCFGS t = this.arenaCfg.targets.get(this.arenaCfg.targets.size()-1);
		int seeMax = -1;
		for (SBean.ArenaTargetCFGS e : this.arenaCfg.targets)
		{
			if (rankSelf <= e.rankFloor)
			{
				t = e;
				seeMax = e.seeMax;
				break;
			}
		}
		
		List<Integer> enemies = new ArrayList<>();
		for (SBean.ArenaTargetRankDeltaCFGS e : t.deltaRank)
		{
			int min = e.deltaMin + rankSelf;
			int max = e.deltaMax + rankSelf;
			
			if(min < seeMax)
				min = seeMax;
			
			if(max < seeMax)
				max = seeMax;
			
			int rank = GameRandom.getRandInt(min, max + 1);
			enemies.add(rank);
		}
		return enemies;
	}
	
	public SBean.ArenaRobotCFGS getArenaRobotCFGS(int robotID)
	{
		return this.arenaRobots.get(robotID);
	}
	
//	public SBean.RoleOverview createArenaRobotRoleOverview(int rank, int rid)
//	{
//		Random r = new Random(rank);
//		int gender = r.nextInt(2) + 1;
//		int headIcon = GameData.getInstance().getRoleHeadIcon((byte)gender, (byte)1, (byte)1);
//		String name = GameData.getInstance().getRandomName(r, gender);
//		return new SBean.RoleOverview(rid, name, gender, headIcon, 0, 0, 0, 0, 0);
//	}

	public int getArenaRobotID(int rank)
	{
		SBean.ArenaRobotGroupCFGS group = null;
		for (SBean.ArenaRobotGroupCFGS e : this.arenaRobotGroup.values())
		{
			if (rank >= e.rankMax && rank <= e.rankMin)
			{
				group = e;
				break;
			}
		}

		if (group == null)
			return 0;

		int rnd = GameRandom.getRandInt(0, group.robots.size());

		return -group.robots.get(rnd).id;
	}
	
	public SBean.BWArenaCFGS getBWArenaCFGS()
	{
		return this.bwarena;
	}
	
	public SBean.BWArenaLvlCFGS getBWArenaLvlCFGS(int lvl)
	{
		if(lvl <= 0 || lvl > this.bwarena.lvls.size())
			return null;
		
		return this.bwarena.lvls.get(lvl - 1);
	}
	
	public SBean.BWArenaRankCFGS getBWArenaRankCFGS(int rank)
	{
		if(rank <=0 || rank > this.bwarena.ranks.size())
			return null;
		
		return this.bwarena.ranks.get(rank - 1);
	}
	
	public LevelExp getAddBWArenaExpLvlExp(int curLvl, int curExp, int addExp)
	{
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for (; lvl <= this.bwarena.lvls.size(); ++lvl)
		{
			SBean.BWArenaLvlCFGS cfg = this.bwarena.lvls.get(lvl - 1);
			if (exp >= cfg.expReq)
			{
				exp -= cfg.expReq;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= this.bwarena.lvls.size())
		{
			curLvl = this.bwarena.lvls.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}
	
	public static Map<Integer, SBean.FightPet> getBWArenaFightPets(Map<Integer, SBean.FightPet> fightPets, int maxCount)
	{
		Set<Integer> usePets = getBWArenaFightPets(new ArrayList<>(fightPets.keySet()), maxCount);		
		Map<Integer, SBean.FightPet> useFightPets = new HashMap<>();
		for(int pid: usePets)
		{
			SBean.FightPet p = fightPets.get(pid);
			if(p != null)
				useFightPets.put(pid, p);
		}
		
		return useFightPets;
	}
	
	public static Set<Integer> getBWArenaFightPets(List<Integer> pets, int maxCount)
	{
		int dCount = maxCount - pets.size();
		for(int i=0; i<dCount; i++)
			pets.add(0);
		
		int useCount = pets.size() > BWARENA_PET_USE_COUNT ? BWARENA_PET_USE_COUNT : pets.size();
		Set<Integer> usePets = new HashSet<>();
		for(int i=0; i<useCount; i++)
		{
			int rnd = GameRandom.getRandom().nextInt(pets.size());
			usePets.add(pets.remove(rnd));
		}
		
		return usePets;
	}
	
	public Collection<SBean.ForceWarCFGS> getAllForceWarCFGS()
	{
		return this.forcewar.values();
	}
	
	public SBean.ForceWarCFGS getForceWarCFGS(int type)
	{
		return this.forcewar.get(type);
	}
	
	public int getForceWarMap(List<Integer> mapIDs, int level)
	{
		int index = 1;
		for(int i = 1; i <= this.forcewarBase.levels.size(); i++)
		{
			if(level <= this.forcewarBase.levels.get(i - 1))
				index = i;
		}
		
		index = index >= mapIDs.size() ? mapIDs.size() : index;
		return mapIDs.get(index - 1);
	}
	
	public SBean.ForceWarBaseCFGS getForceWarBaseCFGS()
	{
		return this.forcewarBase;
	}
	
	public static float getForceWarDayFeatPercent(int times, List<Float> feats)
	{
		if(times <= 0)
			return 0;
		
		int index = times > feats.size() ? feats.size() - 1 : times - 1;
		return feats.get(index);
	}
	
	public int getForceWarFeatByRank(int rank, boolean win)
	{
		for(SBean.ForceWarFeat e: this.forcewarBase.feat.rewards)
		{
			if(rank <= e.floor)
				return win ? e.win : e.lose;
		}
		
		return win ? this.forcewarBase.feat.rewards.get(this.forcewarBase.feat.rewards.size() - 1).win : this.forcewarBase.feat.rewards.get(this.forcewarBase.feat.rewards.size() - 1).lose;
	}
	
	public int getForceWarTotalFeatLvl(int totalFeat)
	{
		for(int i=0; i<this.forcewarBase.rewards.size(); i++)
		{
			if(totalFeat <= this.forcewarBase.rewards.get(i).floor)
				return i;
		}
		
		return this.forcewarBase.rewards.size() - 1;
	}
	
	public SBean.ForceWarRewardCFGS getForceWarReward(int lvl)
	{
		if(lvl < 0 || lvl >= this.forcewarBase.rewards.size())
			return null;
		
		return this.forcewarBase.rewards.get(lvl);
	}
	
	public static List<SBean.DBEquipPart> createNewRoleWearParts()
	{
		List<SBean.DBEquipPart> wearParts = new ArrayList<>();
		for (int i = 0; i < GameData.EQUIP_MAX_PARTNUM; ++i)
			wearParts.add(createDefaultWearPart(i+1));
		return wearParts;
	}
	
	
	public static SBean.DBEquipPart createDefaultWearPart(int wearId)
	{
		SBean.EquipSlotCFGS slotCfg = GameData.getInstance().getEquipSlotCFG(wearId);
		if (slotCfg == null)
			return null;
		List<Integer> partSlots = new ArrayList<>();
		for (int s = 0; s < slotCfg.slot.size(); ++s)
		{
			partSlots.add(0);	
		}
		return new SBean.DBEquipPart(wearId, 0, 0, partSlots,0);
	}
	
	public SBean.BattleEnemyOverview getArenaRobotBattleArrayOverview(int rank, int rid)
	{
		SBean.ArenaRobotCFGS cfg = GameData.getInstance().getArenaRobotCFGS(-rid);
		if (cfg == null)
			return null;
		
		Random r = new Random(rank + 5772156649L);
		int gender = r.nextInt(2) + 1;
		int fashionID = (cfg.classType - 1) * 2 + gender;
		SBean.ClassRoleFashionCFGS fashionCfg = getClassRoleFashionCFGS(fashionID);
		if(fashionCfg == null)
			return null;
		
		byte face = fashionCfg.faces.get(r.nextInt(fashionCfg.faces.size()));
		byte hair = fashionCfg.hairs.get(r.nextInt(fashionCfg.hairs.size()));
		int headIcon = GameData.getInstance().getRoleHeadIcon((byte)gender, (byte)face, (byte)hair);
		String name = GameData.getInstance().getRandomName(r, gender);
		SBean.RoleOverview role = new SBean.RoleOverview(rid, name, gender, headIcon, cfg.classType, cfg.transformLevel, (byte)0, cfg.lvl, 0);
		List<SBean.PetOverview> pets = new ArrayList<>();
		for (SBean.PetBriefCFGS p : cfg.pets)
		{
			SBean.PetOverview pet = new SBean.PetOverview(p.id, p.lvl, 0, 0);
			pets.add(pet);
		}
		return new SBean.BattleEnemyOverview(new SBean.RoleSocial(role, 0, "", ""), pets, (byte)0);
	}
	
	public SBean.BattleArray getArenaRobotBattleArray(int rank, int rid)
	{
		SBean.ArenaRobotCFGS cfg = GameData.getInstance().getArenaRobotCFGS(-rid);
		if (cfg == null)
			return null;
		Random r = new Random(rank + 5772156649L);
		int gender = r.nextInt(2) + 1;
		int fashionID = (cfg.classType - 1) * 2 + gender;
		SBean.ClassRoleFashionCFGS fashionCfg = getClassRoleFashionCFGS(fashionID);
		if(fashionCfg == null)
			return null;
		
		byte face = fashionCfg.faces.get(r.nextInt(fashionCfg.faces.size()));
		byte hair = fashionCfg.hairs.get(r.nextInt(fashionCfg.hairs.size()));
		
		int headIcon = GameData.getInstance().getRoleHeadIcon((byte)gender, face, hair);
		String name = GameData.getInstance().getRandomName(r, gender);
		SBean.BasePlayer basePlay = new SBean.BasePlayer(rid, cfg.classType, cfg.transformLevel, (byte)0, cfg.lvl, new HashSet<>(), emptyMap(), 
														 emptyList(), new HashMap<Integer, SBean.DBSkill>(), emptyMap(), emptyMap(), emptySet(), 
														 new HashMap<Integer, Integer>(), new SBean.DBHorse(emptyMap(), new HashMap<Integer, Integer>(), 0, 0), 
														 emptyMap(), new HashMap<>(),new SBean.DBSealData(0, emptyMap(), 0, emptyMap()), emptyMap(), emptyMap(),
														 new SBean.DBRoleTitle(emptyMap(), 0, emptyList(), GameData.INIT_TITLESLOT_SIZE), emptySet(), emptyMap(), 
														 null, new HashMap<>(), 0, 0, emptyMap(), emptyMap(), emptyMap());
		
		SBean.FightRole fightRole = new SBean.FightRole(basePlay, name, (byte)gender, (short)headIcon, face, hair, new ArrayList<>(), 0, 0, 
														null, new SBean.SectBrief(0, "", (byte)-1, (short) 0, 0), 0, new HashMap<>(), (byte)0);

		cfg.skills.stream().filter(s -> s.id > 0).forEach(s -> {
			fightRole.base.skills.put(s.id, new SBean.DBSkill(s.id, s.lvl, 0));
			fightRole.curSkills.add(s.id);
		});
		
		Map<Integer, SBean.FightPet> fightPets = new TreeMap<>();
		List<Integer> petSeq = new ArrayList<>();
		for (SBean.PetBriefCFGS p : cfg.pets)
		{
			SBean.FightPet pet = new SBean.FightPet(p.id, p.lvl, 0, new TreeMap<>(), 0, 0, getInitPetSkill(p.id), emptyList());
			fightPets.put(p.id, pet);
			petSeq.add(p.id);
		}
		return new SBean.BattleArray(fightRole, fightPets, petSeq);
	}

	public List<Integer> getInitPetSkill(int id)
	{
		List<Integer> skills = new ArrayList<>();
		if (this.getPetCFG(id) != null)
			for (int i = 0; i < this.getPetCFG(id).skills.size(); i++)
				skills.add(1);
		return skills;
	}

	public SBean.RoleBrief getArenaRobotBrief(int rid, int rank)
	{
		SBean.ArenaRobotCFGS cfg = GameData.getInstance().getArenaRobotCFGS(-rid);
		if (cfg == null)
			return null;
		
		Random r = new Random(rank + 5772156649L);
		int gender = r.nextInt(2) + 1;
		int fashionID = (cfg.classType - 1) * 2 + gender;
		SBean.ClassRoleFashionCFGS fashionCfg = getClassRoleFashionCFGS(fashionID);
		if(fashionCfg == null)
			return null;
		
		byte face = fashionCfg.faces.get(r.nextInt(fashionCfg.faces.size()));
		byte hair = fashionCfg.hairs.get(r.nextInt(fashionCfg.hairs.size()));
		String name = GameData.getInstance().getRandomName(r, gender);
		
		return new SBean.RoleBrief(new SBean.RoleOverview(rid, name, gender, 0, cfg.classType, cfg.transformLevel, (byte)0, cfg.lvl, cfg.power), 
				new SBean.RoleModel(face, hair, new HashMap<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>(), new SBean.ArmorBrief(0, 0), new SBean.HeirloomBrief((byte)0, 0)));
	}
	
	public static SBean.RoleOverview getRoleOverviewFromFightRole(SBean.FightRole frole)
	{
		return new SBean.RoleOverview(frole.base.roleID, frole.roleName, frole.gender, frole.headIcon, frole.base.classType, frole.base.transformLevel, frole.base.BWType, frole.base.level, frole.fightPower);
	}
	
	public static SBean.PetOverview getPetOverviewFromFightPet(SBean.FightPet fpet)
	{
		return new SBean.PetOverview(fpet.id, fpet.level, fpet.star, fpet.fightPower);
	}
	
	public static Map<Integer, SBean.PetOverview> getPetOverviewsFromFightPets(Map<Integer, SBean.FightPet> fpets)
	{
		Map<Integer, SBean.PetOverview> overviews = new TreeMap<>();
		for (SBean.FightPet fpet : fpets.values())
		{
			overviews.put(fpet.id, getPetOverviewFromFightPet(fpet));
		}
		return overviews;
	}
	
	public static SBean.BattleArrayOverview getBattleArrayOverviewFromBattleArray(SBean.BattleArray battleArray)
	{
		return new SBean.BattleArrayOverview(new SBean.RoleSocial(getRoleOverviewFromFightRole(battleArray.fightRole), battleArray.fightRole.sectBrief.sectID, battleArray.fightRole.sectBrief.sectName, ""), getPetOverviewsFromFightPets(battleArray.fightPets));
	}
	
	public static SBean.BattleArrayProfile getBattleArrayProfileFromBattleArrayOverviewHP(SBean.BattleArrayOverview overview, SBean.BattleArrayHp hp)
	{
		SBean.RoleProfile roleProfile = new SBean.RoleProfile(overview.roleSocial.role, hp.roleHp.curValue, hp.roleHp.maxValue);
		Map<Integer, SBean.PetProfile> petsProfile = new TreeMap<>();
		for (SBean.PetOverview p : overview.pets.values())
		{
			SBean.PetProfile petProfile = new SBean.PetProfile(p, 0, 0);
			SBean.Hp petHp = hp.petsHp.get(p.id);
			if (petHp != null)
			{
				petProfile.curHp = petHp.curValue;
				petProfile.maxHp = petHp.maxValue;
			}
			petsProfile.put(petProfile.overview.id, petProfile);
		}
		return new SBean.BattleArrayProfile(roleProfile, petsProfile);
	}

	public SBean.ArenaBestRankRewardCFGS getArenaRankBestReward(int rankNow, int bestRank)
	{
		if(bestRank >= rankNow)
			return new SBean.ArenaBestRankRewardCFGS(0, 0, 0);
		
		SBean.ArenaBestRankRewardCFGS now = getArenaRankBestReward(rankNow);
		SBean.ArenaBestRankRewardCFGS best = getArenaRankBestReward(bestRank);
		//TODO del 
//		System.err.println("rankNow " + rankNow + " money " + now.money + " stone " + now.stone);
//		System.err.println("bestRank " + bestRank + " money " + best.money + " stone " + best.stone);
		
		return new SBean.ArenaBestRankRewardCFGS(0, best.money - now.money, best.stone - now.stone);
	}

	public SBean.ArenaBestRankRewardCFGS getArenaRankBestReward(int rank)
	{
		if(rank == 10000)
			return new SBean.ArenaBestRankRewardCFGS(0, 0, 0);
		
		int money = 0;
		int stone = 0;
		int rankFloor = 10000;
		for(int i=this.arenaCfg.bestRankRewards.size() - 1; i>=0; i--)
		{
			SBean.ArenaBestRankRewardCFGS e = this.arenaCfg.bestRankRewards.get(i);
			if(rank < e.rankFloor)
			{
				money = e.money;
				stone = e.stone;
				rankFloor = e.rankFloor;
				continue;
			}
			
			if(i >= 0)
			{
				float per = (rankFloor - rank)/(float)(rankFloor - e.rankFloor);
				money = money + (int) (Math.ceil((e.money - money) * per));
				stone = stone + (int) (Math.ceil((e.stone - stone) * per));
			}
			break;
		}
		return new SBean.ArenaBestRankRewardCFGS(0, money, stone);
	}
	
	public List<SBean.DummyGoods> getFixedDrop(int tblID, int ratio)
	{
		SBean.FixedDropTableCFGS cfg = fixedDrops.get(tblID);
		if (cfg == null || cfg.drops.isEmpty())
			return GameData.emptyList();
		
		List<SBean.DummyGoods> drops = new ArrayList<>();
		for (SBean.DropEntity drop : cfg.drops)
		{
			int count = GameRandom.getRandInt(drop.minCount, drop.maxCount);
			for (int i = 0; i < ratio; ++i)
				drops.add(new SBean.DummyGoods(drop.did, count));
		}
		return drops;
	}
	
	public Map<Integer, Integer> getMergedFixedDrop(int tblID, int ratio)
	{
		SBean.FixedDropTableCFGS cfg = fixedDrops.get(tblID);
		if (cfg == null || cfg.drops.isEmpty())
			return GameData.emptyMap();
		Map<Integer, Integer> drops = new TreeMap<>();
		for (SBean.DropEntity drop : cfg.drops)
		{
			int count = GameRandom.getRandInt(drop.minCount, drop.maxCount);
			drops.merge(drop.did, count*ratio, (ov, nv)->ov+nv);
		}
		return drops;
	}

	public List<SBean.DropItemCFGS> getNoDuplicateDrops(int tblID, int dropCount)
	{
		SBean.NoDuplicateDropTableCFGS cfg = noDuplicateDrops.get(tblID);
		if (cfg == null || cfg.drops.isEmpty() || cfg.drops.size() < dropCount)
			return null;
		List<SBean.DropItemCFGS> copy = new ArrayList<>();
		copy.addAll(cfg.drops);
		List<SBean.DropItemCFGS> lst = new ArrayList<>();
		int weightSum = cfg.weightSum;
		for (int i = 0; i < dropCount; ++i)
		{
			int r = GameRandom.getRandom().nextInt(weightSum);
			int curSum = 0;
			SBean.DropItemCFGS dropcfg = copy.get(copy.size() - 1);
			Iterator<SBean.DropItemCFGS> it = copy.iterator();
			while (it.hasNext())
			{
				SBean.DropItemCFGS e = it.next();
				curSum += e.weight;
				if (r < curSum)
				{
					dropcfg = e;
					it.remove();
					break;
				}
			}
			weightSum -= dropcfg.weight;
			lst.add(dropcfg);
		}
		return lst;
	}

	public SBean.DummyGoods getNextDrop(List<SBean.DropItemCFGS> drops)
	{
		if (drops == null || drops.isEmpty())
			return null;
		int weightSum = 0;
		for (SBean.DropItemCFGS e : drops)
		{
			weightSum += e.weight;
		}
		int r = GameRandom.getRandom().nextInt(weightSum);
		int curSum = 0;
		SBean.DropItemCFGS dropcfg = drops.get(drops.size() - 1);
		Iterator<SBean.DropItemCFGS> it = drops.iterator();
		while (it.hasNext())
		{
			SBean.DropItemCFGS e = it.next();
			curSum += e.weight;
			if (r < curSum)
			{
				dropcfg = e;
				it.remove();
				break;
			}
		}
		return new SBean.DummyGoods(dropcfg.did, dropcfg.count);
	}

	public SBean.DropEntry createRandomDropEntry(SBean.RandomDropTableCFGS cfg)
	{
		float r = GameRandom.getRandom().nextFloat();
		SBean.DropEntry dropcfg = cfg.drops.get(cfg.drops.size() - 1);
		for (SBean.DropEntry e : cfg.drops)
		{
			if (r < e.probability)
			{
				dropcfg = e;
				break;
			}
		}
		
		return dropcfg;
	}
	
	public SBean.DropEntity getRandomDropEntity(List<SBean.DropEntry> drops)
	{
		float r = GameRandom.getRandom().nextFloat();
		SBean.DropEntity dropEntity = null;
		for (SBean.DropEntry e : drops)
		{
			if (r < e.probability)
			{
				dropEntity = e.drop;
				break;
			}
		}
		return dropEntity;
	}
	
	public SBean.DummyGoods getRandomDrop(List<SBean.DropEntry> drops)
	{
		SBean.DropEntity dropEntity = getRandomDropEntity(drops);
		if (dropEntity != null && dropEntity.did != GameData.COMMON_ITEM_ID_NULL)
		{
			int count = GameRandom.getRandInt(dropEntity.minCount, dropEntity.maxCount);
			return new SBean.DummyGoods(dropEntity.did, count);
		}
		return null;
	}
	
	public Map<Integer, Integer> mergeBatchFixedDrop(int tblID, int dropCount, Map<Integer, Integer> drops)
	{
		SBean.FixedDropTableCFGS cfg = fixedDrops.get(tblID);
		if (cfg == null || cfg.drops.isEmpty())
			return GameData.emptyMap();
		
		for (SBean.DropEntity drop : cfg.drops)
		{
			int count = (drop.minCount + drop.maxCount) / 2 * dropCount;
			if (drop.did != 0 && count > 0)
				drops.merge(drop.did, count, (ov, nv) -> ov + nv);
		}
		return drops;
	}

	public Map<Integer, Integer> mergeBatchRandomDrop(int tblID, int dropCount, Map<Integer, Integer> drops)
	{
		SBean.RandomDropTableCFGS cfg = randomDrops.get(tblID);
		if (cfg == null || cfg.drops.isEmpty() || dropCount <= 0)
			return GameData.emptyMap();
		
		float lastprob = 0;
		for (SBean.DropEntry drop : cfg.drops)
		{
			int count = (int) ((drop.drop.minCount + drop.drop.maxCount) / 2 * dropCount * (drop.probability - lastprob));
			lastprob = drop.probability;
			if (drop.drop.did != 0 && count > 0)
				drops.merge(drop.drop.did, count, (ov, nv) -> ov + nv);
		}
		return drops;
	}
	
	public List<SBean.DummyGoods> getRandomDrop(int tblID, int dropCount, int dropRatio)
	{
		SBean.RandomDropTableCFGS cfg = randomDrops.get(tblID);
		if (cfg == null || cfg.drops.isEmpty() || dropCount <= 0)
			return GameData.emptyList();
		
		List<SBean.DummyGoods> drops = new ArrayList<>();
		for (int i = 0; i < dropCount; ++i)
		{
			SBean.DummyGoods drop = getRandomDrop(cfg.drops);
			if (drop != null)
				for (int j = 0; j < dropRatio; ++j)
					drops.add(drop);
		}
		return drops;
	}
	
	public Map<Integer, Integer> getMergedRandomDrop(int tblID, int dropCount, int dropRatio)
	{
		SBean.RandomDropTableCFGS cfg = randomDrops.get(tblID);
		if (cfg == null || cfg.drops.isEmpty() || dropCount <= 0)
			return emptyMap();
		Map<Integer, Integer> drops = new TreeMap<>();
		for (int i = 0; i < dropCount; ++i)
		{
			SBean.DropEntry dropcfg = createRandomDropEntry(cfg);
			if (dropcfg.drop.did != GameData.COMMON_ITEM_ID_NULL)
			{
				int count = GameRandom.getRandInt(dropcfg.drop.minCount, dropcfg.drop.maxCount);
				drops.merge(dropcfg.drop.did, count*dropRatio, (ov, nv)->ov+nv);
			}
		}
		return drops;
	}

	public static List<SBean.DummyGoods> convertDrops(List<SBean.DropItemCFGS> drops)
	{
		List<SBean.DummyGoods> dummyGoods = new ArrayList<>();
		if (drops == null)
			return dummyGoods;
		
		for (SBean.DropItemCFGS drop : drops)
			dummyGoods.add(new SBean.DummyGoods(drop.did, drop.count));
		
		return dummyGoods;
	}

	public static SBean.DropRatio getMonsterDoubleDropRatio(int mapId, int monsterId, SBean.DoubleDropCfg cfg)
	{
		if (cfg == null)
			return defaultDropRatio;
		SBean.DropRatio dr = null;
		dr = cfg.monsters.get(monsterId);
		if (dr != null)
			return dr;
		dr = cfg.mapcopys.get(mapId);
		if (dr != null)
			return dr;
		return defaultDropRatio;
	}

	public static SBean.RewardRatio getMapcopyRewardRatio(int mapId, GameConf.DoubleDropConfig cfg)
	{
		if (cfg == null)
			return defaultRewardRatio;
		SBean.RewardRatio rr = cfg.getMapcopyRewardRatio(mapId);
		if (rr != null)
			return rr;
		return defaultRewardRatio;
	}

	public static SBean.ExtraDropTbl getMonsterExtraDropTable(int mapId, int monsterId, SBean.ExtraDropCfg cfg)
	{
		if (cfg == null)
			return null;
		SBean.ExtraDropTbl ed;
		ed = cfg.monsters.get(monsterId);
		if (ed != null)
			return ed;
		ed = cfg.mapcopys.get(mapId);
		return ed;
	}

	public static List<SBean.DummyGoods> getDrops(int fixedDropId, int randomDropId, int randomDropCount, int fixedDropRatio, int randomDropRatio, SBean.ExtraDropTbl extraDropTbl)
	{
		List<SBean.DummyGoods> drops = new ArrayList<>();
		drops.addAll(GameData.getInstance().getFixedDrop(fixedDropId, fixedDropRatio));
		randomDropCount = randomDropRatio > 0 ? randomDropCount * randomDropRatio : randomDropCount; 
		drops.addAll(GameData.getInstance().getRandomDrop(randomDropId, randomDropCount, 1));
		if(extraDropTbl != null)
		{
			SBean.DummyGoods extra = GameData.getInstance().getRandomDrop(extraDropTbl.drops);
			if(extra != null)
				drops.add(extra);
		}
		return drops;
	}
	
	public static Map<Integer, Integer> getMergedDrops(int fixedDropId, int randomDropId, int randomDropCount, int fixedDropRatio, int randomDropRatio, SBean.ExtraDropTbl extraDropTbl, Map<Integer, Integer> drops)
	{
		drops = drops == null ? new TreeMap<>() : drops;
		Map<Integer, Integer> fixedDropItems = GameData.getInstance().getMergedFixedDrop(fixedDropId, 1);
		for (Map.Entry<Integer, Integer> e : fixedDropItems.entrySet())
		{
			drops.merge(e.getKey(), e.getValue() * fixedDropRatio, (ov, nv) ->ov+nv);
		}

		randomDropCount = randomDropRatio > 0 ? randomDropCount * randomDropRatio : randomDropCount;
		Map<Integer, Integer> randomDropItems = GameData.getInstance().getMergedRandomDrop(randomDropId, randomDropCount, 1);
		for (Map.Entry<Integer, Integer> e : randomDropItems.entrySet())
		{
			drops.merge(e.getKey(), e.getValue(), (ov, nv) ->ov+nv);
//			drops.merge(e.getKey(), e.getValue() * randomDropRatio, (ov, nv) ->ov+nv);
		}
		if(extraDropTbl != null)
		{
			SBean.DummyGoods extra = GameData.getInstance().getRandomDrop(extraDropTbl.drops);
			if(extra != null)
				drops.merge(extra.id, extra.count, (ov, nv)->ov+nv);
		}
		return drops;
	}
	
	public List<Integer> getBuffDrops(int tblID, int count)
	{
		List<Integer> lst = new ArrayList<>();
		SBean.BuffDropTableCFGS cfg = this.buffDrops.get(tblID);
		if (cfg == null)
			return lst;

		float r = 0.0f;
		int mapBuffID = 0;
		for (int i = 0; i < count; i++)
		{
			r = GameRandom.getRandom().nextFloat();
			for (SBean.DropBuffCFGS e : cfg.drops)
			{
				if (r < e.probability)
				{
					mapBuffID = e.mapBuffID;
					break;
				}
			}

			if (mapBuffID > 0)
				lst.add(mapBuffID);
		}
		return lst;
	}
	
	public static int getTimesCost(List<Integer> timesCosts, int times)
	{
		int index = times > timesCosts.size() ? timesCosts.size() - 1 : times - 1;
		return timesCosts.get(index >= 0 ? index : 0);
	}

	public static int getSuperArenaKillHonor(List<Integer> honors, int killCount)
	{
		if(killCount <= 0 || killCount > honors.size())
			return 0;
		
		return honors.get(killCount - 1);
	}
	
	public static int getSuperArenaAddHonorPencent(List<Integer> percents, int times)
	{
		if(times <= 0 || times > percents.size())
			return 0;
		
		return percents.get(times - 1);
	}
	
	public int getShopTypeCount()
	{
		return this.shops.size();
	}

	public SBean.ShopCFGS getShopCFG(int shopId)
	{
		if (shopId <= 0 || shopId > this.shops.size())
			return null;
		return this.shops.get(shopId - 1);
	}

	public static List<SBean.DBShopGoods> getShopGoods(SBean.ShopCFGS cfg, int level)
	{
		List<SBean.DBShopGoods> goods = new ArrayList<>();
		for (Map.Entry<Integer, SBean.ShopGoodsLevelCFGS> e : cfg.levels.entrySet())
		{
			if (level <= e.getKey())
			{
				SBean.ShopGoodsLevelCFGS lcfg = e.getValue();
				for (int i = 1; i < lcfg.groups.size(); ++i)
				{
					SBean.ShopGoodsCFGS sgcfg = getShopRandGoods(lcfg.groups.get(i));
					if (sgcfg != null)
					{
						goods.add(new SBean.DBShopGoods(sgcfg.id, 0));
					}
				}
				while (goods.size() < cfg.refreshCount)
				{
					SBean.ShopGoodsCFGS sgcfg = getShopRandGoods(lcfg.groups.get(0));
					if (sgcfg != null)
					{
						goods.add(new SBean.DBShopGoods(sgcfg.id, 0));
					}
				}
				break;
			}
		}
		return goods;
	}
	
	private static SBean.ShopGoodsCFGS getShopRandGoods(SBean.ShopGoodsGroupCFGS cfg)
	{
		if (cfg.goods.isEmpty())
			return null;
		float r = GameRandom.getRandom().nextFloat();
		for (SBean.ShopGoodsCFGS e : cfg.goods)
		{
			if (r < e.pro && GameTime.getTime() > getRefreshTimeByDayTime(e.startdate))
				return e;
		}
		return null;
	}

//	public static int getShopLastRefreshTime(SBean.ShopCFGS cfg)
//	{
//		int s = GameTime.getSecondOfDay();
//		int lastRefreshTime = cfg.refreshTime.get(cfg.refreshTime.size() - 1) - GameTime.getDayTimeSpan();
//		for (int t : cfg.refreshTime)
//		{
//			if (s >= t)
//				lastRefreshTime = t;
//		}
//		return GameTime.getDayTime(lastRefreshTime);
//	}
	
	public static int getLastRefreshTime(int now, List<Integer> refreshTimes)
	{
		int s = GameTime.getSecondOfDay(now);
		int lastRefreshTime = refreshTimes.get(refreshTimes.size() - 1) - GameTime.getDayTimeSpan();
		for (int t : refreshTimes)
		{
			if (s >= t)
				lastRefreshTime = t;
		}
		return GameTime.getDayTime(lastRefreshTime);
	}

	// public static int getShopRefreshPrice(SBean.ShopCFGS cfg, int times)
	// {
	// int index = times > cfg.refreshPrice.size() ? cfg.refreshPrice.size()-1 :
	// times-1;
	// return cfg.refreshPrice.get(index);
	// }

	public static SBean.ShopGoodsCFGS getShopGoodsById(SBean.ShopCFGS cfg, int id)
	{
		return cfg.allGoods.get(id);
	}
	
	public int getGambleShopTypeCount()
	{
		return this.gambleShops.size();
	}

	public SBean.GambleShopCFGS getGambleShopCFG(int shopId)
	{
		if (shopId <= 0 || shopId > this.gambleShops.size())
			return null;
		return this.gambleShops.get(shopId - 1);
	}

	public static List<SBean.DBGambleShopGoods> getGambleShopGoods(SBean.GambleShopCFGS cfg, int level)
	{
		List<SBean.DBGambleShopGoods> goods = new ArrayList<>();
		for (Map.Entry<Integer, SBean.GambleShopGoodsLevelCFGS> e : cfg.levels.entrySet())
		{
			if (level <= e.getKey())
			{
				SBean.GambleShopGoodsLevelCFGS lcfg = e.getValue();
				for (int i = 1; i < lcfg.groups.size(); ++i)
				{
					SBean.GambleShopGoodsCFGS sgcfg = getGambleShopRandGoods(lcfg.groups.get(i));
					if (sgcfg != null)
					{
						goods.add(new SBean.DBGambleShopGoods(sgcfg.id, 0));
					}
				}
				while (goods.size() < cfg.refreshCount)
				{
					SBean.GambleShopGoodsCFGS sgcfg = getGambleShopRandGoods(lcfg.groups.get(0));
					if (sgcfg != null)
					{
						goods.add(new SBean.DBGambleShopGoods(sgcfg.id, 0));
					}
				}
				break;
			}
		}
		return goods;
	}

	public static SBean.GambleShopGoodsCFGS getGambleShopGoodsById(SBean.GambleShopCFGS cfg, int id)
	{
		return cfg.allGoods.get(id);
	}
	
	private static SBean.GambleShopGoodsCFGS getGambleShopRandGoods(SBean.GambleShopGoodsGroupCFGS cfg)
	{
		if (cfg.goods.isEmpty())
			return null;
		float r = GameRandom.getRandom().nextFloat();
		for (SBean.GambleShopGoodsCFGS e : cfg.goods)
		{
			if (r < e.pro && GameTime.getTime() > getRefreshTimeByDayTime(e.startdate))
				return e;
		}
		return null;
	}
	
	public int getRoleRankCount()
	{
		return this.roleRanks.size();
	}

	public SBean.RoleRankCFGS getRoleRankCFG(int rankId)
	{
		if (rankId <= 0 || rankId > this.roleRanks.size())
			return null;
		return this.roleRanks.get(rankId - 1);
	}

	public SBean.SectRankCFGS getSectRankCFG(int rankID)
	{
		if(rankID <= 0 || rankID > this.sectRanks.size())
			return null;
		
		return this.sectRanks.get(rankID - 1);
	}
	
	public int getSectRankConut()
	{
		return this.sectRanks.size();
	}
	
	public static int createRoleLevelExpRankKey(int level, long exp)
	{
		return (int) ((level << 4 & 0xff0) | ((exp / (GameData.getInstance().getLevelUpExp(level) >> 4)) & 0x00f));
	}
	
	public static int createRoleLevelRankKey(int time, int level, long exp)
	{
		return ((createRoleLevelExpRankKey(level, exp) << 20) & 0xfff00000) | (~(time / 3600) & 0x000fffff);
	}
	
	public static int getRoleLevelFromRoleLevelRankKey(int rankkey)
	{
		return (rankkey >> 24) & 0xff;
	}
	
	public static int getRoleLevelExpFromRoleLevelRankKey(int rankkey)
	{
		return (rankkey >> 20) & 0xfff;
	}
	
	public static int createSteleCardRankKey(int time, int cards)
	{
		return (cards << 16 & 0xffff0000) | (time & 0xffff);
	}
	
	public static int createEmergencyRankKey(int power, int prestige)
	{
		return (prestige << 10 & 0xfffffc00) | (power / 1000 & 0x3ff);
	}
	
	public static int getSteleCardByRankKey(int rankKey)
	{
		return ( rankKey >> 16) & 0x0000ffff;
	}
	
	//前8:grade, 后24:groupID
	public static int createDemonHoleGroupKey(int grade, int groupID)
	{
		return (grade << 24 & 0xff000000) | (groupID & 0x00ffffff);
	}
	
	public static int createDemonHoleRankKey(int kills, int bekills)
	{
		return (kills << 16 & 0xffff0000) | (~bekills & 0x0000ffff);
	}
	
	public static int getDemonHoleGradeFromKey(int key)
	{
		return (key >> 24) & 0x000000ff;
	}
	
	public static int getDemonHoleGroupIDFromKey(int key)
	{
		return key & 0x00ffffff;
	}
	
	//前8:themeType, 后24:themeID
	public static int createSocialCommentThemeKey(int themeType, int themeID)
	{
		return (themeType << 24 & 0xff000000) | (themeID & 0x00ffffff);
	}
	
	public static int getSocialCommentThemeTypeFromKey(int key)
	{
		return (key >> 24) & 0x000000ff;
	}
	
	public static int getSocialCommentThemeIDFromKey(int key)
	{
		return key & 0x00ffffff;
	}
	
	public static int createEquipRankPartKey(int rank, int partID)
	{
		return (rank << 16 & 0xffff0000) | (partID & 0x0000ffff);
	}
	
	public static SBean.RankTitle getRankTitle(List<SBean.RankTitle> titles, int rank)
	{
		for(SBean.RankTitle e: titles)
		{
			if(rank <= e.floor)
				return e;
		}
		
		return null;
	}
	
	public static int getClassRoleLevelRankID(int classType)
	{
		switch (classType)
		{
		case GameData.CLASS_TYPE_BLADE:
			return GameData.RANK_TYPE_ROLE_LEVEL_BALDE;
		case GameData.CLASS_TYPE_SWORD:
			return GameData.RANK_TYPE_ROLE_LEVEL_SWORD;
		case GameData.CLASS_TYPE_SPEAR:
			return GameData.RANK_TYPE_ROLE_LEVEL_SPEAR;
		case GameData.CLASS_TYPE_ARROW:
			return GameData.RANK_TYPE_ROLE_LEVEL_ARROW;
		case GameData.CLASS_TYPE_HEAL:
			return GameData.RANK_TYPE_ROLE_LEVEL_HEAL;
		default:
			return -1;
		}
	}
	
	public static int getClassRolePowerRankID(int classType)
	{
		switch (classType)
		{
		case GameData.CLASS_TYPE_BLADE:
			return GameData.RANK_TYPE_ROLE_POWER_BALDE;
		case GameData.CLASS_TYPE_SWORD:
			return GameData.RANK_TYPE_ROLE_POWER_SWORD;
		case GameData.CLASS_TYPE_SPEAR:
			return GameData.RANK_TYPE_ROLE_POWER_SPEAR;
		case GameData.CLASS_TYPE_ARROW:
			return GameData.RANK_TYPE_ROLE_POWER_ARROW;
		case GameData.CLASS_TYPE_HEAL:
			return GameData.RANK_TYPE_ROLE_POWER_HEAL;
		default:
			return -1;
		}
	}
	
	public Collection<SBean.DailyTaskCFGS> getAllDailyTasks()
	{
		return this.dailyTasks.values();
	}

	public SBean.DailyTaskCFGS getDailyTask(int id)
	{
		return this.dailyTasks.get(id);
	}

	public SBean.DailyTaskCFGS getDailyActivity(int id)
	{
		return this.dailyActivities.get(id);
	}

	public Collection<Integer> getDailyOnlineGiftRewardPoints()
	{
		return this.dailyOnlineGift.keySet();
	}
	
	public SBean.DailyOnlineGiftCFGS getDailyOnlineGift(int minute)
	{
		return this.dailyOnlineGift.get(minute);
	}
	
	public SBean.OfflineExpCFGS getOfflineExpCFG()
	{
		return this.offlineExp;
	}
	
	public float getOfflineExpDistributeFactor(int type, int param)
	{
		SBean.OfflineExpDistributeCFGS cfg = this.offlineExp.distribute.get(type);
		return cfg == null ? 0.0f : cfg.id2ExpFactor.getOrDefault(param, 0.0f);
	}
	
	public SBean.DailyQuizGiftCFGS getDailyQuizGift()
	{
		return this.dailyQuizGift;
	}
	
	public SBean.QuestionCFGS getActivityQuestion(int id)
	{
		if (id <= 0 || id > this.activityQuestionsBank.size())
			return null;
		return this.activityQuestionsBank.get(id-1);
	}
	
	public int getAddBonusOnQuizAnswerQuickly(int second)
	{
		int addBonus = 0;
		for (SBean.QuicklyAnswerBonusCFGS cfg : this.dailyQuizGift.quickBonus)
		{
			if (second <= cfg.timeCostCeil)
			{
				addBonus = cfg.bonus;
				break;
			}
		}
		return addBonus;
	}
	
	public int getAddBonusOnQuizAnswerContinuous(int count)
	{
		int addBonus = 0;
		for (SBean.ContinuousAnswerBonusCFGS cfg : this.dailyQuizGift.continuousBonus)
		{
			if (count >= cfg.continuousTimesFloor)
			{
				addBonus = cfg.bonus;
			}
			else
			{
				break;
			}
		}
		return addBonus;
	}
	
//	public List<SBean.DummyGoods> getQuizGiftByRank(int rank)
//	{
//		List<SBean.DummyGoods> rewards = null;
//		for (SBean.QuizBonusRewardCFGS cfg : this.dailyQuizGift.bonusReward)
//		{
//			if (rank >= cfg.rankFloor)
//			{
//				rewards = cfg.rewards;
//			}
//			else
//			{
//				break;
//			}
//		}
//		return rewards == null ? GameData.emptyList() : rewards;
//	}
	
	public SBean.QuizBonusRewardCFGS getQuizBonusRewardCFGS(int rank)
	{
		for (SBean.QuizBonusRewardCFGS e : this.dailyQuizGift.bonusReward)
		{
			if (rank <= e.rankFloor)
				return e;
		}
		
		return null;
	}
	
	public List<Integer> getActivityRandomQuiz(int count)
	{
		List<Integer> quizzes = new ArrayList<>();
		for (int i = 0; i < count; ++i)
		{
			while (true)
			{
				int id = GameRandom.getRandom().nextInt(this.activityQuestionsBank.size()) + 1;
				if (!quizzes.contains(id))
				{
					quizzes.add(id);
					break;
				}
			}
		}
		return quizzes;
	}
	
	public SBean.QuestionCFGS getTaskQuestion(int id)
	{
		if (id <= 0 || id > this.taskQuestionsBank.size())
			return null;
		return this.taskQuestionsBank.get(id-1);
	}
	
	// 幸运大转盘
	public SBean.LuckyWheelCFGS getLuckyWheelCFGS()
	{
		return this.luckyWheel;
	}
	private int getLuckyWheelGradeId(int level)
	{
		int gradeId = 0;
		for( int i = 0; i < this.luckyWheel.gradeList.size(); ++i)
		{
			if(level < this.luckyWheel.gradeList.get(i).levelCeil)
				break;
			gradeId = i;
		}
		return gradeId; // index 0,1,2
	}
	public int getOneDiffRandomInteger(List<Integer> list,int level)
	{
		SBean.LuckyWheelGradeCFGS gradeCfg = GameData.getInstance().getLuckyWheelGradeCFGS(level);
		int maxRand = gradeCfg.rewards.size();
		
		int rand = GameRandom.getRandInt(0, maxRand); // [0~6] index
		while(true)
		{
			if (list.contains(rand))
				rand = GameRandom.getRandInt(0, maxRand);
			else
				break;
		}
		return rand;
	}
	public int getLuckyWheelListLastPos(List<Integer> list)
	{
		return list.isEmpty() ? 0 : list.get(list.size() - 1);
	}
	public SBean.LuckyWheelGradeCFGS getLuckyWheelGradeCFGS(int level)
	{
		int gradeId = GameData.getInstance().getLuckyWheelGradeId(level);
		return this.luckyWheel.gradeList.get(gradeId);
	}
	public int getLuckyWheelDrawPoint(int level, List<Integer> list)
	{
		SBean.LuckyWheelGradeCFGS gradeList = getLuckyWheelGradeCFGS(level);
		SBean.LuckyWheelRewardsCFGS t = gradeList.rewards.get(GameData.getInstance().getLuckyWheelListLastPos(list));
		int allWeight = 0;
		for (int i = 0; i < GameData.EQUIP_MAX_PARTNUM; ++i)
		{
			allWeight += t.items.get(i).weight;
		}
		int rand = GameRandom.getRandInt(0, allWeight);
		int tempWeight = 0;
		int result = 0;
		for (int i = 0; i < GameData.EQUIP_MAX_PARTNUM; ++i)
		{
			tempWeight += t.items.get(i).weight;
			result++;
			if (tempWeight >= rand)
			{
				break;
			}
		}
		return result;
	}
	
	public SBean.RedEnvelopeCFGS getRedEnvelopeCFGS()
	{
		return this.redEnvelope;
	}
	
	public static SBean.RedEnvelopeLevelCFGS getPayRedEnvelopeLevelCFGS(List<SBean.RedEnvelopeLevelCFGS> levelcfgs, int pay)
	{
		SBean.RedEnvelopeLevelCFGS triggerCfg = null;
		for (SBean.RedEnvelopeLevelCFGS cfg : levelcfgs)
		{
			if (pay >= cfg.payAmount)
				triggerCfg = cfg;
			else
				break;
		}
		return triggerCfg;
	}
	
	public static SBean.RedEnvelopeLevelCFGS getRandomRedEnvelopeLevelCFGS(List<SBean.RedEnvelopeLevelCFGS> levelcfgs)
	{
		return levelcfgs.get(GameRandom.getRandom().nextInt(levelcfgs.size()));
	}
	
	public SBean.ChallengeTaskGroupCFGS getChallengeTaskGroupCFGS(int type)
	{
		return this.challengeTasksGroup.get(type);
	}
	
	public SBean.ChallengeTaskCFGS getChallengeTaskCFGS(int type, int seq)
	{
		SBean.ChallengeTaskGroupCFGS group = this.challengeTasksGroup.get(type);
		if (group == null)
			return null;

		if (seq <= 0 || seq > group.tasks.size())
			return null;

		return group.tasks.get(seq - 1);
	}
	
	public Collection<SBean.ChallengeTaskGroupCFGS> getAllChallengeTasks()
	{
		return this.challengeTasksGroup.values();
	}
	
	public int getFameOpenLevel()
	{
		return this.fames.openLevel;
	}
	
	public SBean.FameLevelCFGS getFameLevelCFGS(int level)
	{
		if (level <= 0 || level > this.fames.fames.size())
			return null;
		return this.fames.fames.get(level-1);
	}
	
	public int getAuctionEquipGrade(int level)
	{
		for(int i = 0; i < this.auction.lvls.size(); i++)
		{
			if(level <= this.auction.lvls.get(i))
				return i + 1;
		}
		
		return this.auction.lvls.size();
	}
	
	public Collection<Integer> getAllAuctionTypes()
	{
		return this.auction.types;
	}
	
	public int getBagItemAuctionType(int id)
	{
		int type = 0;
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
		case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			break;
		case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				SBean.ItemCFGS item = this.getItemCFG(id);
				if(item != null)
					type = item.auctiontype;
			}
			break;
		case GameData.COMMON_ITEM_ID_GEM_PLANE:
			{
				SBean.GemCFGS gem = this.getGemCFG(id);
				if(gem != null)
					type = AUCTION_ITEM_TYPE_GROW;
			}
			break;
		case GameData.COMMON_ITEM_ID_BOOK_PLANE:
			{
				SBean.BookCFGS book = this.getBookCFG(id);
				if(book != null)
					type = AUCTION_ITEM_TYPE_SPIRIT;
			}
			break;
		default:
			{
				SBean.EquipCFGS equip = this.getEquipCFG(id);
				if(equip != null)
					type = equip.type;
			}
			break;
		}
		return type;
	}
	
	public boolean checkBagItemCanTrade(int id, int price)
	{
		boolean canTrade = false;
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
		case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			break;
		case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				SBean.ItemCFGS item = this.getItemCFG(id);
				canTrade = item != null && item.canTrade == 1 && checkAuctionItemPrice(item.auctionPrice, price);
			}
			break;
		case GameData.COMMON_ITEM_ID_GEM_PLANE:
			{
				SBean.GemCFGS gem = this.getGemCFG(id);
				canTrade = gem != null && gem.canTrade == 1 && checkAuctionItemPrice(gem.auctionPrice, price);
			}
			break;
		case GameData.COMMON_ITEM_ID_BOOK_PLANE:
			{
				SBean.BookCFGS book = this.getBookCFG(id);
				canTrade = book != null && book.canTrade == 1 && checkAuctionItemPrice(book.auctionPrice, price);
			}
			break;
		default:
			{
				SBean.EquipCFGS equip = this.getEquipCFG(id);
				canTrade = equip != null && equip.canTrade == 1;
			}
			break;
		}
		return canTrade;
	}
	
	boolean checkAuctionItemPrice(int auctionPrice, int price)
	{
		int min = (int) (auctionPrice * this.common.auction.minPercent);
		int max = (int) (auctionPrice * this.common.auction.maxPercent);
		return price >= min && price <= max;
	}
	
	public boolean checkEntityIdValid(int id)
	{
		boolean valid = false;
		switch (id)
		{
		case GameData.COMMON_ITEM_ID_DIAMOND:
		case -GameData.COMMON_ITEM_ID_DIAMOND:
		case GameData.COMMON_ITEM_ID_COIN:
		case -GameData.COMMON_ITEM_ID_COIN:
		case GameData.COMMON_ITEM_ID_SECT_MONEY:
		case GameData.COMMON_ITEM_ID_ARENA_MONEY:
		case GameData.COMMON_ITEM_ID_SUPERARENA_MONEY:
		case GameData.COMMON_ITEM_ID_MASTER_POINT:
		case GameData.COMMON_ITEM_ID_MASTER_REPUTATION:
		case GameData.COMMON_ITEM_ID_EQUIP_ENERGY:
		case GameData.COMMON_ITEM_ID_GEM_ENERGY:
		case GameData.COMMON_ITEM_ID_BOOK_INSPIRATION:
		case GameData.COMMON_ITEM_ID_VIT:
		case GameData.COMMON_ITEM_ID_SPLITSP:
		case GameData.COMMON_ITEM_ID_XUANTIE:
		case GameData.COMMON_ITEM_ID_YAOCAO:
		case GameData.COMMON_ITEM_ID_EXPCOIN:
		case GameData.COMMON_ITEM_ID_ROB_MONEY:
		case GameData.COMMON_ITEM_ID_CREDIT:
		case GameData.COMMON_ITEM_ID_OFFLINE_FUNC_POINT:
			valid = true;
			break;
		default:
			valid = checkBagItemIdValid(id);
			break;
		}
		return valid;
	}
	
	public boolean checkBagItemIdValid(int id)
	{
		boolean valid = false;
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
		case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			break;
		case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				SBean.ItemCFGS item = this.getItemCFG(id);
				valid = item != null;
			}
			break;
		case GameData.COMMON_ITEM_ID_GEM_PLANE:
			{
				SBean.GemCFGS gem = this.getGemCFG(id);
				valid = gem != null;
			}
			break;
		case GameData.COMMON_ITEM_ID_BOOK_PLANE:
			{
				SBean.BookCFGS book = this.getBookCFG(id);
				valid = book != null;
			}
			break;
		default:
			{
				SBean.EquipCFGS equip = this.getEquipCFG(id);
				valid = equip != null;
			}
			break;
		}
		return valid;
	}

	public int getItemClassType(int id)
	{
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
			case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			{
				return 0;
			}
			case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				return 0;
			}
			case GameData.COMMON_ITEM_ID_GEM_PLANE:
			{
				return 0;
			}
			case GameData.COMMON_ITEM_ID_BOOK_PLANE:
			{
				SBean.BookCFGS book = this.getBookCFG(id);
				return book == null ? 0 : book.classType;
			}
			default:
			{
				SBean.EquipCFGS equip = this.getEquipCFG(id);
				return equip == null ? 0 : equip.classType;
			}
		}
	}
	
	public int getItemRank(int id)
	{
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
			case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			{
				SBean.BaseDummyItemCFGS base = this.getBaseCFG(id);
				return base == null ? 0 : base.rank;
			}
			case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				SBean.ItemCFGS item = this.getItemCFG(id);
				return item == null ? 0 : item.rank;
			}
			case GameData.COMMON_ITEM_ID_GEM_PLANE:
			{
				SBean.GemCFGS gem = this.getGemCFG(id);
				return gem == null ? 0 : gem.rank;
			}
			case GameData.COMMON_ITEM_ID_BOOK_PLANE:
			{
				SBean.BookCFGS book = this.getBookCFG(id);
				return book == null ? 0 : book.rank;
			}
			default:
			{
				SBean.EquipCFGS equip = this.getEquipCFG(id);
				return equip == null ? 0 : equip.rank;
			}
		}
	}

	public int getEquipFghtPower(SBean.DBEquip dbEquip)
	{
		SBean.EquipCFGS equip = this.getEquipCFG(dbEquip.id);
		if(equip == null)
			return 0;
		
		return FightProperties.calcDBEquipFightPower(dbEquip);
	}
	
	public String getItemName(int id)
	{
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
			case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			{
				SBean.BaseDummyItemCFGS base = this.getBaseCFG(id);
				return base == null ? null : base.name;
			}
			case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				SBean.ItemCFGS item = this.getItemCFG(id);
				return item == null ? null : item.name;
			}
			case GameData.COMMON_ITEM_ID_GEM_PLANE:
			{
				SBean.GemCFGS gem = this.getGemCFG(id);
				return gem == null ? null : gem.name;
			}
			case GameData.COMMON_ITEM_ID_BOOK_PLANE:
			{
				SBean.BookCFGS book = this.getBookCFG(id);
				return book == null ? null : book.name;
			}
			default:
			{
				SBean.EquipCFGS equip = this.getEquipCFG(id);
				return equip == null ? null : equip.name;
			}
		}
	}

	public SBean.GiftCFGS getGiftCFG(int id)
	{
		return gifts.get(id);
	}
	
	public static class CheckInInfo
	{
		public int daySeq;
		public SBean.CheckInCFGS cfg;
		public CheckInInfo(int daySeq, SBean.CheckInCFGS cfg)
		{
			this.daySeq = daySeq;
			this.cfg = cfg;
		}
	}
	
	public CheckInInfo getCheckInRewardCFG(int day)
	{
		for (SBean.CheckInCFGS e : this.checkins)
		{
			int startDay = GameTime.getDay(e.startTime);
			int endDay = startDay + e.rewards.size(); 
			if (day >= startDay && day < endDay)
			{
				int daySeq = 1 + (day-startDay);
				return new CheckInInfo(daySeq, e);
			}
		}
		return null;
	}

	public SBean.ClassRoleCFGS getClassRoleCFG(int id)
	{
		if (id <= 0 || id > classRoles.size())
			return null;
		return classRoles.get(id - 1);
	}
	
	public int getClassRoleCount()
	{
		return classRoles.size();
	}

	public SBean.ClassRoleFashionCFGS getClassRoleFashionCFGS(int id)
	{
		return this.classRoleFashions.get(id);
	}
	
	public Set<Integer> getCollectSuites(Map<Integer, SBean.DBSuite> dbSuites)
	{
		Set<Integer> collectSuites = new HashSet<>();
		for(SBean.DBSuite dbSuite: dbSuites.values())
		{
			SBean.SuiteCFGS cfg = this.suites.get(dbSuite.id);
			if(cfg == null)
				continue;
			
			if(cfg.parts.size() == dbSuite.collect.size())
				collectSuites.add(dbSuite.id);
		}
		return collectSuites;
	}
	
	public int getClassRoleInitHp(int id, Map<Integer, SBean.DBWearEquip> wearEquips)
	{
		double hpBase = 0;
		double hpPercent = 0;
		SBean.ClassRoleCFGS cfg = getClassRoleCFG(id);
		if (cfg != null)
		{
			hpBase = cfg.hp.org;
			SBean.CommonCFGS commonCfg = GameData.getInstance().getCommonCFG();
			for (SBean.DBWearEquip wearEquip : wearEquips.values())
			{
				if (wearEquip == null || wearEquip.equip.durability >= 0 && wearEquip.equip.durability <= commonCfg.equip.disableValue)
					continue;

				SBean.EquipCFGS equipCfgs = GameData.getInstance().getEquipCFG(wearEquip.equip.id);
				// 基础属性值
				double baseAdd = wearEquip.equip.durability < 0 ? 0 : GameData.getInstance().getLegenOneBaseAdd(wearEquip.equip.legends.get(0));
				for (SBean.EquipBasePropCFGS baseArg : equipCfgs.baseProp)
				{
					if (baseArg.advEffect == 1 && baseArg.type == BaseRole.EPROPID_MAXHP)
						hpBase += baseArg.value * (1 + baseAdd);
				}

				// 附加属性值
				double addtionAdd = wearEquip.equip.durability < 0 ? 0 : GameData.getInstance().getLegendTwoAddtionAdd(wearEquip.equip.legends.get(1));
				for (int i = 0; i < wearEquip.equip.addValues.size(); i++)
				{
					SBean.EquipAdditPropCFGS additProp = equipCfgs.additProp.get(i);
					if (additProp.type == GameData.EQUIP_ADDPROP_TYPE1 && additProp.arg == BaseRole.EPROPID_MAXHP)
						hpBase += wearEquip.equip.addValues.get(i) * (1 + addtionAdd);
				}
				
				//3号位
				int legendThree = wearEquip.equip.legends.get(2);
				if(legendThree > 0)
				{
					SBean.LegendThreeCFGS ltCfg = GameData.getInstance().getLegengThreeCFGS(equipCfgs.type, legendThree);
					if(ltCfg != null && ltCfg.type == GameData.LEGEND_EQUIP_THREE_TYPE_ADDPROP)
					{
						int propID = ltCfg.params.get(0);
						if(propID == BaseRole.EPROPID_MAXHP)
						{
							int valueTyp = ltCfg.params.get(1);
							int value = ltCfg.params.get(2);
							
							if(propID == BaseRole.EPROPID_MAXHP)
							{
								if(valueTyp == GameData.VALUE_TYPE_FIXED)
									hpBase += value;
								else
									hpPercent += value;
							}
						}
					}
				}
			}
		}

		return (int) (hpBase * (1 + hpPercent / 10_000));
	}

	public SBean.MapLocation getClassRoleSpawnMapPosition(int id)
	{
		SBean.ClassRoleCFGS cfg = getClassRoleCFG(id);
		return cfg == null ? null : new SBean.MapLocation(cfg.spawnPosition.mapID, new SBean.Location(cfg.spawnPosition.position.kdClone(), new SBean.Vector3F(1.0f, 0.0f, 0.0f)));// 初始方向朝向x轴正方向
		
	}
	
//	static class TestPerformanceSpawnPositions
//	{
//		List<SBean.MapLocation> performanceSpawns = new ArrayList<SBean.MapLocation>();
//		AtomicInteger seq = new AtomicInteger(0);
//		public TestPerformanceSpawnPositions()
//		{
//			spawns.add(new SBean.MapLocation(1, new SBean.Location(new SBean.Vector3(-2934, 2343, -522), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(3530, 435, 6265), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(4881, 435, 3856), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(5099, 435, -720), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(4796, 435, -7403), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(116, 435, -9664), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-4499, 435, -8971), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-9280, 435, -9249), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-13061, 435, -9150), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-7086, 662, 1584), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-14725, 435, 2021), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-16379, 459, 7483), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-11902, 425, 5502), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-6699, 425, 6594), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//			spawns.add(new SBean.MapLocation(4, new SBean.Location(new SBean.Vector3(-6805, 433, -3443), new SBean.Vector3F(1.0f, 0.0f, 0.0f))));
//		}
		
//		public SBean.MapLocation getNextTestSpawnMapPositon()
//		{
//			return performanceSpawns.get(seq.incrementAndGet()%getInstance().performanceSpawns.size());
//		}
//	}
	
	public SBean.MapLocation getClassRoleTestSpawnMapPosition()
	{
		return performanceSpawns.get(GameRandom.getRandom().nextInt(performanceSpawns.size()));
	}
	
	public float getPathAngel(float range)
	{
		if(range <= this.common.skill.attackEdge)
			return this.common.skill.closeAngle/4.f;
		
		return this.common.skill.farAngle/4.f;
	}
	
	public boolean checkSkillRush(int skillID)
	{
		SBean.SkillCFGS cfg = getSkillCFG(skillID);
		if(cfg == null)
			return true;
		
		if(!cfg.baseData.common.specialIDs.isEmpty())
		{
			for(int specialID: cfg.baseData.common.specialIDs)
			{
				SBean.SkillSpecialCFGS ssCfg = getSkillSpecialCFG(specialID);
				if(ssCfg == null || ssCfg.formulaID == GameData.SKILL_SPECIAL_RUSH)
					return true;
			}
		}
		return false;
	}
	
	public SBean.SkillCFGS getSkillCFG(int id)
	{
		return skills.get(id);
	}

	public SBean.SkillSpecialCFGS getSkillSpecialCFG(int id)
	{
		return skillSpecial.get(id);
	}

	public SBean.BuffCFGS getBuffCFG(int id)
	{
		return buffs.get(id);
	}

	public static SBean.SkillLevelCFGS getSkillLevelCFG(SBean.SkillCFGS cfg, int level)
	{
		if (level < 1 || level > cfg.lvlDatas.size())
			return null;
		return cfg.lvlDatas.get(level - 1);
	}

	public SBean.MonsterCFGS getMonsterCFGS(int id)
	{
		return monsters.get(id);
	}

	public float getMonsterExpPercent(int dLvl)
	{
		for(SBean.MonsterExp e: this.common.monsterExp.dLvls)
		{
			if(dLvl <= e.floor)
				return e.percent;
		}
		
		return this.common.monsterExp.dLvls.get(this.common.monsterExp.dLvls.size() - 1).percent;
	}
	
	public SBean.NpcCFGS getNpcCFGS(int id)
	{
		return npcs.get(id);
	}

	public SBean.MineralCFGS getMineralCFGS(int id)
	{
		return minerals.get(id);
	}
	
	public SBean.MapBuffCFGS getMapBuffCFGS(int id)
	{
		return this.mapBuffs.get(id);
	}
	
	public static class MapException extends Exception
	{
		public MapException(String message)
		{
			super(message);
		}
		
		public MapException(String message, Throwable t)
		{
			super(message, t);
		}
	}
	public int checkMapValid(int mapId) throws GameData.MapException
	{
		SBean.MapClusterCFGS cfg = GameData.getInstance().getMapClusterCFGS(mapId);
		if (cfg == null)
			throw new MapException("map cluster config not found!");
		switch (cfg.type)
		{
		case GameData.MAP_TYPE_MAP_WORLD:
			if (GameData.getInstance().getWorldMapCFGS(mapId) == null)
				throw new MapException("world map " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_NORMAL:
			if (GameData.getInstance().getMapCopyCFGS(mapId) == null)
				throw new MapException("normal map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_SECT:
			if (GameData.getInstance().getSectMapCFGS(mapId) == null)
				throw new MapException("sect map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_ARENA:
			if (GameData.getInstance().getArenaMapCFGS(mapId) == null)
				throw new MapException("arena map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_CLAN_ORE:
//			if (GameData.getInstance().getClanOreMapCFGS(mapId) == null)
//				throw new MapException("normal clan ore map copy config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_CLAN_TASK:
//			if (GameData.getInstance().getClanTaskMapCFGS(mapId) == null)
//				throw new MapException("normal clan task map copy config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLE:
//			if (GameData.getInstance().getClanBattleMapCFGS(mapId) == null)
//				throw new MapException("normal clan battle map copy config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLEHELP:
//			if (GameData.getInstance().getClanBattleHelpMapCFGS(mapId) == null)
//				throw new MapException("normal clan battle help map copy config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_ACTIVITY:
			if (GameData.getInstance().getActivityMapCFGS(mapId) == null)
				throw new MapException("activity map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_SUPERARENA:
			SBean.SuperArenaMapCFGS mapCfg = GameData.getInstance().getSuperArenaMapCFGS(mapId);
			if (mapCfg == null)
				throw new MapException("super arena map copy " + mapId + " config not found !");
			else if(GameData.getInstance().getSuperArenaTypeCFG(mapCfg.type) == null)
				throw new MapException("super arena map copy " + mapId + " type not found !");
				
			break;
		case GameData.MAP_TYPE_MAPCOPY_BWARENA:
			if (GameData.getInstance().getBWArenaMapCFGS(mapId) == null)
				throw new MapException("bw arena map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_PETLIFE:
			if(GameData.getInstance().getPetLifeMapCFGS(mapId) == null)
				throw new MapException("pet life map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_CLIMBTOWER:
			if(GameData.getInstance().getClimbTowerMapCFGS(mapId) == null)
				throw new MapException("climb tower map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
			if(GameData.getInstance().getForceWarMapCFGS(mapId) == null)
				throw new MapException("force war map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_SECT_GROUP:
			if (GameData.getInstance().getSectGroupMapCFGS(mapId) == null)
				throw new MapException("sect group map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_WEAPON:
			if(GameData.getInstance().getWeaponMapCFGS(mapId) == null)
				throw new MapException("weapon map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE:
			if(GameData.getInstance().getDemonHoleMapCFGS(mapId) == null)
				throw new MapException("demon hole map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_JUSTICE:
			if(GameData.getInstance().getJusticeMapCopyCFGS(mapId) == null)
				throw new MapException("justice map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_EMERGENCY:
			break;
		case GameData.MAP_TYPE_MAPCOPY_FIGHTNPC:
			if(getFightNpcMapCFGS(mapId) == null)
				throw new MapException("fight npc map copy " + mapId + " config not found !");
			break;
		case GameData.MAP_TYPE_MAPCOPY_TOWER_DEFENCE:
			if(getTowerDefenceMapCFGS(mapId) == null)
				throw new MapException("tower defence map copy " + mapId + " config not found !");
			break;
		default:
			throw new MapException("not support map type " + cfg.type);
		}
		return cfg.type;
	}

	public SBean.MapClusterCFGS getMapClusterCFGS(int id)
	{
		return this.mapClusters.get(id);
	}

	public Collection<SBean.WorldMapCFGS> getAllWorldMapCFGS()
	{
		return this.worldMaps.values();
	}
	
	public SBean.WorldMapCFGS getWorldMapCFGS(int id)
	{
		return this.worldMaps.get(id);
	}

	public SBean.MapCopyCFGS getMapCopyCFGS(int id)
	{
		return this.mapcopys.get(id);
	}
	
	public SBean.Vector3 getMapCopyIndexPos(int index, List<SBean.Vector3> points)
	{
		if(index <= 0 || index > points.size())
			return null;
		
		return points.get(index - 1);
	}
	
	public SBean.ArenaMapCFGS getArenaMapCFGS(int id)
	{
		return this.arenaMaps.get(id);
	}
	
	public SBean.Vector3 getArenaPetPosBySeq(int mapID, int seq, boolean self)
	{
		final SBean.ArenaMapCFGS amc = getArenaMapCFGS(mapID);
		if(amc == null)
			return null;
		
		if(seq <= 0 || (self ? seq > amc.selfPetPos.size() : seq > amc.enemyPetPos.size()))
			return null;
		
		return self ? amc.selfPetPos.get(seq - 1) : amc.enemyPetPos.get(seq - 1);
	}
	
	public SBean.SuperArenaMapCFGS getSuperArenaMapCFGS(int id)
	{
		return this.superArenaMaps.get(id);
	}
	
	public SBean.BWArenaMapCFGS getBWArenaMapCFGS(int id)
	{
		return this.bwarenaMaps.get(id);
	}
	
	public SBean.Vector3 getBWArenaPetPosBySeq(int mapID, int seq, boolean self)
	{
		final SBean.BWArenaMapCFGS bamc = getBWArenaMapCFGS(mapID);
		if(bamc == null)
			return null;
		
		if(seq <= 0 || (self ? seq > bamc.selfPetPos.size() : seq > bamc.enemyPetPos.size()))
			return null;
		
		return self ? bamc.selfPetPos.get(seq - 1) : bamc.enemyPetPos.get(seq - 1);
	}
	
//	public SBean.ClanOreMapCFGS getClanOreMapCFGS(int id)
//	{
//		return this.clanOreMaps.get(id);
//	}
//	
//	public SBean.ClanTaskMapCFGS getClanTaskMapCFGS(int id)
//	{
//		return this.clanTaskMaps.get(id);
//	}
//	
//	public SBean.ClanBattleMapCFGS getClanBattleMapCFGS(int id)
//	{
//		return this.clanBattleMaps.get(id);
//	}
//	
//	public SBean.ClanBattleHelpMapCFGS getClanBattleHelpMapCFGS(int id)
//	{
//		return this.clanBattleHelpMaps.get(id);
//	}
	
	public SBean.SectMapCFGS getSectMapCFGS(int id)
	{
		return this.sectMapcopys.get(id);
	}
	
	public int getSectMapLevel(int id)
	{
		SBean.SectMapCFGS cfgs = this.sectMapcopys.get(id);
		if (cfgs == null)
		{
			return 0;
		}
		int level = 1;
		while (cfgs.preMapId != -1)
		{
			level ++;
			cfgs = this.sectMapcopys.get(cfgs.preMapId);
		}
		return level;
	}

	public SBean.ActivityMapCFGS getActivityMapCFGS(int id)
	{
		return this.activityMaps.get(id);
	}

	public SBean.ActivityLastCFGS getActivityLastCFGS(int id)
	{
		return id <= 0 || id > this.activityLast.size() ? null : this.activityLast.get(id - 1);
	}
	
	public SBean.ActivityMapGroupCFGS getActivityMapGroupCFGS(int id)
	{
		return this.actMapGroups.get(id);
	}
	
	public SBean.PetLifeMapCFGS getPetLifeMapCFGS(int mapID)
	{
		return this.petLifeMaps.get(mapID);
	}
	
	public SBean.ClimbTowerMapCFGS getClimbTowerMapCFGS(int mapID)
	{
		return this.climbTowerMaps.get(mapID);
	}
	
	public SBean.ForceWarMapCFGS getForceWarMapCFGS(int mapID)
	{
		return this.forcewarMaps.get(mapID);
	}
	
	public SBean.WeaponMapCFGS getWeaponMapCFGS(int mapID)
	{
		return this.weaponMaps.get(mapID);
	}
	
	public int getWeaponMapByLevel(int level)
	{
		for(SBean.WeaponMapGradeCFGS g: this.weaponMapGrades)
		{
			if(level <= g.floor)
				return g.mapID;
		}
		
		return this.weaponMapGrades.get(this.weaponMapGrades.size() - 1).mapID;
	}
	
	public int calcNormalMapCopyScore(boolean privateMap, int finishTime, int maxTime, int deadTimes)
	{
		double arg1 = 0.0;
		double arg2 = 0.0;
		int min1 = this.common.map.mapcopyFinishArgs.get(2);
		int max1 = this.common.map.mapcopyFinishArgs.get(3);
		int min2 = this.common.map.mapcopyFinishArgs.get(0);
		int max2 = this.common.map.mapcopyFinishArgs.get(1);
		int base = this.common.map.mapcopyFinishArgs.get(4);

		if(privateMap)
		{
			arg1 = this.common.map.primapFinishArgs.get(0) * (float)(maxTime-finishTime)/maxTime;
			arg2 = this.common.map.primapFinishArgs.get(1) - Math.pow(base, deadTimes);
		}
		else
		{
			arg1 = this.common.map.teammapFinishArgs.get(0) * (float)(maxTime-finishTime)/maxTime;
			arg2 = this.common.map.teammapFinishArgs.get(1) - Math.pow(base, deadTimes-1);
		}
		arg1 = arg1 > max1 ? max1 : (arg1 < min1 ? min1 : arg1);
		arg2 = arg2 > max2 ? max2 : (arg2 < min2 ? min2 : arg2);
		double arg = arg1 + arg2;
		int index = 0;
		for (int scoreReq : this.common.map.scores)
		{
			if (arg > scoreReq)
				break;
			++index;
		}
		return this.common.map.scores.size() + 1 - index;
	}
	
	public int getMapType(int id)
	{
		SBean.MapClusterCFGS cfg = mapClusters.get(id);
		if (cfg == null)
			return -1;
		return cfg.type;
	}

	public int getMapCopyOpenType(int id)
	{
		SBean.MapCopyCFGS cfg = mapcopys.get(id);
		if (cfg == null)
			return -1;
		return cfg.openType;
	}

	public int getMapCopyDifficulty(int mapID)
	{
		SBean.MapCopyCFGS cfg = mapcopys.get(mapID);
		if (cfg == null)
			return -1;
		
		return cfg.difficulty;
	}
	

//	public int getChTaskTypeOneMap(int mapID)
//	{
//		int difficulty = getMapCopyDifficulty(mapID);
//		switch (difficulty)
//		{
//		case MAPCOPY_DIFFICULT_TEAM:
//			return CHALLENGE_TASK_ID_ONE_TEAM_MAPCOPY;
//		case MAPCOPY_DIFFICULT_STORY:
//			return CHALLENGE_TASK_ID_ONE_STORY_MAPCOPY;
//		case MAPCOPY_DIFFICULT_EASY:
//			return CHALLENGE_TASK_ID_ONE_EASY_MAPCOPY;
//		case MAPCOPY_DIFFICULT_HARD:
//			return CHALLENGE_TASK_ID_ONE_HARD_MAPCOPY;
//		default:
//			break;
//		}
//
//		return -1;
//	}
//
//	public int getChTaskTypeAnyMap(int mapID)
//	{
//		int openType = getMapCopyOpenType(mapID);
//		switch (openType)
//		{
//		case MAPCOPY_OPEN_TYPE_PRIVATE:
//			return CHALLENGE_TASK_ID_ANY_PRIVATE_MAPCOPY;
//		case MAPCOPY_OPEN_TYPE_PUBLIC:
//			return CHALLENGE_TASK_ID_ANY_PUBLIC_MAPCOPY;
//		default:
//			break;
//		}
//
//		return -1;
//	}
	
	public static boolean isMapCopyInOpenTime(int startTime, int endTime)
	{
		int curSecondeOfDay = GameTime.getSecondOfDay();
		return (curSecondeOfDay >= startTime && curSecondeOfDay <= endTime);
	}
	
	public static boolean checkInOpenTime(int startTime, int lastTime)
	{
		int now = GameTime.getTime();
		int curDay = getDayByRefreshTimeOffset(now);
		startTime = GameTime.getDayTime(curDay, startTime); 
		
		return (now >= startTime && now <= (startTime + lastTime));
	}
	
	public static boolean isTowerDefenceInOpneTime(SBean.TowerDefenceBaseCFGS baseCfg)
	{
		int now = GameTime.getTime();
		if(now < baseCfg.startDate || now > baseCfg.endDate)
			return false;
		
		int weekDay = GameTime.getWeekdayByOffset(now, GameData.GAME_DAY_REFRESH_TIME * 3600);
		if(!baseCfg.openDays.contains(weekDay))
			return false;
		
		return checkInOpenTime(baseCfg.startTime, baseCfg.lastTime);
	}
	
	public static boolean isSuperArenaInOpenTime(SBean.SuperArenaTypeCFGS cfg)
	{		
		int weekDay = GameTime.getWeekdayByOffset(GameTime.getTime(), GameData.GAME_DAY_REFRESH_TIME * 3600);
		if(!cfg.openDays.contains(weekDay))
			return false;
		
		for(int startTime: cfg.startTimes)
		{
			if(checkInOpenTime(startTime, cfg.lastTime))
				return true;
		}
		
		return false;
	}
	
	public Collection<Integer> getAllSuperArenaTypes()
	{
		return this.superarenaTypes.keySet();
	}
	
	public SBean.SuperArenaTypeCFGS getSuperArenaTypeCFG(int type)
	{
		return this.superarenaTypes.get(type);
	}
	
	public int getSuperArenaNeedMember(int type)
	{
		SBean.SuperArenaTypeCFGS satc = this.superarenaTypes.get(type);
		if(satc == null)
			return 0;
		
		return satc.members;
	}
	
	public static double getSuperArenaELOWD(int diff)
	{
		return Math.pow((1 + Math.pow(10, (-diff / 400.0))), -1);
	}
	
	public int getSuperArenaAddELO(int oldELO, int diff, double w)
	{
		double wd = getSuperArenaELOWD(diff);
		int newELO = (int) (oldELO + 100 * (w - wd));
		
		if(newELO < this.superarena.minELO)
			newELO = this.superarena.minELO;
		
		if(newELO > this.superarena.maxELO)
			newELO = this.superarena.maxELO;
		
		return newELO - oldELO;
	}
	
	public boolean checkTrapInMapCopy(int mapID, int trapID)
	{
		SBean.MapClusterCFGS mapClusterCfg = getMapClusterCFGS(mapID);
		return mapClusterCfg != null && mapClusterCfg.traps.contains(trapID);
	}

	public static SBean.MapLocation getMapSpawnLocation(SBean.MapClusterCFGS cfg)
	{
		SBean.Vector3 position = cfg.spawnPos.kdClone();
		SBean.Vector3F rotation =  cfg.spawnRotation.kdClone();
		return new SBean.MapLocation(cfg.id, new SBean.Location(position, rotation));
	}

	public static SBean.MapLocation getMapSpawnLocation(SBean.MapClusterCFGS cfg, boolean mainSpawnPos, float angle)
	{
		SBean.Vector3 position = mainSpawnPos ? cfg.spawnPos.kdClone() : cfg.spawnPos2nd.kdClone();
		position.x += 300 * Math.cos(angle);
		position.z += 300 * Math.sin(angle); 
		SBean.Vector3F rotation = new SBean.Vector3F(1.0f, 0, 0);
		return new SBean.MapLocation(cfg.id, new SBean.Location(position, rotation));
	}
	
	public SBean.MapLocation getDemonHoleSpawnLocation(int mapID)
	{
		SBean.DemonHoleMapCFGS mapCfg = demonHoleMaps.get(mapID);
		if(mapCfg == null || mapCfg.spawns.isEmpty())
			return null;
		
		SBean.Vector3 rand = mapCfg.spawns.get(GameRandom.getRandInt(0, mapCfg.spawns.size()));
		return new SBean.MapLocation(mapID, new SBean.Location(new SBean.Vector3(rand.x, rand.y, rand.z), new SBean.Vector3F(1, 0, 0)));
	}
	
	public static boolean testSectMapContainsReward(SBean.SectMapCFGS cfg, int rewardId)
	{
		for (SBean.ProgressRewards e : cfg.progressRewards.values())
		{
			if (e.rewards.containsKey(rewardId))
				return true;
		}
		return false;
	}

	public SBean.Location getSafeRevivePosition(SBean.WorldMapCFGS world)
	{
		SBean.MapClusterCFGS mapClusterCfg = this.getMapClusterCFGS(world.safeReviveMap);
		if(mapClusterCfg == null)
			return null;
		
		boolean mainRevive = GameRandom.getRandInt(0, 2) > 0;
		SBean.Vector3F rotation = new SBean.Vector3F(1.0f, 0, 0);
		return new SBean.Location(mainRevive ? mapClusterCfg.revivePos : mapClusterCfg.revivePos2nd, rotation);
	}
	
	public SBean.Location getMapRevivePosition(int mapID, boolean mainRevive)
	{
		SBean.MapClusterCFGS mapClusterCfg = this.getMapClusterCFGS(mapID);
		if (mapClusterCfg == null)
			return null;

		SBean.Vector3 position;
		if(mapClusterCfg.type == MAP_TYPE_MAPCOPY_DEMON_HOLE)
			position = getDemonHoleMapRevivePosition(mapID);
		else
			position = mainRevive ? mapClusterCfg.revivePos : mapClusterCfg.revivePos2nd;
		
		SBean.Vector3F rotation = new SBean.Vector3F(1.0f, 0, 0);
		return new SBean.Location(position.kdClone(), rotation);
	}
	
	public SBean.Vector3 getDemonHoleMapRevivePosition(int mapID)
	{
		SBean.DemonHoleMapCFGS mapCfg = demonHoleMaps.get(mapID);
		if(mapCfg == null || mapCfg.revives.isEmpty())
			return null;
		
		return mapCfg.revives.get(GameRandom.getRandInt(0, mapCfg.revives.size()));
	}
	
	public static int getPointMonsterCount(SBean.SpawnPointCFGS pointCfg)
	{
		if (pointCfg == null)
			return 0;
		int count = 0;
		switch (pointCfg.spawnType)
		{
		case GameData.BYORDER:
			for (int num : pointCfg.spawnNum)
			{
				count = count + num;
			}
			break;
		case GameData.BYTIME:
			count = pointCfg.spawnNum.get(0) * pointCfg.spawnTimes;
			break;
		case GameData.BYNUMBER:
			count = pointCfg.spawnNum.get(0) * pointCfg.spawnTimes;
			break;
		default:
			break;
		}

		return count;
	}

	public SBean.SpawnAreaCFGS getSpawnArea(int id)
	{
		return spawnAreas.get(id);
	}

	public SBean.SpawnPointCFGS getSpawnPoint(int id)
	{
		return spawnPoints.get(id);
	}

	public SBean.NpcPointCFGS getNpcPointCfg(int id)
	{
		return this.npcPoints.get(id);
	}

//	public SBean.MapPosition getNpcMapPosition(int id)
//	{
//		NpcCFGS npcCfg = this.getNpcCFGS(id);
//		if (npcCfg == null)
//			return null;
//		return npcCfg.mapposition;
//	}

	public SBean.MineralPointCFGS getMineralPointCFGS(int id)
	{
		return this.mineralPoints.get(id);
	}

	public SBean.WayPointCFGS getWayPointCFGS(int id)
	{
		return this.wayPoints.get(id);
	}

	public SBean.MapBuffPointCFGS getBuffPointCFGS(int id)
	{
		return this.mapBuffPoints.get(id);
	}

	public boolean testNearbyPosition(int roleMapId, SBean.Vector3 rolePos, int mapID, int x, int y, int z, int radius)
	{
		if (roleMapId != mapID)
			return false;
		GVector3 posSelf = new GVector3(rolePos);
		GVector3 posTarget = new GVector3(x, y, z);
		return (posSelf.distance(posTarget) <= radius);
	}
	
	public static boolean testNearByPosition(SBean.Vector3 pos1, SBean.Vector3 pos2, int radius)
	{
		return new GVector3(pos1).distance(new GVector3(pos2)) <= radius;
	}
	
	public boolean testNearbyNPC(final SBean.MapLocation roleLocation, int npcID, int radius)
	{
		return testNearbyNPC(roleLocation.mapID, roleLocation.location.position, npcID, radius);
	}
	
	public boolean testNearbyNPC(int roleMapId, SBean.Vector3 rolePos, int npcID, int radius)
	{
		if (npcID == 0)
			return true;
		
		SBean.Vector3 npcPos = getNPCPosition(roleMapId, npcID);
		if (npcPos == null)
			return false;
		GVector3 posSelf = new GVector3(rolePos);
		GVector3 posNPC = new GVector3(npcPos);
		return posSelf.distance(posNPC) <= (radius + CHECK_NPC_POS_ERROR);
	}
	
	public SBean.Vector3 getNPCPosition(int mapId, int npcId)
	{
		SBean.MapClusterCFGS mCfg = getMapClusterCFGS(mapId);
		if (mCfg == null)
			return null;
		for (int npcPoint : mCfg.npcs)
		{
			SBean.NpcPointCFGS npcPointCfg = getNpcPointCfg(npcPoint);
			if (npcPointCfg != null && npcPointCfg.relatedID == npcId)
			{
				return npcPointCfg.position;
			}
		}
		return null;
	}
	
	public SBean.MapLocation tryGetWayPointMapPosition(int roleMapId, SBean.Vector3 rolePos, int wid)
	{
		SBean.MapClusterCFGS mCfg = getMapClusterCFGS(roleMapId);
		if (mCfg == null || !mCfg.wayPoints.contains(wid))
			return null;

		SBean.WayPointCFGS wCfg = getWayPointCFGS(wid);
		if (wCfg == null )
			return null;
		GVector3 posSelf = new GVector3(rolePos);
		GVector3 posWayPoint = new GVector3(wCfg.position);
		if (posSelf.distance(posWayPoint) > wCfg.trigRadius * 2)
			return null;

		return new SBean.MapLocation(wCfg.target.mapID, new SBean.Location(wCfg.target.position, new SBean.Vector3F(1.0f, 0.0f, 0.0f)));
	}

	public SBean.MapLocation getWorldMapRandPos(int mapID)
	{
		SBean.WorldMapCFGS wCfg = this.worldMaps.get(mapID);
		if(wCfg == null || wCfg.randomPos.isEmpty())
			return null;
		
		int rand = GameRandom.getRandom().nextInt(wCfg.randomPos.size());
		return new SBean.MapLocation(mapID, new SBean.Location(wCfg.randomPos.get(rand), new SBean.Vector3F(1.0f, 0.0f, 0.0f))) ;
	}

	public static class LevelExp
	{
		public int level;
		public long exp;
		public long outExp;
		
		public LevelExp(int level, long exp)
		{
			this.level = level;
			this.exp = exp;
			this.outExp = 0;
		}
		
		public LevelExp(int level, long exp, long outExp)
		{
			this.level = level;
			this.exp = exp;
			this.outExp = outExp;
		}
	}

	public LevelExp getAddExpLvlExp(int curLvl, long curExp, long addExp)
	{
		long exp = curExp + addExp;
		int lvl = curLvl + 1;
		for (; lvl <= levels.size(); ++lvl)
		{
			SBean.LevelCFGS cfg = levels.get(lvl - 1);
			if (exp >= cfg.exp)
			{
				if(curLvl == this.levelLimit.base.limitLvl)
					return new LevelExp(curLvl, cfg.exp, exp - cfg.exp);
				
				exp -= cfg.exp;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= levels.size())
		{
			curLvl = levels.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}
	
	public LevelExp getAddOfflineWizardExp(int curLvl, int curExp, int addExp)
	{
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for (; lvl <= offlineExp.levelFunc.size(); ++lvl)
		{
			SBean.OfflineWizardFuncCFGS cfg = offlineExp.levelFunc.get(lvl - 1);
			if (exp >= cfg.levelUpExp)
			{
				exp -= cfg.levelUpExp;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= offlineExp.levelFunc.size())
		{
			curLvl = offlineExp.levelFunc.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}
	
	public LevelExp getAddMrgLvlExp(int curLvl, int curExp, int addExp)
	{
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for (; lvl <= marriage.marriageAttributes.size(); ++lvl)
		{
			SBean.MarriageAttributeCFGS cfg = marriage.marriageAttributes.get(lvl - 1);
			if (exp >= cfg.marriagePoint)
			{
				exp -= cfg.marriagePoint;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= levels.size())
		{
			curLvl = levels.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}
	
	public long getLevelUpExp(int level)
	{
		return level < 0 || level >= this.levels.size() ? ROLE_LEVEL_EXP_MAX : this.levels.get(level).exp;
	}
	
	public int getRoleLevelLimit()
	{
		return levels.size();
	}

	public int getMaxVit(int lvl)
	{
		if (lvl <= 0 || lvl > this.levels.size())
			return -1;
		return this.levels.get(lvl - 1).maxVit;
	}
	
	public int getLevelUpAddVit(int oldLvl, int newLvl)
	{
		if (oldLvl <= 0 || oldLvl >= newLvl || newLvl > this.levels.size())
			return 0;
		int addVit = 0;
		for (int lvl = oldLvl + 1; lvl <= newLvl; ++lvl)
		{
			addVit += this.levels.get(lvl-1).addVit;
		}
		return addVit;
	}
	
	public int getLevelUpAddCredit(int oldLvl, int newLvl)
	{
		if (oldLvl <= 0 || oldLvl >= newLvl || newLvl > this.levels.size())
			return 0;
		
		int addCredit = 0;
		for(int lvl = oldLvl + 1; lvl <= newLvl; ++lvl)
			addCredit += levels.get(lvl - 1).credit;
		
		return addCredit;
	}
	
	public int getDailyTaskLevelBaseExp(int level)
	{
		if (level <= 0 || level > this.levels.size())
			return 0;
		return this.levels.get(level-1).dailyTaskBaseExp;
	}
	
	public int getDemonHoleLevelExp(int level)
	{
		if (level <= 0 || level > this.levels.size())
			return 0;
		
		return this.levels.get(level-1).demonHoleBaseExp;
	}
	
	public int getSectTaskLevelBaseExp(int level)
	{
		if (level <= 0 || level > this.levels.size())
			return 0;
		return this.levels.get(level-1).sectTaskBaseExp;
	}
	
	public int getQuizGiftLevelBaseExp(int level)
	{
		if (level <= 0 || level > this.levels.size())
			return 0;
		return this.levels.get(level-1).quizBaseExp;
	}
	
	public int getOfflineLevelBaseExp(int level)
	{
		if (level <= 0 || level > this.levels.size())
			return 0;
		return this.levels.get(level-1).offlineBaseExp;
	}
	
	public int getMrgTaskLevelBaseExp(int level)
	{
		if (level <= 0 || level > this.levels.size())
			return 0;
		return this.levels.get(level-1).mrgTaskBaseExp;
	}
	
	public int getDaySnatchRedEnvelopeMaxTimes(int level)
	{
		if (level <= 0 || level > this.levels.size())
			return 0;
		return this.levels.get(level-1).maxSnatchRedEnvelopeTimes;
	}
	
	public List<Integer> getSecretTasksWithLevel(int level)
	{
		if (level > this.levels.size() || level < 1)
			return null;
		return this.levels.get(level - 1).secretAreaTasks;
	}

	public SBean.GemLevelUpCostCFGS getGemLevelUpCost(int from, int to)
	{
		SBean.GemCFGS cfg = this.getGemCFG(from);
		if (cfg == null)
			return null;
		int realTo = to < 0 ? -to : to;
		return cfg.lvlupcosts.get(realTo);
	}

	public SBean.EquipLevelUpCostCFGS getEquipLevelUpCost(int from, int to)
	{
		if (from < 0 || to < 0 || from >= to || to > toughens.size())
			return null;
		SBean.EquipToughenCFGS cfg = toughens.get(to);
		if (from >= cfg.cost.size())
			return null;
		return cfg.cost.get(from);
	}

	public SBean.EquipStarUpCostCFGS getEquipStarUpCost(int from, int to)
	{
		if (from < 0 || to < 0 || from >= to || to > upStars.size())
			return null;
		SBean.EquipUpStarCFGS cfg = upStars.get(to);
		if (from >= cfg.cost.size())
			return null;
		return cfg.cost.get(from);
	}

	public SBean.EquipUpStarCFGS getEquipUpStarCFGS(int lvl)
	{
		if (lvl < 0 || lvl >= upStars.size())
			return null;
		return upStars.get(lvl);
	}

	public SBean.SkillBournCFGS getSkillBournCFGS(int lvl)
	{
		if (lvl < 0 || lvl >= skillBourns.size())
			return null;
		return skillBourns.get(lvl);
	}

	public SBean.MainTaskCFGS getMainTaskCFG(int id)
	{
		if (id <= 0 || id > this.mainTasks.size())
			return null;
		return this.mainTasks.get(id - 1);
	}

	public static int getNextMainTaskId(SBean.MainTaskCFGS cfg, int bwType)
	{
		if(cfg.nextId > 0)
			return cfg.nextId;
		
		switch (bwType)
		{
		case GameData.BWTYPE_WHITE:
			return cfg.nextWhiteId;
		case GameData.BWTYPE_BLACK:
			return cfg.nextBlackId;
		default:
			break;
		}
		
		return (cfg.nextId == 0 && cfg.nextWhiteId == 0 && cfg.nextBlackId == 0) ? 0 : -1;
	}
	
	public boolean isTaskValid(int taskID, int bwType)
	{
		SBean.MainTaskBWTypeCFGS cfg = this.bwTypeMainTasks.get(bwType);
		if(cfg == null)
			return false;
		
		return getTaskGrade(taskID, cfg.taskGrades) >= 0;
	}
	
	public boolean testMainTaskFinished(int taskId, int curTaskId, int bwType)
	{
		SBean.MainTaskBWTypeCFGS cfg = this.bwTypeMainTasks.get(bwType);
		if(cfg == null)
			return false;
		
		int taskGrade = getTaskGrade(taskId, cfg.taskGrades);
		int curTaskGrade = getTaskGrade(curTaskId, cfg.taskGrades);
		if(taskGrade < 0 || curTaskGrade < 0)
			return false;
		
		if(taskGrade != curTaskGrade)
			return taskGrade < curTaskGrade;
		
		return testMainTaskFinishedImpl(taskId, curTaskId, cfg.taskGrades.get(taskGrade).head, bwType);
	}
	
	int getTaskGrade(int taskID, List<SBean.MainTaskGradeCFGS> taskGrades)
	{
		for(int grade = 0; grade < taskGrades.size(); grade++)
		{
			if(taskGrades.get(grade).tasks.contains(taskID))
				return grade;
		}
		
		return -1;
	}
	
	public boolean testMainTaskFinishedImpl(int taskId, int curTaskId, int head, int bwType)
	{
		for(SBean.MainTaskCFGS cfg = getMainTaskCFG(head); cfg != null && curTaskId != cfg.id; cfg = getMainTaskCFG((getNextMainTaskId(cfg, bwType))))
		{
			if (cfg.id == taskId)
				return true;
		}
		
		return false;
	}
	
	public SBean.AlterCFGS getAlterCFGS(int id)
	{
		return this.alters.get(id);
	}
	
	public static Collection<SBean.DummyGoods> getMainTaskRewards(SBean.MainTaskCFGS cfg, int classType)
	{
		return getClassTypeRewards(cfg.rewardItems, classType);
	}
	
	public static Collection<SBean.DummyGoods> getClassTypeRewards(List<SBean.ClassTypeReward> rewardItems, int classType)
	{
		List<SBean.DummyGoods> rewards = new ArrayList<>();
		for (SBean.ClassTypeReward r : rewardItems)
		{
			if (classType <= 0 || classType > r.ids.size() || r.ids.get(classType - 1) == 0)
				continue;
			
			rewards.add(new SBean.DummyGoods(r.ids.get(classType - 1), r.count));
		}
		return rewards;
	}
	
	public SBean.WeaponTaskCFGS getWeaponTaskCFG(int type, int id)
	{
		if (type < 0 || type >= this.weaponTasks.size())
			return null;
		if (id <= 0 || id > this.weaponTasks.get(type).tasks.size())
			return null;
		return this.weaponTasks.get(type).tasks.get(id - 1);
	}
	
	
	public SBean.WeaponTaskCFGS getNextWeaponTaskCFG(int roleLvl, int type, int id)
	{
		if (type < 0 || type >= this.weaponTasks.size() || id <= 0)
			return null;
		int nextType = type;
		int nextId = id + 1;
		if (type < this.weaponTasks.size() - 1)
		{
			if (nextId > this.weaponTasks.get(nextType).tasks.size())
			{
				nextType += 1;
				nextId = 1;
			}
		}
		if (nextType == this.weaponTasks.size() - 1)
		{
			nextId = this.getNextWeaponRingTaskId(roleLvl);
		}
		return this.weaponTasks.get(nextType).tasks.get(nextId - 1);
	}

	private int getNextWeaponRingTaskId(int level)
	{
		if (level <= 0 || level > levels.size())
			return -1;
		SBean.LevelCFGS cfg = levels.get(level - 1);
		int index = GameRandom.getRandom().nextInt(cfg.weaponRingTaskIds.size());
		return cfg.weaponRingTaskIds.get(index);
	}

	public SBean.PetTaskCFGS getPetTaskCFG(int id)
	{
		if (id <= 0 || id > this.petTasks.size())
			return null;
		return this.petTasks.get(id - 1);
	}

	public SBean.SectTaskCFGS getSectTaskCFG(int id)
	{
		if (id <= 0 || id > this.sectTasks.size())
			return null;
		return this.sectTasks.get(id - 1);
	}

	public int getSectTaskNextRefreshTime(int now)
	{
		List<Integer> refreshTimes = this.getCommonCFG().sect.taskRefreshTime;
		int nextTime = refreshTimes.get(refreshTimes.size() - 1);
		int daySecond = GameTime.getSecondOfDay(now);
		for (int t : refreshTimes)
		{
			if (t <= daySecond)
			{
				nextTime = daySecond;
				break;
			}
		}
		return nextTime;
	}

	public int getSectTaskLastRefreshTime()
	{
		int s = GameTime.getSecondOfDay();
		List<Integer> refreshTime = this.getCommonCFG().sect.taskRefreshTime;
		int lastRefreshTime = refreshTime.get(refreshTime.size() - 1) - GameTime.getDayTimeSpan();
		for (int t : refreshTime)
		{
			if (s >= t)
				lastRefreshTime = t;
		}
		return GameTime.getDayTime(lastRefreshTime);
	}

	public List<SBean.SectTask> getSectTasksByCount(int sId, int total)
	{
		List<SBean.SectTask> resultTasks = new ArrayList<>();
		List<SBean.SectTaskCFGS> taskCopy = new ArrayList<>();
		taskCopy.addAll(this.sectTasks);

		while (true)
		{
			int index = GameRandom.getRandInt(0, taskCopy.size() - 1);
			SBean.SectTaskCFGS taskCFGS = taskCopy.get(index);
			resultTasks.add(new SBean.SectTask(++sId, taskCFGS.id));
			taskCopy.remove(index);
			if (resultTasks.size() >= total || taskCopy.size() <= 0)
			{
				break;
			}
		}

		return resultTasks;
	}

	public List<SBean.SectTaskCFGS> getAllSectTaskCFGS()
	{
		List<SBean.SectTaskCFGS> taskCopy = new ArrayList<>();
		taskCopy.addAll(this.sectTasks);

		return taskCopy;
	}

	public Map<Integer, SBean.SectTask> getRandomSectTasks(int lastSeq, int count)
	{
		Map<Integer, SBean.SectTask> tasks = new TreeMap<>();
		Set<Integer> taskIds = new TreeSet<>();
		while (true)
		{
			int index = GameRandom.getRandom().nextInt(this.sectTasks.size());
			SBean.SectTaskCFGS taskCFGS = this.sectTasks.get(index);
			if (!taskIds.contains(taskCFGS.id))
			{
				taskIds.add(taskCFGS.id);
				SBean.SectTask t = new SBean.SectTask(++lastSeq, taskCFGS.id);
				tasks.put(t.sid, t);
				if (tasks.size() >= count)
					break;
			}
		}
		return tasks;
	}

	public SBean.PetTaskCFGS getNextPetTaskCFG(int petid, int coPracticeLvl)
	{
		if (coPracticeLvl <= 0 || coPracticeLvl > this.petCoPractices.size())
			return null;
		SBean.PetCoPracticeCFGS petCoPracticeCFGS = this.petCoPractices.get(petid).petCoPracticeCFGSs.get(coPracticeLvl-1);
		List<Integer> gtasks = petCoPracticeCFGS.tasklist;
		if (gtasks == null)
			return null;

		int index = GameRandom.getRandom().nextInt(gtasks.size());
		int tid = gtasks.get(index);
		if (tid <= 0 || tid > this.petTasks.size())
			return null;
		return this.petTasks.get(tid-1);
	}

	public SBean.MrgSeriesTaskGroupCFGS getMrgSeriesTaskGroupCFGS(int groupID)
	{
		return this.mrgSeriesTaskGroups.get(groupID);
	}
	
	public static SBean.MrgSeriesTaskCFGS getMrgSeriesTaskCFGS(SBean.MrgSeriesTaskGroupCFGS groupCfg, int taskID)
	{
		if(taskID <= 0 || taskID > groupCfg.tasks.size())
			return null;
		
		return groupCfg.tasks.get(taskID - 1);
	}
	
	public SBean.MrgLoopTaskCFGS getMrgLoopTaskCFGS(int taskID)
	{
		if(taskID <= 0 || taskID > this.mrgLoopTasks.size())
			return null;
		
		return this.mrgLoopTasks.get(taskID - 1);
	}
	
	public int getMrgLoopRandTask(int level)
	{
		if (level <= 0 || level > levels.size())
			return -1;
		
		SBean.LevelCFGS cfg = levels.get(level - 1);
		int index = GameRandom.getRandom().nextInt(cfg.mrgLoopTasks.size());
		return cfg.mrgLoopTasks.get(index);
	}
	
	public List<SBean.PropAwardCFGS> getPropTypeAward(int type)
	{
		return this.propAwards.get(type);
	}

	public SBean.TrapExpandedCFGS getTrapCFG(int id)
	{
		return this.traps.get(id);
	}

	public int getRandomEquipDurability(SBean.EquipCFGS cfg, float prAdjust)
	{
		return cfg.rank >= 4 ? (GameRandom.getRandom().nextFloat() <= (common.equip.probability + prAdjust) ? common.equip.initDurability : -1) : -1;
	}

//	public int getPetFriendLevelLimit()
//	{
//		return this.petFriends.size();
//	}
//	
//	public int getPetFriendLevel(int friendliness)
//	{
//		int level = 0;
//		for (SBean.PetFriendCFGS cfg : this.petFriends)
//		{
//			if (friendliness >= cfg.friendliness)
//			{
//				friendliness -= cfg.friendliness;
//				level++;
//				continue;
//			}
//			break;
//		}
//		if (level >= this.petFriends.size())
//			level = this.petFriends.size();
//		return level;
//	}
	
	public int getPetCoPracticeLevelLimit(int petid)
	{
		return this.petCoPractices.get(petid).petCoPracticeCFGSs.size();
	}

	public int getPetCoPracticeLevel(int petid, int coPracticeExp)
	{
		int level = 0;
		for (SBean.PetCoPracticeCFGS cfg : this.petCoPractices.get(petid).petCoPracticeCFGSs)
		{
			if (coPracticeExp >= cfg.needExp)
			{
				coPracticeExp -= cfg.needExp;
				level++;
				continue;
			}
			break;
		}
		if (level >= this.petCoPractices.size())
			level = this.petCoPractices.size();
		return level;
	}

	public List<SBean.PetCFGS> getAllPetsCFG()
	{
		return this.pets;
	}

	public SBean.PetCFGS getPetCFG(int id)
	{
		if (id <= 0 || id > this.pets.size())
			return null;
		return this.pets.get(id-1);
	}

//	public SBean.PetFriendCFGS getPetFriendCFG(int lvl)
//	{
//		if (lvl <= 0 || lvl > this.petFriends.size())
//			return null;
//		return this.petFriends.get(lvl-1);
//	}

	public SBean.PetCoPracticeCFGS getPetCoPracticeCFG(int petid, int lvl)
	{
		SBean.PetCoPracticeGroupCFGS groupCfg = this.petCoPractices.get(petid);
		if(groupCfg == null)
			return null;
		
		if (lvl <= 0 || lvl > groupCfg.petCoPracticeCFGSs.size())
			return null;
		return this.petCoPractices.get(petid).petCoPracticeCFGSs.get(lvl - 1);
	}

	public SBean.PetTransformCFGS getPetTransfromCFG(int lvl)
	{
		if (lvl < 0 || lvl >= this.petTransforms.size())
			return null;
		return this.petTransforms.get(lvl);
	}
	
	public int getPetTransformMaxLevel(int tlvl)
	{
		int nextTlvl = tlvl + 1;
		if (nextTlvl < 1)
			nextTlvl = 1;
		if (nextTlvl < this.petTransforms.size())
			return this.petTransforms.get(nextTlvl).mustTransfromLvl;
		return getPetLevelLimit();
	}
	
	public int getPetTransfromLevelFromLvl(int lvl)
	{
		for (SBean.PetTransformCFGS transform : petTransforms)
		{
			if (transform.mustTransfromLvl > lvl)
				return transform.tlevel - 1;
		}
		return 0;
	}

	public SBean.PetLvlCFGS getPetLevelCFG(int lvl)
	{
		if (lvl <= 0 || lvl > this.petLvls.size())
			return null;
		return this.petLvls.get(lvl-1);
	}
	
	public int getPetLevelLimit()
	{
		return this.petLvls.size();
	}
	
	public int getPetStarLimit(int petId)
	{
		return this.pets.size() > petId && petId > 0 ? this.pets.get(petId - 1).stars.size() - 1 : -1;
	}
	
	public LevelExp getAddPetExpLvlExp(int curLvl, int curExp, int addExp)
	{
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for (; lvl <= this.petLvls.size(); ++lvl)
		{
			SBean.PetLvlCFGS lvlcfg = this.petLvls.get(lvl - 1);
			if (exp >= lvlcfg.exp)
			{
				exp -= lvlcfg.exp;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= this.petLvls.size())
		{
			curLvl = this.petLvls.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}

	public static SBean.PetStarCFGS getPetStarCFG(SBean.PetCFGS cfg, int star)
	{
		if (star < 0 || star >= cfg.stars.size())
			return null;
		return cfg.stars.get(star);
	}

	public SBean.PetBreakSkillLevelCFGS getPetBreakSkillCFG(int skillId, int lvl)
	{
		SBean.PetBreakSkillCFGS cfg = this.petBreakSkills.get(skillId);
		if (cfg == null)
			return null;
		if (lvl < 0 || lvl >= cfg.levels.size())
			return null;

		return cfg.levels.get(lvl);
	}

	public int getPetMaxLevel()
	{
		return this.petLvls.size();
	}
	
	public SBean.PetSpiritGroupCFGS getPetSpiritGroupCFGS(int spiritID)
	{
		return this.petSpiritGroups.get(spiritID);
	}
	
	public SBean.PetSpiritCFGS getPetSpiritCFGS(int spiritID, int lvl)
	{
		SBean.PetSpiritGroupCFGS group = getPetSpiritGroupCFGS(spiritID);
		if(group == null)
			return null;
		
		return getPetSpiritCFGS(group, lvl);
	}
	
	public static SBean.PetSpiritCFGS getPetSpiritCFGS(SBean.PetSpiritGroupCFGS groupCfg, int lvl)
	{
		if(lvl <= 0 || lvl > groupCfg.spirits.size())
			return null;
		
		return groupCfg.spirits.get(lvl - 1);
	}
	
	public int getPetMaxExploit(int petID)
	{
		SBean.PetExploitCFGS cfg = getPetExploitCFGS(petID);
		if(cfg == null || cfg.exploits.isEmpty())
			return -1;
		
		return cfg.exploits.get(cfg.exploits.size() -1);
	}
	
	public SBean.PetExploitCFGS getPetExploitCFGS(int petID)
	{
		return this.petExploits.get(petID);
	}
	
	public int getPetExploit(int petID, int index)
	{
		SBean.PetExploitCFGS cfg = this.petExploits.get(petID);
		if(cfg == null || index <= 0 || index > cfg.exploits.size())
			return -1;
		
		return cfg.exploits.get(index - 1);
	}
	
	public static int petSpiritLearn(SBean.PetSpiritGroupCFGS group, int maxLevel)
	{
		maxLevel = maxLevel > group.spirits.size() ?  group.spirits.size() : maxLevel;
		float sum = 0;
		List<Float> weights = new ArrayList<>();
		for(int i = 0; i < maxLevel; i++)
		{
			sum += group.spirits.get(i).weight;
			weights.add(sum);
		}
		
		float rnd = GameRandom.getRandFloat(0, 1);
		for(int i = 0; i < weights.size(); i++)
		{
			if(rnd <= weights.get(i) / sum)
				return i + 1;
		}
		
		return maxLevel;
	}
	
//	public int getPetNormalReviveCost(int evoLvl, int lvl)
//	{
//		SBean.CommonRevivesCFGS cfg = this.common.revives;
//		if (cfg == null)
//			return 0;
//		float arg1 = cfg.petMoneyArg1;
//		int arg2 = cfg.petMoneyArgs.get(0);
//		int arg3 = cfg.petMoneyArgs.get(1);
//		int arg4 = cfg.petMoneyArgs.get(2);
//		return (int) ((arg2 + arg1 * evoLvl) * (arg3 + arg4 * lvl));
//	}
//
//	public int getPetStoneReviveCost()
//	{
//		SBean.CommonRevivesCFGS cfg = this.common.revives;
//		if (cfg == null)
//			return 0;
//		return cfg.petStoneCost;
//	}
	
    public Set<Integer> getFlashSaleConfig()
    {
        return flashsaleResources;
    }
    
    public Set<Integer> getAdversBackgroundCfg()
    {
        return adverBackgrounds;
    }

	public List<Integer> getAuthority()
	{
		List<Integer> auths = new ArrayList<>();
		for (SBean.SectAuthorityCFGS auth : this.sectAuthority.values())
		{
			if (auth.accept == 1)
			{
				auths.add(auth.id);
			}
		}
		return auths;
	}

	public SBean.SectAuthorityCFGS getSectAuthorityCFGS(int type)
	{
		return this.sectAuthority.get(type);
	}

	public SBean.FightSPCFGS getfFightSPCFGS(int id)
	{
		return this.fightSP.get(id);
	}

	public SBean.BlurCFGS getBlurCFGS(int id)
	{
		return this.blurs.get(id);
	}

	public int getReaNameGrade(int pkValue)
	{
		for (int index = 0; index < pkSystem.deltaMax.size(); ++index)
		{
			if (pkValue <= pkSystem.deltaMax.get(index))
				return index;
		}
		return pkSystem.deltaMax.size() - 1;
	}
	
	public float getPriceIncrease(int pkValue)
	{
		int pos = getReaNameGrade(pkValue);
		return pkSystem.priceIncrease.get(pos);
	}
	

	public int getRedNameDamageDecreases(int grade)
	{
		List<Integer> damageDecreases = pkSystem.dmgDecrease;
		if (grade < 0 || grade > (damageDecreases.size() - 1))
			return 0;

		return damageDecreases.get(grade);
	}

	// 自创武功
	public SBean.DIYSkillBaseCFGS getDIYSkillBaseCFGS(int lvl)
	{
		if (lvl <= 0 || lvl > this.diySkills.size())
			return null;

		return this.diySkills.get(lvl - 1);
	}

	public SBean.DIYSkillGradeCFGS getDIYSkillGradeCFGS(int id)
	{
		return this.diySkillGrades.get(id);
	}

	public SBean.DIYSkillActionCFGS getDIYSkillActionCFGS(int id)
	{
		return this.diySkillActions.get(id);
	}

	public boolean chechDIYSkillIcon(int iconId)
	{
		return this.diySkillUnique.scores.contains(iconId);
	}

	public SBean.DIYSkillUniqueCFGS getDIYSkillUniqueCFGS()
	{
		return this.diySkillUnique;
	}
	
	public int getDiySkillGradeId(int level, boolean isPutAllPoint)
	{
		if(isPutAllPoint == false)
			return 1; //未投入所有悟道点，默认返回D等级
		float rnd = GameRandom.getRandom().nextFloat();
		for (SBean.DIYSkillGradeCFGS skillGradeCFGS : this.diySkillGrades.values())
		{
			if(rnd <= skillGradeCFGS.createProb)
			{
				//如果熟练度等级为1并且随机到评分等级为5（s）则从新随机
				if(level == 1 && skillGradeCFGS.id == 5)
					return getDiySkillGradeId(level, isPutAllPoint);
				return skillGradeCFGS.id;
			}
		}
		return 0;
	}

	public static class DIYBuffLib
	{
		public DIYBuffLib(int libType)
		{
			this.libType = libType;
			buffs = new ArrayList<>();
		}

		public List<SBean.DIYBUFFCFGS> buffs;
		public int libType;
	}

	public List<SBean.DIYBUFFCFGS> getDIYBuffLib(int libType)
	{
		List<SBean.DIYBUFFCFGS> list = new ArrayList<>();
		DIYBuffLib lib = diyBuffLibs.get(libType);
		if (lib != null)
			list = lib.buffs;

		return list;
	}

	public SBean.DiySkillSlotUnblockCFGS getDiySkillSlotUnblockCFGS(int id)
	{
		return this.slotUnblock.get(id);
	}

	public int getDiySkillTotalUnlockSlot()
	{
		int count = 0;
		for(SBean.DiySkillSlotUnblockCFGS t : this.slotUnblock.values())
		{
			if(t.isOpen != 1)
				count ++;
		}
		return count;
	}
	
	public int getDiySkillSlotTotal()
	{
		return this.slotUnblock.size();
	}

	public SBean.DBDIYSkillData createDIYSkill(int classType, Map<Integer, Integer> skillArgs, int proficietLvl, int skillGrade)
	{
		SBean.DIYSkillBaseCFGS baseCfg = this.getDIYSkillBaseCFGS(proficietLvl);
		SBean.DIYSkillUniqueCFGS uniqueCfg = this.diySkillUnique;
		if (baseCfg == null || uniqueCfg == null)
			return null;

		// skillGrade 评级
		SBean.DIYSkillGradeCFGS gradeCfg = this.getDIYSkillGradeCFGS(skillGrade);
		if (gradeCfg == null)
			return null;

		float scoreArg = GameRandom.getRandFloat(gradeCfg.minFix, gradeCfg.maxFix); // 评分随机乘数

		SBean.DBDIYSkillData dbSkillData = new SBean.DBDIYSkillData();
		dbSkillData.gradeId = skillGrade;
		int invest = 0;

		// 连-参数3
		int damageTimes = 1;
		invest = skillArgs.getOrDefault(GameData.DIY_SKILL_COMBO, 0);
		if (invest > 0)
			damageTimes = getDiySkillDamageTimes(invest, baseCfg.damages);
		dbSkillData.damageTimes = (byte) damageTimes;
		
		// 劲-参数1
		dbSkillData.damageArgs = new ArrayList<>();
		invest = skillArgs.getOrDefault(GameData.DIY_SKILL_POWER, 0);
		float arg1 = (baseCfg.damageArg1.baseProb + baseCfg.damageArg1.unit * invest) * scoreArg / damageTimes;
		float arg2 = (baseCfg.damageArg2.baseProb + baseCfg.damageArg2.unit * invest) * scoreArg / damageTimes;
		dbSkillData.damageArgs.add(arg1);
		dbSkillData.damageArgs.add(arg2);

		// 切-参数2
		invest = skillArgs.getOrDefault(GameData.DIY_SKILL_INCISE, 0);
		dbSkillData.atrDecrease = (baseCfg.atrDecrease.baseProb + baseCfg.atrDecrease.unit * invest) * scoreArg;
		dbSkillData.acrDecrease = (baseCfg.acrDecrease.baseProb + baseCfg.acrDecrease.unit * invest) * scoreArg;

		// 御-参数4
		invest = skillArgs.getOrDefault(GameData.DIY_SKILL_CDSP, 0);
		dbSkillData.cd = (int) ((baseCfg.cd.max - baseCfg.cd.unit * invest) * scoreArg);
		if (dbSkillData.cd < baseCfg.cd.min)
			dbSkillData.cd = (int) baseCfg.cd.min;

		dbSkillData.addSP = (int) ((baseCfg.addSP.min + baseCfg.addSP.unit * invest) * scoreArg);
		if (dbSkillData.addSP > baseCfg.addSP.max)
			dbSkillData.addSP = (int) baseCfg.addSP.max;

		// 缠-参数5
		invest = skillArgs.getOrDefault(GameData.DIY_SKILL_WIND, 0);
		dbSkillData.scope = new ArrayList<>();
		int scopeType = getDiySkillScopeType(uniqueCfg.scopeOdds);
		setDiySkillScope(dbSkillData.scope, baseCfg.scopes.get(scopeType), scopeType, invest, scoreArg);

		dbSkillData.skillActionID = getDiySkillActionID(classType, dbSkillData.scope.get(0), scopeType);
		SBean.DIYSkillActionCFGS actionCfg = this.getDIYSkillActionCFGS(dbSkillData.skillActionID);
		if (actionCfg == null)
			return null;
		
		// 追-参数6（破、控、幻）
		dbSkillData.buffs = new ArrayList<>();
		if (baseCfg.chaseCount > 0)
			setDiySkillBuff(baseCfg, uniqueCfg, dbSkillData.buffs, skillArgs, scoreArg);

		return dbSkillData;
	}
	
	// 连-参数3
	int getDiySkillDamageTimes(int invest, List<SBean.DamageCFGS> damages)
	{
		float oneDamage = damages.get(0).baseProb + invest * damages.get(0).unit;
		float twoDamages = damages.get(1).baseProb + invest * damages.get(1).unit;
		float rnd = GameRandom.getRandom().nextFloat();
		if (rnd < oneDamage)
			return 1;
		else if (rnd < (oneDamage + twoDamages))
			return 2;

		return 3;
	}
	
	// 缠-参数5
	int getDiySkillScopeType(Map<Integer, Float> scopeOdds)
	{
		float rnd = GameRandom.getRandom().nextFloat();
		if (rnd < scopeOdds.get(Skill.eSScopT_Single))
			return Skill.eSScopT_Single;
		else if (rnd <= scopeOdds.get(Skill.eSScopT_CricleO))
			return Skill.eSScopT_CricleO;
		else if (rnd <= scopeOdds.get(Skill.eSScopT_CricleT))
			return Skill.eSScopT_CricleT;
		else if (rnd <= scopeOdds.get(Skill.eSScopT_SectorO))
			return Skill.eSScopT_SectorO;

		return Skill.eSScopT_RectO;
	}
	
	// 缠-参数5
	void setDiySkillScope(List<Integer> scope, SBean.ParaCFGS paraCfg, int scopeType, int invest, float scoreArg)
	{
		int length = 0;
		int radius = 0;
		int width = 0;
		int angle = 0;
		switch (scopeType)
		{
//		case Skill.eSScopT_Single:
//			length = (int) ((paraCfg.min + paraCfg.unit * invest) * scoreArg);
//			if (length > paraCfg.max)
//				length = (int) paraCfg.max;
//
//			scope.add(length);
//			break;
//		case Skill.eSScopT_CricleT:
//			radius = (int) ((paraCfg.min + paraCfg.unit * invest) * scoreArg);
//			if (radius > paraCfg.max)
//				radius = (int) paraCfg.max;
//	
//			length = radius;
//			scope.add(length);
//			scope.add(radius);
//			break;
		case Skill.eSScopT_CricleO:
			radius = (int) ((paraCfg.min + paraCfg.unit * invest) * scoreArg);
			if (radius > paraCfg.max)
				radius = (int) paraCfg.max;

			scope.add(radius);
			break;
		case Skill.eSScopT_SectorO:
			radius = (int) ((paraCfg.min + paraCfg.unit * invest) * scoreArg);
			if (radius > paraCfg.max)
				radius = (int) paraCfg.max;

//			angle = GameRandom.getRandInt(uniqueCfg.minAngle, uniqueCfg.maxAngle);
			scope.add(radius);
			scope.add(angle);
			break;
		case Skill.eSScopT_RectO:
			length = (int) ((paraCfg.min + paraCfg.unit * invest) * scoreArg);
			if (length > paraCfg.max)
				length = (int) paraCfg.max;

//			width = GameRandom.getRandInt(uniqueCfg.minWidth, uniqueCfg.maxWidth);
			scope.add(length);
			scope.add(width);
			break;
		default:
			scope.add(length);
			scope.add(radius);
			break;
		}
	}
	
	int getDiySkillActionID(int classType, int range, int scopeType)
	{
		int grade = range / 100 - 1;
		if(scopeType == Skill.eSScopT_RectO)
			grade -= 1;
		
		grade = Math.min(1, grade);
		grade = Math.max(4, grade);
		
		int rnd = GameRandom.getRandom().nextInt(2) + 1;
		
		return createDiySkillAuctionID(classType, scopeType, grade, rnd);
	}
	
	public static int createDiySkillAuctionID(int classType, int scopeType, int grade, int randActionID)
	{
		return classType * 1000 + scopeType * 100 + grade * 10 + randActionID;
	}
	
	public static void fixDiySkillScope(int classType, SBean.Scope scope)
	{
		int length = 0;
		int radius = 0;
		int width = 200;
		int angle = 120;
		
		boolean far = classType == CLASS_TYPE_ARROW || classType == CLASS_TYPE_HEAL;
		switch (scope.type)
		{
		case Skill.eSScopT_CricleO:
			if(scope.args.size() < 1)
				return;
			
			if(far)
			{
				length = scope.args.get(0) + 250;
				scope.args.set(0, length);
			}
			break;
		case Skill.eSScopT_SectorO:
			if(scope.args.size() < 2)
				return;
			if(far)
			{
				radius = (scope.args.get(0) - 50) * 2 + 200;
				scope.args.set(0, radius);
				angle = 60;
			}
			scope.args.set(1, angle);
			break;
		case Skill.eSScopT_RectO:
			if(scope.args.size() < 2)
				return;
			
			if(far)
			{
				length = (scope.args.get(0) - 100) * 2 + 200;
				scope.args.set(0, length);
				width = 150;
			}
			scope.args.set(1, width);
			break;
		default:
			break;
		}
	}
	
	// 追-参数6（破、控、幻）
	void setDiySkillBuff(SBean.DIYSkillBaseCFGS baseCfg, SBean.DIYSkillUniqueCFGS uniqueCfg, List<SBean.DBDIYBUFF> buffs, Map<Integer, Integer> skillArgs, float scoreArg)
	{
		int invest = 0;
		
		invest = skillArgs.getOrDefault(GameData.DIY_SKILL_CHASE, 0);
		Map<Integer, Integer> weights = new TreeMap<>();
		int count = 0;
		for (int i = GameData.DIY_SKILL_BREAK; i <= GameData.DIY_SKILL_UNREAL; i++)
		{
			int v = skillArgs.getOrDefault(i, 0);
			weights.put(i, v);
			if (v > 0)
				count++;
		}

		int sum = 0;
		int weight = 0;
		if (count == 0)
		{
			weight = uniqueCfg.chaseDefaultW;
			for (int i = GameData.DIY_SKILL_BREAK; i <= GameData.DIY_SKILL_UNREAL; i++)
			{
				sum += weight;
				weights.put(i, sum);
			}
		}
		else if (count > 0 && count < 3)
		{
			for (int i = GameData.DIY_SKILL_BREAK; i <= GameData.DIY_SKILL_UNREAL; i++)
			{
				if (weights.get(i) > 0)
					weight = uniqueCfg.chaseSelectW;
				else
				{
					weight = (10000 - count * uniqueCfg.chaseSelectW) / (weights.size() - count);
				}

				sum += weight;
				weights.put(i, sum);
			}
		}

		int rnd = 0;
		int libType = 0;
		for (int i = 0; i < baseCfg.chaseCount; i++)
		{
			rnd = GameRandom.getRandom().nextInt(sum);
			Iterator<Map.Entry<Integer, Integer>> it = weights.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<Integer, Integer> entry = it.next();
				weight = entry.getValue();
				if (rnd < weight)
				{
					SBean.DBDIYBUFF dbBuff = new SBean.DBDIYBUFF(new SBean.SubStatus(), 0, 0);
					libType = getDiySkillBuffType(entry.getKey());

					List<SBean.DIYBUFFCFGS> list = this.getDIYBuffLib(libType);
					if (list != null && list.size() > 0)
					{
						rnd = GameRandom.getRandom().nextInt(list.get(0).sum);
						int w = 0;
						for (SBean.DIYBUFFCFGS b : list)
						{
							w += b.weight;
							if (rnd < w)
							{
								dbBuff.status.buffID = b.buffID;
								break;
							}
						}
						dbBuff.status.odds = (int) ((baseCfg.buffOdds.min + baseCfg.buffOdds.unit * invest) * scoreArg);
						if (dbBuff.status.odds > baseCfg.buffOdds.max)
							dbBuff.status.odds = (int) baseCfg.buffOdds.max;

						SBean.BuffCFGS buffCfg = this.getBuffCFG(dbBuff.status.buffID);
						if (buffCfg != null)
							setDiySkillBuffAffect(dbBuff, libType, baseCfg, invest, scoreArg, buffCfg.affectID);
					}

					buffs.add(dbBuff);
					it.remove();
					break;
				}
			}
		}
	}
	
	int getDiySkillBuffType(int type)
	{
		switch (type)
		{
		case GameData.DIY_SKILL_BREAK:
			return DIY_BUFF_BREAK;
		case GameData.DIY_SKILL_CONTROL:
			return DIY_BUFF_CONTROL;
		case GameData.DIY_SKILL_UNREAL:
			return DIY_BUFF_UNREAL;
		default:
			break;
		}
		
		return 0;
	}
	
	void setDiySkillBuffAffect(SBean.DBDIYBUFF dbBuff, int libType, SBean.DIYSkillBaseCFGS baseCfg, int invest, float scoreArg, int affectID)
	{
		Map<Integer, SBean.ParaCFGS> loopTimes = null;
		Map<Integer, SBean.ParaCFGS> affectValus = null;
		switch (libType)
		{
		case DIY_BUFF_BREAK:
			loopTimes = baseCfg.breakLoopTimes;
			affectValus = baseCfg.breakValues;
			break;
		case DIY_BUFF_CONTROL:
			loopTimes = baseCfg.controlLoopTimes;
			affectValus = baseCfg.controlValues;
			break;
		case DIY_BUFF_UNREAL:
			loopTimes = baseCfg.unrealLoopTimes;
			affectValus = baseCfg.unrealValues;
			break;
		default:
			break;
		}

		if (loopTimes != null && affectValus != null)
		{
			SBean.ParaCFGS loopTime = loopTimes.get(affectID);
			SBean.ParaCFGS affectValue = affectValus.get(affectID);
			if (loopTime != null && affectValue != null)
			{
				dbBuff.loopTime = (int) ((loopTime.min + loopTime.unit * invest) * scoreArg);
				dbBuff.affectValue = (int) ((affectValue.min + affectValue.unit * invest) * scoreArg);
			}
		}
	}
	
	public String getRandomName(Random random, int gender)
	{
		StringBuilder name = new StringBuilder("");
		int r = random.nextInt(this.randomNames.familyname.size());

		name.append(this.randomNames.familyname.get(r));
		if (gender == 1)
		{
			r = random.nextInt(this.randomNames.malename.size());
			name.append(this.randomNames.malename.get(r));
		}
		else
		{
			r = random.nextInt(this.randomNames.femalename.size());
			name.append(this.randomNames.femalename.get(r));
		}

		return name.toString();
	}
	
	public static int getRandomMultiplier(List<SBean.CritCFGS> crits)
	{
		float r = GameRandom.getRandom().nextFloat();
		int multiplier = crits.get(crits.size() - 1).multiplier;
		for (SBean.CritCFGS e : crits)
		{
			if (r < e.probability)
			{
				multiplier = e.multiplier;
				break;
			}
		}
		return multiplier;
	}

	public SBean.ProduceCFGS getProduceCFGS()
	{
		return this.produce;
	}
	
	public int getProduceLvlUpNeedExp(int lvl)
	{
		if(lvl <= 0 || lvl > this.produce.lvlNeedExp.size())
			return 0;
		
		return this.produce.lvlNeedExp.get(lvl - 1);
	}
	
	public Set<Integer> getProduceDefaultRecipes()
	{
		return this.common.produce;
	}
	
	public SBean.ProduceRecipeCFGS getProduceRecipeCFGS(int id)
	{
		return this.produceRecipes.get(id);
	}
	
	public LevelExp getAddProduceExpLvlExp(int curLvl, int curExp, int addExp)
	{
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		List<Integer> levels = this.produce.lvlNeedExp;
		if(lvl > levels.size())
			return new LevelExp(curLvl, exp);
		
		for (; lvl <= levels.size(); ++lvl)
		{
			int needExp = levels.get(lvl - 1);
			if (exp >= needExp)
			{
				exp -= needExp;
				curLvl++;
				continue;
			}
			break;
		}
		if (curLvl >= levels.size())
		{
			curLvl = levels.size();
			exp = 0;
		}
		return new LevelExp(curLvl, exp);
	}
	
	public int getMaxSplitSP(int vipLvl)
	{
		//TODO vipLvl to count
		return this.produce.spMax;
	}
	
	public int getCostSplitSPPerFusion()
	{
	    return this.fusion.costEnergy;
	}
	
	public int getFullFusionPoint()
	{
	    return this.fusion.fullPoint;
	}
	
	public int getFusionOpenLevel()
	{
	    return this.fusion.openLevel;
	}
	
    public boolean checkFusionOpen(int level)
    {
        return level >= getFusionOpenLevel();
    }
	
	private SBean.FusionSectionCFGS getFusionRewardSectionByLevel(int level)
	{
	    for (SBean.FusionSectionCFGS section : fusion.fusionsection)
	    {
	        if (section.startLevel <= level && section.endLevel >= level)
	        {
	            return section;
	        }
	    }
	    
	    return null;
	}
	
	public Map<Integer, Integer> getFusionRewardsDrop(int level, int times)
	{
	    Map<Integer, Integer> rewardsMap = new HashMap<>();
	    SBean.FusionSectionCFGS sectionCfg = getFusionRewardSectionByLevel(level);
	    if (sectionCfg != null)
	    {
	        rewardsMap = getMergedRandomDrop(sectionCfg.reward, times, 1); 
	    }
	    return rewardsMap;
	}
	
	public SBean.AiTrigerCFGS getAiTrigerCFGS(int trigID)
	{
		return this.aitrigers.get(trigID);
	}
	
	public SBean.TrigEventCFGS getTrigEventCFGS(int eventID)
	{
		return this.trigEvents.get(eventID);
	}
	
	public SBean.TrigBehaviorCFGS getTrigBehaviorCFGS(int behaviorID)
	{
		return this.trigBehaviors.get(behaviorID);
	}
	
	public Map<Integer, SBean.WorldBossCFGS> getAllWorldBoss()
	{
		return this.worldBosses;
	}
	
	public Map<Integer, SBean.WorldMonsterCFGS> getAllWorldSuperMonsters()
	{
		return this.worldSuperMonsters;
	}
	
	public Collection<SBean.WorldMineralCFGS> getAllWorldMinerals()
	{
		return this.worldMinerals.values();
	}
	
	public SBean.WorldBossCFGS getWorldBossCFGS(int bossID)
	{
		return this.worldBosses.get(bossID);
	}
	
	public SBean.WorldMonsterCFGS getWorldSuperMonster(int id)
	{
		return this.worldSuperMonsters.get(id);
	}
	
	public SBean.WorldMineralCFGS getWorldMineral(int id)
	{
		return this.worldMinerals.get(id);
	}
	
	public static int getClassTypeDropTblID(SBean.ClassTypeDrop cd, int classType)
	{
		if(classType <= 0 || classType > cd.dropIDs.size())
			return 0;
		
		return cd.dropIDs.get(classType - 1);
	}
	
	public static SBean.ClassTypeDrop getRankDrop(List<SBean.ClassTypeDrop> rankDrops, int rank)
	{
		if(rank <= 0 || rank > rankDrops.size())
			return null;
		
		return rankDrops.get(rank - 1);
	}
	
	public SBean.MapLocation getWorldBossMapLocation(int bossID, int seq)
	{
		SBean.WorldBossCFGS cfg = this.getWorldBossCFGS(bossID);
		if(cfg == null || seq <= 0 || seq > cfg.base.refreshPos.size())
			return null;
		return new SBean.MapLocation(cfg.base.mapID, new SBean.Location(cfg.base.refreshPos.get(seq - 1), new SBean.Vector3F(1.0f, 0.0f, 0.0f)));
	}
	
	public SBean.MapLocation getSuperMonsterMapLocation(int monsterID, int seq)
	{
		SBean.WorldMonsterCFGS cfg = GameData.getInstance().getWorldSuperMonster(monsterID);
		if(cfg == null || seq <= 0 || seq > cfg.refreshPos.size())
			return null;
		return new SBean.MapLocation(cfg.mapID, new SBean.Location(cfg.refreshPos.get(seq - 1), new SBean.Vector3F(1.0f, 0.0f, 0.0f)));
	}
	
	public SBean.MapLocation getMineralMapLocation(int mineralID, int seq)
	{
		SBean.WorldMineralCFGS cfg = this.getWorldMineral(mineralID);
		if(cfg == null || seq <= 0 || seq > cfg.refreshPos.size())
			return null;
		return new SBean.MapLocation(cfg.mapID, new SBean.Location(cfg.refreshPos.get(seq - 1), new SBean.Vector3F(1.0f, 0.0f, 0.0f)));
	}

	public int getMonsterDMGTOPropID(int race)
	{
		return this.monsterDamage.dmgTos.getOrDefault(race, -1);
	}
	
	public int getMonsterDMGBYPropID(int race)
	{
		return this.monsterDamage.dmgBys.getOrDefault(race, -1);
	}
	
	public boolean checkStateCanRebound(int stateID)
	{
		SBean.StateCFGS state = this.states.get(stateID);
		return state == null || state.canRebound == 1;
	}
	
	
//	public boolean checkExistPosition(long pos)
//	{
//		return this.clanCFGS.others.existPosition.containsKey(pos);
//	}
	
	public double getSpiritFix(int diff)
	{
		return getGradeFix(diff, this.spiritFactors.grades, this.spiritFactors.factors);
	}
	
	public double getWeaponFix(int diff)
	{
		return getGradeFix(diff, this.weaponFactors.grades, this.weaponFactors.factors);
	}
	
	public double getGradeFix(int diff, List<Integer> grades, List<Double> factors)
	{
		int min = 0;
		int max = grades.size() - 1;
		if(diff <= grades.get(min))
			return factors.get(min);
		
		if(diff >= grades.get(max))
			return factors.get(max);
		
		for(int i=0; i<grades.size(); i++)
		{
			if(diff == grades.get(i))
				return factors.get(i);
			
			if(diff > grades.get(i) && diff < grades.get(i + 1))
			{
				double factorDidd = factors.get(i + 1) - factors.get(i);
				double gradeDiff = grades.get(i + 1) - grades.get(i);
				return factorDidd/gradeDiff * (diff - grades.get(i)) + factors.get(i);
			}
		}
		
		return 0.0;
	}
	
	public SBean.HorseCFGS getHorseCFGS(int hid)
	{
		return this.horses.get(hid);
	}
	
	public SBean.HorseEnHanceLvlCommonCFGS getHorseDefaultEnHanceLvlCommonCFGS()
	{
		return this.horseEnhanceLvlcommons.get(0);
	}
	
	public static SBean.HorseGrowUpCGFS getHorseGrowUpCGFS(SBean.HorseCFGS horseCfg, int star)
	{
		if(star < 0 || star >= horseCfg.growups.size())
			return null;
		
		return horseCfg.growups.get(star);
	}
	
	public SBean.HorseEnHanceCFGS getEnHanceCFGS(int attrID)
	{
		return this.horseEnHances.get(attrID);
	}
	
	public SBean.HorseShowCFGS getHorseShowCFGS(int showID)
	{
		return this.horseShows.get(showID);
	}
	
	public SBean.Vector3 getHorseOffSet(int showID, int index)
	{
		SBean.HorseShowCFGS hsc = this.horseShows.get(showID);
		if(hsc == null || index < 0 || index >= hsc.offsets.size())
			return new SBean.Vector3();
		
		return hsc.offsets.get(index);
	}
	
	public SBean.HorseEnHanceDataCFGS getHorseEnHanceDataCFGS(SBean.HorseEnHanceCFGS enhanceCfg, int level)
	{
		if(level <= 0 || level > enhanceCfg.datas.size())
			return null;
		
		return enhanceCfg.datas.get(level - 1);
	}
	
	public SBean.HorseEnHanceAttrLineCFGS getHorseEnHanceAttrLineCFGS(int type, int level)
	{
		if (!horseEnhanceTypes.containsKey(type))
			return null;
		if (level <= 0 || level > horseEnhanceTypes.get(type).lines.size())
			return null;
		return horseEnhanceTypes.get(type).lines.get(level - 1);
	}
	
	public int getEnHanceRandomRank()
	{
		int rand = GameRandom.getRandom().nextInt(10001);
		for(int i=0; i<this.horseCommon.rankOdds.size(); i++)
		{
			if(rand <= this.horseCommon.rankOdds.get(i))
				return i + 1;
		}
		
		return 1;
	}
	
	public int getEnHanceRandomPropValue(SBean.HorseEnHanceDataCFGS dataCfg)
	{	
		int rank = GameData.getInstance().getEnHanceRandomRank();
		int base = rank - 1;
		return GameRandom.getRandInt(dataCfg.values.get(base), dataCfg.values.get(rank) + 1);
	}
	
	public SBean.HorseEnHanceLvlCFGS getHorseEnHanceLvlCFGS(int hid)
	{
		return this.horseEnhanceLvls.get(hid);
	}
	
	public static SBean.HorseEnHanceLvlDataCFGS getHorseEnHanceLvlDataCFGS(SBean.HorseEnHanceLvlCFGS lvlCfg, int level)
	{
		if(level <= 0 || level > lvlCfg.datas.size())
			return null;
		
		return lvlCfg.datas.get(level - 1);
	}
	
	public SBean.HorseSkillCFGS getHorseSkillCFGS(int skillID)
	{
		return this.horseSkills.get(skillID);
	}
	
	public SBean.HorseSkillUpdateDataCFGS getHorseSkillUpdateCFGS(int id)
	{
		return this.horseSkillUpdate.get(id);
	}
	
	public SBean.HorseSkillUpdateCFGS getHorseSkillLvlCFGS(int skillID, int lvl)
	{
		SBean.HorseSkillUpdateDataCFGS all = this.horseSkillUpdate.get(skillID);
		if(all == null || lvl <= 0 || lvl > all.data.size())
			return null;
		
		return all.data.get(lvl - 1);
	}
	
	public SBean.HorseEffectCFGS getHorseEffectCFGS(int skillID)
	{
		return this.horseEffectCfgs.get(skillID);
	}
	
	public static SBean.HorseEffectDataCFGS getHorseEffectDataCFGS(SBean.HorseEffectCFGS lvlCfg, int level)
	{
		if(level <= 0 || level > lvlCfg.datas.size())
			return null;
		
		return lvlCfg.datas.get(level - 1); 
	}
	
	public SBean.HorseEffectDataCFGS getHorseEffectDataCFGS(int hid, int skillLevel, int skillID)
	{
		SBean.HorseEnHanceLvlCFGS lvlCfg = this.getHorseEnHanceLvlCFGS(hid);
		if(lvlCfg == null)
			return null;
		
		SBean.HorseEffectCFGS effectCfg = GameData.getInstance().getHorseEffectCFGS(skillID);
		if(effectCfg == null)
			return null;
		
		if(skillLevel <= 0 || skillLevel > effectCfg.datas.size())
			return null;
		
		return effectCfg.datas.get(skillLevel - 1); 
	}
	
	public LevelExp getAddHorseEnHanceLvlExp(int curLvl, int curExp, int addExp, SBean.HorseEnHanceLvlCFGS cfg)
	{		
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for(;lvl <= cfg.datas.size(); ++lvl)
		{
			SBean.HorseEnHanceLvlDataCFGS dataCfg = cfg.datas.get(lvl - 1);
			if(exp >= dataCfg.needExp)
			{
				exp -= dataCfg.needExp;
				curLvl++;
				continue;
			}
			break;
		}
		
		if (curLvl >= cfg.datas.size())
		{
			curLvl = cfg.datas.size();
			exp = 0;
		}
		
		return new LevelExp(curLvl, exp);
	}
	
	public SBean.HorseCommonCFGS getHorseCommonCFGS()
	{
		return this.horseCommon;
	}
	
	
	public SBean.TreasureBaseCFGS getTreasureBaseCFGS()
	{
		return this.treasureBase;
	}
	
	public SBean.Clue getClueCFGS(SBean.ClueTreeCFGS clueTree, int index)
	{
		if(index <=0 || index > clueTree.clues.size())
			return null;
		
		return clueTree.clues.get(index - 1);
	}
	
	public SBean.TreasureMapCFGS getTreasureMapCFGS(int id)
	{
		return this.treasureMaps.get(id);
	}
	
	public static int getTreasureMapRewardGrade(SBean.TreasureMapCFGS mapCfg, int percent)
	{
		int grade = 0;
		for(int i=1; i<=mapCfg.rewards.size(); i++)
		{
			SBean.TreasureReward reward = mapCfg.rewards.get(i - 1);
			if(percent < reward.percent)
				return grade;
			
			grade = i;
		}
		
		return grade;
	}
	
	public SBean.TreasurePieceCFGS getTreasurePieceCFGS(int id)
	{
		return this.treasurePieces.get(id);
	}
	
	public SBean.InfoPointCFGS getPointCFGS(int id)
	{
		return this.infoPoints.get(id);
	}
	
	public SBean.MedalCFGS getMedalCFGS(int id)
	{
		return this.medals.get(id);
	}
	
	public Collection<SBean.TreasureNpcCFGS> getAllTreasureNpc()
	{
		return this.treasureNpcs.values();
	}
	
	public SBean.TreasureNpcCFGS getTreasureNpcCFGS(int id)
	{
		return this.treasureNpcs.get(id);
	}
	
	public SBean.NpcFameCFGS getNpcMaxFameCFGS()
	{
		return this.treasureBase.npcFames.get(this.treasureBase.npcFames.size() - 1);
	}
	
	public SBean.NpcFameCFGS getNpcFameCFGS(int fame)
	{
		for(SBean.NpcFameCFGS fameCfg:this.treasureBase.npcFames)
		{
			if(fame <= fameCfg.fameFloor)
				return fameCfg;
		}

		return this.treasureBase.npcFames.get(this.treasureBase.npcFames.size() - 1);
	}
	
	public Collection<SBean.PropertyCFGS> getAllPropertyCFGS()
	{
		return this.properties.values();
	}
	
	public SBean.PropertyCFGS getPropertyCFGS(int propID)
	{
		return this.properties.get(propID);
	}
	
	public SBean.FashionCFGS getFashionCFGS(int id)
	{
		return this.fashions.get(id);
	}
	
	public Set<Integer> getAllFashionTypes()
	{
		return this.allFashionTypes;
	}
	
	public SBean.SocialActionCFGS getSocialActionCFGS(int id)
	{
		return this.socialActions.get(id);
	}
	
	public SBean.SealBaseCFGS getSealBaseCFGS()
	{
		return this.sealBase;
	}
	
	public int getSealMaxGrade()
	{
		return this.sealGrades.size();
	}
	
	public SBean.SealGradeCFGS getSealGradeCFGS(int grade)
	{
		if(grade < 1 || grade > this.sealGrades.size())
			return null;
		
		return this.sealGrades.get(grade - 1);
	}
	
	public SBean.SealEnhanceBWCFGS getSealEnhanceBWCFGS(int classType, int bwType)
	{
		SBean.SealEnhanceTypeCFGS typeCfg = this.sealEnhanceTypes.get(classType);
		if(typeCfg == null)
			return null;
		
		return typeCfg.bw.get(bwType);
	}
	
	public List<SBean.SealEnhanceCFGS> getSealEnhanceCFGSs(int classType, int bwType, int enhanceCount)
	{
		SBean.SealEnhanceBWCFGS bwCfg = getSealEnhanceBWCFGS(classType, bwType);
		if(bwCfg == null)
			return emptyList();
		
		return getSealEnhanceCFGSs(bwCfg, enhanceCount);
	}
	
	public List<SBean.SealEnhanceCFGS> getSealEnhanceCFGSs(SBean.SealEnhanceBWCFGS bwCfg, int enhanceCount)
	{
		List<SBean.SealEnhanceCFGS> list = new ArrayList<>();
		for(SBean.SealEnhanceCFGS cfg: bwCfg.enhances)
		{
			if(cfg.enhanceReq > enhanceCount)
				break;
			
			list.add(cfg);
		}
		
		return list;
	}
	
	public Map<Integer, Integer> getSealEnhanceSkillProp(List<SBean.SealEnhanceCFGS> enhances, int enhanceCount)
	{
		int skillCount = enhances.size() > this.sealBase.skillCount ? this.sealBase.skillCount : enhances.size();
		int upLvl = 1;
		
		for(int i=0; i<this.sealBase.needEnhanceCnt.size(); i++)
		{
			if(enhanceCount < this.sealBase.needEnhanceCnt.get(i))
				break;
			
			upLvl = i+1;
		}

		Map<Integer, Integer> skillProp = new HashMap<>();
		for(int i=0; i<skillCount; i++)
		{
			SBean.SkillBriefCFGS skill = createSkillProp(enhances, upLvl);
			if(skill != null)
				skillProp.put(skill.id, skill.lvl);
		}
		
		return skillProp;
	}
	
	SBean.SkillBriefCFGS createSkillProp(List<SBean.SealEnhanceCFGS> enhances, int upLvl)
	{
		if(enhances.size() == 0)
			return null;
		
		float sum = 0;
		for(SBean.SealEnhanceCFGS e: enhances)
			sum += e.weight;
		
		float sp = 0;
		List<Float> weights = new ArrayList<>();

		for(SBean.SealEnhanceCFGS e: enhances)
		{
			sp += e.weight / sum;
			weights.add(sp);
		}
		
		float rnd = GameRandom.getRandom().nextFloat();
		int index = 0;
		for(int i=0; i<weights.size(); i++)
		{
			if(rnd <= weights.get(i))
			{
				index = i;
				break;
			}
		}
		
		return createSkillProp(enhances.remove(index), upLvl);
	}
	
	SBean.SkillBriefCFGS createSkillProp(SBean.SealEnhanceCFGS enhanceCfg, int upLvl)
	{
		upLvl = upLvl > enhanceCfg.lvlWeight.size() ? enhanceCfg.lvlWeight.size() : upLvl;
		float sum = 0;
		for(int weight: enhanceCfg.lvlWeight.subList(0, upLvl))
			sum += weight;
		
		float sp = 0;
		List<Float> weights = new ArrayList<>();
		for(int weight: enhanceCfg.lvlWeight.subList(0, upLvl))
		{
			sp += weight / sum;
			weights.add(sp);
		}
		
		int lvl = 1;
		float rnd = GameRandom.getRandom().nextFloat();
		for(int i=0; i<weights.size(); i++)
		{
			if(rnd <= weights.get(i))
			{
				lvl = i + 1;
				break;
			}
		}
		
		return new SBean.SkillBriefCFGS(enhanceCfg.skillID, lvl);
	}
	
	public Map<Integer, SBean.LeadGroupCFGS> getLeadGroupCFGS()
	{
		return this.leadGroups;
	}
	
	public SBean.ExpCoinBaseCFGS getExpCoinBaseCFGS()
	{
		return this.expCoinBase;
	}
	
	public SBean.RareBookCFGS getRareBookCFGS(int id, int lvl)
	{
		SBean.RareBookGroupCFGS gCfg = this.rarebookGroups.get(id);
		if(gCfg == null || lvl <= 0 || lvl > gCfg.books.size())
			return null;
		
		return gCfg.books.get(lvl - 1);
	}
	
	public Collection<Integer> getAllGraspGroups()
	{
		return this.graspGroups.keySet();
	}
	
	public SBean.GraspGroupCFGS getGraspGroupCFGS(int id)
	{
		return this.graspGroups.get(id);
	}
	
	public SBean.GraspCFGS getGraspCFGS(int id, int lvl)
	{
		SBean.GraspGroupCFGS gCfg = this.graspGroups.get(id);
		if(gCfg == null || lvl <=0 || lvl > gCfg.grasps.size())
			return null;
		
		return gCfg.grasps.get(lvl - 1);
	}
	
	public int getGraspDayFortune()
	{
		if(this.graspIDs.isEmpty())
			return 0;
		
		return this.graspIDs.get(GameRandom.getRandom().nextInt(this.graspIDs.size()));
	}

	public LevelExp getAddGraspLvlExp(int curLvl, int curExp, int addExp, SBean.GraspGroupCFGS groupCfg)
	{		
		int exp = curExp + addExp;
		int lvl = curLvl + 1;
		for(;lvl <= groupCfg.grasps.size(); ++lvl)
		{
			SBean.GraspCFGS cfg = groupCfg.grasps.get(lvl - 1);
			if(exp >= cfg.needExp)
			{
				exp -= cfg.needExp;
				curLvl++;
				continue;
			}
			break;
		}
		
		if (curLvl >= groupCfg.grasps.size())
		{
			curLvl = groupCfg.grasps.size();
			exp = 0;
		}
		
		return new LevelExp(curLvl, exp);
	}
	
	public SBean.DMGTransferCFGS getDMGTransferCFGS(int id, int level)
	{
		SBean.DMGTransferLevelCFGS lvlCfg = getDMGTransferLevelCFGS(id);
		if(lvlCfg == null)
			return null;
		
		return getDMGTransferCFGS(lvlCfg, level);
	}
	
	//乾坤点的等级（level）从0开始，不需要减1
	public SBean.DMGTransferCFGS getDMGTransferCFGS(SBean.DMGTransferLevelCFGS lvlCfg, int level)
	{
		if(level < 0 || level >= lvlCfg.levels.size())
			return null;
		
		return lvlCfg.levels.get(level);
	}
	
	public SBean.DMGTransferLevelCFGS getDMGTransferLevelCFGS(int id)
	{
		return dmgTransfers.get(id);
	}
	
	//超过配置的最大次数，取最大的次数
	public SBean.DMGTransferBuyCFGS getDMGTransferBuyCFGS(int index)
	{
		if(index <= 0)
			return null;
		
		index = index > dmgTransferBuys.size() ? dmgTransferBuys.size() : index;
		return dmgTransferBuys.get(index - 1);
	}
	
	public int getDMGTransferMaxBuyTimes()
	{
		return dmgTransferBuys.size();
	}
	
	public SBean.TitleCFGS getTitleCFGS(int id)
	{
		return this.titles.get(id);
	}
	
	public SBean.BetaActivityCFGS getBetaActivity()
	{
		return betaActivity;
	}
	
	public int getBetaActivityDay()
	{
		return betaActivity.dailyLogin.size();
	}
	
	public SBean.PetAchieveGroupCFGS getPetAchieveGroupCFGS(int type)
	{
		return this.petAchieveGroups.get(type);
	}
	
	public SBean.PetAchieveCFGS getPetAchieveCFGS(int id)
	{
		return this.petAchieves.get(id);
	}

	public SBean.PetLifeTaskCFGS getPetLifeTaskCFGS(int petid, int taskid)
	{
		if(taskid == 0) return null;
		SBean.PetLifeTaskGroupCFGS lifeTask = this.petLifeTask.get(petid);
		if(lifeTask == null || taskid > lifeTask.petLifeTasks.size()) return null;
		return lifeTask.petLifeTasks.get(taskid - 1);
	}
	
	public SBean.PetLifeTaskCFGS getPetLastLifeTaskCFGS(int petid)
	{
		SBean.PetLifeTaskGroupCFGS lifeTask = this.petLifeTask.get(petid);
		if(lifeTask == null || lifeTask.petLifeTasks.isEmpty()) return null;
		return lifeTask.petLifeTasks.get(lifeTask.petLifeTasks.size() - 1);
	}
	
	public Set<Integer> getAllUniqueSkillIDs()
	{
		return this.uniqueSkills.keySet();
	}
	
	public SBean.UniqueSkillCFG getUniqueSkillCFG(int id)
	{
		return this.uniqueSkills.get(id);
	}
	
	public boolean isUniqueSkill(int skillID)
	{
		return this.uniqueSkillIDs.contains(skillID);
	}
	
	//帮派运镖相关
	public SBean.SectDeliverCFGS getSectDeliverCfgs()
	{
		return this.sectDeliver;
	}
	
	public SBean.SectDeliverTask getSectDeliverTaskCfgs(int id)
	{
		if(id > this.sectDeliverTask.tasks.size() || id <= 0)
			return null;
		return this.sectDeliverTask.tasks.get(id - 1);
	}
	
	public SBean.SectDeliverVehicle getSectDeliverVehicleCfgs(int id)
	{
		if(id > this.sectDeliverVehicle.vehicles.size() || id <= 0)
			return null;
		return this.sectDeliverVehicle.vehicles.get(id - 1);
	}
	
	public SBean.SectDeliverRoute getSectDeliverRouteCfgs(int id)
	{
		if(id > this.sectDeliverRoute.deliverRoutes.size() || id <= 0)
			return null;
		return this.sectDeliverRoute.deliverRoutes.get(id - 1);
	}
	
	public SBean.SectDeliverWish getSectDeliverWishCfgs(int id)
	{
		if(id > this.sectDeliverWish.wishes.size() || id <= 0)
			return null;
		return this.sectDeliverWish.wishes.get(id - 1);
	}
	public int getSectDeliverTaskMaxId()
	{
		return this.sectDeliverTask.tasks.size();
	}
	
	public int getSectDeliverVehicleMaxId()
	{
		return this.sectDeliverVehicle.vehicles.size();
	}
	
	public int getSectDeliverRouteMaxId()
	{
		return this.sectDeliverRoute.deliverRoutes.size();
	}
	
	public int getSectDeliverWishMaxId()
	{
		return this.sectDeliverWish.wishes.size();
	}
	
	public List<SBean.SectDeliverTaskInfo> getSectDeliverTasks(int curtaskId)
	{
		int tasks = sectDeliverTask.tasks.size();
		List<SBean.SectDeliverTaskInfo> data = new ArrayList<>();
		List<Integer> dataids = new ArrayList<>();
		if (curtaskId > 0)
		{
			data.add(new SBean.SectDeliverTaskInfo(curtaskId, (byte) 1));
		}
		while (data.size() < GameData.SECT_DELIVER_TASK_SIZE)
		{
			int rand = getNextSectDeliverTask();
			if (!dataids.contains(rand)){
				data.add(new SBean.SectDeliverTaskInfo(rand, (byte) 0));
				dataids.add(rand);
			}
		}
		return data;
	}
	
	private int getNextSectDeliverTask()
	{
		int weightSum = 0;
		for (SBean.SectDeliverTask e : sectDeliverTask.tasks)
		{
			weightSum += e.weight;
		}
		int r = GameRandom.getRandom().nextInt(weightSum);
		int curSum = 0;
		SBean.SectDeliverTask dropcfg = sectDeliverTask.tasks.get(sectDeliverTask.tasks.size() - 1);
		Iterator<SBean.SectDeliverTask> it = sectDeliverTask.tasks.iterator();
		while (it.hasNext())
		{
			SBean.SectDeliverTask e = it.next();
			curSum += e.weight;
			if (r < curSum)
			{
				dropcfg = e;
				break;
			}
		}
		return dropcfg.id;
	}

	public SBean.DBSectDeliverWishChange getTempWishChange(int wishTimes, int exp, int money, int hp)
	{
		SBean.DBSectDeliverWishChange data = new SBean.DBSectDeliverWishChange();
		data.exp = exp;
		data.money = money;
		data.hp = hp;
		data.expTo = exp;
		data.moneyTo = money;
		data.hpTo = hp;
		
		if(this.sectDeliverWish.wishes.size() < 3 )
			return null;
		int randomtype = GameRandom.getRandInt(1, 7);
		if( wishTimes < 5)
		{
			if((randomtype & 1) > 0)
				data.expTo = GameRandom.getRandInt(exp, this.sectDeliverWish.wishes.get(0).first5max );
			if((randomtype & 1 << 1) > 0)
				data.moneyTo = GameRandom.getRandInt(money, this.sectDeliverWish.wishes.get(1).first5max);
			if((randomtype & 1 << 2) > 0)
				data.hpTo = GameRandom.getRandInt(hp, this.sectDeliverWish.wishes.get(2).first5max);
		}
		else if( 5 <= wishTimes && wishTimes < 10)
		{
			if((randomtype & 1) > 0)
			data.expTo = GameRandom.getRandInt(exp, this.sectDeliverWish.wishes.get(0).first10max );
			if((randomtype & 1 << 1) > 0)
			data.moneyTo = GameRandom.getRandInt(money, this.sectDeliverWish.wishes.get(1).first10max);
			if((randomtype & 1 << 2) > 0)
			data.hpTo = GameRandom.getRandInt(hp, this.sectDeliverWish.wishes.get(2).first10max);
		}
		else if(10 <= wishTimes && wishTimes < 15)
		{
			if((randomtype & 1) > 0)
			data.expTo = GameRandom.getRandInt(exp, this.sectDeliverWish.wishes.get(0).first15max );
			if((randomtype & 1 << 1) > 0)
			data.moneyTo = GameRandom.getRandInt(money, this.sectDeliverWish.wishes.get(1).first15max);
			if((randomtype & 1 << 2) > 0)
			data.hpTo = GameRandom.getRandInt(hp, this.sectDeliverWish.wishes.get(2).first15max);
		}
		else if(15 <= wishTimes && wishTimes < 30)
		{
			if((randomtype & 1) > 0)
			data.expTo = GameRandom.getRandInt(exp, this.sectDeliverWish.wishes.get(0).first30max );
			if((randomtype & 1 << 1) > 0)
			data.moneyTo = GameRandom.getRandInt(money, this.sectDeliverWish.wishes.get(1).first30max);
			if((randomtype & 1 << 2) > 0)
			data.hpTo = GameRandom.getRandInt(hp, this.sectDeliverWish.wishes.get(2).first30max);
		}
		else if(30 <= wishTimes && wishTimes < 60)
		{
			if((randomtype & 1) > 0)
			data.expTo = GameRandom.getRandInt(exp, this.sectDeliverWish.wishes.get(0).first60max );
			if((randomtype & 1 << 1) > 0)
			data.moneyTo = GameRandom.getRandInt(money, this.sectDeliverWish.wishes.get(1).first60max);
			if((randomtype & 1 << 2) > 0)
			data.hpTo = GameRandom.getRandInt(hp, this.sectDeliverWish.wishes.get(2).first60max);
		}
		else
		{
			if((randomtype & 1) > 0)
			data.expTo = GameRandom.getRandInt(exp, this.sectDeliverWish.wishes.get(0).maxValue );
			if((randomtype & 1 << 1) > 0)
			data.moneyTo = GameRandom.getRandInt(money, this.sectDeliverWish.wishes.get(1).maxValue);
			if((randomtype & 1 << 2) > 0)
			data.hpTo = GameRandom.getRandInt(hp, this.sectDeliverWish.wishes.get(2).maxValue);
		}
		return data;
	}

	public boolean checkSectDeliverNearMax(SBean.DBDeliverWishInSect info)
	{
		return info.exp >= this.sectDeliverWish.wishes.get(0).maxValue * 0.95 && info.money >= this.sectDeliverWish.wishes.get(1).maxValue * 0.95 && info.hp >= this.sectDeliverWish.wishes.get(2).maxValue * 0.95;
	}
	
	public int getSectDeliverRobPercent(int time, int beRobbedTimes, int taskId, int isProtect)
	{
		SBean.SectDeliverTask taskcfg = getSectDeliverTaskCfgs(taskId);
		if(taskcfg == null)
			return 0;
		int robPercent = 10000; //default value
		if (isProtect == 0)
		{
			if (beRobbedTimes == 1)
			{
				robPercent = taskcfg.firstRobLeft;
			}
			else if (beRobbedTimes == 2)
			{
				robPercent = taskcfg.secondRobLeft;
			}
			else if (beRobbedTimes == 3)
			{
				robPercent = taskcfg.thirdRobLeft;
			}
		}
		return robPercent;
	}
	
	public int getSectDeliverExp(int level, int taskid)
	{
		if(level > this.levels.size() || level <= 0 || taskid > sectDeliverTask.tasks.size() || taskid <= 0)
			return 0;
		return (int)Math.floor(sectDeliverTask.tasks.get(taskid - 1).rewardExpRate / 10000F * levels.get(level - 1).sectDeliverBaseExp);
	}

	public int getSectDeliverGold(int level, int taskid)
	{
		if(level > this.levels.size() || level <= 0 || taskid > sectDeliverTask.tasks.size() || taskid <= 0)
			return 0;
		return (int)Math.floor(sectDeliverTask.tasks.get(taskid - 1).rewardMoneyRate / 10000F * levels.get(level - 1).sectDeliverBaseGold);
	}
	
//////////////////////////////////////////////////////////////
	public static int getGlobalRoleID(int gsid, int rid)
	{
		return rid;
	}
	
	public static int getGameServerID(int grid)
	{
		return getZoneIdFromRoleId(grid); // 使用区内id, gsid的后三位，从1开始
	}
	
	public static int getRoleID(int grid)
	{
		return grid;
	}
	
	//替换除了宗门外，其它地方用到getGlobalRoleID函数的地方
	public static long getLongTypeValue(int high, int low)
	{
		return ((long)high << 32) | ((long)low & 0xffffffffL);
	}
	
	public static int getHighInt(long value)
	{
		return (int)((value >> 32) & 0xffffffffL);
	}
	
	public static int getLowInt(long value)
	{
		return (int)(value & 0xffffffffL);
	}
	
//////////////////////////////////////////////////////////////
	public static boolean checkRandom(int max, int odds)
	{
		return GameRandom.getRandom().nextInt(max) <= odds;
	}
	
	public static boolean checkRandom(int odds)
	{
		return GameRandom.getRandom().nextInt(10001) <= odds;
	}
	
	public static boolean checkRandom(float odds)
	{
		return GameRandom.getRandom().nextFloat() <= odds;
	}
	
	public static int getLastRefreshWeekDay(int nowDay, int weekDay, int refreshWeekDay)
	{
		return nowDay - (7 - refreshWeekDay + weekDay) % 7;
	}

	///////////支线任务/////////	
	public SBean.BranchTaskDataCFGS getBranchTaskCFG(int groupId, int id)
	{
		SBean.BranchTaskCFGS cfgs = this.branchTask.get(groupId);
		if (cfgs == null || id <= 0 || id > cfgs.datas.size())
			return null;
		return cfgs.datas.get(id-1);
	}
	
	public Map<Integer, SBean.BranchTaskCFGS> getBranchTask()
	{
		return this.branchTask;
	}
	
	
	/////////////日程表//////////
	
	public SBean.ScheduleDropCFGS getScheduleRewards( int sid ,int level)
	{
		SBean.ScheduleRewardsCFGS reward = this.scheduleRewards.get(sid);
		if(reward==null||reward.drop==null){
			return null;
		}
		int index = 0 ;
		for(int  im :reward.levels){
			if(level<im){
				break;
			}
			index++;
		}
		if(index>=reward.drop.size()){
			return null;
		}
		return reward.drop.get(index);
	}
	
	public int getScheduleActivity( int sid)
	{
		SBean.ScheduleRewardsCFGS reward = this.scheduleRewards.get(sid);
		if(reward==null||reward.drop==null){
			return -1;
		}
		return reward.activity;
	}
	
	public SBean.ScheduleDataCFGS getScheduleData( int sid)
	{
		for(SBean.ScheduleDataCFGS schedule : this.scheduleData.values()){
			if(schedule.type==sid){
				return schedule;
			}
		}
		
		return null;
	}
	
	public SBean.ScheduleDataCFGS getScheduleDatabyMapId(int type, int sid)
	{
		for(SBean.ScheduleDataCFGS scData :this.scheduleData.values()){
			if ((type == 0 || scData.type == type) && scData.mapid == sid)
			{
				return scData;
			}
		}
		
		return null;
	}
	
	////////////爬塔系统
	//层组配置总数据
	public Map<Integer, SBean.ClimbTowerFloorCFGS> getClimbTowerFloorDatas()
	{
		return this.climbTowerFloor;
	}
	
	//声望配置总数据
	public Map<Integer, SBean.ClimbTowerFameCFGS> getClimbTowerFameDatas()
	{
		return this.climbTowerFame;
	}
	
	//基础数据配置
	public SBean.ClimbTowerBaseDataCFGS getClimbTowerBaseData()
	{
		return this.climbTowerBaseData;
	}
	
	public SBean.ClimbTowerFloorDataCFGS getClimbTowerFloorDataCfg(int groupId, int floor)
	{
		if (!this.climbTowerFloor.containsKey(groupId))
			return null;
		return this.climbTowerFloor.get(groupId).datas.get(floor);
	}
	
	public SBean.ClimbTowerFameDataCFGS getClimbTowerFameDataCfg(int groupId, int level)
	{
		SBean.ClimbTowerFameCFGS fameDatas = this.climbTowerFame.get(groupId);
		if (fameDatas == null)
			return null;
		return fameDatas.datas.get(level);
	}
	
	public SBean.SecretAreaTaskCFGS getSecretTaskCfg(int id)
	{
		if (id > this.secretAreaData.size() || id < 1)
			return null;
		return this.secretAreaData.get(id - 1);
	}
	
	public SBean.RemainActivityCFGS getRemainActivityCFGS(int id)
	{
		if(id <= 0 || id > this.rmActivitys.size())
			return null;
		
		return this.rmActivitys.get(id - 1);
	}
	
	//OB=(xcosα-ysinα,xsiaα+ycosα) --> OB=(zsiaα+xcosα, 0 , zcosα-xsinα)
	public static SBean.Location createRandomLocation(int radius, float angle, final SBean.Vector3 pos, final SBean.Vector3F dir)
	{
		float rda = GameRandom.getRandFloat(-angle, angle);
		SBean.Vector3F rdd = new SBean.Vector3F((float)(dir.x * Math.cos(rda) - dir.z * Math.sin(rda)), 0, (float)(dir.x * Math.sin(rda) + dir.z * Math.cos(rda)));
//		SBean.Vector3F rdd = new SBean.Vector3F((float)(dir.z * Math.sin(rda) + dir.x * Math.cos(rda)), 0, (float)(dir.z * Math.cos(rda) - dir.x * Math.sin(rda)));
		SBean.Vector3 target = new SBean.Vector3((int)(pos.x + rdd.x * radius), pos.y ,(int)(pos.z + rdd.z * radius));
		return new SBean.Location(target, new GVector3(pos).diffence2D(new GVector3(target)).normalize().toVector3F());
	}
	
//////////////////////////////////////////////////////////////
//生成跑马灯正文
	public SBean.RollNoticeCFGS getRollNoticeCFGS()
	{
		return rollNotice;
	}
	
	public SBean.ComposeCFGS getComposeCFGS(int id)
	{
		return composes.size() < id || id < 1 ? null : composes.get(id - 1);
	}
	
	public SBean.MessageBoardCFGS getMessageBoardCFGS(int side, int id)
	{
		return !messageBoards.containsKey(side) || messageBoards.get(side).messageBoards.size() < id || id < 1 ? null : messageBoards.get(side).messageBoards.get(id - 1);
	}
	
	public SBean.MessageBoardCommonCFGS getMessageBoardCommonCFGS()
	{
		return messageBoardCommon;
	}

	public int getArmorTypeSize()
	{
		return armor.armorType.size();
	}

	public SBean.ArmorSlotGroupCFGS getSoltGroupCFGS(int armorType)
	{
		return armor.armorSlotGroup.get(armorType);
	}

	public SBean.ArmorTypeCFGS getArmorTypeCFGS(int type)
	{
		return type <= 0 || type > armor.armorType.size() ? null : armor.armorType.get(type - 1);
	}

	public SBean.ArmorRankCFGS getArmorRankCFGS(int type, int rank)
	{
		SBean.ArmorRankGroupCFGS group = armor.armorRankGroup.get(type);
		if (group ==null)
		{
			return null;
		}
		return rank <= 0 || rank > group.armorRanks.size() ? null : group.armorRanks.get(rank - 1);
	}

	public int getArmorRankSize(int type)
	{
		SBean.ArmorRankGroupCFGS group = armor.armorRankGroup.get(type);
		if (group ==null)
		{
			return 0;
		}
		return group.armorRanks.size();
	}

	public boolean tryArmorUpRank(int totalFeats, SBean.ArmorData theArmor)
	{
		SBean.ArmorRankCFGS cfg = getArmorRankCFGS(theArmor.id, theArmor.rank);
		if(cfg==null)
		return false;
		if(theArmor.curUpRankTime<=cfg.mustFailTime)
			return false;
		if (theArmor.curUpRankTime >= cfg.mustSuccessTime)
			return true;
		int successRate = getSuccessRate(totalFeats, theArmor);
		if (GameRandom.getRandInt(0, 10000) < successRate)
			return true;
		return false;
	}

	private int getSuccessRate(int totalFeats, SBean.ArmorData theArmor)
	{
		SBean.ArmorRankCFGS cfg = getArmorRankCFGS(theArmor.id, theArmor.rank);
		int wishPoint = 0;
		for(SBean.FeatAddWishPointCFGS i:armor.featAddWishPoint)
		{
			if(i.feat > wishPoint)
				break;
			wishPoint = i.startWishPoint;
		}
		wishPoint = Math.round(wishPoint > cfg.maxWishPoint / 10000f * armor.armorCommon.wishPointMaxRate ? cfg.maxWishPoint / 10000f * armor.armorCommon.wishPointMaxRate : wishPoint + theArmor.wishPoint);
		return wishPoint * cfg.addRatePerWishPoint;
	}
	
	public SBean.ArmorLevelCFGS getArmorLevelCFGS(int type, int level)
	{
		SBean.ArmorLevelGroupCFGS levelGroup = armor.armorLevelGroup.get(type);
		if (levelGroup == null)
			return null;
		return level <= 0 || level > levelGroup.armorLevels.size() ? null : levelGroup.armorLevels.get(level - 1);
	}

	public SBean.ArmorTalentCFGS getArmorTalentCFGS(int type, int talentId)
	{
		SBean.ArmorTalentGroupCFGS talentGroup = armor.armorTalentGroup.get(type);
		if (talentGroup == null)
			return null;
		return talentId <= 0 || talentId > talentGroup.armorTalents.size() ? null : talentGroup.armorTalents.get(talentId - 1);
	}

	public void updateArmorLevel(SBean.ArmorData armorData)
	{
		SBean.ArmorLevelGroupCFGS levelGroup = armor.armorLevelGroup.get(armorData.id);
		if (levelGroup == null)
			return;
		SBean.ArmorLevelCFGS levelCFGS = levelGroup.armorLevels.get(armorData.level - 1);
		if (levelCFGS == null)
			return;
		int expsum = levelCFGS.needExp + armorData.exp;
		int level = armorData.level;
		for (; level < levelGroup.armorLevels.size(); level++)
		{
			if (levelGroup.armorLevels.get(level).needExp > expsum)
				break;
		}
		armorData.level = level;
		if(armorData.level == levelGroup.armorLevels.size())
			armorData.exp = 0;
		else
			armorData.exp = expsum - levelGroup.armorLevels.get(level - 1).needExp;
	}
	
	public boolean isRune(int id)
	{
		return armor.rune.containsKey(id > 0 ? id : -id);
	}

	public SBean.DummyGoods getCurResetUseItem(int curResetTalentTimes)
	{
		int itemnumsize = armor.armorCommon.refreshItemNum.size();
		return new SBean.DummyGoods(armor.armorCommon.refreshItemId, curResetTalentTimes >= itemnumsize ? armor.armorCommon.refreshItemNum.get(itemnumsize - 1) : armor.armorCommon.refreshItemNum.get(curResetTalentTimes));
	}

	public SBean.ArmorSlotCFGS getArmorSlotCFGS(int type, int soltGroupIndex)
	{
		SBean.ArmorSlotGroupCFGS slotGroup = armor.armorSlotGroup.get(type);
		if (slotGroup == null)
			return null;
		return soltGroupIndex <= 0 || soltGroupIndex > slotGroup.armorSlots.size() ? null : slotGroup.armorSlots.get(soltGroupIndex - 1);
	}

	public SBean.RuneCFGS getRuneCFGS(int runeId)
	{
		return this.armor.rune.get(runeId > 0 ? runeId : -runeId);
	}

	public SBean.RuneLangCFGS getRuneLangCFGS(int langId)
	{
		return langId <= 0 || langId > this.armor.runeLang.size() ? null : this.armor.runeLang.get(langId - 1);
	}

	public SBean.ArmorCommonCFGS getArmorCommonCFGS()
	{
		return this.armor.armorCommon;
	}

	public List<SBean.DummyGoods> getRuneWishDrops(int wishPoint)
	{
		SBean.RuneTransmigrateCFGS curdrop = null;
		for (SBean.RuneTransmigrateCFGS wishdrop : this.armor.runeTransmigrate)
		{
			if (wishPoint >= wishdrop.transmigrate)
			{
				curdrop = wishdrop;
			}
			else 
			{
				break;
			}
		}
		if (curdrop == null)
			curdrop = this.armor.runeTransmigrate.get(this.armor.runeTransmigrate.size() - 1);
		return getRandomDrop(curdrop.dropId, GameRandom.getRandInt(curdrop.minDropNum, curdrop.maxDropNum), 1);
	}
	
	public SBean.RuneLangCFGS getRuneLangCFGS(List<Integer> runes)
	{
		Set<Integer> realRunes = new HashSet<>();
		for(int rune: runes)
		{
			if(rune == 0)
				return null;
			
			realRunes.add(rune > 0 ? rune : -rune);
		}
		
		for(SBean.RuneLangCFGS e: this.armor.runeLang)
		{
			if(realRunes.size() != e.runes.size())
				return null;
			
			if(realRunes.containsAll(e.runes))
				return e;
		}
		
		return null;
	}
	
	//reward <itemID, count>
	public Map<Integer, Integer> filterReward(Map<Integer, Integer> reward, int needCount)
	{
		if(reward.size() <= needCount)
			return reward;
		
		TreeMap<Long, Integer> rank = new TreeMap<>();			//<(long)rank--itemID, (int)count>
		for(Map.Entry<Integer, Integer> e: reward.entrySet())
			rank.put(getLongTypeValue(GameData.getInstance().getItemRank(e.getKey()), e.getKey()), e.getValue());
		
		return rank.descendingMap().entrySet().stream().limit(needCount).collect(Collectors.toMap(e -> getLowInt(e.getKey()), e -> e.getValue()));
	}
	
	public boolean isTrigTaskAlter(List<Integer> trigIDs)
	{
		for(int trigID: trigIDs)
		{
			SBean.SceneTrigCFGS stc = this.sceneTrigs.get(trigID);
			if(stc != null && stc.trigBehavior == SCENE_TRIG_BEHAVIOR_TASK_ALTER)
				return true;
		}
		
		return false;
	}
	
	public SBean.SceneTrigCFGS getSceneTrigCFGS(int trigID)
	{
		return this.sceneTrigs.get(trigID);
	}
	
	public SBean.SceneSpawnPointCFGS getSceneSpawnPointCFGS(int pointID)
	{
		return this.sceneSpawnPoints.get(pointID);
	}

	public Iterator<Integer> getRollNoticeLevel(int eqGrowLvl, int level)
	{
		return rollNotice.equipStrengthens.stream().filter(item -> item > eqGrowLvl && item <= level).iterator();
	}
	
	public SBean.MarriageCFGS getMarriageCFGS()
	{
		return this.marriage;
	}
	
	public SBean.MarriageBaseCFGS getMarriageBaseCFGS()
	{
		return marriage.marriageCommon;
	}
	
	public SBean.MarriageBespeakTimeCFGS getMarriageBespeakCFGS(int index)
	{
		return index <= 0 || index > marriage.marriageBespeakTimes.size() ? null : marriage.marriageBespeakTimes.get(index - 1);
	}

	public SBean.MarriageGradeCFGS getMarriageGradeCFGS(int grade)
	{
		return grade <= 0 || grade > marriage.marriageGrade.size() ? null : marriage.marriageGrade.get(grade - 1);
	}

	public Map<Integer, SBean.MarriageSkillGroupCFGS> getMarriageSkillCFGS()
	{
		return marriage.marriageSkills;
	}

	public SBean.MarriageSkillGroupCFGS getMarriageSkillGroupCFGS(int skillID)
	{
		return marriage.marriageSkills.get(skillID);
	}
	
	public static SBean.MarriageSkillCFGS getMarriageSkillCFGS(SBean.MarriageSkillGroupCFGS group, int skillLvl)
	{
		if(skillLvl < 1 || skillLvl > group.skill.size())
			return null;
		
		return group.skill.get(skillLvl - 1);
	}
	
	public Map<Integer, SBean.MarriageSkillInfo> initMarriageSkill(int grade)
	{
		Map<Integer, SBean.MarriageSkillInfo> skill = new HashMap<>();
		int skillLevel = getMarriageGradeCFGS(grade) == null ? 1 : getMarriageGradeCFGS(grade).skillLevel;
		marriage.marriageSkills.entrySet().forEach(skillGroup -> {
			skill.put(skillGroup.getKey(), new SBean.MarriageSkillInfo(skillGroup.getKey(), Math.min(skillGroup.getValue().skill.size(), skillLevel), 0));
		});
		return skill;
	}

	public SBean.MarriageLineCFGS getMarriageLineCFGS(int lineID)
	{
		return this.marriage.lines.get(lineID);
	}
	
	public SBean.Vector3 getMarriageLinePos(int lineID, int index)
	{
		SBean.MarriageLineCFGS line = getMarriageLineCFGS(lineID);
		if(line == null || index < 0 || index >= line.points.size())
			return null;
			
		return line.points.get(index);
	}
	
	public SBean.MarriageCarCFGS getMarriageCarCFGS(int carID)
	{
		return marriage.cars.get(carID);
	}
	
	public SBean.MarriageBanquetCFGS getMarriageBanquetCFGS(int banquet)
	{
		return marriage.banquets.get(banquet);
	}
	
	public int getMarriageBanquetMap()
	{
		return marriage.banquetBase.mapID;
	}
	
	public SBean.PosEntity getMarriageMineral(int id)
	{
		return marriage.banquetBase.minerals.get(id);
	}
	
	public SBean.PosEntity getMarriageMonster(int id)
	{
		return marriage.banquetBase.monsters.get(id);
	}
	
	public SBean.ExchangeCFGS getExchangeCFGS(int exchangeId)
	{
		return exchangeId <= 0 || exchangeId > exchange.size() ? null : exchange.get(exchangeId - 1);
	}

	public int getMarriageSkillLevelUpTimes(int skillId, int skillLevel)
	{
		SBean.MarriageSkillGroupCFGS group = marriage.marriageSkills.get(skillId);
		if (group == null)
			return 0;
		if (skillLevel <= 0 || skillLevel >= group.skill.size())
			return 0;
		return group.skill.get(skillLevel).levelUpTimes;
	}

	public int getMarriageSkillMaxLevel(int skillId)
	{
		SBean.MarriageSkillGroupCFGS group = marriage.marriageSkills.get(skillId);
		if (group == null)
			return 0;
		return group.skill.size();
	}
	
	public SBean.MarriageAttributeCFGS getMarriageAttrCFGS(int level)
	{
		if(level <= 0 || level > marriage.marriageAttributes.size())
			return null;
		
		return marriage.marriageAttributes.get(level - 1);
	}
	
	public int computeLevel(int skillId, SBean.MarriageSkillInfo skill, int levelupTimes, int marriageLevel)
	{
		SBean.MarriageSkillGroupCFGS group = marriage.marriageSkills.get(skillId);
		if (group == null || skill.skillLevel >= group.skill.size())
			return GameData.PROTOCOL_OP_FAILED;
		int levelTimes = 0;
		if (skill.skillUpTimes > group.skill.get(skill.skillLevel).levelUpTimes)
		{
			skill.skillLevel++;
			skill.skillUpTimes = 0;
		}
		for (int i = skill.skillLevel; i <= group.skill.size(); i++)
		{
			SBean.MarriageSkillCFGS levelCfgs = group.skill.get(i - 1);
			if (levelCfgs.marriageLevelNeed > marriageLevel)
				return GameData.PROTOCOL_OP_MARRIAGE_MARRIAGE_LEVEL_NEED;
			if (i == group.skill.size())
				return skill.skillLevel << 16;
			if (skill.skillUpTimes + levelupTimes < getMarriageSkillLevelUpTimes(skillId, skill.skillLevel))
				return skill.skillLevel << 16 | (skill.skillUpTimes + levelupTimes);
			else
			{
				levelupTimes -= getMarriageSkillLevelUpTimes(skillId, skill.skillLevel) - skill.skillUpTimes;
				skill.skillLevel++;
				skill.skillUpTimes = 0;
			}
		}
		return levelTimes;
	}
	
	public SBean.SectGroupMapCFGS getSectGroupMapCFGS(int mapId)
	{
		return sectGroupMaps.get(mapId);
	}
	
	public int getSectGroupMapLevel(int id)
	{
		SBean.SectGroupMapCFGS cfgs = this.sectGroupMaps.get(id);
		if (cfgs == null)
		{
			return 0;
		}
		int level = 1;
		while (cfgs.preMapId != -1)
		{
			level ++;
			cfgs = this.sectGroupMaps.get(cfgs.preMapId);
		}
		return level;
	}

	public SBean.SectGroupMapPersonRewardCFGS getSectGroupMapRewardCFGS(int mapId)
	{
		return sectGroupMapRankRewards.get(mapId);
	}

	public int getLastVitTaskTime(int time)
	{
		int lasttime = 0;
		int daytime = GameTime.getSecondOfDay(time);
		for (int i = 0; i < 3; i++ )
		{
			int dailyVitId = GameData.DAILY_TASK_ID_RECEIVE_VIT_1 + i;
			SBean.DailyTaskCFGS vitTask = this.dailyActivities.get(dailyVitId);
			if (i == 0 && daytime <= vitTask.rewardEndTime)
				return time - daytime + this.dailyActivities.get(DAILY_TASK_ID_RECEIVE_VIT_3).rewardEndTime - GameTime.getDayTimeSpan();
			if (daytime > vitTask.rewardEndTime)
				lasttime = vitTask.rewardEndTime;
			else
				return time - daytime + lasttime;
		}
		return time - daytime + lasttime;
	}

	public int getNextVitTaskTime(int time)
	{
		int daytime = GameTime.getSecondOfDay(time);
		int day = GameTime.getDay(time);
		for (int i = 0; i < 3; i++)
		{
			if (daytime < this.dailyActivities.get(DAILY_TASK_ID_RECEIVE_VIT_1 + i).rewardEndTime)
			{
				return GameTime.getDayTime(day, this.dailyActivities.get(DAILY_TASK_ID_RECEIVE_VIT_1 + i).rewardEndTime);
			}
		}
		return GameTime.getDayTime(day + 1, this.dailyActivities.get(DAILY_TASK_ID_RECEIVE_VIT_1).rewardEndTime);
	}

	public SBean.WeaponTalentCommonCFGS getWeaponTalentCommonCFGS()
	{
		return this.weaponTalent;
	}

	public SBean.FlagBattleCFGS getFlagBattleCFGS()
	{
		return this.flagBattle;
	}

	public List<SBean.DailyTaskCFGS> getDailyVits()
	{
		return dailyActivities.values().stream().filter(activity -> activity.id == DAILY_TASK_ID_RECEIVE_VIT_1 || activity.id == DAILY_TASK_ID_RECEIVE_VIT_2 || activity.id == DAILY_TASK_ID_RECEIVE_VIT_3).collect(Collectors.toList());
	}

	public int getMonsterPercent(int mapId, int id)
	{
		if (!activityMapProcess.containsKey(mapId))
			return 0;
		return activityMapProcess.get(mapId).monsterProcess.getOrDefault(id, 0);
	}

	public SBean.HeirloomCFGS getHeirloomCFGS()
	{
		return heirloom;
	}

	public List<SBean.AttrCFGS> getHeirloomProps(int perfectDegree)
	{
		List<SBean.AttrCFGS> props = new ArrayList<>();
		for (int i = 0;i< heirloom.properties.size();i++)
		{
			if (perfectDegree < heirloom.properties.get(i).perfectDegree)
				break;
			props = heirloom.properties.get(i).properties;
		}
		return props;
	}

	public List<SBean.OfflineWizardFuncCFGS> getOfflineWizardLevelCFGS()
	{
		return this.offlineExp.levelFunc;
	}

	public SBean.OfflineWizardFuncCFGS getOfflineWizardLevelCFGS(int level)
	{
		return level <= 0 || level > this.offlineExp.levelFunc.size() ? null : this.offlineExp.levelFunc.get(level - 1);
	}

	public SBean.MonsterCFGS getOfflineWizardMonsterCFGS(int level)
	{
		return level <= 0 || level > this.offlineExp.levelMonster.size() ? null : getMonsterCFGS(this.offlineExp.levelMonster.get(level - 1));
	}
	
	public SBean.LevelLimitCFGS getLevelLimit()
	{
		return this.levelLimit;
	}
	
	public float getSpeedUpAdd(int diff)
	{
		int size = this.levelLimit.speedUp.adds.size();
		if(size == 0 || diff < this.levelLimit.speedUp.adds.get(0).diff)
			return 0;
		
		for(int i = size - 1; i >= 0; i--)
		{
			SBean.SpeedUpAdd s = this.levelLimit.speedUp.adds.get(i);
			if(diff >= s.diff)
				return s.add;
		}
		
		return 0;
	}
	
	public boolean isLevelLimit(int curLevel, long curExp)
	{
		if(curLevel < this.levelLimit.base.limitLvl)
			return false;
		
		int nextLevel = curLevel + 1;
		if(nextLevel <= 0 || nextLevel > this.levels.size())
			return false;
		
		SBean.LevelCFGS next = this.levels.get(nextLevel - 1);
		return curExp >= next.exp;
	}
	
	public int getSpeedUpLvl(int rankSpeedUpLvl, int openDays)
	{
		int size = this.levelLimit.speedUp.lvls.size();
		if(openDays < size && openDays >= 0)
			return this.levelLimit.speedUp.lvls.get(openDays);
			
		return rankSpeedUpLvl > this.levelLimit.speedUp.lvls.get(size - 1) ? rankSpeedUpLvl : this.levelLimit.speedUp.lvls.get(size - 1);
	}
	
	public int testInBespeak(SBean.DBMarriageBespeak bespeak, int time)
	{
		if (bespeak == null)
			return PROTOCOL_OP_MARRIAGE_NO_BESPEAK;
		if (bespeak.timeIndex <= 0 || bespeak.timeIndex > marriage.marriageBespeakTimes.size())
			return PROTOCOL_OP_FAILED;
		int daytime = GameTime.getSecondOfDay(time);
		if (daytime < marriage.marriageBespeakTimes.get(bespeak.timeIndex - 1).time.startTime)
			return PROTOCOL_OP_MARRIAGE_BEFORE_BESPEAK_TIME;
		if (daytime > marriage.marriageBespeakTimes.get(bespeak.timeIndex - 1).time.endTime)
			return PROTOCOL_OP_MARRIAGE_TIME_IS_TOO_LATE;
		return PROTOCOL_OP_SUCCESS;
	}
	
	public int getMarriageEndTime(int time)
	{
		int dayTime = GameTime.getDayTime(GameTime.getDay(time), 0);
		List<SBean.MarriageBespeakTimeCFGS> timeSpans = this.marriage.marriageBespeakTimes.stream().filter(timeSpan -> dayTime + timeSpan.time.startTime < time && dayTime + timeSpan.time.endTime >= time).collect(Collectors.toList());
		if (timeSpans.size() != 1)
			return 0;
		return dayTime + timeSpans.get(0).time.endTime;
	}
	
	public boolean testTimeCanBespeak(int timeIndex)
	{
		return timeIndex > 0 && timeIndex <= this.marriage.marriageBespeakTimes.size() && GameTime.getSecondOfDay() < this.marriage.marriageBespeakTimes.get(timeIndex - 1).time.startTime;
	}
	
	public int getActivityChallengeType(int type, int arg)
	{
		for (int i = 0; i < activityChallengeType.size(); i++)
		{
			if (activityChallengeType.get(i).type == type && activityChallengeType.get(i).arg == arg)
				return activityChallengeType.get(i).id;
		}
		return 0;
	}
	
	public static List<SBean.DummyGoods> getActivityChallengeTimeReward(GameConf.ActivityChallengeGiftConfig cfg, int activityId, int times)
	{
		List<SBean.ChallengeGift> gifts = cfg.getInnerConfig().gifts.stream().filter(gift -> gift.id == activityId).collect(Collectors.toList());
		if (gifts.size() != 1)
			return null;
		List<SBean.ChallengeTimeGift> list = gifts.get(0).gifts.stream().filter(gift -> gift.times == times).collect(Collectors.toList());
		if (list.size() != 1)
			return null;
		return list.get(0).gifts;
	}
	
	public int getIdleNoticeInterval(int type, int secondOfDay)
	{
		SBean.RobotBroadcastTimeCFGS t = getIdleNoticeInterval(this.robot.common.broadcastTime, secondOfDay);
		if(t == null)
			return -1;
		
		switch (type)
		{
		case GameData.ROLLNOTICE_TYPE_TRANSFER:
			return GameRandom.getRandInt(t.transform.min, t.transform.max);
		case GameData.ROLLNOTICE_TYPE_WIN_HIGH_SCORE:
			return GameRandom.getRandInt(t.arena.min, t.arena.max);
		default:
			break;
		}
		
		return -1;
	}
	
	public SBean.RobotBroadcastTimeCFGS getIdleNoticeInterval(List<SBean.RobotBroadcastTimeCFGS> times, int secondOfDay)
	{
		if(times.isEmpty())
			return null;
		
		for(SBean.RobotBroadcastTimeCFGS t: times)
		{
			if(secondOfDay <= t.floor)
				return t;
		}
		
		return times.get(times.size() - 1);
	}
	
	public String getIdleNoticeContext(int type)
	{
		switch (type)
		{
		case GameData.ROLLNOTICE_TYPE_TRANSFER:
			return getTransNoticeContext();
		case GameData.ROLLNOTICE_TYPE_WIN_HIGH_SCORE:
			return getArenaNoticeContext();
		default:
			break;
		}
		
		return null;
	}
	
	public String getTransNoticeContext()
	{
		String name = randRobotName();
		if(name == null)
			return null;
		
		int tlvl = 1;
		int bwType = 0;
		int classType = GameRandom.getRandInt(1, 6);
		return name + "|" + bwType + "|" + classType + "|" + tlvl;
	}
	
	public String getArenaNoticeContext()
	{
		String attacker = randRobotName();
		String defender = randRobotName();
		if(attacker == null || defender == null)
			return null;
		
		int newRank = GameRandom.getRandInt(robot.common.arenaRank.min, robot.common.arenaRank.max);
		return attacker + "|" + defender + "|" + newRank;
	}
	
	public String randRobotName()
	{
		if(robot.surNames.isEmpty() || robot.overviews.isEmpty())
			return null;
		
		return robot.surNames.get(GameRandom.getRandInt(0, robot.surNames.size())) + robot.overviews.get(GameRandom.getRandInt(0, robot.overviews.size())).name;
	}
	
	public String randRobotSurName()
	{
		if(robot.surNames.isEmpty())
			return null;
		
		return robot.surNames.get(GameRandom.getRandInt(0, robot.surNames.size()));
	}
	
	public SBean.RobotOverviewCFGS randRobotOverview()
	{
		if(robot.overviews.isEmpty())
			return null;
		
		return robot.overviews.get(GameRandom.getRandInt(0, robot.overviews.size()));
	}
	
	public int getRobotOpenDayByType(int type)
	{
		return robot.common.openDays.getOrDefault(type, -1);
	}

	public boolean isFullStar(SBean.DBWeapon weapon)
	{
		return weapon != null && getWeaponCFGS(weapon.id) != null && weapon.star == getWeaponCFGS(weapon.id).weaponStar.size() - 1;
	}
	
	public SBean.EquipStarAddPropCFGS getEquipStartAddPropCFGS(int part)
	{
		if (part <= 0 || part > this.starAdditionProp.size())
			return null;
		
		return this.starAdditionProp.get(part - 1);
	}
	
	public SBean.SteleCFGS getSteleCFGS()
	{
		return stele;
	}
	
	public boolean checkSteleInOpenTime(int now)
	{
		int weekDay = GameTime.getWeekdayByOffset(now, GameData.GAME_DAY_REFRESH_TIME * 3600);
		if(!stele.base.openDays.contains(weekDay))
			return false;
		
		return checkInOpenTime(stele.base.startTime, stele.base.lastTime);
	}
	
	public SBean.SteleMineralTypeCFGS getSteleMineralTypeCFGS(int type)
	{
		if(type <= 0 || type > stele.mineralTypes.size())
			return null;
		
		return stele.mineralTypes.get(type - 1);
	}
	
	public int randSteleMineralType()
	{
		int size = stele.mineralTypes.size();
		if(size == 0)
			return 0;
		
		return GameRandom.getRandInt(0, size) + 1;
	}
	
	public static SBean.SteleMineralCFGS getSteleMineralCFGS(SBean.SteleMineralTypeCFGS typeCfg, int index)
	{
		if(index <= 0 || index > typeCfg.minerals.size())
			return null;
		
		return typeCfg.minerals.get(index - 1);
	}
	
	public SBean.SteleRewardCFGS getSteleRankReward(int rank)
	{
		for(SBean.SteleRewardCFGS e: stele.rankRewards)
		{
			if(rank <= e.floor)
				return e;
		}
		
		return null;
	}
	

	public SBean.SteleLevelMonsterCFGS getSteleLevelMonsterCFGS(int level)
	{
		for (SBean.SteleLevelMonsterCFGS e : stele.lvlMonsters)
		{
			if (level <= e.level)
				return e;
		}

		return stele.lvlMonsters.isEmpty() ? null : stele.lvlMonsters.get(stele.lvlMonsters.size() - 1);
	}

	public static SBean.SteleMonsterCFGS getSteleMonsterCFGS(SBean.SteleLevelMonsterCFGS lvlMonster)
	{
		float odds = GameRandom.getRandFloat(0, 1);
		for (SBean.SteleMonsterCFGS m : lvlMonster.monsters)
		{
			if (odds < m.weight)
				return m;
		}

		return lvlMonster.monsters.isEmpty() ? null : lvlMonster.monsters.get(lvlMonster.monsters.size() - 1);
	}

	public static int getSectMapDamageReward(List<SBean.FactorCFGS> rewardFactor, int damage)
	{
		int coin = 0;
		int lastMaxHp = 0;
		for (SBean.FactorCFGS factor : rewardFactor)
		{
			if (damage < factor.maxHp)
				return (int) ((damage - lastMaxHp) * factor.factor + coin);
			else
			{
				coin += (factor.maxHp - lastMaxHp) * factor.factor;
				lastMaxHp = factor.maxHp;
			}
		}
		return coin;
	}

	public static boolean isInDay(Collection<Integer> openDay)
	{
		int weekDay = GameTime.getWeekdayByOffset(GameTime.getTime(), GameData.GAME_DAY_REFRESH_TIME * 3600);
		if(openDay.contains(weekDay))
			return true;
		return false;
	}

	public boolean isInJusticeDay()
	{
		return isInDay(this.justice.openDay);
	}
	
	public int getNextJusticeNpcEndTime(int time)
	{
		for (SBean.TimeSpan openTime : justice.openTimes)
		{
			int daytime = GameTime.getSecondOfDay(time);
			int day = GameTime.getDay(time);
			if (daytime < openTime.startTime)
				return 0;
			if (daytime < openTime.endTime)
				return GameTime.getDayTime(day, openTime.endTime);
		}
		return 0;
	}

	public int getJusticeNpcPointSize()
	{
		return this.justice.npcPoints.size();
	}
	
	public SBean.MapLocation getJusticeNpcPoint(int index)
	{
		return index < 0 || index > this.justice.npcPoints.size() ? null : this.justice.npcPoints.get(index);
	}

	public SBean.JusticeMapCFGS getJusticeMapCFGS()
	{
		return this.justice;
	}

	public SBean.JusticeMapCopyCFGS getJusticeMapCopyCFGS(int mapID)
	{
		return justiceMaps.get(mapID);
	}

	public int getLevelMapId(List<SBean.LevelToMapIdCFGS> lvls, int level)
	{
		int mapId = 0;
		for (SBean.LevelToMapIdCFGS lvl2map : lvls)
		{
			if (level < lvl2map.level)
				break;
			mapId = lvl2map.mapId;
		}
		return mapId;
	}
	
	public int getDemonHoleGrades()
	{
		return this.demonHole.grades.size();
	}
	
	public boolean checkDemonHoleInOpenTime(int now)
	{
		int weekDay = GameTime.getWeekdayByOffset(now, GameData.GAME_DAY_REFRESH_TIME * 3600);
		if(!demonHole.base.openDays.contains(weekDay))
			return false;
		
		return checkInOpenTime(demonHole.base.startTime, demonHole.base.lastTime);
	}
	
	public SBean.DemonHoleMapCFGS getDemonHoleMapCFGSByFloor(int grade, int floor)
	{
		int mapID = getDemonHoleFloorMapID(grade, floor);
		if(mapID <= 0)
			return null;
		
		return demonHoleMaps.get(mapID);
	}
	
	public int getDemonHoleFloorMapID(int grade, int floor)
	{
		if(grade <= 0 || grade > demonHole.grades.size())
			return 0;
		
		if(floor <= 0 || floor > demonHole.grades.get(grade - 1).maps.size())
			return 0;
		
		return demonHole.grades.get(grade - 1).maps.get(floor - 1);
	}
	
	public int getDemonHoleGradeByLevel(int level)
	{
		for(int grade = 1; grade <= demonHole.grades.size(); grade++)
		{
			if(level <= demonHole.grades.get(grade - 1).lvlFloor)
				return grade;
		}
		
		return demonHole.grades.isEmpty() ? 0 : demonHole.grades.size();
	}
	
	public SBean.DemonHoleGradeCFGS getDemonHoleGradeCFGS(int grade)
	{
		if(grade <= 0 || grade > demonHole.grades.size())
			return null;
		
		return demonHole.grades.get(grade - 1);
	}
	
	public SBean.DemonHoleBaseCFGS getDemonHoleBaseCFGS()
	{
		return this.demonHole.base;
	}
	
	public SBean.DemonHoleMapCFGS getDemonHoleMapCFGS(int mapID)
	{
		return this.demonHoleMaps.get(mapID);
	}

	public int getDemonHoleKillStreakBuff(int kills)
	{
		for(int i = demonHole.base.killBuffs.size() - 1; i >= 0; i--)
		{
			if(kills >= demonHole.base.killBuffs.get(i).times)
				return demonHole.base.killBuffs.get(i).buffID;
		}
		
		return 0;
	}
	
	public int getDemonHoleDeadStreakBuff(int deads)
	{
		for(int i = demonHole.base.deadBuffs.size() - 1; i >= 0; i--)
		{
			if(deads >= demonHole.base.deadBuffs.get(i).times)
				return demonHole.base.deadBuffs.get(i).buffID;
		}
		
		return 0;
	}
	
	public SBean.DemonHoleBossCFGS getDemonHoleBossCFGS(int bossID)
	{
		return demonHole.bosses.get(bossID);
	}
	
	public SBean.EmergencyCFGS getEmergencyCFGS()
	{
		return this.emergency;
	}

	public int getEmergencyEndTime()
	{
		return this.emergency.openTime + this.emergency.duration;
	}

	public SBean.EmergencyActivityCFGS getEmergencyActivityCFGS(int activityId)
	{
		return this.emergency.activities.get(activityId);
	}

	public int getEmergencyBasePrestige(int activityId, int index)
	{
		SBean.EmergencyActivityCFGS activity = emergency.activities.get(activityId);
		if (activity == null)
			return 0;
		return index < 0 || index >= activity.basePrestige.size() ? 0 : activity.basePrestige.get(index);
	}

	public boolean isInEmergencyDay()
	{
		int weekDay = GameTime.getWeekdayByOffset(GameTime.getTime(), GameData.GAME_DAY_REFRESH_TIME * 3600);
		if(emergency.openDay.contains(weekDay))
			return true;
		return false;
	}

	public static SBean.TimeSpan getCurEmergencyTime(List<SBean.TimeSpan> openTime)
	{
		int second = GameTime.getSecondOfDay();
		for (SBean.TimeSpan timeSpan : openTime)
		{
			if (second < timeSpan.startTime)
				return null;
			if (second < timeSpan.endTime)
				return timeSpan;
		}
		return null;
	}
	
	public Collection<SBean.DummyGoods> getEmergencyRankReward(int rank)
	{
		Collection<SBean.DummyGoods> rewards = new ArrayList<>();
		for (SBean.EmergencyRankCFGS rankreward : this.emergency.rankRewards)
		{
			if (rankreward.rank <= rank)
				rewards = rankreward.rewards;
			else
				break;
		}
		return rewards;
	}

	public Collection<SBean.DummyGoods> getEmergencyJoinReward()
	{
		return this.emergency.rankRewards.get(this.emergency.rankRewards.size() - 1).rewards;
	}
	
	public SBean.MasterCFGS getMasterCFGS()
	{
		return master;
	}

	public SBean.LucklyStarCFGS getLucklyStarCFGS()
	{
		return this.lucklyStar;
	}

	public List<SBean.DummyGoods> getLucklyStarLevelReward(int level)
	{
		List<SBean.DummyGoods> lvlRewards = new ArrayList<>();
		for (SBean.LucklyStarLevelRewardCFGS rewards:this.lucklyStar.rankRewards)
		{
			if (level < rewards.level)
				break;
			lvlRewards = rewards.rewards;
		}
		return lvlRewards;
	}

	public SBean.FightNpcMapCFGS getFightNpcMapCFGS(int mapID)
	{
		return this.fightNpcMaps.get(mapID);
	}
	
	public int randFightNpcGroup()
	{
		if(fightNpcGroups.isEmpty())
			return 0;
		
		return GameRandom.getRandInt(0, fightNpcGroups.size()) + 1;
	}
	
	public SBean.FightNpcGroupCFGS getFightNpcGroupCFGS(int groupID)
	{
		if(groupID <= 0 || groupID > fightNpcGroups.size())
			return null;
		
		return fightNpcGroups.get(groupID - 1);
	}
	
	public SBean.FightNpcCFGS getFightNpcCFGS(int groupID, int index)
	{
		SBean.FightNpcGroupCFGS group = getFightNpcGroupCFGS(groupID);
		if(group == null)
			return null;
		
		if(index <= 0 || index > group.fightNpcs.size())
			return null;
		
		return group.fightNpcs.get(index - 1);
	}
	
	public SBean.TowerDefenceMapCFGS getTowerDefenceMapCFGS(int mapID)
	{
		return this.towerDefenceMaps.get(mapID);
	}
	
	public SBean.HorseEnHanceAttrCFGS getHorseEnHanceAttrCFGSByAttrId(SBean.HorseEnHanceAttrLineCFGS enhanceCfg, int attrId)
	{
		for (SBean.HorseEnHanceAttrCFGS attr : enhanceCfg.attrs)
		{
			if (attr.attrId == attrId)
			{
				return attr;
			}
		}
		return null;
	}

	public SBean.Prop getHorseEnHanceRandomProp(SBean.HorseEnHanceAttrLineCFGS enhanceCfg)
	{
		int attrSize = enhanceCfg.attrs.size();
		int maxWeight = enhanceCfg.attrs.get(attrSize - 1).attrWeight;
		int propWeight = GameRandom.getRandom().nextInt(maxWeight);
		SBean.HorseEnHanceAttrCFGS attrCFGS = null;
		for (SBean.HorseEnHanceAttrCFGS attr : enhanceCfg.attrs)
		{
			if (attr.attrWeight > propWeight)
			{
				attrCFGS = attr;
				break;
			}
		}
		if (attrCFGS == null)
			return null;
		SBean.HorseEnHanceWeightCFGS weightCFGS = horseEnhanceWeights.get(attrCFGS.randomRuleId - 1);
		int percentWeight = GameRandom.getRandom().nextInt(weightCFGS.rate8100);
		int minPer = 1;
		int maxPer = 100;
		if (percentWeight < weightCFGS.rate0120)
		{
			minPer = 1;
			maxPer = 20;
		}
		else if (percentWeight < weightCFGS.rate2140)
		{
			minPer = 21;
			maxPer = 40;
		}
		else if (percentWeight < weightCFGS.rate4160)
		{
			minPer = 41;
			maxPer = 60;
		}
		else if (percentWeight < weightCFGS.rate6180)
		{
			minPer = 61;
			maxPer = 80;
		}
		else if (percentWeight < weightCFGS.rate8100)
		{
			minPer = 81;
			maxPer = 100;
		}
		int percent = GameRandom.getRandInt(minPer, maxPer + 1);
		int value = (int) ((attrCFGS.maxNum - attrCFGS.minNum) * (percent / 100.0)) + attrCFGS.minNum;
		return new SBean.Prop(attrCFGS.attrId, value);
	}

	public SBean.HorseEnHanceLvlCommonCFGS getHorseEnHanceLvlCommonCFGS(int enhanceLvl)
	{
		if (enhanceLvl <= 0 || enhanceLvl > horseEnhanceLvlcommons.size())
			return null;
		return horseEnhanceLvlcommons.get(enhanceLvl - 1);
	}
	
	public List<SBean.WizardPetCFGS> getWizardPetCFGSs()
	{
		return this.wizardPet;
	}
	
	public SBean.WizardPetCFGS getWizardPetCFGSs(int petId)
	{
		return petId <= 0 || petId > this.wizardPet.size() ? null : this.wizardPet.get(petId - 1);
	}
	
	public SBean.NpcTransfromFuncCFGS getNpcTransfromFuncCFGS(int transfromId)
	{
		return transfromId <= 0 || transfromId > this.npcTransfromFunc.size() ? null : this.npcTransfromFunc.get(transfromId - 1);
	}
	
	public static boolean testCanNpcTransfrom(SBean.NpcTransfromFuncCFGS cfg, Role role)
	{
		for (SBean.Condition condition : cfg.conditions)
		{
			switch (condition.conditionType)
			{
			case GameData.NPC_TRANSFROM_CONDITION_TYPE_VIP_LEVEL:
				if (role.getUseableVipLvl() < condition.conditionArg)
					return false;
				break;
			case GameData.NPC_TRANSFROM_CONDITION_TYPE_ROLE_LEVEL:
				if (role.level < condition.conditionArg)
					return false;
				break;
			default:
				return false;
			}
		}
		return true;
	}
	
	public SBean.NpcMapCFGS getNpcMapCFGS(int mapId)
	{
		return mapId <= 0 || mapId > this.npcMaps.size() ? null : this.npcMaps.get(mapId - 1);
	}
	
	public boolean isJusticeMap(int mapId)
	{
		return this.justiceMaps.containsKey(mapId) ? this.justiceMaps.get(mapId).npcMapGroup == 0 : false;
	}
	
	public SBean.NpcPrayCFGS getNpcPrayCFGS(int prayId)
	{
		return prayId <= 0 || prayId > this.npcPrays.size() ? null : this.npcPrays.get(prayId - 1);
	}
	
	public SBean.PrayDropCFGS getPrayDropCFGS(int dropId)
	{
		return dropId <= 0 || dropId > this.prayDrops.size() ? null : this.prayDrops.get(dropId - 1);
	}

	public SBean.TowerDefenceCFGS getTowerDefenceCFGS(int mapID)
	{
		return this.towerDefences.get(mapID);
	}
	
	public static List<SBean.DummyGoods> getFloorReward(List<SBean.FloorReward> frs, int value)
	{
		if(frs.isEmpty())
			return null;
		
		for(SBean.FloorReward fr: frs)
		{
			if(value <= fr.floor)
				return fr.items;
		}
		
		return frs.get(frs.size() - 1).items;
		
	}

	public SBean.MapSkillCFGS getMapSkillCFGS(int mapID)
	{
		SBean.JusticeMapCopyCFGS map = justiceMaps.get(mapID);
		if (map == null)
			return null;
		return map.mapSkill <= 0 || map.mapSkill > mapSkills.size() ? null : mapSkills.get(map.mapSkill - 1);
	}
	
	
	private int gsid;
//	private TestPerformanceSpawnPositions testPerformance = new TestPerformanceSpawnPositions();
	
	private AtomicInteger equipSeqNum = new AtomicInteger();

	private Pattern msgPattern;
	private Pattern reservedCharsPattern;
	private Pattern spaceCharsPattern;
	private Map<String, Pattern> invalidStrPatterns = new HashMap<>();
	private String maxLengthInvalidReplaceStr;
	private Pattern mapSendPattern;
	private Pattern mapRecvPattern;

	private SBean.CommonCFGS common;
	private Map<Integer, Short> headIcons = new HashMap<>();
	
	private Map<Integer, SBean.PropertyCFGS> properties = new HashMap<>();
	private Map<Integer, SBean.StateCFGS> states = new HashMap<>();
	private Map<Integer, SBean.BuffCFGS> buffs = new TreeMap<>();
	private Map<Integer, SBean.MapBuffCFGS> mapBuffs = new TreeMap<>();
	
	private Map<Integer, SBean.BaseDummyItemCFGS> base = new HashMap<>();
	private Map<Integer, SBean.ItemCFGS> items = new HashMap<>();
	private Map<Integer, SBean.EquipCFGS> equips = new HashMap<>();
	private Map<Integer, SBean.EquipRefineGroupCFGS> equipRefineGroups;
	private SBean.LegendsCFGS legend;
	private Map<Integer, SBean.GemCFGS> gems = new HashMap<>();
	private Map<Integer, SBean.BookCFGS> books = new HashMap<>();

	private Map<Integer, SBean.GiftCFGS> gifts = new TreeMap<>();
	private List<SBean.CheckInCFGS> checkins = new ArrayList<>();
	
	private Map<Integer, SBean.FixedDropTableCFGS> fixedDrops = new TreeMap<>();
	private Map<Integer, SBean.RandomDropTableCFGS> randomDrops = new TreeMap<>();
	private Map<Integer, SBean.NoDuplicateDropTableCFGS> noDuplicateDrops = new TreeMap<>();
	private Map<Integer, SBean.BuffDropTableCFGS> buffDrops = new TreeMap<>();

	private Map<Integer, SBean.SkillCFGS> skills = new TreeMap<>();
	private List<SBean.SkillBournCFGS> skillBourns = new ArrayList<>();
	private Map<Integer, SBean.SkillSpecialCFGS> skillSpecial = new TreeMap<>();
	private Map<Integer, SBean.SpiritCFGS> spirits = new HashMap<>();
	private Map<Integer, SBean.SpiritEffectCFGS> spiritEffects = new HashMap<>();
	
	private Map<Integer, SBean.MonsterCFGS> monsters = new TreeMap<>();
	private Map<Integer, SBean.NpcCFGS> npcs = new TreeMap<>();
	private Map<Integer, SBean.MineralCFGS> minerals = new TreeMap<>();
	private Map<Integer, SBean.TrapExpandedCFGS> traps = new TreeMap<>();
	
	private Map<Integer, SBean.SpawnAreaCFGS> spawnAreas = new TreeMap<>();
	private Map<Integer, SBean.SpawnPointCFGS> spawnPoints = new TreeMap<>();
	private Map<Integer, SBean.NpcPointCFGS> npcPoints = new TreeMap<>();
	private Map<Integer, SBean.MineralPointCFGS> mineralPoints = new TreeMap<>();
	private Map<Integer, SBean.WayPointCFGS> wayPoints = new TreeMap<>();
	private Map<Integer, SBean.MapBuffPointCFGS> mapBuffPoints = new TreeMap<>();
	private Map<Integer, SBean.MapClusterCFGS> mapClusters = new TreeMap<>();
	private Map<Integer, SBean.WorldMapCFGS> worldMaps = new TreeMap<>();
	private Map<Integer, SBean.MapCopyCFGS> mapcopys = new TreeMap<>();
	private Map<Integer, SBean.SectMapCFGS> sectMapcopys = new TreeMap<>();
	private Map<Integer, SBean.ArenaMapCFGS> arenaMaps = new HashMap<>();
	private Map<Integer, SBean.SuperArenaMapCFGS> superArenaMaps = new HashMap<>();
	private Map<Integer, SBean.BWArenaMapCFGS> bwarenaMaps = new HashMap<>();
	private Map<Integer, SBean.ActivityMapCFGS> activityMaps = new TreeMap<>();
//	private Map<Integer, SBean.ClanTaskMapCFGS> clanTaskMaps = new TreeMap<>();
//	private Map<Integer, SBean.ClanOreMapCFGS> clanOreMaps = new TreeMap<>();
//	private Map<Integer, SBean.ClanBattleMapCFGS> clanBattleMaps = new TreeMap<>();
//	private Map<Integer, SBean.ClanBattleHelpMapCFGS> clanBattleHelpMaps = new TreeMap<>();
	private Map<Integer, SBean.PetLifeMapCFGS> petLifeMaps = new TreeMap<>();
	private Map<Integer, SBean.ClimbTowerMapCFGS> climbTowerMaps = new TreeMap<>();
	private Map<Integer, SBean.ForceWarMapCFGS> forcewarMaps = new TreeMap<>();
	private Map<Integer, SBean.WeaponMapCFGS> weaponMaps = new HashMap<>();
	private List<SBean.WeaponMapGradeCFGS> weaponMapGrades = new ArrayList<>();
	private Map<Integer, SBean.DemonHoleMapCFGS> demonHoleMaps = new HashMap<>();
	private Map<Integer, SBean.JusticeMapCopyCFGS> justiceMaps = new HashMap<>();
	private Map<Integer, SBean.FightNpcMapCFGS> fightNpcMaps = new HashMap<>();
	private Map<Integer, SBean.TowerDefenceMapCFGS> towerDefenceMaps = new HashMap<>();
	
	private Map<Integer, SBean.ActivityMapGroupCFGS> actMapGroups = new TreeMap<>();
	
	private List<SBean.ClassRoleCFGS> classRoles = new ArrayList<>();
	private Map<Integer, SBean.ClassRoleFashionCFGS> classRoleFashions = new HashMap<>();

	private Map<Byte, SBean.EquipPurgatoryCFGS> purgatorys = new TreeMap<>();
	private List<SBean.EquipToughenCFGS> toughens = new ArrayList<>();
	private List<SBean.EquipUpStarCFGS> upStars = new ArrayList<>();

	private List<SBean.LevelCFGS> levels = new ArrayList<>();
	private SBean.LevelLimitCFGS levelLimit;
	private List<SBean.ShopCFGS> shops = new ArrayList<>();
	private List<SBean.GambleShopCFGS> gambleShops = new ArrayList<>();
	private List<SBean.RoleRankCFGS> roleRanks = new ArrayList<>();
	private List<SBean.SectRankCFGS> sectRanks = new ArrayList<>();

	private Map<Integer, SBean.DailyTaskCFGS> dailyTasks = new TreeMap<>();
	private Map<Integer, SBean.DailyTaskCFGS> dailyActivities = new TreeMap<>();
	private Map<Integer, SBean.ChallengeTaskGroupCFGS> challengeTasksGroup = new TreeMap<>();
	private SBean.FameCFGS fames = new SBean.FameCFGS();
	private Map<Integer, SBean.DailyOnlineGiftCFGS> dailyOnlineGift = new TreeMap<>();
	private SBean.OfflineExpCFGS offlineExp;
	private SBean.DailyQuizGiftCFGS dailyQuizGift;
	private List<SBean.QuestionCFGS> activityQuestionsBank = new ArrayList<>();
	private List<SBean.QuestionCFGS> taskQuestionsBank = new ArrayList<>();
	private SBean.LuckyWheelCFGS luckyWheel;
	private SBean.RedEnvelopeCFGS redEnvelope;
	private List<SBean.ActivityChallengeTypeCFGS> activityChallengeType;

	private List<SBean.MainTaskCFGS> mainTasks = new ArrayList<>();
	private Map<Integer, SBean.MainTaskBWTypeCFGS> bwTypeMainTasks = new HashMap<>();
	private List<SBean.WeaponGroupTaskCFGS> weaponTasks = new ArrayList<>();
	private List<SBean.PetTaskCFGS> petTasks = new ArrayList<>();
	private List<SBean.SectTaskCFGS> sectTasks = new ArrayList<>();
	private Map<Integer, SBean.MrgSeriesTaskGroupCFGS> mrgSeriesTaskGroups = new HashMap<>();
	private List<SBean.MrgLoopTaskCFGS> mrgLoopTasks = new ArrayList<>();
	
	private List<SBean.EquipSlotCFGS> slots = new ArrayList<>();
	private List<SBean.EquipStarAddPropCFGS> starAdditionProp = new ArrayList<>();
	private Map<Integer, List<SBean.PropAwardCFGS>> propAwards = new TreeMap<>();
	private Map<Integer, SBean.AlterCFGS> alters = new HashMap<>();

	private Map<Integer, SBean.WeaponCFGS> weapons = new TreeMap<>();
	private Map<Integer, SBean.WeaponUniqueSkillCFGS> weaponUSkills;

	private List<SBean.PetCFGS> pets = new ArrayList<>();
	private List<SBean.PetLvlCFGS> petLvls = new ArrayList<>();
	private List<SBean.PetTransformCFGS> petTransforms = new ArrayList<>();
	private Map<Integer, SBean.PetCoPracticeGroupCFGS> petCoPractices = new HashMap<>();
	private Map<Integer, SBean.PetBreakSkillCFGS> petBreakSkills = new TreeMap<>();
	private Map<Integer, SBean.PetSpiritGroupCFGS> petSpiritGroups = new HashMap<>();
	private Map<Integer, SBean.PetExploitCFGS> petExploits = new HashMap<>();

	private List<SBean.CLassTransformCFGS> transforms = new ArrayList<>();

	private Map<Integer, SBean.SuiteCFGS> suites = new TreeMap<>();
	private Map<Integer, Integer> equipToSuites = new TreeMap<>();
	private Map<Integer, SBean.SectAuthorityCFGS> sectAuthority = new TreeMap<>();
	private Map<Integer, SBean.SectUpLevelCFGS> sectUpLevel = new TreeMap<>();
	private Map<Integer, SBean.SectGroupSkillCFGS> sectGroupSkill = new TreeMap<>();
	private Map<Integer, SBean.SectWorshipCFGS> sectWorship = new TreeMap<>();
	private Map<Integer, SBean.SectWorshipExpCFGS> sectWorshipExp = new TreeMap<>();
	private Map<Integer, SBean.SectBanquetCFGS> sectBanquet = new TreeMap<>();
	
	private Map<Integer, SBean.FightSPCFGS> fightSP = new TreeMap<>();
	private Map<Integer, SBean.BlurCFGS> blurs = new TreeMap<>();
	private List<SBean.SectIconCFGS> sectIcons = new ArrayList<>();

	private List<SBean.DIYSkillBaseCFGS> diySkills = new ArrayList<>();
	private Map<Integer, SBean.DIYSkillGradeCFGS> diySkillGrades = new TreeMap<>();
	private SBean.DIYSkillUniqueCFGS diySkillUnique;
	private Map<Integer, SBean.DIYSkillActionCFGS> diySkillActions = new TreeMap<>();
	private Map<Integer, DIYBuffLib> diyBuffLibs = new TreeMap<>();
	private Map<Integer, SBean.DiySkillSlotUnblockCFGS> slotUnblock = new TreeMap<>();
	private List<SBean.VipCFGS> vips = new ArrayList<>();
	private int payId;
	private Map<Integer, SBean.PayCFGS> pays = new HashMap<>();
	private Map<String, SBean.ChannelCFGS> channels = new HashMap<>();
	private List<SBean.SpecialCardCFGS> specialCards = new ArrayList<>();

//	private SBean.ClanCFGS clanCFGS;
//	private SBean.ClanValueCFGS clanValueCFGS;
//	
//	private Map<Integer, SBean.ClanTaskCFGS> clanTasks = new TreeMap<>();
//	private Map<Integer, SBean.ClanTaskGroupCFGS> clanGroupTasks = new TreeMap<>();
//	
//	private Map<Integer, SBean.ClanDiscipleNameCFGS> discipleName = new TreeMap<>();

	private SBean.ArenaCFGS arenaCfg;
	private Map<Integer, SBean.ArenaRobotCFGS> arenaRobots = new TreeMap<>();
	private Map<Integer, SBean.ArenaRobotGroupCFGS> arenaRobotGroup = new TreeMap<>();
	private SBean.SuperArenaCFGS superarena;
	private Map<Integer, SBean.SuperArenaTypeCFGS> superarenaTypes;
	private SBean.BWArenaCFGS bwarena;
	private SBean.RandomNameCFGS randomNames;
	private SBean.PKCFGS pkSystem;
	private Map<Integer, SBean.ForceWarCFGS> forcewar;
	private SBean.ForceWarBaseCFGS forcewarBase;
	
	private SBean.ProduceCFGS produce;
	private Map<Integer, SBean.ProduceRecipeCFGS> produceRecipes;
	private SBean.FusionCFGS fusion;
	
	private Map<Integer, SBean.AiTrigerCFGS> aitrigers;
	private Map<Integer, SBean.TrigEventCFGS> trigEvents;
	private Map<Integer, SBean.TrigBehaviorCFGS> trigBehaviors;
	
	private Map<Integer, SBean.WorldBossCFGS> worldBosses;
	private Map<Integer, SBean.WorldMonsterCFGS> worldSuperMonsters;
	private Map<Integer, SBean.WorldMineralCFGS> worldMinerals; 
	private SBean.MonsterDamageCFGS monsterDamage;
	private SBean.FightFactorCFGS spiritFactors;
	private SBean.FightFactorCFGS weaponFactors;
	
	private Map<Integer, SBean.StoreCFGS> stores;
	
	private Map<Integer, SBean.HorseCFGS> horses;
	private Map<Integer, SBean.HorseEnHanceCFGS> horseEnHances;
	private Map<Integer, SBean.HorseEnHanceLvlCFGS> horseEnhanceLvls;
	private Map<Integer, SBean.HorseShowCFGS> horseShows;
	private Map<Integer, SBean.HorseSkillCFGS> horseSkills;
	private Map<Integer, SBean.HorseSkillUpdateDataCFGS> horseSkillUpdate;
	private Map<Integer, SBean.HorseEffectCFGS> horseEffectCfgs;
	private SBean.HorseCommonCFGS horseCommon;
	private List<SBean.HorseEnHanceLvlCommonCFGS> horseEnhanceLvlcommons;
	private Map<Integer, SBean.HorseEnHanceTypeCFGS> horseEnhanceTypes;
	private List<SBean.HorseEnHanceWeightCFGS> horseEnhanceWeights;
	
	private SBean.TreasureBaseCFGS treasureBase;
	private Map<Integer, SBean.TreasureMapCFGS> treasureMaps;
	private Map<Integer, SBean.TreasurePieceCFGS> treasurePieces;
	private Map<Integer, SBean.InfoPointCFGS> infoPoints;
	private Map<Integer, SBean.MedalCFGS> medals;
	private Map<Integer, SBean.TreasureNpcCFGS> treasureNpcs;
	
	private List<SBean.FriendGiveRewardCFGS> friendGives;
	private List<SBean.CharmCFGS> charms;
	private Map<Integer, SBean.FriendHeadCFGS> friendHeads;
	private Map<Integer, SBean.FashionCFGS> fashions;
	private Set<Integer> allFashionTypes = new HashSet<>();;
	private Map<Integer, SBean.SocialActionCFGS> socialActions;
	private SBean.SealBaseCFGS sealBase;
	private List<SBean.SealGradeCFGS> sealGrades;
	private Map<Integer, SBean.SealEnhanceTypeCFGS> sealEnhanceTypes;
	private Map<Integer, SBean.LeadGroupCFGS> leadGroups;
	private SBean.ExpCoinBaseCFGS expCoinBase;
	private Map<Integer, SBean.RareBookGroupCFGS> rarebookGroups;
	private Map<Integer, SBean.GraspGroupCFGS> graspGroups;
	private List<Integer> graspIDs;
	private Map<Integer, SBean.DMGTransferLevelCFGS> dmgTransfers;
	private List<SBean.DMGTransferBuyCFGS> dmgTransferBuys;
	private Map<Integer, SBean.TitleCFGS> titles;
	private SBean.BetaActivityCFGS betaActivity;
	private Map<Integer, SBean.PetAchieveGroupCFGS> petAchieveGroups = new HashMap<>();		//<type, PetAchieveGroupCFGS>
	private Map<Integer, SBean.PetAchieveCFGS> petAchieves = new HashMap<>();
	private Map<Integer, SBean.BranchTaskCFGS> branchTask = new HashMap<>();
	private Map<Integer, SBean.UniqueSkillCFG> uniqueSkills = new HashMap<>();				//<绝技ID, UniqueSkillCFG>
	private Set<Integer> uniqueSkillIDs = new HashSet<>();									//<技能ID>
	private Map<Integer, SBean.ClimbTowerFloorCFGS> climbTowerFloor = new HashMap<>();
	private Map<Integer, SBean.ClimbTowerFameCFGS> climbTowerFame = new HashMap<>();
	private SBean.ClimbTowerBaseDataCFGS climbTowerBaseData;
	private List<SBean.SecretAreaTaskCFGS> secretAreaData;
	private Map<Integer, SBean.PetLifeTaskGroupCFGS> petLifeTask = new HashMap<>();
	private SBean.SectDeliverCFGS sectDeliver;
	private SBean.SectDeliverTaskCFGS sectDeliverTask;
	private SBean.SectDeliverVehicleCFGS sectDeliverVehicle;
	private SBean.SectDeliverRouteCFGS sectDeliverRoute;
	private SBean.SectDeliverWishCFGS sectDeliverWish;
	private SBean.RollNoticeCFGS rollNotice;
	private List<SBean.RemainActivityCFGS> rmActivitys = new ArrayList<>();
	private List<SBean.ComposeCFGS> composes;
	private Map<Integer, SBean.MessageBoardSide> messageBoards;
	private SBean.MessageBoardCommonCFGS messageBoardCommon;
	private Map<Integer, SBean.ScheduleRewardsCFGS> scheduleRewards;
	private Map<Integer, SBean.ScheduleDataCFGS> scheduleData;
	private SBean.ArmorCFGS armor;
	private Map<Integer, SBean.SceneTrigCFGS> sceneTrigs;
	private Map<Integer, SBean.SceneSpawnPointCFGS> sceneSpawnPoints;
	private SBean.MarriageCFGS marriage;
	private List<SBean.ExchangeCFGS> exchange;
	private Map<Integer, SBean.SectGroupMapCFGS> sectGroupMaps;
	private Map<Integer, SBean.SectGroupMapPersonRewardCFGS> sectGroupMapRankRewards;
	private SBean.WeaponTalentCommonCFGS weaponTalent;
	private SBean.FlagBattleCFGS flagBattle;
	private Map<Integer, SBean.MonsterProcess> activityMapProcess;
	private SBean.HeirloomCFGS heirloom;
	private SBean.RobotCFGS robot;
	private List<SBean.MapLocation> performanceSpawns;
	private SBean.AuctionCFGS auction;
	private Set<Integer>   flashsaleResources;
	private Set<Integer>   adverBackgrounds;
	private List<SBean.ActivityLastCFGS> activityLast;
	private SBean.SteleCFGS stele;
	private SBean.JusticeMapCFGS justice;
	private SBean.DemonHoleCFGS demonHole;
	private SBean.BattleMapCFGS battleMap;
	private SBean.EmergencyCFGS emergency;
	private SBean.LucklyStarCFGS lucklyStar;
	private List<SBean.FightNpcGroupCFGS> fightNpcGroups;
	private List<SBean.WizardPetCFGS> wizardPet;
	private List<SBean.NpcTransfromFuncCFGS> npcTransfromFunc;

	private List<SBean.NpcMapCFGS> npcMaps;
	private List<SBean.NpcPrayCFGS> npcPrays;
	private List<SBean.PrayDropCFGS> prayDrops;
	
	private Map<Integer, SBean.TowerDefenceCFGS> towerDefences;
	private SBean.MasterCFGS master;
	private List<SBean.MapSkillCFGS> mapSkills;

}
