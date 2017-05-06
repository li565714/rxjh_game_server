package i3k.gtool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import i3k.SBean;
import ket.moi.ExcelReader;
import ket.moi.ExcelSheet;
import ket.moi.ReaderException;
import ket.util.ArgsMap;
import ket.util.Stream;

public class GMData
{
	public static void main(String[] args) throws ReaderException, IOException
	{
		SBean.itemIdNameMapCFGS itemMap = new SBean.itemIdNameMapCFGS(new HashMap<Integer, String>());
		ArgsMap am = new ArgsMap(args);
		String srcdir = am.get("-srcdir", ".");
		String server_dstdir = am.get("-server_dstdir", ".");
		readExcel(srcdir + "/基础物品属性表", itemMap);
		readExcel(srcdir + "/道具表", itemMap);
		readExcel(srcdir + "/宝石表", itemMap);
		readExcel(srcdir + "/装备表", itemMap);
		readExcel(srcdir + "/心法书", itemMap);
		File item_idname = new File(server_dstdir + "/item_idname.dat");
		Stream.storeObjLE(itemMap, item_idname);
		System.out.println("output server data cfg to " + item_idname.getAbsolutePath());
	}
	
	private static void readExcel(String filename,SBean.itemIdNameMapCFGS itemMap) throws ReaderException{
		ExcelReader reader = ket.moi.Factory.newExcelReader(filename
				+ ".xlsx");
		ExcelSheet sheet = reader.getSheet(0);
		int row = 2;
		int col = 0;
		while (sheet.isNotEmpty(row, col)) {
			itemMap.datamap.put(sheet.getIntValue(row, col),
					sheet.getStringValue(row, col + 1));
			row++;
		}
	}
}
