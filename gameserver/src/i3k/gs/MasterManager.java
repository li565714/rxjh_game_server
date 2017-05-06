
package i3k.gs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.kdb.Transaction.AutoInit;
import ket.kdb.Transaction.ErrorCode;
import ket.util.Stream;
import i3k.SBean;
import i3k.gs.BossManager.SaveTrans;
import i3k.util.GameTime;

public class MasterManager 
{
	private static final int MASTER_LIST_SAVE_INTERVAL = 611;
	private static final int MASTER_OFFER_REQ_RESERVE_TIME = 60;
	public static final String DB_KEY_NAME = "master_list";
	
	public MasterManager(GameServer gs)
	{
		this.gs = gs;
	}
	
	public void onTimer(int timeTick)
	{
		if(this.lastSaveTime + MASTER_LIST_SAVE_INTERVAL <= timeTick)
			this.save();
		
		clearOfferCache();
	}
	
	private void clearOfferCache()
	{
		int now = GameTime.getTime();
		synchronized( offerCache )
		{
			Iterator<Map.Entry<Long, Integer>> iter = offerCache.entrySet().iterator();
			while( iter.hasNext() )
			{
				Integer e = iter.next().getValue();
				if( e + MASTER_OFFER_REQ_RESERVE_TIME >= now )
					break;
				iter.remove();
			}
		}
	}
	
	public class SaveTrans implements Transaction
	{
		public SaveTrans(SBean.MasterAnnounce announce)
		{
			this.announce = announce;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE(DB_KEY_NAME);
			byte[] data = Stream.encodeLE(announce);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if( errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("world map boss save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		public final SBean.MasterAnnounce announce;
	}
	
	public void save()
	{
		int now = GameTime.getTime();
		SBean.MasterCFGS cfg = GameData.getInstance().getMasterCFGS();
		synchronized( map )
		{
			Iterator<Map.Entry<Integer, SBean.MasterAnnounceEntry>> iter = map.entrySet().iterator();
			while( iter.hasNext() )
			{
				SBean.MasterAnnounceEntry e = iter.next().getValue();
				if( e.insertTime + cfg.maxAnnouncementReserveTime >= now )
					break;
				iter.remove();
			}
		}
		gs.getDB().execute(new SaveTrans(getAnnounceData()));
		this.lastSaveTime = GameTime.getTime();
	}
	
	public SBean.MasterAnnounce getAnnounceData()
	{
		SBean.MasterAnnounce data = new SBean.MasterAnnounce();
		data.announceList = new ArrayList<>();
		synchronized( map )
		{
			data.announceList.addAll(map.values());
		}
		return data;
	}
	
	private void onMasterInfoReq(final int masterID, final int sid, final List<Integer> roleIDs
			, final SBean.master_info_res res)
	{
		gs.getLoginManager().readRoles(roleIDs, roles->
		{
			if( roles != null && roles.size() == roleIDs.size() )
			{
				res.members = new ArrayList<>();
				res.retCode = GameData.PROTOCOL_OP_MASTER_OK;
				for(Role role : roles)
				{
					if( role == null )
					{
						res.retCode = GameData.PROTOCOL_OP_MASTER_FAIL;
						break;
					}
					if( role.id != masterID && role.master.master != masterID )
					{
						gs.getMasterManager().masterLeave(masterID, role.id);
					}	
					else
						res.members.add(role.getMasterInfoWithLock());
				}
			}
			gs.getRPCManager().sendStrPacket(sid, res);
		});
	}
	
	public void noticeApply(final int masterID, SBean.ApprenticeDetail detail)
	{
		Role role = gs.getLoginManager().getOnGameRole(masterID);
		if( role == null || ! role.isOnline() )
			return;
		int sid = role.netsid;
		if( sid == 0 )
			return;
		gs.getRPCManager().sendStrPacket(sid, new SBean.master_apply_notice(detail));
	}
	
	public void noticeAcceptApply(final int appID, int masterID, String masterName, boolean accept)
	{
		Role role = gs.getLoginManager().getOnGameRole(appID);
		if( role == null || ! role.isOnline() )
			return;
		int sid = role.netsid;
		if( sid == 0 )
			return;
		gs.getRPCManager().sendStrPacket(sid, new SBean.master_accept_apply_notice(masterID, masterName, accept));
	}
	
	public void noticeAcceptOfferApply(final int masterID, int appID, String appName)
	{
		Role role = gs.getLoginManager().getOnGameRole(masterID);
		if( role == null || ! role.isOnline() )
			return;
		int sid = role.netsid;
		if( sid == 0 )
			return;
		gs.getRPCManager().sendStrPacket(sid, new SBean.master_accept_offer_notice(appID, appName));
	}
	
	public void masterDismiss(boolean bActive, final int masterID, final int appID)
	{		
		gs.getLoginManager().exeCommonRoleVisitor(appID, true, new LoginManager.CommonRoleVisitor() {
			
			@Override
			public boolean visit(Role role, Role sameUserRole) {
				return role != null && role.masterOnPassiveDismiss(masterID);
			}
			
			@Override
			public void onCallback(boolean success) {
				// TODO
			}
		});
		
	}
	
	public void masterLeave(final int masterID, final int appID)
	{		
		gs.getLoginManager().exeCommonRoleVisitor(masterID, false, new LoginManager.CommonRoleVisitor() {
			
			@Override
			public boolean visit(Role role, Role sameUserRole) {
				if( role != null )
				{
					synchronized( role )
					{
						if( role.master.apprentices.removeIf(app->app == appID) )
							return true;
					}
				}
				return false;
			}
			
			@Override
			public void onCallback(boolean success) {
				// TODO
			}
		});
		
	}
	
	public void onMasterInfoReq(final int sid, final int masterRoleID, 
			final int sourceRoleID, final SBean.master_info_res res)
	{
		gs.getLoginManager().exeCommonRoleVisitor(masterRoleID, false, new LoginManager.CommonRoleVisitor() {
			
			final List<Integer> roleIDs = new ArrayList<>();
			
			@Override
			public boolean visit(Role role, Role sameUserRole) {
				if( role != null )
				{
					if( ! role.master.apprentices.isEmpty() )
					{
						roleIDs.add(role.id);
						roleIDs.addAll(role.master.apprentices);
					}
				}
				return false;
			}
			
			@Override
			public void onCallback(boolean success) {
				if( success && masterRoleID != sourceRoleID )
				{
					if( ! roleIDs.stream().anyMatch(rid->rid == sourceRoleID) )
					{
						masterDismiss(false, masterRoleID, sourceRoleID);
					}
				}
				
				if( success && ! roleIDs.isEmpty() )
				{
					onMasterInfoReq(masterRoleID, sid, roleIDs, res);
				}
				else
				{
					res.retCode = GameData.PROTOCOL_OP_MASTER_FAIL;
					gs.getRPCManager().sendStrPacket(sid, res);
				}
			}
		});
	}
	
	public boolean removeAnnounce(int rid)
	{
		synchronized( map )
		{
			map.remove(rid);
		}
		return true;
	}
	
	public String getAnnounce(int rid)
	{
		synchronized( map )
		{
			SBean.MasterAnnounceEntry e = map.get(rid);
			if( e != null )
				return e.content;
		}
		return null;
	}
	
	public void masterAcceptOfferConfirm(final int sid,
			final int appID,
			final int masterID,
			final SBean.master_accept_offer_res res)
	{
		gs.getLoginManager().exeCommonRoleVisitor(appID, true, new LoginManager.CommonRoleVisitor() {
			
			String appName = null;
			
			@Override
			public boolean visit(Role role, Role sameUserRole) {
				if( role != null )
				{
					appName = role.name;
					res.retCode = role.masterOnAcceptOfferConfirm(masterID);
				}
				else
					res.retCode = GameData.PROTOCOL_OP_MASTER_NOT_FOUND;
				return res.retCode == GameData.PROTOCOL_OP_MASTER_OK;
			}
			
			@Override
			public void onCallback(boolean success) {
				if( ! success || appName == null )
					res.retCode = GameData.PROTOCOL_OP_MASTER_NOT_FOUND;
				if( res.retCode != GameData.PROTOCOL_OP_MASTER_OK )
				{
					gs.getMasterManager().masterDismiss(false, masterID, appID);
				}
				else
				{
					/*
					gs.getLoginManager().sysSendMail(req.targetRoleID
							, MailBox.SysMailType.MasterAccept
							, MailBox.MAX_RESERVE_TIME
							, masterID + "|" + masterName
							, new ArrayList<>()
							, new ArrayList<>());
					*/
					noticeAcceptOfferApply(masterID, appID, appName);
				}
				gs.getRPCManager().sendStrPacket(sid, res);
			}
		});
	}
	
	public void masterAcceptApplyConfirm(final int sid,
			final int masterID,
			final String masterName,
			final SBean.master_accept_apply_req req,
			final SBean.master_accept_apply_res res)
	{
		gs.getLoginManager().exeCommonRoleVisitor(masterID, true, new LoginManager.CommonRoleVisitor() {
			
			boolean[] appFull = new boolean[1];
			@Override
			public boolean visit(Role role, Role sameUserRole) {
				if( role != null )
				{
					res.retCode = role.masterOnAcceptConfirm(req.targetRoleID, appFull);
				}
				else
					res.retCode = GameData.PROTOCOL_OP_MASTER_NOT_FOUND;
				return res.retCode == GameData.PROTOCOL_OP_MASTER_OK;
			}
			
			@Override
			public void onCallback(boolean success) {
				if( ! success )
					res.retCode = GameData.PROTOCOL_OP_MASTER_NOT_FOUND;
				if( res.retCode != GameData.PROTOCOL_OP_MASTER_OK )
				{
					gs.getMasterManager().masterDismiss(false, masterID, req.targetRoleID);
				}
				else
				{
					gs.getLoginManager().sysSendMail(req.targetRoleID
							, MailBox.SysMailType.MasterAccept
							, MailBox.MAX_RESERVE_TIME
							, masterID + "|" + masterName
							, new ArrayList<>()
							, new ArrayList<>());
					if( appFull[0] )
						gs.getMasterManager().removeAnnounce(masterID);
					gs.getMasterManager().noticeAcceptApply(req.targetRoleID, masterID, masterName, true);
				}
				gs.getRPCManager().sendStrPacket(sid, res);
			}
		});
	}
	
	public boolean setAnnounce(int rid, String content)
	{
		if( content == null || ! GameData.getInstance().checkInputStrValid(content, 
				GameData.getInstance().getMasterCFGS().maxAnnounceLength, false) )
			return false;
		synchronized( map )
		{
			SBean.MasterAnnounceEntry e = map.get(rid);
			if( e != null )
				e.content = content;
			else
				map.put(rid, new SBean.MasterAnnounceEntry(rid, GameTime.getTime(), content));
		}
		return true;
	}
	
	public void listMaster(final int sid, int lastStartIndex)
	{
		List<Integer> roleIDs = new ArrayList<>();
		final SBean.master_list_res res = new SBean.master_list_res();
		res.retCode = GameData.PROTOCOL_OP_MASTER_OK;
		res.masters = new ArrayList<>();
		res.startIndex = 0;
		int pageSize = GameData.getInstance().getMasterCFGS().masterListPageSize;
		int totalCount = 0;		
		synchronized( map )
		{
			totalCount = map.size();
			int fillCount = Math.min(pageSize, totalCount);
			if( totalCount > 0 )
			{
				if( lastStartIndex < 0 )			
					res.startIndex = rand.nextInt(totalCount);
				else
					res.startIndex = (lastStartIndex + pageSize) % totalCount;
			}
			
			Iterator<SBean.MasterAnnounceEntry> iter = map.values().iterator();
			for(int i = 0; i < res.startIndex; ++i)
				iter.next();
			while( roleIDs.size() < fillCount )
			{
				if( iter.hasNext() )
					roleIDs.add(iter.next().roleID);
				else
				{
					iter = map.values().iterator();
					continue;
				}
			}			
		}
		if( totalCount <= 0 || roleIDs.isEmpty() )
		{
			gs.getRPCManager().sendStrPacket(sid, res);
			return;
		}
		gs.getLoginManager().readRoles(roleIDs, roles->
		{
			if( roles != null )
			{
				for(Role role : roles)
				{
					if( role != null )
					{
						res.masters.add(role.getMasterDetail());
					}
				}
			}
			gs.getRPCManager().sendStrPacket(sid, res);
		});
	}
	
	public void init(SBean.MasterAnnounce data)
	{
		if( data == null )
			return;
		synchronized( map )
		{
			map.clear();
			data.announceList.stream().forEach(entry->map.put(entry.roleID, entry));
		}
	}
	
	private static long makeLong(int i1, int i2)
	{
		return (((long)i1)<<32)|((long)i2);
	}
	
	public void logOfferReq(int masterID, int appID)
	{
		synchronized( offerCache )
		{
			offerCache.put(makeLong(masterID, appID), GameTime.getTime());
		}
	}
	
	public boolean testOfferReq(int masterID, int appID)
	{
		synchronized( offerCache )
		{
			return offerCache.containsKey(makeLong(masterID, appID));
		}
	}
	
	public boolean removeOfferReq(int masterID, int appID)
	{
		synchronized( offerCache )
		{
			return null != offerCache.remove(makeLong(masterID, appID));
		}
	}
	
	public static void main(String[] args)
	{
	
	}
	
	private final GameServer gs;
	int lastSaveTime;
	private LinkedHashMap<Integer, SBean.MasterAnnounceEntry> map = new LinkedHashMap<>();
	private LinkedHashMap<Long, Integer> offerCache = new LinkedHashMap<>();
	private Random rand = new Random();
}
