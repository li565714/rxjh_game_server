package i3k.gtool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import i3k.gtool.KeyGen.*;

public class ActiveKeyGen {

	// mind the letters cases
	private static final String CLASS_NAME = "activekey";
	private static final String INIT_BATCH_CODE = "MENG";
	
	private static final String ACTIVE_PROPERTIES_FILE_NAME_PREFIX = "activekey_";
    private static final String PROPERTIES_FILE_SEPARATOR_CHAR = "_";
    private static final String PROPERTIES_FILE_EXTENSION_NAME = ".properties";
    
	static String genBatchGenCode(int bid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(genBatchCode(bid));
		return sb.toString();
	}
	
	private static String genBatchCode(int batchId)
	{
		return KeyGen.genIDCode(batchId, INIT_BATCH_CODE);
	}
	
	private static int calcBatchID(String batchCode) 
	{
		return KeyGen.calcCodeID(batchCode, INIT_BATCH_CODE);
	}
	static int getBatchID(String code)
	{
		return calcBatchID(code.substring(0, INIT_BATCH_CODE.length()));
	}

	static int getCodeIDInfo(EncryptKeyCode ekc, String code)
	{
		code = code.toUpperCase();
		int result = -1;
		if (KeyGen.checkCodeFormatValid(code))
		{
			String batchCode = KeyGen.getCodeBatch(ekc, code);
			if(batchCode != null)
			{
				result = getBatchID(batchCode);
			}
		}
		return result;
	}
	
	
	public static List<String> genCodes(int batchID, int count)
	{
		String batchCode = genBatchGenCode(batchID);
		return KeyGen.genCodes(batchCode, count);
	}
	
	
	public static void createNextGenRegisterCDKey(int batchID,int count, String dir)
	{
		System.out.println("gen cdkeys : batchId="+batchID+", outdir=" + dir);
		File file = new File(dir);
		if (!file.exists())
			file.mkdir();
		int maxBatchID = (int)Math.pow(CodeChars.getCharsCount(), INIT_BATCH_CODE.length());
		if (batchID < 0 || batchID >= maxBatchID )
		{
			System.err.println("gen cdkeys : batchId="+batchID+", id is invalid !!!!!");
			return;
		}
		List<String> cdkeys = genCodes(batchID, count);
		EncryptKeyCode ekc = new EncryptKeyCode();
		Map<String, Integer> checkMap = new TreeMap<>();
		for (String key : cdkeys)
		{
			System.out.println("create cdkey :" + key);
			int info = getCodeIDInfo(ekc, key);
			checkMap.put(key, info);
			if (info != batchID )
			{
				System.err.println("key " + key + " not valid!!! cacl batchID="+info );
			    return;
	    	}
	    }
		if(checkMap.size() != count)
		{
			System.err.println("same key exists!");
			return;
		}else
		{
			System.out.println("all keys are different");
		}
    	KeyGen.saveGenCDKeyProperties(batchID, cdkeys, dir, CLASS_NAME);
    	KeyGen.saveGenCDkeySqlResult(batchID, cdkeys, dir, CLASS_NAME);
    	KeyGen.saveGenCDkeyResult(batchID, cdkeys, dir, CLASS_NAME);
	}
	
	//获取校验id
	public static int getCheckBatchId(String key)
	{
		return getCodeIDInfo(new EncryptKeyCode(), key);
	}
	
	public static void genRegisterCDKey(int batchID, int count, String dir)
	{
		File file = new File(dir);
    	if (!file.exists())
    		file.mkdirs();
    	batchID = batchID >= 0 ? batchID : KeyGen.getNextBatchId(dir, new KeyGen.BatchGenCDKeyFileFilter(ACTIVE_PROPERTIES_FILE_NAME_PREFIX, PROPERTIES_FILE_EXTENSION_NAME)
    	{
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(filePrefixName) && name.endsWith(fileExtensionName))
	    		{
	    			String batchGenIdStr = name.substring(filePrefixName.length(), name.length() - fileExtensionName.length());
	    			String[] childStrs = batchGenIdStr.split(PROPERTIES_FILE_SEPARATOR_CHAR);
	    			if (childStrs.length != 1)
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
    	});
    	createNextGenRegisterCDKey(batchID, count, dir);
	}
	
	public static void main(String[] args)
	{
		int genCount = 1000;
    	int batchId = -1;
    	String dir = "./activeKey";
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
    		else if (arg.startsWith("--outdir="))
    		{
    			dir = arg.substring("--outdir=".length());
    		}
    	}
		genRegisterCDKey(batchId, genCount, dir);
//		System.out.println("real batchID = " + batchId +",gen batchID = " + getCheckBatchId("CMKAPI2I5NTTUEYV"));
	}
	
}
