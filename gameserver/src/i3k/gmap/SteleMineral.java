package i3k.gmap;

import java.util.List;

import i3k.SBean;
import i3k.gmap.MapCluster.MapStele;
import i3k.gs.GameData;
import i3k.util.GVector3;
import i3k.util.GameRandom;

public class SteleMineral extends Mineral
{
	final MapStele mapStele;
	
	SteleMineral(int configID, MapServer ms, MapStele mapStele)
	{
		super(configID, ms);
		this.mapStele = mapStele;
	}
	
	public SteleMineral createNew(SBean.Location location)
	{
		this.id = ms.getMapManager().getNextMineralID().incrementAndGet();
		this.curPosition = new GVector3(location.position);
		this.birthPosition.reset(this.curPosition);
		this.curRotation = new GVector3(location.rotation);

		this.ctrlType = ECTRL_TYPE_AI;
		this.entityType = GameData.ENTITY_TYPE_MINERAL;
		this.mineralCount = -1;
		this.standTime = -1;
		return this;
	}
	
	MineralInfo onMineralEnd(int roleID)
	{
		MineralInfo info = this.mineralInfos.remove(roleID);
		if(this.mapStele == null || !this.mapStele.canMineral())
			return null;
		
		this.mapStele.reduceRemainTimes();
		return info;
	}
	
	boolean checkTimesEnough()
	{
		return this.mapStele == null ? false : this.mapStele.getRemainTimes() > 0;
	}
	
	int getState()
	{
		return checkTimesEnough() ? 1 : 0;
	}
	
	void onRoleMineralSuccess(MapRole role)
	{
		if(this.mapStele == null)
			return;
		
		SBean.SteleMineralTypeCFGS typeCfg = GameData.getInstance().getSteleMineralTypeCFGS(this.mapStele.getSteleType());
		if(typeCfg == null)
			return;
		
		SBean.SteleMineralCFGS mCfg = GameData.getSteleMineralCFGS(typeCfg, this.mapStele.getIndex());
		if(mCfg == null)
			return;
		
		randCards(role, mCfg);
		refreshMonetsr(role, mCfg);
	}
	
	private void randCards(MapRole role, SBean.SteleMineralCFGS mCfg)
	{
		int mineralCard = GameRandom.getRandInt(mCfg.mineralCards.min, mCfg.mineralCards.max);
		if(mineralCard > 0)
			ms.getRPCManager().notifyGSSyncRoleAddSteleCard(role.getID(), mineralCard, GameData.STELE_ADD_CARD_TYPE_SELF);
		
		List<MapRole> membersNearBy = role.getTeamMemberNearBy(role, GameData.getInstance().getCommonCFG().team.expAddDistance);
		for(MapRole member: membersNearBy)
		{
			int memberCard = GameRandom.getRandInt(mCfg.memberCards.min, mCfg.memberCards.max);
			if(memberCard > 0)
				ms.getRPCManager().notifyGSSyncRoleAddSteleCard(member.getID(), memberCard, GameData.STELE_ADD_CARD_TYPE_MEMBER);
		}
	}
	
	private void refreshMonetsr(MapRole role, SBean.SteleMineralCFGS mCfg)
	{
		if(GameData.checkRandom(mCfg.refreshMonster))
		{
			SBean.SteleLevelMonsterCFGS lvlMonster = GameData.getInstance().getSteleLevelMonsterCFGS(role.level);
			if(lvlMonster == null)
				return;
			
			SBean.SteleMonsterCFGS smCfg = GameData.getSteleMonsterCFGS(lvlMonster);
			if(smCfg == null || smCfg.monsterID == 0)
				return;
			
			SBean.MonsterCFGS monsterCfg = GameData.getInstance().getMonsterCFGS(smCfg.monsterID);
			if(monsterCfg == null)
				return;
			
			for (int i = 0; i < smCfg.count; i++)
				this.curMap.createMonster(smCfg.monsterID, this.createPosition(GameData.getInstance().getCommonCFG().mineral.appearRadius), GVector3.randomRotation(), true, lvlMonster.standByTime * 1000, -1);
			
			if(smCfg.broadcast > 0)
				ms.getRPCManager().notifyGSSyncRefreshSteleMonster(role.getID(), role.getName(), this.getMapID(), this.getMapInstanceID(), this.mapStele.getSteleType(), this.mapStele.getIndex(), smCfg.monsterID);
		}
	}
}
