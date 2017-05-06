
package i3k;

import java.util.Map;
import java.util.HashMap;

import ket.util.Stream;

public class DBMarriageShare implements Stream.IStreamable
{

	public DBMarriageShare() { }

	public DBMarriageShare(int id, int cellSize, int marriageType, int marriageExp, int marriageLevel, Map<Integer, SBean.MarriageSkillInfo> marriageSkill, int marriageTime, int manId, int ladyId, SBean.DBMarriageTask task, int lastDayRefresh, int marriageStep)
	{
		this.id = id;
		this.warehouse = new SBean.DBItemCells(cellSize, 0, new HashMap<>());
		this.marriageType = marriageType;
		this.marriageExp = marriageExp;
		this.marriageLevel = marriageLevel;
		this.marriageSkill = marriageSkill;
		this.marriageTime = marriageTime;
		this.manId = manId;
		this.ladyId = ladyId;
		this.task = task;
		this.lastDayRefresh = lastDayRefresh;
		this.marriageStep = marriageStep;
	}
	
	public DBMarriageShare(int id, SBean.DBItemCells cells, int marriageType, int marriageExp, int marriageLevel, Map<Integer, SBean.MarriageSkillInfo> marriageSkill, int marriageTime, int manId, int ladyId, SBean.DBMarriageTask task, int lastDayRefresh, int marriageStep)
	{
		this.id = id;
		this.warehouse = cells;
		this.marriageType = marriageType;
		this.marriageExp = marriageExp;
		this.marriageLevel = marriageLevel;
		this.marriageSkill = marriageSkill;
		this.marriageTime = marriageTime;
		this.manId = manId;
		this.ladyId = ladyId;
		this.task = task;
		this.lastDayRefresh = lastDayRefresh;
		this.marriageStep = marriageStep;
	}
	

	@Override
	public void decode(Stream.AIStream is) throws Stream.EOFException, Stream.DecodeException
	{
		id = is.popInteger();
		
		if (warehouse == null)
			warehouse = new SBean.DBItemCells();
		is.pop(warehouse);
		marriageType = is.popInteger();
		marriageExp = is.popInteger();
		marriageLevel = is.popInteger();
		marriageSkill = is.popIntegerHashMap(SBean.MarriageSkillInfo.class);
		marriageTime = is.popInteger();
		manId = is.popInteger();
		ladyId = is.popInteger();
		if (task == null)
			task = new SBean.DBMarriageTask();
		is.pop(task);
		lastDayRefresh = is.popInteger(); 
		marriageStep = is.popInteger(); 
				
		padding1 = is.popInteger();

	}

	@Override
	public void encode(Stream.AOStream os)
	{
		os.pushInteger(id);
		
		os.push(warehouse);
		os.pushInteger(marriageType);
		os.pushInteger(marriageExp);
		os.pushInteger(marriageLevel);
		os.pushIntegerMap(marriageSkill);
		os.pushInteger(marriageTime);
		os.pushInteger(manId);
		os.pushInteger(ladyId);
		os.push(task);
		os.pushInteger(lastDayRefresh);
		os.pushInteger(marriageStep);
		
		os.pushInteger(padding1);
	
	}

	public int id;
	public SBean.DBItemCells warehouse;
	public int marriageType;
	public int marriageExp;
	public int marriageLevel;
	public Map<Integer, SBean.MarriageSkillInfo> marriageSkill;
	public int marriageTime;
	public int manId;
	public int ladyId;
	public SBean.DBMarriageTask task;
	public int lastDayRefresh;
	public int marriageStep;
	
	public int padding1;
}