
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

import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

public class LogDBCreater
{
	public static void main(String[] args)
	{
		String filePath = "qsg_tlog.xml";
		String sqlFile = "tlog.sql";
		String dbname = "tlog";
		if (args.length > 0)
			filePath = args[0];
		if (args.length > 1)
			sqlFile = args[1];
		if (args.length > 2)
			dbname = args[2];
		Collection<String> dbtables = readDBConfigs(filePath);
		if (dbtables != null)
		{
			try
			{
				Writer sqlwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sqlFile), "UTF-8"));
				try
				{
					sqlwriter.write("CREATE DATABASE IF NOT EXISTS " + dbname + " DEFAULT CHARACTER SET utf8;\n\n");
					sqlwriter.write("USE " + dbname + ";\n\n");
					for (String tbl : dbtables)
					{
						sqlwriter.write(tbl);
					}
					sqlwriter.flush();
					System.out.println("create sql file (" + sqlFile + ") success.");	
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					sqlwriter.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static Collection<String> readDBConfigs(String filePath)
	{
		System.out.println("read db config file " + filePath + " ...");
		try
		{
			XmlElement root = XmlElement.parseXml(filePath);
			Collection<String> cfgs = parseDBTableConfigs(root);
			System.out.println("read db config file success.");
			return cfgs;
		}
		catch (Throwable t)
		{
			System.err.println("read config file failed !!!");
			t.printStackTrace();
		}
		return null;
	}
	
	private static Collection<String> parseDBTableConfigs(XmlElement root) throws Exception
	{
		List<String> dbtables = new ArrayList<>();
		if (root.getName().equals("metalib"))
		{
			String partitionStatement = getDBPartitionStatement();
			for (XmlElement e : root.getChildrenByName("struct"))
			{
				dbtables.add(parseDBTable(e, partitionStatement));
			}
		}
		return dbtables;
	}
	
	private static String parseDBTable(XmlElement root, String partitionStatement) throws Exception
	{
		String tblname = root.getStringAttribute("name");
		if (tblname.isEmpty())
			throw new Exception("data base table name is empty string! ");
		String indexStr = root.getStringAttribute("index", "");
		String[] index = indexStr.isEmpty() ? null : indexStr.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE IF EXISTS " + tblname + ";\n");
		sb.append("CREATE TABLE " + tblname + " (");
		for (XmlElement e : root.getChildrenByName("entry"))
		{
			String colname = e.getStringAttribute("name");
			String coltype = e.getStringAttribute("type");
			String colsize = e.getStringAttribute("size", "");
			String coldefault = e.getStringAttribute("defaultvalue", "");
			String coldesc = e.getStringAttribute("desc", "");
			coltype = getDBDataType(coltype);
			if (!colsize.isEmpty())
				colsize = "("+colsize+")";
			if (!coldefault.isEmpty())
				coldefault = "default "+ coldefault;
			if (!coldesc.isEmpty())
				coldesc = "COMMENT '" + coldesc + "'";
			sb.append("\n\t").append(colname).append(" ").append(coltype).append(colsize).append(" ").append(coldefault).append(" ").append(coldesc).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		if (index != null && index.length > 0)
		{
			sb.append(", \n KEY `" + tblname + "_key`(");
			StringBuilder indexsb = new StringBuilder();
			for (String keycolumn : index)
			{
				if (indexsb.length() > 0) 
					indexsb.append(",`");
				else 
					indexsb.append("`");
				indexsb.append(keycolumn.trim());
				indexsb.append("`");
			}
			sb.append(indexsb);
			sb.append(")");
		}
		String tbldesc = root.getStringAttribute("desc", "");
		if (!tbldesc.isEmpty())
			tbldesc = "COMMENT '" + tbldesc + "'";
		sb.append("\n)ENGINE=InnoDB DEFAULT CHARSET=utf8 " + tbldesc);
		sb.append("\n").append(partitionStatement).append(");\n\n");
		return sb.toString();
	}
	
	private static String getDBDataType(String typeName) throws Exception
	{
		switch (typeName)
		{
		case "string":
			return "varchar";
		case "int":
		case "uint":
			return "int";
		case "datetime":
			return "datetime";
		case "float":
			return "float";
		default:
			throw new Exception("invalid database data type (" + typeName + ") !");
		}
	}
	
	private static String getDBPartitionStatement()
	{
		 StringBuilder sb = new StringBuilder();
		 Date d = new Date();
		 SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		 SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		 sb.append("PARTITION BY LIST(to_days(dtEventTime))(\n");
		 for (int i = 0; i <= 30; ++i)
		 {
			 sb.append("PARTITION p_").append(dateFormat2.format(new Date(d.getTime() + i*24*60*60*1000l)));
			 sb.append(" VALUES IN (to_days('").append(dateFormat1.format(new Date(d.getTime() + i*24*60*60*1000l))).append("'))");
			 if (i == 30)
				 sb.append("\n");
			 else
				 sb.append(",\n");
		 }
		 return sb.toString();
	}
}
