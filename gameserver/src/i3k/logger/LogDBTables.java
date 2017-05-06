
package i3k.logger;

import i3k.util.XmlElement;

import java.util.regex.Pattern;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

import org.apache.log4j.Logger;

public class LogDBTables
{
	static class DBTableMultiRow
	{
		static final String regexplogMultiRowDelimiter = Pattern.quote(",");
		static final String regexplogMultiRowColDelimiter = Pattern.quote("_");
		public String tableName;
		public List<Integer> copyRowsHead;
		public List<Integer> copyRowsTail;
		DBTableMultiRow(String tableName, List<Integer> copyRowsHead, List<Integer> copyRowsTail)
		{
			this.tableName = tableName;
			this.copyRowsHead = copyRowsHead;
			this.copyRowsTail = copyRowsTail;
		}
		
		String getMultiRowTblSqlFromLog(String[] cols, int multiRowTblColSeq)
		{
			if (multiRowTblColSeq > cols.length)
				return null;
			String multiRowColStr = cols[multiRowTblColSeq];
			if(multiRowColStr.isEmpty())
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append("insert into ").append(tableName).append(" values");
			StringBuilder sbhead = new StringBuilder();
			sbhead.append("(");
			for (int seq : copyRowsHead)
			{
				sbhead.append("'").append(cols[seq].replace("'", "\\'")).append("'").append(", ");
			}
			StringBuilder sbtail = new StringBuilder();
			for (int seq : copyRowsTail)
			{
				sbtail.append(", ").append("'").append(cols[seq].replace("'", "\\'")).append("'");
			}
			sbtail.append(")");
			String[] multiRows = multiRowColStr.split(regexplogMultiRowDelimiter, -1);
			for (int r = 0; r < multiRows.length; ++r)
			{
				StringBuilder sbrow = new StringBuilder();
				sbrow.append(sbhead);
				String[] multiRowCols =  multiRows[r].split(regexplogMultiRowColDelimiter, -1);
				for (int c = 0; c < multiRowCols.length; ++c)
				{
					if (c != 0)
						sbrow.append(", ");
					sbrow.append("'").append(multiRowCols[c].replace("'", "\\'")).append("'");
				}
				sbrow.append(sbtail);
				if (r != 0)
					sb.append(", ");
				sb.append(sbrow);
			}
			return sb.toString();
		}
		
		static DBTableMultiRow tryCreate(String multiRowStr)
		{
			String[] strs = multiRowStr.split(regexplogMultiRowDelimiter, -1);
			if (strs.length != 3)
				return null;
			String[] colsHead = strs[1].isEmpty() ? new String[0] : strs[1].split(regexplogMultiRowColDelimiter, -1);
			String[] colsTail = strs[2].isEmpty() ? new String[0] : strs[2].split(regexplogMultiRowColDelimiter, -1);
			List<Integer> copyRowsHead = new ArrayList<Integer>();
			List<Integer> copyRowsTail = new ArrayList<Integer>();
			for (int i = 0; i < colsHead.length; ++i)
			{
				try
				{
					copyRowsHead.add(Integer.valueOf(colsHead[i]));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return null;
				}
			}
			for (int i = 0; i < colsTail.length; ++i)
			{
				try
				{
					copyRowsTail.add(Integer.valueOf(colsTail[i]));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return null;
				}
			}
			return new DBTableMultiRow(strs[0], copyRowsHead, copyRowsTail);
		}
	}
	static class DBTableDefinition
	{
		public static final String logColDelimiter = "|";
		static final String regexplogColDelimiter = Pattern.quote(logColDelimiter);
		public String name;
		public Map<Integer, DBTableMultiRow> multiRowTbls = new HashMap<>();
		
		public DBTableDefinition(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
		public void setMultiRowTbl(int colSeq, String multiRowStr)
		{
			DBTableMultiRow dmr = DBTableMultiRow.tryCreate(multiRowStr);
			if (dmr != null)
				this.multiRowTbls.put(colSeq, dmr);
		}
		
		public Collection<String> convertToSqls(String log)
		{
			String[] cols = log.split(regexplogColDelimiter, -1);
			if (cols.length < 2)
				return null;
			Collection<String> sqls = getMultiRowTblSqlFromLog(cols);
			if (sqls == null)
				sqls = new ArrayList<>();
			sqls.add(getRawTblSqlFromLog(cols));
			return sqls;
		}
		
		
		Collection<String> getMultiRowTblSqlFromLog(String[] cols)
		{
			if (!multiRowTbls.isEmpty())
			{
				List<String> sqls = new ArrayList<>();
				for (Map.Entry<Integer, DBTableMultiRow> e : multiRowTbls.entrySet())
				{
					String sql = e.getValue().getMultiRowTblSqlFromLog(cols,  e.getKey());
					if (sql != null)
						sqls.add(sql);
				}
				return sqls;
			}
			return null;
		}
		
		static String getRawTblSqlFromLog(String[] cols)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("insert into ").append(cols[0]).append(" values(");
			for (int i = 1; i < cols.length; ++i)
			{
				if (i != 1)
					sb.append(", ");
				sb.append("'").append(cols[i].replace("'", "\\'")).append("'");
			}
			sb.append(");");	
			return sb.toString();
		}
		
	}
	
	public LogDBTables(LogServer ls)
	{
		this.ls = ls;
	}
	
	public Collection<String> getLogSqls(String log)
	{
		return getLogSqls(allDBTables, log);
	}
	
	public static Collection<String> getLogSqls(Map<String, DBTableDefinition> allDBTables, String log)
	{
		int index = log.indexOf(DBTableDefinition.logColDelimiter);
		if (index < 0)
			return null;
		String tblname = log.substring(0, index);
		DBTableDefinition tbldef = allDBTables.get(tblname);
		if (tbldef == null)
			return null;
		return tbldef.convertToSqls(log);
	}
	
	public boolean start()
	{
		ls.getResourceManager().addWatch(ls.getConfig().tlogCfgFileName, filePath -> {
			Map<String, DBTableDefinition> tables = loadConfigs(filePath);
			if (tables != null)
				allDBTables = tables;
            });
		ls.getLogger().info("load dababase table config file(" + ls.getConfig().tlogCfgFileName +") " + (allDBTables != null ? " ok" : " failed")+ "!");
		return allDBTables != null;
	}
	
	public Map<String, DBTableDefinition> loadConfigs(String filePath)
	{
		return loadConfigs(filePath, ls.getLogger());
	}
	
	public static Map<String, DBTableDefinition> loadConfigs(String filePath, Logger logger)
	{
		logger.info("try load db config file " + filePath + " ...");
		try
		{
			XmlElement root = XmlElement.parseXml(filePath);
			Map<String, DBTableDefinition> cfgs = parseConfigs(root);
			logger.info("load db config file success.");
			return cfgs;
		}
		catch (Throwable t)
		{
			logger.warn("load config file failed !!!", t);
		}
		return null;
	}
	
	private static Map<String, DBTableDefinition> parseConfigs(XmlElement root) throws Exception
	{
		Map<String, DBTableDefinition> dbtables = new HashMap<>();
		if (root.getName().equals("metalib"))
		{
			for (XmlElement e : root.getChildrenByName("struct"))
			{
				String tblname = e.getStringAttribute("name");
				if (!tblname.isEmpty())
				{
					DBTableDefinition tbl = new DBTableDefinition(tblname);
					int colSeq = 1;
					for (XmlElement ee : e.getChildrenByName("entry"))
					{
						String multitblname = ee.getStringAttribute("multirowtbl", "");
						if (!multitblname.isEmpty())
						{
							tbl.setMultiRowTbl(colSeq, multitblname);
						}
						++colSeq;
					}
					dbtables.put(tblname, tbl);
				}
			}
		}
		return dbtables;
	}
	
	public static void convertLogsToSql(Map<String, DBTableDefinition> allDBTables, String logfile, String sqlfile)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logfile), "utf-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sqlfile), "utf-8"));
			try
			{
				writer.write("use tlog;\n");
				writer.write("SET collation_connection = utf8_bin;\n");
				writer.write("SET character_set_client = utf8;\n");
	        	writer.write("SET character_set_connection = utf8;\n");
	        	writer.write("SET autocommit=0;\n");
	        	
				for (String line = reader.readLine(); line != null; line = reader.readLine()) 
				{
					Collection<String> sqls = getLogSqls(allDBTables, line.trim());
					if (sqls == null)
					{
						System.err.println("try create log sql failed: \t" + line.trim());
						continue;
					}
					for (String sql : sqls)
					{
						writer.write(sql + "\n");
					}
				}
				writer.write("SET autocommit=1;\n");
				writer.flush();
			}
			finally
			{
				reader.close();
				writer.close();
				System.out.println("create log sql file(" + sqlfile + ") end .");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		{
			org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
			ca.setName("AA");
			ca.setWriter(new java.io.PrintWriter(System.out));
			ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
			Logger.getRootLogger().addAppender(ca);
		}
		if (args.length < 3)
		{
			System.err.println("must be more than 3 arguments : convert cfgxml  outputdir files ...");
			return;
		}
		
		String cfgxml = args[0];
		Map<String, DBTableDefinition> dbtables = LogDBTables.loadConfigs(cfgxml, Logger.getRootLogger());
		if (dbtables == null)
		{
			System.err.println("LogDBTables load cfgxml(" + cfgxml + ") failed ! ");
			return;
		}
		String outputdir = args[1];
    	File file = new File(outputdir);
    	if (!file.exists())
    		file.mkdirs();
		for (int i = 2; i < args.length; ++i)
		{
			try
			{
				String srcFilePath = args[i];
				File srcFile = new File(srcFilePath);
				if (!srcFile.exists())
				{
					System.err.println("log file(" + srcFile + ") is not exist ! ");	
					break;
				}
				String parentFilePath = srcFile.getCanonicalFile().getParent();
				String srcFileName = srcFile.getCanonicalPath().substring(parentFilePath.length());
				String dstFilePath = outputdir + srcFileName + ".sql";
				System.out.println("log db try convert src file(" + srcFilePath + ") to dst file(" + dstFilePath +")");
				LogDBTables.convertLogsToSql(dbtables, srcFilePath, dstFilePath);				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	LogServer ls;
	private Map<String, DBTableDefinition> allDBTables;
}
