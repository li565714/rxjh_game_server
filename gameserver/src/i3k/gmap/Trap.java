package i3k.gmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import i3k.SBean;
import i3k.gmap.DropGoods.DropItem;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

public class Trap extends BaseRole
{

	Trap(int trapID, MapServer ms)
	{
		super(ms, true);
		this.configID = trapID;
	}

	public Trap createNew(GVector3 position)
	{
		this.id = ms.getMapManager().getNextTrapID().incrementAndGet();
		this.curPosition.reset(position);
		this.birthPosition.reset(position);

		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_TRAP;
		this.curHP = 1;
		this.radius = 75;

		SBean.TrapExpandedCFGS trapCfg = GameData.getInstance().getTrapCFG(this.getConfigID());
		if (trapCfg != null)
		{
			this.trapState = trapCfg.orgState;
			this.trigType = trapCfg.trigType;
			this.delay = (long) trapCfg.delay;
			for (int i = 1; i <= trapCfg.stateCondition.size(); i++)
			{
				int condition = trapCfg.stateCondition.get(i - 1);
				if (condition > 0)
					this.stateChangeCondition.put(i, condition);
			}

			this.curSkillID = trapCfg.skillID;
			SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(this.curSkillID);
			if (sCfg != null)
			{
				SBean.SkillLevelCFGS sDataCfg = GameData.getSkillLevelCFG(sCfg, 1);
				if (sDataCfg != null)
				{
					this.skillInfo = new SkillData(this.curSkillID, 1).createNew(sCfg.baseData, sDataCfg.fix);
					this.attackRange = this.calcuAttackRange(sCfg.baseData.fix.scope, sCfg.baseData.common.fixDistance);
				}
			}
			this.attackInterval = trapCfg.paras.get(trapCfg.paras.size() - 1);
		}
		this.transLogic = 0;
		return this;
	}

	int getFightProp(int propID)
	{
		switch (propID)
		{
		case BaseRole.EPROPID_HP:
			return this.curHP;
		case BaseRole.EPROPID_MAXHP:
			return 1;
		default:
			break;
		}

		return 0;
	}

	void onTimer(int timeTick, long logicTime)
	{
		if (this.curMap == null || (this.curMapGrid == null && !this.isInPrivateMap()))
			return;

		this.onMilliSecondTask(logicTime);
	}

	void onMilliSecondTask(long logicTime)
	{
		if (this.trapState == MapManager.ESTRAP_TRIG)
			this.checkOpen(logicTime);

		this.checkSkill(logicTime);
		this.onCheckSkillDuration(logicTime);
		this.onCheckSkillEnd(logicTime);
	}

	void checkSkill(long logicTime)
	{
		if (logicTime > this.startAttackTime + this.attackInterval && !this.checkState(Behavior.EBATTACK) && this.curSkillID > 0)
			this.onUseSkill(logicTime);

		if (this.checkState(Behavior.EBATTACK))
			this.onAutoProcessDamage(logicTime);
	}

	private void checkOpen(long logicTime)
	{
		if (logicTime >= this.openTime)
			this.openTrap(openRoleID);
	}

	private boolean canUseSkill()
	{
		return !(this.checkState(Behavior.EBDISATTACK) || this.checkState(Behavior.EBATTACK)) && this.addState(Behavior.EBATTACK);

	}

	//陷阱的state 为owerID
	SBean.EnterBase getEnterBase()
	{
		return new SBean.EnterBase(this.id, this.configID, this.trapState, new SBean.Location(this.getLogicPosition(), this.curRotation.toVector3F()), this.getBWType(), this.getSectId(), 0);
	}
	
	void onUseSkill(long logicTime)
	{
		if(!this.checkHasRoleNearBy(USESKILL_CHECK_SIZE, this.attackRange))
			return;
		
		if (!this.canUseSkill())
			return;

		if (this.skillInfo == null)
			return;

		Skill skill = this.createNewSkill(this.skillInfo.baseCommonCfg, this.skillInfo.baseFixCfg, this.skillInfo.lvlFixCfg, 1, 0, Skill.eSG_Attack, logicTime);
		if (skill == null)
			return;
		
//		List<BaseRole> targets = this.getTargets(skill);
//		if (targets.isEmpty())
//			return;

		this.startAttackTime = logicTime;
		this.attack = skill;
		this.curUseSkillAddCache.add(skill);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_trap_useskill(this.getID(), this.curSkillID));
	}

	void onAutoProcessDamage(long logicTime)
	{
		this.refreshCurUseSkills();
		if(this.curUseSkills.isEmpty())
			return;
		
		SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
		Iterator<Skill> it = this.curUseSkills.iterator();
		while (it.hasNext())
		{
			Skill skill = it.next();
			if (!skill.autoProcessDamage(this, logicTime, timeTick))
			{
				it.remove();
				this.onEndSkillHandle(skill);
			}
		}
	}

	List<BaseRole> getEnemiesNearBy()
	{
		return this.getPlayerEnemiesNearBy();
	}

	List<BaseRole> getTargets(Skill skill)
	{
		SBean.Scope scope = skill.baseFixCfg.scope;
		int skillType = skill.baseFixCfg.type;
		int maxTargets = skill.baseFixCfg.maxTargets;
		List<BaseRole> targets = new ArrayList<>();

		//技能范围类型（自身、单点技能）
		if (scope.type == Skill.eSScopT_Owner)
		{
			if (!this.isDead())
				targets.add(this);
			return targets;
		}
		else if (scope.type == Skill.eSScopT_Single)
		{
			BaseRole entity = this.getRoleInCheckRange();
			if (entity != null)
				targets.add(entity);
			return targets;
		}

		//技能效果类型
		switch (skillType)
		{
		case GameData.eSE_Buff:
			//己方单位

			break;
		case GameData.eSE_Damage:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());

			//中立单位
			break;
		case GameData.eSE_DBuff:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());
			break;
		default:
			break;
		}
		targets = skill.checkTarget(this, targets, scope, maxTargets);
		return targets;
	}

	BaseRole getRoleInCheckRange()
	{
		float minDistance = GameData.MAX_NUMBER;
		float distance = 0;
		BaseRole entity = null;
		for (BaseRole r : this.getPlayerEnemiesNearBy())
		{
			distance = r.getCurPosition().distance(this.getCurPosition());
			if (!r.isInProtectTime())
			{
				if (distance < minDistance)
				{
					minDistance = distance;
					entity = r;
				}
			}
		}

		return entity;
	}

	SBean.TrapDetail getTrapDetail()
	{
		SBean.TrapExpandedCFGS trapCfg = GameData.getInstance().getTrapCFG(this.getConfigID());
		if(trapCfg == null)
			return new SBean.TrapDetail(this.getID(), this.getConfigID(), this.trapState, GameData.emptyList());
		
		List<SBean.RelatedTrap> relatedTraps = trapCfg.relateTraps.isEmpty() ? GameData.emptyList() : new ArrayList<>();
		for (Integer tid : trapCfg.relateTraps)
		{
			Trap t = this.curMap.getConfigTrap(tid);
			if (t != null)
				relatedTraps.add(new SBean.RelatedTrap(t.getID(), t.getLogicPosition()));
		}
		
		return new SBean.TrapDetail(this.getID(), this.getConfigID(), this.trapState, relatedTraps);
	}
	
	void onGetDamage(BaseRole attacker, SBean.DamageResult res, int skillType, int skillID, int curDamageEventID)
	{
		if (skillType != GameData.eSE_Buff)
		{
			if (this.canAttacked())
				this.changeTrapState(attacker.owner);
		}
	}

	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterBase> traps  = new ArrayList<>();
			traps.add(this.getEnterBase());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_traps(traps));
		}
	}

	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> traps = new ArrayList<>();
			traps.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_traps(traps));
		}
	}
	
	boolean canAttacked()
	{
		return this.trigType == MapManager.TRAP_TRIGTYPE_ATTACK;
	}

	boolean canClicked()
	{
		return this.trigType == MapManager.TRAP_TRIGTYPE_CLICKED;
	}

	public int setCurHP(int curHP)
	{
		return this.curHP;
	}

	void onAttacked(BaseRole role)
	{
		if (this.canAttacked())
			this.changeTrapState(role.owner);
	}

	void onClicked(MapRole role)
	{
		if (this.canClicked() && this.trapState == MapManager.ESTRAP_ACTIVE)
		{
			this.changeTrapState(role);
		}
	}

	//触发
	void onTrigTrap(int condition, MapRole role)
	{
		if (this.trapState == MapManager.ESTRAP_OPEN)
			return;

		this.transLogic += condition;
		Integer trans = this.stateChangeCondition.get(this.trapState);
		if (trans != null && this.transLogic < trans)
			return;

		this.stateChangeCondition.remove(this.trapState);

		if (this.stateChangeCondition.isEmpty()) //满足改变状态条件
			this.changeTrapState(role);
	}

	//转换状态
	void changeTrapState(MapRole role)
	{
		if (this.trapState == MapManager.ESTRAP_OPEN)
			return;

		if (this.trapState == MapManager.ESTRAP_TRIG)
			return;

		this.trapState++;
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_trap_changestate(this.getID(), this.trapState));
		this.openTime = ms.getMapManager().getMapLogicTime() + this.delay;
		if (this.trapState == MapManager.ESTRAP_TRIG)
		{
			int roleID = role == null ? 0 : role.getID();
			if (this.delay == 0)
				this.openTrap(roleID);
			else
				this.openRoleID = roleID;

		}
	}

	//强制设置状态
	void setTrapState(int state)
	{
		this.trapState = state;
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_trap_changestate(this.getID(), this.trapState));
		this.openTime = ms.getMapManager().getMapLogicTime() + this.delay;
		if (this.trapState == MapManager.ESTRAP_TRIG)
		{
			if (this.delay == 0)
				this.openTrap(0);
			else
				this.openRoleID = 0;
		}
	}

	void openTrap(int roleID)
	{
		if (this.trapState == MapManager.ESTRAP_OPEN)
			return;

		//触发关联陷阱
		SBean.TrapExpandedCFGS trapCfg = GameData.getInstance().getTrapCFG(this.getConfigID());
		if (trapCfg != null && trapCfg.relateTraps.size() > 0)
		{
			for (Integer tid : trapCfg.relateTraps)
			{
				Trap t = this.curMap.getConfigTrap(tid);
				if (t != null)
				{
					t.onTrigTrap(MapManager.TRAP_CHANGE_STATE2, null);
				}
			}
		}

		MapRole role = this.curMap.getRole(roleID);
		if (role != null)
		{
			SBean.TrapExpandedCFGS cfg = GameData.getInstance().getTrapCFG(this.getConfigID());
			if (cfg != null)
			{
				List<MapRole> members = role.getTeamMemberNearBy(role, GameData.getInstance().getCommonCFG().team.expAddDistance);
				members.add(role);
				SBean.Vector3 position = cfg.position;
				for (MapRole r : members)
				{
					List<DropItem> dropItems = this.curMap.getDropItemList(r.id, cfg.fixedDropID, cfg.randomDropID, cfg.randomDropCnt, 1, 1, null, position, this.getConfigID(), this.getEntityType(), r.canTakeDrop);
					if (dropItems.size() > 0)
					{
						List<SBean.DropInfo> dropInfo = new ArrayList<>();
						for (DropItem d : dropItems)
							dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));

						ms.getRPCManager().sendStrPacket(r.getID(), new SBean.role_sync_drops(position, dropInfo));
					}
				}

				//出怪
				if(cfg.monsterOdds > 0 && GameData.checkRandom(cfg.monsterOdds))
				{
					int monsterID = cfg.monsterID;
					int monsterCount = cfg.monsterCount;
					SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(monsterID);
					if (monsterCfg != null)
					{
						float angle;
						int dropRadius = cfg.dropRadius;
						for (int i = 0; i < monsterCount; i++)
						{
							angle = (float) (GameRandom.getRandom().nextFloat() * Math.PI * 2.0f);
							GVector3 pos = new GVector3((float) (dropRadius * Math.cos(angle)), 0.0f, (float) (dropRadius * Math.sin(angle)));
							pos = this.getCurPosition().sum(pos);
							this.curMap.createMonster(monsterID, pos, GVector3.randomRotation(), true, -1, -1);
						}
					}
				}

				//出场景BUFF
				List<Integer> dropBuffs = GameData.getInstance().getBuffDrops(cfg.buffDropID, cfg.buffDropCnt);
				for (Integer bid : dropBuffs)
				{
					MapBuff mapBuff = new MapBuff(bid, this.ms).createNew(this.createPosition(cfg.dropRadius));
					this.curMap.addMapBuff(mapBuff);
				}
			}
		}
		else
		{

		}

		this.trapState = MapManager.ESTRAP_OPEN;
	}

	private static final int USESKILL_CHECK_SIZE = 3;
	private int trapState;
	private int trigType;
	private long openTime;
	private int openRoleID;
	private long delay;
	private int transLogic;
	private Map<Integer, Integer> stateChangeCondition = new TreeMap<>(); //<stateID, condition>
	private SkillData skillInfo;

	private int attackInterval;
}
