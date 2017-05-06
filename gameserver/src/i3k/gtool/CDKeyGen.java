package i3k.gtool;



import java.util.Collections;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Properties;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;


import java.util.Random;
import java.security.MessageDigest;

import i3k.gtool.KeyGen.*;
	
public class CDKeyGen
{
	
	private static final int CODE_SEGMENT_COUNT = 4;
	private static final int CODE_SEGMENT_LENGTH = 4;
	
	//private static final int BATCH_CODE_LENGTH = 2;
	private static final String INIT_BATCH_CODE = "I3K";
	//private static final int BATCH_ID_OFFSET = getInitCodeOffset(INIT_BATCH_CODE);
	
	private static final String INIT_GEN_CODE = "Q";
	//private static final int GEN_ID_OFFSET = getInitCodeOffset(INIT_GEN_CODE);
	
	private static final String BATCH_ID_PROPERTY_NAME = "batchId";
	private static final String GEN_ID_PROPERTY_NAME = "genId";
	private static final String KEY_NAME_PREFIX_IN_PROPERTIES = "key_";
	private static final String SEPARATOR_CHAR_IN_PROPERTIES = "_";
	
	private static final String PROPERTIES_FILE_NAME_PREFIX = "cdkey_";
    private static final String PROPERTIES_FILE_SEPARATOR_CHAR = "_";
    private static final String PROPERTIES_FILE_EXTENSION_NAME = ".properties";
    private static final String CDKEY_FILENAME_PREFIX = "key-";
    private static final String CDKEY_FILENAME_SEPARATOR_CHAR = "_";
    private static final String CDKEY_FILE_EXTENSION_NAME = ".txt";
    private static final String CDKEY_SQL_FILENAME_PREFIX = "giftkey-";
    private static final String CDKEY_SQL_FILENAME_SEPARATOR_CHAR = "_";
    private static final String CDKEY_SQL_FILE_EXTENSION_NAME = ".sql";
	
    EncryptKeyCode encryptKeyCode = new EncryptKeyCode();
	public CDKeyGen()
	{
		
	}
	
	
	private static String genBatchCode(int batchId)
	{
		return KeyGen.genIDCode(batchId, INIT_BATCH_CODE);
	}
	
	private static int calcBatchID(String batchCode) 
	{
		return KeyGen.calcCodeID(batchCode, INIT_BATCH_CODE);
	}
	
	private static String genGenCode(int genID)
	{
		return KeyGen.genIDCode(genID, INIT_GEN_CODE);
	}
	
	private static int calcGenID(String genCode)
	{
		return KeyGen.calcCodeID(genCode, INIT_GEN_CODE);
	}
	
//		private static String genBatchCode(int batchId) 
//		{
//	    	int offsetBatchId = BATCH_ID_OFFSET + batchId;
//	    	int batchCodeCount = 1;
//	    	for (int i = 0; i < INIT_BATCH_CODE.length(); ++i)
//	    		batchCodeCount *= CodeChars.getCharsCount();
//	    	offsetBatchId %= batchCodeCount;
//	    	char[] code = new char[INIT_BATCH_CODE.length()];
//	    	for (int i = 0; i < INIT_BATCH_CODE.length(); ++i) 
//	    	{
//	    		int radix = 1;
//	    		for (int j = 0; j < INIT_BATCH_CODE.length()-1-i; ++j)
//	    			radix *= CodeChars.getCharsCount();
//	    		int index = offsetBatchId/radix%CodeChars.getCharsCount();
//	    		code[i] = CodeChars.getChar(index);
//	    	}
//	    	return new String(code);
//	    }
	
//		private static int calcBatchID(String batchCode) 
//		{
//	    	int batchId = -1;
//	    	int batchIdOffset = 0;
//    		for (int i = 0; i < batchCode.length(); ++i) 
//    		{
//    			batchIdOffset = Character.getNumericValue(batchCode.charAt(i)) + batchIdOffset*CodeChars.getCharsCount();
//        	}
//    		int batchCodeMax = 1;
//        	for (int i = 0; i < batchCode.length(); ++i)
//        		batchCodeMax *= CodeChars.getCharsCount();
//        	batchId = (batchCodeMax + batchIdOffset - BATCH_ID_OFFSET)%batchCodeMax;
//	    	return batchId;
//	    }
	
//		private static char genGenCode(int genID)
//		{
//			return CodeChars.getChar(genID);
//		}
//		
//		private static int calcGenID(char genCode)
//		{
//			return Character.getNumericValue(genCode);
//		}
	
	static int getBatchID(String code)
	{
		return calcBatchID(code.substring(0, INIT_BATCH_CODE.length()));
	}
	
	static int getGenID(String code)
	{
		return calcGenID(code.substring(INIT_BATCH_CODE.length(), INIT_BATCH_CODE.length()+INIT_GEN_CODE.length()));
	}
	
	static String genBatchGenCode(int bid, int gid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(genBatchCode(bid));
		sb.append(genGenCode(gid));
		return sb.toString();
	}
	
	
	static CDKeyIDInfo getCodeIDInfo(EncryptKeyCode ekc, String code)
	{
		code = code.toUpperCase();
		CDKeyIDInfo info = new CDKeyIDInfo();
		if (KeyGen.checkCodeFormatValid(code))
		{
			String batchCode = KeyGen.getCodeBatch(ekc, code);
			if (batchCode != null)
			{
				info.batchID = getBatchID(batchCode);
				info.genID = getGenID(batchCode);
			}	
		}
		return info;
	}
	
	public CDKeyIDInfo getCodeIDInfo(String code)
	{
		return getCodeIDInfo(encryptKeyCode, code);
	}
	
	public boolean testCodeValid(String code)
	{
		return getCodeIDInfo(encryptKeyCode, code) != null;
	}
	
	public static boolean isCodeValid(String code)
	{
		return KeyGen.getCodeBatch(new EncryptKeyCode(), code) != null;
	}
	
	
	
	public static List<String> genCodes(int batchId, int genId, int count)
	{
		String batchCode = genBatchGenCode(batchId, genId);
		return KeyGen.genCodes(batchCode, count);
	}
	
	static List<String> genCodes(int genId, int count)
	{
		return KeyGen.genCodes(KeyGen.genIDCode(genId, "rxjh"), count);
	}
    
    
    private static String getPropertiesFileName(int batchId, int genID)
    {
    	return PROPERTIES_FILE_NAME_PREFIX + batchId + PROPERTIES_FILE_SEPARATOR_CHAR + genID + PROPERTIES_FILE_EXTENSION_NAME;
    }
    
    private static String getResultFileName(int batchId, int genId) 
    {
    	return CDKEY_FILENAME_PREFIX  + batchId + CDKEY_FILENAME_SEPARATOR_CHAR + genId + CDKEY_FILE_EXTENSION_NAME;
    }
    
    private static String getSqlFileName(int batchId, int genId) 
    {
    	return CDKEY_SQL_FILENAME_PREFIX + batchId + CDKEY_SQL_FILENAME_SEPARATOR_CHAR + genId + CDKEY_SQL_FILE_EXTENSION_NAME;
    }
	
	private static void saveGenCDKeyProperties(int batchID, int genID, List<String> cdkeys, String dir) 
	{
    	try 
    	{
        	Properties properties = new Properties();
        	properties.setProperty(BATCH_ID_PROPERTY_NAME, String.valueOf(batchID));
        	properties.setProperty(GEN_ID_PROPERTY_NAME, String.valueOf(genID));
        	System.out.println("......save batch gen info properties: batchId=" + batchID + ", genId=" + genID);
        	int seqNo = 1;
        	for (String cdkey : cdkeys)
        	{
        		String propertyName = KEY_NAME_PREFIX_IN_PROPERTIES + seqNo++;
        		properties.setProperty(propertyName, cdkey);
        	}
        	System.out.println("......save batch gen cdkey properties: batchId=" + batchID + ", genId=" + genID + ", keyCount=" + cdkeys.size());
        	String propertiesFileName = dir + File.separator + getPropertiesFileName(batchID, genID);
            FileOutputStream out = new FileOutputStream(propertiesFileName);
            properties.store(out, "gen cdkeys batchID="+batchID+", genID="+genID+", at " + new Date());
            out.close();
            System.out.println("save properties file : " + propertiesFileName);
        } 
    	catch (Exception e) 
    	{
            e.printStackTrace();
        } 
    	finally 
    	{
        }
    }
	
	private static void saveGenCDkeyResult(int batchID, int genID, List<String> cdkeys, String dir) 
	{
    	try 
    	{
    		String cdKeyResultFileName = dir + File.separator +  getResultFileName(batchID, genID);
        	Writer resultwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cdKeyResultFileName), "UTF-8"));
        	
        	for (String cdkey : cdkeys)
        	{
                resultwriter.write(cdkey+"\r\n");
        	}
        	
            resultwriter.flush();
            resultwriter.close();
            System.out.println("save result file : " + cdKeyResultFileName);
        } 
    	catch (Exception e) 
    	{
            e.printStackTrace();
        } 
    	finally 
    	{
        }
    }
	
	private static String createCDKeyInsertSql(int batchid, int batchGenid, String cdkey) 
    {
        //return String.format("INSERT INTO keyinfo(bid, gid, kvalue) VALUES(%d, %d, '%s');\n", batchid, batchGenid, cdkey);
		return String.format("INSERT INTO keyinfo(kvalue) VALUES('%s');\n",  cdkey);
    }
	
	 private static void saveGenCDkeySqlResult(int batchId, int genId, List<String> cdkeys, String dir) 
	 {
	    	try 
	    	{
	    		String genCDKeySqlFileName = dir + File.separator +  getSqlFileName(batchId, genId);
	    		
	        	Writer sqlwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(genCDKeySqlFileName), "UTF-8"));
	        	sqlwriter.write("use giftkeys;\n");
	        	sqlwriter.write("SET collation_connection = utf8_bin;\n");
	        	sqlwriter.write("SET character_set_client = utf8;\n");
	        	sqlwriter.write("SET character_set_connection = utf8;\n");
	        	sqlwriter.write("SET autocommit=0;\n");
	        	
	        	for (String cdkey : cdkeys) 
	        	{
	        		String cdkeySql = createCDKeyInsertSql(batchId, genId, cdkey);
	                sqlwriter.write(cdkeySql);
	        	}
	        	
	            sqlwriter.write("SET autocommit=1;\n");
	            sqlwriter.flush();
	            sqlwriter.close();
	            System.out.println("save sql file : " + genCDKeySqlFileName);
	            
	        } 
	    	catch (Exception e) 
	        {
	            e.printStackTrace();
	        } 
	    	finally 
	    	{
	        }
	    }

    
    private static class BatchGenCDKeyFileFilter implements FilenameFilter 
    {
    	int batchID = -1;
    	public BatchGenCDKeyFileFilter() 
    	{
    		
    	}
    	
    	public BatchGenCDKeyFileFilter(int batchID) 
    	{
    		this.batchID = batchID;
    	}
    	
    	public boolean accept(File dir, String name) 
    	{
    		if (name.startsWith(PROPERTIES_FILE_NAME_PREFIX) && name.endsWith(PROPERTIES_FILE_EXTENSION_NAME)) 
    		{
    			String batchGenIdStr = name.substring(PROPERTIES_FILE_NAME_PREFIX.length(), name.length() - PROPERTIES_FILE_EXTENSION_NAME.length());
    			String[] childStrs = batchGenIdStr.split(PROPERTIES_FILE_SEPARATOR_CHAR);
    			if (childStrs.length != 2)
    				return false;
    			for (String str : childStrs)
    			{
    				for (int i = 0; i < str.length(); ++i)
	    			{
	    				if (!Character.isDigit(str.charAt(i)))
	    					return false;	
	    			}
    			}
    			if (this.batchID >= 0)
    			{
    				int bid = Integer.parseInt(childStrs[0]);
    				if (bid != this.batchID)
    					return false;
    			}
    			return true;
    		}
    		return false;
    	}
    }
    
    private static String[] getAllBatchCDKeyFileNames(String dir) 
    {
    	File curfile = new File(dir);
    	String[] files = curfile.list(new BatchGenCDKeyFileFilter());
    	return files;
    }
    
    private static String[] getBatchCDKeyFileNames(String dir, int batchID) 
    {
    	File curfile = new File(dir);
    	String[] files = curfile.list(new BatchGenCDKeyFileFilter(batchID));
    	return files;
    }
    
    private static int getNextBatchId(String dir) 
    {
    	int maxBatchId = 0;
    	String[] allBatchCDKeyFileNames = getAllBatchCDKeyFileNames(dir);
    	for (String name : allBatchCDKeyFileNames) 
    	{
    		String batchGenIdStr = name.substring(PROPERTIES_FILE_NAME_PREFIX.length(), name.length() - PROPERTIES_FILE_EXTENSION_NAME.length());
    		String[] childStrs = batchGenIdStr.split(PROPERTIES_FILE_SEPARATOR_CHAR);
			int batchid = Integer.parseInt(childStrs[0]);
			if (batchid > maxBatchId)
				maxBatchId = batchid;
    	}
    	return maxBatchId + 1;
    }
    
    private static int getNextGenId(String dir, int batchID) 
    {
    	int maxGenId = 0;
    	String[] allBatchCDKeyFileNames = getBatchCDKeyFileNames(dir, batchID);
    	for (String name : allBatchCDKeyFileNames) 
    	{
    		String batchGenIdStr = name.substring(PROPERTIES_FILE_NAME_PREFIX.length(), name.length() - PROPERTIES_FILE_EXTENSION_NAME.length());
    		String[] childStrs = batchGenIdStr.split(PROPERTIES_FILE_SEPARATOR_CHAR);
			int genid = Integer.parseInt(childStrs[1]);
			if (genid > maxGenId)
				maxGenId = genid;
    	}
    	return maxGenId + 1;
    }
    
    private static void createNextGenCDKey(int batchID, int genID, int count, String dir) 
    {
    	System.out.println("gen cdkeys : batchId="+batchID+", genID="+genID + ",  outdir=" + dir);
		File file = new File(dir);
		if (!file.exists())
			file.mkdir();
    	int maxBatchID = (int)Math.pow(CodeChars.getCharsCount(), INIT_BATCH_CODE.length());
    	int maxGenID = (int)Math.pow(CodeChars.getCharsCount(), INIT_GEN_CODE.length());
    	if (batchID < 0 || batchID >= maxBatchID || genID < 0 || genID >= maxGenID)
    	{
    		System.err.println("gen cdkeys : batchId="+batchID+", genID="+genID + ", id is invalid !!!!!");
    		return;
    	}
    	List<String> cdkeys = genCodes(batchID, genID, count);
    	EncryptKeyCode ekc = new EncryptKeyCode();
    	for (String key : cdkeys)
    	{
    		System.out.println("create cdkey :" + key);
    		CDKeyIDInfo info = getCodeIDInfo(ekc, key);
    		if (info.batchID != batchID || info.genID != genID)
    		{
    			System.err.println("key " + key + " not valid!!! cacl batchID="+info.batchID +", genID="+info.genID);
    			return;
    		}
    	}
    	saveGenCDKeyProperties(batchID, genID, cdkeys, dir);
    	saveGenCDkeySqlResult(batchID, genID, cdkeys, dir);
    	saveGenCDkeyResult(batchID, genID, cdkeys, dir);
    }
    
    public static void genCDKeys(int batchID, int genID, int count, String dir)
    {
    	File file = new File(dir);
    	if (!file.exists())
    		file.mkdirs();
    	batchID = batchID >= 0 ? batchID : getNextBatchId(dir);
    	genID = genID >= 0 ? genID : getNextGenId(dir, batchID);
    	createNextGenCDKey(batchID, genID, count, dir);
    }
	//java -classpath lib/gs.jar i3k.gtool.CDKeyGen  --c=1000 --b=4 --outdir='ooo'
	public static void main(String[] args)
	{
		//test();
    	int genCount = 1000;
    	int batchId = -1;
    	int genId = -1;
    	String dir = ".";
    	for (String arg : args) 
    	{
    		if (arg.startsWith("--c=")) 
    		{
    			String str = arg.substring("--c=".length());
    			genCount = Integer.parseInt(str);
    		}
    		else if (arg.startsWith("--b=")) 
    		{
    			String str = arg.substring("--b=".length());
    			batchId = Integer.parseInt(str);
    		}
    		else if (arg.startsWith("--g=")) 
    		{
    			String str = arg.substring("--g=".length());
    			genId = Integer.parseInt(str);
    		}
    		else if (arg.startsWith("--outdir="))
    		{
    			dir = arg.substring("--outdir=".length());
    		}
    	}
    	genCDKeys(batchId, genId, genCount, dir);
		System.out.println("create cdkey end");
//			CDKeyGen cdkeyGen = new CDKeyGen();
//			CDKeyGen.CDKeyIDInfo keyInfo = cdkeyGen.getCodeIDInfo("5F9TAKYFI8Z6R019");
//			System.out.println(keyInfo.batchID + ", " + keyInfo.genID);
	}
	
	private static void test()
	{
		System.out.println(Character.getNumericValue('a'));
		System.out.println(Character.getNumericValue('A'));
		List<String> codes = KeyGen.genCodes("i3kQ", 10);
		for (String code : codes)
		{
			System.out.println(code + ", " + isCodeValid(code));
		}
		try
		{
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] m = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35};
			byte[] d = md.digest(m);
			for (int i = 0; i < d.length; ++i)
			{
				System.out.print(d[i] + ", ");
			}
			System.out.println();
			System.out.println(d.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			byte[] ks = {117, -37, 79, 107, -52, 5, -89, -127,}; 
			java.security.Key key = new javax.crypto.spec.SecretKeySpec(ks, "DES");
			byte[] m = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35};
			javax.crypto.Cipher encryptCipher = javax.crypto.Cipher.getInstance("DES");
			encryptCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
			byte[] d = encryptCipher.doFinal(m);
			for (int i = 0; i < d.length; ++i)
			{
				System.out.print(d[i] + ", ");
			}
			System.out.println();
			System.out.println(d.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}





