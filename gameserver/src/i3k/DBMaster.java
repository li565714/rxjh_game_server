
package i3k;

import i3k.SBean.DBMasterApply;
import i3k.SBean.DBMasterNotice;
import i3k.SBean.DBMasterGraduateReq;
import i3k.SBean.DBMasterTask;
import i3k.gs.GameData;

import java.util.ArrayList;
import java.util.List;

import ket.util.Stream;

public class DBMaster implements Stream.IStreamable, Stream.KCloneable<DBMaster>
{
	private static final long serialVersionUID = 1L;	
	private static int VERSION_NOW = 1;
	
	public DBMaster() { }

	public DBMaster(int master, List<Integer> apprentices, int point, int historyPoint, int reputation, 
            int lastDismissTime, int lastBetrayTime, int lastGraduateRequestTime, List<DBMasterTask> tasks, 
            List<DBMasterApply> applyList, List<DBMasterNotice> betrayList, List<DBMasterGraduateReq> graduateReqList, int padding1, 
            int padding2, int padding3, int padding4, int padding5)
	{
		this.master = master;
		this.apprentices = apprentices;
		this.point = point;
		this.historyPoint = historyPoint;
		this.reputation = reputation;
		this.lastDismissTime = lastDismissTime;
		this.lastBetrayTime = lastBetrayTime;
		this.lastGraduateRequestTime = lastGraduateRequestTime;
		this.tasks = tasks;
		this.applyList = applyList;
		this.betrayList = betrayList;
		this.graduateReqList = graduateReqList;
		this.padding1 = padding1;
		this.padding2 = padding2;
		this.padding3 = padding3;
		this.padding4 = padding4;
		this.padding5 = padding5;
	}
	
	public DBMaster makeNew()
	{
		this.master = 0;
		this.apprentices = new ArrayList<>();
		this.point = 0;
		this.historyPoint = 0;
		this.reputation = 0;
		this.lastDismissTime = 0;
		this.lastBetrayTime = 0;
		this.lastGraduateRequestTime = 0;
		this.tasks = new ArrayList<>();
		this.applyList = new ArrayList<>();
		this.betrayList = new ArrayList<>();
		this.graduateReqList = new ArrayList<>();
		return this;
	}

	public DBMaster ksClone()
	{
		return new DBMaster(master, apprentices, point, historyPoint, reputation, 
		                    lastDismissTime, lastBetrayTime, lastGraduateRequestTime, tasks, 
		                    applyList, betrayList, graduateReqList, padding1, 
		                    padding2, padding3, padding4, padding5);
	}

	@Override
	public DBMaster kdClone()
	{
		DBMaster _kio_clobj = ksClone();
		_kio_clobj.apprentices = new ArrayList<Integer>(apprentices);
		_kio_clobj.tasks = Stream.clone(tasks);
		_kio_clobj.applyList = Stream.clone(applyList);
		_kio_clobj.betrayList = Stream.clone(betrayList);
		_kio_clobj.graduateReqList = Stream.clone(graduateReqList);
		return _kio_clobj;
	}

	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		is.popInteger();
		
		master = is.popInteger();
		apprentices = is.popIntegerList();
		point = is.popInteger();
		historyPoint = is.popInteger();
		reputation = is.popInteger();
		lastDismissTime = is.popInteger();
		lastBetrayTime = is.popInteger();
		lastGraduateRequestTime = is.popInteger();
		tasks = is.popList(DBMasterTask.class);
		applyList = is.popList(DBMasterApply.class);
		betrayList = is.popList(DBMasterNotice.class);
		graduateReqList = is.popList(DBMasterGraduateReq.class);
		padding1 = is.popInteger();
		padding2 = is.popInteger();
		padding3 = is.popInteger();
		padding4 = is.popInteger();
		padding5 = is.popInteger();
	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(VERSION_NOW);
		
		os.pushInteger(master);
		os.pushIntegerList(apprentices);
		os.pushInteger(point);
		os.pushInteger(historyPoint);
		os.pushInteger(reputation);
		os.pushInteger(lastDismissTime);
		os.pushInteger(lastBetrayTime);
		os.pushInteger(lastGraduateRequestTime);
		os.pushList(tasks);
		os.pushList(applyList);
		os.pushList(betrayList);
		os.pushList(graduateReqList);
		os.pushInteger(padding1);
		os.pushInteger(padding2);
		os.pushInteger(padding3);
		os.pushInteger(padding4);
		os.pushInteger(padding5);
	}
	
	public int master;
	public List<Integer> apprentices;
	public int point;
	public int historyPoint;
	public int reputation;
	public int lastDismissTime;
	public int lastBetrayTime;
	public int lastGraduateRequestTime;
	public List<DBMasterTask> tasks;
	public List<DBMasterApply> applyList;
	public List<DBMasterNotice> betrayList;
	public List<DBMasterGraduateReq> graduateReqList;
	public int padding1;
	public int padding2;
	public int padding3;
	public int padding4;
	public int padding5;
}
