
package i3k.gs;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

import ket.util.Stream;
import i3k.SBean;

public class ToolData
{
	public static ToolData loadToolData(String filePath)
	{
		try
		{
			SBean.GameDataCFGT gamedata = new SBean.GameDataCFGT();
			if( Stream.loadObjLE(gamedata, new File(filePath)) )
				return new ToolData(gamedata);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public ToolData(SBean.GameDataCFGT cfg) throws Exception
	{
		this.cfg = cfg;
//		for(SBean.GeneralName e : cfg.generals)
//			generals.put(e.id, e.name);
//		for(SBean.GeneralName e : cfg.equips)
//			equips.put(e.id, e.name);
//		for(SBean.GeneralName e : cfg.items)
//			items.put(e.id, e.name);
		for(Map.Entry<Integer, String> entry : cfg.equip.entrySet())
		{
			equips.put(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<Integer, String> entry : cfg.item.entrySet())
		{
			items.put(entry.getKey(), entry.getValue());
		}
	}
	
	public String getGeneralName(short gid)
	{
		return generals.get(gid);
	}
	
	public Iterable<Map.Entry<Short, String>> getAllGenerals()
	{
		return generals.entrySet();
	}
	
	public String getEquipName(short gid)
	{
		return equips.get(gid);
	}
	
	public Iterable<Map.Entry<Integer, String>> getAllEquips()
	{
		return equips.entrySet();
	}
	
	public String getItemName(short gid)
	{
		return items.get(gid);
	}
	
	public Iterable<Map.Entry<Integer, String>> getAllItems()
	{
		return items.entrySet();
	}
	
	public String getPetName(short pid)
	{
		return pets.get(pid);
	}
	
	public Iterable<Map.Entry<Short, String>> getAllPets()
	{
		return pets.entrySet();
	}
	
	public static void main(String[] args)
	{
		ToolData td = loadToolData("tool_cfg.dat");
	}
	
	Map<Short, String> generals = new HashMap<Short, String>();
	Map<Integer, String> equips = new HashMap<Integer, String>();
	Map<Integer, String> items = new HashMap<Integer, String>();
	Map<Short, String> pets = new HashMap<Short, String>();
	SBean.GameDataCFGT cfg;
}

