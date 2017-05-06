package i3k.social;

import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List; 

import ket.util.Stream;
import i3k.DBSocialTheme;
import i3k.SBean;
import i3k.gs.GameData;
import i3k.gs.RankData.RankItemReader;
import i3k.social.SocialManager.SocialUser;
import i3k.util.CyclicQueue;
import i3k.util.GameTime;

public class ThemeCommentCluster
{
	final static RankCommentReader likeReader = new RankCommentReader(true);
	final static RankCommentReader dislikeReader = new RankCommentReader(false);
	
	ConcurrentMap<Integer, ThemeComment> themeComments = new ConcurrentHashMap<>();
	final int themeType;
	
	ThemeCommentCluster(int themeType)
	{
		this.themeType = themeType;
	}
	
	public static class RankCommentReader implements i3k.gs.RankData.RankItemReader<SBean.DBSocialComment>
	{
		final boolean like;
		RankCommentReader(boolean like)
		{
			this.like = like;
		}
		
		@Override
		public int getRankItemId(i3k.SBean.DBSocialComment rankItem)
		{
			return rankItem.commentId;
		}

		@Override
		public int getRankItemKey(i3k.SBean.DBSocialComment rankItem)
		{
			return like ? rankItem.liked : rankItem.disliked;
		}

		@Override
		public boolean canUpdateRankItem(i3k.SBean.DBSocialComment rankItem, i3k.SBean.RankCFGS cfg, boolean blackListOn, Set<Integer> idBlackList)
		{
			return true;
		}
	}
	
	public static class CommentRank extends i3k.gs.RankData.Ranks<SBean.DBSocialComment>
	{
		public CommentRank(RankItemReader<SBean.DBSocialComment> reader)
		{
			super(reader);
		}
		
		public List<SBean.DBSocialComment> getPageComment(int pageNo, int len)
		{
			if(this.rankValues.isEmpty() || len <= 0)
				return GameData.emptyList();
			
			len = len > CyclicQueue.MAX_PAGE_LENGTH ? CyclicQueue.MAX_PAGE_LENGTH : len;
			int fromIndex = (pageNo - 1) * len + 1;
			if(fromIndex > this.rankValues.size())
				return GameData.emptyList();
			
			int index = 1;
			List<SBean.DBSocialComment> lst = new ArrayList<>();
			for(long rankValue: this.rankValues.descendingSet())
			{
				if(index < fromIndex)
				{
					index++;
					continue;
				}
				
				int cid = getItemId(rankValue);
				SBean.DBSocialComment comment = this.rankItems.get(cid);
				if(comment != null)
					lst.add(comment);
				
				if(lst.size() >= len)
					return lst;
				
				index++;
			}
			
			return lst;
		}
		
		public void updateRankItem(SBean.DBSocialComment comment)
		{
			SBean.DBSocialComment old = this.rankItems.get(comment.commentId);
			if(old != null)
			{
				old.liked = comment.liked;
				old.disliked = comment.disliked;
			}
		}
	}
	
	public void initAddTheme(int themeID, DBSocialTheme dbTheme)
	{
		themeComments.put(themeID, new ThemeComment().fromDB(dbTheme));
	}
	
	public void sendComment(long grid, int themeID, String comment)
	{
		ThemeComment tc = themeComments.get(themeID);
		if(tc == null)
		{
			tc = new ThemeComment();
			themeComments.put(themeID, tc);
		}
		
		tc.sendComment(grid, comment);
	}
	
	public List<SBean.DBSocialComment> syncPageComment(int themeID, int tag, int pageNo, int len)
	{
		ThemeComment tc = themeComments.get(themeID);
		if(tc == null)
			return GameData.emptyList();
		
		return tc.syncPageComment(tag, pageNo, len);
	}
	
	public long likeComment(SocialUser user, int themeID, int commentID)
	{
		ThemeComment tc = themeComments.get(themeID);
		if(tc == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		return tc.likeComment(user, commentID);
	}
	
	public long dislikeComment(SocialUser user, int themeID, int commentID)
	{
		ThemeComment tc = themeComments.get(themeID);
		if(tc == null)
			return GameData.PROTOCOL_OP_FAILED;
		
		return tc.dislikeComment(user, commentID);
	}
	
	public Map<Integer, DBSocialTheme> toDB()
	{
		Map<Integer, DBSocialTheme> themes = new HashMap<>();
		for(Map.Entry<Integer, ThemeComment> e: this.themeComments.entrySet())
		{
			int key = GameData.createSocialCommentThemeKey(themeType, e.getKey());
			themes.put(key, e.getValue().toDB());
		}
		return themes;
	}
	
	public static class ThemeComment
	{
		private final static int THEME_COMMENT_LATEST_COUNT 	= 1000;
		private final static int THEME_COMMENT_LIKE_COUNT 		= 100;
		private final static int THEME_COMMENT_DISLIKE_COUNT 	= 100;
		
		CyclicQueue<SBean.DBSocialComment> latestComments;
		CommentRank likeRank = new CommentRank(likeReader);
		CommentRank dislikeRank = new CommentRank(dislikeReader);
		
		int nextCommentID;
		Map<Integer, SBean.DBSocialComment> allComments = new HashMap<>();
		
		ThemeComment()
		{
			latestComments = new CyclicQueue<>(THEME_COMMENT_LATEST_COUNT);
		}
		
		private int getNextCommentID()
		{
			return ++nextCommentID;
		}
		
		ThemeComment fromDB(DBSocialTheme dbTheme)
		{
			int maxCommentID = 0;
			for(SBean.DBSocialComment e: dbTheme.latest)
			{
				latestComments.forcePush(e);
				allComments.put(e.commentId, e);
				
				if(e.commentId > maxCommentID)
					maxCommentID = e.commentId;
			}
			
			for(SBean.DBSocialComment e: dbTheme.likeRank)
			{
				likeRank.tryUpdateRank(e, THEME_COMMENT_LIKE_COUNT);
				
				allComments.put(e.commentId, e);
				if(e.commentId > maxCommentID)
					maxCommentID = e.commentId;
			}
			
			for(SBean.DBSocialComment e: dbTheme.dislikeRank)
			{
				dislikeRank.tryUpdateRank(e, THEME_COMMENT_DISLIKE_COUNT);
				allComments.put(e.commentId, e);
				if(e.commentId > maxCommentID)
					maxCommentID = e.commentId;
			}
			
			this.nextCommentID = maxCommentID;
			return this;
		}
		
		public synchronized DBSocialTheme toDB()
		{
			DBSocialTheme dbSocial = new DBSocialTheme();
			dbSocial.latest = Stream.clone(latestComments.getAll());
			dbSocial.likeRank = Stream.clone(new ArrayList<>(likeRank.rankItems.values()));
			dbSocial.dislikeRank = Stream.clone(new ArrayList<>(dislikeRank.rankItems.values()));
			return dbSocial;
		}
		
		public synchronized void sendComment(long grid, String comment)
		{
			int cid = getNextCommentID();
			SBean.DBSocialComment sc = new SBean.DBSocialComment(cid, grid, comment, 0, 0, GameTime.getTime());
			latestComments.forcePush(sc);
			allComments.put(cid, sc);
		}
		
		public synchronized List<SBean.DBSocialComment> syncPageComment(int tag, int pageNo, int len)
		{
			switch (tag)
			{
			case GameData.SOCIAL_COMMENT_TAG_LAST:
				return latestComments.getReversePageItems(pageNo, len);
			case GameData.SOCIAL_COMMENT_TAG_LIKE:
				return likeRank.getPageComment(pageNo, len);
			case GameData.SOCIAL_COMMENT_TAG_DISLIKE:
				return dislikeRank.getPageComment(pageNo, len);
			default:
				break;
			}
			
			return GameData.emptyList();
		}
		
		public synchronized long likeComment(SocialUser user, int commentID)
		{
			SBean.DBSocialComment sc = allComments.get(commentID);
			if(sc == null)
				return GameData.PROTOCOL_OP_SOCIAL_COMMENT_NOT_EXIT;
			
			if(!user.likeCommont())
				return GameData.PROTOCOL_OP_SOCIAL_COMMENT_NO_TIMES;
			
			sc.liked++;
			likeRank.tryUpdateRank(sc.kdClone(), THEME_COMMENT_LIKE_COUNT);
			dislikeRank.updateRankItem(sc);
			return sc.ownerId;
		}
		
		public synchronized long dislikeComment(SocialUser user, int commentID)
		{
			SBean.DBSocialComment sc = allComments.get(commentID);
			if(sc == null)
				return GameData.PROTOCOL_OP_SOCIAL_COMMENT_NOT_EXIT;
			
			if(!user.dislikeComment())
				return GameData.PROTOCOL_OP_SOCIAL_COMMENT_NO_TIMES;
			
			sc.disliked++;
			dislikeRank.tryUpdateRank(sc.kdClone(), THEME_COMMENT_DISLIKE_COUNT);
			likeRank.updateRankItem(sc);
			return sc.ownerId;
		}
	}
}
