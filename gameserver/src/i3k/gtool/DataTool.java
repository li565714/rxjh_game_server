package i3k.gtool;

import i3k.SBean;
import i3k.SBean.DummyGoods;
import i3k.SBean.GameDataCFGS;
import i3k.SBean.SurveyQuestionCFGS;
import i3k.SBean.Vector3;
import i3k.SBean.Vector3F;
import i3k.gmap.BaseRole;
import i3k.gmap.Behavior;
import i3k.gmap.Skill;
import i3k.gs.BossManager;
import i3k.gs.GameData;
import i3k.gs.SteleManager;
import i3k.util.GVector3;
import i3k.util.GameTime;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.HashSet;









import ket.moi.ExcelReader;
import ket.moi.ExcelSheet;
import ket.util.ArgsMap;
import ket.util.Stream;

public class DataTool
{

	static class ExcelReadContext
	{
		String srcDir;
		String curExcelFileNmae;
		int curSheetIndex;

		ExcelReader reader;
		ExcelSheet sheet;

		public ExcelReadContext(String srcDir)
		{
			this.srcDir = srcDir;
		}

		public void ReadNextFile(String fileName)
		{
			curExcelFileNmae = fileName;
			reader = ket.moi.Factory.newExcelReader(srcDir + "/" + fileName + ".xlsx");
			System.out.println("load table " + fileName + " ......");
		}

		public void ReadSheet(int index) throws Exception
		{
			sheet = reader.getSheet(index);
			System.out.println("read sheet " + index + " ...");
		}

		public boolean isNotEmpty(int r, int c)
		{
			return sheet.isNotEmpty(r, c);
		}

		public String getStringValue(int r, int c) throws Exception
		{
			return sheet.getStringValue(r, c);
		}

		public byte getByteValue(int r, int c) throws Exception
		{
			return sheet.getByteValue(r, c);
		}

		public short getShortValue(int r, int c) throws Exception
		{
			return sheet.getShortValue(r, c);
		}

		public int getIntValue(int r, int c) throws Exception
		{
			if(!sheet.isNotEmpty(r, c))
				return 0;
			return sheet.getIntValue(r, c);
		}

		public long getLongValue(int r, int c) throws Exception
		{
			return sheet.getLongValue(r, c);
		}

		public float getFloatValue(int r, int c) throws Exception
		{
			return sheet.getFloatValue(r, c);
		}

		public double getDoubleValue(int r, int c) throws Exception
		{
			return sheet.getDoubleValue(r, c);
		}

		public List<Integer> getIntegerList(int r, int c, String delimiter, boolean zeroValid) throws Exception
		{
			String strVal = sheet.getStringValue(r, c);
			try
			{
				List<Integer> lst = new ArrayList<>();
				if (!strVal.isEmpty())
				{
					String[] strVals = strVal.split(delimiter);
					for (String s : strVals)
					{
						// int v = (int)Float.parseFloat(s);
						int v = (int) Double.parseDouble(s);
						if(zeroValid || v > 0)
							lst.add(v);
					}
				}
				return lst;
			}
			catch (Exception ex)
			{
				throw new Exception("error value of " + strVal + ", row=" + (r + 1) + ", col=" + (c + 1), ex);
			}
		}
		
		public List<Integer> getIntegerList(int r, int c, String delimiter) throws Exception
		{
			return getIntegerList(r, c, delimiter, true);
		}

		public List<Byte> getByteList(int r, int c, String delimiter) throws Exception
		{
			String strVal = sheet.getStringValue(r, c);
			try
			{
				List<Byte> lst = new ArrayList<>();
				if (!strVal.isEmpty())
				{
					String[] strVals = strVal.split(delimiter);
					for (String s : strVals)
					{
						// int v = (int)Float.parseFloat(s);
						byte v = (byte) Double.parseDouble(s);
						lst.add(v);
					}
				}
				return lst;
			}
			catch (Exception ex)
			{
				throw new Exception("error value of " + strVal + ", row=" + (r + 1) + ", col=" + (c + 1), ex);
			}
		}
		
		public List<Float> getFloatList(int r, int c, String delimiter) throws Exception
		{
			String strVal = sheet.getStringValue(r, c);
			try
			{
				List<Float> lst = new ArrayList<>();
				if (!strVal.isEmpty())
				{
					String[] strVals = strVal.split(delimiter);
					for (String s : strVals)
					{
						float v = (float) Double.parseDouble(s);
						lst.add(v);
					}
				}
				return lst;
			}
			catch (Exception ex)
			{
				throw new Exception("error value of " + strVal + ", row=" + (r + 1) + ", col=" + (c + 1), ex);
			}
		}

		public List<String> getStringList(int r, int c, String delimiter) throws Exception
		{
			String strVal = sheet.getStringValue(r, c);
			try
			{
				if (!strVal.isEmpty())
				{
					String[] strVals = strVal.split(delimiter);
					return Arrays.asList(strVals);
				}
				return GameData.emptyList();
			}
			catch (Exception ex)
			{
				throw new Exception("error value of " + strVal + ", row=" + (r + 1) + ", col=" + (c + 1), ex);
			}
		}

		public SBean.Vector3F getVector3F(int r, int c, String delimiter) throws Exception
		{
			String strVal = sheet.getStringValue(r, c);
			try
			{
				SBean.Vector3F v = new SBean.Vector3F();
				if (!strVal.isEmpty())
				{
					String[] strVals = strVal.split(delimiter);
					v.x = (float)Math.cos(2 * Math.PI - Float.parseFloat(strVals[1])/180.0 * Math.PI);
					v.y = 0.0f;
					v.z = (float)Math.sin(2 * Math.PI - Float.parseFloat(strVals[1])/180.0 * Math.PI);
					
//					v.x = (float) (Float.parseFloat(strVals[0])/180.0 * Math.PI);
//					v.y = (float) (Float.parseFloat(strVals[1])/180.0 * Math.PI);
//					v.z = (float) (Float.parseFloat(strVals[2])/180.0 * Math.PI);
				}
				return v;
			}
			catch (Exception ex)
			{
				throw new Exception("error value of " + strVal + ", row=" + (r + 1) + ", col=" + (c + 1), ex);
			}
		}

		public SBean.Vector3 getVector3(int r, int c, String delimiter) throws Exception
		{
			String strVal = sheet.getStringValue(r, c);
			try
			{
				SBean.Vector3 v = new SBean.Vector3();
				if (!strVal.isEmpty())
				{
					String[] strVals = strVal.split(delimiter);
					v.x = (int) (Float.parseFloat(strVals[0]) * 100.0f);
					v.y = (int) (Float.parseFloat(strVals[1]) * 100.0f);
					v.z = (int) (Float.parseFloat(strVals[2]) * 100.0f);
				}
				return v;
			}
			catch (Exception ex)
			{
				throw new Exception("error value of " + strVal + ", row=" + (r + 1) + ", col=" + (c + 1), ex);
			}
		}

		public int locateColumnTag(int col, String tagName) throws Exception
		{
			final int maxBlank = 1024;
			int nBlank = 0;
			int row = 0;
			while (true)
			{
				if (sheet.isNotEmpty(row, col))
				{
					nBlank = 0;
					String str = sheet.getStringValue(row, col);
					if (tagName.equals(str))
						return row;
				}
				else
				{
					if (++nBlank >= maxBlank)
						break;
				}
				++row;
			}
			throw new Exception("寻找标签 " + tagName + " 失败！");
		}

	}

	DataTool(String srcDir)
	{
		excelReadContext = new ExcelReadContext(srcDir);
	}

	static class QEntry implements Comparable<QEntry>
	{
		public QEntry(short id, float p)
		{
			this.id = id;
			this.p = p;
		}

		short id;
		float p;

		@Override
		public int compareTo(QEntry oe)
		{
			if (p > oe.p)
				return -1;
			if (p < oe.p)
				return 1;
			return 0;
		}
	}

	void checkModelIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !this.modelIds.contains(id))
			throw new Exception("row " + row + ", 模型ID " + id + " invalid!");
	}
	
	void checkDialogIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !this.dialogIds.contains(id))
			throw new Exception("row " + row + ", 对话 ID " + id + " invalid!");
	}
	
	void checkAnimationIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(id > 0 && !this.animation.contains(id))
			throw new Exception("row " + row + " 剧情动画表 " + id + " invalid!");
	}
	
	void checkClassTypeValid(SBean.GameDataCFGS gdCfgs, int row, int classType) throws Exception
	{
		if(classType < 1 || classType > 5)
			throw new Exception("row " + row + ", 职业类型 " + classType + " invalid!");
	}
	
	void checkPropertyIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (!gdCfgs.properties.containsKey(id))
			throw new Exception("row " + row + ", 属性 ID " + id + " invalid!");
	}
	
	void checkStateIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (!gdCfgs.states.containsKey(id))
			throw new Exception("row " + row + ", 状态ID " + id + " invalid!");
	}
	
	void checkAiTrigerIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (!gdCfgs.aitrigers.containsKey(id))
			throw new Exception("row " + row + ", aitriger ID " + id + " invalid!");
	}
	
	void checkBuffIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (!gdCfgs.buffs.containsKey(id))
			throw new Exception("row " + row + ", Buff ID " + id + " invalid!");
	}
	
	void checkMapBuffIDValid(SBean.GameDataCFGS gdCfgs, int row, int mapBuffID) throws Exception
	{
		if (!gdCfgs.mapbuffs.containsKey(mapBuffID))
			throw new Exception("row " + row + ", 场景BUFFID " + mapBuffID + " 不存在");
	}
	
	void checkBuffDamageType(SBean.GameDataCFGS gdCfgs, int row, int buffID, int type) throws Exception
	{
		if(type != GameData.BUFF_DAMAGETYPE_BUFF && type != GameData.BUFF_DAMAGETYPE_DBUFF &&type != GameData.BUFF_DAMAGETYPE_UNKNOEN)
			throw new Exception("row " + row + ",BUFFID " + buffID + " 正面负面类型非法");
	}
	
	void checkFixedDropIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.fixedDropTbl.containsKey(id))
			throw new Exception("row " + row + ", 必然掉落 drop ID " + id + " invalid!");
	}

	void checkRandomDropIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.randomDropTbl.containsKey(id))
			throw new Exception("row " + row + ", 随机掉落 drop ID " + id + " invalid!");
	}
	
	void checkBuffDropIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.buffDropTbl.containsKey(id))
			throw new Exception("row " + row + ", buff掉落  ID " + id + " invalid!");
	}

	void checkNoDuplicateDropIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.noDuplicateDropTbl.containsKey(id))
			throw new Exception("row " + row + ", 副本翻牌  ID " + id + " invalid!");
	}
	
	void checkSpecialIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.skillSpecial.containsKey(id))
			throw new Exception("row " + row + ", 特化公式  ID " + id + " invalid!");
	}
	
	void checkSkillIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.skills.containsKey(id))
			throw new Exception("row " + row + ", 技能  ID " + id + " invalid!");
	}
	
	void checkSkillIDAndLevelValid(SBean.GameDataCFGS gdCfgs, int row, int id, int level) throws Exception
	{
		if(id <= 0)
			return;
		
		if(!gdCfgs.skills.containsKey(id))
			throw new Exception("row " + row + ", 技能  ID " + id + " invalid!");
		
		if(level <= 0 || level > gdCfgs.skills.get(id).lvlDatas.size())
			throw new Exception("row " + row + ", 技能  ID " + id + " 等级 " + level + " invalid!");
		
	}
	
	void checkSPiritIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.spirits.containsKey(id))
			throw new Exception("row " + row + ", 心法  ID " + id + " invalid!");
	}

	void checkSpiritEffectIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.spiritEffects.containsKey(id))
			throw new Exception("row " + row + ", 心法效果  ID " + id + " invalid!");
	}
	
	void checkEntityIDValid(SBean.GameDataCFGS gdCfgs, int row, int id, boolean nullIDValid) throws Exception
	{
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
		case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
		{
			switch (id)
			{
			case GameData.COMMON_ITEM_ID_NULL:
				if (!nullIDValid)
					throw new Exception("row " + row + ", null entity ID " + id + " invalid!");
				break;
			case GameData.COMMON_ITEM_ID_DIAMOND:
			case -GameData.COMMON_ITEM_ID_DIAMOND:
			case GameData.COMMON_ITEM_ID_COIN:
			case -GameData.COMMON_ITEM_ID_COIN:
			case GameData.COMMON_ITEM_ID_SECT_MONEY:
			case GameData.COMMON_ITEM_ID_ARENA_MONEY:
			case GameData.COMMON_ITEM_ID_SUPERARENA_MONEY:
			case GameData.COMMON_ITEM_ID_MASTER_POINT:
			case GameData.COMMON_ITEM_ID_MASTER_REPUTATION:
			case GameData.COMMON_ITEM_ID_EQUIP_ENERGY:
			case GameData.COMMON_ITEM_ID_GEM_ENERGY:
			case GameData.COMMON_ITEM_ID_BOOK_INSPIRATION:
			case GameData.COMMON_ITEM_ID_VIT:
			case GameData.COMMON_ITEM_ID_XUANTIE:
			case GameData.COMMON_ITEM_ID_YAOCAO:
			case GameData.COMMON_ITEM_ID_SPLITSP:
			case GameData.COMMON_ITEM_ID_EXPCOIN:
			case GameData.COMMON_ITEM_ID_ROB_MONEY:
			case GameData.COMMON_ITEM_ID_CREDIT:
			case GameData.COMMON_ITEM_ID_OFFLINE_FUNC_POINT:
				break;
			default:
				throw new Exception("row " + row + ", base entity ID " + id + " invalid!");
			}
		}
			break;
		case GameData.COMMON_ITEM_ID_ITEM_PLANE:
		{
			if (!checkItemIDValid(gdCfgs, id))
				throw new Exception("row " + row + ", item entity ID " + id + " invalid!");
		}
			break;
		case GameData.COMMON_ITEM_ID_GEM_PLANE:
		{
			if (!checkGemIDValid(gdCfgs, id))
				throw new Exception("row " + row + ", gem entity ID " + id + " invalid!");
		}
			break;
		case GameData.COMMON_ITEM_ID_BOOK_PLANE:
		{
			if (!checkBookIDValid(gdCfgs, id))
				throw new Exception("row " + row + ", book entity ID " + id + " invalid!");
		}
			break;
		default:
		{
			if (id > GameData.COMMON_ITEM_ID_EQUIP_MIN || id < -GameData.COMMON_ITEM_ID_EQUIP_MIN)
			{
				if (!checkEquipIDValid(gdCfgs, id))
					throw new Exception("row " + row + ", equip entity ID " + id + " invalid!");
			}
		}
			break;
		}
	}
	
	void checkConsumeIDValid(SBean.GameDataCFGS gdCfgs, int row, int id, boolean nullIDValid) throws Exception
	{
		int idPlane = GameData.getVirtualItemIDPlane(id);
		switch (idPlane)
		{
		case GameData.COMMON_ITEM_ID_RESERVED_PLANE:
			{
				switch (id)
				{
				case GameData.COMMON_ITEM_ID_NULL:
					if (!nullIDValid)
						throw new Exception("row " + row + ", null entity ID " + id + " invalid!");
					break;
				case GameData.COMMON_ITEM_ID_DIAMOND:
				case -GameData.COMMON_ITEM_ID_DIAMOND:
				case GameData.COMMON_ITEM_ID_COIN:
				case -GameData.COMMON_ITEM_ID_COIN:
					break;
				default:
					throw new Exception("row " + row + ", base entity ID " + id + " invalid!");
				}
			}
			break;
		case GameData.COMMON_ITEM_ID_ITEM_PLANE:
			{
				if (!checkItemIDValid(gdCfgs, id))
					throw new Exception("row " + row + ", item entity ID " + id + " invalid!");
			}
			break;
		default:
			throw new Exception("row " + row + ", item entity ID " + id + " invalid!");
		}
	}

	void checkEquipRefineValid(SBean.GameDataCFGS gdCfgs, int row, int groupID) throws Exception
	{
		if(!gdCfgs.equipRefines.containsKey(groupID))
			throw new Exception("row " + row + " 装备精炼组ID " + groupID + " 非法!");
	}
	
	boolean checkItemIDValid(SBean.GameDataCFGS gdCfgs, int id)
	{
		int realID = id < 0 ? -id : id;
		return gdCfgs.items.containsKey(realID);
	}

	boolean checkGemIDValid(SBean.GameDataCFGS gdCfgs, int id)
	{
		int realID = id < 0 ? -id : id;
		return gdCfgs.gems.containsKey(realID);
	}

	boolean checkBookIDValid(SBean.GameDataCFGS gdCfgs, int id)
	{
		int realID = id < 0 ? -id : id;
		return gdCfgs.books.containsKey(realID);
	}

	boolean checkEquipIDValid(SBean.GameDataCFGS gdCfgs, int id)
	{
		int realID = id < 0 ? -id : id;
		return gdCfgs.equips.containsKey(realID);
	}

	boolean checkEquipPartValid(int part)
	{
		return !(part < 1 || part > GameData.EQUIP_MAX_PARTNUM);

	}

	void checkCommonItemIDRange(int row, int id, int idPlane) throws Exception
	{
		int minID = idPlane >= 0 ? ((idPlane << 16) + 1) : 10000000;
		int maxID = idPlane >= 0 ? ((idPlane + 1) << 16) - 1 : Integer.MAX_VALUE;
		if (id < minID || id > maxID)
			throw new Exception("row " + row + ", ID " + id + " 超出范围 (" + minID + "," + maxID + ")");
	}

	void checkCommonItemIDRank(int row, int rank) throws Exception
	{
		int minRank = 1;
		int maxRank = 5;
		if (rank < minRank || rank > maxRank)
			throw new Exception("row " + row + ", rank " + rank + " 数值非法 (" + minRank + "," + maxRank + ")");
	}

	void checkBoolean(int row, byte val) throws Exception
	{
		if (val != 0 && val != 1)
			throw new Exception("row " + row + ", boolean value " + val + " 布尔值非法 ");
	}

	void checkValueType(int row, int val) throws Exception
	{
		if (val != GameData.VALUE_TYPE_FIXED && val != GameData.VALUE_TYPE_PERCENT)
			throw new Exception("row " + row + ", value type " + val + " 数值类型非法 ");
	}
	
	void checkDamageType(int row, int val) throws Exception
	{
		if (val != GameData.eSE_Buff && val != GameData.eSE_Damage && val != GameData.eSE_DBuff)
			throw new Exception("row " + row + ", value type " + val + " 伤害类型非法 ");
	}
	
	void checkShiftType(int row, int val)throws Exception
	{
		if (val != GameData.SHIFT_TYPE_ONGETDAMAGEF && val != GameData.SHIFT_TYPE_ONUSESKILL && val != GameData.SHIFT_TYPE_ONGETDAMAGER)
			throw new Exception("row " + row + ", shift type " + val + " 击退类型非法 ");
	}
	
	void checkBuffChangeType(int row, int val)throws Exception
	{
		if(val != GameData.BUFF_CHANGETYPE_ADD && val != GameData.BUFF_CHANGETYPE_REMOVE && val != GameData.BUFF_CHANGETYPE_VALUECHANGE)
			throw new Exception("row " + row + ", buff change type " + val + " buff触发条件类型非法 ");
	}
	
	void checkMonsterID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (!gdCfgs.monsters.containsKey(id))
			throw new Exception("row " + row + ", monster ID " + id + " 非法 ");
	}
	
	void checkNPCPositionID(SBean.MapClusterCFGS mapClusterCFGS, int row, int id) throws Exception
	{
		if( !mapClusterCFGS.npcs.contains(id))
		{
			throw new Exception("row" + row + ", npc position id = " + id + "NPC点  不存在这个id");
		}
	}
	
	void checkNPCID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.npcs.containsKey(id))
			throw new Exception("row " + row + ", npc ID " + id + " 非法 ");
	}
	
	void checkMineralID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.minerals.containsKey(id))
			throw new Exception("row " + row + ", 矿物 ID " + id + " 非法 ");
	}
	
	void checkTrapID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.traps.containsKey(id))
			throw new Exception("row " + row + ", 陷阱组 ID " + id + " 非法 ");
	}
	
	void checkMapID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id > 0 && !gdCfgs.mapClusters.containsKey(id))
			throw new Exception("row " + row + ", 地图 ID " + id + " 非法 ");
	}

	void checkMapPosValid(int row, SBean.MapClusterCFGS cfg, SBean.Vector3 pos, String entity) throws Exception
	{
		if(cfg != null && (pos.x <= cfg.minX ||pos.x >= cfg.maxX || pos.z <= cfg.minZ || pos.z >= cfg.maxZ))
			throw new Exception("row " + row + ", 地图 ID " + cfg.id + " 有效位置  x: " + cfg.minX + " ~ " + cfg.maxX + " z: " + cfg.minZ + " ~ " + cfg.maxZ + " " +  entity + " 位置  " + GameData.toString(pos) + " 非法");
	}
	
	void checkNormalMapCopyID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.mapcopys.containsKey(id))
			throw new Exception("row " + row + ", 副本地图 ID " + id + " 非法 ");
	}
	
	void checkMapCopyWinCondition(SBean.GameDataCFGS gdCfgs, int row, int mapID, int winCondition) throws Exception
	{
		switch (winCondition)
		{
		case GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_BOSSES:
		case GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT:
		case GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_MONSTER_COUNT:
			break;
		default:
			throw new Exception("副本 " + mapID + " 不支持的胜利类型条件  " + winCondition + " !");
		}
	}
	
	void checkWayPointIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.wayPoints.containsKey(id))
			throw new Exception("row " + row + " 传送点 " + id + " 非法");
	}
	
	void checkTaskAlterIDValid(SBean.GameDataCFGS gdCfgs, int row, int alterID) throws Exception
	{
		if(!gdCfgs.alters.containsKey(alterID))
			throw new Exception("row " + row + " 任务变身ID " + alterID + " 非法！");
	}
	
	void checkTaskCond(SBean.GameDataCFGS gdCfgs, int row, SBean.TaskCondCFGS cfg) throws Exception
	{
		switch (cfg.type)
		{
		case GameData.TASK_TYPE_KILL:
			this.checkMonsterID(gdCfgs, row, cfg.param1);
			if (cfg.param2 <= 0)
				throw new Exception("row " + row + " 杀怪任务类型中 杀怪个数不大于0!");
			break;
		case GameData.TASK_TYPE_GATHER:
			this.checkMineralID(gdCfgs, row, cfg.param1);
			if (cfg.param2 <= 0)
				throw new Exception("row " + row + " 采集任务类型中 采集个数不大于0!");
			break;
		case GameData.TASK_TYPE_USE_ITEM:
			this.checkEntityIDValid(gdCfgs, row, cfg.param1, false);
			this.checkMapID(gdCfgs, row, cfg.param2);
			if (cfg.param6 < 1000)
				throw new Exception("row " + row + " 定点使用道具任务类型中 有效半径不大于1000厘米!");
			break;
		case GameData.TASK_TYPE_LOGIN_DAYS:
			if (cfg.param1 <= 0)
				throw new Exception("row " + row + " 登录天数任务类型中 天数不大于0!");
			break;
		case GameData.TASK_TYPE_ROLE_LEVEL:
			if (cfg.param1 <= 0)
				throw new Exception("row " + row + " 角色等级任务类型中 等级不大于0!");
			break;
//		case GameData.TASK_TYPE_MAIN_NPC_TALK:
//			this.checkNPCID(gdCfgs, row, cfg.param1);
//			if (cfg.param2 < 1000)
//				throw new Exception("row " + row + " 主线NPC对话任务类型中 有效半径不大于1000厘米!");
//			break;
		case GameData.TASK_TYPE_SUBMIT_ITEM:
			this.checkEntityIDValid(gdCfgs, row, cfg.param1, false);
			if (cfg.param2 <= 0)
				throw new Exception("row " + row + " 提交道具任务类型中 道具个数不大于0!");
			
			if(cfg.param4 > 0)
			{
				checkMonsterID(gdCfgs, row, cfg.param4);
				if(cfg.param5 <= 0)
					throw new Exception("row " + row + " 提交道具任务类型中 杀怪的任务掉落ID " + cfg.param5 + " 非法");
				
				checkRandomDropIDValid(gdCfgs, row, cfg.param5);
				
				Integer dropID = this.taskDrops.put(cfg.param4, cfg.param5);
				if(dropID != null && dropID != cfg.param5)
					throw new Exception("row " + row + " 提交道具任务类型中击杀怪物 " + cfg.param4 + " 掉落ID 有多个");
			}
				
			break;
		case GameData.TASK_TYPE_FINISH_MAPCOPY:
			SBean.MapCopyCFGS mccfg = gdCfgs.mapcopys.get(cfg.param1);
			if (mccfg == null || mccfg.openType != GameData.MAPCOPY_OPEN_TYPE_PRIVATE || cfg.param2 <= 0)
				throw new Exception("row " + row + " 通关副本任务类型中 副本Id不是单人本或者通关次数不大于0!");
			break;
		case GameData.TASK_TYPE_GAIN_PET:
			if (cfg.param1 <= 0)
				throw new Exception("row " + row + " 拥有佣兵任务类型中 佣兵个数不大于0!");
			break;
		case GameData.TASK_TYPE_POWER_LEVEL:
			if (cfg.param1 <= 0)
				throw new Exception("row " + row + " 战力任务类型中 战力值不大于0!");
			break;
		case GameData.TASK_TYPE_TRANSFROM_LEVEL:
			if (cfg.param1 <= 0)
				throw new Exception("row " + row + " 转职等级任务类型中 转职等级不大于0!");
			break;
		case GameData.TASK_TYPE_NPC_TALK:
			this.checkNPCID(gdCfgs, row, cfg.param1);
			this.checkDialogIDValid(gdCfgs, row, cfg.param2);
			if (cfg.param3 < 1000)
				throw new Exception("row " + row + " 主线NPC对话任务类型中 有效半径不大于1000厘米!");
			break;
		case GameData.TASK_TYPE_GOTO_ACTIVITYGROUP:
			SBean.ActivityMapGroupCFGS acmgCfg = gdCfgs.actMapGroups.get(cfg.param1);
			if(acmgCfg == null)
				throw new Exception("row " + row + " 参与活动本任务类型中 活动本组ID " + cfg.param1 + " 非法！");
			break;
		case GameData.TASK_TYPE_GOTO_NORMALARENA:
			if(cfg.param1 <= 0)
				throw new Exception("row " + row + " 参与竞技场任务类型中参与次数" + cfg.param1 + " 不大于0!");
			break;
		case GameData.TASK_TYPE_CONVOY_NPC:
			this.checkNPCID(gdCfgs, row, cfg.param1);
			
			this.checkMapID(gdCfgs, row, cfg.param2);
			if (cfg.param6 < 1000)
				throw new Exception("row " + row + " 护送NPC任务类型中 有效半径不大于1000厘米!");
			break;
		case GameData.TASK_TYPE_CONVOY_ITEM:
			this.checkMapID(gdCfgs, row, cfg.param2);
			if (cfg.param6 < 1000)
				throw new Exception("row " + row + " 护送NPC任务类型中 有效半径不大于1000厘米!");
			break;
		case GameData.TASK_TYPE_ANSWER:
			this.checkNPCID(gdCfgs, row, cfg.param1);
			this.checkTaskQuestionIDValid(gdCfgs, row, cfg.param2);
			break;
		case GameData.TASK_TYPE_JOIN_FACTION:
			break;
		case GameData.TASK_TYPE_RAND_QUESTION:
			this.checkNPCID(gdCfgs, row, cfg.param1);
			SBean.RandQuestionCFGS rqc = this.randQuestions.get(cfg.param2);
			if(rqc == null)
				throw new Exception("row " + row + " 任务类型随机答题， 答题ID " + cfg.param2 + " 不存在！");
			
			if(rqc.questions.size() < cfg.param3)
				throw new Exception("row " + row + " 任务类型随机答题， 答题数量 " + cfg.param3 + " 非法！");
			break;
		case GameData.TASK_TYPE_ENTER_WAYPOINT:
			this.checkWayPointIDValid(gdCfgs, row, cfg.param1);
			break;
		case GameData.TASK_TYPE_ENTER_PRIVATEMAP:
			this.checkMapID(gdCfgs, row, cfg.param1);
			break;
		default:
			throw new Exception("row " + row + " 不支持的任务条件类型 " + cfg.type + " !");
		}
	}
	
	void checkWeaponRingTaskID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id <= 0 || id > gdCfgs.weaponTasks.get(1).tasks.size())
			throw new Exception("row " + row + ", 神兵环任务 ID " + id + " 非法 ");
	}
	
	void checkWeaponValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(gdCfgs.weapons.containsKey(id))
			throw new Exception("row " + row + ", 神兵 " + id + " 非法 ");
	}
	
	void checkPetTaskID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id <= 0 || id > gdCfgs.petTasks.size())
			throw new Exception("row " + row + ", 佣兵任务 ID " + id + " 非法 ");
	}
	
	void checkPositionType(int row, int type) throws Exception
	{
		if(type != GameData.POSITION_TYPE_TARGET && type != GameData.POSITION_TYPE_OWNER)
			throw new Exception("row " + row + " 初始位置类型 " + type + " 非法 ");
	}
	
	void checkTargetType(int row, int type) throws Exception
	{
		if(type != GameData.TARGET_TYPE_TARGET && type != GameData.TARGET_TYPE_OWNER)
			throw new Exception("row " + row + " 目标类型 " + type + " 非法 ");
	}
	
	void checkHorseIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.horses.containsKey(id))
			throw new Exception("row " + row + " 坐骑ID " + id + " 非法 ");
	}
	
	int checkHorseAttrID(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		SBean.HorseEnHanceCFGS cfg = gdCfgs.horseEnHances.get(id);
		if(cfg == null)
			throw new Exception("row " + row + " 坐骑洗练ID " + id + " 非法 ");
		
		return cfg.datas.get(0).propID;
	}
	
	void checkHorseShowIDValid(SBean.GameDataCFGS gdCfgs, int row, int showID) throws Exception
	{
		if(!gdCfgs.horseShows.containsKey(showID))
			throw new Exception("row " + row + " 坐骑幻化ID " + showID + " 非法 ");
	}
	
	void checkHorseSkillIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.horseSkills.containsKey(id))
			throw new Exception("row " + row + " 骑术ID " + id + " 非法 ");
	}
	
	SBean.ClueTreeCFGS checkClueTreeType(SBean.GameDataCFGS gdCfgs, int row, int type) throws Exception
	{
		SBean.ClueTreeCFGS cfg = gdCfgs.treasureBase.clueTrees.get(type);
		if(cfg == null)
			throw new Exception("row " + row + " 线索架构类型 " + type + " 非法！");
		
		return cfg;
	}
	
	void checkInfoPointIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.infoPoints.containsKey(id))
			throw new Exception("row " + row + " 情报点ID " + id + " 非法！");
	}
	
	void checkMedalIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.medals.containsKey(id))
			throw new Exception("row " + row + " 收藏品ID " + id + " 非法！");
	}
	
	SBean.TreasureMapCFGS checkTreasureMapIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		SBean.TreasureMapCFGS mapCfg = gdCfgs.treasureMaps.get(id);
		if(mapCfg == null)
			throw new Exception("row " + row + " 藏宝图ID " + id + " 非法！");
		
		return mapCfg;
	}
	
	void checkTreasurePieceIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.treasurePieces.containsKey(id))
			throw new Exception("row " + row + " 藏宝图碎片ID " + id + " 非法！");
	}
	
	void checkClanRecipeIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.prodeceRecipes.containsKey(id))
			throw new Exception("row " + row + " 配方ID " + id + " 非法！");
	}
	
	void checkTaskQuestionIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if (id <= 0 || id > gdCfgs.taskQuestionsBank.size())
			throw new Exception("row " + row + " 任务题库ID " + id + " 非法！");
	}
	
	void checkTitleIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(!gdCfgs.titles.containsKey(id))
			throw new Exception("row " + row + " 称号ID " + id + " 非法！");
	}
	
	void checkSenceSpawnPoint(SBean.GameDataCFGS gdCfgs, int row, int pointID) throws Exception
	{
		if(!gdCfgs.sceneSpawnPoint.containsKey(pointID))
			throw new Exception("row " + row + " 特殊刷怪点 " + pointID + " 非法！");
	}
	
	void checkSenceTrigIDValid(SBean.GameDataCFGS gdCfgs, int row, int id) throws Exception
	{
		if(id != 0 && !this.senceTrigIDs.contains(id))
			throw new Exception("row " + row + " 场景触发ID " + id + " 非法！");
	}
	
	public void loadDialogTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			Map<Integer, Short> headIcons = new TreeMap<>();
			final int rowStart = 1;
			final int colStart = 0;
			int row = rowStart;
			int col = colStart;
			while (excelReadContext.isNotEmpty(row, col))
			{
				int id = excelReadContext.getIntValue(row, col++);
				if (!this.dialogIds.add(id))
					throw new Exception("row " + row + " 中id 重复！");
				++row;
				col = colStart;
			}
			gdCfgs.headIcons = headIcons;
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadModelTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			final int rowStart = 2;
			final int colStart = 0;
			int row = rowStart;
			int col = colStart;
			while (excelReadContext.isNotEmpty(row, col))
			{
				int id = excelReadContext.getIntValue(row, col++);
				if (!this.modelIds.add(id))
					throw new Exception("row " + row + " 中id " + id + " 重复！");
				++row;
				col = colStart;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loadBattleTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				int colStart = 0;
				int row = 2;
				int col = 4;
				
				SBean.BattleMapCFGS cfg = new SBean.BattleMapCFGS(new HashSet<>());
				while(excelReadContext.isNotEmpty(row, 0))
				{
					int battleID = excelReadContext.getIntValue(row, colStart);
					int packet = excelReadContext.getIntValue(row++, col);
					if(battleMapPackets.put(battleID, packet) != null)
						throw new Exception("row " + row + " ID " + battleID + "重复!");
					
					cfg.allPackets.add(packet);
				}
				
				gdCfgs.battleMap = cfg;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadAnimationTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			final int rowStart = 1;
			final int colStart = 0;
			int row = rowStart;
			int col = colStart;
			
			while(excelReadContext.isNotEmpty(row, colStart))
			{
				int id = excelReadContext.getIntValue(row, colStart);
				if(!this.animation.add(id))
					throw new Exception("row " + row + "剧情动画表 ID " + id +" 重复！");
				
				++row;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loaEffectTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			final int rowStart = 2;
			final int colStart = 0;
			int row = rowStart;
			int col = colStart;
			
			while(excelReadContext.isNotEmpty(row, colStart))
			{
				int id = excelReadContext.getIntValue(row, colStart);
				if(!this.effectIDs.add(id))
					throw new Exception("row " + row + "特效ID " + id + " 重复！");
				
				++row;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loadMapCopySpawnPaths(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				int row = 2;
				int col = 1;
				while(excelReadContext.isNotEmpty(row, 0))
				{
					SBean.MapCopySpawnPath cfg = new SBean.MapCopySpawnPath(new ArrayList<>());
					int spawnID = excelReadContext.getIntValue(row, col++);
					while(excelReadContext.isNotEmpty(row, col))
					{
						cfg.points.add(excelReadContext.getVector3(row, col++, ";"));
					}

					if(allSpawnPaths.put(spawnID, cfg) != null)
						throw new Exception("row " + row + " 刷怪点ID " + spawnID + " 重复!");
					
					row++;
					col = 1;
				}
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadHeadIconTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, Short> headIcons = new TreeMap<>();
				final int rowStart = 2;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					byte gender = excelReadContext.getByteValue(row, col++);
					byte face = excelReadContext.getByteValue(row, col++);
					byte hair = excelReadContext.getByteValue(row, col++);
					short icon = excelReadContext.getShortValue(row, col++);
					if (icon <= 0)
						throw new Exception("头像表 row " + row + " icon值小于或等于0!");
					int key = GameData.getRoleHeadKey(gender, face, hair);
					if (headIcons.put(key, icon) != null)
						throw new Exception("头像表 row " + row + " 中存在性别、脸型、发型重复项！");
					++row;
					col = colStart;
				}
				gdCfgs.headIcons = headIcons;
			}
			
			excelReadContext.ReadSheet(2);
			{

				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.FriendHeadCFGS> friendHeads = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.FriendHeadCFGS friendHead = new SBean.FriendHeadCFGS();
					col = colStart;
					friendHead.headId = excelReadContext.getIntValue(row, col++);
					col++;
					friendHead.openType = excelReadContext.getIntValue(row, col++);
					friendHead.arg1 = excelReadContext.getIntValue(row, col++);
					friendHead.arg2 = excelReadContext.getIntValue(row, col++);
					
					friendHeads.put(friendHead.headId, friendHead);
					row++;
				}
				
				gdCfgs.friendHead = friendHeads;
			}
			
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadRandomNameTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int rowStart = 1;
			final int colStart = 1;
			int row = rowStart;
			int col = colStart;

			SBean.RandomNameCFGS cfg = new SBean.RandomNameCFGS();
			cfg.familyname = new ArrayList<>();
			cfg.malename = new ArrayList<>();
			cfg.femalename = new ArrayList<>();
			excelReadContext.ReadSheet(0);
			{
				row = rowStart;
				col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					cfg.familyname.add(excelReadContext.getStringValue(row++, colStart));
				}
			}

			excelReadContext.ReadSheet(1);
			{
				row = rowStart;
				col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					cfg.malename.add(excelReadContext.getStringValue(row++, colStart));
				}
			}

			excelReadContext.ReadSheet(2);
			{
				row = rowStart;
				col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					cfg.femalename.add(excelReadContext.getStringValue(row++, colStart));
				}
			}
			gdCfgs.randomNames = cfg;
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadPropertiesTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			Map<Integer, SBean.PropertyCFGS> properties = new HashMap<>();
			final int rowStart = 0;
			final int colStart = 0;
			int row = rowStart + 2;
			int col = colStart;
			while (excelReadContext.isNotEmpty(row, col))
			{
				SBean.PropertyCFGS cfg = new SBean.PropertyCFGS();
				cfg.id = excelReadContext.getIntValue(row, col++);
				cfg.desc = excelReadContext.getStringValue(row, col++);
				cfg.canNegative = excelReadContext.getByteValue(row, col++);
				checkBoolean(row, cfg.canNegative);
				col = 8;
				cfg.valueType = excelReadContext.getByteValue(row, col++);
				checkBoolean(row, cfg.valueType);
				
				col = 11;
				cfg.rolePower = excelReadContext.getDoubleValue(row, col++);
				cfg.petPower = excelReadContext.getDoubleValue(row, col++);
				
				if (properties.put(cfg.id, cfg) != null)
					throw new Exception("属性ID重复， id = " + cfg.id);

				++row;
				col = colStart;
			}
			gdCfgs.properties = properties;
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	// 状态 基础表
	public void loadStateTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.StateCFGS> states = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.StateCFGS cfg = new SBean.StateCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col = colStart + 4;
					cfg.canRebound = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.canRebound);
					

					if (states.put(cfg.id, cfg) != null)
						throw new Exception("buffID重复， id = " + cfg.id);
					row++;
					col = colStart;
				}
				gdCfgs.states = states;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	//ai触发公式
	public void loadAiTrigTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;
			int row = rowStart;
			int col = colStart;
			
			excelReadContext.ReadSheet(1);
			{
				Map<Integer, SBean.TrigEventCFGS> cfgs = new HashMap<>();
				row = rowStart;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.TrigEventCFGS cfg = new SBean.TrigEventCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					col = 2;
					cfg.eventType = excelReadContext.getIntValue(row, col++);
					switch (cfg.eventType)
					{
						case GameData.TRIG_EVENT_EDEADCOUNT:
							break;
						case GameData.TRIG_EVENT_SDEAD:
							break;
						case GameData.TRIG_EVENT_SKILLCOUNT:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							if(!checkSkillUseType(cfg.param1))
								throw new Exception("触发条件公式 " + cfg.id + " param1 " + cfg.param1 + " 非法");
							
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							cfg.param3 = excelReadContext.getIntValue(row, col++);
							if(cfg.param3 != -1)
								checkDamageType(row, cfg.param3);
							break;
						case GameData.TRIG_EVENT_SHPLOWER:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							checkValueType(row, cfg.param1);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_DMGBYCOUNT_D:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_SDMGBY_VALUE:
							break;
						case GameData.TRIG_EVENT_ENEMYARROUND:
							break;
						case GameData.TRIG_EVENT_LOSEHP:
							break;
						case GameData.TRIG_EVENT_IDLE:
							break;
						case GameData.TRIG_EVENT_DMGBYCOUNT_I:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_INTERVAL:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_DGMTOFIX:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							checkDamageType(row, cfg.param1);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_BUFFCHANGE:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							checkBuffChangeType(row, cfg.param1);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_DODGE:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_DMGTOCOUNT_D:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							cfg.param3 = excelReadContext.getIntValue(row, col++);
							checkDamageType(row, cfg.param3);
							break;
						case GameData.TRIG_EVENT_DMGBY_HP:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_DMGTO_THP:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_ANY_MISS:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_EVENT_DMGTO_STATE:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							checkDamageType(row, cfg.param1);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							cfg.param3 = excelReadContext.getIntValue(row, col++);
							checkStateIDValid(gdCfgs, row, cfg.param3);
							break;
						case GameData.TRIG_EVENT_WEAPON_MOTIVATE:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							if(cfg.param1 != GameData.WEAPON_MOTIVATE_START && cfg.param1 != GameData.WEAPON_MOTIVATE_END && cfg.param1 != GameData.WEAPON_MOTIVATE_ALL)
								throw new Exception("row " + row + " 触发条件公式 " + cfg.id + " 类型 " + cfg.eventType + " 参数1 " + cfg.param1 + " 非法");
							
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						default:
							break;
//							throw new Exception("非法的触发条件类型ID");
					}
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("触发条件公式 id " + cfg.id + " 重复");
					
					row++;
				}
				gdCfgs.trigEvents = cfgs;
			}
			
			excelReadContext.ReadSheet(2);
			{
				Map<Integer, SBean.TrigBehaviorCFGS> cfgs = new HashMap<>();
				row = rowStart;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.TrigBehaviorCFGS cfg = new SBean.TrigBehaviorCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					col = 2;
					cfg.behaviorType = excelReadContext.getIntValue(row, col++);
					switch (cfg.behaviorType)
					{
						case GameData.TRIG_BEHAVIOR_USESKILL:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							cfg.param3 = excelReadContext.getIntValue(row, col++);
							checkBoolean(row, (byte)cfg.param3);
							cfg.param4 = excelReadContext.getIntValue(row, col++);
							checkBoolean(row, (byte)cfg.param4);
							break;
						case GameData.TRIG_BEHAVIOR_USEBUFF:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_BEHAVIOR_DMGTOFIX:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							cfg.param3 = excelReadContext.getIntValue(row, col++);
							cfg.param4 = excelReadContext.getIntValue(row, col++);
							cfg.param5 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_BEHAVIOR_DMGBYFIX:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							break;
						case GameData.TRIG_BEHAVIOR_QUICKCOOL_SKILL:
							cfg.param1 = excelReadContext.getIntValue(row, col++);
							cfg.param2 = excelReadContext.getIntValue(row, col++);
							break;
						default:
							break;
					}
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("触发行为公式 id " + cfg.id + " 重复");
					
					row++;
				}
				gdCfgs.trigBehaviors = cfgs;
			}
			
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.AiTrigerCFGS> cfgs = new HashMap<>();
				row = rowStart;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.AiTrigerCFGS cfg = new SBean.AiTrigerCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.eventID = excelReadContext.getIntValue(row, col++);
					
					if(!gdCfgs.trigEvents.containsKey(cfg.eventID))
						throw new Exception("row " + row + " ai " + cfg.id + " 触发条件公式 " + cfg.eventID + " 非法!");
					
					cfg.behaviorID = excelReadContext.getIntValue(row, col++);
					if(!gdCfgs.trigBehaviors.containsKey(cfg.behaviorID))
						throw new Exception("row " + row + " ai " + cfg.id + " 触发行为公式 " + cfg.behaviorID + " 非法!");
					
					cfg.coolDown = excelReadContext.getIntValue(row, col++);
					cfg.odds = excelReadContext.getIntValue(row, col++);
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("ai触发公式 id " + cfg.id + " 重复");
					row++;
				}
				gdCfgs.aitrigers = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	// buff 基础表
	public void loadBaseBuffTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.BuffCFGS> buffs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.BuffCFGS buffCfg = new SBean.BuffCFGS();
					buffCfg.id = excelReadContext.getIntValue(row, colStart);
					col = colStart + 4;
					buffCfg.hasShowID = (byte) (excelReadContext.getIntValue(row, col++) > 0 || excelReadContext.isNotEmpty(row, col++) ? 1 : 0);
					checkBoolean(row, buffCfg.hasShowID);
					
					col = colStart + 6;
					buffCfg.owner = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, buffCfg.owner);
					buffCfg.loopTime = excelReadContext.getIntValue(row, col++);
					buffCfg.overLays = excelReadContext.getIntValue(row, col++);
					buffCfg.overLayType = excelReadContext.getIntValue(row, col++);
					buffCfg.affectType = excelReadContext.getByteValue(row, col++);
					buffCfg.affectID = excelReadContext.getIntValue(row, col++);
					switch (buffCfg.affectType)
					{
					case GameData.EBUFF_PROP:
						this.checkPropertyIDValid(gdCfgs, row, buffCfg.affectID);
						break;
					case GameData.EBUFF_STATUS:
						this.checkStateIDValid(gdCfgs, row, buffCfg.affectID);
						break;
					default:
						throw new Exception("row " + row + " 不支持的效果类型!");
					}
					buffCfg.valueType = excelReadContext.getByteValue(row, col++);
					checkValueType(row, buffCfg.valueType);
					buffCfg.affectValue = excelReadContext.getIntValue(row, col++);
					buffCfg.realmAdd = excelReadContext.getDoubleValue(row, col++);
					buffCfg.interval = excelReadContext.getIntValue(row, col);

					col += 2;
					buffCfg.child = new ArrayList<>();
					if (excelReadContext.isNotEmpty(row, col))
					{
						buffCfg.child = excelReadContext.getIntegerList(row, col, ";");
					}
					col++;
					if (excelReadContext.isNotEmpty(row, col))
					{
						buffCfg.removeOnZero = excelReadContext.getByteValue(row, col);
						checkBoolean(row, buffCfg.removeOnZero);
					}

					col = colStart + 19;
					buffCfg.triggers = new ArrayList<>();
					if (excelReadContext.isNotEmpty(row, col))
					{
						buffCfg.triggers = excelReadContext.getIntegerList(row, col, ";");
						for(int id: buffCfg.triggers)
						{
							checkAiTrigerIDValid(gdCfgs, row, id);
						}
					}
					col++;
					buffCfg.damageType = excelReadContext.getIntValue(row, col++);
					checkBuffDamageType(gdCfgs, row, buffCfg.id, buffCfg.damageType);
					buffCfg.againstPropID = excelReadContext.getIntValue(row, col++);

					col = 24;
					buffCfg.fightSpAddTime = excelReadContext.getIntValue(row, col++);
					

					if (buffs.put(buffCfg.id, buffCfg) != null)
						throw new Exception("buffID重复， id = " + buffCfg.id);
					row++;
				}
				gdCfgs.buffs = buffs;
			}
		}
		
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loadMapBuffTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;
			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.MapBuffCFGS> cfgs = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.MapBuffCFGS cfg = new SBean.MapBuffCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);

					col = colStart + 2;
					cfg.buffID = excelReadContext.getIntValue(row, col++);
					this.checkBuffIDValid(gdCfgs, row, cfg.buffID);
					cfg.rebirthTime = excelReadContext.getIntValue(row, col++);
					cfg.rebirthCnt = excelReadContext.getIntValue(row, col++);
					cfg.scopeType = excelReadContext.getIntValue(row, col++);
					cfg.radius = excelReadContext.getIntValue(row, col++);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("场景BUFF表ID " + cfg.id + " 重复");
					row++;
				}
				gdCfgs.mapbuffs = cfgs;
			}
		}
	}
	
	public void loadBaseDummyItemTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			Map<Integer, SBean.BaseDummyItemCFGS> tbl = new HashMap<>();
			final int rowStart = 0;
			final int colStart = 0;
			int row = rowStart + 2;
			int col = colStart;
			while (excelReadContext.isNotEmpty(row, col))
			{
				SBean.BaseDummyItemCFGS cfg = new SBean.BaseDummyItemCFGS();
				cfg.id = excelReadContext.getIntValue(row, col++);
				cfg.name = excelReadContext.getStringValue(row, col++);
				col += 3;
				cfg.rank = excelReadContext.getIntValue(row, col++);
				col += 5;
				cfg.quickBuyCostItem = excelReadContext.getIntValue(row, col++);
				cfg.quickBuyCostNum = excelReadContext.getIntValue(row, col++);
				cfg.quickBuyGetNum = excelReadContext.getIntValue(row, col++);
				checkCommonItemIDRank(row, cfg.rank);
				if (tbl.put(cfg.id, cfg) != null)
					throw new Exception("基础虚拟道具ID重复， id = " + cfg.id);

				++row;
				col = colStart;
			}
			gdCfgs.base = tbl;
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadEquipRefineTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				
				Map<Integer, SBean.EquipRefineGroupCFGS> cfgs = new HashMap<>();
				Map<Integer, Float> sums = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int groupID = excelReadContext.getIntValue(row, col++);
					SBean.EquipRefineGroupCFGS groupCfg = cfgs.get(groupID);
					if(groupCfg == null)
					{
						groupCfg = new SBean.EquipRefineGroupCFGS(new ArrayList<>());
						cfgs.put(groupID, groupCfg);
					}
					
					float sum = sums.getOrDefault(groupID, 0.f);
					SBean.EquipRefineCFGS cfg = new SBean.EquipRefineCFGS(0, 0, 0, 0, new ArrayList<>(), new ArrayList<>());
					groupCfg.refines.add(cfg);
					int id = excelReadContext.getIntValue(row, col++);
					if(id != groupCfg.refines.size())
						throw new Exception("row " + row + " 组ID " + groupID + " 组条目 " + id + " 不连续!");
					
					cfg.weight = excelReadContext.getIntValue(row, col++);
					sum += cfg.weight;
					sums.put(groupID, sum);
					
					cfg.propID = excelReadContext.getIntValue(row, col++);
					checkPropertyIDValid(gdCfgs, row, cfg.propID);
					cfg.minValue = excelReadContext.getIntValue(row, col++);
					cfg.maxValue = excelReadContext.getIntValue(row, col++);
					if(cfg.minValue > cfg.maxValue)
						throw new Exception("row " + row + " 属性下限 " + cfg.minValue + " 大于 属性上限 " + cfg.maxValue);
					
					cfg.levels = excelReadContext.getIntegerList(row, col++, ";");
					cfg.multiples = excelReadContext.getFloatList(row, col++, ";");
					
					if(cfg.levels.size() != cfg.multiples.size() || cfg.multiples.isEmpty())
						throw new Exception("row " + row + " 最终乘数1 数量 " + cfg.levels.size() + " 和 最终乘数2 " + cfg.multiples.size() + " 数量不一致");
					
					row++;
				}
				
				for(Map.Entry<Integer, SBean.EquipRefineGroupCFGS> e: cfgs.entrySet())
				{
					int groupID = e.getKey();
					float sum = sums.get(groupID);
					
					float oddSum = 0;
					for(SBean.EquipRefineCFGS c: e.getValue().refines)
					{
						oddSum += c.weight / sum;
						c.weight = oddSum;
					}
				}
				
				gdCfgs.equipRefines = cfgs;
			}
		}
	}
	
	public void loadItemTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.ItemCFGS> items = new TreeMap<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ItemCFGS itemcfg = new SBean.ItemCFGS();
					itemcfg.id = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRange(row, itemcfg.id, 1);
					itemcfg.name = excelReadContext.getStringValue(row, col++);
					col += 3;
					itemcfg.type = excelReadContext.getIntValue(row, col++);
					switch (itemcfg.type)
					{
						case GameData.GAME_ITEM_TYPE_DIAMOND:
						case GameData.GAME_ITEM_TYPE_COIN:
						case GameData.GAME_ITEM_TYPE_EXP:
						case GameData.GAME_ITEM_TYPE_GIFT:
						case GameData.GAME_ITEM_TYPE_RECIPEREEL:
						case GameData.GAME_ITEM_TYPE_HP:
						case GameData.GAME_ITEM_TYPE_MATERIAL:
						case GameData.GAME_ITEM_TYPE_HPPOOL:
						case GameData.GAME_ITEM_TYPE_CHEST:
						case GameData.GAME_ITEM_TYPE_TASK_EFFECT:
						case GameData.GAME_ITEM_TYPE_WEAPON_EXP:
						case GameData.GAME_ITEM_TYPE_PET_EXP:
						case GameData.GAME_ITEM_TYPE_EQUIP_ENERGY:
						case GameData.GAME_ITEM_TYPE_GEM_ENERGY:
						case GameData.GAME_ITEM_TYPE_SPIRIT_INSPIRATION:
						case GameData.GAME_ITEM_TYPE_VIT:
						case GameData.GAME_ITEM_TYPE_FASHION:
						case GameData.GAME_ITEM_TYPE_FLOWER:
						case GameData.GAME_ITEM_TYPE_EXPCOIN_POOL:
						case GameData.GAME_ITEM_TYPE_RARE_BOOK:
						case GameData.GAME_ITEM_TYPE_SPECIAL_CARD:
						case GameData.GAME_ITEM_TYPE_VIP_CARD:
						case GameData.GAME_ITEM_TYPE_TOWER_FAME:
						case GameData.GAME_ITEM_TYPE_FEAT:
						case GameData.GAME_ITEM_TYPE_SKILL:
						case GameData.GAME_ITEM_TYPE_LETTER:
						case GameData.GAME_ITEM_TYPE_PIECE:
						case GameData.GAME_ITEM_TYPE_ARMOR_EXP_ITEM:
						case GameData.GAME_ITEM_TYPE_RUNE:
						case GameData.GAME_ITEM_TYPE_EVIL_VALUE:
						case GameData.GAME_ITEM_TYPE_FIREWORK:
						case GameData.GAME_ITEM_TYPE_ENCHANT:
						case GameData.GAME_ITEM_TYPE_PROP_STRENGTH:
						case GameData.GAME_ITEM_TYPE_OFFLINE_FUNC_POINT:
						case GameData.GAME_ITEM_TYPE_TITLE_ITEM:
						case GameData.GAME_ITEM_TYPE_USKILL_ITEM:
						case GameData.GAME_ITEM_TYPE_HEAD_ITEM:
							break;
						default:
							throw new Exception("row " + row + " 不支持的道具类型 " + itemcfg.type + " !");
					}
					itemcfg.rank = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRank(row, itemcfg.rank);
					itemcfg.lvlReq = excelReadContext.getIntValue(row, col++);
					itemcfg.viplvlReq = excelReadContext.getIntValue(row, col++);
					itemcfg.saleGold = excelReadContext.getIntValue(row, col++);
					itemcfg.canTrade = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, itemcfg.canTrade);
					itemcfg.maxStack = excelReadContext.getIntValue(row, col++);
					itemcfg.composeId = excelReadContext.getIntValue(row, col++);
					itemcfg.composeCntReq = excelReadContext.getIntValue(row, col++);
					itemcfg.arg1 = excelReadContext.getIntValue(row, col++);
					itemcfg.arg2 = excelReadContext.getIntValue(row, col++);
					itemcfg.arg3 = excelReadContext.getIntValue(row, col++);
					itemcfg.arg4 = excelReadContext.getIntValue(row, col++);
					itemcfg.arg5 = excelReadContext.getIntValue(row, col++);
					
//					col = colStart + 22;
//					itemcfg.auctionPrice = excelReadContext.getIntValue(row, col++);
					col = colStart + 23;
					itemcfg.auctiontype = excelReadContext.getIntValue(row, col++);
					
					itemcfg.dayUseTimes = new ArrayList<>();
					itemcfg.dayUseTimes.addAll(excelReadContext.getIntegerList(row, col++, ";"));
					if (itemcfg.dayUseTimes.size() != 16 && itemcfg.dayUseTimes.size() != 0)
						throw new Exception("道具限制每日使用次数，如果有值，必须是16个  : " + itemcfg.dayUseTimes.size());

					col += 7;
					itemcfg.canGift = excelReadContext.getByteValue(row, col++);
					itemcfg.roleCanUseTimes = excelReadContext.getIntValue(row, col++);
					itemcfg.auctionPrice = excelReadContext.getIntValue(row, col++);
					for(int index = 1; index < itemcfg.dayUseTimes.size(); index ++)
					{
						int a = itemcfg.dayUseTimes.get(index - 1);
						int b = itemcfg.dayUseTimes.get(index);
						if(a > b)
							throw new Exception("道具限制每日使用次数，必须递增 ");
					}
					
					itemcfg.canFusion = excelReadContext.getIntValue(row, col++);
					itemcfg.fusionPoint = excelReadContext.getIntValue(row, col++);
					itemcfg.fusionHint = excelReadContext.getIntValue(row, col++);
					
					if (itemcfg.canFusion!=0 && itemcfg.fusionPoint<=0 )
					{
						throw new Exception("道具可炼化时, 炼化点必须大于0  item id " + itemcfg.id + " at row " + row );
					}
					
					if(itemcfg.canTrade == 1)
					{
						switch (itemcfg.auctiontype)
						{
							case GameData.AUCTION_ITEM_TYPE_GROW:
							case GameData.AUCTION_ITEM_TYPE_SKILL:
							case GameData.AUCTION_ITEM_TYPE_WEAPON:
							case GameData.AUCTION_ITEM_TYPE_PET:
							case GameData.AUCTION_ITEM_TYPE_CLAN:
							case GameData.AUCTION_ITEM_TYPE_FASHION:
							case GameData.AUCTION_ITEM_TYPE_DRUG:
							case GameData.AUCTION_ITEM_TYPE_OTHER:
								break;
							default:
								throw new Exception("row " + row + " 道具 " + itemcfg.id + " 不支持的寄售行分类类型 " + itemcfg.auctiontype + " !");
						}
					}
					else if(itemcfg.auctiontype > 0)
					{
						throw new Exception("row " + row + " 道具 " + itemcfg.id + " 不可交易，寄售行分类类型 " + itemcfg.auctiontype + " !");
					}
					
					if (items.put(itemcfg.id, itemcfg) != null)
						throw new Exception("道具ID重复， id = " + itemcfg.id);

					++row;
					col = colStart;
				}
				// 检查道具表引用关系
				for (SBean.ItemCFGS e : items.values())
				{
					if (e.composeId != 0)
					{
						SBean.ItemCFGS ee = items.get(e.composeId);
						if (ee == null || ee.id == e.composeId)
							throw new Exception("道具 id " + e.id + " 向上合成物品 ID " + e.composeId + " 非法!");
					}
				}
				
				gdCfgs.items = items;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadGemTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.GemCFGS> gems = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				Map<Integer, SBean.GemLevelUpCostCFGS> costs = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.GemCFGS gemcfg = new SBean.GemCFGS();
					gemcfg.id = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRange(row, gemcfg.id, 2);
					gemcfg.name = excelReadContext.getStringValue(row, col++);
					col += 3;
					gemcfg.type = excelReadContext.getIntValue(row, col++);
					gemcfg.rank = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRank(row, gemcfg.rank);
					gemcfg.level = excelReadContext.getIntValue(row, col++);
					gemcfg.saleEnergy = excelReadContext.getIntValue(row, col++);
					gemcfg.canTrade = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, gemcfg.canTrade);
					gemcfg.maxStack = excelReadContext.getIntValue(row, col++);
					gemcfg.propID = excelReadContext.getIntValue(row, col++);
					gemcfg.propVal = excelReadContext.getIntValue(row, col++);
					this.checkPropertyIDValid(gdCfgs, row, gemcfg.propID);
					gemcfg.lvlupcosts = new HashMap<>();
					int upgradeID = excelReadContext.getIntValue(row, col++);
					int upgradeEnergy = excelReadContext.getIntValue(row, col++);
					List<SBean.DummyGoods> upgradeCosts = new ArrayList<>();
					Set<Integer> checkSet = new TreeSet<>();
					for (int i = 0; i < 2; ++i)
					{
						int iid = excelReadContext.getIntValue(row, col++);
						if (iid != 0)
						{
							this.checkConsumeIDValid(gdCfgs, row, iid, false);
							int icnt = excelReadContext.getIntValue(row, col++);
							if (icnt <= 0)
								throw new Exception("宝石ID id = " + gemcfg.id + " 升级消耗道具" + iid + " 个数非法!");
							upgradeCosts.add(new SBean.DummyGoods(iid, icnt));
							checkSet.add(iid);
						}
						else
						{
							col += 1;
						}
					}
					costs.put(gemcfg.id, new SBean.GemLevelUpCostCFGS(gemcfg.id, upgradeID, upgradeEnergy, upgradeCosts));
					if (checkSet.size() != upgradeCosts.size())
						throw new Exception("宝石ID id = " + gemcfg.id + " 宝石升级消耗物品ID重复！");
					
					col = colStart + 26;
					gemcfg.auctionPrice = excelReadContext.getIntValue(row, col++);
					
					if (gems.put(gemcfg.id, gemcfg) != null)
						throw new Exception("宝石ID重复， id = " + gemcfg.id);

					++row;
					col = colStart;
				}

				//Map<Integer, Map<Integer, SBean.GemLevelUpCostCFGS>> allgemlvlupcosts = new TreeMap<Integer, Map<Integer, SBean.GemLevelUpCostCFGS>>();
				for (SBean.GemLevelUpCostCFGS lvlcost : costs.values())
				{
					SBean.GemCFGS sfcfg = gems.get(lvlcost.fromId);
					Map<Integer, SBean.GemLevelUpCostCFGS> gemlvlupcosts = new TreeMap<>();
					int energy = 0;
					Map<Integer, SBean.DummyGoods> lvlcostMap = new TreeMap<>();
					for (SBean.GemLevelUpCostCFGS lc = lvlcost; lc != null; lc = costs.get(lc.toId))
					{
						// System.out.println("gem " + lc.fromId + "  ==> " +lc.toId);
						if (lc.toId == 0)
							break;
						energy += costs.get(lc.toId).energy;
						for (SBean.DummyGoods e : costs.get(lc.toId).items)
						{
							lvlcostMap.merge(e.id, e, (ov, nv) -> new SBean.DummyGoods(e.id, ov.count + nv.count));
						}
						SBean.GemLevelUpCostCFGS newLvlcost = new SBean.GemLevelUpCostCFGS(lvlcost.fromId, lc.toId, energy, new ArrayList<SBean.DummyGoods>(lvlcostMap.values()));
						SBean.GemCFGS stcfg = gems.get(lc.toId);
						if (stcfg == null)
							throw new Exception("宝石 id " + lvlcost.fromId + " 升级后  id " + lc.toId + " 不存在!");
						if (sfcfg.type != stcfg.type)
							throw new Exception("宝石 id " + lvlcost.fromId + " 升级后  id " + lc.toId + " 升级后宝石类型不一致!");
						gemlvlupcosts.put(newLvlcost.toId, newLvlcost);
					}
					sfcfg.lvlupcosts = gemlvlupcosts;
				}
				gdCfgs.gems = gems;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loadLegendEquipTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.LegendsCFGS legend = new SBean.LegendsCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				
				float sum = 0;
				float makeSum = 0;
				List<SBean.LegendEquipRandCFGS> rands = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = 1;
					SBean.LegendEquipRandCFGS cfg = new SBean.LegendEquipRandCFGS(new ArrayList<>(), 0, 0);
					for(int i = 0; i < 3; i++)
					{
						byte has = excelReadContext.getByteValue(row, col++);
						checkBoolean(row, has);
						cfg.values.add(has);
					}
					
					sum += excelReadContext.getIntValue(row, col++);
					makeSum += excelReadContext.getIntValue(row, col++);
					cfg.weight = sum;
					cfg.makeWeight = makeSum;
					
					rands.add(cfg);
					row++;
				}
				
				if(rands.isEmpty())
					throw new Exception("传世装备随机表为空");
				
				for(SBean.LegendEquipRandCFGS e: rands)
				{
					e.weight = e.weight / sum;
					e.makeWeight = e.makeWeight / makeSum;
				}
				
				legend.rands = rands;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				
				float sum = 0;
				List<SBean.LegendOneCFGS> lst = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = 4;
					SBean.LegendOneCFGS cfg = new SBean.LegendOneCFGS(0, 0);
					sum += excelReadContext.getIntValue(row, col++);
					cfg.weight = sum;
					cfg.baseAdd = excelReadContext.getIntValue(row, col++) / 10_000.0;
					
					lst.add(cfg);
					row++;
				}
				
				if(lst.isEmpty())
					throw new Exception("传世装备1号位为空");
				
				for(SBean.LegendOneCFGS e: lst)
					e.weight = e.weight / sum;
				
				legend.legendOnes = lst;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				
				float sum = 0;
				List<SBean.LegendTwoCFGS> lst = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = 4;
					SBean.LegendTwoCFGS cfg = new SBean.LegendTwoCFGS(0, 0);
					sum += excelReadContext.getIntValue(row, col++);
					cfg.weight = sum;
					cfg.addtionAdd = excelReadContext.getIntValue(row, col++) / 10_000.0;
					
					lst.add(cfg);
					row++;
				}
				
				for(SBean.LegendTwoCFGS e: lst)
					e.weight = e.weight / sum;
				
				legend.legendTwos = lst;
			}
			
			excelReadContext.ReadSheet(3);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				
				Map<Integer, SBean.LegendThreePartCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = 1;
					int partID = excelReadContext.getIntValue(row, col++);
					if(!checkEquipPartValid(partID))
						throw new Exception("row " + row + " 传世装备 3号位 部位" + partID + " 非法");
					
					SBean.LegendThreePartCFGS part = cfgs.get(partID);
					if(part == null)
					{
						part = new SBean.LegendThreePartCFGS(new ArrayList<>());
						cfgs.put(partID, part);
					}
					
					col = 5;
					int type = excelReadContext.getIntValue(row, col++);
					SBean.LegendThreeCFGS cfg = new SBean.LegendThreeCFGS(type, new ArrayList<>());
					for(int i = 0; i < 3; i++)
						cfg.params.add(excelReadContext.getIntValue(row, col++));
					
					switch (type)
					{
					case GameData.LEGEND_EQUIP_THREE_TYPE_ADDAI:
						checkAiTrigerIDValid(gdCfgs, row, cfg.params.get(0));
						break;
					case GameData.LEGEND_EQUIP_THREE_TYPE_ADDPROP:
						checkPropertyIDValid(gdCfgs, row, cfg.params.get(0));
						checkValueType(row, cfg.params.get(1));
						break;
					case GameData.LEGEND_EQUIP_THREE_TYPE_ADDBUFF:
						checkBuffIDValid(gdCfgs, row, cfg.params.get(0));
						break;
					case GameData.LEGEND_EQUIP_THREE_TYPE_POOLHPCD:
					case GameData.LEGEND_EQUIP_THREE_TYPE_DODGECD:
						break;
					default:
						throw new Exception("row " + row + " 传世装备 3号位" + " 效果类型 " + type + "非法");
					}
					
					part.effects.add(cfg);
					row++;
				}
				
				legend.legendThreeParts = cfgs;
			}
			
			excelReadContext.ReadSheet(4);
			{
				SBean.LegendMakeCFGS makeCfg = new SBean.LegendMakeCFGS(0, new ArrayList<>(), new HashMap<>());
//				final int rowMakeCost = excelReadContext.locateColumnTag(0, "传世装备打造");
//				{
//					int row = rowMakeCost + 2;
//					int col = 1;
//					for(int i = 0; i < 3; i++)
//					{
//						int itemID = excelReadContext.getIntValue(row, col++);
//						int count = excelReadContext.getIntValue(row, col++);
//						if(itemID != 0 && count != 0)
//						{
//							checkEntityIDValid(gdCfgs, row, itemID, false);
//							makeCfg.cost.add(new SBean.DummyGoods(itemID, count));
//						}
//					}
//				}
				
				final int rollNotice = excelReadContext.locateColumnTag(0, "走马灯时间配置");
				{
					int row = rollNotice + 1;
					int col = 1;
					makeCfg.rollNoticeInterval = excelReadContext.getIntValue(row, col++);
				}
				
				final int roleMakeCost = excelReadContext.locateColumnTag(0, "装备打造消耗");
				{
					int row = roleMakeCost + 1;
					int col = 3;
					int lastLvl = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int lvl = excelReadContext.getIntValue(row, col++);
						if(lvl <= lastLvl)
							throw new Exception("row " + row + " col " + col + " 传世装备打造消耗 等级 " + lvl + " 非法!");
						
						makeCfg.lvlReqs.add(lvl);
					}
					
					row++;
					col = 1;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int rank = excelReadContext.getIntValue(row, col++);
						int partID = excelReadContext.getIntValue(row, col++);
						int key = GameData.createEquipRankPartKey(rank, partID);
						SBean.LegendMakeCostCFGS cost = new SBean.LegendMakeCostCFGS(new ArrayList<>());
						for(int i = 0; i < makeCfg.lvlReqs.size(); i++)
						{
							List<Integer> items = excelReadContext.getIntegerList(row, col++, ";");
							for(int itemID: items)
								checkEntityIDValid(gdCfgs, row, itemID, false);
							
							cost.lvls.add(new SBean.IntSet(new HashSet<>(items)));
						}
						
						if(makeCfg.makeCost.put(key, cost) != null)
							throw new Exception("row " + row + " 传世装备打造消耗 品质 " + rank + " 部位 " + partID + " 重复!");
						
						row++;
						col = 1;
					}
				}
				
				legend.make = makeCfg;
			}
			
			gdCfgs.legend = legend;
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadEquipTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.EquipCFGS> equips = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.EquipCFGS equipcfg = new SBean.EquipCFGS();
					equipcfg.id = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRange(row, equipcfg.id, -1);
					equipcfg.name = excelReadContext.getStringValue(row, col++);
					col += 3;
					equipcfg.type = excelReadContext.getIntValue(row, col++);
					if(!checkEquipPartValid(equipcfg.type))
						throw new Exception("装备ID， id = " + equipcfg.id + " 部位ID 非法");
					
					equipcfg.rank = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRank(row, equipcfg.rank);
					equipcfg.lvlReq = excelReadContext.getIntValue(row, col++);
					equipcfg.saleEnergy = excelReadContext.getIntValue(row, col++);
					equipcfg.canTrade = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, equipcfg.canTrade);
					equipcfg.classType = excelReadContext.getIntValue(row, col++);
					equipcfg.baseProp = new ArrayList<>();
					for (int i = 0; i < 4; ++i)
					{
						int type = excelReadContext.getIntValue(row, col++);
						if (type != 0)
						{
							SBean.EquipBasePropCFGS basePropcfg = new SBean.EquipBasePropCFGS();
							basePropcfg.type = type;
							basePropcfg.value = excelReadContext.getIntValue(row, col++);
							basePropcfg.growUp = excelReadContext.getIntegerList(row, col++, ";");
							basePropcfg.advEffect = excelReadContext.getByteValue(row, col++);
							checkBoolean(row, basePropcfg.advEffect);
							equipcfg.baseProp.add(basePropcfg);
						}
						else
						{
							col += 3;
						}

					}
					equipcfg.additProp = new ArrayList<>();
					for (int i = 0; i < 5; ++i)
					{
						int type = excelReadContext.getIntValue(row, col++);
						if (type != 0)
						{
							SBean.EquipAdditPropCFGS additPropcfg = new SBean.EquipAdditPropCFGS();
							additPropcfg.type = type;
							additPropcfg.arg = excelReadContext.getIntValue(row, col++);
							additPropcfg.valMin = excelReadContext.getIntValue(row, col++);
							additPropcfg.valMax = excelReadContext.getIntValue(row, col++);
							equipcfg.additProp.add(additPropcfg);
						}
						else
						{
							col += 3;
						}

					}
					col += 5;
					equipcfg.tlvlReq = excelReadContext.getIntValue(row, col++);
					equipcfg.bwTypeReq = excelReadContext.getIntValue(row, col++);
					
					col = 57;
					equipcfg.canSplit = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, equipcfg.canSplit);
					equipcfg.splitNeedSP = excelReadContext.getIntValue(row, col++);
					equipcfg.splitOutput = new ArrayList<>();
					for(int i=0; i<3; i++)
					{
						SBean.DummyGoods goods = new SBean.DummyGoods(excelReadContext.getIntValue(row, col++), excelReadContext.getIntValue(row, col++));
						if(goods.id != 0)
							equipcfg.splitOutput.add(goods);
						
						checkEntityIDValid(gdCfgs, row, goods.id, true);
					}

					col = 70;
					int refineFixCostID = excelReadContext.getIntValue(row, col++);
					int refineFixCostCount = excelReadContext.getIntValue(row, col++);
					if(refineFixCostID != 0)
						checkEntityIDValid(gdCfgs, row, refineFixCostID, false);
					equipcfg.refineFixCost = new SBean.DummyGoods(refineFixCostID, refineFixCostCount);
					
					equipcfg.refineCosts = new HashSet<>();
					List<Integer> refineCosts = excelReadContext.getIntegerList(row, col++, ";");
					for(int itemID: refineCosts)
					{
						if(!equipcfg.refineCosts.add(itemID))
							throw new Exception("row " + row + " 装备 " + equipcfg.id + " 可用精炼道具列表 " + itemID + " 重复!");
						
						if(itemID < 0)
							throw new Exception("row " + row + " 装备 " + equipcfg.id + " 可用精炼道具列表 " + itemID + " 非法!");
						
						checkEntityIDValid(gdCfgs, row, itemID, false);
					}
					equipcfg.auctionPrice = excelReadContext.getIntValue(row, col++);
					
					if (equips.put(equipcfg.id, equipcfg) != null)
						throw new Exception("装备ID重复， id = " + equipcfg.id);
					
					++row;
					col = colStart;
				}
				gdCfgs.equips = equips;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadBookTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.BookCFGS> books = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.BookCFGS cfg = new SBean.BookCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRange(row, cfg.id, 3);
					cfg.name = excelReadContext.getStringValue(row, col++);
					col += 3;
					cfg.rank = excelReadContext.getIntValue(row, col++);
					checkCommonItemIDRank(row, cfg.rank);

					cfg.canTrade = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.canTrade);

					cfg.maxStack = excelReadContext.getIntValue(row, col++);
					cfg.saleSpirit = excelReadContext.getIntValue(row, col++);
					cfg.spiritID = excelReadContext.getIntValue(row, col++);
					
					col = colStart + 15;
					cfg.auctionPrice = excelReadContext.getIntValue(row, col++);
					cfg.classType = excelReadContext.getIntValue(row, col++);
					if(cfg.classType > 0)
						checkClassTypeValid(gdCfgs, row, cfg.classType);
					
					if (books.put(cfg.id, cfg) != null)
						throw new Exception("心法书ID重复， id = " + cfg.id);
					
					++row;
					col = colStart;
				}
				gdCfgs.books = books;

			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	
	public void loadDropTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.FixedDropTableCFGS> dropcfgs = new HashMap<>();
				final int colStart = 2;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.FixedDropTableCFGS cfg = new SBean.FixedDropTableCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.drops = new ArrayList<>();
					int curRowStart = row;
					while (excelReadContext.isNotEmpty(row, col))
					{
						SBean.DropEntity dropcfg = new SBean.DropEntity();
						dropcfg.did = excelReadContext.getIntValue(row++, col);
						checkEntityIDValid(gdCfgs, row, dropcfg.did, false);
						dropcfg.minCount = excelReadContext.getIntValue(row++, col);
						dropcfg.maxCount = excelReadContext.getIntValue(row++, col);
						if (dropcfg.minCount > dropcfg.maxCount || dropcfg.minCount < 1)
							throw new Exception("drop min > max count or min count < 1 error at drop table id=" + cfg.id + ", col=" + col);
						if (GameData.isEquip(dropcfg.did) && (dropcfg.minCount != 1 || dropcfg.maxCount != 1))
							throw new Exception("装备掉落个数不为1 drop table id=" + cfg.id + ", col=" + col + ", equip id=" + dropcfg.did);
						cfg.drops.add(dropcfg);

						row = curRowStart;
						col++;
					}
					if (dropcfgs.put(cfg.id, cfg) != null)
						throw new Exception("必然掉落表ID重复， id = " + cfg.id);

					row += 3;
					col = colStart;
				}
				gdCfgs.fixedDropTbl = dropcfgs;
			}
			excelReadContext.ReadSheet(1);
			{
				Map<Integer, SBean.RandomDropTableCFGS> dropcfgs = new HashMap<>();
				final int colStart = 2;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.RandomDropTableCFGS cfg = new SBean.RandomDropTableCFGS();
					float sum = 0;
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.drops = new ArrayList<>();
					int curRowStart = row;
					while (excelReadContext.isNotEmpty(row, col))
					{
						SBean.DropEntry dropcfg = new SBean.DropEntry(new SBean.DropEntity(), 0);
						dropcfg.drop.did = excelReadContext.getIntValue(row++, col);
						checkEntityIDValid(gdCfgs, row, dropcfg.drop.did, true);
						dropcfg.drop.minCount = excelReadContext.getIntValue(row++, col);
						dropcfg.drop.maxCount = excelReadContext.getIntValue(row++, col);
						if (dropcfg.drop.minCount > dropcfg.drop.maxCount)
							throw new Exception("drop min > max count error at drop table id=" + cfg.id + ", col=" + col);
						dropcfg.probability = excelReadContext.getFloatValue(row++, col);
						if (dropcfg.probability <= 0)
							throw new Exception("row " + (row - 1) + " drop weight =" + dropcfg.probability + " is invalid!");
						sum += dropcfg.probability;
						cfg.drops.add(dropcfg);

						row = curRowStart;
						col++;
					}
					float sp = 0;
					for (SBean.DropEntry e : cfg.drops)
					{
						sp += e.probability / sum;
						e.probability = sp;
					}
					if (dropcfgs.put(cfg.id, cfg) != null)
						throw new Exception("随机掉落表ID重复， id = " + cfg.id);

					row += 4;
					col = colStart;
				}
				gdCfgs.randomDropTbl = dropcfgs;
			}
			excelReadContext.ReadSheet(2);
			{
				Map<Integer, SBean.NoDuplicateDropTableCFGS> dropcfgs = new HashMap<>();
				final int colStart = 2;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					SBean.NoDuplicateDropTableCFGS cfg = new SBean.NoDuplicateDropTableCFGS(id, 0, new ArrayList<>());
					cfg.id = id;
					int sum = 0;
					int curRowStart = row;
					while (excelReadContext.isNotEmpty(row, col))
					{
						SBean.DropItemCFGS dropCfg = new SBean.DropItemCFGS();
						dropCfg.did = excelReadContext.getIntValue(row++, col);
						checkEntityIDValid(gdCfgs, row, dropCfg.did, false);
						dropCfg.count = excelReadContext.getIntValue(row++, col);
						dropCfg.weight = excelReadContext.getIntValue(row++, col);
						cfg.drops.add(dropCfg);
						sum += dropCfg.weight;

						row = curRowStart;
						col++;
					}
					cfg.weightSum = sum;
					if (dropcfgs.put(cfg.id, cfg) != null)
						throw new Exception("副本翻牌表ID重复， id = " + cfg.id);
					row += 3;
					col = colStart;
				}
				gdCfgs.noDuplicateDropTbl = dropcfgs;
			}

			excelReadContext.ReadSheet(3);
			{
				final int colStart = 2;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				float sum = 0.0f;
				float sp = 0.0f;
				int curRowStart = row;
				Map<Integer, SBean.BuffDropTableCFGS> dropcfgs = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.BuffDropTableCFGS cfg = new SBean.BuffDropTableCFGS();
					sum = 0;
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.drops = new ArrayList<>();
					curRowStart = row;
					while (excelReadContext.isNotEmpty(row, col))
					{
						SBean.DropBuffCFGS dropCfg = new SBean.DropBuffCFGS();
						dropCfg.mapBuffID = excelReadContext.getIntValue(row++, col);
						if (dropCfg.mapBuffID > 0)
							checkMapBuffIDValid(gdCfgs, row, dropCfg.mapBuffID);
						dropCfg.probability = excelReadContext.getFloatValue(row++, col);
						sum += dropCfg.probability;
						cfg.drops.add(dropCfg);

						row = curRowStart;
						col++;
					}
					sp = 0.0f;
					for (SBean.DropBuffCFGS e : cfg.drops)
					{
						sp += e.probability / sum;
						e.probability = sp;
					}

					if (dropcfgs.put(cfg.id, cfg) != null)
						throw new Exception("BUFF掉落表ID " + cfg.id + " 重复");
					row += 2;
					col = colStart;
				}
				gdCfgs.buffDropTbl = dropcfgs;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	
	public void loadGiftTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			Map<Integer, SBean.GiftCFGS> gifts = new HashMap<>();
			final int rowStart = 0;
			final int colStart = 0;
			int row = rowStart + 2;
			int col = colStart;
			while (excelReadContext.isNotEmpty(row, col))
			{
				SBean.GiftCFGS cfg = new SBean.GiftCFGS();
				cfg.id = excelReadContext.getIntValue(row, col++);
				cfg.name = excelReadContext.getStringValue(row, col++);
				col+=1;
				cfg.gifts = new ArrayList<>();
				for (int i = 0; i < 6; ++i)
				{
					int gid = excelReadContext.getIntValue(row, col++);
					if (gid != 0)
					{
						this.checkEntityIDValid(gdCfgs, row, gid, false);
						int gcount = excelReadContext.getIntValue(row, col++);
						if (gcount <= 0)
							throw new Exception("礼包 id " + cfg.id + " 引用物品ID " + gid + " 数量 " + gcount + " 非法！");
						cfg.gifts.add(new SBean.DummyGoods(gid, gcount));
						col += 1;
					}
					else
					{
						col += 2;
					}
				}
				Set<Integer> set = new TreeSet<>();
				for (SBean.DummyGoods e : cfg.gifts)
				{
					set.add(e.id);
				}
				if (set.size() != cfg.gifts.size())
					throw new Exception("礼包表中礼包项ID重复， id = " + cfg.id);
				if (set.isEmpty())
					throw new Exception("礼包 id " + cfg.id + " 内无任何物品!");
				if (gifts.put(cfg.id, cfg) != null)
					throw new Exception("礼包表ID重复， id = " + cfg.id);

				++row;
				col = colStart;
			}
			gdCfgs.gifts = gifts;
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loadCheckInTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			List<SBean.CheckInCFGS> cfgs = new ArrayList<>();
			final int rowStart = 0;
			final int colStart = 0;
			int row = rowStart + 2;
			int col = colStart;
			int lastStartTime = 0;
			while (excelReadContext.isNotEmpty(row, col))
			{
				SBean.CheckInCFGS cfg = new SBean.CheckInCFGS();
				cfg.id = excelReadContext.getIntValue(row, col++);
				cfg.startTime = GameTime.parseSecondOfDate(excelReadContext.getStringValue(row, col++));
				if (cfg.startTime <= lastStartTime)
					throw new Exception("row=" + row + ", 签到开始时间在上一行时间前面！");
				col += 2;
				cfg.rewards = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					List<Integer> rvals = excelReadContext.getIntegerList(row, col++, ";");
					if (rvals.size() != 3)
						throw new Exception("row=" + row + ", col=" + col + " 签到奖励格式不正确！");
					this.checkEntityIDValid(gdCfgs, row, rvals.get(0), false);
					SBean.CheckInRewardCFGS rcfg = new SBean.CheckInRewardCFGS(new SBean.DummyGoods(rvals.get(0), rvals.get(1)), rvals.get(2));
					cfg.rewards.add(rcfg);
				}

				cfgs.add(cfg);
				++row;
				col = colStart;
			}
			
			gdCfgs.checkins = cfgs;
		}
		System.out.println("load table " + fileName + " success.");
	}

	// 技能基础表
	public void loadBaseSkillTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;

			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				// 技能表
				Map<Integer, SBean.SkillCFGS> skills = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart + 5;
					SBean.SkillCFGS skillCfg = new SBean.SkillCFGS();
					SBean.SkillBaseCFGS baseData = new SBean.SkillBaseCFGS(new SBean.SkillBaseCommonCFGS(), new SBean.SkillBaseFixCFGS());
					skillCfg.id = excelReadContext.getIntValue(row, colStart);
					baseData.common.id = skillCfg.id;
					baseData.fix.type = excelReadContext.getByteValue(row, col++);
					baseData.common.triType = excelReadContext.getByteValue(row, col++);
					baseData.common.maxLvl = excelReadContext.getIntValue(row, col++);
					baseData.common.maxEffectLvl = excelReadContext.getIntValue(row, col++);
					baseData.common.parentID = excelReadContext.getIntValue(row, col++);
					baseData.common.parentLvl = excelReadContext.getIntValue(row, col++);
					baseData.fix.duration = excelReadContext.getIntValue(row, col++);

					List<SBean.AttackCFGS> attackCfgs = new ArrayList<>();
					int spellTime = excelReadContext.getIntValue(row, col++);
					int chargeTime = excelReadContext.getIntValue(row, col++);
					int attackTime = baseData.fix.duration - spellTime - chargeTime;
					for (int i = 0; i < 3; i++)
					{
						SBean.AttackCFGS a = new SBean.AttackCFGS();
						a.breakByDamage = excelReadContext.getByteValue(row, col++);
						checkBoolean(row, a.breakByDamage);
						a.breakByCtrl = excelReadContext.getByteValue(row, col++);
						checkBoolean(row, a.breakByCtrl);
						a.breakByMove = excelReadContext.getByteValue(row, col++);
						checkBoolean(row, a.breakByMove);
						attackCfgs.add(a);
					}
					Iterator<SBean.AttackCFGS> ai = attackCfgs.iterator();
					baseData.common.spell = ai.next();
					baseData.common.spell.time = spellTime;
					baseData.common.charge = ai.next();
					baseData.common.charge.time = chargeTime;
					baseData.common.attack = ai.next();
					baseData.common.attack.time = attackTime;

					col = colStart + 27;
					baseData.common.specialIDs = excelReadContext.getIntegerList(row, col++, ";", false);
					List<Integer> showIDs = excelReadContext.getIntegerList(row, col++, ";");
					for(int showID: showIDs)
					{
						if(showID > 0)
						{
							baseData.common.hasShowID = 1;
							break;
						}
					}
					checkBoolean(row, baseData.common.hasShowID);
					baseData.common.canAttack = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, baseData.common.canAttack);
					baseData.common.forceBreak = excelReadContext.getByteValue(row, col);
					checkBoolean(row, baseData.common.forceBreak);

					col += 3;
					SBean.Scope scope = new SBean.Scope();
					scope.type = excelReadContext.getByteValue(row, col++);
					if(baseData.fix.type == GameData.eSE_Buff && scope.type == Skill.eSScopT_Single)
						throw new Exception("技能表 row " + row + " 祝福技能 " + skillCfg.id + " 范围类型 " + scope.type + " 非法");

					scope.args = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						if (excelReadContext.isNotEmpty(row, col))
						{
							scope.args.add(excelReadContext.getIntValue(row, col));
						}
						col++;
					}
					baseData.fix.scope = scope;
					baseData.common.fixDistance = excelReadContext.getIntValue(row, col++);
					baseData.fix.maxTargets = excelReadContext.getIntValue(row, col++);
					col = colStart + 43;
					baseData.common.priority = excelReadContext.getIntValue(row, col++);
					baseData.common.addFightSp = excelReadContext.getIntValue(row, col++);
					col = colStart + 46;
					baseData.common.children = excelReadContext.getIntegerList(row, col++, ";");
					baseData.common.followSkills = excelReadContext.getIntegerList(row, col++, ";");
					col = colStart + 48;
					baseData.common.relateSKill = excelReadContext.getIntValue(row, col++);
					skillCfg.baseData = baseData;
					
					skillCfg.lvlDatas = new ArrayList<>();
					if (skills.put(skillCfg.id, skillCfg) != null)
					{
						throw new Exception("技能表ID重复， id = " + skillCfg.id);
					}
					row++;
				}
				gdCfgs.skills = skills;
			}

			excelReadContext.ReadSheet(1);
			{
				// 技能数据表
				row = rowStart;
				List<SBean.SkillLevelCFGS> skillLevelCFGSList = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart + 3;
					SBean.SkillLevelCFGS skillDataCfg = new SBean.SkillLevelCFGS(new SBean.SkillLevelCommonCFGS(), new SBean.SkillLevelFixCFGS());
					skillDataCfg.common.costs = new TreeMap<>();

					skillDataCfg.common.id = excelReadContext.getIntValue(row, colStart);
					skillDataCfg.common.level = excelReadContext.getIntValue(row, col++);
					skillDataCfg.common.roleLvlReq = excelReadContext.getIntValue(row, col++);
					skillDataCfg.common.coinCost = excelReadContext.getIntValue(row, col++);
					skillDataCfg.common.itemIDCost = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdCfgs, row, skillDataCfg.common.itemIDCost, true);
					skillDataCfg.common.itemCntCost = excelReadContext.getIntValue(row, col++);
					if(skillDataCfg.common.level == 1 && (skillDataCfg.common.coinCost > 0 || skillDataCfg.common.itemIDCost != 0))
						throw new Exception("技能数据表 row " + row + " 技能 " + skillDataCfg.common.id + " 1级的消耗 不为0！");
					
					col = colStart + 9;
					skillDataCfg.fix.addSP = excelReadContext.getIntValue(row, col++);
					skillDataCfg.fix.cool = excelReadContext.getIntValue(row, col++);

					skillDataCfg.fix.events = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						SBean.SkillEventCFGS event = new SBean.SkillEventCFGS();
						event.triTime = excelReadContext.getIntValue(row, col);

						col += 4;
						SBean.SubDamageCFGS subDamage = new SBean.SubDamageCFGS();
						subDamage.odds = excelReadContext.getIntValue(row, col++);
						subDamage.atrType = excelReadContext.getByteValue(row, col++);
						subDamage.acrType = excelReadContext.getByteValue(row, col++);
						if (excelReadContext.isNotEmpty(row, col))
							subDamage.arg1 = excelReadContext.getFloatValue(row, col);
						col++;
						if (excelReadContext.isNotEmpty(row, col))
							subDamage.arg2 = excelReadContext.getFloatValue(row, col);
						col++;
						subDamage.realmAdd = excelReadContext.getFloatValue(row, col++);
						event.damage = subDamage;

						event.status = new ArrayList<>();
						for (int j = 0; j < 2; j++)
						{
							SBean.SubStatus subStatus = new SBean.SubStatus();
							if (excelReadContext.isNotEmpty(row, col))
								subStatus.odds = excelReadContext.getIntValue(row, col);
							col++;
							if (excelReadContext.isNotEmpty(row, col))
								subStatus.buffID = excelReadContext.getIntValue(row, col);
							col++;
							event.status.add(subStatus);
						}
						skillDataCfg.fix.events.add(event);
					}

					col = colStart + 53;

					skillDataCfg.common.skillPower = excelReadContext.getIntValue(row, col++);
					skillDataCfg.common.realmPower = excelReadContext.getIntegerList(row, col++, ";");

					SBean.SkillCFGS skillcfg = gdCfgs.skills.get(skillDataCfg.common.id);
					if (skillcfg == null)
						throw new Exception("只有技能等级数据 没有技能数据 ！ id " + skillDataCfg.common.id + ", level " + skillDataCfg.common.level);
					if (skillcfg.lvlDatas.isEmpty() && skillDataCfg.common.level != 1 || !skillcfg.lvlDatas.isEmpty() && skillcfg.lvlDatas.get(skillcfg.lvlDatas.size() - 1).common.level + 1 != skillDataCfg.common.level)
						throw new Exception("技能 " + skillDataCfg.common.id + " 等级数据在等级 " + skillDataCfg.common.level + " 不连续！");
					skillcfg.lvlDatas.add(skillDataCfg);
					skillLevelCFGSList.add(skillDataCfg);
					row++;
				}
				
				int beginIndex = 0;
				while(beginIndex != skillLevelCFGSList.size())
				{
					List<SBean.SkillLevelCFGS> totelSkillLevel = new ArrayList<>();
					SBean.SkillLevelCFGS skillLeve1 = skillLevelCFGSList.get(beginIndex);
					totelSkillLevel.add(skillLeve1);
					for(int j = beginIndex + 1; j < skillLevelCFGSList.size(); j++ )
					{
						SBean.SkillLevelCFGS skillLe = skillLevelCFGSList.get(j);
						if(skillLeve1.common.level == skillLe.common.level)
							break;
						totelSkillLevel.add(skillLe);
					}
					beginIndex += totelSkillLevel.size();
					if(totelSkillLevel.size() != 1)
					{
						
						for(int index = 0; index < totelSkillLevel.size(); index ++)
						{
							SBean.SkillLevelCFGS srcSkill = totelSkillLevel.get(index);

							for(int indexdd = index + 1; indexdd < totelSkillLevel.size(); indexdd ++)
							{
								Map<Integer, Integer> itemCost = new TreeMap<>();
								int totalCoin = 0;
								int conLevelReq= 0;
								for(int subIndex = index + 1; subIndex <= indexdd; subIndex ++)
								{
									//累加道具 
									SBean.SkillLevelCFGS ts = totelSkillLevel.get(subIndex);
									if(ts.common.itemIDCost > 0)
									{
										Integer count = itemCost.get(ts.common.itemIDCost);
										if(count != null)
											itemCost.put(ts.common.itemIDCost, count + ts.common.itemCntCost);
										else 
											itemCost.put(ts.common.itemIDCost, ts.common.itemCntCost);
									}
									totalCoin += ts.common.coinCost;
									conLevelReq = ts.common.roleLvlReq;
									
								}
								
								//System.out.println((index+1) + "升到" + (indexdd+1) + "需要物品 = " + itemCost.toString() + "coin" + totalCoin);

								SBean.SkillLevelUpLevelCostCFGS costCFGS = new SBean.SkillLevelUpLevelCostCFGS(totalCoin, itemCost, conLevelReq);
								srcSkill.common.costs.put(indexdd + 1, costCFGS);
							}
							
							//System.out.println(21);
						}
						
					}
				
				}
				
			}

			// 境界消耗
			excelReadContext.ReadSheet(2);
			{
				row = 2;
				col = 0;
				List<SBean.SkillBournCFGS> all = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SkillBournCFGS skillBournCFGS = new SBean.SkillBournCFGS();
					skillBournCFGS.lvl = excelReadContext.getIntValue(row, col++);
					String name = excelReadContext.getStringValue(row, col++);
					skillBournCFGS.items = new ArrayList<>();
					for (int index = 0; index < 2; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
							skillBournCFGS.items.add(new SBean.DummyGoods(id, count));
					}
					Set<Integer> set = new TreeSet<>();
					for (SBean.DummyGoods e : skillBournCFGS.items)
					{
						set.add(e.id);
					}
					if (set.size() != skillBournCFGS.items.size())
						throw new Exception("境界消耗表中礼包项ID重复， level = " + skillBournCFGS.lvl);
					all.add(skillBournCFGS);
					row++;
					col = 0;
				}

				gdCfgs.skillBourns = all;
			}

		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadSkillSpecial(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.SkillSpecialCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SkillSpecialCFGS cfg = new SBean.SkillSpecialCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					col = colStart + 2;
					cfg.formulaID = excelReadContext.getIntValue(row, col++);
					switch (cfg.formulaID)
					{
					case GameData.SKILL_SPECIAL_GUIDE:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++) - 1;
						if(cfg.param3 < 0 || cfg.param3 >= 3)
							throw new Exception(excelReadContext.getStringValue(row, 1) + " 特化公式ID " + cfg.id  + " 伤害事件ID: " + cfg.param3 + " 非法");
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						if(cfg.param4 != GameData.GUIDE_SKILL_BYTIME && cfg.param4 != GameData.GUIDE_SKILL_BYCOUNT)
							throw new Exception(excelReadContext.getStringValue(row, 1) + " 特化公式ID " + cfg.id  + " 类型: " + cfg.param4 + " 非法");
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						cfg.param6 = excelReadContext.getIntValue(row, col++);
						checkBoolean(row, (byte)(cfg.param6));
						break;
					case GameData.SKILL_SPECIAL_RUSH:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						if(cfg.param1 <= 0 || cfg.param1 > 3)
							throw new Exception(excelReadContext.getStringValue(row, 1) + " 特化公式ID " + cfg.id + " 类型: " + cfg.param1 + " 非法");
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						if(cfg.param4 <= 0)
							throw new Exception(excelReadContext.getStringValue(row, 1) + " 特化公式ID " + cfg.id  + " 速度: " + cfg.param4 + " 非法");
						
//						cfg.param5 = getArcLength(Math.abs(cfg.param2), Math.abs(cfg.param3));
//						System.err.println("rush distance " + cfg.param5 + " length " + cfg.param2 + " high " + cfg.param3);
						break;
					case GameData.SKILL_SPECIAL_SHIFT:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						checkShiftType(row, cfg.param1);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						if(cfg.param4 <= 0)
							throw new Exception(excelReadContext.getStringValue(row, 1) + " 特化公式ID " + cfg.id  + " 速度: " + cfg.param4 + " 非法");
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_SUMMON:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						if(cfg.param1 == GameData.SUMMON_TYPE_MONSTER)
							checkMonsterID(gdCfgs, row, cfg.param2);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						if(cfg.param1 == GameData.SUMMON_TYPE_SKILL)
							checkSkillIDValid(gdCfgs, row, cfg.param3);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						col++;
						cfg.param7 = excelReadContext.getIntValue(row, col++);
						cfg.param8 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_STARGER:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_PDAM_USESKILL:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						if(cfg.param2 == -1)
							checkSkillIDValid(gdCfgs, row,  cfg.param1);
						else
							checkSkillIDAndLevelValid(gdCfgs, row, cfg.param1, cfg.param2);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						checkPositionType(row, cfg.param3);
						break;
					case GameData.SKILL_SPECIAL_FIX_DAMVALUE:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						checkValueType(row, cfg.param1);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						cfg.param6 = excelReadContext.getIntValue(row, col++);
						cfg.param7 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_FLY_SKILL:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						if(cfg.param2 <= 0)
							throw new Exception(excelReadContext.getStringValue(row, 1) + " 特化公式ID " + cfg.id  + " 飞行速度: " + cfg.param2 + " 非法");
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						col++;
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_AURA:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						if(cfg.param3 != GameData.AURA_TARGET_OWNER && cfg.param3 != GameData.AURA_TARGET_ENEMY)
							throw new Exception("特化公式ID： 9 " + " 光环目标类型" + cfg.param3 + " 非法");
						
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						checkBuffIDValid(gdCfgs, row, cfg.param5);
						break;
					case GameData.SKILL_SPECIAL_WEAPONSP:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						checkTargetType(row, cfg.param2);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_DMGADDHP:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_OMNISLASH:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_MAXHPDMG:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SKILL_SPECIAL_EXTRA_DROP:
						cfg.param1 = excelReadContext.getIntValue(row, col++);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						break;
					default:
						throw new Exception("技能特化公式ID " + cfg.formulaID + " 非法");
					}

					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("技能特化公式ID重复， id = " + cfg.id);

					row++;
				}
				gdCfgs.skillSpecial = cfgs;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadSpiritTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			Map<Integer, SBean.SpiritCFGS> cfgs = new HashMap<>();
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;

				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SpiritCFGS cfg = new SBean.SpiritCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col += 1;
					cfg.type = excelReadContext.getByteValue(row, col++);
					col += 2;
					cfg.maxLayer = excelReadContext.getIntValue(row, col++);
					cfg.index = excelReadContext.getIntValue(row, col++);
					cfg.studyUse = excelReadContext.getIntValue(row, col++);
					cfg.growups = new TreeMap<>();

					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 重复的心法ID " + cfg.id);

					++row;
					col = colStart;
				}
			}

			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;

				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SpiritGrowUpCFGS cfg = new SBean.SpiritGrowUpCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col += 1;
					cfg.level = excelReadContext.getByteValue(row, col++);
					cfg.bookInspiration = excelReadContext.getIntValue(row, col++);
					cfg.items = new ArrayList<>(2);
					for (int index = 0; index < 1; index++)
					{

						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if (id != 0)
						{
							this.checkEntityIDValid(gdCfgs, row, id, false);
							SBean.DummyGoods good = new SBean.DummyGoods(id, count);
							cfg.items.add(good);
						}
					}

					cfg.attrs = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.value = excelReadContext.getIntValue(row, col++);
						if (attr.id > 0)
						{
							this.checkPropertyIDValid(gdCfgs, row, attr.id);
							cfg.attrs.add(attr);
						}
					}

					cfg.effectsIds = excelReadContext.getIntegerList(row, col++, ";");
					for(int effectsId: cfg.effectsIds)
						checkSpiritEffectIDValid(gdCfgs, row, effectsId);
					
					cfg.effectsType = excelReadContext.getIntValue(row, col++);
					cfg.addPower = excelReadContext.getIntValue(row, col++);

					SBean.SpiritCFGS scfg = cfgs.get(cfg.id);
					if (scfg == null)
						throw new Exception("心法消耗成长表中  row " + row + " 心法ID非法！");
					if (scfg.growups.containsKey(cfg.level))
						throw new Exception("心法消耗成长表中  row " + row + " 心法段位重复非法！");
					scfg.growups.put(cfg.level, cfg);

					++row;
					col = colStart;
				}
			}
			gdCfgs.spirits = cfgs;
		}

		System.out.println("load table " + fileName + " success.");
	}

	public void loadSpiritEffect(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.SpiritEffectCFGS> mapcfg = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart + 2;
					SBean.SpiritEffectCFGS cfg = new SBean.SpiritEffectCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					cfg.type = excelReadContext.getIntValue(row, col++);
					List<Integer> param1 = excelReadContext.getIntegerList(row, col++, ";");
					cfg.param1 = new HashSet<>(param1);
					switch (cfg.type)
					{
					case GameData.SPIRIT_EFFECT_PROP:
						for(Integer pid: cfg.param1)
							checkPropertyIDValid(gdCfgs, row, pid);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						checkValueType(row, cfg.param2);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SPIRIT_EFFECT_ADDAI:
						for(Integer aid: cfg.param1)
							checkAiTrigerIDValid(gdCfgs, row, aid);
						break;
					case GameData.SPIRIT_EFFECT_SKILLPASV:
						for(Integer sid: cfg.param1)
							checkSkillIDValid(gdCfgs, row, sid);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						if(cfg.param2 < 1 || cfg.param2 > 6 )
							throw new Exception("心法效果表 id " + cfg.id + " param2 " + cfg.param2 + " 范围非法");
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SPIRIT_EFFECT_ADDBUFF:
						for(Integer buffID: cfg.param1)
							checkBuffIDValid(gdCfgs, row, buffID);
						break;
					case GameData.SPIRIT_EFFECT_FIXSUBDAMAGE:
						for(Integer sid: cfg.param1)
							checkSkillIDValid(gdCfgs, row, sid);
						cfg.param2 = excelReadContext.getIntValue(row, col++) - 1;
						if(cfg.param2 < 0 || cfg.param2 >= 3)
							throw new Exception("心法效果表 " + cfg.id  + " 伤害事件ID: " + cfg.param2 + " 非法");
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SPIRIT_EFFECT_FIXBASESKILL:
						for(Integer sid: cfg.param1)
							checkSkillIDValid(gdCfgs, row, sid);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.SPIRIT_EFFECT_FIXAI:
						for(Integer aid: cfg.param1)
							checkAiTrigerIDValid(gdCfgs, row, aid);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						break;
					default:
//						break;
						throw new Exception("row " + row + " 不支持的心法效果公式ID " + cfg.type + " !");
					}
					
					if(mapcfg.put(cfg.id, cfg) != null)
						throw new Exception("心法效果表 id " + cfg.id + " 重复");
					row++;
				}
				gdCfgs.spiritEffects = mapcfg;
			}
		}
		
		System.out.println("load table " + fileName + " success.");
	}
	
	// 怪物表
	public void loadMonsterTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 2;

				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.MonsterCFGS> monsters = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.MonsterCFGS monsterCfg = new SBean.MonsterCFGS();
					monsterCfg.id = excelReadContext.getIntValue(row, colStart);
					col = colStart + 7;
					monsterCfg.bossType = excelReadContext.getIntValue(row, col++);
					switch (monsterCfg.bossType)
					{
					case GameData.MONSTER_BOSSTYPE_COMMON:		
					case GameData.MONSTER_BOSSTYPE_NORMALBOSS:
					case GameData.MONSTER_BOSSTYPE_FINALBOSS:	
					case GameData.MONSTER_BOSSTYPE_NPC:	
					case GameData.MONSTER_BOSSTYPE_NORMALSTATUE:
					case GameData.MONSTER_BOSSTYPE_BIGSTATUE:
					case GameData.MONSTER_BOSSTYPE_BOSSSTATUE:
					case GameData.MONSTER_BOSSTYPE_EMERGENCY_NORMAL:
					case GameData.MONSTER_BOSSTYPE_EMERGENCY_NORMALBOSS:
					case GameData.MONSTER_BOSSTYPE_EMERGENCY_BOSS:
						break;
					default:
						throw new Exception("怪物表 row " + row + " 怪物 ： " + monsterCfg.id + " boss type " + monsterCfg.bossType + " 非法");
					}
					monsterCfg.level = excelReadContext.getIntValue(row, col++);
					monsterCfg.maxHP = excelReadContext.getIntValue(row, col++);
					monsterCfg.atkN = excelReadContext.getIntValue(row, col++);
					monsterCfg.defN = excelReadContext.getIntValue(row, col++);
					monsterCfg.atr = excelReadContext.getIntValue(row, col++);
					monsterCfg.ctr = excelReadContext.getIntValue(row, col++);
					monsterCfg.acrN = excelReadContext.getIntValue(row, col++);
					monsterCfg.tou = excelReadContext.getIntValue(row, col++);
					monsterCfg.atkA = excelReadContext.getIntValue(row, col++);
					monsterCfg.atkC = excelReadContext.getIntValue(row, col++);
					monsterCfg.defC = excelReadContext.getIntValue(row, col++);
					monsterCfg.atkW = excelReadContext.getIntValue(row, col++);
					monsterCfg.defW = excelReadContext.getIntValue(row, col++);
					monsterCfg.attacks = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						int attackId = excelReadContext.getIntValue(row, col++);
						this.checkSkillIDValid(gdCfgs, row, attackId);
						monsterCfg.attacks.add(attackId);
					}
					monsterCfg.skills = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						SBean.SkillBriefCFGS monsterskill = new SBean.SkillBriefCFGS();
						monsterskill.id = excelReadContext.getIntValue(row, col++);
						monsterskill.lvl = excelReadContext.getIntValue(row, col++);
						this.checkSkillIDValid(gdCfgs, row, monsterskill.id);
						monsterCfg.skills.add(monsterskill);
					}
					monsterCfg.attackList = excelReadContext.getIntegerList(row, col++, ";");
					for (Integer seq : monsterCfg.attackList)
					{
						if (seq > 0)
						{
							SBean.SkillBriefCFGS s = monsterCfg.skills.get(seq - 1);
							if (s == null || s.id <= 0)
								throw new Exception("怪物 " + monsterCfg.id + " 攻击序列 " + seq + "  技能 = " + s.id);
						}
					}

					if (monsterCfg.attackList.size() > 0)
					{
						for (Integer sid : monsterCfg.attacks)
						{
							if (sid <= 0)
								throw new Exception("怪物 " + monsterCfg.id + " 普通攻击  " + sid);
						}
					}

					monsterCfg.speed = excelReadContext.getIntValue(row, col++);
					monsterCfg.radius = excelReadContext.getIntValue(row, col++);
					monsterCfg.checkRange = excelReadContext.getIntValue(row, col);		//搜索半径

					col += 2;
					monsterCfg.fixedDropID = excelReadContext.getIntValue(row, col++);
					checkFixedDropIDValid(gdCfgs, row, monsterCfg.fixedDropID);
					monsterCfg.randomDropIDs = excelReadContext.getIntegerList(row, col++, ";");
					if(monsterCfg.randomDropIDs.size() != 5)
						throw new Exception("怪物表随机掉落ID 数量 非法");
					for(int randomDropID: monsterCfg.randomDropIDs)
					{
						checkRandomDropIDValid(gdCfgs, row, randomDropID);
					}
					monsterCfg.randomDropCnt = excelReadContext.getIntValue(row, col++);
					monsterCfg.isActive = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, monsterCfg.isActive);
					monsterCfg.race = excelReadContext.getIntValue(row, col++);
					monsterCfg.patrolInterval = excelReadContext.getIntegerList(row, col++, ";");
					monsterCfg.patrolRadius = excelReadContext.getIntValue(row, col++);
					monsterCfg.patrolSpeed = excelReadContext.getIntValue(row, col++);
					monsterCfg.maxTraceRange = excelReadContext.getIntValue(row, col++);		//追踪最大半径
					monsterCfg.addExp = excelReadContext.getIntValue(row, col++);
					monsterCfg.buffDropID = excelReadContext.getIntValue(row, col++);
					this.checkBuffDropIDValid(gdCfgs, row, monsterCfg.buffDropID);
					monsterCfg.buffDropCnt = excelReadContext.getIntValue(row, col++);
					monsterCfg.foreverState = excelReadContext.getIntegerList(row, col++, ";");
					monsterCfg.spaHP = excelReadContext.getFloatValue(row, col++);
					monsterCfg.spaOdd = excelReadContext.getFloatValue(row, col++);
					
					col = colStart + 55;
					monsterCfg.countType = excelReadContext.getByteValue(row, col++);
					switch (monsterCfg.countType)
					{
					case GameData.MONSTER_COUNTTYPE_DAMAGE:
					case GameData.MONSTER_COUNTTYPE_KILL:	
						break;
					default:
						throw new Exception("怪物计数类型 " + monsterCfg.countType + " 非法");
					}
					monsterCfg.bwType = excelReadContext.getByteValue(row, col++);
					switch (monsterCfg.bwType)
					{
					case GameData.BWTYPE_NONE:
					case GameData.BWTYPE_WHITE:
					case GameData.BWTYPE_BLACK:
					case GameData.BWTYPE_SAFE:
						break;
					default:
						throw new Exception("怪物阵营  " + monsterCfg.bwType + " 非法");
					}
					
					col = colStart + 68;
					monsterCfg.percentDrop = new HashMap<>();
					String dropString = excelReadContext.getStringValue(row, col++);
					if (!(dropString.equals("0.0") || dropString.equals("0")))
					{
						String[] percents = dropString.split(";");
						List<String> drops = excelReadContext.getStringList(row, col, ";");
						if (percents.length != drops.size())
						{
							throw new Exception("怪物持续掉落配置 " + row + " 行掉落和比例不匹配");
						}
						for (int i = 0; i < percents.length; i++)
						{
							String[] drop = drops.get(i).split(",");
							int rate = Integer.parseInt(percents[i]);
							int dropId = Integer.parseInt(drop[0]);
							int dropTimes = Integer.parseInt(drop[1]);
							if (rate <= 0 || rate >= 10000)
							{
								throw new Exception("怪物持续掉落比例  " + rate + " 非法");
							}
							checkRandomDropIDValid(gdCfgs, row, dropId);
							if (dropTimes <= 0)
							{
								throw new Exception("怪物持续掉落次数  " + dropTimes + " 必须大于0");
							}
							monsterCfg.percentDrop.put(rate, new SBean.PercentDropCFGS(dropId, dropTimes));
						}
					}
					col++;
					
					monsterCfg.birthBuffs = excelReadContext.getIntegerList(row, col++, ";");
					for(int buffID: monsterCfg.birthBuffs)
					{
						if(buffID > 0)
							this.checkBuffIDValid(gdCfgs, row, buffID);
					}
					
					if (monsters.put(monsterCfg.id, monsterCfg) != null)
						throw new Exception("怪物ID重复， id = " + monsterCfg.id);

					if (excelReadContext.getIntValue(row, 53) == 1 && excelReadContext.getStringValue(row, 54).equals("0.0"))
						throw new Exception("怪物有描边无描边颜色， id = " + monsterCfg.id);
					monsterCfg.logDamage = excelReadContext.getByteValue(row, col++);
					monsterCfg.tdScore = excelReadContext.getIntValue(row, col++);
					monsterCfg.armorID = excelReadContext.getIntValue(row, col++);
					monsterCfg.armorMaxVal = excelReadContext.getIntValue(row, col++);
					monsterCfg.armorTransRate = excelReadContext.getIntValue(row, col++) / 10_000.f;
					monsterCfg.armorDmgDeep = excelReadContext.getIntValue(row, col++) / 10_000.f;
					monsterCfg.armorTransVal = excelReadContext.getIntValue(row, col++);
					row++;
				}
				gdCfgs.monsters = monsters;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 1;
				final int rowStart = excelReadContext.locateColumnTag(colStart, "怪物类型");
				int col = colStart + 1;
				int row = rowStart + 1;
				SBean.MonsterDamageCFGS cfg = new SBean.MonsterDamageCFGS();
				cfg.dmgTos = new HashMap<>();
				cfg.dmgBys = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart + 1))
				{
					int type = excelReadContext.getIntValue(row, colStart + 1);
					col = colStart + 3;
					int prop1 = excelReadContext.getIntValue(row, col++);
					if(prop1 > 0)
					{
						checkPropertyIDValid(gdCfgs, row, prop1);
						cfg.dmgTos.put(type, prop1);
					}
					int prop2 = excelReadContext.getIntValue(row, col++);
					if(prop2 > 0)
					{
						checkPropertyIDValid(gdCfgs, row, prop2);
						cfg.dmgBys.put(type, prop2);
					}
					row++;
				}
				gdCfgs.monsterDamages = cfg;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	// npc表
	public void loadNpcTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;

			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.NpcCFGS> npcs = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.NpcCFGS npcCfg = new SBean.NpcCFGS();
					npcCfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 3;
					List<Integer> funcs = excelReadContext.getIntegerList(row, col++, ";");
					npcCfg.funcs = new HashSet<>(funcs);
					if(npcCfg.funcs.size() > 1 && npcCfg.funcs.contains(GameData.NPC_FUNCTION_ENTER_WEAPONMAP))
						throw new Exception("row " + row + " npc " + npcCfg.id + " 功能ID " + npcCfg.functionArg + " 非法");
					
					npcCfg.toMonsterID = excelReadContext.getIntValue(row, col++);
					
					col = colStart + 6;
					boolean showInMap = excelReadContext.getIntValue(row, col++) == 1;
					
					col = colStart + 10;
					if(showInMap && excelReadContext.isNotEmpty(row, col))
						throw new Exception("NPC表 row " + row + " npc " + npcCfg.id + " 同时在地图列表显示 和处于任务 " + excelReadContext.getIntegerList(row, col, ";") + " 可见");
					
					if (npcs.put(npcCfg.id, npcCfg) != null)
						throw new Exception("NpcID重复， id = " + npcCfg.id);

					col = colStart + 13;
					List<Integer> functionArg = excelReadContext.getIntegerList(row, col, ";");
					npcCfg.functionArg = new HashSet<>(functionArg);
					
					row++;
				}
				gdCfgs.npcs = npcs;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadMineral(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;

			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.MineralCFGS> cfgs = new HashMap<>();
				row = rowStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.MineralCFGS cfg = new SBean.MineralCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					col = colStart + 5;
					cfg.mineralType = excelReadContext.getIntValue(row, col++);
					switch (cfg.mineralType)
					{
					case GameData.MINERAL_TYPE_TASK:
					case GameData.MINERAL_TYPE_MAPCOPY:
					case GameData.MINERAL_TYPE_WORLDRAND:
					case GameData.MINERAL_TYPE_FLAG:
					case GameData.MINERAL_TYPE_BANQUET:
					case GameData.MINERAL_TYPE_WEDDINF_BOX:
					case GameData.MINERAL_TYPE_STELE:
						break;
					default:
						throw new Exception("row " + row + " 不支持的矿物 类型 " + cfg.mineralType + " !");
					}
					cfg.needLvl = excelReadContext.getIntValue(row, col++);
					cfg.needItem = excelReadContext.getIntValue(row, col++);
					this.checkConsumeIDValid(gdCfgs, row, cfg.needItem, true);
					cfg.isCostItem = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.isCostItem);
					cfg.mineralCount = excelReadContext.getIntValue(row, col++);
					cfg.mineralTime = excelReadContext.getIntValue(row, col++);
					col = colStart + 13;
					cfg.monsterOdds = excelReadContext.getIntValue(row, col++);
					cfg.monsterID = excelReadContext.getIntValue(row, col++);
					if(cfg.monsterID > 0)
						this.checkMonsterID(gdCfgs, row, cfg.monsterID);
					cfg.monsterCount = excelReadContext.getIntValue(row, col++);
					cfg.monsterStandTime = excelReadContext.getIntValue(row, col++);
					cfg.refreshInterval = excelReadContext.getIntValue(row, col++);
					cfg.fixedDropID = excelReadContext.getIntValue(row, col++);
					this.checkFixedDropIDValid(gdCfgs, row, cfg.fixedDropID);
					cfg.randomDropID = excelReadContext.getIntValue(row, col++);
					checkRandomDropIDValid(gdCfgs, row, cfg.randomDropID);
					cfg.randomDropCnt = excelReadContext.getIntValue(row, col++);
					cfg.buffDropID = excelReadContext.getIntValue(row, col++);
					this.checkBuffDropIDValid(gdCfgs, row, cfg.buffDropID);
					cfg.buffDropCnt = excelReadContext.getIntValue(row, col++);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("采矿表ID重复， id = " + cfg.id);
					row++;
				}
				gdCfgs.minerals = cfgs;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadTrapExpanded(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(2);
			{
				Map<Integer, SBean.TrapExpandedCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.TrapExpandedCFGS cfg = new SBean.TrapExpandedCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					cfg.groupID = excelReadContext.getIntValue(row, colStart + 1);
					col = colStart + 3;
					cfg.position = excelReadContext.getVector3(row, col++, ";");
					cfg.rotation = excelReadContext.getVector3F(row, col++, ";");
					cfg.orgState = excelReadContext.getIntValue(row, col++);
					cfg.trigType = excelReadContext.getIntValue(row, col++);

					col = colStart + 8;
					cfg.relateTraps = new ArrayList<>();
					if (excelReadContext.isNotEmpty(row, col))
						cfg.relateTraps = excelReadContext.getIntegerList(row, col, ";");

					col = colStart + 10;
					cfg.stateCondition = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						cfg.stateCondition.add(excelReadContext.getIntValue(row, col++));
					}
					cfg.delay = excelReadContext.getIntValue(row, col++);
					cfg.fixedDropID = excelReadContext.getIntValue(row, col++);
					this.checkFixedDropIDValid(gdCfgs, row, cfg.fixedDropID);
					cfg.randomDropID = excelReadContext.getIntValue(row, col++);
					this.checkRandomDropIDValid(gdCfgs, row, cfg.randomDropID);
					cfg.randomDropCnt = excelReadContext.getIntValue(row, col++);
					cfg.paras = new ArrayList<>();
					for(int i=0; i<4; i++)
					{
						cfg.paras.add(excelReadContext.getIntValue(row, col++));
					}
//					col = colStart + 20;
					cfg.monsterOdds = excelReadContext.getIntValue(row, col++);
					cfg.monsterCount = excelReadContext.getIntValue(row, col++);
					cfg.monsterID = excelReadContext.getIntValue(row, col++);
					if(cfg.monsterID > 0)
						this.checkMonsterID(gdCfgs, row, cfg.monsterID);
					cfg.buffDropID = excelReadContext.getIntValue(row, col++);
					this.checkBuffDropIDValid(gdCfgs, row, cfg.buffDropID);
					cfg.buffDropCnt = excelReadContext.getIntValue(row, col++);
					cfg.dropRadius = excelReadContext.getIntValue(row, col++);
					cfg.skillID = excelReadContext.getIntValue(row, col++);
					this.checkSkillIDValid(gdCfgs, row, cfg.skillID);
					if(cfg.skillID > 0 && cfg.paras.get(cfg.paras.size() - 1) <= 0)
						throw new Exception("陷阱 " + cfg.id + " 陷阱技能ID: " + cfg.skillID + ", 可选参数4 技能释放时间间隔 不大于0 !");
					
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("陷阱ID " + cfg.id + " 重复！");
					
					row++;
				}
				gdCfgs.traps = cfgs;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadEquipToughen(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{

				List<SBean.EquipToughenCFGS> etList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				int curLvl = 0;
				List<SBean.EquipLevelUpCostCFGS> costs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int lvl = excelReadContext.getIntValue(row, col++);
					if (curLvl++ != lvl)
						throw new Exception("强化等级不连续，缺少等级" + (curLvl - 1));
					SBean.EquipToughenCFGS etcfg = new SBean.EquipToughenCFGS(lvl, new ArrayList<>());

					// 读取每一级升级消耗
					SBean.EquipLevelUpCostCFGS costCfg = new SBean.EquipLevelUpCostCFGS();
					costCfg.energy = excelReadContext.getIntValue(row, col++);
					if (costCfg.energy < 0)
						throw new Exception("强化等级 " + lvl + " 强化能量小于0 !");
					costCfg.items = new ArrayList<>();
					for (int index = 0; index < 3; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
						{
							costCfg.items.add(new SBean.DummyGoods(id, count));
						}
					}
					costs.add(costCfg);

					// 计算从前面任何一级升级到当前级的升级消耗
					for (int i = 0; i <= lvl; ++i)
					{
						int lvlenergy = 0;
						Map<Integer, SBean.DummyGoods> lvlcostMap = new TreeMap<>();
						for (int j = i + 1; j <= lvl; ++j)
						{
							SBean.EquipLevelUpCostCFGS c = costs.get(j);
							lvlenergy += c.energy;
							for (SBean.DummyGoods e : c.items)
							{
								SBean.DummyGoods lvlitem = lvlcostMap.get(e.id);
								if (lvlitem == null)
									lvlitem = new SBean.DummyGoods(e.id, e.count);
								else
									lvlitem.count += e.count;
								lvlcostMap.put(lvlitem.id, lvlitem);
							}
						}
						SBean.EquipLevelUpCostCFGS lvlcfg = new SBean.EquipLevelUpCostCFGS(lvlenergy, new ArrayList<>());
						lvlcfg.items.addAll(lvlcostMap.values());
						etcfg.cost.add(lvlcfg);
					}

					etList.add(etcfg);
					++row;
					col = colStart;
				}
				gdCfgs.toughen = etList;

			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadEquipUpStar(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				List<SBean.EquipUpStarCFGS> eusList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				int curLvl = 0;
				List<SBean.EquipStarUpCostCFGS> costs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int lvl = excelReadContext.getByteValue(row, col++);
					if (curLvl++ != lvl)
						throw new Exception("升星不连续，缺少星级" + (curLvl - 1));
					col += 2;
					int atkNUp = excelReadContext.getIntValue(row, col++);

					SBean.EquipUpStarCFGS euscfg = new SBean.EquipUpStarCFGS(lvl, atkNUp / 10000.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, new ArrayList<>());

					SBean.EquipStarUpCostCFGS costCfg = new SBean.EquipStarUpCostCFGS(new ArrayList<>());
					for (int index = 0; index < 4; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
						{
							costCfg.items.add(new SBean.DummyGoods(id, count));
						}
					}
					costs.add(costCfg);
					euscfg.defNUp = excelReadContext.getIntValue(row, col++) / 10000.0;
					euscfg.maxHpUp = excelReadContext.getIntValue(row, col++) / 10000.0;
					
					euscfg.maxHpFixedUp = excelReadContext.getIntValue(row, col++) ;
					euscfg.atkFixedUp = excelReadContext.getIntValue(row, col++) ;
					euscfg.defFixedUp = excelReadContext.getIntValue(row, col++) ;
					euscfg.sucMin = excelReadContext.getIntValue(row, col++) ;
					euscfg.sucMax = excelReadContext.getIntValue(row, col++) ;
					euscfg.sucRate = excelReadContext.getIntValue(row, col++) ;

					for (int i = 0; i <= lvl; ++i)
					{
						Map<Integer, SBean.DummyGoods> lvlcostMap = new TreeMap<>();
						for (int j = i + 1; j <= lvl; ++j)
						{
							SBean.EquipStarUpCostCFGS c = costs.get(j);
							for (SBean.DummyGoods e : c.items)
							{
								SBean.DummyGoods lvlitem = lvlcostMap.get(e.id);
								if (lvlitem == null)
									lvlitem = new SBean.DummyGoods(e.id, e.count);
								else
									lvlitem.count += e.count;
								lvlcostMap.put(lvlitem.id, lvlitem);
							}
						}
						SBean.EquipStarUpCostCFGS lvlcfg = new SBean.EquipStarUpCostCFGS(new ArrayList<>());
						lvlcfg.items.addAll(lvlcostMap.values());
						euscfg.cost.add(lvlcfg);
					}

					eusList.add(euscfg);
					++row;
					col = colStart;
				}
				gdCfgs.upStar = eusList;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadEquipPurgatory(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				List<SBean.EquipPurgatoryCFGS> epList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.EquipPurgatoryCFGS epcfg = new SBean.EquipPurgatoryCFGS();
					epcfg.lvl = excelReadContext.getByteValue(row, col++);
					epcfg.quality = excelReadContext.getByteValue(row, col++);
					epcfg.goods = new ArrayList<>();
					for (int index = 0; index < 4; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if (id != 0)
						{
							SBean.DummyGoods good = new SBean.DummyGoods(id, count);
							epcfg.goods.add(good);
						}
					}
					epList.add(epcfg);
					++row;
					col = colStart;
				}
				gdCfgs.purgatory = epList;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loadActivityTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.ActivityMapGroupCFGS> cfgs = new HashMap<>();
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ActivityMapGroupCFGS cfg = new SBean.ActivityMapGroupCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col += 2;
					List<Integer> weekDays = excelReadContext.getIntegerList(row, col++, ";");
					cfg.weekDays = new HashSet<>();
					for(int d: weekDays)
					{
						if(!cfg.weekDays.add(d))
							throw new Exception("row " + row + " 活动表组ID " + cfg.id + " 开放日 " + d + " 重复！");
					}
					
					cfg.dayEnterTimes = excelReadContext.getIntValue(row, col++); 
					cfg.openStartTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					if(cfg.openStartTime < GameData.GAME_DAY_REFRESH_TIME * 3600)
						throw new Exception("row " + row + " 活动表组ID " + cfg.id + " 开启时间 " + cfg.openStartTime + " 非法！");
					
					cfg.lastTime = excelReadContext.getIntValue(row, col++);
					if(cfg.lastTime + (cfg.openStartTime - GameData.GAME_DAY_REFRESH_TIME * 3600) > GameTime.getDayTimeSpan())
						throw new Exception("row " + row + " 活动表组ID " + cfg.id + " 持续时间 " + cfg.lastTime + " 超过一天！");
					
					cfg.maps = new TreeSet<>();
					
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("活动表组ID " + cfg.id + " 重复！");
					
					row++;
					col = colStart;
				}
				gdCfgs.actMapGroups = cfgs;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Set<Integer> monsterIDs = new HashSet<>();
				Map<Integer, SBean.WorldBossCFGS> cfgMap = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.WorldBossCFGS cfg = new SBean.WorldBossCFGS();
					cfg.base = new SBean.WorldMonsterCFGS();
					cfg.base.id = excelReadContext.getIntValue(row, col++);
					cfg.base.monsterID = excelReadContext.getIntValue(row, col++);
					checkMonsterID(gdCfgs, row, cfg.base.monsterID);
					if(!monsterIDs.add(cfg.base.monsterID))
						throw new Exception("世界boss ID " + cfg.base.id + " 对应的怪物ID " + cfg.base.monsterID + " 重复");
					
					cfg.base.mapID = excelReadContext.getIntValue(row, col++);
					List<Integer> openDay = excelReadContext.getIntegerList(row, col++, ";");
					cfg.base.openDay = new TreeSet<>();
					for(Integer day: openDay)
						cfg.base.openDay.add(day);
					cfg.base.refreshTime = new ArrayList<>();
					List<String> refreshTime = excelReadContext.getStringList(row, col++, ";");
					for(String time: refreshTime)
						cfg.base.refreshTime.add(GameTime.parseSecondOfDay(time) - BossManager.BOSS_REFRESH_FORWARDTIME);		//提前10分钟刷
					cfg.base.standbyTime = excelReadContext.getIntValue(row, col++);
					
					for(int i=0; i<cfg.base.refreshTime.size() - 1; i++)
					{
						if(cfg.base.refreshTime.get(i) + cfg.base.standbyTime >= cfg.base.refreshTime.get(i + 1))
							throw new Exception("row " + row + " 世界BOSS " + cfg.base.id + " 存活时间 " + cfg.base.standbyTime + " 非法");
					}
					cfg.base.refreshPos = new ArrayList<>();
					for(int i=0; i<3; i++)
						cfg.base.refreshPos.add(excelReadContext.getVector3(row, col++, ";"));
					
					col = colStart + 24;
					cfg.killDrop = new SBean.ClassTypeDrop(excelReadContext.getIntegerList(row, col++, ";"), excelReadContext.getIntValue(row, col++));
					if(cfg.killDrop.dropIDs.size() != GameData.CLASS_TYPE_END)
						throw new Exception("row " + row + " 击杀者随机掉落ID " + cfg.killDrop.dropIDs.size() + " 数量不正确！");
					for(int rd: cfg.killDrop.dropIDs)
						checkRandomDropIDValid(gdCfgs, row, rd);
					
					cfg.rankDrops = new ArrayList<>();
					for(int i=0; i<10; i++)
					{
						SBean.ClassTypeDrop cd = new SBean.ClassTypeDrop(excelReadContext.getIntegerList(row, col++, ";"), excelReadContext.getIntValue(row, col++));
						if(cd.dropIDs.size() != GameData.CLASS_TYPE_END)
							throw new Exception("row " + row + " 排名 " + (i + 1) + " 随机掉落ID " + cd.dropIDs.size() + " 数量不正确！");
						
						for(int rd: cd.dropIDs)
							checkRandomDropIDValid(gdCfgs, row, rd);
						
						cfg.rankDrops.add(cd);
					}
					
					cfg.joinDrop = new SBean.ClassTypeDrop(excelReadContext.getIntegerList(row, col++, ";"), excelReadContext.getIntValue(row, col++));
					if(cfg.joinDrop.dropIDs.size() != GameData.CLASS_TYPE_END)
						throw new Exception("row " + row + " 参与奖随机掉落ID " + cfg.joinDrop.dropIDs.size() + " 数量不正确！");
					for(int rd: cfg.joinDrop.dropIDs)
						checkRandomDropIDValid(gdCfgs, row, rd);
					
					col = 48;
					List<Integer> popTimes = excelReadContext.getIntegerList(row, col++, ";");
					cfg.popTimes = new ArrayList<>();
					for(int t: popTimes)
						cfg.popTimes.add(t * 60);
					
					List<Integer> popIDs = excelReadContext.getIntegerList(row, col++, ";");
					if(cfg.popTimes.size() != popIDs.size())
						throw new Exception("row " + row + " 世界BOSS冒泡时间数量 " + cfg.popTimes.size() + " 和气泡ID数量 " + popIDs + " 不一致 ");
					
					if(cfgMap.put(cfg.base.id, cfg) != null)
						throw new Exception("世界boss ID " + cfg.base.id + " 重复");
					
					row++;
				}
				gdCfgs.worldBosses = cfgMap;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Set<Integer> monsterIDs = new HashSet<>();
				Map<Integer, SBean.WorldMonsterCFGS> cfgMap = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.WorldMonsterCFGS cfg = new SBean.WorldMonsterCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.monsterID = excelReadContext.getIntValue(row, col++);
					checkMonsterID(gdCfgs, row, cfg.monsterID);
					if(!monsterIDs.add(cfg.monsterID))
						throw new Exception("精英怪 " + cfg.id + " 对应的怪物ID " + cfg.monsterID + " 重复");
					
					cfg.mapID = excelReadContext.getIntValue(row, col++);
//					checkWorldMapID(gdCfgs, row, cfg.mapID);
					List<Integer> openDay = excelReadContext.getIntegerList(row, col++, ";");
					cfg.openDay = new TreeSet<>();
					for(Integer day: openDay)
						cfg.openDay.add(day);
					cfg.refreshTime = new ArrayList<>();
					List<String> refreshTime = excelReadContext.getStringList(row, col++, ";");
					for(String time: refreshTime)
						cfg.refreshTime.add(GameTime.parseSecondOfDay(time));
					cfg.standbyTime = excelReadContext.getIntValue(row, col++);
					cfg.refreshPos = new ArrayList<>();
					for(int i=0; i<5; i++)
						cfg.refreshPos.add(excelReadContext.getVector3(row, col++, ";"));
					
					cfg.rollNoticeType = excelReadContext.getByteValue(row, col++);
					switch (cfg.rollNoticeType)
					{
					case GameData.ROLLNOTICE_TYPE_SUPERMONSTER1:
					case GameData.ROLLNOTICE_TYPE_SUPERMONSTER2:
					case GameData.ROLLNOTICE_TYPE_SUPERMONSTER3:
					case 0:
						break;
					default:
						throw new Exception("row " + row + " 精英怪 " + cfg.id + " 走马灯类型ID " + cfg.rollNoticeType + " 非法");
					}
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("精英怪 ID " + cfg.id + " 重复");
					
					row++;
				}
				gdCfgs.worldSuperMonsters = cfgMap;
			}
			
			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.WorldMineralCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.WorldMineralCFGS cfg = new SBean.WorldMineralCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.mineralID = excelReadContext.getIntValue(row, col++);
					SBean.MineralCFGS mineCfg = gdCfgs.minerals.get(cfg.mineralID);
					if(mineCfg == null)
						throw new Exception("row " + row + " 活动矿 " + cfg.id + " 矿物ID " + cfg.mineralID + " 非法");
					
					if(mineCfg.mineralType != GameData.MINERAL_TYPE_WORLDRAND)
						throw new Exception("row " + row + " 活动矿 " + cfg.id + " 矿物ID " + cfg.mineralID + " 采矿类型 " + mineCfg.mineralType + " 非法");
						
//					checkMineralID(gdCfgs, row, cfg.mineralID);
					cfg.mapID = excelReadContext.getIntValue(row, col++);
					
					List<Integer> openDay = excelReadContext.getIntegerList(row, col++, ";");
					cfg.openDay = new TreeSet<>();
					for(Integer day: openDay)
						cfg.openDay.add(day);
					cfg.refreshTime = new ArrayList<>();
					List<String> refreshTime = excelReadContext.getStringList(row, col++, ";");
					for(String time: refreshTime)
						cfg.refreshTime.add(GameTime.parseSecondOfDay(time));
					cfg.standbyTime = excelReadContext.getIntValue(row, col++);
					cfg.refreshPos = new ArrayList<>();
					for(int i=0; i<5; i++)
						cfg.refreshPos.add(excelReadContext.getVector3(row, col++, ";"));
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("活动矿 ID " + cfg.id + " 重复");
					
					row++;
				}
				gdCfgs.worldMinerals = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadDemonHoleTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.DemonHoleCFGS demonHoleCfg = new SBean.DemonHoleCFGS();
			excelReadContext.ReadSheet(0);
			{
				demonHoleCfg.base = new SBean.DemonHoleBaseCFGS(0, 0, 0, new HashSet<>(), 0, 0, new ArrayList<>(), new ArrayList<>(), 0, 0, 0, new ArrayList<>());
				final int rowCommon = excelReadContext.locateColumnTag(0, "通用配置");
				{
					int row = rowCommon + 1;
					int col = 2;
					demonHoleCfg.base.reviveBuffID = excelReadContext.getIntValue(row++, col);
					checkBuffIDValid(gdCfgs, row, demonHoleCfg.base.reviveBuffID);
					demonHoleCfg.base.enterCostItem = excelReadContext.getIntValue(row++, col);
					SBean.ItemCFGS costCfg = gdCfgs.items.get(Math.abs(demonHoleCfg.base.enterCostItem));
					if(costCfg == null || costCfg.canTrade != 1)
						throw new Exception("row " + row + " 进入上一层的钥匙ID " + demonHoleCfg.base.enterCostItem + " 非法!");
					
					demonHoleCfg.base.dayEnterTimes = excelReadContext.getIntValue(row++, col);
					List<Integer> openDays = excelReadContext.getIntegerList(row++, col, ";");
					for(int w: openDays)
					{
						if(w < 0 || w > GameData.WEEK_DAY_SATURDAY)
							throw new Exception("row " + row + " 伏魔洞开放日 " + w + " 非法!");
						
						demonHoleCfg.base.openDays.add(w);
					}
					demonHoleCfg.base.startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
					demonHoleCfg.base.lastTime = excelReadContext.getIntValue(row++, col);
					demonHoleCfg.base.lvlReq = excelReadContext.getIntValue(row++, col);
					demonHoleCfg.base.transformLvlReq = excelReadContext.getIntValue(row++, col);
					row++;
					demonHoleCfg.base.autoCloseTime = excelReadContext.getIntValue(row++, col);
					row += 4;
					int roleDeadDropItemID = excelReadContext.getIntValue(row++, col);
					if(roleDeadDropItemID >= 0)
						throw new Exception("row " + row + " 玩家死亡掉落道具 " + roleDeadDropItemID + " 必须是非绑定道具");
					SBean.ItemCFGS dItemCfg = gdCfgs.items.get(Math.abs(roleDeadDropItemID));
					if(dItemCfg == null || dItemCfg.canTrade != 1)
						throw new Exception("row " + row + " 玩家死亡掉落道具 " + roleDeadDropItemID + " 非法!");
					
					int roleDeadDropItemCount = excelReadContext.getIntValue(row++, col);
					if(roleDeadDropItemCount <= 0)
						throw new Exception("row " + row + " 玩家死亡掉落数量 " + roleDeadDropItemCount + " 非法!");
					demonHoleCfg.base.roleDeadDrop.add(new SBean.DummyGoods(roleDeadDropItemID, roleDeadDropItemCount));
				}
				
				final int rowDeadBuff = excelReadContext.locateColumnTag(0, "死亡头顶buff");
				{
					int row = rowDeadBuff + 2;
					int col = 1;
					int lastDeadTimes = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int deadTimes = excelReadContext.getIntValue(row, col);
						if(deadTimes < lastDeadTimes)
							throw new Exception("row " + row + " 连续死亡次数 " + deadTimes + " 非法");
						
						int buffID = excelReadContext.getIntValue(row++, col + 1);
						checkBuffIDValid(gdCfgs, row, buffID);
						
						demonHoleCfg.base.deadBuffs.add(new SBean.DemonHoleBuffCFGS(deadTimes, buffID));
					}
				}
				
				final int rowKillBuff = excelReadContext.locateColumnTag(0, "连杀头顶buff/特效");
				{
					int row = rowKillBuff + 2;
					int col = 1;
					int lastKillTimes = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int killTimes = excelReadContext.getIntValue(row, col);
						if(killTimes < lastKillTimes)
							throw new Exception("row " + row + " 连杀次数 " + killTimes + " 非法");
						
						int buffID = excelReadContext.getIntValue(row++, col + 1);
						checkBuffIDValid(gdCfgs, row, buffID);
						
						demonHoleCfg.base.killBuffs.add(new SBean.DemonHoleBuffCFGS(killTimes, buffID));
					}
				}
			}
			
			excelReadContext.ReadSheet(1);
			{
				int row = 1;
				int col = 0;
				
				int lastMinLvl = 0;
				int lastMaxLvl = 0;
				demonHoleCfg.grades = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, 0))
				{
					int grade = excelReadContext.getIntValue(row, col++);
					SBean.DemonHoleGradeCFGS gradeCfg = new SBean.DemonHoleGradeCFGS(0, 0, new ArrayList<>());
					demonHoleCfg.grades.add(gradeCfg);
					if(grade != demonHoleCfg.grades.size())
						throw new Exception("row " + row + " 难度 " + grade + " 不连续");
					
					col = 2;
					int minLvl = excelReadContext.getIntValue(row, col++);
					if(minLvl < lastMinLvl)
						throw new Exception("row " + row + " 等级下限 " + minLvl + " 非法!");
					
					if(lastMinLvl == 0 && demonHoleCfg.base.lvlReq < minLvl)
						throw new Exception("row " + row + " 最小等级下限 " + minLvl + " 小于开放等级 " + demonHoleCfg.base.lvlReq);
					
					lastMinLvl = minLvl;
					gradeCfg.lvlFloor = excelReadContext.getIntValue(row, col++);
					if(gradeCfg.lvlFloor <= lastMaxLvl)
						throw new Exception("row " + row + " 等级上限 " + gradeCfg.lvlFloor + " 非法!");
					
					lastMaxLvl = gradeCfg.lvlFloor;
					if(lastMinLvl < demonHoleCfg.base.lvlReq)
						throw new Exception("row " + row + " 等级下限 " + minLvl + " 小于开放等级 " + demonHoleCfg.base.lvlReq);
					
					if(lastMaxLvl < lastMinLvl)
						throw new Exception("row " + row + " 等级上限 " + lastMaxLvl + " 小于 等级下限 " + lastMinLvl);
						
					col++;
					gradeCfg.maxRoles = excelReadContext.getIntValue(row, col++);
					
					row++;
					col = 0;
				}
			}
			
			excelReadContext.ReadSheet(2);
			{
				int row = 1;
				int col = 0;
				demonHoleCfg.bosses = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, 0))
				{
					SBean.DemonHoleBossCFGS bossCfg = new SBean.DemonHoleBossCFGS(0, 0, new ArrayList<>());
					bossCfg.bossID = excelReadContext.getIntValue(row, col++);
					bossCfg.monsterID = excelReadContext.getIntValue(row, col++);
					checkMonsterID(gdCfgs, row, bossCfg.monsterID);
					
					int rank = 1;
					while(excelReadContext.isNotEmpty(row, col))
					{
						SBean.ClassTypeDrop cd = new SBean.ClassTypeDrop(excelReadContext.getIntegerList(row, col++, ";"), excelReadContext.getIntValue(row, col++));
						if(cd.dropIDs.size() != GameData.CLASS_TYPE_END)
							throw new Exception("row " + row + " 排名 " + rank + "随机掉落ID " + cd.dropIDs.size() + " 数量不正确！");
						
						for(int rd: cd.dropIDs)
							checkRandomDropIDValid(gdCfgs, row, rd);
						
						bossCfg.rankDrops.add(cd);
						rank++;
					}
					
					if(demonHoleCfg.bosses.put(bossCfg.bossID, bossCfg) != null)
						throw new Exception("row " + row + " 伏魔洞bossID " + bossCfg.bossID + " 重复!");
					
					row++;
					col = 0;
				}
			}
			
			gdCfgs.demonHole = demonHoleCfg;
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadMapTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			
			Map<Integer, List<Integer>> trapgroups = new TreeMap<>();
			for (SBean.TrapExpandedCFGS e : gdCfgs.traps.values())
			{
				trapgroups.compute(e.groupID, (k, v) ->
				{
					if (v == null)
						v = new ArrayList<>();
					v.add(e.id);
					return v;
				});
			}
			// 地图总表
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.MapClusterCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Set<Integer> spawnAreas = new TreeSet<>();
				Set<Integer> npcs = new TreeSet<>();
				Set<Integer> mines = new TreeSet<>();
				Set<Integer> waypoints = new TreeSet<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.MapClusterCFGS cfg = new SBean.MapClusterCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.name = excelReadContext.getStringValue(row, col++);
					col += 1;
					cfg.type = excelReadContext.getIntValue(row, col++);
					
					int battleID = excelReadContext.getIntValue(row, col++);
					Integer patchPacket = battleMapPackets.get(battleID);
					if(patchPacket == null)
						throw new Exception("row " + row + " 地图 [" + cfg.id + " ," + cfg.name + "] 对应的战役地图ID " + battleID + " 非法!");
					cfg.patchPacket = patchPacket;
					
					switch (cfg.type)
					{
						case GameData.MAP_TYPE_MAP_WORLD:		
						case GameData.MAP_TYPE_MAPCOPY_NORMAL:		
						case GameData.MAP_TYPE_MAPCOPY_SECT:		
						case GameData.MAP_TYPE_MAPCOPY_ARENA:		
						case GameData.MAP_TYPE_MAPCOPY_CLAN_TASK:
						case GameData.MAP_TYPE_MAPCOPY_CLAN_ORE:
						case GameData.MAP_TYPE_MAPCOPY_ACTIVITY:
						case GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLE:
						case GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLEHELP:
						case GameData.MAP_TYPE_MAPCOPY_BWARENA:	
						case GameData.MAP_TYPE_MAPCOPY_PETLIFE:
						case GameData.MAP_TYPE_MAPCOPY_CLIMBTOWER:
						case GameData.MAP_TYPE_MAPCOPY_NOVICE_GUIDE:
						case GameData.MAP_TYPE_MAPCOPY_SECT_GROUP:		
						case GameData.MAP_TYPE_MAPCOPY_WEAPON:
						case GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE:
						case GameData.MAP_TYPE_MAPCOPY_JUSTICE:
						case GameData.MAP_TYPE_MAPCOPY_EMERGENCY:
						case GameData.MAP_TYPE_MAPCOPY_FIGHTNPC:
						case GameData.MAP_TYPE_MAPCOPY_TOWER_DEFENCE:
							break;
						case GameData.MAP_TYPE_MAPCOPY_SUPERARENA:
						case GameData.MAP_TYPE_MAPCOPY_FORCEWAR:
//							if(patchPacket != 0)
//								throw new Exception("row " + row + " 势力战或会武 地图 [" + cfg.id + " ," + cfg.name + "] 对应的战役地图ID " + battleID + " 打包序号 " + patchPacket + " 非法!");
							break;
						default:
							throw new Exception("row " + row + " 地图总表 地图类型 " + cfg.type + " 非法!");
					}
					
					col += 1;
					cfg.spawnAreas = excelReadContext.getIntegerList(row, col++, ";");
					for (int sp : cfg.spawnAreas)
					{
						//一个刷怪点个地图上用
						if (!spawnAreas.add(sp))
							throw new Exception("row " + row + " 刷怪区域 " + sp + "被重复使用！");
						
						area2map.put(sp, cfg.id);
					}
					if (cfg.type == GameData.MAP_TYPE_MAPCOPY_SECT && cfg.spawnAreas.size() > 1)
						throw new Exception("row " + row + " 帮派地图ID " + cfg.id + " 刷怪区域 > 1");
					cfg.spawnPoints = new HashSet<>();
					
					List<Integer> trapGroupIDs = excelReadContext.getIntegerList(row, col++, ";");
					Set<Integer> trapGroupsIDSet = new TreeSet<>(trapGroupIDs);
					if (trapGroupIDs.size() != trapGroupsIDSet.size())
						throw new Exception("row " + row + " 地图ID " + cfg.id + " Trap 组 ID重复!");
					
					//同一场景BUFF点可以用于多个地图
					List<Integer> mapBuffs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.mapBuffs = new HashSet<>();
					for(int mapBuff: mapBuffs)
					{
						if(!cfg.mapBuffs.add(mapBuff))
							throw new Exception("row " + row + " 地图ID " + cfg.id + " 场景BUFF ID " + mapBuff + " 重复!");
						
						mapbuff2map.put(mapBuff, cfg.id);
					}
					
					List<Integer> npcPoints = excelReadContext.getIntegerList(row, col++, ";");
					cfg.npcs = new HashSet<>();
					for (int npc : npcPoints)
					{
						if (!cfg.npcs.add(npc))
							throw new Exception("row " + row + " 地图ID " + cfg.id + " NPC点 ID " + npc + " 重复!");
						
						//一个npc点只能在一个地图上用
						if (!npcs.add(npc))
							throw new Exception("row " + row + " npc " + npc + "被重复使用！");
						
						npc2map.put(npc, cfg.id);
					}
					
					List<Integer> mineralPoints = excelReadContext.getIntegerList(row, col++, ";");
					cfg.minerals = new HashSet<>();
					for (int mine : mineralPoints)
					{
						if (!cfg.minerals.add(mine))
							throw new Exception("row " + row + " 地图ID " + cfg.id + " 矿产点 ID " + mine + " 重复!");
						
						//一个矿物只能在一个地图上用
						if (!mines.add(mine))
							throw new Exception("row " + row + " 矿产 " + mine + "被重复使用！");
						
						mineral2map.put(mine, cfg.id);
					}
					
					List<Integer> wayPoints = excelReadContext.getIntegerList(row, col++, ";");
					cfg.wayPoints = new HashSet<>();
					for (int waypoint : wayPoints)
					{
						if (!cfg.wayPoints.add(waypoint))
							throw new Exception("row " + row + " 地图ID " + cfg.id + " 传送点 ID " + waypoint + " 重复!");
						
						//一个传送点只能在一个地图上用
						if (!waypoints.add(waypoint))
							throw new Exception("row " + row + " 传送点 " + waypoint + "被重复使用！");
						
						wayPoint2map.put(waypoint, cfg.id);
					}
					cfg.spawnPos = excelReadContext.getVector3(row, col++, ";");
					cfg.spawnPos2nd = excelReadContext.getVector3(row, col++, ";");
					{
						cfg.spawnRotation = new SBean.Vector3F(1.0f, 0, 0);
						if(cfg.spawnPos2nd.x != 0 || cfg.spawnPos2nd.z != 0)
							cfg.spawnRotation = new GVector3(cfg.spawnPos2nd).diffence2D(new GVector3(cfg.spawnPos)).normalize().toVector3F();
					}
					cfg.revivePos = excelReadContext.getVector3(row, col++, ";");
					cfg.revivePos2nd = excelReadContext.getVector3(row, col++, ";");
					
					col = colStart + 18;
					SBean.Vector3 minPos = excelReadContext.getVector3(row, col++, ";");
					SBean.Vector3 maxPos = excelReadContext.getVector3(row, col++, ";");
					cfg.minX = minPos.x;
					cfg.minZ = minPos.z;
					cfg.maxX = maxPos.x;
					cfg.maxZ = maxPos.z;
					if(cfg.minX >= cfg.maxX || cfg.minZ >= cfg.maxZ)
						throw new Exception("row " + row  + " 地图 " + cfg.id + " 有效范围非法！minPos " + GameData.toString(minPos) + " maxPos " + GameData.toString(maxPos));
					
					cfg.traps = new HashSet<>();
					for (int trapgroup : trapGroupIDs)
					{
						//同一trap组可以用于多个地图
						List<Integer> gtraps = trapgroups.get(trapgroup);
						if (gtraps == null)
							throw new Exception("row " + row + " 地图ID " + cfg.id + " Trap 组 ID " + trapgroup + " 不存在!");
						
						for(int trap: gtraps)
						{
							if(!cfg.traps.add(trap))
								throw new Exception("row " + row + " 地图ID " + cfg.id + " Trap 组 ID " + trapgroup + "中陷阱ID " + trap + " 重复!");
							
							SBean.TrapExpandedCFGS tCfg = gdCfgs.traps.get(trap);
							if(tCfg == null)
								throw new Exception("row " + row + " 地图ID " + cfg.id + " Trap 组 ID " + trapgroup + " 陷阱 " + trap + " 不存在！");
							
							checkMapPosValid(row, cfg, tCfg.position, "陷阱");
						}
					}
					
					checkMapPosValid(row, cfg, cfg.spawnPos, "出生点");
					checkMapPosValid(row, cfg, cfg.spawnPos2nd, "第二出生点");
					checkMapPosValid(row, cfg, cfg.revivePos, "复活点");
					checkMapPosValid(row, cfg, cfg.revivePos2nd, "第二复活点");
					
					cfg.center = excelReadContext.getVector3(row, col++, ";");
					cfg.aiMaxDisToCenter = excelReadContext.getIntValue(row, col++);
					
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("地图ID " + cfg.id + " 重复");

					row++;
					col = colStart;
				}
				gdCfgs.mapClusters = cfgs;
			}

			excelReadContext.ReadSheet(1);
			{
				Map<Integer, SBean.SpawnAreaCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Set<Integer> set = new TreeSet<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SpawnAreaCFGS spawnAreaCfg = new SBean.SpawnAreaCFGS();
					spawnAreaCfg.id = excelReadContext.getIntValue(row, col++);
					int mapID = area2map.getOrDefault(spawnAreaCfg.id, 0);
//					SBean.MapClusterCFGS mcCfg = gdCfgs.mapClusters.get(mapID);
//					if(mcCfg == null)
//						throw new Exception("row " + row + " 刷怪区域  " + spawnAreaCfg.id + " 不在任何地图上");
					
					spawnAreaCfg.checkRadius = excelReadContext.getIntValue(row, col++);
					spawnAreaCfg.hasTripDoor = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, spawnAreaCfg.hasTripDoor);
					spawnAreaCfg.trapDoorIDs = excelReadContext.getIntegerList(row, col++, ";");
					spawnAreaCfg.spawnOpen = excelReadContext.getIntegerList(row, col++, ";");
					spawnAreaCfg.spawnClose = excelReadContext.getIntegerList(row, col++, ";");
					spawnAreaCfg.clearOpen = excelReadContext.getIntegerList(row, col++, ";");
					spawnAreaCfg.clearClose = excelReadContext.getIntegerList(row, col++, ";");

					col = colStart + 8;
					spawnAreaCfg.spawnPoint = excelReadContext.getIntegerList(row, col++, ";");
					for (int sp : spawnAreaCfg.spawnPoint)
					{
						//一个刷怪点只能在一个地图上用
						if (!set.add(sp))
							throw new Exception("row " + row + " 刷怪点 " + sp + "被重复使用！");
						
						point2map.put(sp, mapID);
					}
					
					col = colStart + 10;
					spawnAreaCfg.delaySpawnTime = excelReadContext.getIntValue(row, col++);
					spawnAreaCfg.paths = new ArrayList<>();
					
					if (cfgs.put(spawnAreaCfg.id, spawnAreaCfg) != null)
						throw new Exception("副本刷怪区域id重复， id = " + spawnAreaCfg.id);
					row++;
					col = colStart;
				}
				gdCfgs.spawnAreas = cfgs;
			}
			
			excelReadContext.ReadSheet(2);
			{
				Map<Integer, SBean.SpawnPointCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SpawnPointCFGS spawnPointCfg = new SBean.SpawnPointCFGS();
					spawnPointCfg.id = excelReadContext.getIntValue(row, col++);
					int mapID = point2map.getOrDefault(spawnPointCfg.id, 0);
					SBean.MapClusterCFGS mcCfg = gdCfgs.mapClusters.get(mapID);
//					if(mcCfg == null)
//						throw new Exception("row " + row + " 刷怪点  " + spawnPointCfg.id + " 不在任何地图上");
					
					spawnPointCfg.position = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcCfg, spawnPointCfg.position, "刷挂点");
					spawnPointCfg.isRandom = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, spawnPointCfg.isRandom);
					spawnPointCfg.randomRadius = excelReadContext.getIntValue(row, col++);
					if(spawnPointCfg.isRandom == 1 && spawnPointCfg.randomRadius == 0)
						throw new Exception("row  " + row + " 刷怪点 是否随机位置: " + spawnPointCfg.isRandom + " 随机半径:" + spawnPointCfg.randomRadius);
					spawnPointCfg.monsterID = excelReadContext.getIntValue(row, col++);
					this.checkMonsterID(gdCfgs, row, spawnPointCfg.monsterID);
					spawnPointCfg.spawnType = excelReadContext.getIntValue(row, col++);
					spawnPointCfg.interval = excelReadContext.getIntValue(row, col++);
					spawnPointCfg.spawnTimes = excelReadContext.getIntValue(row, col++);
					spawnPointCfg.spawnNum = new ArrayList<>();
					for (int i = 0; i < 9; i++)
					{
						if (excelReadContext.isNotEmpty(row, col))
							spawnPointCfg.spawnNum.add(excelReadContext.getIntValue(row, col++));
						else
							break;
					}

					switch (spawnPointCfg.spawnType)
					{
					case GameData.BYORDER:
						if(spawnPointCfg.spawnTimes != spawnPointCfg.spawnNum.size())
							throw new Exception("row " + row + " 刷怪点 " + spawnPointCfg.id + " 刷怪方式 " + spawnPointCfg.spawnType + " 顺序刷怪 ， 刷怪次数 " + spawnPointCfg.spawnTimes + " 和实际刷怪波数 " + spawnPointCfg.spawnNum.size() + " 不一致");
						break;
					case GameData.BYTIME:
					case GameData.BYNUMBER:
						break;
					default:
						throw new Exception("row " + row + " 不支持的刷怪方式  " + spawnPointCfg.spawnType + " !");
					}
					
					col = colStart + 18;
					spawnPointCfg.rotationType = excelReadContext.getByteValue(row, col++);
					spawnPointCfg.rotation =excelReadContext.getVector3F(row, col++, ";");
					if (cfgs.put(spawnPointCfg.id, spawnPointCfg) != null)
						throw new Exception("副本刷怪点 id重复，id = " + spawnPointCfg.id);
					row++;
					col = colStart;
				}
				gdCfgs.spawnPoints = cfgs;
			}
			
			excelReadContext.ReadSheet(3);
			{
				Map<Integer, SBean.NpcPointCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.NpcPointCFGS cfg = new SBean.NpcPointCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					int mapID = npc2map.getOrDefault(cfg.id, 0);
					SBean.MapClusterCFGS mcCfg = gdCfgs.mapClusters.get(mapID);
					if(mcCfg == null)
						throw new Exception("row " + row + " npc点 " + cfg.id + " 不在任何地图上");
					
					cfg.position = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcCfg, cfg.position, "npc点");
					cfg.rotation = excelReadContext.getVector3F(row, col++, ";");
					cfg.relatedID = excelReadContext.getIntValue(row, col++);
					this.checkNPCID(gdCfgs, row, cfg.relatedID);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("副本NPC点id重复， id = " + cfg.id);
					row++;
					col = colStart;
				}
				gdCfgs.npcPoints = cfgs;
			}
			excelReadContext.ReadSheet(4);
			{
				Map<Integer, SBean.MineralPointCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.MineralPointCFGS cfg = new SBean.MineralPointCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					int mapID = mineral2map.getOrDefault(cfg.id, 0);
					SBean.MapClusterCFGS mcCfg = gdCfgs.mapClusters.get(mapID);
					if(mcCfg == null)
						throw new Exception("row " + row + " 矿点 " + cfg.id + " 不在任何地图上");
					
					cfg.position = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcCfg, cfg.position, "矿点");
					cfg.rotation = excelReadContext.getVector3F(row, col++, ";");
					cfg.relatedID = excelReadContext.getIntValue(row, col++);
					this.checkMineralID(gdCfgs, row, cfg.relatedID);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("副本矿点id重复， id = " + cfg.id);
					row++;
					col = colStart;
				}
				gdCfgs.mineralPoints = cfgs;
			}
			excelReadContext.ReadSheet(5);
			{
				Map<Integer, SBean.WayPointCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.WayPointCFGS cfg = new SBean.WayPointCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.position = excelReadContext.getVector3(row, col++, ";");
					cfg.target = new SBean.MapPosition();
					cfg.target.mapID = excelReadContext.getIntValue(row, col++);
						
					cfg.target.position = excelReadContext.getVector3(row, col++, ";");
					col = colStart + 5;
					cfg.trigRadius = excelReadContext.getIntValue(row, col++);
					col++;
					cfg.pointFunc = excelReadContext.getIntValue(row, col++);
					switch(cfg.pointFunc)
					{
					case GameData.WAYPOINT_FUNC_NORMAL:
					case GameData.WAYPOINT_FUNC_EMERGENCY:
						break;
					default:
						throw new Exception("row " + row + " 传送点功能 " + cfg.pointFunc + " 无效");
					}
					if (cfg.pointFunc == GameData.WAYPOINT_FUNC_NORMAL)
					{
						int mapID = wayPoint2map.getOrDefault(cfg.id, 0);
						SBean.MapClusterCFGS mcCfg = gdCfgs.mapClusters.get(mapID);
						if (mcCfg == null)
							throw new Exception("row " + row + " 传送点 " + cfg.id + " 不在任何地图上");
						checkMapPosValid(row, mcCfg, cfg.position, "传送点");
						SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.target.mapID);
						if (mapclustercfg == null)
							throw new Exception("传送点ID " + cfg.id + " 的目的地图 " + cfg.target.mapID + " 在地图总表中不存在！");
						if (mapclustercfg.type != GameData.MAP_TYPE_MAP_WORLD)
							throw new Exception("传送点ID " + cfg.id + " 的目的地图 " + cfg.target.mapID + " 不是世界地图！");
						checkMapPosValid(row, mapclustercfg, cfg.target.position, "传送目的地坐标");
					}
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("传送点id重复， id = " + cfg.id);

					row++;
					col = colStart;
				}
				gdCfgs.wayPoints = cfgs;
			}

			excelReadContext.ReadSheet(7);
			{
				Map<Integer, SBean.MapBuffPointCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.MapBuffPointCFGS cfg = new SBean.MapBuffPointCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					int mapID = mapbuff2map.getOrDefault(cfg.id, 0);
					SBean.MapClusterCFGS mcCfg = gdCfgs.mapClusters.get(mapID);
					if(mcCfg == null)
						throw new Exception("row " + row + " 场景BUFF " + cfg.id + " 不在任何地图上");
					
					cfg.position = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcCfg, cfg.position, "场景BUFF");
					cfg.mapBuffID = excelReadContext.getIntValue(row, col++);
					this.checkMapBuffIDValid(gdCfgs, row, cfg.mapBuffID);

					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("地图场景BUFFID " + cfg.id + " 重复");

					row++;
					col = colStart;
				}
				gdCfgs.mapBuffPoints = cfgs;
			}

//			Set<Integer> monster = new HashSet<>();
			//校验地图上的刷怪区
			for (SBean.MapClusterCFGS mccfg : gdCfgs.mapClusters.values())
			{
				for (int aid : mccfg.spawnAreas)
				{
					SBean.SpawnAreaCFGS sacfg = gdCfgs.spawnAreas.get(aid);
					if (sacfg == null)
						throw new Exception("地图  " + mccfg.id + " 上的刷怪区域  " + aid + " 不存在!");
					
					for(int pid: sacfg.spawnPoint)
					{
						if(!mccfg.spawnPoints.add(pid))
							throw new Exception("地图  " + mccfg.id + " 上的刷怪区域  " + aid + " 刷怪点 " + pid + " 重复!");
						
						SBean.SpawnPointCFGS spcfg = gdCfgs.spawnPoints.get(pid);
						if(spcfg == null)
							throw new Exception("地图  " + mccfg.id + " 上的刷怪区域  " + aid + " 刷怪点 " + pid + " 不存在!");
						
//						if(!monster.add(spcfg.monsterID))
//							throw new Exception("地图  " + mccfg.id + " 上的刷怪区域  " + aid + " 刷怪点 " + pid + " 对应的怪物  " + spcfg.monsterID + " 在多个地图中重复使用！");
					}
				}

				for (int npcid : mccfg.npcs)
				{
					if (!gdCfgs.npcPoints.containsKey(npcid))
						throw new Exception("地图  " + mccfg.id + " 上的NPC点  " + npcid + " 不存在!");
				}
				for (int mineid : mccfg.minerals)
				{
					if (!gdCfgs.mineralPoints.containsKey(mineid))
						throw new Exception("地图  " + mccfg.id + " 上的矿物点  " + mineid + " 不存在!");
				}
				for (int wid : mccfg.wayPoints)
				{
					if (!gdCfgs.wayPoints.containsKey(wid))
						throw new Exception("地图  " + mccfg.id + " 上的传送点  " + wid + " 不存在!");
				}
				for (int mbid : mccfg.mapBuffs)
				{
					if (!gdCfgs.mapBuffPoints.containsKey(mbid))
						throw new Exception("地图  " + mccfg.id + " 上的场景BUFF  " + mbid + " 不存在!");
				}
			}
			//校验刷怪区域上的刷怪点
			for (SBean.SpawnAreaCFGS areacfg : gdCfgs.spawnAreas.values())
			{
				for (int pid : areacfg.spawnPoint)
				{
					if (!gdCfgs.spawnPoints.containsKey(pid))
						throw new Exception("刷怪区域  " + areacfg.id + " 上的刷怪点  " + pid + " 不存在!");
				}
			}
			
			// 野外场景
			excelReadContext.ReadSheet(8);
			{
				Map<Integer, SBean.WorldMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.WorldMapCFGS cfg = new SBean.WorldMapCFGS(0, new ArrayList<>(), (byte)0, 0, new ArrayList<>(), 0);
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("野外场景地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAP_WORLD)
						throw new Exception("野外场景地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");

					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("野外场景地图ID " + cfg.id + " 重复");
					
					Set<Integer> npcIDs = new HashSet<>();
					for(int point: mapclustercfg.npcs)
					{
						SBean.NpcPointCFGS npcPointCfg = gdCfgs.npcPoints.get(point);
						if(npcPointCfg == null)
							throw new Exception("野外场景地图ID " + cfg.id + " NPC点 " + point + " 非法!");
						
						if(!npcIDs.add(npcPointCfg.relatedID))
							throw new Exception("野外场景地图ID " + cfg.id + " NPC点 " + point + " 关联的NPC " + npcPointCfg.relatedID + " 重复!");
						
						SBean.NpcCFGS npcCfg = gdCfgs.npcs.get(npcPointCfg.relatedID);
						if(npcCfg == null)
							throw new Exception("野外场景地图ID " + cfg.id + " NPC点 " + point + " 关联的NPC " + npcPointCfg.relatedID + " 非法!");
						
						if(npcCfg.funcs.contains(GameData.NPC_FUNCTION_ENTER_WEAPONMAP))
							cfg.weaponMapNpcs.add(npcPointCfg.relatedID);
					}
					
					col = colStart + 7;  // source excel file add another 3 colmuns for client use
					for(int i=0; i<5; i++)
					{
						SBean.Vector3 p = excelReadContext.getVector3(row, col++, ";");
						if(p.x <= mapclustercfg.minX ||p.x >= mapclustercfg.maxX || p.z <= mapclustercfg.minZ || p.z >= mapclustercfg.maxZ)
							throw new Exception("row " + row + " 野外场景地图ID " + cfg.id + " 随机传送点 " + (i + 1) + " 不在地图范围内！");
						checkMapPosValid(row, mapclustercfg, p, "随机传送点");
						cfg.randomPos.add(p);
					}
					
					col = colStart + 15;
					cfg.pkType = excelReadContext.getByteValue(row, col++);
					switch (cfg.pkType)
					{
					case GameData.MAP_PKTYPE_NORMAL:
					case GameData.MAP_PKTYPE_SAFE:
					case GameData.MAP_PKTYPE_SECT:
					case GameData.MAP_PKTYPE_KILL:
						break;
					default:
						throw new Exception("row " + row + " 野外场景地图ID " + cfg.id + "PK类型 + " + cfg.pkType + "非法!");
					}
					
					col = colStart + 18;
					cfg.safeReviveMap = excelReadContext.getIntValue(row, col++);
					col++;
					cfg.worldNum = excelReadContext.getIntValue(row, col++);
					
					row++;
					col = colStart;
				}
				
				for(SBean.WorldMapCFGS w: cfgs.values())
				{
					SBean.WorldMapCFGS safe = cfgs.get(w.safeReviveMap);
					if(safe == null || safe.pkType != GameData.MAP_PKTYPE_SAFE)
						throw new Exception("野外场景地图" + w.id + " 安全复活场景ID " + w.safeReviveMap + " 非法");
				}
				
				gdCfgs.worldMaps = cfgs;
			}

			// 副本地图
			excelReadContext.ReadSheet(9);
			{
				Map<Integer, SBean.MapCopyCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.MapCopyCFGS cfg = new SBean.MapCopyCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("副本地图ID " + cfg.id + " 在地图总表中不存在");
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_NORMAL)
						throw new Exception("副本地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					col += 1;
					cfg.openType = excelReadContext.getIntValue(row, col++);
					switch (cfg.openType)
					{
					case GameData.MAPCOPY_OPEN_TYPE_PRIVATE:
						break;
					case GameData.MAPCOPY_OPEN_TYPE_PUBLIC:
						for(int areaID: mapclustercfg.spawnAreas)
						{
							SBean.SpawnAreaCFGS areaCfg = gdCfgs.spawnAreas.get(areaID);
							if(areaCfg == null)
								throw new Exception("副本 " + cfg.id + " 刷怪区域ID " + areaID + " 非法!");
							
							SBean.MapCopySpawnPath pCfg = allSpawnPaths.get(areaID); //TODO
							if(pCfg != null)
							{
								for(SBean.Vector3 pos: pCfg.points)
								{
									checkMapPosValid(row, mapclustercfg, pos, "副本 " + cfg.id + " 刷怪区域ID " + areaID + "寻路点");
									areaCfg.paths.add(pos);
								}
							}
						}
						break;
					default:
						throw new Exception("副本地图ID " + cfg.id + " 不支持的开启类型  " + cfg.openType + " !");
					}
					cfg.winCondition = excelReadContext.getIntValue(row, col++);
					checkMapCopyWinCondition(gdCfgs, row, cfg.id, cfg.winCondition);
					cfg.limitTimes = excelReadContext.getIntValue(row, col++);
					cfg.costVit = excelReadContext.getIntValue(row, col++);
					if (cfg.openType == GameData.MAPCOPY_OPEN_TYPE_PUBLIC && cfg.costVit > 0)
						throw new Exception("row " + row + " 多人副本不能消耗体力，消耗体力值需要为0！");

					cfg.needLevel = excelReadContext.getIntValue(row, col++);
					cfg.minPlayers = excelReadContext.getIntValue(row, col++);
					cfg.maxPlayers = excelReadContext.getIntValue(row, col++);
					cfg.preTask = excelReadContext.getIntValue(row, col++);
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					if (cfg.maxTime <= 0)
						throw new Exception("副本地图ID " + cfg.id + " 最大时长不是大于0 的值!");
					col++;
					cfg.groupID = excelReadContext.getIntValue(row, col++);
					cfg.difficulty = excelReadContext.getIntValue(row, col++);
					
					for (SBean.MapCopyCFGS e : cfgs.values())
					{
						if (e.groupID == cfg.groupID && e.difficulty == cfg.difficulty)
							throw new Exception("副本地图ID " + cfg.id + " 组编号和难度类型存在重复项!");
					}
					cfg.startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					cfg.endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					cfg.preMapCopy = excelReadContext.getIntValue(row, col++);
					if (cfg.preMapCopy > 0)
					{
						SBean.MapClusterCFGS mccfg = gdCfgs.mapClusters.get(cfg.id);
						if (mccfg == null)
							throw new Exception("副本地图ID " + cfg.id + " 的前置地图 " + cfg.preMapCopy + " 在地图总表中不存在");
						if (mccfg.type != GameData.MAP_TYPE_MAPCOPY_NORMAL)
							throw new Exception("副本地图ID " + cfg.id + " 的前置地图 " + cfg.preMapCopy + " 的配置类型与当前地图类型不一致!");
					}
					cfg.fightPower = excelReadContext.getIntValue(row, col++);
					cfg.sweepLvl = excelReadContext.getIntValue(row, col++);
					cfg.sweepScore = excelReadContext.getIntValue(row, col++);
					cfg.rewardExp = excelReadContext.getIntValue(row, col++);
					cfg.fixedDropID = excelReadContext.getIntValue(row, col++);
					this.checkFixedDropIDValid(gdCfgs, row, cfg.fixedDropID);
					cfg.turnPlateDropID = excelReadContext.getIntValue(row, col++);
					this.checkNoDuplicateDropIDValid(gdCfgs, row, cfg.turnPlateDropID);
					
					cfg.allMonsters = new TreeMap<>();
					cfg.allTraps = new TreeMap<>();
					cfg.bosses = new TreeMap<>();
					megerMapCopyEntities(gdCfgs, mapclustercfg, cfg.id, cfg.allMonsters, cfg.allTraps, cfg.bosses, true);
					switch (cfg.winCondition)
					{
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_BOSSES:
						if(cfg.bosses.isEmpty())
							throw new Exception("row " + row + " 副本地图 " + cfg.id + " 胜利条件为 " + cfg.winCondition + " 击杀boss, 没有配置boss");
						break;
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT:
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_MONSTER_COUNT:
						if(cfg.allMonsters.isEmpty())
							throw new Exception("row " + row + " 副本地图 " + cfg.id + " 胜利条件为 " + cfg.winCondition + " 没有配置怪物");
						break;
					default:
						throw new Exception("row " + row + " 副本地图 " + cfg.id + " 胜利条件 非法!");
					}
					
					
					col = colStart + 35;
					cfg.slowMotionTime = excelReadContext.getIntValue(row, col++);
					
					col = colStart + 42;
					cfg.masterApprenticePoint = excelReadContext.getIntValue(row, col++);
					cfg.masterMasterPoint = excelReadContext.getIntValue(row, col++);
					cfg.masterMasterReputation = excelReadContext.getIntValue(row, col++);
					
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("副本地图ID " + cfg.id + " 重复");
					row++;
					col = colStart;
				}
				gdCfgs.mapcopys = cfgs;
			}
			
			// 帮派、战场地图
			excelReadContext.ReadSheet(10);
			{
				Map<Integer, SBean.SectMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SectMapCFGS cfg = new SBean.SectMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("帮派副本地图ID " + cfg.id + " 在地图总表中不存在");
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_SECT)
						throw new Exception("帮派副本地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					Set<Integer> spawnPoints = new TreeSet<>();
					for (int areaID : mapclustercfg.spawnAreas)
					{
						SBean.SpawnAreaCFGS areaCfg = gdCfgs.spawnAreas.get(areaID);
						if (areaCfg == null)
							throw new Exception("地图ID " + cfg.id + " 刷怪区域  " + areaID + " 不存在！");
						spawnPoints.addAll(areaCfg.spawnPoint);
					}
					
					cfg.dayEnterTimes = excelReadContext.getIntValue(row, col++);
					cfg.enterCostVit = excelReadContext.getIntValue(row, col++);
					cfg.resetTimes = excelReadContext.getIntValue(row, col++);
					cfg.preMapId = excelReadContext.getIntValue(row, col++);
					if (cfg.preMapId > 0)
					{
						SBean.MapClusterCFGS mccfg = gdCfgs.mapClusters.get(cfg.id);
						if (mccfg == null)
							throw new Exception("帮派副本地图ID " + cfg.id + " 的前置地图 " + cfg.preMapId + " 在地图总表中不存在");
						if (mccfg.type != GameData.MAP_TYPE_MAPCOPY_SECT)
							throw new Exception("帮派副本地图ID " + cfg.id + " 的前置地图 " + cfg.preMapId + " 的配置类型与当前地图类型不一致!");
					}
					cfg.openCostVit = excelReadContext.getIntValue(row, col++);
					cfg.lvlReq = excelReadContext.getIntValue(row, col++);
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					if (cfg.maxTime <= 0)
						throw new Exception("副本地图ID " + cfg.id + " 最大时长不是大于0 的值!");
					col += 1;
					cfg.startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					cfg.endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					col += 2;
					cfg.boss = excelReadContext.getIntValue(row, col++);
					{
						SBean.SpawnPointCFGS point = gdCfgs.spawnPoints.get(cfg.boss);
						if (point == null || !spawnPoints.contains(cfg.boss))
							throw new Exception("帮派地图  Boss 刷怪点ID " + cfg.boss + " 不在当前地图上！");
						if (point.spawnNum.get(0) > 1)
							throw new Exception("帮派地图  Boss 刷怪点ID " + cfg.boss  + " 怪物数量 > 1");
					}
					cfg.monsters = new ArrayList<>();
					for (int i = 0; i < 6; i++)
					{
						int mon = excelReadContext.getIntValue(row, col++);
						if (mon > 0)
						{
							cfg.monsters.add(mon);
							SBean.SpawnPointCFGS point = gdCfgs.spawnPoints.get(mon);
							if (point == null || !spawnPoints.contains(mon))
								throw new Exception("帮派地图  第" + (i + 1) + " 刷怪点ID " + mon + " 不在当前地图上！");

							if (point.spawnNum.get(0) > 1)
								throw new Exception("帮派地图  第" + (i + 1) + " 刷怪点ID " + mon  + " 怪物数量 > 1");
						}
					}

					int exReward50 = excelReadContext.getIntValue(row, col++);
					int exReward100 = excelReadContext.getIntValue(row, col++);
					cfg.extraRewards = new TreeMap<>();
					cfg.extraRewards.put(5000, exReward50);
					cfg.extraRewards.put(10000, exReward100);
					String factors = excelReadContext.getStringValue(row, col++);
					cfg.moneyRewardFactor = new ArrayList<>();
					for (String f : factors.split(";"))
					{
						String[] factor = f.split(",");
						SBean.FactorCFGS lastCfgs = cfg.moneyRewardFactor.size() > 0 ? cfg.moneyRewardFactor.get(cfg.moneyRewardFactor.size() - 1) : null;
						int hp = Integer.parseInt(factor[0]);
						if (lastCfgs != null && hp < lastCfgs.maxHp)
							throw new Exception("帮派地图地图ID " + cfg.id + " 的血量奖励系数血量必须递增");
						float factorF = Float.parseFloat(factor[1]);
						if (lastCfgs != null && factorF > lastCfgs.factor)
							throw new Exception("帮派地图地图ID " + cfg.id + " 的血量奖励系数系数必须递减");
						cfg.moneyRewardFactor.add(new SBean.FactorCFGS(hp, factorF));
					}
					cfg.progressRewards = new TreeMap<>();
					for (int i = 0; i < 10; ++i)
					{
						String str = excelReadContext.getStringValue(row, col++);
						if (!str.isEmpty())
						{
							String[] strs = str.split(";");
							SBean.ProgressRewards rewards = new SBean.ProgressRewards(new TreeMap<>());
							for (String s : strs)
							{
								String[] rewardstr = s.split(",");
								if (rewardstr.length != 2)
									throw new Exception("row " + row + " col " + col + " 节点奖励格式不正确！");
								try
								{
									int iid = Integer.parseInt(rewardstr[0]);
									checkEntityIDValid(gdCfgs, row, iid, false);
									int icount = Integer.parseInt(rewardstr[1]);
									if (icount <= 0 || icount >= 100)
										throw new Exception("row " + row + " col " + col + " 节点奖励物品个数不在1到99之间！");
									rewards.rewards.put(iid, icount);
								}
								catch (Exception e)
								{
									throw new Exception("row " + row + " col " + col + " 节点奖励非法！");
								}
							}
							cfg.progressRewards.put((i + 1) * 1000, rewards);
						}
					}
					cfg.slowMotionTime = excelReadContext.getIntValue(row, col++);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("帮派副本地图ID " + cfg.id + " 重复");
					row++;
					col = colStart;
				}
				gdCfgs.sectmaps = cfgs;
			}
			
			// 单人竞技场
			excelReadContext.ReadSheet(11);
			{
				Map<Integer, SBean.ArenaMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ArenaMapCFGS cfg = new SBean.ArenaMapCFGS(0, new ArrayList<>(), new ArrayList<>());
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("竞技场地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_ARENA)
						throw new Exception("竞技场地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");

					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("竞技场地图ID " + cfg.id + " 重复");
					
					col++;
					for(int i=0; i<GameData.PET_FIGHT_MAX_USE; i++)
						cfg.selfPetPos.add(excelReadContext.getVector3(row, col++, ";"));
					
					for(int i=0; i<GameData.PET_FIGHT_MAX_USE; i++)
						cfg.enemyPetPos.add(excelReadContext.getVector3(row, col++, ";"));
					
					row++;
					col = colStart;
				}
				gdCfgs.arenamaps = cfgs;
			}
			// 宗门任务遭遇战
			excelReadContext.ReadSheet(12);
			{
				Map<Integer, SBean.ClanTaskMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanTaskMapCFGS cfg = new SBean.ClanTaskMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("宗门任务遭遇战地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_CLAN_TASK)
						throw new Exception("宗门任务遭遇战地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("宗门任务遭遇战地图ID " + cfg.id + " 重复");
					row++;
					col = colStart;
				}
				gdCfgs.clanTaskMaps = cfgs;
			}
			// 宗门抢矿
			excelReadContext.ReadSheet(13);
			{
				Map<Integer, SBean.ClanOreMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanOreMapCFGS cfg = new SBean.ClanOreMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("宗门夺矿战地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_CLAN_ORE)
						throw new Exception("宗门夺矿战地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("宗门夺矿地图ID " + cfg.id + " 重复");
					row++;
					col = colStart;
				}
				gdCfgs.clanOreMaps = cfgs;
			}
			
			
			//活动副本
			excelReadContext.ReadSheet(14);
			{
				Map<Integer, SBean.ActivityMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ActivityMapCFGS cfg = new SBean.ActivityMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("活动副本地图ID " + cfg.id + " 在地图总表中不存在");
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_ACTIVITY)
						throw new Exception("活动副本地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					col += 1;
					cfg.groupId = excelReadContext.getIntValue(row, col++);
					SBean.ActivityMapGroupCFGS gcfg = gdCfgs.actMapGroups.get(cfg.groupId);
					if (gcfg == null)
						throw new Exception("活动副本地图ID " + cfg.id + " 组ID " + cfg.groupId + " 在活动表中不存在!");
					gcfg.maps.add(cfg.id);
					cfg.difficulty = excelReadContext.getIntValue(row, col++);
					cfg.winCondition = excelReadContext.getIntValue(row, col++);
					if(cfg.winCondition != GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT)
						throw new Exception("row " + row + " 活动副本 " + cfg.id + " 胜利条件 " + cfg.winCondition + " 非法！");
					checkMapCopyWinCondition(gdCfgs, row, cfg.id, cfg.winCondition);
					cfg.costVit = excelReadContext.getIntValue(row, col++);
					cfg.lvlReq = excelReadContext.getIntValue(row, col++);
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					if (cfg.maxTime <= 0)
						throw new Exception("活动副本地图ID " + cfg.id + " 最大时长不是大于0 的值!");
					col++;
					cfg.preMap = excelReadContext.getIntValue(row, col++);
					if (cfg.preMap > 0)
					{
						SBean.MapClusterCFGS mccfg = gdCfgs.mapClusters.get(cfg.id);
						if (mccfg == null)
							throw new Exception("活动副本地图ID " + cfg.id + " 的前置地图 " + cfg.preMap + " 在地图总表中不存在");
						if (mccfg.type != GameData.MAP_TYPE_MAPCOPY_ACTIVITY)
							throw new Exception("活动副本地图ID " + cfg.id + " 的前置地图 " + cfg.preMap + " 的配置类型与当前地图类型不一致!");
					}
					col += 3;
					cfg.rewardExp = excelReadContext.getIntValue(row, col++);
					cfg.fixedDropID = excelReadContext.getIntValue(row, col++);
					this.checkFixedDropIDValid(gdCfgs, row, cfg.fixedDropID);
					cfg.turnPlateDropID = excelReadContext.getIntValue(row, col++);
					this.checkNoDuplicateDropIDValid(gdCfgs, row, cfg.turnPlateDropID);
					col += 12;
					cfg.slowMotionTime = excelReadContext.getIntValue(row, col++);
					
					cfg.allMonsters = new TreeMap<>();
					cfg.allTraps = new TreeMap<>();
					cfg.bosses = new TreeMap<>();
					megerMapCopyEntities(gdCfgs, mapclustercfg, cfg.id, cfg.allMonsters, cfg.allTraps, cfg.bosses, false);
					
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("副本地图ID " + cfg.id + " 重复");
					row++;
					col = colStart;
				}
				gdCfgs.activitymaps = cfgs;
			}
			
			
			//宗门战
			excelReadContext.ReadSheet(15);
			{
				Map<Integer, SBean.ClanBattleMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanBattleMapCFGS cfg = new SBean.ClanBattleMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("宗门战地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLE)
						throw new Exception("宗门战地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("宗门战地图ID " + cfg.id + " 重复");
					row++;
					col = colStart;
				}
				gdCfgs.clanBattleMaps = cfgs;
			}
			//宗门支援战
			excelReadContext.ReadSheet(16);
			{
				Map<Integer, SBean.ClanBattleHelpMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanBattleHelpMapCFGS cfg = new SBean.ClanBattleHelpMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("宗门支援战地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_CLAN_BATTLEHELP)
						throw new Exception("宗门支援战地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("宗门支援战地图ID " + cfg.id + " 重复");
					row++;
					col = colStart;
				}
				gdCfgs.clanBattleHelpMaps = cfgs;
			}
			
			//会武
			excelReadContext.ReadSheet(17);
			{
				Map<Integer, SBean.SuperArenaMapCFGS> cfgMap = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.SuperArenaMapCFGS cfg = new SBean.SuperArenaMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.type = excelReadContext.getIntValue(row, col++);
					col++;
					List<Integer> trapGroupIDs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.oneoffTraps = new HashSet<>();
					for (int trapgroup : trapGroupIDs)
					{
						List<Integer> gtraps = trapgroups.get(trapgroup);
						if (gtraps == null)
							throw new Exception("row " + row + " 地图ID " + cfg.id + " 开场Trap 组 ID " + trapgroup + " 不存在!");
						
						for(int trap: gtraps)
						{
							if(!cfg.oneoffTraps.add(trap))
								throw new Exception("row " + row + " 地图ID " + cfg.id + " 开场Trap 组 ID " + trapgroup + "中陷阱ID " + trap + " 重复!");
						}
					}
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("会武地图ID " + cfg.id + " 重复！");
					
					row++;
				}
				gdCfgs.superArenaMaps = cfgMap;
			}
			
			// 正邪道场
			excelReadContext.ReadSheet(18);
			{
				Map<Integer, SBean.BWArenaMapCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.BWArenaMapCFGS cfg = new SBean.BWArenaMapCFGS(0, new ArrayList<>(), new ArrayList<>());
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("正邪道场地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_BWARENA)
						throw new Exception("正邪道场地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");

					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("正邪道场地图ID " + cfg.id + " 重复");

					col++;
					for(int i=0; i<GameData.PET_FIGHT_MAX_USE; i++)
						cfg.selfPetPos.add(excelReadContext.getVector3(row, col++, ";"));
					
					for(int i=0; i<GameData.PET_FIGHT_MAX_USE; i++)
						cfg.enemyPetPos.add(excelReadContext.getVector3(row, col++, ";"));
					
					row++;
					col = colStart;
				}
				gdCfgs.bwarenamaps = cfgs;
			}
			
			excelReadContext.ReadSheet(19);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.PetLifeMapCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.PetLifeMapCFGS cfg = new SBean.PetLifeMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("身世任务副本ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_PETLIFE)
						throw new Exception("身世任务副本ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("row  " + row + " 身世任务副本 " + cfg.id + " 重复");
					
					Set<Integer> npcIDs = new HashSet<>();
					for(int point: mapclustercfg.npcs)
					{
						SBean.NpcPointCFGS npcPointCfg = gdCfgs.npcPoints.get(point);
						if(npcPointCfg == null)
							throw new Exception("身世任务副本 " + cfg.id + " NPC点 " + point + " 非法!");
						
						if(!npcIDs.add(npcPointCfg.relatedID))
							throw new Exception("身世任务副本 " + cfg.id + " NPC点 " + point + " 关联的NPC " + npcPointCfg.relatedID + " 重复!");
						
						SBean.NpcCFGS npcCfg = gdCfgs.npcs.get(npcPointCfg.relatedID);
						if(npcCfg == null)
							throw new Exception("身世任务副本 " + cfg.id + " NPC点 " + point + " 关联的NPC " + npcPointCfg.relatedID + " 非法!");
					}
					
					row++;
				}
				gdCfgs.petlifeMaps = cfgMap;
			}
			
			excelReadContext.ReadSheet(20);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.ClimbTowerMapCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ClimbTowerMapCFGS cfg = new SBean.ClimbTowerMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 2;
					cfg.winCondition = excelReadContext.getIntValue(row, col++);
					checkMapCopyWinCondition(gdCfgs, row, cfg.id, cfg.winCondition);
					cfg.winCondParam = excelReadContext.getIntValue(row, col++);
					if (cfg.winCondition == GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_MONSTER_COUNT && cfg.winCondParam<=0)
						throw new Exception("爬塔副本ID " + cfg.id + " 胜利条件参数" + cfg.winCondParam + "无效!");
					cfg.costVit = excelReadContext.getIntValue(row, col++);
					cfg.needLevel = excelReadContext.getIntValue(row, col++);
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					col++;
					cfg.fightPower = excelReadContext.getIntValue(row, col++);
					cfg.rewardExp = excelReadContext.getIntValue(row, col++);
					cfg.fixedDropID = excelReadContext.getIntValue(row, col++);
					this.checkFixedDropIDValid(gdCfgs, row, cfg.fixedDropID);
					cfg.turnPlateDropID = excelReadContext.getIntValue(row, col++);
					this.checkNoDuplicateDropIDValid(gdCfgs, row, cfg.turnPlateDropID);
					cfg.allMonsters = new TreeMap<>();
					cfg.allTraps = new TreeMap<>();
					cfg.bosses = new TreeMap<>();
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("爬塔副本ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_CLIMBTOWER)
						throw new Exception("爬塔副本ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					megerMapCopyEntities(gdCfgs, mapclustercfg, cfg.id, cfg.allMonsters, cfg.allTraps, cfg.bosses, true);
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("row  " + row + " 爬塔副本 " + cfg.id + " 重复");
					
					row++;
				}
				
				gdCfgs.climbTowerMaps = cfgMap;
			}
			
			//势力战
			excelReadContext.ReadSheet(21);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.ForceWarMapCFGS> cfgs = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ForceWarMapCFGS cfg = new SBean.ForceWarMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 势力战地图 " + cfg.id + " 重复！");
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("row " + row + " 势力战地图ID " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_FORCEWAR)
						throw new Exception("row " + row + " 势力战地图ID " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					col = 10;
					cfg.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.FORCEWAR_TYPE_BWTYPE:
					case GameData.FORCEWAR_TYPE_MESS:
						break;
					default:
						throw new Exception("row " + row + " 势力地图 " + cfg.id + " 类型 " + cfg.type + " 非法");
					}
					
					megerForceWarStatues(gdCfgs, mapclustercfg, cfg.id, cfg);
					row++;
				}
				
				gdCfgs.forcewarMaps = cfgs;
			}
			
			//帮派团队本
			excelReadContext.ReadSheet(23);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.SectGroupMapCFGS> cfgs = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.SectGroupMapCFGS cfg = new SBean.SectGroupMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 帮派团队本地图ID " + cfg.id + " 重复！");
					cfg.memberLevelNeed = excelReadContext.getIntValue(row, col++);
					if (cfg.memberLevelNeed <= 0)
						throw new Exception("row " + row + " 帮派团队本地图成员等级需求 " + cfg.memberLevelNeed + " 必须大于0！");
					cfg.memberNumNeed = excelReadContext.getIntValue(row, col++);
					if (cfg.memberNumNeed <= 0)
						throw new Exception("row " + row + " 帮派团队本地图成员数量需求 " + cfg.memberNumNeed + " 必须大于0！");
					cfg.enterLevel = excelReadContext.getIntValue(row, col++);
					if (cfg.enterLevel <= 0)
						throw new Exception("row " + row + " 帮派团队本进入等级 " + cfg.enterLevel + " 必须大于0！");
					cfg.resetCD = excelReadContext.getIntValue(row, col++);
					if (cfg.resetCD <= 0)
						throw new Exception("row " + row + " 帮派团队本重置CD " + cfg.resetCD + " 必须大于0！");
					cfg.openCostId = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdCfgs, row, cfg.openCostId, false);
					cfg.openCostCount = excelReadContext.getIntValue(row, col++);
					if (cfg.openCostCount <= 0)
						throw new Exception("row " + row + " 帮派团队本开启消耗 " + cfg.openCostCount + " 必须大于0！");
					cfg.monsterTimes = excelReadContext.getIntValue(row, col++);
					if (cfg.monsterTimes <= 0)
						throw new Exception("row " + row + " 帮派团队本怪物波数 " + cfg.monsterTimes + " 必须大于0！");
					col++;
					cfg.preMapId = excelReadContext.getIntValue(row, col++);
					if (cfg.preMapId != -1 && !cfgs.containsKey(cfg.preMapId))
						throw new Exception("row " + row + " 帮派团队本前置地图ID " + cfg.id + " 不存在！");
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					if (cfg.maxTime <= 0)
						throw new Exception("row " + row + " 帮派团队本持续时间 " + cfg.maxTime + " 必须大于0！");
					col += 2;
					cfg.drops = excelReadContext.getIntegerList(row, col++, ";");
					cfg.finishReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finishReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本通关奖励不能超过四个！");
					cfg.finishActivites = excelReadContext.getIntValue(row, col++);
					if (cfg.finishActivites < 0)
						throw new Exception("row " + row + " 帮派团队本通关活跃度 " + cfg.finishActivites + " 不能小于0！");
					cfg.quickFinishReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.quickFinishReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本快速通关奖励不能超过四个！");
					cfg.quickFinishActivites = excelReadContext.getIntValue(row, col++);
					if (cfg.quickFinishActivites < 0)
						throw new Exception("row " + row + " 帮派团队本快速通关活跃度 " + cfg.quickFinishActivites + " 不能小于0！");
					cfg.finish10percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish10percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本10%奖励不能超过四个！");
					cfg.finish10percentActivites = excelReadContext.getIntValue(row, col++);
					if (cfg.finish10percentActivites < 0)
						throw new Exception("row " + row + " 帮派团队本10%活跃度 " + cfg.finish10percentActivites + " 不能小于0！");
					cfg.finish50percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish50percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本50%奖励不能超过四个！");
					cfg.finish50percentActivites = excelReadContext.getIntValue(row, col++);
					if (cfg.finish50percentActivites < 0)
						throw new Exception("row " + row + " 帮派团队本50%活跃度 " + cfg.finish50percentActivites + " 不能小于0！");
					cfg.finish75percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish75percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本75%奖励不能超过四个！");
					cfg.finish75percentActivites = excelReadContext.getIntValue(row, col++);
					if (cfg.finish75percentActivites < 0)
						throw new Exception("row " + row + " 帮派团队本75%活跃度 " + cfg.finish75percentActivites + " 不能小于0！");
					cfg.finish100percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish100percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本100%奖励不能超过四个！");
					cfg.finish100percentActivites = excelReadContext.getIntValue(row, col++);
					if (cfg.finish100percentActivites < 0)
						throw new Exception("row " + row + " 帮派团队本100%活跃度 " + cfg.finish100percentActivites + " 不能小于0！");

					row++;
				}
				
				gdCfgs.sectGroupMaps = cfgs;
			}
			
			//神兵特技副本
			excelReadContext.ReadSheet(24);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.WeaponMapCFGS> maps = new HashMap<>();
				List<SBean.WeaponMapGradeCFGS> grades = new ArrayList<>();
				int lastFloor = 0;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.WeaponMapCFGS cfg = new SBean.WeaponMapCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("row " + row + " 神兵特技副本 " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_WEAPON)
						throw new Exception("row " + row + " 神兵特技副本 " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					int lvlFloor = excelReadContext.getIntValue(row, col++);
					if(lvlFloor <= lastFloor)
						throw new Exception("row " + row + " 神兵特技副本 " + cfg.id + " 等级上限 " + lvlFloor + " 非法!");
					lastFloor = lvlFloor;
					grades.add(new SBean.WeaponMapGradeCFGS(lvlFloor, cfg.id));
					
					cfg.keepMotivate = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.keepMotivate);
					cfg.motivateWeapon = excelReadContext.getIntValue(row, col++);
					cfg.winCondition = excelReadContext.getIntValue(row, col++);
					checkMapCopyWinCondition(gdCfgs, row, cfg.id, cfg.winCondition);
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					cfg.rewardExp = excelReadContext.getIntValue(row, col++);
					cfg.fixedDropID = excelReadContext.getIntValue(row, col++);
					checkFixedDropIDValid(gdCfgs, row, cfg.fixedDropID);
					cfg.turnPlateDropID = excelReadContext.getIntValue(row, col++);
					
					col = 21;
					cfg.slowMotionTime = excelReadContext.getIntValue(row, col++);
					Map<Integer, SBean.MapEntity> allMonster =  new HashMap<>();
					megerMapCopyMonsters(gdCfgs, mapclustercfg, cfg.id, allMonster, true);
					cfg.bosses = new HashMap<>();
					megerMapCopyBoss(gdCfgs, mapclustercfg, cfg.bosses);
					switch (cfg.winCondition)
					{
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_BOSSES:
						if(cfg.bosses.isEmpty())
							throw new Exception("row " + row + " 神兵特技副本" + cfg.id + " 胜利条件为 " + cfg.winCondition + " 击杀boss, 没有配置boss");
						break;
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT:
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_MONSTER_COUNT:
						if(allMonster.isEmpty())
							throw new Exception("row " + row + " 神兵特技副本 " + cfg.id + " 胜利条件为 " + cfg.winCondition + " 没有配置怪物");
						break;
					default:
						throw new Exception("row " + row + " 神兵特技副本 " + cfg.id + " 胜利条件 非法!");
					}
					
					
					if(maps.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 神兵特技副本 " + cfg.id + " 重复!");
					
					row++;
				}
				
				if(grades.isEmpty())
					throw new Exception("row " + row + " 神兵特技副本 没有配置");
				
				gdCfgs.weaponMaps = maps;
				gdCfgs.weaponMapGrades = grades;
			}
			
			excelReadContext.ReadSheet(25);
			{
				int row = 2;
				int col = 0;
				
				Map<Integer, SBean.DemonHoleMapCFGS> maps = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, 0))
				{
					SBean.DemonHoleMapCFGS cfg = new SBean.DemonHoleMapCFGS(0, 0, 0, 0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, new SBean.Vector3(), 0, 0);
					cfg.id = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("row " + row + " 伏魔洞 " + cfg.id + " 在地图总表中不存在");
					
					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_DEMON_HOLE)
						throw new Exception("row " + row + " 伏魔洞 " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					col++;
					int grade = excelReadContext.getIntValue(row, col++);
					SBean.DemonHoleGradeCFGS gradeCfg = gdCfgs.demonHole.grades.get(grade - 1);
					if(gradeCfg == null)
						throw new Exception("row " + row + " 副本难度类型 " + grade + " 非法");
					gradeCfg.maps.add(cfg.id);
					cfg.enterCostCnt = excelReadContext.getIntValue(row, col++);
					cfg.addExpInterval = excelReadContext.getIntValue(row, col++);
					cfg.addExpPercent = excelReadContext.getIntValue(row, col++) / 100.f;
					for(int i = 0; i < 6; i++)
					{
						SBean.Vector3 pos = excelReadContext.getVector3(row, col++, ";");
						checkMapPosValid(row, mapclustercfg, pos, " 伏魔洞 " + cfg.id + " 复活点 " + (i + 1));
						cfg.spawns.add(pos);
					}
					
					for(int i = 0; i < 6; i++)
					{
						SBean.Vector3 pos = excelReadContext.getVector3(row, col++, ";");
						checkMapPosValid(row, mapclustercfg, pos, " 伏魔洞 " + cfg.id + " 复活点 " + (i + 1));
						cfg.revives.add(pos);
					}
					
					for(int i = 0; i < 6; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count  = excelReadContext.getIntValue(row, col++);
						if(itemID != 0)
						{
							checkEntityIDValid(gdCfgs, row, itemID, true);
							cfg.rewards.add(new SBean.DummyGoods(itemID, count));
						}
					}
					
					cfg.bossID = excelReadContext.getIntValue(row, col++);
					if(cfg.bossID > 0 && !gdCfgs.demonHole.bosses.containsKey(cfg.bossID))
						throw new Exception("row " + row + " 伏魔洞  " + cfg.id + " bossID + " + cfg.bossID + "非法!");
					cfg.bossPos = excelReadContext.getVector3(row, col++, ";");
					if(cfg.bossID > 0)
						checkMapPosValid(row, mapclustercfg, cfg.bossPos, " 伏魔洞 " + cfg.id + " boss位置 ");
					cfg.bossRefreshTime = excelReadContext.getIntValue(row, col++);
					cfg.pkType = excelReadContext.getIntValue(row, col++);
					
					switch (cfg.pkType)
					{
					case GameData.MAP_PKTYPE_NORMAL:
					case GameData.MAP_PKTYPE_SAFE:
					case GameData.MAP_PKTYPE_SECT:
					case GameData.MAP_PKTYPE_KILL:
						break;
					default:
						throw new Exception("row " + row + " 伏魔洞  " + cfg.id + " PK类型 + " + cfg.pkType + "非法!");
					}
					
					if(maps.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 地图ID " + cfg.id + " 重复!");
					
					row++;
					col = 0;
				}
				
				for(int grade = 1; grade <= gdCfgs.demonHole.grades.size(); grade++)
				{
					if(gdCfgs.demonHole.grades.get(grade - 1).maps.size() != 10)
						throw new Exception("伏魔洞 难度类型 " + grade + " 地图层数 " + gdCfgs.demonHole.grades.get(grade - 1).maps.size() + " 非法!");
				}
				
				gdCfgs.demonHoleMaps = maps;
			}
			
			excelReadContext.ReadSheet(26);
			{
				int row = 2;
				int col = 0;
				
				Map<Integer, SBean.JusticeMapCopyCFGS> maps = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, 0))
				{
					col = 0;
					SBean.JusticeMapCopyCFGS cfg = new SBean.JusticeMapCopyCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("row " + row + " 正义之心副本 " + cfg.id + " 在地图总表中不存在");

					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_JUSTICE)
						throw new Exception("row " + row + " 正义之心副本 " + cfg.id + " 与地图总表中的地图配置类型不一致!");

					col++;
					cfg.openType = excelReadContext.getIntValue(row, col++);
					cfg.winCondition = excelReadContext.getIntValue(row, col++);
					
					Map<Integer, SBean.MapEntity> allMonster =  new HashMap<>();
					megerMapCopyMonsters(gdCfgs, mapclustercfg, cfg.id, allMonster, true);
					switch (cfg.winCondition)
					{
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT:
						if(allMonster.isEmpty())
							throw new Exception("row " + row + " 正义之心副本 " + cfg.id + " 胜利条件为 " + cfg.winCondition + " 没有配置怪物");
						break;
					default:
						throw new Exception("row " + row + " 正义之心副本 " + cfg.id + " 胜利条件 非法!");
					}
					
					col++;
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					if (cfg.maxTime <= 0)
						throw new Exception("row " + row + " 正义之心副本最大时长 " + cfg.maxTime + " 必须大于0");
					cfg.rewardExp = excelReadContext.getIntValue(row, col++);
					if (cfg.rewardExp < 0)
						throw new Exception("row " + row + " 正义之心副本完成奖励经验 " + cfg.rewardExp + " 不能小于0");
					cfg.turnPlateDropID = excelReadContext.getIntValue(row, col++);
					this.checkNoDuplicateDropIDValid(gdCfgs, row, cfg.turnPlateDropID);
					col = 20;
					cfg.slowMotionTime = excelReadContext.getIntValue(row, col++);
					col = 27;
					cfg.npcMapGroup = excelReadContext.getIntValue(row, col++);
					cfg.mapSkill = excelReadContext.getIntValue(row, col++);
					maps.put(cfg.id, cfg);
					row++;
				}
				
				gdCfgs.justiceMaps = maps;
			}
			
			excelReadContext.ReadSheet(27);
			{
				int row = 2;
				int col = 0;
				Map<Integer, SBean.FightNpcMapCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, 0))
				{
					SBean.FightNpcMapCFGS cfg = new SBean.FightNpcMapCFGS(0, 0, 0, new HashMap<>());
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.winCondition = excelReadContext.getIntValue(row, col++);
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(cfg.id);
					if (mapclustercfg == null)
						throw new Exception("row " + row + " 约战NPC地图 " + cfg.id + " 在地图总表中不存在");

					if (mapclustercfg.type != GameData.MAP_TYPE_MAPCOPY_FIGHTNPC)
						throw new Exception("row " + row + " 约战NPC地图 " + cfg.id + " 与地图总表中的地图配置类型不一致!");
					
					megerMapCopyBoss(gdCfgs, mapclustercfg, cfg.bosses);
					switch (cfg.winCondition)
					{
					case GameData.MAPCOPY_NORMAL_FINISH_TYPE_KILL_BOSSES:
						if(cfg.bosses.isEmpty())
							throw new Exception("row " + row + " 约战NPC地图 " + cfg.id + " 胜利条件为 " + cfg.winCondition + " 击杀boss, 没有配置boss");
						break;
					default:
						throw new Exception("row " + row + " 约战NPC地图 " + cfg.id + " 胜利条件 非法!");
					}
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 约战NPC地图ID " + cfg.id + " 重复");
					
					row++;
					col = 0;
				}
				gdCfgs.fightNpcMaps = cfgs;
			}
			
			//守护地图
			excelReadContext.ReadSheet(29);
			{
				int row = 2;
				int col = 0;
				
				Map<Integer, SBean.TowerDefenceMapCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, 0))
				{
					int mapID = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS clusterCfg = gdCfgs.mapClusters.get(mapID);
					if(clusterCfg == null)
						throw new Exception("row " + row + " 守护地图 " + mapID + " 在地图总表中不存在!");
					
					int winCondition = GameData.MAPCOPY_NORMAL_FINISH_TYPE_TIME_LIMIT;
					int maxTime = excelReadContext.getIntValue(row, col++);
					col++;
					for(int areaID: clusterCfg.spawnAreas)
					{
						SBean.SpawnAreaCFGS areaCfg = gdCfgs.spawnAreas.get(areaID);
						for(int pointID: areaCfg.spawnPoint)
						{
							SBean.SpawnPointCFGS pointCfg = gdCfgs.spawnPoints.get(pointID);
							if(pointCfg.spawnTimes != 1 || pointCfg.spawnNum.get(0) != 1)
								throw new Exception("刷怪区域 " + areaID + " 刷怪点 " + pointID + " 刷新怪物数量  非法!");
						}
					}
					
					int turnPlateDropID = excelReadContext.getIntValue(row, col++);
					if(cfgs.put(mapID, new SBean.TowerDefenceMapCFGS(mapID, winCondition, maxTime, turnPlateDropID)) != null)
						throw new Exception("row " + row + " 守护地图 " + mapID + " 重复!");
					
					row++;
					col = 0;
				}
				
				gdCfgs.towerDefenceMaps = cfgs;
			}
			
			for(SBean.WorldBossCFGS bossCfg: gdCfgs.worldBosses.values())
			{
				SBean.MapClusterCFGS mapClusterCfg = gdCfgs.mapClusters.get(bossCfg.base.mapID);
				if(mapClusterCfg == null || mapClusterCfg.type != GameData.MAP_TYPE_MAP_WORLD)
					throw new Exception("世界boss ：" + bossCfg.base.id + " , 所在世界地图 ID " + bossCfg.base.mapID + " 非法 ");
				
				for(SBean.Vector3 pos: bossCfg.base.refreshPos)
				{
					if(pos.x <= mapClusterCfg.minX ||pos.x >= mapClusterCfg.maxX || pos.z <= mapClusterCfg.minZ || pos.z >= mapClusterCfg.maxZ)
						throw new Exception("世界boss ：" + bossCfg.base.id + ", 所在地图 ID " + bossCfg.base.mapID + " 有效位置  x: " + mapClusterCfg.minX + " ~ " + mapClusterCfg.maxX + " z: " + mapClusterCfg.minZ + " ~ " + mapClusterCfg.maxZ +  " 刷新位置  " + GameData.toString(pos) + " 非法");
				}
				
				SBean.WorldMapCFGS wmCfg = gdCfgs.worldMaps.get(bossCfg.base.mapID);
				if(wmCfg == null || wmCfg.pkType != GameData.MAP_PKTYPE_NORMAL)
					throw new Exception("世界boss ：" + bossCfg.base.id + " , 所在世界地图 ID " + bossCfg.base.mapID + " 非法 ");
			}
			
			for(SBean.WorldMonsterCFGS mCfg: gdCfgs.worldSuperMonsters.values())
			{
				SBean.MapClusterCFGS mapClusterCfg = gdCfgs.mapClusters.get(mCfg.mapID);
				if(mapClusterCfg == null || mapClusterCfg.type != GameData.MAP_TYPE_MAP_WORLD)
					throw new Exception("精英怪 ：" + mCfg.id + " , 所在世界地图 ID " + mCfg.mapID + " 非法 ");
				
				for(SBean.Vector3 pos: mCfg.refreshPos)
				{
					if(pos.x <= mapClusterCfg.minX ||pos.x >= mapClusterCfg.maxX || pos.z <= mapClusterCfg.minZ || pos.z >= mapClusterCfg.maxZ)
						throw new Exception("精英怪 ：" + mCfg.id + ", 所在地图 ID " + mCfg.mapID + " 有效位置  x: " + mapClusterCfg.minX + " ~ " + mapClusterCfg.maxX + " z: " + mapClusterCfg.minZ + " ~ " + mapClusterCfg.maxZ +  " 刷新位置  " + GameData.toString(pos) + " 非法");
				}
			}
			
			for(SBean.WorldMineralCFGS mCfg: gdCfgs.worldMinerals.values())
			{
				SBean.MapClusterCFGS mapClusterCfg = gdCfgs.mapClusters.get(mCfg.mapID);
				if(mapClusterCfg == null || mapClusterCfg.type != GameData.MAP_TYPE_MAP_WORLD)
					throw new Exception("活动矿 ：" + mCfg.id + " , 所在世界地图 ID " + mCfg.mapID + " 非法 ");
				
				for(SBean.Vector3 pos: mCfg.refreshPos)
				{
					if(pos.x <= mapClusterCfg.minX ||pos.x >= mapClusterCfg.maxX || pos.z <= mapClusterCfg.minZ || pos.z >= mapClusterCfg.maxZ)
						throw new Exception("活动矿：" + mCfg.id + ", 所在地图 ID " + mCfg.mapID + " 有效位置  x: " + mapClusterCfg.minX + " ~ " + mapClusterCfg.maxX + " z: " + mapClusterCfg.minZ + " ~ " + mapClusterCfg.maxZ +  " 刷新位置  " + GameData.toString(pos) + " 非法");
				}
			}
			
			gdCfgs.activityMapProcess = new HashMap<>();
			for(SBean.ActivityMapCFGS activitymap:gdCfgs.activitymaps.values())
			{
				if (activitymap.groupId == 1 || activitymap.groupId == 2)
					continue;
				SBean.MonsterProcess mp = new SBean.MonsterProcess(new HashMap<>());
				SBean.MapClusterCFGS mapclustercfg = gdCfgs.mapClusters.get(activitymap.id);
				long monsterHp = 0;
				for (int areaID : mapclustercfg.spawnAreas)
				{
					SBean.SpawnAreaCFGS areaCfg = gdCfgs.spawnAreas.get(areaID);
					if (areaCfg == null)
						throw new Exception("地图ID " + activitymap.id + " 刷怪区域  " + areaID + " 不存在！");
					for (int point:areaCfg.spawnPoint)
					{
						SBean.SpawnPointCFGS pointCfg = gdCfgs.spawnPoints.get(point);
						if (pointCfg == null)
							continue;
						int count = GameData.getPointMonsterCount(pointCfg);
						SBean.MonsterCFGS monsterCFGS = gdCfgs.monsters.get(pointCfg.monsterID);
						monsterHp += monsterCFGS.maxHP * count;
						mp.monsterProcess.put(monsterCFGS.id, monsterCFGS.maxHP);
					}
				}
				for (int key : mp.monsterProcess.keySet())
				{
					mp.monsterProcess.put(key, (int) (mp.monsterProcess.get(key) * 10000l / monsterHp));
				}
				gdCfgs.activityMapProcess.put(activitymap.id, mp);
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	private List<DummyGoods> getDummyGoodsList(ExcelReadContext excelReadContext2, int row, int col, String string, GameDataCFGS gdCfgs) throws Exception
	{
		List<DummyGoods> dummyGoods = new ArrayList<>();
		List<String> dummyString = excelReadContext2.getStringList(row, col, string);
		if (dummyString.size() == 1 && dummyString.get(0).equals("0"))
			return dummyGoods;
		for (String dummy : dummyString)
		{
			String[] dummyIdCount = dummy.split(",");
			int id = Integer.parseInt(dummyIdCount[0]);
			checkEntityIDValid(gdCfgs, row, id, false);
			int count = Integer.parseInt(dummyIdCount[1]);
			if (count <= 0)
				throw new Exception("(" + row + "," + col + ") , 道具数量 " + count + " 必须大于0 ");
			dummyGoods.add(new SBean.DummyGoods(id, count));
		}
		return dummyGoods;
	}

	private void megerForceWarStatues(SBean.GameDataCFGS gdCfgs, SBean.MapClusterCFGS mapclustercfg, int mapID, SBean.ForceWarMapCFGS forcewar) throws Exception
	{
		int whiteNormalStatues = 0;
		int whiteBigStatues = 0;
		int whiteBossStatues = 0;
		
		int blackNormalStatues = 0;
		int blackBigStatues = 0;
		int blackBossStatues = 0;
		for (int areaID : mapclustercfg.spawnAreas)
		{
			SBean.SpawnAreaCFGS areaCfg = gdCfgs.spawnAreas.get(areaID);
			if (areaCfg == null)
				throw new Exception("势力战地图: " + mapID + " 刷怪区域  " + areaID + " 不存在");
			
			for (int pointID : areaCfg.spawnPoint)
			{
				SBean.SpawnPointCFGS pointCfg = gdCfgs.spawnPoints.get(pointID);
				if (pointCfg == null)
					throw new Exception("势力战地图: " + mapID + " 刷怪区域 " + areaID + " 刷怪点 " + pointID + "不存在");

				SBean.MonsterCFGS mCfg = gdCfgs.monsters.get(pointCfg.monsterID);
				if (mCfg == null)
					throw new Exception("势力战地图: " + mapID + " 上怪物  " + pointCfg.monsterID + " 不存在");
				
				int count = GameData.getPointMonsterCount(pointCfg);
				if(count < 0 && mCfg.bossType != GameData.MONSTER_BOSSTYPE_NPC)
					throw new Exception("势力战地图 " + mapID + " 刷怪区域 " + areaID + " 刷怪点 " + pointID + " 怪物数量 " + count + " 非法！");
				
				if(mCfg.bwType == GameData.BWTYPE_WHITE)
				{
					if(mCfg.bossType == GameData.MONSTER_BOSSTYPE_NORMALSTATUE)
						whiteNormalStatues += count;
					else if(mCfg.bossType == GameData.MONSTER_BOSSTYPE_BIGSTATUE)
						whiteBigStatues += count;
					else if(mCfg.bossType == GameData.MONSTER_BOSSTYPE_BOSSSTATUE)
						whiteBossStatues += count;
				}
				else if(mCfg.bwType == GameData.BWTYPE_BLACK)
				{
					if(mCfg.bossType == GameData.MONSTER_BOSSTYPE_NORMALSTATUE)
						blackNormalStatues += count;
					else if(mCfg.bossType == GameData.MONSTER_BOSSTYPE_BIGSTATUE)
						blackBigStatues += count;
					else if(mCfg.bossType == GameData.MONSTER_BOSSTYPE_BOSSSTATUE)
						blackBossStatues += count;
				}	
			}
		}
		
		if(whiteNormalStatues != blackNormalStatues)
			throw new Exception("势力战地图: " + mapID + " 正派普通雕像数量 " + whiteNormalStatues + " 邪派普通雕像数量 " + blackNormalStatues + " 不一致");
		
		if(whiteBigStatues != blackBigStatues)
			throw new Exception("势力战地图: " + mapID + " 正派大雕像数量 " + whiteBigStatues + " 邪派大雕像数量 " + blackBigStatues + " 不一致");
		
		if(whiteBossStatues != blackBossStatues || whiteBossStatues != 1 || blackBossStatues != 1)
			throw new Exception("势力战地图: " + mapID + " 正派水晶数量 " + whiteBigStatues + " 邪派水晶数量 " + blackBigStatues + " 不一致 或非法");
		
		forcewar.normalStatues = whiteNormalStatues;
		forcewar.bigStatues = whiteBigStatues;
	}
	
	private void megerMapCopyEntities(SBean.GameDataCFGS gdCfgs, SBean.MapClusterCFGS mapclustercfg, int mapID, Map<Integer, SBean.MapEntity> monsters, Map<Integer, SBean.MapEntity> traps, Map<Integer, Integer> bosses, boolean countLimit) throws Exception
	{
		megerMapCopyMonsters(gdCfgs, mapclustercfg, mapID, monsters, countLimit);
		megerMapCopyTraps(gdCfgs, mapclustercfg, mapID, traps);
		megerMapCopyBoss(gdCfgs, mapclustercfg, bosses);
	}
	
	private void megerMapCopyMonsters(SBean.GameDataCFGS gdCfgs, SBean.MapClusterCFGS mapclustercfg, int mapID, Map<Integer, SBean.MapEntity> monsters, boolean countLimit) throws Exception
	{
		Map<Integer, Integer> allMonsters = new TreeMap<>();
		for (int areaID : mapclustercfg.spawnAreas)
		{
			SBean.SpawnAreaCFGS areaCfg = gdCfgs.spawnAreas.get(areaID);
			if (areaCfg == null)
				throw new Exception("地图: " + mapID + " 刷怪区域  " + areaID + " 不存在");
			for (int pointID : areaCfg.spawnPoint)
			{
				SBean.SpawnPointCFGS pointCfg = gdCfgs.spawnPoints.get(pointID);
				if (pointCfg == null)
					throw new Exception("地图: " + mapID + " 刷怪区域 " + areaID + " 刷怪点 " + pointID + "不存在");

				int count = GameData.getPointMonsterCount(pointCfg);
				if(count < 0 && countLimit)
					throw new Exception("副本 " + mapID + " 刷怪区域 " + areaID + " 刷怪点 " + pointID + " 怪物数量 " + count + " 非法！");
				
				allMonsters.merge(pointCfg.monsterID, count, (ov, nv) -> ov + nv);
			}
		}
		for (Map.Entry<Integer, Integer> e : allMonsters.entrySet())
		{
			int monsterId = e.getKey();
			int monsterCount = e.getValue();
			SBean.MonsterCFGS mCfg = gdCfgs.monsters.get(monsterId);
			if (mCfg == null)
				throw new Exception("地图: " + mapID + " 上怪物  " + monsterId + " 不存在");
			SBean.MapEntity entity = new SBean.MapEntity(monsterId, monsterCount, mCfg.addExp, mCfg.fixedDropID, mCfg.randomDropIDs, mCfg.randomDropCnt);
			monsters.put(entity.id, entity);
		}
	}
	
	private void megerMapCopyTraps(SBean.GameDataCFGS gdCfgs, SBean.MapClusterCFGS mapclustercfg, int mapID, Map<Integer, SBean.MapEntity> traps) throws Exception
	{
		for (Integer trapId : mapclustercfg.traps)
		{
			SBean.TrapExpandedCFGS trapcfg = gdCfgs.traps.get(trapId);
			if (trapcfg == null)
				throw new Exception("地图: " + mapID + "陷阱组引用的陷阱id " + trapId + " 不存在");
			
			List<Integer> randomDropIDs = new ArrayList<>();
			for(int i=0; i<5; i++)
				randomDropIDs.add(trapcfg.randomDropID);
			SBean.MapEntity entity = new SBean.MapEntity(trapId, 1, 0, trapcfg.fixedDropID, randomDropIDs, trapcfg.randomDropCnt);
			traps.put(entity.id, entity);
		}
	}
	
	private void megerMapCopyBoss(SBean.GameDataCFGS gdCfgs, SBean.MapClusterCFGS mapclustercfg,  Map<Integer, Integer> bosses) throws Exception
	{
		for (int aid : mapclustercfg.spawnAreas)
		{
			SBean.SpawnAreaCFGS areacfg = gdCfgs.spawnAreas.get(aid);
			if (areacfg == null)
				throw new Exception("地图  " + mapclustercfg.id + " 上的刷怪区域  " + aid + " 不存在!");
			for (int pid : areacfg.spawnPoint)
			{
				SBean.SpawnPointCFGS pointcfg = gdCfgs.spawnPoints.get(pid);	
				if (pointcfg == null)
					throw new Exception("地图  " + mapclustercfg.id + " 上的刷怪点  " + pid + " 不存在!");
				SBean.MonsterCFGS monstercfg = gdCfgs.monsters.get(pointcfg.monsterID);
				if (monstercfg == null)
					throw new Exception("地图  " + mapclustercfg.id + " 上的怪物  " + pointcfg.monsterID + " 不存在!");
				
				if (monstercfg.bossType == GameData.MONSTER_BOSSTYPE_FINALBOSS)
				{
					if(pointcfg.spawnNum.get(0) > 1)		//击杀boss会播放慢动作
						throw new Exception("副本地图  " + mapclustercfg.id + " 上的刷怪点  " + pid + " 为BOSS 数量 ：" + pointcfg.spawnNum.get(0));
					bosses.merge(monstercfg.id, 1, (ov, nv) -> ov + nv);
				}
			}
		}
	}
	
	// 武将表
	public void loadClassRoleTables(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				List<SBean.ClassRoleCFGS> classRoleList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Set<Integer> set = new TreeSet<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.ClassRoleCFGS classRole = new SBean.ClassRoleCFGS();
					classRole.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 6;
					List<SBean.ClassRolePropCFGS> props = new ArrayList<>();
					for (int i = 0; i < 8; i++)
					{
						SBean.ClassRolePropCFGS p = new SBean.ClassRolePropCFGS();
						p.org = excelReadContext.getDoubleValue(row, col++);
						p.incs1 = excelReadContext.getDoubleValue(row, col++);
						p.incs2 = excelReadContext.getDoubleValue(row, col++);
						props.add(p);
					}
					Iterator<SBean.ClassRolePropCFGS> arrProps = props.iterator();
					classRole.hp = arrProps.next();
					classRole.atkN = arrProps.next();
					classRole.defN = arrProps.next();
					classRole.atr = arrProps.next();
					classRole.ctr = arrProps.next();
					classRole.acrN = arrProps.next();
					classRole.tou = arrProps.next();
					classRole.atkA = arrProps.next();
					
					Set<Integer> attacks = new HashSet<>();
					classRole.attacks = excelReadContext.getIntegerList(row, col++, ";");
					for(int sid: classRole.attacks)
					{
						if(!attacks.add(sid))
							throw new Exception("武将普通攻击ID重复， id = " + sid);
					}
					for (int sid: classRole.attacks)
						this.checkSkillIDValid(gdCfgs, row, sid);
					
					List<Integer> skills = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						int sid = excelReadContext.getIntValue(row, col++);
						this.checkSkillIDValid(gdCfgs, row, sid);
						if (skills.contains(sid))
							throw new Exception("武将技能ID重复， id = " + sid);

						skills.add(sid);
					}
					classRole.skills = skills;

					classRole.dodgeSkill = excelReadContext.getIntValue(row, col++);
					this.checkSkillIDValid(gdCfgs, row, classRole.dodgeSkill);

//					col = colStart + 36;
					classRole.attackList = excelReadContext.getIntegerList(row, col++, ";");
					classRole.speed = excelReadContext.getIntValue(row, col++);
					classRole.radius = excelReadContext.getIntValue(row, col++);
					classRole.checkRange = excelReadContext.getIntValue(row, col++);
					col += 1;
					classRole.spirits = new ArrayList<>();
					List<Integer> spritsZY = excelReadContext.getIntegerList(row, col++, ";");
					for (int spiritId : spritsZY)
					{
						this.checkSPiritIDValid(gdCfgs, row, spiritId);
					}
					List<Integer> spiritsJH = excelReadContext.getIntegerList(row, col++, ";");
					for (int spiritId : spiritsJH)
					{
						this.checkSPiritIDValid(gdCfgs, row, spiritId);
					}
					List<Integer> spiritsPB = excelReadContext.getIntegerList(row, col++, ";");
					for (int spiritId : spiritsPB)
					{
						this.checkSPiritIDValid(gdCfgs, row, spiritId);
					}
					classRole.spirits.addAll(spritsZY);
					classRole.spirits.addAll(spiritsJH);
					classRole.spirits.addAll(spiritsPB);
					classRole.spawnPosition = new SBean.MapPosition(1, new SBean.Vector3());
					classRole.spawnPosition.mapID = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mcc = gdCfgs.mapClusters.get(classRole.spawnPosition.mapID);
					if (mcc == null)
						throw new Exception("武将 " + classRole.id + " 出生地图 " + classRole.spawnPosition.mapID + " 不存在");
					
					if (mcc.type != GameData.MAP_TYPE_MAP_WORLD)
						throw new Exception("武将 " + classRole.id + " 出生地图 " + classRole.spawnPosition.mapID + " 非法");
					
					if (mcc.patchPacket > 0)
						throw new Exception("武将 " + classRole.id + " 出生地图 打包放置序号" + mcc.patchPacket + " 非法");
					
					classRole.spawnPosition.position = excelReadContext.getVector3(row, col++, ";");
					List<Integer> initEquips = new ArrayList<>();
					for (int i = 0; i < 6; ++i)
					{
						int eid = excelReadContext.getIntValue(row, col++);
						SBean.EquipCFGS equipCFG = gdCfgs.equips.get(eid);
						if (equipCFG == null)
							throw new Exception("武将 id = " + classRole.id + " 初始装备 id" + eid + "非法!");
						if (equipCFG.type != i + 1)
							throw new Exception("武将 id = " + classRole.id + " 初始装备 id" + eid + "装备位置非法!");
						if (equipCFG.lvlReq > 1)
							throw new Exception("武将 id = " + classRole.id + " 初始装备 id" + eid + "需求等级大于1！");
						initEquips.add(eid);
					}
					classRole.initEquips = initEquips;
					classRole.aotufightAi = excelReadContext.getIntegerList(row, col++, ";");
					classRole.classReduce = new ArrayList<>();
					for(int i=0; i<5; i++)
						classRole.classReduce.add(excelReadContext.getFloatValue(row, col++));
					
					if (!set.add(classRole.id))
						throw new Exception("武将ID重复， id = " + classRole.id);
					if (classRole.id != classRoleList.size() + 1)
						throw new Exception("武将ID重复， id = " + classRole.id + "不连续！");
					classRoleList.add(classRole);
					row++;
				}
				
				if(classRoleList.size() != GameData.CLASS_TYPE_END)
					throw new Exception("武将表 职业数量非法!");
				
				gdCfgs.classRoles = classRoleList;
			}
			
			excelReadContext.ReadSheet(1);
			{
				Map<Integer, SBean.ClassRoleFashionCFGS> cfgMap = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.ClassRoleFashionCFGS cfg = new SBean.ClassRoleFashionCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 4;
					cfg.faces = excelReadContext.getByteList(row, col++, ";");
					cfg.hairs = excelReadContext.getByteList(row, col++, ";");
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("武将职业外观表ID " + cfg.id + " 重复");
					
					row++;
				}
				
				gdCfgs.classRoleFashions = cfgMap;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	int checkMainTask(SBean.GameDataCFGS gdCfgs, List<SBean.MainTaskCFGS> cfgs, int bwType) throws Exception
	{
		Set<Integer> nextIDs = new HashSet<>();
		boolean alter = false;
		boolean canTransformTask = false;
		SBean.MainTaskCFGS t = cfgs.get(0);
		int nextID = GameData.getNextMainTaskId(t, bwType);
		int taskCount = 1;
		
		int maxCount = 50;
		List<SBean.MainTaskGradeCFGS> taskGrades = new ArrayList<>();
		SBean.MainTaskGradeCFGS first = new SBean.MainTaskGradeCFGS(t.id, new HashSet<>());
		first.tasks.add(t.id);
		taskGrades.add(first);
		
		while(nextID > 0)
		{
			SBean.MainTaskGradeCFGS g = taskGrades.get(taskGrades.size() - 1);
			if(g.tasks.size() >= maxCount)
			{
				g = new SBean.MainTaskGradeCFGS(nextID, new HashSet<>());
				taskGrades.add(g);
			}
			g.tasks.add(nextID);
			
			if(t.nextId > 0 && (t.nextWhiteId != 0 || t.nextBlackId != 0))
				throw new Exception("主线任务  id " + t.id + " 的后置任务 " + t.nextId + " 和后置转职任务同时存在 正派后置任务ID " + t.nextWhiteId + " 邪派后置任务ID " + t.nextBlackId );
			
			if (nextID < 0 || nextID > cfgs.size() || t.id == nextID)
				throw new Exception("主线任务  id " + t.id + " 的后置任务ID " + t.nextId + " 非法！");
			
			if(t.cond.type == GameData.TASK_TYPE_TRANSFROM_LEVEL && t.cond.param1 > 1)
				canTransformTask = true;
			
			if(!canTransformTask && (t.nextWhiteId > 0 || t.nextBlackId > 0))
				throw new Exception("主线任务 id " + t.id + " 没有转职就有后置转职任务 正派后置任务ID " + t.nextWhiteId + " 邪派后置任务ID " + t.nextBlackId);
			
			for(int trigID: t.senceTrigIDs)
			{
				SBean.SceneTrigCFGS stc = gdCfgs.sceneTrigs.get(trigID);
				if(stc == null)
					throw new Exception("主线任务  id " + t.id + " 场景触发ID " + trigID + " 非法！");
				
				switch (stc.trigBehavior)
				{
				case GameData.SCENE_TRIG_BEHAVIOR_TASK_ALTER:
					if(alter)
						throw new Exception("主线任务  id " + t.id + " 场景触发ID " + trigID + " 在变身状态下 触发角色变身非法");
					
					alter = true;
					break;
				case GameData.SCENE_TRIG_BEHAVIOR_QUIT_ALTER:
					if(!alter)
						throw new Exception("主线任务  id " + t.id + " 场景触发ID " + trigID + " 在未变身状态下 触发解除角色变身非法");
					
					alter = false;
					break;
				default:
					break;
				}
			}
			
			if (nextID > 0)
			{
				t = cfgs.get(nextID - 1);
				nextID = GameData.getNextMainTaskId(t, bwType);
				taskCount++;
				
				if(!nextIDs.add(nextID))
					throw new Exception("主线任务 后置任务 " + nextID + " 重复");
			}
		}
		
		if(alter)
			throw new Exception("主线任务完成最后一个任务后还处于变身状态！");
		
		gdCfgs.bwTypeMaintasks.put(bwType, new SBean.MainTaskBWTypeCFGS(taskGrades));
		return taskCount;
	}
	
	public void loadTaskTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart + 0;
				
				gdCfgs.bwTypeMaintasks = new HashMap<>();
				List<SBean.MainTaskCFGS> cfgs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.MainTaskCFGS cfg = new SBean.MainTaskCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					if (cfgs.size() + 1 != cfg.id)
						throw new Exception("主线任务ID " + cfg.id + " 不连续");
					cfg.nextId = excelReadContext.getIntValue(row, col++);
					cfg.nextWhiteId = excelReadContext.getIntValue(row, col++);
					cfg.nextBlackId = excelReadContext.getIntValue(row, col++);
					
					col = 8;
					SBean.TaskCondCFGS taskCondCFGS = new SBean.TaskCondCFGS();
					taskCondCFGS.type = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param1 = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param2 = excelReadContext.getIntValue(row, col++);
					if (taskCondCFGS.type == GameData.TASK_TYPE_USE_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						taskCondCFGS.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						taskCondCFGS.param3 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param4 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param5 = excelReadContext.getIntValue(row, col++);
					}
					taskCondCFGS.param6 = excelReadContext.getIntValue(row, col++);
					this.checkTaskCond(gdCfgs, row, taskCondCFGS);
					cfg.cond = taskCondCFGS;

					col = 17;
					cfg.startNPC = excelReadContext.getIntValue(row, col++);
					if(cfg.startNPC > 0)
						checkNPCID(gdCfgs, row, cfg.startNPC);
					for (int i = 0; i < 4; ++i)
					{
						int dialogueID = excelReadContext.getIntValue(row,  col++);
						if (dialogueID > 0)
							checkDialogIDValid(gdCfgs, row, dialogueID);
						
						int modelID = excelReadContext.getIntValue(row,  col++);
						if (modelID > 0)
							checkModelIDValid(gdCfgs, row, modelID);
					}
					
					cfg.endNPC = excelReadContext.getIntValue(row, col++);
					if(cfg.endNPC > 0)
						checkNPCID(gdCfgs, row, cfg.endNPC);
					for (int i = 0; i < 4; ++i)
					{
						int dialogueID = excelReadContext.getIntValue(row,  col++);
						if (dialogueID > 0)
							checkDialogIDValid(gdCfgs, row, dialogueID);
						
						int modelID = excelReadContext.getIntValue(row,  col++);
						if (modelID > 0)
							checkModelIDValid(gdCfgs, row, modelID);
					}
					
					cfg.rewardItems = new ArrayList<>();
					for (int index = 0; index < 4; index++)
					{
						List<Integer> itemIDs = excelReadContext.getIntegerList(row, col++, ";");
						if (itemIDs.size() != gdCfgs.classRoles.size())
							throw new Exception("row " + row + " 主线任务奖励种类个数和职业类型数目不匹配！");
						
						for(int itemID: itemIDs)
						{
							if(itemID != 0)
								this.checkEntityIDValid(gdCfgs, row, itemID, true);
						}
						
						cfg.rewardItems.add(new SBean.ClassTypeReward(itemIDs, excelReadContext.getIntValue(row, col++)));
					}
					cfg.rewardExp = excelReadContext.getIntValue(row, col++);
					
//					col++;
//					cfg.alterID = excelReadContext.getIntValue(row, col++);
//					if(cfg.alterID > 0)
//						checkTaskAlterIDValid(gdCfgs, row, cfg.alterID);
					
					col = 48;
					cfg.title = excelReadContext.getIntValue(row, col++);
					
					col = 50;
					List<Integer> trigIDs = excelReadContext.getIntegerList(row, col++, ";");
					Set<Integer> all = new HashSet<>();
					cfg.senceTrigIDs = new ArrayList<>();
					for(int tid: trigIDs)
					{
						if(!all.add(tid))
							throw new Exception("row " + row + " 场景触发ID " + tid + " 重复！");
						
						if(tid < 0)
							cfg.senceTrigIDs.add(tid);
						
						checkSenceTrigIDValid(gdCfgs, row, tid);
					}
					cfg.canQuit = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.canQuit);
					cfgs.add(cfg);
					++row;
					col = colStart;
				}
				
				if (cfgs.isEmpty())
					throw new Exception("主线任务未配置！");

				checkMainTask(gdCfgs, cfgs, GameData.BWTYPE_NONE);
				int whiteTaskCount = checkMainTask(gdCfgs, cfgs, GameData.BWTYPE_WHITE);
				int blackTaskCount = checkMainTask(gdCfgs, cfgs, GameData.BWTYPE_BLACK);
				
//				if(whiteTaskCount != blackTaskCount)
//					throw new Exception("正派主线任务数量" + whiteTaskCount + " 和邪派主线任务数量 " + blackTaskCount + " 不一致");
				
				gdCfgs.mainTasks = cfgs;
			}
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 1;
				int col = colStart + 0;

				gdCfgs.weaponTasks = new ArrayList<>();
				gdCfgs.weaponTasks.add(new SBean.WeaponGroupTaskCFGS(0, new ArrayList<>()));
				gdCfgs.weaponTasks.add(new SBean.WeaponGroupTaskCFGS(1, new ArrayList<>()));
				int curId = 1;
				int curType = 0;
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					int type = excelReadContext.getIntValue(row, col++);
					if (type != curType)
					{
						if (type != 1)
							throw new Exception("row " + row + " 神兵任务只有普通任务类型和环任务类型两种!");
						curId = 1;
						curType = 1;
					}
					if (curId++ != id)
						throw new Exception("神兵任务  类型  " + type + " id " + id + " 不连续！");
					SBean.WeaponTaskCFGS cfg = new SBean.WeaponTaskCFGS();
					cfg.type = type;
					cfg.id = id;

					col += 1;
					List<SBean.TaskCondCFGS> conds = new ArrayList<>();
					for (int i = 0; i < 2; ++i)
					{
						int condType = excelReadContext.getIntValue(row, col++);
						if (condType > 0)
						{
							SBean.TaskCondCFGS taskCondCFGS = new SBean.TaskCondCFGS();
							taskCondCFGS.type = condType;
							taskCondCFGS.param1 = excelReadContext.getIntValue(row, col++);
							taskCondCFGS.param2 = excelReadContext.getIntValue(row, col++);
							if (taskCondCFGS.type == GameData.TASK_TYPE_USE_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_NPC)
							{
								taskCondCFGS.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
								taskCondCFGS.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
								taskCondCFGS.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
							}
							else
							{
								taskCondCFGS.param3 = excelReadContext.getIntValue(row, col++);
								taskCondCFGS.param4 = excelReadContext.getIntValue(row, col++);
								taskCondCFGS.param5 = excelReadContext.getIntValue(row, col++);
							}
							taskCondCFGS.param6 = excelReadContext.getIntValue(row, col++);
//							if (i > 0 && conds.size() == i)
//							{
//								if (condType == conds.get(i-1).type)
//									throw new Exception("row " + row + " 同一神兵任务 条件类型相同!");
//							}
							this.checkTaskCond(gdCfgs, row, taskCondCFGS);
							conds.add(taskCondCFGS);
						}
						else
						{
							col += 6;
						}
					}
					if (conds.isEmpty())
						throw new Exception("row " + row + " 神兵任务 条件为空!");
					cfg.conds = conds;
					
					col += 2;
					int iid = excelReadContext.getIntValue(row, col++);
					this.checkEntityIDValid(gdCfgs, row, iid, false);
					int icount = excelReadContext.getIntValue(row, col++);
					if (icount <= 0)
						throw new Exception("row " + row + " 神兵任务奖励物品个数非法!");
					cfg.reward = new SBean.DummyGoods(iid, icount);
					gdCfgs.weaponTasks.get(type).tasks.add(cfg);

					++row;
					col = colStart;
				}
				
			}
			excelReadContext.ReadSheet(2);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 1;
				int col = colStart + 0;

				List<SBean.PetTaskCFGS> cfgs = new ArrayList<>();
				int curId = 1;
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					if (curId++ != id)
						throw new Exception("佣兵任务ID " + id + " 不连续");
					SBean.PetTaskCFGS cfg = new SBean.PetTaskCFGS();
					cfg.id = id;

					col += 1;
					SBean.TaskCondCFGS taskCondCFGS = new SBean.TaskCondCFGS();
					taskCondCFGS.type = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param1 = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param2 = excelReadContext.getIntValue(row, col++);
					if (taskCondCFGS.type == GameData.TASK_TYPE_USE_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						taskCondCFGS.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						taskCondCFGS.param3 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param4 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param5 = excelReadContext.getIntValue(row, col++);
					}
					taskCondCFGS.param6 = excelReadContext.getIntValue(row, col++);
					this.checkTaskCond(gdCfgs, row, taskCondCFGS);
					cfg.cond = taskCondCFGS;

					col += 2;
					cfg.friendliness = excelReadContext.getIntValue(row, col++);
					if (cfg.friendliness <= 0)
						throw new Exception("row " + row + " 合修经验奖励值为0或负数！");
					cfg.coinnum = excelReadContext.getIntValue(row, ++col);
					if (cfg.coinnum <= 0)
						throw new Exception("row " + row + " 绑定铜钱奖励值为0或负数！");
					cfg.quickfinishdiamond = excelReadContext.getIntValue(row, ++col);
					if (cfg.quickfinishdiamond <= 0)
						throw new Exception("row " + row + " 立即完成消耗钻石为0或负数！");
					cfgs.add(cfg);

					++row;
					col = colStart;
				}
				gdCfgs.petTasks = cfgs;
			}

			excelReadContext.ReadSheet(3);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 1;
				int col = colStart + 0;

				List<SBean.SectTaskCFGS> cfgs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SectTaskCFGS sectTaskCFGS = new SBean.SectTaskCFGS();
					sectTaskCFGS.id = excelReadContext.getIntValue(row, col++);
					col += 2;
					sectTaskCFGS.star = excelReadContext.getIntValue(row, col++);

					SBean.TaskCondCFGS taskCondCFGS = new SBean.TaskCondCFGS();
					taskCondCFGS.type = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param1 = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param2 = excelReadContext.getIntValue(row, col++);
					if (taskCondCFGS.type == GameData.TASK_TYPE_USE_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						taskCondCFGS.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						taskCondCFGS.param3 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param4 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param5 = excelReadContext.getIntValue(row, col++);
					}
					taskCondCFGS.param6 = excelReadContext.getIntValue(row, col++);
					this.checkTaskCond(gdCfgs, row, taskCondCFGS);
					sectTaskCFGS.cond = taskCondCFGS;

					col += 11;

					sectTaskCFGS.finisheNPC = excelReadContext.getIntValue(row, col++);

					col += 8;

					sectTaskCFGS.rewardCon = excelReadContext.getIntValue(row, col++);
					sectTaskCFGS.rewardExpFactor = excelReadContext.getFloatValue(row, col++);

					sectTaskCFGS.taskRewards = new ArrayList<>();
					for (int index = 0; index < 6; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						this.checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
						{
							SBean.DummyGoods good = new SBean.DummyGoods(id, count);
							sectTaskCFGS.taskRewards.add(good);
						}
					}

					sectTaskCFGS.sharedRewardCon = excelReadContext.getIntValue(row, col++);
					sectTaskCFGS.sharedRewardExpFactor = excelReadContext.getFloatValue(row, col++);

					sectTaskCFGS.shareRewards = new ArrayList<>();
					for (int index = 0; index < 2; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						this.checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
						{
							SBean.DummyGoods good = new SBean.DummyGoods(id, count);
							sectTaskCFGS.shareRewards.add(good);
						}
					}

					cfgs.add(sectTaskCFGS);
					++row;
					col = colStart;
				}
				gdCfgs.sectTasks = cfgs;
			}
			
			excelReadContext.ReadSheet(4);
			{
				final	 int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.BranchTaskCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.BranchTaskDataCFGS cfg = new SBean.BranchTaskDataCFGS();
					cfg.groupId = excelReadContext.getIntValue(row, colStart);
					col = colStart + 1;
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.nextId = excelReadContext.getIntValue(row, col++);
					SBean.BranchTaskCFGS taskCfg = cfgMap.get(cfg.groupId);
					if(taskCfg == null)
					{
						cfg.accCondType = excelReadContext.getIntValue(row, col++);
						switch(cfg.accCondType)
						{
						case GameData.BRANCH_TASK_ACCESS_TYPE_LEVEL:
						case GameData.BRANCH_TASK_ACCESS_TYPE_POWER:
						case GameData.BRANCH_TASK_ACCESS_TYPE_MAIN_TASK:
						case GameData.BRANCH_TASK_ACCESS_TYPE_BRANCH_GROUP:
						case GameData.BRANCH_TASK_ACCESS_TYPE_CLIENT:
						case GameData.BRANCH_TASK_ACCESS_TYPE_LOGINDAYS:
							break;
						default:
							throw new Exception("row " + row +  " col " + col + "未知的接取条件类型！");
						}
						
						cfg.accCondParam = excelReadContext.getIntValue(row, col++);
						cfg.accCondBWType = excelReadContext.getByteValue(row, col++);
						switch (cfg.accCondBWType)
						{
						case GameData.BWTYPE_NONE:
						case GameData.BWTYPE_WHITE:
						case GameData.BWTYPE_BLACK:
							break;
						default:
							throw new Exception("row " + row + " col " + col + " 接取条件阵营 " + cfg.accCondBWType);
						}
						taskCfg =  new SBean.BranchTaskCFGS(new ArrayList<>());
						cfgMap.put(cfg.groupId, taskCfg);
					}
					
					if (cfg.id != taskCfg.datas.size() + 1)
						throw new Exception("支线任务row " + row + " 支线任务ID " + cfg.id + " 不连续！");
					
					col = 8;
					SBean.TaskCondCFGS cond = new SBean.TaskCondCFGS();
					cond.type = excelReadContext.getIntValue(row, col++);
					cond.param1 = excelReadContext.getIntValue(row, col++);
					cond.param2 = excelReadContext.getIntValue(row, col++);
					if (cond.type == GameData.TASK_TYPE_USE_ITEM || cond.type == GameData.TASK_TYPE_CONVOY_ITEM || cond.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						cond.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cond.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cond.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						cond.param3 = excelReadContext.getIntValue(row, col++);
						cond.param4 = excelReadContext.getIntValue(row, col++);
						cond.param5 = excelReadContext.getIntValue(row, col++);
					}
					cond.param6 = excelReadContext.getIntValue(row, col++);
					this.checkTaskCond(gdCfgs, row, cond);
					cfg.taskParam = cond;
					col++;
					cfg.startNpc = excelReadContext.getIntValue(row, col++);
					col += 8;
					cfg.endNpc = excelReadContext.getIntValue(row, col++);
					col += 8;
					
					cfg.reward = new ArrayList<>();
					for (int index = 0; index < 4; index++)
					{
						List<Integer> itemIDs = excelReadContext.getIntegerList(row, col++, ";");
						if (itemIDs.size() != gdCfgs.classRoles.size())
							throw new Exception("row " + row + " 主线任务奖励种类个数和职业类型数目不匹配！");
						
						for(int itemID: itemIDs)
						{
							if(itemID != 0)
								this.checkEntityIDValid(gdCfgs, row, itemID, true);
						}
						
						cfg.reward.add(new SBean.ClassTypeReward(itemIDs, excelReadContext.getIntValue(row, col++)));
					}
					
					col++;
					cfg.rewardExp = excelReadContext.getIntValue(row, col++);
					
					col = 45;
					List<Integer> trigIDs = excelReadContext.getIntegerList(row, col++, ";");
					Set<Integer> all = new HashSet<>();
					cfg.senceTrigIDs = new ArrayList<>();
					for(int tid: trigIDs)
					{
						if(!all.add(tid))
							throw new Exception("row " + row + " 场景触发ID " + tid + " 重复！");
						
						if(tid < 0)
							cfg.senceTrigIDs.add(tid);
						
						checkSenceTrigIDValid(gdCfgs, row, tid);
					}
					cfg.canQuit = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.canQuit);
					taskCfg.datas.add(cfg);
					++row;
				}
				gdCfgs.branchTask = cfgMap;
			}
			
			excelReadContext.ReadSheet(5);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				Map<Integer, SBean.PetLifeTaskGroupCFGS> cfgMap = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.PetLifeTaskCFGS cfg = new SBean.PetLifeTaskCFGS();
					cfg.petid = excelReadContext.getIntValue(row, col++);
					cfg.id = excelReadContext.getIntValue(row, col++);
//					checkPetValid(gdCfgs, row, cfg.petid);
					cfg.nextId = excelReadContext.getIntValue(row, col);
					col += 3;
					SBean.TaskCondCFGS taskCondCFGS = new SBean.TaskCondCFGS();
					taskCondCFGS.type = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param1 = excelReadContext.getIntValue(row, col++);
					taskCondCFGS.param2 = excelReadContext.getIntValue(row, col++);
					if (taskCondCFGS.type == GameData.TASK_TYPE_USE_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_ITEM || taskCondCFGS.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						taskCondCFGS.param3 = (int) (excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param4 = (int) (excelReadContext.getFloatValue(row, col++) * 100);
						taskCondCFGS.param5 = (int) (excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						taskCondCFGS.param3 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param4 = excelReadContext.getIntValue(row, col++);
						taskCondCFGS.param5 = excelReadContext.getIntValue(row, col++);
					}
					taskCondCFGS.param6 = excelReadContext.getIntValue(row, col);
					this.checkTaskCond(gdCfgs, row, taskCondCFGS);
					cfg.cond = taskCondCFGS;
					col += 2;
					cfg.startNpcId = excelReadContext.getIntValue(row, col);
					col += 9;
					cfg.endNpcId = excelReadContext.getIntValue(row, col);
					col += 9;
					cfg.rewardItems = new ArrayList<SBean.DummyGoods>();
					for (int i = 0; i < 4; i++)
					{
						int id = excelReadContext.getIntValue(row, col);
						if (id != 0)
						{
							checkEntityIDValid(gdCfgs, row, id, false);
							int count = excelReadContext.getIntValue(row, col + 1);
							if (count <= 0)
								throw new Exception("row " + row + " 佣兵身世任务奖励数量必须大于0！");
							cfg.rewardItems.add(new SBean.DummyGoods(id, count));
						}
						col += 2;
					}
					col += 1;
					cfg.mapCopyId = excelReadContext.getIntValue(row, col);
					if (!cfgMap.containsKey(cfg.petid))
					{
						cfgMap.put(cfg.petid, new SBean.PetLifeTaskGroupCFGS(new ArrayList<SBean.PetLifeTaskCFGS>()));
					}
					int cursize = cfgMap.get(cfg.petid).petLifeTasks.size();
					if (cursize != 0 && cfgMap.get(cfg.petid).petLifeTasks.get(cursize - 1).nextId != cfg.id)
						throw new Exception("row " + row + " 佣兵身世任务不连续！");
					cfgMap.get(cfg.petid).petLifeTasks.add(cfg);
					++row;
				}
				gdCfgs.petLifeTasks = cfgMap;
			}
			
			excelReadContext.ReadSheet(6);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.SecretAreaTaskCFGS> areaCfg = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.SecretAreaTaskCFGS dataCfg = new SBean.SecretAreaTaskCFGS();
					dataCfg.id = excelReadContext.getIntValue(row, col++);
					col += 2;
					SBean.TaskCondCFGS cond = new SBean.TaskCondCFGS();
					cond.type = excelReadContext.getIntValue(row, col++);
					cond.param1 = excelReadContext.getIntValue(row, col++);
					cond.param2 = excelReadContext.getIntValue(row, col++);
					if (cond.type == GameData.TASK_TYPE_USE_ITEM || cond.type == GameData.TASK_TYPE_CONVOY_ITEM || cond.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						cond.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cond.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cond.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						cond.param3 = excelReadContext.getIntValue(row, col++);
						cond.param4 = excelReadContext.getIntValue(row, col++);
						cond.param5 = excelReadContext.getIntValue(row, col++);
					}
					cond.param6 = excelReadContext.getIntValue(row, col++);
					this.checkTaskCond(gdCfgs, row, cond);
					dataCfg.taskCond = cond;
					col++;
					List<SBean.DummyGoods> rewards = new ArrayList<>();
					for (int i = 0; i < 4; ++i)
					{
						int rewardId = excelReadContext.getIntValue(row, col++);
						int rewardCount = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, rewardId, true);
						if (rewardId != 0)
							rewards.add(new SBean.DummyGoods(rewardId, rewardCount));
					}
					dataCfg.rewards = rewards;
					col += 1;
					dataCfg.mapId = excelReadContext.getIntValue(row, col++);
					areaCfg.add(dataCfg);
					row++;
				}
				gdCfgs.secretAreaData = areaCfg;
			}
			
			excelReadContext.ReadSheet(7);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.MrgSeriesTaskGroupCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MrgSeriesTaskCFGS cfg = new SBean.MrgSeriesTaskCFGS();
					cfg.groupID = excelReadContext.getIntValue(row, col++);
					SBean.MrgSeriesTaskGroupCFGS groupCfg = cfgs.get(cfg.groupID);
					if(groupCfg == null)
					{
						groupCfg = new SBean.MrgSeriesTaskGroupCFGS(new ArrayList<>());
						cfgs.put(cfg.groupID, groupCfg);
					}
					
					cfg.taskID = excelReadContext.getIntValue(row, col++);
					cfg.nextID = excelReadContext.getIntValue(row, col++);
					cfg.nextGroup = excelReadContext.getIntValue(row, col++);
					
					col = 5;
					cfg.cond = new SBean.TaskCondCFGS();
					cfg.cond.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.cond.type) 
					{
					case GameData.TASK_TYPE_KILL: 			
					case GameData.TASK_TYPE_GATHER:
					case GameData.TASK_TYPE_USE_ITEM:
					case GameData.TASK_TYPE_SUBMIT_ITEM:
					case GameData.TASK_TYPE_NPC_TALK:
					case GameData.TASK_TYPE_CONVOY_NPC:
					case GameData.TASK_TYPE_RAND_QUESTION:
						break;
					default:
						throw new Exception("row " + row + " 姻缘系列任务 " + cfg.groupID + " , " + cfg.taskID + " 任务条件类型 " + cfg.cond.type + " 非法");
					}
					
					cfg.cond.param1 = excelReadContext.getIntValue(row, col++);
					cfg.cond.param2 = excelReadContext.getIntValue(row, col++);
					if (cfg.cond.type == GameData.TASK_TYPE_USE_ITEM || cfg.cond.type == GameData.TASK_TYPE_CONVOY_ITEM || cfg.cond.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						cfg.cond.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cfg.cond.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cfg.cond.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						cfg.cond.param3 = excelReadContext.getIntValue(row, col++);
						cfg.cond.param4 = excelReadContext.getIntValue(row, col++);
						cfg.cond.param5 = excelReadContext.getIntValue(row, col++);
					}
					cfg.cond.param6 = excelReadContext.getIntValue(row, col++);
					this.checkTaskCond(gdCfgs, row, cfg.cond);
					
					col = 14;
					cfg.startNPC = excelReadContext.getIntValue(row, col++);
					if(cfg.startNPC > 0)
						checkNPCID(gdCfgs, row, cfg.startNPC);
					for (int i = 0; i < 4; ++i)
					{
						int dialogueID = excelReadContext.getIntValue(row,  col++);
						if (dialogueID > 0)
							checkDialogIDValid(gdCfgs, row, dialogueID);
						
						int modelID = excelReadContext.getIntValue(row,  col++);
						if (modelID > 0)
							checkModelIDValid(gdCfgs, row, modelID);
					}
					
					col = 24;
					cfg.endNPC = excelReadContext.getIntValue(row, col++);
					if(cfg.endNPC > 0)
						checkNPCID(gdCfgs, row, cfg.endNPC);
					for (int i = 0; i < 4; ++i)
					{
						int dialogueID = excelReadContext.getIntValue(row,  col++);
						if (dialogueID > 0)
							checkDialogIDValid(gdCfgs, row, dialogueID);
						
						int modelID = excelReadContext.getIntValue(row,  col++);
						if (modelID > 0)
							checkModelIDValid(gdCfgs, row, modelID);
					}
					
					List<Integer> trigIDs = excelReadContext.getIntegerList(row, col++, ";");
					Set<Integer> all = new HashSet<>();
					cfg.sceneTrigIDs = new ArrayList<>();
					for(int tid: trigIDs)
					{
						if(!all.add(tid))
							throw new Exception("row " + row + " 姻缘系列任务场景触发ID " + tid + " 重复！");
						
						if(tid < 0)
							cfg.sceneTrigIDs.add(tid);
						checkSenceTrigIDValid(gdCfgs, row, tid);
					}
					
					cfg.mrgExp = excelReadContext.getIntValue(row, col++);
					cfg.expFactor = excelReadContext.getFloatValue(row, col++);
					
					cfg.rewards = new ArrayList<>();
					for (int index = 0; index < 4; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						this.checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
						{
							SBean.DummyGoods goods = new SBean.DummyGoods(id, count);
							cfg.rewards.add(goods);
						}
					}
					
					int bindCoinReward = excelReadContext.getIntValue(row, col++);
					if(bindCoinReward > 0)
						cfg.rewards.add(new SBean.DummyGoods(GameData.COMMON_ITEM_ID_COIN, bindCoinReward));
					
					groupCfg.tasks.add(cfg);
					if(groupCfg.tasks.size() != cfg.taskID)
						throw new Exception("row " + row + " 姻缘系列任务" + " 任务ID " + cfg.taskID + " 不连续！");
					
					row++;
				}
				
				gdCfgs.mrgSeriesTaskGroups = cfgs;
			}
			
			excelReadContext.ReadSheet(8);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.MrgLoopTaskCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.MrgLoopTaskCFGS cfg = new SBean.MrgLoopTaskCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = 2;
					cfg.cond = new SBean.TaskCondCFGS();
					cfg.cond.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.cond.type) 
					{
					case GameData.TASK_TYPE_KILL: 			
					case GameData.TASK_TYPE_GATHER:
					case GameData.TASK_TYPE_USE_ITEM:
					case GameData.TASK_TYPE_SUBMIT_ITEM:
					case GameData.TASK_TYPE_NPC_TALK:
					case GameData.TASK_TYPE_CONVOY_NPC:
					case GameData.TASK_TYPE_RAND_QUESTION:
						break;
					default:
						throw new Exception("row " + row + " 姻缘随机任务 " + cfg.id + " 任务条件类型 " + cfg.cond.type + " 非法");
					}
					
					cfg.cond.param1 = excelReadContext.getIntValue(row, col++);
					cfg.cond.param2 = excelReadContext.getIntValue(row, col++);
					if (cfg.cond.type == GameData.TASK_TYPE_USE_ITEM || cfg.cond.type == GameData.TASK_TYPE_CONVOY_ITEM || cfg.cond.type == GameData.TASK_TYPE_CONVOY_NPC)
					{
						cfg.cond.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cfg.cond.param4 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cfg.cond.param5 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
					}
					else
					{
						cfg.cond.param3 = excelReadContext.getIntValue(row, col++);
						cfg.cond.param4 = excelReadContext.getIntValue(row, col++);
						cfg.cond.param5 = excelReadContext.getIntValue(row, col++);
					}
					cfg.cond.param6 = excelReadContext.getIntValue(row, col++);
					this.checkTaskCond(gdCfgs, row, cfg.cond);
					
					col = 10;
					cfg.startNPC = excelReadContext.getIntValue(row, col++);
					if(cfg.startNPC > 0)
						checkNPCID(gdCfgs, row, cfg.startNPC);
					for (int i = 0; i < 4; ++i)
					{
						int dialogueID = excelReadContext.getIntValue(row,  col++);
						if (dialogueID > 0)
							checkDialogIDValid(gdCfgs, row, dialogueID);
						
						int modelID = excelReadContext.getIntValue(row,  col++);
						if (modelID > 0)
							checkModelIDValid(gdCfgs, row, modelID);
					}
					
					cfg.endNPC = excelReadContext.getIntValue(row, col++);
					if(cfg.endNPC > 0)
						checkNPCID(gdCfgs, row, cfg.endNPC);
					for (int i = 0; i < 4; ++i)
					{
						int dialogueID = excelReadContext.getIntValue(row,  col++);
						if (dialogueID > 0)
							checkDialogIDValid(gdCfgs, row, dialogueID);
						
						int modelID = excelReadContext.getIntValue(row,  col++);
						if (modelID > 0)
							checkModelIDValid(gdCfgs, row, modelID);
					}
					List<Integer> trigIDs = excelReadContext.getIntegerList(row, col++, ";");
					Set<Integer> all = new HashSet<>();
					cfg.sceneTrigIDs = new ArrayList<>();
					for(int tid: trigIDs)
					{
						if(!all.add(tid))
							throw new Exception("row " + row + " 姻缘环任务场景触发ID " + tid + " 重复！");
						
						if(tid < 0)
						{
							throw new Exception("row " + row + " 姻缘环任务场景触发ID " + tid + " 非法!");
//							cfg.sceneTrigIDs.add(tid);
						}
						checkSenceTrigIDValid(gdCfgs, row, tid);
					}
					
					col = 30;
					cfg.mrgExp = excelReadContext.getIntValue(row, col++);
					cfg.expFactor = excelReadContext.getFloatValue(row, col++);
					cfg.rewards = new ArrayList<>();
					for (int index = 0; index < 4; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						this.checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
						{
							SBean.DummyGoods goods = new SBean.DummyGoods(id, count);
							cfg.rewards.add(goods);
						}
					}
					
					int bindCoinReward = excelReadContext.getIntValue(row, col++);
					if(bindCoinReward > 0)
						cfg.rewards.add(new SBean.DummyGoods(GameData.COMMON_ITEM_ID_COIN, bindCoinReward));
					
					cfgs.add(cfg);
					if(cfgs.size() != cfg.id)
						throw new Exception("row " + row + " 姻缘随机任务 " + cfg.id + " 不连续！");
					
					row++;
				}
				
				gdCfgs.mrgLoopTasks = cfgs;
			}
		}

		System.out.println("load table " + fileName + " success.");
	}
	
	private void checkPetValid(GameDataCFGS gdCfgs, int row, int petid) throws Exception
	{
		if(petid > gdCfgs.pets.size())
			throw new Exception("row " + row + "佣兵ID不存在！");
	}

	public void loadTaskSpecialTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.AlterCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.AlterCFGS cfg = new SBean.AlterCFGS(0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, 0, new HashSet<>(), 0);
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 4;
					for(int i=0; i<2; i++)
					{
						int skillID = excelReadContext.getIntValue(row, col++);
						checkSkillIDValid(gdCfgs, row, skillID);
						cfg.attacks.add(skillID);
					}
					
					for(int i=0; i<4; i++)
					{
						int skillID = excelReadContext.getIntValue(row, col++);
						int skillLvl = excelReadContext.getIntValue(row, col++);
						if(skillID > 0)
						{
							checkSkillIDAndLevelValid(gdCfgs, row, skillID, skillLvl);
							cfg.skills.add(new SBean.SkillBriefCFGS(skillID, skillLvl));
						}
					}
					
					for(int i=0; i<4; i++)
					{
						int propID = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						if(propID > 0)
						{
							checkPropertyIDValid(gdCfgs, row, propID);
							cfg.attrs.add(new SBean.AttrCFGS(propID, value));
						}
					}
					
					cfg.loopTime = excelReadContext.getIntValue(row, col++);
					cfg.speed = excelReadContext.getIntValue(row, col++);
					List<Integer> buffs = excelReadContext.getIntegerList(row, col++, ";");
					for(int buffID: buffs)
					{
						if(buffID > 0)
						{
							checkBuffIDValid(gdCfgs, row, buffID);
							cfg.buffs.add(buffID);
						}
					}
					
					col = 31;
					int dodge = excelReadContext.getIntValue(row, col++);
					if(dodge > 0)
					{
						cfg.dodge = dodge;
						checkSkillIDValid(gdCfgs, row, dodge);
					}
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("变身表ID " + cfg.id + " 重复！");
					
					row++;
				}
				gdCfgs.alters = cfgMap;
			}
		}
	}
	
	public void loadUpLvlExp(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				List<SBean.LevelCFGS> lvllst = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;
				int curLvl = 1;
				while (excelReadContext.isNotEmpty(row, col))
				{

					int lvl = excelReadContext.getShortValue(row, col++);
					if (curLvl++ != lvl)
						throw new Exception("升级经验不连续，缺少等级" + curLvl);
					long exp = excelReadContext.getLongValue(row, col++);
					if(exp > GameData.ROLE_LEVEL_EXP_MAX)
						throw new Exception("row " + row + " 等级 " + lvl + " 经验值 " + exp + " 超上限 " + GameData.ROLE_LEVEL_EXP_MAX);
					
					List<Integer> weaponRingTasks = excelReadContext.getIntegerList(row, col++, ";");
					if (weaponRingTasks.isEmpty())
						throw new Exception("等级 " + lvl + " 无任何神兵环任务");
					for (int wtId : weaponRingTasks)
					{
						this.checkWeaponRingTaskID(gdCfgs, row, wtId);
					}
					int maxVit = excelReadContext.getIntValue(row, col++);
					int addVit = excelReadContext.getIntValue(row, col++);
					int dailyTaskBaseExp = excelReadContext.getIntValue(row, col++);
					int sectTaskBaseExp = excelReadContext.getIntValue(row, col++);
					int quizBaseExp = excelReadContext.getIntValue(row, col++);
					int offlineBaseExp = excelReadContext.getIntValue(row, col++);
					List<Integer> secretTasks = excelReadContext.getIntegerList(row, col++, ";");
					if (secretTasks.isEmpty())
						throw new Exception("等级 " + lvl + " 无对应的秘境任务！");
					for (int id : secretTasks)
					{
						if (id > gdCfgs.secretAreaData.size())
							throw new Exception("row " + row + " 秘境任务id " + id + " 秘境任务表中无此对应任务！");
					}
					int sectDeliverBaseExp = excelReadContext.getIntValue(row, col++);
					int sectDeliverBaseGold = excelReadContext.getIntValue(row, col++);
					int maxSnatchRedEnvelopeTimes = excelReadContext.getIntValue(row, col++);
					
					col = 16;
					int mrgTaskBaseExp = excelReadContext.getIntValue(row, col++);
					
					Set<Integer> allTasks = new HashSet<>();
					List<Integer> tasks = excelReadContext.getIntegerList(row, col++, ";");
					if(tasks.isEmpty())
						throw new Exception("row " + row + " lvl " + lvl + " 姻缘环任务集合为空！");
					
					for(int taskID: tasks)
					{
						if(!allTasks.add(taskID))
							throw new Exception("row " + row + " lvl " + lvl + " 姻缘环任务 " + taskID + " 重复!");
						
						if(taskID <= 0 || taskID > gdCfgs.mrgLoopTasks.size())
							throw new Exception("row " + row + " lvl " + lvl + " 姻缘环任务 " + taskID + " 非法!");
					}
					
					int demonHoleBaseExp = excelReadContext.getIntValue(row, col++);
					int credit = excelReadContext.getIntValue(row, col++);
					
					lvllst.add(new SBean.LevelCFGS(lvl, exp, weaponRingTasks, maxVit, addVit, dailyTaskBaseExp, sectTaskBaseExp, quizBaseExp, offlineBaseExp, secretTasks, sectDeliverBaseGold, sectDeliverBaseExp, maxSnatchRedEnvelopeTimes, mrgTaskBaseExp, tasks, demonHoleBaseExp, credit));
					
					++row;
					col = colStart;
				}
				gdCfgs.levels = lvllst;
			}
			
			excelReadContext.ReadSheet(1);
			{
				int row = 0;
				int col = 0;
				SBean.LevelLimitCFGS cfg = new SBean.LevelLimitCFGS();
				final int rowBase = excelReadContext.locateColumnTag(0, "基础配置");
				{
					row = rowBase + 1;
					col = 2;
					cfg.base = new SBean.LevelLimitBaseCFGS();
					cfg.base.limitLvl = excelReadContext.getIntValue(row++, col);
					if(cfg.base.limitLvl >= gdCfgs.levels.size())
						throw new Exception("row " + row + " 封印等级 " + cfg.base.limitLvl + " 大于等于当前最大等级 " + gdCfgs.levels.size());
					
					int expReduce = excelReadContext.getIntValue(row++, col);
					cfg.base.expRate = (100.f - expReduce) / 100.f;
					if(cfg.base.expRate <= 0)
						throw new Exception("row " + row + " 经验削减百分比 " + expReduce + " 非法!");
					
					int mul = excelReadContext.getIntValue(row++, col);
					if(mul - 1 <= 0)
						throw new Exception("row " + row + " 经验上限 " + mul + " 非法!");
					
					SBean.LevelCFGS nextCfg = gdCfgs.levels.get(cfg.base.limitLvl);
					cfg.base.expVolume = nextCfg.exp * (mul - 1);
				}
				
				final int rowSpeedUp = excelReadContext.locateColumnTag(0, "冲关配置");
				{
					row = rowSpeedUp + 1;
					col = 2;
					cfg.speedUp = new SBean.SpeedUpCFGS(new ArrayList<>(), 0, new ArrayList<>());
					cfg.speedUp.lvls = excelReadContext.getIntegerList(row++, col, ";");
					if(cfg.speedUp.lvls.size() < 2)
						throw new Exception("row " + row + " 前几天的冲关等级 " + cfg.speedUp.lvls + " 数量非法!");
					
					cfg.speedUp.rank = excelReadContext.getIntValue(row++, col);
				}
				
				final int rowSpeedUpAdd = excelReadContext.locateColumnTag(0, "冲关等级经验加成");
				{
					row = rowSpeedUpAdd + 1;
					col = 1;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int diff = excelReadContext.getIntValue(row, col);
						int add = excelReadContext.getIntValue(row++, col + 1);
						if(add <= 0)
							throw new Exception("row " + row + " 加成百分比 " + add + " 非法!");
						
						cfg.speedUp.adds.add(new SBean.SpeedUpAdd(diff, add / 100.f));
					}
				}
				
				gdCfgs.levelLimit = cfg;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}


	

	public void loadPropAward(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				List<SBean.PropAwardCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PropAwardCFGS cfg = new SBean.PropAwardCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);

					col = colStart + 2;
					cfg.conditionType = excelReadContext.getIntValue(row, col++);
					cfg.conditionArg = excelReadContext.getIntValue(row, col++);
					cfg.awards = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						SBean.AttrCFGS award = new SBean.AttrCFGS();
						award.id = excelReadContext.getIntValue(row, col++);
						if (award.id <= 0)
						{
							break;
						}
						this.checkPropertyIDValid(gdCfgs, row, award.id);
						award.value = excelReadContext.getIntValue(row, col++);
						cfg.awards.add(award);
					}

					if (propAwardMap.containsKey(cfg.id))
						throw new Exception("属性奖励ID重复， id = " + cfg.id);

					cfgList.add(cfg);
					propAwardMap.put(cfg.id, cfg);
					row++;
				}
				gdCfgs.propaward = cfgList;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadEquipSlotTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;

				List<SBean.EquipSlotCFGS> slotList = new ArrayList<>();

				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.EquipSlotCFGS slotCFGS = new SBean.EquipSlotCFGS();
					slotCFGS.id = excelReadContext.getIntValue(row, col++);
					col += 1;
					slotCFGS.slot = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						int solttype = 0;
						List<Integer> vs = excelReadContext.getIntegerList(row, col++, ";");
						for (int j = 0; j < vs.size(); j++)
						{
							int v = vs.get(j);
							if (v != 0)
							{
								if (v < 0 || v > 20)
									throw new Exception("装备部位 插槽类型 为 " + v + " 未知！");
								solttype |= (1 << (v - 1));
							}
						}
						slotCFGS.slot.add(solttype);
					}

					slotList.add(slotCFGS);

					++row;
					col = colStart;
				}

				gdCfgs.slots = slotList;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				gdCfgs.starAdditionProp = new ArrayList<>();
				for (int i=0;i<gdCfgs.slots.size();i++)
				{
					gdCfgs.starAdditionProp.add(new SBean.EquipStarAddPropCFGS(new HashMap<>()));
				}
				while (excelReadContext.isNotEmpty(row, col))
				{
					int part = excelReadContext.getIntValue(row, col++);
					if (part <= 0 || part > gdCfgs.starAdditionProp.size())
						throw new Exception("row " + row + " 不存在的部位 " + part);
					int star = excelReadContext.getIntValue(row, col++);
					if (gdCfgs.starAdditionProp.get(part - 1).additionProp.containsKey(star))
						throw new Exception("row " + row + " 重复的星级属性 " + star);
					col++;
					List<SBean.AttrCFGS> attrs = new ArrayList<>();
					for (int index = 0; index < 3; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if (id > 0)
						{
							checkPropertyIDValid(gdCfgs, row, id);
							attrs.add(new SBean.AttrCFGS(id, count));
						}
					}
					gdCfgs.starAdditionProp.get(part - 1).additionProp.put(star, new SBean.StarAdditionProp(attrs));
					++row;
					col = colStart;
				}
			}

		}

		System.out.println("load table " + fileName + " success.");

	}

	

	public void loadWeaponTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			Map<Integer, SBean.WeaponCFGS> cfgs = new TreeMap<>();
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.WeaponCFGS cfg = new SBean.WeaponCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col = col + 3;
					cfg.useId = excelReadContext.getIntValue(row, col++);
					if (!checkItemIDValid(gdCfgs, cfg.useId))
						throw new Exception("道具id错误，  id = " + cfg.useId);

					cfg.useCount = excelReadContext.getIntValue(row, col++);

					cfg.skills = new ArrayList<>(4);
					for (int i = 0; i < 4; i++)
					{
						cfg.skills.add(excelReadContext.getIntValue(row, col++));
					}

					cfg.initStar = excelReadContext.getIntValue(row, col++);
					cfg.conTime = excelReadContext.getIntValue(row, col++);
					cfg.varyType = excelReadContext.getIntValue(row, col++);
					cfg.varyParam = excelReadContext.getIntValue(row, col++);
					
					col = 20;
					cfg.isUSkillOpen = excelReadContext.getByteValue(row, col);
					checkBoolean(row, cfg.isUSkillOpen);
					
					col = 22;
					cfg.addMaster = excelReadContext.getIntValue(row, col++);
					cfg.maxMaster = excelReadContext.getIntValue(row, col++);
					
					col = 38;
					cfg.dodge = excelReadContext.getIntValue(row, col++);
					checkSkillIDValid(gdCfgs, row, cfg.dodge);
					
					cfg.weaponLevel = new ArrayList<>();
					cfg.weaponStar = new ArrayList<>();
					cfg.uniqueSkills = new HashSet<>();
					
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("神兵ID重复， id = " + cfg.id);
					row++;
					col = colStart;
				}
				
				gdCfgs.weapons = cfgs;
			}

			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.WeaponLevelCFGS cfg = new SBean.WeaponLevelCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.level = excelReadContext.getIntValue(row, col++);
					cfg.roleLvlReq = excelReadContext.getIntValue(row, col++);
					cfg.exp = excelReadContext.getIntValue(row, col++);
					cfg.useDiamond = excelReadContext.getIntValue(row, col++);

					cfg.attrs = new ArrayList<>(4);
					for (int i = 0; i < 4; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.value = excelReadContext.getIntValue(row, col++);
						if (attr.id > 0)
						{
							cfg.attrs.add(attr);
						}
					}

					cfg.items = new ArrayList<>(3);
					String itemIdString = excelReadContext.getStringValue(row, col++);
					String[] ids = itemIdString.split(";");
					for (String id : ids)
					{
						int idd = Integer.valueOf(id);
						if (!this.checkItemIDValid(gdCfgs, idd))
						{
							throw new Exception("道具id错误，  id = " + idd);
						}
						else
						{
							cfg.items.add(idd);
						}
					}

					SBean.WeaponCFGS wcfg = cfgs.get(cfg.id);
					if (wcfg == null)
						throw new Exception("神兵 升级表中 id = " + cfg.id + " 的神兵不存在!");
					if (wcfg.weaponLevel.size() + 1 != cfg.level)
						throw new Exception("神兵 升级表中 id = " + cfg.id + " 的神兵升级数据 在等级 " + cfg.level + "不连续！");
					wcfg.weaponLevel.add(cfg);
					
					row++;
					col = colStart;
				}
				
				for(SBean.WeaponCFGS weaponCFGS : cfgs.values())
				{
					int length = weaponCFGS.weaponLevel.size();
					for(int index = 0; index < length; index ++)
					{
						SBean.WeaponLevelCFGS firstCFGS = weaponCFGS.weaponLevel.get(index);
						firstCFGS.useDiamonds = new TreeMap<Integer, Integer>();
						for(int pos = index + 1; pos < length; pos ++)
						{
							SBean.WeaponLevelCFGS curCFGS = weaponCFGS.weaponLevel.get(pos);
							Integer upCFGS = firstCFGS.useDiamonds.get(curCFGS.level - 1);
							if(upCFGS != null)
							{
								firstCFGS.useDiamonds.put(curCFGS.level, curCFGS.useDiamond + upCFGS);
							}else{
								firstCFGS.useDiamonds.put(curCFGS.level, curCFGS.useDiamond);
							}
						}
					}
				}
			}

			excelReadContext.ReadSheet(2);
			{
				List<SBean.WeaponStarCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.WeaponStarCFGS cfg = new SBean.WeaponStarCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.star = excelReadContext.getIntValue(row, col++);
					cfg.useId = excelReadContext.getIntValue(row, col++);
					cfg.useCount = excelReadContext.getIntValue(row, col++);
					if (cfg.useId != 0)
					{
						if (!this.checkItemIDValid(gdCfgs, cfg.useId))
							throw new Exception("道具id错误，  id = " + cfg.useId);
						if (cfg.useCount <= 0)
							throw new Exception("道具id错误，  id = " + cfg.useId + " 消耗的个数不大于0！");
					}
					cfg.replaceId = excelReadContext.getIntValue(row, col++);
					if (cfg.replaceId != 0)
					{
						if (!this.checkItemIDValid(gdCfgs, cfg.replaceId))
							throw new Exception("替换道具id错误，  id = " + cfg.replaceId);
					}
					cfg.quality = excelReadContext.getIntValue(row, col++);

					cfg.attrs = new ArrayList<>(4);
					cfg.skillLvl = new ArrayList<>(4);
					for (int i = 0; i < 4; i++)
					{
						cfg.skillLvl.add(excelReadContext.getIntValue(row, col++));
					}

					cfg.attrs = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.value = excelReadContext.getIntValue(row, col++);
						if (attr.id > 0)
						{
							cfg.attrs.add(attr);
						}
					}
					cfg.proficiencyAdd = excelReadContext.getIntValue(row, col++);

					cfgList.add(cfg);
					row++;
					col = colStart;

					SBean.WeaponCFGS wcfg = cfgs.get(cfg.id);
					if (wcfg == null)
						throw new Exception("神兵 升级表中 id = " + cfg.id + " 的神兵不存在!");

					if (wcfg.weaponStar.size() != cfg.star)
						throw new Exception("神兵 星级表中 id = " + cfg.id + " 的神兵星级数据 在星级 " + cfg.star + "不连续！");
					wcfg.weaponStar.add(cfg);
				}
			}

			excelReadContext.ReadSheet(4);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.WeaponSkillLevelUpCFGS cfg = new SBean.WeaponSkillLevelUpCFGS();
					int weaponId = excelReadContext.getIntValue(row, col++);
					SBean.WeaponCFGS wcfg = cfgs.get(weaponId);
					if (wcfg == null)
						throw new Exception("神兵技能升级表中 id = " + weaponId + " 的神兵不存在!");
					if (wcfg.skillLevelUpCost == null)
						wcfg.skillLevelUpCost = new ArrayList<>();
					int skillIndex = excelReadContext.getIntValue(row, col++);
					if (skillIndex <= 0 || skillIndex > wcfg.skills.size())
						throw new Exception("神兵技能升级表中 row " + row + " skillIndex = " + skillIndex + " 的技能不存在!");
					if (skillIndex > wcfg.skillLevelUpCost.size() + 1)
						throw new Exception("神兵技能升级表中 row " + row + " skillIndex = " + skillIndex + " 的技能序号不连续!");
					if (skillIndex == wcfg.skillLevelUpCost.size() + 1)
						wcfg.skillLevelUpCost.add(new SBean.WeaponSkillLevelUpGroupCFGS(new ArrayList<>()));
					SBean.WeaponSkillLevelUpGroupCFGS costs = wcfg.skillLevelUpCost.get(skillIndex - 1);
					int skillLevel = excelReadContext.getIntValue(row, col++);
					if (skillLevel != costs.costGroup.size() + 1)
						throw new Exception("神兵技能升级表中 row " + row + " skillLevel = " + skillLevel + " 的技能等级不连续!");
					cfg.weaponLevelNeed = excelReadContext.getIntValue(row, col++);
					if (cfg.weaponLevelNeed <= 0 || cfg.weaponLevelNeed > wcfg.weaponLevel.size())
						throw new Exception("神兵技能升级表中 row " + row + " 神兵等级需求 " + cfg.weaponLevelNeed + " 不在神兵等级范围内!");
					cfg.costs = new HashMap<>();
					for (int i = 1; i < skillLevel; i++)
					{
						costs.costGroup.get(i - 1).costs.put(skillLevel, new SBean.MutiDummyGoods(new HashMap(costs.costGroup.get(i - 1).costs.getOrDefault(skillLevel - 1, new SBean.MutiDummyGoods(new HashMap<>())).dummyGoods)));
					}
					for (int i = 0; i < 2; i++)
					{
						int itemId = excelReadContext.getIntValue(row, col++);
						int itemNum = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemId, true);
						if (itemId == 0)
							continue;
						if (itemNum <= 0)
							throw new Exception("神兵技能升级表中 row " + row + " 道具消耗数量 " + itemNum + " 必须大于0!");
						for (int j = 1; j < skillLevel; j++)
						{
							costs.costGroup.get(j - 1).costs.get(skillLevel).dummyGoods.merge(itemId, itemNum, (ov, nv) -> ov + nv);
						}
					}
					row++;
					col = colStart;

					costs.costGroup.add(cfg);
				}
			}

			excelReadContext.ReadSheet(5);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.WeaponTalentCFGS cfg = new SBean.WeaponTalentCFGS();
					cfg.weaponId = excelReadContext.getIntValue(row, col++);
					SBean.WeaponCFGS wcfg = cfgs.get(cfg.weaponId);
					if (wcfg == null)
						throw new Exception("神兵天赋表中 id = " + cfg.weaponId + " 的神兵不存在!");
					if (wcfg.talents == null)
						wcfg.talents = new ArrayList<SBean.WeaponTalentCFGS>();
					cfg.talentIndex = excelReadContext.getIntValue(row, col);
					if (cfg.talentIndex != wcfg.talents.size() + 1)
						throw new Exception("神兵天赋表中 row " + row + " 天赋序号 " + cfg.talentIndex + " 不连续!");
					col += 4;
					cfg.preTalentPoint = excelReadContext.getIntValue(row, col++);
					if (cfg.preTalentPoint < 0)
						throw new Exception("神兵天赋表中 row " + row + " 前置天赋点 " + cfg.preTalentPoint + " 不能小于0!");
					cfg.maxUsePoint = excelReadContext.getIntValue(row, col++);
					cfg.talentEffectType = excelReadContext.getByteValue(row, col++);
					if (cfg.talentEffectType != GameData.WEAPON_TALENT_TYPE_PROP && cfg.talentEffectType != GameData.WEAPON_TALENT_TYPE_SKILL)
						throw new Exception("神兵天赋表中 row " + row + " 天赋效果类型 " + cfg.talentEffectType + " 错误!");
					cfg.talentPropId = excelReadContext.getIntValue(row, col++);
					if (cfg.talentPropId != 0 && !gdCfgs.properties.containsKey(cfg.talentPropId))
						throw new Exception("神兵天赋表中 row " + row + " 提升属性ID " + cfg.talentPropId + " 不存在!");
					cfg.talentPropType = excelReadContext.getByteValue(row, col++);
					if (cfg.talentPropType != 0 && cfg.talentPropType != 1 && cfg.talentPropType != 2)
						throw new Exception("神兵天赋表中 row " + row + " 提升属性类型 " + cfg.talentPropType + " 错误!");
					cfg.talentPropNum = excelReadContext.getIntegerList(row, col++, ";");
					cfg.talentTriggerList = excelReadContext.getIntegerList(row, col++, ";");
					if (cfg.talentEffectType == 1 && cfg.maxUsePoint != cfg.talentPropNum.size())
						throw new Exception("神兵天赋表中 row " + row + " 提升属性数值和最大投入点数不符合!");
					if (cfg.talentEffectType == 2 && cfg.maxUsePoint != cfg.talentTriggerList.size())
						throw new Exception("神兵天赋表中 row " + row + " 触发技能列表和最大投入点数不符合!");
					row++;
					col = colStart;
					wcfg.talents.add(cfg);
				}
			}

			excelReadContext.ReadSheet(6);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					int weaponId = excelReadContext.getIntValue(row, col++);
					SBean.WeaponCFGS wcfg = cfgs.get(weaponId);
					if (wcfg == null)
						throw new Exception("神兵天赋购买表中 id = " + weaponId + " 的神兵不存在!");
					if(wcfg.talentLevelUpCost==null)
						wcfg.talentLevelUpCost=new ArrayList<>();
					int talentIndex = excelReadContext.getIntValue(row, col++);
					if(talentIndex!=wcfg.talentLevelUpCost.size()+1)
						throw new Exception("神兵天赋购买表中 row " + row + " 天赋序号 " + talentIndex + " 不连续!");
					SBean.MutiDummyGoods curDummyGoods = new SBean.MutiDummyGoods(new HashMap<>());
					for (int i = 0; i < 2; i++)
					{
						int itemId = excelReadContext.getIntValue(row, col++);
						int itemNum = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemId, true);
						if (itemId == 0)
							continue;
						if (itemNum <= 0)
							throw new Exception("神兵天赋购买表中 row " + row + " 道具消耗数量 " + itemNum + " 必须大于0!");
						curDummyGoods.dummyGoods.put(itemId, itemNum);
					}
					row++;
					col = colStart;

					wcfg.talentLevelUpCost.add(curDummyGoods);
				}
			}

			excelReadContext.ReadSheet(7);
			{
				final int colStart = 1;
				final int rowStart = 0;
				int row = rowStart;
				int col = colStart;
				SBean.WeaponTalentCommonCFGS cfg = new SBean.WeaponTalentCommonCFGS();
				cfg.resetPointItemId = excelReadContext.getIntValue(row++, col);
				checkEntityIDValid(gdCfgs, row, cfg.resetPointItemId, false);
				cfg.resetPointItemNum = excelReadContext.getIntValue(row++, col);
				if (cfg.resetPointItemNum <= 0)
					throw new Exception("神兵其他表中 row " + row + " 道具消耗数量 " + cfg.resetPointItemNum + " 必须大于0!");
				cfg.defaultTalentPoint = excelReadContext.getIntValue(row++, col);
				if (cfg.defaultTalentPoint <= 0)
					throw new Exception("神兵其他表中 row " + row + " 初始赠送天赋点数 " + cfg.defaultTalentPoint + " 必须大于0!");

				gdCfgs.weaponTalent = cfg;
			}
			
			excelReadContext.ReadSheet(8);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.WeaponUniqueSkillCFGS> weaponUSkills = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int weaponID = excelReadContext.getIntValue(row, col++);
					SBean.WeaponCFGS wCfg = cfgs.get(weaponID);
					if(wCfg == null)
						throw new Exception("row " + row + " 神兵绝技逻辑表 所属神兵ID " + weaponID + " 非法！");
					
					SBean.WeaponUniqueSkillCFGS usCfg = new SBean.WeaponUniqueSkillCFGS();
					usCfg.id = excelReadContext.getIntValue(row, col++);
					if(!wCfg.uniqueSkills.add(usCfg.id))
						throw new Exception("row " + row + " 神兵绝技逻辑表 神兵 " + weaponID + " 特技ID " + usCfg.id + " 重复!");
					
					usCfg.type = excelReadContext.getIntValue(row, col++);
					usCfg.param1 = excelReadContext.getIntValue(row, col++);
					usCfg.param2 = excelReadContext.getIntValue(row, col++);
					usCfg.param3 = excelReadContext.getIntValue(row, col++);
					usCfg.param4 = excelReadContext.getIntValue(row, col++);
					usCfg.param5 = excelReadContext.getIntValue(row, col++);
					usCfg.param6 = excelReadContext.getIntValue(row, col++);
					usCfg.effectType = excelReadContext.getIntValue(row, col++);
					usCfg.fullStarParam1 = excelReadContext.getIntValue(row, col++);
					usCfg.fullStarParam2 = excelReadContext.getIntValue(row, col++);
					usCfg.fullStarParam3 = excelReadContext.getIntValue(row, col++);
					usCfg.fullStarParam4 = excelReadContext.getIntValue(row, col++);
					usCfg.fullStarParam5 = excelReadContext.getIntValue(row, col++);
					usCfg.fullStarParam6 = excelReadContext.getIntValue(row, col++);
					
					switch (usCfg.type)
					{
						case GameData.WEAPON_USKILL_TYPE_ITEM:
							if(usCfg.param1 != 0)
								checkEntityIDValid(gdCfgs, row, usCfg.param1, false);
							break;
						case GameData.WEAPON_USKILL_TYPE_WITHPET_PROP:
							if(usCfg.param1 != -1)
								checkPetValid(gdCfgs, row, usCfg.param1);
							if(!cfgs.containsKey(usCfg.param2))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数2 " + usCfg.param2 + " inValid!");
							
							checkPropertyIDValid(gdCfgs, row, usCfg.param3);
							checkValueType(row, usCfg.param4);
							checkPetValid(gdCfgs, row, usCfg.fullStarParam1);
							if(!cfgs.containsKey(usCfg.fullStarParam2))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数2 " + usCfg.fullStarParam2 + " inValid!");
							
							checkPropertyIDValid(gdCfgs, row, usCfg.fullStarParam3);
							checkValueType(row, usCfg.fullStarParam4);
							break;
						case GameData.WEAPON_USKILL_TYPE_KILL_EXP:	
							if(!cfgs.containsKey(usCfg.param1))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数1 " + usCfg.param1 + " inValid!");
							if(!cfgs.containsKey(usCfg.fullStarParam1))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数1 " + usCfg.fullStarParam1 + " inValid!");
							
							if(usCfg.effectType != GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							break;
						case GameData.WEAPON_USKILL_TYPE_KILL_DROP:
							if(!cfgs.containsKey(usCfg.param1))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数1 " + usCfg.param1 + " inValid!");
							if(!cfgs.containsKey(usCfg.fullStarParam1))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数1 " + usCfg.fullStarParam1 + " inValid!");
							
							if(usCfg.effectType != GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							break;
						case GameData.WEAPON_USKILL_TYPE_USKILL:
							SBean.UniqueSkillCFG uniqueCfg = gdCfgs.uniqueSkills.get(usCfg.param1);
							if(uniqueCfg == null)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数1 " + usCfg.param1 + " inValid!");
							SBean.UniqueSkillCFG uniqueStarCfg = gdCfgs.uniqueSkills.get(usCfg.fullStarParam1);
							if(uniqueStarCfg == null)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数1 " + usCfg.fullStarParam1 + " inValid!");
							
							for(int skillID: uniqueCfg.skills)
								checkSkillIDAndLevelValid(gdCfgs, row, skillID, usCfg.param2);
							for(int skillID: uniqueStarCfg.skills)
								checkSkillIDAndLevelValid(gdCfgs, row, skillID, usCfg.fullStarParam2);
							break;
						case GameData.WEAPON_USKILL_TYPE_INSIGHT:
							if(usCfg.param1 <= 0)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数1 " + usCfg.param1 + " inValid!");
							if(usCfg.fullStarParam1 <= 0)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数1 " + usCfg.fullStarParam1 + " inValid!");
							
							if(usCfg.effectType == GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							break;
						case GameData.WEAPON_USKILL_TYPE_REVENGE:
							if(usCfg.param1 <= 0)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数1 " + usCfg.param1 + " inValid!");
							if(usCfg.param2 <= 0)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数2 " + usCfg.param2 + " inValid!");
							if(usCfg.fullStarParam1 <= 0)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数1 " + usCfg.fullStarParam1 + " inValid!");
							if(usCfg.fullStarParam2 <= 0)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数2 " + usCfg.fullStarParam2 + " inValid!");
							
							if(usCfg.effectType == GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							
							break;
						case GameData.WEAPON_USKILL_TYPE_WITHHOURSE_PROP:
							if(usCfg.param1 != -1)
								checkHorseIDValid(gdCfgs, row, usCfg.param1);
							if(!cfgs.containsKey(usCfg.param2))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数2 " + usCfg.param2 + " inValid!");
							checkHorseIDValid(gdCfgs, row, usCfg.fullStarParam1);
							if(!cfgs.containsKey(usCfg.fullStarParam2))
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 满星参数2 " + usCfg.fullStarParam2 + " inValid!");
							
							checkPropertyIDValid(gdCfgs, row, usCfg.param3);
							checkValueType(row, usCfg.param4);
							checkPropertyIDValid(gdCfgs, row, usCfg.fullStarParam3);
							checkValueType(row, usCfg.fullStarParam4);
							break;
						case GameData.WEAPON_USKILL_TYPE_ADD_AI:
							checkAiTrigerIDValid(gdCfgs, row, usCfg.param1);
							checkAiTrigerIDValid(gdCfgs, row, usCfg.fullStarParam1);
							break;
						case GameData.WEAPON_USKILL_TYPE_SET_FORM:
							if(usCfg.effectType == GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							break;
						case GameData.WEAPON_USKILL_TYPE_UP_CRYSTAL_EQUIP:
							if(usCfg.effectType == GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							break;
						case GameData.WEAPON_USKILL_TYPE_REDUCE_FIX_EQUIP_PRICE:
							if(usCfg.param1 > 10000)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数1 " + usCfg.param1 + " inValid!");
							if(usCfg.fullStarParam1 > 10000)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 参数1 " + usCfg.fullStarParam1 + " inValid!");
							
							if(usCfg.effectType == GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							break;
						case GameData.WEAPON_USKILL_TYPE_ENTER_MAPCOPY:
							if(usCfg.effectType == GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE)
								throw new Exception("row " + row + " 神兵 " + weaponID + " 特技 " + usCfg.id + " 生效类型 " + usCfg.effectType + " inValid!");
							break;
						default:
							throw new Exception("row " + row + " 神兵绝技逻辑表 特技类型 " + usCfg.type + " 非法！");
					}
					
					switch (usCfg.effectType)
					{
					case GameData.WEAPON_USKILL_EFFECT_TYPE_OPEN:		
					case GameData.WEAPON_USKILL_EFFECT_TYPE_ALL_TIME:
					case GameData.WEAPON_USKILL_EFFECT_TYPE_EQUIP:
					case GameData.WEAPON_USKILL_EFFECT_TYPE_MOTIVATE:
						break;
					default:
						throw new Exception("row " + row + " 神兵绝技逻辑表 生效类型 " + usCfg.effectType + " 非法！");
					}
					
					if(weaponUSkills.put(usCfg.id, usCfg) != null)
						throw new Exception("row " + row + " 神兵绝技逻辑表 特技ID " + usCfg.id + " 重复！");
					
					row++;
				}
				gdCfgs.weaponUSkills = weaponUSkills;
			}
		}

		System.out.println("load table " + fileName + " success.");
	}

	public void loadPetTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				List<SBean.PetCFGS> cfgs = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PetCFGS cfg = new SBean.PetCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col += 4;

					List<SBean.ClassRolePropCFGS> props = new ArrayList<>();
					for (int i = 0; i < 8; i++)
					{
						SBean.ClassRolePropCFGS p = new SBean.ClassRolePropCFGS();
						p.org = excelReadContext.getDoubleValue(row, col++);
						p.incs1 = excelReadContext.getDoubleValue(row, col++);
						p.incs2 = excelReadContext.getDoubleValue(row, col++);
						props.add(p);
					}
					Iterator<SBean.ClassRolePropCFGS> arrProps = props.iterator();
					cfg.hp = arrProps.next();
					cfg.atkN = arrProps.next();
					cfg.defN = arrProps.next();
					cfg.atr = arrProps.next();
					cfg.ctr = arrProps.next();
					cfg.acrN = arrProps.next();
					cfg.tou = arrProps.next();
					cfg.atkA = arrProps.next();
					
					cfg.spiritInherit = excelReadContext.getDoubleValue(row, col++);
					col += 5;
					cfg.weaponInherit = excelReadContext.getDoubleValue(row, col++);
					col += 5;

					List<Integer> attacks = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						int aid = excelReadContext.getIntValue(row, col++);
						this.checkSkillIDValid(gdCfgs, row, aid);
						attacks.add(aid);
					}
					cfg.attacks = attacks;

					List<Integer> skills = new ArrayList<>();
					for (int i = 0; i < 5; i++)
					{
						if (i == 3)
						{
							col++;
							continue;
						}
						int sid = excelReadContext.getIntValue(row, col++);
						this.checkSkillIDValid(gdCfgs, row, sid);
						if (skills.contains(sid))
							throw new Exception("佣兵技能ID重复， id = " + sid);

						skills.add(sid);
					}
					cfg.skills = skills;
					
					cfg.attackList = excelReadContext.getIntegerList(row, col++, ";");
					cfg.speed = excelReadContext.getIntValue(row, col++);
					cfg.radius = excelReadContext.getIntValue(row, col++);
					cfg.checkRange = excelReadContext.getIntValue(row, col++);
					
					col += 1;

					cfg.breakSkills = new ArrayList<>();
					for (int index = 0; index < 4; index++)
					{
						SBean.PetBreakSkillCondCFGS bscfg = new SBean.PetBreakSkillCondCFGS();
						bscfg.id = excelReadContext.getIntValue(row, col++);
						bscfg.value = excelReadContext.getIntValue(row, col++);
						cfg.breakSkills.add(bscfg);
					}

					cfg.makeConds = new ArrayList<>();
					for (int index = 0; index < 3; index++)
					{
						int type = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						if (type != 0)
						{
							switch (type)
							{
							case GameData.PET_MAKE_CONDITION_MAIN_TASK:
								break;
							case GameData.PET_MAKE_CONDITION_FIGHT_POWER:
								break;
							case GameData.PET_MAKE_CONDITION_FINISH_MAPCOPY:
								break;
							case GameData.PET_MAKE_CONDITION_ROLE_LEVEL:
								break;
							default:
								throw new Exception("row " + row + "不支持的佣兵召唤条件类型 " + type + " !");
							}
							cfg.makeConds.add(new SBean.PetMakeCondCFGS(type, value));
						}
					}

					cfg.makeConsumeItem = excelReadContext.getIntValue(row, col++);
					this.checkConsumeIDValid(gdCfgs, row, cfg.makeConsumeItem, true);
					cfg.makeConsumeItemCount = excelReadContext.getIntValue(row, col++);

					col += 1;
					cfg.hurtAddSP = excelReadContext.getIntValue(row, col++);
					
					cfg.stars = new ArrayList<>();
					
					col = colStart + 74;
					cfg.minStupidTime = excelReadContext.getIntValue(row, col++);
					cfg.maxStupidTime = excelReadContext.getIntValue(row, col++);
					cfg.maxFollowDistance = excelReadContext.getIntValue(row, col++);
					cfg.maxBehaviorRadius = excelReadContext.getIntValue(row, col++);
					cfg.minBehaviorRadius = excelReadContext.getIntValue(row, col++);
					cfg.lifeMapCopyId = excelReadContext.getIntValue(row, col++);
					cfg.startLevel = excelReadContext.getIntValue(row, col++);
					if (cfg.startLevel <= 0)
						throw new Exception("row " + row + " 佣兵起始等级必须大于0！");
					cfg.startStar = excelReadContext.getIntValue(row, col++);
					if (cfg.startStar < 0 || cfg.startStar > 4)
						throw new Exception("row " + row + " 佣兵起始星级只能为0-4！");
					
					cfg.spiritLearnCost = new ArrayList<>();
					for(int i = 0; i < 2; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if(itemID != 0)
							checkEntityIDValid(gdCfgs, row, itemID, false);
						if(count > 0)
							cfg.spiritLearnCost.add(new SBean.DummyGoods(itemID, count));
					}
					
					if(cfg.spiritLearnCost.isEmpty())
						throw new Exception("row " + row + " 学习心法消耗数量为空");
					
					if (cfgs.size() + 1 != cfg.id)
						throw new Exception("row " + row + " 佣兵id 不连续！");
					cfgs.add(cfg);
					row++;
					col = colStart;
				}
				gdCfgs.pets = cfgs;
			}

			excelReadContext.ReadSheet(1);
			{
				List<SBean.PetLvlCFGS> petLvlCFGSs = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PetLvlCFGS petLvlCFGS = new SBean.PetLvlCFGS();
					petLvlCFGS.lvl = excelReadContext.getIntValue(row, col++);
					petLvlCFGS.exp = excelReadContext.getIntValue(row, col++);
					petLvlCFGS.buyDiamond = excelReadContext.getIntValue(row, col++);

					petLvlCFGS.costItems = excelReadContext.getIntegerList(row, col++, ";");
					for (int item : petLvlCFGS.costItems)
					{
						this.checkConsumeIDValid(gdCfgs, row, item, false);
					}

					petLvlCFGSs.add(petLvlCFGS);
					row++;
					col = colStart;
				}
				
				int length = petLvlCFGSs.size();
				for(int index = 0; index < length; index ++)
				{
					SBean.PetLvlCFGS firstCFGS = petLvlCFGSs.get(index);
					firstCFGS.buyDiamonds = new TreeMap<Integer, Integer>();
					for(int pos = index + 1; pos < length; pos ++)
					{
						SBean.PetLvlCFGS curCFGS = petLvlCFGSs.get(pos);
						Integer upCFGS = firstCFGS.buyDiamonds.get(curCFGS.lvl - 1);
						if(upCFGS != null)
						{
							firstCFGS.buyDiamonds.put(curCFGS.lvl, curCFGS.buyDiamond + upCFGS);
						}else{
							firstCFGS.buyDiamonds.put(curCFGS.lvl, curCFGS.buyDiamond);
						}
					}
				}
				
				
				gdCfgs.petLvls = petLvlCFGSs;
			}

			excelReadContext.ReadSheet(2);
			{
				Map<Integer, SBean.PetCoPracticeGroupCFGS> petCoPracticeCFGSs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PetCoPracticeCFGS petCoPraticeCFGS = new SBean.PetCoPracticeCFGS();
					petCoPraticeCFGS.petid = excelReadContext.getIntValue(row, col++);
					petCoPraticeCFGS.level = excelReadContext.getIntValue(row, col++);
					petCoPraticeCFGS.needExp = excelReadContext.getIntValue(row, col++);

					petCoPraticeCFGS.skillLevels = new ArrayList<>();
					for (int index = 0; index < 4; index++)
					{
						int skillId = excelReadContext.getIntValue(row, col++);
						petCoPraticeCFGS.skillLevels.add(skillId);
					}

					petCoPraticeCFGS.tasklist = new ArrayList<>();
					List<Integer> tasks = excelReadContext.getIntegerList(row, col++, ";");
					for (int tid : tasks)
					{
						this.checkPetTaskID(gdCfgs, row, tid);
						petCoPraticeCFGS.tasklist.add(tid);
					}

					petCoPraticeCFGS.attris = new ArrayList<SBean.AttrCFGS>();
					for (int i = 0; i < 7; i++)
					{
						int attrID = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						if(attrID > 0)
						{
							checkPropertyIDValid(gdCfgs, row, attrID);
							petCoPraticeCFGS.attris.add(new SBean.AttrCFGS(attrID, value));
						}
					}

					if (!petCoPracticeCFGSs.containsKey(petCoPraticeCFGS.petid))
					{
						petCoPracticeCFGSs.put(petCoPraticeCFGS.petid, new SBean.PetCoPracticeGroupCFGS(petCoPraticeCFGS.petid, new ArrayList<SBean.PetCoPracticeCFGS>()));
					}
					int cursize = petCoPracticeCFGSs.get(petCoPraticeCFGS.petid).petCoPracticeCFGSs.size();
					if (cursize == 0 && petCoPraticeCFGS.level != 1)
					{
						throw new Exception("row " + row + " 该随从合修起始等级 " + petCoPraticeCFGS.level + " 不为1!");
					}
					if (cursize > 0 && petCoPraticeCFGS.level != petCoPracticeCFGSs.get(petCoPraticeCFGS.petid).petCoPracticeCFGSs.get(cursize - 1).level + 1)
					{
						throw new Exception("row " + row + " 该随从合修等级 " + petCoPraticeCFGS.level + " 不连续!");
					}
					petCoPracticeCFGSs.get(petCoPraticeCFGS.petid).petCoPracticeCFGSs.add(petCoPraticeCFGS);
					row++;
					col = colStart;
				}
				gdCfgs.petCoPractices = petCoPracticeCFGSs;
			}

			excelReadContext.ReadSheet(3);
			{
				List<SBean.PetTransformCFGS> cfgs = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PetTransformCFGS cfg = new SBean.PetTransformCFGS();
					cfg.tlevel = excelReadContext.getIntValue(row, col++);
					cfg.mustTransfromLvl = excelReadContext.getIntValue(row, col++);

					cfg.costItems = new ArrayList<>();
					for (int index = 0; index < 3; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if (id != 0)
						{
							this.checkConsumeIDValid(gdCfgs, row, id, false);
							cfg.costItems.add(new SBean.DummyGoods(id, count));	
						}
					}

					cfg.cond = excelReadContext.getIntValue(row, col++);
					cfg.value = excelReadContext.getIntValue(row, col++);
					switch (cfg.cond)
					{
					case GameData.PET_TRANSFORM_CONDITION_NONE:
						break;
					case GameData.PET_TRANSFORM_CONDITION_PET_FINISH_MAPCOPY:
						SBean.MapCopyCFGS mccfg = gdCfgs.mapcopys.get(cfg.value);
						if (mccfg == null || mccfg.openType != GameData.MAPCOPY_OPEN_TYPE_PRIVATE)
							throw new Exception("row " + row + " 转职条件：通关副本任务类型中 副本Id " + cfg.value + " 不是单人本!");
						break;
					default:
						throw new Exception("row " + row + " 不支持的转职条件类型 " + cfg.cond + " !");
					}

					cfgs.add(cfg);
					row++;
					col = colStart;
				}
				gdCfgs.petTransforms = cfgs;
			}

			excelReadContext.ReadSheet(4);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PetStarCFGS cfg = new SBean.PetStarCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.star = excelReadContext.getIntValue(row, col++);
					cfg.itemId = excelReadContext.getIntValue(row, col++);
					this.checkConsumeIDValid(gdCfgs, row, cfg.itemId, true);
					cfg.itemCount = excelReadContext.getIntValue(row, col++);
					cfg.replaceId = excelReadContext.getIntValue(row, col++);
					this.checkConsumeIDValid(gdCfgs, row, cfg.replaceId, true);

					cfg.harmUpRate = excelReadContext.getIntValue(row, col++);
					cfg.harmDownRate = excelReadContext.getIntValue(row, col++);
					cfg.spiritRate = excelReadContext.getDoubleValue(row, col++);
					cfg.weaponRate = excelReadContext.getDoubleValue(row, col++);

					
					if (cfg.id <= 0 || cfg.id > gdCfgs.pets.size())
						throw new Exception("row " + row + " 佣兵星级表 id=" + cfg.id + " 的佣兵不存在！");
					SBean.PetCFGS petcfg = gdCfgs.pets.get(cfg.id-1);
					
					if (petcfg.stars.size() != cfg.star)
						throw new Exception("佣兵 星级表中 id = " + cfg.id + " 的佣兵星级数据 在星级 " + cfg.star + "不连续！");
					petcfg.stars.add(cfg);
					row++;
					col = colStart;
				}
			}

			excelReadContext.ReadSheet(5);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.PetBreakSkillCFGS> cfgs = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PetBreakSkillLevelCFGS cfg = new SBean.PetBreakSkillLevelCFGS();
					cfg.skillId = excelReadContext.getIntValue(row, col++);
					cfg.level = excelReadContext.getIntValue(row, col++);
					cfg.type = excelReadContext.getIntValue(row, col++);
					cfg.harmUpRate = excelReadContext.getIntValue(row, col++);
					cfg.harmDownRate = excelReadContext.getIntValue(row, col++);
					cfg.spiritRate = excelReadContext.getDoubleValue(row, col++);
					cfg.weaponRate = excelReadContext.getDoubleValue(row, col++);
					cfg.itemId = excelReadContext.getIntValue(row, col++);
					this.checkConsumeIDValid(gdCfgs, row, cfg.itemId, true);
					cfg.itemCount = excelReadContext.getIntValue(row, col++);
					cfg.replaceId = excelReadContext.getIntValue(row, col++);
					this.checkConsumeIDValid(gdCfgs, row, cfg.replaceId, true);
					
					SBean.PetBreakSkillCFGS petbreakskillcfg = cfgs.get(cfg.skillId);
					if (petbreakskillcfg == null)
					{
						petbreakskillcfg = new SBean.PetBreakSkillCFGS(cfg.skillId, new ArrayList<>());
						cfgs.put(cfg.skillId, petbreakskillcfg);
					}
					if (petbreakskillcfg.levels.size() != cfg.level)
						throw new Exception("row " + row + " 佣兵突破表 突破技能id=" + cfg.skillId + " 的等级数据在等级 " + cfg.level + " 不连续！");
					petbreakskillcfg.levels.add(cfg);

					row++;
					col = colStart;
				}
				gdCfgs.petBreakSkills = cfgs;
			}
			
			excelReadContext.ReadSheet(7);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PetSkillLevelUpCFGS cfg = new SBean.PetSkillLevelUpCFGS();
					int petId = excelReadContext.getIntValue(row, col++);
					SBean.PetCFGS pcfg = gdCfgs.pets.get(petId - 1);
					if (pcfg == null)
						throw new Exception("随从技能升级表中 id = " + petId + " 的随从不存在!");
					if (pcfg.petSkill == null)
						pcfg.petSkill = new ArrayList<>();
					int skillIndex = excelReadContext.getIntValue(row, col++);
					if (skillIndex <= 0 || skillIndex > pcfg.skills.size())
						throw new Exception("随从技能升级表中 row " + row + " skillIndex = " + skillIndex + " 的技能不存在!");
					if (skillIndex > pcfg.petSkill.size() + 1)
						throw new Exception("随从技能升级表中 row " + row + " skillIndex = " + skillIndex + " 的技能序号不连续!");
					if (skillIndex == pcfg.petSkill.size() + 1)
						pcfg.petSkill.add(new SBean.PetSkillLevelUpGroupCFGS(new ArrayList<>()));
					SBean.PetSkillLevelUpGroupCFGS costs = pcfg.petSkill.get(skillIndex - 1);
					int skillLevel = excelReadContext.getIntValue(row, col++);
					if (skillLevel != costs.costGroup.size() + 1)
						throw new Exception("随从技能升级表中 row " + row + " skillLevel = " + skillLevel + " 的技能等级不连续!");
					cfg.petLevelNeed = excelReadContext.getIntValue(row, col++);
					if (cfg.petLevelNeed <= 0 || cfg.petLevelNeed > gdCfgs.petLvls.size())
						throw new Exception("随从技能升级表中 row " + row + " 随从等级需求 " + cfg.petLevelNeed + " 不在随从等级范围内!");
					cfg.costs = new HashMap<>();
					for (int i = 1; i < skillLevel; i++)
					{
						costs.costGroup.get(i - 1).costs.put(skillLevel, new SBean.MutiDummyGoods(new HashMap(costs.costGroup.get(i - 1).costs.getOrDefault(skillLevel - 1, new SBean.MutiDummyGoods(new HashMap<>())).dummyGoods)));
					}
					for (int i = 0; i < 2; i++)
					{
						int itemId = excelReadContext.getIntValue(row, col++);
						int itemNum = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemId, true);
						if (itemId == 0)
							continue;
						if (itemNum <= 0)
							throw new Exception("随从技能升级表中 row " + row + " 道具消耗数量 " + itemNum + " 必须大于0!");
						for (int j = 1; j < skillLevel; j++)
						{
							costs.costGroup.get(j - 1).costs.get(skillLevel).dummyGoods.merge(itemId, itemNum, (ov, nv) -> ov + nv);
						}
					}
					row++;
					col = colStart;

					costs.costGroup.add(cfg);
				}
			}
			
			excelReadContext.ReadSheet(8);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.PetSpiritGroupCFGS> groups = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int spiritID = excelReadContext.getIntValue(row, col++);
					col++;
					int lvl = excelReadContext.getIntValue(row, col++);
					SBean.PetSpiritGroupCFGS group = groups.get(spiritID);
					if(group == null)
					{
						group = new SBean.PetSpiritGroupCFGS(new ArrayList<>());
						groups.put(spiritID, group);
					}
					
					SBean.PetSpiritCFGS cfg = new SBean.PetSpiritCFGS(0.f, new ArrayList<>(), 0, new ArrayList<>());
					group.spirits.add(cfg);
					if(group.spirits.size() != lvl)
						throw new Exception("row " + row + " 武库心法 " + spiritID + " 等级 " + lvl + " 不连续!");
					
					cfg.weight = excelReadContext.getFloatValue(row, col++);
					if(cfg.weight == 0)
						throw new Exception("row " + row + " 武库心法 " + spiritID + " 等级 " + lvl + " 抽取权重 " + cfg.weight + " 非法!");
					
					for(int i = 0; i < 2; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if(itemID != 0)
						{
							checkEntityIDValid(gdCfgs, row, itemID, false);
							cfg.cost.add(new SBean.DummyGoods(itemID, count));
						}
					}
					
					col = 9;
					cfg.effectType = excelReadContext.getIntValue(row, col++);
					for(int i = 0; i < 3; i++)
						cfg.params.add(excelReadContext.getIntValue(row, col++));
					
					switch (cfg.effectType)
					{
						case GameData.PET_SPIRIT_EFFECT_TYPE_PET_PROP:
							checkPropertyIDValid(gdCfgs, row, cfg.params.get(0));
							checkValueType(row, cfg.params.get(1));
							break;
						case GameData.PET_SPIRIT_EFFECT_TYPE_REDUCE_STUPID:
							break;
						case GameData.PET_SPIRIT_EFFECT_TYPE_ROLE_PROP:
							checkPropertyIDValid(gdCfgs, row, cfg.params.get(0));
							checkValueType(row, cfg.params.get(1));
							break;
						case GameData.PET_SPIRIT_EFFECT_TYPE_PET_AI:
							checkAiTrigerIDValid(gdCfgs, row, cfg.params.get(0));
							break;
						default:
							throw new Exception("row " + row + " 武库心法 " + spiritID + " 效果类型 " + cfg.effectType + " 非法!");
					}
					
					row++;
				}
				
//				for(Map.Entry<Integer, SBean.PetSpiritGroupCFGS> e: groups.entrySet())
//				{
//					float sum = sums.get(e.getKey());
//					for(SBean.PetSpiritCFGS p: e.getValue().spirits)
//						p.weight = p.weight / sum;
//				}
				
				gdCfgs.petSpiritGroups = groups;
			}
			
			excelReadContext.ReadSheet(9);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.PetExploitCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int petID = excelReadContext.getIntValue(row, col++);
					SBean.PetExploitCFGS cfg = cfgs.get(petID);
					if(cfg == null)
					{
						cfg = new SBean.PetExploitCFGS(new ArrayList<>());
						cfgs.put(petID, cfg);
					}
					int index = excelReadContext.getIntValue(row, col++);
					int exploit = excelReadContext.getIntValue(row, col++);
					cfg.exploits.add(exploit);
					if(cfg.exploits.size() != index)
						throw new Exception("row " + row + " 佣兵 " + petID + " 心法槽位 " + index + " 不连续!");
					
					row++;
				}
				
				gdCfgs.petExploit = cfgs;
			}
			
			{
				for (SBean.PetCFGS pet : gdCfgs.pets)
				{
					for (SBean.PetBreakSkillCondCFGS bs : pet.breakSkills)
					{
						if (!gdCfgs.petBreakSkills.containsKey(bs.id))
							throw new Exception("佣兵 id=" + pet.id + " 的突破技能 skillId="+bs.id+ " 不存在！");
					}
				}
			}
			System.out.println("load table " + fileName + " success.");
		}

	}

	public void loadTransformTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart + 0;

				Set<Integer> allSkills = new HashSet<>();
				List<SBean.CLassTransformCFGS> cfgs = new ArrayList<>();
				for (SBean.ClassRoleCFGS crCfg : gdCfgs.classRoles)
				{
					cfgs.add(new SBean.CLassTransformCFGS(crCfg.id, new ArrayList<>(), new HashMap<>()));
				}
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.TransformCFGS tcfg = new SBean.TransformCFGS();

					tcfg.id = excelReadContext.getIntValue(row, col++);
					
					if (tcfg.id <= 0 || tcfg.id > cfgs.size())
						throw new Exception("转职表 row " + row + " id 非法不在武将表中！");
					
					col += 2;
					tcfg.transformLevel = excelReadContext.getIntValue(row, col++);
					SBean.CLassTransformCFGS ccfg = cfgs.get(tcfg.id - 1);
					if (tcfg.transformLevel != ccfg.transforms.size() + 1 && tcfg.transformLevel != ccfg.transforms.size())
						throw new Exception("转职表 row " + row + " 同一职业ID转职等级不连续！");
					
					tcfg.type = excelReadContext.getIntValue(row, col++);
					if (tcfg.transformLevel == ccfg.transforms.size() + 1)
						ccfg.transforms.add(new SBean.BWTransformCFGS(tcfg.id, tcfg.transformLevel, new TreeMap<>()));
					SBean.BWTransformCFGS bwcfgs = ccfg.transforms.get(tcfg.transformLevel - 1);
					bwcfgs.transforms.put(tcfg.type, tcfg);
						
					tcfg.lvlReq = excelReadContext.getIntValue(row, col++);
					tcfg.skills = new HashSet<>();
					for (int i = 0; i < 2; ++i)
					{
						int skill = excelReadContext.getIntValue(row, col++);
						checkSkillIDValid(gdCfgs, row, skill);
						tcfg.skills.add(skill);
						
						ccfg.skills.put(skill, tcfg.transformLevel);
						if(!allSkills.add(skill))
							throw new Exception("转职表 row " + row + " 技能ID" + skill + " 重复！");
							
					}
					tcfg.cost = new ArrayList<>();
					for (int i = 0; i < 2; ++i)
					{
						int iid = excelReadContext.getIntValue(row, col++);
						this.checkEntityIDValid(gdCfgs, row, iid, false);
						int icount = excelReadContext.getIntValue(row, col++);
						if (icount <= 0)
							throw new Exception("转职表  row " + row + " 消耗物品数量非法  " + icount);
						tcfg.cost.add(new SBean.DummyGoods(iid, icount));
					}
					tcfg.attrs = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int atype = excelReadContext.getIntValue(row, col++);
						checkPropertyIDValid(gdCfgs, row, atype);
						int avalue = excelReadContext.getIntValue(row, col++);
						tcfg.attrs.add(new SBean.AttrCFGS(atype, avalue));
					}
					tcfg.spirits = new ArrayList<>();
					List<Integer> spritsZY = excelReadContext.getIntegerList(row, col++, ";");
					List<Integer> spiritsJH = excelReadContext.getIntegerList(row, col++, ";");
					List<Integer> spiritsPB = excelReadContext.getIntegerList(row, col++, ";");
					tcfg.spirits.addAll(spritsZY);
					tcfg.spirits.addAll(spiritsJH);
					tcfg.spirits.addAll(spiritsPB);

					row++;
					col = colStart;
				}

				gdCfgs.transforms = cfgs;
			}

		}

		System.out.println("load table " + fileName + " success.");
	}

	public void loadSuiteTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.SuiteCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Map<Integer, Integer> equipToSuites = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SuiteCFGS cfg = new SBean.SuiteCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col += 1;
					cfg.classType = excelReadContext.getIntValue(row, col++);
					cfg.reduce = excelReadContext.getIntValue(row, col++);
					cfg.buyCost = excelReadContext.getIntValue(row, col++);
					int count = excelReadContext.getIntValue(row, col++);

					cfg.parts = new ArrayList<>();
					for (int index = 0; index < 6; index++)
					{
						int equipId = excelReadContext.getIntValue(row, col++);
						if (equipId != 0)
						{
							if (equipId < 0)
								throw new Exception("套装 " + cfg.id + " 中装备id必须是正数，绑定装备！");
							SBean.EquipCFGS equipCfg = gdCfgs.equips.get(equipId);
							if (equipCfg == null)
								throw new Exception("套装里的装备 " + equipId + " id 不存在！");
							if (equipCfg.type != index+1)
								throw new Exception("套装里的装备 " + equipId + " id 所在部位与装备表不符！");
							cfg.parts.add(equipId);
							if (equipToSuites.put(equipId, cfg.id) != null)
								throw new Exception("套装表里的同一装备id出现在两个套装中， 装备id = " + equipId);
						}
						
					}
					if (cfg.parts.size() != count)
						throw new Exception("套装 id " + cfg.id + " 中的实际装备数目和表格中填入的部件数目不相同!");

					cfg.attris = new ArrayList<>();
					for (int index = 0; index < 5; index++)
					{
						int attrId = excelReadContext.getIntValue(row, col++);
						if (attrId != 0)
						{
							int attrVal = excelReadContext.getIntValue(row, col++);
							cfg.attris.add(new SBean.AttrCFGS(attrId, attrVal));
						}
						else
						{
							break;
						}
					}

					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("套装 id " + cfg.id + " 重复!");
					row++;
					col = colStart;
				}
				gdCfgs.suites = cfgs;
			}
			System.out.println("load table " + fileName + " success.");
		}

	}

	public void loadSectTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			// 帮派升级表
			excelReadContext.ReadSheet(0);
			{
				List<SBean.SectUpLevelCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SectUpLevelCFGS cfg = new SBean.SectUpLevelCFGS();
					cfg.lvl = excelReadContext.getIntValue(row, col++);
					cfg.roleCount = excelReadContext.getIntValue(row, col++);
					cfg.upTimes = excelReadContext.getIntValue(row, col++);
					cfg.diamond = excelReadContext.getIntValue(row, col++);
					col += 1;
					cfg.deputyCount = excelReadContext.getIntValue(row, col++);
					col += 1;
					cfg.elderCount = excelReadContext.getIntValue(row, col++);
					cfg.active = excelReadContext.getIntValue(row, col++);

					String skillOpenLevel = excelReadContext.getStringValue(row, col++);
					cfg.dayKickTimes = excelReadContext.getIntValue(row, col++);
					String[] strVals = skillOpenLevel.split(";");
					cfg.skillOpenLevel = new ArrayList<>();
					for (String s : strVals)
					{
						int level = (int) Float.parseFloat(s);
						if (level > 0)
							cfg.skillOpenLevel.add(level);
					}

					cfgList.add(cfg);
					row++;
					col = colStart;
				}
				gdCfgs.sectUpLevel = cfgList;
			}

			// 帮派权限表
			excelReadContext.ReadSheet(1);
			{
				List<SBean.SectAuthorityCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SectAuthorityCFGS cfg = new SBean.SectAuthorityCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col += 1;
					String authority = excelReadContext.getStringValue(row, col++);
					String[] strVals = authority.split(";");
					cfg.authority = new ArrayList<>();
					for (String s : strVals)
					{
						int iauth = (int) Float.parseFloat(s);
						if (iauth > 0)
							cfg.authority.add(iauth);
					}
					// cfg.skillUpLvl = excelReadContext.getIntValue(row,
					// col++);
					cfg.accept = excelReadContext.getIntValue(row, col++);
					cfg.sectBoss = excelReadContext.getIntValue(row, col++);
					cfg.sectMapCopy = excelReadContext.getIntValue(row, col++);
					cfg.sectUpLvl = excelReadContext.getIntValue(row, col++);
					cfg.sectFight = excelReadContext.getIntValue(row, col++);
					cfg.manorFight = excelReadContext.getIntValue(row, col++);
					cfg.creed = excelReadContext.getIntValue(row, col++);
					cfg.mapReset = excelReadContext.getIntValue(row, col++);
					cfg.mapOpen = excelReadContext.getIntValue(row, col++);
					cfg.sectMail = excelReadContext.getIntValue(row, col++);
					cfg.qqGroup = excelReadContext.getIntValue(row, col++);
					cfg.changeSectName = excelReadContext.getIntValue(row, col++);
					cfg.changeSectIcon = excelReadContext.getIntValue(row, col++);
					cfg.changeJoinLvl = excelReadContext.getIntValue(row, col++);
					cfg.applyOption = excelReadContext.getIntValue(row, col++);
					cfg.kickMember = excelReadContext.getIntValue(row, col++);
					cfgList.add(cfg);
					row++;
					col = colStart;
				}
				gdCfgs.sectAuthority = cfgList;
			}

			// 帮派技能表
			excelReadContext.ReadSheet(2);
			{
				List<SBean.SectGroupSkillCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					//
					SBean.SectSkillCFGS cfg = new SBean.SectSkillCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					col += 2;
					cfg.lvl = excelReadContext.getIntValue(row, col++);
					cfg.items = new TreeMap<>();
					for (int index = 0; index < 4; index++)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if (checkItemIDValid(gdCfgs, id))
						{
							SBean.SectItemCFGS sectItem = new SBean.SectItemCFGS(id, value, count);
							cfg.items.put(id, sectItem);
						}
					}
					cfg.attri = excelReadContext.getIntValue(row, col++);
					cfg.attriValue = excelReadContext.getIntValue(row, col++);
					cfg.sectLvl = excelReadContext.getIntValue(row, col++);
					cfg.expcoinRateRaise = excelReadContext.getIntValue(row, col++);

					if (!sectIdToSkill.containsKey(cfg.id))
					{
						List<SBean.SectSkillCFGS> lists = new ArrayList<>();
						lists.add(cfg.lvl, cfg);
						sectIdToSkill.put(cfg.id, lists);
					}
					else
					{
						sectIdToSkill.get(cfg.id).add(cfg.lvl, cfg);
					}

					row++;
					col = colStart;
				}

				for (Map.Entry<Integer, List<SBean.SectSkillCFGS>> entry : this.sectIdToSkill.entrySet())
				{
					SBean.SectGroupSkillCFGS groupSkillCFGS = new SBean.SectGroupSkillCFGS();
					groupSkillCFGS.sectGroupSkill = new ArrayList<>();
					groupSkillCFGS.sectGroupSkill.addAll(entry.getValue());
					groupSkillCFGS.groupId = entry.getKey();
					cfgList.add(groupSkillCFGS);
				}

				gdCfgs.sectSkill = cfgList;
			}

			// 膜拜表
			excelReadContext.ReadSheet(4);
			{
				List<SBean.SectWorshipCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SectWorshipCFGS worshipCFGS = new SBean.SectWorshipCFGS();
					worshipCFGS.type = excelReadContext.getIntValue(row, col++);
					worshipCFGS.vipOpenLimit = excelReadContext.getIntValue(row, col++);
					worshipCFGS.moneyType = excelReadContext.getIntValue(row, col++);
					worshipCFGS.moneyValue = excelReadContext.getIntValue(row, col++);
					worshipCFGS.maxWorshipedTimes = excelReadContext.getIntValue(row, col++);

					cfgList.add(worshipCFGS);
					row++;
					col = colStart;
				}

				gdCfgs.sectWorship = cfgList;
			}

			// 膜拜经验表
			excelReadContext.ReadSheet(5);
			{
				List<SBean.SectWorshipExpCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SectWorshipExpCFGS worshipExpCFGS = new SBean.SectWorshipExpCFGS();
					worshipExpCFGS.level = excelReadContext.getIntValue(row, col++);
					worshipExpCFGS.exps = new ArrayList<>();
					for (int index = 0; index < 3; index++)
					{
						int exp = excelReadContext.getIntValue(row, col++);
						if (exp > 0)
						{
							worshipExpCFGS.exps.add(exp);
						}
					}

					worshipExpCFGS.offers = new ArrayList<>();
					for (int index = 0; index < 3; index++)
					{
						int offer = excelReadContext.getIntValue(row, col++);
						if (offer > 0)
						{
							worshipExpCFGS.offers.add(offer);
						}
					}

					cfgList.add(worshipExpCFGS);
					row++;
					col = colStart;
				}

				gdCfgs.sectWorshipExp = cfgList;
			}

			// 帮派宴席表
			excelReadContext.ReadSheet(3);
			{
				List<SBean.SectBanquetCFGS> cfgList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SectBanquetCFGS banquetCFGS = new SBean.SectBanquetCFGS();
					banquetCFGS.type = excelReadContext.getIntValue(row, col++);
					col += 2;
					banquetCFGS.moneyType = excelReadContext.getIntValue(row, col++);
					banquetCFGS.moneyValue = excelReadContext.getIntValue(row, col++);
					banquetCFGS.openCount = excelReadContext.getIntValue(row, col++);
					banquetCFGS.total = excelReadContext.getIntValue(row, col++);
					banquetCFGS.time = excelReadContext.getIntValue(row, col++);
					banquetCFGS.createSect = excelReadContext.getIntValue(row, col++);
					banquetCFGS.joinVit = excelReadContext.getIntValue(row, col++);
					banquetCFGS.percent = new ArrayList<>();
					banquetCFGS.rate = new ArrayList<>();
					List<String> percentrate = excelReadContext.getStringList(row, col++, ";");
					for (String rateitem: percentrate)
					{
						String[] str = rateitem.split(",");
						banquetCFGS.percent.add(Integer.parseInt(str[0]));
						banquetCFGS.rate.add(Integer.parseInt(str[1]));
					}
					cfgList.add(banquetCFGS);
					row++;
					col = colStart;
				}

				gdCfgs.sectBanquet = cfgList;
			}

			// 帮派图标表
			excelReadContext.ReadSheet(6);
			{
				List<SBean.SectIconCFGS> cfgs = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.SectIconCFGS iconCFGS = new SBean.SectIconCFGS();
					iconCFGS.iconId = excelReadContext.getIntValue(row, col++);	
					iconCFGS.open = excelReadContext.getIntValue(row, col++);	
					row++;
					cfgs.add(iconCFGS);
				}
				gdCfgs.sectIcons = cfgs;
			}


			excelReadContext.ReadSheet(7);
			{
				Map<Integer, SBean.SectGroupMapPersonRewardCFGS> cfgMap = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SectGroupMapRankRewardCFGS cfg = new SBean.SectGroupMapRankRewardCFGS();
					cfg.mapId = excelReadContext.getIntValue(row, col++);
					if(!cfgMap.containsKey(cfg.mapId))
					{
						cfgMap.put(cfg.mapId, new SBean.SectGroupMapPersonRewardCFGS(cfg.mapId,new ArrayList<>()));
					}
					SBean.SectGroupMapPersonRewardCFGS cfgGroup = cfgMap.get(cfg.mapId);
					cfg.rank = excelReadContext.getIntValue(row, col++);
					if ((cfgGroup.rewards.size() == 0 && cfg.rank != 1) || (cfgGroup.rewards.size() != 0 && cfg.rank <= cfgGroup.rewards.get(cfgGroup.rewards.size() - 1).rank))
						throw new Exception(" row " + row + " 帮派团队本个人排名奖励排名 " + cfg.rank + " 必须由1开始递增！");
					cfg.finishReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finishReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本个人排名通关奖励不能超过四个！");
					cfg.quickFinishReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.quickFinishReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本个人排名快速通关奖励不能超过四个！");
					cfg.finish10percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish10percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本个人排名10%奖励不能超过四个！");
					cfg.finish50percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish50percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本个人排名50%奖励不能超过四个！");
					cfg.finish75percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish75percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本个人排名75%奖励不能超过四个！");
					cfg.finish100percentReward = getDummyGoodsList(excelReadContext, row, col++, ";", gdCfgs);
					if (cfg.finish100percentReward.size() > 4)
						throw new Exception("row " + row + " 帮派团队本个人排名100%奖励不能超过四个！");

					cfgGroup.rewards.add(cfg);
					row++;
					col = colStart;
				}
				gdCfgs.sectGroupMapRankRewards = cfgMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadShopTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			gdCfgs.shops = new ArrayList<>();
			for (int s = 0; s <= 4; ++s)
			{
				SBean.ShopCFGS shopCfg = new SBean.ShopCFGS();
				excelReadContext.ReadSheet(s);
				{
					final int colStart = 1;
					final int rowStart = 0;
					int row = rowStart;
					int col = colStart;
					shopCfg.refreshTime = new ArrayList<>();
					{
						List<String> lst = excelReadContext.getStringList(row++, col, ";");
						for (String str : lst)
						{
							int daySecond = GameTime.parseSecondOfDay(str);
							if (daySecond < 0)
								throw new Exception("商城刷新时间 " + lst + "非法！");
							shopCfg.refreshTime.add(daySecond);
						}
					}
					if (shopCfg.refreshTime.isEmpty())
						throw new Exception("商城刷新时间没有配置！");
					shopCfg.refreshCount = excelReadContext.getIntValue(row++, col);
					shopCfg.refreshCurUnit = excelReadContext.getIntValue(row++, col);
					shopCfg.refreshPrice = excelReadContext.getIntegerList(row++, col, ";");
					shopCfg.refreshCurUnit2 = excelReadContext.getIntValue(row++, col);
					shopCfg.refreshPrice2 = excelReadContext.getIntegerList(row++, col, ";");
				}
				shopCfg.levels = new TreeMap<>();
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "货物列表");
					int row = rowStart + 2;
					int col = colStart + 1;
					Set<Integer> ids = new TreeSet<>();
					Map<Integer, Map<Integer, List<SBean.ShopGoodsCFGS>>> goods = new TreeMap<>();
					while (excelReadContext.isNotEmpty(row, col))
					{
						int id = excelReadContext.getIntValue(row, col++);
						if (!ids.add(id))
							throw new Exception("货物Id " + id + " 重复！");
						int type = excelReadContext.getIntValue(row, col++);
						if (type > shopCfg.refreshCount || type < 0)
							throw new Exception("货物Id " + id + " 所属类型  " + type + " 大于每次刷新货物个数或者小于0！");
						int weight = excelReadContext.getIntValue(row, col++);
						int goodsId = excelReadContext.getIntValue(row, col++);
						this.checkEntityIDValid(gdCfgs, row, goodsId, false);
						col += 1;
						int goodsCount = excelReadContext.getIntValue(row, col++);
						int currencyId = excelReadContext.getIntValue(row, col++);
						int price = excelReadContext.getIntValue(row, col++);
						int levelCeil = excelReadContext.getIntValue(row, col++);
						int startdate = 0;
						if(excelReadContext.isNotEmpty(row, col))
						{
							startdate = GameTime.getTime(new SimpleDateFormat("yyyy.MM.dd").parse(excelReadContext.getStringValue(row, col++)));
						}
						SBean.ShopGoodsCFGS sgCfg = new SBean.ShopGoodsCFGS(id, currencyId, price, weight, startdate, new SBean.DummyGoods(goodsId, goodsCount));
						goods.compute(levelCeil, (k, v) ->
						{
							if (v == null)
								v = new TreeMap<>();
							v.compute(type, (kk, vv) ->
							{
								if (vv == null)
									vv = new ArrayList<>();
								vv.add(sgCfg);
								return vv;
							});
							return v;
						});

						++row;
						col = colStart + 1;
					}
					for (Map.Entry<Integer, Map<Integer, List<SBean.ShopGoodsCFGS>>> e : goods.entrySet())
					{
						int levelCeil = e.getKey();
						SBean.ShopGoodsLevelCFGS sglevelCfg = new SBean.ShopGoodsLevelCFGS(new ArrayList<>());
						Map<Integer, List<SBean.ShopGoodsCFGS>> levelGoods = e.getValue();
						if (!levelGoods.containsKey(0) && levelGoods.size() < shopCfg.refreshCount)
							throw new Exception("商城 等级上限为 " + levelCeil + " 时，货物系列数目不足刷新数目，并且没有可选的 系列类型为0的货物！");
						for (Map.Entry<Integer, List<SBean.ShopGoodsCFGS>> ee : levelGoods.entrySet())
						{
							int type = e.getKey();
							SBean.ShopGoodsGroupCFGS sggcfg = new SBean.ShopGoodsGroupCFGS(ee.getValue());
							float sum = 0;
							for (SBean.ShopGoodsCFGS eee : sggcfg.goods)
							{
								sum += eee.pro;
							}
							float sp = 0;
							for (SBean.ShopGoodsCFGS eee : sggcfg.goods)
							{
								sp += eee.pro / sum;
								eee.pro = sp;
							}
							if (type == 0)
								sglevelCfg.groups.add(0, sggcfg);
							else
								sglevelCfg.groups.add(sggcfg);
						}
						shopCfg.levels.put(levelCeil, sglevelCfg);
					}
					shopCfg.allGoods = new TreeMap<>();
				}
				gdCfgs.shops.add(shopCfg);
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadGambleShopTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			gdCfgs.gambleShops = new ArrayList<>();
			for (int s = 0; s <= 1; ++s)
			{
				SBean.GambleShopCFGS shopCfg = new SBean.GambleShopCFGS();
				excelReadContext.ReadSheet(s);
				{
					final int colStart = 1;
					final int rowStart = 0;
					int row = rowStart;
					int col = colStart;
					shopCfg.refreshTime = new ArrayList<>();
					{
						List<String> lst = excelReadContext.getStringList(row++, col, ";");
						for (String str : lst)
						{
							int daySecond = GameTime.parseSecondOfDay(str);
							if (daySecond < 0)
								throw new Exception("商城刷新时间 " + lst + "非法！");
							shopCfg.refreshTime.add(daySecond);
						}
					}
					if (shopCfg.refreshTime.isEmpty())
						throw new Exception("商城刷新时间没有配置！");
					shopCfg.refreshCount = excelReadContext.getIntValue(row++, col);
					shopCfg.refreshCurUnit = excelReadContext.getIntValue(row++, col);
					shopCfg.refreshPrice = excelReadContext.getIntegerList(row++, col, ";");
				}
				shopCfg.levels = new TreeMap<>();
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "货物列表");
					int row = rowStart + 2;
					int col = colStart + 1;
					Set<Integer> ids = new TreeSet<>();
					Map<Integer, Map<Integer, List<SBean.GambleShopGoodsCFGS>>> goods = new TreeMap<>();
					while (excelReadContext.isNotEmpty(row, col))
					{
						int id = excelReadContext.getIntValue(row, col++);
						if (!ids.add(id))
							throw new Exception("货物Id " + id + " 重复！");
						int type = excelReadContext.getIntValue(row, col++);
						if (type > shopCfg.refreshCount || type < 0)
							throw new Exception("货物Id " + id + " 所属类型  " + type + " 大于每次刷新货物个数或者小于0！");
						int weight = excelReadContext.getIntValue(row, col++);
						int goodsId = excelReadContext.getIntValue(row, col++);
						this.checkRandomDropIDValid(gdCfgs, row, goodsId);
						col += 1;
						int currencyId = excelReadContext.getIntValue(row, col++);
						int price = excelReadContext.getIntValue(row, col++);
						int levelCeil = excelReadContext.getIntValue(row, col++);
						int startdate = 0;
						if(excelReadContext.isNotEmpty(row, col))
						{
							startdate = GameTime.getTime(new SimpleDateFormat("yyyy.MM.dd").parse(excelReadContext.getStringValue(row, col)));
						}
						col++;
						int conditionType = excelReadContext.getIntValue(row, col++);
						int conditionNum = excelReadContext.getIntValue(row, col++);
						
						SBean.GambleShopGoodsCFGS sgCfg = new SBean.GambleShopGoodsCFGS(id, currencyId, price, weight, startdate, conditionType, conditionNum, goodsId);
						goods.compute(levelCeil, (k, v) ->
						{
							if (v == null)
								v = new TreeMap<>();
							v.compute(type, (kk, vv) ->
							{
								if (vv == null)
									vv = new ArrayList<>();
								vv.add(sgCfg);
								return vv;
							});
							return v;
						});

						++row;
						col = colStart + 1;
					}
					for (Map.Entry<Integer, Map<Integer, List<SBean.GambleShopGoodsCFGS>>> e : goods.entrySet())
					{
						int levelCeil = e.getKey();
						SBean.GambleShopGoodsLevelCFGS sglevelCfg = new SBean.GambleShopGoodsLevelCFGS(new ArrayList<>());
						Map<Integer, List<SBean.GambleShopGoodsCFGS>> levelGoods = e.getValue();
						if (!levelGoods.containsKey(0) && levelGoods.size() < shopCfg.refreshCount)
							throw new Exception("商城 等级上限为 " + levelCeil + " 时，货物系列数目不足刷新数目，并且没有可选的 系列类型为0的货物！");
						for (Map.Entry<Integer, List<SBean.GambleShopGoodsCFGS>> ee : levelGoods.entrySet())
						{
							int type = e.getKey();
							SBean.GambleShopGoodsGroupCFGS sggcfg = new SBean.GambleShopGoodsGroupCFGS(ee.getValue());
							float sum = 0;
							for (SBean.GambleShopGoodsCFGS eee : sggcfg.goods)
							{
								sum += eee.pro;
							}
							float sp = 0;
							for (SBean.GambleShopGoodsCFGS eee : sggcfg.goods)
							{
								sp += eee.pro / sum;
								eee.pro = sp;
							}
							if (type == 0)
								sglevelCfg.groups.add(0, sggcfg);
							else
								sglevelCfg.groups.add(sggcfg);
						}
						shopCfg.levels.put(levelCeil, sglevelCfg);
					}
					shopCfg.allGoods = new TreeMap<>();
				}
				gdCfgs.gambleShops.add(shopCfg);
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadDailyTaskTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.DailyTaskCFGS> cfgs = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					int id = excelReadContext.getIntValue(row, col++);
					col += 3;
					int lvlReq = excelReadContext.getIntValue(row, col++);
					switch (id)
					{
					case GameData.DAILY_TASK_ID_FINISH_ANY_NORMAL_PRIVATE_MAPCOPY:
						break;
					case GameData.DAILY_TASK_ID_FINISH_ANY_NORMAL_PUBLIC_MAPCOPY:
						break;
					case GameData.DAILY_TASK_ID_FINISH_ANY_SECT_TASK:
						break;
					case GameData.DAILY_TASK_ID_START_ANY_NORMAL_ARENA:
						if(lvlReq < gdCfgs.arena.lvlReq)
							throw new Exception("row = " + row + " 活动类型 " + id + " 参与1V1竞技场的可见等级 " + lvlReq + " 小于1V1竞技场开放等级！");
						break;
					case GameData.DAILY_TASK_ID_FINISH_ANY_ACTIVITY_MAPCOPY_1:
						break;
					case GameData.DAILY_TASK_ID_START_ANY_SECT_MAPCOPY:
						break;
					case GameData.DAILY_TASK_ID_BUY_COINS:
						break;
					case GameData.DAILY_TASK_ID_FINISH_ANY_NORMAL_HARD_MAPCOPY:
						break;
					case GameData.DAILY_TASK_ID_FINISH_ANY_NORMAL_MAPCOPY:
						break;
					case GameData.DAILY_TASK_ID_FINISH_ANY_ACTIVITY_MAPCOPY_2:
						break;
					case GameData.DAILY_TASK_ID_REFRESH_TREASURE_INFO:
						if(lvlReq < gdCfgs.treasureBase.needlevel)
							throw new Exception("row = " + row + " 活动类型 " + id + " 寻宝专家的的可见等级 " + lvlReq + " 小于藏宝图的开放等级！");
						break;
					case GameData.DAILY_TASK_ID_FINISH_ANY_BW_MAPCOPY:
						if(lvlReq < gdCfgs.bwarena.base.lvlReq)
							throw new Exception("row = " + row + " 活动类型 " + id + " 完成正邪道场的可见等级 " + lvlReq + " 小于正邪道场开放等级！");
						break;
					case GameData.DAILY_TASK_ID_SECT_ESCORTCAR:
						break;
					case GameData.DAILY_TASK_ID_ENTER_ANY_SUPER_ARENA:
						if(lvlReq < gdCfgs.superarena.needLvl)
							throw new Exception("row = " + row + " 活动类型 " + id + " 参与会武的可见等级 " + lvlReq + " 小于会武的开放等级！");
						break;
					case GameData.DAILY_TASK_ID_ENTER_ANY_CLIMB_TOWER:
						if(lvlReq < gdCfgs.climbTowerBaseData.needlvl)
							throw new Exception("row = " + row + " 活动类型 " + id + " 参与爬塔的可见等级 " + lvlReq + " 小于爬塔的开放等级！");
						break;
					case GameData.DAILY_TASK_ID_GIVE_FRIEND_VIT:
						break;
					case GameData.DAILY_TASK_ID_SHARE_GIFT:
						break;
					default:
						throw new Exception("row = " + row + " 无效的每日活动类型 " + id);
					}
					col += 1;
					int times = excelReadContext.getIntValue(row, col++);
					int startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					int endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					float expFactor = excelReadContext.getFloatValue(row, col++);
					List<SBean.DummyGoods> rewards = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int rewardId = excelReadContext.getIntValue(row, col++);
						int rewardCount = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, rewardId, true);
						if (rewardId != 0)
							rewards.add(new SBean.DummyGoods(rewardId, rewardCount));
					}
					if (rewards.isEmpty())
						throw new Exception("日常任务Id " + id + " 奖励为空！");
					SBean.DailyTaskCFGS cfg = new SBean.DailyTaskCFGS(id, lvlReq, times, startTime, endTime, expFactor, rewards);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("日常任务Id " + id + " 重复！");
					row += 1;
					col = colStart;
				}
				gdCfgs.dailyTasks = cfgs;
			}
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.DailyTaskCFGS> cfgs = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					int id = excelReadContext.getIntValue(row, col++);
					col += 3;
					int lvlReq = excelReadContext.getIntValue(row, col++);
					switch (id)
					{
					case GameData.DAILY_TASK_ID_RECEIVE_MONTHLY_CARD:
						break;
					case GameData.DAILY_TASK_ID_RECEIVE_VIT_1:
						break;
					case GameData.DAILY_TASK_ID_RECEIVE_VIT_2:
						break;
					case GameData.DAILY_TASK_ID_RECEIVE_VIT_3:
						break;
					default:
						throw new Exception("row = " + row + " 无效的每日活动类型 " + id);
					}
					col += 1;
					int times = excelReadContext.getIntValue(row, col++);
					int startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					int endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					float expFactor = excelReadContext.getFloatValue(row, col++);
					List<SBean.DummyGoods> rewards = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int rewardId = excelReadContext.getIntValue(row, col++);
						int rewardCount = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, rewardId, true);
						if (rewardId != 0)
							rewards.add(new SBean.DummyGoods(rewardId, rewardCount));
					}
					if (rewards.isEmpty())
						throw new Exception("日常任务Id " + id + " 奖励为空！");
					SBean.DailyTaskCFGS cfg = new SBean.DailyTaskCFGS(id, lvlReq, times, startTime, endTime, expFactor, rewards);
					if (cfgs.put(cfg.id, cfg) != null)
						throw new Exception("日常任务Id " + id + " 重复！");
					switch (cfg.id)
					{
					case GameData.DAILY_TASK_ID_RECEIVE_VIT_1:
						gdCfgs.common.vipMissVitGet.morningNum = cfg.rewards.get(0).count / gdCfgs.common.vipMissVitGet.itemOfferVit;
						break;
					case GameData.DAILY_TASK_ID_RECEIVE_VIT_2:
						gdCfgs.common.vipMissVitGet.noonNum = cfg.rewards.get(0).count / gdCfgs.common.vipMissVitGet.itemOfferVit;
						break;
					case GameData.DAILY_TASK_ID_RECEIVE_VIT_3:
						gdCfgs.common.vipMissVitGet.nightNum = cfg.rewards.get(0).count / gdCfgs.common.vipMissVitGet.itemOfferVit;
						break;
					default:
						break;
					}
					row += 1;
					col = colStart;
				}
				gdCfgs.dailyActivities = cfgs;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadChallengeTaskTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;
			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				row = rowStart;
				Map<Integer, SBean.ChallengeTaskGroupCFGS> cfgMap = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ChallengeTaskCFGS cfg = new SBean.ChallengeTaskCFGS();
					cfg.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.CHALLENGE_TASK_ID_LEVEL:
					case GameData.CHALLENGE_TASK_ID_FIGHTPOWER:
					case GameData.CHALLENGE_TASK_ID_WEAR_PEQUIP:
					case GameData.CHALLENGE_TASK_ID_WEAR_OEQUIP:
					case GameData.CHALLENGE_TASK_ID_ONE_STORY_MAPCOPY:
					case GameData.CHALLENGE_TASK_ID_ONE_EASY_MAPCOPY:
					case GameData.CHALLENGE_TASK_ID_ONE_HARD_MAPCOPY:
					case GameData.CHALLENGE_TASK_ID_ONE_TEAM_MAPCOPY:
					case GameData.CHALLENGE_TASK_ID_ANY_PRIVATE_MAPCOPY:
					case GameData.CHALLENGE_TASK_ID_ANY_PUBLIC_MAPCOPY:
					case GameData.CHALLENGE_TASK_ID_NORMALARENA_TOP100:
					case GameData.CHALLENGE_TASK_ID_SUPERARENA_TOP100:
					case GameData.CHALLENGE_TASK_ID_USE_BIND_COIN:
					case GameData.CHALLENGE_TASK_ID_USE_BIND_DIAMOND:
					case GameData.CHALLENGE_TASK_ID_USE_FREE_COIN:
					case GameData.CHALLENGE_TASK_ID_USE_FREE_DIAMOND:
					case GameData.CHALLENGE_TASK_ID_GAIN_WEAPON:
					case GameData.CHALLENGE_TASK_ID_WEAPON_LEVEL:
					case GameData.CHALLENGE_TASK_ID_WEAPON_STAR:
					case GameData.CHALLENGE_TASK_ID_GAIN_PET:
					case GameData.CHALLENGE_TASK_ID_PET_LEVEL:
					case GameData.CHALLENGE_TASK_ID_PET_STAR:
					case GameData.CHALLENGE_TASK_ID_FRIEND_COUNT:
					case GameData.CHALLENGE_TASK_ID_OPSEX_FRIEND_COUNT:
					case GameData.CHALLENGE_TASK_ID_MARRY:
					case GameData.CHALLENGE_TASK_ID_SWORN:
					case GameData.CHALLENGE_TASK_ID_JOIN_SECT:
					case GameData.CHALLENGE_TASK_ID_EQUIP_STRENGTHEN:
					case GameData.CHALLENGE_TASK_ID_EQUIP_STAR_UP:
					case GameData.CHALLENGE_TASK_ID_SKILL_LEVEL:
					case GameData.CHALLENGE_TASK_ID_UNIQUE_SKILL_LEVEL:
					case GameData.CHALLENGE_TASK_ID_SPIRIT:
					case GameData.CHALLENGE_TASK_ID_EMBED:
					case GameData.CHALLENGE_TASK_ID_ANY_GEMLEVEL:
					case GameData.CHALLENGE_TASK_ID_TREASURE:
					case GameData.CHALLENGE_TASK_ID_RIDING_LEVEL:
					case GameData.CHALLENGE_TASK_ID_SUPERARENA_TIMES:
					case GameData.CHALLENGE_TASK_ID_FORCEWAR_TIMES:
					case GameData.CHALLENGE_TASK_ID_ANY_TOWER_LEVEL:
					case GameData.CHALLENGE_TASK_ID_BW_COPY_LEVEL:
					case GameData.CHALLENGE_TASK_ID_RAREBOOK:
					case GameData.CHALLENGE_TASK_ID_GRASP_LEVEL:
					case GameData.CHALLENGE_TASK_ID_PRODUCE_LEVEL:
					case GameData.CHALLENGE_TASK_ID_SELL_OR_BUY:
					case GameData.CHALLENGE_TASK_ID_ANY_ARMORLEVEL:
					case GameData.CHALLENGE_TASK_ID_ANY_ARMORRANK:
						break;
					default:
						throw new Exception("row = " + row + " 无效的挑战任务类型 " + cfg.type);
					}
					
					
					SBean.ChallengeTaskGroupCFGS group = cfgMap.get(cfg.type);
					if(group == null)
					{
						group = new SBean.ChallengeTaskGroupCFGS(cfg.type, new ArrayList<>());
						cfgMap.put(cfg.type, group);
					}
					group.tasks.add(cfg);
					
					cfg.target = excelReadContext.getIntValue(row, col++);
					cfg.param1 = excelReadContext.getIntValue(row, col++);
					cfg.levelNeed = excelReadContext.getIntValue(row, col++);
					
					col = colStart + 7;
					cfg.exp = excelReadContext.getIntValue(row, col++);
					cfg.rewards = new ArrayList<>();
					for(int i=0; i<3; i++)
					{
						SBean.DummyGoods goods = new SBean.DummyGoods();
						goods.id = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, goods.id, true);
						goods.count = excelReadContext.getIntValue(row, col++);
						if(goods.id != 0)
							cfg.rewards.add(goods);
					}
					
					col = colStart + 15;
					cfg.title = excelReadContext.getIntValue(row, col++);
					if(cfg.title > 0)
						checkTitleIDValid(gdCfgs, row, cfg.title);
					
					cfg.achtype = excelReadContext.getByteValue(row, col++);
					cfg.achpoint = excelReadContext.getIntValue(row, col++);
					
					switch (cfg.achtype)
					{
					    case GameData.ACHIEVEMENT_TYPE_BUDO:
					    case GameData.ACHIEVEMENT_TYPE_GEWU:
					    case GameData.ACHIEVEMENT_TYPE_XIAMING:
					    case GameData.ACHIEVEMENT_TYPE_CITIES:
					        break;
					    default:
					        throw new Exception("row = " + row + " 无效的成就类型 " + cfg.type);
					}
					
					row++;
				}
				gdCfgs.challengeTaskGroups = cfgMap;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadFameTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.FameCFGS fcfg = new SBean.FameCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 2;
				int row = rowStart;
				int col = colStart;
				row = rowStart;
				List<SBean.FameLevelCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int level = excelReadContext.getIntValue(row, col++);
					if (level != cfgs.size() + 1)
						throw new Exception("声望等级不连续!");
					col += 1;
					List<SBean.FameConditionCFGS> conditions = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						if (id > 0)
						{
							switch (id)
							{
								case GameData.FAME_ID_ROLE_LEVEL:
								case GameData.FAME_ID_HIGHEST_POWER:
								case GameData.FAME_ID_JOIN_SECT:
								case GameData.FAME_ID_MAX_FRIENDS:
								case GameData.FAME_ID_TOTAL_CHARM:
								case GameData.FAME_ID_TOTAL_SECT_CONTRIBUTION:
								case GameData.FAME_ID_TOTAL_AREA_POINT:
								case GameData.FAME_ID_TOTAL_SUPERAREA_HONOR:
								case GameData.FAME_ID_TOTAL_ROBMONEY:
								case GameData.FAME_ID_TOTAL_DELIVER:
								case GameData.FAME_ID_TOTAL_OPEN_BANQUETS:
								case GameData.FAME_ID_TOTAL_DIY_SKILLS:
								case GameData.FAME_ID_TOTAL_ENTER_ACTIVITY_MAPCOPYS:
								case GameData.FAME_ID_ALL_TOWER_LEVEL:
									break;
								default:
									throw new Exception("row " + row + " 声望条件id=" + id + " 非法!");
							}
							conditions.add(new SBean.FameConditionCFGS(id, value));	
						}
					}
					List<SBean.DummyGoods> cost = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if (id != 0)
						{
							this.checkEntityIDValid(gdCfgs, row, id, false);
							cost.add(new SBean.DummyGoods(id, count));	
						}
					}
					List<SBean.DummyGoods> rewards = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if (id != 0)
						{
							this.checkEntityIDValid(gdCfgs, row, id, true);
							rewards.add(new SBean.DummyGoods(id, count));	
						}
					}
					int cd = excelReadContext.getIntValue(row, col++);
					
					int title = excelReadContext.getIntValue(row, col++);
					if(title > 0)
						checkTitleIDValid(gdCfgs, row, title);
					
					cfgs.add(new SBean.FameLevelCFGS(level, conditions, cost, rewards, cd, title));
					row++;
				}
				fcfg.fames = cfgs;
			}
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 1;
				final int rowStart = 0;
				int row = rowStart;
				int col = colStart;
				fcfg.openLevel = excelReadContext.getIntValue(row, col++);
			}
			gdCfgs.fames = fcfg;
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadQuestionsBankTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				List<SBean.QuestionCFGS> questions = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					col += 5;
					int answer = excelReadContext.getIntValue(row, col++);
					if (id != questions.size()+1)
						throw new Exception("row " + row + " 题库 id不连续!");
					if (answer < 1 || answer > 4)
						throw new Exception("row " + row + " 题库 答案不是1到4之间的数!");
					
					questions.add(new SBean.QuestionCFGS(id, answer));
					
					++row;
					col = colStart;
				}
				gdCfgs.activityQuestionsBank = questions;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				List<SBean.QuestionCFGS> questions = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					col += 5;
					int answer = excelReadContext.getIntValue(row, col++);
					if (id != questions.size()+1)
						throw new Exception("row " + row + " 任务题库 id不连续!");
					if (answer < 1 || answer > 4)
						throw new Exception("row " + row + " 任务题库 答案不是1到4之间的数!");
					
					questions.add(new SBean.QuestionCFGS(id, answer));
					
					++row;
					col = colStart;
				}
				gdCfgs.taskQuestionsBank = questions;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				List<SBean.SurveyQuestionCFGS> questions = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SurveyQuestionCFGS surveyquestionitem = new SBean.SurveyQuestionCFGS();
					surveyquestionitem.id = excelReadContext.getIntValue(row, col++);
					surveyquestionitem.question = excelReadContext.getStringValue(row, col++);
					surveyquestionitem.type = excelReadContext.getByteValue(row, col++);
					surveyquestionitem.options = new ArrayList<String>();
					while(excelReadContext.isNotEmpty(row, col))
					{
						surveyquestionitem.options.add(excelReadContext.getStringValue(row, col++));
					}
					if (surveyquestionitem.id != questions.size()+1)
						throw new Exception("row " + row + " 任务题库 id不连续!");
					if (surveyquestionitem.options.isEmpty())
						throw new Exception("row " + row + " 调研题库未设置任何选项!");
					questions.add(surveyquestionitem);
					
					++row;
					col = colStart;
				}
				gdCfgs.betaActivity.surveyQuestions = questions;
			}
			
			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.RandQuestionCFGS cfg = new SBean.RandQuestionCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.questions = new HashSet<>();
					for(int i = 0; i < 6; i++)
					{
						int q = excelReadContext.getIntValue(row, col++);
						if(q > 0)
							checkTaskQuestionIDValid(gdCfgs, row, q);
						
						if(!cfg.questions.add(q))
							throw new Exception("row " + row + " 任务问答 题库ID " + q + " 重复！");
					}
					
					for (int i = 0; i < 8; ++i)
					{
						int dialogueID = excelReadContext.getIntValue(row,  col++);
						if (dialogueID > 0)
							checkDialogIDValid(gdCfgs, row, dialogueID);
						
						int modelID = excelReadContext.getIntValue(row,  col++);
						if (modelID > 0)
							checkModelIDValid(gdCfgs, row, modelID);
					}
					
					if(this.randQuestions.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 任务问答 ID " + cfg.id + " 重复！");
					
					row++;
				}
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	public void loadOtherActivityTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.DailyOnlineGiftCFGS> cfg = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int minute = excelReadContext.getIntValue(row, col++);
					List<SBean.DummyGoods> lst = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, id, true);
						if (id != 0)
							lst.add(new SBean.DummyGoods(id, count));
					}
					if (lst.isEmpty())
						throw new Exception("每日在线奖励 在线时长 " + minute + " 奖励为空！");
					if (cfg.put(minute, new SBean.DailyOnlineGiftCFGS(minute, lst)) != null)
						throw new Exception("每日在线奖励 在线时长 " + minute + " 重复！");

					++row;
					col = colStart;
				}
				gdCfgs.dailyOnlineGift = cfg;
			}
			excelReadContext.ReadSheet(1);
			{
				int rowStart = 0;
				int colStart = 0;
				int row = rowStart;
				int col = colStart + 1;
				SBean.DailyQuizGiftCFGS cfg = new SBean.DailyQuizGiftCFGS();
				cfg.levelReq = excelReadContext.getIntValue(row++, col);
				cfg.openTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
				cfg.noticeTime = excelReadContext.getIntValue(row++, col);
				cfg.showTime = excelReadContext.getIntValue(row++, col);
				cfg.maxAnswerTime = excelReadContext.getIntValue(row++, col);
				row += 1;
				cfg.questionsCount = excelReadContext.getIntValue(row++, col);
				if (cfg.questionsCount > gdCfgs.activityQuestionsBank.size()/2)
					throw new Exception("全服答题活动每次随机题目数目 " + cfg.questionsCount + " 大于题库所有题目数目的一半！");
				cfg.doubleBonusTimes = excelReadContext.getIntValue(row++, col);
				cfg.baseBonus = excelReadContext.getIntValue(row++, col);
				cfg.maxRewardRank = excelReadContext.getIntValue(row++, col);
				cfg.answerQuestionExp = excelReadContext.getIntValue(row++, col);
				cfg.answerRightExp = excelReadContext.getIntValue(row++, col);
				cfg.quickBonus = new ArrayList<>();
				{
					rowStart = excelReadContext.locateColumnTag(colStart, "快速正确答题增加分数配置");
					row = rowStart + 1;
					col = colStart + 1;
					int lastTimeCeil = 0;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int timeCeil = excelReadContext.getIntValue(row++, col);
						if (timeCeil <= lastTimeCeil)
							throw new Exception("row " + row + " col " + col + " 快速答题时间上限非递增！");
						lastTimeCeil = timeCeil;
						int bonus = excelReadContext.getIntValue(row++, col);
						cfg.quickBonus.add(new SBean.QuicklyAnswerBonusCFGS(timeCeil, bonus));
						row = rowStart + 1;
						++col;
					}	
				}
				cfg.continuousBonus = new ArrayList<>();
				{
					rowStart = excelReadContext.locateColumnTag(colStart, "连续答题正确增加分数配置");
					row = rowStart + 1;
					col = colStart + 1;
					int lastTimesFloor = 0;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int timesFloor = excelReadContext.getIntValue(row++, col);
						if (timesFloor <= lastTimesFloor)
							throw new Exception("row " + row + " col " + col + " 连续答对次数下限非递增！");
						lastTimesFloor = timesFloor;
						if (timesFloor > cfg.questionsCount)
							throw new Exception("row " + row + " col " + col + " 快速答对次数大于最大题目数！");
						int bonus = excelReadContext.getIntValue(row++, col);
						cfg.continuousBonus.add(new SBean.ContinuousAnswerBonusCFGS(timesFloor, bonus));
						
						row = rowStart + 1;
						++col;
					}	
				}
				
				cfg.bonusReward = new ArrayList<>();
				{
					rowStart = excelReadContext.locateColumnTag(colStart, "排名奖励配置");
					row = rowStart + 1;
					col = colStart + 1;
					int lastRankFloor = 0;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int rankFloor = excelReadContext.getIntValue(row++, col);
						if (rankFloor <= lastRankFloor)
							throw new Exception("row " + row + " col " + col + " 积分奖励下限非递增！");
						lastRankFloor = rankFloor;
						List<SBean.DummyGoods> rewards = new ArrayList<>();
						for (int i = 0; i < 4; ++i)
						{
							int iid = excelReadContext.getIntValue(row++, col);
							this.checkEntityIDValid(gdCfgs, row, iid, true);
							int icount = excelReadContext.getIntValue(row++, col);
							if (iid != 0)
								rewards.add(new SBean.DummyGoods(iid, icount));
						}
						int titleID = excelReadContext.getIntValue(row++, col);
						if(titleID > 0 && !gdCfgs.titles.containsKey(titleID))
							throw new Exception("全服答题 科举称号ID " + titleID + " 非法！");
						
						cfg.bonusReward.add(new SBean.QuizBonusRewardCFGS(rankFloor, rewards, titleID));
						row = rowStart + 1;
						++col;
					}
					
				}
				gdCfgs.dailyQuizGift = cfg;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart + 1;
				SBean.LuckyWheelCFGS cfg = new SBean.LuckyWheelCFGS();
				{
					cfg.openAtLevel = excelReadContext.getIntValue(row++, col);
					cfg.beginTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
					cfg.endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
					cfg.dayFreeDrawTimes = excelReadContext.getIntValue(row++, col);
					cfg.dayBuyTimes = excelReadContext.getIntValue(row++, col);
					cfg.buyTimesNeedDiamond = excelReadContext.getIntegerList(row++, col, ";");
					
					row = excelReadContext.locateColumnTag(colStart, "奖励档次配置") + 2;
					List <Integer> lower = new ArrayList<>();
					int check = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int value = excelReadContext.getIntValue(row++, col);
						if( value < check ){
							throw new Exception("等级下限非递增");
						}
						check = value;
						lower.add(value);
					}
					
					row = excelReadContext.locateColumnTag(colStart, "奖励库配置") + 2;
					col = colStart ;
					List<SBean.LuckyWheelGradeCFGS> gradeCfg = new ArrayList<>();
					int tempGrade = 1;
					while (excelReadContext.isNotEmpty(row, col))
					{
						SBean.LuckyWheelGradeCFGS  singleGradeCfg = new SBean.LuckyWheelGradeCFGS();
						int gradeId = excelReadContext.getIntValue(row, col);
						singleGradeCfg.gradeId = excelReadContext.getIntValue(row, col);
						singleGradeCfg.levelCeil = lower.get(gradeId - 1);
						List<SBean.LuckyWheelRewardsCFGS> rewardList = new ArrayList<>();
						
						while(excelReadContext.isNotEmpty(row, colStart) && tempGrade == excelReadContext.getIntValue(row, colStart) )
						{
							SBean.LuckyWheelRewardsCFGS singleReward = new SBean.LuckyWheelRewardsCFGS();
							singleReward.id = excelReadContext.getIntValue(row, col++);
							List<SBean.LuckyWheelItemCFGS> itemList = new ArrayList<>();
							for(int j = 0; j < 8; ++j)
							{
								int itemId = excelReadContext.getIntValue(row, col++);
								int count = excelReadContext.getIntValue(row, col++);
								int weight = excelReadContext.getIntValue(row, col++);
								itemList.add(new SBean.LuckyWheelItemCFGS(new SBean.DummyGoods(itemId, count), weight));
							}
							singleReward.items = itemList;
							rewardList.add(singleReward);
							row++;
							col = colStart + 1;
						}
						tempGrade ++ ;
						singleGradeCfg.rewards = rewardList;
						col = colStart;
						gradeCfg.add(singleGradeCfg);
					}
					cfg.gradeList = gradeCfg;
				}
				gdCfgs.luckyWheel = cfg ;
			}
			
			excelReadContext.ReadSheet(3);
			{
				int rowStart = 0;
				int colStart = 0;
				int row = rowStart;
				int col = colStart + 1;
				SBean.OfflineExpCFGS cfg = new SBean.OfflineExpCFGS();
				cfg.expPerMinute = excelReadContext.getIntValue(row++, col);
				cfg.maxAccTime = excelReadContext.getIntValue(row++, col);
				cfg.doubleCostPerMinute = excelReadContext.getFloatValue(row++, col);
				row ++;
				cfg.openLevel = excelReadContext.getIntValue(row++, col);
				cfg.monsterNumPerMin = excelReadContext.getIntValue(row++, col);
				cfg.funcPointBuyNum = excelReadContext.getIntValue(row++, col);
				cfg.buyFuncPointDiamond = excelReadContext.getIntegerList(row++, col, ";");
				cfg.maxFuncPoint = excelReadContext.getIntValue(row++, col);
				cfg.dayMaxAccTime = excelReadContext.getIntValue(row++, col);
				cfg.distribute = new HashMap<>();
				{
					rowStart = excelReadContext.locateColumnTag(colStart, "经验发放配置");
					row = rowStart + 1;
					col = colStart + 1;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int type = excelReadContext.getIntValue(row, col++);
						int id = excelReadContext.getIntValue(row, col++);
						float factor = excelReadContext.getFloatValue(row, col++);
						switch (type)
						{
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_KILL_MONSTER:
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_DAILY_TASK:
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_ACTIVITY_MAPCOPY:
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_MAIN_TASK:
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_SECT_TASK:
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_BRANCH_TASK:
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_SECT_DELIVER:
						case GameData.OFFLINE_EXP_DISTRIBUTE_TYPE_MARRIAGE_TASK:
							break;
						default:
							throw new Exception("row = " + row + " 无效的经验发放配置类型 " + type);
						}
						SBean.OfflineExpDistributeCFGS dcfg = cfg.distribute.get(type);
						if (dcfg == null)
						{
							dcfg = new SBean.OfflineExpDistributeCFGS(type, new HashMap<>());
							cfg.distribute.put(type, dcfg);
						}
						if (dcfg.id2ExpFactor.put(id, factor) != null)
							throw new Exception("row " + row + " col " + col + " 离线经验发放方式重复！");
							
						++row;
						col = colStart + 1;
					}	
				}
				cfg.levelMonster = new ArrayList<>();
				{
					rowStart = excelReadContext.locateColumnTag(colStart, "挂机精灵怪物配置");
					row = rowStart + 1;
					while (excelReadContext.isNotEmpty(row, col))
					{
						col = colStart + 1;
						int level = excelReadContext.getIntValue(row, col++);
						int monsterId = excelReadContext.getIntValue(row, col);
						if (level != cfg.levelMonster.size() + 1)
							throw new Exception("row " + row + " col " + col + " 挂机精灵等级对应怪物ID等级 " + level + " 不连续！");
						if (!gdCfgs.monsters.containsKey(monsterId))
							throw new Exception("row " + row + " col " + col + " 挂机精灵等级对应怪物ID " + monsterId + " 不存在！");
						cfg.levelMonster.add(monsterId);
						row++;
					}	
				}
				cfg.levelFunc = new ArrayList<>();
				{
					rowStart = excelReadContext.locateColumnTag(colStart, "挂机精灵特权");
					row = rowStart + 1;
					while (excelReadContext.isNotEmpty(row, colStart + 1))
					{
						col = colStart + 1;
						SBean.OfflineWizardFuncCFGS funccfg = new SBean.OfflineWizardFuncCFGS();
						funccfg.level = excelReadContext.getIntValue(row, col++);
						if (funccfg.level != cfg.levelFunc.size() + 1)
							throw new Exception("row " + row + " col " + col + " 挂机精灵特权等级 " + funccfg.level + " 不连续！");
						funccfg.levelUpExp = excelReadContext.getIntValue(row, col++);
						if (funccfg.levelUpExp < 0)
							throw new Exception("row " + row + " col " + col + " 挂机精灵特权升级经验 " + funccfg.levelUpExp + " 不能小于0！");
						funccfg.offlineCoin = excelReadContext.getIntValue(row, col++);
						if (funccfg.offlineCoin < 0)
							throw new Exception("row " + row + " col " + col + " 挂机精灵特权离线铜钱奖励 " + funccfg.offlineCoin + " 不能小于0！");
						funccfg.offlineDrop = excelReadContext.getIntValue(row, col++);
						if (funccfg.offlineDrop < 0)
							throw new Exception("row " + row + " col " + col + " 挂机精灵特权离线掉落奖励 " + funccfg.offlineDrop + " 不能小于0！");
						funccfg.activitySweep = excelReadContext.getIntegerList(row, col++, ";");
						if (funccfg.activitySweep.get(0) == 0)
							funccfg.activitySweep.clear();
						funccfg.offlineExpGet = excelReadContext.getIntValue(row, col++);
						if (funccfg.offlineExpGet < 0)
							throw new Exception("row " + row + " col " + col + " 挂机精灵特权离线经验获取 " + funccfg.offlineExpGet + " 不能小于0！");
						cfg.levelFunc.add(funccfg);
						row++;
					}	
				}
				gdCfgs.offlineExp = cfg;
			}
			excelReadContext.ReadSheet(4);
			{
				int rowStart = 0;
				int colStart = 0;
				int row = rowStart;
				int col = colStart + 1;
				SBean.RedEnvelopeCFGS cfg = new SBean.RedEnvelopeCFGS();
				String beginTime = excelReadContext.getStringValue(row++, col);
				String endTime = excelReadContext.getStringValue(row++, col);
				cfg.beginTime = GameTime.parseSecondOfDay(beginTime);
				cfg.endTime = GameTime.parseSecondOfDay(endTime);
				if (cfg.beginTime >= cfg.endTime)
					throw new Exception("抢红包结束时间 " + beginTime +" 不大于开始时间 " + endTime);
				cfg.checkInterval = excelReadContext.getIntegerList(row++, col, ";");
				cfg.checkThreshold = excelReadContext.getIntValue(row++, col);
				cfg.overlimitPercent = excelReadContext.getIntValue(row++, col)/100.0f;
				if (cfg.overlimitPercent < 0 || cfg.overlimitPercent > 1.0f)
					throw new Exception("抢红包超限概率非法!");
				cfg.firstHalfPercent = excelReadContext.getIntValue(row++, col)/100.0f;
				if (cfg.overlimitPercent < 0 || cfg.overlimitPercent > 1.0f)
					throw new Exception("抢红包前一半概率非法!");
				cfg.secondHalfPercent = excelReadContext.getIntValue(row++, col)/100.0f;
				if (cfg.overlimitPercent < 0 || cfg.overlimitPercent > 1.0f)
					throw new Exception("抢红包后一半概率非法!");
				cfg.openLevel = excelReadContext.getIntValue(row++, col);
				cfg.dayEmptyGiftTimes = excelReadContext.getIntValue(row++, col);
				if (cfg.dayEmptyGiftTimes < 0)
					throw new Exception("每日空红包礼物次数非法!");
				cfg.emptyGiftId = excelReadContext.getIntValue(row++, col);
				checkEntityIDValid(gdCfgs, row, cfg.emptyGiftId, false);
				cfg.levelData = new ArrayList<>();
				{
					rowStart = excelReadContext.locateColumnTag(colStart, "触发配置表");
					row = rowStart + 2;
					col = colStart;
					int lastPayFloor = 0;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int payFloor = excelReadContext.getIntValue(row, col++);
						if (payFloor <= lastPayFloor)
							throw new Exception("row " + row + " col " + col + " 抢红包充值触发元宝数下限非递增！");
						lastPayFloor = payFloor;
						int count = excelReadContext.getIntValue(row, col++);
						int minDiamond = excelReadContext.getIntValue(row, col++);
						int maxDiamond = excelReadContext.getIntValue(row, col++);
						int lifeTime = excelReadContext.getIntValue(row, col++);
						int rollNoticeId = excelReadContext.getIntValue(row, col++);
						cfg.levelData.add(new SBean.RedEnvelopeLevelCFGS(payFloor, count, minDiamond, maxDiamond, lifeTime, rollNoticeId));
						++row;
						col = colStart;
					}	
				}
				gdCfgs.redEnvelope = cfg;
			}
			excelReadContext.ReadSheet(5);
			{
				int rowStart = 0;
				int colStart = 0;
				int row = rowStart + 1;
				int col = colStart;
				List<SBean.ActivityChallengeTypeCFGS> accfgs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.ActivityChallengeTypeCFGS cfg = new SBean.ActivityChallengeTypeCFGS();
					col = colStart;
					cfg.id = excelReadContext.getIntValue(row, col++);
					if (cfg.id != accfgs.size() + 1)
						throw new Exception("row " + row + " col " + col + " id不连续！");
					cfg.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.ACTIVITY_CHALLENGE_TYPE_JJC:
					case GameData.ACTIVITY_CHALLENGE_TYPE_ZXDC:
					case GameData.ACTIVITY_CHALLENGE_TYPE_HWC:
					case GameData.ACTIVITY_CHALLENGE_TYPE_ZXSLZ:
					case GameData.ACTIVITY_CHALLENGE_TYPE_ACTIVITY_MAP:
					case GameData.ACTIVITY_CHALLENGE_TYPE_WORLD_BOSS:
						break;
					default:
						throw new Exception("row " + row + " col " + col + " 无效的类型！");
					}
					cfg.arg = excelReadContext.getIntValue(row, col++);
					accfgs.add(cfg);
					row++;
				}
				gdCfgs.activityChallengeType = accfgs;
			}

			System.out.println("load table " + fileName + " success.");
		}
	}
	
	
	public void loadRankTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				int rowStart = 0;
				int colStart = 0;
				int row = rowStart + 1;
				int col = colStart;
				List<SBean.RoleRankCFGS> cfgs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					switch (id)
					{
					case GameData.RANK_TYPE_ROLE_LEVEL:
					case GameData.RANK_TYPE_ROLE_POWER:
					case GameData.RANK_TYPE_PETS_POWER:
					case GameData.RANK_TYPE_WEAPONS_POWER:
					case GameData.RANK_TYPE_SUPER_ARENA_HISTORY:
					case GameData.RANK_TYPE_SUPER_ARENA_WEEK:
					case GameData.RANK_TYPE_CHARM_FEMALE:
					case GameData.RANK_TYPE_CHARM_MALE:
					case GameData.RANK_TYPE_LOCAL_FORCEWAR_WHITE:
					case GameData.RANK_TYPE_LOCAL_FORCEWAR_BLACK:
					case GameData.RANK_TYPE_GLOBAL_FORCEWAR_WHITE:
					case GameData.RANK_TYPE_GLOBAL_FORCEWAR_BLACK:
					case GameData.RANK_TYPE_ROLE_ACHIEVE:
					case GameData.RANK_TYPE_ROLE_LEVEL_BALDE:
					case GameData.RANK_TYPE_ROLE_LEVEL_SWORD:
					case GameData.RANK_TYPE_ROLE_LEVEL_SPEAR:
					case GameData.RANK_TYPE_ROLE_LEVEL_ARROW:
					case GameData.RANK_TYPE_ROLE_LEVEL_HEAL:
					case GameData.RANK_TYPE_ROLE_POWER_BALDE:
					case GameData.RANK_TYPE_ROLE_POWER_SWORD:
					case GameData.RANK_TYPE_ROLE_POWER_SPEAR:
					case GameData.RANK_TYPE_ROLE_POWER_ARROW:
					case GameData.RANK_TYPE_ROLE_POWER_HEAL:
					case GameData.RANK_TYPE_ROLE_MASTER_REPUTATION:
						break;
					default:
						throw new Exception("row = " + row + " 无效的排行榜类型 " + id);
					}
					if (id != cfgs.size() + 1)
						throw new Exception("row " + row + "  排行榜类型Id不连续！");

					col = colStart + 4;
					int length = excelReadContext.getIntValue(row, col++);
					if (length <= 0 || length > GameData.MAX_RANK_LIST_SIZE)
						throw new Exception("排行榜  id = " + id + " 排行榜长度  " + length + " 非法！");
					List<Integer> refreshTimes = new ArrayList<>();
					{
						List<String> lst = excelReadContext.getStringList(row, col++, ";");
						for (String str : lst)
						{
							int daySecond = GameTime.parseSecondOfDay(str);
							if (daySecond < 0)
								throw new Exception("排行榜  id = " + id + " 刷新时间 " + lst + "非法！");
							refreshTimes.add(daySecond);
						}
					}
					if (refreshTimes.isEmpty())
						throw new Exception("排行榜  id = " + id + " 刷新时间没有配置！");
					
					byte rankType = excelReadContext.getByteValue(row, col++);
					int lvlReq = excelReadContext.getIntValue(row, col++);
					List<SBean.RankTitle> titles = new ArrayList<>();
					int titleLastRank = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int floor = excelReadContext.getIntValue(row, col++);
						int title = excelReadContext.getIntValue(row, col++);
						if(floor == 0)
							break;
						
						titles.add(new SBean.RankTitle(floor, title));
						titleLastRank = floor;
					}
					cfgs.add(new SBean.RoleRankCFGS(new SBean.RankCFGS(id, length, refreshTimes, rankType, lvlReq, titles, titleLastRank)));
					
					++row;
					col = rowStart;
				}
				gdCfgs.roleRanks = cfgs;
			}
			
			excelReadContext.ReadSheet(2);
			{
				int rowStart = 0;
				int colStart = 0;
				int row = rowStart + 1;
				int col = colStart;
				List<SBean.SectRankCFGS> cfgs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					switch (id)
					{
					case GameData.RANK_TYPE_SECT_WEEK_VIT:
						break;
					default:
						throw new Exception("row = " + row + " 无效的帮派排行榜类型 " + id);
					}
					if (id != cfgs.size() + 1)
						throw new Exception("row " + row + "  帮派排行榜类型Id不连续！");
					
					col = colStart + 4;
					int length = excelReadContext.getIntValue(row, col++);
					if (length <= 0 || length > GameData.MAX_RANK_LIST_SIZE)
						throw new Exception("帮派排行榜  id = " + id + " 排行榜长度  " + length + " 非法！");
					
					List<Integer> refreshTimes = new ArrayList<>();
					{
						List<String> lst = excelReadContext.getStringList(row, col++, ";");
						for (String str : lst)
						{
							int daySecond = GameTime.parseSecondOfDay(str);
							if (daySecond < 0)
								throw new Exception("帮派排行榜  id = " + id + " 刷新时间 " + lst + "非法！");
							refreshTimes.add(daySecond);
						}
					}
					if (refreshTimes.isEmpty())
						throw new Exception("帮派排行榜  id = " + id + " 刷新时间没有配置！");
					
					byte rankType = excelReadContext.getByteValue(row, col++);
					int lvlReq = excelReadContext.getIntValue(row, col++);
					List<SBean.RankTitle> titles = new ArrayList<>();
					int titleLastRank = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int floor = excelReadContext.getIntValue(row, col++);
						int title = excelReadContext.getIntValue(row, col++);
						if(floor == 0)
							break;
						
						titles.add(new SBean.RankTitle(floor, title));
						titleLastRank = floor;
					}
					cfgs.add(new SBean.SectRankCFGS(new SBean.RankCFGS(id, length, refreshTimes, rankType, lvlReq, titles, titleLastRank)));
					
					++row;
					col = rowStart;
				}
				
				gdCfgs.sectRanks = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadFightSPTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;
			excelReadContext.ReadSheet(0);
			{
				int row = rowStart;
				int col = colStart;
				List<SBean.FightSPCFGS> cfgList = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart + 2;
					SBean.FightSPCFGS cfg = new SBean.FightSPCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					if (cfg.id <= 0 || cfg.id > gdCfgs.classRoles.size())
						throw new Exception("战斗能量表职业ID " + +cfg.id + " 没有对应的职业");

					cfg.conditions = excelReadContext.getIntegerList(row, col++, ";");
					cfg.odds = excelReadContext.getIntValue(row, col++);
					cfg.oddsSpirit = excelReadContext.getIntegerList(row, col++, ";");
					cfg.maxLays = excelReadContext.getIntValue(row, col++);
					cfg.duration = excelReadContext.getIntValue(row, col++);
					cfg.affectType = excelReadContext.getIntValue(row, col++);
					cfg.attrs = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						SBean.AttrListCFGS attr = new SBean.AttrListCFGS(0, new ArrayList<>());
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.values = excelReadContext.getIntegerList(row, col++, ";");
						if (attr.id > 0)
							cfg.attrs.add(attr);
					}

					col = colStart + 13;
					cfg.relatedSkill = excelReadContext.getIntValue(row, col++);
					cfg.reamlAddOdds = excelReadContext.getIntValue(row, col++);
					cfg.spiritID = excelReadContext.getIntValue(row, col++);

					if (fightSPIDMap.put(cfg.id, cfg) != null)
						throw new Exception("战斗能量表 职业ID " + cfg.id + " 重复");
					cfgList.add(cfg);
					row++;
				}
				gdCfgs.fightSP = cfgList;
			}

			excelReadContext.ReadSheet(1);
			{
				int row = rowStart;
				int col = colStart;
				List<SBean.BlurCFGS> cfgList = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart + 5;
					SBean.BlurCFGS cfg = new SBean.BlurCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					cfg.maxHPs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.atkNs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.defNs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.atrs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.ctrs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.acrNs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.tous = excelReadContext.getIntegerList(row, col++, ";");
					cfg.atkAs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.atkHs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.attacks = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						int sid = excelReadContext.getIntValue(row, col++);
						if (sid > 0)
						{
							this.checkSkillIDValid(gdCfgs, row, sid);
							cfg.attacks.add(sid);
						}
					}

					cfg.skills = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						int sid = excelReadContext.getIntValue(row, col++);
						if (sid > 0)
						{
							this.checkSkillIDValid(gdCfgs, row, sid);
							cfg.skills.add(sid);
						}
					}

					cfg.attackList = excelReadContext.getIntegerList(row, col++, ";");
					for (Integer seq : cfg.attackList)
					{
						if (seq > 0)
						{
							Integer sid = cfg.skills.get(seq - 1);
							if (sid == null || sid <= 0)
								throw new Exception("残影 " + cfg.id + " 攻击序列 " + seq + "  技能 = " + sid + " 错误");
						}
					}

					if (cfg.attackList.size() > 0)
					{
						for (Integer sid : cfg.attacks)
						{
							if (sid <= 0)
								throw new Exception("残影 " + cfg.id + " 普通攻击  " + sid + " 错误");
						}
					}

					cfg.speed = excelReadContext.getIntValue(row, col++);
					cfg.radius = excelReadContext.getIntValue(row, col++);
					cfg.checkRadius = excelReadContext.getIntValue(row, col++);
					cfg.survivalTime = excelReadContext.getIntValue(row, col++);
					col++;
					cfg.addMaxHpSpirit = excelReadContext.getIntValue(row, col++);
					cfg.addHps = excelReadContext.getIntegerList(row, col++, ";");

					if (blurIDMap.put(cfg.id, cfg) != null)
						throw new Exception("残影配置表 ID " + cfg.id + " 重复");
					cfgList.add(cfg);
					row++;
				}
				gdCfgs.blurs = cfgList;
			}

			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadInvalidStrTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			Set<String> cfgs = new HashSet<>();
			for (int i = 0; i < 3; ++i)
			{
				excelReadContext.ReadSheet(i);
				{
					final int colStart = 1;
					final int rowStart = 0;
					int row = rowStart;
					int col = colStart;

					while (excelReadContext.isNotEmpty(row, colStart))
					{
						String badstr = excelReadContext.getStringValue(row++, col);
						cfgs.add(badstr.toLowerCase());
					}

				}
			}
			gdCfgs.badstrs = cfgs;
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadDIYSkillTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;
			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				List<SBean.DIYSkillBaseCFGS> cfgList = new ArrayList<>();
				row = rowStart;
				col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.DIYSkillBaseCFGS cfg = new SBean.DIYSkillBaseCFGS();
					cfg.proficiencyLvl = excelReadContext.getIntValue(row, col++);
					cfg.needProficiency = excelReadContext.getIntValue(row, col++);
					cfg.dayUseTimes = excelReadContext.getIntValue(row, col++);
					cfg.totalSavvy = excelReadContext.getIntValue(row, col++);
					cfg.investLimits = new TreeMap<>();
					int value = 0;
					for (int i = 1; i <= 6; i++)
					{
						value = excelReadContext.getIntValue(row, col++);
						if (value < 0)
							throw new Exception("投入上限 " + i + " = " + value);
						cfg.investLimits.put(i, value);
					}
					cfg.haseChase = excelReadContext.getByteValue(row, col++);
					//checkBoolean(row, cfg.haseChase);
					cfg.chaseCount = excelReadContext.getIntValue(row, col++);
					cfg.chaseSelectWeight = excelReadContext.getIntValue(row, col++);
					col = 14;
					cfg.damageArg1 = new SBean.DamageCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++));
					cfg.damageArg2 = new SBean.DamageCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++));
					cfg.atrDecrease = new SBean.DamageCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++));
					cfg.acrDecrease = new SBean.DamageCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++));
					cfg.damages = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						cfg.damages.add(new SBean.DamageCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));

					}
					cfg.cd = new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++));
					cfg.addSP = new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++));

					cfg.scopes = new TreeMap<>();
					for (int i = 2; i <= 6; i++)
					{
						cfg.scopes.put(i, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					}

					cfg.buffOdds = new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++));
					// 控
					cfg.controlLoopTimes = new TreeMap<>();
					cfg.controlValues = new TreeMap<>();
					for (int i = Behavior.EBSTUN; i <= Behavior.EBSILENT; i++)
					{
						cfg.controlLoopTimes.put(i, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
						cfg.controlValues.put(i, new SBean.ParaCFGS());
					}
					cfg.controlLoopTimes.put(Behavior.EBFEAR, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					cfg.controlValues.put(Behavior.EBFEAR, new SBean.ParaCFGS());

					cfg.controlLoopTimes.put(BaseRole.EPROPID_SPEED, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					cfg.controlValues.put(BaseRole.EPROPID_SPEED, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));

					// 破
					cfg.breakLoopTimes = new TreeMap<>();
					cfg.breakValues = new TreeMap<>();
					for (int i = BaseRole.EPROPID_MAXHP; i <= BaseRole.EPROPID_DEFN; i++)
					{
						cfg.breakLoopTimes.put(i, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
						cfg.breakValues.put(i, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					}
					cfg.breakLoopTimes.put(BaseRole.EPROPID_HP, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					cfg.breakValues.put(BaseRole.EPROPID_HP, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));

					// 幻
					cfg.unrealLoopTimes = new TreeMap<>();
					cfg.unrealValues = new TreeMap<>();
					cfg.unrealLoopTimes.put(BaseRole.EPROPID_HP, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					cfg.unrealValues.put(BaseRole.EPROPID_HP, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));

					cfg.unrealLoopTimes.put(BaseRole.EPROPID_SPEED, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					cfg.unrealValues.put(BaseRole.EPROPID_SPEED, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					for (int i = BaseRole.EPROPID_MAXHP; i <= BaseRole.EPROPID_DEFN; i++)
					{
						cfg.unrealLoopTimes.put(i, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
						cfg.unrealValues.put(i, new SBean.ParaCFGS(excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++), excelReadContext.getFloatValue(row, col++)));
					}

					if (diySkillMap.put(cfg.proficiencyLvl, cfg) != null)
						throw new Exception("自创武功基础配置表有重复等级 " + cfg.proficiencyLvl);

					cfgList.add(cfg);
					if (cfgList.size() >= 2)
					{
						SBean.DIYSkillBaseCFGS d = cfgList.get(cfgList.size() - 2);
						if ((d.proficiencyLvl + 1) != cfg.proficiencyLvl)
							throw new Exception("自创武功基础配置表等级不连续 " + cfg.proficiencyLvl);
					}
					row++;
				}
				gdCfgs.diySkills = cfgList;
			}

			excelReadContext.ReadSheet(1);
			{
				row = rowStart;
				col = colStart;
				
				float total = 0.0f;
				List<SBean.DIYSkillGradeCFGS> cfgList = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = 4;
					SBean.DIYSkillGradeCFGS cfg = new SBean.DIYSkillGradeCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					cfg.addProficiency = excelReadContext.getIntValue(row, col++);
					cfg.createProb = excelReadContext.getFloatValue(row, col++);
					cfg.minFix = excelReadContext.getFloatValue(row, col++);
					cfg.maxFix = excelReadContext.getFloatValue(row, col++);

					if (diySkillGradeMap.put(cfg.id, cfg) != null)
						throw new Exception("自创武功评分表ID " + cfg.id + " 重复");
					cfgList.add(cfg);
					total += cfg.createProb;
					row++;
				}
				
				float sp = 0.0f;
				for (SBean.DIYSkillGradeCFGS e : cfgList)
				{
					sp += e.createProb / total;
					e.createProb = sp;
				}
				
				gdCfgs.diySkillGrades = cfgList;
			}

			excelReadContext.ReadSheet(2);
			{
				row = rowStart;
				col = colStart;
				List<SBean.DiySkillSlotUnblockCFGS> cfgList = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.DiySkillSlotUnblockCFGS cfg = new SBean.DiySkillSlotUnblockCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.isOpen = excelReadContext.getIntValue(row, col++);
					cfg.openCoin = excelReadContext.getIntValue(row, col++);

					cfgList.add(cfg);
					row++;
				}
				gdCfgs.diySkillSlotUnblock = cfgList;
			}

			// 独立字段
			excelReadContext.ReadSheet(5);
			{
				row = 1;
				col = 2;
				SBean.DIYSkillUniqueCFGS cfg = new SBean.DIYSkillUniqueCFGS();
				cfg.takeCount = excelReadContext.getIntValue(row++, col);
				cfg.saveDays = excelReadContext.getIntValue(row++, col);
				cfg.shareMax = excelReadContext.getIntValue(row++, col);
				cfg.shareAward = excelReadContext.getIntValue(row++, col);
				cfg.awardCount = excelReadContext.getIntValue(row++, col);
				cfg.shareSkillStandTime = excelReadContext.getIntValue(row++, col);
				row += 1;
				String iconString = excelReadContext.getStringValue(row++, col);
				String icons[] = iconString.split(";");
				cfg.icons = new ArrayList<>();
				for (String icon : icons)
				{
					cfg.icons.add(Integer.valueOf(icon));
				}

				row = 12;
				cfg.scores = new ArrayList<>();
				for (int i = 0; i < 3; i++)
				{
					cfg.scores.add(excelReadContext.getIntValue(row++, col));
				}

				float sum = 0;
				float weight = 0;
				cfg.scopeOdds = new TreeMap<>();
				for (int i = 2; i <= 6; i++)
				{
					weight = excelReadContext.getFloatValue(row++, col);
					cfg.scopeOdds.put(i, weight);
					sum += weight;
				}

				float odd = 0.f;
				for (Map.Entry<Integer, Float> e: cfg.scopeOdds.entrySet())
				{
					odd += e.getValue()/sum;
					e.setValue(odd);
				}
				
				cfg.minAngle = excelReadContext.getIntValue(row++, col);
				cfg.maxAngle = excelReadContext.getIntValue(row++, col);
				cfg.minWidth = excelReadContext.getIntValue(row++, col);
				cfg.maxWidth = excelReadContext.getIntValue(row++, col);
				cfg.chaseDefaultW = excelReadContext.getIntValue(row++, col);
				cfg.chaseSelectW = excelReadContext.getIntValue(row++, col);
				
				//自创武功开放等级，帮派最多分享数量
				row = excelReadContext.locateColumnTag(colStart, "自创武功开放等级");
				col = 2;
				cfg.openLevel = excelReadContext.getIntValue(row++, col);
				cfg.sectMaxShareCount = excelReadContext.getIntValue(row++, col);
				gdCfgs.diySkillUnique = cfg;
			}

			// 技能动作表
			excelReadContext.ReadSheet(9);
			{
				row = rowStart;
				List<SBean.DIYSkillActionCFGS> cfgList = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.DIYSkillActionCFGS cfg = new SBean.DIYSkillActionCFGS(0, 0, 0, new ArrayList<Integer>());
					cfg.id = excelReadContext.getIntValue(row, col++);
					int classType = excelReadContext.getIntValue(row, col++);
					cfg.scopeType = excelReadContext.getIntValue(row, col++);
					int scopeGrade = excelReadContext.getIntValue(row, col++);
					int randActionID = excelReadContext.getIntValue(row, col++);
					if (cfg.id != GameData.createDiySkillAuctionID(classType, cfg.scopeType, scopeGrade, randActionID))
						throw new Exception("自创武功技能动作表ID " + cfg.id + " 错误");

					col = 6;
					cfg.duration = excelReadContext.getIntValue(row, col++);
					for(int i=0; i<3; i++)
						cfg.trigTimes.add(excelReadContext.getIntValue(row, col++));
					
					if (diySkillActionMap.put(cfg.id, cfg) != null)
						throw new Exception("自创武功技能动作表ID " + cfg.id + " 重复");

					cfgList.add(cfg);
					row++;
				}
				gdCfgs.diySkillActions = cfgList;
			}

			// 追BUFF库
			excelReadContext.ReadSheet(7);
			{
				row = rowStart;
				List<SBean.DIYBUFFCFGS> cfgList = new ArrayList<>();
				Integer sum = 0;
				Map<Integer, Integer> sums = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = 2;
					SBean.DIYBUFFCFGS cfg = new SBean.DIYBUFFCFGS();
					cfg.libType = excelReadContext.getIntValue(row, colStart);
					cfg.weight = excelReadContext.getIntValue(row, col++);
					sum = sums.get(cfg.libType);
					if (sum == null)
					{
						sum = 0;
					}
					sum += cfg.weight;
					sums.put(cfg.libType, sum);
					cfg.buffID = excelReadContext.getIntValue(row, col++);

					cfgList.add(cfg);
					row++;
				}

				for (SBean.DIYBUFFCFGS cfg : cfgList)
				{
					sum = sums.get(cfg.libType);
					if (sum == null)
						throw new Exception("自创武功追BUFF库表 库类型 " + cfg.libType + " 读表错误");
					cfg.sum = sum;
				}

				gdCfgs.diybuffs = cfgList;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	public void loadVipsTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;
			excelReadContext.ReadSheet(0);
			{
				int row = rowStart;
				int col = colStart;
				List<SBean.VipCFGS> cfgList = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{

					SBean.VipCFGS cfg = new SBean.VipCFGS();
					cfg.level = excelReadContext.getIntValue(row, col++);
					cfg.points = excelReadContext.getIntValue(row, col++);
					cfg.rewards = new ArrayList<>();
					for (int i = 0; i < 4; ++i)
					{
						int iid = excelReadContext.getIntValue(row, col++);
						int icount = excelReadContext.getIntValue(row, col++);
						if (iid != 0)
						{
							this.checkEntityIDValid(gdCfgs, row, iid, false);
							cfg.rewards.add(new SBean.DummyGoods(iid, icount));
						}
					}
					col += 1;
					cfg.diyBuyCount = excelReadContext.getIntValue(row, col++);
					cfg.diyBuyUseDiamond = excelReadContext.getIntegerList(row, col++, ";");
					cfg.diyDayAddCount = excelReadContext.getIntValue(row, col++);
					cfg.dayMaxBuyCoinTimes = excelReadContext.getIntValue(row, col++);
					cfg.dayMaxBuyVitTimes = excelReadContext.getIntValue(row, col++);
					cfg.dayMaxBuyDopowerTimes = excelReadContext.getIntValue(row, col++);
					cfg.dayMaxBuyArenaFight = excelReadContext.getIntValue(row, col++);
					
					cfg.worship1 = excelReadContext.getIntValue(row, col++);
					cfg.worship2 = excelReadContext.getIntValue(row, col++);
					cfg.worship3 = excelReadContext.getIntValue(row, col++);

					cfg.dayMaxBuyNormalMapCopyEnterTimes = excelReadContext.getIntValue(row, col++);
					cfg.dayMaxBuyActivityMapGroupEnterTimes = excelReadContext.getIntValue(row, col++);

					cfg.auctionMaxCells = excelReadContext.getIntValue(row, col++);
					cfg.dayClanOccupyOreMaxTimes = excelReadContext.getIntValue(row, col++);
					cfg.luckyWheelFreeDrawTiems = excelReadContext.getIntValue(row, col++);
					cfg.bwarenaDayBuyTiems = excelReadContext.getIntValue(row, col++);
					cfg.title = excelReadContext.getIntValue(row, col++);
					if(cfg.title > 0)
						checkTitleIDValid(gdCfgs, row, cfg.title);
					cfg.canGetMissVit = excelReadContext.getByteValue(row, col++);
					cfg.activityLastTimes = excelReadContext.getIntegerList(row, col++, ",");
					if (cfg.activityLastTimes.size() != gdCfgs.actMapGroups.size())
						throw new Exception("row " + row + " 活动补做次数列表大小 " + cfg.activityLastTimes.size() + " 与活动本不对应！");
					col+=2;
					cfg.vipRewardPrice = excelReadContext.getIntValue(row, col++);
					cfg.freeTransfrom = excelReadContext.getByteValue(row, col++);
					cfg.taxReduction = excelReadContext.getIntValue(row, col++);
					if (gdCfgs.common.auction.taxRate < cfg.taxReduction / 10000f)
						throw new Exception("row " + row + " 交易税减免 " + cfg.taxReduction + " 超过了总交易税！");
					cfg.reviveReduction = excelReadContext.getIntValue(row, col++);
					if (cfg.taxReduction / 10000f > 1)
						throw new Exception("row " + row + " 复活价格 " + cfg.taxReduction + " 超过了100%！");
					cfg.sectTaskRewardRate = excelReadContext.getFloatValue(row, col++);
					cfg.teamMapDropTimeRate = excelReadContext.getFloatValue(row, col++);
					//综合和帮派频道发言是否有特效
					col++;
					
					String cashBackString = excelReadContext.getStringValue(row, col++);
					try 
					{
					    cfg.PBTCashBackRewards = getRewardsListByString(cashBackString, gdCfgs, row);
					}
					catch (Exception e)
					{
					    throw new Exception(e.getMessage() + " at row " + row + " col " + col);
					}
					 
					cfgList.add(cfg);
					row++;
					col = colStart;
				}
				gdCfgs.vips = cfgList;
			}

			System.out.println("load table " + fileName + " success.");
		}
	}
	
	private List<SBean.DummyGoods> getRewardsListByString(String rewardString, SBean.GameDataCFGS gdCfgs, int row) throws Exception
	{
		String[] cashBacks = rewardString.trim().split(",");
		List<SBean.DummyGoods> cashBackRewards = new ArrayList<>();
		for (String cashBack : cashBacks)
		{
		    if("".equals(cashBack.trim()))
		    {
		        continue;
		    }
		    
		    String[] reward = cashBack.split("_");
		    if (reward.length!=2)
		    {
		        throw new Exception(" reward format error ");
		    }
		    
		    int itemId = Integer.valueOf(reward[0].trim());
		    int itemCount = Integer.valueOf(reward[1].trim());
		    
			if (itemId != 0)
			{
				this.checkEntityIDValid(gdCfgs, row, itemId, true);
			}
		    
			if (itemId != 0 && itemCount > 0)
			    cashBackRewards.add(new SBean.DummyGoods(itemId, itemCount));
		}
		
		return cashBackRewards;
	}
	
	public void loadChannelPaysTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			Set<Integer> channels = new TreeSet<>();
			excelReadContext.ReadSheet(0);
			{
				gdCfgs.payId = excelReadContext.getIntValue(1, 1);
			}
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				Map<String, SBean.ChannelCFGS> cfgs = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int id = excelReadContext.getIntValue(row, col++);
					String name = excelReadContext.getStringValue(row, col++);
					int payId = excelReadContext.getIntValue(row, col++);
					
					if (name.isEmpty())
						throw new Exception("渠道表中 id " + id + " 渠道名字为空字符串！");
					if (!channels.add(id))
						throw new Exception("渠道表中 id " + id + " 重复！");
					if (cfgs.put(name, new SBean.ChannelCFGS(id, name, payId)) != null)
						throw new Exception("渠道表中 name " + name + " 重复！");
					
					row++;
					col = colStart;
				}
				gdCfgs.channels = cfgs;
			}
			excelReadContext.ReadSheet(2);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.PayCFGS> cfgs = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					int payId = excelReadContext.getIntValue(row, col++);
					SBean.PayLevelCFGS cfg = new SBean.PayLevelCFGS();
					cfg.level = excelReadContext.getIntValue(row, col++);
					cfg.id = excelReadContext.getStringValue(row, col++);
					col += 1;
					cfg.buyNum = excelReadContext.getIntValue(row, col++);
					col += 2;
					cfg.worth = excelReadContext.getIntValue(row, col++);
					cfg.money = excelReadContext.getIntValue(row, col++);
					cfg.points = excelReadContext.getIntValue(row, col++);
					cfg.diamond = excelReadContext.getIntValue(row, col++);
					cfg.credit = excelReadContext.getIntValue(row, col++);
					cfg.rebates = excelReadContext.getIntegerList(row, col++, ";");
					col += 1;
					cfg.price = excelReadContext.getFloatValue(row, col++);
					col += 3;
					cfg.type = excelReadContext.getIntValue(row, col++);
					cfg.param = excelReadContext.getIntValue(row, col++);

					SBean.PayCFGS payCfg = cfgs.get(payId);
					if (payCfg == null)
					{
						payCfg = new SBean.PayCFGS(payId, new TreeMap<>());
						cfgs.put(payCfg.id, payCfg);
					}
					
					if(cfg.type == GameData.GAME_PAY_GOODS_TYPE_CARD)
						pay2SpecialCard.compute(payCfg.id, (k,v) -> v == null ? 1 : v + 1);
					
					if (payCfg.payLevels.put(cfg.level, cfg) != null)
						throw new Exception("充值表中 充值配置 " + payId + " 的充值档位 " + cfg.level + " 重复！");
					
					row++;
					col = colStart;
				}
				gdCfgs.pays = cfgs;
			}
			//check
			{
				if (!gdCfgs.pays.containsKey(gdCfgs.payId))
					throw new Exception("服务器使用的充值表  " + gdCfgs.payId + " 不存在！");
				for (SBean.ChannelCFGS e : gdCfgs.channels.values())
				{
					if (!gdCfgs.pays.containsKey(e.payId))
						throw new Exception("渠道表中 渠道 " + e.name + " 充值配置  " + e.payId + " 不存在！");
				}
			}

			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadVipCardsTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				int row = 1;
				int col = 0;
				List<SBean.SpecialCardCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, 0))
				{
					int id = excelReadContext.getIntValue(row, col++);
					col++;
					
					List<SBean.AttrCFGS> attrs = new ArrayList<>();
					for(int i = 0; i < 2; i++)
					{
						int attrID = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						if(attrID > 0 && value != 0)
						{
							checkPropertyIDValid(gdCfgs, row, attrID);
							attrs.add(new SBean.AttrCFGS(attrID, value));
						}
					}
					
					List<SBean.DummyGoods> dayReward = new ArrayList<>();
					for(int i = 0; i < 3; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if(itemID != 0 && count > 0)
						{
							checkEntityIDValid(gdCfgs, row, itemID, false);
							dayReward.add(new SBean.DummyGoods(itemID, count));
						}
					}
					
					int lastTime = excelReadContext.getIntValue(row, col++) * 86400;			//转换成秒
					int offlineAddtion = excelReadContext.getIntValue(row, col++) * 60;			//转换成分
					float takeVitAddtion = excelReadContext.getIntValue(row, col++) / 10000.f;	//万分比
					cfgs.add(new SBean.SpecialCardCFGS(attrs, dayReward, lastTime, offlineAddtion, takeVitAddtion));
					
					if(cfgs.size() != id)
						throw new Exception("row " + row + " 特权卡 ID " + id + " 不连续!");
					
					row++;
					col = 0;
				}
				
				for(Map.Entry<Integer, Integer> e: pay2SpecialCard.entrySet())
				{
					if(e.getValue() > cfgs.size())
						throw new Exception("充值配置ID " + e.getKey() + " 配置的特权卡数量大于 实际特权卡数量 " + cfgs.size());
				}
				
				gdCfgs.specialCards = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadArenaTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int colStart = 0;
			final int rowStart = 2;
			int col = colStart;
			int row = rowStart;
			excelReadContext.ReadSheet(0);
			{
				final int rowArena = excelReadContext.locateColumnTag(colStart, "竞技场");
				row = rowArena + 1;
				SBean.ArenaCFGS arenaCfg = new SBean.ArenaCFGS();
				arenaCfg.refreshTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, 2));
				arenaCfg.lvlReq = excelReadContext.getIntValue(row, 5);
				arenaCfg.freeTimes = excelReadContext.getIntValue(row, 8);
				arenaCfg.stoneRefresh = excelReadContext.getIntValue(row, 11);
				arenaCfg.timesPrice = excelReadContext.getIntegerList(row, 15, ";");

				row = rowArena + 3;
				arenaCfg.rankMax = excelReadContext.getIntValue(row, 2);
				arenaCfg.coolTime = excelReadContext.getIntValue(row, 5);

				row = rowArena + 5;
				arenaCfg.arenaMapID = excelReadContext.getIntValue(row, 2);
				arenaCfg.arenaMaxTime = excelReadContext.getIntValue(row, 5);
				arenaCfg.arenaAutoCloseTime = excelReadContext.getIntValue(row, 8);
				arenaCfg.prepareTime = excelReadContext.getIntValue(row, 11);
				
				row = rowArena + 7;
				arenaCfg.winAddScore = excelReadContext.getIntValue(row, 3);
				arenaCfg.loseAddScore = excelReadContext.getIntValue(row, 5);
				row++;
				arenaCfg.hideDefenceVipReq = excelReadContext.getIntValue(row, 2);
				
				final int rowTarget = excelReadContext.locateColumnTag(colStart, "可见对手");
				row = rowTarget + 2;
				col = 1;
				arenaCfg.targets = new ArrayList<>();
				int rankFloor = 0;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ArenaTargetCFGS target = new SBean.ArenaTargetCFGS();
					rankFloor = excelReadContext.getIntValue(row, col++);
					target.rankFloor = rankFloor;
					target.deltaRank = new ArrayList<>();
					int lastDeltaMax = -target.rankFloor;
					for (int i = 0; i < 4; i++)
					{
						int deltaMin = excelReadContext.getIntValue(row, col++);
						int deltaMax = excelReadContext.getIntValue(row, col++);
						if (deltaMin > deltaMax)
							throw new Exception("row " + row + " 个人竞技场可见对手表中 对手 " + (i+1) + " 名次差 下限比上限大！");
						if (deltaMin <= lastDeltaMax)
							throw new Exception("row " + row + " 个人竞技场可见对手表中 对手 " + (i+1) + " 名次差 下限比前一个对手名次差上限大， 名次重叠！");
						target.deltaRank.add(new SBean.ArenaTargetRankDeltaCFGS(deltaMin, deltaMax));
						lastDeltaMax = deltaMax;
					}
					
					target.seeMax = excelReadContext.getIntValue(row, col++);
					if(arenaCfg.targets.size() > 0)
					{
						SBean.ArenaTargetCFGS preTarget = arenaCfg.targets.get(arenaCfg.targets.size() - 1);
						int maxTarget = target.deltaRank.get(0).deltaMin + preTarget.rankFloor + 1;
						if(maxTarget < target.seeMax)
							throw new Exception("row " + row + " 当前名次档次 " + target.rankFloor + " 对手1最高排名 " + maxTarget + " < 最高可见排名 " + target.seeMax); 
					}
					arenaCfg.targets.add(target);
					
					col = 1;
					row++;
				}
				if (rankFloor != arenaCfg.rankMax)
					throw new Exception("个人竞技场排名数量 " + rankFloor + " 必须和可见对手最后一列 " + arenaCfg.rankMax + " 一致");

				final int rowRankReward = excelReadContext.locateColumnTag(colStart, "排名奖励");
				row = rowRankReward + 2;
				col = 1;
				arenaCfg.rankRewards = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ArenaRewardCFGS reward = new SBean.ArenaRewardCFGS();
					reward.floor = excelReadContext.getIntValue(row, col++);
					reward.money = excelReadContext.getIntValue(row, col++);
					reward.point = excelReadContext.getIntValue(row, col++);
					reward.stone = excelReadContext.getIntValue(row, col++);
					reward.items = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						SBean.DummyGoods item = new SBean.DummyGoods();
						item.id = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, item.id, true);
						item.count = excelReadContext.getIntValue(row, col++);
						if (item.id != 0 && item.count > 0)
							reward.items.add(item);
					}

					arenaCfg.rankRewards.add(reward);
					col = 1;
					row ++;
				}

				final int rowScoreReward = excelReadContext.locateColumnTag(colStart, "积分奖励");
				row = rowScoreReward + 2;
				col = 1;
				arenaCfg.scoreRewards = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ArenaRewardCFGS reward = new SBean.ArenaRewardCFGS();
					reward.floor = excelReadContext.getIntValue(row, col++);
					reward.money = excelReadContext.getIntValue(row, col++);
					reward.point = excelReadContext.getIntValue(row, col++);
					reward.stone = excelReadContext.getIntValue(row, col++);
					reward.items = new ArrayList<>();
					for (int i = 0; i < 1; i++)
					{
						SBean.DummyGoods item = new SBean.DummyGoods();
						item.id = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, item.id, true);
						item.count = excelReadContext.getIntValue(row, col++);
						if (item.id != 0 && item.count > 0)
							reward.items.add(item);
					}

					arenaCfg.scoreRewards.put(reward.floor, reward);
					col = 1;
					row++;
				}

				final int rowBestRank = excelReadContext.locateColumnTag(colStart, "历史最高奖励");
				row = rowBestRank + 2;
				col = 1;
				arenaCfg.bestRankRewards = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ArenaBestRankRewardCFGS bestReward = new SBean.ArenaBestRankRewardCFGS();
					bestReward.rankFloor = excelReadContext.getIntValue(row, col++);
					bestReward.money = excelReadContext.getIntValue(row, col++);
					bestReward.stone = excelReadContext.getIntValue(row, col++);

					arenaCfg.bestRankRewards.add(bestReward);
					col = 1;
					row++;
				}

				gdCfgs.arena = arenaCfg;
			}

			excelReadContext.ReadSheet(1);
			{
				Map<Integer, SBean.ArenaRobotCFGS> cfgRobots = new TreeMap<>();
				Map<Integer, SBean.ArenaRobotGroupCFGS> cfgGroups = new TreeMap<>();
				row = 5;
				col = colStart;
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ArenaRobotCFGS cfg = new SBean.ArenaRobotCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.rankMax = excelReadContext.getIntValue(row, col++);
					cfg.rankMin = excelReadContext.getIntValue(row, col++);
					cfg.lvl = excelReadContext.getIntValue(row, col++);
					cfg.classType = excelReadContext.getByteValue(row, col++);
					checkClassTypeValid(gdCfgs, row, cfg.classType);
					cfg.transformLevel = excelReadContext.getByteValue(row, col++);

					cfg.attrs = new HashMap<>();
					// int
					for (int i = BaseRole.EPROPID_MAXHP; i <= BaseRole.EPROPID_TOU; i++)
						cfg.attrs.put(i, excelReadContext.getIntValue(row, col++));

					// float
					for (int i = BaseRole.EPROPID_ATKA; i <= BaseRole.EPROPID_DEFA; i++)
						cfg.attrs.put(i, excelReadContext.getIntValue(row, col++));

					// int
					for (int i = BaseRole.EPROPID_ATKH; i <= BaseRole.EPROPID_MASTERW; i++)
						cfg.attrs.put(i, excelReadContext.getIntValue(row, col++));

					cfg.attrs.put(BaseRole.EPROPID_HEALA, excelReadContext.getIntValue(row, col++));

					cfg.skills = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						SBean.SkillBriefCFGS skill = new SBean.SkillBriefCFGS();
						skill.id = excelReadContext.getIntValue(row, col++);
						skill.lvl = excelReadContext.getIntValue(row, col++);
						if (skill.id > 0)
						{
							this.checkSkillIDValid(gdCfgs, row, skill.id);
							cfg.skills.add(skill);
						}
					}

					cfg.pets = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						SBean.PetBriefCFGS pet = new SBean.PetBriefCFGS();
						pet.id = excelReadContext.getIntValue(row, col++);
						pet.lvl = excelReadContext.getIntValue(row, col++);
						if (pet.id > 0)
							cfg.pets.add(pet);
					}
					cfg.power = excelReadContext.getIntValue(row, col++);
					
					if (arenaRobotIDMap.put(cfg.id, cfg) != null)
						throw new Exception("个人竞技场机器人配置表 ID " + cfg.id + " 重复");

					cfgRobots.put(cfg.id, cfg);
					SBean.ArenaRobotGroupCFGS group = cfgGroups.get(cfg.rankMin);
					if(group == null)
					{
						group = new SBean.ArenaRobotGroupCFGS(cfg.rankMin, cfg.rankMax, new ArrayList<>());
						cfgGroups.put(cfg.rankMin, group);
					}
					
					if(group.rankMax != cfg.rankMax)
						throw new Exception("个人竞技场机器人配置表 ID " + cfg.id + " 排名区间上限 " + cfg.rankMax + " 非法！");
						
					group.robots.add(cfg);
					
					row++;
				}
				gdCfgs.arenaRobots = cfgRobots;
				gdCfgs.arenaRobotGroup = cfgGroups;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadSuperArenaTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 0;
				final int colStart = 0;
				int row = rowStart + 1;
				int col = colStart;
				
				Map<Integer, SBean.SuperArenaTypeCFGS> cfgMaps = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SuperArenaTypeCFGS cfg = new SBean.SuperArenaTypeCFGS(0, 
																				0, 
																				new HashSet<>(), 
																				new ArrayList<>(),
																				0,
																				0, 
																				0, 
																				new ArrayList<>(), 
																				0, 
																				0, 
																				0, 
																				0,
																				0,
																				0);
					
					cfg.type = excelReadContext.getIntValue(row, colStart);
					
					switch (cfg.type)
					{
					case GameData.MAPCOPY_SUPERARENA_NORMAL:
					case GameData.MAPCOPY_SUPERARENA_THREEBEST:
						break;
					default:
						throw new Exception("row " + row + " 会武类型 " + cfg.type + " 非法");
					}
					
					col = colStart + 2;
					cfg.mapID = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mapClusterCfg = gdCfgs.mapClusters.get(cfg.mapID);
					if(mapClusterCfg == null)
						throw new Exception("会武类型 " + cfg.type + " 地图ID" + cfg.mapID + " 在地图总表中不存在！");
					
					SBean.SuperArenaMapCFGS mapCfg = gdCfgs.superArenaMaps.get(cfg.mapID);
					if(mapCfg == null || mapCfg.type != cfg.type)
						throw new Exception("会武类型 " + cfg.type + " 非法！");
					
					List<Integer> list = excelReadContext.getIntegerList(row, col++, ";");
					for(int day: list)
					{
						if(!cfg.openDays.add(day))
							throw new Exception("会武类型 " + cfg.type + " 开放日 " + day + " 重复！");
					}
					
					List<String> startTimes = excelReadContext.getStringList(row, col++, ";");
					cfg.lastTime = excelReadContext.getIntValue(row, col++);
					
					int lastEndTime = 0;
					for(int i=0; i<startTimes.size(); i++)
					{
						int startTime = GameTime.parseSecondOfDay(startTimes.get(i));
						if(startTime < GameData.GAME_DAY_REFRESH_TIME * 3600)
							throw new Exception("会武开启时间 " + startTime + " 非法！");
						
						int endTime = startTime + cfg.lastTime;
						if(startTime >= endTime || (lastEndTime > 0 && lastEndTime >= startTime))
							throw new Exception("会武类型 " + cfg.type + " 开放时间起点和开放时间终点非法！");
						
						lastEndTime = endTime;
						cfg.startTimes.add(startTime);
					}
					
					if(cfg.startTimes.size() > 1)
					{
						if(cfg.lastTime + (cfg.startTimes.get(cfg.startTimes.size() - 1) - GameData.GAME_DAY_REFRESH_TIME * 3600) > GameTime.getDayTimeSpan())
							throw new Exception("会武持续时间 " + cfg.lastTime + " 超过一天！");
					}
					
					cfg.needLvl = excelReadContext.getIntValue(row, col++);
					{
						SBean.RankCFGS sahCfg = gdCfgs.roleRanks.get(GameData.RANK_TYPE_SUPER_ARENA_HISTORY -1).rank;
						if(sahCfg.lvlReq > cfg.needLvl)
							throw new Exception("会武历史荣誉排行榜的可见等级 " + sahCfg.lvlReq + " 大于会武开放等级 " + cfg.needLvl);
						SBean.RankCFGS sawCfg = gdCfgs.roleRanks.get(GameData.RANK_TYPE_SUPER_ARENA_WEEK -1).rank;
						if(sawCfg.lvlReq > cfg.needLvl)
							throw new Exception("会武周荣誉排行榜的可见等级 " + sawCfg.lvlReq + " 大于会武开放等级 " + cfg.needLvl);
					}
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					cfg.honorPercent = excelReadContext.getIntegerList(row, col++, ";");
					cfg.winHonor = excelReadContext.getIntValue(row, col++);
					cfg.loseHonor = excelReadContext.getIntValue(row, col++);
					cfg.hangWinHonor = excelReadContext.getIntValue(row, col++);
					cfg.hangLoseHonor = excelReadContext.getIntValue(row, col++);
					
					col = 15;
					cfg.members = excelReadContext.getIntValue(row, col++);
					cfg.races = excelReadContext.getIntValue(row, col++);
					if(cfgMaps.put(cfg.type, cfg) != null)
						throw new Exception("会武类型 " + cfg.type + " 重复！");
					
					row++;
				}
				gdCfgs.superarenaTypes = cfgMaps;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 0;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				SBean.SuperArenaCFGS cfg = new SBean.SuperArenaCFGS(0, 
																	0, 
																	0,
																	new ArrayList<>(),
																	new ArrayList<>(),
																	new ArrayList<>(),
																	new ArrayList<>(),
																	0,
																	0,
																	new SBean.SuperArenaBuffCFGS(0, new ArrayList<>()),
																	(short)0,
																	(short)0,
																	(short)0,
																	0,
																	0,
																	0,
																	0,
																	new SBean.SuperArenaNormalCFGS(0, 0, 0, new ArrayList<>(), new ArrayList<>(), 0, 0, 0, 0));
				
				final int rowMatch = excelReadContext.locateColumnTag(colStart, "会武匹配规则");
				row = rowMatch + 1;
				col = colStart + 2;
				cfg.maxRoleMatch = excelReadContext.getIntValue(row++, col);
				cfg.maxTeamMatch = excelReadContext.getIntValue(row++, col);
				cfg.maxMatchTime = excelReadContext.getIntValue(row++, col);
				
				final int rowGrade4v4 = excelReadContext.locateColumnTag(colStart, "4v4等级段划分");
				row = rowGrade4v4 + 2;
				while(excelReadContext.isNotEmpty(row, colStart + 1))
				{
					col = colStart + 1;
					SBean.GradeCFGS grade = new SBean.GradeCFGS(0, 0);
					grade.min = excelReadContext.getIntValue(row, col++);
					grade.max = excelReadContext.getIntValue(row, col++);
					cfg.grades4v4.add(grade);
					row++;
				}
				
				final int rowELO4v4 = excelReadContext.locateColumnTag(colStart, "4v4ELO段划分");
				{
					row = rowELO4v4 + 2;
					col = 1;
					int lastCeiling = 0;
					while(excelReadContext.isNotEmpty(row, 1))
					{
						int ceiling = excelReadContext.getIntValue(row, col++);
						if(ceiling <= lastCeiling && ceiling > 0)
							throw new Exception("row " + row + " 4v4ELO段划分 ELO上限 " + ceiling + " 不是递增的!");
						
						if(ceiling > 0)
							lastCeiling = ceiling;
						
						int degradeTime = excelReadContext.getIntValue(row, col);
						cfg.elos4v4.add(new SBean.ELOGradeCFG(ceiling, degradeTime));
						
						row++;
						col = 1;
					}
					
					SBean.ELOGradeCFG first = cfg.elos4v4.get(0);
					if(first.degradeTime >= 0)
						throw new Exception("4v4ELO段 第一个分段的划入低分组时间 必须为-1");
				}
				
				final int rowGrade2v2 = excelReadContext.locateColumnTag(colStart, "会武2v2等级段划分");
				row = rowGrade2v2 + 2;
				while(excelReadContext.isNotEmpty(row, colStart + 1))
				{
					col = colStart + 1;
					SBean.GradeCFGS grade = new SBean.GradeCFGS(0, 0);
					grade.min = excelReadContext.getIntValue(row, col++);
					grade.max = excelReadContext.getIntValue(row, col++);
					cfg.grades2v2.add(grade);
					row++;
				}
				
				final int rowELO2v2 = excelReadContext.locateColumnTag(colStart, "2v2ELO段划分");
				{
					row = rowELO2v2 + 2;
					col = 1;
					int lastCeiling = 0;
					while(excelReadContext.isNotEmpty(row, 1))
					{
						int ceiling = excelReadContext.getIntValue(row, col++);
						if(ceiling <= lastCeiling && ceiling > 0)
							throw new Exception("row " + row + " 2v2ELO段划分 ELO上限 " + ceiling + " 不是递增的!");
						
						if(ceiling > 0)
							lastCeiling = ceiling;
						int degradeTime = excelReadContext.getIntValue(row, col);
						cfg.elos2v2.add(new SBean.ELOGradeCFG(ceiling, degradeTime));
						
						row++;
						col = 1;
					}
					
					SBean.ELOGradeCFG first = cfg.elos2v2.get(0);
					if(first.degradeTime >= 0)
						throw new Exception("2v2ELO段 第一个分段的划入低分组时间 必须为-1");
				}
				
				
				final int rowCommon = excelReadContext.locateColumnTag(colStart, "会武通用配置") ;
				row = rowCommon + 1;
				col = colStart + 2;
				cfg.needLvl = excelReadContext.getIntValue(row++, col);
				cfg.openDay = excelReadContext.getIntValue(row++, col);
				cfg.buff.baseBuff = excelReadContext.getIntValue(row++, col);
				checkBuffIDValid(gdCfgs, row, cfg.buff.baseBuff);
				cfg.buff.failedBuffs = excelReadContext.getIntegerList(row++, col, ";");
				for(int buffID: cfg.buff.failedBuffs)
					checkBuffIDValid(gdCfgs, row, buffID);

				int orgELO = excelReadContext.getIntValue(row++, col);
				int minELO = excelReadContext.getIntValue(row++, col);
				if(minELO < -GameData.SUPER_ARENA_ELO_LIMIT)
					throw new Exception("row " + row + " ELO积分下限 " + minELO + " 非法!");
				int maxELO = excelReadContext.getIntValue(row++, col);
				if(maxELO > GameData.SUPER_ARENA_ELO_LIMIT)
					throw new Exception("row " + row + " ELO积分上限 " + maxELO + " 非法!");
				
				if(orgELO < minELO || orgELO > maxELO)
					throw new Exception("ELO初始积分 " + cfg.orgELO + " 不在  [" + minELO + " " + maxELO + "] 之内");
				
				cfg.orgELO = (short)orgELO;
				cfg.minELO = (short)minELO;
				cfg.maxELO = (short)maxELO;
				
				
				cfg.eloParamK = excelReadContext.getIntValue(row++, col);
				cfg.eloParamWin = excelReadContext.getFloatValue(row++, col);
				cfg.eloParamNone = excelReadContext.getFloatValue(row++, col);
				if(cfg.eloParamNone > cfg.eloParamWin)
					throw new Exception("row " + row + " 平局ELO参数W " + cfg.eloParamNone + " 大于胜利ELO参数W " + cfg.eloParamWin);
				cfg.eloParamLose = excelReadContext.getFloatValue(row++, col);
				if(cfg.eloParamLose > cfg.eloParamNone)
					throw new Exception("row " + row + " 失败ELO参数W " + cfg.eloParamLose + " 大于平局ELO参数W " + cfg.eloParamNone);
				
				final int rowNormal = excelReadContext.locateColumnTag(colStart, "4v4竞技场配置表") ;
				row = rowNormal + 1;
				col = colStart + 2;
				cfg.normal.prepareTime = excelReadContext.getIntValue(row++, col);
				cfg.normal.autoCloseTime = excelReadContext.getIntValue(row++, col);
				cfg.normal.reviveTimes = excelReadContext.getIntValue(row++, col);
				cfg.normal.killHonors = excelReadContext.getIntegerList(row++, col, ";");
				cfg.normal.otherKillHonors = excelReadContext.getIntegerList(row++, col, ";");
				cfg.normal.addHonorRange = excelReadContext.getIntValue(row++, col);
				cfg.normal.firstBloodHonor = excelReadContext.getIntValue(row++, col);
				cfg.normal.mostKillHonor = excelReadContext.getIntValue(row++, col);
				
				row = rowNormal + 12;
				cfg.normal.autoRevive = excelReadContext.getIntValue(row++, col);
				
				gdCfgs.superarena = cfg;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadBWArenaTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 0;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				SBean.BWArenaCFGS cfg = new SBean.BWArenaCFGS();
				cfg.base = new SBean.BWArenaBaseCFGS();
				final int rowBase = excelReadContext.locateColumnTag(colStart, "基本设定");
				row  = rowBase + 1;
				col = colStart + 2;
				cfg.base.lvlReq = excelReadContext.getIntValue(row++, col);
				cfg.base.startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col));
				cfg.base.endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col + 1));
				cfg.base.rankRefreshTime = new ArrayList<>();
				for(int i=0; i<3; i++)
				{
					cfg.base.rankRefreshTime.add(GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col + i)));
				}
				row++;
				
				cfg.base.mailRewardTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
				cfg.base.dayEnterTimes = excelReadContext.getIntValue(row++, col);
				cfg.base.diamondBuys = excelReadContext.getIntegerList(row++, col, ";");
				cfg.base.diamondRefresh = excelReadContext.getIntValue(row++, col);
				cfg.base.rewardScoreReq = excelReadContext.getIntValue(row++, col);
				cfg.base.winExp = new ArrayList<>(); 
				cfg.base.winExp.add(excelReadContext.getIntValue(row++, col));
				cfg.base.winExp.add(excelReadContext.getIntValue(row++, col));
				cfg.base.loseExp = new ArrayList<>();
				cfg.base.loseExp.add(excelReadContext.getIntValue(row++, col));
				cfg.base.loseExp.add(excelReadContext.getIntValue(row++, col));
				cfg.base.winScore = new ArrayList<>();
				cfg.base.winScore.add(excelReadContext.getIntValue(row++, col));
				cfg.base.winScore.add(excelReadContext.getIntValue(row++, col));
				cfg.base.loseScore = new ArrayList<>();
				cfg.base.loseScore.add(excelReadContext.getIntValue(row++, col));
				cfg.base.loseScore.add(excelReadContext.getIntValue(row++, col));
				cfg.base.mailRewardLvlReq = excelReadContext.getIntValue(row++, col);
				
				cfg.fight = new SBean.BWArenaFightCFGS();
				final int rowFight = excelReadContext.locateColumnTag(colStart, "战斗相关");
				row = rowFight + 1;
				col = colStart + 2;
				cfg.fight.mapID = excelReadContext.getIntValue(row++, col);
				cfg.fight.maxTime = excelReadContext.getIntValue(row++, col);
				cfg.fight.autoCloseTime = excelReadContext.getIntValue(row++, col);
				cfg.fight.prepareTime = excelReadContext.getIntValue(row++, col);
				cfg.fight.damageDecrease = excelReadContext.getIntValue(row++, col);
				
				cfg.lvls = new ArrayList<>();
				final int rowLvl = excelReadContext.locateColumnTag(colStart, "等级相关参数");
				row = rowLvl + 2;
				while(excelReadContext.isNotEmpty(row, colStart + 1))
				{
					col = colStart + 2;
					SBean.BWArenaLvlCFGS lvlCfg = new SBean.BWArenaLvlCFGS();
					lvlCfg.petCount = excelReadContext.getIntValue(row, col++);
					lvlCfg.expReq = excelReadContext.getIntValue(row, col++);
					
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemID, false);
						if(count <= 0)
							throw new Exception("正派积分奖励道具数量 " + count + " invalid");
						lvlCfg.whiteScoreReward = new SBean.DummyGoods(itemID, count);
					}
					
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemID, false);
						if(count <= 0)
							throw new Exception("邪派积分奖励道具数量 " + count + " invalid");
						lvlCfg.blackScoreReward = new SBean.DummyGoods(itemID, count);
					}
					
					cfg.lvls.add(lvlCfg);
					row++;
				}
				
				cfg.ranks = new ArrayList<>();
				final int rowRank = excelReadContext.locateColumnTag(colStart, "排行发奖相关");
				row = rowRank + 2;
				col = colStart + 2;
				
				while(excelReadContext.isNotEmpty(row, colStart + 1))
				{
					col = colStart + 2;
					SBean.BWArenaRankCFGS rankCfg = new SBean.BWArenaRankCFGS();
					rankCfg.whiteRankReward = new ArrayList<>();
					for(int i=0; i<4; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemID, true);
						if(count > 0)
							rankCfg.whiteRankReward.add(new SBean.DummyGoods(itemID, count));
					}
					
					rankCfg.blackRankReward = new ArrayList<>();
					for(int i=0; i<4; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemID, true);
						if(count > 0)
							rankCfg.blackRankReward.add(new SBean.DummyGoods(itemID, count));
					}
					
					rankCfg.roleWhiteTitle = excelReadContext.getIntValue(row, col++);
					rankCfg.roleBlackTitle = excelReadContext.getIntValue(row, col++);
					if(rankCfg.roleWhiteTitle > 0)
						checkTitleIDValid(gdCfgs, row, rankCfg.roleWhiteTitle);
					
					if(rankCfg.roleBlackTitle > 0)
						checkTitleIDValid(gdCfgs, row, rankCfg.roleBlackTitle);
							
					cfg.ranks.add(rankCfg);
					row++;
				}
				gdCfgs.bwarena = cfg;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadForceWarTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 0;
				final int colStart = 0;
				int row = rowStart + 1;
				int col = colStart;
				Map<Integer, SBean.ForceWarCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.ForceWarCFGS cfg = new SBean.ForceWarCFGS(0, new ArrayList<>(), new HashSet<>(), 0, 0, 0, 0, new ArrayList<>());
					cfg.type = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 2;
					cfg.mapIDs = excelReadContext.getIntegerList(row, col++, ";");
					for(int mapID: cfg.mapIDs)
					{
						SBean.ForceWarMapCFGS fw = gdCfgs.forcewarMaps.get(mapID);
						if(fw == null)
							throw new Exception("row " + row + " 势力战类型 " + cfg.type + " 包含的map" + mapID + " 在副本配置表不存在!");
						
						if(fw.type != cfg.type)
							throw new Exception("row " + row + " 势力战类型 " + cfg.type + " 包含的map" + mapID + " 与副本配置表对应的类型 " + fw.type + " 不一致!");
					}
					
					if(cfg.mapIDs.isEmpty())
						throw new Exception("row " + row + " 势力战地图列表为空 ");
					
					List<Integer> openDays = excelReadContext.getIntegerList(row, col++, ";");
					for(int d: openDays)
					{
						if(d < 0 || d > 6)
							throw new Exception("row " + row + " 势力战类型 " + cfg.type + " 开放日 " + d + " 非法");
						
						if(!cfg.openDays.add(d))
							throw new Exception("row " + row + " 势力战类型 " + cfg.type + " 开放日 " + d + " 重复");
					}
					cfg.openStartTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					if(cfg.openStartTime < GameData.GAME_DAY_REFRESH_TIME * 3600)
						throw new Exception("row " + row + " 势力战类型 " + cfg.type + " 开启时间 " + cfg.openStartTime + " 非法！");
					
					cfg.openLastTime = excelReadContext.getIntValue(row, col++);
					if(cfg.openLastTime + (cfg.openStartTime - GameData.GAME_DAY_REFRESH_TIME * 3600) > GameTime.getDayTimeSpan())
						throw new Exception("row " + row + " 势力战类型 " + cfg.type + " 持续时间 " + cfg.openLastTime + " 超过一天！");
					
					cfg.lvlReq = excelReadContext.getIntValue(row, col++);
					{
						SBean.RankCFGS lw = gdCfgs.roleRanks.get(GameData.RANK_TYPE_LOCAL_FORCEWAR_WHITE - 1).rank;
						if(lw.lvlReq > cfg.lvlReq)
							throw new Exception("势力战本服正派排行榜的可见等级 " + lw.lvlReq + " 大于开放等级 " + cfg.lvlReq);
						
						SBean.RankCFGS lb = gdCfgs.roleRanks.get(GameData.RANK_TYPE_LOCAL_FORCEWAR_BLACK - 1).rank;
						if(lb.lvlReq > cfg.lvlReq)
							throw new Exception("势力战本服邪派排行榜的可见等级 " + lb.lvlReq + " 大于开放等级 " + cfg.lvlReq);
						
						SBean.RankCFGS gw = gdCfgs.roleRanks.get(GameData.RANK_TYPE_GLOBAL_FORCEWAR_WHITE - 1).rank;
						if(gw.lvlReq > cfg.lvlReq)
							throw new Exception("势力战跨服正派排行榜的可见等级 " + gw.lvlReq + " 大于开放等级 " + cfg.lvlReq);
						
						SBean.RankCFGS gb = gdCfgs.roleRanks.get(GameData.RANK_TYPE_GLOBAL_FORCEWAR_BLACK - 1).rank;
						if(gb.lvlReq > cfg.lvlReq)
							throw new Exception("势力战跨服邪派排行榜的可见等级 " + gb.lvlReq + " 大于开放等级 " + cfg.lvlReq);
					}
					cfg.maxTime = excelReadContext.getIntValue(row, col++);
					cfg.gainFeats = new ArrayList<>();
					List<Integer> gainFeats = excelReadContext.getIntegerList(row, col++, ";");
					for(int gain: gainFeats)
						cfg.gainFeats.add(gain / 100.f);
						
					if(cfg.gainFeats.isEmpty())
						throw new Exception("row " + row + " 势力战奖励武勋百分比为空");
					
					if(cfgs.put(cfg.type, cfg) != null)
						throw new Exception("势力战 类型 " + cfg.type + " 重复!");
					
					row++;
				}
				
				gdCfgs.forcewar = cfgs;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 0;
				final int colStart = 0;
				int row = rowStart + 1;
				int col = colStart;
				
				SBean.ForceWarBaseCFGS baseCfg = new SBean.ForceWarBaseCFGS();
				
				final int rowMatch = excelReadContext.locateColumnTag(colStart, "势力战匹配规则");
				row = rowMatch + 1;
				col = colStart + 2;
				{
					baseCfg.match = new SBean.ForceWarMatchCFGS(0, 0, 0, 0, 0, 0);
					baseCfg.match.timeout = excelReadContext.getIntValue(row++, col);
					baseCfg.match.maxCnt = excelReadContext.getIntValue(row++, col);
					baseCfg.match.punishTime = excelReadContext.getIntValue(row++, col);
					baseCfg.match.lowerCntTime = excelReadContext.getIntValue(row++, col);
					baseCfg.match.lowerCnt = excelReadContext.getIntValue(row++, col);
					if(baseCfg.match.lowerCnt > baseCfg.match.maxCnt)
						throw new Exception("势力战通用配置表 row " + row + " 匹配不成降低人数标准所需人数 " + baseCfg.match.lowerCnt + " 大于战场单方人数限制 " + baseCfg.match.maxCnt);
					
					baseCfg.match.openDayLimit = excelReadContext.getIntValue(row++, col);
				}
				
				final int rowScore = excelReadContext.locateColumnTag(colStart, "积分奖励规则");
				row = rowScore + 2;
				col = colStart + 2;
				{
					baseCfg.score = new SBean.ForceWarScoreCFGS();
					baseCfg.score.killRole = new SBean.ForceWarScore(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2));
					baseCfg.score.firstKill = new SBean.ForceWarScore(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2));
					baseCfg.score.mostKill = new SBean.ForceWarScore(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2));
					baseCfg.score.killMonsters = new HashMap<>();
					baseCfg.score.killMonsters.put(GameData.MONSTER_BOSSTYPE_NPC, new SBean.ForceWarScore(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2)));
					baseCfg.score.killMonsters.put(GameData.MONSTER_BOSSTYPE_NORMALSTATUE, new SBean.ForceWarScore(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2)));
					baseCfg.score.killMonsters.put(GameData.MONSTER_BOSSTYPE_BIGSTATUE, new SBean.ForceWarScore(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2)));
					baseCfg.score.killMonsters.put(GameData.MONSTER_BOSSTYPE_BOSSSTATUE, new SBean.ForceWarScore(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2)));
				}
				
				final int rowFeat = excelReadContext.locateColumnTag(colStart, "积分对应奖励武勋值");
				row = rowFeat + 2;
				col = colStart + 2;
				{
					baseCfg.feat = new SBean.ForceWarFeatCFGS(new ArrayList<>());
					int lastFloor = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						SBean.ForceWarFeat feat = new SBean.ForceWarFeat(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1), excelReadContext.getIntValue(row++, col + 2));
						if(lastFloor > feat.floor)
							throw new Exception("row " + row + " 势力战通用配置表积分对应奖励武勋值 名次下限 " + feat.floor + " 非法");
						baseCfg.feat.rewards.add(feat);
					}
				}
				
				final int rowOther = excelReadContext.locateColumnTag(colStart, "其他势力战相关");
				row = rowOther + 1;
				col = colStart + 2;
				{
					baseCfg.other = new SBean.ForceWarOtherCFG();
					baseCfg.other.goldMapID = excelReadContext.getIntValue(row++, col);
					checkNormalMapCopyID(gdCfgs, row, baseCfg.other.goldMapID);
					baseCfg.other.silverMapID = excelReadContext.getIntValue(row++, col);
					checkNormalMapCopyID(gdCfgs, row, baseCfg.other.silverMapID);
					baseCfg.other.winAdd = excelReadContext.getIntValue(row++, col);
					baseCfg.other.loseAdd = excelReadContext.getIntValue(row++, col);
					baseCfg.other.goldMapMaxTime = excelReadContext.getIntValue(row++, col);
					baseCfg.other.silverMapMaxTime = excelReadContext.getIntValue(row++, col);
					baseCfg.other.prepareTime = excelReadContext.getIntValue(row++, col);
					baseCfg.other.autoCloseTime = excelReadContext.getIntValue(row++, col);
					baseCfg.other.doubleScoreStartTime = excelReadContext.getIntValue(row++, col);
					baseCfg.other.assistDistance = excelReadContext.getIntValue(row++, col);
					baseCfg.other.mostKillCnt = excelReadContext.getIntValue(row++, col);
					baseCfg.other.killStreaks = excelReadContext.getIntValue(row++, col);
					baseCfg.other.endKills = excelReadContext.getIntValue(row++, col);
					baseCfg.other.autoRevive = excelReadContext.getIntValue(row++, col);
					baseCfg.other.dayMapRewardTimes = excelReadContext.getIntValue(row++, col);
				}
				
				final int rowReward = excelReadContext.locateColumnTag(colStart, "武勋相关");
				row = rowReward + 2;
				col = colStart + 1;
				{
					baseCfg.rewards = new ArrayList<>();
					int lastFloor = 0;
					while(excelReadContext.isNotEmpty(row, col))
					{
						int floor = excelReadContext.getIntValue(row, col);
						if(floor < lastFloor)
							throw new Exception("势力战通用配置表 武勋相关 row " + row + " 武勋值下限 " + floor + " 非法");
						
						int whiteTitle = excelReadContext.getIntValue(row, col + 1);
						if(whiteTitle > 0)
							checkTitleIDValid(gdCfgs, row, whiteTitle);
						
						int blackTitle = excelReadContext.getIntValue(row++, col + 2);
						if(blackTitle > 0)
							checkTitleIDValid(gdCfgs, row, blackTitle);
						
						baseCfg.rewards.add(new SBean.ForceWarRewardCFGS(floor, whiteTitle, blackTitle));
					}
					if(baseCfg.rewards.isEmpty())
						throw new Exception("势力战通用配置表 武勋相关 没有配置");
				}
				
				final int rowLevels = excelReadContext.locateColumnTag(colStart, "动态等级段");
				row = rowLevels + 1;
				col = colStart + 1;
				{
					baseCfg.levels = new ArrayList<>();
					while(excelReadContext.isNotEmpty(row, col))
					{
						baseCfg.levels.add(excelReadContext.getIntValue(row++, col));
					}
					
					for(SBean.ForceWarCFGS e: gdCfgs.forcewar.values())
					{
						if(e.mapIDs.size() != baseCfg.levels.size() || baseCfg.levels.isEmpty())
							throw new Exception("势力战通用配置表 动态等级段 非法");
					}
				}
				
				final int rowProp = excelReadContext.locateColumnTag(colStart, "势力战属性加成");
				row = rowProp + 1;
				col = 2;
				{
					baseCfg.baseBuff = excelReadContext.getIntValue(row++, col);
					if(baseCfg.baseBuff > 0)
						checkBuffIDValid(gdCfgs, row, baseCfg.baseBuff);
				}
				
				gdCfgs.forcewarbase = baseCfg;
			}
		}
	}
	
	public void loadClanTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				SBean.ClanCFGS clanCFGS = new SBean.ClanCFGS();
				final int colStart = 0;

				int rowStart = excelReadContext.locateColumnTag(colStart, "建立宗门相关");
				int row = rowStart + 1;
				int col = colStart + 2;
				clanCFGS.level = excelReadContext.getIntValue(row++, col);
				clanCFGS.vip = excelReadContext.getIntValue(row++, col);
				clanCFGS.diamond = excelReadContext.getIntValue(row++, col);
				clanCFGS.addLevels = new ArrayList<>();
				for (int index = 0; index < 3; index++)
				{
					clanCFGS.addLevels.add(excelReadContext.getIntValue(row++, col));
				}

				int rowClanLevel = excelReadContext.locateColumnTag(colStart, "与宗门等级挂钩的参数");
				clanCFGS.clanLevels = new ArrayList<>();
				int index = 0;
				
				row = rowClanLevel + 3; // 行
				col = colStart + 1; // 列
				
				while (excelReadContext.isNotEmpty(row, col))
				{
					
					SBean.ClanLevelCFGS clanLevelCFGS = new SBean.ClanLevelCFGS();
					clanLevelCFGS.level = excelReadContext.getIntValue(row, col++);
					clanLevelCFGS.xuetie = excelReadContext.getIntValue(row, col++);
					clanLevelCFGS.yaocao = excelReadContext.getIntValue(row, col++);
					clanLevelCFGS.shengwang = excelReadContext.getIntValue(row, col++);
					clanLevelCFGS.subLvl = excelReadContext.getIntValue(row, col++);
					clanLevelCFGS.doPowerMax = excelReadContext.getIntValue(row, col++);
					clanLevelCFGS.elderCountMax = excelReadContext.getIntValue(row, col++);
					
					col += 1;
					clanLevelCFGS.shareCountMax = excelReadContext.getIntValue(row, col++);
					clanCFGS.clanLevels.add(clanLevelCFGS);
					
					row ++;
					col = colStart + 1;
				}
				gdCfgs.clan = clanCFGS;

				int rowClanParam = excelReadContext.locateColumnTag(colStart, "宗门补充参数");
				SBean.ClanSupplementCFGS supplement = new SBean.ClanSupplementCFGS();
				row = rowClanParam + 1;
				col = colStart + 2;
				//supplement.doPowerBuyCount = excelReadContext.getIntValue(row++, col);
				supplement.doPowerBuyDiamond = excelReadContext.getIntegerList(row++, col, ";");
				supplement.buyDoPower = excelReadContext.getIntValue(row++, col);
				supplement.appointUseDoPower = excelReadContext.getIntValue(row++, col);
				supplement.recruitUseDiamond = excelReadContext.getIntValue(row++, col);
				supplement.kickUseDoPower = excelReadContext.getIntValue(row++, col);
				supplement.appointElderLevel = excelReadContext.getIntValue(row++, col);

				gdCfgs.clan.supplement = supplement;

				int rowClanDisciple = excelReadContext.locateColumnTag(colStart, "收徒");
				SBean.ClanShoutuCFGS discipleCFGS = new SBean.ClanShoutuCFGS();
				row = rowClanDisciple + 1;
				col = colStart + 2;
				discipleCFGS.needLevel = excelReadContext.getIntValue(row++, col);
				discipleCFGS.useDoPower = excelReadContext.getIntegerList(row++, col, ";");
				discipleCFGS.cd1 = excelReadContext.getIntegerList(row++, col, ";");
				
				discipleCFGS.addSpeed = excelReadContext.getIntValue(row++, col);
				discipleCFGS.genDisciples = excelReadContext.getIntegerList(row++, col, ";");
	
				discipleCFGS.range = excelReadContext.getFloatValue(row++, col);
				discipleCFGS.cd2 = excelReadContext.getIntegerList(row++, col, ";");

				gdCfgs.clan.shoutu = discipleCFGS;

				int rowClanBiwu = excelReadContext.locateColumnTag(colStart, "比武");
				SBean.ClanBiwuCFGS biwuCFGS = new SBean.ClanBiwuCFGS();
				row = rowClanBiwu + 1;
				col = colStart + 2;
				biwuCFGS.needLevel = excelReadContext.getIntValue(row++, col);
				biwuCFGS.needDZ = excelReadContext.getIntValue(row++, col);
				biwuCFGS.useDoPower = excelReadContext.getIntegerList(row++, col, ";");
				
				biwuCFGS.cd1 = excelReadContext.getIntegerList(row++, col, ";");
				
				biwuCFGS.addSpeed = excelReadContext.getIntValue(row++, col);
				biwuCFGS.cd2 = excelReadContext.getIntegerList(row++, col, ";");
				
				gdCfgs.clan.biwu = biwuCFGS;

				int rowClanBushi = excelReadContext.locateColumnTag(colStart, "布施");
				SBean.ClanBushiCFGS bushiCFGS = new SBean.ClanBushiCFGS();
				row = rowClanBushi + 1;
				col = colStart + 2;
				bushiCFGS.needLevel = excelReadContext.getIntValue(row++, col);
				bushiCFGS.needItemId = excelReadContext.getIntValue(row++, col);
				bushiCFGS.needItemCount = excelReadContext.getIntValue(row++, col);
				bushiCFGS.cd = excelReadContext.getIntValue(row++, col);
				bushiCFGS.baseValue = excelReadContext.getIntValue(row++, col);
				bushiCFGS.floatValue = excelReadContext.getIntValue(row++, col);
				gdCfgs.clan.bushi = bushiCFGS;

				int rowClanChuandao = excelReadContext.locateColumnTag(colStart, "传道");
				SBean.ClanChuandaoCFGS chuandaoCFGS = new SBean.ClanChuandaoCFGS();
				row = rowClanChuandao + 1;
				col = colStart + 2;
				chuandaoCFGS.needLevel = excelReadContext.getIntValue(row++, col);
				chuandaoCFGS.needItemId = excelReadContext.getIntValue(row++, col);
				chuandaoCFGS.needItemCount = excelReadContext.getIntValue(row++, col);
				chuandaoCFGS.needjydz = excelReadContext.getIntValue(row++, col);
				chuandaoCFGS.cd = excelReadContext.getIntValue(row++, col);
				gdCfgs.clan.chuandao = chuandaoCFGS;

				//
				int rowClanDizitang = excelReadContext.locateColumnTag(colStart, "宗门弟子堂");
				SBean.ClanDizdtangCFGS clanDizdtangCFGS = new SBean.ClanDizdtangCFGS();
				row = rowClanDizitang + 1;
				col = colStart + 2;
				clanDizdtangCFGS.needLevel = excelReadContext.getIntValue(row++, col);
				row += 1;
				col = colStart + 1;
				clanDizdtangCFGS.wgValue = new ArrayList<>();
				clanDizdtangCFGS.xfValue = new ArrayList<>();
				clanDizdtangCFGS.yqValue = new ArrayList<>();
				clanDizdtangCFGS.tjValue = new ArrayList<>();

				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanDizdtangAttriCFGS wgValue = new SBean.ClanDizdtangAttriCFGS();
					wgValue.level = excelReadContext.getIntValue(row, col++);
					wgValue.value = excelReadContext.getIntValue(row, col++);
					clanDizdtangCFGS.wgValue.add(wgValue);

					SBean.ClanDizdtangAttriCFGS xfValue = new SBean.ClanDizdtangAttriCFGS();
					xfValue.level = excelReadContext.getIntValue(row, col++);
					xfValue.value = excelReadContext.getIntValue(row, col++);
					clanDizdtangCFGS.xfValue.add(xfValue);

					SBean.ClanDizdtangAttriCFGS yqValue = new SBean.ClanDizdtangAttriCFGS();
					yqValue.level = excelReadContext.getIntValue(row, col++);
					yqValue.value = excelReadContext.getIntValue(row, col++);
					clanDizdtangCFGS.yqValue.add(yqValue);

					SBean.ClanDizdtangAttriCFGS tjValue = new SBean.ClanDizdtangAttriCFGS();
					tjValue.level = excelReadContext.getIntValue(row, col++);
					tjValue.value = excelReadContext.getIntValue(row, col++);
					clanDizdtangCFGS.tjValue.add(tjValue);

					row++;
					col = colStart + 1;
				}
				gdCfgs.clan.dizitang = clanDizdtangCFGS;

				//
				int rowClanTask = excelReadContext.locateColumnTag(colStart, "宗门任务相关");
				SBean.ClanTaskParamCFGS taskParamCFGS = new SBean.ClanTaskParamCFGS();
				row = rowClanTask + 1;
				col = colStart + 2;
				taskParamCFGS.refreshDoPower = excelReadContext.getIntValue(row++, col);
				taskParamCFGS.dayAddDzCount = excelReadContext.getIntValue(row++, col);
				taskParamCFGS.dayAddDzDoPower = excelReadContext.getIntegerList(row++, col, ";");
				
				taskParamCFGS.dayTaskMax = excelReadContext.getIntValue(row++, col);
				taskParamCFGS.rewardRate = excelReadContext.getFloatValue(row++, col);
				taskParamCFGS.critRate = excelReadContext.getFloatValue(row++, col);
				taskParamCFGS.remainRate = excelReadContext.getFloatValue(row++, col);
				taskParamCFGS.recoverRate = excelReadContext.getFloatValue(row++, col);
				taskParamCFGS.mapId = excelReadContext.getIntValue(row++, col);
				taskParamCFGS.fightTime = excelReadContext.getIntValue(row++, col);
				taskParamCFGS.autoCloseTime = excelReadContext.getIntValue(row++, col);
				taskParamCFGS.fightStartCD = excelReadContext.getIntValue(row++, col);
				
				gdCfgs.clan.taskParam = taskParamCFGS;
				
				//
				int rowClanOccupyOre = excelReadContext.locateColumnTag(colStart, "宗门之占矿");
				row = rowClanOccupyOre + 1;
				col = colStart + 2;
				SBean.ClanOccupyOreCFGS occupyOreCFGS = new SBean.ClanOccupyOreCFGS();
				occupyOreCFGS.ironOpenLvl = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.ironBaseValue = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.herbOpenLvl = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.herbBaseValue = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.thorpeOpenLvl = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.thorpeBaseValue = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.occupyValue = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.finishTime = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.harryBase = excelReadContext.getFloatValue(row++, col);
				occupyOreCFGS.failDiamondDown = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.faildiamondUp = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.successDiamondDown = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.successDiamondUp = excelReadContext.getIntValue(row++, col);
				occupyOreCFGS.petLevel = excelReadContext.getIntValue(row++, col);
				row += 1;
				col = colStart + 1;
				occupyOreCFGS.oreLvl = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanOccupyOreLvlCFGS lvlCFGS = new SBean.ClanOccupyOreLvlCFGS();
					lvlCFGS.Lvl = excelReadContext.getIntValue(row, col++);
					lvlCFGS.ironPrestige = excelReadContext.getIntValue(row, col++);
					lvlCFGS.ironItemId = excelReadContext.getIntValue(row, col++);
					lvlCFGS.ironItemCount = excelReadContext.getIntValue(row, col++);
					lvlCFGS.ironAdditionRate = excelReadContext.getFloatValue(row, col++);
					lvlCFGS.herbPrestige = excelReadContext.getIntValue(row, col++);
					lvlCFGS.herbItemId = excelReadContext.getIntValue(row, col++);
					lvlCFGS.herbItemCount = excelReadContext.getIntValue(row, col++);
					lvlCFGS.herbAdditionRate = excelReadContext.getFloatValue(row, col++);
					lvlCFGS.thorpePrestige = excelReadContext.getIntValue(row, col++);
					lvlCFGS.thorpeItemId = excelReadContext.getIntValue(row, col++);
					lvlCFGS.thorpeItemCount = excelReadContext.getIntValue(row, col++);
					lvlCFGS.thorpeAdditionRate = excelReadContext.getFloatValue(row, col++);
					lvlCFGS.needClanLevel = excelReadContext.getIntValue(row, col++);
					occupyOreCFGS.oreLvl.add(lvlCFGS);
					row ++;
					col = colStart + 1;
				}
				gdCfgs.clan.occupyOre = occupyOreCFGS;
				
				
				//
				int rowClanRobOre = excelReadContext.locateColumnTag(colStart, "宗门之夺矿");
				row = rowClanRobOre + 1;
				col = colStart + 2;
				SBean.ClanRobOreCFGS robOreCFGS = new SBean.ClanRobOreCFGS();
				robOreCFGS.searchCoin = excelReadContext.getIntegerList(row++, col, ";");
				robOreCFGS.searchTime = excelReadContext.getIntValue(row++, col);
				robOreCFGS.pointUp = excelReadContext.getIntValue(row++, col);
				robOreCFGS.pointSpeed = excelReadContext.getIntValue(row++, col);
				robOreCFGS.usePoint = excelReadContext.getIntValue(row++, col);
				robOreCFGS.giveEnemyPoint = excelReadContext.getIntValue(row++, col);
				robOreCFGS.diamondDown = excelReadContext.getIntValue(row++, col);
				robOreCFGS.diamondUp = excelReadContext.getIntValue(row++, col);
				robOreCFGS.dayGivePoint = excelReadContext.getIntValue(row++, col);
				robOreCFGS.successRatio = excelReadContext.getFloatValue(row++, col);
				robOreCFGS.petLevel = excelReadContext.getIntValue(row++, col);
				robOreCFGS.mapId = excelReadContext.getIntValue(row++, col);
				robOreCFGS.fightTime = excelReadContext.getIntValue(row++, col);
				robOreCFGS.autoCloseTime = excelReadContext.getIntValue(row++, col);
				robOreCFGS.fightStartCD = excelReadContext.getIntValue(row++, col);
				gdCfgs.clan.robOre = robOreCFGS;
				
				int rowClanOther = excelReadContext.locateColumnTag(colStart, "宗门杂七杂八");
				row = rowClanOther + 1;
				col = 2;
				SBean.ClanOthersCFGS othersCfg = new SBean.ClanOthersCFGS();
				othersCfg.moveCost = excelReadContext.getIntegerList(row++, col, ";");
				othersCfg.jyExpMax = excelReadContext.getIntValue(row++, col);
				othersCfg.orgRecipes = new HashSet<Integer>();
				List<Integer> recipes = excelReadContext.getIntegerList(row++, col, ";");
				for(int rid: recipes)
				{
					//checkClanRecipeIDValid(gdCfgs, row, rid); // 宗门生产部分移除掉了
					othersCfg.orgRecipes.add(rid);
				}
				othersCfg.jyMax = excelReadContext.getIntValue(row++, col);
				othersCfg.jyInit = excelReadContext.getIntValue(row++, col);
				othersCfg.ptInit = excelReadContext.getIntValue(row++, col);
				othersCfg.xuetieInit = excelReadContext.getIntValue(row++, col);
				othersCfg.yaocaoInit = excelReadContext.getIntValue(row++, col);
				othersCfg.doPowerInit = excelReadContext.getIntValue(row++, col);
				othersCfg.prestigeInit = excelReadContext.getIntValue(row++, col);
				othersCfg.jyExpInit = excelReadContext.getIntValue(row++, col);
				othersCfg.memberMax = excelReadContext.getIntValue(row++, col);
				othersCfg.disbandCDLevel = excelReadContext.getIntValue(row++, col);
				othersCfg.disbandCD = excelReadContext.getIntValue(row++, col);
				
				row += 1;
				col = colStart + 1;
				othersCfg.existPosition = new TreeMap<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanExistPositionCFGS positionCFGS = new SBean.ClanExistPositionCFGS();
					positionCFGS.id = excelReadContext.getIntValue(row, col++);
					positionCFGS.x = excelReadContext.getIntValue(row, col++);
					positionCFGS.y = excelReadContext.getIntValue(row, col++);

					othersCfg.existPosition.put(((long)positionCFGS.x << 32) | ((long)positionCFGS.y & 0xffffffffL), positionCFGS);
					row ++;
					col = colStart + 1;
				}
				
				gdCfgs.clan.others = othersCfg;
				
				
				
				
				int rowClanFightTeam = excelReadContext.locateColumnTag(colStart, "宗门战之配置部队");
				row = rowClanFightTeam + 1;
				col = 2;
				SBean.ClanFightTeamCFGS fightTeamCFGS = new SBean.ClanFightTeamCFGS();
				fightTeamCFGS.teamSize = excelReadContext.getIntValue(row++, col);
				fightTeamCFGS.teamLimit = excelReadContext.getIntegerList(row++, col, ";");
				fightTeamCFGS.petMinLvl = excelReadContext.getIntValue(row++, col);
				gdCfgs.clan.fightTeam = fightTeamCFGS;
				
				
				int rowClanBattle = excelReadContext.locateColumnTag(colStart, "宗门战之常规战斗");
				row = rowClanBattle + 1;
				col = 2;
				SBean.ClanNormalBattleCFGS normalBattleCFGS = new SBean.ClanNormalBattleCFGS();
				normalBattleCFGS.useDoPower = excelReadContext.getIntValue(row++, col);
				
				normalBattleCFGS.clanMinLvl = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.ensureTime = excelReadContext.getIntValue(row++, col);
				
				normalBattleCFGS.mapId = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.battleTime = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.battleEndCloseTime = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.battleStartTime = excelReadContext.getIntValue(row++, col);
				
				String s1 = excelReadContext.getStringValue(row++, col);
				String s2 = excelReadContext.getStringValue(row, col+1);
				normalBattleCFGS.battleOpenUpTime = GameTime.parseSecondOfDay(s1);
				normalBattleCFGS.battleOpenDownTime = GameTime.parseSecondOfDay(s2);
				normalBattleCFGS.expiredTime = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.moveTime = excelReadContext.getFloatList(row++, col, ";");
				normalBattleCFGS.moveShortTime = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.moveLongTime = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.keekDiamond = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.failRateXuantie = excelReadContext.getFloatValue(row++, col);
				normalBattleCFGS.failRateyaocao = excelReadContext.getFloatValue(row++, col);
				normalBattleCFGS.failRatePrestige = excelReadContext.getFloatValue(row++, col);
				normalBattleCFGS.failPrestigeLel = excelReadContext.getIntValue(row++, col);
				normalBattleCFGS.reduceXuantieRate = excelReadContext.getFloatValue(row++, col);
				normalBattleCFGS.reduceYaocaoRate = excelReadContext.getFloatValue(row++, col);
				normalBattleCFGS.reducePrestigeRate = excelReadContext.getFloatValue(row++, col);
				gdCfgs.clan.normalBattle = normalBattleCFGS;
				
				
				int rowClanBattleHelp = excelReadContext.locateColumnTag(colStart, "宗门战之支援");
				row = rowClanBattleHelp + 1;
				col = 2;
				SBean.ClanBattleHelpCFGS battleHelpCFGS = new SBean.ClanBattleHelpCFGS();
				battleHelpCFGS.qyRemainTime = excelReadContext.getIntValue(row++, col);
				battleHelpCFGS.zyRemainTime = excelReadContext.getIntValue(row++, col);
				battleHelpCFGS.mapId = excelReadContext.getIntValue(row++, col);
				battleHelpCFGS.battleTime = excelReadContext.getIntValue(row++, col);
				battleHelpCFGS.battleEndCloseTime = excelReadContext.getIntValue(row++, col);
				battleHelpCFGS.battleStartTime = excelReadContext.getIntValue(row++, col);
				battleHelpCFGS.expiredTime = excelReadContext.getIntValue(row++, col);
				battleHelpCFGS.battleHelpTimes = excelReadContext.getIntValue(row++, col);
				row += 1;
				col = colStart + 1;
				battleHelpCFGS.enemyLevel = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanBattleHelpEnemyLevelCFGS lvlCFGS = new SBean.ClanBattleHelpEnemyLevelCFGS();
					lvlCFGS.level = excelReadContext.getIntValue(row, col++);
					lvlCFGS.prestige = excelReadContext.getIntValue(row, col++);
					battleHelpCFGS.enemyLevel.add(lvlCFGS);
					row ++;
					col = colStart + 1;
				}
				gdCfgs.clan.battleHelp = battleHelpCFGS;
				
				
				int rowClanPrestige = excelReadContext.locateColumnTag(colStart, "宗门声望获得");
				row = rowClanPrestige + 2;
				col = 2;
				SBean.ClanAddPrestigeCFGS prestige = new SBean.ClanAddPrestigeCFGS();
				prestige.items = new TreeMap<Integer, Integer>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					prestige.items.put(excelReadContext.getIntValue(row, col++), excelReadContext.getIntValue(row, col++));
					row ++;
					col = 2;
				}
				
				clanCFGS.clanRanks = new ArrayList<>();
				{
					final int rowRank = excelReadContext.locateColumnTag(colStart, "宗门排行榜");
					row = rowRank + 2;
					while(excelReadContext.isNotEmpty(row, colStart + 1))
					{
						col = colStart + 1;
						int rankFloor = excelReadContext.getIntValue(row, col++);
						int whiteTitle = excelReadContext.getIntValue(row, col++);
						int blackTitle = excelReadContext.getIntValue(row, col++);
						
						clanCFGS.clanRanks.add(new SBean.ClanRankCFGS(rankFloor, whiteTitle, blackTitle));
						row++;
					}
				}
				
				gdCfgs.clan.addPrestige = prestige;
			}
			
			
			excelReadContext.ReadSheet(1);
			{
				Map<Integer, SBean.ClanTaskGroupCFGS> taskGroupCFGSList = new TreeMap<>();
				Map<Integer, SBean.ClanTaskCFGS> taskCFGSList = new TreeMap<>();

				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col)) {
					SBean.ClanTaskCFGS taskCFGS = new SBean.ClanTaskCFGS();
					taskCFGS.id = excelReadContext.getIntValue(row, col ++);
					taskCFGS.level = excelReadContext.getIntValue(row, col ++);
					col += 3;
					taskCFGS.star = excelReadContext.getIntValue(row, col ++);
					taskCFGS.finishCount = excelReadContext.getIntValue(row, col ++);
					taskCFGS.jydzSkill = excelReadContext.getIntValue(row, col ++);
					taskCFGS.ptdzCount = excelReadContext.getIntValue(row, col ++);
					taskCFGS.calldzCount = excelReadContext.getIntValue(row, col ++);
					taskCFGS.xuantie = excelReadContext.getIntValue(row, col ++);
					taskCFGS.caoyao = excelReadContext.getIntValue(row, col ++);
					taskCFGS.dzexp = excelReadContext.getIntValue(row, col ++);
					taskCFGS.baseTime = excelReadContext.getIntValue(row, col ++);
					taskCFGSList.put(taskCFGS.id, taskCFGS);
					
					SBean.ClanTaskGroupCFGS taskGroupCFGS = taskGroupCFGSList.get(taskCFGS.level);
					if(taskGroupCFGS == null){
						taskGroupCFGS = new SBean.ClanTaskGroupCFGS();
						taskGroupCFGS.level = taskCFGS.level;
						taskGroupCFGS.tasks = new ArrayList<>();
						taskGroupCFGS.tasks.add(taskCFGS.id);
						taskGroupCFGSList.put(taskGroupCFGS.level, taskGroupCFGS);
						//clanTaskGroup.put(taskCFGS.level, taskGroupCFGS);
					}else{
						taskGroupCFGS.tasks.add(taskCFGS.id);
					}
					
					row ++;
					col = colStart;
				}
				gdCfgs.clanTask = taskCFGSList;
				gdCfgs.clanGroupTask = taskGroupCFGSList;
			}
			
			excelReadContext.ReadSheet(2);
			{
				SBean.ClanValueCFGS clanValueCFGS = new SBean.ClanValueCFGS();
				final int colStart = 0;

				int rowStart = excelReadContext.locateColumnTag(colStart, "精英弟子相关");
				int row = rowStart + 1;
				int col = colStart + 2;
				clanValueCFGS.attriSum = excelReadContext.getIntValue(row++, col);
				clanValueCFGS.eachAttri = excelReadContext.getIntValue(row++, col);
				clanValueCFGS.skillCount = excelReadContext.getIntValue(row++, col);
				clanValueCFGS.skillLvl = excelReadContext.getIntValue(row++, col);

				clanValueCFGS.pointToDisciple = new ArrayList<>();
				row += 1;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanPointToDiscipleCFGS point = new SBean.ClanPointToDiscipleCFGS();
					point.dzCount = excelReadContext.getIntValue(row, col++);
					point.point = excelReadContext.getIntValue(row, col++);
					clanValueCFGS.pointToDisciple.add(point);
					row++;
					col = colStart + 2;
				}

				//
				int rowChuandao = excelReadContext.locateColumnTag(colStart, "传道相关");
				row = rowChuandao + 2;
				col = colStart + 2;
				clanValueCFGS.attriTotal = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanAttriTotalCFGS attri = new SBean.ClanAttriTotalCFGS();
					attri.attriTotal = excelReadContext.getIntValue(row, col++);
					attri.minValue = excelReadContext.getIntValue(row, col++);
					attri.maxValue = excelReadContext.getIntValue(row, col++);
					clanValueCFGS.attriTotal.add(attri);
					row++;
					col = colStart + 2;
				}

				//
				int rowRushTollgate = excelReadContext.locateColumnTag(colStart, "精英弟子冲关");
				row = rowRushTollgate + 2;
				col = colStart + 1;
				clanValueCFGS.rushTollgate = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanRushTollgateCFGS rushTollgateCFGS = new SBean.ClanRushTollgateCFGS();
					rushTollgateCFGS.count = excelReadContext.getIntValue(row, col++);
					rushTollgateCFGS.exp = excelReadContext.getIntValue(row, col++);
					rushTollgateCFGS.rate = excelReadContext.getFloatValue(row, col++);
					rushTollgateCFGS.attriRate = excelReadContext.getFloatValue(row, col++);
					rushTollgateCFGS.skillRate = excelReadContext.getFloatValue(row, col++);
					rushTollgateCFGS.diamond = excelReadContext.getIntValue(row, col++);
					rushTollgateCFGS.minValue = excelReadContext.getIntValue(row, col++);
					rushTollgateCFGS.maxValue = excelReadContext.getIntValue(row, col++);
					clanValueCFGS.rushTollgate.add(rushTollgateCFGS);
					row++;
					col = colStart + 1;
				}

				//
				int rowUpLevelTmp = excelReadContext.locateColumnTag(colStart, "使用道具提升特技");
				row = rowUpLevelTmp + 2;
				col = colStart + 1;
				clanValueCFGS.itemUpSkill = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.ClanItemUpSkillRateCFGS itemUpSkillCFGS = new SBean.ClanItemUpSkillRateCFGS();
					itemUpSkillCFGS.level = excelReadContext.getIntValue(row, col++);
					itemUpSkillCFGS.rate = excelReadContext.getFloatValue(row, col++);
					clanValueCFGS.itemUpSkill.add(itemUpSkillCFGS);
					row++;
					col = colStart + 1;
				}
				
				
				//
				int rowAttriUpLevelTmp = excelReadContext.locateColumnTag(colStart, "使用道具提升属性");
				row = rowAttriUpLevelTmp + 2;
				col = colStart + 1;
				clanValueCFGS.itemUpAttri = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col)) {
					SBean.ClanItemUpAttriRateCFGS itemUpAttriCFGS = new SBean.ClanItemUpAttriRateCFGS();
					itemUpAttriCFGS.value = excelReadContext.getIntValue(row, col++);
					itemUpAttriCFGS.rate = excelReadContext.getFloatValue(row, col++);
					clanValueCFGS.itemUpAttri.add(itemUpAttriCFGS);
					row++;
					col = colStart + 1;
				}
				
				
				//
				int rowEliteDZTaskTmp = excelReadContext.locateColumnTag(colStart, "精英弟子影响宗门任务");
				row = rowEliteDZTaskTmp + 2;
				col = colStart + 1;
				clanValueCFGS.discipleTask = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col)) {
					SBean.ClanEliteDZTaskCFGS eliteDZTaskCFGS = new SBean.ClanEliteDZTaskCFGS();
					eliteDZTaskCFGS.level = excelReadContext.getIntValue(row, col++);
					eliteDZTaskCFGS.decTimeRate = excelReadContext.getFloatValue(row, col++);
					eliteDZTaskCFGS.decDiscipleRate = excelReadContext.getFloatValue(row, col++);
					clanValueCFGS.discipleTask.add(eliteDZTaskCFGS);
					row++;
					col = colStart + 1;
				}
				
				
				//
				int rowEliteDisSkill = excelReadContext.locateColumnTag(colStart, "特长相关");
				row = rowEliteDisSkill + 2;
				col = colStart + 1;
				clanValueCFGS.skillItem = new ArrayList<>();
				clanValueCFGS.itemMappingSkill = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, col)){
					SBean.ClanItemUpSkillCFGS itemUpSkillCFGS = new SBean.ClanItemUpSkillCFGS();
					itemUpSkillCFGS.id = excelReadContext.getIntValue(row, col++);
					col += 1;
					itemUpSkillCFGS.items = new ArrayList<>();
					String items = excelReadContext.getStringValue(row, col++);
					for(String item : items.split(";")){
						itemUpSkillCFGS.items.add(Integer.valueOf(item));
						clanValueCFGS.itemMappingSkill.put(Integer.valueOf(item), itemUpSkillCFGS.id);
					}
					clanValueCFGS.skillItem.add(itemUpSkillCFGS);
					row++;
					col = colStart + 1;
				}
				
				
				//
				int rowEliteDisAttri = excelReadContext.locateColumnTag(colStart, "属性相关");
				row = rowEliteDisAttri + 2;
				col = colStart + 1;
				clanValueCFGS.attriItem = new ArrayList<>();
				clanValueCFGS.itemMappingAttri = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, col)){
					SBean.ClanItemUpAttriCFGS itemUpAttriCFGS = new SBean.ClanItemUpAttriCFGS();
					itemUpAttriCFGS.id = excelReadContext.getIntValue(row, col++);
					col += 1;
					itemUpAttriCFGS.items = new TreeMap<>();
					String items1 = excelReadContext.getStringValue(row, col++);
					for(String item : items1.split(";")){
						if(itemUpAttriCFGS.items.containsKey(Integer.valueOf(item)))
							throw new Exception("宗门 弟子堂属性相关表，道具提升属性ID " + Integer.valueOf(item) + " 重复");
						itemUpAttriCFGS.items.put(Integer.valueOf(item), 1);
						clanValueCFGS.itemMappingAttri.put(Integer.valueOf(item), itemUpAttriCFGS.id);
					}
					String items2 = excelReadContext.getStringValue(row, col++);
					for(String item : items2.split(";")){
						if(itemUpAttriCFGS.items.containsKey(Integer.valueOf(item)))
							throw new Exception("宗门 弟子堂属性相关表，道具提升属性ID " + Integer.valueOf(item) + " 重复");
						itemUpAttriCFGS.items.put(Integer.valueOf(item), 2);
						clanValueCFGS.itemMappingAttri.put(Integer.valueOf(item), itemUpAttriCFGS.id);
					}
					
					String items5 = excelReadContext.getStringValue(row, col++);
					for(String item : items5.split(";")){
						if(itemUpAttriCFGS.items.containsKey(Integer.valueOf(item)))
							throw new Exception("宗门 弟子堂属性相关表，道具提升属性ID " + Integer.valueOf(item) + " 重复");
						itemUpAttriCFGS.items.put(Integer.valueOf(item), 5);
						clanValueCFGS.itemMappingAttri.put(Integer.valueOf(item), itemUpAttriCFGS.id);
					}
					row++;
					col = colStart + 1;
					clanValueCFGS.attriItem.add(itemUpAttriCFGS);
					
				}
				gdCfgs.clanValue = clanValueCFGS;
				
				
				

			}
			

			
			excelReadContext.ReadSheet(3);
			{
				List<SBean.ClanDiscipleNameCFGS> discipleNameCFGSList = new ArrayList<>();
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col)) {
					SBean.ClanDiscipleNameCFGS discipleNameCFGS = new SBean.ClanDiscipleNameCFGS();
					discipleNameCFGS.id = excelReadContext.getIntValue(row, col ++);
					discipleNameCFGS.name = excelReadContext.getStringValue(row, col ++);
					discipleNameCFGS.icon = excelReadContext.getIntValue(row, col ++);
					row ++;
					col = colStart;
					
					discipleNameCFGSList.add(discipleNameCFGS);
				}
				
				gdCfgs.clanDiscipleName = discipleNameCFGSList;
			}
			

			System.out.println("load table " + fileName + " success.");
		}
	}

	public void loadPKTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int rowStart = 2;
			final int colStart = 0;
			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				SBean.PKCFGS cfg = new SBean.PKCFGS(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					cfg.deltaMax.add(excelReadContext.getIntValue(row, col++));
					cfg.dmgDecrease.add(excelReadContext.getIntValue(row, col++));
					cfg.priceIncrease.add(excelReadContext.getFloatValue(row, col++));
					row++;
				}
				
				if(cfg.dmgDecrease.isEmpty() || cfg.dmgDecrease.get(0) != 0 || cfg.priceIncrease.get(0) != 0)
					throw new Exception("PK系统的第一档 伤害惩罚 或者 价格惩罚 非法");
				
				gdCfgs.pkSystem = cfg;
			}
		}
	}
	
	public void loadMainTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				SBean.CommonCFGS cfg = new SBean.CommonCFGS();
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "引擎相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					
					SBean.CommonEngineCFGS engineCfg = new SBean.CommonEngineCFGS();
					engineCfg.interval = excelReadContext.getIntValue(row++, col);
					cfg.engine = engineCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "输入相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonInputCFGS inputcfg = new SBean.CommonInputCFGS();
					inputcfg.maxUserNameLength = excelReadContext.getIntValue(row++, col);
					inputcfg.maxRoleNameLength = excelReadContext.getIntValue(row++, col);
					inputcfg.maxSectNameLength = excelReadContext.getIntValue(row++, col);
					inputcfg.maxSectCreedLength = excelReadContext.getIntValue(row++, col);
					inputcfg.maxMsgLength = excelReadContext.getIntValue(row++, col);
					inputcfg.maxDiySkillNameLength = excelReadContext.getIntValue(row++, col);
					inputcfg.maxClanNameLength = excelReadContext.getIntValue(row++, col);
					inputcfg.maxSkillPresetNameLength = excelReadContext.getIntValue(row++, col);
					cfg.input = inputcfg;	
				}
				

				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "聊天相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonChatCFGS chatcfg = new SBean.CommonChatCFGS();
					chatcfg.worldChatItem = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, chatcfg.worldChatItem, false);
					chatcfg.publicChatInterval = excelReadContext.getIntValue(row++, col);
					chatcfg.privateChatInterval = excelReadContext.getIntValue(row++, col);
					chatcfg.mRoomInviteChatInterval = excelReadContext.getIntValue(row++, col);
					row = rowStart + 2;
					col = colStart + 4;
					chatcfg.allServerChatVipNeed = excelReadContext.getIntValue(row++, col);
					chatcfg.allServerChatItem = excelReadContext.getIntValue(row++, col);
					chatcfg.sectInviteChatInterval = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, chatcfg.allServerChatItem, false);
					row = rowStart + 3;
					col = colStart + 2;
					chatcfg.allServerChatInterval = excelReadContext.getIntValue(row++, col);
					
					cfg.chat = chatcfg;	
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "背包相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonBagCFGS bagcfg = new SBean.CommonBagCFGS();
					bagcfg.initCount = excelReadContext.getIntValue(row, col);
					col += 2;
					bagcfg.extendCellCount = excelReadContext.getIntValue(row, col);
					col += 2;
					bagcfg.extendCellCost = excelReadContext.getIntegerList(row, col, ";");
					
					row += 2;
					col = colStart + 2;
					
					bagcfg.extendCellSize = excelReadContext.getIntValue(row, col);
					cfg.bag = bagcfg;
				}
				
				{
					// 技能相关
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "技能相关");
					int row = rowStart + 2;
					int col = colStart + 3;

					SBean.CommonSkillCFGS cSkillCfgs = new SBean.CommonSkillCFGS();
					cSkillCfgs.atk = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						cSkillCfgs.atk.add(excelReadContext.getDoubleValue(row, col));
						col = col + 2;
					}

					row += 2;
					col = colStart + 3;
					cSkillCfgs.atr = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						cSkillCfgs.atr.add(excelReadContext.getDoubleValue(row, col));
						col = col + 2;
					}

					row += 2;
					col = colStart + 3;
					cSkillCfgs.cri = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						cSkillCfgs.cri.add(excelReadContext.getDoubleValue(row, col));
						col = col + 2;
					}

					row += 2;
					col = colStart + 3;
					cSkillCfgs.sbd = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						cSkillCfgs.sbd.add(excelReadContext.getDoubleValue(row, col));
						col = col + 2;
					}

					row += 2;
					col = colStart + 3;
					cSkillCfgs.hel = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						cSkillCfgs.hel.add(excelReadContext.getDoubleValue(row, col));
						col = col + 2;
					}

					row += 2;
					col = colStart + 3;
					cSkillCfgs.shell = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						cSkillCfgs.shell.add(excelReadContext.getDoubleValue(row, col));
						col = col + 2;
					}

					row += 2;
					col = colStart + 3;
					cSkillCfgs.spa = excelReadContext.getIntValue(row, col);
					
					row ++;
					col = colStart + 2;
					cSkillCfgs.invalidSkillIDs = excelReadContext.getIntegerList(row, col, ";");
					col += 2;
					List<Integer> stupidTimes = excelReadContext.getIntegerList(row, col, ";");
					cSkillCfgs.stupidMin = stupidTimes.get(0);
					cSkillCfgs.stupidMax = stupidTimes.get(1);
					col += 2;
					cSkillCfgs.bloodrate = excelReadContext.getFloatValue(row, col);
					
					row++;
					col = colStart + 2;
					cSkillCfgs.attackEdge = excelReadContext.getIntValue(row, col);
					col += 2;
					cSkillCfgs.closeAngle = (float) (excelReadContext.getIntValue(row, col)/180.f * Math.PI);
					col += 2;
					cSkillCfgs.farAngle = (float) (excelReadContext.getIntValue(row, col)/180.f * Math.PI);
					row++;
					col = colStart + 2;
					cSkillCfgs.percentBuffMax = excelReadContext.getIntValue(row, col);
					col += 2;
					cSkillCfgs.stepTrigCondition = excelReadContext.getFloatValue(row, col);
					col += 2;
					cSkillCfgs.stepTrigOdds = excelReadContext.getFloatValue(row, col);
					row += 2;
					col = colStart + 2;
					cSkillCfgs.skillPresetMaxSize = excelReadContext.getIntValue(row, col);
					col += 2;
					cSkillCfgs.spiritsPresetMaxSize = excelReadContext.getIntValue(row, col);
					
					row = rowStart + 21;
					col = colStart + 2;
					cSkillCfgs.privateMapMasterMaxCount = excelReadContext.getIntValue(row, col);
					cSkillCfgs.weaponMasterInterval = excelReadContext.getIntValue(row, col + 2);
					cfg.skill = cSkillCfgs;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "内甲战斗系数");
					int row = rowStart + 1;
					int col = colStart + 3;
					
					SBean.CommonArmorCFGS armorCfg = new SBean.CommonArmorCFGS();
					armorCfg.transParam1 = excelReadContext.getIntValue(row, col);
					armorCfg.transParam2 = excelReadContext.getIntValue(row, col + 2);
					row++;
					col = 2;
					armorCfg.transRate = excelReadContext.getIntValue(row, col) / 10_000.f;
					
					row++;
					col = 3;
					armorCfg.defParam1 = excelReadContext.getIntValue(row, col);
					armorCfg.defParam2 = excelReadContext.getIntValue(row, col + 2);
					
					row++;
					col = 2;
					armorCfg.dmgDeep = excelReadContext.getIntValue(row, col) / 10_000.f;
					
					row++;
					armorCfg.recoverInterval = excelReadContext.getIntValue(row, col);
					
					row++;
					armorCfg.freezeTime = excelReadContext.getIntValue(row, col);

					row++;
					armorCfg.suck = new SBean.ArmorDeepCFG();
					armorCfg.suck.interval = excelReadContext.getIntValue(row, col);
					armorCfg.suck.add = excelReadContext.getIntValue(row, col + 2) / 10_000.f;
					
					row++;
					armorCfg.destroy = new SBean.ArmorDeepCFG();
					armorCfg.destroy.interval = excelReadContext.getIntValue(row, col);
					armorCfg.destroy.add = excelReadContext.getIntValue(row, col + 2) / 10_000.f;
					
					row++;
					armorCfg.weak = new SBean.ArmorDeepCFG();
					armorCfg.weak.interval = excelReadContext.getIntValue(row, col);
					armorCfg.weak.add = excelReadContext.getIntValue(row, col + 2) / 10_000.f;
					
					col = 3;
					row++;
					armorCfg.transOddMin =  excelReadContext.getIntValue(row, col) / 10_000.f;
					armorCfg.transOddMax =  excelReadContext.getIntValue(row, col + 2) / 10_000.f;
					
					row++;
					armorCfg.dmgFixMin =  excelReadContext.getIntValue(row, col) / 10_000.f;
					armorCfg.dmgFixMax =  excelReadContext.getIntValue(row, col + 2) / 10_000.f;
					cfg.armor = armorCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "药水相关");
					int row = rowStart + 1;
					int col = colStart + 2;

					SBean.CommonMedicineCFGS medicineCfg = new SBean.CommonMedicineCFGS();
					medicineCfg.vipTripInterval = excelReadContext.getIntValue(row, col);
					col += 2;
					medicineCfg.hpCD = excelReadContext.getIntValue(row, col);
					cfg.medicine = medicineCfg;
				}

				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "装备相关");
					int row = rowStart + 1;
					int col = 2;
					SBean.CommonEquipCFGS equipCFGS = new SBean.CommonEquipCFGS();
					equipCFGS.probability = excelReadContext.getFloatValue(row, col);
					if (equipCFGS.probability < 0 || equipCFGS.probability > 1)
						throw new Exception("水晶装备生成概率不在0和1之间!");
					col += 2;
					equipCFGS.initDurability = excelReadContext.getIntValue(row, col);
					col += 2;
					equipCFGS.disableValue = excelReadContext.getIntValue(row, col);
					if (equipCFGS.disableValue > equipCFGS.initDurability)
						throw new Exception("谁将装备失效值大于初始值！");
					col += 2;
					equipCFGS.propertiesAdd = excelReadContext.getDoubleValue(row, col);
					col += 2;

					equipCFGS.fixCostCoin = excelReadContext.getFloatValue(row, col);
					col += 2;
					equipCFGS.useSkillLoss = excelReadContext.getIntValue(row, col);
					cfg.equip = equipCFGS;
					
					row++;
					col = 2;
					equipCFGS.awardPartNum = excelReadContext.getIntValue(row, col);
					col += 2;
					equipCFGS.awardSlotNum = excelReadContext.getIntValue(row, col);
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "副本相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonMapCFGS mapCfg = new SBean.CommonMapCFGS();
					mapCfg.dropClearTime = new ArrayList<>();
					for (int i = 0; i < 5; i++)
					{
						mapCfg.dropClearTime.add(excelReadContext.getIntValue(row, col++));
					}
					row++;
					col = colStart + 2;
					mapCfg.palteCount = excelReadContext.getIntValue(row, col);
					col += 2;
					mapCfg.mapCopyAutoFlipCardTime = excelReadContext.getIntValue(row, col);
					col += 2;
					mapCfg.mapCopyAutoCloseTime = excelReadContext.getIntValue(row, col);

					row++;
					col = colStart + 2;
					mapCfg.lotteryCostDiamond = excelReadContext.getIntValue(row, col);
					if (mapCfg.lotteryCostDiamond <= 0)
						throw new Exception("副本扫荡消耗钻石小于0！");
					col += 2;
					mapCfg.sweepItemId = excelReadContext.getIntValue(row, col);
					this.checkEntityIDValid(gdCfgs, row, mapCfg.sweepItemId, false);
					
					row++;
					col = colStart + 2;
					mapCfg.mapcopyFinishArgs = excelReadContext.getIntegerList(row, col, ";");
					col += 2;
					mapCfg.primapFinishArgs = excelReadContext.getFloatList(row, col, ";");
					col += 2;
					mapCfg.teammapFinishArgs = excelReadContext.getFloatList(row, col, ";");
					col += 2;
					mapCfg.scores = excelReadContext.getIntegerList(row, col, ";");
					
					row++;
					col = colStart + 2;
					mapCfg.buyUseDiamond = excelReadContext.getIntegerList(row, col, ";");
					
					row++;
					col = colStart + 2;
//					mapCfg.mapCopySlowMotionTime = excelReadContext.getIntValue(row, col);
					row++;
					col = colStart + 2;
					mapCfg.mapCopyNoAnimationWaitPopupTime = excelReadContext.getIntValue(row++, col);
					mapCfg.forbidReviveMaps = new HashSet<>();
					mapCfg.forbidUseHpPoolMaps =  new HashSet<>();
					List<Integer> forbidReviveMaps = excelReadContext.getIntegerList(row++, col, ";");
					for(int mType: forbidReviveMaps)
						mapCfg.forbidReviveMaps.add(mType);
					List<Integer> forbidUseHpPoolMaps = excelReadContext.getIntegerList(row++, col, ";");
					for(int mType: forbidUseHpPoolMaps)
						mapCfg.forbidUseHpPoolMaps.add(mType);
					
					cfg.map = mapCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "武将相关");
					int row = rowStart + 2;
					int col = colStart + 2;
					SBean.CommonGeneralCFGS generalCfg = new SBean.CommonGeneralCFGS();
					generalCfg.maxSP = excelReadContext.getIntValue(row, col);
					generalCfg.fightTime = excelReadContext.getIntValue(row, col + 4);
					cfg.general = generalCfg;
				}
				

				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "采矿相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonMineralCFGS mineralCfg = new SBean.CommonMineralCFGS();
					mineralCfg.mineralDistance = excelReadContext.getIntValue(row, col);
					col = col + 2;
					mineralCfg.appearRadius = excelReadContext.getIntValue(row, col);
					cfg.mineral = mineralCfg;
				}
				
				{
					// 佣兵单人副本栏位解锁顺序
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "佣兵相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonPetCFGS petCfg = new SBean.CommonPetCFGS();
					petCfg.level = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						petCfg.level.add(excelReadContext.getIntValue(row, col + i * 2));
					}
					
					col = 8;
					petCfg.autoRevive = excelReadContext.getIntValue(row, col);
					row += 2;
					col = colStart + 2;
					petCfg.maxCoPracticeTaskNum = excelReadContext.getIntValue(row, col);
					col += 2;
					petCfg.coPracticeOpenLvl = excelReadContext.getIntValue(row, col);
					row++;
					col = colStart + 2;
					petCfg.autoUsePoolHp = excelReadContext.getFloatValue(row, col);
					col += 2;
					petCfg.poolCD = excelReadContext.getIntValue(row, col);
					col += 2;
					petCfg.healPerPool = excelReadContext.getIntValue(row, col);
					row++;
					col = 2;
					petCfg.spiritLvlReq = excelReadContext.getIntValue(row++, col);
					petCfg.normalArenaExploit = excelReadContext.getIntValue(row, col);
					petCfg.bwArenaExploit = excelReadContext.getIntValue(row, col + 2);
					petCfg.superArenaExploit = excelReadContext.getIntValue(row, col + 4);
					row = rowStart + 7;
					col = colStart +2;
					petCfg.fullStarPropAdd = excelReadContext.getIntValue(row, col);
					
					cfg.pet = petCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "战力相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonFightPowerCFGS fightPowerCfg = new SBean.CommonFightPowerCFGS();
					fightPowerCfg.args = new ArrayList<>();
					fightPowerCfg.dmgs = new ArrayList<>();
					for (int i = 0; i < 2; i++)
						fightPowerCfg.args.add(excelReadContext.getFloatValue(row, col + i * 2));

					for (int i = 2; i < 4; i++)
						fightPowerCfg.dmgs.add(excelReadContext.getIntValue(row, col + i * 2));

					fightPowerCfg.spirit = excelReadContext.getIntValue(row, col + 8);
					fightPowerCfg.weapon = excelReadContext.getIntValue(row, col + 10);
					fightPowerCfg.atkIFAndStep = excelReadContext.getFloatValue(row, col + 12);
					
					row++;
					col = colStart + 2;
					fightPowerCfg.seal = new ArrayList<>();
					for(int i=0; i<3; i++)
						fightPowerCfg.seal.add(excelReadContext.getFloatValue(row, col + i * 2));
					
					row++;
					col = colStart + 2;
					fightPowerCfg.maxHPFac = excelReadContext.getFloatValue(row, col);
					fightPowerCfg.atkNFac = excelReadContext.getFloatValue(row, col + 2);
					fightPowerCfg.defNFac = excelReadContext.getFloatValue(row, col + 4);
					cfg.fightPower = fightPowerCfg;

					final int rowTeam = excelReadContext.locateColumnTag(colStart, "组队相关");
					row = rowTeam + 1;
					col = colStart + 2;
					SBean.CommonTeamCFGS teamCfg = new SBean.CommonTeamCFGS();
					teamCfg.killMonsterExpAdd = excelReadContext.getFloatValue(row, col);
					col += 2;
					teamCfg.expAddDistance = excelReadContext.getIntValue(row, col);
					
					row = rowTeam + 2;
					col = 2;
					teamCfg.lvlReq = excelReadContext.getIntValue(row, col);
					
					cfg.team = teamCfg;
				}
				
				{
					// 体力相关
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "体力相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonVitCFGS vitCFGS = new SBean.CommonVitCFGS();
					vitCFGS.revertValue = excelReadContext.getIntValue(row, col);
					col += 2;
					vitCFGS.revertInterval = excelReadContext.getIntValue(row, col);
					row += 1;
					col = colStart + 2;
					vitCFGS.buyVitValue = excelReadContext.getIntValue(row, col);
					row += 1;
					col = colStart + 2;
					vitCFGS.buyTimesCost = excelReadContext.getIntegerList(row, col, ";");
					cfg.vit = vitCFGS;
				}
				
				{
					// 帮派相关
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "帮派相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonSectCFGS sectCFGS = new SBean.CommonSectCFGS();
					sectCFGS.createLvl = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.joinLvlReq = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.diamond = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.coin = excelReadContext.getIntValue(row, col);
					
					//
					row += 2;
					col = colStart + 2;
					sectCFGS.sectMaxApplicant = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.dayFinishTask = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.exitPunishTime = excelReadContext.getIntegerList(row, col, ";");
					col += 2;
					sectCFGS.eventMax = excelReadContext.getIntValue(row, col);
					
					//
					row += 2;
					col = colStart + 2;
					sectCFGS.fastApplyActive = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.fastApplyRole = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.sectUpLvlDiamond = excelReadContext.getIntValue(row, col);
					
					//
					row += 2;
					col = colStart + 2;
					sectCFGS.banOpenTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col));
					col += 2;
					sectCFGS.banEndTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col));
					col += 2;
					sectCFGS.mapOpenLvl = excelReadContext.getIntValue(row, col);
					
					//
					row += 2;
					col = colStart + 2;
					sectCFGS.mapAutoCloseTime = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.creedLength = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.eatTotal = excelReadContext.getIntValue(row, col);
					col += 2;
					String taskShareStarString = excelReadContext.getStringValue(row, col);
					String[] strs = taskShareStarString.split(";");
					sectCFGS.taskShareStar = new ArrayList<>();
					for (String s : strs)
					{
						sectCFGS.taskShareStar.add(Integer.valueOf(s));
					}
					
					//
					row += 2;
					col = colStart + 2;
					String taskResetTimeString = excelReadContext.getStringValue(row, col);
					sectCFGS.taskVipMax = GameTime.parseSecondOfDay(taskResetTimeString);
					col += 2;
					String taskRefreshTimeString = excelReadContext.getStringValue(row, col);
					String[] strs2 = taskRefreshTimeString.split(";");
					sectCFGS.taskRefreshTime = new ArrayList<>();
					for (String s : strs2)
					{
						sectCFGS.taskRefreshTime.add(GameTime.parseSecondOfDay(s));
					}
					col += 2;
					sectCFGS.taskShareLimitTime = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.taskShareMakeCount = excelReadContext.getIntValue(row, col);
					
					//
					row += 2;
					col = colStart + 2;
					sectCFGS.taskLibDefaultCount = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.taskLibAddCount = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.taskOpenLvl = excelReadContext.getIntValue(row, col);
					
					//
					row += 2;
					col = colStart + 2;
					sectCFGS.mapRecordsAllocateStartTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col));
					col += 2;
					sectCFGS.mapRecordsAllocateEndTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col));
					col += 2;
					sectCFGS.taskCancelPunishTime = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.sectTaskDiamond = new ArrayList<>();
					String sectTaskDiamondStr = excelReadContext.getStringValue(row, col);
					for(String s : sectTaskDiamondStr.split(";")){
						sectCFGS.sectTaskDiamond.add(Integer.valueOf(s));
					}
					
					//
					row += 2;
					col = colStart + 2;
					sectCFGS.maxDayVitality = excelReadContext.getIntValue(row, col);
					col += 2;
					sectCFGS.changeNameDiamond = excelReadContext.getIntValue(row, col);

					row += 2;
					col = colStart + 2;
					sectCFGS.groupMapFinishEndTime = excelReadContext.getIntValue(row, col);
					row += 2;
					sectCFGS.chiefTimeout = excelReadContext.getIntValue(row, col);
					row += 2;
					sectCFGS.receiveChiefLastTime = excelReadContext.getIntValue(row, col);
					row += 2;
					sectCFGS.qqGroupLength = excelReadContext.getIntValue(row, col);
					row += 2;
					sectCFGS.startVit = excelReadContext.getIntValue(row, col);
					if(sectCFGS.startVit < 0)
						throw new Exception("通用配置表 帮派起始活跃度 row " + row + " 的值 " + sectCFGS.startVit + " 不能小于0！");

					cfg.sect = sectCFGS;
				}
				
//				{
//					// 复活（还缺玩家复活）
//					final int colStart = 0;
//					final int rowStart = excelReadContext.locateColumnTag(colStart, "随从复活");
//					int row = rowStart + 1;
//					int col = colStart + 2;
//					SBean.CommonRevivesCFGS reviveCfg = new SBean.CommonRevivesCFGS();
//					reviveCfg.petMoneyArg1 = excelReadContext.getFloatValue(row, col);
//					reviveCfg.petMoneyArgs = new ArrayList<Integer>();
//					for (int i = 1; i < 4; i++)
//					{
//						reviveCfg.petMoneyArgs.add(excelReadContext.getIntValue(row, col + 2 * i));
//					}
//					row++;
//					reviveCfg.petStoneCost = excelReadContext.getIntValue(row, col);
//					cfg.revives = reviveCfg;
//				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "复活相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonRevivesCFGS reviveCfg = new SBean.CommonRevivesCFGS();
					reviveCfg.spawnReviveCD = excelReadContext.getIntValue(row, col);
					reviveCfg.pvpReviveCostBase = excelReadContext.getIntValue(row, col + 2);
					reviveCfg.pvpReviveCostAdd = excelReadContext.getIntValue(row, col + 4);
					reviveCfg.clearRecentReviveTime = excelReadContext.getIntValue(row, col + 6);
					
					row++;
					reviveCfg.protectTime = excelReadContext.getIntValue(row, col);
					cfg.revives = reviveCfg;
					
					row++;
					reviveCfg.pveReviveMapTypes = new HashSet<>();
					List<Integer> pveMapTypes = excelReadContext.getIntegerList(row, col, ";");
					for(int type: pveMapTypes)
						reviveCfg.pveReviveMapTypes.add(type);
					reviveCfg.pveReviveCost = excelReadContext.getIntValue(row, col + 2);
				}
				
				{
					// PK相关
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "PK相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					int i = 0;
					SBean.CommonPKCFGS pkCfg = new SBean.CommonPKCFGS();
					pkCfg.needLvl = excelReadContext.getIntValue(row, col + (i++) * 2);
					row++;
					i = 0;
					col = colStart + 2;
					pkCfg.pkKeepTime = excelReadContext.getIntValue(row, col + (i++) * 2);
					pkCfg.orangeNameKeepTime = excelReadContext.getIntValue(row, col + (i++) * 2);
					pkCfg.pkValueInterval = excelReadContext.getIntValue(row, col + (i++) * 2);
					row++;
					pkCfg.punishSkill = excelReadContext.getIntValue(row, col);
					checkSkillIDAndLevelValid(gdCfgs, row, pkCfg.punishSkill, 1);
					pkCfg.punishReduceValue = excelReadContext.getIntValue(row, col + 2);
					pkCfg.punishReq = excelReadContext.getIntValue(row, col + 4);
					if(pkCfg.punishReq <= pkCfg.punishReduceValue)
						throw new Exception("row " + row + " 天雷惩罚目标最小善恶值 " + pkCfg.punishReq + " 小于天雷惩罚后减少善恶值 " + pkCfg.punishReduceValue);
					pkCfg.punishInterval = excelReadContext.getIntValue(row, col + 6);
					pkCfg.punishDamageBuff = excelReadContext.getIntValue(row, col + 8);
					checkBuffIDValid(gdCfgs, row, pkCfg.punishDamageBuff);
					
					cfg.pk = pkCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "购买金币相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonCoinCFGS coincfg = new SBean.CommonCoinCFGS();
					coincfg.buyBaseAmount = excelReadContext.getIntValue(row++, col);
					coincfg.buyRoleLvlAmount = excelReadContext.getIntValue(row++, col);
					coincfg.buyTimesAmount = excelReadContext.getIntValue(row++, col);
					coincfg.buyCrits = new ArrayList<>();
					{
						String str = excelReadContext.getStringValue(row++, col);
						String[] strs = str.split(";");
						int sum = 0;
						for (String e : strs)
						{
							String[] v = e.split(":");
							int multiplier = Integer.parseInt(v[0]);
							int weight = Integer.parseInt(v[1]);
							sum += weight;
							coincfg.buyCrits.add(new SBean.CritCFGS(multiplier, weight));
						}
						float sp = 0;
						for (SBean.CritCFGS e : coincfg.buyCrits)
						{
							sp += e.probability / sum;
							e.probability = sp;
						}
					}
					coincfg.buyTimesCost = excelReadContext.getIntegerList(row++, col, ";");
					cfg.coin = coincfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "活动相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonActivityCFGS activityCfg = new SBean.CommonActivityCFGS();
					activityCfg.bossTransCD = excelReadContext.getIntValue(row, col);
					activityCfg.resetDiamond = excelReadContext.getIntValue(row, col+2);
					activityCfg.transItemID = excelReadContext.getIntValue(row, col+4);
					
					row = row + 2;
					col = colStart + 2;
					activityCfg.buyUseDiamond = excelReadContext.getIntegerList(row, col, ";");
					
					cfg.activity = activityCfg;
				}
				
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "神兵任务");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonWeaponTaskCFGS weaponTaskCfg = new SBean.CommonWeaponTaskCFGS();
					weaponTaskCfg.dayLoopCount = excelReadContext.getIntValue(row, col);
					
					cfg.weaponTask = weaponTaskCfg;
				}
				
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "心法相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonSpiritCFGS spiritCfg = new SBean.CommonSpiritCFGS();
					spiritCfg.jobTotal = excelReadContext.getIntValue(row, col);
					col += 2;
					spiritCfg.gangTotal = excelReadContext.getIntValue(row, col);
					col += 2;
					spiritCfg.bulimTotal = excelReadContext.getIntValue(row, col);
					cfg.spirits = spiritCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "寄售行");
					int row = rowStart + 2;
					int col = colStart + 2;
					SBean.CommonAuctionCFGS auctionCfg = new SBean.CommonAuctionCFGS();
					auctionCfg.needLvl = excelReadContext.getIntValue(row++, col);
					auctionCfg.openTime = excelReadContext.getIntValue(row++, col);
					auctionCfg.consignTime = excelReadContext.getIntValue(row++, col) * 3600 * 24;
					auctionCfg.expandCost = excelReadContext.getIntegerList(row++, col, ";");
					int downCount = excelReadContext.getIntValue(row++, col);
					int upCount = excelReadContext.getIntValue(row++, col);
					int percentUnit = excelReadContext.getIntValue(row++, col);
					auctionCfg.minPercent = 1.f - (downCount * percentUnit) / 100.f;
					auctionCfg.maxPercent = 1.f + (upCount * percentUnit) / 100.f;
					
					auctionCfg.freeAmount = excelReadContext.getIntValue(row++, col);
					auctionCfg.taxRate = excelReadContext.getIntValue(row++, col) / 100.f;
					auctionCfg.buyCreditRate = excelReadContext.getFloatValue(row++, col);
					auctionCfg.putOnDelayMin = excelReadContext.getIntValue(row++, col);
					auctionCfg.putOnDelayMax = excelReadContext.getIntValue(row++, col);
					if(auctionCfg.putOnDelayMin > auctionCfg.putOnDelayMax)
						throw new Exception("row " + row + " 上架延时时间下限 " + auctionCfg.putOnDelayMin + " 大于上架延时时间上限 " + auctionCfg.putOnDelayMax);
					
					if(auctionCfg.putOnDelayMax > auctionCfg.consignTime)
						throw new Exception("row " + row + " 上架延时时间上限 " + auctionCfg.putOnDelayMax + " 大于寄售物品存在时长 " + auctionCfg.consignTime);
					
					auctionCfg.costCreditTypes = new HashSet<>();
					List<Integer> costCreditTypes = excelReadContext.getIntegerList(row, col, ";");
					for(int t: costCreditTypes)
						auctionCfg.costCreditTypes.add(t);
					cfg.auction = auctionCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "帮派邮件");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonSectMailCFGS mailCfg = new SBean.CommonSectMailCFGS();
					mailCfg.freeCount = excelReadContext.getIntValue(row++, col);
					mailCfg.noFreeUseMoney = excelReadContext.getIntValue(row++, col);
					mailCfg.maxContentLength = excelReadContext.getIntValue(row++, col);
					cfg.sectMail = mailCfg;
				}
				
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "好友相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonFriendCFGS friendCfg = new SBean.CommonFriendCFGS();
					friendCfg.cdTime = excelReadContext.getIntValue(row++, col);
					friendCfg.itemId = excelReadContext.getIntValue(row++, col);
					friendCfg.maxCount = excelReadContext.getIntValue(row++, col);
					friendCfg.heartLength = excelReadContext.getIntValue(row++, col);
					friendCfg.addExp = excelReadContext.getIntValue(row++, col);
					friendCfg.focusDown = excelReadContext.getIntValue(row++, col);
					friendCfg.focusUp = excelReadContext.getIntValue(row++, col);
					friendCfg.focusDefault = excelReadContext.getIntValue(row++, col);
					friendCfg.focusAdd = excelReadContext.getIntValue(row++, col);
					friendCfg.maxEnemies = excelReadContext.getIntValue(row++, col);
					friendCfg.maxBlackList = excelReadContext.getIntValue(row++, col);
					cfg.friend = friendCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "游戏设置");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonGameSetCFGS gamesetCfg = new SBean.CommonGameSetCFGS();
					gamesetCfg.wrongPosLeaveCD = excelReadContext.getIntValue(row, col);
					
					cfg.gameset = gamesetCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "任务通用");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonTaskCFGS taskCfg = new SBean.CommonTaskCFGS();
					taskCfg.npcRadius = excelReadContext.getIntValue(row, col);
					cfg.task = taskCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "送花");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonFlowerCFGS flowerCfg = new SBean.CommonFlowerCFGS();
					flowerCfg.flowerId = excelReadContext.getIntValue(row++, col);
					flowerCfg.acceptMaxCount = excelReadContext.getIntValue(row++, col);
					flowerCfg.acceptShowCount = excelReadContext.getIntValue(row++, col);
					flowerCfg.giveMaxCount = excelReadContext.getIntValue(row++, col);
					flowerCfg.giveShowCount = excelReadContext.getIntValue(row++, col);
					cfg.flower = flowerCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "称号相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					SBean.CommonTitleCFGS titleCfg = new SBean.CommonTitleCFGS(0, 0, new ArrayList<Integer>(GameData.MAX_TITLESLOT_SIZE));
					titleCfg.timedTitleCnt = excelReadContext.getIntValue(row++, col);
					row++;
					titleCfg.initTitle = excelReadContext.getIntValue(row++, col);
					if(titleCfg.initTitle > 0)
						checkTitleIDValid(gdCfgs, row, titleCfg.initTitle);
					titleCfg.unlockCfgs = excelReadContext.getIntegerList(row++, col, ";");
					cfg.title = titleCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "怪物经验惩罚");
					int row = rowStart + 1;
					int col = colStart + 1;
					
					SBean.CommonMonsterExpCFGS meCfg = new SBean.CommonMonsterExpCFGS(new HashSet<>(), new ArrayList<>());
					List<Integer> effectTypes = excelReadContext.getIntegerList(row++, col + 1, ";");
					for(int type: effectTypes)
					{
						if(!meCfg.effectMapTyps.add(type))
							throw new Exception("通用配置表 怪物经验惩罚 row " + row + " 支持的地图类型列表 " + type + " 重复！");
					}
					
					row = rowStart + 3;
					Integer lastLvl = null;
					while(excelReadContext.isNotEmpty(row, 1))
					{
						col = colStart + 1;
						int lvl = excelReadContext.getIntValue(row, col++);
						meCfg.dLvls.add(new SBean.MonsterExp(lvl, excelReadContext.getIntValue(row, col++) / 10_000.f));
						if(lastLvl != null && lastLvl > lvl)
							throw new Exception("怪物经验惩罚 等级差 " + lvl + " 非法!");
							
						lastLvl = lvl;
						row++;
					}
					cfg.monsterExp = meCfg;
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "生产系统");
					int row = rowStart + 1;
					int col = colStart + 1;
					cfg.produce = new HashSet<Integer>();
					List<Integer> recipes = excelReadContext.getIntegerList(row, col, ";");
					for(int rid: recipes)
					{
						checkClanRecipeIDValid(gdCfgs, row, rid);
						cfg.produce.add(rid);
					}
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "改名相关");
					int row = rowStart;
					int col = colStart + 2;
					cfg.rename = new SBean.CommonRenameCFGS(0, 0);
					cfg.rename.diamondCost = excelReadContext.getIntValue(row, col);
					cfg.rename.costItemID = excelReadContext.getIntValue(row, col + 2);
					checkEntityIDValid(gdCfgs, row, cfg.rename.costItemID, false);
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "日程表");
					int row = rowStart + 2;
					int col = colStart + 2;
					
					cfg.activityMax = excelReadContext.getIntValue(row, col);
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "仓库相关");
					int row = rowStart + 1;
					int col = colStart + 2;
					cfg.warehouse = new SBean.CommonWarehouseCFGS();
					cfg.warehouse.expandCells = excelReadContext.getIntValue(row++, col);
					cfg.warehouse.priavetExpandCost = excelReadContext.getIntegerList(row++, col, ";");
					cfg.warehouse.priavetExpandSize = excelReadContext.getIntValue(row++, col);
					cfg.warehouse.publicExpandCost = excelReadContext.getIntegerList(row++, col, ";");
					cfg.warehouse.publicExpandSize = excelReadContext.getIntValue(row++, col);
					cfg.warehouse.privateDefaultSize = excelReadContext.getIntValue(row++, col);
					if (cfg.warehouse.privateDefaultSize <= 0)
						throw new Exception("仓库配置私有仓库默认大小 " + cfg.warehouse.privateDefaultSize + " 必须大于0");
					cfg.warehouse.privateUnlockPrice = excelReadContext.getIntValue(row++, col);
					if (cfg.warehouse.privateUnlockPrice <= 0)
						throw new Exception("仓库配置私有仓库解锁价格 " + cfg.warehouse.privateUnlockPrice + " 必须大于0");
					cfg.warehouse.unlockLevel = excelReadContext.getIntValue(row++, col);
					if (cfg.warehouse.unlockLevel <= 0)
						throw new Exception("仓库配置可解锁等级 " + cfg.warehouse.unlockLevel + " 必须大于0");
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "体力补领");
					int row = rowStart + 1;
					int col = colStart + 2;
					cfg.vipMissVitGet = new SBean.VipMissVitGetCFGS();
					cfg.vipMissVitGet.itemId = excelReadContext.getIntValue(row++, col);
					cfg.vipMissVitGet.itemOfferVit = excelReadContext.getIntValue(row++, col);
					cfg.vipMissVitGet.getMaxTimes = excelReadContext.getIntValue(row++, col);
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "功能开启");
					int row = rowStart + 1;
					int col = colStart + 2;
					cfg.funcOpen = new SBean.CommonFuncOpenCFGS();
					cfg.funcOpen.equipRefine = excelReadContext.getIntValue(rowStart + 4, col);
				}
				
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "打坐相关");
					int row = rowStart + 1;
					cfg.socialAction = new SBean.CommonSocialActionCFGS(0, 0, new HashSet<>());
					
					cfg.socialAction.autoActionTime = excelReadContext.getIntValue(row, colStart + 2);
					cfg.socialAction.autoActionID = excelReadContext.getIntValue(row, colStart + 4);
					if(!gdCfgs.socialActions.containsKey(cfg.socialAction.autoActionID))
						throw new Exception("row " + row + " 打坐对应社交动作ID " + cfg.socialAction.autoActionID + " 非法");
					
					List<Integer> mapTypes = excelReadContext.getIntegerList(row, colStart + 6, ";");
					for(int mapType: mapTypes)
						cfg.socialAction.effectMapTypes.add(mapType);
				}
				
				{
					final int colStart = 1;
					final int rowStart = excelReadContext.locateColumnTag(0, "下载资源分包配置");
					int row = rowStart;
					int col = colStart + 1;
					cfg.packetReward = new SBean.CommonPacketRewardCFGS();
					cfg.packetReward.levelNeeds = excelReadContext.getIntegerList(row, col, ",");
					for (int i = 0; i < cfg.packetReward.levelNeeds.size() - 1; i++)
					{
						if (cfg.packetReward.levelNeeds.get(i) > cfg.packetReward.levelNeeds.get(i + 1))
							throw new Exception("下载资源分包配置等级配置必须递增");
					}
					row += 2;
					col = colStart;
					cfg.packetReward.rewards = new ArrayList<>();
					while (excelReadContext.isNotEmpty(row, col))
					{
						List<Integer> item = excelReadContext.getIntegerList(row, col++, ",");
						if (item.size() != 2)
							throw new Exception("row " + row + " col " + (col - 1) + " 奖励道具配置错误");
						if (item.get(0) > 0)
						{
							checkEntityIDValid(gdCfgs, row, item.get(0), false);
							if (item.get(1) > 0)
								cfg.packetReward.rewards.add(new SBean.DummyGoods(item.get(0), item.get(1)));
						}
					}
					if (cfg.packetReward.levelNeeds.size() != cfg.packetReward.rewards.size())
						throw new Exception("下载资源分包配置等级与奖励数量不匹配");
				}
				
				{
					final int rowSocialCommen = excelReadContext.locateColumnTag(0, "宠物神兵评价");
					int row = rowSocialCommen + 1;
					int col = 2;
					cfg.socialComment = new SBean.CommonSocialCommentCFGS(0, 0, 0, 0, 0, 0, 0, 0, 0);
					cfg.socialComment.lvlReq = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.latestCount = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.likeCount = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.dislikeCount = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.minWords = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.maxWords = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.dayLikeTimes = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.dayDislikeTimes = excelReadContext.getIntValue(row++, col);
					cfg.socialComment.daySendTimes = excelReadContext.getIntValue(row++, col);
				}
				
				{
					final int rowSocialCommen = excelReadContext.locateColumnTag(0, "抽奖类背包空格限制");
					int row = rowSocialCommen + 1;
					int col = 2;
					cfg.bagSizeNeed = new SBean.BagSizeNeedCFGS(0, 0, 0);
					cfg.bagSizeNeed.wheelNeeds = excelReadContext.getIntValue(row++, col);
					cfg.bagSizeNeed.prayNeeds = excelReadContext.getIntValue(row++, col);
					cfg.bagSizeNeed.gambleShopNeeds = excelReadContext.getIntValue(row++, col);
				}
				
				gdCfgs.common = cfg;
			}
		}
		System.out.println("load table " + fileName + " success.");
		
	}
	
	private void checkAllCFGS(SBean.GameDataCFGS cfgs) throws Exception
	{
		System.out.println("开始检查数据引用关系 ...");

		//测试道具
		for (SBean.ItemCFGS e : cfgs.items.values())
		{
			switch (e.type)
			{
				case GameData.GAME_ITEM_TYPE_DIAMOND:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为钻石包，内含钻石数目 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_COIN:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为金币包，内含钻石数目 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_EXP:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为经验丹，内含经验值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_GIFT:
					if (!cfgs.gifts.containsKey(e.arg1))
						throw new Exception("道具 " + e.id + " 的类型为礼包, 礼包id " + e.arg1 + " 不存在！");
					
					if(e.arg2 != 0)
						checkItemIDValid(cfgs, e.arg2);
					break;
				case GameData.GAME_ITEM_TYPE_RECIPEREEL:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为配方卷轴，内含配方ID " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_HP:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为血瓶，内含血量值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_MATERIAL:
					break;
				case GameData.GAME_ITEM_TYPE_HPPOOL:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为vip血药，内含血量值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_CHEST:
					if (!cfgs.randomDropTbl.containsKey(e.arg1))
						throw new Exception("道具 " + e.id + " 的类型为宝箱，内含随机掉落表ID " + e.arg1 + " 不存在！");
					
					if(e.arg2 != 0)
						checkItemIDValid(cfgs, e.arg2);
					break;
				case GameData.GAME_ITEM_TYPE_TASK_EFFECT:
					if (e.arg1 < 0)
						throw new Exception("道具 " + e.id + " 的类型为任务道具，内含特效ID " + e.arg1 + " 小于0！");
					
					if (e.arg5 > 0 && !cfgs.buffs.containsKey(e.arg5))
						throw new Exception("道具 " + e.id + " 的类型为任务道具，BUFFID " + e.arg5 + " 非法！");
						
					break;
				case GameData.GAME_ITEM_TYPE_WEAPON_EXP:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为神兵升级经验丹，内含经验值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_PET_EXP:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为佣兵升级经验丹，内含经验值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_EQUIP_ENERGY:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为装备能量丹，内含能量值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_GEM_ENERGY:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为宝石能量丹，内含能量值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_SPIRIT_INSPIRATION:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为心法悟性丹，内含悟性值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_VIT:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为体力丹，内含体力值 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_FASHION:
					if(!cfgs.fashions.containsKey(e.arg1))
						throw new Exception("道具 " + e.id + " 的类型为时装， 内含时装ID " + e.arg1 + " 非法！");
					break;
				case GameData.GAME_ITEM_TYPE_FLOWER:
					if(e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为送花， 内含玫瑰花ID " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_EXPCOIN_POOL:
					if(e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为历练ping， 内含瓶子容量 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_RARE_BOOK:
					break;
				case GameData.GAME_ITEM_TYPE_SPECIAL_CARD:
					if(e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为月卡， 包含时间 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_VIP_CARD:
					if(e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为VIP体验卡， 包含时间 " + e.arg1 + " 不大于0！");
					if(e.arg2 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为VIP体验卡， 体验等级 " + e.arg2 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_TOWER_FAME:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为提升爬塔声望道具， 提升声望 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_FEAT:
					if (e.arg1 <= 0)
						throw new Exception("道具 " + e.id + " 的类型为增加武勋道具， 增加武勋的 " + e.arg1 + " 不大于0！");
					break;
				case GameData.GAME_ITEM_TYPE_SKILL:
					if (cfgs.skills.get(e.arg1) == null)
						throw new Exception("道具" + e.id + " 的类型为技能道具，使用道具对应的技能id " + e.arg1 + " 在技能表中不存在相应技能！");
					if (e.arg2 > cfgs.skills.get(e.arg1).lvlDatas.size())
						throw new Exception("道具" + e.id + " 的类型为技能道具，使用道具对应的技能id " + e.arg1 + " 无等级 " + e.arg2);
					break;
				case GameData.GAME_ITEM_TYPE_LETTER:
					if (!(cfgs.branchTask.containsKey(e.arg2) || e.arg2 == 0))
						throw new Exception("道具" + e.id + " 的类型为书信道具，使用道具对应的支线任务组id " + e.arg2 + " 无效！ ");
					break;
				case GameData.GAME_ITEM_TYPE_PIECE:
					if (e.arg1 > cfgs.composes.size())
						throw new Exception("道具" + e.id + " 的类型为碎片道具，道具对应的合成配方id " + e.arg1 + " 无效! ");
					break;
				case GameData.GAME_ITEM_TYPE_ARMOR_EXP_ITEM:
					if (e.arg1 <= 0)
						throw new Exception("道具" + e.id + " 的类型为内甲经验道具，道具提供的内甲经验 " + e.arg1 + " 必须大于0! ");
					break;
				case GameData.GAME_ITEM_TYPE_RUNE:
					if (!cfgs.armor.rune.containsKey(e.id))
						throw new Exception("道具" + e.id + " 的类型为符文道具，道具对应的符文id " + e.id + " 无效! ");
					break;
				case GameData.GAME_ITEM_TYPE_EVIL_VALUE:
					if (e.arg1 < 0)
						throw new Exception("道具" + e.id + " 的类型为善恶值道具，道具扣除的善恶值小于0");
					break;
				case GameData.GAME_ITEM_TYPE_FIREWORK:
					break;
				case GameData.GAME_ITEM_TYPE_ENCHANT:
				{
					SBean.EquipRefineGroupCFGS ergCfg = cfgs.equipRefines.get(e.arg1);
					if(ergCfg == null)
						throw new Exception("道具" + e.id + " 的类型为精炼道具 精炼组ID " + e.arg1 + " 不存在!");
					
					if(e.arg2 <= 0 || e.arg2 > ergCfg.refines.size())
						throw new Exception("道具" + e.id + " 的类型为精炼道具 随机属性数量 " + e.arg2 + " 非法!");
				}
					break;
				case GameData.GAME_ITEM_TYPE_PROP_STRENGTH:
					if (!cfgs.properties.containsKey(e.arg1))
						throw new Exception("道具" + e.id + " 的类型为属性强化道具，道具增加的属性不存在");
					if (e.arg2 <= 0)
						throw new Exception("道具" + e.id + " 的类型为属性强化道具，道具增加的属性值必须大于0");
					break;
				case GameData.GAME_ITEM_TYPE_OFFLINE_FUNC_POINT:
					if (e.arg1 <= 0)
						throw new Exception("道具" + e.id + " 的类型为离线精灵修炼点添加道具，道具增加的修炼点必须大于0");
					break;
				case GameData.GAME_ITEM_TYPE_TITLE_ITEM:
					if (!cfgs.titles.containsKey(e.arg1))
						throw new Exception("道具" + e.id + " 的类型为称号道具，道具对应的称号不存在");
					break;
				case GameData.GAME_ITEM_TYPE_USKILL_ITEM:
					if (!cfgs.uniqueSkills.containsKey(e.arg1))
						throw new Exception("道具" + e.id + " 的类型为绝技道具，道具对应的绝技不存在");
					break;
				case GameData.GAME_ITEM_TYPE_HEAD_ITEM:
					if (!cfgs.headIcons.containsKey(e.arg1))
						throw new Exception("道具" + e.id + " 的类型为头像激活道具，道具对应的头像不存在");
					break;
				default:
					throw new Exception("道具 " + e.id + " 不支持的道具类型 " + e.type + " !");
			}
		}
		
		for(SBean.TrigEventCFGS e: cfgs.trigEvents.values())
		{
			if(e.eventType == GameData.TRIG_EVENT_BUFFCHANGE)
			{
				if(e.param2 > 0 && cfgs.buffs.get(e.param2) == null)
					throw new Exception("触发条件公式 ID " + e.id + " 触发条件 类型ID" + e.eventType + " buffID " + e.param2 + " 非法");
				
			}
		}
		
		for(SBean.TreasureMapCFGS mapCfg: cfgs.treasureMaps.values())
		{
			if(mapCfg.pieceID <= 0)
				throw new Exception("藏宝图 " + mapCfg.id + " 对应的藏宝图碎片 " + mapCfg.pieceID + " 非法!");
		}
		
		for(SBean.MainTaskCFGS cfg: cfgs.mainTasks)
		{
			if(cfg.title > 0 && !cfgs.titles.containsKey(cfg.title))
				throw new Exception("主线任务 " + cfg.id + " 称号奖励 " + cfg.title + " 非法！");
		}
		
		for(SBean.WeaponMapCFGS wMap: cfgs.weaponMaps.values())
		{
			if(!cfgs.weapons.containsKey(wMap.motivateWeapon))
				throw new Exception("神兵特技副本 " + wMap.id + " 永久保持的神兵 " + wMap.motivateWeapon + " 非法！");
		}
		
		for(SBean.TrigEventCFGS event: cfgs.trigEvents.values())
		{
			switch (event.eventType)
			{
				case GameData.TRIG_EVENT_EDEADCOUNT:
				case GameData.TRIG_EVENT_SDEAD:
				case GameData.TRIG_EVENT_SKILLCOUNT:
				case GameData.TRIG_EVENT_SHPLOWER:
				case GameData.TRIG_EVENT_DMGBYCOUNT_D:
				case GameData.TRIG_EVENT_SDMGBY_VALUE:
				case GameData.TRIG_EVENT_ENEMYARROUND:
				case GameData.TRIG_EVENT_LOSEHP:
				case GameData.TRIG_EVENT_IDLE:
				case GameData.TRIG_EVENT_INTERVAL:
				case GameData.TRIG_EVENT_DGMTOFIX:
				case GameData.TRIG_EVENT_BUFFCHANGE:
				case GameData.TRIG_EVENT_DODGE:
				case GameData.TRIG_EVENT_DMGTOCOUNT_D:
				case GameData.TRIG_EVENT_DMGBY_HP:
				case GameData.TRIG_EVENT_DMGTO_THP:
				case GameData.TRIG_EVENT_ANY_MISS:
				case GameData.TRIG_EVENT_DMGTO_STATE:
					break;
				case GameData.TRIG_EVENT_DMGBYCOUNT_I:
					if(event.param1 > 0 && !cfgs.buffs.containsKey(event.param1))
						throw new Exception("触发条件公式 " + event.id + " param1 " + event.param1 + " 非法");
					break;
				case GameData.TRIG_EVENT_WEAPON_MOTIVATE:
					if(event.param2 > 0 && !cfgs.weapons.containsKey(event.param2))
						throw new Exception("触发条件公式 " + event.id + " param2 " + event.param2 + " 非法");
					break;
				default:
					break;
			}
		}
		
		for(SBean.TrigBehaviorCFGS behavior: cfgs.trigBehaviors.values())
		{
			switch (behavior.behaviorType)
			{
				case GameData.TRIG_BEHAVIOR_USESKILL:
					if(behavior.param1 > 0 && !cfgs.skills.containsKey(behavior.param1))
						throw new Exception("触发行为公式 " + behavior.id + " param1 " + behavior.param1 + " 非法");
					
					if(behavior.param2 > 0 && behavior.param2 > cfgs.skills.get(behavior.param1).lvlDatas.size())
						throw new Exception("触发行为公式 " + behavior.id + " param2 " + behavior.param2 + " 非法");
					break;
				case GameData.TRIG_BEHAVIOR_USEBUFF:
					if(behavior.param1 > 0 && !cfgs.buffs.containsKey(behavior.param1))
						throw new Exception("触发行为公式 " + behavior.id + " param1 " + behavior.param1 + " 非法");
					break;
				case GameData.TRIG_BEHAVIOR_DMGTOFIX:
				case GameData.TRIG_BEHAVIOR_DMGBYFIX:
					break;
				case GameData.TRIG_BEHAVIOR_QUICKCOOL_SKILL:
					if(behavior.param1 > 0 && !cfgs.skills.containsKey(behavior.param1))
						throw new Exception("触发行为公式 " + behavior.id + " param1 " + behavior.param1 + " 非法");
					break;
				default:
					break;
			}
		}
		
	}
	
	boolean checkSkillUseType(int useType)
	{
		switch (useType)
		{
		case GameData.SKILL_USE_TYPE_CUR:
		case GameData.SKILL_USE_TYPE_DODGE:
		case GameData.SKILL_USE_TYPE_DIY:
		case GameData.SKILL_USE_TYPE_UNIQUE:
		case GameData.SKILL_USE_TYPE_ATTACK:
		case GameData.SKILL_USE_TYPE_WEAPON:
		case GameData.SKILL_USE_TYPE_ALL:
			return true;		
		default:
			return false;
		}
	}
	
	public void loadProduceTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			final int rowStart = 2;
			final int colStart = 0;
			int row = rowStart;
			int col = colStart;
			excelReadContext.ReadSheet(0);
			{
				final int rowLvlExp = excelReadContext.locateColumnTag(colStart, "生产经验升级表");
				row = rowLvlExp + 2;
				col = 2;
				SBean.ProduceCFGS produceCfg = new SBean.ProduceCFGS();
				produceCfg.lvlNeedExp = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, col))
				{
					produceCfg.lvlNeedExp.add(excelReadContext.getIntValue(row++, col));
				}
				
				final int rowSplit = excelReadContext.locateColumnTag(colStart, "分解相关");
				row = rowSplit + 1;
				col = 2;
				produceCfg.spMax = excelReadContext.getIntValue(row++, col);
				produceCfg.buyTimes = excelReadContext.getIntValue(row++, col);
				produceCfg.buySP = excelReadContext.getIntValue(row++, col);
				produceCfg.costStones = excelReadContext.getIntegerList(row++, col, ";");
				produceCfg.spRevertInterval = excelReadContext.getIntValue(row++, col) * 60;	//minute to second
				produceCfg.spRevertCount = excelReadContext.getIntValue(row++, col);
				
				gdCfgs.produce = produceCfg;
			}
			
			excelReadContext.ReadSheet(1);
			{
				Map<Integer, SBean.ProduceRecipeCFGS> cfgMap = new TreeMap<>();
				row = rowStart;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart + 2;
					SBean.ProduceRecipeCFGS cfg = new SBean.ProduceRecipeCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					cfg.produceLvlReq = excelReadContext.getIntValue(row, col++);
					cfg.roleLvlReq = excelReadContext.getIntValue(row, col++);
					cfg.output = new SBean.DummyGoods(excelReadContext.getIntValue(row, col++), excelReadContext.getIntValue(row, col++));
					checkEntityIDValid(gdCfgs, row, cfg.output.id, false);
					
					cfg.input = new ArrayList<>();
					for(int i=0; i<5; i++)
					{
						SBean.DummyGoods goods = new SBean.DummyGoods(excelReadContext.getIntValue(row, col++), excelReadContext.getIntValue(row, col++));
						if(goods.id != 0)
						{
							checkEntityIDValid(gdCfgs, row, goods.id, false);
							cfg.input.add(goods);
						}
					}
					cfg.gainExp = excelReadContext.getIntValue(row, col++);
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("生产配置表 配方id : " + cfg.id + " 重复");
					
					row++;
				}
				gdCfgs.prodeceRecipes = cfgMap;
			}
			
			excelReadContext.ReadSheet(3);
			{
			    final int openLevelRow = excelReadContext.locateColumnTag(1, "功能开放等级");
				int openLevel = excelReadContext.getIntValue(openLevelRow, 2);
				final int costEnergyRow = excelReadContext.locateColumnTag(1, "炼化一次消耗能量(生产能量)");
				int costEnergy = excelReadContext.getIntValue(costEnergyRow, 2);
				final int fullPointRow = excelReadContext.locateColumnTag(1, "炼化一次需要生产点数(道具熔炼值)");
				int fullPoint = excelReadContext.getIntValue(fullPointRow, 2);
				
				final int startRow = excelReadContext.locateColumnTag(1, "等级段");
				final int startCol = 1;
				row = startRow+1;
				col = startCol;
				int lastLevel = -1;
				
				List<SBean.FusionSectionCFGS> fusionSections = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
				    String levelRangeStr = excelReadContext.getStringValue(row, col++);
				    int reward = excelReadContext.getIntValue(row, col);
				    
				    String[] levelRange = levelRangeStr.split(";");
				    if (levelRange.length!=2)
				    {
				        throw new Exception(" fusion level section format wrong at row " + row);
				    }
				    
				    int startLevel = Integer.valueOf(levelRange[0]);
				    int endLevel = Integer.valueOf(levelRange[1]);
				    
				    if (startLevel <= lastLevel || endLevel < startLevel)
				    {
				        throw new Exception(" fusion level range conflict at row " + row);
				    }
				    
				    if (startLevel-lastLevel!=1 && lastLevel!=-1)
				    {
				        throw new Exception(" fusion level range vacancy at row " + row);
				    }
				    
				    if (!gdCfgs.randomDropTbl.containsKey(reward))
				    {
				        throw new Exception(" reward drop id vacand in random drop table at row " + row);
				    }
				    
				    lastLevel = endLevel;
				    
				    fusionSections.add(new SBean.FusionSectionCFGS(startLevel, endLevel, reward));
				    
				    row++;
				    col = startCol;
				}
				
				SBean.FusionCFGS fusionCfg = new SBean.FusionCFGS(openLevel, costEnergy, fullPoint, fusionSections);
				gdCfgs.fusion = fusionCfg;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadFightFactor(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				SBean.FightFactorCFGS spiritFactors = new SBean.FightFactorCFGS(new ArrayList<>(), new ArrayList<>());
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					spiritFactors.grades.add(excelReadContext.getIntValue(row, col++));
					spiritFactors.factors.add(excelReadContext.getDoubleValue(row, col++));
					
					row++;
				}
				gdCfgs.spiritFactors = spiritFactors;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				SBean.FightFactorCFGS weaponFactors = new SBean.FightFactorCFGS(new ArrayList<>(), new ArrayList<>());
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					weaponFactors.grades.add(excelReadContext.getIntValue(row, col++));
					weaponFactors.factors.add(excelReadContext.getDoubleValue(row, col++));
					
					row++;
				}
				gdCfgs.weaponFactors = weaponFactors;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	
	
	public void loadStoreTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.StoreCFGS> storeMap = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.StoreCFGS storeCFGS = new SBean.StoreCFGS();
					col = colStart;
					storeCFGS.id = excelReadContext.getIntValue(row, col++);
					storeCFGS.itemId = excelReadContext.getIntValue(row, col++);
					checkItemIDValid(gdCfgs, storeCFGS.itemId);
					col += 1;
					storeCFGS.count = excelReadContext.getIntValue(row, col++);
					storeCFGS.costType = excelReadContext.getIntValue(row, col++);
					storeCFGS.money = excelReadContext.getIntValue(row, col++);
					col += 1;
					storeCFGS.isStop = excelReadContext.getIntValue(row, col++);
					
					storeMap.put(storeCFGS.id, storeCFGS);
					row++;
				}
				
				gdCfgs.stores = storeMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadHorseTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			// 先load 洗练、幻化表、骑术表
			excelReadContext.ReadSheet(2);
			{
				final	 int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.HorseEnHanceCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.HorseEnHanceDataCFGS cfg = new SBean.HorseEnHanceDataCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					SBean.HorseEnHanceCFGS enhanceCfg = cfgMap.get(cfg.id);
					if(enhanceCfg == null)
					{
						enhanceCfg =  new SBean.HorseEnHanceCFGS(new ArrayList<>());
						cfgMap.put(cfg.id, enhanceCfg);
					}
					
					col = colStart + 2;
					cfg.level = excelReadContext.getIntValue(row, col++);
					if(cfg.level != enhanceCfg.datas.size() + 1)
						throw new Exception("row " + row + " 坐骑表 洗练属性表 ID " + cfg.id + " 等级 " + cfg.level + " 不连续！");
					enhanceCfg.datas.add(cfg);
					
					cfg.propID = excelReadContext.getIntValue(row, col++);
					checkPropertyIDValid(gdCfgs, row, cfg.propID);
					
					cfg.values = new ArrayList<>();
					for(int i=0; i<6; i++)
					{
						int value = excelReadContext.getIntValue(row, col++);
						if(cfg.values.size() > 0 && cfg.values.get(cfg.values.size() - 1) > value)
							throw new Exception("row " + row + " 坐骑表洗练 属性峰值 " + value + " 非法");
						cfg.values.add(value);
					}
					
					row++;
				}
				gdCfgs.horseEnHances = cfgMap;
			}
			
			excelReadContext.ReadSheet(4);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.HorseShowCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.HorseShowCFGS cfg = new SBean.HorseShowCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 5;
					cfg.cost = new SBean.DummyGoods();
					cfg.cost.id = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdCfgs, row, cfg.cost.id, true);
					cfg.cost.count = excelReadContext.getIntValue(row, col++);
					
					cfg.offsets = new ArrayList<>();
					col = 10;
					List<Integer> offsets = excelReadContext.getIntegerList(row, col, ";");
					if(!offsets.isEmpty())
					{
						if(offsets.size() % 2 != 0)
							throw new Exception("row " + row + " 多人坐骑偏 " + offsets + " 错误");
						
						for(int i=0; i<offsets.size()/2; i++)
							cfg.offsets.add(new SBean.Vector3(offsets.get(i * 2), 0, offsets.get(i * 2 + 1)));
					}
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 坐骑幻化表 ID " +  cfg.id + " 重复！");
					
					
					row++;
				}
				gdCfgs.horseShows = cfgMap;
			}
			
			excelReadContext.ReadSheet(5);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.HorseSkillCFGS> mapCfg = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.HorseSkillCFGS cfg = new SBean.HorseSkillCFGS();
					cfg.id =  excelReadContext.getIntValue(row, colStart);
					
					col = 2;
					cfg.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.HORSE_SKILL_TYPE_BORN:
					case GameData.HORSE_SKILL_TYPE_LEANR:
						break;
					default:
						throw new Exception("row " + row + " 坐骑表 骑术类型 " + cfg.type + " 非法！");
					}
					
					cfg.learnCost = new SBean.DummyGoods();
					cfg.learnCost.id = excelReadContext.getIntValue(row, col++);
					cfg.learnCost.count = excelReadContext.getIntValue(row, col++);
					
					if(mapCfg.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 骑术表 ID " + cfg.id + " 重复");
					
					row++;
				}
				
				gdCfgs.horseSkills = mapCfg;
			}
			
			excelReadContext.ReadSheet(8);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.HorseEffectCFGS> mapCfg = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.HorseEffectDataCFGS dataCfg = new SBean.HorseEffectDataCFGS(0, 0, new HashSet<>());
					dataCfg.id  = excelReadContext.getIntValue(row, col++);
					checkHorseSkillIDValid(gdCfgs, row, dataCfg.id);
					SBean.HorseEffectCFGS cfg = mapCfg.get(dataCfg.id);
					if(cfg == null)
					{
						cfg =  new SBean.HorseEffectCFGS(new ArrayList<>());
						mapCfg.put(dataCfg.id, cfg);
					}
					
					dataCfg.level = excelReadContext.getIntValue(row, col++);
					if(dataCfg.level != cfg.datas.size() + 1)
						throw new Exception("row " + row + " 骑术效果表ID " + dataCfg.id + " 等级 " + dataCfg.level + " 不连续！");
					
					cfg.datas.add(dataCfg);
					
					col = 4;
					List<Integer> effectIDs = excelReadContext.getIntegerList(row, col++, ";");
					for(int effectID: effectIDs)
					{
						if(!dataCfg.effectIDs.add(effectID))
							throw new Exception("row " + row + " 骑术效果表ID " + dataCfg.id + " 等级 " + dataCfg.level + " 效果ID " + effectID + " 重复！");
						
						checkSpiritEffectIDValid(gdCfgs, row, effectID);
					}
					
					row++;
				}
				gdCfgs.horseEffects = mapCfg;
			}
			
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.HorseCFGS> cfgMap = new HashMap<>();
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.HorseCFGS cfg = new SBean.HorseCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 2;
					cfg.orgShowID = excelReadContext.getIntValue(row, col++);
					checkHorseShowIDValid(gdCfgs, row, cfg.orgShowID);
					cfg.fullStarShowID = excelReadContext.getIntValue(row, col++);
					checkHorseShowIDValid(gdCfgs, row, cfg.fullStarShowID);
					cfg.buyShowID = excelReadContext.getIntValue(row, col++);
					checkHorseShowIDValid(gdCfgs, row, cfg.buyShowID);
					cfg.speed = excelReadContext.getIntValue(row, col++);
					
					cfg.tameCost = new SBean.DummyGoods();
					cfg.tameCost.id = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdCfgs, row, cfg.tameCost.id, false);
					cfg.tameCost.count = excelReadContext.getIntValue(row, col++);
					cfg.enhanceCosts = new ArrayList<>();
					for(int i=0; i<2; i++)
					{
						SBean.DummyGoods goods = new SBean.DummyGoods();
						goods.id = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, goods.id, true);
						goods.count = excelReadContext.getIntValue(row, col++);
						if(goods.id != 0)
							cfg.enhanceCosts.add(goods);
					}
					
					cfg.lockCostItem = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdCfgs, row, cfg.lockCostItem, false);
					cfg.lockCostCounts = excelReadContext.getIntegerList(row, col++, ";");
					cfg.lockCostReplaceItem = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdCfgs, row, cfg.lockCostReplaceItem, false);
					cfg.lockCostReplaceCounts = excelReadContext.getIntegerList(row, col++, ";");
					
					Set<Integer> propIDs = new HashSet<>();
					List<Integer> attrs = excelReadContext.getIntegerList(row, col++, ";");
					cfg.attrs = new HashSet<>();
					for(int id: attrs)
					{
						int propID = checkHorseAttrID(gdCfgs, row, id);
						cfg.attrs.add(id);
						
						if(!propIDs.add(propID))
							throw new Exception("坐骑表  洗练属性列表 id " + id + " 对应属性ID " + propID + "　重复！");
					}
					
					cfg.enhanceExp = excelReadContext.getIntValue(row, col++);
					cfg.lockExps = excelReadContext.getIntegerList(row, col++, ";");
					if (cfg.lockExps.size() != cfg.lockCostCounts.size())
						throw new Exception("坐骑表  洗练锁增加经验与最大锁定数不符！");
					cfg.bornSkill = excelReadContext.getIntValue(row, col++);
					cfg.pure = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.pure);
					if(cfg.pure != 1)
						checkHorseSkillIDValid(gdCfgs, row, cfg.bornSkill);
					
					col = 22;
					cfg.rideCnt = excelReadContext.getByteValue(row, col++);
					cfg.enHanceType = excelReadContext.getByteValue(row, col++);
					cfg.growups = new ArrayList<>();
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("坐骑ID " + cfg.id + " 重复");
					
					row++;
				}
				gdCfgs.horses = cfgMap;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.HorseGrowUpCGFS cfg = new SBean.HorseGrowUpCGFS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.star = excelReadContext.getIntValue(row, col++);
					cfg.bornOpen = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.bornOpen);
					cfg.skillCount = excelReadContext.getIntValue(row, col++);
					
					col = 5;
					cfg.costs = new ArrayList<>();
					for(int i=0; i<2; i++)
					{
						SBean.DummyGoods goods = new SBean.DummyGoods();
						goods.id = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, goods.id, true);
						goods.count = excelReadContext.getIntValue(row, col++);
						if(goods.id != 0)
							cfg.costs.add(goods);
					}
					
					cfg.attrs = new ArrayList<>();
					for(int i=0; i<9; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						if(attr.id > 0)
						{
							checkPropertyIDValid(gdCfgs, row, attr.id);
							attr.value = excelReadContext.getIntValue(row, col++);
							cfg.attrs.add(attr);
						}
					}
					
					SBean.HorseCFGS horseCfg = gdCfgs.horses.get(cfg.id);
					if(horseCfg == null)
						throw new Exception("升星表 坐骑ID " + cfg.id + " 非法");
					
					if(horseCfg.growups.size() != cfg.star)
						throw new Exception("升星表 星级 " + cfg.star + " 不连续");
					
					horseCfg.growups.add(cfg);
					row++;
				}
			}
			
			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.HorseEnHanceLvlCFGS> mapCfg = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.HorseEnHanceLvlDataCFGS dataCfg = new SBean.HorseEnHanceLvlDataCFGS();
					dataCfg.id = excelReadContext.getIntValue(row, col++);
					checkHorseIDValid(gdCfgs, row, dataCfg.id);
					
					SBean.HorseEnHanceLvlCFGS cfg = mapCfg.get(dataCfg.id);
					if(cfg == null)
					{
						cfg = new SBean.HorseEnHanceLvlCFGS(new ArrayList<>());
						mapCfg.put(dataCfg.id, cfg);
					}
					
					dataCfg.level = excelReadContext.getIntValue(row, col++);
					if(dataCfg.level != cfg.datas.size() + 1)
						throw new Exception("坐骑表 洗练等级表 ID " + dataCfg.id + " 等级 " + dataCfg.level + " 不连续！");
					
					cfg.datas.add(dataCfg);
					
					dataCfg.needExp = excelReadContext.getIntValue(row, col++);
					
					row++;
				}
				
				gdCfgs.horseEnhanceLvls = mapCfg;
			}
			
			excelReadContext.ReadSheet(7);
			{
				final int rowStart = 3;
				final int colStart = 2;
				int row = rowStart;
				
				SBean.HorseCommonCFGS cfg = new SBean.HorseCommonCFGS();
				List<Integer> canRideMaps = excelReadContext.getIntegerList(row++, colStart, ";");
				cfg.canRideMaps = new HashSet<>();
				for(Integer map: canRideMaps)
					cfg.canRideMaps.add(map);

				cfg.forbidTime = excelReadContext.getIntValue(row++, colStart);
				row = 7;
				cfg.inuseAdd = excelReadContext.getIntValue(row++, colStart);
				cfg.rankOdds = excelReadContext.getIntegerList(row++, colStart, ";");
				row = 17;
				cfg.valueFix = excelReadContext.getIntValue(row++, colStart);
				row = 21;
				cfg.inviteDistance = excelReadContext.getIntValue(row++, colStart);
				cfg.unlockCost = new ArrayList<>();
				String itemsStr = excelReadContext.getStringValue(row++, colStart - 1);
				String[] items = itemsStr.split(";");
				for (String itemStr : items)
				{
					if (itemStr.isEmpty())
						continue;
					String[] item = itemStr.split(",");
					if (item.length != 2)
						throw new Exception("坐骑表 通用配置表 激活属性消耗道具 " + itemStr + " 有误！");
					int id = Integer.parseInt(item[0]);
					int num = Integer.parseInt(item[1]);
					checkEntityIDValid(gdCfgs, row, id, true);
					if (id != 0 && num <= 0)
						throw new Exception("坐骑表 通用配置表 激活属性消耗道具 " + itemStr + " 有误！");
					cfg.unlockCost.add(new SBean.DummyGoods(id, num));
				}
				cfg.lockAddArg = excelReadContext.getIntValue(row++, colStart - 1);
				gdCfgs.horseCommon = cfg;
			}
			
			// 骑术升级
			excelReadContext.ReadSheet(6);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				int horseSkillId = 1;
				
				Map<Integer , SBean.HorseSkillUpdateDataCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int tempId = excelReadContext.getIntValue(row, col);
					if (tempId == horseSkillId)
					{
						SBean.HorseSkillUpdateDataCFGS levelCfg = new SBean.HorseSkillUpdateDataCFGS();
						levelCfg.data = new ArrayList<>();
						while(excelReadContext.isNotEmpty(row, col) && horseSkillId == excelReadContext.getIntValue(row, col))
						{
							SBean.HorseSkillUpdateCFGS lineCfg = new SBean.HorseSkillUpdateCFGS();
							lineCfg.id = excelReadContext.getIntValue(row, col++);
							lineCfg.level = excelReadContext.getIntValue(row, col);
							col += 3;
							lineCfg.consume1 = new SBean.DummyGoods(excelReadContext.getIntValue(row, col++), excelReadContext.getIntValue(row, col++));
							lineCfg.consume2 = new SBean.DummyGoods(excelReadContext.getIntValue(row, col++), excelReadContext.getIntValue(row, col++));
							lineCfg.fightPower = excelReadContext.getIntValue(row, col++);
							levelCfg.data.add(lineCfg);
							col = colStart;
							row++;
						}
						cfgs.put(horseSkillId, levelCfg);
					}else
					{
						horseSkillId ++;
					}
					
				}
				gdCfgs.horseSkillUpdate = cfgs;
			}
			
			// 洗练条目数
			excelReadContext.ReadSheet(10);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.HorseEnHanceLvlCommonCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.HorseEnHanceLvlCommonCFGS cfg = new SBean.HorseEnHanceLvlCommonCFGS();
					cfg.level = excelReadContext.getIntValue(row, col++);
					if (cfg.level != cfgs.size() + 1)
						throw new Exception("坐骑表 洗练条目数洗练等级 " + cfg.level + " 不连续！");
					cfg.attrNum = excelReadContext.getIntValue(row, col++);
					cfg.canLockGrow = excelReadContext.getByteValue(row, col++);
					cfg.maxLockNum = excelReadContext.getIntValue(row, col++);
					
					cfgs.add(cfg);
					col=colStart;
					row++;
				}
				gdCfgs.horseEnhanceLvlcommons = cfgs;
			}
			
			// 权重随机规则
			excelReadContext.ReadSheet(12);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.HorseEnHanceWeightCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.HorseEnHanceWeightCFGS cfg = new SBean.HorseEnHanceWeightCFGS();
					cfg.weightId = excelReadContext.getIntValue(row, col++);
					if (cfg.weightId != cfgs.size() + 1)
						throw new Exception("坐骑表 权重随机规则Id " + cfg.weightId + " 不连续！");
					cfg.rate0120 = excelReadContext.getIntValue(row, col++);
					cfg.rate2140 = cfg.rate0120 + excelReadContext.getIntValue(row, col++);
					cfg.rate4160 = cfg.rate2140 + excelReadContext.getIntValue(row, col++);
					cfg.rate6180 = cfg.rate4160 + excelReadContext.getIntValue(row, col++);
					cfg.rate8100 = cfg.rate6180 + excelReadContext.getIntValue(row, col++);
					
					cfgs.add(cfg);
					col=colStart;
					row++;
				}
				gdCfgs.horseEnhanceWeights = cfgs;
			}
			
			// 洗练属性
			excelReadContext.ReadSheet(11);
			{
				final int rowStart = 2;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.HorseEnHanceTypeCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int type  = excelReadContext.getIntValue(row, col++);
					if (!cfgs.containsKey(type))
					{
						cfgs.put(type, new SBean.HorseEnHanceTypeCFGS(new ArrayList<>()));
					}
					SBean.HorseEnHanceTypeCFGS typeCFGS = cfgs.get(type);
					int line  = excelReadContext.getIntValue(row, col++);
					if (typeCFGS.lines.size() < line)
					{
						if (line != typeCFGS.lines.size() + 1)
							throw new Exception("坐骑表 洗练属性 洗练条目 " + line + " 不连续！");
						typeCFGS.lines.add(new SBean.HorseEnHanceAttrLineCFGS(new ArrayList<>()));
					}
					SBean.HorseEnHanceAttrLineCFGS lineCFGS = typeCFGS.lines.get(line - 1);
					SBean.HorseEnHanceAttrCFGS attrCFGS = new SBean.HorseEnHanceAttrCFGS();
					attrCFGS.attrId = excelReadContext.getIntValue(row, col++);
					checkPropertyIDValid(gdCfgs, row, attrCFGS.attrId);
					attrCFGS.attrType = excelReadContext.getByteValue(row, col++);
					attrCFGS.minNum = excelReadContext.getIntValue(row, col++);
					attrCFGS.maxNum = excelReadContext.getIntValue(row, col++);
					attrCFGS.randomRuleId = excelReadContext.getIntValue(row, col++);
					if (attrCFGS.randomRuleId <= 0 || attrCFGS.randomRuleId > gdCfgs.horseEnhanceWeights.size())
						throw new Exception("坐骑表 洗练属性 权重随机规则 " + attrCFGS.randomRuleId + " 不存在！");

					attrCFGS.attrWeight = (lineCFGS.attrs.size() > 0 ? lineCFGS.attrs.get(lineCFGS.attrs.size() - 1).attrWeight : 0) + excelReadContext.getIntValue(row, col++);
					lineCFGS.attrs.add(attrCFGS);
					col = colStart;
					row++;
				}
				gdCfgs.horseEnhanceTypes = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadTreasureTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				SBean.TreasureBaseCFGS baseCfg = new SBean.TreasureBaseCFGS(new HashMap<>(),
																			new ArrayList<>(),
																			new ArrayList<>(), 
																			0, 
																			0, 
																			0,
																			new HashMap<>());
				//线索树
				{
					final int rowStart = 2;
					final int colStart = 2;
					int row = rowStart;
					int col = colStart;
					
					for(int i=1; i<4; i++)
					{
						SBean.ClueTreeCFGS clueTree = new SBean.ClueTreeCFGS(new ArrayList<>());
						final int rowPoint = excelReadContext.locateColumnTag(0, "线索树类型结构" + i);
						row = rowPoint + 1;
						final int count = excelReadContext.getIntValue(row++, col);
						
						int total = 0;
						for(int j=0; j<count; j++)
						{
							SBean.Clue clue = new SBean.Clue(0, new HashSet<>());
							clue.gain = excelReadContext.getIntValue(row++, col);
							total += clue.gain;
							
							List<Integer> prePoints = excelReadContext.getIntegerList(row++, col, ";");
							for(int p: prePoints)
							{
								if(p > 0)
								{
									if(!clue.prePoints.add(p))
										throw new Exception("线索树类型结构 " + i + " 情报点 " + (j + 1) + " 前提情报点 " + p + " 重复！");
								}
							}
							
							clueTree.clues.add(clue);
						}
						
						if(total != 100)
							throw new Exception("线索树类型结构 " + i + "完成度和  != 100 ");
						
						baseCfg.clueTrees.put(i, clueTree);
					}
				}
				
				//NPC声望
				{
					final int rowStart =  excelReadContext.locateColumnTag(0, "NPC声望") + 2;
					final int colStart = 1;
					int row = rowStart;
					int col = colStart;
					
					while(excelReadContext.isNotEmpty(row, colStart))
					{
						col = colStart;
						SBean.NpcFameCFGS fame = new SBean.NpcFameCFGS();
						fame.level = excelReadContext.getIntValue(row, col++);
						fame.fameFloor = excelReadContext.getIntValue(row, col++);
						fame.count = excelReadContext.getIntValue(row, col++);
						fame.min = excelReadContext.getIntValue(row, col++);
						fame.max = excelReadContext.getIntValue(row, col++);
						
						baseCfg.npcFames.add(fame);
						row++;
					}
				}
				
				final int rowRefresh = excelReadContext.locateColumnTag(0, "刷新") + 1;
				baseCfg.refreshDiamond = excelReadContext.getIntegerList(rowRefresh, 2, ";");
				
				{
					final int rowOther = excelReadContext.locateColumnTag(0, "其他");
					final int colStart = 2;
					int row = rowOther + 1;
					baseCfg.needlevel = excelReadContext.getIntValue(row++, colStart);
					baseCfg.searchVit = excelReadContext.getIntValue(row++, colStart);
					row = rowOther + 6;
					baseCfg.distance = excelReadContext.getIntValue(row++, colStart);
				}
				
				{
					final int rowBornPieces = excelReadContext.locateColumnTag(0, "初始藏宝图碎片");
					int row = rowBornPieces + 1;
					final int colStart = 2;
					for(int i=0; i<3; i++)
					{
						int pieceID = excelReadContext.getIntValue(row, colStart);
						int count = excelReadContext.getIntValue(row++, colStart + 1);
						if(baseCfg.bornPieces.putIfAbsent(pieceID, count) != null)
							throw new Exception("初始藏宝图碎片 " + pieceID + " 重复！");
					}
				}
				
				gdCfgs.treasureBase = baseCfg;
			}
			
			//先load 情报点和收藏品
			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.InfoPointCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.InfoPointCFGS cfg = new SBean.InfoPointCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.type = excelReadContext.getIntValue(row, col++);
					
					col = 5;
					cfg.param1 = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.TREASURE_INFOPOINT_KILLMONSTER:
						checkMonsterID(gdCfgs, row, cfg.param1);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.TREASURE_INFOPOINT_DIALOGUE:
						checkNPCID(gdCfgs, row, cfg.param1);
						break;
					case GameData.TREASURE_INFOPOINT_MINERAL:
						checkMapID(gdCfgs, row, cfg.param1);
						cfg.param2 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cfg.param3 = (int)(excelReadContext.getFloatValue(row, col++) * 100);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						break;
					case GameData.TREASURE_INFOPOINT_SECRET_BOX:
						checkNPCID(gdCfgs, row, cfg.param1);
						cfg.param2 = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, cfg.param2, true);
						cfg.param3 = excelReadContext.getIntValue(row, col++);
						cfg.param4 = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, cfg.param4, true);
						cfg.param5 = excelReadContext.getIntValue(row, col++);
						break;
					default:
						throw new Exception("情报点ID " + cfg.id + " 类型 " + cfg.type + " 非法！");
						
					}
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("情报点ID " + cfg.id + " 重复！");
					
					row++;
				}
				gdCfgs.infoPoints = cfgMap;
			}
			
			excelReadContext.ReadSheet(4);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.MedalCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.MedalCFGS cfg = new SBean.MedalCFGS(0, new ArrayList<>(), new ArrayList<>(), 0);
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = 5;
					for(int i=0; i<2; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.value = excelReadContext.getIntValue(row, col++);
						if(attr.id > 0)
						{
							checkPropertyIDValid(gdCfgs, row, attr.id);
							cfg.attrs.add(attr);
						}
					}
					
					for(int i=0; i<2; i++)
					{
						SBean.DummyGoods goods = new SBean.DummyGoods();
						goods.id = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, goods.id, true);
						goods.count = excelReadContext.getIntValue(row, col++);
						cfg.normalCost.add(goods);
					}
					
					cfg.diamondCost = excelReadContext.getIntValue(row, col++);
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("收藏品ID " + cfg.id + " 重复！");
					
					row++;
				}
				gdCfgs.medals = cfgMap;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.TreasureMapCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.TreasureMapCFGS cfg = new SBean.TreasureMapCFGS(0, 0, new ArrayList<>(), new ArrayList<>(), 0, 0);
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = 4;
					cfg.clueType = excelReadContext.getIntValue(row, col++);
					SBean.ClueTreeCFGS clurTreeCfg = checkClueTreeType(gdCfgs, row, cfg.clueType);
					cfg.points = excelReadContext.getIntegerList(row, col++, ";");
					for(int pointID: cfg.points)
						this.checkInfoPointIDValid(gdCfgs, row, pointID);
					
					if(clurTreeCfg.clues.size() != cfg.points.size())
						throw new Exception("藏宝图ID " + cfg.id + " 线索架构情报点数量 " + cfg.points.size() + " 不正确！");
					
					for(int i=0; i<3; i++)
					{
						SBean.TreasureReward reward = new SBean.TreasureReward();
						reward.percent = excelReadContext.getIntValue(row, col++);
						reward.fixTblID = excelReadContext.getIntValue(row, col++);
						checkFixedDropIDValid(gdCfgs, row, reward.fixTblID);
						cfg.rewards.add(reward);
					}
					
					cfg.medal = excelReadContext.getIntValue(row, col++);
					checkMedalIDValid(gdCfgs, row, cfg.medal);
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("藏宝图ID " + cfg.id + " 重复！");
					
					row++;
				}
				
				gdCfgs.treasureMaps = cfgMap;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.TreasurePieceCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.TreasurePieceCFGS cfg = new SBean.TreasurePieceCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = 4;
					cfg.relateMapID = excelReadContext.getIntValue(row, col++);
					SBean.TreasureMapCFGS treasureCfg =  checkTreasureMapIDValid(gdCfgs, row, cfg.relateMapID);
					treasureCfg.pieceID = cfg.id;
					
					cfg.needCount = excelReadContext.getIntValue(row, col++);
					cfg.price = excelReadContext.getIntValue(row, col++);
					cfg.addFame = excelReadContext.getIntValue(row, col++);
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("藏宝图碎片ID " + cfg.id + " 重复！");
					
					row++;
				}
				
				//check born pieces
				for(int pieceID: gdCfgs.treasureBase.bornPieces.keySet())
				{
					if(!cfgMap.containsKey(pieceID))
						throw new Exception("初始藏宝图碎片ID " + pieceID + " 非法！");
				}
				
				gdCfgs.treasurePieces = cfgMap;
			}
			
			excelReadContext.ReadSheet(5);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.TreasureNpcCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.TreasureNpcCFGS cfg = new SBean.TreasureNpcCFGS(0, 0, new ArrayList<>(), new ArrayList<>());
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = 3;
					cfg.needPower = excelReadContext.getIntValue(row, col++);
					
					col = 9;
					for(int i=0; i<8; i++)
					{
						List<Integer> pieces = excelReadContext.getIntegerList(row, col++, ";");
						for(int pid: pieces)
							checkTreasurePieceIDValid(gdCfgs, row, pid);
						
						cfg.pieces.add(new SBean.IntList(pieces));
					}
					
					for(int i=0; i<8; i++)
					{
						int fixTblID = excelReadContext.getIntValue(row, col++);
						checkFixedDropIDValid(gdCfgs, row, fixTblID);
						cfg.fixTblIDs.add(fixTblID);
					}
					
					int npcMaxLvl = gdCfgs.treasureBase.npcFames.size();
					if(cfg.pieces.size() != npcMaxLvl || cfg.fixTblIDs.size() != npcMaxLvl)
						throw new Exception("客栈NPCID " + cfg.id + " npc 最大声望阶段非法！");
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("客栈NPCID " + cfg.id + " 重复！");
					
					row++;
				}
				
				gdCfgs.treasureNpcs = cfgMap;
			}
			
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	
	public void loadFriedGiveRewardTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				List<SBean.FriendGiveRewardCFGS> friendFives = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.FriendGiveRewardCFGS friendFiveCFGS = new SBean.FriendGiveRewardCFGS();
					col = colStart;
					friendFiveCFGS.level = excelReadContext.getIntValue(row, col++);
					friendFiveCFGS.exp = excelReadContext.getIntValue(row, col++);
					friendFiveCFGS.count = excelReadContext.getIntValue(row, col++);
					
					friendFives.add(friendFiveCFGS.level - 1, friendFiveCFGS);
					row++;
				}
				
				gdCfgs.friendGive = friendFives;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.CharmCFGS> charms = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.CharmCFGS cfg = new SBean.CharmCFGS(0, 0, 0, 0);
					cfg.level = excelReadContext.getIntValue(row, col++);
					cfg.charmReq = excelReadContext.getIntValue(row, col++);
					cfg.maleTitle = excelReadContext.getIntValue(row, col++);
					cfg.femaleTitle = excelReadContext.getIntValue(row, col++);
					if(cfg.maleTitle > 0)
						checkTitleIDValid(gdCfgs, row, cfg.maleTitle);
					
					if(cfg.femaleTitle > 0)
						checkTitleIDValid(gdCfgs, row, cfg.femaleTitle);
						
					charms.add(cfg);
					if(cfg.level != charms.size())
						throw new Exception("魅力值表 等级" + cfg.level + " 不连续！");
					
					row++;
				}
				gdCfgs.charms = charms;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	
	}
	
	
	public void loadFriedHeadTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{

		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.FriendHeadCFGS> friendHeads = new TreeMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.FriendHeadCFGS friendHead = new SBean.FriendHeadCFGS();
					col = colStart;
					friendHead.headId = excelReadContext.getIntValue(row, col++);
					friendHead.openType = excelReadContext.getIntValue(row, col++);
					friendHead.arg1 = excelReadContext.getIntValue(row, col++);
					friendHead.arg2 = excelReadContext.getIntValue(row, col++);
					
					friendHeads.put(friendHead.headId, friendHead);
					row++;
				}
				
				gdCfgs.friendHead = friendHeads;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	
	}
	
	public void loadFashionTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.FashionCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.FashionCFGS cfg = new SBean.FashionCFGS(0, 0, new ArrayList<SBean.AttrCFGS>(), (byte)0);
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 3;
					cfg.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.FASHION_TYPE_WEAPON:
					case GameData.FASHION_TYPE_CLOTHES:
					case GameData.FASHION_TYPE_OTHER:
						break;
					default:
						throw new Exception("row " + row + " 时装类型 " + cfg.type + " 非法");
					}
					
					for(int i=0; i<4; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.value = excelReadContext.getIntValue(row, col++);
						if (attr.id > 0)
						{
							checkPropertyIDValid(gdCfgs, row, attr.id);

							cfg.attrs.add(attr);
						}
					}
					col += 3;
					cfg.sex = excelReadContext.getByteValue(row, col++);

					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("时装 ID " + cfg.id + " 重复");
					
					row++;
				}
				
				gdCfgs.fashions = cfgMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadSocialActionTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				Map<Integer, SBean.SocialActionCFGS> cfgMap = new HashMap<>();
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SocialActionCFGS cfg = new SBean.SocialActionCFGS(0, (byte)0, (byte)0, new ArrayList<>(), 0);
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 3;
					cfg.keep = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.keep);
					
					col = 6;
					List<Integer> buffs = excelReadContext.getIntegerList(row, col++, ";");
					for(int buffID: buffs)
					{
						if(buffID > 0)
						{
							cfg.buffs.add(buffID);
							checkBuffIDValid(gdCfgs, row, buffID);
						}
					}
					
					cfg.checkFight = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.checkFight);
					cfg.relateState = excelReadContext.getIntValue(row, col++);
					if(cfg.relateState > 0)
						checkStateIDValid(gdCfgs, row, cfg.relateState);
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("社交动作ID " + cfg.id + " 重复！");
						
					row++;
				}
				
				gdCfgs.socialActions = cfgMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadSealTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				SBean.SealBaseCFGS baseCfg = new SBean.SealBaseCFGS(new SBean.SealOpenCFGS(0, 0, 0), 
																	new ArrayList<>(), 
																	0, 
																	0, 
																	0,
																	new ArrayList<>(), 
																	new ArrayList<>());
				
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				final int rowOpen = excelReadContext.locateColumnTag(colStart, "开启限制");
				row = rowOpen + 1;
				col = colStart + 1;
				baseCfg.open.lvlReq = excelReadContext.getIntValue(row++, col);
				baseCfg.open.powerReq = excelReadContext.getIntValue(row++, col);
				baseCfg.open.transformReq = excelReadContext.getIntValue(row++, col);
				
				final int rowMake = excelReadContext.locateColumnTag(colStart, "合成消耗");
				row = rowMake + 1;
				col = colStart + 1;
				for(int i=0; i<4; i++)
				{
					int itemID = excelReadContext.getIntValue(row++, col);
					int count = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, itemID, true);
					
					if(count > 0)
						baseCfg.normalMake.add(new SBean.DummyGoods(itemID, count));
				}
				baseCfg.diamondMake = excelReadContext.getIntValue(row++, col);
				
				final int rowSkill = excelReadContext.locateColumnTag(colStart, "技能属性相关");
				row  = rowSkill + 1;
				col = colStart + 1;
				baseCfg.skillCount = excelReadContext.getIntValue(row++, col);
				baseCfg.skillOpenReq = excelReadContext.getIntValue(row++, col);
				
				final int rowUpLvl = excelReadContext.locateColumnTag(colStart, "技能属性洗练提升等级限制");
				row = rowUpLvl + 2;
				col = colStart + 1;
				while(excelReadContext.isNotEmpty(row, col))
				{
					baseCfg.needEnhanceCnt.add(excelReadContext.getIntValue(row++, col));
				}
				
				final int rowEnhance = excelReadContext.locateColumnTag(colStart, "每次洗练消耗");
				row = rowEnhance + 1;
				col = colStart + 1;
				for(int i=0; i<4; i++)
				{
					int itemID = excelReadContext.getIntValue(row++, col);
					int count = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, itemID, true);
					
					if(count > 0)
						baseCfg.enhanceCost.add(new SBean.DummyGoods(itemID, count));
				}
				
				gdCfgs.sealBase = baseCfg;
			}
			
			excelReadContext.ReadSheet(1);
			{
				List<SBean.SealGradeCFGS> cfgs = new ArrayList<>();
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SealGradeCFGS cfg = new SBean.SealGradeCFGS(0, new ArrayList<>(), new ArrayList<>());
					cfg.grade = excelReadContext.getIntValue(row, colStart);
					col = colStart + 2;
					for(int i=0; i<4; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, itemID, true);
						
						if(count > 0)
							cfg.cost.add(new SBean.DummyGoods(itemID, count));
					}
					
					for(int i=0; i<6; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.value = excelReadContext.getIntValue(row, col++);
						if (attr.id > 0)
						{
							this.checkPropertyIDValid(gdCfgs, row, attr.id);
							cfg.attrs.add(attr);
						}
					}
					
					cfgs.add(cfg);
					if(cfgs.size() != cfg.grade)
						throw new Exception("龙印升阶表 阶位 " + cfg.grade + " 不连续！");
					
					row++;
				}
				gdCfgs.sealGrades = cfgs;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				Map<Integer, SBean.SealEnhanceTypeCFGS> cfgMap = new HashMap<>();
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SealEnhanceCFGS cfg = new SBean.SealEnhanceCFGS(0, 0, 0, 0, 0, 0, new ArrayList<>());
					col = colStart;
					cfg.id = excelReadContext.getIntValue(row, col++);
					
					cfg.classType = excelReadContext.getIntValue(row, col++);
					checkClassTypeValid(gdCfgs, row, cfg.classType);
					SBean.SealEnhanceTypeCFGS typeCfg = cfgMap.get(cfg.classType);
					if(typeCfg == null)
					{
						typeCfg = new SBean.SealEnhanceTypeCFGS(new HashMap<>());
						cfgMap.put(cfg.classType, typeCfg);
					}
					
					cfg.bwType = excelReadContext.getIntValue(row, col++);
					if(cfg.bwType != 1 && cfg.bwType != 2)
						throw new Exception("龙印洗练配置ID " + cfg.id + " 正邪类型 " + cfg.bwType + " invalid!");
					SBean.SealEnhanceBWCFGS bwCfg = typeCfg.bw.get(cfg.bwType);
					if(bwCfg == null)
					{
						bwCfg = new SBean.SealEnhanceBWCFGS(new ArrayList<>());
						typeCfg.bw.put(cfg.bwType, bwCfg);
					}
					bwCfg.enhances.add(cfg);
					cfg.weight = excelReadContext.getIntValue(row, col++);
					cfg.enhanceReq = excelReadContext.getIntValue(row, col++);
					cfg.skillID = excelReadContext.getIntValue(row, col++);
					cfg.lvlWeight = new ArrayList<>();
					
					while(excelReadContext.isNotEmpty(row, col))
					{
						cfg.lvlWeight.add(excelReadContext.getIntValue(row, col++));
					}
					
					row++;
				}
				gdCfgs.sealEnhanceTypes = cfgMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadLeadTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.LeadGroupCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int groupID = excelReadContext.getIntValue(row, colStart);
					SBean.LeadGroupCFGS gCfg = cfgMap.get(groupID);
					if(gCfg == null)
					{
						gCfg = new SBean.LeadGroupCFGS(groupID);
						cfgMap.put(groupID, gCfg);
					}
					row++;
				}
				gdCfgs.leadGroups = cfgMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadExpCoinTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				SBean.ExpCoinBaseCFGS cfg = new SBean.ExpCoinBaseCFGS();
				final int rowBase = excelReadContext.locateColumnTag(colStart, "基础配置");
				row = rowBase + 1;
				col = colStart + 1;
				cfg.lvlReq = excelReadContext.getIntValue(row++, col);
				cfg.rate = excelReadContext.getIntValue(row++, col);
				cfg.volume = excelReadContext.getIntValue(row++, col);
				cfg.dayTakeTimes = excelReadContext.getIntValue(row++, col);
				
				final int rowGraspBase = excelReadContext.locateColumnTag(colStart, "参悟相关");
				row = rowGraspBase + 1;
				col = colStart + 1;
				cfg.graspBase = new SBean.GraspBaseCFGS();
				cfg.graspBase.dayGraspTimes = excelReadContext.getIntValue(row++, col);
				cfg.graspBase.coolTime = excelReadContext.getIntValue(row++, col);
				cfg.graspBase.resetDiamond = excelReadContext.getIntValue(row++, col);
				
				final int rowGraspCost = excelReadContext.locateColumnTag(colStart, "参悟消耗");
				row = rowGraspCost + 1;
				col = colStart + 1;
				
				cfg.graspNormalCost = new SBean.GraspCostCFGS(0, new ArrayList<>(), 0);
				cfg.graspNormalCost.expCoinCost = excelReadContext.getIntValue(row++, col);
				for(int i=0; i<2; i++)
				{
					int itemID = excelReadContext.getIntValue(row++, col);
					int count = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, itemID, true);
					
					if(count > 0)
						cfg.graspNormalCost.itemsCost.add(new SBean.DummyGoods(itemID, count));
				}
				cfg.graspNormalCost.addExp = excelReadContext.getIntValue(row++, col);
				
				row = rowGraspCost + 1;
				col = colStart + 2;
				cfg.graspSuitCost = new SBean.GraspCostCFGS(0, new ArrayList<>(), 0);
				cfg.graspSuitCost.expCoinCost = excelReadContext.getIntValue(row++, col);
				for(int i=0; i<2; i++)
				{
					int itemID = excelReadContext.getIntValue(row++, col);
					int count = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, itemID, true);
					
					if(count > 0)
						cfg.graspSuitCost.itemsCost.add(new SBean.DummyGoods(itemID, count));
				}
				cfg.graspSuitCost.addExp = excelReadContext.getIntValue(row++, col);
				
				
				final int rowExpCoinPool = excelReadContext.locateColumnTag(colStart, "提取历练相关");
				row = rowExpCoinPool + 1;
				col = colStart + 1;
				cfg.extract = new SBean.ExpCoinExtractCFGS();
				{
					int itemID = excelReadContext.getIntValue(row++, col);
					int count = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, itemID, true);
					if(count <= 0)
						throw new Exception("历练基础配置 提取历练相关 历练瓶（空）ID " + itemID + " 消耗个数 " + count + " invalid!");
					
					cfg.extract.input = new SBean.DummyGoods(itemID, count);
				}
				
				{
					int itemID = excelReadContext.getIntValue(row++, col);
					int count = excelReadContext.getIntValue(row++, col);
					checkEntityIDValid(gdCfgs, row, itemID, true);
					if(count <= 0)
						throw new Exception("历练基础配置 提取历练相关 历练瓶（满）ID " + itemID + " 生成个数 " + count + " invalid!");
					
					cfg.extract.output = new SBean.DummyGoods(itemID, count);
				}
				
				final int rowDmgTransfer = excelReadContext.locateColumnTag(0, "乾坤点相关");
				{
					row = rowDmgTransfer + 1;
					col = 1;
					cfg.dmgTransfer = new SBean.DMGTransferBaseCFGS(0, new ArrayList<>(), 0, new HashMap<>(), new HashMap<>(), 0);
					cfg.dmgTransfer.initPoint = excelReadContext.getIntValue(row++, col);
					for(int i = 0; i < 1; i++)
					{
						int itemID = excelReadContext.getIntValue(row++, col);
						if(itemID != 0)
							checkEntityIDValid(gdCfgs, row, itemID, false);
						int count = excelReadContext.getIntValue(row++, col);
						
						if(itemID != 0 && count > 0)
							cfg.dmgTransfer.cost.add(new SBean.DummyGoods(itemID, count));
					}
					
					row++;
					cfg.dmgTransfer.lvlReq = excelReadContext.getIntValue(row++, col);
					cfg.dmgTransfer.pointLimit = excelReadContext.getIntValue(row++, col);
				}
				
				final int rowClassType = excelReadContext.locateColumnTag(0, "职业克制属性映射");
				{
					row = rowClassType + 2;
					col = 0;
					
					while(excelReadContext.isNotEmpty(row, 0))
					{
						int classType = excelReadContext.getIntValue(row, col);
						checkClassTypeValid(gdCfgs, row, classType);
						int dmgToPropID = excelReadContext.getIntValue(row, col + 1);
						checkPropertyIDValid(gdCfgs, row, dmgToPropID);
						int dmgByPropID = excelReadContext.getIntValue(row++, col + 2);
						checkPropertyIDValid(gdCfgs, row, dmgByPropID);
						
						cfg.dmgTransfer.dmgTos.put(classType, dmgToPropID);
						cfg.dmgTransfer.dmgBys.put(classType, dmgByPropID);
					}
				}
				
				gdCfgs.expCoinBase = cfg;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.RareBookGroupCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.RareBookCFGS cfg = new SBean.RareBookCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					SBean.RareBookGroupCFGS gCfg = cfgMap.get(cfg.id);
					if(gCfg == null)
					{
						gCfg = new SBean.RareBookGroupCFGS(new ArrayList<>());
						cfgMap.put(cfg.id, gCfg);
					}
					gCfg.books.add(cfg);
					
					col = colStart + 3;
					cfg.classType = excelReadContext.getIntValue(row, col++);
					cfg.lvl = excelReadContext.getIntValue(row, col++);
					if(gCfg.books.size() != cfg.lvl)
						throw new Exception("藏书ID " + cfg.id + " 等级 " + cfg.lvl + " 不连续!");
					
					cfg.rareBookID = excelReadContext.getIntValue(row, col++);
					cfg.rareBookCount = excelReadContext.getIntValue(row, col++);
					cfg.expCoinCost = excelReadContext.getIntValue(row, col++);
					
					cfg.itemCost = new ArrayList<>();
					cfg.itemDoubleCost = new ArrayList<>();
					for(int i=0; i<2; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if(itemID != 0)
						{
							checkEntityIDValid(gdCfgs, row, itemID, true);
							cfg.itemCost.add(new SBean.DummyGoods(itemID, count));
							cfg.itemDoubleCost.add(new SBean.DummyGoods(itemID, count * 2));
							
							if(count <= 0)
								throw new Exception("row " + row + " 藏书升级消耗道具 " + itemID + " 数量为 " + count);
						}
					}
					
					cfg.attrs = new ArrayList<>();
					for(int i=0; i<3; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS();
						attr.id = excelReadContext.getIntValue(row, col++);
						attr.value = excelReadContext.getIntValue(row, col++);
						if (attr.id > 0)
						{
							this.checkPropertyIDValid(gdCfgs, row, attr.id);
							cfg.attrs.add(attr);
						}
					}
					
					row++;
				}
				
				gdCfgs.rarebookGroups = cfgMap;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.GraspGroupCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.GraspCFGS cfg = new SBean.GraspCFGS();
					cfg.id = excelReadContext.getIntValue(row, colStart);
					SBean.GraspGroupCFGS gCfg = cfgMap.get(cfg.id);
					if(gCfg == null)
					{
						gCfg = new SBean.GraspGroupCFGS(new ArrayList<>());
						cfgMap.put(cfg.id, gCfg);
					}
					gCfg.grasps.add(cfg);
					
					col = colStart + 3;
					cfg.lvl = excelReadContext.getIntValue(row, col++);
					if(gCfg.grasps.size() != cfg.lvl)
						throw new Exception("参悟ID " + cfg.id + " 等级 " + cfg.lvl + " 不连续!");
					
					cfg.needExp = excelReadContext.getIntValue(row, col++);
					cfg.rarebookRaise = excelReadContext.getIntValue(row, col++);
					
					cfg.attr = new SBean.AttrCFGS();
					cfg.attr.id = excelReadContext.getIntValue(row, col++);
					cfg.attr.value = excelReadContext.getIntValue(row, col++);
					if (cfg.attr.id > 0)
						this.checkPropertyIDValid(gdCfgs, row, cfg.attr.id);
					
					row++;
				}
				gdCfgs.graspGroups = cfgMap;
			}
			
			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.DMGTransferLevelCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int transferID = excelReadContext.getIntValue(row, col++);
					SBean.DMGTransferLevelCFGS lvlCfg = cfgs.get(transferID);
					if(lvlCfg == null)
					{
						lvlCfg = new SBean.DMGTransferLevelCFGS(new ArrayList<>());
						cfgs.put(transferID, lvlCfg);
					}
					
					col++;
					int lvl = excelReadContext.getIntValue(row, col++);
					if(lvl != lvlCfg.levels.size())
						throw new Exception("row " + row + " 乾坤ID " + transferID + " 等级 " + lvl + " 不连续!");
					
					int propID = excelReadContext.getIntValue(row, col++);
					checkPropertyIDValid(gdCfgs, row, propID);
					int value = excelReadContext.getIntValue(row, col++);
					int pointsReq = excelReadContext.getIntValue(row, col++);
					
					List<SBean.DummyGoods> costItems = new ArrayList<>();
					for(int i = 0; i < 2; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if(itemID != 0)
						{
							checkEntityIDValid(gdCfgs, row, itemID, false);
							costItems.add(new SBean.DummyGoods(itemID, count));
						}
					}
							
					List<SBean.DummyGoods> returnItems = new ArrayList<>();
					for(int i = 0; i < 2; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if(itemID != 0)
						{
							checkEntityIDValid(gdCfgs, row, itemID, false);
							returnItems.add(new SBean.DummyGoods(itemID, count));
						}
					}
					
					lvlCfg.levels.add(new SBean.DMGTransferCFGS(propID, value, pointsReq, costItems, returnItems));
					
					row++;
					col = colStart;
				}
				gdCfgs.dmgTransfers = cfgs;
			}
			
			excelReadContext.ReadSheet(4);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.DMGTransferBuyCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int buyTimes = excelReadContext.getIntValue(row, col++);
					List<SBean.DummyGoods> normalCost = new ArrayList<>();
					for(int i = 0; i < 2; i++)
					{
						int itemID = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						if(itemID != 0)
						{
							checkEntityIDValid(gdCfgs, row, itemID, false);
							normalCost.add(new SBean.DummyGoods(itemID, count));
						}
					}
					int normalGain = excelReadContext.getIntValue(row, col++);
					int discountGain = excelReadContext.getIntValue(row, col++);
					
					List<SBean.DummyGoods> discountCost = new ArrayList<>();
					for(int i = 0; i < 2; i++)
					{
						int count = excelReadContext.getIntValue(row, col++);
						if(i >= normalCost.size())
							continue;
						
						int itemID = normalCost.get(i).id;
						discountCost.add(new SBean.DummyGoods(itemID, count));
					}
					
					cfgs.add(new SBean.DMGTransferBuyCFGS(normalCost, normalGain, discountCost, discountGain));
					if(buyTimes != cfgs.size())
						throw new Exception("row " + row + " 乾坤点第 " + buyTimes + " 次购买不连续");
					
					row++;
					col = colStart;
				}
				gdCfgs.dmgTransferBuys = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadTitleTable(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 0;
				final int rowStart = 0;
				int row = rowStart + 2;
				int col = colStart;
				
				Map<Integer, SBean.TitleCFGS> cfgMap = new HashMap<>();
				Map<Integer, Set<Integer>> typeRanks = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.TitleCFGS cfg = new SBean.TitleCFGS(0, 0, 0, new ArrayList<>(), 0);
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.type = excelReadContext.getIntValue(row, col++);
					if(cfg.type == 0)
						throw new Exception("row " + row + " 称号 " + cfg.id + " 类型 " + cfg.type + " 非法！");
					
					col = colStart + 5;
					cfg.loopTime = excelReadContext.getIntValue(row, col++);
					
					for(int i=0; i<5; i++)
					{
						int attrID = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						if (attrID > 0 && value > 0)
						{
							this.checkPropertyIDValid(gdCfgs, row, attrID);
							cfg.attrs.add(new SBean.AttrCFGS(attrID, value));
						}
					}
					
					col = 17;
					cfg.rank = excelReadContext.getIntValue(row, col++);
					Set<Integer> ranks = typeRanks.get(cfg.type);
					if(ranks == null)
					{
						ranks = new HashSet<>();
						typeRanks.put(cfg.type, ranks);
					}
					
					if(!ranks.add(cfg.rank))
						throw new Exception("row " + row + " title " + cfg.id + " 品质 " + cfg.rank + " 重复！");
					
					if(cfgMap.put(cfg.id, cfg) != null)
						throw new Exception("row " + row +  " 称号ID " + cfg.id + " 重复！");
					
					row++;
				}
				
				gdCfgs.titles = cfgMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	
	private void loadBetaActivityTable(String fileName, GameDataCFGS cfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				SBean.BetaActivityCFGS cfg=new SBean.BetaActivityCFGS();
				final int rowStart = 2;
				final int colStart = 0;
				int currow;
				int curcol;
				int rowOpen = excelReadContext.locateColumnTag(colStart, "活动参与等级");
				cfg.lvlNeed=excelReadContext.getIntValue(rowOpen, colStart+1);
				if(cfg.lvlNeed<=0)
					throw new Exception("活动参与等级小于等于0");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "封测活动推送（完成任务ID）");
				cfg.notifyTaskId=excelReadContext.getIntValue(rowOpen, colStart+1);
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "有奖调研开启等级");
				cfg.surveyNeedLvl = excelReadContext.getIntValue(rowOpen, colStart+1);

				rowOpen = excelReadContext.locateColumnTag(colStart, "题库数量（最后一题的ID）");
				cfg.questionNum = excelReadContext.getIntValue(rowOpen, colStart+1);
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "调研最终奖励");
				currow=rowOpen+2;
				cfg.surveyFinalReward=new ArrayList<SBean.DummyGoods>();
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					if(cfg.surveyFinalReward.size()>=4)
						throw new Exception("调研最终奖励超过4个");
					cfg.surveyFinalReward.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, colStart+1),excelReadContext.getIntValue(currow++, colStart+2)));
				}
				if(cfg.surveyFinalReward.isEmpty())
					throw new Exception("调研最终奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "调研每题奖励配置");
				currow=rowOpen+2;
				cfg.everyQuestionReward=new ArrayList<SBean.DummyGoods>();
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					if(cfg.everyQuestionReward.size()>=4)
						throw new Exception("调研每题奖励超过4个");
					cfg.everyQuestionReward.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, colStart+1),excelReadContext.getIntValue(currow++, colStart+2)));
				}
				if(cfg.everyQuestionReward.isEmpty())
					throw new Exception("调研每题奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "有奖调研第二阶段开启等级");
				cfg.surveyNeedLvl2 = excelReadContext.getIntValue(rowOpen, colStart+1);

				rowOpen = excelReadContext.locateColumnTag(colStart, "第二阶段题库数量（最后一题的ID）");
				cfg.questionNum2 = excelReadContext.getIntValue(rowOpen, colStart+1);
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "第二阶段调研最终奖励");
				currow=rowOpen+2;
				cfg.surveyFinalReward2=new ArrayList<SBean.DummyGoods>();
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					if(cfg.surveyFinalReward2.size()>=4)
						throw new Exception("第二阶段调研最终奖励超过4个");
					cfg.surveyFinalReward2.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, colStart+1),excelReadContext.getIntValue(currow++, colStart+2)));
				}
				if(cfg.surveyFinalReward2.isEmpty())
					throw new Exception("第二阶段调研最终奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "第二阶段调研每题奖励配置");
				currow=rowOpen+2;
				cfg.everyQuestionReward2=new ArrayList<SBean.DummyGoods>();
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					if(cfg.everyQuestionReward2.size()>=4)
						throw new Exception("第二阶段调研每题奖励超过4个");
					cfg.everyQuestionReward2.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, colStart+1),excelReadContext.getIntValue(currow++, colStart+2)));
				}
				if(cfg.everyQuestionReward2.isEmpty())
					throw new Exception("第二阶段调研每题奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "封测钜献");
				currow=rowOpen+2;
				cfg.dailyLogin=new TreeMap<Integer, SBean.DailyLoginCFGS>();
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					SBean.DailyLoginCFGS dailyitem=new SBean.DailyLoginCFGS(new ArrayList<SBean.DummyGoods>());
					curcol=colStart+2;
					while(excelReadContext.isNotEmpty(currow, curcol)){
						if(dailyitem.rewards.size()>=4)
							throw new Exception(currow+"行封测钜献奖励超过4个");
						dailyitem.rewards.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, curcol),excelReadContext.getIntValue(currow, curcol+1)));
						curcol+=2;
					}
					cfg.dailyLogin.put(excelReadContext.getIntValue(currow++, colStart+1), dailyitem);
				}
				if(cfg.dailyLogin.isEmpty())
					throw new Exception("封测钜献奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "等级冲刺");
				currow=rowOpen+2;
				cfg.lvlUpReward=new TreeMap<Integer, SBean.LvlUpRewardCFGS>();
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					SBean.LvlUpRewardCFGS lvlupitem=new SBean.LvlUpRewardCFGS(new ArrayList<SBean.DummyGoods>(), 0);
					lvlupitem.vipPoint=excelReadContext.getIntValue(currow, colStart+2);
					if(lvlupitem.vipPoint<0)
						throw new Exception(currow + "行每日V礼包奖励VIP点数小于0");
					curcol=colStart+3;
					while(excelReadContext.isNotEmpty(currow, curcol)){
						if(lvlupitem.rewards.size()>=4)
							throw new Exception(currow+"行每日V礼包奖励超过4个");
						lvlupitem.rewards.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, curcol),excelReadContext.getIntValue(currow, curcol+1)));
						curcol+=2;
					}
					cfg.lvlUpReward.put(excelReadContext.getIntValue(currow++, colStart+1), lvlupitem);
				}
				if(cfg.lvlUpReward.isEmpty())
					throw new Exception("每日V礼包奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "使命召唤");
				currow=rowOpen+1;
				cfg.completUserInfoReward=new ArrayList<SBean.DummyGoods>();
				cfg.userDataLvl = excelReadContext.getIntValue(currow++, colStart+1);
				currow++;
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					if(cfg.completUserInfoReward.size()>=4)
						throw new Exception("使命召唤奖励超过4个");
					cfg.completUserInfoReward.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, colStart+1),excelReadContext.getIntValue(currow++, colStart+2)));
				}
				if(cfg.completUserInfoReward.isEmpty())
					throw new Exception("使命召唤奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "最后冲刺");
				currow=rowOpen+1;
				cfg.lastBetaStartDay=excelReadContext.getIntValue(currow, colStart+1);
				currow+=2;
				cfg.lastBetaReward=new TreeMap<Integer, SBean.LastBetaRewardCFGS>();
				while(excelReadContext.isNotEmpty(currow, colStart+1)){
					SBean.LastBetaRewardCFGS lastbetaitem=new SBean.LastBetaRewardCFGS(new ArrayList<SBean.DummyGoods>());
					curcol=colStart+2;
					while(excelReadContext.isNotEmpty(currow, curcol)){
						if(lastbetaitem.rewards.size()>=4)
							throw new Exception(currow+"行最后冲刺奖励超过4个");
						lastbetaitem.rewards.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, curcol),excelReadContext.getIntValue(currow, curcol+1)));
						curcol+=2;
					}
					cfg.lastBetaReward.put(excelReadContext.getIntValue(currow++, colStart+1), lastbetaitem);
				}
				if(cfg.lastBetaStartDay<=0)
					throw new Exception("最后冲刺开始时间小于等于0");
				if(cfg.lastBetaReward.isEmpty())
					throw new Exception("最后冲刺奖励未设置");
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "限时放送");
				currow = rowOpen + 1;
				String[] startstring = excelReadContext.getStringValue(currow++, colStart + 1).split(":");
				cfg.onTimeStartTime = Integer.parseInt(startstring[0]) * 3600 + Integer.parseInt(startstring[1]) * 60 + Integer.parseInt(startstring[2]);
				String[] endstring = excelReadContext.getStringValue(currow, colStart + 1).split(":");
				cfg.onTimeEndTime = Integer.parseInt(endstring[0]) * 3600 + Integer.parseInt(endstring[1]) * 60 + Integer.parseInt(endstring[2]);
				currow+=2;
				cfg.onTimeReward = new HashMap<Integer, SBean.onTimeRewardCFGS>();
				while(excelReadContext.isNotEmpty(currow, colStart)){
					SBean.onTimeRewardCFGS ontimeitem = new SBean.onTimeRewardCFGS(new ArrayList<SBean.DummyGoods>());
					curcol=colStart+1;
					while(excelReadContext.isNotEmpty(currow, curcol)){
						if(ontimeitem.rewards.size()>=3)
							throw new Exception(currow+"行限时领奖奖励超过3个");
						ontimeitem.rewards.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, curcol),excelReadContext.getIntValue(currow, curcol+1)));
						curcol+=2;
					}
					cfg.onTimeReward.put(excelReadContext.getIntValue(currow++, colStart), ontimeitem);
				}
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "强化比拼");
				currow = rowOpen + 2;
				cfg.strengthenReward = new HashMap<>();
				while(excelReadContext.isNotEmpty(currow, colStart)){
					SBean.strengthenRewardCFGS strengthenitem = new SBean.strengthenRewardCFGS(new ArrayList<SBean.DummyGoods>());
					curcol=colStart+1;
					while(excelReadContext.isNotEmpty(currow, curcol)){
						if(strengthenitem.rewards.size()>=3)
							throw new Exception(currow+"行强化比拼奖励超过3个");
						strengthenitem.rewards.add(new SBean.DummyGoods(excelReadContext.getIntValue(currow, curcol),excelReadContext.getIntValue(currow, curcol+1)));
						curcol+=2;
					}
					cfg.strengthenReward.put(excelReadContext.getIntValue(currow++, colStart), strengthenitem);
				}
				
				rowOpen = excelReadContext.locateColumnTag(colStart, "官网调研");
				currow = rowOpen + 3;
				cfg.officialResearchReward = new SBean.DummyGoods(excelReadContext.getIntValue(currow, colStart), excelReadContext.getIntValue(currow, colStart+1));
				
				cfgs.betaActivity = cfg;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	private void loadPetAchieve(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.PetAchieveGroupCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.PetAchieveCFGS cfg = new SBean.PetAchieveCFGS(0, 0, 0, 0, new ArrayList<>());
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.PET_ACHIEVE_TYPE_LEVEL:
					case GameData.PET_ACHIEVE_TYPE_STAR:
					case GameData.PET_ACHIEVE_TYPE_BREAKSKILL:
						break;
					default:
						throw new Exception("随从成就系统条件类型 " + cfg.type + " 非法！");
					}
					SBean.PetAchieveGroupCFGS groupCfg = cfgMap.get(cfg.type);
					if(groupCfg == null)
					{
						groupCfg = new SBean.PetAchieveGroupCFGS(new ArrayList<>());
						cfgMap.put(cfg.type, groupCfg);
					}
					groupCfg.achieves.add(cfg);
					
					col = colStart + 3;
					cfg.petCnt = excelReadContext.getIntValue(row, col++);
					cfg.cond = excelReadContext.getIntValue(row, col++);
					
					for(int i=0; i<2; i++)
					{
						int attrID = excelReadContext.getIntValue(row, col++);
						int value = excelReadContext.getIntValue(row, col++);
						if (attrID > 0 && value > 0)
						{
							this.checkPropertyIDValid(gdCfgs, row, attrID);
							cfg.attrs.add(new SBean.AttrCFGS(attrID, value));
						}
					}
					
					row++;
				}
				
				gdCfgs.petAchieveGroups = cfgMap;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	private void loadUniqueSkill(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.UniqueSkillCFG> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.UniqueSkillCFG cfg = new SBean.UniqueSkillCFG(0, new ArrayList<>());
					cfg.id = excelReadContext.getIntValue(row, col++);
					for(int i=0; i<5; i++)
					{
						int skillID  = excelReadContext.getIntValue(row, col++);
						checkSkillIDValid(gdCfgs, row, skillID);
						cfg.skills.add(skillID);
					}
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 绝技ID " + cfg.id + " 重复！");
					
					row++;
				}
				
				gdCfgs.uniqueSkills = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadClimbTowerTable(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.ClimbTowerFloorCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ClimbTowerFloorDataCFGS floorCfg = new SBean.ClimbTowerFloorDataCFGS();
					floorCfg.groupId = excelReadContext.getIntValue(row, col++);
					floorCfg.floor = excelReadContext.getIntValue(row, col++);
					if (cfgMap.get(floorCfg.groupId) != null)
					{
						SBean.ClimbTowerFloorCFGS localCfg = cfgMap.get(floorCfg.groupId); 
						if (localCfg.datas.get(floorCfg.floor) != null)
							throw new Exception("爬塔系统 row" + row + " 组" + floorCfg.groupId + " 层" + floorCfg.floor + " 重复");
					}
					col += 2;
					floorCfg.dungeonId = excelReadContext.getIntValue(row, col++);
					if (gdCfgs.climbTowerMaps.get(floorCfg.dungeonId) == null)
						throw new Exception("爬塔系统 组id " + floorCfg.groupId + " 层id " + floorCfg.floor + " 对应副本ID " + floorCfg.dungeonId + " 副本配置表中无此id！");
					SBean.ClimbTowerFloorCFGS cfg = cfgMap.get(floorCfg.groupId);
					if(cfg == null)
					{
						cfg = new SBean.ClimbTowerFloorCFGS(new HashMap<>());
						cfgMap.put(floorCfg.groupId, cfg);
					}
					cfg.datas.put(floorCfg.floor, floorCfg);
					row++;
				}
				gdCfgs.climbTowerFloor = cfgMap;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.ClimbTowerFameCFGS> cfgMap = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ClimbTowerFameDataCFGS fameCfg = new SBean.ClimbTowerFameDataCFGS();
					fameCfg.groupId = excelReadContext.getIntValue(row, col++);
					fameCfg.level = excelReadContext.getIntValue(row, col++);
					col++;
					fameCfg.levelUpNeedFame = excelReadContext.getIntValue(row, col++);
					List<SBean.DummyGoods> rewards = new ArrayList<>();
					for (int i = 0; i < 3; ++i)
					{
						int rewardId = excelReadContext.getIntValue(row, col++);
						int rewardCount = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdCfgs, row, rewardId, true);
						if (rewardId != 0)
							rewards.add(new SBean.DummyGoods(rewardId, rewardCount));
					}
					fameCfg.rewards = rewards;
					fameCfg.canGetStunt = excelReadContext.getIntValue(row, col++);
					List<Integer> list = excelReadContext.getIntegerList(row, col++, ";");
					Set<Integer> donateIds = new HashSet<>();
					fameCfg.donateId = new ArrayList<>();
					for(int id: list)
					{
						fameCfg.donateId.add(id);
						if(!donateIds.add(id))
							throw new Exception("爬塔声望 组" + fameCfg.groupId + "  声望等级" + fameCfg.level + " 捐赠物品id " + id + " 重复！");
						checkEntityIDValid(gdCfgs, row, id, true);
					}
					SBean.ClimbTowerFameCFGS cfg = cfgMap.get(fameCfg.groupId);
					if(cfg == null)
					{
						cfg = new SBean.ClimbTowerFameCFGS(new HashMap<>());
						cfgMap.put(fameCfg.groupId, cfg);
					}
					cfg.datas.put(fameCfg.level, fameCfg);
					
					++row;
				}
				
				gdCfgs.climbTowerFame = cfgMap;
			}
			
			excelReadContext.ReadSheet(3);
			{
				SBean.ClimbTowerBaseDataCFGS cfg = new SBean.ClimbTowerBaseDataCFGS();
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "独立字段");
					int row = rowStart + 1;
					int col = colStart + 1;
					
					cfg.needlvl = excelReadContext.getIntValue(row++, col);
					cfg.displaylvl = excelReadContext.getIntValue(row++, col);
					cfg.dayTimes = excelReadContext.getIntValue(row++, col);
					cfg.sweepParam = excelReadContext.getFloatValue(row++, col);
					if(cfg.sweepParam > 1)
						throw new Exception("爬塔系统 其他字段 扫荡参数非法！");
					cfg.needDiamond = excelReadContext.getIntegerList(row++, col, ";");
//					List<Integer> list = excelReadContext.getIntegerList(row++, col, ";");
//					Set<Integer> needDiamonds = new HashSet<>();
//					for(int count: list)
//					{
//						cfg.needDiamond.add(count);
//						if(!needDiamonds.add(count))
//							throw new Exception("爬塔系统 其他字段 购买次数所需元宝重复！");
//					}
					cfg.notInTime = excelReadContext.getIntValue(row++, col);
				}
				gdCfgs.climbTowerBaseData = cfg;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}
	public void loadSectDeliverTable(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				SBean.SectDeliverCFGS cfgs = new SBean.SectDeliverCFGS();
				final int colStart = 1;
				final int rowStart = 0;
				int row = rowStart;
				int col = colStart;
				
				cfgs.startLevel = excelReadContext.getIntValue(row++, col);
				List<String> list = excelReadContext.getStringList(row++, col, ";");
				cfgs.doubleBonusTimes = new ArrayList<Integer>();
				for(String l : list)
				{
					int t = GameTime.parseSecondOfDay(l);
					cfgs.doubleBonusTimes.add(t);
				}
				cfgs.deliverTimes = excelReadContext.getIntValue(row++, col);
				cfgs.robTimes = excelReadContext.getIntValue(row++, col);
				cfgs.beRobbedTimes = excelReadContext.getIntValue(row++, col);
				list = excelReadContext.getStringList(row++, col, ";");
				cfgs.protectEndTime = new ArrayList<Integer>();
				for(String l : list)
				{
					int t = GameTime.parseSecondOfDay(l);
					cfgs.protectEndTime.add(t);
				}
				if (cfgs.protectEndTime.size() != 2)
					throw new Exception("帮派运镖投保禁用时间不为一个时间段");
				cfgs.protectDiamond = excelReadContext.getIntValue(row++, col);
				cfgs.refreshDiamond = excelReadContext.getIntegerList(row++, col, ";");
				cfgs.wishDiamonds = excelReadContext.getIntegerList(row++, col, ";"	);
				cfgs.moveSpeed = excelReadContext.getIntValue(row++, col);
				cfgs.stopDistance = excelReadContext.getIntValue(row++, col);
				cfgs.faildByTime = excelReadContext.getIntValue(row++, col);
				cfgs.robSuccessHP = excelReadContext.getIntValue(row++, col);
				cfgs.needVit = excelReadContext.getIntValue(row++, col);
				cfgs.wishGet = excelReadContext.getIntValue(row++, col);
				cfgs.teamBuffs = excelReadContext.getIntegerList(row++, col, ";");
				cfgs.searchHelpTime = excelReadContext.getIntValue(row++, col);
				
				{
					int buffID = cfgs.teamBuffs.get(0);
					SBean.BuffCFGS buffCfg = gdCfgs.buffs.get(buffID);
					if(buffCfg == null)
						throw new Exception("镖车组队 伤害减免 buff " + buffID + " 不存在");
					
					if(buffCfg.affectType != GameData.EBUFF_PROP || buffCfg.affectID != BaseRole.EPROPID_DMGBY || buffCfg.valueType != GameData.VALUE_TYPE_FIXED || buffCfg.interval > 0 || buffCfg.owner != 1)
						throw new Exception("镖车组队 伤害减免 buff： " + buffID + " affectID " + buffCfg.affectID + " valueType： " + buffCfg.valueType + " interval： " + buffCfg.interval + " target: " + buffCfg.owner + " 非法！");
				}
				
				{
					int buffID = cfgs.teamBuffs.get(1);
					SBean.BuffCFGS buffCfg = gdCfgs.buffs.get(buffID);
					if(buffCfg == null)
						throw new Exception("镖车组队  回复气血 buff " + buffID + " 不存在");
					
					if(buffCfg.affectType != GameData.EBUFF_PROP || buffCfg.affectID != BaseRole.EPROPID_MAXHP || buffCfg.valueType != GameData.VALUE_TYPE_PERCENT || buffCfg.interval <= 0 || buffCfg.owner != 1)
						throw new Exception("镖车组队 回复气血 buff： " + buffID + " affectID： " + buffCfg.affectID + " valueType： " + buffCfg.valueType + " interval： " + buffCfg.interval + " target: " + buffCfg.owner + " 非法！");
				}
				
				cfgs.invincibleBuff = excelReadContext.getIntValue(row++, col);
				{
					int buffID = cfgs.invincibleBuff;
					SBean.BuffCFGS buffCfg = gdCfgs.buffs.get(buffID);
					if(buffCfg == null)
						throw new Exception("镖车无敌 buff " + buffID + " 不存在");
					
					if(buffCfg.affectType != GameData.EBUFF_STATUS || buffCfg.affectID != Behavior.EBINVINCIBLE || buffCfg.interval > 0 || buffCfg.owner != 1)
						throw new Exception("镖车无敌 buff： " + buffID + " affectID： " + buffCfg.affectID + " valueType： " + buffCfg.valueType + " interval： " + buffCfg.interval + " target " + buffCfg.owner + " 非法！");
				}
				row++;
				cfgs.dmgBy = excelReadContext.getIntValue(row++, col);
				cfgs.maxHp = excelReadContext.getIntValue(row++, col);
				cfgs.doubleBonusRate = excelReadContext.getIntValue(row++, col);
				cfgs.robValidNum = excelReadContext.getIntValue(row++, col);
				cfgs.robValidDistance = excelReadContext.getIntValue(row++, col);
				cfgs.roleLevelNeed = excelReadContext.getIntValue(row++, col);
				if(cfgs.roleLevelNeed <= 0)
					throw new Exception("角色参与目标等级必须大于0");
				cfgs.rewardForQuality = excelReadContext.getIntegerList(row++, col, ";");
				if(cfgs.rewardForQuality.isEmpty())
					throw new Exception("劫镖任务奖励赏金点未设置");

				gdCfgs.sectDeliver = cfgs;
			}
			excelReadContext.ReadSheet(1);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				SBean.SectDeliverTaskCFGS cfgs = new SBean.SectDeliverTaskCFGS();
				cfgs.tasks = new ArrayList<>();
				int check = 0;
				while(excelReadContext.isNotEmpty(row, col))
				{
					SBean.SectDeliverTask item = new SBean.SectDeliverTask();
					item.id = excelReadContext.getIntValue(row, col++);
					item.name = excelReadContext.getStringValue(row, col++);
					item.iconId = excelReadContext.getIntValue(row, col++);
					item.vehicleId = excelReadContext.getIntValue(row, col++);
					item.weight = excelReadContext.getIntValue(row, col++);
					item.rewardExpRate = excelReadContext.getIntValue(row, col++);
					item.rewardMoneyRate = excelReadContext.getIntValue(row, col++);
					item.itemId = excelReadContext.getIntValue(row, col++);
					if( this.checkItemIDValid(gdCfgs, item.itemId) == false && item.itemId != -1)// -1 表示预留
						throw new Exception("运镖奖励道具非法！ row = " + row + ", col = " + col);
					item.firstRobLeft = excelReadContext.getIntValue(row, col++);
					item.secondRobLeft = excelReadContext.getIntValue(row, col++);
					item.thirdRobLeft = excelReadContext.getIntValue(row, col++);
					if(item.id != ( ++check ))
						throw new Exception("运镖任务id不连续！");
					cfgs.tasks.add(item);
					row++;
					col = colStart;
				}
				gdCfgs.sectDeliverTask = cfgs;
			}
			excelReadContext.ReadSheet(2);	
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				SBean.SectDeliverVehicleCFGS cfgs = new SBean.SectDeliverVehicleCFGS();
				cfgs.vehicles = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, col))
				{
					SBean.SectDeliverVehicle item = new SBean.SectDeliverVehicle();
					item.id = excelReadContext.getIntValue(row, col++);
					item.quality = excelReadContext.getIntValue(row, col++);
					
					col = colStart + 4;
					item.damageLimit = excelReadContext.getIntValue(row, col++);
					item.defN = excelReadContext.getIntValue(row, col++);
					item.ctr = excelReadContext.getIntValue(row, col++);
					item.tou = excelReadContext.getIntValue(row, col++);
					item.defC = excelReadContext.getIntValue(row, col++);
					item.defW = excelReadContext.getIntValue(row, col++);
					item.radius = excelReadContext.getIntValue(row, col++);
					if(item.quality > gdCfgs.sectDeliver.rewardForQuality.size())
						throw new Exception("镖车品质超出劫镖奖励配置数量");
					cfgs.vehicles.add(item);
					row++;
					col = colStart;
				}
				gdCfgs.sectDeliverVehicle = cfgs;
			}
			excelReadContext.ReadSheet(3);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				SBean.SectDeliverRouteCFGS cfgs = new SBean.SectDeliverRouteCFGS();
				cfgs.deliverRoutes = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, col))
				{
					SBean.SectDeliverRoute item = new SBean.SectDeliverRoute();
					item.id = excelReadContext.getIntValue(row, col++);
					item.name = excelReadContext.getStringValue(row, col++);
					item.picId = excelReadContext.getIntValue(row, col++);
					item.startMapId = excelReadContext.getIntValue(row, col++);
					this.checkMapID(gdCfgs, row, item.startMapId);
					item.endMapId = excelReadContext.getIntValue(row, col++);
					this.checkMapID(gdCfgs, row, item.endMapId);
					item.startNPC = excelReadContext.getIntValue(row, col++);
					this.checkNPCPositionID(gdCfgs.mapClusters.get(item.startMapId), row, item.startNPC);
					item.endNPC = excelReadContext.getIntValue(row, col++);
					this.checkNPCPositionID(gdCfgs.mapClusters.get(item.endMapId), row, item.endNPC);
					cfgs.deliverRoutes.add(item);
					row++;
					col = colStart;
				}
				gdCfgs.sectDeliverRoute = cfgs;
			}
			excelReadContext.ReadSheet(4);
			{
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				SBean.SectDeliverWishCFGS cfgs = new SBean.SectDeliverWishCFGS();
				cfgs.wishes = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, col))
				{
					SBean.SectDeliverWish item = new SBean.SectDeliverWish();
					item.wishType = excelReadContext.getIntValue(row, col++);
					item.initValue = excelReadContext.getIntValue(row, col++);
					item.first5max = excelReadContext.getIntValue(row, col++);
					item.first10max = excelReadContext.getIntValue(row, col++);
					item.first15max = excelReadContext.getIntValue(row, col++);
					item.first30max = excelReadContext.getIntValue(row, col++);
					item.first60max = excelReadContext.getIntValue(row, col++);
					item.maxValue = excelReadContext.getIntValue(row, col++);
					
					cfgs.wishes.add(item);
					row++;
					col = colStart;
				}
				if(cfgs.wishes.size() < 3)
					throw new Exception("祝福类型小于3种！");
				gdCfgs.sectDeliverWish = cfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}

	private void loadRollNoticeTable(String fileName, GameDataCFGS cfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int colStart = 1;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				SBean.RollNoticeCFGS rncfgs = new SBean.RollNoticeCFGS();
				rncfgs.transfers = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.sectLevels = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.equipStrengthens = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.equipStarups = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.gemLevels = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.hourseStarups = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.weaponStarups = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.petStarups = excelReadContext.getIntegerList(row ++, col, ";");
				rncfgs.firstDayPkRank = excelReadContext.getIntValue(row ++, col);
				rncfgs.pkRank = excelReadContext.getIntValue(row ++, col);
				cfgs.rollNotice = rncfgs;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadRemainActivity(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.RemainActivityCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.RemainActivityCFGS cfg = new SBean.RemainActivityCFGS(0, 0, 0, new ArrayList<>());
					cfg.id = excelReadContext.getIntValue(row, colStart);
					
					col = colStart + 2;
					cfg.type = excelReadContext.getIntValue(row, col++);
					switch (cfg.type)
					{
					case GameData.REMAIN_ACTIVITY_TYPE_LEVEL:
					case GameData.REMAIN_ACTIVITY_TYPE_LOGINDAYS:	
						break;
					default:
						throw new Exception("七日留存活动 row " + row + " 条件类型 " + cfg.type + " 非法！");
					}
					cfg.cond = excelReadContext.getIntValue(row, col++);
					cfg.rewards = new ArrayList<>();
					for (int i = 0; i < 5; i++)
						cfg.rewards.add(new SBean.MutiDummyGoods(new HashMap<>()));
					for (int i = 0; i < 6; i++)
					{
						List<Integer> items = excelReadContext.getIntegerList(row, col++, ";");
						int count = excelReadContext.getIntValue(row, col++);
						if (items.size() != 5)
							throw new Exception("七日留存活动 row " + row + " 奖励道具 非法");
						for (int j = 0; j < items.size(); j++)
						{
							int item = items.get(j);
							if (item == 0)
								continue;
							checkItemIDValid(gdCfgs, item);
							cfg.rewards.get(j).dummyGoods.merge(item, count, (ov, nv) -> ov + nv);
						}
					}
					cfgs.add(cfg);
					if(cfgs.size() != cfg.id)
						throw new Exception("七日留存活动 row " + row + " 序号 " + cfg.id + " 不连续！");
					
					row++;
				}
				
				gdCfgs.remainActivitys = cfgs;
			}
		}
	}

	private void loadComposeTable(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.ComposeCFGS> cfgs = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ComposeCFGS cfg = new SBean.ComposeCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					if (cfg.id != cfgs.size() + 1)
						throw new Exception("合成配方 " + row + " 的配方 " + cfg.id + " ID不连续！");
					int productid = excelReadContext.getIntValue(row, col);
					if (!checkItemIDValid(gdCfgs, productid))
						throw new Exception("合成配方 " + row + " 的产出物品 " + productid + " ID无效！");
					int productnum = excelReadContext.getIntValue(row, col + 1);
					if (productnum <= 0)
						throw new Exception("合成配方 " + row + " 的产出物品 " + productid + " 数量必须大于0！");
					cfg.product = new SBean.DummyGoods(productid, productnum);
					col += 2;
					List<SBean.DummyGoods> items = new ArrayList<>();
					for (int i = 0; i < 6; i++)
					{
						if (!excelReadContext.isNotEmpty(row, col))
							break;
						int itemid = excelReadContext.getIntValue(row, col);
						if (itemid == 0)
							break;
						if (!checkItemIDValid(gdCfgs, itemid))
							throw new Exception("合成配方 " + row + " 的碎片物品 " + itemid + " ID无效！");
						int itemnum = excelReadContext.getIntValue(row, col + 1);
						if (itemnum <= 0)
							throw new Exception("合成配方 " + row + " 的碎片物品 " + itemid + " 数量必须大于0！");
						items.add(new SBean.DummyGoods(itemid, itemnum));
						col += 2;
					}
					cfg.materials = items;
					cfgs.add(cfg);
					row++;
				}
				
				gdCfgs.composes = cfgs;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	
	
	private void loadScheduleTable(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				int maxMapID = 1 << 16;
				Map<Integer, SBean.ScheduleDataCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ScheduleDataCFGS cfg = new SBean.ScheduleDataCFGS();
					cfg.type = excelReadContext.getIntValue(row, col++);
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.mapid = excelReadContext.getIntValue(row, col++);
					if (cfgs.values().stream().anyMatch(cfgitem -> cfg.type == cfgitem.type && cfg.mapid == cfgitem.mapid))
						throw new Exception("日程表 " + row + " 的类型 " + cfg.type + " 对应mapId " + cfg.mapid + " 重复！");
					
					if(cfg.mapid > maxMapID)
						throw new Exception("日程表 " + row + " 的类型 " + cfg.type + " 对应mapId " + cfg.mapid + " 超过上限 " + maxMapID);
					
					col++;
					col++;
					cfg.num = excelReadContext.getIntValue(row, col++);
					if ((cfg.type == GameData.SCHEDULE_TYPE_KILL_MAPBOSS || cfg.type == GameData.SCHEDULE_TYPE_EMERGENCY) && cfg.num != 1)
						throw new Exception("日程表 " + row + " 的类型 " + cfg.type + " 对应次数 " + cfg.num + " 必须为1！");
					cfg.activity = excelReadContext.getIntValue(row, col++);
					
					col = 11;
					List<Integer> openDays = excelReadContext.getIntegerList(row, col++, ";");
					switch (cfg.type)
					{
					case GameData.SCHEDULE_TYPE_ACTIVITY_MAP:
					case GameData.SCHEDULE_TYPE_TEAM_MAP_COPY:
					case GameData.SCHEDULE_TYPE_NORMAL_MAP_COPY:
					case GameData.SCHEDULE_TYPE_DIFF_MAP_COPY:
					case GameData.SCHEDULE_TYPE_TREASUREMAP:
					case GameData.SCHEDULE_TYPE_CLIMB_TOWER:
					case GameData.SCHEDULE_TYPE_DIYSKILL:
					case GameData.SCHEDULE_TYPE_SECT_MAPCOPY:
					case GameData.SCHEDULE_TYPE_SECT_ESCORTCAR:
					case GameData.SCHEDULE_TYPE_FINISH_ANY_SECT_TASK:
					case GameData.SCHEDULE_TYPE_NORMAL_ARENA:
					case GameData.SCHEDULE_TYPE_BW_MAPCOPY:
					case GameData.SCHEDULE_TYPE_SUPER_ARENA:
					case GameData.SCHEDULE_TYPE_FORCEWAR:
					case GameData.SCHEDULE_TYPE_PRODUCE:
					case GameData.SCHEDULE_TYPE_SELL_EQUIPS:
					case GameData.SCHEDULE_TYPE_KILL_MAPBOSS:
					case GameData.SCHEDULE_TYPE_EXCHANGE:
					case GameData.SCHEDULE_TYPE_MARRIAGE_TASK:
					case GameData.SCHEDULE_TYPE_QUESTION:
					case GameData.SCHEDULE_TYPE_JUSTICE_MAP:
					case GameData.SCHEDULE_TYPE_EMERGENCY:
					case GameData.SCHEDULE_TYPE_SECT_FLAG_BATTLE:
					case GameData.SCHEDULE_TYPE_TOWER_DEFENCE:
					case GameData.SCHEDULE_TYPE_NPC_MAP:
					case GameData.SCHEDULE_TYPE_NPC_PRAY:
						break;
					case GameData.SCHEDULE_TYPE_STELE:
						{
							for(int w: openDays)
							{
								if(!gdCfgs.stele.base.openDays.contains(w))
									throw new Exception("row " + row + " 日程表 　类别 " + cfg.type  + " 开放日 " + w + " 太玄碑文 开放日 不一致");
							}
							
							String s = excelReadContext.getStringValue(row, col++);
							String[] times = s.split(";");
							int startTime = GameTime.parseSecondOfDay(times[0]);
							int endTime = GameTime.parseSecondOfDay(times[1]);
							if(startTime != gdCfgs.stele.base.startTime)
								throw new Exception("row " + row + " 日程表 　类别 " + cfg.type  + " 开始时间 " + startTime + " 太玄碑文 开始时间  " + (gdCfgs.stele.base.startTime) + " 不一致");
							
							if((endTime - startTime) < gdCfgs.stele.base.lastTime)
								throw new Exception("row " + row + " 日程表 　类别 " + cfg.type  + " 持续时间 " + (endTime - startTime) + " 小于太玄碑文 持续时间  " + (gdCfgs.stele.base.lastTime));
						}
						break;
					default:
						throw new Exception("日程表 " + row + " 的类型 " + cfg.type + " 未定义！");
					}
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 日程表 编号 " + cfg.id + " 重复!");
					row++;
				}
				gdCfgs.scheduleData = cfgs;
			}

			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.ScheduleRewardsCFGS> cfgs2 = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ScheduleRewardsCFGS cfg = new SBean.ScheduleRewardsCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.activity = excelReadContext.getIntValue(row, col++);
					cfg.bagCount = excelReadContext.getIntValue(row, col++);
					cfg.levels = excelReadContext.getIntegerList(row, col++,";");
					cfg.drop = new ArrayList<SBean.ScheduleDropCFGS>();
					for(int i =0 ;i<cfg.levels.size();i++){
						
						SBean.ScheduleDropCFGS cfgdrop = new SBean.ScheduleDropCFGS();
						cfgdrop.fixedDropID = excelReadContext.getIntValue(row, col++);
						cfgdrop.randomDropIDs = excelReadContext.getIntValue(row, col++);
						
						checkRandomDropIDValid(gdCfgs, row, cfgdrop.randomDropIDs);
						
						cfgdrop.randomDropCnt = excelReadContext.getIntValue(row, col++);
						
						cfg.drop.add(cfgdrop);
					}
					
					cfgs2.put(cfg.id, cfg);
					row++;
				}
				gdCfgs.scheduleRewards = cfgs2;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}

	private void loadMessageBoardTable(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.MessageBoardSide> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MessageBoardCFGS cfg = new SBean.MessageBoardCFGS();
					cfg.side = excelReadContext.getIntValue(row, col++);
					if (cfg.side != GameData.MESSAGE_BOARD_SIDE_PRO && cfg.side!= GameData.MESSAGE_BOARD_SIDE_CON)
						throw new Exception("留言板配置 " + row + " 的正反面 " + cfg.id + " 类型错误！");
					if(!cfgs.containsKey(cfg.side))
						cfgs.put(cfg.side, new SBean.MessageBoardSide(new ArrayList<>()));
					cfg.id = excelReadContext.getIntValue(row, col++);
					if (cfg.id != cfgs.get(cfg.side).messageBoards.size() + 1)
						throw new Exception("留言板配置 " + row + " 的留言板 " + cfg.id + " ID不连续！");
					cfg.maxLength = excelReadContext.getIntValue(row, col++);
					if (cfg.maxLength <= 0)
						throw new Exception("留言板配置 " + row + " 的最大文本长度 " + cfg.maxLength + " 必须大于0！");
					cfg.consumeType = excelReadContext.getIntValue(row, col++);
					if (!(cfg.consumeType == GameData.GAME_ITEM_TYPE_COIN || cfg.consumeType == GameData.GAME_ITEM_TYPE_DIAMOND))
						throw new Exception("留言板配置 " + row + " 的消耗货币 " + cfg.consumeType + " 类型必须为铜钱或元宝！");
					Map<Integer, Integer> timecost = new HashMap<>();
					for (int i = 0; i < 3; i++)
					{
						int time = excelReadContext.getIntValue(row, col);
						if (time <= 0)
							throw new Exception("留言板配置 " + row + " 的持续时间 " + time + " 必须大于0！");
						int cost = excelReadContext.getIntValue(row, col + 1);
						if (cost <= 0)
							throw new Exception("留言板配置 " + row + " 的花费 " + cost + " 必须大于0！");
						timecost.put(time, cost);
						col += 2;
					}
					cfg.rewritePercent = excelReadContext.getFloatValue(row, col++);
					if (cfg.rewritePercent < 0)
						throw new Exception("留言板配置 " + row + " 的覆盖费用倍率 " + cfg.rewritePercent + " 必须大于等于0！");
					cfg.protectTime = excelReadContext.getIntValue(row, col++);
					if (cfg.protectTime < 0)
						throw new Exception("留言板配置 " + row + " 的保护时间 " + cfg.protectTime + " 必须大于等于0！");
					cfg.timeCost = timecost;
					cfgs.get(cfg.side).messageBoards.add(cfg);
					row++;
				}
				gdCfgs.messageBoards = cfgs;
			}

			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 0;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;
				SBean.MessageBoardCommonCFGS commoncfg = new SBean.MessageBoardCommonCFGS();
				commoncfg.dayCommentTime = excelReadContext.getIntValue(row++, col);
				if (commoncfg.dayCommentTime <= 0)
					throw new Exception("留言板配置 " + row + " 的每日评论次数 " + commoncfg.dayCommentTime + " 必须大于0！");
				commoncfg.openLevel = excelReadContext.getIntValue(row++, col);
				if (commoncfg.openLevel <= 0)
					throw new Exception("留言板配置 " + row + " 的开启等级 " + commoncfg.openLevel + " 必须大于0！");
				commoncfg.anonymousName = excelReadContext.getStringValue(row++, col);
				if (commoncfg.anonymousName.isEmpty())
					throw new Exception("留言板配置 " + row + " 的匿名名称不能为空！");
				commoncfg.replaceNotice = excelReadContext.getStringValue(row, col);
				if (commoncfg.replaceNotice.isEmpty())
					throw new Exception("留言板配置 " + row + " 的被覆盖通知不能为空！");
				gdCfgs.messageBoardCommon = commoncfg;
			}
			System.out.println("load table " + fileName + " success.");
		}
	}

	private void loadArmorTable(String fileName, GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.ArmorCFGS armorCFGS = new SBean.ArmorCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				List<SBean.ArmorTypeCFGS> cfgs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ArmorTypeCFGS cfg = new SBean.ArmorTypeCFGS();
					cfg.type = excelReadContext.getIntValue(row, col);
					if (cfg.type != cfgs.size() + 1)
						throw new Exception("内甲分类 " + row + " 的类型ID " + cfg.type + " 不连续！");
					col += 2;
					cfg.restrainType = excelReadContext.getIntValue(row, col++);
					if (cfg.restrainType <= 0 || cfg.restrainType > 3)
						throw new Exception("内甲分类 " + row + " 的克制类型 " + cfg.restrainType + " 无效！");
					cfg.beRestrainType = excelReadContext.getIntValue(row, col++);
					if (cfg.beRestrainType <= 0 || cfg.beRestrainType > 3)
						throw new Exception("内甲分类 " + row + " 的被克制类型 " + cfg.beRestrainType + " 无效！");
					cfg.unLockLevel = excelReadContext.getIntValue(row, col++);
					if (cfg.unLockLevel <= 0)
						throw new Exception("内甲分类 " + row + " 的解锁等级 " + cfg.unLockLevel + " 无效！");
					cfg.unLockPower = excelReadContext.getIntValue(row, col++);
					if (cfg.unLockPower < 0)
						throw new Exception("内甲分类 " + row + " 的解锁战力 " + cfg.unLockPower + " 无效！");
					cfg.unLockFeat = excelReadContext.getIntValue(row, col++);
					if (cfg.unLockFeat < 0)
						throw new Exception("内甲分类 " + row + " 的解锁武勋 " + cfg.unLockFeat + " 无效！");
					cfg.unLockItems = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						SBean.DummyGoods item = new SBean.DummyGoods(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
						if (item.id != 0)
						{
							checkEntityIDValid(gdCfgs, row, item.id, false);
							if (item.count <= 0)
								throw new Exception("内甲分类 " + row + " 的解锁道具数量 " + item.count + " 必须大于0！");
							cfg.unLockItems.add(item);
						}
						col += 2;
					}
					cfgs.add(cfg);
					row++;
				}
				armorCFGS.armorType = cfgs;
			}

			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				Map<Integer, SBean.ArmorLevelGroupCFGS> levelgroup = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ArmorLevelCFGS cfg = new SBean.ArmorLevelCFGS();
					cfg.type = excelReadContext.getIntValue(row, col++);
					cfg.level = excelReadContext.getIntValue(row, col++);
					if (!levelgroup.containsKey(cfg.type))
						levelgroup.put(cfg.type, new SBean.ArmorLevelGroupCFGS(cfg.type, new ArrayList<>()));
					int oldsize = levelgroup.get(cfg.type).armorLevels.size();
					if (cfg.level != oldsize + 1)
						throw new Exception("内甲等级 " + row + " 的等级 " + cfg.level + " 不连续！");
					cfg.needExp = excelReadContext.getIntValue(row, col++);
					if (cfg.needExp < 0)
						throw new Exception("内甲等级 " + row + " 的升级经验 " + cfg.needExp + " 不能小于0！");
					if (oldsize > 0)
						cfg.needExp = levelgroup.get(cfg.type).armorLevels.get(oldsize - 1).needExp + cfg.needExp;
					cfg.canUseItem = excelReadContext.getIntegerList(row, col++, ";");
					if (cfg.canUseItem.isEmpty())
						throw new Exception("内甲等级 " + row + " 的升级使用道具不能为空！");
					cfg.talentPoint = excelReadContext.getIntValue(row, col++);
					if (cfg.talentPoint <= 0)
						throw new Exception("内甲等级 " + row + " 的本级天赋点 " + cfg.talentPoint + " 必须大于0！");
					if (oldsize > 0)
						cfg.talentPoint = levelgroup.get(cfg.type).armorLevels.get(oldsize - 1).talentPoint + cfg.talentPoint;
					cfg.properties = new ArrayList<>();
					for (int i = 0; i < 10; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
						if (attr.id != 0)
						{
							checkPropertyIDValid(gdCfgs, row, attr.id);
							if (attr.value <= 0)
								throw new Exception("内甲等级 " + row + " 的属性值 " + attr.value + " 必须大于0！");
							cfg.properties.add(attr);
						}
						col += 2;
					}
					levelgroup.get(cfg.type).armorLevels.add(cfg);
					row++;
				}
				armorCFGS.armorLevelGroup = levelgroup;
			}

			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				Map<Integer, SBean.ArmorRankGroupCFGS> rankgroup = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ArmorRankCFGS cfg = new SBean.ArmorRankCFGS();
					cfg.type = excelReadContext.getIntValue(row, col++);
					cfg.rank = excelReadContext.getIntValue(row, col);
					if (!rankgroup.containsKey(cfg.type))
						rankgroup.put(cfg.type, new SBean.ArmorRankGroupCFGS(cfg.type, new ArrayList<>()));
					int oldsize = rankgroup.get(cfg.type).armorRanks.size();
					if (cfg.rank != oldsize + 1)
						throw new Exception("内甲升阶 " + row + " 的阶级 " + cfg.rank + " 不连续！");
					col += 3;
					cfg.propertyRate = excelReadContext.getIntValue(row, col++) / 10_000.0;
					if (cfg.propertyRate < 0)
						throw new Exception("内甲升阶 " + row + " 的属性提升比例 " + cfg.propertyRate + " 不能小于0！");
					cfg.properties = new ArrayList<SBean.AttrCFGS>();
					for (int i = 0; i < 4; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
						if (attr.id != 0)
						{
							checkPropertyIDValid(gdCfgs, row, attr.id);
							if (attr.value <= 0)
								throw new Exception("内甲升阶 " + row + " 的属性值 " + attr.value + " 必须大于0！");
							cfg.properties.add(attr);
						}
						col += 2;
					}
					cfg.rankUpConsume = new ArrayList<>();
					for (int i = 0; i < 2; i++)
					{
						SBean.DummyGoods item = new SBean.DummyGoods(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
						if (item.id != 0)
						{
							checkEntityIDValid(gdCfgs, row, item.id, false);
							if (item.count < 0)
								throw new Exception("内甲升阶 " + row + " 的消耗道具数量 " + item.count + " 不能小于0！");
							cfg.rankUpConsume.add(item);
						}
						col += 2;
					}
					cfg.maxWishPoint = excelReadContext.getIntValue(row, col++);
					if (cfg.maxWishPoint <= 0)
						throw new Exception("内甲升阶 " + row + " 的最大祝福点 " + cfg.maxWishPoint + " 必须大于0！");
					cfg.addWishPointEveryTime = excelReadContext.getIntValue(row, col++);
					if (cfg.addWishPointEveryTime <= 0)
						throw new Exception("内甲升阶 " + row + " 的每次增加祝福点 " + cfg.addWishPointEveryTime + " 必须大于0！");
					cfg.rankUpDefaultRate = excelReadContext.getIntValue(row, col++);
					cfg.addRatePerWishPoint = excelReadContext.getIntValue(row, col++);
					if (cfg.addRatePerWishPoint <= 0)
						throw new Exception("内甲升阶 " + row + " 的祝福点增加成功率 " + cfg.addRatePerWishPoint + " 必须大于0！");
					cfg.mustFailTime = excelReadContext.getIntValue(row, col++);
					cfg.mustSuccessTime = excelReadContext.getIntValue(row, col++);
					if (cfg.mustFailTime < 0 || cfg.mustSuccessTime <= 0 || cfg.mustFailTime >= cfg.mustSuccessTime)
						throw new Exception("内甲升阶 " + row + " 的必然失败或必然成功数值错误！");
					if (cfg.maxWishPoint * 0.85 <= cfg.addWishPointEveryTime * cfg.mustSuccessTime)
						throw new Exception("内甲升阶 " + row + " 祝福值可能超出上限！");
					rankgroup.get(cfg.type).armorRanks.add(cfg);
					row++;
				}
				armorCFGS.armorRankGroup = rankgroup;
			}

			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				List<SBean.FeatAddWishPointCFGS> featAddWishPointCFGSs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.FeatAddWishPointCFGS featAddWishPoint = new SBean.FeatAddWishPointCFGS(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
					if (featAddWishPointCFGSs.size() > 0 && featAddWishPoint.feat <= featAddWishPointCFGSs.get(featAddWishPointCFGSs.size() - 1).feat)
						throw new Exception("武勋附加祝福值 " + row + " 的武勋区间非递增！");
					if (featAddWishPoint.feat < 0)
						throw new Exception("武勋附加祝福值 " + row + " 的武勋区间 " + featAddWishPoint.feat + " 不能小于0！");
					if (featAddWishPoint.startWishPoint < 0)
						throw new Exception("武勋附加祝福值 " + row + " 的起始祝福点 " + featAddWishPoint.startWishPoint + " 不能小于0！");
					featAddWishPointCFGSs.add(featAddWishPoint);
					row++;
				}
				armorCFGS.featAddWishPoint = featAddWishPointCFGSs;
			}

			excelReadContext.ReadSheet(4);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				Map<Integer, SBean.ArmorTalentGroupCFGS> talentgroup = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;

					SBean.ArmorTalentCFGS cfg = new SBean.ArmorTalentCFGS();
					cfg.type = excelReadContext.getIntValue(row, col++);
					cfg.talentId = excelReadContext.getIntValue(row, col);
					if (!talentgroup.containsKey(cfg.type))
						talentgroup.put(cfg.type, new SBean.ArmorTalentGroupCFGS(cfg.type, new ArrayList<>()));
					int oldsize = talentgroup.get(cfg.type).armorTalents.size();
					if (cfg.talentId != oldsize + 1)
						throw new Exception("内甲天赋 " + row + " 的天赋ID " + cfg.talentId + " 不连续！");
					col += 4;
					cfg.maxTalentPoint = excelReadContext.getIntValue(row, col++);
					if (cfg.maxTalentPoint <= 0)
						throw new Exception("内甲天赋 " + row + " 的天赋最大点数 " + cfg.maxTalentPoint + " 必须大于0！");
					cfg.needTalentPoint = excelReadContext.getIntValue(row, col++);
					if (cfg.needTalentPoint < 0)
						throw new Exception("内甲天赋 " + row + " 的所需投入点数 " + cfg.needTalentPoint + " 不能小于0！");
					cfg.preTalentId = excelReadContext.getIntValue(row, col++);
					if (cfg.preTalentId < 0 || cfg.preTalentId > cfg.talentId)
						throw new Exception("内甲天赋 " + row + " 的前置天赋ID " + cfg.preTalentId + " 不能小于0或者大于当前ID！");
					cfg.preTalentPoint = excelReadContext.getIntValue(row, col++);
					if (cfg.preTalentPoint < 0)
						throw new Exception("内甲天赋 " + row + " 的所需前置点数 " + cfg.preTalentPoint + " 不能小于0！");
					cfg.needFeat = excelReadContext.getIntValue(row, col++);
					if (cfg.needFeat < 0)
						throw new Exception("内甲天赋 " + row + " 的所需武勋 " + cfg.needFeat + " 不能小于0！");
					cfg.effectType = excelReadContext.getByteValue(row, col++);
					cfg.attrId = excelReadContext.getIntValue(row, col++);
					cfg.addAttrType = excelReadContext.getByteValue(row, col++);
					cfg.addAttrNum = excelReadContext.getIntegerList(row, col++, ";");
					if (cfg.addAttrNum.stream().anyMatch((num) -> num < 0))
						throw new Exception("内甲天赋 " + row + " 的提升属性数值不能小于0！");
					cfg.trigSkills = excelReadContext.getIntegerList(row, col++, ";");
					
					switch (cfg.effectType) {
					case GameData.ARMOR_TALENT_TYPE_PROP:
						checkPropertyIDValid(gdCfgs, row, cfg.attrId);
						checkValueType(row, cfg.addAttrType);
						if(cfg.maxTalentPoint != cfg.addAttrNum.size())
							throw new Exception("row " + row + " 天赋最大点数 " + cfg.maxTalentPoint + " 和提升属性数值列表 " + cfg.addAttrNum.size() + " 不一致");
						break;
					case GameData.ARMOR_TALENT_TYPE_SKILL:
						if(cfg.maxTalentPoint != cfg.trigSkills.size())
							throw new Exception("row " + row + " 天赋最大点数 " + cfg.maxTalentPoint + " 和附加触发技能列表 " + cfg.trigSkills.size() + " 不一致");
						
						for(int aid: cfg.trigSkills)
							checkAiTrigerIDValid(gdCfgs, row, aid);
						break;
					default:
						throw new Exception("row " + row + " 内甲天赋效果类型 " + cfg.effectType + " 非法！");
					}
					
					cfg.powers = excelReadContext.getIntegerList(row, col++, ";");
					if (cfg.powers.stream().anyMatch((num) -> num < 0))
						throw new Exception("内甲天赋 " + row + " 的提升附加战力数值不能小于0！");
					talentgroup.get(cfg.type).armorTalents.add(cfg);
					row++;
				}
				armorCFGS.armorTalentGroup = talentgroup;
			}

			excelReadContext.ReadSheet(5);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				Map<Integer, SBean.ArmorSlotGroupCFGS> slotgroup = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;

					SBean.ArmorSlotCFGS cfg = new SBean.ArmorSlotCFGS();
					cfg.type = excelReadContext.getIntValue(row, col++);
					cfg.slotGroupId = excelReadContext.getIntValue(row, col++);
					if (!slotgroup.containsKey(cfg.type))
						slotgroup.put(cfg.type, new SBean.ArmorSlotGroupCFGS(cfg.type, new ArrayList<>()));
					int oldsize = slotgroup.get(cfg.type).armorSlots.size();
					if (cfg.slotGroupId != oldsize + 1)
						throw new Exception("插槽相关 " + row + " 的第几套ID " + cfg.slotGroupId + " 不连续！");
					cfg.slotNum = excelReadContext.getIntValue(row, col++);
					if (cfg.slotNum <= 0)
						throw new Exception("插槽相关 " + row + " 的插槽数量 " + cfg.slotNum + " 必须大于0！");
					cfg.defaultOpen = excelReadContext.getByteValue(row, col++);
					cfg.unlockLevel = excelReadContext.getIntValue(row, col++);
					if (cfg.unlockLevel < 0)
						throw new Exception("插槽相关 " + row + " 的解锁等级 " + cfg.unlockLevel + " 无效！");
					cfg.unLockPower = excelReadContext.getIntValue(row, col++);
					if (cfg.unLockPower < 0)
						throw new Exception("插槽相关 " + row + " 的解锁战力 " + cfg.unLockPower + " 无效！");
					cfg.unLockFeat = excelReadContext.getIntValue(row, col++);
					if (cfg.unLockFeat < 0)
						throw new Exception("插槽相关 " + row + " 的解锁武勋 " + cfg.unLockFeat + " 无效！");
					cfg.unLockItems = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						SBean.DummyGoods item = new SBean.DummyGoods(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
						if (item.id != 0)
						{
							checkEntityIDValid(gdCfgs, row, item.id, false);
							if (item.count <= 0)
								throw new Exception("内甲分类 " + row + " 的解锁道具数量 " + item.count + " 必须大于0！");
							cfg.unLockItems.add(item);
						}
						col += 2;
					}
					slotgroup.get(cfg.type).armorSlots.add(cfg);
					row++;
				}
				armorCFGS.armorSlotGroup = slotgroup;
			}

			excelReadContext.ReadSheet(6);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				Map<Integer, SBean.RuneCFGS> runes = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;

					SBean.RuneCFGS cfg = new SBean.RuneCFGS();
					cfg.runeId = excelReadContext.getIntValue(row, col);
					if (!checkItemIDValid(gdCfgs, cfg.runeId))
						throw new Exception("符文 " + row + " 的符文ID " + cfg.runeId + " 在道具表中不存在！");
					col += 3;
					cfg.property = new SBean.AttrCFGS(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
					if (cfg.property.value < 0)
						throw new Exception("符文 " + row + " 的附加属性值 " + cfg.property.value + " 必须大于0！");
					checkPropertyIDValid(gdCfgs, row, cfg.property.id);
					
					col += 2;
					cfg.transmigratePoint = excelReadContext.getIntValue(row, col++);
					if (cfg.transmigratePoint < 0)
						throw new Exception("符文 " + row + " 的许愿权重 " + cfg.transmigratePoint + " 必须大于0！");
					cfg.mutexRunes = excelReadContext.getIntegerList(row, col++, ";");
					cfg.power = excelReadContext.getIntValue(row, col++);
					if (cfg.power < 0)
						throw new Exception("符文 " + row + " 的附加战力 " + cfg.power + " 必须大于0！");

					runes.put(cfg.runeId, cfg);
					row++;
				}
				armorCFGS.rune = runes;
			}

			excelReadContext.ReadSheet(7);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				List<SBean.RuneTransmigrateCFGS> runeTransmigrates = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;

					SBean.RuneTransmigrateCFGS cfg = new SBean.RuneTransmigrateCFGS();
					cfg.transmigrate = excelReadContext.getIntValue(row, col++);
					if (runeTransmigrates.size() > 0 && cfg.transmigrate < runeTransmigrates.get(runeTransmigrates.size() - 1).transmigrate)
						throw new Exception("符文许愿 " + row + " 的许愿权重区间必须递增！");
					if (cfg.transmigrate < 0)
						throw new Exception("符文许愿 " + row + " 的许愿权重区间 " + cfg.transmigrate + " 不能小于0！");
					cfg.dropId = excelReadContext.getIntValue(row, col++);
					if (!gdCfgs.randomDropTbl.containsKey(cfg.dropId))
						throw new Exception("符文许愿 " + row + " 的随机掉落表ID " + cfg.dropId + " 不存在！");
					cfg.minDropNum = excelReadContext.getIntValue(row, col++);
					cfg.maxDropNum = excelReadContext.getIntValue(row, col++);
					if (cfg.minDropNum <= 0 || cfg.maxDropNum <= 0 || cfg.maxDropNum < cfg.minDropNum)
						throw new Exception("符文许愿 " + row + " 的掉落数量出错！");
					runeTransmigrates.add(cfg);
					row++;
				}
				armorCFGS.runeTransmigrate = runeTransmigrates;
			}

			excelReadContext.ReadSheet(8);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				List<SBean.RuneLangCFGS> runeLangs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;

					SBean.RuneLangCFGS cfg = new SBean.RuneLangCFGS();
					cfg.id = excelReadContext.getIntValue(row, col);
					if (cfg.id != runeLangs.size() + 1)
						throw new Exception("符文之语 " + row + " 的ID " + cfg.id + " 不连续！");
					col += 2;
					
					Set<Integer> runes = new HashSet<>();
					cfg.runes = new ArrayList<>();
					for (int i = 0; i < 6; i++)
					{
						int runeId = excelReadContext.getIntValue(row, col++);
						if (!armorCFGS.rune.containsKey(runeId))
							throw new Exception("符文之语 " + row + " 的符文ID " + runeId + " 不存在！");
						
						if (!runes.add(runeId))
							throw new Exception("符文之语 " + row + " 的符文ID " + runeId + " 重复！");
						
						cfg.runes.add(runeId);
					}
					
					cfg.properties = new ArrayList<>();
					for (int i = 0; i < 10; i++)
					{
						SBean.AttrCFGS attr = new SBean.AttrCFGS(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
						if (attr.id != 0)
						{
							if (attr.value <= 0)
								throw new Exception("符文之语 " + row + " 的属性值 " + attr.value + " 必须大于0！");
							cfg.properties.add(attr);
						}
						col += 2;
					}
					runeLangs.add(cfg);
					row++;
				}
				armorCFGS.runeLang = runeLangs;
			}

			excelReadContext.ReadSheet(9);
			{
				final int rowStart = 0;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;

				SBean.ArmorCommonCFGS armorCommonCFGS = new SBean.ArmorCommonCFGS();
				armorCommonCFGS.wishPointMaxRate = excelReadContext.getIntValue(row++, col);
				if (armorCommonCFGS.wishPointMaxRate <= 0)
					throw new Exception("独立字段 " + row + " 的武勋最大附加比例 " + armorCommonCFGS.wishPointMaxRate + " 必须大于0！");
				armorCommonCFGS.rollNoticeRank = excelReadContext.getIntValue(row++, col);
				if (armorCommonCFGS.rollNoticeRank <= 0)
					throw new Exception("独立字段 " + row + " 的全服通告阶位 " + armorCommonCFGS.rollNoticeRank + " 必须大于0！");
				armorCommonCFGS.refreshItemId = excelReadContext.getIntValue(row++, col);
				checkEntityIDValid(gdCfgs, row, armorCommonCFGS.refreshItemId, false);
				armorCommonCFGS.refreshItemNum = excelReadContext.getIntegerList(row++, col, ";");
				if (armorCommonCFGS.refreshItemNum.stream().anyMatch(num -> num <= 0))
					throw new Exception("独立字段 " + row + " 的重置天赋消耗道具数量必须大于0！");
				armorCommonCFGS.minWishRuneNum = excelReadContext.getIntValue(row++, col);
				armorCommonCFGS.maxWishRuneNum = excelReadContext.getIntValue(row, col);
				if (armorCommonCFGS.minWishRuneNum <= 0 || armorCommonCFGS.maxWishRuneNum <= 0 || armorCommonCFGS.maxWishRuneNum < armorCommonCFGS.minWishRuneNum)
					throw new Exception("独立字段 " + row + " 的符文许愿投入数量错误！");
				row += 5;
				armorCommonCFGS.wishUseItem = excelReadContext.getIntValue(row++, col);
				checkEntityIDValid(gdCfgs, row, armorCommonCFGS.wishUseItem, false);
				armorCommonCFGS.wishUseItemNum = excelReadContext.getIntValue(row++, col);
				if (armorCommonCFGS.wishUseItemNum <= 0)
					throw new Exception("独立字段 " + row + " 的符文祝福消耗道具数量 " + armorCommonCFGS.wishUseItemNum + " 必须大于0！");
				armorCFGS.armorCommon = armorCommonCFGS;
			}
			gdCfgs.armor = armorCFGS;
			System.out.println("load table " + fileName + " success.");
		}
	}
	
	public void loadSceneTrig(String fileName, SBean.GameDataCFGS gdCfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.SceneSpawnPointCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.SceneSpawnPointCFGS cfg = new SBean.SceneSpawnPointCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.mapID = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mcc = gdCfgs.mapClusters.get(cfg.mapID);
					if(mcc == null)
						throw new Exception("row " + row + " 特殊刷怪点 " + cfg.id + " 所在地图 " + cfg.mapID + " 不存在！");
					
					cfg.position = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcc, cfg.position, "特殊刷怪点");
					cfg.posRand = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, cfg.posRand);
					cfg.randRadius = excelReadContext.getIntValue(row, col++);
					cfg.odds = excelReadContext.getIntValue(row, col++);
					cfg.monsterID = excelReadContext.getIntValue(row, col++);
					checkMonsterID(gdCfgs, row, cfg.monsterID);
					cfg.count = excelReadContext.getIntValue(row, col++);
					cfg.standTime = excelReadContext.getIntValue(row, col++);
					cfg.seeType = excelReadContext.getIntValue(row, col++);
					
					if(cfgs.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 特殊刷怪点 " + cfg.id + " 重复！");
					
					row++;
				}
				gdCfgs.sceneSpawnPoint = cfgs;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int id = excelReadContext.getIntValue(row, colStart);
					if(!this.playerRepalceActions.add(id))
						throw new Exception("row " + row + " 角色替换动作表 ID " + id + " 重复");
					row++;
				}
			}
			
			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int id = excelReadContext.getIntValue(row, col++);
					if(!this.npcRepalceActions.add(id))
						throw new Exception("row " + row + " NPC动作替换表 ID " + id + " 重复");
					
					int npcID = excelReadContext.getIntValue(row, col++);
					checkNPCID(gdCfgs, row, npcID);
					row++;
				}
			}
			
			excelReadContext.ReadSheet(4);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int id = excelReadContext.getIntValue(row, col++);
					if(!this.effectPlayIDs.add(id))
						throw new Exception("row " + row + " 特效播放表 ID " + id + " 重复");
					
					int effectID = excelReadContext.getIntValue(row, col++);
					if(!this.effectIDs.contains(effectID))
						throw new Exception("row " + row + " 特效ID " + effectID + " 非法");
					row++;
				}
			}
			
			excelReadContext.ReadSheet(5);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int id = excelReadContext.getIntValue(row, col++);
					if(!this.openUIIDs.add(id))
						throw new Exception("row " + row + " 弹出UI界面表ID " + id + " 重复");
					
					row++;
				}
			}
			
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.SceneTrigCFGS> clientCfgs = new HashMap<>();
				Map<Integer, SBean.SceneTrigCFGS> serverCfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.SceneTrigCFGS cfg = new SBean.SceneTrigCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.mapID = excelReadContext.getIntValue(row, col++);
					cfg.trigEvent = excelReadContext.getIntValue(row, col++);
					switch (cfg.trigEvent)
					{
					case GameData.SCENE_TRIG_EVENT_TAKE_TASK: 		
					case GameData.SCENE_TRIG_EVENT_TASK_REWARD:
					case GameData.SCENE_TRIG_EVENT_TASK_USE_ITEM:	
					case GameData.SCENE_TRIG_EVENT_TASK_MINERAL:
					case GameData.SCENE_TRIG_EVENT_TALK_NPC:
					case GameData.SCENE_TRIG_EVENT_TASK_FINISH:
					case GameData.SCENE_TRIG_EVENT_PUT_NPC:
						break;
					default:
						throw new Exception("row " + row + " 场景触发条件 " + cfg.trigEvent + " 非法！");
					}
					
					cfg.trigBehavior = excelReadContext.getIntValue(row, col++);
					
					int param1 = excelReadContext.getIntValue(row, col++);
					int param2 = excelReadContext.getIntValue(row, col++);
					int param3 = excelReadContext.getIntValue(row, col++);
					int param4 = excelReadContext.getIntValue(row, col++);
					switch (cfg.trigBehavior)
					{
					case GameData.SCENE_TRIG_BEHAVIOR_NPC_ACTION:
						if(!this.npcRepalceActions.contains(param1))
							throw new Exception("row " + row + " NPC动作替换表ID " + param1 + " 非法");
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_ROLE_ACTION:
						if(!this.playerRepalceActions.contains(param1))
							throw new Exception("row " + row + " 角色替换动作表ID " + param1 + " 非法");
						
						checkDialogIDValid(gdCfgs, row, param2);
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_EFFECT:
						if(!this.effectPlayIDs.contains(param1))
							throw new Exception("row " + row + " 特效播放表ID " + param1 + " 非法");
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_ANIMATION:
						checkAnimationIDValid(gdCfgs, row, param2);
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_SCALE:
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_SPAWN_MONSTER:
						checkSenceSpawnPoint(gdCfgs, row, param1);
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_TASK_ALTER:
						checkTaskAlterIDValid(gdCfgs, row, param1);
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_QUIT_ALTER:
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_NPC_PUTACTION:
						if(!this.npcRepalceActions.contains(param1))
							throw new Exception("row " + row + " NPC动作替换表ID " + param1 + " 非法");
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_NPC_RECOVER:
						checkNPCID(gdCfgs, row, param1);
						break;
					case GameData.SCENE_TRIG_BEHAVIOR_OPEN_UI:
						if(!this.openUIIDs.contains(param1))
							throw new Exception("row " + row + " 弹出UI界面表ID " + param1 + " 非法");
						break;
					default:
						throw new Exception("row " + row + " 场景触发行为 " + cfg.trigBehavior + " 非法！");
					}
					cfg.param1 = param1;
					
					if(cfg.id > 0)
					{
						if(clientCfgs.put(cfg.id, cfg) != null)
							throw new Exception("row " + row + " 场景触发ID " + cfg.id + " 不连续！");
					}
					else if(cfg.id < 0)
					{
						if(serverCfgs.put(cfg.id, cfg) != null)
							throw new Exception("row " + row + " 场景触发ID " + cfg.id + " 重复！");
					}
					
					this.senceTrigIDs.add(cfg.id);
					row++;
				}
				
				gdCfgs.sceneTrigs = serverCfgs;
			}
			
			System.out.println("load table " + fileName + " success.");
		}
	}

	private void loadMarriageTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.MarriageCFGS marriageCFGS = new SBean.MarriageCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 2;
				int row = rowStart;
				int col = colStart;
				SBean.MarriageBaseCFGS base = new SBean.MarriageBaseCFGS();
				base.levelNeed = excelReadContext.getIntValue(row++, col);
				if (base.levelNeed < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚需求等级 " + base.levelNeed + " 不能小于0！");
				base.friendPointNeed = excelReadContext.getIntValue(row++, col);
				if (base.friendPointNeed < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚需求友好度 " + base.friendPointNeed + " 不能小于0！");
				base.pkPointLess = excelReadContext.getIntValue(row++, col);
				if (base.pkPointLess < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚善恶度限制 " + base.pkPointLess + " 不能小于0！");
				base.giftMaxGetTimes = excelReadContext.getIntValue(row++, col);
				if (base.giftMaxGetTimes < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚礼品最多获取次数 " + base.giftMaxGetTimes + " 不能小于0！");
				base.banquetMaxGetTimes = excelReadContext.getIntValue(row++, col);
				if (base.banquetMaxGetTimes < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚宴席最多获取次数 " + base.banquetMaxGetTimes + " 不能小于0！");
				base.banquetLast = excelReadContext.getIntValue(row++, col);
				if (base.banquetLast < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚宴席持续时间 " + base.banquetLast + " 不能小于0！");
				base.paradeLast = excelReadContext.getIntValue(row, col);
				if (base.paradeLast < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚游街持续时间 " + base.paradeLast + " 不能小于0！");
				row += 3;
				base.divorceCostId = excelReadContext.getIntValue(row++, col);
				checkEntityIDValid(gdcfgs, row - 1, base.divorceCostId, true);
				base.divorceCost = excelReadContext.getIntValue(row++, col);
				if (base.divorceCost < 0)
					throw new Exception("结婚配置表 row " + row + " 离婚消耗数量 " + base.divorceCost + " 不能小于0！");
				base.divorceCD = excelReadContext.getIntValue(row++, col);
				if (base.divorceCD < 0)
					throw new Exception("结婚配置表 row " + row + " 离婚惩罚时间 " + base.divorceCD + " 不能小于0！");
				base.transmitCD = excelReadContext.getIntValue(row++, col);
				if (base.transmitCD < 0)
					throw new Exception("结婚配置表 row " + row + " 结婚传送CD " + base.transmitCD + " 不能小于0！");
				base.marriageNpcId = excelReadContext.getIntValue(row++, col);
				if (!gdcfgs.npcs.containsKey(base.marriageNpcId))
					throw new Exception("结婚配置表 row " + row + " 月老ID " + base.marriageNpcId + " 不存在！");
				base.skillUseCoin = excelReadContext.getIntValue(row++, col);
				if (base.skillUseCoin <= 0)
					throw new Exception("结婚配置表 row " + row + " 技能升级消耗铜钱 " + base.skillUseCoin + " 必须大于0！");
				row += 1;
				base.skillHillDistance = excelReadContext.getIntValue(row++, col);
				if (base.skillHillDistance <= 0)
					throw new Exception("结婚配置表 row " + row + " 技能治疗距离 " + base.skillHillDistance + " 不能小于0！");

				base.carMoveSpeed = excelReadContext.getIntValue(row++, col);
				base.banquetType = excelReadContext.getIntValue(row++, col);
				base.carId = excelReadContext.getIntValue(row++, col);
				base.mapId = excelReadContext.getIntValue(row++, col);
				checkMapID(gdcfgs, row, base.mapId);
				
				marriageCFGS.marriageCommon = base;
				
				final int rowTask = excelReadContext.locateColumnTag(0, "结婚姻缘任务");
				row = rowTask + 1;
				col = colStart;
				SBean.MarriageTaskCFG taskCfg = new SBean.MarriageTaskCFG();
				taskCfg.openNPC = excelReadContext.getIntValue(row++, col);
				taskCfg.dayLoopTasks = excelReadContext.getIntValue(row++, col);
				taskCfg.distance = excelReadContext.getIntValue(row++, col);
				marriageCFGS.task = taskCfg;
			}
			
			//结婚宴席相关
			excelReadContext.ReadSheet(2);
			{
				marriageCFGS.banquetBase = new SBean.MarriageBanquetBaseCFGS(0, new HashMap<>(), new HashMap<>());
				int row = 0;
				int col = 0;
				final int rowMap = excelReadContext.locateColumnTag(0, "结婚宴席地图");
				row = rowMap;
				col = 1;
				marriageCFGS.banquetBase.mapID = excelReadContext.getIntValue(row, col);
				SBean.MapClusterCFGS mccCfg = gdcfgs.mapClusters.get(marriageCFGS.banquetBase.mapID);
				if(mccCfg == null)
					throw new Exception("row " + row + " 结婚宴席地图 " + marriageCFGS.banquetBase.mapID + " 非法!");
				
				final int rowMineral = excelReadContext.locateColumnTag(0, "结婚宴席");
				row = rowMineral + 2;
				col = 1;
				while(excelReadContext.isNotEmpty(row, 1))
				{
					int id = excelReadContext.getIntValue(row, col);
					int mineralID = excelReadContext.getIntValue(row, col + 1);
//					checkMineralID(gdcfgs, row, mineralID);
					SBean.MineralCFGS mineCfg = gdcfgs.minerals.get(mineralID);
					if(mineCfg == null || mineCfg.mineralType != GameData.MINERAL_TYPE_BANQUET)
						throw new Exception("row "+ row + " 结婚宴席ID " + id + " 关联矿物 " + mineralID + " 非法!");
					
					SBean.Vector3 pos = excelReadContext.getVector3(row, col + 2, ";");
					if(marriageCFGS.banquetBase.minerals.put(id, new SBean.PosEntity(mineralID, pos)) != null)
						throw new Exception("row " + row + " 结婚宴席ID " + id + " 重复");
					
					checkMapPosValid(row, mccCfg, pos, "结婚宴席 矿物坐标");
					row++;
				}
				
				final int rowMonster = excelReadContext.locateColumnTag(0, "结婚怪物");
				row = rowMonster + 2;
				col = 1;
				while(excelReadContext.isNotEmpty(row, 1))
				{
					int id = excelReadContext.getIntValue(row, col);
					int monsterID = excelReadContext.getIntValue(row, col + 1);
					checkMonsterID(gdcfgs, row, monsterID);
					SBean.Vector3 pos = excelReadContext.getVector3(row, col + 2, ";");
					if(marriageCFGS.banquetBase.monsters.put(id, new SBean.PosEntity(monsterID, pos)) != null)
						throw new Exception("row " + row + " 结婚怪物ID " + id + " 重复");
					
					checkMapPosValid(row, mccCfg, pos, "结婚宴席 怪物坐标");
					row++;
				}
			}
			
			//结婚宴席
			excelReadContext.ReadSheet(3);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.MarriageBanquetCFGS> banques = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MarriageBanquetCFGS cfg = new SBean.MarriageBanquetCFGS();
					cfg.id = excelReadContext.getIntValue(row, col++);
					List<Integer> mrgMinerals = excelReadContext.getIntegerList(row, col++, ";"); 
					cfg.minerals = new HashSet<>();
					for(int m: mrgMinerals)
					{
						if(!cfg.minerals.add(m))
							throw new Exception("row " + row + " 结婚宴席ID " + m + " 重复!");
					}
					
					List<Integer> mrgMonsters = excelReadContext.getIntegerList(row, col++, ";");
					cfg.monsters = new HashSet<>();
					for(int m: mrgMonsters)
					{
						if(!cfg.monsters.add(m))
							throw new Exception("row " + row + " 结婚怪物ID " + m + " 重复!");
					}
					
					cfg.refreshCnt = excelReadContext.getIntValue(row, col++);
					cfg.refreshInterval = excelReadContext.getIntValue(row, col++);
					
					if(cfg.refreshCnt > 0 && cfg.refreshInterval == 0)
						throw new Exception("row " + row + " 怪物刷新波数为 " + cfg.refreshCnt + " 时怪物刷新间隔 为 " + cfg.refreshInterval + " 非法!");
					
					if(banques.put(cfg.id, cfg) != null)
						throw new Exception("row " + row + " 结婚宴席类型ID " + cfg.id + " 重复!");
					
					row++;
				}
				
				marriageCFGS.banquets = banques;
			}
			
			//游街路径
			excelReadContext.ReadSheet(8);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.MarriageLineCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MarriageLineCFGS cfg = new SBean.MarriageLineCFGS(0, 0, new ArrayList<>());
					cfg.id = excelReadContext.getIntValue(row, col++);
					cfg.mapID = excelReadContext.getIntValue(row, col++);
					
					SBean.MapClusterCFGS mc = gdcfgs.mapClusters.get(cfg.mapID);
					if(mc == null || mc.type != GameData.MAP_TYPE_MAP_WORLD)
						throw new Exception("row " + row + " 游街路径 " + cfg.id + " mapID " + cfg.mapID + " 非法!");
					
					while(excelReadContext.isNotEmpty(row, col))
					{
						SBean.Vector3 pos = excelReadContext.getVector3(row, col++, ";");
						checkMapPosValid(row, mc, pos, "游街路径坐标");
						cfg.points.add(pos);
					}
					
					cfgs.put(cfg.id, cfg);
					row++;
				}
				
				marriageCFGS.lines = cfgs;
			}
			
			//预约时间
			excelReadContext.ReadSheet(9);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;

				List<SBean.MarriageBespeakTimeCFGS> cfgs = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MarriageBespeakTimeCFGS cfg = new SBean.MarriageBespeakTimeCFGS();

					List<Integer> timePoint = new ArrayList<>();
					List<String> lst = excelReadContext.getStringList(row, col++, ";");
					for (String str : lst)
					{
						int daySecond = GameTime.parseSecondOfDay(str);
						if (daySecond < 0)
							throw new Exception("结婚配置表 row " + row + " 预约时间点 " + str + " 非法！");
						timePoint.add(daySecond);
					}
					int startTime = timePoint.get(0);
					int endTime = timePoint.get(1);
					cfg.time = new SBean.TimeSpan(startTime, endTime);
					if (cfgs.size() > 0 && startTime < cfgs.get(cfgs.size() - 1).time.endTime)
						throw new Exception("结婚配置表 row " + row + " 预约时间点开始时间小于上一段的结束时间！");
					cfg.costId = excelReadContext.getIntValue(row, col++);
					cfg.costNum = excelReadContext.getIntValue(row, col++);
					cfgs.add(cfg);
					row++;
				}
				
				marriageCFGS.marriageBespeakTimes = cfgs;
			}
			
			//结婚车队
			excelReadContext.ReadSheet(7);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				Map<Integer, SBean.MarriageCarCFGS> cfgs = new HashMap<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					int id = excelReadContext.getIntValue(row, col++);
					int carID = excelReadContext.getIntValue(row, col++);
					checkNPCID(gdcfgs, row, carID);
					
					List<Integer> npcs = new ArrayList<>();
					List<SBean.Vector3> offsetPoss = new ArrayList<>();
					for(int i = 0; i < 10; i++)
					{
						int npcID = excelReadContext.getIntValue(row, col++);
						if(npcID > 0)
						{
							checkNPCID(gdcfgs, row, npcID);
							npcs.add(npcID);
						}
					}
					
					List<Integer> offsets = excelReadContext.getIntegerList(row, col++, ";");
					if(!offsets.isEmpty())
					{
						if(offsets.size() % 2 != 0)
							throw new Exception("row " + row + " 结婚车队偏移 " + offsets + " 错误");
						
						for(int i=0; i<offsets.size()/2; i++)
							offsetPoss.add(new SBean.Vector3(offsets.get(i * 2), 0, offsets.get(i * 2 + 1)));
					}
					
					int lineID = excelReadContext.getIntValue(row, col++);
					SBean.MarriageLineCFGS lineCfg = marriageCFGS.lines.get(lineID);
					if(lineCfg == null)
						throw new Exception("row "+ row + " 结婚车队 " + id + " 游街路径 " + lineID + " 非法!");
					
					int mineralID = excelReadContext.getIntValue(row, col++);
					SBean.MineralCFGS mineCfg = gdcfgs.minerals.get(mineralID);
					if(mineCfg == null || mineCfg.mineralType != GameData.MINERAL_TYPE_WEDDINF_BOX)
						throw new Exception("row "+ row + " 结婚车队 " + id + " 结婚礼盒 " + mineralID + " 非法!");
					
					int mineralCnt = excelReadContext.getIntValue(row, col++);
					List<Integer> points = excelReadContext.getIntegerList(row, col++, ";");
					Set<Integer> refreshPoints = new HashSet<>();
					for(int p: points)
					{
						if(!refreshPoints.add(p) || p <= 0 || p > lineCfg.points.size())
							throw new Exception("row "+ row + " 结婚车队 " + id + " 礼盒刷新路径序号 " + p + " 非法!");
					}
					
					int radius = excelReadContext.getIntValue(row, col++);
					if(npcs.size() != offsetPoss.size())
						throw new Exception("row "+ row + " 结婚车队NPC数量 " + (npcs.size() + 1) + " 和偏移数量 " + offsetPoss.size() + " 不一致!");
					
					int standByTime = excelReadContext.getIntValue(row, col++);
					if(cfgs.put(id, new SBean.MarriageCarCFGS(id, lineID, mineralID, mineralCnt, refreshPoints, radius, standByTime)) != null)
						throw new Exception("row "+ row + " 结婚车队ID " + id + " 重复!");
					
					row++;
				}
				marriageCFGS.cars = cfgs;
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				marriageCFGS.marriageGrade = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MarriageGradeCFGS marriageGradeCFGS = new SBean.MarriageGradeCFGS();
					marriageGradeCFGS.id = excelReadContext.getIntValue(row, col++);
					if (marriageCFGS.marriageGrade.size() + 1 != marriageGradeCFGS.id)
						throw new Exception("结婚档次 row " + row + " 类型ID " + marriageGradeCFGS.id + " 不连续！");
					marriageGradeCFGS.coinCost = excelReadContext.getIntValue(row, col++);
					if (marriageGradeCFGS.coinCost < 0)
						throw new Exception("结婚档次 row " + row + " 结婚消耗铜钱 " + marriageGradeCFGS.coinCost + " 不能小于0！");
					marriageGradeCFGS.diamondCost = excelReadContext.getIntValue(row, col++);
					SBean.DummyGoods costItem = new SBean.DummyGoods(excelReadContext.getIntValue(row, col), excelReadContext.getIntValue(row, col + 1));
					checkEntityIDValid(gdcfgs, row, costItem.id, true);
					if (costItem.count < 0)
						throw new Exception("结婚档次 row " + row + " 结婚消耗道具 " + costItem.count + " 不能小于0！");
					marriageGradeCFGS.itemCost = costItem;
					col += 2;
					marriageGradeCFGS.chatDisplay = excelReadContext.getByteValue(row, col++);
					marriageGradeCFGS.rollNoticeDisplay = excelReadContext.getByteValue(row, col++);
					marriageGradeCFGS.publicWarehouseOpen = excelReadContext.getByteValue(row, col++);
					marriageGradeCFGS.publicWarehouseSize = excelReadContext.getIntValue(row, col++);
					if (marriageGradeCFGS.publicWarehouseSize < 0)
						throw new Exception("结婚档次 row " + row + " 公共仓库大小 " + marriageGradeCFGS.publicWarehouseSize + " 不能小于0！");
					marriageGradeCFGS.skillLevel = excelReadContext.getIntValue(row, col++);
					if (marriageGradeCFGS.skillLevel < 0)
						throw new Exception("结婚档次 row " + row + " 初始技能等级 " + marriageGradeCFGS.skillLevel + " 不能小于0！");
					
					col++;
					marriageGradeCFGS.dressId = excelReadContext.getIntValue(row, col++);
					
					marriageCFGS.marriageGrade.add(marriageGradeCFGS);
					row++;
				}
			}
			
			excelReadContext.ReadSheet(5);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				marriageCFGS.marriageAttributes = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MarriageAttributeCFGS marriageAttributeCFGS = new SBean.MarriageAttributeCFGS();
					marriageAttributeCFGS.level = excelReadContext.getIntValue(row, col++);
					if (marriageCFGS.marriageAttributes.size() + 1 != marriageAttributeCFGS.level)
						throw new Exception("姻缘属性 row " + row + " 等级 " + marriageAttributeCFGS.level + " 不连续！");
					marriageAttributeCFGS.marriagePoint = excelReadContext.getIntValue(row, col++);
					if (marriageAttributeCFGS.marriagePoint<0)
						throw new Exception("姻缘属性 row " + row + " 所需姻缘值 " + marriageAttributeCFGS.marriagePoint + " 不能小于0！");
					marriageAttributeCFGS.properties = new ArrayList<>();
					for (int i = 0; i < 6; i++)
					{
						int attrId = excelReadContext.getIntValue(row, col + i * 2);
						if (attrId == 0)
							break;
						if (!gdcfgs.properties.containsKey(attrId))
							throw new Exception("姻缘属性 row " + row + " 属性ID " + attrId + " 无效！");
						int attrCount = excelReadContext.getIntValue(row, col + i * 2 + 1);
						if (attrCount < 0)
							throw new Exception("姻缘属性 row " + row + " 属性值 " + attrCount + " 必须大于0！");
						marriageAttributeCFGS.properties.add(new SBean.AttrCFGS(attrId, attrCount));
					}
					col += 12;
					marriageCFGS.marriageAttributes.add(marriageAttributeCFGS);
					row++;
				}
			}
			
			excelReadContext.ReadSheet(6);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				marriageCFGS.marriageSkills = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.MarriageSkillCFGS marriageSkillCFGS = new SBean.MarriageSkillCFGS();
					marriageSkillCFGS.skillId = excelReadContext.getIntValue(row, col++);
					if (!marriageCFGS.marriageSkills.containsKey(marriageSkillCFGS.skillId))
						marriageCFGS.marriageSkills.put(marriageSkillCFGS.skillId, new SBean.MarriageSkillGroupCFGS(marriageSkillCFGS.skillId, new ArrayList<>()));
					marriageSkillCFGS.level = excelReadContext.getIntValue(row, col);
					if (marriageCFGS.marriageSkills.get(marriageSkillCFGS.skillId).skill.size() + 1 != marriageSkillCFGS.level)
						throw new Exception("姻缘技能 row " + row + " 等级 " + marriageSkillCFGS.level + " 不连续！");
					col += 3;
					marriageSkillCFGS.levelUpTimes =  excelReadContext.getIntValue(row, col++);
					if(marriageSkillCFGS.levelUpTimes<0)
						throw new Exception("姻缘技能 row " + row + " 升级所需次数 " + marriageSkillCFGS.levelUpTimes + " 不能小于0！");
					marriageSkillCFGS.breakItemCost = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						int itemId = excelReadContext.getIntValue(row, col + i * 2);
						if (itemId == 0)
						{
							break;
						}
						checkEntityIDValid(gdcfgs, row, itemId, false);
						int itemCount = excelReadContext.getIntValue(row, col + i * 2 + 1);
						if (itemCount < 0)
							throw new Exception("姻缘技能 row " + row + " 道具消耗数量 " + itemCount + " 必须大于0！");
						marriageSkillCFGS.breakItemCost.add(new SBean.DummyGoods(itemId, itemCount));
					}
					col += 6;
					marriageSkillCFGS.buffId = excelReadContext.getIntValue(row, col++);
					if(marriageSkillCFGS.buffId > 0)
						checkBuffIDValid(gdcfgs, row, marriageSkillCFGS.buffId);
					
					marriageSkillCFGS.marriageLevelNeed = excelReadContext.getIntValue(row, col++);
					marriageCFGS.marriageSkills.get(marriageSkillCFGS.skillId).skill.add(marriageSkillCFGS);
					row++;
				}
			}
			
			gdcfgs.marriage = marriageCFGS;
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	private void loadExchangeTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 2;
				final int colStart = 0;
				int row = rowStart;
				List<SBean.ExchangeCFGS> exchanges = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					int col = colStart;
					SBean.ExchangeCFGS exchange = new SBean.ExchangeCFGS();
					exchange.id = excelReadContext.getIntValue(row, col);
					if (exchange.id != exchanges.size() + 1)
						throw new Exception("物物兑换表 row " + row + " id " + exchange.id + " 不连续！");
					col += 2;
					exchange.dayTimes = excelReadContext.getIntValue(row, col++);
					if (exchange.dayTimes != -1 && exchange.dayTimes <= 0)
						throw new Exception("物物兑换表 row " + row + " 每日限制次数 " + exchange.dayTimes + " 必须为-1或大于0！");
					exchange.itemCost = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						int id = excelReadContext.getIntValue(row, col + i * 2);
						if (id == 0)
							break;
						checkEntityIDValid(gdcfgs, row, id, false);
						int count = excelReadContext.getIntValue(row, col + i * 2 + 1);
						if (count <= 0)
							throw new Exception("物物兑换表 row " + row + " 所需道具数量 " + count + " 必须大于0！");
						exchange.itemCost.add(new SBean.DummyGoods(id, count));
					}
					if (exchange.itemCost.isEmpty())
						throw new Exception("物物兑换表 row " + row + " 所需道具不能为空！");
					col += 6;
					exchange.itemGet = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						int id = excelReadContext.getIntValue(row, col + i * 2);
						if (id == 0)
							break;
						checkEntityIDValid(gdcfgs, row, id, false);
						int count = excelReadContext.getIntValue(row, col + i * 2 + 1);
						if (count <= 0)
							throw new Exception("物物兑换表 row " + row + " 所需道具数量 " + count + " 必须大于0！");
						exchange.itemGet.add(new SBean.DummyGoods(id, count));
					}
					if (exchange.itemCost.isEmpty())
						throw new Exception("物物兑换表 row " + row + " 获得道具不能为空！");
					col += 6;
					row++;
					exchanges.add(exchange);
				}
				gdcfgs.exchange = exchanges;
			}
		}
		System.out.println("load table " + fileName + " success.");
	}

	private void loadFlagBattleTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.FlagBattleCFGS flagCFGS = new SBean.FlagBattleCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 2;
				int row = rowStart;
				int col = colStart;
				flagCFGS.startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
				flagCFGS.endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
				flagCFGS.sectMinLevel = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.sectMinLevel <= 0)
					throw new Exception("帮派夺旗战 row " + row + " 帮派参与等级 " + flagCFGS.sectMinLevel + " 必须大于0！");
				flagCFGS.roleMinLevel = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.roleMinLevel <= 0)
					throw new Exception("帮派夺旗战 row " + row + " 角色参与等级 " + flagCFGS.roleMinLevel + " 必须大于0！");
				flagCFGS.sectMaxFlagNum = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.sectMaxFlagNum <= 0)
					throw new Exception("帮派夺旗战 row " + row + " 帮派最大占领旗帜 " + flagCFGS.sectMaxFlagNum + " 必须大于0！");
				flagCFGS.getFlagSecond = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.getFlagSecond <= 0)
					throw new Exception("帮派夺旗战 row " + row + " 夺取旗帜时间 " + flagCFGS.getFlagSecond + " 必须大于0！");
				flagCFGS.roleRewardLevel = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.roleRewardLevel <= 0)
					throw new Exception("帮派夺旗战 row " + row + " 玩家奖励等级 " + flagCFGS.roleRewardLevel + " 必须大于0！");
				flagCFGS.roleRewardTime = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.roleRewardTime <= 0)
					throw new Exception("帮派夺旗战 row " + row + " 角色奖励间隔 " + flagCFGS.roleRewardTime + " 必须大于0！");
				flagCFGS.sectVitRewardTime = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.sectVitRewardTime <= 0)
					throw new Exception("帮派夺旗战 row " + row + " 帮派活跃度奖励间隔 " + flagCFGS.sectVitRewardTime + " 必须大于0！");
				flagCFGS.roleJoinSectTimeLimit = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.roleJoinSectTimeLimit < 0)
					throw new Exception("帮派夺旗战 row " + row + " 角色参与所需进入帮派时间 " + flagCFGS.roleJoinSectTimeLimit + " 不能小于0！");
				row += 3;
				flagCFGS.activityLine = excelReadContext.getIntValue(row++, col);
				if (flagCFGS.activityLine < 0)
					throw new Exception("帮派夺旗战 row " + row + " 活动所在线 " + flagCFGS.activityLine + " 必须大于0！");
			}
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				flagCFGS.flags = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					int col = colStart;
					SBean.FlagBattleMapCFGS flagmapCFGS = new SBean.FlagBattleMapCFGS();
					flagmapCFGS.mapId = excelReadContext.getIntValue(row, col++);
					if (flagCFGS.flags.containsKey(flagmapCFGS.mapId))
						throw new Exception("帮派夺旗战 row " + row + " mapId " + flagmapCFGS.mapId + " 重复！");
					if (!gdcfgs.worldMaps.containsKey(flagmapCFGS.mapId) || gdcfgs.worldMaps.get(flagmapCFGS.mapId).pkType != GameData.MAP_PKTYPE_NORMAL)
						throw new Exception("帮派夺旗战 row " + row + " mapId " + flagmapCFGS.mapId + " 对应地图PK类型不对！");
					flagmapCFGS.flagPoint = excelReadContext.getVector3(row, col++, ";");
					flagmapCFGS.flagId = excelReadContext.getIntValue(row, col++);
					flagmapCFGS.monsterPointId = excelReadContext.getIntegerList(row, col++, ";");
					if (flagmapCFGS.monsterPointId.isEmpty())
						throw new Exception("帮派夺旗战 row " + row + " 刷怪点数量 " + flagmapCFGS.monsterPointId.size() + " 必须大于0！");
					flagmapCFGS.timeSectVitReward = excelReadContext.getIntValue(row, col++);
					if (flagmapCFGS.timeSectVitReward < 0)
						throw new Exception("帮派夺旗战 row " + row + " 争夺期奖励 " + flagmapCFGS.timeSectVitReward + " 不能小于0！");
					flagmapCFGS.endSectVitReward = excelReadContext.getIntValue(row, col++);
					if (flagmapCFGS.endSectVitReward < 0)
						throw new Exception("帮派夺旗战 row " + row + " 每日结算奖励 " + flagmapCFGS.endSectVitReward + " 不能小于0！");
					flagmapCFGS.timeRoleReward = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						int itemId = excelReadContext.getIntValue(row, col++);
						int itemNum = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdcfgs, row, itemId, true);
						if (itemNum < 0)
							throw new Exception("帮派夺旗战 row " + row + " 奖励道具数量 " + itemNum + " 不能小于0！");
						if (itemId != 0)
							flagmapCFGS.timeRoleReward.add(new DummyGoods(itemId, itemNum));
					}
					flagmapCFGS.endRoleReward = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						int itemId = excelReadContext.getIntValue(row, col++);
						int itemNum = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdcfgs, row, itemId, true);
						if (itemNum < 0)
							throw new Exception("帮派夺旗战 row " + row + " 奖励道具数量 " + itemNum + " 不能小于0！");
						if (itemId != 0)
							flagmapCFGS.endRoleReward.add(new DummyGoods(itemId, itemNum));
					}

					flagCFGS.flags.put(flagmapCFGS.mapId, flagmapCFGS);
					row++;
				}
			}
			
			excelReadContext.ReadSheet(2);
			{
				Map<Integer, SBean.SpawnPointCFGS> cfgs = new HashMap<>();
				final int colStart = 0;
				final int rowStart = 1;
				int row = rowStart;
				int col = colStart;
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.SpawnPointCFGS spawnPointCfg = new SBean.SpawnPointCFGS();
					spawnPointCfg.id = excelReadContext.getIntValue(row, col++);
					int mapID = point2map.getOrDefault(spawnPointCfg.id, 0);
					SBean.MapClusterCFGS mcCfg = gdcfgs.mapClusters.get(mapID);
					
					spawnPointCfg.position = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcCfg, spawnPointCfg.position, "刷怪点");
					spawnPointCfg.isRandom = excelReadContext.getByteValue(row, col++);
					checkBoolean(row, spawnPointCfg.isRandom);
					spawnPointCfg.randomRadius = excelReadContext.getIntValue(row, col++);
					if(spawnPointCfg.isRandom == 1 && spawnPointCfg.randomRadius == 0)
						throw new Exception("row  " + row + " 刷怪点 是否随机位置: " + spawnPointCfg.isRandom + " 随机半径:" + spawnPointCfg.randomRadius);
					spawnPointCfg.monsterID = excelReadContext.getIntValue(row, col++);
					this.checkMonsterID(gdcfgs, row, spawnPointCfg.monsterID);
					spawnPointCfg.spawnType = excelReadContext.getIntValue(row, col++);
					switch (spawnPointCfg.spawnType)
					{
					case GameData.BYORDER:
					case GameData.BYTIME:
					case GameData.BYNUMBER:
						break;
					default:
						throw new Exception("row " + row + " 不支持的刷怪方式  " + spawnPointCfg.spawnType + " !");
					}
					spawnPointCfg.interval = excelReadContext.getIntValue(row, col++);
					spawnPointCfg.spawnTimes = excelReadContext.getIntValue(row, col++);
					spawnPointCfg.spawnNum = new ArrayList<>();
					for (int i = 0; i < 9; i++)
					{
						if (excelReadContext.isNotEmpty(row, col))
							spawnPointCfg.spawnNum.add(excelReadContext.getIntValue(row, col++));
						else
							break;
					}
					
					col = colStart + 18;
					spawnPointCfg.rotationType = excelReadContext.getByteValue(row, col++);
					spawnPointCfg.rotation =excelReadContext.getVector3F(row, col++, ";");
					if (cfgs.put(spawnPointCfg.id, spawnPointCfg) != null)
						throw new Exception("副本刷怪点 id重复，id = " + spawnPointCfg.id);
					row++;
					col = colStart;
				}
				flagCFGS.flagMonsters = cfgs;
			}
			
			gdcfgs.flagBattle = flagCFGS;
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	private void loadHeirloomTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.HeirloomCFGS heirloomCFGS = new SBean.HeirloomCFGS();
			excelReadContext.ReadSheet(0);
			{
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "基础配置");
					int row = rowStart + 1;
					int col = colStart + 1;
					heirloomCFGS.remainIndex = excelReadContext.getIntValue(row++, col);
					if (heirloomCFGS.remainIndex <= 0 || heirloomCFGS.remainIndex > gdcfgs.remainActivitys.size())
						throw new Exception("传家宝 row " + row + " 七日留存序号 " + heirloomCFGS.remainIndex + " 无效！");
					heirloomCFGS.maxPerfect = excelReadContext.getIntValue(row++, col);
					if (heirloomCFGS.maxPerfect <= 0)
						throw new Exception("传家宝 row " + row + " 最大完美度 " + heirloomCFGS.maxPerfect + " 必须大于0！");
					heirloomCFGS.dayMaxWipeTimes = excelReadContext.getIntValue(row++, col);
					if (heirloomCFGS.dayMaxWipeTimes <= 0)
						throw new Exception("传家宝 row " + row + " 每日最大擦拭次数 " + heirloomCFGS.dayMaxWipeTimes + " 必须大于0！");
				}
				{
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "炼化配置");
					int row = rowStart + 1;
					int col = colStart + 1;
					heirloomCFGS.takeoutPerfect = excelReadContext.getIntValue(row++, col);
					if (heirloomCFGS.takeoutPerfect <= 0)
						throw new Exception("传家宝 row " + row + " 取出所需完美度 " + heirloomCFGS.takeoutPerfect + " 必须大于0！");

					row = excelReadContext.locateColumnTag(colStart, "颜色调类型对应值（取出前）");
					heirloomCFGS.wipePerfectIn = excelReadContext.getIntegerList(row++, col, ";");
					heirloomCFGS.wipePerfectOut = excelReadContext.getIntegerList(row++, col, ";");
				}
			}
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				heirloomCFGS.properties = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					int col = colStart;
					SBean.HeirloomPropCFGS heirloomPropCFGS = new SBean.HeirloomPropCFGS();
					heirloomPropCFGS.perfectDegree = excelReadContext.getIntValue(row, col);
					int oldsize= heirloomCFGS.properties.size();
					if (oldsize > 0 && heirloomPropCFGS.perfectDegree <= heirloomCFGS.properties.get(oldsize - 1).perfectDegree)
						throw new Exception("传家宝属性 row " + row + " 完美度必须递增！");
					col+=2;
					heirloomPropCFGS.properties = new ArrayList<>();
					for (int i = 0; i < 5; i++)
					{
						int propId = excelReadContext.getIntValue(row, col++);
						int propNum = excelReadContext.getIntValue(row, col++);
						if (propId != 0)
						{
							checkPropertyIDValid(gdcfgs, row, propId);
							if (propNum < 0)
								throw new Exception("传家宝属性 row " + row + " 属性值 " + propNum + " 不能小于0！");
							heirloomPropCFGS.properties.add(new SBean.AttrCFGS(propId, propNum));
						}
					}
					heirloomCFGS.properties.add(heirloomPropCFGS);
					row++;
				}
			}
			excelReadContext.ReadSheet(2);
			{
				{
					heirloomCFGS.strengths = new ArrayList<>();
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "强化层级");
					int row = rowStart + 2;
					int col = colStart + 1;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int oldSize = heirloomCFGS.strengths.size();
						int strengthLvl = excelReadContext.getIntValue(row, col++);
						if (strengthLvl != oldSize + 1)
							throw new Exception("传家宝 row " + row + " 强化层级 " + strengthLvl + " 不连续！");
						SBean.HeirloomStrengthCFGS cfg = new SBean.HeirloomStrengthCFGS(new ArrayList<>());
						while (excelReadContext.isNotEmpty(row, col))
						{
							int propMax = excelReadContext.getIntValue(row, col++);
							if (propMax <= 0)
								throw new Exception("传家宝 row " + row + " 最大属性 " + propMax + " 必须大于0！");
							cfg.propMax.add(propMax);
						}
						if (oldSize > 0 && cfg.propMax.size() != heirloomCFGS.strengths.get(oldSize - 1).propMax.size())
							throw new Exception("传家宝 row " + row + " 属性数量与上一行不符合！");
						heirloomCFGS.strengths.add(cfg);
						row++;
						col = colStart + 1;
					}
				}
				{
					heirloomCFGS.strengthProperties = new ArrayList<>();
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "可选属性");
					int row = rowStart + 2;
					int col = colStart + 1;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int oldSize = heirloomCFGS.strengthProperties.size();
						int propIndex = excelReadContext.getIntValue(row, col++);
						if (propIndex != oldSize + 1)
							throw new Exception("传家宝 row " + row + " 属性序号 " + propIndex + " 不连续！");
						int propId = excelReadContext.getIntValue(row, col++);
						checkPropertyIDValid(gdcfgs, row, propId);
						int propNum = excelReadContext.getIntValue(row, col++);
						if (propNum <= 0)
							throw new Exception("传家宝 row " + row + " 基础属性 " + propNum + " 必须大于0！");
						heirloomCFGS.strengthProperties.add(new SBean.Prop(propId, propNum));
						row++;
						col = colStart + 1;
					}
				}
				{
					heirloomCFGS.strengthPropGet = new ArrayList<>();
					final int colStart = 0;
					final int rowStart = excelReadContext.locateColumnTag(colStart, "颜色序号对应倍率");
					int row = rowStart + 2;
					int col = colStart + 1;
					while (excelReadContext.isNotEmpty(row, col))
					{
						int oldSize = heirloomCFGS.strengthPropGet.size();
						int index = excelReadContext.getIntValue(row, col++);
						if (index != oldSize + 1)
							throw new Exception("传家宝 row " + row + " 颜色条类型 " + index + " 不连续！");
						int rate = excelReadContext.getIntValue(row, col++);
						if (rate <= 0)
							throw new Exception("传家宝 row " + row + " 倍率 " + rate + " 必须大于0！");
						heirloomCFGS.strengthPropGet.add(rate);
						row++;
						col = colStart + 1;
					}
				}
			}
			gdcfgs.heirloom = heirloomCFGS;
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	private void loadRobotTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.RobotCFGS robot = new SBean.RobotCFGS();
			excelReadContext.ReadSheet(0);
			{
				int row = 1;
				int col = 1;
				robot.surNames = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, 0))
				{
					robot.surNames.add(excelReadContext.getStringValue(row++, col));
				}
			}
			
			excelReadContext.ReadSheet(1);
			{
				int row = 1;
				int col = 1;
				robot.overviews = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, col))
				{
					String name = excelReadContext.getStringValue(row, col);
					int gender = excelReadContext.getIntValue(row++, col + 1);
					robot.overviews.add(new SBean.RobotOverviewCFGS(name, gender));
				}
			}
			
			excelReadContext.ReadSheet(2);
			{
				robot.common = new SBean.RobotCommonCFGS();
				int row = 0;
				int col = 0;
				final int rowArena = excelReadContext.locateColumnTag(0, "机器人广播竞技场排名");
				{
					row = rowArena;
					col = 2;
					
					int rankMin = excelReadContext.getIntValue(row++, col);
					int rankMax = excelReadContext.getIntValue(row++, col);
					robot.common.arenaRank = new SBean.GradeCFGS(rankMin, rankMax);
				}
				
				final int rowBroadCastTime = excelReadContext.locateColumnTag(0, "广播出现频率对应时间段");
				{
					row = rowBroadCastTime + 1;
					col = 1;
					
					robot.common.broadcastTime = new ArrayList<>();
					while(excelReadContext.isNotEmpty(row, 1))
					{
						int floor = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col));
						List<Integer> transform = excelReadContext.getIntegerList(row, col + 1, ";");
						SBean.GradeCFGS t = new SBean.GradeCFGS(transform.get(0) * 60, transform.get(1) * 60);
						
						List<Integer> arena = excelReadContext.getIntegerList(row, col + 1, ";");
						SBean.GradeCFGS a = new SBean.GradeCFGS(arena.get(0) * 60, arena.get(1) * 60);
						
						robot.common.broadcastTime.add(new SBean.RobotBroadcastTimeCFGS(floor, t, a));
						row++;
					}
				}
				
				final int rowOpenTime = excelReadContext.locateColumnTag(0, "机器人功能第N天开启");
				{
					robot.common.openDays = new HashMap<>();
					row = rowOpenTime;
					col = 2;
					
					int arenaOpenDay = excelReadContext.getIntValue(row++, col);
					robot.common.openDays.put((int)GameData.ROLLNOTICE_TYPE_WIN_HIGH_SCORE, arenaOpenDay);
					int transformOpneDay = excelReadContext.getIntValue(row++, col);
					robot.common.openDays.put((int)GameData.ROLLNOTICE_TYPE_TRANSFER, transformOpneDay);
				}
			}
			
			gdcfgs.robot = robot;
		}
		System.out.println("load table " + fileName + " success.");
	}
	
	public void loadAuctionTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.AuctionCFGS cfg = new SBean.AuctionCFGS(new HashSet<>(), new ArrayList<>());
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int auctionType = excelReadContext.getIntValue(row++, colStart);
					switch (auctionType)
					{
						case GameData.AUCTION_ITEM_TYPE_GROW:
						case GameData.AUCTION_ITEM_TYPE_SKILL:
						case GameData.AUCTION_ITEM_TYPE_WEAPON:
						case GameData.AUCTION_ITEM_TYPE_PET:
						case GameData.AUCTION_ITEM_TYPE_CLAN:
						case GameData.AUCTION_ITEM_TYPE_FASHION:
						case GameData.AUCTION_ITEM_TYPE_DRUG:
						case GameData.AUCTION_ITEM_TYPE_SPIRIT:
						case GameData.AUCTION_ITEM_TYPE_OTHER:
							break;
						default:
							if(auctionType < 0 || auctionType > GameData.AUCTION_ITEM_TYPE_EQUIP)
								throw new Exception("row " + row + " 不支持的寄售行分类类型 " + auctionType + " !");
					}
					
					cfg.types.add(auctionType);
				}
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = 1;
				
				int lastLvl = 0;
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int level = excelReadContext.getIntValue(row++, col);
					if(level <= lastLvl)
						throw new Exception("row " + row + " 装备等级段 " + level + " invalid!");
					
					cfg.lvls.add(level);
				}
				
				gdcfgs.auction = cfg;
			}
		}
		
		System.out.println("load table " + fileName + " success.");
	}

	private void loadFlashSaleResourceTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
		    Set<Integer> resources = new TreeSet<>();
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 3;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int id = excelReadContext.getIntValue(row, col);
					String path = excelReadContext.getStringValue(row, col+1);
					
					if (path==null)
					    throw new Exception(" row " + row + "  picture have no path");
					
					if (!resources.add(id))
					    throw new Exception(" row " + row + "  id conflict " + id );
					row++;
				}
				
				gdcfgs.flashsaleresource = resources;
			}
		}

		System.out.println("load table " + fileName + " success.");
	}
	
	private void loadAdversResourceTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
		    Set<Integer> resources = new TreeSet<>();
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 3;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int id = excelReadContext.getIntValue(row, col);
					String path = excelReadContext.getStringValue(row, col+1);
					
					if (path==null)
					    throw new Exception(" row " + row + "  picture have no path");
					
					if (!resources.add(id))
					    throw new Exception(" row " + row + "  id conflict " + id );
					row++;
				}
				
				gdcfgs.adversresource = resources;
			}
		}

		System.out.println("load table " + fileName + " success.");
	}

	public void loadActivityLastTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			excelReadContext.ReadSheet(0);
			{
				final int rowStart = 1;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;

				gdcfgs.activityLast = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					col = colStart;
					SBean.ActivityLastCFGS cfg = new SBean.ActivityLastCFGS();
					int groupId = excelReadContext.getIntValue(row, col++);
					if (groupId != gdcfgs.activityLast.size() + 1)
						throw new Exception("row " + row + " 组ID " + groupId + " 不连续!");
					cfg.cost = excelReadContext.getIntegerList(row, col++, ",");
					cfg.arg1 = excelReadContext.getIntValue(row, col++);
					gdcfgs.activityLast.add(cfg);
					row++;
				}
			}
		}

		System.out.println("load table " + fileName + " success.");
	}

	public void loadSteleTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.SteleCFGS cfg = new SBean.SteleCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int rowCommon = excelReadContext.locateColumnTag(0, "通用配置");
				{
					int row = rowCommon + 1;
					int col = 2;
					
					SBean.SteleBaseCFGS base = new SBean.SteleBaseCFGS(new HashSet<>(), 0, 0, 0, 0, 0, 0);
					List<Integer> openDays = excelReadContext.getIntegerList(row++, col, ";");
					for(int w: openDays)
					{
						if(w < 0 || w > GameData.WEEK_DAY_SATURDAY)
							throw new Exception("row " + row + " 太玄碑文 活动开放日 " + w + " 非法!");

						base.openDays.add(w);
					}
					base.startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
					base.lastTime = excelReadContext.getIntValue(row++, col);
					if(base.lastTime > 12 * 3600)
						throw new Exception("row " + row + " 持续时间 " + base.lastTime + " 大于12小时");
					
					base.lvlReq = excelReadContext.getIntValue(row++, col);
					base.showRanks = excelReadContext.getIntValue(row++, col);
					base.maxCards = excelReadContext.getIntValue(row++, col);
					if(base.maxCards > SteleManager.STELT_CARD_MAX_COUNT)
						throw new Exception("row " + row + " 活动拓片最大获得数 " + base.lastTime + " 非法");

					cfg.base = base;
				}
				
				final int rowRankReward = excelReadContext.locateColumnTag(0, "奖励设定");
				{
					int row = rowRankReward + 2;
					int col = 1;
					
					int maxRanks = 0;
					List<SBean.SteleRewardCFGS> rankRewards = new ArrayList<>();
					while(excelReadContext.isNotEmpty(row, col))
					{
						SBean.SteleRewardCFGS rankReward = new SBean.SteleRewardCFGS(0, new ArrayList<>());
						rankReward.floor = excelReadContext.getIntValue(row, col++);
						if(rankReward.floor < maxRanks)
							throw new Exception("row " + row + " 奖励设定 排名 " + rankReward.floor + " 非法!");
						
						maxRanks = rankReward.floor;
						while(excelReadContext.isNotEmpty(row, col))
						{
							int id = excelReadContext.getIntValue(row, col++);
							int count = excelReadContext.getIntValue(row, col++);
							checkEntityIDValid(gdcfgs, row, id, true);
							if (id != 0)
								rankReward.rewards.add(new SBean.DummyGoods(id, count));
						}
						rankRewards.add(rankReward);
						row++;
						col = 1;
					}
					
					cfg.base.maxRanks = maxRanks;
					if(cfg.base.maxRanks < cfg.base.showRanks)
						throw new Exception("太玄碑文 最大排行" + cfg.base.maxRanks + " 小于 太玄碑文排行榜显示名次 " + cfg.base.showRanks);
					
					cfg.rankRewards = rankRewards;
				}
				
				final int rowJoinReward = excelReadContext.locateColumnTag(0, "参与奖");
				{
					int row = rowJoinReward + 1;
					int col = 2;
					List<SBean.DummyGoods> joinRewards = new ArrayList<>();
					while(excelReadContext.isNotEmpty(row, col))
					{
						int id = excelReadContext.getIntValue(row, col++);
						int count = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdcfgs, row, id, true);
						if (id != 0)
							joinRewards.add(new SBean.DummyGoods(id, count));
					}
					
					cfg.joinRewards = joinRewards;
				}
			}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 0;
				final int colStart = 0;
				int row = rowStart + 2;
				int col = colStart;
				
				Map<Integer, Set<Integer>> typeMineIdDs = new HashMap<>();
				List<SBean.SteleMineralTypeCFGS> mineralTypes = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SteleMineralTypeCFGS mineralType = null;
					Set<Integer> mineIDs = null;
					int type = excelReadContext.getIntValue(row, col++);
					if(type > mineralTypes.size())
					{
						mineralType = new SBean.SteleMineralTypeCFGS(new ArrayList<>());
						mineIDs = new HashSet<>();
						mineralTypes.add(mineralType);
						typeMineIdDs.put(type, mineIDs);
						if(type != mineralTypes.size())
							throw new Exception("row " + row + " 碑文分组 " + type + " 不连续!");
					}
					else
					{
						mineralType = mineralTypes.get(type - 1);
						mineIDs = typeMineIdDs.get(type);
					}
					
					col++;
					SBean.SteleMineralCFGS steleMineral = new SBean.SteleMineralCFGS();
					steleMineral.mineralID = excelReadContext.getIntValue(row, col++);
					SBean.MineralCFGS mCfg = gdcfgs.minerals.get(steleMineral.mineralID);
					if(mCfg == null || mCfg.mineralType != GameData.MINERAL_TYPE_STELE)
						throw new Exception("row " + row + " 碑文对应矿物ID " + steleMineral.mineralID + " 非法!");
					
					if(!mineIDs.add(steleMineral.mineralID))
						throw new Exception("row " + row + " 碑文分组 " + type + " 对应矿物ID " + steleMineral.mineralID + " 重复!");
					
					steleMineral.mineralTimes = excelReadContext.getIntValue(row, col++);
					if(steleMineral.mineralTimes <= 0)
						throw new Exception("row " + row + " 碑文耐久 " + steleMineral.mineralTimes + " 非法!");
					
					int mapID = excelReadContext.getIntValue(row, col++);
					SBean.MapClusterCFGS mcc = gdcfgs.mapClusters.get(mapID);
					if(mcc == null || mcc.type != GameData.MAP_TYPE_MAP_WORLD)
						throw new Exception("row " + row + " 碑文所在地图ID " + mapID + " 非法!");
					
					SBean.Vector3 pos = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcc, pos, "碑文所在位置坐标");
					SBean.Vector3F rotation = excelReadContext.getVector3F(row, col++, ";");
					steleMineral.mapLocation = new SBean.MapLocation(mapID, new SBean.Location(pos, rotation));
					
					int mineralerMinCard = excelReadContext.getIntValue(row, col++);
					if(mineralerMinCard < 0)
						throw new Exception("row " + row + " 开采者获得拓片数量下限 " + mineralerMinCard + " 非法!");
					int mineralerMaxCard = excelReadContext.getIntValue(row, col++);
					if(mineralerMaxCard < 0)
						throw new Exception("row " + row + " 开采者获得拓片数量上限 " + mineralerMaxCard + " 非法!");
					steleMineral.mineralCards = new SBean.GradeCFGS(mineralerMinCard, mineralerMaxCard);
					
					int memberMinCard = excelReadContext.getIntValue(row, col++);
					if(memberMinCard < 0)
						throw new Exception("row " + row + " 队友获得拓片数量下限 " + memberMinCard + " 非法!");
					int memberMaxCard = excelReadContext.getIntValue(row, col++);
					if(memberMaxCard < 0)
						throw new Exception("row " + row + " 队友获得拓片数量上限 " + memberMaxCard + " 非法!");
					steleMineral.memberCards = new SBean.GradeCFGS(memberMinCard, memberMaxCard);
					
					steleMineral.refreshMonster = excelReadContext.getIntValue(row, col++) / 10_000.f;
					mineralType.minerals.add(steleMineral);
					
					col = colStart;
					row++;
				}
				
				cfg.mineralTypes = mineralTypes;
			}
			
			excelReadContext.ReadSheet(2);
			{
				final int rowStart = 0;
				final int colStart = 0;
				int row = rowStart + 2;
				int col = colStart;
				
				List<SBean.SteleLevelMonsterCFGS> levelMonsters = new ArrayList<>();
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.SteleLevelMonsterCFGS levelMonster = new SBean.SteleLevelMonsterCFGS(0, 0, new ArrayList<>());
					levelMonster.level = excelReadContext.getIntValue(row, col++);
					levelMonster.standByTime = excelReadContext.getIntValue(row, col++);
					if(levelMonster.standByTime < 0)
						throw new Exception("row " + row + " 怪物寿命 " + levelMonster.standByTime + " 非法!");
					
					float sum = 0;
					for(int i = 0; i < 3; i++)
					{
						sum += excelReadContext.getIntValue(row, col++);
						int monsterID = excelReadContext.getIntValue(row, col++);
						checkMonsterID(gdcfgs, row, monsterID);
						int count = excelReadContext.getIntValue(row, col++);
						byte broadcast = excelReadContext.getByteValue(row, col++);
						checkBoolean(row, broadcast);
						
						levelMonster.monsters.add(new SBean.SteleMonsterCFGS(sum, monsterID, count, broadcast));
					}
					
					for(SBean.SteleMonsterCFGS m: levelMonster.monsters)
						m.weight /= sum;
					
					levelMonsters.add(levelMonster);
					col = colStart;
					row++;
				}
				
				cfg.lvlMonsters = levelMonsters;
			}
			
			gdcfgs.stele = cfg;
		}
		
		System.out.println("load table " + fileName + " success.");
	}

	public void loadJusticeMapTable(String fileName, GameDataCFGS gdcfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.JusticeMapCFGS cfg = new SBean.JusticeMapCFGS();
			excelReadContext.ReadSheet(0);
			{
				int row = 0;
				int col = 1;

				cfg.openLvl = excelReadContext.getIntValue(row++, col);
				if (cfg.openLvl < 0)
					throw new Exception("正义之心 row " + row + " 开启等级 " + cfg.openLvl + " 非法！");
				cfg.dayEnterTimes = excelReadContext.getIntValue(row++, col);
				if (cfg.dayEnterTimes < 0)
					throw new Exception("正义之心 row " + row + " 每日参与次数 " + cfg.dayEnterTimes + " 非法！");
				cfg.npcId = excelReadContext.getIntValue(row++, col);
				if (!gdcfgs.npcs.containsKey(cfg.npcId))
					throw new Exception("正义之心 row " + row + " 活动NPCID " + cfg.npcId + " 不存在！");
				cfg.openDay = excelReadContext.getIntegerList(row++, col, ";").stream().collect(Collectors.toSet());
				cfg.chatDistance = excelReadContext.getIntValue(row++, col);
				if (cfg.chatDistance < 0)
					throw new Exception("正义之心 row " + row + " 对话距离 " + cfg.chatDistance + " 非法！");
				row++;
				cfg.teamInviteChatInterval = excelReadContext.getIntValue(row++, col);
				if (cfg.teamInviteChatInterval < 0)
					throw new Exception("正义之心 row " + row + " 队伍集结冷却 " + cfg.teamInviteChatInterval + " 非法！");
			}
			
			excelReadContext.ReadSheet(1);
			{
				cfg.level2mapId = new ArrayList<>();
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.LevelToMapIdCFGS l2mCfgs = new SBean.LevelToMapIdCFGS();
					l2mCfgs.level = excelReadContext.getIntValue(row, col++);
					if (l2mCfgs.level < 0 || (cfg.level2mapId.size() > 0 && l2mCfgs.level <= cfg.level2mapId.get(cfg.level2mapId.size() - 1).level))
						throw new Exception("正义之心 row " + row + " 等级 " + l2mCfgs.level + " 非法！");
					l2mCfgs.mapId = excelReadContext.getIntValue(row, col++);
					checkMapID(gdcfgs, row, l2mCfgs.mapId);
					cfg.level2mapId.add(l2mCfgs);
					col = colStart;
					row++;
				}
			}
			
			excelReadContext.ReadSheet(2);
			{
				cfg.openTimes = new ArrayList<>();
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					List<Integer> timePoint = new ArrayList<>();
					List<String> lst = excelReadContext.getStringList(row, col, ";");
					for (String str : lst)
					{
						int daySecond = GameTime.parseSecondOfDay(str);
						if (daySecond < 0)
							throw new Exception("正义之心 row " + row + " 时间点 " + str + " 非法！");
						timePoint.add(daySecond);
					}
					int startTime = timePoint.get(0);
					int endTime = timePoint.get(1);
					if (cfg.openTimes.size() > 0 && startTime < cfg.openTimes.get(cfg.openTimes.size() - 1).endTime)
						throw new Exception("正义之心 row " + row + " 时间点开始时间小于上一段的结束时间！");
					cfg.openTimes.add(new SBean.TimeSpan(startTime, endTime));
					col = colStart;
					row++;
				}
			}
			
			excelReadContext.ReadSheet(3);
			{
				cfg.npcPoints = new ArrayList<>();
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while(excelReadContext.isNotEmpty(row, colStart))
				{
					int mapId = excelReadContext.getIntValue(row, col++);
					Vector3 position = excelReadContext.getVector3(row, col++, ";");
					Vector3F rotation = excelReadContext.getVector3F(row, col++, ";");
					checkMapID(gdcfgs, row, mapId);
					checkMapPosValid(row, gdcfgs.mapClusters.get(mapId), position, "正义之心NPC点");
					cfg.npcPoints.add(new SBean.MapLocation(mapId, new SBean.Location(position, rotation)));
					col = colStart;
					row++;
				}
			}
			
			gdcfgs.justice = cfg;
		}
		
		System.out.println("load table " + fileName + " success.");
	}
	
    private void loadEmergencyTable(String fileName, GameDataCFGS cfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.EmergencyCFGS emergency = new SBean.EmergencyCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int startRow = 0;
				final int startCol = 1;

				int row = startRow;
				int col = startCol;

				emergency.openDay = excelReadContext.getIntegerList(row++, col, ";").stream().collect(Collectors.toSet());
				emergency.levelNeed = excelReadContext.getIntValue(row++, col);
				if (emergency.levelNeed <= 0)
					throw new Exception("江湖告急 row " + row + " 开启等级 " + emergency.levelNeed + " 必须大于0！");
				emergency.prestigeTeamRatio = excelReadContext.getIntValue(row++, col);
				if (emergency.prestigeTeamRatio <= 0)
					throw new Exception("江湖告急 row " + row + " 组队声望修正系数 " + emergency.prestigeTeamRatio + " 必须大于0！");
				emergency.prestigeDistance = excelReadContext.getIntValue(row++, col);
				if (emergency.prestigeDistance <= 0)
					throw new Exception("江湖告急 row " + row + " 组队声望共享距离 " + emergency.prestigeDistance + " 必须大于0！");
				emergency.openTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
				emergency.duration = excelReadContext.getIntValue(row++, col);
				if (emergency.duration <= 0)
					throw new Exception("江湖告急 row " + row + " 持续时间 " + emergency.duration + " 必须大于0！");
				emergency.rankDisplayLimit = excelReadContext.getIntValue(row++, col);
				if (emergency.rankDisplayLimit <= 0)
					throw new Exception("江湖告急 row " + row + " 排行榜显示大小 " + emergency.rankDisplayLimit + " 必须大于0！");
				emergency.rankSaveLimit = excelReadContext.getIntValue(row++, col);
				if (emergency.rankSaveLimit <= 0 || emergency.rankSaveLimit > 2000)
					throw new Exception("江湖告急 row " + row + " 排行榜记录大小 " + emergency.rankSaveLimit + " 必须大于0，并且不能超过2000！");
			}

			excelReadContext.ReadSheet(1);
			{
				final int startRow = 1;
				final int startCol = 0;

				int row = startRow;
				int col = startCol;

				emergency.activities = new HashMap<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.EmergencyActivityCFGS ea = new SBean.EmergencyActivityCFGS();
					ea.activityId = excelReadContext.getIntValue(row, col++);
					if (emergency.activities.containsKey(ea.activityId))
						throw new Exception("江湖告急 row " + row + " 活动ID " + ea.activityId + " 重复！");
					ea.mapId = excelReadContext.getIntValue(row, col++);
					checkMapID(cfgs, row, ea.mapId);
					ea.maxRoleSize = excelReadContext.getIntValue(row, col++);
					if (ea.maxRoleSize <= 0)
						throw new Exception("江湖告急 row " + row + " 最大人数 " + ea.maxRoleSize + " 必须大于0！");
					ea.basePrestige = excelReadContext.getIntegerList(row, col++, ";");
					if (ea.basePrestige.size() <= 0)
						throw new Exception("江湖告急 row " + row + " 基础声望未配置！");

					ea.openTime = new ArrayList<>();
					emergency.activities.put(ea.activityId, ea);
					col = startCol;
					row++;
				}
			}

			excelReadContext.ReadSheet(2);
			{
				final int startRow = 1;
				final int startCol = 0;

				int row = startRow;
				int col = startCol;

				emergency.rankRewards = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, col))
				{
					SBean.EmergencyRankCFGS er = new SBean.EmergencyRankCFGS();
					er.rank = excelReadContext.getIntValue(row, col++);
					int lastSize = emergency.rankRewards.size();
					if (lastSize > 0 && emergency.rankRewards.get(lastSize - 1).rank >= er.rank)
						throw new Exception("江湖告急 row " + row + " 排名 " + er.rank + " 必须递增！");
					er.rewards = new ArrayList<>();
					while (excelReadContext.isNotEmpty(row, col))
					{
						int itemId = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(cfgs, row, itemId, true);
						int itemCount = excelReadContext.getIntValue(row, col++);
						if (itemCount > 0)
							er.rewards.add(new SBean.DummyGoods(itemId, itemCount));
					}
					emergency.rankRewards.add(er);
					col = startCol;
					row++;
				}
			}

			excelReadContext.ReadSheet(3);
			{
				final int startRow = 1;
				final int startCol = 0;

				int row = startRow;
				int col = startCol;

				while (excelReadContext.isNotEmpty(row, col))
				{
					int openTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					int endTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					List<Integer> activitys = excelReadContext.getIntegerList(row, col++, ";");
					for (int activityId : activitys)
					{
						SBean.EmergencyActivityCFGS activity = emergency.activities.get(activityId);
						int lastSize = activity.openTime.size();
						if (lastSize > 0 && openTime < activity.openTime.get(lastSize - 1).endTime)
							throw new Exception("江湖告急 row " + row + " 活动 " + activityId + " 时间冲突！");
						activity.openTime.add(new SBean.TimeSpan(openTime, endTime));
					}
					col = startCol;
					row++;
				}
			}
			cfgs.emergency = emergency;
		}
		System.out.println("load table " + fileName + " success.");
	}
	
    private void loadLucklyStarTable(String fileName, GameDataCFGS cfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.LucklyStarCFGS lucklyStar = new SBean.LucklyStarCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int rowCommon = excelReadContext.locateColumnTag(0, "幸运星通用配置");
				{
					int row = rowCommon + 1;
					int col = 2;
					lucklyStar.startRole = excelReadContext.getIntValue(row++, col);
					if (lucklyStar.startRole < 0)
						throw new Exception("幸运星 row " + row + " 起始人数 " + lucklyStar.startRole + " 不能小于0！");
					lucklyStar.levelNeed = excelReadContext.getIntValue(row++, col);
					if (lucklyStar.levelNeed <= 0)
						throw new Exception("幸运星 row " + row + " 等级需求 " + lucklyStar.levelNeed + " 必须大于0！");
					lucklyStar.starSendTimes = excelReadContext.getIntValue(row++, col);
					if (lucklyStar.starSendTimes <= 0)
						throw new Exception("幸运星 row " + row + " 幸运星发送次数 " + lucklyStar.starSendTimes + " 必须大于0！");
					lucklyStar.roleRecvTimes = excelReadContext.getIntValue(row++, col);
					if (lucklyStar.roleRecvTimes <= 0)
						throw new Exception("幸运星 row " + row + " 角色最大接受次数 " + lucklyStar.roleRecvTimes + " 必须大于0！");
					lucklyStar.sameProReward = excelReadContext.getIntValue(row++, col);
				}

				final int rowRewards = excelReadContext.locateColumnTag(0, "幸运星奖励分档");
				{
					final int colStart = 1;
					int row = rowRewards + 2;
					int col = colStart;
					lucklyStar.rankRewards = new ArrayList<>();
					while (excelReadContext.isNotEmpty(row, col))
					{
						SBean.LucklyStarLevelRewardCFGS lvlReward = new SBean.LucklyStarLevelRewardCFGS();
						lvlReward.level = excelReadContext.getIntValue(row, col++);
						int lastSize = lucklyStar.rankRewards.size();
						if (lastSize > 0 && lvlReward.level <= lucklyStar.rankRewards.get(lastSize - 1).level)
							throw new Exception("幸运星 row " + row + " 奖励等级 " + lvlReward.level + " 必须递增！");
						lvlReward.rewards = new ArrayList<>();
						while (excelReadContext.isNotEmpty(row, col))
						{
							int itemId = excelReadContext.getIntValue(row, col++);
							checkEntityIDValid(cfgs, row, itemId, true);
							int itemNum = excelReadContext.getIntValue(row, col++);
							if (itemId != 0 && itemNum > 0)
								lvlReward.rewards.add(new SBean.DummyGoods(itemId, itemNum));
						}
						if (lvlReward.rewards.isEmpty())
							throw new Exception("幸运星 row " + row + " 奖励未配置！");
						lucklyStar.rankRewards.add(lvlReward);
						row++;
						col = colStart;
					}
					if (lucklyStar.rankRewards.size() > 0)
					{
						if (lucklyStar.rankRewards.get(0).level > lucklyStar.levelNeed)
							throw new Exception("幸运星 最小奖励等级 " + lucklyStar.rankRewards.get(0).level + " 大于 参与等级" + lucklyStar.levelNeed + " 奖励未配置！");
					}
				}
			}

			cfgs.lucklyStar = lucklyStar;
		}
		System.out.println("load table " + fileName + " success.");
	}
    
    private void loadMasterTable(String fileName, GameDataCFGS cfgs) throws Exception
	{
		excelReadContext.ReadNextFile(fileName);
		{
			SBean.MasterCFGS master = new SBean.MasterCFGS();
			excelReadContext.ReadSheet(0);
			{
				final int rowCommon = excelReadContext.locateColumnTag(0, "通用配置");
				{
					int row = rowCommon + 1;
					int col = 2;
					
					master.minMasterLvl = excelReadContext.getIntValue(row++, col);
					master.minApprenticeLvl = excelReadContext.getIntValue(row++, col);
					master.maxApprenticeLvl = excelReadContext.getIntValue(row++, col);
					master.minGraduateLvl = excelReadContext.getIntValue(row++, col);
					master.maxApprenticeCount = excelReadContext.getIntValue(row++, col);
					master.dismissCoolTime = excelReadContext.getIntValue(row++, col);
					master.betrayCoolTime = excelReadContext.getIntValue(row++, col);
					master.reqGraduateCoolTime = excelReadContext.getIntValue(row++, col);
					master.autoAcceptGraduateTime = excelReadContext.getIntValue(row++, col);
					master.maxApplyMsgCount = excelReadContext.getIntValue(row++, col);
					master.maxBetrayMsgReserveTime = excelReadContext.getIntValue(row++, col);
					master.maxAnnouncementReserveTime = excelReadContext.getIntValue(row++, col);
					master.masterListPageSize = excelReadContext.getIntValue(row++, col);
					master.maxAnnounceLength = excelReadContext.getIntValue(row++, col);
					if( master.minApprenticeLvl > master.maxApprenticeLvl 
							|| master.maxApprenticeLvl + 1 != master.minGraduateLvl
							|| master.maxApprenticeLvl + 1 != master.minMasterLvl )
						throw new Exception("检查等级之间的关系!");
				}	
			}
			
			excelReadContext.ReadSheet(1);
			master.graduateApprenticeRewards = new ArrayList<>();
			{
				int row = 2;
				while(excelReadContext.isNotEmpty(row, 0))
    			{
					SBean.MasterGraduateRewardCFGS lvlcfg = new SBean.MasterGraduateRewardCFGS();
					lvlcfg.minScore = excelReadContext.getIntValue(row, 1);
					lvlcfg.rewards = new ArrayList<>();
					int rewardCount = excelReadContext.getIntValue(row, 2);
					final int start_col = 3;
					for(int i = 0; i < rewardCount; ++i)
					{
						int id = excelReadContext.getIntValue(row, start_col + 2 * i);
						checkEntityIDValid(cfgs, row, id, false);
						int count = excelReadContext.getIntValue(row, start_col + 2 * i + 1);
						if( count > 0 )
							lvlcfg.rewards.add(new SBean.DummyGoods(id, count));
					}
					master.graduateApprenticeRewards.add(lvlcfg);
					++row;
    			}
				if( master.graduateApprenticeRewards.isEmpty() )
					throw new Exception("徒弟没有出师奖励");
				master.minGraduateScore = master.graduateApprenticeRewards.get(0).minScore;				
			}
			
			excelReadContext.ReadSheet(2);
			master.graduateMasterRewards = new ArrayList<>();
			{
				int row = 2;
				int rewardCount = excelReadContext.getIntValue(row, 2);
				final int start_col = 3;
				for(int i = 0; i < rewardCount; ++i)
				{
					int id = excelReadContext.getIntValue(row, start_col + 2 * i);
					checkEntityIDValid(cfgs, row, id, false);
					int count = excelReadContext.getIntValue(row, start_col + 2 * i + 1);
					if( count > 0 )
						master.graduateMasterRewards.add(new SBean.DummyGoods(id, count));
				}
			}
			
			excelReadContext.ReadSheet(3);
			master.graduateTasks = new TreeMap<>();
			{
				int row = 2;
				while(excelReadContext.isNotEmpty(row, 0))
    			{
					int typeID = excelReadContext.getIntValue(row, 0);
					if( typeID != row - 1 )
						throw new Exception("ID必须依次增高");
					int target = excelReadContext.getIntValue(row, 2);
					int score = excelReadContext.getIntValue(row, 3);
					master.graduateTasks.put(typeID, new SBean.MasterGraduateTaskCFGS(typeID, target, score));
					++row;
    			}
			}
			
			cfgs.master = master;
		}
		System.out.println("load table " + fileName + " success.");
	}
    
    public void loadFightNpcTable(String fileName, GameDataCFGS gdcfgs) throws Exception
    {
    	excelReadContext.ReadNextFile(fileName);
    	{
    		excelReadContext.ReadSheet(0);
    		{
    			int row = 2;
    			int col = 0;
    			
    			int lastGroupID = 0;
    			List<SBean.FightNpcGroupCFGS> groups = new ArrayList<>();
    			while(excelReadContext.isNotEmpty(row, 0))
    			{
    				int groupID = excelReadContext.getIntValue(row, col++);
    				if(groupID != lastGroupID)
    				{
    					if(groupID != lastGroupID + 1)
    						throw new Exception("row " + row + " 约战分组 " + groupID + " 不连续!");
    					
    					lastGroupID = groupID;
    					groups.add(new SBean.FightNpcGroupCFGS(new ArrayList<>()));
    				}
    				col++;
    				SBean.FightNpcGroupCFGS group = groups.get(groupID - 1);
    				int npcID = excelReadContext.getIntValue(row, col++);
    				if(!gdcfgs.npcs.containsKey(npcID))
    					throw new Exception("row " + row + " 约战对应NPC " + npcID + " 非法!");
    				
    				int mapID = excelReadContext.getIntValue(row, col++);
    				if(!gdcfgs.fightNpcMaps.containsKey(mapID))
    					throw new Exception("row " + row + " 约战对应副本ID " + mapID + " 非法!");
    				SBean.FightNpcCFGS cfg = new SBean.FightNpcCFGS(mapID, new ArrayList<>(), new ArrayList<>(), 0, new ArrayList<>());
    				group.fightNpcs.add(cfg);
    				
    				List<Integer> trigCondTypes = excelReadContext.getIntegerList(row, col++, ";");
    				List<Integer> trigCondParams = excelReadContext.getIntegerList(row, col++, ";");
    				if(trigCondTypes.isEmpty() || trigCondTypes.size() != trigCondParams.size())
    					throw new Exception("row " + row + " 触发条件类型数量" + trigCondTypes.size() + " 触发条件类型参数数量 " + trigCondParams.size() + " 数量不一致!");
    				
    				for(int i = 0; i < trigCondTypes.size(); i++)
    				{
    					int type = trigCondTypes.get(i);
    					switch (type)
    					{
						case GameData.FIGHT_NPC_COND_TYPE_LEVEL:
						case GameData.FIGHT_NPC_COND_TYPE_POWER:
						case GameData.FIGHT_NPC_COND_TYPE_WEAPON_STAR_SUM:
						case GameData.FIGHT_NPC_COND_TYPE_SPIRIT_LVL_SUM:
							break;
						default:
							throw new Exception("row " + row + " 触发条件类型 " + type + " 非法!");
						}
    					
    					cfg.trigConds.add(new SBean.FightNpcCondCFGS(type, trigCondParams.get(i)));
    				}
    				
    				List<Integer> openCondTypes = excelReadContext.getIntegerList(row, col++, ";");
    				List<Integer> openCondParams = excelReadContext.getIntegerList(row, col++, ";");
    				if(openCondTypes.isEmpty() || openCondTypes.size() != openCondParams.size())
    					throw new Exception("row " + row + " 开启条件类型数量" + openCondTypes.size() + " 开启条件类型参数数量 " + openCondParams.size() + " 数量不一致!");
    				
    				for(int i = 0; i < openCondTypes.size(); i++)
    				{
    					int type = openCondTypes.get(i);
    					switch (type)
    					{
						case GameData.FIGHT_NPC_COND_TYPE_LEVEL:
						case GameData.FIGHT_NPC_COND_TYPE_POWER:
						case GameData.FIGHT_NPC_COND_TYPE_WEAPON_STAR_SUM:
						case GameData.FIGHT_NPC_COND_TYPE_SPIRIT_LVL_SUM:
							break;
						default:
							throw new Exception("row " + row + " 开启条件类型 " + type + " 非法!");
						}
    					
    					cfg.openConds.add(new SBean.FightNpcCondCFGS(type, openCondParams.get(i)));
    				}
    				
    				cfg.coolTime = excelReadContext.getIntValue(row, col++) * 60;		//配的是分，转成秒
    				for(int i = 0; i < 5; i++)
    				{
    					List<Integer> itemIDs = excelReadContext.getIntegerList(row, col++, ";");
						if (itemIDs.size() != gdcfgs.classRoles.size())
							throw new Exception("row " + row + " 约战NPC奖励种类 " + itemIDs + " 和职业类型数目不匹配！");
						
						for(int itemID: itemIDs)
						{
							if(itemID != 0)
								this.checkEntityIDValid(gdcfgs, row, itemID, false);
						}
						
						int count = excelReadContext.getIntValue(row, col++);
						if(count > 0)
							cfg.rewards.add(new SBean.ClassTypeReward(itemIDs, count));
    				}
    				
    				row++;
    				col = 0;
    			}
    			
    			gdcfgs.fightNpcGroups = groups;
    		}
    		
    		System.out.println("load table " + fileName + " success.");
    	}
    }
    
    public void loadWizardPetTable(String fileName, GameDataCFGS gdcfgs) throws Exception
    {
    	excelReadContext.ReadNextFile(fileName);
    	{
    		excelReadContext.ReadSheet(0);
    		{
    			int row = 2;
    			int col = 0;
    			
				List<SBean.WizardPetCFGS> groups = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, 0))
				{
					SBean.WizardPetCFGS cfg = new SBean.WizardPetCFGS();
					cfg.petId = excelReadContext.getIntValue(row, col);
					if (cfg.petId != groups.size() + 1)
					{
						throw new Exception("row " + row + " 宠物ID " + cfg.petId + " 不连续!");
					}
					col += 3;
					cfg.channels = excelReadContext.getStringList(row, col++, ";");
					if (cfg.channels.size() == 1)
					{
						if (cfg.channels.get(0).contains(".0"))
							throw new Exception("row " + row + " 渠道ID " + cfg.channels.get(0) + " 未设置为文本格式!");
						if (cfg.channels.get(0).equals("0"))
							cfg.channels = new ArrayList<>();
					}
					cfg.defaultHave = excelReadContext.getByteValue(row, col++);
					cfg.defaultTime = excelReadContext.getIntValue(row, col++);
					cfg.buyCostId = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdcfgs, row, cfg.buyCostId, false);
					cfg.buyCostNum = excelReadContext.getIntValue(row, col++);
					if (cfg.buyCostNum <= 0)
					{
						throw new Exception("row " + row + " 宠物购买道具消耗数量 " + cfg.buyCostNum + " 必须大于0!");
					}
					cfg.buyTime = excelReadContext.getIntValue(row, col++);
					if (cfg.buyTime <= 0)
					{
						throw new Exception("row " + row + " 宠物购买时间 " + cfg.buyTime + " 必须大于0!");
					}
					if (cfg.petId == 1 && (cfg.defaultHave != 1 || cfg.defaultTime != -1))
					{
						throw new Exception("row " + row + " 默认宠物 " + cfg.petId + " 必须为初始持有并且持续时间永久!");
					}
					groups.add(cfg);
					row++;
					col = 0;
				}
    			
    			gdcfgs.wizardPet = groups;
    		}
    		
    		System.out.println("load table " + fileName + " success.");
    	}
    }
    
    public void loadNPCTransfromTable(String fileName, GameDataCFGS gdcfgs) throws Exception
    {
    	excelReadContext.ReadNextFile(fileName);
    	{
    		excelReadContext.ReadSheet(0);
    		{
    			int row = 2;
    			int col = 0;
    			
				List<SBean.NpcTransfromFuncCFGS> groups = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, 0))
				{
					SBean.NpcTransfromFuncCFGS cfg = new SBean.NpcTransfromFuncCFGS();
					cfg.transfromId = excelReadContext.getIntValue(row, col++);
					if (cfg.transfromId != groups.size() + 1)
					{
						throw new Exception("row " + row + " 传送ID " + cfg.transfromId + " 不连续!");
					}
					cfg.mapId = excelReadContext.getIntValue(row, col++);
					checkMapID(gdcfgs, row, cfg.mapId);
					SBean.MapClusterCFGS mcCfg = gdcfgs.mapClusters.get(cfg.mapId);
					cfg.position = excelReadContext.getVector3(row, col++, ";");
					checkMapPosValid(row, mcCfg, cfg.position, "传送位置");
					cfg.line = excelReadContext.getIntValue(row, col++);
					cfg.npcs = excelReadContext.getIntegerList(row, col++, ";", false);
					for (int npc:cfg.npcs)
						checkNPCID(gdcfgs, row, npc);
					int itemId = excelReadContext.getIntValue(row, col++);
					checkEntityIDValid(gdcfgs, row, itemId, true);
					int itemNum = excelReadContext.getIntValue(row, col++);
					if (itemId != 0 && itemNum <= 0)
					{
						throw new Exception("row " + row + " 消耗道具若配置数量必须大于0!");
					}
					cfg.cost = new SBean.DummyGoods(itemId, itemNum);
					cfg.conditions = new ArrayList<>();
					for (int i = 0; i < 4; i++)
					{
						int conditionType = excelReadContext.getIntValue(row, col++);
						int conditionArg = excelReadContext.getIntValue(row, col++);
						switch (conditionType)
						{
						case 0:
							continue;
						case GameData.NPC_TRANSFROM_CONDITION_TYPE_VIP_LEVEL:
						case GameData.NPC_TRANSFROM_CONDITION_TYPE_ROLE_LEVEL:
							break;
						default:
							throw new Exception("row " + row + " 条件类型 " + conditionType + " 无效!");
						}
						cfg.conditions.add(new SBean.Condition(conditionType, conditionArg));
					}
					groups.add(cfg);
					row++;
					col = 0;
				}
    			
    			gdcfgs.npcTransfromFunc = groups;
    		}
    		
    		System.out.println("load table " + fileName + " success.");
    	}
    }
    
    public void loadNPCMapTable(String fileName, GameDataCFGS gdcfgs) throws Exception
    {
    	excelReadContext.ReadNextFile(fileName);
    	{
    		excelReadContext.ReadSheet(0);
    		{
    			int row = 1;
    			int col = 0;
    			
				List<SBean.NpcMapCFGS> groups = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, 0))
				{
					SBean.NpcMapCFGS cfg = new SBean.NpcMapCFGS();
					cfg.npcmapId = excelReadContext.getIntValue(row, col++);
					if (cfg.npcmapId != groups.size() + 1)
					{
						throw new Exception("row " + row + " NPC地图活动ID " + cfg.npcmapId + " 不连续!");
					}
					cfg.openLvl = excelReadContext.getIntValue(row, col++);
					if (cfg.openLvl <= 0)
					{
						throw new Exception("row " + row + " NPC地图活动开启等级 " + cfg.openLvl + " 必须大于0!");
					}
					cfg.dayEnterTime = excelReadContext.getIntValue(row, col++);
					if (cfg.dayEnterTime <= 0)
					{
						throw new Exception("row " + row + " NPC地图活动每日参与次数 " + cfg.dayEnterTime + " 必须大于0!");
					}
					cfg.opendays = excelReadContext.getIntegerList(row, col++, ";").stream().collect(Collectors.toSet());
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					cfg.mapstartDay = GameTime.getDay(GameTime.getTime(format.parse(excelReadContext.getStringValue(row, col++))));
					cfg.mapstartTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					cfg.mapendDay = GameTime.getDay(GameTime.getTime(format.parse(excelReadContext.getStringValue(row, col++))));
					cfg.mapendTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row, col++));
					if (cfg.mapendTime <= cfg.mapstartTime)
					{
						throw new Exception("row " + row + " NPC地图活动结束时间必须大于开始时间!");
					}
					groups.add(cfg);
					row++;
					col = 0;
				}
    			
    			gdcfgs.npcMaps = groups;
    		}
			
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 1;
				final int colStart = 0;
				int row = rowStart;
				int col = colStart;
				
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					int npcmapId = excelReadContext.getIntValue(row, col++);
					if (npcmapId <= 0 || npcmapId > gdcfgs.npcMaps.size())
					{
						throw new Exception("row " + row + " NPC地图活动ID " + npcmapId + " 不存在!");
					}
					SBean.NpcMapCFGS cfg = gdcfgs.npcMaps.get(npcmapId - 1);
					if (cfg.level2mapId == null)
						cfg.level2mapId = new ArrayList<>();
					SBean.LevelToMapIdCFGS l2mCfgs = new SBean.LevelToMapIdCFGS();
					l2mCfgs.level = excelReadContext.getIntValue(row, col++);
					if (l2mCfgs.level < 0 || (cfg.level2mapId.size() > 0 && l2mCfgs.level <= cfg.level2mapId.get(cfg.level2mapId.size() - 1).level))
						throw new Exception("正义之心 row " + row + " 等级 " + l2mCfgs.level + " 非法！");
					l2mCfgs.mapId = excelReadContext.getIntValue(row, col++);
					checkMapID(gdcfgs, row, l2mCfgs.mapId);
					cfg.level2mapId.add(l2mCfgs);
					col = colStart;
					row++;
				}
			}
    		
    		System.out.println("load table " + fileName + " success.");
    	}
    }
    
    public void loadNPCPrayTable(String fileName, GameDataCFGS gdcfgs) throws Exception
    {
    	excelReadContext.ReadNextFile(fileName);
    	{
			excelReadContext.ReadSheet(1);
			{
				final int rowStart = 2;
				final int colStart = 1;
				int row = rowStart;
				int col = colStart;

				List<SBean.PrayDropCFGS> groups = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, colStart))
				{
					SBean.PrayDropCFGS cfg = new SBean.PrayDropCFGS();
					cfg.prayDropId = excelReadContext.getIntValue(row, col);
					if (cfg.prayDropId != groups.size() + 1)
					{
						throw new Exception("row " + row + " 祈福掉落ID " + cfg.prayDropId + " 不连续!");
					}
					col += 3;
					cfg.randomDropId = excelReadContext.getIntValue(row, col++);
					if (!gdcfgs.randomDropTbl.containsKey(cfg.randomDropId))
					{
						throw new Exception("row " + row + " 祈福掉落随机掉落ID " + cfg.randomDropId + " 不存在!");
					}
					groups.add(cfg);
					col = colStart;
					row+=3;
				}
				gdcfgs.prayDrops = groups;
			}
			
    		excelReadContext.ReadSheet(0);
    		{
    			int row = 2;
    			int col = 0;
    			
				List<SBean.NpcPrayCFGS> groups = new ArrayList<>();
				while (excelReadContext.isNotEmpty(row, 0))
				{
					SBean.NpcPrayCFGS cfg = new SBean.NpcPrayCFGS();
					cfg.prayId = excelReadContext.getIntValue(row, col);
					if (cfg.prayId != groups.size() + 1)
					{
						throw new Exception("row " + row + " NPC祈福活动ID " + cfg.prayId + " 不连续!");
					}
					col += 3;
					cfg.levelReq = excelReadContext.getIntValue(row, col++);
					if (cfg.levelReq <= 0)
					{
						throw new Exception("row " + row + " NPC祈福活动等级限制 " + cfg.levelReq + " 必须大于0!");
					}
					cfg.dayJoinTime = excelReadContext.getIntValue(row, col++);
					if (cfg.dayJoinTime <= 0)
					{
						throw new Exception("row " + row + " NPC祈福活动每日参与次数 " + cfg.dayJoinTime + " 必须大于0!");
					}
					cfg.costs = new ArrayList<>();
					for (int i = 0; i < 3; i++)
					{
						int itemId = excelReadContext.getIntValue(row, col++);
						checkEntityIDValid(gdcfgs, row, itemId, true);
						int itemCount = excelReadContext.getIntValue(row, col++);
						if (itemId != 0)
						{
							cfg.costs.add(new SBean.DummyGoods(itemId, itemCount));
						}
					}
					SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					cfg.startTime = GameTime.getTime(format.parse(excelReadContext.getStringValue(row, col++)));
					cfg.endTime = GameTime.getTime(format.parse(excelReadContext.getStringValue(row, col++)));
					if (cfg.endTime <= cfg.startTime)
					{
						throw new Exception("row " + row + " NPC祈福活动结束时间必须大于开始时间!");
					}
					cfg.prayDrops = excelReadContext.getIntegerList(row, col++, ";");
					for(int drop : cfg.prayDrops)
						if (drop <= 0 || drop > gdcfgs.prayDrops.size())
							throw new Exception("row " + row + " NPC祈福掉落祈福掉落ID " + drop + " 不存在!");
					groups.add(cfg);
					row++;
					col = 0;
				}
    			
    			gdcfgs.npcPrays = groups;
    		}
    		
    		System.out.println("load table " + fileName + " success.");
    	}
    }
    
    public void loadMapSkillTable(String fileName, GameDataCFGS gdcfgs) throws Exception
    {
    	excelReadContext.ReadNextFile(fileName);
    	{
    		excelReadContext.ReadSheet(0);
    		{
    			int row = 2;
    			int col = 0;
    			
    			List<SBean.MapSkillCFGS> groups = new ArrayList<>();
    			while (excelReadContext.isNotEmpty(row, 0))
    			{
    				SBean.MapSkillCFGS cfg = new SBean.MapSkillCFGS(0, 0, new HashMap<>());
    				int skillGroupId = excelReadContext.getIntValue(row, col++);
    				if (skillGroupId != groups.size() + 1)
    				{
    					throw new Exception("row " + row + " 副本专有技能ID " + skillGroupId + " 不连续!");
    				}
    				cfg.isCommonCD = excelReadContext.getIntValue(row, col++);
    				cfg.commonCD = excelReadContext.getIntValue(row, col++);
    				for (int i = 0; i < 4; i++)
    				{
    					int skillId = excelReadContext.getIntValue(row, col++);
    					int skillLevel = excelReadContext.getIntValue(row, col++);
    					checkSkillIDAndLevelValid(gdcfgs, row, skillId, skillLevel);
    					int skillTime = excelReadContext.getIntValue(row, col++);
    					if (skillId != 0)
    					{
    						cfg.skills.put(skillId, new SBean.MapSkillInstance(skillId, skillLevel, skillTime));
    					}
    				}
    				groups.add(cfg);
    				row++;
    				col = 0;
    			}
    			
    			gdcfgs.mapSkills = groups;
    		}
    		
    		System.out.println("load table " + fileName + " success.");
    	}
    }
	
    public void loadTowerDefenceTable(String fileName, GameDataCFGS gdcfgs) throws Exception
    {
    	excelReadContext.ReadNextFile(fileName);
    	{
    		gdcfgs.towerDefences = new HashMap<>();
    		for(int i = 0; i < 2; i++)
    		{
    			SBean.TowerDefenceCFGS cfg = new SBean.TowerDefenceCFGS();
    			excelReadContext.ReadSheet(i);
    			{
    				int row = 0;
					int col = 1;
					
					int mapID = excelReadContext.getIntValue(row++, col);
					SBean.MapClusterCFGS mapclusterCfg = gdcfgs.mapClusters.get(mapID);
					if(mapclusterCfg == null)
						throw new Exception("row " + row + " 地图ID " + mapID + " 非法!");
					
					int lvlReq = excelReadContext.getIntValue(row++, col);
					int dayEnterTimes = excelReadContext.getIntValue(row++, col);
					Set<Integer> openDays = new HashSet<>(excelReadContext.getIntegerList(row++, col, ";"));
					int startDate = GameTime.parseDate(excelReadContext.getStringValue(row++, col));
					int endDate = GameTime.parseDate(excelReadContext.getStringValue(row++, col));
					if(startDate > endDate)
						throw new Exception("row " + row + " 开放日期 " + startDate + " 小于结束日期 " + endDate);

					int startTime = GameTime.parseSecondOfDay(excelReadContext.getStringValue(row++, col));
					int lastTime = excelReadContext.getIntValue(row++, col);
					
					if(lastTime + (startTime - GameData.GAME_DAY_REFRESH_TIME * 3600) > GameTime.getDayTimeSpan())
						throw new Exception("row " + row + " 持续时间 " + lastTime + " 非法！");
					
					int protectID = excelReadContext.getIntValue(row++, col);
					checkMonsterID(gdcfgs, row, protectID);
					SBean.Location location = new SBean.Location(excelReadContext.getVector3(row++, col, ";"), excelReadContext.getVector3F(row++, col, ";"));
					checkMapPosValid(row, mapclusterCfg, location.position, "守护泫勃派 npc");
					row++;
					List<Float> popReqs = new ArrayList<>();
					List<Float> lst = excelReadContext.getFloatList(row++, col, ";");
					float lastReq = 1.0f;
					for(float req: lst)
					{
						req /= 10000.f;
						if(req >= lastReq)
							throw new Exception("row " + row + " NPC喊话血量 " + req + " 不是递减的!");
						
						lastReq = req;
						if(req > 0)
							popReqs.add(req);
					}
					cfg.base = new SBean.TowerDefenceBaseCFGS(mapID, lvlReq, dayEnterTimes, openDays, startDate, endDate, startTime, lastTime, protectID, location, popReqs);
					int rowMapReward = excelReadContext.locateColumnTag(0, "怪物波数");
					{
						row = rowMapReward + 2;
						col = 0;
						
						int lastFloor = -1;
						cfg.mapRewards = new ArrayList<>();
						while(excelReadContext.isNotEmpty(row, col))
						{
							int floor = excelReadContext.getIntValue(row, col++);
							if(floor <= lastFloor)
								throw new Exception("row " + row + " 怪物波数 " + floor + " 不是递增的!");
							
							lastFloor = floor;
							List<SBean.DummyGoods> reward = new ArrayList<>();
							while(excelReadContext.isNotEmpty(row, col))
							{
								int itemID = excelReadContext.getIntValue(row, col++);
								if(itemID != 0)
									checkEntityIDValid(gdcfgs, row, itemID, false);
								int count = excelReadContext.getIntValue(row, col++);
								if(count > 0)
									reward.add(new SBean.DummyGoods(itemID, count));
							}
							
							cfg.mapRewards.add(new SBean.FloorReward(floor, reward));
							
							row++;
							col = 0;
						}
					}
					
					int rowRankReward = excelReadContext.locateColumnTag(0, "奖励设定");
					{
						row = rowRankReward + 2;
						col = 1;
						
						int lastRank = 0;
						cfg.rankRewards = new ArrayList<>();
						while(excelReadContext.isNotEmpty(row, col))
						{
							int rank = excelReadContext.getIntValue(row, col++);
							if(rank <= lastRank)
								throw new Exception("row " + row + " 排名 " + rank + " 不是递增的!");
							
							lastRank = rank;
							List<SBean.DummyGoods> reward = new ArrayList<>();
							while(excelReadContext.isNotEmpty(row, col))
							{
								int itemID = excelReadContext.getIntValue(row, col++);
								if(itemID != 0)
									checkEntityIDValid(gdcfgs, row, itemID, false);
								int count = excelReadContext.getIntValue(row, col++);
								if(count > 0)
									reward.add(new SBean.DummyGoods(itemID, count));
							}
							
							cfg.rankRewards.add(new SBean.FloorReward(rank, reward));
							row++;
							col = 1;
						}
					}
					
					int rowJoinReward = excelReadContext.locateColumnTag(0, "参与奖");
					{
						row = rowJoinReward + 2;
						col = 2;
						
						cfg.joinReward = new ArrayList<>();
						while(excelReadContext.isNotEmpty(row, col))
						{
							int itemID = excelReadContext.getIntValue(row, col++);
							if(itemID != 0)
								checkEntityIDValid(gdcfgs, row, itemID, false);
							int count = excelReadContext.getIntValue(row, col++);
							if(count > 0)
								cfg.joinReward.add(new SBean.DummyGoods(itemID, count));
						}
					}
					
					if(gdcfgs.towerDefences.put(cfg.base.mapID, cfg) != null)
						throw new Exception("守护活动 " + cfg.base.mapID + " 重复");
    			}
    		}
    		
    		System.out.println("load table " + fileName + " success.");
    	}
    }
    
	private void initPerformanceSpawns(GameDataCFGS gdcfgs) throws Exception
	{
		int birthMap = gdcfgs.classRoles.get(0).spawnPosition.mapID;
		gdcfgs.performanceSpawns = new ArrayList<>();
		for (int npc : gdcfgs.mapClusters.get(birthMap).npcs)
		{
			SBean.NpcPointCFGS npcPoint = gdcfgs.npcPoints.get(npc);
			if (npcPoint != null)
				gdcfgs.performanceSpawns.add(new SBean.MapLocation(birthMap, new SBean.Location(npcPoint.position, npcPoint.rotation)));
		}
		for (int point : gdcfgs.mapClusters.get(birthMap).spawnPoints)
		{
			SBean.SpawnPointCFGS spawnPoint = gdcfgs.spawnPoints.get(point);
			if (spawnPoint != null)
				gdcfgs.performanceSpawns.add(new SBean.MapLocation(birthMap, new SBean.Location(spawnPoint.position, spawnPoint.rotation)));
		}
		System.out.println("init performance spawns success.");
	}
	
	public static void main(String[] args)
	{
		ArgsMap am = new ArgsMap(args);
		String srcdir = am.get("-srcdir", ".");
		String dstdir = am.get("-dstdir", ".");
		String server_dstdir = am.get("-server_dstdir", ".");
		DataTool tool = new DataTool(srcdir);
		SBean.GameDataCFGS cfgs = new SBean.GameDataCFGS();
		SBean.GameDataCFGT cfgt = new SBean.GameDataCFGT();
		try
		{
			//tool.loadConsumeTable("单一消耗表", cfgs);
			tool.loadModelTable("模型资源表", cfgs);
			tool.loadDialogTable("对话表", cfgs);
			tool.loadAnimationTable("剧情动画表", cfgs);
			tool.loadBattleTable("战役地图表", cfgs);
			tool.loaEffectTable("特效表", cfgs);
			tool.loadHeadIconTable("头像资源表", cfgs);
			tool.loadPropertiesTable("属性ID对应表格", cfgs);
			tool.loadStateTable("状态效果ID表", cfgs);
			tool.loadAiTrigTable("ai触发公式", cfgs);
			tool.loadBaseBuffTable("buff基本表", cfgs);
			tool.loadMapBuffTable("场景BUFF表", cfgs);
			tool.loadAuctionTable("寄售行", cfgs);
			tool.loadMapCopySpawnPaths("副本寻路点", cfgs);
			
			tool.loadBaseDummyItemTable("基础物品属性表", cfgs);
			tool.loadEquipRefineTables("装备精炼表", cfgs);
			tool.loadItemTables("道具表", cfgs);
			tool.loadGemTables("宝石表", cfgs);
			tool.loadEquipTables("装备表", cfgs);
			tool.loadBookTables("心法书", cfgs);
			tool.loadLegendEquipTables("传世装备", cfgs);
			
			tool.loadGiftTable("礼包表", cfgs);
			tool.loadCheckInTable("签到表", cfgs);
			
			tool.loadDropTables("掉落表", cfgs);
			
			tool.loadBaseSkillTables("技能基础表", cfgs);
							
			tool.loadMonsterTables("怪物表", cfgs);
			tool.loadNpcTables("NPC表", cfgs);
			tool.loadMineral("采矿表", cfgs);
			tool.loadTrapExpanded("Trap基本表", cfgs);
			
			tool.loadSkillSpecial("技能特化公式", cfgs);
			tool.loadUniqueSkill("绝技系统", cfgs);
			tool.loadSpiritEffect("心法效果表", cfgs);
			tool.loadSpiritTable("心法配置表", cfgs);
			
			tool.loadActivityTables("活动表", cfgs);
			tool.loadDemonHoleTables("伏魔洞", cfgs);
			tool.loadMapTables("副本配置表", cfgs);
			tool.loadBetaActivityTable("封测运营活动", cfgs);
			tool.loadQuestionsBankTable("题库", cfgs);
			
			tool.loadClassRoleTables("武将表", cfgs);

			tool.loadTaskSpecialTable("任务特殊逻辑", cfgs);
			tool.loadSceneTrig("场景触发配置表", cfgs);
			tool.loadTaskTable("任务表", cfgs);
			
			tool.loadPetTable("佣兵表", cfgs);
			tool.loadUpLvlExp("角色等级表", cfgs);
			tool.loadEquipToughen("装备强化表", cfgs);
			tool.loadEquipPurgatory("装备洗练表", cfgs);
			tool.loadEquipUpStar("装备升星表", cfgs);
			
			tool.loadEquipSlotTable("装备部位表", cfgs);
			tool.loadPropAward("属性奖励通用表", cfgs);

			tool.loadHorseTable("坐骑表", cfgs);
			tool.loadWeaponTable("神兵配置表", cfgs);
			
			tool.loadTransformTable("转职表", cfgs);
			tool.loadSuiteTable("套装表", cfgs);
			
			tool.loadTitleTable("称号系统", cfgs);
			tool.loadSectTable("帮派配置表", cfgs);
			tool.loadShopTable("个人商店表", cfgs);
			tool.loadFightSPTable("战斗能量", cfgs);
			tool.loadChallengeTaskTable("挑战任务", cfgs);
			tool.loadFameTable("名望系统", cfgs);
			tool.loadOtherActivityTable("小活动", cfgs);
			tool.loadRankTable("排行榜", cfgs);
			tool.loadDIYSkillTable("自创武功", cfgs);
			tool.loadChannelPaysTable("渠道充值配置", cfgs);
			tool.loadVipCardsTable("特权卡", cfgs);
			tool.loadProduceTable("生产配置表", cfgs);
			tool.loadClanTable("宗门配置表", cfgs);
			tool.loadInvalidStrTable("非法字符表", cfgs);
			tool.loadArenaTable("个人竞技场", cfgs);
			tool.loadSuperArenaTable("会武", cfgs);
			tool.loadBWArenaTable("正邪道场", cfgs);
			tool.loadForceWarTable("势力战", cfgs);
			tool.loadRandomNameTable("随机名字表", cfgs);
			tool.loadPKTable("PK系统", cfgs);
			tool.loadFightFactor("战斗公式系数", cfgs);

			tool.loadStoreTable("杂货店", cfgs);
			tool.loadTreasureTable("藏宝图配置表", cfgs);
			
			tool.loadFriedGiveRewardTable("好友受赠奖励", cfgs);
			//tool.loadFriedHeadTable("好友头像设置表", cfgs);
			
			tool.loadFashionTable("时装表", cfgs);
			tool.loadSocialActionTable("社交动作表", cfgs);
			tool.loadSealTable("龙印配置", cfgs);
			tool.loadLeadTable("引导表", cfgs);
			tool.loadExpCoinTable("历练配置", cfgs);
			tool.loadMainTable("通用配置表", cfgs);
			tool.loadVipsTable("VIP表", cfgs);
			
			tool.loadClimbTowerTable("爬塔系统", cfgs);
			tool.loadPetAchieve("随从成就系统", cfgs);
			tool.loadSectDeliverTable("帮派运镖", cfgs);
			tool.loadRollNoticeTable("走马灯配置", cfgs);
			tool.loadDailyTaskTable("日常任务表", cfgs);
			tool.loadRemainActivity("七日留存活动", cfgs);
			tool.loadComposeTable("合成逻辑表", cfgs);
			tool.loadMessageBoardTable("留言板配置表", cfgs);
			tool.loadArmorTable("内甲系统", cfgs);
			tool.loadMarriageTable("结婚系统", cfgs);
			tool.loadExchangeTable("物物兑换表", cfgs);
			tool.loadFlagBattleTable("帮派夺旗战", cfgs);
			tool.loadHeirloomTable("传家宝", cfgs);
			tool.loadGambleShopTable("赌博商店表", cfgs);
			tool.loadRobotTable("机器人配置表", cfgs);
			tool.initPerformanceSpawns(cfgs);
			tool.loadFlashSaleResourceTable("限时特卖广告资源图", cfgs);			
			tool.loadAdversResourceTable("运营时装背景资源图", cfgs);			
			tool.loadActivityLastTable("试炼补做", cfgs);	
			tool.loadSteleTable("太玄碑文", cfgs);
			tool.loadJusticeMapTable("正义之心", cfgs);
			tool.loadEmergencyTable("江湖告急", cfgs);
			tool.loadLucklyStarTable("幸运星", cfgs);
			tool.loadMasterTable("师徒系统", cfgs);
			tool.loadFightNpcTable("约战表", cfgs);
			tool.loadWizardPetTable("休闲宠物", cfgs);
			tool.loadNPCTransfromTable("NPC传送表", cfgs);
			tool.loadNPCMapTable("NPC副本配表", cfgs);
			tool.loadTowerDefenceTable("守护活动配置", cfgs);
			tool.loadNPCPrayTable("祈福活动", cfgs);		
			tool.loadMapSkillTable("副本专有技能", cfgs);		
			
			tool.loadScheduleTable("日程表", cfgs);
			tool.checkAllCFGS(cfgs);
			if (am.containsKey("check"))
			{
				System.out.println("数据检查成功");
				return;
			}

			File server_ofile = new File(server_dstdir + "/server_cfg.dat");
			Stream.storeObjLE(cfgs, server_ofile);
			System.out.println("output server data cfg to " + server_ofile.getAbsolutePath());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.err.println("数据异常.");
			System.exit(-1);
		}
		
		
	}



    ExcelReadContext excelReadContext;

	Set<Integer> dialogIds = new TreeSet<>();
	Set<Integer> modelIds = new TreeSet<>();

	Map<Integer, SBean.PropAwardCFGS> propAwardMap = new TreeMap<>();
	Map<Integer, List<SBean.SectSkillCFGS>> sectIdToSkill = new TreeMap<>();

	Map<Integer, SBean.FightSPCFGS> fightSPIDMap = new TreeMap<>();
	Map<Integer, SBean.BlurCFGS> blurIDMap = new TreeMap<>();

	Map<Integer, SBean.DIYSkillBaseCFGS> diySkillMap = new TreeMap<>();
	Map<Integer, SBean.DIYSkillGradeCFGS> diySkillGradeMap = new TreeMap<>();
	Map<Integer, SBean.DIYSkillActionCFGS> diySkillActionMap = new TreeMap<>();

	Map<Integer, SBean.ArenaRobotCFGS> arenaRobotIDMap = new TreeMap<>();
	Map<Integer, SBean.ClanTaskGroupCFGS> clanTaskGroup = new TreeMap<>();
	Map<Integer, Integer> area2map = new HashMap<>();
	Map<Integer, Integer> point2map = new HashMap<>();
	Map<Integer, Integer> npc2map = new HashMap<>();
	Map<Integer, Integer> mineral2map = new HashMap<>();
	Map<Integer, Integer> wayPoint2map = new HashMap<>();
	Map<Integer, Integer> mapbuff2map = new HashMap<>();
	Set<Integer> animation = new HashSet<>();
	Set<Integer> effectIDs = new HashSet<>();
	Set<Integer> senceTrigIDs = new HashSet<>();
	Set<Integer> playerRepalceActions = new HashSet<>();
	Set<Integer> npcRepalceActions = new HashSet<>();
	Set<Integer> effectPlayIDs = new HashSet<>();
	Set<Integer> openUIIDs = new HashSet<>();
	
	Map<Integer, SBean.RandQuestionCFGS> randQuestions = new HashMap<>();
	
	Map<Integer, Integer> taskDrops = new HashMap<>();
	private Map<Integer, Integer> battleMapPackets = new HashMap<>();
	private Map<Integer, SBean.MapCopySpawnPath> allSpawnPaths = new HashMap<>();
	private Map<Integer, Integer> pay2SpecialCard = new HashMap<>();
}
