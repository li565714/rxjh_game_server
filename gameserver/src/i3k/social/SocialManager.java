package i3k.social;

import i3k.DBSocialTheme;
import i3k.DBSocialUser;
import i3k.DBUser;
import i3k.SBean;
import i3k.gs.ExchangeService;
import i3k.gs.GameData;
import i3k.util.GameTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import ket.kdb.Table;
import ket.kdb.TableEntry;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.kdb.Transaction.AutoInit;
import ket.kdb.Transaction.ErrorCode;

public class SocialManager 
{
	SocialManager(SocialServer ss)
	{
		this.ss = ss;
	}
	
	void start()
	{	
		
	}
	
	void destroy()
	{
		save();
	}
	
	public class SaveTrans implements Transaction
	{

		public SaveTrans(Map<Long, DBSocialUser> dbUsers, Map<Integer, DBSocialTheme> dbThemes)
		{
			this.dbUsers = dbUsers;
			this.dbThemes = dbThemes;
		}
		
		@Override
		public boolean doTransaction()
		{
			for(Map.Entry<Long, DBSocialUser> e: dbUsers.entrySet())
			{
				users.put(e.getKey(), e.getValue());
			}
			
			for(Map.Entry<Integer, DBSocialTheme> e: dbThemes.entrySet())
			{
				themes.put(e.getKey(), e.getValue());
			}
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				ss.getLogger().warn("social comment save failed");
			}
		}
		
		@AutoInit
		public Table<Long, DBSocialUser> users;
		@AutoInit
		public Table<Integer, DBSocialTheme> themes;
		
		final Map<Long, DBSocialUser> dbUsers;
		final Map<Integer, DBSocialTheme> dbThemes;
	}
	
	void onTimer(int timeTick)
	{
		if(timeTick - this.lastSavaTime > SOCIAL_COMMENT_SAVE_INTERVAL)
			save();
	}
	
	void init(TableReadonly<Long, DBSocialUser> dbUsers, TableReadonly<Integer, DBSocialTheme> dbThemes)
	{
		for(int i = GameData.SOCIAL_COMMENT_THEME_START; i <= GameData.SOCIAL_COMMENT_THEME_END; i++)
		{
			this.clusters.put(i, new ThemeCommentCluster(i));
		}
		
		if(dbUsers != null)
		{
			for(TableEntry<Long, DBSocialUser> e: dbUsers)
			{
				this.users.put(e.getKey(), new SocialUser().fromDB(e.getValue()));
			}
		}
		
		if(dbThemes != null)
		{
			for(TableEntry<Integer, DBSocialTheme> e: dbThemes)
			{
				int themeType = GameData.getSocialCommentThemeTypeFromKey(e.getKey());
				int themeID = GameData.getSocialCommentThemeIDFromKey(e.getKey());
				
				ThemeCommentCluster cluster = clusters.get(themeType);
				if(cluster == null)
					continue;
				
				cluster.initAddTheme(themeID, e.getValue());
			}
		}
	}
	
	void save()
	{
		ss.getDB().execute(new SaveTrans(getAllUsers(), getAllThemes()));
		this.lastSavaTime = GameTime.getTime();
	}
	
	private Map<Integer, DBSocialTheme> getAllThemes()
	{
		Map<Integer, DBSocialTheme> all = new HashMap<>();
		for(ThemeCommentCluster cluster: this.clusters.values())
			all.putAll(cluster.toDB());
		
		return all;
	}
	
	private Map<Long, DBSocialUser> getAllUsers()
	{
		Map<Long, DBSocialUser> all = new HashMap<>();
		for(Map.Entry<Long, SocialUser> e: this.users.entrySet())
			all.put(e.getKey(), e.getValue().toDB());
		
		return all;
	}
	
	public static class SocialUser
	{
		int serverID;
		String serverName;
		int roleID;
		String roleName;
		
		int sendCount;
		int likesCount;
		int likedCount;
		int dislikesCount;
		int dislikedCount;
		
		int lastDayRefresh;
		
		SocialUser()
		{
			
		}
		
		SocialUser(int serverID, String serverName, int roleID, String roleName)
		{
			this.serverID = serverID;
			this.serverName = serverName;
			this.roleID = roleID;
			this.roleName = roleName;
		}
		
		private void dayRefresh()
		{
			int nowDay = GameData.getDayByRefreshTimeOffset(GameTime.getTime());
			if(nowDay != lastDayRefresh)
			{
				lastDayRefresh = nowDay;
			}
		}
		
		SocialUser fromDB(DBSocialUser dbUser)
		{
			this.serverID = dbUser.serverID;
			this.serverName = dbUser.serverName;
			this.roleID = dbUser.roleID;
			this.roleName = dbUser.roleName;
			
			this.sendCount = dbUser.sendCount;
			this.likesCount = dbUser.likesCount;
			this.likedCount = dbUser.likedCount;
			this.dislikesCount = dbUser.likesCount;
			this.dislikedCount = dbUser.dislikedCount;
					
			this.lastDayRefresh = dbUser.lastDayRefresh;
			
			return this;
		}
		
		synchronized DBSocialUser toDB()
		{
			DBSocialUser dbUser = new DBSocialUser();
			dbUser.serverID = this.serverID;
			dbUser.serverName =  this.serverName;
			dbUser.roleID = this.roleID;
			dbUser.roleName = this.roleName;
			
			dbUser.sendCount = this.sendCount;
			dbUser.likesCount = this.likesCount;
			dbUser.likedCount = this.likedCount;
			dbUser.dislikesCount = this.dislikesCount;
			dbUser.dislikedCount = this.dislikedCount;
			
			dbUser.lastDayRefresh = this.lastDayRefresh;
			return dbUser;
		}
		
		public synchronized void updateName(String serverName, String roleName)
		{
			this.serverName = serverName;
			this.roleName = roleName;
		}
		
		public synchronized boolean sendComment()
		{
			dayRefresh();
			sendCount++;
			return true;
		}
		
		public synchronized boolean likeCommont()
		{
			dayRefresh();
			likesCount++;
			return true;
		}
		
		public synchronized boolean dislikeComment()
		{
			dayRefresh();
			dislikesCount++;
			return true;
		}
		
		public synchronized void liked()
		{
			likedCount++;
		}
		
		public synchronized void disliked()
		{
			dislikedCount++;
		}
		
		public synchronized void updateComment(SBean.SocialComment comment)
		{
			comment.serverId = this.serverID;
			comment.serverName = this.serverName;
			comment.roleId = this.roleID;
			comment.roleName = this.roleName;
		}
	}
	
	public void roleSendComment(int serverID, String serverName, int roleID, String roleName, int themeType, int themeID, String comment, ExchangeService.SendCommentCallback callback)
	{
		ThemeCommentCluster cluster = clusters.get(themeType);
		if(cluster == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		long grid = GameData.getLongTypeValue(serverID, roleID);
		SocialUser sender = users.get(grid);
		if(sender == null)
		{
			sender = new SocialUser(serverID, serverName, roleID, roleName);
			users.put(grid, sender);
		}
		
		if(!sender.sendComment())
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		cluster.sendComment(grid, themeID, comment);
		callback.onCallback(GameData.PROTOCOL_OP_SUCCESS);
	}
	
	public void syncPageComment(int themeType, int themeID, int tag, int pageNo, int len, ExchangeService.SyncPageCommentCallback callback)
	{
		ThemeCommentCluster cluster = clusters.get(themeType);
		if(cluster == null)
		{
			callback.onCallback(GameData.emptyList());
			return;
		}
		
		List<SBean.SocialComment> lst = new ArrayList<>();
		List<SBean.DBSocialComment> tmp = cluster.syncPageComment(themeID, tag, pageNo, len);
		for(SBean.DBSocialComment e: tmp)
		{
			SocialUser owner = users.get(e.ownerId);
			if(owner == null)
				continue;
			
			SBean.SocialComment sc = new SBean.SocialComment(0, 0, "", "", e.commentId, e.comment, e.liked, e.disliked, e.sendTime);
			owner.updateComment(sc);
			lst.add(sc);
		}
		
		callback.onCallback(lst);
	}
	
	public void likeComment(int serverID, String serverName, int roleID, String roleName, int themeType, int themeID, int commentID, ExchangeService.LikeCommentCallback callback)
	{
		long grid = GameData.getLongTypeValue(serverID, roleID);
		SocialUser user = users.get(grid);
		if(user == null)
		{
			user = new SocialUser(serverID, serverName, roleID, roleName);
			users.put(grid, user);
		}
		else
		{
			user.updateName(serverName, roleName);
		}
			
		
		ThemeCommentCluster cluster = clusters.get(themeType);
		if(cluster == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		long ownerID = cluster.likeComment(user, themeID, commentID);
		SocialUser owner = users.get(ownerID);
		if(owner != null)
			owner.liked();
		
		callback.onCallback(GameData.getLowInt(ownerID));
	}
	
	public void dislikeComment(int serverID, String serverName, int roleID, String roleName, int themeType, int themeID, int commentID, ExchangeService.DislikeCommentCallback callback)
	{
		long grid = GameData.getLongTypeValue(serverID, roleID);
		SocialUser user = users.get(grid);
		if(user == null)
		{
			user = new SocialUser(serverID, serverName, roleID, roleName);
			users.put(grid, user);
		}
		else
		{
			user.updateName(serverName, roleName);
		}
		
		ThemeCommentCluster cluster = clusters.get(themeType);
		if(cluster == null)
		{
			callback.onCallback(GameData.PROTOCOL_OP_FAILED);
			return;
		}
		
		long ownerID = cluster.dislikeComment(user, themeID, commentID);
		SocialUser owner = users.get(ownerID);
		if(owner != null)
			owner.disliked();
		
		callback.onCallback(GameData.getLowInt(ownerID));
	}

	private static final int SOCIAL_COMMENT_SAVE_INTERVAL = 900;
	int lastSavaTime;
	
	SocialServer ss;
	ConcurrentMap<Long, SocialUser> users = new ConcurrentHashMap<>();
	Map<Integer, ThemeCommentCluster> clusters = new HashMap<>();
}
