package i3k.gmap;

import i3k.SBean;
import i3k.gmap.BaseMap.DemonHoleMap;
import i3k.gmap.BaseMap.ForceWarMap;
import i3k.gmap.BaseMap.JusticeMapCopy;
import i3k.gmap.BaseMap.PrivateMap;
import i3k.gmap.BaseMap.PublicMap;
import i3k.gmap.BaseMap.SectGroupMap;
import i3k.gmap.BaseMap.SuperArenaMap;
import i3k.gmap.BaseMap.TowerDefenceMap;
import i3k.gmap.BaseMap.WorldMap;
import i3k.gmap.BaseRole.SpiritCluster.SpiritContainer;
import i3k.gmap.Behavior.RoleState;
import i3k.gmap.DropGoods.DropItem;
import i3k.gmap.Mineral.MineralInfo;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class MapRole extends BaseRole
{
	MapRole(MapServer ms, boolean serverControl)
	{
		super(ms, serverControl);
		this.tickTimeUpdate = GameTime.getTime();
	}

	MapRole fromDBWitoutLock(int mapId, SBean.Location location, SBean.FightRole fightRole, Map<Integer, SBean.FightPet> curFightPets, List<Integer> petSeqs, SBean.PetHost pethost, 
			int hp, int sp, int armorVal, Map<Integer, SBean.DBBuff> dbBuffs, SBean.PKInfo pkInfo, int curRideHorse, SBean.DBAlterState alterState, SBean.MulRoleInfo mulRoleInfo, int dayFailedStreak, int vipLevel, int curWizardPet, byte canTakeDrop)
	{
		this.ctrlType = ECTRL_TYPE_PLAYER;
		this.fromDB(location);
		this.armorMgr = new ArmorManager(armorVal);
		this.setCommonProps(fightRole, curFightPets, false);
		this.setPetSeqs(petSeqs);

		this.syncPos.fromVector3(location.position);
		this.clientLastPos.fromVector3(location.position);
		this.curMapID = mapId;
		for (SBean.DBBuff b : dbBuffs.values())
		{
			Buff buff = new Buff(b.endTime, b.buff.id, 0, 0, b.buff.overLays, b.buff.value, b.buff.attackerType, 0, 0, "", (byte)0, 0, false, true);
			this.buffsAddCache.add(buff);
		}

		this.otherPethost = pethost;
		this.pkInfo = pkInfo == null ? new SBean.PKInfo(GameData.ATTACK_MODE_PEACE, 0) : pkInfo;
		this.curRideHorse = curRideHorse;
		this.mulRoleInfo = mulRoleInfo;
		this.alterState = alterState;
		this.dayFailedStreak = dayFailedStreak;
		this.vipLevel = vipLevel;
		this.curWizardPet = curWizardPet;
		this.canTakeDrop = canTakeDrop == 1;
		return this;
	}

	MapRole createRobotRole(SBean.FightRole robot, Map<Integer, SBean.FightPet> curFightPets, List<Integer> petSeqs, SBean.MapClusterCFGS mapClusterCfg)
	{
		this.fromDB(new SBean.Location(mapClusterCfg.spawnPos2nd, new SBean.Vector3F(-mapClusterCfg.spawnRotation.x, 0, -mapClusterCfg.spawnRotation.z)));
		this.armorMgr = new ArmorManager(this.maxArmorValue);
		this.setCommonProps(robot, curFightPets, true);
		this.setPetSeqs(petSeqs);
		this.birthPosition = new GVector3(mapClusterCfg.center);
		this.maxTraceRange = mapClusterCfg.aiMaxDisToCenter;
		
		this.curMapID = mapClusterCfg.id;
		this.pkInfo = new SBean.PKInfo(GameData.ATTACK_MODE_ALL, 0);
		this.robot = true;
		
		this.active = true;
		this.curAttackSeq = 0;
		this.setCurSkillID();
		return this;
	}
	
	void onLeavaMap()
	{
		if(this.isInPrivateMap())
			return;
		
		if(this.getCurMapGrid() != null)
		{
			int gridX = this.getCurMapGrid().getGridX();
			int gridZ = this.getCurMapGrid().getGridZ();
			Set<Integer> rids = this.curMap.getRoleIDsNearBy(gridX, gridZ, this, true);
			this.filterAndDelNearByRoleIDs(rids);
			this.onSelfLeaveNearBy(rids, 1);
		}
		
		this.dissolveCurPets();
		this.clearBlurs(0);
		this.clearSkillEntity();
		this.clearSceneTrigMonsters();
	}
	
	void update(int hp, int sp)
	{
		this.updateSkillSpiritEffect();
		this.updateArmorFightProps();
		this.updateArmorTalentAi(this.propRole.getCurArmor(), true);
		this.updateHorseFightProps();
		this.updateWeaponUniqueSkills();
		this.initEquipLegend(this.propRole.getAllWearEquip());
		this.onUpdateCurBuff(ms.getMapManager().getMapLogicTime());
//		this.updateBuffProps();
		this.onPropUpdate();
		this.curHP = hp < 0 ? this.maxHp : Math.min(hp, this.maxHp);
		this.curSP = sp;
	}
	
	void setCommonProps(SBean.FightRole fightRole, Map<Integer, SBean.FightPet> curFightPets, boolean isInArena)
	{
		this.propRole = new PropRole(false).createNew(fightRole.base, curFightPets, isInArena);
		this.setPropBase(this.propRole);
		this.entityType = GameData.ENTITY_TYPE_PLAYER;
		this.id = fightRole.base.roleID;
		this.roleName = fightRole.roleName;
		this.gender = fightRole.gender;
		this.headIcon = fightRole.headIcon;
		this.face = fightRole.face;
		this.hair = fightRole.hair;
		this.configID = fightRole.base.classType;
		this.level = fightRole.base.level;
		this.curWeapon = fightRole.curWeapon;
		this.curDIYSkill = fightRole.diyskill;
		this.curSkills = fightRole.curSkills;
		this.curUniqueSkill = fightRole.curUniqueSkill;
		this.sectBrief = fightRole.sectBrief;
		this.showFashionTypes = fightRole.showFashionTypes; 
		this.isHeirloomDisplay = fightRole.isHeirloomDisplay == 1;
		this.totalPower = fightRole.fightPower;
		for(SBean.FightPet p: curFightPets.values())
			this.totalPower += p.fightPower;
		
		this.curPermanentTitle = fightRole.base.title.curPermanent;
		//this.curTimedTitles = fightRole.base.title.curTimed;
		this.curTimedTitles = fightRole.base.title.curTimedTitles;

		SBean.ClassRoleCFGS classRolecfg = GameData.getInstance().getClassRoleCFG(this.configID);
		if (classRolecfg != null)
		{
			this.dodgeSkills.add(classRolecfg.dodgeSkill);
			this.classReduce = new ArrayList<>(classRolecfg.classReduce);
			this.setRadius(classRolecfg.radius);
		}
		
		this.selfPethost = this.propRole.getMapPetHost();
		this.owner = this;
		this.ghost = false;
	}
	
	void setPetSeqs(List<Integer> seqs)
	{
		if(seqs.size() > GameData.PET_FIGHT_MAX_USE)
			return;
		
		for(int i=0; i<seqs.size(); i++)
			this.fightPetSeqs.put(seqs.get(i), i + 1);
	}
	
	int getPetSeq(int pid)
	{
		return this.fightPetSeqs.getOrDefault(pid, 0);
	}
	
	private void resetFightSkills(boolean breakSkill)
	{
//		this.curAttackSeq = 0;
//		this.attackListSkill = new ArrayList<>(this.curSkills);
		this.resetSkills();
		this.resetUniqueSkill();
		this.resetWeaponSkill();
		this.resetAttack();
		this.resetDiySkill();
		this.resetAlterSkill(false);
		
		if(breakSkill)
			this.breakSkill();
	}
	
	private void resetSkills()
	{
		for (Integer sid : this.curSkills)
		{
			if(this.fightSkills.containsKey(sid))
				continue;

			SBean.DBSkill s = this.propRole.getSkill(sid);
			if(s == null)
				continue;
			
			int sLvl = getSkillLvl(s);
			FightSkill skill = new FightSkill(s.id, sLvl, 0, s.bourn, Skill.eSG_Skill, (float) (1.0 - this.getFightPropF(BaseRole.EPROPID_FASTSKILL)), GameData.SKILL_USE_TYPE_CUR).copySkillData();
			if (skill == null)
				continue;
			
			this.fightSkills.put(skill.id, skill);
			this.normalSkills.add(sid);
		}
	}
	
	private void resetUniqueSkill()
	{
		this.resetUniqueSkill(0);
	}
	
	private void resetUniqueSkill(long coolDownTime)
	{
		if(this.fightSkills.containsKey(this.curUniqueSkill))
			return;

		SBean.DBSkill s = this.propRole.getSkill(this.curUniqueSkill);
		if(s == null)
			return;
				
		int sLvl = getSkillLvl(s);
		FightSkill skill = new FightSkill(s.id, sLvl, coolDownTime, s.bourn, Skill.eSG_Skill, (float) (1.0 - this.getFightPropF(BaseRole.EPROPID_FASTSKILL)), GameData.SKILL_USE_TYPE_UNIQUE).copySkillData();
		if (skill == null)
			return;
		
		this.fightSkills.put(skill.id, skill);
		this.normalSkills.add(skill.id);
	}
	
	private void resetAttack()
	{
		//普通攻击
		SBean.ClassRoleCFGS classRolecfg = GameData.getInstance().getClassRoleCFG(this.configID);
		if(classRolecfg == null)
			return;

		for (Integer sid : classRolecfg.attacks)
		{
			if (this.fightSkills.containsKey(sid))
				continue;

			SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(sid);
			if (sCfg == null)
				continue;
			
			FightSkill skill = new FightSkill(sid, sCfg.baseData.common.maxLvl, 0, 0, Skill.eSG_Attack, (float) (1.0 - this.getFightPropF(BaseRole.EPROPID_FASTSKILL)), GameData.SKILL_USE_TYPE_ATTACK).copySkillData();
			if (skill == null)
				continue;
			
			this.fightSkills.put(sid, skill);
			this.normalSkills.add(sid);
		}

		//闪避技能(轻功)
		SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(classRolecfg.dodgeSkill);
		if (sCfg != null && !this.fightSkills.containsKey(sCfg.id))
		{
			FightSkill skill = new FightSkill(classRolecfg.dodgeSkill, sCfg.baseData.common.maxLvl, 0, 0, Skill.eSG_Skill, (float) (1.0 - this.getFightPropF(BaseRole.EPROPID_FASTSKILL)), GameData.SKILL_USE_TYPE_DODGE).copySkillData();
			if (skill != null)
			{
				this.fightSkills.put(classRolecfg.dodgeSkill, skill);
				this.normalSkills.add(skill.id);
			}
		}
	}

	private void resetWeaponSkill()
	{
		SBean.WeaponCFGS cfg = GameData.getInstance().getWeaponCFGS(this.curWeapon);
		SBean.DBWeapon dbWeapon = this.propRole.getWeapon(this.curWeapon);
		if (cfg != null && dbWeapon != null)
		{
			if (!this.weaponMotivate)
			{
				for (Integer skillID : cfg.skills)
					this.fightSkills.remove(skillID);
				
				this.fightSkills.remove(cfg.dodge);
				this.weaponSkills.clear();
				this.dodgeSkills.remove(cfg.dodge);
			}
			else
			{
				int skillID;
				int skillLvl;
				this.attackList.clear();
				this.curAttackSeq = 0;
				this.weaponSkills.clear();
				int count = cfg.skills.size() < dbWeapon.skills.size() ? cfg.skills.size() : dbWeapon.skills.size();
				for (int i = 0; i < count; i++)
				{
					skillID = cfg.skills.get(i);
					skillLvl = dbWeapon.skills.get(i);
					SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(skillID);
					if (sCfg != null)
					{
						FightSkill skill = new FightSkill(skillID, skillLvl, 0, 0, Skill.eSG_Attack, 1, GameData.SKILL_USE_TYPE_WEAPON).copySkillData();
						if (skill != null)
							this.fightSkills.put(skill.id, skill);
					}
					
					this.weaponSkills.add(skillID);
					this.attackList.add(skillID);
				}
				
				//dodge
				SBean.SkillCFGS dodgeCfg = GameData.getInstance().getSkillCFG(cfg.dodge);
				if(dodgeCfg != null)
				{
					FightSkill dodgeSkill = new FightSkill(cfg.dodge, 1, 0, 0, Skill.eSG_Attack, 1, GameData.SKILL_USE_TYPE_WEAPON).copySkillData();
					if(dodgeSkill != null)
					{
						this.fightSkills.put(dodgeSkill.id, dodgeSkill);
						this.weaponSkills.add(cfg.dodge);
						this.dodgeSkills.add(cfg.dodge);
					}
				}
			}
		}
	}
	
	private void updateWeaponSkill()
	{
		SBean.WeaponCFGS cfg = GameData.getInstance().getWeaponCFGS(this.curWeapon);
		SBean.DBWeapon dbWeapon = this.propRole.getWeapon(this.curWeapon);
		if(cfg != null && dbWeapon != null)
		{
			int skillID;
			int skillLvl;
			int count = cfg.skills.size() < dbWeapon.skills.size() ? cfg.skills.size() : dbWeapon.skills.size();
			for (int i = 0; i < count; i++)
			{
				skillID = cfg.skills.get(i);
				skillLvl = dbWeapon.skills.get(i);
				SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(skillID);
				if (sCfg != null)
				{
					FightSkill skill = this.fightSkills.get(skillID);
					if(skill == null)
						continue;
					
					skill.level = skillLvl;
					SBean.SkillLevelCFGS lvlDataCfg = GameData.getSkillLevelCFG(sCfg, skillLvl);
					if (lvlDataCfg != null)
						skill.lvlFixCfg = lvlDataCfg.fix;
				}
			}
		}
	}
	
	private void resetDiySkill()
	{
		//自创武功
		if (this.curDIYSkill != null && !this.fightSkills.containsKey(Skill.DIY_SKILL_ID))
		{
			FightSkill skill = new FightSkill(Skill.DIY_SKILL_ID, 1, 0, 0, Skill.eSG_Skill, (float) (1.0 - this.getFightPropF(BaseRole.EPROPID_FASTSKILL)), GameData.SKILL_USE_TYPE_DIY).copySkillData();
			if (skill != null)
			{
				this.fightSkills.put(Skill.DIY_SKILL_ID, skill);
				this.setDiySkillData(skill.baseDataCfg, skill.lvlFixCfg);
				this.normalSkills.add(skill.id);
			}
		}
	}

	private void resetAlterSkill(boolean clear)
	{
		if(!this.isInWorld() && this.curMap != null)
			return;
		
		SBean.AlterCFGS alterCfg = GameData.getInstance().getAlterCFGS(this.alterState.alterID);
		if (alterCfg == null)
			return;

		if (clear)
		{
			for (int sid : alterCfg.attacks)
				this.fightSkills.remove(sid);

			for (SBean.SkillBriefCFGS s : alterCfg.skills)
				this.fightSkills.remove(s.id);
			
			for(int buffID: alterCfg.buffs)
				this.removeBuffByID(buffID);
			
			SBean.ClassRoleCFGS classRoleCfg = GameData.getInstance().getClassRoleCFG(this.configID);
			if(classRoleCfg != null)
				this.getPropBase().setFightPropFixValue(EPROPID_SPEED, classRoleCfg.speed);
			
			this.alterSkills.clear();
			this.dodgeSkills.remove(alterCfg.dodge);
		}
		else
		{
			for (int sid : alterCfg.attacks)
			{
				FightSkill skill = new FightSkill(sid, 1, 0, 0, Skill.eSG_Attack, 1, GameData.SKILL_USE_TYPE_ALL).copySkillData();
				if (skill != null)
				{
					this.fightSkills.put(sid, skill);
					this.alterSkills.add(sid);
				}
			}

			for (SBean.SkillBriefCFGS s : alterCfg.skills)
			{
				FightSkill skill = new FightSkill(s.id, s.lvl, 0, 0, Skill.eSG_Skill, 1, GameData.SKILL_USE_TYPE_ALL).copySkillData();
				if (skill != null)
				{
					this.fightSkills.put(s.id, skill);
					this.alterSkills.add(s.id);
				}
			}
			
			//dodge
			SBean.SkillCFGS dodgeCfg = GameData.getInstance().getSkillCFG(alterCfg.dodge);
			if(dodgeCfg != null)
			{
				FightSkill dodgeSkill = new FightSkill(alterCfg.dodge, 1, 0, 0, Skill.eSG_Attack, 1, GameData.SKILL_USE_TYPE_WEAPON).copySkillData();
				if(dodgeSkill != null)
				{
					this.fightSkills.put(dodgeSkill.id, dodgeSkill);
					this.alterSkills.add(alterCfg.dodge);
					this.dodgeSkills.add(alterCfg.dodge);
				}
			}
			
			if(this.alterState.attrEndTime > 0)
			{
				for(int buffID: alterCfg.buffs)
					this.addBuffByID(buffID, this, null, -1);
			}
			
			this.getPropBase().setFightPropFixValue(EPROPID_SPEED, alterCfg.speed);
		}
		this.breakSkill();
	}
	
	private void resetPetAlterSkill()
	{
		if(this.alterPet == null)
			return;
		
		for(FightSkill e: this.alterPet.fightSkills.values())
		{
			this.fightSkills.put(e.id, e);
			this.normalSkills.add(e.id);
		}
	}
	
	void setDiySkillData(SBean.SkillBaseCFGS baseData, SBean.SkillLevelFixCFGS lvlFix)
	{
		if (this.curDIYSkill == null)
			return;

		this.curDIYSkill.skillActionID = this.curDIYSkill.skillActionID % 1000 + this.configID * 1000;
		SBean.DIYSkillActionCFGS actionCfg = GameData.getInstance().getDIYSkillActionCFGS(this.curDIYSkill.skillActionID);
		if(actionCfg == null)
			return;
		
		this.diySkillData = new SkillData(Skill.DIY_SKILL_ID, 1).createNew(baseData, lvlFix);
		this.diySkillData.baseFixCfg = baseData.fix.kdClone();
		this.diySkillData.lvlFixCfg = lvlFix.kdClone();

		this.diySkillData.baseFixCfg.duration = actionCfg.duration;
		this.diySkillData.baseFixCfg.scope = new SBean.Scope(actionCfg.scopeType, new ArrayList<>(this.curDIYSkill.scope));
		GameData.fixDiySkillScope(this.configID, this.diySkillData.baseFixCfg.scope);
		this.diySkillData.baseFixCfg.type = GameData.eSE_Damage;
		this.diySkillData.baseFixCfg.maxTargets = -1;
		this.diySkillData.lvlFixCfg.addSP = this.curDIYSkill.addSP;
		this.diySkillData.lvlFixCfg.cool = this.curDIYSkill.cd;

		this.diySkillData.lvlFixCfg.events.clear();
		float arg1 = this.curDIYSkill.damageArgs.get(0);
		float arg2 = this.curDIYSkill.damageArgs.get(1);
		SBean.SubDamageCFGS damage = new SBean.SubDamageCFGS(10000, (byte) 0, (byte) 0, arg1, arg2, 0);
		List<SBean.SubStatus> status = new ArrayList<>();
		curDIYSkill.buffs.forEach(buff -> status.add(buff.status));

		for (int i = 1; i <= this.curDIYSkill.damageTimes; i++)
		{
			SBean.SkillEventCFGS event = new SBean.SkillEventCFGS(actionCfg.trigTimes.get(i - 1), damage, new ArrayList<SBean.SubStatus>());
			if (i == this.curDIYSkill.damageTimes)
				event.status = status;

			this.diySkillData.lvlFixCfg.events.add(event);
		}
	}

	void updateSkillSpiritEffect()
	{
		this.resetFightSkills(false);
		this.setSpiritEffect(true, -1);
		SBean.HorseInfo info = this.propRole.getHorse(this.propRole.getCurHorseId());
		if (info != null)
			this.setHorseSkillEffect(info, true, -1);
		
		this.resetSkillOrder();
	}
	
	SBean.PetHost getPetHost(int id)
	{
		return id > 0 ? this.selfPethost : this.otherPethost;
	}

	int getFightProp(int propID)
	{
		if(this.alterPet != null)
			return this.alterPet.getFightProp(propID); 
		
		switch (propID)
		{
		case BaseRole.EPROPID_LVL:
			return this.level;
		case BaseRole.EPROPID_HP:
			return this.curHP;
		case BaseRole.EPROPID_SP:
			return this.curSP;
		case BaseRole.EPROPID_ARMCURHP:
			return this.armorMgr.getArmorValue();
		case BaseRole.EPROPID_SPEED:
			int speed = this.getPropBase().getFinalProps(propID, getPropRedNameDamageDecreaseValue(propID) + getPropPetLackDamageDecreaseValue(propID));
			return speed < 0 ? 0 : speed;
		default:
			return this.getPropBase().getFinalProps(propID, getPropRedNameDamageDecreaseValue(propID) + getPropPetLackDamageDecreaseValue(propID));
		}
	}
	
	int getPropRedNameDamageDecreaseValue(int propID)
	{
		int redDecrease = 0;
		if (propID == EPROPID_DMGTO && this.isInWorld())
		{
			int grade = GameData.getInstance().getReaNameGrade(this.pkInfo.value);
			if (grade > 0)
				redDecrease = GameData.getInstance().getRedNameDamageDecreases(grade);
		}
		return redDecrease;
	}
	
	int getPropPetLackDamageDecreaseValue(int propID)
	{
		int decrease = 0;
		if (this.petLack && propID == EPROPID_DMGTO && this.getMapType() == GameData.MAP_TYPE_MAPCOPY_BWARENA)
			decrease = GameData.getInstance().getBWArenaCFGS().fight.damageDecrease;
		
		return decrease;
	}
	
	void notifyClientMaxHPUpdate()
	{
		boolean out = false;
		if (this.curHP > this.getMaxHP())
		{
			this.setCurHP(this.getMaxHP());
			out = true;
		}

		if(!this.robot)
			ms.getRPCManager().syncRoleHP(this.id, this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
		
		if(!this.isInPrivateMap())
		{
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			this.notifyUpdateMaxHp(rids);
			if(out)
			{
				ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_role_updatehp(this.getID(), this.getCurHP()));
				this.notifyUpdateHp(rids);
			}
		}
	}
	
	public void onRoleRename(String newName)
	{
		this.roleName = newName;
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
		{
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_rename(this.id, newName));
		}
	}
////////////////////////////////////////////////基础属性////////////////////////////////////////////////
	void onPropUpdate()
	{
		this.updateMaxHp();
		this.updateMaxArmorVal();
		this.updateFastSkill();
	}
	
	public boolean updateMaxHp()
	{
		boolean update = super.updateMaxHp();
		if (update && this.curMap != null)
			this.notifyClientMaxHPUpdate();
		return update;
	}
	
	boolean updateMaxArmorVal()
	{
		int newMax = this.getFightProp(EPROPID_ARMMAXHP);
		if (newMax != this.maxArmorValue)
		{
			this.maxArmorValue = newMax;
			this.notifyArmorMaxValUpdate();
			return true;
		}
		return false;
	}
	
	boolean updateFastSkill()
	{
		int newMax = this.getFightProp(EPROPID_FASTSKILL);
		if(newMax != this.maxFastSkill)
		{
			this.maxFastSkill = newMax;
			for(int sid: this.normalSkills)
			{
				FightSkill fSkill = this.fightSkills.get(sid);
				if(fSkill != null)
					fSkill.setCoolDownPercent((float)(1.0 - this.getFightPropF(BaseRole.EPROPID_FASTSKILL)));
			}
			return true;
		}
		return false;
	}
	
	void notifyArmorMaxValUpdate()
	{
		if (this.armorMgr.getArmorValue() > this.maxArmorValue)
		{
			this.setArmorVal(this.maxArmorValue);
			if(!this.isInPrivateMap())
				notifySelfArmorValUpdate();
		}
	}
	
	public void onUpdateLevel(int level)
	{
		this.level = level;
		
		this.propRole.onUpdateLvl(level);
		this.updatePetFightProps();
		this.onPropUpdate();
		this.setCurHP(this.getMaxHP());
		ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_role_updatehp(this.getID(), this.getCurHP()));
		
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_lvlup(this.getID(), this.level));
		this.notifyUpdateHp(rids);
	}
////////////////////////////////////////////////基础属性////////////////////////////////////////////////
	
////////////////////////////////////////////////装备属性////////////////////////////////////////////////
	void initEquipLegend(Collection<SBean.DBWearEquip> wearEquips)
	{
		for(SBean.DBWearEquip we: wearEquips)
		{
			if(!GameData.getInstance().isLegengThreeValid(we.equip))
				continue;
			
			setEquipLegend(we.wid, we.equip.legends.get(2), true);
		}
	}
	
	void setEquipLegend(int partID, int legendThree, boolean add)
	{
		SBean.LegendThreeCFGS ltCfg = GameData.getInstance().getLegengThreeCFGS(partID, legendThree);
		if(ltCfg == null)
			return;
		
		switch (ltCfg.type)
		{
		case GameData.LEGEND_EQUIP_THREE_TYPE_ADDAI:
			this.updateAi(add, ltCfg.params.get(0), 0, legendThreeAis);
			break;
		case GameData.LEGEND_EQUIP_THREE_TYPE_ADDBUFF:
			this.updateSpecialBuff(add, -1, ltCfg.params.get(0));
			break;
		case GameData.LEGEND_EQUIP_THREE_TYPE_DODGECD:
			updateDodgeSkillReduce(add, ltCfg.params.get(0));
			break;
		default:
			break;
		}
	}
	
	void updateDodgeSkillReduce(boolean add, int reduce)
	{
		if(add)
			this.dodgeSkillCDReduce += reduce;
		else
			this.dodgeSkillCDReduce -= reduce;
		
		if(this.dodgeSkillCDReduce < 0)
		{
			ms.getLogger().warn("role " + this.id + " update dodge skill cd reduce " + this.dodgeSkillCDReduce + " invalid");
			this.dodgeSkillCDReduce = 0;
		}
	}

	void updateEquipImpl(int wid, SBean.DBEquip equip)
	{
		this.propRole.onUpdateEquip(wid, equip);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
	
	public void onUpdateEquip(int wid, SBean.DBEquip equip)
	{
		SBean.DBWearEquip old = this.propRole.getWearEquip(wid);
		int oldLegendThree = GameData.getInstance().isLegengThreeValid(old == null ? null : old.equip) ? old.equip.legends.get(2) : 0;
		int newLegendThree = GameData.getInstance().isLegengThreeValid(equip) ? equip.legends.get(2) : 0;
		if(oldLegendThree != newLegendThree && oldLegendThree > 0)
			this.setEquipLegend(wid, oldLegendThree, false);
		
		int wearEquip = this.propRole.getWearEquipID(wid);
		this.updateEquipImpl(wid, equip);
		
		if(oldLegendThree != newLegendThree && newLegendThree > 0)
			this.setEquipLegend(wid, newLegendThree, true);
		
		if(equip == null || wearEquip != equip.id)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
			{
				if (equip != null)
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updateequip(this.getID(), wid, equip.id));
				else
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_removeequip(this.getID(), wid));
			}
		}
	}
	
	public void onUpdateEquipPart(SBean.DBEquipPart equipPart)
	{
		this.propRole.onUpdateEquipPart(equipPart);
		this.updatePetFightProps();
		this.onPropUpdate();
		SBean.EquipPart part = new SBean.EquipPart(equipPart.id, equipPart.eqGrowLvl, equipPart.eqEvoLvl);
		
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updatepart(this.id, part));
	}
	
	public void onGainNewSuite(int suiteID)
	{
		this.propRole.onGainNewSuite(suiteID);
		this.updatePetFightProps();
		this.onPropUpdate();
	}

	void onUpWearFashion(int type, int fashionID, int isShow)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		int oldShow = this.showFashionTypes.getOrDefault(type, 0);
		if(oldShow != isShow)
		{
			this.showFashionTypes.put(type, isShow);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_set_fashionshow(this.id, type, isShow));
		}
		
		if(this.propRole.onUpWearFashion(type, fashionID))
		{
			this.updatePetFightProps();
			this.onPropUpdate();
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_upwear_fashion(this.id, type, fashionID));
		}
	}
	
	public void onUpdateSealGrade(int grade)
	{
		this.propRole.onUpdateSealGrade(grade);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
	
	void lossDurabilityOnUseSkill()
	{
		if(this.robot)
			return;
		
		SBean.DBWearEquip wearEquip = this.propRole.getWearEquip(GameRandom.getRandInt(1, GameData.EQUIP_MAX_PARTNUM + 1));
		if (wearEquip != null && wearEquip.equip.durability > 0)
		{
			boolean legendValid = GameData.getInstance().isLegengThreeValid(wearEquip.equip);
			
			SBean.CommonCFGS commonCfg = GameData.getInstance().getCommonCFG();
			int old = wearEquip.equip.durability; 
			wearEquip.equip.durability -= commonCfg.equip.useSkillLoss;
			if (wearEquip.equip.durability < 0)
				wearEquip.equip.durability = 0;

			if (old > commonCfg.equip.disableValue && wearEquip.equip.durability <= commonCfg.equip.disableValue)
			{
				this.updateEquipImpl(wearEquip.wid, wearEquip.equip);
				if(legendValid)
					this.setEquipLegend(wearEquip.wid, wearEquip.equip.legends.get(2), false);
			}

			if(old != wearEquip.equip.durability)
			{
				ms.getRPCManager().syncDurability(this.getID(), this.getMapID(), this.getMapInstanceID(), wearEquip.wid, wearEquip.equip.durability);
				ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_durability(wearEquip.wid, wearEquip.equip.durability));
			}
		}
	}
	
	public void onSyncPrivateMapDurability(int wid)
	{
		if (!this.curMap.isTimeOut())
		{
			SBean.DBWearEquip wearEquip = this.propRole.getWearEquip(wid);
			if (wearEquip != null && wearEquip.equip.durability > 0)
			{
				SBean.CommonCFGS commonCfg = GameData.getInstance().getCommonCFG();
				wearEquip.equip.durability -= commonCfg.equip.useSkillLoss;
				if (wearEquip.equip.durability < 0)
					wearEquip.equip.durability = 0;

				if (wearEquip.equip.durability <= commonCfg.equip.disableValue)
				{
					this.propRole.onUpdateEquip(wearEquip.wid, wearEquip.equip);
					this.updatePetFightProps();
					this.onPropUpdate();
				}
				ms.getRPCManager().syncDurability(this.getID(), this.getMapID(), this.getMapInstanceID(), wearEquip.wid, wearEquip.equip.durability);
			}
		}
	}
////////////////////////////////////////////////装备属性////////////////////////////////////////////////

////////////////////////////////////////////////技能属性////////////////////////////////////////////////	
	private void updateFightSPProp()
	{
		this.propRole.clearSpProp();
		SBean.FightSPCFGS fightCfg = GameData.getInstance().getfFightSPCFGS(this.getConfigID());
		if (fightCfg != null && fightCfg.affectType == GameData.FIGHTSP_PROP && this.fightSP > 0)
		{
			SBean.DBSkill skill = this.propRole.getSkill(fightCfg.relatedSkill);
			if (skill != null)
			{
				int lvl = this.getSkillLvl(skill) - 1;
				fightCfg.attrs.stream().filter(attr -> attr.id > 0).forEach(attr -> this.propRole.addSpPropFixValue(attr.id, attr.values.get(lvl) * this.fightSP));
			}
		}
	}

	private void setSpiritEffect(boolean bind, int type)
	{
		for (Integer spiritID : this.propRole.getCurSpirits())
			this.setSpiritEffect(spiritID, bind, type);
	}

	private void setSpiritEffect(int spiritID, boolean bind, int type)
	{
		SBean.DBSpirit dbSpirit = this.propRole.getSpirit(spiritID);
		if (dbSpirit == null)
			return;

		SBean.SpiritGrowUpCFGS curGrowCfg = GameData.getInstance().getSpiritGrowUpCFGS(spiritID, dbSpirit.level);
		if (curGrowCfg != null)
			this.updateSpirirEffectOther(curGrowCfg, bind, type);
	}
	
	private void updateSpirirEffectOther(SBean.SpiritGrowUpCFGS growCfg, boolean bind, int type)
	{
		for (Integer eid : growCfg.effectsIds)
		{
			SBean.SpiritEffectCFGS effectCfg = GameData.getInstance().getSpiritEffectCFGS(eid);
			if (effectCfg == null)
				continue;

			if (effectCfg.type != type && type != -1)
				continue;

			switch (effectCfg.type)
			{
			case GameData.SPIRIT_EFFECT_ADDAI:
				this.updateSpiritAddAi(effectCfg, bind, this.passiveTrigAis);
				break;
			case GameData.SPIRIT_EFFECT_SKILLPASV:
				this.updateSpiritSkillPasv(effectCfg, bind);
				break;
			case GameData.SPIRIT_EFFECT_ADDBUFF:
				this.updateSpiritAddBuff(effectCfg, bind);
				break;
			case GameData.SPIRIT_EFFECT_FIXSUBDAMAGE:
				this.updateSpiritOther(effectCfg, bind, this.allSpirits);
				break;
			case GameData.SPIRIT_EFFECT_FIXBASESKILL:
				this.updateSpiritOther(effectCfg, bind, this.allSpirits);
				break;
			case GameData.SPIRIT_EFFECT_FIXAI:
				this.updateSpiritOther(effectCfg, bind, this.allSpirits);
				break;
			default:
				break;
			}
		}
	}

	private void updateSpiritSkillPasv(SBean.SpiritEffectCFGS effectCfg, boolean add)
	{
		for (Integer sid : effectCfg.param1)
			this.updateSpiritSkillPasv(sid, effectCfg, add);
	}

	private void updateSpiritSkillPasv(int sid, SBean.SpiritEffectCFGS effectCfg, boolean add)
	{
		int eid = effectCfg.id;
		SBean.DBSkill skill = this.propRole.getSkill(sid);
		if (skill == null)
			return;

		SBean.SkillCFGS skillCfg = GameData.getInstance().getSkillCFG(sid);
		if (skillCfg == null)
			return;

		SBean.SkillLevelCFGS skillLvlCfg = GameData.getSkillLevelCFG(skillCfg, this.getSkillLvl(skill));
		if (skillLvlCfg == null)
			return;

		int eventID = (int) Math.ceil(effectCfg.param2 / 2.0) - 1; 			//0,1,2
		int index = (effectCfg.param2 + 1) % 2; //0,1
		SBean.SkillEventCFGS event = skillLvlCfg.fix.events.get(eventID);
		SBean.SubStatus status = event.status.get(index);
		if (status.buffID > 0)
		{
			if (add)
			{
				SBean.IntList buffs = this.passiveBuffs.get(sid);
				if (buffs == null)
				{
					buffs = new SBean.IntList(new ArrayList<>());
					this.passiveBuffs.put(sid, buffs);
				}
				buffs.list.add(status.buffID);

				this.addSpiritBuff(status.buffID, sid, skill.bourn, effectCfg);
			}
			else
			{
				Buff buff = this.buffs.get(status.buffID);
				if (buff != null && buff.spiritEffectID == eid)
					this.removeBuff(buff);

				this.passiveBuffs.remove(sid);
			}
		}
	}
	
	private void updateSpiritAddAi(SBean.SpiritEffectCFGS effectCfg, boolean add, Map<Integer, Integer> trigAis)
	{
		int eid = effectCfg.id;
		for (Integer aid : effectCfg.param1)
			updateAi(add, aid, eid, trigAis);
	}
	
	private void updateAi(boolean add, int aid, int effectID, Map<Integer, Integer> trigAis)
	{
		if(add)
		{
			TrigerAi trigAi = this.addTrigAi(aid, effectID);
			if (trigAi != null)
				trigAis.put(aid, trigAi.id);
		}
		else
		{
			Integer instanceID = trigAis.remove(aid);
			if (instanceID != null)
				this.trigerAiMgr.removeTrigerAi(instanceID);
		}
	}

	private void updateSpecialBuff(boolean add, int eid, int buffID)
	{
		long now = ms.getMapManager().getMapLogicTime();
		if (add)
		{
			SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(buffID);
			if (buffCfg == null)
				return;

			Buff buff = this.buffs.get(buffID);
			if (buff != null && !this.buffsRemoveCache.contains(buff))
				this.removeBuff(buff);

			Buff newBuff = new Buff(-1, buffID, 0, now, 1, buffCfg.affectValue, 0, 0, 0, "", (byte)0, eid, false, buffCfg.hasShowID == 1);
			this.buffsAddCache.add(newBuff);
		}
		else
		{
			Buff buff = this.buffs.get(buffID);
			if (buff != null && buff.spiritEffectID == eid)
				this.removeBuff(buff);
		}
	}
	
	private void updateSpiritAddBuff(SBean.SpiritEffectCFGS effectCfg, boolean add)
	{
		int eid = effectCfg.id;
		for (int buffID : effectCfg.param1)
			updateSpecialBuff(add, eid, buffID);
	}

	private int createKeyBySpiritEffect(SBean.SpiritEffectCFGS effectCfg)
	{
		switch (effectCfg.type)
		{
		case GameData.SPIRIT_EFFECT_FIXSUBDAMAGE:
			return effectCfg.param2 * 10 + effectCfg.param3;
		case GameData.SPIRIT_EFFECT_FIXBASESKILL:
			return effectCfg.param2;
		case GameData.SPIRIT_EFFECT_FIXAI:
			return effectCfg.param2 * 10 + effectCfg.param3;
		default:
			break;
		}
		return effectCfg.param2;
	}

	private void updateSpiritOther(SBean.SpiritEffectCFGS effectCfg, boolean add, Map<Integer, SpiritCluster> spirits)
	{
		SpiritCluster cluster = spirits.get(effectCfg.type);
		int key = this.createKeyBySpiritEffect(effectCfg);
		for (int k : effectCfg.param1)
		{
			if (add)
			{
				if (cluster == null)
				{
					cluster = new SpiritCluster();
					spirits.put(effectCfg.type, cluster);
				}

				SpiritContainer container = cluster.containers.get(k);
				if (container == null)
				{
					container = new SpiritContainer();
					cluster.containers.put(k, container);
				}

				container.spiritEffects.put(key, effectCfg);
			}
			else
			{
				if (cluster == null)
					continue;

				SpiritContainer container = cluster.containers.get(k);
				if (container == null)
					continue;

				container.spiritEffects.remove(key);

				if (container.spiritEffects.isEmpty())
					cluster.containers.remove(k);

				if (cluster.containers.isEmpty())
					spirits.remove(effectCfg.type);
			}
		}
	}
	
	public void onUpdateCurSpirit(Set<Integer> curSpirits)
	{
		Set<Integer> same = new HashSet<>();
		for(int spiritID: this.propRole.getCurSpirits())
		{
			if(curSpirits.contains(spiritID))
				same.add(spiritID);
			else
				this.setSpiritEffect(spiritID, false, -1);
		}

		this.propRole.onUpdateCurSpirit(curSpirits);
		this.updatePetFightProps();
		this.onPropUpdate();
		
		for(int spiritID: this.propRole.getCurSpirits())
		{
			if(same.contains(spiritID))
				continue;
			
			this.setSpiritEffect(spiritID, true, -1);
		}
	}
	
	//影响心法被动技能
	public void onUpdateSkill(SBean.DBSkill skill)
	{
		SBean.IntList buffs = this.passiveBuffs.remove(skill.id);
		if(buffs != null)
			this.setSpiritEffect(false, GameData.SPIRIT_EFFECT_SKILLPASV);
		
		this.propRole.onUpdateSkill(skill);
		this.updatePetFightProps();
		this.updateFightSPProp();
		this.updateSkillLvl(skill);
		
		if(buffs != null)
			this.setSpiritEffect(true, GameData.SPIRIT_EFFECT_SKILLPASV);
		
		this.onPropUpdate();
	}
	
	private void updateSkillLvl(SBean.DBSkill skill)
	{
		SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(skill.id);
		if (sCfg == null)
			return;
		
		FightSkill fightSkill = this.fightSkills.get(skill.id);
		if(fightSkill != null)
		{
			int skillLvl = getSkillLvl(skill);
			fightSkill.level = skillLvl;
			fightSkill.realmLvl = skill.bourn;
			SBean.SkillLevelCFGS lvlDataCfg = GameData.getSkillLevelCFG(sCfg, skillLvl);
			if (lvlDataCfg != null)
				fightSkill.lvlFixCfg = lvlDataCfg.fix;
		}
	}
	
	public void onUpdateCurSkill(List<Integer> curSkills)
	{
		this.curSkills = curSkills;
//		this.resetFightSkills(false);
		this.resetSkills();
		this.resetSkillOrder();
	}

	public void onUpdateCurUniqueSkill(int curUniqueSkill)
	{
		FightSkill curSkill = this.fightSkills.get(this.curUniqueSkill);
		this.curUniqueSkill = curUniqueSkill;
		this.resetUniqueSkill(curSkill == null ? 0 : curSkill.coolDownTime);
		this.resetSkillOrder();
	}
	
	public void onUpdateCurDIYSkill(SBean.DBDIYSkillData curDIYSkill)
	{
		int skillActionID = 0;
		this.curDIYSkill = curDIYSkill;
		if (this.curDIYSkill != null)
		{
			FightSkill skill = new FightSkill(Skill.DIY_SKILL_ID, 1, 0, 0, Skill.eSG_Skill, (float) (1.0 - this.getFightPropF(BaseRole.EPROPID_FASTSKILL)), GameData.SKILL_USE_TYPE_DIY).copySkillData();
			if (skill != null)
			{
				this.fightSkills.put(Skill.DIY_SKILL_ID, skill);
				this.setDiySkillData(skill.baseDataCfg, skill.lvlFixCfg);
				this.normalSkills.add(Skill.DIY_SKILL_ID);
			}
			skillActionID = this.curDIYSkill.skillActionID;
		}

		if (skillActionID == 0)
			this.fightSkills.remove(Skill.DIY_SKILL_ID);
		
		this.resetSkillOrder();
	}

	public void onUpdateTransformInfo(byte transformLevel, byte BWType)
	{
		this.propRole.onUpdateTransformInfo(transformLevel, BWType);
		this.updatePetFightProps();
		this.onPropUpdate();
	}

	public void onUpdateSpirit(SBean.DBSpirit dbSpirit)
	{
		if(this.propRole.isCurSpirit(dbSpirit.id))
			this.setSpiritEffect(dbSpirit.id, false, -1);
		
		this.propRole.onUpdateSpirit(dbSpirit);
		this.updatePetFightProps();
		this.onPropUpdate();
		
		if(this.propRole.isCurSpirit(dbSpirit.id))
			this.setSpiritEffect(dbSpirit.id, true, -1);
	}
	
	public void onUpdateSealSkills(Map<Integer, Integer> skills)
	{
		Set<Integer> same = new HashSet<>();
		Set<Integer> old = new HashSet<>(this.propRole.getAllSealSkills());
		for(Map.Entry<Integer, Integer> e: skills.entrySet())
		{
			int sid = e.getKey();
			if(this.propRole.getSealSkillLevel(sid) == e.getValue())
				same.add(sid);
		}
		
		this.setSpiritEffect(false, GameData.SPIRIT_EFFECT_SKILLPASV);
		this.propRole.onUpdateSealSkill(skills);
		this.updatePetFightProps();
		
		for(int sid: old)
		{
			if(same.contains(sid))
				continue;
			
			SBean.DBSkill dbSkill = this.propRole.getSkill(sid);
			if(dbSkill != null)
				this.updateSkillLvl(dbSkill);
		}
		for(int sid: skills.keySet())
		{
			if(same.contains(sid))
				continue;
			
			SBean.DBSkill dbSkill = this.propRole.getSkill(sid);
			if(dbSkill != null)
				this.updateSkillLvl(dbSkill);
		}
		
		this.setSpiritEffect(true, GameData.SPIRIT_EFFECT_SKILLPASV);
		this.updateFightSPProp();
		this.onPropUpdate();
	}
////////////////////////////////////////////////技能属性////////////////////////////////////////////////
	
////////////////////////////////////////////////神兵属性////////////////////////////////////////////////
	void onUpdateWeapon(SBean.DBWeapon weapon)
	{
		if (this.propRole.onUpdateWeapon(weapon))
			this.updateWeaponUniqueSkills();
		this.updatePetFightProps();
		this.onPropUpdate();
	}

	public void onUpdateWeaponSkill(int weaponID, List<Integer> skills)
	{
		this.propRole.onUpdateWeaponSkill(weaponID, skills);
		
		if(this.weaponMotivate && weaponID == this.curWeapon)
			this.updateWeaponSkill();
	}

	public void onUpdateWeaponTalent(int weaponID, List<Integer> talents)
	{
		if(this.weaponMotivate && weaponID == this.curWeapon)
			this.updateWeaponTalentAi(false);
		
		this.propRole.onUpdateWeaponTalent(weaponID, talents);
		this.updateWeaponFightProps();
		
		if(this.weaponMotivate && weaponID == this.curWeapon)
			this.updateWeaponTalentAi(true);
		
		this.onPropUpdate();
	}

	void updateWeaponFightProps()
	{
		if(this.isInPrivateMap())
			return;
		
		SBean.DBWeapon dbWeapon = this.propRole.getWeapon(this.curWeapon);
		if(dbWeapon == null)
			return;
		
		this.propRole.updateWeaponFightProperties(this.weaponMotivate ? dbWeapon : null);
	}
	
	void updateWeaponUSkillProps()
	{
		if(this.isInPrivateMap())
			return;
		
		this.propRole.updateWeaponUSkillProperties(this.weaponUSkills);
	}
	
	void updateWeaponTalentAi(boolean add)
	{
		SBean.DBWeapon dbWeapon = this.propRole.getWeapon(this.curWeapon);
		if(dbWeapon == null)
			return;
		
		SBean.WeaponCFGS wCfg = GameData.getInstance().getWeaponCFGS(dbWeapon.id);
		if(wCfg == null)
			return;
		
		for(int index = 0; index < dbWeapon.talent.size(); index++)
		{
			SBean.WeaponTalentCFGS tCfg = GameData.getInstance().getWeaponTalentCFGS(wCfg, index + 1);
			if(tCfg == null)
				continue;
			
			if(tCfg.talentEffectType != GameData.WEAPON_TALENT_TYPE_SKILL)
				continue;
			
			int aid = PropFightRole.getListValue(tCfg.talentTriggerList, dbWeapon.talent.get(index));
			if(aid > 0)
				updateAi(add, aid, 0, this.weaponAis);
		}
	}
	
	void onWeaponOpen(int weaponID)
	{
		this.propRole.onWeaponOpen(weaponID);
		this.updateWeaponUniqueSkills();
		this.onPropUpdate();
	}
	
	void updateWeaponUniqueSkills()
	{
		this.updateWeaponUSKillAis(false);
		this.weaponUSkills.clear();
		this.weaponAddExp = 1.f;
		this.weaponDrop.clear();
		for(SBean.DBWeapon dbWeapon: this.propRole.getAllWeapons())
		{
			if(dbWeapon.uniqueSkill.open == 0)
				continue;
			
			SBean.WeaponCFGS wCfg = GameData.getInstance().getWeaponCFGS(dbWeapon.id);
			if(wCfg == null)
				continue;
			
			for(int uniqueSkillID: wCfg.uniqueSkills)
			{
				SBean.WeaponUniqueSkillCFGS wusCfg = GameData.getInstance().getWeaponUSkillCFGS(uniqueSkillID);
				boolean isFullStar = GameData.getInstance().isFullStar(dbWeapon);
				if (wusCfg == null)
					continue;

				switch (wusCfg.effectType)
				{
				case GameData.WEAPON_USKILL_EFFECT_TYPE_ALL_TIME:
					this.tryAddWeaponUnqiueSkill(wusCfg, isFullStar);
					break;
				case GameData.WEAPON_USKILL_EFFECT_TYPE_EQUIP:
					if (this.curWeapon == dbWeapon.id)
						this.tryAddWeaponUnqiueSkill(wusCfg, isFullStar);
					break;
				case GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE:
					if (this.curWeapon == dbWeapon.id && (this.weaponMotivate || this.isInPrivateMap()))
						this.tryAddWeaponUnqiueSkill(wusCfg, isFullStar);
					break;
				default:
					break;
				}
			}
		}
		
		this.updateWeaponUSkillProps();
		this.updateWeaponUSKillAis(true);
	}
	
	private void updateWeaponUSKillAis(boolean add)
	{
		for(java.util.Map.Entry<Integer, Boolean> uSkillID: this.weaponUSkills.entrySet())
		{
			SBean.WeaponUniqueSkillCFGS wusCfg = GameData.getInstance().getWeaponUSkillCFGS(uSkillID.getKey());
			if (wusCfg == null || wusCfg.type != GameData.WEAPON_USKILL_TYPE_ADD_AI || (uSkillID.getValue() ? wusCfg.fullStarParam1 <= 0 : wusCfg.param1 <= 0))
				continue;

			int aid = uSkillID.getValue() ? wusCfg.fullStarParam1 : wusCfg.param1;
			if (aid > 0)
				updateAi(add, aid, 0, this.weaponUSkillAis);
		}
	}
	
	private void tryAddWeaponUnqiueSkill(SBean.WeaponUniqueSkillCFGS wusCfg, boolean isFullStar)
	{
		switch (wusCfg.type)
		{
		case GameData.WEAPON_USKILL_TYPE_WITHPET_PROP:
			if((!this.propRole.isCurFightPet(wusCfg.param1) && wusCfg.param1 != -1) || this.curWeapon != wusCfg.param2)
				return;
			
			this.weaponUSkills.put(wusCfg.id, isFullStar);
			break;
		case GameData.WEAPON_USKILL_TYPE_WITHHOURSE_PROP:
			if((this.propRole.getCurHorseId() != wusCfg.param1 && wusCfg.param1 != -1) || this.curWeapon != wusCfg.param2)
				return;
			
			this.weaponUSkills.put(wusCfg.id, isFullStar);
			break;
		case GameData.WEAPON_USKILL_TYPE_KILL_EXP:
			if(this.curWeapon == wusCfg.param1)
				this.weaponAddExp *= (1.f + (isFullStar ? wusCfg.fullStarParam2 : wusCfg.param2) / 10_000.f);
			break;
		case GameData.WEAPON_USKILL_TYPE_KILL_DROP:
			if(this.curWeapon == wusCfg.param1)
			{
				this.weaponDrop.odd = isFullStar ? wusCfg.fullStarParam2 : wusCfg.param2;
				this.weaponDrop.multiple = isFullStar ? wusCfg.fullStarParam3 : wusCfg.param3;
			}
			break;
		case GameData.WEAPON_USKILL_TYPE_ADD_AI:
			this.weaponUSkills.put(wusCfg.id, isFullStar);
			break;
		default:
			break;
		}
	}
	
	float getWeaponAddExp()
	{
		return this.weaponAddExp;
	}
	
	public int getWeaponMultipleDrop()
	{
		if(this.weaponDrop.odd == 0)
			return 0;
		
		if(GameData.checkRandom(this.weaponDrop.odd))
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_double_drop());
			return this.weaponDrop.multiple;
		}
		
		return 0;
	}
	
	public class WeaponDrop
	{
		int odd;
		int multiple;
		
		void clear()
		{
			this.odd = 0;
			this.multiple = 1;
		}
	}
////////////////////////////////////////////////神兵属性////////////////////////////////////////////////
	
////////////////////////////////////////////////坐骑属性////////////////////////////////////////////////
	void updateHorseFightProps()
	{
		this.propRole.updateHorseFightProperties(this.propRole.getHorse(this.propRole.getCurHorseId()), this.propRole.getAllHorseSkills());
	}
	
	void onUpdateHorse(SBean.HorseInfo info)
	{
		SBean.HorseInfo old = this.propRole.getHorse(info.id);
		this.propRole.onUpdateHorse(info);
		if(info.id == this.propRole.getCurHorseId())
		{
			this.updateHorseFightProps();
			if (old != null)
				this.setHorseSkillEffect(old, false, -1);
			
			this.setHorseSkillEffect(info, true, -1);
		}
		
		this.updatePetFightProps();
		this.onPropUpdate();
	}

	void onUpdateCurUseHorse(int hid)
	{
		SBean.HorseInfo oldInfo = this.propRole.getHorse(this.propRole.getCurHorseId());
		if (oldInfo != null)
			this.setHorseSkillEffect(oldInfo, false, -1);

		this.propRole.onUpdateCurUseHorse(hid);
		this.updateHorseFightProps();
		
		SBean.HorseInfo newInfo = this.propRole.getHorse(this.propRole.getCurHorseId());
		if (newInfo != null)
			this.setHorseSkillEffect(newInfo, true, -1);
		
		this.updateWeaponUniqueSkills();
		this.updatePetFightProps();
		this.onPropUpdate();

	}
	
	void onUpdateHorseSkill(int skillID, int skillLvl)
	{
		SBean.HorseInfo info = this.propRole.getHorse(this.propRole.getCurHorseId());
		int oldSkillLvl = this.propRole.getHorseSkillLvl(skillID);
		
		int updateSKillID = 0;
		if(info != null)
		{
			SBean.HorseCFGS horseCfg = GameData.getInstance().getHorseCFGS(info.id);
			if(horseCfg != null && horseCfg.bornSkill == skillID)
			{
				updateSKillID = horseCfg.bornSkill;
			}
			else
			{
				for (int sid : info.curHorseSkills.values())
				{
					if(skillID == sid)
					{
						updateSKillID = sid;
						break;
					}
				}
			}
			
			SBean.HorseEffectDataCFGS effectDataCfg = GameData.getInstance().getHorseEffectDataCFGS(info.id, oldSkillLvl, updateSKillID);
			if (effectDataCfg != null)
				this.updateHorseSkillOther(effectDataCfg, false, -1);
		}
		
		this.propRole.onUpdateHorseSkill(skillID, skillLvl);
		this.updateHorseFightProps();
		
		if(updateSKillID > 0)
		{
			SBean.HorseEffectDataCFGS effectDataCfg = GameData.getInstance().getHorseEffectDataCFGS(info.id, skillLvl, updateSKillID);
			if (effectDataCfg != null)
				this.updateHorseSkillOther(effectDataCfg, true, -1);
		}
		
		this.updatePetFightProps();
		this.onPropUpdate();
	}
	
	void onChangeHorseShow(int hid, int showID)
	{
		SBean.HorseInfo info = this.propRole.getHorse(hid);
		if (info == null || info.curShowID == showID)
			return;
		
//		info.showIDs.add(showID);
		info.curShowID = showID;
		if(this.curRideHorse != hid)
			return;
		
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_ride_horse(this.id, showID));
	}

	void setHorseSkillEffect(SBean.HorseInfo info, boolean add, int type)
	{
		SBean.HorseCFGS horseCfg = GameData.getInstance().getHorseCFGS(info.id);
		if (horseCfg == null)
			return;

		SBean.HorseGrowUpCGFS growCfg = GameData.getHorseGrowUpCGFS(horseCfg, info.star);
		if (growCfg == null)
			return;
		
		Set<Integer> skills = new HashSet<>(info.curHorseSkills.values());
		if (growCfg.bornOpen > 0 && horseCfg.bornSkill > 0)
			skills.add(horseCfg.bornSkill);
		
		for (int skillID : skills)
		{
			int skillLvl = this.propRole.getHorseSkillLvl(skillID);
			if(skillLvl == 0)
				continue;
			
			SBean.HorseEffectDataCFGS effectDataCfg = GameData.getInstance().getHorseEffectDataCFGS(info.id, skillLvl, skillID);
			if (effectDataCfg == null)
				continue;
			
			this.updateHorseSkillOther(effectDataCfg, add, type);
		}
	}
	
	private void updateHorseSkillOther(SBean.HorseEffectDataCFGS effectDataCfg, boolean bind, int type)
	{
		for (Integer eid : effectDataCfg.effectIDs)
		{
			SBean.SpiritEffectCFGS effectCfg = GameData.getInstance().getSpiritEffectCFGS(eid);
			if (effectCfg == null)
				continue;

			if (effectCfg.type != type && type != -1)
				continue;

			switch (effectCfg.type)
			{
			case GameData.SPIRIT_EFFECT_ADDAI:
				this.updateSpiritAddAi(effectCfg, bind, this.horseSkillAis);
				break;
			case GameData.SPIRIT_EFFECT_FIXSUBDAMAGE:
				this.updateSpiritOther(effectCfg, bind, this.horseSkillSpirits);
				break;
			default:
				break;
			}
		}
	}
	
	private boolean canRide()
	{
		return ms.getMapManager().getMapLogicTime() > this.fightTime && GameData.getInstance().getHorseCommonCFGS().canRideMaps.contains(this.getMapType()) 
				&& !this.checkState(Behavior.forbidRideStates) && (this.alterState == null || this.alterState.alterID == 0);
//		return this.fightTime > ms.getMapManager().getMapLogicTime() || GameData.getInstance().getHorseCommonCFGS().canRideMaps.contains(this.curMap.getMapType()) && !this.checkState(Behavior.forbidRideStates);
	}
	
	void syncRoleCurRideHorse(int horseID)
	{
		if(horseID == this.curRideHorse)
			return;
		
		if(horseID == 0)
		{
			this.roleUnRideHorse(false);
		}
		else if(this.canRide())
		{
			this.roleRideHorse();
		}
	}
	
	boolean inMulRoles()
	{
		return this.mulRoleInfo.leader > 0;
	}
	
	boolean isMulRolesMember()
	{
		return this.mulRoleInfo.leader > 0 && this.mulRoleInfo.members.isEmpty();
	}
	
	boolean isVirtual()
	{
		return isMulRolesMember() || this.weddingCar > 0;
	}
	
	boolean isMyMulRoleMember(int memberID)
	{
		for(int mid: this.mulRoleInfo.members)
		{
			if(mid == memberID)
				return true;
		}
		return false;
	}
	
	void roleRideHorse()
	{
		if (!this.canRide())
			return;
		
		int curUseHorse = this.propRole.getCurHorseId();
		if (curUseHorse <= 0)
			return;

		SBean.HorseInfo info = this.propRole.getHorse(curUseHorse);
		if (info == null)
			return;

		SBean.HorseCFGS horseCfg = GameData.getInstance().getHorseCFGS(curUseHorse);
		if (horseCfg == null)
			return;

		if(this.weaponMotivate)
			this.setWeaponEnd(true);
		
		this.curRideHorse = curUseHorse;
		this.getPropBase().setFightPropFixValue(EPROPID_SPEED, horseCfg.speed);
		if(horseCfg.rideCnt > 1)
		{
			this.mulRoleInfo.leader = this.id;
			this.mulRoleInfo.type = GameData.MULROLE_TYPE_HORSE;
			this.mulRoleInfo.members.clear();
			for(int pos = 0; pos < horseCfg.rideCnt - 1; pos++)
				this.mulRoleInfo.members.add(0);
			
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_mulhorse(this.getRoleDetail(), this.getMulRoleMembers(), this.getLogicPosition(), this.getCurRotation().toVector3F()));
		}
		
		this.tryClearSocialAction();
//		ms.getRPCManager().syncRoleCurRideHorse(this.id, this.getMapID(), this.getMapInstanceID(), this.curRideHorse);
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			 ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_ride_horse(this.id, info.curShowID));
		
		this.dissolveCurPets();
	}

	void roleUnRideHorse(boolean notifySelf)
	{
		if (this.curRideHorse == 0)
			return;

		SBean.HorseCFGS horseCfg = GameData.getInstance().getHorseCFGS(this.propRole.getCurHorseId());
		if (horseCfg == null)
			return;
		
		this.curRideHorse = 0;
		SBean.ClassRoleCFGS classRoleCfg = GameData.getInstance().getClassRoleCFG(this.configID);
		if(classRoleCfg != null)
			this.getPropBase().setFightPropFixValue(EPROPID_SPEED, classRoleCfg.speed);

		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_curridehorse(this.getCurRideHorseID()));
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_unride_horse(this.id));
		
		this.clearMulHorseMember();
		this.changeCurFightPetsImpl();
	}
	
	void clearMulHorseMember()
	{
		this.mulRoleInfo.leader = 0;
		this.mulRoleInfo.type = 0;
		for(int m: this.mulRoleInfo.members)
		{
			MapRole member = this.curMap.getRole(m);
			if(member != null)
				member.onLeaveMulHorse();
		}
		
		this.mulRoleInfo.members.clear();
	}
	
	//memberID > 0 上车，memberID < 0 下车
	void onUpdateMulHorse(int index, int memberID)
	{
		if(memberID > 0)
			this.memberJoinMulHorse(index, memberID);
		else
			this.memberLeaveMulHorse(index);
	}
	
	//horse offset = (offset.x * dir.x - offset.z * dir.z, y , offset.x * dir.z + offset.z * dir.x)
	private GVector3 getMulMemberPos(int index)
	{
		int curUseHorse = this.propRole.getCurHorseId();
		if (curUseHorse <= 0)
			return new GVector3().reset(this.curPosition);

		SBean.HorseInfo info = this.propRole.getHorse(curUseHorse);
		if (info == null)
			return new GVector3().reset(this.curPosition);
		
		SBean.Vector3 offset = GameData.getInstance().getHorseOffSet(info.curShowID, index);
//		GVector3 pos = new GVector3(offset.x * this.curRotation.x - offset.z * this.curRotation.z, 0, offset.x * this.curRotation.z + offset.z * offset.x * this.curRotation.x);
		GVector3 pos = new GVector3(offset.x * this.curRotation.x - offset.z * this.curRotation.z, 0, offset.x * this.curRotation.z + offset.z * this.curRotation.x);
		pos.selfSum(this.curPosition);
		
		return pos;
	}
	
	//index [0-size)
	private void memberJoinMulHorse(int index, int memberID)
	{
		if(index < 0 || index >= this.mulRoleInfo.members.size())
			return;
		
		MapRole member = this.curMap.getRole(memberID);
		if(member == null)
			return;
		
		if(member.weaponMotivate)
			member.setWeaponEnd(true);
		member.tryClearSocialAction();
		
		member.setCurPosition(getMulMemberPos(index));
		SBean.RoleDetail md = member.getRoleDetail();
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_update_mulhorse(index + 1, md));
		for(int rid: this.mulRoleInfo.members)
		{
			if(rid == 0)
				continue;
			
			MapRole role = this.curMap.getRole(rid);
			if(role != null)
				ms.getRPCManager().sendStrPacket(role.id, new SBean.role_update_mulhorse(index + 1, md));
		}
		
		this.mulRoleInfo.members.set(index, memberID);
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		rids.removeAll(this.mulRoleInfo.members);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_update_mulhorse(this.id, index + 1, md));
		
		member.onEnterMulHorse(this);
	}
	
	//index [0-size)
	private Integer getMulRoleMemberID(int index)
	{
		if(index < 0 || index >= this.mulRoleInfo.members.size())
			return null;
		
		return this.mulRoleInfo.members.get(index);
	}
	
	private void memberLeaveMulHorse(int index)
	{
		Integer memberID = getMulRoleMemberID(index);
		MapRole member = this.curMap.getRole(memberID);
		if(member == null)
			return;
		
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_update_mulhorse(index + 1, null));
		for(int rid: this.mulRoleInfo.members)
		{
			if(rid == memberID || rid == 0)
				continue;
			
			MapRole role = this.curMap.getRole(rid);
			if(role != null)
				ms.getRPCManager().sendStrPacket(role.id, new SBean.role_update_mulhorse(index + 1, null));
		}
		
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		rids.removeAll(this.mulRoleInfo.members);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_update_mulhorse(this.id, index + 1, null));
		this.mulRoleInfo.members.set(index, 0);
		
		member.setCurPosition(this.curPosition);
		member.onLeaveMulHorse();
	}
	
	private void onLeaveMulHorse()
	{
		int leaderID = this.mulRoleInfo.leader;
		this.mulRoleInfo.leader = 0;
		this.mulRoleInfo.type = 0;
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_leave_mulhorse(this.getLogicPosition()));
		this.memberLeaveMulRole(leaderID);
	}
	
	private void memberLeaveMulRole(int leaderID)
	{
		//下车后进入周围玩家视野
		Set<Integer> rids = new HashSet<>();
		int gridX = this.getCurMapGrid().getGridX();
		int gridZ = this.getCurMapGrid().getGridZ();
		EnterInfo enterInfo = this.curMap.getEntitiesNearBy(gridX, gridZ, this);
		if(!enterInfo.roles.isEmpty())
		{
			for(int rid: enterInfo.roles.keySet())
			{
				if(rid != this.id && rid != leaderID)
				{
					MapRole r = this.curMap.getRole(rid);
					if(r != null && !r.robot)
						rids.add(rid);
				}
			}
		}
		
		this.leaveVirtual(rids);
	}
	
	void leaveVirtual(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			this.filterAndAddNearByRoleIDs(rids, false);
			this.onSelfEnterNearBy(rids);
		}
		this.changeCurFightPetsImpl();
	}
	
	private void onEnterMulHorse(MapRole leader)
	{
		if(this.weaponMotivate)
			this.setWeaponEnd(true);
		
		this.mulRoleInfo.leader = leader.id;
		this.mulRoleInfo.type = GameData.MULROLE_TYPE_HORSE;
		
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_mulhorse(leader.getRoleDetail(), leader.getMulRoleMembers(), leader.getLogicPosition(), leader.getCurRotation().toVector3F()));
		this.memberEnterMulRole();
	}
	
	private void memberEnterMulRole()
	{
		//上车后离开周围玩家视野
		Set<Integer> rids = super.getRoleIDsNearBy(this);
		rids.remove(this.mulRoleInfo.leader);
		this.enterVirtual(rids);
	}
	
	void enterVirtual(Set<Integer> rids)
	{
		Iterator<Integer> it = rids.iterator();
		while(it.hasNext())
		{
			int rid = it.next();
			MapRole role = this.curMap.getRole(rid);
			if(role == null)
			{
				it.remove();
				continue;
			}
			
			this.beSeenRoles.remove(rid);
			if(!role.delNearByRole(this.id))
				it.remove();
		}
		
		this.onSelfLeaveNearBy(rids, 1);
		this.dissolveCurPets();
		this.clearBlurs(0);
		this.clearSkillEntity();
	}
	
	void onUpdateStayWith(SBean.MulRoleInfo mulRoleInfo)
	{
		if(mulRoleInfo.leader > 0)
			this.createStayWith(mulRoleInfo);
		else
			this.dissolveStayWith();
	}
	
	private void createStayWith(SBean.MulRoleInfo mulRoleInfo)
	{
		if(this.weaponMotivate)
			this.setWeaponEnd(true);
		
		this.tryClearSocialAction();
		MapRole member = null;
		for(int memberID: mulRoleInfo.members)
		{
			member = this.curMap.getRole(memberID);
			if(member != null)
			{
				if(member.weaponMotivate)
					member.setWeaponEnd(true);
				
				member.tryClearSocialAction();
				break;
			}
		}
		
		SBean.RoleDetail leaderDetail = this.getRoleDetail();
		SBean.RoleDetail memberDetail = member == null ? null : member.getRoleDetail();
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		rids.remove(mulRoleInfo.leader);
		rids.removeAll(mulRoleInfo.members);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_staywith(this.id, leaderDetail, memberDetail));
		
		this.mulRoleInfo = mulRoleInfo.kdClone();
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_staywith(leaderDetail, memberDetail, this.getLogicPosition(), this.getCurRotation().toVector3F()));
		
		if(member != null)
		{
			member.mulRoleInfo.leader = mulRoleInfo.leader;
			member.mulRoleInfo.members.clear();
			member.mulRoleInfo.type = mulRoleInfo.type;
			member.setCurPosition(this.curPosition);
			member.setClientLastPos(this.clientLastPos);
			ms.getRPCManager().sendStrPacket(member.id, new SBean.role_staywith(leaderDetail, memberDetail, this.getLogicPosition(), this.getCurRotation().toVector3F()));
			member.memberEnterMulRole();
		}
	}
	
	private void dissolveStayWith()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		rids.remove(this.mulRoleInfo.leader);
		rids.removeAll(this.mulRoleInfo.members);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_dissolve_staywith(this.id));
		
		for(int memberID: mulRoleInfo.members)
		{
			MapRole member = this.curMap.getRole(memberID);
			if(member != null)
			{
				member.mulRoleInfo.leader = 0;
				member.mulRoleInfo.type = 0;
				member.mulRoleInfo.members.clear();
				ms.getRPCManager().sendStrPacket(member.id, new SBean.role_dissolve_staywith());
				member.memberLeaveMulRole(this.mulRoleInfo.leader);
			}
		}
		
		this.mulRoleInfo.leader = 0;
		this.mulRoleInfo.type = 0;
		this.mulRoleInfo.members.clear();
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_dissolve_staywith()); 
	}
////////////////////////////////////////////////坐骑属性////////////////////////////////////////////////

////////////////////////////////////////////////帮派属性////////////////////////////////////////////////
	public void onUpdateSectAura(int auraID, int auraLvl)
	{
		this.propRole.onUpdateSectAura(auraID, auraLvl);
		this.updatePetFightProps();
		this.onPropUpdate();
	}

	public void onResetSectAuras(Map<Integer, Integer> sectAuras)
	{
		this.propRole.onUpdateSectAuras(sectAuras);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
	
	public void onUpdateSectBrief(SBean.SectBrief sectBrief)
	{
		this.sectBrief = sectBrief;
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_update_sectbrief(this.id, sectBrief));
	}
////////////////////////////////////////////////帮派属性////////////////////////////////////////////////
////////////////////////////////////////////////BUFF属性////////////////////////////////////////////////
	void updateBuffProps()
	{
		if(this.isInPrivateMap())
			return;
		
		super.updateBuffProps();
		this.onPropUpdate();
	}
	
	public void onUpdateBuff(SBean.DBBuff dbBuff)
	{
		Buff buff = new Buff(dbBuff.endTime, dbBuff.buff.id, 0, 0, dbBuff.buff.overLays, dbBuff.buff.value, dbBuff.buff.attackerType, 0, 0, "", (byte)0, 0, false, true);
		this.buffsAddCache.add(buff);
	}

	public void onAddBuff(int buffID)
	{
		if (!this.isInWorld())
			return;

		this.addBuffByID(buffID, this, null);
	}
	
	public void syncSuperArenaFailedStreak(int failedStreak)
	{
		if(this.curMap instanceof SuperArenaMap)
		{
			int failedBuff = GameData.getInstance().getSuperArenaFailedBuff(failedStreak);
			SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(failedBuff);
			if(buffCfg == null)
				return;
			
			Buff buff = this.createNewBuff(buffCfg, 0, 0, this, null);
			buff.endTime = -1;
			buff.spiritEffectID = -1;
			this.addBuff(buff, this, null);
		}
	}
////////////////////////////////////////////////BUFF属性////////////////////////////////////////////////

////////////////////////////////////////////////收藏品属性////////////////////////////////////////////////
	void onUpdateAlterState(SBean.DBAlterState alterState)
	{
		int oldAlterID = this.alterState.alterID;
		this.resetAlterSkill(true);
		this.alterState = alterState;
		if (!this.isInWorld())
			return;

		if (alterState.alterID > 0)
		{
			if (this.weaponMotivate)
				this.setWeaponEnd(true);
			
			tryClearSocialAction();
		}

		if (oldAlterID != alterState.alterID)
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_taskalter(alterState));
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_alterstate(this.id, this.alterState.alterID));
		}
		this.resetAlterSkill(false);
	}
	
	void onUpdateMedal(int medal, byte state)
	{
		this.propRole.onUpdateMedal(medal, state);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
////////////////////////////////////////////////收藏品属性////////////////////////////////////////////////
	
////////////////////////////////////////////////参悟属性////////////////////////////////////////////////	
	public void onUpdateGrasp(int graspID, int lvl)
	{
		this.propRole.onUpdateGrasp(graspID, lvl);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
////////////////////////////////////////////////参悟属性////////////////////////////////////////////////	
	
////////////////////////////////////////////////藏书属性////////////////////////////////////////////////
	public void onUpdateRareBook(int bookID, int lvl)
	{
		this.propRole.onUpdateRareBook(bookID, lvl);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
////////////////////////////////////////////////乾坤属性////////////////////////////////////////////////
	public void onDMGTransferPointLvlsUpdate(Map<Integer, Integer> pointLvls)
	{
		this.propRole.onDMGTransferPointLvlsUpdate(pointLvls);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
	
////////////////////////////////////////////////称号////////////////////////////////////////////////
	void onUpdateTitle(int titleID, boolean add)
	{
		this.propRole.onUpdateTitle(titleID, add);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
	
	void onUpdateCurTitle(int titleID, int titleType)
	{
		if(titleType == 0)
		{
		    this.curTimedTitles.removeIf( x -> x.titleId == curPermanentTitle);
		    this.curTimedTitles.add(0, new SBean.DBTitleSlot(titleType, titleID));
			this.curPermanentTitle = titleID;
		}
		else
		{
			if(titleID > 0)
			{
			    this.curTimedTitles.removeIf( x -> x.titletype == titleType);
			    this.curTimedTitles.add(0, new SBean.DBTitleSlot(titleType, titleID));
			}
			else
			{
			    this.curTimedTitles.removeIf( y -> y.titletype == titleType);
			}
		}
		
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty()) {
		    List<SBean.DBTitleSlot> titleList = new ArrayList<>(this.curTimedTitles);
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updatetitle(this.id, titleList));
		}
	}
////////////////////////////////////////////////称号////////////////////////////////////////////////
	
////////////////////////////////////////////////随从成就////////////////////////////////////////////////
	void onUpdatePetAchieves(Set<Integer> achieves)
	{
		this.propRole.onUpdatePetAchieves(achieves);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
	
////////////////////////////////////////////////特权卡属性///////////////////////////////////////////
	public void onUpdateSpecialCardAttr(Map<Integer, Integer> attrs)
	{
		this.propRole.onUpdateSpecialCardAttr(attrs);
		this.updatePetFightProps();
		this.onPropUpdate();
	}
////////////////////////////////////////////////内甲////////////////////////////////////////////////
	void updateArmorFightProps()
	{
		if(this.isInPrivateMap())
			return;
		
		this.propRole.updateArmorFightProperties(this.propRole.getCurArmor());
	}
	
	void onChangeCurArmor(SBean.ArmorFightData curArmor)
	{
		this.updateArmorTalentAi(this.propRole.getCurArmor(), false);
		boolean first = this.propRole.getCurArmor() == null;
		this.propRole.onChangeCurArmor(curArmor);
		this.updateArmorFightProps();
		this.updateArmorTalentAi(this.propRole.getCurArmor(), true);
		this.onPropUpdate();
		if(first)
		{
			this.setArmorVal(this.maxArmorValue);
			notifySelfArmorValUpdate();
		}
		
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updatearmor(this.id, getArmorBrief(curArmor)));
	}
	
	void onUpdateArmorLevel(int armorLevel)
	{
		this.propRole.onUpdateArmorLevel(armorLevel);
		this.onPropUpdate();
	}
	
	void onUpdateArmorRank(int armorRank)
	{
		this.propRole.onUpdateArmorRank(armorRank);
		this.onPropUpdate();
		
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updatearmor(this.id, getArmorBrief(this.propRole.getCurArmor())));
	}
	
	void onUpdateArmorRune(int index, List<Integer> runes)
	{
		this.propRole.onUpdateArmorRune(index, runes);
		this.updateArmorFightProps();
		this.onPropUpdate();
	}
	
	void onUpdateArmorTalent(Map<Integer, Integer> talentPoint)
	{
		this.updateArmorTalentAi(this.propRole.getCurArmor(), false);
		
		this.propRole.onUpdateArmorTalent(talentPoint);
		this.updateArmorFightProps();
		this.updateArmorTalentAi(this.propRole.getCurArmor(), true);
		this.onPropUpdate();
	}
	
	void updateArmorTalentAi(SBean.ArmorFightData armor, boolean add)
	{
		if(armor == null)
			return;
		
		for(Map.Entry<Integer, Integer> e: armor.talentPoint.entrySet())
		{
			final SBean.ArmorTalentCFGS arc = GameData.getInstance().getArmorTalentCFGS(armor.id, e.getKey());
			if(arc == null || arc.effectType != GameData.ARMOR_TALENT_TYPE_SKILL)
				continue;
			
			int aid = PropFightRole.getListValue(arc.trigSkills, e.getValue());
			if(aid > 0)
				updateAi(add, aid, 0, this.armorAis);
		}
	}
////////////////////////////////////////////////内甲////////////////////////////////////////////////
	
////////////////////////////////////////////////道具永久属性////////////////////////////////////////////////	
	public void onUpdateItemProps(Map<Integer, Integer> props)
	{
		this.propRole.onUpdateItemProps(props);
		this.onPropUpdate();
	}
////////////////////////////////////////////////道具永久属性////////////////////////////////////////////////	
	
	void updatePetFightProps()
	{
		SBean.PetHost host = this.propRole.getMapPetHost();
		if (this.selfPethost.hostAtkw != host.hostAtkw || this.selfPethost.hostAtkw != host.hostAtkw || this.selfPethost.hostAtkc != host.hostAtkc || this.selfPethost.hostDefc != host.hostDefc || this.selfPethost.hostMasterW != host.hostMasterW || this.selfPethost.hostMasterC != host.hostMasterC || this.selfPethost.spiritTotalLays != host.spiritTotalLays || this.selfPethost.weaponTotalLays != host.weaponTotalLays)
		{
			this.selfPethost = host;
//			PetCluster cluster = ms.getMapManager().getPetCluster(this.getID());
			PetCluster cluster = this.curMap.getPetCluster(this.getID());
			if (cluster != null)
			{
				cluster.pets.values().stream().filter(pet -> pet.getID() > 0).forEach(pet -> pet.onUpdateHost(host));
			}
		}
	}

	public void setCurPosition(GVector3 curPosition)
	{
		super.setCurPosition(curPosition);
		
		for(int index = 0; index < this.mulRoleInfo.members.size(); index++)
		{
			int rid = this.mulRoleInfo.members.get(index);
			if(rid == 0)
				continue;
			
			MapRole member = this.curMap.getRole(rid);
			if(member != null)
			{
				member.setClientLastPos(this.clientLastPos);
				member.setNewPosition(getMulMemberPos(index));
			}
			
		}
	}
	
	boolean changeMapGrid(MapGrid grid)
	{
		if (grid != this.curMapGrid)
		{
			this.curMapGrid.delRole(this);
			grid.addRole(this);
			return true;
		}
		return false;
	}

	private void addSPOnUseSkill(int sp)
	{
		this.setCurSP(this.curSP + sp);
	}

	void addSpiritBuff(int buffID, int skillID, int attackerRealmLvl, SBean.SpiritEffectCFGS effectCfg)
	{
		SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(buffID);
		if (buffCfg == null)
			return;

		long curTime = ms.getMapManager().getMapLogicTime();
		Buff buff = this.buffs.get(buffID);
		float value = 0.f;
		float realmAdd = (float) (buffCfg.realmAdd * attackerRealmLvl * effectCfg.param4 / 10000.0f);
		if (buffCfg.valueType == GameData.VALUE_TYPE_FIXED)
			value = buffCfg.affectValue * (1 + realmAdd);
		else
			value = buffCfg.affectValue;

		value *= effectCfg.param3 / 10000.0f;

		if (buff != null && !this.buffsRemoveCache.contains(buff))
			this.removeBuff(buff);

		Buff newBuff = new Buff(-1, buffID, 0, curTime, 1, (int) value, this.getEntityType(), this.id, this.id, this.roleName, (byte)this.configID, effectCfg.id, false, buffCfg.hasShowID == 1);
		this.buffsAddCache.add(newBuff);

		for (int subBuffID : buffCfg.child)
			this.addSpiritBuff(subBuffID, skillID, attackerRealmLvl, effectCfg);
	}

	//同步位置信息
	private void syncPosition()
	{
//		if (this.getLogicPosition() != null && this.syncPos != null)
//		{
//			if (!this.getCurPosition().equals(this.syncPos))
//			{
//				ms.getRPCManager().syncRoleLocation(this.getID(), this.getMapID(), this.getMapInstanceID(), new SBean.Location(this.getLogicPosition(), this.curRotation.toVector3F()));
//				syncPos.reset(this.getCurPosition());
//			}
//		}
		
		if (!this.clientLastPos.equals(this.syncPos))
		{
			ms.getRPCManager().syncRoleLocation(this.getID(), this.getMapID(), this.getMapInstanceID(), new SBean.Location(this.clientLastPos.toVector3(), this.curRotation.toVector3F()));
			syncPos.reset(this.clientLastPos);
		}
	}

	private void updateOrangeName()
	{
		if (this.pkInfo.value > GameData.RED_NAME_MIN)
		{
			this.orangeNameTime = 0;
			return;
		}

		if (this.orangeNameTime <= 0)
			return;

		int tempGrade = this.getNameGrade();
		this.orangeNameTime--;
		int grade = this.getNameGrade();
		if (tempGrade != grade)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_update_pkinfo(this.getID(), grade, this.getPKState()));
		}
	}

	private void updatePKState()
	{
		if (this.curMap.isFightMap())
			return;

		if (this.pkStateTime <= 0)
			return;

		this.pkStateTime--;
		if (pkStateTime == 0)
		{
			int grade = this.getNameGrade();
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_update_pkinfo(this.getID(), grade, 0));
		}
	}

	boolean effectPKState(MapRole attacker)
	{
		if (!this.isInWorld() || this.curMap.isFightMap() || this.curMap.isPKMap())
			return false;
		
		return this.carRobber >= 0 && attacker.carRobber * this.carOwner >= 0;		//劫镖者（暴露的）被杀，劫镖者杀运镖的不加pk值
	}
	
	void resetPKState(boolean red)
	{
		boolean notify = false;
		if (this.pkStateTime == 0)
			notify = true;
		this.pkStateTime = GameData.getInstance().getCommonCFG().pk.pkKeepTime;

		int tempGrade = this.getNameGrade();
		if (!red && this.getNameColor() != GameData.NAME_COLOR_RED)
			this.orangeNameTime = GameData.getInstance().getCommonCFG().pk.orangeNameKeepTime;

		int grade = this.getNameGrade();
		if (notify || tempGrade != grade)
		{
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_update_pkvalue(this.pkInfo.value));
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_update_pkinfo(this.getID(), grade, this.getPKState()));
		}
	}

	//神兵激活结束
	private void onMotivateEnd(long logicTime)
	{
		if (this.weaponMotivate && logicTime > this.motivateEndTime)
		{
			this.setWeaponEnd(true);
			this.onMotivateWeaponCnt(this.curWeapon, GameData.WEAPON_MOTIVATE_END);
		}
	}

	private void setWeaponEnd(boolean notifySelf)
	{
		this.setWeaponEndImpl();
		if (!this.isInPrivateMap())
		{
			Set<Integer> rids = this.getRoleIDsNearBy(notifySelf ? null : this);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_motivateend(this.getID(), ms.getMapManager().getTimeTickDeep()));
		}
	}

	private void setWeaponEndImpl()
	{
		this.weaponMotivate = false;
		this.motivateEndTime = 0;
		this.updateWeaponTalentAi(false);
		this.resetFightSkills(true);
		this.updateWeaponMotivate();
	}
	
	private void updateWeaponMotivate()
	{
		this.updateWeaponUniqueSkills();
		this.updateWeaponFightProps();
		this.onPropUpdate();
	}
	
	int getWeaponLeftTime()
	{
		long left = this.motivateEndTime - ms.getMapManager().getMapLogicTime();
		return (int) (left < 0 ? 0 : left);
	}

	SBean.DBAlterState getAlterState()
	{
		return this.isInWorld() ? this.alterState.kdClone() : new SBean.DBAlterState(0, 0);
	}

	//采矿
	private void onCheckMineral(long logicTime)
	{
		if (this.curMineralID > 0 && logicTime >= this.mineralEndTime)
		{
			int errorCode = 0;
			SBean.CommonMineralCFGS commonMineral = GameData.getInstance().getCommonCFG().mineral;
			Mineral mineral = this.curMap.getMineral(this.curMineralID);
			if (mineral != null)
			{
				float distance = this.curPosition.distance(mineral.getCurPosition());
				if (distance < commonMineral.mineralDistance || this.isInPrivateMap())
				{
					MineralInfo info = mineral.onMineralEnd(this.getID());
					if (info != null)
						errorCode = 1;
				}
			}
			this.curMineralID = 0;

			if(mineral != null)
				ms.getRPCManager().syncEndMine(this.getID(), this.getMapID(), this.getMapInstanceID(), mineral.getConfigID(), mineral.getID(), errorCode == 1);
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.nearby_role_mineralend(errorCode, this.getID()));

			if (!this.isInPrivateMap())
			{
				Set<Integer> rids = this.getRoleIDsNearBy(this);
				if(!rids.isEmpty())
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_mineralend(errorCode, this.getID()));
			}

			//掉落、出怪
			if (errorCode == 1)
			{
				SBean.MineralCFGS mineralCfg = GameData.getInstance().getMineralCFGS(mineral.getConfigID());
				if (mineralCfg != null)
				{
					//掉落
					List<DropItem> dropItems = this.curMap.getDropItemList(this.id, mineralCfg.fixedDropID, mineralCfg.randomDropID, mineralCfg.randomDropCnt, 1, 1, null, mineral.getLogicPosition(), mineral.getConfigID(), mineral.getEntityType(), this.canTakeDrop);
					if (dropItems.size() > 0)
					{
						List<SBean.DropInfo> dropInfo = new ArrayList<>();
						for (DropItem d : dropItems)
							dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));

						ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_drops(mineral.getLogicPosition(), dropInfo));
					}

					//出怪
					int appearRadius = commonMineral.appearRadius;
					if(mineralCfg.monsterOdds > 0 && GameData.checkRandom(mineralCfg.monsterOdds))
					{
						int monsterID = mineralCfg.monsterID;
						int monsterCount = mineralCfg.monsterCount;
						SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(monsterID);
						if (monsterCfg != null)
						{
							for (int i = 0; i < monsterCount; i++)
								this.curMap.createMonster(monsterID, this.createPosition(appearRadius), GVector3.randomRotation(), true, mineralCfg.monsterStandTime * 1000, -1);
						}
					}

					//出场景BUFF
					List<Integer> dropBuffs = GameData.getInstance().getBuffDrops(mineralCfg.buffDropID, mineralCfg.buffDropCnt);
					for (Integer bid : dropBuffs)
					{
						MapBuff mapBuff = new MapBuff(bid, this.ms).createNew(this.createPosition(appearRadius));
						this.curMap.addMapBuff(mapBuff);

						if (this.isInPrivateMap())
							ms.getRPCManager().sendStrPacket(this.getID(), new SBean.drop_mapbuff(new SBean.BriefInfo(mapBuff.getID(), mapBuff.getConfigID(), mapBuff.getLogicPosition())));
					}
				}
				
				if(mineral instanceof SteleMineral)
				{
					SteleMineral steleMineral = SteleMineral.class.cast(mineral);
					steleMineral.onRoleMineralSuccess(this);
				}
			}
		}
	}

	boolean checkCanBeAttack(BaseRole attacker)
	{
		if(attacker.getMonsterSpawnRole() > 0 && attacker.getMonsterSpawnRole() != this.id)
			return false;
		
		if (attacker.owner == null)
			return true;

		if (attacker.owner == this || !attacker.owner.isNearByRoles(this.mulRoleInfo.leader > 0 ? this.mulRoleInfo.leader : this.id))
			return false;
		
		if (!this.active || attacker.isInProtectTime() || this.isInProtectTime() || attacker.owner.isTeamMember(this))
			return false;

		if(!attacker.owner.isSectMember(this) && (attacker.owner.carRobber * this.carOwner != 0 || this.carRobber < 0))
			return true;
		
		if(attacker.owner.level < GameData.getInstance().getCommonCFG().pk.needLvl || this.level < GameData.getInstance().getCommonCFG().pk.needLvl)
			return false;
		
		switch (attacker.owner.getPKMode()) 
		{
		case GameData.ATTACK_MODE_PEACE:
			return false;
		case GameData.ATTACK_MODE_BW:
			return this.getNameColor() == GameData.NAME_COLOR_RED && !attacker.owner.isSectMember(this);
		case GameData.ATTACK_MODE_SECT:
			return !attacker.owner.isSectMember(this);
		default:
			return true;
		}
	}
	
	//获取目标
	List<BaseRole> getTargets(Skill skill)
	{
		SBean.Scope scope = skill.baseFixCfg.scope;
		int skillType = skill.baseFixCfg.type;
		int maxTargets = skill.baseFixCfg.maxTargets;
		List<BaseRole> targets = new ArrayList<>();

		//技能范围类型（自身、单点技能）
		if (scope.type == Skill.eSScopT_Owner)
		{
			targets.add(this);
			return targets;
		}
		else if (scope.type == Skill.eSScopT_Single)	//现在单体的没有的祝福技能
		{
			float skillRange = scope.args.get(0) + this.getRadius();
			float distance = 0.0f;

			BaseRole entity = null;
			boolean changeRotation = false;
			if (skill.triSkillTarget > 0 && this.lastAttacker != null)
			{
				if (this.checkTargetValid(this.lastAttacker))
				{
					entity = this.lastAttacker;
					changeRotation = true;
				}
				else
					this.lastAttacker = null;
			}

			if (entity == null)
			{
				BaseRole force = this.getForceTarget();
				if (force != null && this.checkTargetValid(force))
					entity = force;
			}

			if (entity != null)
			{
				if (!this.curMap.checkBaseRoleCanAttack(this, entity))
					entity = null;
			}
			else
				changeRotation = true;

			if (entity == null)
				entity = this.getRoleInCheckRange();

			if (entity != null)
			{
				if (changeRotation)
				{
					this.forceTarget = new MapEntity(entity.getEntityType(), entity.id, entity.owner == null ? 0 : entity.owner.id);
					this.setCurRotation(entity.curPosition.diffence2D(this.getCurPosition()).normalize());
					this.notifySelfChangeRotation();
					this.notifySelfChangeTarget(this.forceTarget.entityID, this.forceTarget.entityType, this.forceTarget.ownerID);
				}
				distance = this.curPosition.distance(entity.curPosition);
				if (distance <= skillRange + entity.getRadius())
					targets.add(entity);
			}
			return targets;
		}
		//技能效果类型
		switch (skillType)
		{
		case GameData.eSE_Buff:
			//己方单位
			targets.addAll(this.getRoleFriendEntityNearBy(this));
			targets.add(this);
			break;
		case GameData.eSE_Damage:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());

			//中立单位
			List<Trap> trapsNearBy = this.getTrapNearBy();
			for (Trap t : trapsNearBy)
			{
				if (!t.isDead())
					targets.add(t);
			}
			break;
		case GameData.eSE_DBuff:
			//敌方单位
			targets.addAll(this.getEnemiesNearBy());
			break;
		default:
			break;
		}
		this.filterTarget(targets, this.getNearByRoles());
		targets = skill.checkTarget(this, targets, scope, maxTargets);
		
		return targets;
	}

	private void onUpdateFightSP(long logicTime)
	{
		if (this.configID == GameData.CLASS_TYPE_ARROW)
			return;

		if (this.fightSP <= 0)
			return;

		if (logicTime >= fightStateEndTime)
			this.normalSetFightSP(this.fightSP - 1);
	}

	void onSecondTask(int timeTick, long logicTime)
	{
		if (timeTick > syncTime && !this.robot)
		{
			syncTime = timeTick;
			this.syncPosition();
			this.updateOrangeName();
			this.updatePKState();
			this.checkMovePosition(timeTick);
			this.onMinuteTask(timeTick, logicTime);
			if(marriageInfo != null)
				marriageInfo.onTimer(this, timeTick);
			
			trySit(timeTick);
			if(timeTick % 2 == 0)
				ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_update_timetick(ms.getMapManager().getTimeTickDeep()));
			
			if(timeTick >= this.lastUpdateNearbyTime + UPDATE_NEARBY_ROLE_INTERVAL)
			{
				if(this.active && this.filterRole() && this.nearbyRoles.size() < ms.getConfig().getWorldMaxRole(this.getMapInstanceID() == 0))
				{
					Map<Integer, SBean.EnterDetail> roles = this.curMap.refreshNearByRoleIDs(this.getCurMapGrid().getGridX(), this.getCurMapGrid().getGridZ(), this, this.getNearByRoles());
					if(!roles.isEmpty())
					{
						List<SBean.EnterDetail> lst = this.enterSetNearByRoles(roles);
						batchRolesEnter(lst);
					}
				}
				this.lastUpdateNearbyTime = timeTick;
			}
//			if(timeTick % 5 == 0)
//			{
//				ms.getLogger().debug("==========role " + this.id + " , " + this.roleName + " curMapGrid " + this.curMapGrid.getGridX() + " , " + this.curMapGrid.getGridZ() + " curPosition " + this.curPosition + " map [ " + this.getMapID() + " , " + this.getMapInstanceID() + "]");
//			}
		}
	}
	
	void onMinuteTask(int timeTick, long logicTime)
	{
		if (timeTick - this.tickTimeUpdate >= 10)
		{
			this.tickTimeUpdate = timeTick;
		}
	}

	int getPing()
	{
		return this.ping;
	}
	
	void onRecvPingSync(int ping)
	{
		this.ping = ping;
	}
	
	void onRecvPingStart(SBean.TimeTick timcTick, int ping)
	{
		ms.getRPCManager().sendStrPacket(this.id, new SBean.client_ping_end(timcTick, ms.getMapManager().getTimeTickDeep()));
	}
	
	void checkMovePosition(int timeTick)
	{
		if (this.robot || this.moveSpeed == 0)
			return;
		
		float total = 0;
		for (int speed : this.checkSpeeds)
			total += speed;

		if (total / this.checkSpeeds.size() > this.getFightProp(EPROPID_SPEED) * 2)
			this.adjustPos();
	}

	void onMilliSecondTask(long logicTime)
	{
		super.onMilliSecondTask(logicTime);				//refreshTrigSkills 基类已执行
		this.processFollowSkillDamage(logicTime);
		this.onCheckMineral(logicTime);
		this.onMotivateEnd(logicTime);
		this.onUpdateFightSP(logicTime);
		this.onCheckStopMove(logicTime);
		if(this.checkState(Behavior.EBGOTODEAD) && this.trigSkills.isEmpty())
		{
			this.removeState(Behavior.EBGOTODEAD);
			if(this.isDead())
			{
				this.onDeadHandle(this.dmgRole.roleID, GameData.ENTITY_TYPE_PLAYER);
				this.dmgRole.clear();
			}
		}
		this.checkFightState(logicTime);
		this.onCheckArmor(logicTime);
		this.lastLogicTime = logicTime;
	}
	
	boolean onTimer(int timeTick, long logicTime)
	{
		if (this.curMap == null || (this.curMapGrid == null && !this.isInPrivateMap()) || !this.active)
			return false;

		this.onSecondTask(timeTick, logicTime);
		this.onMilliSecondTask(logicTime);
		
		return false;
	}
	
	void onCheckSkillDuration(long logicTime)
	{
		if(this.robot)
		{
			super.onCheckSkillDuration(logicTime);
		}
		else
		{
			if (this.attack == null)
				return;

			if (this.attack.skillEndTime - timerInterval > logicTime)
				return;

			if(this.checkState(Behavior.EBATTACK))
			{
				this.curStates.remove(Behavior.EBATTACK);
				this.notifyFinishAttack(this.attack);
			}
			
			if (!this.checkState(Behavior.EBATTACK) && !this.checkState(Behavior.EBDISATTACK))
			{
				if (this.attack.rushInfo != null && !this.attack.rushInfo.rushStart && this.attack.valid)
					this.adjustPos();

				this.nextSkill();
			}
			
			if (this.attack.skillEndTime > logicTime)
				return;

			this.attack = null;
//			ms.getLogger().trace("skill " + this.attack.id + " remove state attack " + this.curUseSkills.size() + " at " + logicTime + " , " + GameTime.getTimeMillis() + 
//					" at tick[" + ms.getMapManager().getTimeTick().tickLine + " , " + ms.getMapManager().getTimeTick().outTick + "]");
		}
	}
	
	void checkSkill(long logicTime)
	{
		if (this.robot)
		{
			super.checkSkill(logicTime);
		}
		else
		{
			this.refreshCurUseSkills();
			Set<Skill> inUseSkills = new HashSet<>(this.curUseSkills);
			this.refreshFollowSkills();
			inUseSkills.addAll(this.followSkills.values());
			if (this.attack != null)
				inUseSkills.add(this.attack);

			for (Skill skill : inUseSkills)
			{
				long skillStartTime = skill.skillEndTime - skill.skillDuration;
				int time = skill.baseCommonCfg.spell.time + skill.baseCommonCfg.charge.time;
				if (logicTime > time + skillStartTime && skill.valid)
				{
					if (skill.flyInfo != null)
						this.checkFlySkill(logicTime, skill);

					if (skill.auraInfo != null)
						this.checkAuraSkill(logicTime, skill);

					if (skill.rushInfo != null)
						this.updateRushPos(logicTime, skill);

					if(skill.summonInfo != null)
						this.checkSummonSkill(logicTime, skill);
					
					this.checkChiledSkill(logicTime, skill);
				}
			}

			if (this.curUseSkills.size() > 0)
			{
				this.onAutoProcessDamage(logicTime);
			}
		}
	}

	void onAutoFight(long logicTime)
	{
		if (!this.robot)
		{
			this.updateMovePosition(logicTime);
			return;
		}

		if (this.isDead() || this.isInProtectTime())
			return;

		if (this.checkState(Behavior.EBPREPAREFIGHT))
			return;

		super.onAutoFight(logicTime);
	}

	void processMove(long logicTime)
	{
		this.updateMoveTarget();
		SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
		if (this.curPosition.distance(this.moveTargetPos) <= 5)
			this.onStopMove(this.curPosition, timeTick, true);
		else
		{
			GVector3 moveDir = this.moveTargetPos.diffence2D(this.curPosition).normalize();
			if (!moveDir.equals(this.curRotation))
			{
				this.onMoveMent((int) this.getFightProp(EPROPID_SPEED), moveDir.toVector3F(), this.curPosition, moveTargetPos, timeTick);
				this.preMoveTime = logicTime;
			}
			else
				this.updateMovePosition(logicTime);
		}
	}

	void startMove(long logicTime)
	{
		if (this.robot)
			this.onMoveMent(this.getFightProp(EPROPID_SPEED), this.curRotation.toVector3F(), this.curPosition, this.moveTargetPos, ms.getMapManager().getTimeTickDeep());
	}

	void nextSkill()
	{
		if(!this.robot)
			return;
		
		this.curAttackSeq++;
		if (this.curAttackSeq >= this.attackList.size())
			this.curAttackSeq = 0;

		if (this.robot)
			this.checkWeaponSkill();
		this.curSkillID = null;
		this.setCurSkillID();
	}

	void resetSkillOrder()
	{
		this.skillOrders.clear();
		
		//装备的4个技能
		for(int skillID: this.curSkills)
		{
			FightSkill fs = this.fightSkills.get(skillID);
			if(fs != null)
				this.skillOrders.put(GameData.getLongTypeValue(fs.baseDataCfg.common.priority, skillID), fs);
		}
		
		//绝技
		if(this.curUniqueSkill > 0)
		{
			FightSkill fs = this.fightSkills.get(this.curUniqueSkill);
			if(fs != null)
				this.skillOrders.put(GameData.getLongTypeValue(fs.baseDataCfg.common.priority, this.curUniqueSkill), fs);
		}
		
		//自创武功
		if (this.curDIYSkill != null)
		{
			FightSkill fs = this.fightSkills.get(Skill.DIY_SKILL_ID);
			if(fs != null)
				this.skillOrders.put(GameData.getLongTypeValue(fs.baseDataCfg.common.priority, Skill.DIY_SKILL_ID), fs);
		}
	}
	
	void checkWeaponSkill()
	{
		if (this.curSP >= this.getMaxSP() && !this.weaponMotivate)
		{
			SBean.DBWeapon dbWeapon = this.propRole.getWeapon(this.curWeapon);
			if (dbWeapon == null)
				return;

			SBean.WeaponCFGS cfg = GameData.getInstance().getWeaponCFGS(dbWeapon.id);
			if (cfg == null)
				return;

			this.setCurSP(0);
			this.weaponMotivate = true;
			this.motivateEndTime = ms.getMapManager().getMapLogicTime() + (long) cfg.conTime;
			this.resetFightSkills(true);
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_motivateweapon(this.getID(), dbWeapon.id, dbWeapon.form, ms.getMapManager().getTimeTickDeep()));
		}
	}

	void setCurSkillID()
	{
		if (this.curSkillID != null)
			return;

		if(this.weaponMotivate)		//神兵技能
		{
			SBean.WeaponCFGS wCfg = GameData.getInstance().getWeaponCFGS(this.curWeapon);
			if(wCfg != null)
				this.curSkillID = this.attackList.get(this.curAttackSeq);
		}
		else
		{
			//装备的技能
			if(!this.checkState(Behavior.EBSILENT))
			{
				long now = ms.getMapManager().getMapLogicTime();
				for(FightSkill e: this.skillOrders.values())
				{
					if(now > e.coolDownTime)
					{
						this.curSkillID = e.id;
						break;
					}
				}
			}
			
			//普攻
			if(this.curSkillID == null)
			{
				SBean.ClassRoleCFGS roleCfg = GameData.getInstance().getClassRoleCFG(this.configID);
				if(roleCfg != null)
				{
					int rand = GameRandom.getRandom().nextInt(roleCfg.attacks.size());
					this.curSkillID = roleCfg.attacks.get(rand);
				}
			}
		}
		
		
		if (this.curSkillID == null || this.curSkillID == 0)
		{
			this.nextSkill();
			return;
		}

		this.setAttackRange();
	}
	
	BaseRole getRoleInCheckRange()
	{
		BaseRole entity = null;		
		entity = this.getPlayerEntity();
//		if (entity == null)
//			entity = getNearestTrap(this.getTrapNearBy());
		return entity;
	}

	void onMineralBreakByNone()
	{
		if (this.curMineralID > 0)
			this.notifyMineralBreak();
		this.curMineralID = 0;
	}

	void onMineralBreak()
	{
		if (this.curMineralID > 0)
		{
			Mineral mineral = this.curMap.getMineral(this.curMineralID);
			if (mineral != null)
			{
				mineral.onMineralBreak(this.getID());
				this.notifyMineralBreak();
			}
			this.curMineralID = 0;
		}
	}

	void notifyMineralBreak()
	{
		Mineral mineral = this.curMap.getMineral(this.curMineralID);
		if (mineral == null)
			return;

		ms.getRPCManager().syncEndMine(this.getID(), this.getMapID(), this.getMapInstanceID(), mineral.getConfigID(), mineral.getID(), false);
		ms.getRPCManager().sendStrPacket(this.getID(), new SBean.nearby_role_mineralend(GameData.PROTOCOL_OP_FAILED, this.getID()));

		if (!this.isInPrivateMap())
		{
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_mineralbreak(this.getID()));
		}
	}

	void setBlurEnmity(BaseRole attacker)
	{
		for (Blur b : this.blurs)
		{
			b.addEnemy(attacker);
			b.setAttacked(true);
		}
	}

	Set<Integer> getRoleIDsNearBy(MapRole role)
	{
		if(!this.filterRole())
			return super.getRoleIDsNearBy(role);
		
		if(this.isMulRolesMember())
		{
			MapRole leader = this.curMap.getRole(this.mulRoleInfo.leader);
			if(leader != null)
			{
				Set<Integer> rids = leader.getRoleIDsNearBy(null);
				if(role != null)
					rids.remove(role.id);
				return rids;
			}
			return new HashSet<>();
		}
		
		
		Set<Integer> rids = new HashSet<>(this.beSeenRoles);
		if(role == null)
			rids.add(this.id);
		
		for(int rid: this.mulRoleInfo.members)
		{
			if(rid > 0)
				rids.add(rid);
		}
		return rids;
	}
	
	boolean isNearByRoles(int roleID)
	{
		return this.nearbyRoles.contains(roleID) || !this.filterRole();
	}
	
	void onDamageEnterRole(MapRole attacker)
	{
		if(attacker == null || attacker.id == this.id || this.isNearByRoles(attacker.id))
			return;
		
		this.addNearByRole(attacker.id);
		attacker.beSeenRoles.add(this.id);
		Set<Integer> rids = new HashSet<>();
		rids.add(this.id);
		attacker.onSelfEnterNearBy(rids);
	}
	
	void onSelfEnterHandler(Set<Integer> rids)
	{
		if(!this.filterRole())
			return;
		
		PetCluster cluster = this.curMap.getPetCluster(this.id);
		if(cluster != null)
		{
			List<SBean.EnterPet> pets = new ArrayList<>(); 
			for(Pet p: cluster.pets.values())
				pets.add(p.getEnterPet());
			
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_pets(pets));
		}
		
		if(!this.blurs.isEmpty())
		{
			List<SBean.EnterDetail> blurs = new ArrayList<>();
			for(Blur b: this.blurs)
				blurs.add(b.getEnterDetail());
			
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_blurs(blurs));
		}
		
		if(!this.skillEntitys.isEmpty())
		{
			List<SBean.EnterSkillEntity> lst = new ArrayList<>();
			for(SkillEntity s: this.skillEntitys)
				lst.add(s.getEnterSkillEntity());
			
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_skillentitys(lst));
		}
	}
	
	void onSelfLeaveHandler(Set<Integer> rids, int destory)
	{
		if(!this.filterRole())
			return;
		
		PetCluster cluster = this.curMap.getPetCluster(this.id);
		if(cluster != null)
		{
			List<SBean.PetBase> pets = new ArrayList<>(); 
			for(Pet p: cluster.pets.values())
				pets.add(new SBean.PetBase(p.owner.id, p.id));
			
			if(!pets.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_pets(pets, destory));
		}
		
		if(!this.blurs.isEmpty())
		{
			List<Integer> blurs = new ArrayList<>();
			for(Blur b: this.blurs)
				blurs.add(b.id);
			
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_blurs(blurs));
		}
		
		if(!this.skillEntitys.isEmpty())
		{
			List<Integer> lst = new ArrayList<>();
			for(SkillEntity s: this.skillEntitys)
				lst.add(s.id);
			
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_skillentitys(lst));
		}
	}
	
	boolean addNearByRole(int rid)
	{
		return this.nearbyRoles.add(rid);
	}
	
	boolean delNearByRole(int rid)
	{
		return this.nearbyRoles.remove(rid);
	}
	
	Set<Integer> getNearByRoles()
	{
		return this.nearbyRoles;
	}
	
	int getNearByRoleCnt()
	{
		return this.nearbyRoles.size();
	}
	
	//过滤rids
	void filterAndAddNearByRoleIDs(Set<Integer> rids, boolean forceAdd)
	{
		if(rids.isEmpty() || this.entityType != GameData.ENTITY_TYPE_PLAYER || !this.filterRole())
			return;
		
		Iterator<Integer> it = rids.iterator();
		while(it.hasNext())
		{
			int rid = it.next();
			if(rid == this.owner.id)
				continue;
			
			MapRole role =  this.curMap.getRole(rid);
			if(role == null || !role.enterAddNearByRole(this.id, forceAdd))
				it.remove();
		}
	}
	
	//过滤rids
	void filterAndDelNearByRoleIDs(Set<Integer> rids)
	{
		if(rids.isEmpty() || this.entityType != GameData.ENTITY_TYPE_PLAYER || !this.filterRole())
			return;
		
		Iterator<Integer> it = rids.iterator();
		while(it.hasNext())
		{
			int rid = it.next();
			if(rid == this.owner.id)
			{
				it.remove();
				continue;
			}
			
			MapRole role =  this.curMap.getRole(rid);
			if(role == null || !role.leaveDelNearByRole(this.id))
				it.remove();
		}
	}
	
	boolean enterAddNearByRole(int roleID, boolean forceAdd)
	{	
		MapRole enterRole = this.curMap.getRole(roleID);
		if(enterRole == null || enterRole.weddingCar > 0)
			return false;
		
		if(!forceAdd && !this.isTeamMember(enterRole) && !this.isMyMulRoleMember(roleID))
		{
			if((enterRole.isVirtual()))
				return false;
			
			if(this.getNearByRoleCnt() >= ms.getConfig().getWorldMaxRole(this.getMapInstanceID() == 0))
				return false;
		}
		
//		this.nearbyRoles.add(roleID);
		if(enterRole.beSeenRoles.add(this.id))
		{
			Set<Integer> s = new HashSet<>();
			s.add(roleID);
			this.onSelfEnterHandler(s);
		}
		
		return this.addNearByRole(roleID);
	}
	
	boolean leaveDelNearByRole(int leaveRoleID)
	{
		MapRole leaveRole = this.curMap.getRole(leaveRoleID);
		if(leaveRole != null)
			leaveRole.beSeenRoles.remove(this.id);
		
		if(this.beSeenRoles.remove(leaveRoleID))
		{
			Set<Integer> s = new HashSet<>();
			s.add(leaveRoleID);
			this.onSelfLeaveHandler(s, 0);
		}
		
		return this.delNearByRole(leaveRoleID);
	}
	
	List<SBean.EnterDetail> enterSetNearByRoles(Map<Integer, SBean.EnterDetail> roles)
	{
		if(!this.filterRole())
			return new ArrayList<>(roles.values());
		
		List<SBean.EnterDetail> lst = new ArrayList<>();
		if(this.team != null)
		{
			for(int rid: this.team.members)
			{
				SBean.EnterDetail e = roles.remove(rid);
				if(e != null)
				{
					if(this.enterAddNearByRole(e.base.id, true))
						lst.add(e);
				}
			}
		}
		
		if(this.getNearByRoleCnt() < ms.getConfig().getWorldMaxRole(this.getMapInstanceID() == 0))
		{
			for(SBean.EnterDetail e: roles.values())
			{
				if(e.base.id == this.id)
					continue;
				
				if(!this.enterAddNearByRole(e.base.id, false))
					continue;
				
				lst.add(e);
				
				if(this.getNearByRoleCnt() >= ms.getConfig().getWorldMaxRole(this.getMapInstanceID() == 0))
					break;
			}
		}
		
		return lst;
	}
	
	void leaveSetNearByRoles(List<Integer> roles)
	{
		if(!this.filterRole())
			return;
		
		Iterator<Integer> it = roles.iterator();
		while(it.hasNext())
		{
			int rid = it.next();
			if(!this.leaveDelNearByRole(rid))
				it.remove();
		}
	}
	
	void onSelfEnterSetCarBeSeen(List<SBean.EnterEscortCar> cars)
	{
		Iterator<SBean.EnterEscortCar> it = cars.iterator();
		while(it.hasNext())
		{
			SBean.EnterEscortCar e = it.next();
			EscortCar car = this.curMap.getEscortCar(e.detail.base.id);
			if(car == null)
			{
				it.remove();
				continue;
			}
			
			car.updateBeSeenRoles(this.id, true);
		}
	}
	
	void onSelfLeaveSetCarBeSeen(List<Integer> cars)
	{
		Iterator<Integer> it = cars.iterator();
		while(it.hasNext())
		{
			EscortCar car = this.curMap.getEscortCar(it.next());
			if(car == null)
			{
				it.remove();
				continue;
			}
			
			car.updateBeSeenRoles(this.id, false);
		}
	}
	
	void syncNearByRoleOnEnter()
	{
		if(this.nearbyRoles.isEmpty() || this.robot)
			return;
		
		List<SBean.EnterDetail> roles = new ArrayList<>();
		for(int rid: this.getNearByRoles())
		{
			MapRole r = this.curMap.getRole(rid);
			if(r != null && r.active)
				roles.add(r.getEnterDetail());
		}
		
		batchRolesEnter(roles);
	}
	
	private void batchRolesEnter(List<SBean.EnterDetail> roles)
	{
		if(!roles.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < roles.size())
			{
				int end = start + BATCH_SIZE;
				if(end > roles.size())
					end = roles.size();
				
				if(start != end)
				{
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_roles(roles.subList(start, end)));
//					ms.getLogger().debug("@@@@@2notify rid " + this.id + " role " + this.id + " enter");
				}
				start = end;
			}
		}
	}
	
	void notifyEnterInfo(EnterInfo enterInfo)
	{
		if (!enterInfo.roles.isEmpty())
		{
			List<SBean.EnterDetail> roles = this.enterSetNearByRoles(enterInfo.roles);
			batchRolesEnter(roles);
		}

		if (!enterInfo.pets.isEmpty())
		{
			this.filterEnterPet(this.getNearByRoles(), enterInfo.pets);
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.pets.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.pets.size())
					end = enterInfo.pets.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_pets(enterInfo.pets.subList(start, end)));
				start = end;
			}
		}

		if (!enterInfo.blurs.isEmpty())
		{
			this.filterEnterDatail(this.getNearByRoles(), enterInfo.blurs);
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.blurs.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.blurs.size())
					end = enterInfo.blurs.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_blurs(enterInfo.blurs.subList(start, end)));
				start = end;
			}
		}

		if (!enterInfo.skillEntitys.isEmpty())
		{
			this.filterEnterSkillEntity(this.getNearByRoles(), enterInfo.skillEntitys);
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.skillEntitys.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.skillEntitys.size())
					end = enterInfo.skillEntitys.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_skillentitys(enterInfo.skillEntitys.subList(start, end)));
				start = end;
			}
		}

		if(!enterInfo.cars.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.cars.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.cars.size())
					end = enterInfo.cars.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_escortcars(enterInfo.cars.subList(start, end)));
				start = end;
			}
		}
		
		if (!enterInfo.monsters.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.monsters.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.monsters.size())
					end = enterInfo.monsters.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_monsters(enterInfo.monsters.subList(start, end)));
				start = end;
			}
		}

		if (!enterInfo.npcs.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.npcs.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.npcs.size())
					end = enterInfo.npcs.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_npcs(enterInfo.npcs.subList(start, end)));
				start = end;
			}
		}

		if (!enterInfo.mapBuffs.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.mapBuffs.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.mapBuffs.size())
					end = enterInfo.mapBuffs.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_mapbuffs(enterInfo.mapBuffs.subList(start, end)));
				start = end;
			}
		}

		if (!enterInfo.minerals.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.minerals.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.minerals.size())
					end = enterInfo.minerals.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_minerals(enterInfo.minerals.subList(start, end)));
				start = end;
			}
		}

		if (!enterInfo.traps.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.traps.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.traps.size())
					end = enterInfo.traps.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_traps(enterInfo.traps.subList(start, end)));
				start = end;
			}
		}

		if (!enterInfo.wayPoints.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < enterInfo.wayPoints.size())
			{
				int end = start + BATCH_SIZE;
				if(end > enterInfo.wayPoints.size())
					end = enterInfo.wayPoints.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_waypoints(enterInfo.wayPoints.subList(start, end)));
				start = end;
			}
		}
		
		if(!enterInfo.wcars.isEmpty())
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_enter_weddingcars(enterInfo.wcars));
		}
	}

	void onSelfEnterNearBy(Set<Integer> rids)
	{
		if(!rids.isEmpty())
		{
			List<SBean.EnterDetail> roles = new ArrayList<>();
			roles.add(this.getEnterDetail());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_enter_roles(roles));
//			ms.getLogger().debug("@@@@@1notify rids " + rids + " role " + this.id + " enter");
			this.onSelfEnterHandler(rids);
		}
	}
	
	void onSelfLeaveNearBy(Set<Integer> rids, int destory)
	{
		if(!rids.isEmpty())
		{
			List<Integer> lst = new ArrayList<>();
			lst.add(this.getID());
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_leave_roles(lst, destory));
			
			this.onSelfLeaveHandler(rids, destory);
		}
	}
	
	void notifyLeaveInfo(LeaveInfo leaveInfo, int destory)
	{
		if(!leaveInfo.roles.isEmpty())
		{
			this.leaveSetNearByRoles(leaveInfo.roles);
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.roles.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.roles.size())
					end = leaveInfo.roles.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_roles(leaveInfo.roles.subList(start, end), destory));
				start = end;
			}
		}
		
		if (!leaveInfo.pets.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.pets.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.pets.size())
					end = leaveInfo.pets.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_pets(leaveInfo.pets.subList(start, end), destory));
				start = end;
			}
		}

		if (!leaveInfo.blurs.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.blurs.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.blurs.size())
					end = leaveInfo.blurs.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_blurs(leaveInfo.blurs.subList(start, end)));
				start = end;
			}
		}

		if (!leaveInfo.skillEntitys.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.skillEntitys.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.skillEntitys.size())
					end = leaveInfo.skillEntitys.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_skillentitys(leaveInfo.skillEntitys.subList(start, end)));
				start = end;
			}
		}
		
		if(!leaveInfo.cars.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.cars.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.cars.size())
					end = leaveInfo.cars.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_escortcars(leaveInfo.cars.subList(start, end)));
				start = end;
			}
		}
		
		if (!leaveInfo.monsters.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.monsters.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.monsters.size())
					end = leaveInfo.monsters.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_monsters(leaveInfo.monsters.subList(start, end)));
				start = end;
			}
		}

		if (!leaveInfo.npcs.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.npcs.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.npcs.size())
					end = leaveInfo.npcs.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_npcs(leaveInfo.npcs.subList(start, end)));
				start = end;
			}
		}

		if (!leaveInfo.mapBuffs.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.mapBuffs.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.mapBuffs.size())
					end = leaveInfo.mapBuffs.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_mapbuffs(leaveInfo.mapBuffs.subList(start, end)));
				start = end;
			}
		}

		if (!leaveInfo.minerals.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.minerals.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.minerals.size())
					end = leaveInfo.minerals.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_minerals(leaveInfo.minerals.subList(start, end)));
				start = end;
			}
		}

		if (!leaveInfo.traps.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.traps.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.traps.size())
					end = leaveInfo.traps.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_traps(leaveInfo.traps.subList(start, end)));
				start = end;
			}
		}

		if (!leaveInfo.wayPoints.isEmpty())
		{
			final int BATCH_SIZE = 50;
			int start = 0;
			while(start < leaveInfo.wayPoints.size())
			{
				int end = start + BATCH_SIZE;
				if(end > leaveInfo.wayPoints.size())
					end = leaveInfo.wayPoints.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_waypoints(leaveInfo.wayPoints.subList(start, end)));
				start = end;
			}
		}
		
		if(!leaveInfo.wcars.isEmpty())
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_leave_weddingcars(leaveInfo.wcars));
		}
	}
	
	void onSelfDead(Set<Integer> rids, int killerID)
	{
		killerID = (this.redNameDamageBuff == GameData.getInstance().getCommonCFG().pk.punishDamageBuff && killerID == this.id) ? -killerID : killerID;
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_dead(this.getID(), killerID));
	}

	void notifyAddBuff(Set<Integer> rids, int buffID, int realmLvl, int remainTime, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_addbuff(this.getID(), buffID, realmLvl, remainTime, timeTick));
	}

	void notifyRemoveBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_removebuff(this.getID(), buffID, timeTick));
	}

	void notifyDispelBuff(Set<Integer> rids, int buffID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_dispelbuff(this.getID(), buffID, timeTick));
	}

	void notifyUpdateHp(Set<Integer> rids)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updatehp(this.getID(), this.getCurHP()));
	}

	void notifyBuffDamage(Set<Integer> rids, int attackerType, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_buffdamage(this.getID(), this.getCurHP(), attackerType, timeTick));
	}

	void notifySelfGetDamage(Set<Integer> rids, BaseRole attacker, int ownerID, SBean.DamageResult res, int skillID, int curDamageEventID, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_ondamage(this.getID(), attacker.getID(), attacker.getEntityType(), ownerID, skillID, curDamageEventID, this.getCurHP(), 
					res.dodge, res.deflect, res.crit, res.suckBlood, res.behead, res.remit, res.armor, timeTick));
		
//		if(res.armor != null)
//			ms.getLogger().debug("@@@ nearby_role_ondamage " + getArmorDamage(res.armor));
	}
	
	void notifySelfReduce(Set<Integer> rids, int reduce)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_reduce(this.id, reduce));
	}

//	void notifySelfBeHead(Set<Integer> rids)
//	{
//		if (!rids.isEmpty())
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_behead(this.id));
//	}

	void onGetBuffDamageHandler(Buff buff, int value)
	{
		if(value >= 0)
			return;
		
		if(buff.attackOwnerID > 0)
		{
			MapRole attacker = this.curMap.getRole(buff.attackOwnerID);
			if(attacker != null)
			{
				if(this.carOwner == 1)
					attacker.exposeCarRobber();
				
				if(this.curMap instanceof ForceWarMap)
					this.damageRoles.add(buff.attackOwnerID);
			}
		}
		this.dmgBreakSocialAction();
	}
	
	void onGetDamageHandler(BaseRole attacker, int damage)
	{
		this.onMineralBreak();
		this.setBlurEnmity(attacker);
		if (attacker.owner != null)
		{
			if(this.effectPKState(attacker.owner))
				attacker.owner.resetPKState(this.getNameColor() == GameData.NAME_COLOR_RED);
			
			if(this.carOwner == 1)
				attacker.owner.exposeCarRobber();
			
			if(this.curMap instanceof ForceWarMap)
				this.damageRoles.add(attacker.owner.getID());
		}
		this.dmgBreakSocialAction();
	}

	void addEbataunt(Buff buff, SBean.TimeTick timeTick)
	{
		this.forceTarget = new MapEntity(buff.attackerType, buff.attackerID, buff.attackOwnerID);
		BaseRole entity = this.getForceTarget();
		if (entity != null)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_addataunt_role(this.getID(), buff.attackerID, buff.attackerType, buff.attackOwnerID, timeTick));
		}
	}

	void onDeadHandle(int attackerType, int attackerID)
	{
		super.onDeadHandle(attackerType, attackerID);
		if(this.getCurSP() > 0)
			this.setCurSP(this.getCurSP() / 2);
		
		if (this.weaponMotivate)
			this.setWeaponEndImpl();
		
		this.clearAllChildren(this.curMap.roleDeadClearChild(), attackerID);
		
		if(this.marriageInfo != null)
			this.marriageInfo.tryDisEffectSkill(this);
		this.clearAllBuff();
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		this.onSelfDead(rids, attackerID);
		this.redNameDamageBuff = 0;
		
		MapRole killer = null;
		if (attackerType == GameData.ENTITY_TYPE_PLAYER)
		{
			killer = this.curMap.getRole(attackerID);
			if (killer != null && killer != this && !this.isTeamMember(killer))
			{
				killer.addKillRole(this);
				if (this.getNameColor() == GameData.NAME_COLOR_WHITE && this.effectPKState(killer))
				{
					killer.setPKValue(killer.pkInfo.value + 1);
					ms.getRPCManager().syncAddPKValue(killer.id, killer.getMapID(), killer.getMapInstanceID(), 1);
				}
			}
		}
		this.curMap.onMapRoleDead(this, killer);
		if(killer != null)
			this.curMap.addRoleMapKills(killer, this);
		
		if(this.curMap instanceof ForceWarMap)
		{
			ForceWarMap fwm = ForceWarMap.class.cast(this.curMap);
			fwm.clearForceWarKillStreaks(this.id);
		}
		
		this.curMap.setAutoRevive(this.id);
		this.damageRoles.clear();
	}
	
	void clearAllChildren(boolean clear, int attackerID)
	{
		if (clear)
		{
			PetCluster cluster = this.curMap.getPetCluster(this.getID());
			if (cluster != null)
			{
				for (Pet pet : cluster.pets.values())
				{
					pet.curHP = 0;
					pet.setCurSP(0);
					Set<Integer> rids = pet.getRoleIDsNearBy(null);
					pet.onSelfDead(rids, attackerID);
					pet.clearSummonMonster(attackerID);
				}
			}
		}
		this.clearSummonMonster(attackerID);
		this.clearBlurs(attackerID);
	}

	void clearBlurs(int attackerID)
	{
		if (this.configID != GameData.CLASS_TYPE_ARROW || this.blurs.isEmpty())
			return;

		for (Blur b : this.blurs)
		{
			Set<Integer> rids = b.getRoleIDsNearBy(null);
			b.onSelfDead(rids, attackerID);
			b.curHP = 0;
			this.curMap.delBlurToRemoveCache(b.id);
		}
		this.blurs.clear();

		if (this.fightSP != 0)
			this.normalSetFightSP(0);
	}
	
	void clearSkillEntity()
	{
		if(this.skillEntitys.isEmpty())
			return;
		
		for(SkillEntity s: this.skillEntitys)
		{
			Set<Integer> rids = s.getRoleIDsNearBy(null);
			s.onSelfLeaveNearBy(rids, 1);
			if (s.skill.auraInfo != null)
			{
				for (BaseRole e : s.skill.auraInfo.effectEntitys)
					e.removeBuff(s.skill.auraInfo.buff);

				s.skill.auraInfo.effectEntitys.clear();
			}
			this.curMap.delSkillEntityToRemoveCache(s.id);
		}
		this.skillEntitys.clear();
	}
	
	void notifyUpdateMaxHp(Set<Integer> rids)
	{	
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updatemaxhp(this.getID(), this.getMaxHP()));
	}

	void notifySelfShiftEnd(Set<Integer> rids, int skillID, GVector3 endpos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_shiftend_role(skillID, this.getID(), endpos.toVector3(), timeTick));
	}

	void notifySelfRemoveState(int sid, SBean.TimeTick timeTick)
	{
		if (!this.robot && Behavior.notifyStates.contains(sid))
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_removestate(sid, timeTick));
		}
	}

	String getName()
	{
		return this.roleName;
	}

	void notifySelfChangeRotation()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_change_rotation(this.id, this.curRotation.toVector3F(), ms.getMapManager().getTimeTickDeep()));
	}
	
	void notifySelfChangeTarget(int targetID, int targetType, int targetOwnerID)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_change_target(this.id, targetID, targetType, targetOwnerID, ms.getMapManager().getTimeTickDeep()));
	}
	
//	void notifySelfSuckBlood(Set<Integer> rids)
//	{
//		if (!rids.isEmpty())
//			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_suckblood(this.getID(), this.curHP));
//	}

	void notifySelfRushStart(Set<Integer> rids, int skillID, SBean.Vector3 endPos, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_rushstart(this.id, skillID, endPos, timeTick));
	}

	void notifyResetSkill(int skillID)
	{
		if (!this.robot && skillID > 0)
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_reset_skill(skillID));
	}

	void notifyQuickCoolSkill(int skillID, int time)
	{
		if (!this.robot && skillID > 0)
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_quickcool_skill(skillID, time));
	}

//	int getNextDropID()
//	{
//		return this.nextDropID.incrementAndGet();
//	}

//	void setStartDropID(int start)
//	{
//		this.nextDropID = new AtomicInteger(start);
//	}

	void onSelfBlurDead(Blur blur)
	{
		if(!blur.buffDead)
			this.normalSetFightSP(this.fightSP - 1);
		this.blurs.remove(blur);
	}

	public void setCurSP(int sp)
	{
		if (this.weaponMotivate || this.alterState.alterID > 0)
			return;

		int temp = this.curSP;
		if (sp < 0)
			this.curSP = 0;
		else if (sp > this.getMaxSP())
			this.curSP = this.getMaxSP();
		else
			this.curSP = sp;

		if(this.robot)
			return;
		
		ms.getRPCManager().syncRoleSp(this.id, this.getMapID(), this.getMapInstanceID(), this.curSP);
		if (temp != this.curSP && !this.isInPrivateMap())
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_sp(this.curSP));
	}

	public int setCurHP(int curHP)
	{
		if (this.isDead())
			return 0;

		if (curHP <= 0)
		{
			if(this.deadTrig())
			{
				SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
				ms.getRPCManager().sendStrPacket(this.id, new SBean.role_addstate(Behavior.EBGOTODEAD, timeTick));
			}
			curHP = 0;
		}
		else if (curHP > this.getMaxHP())
			curHP = this.getMaxHP();

		if (this.curHP != curHP)
		{
			this.curHP = curHP;
			if (!this.robot)
				ms.getRPCManager().syncRoleHP(this.getID(), this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
		}
		return this.curHP;
	}

	void dmgToSPHandler(BaseRole target)
	{
		RoleState state = this.curStates.get(Behavior.EBDMGTOWEAPONSP);
		if (state == null)
			return;

		if (target.getEntityType() != GameData.ENTITY_TYPE_PLAYER)
			return;

		for (Integer bid : state.buffIDs)
		{
			SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(bid);
			if(buffCfg == null)
				continue;
			
			int specialID = buffCfg.affectValue;
			SBean.SkillSpecialCFGS specialCfg = GameData.getInstance().getSkillSpecialCFG(specialID);
			if (specialCfg == null || specialCfg.formulaID != GameData.SKILL_SPECIAL_WEAPONSP)
				return;

			if(GameData.checkRandom(specialCfg.param1))
			{
				if (specialCfg.param2 == GameData.TARGET_TYPE_TARGET)
					target.setCurSP(target.getCurSP() + specialCfg.param3);
				else if (specialCfg.param2 == GameData.TARGET_TYPE_OWNER)
					this.setCurSP(this.curSP + specialCfg.param3);
			}
		}
	}

	void setBuffFightSP(int value)
	{
		int temp = this.fightSP;
		if(value > 0)
		{
			this.setFightSP(this.fightSP + value);
			if(this.fightSP != temp)
				this.buffSyncFightSP();
			return;
		}
		
		if(this.fightSP <= 0)
			return;
		
		if(this.configID == GameData.CLASS_TYPE_ARROW)
		{
			int count = 0;
			for(Blur blur: this.blurs)
			{
				if(count == value)
					return;
				
				blur.buffDead = true;
				blur.standTime = 0;
				count++;
			}
		}
		else
		{
			this.setFightSP(this.fightSP + value);
		}
		
		if(this.fightSP != temp)
			this.buffSyncFightSP();
	}
	
	public void setFightSP(int fightSP)
	{
		SBean.FightSPCFGS fightCfg = GameData.getInstance().getfFightSPCFGS(this.getConfigID());
		if (fightCfg != null)
		{
			int temp = this.fightSP;
			if (fightSP < 0)
				fightSP = 0;
			else if (fightSP > fightCfg.maxLays)
				fightSP = fightCfg.maxLays;

			if (this.fightSP != fightSP)
			{
				this.fightSP = fightSP;
				this.fightStateEndTime = ms.getMapManager().getMapLogicTime() + (long) fightCfg.duration;

				if (fightCfg.affectType == GameData.FIGHTSP_PROP)
				{
					this.updateFightSPProp();
					this.onPropUpdate();
				}
				else if (fightCfg.affectType == GameData.FIGHTSP_BLUR)
				{
					int count = this.fightSP - temp;
					if (count > 0)
					{
						int blurID = this.getBlurID(fightCfg);
						for (int i = 0; i < count; i++)
						{
							Blur blur = new Blur(blurID, this.ms).createNew(this.createPosition(this.curPosition, MapManager.PET_CREATE_RADIUS), this);
							this.curMap.addBlur(blur);
							blur.onCreateHandle();
							this.blurs.add(blur);
							BaseRole entity = this.getForceTarget();
							if (entity != null)
								blur.addEnemy(entity);
						}
					}
				}
			}
		}
	}
	
	void normalSetFightSP(int fightSP)
	{
		int temp = this.fightSP;
		this.setFightSP(fightSP);
		if(this.fightSP != temp)
			this.normalSyncFightSP();
	}
	
	private void normalSyncFightSP()
	{
		if (!this.robot)
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_fightSP(this.fightSP));
	}
	
	private void buffSyncFightSP()
	{
		if (!this.robot)
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_bufffightSP(this.fightSP));
	}
	
	//没装心法默认为1， 装了心法根据心法层级获取
	int getBlurID(SBean.FightSPCFGS fightCfg)
	{
		int blurID = fightCfg.attrs.get(0).id;
		int spiritID = fightCfg.attrs.get(1).id;
		if (this.propRole.getCurSpirits().contains(spiritID))
		{
			SBean.DBSpirit dbSpirit = this.propRole.getSpirit(spiritID);
			if (dbSpirit != null)
			{
				int lvl = dbSpirit.level / GameData.SPIRIT_LEVLE_PERLAY;
				if (lvl >= 0 && lvl <= fightCfg.attrs.get(1).values.size())
					blurID = fightCfg.attrs.get(1).values.get(lvl);
			}
		}

		return blurID;
	}

	void addFightSP(int contion)
	{
		SBean.FightSPCFGS fightCfg = GameData.getInstance().getfFightSPCFGS(this.getConfigID());
		if (fightCfg == null)
			return;

		if (!fightCfg.conditions.contains(contion))
			return;

		int realmLvl = this.getSkillRealmLevel(fightCfg.relatedSkill);

		if (this.fightSP < fightCfg.maxLays)
		{

			SBean.DBSpirit dbSpirit = this.propRole.getSpirit(fightCfg.spiritID);
			if (dbSpirit != null && this.propRole.getCurSpirits().contains(fightCfg.spiritID))
			{
				int lvl = dbSpirit.level / GameData.SPIRIT_LEVLE_PERLAY;
				int odds = fightCfg.odds + fightCfg.oddsSpirit.get(lvl) + realmLvl * fightCfg.reamlAddOdds;
				if(GameData.checkRandom(odds))
					this.normalSetFightSP(this.fightSP + 1);
				return;
			}

			if (this.checkState(Behavior.EBFIGHTSP))
			{
				int odds = fightCfg.odds + realmLvl * fightCfg.reamlAddOdds;
				if(GameData.checkRandom(odds))
					this.normalSetFightSP(this.fightSP + 1);
			}
		}
	}

	public void onSyncPrivateMapSP(int skillID)
	{
		FightSkill fighSkill = this.fightSkills.get(skillID);
		if (fighSkill == null || fighSkill.skillGroup != Skill.eSG_Skill || this.curWeapon <= 0 || this.weaponMotivate)
			return;

		SBean.SkillCFGS skillCfg = GameData.getInstance().getSkillCFG(skillID);
		if (skillCfg == null)
			return;

		SBean.SkillLevelCFGS skillData = GameData.getSkillLevelCFG(skillCfg, fighSkill.level);
		this.addSPOnUseSkill(skillData.fix.addSP);
	}

	public void onSyncPrivateMapFightSP(int fightSP)
	{
		SBean.FightSPCFGS fightCfg = GameData.getInstance().getfFightSPCFGS(this.getConfigID());
		if (fightCfg == null)
			return;

		if (fightSP < 0)
			fightSP = 0;
		else if (fightSP > fightCfg.maxLays)
			fightSP = fightCfg.maxLays;

		this.normalSetFightSP(fightSP);
	}

	private void addMoveCheckSpeed(int checkSpeed)
	{
		this.checkSpeeds.add(checkSpeed);
		if (this.checkSpeeds.size() > 10)
			this.checkSpeeds.remove(0);
	}

	void onSelfUpdatePosition(Set<Integer> rids, SBean.Vector3 position, SBean.TimeTick timeTick)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updateposition(this.id, position, timeTick));
	}

	void clientAdjustRolePos(SBean.Vector3 position)
	{
		if (this.moveSpeed == 0 || !this.checkState(Behavior.EBMOVE))
			return;
		
		GVector3 newPosition = new GVector3(position);
		this.setCurPosition(newPosition);
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updateposition(this.id, position, ms.getMapManager().getTimeTickDeep()));
	}
	
	void setClientLastPos(GVector3 pos)
	{
		this.clientLastPos.reset(pos);
	}
	
	boolean onMoveMent(int speed, SBean.Vector3F rotation, GVector3 pos, GVector3 targetPos, SBean.TimeTick timeTick)
	{
		int dTime = getTimeInterval(ms.getMapManager().getTimeTick(), timeTick);
//		int dTime = (int) (getTimeByTimeTick(timeTick) - ms.getMapManager().getCurMapLogicTime());
		if(dTime - this.ping > GameData.getInstance().getCommonCFG().engine.interval * 2)
		{
			this.adjustPos();
//			ms.getLogger().debug("onMoveMent client tickLine " + timeTick.tickLine + " server tickLine " + ms.getMapManager().getTimeTick().tickLine + " ping " + this.ping + " at " + GameTime.getTimeMillis());
			ms.getLogger().info("role " + this.id + " onMoveMent client tickLine " + ms.getMapManager().timeTickToString(timeTick) + " server tickLine " + ms.getMapManager().timeTickToString() + " ping " + this.ping + " at " + GameTime.getTimeMillis());
			return false;
		}
		
		if (speed > this.getFightProp(EPROPID_SPEED) * 2 || this.isVirtual())
			return false;

		if (!super.onMoveMent(speed, rotation, pos, targetPos, timeTick))
			return false;

		if (!this.robot)
		{
			if(this.moveCheckInfo.isValid())
			{
				int dTimeServer = getTimeInterval(this.moveCheckInfo.preServerMoveTimeTick);
				int dTimeClient = getTimeInterval(this.moveCheckInfo.preClientMoveTimeTick, timeTick);
				if(dTimeServer > 0 && dTimeClient > 0)
				{
					if(dTimeClient > 0)
						this.addMoveCheckSpeed((int) (this.moveCheckInfo.preMovePosition.distance(pos) / (dTimeClient / 1000.f)));
				}
			}
			this.moveCheckInfo.update(pos, timeTick.kdClone(), ms.getMapManager().getTimeTickDeep());
			
			GVector3 realPos = pos.sum(this.curRotation.scale(this.ping * speed / 1000.0f));
			this.fixNewPosition(this.curRotation, realPos, this.moveTargetPos);
			if(realPos.distance(this.curPosition) > DISTANCEERROR)
				ms.getLogger().trace("@@@@onMoveMent pos error client pos " + pos + " , targetPos " + targetPos + " server pos " + this.curPosition + " calc pos " + 
						realPos + " speed " + speed + " rotation " + this.curRotation + " ping " + this.ping);
			
			this.setClientLastPos(this.curMap.fixPos(pos, this));
			this.setNewPosition(realPos);
		}
		
		this.serverStopTime = 0;
		this.tryClearSocialAction();
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().nearbyRoleMove(rids, this.id, this.getLogicPosition(), this.moveSpeed, rotation, targetPos.toVector3(), timeTick.kdClone());
		
//		ms.getLogger().debug("role" + this.id + " , " + this.roleName + " move at map [" + this.getMapID() + " , " + this.getMapInstanceID() + "]");
//		ms.getLogger().debug("role " + this.getID() + " , " + this.roleName + " curHP: " + this.curHP + " maxHP: " + this.getMaxHP() + " curSP " + this.curSP + " attackMode " + this.pkInfo.mode + " pkValue " + this.pkInfo.value 
//				+ " buff count " + this.buffs.size() + " fightSP " + this.fightSP + " props : " + this.getPropBase());
		return true;
	}
	
	//神模式调式用，打印服务属性
	void showProps(int propID)
	{
//		this.curMap.createMonster(60301, new GVector3().reset(this.getCurPosition()), new GVector3(1, 0, 0), true, -1, -1);
		ms.getLogger().info("role cur pos " + this.curPosition + " client pos " + this.clientLastPos);
		Map<Integer, Integer> props = new HashMap<>();
		if(propID > 0)
		{
			SBean.PropertyCFGS propCfg = GameData.getInstance().getPropertyCFGS(propID);
			if(propCfg != null)
			{
				int value =  getFightProp(propID);
				props.put(propID, value);
				ms.getRPCManager().sendStrPacket(this.id, new SBean.role_show_props(props));
				ms.getLogger().info("role " + this.id + " prop " + propCfg.desc + " [" + propID + " = " + value + "]");
			}
			if(propID == EPROPID_LVL)
			{
				ms.getLogger().info("map [" + this.getMapID() + " " + this.getMapInstanceID() + "] info: ----------------");
				ms.getLogger().info("timeout " + this.curMap.timeout + " isMapFinish " + this.curMap.isMapAlreadyFinish);
				ms.getLogger().info("preparetime " + this.curMap.getMapPrepareTime());
			}
		}
		else
		{
			for(SBean.PropertyCFGS propCfg: GameData.getInstance().getAllPropertyCFGS())
			{
				int value = (int) (propCfg.valueType == 0 ? getFightProp(propCfg.id) : getFightPropF(propCfg.id));
				props.put(propID, value);
			}
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_show_props(props));
			ms.getLogger().info("role " + this.getID() + " , " + this.roleName + " curHP: " + this.curHP + " maxHP: " + this.getMaxHP() + " curSP " + this.curSP + " attackMode " + this.pkInfo.mode + " pkValue " + this.pkInfo.value 
					+ " buff count " + this.buffs.size() + " fightSP " + this.fightSP + " props : " + this.getPropBase());
		}
	}
	
	void gmCommond(String iType, int iArg1, int iArg2, int iArg3, String sArg)
	{
		switch (iType)
		{
		case "worldboss":
			gmWorldBoss(iArg1);
			break;
		default:
			break;
		}
	}
	
	private void gmWorldBoss(int bossID)
	{
		SBean.WorldBossCFGS bossCfg = GameData.getInstance().getWorldBossCFGS(bossID);
		if(bossCfg == null)
			return;
		
		if(this.curMap instanceof WorldMap)
		{
			WorldMap wm = WorldMap.class.cast(this.curMap);
			wm.gmCreateWorldBoss(this, bossID);
		}
	}
	
	private void onCheckStopMove(long logicTime)
	{
		if(this.serverStopTime == 0)
			return;
			
		if(logicTime - this.serverStopTime >= 1000)
		{
			this.serverStopTime = 0;
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().nearbyRoleStopMove(rids, this.id, this.getLogicPosition(), this.getFightProp(EPROPID_SPEED), ms.getMapManager().getTimeTickDeep());
		}
	}
	
	public void onStopMove(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		if(position.distance(this.curPosition) > 700)
		{
			ms.getLogger().debug("@@@@onStopMove pos error client pos " + position + " server pos " + this.curPosition);
			this.adjustPos();
			this.onStopMoveImpl(this.curPosition, timeTick, true);
			return;
		}
		
		if(broadcast)
			this.setClientLastPos(this.curMap.fixPos(position, this));
		
		this.setNewPosition(position);
		this.onStopMoveImpl(position, timeTick, broadcast);
	}
	
	void onStopMoveImpl(GVector3 position, SBean.TimeTick timeTick, boolean broadcast)
	{
		this.moveSpeed = 0;
		this.moveTarget = null;
//		this.preMovePosition = null;
		this.tryClearSocialAction();
		int speed = this.getFightProp(EPROPID_SPEED);
		if(broadcast || this.robot)
		{
			this.moveCheckInfo.reset();
			this.serverStopTime = 0;
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().nearbyRoleStopMove(rids, this.id, this.getLogicPosition(), speed, timeTick);
		}
		
		this.removeState(Behavior.EBMOVE);
		this.removeState(Behavior.EBRETREAT);
	}
	
	void changePosition(int curGridX, int curGridZ, int newGridX, int newGridZ, int destory)
	{
		EnterInfo enterInfo = this.curMap.getSelfEnterEntities(curGridX, curGridZ, newGridX, newGridZ, this);
		LeaveInfo leaveInfo = this.curMap.getSelfLeaveEntities(curGridX, curGridZ, newGridX, newGridZ, this);
		
		if(!enterInfo.roles.isEmpty() && !this.isVirtual())
		{
			Set<Integer> enterRids = new HashSet<>();
			for (int rid: enterInfo.roles.keySet())
			{
				MapRole role = this.curMap.getRole(rid);
				if(role != null && !role.robot)
					enterRids.add(rid);
			}
			this.filterAndAddNearByRoleIDs(enterRids, false);
			this.onSelfEnterNearBy(enterRids);
		}
		this.notifyEnterInfo(enterInfo);
		
		if(!leaveInfo.roles.isEmpty() && !this.isVirtual())
		{
			Set<Integer> leaveRids = new HashSet<>();
			for(int rid: leaveInfo.roles)
			{
				MapRole role = this.curMap.getRole(rid);
				if(role != null && !role.robot)
					leaveRids.add(rid);
			}
			this.filterAndDelNearByRoleIDs(leaveRids);
			this.onSelfLeaveNearBy(leaveRids, destory);
		}
		this.notifyLeaveInfo(leaveInfo, destory);
	}
	
	public boolean canAddHp()
	{
		return !this.checkState(Behavior.EBSTUN);
	}

	public void onAddHp(int hp)
	{
		int oldHp = this.getCurHP();
		this.setCurHP(oldHp + hp);
		if (oldHp != this.getCurHP())
		{
			if (!this.isInPrivateMap())
			{
				Set<Integer> rids = this.getRoleIDsNearBy(null);
				this.notifyUpdateHp(rids);
			}
			else
				ms.getRPCManager().sendStrPacket(this.id, new SBean.nearby_role_updatehp(this.getID(), this.getCurHP()));
		}
	}

	public void onChangeWeapon(int curWeapon)
	{
		if (curWeapon != this.curWeapon && !this.weaponMotivate)
		{
			this.curWeapon = curWeapon;
			this.setCurSP(0);
			
			this.updateWeaponUniqueSkills();
			this.onPropUpdate();
		}
	}
	
	public void setWeaponForm(int weaponID, byte form)
	{
		SBean.DBWeapon dbWeapon = this.propRole.getWeapon(weaponID);
		if(dbWeapon != null)
			dbWeapon.form = form;
	}
	
	public void onRoleRevive(boolean fullHp)
	{		
		if (fullHp)
			this.curHP = this.getMaxHP();
		else
			this.curHP = ((int) (this.getMaxHP() * 0.6));

		SBean.Vector3 pos = (fullHp && this.isInPrivateMap()) ? null : this.getLogicPosition();
		ms.getRPCManager().syncRoleHP(this.getID(), this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
		
		if(fullHp)
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_revive(this.curHP, pos));
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_revive(this.getID(), this.getCurHP(), this.getLogicPosition()));
			
			SBean.CommonCFGS commonCfg = GameData.getInstance().getCommonCFG();
			this.addState(Behavior.EBREVIVE);
			this.specialState.put(Behavior.EBREVIVE, ms.getMapManager().getMapLogicTime() + (long) (commonCfg.revives.protectTime + 500));
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_addstate(Behavior.EBREVIVE, ms.getMapManager().getTimeTickDeep()));
		}
		else if(this.isInPrivateMap())
		{
			SBean.MapClusterCFGS mcCfg = GameData.getInstance().getMapClusterCFGS(this.curMap.getMapID());
			if(mcCfg != null)
				ms.getRPCManager().sendStrPacket(this.id, new SBean.role_revive(this.curHP, new SBean.Vector3(mcCfg.revivePos.x, mcCfg.revivePos.y, mcCfg.revivePos.z)));
		}

		this.clearAllSkillsCD();
		if(this.curMap instanceof DemonHoleMap)
		{
			DemonHoleMap dhm = DemonHoleMap.class.cast(this.curMap);
			dhm.addDeadStreakOnRevive(this);
		}
		PetCluster cluster = this.curMap.getPetCluster(this.getID());
		if (cluster == null)
		{
			if(!this.propRole.isCurFightPetEmpty())
			{
				this.summonCurPets();
				this.syncClientCurPets();
			}
			return;
		}

		cluster.pets.values().stream().forEach(pet -> pet.onPetRevive(fullHp));
	}
	
	void autoRevive()
	{
		SBean.Location location = GameData.getInstance().getMapRevivePosition(this.getMapID(), this.isMainSpawnPos);
		this.setNewPosition(this.createPosition(new GVector3(location.position), 500));
		this.onRoleRevive(true);
	}
	
	//2v2 每一小场结束
	void superAreanRaceEnd()
	{
		if(this.fightSP > 0)
			this.normalSetFightSP(0);
		
		if(this.curSP > 0)
			this.setCurSP(0);
		
		if(this.weaponMotivate)
			this.setWeaponEnd(true);
		
		if(this.ghost)
			this.ghost = false;
			
		this.clearAllChildren(false, 0);
		this.clearAllBuff();
		
		this.curHP = this.getMaxHP();
		ms.getRPCManager().syncRoleHP(this.id, this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
		
		this.addState(Behavior.EBPREPAREFIGHT);
		this.specialState.put(Behavior.EBPREPAREFIGHT, ms.getMapManager().getMapLogicTime() + GameData.getInstance().getSuperArenaCFGS().normal.prepareTime * 1000L);	//TODO get prepare time
		
		PetCluster cluster = this.curMap.getPetCluster(this.id);
		if(cluster != null)
		{
			for(Pet p: cluster.pets.values())
				p.superAreanRaceEnd();
		}
	}
	
	public void onUpdatePet(SBean.FightPet fightPet)
	{
		Pet pet = this.curMap.getPet(this.id, fightPet.id);
		if (pet != null)
			pet.onUpdateInfo(fightPet);
		
		if(this.propRole.onUpdatePetCoPractice(fightPet.id, fightPet.coPracticeLvl, this.propRole.onUpdatePet(fightPet)))
		{
			this.updatePetFightProps();
			this.onPropUpdate();
		}
	}

	public void onUpdatePetSkill(int petID, List<Integer> skills)
	{
		Pet pet = this.curMap.getPet(this.id, petID);
		if(pet != null)
			pet.onUpdatePetSkill(skills);
	}

	public void onUpdatePerfectDegree(int perfectDegree)
	{
		this.propRole.onUpdatePerfectDegree(perfectDegree);
	}
	
	public void onUpdateHeirloomDisplay(boolean display)
	{
		if(this.isHeirloomDisplay == display)
			return;
		
		this.isHeirloomDisplay = display;
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updateheirloom(this.getID(), this.getHeirloomBrief()));
	}
	
	public void onUpdateMarriageSkillInfo(Map<Integer, Integer> skills, int partnerId)
	{
		if(this.marriageInfo != null)
			this.marriageInfo.disEffectSkill(this);
		
		if(partnerId == 0)
		{
			this.marriageInfo = null;
		}
		else
		{
			this.marriageInfo = new MarriageInfo(skills, partnerId);
		}
	}
	
	public void onUpdateMarriageSkillLevel(int skillID, int skillLvl)
	{
		if(this.marriageInfo != null)
			this.marriageInfo.updateSkill(this, skillID, skillLvl);
	}
	
	public void onUpdateVipLevel(int viplevel)
	{
		this.vipLevel = viplevel;
	}
	
	public void onUpdateCurWizardPet(int petId)
	{
		this.curWizardPet = petId;
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_updatewizardpet(this.getID(), this.curWizardPet));
	}
	
	public void onMarriageLevelChange(int newLevel)
	{
		this.propRole.onMarriageLevelChange(newLevel);
		this.onPropUpdate();
	}
	
	public void summonCurPets()
	{
		if ((this.getTeamID() != 0 && !(this.curMap instanceof SuperArenaMap))|| this.curRideHorse > 0 || this.isMulRolesMember())
			return;
		
		Collection<SBean.FightPet> curFightPets = this.propRole.getCurFightPets();
		if (curFightPets.isEmpty())
			return;

		if (this.isDead())
			return;

		long now = ms.getMapManager().getMapLogicTime();
		int index = 0;
		int count = curFightPets.size();
		float unit = (float) (Math.PI * 2.f) / count;
		for (SBean.FightPet fPet: curFightPets)
		{
			final int seq = this.getPetSeq(fPet.id);
			float angle = GameRandom.getRandFloat(unit * index, unit * index + unit * 0.8f); 
			SBean.Location location = new SBean.Location(this.curMap.createPetPos(this, MapManager.PET_CREATE_RADIUS, angle, seq).toVector3(), this.curRotation.toVector3F());
			Pet pet = ms.getMapManager().createPet(fPet, this, location, seq);
			
			if(this.curMap.isInPrepared(now))
			{
				pet.addState(Behavior.EBPREPAREFIGHT);
				pet.specialState.put(Behavior.EBPREPAREFIGHT, this.curMap.getMapPrepareTime());
			}
			
			this.curMap.addPet(pet);
			index++;
			ms.getRPCManager().syncPetHp(this.id, pet.id, this.getMapID(), this.getMapInstanceID(), pet.getCurHP(), pet.getMaxHP());
		}
	}

	public void robotRoleSummonPets(int prepareTime)
	{
		Collection<SBean.FightPet> curFightPets = this.propRole.getCurFightPets();
		if (curFightPets.isEmpty())
			return;

		long now = ms.getMapManager().getMapLogicTime();
		int index = 0;
		int count = curFightPets.size();
		float unit = (float) (Math.PI * 2.f) / count;
		for (SBean.FightPet fPet: curFightPets)
		{
			final int seq = this.getPetSeq(fPet.id);
			float angle = GameRandom.getRandFloat(unit * index, unit * index + unit * 0.8f); 
			SBean.Location location = new SBean.Location(this.curMap.createPetPos(this, MapManager.PET_CREATE_RADIUS, angle, seq).toVector3(), this.curRotation.toVector3F());
			Pet pet = new Pet(ms, this.serverControl).createNew(fPet, this, location, owner.getPetHost(fPet.id), seq);
			if (this.curMap.isInPrepared(now))
			{
				pet.addState(Behavior.EBPREPAREFIGHT);
				pet.specialState.put(Behavior.EBPREPAREFIGHT, this.curMap.getMapPrepareTime());
			}
			
			this.curMap.addPet(pet);
			index++;
		}
	}
	
	public void onUpdateCurPetSpirit(int petID, int index, SBean.PetSpirit spirit)
	{
		this.propRole.onUpdateCurPetSpirit(petID, index, spirit);
		this.onPropUpdate();
		
		Pet pet = this.curMap.getPet(this.id, petID);
		if(pet != null)
			pet.onUpdatePetSpirit(index, spirit);
	}
	
	public void changeCurFightPets(Map<Integer, SBean.FightPet> pets)
	{
		this.propRole.onChangeCurFightPet(pets);
		this.changeCurFightPetsImpl();
		this.updateWeaponUniqueSkills();
		this.onPropUpdate();
	}
	
	public void changeCurFightPetsImpl()
	{
		if ((this.getTeamID() != 0  && !(this.curMap instanceof SuperArenaMap))|| this.curRideHorse > 0)
			return;

		PetCluster cluster = this.curMap.getPetCluster(this.getID());
		if (cluster != null)
		{
			Iterator<Pet> it = cluster.pets.values().iterator();
			while (it.hasNext())
			{
				Pet pet = it.next();
				this.petState.put(pet.id, new PetState(pet.getCurHP(), pet.getCurSP()));
				Set<Integer> rids = pet.getRoleIDsNearBy(this);
				
				this.curMap.delPet(this.id, pet.id);
				it.remove();
				ms.getRPCManager().syncPetHp(this.id, pet.id, this.getMapID(), this.getMapInstanceID(), -1, -1);
				ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_unsummon_pet(pet.id));
				if(!rids.isEmpty())
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_dissolve_pet(pet.owner.getID(), pet.getID()));
			}
		}

		long now = ms.getMapManager().getMapLogicTime();
		for (SBean.FightPet fPet : this.propRole.getCurFightPets())
		{
			final int seq = this.getPetSeq(fPet.id);
			SBean.Location location = new SBean.Location(this.curMap.createPetPos(this, MapManager.PET_CREATE_RADIUS, 0, seq).toVector3(), this.curRotation.toVector3F());
			Pet pet = ms.getMapManager().createPet(fPet, this, location, seq);
			
			if(this.curMap.isInPrepared(now))
			{
				pet.addState(Behavior.EBPREPAREFIGHT);
				pet.specialState.put(Behavior.EBPREPAREFIGHT, this.curMap.getMapPrepareTime());
				
				if(this.active)
					ms.getRPCManager().sendStrPacket(pet.owner.id, new SBean.pet_addstate(pet.id, Behavior.EBPREPAREFIGHT, ms.getMapManager().getTimeTickDeep()));
			}
			
			this.curMap.addPet(pet);
			PetState s = this.petState.remove(pet.id);
			if(s != null)
			{
				pet.setCurHP(s.hp);
				pet.curSP = s.sp;
			}
			
			Set<Integer> rids = pet.getRoleIDsNearBy(this);
			if (this.active)
			{
				ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_summon_pet(pet.id, pet.curHP, pet.getCurSP(), location, pet.seq, pet.isDead() ? 1 : 0));
				pet.onSelfEnterNearBy(rids);
			}
		}
	}

	//gs 通知收起佣兵
	public void unSummonFightPets(boolean force)
	{
		if (this.isInWorld() || force)
		{
			this.dissolveCurPets();
			this.propRole.clearCurFightPets();
			this.updateWeaponUniqueSkills();
			this.onPropUpdate();
		}
	}

	public void dissolveCurPets()
	{
		PetCluster cluster = this.curMap.getPetCluster(this.getID());
		if (cluster == null)
			return;

		Iterator<Pet> it = cluster.pets.values().iterator();
		while (it.hasNext())
		{
			Pet pet = it.next();
			this.petState.put(pet.id, new PetState(pet.getCurHP(), pet.getCurSP()));
			Set<Integer> rids = pet.getRoleIDsNearBy(this);
			this.curMap.delPet(this.id, pet.id);
			it.remove();
			ms.getRPCManager().syncPetHp(this.id, pet.id, this.getMapID(), this.getMapInstanceID(), -1, -1);
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_unsummon_pet(pet.id));
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_dissolve_pet(pet.owner.getID(), pet.getID()));
		}
		
		this.curMap.delPetCluster(this.getID());
	}
	
	public void syncClientRoleEnterInfo()
	{
		SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
		if (this.checkState(Behavior.EBPREPAREFIGHT))
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_addstate(Behavior.EBPREPAREFIGHT, timeTick));

		if(this.checkState(Behavior.EBWIZARDPET))
			ms.getRPCManager().sendStrPacket(this.id, new SBean.role_addstate(Behavior.EBWIZARDPET, timeTick));
			
		int curSpawnArea = this.curMap.getCurOrNextSpawnArea();
		if (curSpawnArea != -1)
			ms.getRPCManager().sendStrPacket(this.id, new SBean.update_curspawnarea(curSpawnArea));

		this.curMap.syncEnterInfo(this);
		this.curMap.syncDamageRank(this);
		
		if(this.getSocialActionID() > 0)
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.nearby_role_socialaction(this.getID(), this.getSocialActionID()));
	}
	
	public void syncClientCurPets()
	{
		PetCluster cluster = this.curMap.getPetCluster(this.getID());
		if (cluster == null)
			return;
		
		if(cluster.pets.size() > 0)
		{
			SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
			for (Pet pet : cluster.pets.values())
			{
				Set<Integer> rids = pet.getRoleIDsNearBy(this);
				
				ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_summon_pet(pet.id, pet.curHP, pet.getCurSP(), new SBean.Location(pet.getLogicPosition(), pet.getCurRotation().toVector3F()), pet.seq, pet.isDead() ? 1 : 0));
				if (pet.checkState(Behavior.EBPREPAREFIGHT))
					ms.getRPCManager().sendStrPacket(pet.owner.id, new SBean.pet_addstate(pet.id, Behavior.EBPREPAREFIGHT, timeTick));
				
				pet.onSelfEnterNearBy(rids);
				pet.active = true;
			}
		}
	}

	public Map<Integer, SBean.CBuff> getAllBuffs()
	{
		long now = ms.getMapManager().getMapLogicTime();
		Map<Integer, SBean.CBuff> allBuffs = new HashMap<>();
		this.buffs.values().stream().filter(b -> b.spiritEffectID <= 0).forEach(b -> allBuffs.put(b.id, new SBean.CBuff((int) (b.endTime - now), new SBean.Buff(b.id, b.overLays, b.value, b.attackerType))));
		return allBuffs;
	}
	
	public Map<Integer, Integer> getAllSkillCD()
	{
		long now = ms.getMapManager().getMapLogicTime();
		return this.fightSkills.values().stream().filter(s -> s.coolDownTime > now).collect(Collectors.toMap(s -> s.id, s -> (int)(s.coolDownTime - now)));
	}
	
	public Map<Integer, SBean.FightPet> getBorrowFightPets()
	{
		Map<Integer, SBean.FightPet> borrowPets = new HashMap<>();
		this.propRole.getCurFightPets().stream().filter(p -> p.id < 0).forEach(p -> borrowPets.put(p.id, p));
		return borrowPets;
	}

	SBean.RoleQueryDetail getRoleQueryDetail()
	{
		return new SBean.RoleQueryDetail(getRoleDetail(), getMulRoleMembers());
	}
	
	SBean.RoleDetail getRoleDetail()
	{
		return new SBean.RoleDetail(this.getRoleOverview(), this.getRoleModel(), this.getRoleTitle(), this.getRoleFightState(), this.getRoleAppearance());
	}
	
	SBean.RoleProfile getRoleProFile()
	{
		return new SBean.RoleProfile(this.getRoleOverview(), this.getCurHP(), this.getMaxHP());
	}
	
	SBean.RoleOverview getRoleOverview()
	{
		return new SBean.RoleOverview(this.getID(), this.getRoleName(), this.gender, this.headIcon, this.getConfigID(), this.propRole.getTransformLevel(), this.getBWType(), this.level, this.propRole.getFightPower());
	}
	
	SBean.RoleModel getRoleModel()
	{
		return new SBean.RoleModel(this.face, 
								   this.hair, 
								   this.propRole.getWearEquipIds(), 
								   this.propRole.getWearParts(), 
								   this.propRole.getCurFashions(), 
								   new HashMap<>(this.showFashionTypes), 
								   getArmorBrief(this.propRole.getCurArmor()),
								   getHeirloomBrief());
	}
	
	SBean.ArmorBrief getArmorBrief(SBean.ArmorFightData curArmor)
	{
		return curArmor == null ? new SBean.ArmorBrief(0, 0) : new SBean.ArmorBrief(curArmor.id, curArmor.rank);
	}
	
	SBean.HeirloomBrief getHeirloomBrief()
	{
		return new SBean.HeirloomBrief(this.isHeirloomDisplay ? (byte) 1 : (byte)0, this.propRole.getHeirLoomPerfect());
	}
	
	SBean.RoleTitle getRoleTitle()
	{
		return new SBean.RoleTitle(	this.getNameGrade(), 
									this.pkStateTime > 0 ? 1 : 0, 
									this.sectBrief.kdClone(), 
									this.curPermanentTitle, 
									new ArrayList<SBean.DBTitleSlot>(this.curTimedTitles),
									this.carOwner,
									this.carRobber < 0 ? 1 : 0);
	}
	
	SBean.FightState getRoleFightState()
	{
		Map<Integer, Integer> curBuffs = this.buffs.isEmpty() ? GameData.emptyMap() : new HashMap<>();
		this.buffs.values().stream().filter(b -> b.hasShowID && b.spiritEffectID <= 0).forEach(b -> curBuffs.put(b.id, b.id));
		Set<Integer> states = GameData.emptySet();
		if(this.checkState(Behavior.EBWIZARDPET))
		{
			states = new HashSet<>();
			states.add(Behavior.EBWIZARDPET);
		}
		return new SBean.FightState(this.getCurHP(), this.getMaxHP(), curBuffs, this.getArmorWeakState(), states);
	}
	
	SBean.RoleAppearance getRoleAppearance()
	{
		SBean.DBWeapon dbWeapon = this.propRole.getWeapon(this.curWeapon);
		return new SBean.RoleAppearance(this.weaponMotivate ? this.curWeapon : 0, 
										dbWeapon == null ? GameData.WEAPON_FORM_DEFAULT : dbWeapon.form, 
										this.getCurRideHorseShowID(), 
										this.alterState.alterID, 
										this.getSocialActionID(), 
										new ArrayList<>(this.mulRoleInfo.members), 
										this.mulRoleInfo.type, 
										this.curWizardPet);
	}
	
	boolean isMulRolesEmpty()
	{
		for(int pos = 0; pos < this.mulRoleInfo.members.size(); pos++)
		{
			if(this.mulRoleInfo.members.get(pos) != 0)
				return false;
		}
		
		return true;
	}
	
	Map<Integer, SBean.RoleDetail> getMulRoleMembers()
	{
		if(this.mulRoleInfo.leader == 0 || this.mulRoleInfo.members.isEmpty() || this.isMulRolesEmpty())
			return GameData.emptyMap();
		
		Map<Integer, SBean.RoleDetail> members = new HashMap<>();
		for(int m: this.mulRoleInfo.members)
		{
			MapRole member = this.curMap.getRole(m);
			if(member != null)
				members.put(m, member.getRoleDetail());
		}
		return members;
	}
	
	SBean.RoleDetail getStayWithMember()
	{
		for(int m: this.mulRoleInfo.members)
		{
			MapRole member = this.curMap.getRole(m);
			if(member != null)
				return member.getRoleDetail();
		}
		
		return null;
	}
	
	public void onQueryForceWarResult()
	{
		if(this.curMap instanceof ForceWarMap)
		{
			ForceWarMap fwm = ForceWarMap.class.cast(this.curMap);
			fwm.onQueryForceWarResult(this.id);
		}
	}
	
	public void onQueryForceWarMemberPos()
	{
		if(this.curMap instanceof ForceWarMap)
		{
			ForceWarMap fwm = ForceWarMap.class.cast(this.curMap);
			fwm.onQueryMemberPos(this);
		}
	}
	
	public void onQueryRolesDetail(List<Integer> roles)
	{
		List<SBean.RoleQueryDetail> details = new ArrayList<>();
		for(int qrid : roles)
		{
			MapRole qRole = this.curMap.getRole(qrid);
			if(qRole == null)
				continue;
			
			details.add(qRole.getRoleQueryDetail());
		}
		
		if(!details.isEmpty())
		{
			final int BATCH_SIZE = 10;
			int start = 0;
			while(start < details.size())
			{
				int end = start + BATCH_SIZE;
				if(end > details.size())
					end = details.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.roles_detail(details.subList(start, end)));
				start = end;
			}
		}
	}
	
	public void onQueryPetsDetail(List<SBean.PetBase> pets)
	{
		List<SBean.PetDetail> details = new ArrayList<>();
		for(SBean.PetBase p: pets)
		{
			PetCluster cluster = this.curMap.getPetCluster(p.ownerID);
			if(cluster == null)
				continue;
			
			Pet pet = cluster.pets.get(p.pid);
			if(pet == null)
				continue;
			
			details.add(pet.getPetDetail());
		}
		
		if(!details.isEmpty())
		{
			final int BATCH_SIZE = 10;
			int start = 0;
			while(start < details.size())
			{
				int end = start + BATCH_SIZE;
				if(end > details.size())
					end = details.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.pets_detail(details.subList(start, end)));
				start = end;
			}
		}
	}
	
	public void onQueryTrapsDetail(List<Integer> traps)
	{
		List<SBean.TrapDetail> details = new ArrayList<>();
		for(int tid: traps)
		{
			Trap trap = this.curMap.getTrap(tid);
			if(trap == null)
				continue;
			
			details.add(trap.getTrapDetail());
		}
		if(!details.isEmpty())
		{
			final int BATCH_SIZE = 10;
			int start = 0;
			while(start < details.size())
			{
				int end = start + BATCH_SIZE;
				if(end > details.size())
					end = details.size();
				
				if(start != end)
					ms.getRPCManager().sendStrPacket(this.id, new SBean.traps_detail(details.subList(start, end)));
				start = end;
			}
		}
	}

	void createSkillEntity(SummonInfo summon, int skillLvl, int skillRealmLvl, long createTime)
	{
		SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(summon.skillID);
		if (sCfg == null)
			return;

		SBean.SkillLevelCFGS sDataCfg = GameData.getSkillLevelCFG(sCfg, skillLvl);
		if (sDataCfg == null)
			return;

		float rot = (float) (summon.angle / 180.f * Math.PI / 4.f);
		GVector3 position = this.getRandomTargetPos(this.curPosition, this.curRotation, radius, rot);
		SkillEntity skillEntity = this.curMap.createSkillEntity(this, position, summon.mid, sCfg.baseData.common, sCfg.baseData.fix, sDataCfg.fix, skillLvl, skillRealmLvl, summon.speed, createTime);
		this.skillEntitys.add(skillEntity);
	}
	
	private void checkFightState(long logicTime)
	{
		if(this.fightTime == 0)
			return;
		
		if(logicTime >= this.fightTime)
		{
			this.fightTime = 0;
			ms.getRPCManager().notifyGSUpdateRoleFightState(this.id, this.getMapID(), this.getMapInstanceID(), false);
		}
	}
	
	void updateFightTime()
	{
		if(ms.getMapManager().getMapLogicTime() > this.fightTime)		//非战斗状态到战斗状态
		{
			ms.getRPCManager().notifyGSUpdateRoleFightState(this.id, this.getMapID(), this.getMapInstanceID(), true);
		}
		
		this.fightTime = ms.getMapManager().getMapLogicTime() + GameData.getInstance().getCommonCFG().general.fightTime;
//		this.roleUnRideHorse(true);
	}

	public boolean useItemSkill(int itemID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		SBean.ItemCFGS itemCfg = GameData.getInstance().getItemCFG(itemID);
		if(itemCfg == null || itemCfg.type != GameData.GAME_ITEM_TYPE_SKILL)
			return false;
		
		int sid = itemCfg.arg1;
		int lvl = itemCfg.arg2;
		FightSkill fSkill = this.fightSkills.get(sid);
		if(fSkill == null)
		{
			fSkill = new FightSkill(sid, lvl, 0, 0, Skill.eSG_Skill, 1, GameData.SKILL_USE_TYPE_ALL).copySkillData();
			if (fSkill == null)
				return false;
			
			this.fightSkills.put(fSkill.id, fSkill);
			fSkill.itemSkill = true;
			this.normalSkills.add(fSkill.id);
		}
		
		if(!this.onUseSkill(sid, pos, rotation, targetID, targetType, ownerID, timeTick))
		{
			this.removeState(Behavior.EBATTACK);
			return false;
		}
		
		return true;
	}
	
	public boolean onUseMapSkill(int skillID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		JusticeMapCopy map = JusticeMapCopy.class.cast(this.curMap);
		int lvl = map.checkCanUseMapSkill(this.id, skillID);
		if (lvl <= 0)
			return false;
		FightSkill fSkill = this.fightSkills.get(skillID);
		if(fSkill == null)
		{
			fSkill = new FightSkill(skillID, lvl, 0, 0, Skill.eSG_Skill, 1, GameData.SKILL_USE_TYPE_ALL).copySkillData();
			if (fSkill == null)
				return false;
			
			this.fightSkills.put(fSkill.id, fSkill);
			fSkill.itemSkill = true;
			this.normalSkills.add(fSkill.id);
		}
		
		if(!this.onUseSkill(skillID, pos, rotation, targetID, targetType, ownerID, timeTick))
		{
			this.removeState(Behavior.EBATTACK);
			return false;
		}
		map.onUseMapSkill(this.id, skillID);
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_usemapskill_ok(skillID));
		return true;
	}
	
	public boolean onUseSkill(int skillID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
//		ms.getLogger().debug("@@@@@receive role " + this.id + " use skill " + skillID + " at client time " + (this.getTimeByTimeTick(timeTick)) + " at server time " + ms.getMapManager().getMapLogicTime() +
//				" client tick " + ms.getMapManager().timeTickToString(timeTick) + " server tick " + ms.getMapManager().timeTickToString());
		if (this.isInProtectTime() || this.isVirtual())
			return false;
		
		boolean diySkill = skillID == Skill.DIY_SKILL_ID;
		boolean dodgeSkill = this.dodgeSkills.contains(skillID);
		if (diySkill && (this.curDIYSkill == null || this.diySkillData == null || this.alterState.alterID > 0))
			return false;

		if (dodgeSkill && this.checkState(Behavior.EBDISDODGESKILL))
			return false;
		
		if(!this.inCurCanSkills(skillID))
			return false;
		
		GVector3 newPos = this.curMap.fixPos(new GVector3(pos), this);
		if (!this.serverControl && this.isPositionError(this.curPosition, newPos))
		{
			this.adjustPos();
			return false;
		}
		else
			this.curPosition = newPos;

		FightSkill fSkill = this.fightSkills.get(skillID);
		if (fSkill == null)
			return false;

		if (!this.canUseSkill(fSkill))
			return false;

		long now = this.getTimeByTimeTick(timeTick);
		BaseRole newTarget = this.getEntity(targetType, targetID, ownerID);
		if (newTarget != null && !newTarget.isDead())
			this.forceTarget = new MapEntity(targetType, targetID, ownerID);
		else
			this.forceTarget = null;

		BaseRole entity = this.getForceTarget();
		if (entity != null)
		{
			if(entity != this)
				this.setCurRotation(entity.curPosition.diffence2D(this.getCurPosition()).normalize());
		}
		else
			this.setCurRotation(new GVector3(rotation));
		
		
		this.roleUnRideHorse(true);
		this.onUseSkillHandle(skillID, pos, rotation, targetID, targetType, ownerID, timeTick);		//先广播在创建
		
		Skill skill = this.createNewSkill(diySkill ? this.diySkillData.baseCommonCfg : fSkill.baseDataCfg.common, diySkill ? this.diySkillData.baseFixCfg : fSkill.baseDataCfg.fix, 
					diySkill ? this.diySkillData.lvlFixCfg : fSkill.lvlFixCfg, fSkill.level, fSkill.realmLvl, fSkill.skillGroup, now);
		
		if(!this.weaponMotivate && this.alterState.alterID == 0 && diySkill)
			skill.damageCount = this.curDIYSkill.damageTimes;
		
		this.onUseSkillTrig(skill.baseFixCfg.type, fSkill.useType);
		long skillCD = (long) (skill.lvlFixCfg.cool * fSkill.coolDownPercent);
		if(dodgeSkill)
			skillCD = skillCD > dodgeSkillCDReduce ? (skillCD - dodgeSkillCDReduce) : 0;
		
		this.attack = skill;
		this.curUseSkillAddCache.add(skill);
		skill.target = entity;
		if (entity != null)
		{
			this.blurs.stream().filter(b -> b.enmityList.isEmpty()).forEach(b -> b.addEnemy(entity));
			this.summonMonsters.stream().filter(m -> m.enmityList.isEmpty()).forEach(m -> m.addEnemy(entity));
		}

		fSkill.coolDownTime = now + skillCD;
		if (skill.baseCommonCfg.canAttack == 0)
			this.addState(Behavior.EBDISATTACK);

		this.normalSetFightSP(this.fightSP + skill.baseCommonCfg.addFightSp);
		this.tryClearSocialAction();
		return true;
	}
	
	private boolean inCurCanSkills(int skillID)
	{
		if(this.weaponMotivate)
			return this.weaponSkills.contains(skillID);
		
		if(this.alterState != null && this.alterState.alterID > 0)
			return this.alterSkills.contains(skillID);
		
		return this.normalSkills.contains(skillID);
	}
	
	void onUseFollowSkill(int skillID, int seq, SBean.TimeTick timeTick)
	{
		Skill skill = this.getCurInUseSkill(skillID, -1);
		if (skill == null || seq <= skill.followSkillSeq || seq > skill.baseCommonCfg.followSkills.size())
			return;

		skill.followSkillSeq++;
		int followSkill = skill.baseCommonCfg.followSkills.get(seq - 1);
		SBean.SkillCFGS skillCfg = GameData.getInstance().getSkillCFG(followSkill);
		if (skillCfg == null)
			return;

		SBean.SkillLevelCFGS skillLvlCfg = GameData.getSkillLevelCFG(skillCfg, 1);
		if (skillLvlCfg == null)
			return;

		skill.valid = false;
		if (skill.rushInfo != null && skill.rushInfo.rushStart && skill.clientRushStartTimeTick != null && skill.attackPosition != null)
		{
			int rushTime = (int) (this.getTimeByTimeTick(timeTick) - this.getTimeByTimeTick(skill.clientRushStartTimeTick));
			if (rushTime > 0 && rushTime <= skill.rushInfo.distance / skill.rushInfo.speed * 1000)
			{
				float distance = skill.rushInfo.speed / 1000.f * rushTime;
				GVector3 endPos = this.createRushEndPos(skill.attackPosition, distance);
				this.setNewPosition(endPos);
			}
		}
		long now = this.getTimeByTimeTick(timeTick);
		Skill newSkill = this.createNewSkill(skillCfg.baseData.common, skillCfg.baseData.fix, skillLvlCfg.fix, 1, 0, Skill.eSG_TriSkill, now);
		this.attack = newSkill;
		newSkill.mainSkillID = skill.id;
		this.followSkills.put(skill.id, newSkill);
		this.normalSetFightSP(this.fightSP + newSkill.baseCommonCfg.addFightSp);
		
		skill.skillEndTime = newSkill.skillEndTime;
		skill.endSkillOnFinish = false;
		if(skill.rushInfo != null && newSkill.rushInfo != null)
			skill.rushInfo.rushEndTime = newSkill.rushInfo.rushEndTime;
			
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_usefollowskill(this.id, skillID, seq, timeTick));
	}

	void processFollowSkillDamage(long logicTime)
	{
		this.refreshFollowSkills();
		if(this.followSkills.isEmpty())
			return;
		
		SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
		Iterator<Map.Entry<Integer, Skill>> it = this.followSkills.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Skill> e = it.next();
			Skill skill = e.getValue();
			if(skill.rushInfo != null && skill.rushInfo.rushEndTime > logicTime)
				continue;
			
			if (!skill.autoProcessDamage(this, logicTime, timeTick))
			{
				int mainSkill = e.getKey();
				it.remove();
				Set<Integer> rids = this.getRoleIDsNearBy(this);
				if(skill.slashInfo != null && skill.damageTick < skill.damageCount)
					rids.add(this.id);
				if(!rids.isEmpty())
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_endskill(this.id, mainSkill, ms.getMapManager().getTimeTickDeep()));
				
//				ms.getLogger().debug("@@@@nearby_role_endskill " + rids + " mainSkill " + mainSkill + " at " + ms.getMapManager().getMapLogicTime());
			}
		}
	}

	void onTrigChildSkill(int mainSkill, int skillID)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_usechildskill(this.id, mainSkill, skillID));
		
//		ms.getLogger().debug("############role " + this.id + " nearby_role_usechildskill mainSkill　" + mainSkill + " skillID " + skillID);
	}
	
	void onSelfTrigSkill(Set<Integer> rids, int skillID)
	{
		if (!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_usetrigskill(this.id, skillID));
	}

	void trigSkillHandler(Skill skill)
	{
		this.normalSetFightSP(this.fightSP + skill.baseCommonCfg.addFightSp);
	}

	public boolean onUseSkillHandle(int skillID, SBean.Vector3 pos, SBean.Vector3F rotation, int targetID, int targetType, int ownerID, SBean.TimeTick timeTick)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
		{
			if (skillID != Skill.DIY_SKILL_ID)
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_useskill(this.getID(), skillID, pos, rotation, targetID, targetType, ownerID, timeTick));
			else if (this.curDIYSkill != null)
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_usediyskill(this.id, skillID, this.curDIYSkill.skillActionID, pos, rotation, targetID, targetType, ownerID, timeTick));
		}
		
//		ms.getLogger().debug("role " + this.id + " use skill " + skillID + " rids " + rids + " at " + ms.getMapManager().getMapLogicTime());
		return true;
	}

	void onEndSkillHandle(Skill skill)
	{
		if (skill.isAttackEffect)
		{
			if (skill.skillGroup == Skill.eSG_Skill)
			{
				this.lossDurabilityOnUseSkill();
				this.addFightSP(GameData.FIGHTSP_TRIG_USESKILL);
				if (this.curWeapon > 0 && !this.weaponMotivate && skill.lvlFixCfg.addSP > 0 && !this.isDead())
					this.addSPOnUseSkill(skill.lvlFixCfg.addSP);
			}
			
			if(!this.robot && this.weaponMotivate && this.weaponSkills.contains(skill.id))
				this.syncGSWeaponMaster();
			
			skill.isAttackEffect = false;
		}

		if (skill.baseCommonCfg.canAttack == 0)
			this.curStates.remove(Behavior.EBDISATTACK);

		if (skill.rushInfo != null && !skill.rushInfo.rushStart && skill.valid)
		{
			if (!this.checkState(Behavior.EBATTACK) && !this.checkState(Behavior.EBDISATTACK))
				this.adjustPos();
		}
		if(skill.valid)
		{
			if(!skill.baseCommonCfg.followSkills.isEmpty() && this.attack != null)
				skill.endSkillOnFinish = true;
			else
				this.notifyEndSkill(skill);
		}
//		ms.getLogger().trace("remove state disattack " + this.curUseSkills.size() + " at " + ms.getMapManager().getMapLogicTime());
	}
	
	void notifyEndSkill(Skill skill)
	{
		super.notifyEndSkill(skill);
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(skill.slashInfo != null && skill.damageTick < skill.damageCount)
			rids.add(this.id);
		
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_endskill(this.id, skill.id, ms.getMapManager().getTimeTickDeep()));
//		ms.getLogger().debug("####nearby_role_endskill " + rids + " skill " + skill.id + " at " + ms.getMapManager().getMapLogicTime());
	}
	
	void notifyFinishAttack(Skill skill)
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(skill.slashInfo != null && skill.damageTick < skill.damageCount)
			rids.add(this.id);
		
		if(skill.endSkillOnFinish && !rids.isEmpty())
		{
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_endskill(this.id, skill.id, ms.getMapManager().getTimeTickDeep()));
//			ms.getLogger().debug("$$$$nearby_role_endskill on finishattack " + rids + " skill " + skill.id + " at " + ms.getMapManager().getMapLogicTime());
		}
		
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_finishattack(this.id, skill.mainSkillID > 0 ? skill.mainSkillID : skill.id, ms.getMapManager().getTimeTickDeep()));
		
//		ms.getLogger().debug("$$$$nearby_role_finishattack " + rids + " skill " + (skill.mainSkillID > 0 ? skill.mainSkillID : skill.id) + " at " + ms.getMapManager().getMapLogicTime());
	}
	
	void notifyBreakSkill()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_breakskill(this.id));
	}
	
	Set<Integer> getCurEquipSkills()
	{
		Set<Integer> eSkills = new HashSet<>(this.curSkills);
		if(this.curUniqueSkill > 0)
			eSkills.add(this.curUniqueSkill);
		
		if(!this.dodgeSkills.isEmpty())
			eSkills.addAll(this.dodgeSkills);
		
		return eSkills;
	}
	
	//神兵激活
	public void onMotivateWeapon(SBean.TimeTick timeTick)
	{
		if(this.alterPet != null)
			return;
		
		if (this.curSP >= this.getMaxSP() && !this.weaponMotivate && this.alterState.alterID == 0 && this.curRideHorse == 0 && this.mulRoleInfo.leader == 0)
		{
			SBean.DBWeapon dbWeapon = this.propRole.getWeapon(this.curWeapon);
			if (dbWeapon != null)
			{
				SBean.WeaponCFGS cfg = GameData.getInstance().getWeaponCFGS(dbWeapon.id);
				this.setCurSP(0);
				this.weaponMotivate = true;
				if (cfg != null)
					this.motivateEndTime = this.getTimeByTimeTick(timeTick) + (long) cfg.conTime;

				if (!this.isInPrivateMap())
				{
					Set<Integer> rids = this.getRoleIDsNearBy(this);
					if(!rids.isEmpty())
						ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_motivateweapon(this.getID(), dbWeapon.id, dbWeapon.form, timeTick));
				}
				this.resetFightSkills(true);
//				this.roleUnRideHorse(true);
				this.breakSkill();
				this.updateWeaponTalentAi(true);
				this.updateWeaponMotivate();
				
				this.onMotivateWeaponCnt(this.curWeapon, GameData.WEAPON_MOTIVATE_START);
			}
			
			if (!this.robot)
				ms.getRPCManager().sendStrPacket(this.getID(), new SBean.motivate_state(this.weaponMotivate ? 1 : 0, timeTick));
			
			this.tryClearSocialAction();
		}
	}

	void adjustPos()
	{
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_adjust_pos(this.clientLastPos.toVector3()));
	}

	public boolean onShiftStartHandler(Skill skill, BaseRole target, GVector3 endpos, SBean.TimeTick timeTick)
	{
		if(super.onShiftStartHandler(skill, target, endpos, timeTick))
		{
			this.setClientLastPos(endpos);
			return true;
		}
		
		return false;
	}
	
	public void onRushStart(int skillID, SBean.Vector3 endPos, SBean.TimeTick timeTick)
	{
//		ms.getLogger().debug("xxxxxnearby_role_rushstart " + skillID + " server at " + ms.getMapManager().getMapLogicTime() + " client at " + (timeTick.tickLine * GameData.getInstance().getCommonCFG().engine.interval + timeTick.outTick));
		Skill skill = this.getCurInUseSkill(skillID, -1);
		if (skill == null || skill.rushInfo == null)
		{
			ms.getLogger().debug("role onRushStart skill : " + (skill == null) + " rush info " + (skill != null && skill.rushInfo == null));
			return;
		}

		int newGridX = this.curMap.calcGridCoordinateX((int) endPos.x);
		int newGridZ = this.curMap.calcGridCoordinateZ((int) endPos.z);
		MapGrid newGrid = this.curMap.getGrid(newGridX, newGridZ);
		if (newGrid == null)
			return;

		GVector3 endPosition = new GVector3(endPos);
		float distance = this.curPosition.distance(endPosition);
//		ms.getLogger().debug("role rush start skillID " + skillID + " curPos " + this.curPosition + " endPos " + endPosition + " distance " + distance + " maxdistance " + skill.rushInfo.distance + " skill.rushInfo.rushEndPos " + skill.rushInfo.rushEndPos);
		if (distance > Math.abs(skill.rushInfo.distance) + 150)
		{
			ms.getLogger().debug("role rush distance " + distance + " too far " + " max distance " + skill.rushInfo.distance);
			return;
		}

		this.setClientLastPos(endPosition);
		skill.rushInfo.rushEndPos = endPosition;
		skill.rushInfo.rushRotation = skill.rushInfo.rushEndPos.diffence2D(this.curPosition).normalize();
		skill.rushInfo.rushStart = true;
		skill.rushInfo.preUpdateTime = ms.getMapManager().getMapLogicTime();
		skill.attackPosition = new GVector3().reset(this.curPosition);
		skill.clientRushStartTimeTick = timeTick;
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_rushstart(this.getID(), skillID, endPos, timeTick));
	}

	private int tryMineral(int mineID, int mineInstance)
	{
		if(this.curMineralID != 0)
			return -1;
			
		if(this.curRideHorse != 0)
			return -2;
		
		Mineral mineral = this.curMap.getMineral(mineInstance);
		if(mineral == null)
			return -3;
		
		SBean.CommonMineralCFGS commonMineral = GameData.getInstance().getCommonCFG().mineral;
		float distance = this.curPosition.distance(mineral.getCurPosition());
		if(distance > (commonMineral.mineralDistance + 700) && !this.isInPrivateMap())
			return -(int)distance;
		
		MineralInfo info = mineral.onMineralStart(this.getID());
		if(info == null)
			return -4;
		
		if(!mineral.checkTimesEnough())
			return -5;
		
		this.curMineralID = mineInstance;
		this.mineralEndTime = info.endTime;
		if (!this.isInPrivateMap())
		{
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_mineralstart(this.getID(), mineInstance));
		}
		
		tryClearSocialAction();
		return 1;
	}
	
	public void onStartMineral(int mineID, int mineInstance)
	{
		int error = tryMineral(mineID, mineInstance);
		if(error <= 0)
		{
			ms.getRPCManager().syncEndMine(this.getID(), this.getMapID(), this.getMapInstanceID(), mineID, mineInstance, false);
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.nearby_role_mineralend(error, this.getID()));
			ms.getLogger().warn("role " + this.id + " start mineral[" + mineID + " , " + mineInstance + "] failed error " + error);
		}
	}

	public void onMineralQuit()
	{
		if (this.curMineralID > 0)
		{
			Mineral mineral = this.curMap.getMineral(this.curMineralID);
			if (mineral != null)
			{
				mineral.onMineralBreak(this.getID());
				this.notifyMineralBreak();
			}
			this.curMineralID = 0;
		}
	}

	public void onPickUpDrops(Set<Integer> drops)
	{
		Set<Integer> syncClientDrops = new TreeSet<>();
		Map<Integer, SBean.DummyGoods> dgs = new HashMap<>();
		for(Integer dropID : drops)
		{
			DropItem dropItem = this.curMap.pickDropItem(this.getID(), dropID);
			if (dropItem != null)
			{	
				syncClientDrops.add(dropID);
				SBean.EquipCFGS equip = GameData.getInstance().getEquipCFG(dropItem.item.id);
				if (equip != null && dropItem.dropMonsterId != 0)
				{
					if (equip.rank == GameData.EQUIP_RANK_ORANGE)
					{
						ms.getRPCManager().rolePickUpRareDrops(this.id, this.getMapID(), this.getMapInstanceID(), dropID, dropItem.item, dropItem.dropMonsterId);
						continue;
					}
				}
				dgs.put(dropID, dropItem.item);
			}
		}
		
		if(!dgs.isEmpty())
			ms.getRPCManager().rolePickUpDrops(this.id, this.getMapID(), this.getMapInstanceID(), dgs);
		
//		if(!this.isInWorld())
//		{
//			this.pickUpResult(syncClientDrops);
//		}
	}
	
	public void pickUpResult(Set<Integer> drops, boolean success)
	{
		if(success)
		{
			for(int dropID: drops)
			{
				this.curMap.delDropItem(this.getID(), dropID);
			}
			
			if(!drops.isEmpty())
				ms.getRPCManager().sendStrPacket(this.id, new SBean.role_pickup_add(drops));
		}
		else
		{
			for(int dropID: drops)
			{
				this.curMap.resetDropItem(this.getID(), dropID);
			}
		}
	}
	
	public void onPickUpMapBuff(int mapBuffID)
	{
		MapBuff mapBuff = this.curMap.getMapBuff(mapBuffID);
		if (mapBuff == null || this.isDead())
		{
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_mapbuff_add(0, mapBuffID));
			return;
		}

		boolean success = false;
		SBean.MapBuffCFGS mapBuffCfg = GameData.getInstance().getMapBuffCFGS(mapBuff.getConfigID());
		if (mapBuff.onPickUpMapBuff(mapBuffCfg, this))
		{
			success = true;
			if (!this.isInPrivateMap())
			{
				Buff buff = this.addBuffByID(mapBuffCfg.buffID, this, null);
				if (buff != null && mapBuffCfg.scopeType == GameData.MAPBUFF_SCOPE_MULTI)
					this.getRoleFriendEntityNearBy(this, mapBuffCfg.radius).stream().filter(r -> !r.isDead()).forEach(r -> r.addBuff(buff, this, null));
			}
		}
		
		ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_mapbuff_add(success ? 1 : 0, mapBuffID));
	}

	public void onPrivateMapKill(int spawnPointID, SBean.Vector3 position, boolean motivate)
	{
		if (spawnPointID <= 0)
			return;

		if (this.curMap.isTimeOut())
			return;

		SBean.SpawnPointProgress progress = this.curMap.getPrivateMapSpawn(spawnPointID);
		if (progress == null)
			progress = this.curMap.addPrivateMapSpawn(spawnPointID);

		if (progress != null)
		{
			SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(spawnPointID);
			int count = 0;
			count = GameData.getPointMonsterCount(pointCfg);
			if (count > 0 && progress.killedCount >= count)
				return;

			progress.killedCount++;
			if (pointCfg != null)
			{
				SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(pointCfg.monsterID);
				if (monsterCfg != null)
				{
					SBean.DropRatio dropRatio = GameData.getMonsterDoubleDropRatio(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentDoubleDropConfig());
					SBean.ExtraDropTbl extraDropTbl = GameData.getMonsterExtraDropTable(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentExtralDropConfig());
					
					//掉落
					int weaponDropRatio = motivate ? getWeaponMultipleDrop() : 0;
					int fixedDropRatio = dropRatio.fixedDrop + weaponDropRatio;
					int randomDropRatio = dropRatio.randomDrop + weaponDropRatio;
					List<DropItem> dropItems = this.curMap.getDropItemList(this.id, monsterCfg.fixedDropID, monsterCfg.randomDropIDs.get(this.configID - 1), monsterCfg.randomDropCnt, fixedDropRatio, randomDropRatio, extraDropTbl, position, monsterCfg.id, GameData.ENTITY_TYPE_MONSTER, this.canTakeDrop);
					if (dropItems.size() > 0)
					{
						List<SBean.DropInfo> dropInfo = new ArrayList<>();
						for (DropItem d : dropItems)
							dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));

						ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_drops(position, dropInfo));
					}

					//场景buff
					List<Integer> dropBuffs = GameData.getInstance().getBuffDrops(monsterCfg.buffDropID, monsterCfg.buffDropCnt);
					for (Integer bid : dropBuffs)
					{
						MapBuff mapBuff = new MapBuff(bid, this.ms).createNew(this.createPosition(new GVector3(position), 300));
						this.curMap.addMapBuff(mapBuff);

						if (this.isInPrivateMap())
							ms.getRPCManager().sendStrPacket(this.getID(), new SBean.drop_mapbuff(new SBean.BriefInfo(mapBuff.getID(), mapBuff.getConfigID(), mapBuff.getLogicPosition())));
					}

					if (monsterCfg.bossType == GameData.MONSTER_BOSSTYPE_FINALBOSS)
						this.curMap.addKilledBoss(pointCfg.monsterID);

					this.curMap.addKillMonster(this, null);
					ms.getRPCManager().addRoleKill(this.getID(), this.getMapID(), this.getMapInstanceID(), GameData.ENTITY_TYPE_MONSTER, monsterCfg.id, motivate ? this.getWeaponAddExp() : 1, this.getID()); //杀怪数
					this.curMap.delMonster(monsterCfg.id);
				}
			}
		}
	}
	
	public void onPrivateMapMonsterDamageDrop(int spawnPointID, SBean.Vector3 position, List<Integer> indexs)
	{
		if (spawnPointID <= 0)
			return;

		if (this.curMap.isTimeOut())
			return;

		SBean.SpawnPointProgress progress = this.curMap.getPrivateMapSpawn(spawnPointID);
		if (progress == null)
			progress = this.curMap.addPrivateMapSpawn(spawnPointID);

		if (progress != null)
		{
			SBean.SpawnPointCFGS pointCfg = GameData.getInstance().getSpawnPoint(spawnPointID);
			int count = 0;
			count = GameData.getPointMonsterCount(pointCfg);
			for (int index : indexs)
			{
				if (progress.earlyDrop.getOrDefault(index, 0) >= count)
					return;

				if (pointCfg != null)
				{
					SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(pointCfg.monsterID);
					if (monsterCfg != null && monsterCfg.percentDrop.containsKey(index))
					{

						SBean.DropRatio dropRatio = GameData.getMonsterDoubleDropRatio(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentDoubleDropConfig());
						SBean.ExtraDropTbl extraDropTbl = GameData.getMonsterExtraDropTable(this.getMapID(), monsterCfg.id, ms.getMapManager().getCurrentExtralDropConfig());
						List<DropItem> dropItems = this.curMap.getDropItemList(this.id, 0, monsterCfg.percentDrop.get(index).randomDropId, monsterCfg.percentDrop.get(index).randomDropTimes, dropRatio.fixedDrop, dropRatio.randomDrop, extraDropTbl, this.getLogicPosition(), monsterCfg.id, GameData.ENTITY_TYPE_MONSTER, this.canTakeDrop);
						if (dropItems.size() > 0)
						{
							List<SBean.DropInfo> dropInfo = new ArrayList<>();
							for (DropItem d : dropItems)
								dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));

							ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_drops(position, dropInfo));
						}
						progress.earlyDrop.merge(index, 1, (ov, nv) -> ov + nv);
					}
				}
			}
		}
	}

	public void onPrivateMapTrapTrig(int trapID, int state)
	{
		if (!this.curMap.isTimeOut())
		{
			if (GameData.getInstance().checkTrapInMapCopy(this.getMapID(), trapID))
			{
				SBean.TrapState trapState = this.curMap.getTriggedTrip(trapID);
				if (trapState == null)
				{
					trapState = new SBean.TrapState(trapID, 0, 0);
					this.curMap.addTriggedTrip(trapState);
				}
				trapState.state = state;
				if (state == MapManager.ESTRAP_OPEN && !this.curMap.isTrapAlreadyClosed(trapID))
				{
					this.curMap.addClosedTraps(trapID);
					//掉落
					SBean.TrapExpandedCFGS cfg = GameData.getInstance().getTrapCFG(trapID);
					if (cfg != null)
					{
						SBean.Vector3 position = cfg.position.kdClone();
						List<DropItem> dropItems = this.curMap.getDropItemList(this.id, cfg.fixedDropID, cfg.randomDropID, cfg.randomDropCnt, 1, 1, null, position, cfg.id, GameData.ENTITY_TYPE_TRAP, this.canTakeDrop);
						if (dropItems.size() > 0)
						{
							List<SBean.DropInfo> dropInfo = new ArrayList<>();
							for (DropItem d : dropItems)
								dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));

							ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_drops(position, dropInfo));
						}

						//出怪
						if(cfg.monsterOdds > 0 && GameData.checkRandom(cfg.monsterOdds))
						{
							SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(cfg.monsterID);
							if (monsterCfg != null)
								trapState.monsterCount = cfg.monsterCount;
						}

						//场景buff
						List<Integer> dropBuffs = GameData.getInstance().getBuffDrops(cfg.buffDropID, cfg.buffDropCnt);
						for (Integer bid : dropBuffs)
						{
							MapBuff mapBuff = new MapBuff(bid, this.ms).createNew(this.createPosition(new GVector3(cfg.position), cfg.dropRadius));
							this.curMap.addMapBuff(mapBuff);

							if (this.isInPrivateMap())
								ms.getRPCManager().sendStrPacket(this.getID(), new SBean.drop_mapbuff(new SBean.BriefInfo(mapBuff.getID(), mapBuff.getConfigID(), mapBuff.getLogicPosition())));
						}
					}
				}
			}
		}
	}

	public void onPrivateMapUpdateHp(int hp)
	{
		int old = this.getCurHP();
		this.curHP = hp;
		ms.getRPCManager().syncRoleHP(this.getID(), this.getMapID(), this.getMapInstanceID(), this.curHP, this.getMaxHP());
//		this.setCurHP(hp);
		if (hp == 0 && old > 0)
			this.curMap.onMapRoleDead(this, null);
	}

//	public void onProcessDamage(int skillID, int damageTick, GVector3 rotation, SBean.TimeTick timeTick)
//	{
//		int curEventID = damageTick;
//		Skill skill = this.getCurInUseSkill(skillID, damageTick);
//		if (skill == null)
//		{
//			ms.getLogger().debug("skill " + skillID + " damageTick " + damageTick + " null");
//			return;
//		}
//
//		if (skill.damageTick >= skill.damageCount)
//			return;
//
//		if (!(skill.flyInfo == null || skill.touchTarget))
//			return;
//
//		if (skill.guideInfo != null)
//			curEventID = skill.guideInfo.damageEventID;
//
//		skill.onProcessDamageHandler(this, curEventID, rotation, timeTick, true);
//	}
	
	void setPetAlter(SBean.FightPet fightPet, SBean.PetHost petHost)
	{
		this.setSpiritEffect(false, -1);
		SBean.HorseInfo info = this.propRole.getHorse(this.propRole.getCurHorseId());
		if (info != null)
			this.setHorseSkillEffect(info, false, -1);
		
		this.alterPet = new Pet(ms, owner.serverControl).createNew(fightPet, owner, new SBean.Location(new SBean.Vector3(), new SBean.Vector3F()), petHost, 0);
		this.resetPetAlterSkill();
		this.onPropUpdate();
		this.setCurHP(this.getMaxHP());
	}
	
	
	int getPetAlterID()
	{
		return this.alterPet != null ? this.alterPet.getConfigID() : 0;
	}
	
	public void setPetLack(boolean petLack)
	{
		this.petLack = petLack;
	}
	
	public void inActive()
	{
		if(this.getMapType() == GameData.MAP_TYPE_MAPCOPY_SUPERARENA || this.getMapType() == GameData.MAP_TYPE_MAPCOPY_FORCEWAR)
			return;
		
		PetCluster cluster = this.curMap.getPetCluster(this.getID());
		if (cluster != null)
		{
			for (Pet pet : cluster.pets.values())
			{
				pet.active = false;
				Set<Integer> prids = pet.getRoleIDsNearBy(this);
				if(!prids.isEmpty())
				{
					List<SBean.PetBase> pets = new ArrayList<>();
					pets.add(new SBean.PetBase(pet.owner.getID(), pet.getID()));
					ms.getRPCManager().broadcastStrPacket(prids, new SBean.nearby_leave_pets(pets, 1));
				}
			}
		}
		
		Set<Integer> rids = super.getRoleIDsNearBy(this);
		this.filterAndDelNearByRoleIDs(rids);
		this.onSelfLeaveNearBy(rids, 1);
		this.nearbyRoles.clear();
		this.beSeenRoles.clear();
		this.active = false;
		this.tryClearSocialAction();
	}

	public void resetLocation(SBean.Location location)
	{
		if(this.isInPrivateMap())
		{
			this.curPosition.reset(new GVector3(location.position));
			this.setClientLastPos(this.curMap.fixPos(this.curPosition, this));
		}
		else
		{
			this.clearBlurs(0);
			this.clearSkillEntity();
			int newGridX = this.curMap.calcGridCoordinateX((int) location.position.x);
			int newGridZ = this.curMap.calcGridCoordinateZ((int) location.position.z);
			MapGrid newGrid = this.curMap.getGrid(newGridX, newGridZ);
			if (newGrid != null)
			{
				this.dissolveCurPets();
				
				Set<Integer> rids = super.getRoleIDsNearBy(this);
				if(!rids.isEmpty())
				{
					this.filterAndDelNearByRoleIDs(rids);
					this.onSelfLeaveNearBy(rids, 1);
				}
				this.nearbyRoles.clear();
				this.beSeenRoles.clear();
				
				this.curPosition.reset(new GVector3(location.position));
				this.setCurRotation(new GVector3(location.rotation));
				this.changeMapGrid(newGrid);
				this.setClientLastPos(this.curMap.fixPos(this.curPosition, this));
				
				this.summonCurPets();
			}
//			this.clearAllSkillsCD();
			this.breakSkill();
			if(this.isMoving())
				this.onStopMoveImpl(this.curPosition, ms.getMapManager().getTimeTickDeep(), false);
			this.tryClearSocialAction();
		}
		
		if(this.getMapType() == GameData.MAP_TYPE_MAPCOPY_SUPERARENA || this.getMapType() == GameData.MAP_TYPE_MAPCOPY_FORCEWAR)
			return;
		
		this.active = false;
	}

	void addKillRole(MapRole deader)
	{
		int count = this.killRoles.getOrDefault(deader.id, 0);
		count++;
		this.killRoles.put(deader.id, count);
	}
	
	int getKillRoleCount(int roleID)
	{
		return this.killRoles.getOrDefault(roleID, 0);
	}
	
	void setDemonHoleKillStreakBuff(int killStreak, int deadStreak)
	{
		int oldBuffID = killStreak == 0 ? 0 : GameData.getInstance().getDemonHoleKillStreakBuff(killStreak);
		killStreak++;
		int newBuffID = GameData.getInstance().getDemonHoleKillStreakBuff(killStreak);
		if(oldBuffID != newBuffID)
		{
			if(oldBuffID > 0)
				this.removeBuffByID(oldBuffID);
			
			if(newBuffID > 0)
				this.addBuffByID(newBuffID, this, null);
		}

		int deadBuffID = deadStreak == 0 ? 0 : GameData.getInstance().getDemonHoleDeadStreakBuff(deadStreak);
		if(deadBuffID > 0)
			this.removeBuffByID(deadBuffID);
	}
	
	void addDemonHoleDeadStreakBuffOnRevive(int deadStreak)
	{
		int buffID = deadStreak == 0 ? 0 : GameData.getInstance().getDemonHoleDeadStreakBuff(deadStreak);
		if(buffID > 0)
			this.addBuffByID(buffID, this, null);
		
		this.addBuffByID(GameData.getInstance().getDemonHoleBaseCFGS().reviveBuffID, this, null);
	}
	
	void demonHoleRoleDeadDrop(SBean.Vector3 position)
	{
		List<DropItem> dropItems = this.curMap.getDropItemList(this.getID(), GameData.getInstance().getDemonHoleBaseCFGS().roleDeadDrop, position, 0, GameData.ENTITY_TYPE_PLAYER);
		if(!dropItems.isEmpty())
		{
			List<SBean.DropInfo> dropInfo = new ArrayList<>();
			for (DropItem d : dropItems)
				dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));
			
			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_drops(this.getLogicPosition(), dropInfo));
		}
	}
	
	void setIsMainSpawnPos(boolean isMainSpawnPos)
	{
		this.isMainSpawnPos = isMainSpawnPos;
	}
	
	boolean isMainSpawnPos()
	{
		return this.isMainSpawnPos;
	}
	
	void setForceWarType(int forceType)
	{
		this.forceType = forceType;
	}
	
	void setGhost()
	{
		if(this.ghost)
			return;
		
		this.ghost = true;
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_ghost());
	}
	
	public void setPKValue(int value)
	{
		if (this.curMap.isFightMap())
			return;

		int tempGrade = this.getNameGrade();
		this.pkInfo.value = value;

		if (this.pkInfo.value < 0)
			this.pkInfo.value = 0;

		if (this.pkInfo.value > GameData.RED_NAME_MAX)
			this.pkInfo.value = GameData.RED_NAME_MAX;

		ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_update_pkvalue(this.pkInfo.value));

		int grade = this.getNameGrade();
		if (tempGrade != grade)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_update_pkinfo(this.getID(), grade, this.getPKState()));
		}
	}

	public void updateAttackMode(int attackMode)
	{
		if (attackMode == this.pkInfo.mode)
			return;

		this.pkInfo.mode = attackMode;
//		if (attackMode == GameData.ATTACK_MODE_PEACE)
//		{
//			SBean.CommonCFGS commonCfg = GameData.getInstance().getCommonCFG();
//			SBean.CommonPKCFGS pkCfg = commonCfg.pk;
//			this.pkStateTime = pkCfg.pkKeepTime;
//		}

		this.blurs.forEach(Blur::onOwnerUpdateAttackMode);
	}

	public String getRoleName()
	{
		return this.roleName;
	}

	public int getNameColor()
	{
		if (this.pkInfo.value > GameData.RED_NAME_MIN)
			return GameData.NAME_COLOR_RED;

		if (this.orangeNameTime > 0)
			return GameData.NAME_COLOR_ORANGE;

		return GameData.NAME_COLOR_WHITE;
	}

	public int getNameGrade()
	{
		if(this.pkInfo.value <= GameData.RED_NAME_MIN)
		{
			if (this.orangeNameTime > 0)
				return -1;
			
			return -2;
		}
		
		return GameData.getInstance().getReaNameGrade(this.pkInfo.value);		
//		int grade = GameData.getInstance().getReaNameGrade(this.pkInfo.value);
//		if (this.pkInfo.value <= GameData.RED_NAME_MIN)
//		{
//			grade = -2;
//			if (this.orangeNameTime > 0)
//				grade = -1;
//		}
//		return grade;
	}
	
	byte getBWType()
	{
		return this.propRole.getBWType();
	}
	
	public int getPKMode()
	{
		return this.pkInfo.mode;
	}
	
	public int getPKState()
	{
		return this.pkStateTime > 0 ? 1 : 0;
	}

	public int getFightSkillLevel(int sid)
	{
		FightSkill skill = this.fightSkills.get(sid);
		if (skill == null)
			return 0;
		return skill.level;
	}

	public void setTeam(SBean.Team team)
	{
		if (!this.isInPrivateMap())
		{
			this.team = team;
			if(this.team.id > 0 && this.active)
			{
				Set<Integer> self = new HashSet<>();
				self.add(this.id);
				
				Set<Integer> members = new HashSet<>();
				for(int rid: this.team.members)
				{
					if(rid == this.id)
						continue;
					
					MapRole role = this.curMap.getRole(rid);
					if(role == null || !role.active)
						continue;
					
					if(PublicMap.isMapGridNearBy(this.curMapGrid, role.curMapGrid))
					{
						if(!this.isNearByRoles(role.id))
						{
							role.onSelfEnterNearBy(self);
							this.addNearByRole(role.id);
							role.beSeenRoles.add(this.id);
						}
						
						if(!role.isNearByRoles(this.id))
						{
							members.add(rid);
							role.addNearByRole(this.id);
							this.beSeenRoles.add(role.id);
						}
					}
					
					this.onSelfEnterNearBy(members);
				}
			}
		}
	}

	int getCurRideHorseID()
	{
		return this.curRideHorse;
	}
	
	int getCurRideHorseShowID()
	{
		SBean.HorseInfo horseInfo = this.propRole.getHorse(this.curRideHorse);
		if (horseInfo == null)
			return 0;

		return horseInfo.curShowID;
	}

	int getCurSP()
	{
		return this.curSP;
	}

	int getFightSP()
	{
		return this.fightSP;
	}

	int getFightPower()
	{
		return this.totalPower;
	}
	
	int getSkillLvl(int skillID)
	{
		SBean.DBSkill dbSkill = this.propRole.getSkill(skillID);
		if (dbSkill == null)
			return 1;
		
		return getSkillLvl(dbSkill);
	}
	
	private int getSkillLvl(SBean.DBSkill dbSkill)
	{		
		Integer sealSkillUp = this.propRole.getSealSkillLevel(dbSkill.id);
		if(sealSkillUp == null)
			return dbSkill.level;
		
		SBean.SkillCFGS sCfg = GameData.getInstance().getSkillCFG(dbSkill.id);
		if(sCfg == null)
			return dbSkill.level;
		
		return dbSkill.level + sealSkillUp > sCfg.lvlDatas.size() ? sCfg.lvlDatas.size() : dbSkill.level + sealSkillUp;
	}
	
	int getSkillRealmLevel(int skillID)
	{
		SBean.DBSkill dbSkill = this.propRole.getSkill(skillID);
		if (dbSkill != null)
			return dbSkill.bourn;

		return 0;
	}

	int getSpiritTotalLays()
	{
		return this.propRole.getSpiritTotalLays();
	}

	int getWeaponTotalLvls()
	{
		return this.propRole.getWeaponTotalLvls();
	}

	SBean.DBDIYSkillData getDIYSkill()
	{
		return this.curDIYSkill;
	}

	SkillData getDiySkillData()
	{
		return this.diySkillData;
	}

	public int getEventLowHpDmgType()
	{
		return GameData.DMGTO_THP_TYPE_HERO;
	}
	
	int getSectID()
	{
		return this.sectBrief == null ? 0 : this.sectBrief.sectID;
	}
	
	int getTeamID()
	{
		return this.team == null ? 0 : this.team.id;
	}
	
	List<Integer> getTeamMember()
	{
		return this.team == null ? GameData.emptyList() : this.team.members;
	}

	boolean isTeamMember(MapRole role)
	{
		return this.getTeamID() == role.getTeamID() && this.getTeamID() != 0;
	}
	
	boolean isSectMember(MapRole role)
	{
		return this.sectBrief.sectID == role.sectBrief.sectID && this.sectBrief.sectID != 0;
	}
	
	boolean isSameBWType(byte bwType)
	{
		return bwType == 0 ? false : bwType == this.getBWType();
	}
	
	void updateCarBehavior(byte carOwner, byte carRobber)
	{
		boolean change = this.carOwner != carOwner;
		if(!change)
			change = (carRobber < 0 ? 1 : 0) != ( this.carRobber < 0 ? 1 : 0);
		
		this.carOwner = carOwner;
		this.carRobber = carRobber;
		
		if(change)
		{
			Set<Integer> rids = this.getRoleIDsNearBy(this);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_carbehavior(this.id, this.carOwner, this.carRobber < 0 ? 1 : 0));
			
//			ms.getLogger().debug("@@@@@@@@@@ role " + this.roleName + " update car behavior " + this.carOwner + " this.carRobber " + (this.carRobber < 0 ? 1 : 0) + " rids " + rids);
		}
	}
	
	void exposeCarRobber()
	{
		if(this.carRobber <= 0)
			return;

		this.carRobber = (byte)-GameData.SECT_IS_ROB;
		ms.getRPCManager().updateRoleCarRobber(this.id);
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_carbehavior(this.id, this.carOwner, 1));
	}
	
	//PVP职业伤害减免
	float getClassReduce(BaseRole attacker)
	{
		BaseRole realAttacker = attacker;
		if (attacker.getEntityType() == GameData.ENTITY_TYPE_SKILL)
			realAttacker = attacker.owner;

		if (realAttacker.getEntityType() != GameData.ENTITY_TYPE_PLAYER)
			return 1.0f;

		if (realAttacker.configID <= 0 || realAttacker.configID > this.classReduce.size())
			return 1.0f;

		int dmgByPropID = GameData.getInstance().getExpCoinBaseCFGS().dmgTransfer.dmgBys.getOrDefault(attacker.configID, 0);
		int dmgToPropID = GameData.getInstance().getExpCoinBaseCFGS().dmgTransfer.dmgTos.getOrDefault(this.configID, 0);
		
		return (float) ((this.classReduce.get(realAttacker.configID - 1)) * (1.f + attacker.getFightPropF(dmgToPropID) - this.getFightPropF(dmgByPropID) + getBWEnemyReduce(attacker)));
//		return (float) (this.classReduce.get(realAttacker.configID - 1) -  this.getFightPropF(dmgByPropID) + attacker.getFightPropF(dmgToPropID) + getBWEnemyReduce(attacker));
	}
	
	float getBWEnemyReduce(BaseRole attacker)
	{
		if(this.getBWType() == GameData.BWTYPE_NONE || attacker.getBWType() == GameData.BWTYPE_NONE || this.getBWType() == attacker.getBWType())
			return 0;
		
		return (float) (attacker.getFightPropF(EPROPID_DMGTO_ENEMY) - this.getFightPropF(EPROPID_DMGBY_ENEMY));
	}
	
	class PetState
	{
		int hp;
		int sp;
		
		PetState(int hp, int sp)
		{
			this.hp = hp;
			this.sp = sp;
		}
	}
	
	//对目标类型的内甲伤害转移
	float getArmorTransRate()
	{
		return GameData.getInstance().getCommonCFG().armor.transRate;
	}
	
	//对目标类型的内甲伤害加深
	float getArmorDmgDeep()
	{
		return GameData.getInstance().getCommonCFG().armor.dmgDeep;
	}
	
	int getCurArmor()
	{
		return this.propRole.getCurArmor() == null ? 0 : this.propRole.getCurArmor().id; 
	}
	
	void notifyNearByArmorWeak()
	{
		Set<Integer> rids = this.getRoleIDsNearBy(this);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_armorweak(this.id, this.getArmorWeakState()));
	}
	
	boolean setArmorVal(int value)
	{
		if(super.setArmorVal(value))
		{
			ms.getRPCManager().syncRoleArmorVal(this.id, this.getMapID(), this.getMapInstanceID(), this.getArmorValue(), this.getArmorValMax());
			return true;
		}
		
		return false;
	}
	
	void notifySelfArmorValUpdate()
	{
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_armorval_update(this.getArmorValue()));
	}
	
	void notifySelArmorFreezeUpdate(int state)
	{
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_armorfreeze_update(state));
	}
	
	void onSelfArmorWeekUpdate(int state)
	{
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_armorweak_update(state));
		this.notifyNearByArmorWeak();
	}
	//---------------------------------------------------------------------------------------------------------------------
	void addSceneTrigMonster(int monsterID)
	{
		this.sceneTrigMonsters.add(monsterID);
	}
	
	void clearSceneTrigMonsters()
	{
		for(int monsterID: this.sceneTrigMonsters)
		{
			Monster monster = this.curMap.getMonster(monsterID);
			if(monster != null)
				monster.onDeadHandle(0, 0);
		}
	}
	
	void clearSceneTrigMonsters(int monsterID)
	{
		for(int mid: this.sceneTrigMonsters)
		{
			Monster monster = this.curMap.getMonster(mid);
			if(monster != null && monster.getConfigID() == monsterID)
				monster.onDeadHandle(0, 0);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------
	void syncTaskDrop(Map<Integer, Integer> taskDrop)
	{
		this.taskDrop = taskDrop;
	}
	
	void roleTaskDrop(int monsterID, SBean.Vector3 position)
	{
		Integer randDropTbID = this.taskDrop.get(monsterID);
		if(randDropTbID == null)
			return;
		
		List<DropItem> dropItems = this.curMap.getDropItemList(this.id, 0, randDropTbID, 1, 1, 1, null, position, monsterID, GameData.ENTITY_TYPE_MONSTER, this.canTakeDrop);
		if (dropItems.size() > 0)
		{
			List<SBean.DropInfo> dropInfo = new ArrayList<>();
			for (DropItem d : dropItems)
				dropInfo.add(new SBean.DropInfo(d.dropID, d.item.id, d.item.count));

			ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_sync_drops(position, dropInfo));
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------
	void syncGSWeaponMaster()
	{
		if(this.curWeapon > 0)
		{
			SBean.DBWeapon weapon = this.propRole.getWeapon(this.curWeapon);
			if(weapon != null && weapon.uniqueSkill.open == 0)
				ms.getRPCManager().notifyGSSyncRoleWeaponMaster(this.id, this.getMapID(), this.getMapInstanceID(), this.curWeapon);
		}
	}
	
	void onPrivateMapWeaponMaster()
	{
		if(!this.isInPrivateMap() || this.privateMapMasterCount >= GameData.getInstance().getCommonCFG().skill.privateMapMasterMaxCount)
			return;
		
		this.syncGSWeaponMaster();
		this.privateMapMasterCount++;
	}
	
	void onPrivateMapSaveDamageRank(Map<Integer, SBean.AttackDamageDetail> damageRank)
	{
		if (this.curMap instanceof PrivateMap)
		{
			PrivateMap pm = PrivateMap.class.cast(this.curMap);
			pm.damageRank = damageRank;
		}
	}
	//---------------------------------------------------------------------------------------------------------------------
	void queryEntityNearBy(int entityID, int entityType)
	{
		BaseRole e = this.getEntity(entityType, entityID, 0);
		if(e == null || !PublicMap.isMapGridNearBy(this.curMapGrid, e.curMapGrid))
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.entity_nearby(entityID, entityType, 0));
		}
		else
		{
			ms.getRPCManager().sendStrPacket(this.id, new SBean.entity_nearby(entityID, entityType, 1));
		}
	}
	//---------------------------------------------------------------------------------------------------------------------
	void onCreateWeddingCar(WeddingCar car)
	{
		this.weddingCar = car.getID();
		this.addState(Behavior.EBPREPAREFIGHT);
		ms.getRPCManager().sendStrPacket(this.id, new SBean.role_addstate(Behavior.EBPREPAREFIGHT, ms.getMapManager().getTimeTickDeep()));
		ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_weddingcar(car.getEnterWeddingCar()));
		
		if(this.active)
		{
			Set<Integer> rids = super.getRoleIDsNearBy(this);
			this.enterVirtual(rids);
		}
	}
	
	void onWeddingCarDead(WeddingCar car)
	{
		this.weddingCar = 0;
		this.removeState(Behavior.EBPREPAREFIGHT);
		ms.getRPCManager().sendStrPacket(this.getID(), new SBean.role_weddingcar_destory());
		this.setNewPosition(car.getCurPosition());
		this.setClientLastPos(car.getCurPosition());
		this.adjustPos();
		
		if(this.active)
		{
			Set<Integer> rids = new HashSet<>();
			int gridX = this.getCurMapGrid().getGridX();
			int gridZ = this.getCurMapGrid().getGridZ();
			EnterInfo enterInfo = this.curMap.getEntitiesNearBy(gridX, gridZ, this);
			if(!enterInfo.roles.isEmpty())
			{
				for(int rid: enterInfo.roles.keySet())
				{
					if(rid != this.id)
					{
						MapRole r = this.curMap.getRole(rid);
						if(r != null && !r.robot)
							rids.add(rid);
					}
				}
			}
			
			this.leaveVirtual(rids);
		}
	}
	
	boolean canAddMarriageBuff()
	{
		return !this.robot && this.active && !this.isDead() && !this.checkState(Behavior.EBREVIVE);
	}
	
	public void sendTowerDefenceAlarm(int type)
	{
		if(this.curMap instanceof TowerDefenceMap)
		{
			TowerDefenceMap map = TowerDefenceMap.class.cast(this.curMap);
			map.sendAlarm(this, type);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------
	class MarriageInfo
	{
		private final int UPDATE_INTERVAL_TIME = 5;
		
		private Map<Integer, Integer> skills;		//<skillID, skillLvl>
		private int partnerID;
		
		private Map<Integer, Integer> curBuffs;		//<skillID, buff instanceID>
		private int second;
		private boolean isParterNearBy;
		
		MarriageInfo(Map<Integer, Integer> skills, int partnerID)
		{
			this.skills = skills;
			this.partnerID = partnerID;
			
			this.curBuffs = new HashMap<>();
			this.isParterNearBy = false;
		}
		
		public void onTimer(MapRole role, int timeTick)
		{
			if(timeTick > second + UPDATE_INTERVAL_TIME)
			{
				tryUpdateSkill(role);
				second = timeTick;
			}
		}
		
		private void tryUpdateSkill(MapRole role)
		{
			if(isParterNearBy)
			{
				tryDisEffectSkill(role);
			}
			else
			{
				tryEffectSkill(role);
			}
		}
		
		//机器人、inactive、死亡状态BUFF不生效
		private boolean isNearBy(MapRole role)
		{
			if(!role.canAddMarriageBuff())
				return false;
			MapRole p = role.curMap.getRole(partnerID);
			return p != null && p.canAddMarriageBuff() && role.getCurPosition().distance(p.getCurPosition()) < GameData.getInstance().getMarriageBaseCFGS().skillHillDistance * 100;
		}
		
		private void tryEffectSkill(MapRole role)
		{
			if(!isNearBy(role))
				return;
			
			for(Map.Entry<Integer, Integer> e: skills.entrySet())
				effectSkill(role, e.getKey(), e.getValue());
			
			isParterNearBy = true;
		}
		
		private void effectSkill(MapRole role, int skillID, int skillLvl)
		{
			SBean.MarriageSkillGroupCFGS groupCfg = GameData.getInstance().getMarriageSkillGroupCFGS(skillID);
			if(groupCfg == null)
				return;
			
			SBean.MarriageSkillCFGS sCfg = GameData.getMarriageSkillCFGS(groupCfg, skillLvl);
			if(sCfg == null || sCfg.buffId <= 0)
				return;
			
			SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(sCfg.buffId);
			if(buffCfg == null)
				return;
			
			Buff buff = role.createNewBuff(buffCfg, 0, 0, role, null);
			if(buff != null)
			{
				role.addBuff(buff, role, null);
				curBuffs.put(skillID, buff.id);
			}
		}
		
		public void tryDisEffectSkill(MapRole role)
		{
			if(isNearBy(role))
				return;
			
			disEffectSkill(role);
			isParterNearBy = false;
		}
		
		private void disEffectSkill(MapRole role)
		{
			for(int bid: curBuffs.values())
			{
				Buff buff = role.buffs.get(bid);
				if(buff != null)
					role.removeBuff(buff);
			}
			curBuffs.clear();
		}
		
		public void updateSkill(MapRole role, int skillID, int skillLvl)
		{
			if(isNearBy(role))
			{
				Integer curBuffID = curBuffs.remove(skillID);
				if(curBuffID != null)
				{
					Buff buff = role.buffs.get(curBuffID);
					if(buff != null)
						role.removeBuff(buff);
				}
				effectSkill(role, skillID, skillLvl);
			}
			
			skills.put(skillID, skillLvl);
		}
	}

	//---------------------------------------------------------------------------------------------------------------------
	class SocialAction
	{
		int id;
		boolean checkFight;
		List<Integer> buffs = new ArrayList<>();
		int relateState;
		
		SocialAction()
		{
			
		}
		
		int getID()
		{
			return this.id;
		}
		
		boolean isDmgBreak()
		{
			return this.id > 0 && this.checkFight;
		}
		
		void enterSocialAction(MapRole role, SBean.SocialActionCFGS socialActionCfg)
		{
			this.id = socialActionCfg.keep == 1 ? socialActionCfg.id : 0;
			this.checkFight = socialActionCfg.checkFight == 1;
			this.buffs = new ArrayList<>(socialActionCfg.buffs);
			this.relateState = socialActionCfg.relateState;
			
			Set<Integer> rids = role.getRoleIDsNearBy(null);
			if(!rids.isEmpty())
				ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_socialaction(role.getID(), socialActionCfg.id));
			
			for(int buffID: this.buffs)
			{
				SBean.BuffCFGS buffCfg = GameData.getInstance().getBuffCFG(buffID);
				if(buffCfg == null)
					continue;
				
				Buff buff = role.createNewBuff(buffCfg, 0, 0, role, null);
				buff.endTime = -1;
				role.addBuff(buff, role, null);
			}
			
			if(this.relateState > 0)
			{
				role.addState(this.relateState);
				SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
				ms.getRPCManager().sendStrPacket(role.getID(), new SBean.role_addstate(this.relateState, timeTick));
				Set<Integer> ridsWithoutSelf = new HashSet<>(rids);
				ridsWithoutSelf.remove(role.getID());
				if(!ridsWithoutSelf.isEmpty())
					ms.getRPCManager().broadcastStrPacket(ridsWithoutSelf, new SBean.nearby_role_addstate(role.getID(), this.relateState, timeTick));
			}
		}
		
		void clearSocialAction(MapRole role)
		{
			for(int buffID: this.buffs)
			{
				Buff buff = role.buffs.get(buffID);
				if (buff != null && buff.spiritEffectID == 0)
					role.removeBuff(buff);
			}
			
			if(this.relateState > 0)
			{
				role.removeState(this.relateState);
				SBean.TimeTick timeTick = ms.getMapManager().getTimeTickDeep();
				ms.getRPCManager().sendStrPacket(role.getID(), new SBean.role_removestate(this.relateState, timeTick));
				Set<Integer> rids = role.getRoleIDsNearBy(role);
				if(!rids.isEmpty())
					ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_removestate(role.getID(), this.relateState, timeTick));
			}
			
			this.clear();
		}
		
		
		void clear()
		{
			this.id = 0;
			this.buffs.clear();
			this.checkFight = false;
		}
	}
	
	int getSocialActionID()
	{
		return this.socialAction.getID();
	}
	
	boolean canSocialAction()
	{
		if(this.isDead() || !this.active || this.robot || this.inMulRoles() || this.weddingCar > 0 || !GameData.getInstance().getCommonCFG().socialAction.effectMapTypes.contains(this.getMapType()))
			return false;
		
		if(this.checkState(Behavior.EBMOVE) || this.checkState(Behavior.EBATTACK) || this.checkState(Behavior.EBDISATTACK))
			return false;
		
		return  this.curRideHorse <= 0 && this.curMineralID <= 0 && !this.weaponMotivate && (this.alterState == null || this.alterState.alterID <= 0) && this.alterPet == null;
	}
	
	public void roleSocailAction(int actionID)
	{
		if(this.getSocialActionID() == actionID || !canSocialAction())
			return;
	
		roleSocailActionImpl(actionID);
	}
	
	private void roleSocailActionImpl(int actionID)
	{
		SBean.SocialActionCFGS socialActionCfg = GameData.getInstance().getSocialActionCFGS(actionID);
		if(socialActionCfg == null)
			return;
		
		this.socialAction.clearSocialAction(this);
		this.socialAction.enterSocialAction(this, socialActionCfg);
		this.roleIdleTime = -1;
	}
	
	private void tryClearSocialAction()
	{
		this.roleIdleTime = -1;
		if(this.getSocialActionID() == 0)
			return;
		
		this.socialAction.clearSocialAction(this);
	}
	
	private void dmgBreakSocialAction()
	{
		this.roleIdleTime = -1;
		if(!this.socialAction.isDmgBreak())
			return;
		
		this.socialAction.clearSocialAction(this);
		Set<Integer> rids = this.getRoleIDsNearBy(null);
		if(!rids.isEmpty())
			ms.getRPCManager().broadcastStrPacket(rids, new SBean.nearby_role_socialaction(this.getID(), 0));
	}
	
	private void trySit(int timeTick)
	{
		if(!canSocialAction())
			return;
		
		if(GameData.getInstance().getCommonCFG().socialAction.autoActionID == this.getSocialActionID())
			return;
		
		if(this.roleIdleTime <= 0)
		{
			this.roleIdleTime = timeTick;
			return;
		}
		
		if(timeTick - this.roleIdleTime < GameData.getInstance().getCommonCFG().socialAction.autoActionTime)
			return;
		
		roleSocailActionImpl(GameData.getInstance().getCommonCFG().socialAction.autoActionID);
	}
	
	public void setMonsterToBirthPos(int mid)
	{
		Monster monster = this.curMap.getMonster(mid);
		if(monster != null)
			monster.setToBirthPos();
	}
	
	public void redNamePunish()
	{
		this.trigSkillImpl(GameData.getInstance().getCommonCFG().pk.punishSkill, 1, 0);
		this.redNameDamageBuff = GameData.getInstance().getCommonCFG().pk.punishDamageBuff;
	}
	
	void buffChangeTrig(int buffID, int type)
	{
		super.buffChangeTrig(buffID, type);
		if(type == GameData.BUFF_CHANGETYPE_REMOVE && buffID == this.redNameDamageBuff)
			this.redNameDamageBuff = 0;
	}
	
	void syncMapDamageRank()
	{
		if (this.curMap != null)
			this.curMap.syncDamageRank(this);
	}
	
	void syncSectGroupMapInfo()
	{
		if (this.curMap != null && this.curMap instanceof SectGroupMap)
		{
			SectGroupMap sgm = SectGroupMap.class.cast(this.curMap);
			sgm.syncSectGroupCurInfo(this);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------
	class MoveCheckInfo
	{
		GVector3 preMovePosition;
		SBean.TimeTick preClientMoveTimeTick;
		SBean.TimeTick preServerMoveTimeTick;
		
		MoveCheckInfo()
		{
			
		}
		
		void update(GVector3 preMovePosition, SBean.TimeTick preClientMoveTimeTick, SBean.TimeTick preServerMoveTimeTick)
		{
			this.preMovePosition = preMovePosition;
			this.preClientMoveTimeTick = preClientMoveTimeTick;
			this.preServerMoveTimeTick = preServerMoveTimeTick;
			
			
		}
		
		boolean isValid()
		{
			return preMovePosition != null && preClientMoveTimeTick != null && preServerMoveTimeTick != null;
		}
		
		void reset()
		{
			this.preMovePosition = null;
			this.preClientMoveTimeTick = null;
			this.preServerMoveTimeTick = null;
		}
	}
	
	
	////////////////////////////////////
	public static final int UPDATE_NEARBY_ROLE_INTERVAL = 10;
	//同步位置信息 时间、位置
	private int syncTime;
	private List<Integer> checkSpeeds = new ArrayList<>();
	private SBean.Team team;
	private int tickTimeUpdate;
	Set<Integer> nearbyRoles = new HashSet<>();
	Set<Integer> beSeenRoles = new HashSet<>();
	private int lastUpdateNearbyTime;
	
	
	PropRole propRole;
	public String roleName;
	short headIcon;
	byte gender; //性别
	byte face;
	byte hair;
	private Set<Integer> dodgeSkills = new HashSet<>();
	private int dodgeSkillCDReduce;
	
	private int fightSP; //职业能量
	private int curSP;
	private int curRideHorse;
	SBean.MulRoleInfo mulRoleInfo = new SBean.MulRoleInfo(0, 0, new ArrayList<>());
	private SBean.DBDIYSkillData curDIYSkill;//自创武功
	
	private SBean.SectBrief sectBrief = new SBean.SectBrief(0, "", (byte)-1, (short)0, 0);
	private Map<Integer, Integer> showFashionTypes = new HashMap<>();
	private int curPermanentTitle;
	private List<SBean.DBTitleSlot> curTimedTitles = new ArrayList<>(GameData.MAX_TITLESLOT_SIZE);

	//佣兵
	SBean.PetHost selfPethost;
	SBean.PetHost otherPethost;
	Map<Integer, Integer> fightPetSeqs = new HashMap<>();
	Map<Integer, PetState> petState = new HashMap<>();

	//神兵
	private boolean weaponMotivate = false;
	private long motivateEndTime;
	private int curWeapon;
	private int curUniqueSkill;

	//采矿
	private long mineralEndTime;
	private int curMineralID;

	private long fightStateEndTime;
	SBean.PKInfo pkInfo; //PK
	private int orangeNameTime;
	private int pkStateTime;
	private List<Float> classReduce = new ArrayList<>(); //职业伤害减免
	private long fightTime;
	private SBean.DBAlterState alterState = new SBean.DBAlterState(0, 0);
	private Pet alterPet;

	boolean robot = false;
	int curAttackSeq;
	private TreeMap<Long, FightSkill> skillOrders = new TreeMap<>();

	//心法被动技能
	private Map<Integer, SBean.IntList> passiveBuffs = new TreeMap<>(); //<skillID, IntList>
	private Map<Integer, Integer> passiveTrigAis = new TreeMap<>(); //<aid, ai instanceID>
	private Map<Integer, Integer> horseSkillAis = new TreeMap<>();
	private Map<Integer, Integer> weaponAis = new HashMap<>();
	private Map<Integer, Integer> weaponUSkillAis = new HashMap<>();
	private Map<Integer, Integer> armorAis = new HashMap<>();
	private Map<Integer, Integer> legendThreeAis = new HashMap<>();
	private SkillData diySkillData;
	
	private Set<Integer> normalSkills = new HashSet<>();
	private Set<Integer> weaponSkills = new HashSet<>();
	private Set<Integer> alterSkills = new HashSet<>();
	
	List<SkillEntity> skillEntitys = new ArrayList<>();
	List<Blur> blurs = new ArrayList<>();

	private int ping;

	private GVector3 syncPos= new GVector3();
	private GVector3 clientLastPos = new GVector3();
	private MoveCheckInfo moveCheckInfo = new MoveCheckInfo();
//	private GVector3 preMovePosition;
//	private SBean.TimeTick preMoveTimeTick;
	private int totalPower;

	boolean isMainSpawnPos;
	boolean ghost;
	
	private boolean petLack;
	
	private SocialAction socialAction = new SocialAction();	//社交动作（持续性的需要缓存）
//	private int socialActionID;			//社交动作（持续性的需要缓存）
//	boolean clanBattleHurt;				//宗门战进攻方是否受伤
	
	byte carOwner;						//是否有镖车 0 , 1
	byte carRobber;						//0:无  > 0:劫镖  < 0:劫镖暴露
//	byte delayRevive;
	
	private int maxFastSkill;
	
	private Set<Integer> sceneTrigMonsters = new HashSet<>();	//monster instanceID
	private Map<Integer, Integer> taskDrop = new HashMap<>();	//<monsterID, randDropTbID>
	
	private int privateMapMasterCount;
	private Map<Integer, Boolean> weaponUSkills = new HashMap<>();
	private float weaponAddExp;
	private WeaponDrop weaponDrop = new WeaponDrop();
	Map<Integer, Integer> killRoles = new HashMap<>();
	
	int weddingCar;
	private MarriageInfo marriageInfo;
	
	private boolean isHeirloomDisplay;
	
	Set<Integer> damageRoles = new HashSet<>();
	int dayFailedStreak;
	
	private int roleIdleTime;
	int vipLevel;
	int curWizardPet;
	private int redNameDamageBuff;
	boolean canTakeDrop;
}
