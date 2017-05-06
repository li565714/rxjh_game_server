package i3k.fight;

import java.util.ArrayList;

import i3k.SBean;
import i3k.gs.GameData;
import i3k.gs.RankData;

public class GlobalRankData
{
	interface RankDataSyncer
	{
		void doSync(int gsid, GlobalRoleRankData roleRankData);
	}
	public static class GlobalRoleRankData extends RankData.RoleRankDataBase
	{
		int gsid;
		RankDataSyncer syncer;
		public GlobalRoleRankData(int id, int gsid, RankDataSyncer syncer)
		{
			super(id);
			this.gsid = gsid;
			this.syncer = syncer;
		}
		
		protected void doSync()
		{
			syncer.doSync(gsid, this);
		}
		
		public boolean isCheckRefreshOnTimer()
		{
			if(super.isCheckRefreshOnTimer())
				return false;
			
			return true;
		}
	}
	
}
