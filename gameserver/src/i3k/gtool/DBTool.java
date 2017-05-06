package i3k.gtool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import i3k.DBRole;
import i3k.DBRoleShare;
import i3k.DBUser;
import i3k.SBean.DBBanData;
import i3k.gs.Ban;
import i3k.gs.GameData;
import i3k.util.GameTime;
import ket.kdb.DB;
import ket.kdb.TableReadonly;
import ket.kdb.Transaction;
import ket.util.ArgsMap;

public class DBTool
{
	private static String dbCfgDir = "";
	private static String outputDir = "";
	private static String gameAppId;
	private static String serviceId;
	private static int skipaccount = 0;
	private static DB db;
	private static boolean loadDBflag = false;
	private static int userSize = 0;
	private static int roleSize = 0;
	

	public static void main(String[] args)
	{
		ArgsMap am = new ArgsMap(args);
		dbCfgDir = am.get("-dbCfgDir", "");
		if (dbCfgDir.isEmpty())
		{
			System.err.println("dbDir is empty!");
			return;
		}
		outputDir = am.get("-outputDir", "");
		if (outputDir.isEmpty())
		{
			System.err.println("outputDir is empty!");
			return;
		}
		gameAppId = am.get("-gameAppId", "520050");
		serviceId = am.get("-serviceId", "1");
		initDB();
		while (!loadDBflag);
		printResult();
	}

	private static void printResult()
	{
		System.out.println("DB account size is: " + userSize);
		System.out.println("DB role size is: " + roleSize);
		System.out.println("DB account no role size is: " + skipaccount);
	}

	private static void initDB()
	{
		System.out.println("Start init DB, dbCfgDir: " + dbCfgDir);
		db = ket.kdb.Factory.newDB();
		Path p = Paths.get(dbCfgDir);
		db.open(p.getParent(), p);
		db.execute(new DBToolInitTrans());
		db.close();
		System.out.println("DB init success");
	}

	public static class DBToolInitTrans implements Transaction
	{
		public DBToolInitTrans()
		{
		}

		@Override
		public boolean doTransaction()
		{
			System.out.println("Start generate snapshot file...");
//			this.role.forEach(entry -> DBTool.roles.add(entry.getValue()));
//			this.user.forEach(entry -> DBTool.users.put(entry.getKey(), entry.getValue()));
//			this.roleshare.forEach(entry -> DBTool.roleShares.put(entry.getKey(), entry.getValue()));
			BISnapshotGenerater generater = new BISnapshotGenerater();
			String path = generater.generatePacket();
			try
			{
				generater.generateAccount(path, this.role, this.user, this.roleshare);
				generater.generateRole(path, this.role, this.user, this.roleshare);
			}
			catch (IOException e)
			{
				System.err.println("generate snapshot file error!");
				e.printStackTrace();
			}
			System.out.println("End generate snapshot file...");
			return true;
		}

		@Override
		public void onCallback(ErrorCode errcode)
		{
			if (errcode == ErrorCode.eOK)
			{
				DBTool.loadDBflag = true;
			}
			else
			{
				System.err.println("DB init failed");
				System.exit(0);
			}
		}

		@AutoInit
		public TableReadonly<Integer, DBRole> role;
		@AutoInit
		public TableReadonly<String, DBUser> user;
		@AutoInit
		public TableReadonly<String, DBRoleShare> roleshare;
	}

	private static void generateFile()
	{
		System.out.println("Start generate snapshot file...");
		BISnapshotGenerater generater = new BISnapshotGenerater();
		String path = generater.generatePacket();
//		try
//		{
//			generater.generateAccount(path);
//			generater.generateRole(path);
//		}
//		catch (IOException e)
//		{
//			System.err.println("generate snapshot file error!");
//			e.printStackTrace();
//		}
		System.out.println("End generate snapshot file...");
	}

	public static class BISnapshotGenerater
	{
		public BISnapshotGenerater()
		{
		}

		public String generatePacket()
		{
			File path = new File(outputDir + "/" + GameTime.getDateStampStr(new Date()) + "/" + gameAppId);
			if (!path.exists())
			{
				path.mkdirs();
			}
			return path.getAbsolutePath();
		}

		private void generateRole(String path, TableReadonly<Integer, DBRole> roles, TableReadonly<String, DBUser> user, TableReadonly<String, DBRoleShare> roleshare) throws IOException
		{
			String filename = generateFilename("role");
			System.out.println("Start generate BIRole...");
			File rolefile = new File(path + "/" + filename);
			if (!rolefile.exists())
			{
				rolefile.createNewFile();
			}
			BufferedWriter rolewriter = new BufferedWriter(new FileWriter(rolefile));
			rolewriter.append("roleid,accountid,name,status,create_time,delete_time,lastlogin_time,bind_cash,money,level,vip_level,exp,time_used,reserve1,reserve2,reserve3,reserve4,reserve5,reserve6\r\n");
			roles.forEach(entry->{
				DBRole role = entry.getValue();
				String userName = role.getUsername();
				if (userName != null)
				{
					DBBanData baninfo = user.get(userName) == null ? null : user.get(userName).ban.get(Ban.BAN_TYPE_LOGIN);
					try
					{
						rolewriter.append(role.id + spliter + GameData.getChannelOpenId(role.register.id) + spliter + "\"" + role.name + "\"" + spliter + (baninfo != null && Ban.isInBan(baninfo.banEndTime, GameTime.getTime()) ? 0 : 1) + spliter + GameTime.getGMTTimeFromServerTime(role.createTime) + spliter + spliter + role.lastLoginTime + spliter + role.diamondR + spliter + (role.coinF + role.coinR) + spliter + role.level + spliter + roleshare.get(role.getUsername()).vipPay.vipLvl + spliter + role.exp + spliter + role.totalOnlineTime + spliter + spliter + spliter + spliter + spliter + spliter + "\r\n");
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
					System.out.println(role.name + " role user name is null");
			});
			rolewriter.close();
			System.out.println("End generate BIRole...");
		}

		private void generateAccount(String path, TableReadonly<Integer, DBRole> roles, TableReadonly<String, DBUser> user, TableReadonly<String, DBRoleShare> roleshare) throws IOException
		{
			String filename = generateFilename("account");
			System.out.println("Start generate BIAccount...");
			File accountfile = new File(path + "/" + filename);
			if (!accountfile.exists())
			{
				accountfile.createNewFile();
			}
			BufferedWriter accountwriter = new BufferedWriter(new FileWriter(accountfile));
			accountwriter.append("accountid,rolelist_size,cents_add,cash_add,cash_total,create_time,login_time,level,vip_level\r\n");
			user.forEach(entry -> 
			{
				try
				{
					String k = entry.getKey();
					DBUser v = entry.getValue();
					DBRoleShare share = roleshare.get(k);
					int totalDiamond = 0;
					for (int roleId : v.rolesID)
					{
						DBRole role = roles.get(roleId);
						if (role != null)
							totalDiamond += role.diamondF;
					}
					if(share != null)
					{
						accountwriter.append(GameData.getChannelOpenId(v.register.id) + spliter + v.rolesID.size() + spliter + share.vipPay.payPoints + spliter + share.vipPay.payDiamond + spliter + totalDiamond + spliter + GameTime.getGMTTimeFromServerTime(v.register.createTime) + spliter + GameTime.getGMTTimeFromServerTime(v.lastLoginTime) + spliter + spliter + share.vipPay.vipLvl + "\r\n");
					}
					else
					{
						skipaccount ++;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
			accountwriter.close();
			System.out.println("End generate BIAccount...");
		}

		private String generateFilename(String tabname)
		{
			return "snapshot_" + tabname + "_" + GameTime.getDateStampStr(new Date(new Date().getTime() - 24 * 3600 * 1000l)) + "_" + gameAppId + "_" + serviceId + ".csv";
		}

		private String spliter = ",";
	}
}
