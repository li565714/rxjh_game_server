package i3k.gs;

import java.util.ArrayList;
import java.util.List;

import i3k.DBMailBox;
import i3k.DBRole;
import i3k.SBean;
import i3k.util.GameRandom;

public class RoleCopy extends Role 
{
	public RoleCopy(int id, GameServer gs)
	{
		super(id, gs, 0);
		this.netsid = 0;
		this.loginnetsid = 0;
	}
	
	public RoleCopy fromDBRole(RoleShare roleShare, DBRole dbRole, DBMailBox dbMailbox, int targetMapID)
	{
		super.fromDBRole(roleShare, dbRole, dbMailbox);
		randSkill();
		randLevel(targetMapID);
		randGenderAndName();
		this.sectData.data.sectBrief.sectID = 0;
		this.sectData.data.sectBrief.sectName = "";
		this.marriageData.partnerName = "";
		return this;
	}
	
	private void randLevel(int targetMapID)
	{
		SBean.MapCopyCFGS cfg = GameData.getInstance().getMapCopyCFGS(targetMapID);
		if(cfg == null)
			return;
		
		int minLevel = 0;
		for(SBean.DBWearEquip we: this.wearEquips.values())
		{
			SBean.EquipCFGS eCfg = GameData.getInstance().getEquipCFG(we.equip.id);
			if(eCfg == null || eCfg.lvlReq < minLevel)
				continue;
			
			minLevel = eCfg.lvlReq;
		}
		
		minLevel = minLevel > cfg.needLevel ? minLevel : cfg.needLevel;
		int randLevel = GameRandom.getRandInt(minLevel, this.level);
		if(randLevel != this.level)
		{
			this.level = randLevel;
			this.roleProperties.onUpdateLevel(this.level);
		}
	}
	
	private void randSkill()
	{
		this.curSkills.clear();
		List<Integer> allSkills = new ArrayList<>();
		for(int sid: this.skills.keySet())
		{
			if(this.uniqueSkills.contains(sid))
				continue;
			
			allSkills.add(sid);
		}
		
		for(int i = allSkills.size(); i < GameData.CUR_USE_SKILL_COUNT; i++)
			allSkills.add(0);
		
//		this.curSkills.add(12101);
		float unit = (float)allSkills.size() / GameData.CUR_USE_SKILL_COUNT;
		for(int i = 0; i < GameData.CUR_USE_SKILL_COUNT; i++)
		{
			int r = GameRandom.getRandInt((int)(unit * i), (int)(unit * i + unit));
			this.curSkills.add(allSkills.get(r));
		}
		
		gs.getLogger().debug("--------------------robot " + this.name + " rand skills " + this.curSkills);
	}
	
	private void randGenderAndName()
	{
		String surName = GameData.getInstance().randRobotSurName();
		SBean.RobotOverviewCFGS ov = GameData.getInstance().randRobotOverview();
		if(surName != null && ov != null)
		{
			this.name = surName + ov.name + this.id;
			if(this.gender != ov.gender)
				this.fashionEquip.curFashions.clear();
			
			this.gender = (byte) ov.gender;
			if(this.gender == 2)
				this.headIcon = 310;
		}
	}
	
	synchronized void kickFromMap()
	{
		gs.getLoginManager().delRobot(this.id);
	}
	
	synchronized void onMRoomMemberKicked(Role member)
	{
		onMRoomMemberLeaveImpl(member);
		if (member.id == this.id)
			gs.getLoginManager().delRobot(this.id);
	}
	
	synchronized void onMRoomMemberLeave(Role member)
	{
		onMRoomMemberLeaveImpl(member);
	}
	
	private void onMRoomMemberLeaveImpl(Role member)
	{
		if (member.id == this.id)
		{
			this.room.id = 0;
			this.room.mapId = 0;
			this.room.leader = 0;
			this.room.members.clear();
		}
		else
		{
			this.room.members.remove(Integer.valueOf(member.id));
		}
	}
	
	synchronized void onMRoomLeaderChange(Role newLeader)
	{
		this.room.leader = newLeader.id;
		gs.getRPCManager().sendStrPacket(this.netsid, new SBean.mroom_change_leader(newLeader.id, newLeader.name));
	}
	
	synchronized void onSelfJoinMRoom(SBean.MRoom room)
	{
		this.room = room;
		this.roomInvites.clear();
	}
	
	synchronized void onNewMemberJoinMRoom(Role newMember)
	{
		this.room.members.add(newMember.id);
	}
	
	public synchronized void syncCommonMapCopyEnd(int mapId, int mapInstance, int time, int score)
	{
		MapCopyContext context = this.gameMapContext.getMapCopyContext();
		if (context instanceof CommonMapCopyContext)
		{
//			gs.getLoginManager().delRobot(this.id);
		}
	}
	
	synchronized boolean enterPublicMapCopy(int mid, int instanceId, boolean mainSpawnPos)
	{
		if(!super.enterPublicMapCopy(mid, instanceId, mainSpawnPos))
			return false;
		
		//TODO
		return true;
	}
	
	public synchronized void syncMapCopyTimeoutLeave(int mapId, int instanceId)
	{
		MapCopyContext context = this.gameMapContext.getMapCopyContext();
		if (context != null)
		{
			if (context.getMapId() == mapId && context.getMapInstance() == instanceId)
			{
				this.gameMapContext.endMapCopy();
				gs.getLoginManager().delRobot(this.id);
			}
		}
	}
	
	public void syncMapMarriageSkills(boolean isDivoce)
	{
		//TODO
	}
	
	public synchronized void addKill(int mapId, int mapInstance, int type, int id, int count, float weaponAdd, int killRole)
	{
		
	}
}
