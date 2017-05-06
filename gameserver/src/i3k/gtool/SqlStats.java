package i3k.gtool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import i3k.DBRole;
import i3k.DBRoleShare;
import i3k.DBUser;
import i3k.SBean.DBBanData;
import i3k.gs.Ban;
import i3k.util.DBUtil;
import i3k.util.GameRandom;
import i3k.util.GameTime;
import ket.kdb.DB;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.moi.ExcelReader;
import ket.moi.ExcelSheet;
import ket.util.ArgsMap;

public class SqlStats
{

	public static void main(String[] args)
	{
		{
			org.apache.log4j.ConsoleAppender ca = new org.apache.log4j.ConsoleAppender();
			ca.setName("AA");
			ca.setWriter(new java.io.PrintWriter(System.out));
			ca.setLayout(new org.apache.log4j.PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %m%n"));
			Logger.getRootLogger().addAppender(ca);
		}
		float f = 1.05f;
		double d = 1.05;
		double dd = f;
		double rd1 =  f * 120;
		double rd2 = d * 120;
		double rd3 = dd * 120;
		System.err.println(f);
		System.err.println(d);
		System.err.println(dd);
		System.err.println(rd1);
		System.err.println(rd2);
		System.err.println(rd3);
		MatchTest mt = new MatchTest();
		mt.test();
		//calcRoleNameDayLogin(args[0], args[1]);
		//calcRoleLevelDistrubution(args[0]);
	}

	static class Team
	{
		static int nextRoleId = 0;
		static int nextId = 0;
		int id;
		Set<Integer> roles = new TreeSet<>();
		TeamQueue tq;
		Team()
		{
			this.id = ++nextId;
		}
		
		Team init(int count)
		{
			for (int i = 0; i < count; ++i)
				roles.add(GetNextRoleId());
			return this;
		}
		
		void onAddToTeamQueue(TeamQueue tq)
		{
			this.tq = tq;
		}
		
		void OnRemoveFromTeamQueue()
		{
			this.tq = null;
		}
		
		void setMatching()
		{
			if (tq != null)
				tq.setTeamMatching(id);
		}
		
		void resetFree()
		{
			if (tq != null)
				tq.resetFreeTeam(id);
		}
		
		static int GetNextRoleId()
		{
			return ++nextRoleId;
		}
		
		int getTeamMemeberCount()
		{
			return roles.size();
		}
		
		public String toString()
		{
			return roles.size() + " roles Team" + id;
		}
	}
	static class TeamQueue
	{
		int memberCount = 0;
		Map<Integer, Team> matchingTeams = new HashMap<Integer, Team>();
		Map<Integer, Team> freeTeams = new HashMap<Integer, Team>();
		TeamQueue(int memberCount)
		{
			this.memberCount = memberCount;
		}
		
		TeamQueue init(int initTeamCount)
		{
			for (int i = 0; i < initTeamCount; ++i)
				addTeam(new Team().init(memberCount));
			return this;
		}
		void addTeam(Team t)
		{
			if (t.getTeamMemeberCount() == memberCount)
			{
				freeTeams.put(t.id, t);
				t.onAddToTeamQueue(this);
			}
		}
		
		Team tryGetFreeTeam()
		{
			Iterator<Team> it =  freeTeams.values().iterator();
			if (it.hasNext())
			{
				Team t = it.next();
				return t;
			}
			return null;
		}
		
		void setTeamMatching(int id)
		{
			Team t = freeTeams.remove(id);
			if (t != null)
				matchingTeams.put(t.id, t);
		}
		
		void resetFreeTeam(int id)
		{
			Team t = matchingTeams.remove(id);
			if (t != null)
				freeTeams.put(t.id, t);
		}
		
		public String toString()
		{
			return memberCount + " roles TeamQueue " + freeTeams.size();
		}
	}
	
	static class TmpTeam
	{
		int memberCount = 0;
		List<Team> teams = new ArrayList<Team>();
		TmpTeam()
		{
			
		}
		
		int pushTeam(Team t)
		{
			teams.add(t);
			memberCount += t.getTeamMemeberCount();
			t.setMatching();
			return memberCount;
		}
		
		Team popTeam()
		{
			if (teams.isEmpty())
				return null;
			Team t = teams.remove(teams.size()-1);
			memberCount -= t.getTeamMemeberCount();
			t.resetFree();
			return t;
		}
		
		void popAllTeam()
		{
			for (Team t : teams)
			{
				t.resetFree();
			}
			memberCount = 0;
			teams.clear();
		}
		
		int getRoleCount()
		{
			return memberCount;
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for (Team t : teams)
			{
				if (sb.length() != 0)
					sb.append("|");
				sb.append(t.toString());
			}
			return sb.toString();
		}
	}
	static class MatchTest
	{
		int cap = 12;
		TeamQueue[] allTeams = new TeamQueue[cap-1]; 
		MatchTest()
		{
			for (int i = 0; i < allTeams.length; ++i)
			{
				allTeams[i] = new TeamQueue(i+1);
			}
			init();
		}
		
		void init()
		{
			for (int i = 0; i < allTeams.length; ++i)
			{
				allTeams[i].init(GameRandom.getRandom().nextInt(10));
			}
		}
		
		void init1()
		{
			allTeams[7-1].init(1);
			allTeams[4-1].init(1);
			allTeams[3-1].init(1);
			allTeams[2-1].init(1);
		}
		
		void init2()
		{
			allTeams[7-1].init(1);
			allTeams[4-1].init(1);
			allTeams[2-1].init(6);			
		}
		
		
		void printInfo()
		{
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			for (int i = 0; i < allTeams.length; ++i)
			{
				System.out.println(allTeams[i]);
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
		
		void test()
		{
			printInfo();
			while (true)
			{
				TmpTeam tt = new TmpTeam();
				if (!getRoles(tt, cap, cap))
					break;
				System.out.println("------+- " + tt);
			}
			printInfo();
		}

		
		
		Team tryGetFreeTeamRecursive(int level)
		{
			if (level <= 0)
				return null;
			if (level <= allTeams.length)
			{
				Team t = allTeams[level-1].tryGetFreeTeam();
				if (t != null)
					return t;
			}
			return tryGetFreeTeamRecursive(level - 1);
		}
		
		Team tryGetFreeTeam(int level)
		{
			for (int i = Math.min(allTeams.length, level); i > 0; --i)
			{
				Team t = allTeams[i-1].tryGetFreeTeam();
				if (t != null)
					return t;
			}
			return null;
		}


		boolean getRoles(TmpTeam tt, int x, int level)
		{
			System.out.println("(" + tt + ")" + " ==> left " + (x-tt.getRoleCount()) + ", try get   from level " + level);
			if (tt.getRoleCount() >= x)
				return true;
			int i = Math.min(x-tt.getRoleCount(), level);
			while (i > 0)
			{
				//System.out.println("(" + tt + ")" + " tryGetFreeTeam from " + i);
				Team t = tryGetFreeTeam(i);
				if (t != null)
				{
					//System.out.println("(" + tt + ")" + " push " + t.getTeamMemeberCount() + " roles team");
					tt.pushTeam(t);
					if (getRoles(tt, x, Math.min(t.getTeamMemeberCount(), i)))
						return true;
					tt.popTeam();
					i = Math.min(t.getTeamMemeberCount()-1, i-1);
					//System.out.println("(" + tt + ")" + " pop  " + t.getTeamMemeberCount() + " roles team");
				}
				else
				{
					break;
				}
			}
			//System.out.println("(" + tt + ")" + " <== left " + (x-tt.getRoleCount()) + ", not found from level " + level);
			return false;
		}
	}
	
	static void test()
	{
		try
		{
			byte[] b = {(byte)0xe9, (byte)0x97, (byte)0xa8, (byte)0xe5, (byte)0xb0, (byte)0x8f, (byte)0xe7, (byte)0x99, (byte)0xbd, (byte)0xe9, (byte)0xbe, (byte)0x99, (byte)0xf0, (byte)0x9f, (byte)0x98, (byte)0x88};
			String ss = new String(b, "utf-8");
			System.err.println(ss + " " + (ss.length() == ss.codePointCount(0, ss.length())));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	static class RoleDayLoginCount
	{
		String rname;
		List<Integer> loginCount = new ArrayList<>();
		RoleDayLoginCount(String rname)
		{
			this.rname = rname;
		}
	}
	private static void calcRoleNameDayLogin(String inputRoleNamesFile, String outputResultFile)
	{
		try
		{
			List<RoleDayLoginCount> roles = new ArrayList<>();
			ExcelReader reader = ket.moi.Factory.newExcelReader(inputRoleNamesFile + ".xlsx");
			ExcelSheet sheet = reader.getSheet(0);
			int row = 0;
			int col = 0;
			while (sheet.isNotEmpty(row, col))
			{
				String rname = sheet.getStringValue(row++, col);
				roles.add(new RoleDayLoginCount(rname));
			}
			
			
			String databaseDriver = "com.mysql.jdbc.Driver";
			String databaseHost = "localhost";
			String databasePort = "3306";
			String databaseUser = "tomcat";
			String databasePassword = "";
			String databaseName = "tlog";
			DBUtil dbUtil = new DBUtil(Logger.getRootLogger());
			dbUtil.init(databaseDriver, databaseHost, databasePort, databaseUser, databasePassword, databaseName);
			Connection connection = dbUtil.connect();
			for (RoleDayLoginCount r : roles)
			{
				String dateStart = "2016-09-06";
				for (int i = 0; i < 7; ++i)
				{
					String sql = "select count(RoleLogin.dtEventTime) from RoleCreate, RoleLogin where RoleCreate.vRoleName='" + r.rname + "' and RoleCreate.iRoleId=RoleLogin.iRoleId and date(RoleLogin.dtEventTime)=Date_Add('" + dateStart + "', INTERVAL " + i + " DAY)";
					Logger.getRootLogger().info(sql);
					Statement stm = connection.createStatement();
					ResultSet rs = stm.executeQuery(sql);
					int LoginCount = 0;
					if (rs.next())
						LoginCount = rs.getInt(1);
					rs.close();
					stm.close();
					r.loginCount.add(LoginCount);
				}
			}
			dbUtil.closeConnection(connection);
			
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputResultFile), "UTF-8"));
			for (RoleDayLoginCount r : roles)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(r.rname).append("\t");
				for (int c : r.loginCount)
				{
					sb.append(c).append("\t");
				}
				sb.append("\n");
				writer.write(sb.toString());
			}
			writer.flush();
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	static class RoleLevelStats
	{
		String date;
		Map<Integer, Integer> levelCount = new TreeMap<>();
		RoleLevelStats(String date)
		{
			this.date = date;
		}
	}
	private static void calcRoleLevelDistrubution(String outputResultFile)
	{
		try
		{
			List<RoleLevelStats> stats = new ArrayList<>();
			
			
			String databaseDriver = "com.mysql.jdbc.Driver";
			String databaseHost = "localhost";
			String databasePort = "3306";
			String databaseUser = "tomcat";
			String databasePassword = "";
			String databaseName = "tlog";
			DBUtil dbUtil = new DBUtil(Logger.getRootLogger());
			dbUtil.init(databaseDriver, databaseHost, databasePort, databaseUser, databasePassword, databaseName);
			Connection connection = dbUtil.connect();
			String dateStart = "2016-09-06";
			for (int i = 0; i < 7; ++i)
			{
				String sql = "select l, count(*) as c from (select RoleEventFlow.dtEventTime,RoleCreate.vChannel,RoleCreate.vUId,RoleCreate.iRoleId,max(RoleEventFlow.iLevel) as l from RoleCreate, RoleEventFlow where RoleCreate.iRoleId=RoleEventFlow.iRoleId and date(RoleCreate.dtEventTime)=date(RoleEventFlow.dtEventTime) and RoleEventFlow.iEventId=50 and date(RoleCreate.dtEventTime)=Date_Add('" + dateStart + "', INTERVAL " + i + " DAY) group by iRoleId) as t group by l";
				Logger.getRootLogger().info(sql);
				Statement stm = connection.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				RoleLevelStats s = new RoleLevelStats(dateStart + " + 1");
				while (rs.next())
				{
					int level = rs.getInt(1);
					int count = rs.getInt(2);
					s.levelCount.put(level, count);
				}
				rs.close();
				stm.close();
				stats.add(s);
			}
			dbUtil.closeConnection(connection);
			
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputResultFile), "UTF-8"));
			for (RoleLevelStats s : stats)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(s.date).append("\n");
				for (Map.Entry<Integer, Integer> e : s.levelCount.entrySet())
				{
					sb.append(e.getKey()).append("\t").append(e.getValue()).append("\n");
				}
				sb.append("\n");
				writer.write(sb.toString());
			}
			writer.flush();
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
