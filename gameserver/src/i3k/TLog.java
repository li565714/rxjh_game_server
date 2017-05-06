// modified by i3k.gtool.QQMetaGen at Sat May 06 17:01:18 CST 2017.

package i3k;

import ket.util.Stream;

public final class TLog
{

	// gm修改道具
	public static final int AT_GM_MOD_ITEM = 1;
	// 充值
	public static final int AT_PAY = 9;
	// 系统奖励
	public static final int AT_SYS_REWARD = 48;
	// 全服奖励
	public static final int AT_WORLD_REWARD = 49;
	// 升级
	public static final int AT_LEVEL_UP = 50;
	// 商城购买
	public static final int AT_BUY_MALL_GOODS = 71;
	// 购买商店商品
	public static final int AT_BUY_SHOP_GOOGS = 72;
	// 杂货店
	public static final int AT_STROE_BUY = 73;
	// 购买套装
	public static final int AT_BUY_SUIT = 74;
	// 商品团购
	public static final int AT_GROUPUY_GOODS = 75;
	// 限时特卖
	public static final int AT_FLASHSALE_GOODS = 76;
	// 购买金币
	public static final int AT_BUY_COIN = 101;
	// 购买体力
	public static final int AT_BUY_VIT = 102;
	// 使用药水
	public static final int AT_USE_POTION = 103;
	// 购买幸运大转盘抽奖次数
	public static final int AT_BUY_LUCKY_WHEEL_DRAW_TIMES = 104;
	// 签到
	public static final int AT_CHECKIN_TAKE = 105;
	// 首次充值领奖
	public static final int AT_TAKE_FIRST_PAY_GIFT_REWARD = 106;
	// 领取充值奖励
	public static final int AT_TAKE_PAY_GIFT_REWARD = 107;
	// 领取消费奖励
	public static final int AT_TAKE_CONSUME_GIFT_REWARD = 108;
	// 领取升级奖励
	public static final int AT_TAKE_UPGRADE_GIFT_REWARD = 109;
	// 购买基金
	public static final int AT_BUY_INVESTMENT_FUND = 110;
	// 领取基金奖励
	public static final int AT_TAKE_INVESTMENT_FUND_REWARD = 111;
	// 购买成长基金
	public static final int AT_BUY_GROWTH_FUND = 112;
	// 领取成长基金
	public static final int AT_TAKE_GROWTH_FUND_REWARD = 113;
	// 兑换活动
	public static final int AT_TAKE_EXCHANGE_GIFT_REWARD = 114;
	// 登录送礼活动
	public static final int AT_TAKE_LOGIN_GIFT_REWARD = 115;
	// 购买普通副本进入次数
	public static final int AT_BUY_NORMAL_MAPCOPY_TIMES = 116;
	// 购买活动副本进入次数
	public static final int AT_BUY_ACTIVITY_MAPCOPY_TIMES = 117;
	// 扫荡副本
	public static final int AT_SWEEP_PRIVATE_MAP = 118;
	// 领取vip奖励
	public static final int AT_TAKE_VIP_REWARD = 119;
	// 扩展背包
	public static final int AT_EXPAND_BAG_CELLS = 120;
	// 出售背包装备
	public static final int AT_SELL_BAG_EQUIP = 121;
	// 出售背包物品
	public static final int AT_SELL_BAG_ITEM = 122;
	// 出售背包宝石
	public static final int AT_SELL_BAG_GEM = 123;
	// 出售背包新法书
	public static final int AT_SELL_BAG_BOOK = 124;
	// 批量出售背包装备
	public static final int AT_BATCH_SELL_BAG_EQUIPS = 125;
	// 批量出售背包道具
	public static final int AT_BATCH_SELL_BAG_ITEMS = 126;
	// 批量出售背包宝石
	public static final int AT_BATCH_SELL_BAG_GEMS = 127;
	// 批量出售背包心法书
	public static final int AT_BATCH_SELL_BAG_BOOKS = 128;
	// 使用礼包
	public static final int AT_USE_ITEM_GIFT_BOX = 129;
	// 使用金币包
	public static final int AT_USE_ITEM_COIN_BAG = 130;
	// 使用宝石包
	public static final int AT_USE_ITEM_DIAMOND_BAG = 131;
	// 使用经验丹
	public static final int AT_USE_ITEM_EXP = 132;
	// 使用血瓶
	public static final int AT_USE_ITEM_HP = 133;
	// 使用血池道具
	public static final int AT_USE_ITEM_HP_POOL = 134;
	// 使用装备能量丹
	public static final int AT_USE_ITEM_EQUIP_ENERGY = 135;
	// 使用宝石能量丹
	public static final int AT_USE_ITEM_GEM_ENERGY = 136;
	// 使用心法丹
	public static final int AT_USE_ITEM_SPIRIT_INSPIRATION = 137;
	// 使用体力瓶
	public static final int AT_USE_ITEM_AS_VIT = 138;
	// 使用时装包
	public static final int AT_USE_ITEM_FASHION = 139;
	// 穿装备
	public static final int AT_UP_WEAR_EQUIP = 140;
	// 脱装备
	public static final int AT_DOWN_WEAR_EQUIP = 141;
	// 装备强化
	public static final int AT_EQUIP_LEVEL_UP = 142;
	// 装备升星
	public static final int AT_EQUIP_STAR_UP = 143;
	// 修炼装备
	public static final int AT_REPAIR_EQUIP = 144;
	// 批量穿装备
	public static final int AT_AUTO_UP_WEAR_EQUIP = 145;
	// 宝石升级
	public static final int AT_GEM_LEVEL_UP = 146;
	// 宝石镶嵌
	public static final int AT_GEM_INLAY = 147;
	// 宝石解镶嵌
	public static final int AT_GEM_UNLAY = 148;
	// 技能等级提升
	public static final int AT_SKILL_LEVEL_UP = 149;
	// 技能强化
	public static final int AT_SKILL_ENHANCE = 150;
	// 学习心法
	public static final int AT_LEARN_SPIRIT = 151;
	// 心法升级
	public static final int AT_SPIRIT_LEVEL_UP = 152;
	// 激活神兵
	public static final int AT_MAKE_WEAPON = 153;
	// 神兵升级
	public static final int AT_WEAPON_LEVEL_UP = 154;
	// 神兵购买升级
	public static final int AT_WEAPON_BUY_LEVEL = 155;
	// 激活佣兵
	public static final int AT_MAKE_PET = 156;
	// 佣兵转职
	public static final int AT_PET_TRANSFORM = 157;
	// 佣兵升级
	public static final int AT_PET_LEVEL_UP = 158;
	// 佣兵购买升级
	public static final int AT_PET_BUY_LEVEL = 159;
	// 佣兵升星
	public static final int AT_PET_STAR_UP = 160;
	// 佣兵技能突破
	public static final int AT_PET_BREAK_SKILL_LEVEL_UP = 161;
	// 领取每日任务奖励
	public static final int AT_TAKE_DAILY_TASK_REWARD = 162;
	// 领取挑战任务奖励
	public static final int AT_TAKE_CHALLENGE_TASK_REWARD = 163;
	// 领取每日在线奖励
	public static final int AT_TAKE_DAILY_ONLINE_GIFT = 164;
	// 幸运大转盘抽奖
	public static final int AT_LUCKY_WHEEL_ON_DRAW = 165;
	// 领取主线任务奖励
	public static final int AT_TAKE_MAIN_TASK_REWARD = 166;
	// 领取神兵任务奖励
	public static final int AT_TAKE_WEAPON_TASK_REWARD = 167;
	// 购买自创武功次数
	public static final int AT_DIY_SKILL_BUY_TIMES = 168;
	// 购买分解装备能量
	public static final int AT_CLAN_SPLIT_SP_BUY = 169;
	// 宗门战之宗门迁移
	public static final int AT_CLAN_MOVE_POSITION = 170;
	// 地图传送到NPC
	public static final int AT_TELEPORT_NPC = 171;
	// 地图传送到怪物
	public static final int AT_TELEPORT_MONSTER = 172;
	// 地图传送到矿
	public static final int AT_TELEPORT_MINERAL = 173;
	// 竞技场清除冷却
	public static final int AT_ARENA_RESET_COOL = 174;
	// 竞技场购买次数
	public static final int AT_ARENA_BUY_TIMES = 175;
	// 竞技场领取积分奖励
	public static final int AT_TAKE_ARENA_SCORE_REWARD = 176;
	// 给好友送花
	public static final int AT_GIVE_FRIEND_FLOWER = 177;
	// 寄售装备
	public static final int AT_PUT_ON_EQUIP = 178;
	// 寄售道具
	public static final int AT_PUT_ON_NORMAL_ITEMS = 179;
	// 取消寄售道具装备
	public static final int AT_PUT_OFF_AUCTION_ITEMS = 180;
	// 购买寄售道具装备
	public static final int AT_BUY_AUCTION_ITEMS = 181;
	// 扩充寄售格子
	public static final int AT_EXPAND_AUCTION_CELLS = 182;
	// 刷新藏宝图碎片
	public static final int AT_REFRESH_TREASURE_INFO = 183;
	// 购买藏宝图碎片
	public static final int AT_BUY_TREASURE_PIECES = 184;
	// 开启寻宝
	public static final int AT_TREASURE_TOTAL_SEARCH = 185;
	// 宝物装裱
	public static final int AT_MEDAL_GROW = 186;
	// 驯服坐骑
	public static final int AT_TAME_HORSE = 187;
	// 坐骑升星
	public static final int AT_UP_STAR_HORSE = 188;
	// 坐骑强化
	public static final int AT_ENHANCE_HORSE = 189;
	// 坐骑幻化
	public static final int AT_ACTIVATE_SHOW = 190;
	// 学习坐骑技能
	public static final int AT_LEARN_HORSE_SKILL = 191;
	// 宗门购买行动力
	public static final int AT_CLAN_BUY_DO_POWER = 192;
	// 消耗大喇叭
	public static final int AT_USE_CHAT_ITEM = 193;
	// 结束挖矿
	public static final int AT_SYNC_END_MINE = 194;
	// 拾取掉落和奖励物品到背包
	public static final int AT_ADD_DROP_TO_BAG = 195;
	// 领取扫荡掉落和奖励物品到背
	public static final int AT_SWEEP_ADD_DROP_TO_BAG = 196;
	// 领取邮件附件
	public static final int AT_TAKE_MAIL_ATTACHMENT = 197;
	// 领取全部邮件附件
	public static final int AT_TAKE_ALL_MAIL_ATTACHMENT = 198;
	// 进入普通副本扣体力
	public static final int AT_COMMON_MAPCOPY_ONSTART = 199;
	// 领取通关副本翻盘奖励
	public static final int AT_ON_SELECT_REWARD_CARD = 200;
	// 进入帮派副本扣体力
	public static final int AT_SECT_MAPCOYP_ONSTART = 201;
	// 帮派副本结束伤害奖励
	public static final int AT_SECT_MAPCOYP_ONEND = 202;
	// 竞技场结束名次上升奖励
	public static final int AT_ARENA_MAPCOPY_ONEND = 203;
	// 原地复活
	public static final int AT_REVIVE_IN_SITU = 204;
	// 转职
	public static final int AT_TRANSFORM = 205;
	// 挖矿
	public static final int AT_TRY_START_MINE = 206;
	// 礼包码兑换奖励
	public static final int AT_TAKE_GIFT_PACKAGE_REWARD = 207;
	// 用户刷新商店
	public static final int AT_USER_REFRESH_SHOP = 208;
	// 生产删除锁定道具
	public static final int AT_DEL_LOCKED_BAG_ITEMS = 209;
	// 尝试刷新体力
	public static final int AT_TRY_REFRESH_VIT = 210;
	// 尝试刷新分解能量
	public static final int AT_TRY_REFRESH_SPLIT_SP = 211;
	// 使用抽奖宝箱
	public static final int AT_USE_ITEM_CHEST_IMPL = 212;
	// 使用配方卷轴
	public static final int AT_USE_ITEM_AS_RECIPEREEL = 213;
	// 神兵升星
	public static final int AT_WEAPON_STAR_UP = 214;
	// 创建新帮派
	public static final int AT_CREATE_NEW_SECT = 215;
	// 尝试膜拜帮派成员
	public static final int AT_TRY_WORSHIP_SECT_MEMBER = 216;
	// 尝试开启帮派宴席
	public static final int AT_TRY_OPEN_SECT_BANQUET = 217;
	// 尝试参加帮派宴席
	public static final int AT_TRY_JOIN_SECT_BANQUET = 218;
	// 完成帮派任务
	public static final int AT_SECT_TASK_FINISH_CB = 219;
	// 提交任务道具
	public static final int AT_TEST_LOG_TASK = 220;
	// 刷新帮派任务
	public static final int AT_SECT_TASK_RESET_CB = 221;
	// 领取帮派共享任务奖励
	public static final int AT_SECT_TASK_TAKE_SHARE_REWARDS = 222;
	// 修改帮派名称
	public static final int AT_CHANGE_SECT_NAME_CB = 223;
	// 发送帮派信息
	public static final int AT_SECT_SEND_MAIL_CB = 224;
	// 自创武功解锁槽位
	public static final int AT_DIYSKILL_SLOT_UNLOCK = 225;
	// 购买声望
	public static final int AT_BUY_PRESTIGE = 226;
	// 创建宗门
	public static final int AT_CLAN_CREATE = 227;
	// 宗门加速收徒
	public static final int AT_CLAN_SHOUTU_SPEEDUP = 228;
	// 宗门加速比武
	public static final int AT_CLAN_BIWU_SPEEDUP = 229;
	// 宗门布施开始
	public static final int AT_CLAN_BUSHI_START = 230;
	// 离线领经验奖励
	public static final int AT_OFFLINE_EXP = 231;
	// 宗门经验提升精英弟子属性
	public static final int AT_CLAN_RUSH_TOLLGATE_TO_EXP = 232;
	// 宗门道具提升精英弟子属性
	public static final int AT_CLAN_RUSH_TOLLGATE_TO_ITEM = 233;
	// 宗门矿升级
	public static final int AT_CLAN_ORE_BUILD_UPLEVEL = 234;
	// 宗门添加矿
	public static final int AT_ADD_CLAN_ORE = 235;
	// 宗门矿添加物品
	public static final int AT_SYNC_ADD_ORE = 236;
	// 宗门传道开始
	public static final int AT_CLAN_CHUANDAO_START = 237;
	// 宗门占矿完成
	public static final int AT_CLAN_ORE_OCCUPY_FINISH = 238;
	// 宗门夺矿搜索
	public static final int AT_CLAN_SEARCH_ORE = 239;
	// 宗门矿遭遇战结束
	public static final int AT_CLAN_ORE_HARRY_FIGHT_END = 240;
	// 宗门战侦查
	public static final int AT_CLAN_BATTLE_KEEK = 241;
	// 宗门分解
	public static final int AT_CLAN_SPLIT = 242;
	// 会武副本结算
	public static final int AT_ON_SUPER_ARENA_END = 243;
	// 重置传送时间
	public static final int AT_RESET_TRANS_TIME = 244;
	// 传送至BOSS
	public static final int AT_CHECK_CAN_TRANS_TO_BOSS = 245;
	// 接收好友赠送的体力批量
	public static final int AT_RECEIVE_FRIEND_VIT = 246;
	// 接收好友赠送的体力单个
	public static final int AT_RECEIVE_FRIEND_VIT2 = 247;
	// 藏宝图任务消耗
	public static final int AT_LOG_TREASURE_TASK = 248;
	// 获取藏宝图NPC奖励
	public static final int AT_TAKE_TREASURE_NPC_REWARD = 249;
	// 获取藏宝图地图奖励
	public static final int AT_TAKE_TREASURE_MAP_REWARD = 250;
	// GM添加物品
	public static final int AT_GM_ADD_GAME_ITEM = 251;
	// 龙印道具合成
	public static final int AT_SEAL_NORMAL_MAKE = 252;
	// 龙印元宝合成
	public static final int AT_SEAL_DIAMOND_MAKE = 253;
	// 龙印升级
	public static final int AT_SEAL_UPGRADE = 254;
	// 龙印洗练
	public static final int AT_SEAL_ENHANCE = 255;
	// 获取经验正常历练币
	public static final int AT_ADD_EXP_COIN = 256;
	// 使用历练瓶
	public static final int AT_USE_ITEM_EXPCOIN_POOL = 257;
	// 提炼历练到历练瓶
	public static final int AT_EXTRACT_EXPCOIN = 258;
	// 正邪道场副本结束
	public static final int AT_BWARENA_MAPCOPY_ONEND = 259;
	// 正邪道场刷新对手
	public static final int AT_BWARENA_REFRESH_ENEMY = 260;
	// 正邪道场领取积分奖励
	public static final int AT_BWARENA_TAKE_SCORE_REWARD = 261;
	// 正邪道场购买次数
	public static final int AT_BWARENA_BUY_TIMES = 262;
	// 藏书道具存到书袋
	public static final int AT_RARE_BOOK_PUSH = 263;
	// 书袋中取出藏书
	public static final int AT_RARE_BOOK_POP = 264;
	// 解锁藏书
	public static final int AT_RARE_BOOK_UNLOCK = 265;
	// 藏书升级
	public static final int AT_RARE_BOOK_UPLVL = 266;
	// 参悟
	public static final int AT_GRASP_IMPL = 267;
	// 参悟CD重置
	public static final int AT_GRASP_RESET = 268;
	// 宗门生产
	public static final int AT_CLAN_PRODUCE = 269;
	// 帮派成员消耗体力
	public static final int AT_ROLE_MEMEBER_USE_VIE = 270;
	// 加快帮派升级冷却
	public static final int AT_ACCELERATE_UPGRADE_COOLING = 271;
	// 捐献帮派技能道具
	public static final int AT_ADD_SECT_AURA_EXP = 272;
	// 获取膜拜奖励
	public static final int AT_TAKE_WORSHIP_REWARD = 273;
	// 获取随从任务奖励
	public static final int AT_TAKE_PET_TASK_REWARD = 274;
	// 获取封测活动奖励
	public static final int AT_TAKE_BETA_ACTIVITY_REWARD = 275;
	// 使用月卡道具
	public static final int AT_USE_ITEM_MONTHLYCARD = 276;
	// 使用VIP体验卡
	public static final int AT_USE_ITEM_VIPCARD = 277;
	// 获取支线任务奖励
	public static final int AT_TAKE_BRANCH_TASK_REWARD = 278;
	// 爬塔活动购买次数
	public static final int AT_CLIMB_TOWER_BUY_TIMES = 279;
	// 爬塔活动进入副本
	public static final int AT_START_CLIMB_TOWER_COPY = 280;
	// 使用获取爬塔声望道具
	public static final int AT_USER_ITEM_TOWER_FAME = 281;
	// 领取声望升级奖励
	public static final int AT_TAKE_TOWER_FAME_REWARD = 282;
	// 刷新帮派运镖任务列表
	public static final int AT_REFRESH_SECT_DELIVER = 283;
	// 帮派运镖投保
	public static final int AT_SECT_DELIVER_PROTECT = 284;
	// 开始帮派运镖
	public static final int AT_SECT_DELIVER_BEGIN = 285;
	// 帮派运镖祝福保存
	public static final int AT_SAVE_WISH_SECT_DELIVER = 286;
	// 领取秘境任务奖励
	public static final int AT_TAKE_SECRET_AREA_REWARD = 287;
	// 扫荡爬塔副本
	public static final int AT_SWEEP_TOWER_MAP = 288;
	// 领取帮派运镖奖励
	public static final int AT_FINISH_SECT_DELIVER = 289;
	// 背包合并
	public static final int AT_MERGE_BAG = 290;
	// 获取劫镖奖励
	public static final int AT_ROB_SUCCESS = 291;
	// 使用增加武勋道具
	public static final int AT_USE_ITEM_FEAT_ADDER = 292;
	// 使用技能道具
	public static final int AT_USE_ITEM_SKILL = 293;
	// 使用技能道具失败
	public static final int AT_USE_ITEM_SKILL_FAIL = 294;
	// 领取七日留存奖励
	public static final int AT_REMAIN_ACTIVITY_REWARD = 295;
	// 使用信件
	public static final int AT_USE_ITEM_LETTER = 296;
	// 碎片合成
	public static final int AT_PIECE_COMPOSE = 297;
	// 角色改名
	public static final int AT_ROLE_RENAME = 298;
	// 获取随从身世任务奖励
	public static final int AT_TAKE_PET_LIFE_TASK_REWARD = 299;
	// 发布留言板
	public static final int AT_ADD_MESSAGE_BOARD = 300;
	// 获取每日充值奖励
	public static final int AT_TAKE_DAILY_PAY_GIFT_REWARD = 301;
	// 获取日程表奖励
	public static final int AT_TAKE_SCHEDULE_REWARD = 302;
	// 解锁内甲类型
	public static final int AT_UNLOCK_ARMOR_TYPE = 303;
	// 使用善恶值道具
	public static final int AT_USE_ITEM_EVIL_VALUE = 304;
	// 内甲升阶
	public static final int AT_ARMOR_UPRANK = 305;
	// 内甲升级
	public static final int AT_ARMOR_LEVEL_UP = 306;
	// 符文存入符文包
	public static final int AT_PUSH_RUNE_TO_RUNE_BAG = 307;
	// 符文取回背包
	public static final int AT_POP_RUNE_TO_BAG = 308;
	// 内甲重置天赋
	public static final int AT_ARMOR_RESET_TALENT = 309;
	// 内甲符文页解锁
	public static final int AT_SOLT_GROUP_UNLOCK = 310;
	// 符文祝福
	public static final int AT_RUNE_WISH = 311;
	// 存入仓库
	public static final int AT_WAREHOUSE_SAVE = 312;
	// 取出仓库
	public static final int AT_WAREHOUSE_TAKE = 313;
	// 结婚
	public static final int AT_MARRIAGE = 314;
	// 离婚
	public static final int AT_DIVORCE = 315;
	// 道具兑换
	public static final int AT_ITEM_EXCHANGE = 316;
	// 结婚技能升级
	public static final int AT_MARRIAGE_SKILL_LEVEL_UP = 317;
	// 拓展仓库
	public static final int AT_EXBAND_WAREHOUSE = 318;
	// 开启帮派团本
	public static final int AT_OPEN_SECT_GROUP_MAP = 319;
	// 坐骑技能升级
	public static final int AT_UP_LEVEL_HORSE_SKILL = 320;
	// 神兵技能升级
	public static final int AT_WEAPON_SKILL_LEVEL_UP = 321;
	// 神兵天赋点购买
	public static final int AT_WEAPON_TALENT_POINT_BUY = 322;
	// 神兵天赋点重置
	public static final int AT_WEAPON_TALENT_POINT_RESET = 323;
	// 抢红包
	public static final int AT_SNATCH_RED_ENEVLOPE = 324;
	// 燃放烟花
	public static final int AT_PLAY_FIREWORK = 325;
	// 领取姻缘系列任务奖励
	public static final int AT_TAKE_MRGSERIES_TASK_REWARD = 326;
	// 领取姻缘环任务奖励
	public static final int AT_TAKE_MRGLOOP_TASK_REWARD = 327;
	// 发送礼物
	public static final int AT_SEND_GIFT = 328;
	// 接受礼物
	public static final int AT_GET_GIFT = 329;
	// 使用属性强化道具
	public static final int AT_USE_ITEM_PROP_STRENGTH = 330;
	// 名望晋级
	public static final int AT_FAME_UPGRADE = 331;
	// 名望领奖
	public static final int AT_FAME_TAKE_REWARD = 332;
	// 随从技能升级
	public static final int AT_PET_SKILL_LEVEL_UP = 333;
	// 神兵特技激活
	public static final int AT_WEAPON_USKILL_OPEN = 334;
	// 装备精炼
	public static final int AT_EQUIP_REFINE = 335;
	// 随从武库心法升级
	public static final int AT_PET_SPIRIT_LVLUP = 336;
	// 随从心法重修
	public static final int AT_PET_SPIRIT_LEARN = 337;
	// 购买修炼点数
	public static final int AT_BUY_OFFLINE_FUNC_POINT = 338;
	// 解锁称号槽
	public static final int AT_TITLE_UNLOCKSLOT = 339;
	// 使用修炼点道具
	public static final int AT_USE_ITEM_OFFLINE_FUNC_POINT = 340;
	// cdkey码兑换奖励
	public static final int AT_TAKE_GIFT_CDKEY_REWARD = 341;
	// 购买赌博商店商品
	public static final int AT_BUY_GAMBLE_SHOP_GOOGS = 342;
	// 刷新赌博商店商品
	public static final int AT_USER_REFRESH_GAMBLE_SHOP = 343;
	// 获取持续充值奖励
	public static final int AT_TAKE_LAST_PAY_GIFT_REWARD = 344;
	// 使用称号道具
	public static final int AT_USE_ITEM_TITLE = 345;
	// 获取活动挑战奖励
	public static final int AT_TAKE_ACT_CHALLENGE_GIFT = 346;
	// 基础货币快速购买
	public static final int AT_BUY_BASE_DUMMY_GOODS = 347;
	// 购买乾坤点
	public static final int AT_TRANSFER_POINT_BUY = 348;
	// 重置乾坤点
	public static final int AT_TRANSFER_POINT_RESET = 349;
	// 试炼补做
	public static final int AT_ACTIVITY_LAST_QUICK_FINISH = 350;
	// 婚礼预约
	public static final int AT_MARRIAGE_BESPEAK = 351;
	// 地图传送到太玄碑文
	public static final int AT_TELEPORT_STELE = 352;
	// 获得公测返现奖励
	public static final int AT_TAKE_PBTCASHBACK = 353;
	// 进入伏魔洞下一层
	public static final int AT_UP_DEMON_FLOOR = 354;
	// 购买升级特惠
	public static final int AT_BUY_UPGRADE_PURCHASE = 355;
	// 伏魔洞每层进入奖励
	public static final int AT_ENTER_DEMONHOLE_FLOOR = 356;
	// 玩转盘奖励
	public static final int AT_PLAY_LUCKYROLL = 357;
	// 送幸运星奖励
	public static final int AT_SEND_LUCKLY_STAR = 358;
	// 约战NPC奖励
	public static final int AT_FIGHT_NPC_REWARD = 359;
	// 领取分包奖励
	public static final int AT_TAKE_PACKET_REWARD = 360;
	// 增加活跃度
	public static final int AT_ADD_ACTIVITY = 361;
	// 解锁坐骑洗练属性
	public static final int AT_UNLOCK_HORSE_PROP = 362;
	// 乾坤点升级
	public static final int AT_LVLUP_TRANSFER_POINT = 363;
	// 使用绝技道具
	public static final int AT_USE_ITEM_USKILL = 364;
	// 购买休闲宠物时间
	public static final int AT_BUY_WIZARD_PET_TIME = 365;
	// 使用头像激活道具
	public static final int AT_USE_ITEM_HEAD = 366;
	// 使用NPC传送功能
	public static final int AT_NPC_TRANSFROM = 367;
	// 传世装备打造
	public static final int AT_MAKE_LEGEND_COST = 368;
	// 传世装备属性保存
	public static final int AT_MAKE_LEGEND_SAVE = 369;
	// 传世装备属性舍弃
	public static final int AT_MAKE_LEGEND_QUIT = 370;
	// 领取直购礼包奖励
	public static final int AT_TAKE_DIRECT_PURCHASE_REWARD = 371;
	// 领取特权卡奖励
	public static final int AT_TAKE_SPECIAL_CARD_REWARD = 372;
	// 抽老虎机奖励
	public static final int AT_PLAY_ONE_ARM_BANDIT = 373;
	// 红包保底奖励
	public static final int AT_GET_RED_ENEVLOPE_EMPTY_GIFT = 374;
	// 组队副本奖励
	public static final int AT_ON_NORMAL_MAP_END = 375;
	// 道具解锁头像
	public static final int AT_UNLOCK_ROLE_HEAD = 375;
	// NPC祈福
	public static final int AT_JOIN_NPC_PRAY = 376;
	// 塔防副本结算奖励
	public static final int AT_TD_MAP_FINISH_REWARD = 377;
	// 解锁私人仓库
	public static final int AT_UNLOCK_PRIVATE_WAREHOUSE = 378;
	// 炼化炉炼化道具
	public static final int AT_PRODUCE_FUSION = 379;

	// 月卡
	public static final int PAY_LEVEL_MONTHCARD = 0;
	// 6元档位
	public static final int PAY_LEVEL_1 = 1;
	// 30元档位
	public static final int PAY_LEVEL_2 = 2;
	// 68元档位
	public static final int PAY_LEVEL_3 = 3;
	// 128元档位
	public static final int PAY_LEVEL_4 = 4;
	// 258元档位
	public static final int PAY_LEVEL_5 = 5;
	// 648元档位
	public static final int PAY_LEVEL_6 = 6;
	// 充值返还
	public static final int PAY_LEVEL_RETURN = 100;
	// 系统赠送
	public static final int PAY_LEVEL_PRESENT = 101;

	// 开始任务
	public static final int TASKEVENT_START = 0;
	// 接取任务
	public static final int TASKEVENT_TAKE = 1;
	// 完成任务
	public static final int TASKEVENT_FINISH = 2;
	// 放弃任务
	public static final int TASKEVENT_CANCLE = 3;

	// 报名
	public static final int COPYEVENT_APPLY = -1;
	// 副本开始
	public static final int COPYEVENT_START = 0;
	// 副本结束
	public static final int COPYEVENT_FINISH = 1;
	// 扫荡副本
	public static final int COPYEVENT_SWEEP = 2;

	// 传送至boss点
	public static final int BOSSEVENT_TRANSTO = 1;
	// 对boss造成伤害
	public static final int BOSSEVENT_DAMAGE = 2;

	// 副本任务
	public static final int TASKTYPE_CARBON = 1;
	// 主线任务类型
	public static final int TASKTYPE_MAIN = 2;
	// 神兵任务类型
	public static final int TASKTYPE_GODWEAPON = 3;
	// 支线任务(生产)
	public static final int TASKTYPE_BRANCH_PRODUCE = 4;
	// 支线任务(帮派声讨堂)
	public static final int TASKTYPE_BRANCH_SECT = 5;
	// 支线任务(生产)
	public static final int TASKTYPE_BRANCH_PRODUCE2 = 6;
	// 支线任务(活动)
	public static final int TASKTYPE_BRANCH_ACTIVE = 7;
	// 支线任务(游历)
	public static final int TASKTYPE_BRANCH_TRAVEL = 8;
	// 支线任务(升星)
	public static final int TASKTYPE_BRANCH_UPSTAR = 9;
	// 日常试炼
	public static final int TASKTYPE_DAILYTRIAL = 10;
	// 每日活跃度任务
	public static final int TASKTYPE_DAILYSCHEDULE = 11;
	// 魔王试炼
	public static final int TASKTYPE_TRIAL_DEMON = 12;
	// 五绝试炼
	public static final int TASKTYPE_TRIAL_FIVEMOST = 13;
	// 宝藏图
	public static final int TASKTYPE_TREASUREMAP = 14;
	// 帮派-镖局
	public static final int TASKTYPE_SECT_DELIVER = 15;
	// 帮派-劫镖
	public static final int TASKTYPE_SECT_ROB = 16;
	// 帮派-凌霄阁
	public static final int TASKTYPE_SECT_LINGXIAO = 17;
	// 竞技场
	public static final int TASKTYPE_ARENA = 18;

	// 人物升级
	public static final int ROLE_STRENGTHEN = 1;
	// 角色转职
	public static final int ROLE_CLASSCHANGE = 2;
	// 坐骑洗练
	public static final int MOUNTS_XILIAN = 3;
	// 坐骑升星
	public static final int MOUNTS_STAR = 4;
	// 骑术升级
	public static final int MOUNTS_RIDING = 5;
	// 神兵升级
	public static final int WEAPON_LEVELUP = 6;
	// 神兵升星
	public static final int WEAPON_STARUP = 7;
	// 随从升级
	public static final int PET_LEVELUP = 8;
	// 随从升星
	public static final int PET_STARUP = 9;
	// 随从合修
	public static final int PET_HEXIU = 10;
	// 装备强化
	public static final int EQUIP_STRENGTHEN = 11;
	// 装备升星
	public static final int EQUIP_STARUP = 12;
	// 装备镶嵌
	public static final int EQUIP_EMBED = 13;
	// 装备升阶
	public static final int EQUIP_UPGRADE = 14;
	// 装备洗练
	public static final int EQUIP_XILIAN = 15;
	// 武功升级
	public static final int SKILL_LEVELUP = 16;
	// 武功境界
	public static final int SKILL_ENHANCE = 17;
	// 气功研读
	public static final int SPIRIT_LEVELUP = 18;
	// 绝技升级
	public static final int UNIQUESKILL_LEVELUP = 19;
	// 宝石升级
	public static final int GEM_LEVELUP = 20;
	// 历练藏书
	public static final int RAREBOOK_PUSH = 21;
	// 历练参悟
	public static final int GRASP_LEVELUP = 22;

	// 装备升级
	public static final int EQUIPSTRENGTH_LEVELUP = 0;
	// 装备升星
	public static final int EQUIPSTRENGTH_STARUP = 1;
	// 装备镶嵌
	public static final int EQUIPSTRENGTH_INLAY = 2;
	// 宝石升级
	public static final int EQUIPSTRENGTH_GEMUP = 3;
	// 装备精炼 3月24号后新加, 之前没有
	public static final int EQUIPSTRENGTH_REFINE = 4;
	// 穿上或者更换装备
	public static final int EQUIPSTRENGTH_EQUIP = 5;

	// 技能升级
	public static final int SKILL_LEVEL_UP = 1;
	// 技能升阶
	public static final int SKILL_RANK_UP = 2;
	// 技能解锁
	public static final int SKILL_UNLOCK = 3;
	// 藏书解锁
	public static final int RAREBOOK_UNLOCK = 4;
	// 藏书升级
	public static final int RAREBOOK_LEVEL_UP = 5;
	// 心法解锁
	public static final int SPIRIT_UNLOCK = 6;
	// 心法升级
	public static final int SPIRIT_LEVEL_UP = 7;
	// 绝技升级
	public static final int UNIQUESKILL_LEVEL_UP = 8;
	// 绝技装备
	public static final int UNIQUESKILL_SET = 9;

	// 随从解锁
	public static final int PET_UNLOCK = 1;
	// 随从升级
	public static final int PET_LEVEL_UP = 2;
	// 随从升星
	public static final int PET_STAR_UP = 3;
	// 随从合修
	public static final int PET_COPRATICE = 4;
	// 随从转职
	public static final int PET_TRANSFORM = 5;

	// 神兵解锁
	public static final int WEAPON_UNLOCK = 1;
	// 神兵升级
	public static final int WEAPON_LEVEL_UP = 2;
	// 神兵升星
	public static final int WEAPON_STAR_UP = 3;
	// 神兵天赋升级
	public static final int WEAPON_TALENT_UP = 4;
	// 神兵技能升级
	public static final int WEAPON_SKILL_UP = 5;
	// 神兵装备
	public static final int WEAPON_EQUIP = 6;

	// 内甲解锁
	public static final int ARMOR_UNLOCK = 1;
	// 内甲升级
	public static final int ARMOR_LEVEL_UP = 2;
	// 内甲升星
	public static final int ARMOR_STAR_UP = 3;
	// 内甲天赋升级
	public static final int ARMOR_TALENT_UP = 4;
	// 内甲技能升级
	public static final int ARMOR_SKILL_UP = 5;
	// 内甲神兵装备
	public static final int ARMOR_EQUIP = 6;

	// 坐骑激活
	public static final int HORSE_UNLOCK = 1;
	// 坐骑洗练
	public static final int HORSE_CLEAR = 2;
	// 坐骑升星
	public static final int HORSE_STAR_UP = 3;
	// 坐骑技能学习
	public static final int HORSE_SKILL_LEARN = 4;
	// 坐骑技能升级
	public static final int HORSE_SKILL_UP = 5;

	// 杂货商店
	public static final int NPC_STORE = 0;
	// 帮派商城
	public static final int SHOP_TYPE_SECT = 1;
	// 竞技商城
	public static final int SHOP_TYPE_ARENA = 2;
	// 会武商城
	public static final int SHOP_TYPE_SUPERARENA = 3;
	// 帮派运镖商城
	public static final int SHOP_TYPE_SECT_DELIVER = 4;
	// 正派赌博商店
	public static final int GAMBLE_SHOP_TYPE_JUSTICE = 11;
	// 邪派赌博商店
	public static final int GAMBLE_SHOP_TYPE_EVIL = 12;

	// 上架
	public static final int MARKET_TYPE_PUTON = 1;
	// 购买
	public static final int MARKET_TYPE_BUY = 2;

	// 制造
	public static final int PRODUCT_TYPE_CREATE = 1;
	// 分解
	public static final int PRODUCT_TYPE_SPLITE = 2;

	// 自己创建帮派
	public static final int JOINSECT_CREATESECT = 1;
	// 发出加入申请
	public static final int JOINSECT_SENDAPPLICATION = 2;
	// 被接纳入帮
	public static final int JOINSECT_BEACCEPTED = 3;

	// 开始竞技场
	public static final int ARENAEVENT_APPLY = -1;
	// 开始竞技场
	public static final int ARENAEVENT_START = 0;
	// 结束竞技场
	public static final int ARENAEVENT_FINISH = 1;

	// 开始竞技场
	public static final int SUPERARENA_2V2 = 0;
	// 结束竞技场
	public static final int SUPERARENA_4V4 = 1;

	// 一般竞技
	public static final int NORMAL = 0;
	// 正邪道场
	public static final int BWARENA = 1;

	// 帮派创建
	public static final int SECT_CREATE = 1;
	// 帮派升级
	public static final int SECT_LEVEL_UP = 2;
	// 帮派改名
	public static final int SECT_RENAME = 3;
	// 新成员加入帮派
	public static final int ROLE_JOIN_SECT = 4;
	// 成员离开帮派
	public static final int ROLE_LEAVE_SECT = 5;
	// 成员被踢出帮派
	public static final int ROLE_LEAVE_SECT_BY_KICK = 6;
	// 帮派解散
	public static final int SECT_DISMISS = 7;

	// 帮派个人本
	public static final int SECT_MAP_PERSON = 1;
	// 帮派团队本
	public static final int SECT_MAP_GROUP = 2;

	// 帮派活动开始
	public static final int SECT_ACT_START = 1;
	// 帮派活动失败
	public static final int SECT_ACT_FAIL = 2;
	// 帮派活动完成
	public static final int SECT_ACT_FINISH = 3;

	// 运镖
	public static final int SECT_DELIVER = 1;
	// 劫镖
	public static final int SECT_ROB = 2;

	// 创建自创武功
	public static final int SECT_DIY_SKILL_CREATE = 1;
	// 保存自创武功
	public static final int SECT_DIY_SKILL_SAVE = 2;
	// 遗忘自创武功
	public static final int SECT_DIY_SKILL_DISCARD = 3;
	// 分享自创武功
	public static final int SECT_DIY_SKILL_SHARE = 4;
	// 借用自创武功
	public static final int SECT_DIY_SKILL_BORROW = 5;

	// 赠送鸡腿
	public static final int INTERACTION_TYPE_SEND_VIT = 1;

	// 全服务器喊话
	public static final int CHAT_TYPE_ALLSERVER = -1;
	// 世界喊话
	public static final int CHAT_TYPE_WORLD = 0;
	// 帮派聊天
	public static final int CHAT_TYPE_SECT = 1;
	// 队伍聊天
	public static final int CHAT_TYPE_TEAM = 2;
	// 私聊
	public static final int CHAT_TYPE_PRIVATE = 3;
	// 战斗
	public static final int CHAT_TYPE_FIGHT = 4;

	// 求婚
	public static final int MARRIAGE_REQUEST = 0;
	// 回应求婚
	public static final int MARRIAGE_RESPONSE = 1;
	// 结婚游行
	public static final int MARRIAGE_WALK = 2;
	// 婚宴
	public static final int MARRIAGE_BANQUET = 3;
	// 离婚
	public static final int MARRIAGE_DIVORCE = 4;

	// 开始正义之心副本
	public static final int EMERGENCY_JOIN_MAP = 0;
	// 结束正义之心副本
	public static final int EMERGENCY_FINISH_MAP = 1;

	// 开始正义之心副本
	public static final int JUSTICE_JOIN_MAP = 0;
	// 结束正义之心副本
	public static final int JUSTICE_FINISH_MAP = 1;

	// 开始正义之心副本
	public static final int STELE_JOIN_EVENT = 0;
	// 结束正义之心副本
	public static final int STELE_FINISH_EVENT = 1;

	// 开始正义之心副本
	public static final int FIGHTNPC_JOIN_EVENT = 0;
	// 结束正义之心副本
	public static final int FIGHTNPC_FINISH_EVENT = 1;



	// (可选)角色创建
	public static class RoleCreate
	{

		public RoleCreate() { }

		public RoleCreate(String dtEventTime, int iSequence, int iGameSvrId, String vGameAppId, 
		                  String vOpenId, String vChannel, String vUId, int iRoleId, 
		                  String vRoleName, int iRoleType, int iGenderType, int iVipLvl, 
		                  String vLoginIp, String vMacAddr, String vDeviceId, String vSystemSoftware, 
		                  String vSystemHardware, String vCpuHardware, int vNetwork)
		{
			this.dtEventTime = dtEventTime;
			this.iSequence = iSequence;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.vRoleName = vRoleName;
			this.iRoleType = iRoleType;
			this.iGenderType = iGenderType;
			this.iVipLvl = iVipLvl;
			this.vLoginIp = vLoginIp;
			this.vMacAddr = vMacAddr;
			this.vDeviceId = vDeviceId;
			this.vSystemSoftware = vSystemSoftware;
			this.vSystemHardware = vSystemHardware;
			this.vCpuHardware = vCpuHardware;
			this.vNetwork = vNetwork;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleCreate");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iSequence);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(vRoleName);
			sb.append('|').append(iRoleType);
			sb.append('|').append(iGenderType);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(vLoginIp);
			sb.append('|').append(vMacAddr);
			sb.append('|').append(vDeviceId);
			sb.append('|').append(vSystemSoftware);
			sb.append('|').append(vSystemHardware);
			sb.append('|').append(vCpuHardware);
			sb.append('|').append(vNetwork);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (可选)同一个服务器上一段时间内唯一的事件序号,循环使用
		public int iSequence;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色名字
		public String vRoleName;
		// (必填)内部角色职业
		public int iRoleType;
		// (必填)内部角色性别
		public int iGenderType;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)登录ip
		public String vLoginIp;
		// (必填)mac地址
		public String vMacAddr;
		// (可选)设备Id
		public String vDeviceId;
		// (可选)移动终端操作系统版本
		public String vSystemSoftware;
		// (必填)移动终端机型
		public String vSystemHardware;
		// (可选)cpu类型|频率|核数
		public String vCpuHardware;
		// (可选)WIFI/2G/3G/4G
		public int vNetwork;
	}

	// (必填)角色登出
	public static class RoleLoginout
	{

		public RoleLoginout() { }

		public RoleLoginout(String dtEventTime, int iSequence, int iGameSvrId, String vGameAppId, 
		                    String vOpenId, String vChannel, String vUId, int iRoleId, 
		                    String dtCreateTime, String vRoleName, int iRoleType, int iTLvl, 
		                    int iBWType, int iLevel, int iVipLvl, int iRolePayPoint, 
		                    int iDiamondF, int iDiamondS, int iCoinF, int iCoinS, 
		                    int iSectId, int iSpouseId, int iFriendsNum, int iInOutType, 
		                    int iOnlineTime, String vClientVersion, String vLoginIP, String vMacAddr, 
		                    String vDeviceId, int vNetwork, String vSystemSoftware, String vSystemHardware, 
		                    String vCpuHardware)
		{
			this.dtEventTime = dtEventTime;
			this.iSequence = iSequence;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.vRoleName = vRoleName;
			this.iRoleType = iRoleType;
			this.iTLvl = iTLvl;
			this.iBWType = iBWType;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iRolePayPoint = iRolePayPoint;
			this.iDiamondF = iDiamondF;
			this.iDiamondS = iDiamondS;
			this.iCoinF = iCoinF;
			this.iCoinS = iCoinS;
			this.iSectId = iSectId;
			this.iSpouseId = iSpouseId;
			this.iFriendsNum = iFriendsNum;
			this.iInOutType = iInOutType;
			this.iOnlineTime = iOnlineTime;
			this.vClientVersion = vClientVersion;
			this.vLoginIP = vLoginIP;
			this.vMacAddr = vMacAddr;
			this.vDeviceId = vDeviceId;
			this.vNetwork = vNetwork;
			this.vSystemSoftware = vSystemSoftware;
			this.vSystemHardware = vSystemHardware;
			this.vCpuHardware = vCpuHardware;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleLoginout");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iSequence);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(vRoleName);
			sb.append('|').append(iRoleType);
			sb.append('|').append(iTLvl);
			sb.append('|').append(iBWType);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iRolePayPoint);
			sb.append('|').append(iDiamondF);
			sb.append('|').append(iDiamondS);
			sb.append('|').append(iCoinF);
			sb.append('|').append(iCoinS);
			sb.append('|').append(iSectId);
			sb.append('|').append(iSpouseId);
			sb.append('|').append(iFriendsNum);
			sb.append('|').append(iInOutType);
			sb.append('|').append(iOnlineTime);
			sb.append('|').append(vClientVersion);
			sb.append('|').append(vLoginIP);
			sb.append('|').append(vMacAddr);
			sb.append('|').append(vDeviceId);
			sb.append('|').append(vNetwork);
			sb.append('|').append(vSystemSoftware);
			sb.append('|').append(vSystemHardware);
			sb.append('|').append(vCpuHardware);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (可选)同一个服务器上一段时间内唯一的事件序号
		public int iSequence;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)内部角色名字
		public String vRoleName;
		// (必填)内部角色职业
		public int iRoleType;
		// (必填)角色转职等级
		public int iTLvl;
		// (必填)角色正邪阵营
		public int iBWType;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)角色充值数量
		public int iRolePayPoint;
		// (必填)角色当前非绑定钻石数量
		public int iDiamondF;
		// (必填)角色当前绑定钻石数量
		public int iDiamondS;
		// (必填)角色当前非绑定金币数量
		public int iCoinF;
		// (必填)角色当前绑定金币数量
		public int iCoinS;
		// (必填)所属帮派Id，无填0
		public int iSectId;
		// (必填)配偶ID, 无填0
		public int iSpouseId;
		// (必填)玩家好友数量
		public int iFriendsNum;
		// 0: logout , 1:new login 2: login reconnect 3:status change (level up etc...)
		public int iInOutType;
		// (必填)本次登录在线时间(秒)
		public int iOnlineTime;
		// (必填)客户端版本
		public String vClientVersion;
		// (必填)登录渠道
		public String vLoginIP;
		// (必填)mac地址
		public String vMacAddr;
		// (可选)设备Id
		public String vDeviceId;
		// (可选)WIFI/2G/3G/4G
		public int vNetwork;
		public String vSystemSoftware;
		public String vSystemHardware;
		public String vCpuHardware;
	}

	// (可选)角色游戏事件流水表
	public static class RoleEventFlow
	{

		public RoleEventFlow() { }

		public RoleEventFlow(String dtEventTime, int iSequence, int iGameSvrId, String vGameAppId, 
		                     String vOpenId, String vChannel, String vUId, int iRoleId, 
		                     int iLevel, int iVipLvl, int iEventId, String vConsumeItems, 
		                     String vProduceItems, int iArg1, int iArg2, int iArg3, 
		                     int iArg4, String vArg)
		{
			this.dtEventTime = dtEventTime;
			this.iSequence = iSequence;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iEventId = iEventId;
			this.vConsumeItems = vConsumeItems;
			this.vProduceItems = vProduceItems;
			this.iArg1 = iArg1;
			this.iArg2 = iArg2;
			this.iArg3 = iArg3;
			this.iArg4 = iArg4;
			this.vArg = vArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleEventFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iSequence);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iEventId);
			sb.append('|').append(vConsumeItems);
			sb.append('|').append(vProduceItems);
			sb.append('|').append(iArg1);
			sb.append('|').append(iArg2);
			sb.append('|').append(iArg3);
			sb.append('|').append(iArg4);
			sb.append('|').append(vArg);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (可选)用于关联一次动作产生多条不同类型的道具流动日志
		public int iSequence;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)游戏事件Id
		public int iEventId;
		// 涉及的变化的通用物品Id ，消耗数目， 消耗后数量，以逗号分隔，不同类型物品之间以分号分隔
		public String vConsumeItems;
		// 涉及的变化的通用物品Id ，产出数目， 产出后数量，以逗号分隔，不同类型物品之间以分号分隔
		public String vProduceItems;
		// (可选)游戏事件描述参数1,根据具体iEvnetId的不同有不同的意义,如完成战役事件在此含义为战役类型
		public int iArg1;
		// (可选)游戏事件描述参数2,根据具体iEvnetId的不同有不同的意义,如完成战役事件在此含义为战役序号
		public int iArg2;
		// (可选)游戏事件描述参数3,根据具体iEvnetId的不同有不同的意义,如完成战役事件在此含义为关卡序号
		public int iArg3;
		// (可选)游戏事件描述参数4,根据具体iEvnetId的不同有不同的意义,如完成战役事件在此含义为星级
		public int iArg4;
		// (可选)游戏事件字符串描述参数
		public String vArg;
	}

	// (可选)角色(绑定)资金流动流水表
	public static class RoleCurrencyChangeFlow_R
	{

		public RoleCurrencyChangeFlow_R() { }

		public RoleCurrencyChangeFlow_R(String dtEventTime, int iSequence, int iGameSvrId, String vGameAppId, 
		                                String vOpenId, String vChannel, String vUId, int iRoleId, 
		                                int iLevel, int iVipLvl, int iEventId, int iItemId, 
		                                int iChangeCnt, int iFinalCnt)
		{
			this.dtEventTime = dtEventTime;
			this.iSequence = iSequence;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iEventId = iEventId;
			this.iItemId = iItemId;
			this.iChangeCnt = iChangeCnt;
			this.iFinalCnt = iFinalCnt;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleCurrencyChangeFlow_R");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iSequence);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iEventId);
			sb.append('|').append(iItemId);
			sb.append('|').append(iChangeCnt);
			sb.append('|').append(iFinalCnt);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (可选)用于关联一次动作产生多条不同类型的道具流动日志
		public int iSequence;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)游戏事件Id
		public int iEventId;
		// 涉及的变化的通用物品Id ，消耗数目， 消耗后数量，以逗号分隔，不同类型物品之间以分号分隔
		public int iItemId;
		// 涉及的变化的通用物品Id ，产出数目， 产出后数量，以逗号分隔，不同类型物品之间以分号分隔
		public int iChangeCnt;
		// 变动后剩余度
		public int iFinalCnt;
	}

	// (可选)角色(非绑定)资金流动流水表
	public static class RoleCurrencyChangeFlow_F
	{

		public RoleCurrencyChangeFlow_F() { }

		public RoleCurrencyChangeFlow_F(String dtEventTime, int iSequence, int iGameSvrId, String vGameAppId, 
		                                String vOpenId, String vChannel, String vUId, int iRoleId, 
		                                int iLevel, int iVipLvl, int iEventId, int iItemId, 
		                                int iChangeCnt, int iFinalCnt)
		{
			this.dtEventTime = dtEventTime;
			this.iSequence = iSequence;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iEventId = iEventId;
			this.iItemId = iItemId;
			this.iChangeCnt = iChangeCnt;
			this.iFinalCnt = iFinalCnt;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleCurrencyChangeFlow_F");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iSequence);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iEventId);
			sb.append('|').append(iItemId);
			sb.append('|').append(iChangeCnt);
			sb.append('|').append(iFinalCnt);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (可选)用于关联一次动作产生多条不同类型的道具流动日志
		public int iSequence;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)游戏事件Id
		public int iEventId;
		// 涉及的变化的通用物品Id ，消耗数目， 消耗后数量，以逗号分隔，不同类型物品之间以分号分隔
		public int iItemId;
		// 涉及的变化的通用物品Id ，产出数目， 产出后数量，以逗号分隔，不同类型物品之间以分号分隔
		public int iChangeCnt;
		// 变动后剩余度
		public int iFinalCnt;
	}

	// (可选)角色游戏道具变化流水表
	public static class RoleItemsFlow
	{

		public RoleItemsFlow() { }

		public RoleItemsFlow(String dtEventTime, int iSequence, int iGameSvrId, String vGameAppId, 
		                     String vOpenId, String vChannel, String vUId, int iRoleId, 
		                     int iLevel, int iVipLvl, int iEventId, int iItemId, 
		                     int iChange, int iFinal)
		{
			this.dtEventTime = dtEventTime;
			this.iSequence = iSequence;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iEventId = iEventId;
			this.iItemId = iItemId;
			this.iChange = iChange;
			this.iFinal = iFinal;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleItemsFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iSequence);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iEventId);
			sb.append('|').append(iItemId);
			sb.append('|').append(iChange);
			sb.append('|').append(iFinal);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (可选)用于关联一次动作产生多条不同类型的道具流动日志
		public int iSequence;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)游戏事件Id
		public int iEventId;
		// 涉及的变化的通用物品Id
		public int iItemId;
		// 涉及的变化的通用物品数量
		public int iChange;
		// 涉及的变化的通用物品最终数量
		public int iFinal;
	}

	// 玩家充值流水表
	public static class RolePayFlow
	{

		public RolePayFlow() { }

		public RolePayFlow(String dtEventTime, int iSequence, int iGameSvrId, String vGameAppId, 
		                   String vOpenId, String vChannel, String vUId, int iRoleId, 
		                   String dtCreateTime, int iLevel, int iVipLvlBefore, int iVipLvlFinal, 
		                   int iAddPayPoint, int iUserPayPointFinal, int iRolePayPointFinal, int iPayLvl, 
		                   int iPayLvlTimes, int iPayAmount, int iAddDiamondF, int iDiamondF, 
		                   String vSerial)
		{
			this.dtEventTime = dtEventTime;
			this.iSequence = iSequence;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvlBefore = iVipLvlBefore;
			this.iVipLvlFinal = iVipLvlFinal;
			this.iAddPayPoint = iAddPayPoint;
			this.iUserPayPointFinal = iUserPayPointFinal;
			this.iRolePayPointFinal = iRolePayPointFinal;
			this.iPayLvl = iPayLvl;
			this.iPayLvlTimes = iPayLvlTimes;
			this.iPayAmount = iPayAmount;
			this.iAddDiamondF = iAddDiamondF;
			this.iDiamondF = iDiamondF;
			this.vSerial = vSerial;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RolePayFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iSequence);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvlBefore);
			sb.append('|').append(iVipLvlFinal);
			sb.append('|').append(iAddPayPoint);
			sb.append('|').append(iUserPayPointFinal);
			sb.append('|').append(iRolePayPointFinal);
			sb.append('|').append(iPayLvl);
			sb.append('|').append(iPayLvlTimes);
			sb.append('|').append(iPayAmount);
			sb.append('|').append(iAddDiamondF);
			sb.append('|').append(iDiamondF);
			sb.append('|').append(vSerial);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (可选)同一个服务器上一段时间内唯一的事件序号
		public int iSequence;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)当前触发充值内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)用户充值前vip等级
		public int iVipLvlBefore;
		// (必填)用户充值后vip等级
		public int iVipLvlFinal;
		// (必填)充值获得的vip点数
		public int iAddPayPoint;
		// (必填)充值后账号的总vip点数
		public int iUserPayPointFinal;
		// (必填)充值后角色的总vip点数
		public int iRolePayPointFinal;
		// (必填)充值档位 PAYLEVELTYPE
		public int iPayLvl;
		// (必填)充值档位 充值次数
		public int iPayLvlTimes;
		// (必填)充值金额(单位美分)
		public int iPayAmount;
		// (必填)获取到非绑定钻石数
		public int iAddDiamondF;
		// (必填)用户最终的非绑定钻石数量
		public int iDiamondF;
		// (必填)充值订单号
		public String vSerial;
	}

	// (必填)服务器状态流水，每分钟一条日志
	public static class GameSvrState
	{

		public GameSvrState() { }

		public GameSvrState(String dtEventTime, int iGameSvrId, String vGameAppId, int iOnlineCount, 
		                    String vGameIP)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.iOnlineCount = iOnlineCount;
			this.vGameIP = vGameIP;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("GameSvrState");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(iOnlineCount);
			sb.append('|').append(vGameIP);
			sb.append('\n');
			return sb.toString();
		}

		// (必填) 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)运行的游戏服务器编号
		public int iGameSvrId;
		// (必填)运行的游戏编号
		public String vGameAppId;
		// (必填)运行的游戏服务器上当前在线人数
		public int iOnlineCount;
		// (必填)服务器IP
		public String vGameIP;
	}

	// idip命令流水
	public static class IdIPCmdFlow
	{

		public IdIPCmdFlow() { }

		public IdIPCmdFlow(String dtEventTime, int iGameSvrId, int iRoleId, int iItemId, 
		                   int iItemCount, int iSource, int iCmd, String vSerial)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.iRoleId = iRoleId;
			this.iItemId = iItemId;
			this.iItemCount = iItemCount;
			this.iSource = iSource;
			this.iCmd = iCmd;
			this.vSerial = vSerial;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("IdIPCmdFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iItemId);
			sb.append('|').append(iItemCount);
			sb.append('|').append(iSource);
			sb.append('|').append(iCmd);
			sb.append('|').append(vSerial);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 内部角色Id
		public int iRoleId;
		// 道具id
		public int iItemId;
		// 道具数量
		public int iItemCount;
		// 渠道号
		public int iSource;
		// 命令字
		public int iCmd;
		// 流水号
		public String vSerial;
	}

	// 角色主线任务流水表
	public static class RoleMainTaskFlow
	{

		public RoleMainTaskFlow() { }

		public RoleMainTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                        String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                        int iLevel, int iVipLvl, int iTaskId, int iTaskState, 
		                        int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleMainTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)主线任务Id
		public int iTaskId;
		// (必填)事件后主线任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// (必填)主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色支线任务流水表
	public static class RoleBranchTaskFlow
	{

		public RoleBranchTaskFlow() { }

		public RoleBranchTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                          String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                          int iLevel, int iVipLvl, int iGroupId, int iTaskId, 
		                          int iTaskState, int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iGroupId = iGroupId;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleBranchTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iGroupId);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)分组Id
		public int iGroupId;
		// (必填)任务Id
		public int iTaskId;
		// (必填)事件后任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// (必填)主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色帮派任务流水表
	public static class RoleSectTaskFlow
	{

		public RoleSectTaskFlow() { }

		public RoleSectTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                        String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                        int iLevel, int iVipLvl, int iSectId, int iTaskId, 
		                        int iTaskState, int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iSectId = iSectId;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSectTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iSectId);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)帮派Id
		public int iSectId;
		// (必填)任务Id
		public int iTaskId;
		// (必填)事件后任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// (必填)主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色帮派任务流水表
	public static class RoleSectDeliverTaskFlow
	{

		public RoleSectDeliverTaskFlow() { }

		public RoleSectDeliverTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                               String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                               int iLevel, int iVipLvl, int iSectId, int iTaskId, 
		                               int iTaskState, int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iSectId = iSectId;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSectDeliverTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iSectId);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)帮派Id
		public int iSectId;
		// (必填)任务Id
		public int iTaskId;
		// (必填)事件后任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// (必填)主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色劫镖流水
	public static class RoleSectRobTaskFlow
	{

		public RoleSectRobTaskFlow() { }

		public RoleSectRobTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                           String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                           int iLevel, int iVipLvl, int iSectId, int iTaskId, 
		                           int iTaskState, int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iSectId = iSectId;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSectRobTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iSectId);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)帮派Id
		public int iSectId;
		// (必填)任务Id
		public int iTaskId;
		// (必填)事件后任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// (必填)主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色日常任务流水
	public static class RoleDailyTaskFlow
	{

		public RoleDailyTaskFlow() { }

		public RoleDailyTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                         String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                         int iLevel, int iVipLvl, int iTaskId, int iTaskState, 
		                         int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleDailyTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏APpId
		public String vGameAppId;
		// 用户OpenId号
		public String vOpenId;
		// 用户channel名
		public String vChannel;
		// 用户UId号
		public String vUId;
		// 内部角色Id
		public int iRoleId;
		// 内部角色Id
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// 角色vip等级
		public int iVipLvl;
		// 任务Id
		public int iTaskId;
		// 事件后任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// 主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色日常任务流水
	public static class RoleScheduleTaskFlow
	{

		public RoleScheduleTaskFlow() { }

		public RoleScheduleTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                            String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                            int iLevel, int iVipLvl, int iTaskId, int iTaskState, 
		                            int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleScheduleTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏APpId
		public String vGameAppId;
		// 用户OpenId号
		public String vOpenId;
		// 用户channel名
		public String vChannel;
		// 用户UId号
		public String vUId;
		// 内部角色Id
		public int iRoleId;
		// 内部角色Id
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// 角色vip等级
		public int iVipLvl;
		// 任务Id
		public int iTaskId;
		// 事件后任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// 主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色副本任务流水表
	public static class RolePrivateNormalCopyFlow
	{

		public RolePrivateNormalCopyFlow() { }

		public RolePrivateNormalCopyFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                                 String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                                 int iLevel, int iVipLvl, int iMapId, int iEventType, 
		                                 int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iMapId = iMapId;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RolePrivateNormalCopyFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iMapId);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏APpId
		public String vGameAppId;
		// 用户OpenId号
		public String vOpenId;
		// 用户channel名
		public String vChannel;
		// 用户UId号
		public String vUId;
		// 内部角色Id
		public int iRoleId;
		// 内部角色Id
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// 角色vip等级
		public int iVipLvl;
		// 主线任务Id
		public int iMapId;
		// 主线任务事件类型 (0:进入 1:扫荡, 2:离开)
		public int iEventType;
		public int iArg;
	}

	// 角色副本任务流水表
	public static class RolePublicNormalCopyFlow
	{

		public RolePublicNormalCopyFlow() { }

		public RolePublicNormalCopyFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                                String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                                int iLevel, int iVipLvl, int iMapId, int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iMapId = iMapId;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RolePublicNormalCopyFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iMapId);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏APpId
		public String vGameAppId;
		// 用户OpenId号
		public String vOpenId;
		// 用户channel名
		public String vChannel;
		// 用户UId号
		public String vUId;
		// 内部角色Id
		public int iRoleId;
		// 内部角色Id
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// 角色vip等级
		public int iVipLvl;
		// 主线任务Id
		public int iMapId;
		// 主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// 角色活动副本流水表
	public static class RoleActiveCopyFlow
	{

		public RoleActiveCopyFlow() { }

		public RoleActiveCopyFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                          String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                          int iLevel, int iVipLvl, int iMapId, int iEventType, 
		                          int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iMapId = iMapId;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleActiveCopyFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iMapId);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)主线任务Id
		public int iMapId;
		// 副本动作(0:开始新副本 1:扫荡副本 2:完成副本)
		public int iEventType;
		public int iArg;
	}

	// 角色活动副本流水表
	public static class RoleChallengeTaskFlow
	{

		public RoleChallengeTaskFlow() { }

		public RoleChallengeTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                             String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                             int iLevel, int iVipLvl, int iTaskId, int iTaskState, 
		                             int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iTaskId = iTaskId;
			this.iTaskState = iTaskState;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleChallengeTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iTaskId);
			sb.append('|').append(iTaskState);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// (必填)游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// (必填)游戏APpId
		public String vGameAppId;
		// (必填)用户OpenId号
		public String vOpenId;
		// (必填)用户channel名
		public String vChannel;
		// (必填)用户UId号
		public String vUId;
		// (必填)内部角色Id
		public int iRoleId;
		// (必填)内部角色Id
		public String dtCreateTime;
		// (必填)等级
		public int iLevel;
		// (必填)角色vip等级
		public int iVipLvl;
		// (必填)主线任务Id
		public int iTaskId;
		// (必填)事件后主线任务的状态 (0:未接取1:接取2:完成但未领奖)
		public int iTaskState;
		// (必填)主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iEventType;
	}

	// boss战记录
	public static class RoleBossTaskFlow
	{

		public RoleBossTaskFlow() { }

		public RoleBossTaskFlow(String dtEventTime, int iGameSvrId, String vGameAppId, String vOpenId, 
		                        String vChannel, String vUId, int iRoleId, String dtCreateTime, 
		                        int iLevel, int iVipLvl, int iBossId, int iTaskType, 
		                        int iKill, int iDamage)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameAppId = vGameAppId;
			this.vOpenId = vOpenId;
			this.vChannel = vChannel;
			this.vUId = vUId;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iBossId = iBossId;
			this.iTaskType = iTaskType;
			this.iKill = iKill;
			this.iDamage = iDamage;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleBossTaskFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameAppId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannel);
			sb.append('|').append(vUId);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iBossId);
			sb.append('|').append(iTaskType);
			sb.append('|').append(iKill);
			sb.append('|').append(iDamage);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏APpId
		public String vGameAppId;
		// 用户OpenId号
		public String vOpenId;
		// 用户channel名
		public String vChannel;
		// 用户UId号
		public String vUId;
		// 内部角色Id
		public int iRoleId;
		// 内部角色Id
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// 角色vip等级
		public int iVipLvl;
		// 主线任务Id
		public int iBossId;
		// 记录类型 (0:传送至boss点 1:对boss造成伤害)
		public int iTaskType;
		// 主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iKill;
		// 主线任务事件类型 (0:开始新任务1:接取任务2:完成任务3:取消任务)
		public int iDamage;
	}

	// 新手引导记录
	public static class RoleGuideFlow
	{

		public RoleGuideFlow() { }

		public RoleGuideFlow(String dtEventTime, String vGameId, int iGameSvrId, String vOpenid, 
		                     String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                     int iLevel, int iVipLevel, int iGuideId)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.iGameSvrId = iGameSvrId;
			this.vOpenid = vOpenid;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iGuideId = iGuideId;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleGuideFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vOpenid);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iGuideId);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 用户Openid号
		public String vOpenid;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 任务id
		public int iGuideId;
	}

	// 消费, 购买记录
	public static class RoleEquipStrengthenFlow
	{

		public RoleEquipStrengthenFlow() { }

		public RoleEquipStrengthenFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenid, 
		                               String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                               int iVip, int iLevel, int iType, int iPosition, 
		                               int iSlot, int iTarLevel, int iSuccess)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenid = vOpenid;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iVip = iVip;
			this.iLevel = iLevel;
			this.iType = iType;
			this.iPosition = iPosition;
			this.iSlot = iSlot;
			this.iTarLevel = iTarLevel;
			this.iSuccess = iSuccess;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleEquipStrengthenFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenid);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iVip);
			sb.append('|').append(iLevel);
			sb.append('|').append(iType);
			sb.append('|').append(iPosition);
			sb.append('|').append(iSlot);
			sb.append('|').append(iTarLevel);
			sb.append('|').append(iSuccess);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// 用户Openid号
		public String vOpenid;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 内部角色Id
		public String dtCreateTime;
		// ip等级
		public int iVip;
		// 角色等级
		public int iLevel;
		// 强化类型  0:升级, 1:升星
		public int iType;
		// 装备部位
		public int iPosition;
		// 孔位
		public int iSlot;
		// 目标等级
		public int iTarLevel;
		// 是否成功, 仅针对升星有效, 0:失败, 1:成功
		public int iSuccess;
	}

	// 消费, 购买记录
	public static class RoleSkillDevelopFlow
	{

		public RoleSkillDevelopFlow() { }

		public RoleSkillDevelopFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                            String vChannelId, String vUid, int iRoleId, int iVip, 
		                            String dtCreateTime, int iSkillId, int iType, int iAfterLevel)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iVip = iVip;
			this.dtCreateTime = dtCreateTime;
			this.iSkillId = iSkillId;
			this.iType = iType;
			this.iAfterLevel = iAfterLevel;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSkillDevelopFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iVip);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iSkillId);
			sb.append('|').append(iType);
			sb.append('|').append(iAfterLevel);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// 用户OpenId
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// vip等级
		public int iVip;
		// 角色创建时间
		public String dtCreateTime;
		// 技能ID
		public int iSkillId;
		// 升级类型
		public int iType;
		// 完成后数值
		public int iAfterLevel;
	}

	// 消费, 购买记录
	public static class RolePetDevelopFlow
	{

		public RolePetDevelopFlow() { }

		public RolePetDevelopFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                          String vChannelId, String vUid, int iRoleId, int iVip, 
		                          String dtCreateTime, int iPetId, int iType, int iAfterLevel)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iVip = iVip;
			this.dtCreateTime = dtCreateTime;
			this.iPetId = iPetId;
			this.iType = iType;
			this.iAfterLevel = iAfterLevel;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RolePetDevelopFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iVip);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iPetId);
			sb.append('|').append(iType);
			sb.append('|').append(iAfterLevel);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// vip等级
		public int iVip;
		// 角色创建时间
		public String dtCreateTime;
		// 随从ID
		public int iPetId;
		// 升级类型
		public int iType;
		// 完成后数值
		public int iAfterLevel;
	}

	// 消费, 购买记录
	public static class RoleWeaponDevelopFlow
	{

		public RoleWeaponDevelopFlow() { }

		public RoleWeaponDevelopFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                             String vChannelId, String vUid, int iRoleId, int iVip, 
		                             String dtCreateTime, int iWeaponId, int iType, int iAfterLevel)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iVip = iVip;
			this.dtCreateTime = dtCreateTime;
			this.iWeaponId = iWeaponId;
			this.iType = iType;
			this.iAfterLevel = iAfterLevel;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleWeaponDevelopFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iVip);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iWeaponId);
			sb.append('|').append(iType);
			sb.append('|').append(iAfterLevel);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// vip等级
		public int iVip;
		// 角色创建时间
		public String dtCreateTime;
		// 神兵ID
		public int iWeaponId;
		// 升级类型
		public int iType;
		// 完成后数值
		public int iAfterLevel;
	}

	// 内甲强化流水
	public static class RoleArmorDevelopFlow
	{

		public RoleArmorDevelopFlow() { }

		public RoleArmorDevelopFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                            String vChannelId, String vUid, int iRoleId, int iVip, 
		                            String dtCreateTime, int iArmorId, int iType, int iAfterLevel)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iVip = iVip;
			this.dtCreateTime = dtCreateTime;
			this.iArmorId = iArmorId;
			this.iType = iType;
			this.iAfterLevel = iAfterLevel;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleArmorDevelopFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iVip);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iArmorId);
			sb.append('|').append(iType);
			sb.append('|').append(iAfterLevel);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// vip等级
		public int iVip;
		// 角色创建时间
		public String dtCreateTime;
		// 神兵ID
		public int iArmorId;
		// 升级类型
		public int iType;
		// 完成后数值
		public int iAfterLevel;
	}

	// 消费, 购买记录
	public static class RoleHorseDevelopFlow
	{

		public RoleHorseDevelopFlow() { }

		public RoleHorseDevelopFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                            String vChannelId, String vUid, int iRoleId, int iVip, 
		                            String dtCreateTime, int iHorseId, int iType, int iSkillId, 
		                            int iAfterLevel)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iVip = iVip;
			this.dtCreateTime = dtCreateTime;
			this.iHorseId = iHorseId;
			this.iType = iType;
			this.iSkillId = iSkillId;
			this.iAfterLevel = iAfterLevel;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleHorseDevelopFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iVip);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iHorseId);
			sb.append('|').append(iType);
			sb.append('|').append(iSkillId);
			sb.append('|').append(iAfterLevel);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// vip等级
		public int iVip;
		// 角色创建时间
		public String dtCreateTime;
		// 随从ID
		public int iHorseId;
		// 升级类型
		public int iType;
		// 升级技能ID，若非技能操作为0
		public int iSkillId;
		// 完成后数值
		public int iAfterLevel;
	}

	// 消费, 购买记录
	public static class RoleBuyStoreFlow
	{

		public RoleBuyStoreFlow() { }

		public RoleBuyStoreFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                        String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                        int iLevel, int iVipLvl, int iStoreType, int iConsumeId, 
		                        int iConsumeCnt, int iProduceId, int iProduceCnt, int iProduceItemId, 
		                        int iProduceItemCnt)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iStoreType = iStoreType;
			this.iConsumeId = iConsumeId;
			this.iConsumeCnt = iConsumeCnt;
			this.iProduceId = iProduceId;
			this.iProduceCnt = iProduceCnt;
			this.iProduceItemId = iProduceItemId;
			this.iProduceItemCnt = iProduceItemCnt;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleBuyStoreFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iStoreType);
			sb.append('|').append(iConsumeId);
			sb.append('|').append(iConsumeCnt);
			sb.append('|').append(iProduceId);
			sb.append('|').append(iProduceCnt);
			sb.append('|').append(iProduceItemId);
			sb.append('|').append(iProduceItemCnt);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// vip等级
		public int iVipLvl;
		// 商店类型
		public int iStoreType;
		// 消耗ID
		public int iConsumeId;
		// 消耗数量
		public int iConsumeCnt;
		// 商品ID
		public int iProduceId;
		// 商品数量
		public int iProduceCnt;
		// 产出物品ID
		public int iProduceItemId;
		// 产出物品数量
		public int iProduceItemCnt;
	}

	// 商城消费, 购买记录
	public static class RoleBuyMallFlow
	{

		public RoleBuyMallFlow() { }

		public RoleBuyMallFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                       String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                       int iLevel, int iVipLvl, int iConsumeId, int iConsumeCnt, 
		                       int iProduceId, int iProduceCnt, int iProduceItemId, int iProduceItemCnt)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iConsumeId = iConsumeId;
			this.iConsumeCnt = iConsumeCnt;
			this.iProduceId = iProduceId;
			this.iProduceCnt = iProduceCnt;
			this.iProduceItemId = iProduceItemId;
			this.iProduceItemCnt = iProduceItemCnt;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleBuyMallFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iConsumeId);
			sb.append('|').append(iConsumeCnt);
			sb.append('|').append(iProduceId);
			sb.append('|').append(iProduceCnt);
			sb.append('|').append(iProduceItemId);
			sb.append('|').append(iProduceItemCnt);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// vip等级
		public int iVipLvl;
		// 消耗ID
		public int iConsumeId;
		// 消耗数量
		public int iConsumeCnt;
		// 商品ID
		public int iProduceId;
		// 商品数量
		public int iProduceCnt;
		// 商品对应道具ID
		public int iProduceItemId;
		// 商品对应道具数量
		public int iProduceItemCnt;
	}

	// 消费, 购买记录
	public static class RoleMarketStoreFlow
	{

		public RoleMarketStoreFlow() { }

		public RoleMarketStoreFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                           String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                           int iLevel, int iVipLvl, int iType, int iItem, 
		                           int iPrice, int iNum, int iStatus)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iType = iType;
			this.iItem = iItem;
			this.iPrice = iPrice;
			this.iNum = iNum;
			this.iStatus = iStatus;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleMarketStoreFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iType);
			sb.append('|').append(iItem);
			sb.append('|').append(iPrice);
			sb.append('|').append(iNum);
			sb.append('|').append(iStatus);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// vip等级
		public int iVipLvl;
		// 上架/购买商品
		public int iType;
		// 商品Id
		public int iItem;
		// 上架/成交 价格
		public int iPrice;
		// 上架/成交 数量
		public int iNum;
		// 是否交易成功
		public int iStatus;
	}

	// 消费, 购买记录
	public static class RoleProduceFlow
	{

		public RoleProduceFlow() { }

		public RoleProduceFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                       String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                       int iLevel, int iVipLvl, int iProductLevel, int iProductType, 
		                       int iItemType, int iItemId, int iItemNum)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iProductLevel = iProductLevel;
			this.iProductType = iProductType;
			this.iItemType = iItemType;
			this.iItemId = iItemId;
			this.iItemNum = iItemNum;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleProduceFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iProductLevel);
			sb.append('|').append(iProductType);
			sb.append('|').append(iItemType);
			sb.append('|').append(iItemId);
			sb.append('|').append(iItemNum);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// vip等级
		public int iVipLvl;
		// 生产等级
		public int iProductLevel;
		// 生产类型
		public int iProductType;
		// 武器,防具,神兵,随从,药品,杂物
		public int iItemType;
		// 制造消耗的物品
		public int iItemId;
		// 操作物品数量
		public int iItemNum;
	}

	public static class RoleJoinSectFlow
	{

		public RoleJoinSectFlow() { }

		public RoleJoinSectFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                        String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                        int iLevel, int iVipLvl, int iTargetSect, int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLvl = iVipLvl;
			this.iTargetSect = iTargetSect;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleJoinSectFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLvl);
			sb.append('|').append(iTargetSect);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 等级
		public int iLevel;
		// vip等级
		public int iVipLvl;
		// 目标帮会
		public int iTargetSect;
		// 事件类型
		public int iEventType;
	}

	// 竞技场记录
	public static class ArenaRank
	{

		public ArenaRank() { }

		public ArenaRank(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                 String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                 int iLevel, int iVip, int iRank, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVip = iVip;
			this.iRank = iRank;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("ArenaRank");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVip);
			sb.append('|').append(iRank);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 内部角色等级
		public int iLevel;
		// 角色Vip等级
		public int iVip;
		// 排名
		public int iRank;
		// 其他参数信息
		public int iArg;
	}

	// 竞技场记录
	public static class ArenaFlow
	{

		public ArenaFlow() { }

		public ArenaFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                 String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                 int iLevel, int iVip, int iEventType, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVip = iVip;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("ArenaFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVip);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 内部角色等级
		public int iLevel;
		// 角色Vip等级
		public int iVip;
		// 事件类型  0开始 : 1 结束
		public int iEventType;
		// 是否成功 0失败  1 成功
		public int iArg;
	}

	// 竞技场记录
	public static class SuperArenaFlow
	{

		public SuperArenaFlow() { }

		public SuperArenaFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                      String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                      int iLevel, int iVip, int iType, int iEventType, 
		                      int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVip = iVip;
			this.iType = iType;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("SuperArenaFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVip);
			sb.append('|').append(iType);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 内部角色等级
		public int iLevel;
		// 角色Vip等级
		public int iVip;
		// 竞技类型
		public int iType;
		// 事件类型  0开始 : 1 结束
		public int iEventType;
		// 是否成功 0失败   1 成功
		public int iArg;
	}

	// 势力战记录
	public static class ForceWarFlow
	{

		public ForceWarFlow() { }

		public ForceWarFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                    String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                    int iLevel, int iVip, int iBW, int iEventType, 
		                    int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVip = iVip;
			this.iBW = iBW;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("ForceWarFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVip);
			sb.append('|').append(iBW);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 内部角色等级
		public int iLevel;
		// 角色Vip等级
		public int iVip;
		// 正邪
		public int iBW;
		// 事件类型  0开始 : 1 结束
		public int iEventType;
		// 是否成功 0失败   1 成功  / 单人组队 (报名时)0单人 1组队
		public int iArg;
	}

	// 正邪战记录
	public static class BWArenaWarFlow
	{

		public BWArenaWarFlow() { }

		public BWArenaWarFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                      String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                      int iLevel, int iVip, int iBW, int iEventType, 
		                      int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVip = iVip;
			this.iBW = iBW;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("BWArenaWarFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVip);
			sb.append('|').append(iBW);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 内部角色等级
		public int iLevel;
		// 角色Vip等级
		public int iVip;
		// 正邪
		public int iBW;
		// 事件类型  0开始 : 1 结束
		public int iEventType;
		// 是否成功 0失败   1 成功
		public int iArg;
	}

	// 势力战记录
	public static class ClimbCopyFlow
	{

		public ClimbCopyFlow() { }

		public ClimbCopyFlow(String dtEventTime, int iGameSvrId, String vGameId, String vOpenId, 
		                     String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                     int iLevel, int iVip, int iGroupId, int iMapId, 
		                     int iEventType, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVip = iVip;
			this.iGroupId = iGroupId;
			this.iMapId = iMapId;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("ClimbCopyFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVip);
			sb.append('|').append(iGroupId);
			sb.append('|').append(iMapId);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 内部角色等级
		public int iLevel;
		// 角色Vip等级
		public int iVip;
		// 分组
		public int iGroupId;
		public int iMapId;
		// 事件类型  0开始 : 1 结束
		public int iEventType;
		// 是否成功 0失败   1 成功
		public int iArg;
	}

	// 帮派事件记录
	public static class SectEventFlow
	{

		public SectEventFlow() { }

		public SectEventFlow(String dtEventTime, int iGameSvrId, int iSectId, String vSectName, 
		                     int iMemberCnt, int iSectLevel, int iEventType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.iSectId = iSectId;
			this.vSectName = vSectName;
			this.iMemberCnt = iMemberCnt;
			this.iSectLevel = iSectLevel;
			this.iEventType = iEventType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("SectEventFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(iSectId);
			sb.append('|').append(vSectName);
			sb.append('|').append(iMemberCnt);
			sb.append('|').append(iSectLevel);
			sb.append('|').append(iEventType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 工会Id
		public int iSectId;
		// 工会名称
		public String vSectName;
		// 成员数
		public int iMemberCnt;
		// 帮派等级
		public int iSectLevel;
		// 帮派事件类型
		public int iEventType;
	}

	// 帮派副本记录
	public static class RoleSectMapJoinFlow
	{

		public RoleSectMapJoinFlow() { }

		public RoleSectMapJoinFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                           String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                           int iLevel, int iVipLevel, int iSectId, int iMapLevel, 
		                           int iMapId, int iMapType)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iSectId = iSectId;
			this.iMapLevel = iMapLevel;
			this.iMapId = iMapId;
			this.iMapType = iMapType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSectMapJoinFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iSectId);
			sb.append('|').append(iMapLevel);
			sb.append('|').append(iMapId);
			sb.append('|').append(iMapType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 帮派Id
		public int iSectId;
		// 地图层级
		public int iMapLevel;
		// 地图ID
		public int iMapId;
		// 地图类型
		public int iMapType;
	}

	// 帮派副本记录
	public static class SectMapFlow
	{

		public SectMapFlow() { }

		public SectMapFlow(String dtEventTime, int iGameSvrId, int iSectId, String vSectName, 
		                   int iMemberCnt, int iSectLevel, int iMapLevel, int iMapId, 
		                   int iMapType, int iOperateType)
		{
			this.dtEventTime = dtEventTime;
			this.iGameSvrId = iGameSvrId;
			this.iSectId = iSectId;
			this.vSectName = vSectName;
			this.iMemberCnt = iMemberCnt;
			this.iSectLevel = iSectLevel;
			this.iMapLevel = iMapLevel;
			this.iMapId = iMapId;
			this.iMapType = iMapType;
			this.iOperateType = iOperateType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("SectMapFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(iSectId);
			sb.append('|').append(vSectName);
			sb.append('|').append(iMemberCnt);
			sb.append('|').append(iSectLevel);
			sb.append('|').append(iMapLevel);
			sb.append('|').append(iMapId);
			sb.append('|').append(iMapType);
			sb.append('|').append(iOperateType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 工会Id
		public int iSectId;
		// 工会名称
		public String vSectName;
		// 成员数
		public int iMemberCnt;
		// 帮派等级
		public int iSectLevel;
		// 地图层级
		public int iMapLevel;
		// 地图ID
		public int iMapId;
		// 地图类型
		public int iMapType;
		// 操作类型
		public int iOperateType;
	}

	// 帮派运镖活动记录
	public static class RoleSectDeliverFlow
	{

		public RoleSectDeliverFlow() { }

		public RoleSectDeliverFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                           String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                           int iLevel, int iVipLevel, int iDeliverType, int iOperateType, 
		                           int iOperateArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iDeliverType = iDeliverType;
			this.iOperateType = iOperateType;
			this.iOperateArg = iOperateArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSectDeliverFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iDeliverType);
			sb.append('|').append(iOperateType);
			sb.append('|').append(iOperateArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 运镖活动类型
		public int iDeliverType;
		// 操作类型
		public int iOperateType;
		// 操作参数（开始运镖0表示未投保，1表示投保）
		public int iOperateArg;
	}

	// 帮派运镖活动记录
	public static class RoleSectDiySkillFlow
	{

		public RoleSectDiySkillFlow() { }

		public RoleSectDiySkillFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                            String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                            int iLevel, int iVipLevel, int iDiySkillType)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iDiySkillType = iDiySkillType;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSectDiySkillFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iDiySkillType);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 运镖活动类型
		public int iDiySkillType;
	}

	// 好友互动记录
	public static class RoleFriendInteractionFlow
	{

		public RoleFriendInteractionFlow() { }

		public RoleFriendInteractionFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                                 String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                                 int iLevel, int iVipLevel, int iInteractionType, int iInteractionArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iInteractionType = iInteractionType;
			this.iInteractionArg = iInteractionArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleFriendInteractionFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iInteractionType);
			sb.append('|').append(iInteractionArg);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 互动类型
		public int iInteractionType;
		// 互动参数
		public int iInteractionArg;
	}

	// 角色日装备记录
	public static class RoleDayEquipFlow
	{

		public RoleDayEquipFlow() { }

		public RoleDayEquipFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                        String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                        int iLevel, int iVipLevel, int iEquip1, int iEquip2, 
		                        int iEquip3, int iEquip4, int iEquip5, int iEquip6)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iEquip1 = iEquip1;
			this.iEquip2 = iEquip2;
			this.iEquip3 = iEquip3;
			this.iEquip4 = iEquip4;
			this.iEquip5 = iEquip5;
			this.iEquip6 = iEquip6;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleDayEquipFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iEquip1);
			sb.append('|').append(iEquip2);
			sb.append('|').append(iEquip3);
			sb.append('|').append(iEquip4);
			sb.append('|').append(iEquip5);
			sb.append('|').append(iEquip6);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 装备1
		public int iEquip1;
		// 装备2
		public int iEquip2;
		// 装备3
		public int iEquip3;
		// 装备4
		public int iEquip4;
		// 装备5
		public int iEquip5;
		// 装备6
		public int iEquip6;
	}

	// 角色日背包道具记录
	public static class RoleDayBagItemFlow
	{

		public RoleDayBagItemFlow() { }

		public RoleDayBagItemFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                          String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                          int iLevel, int iVipLevel, int iItemId, int iItemNum)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iItemId = iItemId;
			this.iItemNum = iItemNum;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleDayBagItemFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iItemId);
			sb.append('|').append(iItemNum);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 道具ID
		public int iItemId;
		// 道具数量
		public int iItemNum;
	}

	// 角色组队记录
	public static class RoleJoinTeamFlow
	{

		public RoleJoinTeamFlow() { }

		public RoleJoinTeamFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                        String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                        int iLevel, int iVipLevel)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleJoinTeamFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
	}

	// 角色日装备记录
	public static class RoleDayGemFlow
	{

		public RoleDayGemFlow() { }

		public RoleDayGemFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                      String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                      int iLevel, int iVipLevel, int iWearSolt, int iGem1, 
		                      int iGem2, int iGem3)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iWearSolt = iWearSolt;
			this.iGem1 = iGem1;
			this.iGem2 = iGem2;
			this.iGem3 = iGem3;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleDayGemFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iWearSolt);
			sb.append('|').append(iGem1);
			sb.append('|').append(iGem2);
			sb.append('|').append(iGem3);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 部位
		public int iWearSolt;
		// 宝石1
		public int iGem1;
		// 宝石2
		public int iGem2;
		// 宝石3
		public int iGem3;
	}

	// 角色日背包道具记录
	public static class RoleDayBagGemFlow
	{

		public RoleDayBagGemFlow() { }

		public RoleDayBagGemFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                         String vChannelId, String vUid, int iRoleId, String dtCreateTime, 
		                         int iLevel, int iVipLevel, int iGemId, int iGemNum)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.dtCreateTime = dtCreateTime;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iGemId = iGemId;
			this.iGemNum = iGemNum;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleDayBagGemFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(dtCreateTime);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iGemId);
			sb.append('|').append(iGemNum);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 角色创建时间
		public String dtCreateTime;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 道具ID
		public int iGemId;
		// 道具数量
		public int iGemNum;
	}

	// 角色发言记录
	public static class RoleChatFlow
	{

		public RoleChatFlow() { }

		public RoleChatFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                    String vChannelId, String vUid, int iRoleId, int iLevel, 
		                    int iVipLevel, int iType, int iTarget, String vContent)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iType = iType;
			this.iTarget = iTarget;
			this.vContent = vContent;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleChatFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iType);
			sb.append('|').append(iTarget);
			sb.append('|').append(vContent);
			sb.append('\n');
			return sb.toString();
		}

		// 游戏事件的时间, 格式 YYYY-MM-DD HH:MM:SS
		public String dtEventTime;
		// 游戏大区编号
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		// (必填)登录的游戏服务器编号
		public int iGameSvrId;
		// 游戏登录的渠道编号
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 发言类型, 0世界聊天 1帮派聊天2队伍聊天3私聊
		public int iType;
		// 发言目标
		public int iTarget;
		// 发言类型, 0世界聊天 1帮派聊天2队伍聊天3私聊
		public String vContent;
	}

	public static class RoleMarriageFlow
	{

		public RoleMarriageFlow() { }

		public RoleMarriageFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                        String vChannelId, String vUid, int iRoleId, int iLevel, 
		                        int iVipLevel, int iPartnerRoleId, int iPartnerLevel, int iPartnerVipLevel, 
		                        int iMarriageId, int iType, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iPartnerRoleId = iPartnerRoleId;
			this.iPartnerLevel = iPartnerLevel;
			this.iPartnerVipLevel = iPartnerVipLevel;
			this.iMarriageId = iMarriageId;
			this.iType = iType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleMarriageFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iPartnerRoleId);
			sb.append('|').append(iPartnerLevel);
			sb.append('|').append(iPartnerVipLevel);
			sb.append('|').append(iMarriageId);
			sb.append('|').append(iType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		public String dtEventTime;
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		public int iGameSvrId;
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 内部角色Id
		public int iPartnerRoleId;
		// 玩家等级
		public int iPartnerLevel;
		// Vip等级
		public int iPartnerVipLevel;
		public int iMarriageId;
		public int iType;
		public int iArg;
	}

	public static class RoleTakePBTCashBackFlow
	{

		public RoleTakePBTCashBackFlow() { }

		public RoleTakePBTCashBackFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                               String vChannelId, String vUid, int iRoleId, int iLevel, 
		                               int iVipLevel, int iBid, int iBackScore, int iBackScoreLevel, 
		                               int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iBid = iBid;
			this.iBackScore = iBackScore;
			this.iBackScoreLevel = iBackScoreLevel;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleTakePBTCashBackFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iBid);
			sb.append('|').append(iBackScore);
			sb.append('|').append(iBackScoreLevel);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		public String dtEventTime;
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		public int iGameSvrId;
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		public int iBid;
		public int iBackScore;
		public int iBackScoreLevel;
		public int iArg;
	}

	public static class RoleEmergencyActivityFlow
	{

		public RoleEmergencyActivityFlow() { }

		public RoleEmergencyActivityFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                                 String vChannelId, String vUid, int iRoleId, int iLevel, 
		                                 int iVipLevel, int iEventType, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleEmergencyActivityFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		public String dtEventTime;
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		public int iGameSvrId;
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 事件类型
		public int iEventType;
		public int iArg;
	}

	public static class RoleJusticeActivityFlow
	{

		public RoleJusticeActivityFlow() { }

		public RoleJusticeActivityFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                               String vChannelId, String vUid, int iRoleId, int iLevel, 
		                               int iVipLevel, int iEventType, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleJusticeActivityFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		public String dtEventTime;
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		public int iGameSvrId;
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 事件类型
		public int iEventType;
		public int iArg;
	}

	public static class RoleSteleActivityFlow
	{

		public RoleSteleActivityFlow() { }

		public RoleSteleActivityFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                             String vChannelId, String vUid, int iRoleId, int iLevel, 
		                             int iVipLevel, int iEventType, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iEventType = iEventType;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleSteleActivityFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iEventType);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		public String dtEventTime;
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		public int iGameSvrId;
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 事件类型
		public int iEventType;
		public int iArg;
	}

	public static class RoleFightNpcActivityFlow
	{

		public RoleFightNpcActivityFlow() { }

		public RoleFightNpcActivityFlow(String dtEventTime, String vGameId, String vOpenId, int iGameSvrId, 
		                                String vChannelId, String vUid, int iRoleId, int iLevel, 
		                                int iVipLevel, int iEventType, int iWin, int iArg)
		{
			this.dtEventTime = dtEventTime;
			this.vGameId = vGameId;
			this.vOpenId = vOpenId;
			this.iGameSvrId = iGameSvrId;
			this.vChannelId = vChannelId;
			this.vUid = vUid;
			this.iRoleId = iRoleId;
			this.iLevel = iLevel;
			this.iVipLevel = iVipLevel;
			this.iEventType = iEventType;
			this.iWin = iWin;
			this.iArg = iArg;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("");
			sb.append("RoleFightNpcActivityFlow");
			sb.append('|').append(dtEventTime);
			sb.append('|').append(vGameId);
			sb.append('|').append(vOpenId);
			sb.append('|').append(iGameSvrId);
			sb.append('|').append(vChannelId);
			sb.append('|').append(vUid);
			sb.append('|').append(iRoleId);
			sb.append('|').append(iLevel);
			sb.append('|').append(iVipLevel);
			sb.append('|').append(iEventType);
			sb.append('|').append(iWin);
			sb.append('|').append(iArg);
			sb.append('\n');
			return sb.toString();
		}

		public String dtEventTime;
		public String vGameId;
		// (必填)用户UId号
		public String vOpenId;
		public int iGameSvrId;
		public String vChannelId;
		// 用户Uid号
		public String vUid;
		// 内部角色Id
		public int iRoleId;
		// 玩家等级
		public int iLevel;
		// Vip等级
		public int iVipLevel;
		// 事件类型
		public int iEventType;
		// 事件类型
		public int iWin;
		public int iArg;
	}

}
