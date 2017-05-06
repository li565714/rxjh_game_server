package i3k.gs;

import i3k.SBean;
import i3k.util.GameTime;

import java.util.HashMap;
import java.util.Map;

import ket.kdb.Table;
import ket.kdb.Transaction;
import ket.kdb.Transaction.AutoInit;
import ket.kdb.Transaction.ErrorCode;
import ket.util.Stream;

//≈¿À˛œ‡πÿ
public class ClimbTowerManager
{
	private static final int CLIMB_TOWER_SAVE_INTERVAL 		= 900;
	
	public ClimbTowerManager(GameServer gameServer)
	{
		this.gs = gameServer;
	}
	
	public class SaveTrans implements Transaction
	{
		public SaveTrans(SBean.DBClimbTowerServerRecordDataCfg datas)
		{
			this.serverDatas = datas;
		}
		
		@Override
		public boolean doTransaction()
		{
			byte[] key = Stream.encodeStringLE("climbTower");
			byte[] data = Stream.encodeLE(serverDatas);
			world.put(key, data);
			return true;
		}

		@Override
		public void onCallback(ErrorCode arg0)
		{
			// TODO Auto-generated method stub
			
		}
		@AutoInit
		public Table<byte[], byte[]> world;
		
		private final  SBean.DBClimbTowerServerRecordDataCfg serverDatas;
	}
	
	public void init(Map<Integer, SBean.DBClimbTowerRecordDataCfg> serverCfg)
	{
		if (serverCfg == null)
		{
			climbTowerData = new HashMap<>();
			return;
		}
		climbTowerData = serverCfg;
	}
	
	public synchronized void updateClimbTowerRecord(int groupId, SBean.DBClimbTowerRecordDataCfg dataCfg)
	{
		SBean.DBClimbTowerRecordDataCfg oldData = this.climbTowerData.get(groupId);
		if (oldData == null || oldData.floor < dataCfg.floor)
			this.climbTowerData.put(groupId, dataCfg);
	}
	
	public void save()
	{
		SBean.DBClimbTowerServerRecordDataCfg cfg = new SBean.DBClimbTowerServerRecordDataCfg();
		synchronized (this) 
		{
			cfg.datas = Stream.clone(this.climbTowerData);
		}
		gs.getDB().execute(new SaveTrans(cfg));
		this.saveTime = GameTime.getTime();
	}
	
	public void onTimer(int timeTick)
	{
		if (this.saveTime + CLIMB_TOWER_SAVE_INTERVAL < timeTick)
			this.save();
		
	}
	
	private final GameServer gs;
	
	int saveTime;
	Map<Integer, SBean.DBClimbTowerRecordDataCfg> climbTowerData = new HashMap<>();
}