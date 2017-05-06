package i3k.gs;

import java.util.HashSet;
import java.util.Set;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.util.Stream;
import i3k.SBean;
import i3k.util.GameTime;

public class LucklyStarManager
{
	private static final int DAY_SEND_HOUR = 5;
	private static final int MINUTE_INTERVAL = 60;
	private static final int MAX_SEND_ROLE_SIZE_AT_ONCE = 50;
	
	GameServer gs;
	private int lastSendDay;
	private int sendTimes;
	private int lastChange;
	private int lastSave;
	private Set<Integer> sendRoles = new HashSet<>();

	public class LucklyStarSaveTrans implements Transaction
	{
		LucklyStarSaveTrans(SBean.DBLucklyStarManagerData dbStele)
		{
			this.dbLuckStar = dbStele;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("lucklystar");
			byte[] data = Stream.encodeLE(dbLuckStar);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if(errcode != ErrorCode.eOK )
			{
				gs.getLogger().warn("stele save failed");
			}
		}
		
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private final SBean.DBLucklyStarManagerData dbLuckStar;
	}
	
	LucklyStarManager(GameServer gs)
	{
		this.gs = gs;
	}
	
	public void init(SBean.DBLucklyStarManagerData dbLucklyStar)
	{
		if(dbLucklyStar == null)
		{
			return;
		}
		
		this.lastSendDay = dbLucklyStar.lastSendDay;
	}
	
	public void save(int timeTick)
	{
		gs.getDB().execute(new LucklyStarSaveTrans(new SBean.DBLucklyStarManagerData(lastSendDay, sendTimes)));
		this.lastSave = timeTick;
	}
	
	public void onTimer(int timeTick)
	{
		int today = GameTime.getDayByOffset(DAY_SEND_HOUR * 3600);
		if (lastSendDay != today)
		{
			synchronized (this)
			{
				lastSendDay = today;
				sendTimes = 0;
				sendRoles.clear();
				lastChange = timeTick;
			}
			save(timeTick);
		}
		if (timeTick % MINUTE_INTERVAL == 43)
			sendStars();
		if (lastChange > lastSave)
			save(timeTick);
	}
	
	public synchronized void sendStars()
	{
		SBean.LucklyStarCFGS cfg = GameData.getInstance().getLucklyStarCFGS();
		if (this.sendTimes >= cfg.startRole)
			return;
		int now = GameTime.getTime();
		Set<Integer> roleIds = gs.getLoginManager().getLucklyStarRoles(sendRoles, Math.min(MAX_SEND_ROLE_SIZE_AT_ONCE, cfg.startRole - this.sendTimes));
		gs.getLogger().debug("try send lucklystar to " + roleIds);
		for (int roleId : roleIds)
		{
			gs.getLoginManager().exeCommonRoleVisitor(roleId, true, new LoginManager.CommonRoleVisitor()
			{
				@Override
				public boolean visit(Role role, Role sameUserRole)
				{
					if (role.level < cfg.levelNeed)
						return false;
					if (role.lucklyStar.dayRecvTimes > 0)
						return false;
					role.lucklyStar.dayRecvTimes = 1;
					role.lucklyStar.needNotice = 1;
					role.lucklyStar.lastGiftTimes = cfg.starSendTimes;
					role.lucklyStar.sendTime = GameData.getDayByRefreshTimeOffset(now);
					return true;
				}

				@Override
				public void onCallback(boolean success)
				{
					if (success)
					{
						LucklyStarManager.this.sendTimes++;
						LucklyStarManager.this.lastChange = now;
						if (LucklyStarManager.this.sendTimes >= cfg.startRole)
							gs.getLoginManager().roleAddRollNotice(GameData.ROLLNOTICE_TYPE_LUCKLYSTAR_FULL, "" + cfg.startRole);
					}
					gs.getLogger().debug("send lucklystar to " + roleId + " result : " + (success ? "success" : "failed") + ", cur sendtime : " + LucklyStarManager.this.sendTimes);
				}
			});
		}
	}
}
